/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Tile.Vegetation.BUILDABLE_MOUNTAIN;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class WellWorker extends Worker {
    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME    = 99;

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;

    private State  state;

    public WellWorker(Player player, GameMap map) {
        super(player, map);

        countdown = new Countdown();
        state     = State.WALKING_TO_TARGET;

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME);
    }

    private enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        DRAWING_WATER,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }

    @Override
    protected void onEnterBuilding(Building building) {
        if (building instanceof Well) {
            setHome(building);
        }

        state = State.RESTING_IN_HOUSE;

        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == State.RESTING_IN_HOUSE) {
            if (countdown.reachedZero() && getHome().isProductionEnabled() && isWaterInGround()) {
                state = State.DRAWING_WATER;

                countdown.countFrom(PRODUCTION_TIME);
            } else if (getHome().isProductionEnabled()) {
                countdown.step();
            } else {

                /* Report that the well worker couldn't do his job */
                productivityMeasurer.reportUnproductivity();
            }
        } else if (state == State.DRAWING_WATER) {
            if (countdown.reachedZero()) {
                Cargo cargo = new Cargo(WATER, map);

                setCargo(cargo);

                /* Go out to the flag to deliver the water */
                setTarget(getHome().getFlag().getPosition());

                state = State.GOING_TO_FLAG_WITH_CARGO;

                /* Report that the well worker produced water */
                productivityMeasurer.reportProductivity();
                productivityMeasurer.nextProductivityCycle();
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == State.GOING_TO_FLAG_WITH_CARGO) {
            Flag flag = getHome().getFlag();
            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToStorage();

            flag.putCargo(getCargo());

            setCargo(null);

            returnHome();

            state = State.GOING_BACK_TO_HOUSE;
        } else if (state == State.GOING_BACK_TO_HOUSE) {
            enterBuilding(getHome());

            state = State.RESTING_IN_HOUSE;
            countdown.countFrom(RESTING_TIME);
        } else if (state == State.RETURNING_TO_STORAGE) {
            Storage storage = (Storage)map.getBuildingAtPoint(getPosition());

            storage.depositWorker(this);
        }
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = GameUtils.getClosestStorage(getPosition(), getPlayer());

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;

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

            /* Don't try to enter the well upon arrival */
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

    private boolean isWaterInGround() {

        /* The well worker can't produce water in a desert */
        if (map.getTerrain().isSurroundedBy(getHome().getPosition(), BUILDABLE_MOUNTAIN)) {
            return false;
        }

        return true;
    }
}
