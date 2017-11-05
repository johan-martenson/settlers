/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Baker.State.BAKING_BREAD;
import static org.appland.settlers.model.Baker.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Baker.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Baker.State.GOING_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.Baker.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Baker.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.WATER;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Baker extends Worker {
    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;
    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME    = 99;

    private State state;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        BAKING_BREAD,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }


    public Baker(Player player, GameMap m) {
        super(player, m);

        countdown = new Countdown();
        state = WALKING_TO_TARGET;

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME);
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Bakery) {
            setHome(b);
        }

        state = RESTING_IN_HOUSE;
        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                state = BAKING_BREAD;
                countdown.countFrom(PRODUCTION_TIME);

                productivityMeasurer.nextProductivityCycle();
            } else {
                countdown.step();
            }
        } else if (state == BAKING_BREAD) {
            if (getHome().getAmount(WATER) > 0 && getHome().getAmount(FLOUR) > 0 && getHome().isProductionEnabled()) {
                if (countdown.reachedZero()) {
                    Cargo cargo = new Cargo(BREAD, map);

                    setCargo(cargo);

                    /* Consume the ingredients */
                    getHome().consumeOne(WATER);
                    getHome().consumeOne(FLOUR);

                    /* Go out to the flag to deliver the bread */
                    state = GOING_TO_FLAG_WITH_CARGO;

                    setTarget(getHome().getFlag().getPosition());

                    /* Report production of the bread */
                    productivityMeasurer.reportProductivity();
                } else {
                    countdown.step();
                }
            } else {

                /* Report the unproductivity */
                productivityMeasurer.reportUnproductivity();
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == GOING_TO_FLAG_WITH_CARGO) {
            Flag f = map.getFlagAtPoint(getPosition());

            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToStorage();

            f.putCargo(getCargo());

            setCargo(null);

            state = GOING_BACK_TO_HOUSE;

            returnHome();
        } else if (state == GOING_BACK_TO_HOUSE) {
            enterBuilding(getHome());

            state = RESTING_IN_HOUSE;

            countdown.countFrom(RESTING_TIME);
        } else if (state == RETURNING_TO_STORAGE) {
            Storage storage = (Storage)map.getBuildingAtPoint(getPosition());

            storage.depositWorker(this);
        }
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = GameUtils.getClosestStorage(getPosition(), getPlayer());

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
    public String toString() {
        return "Baker " + state;
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws Exception {

        /* Return to storage if the planned path no longer exists */
        if (state == State.WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter upon arrival */
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
                (double)(productivityMeasurer.getNumberOfCycles())) * 100);
    }
}
