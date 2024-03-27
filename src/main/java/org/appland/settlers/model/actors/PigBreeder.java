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
import org.appland.settlers.model.buildings.Storehouse;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.PigBreeder.State.*;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class PigBreeder extends Worker {

    private static final int TIME_TO_REST        = 99;
    private static final int TIME_TO_FEED        = 19;
    private static final int TIME_TO_PREPARE_PIG = 19;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private State state;

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_FEED,
        FEEDING,
        GOING_BACK_TO_HOUSE_AFTER_FEEDING,
        PREPARING_PIG_FOR_DELIVERY,
        GOING_BACK_TO_HOUSE,
        GOING_OUT_TO_PUT_CARGO,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        RETURNING_TO_STORAGE
    }

    public PigBreeder(Player player, GameMap map) {
        super(player, map);

        state = State.WALKING_TO_TARGET;
        countdown = new Countdown();

        productivityMeasurer = new ProductivityMeasurer(TIME_TO_REST + TIME_TO_FEED + TIME_TO_PREPARE_PIG, null);
    }

    public boolean isFeeding() {
        return state == FEEDING;
    }

    @Override
    protected void onEnterBuilding(Building building) {
        state = RESTING_IN_HOUSE;

        countdown.countFrom(TIME_TO_REST);

        productivityMeasurer.setBuilding(building);
    }

    @Override
    protected void onIdle() {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.hasReachedZero() && getHome().isProductionEnabled()) {
                if (getHome().getAmount(WATER) > 0 && getHome().getAmount(WHEAT) > 0) {
                    Point pointToFeedPigsAt = getHome().getPosition().downLeft();

                    state = State.GOING_OUT_TO_FEED;

                    setOffroadTarget(pointToFeedPigsAt);
                } else {

                    /* Report that the pig breeder couldn't go out to feed because it's lacking resources */
                    productivityMeasurer.reportUnproductivity();
                }
            } else if (getHome().isProductionEnabled()) {
                countdown.step();
            }
        } else if (state == FEEDING) {
            if (countdown.hasReachedZero()) {

                /* Consume the resources */
                getHome().consumeOne(WATER);
                getHome().consumeOne(WHEAT);

                /* Go back into the farm */
                state = GOING_BACK_TO_HOUSE_AFTER_FEEDING;

                returnHomeOffroad();
            } else {
                countdown.step();
            }
        } else if (state == PREPARING_PIG_FOR_DELIVERY) {
            if (countdown.hasReachedZero()) {

                /* Report that the pig breeder produced a pig */
                productivityMeasurer.reportProductivity();
                productivityMeasurer.nextProductivityCycle();

                /* Handle transportation */
                if (getHome().getFlag().hasPlaceForMoreCargo()) {
                    Cargo cargo = new Cargo(PIG, map);

                    setCargo(cargo);

                    /* Go out to the flag to deliver the pig */
                    state = GOING_OUT_TO_PUT_CARGO;

                    setTarget(getHome().getFlag().getPosition());

                    getHome().getFlag().promiseCargo(getCargo());
                } else {
                    state = WAITING_FOR_SPACE_ON_FLAG;
                }
            } else {
                countdown.step();
            }
        } else if (state == WAITING_FOR_SPACE_ON_FLAG) {
            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                Cargo cargo = new Cargo(PIG, map);

                setCargo(cargo);

                /* Go out to the flag to deliver the pig */
                state = GOING_OUT_TO_PUT_CARGO;

                setTarget(getHome().getFlag().getPosition());

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

    private boolean isPigReceiver(Building building) {
        if (building.isReady() && building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(PIG);
        }

        if (building.isReady() && building.needsMaterial(PIG)) {
            return true;
        }

        return false;
    }

    @Override
    public void onArrival() {
        if (state == GOING_OUT_TO_PUT_CARGO) {
            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToReceivingBuilding(this::isPigReceiver);
            getHome().getFlag().putCargo(cargo);

            setCargo(null);

            setTarget(getHome().getPosition());

            state = GOING_BACK_TO_HOUSE;
        } else if (state == GOING_BACK_TO_HOUSE) {
            state = RESTING_IN_HOUSE;

            enterBuilding(getHome());

            countdown.countFrom(TIME_TO_REST);
        } else if (state == GOING_BACK_TO_HOUSE_AFTER_FEEDING) {
            enterBuilding(getHome());

            state = PREPARING_PIG_FOR_DELIVERY;

            countdown.countFrom(TIME_TO_PREPARE_PIG);
        } else if (state == GOING_OUT_TO_FEED) {
            countdown.countFrom(TIME_TO_FEED);

            state = FEEDING;
        } else if (state == RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        } else if (state == PigBreeder.State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, PIG_BREEDER);

            if (storehouse != null) {
                state = RETURNING_TO_STORAGE;

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
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, PIG_BREEDER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), PIG_BREEDER);

            if (storage != null) {
                state = RETURNING_TO_STORAGE;

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
        if (state == WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter the pig farm upon arrival */
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
        state = PigBreeder.State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
