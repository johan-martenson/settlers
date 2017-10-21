/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

/* WALKING_TO_TARGET -> RESTING_IN_HOUSE -> GOING_OUT_TO_PLANT -> PLANTING -> GOING_BACK_TO_HOUSE -> RESTING_IN_HOUSE  */

@Walker(speed = 10)
public class Forester extends Worker {
    private static final int TIME_TO_PLANT = 19;
    private static final int TIME_TO_REST = 99;
    private static final int RANGE = 8;

    private final Countdown countdown;
    private State state;

    private boolean spotIsClearForTree(Point point) {
        if (map.isBuildingAtPoint(point)) {
            return false;
        }

        if (map.isFlagAtPoint(point)) {
            return false;
        }

        if (map.isRoadAtPoint(point)) {
            return false;
        }

        if (map.isTreeAtPoint(point)) {
            return false;
        }

        if (map.isStoneAtPoint(point)) {
            return false;
        }

        if (map.getTerrain().isOnMountain(point)) {
            return false;
        }

        if (map.getTerrain().isInWater(point)) {
            return false;
        }

        if (map.findWayOffroad(
                getHome().getFlag().getPosition(),
                point,
                null) == null) {
            return false;
        }

        return true;
    }
    private Point getTreeSpot() throws Exception {
        Iterable<Point> adjacentPoints = map.getPointsWithinRadius(getHome().getPosition(), RANGE);

        for (Point point : adjacentPoints) {

            /* Filter points where trees cannot be placed */
            if (!spotIsClearForTree(point)) {
                continue;
            }

            /* Return the first point that passed the filter */
            return point;
        }

        return null;
    }

    private enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_PLANT,
        PLANTING,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }

    public Forester(Player player, GameMap map) {
        super(player, map);

        state = State.WALKING_TO_TARGET;

        countdown = new Countdown();
    }

    public boolean isPlanting() {
        return state == State.PLANTING;
    }

    @Override
    protected void onEnterBuilding(Building building) {
        if (building instanceof ForesterHut) {
            setHome(building);
        }

        state = State.RESTING_IN_HOUSE;

        countdown.countFrom(TIME_TO_REST);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == State.RESTING_IN_HOUSE && getHome().isProductionEnabled()) {
            if (countdown.reachedZero()) {
                Point point = getTreeSpot();

                if (point == null) {
                    return;
                }

                setOffroadTarget(point);

                state = State.GOING_OUT_TO_PLANT;
            } else {
                countdown.step();
            }
        } else if (state == State.PLANTING) {
            if (countdown.reachedZero()) {

                /* Place a tree if the point is still open */
                if (spotIsClearForTree(getPosition())) {
                    map.placeTree(getPosition());
                }

                state = State.GOING_BACK_TO_HOUSE;

                returnHomeOffroad();
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == State.GOING_OUT_TO_PLANT) {
            state = State.PLANTING;

            countdown.countFrom(TIME_TO_PLANT);
        } else if (state == State.GOING_BACK_TO_HOUSE) {
            state = State.RESTING_IN_HOUSE;

            enterBuilding(getHome());

            countdown.countFrom(TIME_TO_REST);
        } else if (state == State.RETURNING_TO_STORAGE) {
            Storage storage = (Storage)map.getBuildingAtPoint(getPosition());

            storage.depositWorker(this);
        }

    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = GameUtils.getClosestStorage(getPosition(), getPlayer());

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroad(getPlayer(), getPosition());

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws Exception {

        /* Return to storage if the planned path no longer exists */
        if (state == State.WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter the forester hut upon arrival */
            clearTargetBuilding();

            /* Go back to the storage */
            returnToStorage();
        }
    }
}
