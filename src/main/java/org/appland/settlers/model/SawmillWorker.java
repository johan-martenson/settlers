/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.SAWMILL_WORKER;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.SawmillWorker.State.CUTTING_WOOD;
import static org.appland.settlers.model.SawmillWorker.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.SawmillWorker.State.GOING_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.SawmillWorker.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.SawmillWorker.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.SawmillWorker.State.WAITING_FOR_SPACE_ON_FLAG;
import static org.appland.settlers.model.SawmillWorker.State.WALKING_TO_TARGET;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class SawmillWorker extends Worker {
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;
    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME    = 99;

    private State state;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        CUTTING_WOOD,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_SPACE_ON_FLAG, GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE, GOING_TO_DIE, DEAD, RETURNING_TO_STORAGE
    }

    public SawmillWorker(Player player, GameMap map) {
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
                state = CUTTING_WOOD;
                countdown.countFrom(PRODUCTION_TIME);

                productivityMeasurer.nextProductivityCycle();
            } else {
                countdown.step();
            }
        } else if (state == CUTTING_WOOD) {
            if (getHome().getAmount(WOOD) > 0 && getHome().isProductionEnabled()) {
                if (countdown.hasReachedZero()) {

                    /* Consume the spent wood */
                    getHome().consumeOne(WOOD);

                    /* Report the production */
                    productivityMeasurer.reportProductivity();

                    /* Handle transportation */
                    if (getHome().getFlag().hasPlaceForMoreCargo()) {
                        Cargo cargo = new Cargo(PLANK, map);

                        setCargo(cargo);
                        /* Go out to delivery the plank to the flag */
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

                /* Report the that the sawmill worker was unproductive */
                productivityMeasurer.reportUnproductivity();
            }
        } else if (state == WAITING_FOR_SPACE_ON_FLAG) {
            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                Cargo cargo = new Cargo(PLANK, map);

                setCargo(cargo);
                /* Go out to delivery the plank to the flag */
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
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, SAWMILL_WORKER);

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
        return "Sawmill worker " + state;
    }

    @Override
    protected void onReturnToStorage() throws Exception, InvalidRouteException {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, SAWMILL_WORKER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), SAWMILL_WORKER);

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

            /* Don't try to enter the sawmill upon arrival */
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
