/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model.actors;

import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Storehouse;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Minter.State.*;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Minter extends Worker {

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        MAKING_COIN,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        RETURNING_TO_STORAGE
    }

    private static final int PRODUCTION_TIME = 49;
    private static final int RESTING_TIME = 99;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;

    private State state;

    public Minter(Player player, GameMap map) {
        super(player, map);

        countdown = new Countdown();
        state = State.WALKING_TO_TARGET;

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME, null);
    }

    @Override
    protected void onEnterBuilding(Building building) {
        state = RESTING_IN_HOUSE;
        countdown.countFrom(RESTING_TIME);

        productivityMeasurer.setBuilding(building);
    }

    @Override
    protected void onIdle() {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.hasReachedZero()) {
                state = MAKING_COIN;
                countdown.countFrom(PRODUCTION_TIME);
            } else {
                countdown.step();
            }
        } else if (state == MAKING_COIN) {
            if (getHome().getAmount(GOLD) > 0 && getHome().getAmount(COAL) > 0 && getHome().isProductionEnabled()) {
                if (countdown.hasReachedZero()) {

                    /* Consume resources */
                    getHome().consumeOne(GOLD);
                    getHome().consumeOne(COAL);

                    /* Report that the minter produced a coin */
                    productivityMeasurer.reportProductivity();
                    productivityMeasurer.nextProductivityCycle();

                    /* Handle transportation */
                    if (getHome().getFlag().hasPlaceForMoreCargo()) {
                        Cargo cargo = new Cargo(COIN, map);

                        setCargo(cargo);

                        /* Go out to the flag to deliver the coin */
                        state = State.GOING_TO_FLAG_WITH_CARGO;

                        setTarget(getHome().getFlag().getPosition());

                        getHome().getFlag().promiseCargo(getCargo());
                    } else {
                        state = WAITING_FOR_SPACE_ON_FLAG;
                    }
                } else {
                    countdown.step();
                }
            } else {

                /* Report that the minter lacked resources and couldn't produce a coin */
                productivityMeasurer.reportUnproductivity();
            }
        } else if (state == WAITING_FOR_SPACE_ON_FLAG) {
            if (getHome().getFlag().hasPlaceForMoreCargo()) {

                Cargo cargo = new Cargo(COIN, map);

                setCargo(cargo);

                /* Go out to the flag to deliver the coin */
                state = GOING_TO_FLAG_WITH_CARGO;

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

    private boolean isCoinReceiver(Building building) {
        if (building.isReady() && building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(COIN);
        }

        if (building.isReady() && building.needsMaterial(COIN)) {
            return true;
        }

        return false;

    }

    @Override
    protected void onArrival() {
        if (state == GOING_TO_FLAG_WITH_CARGO) {
            Flag flag = map.getFlagAtPoint(getPosition());

            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToReceivingBuilding(this::isCoinReceiver);

            flag.putCargo(getCargo());

            setCargo(null);

            state = GOING_BACK_TO_HOUSE;

            returnHome();
        } else if (state == GOING_BACK_TO_HOUSE) {
            enterBuilding(getHome());

            state = RESTING_IN_HOUSE;

            countdown.countFrom(RESTING_TIME);
        } else if (state == RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        } else if (state == State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, MINTER);

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
    public String toString() {
        if (isExactlyAtPoint()) {
            return "Minter " + getPosition();
        } else {
            return "Minter " + getPosition() + " - " + getNextPoint();
        }
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, MINTER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), MINTER);

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

            /* Don't try to enter the mint upon arrival */
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
        state = State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
