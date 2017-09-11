/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.List;
import static org.appland.settlers.model.Material.STONE;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Stonemason extends Worker {

    private final static int TIME_TO_REST = 99;
    private final static int TIME_TO_GET_STONE = 49;
    private final Countdown countdown;
    private State state;
    private Point stoneTarget;

    private enum State {

        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_GET_STONE,
        GETTING_STONE,
        GOING_BACK_TO_HOUSE_WITH_CARGO,
        IN_HOUSE_WITH_CARGO,
        GOING_OUT_TO_PUT_CARGO,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }

    public Stonemason(Player player, GameMap map) {
        super(player, map);

        state = State.WALKING_TO_TARGET;

        countdown = new Countdown();
        stoneTarget = null;
    }

    public boolean isGettingStone() {
        return state == State.GETTING_STONE;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Quarry) {
            setHome(b);
        }

        state = State.RESTING_IN_HOUSE;

        countdown.countFrom(TIME_TO_REST);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == State.RESTING_IN_HOUSE && getHome().isProductionEnabled()) {
            if (countdown.reachedZero()) {
                Point accessPoint = null;
                double distance = Integer.MAX_VALUE;
                Point homePoint = getHome().getPosition();

                /* Look for stones within range */
                for (Point p : map.getPointsWithinRadius(homePoint, 8)) {

                    /* Filter points without stones */
                    if (!map.isStoneAtPoint(p)) {
                        continue;
                    }

                    /* Is the stone reachable? */
                    int distanceToAccessPoint = Integer.MAX_VALUE;
                    Point potentialAccessPoint = null;
                    for (Point p2 : p.getAdjacentPoints()) {

                        /* Filter the quarry since the stone mason needs to go outside  */
                        if (p2.equals(getHome().getPosition())) {
                            continue;
                        }

                        /* Filter points that can't be reached */
                        List<Point> path = map.findWayOffroad(getHome().getPosition(), p2, null);
                        if (path == null) {
                            continue;
                        }

                        /* Look for the closest access point */
                        if (path.size() < distanceToAccessPoint) {
                            distanceToAccessPoint = path.size();

                            potentialAccessPoint = p2;

                        }
                    }

                    /* Skip the stone if there is no way to reach it */
                    if (potentialAccessPoint == null) {
                        continue;
                    }

                    /* Check if this is the closest access point this far */
                    if (distanceToAccessPoint < distance) {
                        distance = distanceToAccessPoint;

                        accessPoint = potentialAccessPoint;

                        stoneTarget = p;
                    }
                }

                /* Report that there are no resources if no point is found */
                if (accessPoint == null) {
                    getHome().reportNoMoreNaturalResources();

                    return;
                }

                setOffroadTarget(accessPoint);

                state = State.GOING_OUT_TO_GET_STONE;
            } else {
                countdown.step();
            }
        } else if (state == State.GETTING_STONE) {
            if (countdown.reachedZero()) {
                map.removePartOfStone(stoneTarget);

                setCargo(new Cargo(STONE, map));

                state = State.GOING_BACK_TO_HOUSE_WITH_CARGO;

                stoneTarget = null;

                returnHomeOffroad();
            } else {
                countdown.step();
            }
        } else if (state == State.IN_HOUSE_WITH_CARGO) {
            setTarget(getHome().getFlag().getPosition());

            state = State.GOING_OUT_TO_PUT_CARGO;
        }
    }

    @Override
    public void onArrival() throws Exception {
        if (state == State.GOING_OUT_TO_PUT_CARGO) {
            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToStorage();
            getHome().getFlag().putCargo(cargo);

            setCargo(null);

            setTarget(getHome().getPosition());

            state = State.GOING_BACK_TO_HOUSE;
        } else if (state == State.GOING_BACK_TO_HOUSE) {
            state = State.RESTING_IN_HOUSE;

            enterBuilding(getHome());

            countdown.countFrom(TIME_TO_REST);
        } else if (state == State.GOING_OUT_TO_GET_STONE) {
            state = State.GETTING_STONE;

            countdown.countFrom(TIME_TO_GET_STONE);
        } else if (state == State.GOING_BACK_TO_HOUSE_WITH_CARGO) {
            enterBuilding(getHome());

            state = State.IN_HOUSE_WITH_CARGO;
        } else if (state == State.RETURNING_TO_STORAGE) {
            Storage storage = (Storage) map.getBuildingAtPoint(getPosition());

            storage.depositWorker(this);
        }

    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = GameUtils.getClosestStorage(getPosition(), map);

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

            /* Don't try to enter the quarry upon arrival */
            clearTargetBuilding();

            /* Go back to the storage */
            returnToStorage();
        }
    }
}
