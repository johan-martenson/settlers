/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model.actors;

import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Storehouse;

import java.util.Optional;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.DonkeyBreeder.State.*;

/**
 *
 * @author johan
 */
@Walker (speed = 10)
public class DonkeyBreeder extends Worker {
    private static final int TIME_TO_REST           = 99;
    private static final int TIME_TO_FEED           = 19;
    private static final int TIME_TO_PREPARE_DONKEY = 19;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;

    private State state;

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

        state = WALKING_TO_TARGET;
        countdown = new Countdown();

        productivityMeasurer = new ProductivityMeasurer(TIME_TO_REST + TIME_TO_FEED + TIME_TO_PREPARE_DONKEY, null);
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
        if (buildingOrRoad.isBuilding() &&
                buildingOrRoad.building.isReady() &&
                buildingOrRoad.building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(DONKEY);
        }

        return buildingOrRoad.isRoad() && buildingOrRoad.road.isMainRoad() && buildingOrRoad.road.needsDonkey();
    }

    @Override
    protected void onIdle() {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.hasReachedZero() && getHome().isProductionEnabled()) {
                if (getHome().getAmount(WATER) > 0 && getHome().getAmount(WHEAT) > 0) {
                    Point pointToFeedDonkeysAt = getHome().getPosition().downLeft();

                    state = State.GOING_OUT_TO_FEED;

                    setOffroadTarget(pointToFeedDonkeysAt);
                } else {

                    /* Report that the donkey breeder didn't have resources available to work */
                    productivityMeasurer.reportUnproductivity();
                }
            } else if (getHome().isProductionEnabled()) {
                countdown.step();
            }
        } else if (state == State.FEEDING) {
            if (countdown.hasReachedZero()) {

                getHome().consumeOne(WATER);
                getHome().consumeOne(WHEAT);

                state = GOING_BACK_TO_HOUSE_AFTER_FEEDING;

                returnHomeOffroad();
            } else {
                countdown.step();
            }
        } else if (state == State.PREPARING_DONKEY_FOR_DELIVERY) {
            if (countdown.hasReachedZero() && getHome().isProductionEnabled()) {
                Optional<GameUtils.HouseOrRoad> optionalHouseOrRoad = GameUtils.getClosestHouseOrRoad(getHome().getPosition(), this::isDonkeyReceiver, map);

                /* Don't create a donkey if no delivery is possible */
                if (optionalHouseOrRoad.isEmpty()) {
                    return;
                }

                GameUtils.HouseOrRoad houseOrRoad = optionalHouseOrRoad.get();

                /* Create a donkey and send it to the closest storage or main road missing a donkey */
                Donkey donkey = new Donkey(getPlayer(), map);

                map.placeWorkerFromStepTime(donkey, getHome());

                if (houseOrRoad.isBuilding()) {
                    donkey.returnToStorage(houseOrRoad.building);
                } else {
                    donkey.assignToRoad(houseOrRoad.road);
                }

                /* Report that the worker was productive */
                productivityMeasurer.reportProductivity();
                productivityMeasurer.nextProductivityCycle();

                map.getStatisticsManager().donkeyGrown(player, map.getTime());

                /* Rest in the house before creating the next donkey */
                state = RESTING_IN_HOUSE;

                countdown.countFrom(TIME_TO_REST);
            } else if (getHome().isProductionEnabled()) {
                countdown.step();
            }
        } else if (state == DEAD) {
            if (countdown.hasReachedZero()) {
                map.removeWorker(this);
            } else {
                countdown.step();
            }
        }
    }

    @Override
    public void onArrival() {
        if (state == GOING_BACK_TO_HOUSE_AFTER_FEEDING) {
            enterBuilding(getHome());

            state = PREPARING_DONKEY_FOR_DELIVERY;

            countdown.countFrom(TIME_TO_PREPARE_DONKEY);
        } else if (state == GOING_OUT_TO_FEED) {
            countdown.countFrom(TIME_TO_FEED);

            state = FEEDING;
        } else if (state == RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        } else if (state == GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, DONKEY_BREEDER);

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
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, DONKEY_BREEDER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), DONKEY_BREEDER);

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

            /* Don't try to enter the donkey farm upon arrival */
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
}
