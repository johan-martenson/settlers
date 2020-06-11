/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Brewer.State.BREWING_BEER;
import static org.appland.settlers.model.Brewer.State.DEAD;
import static org.appland.settlers.model.Brewer.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Brewer.State.GOING_TO_DIE;
import static org.appland.settlers.model.Brewer.State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;
import static org.appland.settlers.model.Brewer.State.GOING_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.Brewer.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Brewer.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Brewer.State.WAITING_FOR_SPACE_ON_FLAG;
import static org.appland.settlers.model.Brewer.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.BREWER;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Brewer extends Worker {
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;
    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME    = 99;

    private State state;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        BREWING_BEER,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_SPACE_ON_FLAG, GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE, GOING_TO_DIE, DEAD, RETURNING_TO_STORAGE
    }

    public Brewer(Player player, GameMap map) {
        super(player, map);

        countdown = new Countdown();
        state = WALKING_TO_TARGET;

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME);
    }

    @Override
    protected void onEnterBuilding(Building building) {
        if (building instanceof Brewery) {
            setHome(building);
        }

        state = RESTING_IN_HOUSE;
        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                state = BREWING_BEER;
                countdown.countFrom(PRODUCTION_TIME);

                productivityMeasurer.nextProductivityCycle();
            } else {
                countdown.step();
            }
        } else if (state == WAITING_FOR_SPACE_ON_FLAG) {

            if (!getHome().getFlag().hasNoPlaceForMoreCargo()) {
                Cargo cargo = new Cargo(BEER, map);

                setCargo(cargo);

                /* Go place the beer at the flag */
                state = GOING_TO_FLAG_WITH_CARGO;

                setTarget(getHome().getFlag().getPosition());

                getHome().getFlag().promiseCargo();
            }

        } else if (state == BREWING_BEER) {
            if (getHome().getAmount(WATER) > 0 && getHome().getAmount(WHEAT) > 0 && getHome().isProductionEnabled()) {
                if (countdown.reachedZero()) {

                    /* Consume the ingredients */
                    getHome().consumeOne(WATER);
                    getHome().consumeOne(WHEAT);

                    /* Report the production */
                    productivityMeasurer.reportProductivity();

                    /* Handle transportation of the produced beer */
                    if (getHome().getFlag().hasNoPlaceForMoreCargo()) {
                        state = Brewer.State.WAITING_FOR_SPACE_ON_FLAG;
                    } else {
                        Cargo cargo = new Cargo(BEER, map);

                        setCargo(cargo);

                        /* Go place the beer at the flag */
                        state = GOING_TO_FLAG_WITH_CARGO;

                        setTarget(getHome().getFlag().getPosition());

                        getHome().getFlag().promiseCargo();
                    }
                } else {
                    countdown.step();
                }
            } else {

                /* Report the that the brewer was unproductive */
                productivityMeasurer.reportUnproductivity();
            }
        } else if (state == DEAD) {
            if (countdown.reachedZero()) {
                map.removeWorker(this);
            } else {
                countdown.step();
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
        } else if (state == Brewer.State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, BREWER);

            if (storehouse != null) {

                state = RETURNING_TO_STORAGE;

                setTarget(storehouse.getPosition());
            } else {
                state = GOING_TO_DIE;

                Point point = super.findPlaceToDie();

                setOffroadTarget(point);
            }
        } else if (state == GOING_TO_DIE) {
            super.setDead();

            state = DEAD;

            countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
        }
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, BREWER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), BREWER);

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            } else {
                Point point = findPlaceToDie();

                setOffroadTarget(point, getPosition().downRight());

                state = GOING_TO_DIE;
            }
        }
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

    @Override
    public void goToOtherStorage(Building building) throws Exception {
        state = GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
