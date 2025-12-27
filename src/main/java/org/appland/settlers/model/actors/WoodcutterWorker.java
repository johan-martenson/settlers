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

import java.util.Objects;

import static java.lang.String.format;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Material.WOODCUTTER_WORKER;
import static org.appland.settlers.model.Tree.TREE_TYPES_THAT_CAN_BE_CUT_DOWN;

/*
 *
 * @author johan
 */
@Walker(speed = 10)
public class WoodcutterWorker extends Worker {
    private static final int TIME_TO_REST = 99;
    private static final int TIME_TO_CUT_TREE = 49;
    private static final int RANGE = 9;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown = new Countdown();
    private final ProductivityMeasurer productivityMeasurer = new ProductivityMeasurer(TIME_TO_REST + TIME_TO_CUT_TREE + Tree.TIME_TO_FALL, null);

    private State state = State.WALKING_TO_TARGET;

    private Point getTreeToCutDown() {
        return map.getPointsWithinRadius(home.getPosition(), RANGE).stream()
                .filter(map::isTreeAtPoint)
                .map(map::getTreeAtPoint)
                .filter(tree -> TREE_TYPES_THAT_CAN_BE_CUT_DOWN.contains(tree.getTreeType()))
                .filter(tree -> tree.getSize() == Tree.TreeSize.FULL_GROWN)
                .filter(tree -> map.getWorkers().stream()
                        .noneMatch(worker -> worker instanceof WoodcutterWorker woodcutterWorker
                                && woodcutterWorker.getPosition().equals(tree.getPosition())
                                && woodcutterWorker.isCuttingTree()))
                .map(Tree::getPosition)
                .filter(position -> map.findWayOffroad(position, home.getFlag().getPosition(), null) != null)
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
                if (home.isProductionEnabled()) {
                    if (countdown.hasReachedZero()) {
                        var point = getTreeToCutDown();

                        if (point == null) {
                            productivityMeasurer.reportUnproductivity();

                            return;
                        }

                        state = State.GOING_OUT_TO_CUT_TREE;
                        setOffroadTarget(point);
                    } else {
                        countdown.step();
                    }
                }
            }

            case WAITING_FOR_TREE_TO_FALL -> {
                if (!map.isTreeAtPoint(position)) {
                    carriedCargo = new Cargo(WOOD, map);
                    map.getStatisticsManager().treeCutDown(player, map.getTime());

                    state = State.GOING_BACK_TO_HOUSE_WITH_CARGO;
                    returnHomeOffroad();
                }
            }

            case CUTTING_TREE -> {
                if (countdown.hasReachedZero()) {

                    // Remove the tree if it's still in place
                    if (map.isTreeAtPoint(position) && !map.getTreeAtPoint(position).isFalling()) {
                        map.getTreeAtPoint(position).fallDown();

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
                if (home.getFlag().hasPlaceForMoreCargo()) {
                    System.out.println();
                    System.out.println("Woodcutter worker going out to place cargo on flag");
                    System.out.println("Home: " + home);
                    System.out.println("Home state: " + home.state);
                    System.out.println("State: " + state);
                    System.out.println("Position: " + position);
                    System.out.println("Path to flag: " + map.findWayWithExistingRoads(position, home.getFlag().getPosition()));
                    System.out.println("I belong to player: " + player.getName());
                    System.out.println("Home belongs to" + home.getPlayer().getName());
                    System.out.println("Flag belongs to: " + home.getFlag().getPlayer().getName());

                    state = State.GOING_OUT_TO_PUT_CARGO;
                    setTarget(home.getFlag().getPosition());

                    home.getFlag().promiseCargo(carriedCargo);

                    home.openDoor();
                } else {
                    state = State.WAITING_FOR_PLACE_ON_FLAG;
                }
            }

            case WAITING_FOR_PLACE_ON_FLAG -> {
                if (home.getFlag().hasPlaceForMoreCargo()) {
                    state = State.GOING_OUT_TO_PUT_CARGO;
                    setTarget(home.getFlag().getPosition());

                    home.getFlag().promiseCargo(carriedCargo);
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
        switch (state) {
            case GOING_OUT_TO_PUT_CARGO -> {
                carriedCargo.setPosition(position);
                carriedCargo.transportToReceivingBuilding(this::isReceiverForWood);
                home.getFlag().putCargo(carriedCargo);
                carriedCargo = null;

                state = State.GOING_BACK_TO_HOUSE;
                setTarget(home.getPosition());
            }

            case GOING_BACK_TO_HOUSE -> {
                state = State.RESTING_IN_HOUSE;
                enterBuilding(home);
                countdown.countFrom(TIME_TO_REST);
            }

            case GOING_OUT_TO_CUT_TREE -> {
                if (map.getWorkers().stream()
                        .noneMatch(worker -> worker instanceof WoodcutterWorker woodcutterWorker
                        && Objects.equals(woodcutterWorker.position, position)
                        && woodcutterWorker.isCuttingTree())) {
                    state = State.CUTTING_TREE;
                    map.reportWorkerStartedAction(this, WorkerAction.CUTTING);
                    countdown.countFrom(TIME_TO_CUT_TREE);
                } else {
                    state = State.GOING_BACK_TO_HOUSE;
                    setOffroadTarget(home.getPosition());

                    // TODO: handle productivity reporting
                }
            }

            case GOING_BACK_TO_HOUSE_WITH_CARGO -> {
                enterBuilding(home);
                state = State.IN_HOUSE_WITH_CARGO;
            }

            case RETURNING_TO_STORAGE -> {
                var storehouse = (Storehouse) map.getBuildingAtPoint(position);
                storehouse.depositWorker(this);
            }

            case GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE -> {

                // Go to the closest storage
                Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, WOODCUTTER_WORKER);

                if (storehouse != null) {
                    state = State.RETURNING_TO_STORAGE;
                    setTarget(storehouse.getPosition());
                } else {
                    state = State.GOING_TO_DIE;
                    setOffroadTarget(findPlaceToDie());
                }
            }

            case GOING_TO_DIE -> {
                setDead();
                state = State.DEAD;
                countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
            }
        }
    }

    @Override
    public String toString() {
        return isTraveling()
                ? format("Woodcutter worker at %s walking to , state: %s", position, getNextPoint(), state)
                : format("Woodcutter worker at %s, state: %s", position, state);
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, WOODCUTTER_WORKER);

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, player, WOODCUTTER_WORKER);

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;
                setOffroadTarget(storage.getPosition());
            } else {
                var point = findPlaceToDie();
                setOffroadTarget(point, position.downRight());
                state = State.GOING_TO_DIE;
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {
        switch (state) {
            case WALKING_TO_TARGET -> {
                var upLeft = position.upLeft();

                if (map.isFlagAtPoint(position)) {

                    // Return to storage if the planned path no longer exists
                    if (!map.arePointsConnectedByRoads(position, getTarget())) {

                        // Don't try to enter the woodcutter upon arrival
                        clearTargetBuilding();

                        // Go back to the storage
                        returnToStorage();
                    } else if (getTarget().equals(upLeft)) {
                        var house = map.getBuildingAtPoint(upLeft);

                        house.openDoor();
                    }
                }
            }
            case GOING_BACK_TO_HOUSE_WITH_CARGO -> {
                var upLeft = position.upLeft();

                if (map.isFlagAtPoint(position) && upLeft.equals(getTarget())) {
                    home.openDoor();
                }
            }
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
