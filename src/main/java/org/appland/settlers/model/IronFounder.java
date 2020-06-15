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
import static org.appland.settlers.model.IronFounder.State.WAITING_FOR_SPACE_ON_FLAG;
import static org.appland.settlers.model.IronFounder.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.IRON_BAR;
import static org.appland.settlers.model.Material.IRON_FOUNDER;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class IronFounder extends Worker {
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
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
        WAITING_FOR_SPACE_ON_FLAG, GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE, GOING_TO_DIE, DEAD, RETURNING_TO_STORAGE
    }

    private State state;

    public IronFounder(Player player, GameMap map) {
        super(player, map);

        countdown = new Countdown();
        state = WALKING_TO_TARGET;

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME);
    }

    @Override
    protected void onEnterBuilding(Building building) {
        if (building instanceof Sawmill) {
            setHome(building);
        }

        state = RESTING_IN_HOUSE;
        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.hasReachedZero()) {
                state = MELTING_IRON;
                countdown.countFrom(PRODUCTION_TIME);
            } else {
                countdown.step();
            }
        } else if (state == MELTING_IRON) {
            if (getHome().getAmount(COAL) > 0 && getHome().getAmount(IRON) > 0 && getHome().isProductionEnabled()) {
                if (countdown.hasReachedZero()) {

                    /* Consume the resources */
                    getHome().consumeOne(COAL);
                    getHome().consumeOne(IRON);

                    /* Report that the iron founder produced */
                    productivityMeasurer.reportProductivity();
                    productivityMeasurer.nextProductivityCycle();

                    /* Handle the transportation */
                    if (getHome().getFlag().hasPlaceForMoreCargo()) {

                        Cargo cargo = new Cargo(IRON_BAR, map);

                        setCargo(cargo);

                        /* Go out to the flag to deliver the iron bar */
                        state = GOING_TO_FLAG_WITH_CARGO;

                        setTarget(getHome().getFlag().getPosition());

                        getHome().getFlag().promiseCargo();
                    } else {
                        state = WAITING_FOR_SPACE_ON_FLAG;
                    }

                } else {
                    countdown.step();
                }
            } else {

                /* Report that the iron founder lacked resources and couldn't work */
                productivityMeasurer.reportUnproductivity();
            }
        } else if (state == WAITING_FOR_SPACE_ON_FLAG) {
            if (getHome().getFlag().hasPlaceForMoreCargo()) {

                Cargo cargo = new Cargo(IRON_BAR, map);

                setCargo(cargo);

                /* Go out to the flag to deliver the iron bar */
                state = GOING_TO_FLAG_WITH_CARGO;

                setTarget(getHome().getFlag().getPosition());

                getHome().getFlag().promiseCargo();
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
    protected void onArrival() throws Exception, InvalidRouteException {
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
        } else if (state == State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, IRON_FOUNDER);

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
        return "Iron founder " + state;
    }

    @Override
    protected void onReturnToStorage() throws Exception, InvalidRouteException {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, IRON_FOUNDER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), IRON_FOUNDER);

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
    protected void onWalkingAndAtFixedPoint() throws Exception {

        /* Return to storage if the planned path no longer exists */
        if (state == WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter the iron foundry upon arrival */
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

    @Override
    public void goToOtherStorage(Building building) throws Exception {
        state = State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
