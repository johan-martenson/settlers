/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Size.LARGE;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class WoodcutterWorker extends Worker {
    private final static int TIME_TO_REST     = 99;
    private final static int TIME_TO_CUT_TREE = 49;
    private final static int RANGE            = 7;

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;

    private State state;

    private Point getTreeToCutDown() {
        Iterable<Point> adjacentPoints = map.getPointsWithinRadius(getHome().getPosition(), RANGE);

        for (Point point : adjacentPoints) {
            if (!map.isTreeAtPoint(point)) {
                continue;
            }

            Tree tree = map.getTreeAtPoint(point);
            if (tree.getSize() != LARGE) {
                continue;
            }

            if (map.findWayOffroad(point, getHome().getFlag().getPosition(), null) != null) {
                return point;
            }

        }

        return null;
    }

    private enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_CUT_TREE,
        CUTTING_TREE,
        GOING_BACK_TO_HOUSE_WITH_CARGO,
        IN_HOUSE_WITH_CARGO,
        GOING_OUT_TO_PUT_CARGO,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_PLACE_ON_FLAG, RETURNING_TO_STORAGE
    }

    public WoodcutterWorker(Player player, GameMap map) {
        super(player, map);

        state     = State.WALKING_TO_TARGET;
        countdown = new Countdown();

        productivityMeasurer = new ProductivityMeasurer(2 * TIME_TO_REST);
    }

    public boolean isCuttingTree() {
        return state == State.CUTTING_TREE;
    }

    @Override
    protected void onEnterBuilding(Building building) {
        if (building instanceof Woodcutter) {
            setHome(building);
        }

        state = State.RESTING_IN_HOUSE;

        countdown.countFrom(TIME_TO_REST);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == State.RESTING_IN_HOUSE && getHome().isProductionEnabled()) {
            if (countdown.reachedZero()) {
                Point point = getTreeToCutDown();

                if (point == null) {
                    productivityMeasurer.reportUnproductivity();

                    return;
                }

                setOffroadTarget(point);

                state = State.GOING_OUT_TO_CUT_TREE;
            } else {
                countdown.step();
            }
        } else if (state == State.CUTTING_TREE) {
            if (countdown.reachedZero()) {

                /* Remove the tree if it's still in place */
                if (map.isTreeAtPoint(getPosition())) {
                    map.removeTree(getPosition());

                    setCargo(new Cargo(WOOD, map));

                    state = State.GOING_BACK_TO_HOUSE_WITH_CARGO;

                    productivityMeasurer.reportProductivity();

                    productivityMeasurer.nextProductivityCycle();
                } else {
                    state = State.GOING_BACK_TO_HOUSE;
                }

                returnHomeOffroad();
            } else {
                countdown.step();
            }
        } else if (state == State.IN_HOUSE_WITH_CARGO) {
            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                setTarget(getHome().getFlag().getPosition());

                state = State.GOING_OUT_TO_PUT_CARGO;

                getHome().getFlag().promiseCargo();
            } else {
                state = State.WAITING_FOR_PLACE_ON_FLAG;
            }
        } else if (state == State.WAITING_FOR_PLACE_ON_FLAG) {
            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                setTarget(getHome().getFlag().getPosition());

                state = State.GOING_OUT_TO_PUT_CARGO;

                getHome().getFlag().promiseCargo();
            }
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
        } else if (state == State.GOING_OUT_TO_CUT_TREE) {
            state = State.CUTTING_TREE;

            countdown.countFrom(TIME_TO_CUT_TREE);
        } else if (state == State.GOING_BACK_TO_HOUSE_WITH_CARGO) {
            enterBuilding(getHome());

            state = State.IN_HOUSE_WITH_CARGO;
        } else if (state == State.RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        }
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = GameUtils.getClosestStorageConnectedByRoads(getPosition(), getPlayer());

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

            /* Don't try to enter the woodcutter upon arrival */
            clearTargetBuilding();

            /* Go back to the storage */
            returnToStorage();
        }
    }

    @Override
    int getProductivity() {

        return (int)
                ((double)(productivityMeasurer.getSumMeasured()) /
                        (double)(productivityMeasurer.getNumberOfCycles()) * 100);
    }
}
