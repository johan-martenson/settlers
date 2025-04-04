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
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Miner.State.*;

/**
 *
 * @author johan
 */
@Walker (speed = 10)
public class Miner extends Worker {
    private static final int RESTING_TIME = 99;
    private static final int TIME_TO_MINE = 49;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;

    private Material mineral;
    private State state;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        MINING,
        GOING_OUT_TO_FLAG,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        DEAD,
        GOING_TO_DIE,
        NO_MORE_RESOURCES
    }

    public Miner(Player player, GameMap map) {
        super(player, map);

        mineral = null;

        countdown = new Countdown();

        state = WALKING_TO_TARGET;

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + TIME_TO_MINE, null);
    }

    public boolean isMining() {
        return state == MINING;
    }

    private void consumeFood() {
        Building home = getHome();

        if (home.getAmount(BREAD) > 0) {
            home.consumeOne(BREAD);
        } else if (home.getAmount(FISH) > 0) {
            home.consumeOne(FISH);
        } else if (home.getAmount(MEAT) > 0) {
            home.consumeOne(MEAT);
        }
    }

    @Override
    protected void onEnterBuilding(Building building) {
        mineral = building.getProducedMaterial()[0];

        state = RESTING_IN_HOUSE;

        countdown.countFrom(RESTING_TIME);

        productivityMeasurer.setBuilding(building);
    }

    @Override
    protected void onIdle() {
        switch (state) {
            case RESTING_IN_HOUSE -> {
                if (countdown.hasReachedZero()) {
                    if (hasFood()) {
                        state = State.MINING;
                        countdown.countFrom(TIME_TO_MINE);

                        /* Note that the next production cycle starts */
                        productivityMeasurer.nextProductivityCycle();
                    } else {

                        /* Report that the miner wasn't able to start mining because of missing food */
                        productivityMeasurer.reportUnproductivity();
                    }
                } else {
                    countdown.step();
                }
            }
            case MINING -> {
                if (countdown.hasReachedZero() && getHome().isProductionEnabled()) {
                    if (map.getAmountOfMineralAtPoint(mineral, getPosition()) > 0) {
                        consumeFood();

                        /* Report the production */
                        productivityMeasurer.reportProductivity();

                        map.getStatisticsManager().mined(player, map.getTime(), mineral);

                        /* Handle transportation */
                        if (getHome().getFlag().hasPlaceForMoreCargo()) {
                            Cargo cargo = map.mineMineralAtPoint(mineral, getPosition());

                            setCargo(cargo);

                            /* Go out to delivery the cargo to the flag */
                            setTarget(getHome().getFlag().getPosition());

                            state = State.GOING_OUT_TO_FLAG;

                            getHome().getFlag().promiseCargo(getCargo());
                        } else {
                            state = State.WAITING_FOR_SPACE_ON_FLAG;
                        }
                    } else {

                        /* Report that there is no more ore available in the mine */
                        getHome().reportNoMoreNaturalResources();

                        getPlayer().reportNoMoreResourcesForBuilding(getHome());

                        state = State.NO_MORE_RESOURCES;
                    }
                } else if (getHome().isProductionEnabled()) {
                    countdown.step();
                }
            }
            case WAITING_FOR_SPACE_ON_FLAG -> {
                if (getHome().getFlag().hasPlaceForMoreCargo()) {
                    Cargo cargo = map.mineMineralAtPoint(mineral, getPosition());

                    setCargo(cargo);

                    /* Go out to delivery the cargo to the flag */
                    setTarget(getHome().getFlag().getPosition());

                    state = State.GOING_OUT_TO_FLAG;

                    getHome().getFlag().promiseCargo(getCargo());
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

    private boolean isOreReceiver(Building building) {
        if (building.isReady() && building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(mineral);
        }

        if (mineral == STONE && building.needsMaterial(STONE)) {
            return true;
        }

        if (building.isReady() && building.needsMaterial(mineral)) {
            return true;
        }

        return false;
    }

    @Override
    protected void onArrival() {
        if (state == GOING_OUT_TO_FLAG) {
            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToReceivingBuilding(this::isOreReceiver);
            getHome().getFlag().putCargo(cargo);

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
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, MINER);

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

    private boolean hasFood() {
        Building home = getHome();

        return home.getAmount(BREAD) > 0 || home.getAmount(FISH)  > 0 || home.getAmount(MEAT)  > 0;
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, MINER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), MINER);

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
    protected void onWalkingAndAtFixedPoint() {

        /* Return to storage if the planned path no longer exists */
        if (state == WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter the mine upon arrival */
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
        state = State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
