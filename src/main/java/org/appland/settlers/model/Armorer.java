/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Armorer.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Armorer.State.GOING_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.Armorer.State.PRODUCING_WEAPON;
import static org.appland.settlers.model.Armorer.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Armorer.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Armorer.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.IRON_BAR;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.SWORD;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Armorer extends Worker {
    private final Countdown countdown;
    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME    = 99;
    private final ProductivityMeasurer productivityMeasurer;

    private Material nextWeapon = SWORD;
    private State    state;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        PRODUCING_WEAPON,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }


    public Armorer(Player player, GameMap map) {
        super(player, map);

        countdown = new Countdown();
        state = WALKING_TO_TARGET;

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME);
    }

    private Material getNextWeapon(Material current) {
        if (current == SWORD) {
            return SHIELD;
        } else {
            return SWORD;
        }
    }

    @Override
    protected void onEnterBuilding(Building building) {
        if (building instanceof Armory) {
            setHome(building);
        }

        state = RESTING_IN_HOUSE;
        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                state = PRODUCING_WEAPON;
                countdown.countFrom(PRODUCTION_TIME);

                productivityMeasurer.nextProductivityCycle();
            } else {
                countdown.step();
            }
        } else if (state == PRODUCING_WEAPON) {
            if (getHome().getAmount(IRON_BAR) > 0 && getHome().getAmount(COAL) > 0 && getHome().isProductionEnabled()) {

                if (countdown.reachedZero()) {
                    Cargo cargo = new Cargo(nextWeapon, map);

                    setCargo(cargo);

                    getHome().consumeOne(IRON_BAR);
                    getHome().consumeOne(COAL);

                    nextWeapon = getNextWeapon(nextWeapon);

                    state = GOING_TO_FLAG_WITH_CARGO;

                    setTarget(getHome().getFlag().getPosition());
                } else {
                    countdown.step();

                    /* Count this as a productive step */
                    productivityMeasurer.reportProductivity();
                }
            } else {

                productivityMeasurer.reportUnproductivity();

                productivityMeasurer.nextProductivityCycle();
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
    protected void onReturnToStorage() throws Exception {
        Building storage = GameUtils.getClosestStorageConnectedByRoads(getPosition(), getPlayer());

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroad(getPlayer(), getPosition());

            if (storage != null) {
                state = RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + state.name().toLowerCase();
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws Exception {

        /* Return to storage if the planned path no longer exists */
        if (state == WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter the armory upon arrival */
            clearTargetBuilding();

            /* Go back to the storage */
            returnToStorage();
        }
    }

    @Override
    int getProductivity() {

        /* Measure productivity across the length of four rest-work periods */
        return (int)
                (((double)productivityMeasurer.getSumMeasured() / (double)(4 * PRODUCTION_TIME)) * 100);
    }
}
