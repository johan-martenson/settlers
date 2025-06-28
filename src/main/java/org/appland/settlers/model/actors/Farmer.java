package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.WorkerAction;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.appland.settlers.model.Crop.GrowthState.FULL_GROWN;
import static org.appland.settlers.model.Crop.GrowthState.HARVESTED;
import static org.appland.settlers.model.Material.FARMER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.actors.Farmer.State.*;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Farmer extends Worker {
    private static final int TIME_TO_REST = 99;
    private static final int TIME_TO_PLANT = 19;
    private static final int TIME_TO_HARVEST = 19;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
    private static final Random RANDOM = new Random(0);

    private final Countdown countdown = new Countdown();
    private final ProductivityMeasurer productivityMeasurer = new ProductivityMeasurer(TIME_TO_REST + TIME_TO_HARVEST + TIME_TO_PLANT, null);

    private Optional<GameUtils.AllocationTracker> wheatAllocationTracker = Optional.empty();
    private State state = WALKING_TO_TARGET;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_PLANT,
        PLANTING,
        GOING_BACK_TO_HOUSE,
        GOING_OUT_TO_HARVEST,
        HARVESTING,
        GOING_BACK_TO_HOUSE_WITH_CARGO,
        GOING_OUT_TO_PUT_CARGO,
        IN_HOUSE_WITH_CARGO,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        RETURNING_TO_STORAGE
    }

    public Farmer(Player player, GameMap map) {
        super(player, map);
    }

    public boolean isHarvesting() {
        return state == HARVESTING;
    }

    public boolean isPlanting() {
        return state == PLANTING;
    }

    @Override
    protected void onEnterBuilding(Building building) {
        state = RESTING_IN_HOUSE;
        countdown.countFrom(TIME_TO_REST);
        productivityMeasurer.setBuilding(building);

        wheatAllocationTracker = wheatAllocationTracker.or(() -> Optional.of(new GameUtils.AllocationTracker(GameUtils.AllocationType.WHEAT_ALLOCATION, player, building.getPosition())));
    }

    @Override
    protected void onIdle() throws InvalidUserActionException {
        switch (state) {
            case RESTING_IN_HOUSE -> {
                if (countdown.hasReachedZero() && home.isProductionEnabled()) {
                    var cropToHarvest = findCropToHarvest();

                    if (cropToHarvest != null) {
                        state = GOING_OUT_TO_HARVEST;
                        setOffroadTarget(cropToHarvest.getPosition());
                    } else if (getSurroundingNonHarvestedCrops().size() < 5) {
                        var point = getFreeSpotToPlant();

                        if (point == null) {
                            productivityMeasurer.reportUnproductivity();

                            return;
                        }

                        setOffroadTarget(point);
                        state = GOING_OUT_TO_PLANT;
                    }
                } else if (home.isProductionEnabled()) {
                    countdown.step();
                } else {
                    productivityMeasurer.reportUnproductivity();
                }
            }

            case PLANTING -> {
                if (countdown.hasReachedZero()) {
                    var cropType = RANDOM.nextInt(10) % 2 == 0
                            ? Crop.CropType.TYPE_2
                            : Crop.CropType.TYPE_1;
                    map.placeCrop(position, cropType);

                    state = GOING_BACK_TO_HOUSE;
                    returnHomeOffroad();
                } else {
                    countdown.step();
                }
            }

            case HARVESTING -> {
                if (countdown.hasReachedZero()) {
                    var crop = map.getCropAtPoint(position);
                    crop.harvest();
                    map.reportHarvestedCrop(crop);
                    carriedCargo = new Cargo(WHEAT, map);

                    productivityMeasurer.reportProductivity();
                    productivityMeasurer.nextProductivityCycle();
                    map.getStatisticsManager().wheatHarvested(player, map.getTime());

                    state = GOING_BACK_TO_HOUSE_WITH_CARGO;
                    returnHomeOffroad();
                } else {
                    countdown.step();
                }
            }

            case IN_HOUSE_WITH_CARGO, WAITING_FOR_SPACE_ON_FLAG -> {
                if (home.getFlag().hasPlaceForMoreCargo()) {
                    setTarget(home.getFlag().getPosition());
                    state = GOING_OUT_TO_PUT_CARGO;
                    home.getFlag().promiseCargo(getCargo());
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

    private Collection<Crop> getSurroundingNonHarvestedCrops() {
        List<Crop> result = new ArrayList<>();

        for (Point point : getSurroundingSpotsForCrops()) {
            if (map.isCropAtPoint(point)) {
                Crop crop = map.getCropAtPoint(point);

                if (crop.getGrowthState() != HARVESTED) {
                    result.add(map.getCropAtPoint(point));
                }
            }
        }

        return result;
    }

    public boolean isWheatReceiverAndAllocationAllowed(Building building) {
        if (building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(WHEAT);
        }

        if (wheatAllocationTracker.isEmpty()) {
            return false;
        }

        GameUtils.AllocationTracker allocationTracker = wheatAllocationTracker.get();

        return building.isReady() &&
                building.needsMaterial(WHEAT) &&
                allocationTracker.isDeliveryAllowed(building);
    }

    @Override
    public void onArrival() {
        switch (state) {
            case GOING_OUT_TO_PUT_CARGO -> {
                carriedCargo.setPosition(position);
                var receivingBuilding = GameUtils.getClosestBuildingConnectedByRoads(position, null, map, this::isWheatReceiverAndAllocationAllowed);

                if (receivingBuilding != null) {
                    carriedCargo.setTarget(receivingBuilding);
                    receivingBuilding.promiseDelivery(carriedCargo.getMaterial());
                    wheatAllocationTracker.ifPresent(at -> at.trackAllocation(receivingBuilding));
                }

                home.getFlag().putCargo(carriedCargo);
                carriedCargo = null;
                state = GOING_BACK_TO_HOUSE;
                setTarget(home.getPosition());
            }
            case GOING_BACK_TO_HOUSE -> {
                state = RESTING_IN_HOUSE;
                enterBuilding(home);
                countdown.countFrom(TIME_TO_REST);
            }
            case GOING_OUT_TO_PLANT -> {
                if (map.getWorkers().stream()
                        .anyMatch(worker ->
                                worker instanceof Farmer farmer && farmer.getPosition().equals(position) && farmer.isPlanting() ||
                                worker instanceof Forester forester && forester.getPosition().equals(position) && forester.isPlanting()) ||
                        map.isFlagAtPoint(position)
                ) {
                    state = GOING_BACK_TO_HOUSE;
                    returnHomeOffroad();
                } else {
                    state = PLANTING;
                    map.reportWorkerStartedAction(this, WorkerAction.PLANTING_WHEAT);
                    countdown.countFrom(TIME_TO_PLANT);

                }
            }
            case GOING_OUT_TO_HARVEST -> {
                state = HARVESTING;
                map.reportWorkerStartedAction(this, WorkerAction.HARVESTING);
                countdown.countFrom(TIME_TO_HARVEST);
            }
            case GOING_BACK_TO_HOUSE_WITH_CARGO -> {
                enterBuilding(home);
                state = IN_HOUSE_WITH_CARGO;
            }
            case RETURNING_TO_STORAGE -> {
                var storehouse = (Storehouse) map.getBuildingAtPoint(position);
                storehouse.depositWorker(this);
            }
            case GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE -> {
                var storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, FARMER);

                if (storehouse != null) {
                    state = RETURNING_TO_STORAGE;
                    setTarget(storehouse.getPosition());
                } else {
                    state = GOING_TO_DIE;
                    var point = findPlaceToDie();
                    setOffroadTarget(point);
                }
            }
            case GOING_TO_DIE -> {
                setDead();
                state = DEAD;
                countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
            }
        }
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, FARMER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, player, FARMER);

            if (storage != null) {
                state = RETURNING_TO_STORAGE;
                setOffroadTarget(storage.getPosition());
            } else {
                setOffroadTarget(findPlaceToDie(), position.downRight());
                state = GOING_TO_DIE;
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {

        // Return to storage if the planned path no longer exists
        if (state == WALKING_TO_TARGET &&
            map.isFlagAtPoint(position) &&
            !map.arePointsConnectedByRoads(position, getTarget())) {

            // Don't try to enter the farm upon arrival
            clearTargetBuilding();

            // Go back to the storage
            returnToStorage();
        }
    }

    @Override
    public int getProductivity() {

        // Measure productivity across the length of four rest-work periods
        return (int)
                (((double)productivityMeasurer.getSumMeasured() /
                        (productivityMeasurer.getNumberOfCycles())) * 100);
    }

    @Override
    public void goToOtherStorage(Building building) {
        state = GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;
        setTarget(building.getFlag().getPosition());
    }

    private Iterable<Point> getSurroundingSpotsForCrops() {
        var hutPoint = home.getPosition();
        var possibleSpotsToPlant = new HashSet<Point>();

        possibleSpotsToPlant.addAll(Arrays.asList(hutPoint.getAdjacentPoints()));
        possibleSpotsToPlant.addAll(Arrays.asList(hutPoint.upLeft().getAdjacentPoints()));
        possibleSpotsToPlant.addAll(Arrays.asList(hutPoint.upRight().getAdjacentPoints()));

        possibleSpotsToPlant.remove(hutPoint);
        possibleSpotsToPlant.remove(hutPoint.upLeft());
        possibleSpotsToPlant.remove(hutPoint.upRight());

        return possibleSpotsToPlant;
    }

    private Point getFreeSpotToPlant() {
        for (var point : getSurroundingSpotsForCrops()) {
            var mapPoint = map.getMapPoint(point);

            // Filter points that's not possible to plant on
            if (mapPoint.isBuilding() || mapPoint.isFlag() || mapPoint.isRoad() ||  mapPoint.isTree() || mapPoint.isStone()) {
                continue;
            }

            // Filter previous crops that aren't harvested yet. It is possible to plant on harvested crops.
            if (mapPoint.isCrop()) {
                var crop = map.getCropAtPoint(point);

                if (crop.getGrowthState() != HARVESTED) {
                    continue;
                }
            }

            // Filter points where someone else is already planting
            if (map.getWorkers().stream()
                    .anyMatch(worker -> ((worker instanceof Farmer farmer) && farmer.isPlanting() && farmer.getPosition().equals(point)) ||
                            ((worker instanceof Forester forester) && forester.getPosition().equals(point) && forester.isPlanting()))) {
                continue;
            }

            // Filter points the farmer can't walk to
            if (map.findWayOffroad(home.getFlag().getPosition(), point, null) == null) {
                continue;
            }

            return point;
        }

        return null;
    }

    private Crop findCropToHarvest() {
        for (var point : getSurroundingSpotsForCrops()) {
            if (map.isCropAtPoint(point)) {
                var crop = map.getCropAtPoint(point);

                // Filter crops that aren't full-grown
                if (crop.getGrowthState() != FULL_GROWN) {
                    continue;
                }

                // Filter crops that can't be reached
                if (map.findWayOffroad(crop.getPosition(), position, null) == null) {
                    continue;
                }

                return crop;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "Farmer{" +
                "countdown=" + countdown.getCount() +
                ", state=" + state +
                ", position=" + position +
                '}';
    }
}
