/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import org.appland.settlers.assets.CropType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.appland.settlers.model.Crop.GrowthState.FULL_GROWN;
import static org.appland.settlers.model.Crop.GrowthState.HARVESTED;
import static org.appland.settlers.model.Farmer.State.*;
import static org.appland.settlers.model.Material.FARMER;
import static org.appland.settlers.model.Material.WHEAT;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Farmer extends Worker {
    private static final int TIME_TO_REST    = 99;
    private static final int TIME_TO_PLANT   = 19;
    private static final int TIME_TO_HARVEST = 19;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
    private static final Random random = new Random(0);

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;

    private State state;

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

        state = WALKING_TO_TARGET;
        countdown = new Countdown();

        productivityMeasurer = new ProductivityMeasurer(TIME_TO_REST + TIME_TO_HARVEST + TIME_TO_PLANT, null);
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
    }

    @Override
    protected void onIdle() throws InvalidUserActionException {

        if (state == RESTING_IN_HOUSE) {
            if (countdown.hasReachedZero() && getHome().isProductionEnabled()) {
                Crop cropToHarvest = findCropToHarvest();

                if (cropToHarvest != null) {
                    state = GOING_OUT_TO_HARVEST;

                    setOffroadTarget(cropToHarvest.getPosition());
                } else if (getSurroundingNonHarvestedCrops().size() < 5) {
                    Point point = getFreeSpotToPlant();

                    if (point == null) {

                        /* Report that it's not possible to harvest or plant */
                        productivityMeasurer.reportUnproductivity();

                        return;
                    }

                    setOffroadTarget(point);

                    state = GOING_OUT_TO_PLANT;
                }
            } else if (getHome().isProductionEnabled()) {
                countdown.step();
            } else {

                /* Report that the farmer isn't working (or resting) */
                productivityMeasurer.reportUnproductivity();
            }
        } else if (state == PLANTING) {
            if (countdown.hasReachedZero()) {

                var cropType = CropType.TYPE_1;

                if (random.nextInt(10) % 2 == 0) {
                    cropType = CropType.TYPE_2;
                }

                map.placeCrop(getPosition(), cropType);

                state = GOING_BACK_TO_HOUSE;

                returnHomeOffroad();
            } else {
                countdown.step();
            }
        } else if (state == HARVESTING) {
            if (countdown.hasReachedZero()) {

                Crop crop = map.getCropAtPoint(getPosition());
                crop.harvest();

                /* Report the harvested crop */
                map.reportHarvestedCrop(crop);

                /* Create a crop cargo to make sure the map is set correctly */
                setCargo(new Cargo(WHEAT, map));

                /* Go back to the farm with the wheat */
                state = GOING_BACK_TO_HOUSE_WITH_CARGO;

                returnHomeOffroad();

                /* Report the productivity */
                productivityMeasurer.reportProductivity();

                productivityMeasurer.nextProductivityCycle();
            } else {
                countdown.step();
            }
        } else if (state == IN_HOUSE_WITH_CARGO) {

            if (getHome().getFlag().hasPlaceForMoreCargo()) {

                setTarget(getHome().getFlag().getPosition());

                state = GOING_OUT_TO_PUT_CARGO;

                /* Tell the flag that the cargo will be delivered */
                getHome().getFlag().promiseCargo(getCargo());
            } else {
                state = WAITING_FOR_SPACE_ON_FLAG;
            }
        } else if (state == WAITING_FOR_SPACE_ON_FLAG) {
            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                state = GOING_OUT_TO_PUT_CARGO;

                setTarget(getHome().getFlag().getPosition());

                /* Tell the flag that the cargo will be delivered */
                getHome().getFlag().promiseCargo(getCargo());
            }
        } else if (state == DEAD) {
            if (countdown.hasReachedZero()) {
                map.removeWorker(this);
            } else {
                countdown.step();
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

    public boolean isWheatReceiver(Building building) {
        if (building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(WHEAT);
        }

        if (building.isReady() && building.needsMaterial(WHEAT)) {
            return true;
        }

        return false;
    }

    @Override
    public void onArrival() {

        if (state == GOING_OUT_TO_PUT_CARGO) {

            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToReceivingBuilding(this::isWheatReceiver);
            getHome().getFlag().putCargo(cargo);

            setCargo(null);

            state = GOING_BACK_TO_HOUSE;

            setTarget(getHome().getPosition());
        } else if (state == GOING_BACK_TO_HOUSE) {
            state = RESTING_IN_HOUSE;

            enterBuilding(getHome());

            countdown.countFrom(TIME_TO_REST);
        } else if (state == GOING_OUT_TO_PLANT) {
            state = PLANTING;

            map.reportWorkerStartedAction(this, WorkerAction.PLANTING_WHEAT);

            countdown.countFrom(TIME_TO_PLANT);
        } else if (state == GOING_OUT_TO_HARVEST) {
            state = HARVESTING;

            map.reportWorkerStartedAction(this, WorkerAction.HARVESTING);

            countdown.countFrom(TIME_TO_HARVEST);
        } else if (state == GOING_BACK_TO_HOUSE_WITH_CARGO) {
            enterBuilding(getHome());

            state = IN_HOUSE_WITH_CARGO;
        } else if (state == RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        } else if (state == GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, FARMER);

            if (storehouse != null) {
                state = RETURNING_TO_STORAGE;

                setTarget(storehouse.getPosition());
            } else {
                state = GOING_TO_DIE;

                Point point = findPlaceToDie();

                setOffroadTarget(point);
            }
        } else if (state == GOING_TO_DIE) {
            setDead();

            state = DEAD;

            countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
        }
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, FARMER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), FARMER);

            if (storage != null) {
                state = RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            } else {
                Point point = findPlaceToDie();

                setOffroadTarget(point, getPosition().downRight());

                state = GOING_TO_DIE;
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {

        /* Return to storage if the planned path no longer exists */
        if (state == WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter the farm upon arrival */
            clearTargetBuilding();

            /* Go back to the storage */
            returnToStorage();
        }
    }

    @Override
    int getProductivity() {

        /* Measure productivity across the length of four rest-work periods */
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
        Point hutPoint = getHome().getPosition();

        Set<Point> possibleSpotsToPlant = new HashSet<>();

        possibleSpotsToPlant.addAll(Arrays.asList(hutPoint.getAdjacentPoints()));
        possibleSpotsToPlant.addAll(Arrays.asList(hutPoint.upLeft().getAdjacentPoints()));
        possibleSpotsToPlant.addAll(Arrays.asList(hutPoint.upRight().getAdjacentPoints()));

        possibleSpotsToPlant.remove(hutPoint);
        possibleSpotsToPlant.remove(hutPoint.upLeft());
        possibleSpotsToPlant.remove(hutPoint.upRight());

        return possibleSpotsToPlant;
    }

    private Point getFreeSpotToPlant() {

        for (Point point : getSurroundingSpotsForCrops()) {

            MapPoint mapPoint = map.getMapPoint(point);

            /* Filter points that's not possible to plant on */
            if (mapPoint.isBuilding() || mapPoint.isFlag() || mapPoint.isRoad() ||  mapPoint.isTree() || mapPoint.isStone()) {
                continue;
            }

            /* Filter previous crops that aren't harvested yet. It is possible to plant on harvested crops. */
            if (mapPoint.isCrop()) {
                Crop crop = map.getCropAtPoint(point);

                if (crop.getGrowthState() != HARVESTED) {
                    continue;
                }
            }

            /* Filter points the farmer can't walk to */
            if (map.findWayOffroad(getHome().getFlag().getPosition(), point, null) == null) {
                continue;
            }

            return point;
        }

        return null;
    }

    private Crop findCropToHarvest() {
        for (Point point : getSurroundingSpotsForCrops()) {
            if (map.isCropAtPoint(point)) {
                Crop crop = map.getCropAtPoint(point);

                /* Filter crops that aren't full-grown */
                if (crop.getGrowthState() != FULL_GROWN) {
                    continue;
                }

                /* Filter crops that can't be reached */
                if (map.findWayOffroad(crop.getPosition(), getPosition(), null) == null) {
                    continue;
                }

                return crop;
            }
        }

        return null;
    }
}
