package org.appland.settlers.model.actors;

import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.Direction;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Vegetation;
import org.appland.settlers.model.WorkerAction;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;

import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.FISHERMAN;
import static org.appland.settlers.model.actors.Fisherman.State.*;

/**
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

    private final Countdown countdown = new Countdown();
    private final ProductivityMeasurer productivityMeasurer = new ProductivityMeasurer(TIME_TO_FISH + TIME_TO_REST, null);

    private State state = WALKING_TO_TARGET;

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
    }

    public boolean isFishing() {
        return switch (state) {
            case FISHING, LOWERING_FISHING_ROD, PULLING_UP_FISH -> true;
            default -> false;
        };
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
        switch (state) {
            case RESTING_IN_HOUSE -> {
                if (home.isProductionEnabled()) {
                    if (!home.isOutOfNaturalResources()) {
                        if (countdown.hasReachedZero()) {
                            var point = getFishingSpot();

                            if (point == null) {
                                home.reportNoMoreNaturalResources();
                                player.reportNoMoreResourcesForBuilding(home);

                                return;
                            }

                            state = GOING_OUT_TO_FISH;
                            setOffroadTarget(point);
                        } else {
                            countdown.step();
                        }
                    } else {
                        productivityMeasurer.reportUnproductivity();
                    }
                }
            }

            case PULLING_UP_FISH -> {
                if (countdown.hasReachedZero()) {
                    if (map.getAmountFishAtPoint(position) == 0) {
                        state = GOING_BACK_TO_HOUSE;
                        returnHomeOffroad();

                        // TODO: should report non-productivity
                    } else {
                        var cargo = map.catchFishAtPoint(position);
                        setCargo(cargo);

                        state = GOING_BACK_TO_HOUSE_WITH_FISH;
                        returnHomeOffroad();

                        productivityMeasurer.reportProductivity();
                        productivityMeasurer.nextProductivityCycle();
                        map.getStatisticsManager().caughtFish(player, map.getTime());
                    }
                } else {
                    countdown.step();
                }
            }

            case IN_HOUSE_WITH_FISH, WAITING_FOR_SPACE_ON_FLAG -> {
                if (home.getFlag().hasPlaceForMoreCargo()) {
                    state = GOING_TO_FLAG;
                    setTarget(home.getFlag().getPosition());
                    home.getFlag().promiseCargo(getCargo());
                } else {
                    state = WAITING_FOR_SPACE_ON_FLAG;
                }
            }

            case DEAD -> {
                if (countdown.hasReachedZero()) {
                    map.removeWorker(this);
                } else {
                    countdown.step();
                }
            }

            case LOWERING_FISHING_ROD -> {
                if (countdown.hasReachedZero()) {
                    state = FISHING;
                    map.reportWorkerStartedAction(this, WorkerAction.FISHING);
                    countdown.countFrom(TIME_TO_FISH);
                } else {
                    countdown.step();
                }
            }

            case FISHING -> {
                if (countdown.hasReachedZero()) {
                    state = PULLING_UP_FISH;
                    map.reportWorkerStartedAction(this, WorkerAction.PULL_UP_FISHING_ROD);
                    countdown.countFrom(TIME_TO_PULL_UP_FISH);
                } else {
                    countdown.step();
                }
            }
        }
    }

    private boolean isFishReceiver(Building building) {
        if (building.isReady()) {
            if (building instanceof Storehouse storehouse) {
                return !storehouse.isDeliveryBlocked(FISH);
            }

            return building.needsMaterial(FISH);
        }

        return false;
    }

    @Override
    protected void onArrival() {
        switch (state) {
            case GOING_OUT_TO_FISH -> {
                state = LOWERING_FISHING_ROD;

                var vegetationUpLeft = map.getVegetationUpLeft(position);
                var vegetationAbove = map.getVegetationAbove(position);
                var vegetationUpRight = map.getVegetationUpRight(position);
                var vegetationDownRight = map.getVegetationDownRight(position);
                var vegetationBelow = map.getVegetationBelow(position);
                var vegetationDownLeft = map.getVegetationDownLeft(position);

                var isWaterUpLeft = Vegetation.WATER_VEGETATION.contains(vegetationUpLeft);
                var isWaterAbove = Vegetation.WATER_VEGETATION.contains(vegetationAbove);
                var isWaterUpRight = Vegetation.WATER_VEGETATION.contains(vegetationUpRight);
                var isWaterDownRight = Vegetation.WATER_VEGETATION.contains(vegetationDownRight);
                var isWaterBelow = Vegetation.WATER_VEGETATION.contains(vegetationBelow);
                var isWaterDownLeft = Vegetation.WATER_VEGETATION.contains(vegetationDownLeft);

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
            }

            case GOING_BACK_TO_HOUSE -> {
                state = RESTING_IN_HOUSE;
                enterBuilding(home);
                countdown.countFrom(TIME_TO_REST);
            }

            case GOING_BACK_TO_HOUSE_WITH_FISH -> {
                enterBuilding(home);
                state = IN_HOUSE_WITH_FISH;
            }

            case GOING_TO_FLAG -> {
                var cargo = getCargo();
                cargo.setPosition(position);
                cargo.transportToReceivingBuilding(this::isFishReceiver);
                home.getFlag().putCargo(cargo);
                setCargo(null);

                returnHome();
                state = GOING_BACK_TO_HOUSE;
            }

            case RETURNING_TO_STORAGE -> {
                var storehouse = (Storehouse) map.getBuildingAtPoint(position);
                storehouse.depositWorker(this);
            }

            case GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE -> {
                var storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, FISHERMAN);

                if (storehouse != null) {
                    state = RETURNING_TO_STORAGE;
                    setTarget(storehouse.getPosition());
                } else {
                    state = GOING_TO_DIE;
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
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, FISHERMAN);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, player, FISHERMAN);

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
        if (state == WALKING_TO_TARGET && map.isFlagAtPoint(position) && !map.arePointsConnectedByRoads(position, getTarget())) {
            clearTargetBuilding();
            returnToStorage();
        }
    }

    @Override
    public int getProductivity() {

        // Measure productivity across the length of four rest-work periods
        return (int)
                (((double) productivityMeasurer.getSumMeasured() /
                        (productivityMeasurer.getNumberOfCycles())) * 100);
    }

    @Override
    public void goToOtherStorage(Building building) {
        state = GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;
        setTarget(building.getFlag().getPosition());
    }

    private Point getFishingSpot() {
        var fishingArea = GameUtils.getHexagonAreaAroundPoint(home.getPosition(), FISHING_RADIUS, map);

        for (var point : fishingArea) {
            var mapPoint = map.getMapPoint(point);

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

            if (map.findWayOffroad(home.getFlag().getPosition(), point, null) == null) {
                continue;
            }

            return point;
        }

        return null;
    }

    @Override
    public boolean isWorking() {
        return state == GOING_OUT_TO_FISH ||
                state == LOWERING_FISHING_ROD ||
                state == FISHING ||
                state == PULLING_UP_FISH ||
                state == GOING_BACK_TO_HOUSE_WITH_FISH ||
                state == IN_HOUSE_WITH_FISH;
    }
}
