/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model.actors;

import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.Vegetation;
import org.appland.settlers.model.Direction;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.MapPoint;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.WorkerAction;

import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.FISHERMAN;
import static org.appland.settlers.model.actors.Fisherman.State.*;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Fisherman extends Worker {
    private static final int TIME_TO_FISH = 89;
    private static final int TIME_TO_REST = 99;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
    private static final int FISHING_RADIUS = 8;
    private static final int TIME_TO_PULL_UP_FISH = 30;
    private static final int TIME_TO_LOWER_FISHING_ROD = 15;

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;

    private State  state;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_FISH,
        FISHING,
        GOING_BACK_TO_HOUSE_WITH_FISH,
        IN_HOUSE_WITH_FISH,
        GOING_TO_FLAG,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        LOWERING_FISHING_ROD,
        PULLING_UP_FISH,
        RETURNING_TO_STORAGE
    }

    public Fisherman(Player player, GameMap map) {
        super(player, map);

        state = WALKING_TO_TARGET;

        countdown = new Countdown();

        productivityMeasurer = new ProductivityMeasurer(TIME_TO_FISH + TIME_TO_REST, null);
    }

    public boolean isFishing() {
        return state == FISHING || state == LOWERING_FISHING_ROD || state == PULLING_UP_FISH;
    }

    @Override
    protected void onEnterBuilding(Building building) {
        if (building.isReady()) {
            state = RESTING_IN_HOUSE;

            countdown.countFrom(TIME_TO_REST);

            productivityMeasurer.setBuilding(building);
        }
    }

    @Override
    protected void onIdle() {
        if (state == RESTING_IN_HOUSE && getHome().isProductionEnabled()) {
            if (!getHome().isOutOfNaturalResources()) {
                if (countdown.hasReachedZero()) {
                    Point point = getFishingSpot();

                    if (point == null) {

                        /* Report that there's no more fish */
                        getHome().reportNoMoreNaturalResources();

                        getPlayer().reportNoMoreResourcesForBuilding(getHome());

                        return;
                    }

                    setOffroadTarget(point);

                    state = GOING_OUT_TO_FISH;
                } else {
                    countdown.step();
                }
            } else {

                /* Report that there was no fish available so the fisherman couldn't fish */
                productivityMeasurer.reportUnproductivity();
            }
        } else if (state == PULLING_UP_FISH) {
            if (countdown.hasReachedZero()) {
                if (map.getAmountFishAtPoint(getPosition()) == 0) {
                    state = GOING_BACK_TO_HOUSE;

                    // TODO: should report non-productivity

                    returnHomeOffroad();
                } else {
                    Cargo cargo = map.catchFishAtPoint(getPosition());

                    setCargo(cargo);

                    state = GOING_BACK_TO_HOUSE_WITH_FISH;
                    returnHomeOffroad();

                    /* Report that the fisherman produced a fish */
                    productivityMeasurer.reportProductivity();
                    productivityMeasurer.nextProductivityCycle();

                    map.getStatisticsManager().fishProduced(player, map.getTime());
                }
            } else {
                countdown.step();
            }
        } else if (state == IN_HOUSE_WITH_FISH) {

            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                state = GOING_TO_FLAG;

                setTarget(getHome().getFlag().getPosition());

                getHome().getFlag().promiseCargo(getCargo());
            } else {
                state = WAITING_FOR_SPACE_ON_FLAG;
            }
        } else if (state == WAITING_FOR_SPACE_ON_FLAG) {

            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                state = GOING_TO_FLAG;

                setTarget(getHome().getFlag().getPosition());

                getHome().getFlag().promiseCargo(getCargo());
            }
        } else if (state == Fisherman.State.DEAD) {
            if (countdown.hasReachedZero()) {
                map.removeWorker(this);
            } else {
                countdown.step();
            }
        } else if (state == LOWERING_FISHING_ROD) {
            if (countdown.hasReachedZero()) {
                state = FISHING;

                map.reportWorkerStartedAction(this, WorkerAction.FISHING);

                countdown.countFrom(TIME_TO_FISH);
            } else {
                countdown.step();
            }
        } else if (state == FISHING) {
            if (countdown.hasReachedZero()) {
                state = PULLING_UP_FISH;

                map.reportWorkerStartedAction(this, WorkerAction.PULL_UP_FISHING_ROD);

                countdown.countFrom(TIME_TO_PULL_UP_FISH);
            } else {
                countdown.step();
            }
        }
    }

    private boolean isFishReceiver(Building building) {
        if (building.isReady() && building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(FISH);
        }

        if (building.isReady() && building.needsMaterial(FISH)) {
            return true;
        }

        return false;
    }

    @Override
    protected void onArrival() {
        if (state == GOING_OUT_TO_FISH) {
            state = LOWERING_FISHING_ROD;

            Point position = getPosition();

            Vegetation vegetationUpLeft = map.getVegetationUpLeft(position);
            Vegetation vegetationAbove = map.getVegetationAbove(position);
            Vegetation vegetationUpRight = map.getVegetationUpRight(position);
            Vegetation vegetationDownRight = map.getVegetationDownRight(position);
            Vegetation vegetationBelow = map.getVegetationBelow(position);
            Vegetation vegetationDownLeft = map.getVegetationDownLeft(position);

            boolean isWaterUpLeft = Vegetation.WATER_VEGETATION.contains(vegetationUpLeft);
            boolean isWaterAbove = Vegetation.WATER_VEGETATION.contains(vegetationAbove);
            boolean isWaterUpRight = Vegetation.WATER_VEGETATION.contains(vegetationUpRight);
            boolean isWaterDownRight = Vegetation.WATER_VEGETATION.contains(vegetationDownRight);
            boolean isWaterBelow = Vegetation.WATER_VEGETATION.contains(vegetationBelow);
            boolean isWaterDownLeft = Vegetation.WATER_VEGETATION.contains(vegetationDownLeft);

            if (isWaterUpLeft && isWaterDownLeft) {
                direction = Direction.LEFT;
            } else if (isWaterUpLeft) {
                direction = Direction.UP_LEFT;
            } else if (isWaterAbove) {
                direction = Direction.UP_RIGHT;
            } else if (isWaterUpRight) {
                direction = Direction.RIGHT;
            } else if (isWaterDownRight) {
                direction = Direction.DOWN_RIGHT;
            } else if (isWaterDownLeft) {
                direction = Direction.DOWN_LEFT;
            }

            map.reportWorkerStartedAction(this, WorkerAction.LOWER_FISHING_ROD);

            countdown.countFrom(TIME_TO_LOWER_FISHING_ROD);
        } else if (state == GOING_BACK_TO_HOUSE) {
            state = RESTING_IN_HOUSE;

            enterBuilding(getHome());

            countdown.countFrom(TIME_TO_REST);
        } else if (state == GOING_BACK_TO_HOUSE_WITH_FISH) {
            enterBuilding(getHome());

            state = IN_HOUSE_WITH_FISH;
        } else if (state == GOING_TO_FLAG) {
            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToReceivingBuilding(this::isFishReceiver);
            getHome().getFlag().putCargo(cargo);

            setCargo(null);

            returnHome();

            state = GOING_BACK_TO_HOUSE;
        } else if (state == RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        } else if (state == GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, FISHERMAN);

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

            state = State.DEAD;

            countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
        }
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, FISHERMAN);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), FISHERMAN);

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

            /* Don't try to enter the fishery upon arrival */
            clearTargetBuilding();

            /* Go back to the storage */
            returnToStorage();
        }
    }

    @Override
    public int getProductivity() {

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

    private Point getFishingSpot() {
        Iterable<Point> fishingArea = GameUtils.getHexagonAreaAroundPoint(getHome().getPosition(), FISHING_RADIUS, map);

        for (Point point : fishingArea) {
            MapPoint mapPoint = map.getMapPoint(point);

            if (mapPoint.isBuilding()) {
                continue;
            }

            if (mapPoint.isStone()) {
                continue;
            }

            if (map.getAmountFishAtPoint(point) == 0) {
                continue;
            }

            if (!map.isNextToAnyWater(point)) {
                continue;
            }

            /* Filter out points that the fisherman can't reach */
            if (map.findWayOffroad(getHome().getFlag().getPosition(), point, null) == null) {
                continue;
            }

            return point;
        }

        return null;
    }
}
