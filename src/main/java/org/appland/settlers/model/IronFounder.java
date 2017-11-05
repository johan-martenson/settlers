/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.IronFounder.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.IronFounder.State.GOING_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.IronFounder.State.MELTING_IRON;
import static org.appland.settlers.model.IronFounder.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.IronFounder.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.IronFounder.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.IRON_BAR;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class IronFounder extends Worker {
    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;
    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME    = 99;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        MELTING_IRON,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }

    private State state;

    public IronFounder(Player player, GameMap m) {
        super(player, m);

        countdown = new Countdown();
        state = WALKING_TO_TARGET;

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME);
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Sawmill) {
            setHome(b);
        }

        state = RESTING_IN_HOUSE;
        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                state = MELTING_IRON;
                countdown.countFrom(PRODUCTION_TIME);
            } else {
                countdown.step();
            }
        } else if (state == MELTING_IRON) {
            if (getHome().getAmount(COAL) > 0 && getHome().getAmount(IRON) > 0 && getHome().isProductionEnabled()) {
                if (countdown.reachedZero()) {
                    Cargo cargo = new Cargo(IRON_BAR, map);

                    setCargo(cargo);

                    /* Consume the resources */
                    getHome().consumeOne(COAL);
                    getHome().consumeOne(IRON);

                    /* Go out to the flag to deliver the iron bar */
                    state = GOING_TO_FLAG_WITH_CARGO;

                    setTarget(getHome().getFlag().getPosition());

                    /* Report that the iron founder produced */
                    productivityMeasurer.reportProductivity();
                    productivityMeasurer.nextProductivityCycle();
                } else {
                    countdown.step();
                }
            } else {

                /* Report that the iron founder lacked resources and couldn't work */
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
    public String toString() {
        return "Iron founder " + state;
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
    protected void onWalkingAndAtFixedPoint() throws Exception {

        /* Return to storage if the planned path no longer exists */
        if (state == State.WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter the iron foundery upon arrival */
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
