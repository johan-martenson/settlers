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
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;

import static java.lang.String.format;
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
    private final ProductivityMeasurer productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME, null);

    private State state = State.WALKING_TO_TARGET;

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
                if (home.getFlag().hasPlaceForMoreCargo()) {
                    var cargo = new Cargo(BREAD, map);
                    setCargo(cargo);
                    home.getFlag().promiseCargo(getCargo());

                    state = State.GOING_TO_FLAG_WITH_CARGO;
                    setTarget(home.getFlag().getPosition());

                }
            }
            case BAKING_BREAD -> {
                if (home.getAmount(WATER) > 0 && home.getAmount(FLOUR) > 0 && home.isProductionEnabled()) {
                    if (countdown.hasReachedZero()) {
                        home.consumeOne(WATER);
                        home.consumeOne(FLOUR);
                        productivityMeasurer.reportProductivity();
                        map.getStatisticsManager().breadProduced(player, map.getTime());

                        if (!home.getFlag().hasPlaceForMoreCargo()) {
                            state = WAITING_FOR_SPACE_ON_FLAG;
                        } else {
                            var cargo = new Cargo(BREAD, map);
                            setCargo(cargo);
                            home.getFlag().promiseCargo(getCargo());

                            state = GOING_TO_FLAG_WITH_CARGO;
                            setTarget(home.getFlag().getPosition());

                        }
                    } else {
                        countdown.step();
                    }
                } else {
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
        if (!building.isReady()) {
            return false;
        }

        if (building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(BREAD);
        }

        return building.needsMaterial(BREAD);
    }

    @Override
    protected void onArrival() {
        switch (state) {
            case GOING_TO_FLAG_WITH_CARGO -> {
                carriedCargo.setPosition(position);
                carriedCargo.transportToReceivingBuilding(this::isBreadReceiver);

                var flag = map.getFlagAtPoint(position);
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
                var storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, BAKER);

                if (storehouse != null) {
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
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, BAKER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;
            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, getPlayer(), BAKER);

            if (storage != null) {
                state = RETURNING_TO_STORAGE;
                setOffroadTarget(storage.getPosition());
            } else {
                setOffroadTarget(findPlaceToDie(), position.downRight());
                state = GOING_TO_DIE;
            }
        }
    }

    @Override
    public String toString() {
        return isExactlyAtPoint()
                ? format("Baker %s", position)
                : format("Baker %s - %s", position, getNextPoint());
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {
        if (state == WALKING_TO_TARGET &&
            map.isFlagAtPoint(position) &&
            !map.arePointsConnectedByRoads(position, getTarget())) {
            clearTargetBuilding();
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
