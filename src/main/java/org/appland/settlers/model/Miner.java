/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.MINER;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Miner.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Miner.State.GOING_OUT_TO_FLAG;
import static org.appland.settlers.model.Miner.State.MINING;
import static org.appland.settlers.model.Miner.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Miner.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Miner.State.WAITING_FOR_SPACE_ON_FLAG;
import static org.appland.settlers.model.Miner.State.WALKING_TO_TARGET;

/**
 *
 * @author johan
 */
@Walker (speed = 10)
public class Miner extends Worker {
    private final static int RESTING_TIME = 99;
    private final static int TIME_TO_MINE = 49;
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
        WAITING_FOR_SPACE_ON_FLAG, GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE, DEAD, GOING_TO_DIE, NO_MORE_RESOURCES
    }

    public Miner(Player player, GameMap map) {
        super(player, map);

        mineral = null;

        countdown = new Countdown();

        state = WALKING_TO_TARGET;

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + TIME_TO_MINE);
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
        if (building.isMine()) {
            setHome(building);
        }

        if (building instanceof GoldMine) {
            mineral = GOLD;
        } else if (building instanceof IronMine) {
            mineral = IRON;
        } else if (building instanceof CoalMine) {
            mineral = COAL;
        } else if (building instanceof GraniteMine) {
            mineral = STONE;
        }

        state = RESTING_IN_HOUSE;

        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                if (hasFood()) {

                    /* Start mining when the rest is over and there is food available */
                    state = MINING;
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
        } else if (state == MINING) {
            if (countdown.reachedZero() && getHome().isProductionEnabled()) {
                if (map.getAmountOfMineralAtPoint(mineral, getPosition()) > 0) {
                    consumeFood();

                    /* Report the production */
                    productivityMeasurer.reportProductivity();

                    /* Handle transportation */
                    if (getHome().getFlag().hasPlaceForMoreCargo()) {
                        Cargo cargo = map.mineMineralAtPoint(mineral, getPosition());

                        setCargo(cargo);

                        /* Go out to delivery the cargo to the flag */
                        setTarget(getHome().getFlag().getPosition());

                        state = GOING_OUT_TO_FLAG;

                        getHome().getFlag().promiseCargo();
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
        } else if (state == WAITING_FOR_SPACE_ON_FLAG) {
            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                Cargo cargo = map.mineMineralAtPoint(mineral, getPosition());

                setCargo(cargo);

                /* Go out to delivery the cargo to the flag */
                setTarget(getHome().getFlag().getPosition());

                state = GOING_OUT_TO_FLAG;

                getHome().getFlag().promiseCargo();
            }
        } else if (state == State.DEAD) {
            if (countdown.reachedZero()) {
                map.removeWorker(this);
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == GOING_OUT_TO_FLAG) {
            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToStorage();
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
                state = State.RETURNING_TO_STORAGE;

                setTarget(storehouse.getPosition());
            } else {
                state = State.GOING_TO_DIE;

                Point point = super.findPlaceToDie();

                setOffroadTarget(point);
            }
        } else if (state == State.GOING_TO_DIE) {
            super.setDead();

            state = State.DEAD;

            countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
        }
    }

    private boolean hasFood() {
        Building home = getHome();

        return home.getAmount(BREAD) > 0 || home.getAmount(FISH)  > 0 || home.getAmount(MEAT)  > 0;
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, MINER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), MINER);

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
    protected void onWalkingAndAtFixedPoint() throws Exception {

        /* Return to storage if the planned path no longer exists */
        if (state == State.WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter the mine upon arrival */
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
        state = State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
