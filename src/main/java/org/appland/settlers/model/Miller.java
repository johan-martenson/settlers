/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.MILLER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Miller.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Miller.State.GOING_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.Miller.State.GRINDING_WHEAT;
import static org.appland.settlers.model.Miller.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Miller.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Miller.State.WAITING_FOR_SPACE_ON_FLAG;
import static org.appland.settlers.model.Miller.State.WALKING_TO_TARGET;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Miller extends Worker {
    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME = 99;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;

    private State state;

    public Miller(Player player, GameMap map) {
        super(player, map);
        countdown = new Countdown();
        state = WALKING_TO_TARGET;

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME);
    }

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GRINDING_WHEAT,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE, GOING_TO_DIE, DEAD, WAITING_FOR_SPACE_ON_FLAG
    }

    @Override
    protected void onEnterBuilding(Building building) {
        if (building instanceof Mill) {
            setHome(building);
        }

        state = RESTING_IN_HOUSE;

        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws InvalidRouteException {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.hasReachedZero()) {
                state = GRINDING_WHEAT;

                countdown.countFrom(PRODUCTION_TIME);
            } else {
                countdown.step();
            }
        } else if (state == WAITING_FOR_SPACE_ON_FLAG) {

            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                Cargo cargo = new Cargo(FLOUR, map);

                setCargo(cargo);

                getHome().getFlag().promiseCargo();

                state = GOING_TO_FLAG_WITH_CARGO;

                getHome().getFlag().promiseCargo();

                setTarget(getHome().getFlag().getPosition());
            }

        } else if (state == GRINDING_WHEAT) {
            if (getHome().getAmount(WHEAT) > 0 && getHome().isProductionEnabled()) {
                if (countdown.hasReachedZero()) {

                    /* Consume the wheat */
                    getHome().consumeOne(WHEAT);

                    /* Go out to the flag to deliver the flour */
                    if (getHome().getFlag().hasPlaceForMoreCargo()) {

                        Cargo cargo = new Cargo(FLOUR, map);

                        cargo.setPosition(getPosition());

                        setCargo(cargo);

                        setTarget(getHome().getFlag().getPosition());

                        state = GOING_TO_FLAG_WITH_CARGO;

                        getHome().getFlag().promiseCargo();

                    /* Wait for space on the flag if it's full */
                    } else {
                        state = WAITING_FOR_SPACE_ON_FLAG;
                    }

                    /* Report that the miller produced flour */
                    productivityMeasurer.reportProductivity();
                    productivityMeasurer.nextProductivityCycle();
                } else {
                    countdown.step();
                }
            } else {

                /* Report that the miller couldn't produce flour because it had no wheat */
                productivityMeasurer.reportUnproductivity();
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
        if (state == GOING_TO_FLAG_WITH_CARGO) {
            Flag flag = getHome().getFlag();

            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToStorage();

            flag.putCargo(getCargo());

            setCargo(null);

            returnHome();

            state = GOING_BACK_TO_HOUSE;
        } else if (state == GOING_BACK_TO_HOUSE) {
            enterBuilding(getHome());

            state = RESTING_IN_HOUSE;
            countdown.countFrom(RESTING_TIME);
        } else if (state == RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        } else if (state == State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, MILLER);

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
    protected void onReturnToStorage() throws InvalidRouteException {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, MILLER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), MILLER);

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
    protected void onWalkingAndAtFixedPoint() throws InvalidRouteException {

        /* Return to storage if the planned path no longer exists */
        if (state == WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter the mill upon arrival */
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
    public void goToOtherStorage(Building building) throws InvalidRouteException {
        state = State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
