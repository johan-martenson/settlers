/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WELL_WORKER;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class WellWorker extends Worker {
    private static final int PRODUCTION_TIME = 49;
    private static final int RESTING_TIME    = 99;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;

    private State  state;

    public WellWorker(Player player, GameMap map) {
        super(player, map);

        countdown = new Countdown();
        state     = State.WALKING_TO_TARGET;

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME, null);
    }

    private enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        DRAWING_WATER,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_SPACE_ON_FLAG, GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE, GOING_TO_DIE, DEAD, RETURNING_TO_STORAGE
    }

    @Override
    protected void onEnterBuilding(Building building) {
        state = State.RESTING_IN_HOUSE;

        countdown.countFrom(RESTING_TIME);

        productivityMeasurer.setBuilding(building);
    }

    @Override
    protected void onIdle() throws InvalidRouteException {
        if (state == State.RESTING_IN_HOUSE) {
            if (countdown.hasReachedZero() && getHome().isProductionEnabled() && isWaterInGround()) {
                state = State.DRAWING_WATER;

                countdown.countFrom(PRODUCTION_TIME);
            } else if (getHome().isProductionEnabled()) {
                countdown.step();
            } else {

                /* Report that the well worker couldn't do his job */
                productivityMeasurer.reportUnproductivity();
            }
        } else if (state == State.DRAWING_WATER) {
            if (countdown.hasReachedZero()) {

                /* Report that the well worker produced water */
                productivityMeasurer.reportProductivity();
                productivityMeasurer.nextProductivityCycle();

                /* Handle transportation */
                if (getHome().getFlag().hasPlaceForMoreCargo()) {
                    Cargo cargo = new Cargo(WATER, map);

                    setCargo(cargo);

                    /* Go out to the flag to deliver the water */
                    setTarget(getHome().getFlag().getPosition());

                    state = State.GOING_TO_FLAG_WITH_CARGO;

                    getHome().getFlag().promiseCargo(getCargo());
                } else {
                    state = WellWorker.State.WAITING_FOR_SPACE_ON_FLAG;
                }
            } else {
                countdown.step();
            }
        } else if (state == State.WAITING_FOR_SPACE_ON_FLAG) {
            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                Cargo cargo = new Cargo(WATER, map);

                setCargo(cargo);

                /* Go out to the flag to deliver the water */
                setTarget(getHome().getFlag().getPosition());

                state = State.GOING_TO_FLAG_WITH_CARGO;

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

    @Override
    protected void onArrival() throws InvalidRouteException {
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
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        } else if (state == State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, WELL_WORKER);

            if (storehouse != null) {
                state = State.RETURNING_TO_STORAGE;

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
    protected void onReturnToStorage() throws InvalidRouteException {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, WELL_WORKER);

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), WELL_WORKER);

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            } else {
                Point point = findPlaceToDie();

                setOffroadTarget(point, getPosition().downRight());

                state = State.GOING_TO_DIE;
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws InvalidRouteException {

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
                        (productivityMeasurer.getNumberOfCycles())) * 100);
    }

    private boolean isWaterInGround() {

        /* The well worker can't produce water in a desert */
        if (map.isSurroundedBy(getHome().getPosition(), DetailedVegetation.BUILDABLE_MOUNTAIN)) {
            return false;
        }

        return true;
    }

    @Override
    public void goToOtherStorage(Building building) throws InvalidRouteException {
        state = State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
