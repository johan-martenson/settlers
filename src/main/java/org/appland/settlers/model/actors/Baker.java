/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Baker.State.*;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Baker extends Worker {
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
    private static final int PRODUCTION_TIME = 49;
    private static final int RESTING_TIME = 99;

    private final Countdown countdown = new Countdown();
    private final ProductivityMeasurer productivityMeasurer;

    private State state;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        BAKING_BREAD,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        RETURNING_TO_STORAGE
    }

    public Baker(Player player, GameMap map) {
        super(player, map);

        state = State.WALKING_TO_TARGET;

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME, null);
    }

    @Override
    protected void onEnterBuilding(Building building) {
        if (building.isReady()) {
            state = RESTING_IN_HOUSE;
            countdown.countFrom(RESTING_TIME);

            productivityMeasurer.setBuilding(building);
        } else {
            state = State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

            setOffroadTarget(building.getPosition().downRight());
        }
    }

    @Override
    protected void onIdle() {
        switch (state) {
            case RESTING_IN_HOUSE -> {
                if (countdown.hasReachedZero()) {
                    state = BAKING_BREAD;
                    countdown.countFrom(PRODUCTION_TIME);

                    productivityMeasurer.nextProductivityCycle();
                } else {
                    countdown.step();
                }
            }
            case WAITING_FOR_SPACE_ON_FLAG -> {
                if (getHome().getFlag().hasPlaceForMoreCargo()) {
                    Cargo cargo = new Cargo(BREAD, map);

                    setCargo(cargo);

                    /* Go out to the flag to deliver the bread */
                    state = State.GOING_TO_FLAG_WITH_CARGO;

                    setTarget(getHome().getFlag().getPosition());

                    getHome().getFlag().promiseCargo(getCargo());
                }
            }
            case BAKING_BREAD -> {
                if (getHome().getAmount(WATER) > 0 && getHome().getAmount(FLOUR) > 0 && getHome().isProductionEnabled()) {
                    if (countdown.hasReachedZero()) {

                        /* Consume the ingredients */
                        getHome().consumeOne(WATER);
                        getHome().consumeOne(FLOUR);

                        /* Report production of the bread */
                        productivityMeasurer.reportProductivity();

                        map.getStatisticsManager().breadProduced(player, map.getTime());

                        /* Handle the transportation of the produced bread */
                        if (!getHome().getFlag().hasPlaceForMoreCargo()) {
                            state = WAITING_FOR_SPACE_ON_FLAG;
                        } else {
                            Cargo cargo = new Cargo(BREAD, map);

                            setCargo(cargo);

                            /* Go out to the flag to deliver the bread */
                            state = GOING_TO_FLAG_WITH_CARGO;

                            setTarget(getHome().getFlag().getPosition());

                            getHome().getFlag().promiseCargo(getCargo());
                        }
                    } else {
                        countdown.step();
                    }
                } else {

                    /* Report the that the baker was unproductive */
                    productivityMeasurer.reportUnproductivity();
                }
            }
            case DEAD -> {
                if (countdown.hasReachedZero()) {
                    map.removeWorker(this);
                } else {
                    countdown.step();
                }
            }
        }
    }

    private boolean isBreadReceiver(Building building) {
        if (building.isReady() && building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(BREAD);
        }

        if (building.isReady() && building.needsMaterial(BREAD)) {
            return true;
        }

        return false;
    }

    @Override
    protected void onArrival() {
        switch (state) {
            case GOING_TO_FLAG_WITH_CARGO -> {
                var cargo = getCargo();
                cargo.setPosition(getPosition());
                cargo.transportToReceivingBuilding(this::isBreadReceiver);

                var flag = map.getFlagAtPoint(getPosition());
                flag.putCargo(getCargo());

                setCargo(null);

                state = GOING_BACK_TO_HOUSE;

                returnHome();
            }
            case GOING_BACK_TO_HOUSE -> {
                enterBuilding(getHome());

                state = RESTING_IN_HOUSE;

                countdown.countFrom(RESTING_TIME);
            }
            case RETURNING_TO_STORAGE -> {
                var storehouse = (Storehouse) map.getBuildingAtPoint(getPosition());
                storehouse.depositWorker(this);
            }
            case GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE -> {
                /* Go to the closest storage */
                Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, BAKER);

                if (storehouse != null) {
                    state = RETURNING_TO_STORAGE;

                    setTarget(storehouse.getPosition());
                } else {
                    state = GOING_TO_DIE;

                    Point point = findPlaceToDie();

                    setOffroadTarget(point);
                }
            }
            case GOING_TO_DIE -> {
                setDead();

                state = DEAD;

                countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
            }
        }
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, BAKER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), BAKER);

            if (storage != null) {
                state = RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            } else {
                Point point = findPlaceToDie();

                setOffroadTarget(point, getPosition().downRight());

                state = GOING_TO_DIE;
            }
        }
    }

    @Override
    public String toString() {
        return isExactlyAtPoint()
                ? "Baker " + getPosition()
                : "Baker " + getPosition() + " - " + getNextPoint();
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {

        /* Return to storage if the planned path no longer exists */
        if (state == WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter upon arrival */
            clearTargetBuilding();

            /* Go back to the storage */
            returnToStorage();
        }
    }

    @Override
    public int getProductivity() {

        /* Measure productivity across the length of four rest-work periods */
        return (int)
                (((double)productivityMeasurer.getSumMeasured() /
                        (productivityMeasurer.getNumberOfCycles())) * 100);
    }

    @Override
    public void goToOtherStorage(Building building) {
        state = GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
