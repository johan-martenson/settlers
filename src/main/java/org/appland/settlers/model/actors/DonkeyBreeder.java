package org.appland.settlers.model.actors;

import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.DonkeyBreeder.State.*;

/**
 *
 * @author johan
 */
@Walker (speed = 10)
public class DonkeyBreeder extends Worker {
    private static final int TIME_TO_REST = 99;
    private static final int TIME_TO_FEED = 19;
    private static final int TIME_TO_PREPARE_DONKEY = 19;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown = new Countdown();
    private final ProductivityMeasurer productivityMeasurer = new ProductivityMeasurer(TIME_TO_REST + TIME_TO_FEED + TIME_TO_PREPARE_DONKEY, null);

    private State state = WALKING_TO_TARGET;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_FEED,
        FEEDING,
        GOING_BACK_TO_HOUSE_AFTER_FEEDING,
        PREPARING_DONKEY_FOR_DELIVERY,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        RETURNING_TO_STORAGE
    }

    public DonkeyBreeder(Player player, GameMap map) {
        super(player, map);
    }

    public boolean isFeeding() {
        return state == State.FEEDING;
    }

    @Override
    protected void onEnterBuilding(Building building) {
        state = RESTING_IN_HOUSE;
        countdown.countFrom(TIME_TO_REST);
        productivityMeasurer.setBuilding(building);
    }

    private boolean isDonkeyReceiver(GameUtils.HouseOrRoad buildingOrRoad) {
        if (buildingOrRoad.isStorehouse() && buildingOrRoad.building.isReady()) {
            return !((Storehouse) buildingOrRoad.building).isDeliveryBlocked(DONKEY);
        }

        return buildingOrRoad.isRoad() && buildingOrRoad.road.isMainRoad() && buildingOrRoad.road.needsDonkey();
    }

    @Override
    protected void onIdle() {
        switch (state) {
            case RESTING_IN_HOUSE -> {
                if (countdown.hasReachedZero() && home.isProductionEnabled()) {
                    if (home.getAmount(WATER) > 0 && home.getAmount(WHEAT) > 0) {
                        setOffroadTarget(home.getPosition().downLeft());
                        state = State.GOING_OUT_TO_FEED;
                        player.reportChangedBuilding(home);
                    } else {
                        productivityMeasurer.reportUnproductivity();
                    }
                } else if (home.isProductionEnabled()) {
                    countdown.step();
                }
            }

            case FEEDING -> {
                if (countdown.hasReachedZero()) {
                    home.consumeOne(WATER);
                    home.consumeOne(WHEAT);

                    state = GOING_BACK_TO_HOUSE_AFTER_FEEDING;
                    returnHomeOffroad();
                } else {
                    countdown.step();
                }
            }

            case PREPARING_DONKEY_FOR_DELIVERY -> {
                if (countdown.hasReachedZero() && home.isProductionEnabled()) {
                    var houseOrRoadOpt = GameUtils.getClosestHouseOrRoad(home.getPosition(), this::isDonkeyReceiver, map);

                    if (houseOrRoadOpt.isEmpty()) {
                        return;
                    }

                    var houseOrRoad = houseOrRoadOpt.get();
                    var donkey = new Donkey(player, map);

                    map.placeWorkerFromStepTime(donkey, home);

                    if (houseOrRoad.isBuilding()) {
                        donkey.returnToStorage(houseOrRoad.building);
                    } else {
                        donkey.assignToRoad(houseOrRoad.road);
                    }

                    productivityMeasurer.reportProductivity();
                    productivityMeasurer.nextProductivityCycle();
                    map.getStatisticsManager().donkeyGrown(player, map.getTime());

                    countdown.countFrom(TIME_TO_REST);
                    state = RESTING_IN_HOUSE;
                } else if (home.isProductionEnabled()) {
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
    public void onArrival() {
        switch (state) {
            case GOING_BACK_TO_HOUSE_AFTER_FEEDING -> {
                enterBuilding(home);
                countdown.countFrom(TIME_TO_PREPARE_DONKEY);
                state = PREPARING_DONKEY_FOR_DELIVERY;
            }

            case GOING_OUT_TO_FEED -> {
                countdown.countFrom(TIME_TO_FEED);
                state = FEEDING;
            }

            case RETURNING_TO_STORAGE -> {
                var storehouse = (Storehouse) map.getBuildingAtPoint(position);
                storehouse.depositWorker(this);
            }

            case GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE -> {
                var storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(
                        position, null, map, DONKEY_BREEDER
                );

                if (storehouse != null) {
                    setTarget(storehouse.getPosition());
                    state = RETURNING_TO_STORAGE;
                } else {
                    setOffroadTarget(findPlaceToDie());
                    state = GOING_TO_DIE;
                }
            }

            case GOING_TO_DIE -> {
                setDead();

                countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
                state = DEAD;
            }
        }
    }

    @Override
    protected void onReturnToStorage() {
        var connectedStorage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, DONKEY_BREEDER);

        if (connectedStorage != null) {
            state = RETURNING_TO_STORAGE;
            setTarget(connectedStorage.getPosition());

            return;
        }

        var offroadStorage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, player, DONKEY_BREEDER);

        if (offroadStorage != null) {
            state = RETURNING_TO_STORAGE;
            setOffroadTarget(offroadStorage.getPosition());
        } else {
            setOffroadTarget(findPlaceToDie(), position.downRight());
            state = GOING_TO_DIE;
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {
        if (state == WALKING_TO_TARGET &&
            map.isFlagAtPoint(position) &&
            !map.arePointsConnectedByRoads(position, getTarget())) {
            clearTargetBuilding();
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

    @Override
    public boolean isWorking() {
        return state == State.FEEDING || state == State.PREPARING_DONKEY_FOR_DELIVERY || state == GOING_OUT_TO_FEED;
    }
}
