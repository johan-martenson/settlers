/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Butcher.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Butcher.State.GOING_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.Butcher.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Butcher.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Butcher.State.SLAUGHTERING_PIG;
import static org.appland.settlers.model.Butcher.State.WAITING_FOR_SPACE_ON_FLAG;
import static org.appland.settlers.model.Butcher.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.PIG;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Butcher extends Worker {
    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;
    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME    = 99;

    private State state;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        SLAUGHTERING_PIG,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_SPACE_ON_FLAG, RETURNING_TO_STORAGE
    }

    public Butcher(Player player, GameMap map) {
        super(player, map);

        countdown = new Countdown();
        state = WALKING_TO_TARGET;

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME);
    }

    @Override
    protected void onEnterBuilding(Building building) {
        if (building instanceof SlaughterHouse) {
            setHome(building);
        }

        state = RESTING_IN_HOUSE;
        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                state = SLAUGHTERING_PIG;
                countdown.countFrom(PRODUCTION_TIME);
            } else {
                countdown.step();
            }
        } else if (state == SLAUGHTERING_PIG) {
            if (getHome().getAmount(PIG) > 0 && getHome().isProductionEnabled()) {
                if (countdown.reachedZero()) {

                    /* Consume the resource */
                    getHome().consumeOne(PIG);

                    /* Report that the butcher produced one piece of meat */
                    productivityMeasurer.reportProductivity();
                    productivityMeasurer.nextProductivityCycle();

                    /* Handle transportation */
                    if (getHome().getFlag().hasPlaceForMoreCargo()) {
                        Cargo cargo = new Cargo(MEAT, map);

                        setCargo(cargo);

                        /* Go out to the flag to deliver the meat */
                        state = GOING_TO_FLAG_WITH_CARGO;

                        setTarget(getHome().getFlag().getPosition());

                        getHome().getFlag().promiseCargo();
                    } else {
                        state = Butcher.State.WAITING_FOR_SPACE_ON_FLAG;
                    }
                } else {
                    countdown.step();
                }
            } else {

                /* Report that the butcher lacked resources and couldn't do his job */
                productivityMeasurer.reportUnproductivity();
            }
        } else if (state == WAITING_FOR_SPACE_ON_FLAG) {
            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                Cargo cargo = new Cargo(MEAT, map);

                setCargo(cargo);

                /* Go out to the flag to deliver the meat */
                state = GOING_TO_FLAG_WITH_CARGO;

                setTarget(getHome().getFlag().getPosition());

                getHome().getFlag().promiseCargo();
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == GOING_TO_FLAG_WITH_CARGO) {
            Flag flag = map.getFlagAtPoint(getPosition());

            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToStorage();

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
        }
    }

    @Override
    public String toString() {
        return "Butcher " + state;
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = GameUtils.getClosestStorageConnectedByRoads(getPosition(), getHome(), getPlayer());

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

            /* Don't try to enter the slaughter house upon arrival */
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
