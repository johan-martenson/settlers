package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
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
        return current == SWORD
                ? SHIELD
                : SWORD;
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
                    if (home.getAmount(IRON_BAR) > 0 && home.getAmount(COAL) > 0) {
                        state = PRODUCING_WEAPON;
                        countdown.countFrom(PRODUCTION_TIME);

                        player.reportChangedBuilding(home);
                    }

                    productivityMeasurer.nextProductivityCycle();
                } else {
                    countdown.step();
                }
            }
            case WAITING_FOR_SPACE_ON_FLAG -> {
                if (home.getFlag().hasPlaceForMoreCargo()) {
                    setCargo(new Cargo(nextWeapon, map));
                    home.getFlag().promiseCargo(carriedCargo);

                    nextWeapon = getNextWeapon(nextWeapon);

                    state = GOING_TO_FLAG_WITH_CARGO;
                    setTarget(home.getFlag().getPosition());
                }
            }
            case PRODUCING_WEAPON -> {
                if (home.getAmount(IRON_BAR) > 0 && home.getAmount(COAL) > 0 && home.isProductionEnabled()) {
                    if (countdown.hasReachedZero()) {

                        // Produce the weapon
                        home.consumeOne(IRON_BAR);
                        home.consumeOne(COAL);

                        map.getStatisticsManager().weaponProduced(player, map.getTime());

                        player.reportChangedBuilding(home);

                        // Handle transportation
                        if (!home.getFlag().hasPlaceForMoreCargo()) {
                            state = WAITING_FOR_SPACE_ON_FLAG;
                        } else {
                            setCargo(new Cargo(nextWeapon, map));
                            home.getFlag().promiseCargo(carriedCargo);

                            nextWeapon = getNextWeapon(nextWeapon);

                            state = GOING_TO_FLAG_WITH_CARGO;
                            setTarget(home.getFlag().getPosition());
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
                var flag = map.getFlagAtPoint(position);

                carriedCargo.setPosition(position);
                carriedCargo.transportToStorage();

                flag.putCargo(carriedCargo);

                carriedCargo = null;

                state = GOING_BACK_TO_HOUSE;
                returnHome();
            }
            case GOING_BACK_TO_HOUSE -> {
                enterBuilding(home);

                state = RESTING_IN_HOUSE;
                countdown.countFrom(RESTING_TIME);
            }
            case RETURNING_TO_STORAGE -> {
                var storehouse = (Storehouse) map.getBuildingAtPoint(position);
                storehouse.depositWorker(this);
            }
            case GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE -> {
                // Go to the closest storage.
                var storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, ARMORER);

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
        var storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, ARMORER);

        if (Objects.nonNull(storage)) {
            state = RETURNING_TO_STORAGE;
            setTarget(storage.getPosition());
        } else {
            storage = (Storehouse) GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, player, ARMORER);

            if (Objects.nonNull(storage)) {
                state = RETURNING_TO_STORAGE;
                setOffroadTarget(storage.getPosition());
            } else {
                state = GOING_TO_DIE;
                setOffroadTarget(findPlaceToDie(), position.downRight());
            }
        }
    }

    @Override
    public String toString() {
        return isExactlyAtPoint()
                ? String.format("Armorer %s", position)
                : String.format("Armorer %s - %s", position, getNextPoint());
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {

        // Return to storage if the planned path no longer exists
        if (state == WALKING_TO_TARGET && map.isFlagAtPoint(position) && !map.arePointsConnectedByRoads(position, target)) {

            // Don't try to enter the armory upon arrival.
            clearTargetBuilding();

            // Go back to the storage
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

    @Override
    public boolean isWorking() {
        return state == State.PRODUCING_WEAPON;
    }
}
