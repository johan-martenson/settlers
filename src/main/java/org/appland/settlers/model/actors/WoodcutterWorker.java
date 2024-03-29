/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model.actors;

import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.TreeSize;
import org.appland.settlers.model.WorkerAction;

import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Material.WOODCUTTER_WORKER;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class WoodcutterWorker extends Worker {
    private static final int TIME_TO_REST     = 99;
    private static final int TIME_TO_CUT_TREE = 49;
    private static final int RANGE            = 9;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

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

            if (!Tree.TREE_TYPES_THAT_CAN_BE_CUT_DOWN.contains(tree.getTreeType())) {
                continue;
            }

            if (tree.getSize() != TreeSize.FULL_GROWN) {
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
        WAITING_FOR_PLACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE, DEAD, RETURNING_TO_STORAGE
    }

    public WoodcutterWorker(Player player, GameMap map) {
        super(player, map);

        state     = State.WALKING_TO_TARGET;
        countdown = new Countdown();

        productivityMeasurer = new ProductivityMeasurer(2 * TIME_TO_REST, null);
    }

    public boolean isCuttingTree() {
        return state == State.CUTTING_TREE;
    }

    @Override
    protected void onEnterBuilding(Building building) {
        state = State.RESTING_IN_HOUSE;

        countdown.countFrom(TIME_TO_REST);

        productivityMeasurer.setBuilding(building);
    }

    @Override
    protected void onIdle() {
        if (state == State.RESTING_IN_HOUSE && getHome().isProductionEnabled()) {
            if (countdown.hasReachedZero()) {
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
            if (countdown.hasReachedZero()) {

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

                getHome().getFlag().promiseCargo(getCargo());
            } else {
                state = State.WAITING_FOR_PLACE_ON_FLAG;
            }
        } else if (state == State.WAITING_FOR_PLACE_ON_FLAG) {
            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                setTarget(getHome().getFlag().getPosition());

                state = State.GOING_OUT_TO_PUT_CARGO;

                getHome().getFlag().promiseCargo(getCargo());
            }
        } else if (state == State.DEAD) {
            if (countdown.hasReachedZero()) {
                map.removeWorker(this);
            } else {
                countdown.step();
            }
        }
    }

    public boolean isReceiverForWood(Building building) {
        if (building.isReady() && building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(WOOD);
        }

        if (building instanceof Sawmill sawmill) {
            return sawmill.isReady() && sawmill.needsMaterial(WOOD);
        }

        return false;
    }

    @Override
    public void onArrival() {
        if (state == State.GOING_OUT_TO_PUT_CARGO) {
            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToReceivingBuilding(this::isReceiverForWood);
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

            map.reportWorkerStartedAction(this, WorkerAction.CUTTING);

            countdown.countFrom(TIME_TO_CUT_TREE);
        } else if (state == State.GOING_BACK_TO_HOUSE_WITH_CARGO) {
            enterBuilding(getHome());

            state = State.IN_HOUSE_WITH_CARGO;
        } else if (state == State.RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        } else if (state == State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, WOODCUTTER_WORKER);

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
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, WOODCUTTER_WORKER);

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), WOODCUTTER_WORKER);

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
    protected void onWalkingAndAtFixedPoint() {

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
    public int getProductivity() {

        return (int)
                ((double)(productivityMeasurer.getSumMeasured()) /
                        (productivityMeasurer.getNumberOfCycles()) * 100);
    }

    @Override
    public void goToOtherStorage(Building building) {
        state = State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
