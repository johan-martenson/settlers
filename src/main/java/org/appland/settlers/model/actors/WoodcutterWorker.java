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
import org.appland.settlers.model.WorkerAction;

import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Material.WOODCUTTER_WORKER;
import static org.appland.settlers.model.Tree.TREE_TYPES_THAT_CAN_BE_CUT_DOWN;

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
        return map.getPointsWithinRadius(getHome().getPosition(), RANGE).stream()
                .filter(map::isTreeAtPoint)
                .map(map::getTreeAtPoint)
                .filter(tree -> TREE_TYPES_THAT_CAN_BE_CUT_DOWN.contains(tree.getTreeType()))
                .filter(tree -> tree.getSize() == Tree.TreeSize.FULL_GROWN)
                .map(Tree::getPosition)
                .filter(position -> map.findWayOffroad(position, getHome().getFlag().getPosition(), null) != null)
                .findFirst()
                .orElse(null);
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
        GOING_TO_DIE,
        DEAD,
        WAITING_FOR_TREE_TO_FALL,
        RETURNING_TO_STORAGE
    }

    public WoodcutterWorker(Player player, GameMap map) {
        super(player, map);

        state     = State.WALKING_TO_TARGET;
        countdown = new Countdown();

        productivityMeasurer = new ProductivityMeasurer(TIME_TO_REST + TIME_TO_CUT_TREE + Tree.TIME_TO_FALL, null);
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
        switch (state) {
            case RESTING_IN_HOUSE -> {
                if (getHome().isProductionEnabled()) {
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
                }
            }
            case WAITING_FOR_TREE_TO_FALL -> {
                if (!map.isTreeAtPoint(getPosition())) {
                    setCargo(new Cargo(WOOD, map));

                    state = State.GOING_BACK_TO_HOUSE_WITH_CARGO;

                    returnHomeOffroad();
                }
            }
            case CUTTING_TREE -> {
                if (countdown.hasReachedZero()) {

                    /* Remove the tree if it's still in place */
                    if (map.isTreeAtPoint(getPosition()) && !map.getTreeAtPoint(getPosition()).isFalling()) {
                        map.getTreeAtPoint(getPosition()).fallDown();

                        productivityMeasurer.reportProductivity();

                        productivityMeasurer.nextProductivityCycle();

                        state = State.WAITING_FOR_TREE_TO_FALL;
                    } else {
                        state = State.GOING_BACK_TO_HOUSE;

                        returnHomeOffroad();
                    }
                } else {
                    countdown.step();
                }
            }
            case IN_HOUSE_WITH_CARGO -> {
                if (getHome().getFlag().hasPlaceForMoreCargo()) {
                    setTarget(getHome().getFlag().getPosition());

                    state = State.GOING_OUT_TO_PUT_CARGO;

                    getHome().getFlag().promiseCargo(getCargo());
                } else {
                    state = State.WAITING_FOR_PLACE_ON_FLAG;
                }
            }
            case WAITING_FOR_PLACE_ON_FLAG -> {
                if (getHome().getFlag().hasPlaceForMoreCargo()) {
                    setTarget(getHome().getFlag().getPosition());

                    state = State.GOING_OUT_TO_PUT_CARGO;

                    getHome().getFlag().promiseCargo(getCargo());
                }
            }
            case DEAD -> {
                if (countdown.hasReachedZero()) {
                    map.removeWorker(this);
                } else {
                    countdown.step();
                }
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
    public String toString() {
        return "Woodcutter worker at " + getPosition() + ((isTraveling()) ? " walking to " + getNextPoint() : "") + ", state: " + state;
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
