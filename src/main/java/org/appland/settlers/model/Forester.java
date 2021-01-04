/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

/* WALKING_TO_TARGET -> RESTING_IN_HOUSE -> GOING_OUT_TO_PLANT -> PLANTING -> GOING_BACK_TO_HOUSE -> RESTING_IN_HOUSE  */

import java.util.Collection;

import static org.appland.settlers.model.Material.FORESTER;

@Walker(speed = 10)
public class Forester extends Worker {
    private static final int TIME_TO_PLANT = 19;
    private static final int TIME_TO_REST = 99;
    private static final int RANGE = 8;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown;

    private State state;

    private boolean spotIsClearForTree(Point point) {

        MapPoint mapPoint = map.getMapPoint(point);

        if (mapPoint.isBuilding()) {
            return false;
        }

        if (mapPoint.isFlag()) {
            return false;
        }

        if (mapPoint.isRoad()) {
            return false;
        }

        if (mapPoint.isTree()) {
            return false;
        }

        if (mapPoint.isStone()) {
            return false;
        }

        /* Filter points where the surrounding terrain doesn't allow placing a tree */
        Collection<DetailedVegetation> surroundingVegetation = map.getSurroundingTiles(point);

        boolean noVegetationAllowingTrees = true;

        for (DetailedVegetation detailedVegetation : surroundingVegetation) {
            if (!DetailedVegetation.MINABLE_MOUNTAIN.contains(detailedVegetation) &&
                !DetailedVegetation.WATER_VEGETATION.contains(detailedVegetation)) {
                noVegetationAllowingTrees = false;

                break;
            }
        }

        if (noVegetationAllowingTrees) {
            return false;
        }

        /* Filter points that the forester cannot walk to */
        if (map.findWayOffroad(getHome().getFlag().getPosition(), point, null) == null) {
            return false;
        }

        return true;
    }

    private Point getTreeSpot() {
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
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE, GOING_TO_DIE, DEAD, RETURNING_TO_STORAGE
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
        state = State.RESTING_IN_HOUSE;

        countdown.countFrom(TIME_TO_REST);
    }

    @Override
    protected void onIdle() throws InvalidRouteException, InvalidUserActionException {
        if (state == State.RESTING_IN_HOUSE && getHome().isProductionEnabled()) {
            if (countdown.hasReachedZero()) {
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
            if (countdown.hasReachedZero()) {

                /* Place a tree if the point is still open */
                if (spotIsClearForTree(getPosition())) {
                    map.placeTree(getPosition());
                }

                state = State.GOING_BACK_TO_HOUSE;

                returnHomeOffroad();
            } else {
                countdown.step();
            }
        } else if (state == State.DEAD) {
            if (countdown.hasReachedZero()) {
                map.removeWorker(this);
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() throws InvalidRouteException {
        if (state == State.GOING_OUT_TO_PLANT) {
            state = State.PLANTING;

            countdown.countFrom(TIME_TO_PLANT);
        } else if (state == State.GOING_BACK_TO_HOUSE) {
            state = State.RESTING_IN_HOUSE;

            enterBuilding(getHome());

            countdown.countFrom(TIME_TO_REST);
        } else if (state == State.RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        } else if (state == State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, FORESTER);

            if (storehouse != null) {
                state = State.RETURNING_TO_STORAGE;

                setTarget(storehouse.getPosition());
            } else {
                state = State.GOING_TO_DIE;

                Point point = findPlaceToDie();

                setOffroadTarget(point);
            }
        } else if (state == State.GOING_TO_DIE) {
            setDead();

            state = State.DEAD;

            countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
        }
    }

    @Override
    protected void onReturnToStorage() throws InvalidRouteException {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, FORESTER);

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), FORESTER);

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            } else {
                Point point = findPlaceToDie();

                setOffroadTarget(point, getPosition().downRight());

                state = State.GOING_TO_DIE;
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws InvalidRouteException {

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

    @Override
    public void goToOtherStorage(Building building) throws InvalidRouteException {
        state = State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
