package org.appland.settlers.model.actors;

import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Vegetation;
import org.appland.settlers.model.WorkerAction;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;

import java.util.Objects;
import java.util.Random;

import static org.appland.settlers.model.Material.FORESTER;

@Walker(speed = 10)
public class Forester extends Worker {
    private static final int TIME_TO_PLANT = 50;
    private static final int TIME_TO_REST = 99;
    private static final int RANGE = 8;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
    private static final Random RANDOM = new Random(1);

    private final Countdown countdown = new Countdown();

    private State state = State.WALKING_TO_TARGET;

    private boolean pointIsClearForTree(Point point) {
        var mapPoint = map.getMapPoint(point);

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

        if (mapPoint.isCrop()) {
            return false;
        }

        // Filter points where the surrounding terrain doesn't allow placing a tree
        var surroundingVegetation = map.getSurroundingTiles(point);

        var noVegetationAllowingTrees = true;

        for (var vegetation : surroundingVegetation) {
            if (!Vegetation.MINABLE_MOUNTAIN.contains(vegetation) &&
                !Vegetation.WATER_VEGETATION.contains(vegetation)) {
                noVegetationAllowingTrees = false;

                break;
            }
        }

        if (noVegetationAllowingTrees) {
            return false;
        }

        // Filter points that the forester cannot walk to
        if (map.findWayOffroad(home.getFlag().getPosition(), point, null) == null) {
            return false;
        }

        return true;
    }

    private Point findPointToPlantTree() {
        var adjacentPoints = map.getPointsWithinRadius(home.getPosition(), RANGE);
        int offset = RANDOM.nextInt(adjacentPoints.size());

        for (int i = 0; i < adjacentPoints.size(); i++) {
            int indexWithOffset = (i + offset) % adjacentPoints.size();
            var point = adjacentPoints.get(indexWithOffset);

            // Filter points where trees cannot be placed
            if (!pointIsClearForTree(point)) {
                continue;
            }

            if (map.getWorkers().stream()
                    .anyMatch(worker -> (worker instanceof Forester forester &&
                            Objects.equals(forester.position, point) &&
                            forester.isPlanting()) ||
                            (worker instanceof Farmer farmer &&
                            Objects.equals(farmer.position, point) &&
                            farmer.isPlanting()))) {
                continue;
            }

            // Return the first point that passed the filter
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
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        RETURNING_TO_STORAGE
    }

    public Forester(Player player, GameMap map) {
        super(player, map);
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
    protected void onIdle() throws InvalidUserActionException {
        switch (state) {
            case RESTING_IN_HOUSE -> {
                if (home.isProductionEnabled()) {
                    if (countdown.hasReachedZero()) {
                        var point = findPointToPlantTree();

                        if (point == null) {
                            return;
                        }

                        setOffroadTarget(point);
                        state = State.GOING_OUT_TO_PLANT;
                    } else {
                        countdown.step();
                    }
                }
            }

            case PLANTING -> {
                if (countdown.hasReachedZero()) {

                    // Place a tree if the point is still open
                    if (pointIsClearForTree(position)) {
                        Tree.TreeType treeType = Tree.PLANTABLE_TREES[(int)(Math.floor(RANDOM.nextDouble() * Tree.PLANTABLE_TREES.length))];

                        map.placeTree(position, treeType, Tree.TreeSize.NEWLY_PLANTED);
                    }

                    state = State.GOING_BACK_TO_HOUSE;
                    returnHomeOffroad();
                } else {
                    countdown.step();
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

    @Override
    protected void onArrival() {
        switch (state) {
            case GOING_OUT_TO_PLANT -> {
                if (map.getWorkers().stream()
                        .noneMatch(worker -> (worker instanceof Forester forester &&
                                (forester.isPlanting() && Objects.equals(forester.position, position))) ||
                                (worker instanceof Farmer farmer &&
                                farmer.isPlanting() && Objects.equals(farmer.position, position))) &&
                !map.isFlagAtPoint(position) &&
                !map.isRoadAtPoint(position)) {
                    state = State.PLANTING;
                    map.reportWorkerStartedAction(this, WorkerAction.PLANTING_TREE);
                    countdown.countFrom(TIME_TO_PLANT);
                } else {
                    state = State.GOING_BACK_TO_HOUSE;
                    setOffroadTarget(home.getPosition(), home.getFlag().getPosition());
                }
            }

            case GOING_BACK_TO_HOUSE -> {
                state = State.RESTING_IN_HOUSE;
                enterBuilding(home);
                countdown.countFrom(TIME_TO_REST);
            }

            case RETURNING_TO_STORAGE -> {
                Storehouse storehouse = (Storehouse) map.getBuildingAtPoint(position);
                storehouse.depositWorker(this);
            }

            case GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE -> {

                // Go to the closest storage
                Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, FORESTER);

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
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, FORESTER);

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;
            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, getPlayer(), FORESTER);

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;
                setOffroadTarget(storage.getPosition());
            } else {
                setOffroadTarget(findPlaceToDie(), position.downRight());
                state = State.GOING_TO_DIE;
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {

        // Return to storage if the planned path no longer exists
        if (state == State.WALKING_TO_TARGET &&
            map.isFlagAtPoint(position) &&
            !map.arePointsConnectedByRoads(position, getTarget())) {

            // Don't try to enter the forester hut upon arrival
            clearTargetBuilding();

            // Go back to the storage
            returnToStorage();
        }
    }

    @Override
    public void goToOtherStorage(Building building) {
        state = State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
