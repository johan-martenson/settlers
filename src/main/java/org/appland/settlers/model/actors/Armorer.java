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

import java.util.Objects;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Armorer.State.*;

/**
 * @author johan
 */
@Walker(speed = 10)
public class Armorer extends Worker {
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
    private static final int PRODUCTION_TIME = 49;
    private static final int RESTING_TIME = 99;

    private final Countdown countdown = new Countdown();
    private final ProductivityMeasurer productivityMeasurer;

    private Material nextWeapon = SWORD;
    private State state = WALKING_TO_TARGET;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        PRODUCING_WEAPON,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        RETURNING_TO_STORAGE
    }

    public Armorer(Player player, GameMap map) {
        super(player, map);

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME, null);
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
        state = RESTING_IN_HOUSE;
        countdown.countFrom(RESTING_TIME);

        productivityMeasurer.setBuilding(building);
    }

    @Override
    protected void onIdle() {
        switch (state) {
            case RESTING_IN_HOUSE -> {
                if (countdown.hasReachedZero()) {
                    state = PRODUCING_WEAPON;
                    countdown.countFrom(PRODUCTION_TIME);

                    productivityMeasurer.nextProductivityCycle();
                } else {
                    countdown.step();
                }
            }
            case WAITING_FOR_SPACE_ON_FLAG -> {
                if (getHome().getFlag().hasPlaceForMoreCargo()) {
                    var cargo = new Cargo(nextWeapon, map);

                    setCargo(cargo);

                    nextWeapon = getNextWeapon(nextWeapon);

                    state = GOING_TO_FLAG_WITH_CARGO;

                    setTarget(getHome().getFlag().getPosition());

                    getHome().getFlag().promiseCargo(getCargo());
                }
            }
            case PRODUCING_WEAPON -> {
                if (getHome().getAmount(IRON_BAR) > 0 && getHome().getAmount(COAL) > 0 && getHome().isProductionEnabled()) {
                    if (countdown.hasReachedZero()) {

                        // Produce the weapon
                        getHome().consumeOne(IRON_BAR);
                        getHome().consumeOne(COAL);

                        map.getStatisticsManager().weaponProduced(player, map.getTime());

                        // Handle transportation
                        if (!getHome().getFlag().hasPlaceForMoreCargo()) {
                            state = WAITING_FOR_SPACE_ON_FLAG;
                        } else {
                            var cargo = new Cargo(nextWeapon, map);

                            setCargo(cargo);

                            nextWeapon = getNextWeapon(nextWeapon);

                            state = GOING_TO_FLAG_WITH_CARGO;

                            setTarget(getHome().getFlag().getPosition());

                            getHome().getFlag().promiseCargo(getCargo());
                        }
                    } else {
                        countdown.step();

                        // Count this as a productive step
                        productivityMeasurer.reportProductivity();
                    }
                } else {
                    productivityMeasurer.reportUnproductivity();

                    productivityMeasurer.nextProductivityCycle();
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

    @Override
    protected void onArrival() {
        switch (state) {
            case GOING_TO_FLAG_WITH_CARGO -> {
                var flag = map.getFlagAtPoint(getPosition());
                var cargo = getCargo();

                cargo.setPosition(getPosition());
                cargo.transportToStorage();

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
                var storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, ARMORER);

                if (Objects.nonNull(storehouse)) {
                    state = RETURNING_TO_STORAGE;

                    setTarget(storehouse.getPosition());
                } else {
                    state = GOING_TO_DIE;
                    setOffroadTarget(findPlaceToDie());
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
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, ARMORER);

        if (Objects.nonNull(storage)) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), ARMORER);

            if (Objects.nonNull(storage)) {
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
                ? String.format("Armorer %s", getPosition())
                : String.format("Armorer %s - %s", getPosition(), getNextPoint());
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {

        // Return to storage if the planned path no longer exists
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
    public int getProductivity() {

        // Measure productivity across the length of four rest-work periods
        return (int)
                (((double) productivityMeasurer.getSumMeasured() / (4 * PRODUCTION_TIME)) * 100);
    }

    @Override
    public void goToOtherStorage(Building building) {
        state = GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
