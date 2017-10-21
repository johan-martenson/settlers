/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.DonkeyBreeder.State.FEEDING;
import static org.appland.settlers.model.DonkeyBreeder.State.GOING_BACK_TO_HOUSE_AFTER_FEEDING;
import static org.appland.settlers.model.DonkeyBreeder.State.GOING_OUT_TO_FEED;
import static org.appland.settlers.model.DonkeyBreeder.State.PREPARING_DONKEY_FOR_DELIVERY;
import static org.appland.settlers.model.DonkeyBreeder.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.DonkeyBreeder.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.DonkeyBreeder.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;

/**
 *
 * @author johan
 */
@Walker (speed = 10)
public class DonkeyBreeder extends Worker {

    private static final int TIME_TO_REST           = 99;
    private static final int TIME_TO_FEED           = 19;
    private static final int TIME_TO_PREPARE_DONKEY = 19;

    private State state;
    private final Countdown countdown;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_FEED,
        FEEDING,
        GOING_BACK_TO_HOUSE_AFTER_FEEDING,
        PREPARING_DONKEY_FOR_DELIVERY,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }

    public DonkeyBreeder(Player player, GameMap map) {
        super(player, map);

        state = WALKING_TO_TARGET;
        countdown = new Countdown();
    }

    public boolean isFeeding() {
        return state == FEEDING;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Storage) {
            return;
        } else if (b instanceof DonkeyFarm) {
            setHome(b);
        }

        state = RESTING_IN_HOUSE;

        countdown.countFrom(TIME_TO_REST);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero() && getHome().isProductionEnabled()) {
                if (getHome().getAmount(WATER) > 0 && getHome().getAmount(WHEAT) > 0) {
                    Point pointToFeedDonkeysAt = getHome().getPosition().downLeft();

                    state = GOING_OUT_TO_FEED;

                    setOffroadTarget(pointToFeedDonkeysAt);
                }
            } else if (getHome().isProductionEnabled()) {
                countdown.step();
            }
        } else if (state == FEEDING) {
            if (countdown.reachedZero()) {

                getHome().consumeOne(WATER);
                getHome().consumeOne(WHEAT);

                state = GOING_BACK_TO_HOUSE_AFTER_FEEDING;

                returnHomeOffroad();
            } else {
                countdown.step();
            }
        } else if (state == PREPARING_DONKEY_FOR_DELIVERY) {
            if (countdown.reachedZero() && getHome().isProductionEnabled()) {

                /* Don't create a donkey if there is no road to a storage */
                Storage storage = GameUtils.getClosestStorage(getHome().getPosition(), getPlayer());

                if (storage == null) {
                    return;
                }

                /* Create a donkey and send it to the closest storage */
                Donkey donkey = new Donkey(getPlayer(), map);

                map.placeWorkerFromStepTime(donkey, getHome());

                donkey.returnToStorage();

                /* Rest in the house before creating the next donkey */
                state = RESTING_IN_HOUSE;

                countdown.countFrom(TIME_TO_REST);
            } else if (getHome().isProductionEnabled()) {
                countdown.step();
            }
        }
    }

    @Override
    public void onArrival() throws Exception {
        if (state == GOING_BACK_TO_HOUSE_AFTER_FEEDING) {
            enterBuilding(getHome());

            state = PREPARING_DONKEY_FOR_DELIVERY;

            countdown.countFrom(TIME_TO_PREPARE_DONKEY);
        } else if (state == GOING_OUT_TO_FEED) {
            countdown.countFrom(TIME_TO_FEED);

            state = FEEDING;
        } else if (state == RETURNING_TO_STORAGE) {
            Storage storage = (Storage)map.getBuildingAtPoint(getPosition());

            storage.depositWorker(this);
        }
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = GameUtils.getClosestStorage(getPosition(), getHome(), getPlayer());

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroad(getPlayer(), getPosition());

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws Exception {

        /* Return to storage if the planned path no longer exists */
        if (state == State.WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter the donkey farm upon arrival */
            clearTargetBuilding();

            /* Go back to the storage */
            returnToStorage();
        }
    }
}
