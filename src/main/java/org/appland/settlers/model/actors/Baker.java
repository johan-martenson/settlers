/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.Direction;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.OffroadOption;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;

import static java.lang.String.format;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.WorkerAction.OPEN_OVEN;
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
    private static final int TIME_FOR_BREAD_TO_BAKE = 29;
    private static final int TIME_TO_TAKE_BREAD_OUT_OF_OVEN = 29;
    private static final int TIME_TO_PUT_BREAD_INTO_OVEN = 29;
    private static final int TIME_TO_WAIT_IN_HOUSE_IN_BREAD = 29;

    private final Countdown countdown = new Countdown();
    private final ProductivityMeasurer productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME, null);

    private State state = State.WALKING_TO_TARGET;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_BAKE_BREAD,
        PUTTING_BREAD_INTO_OVEN,
        WAITING_FOR_BREAD_TO_BAKE,
        TAKING_BREAD_OUT_OF_OVEN,
        GOING_BACK_TO_HOUSE_WITH_BREAD,
        WAITING_IN_HOUSE_WITH_BREAD,
        GOING_TO_FLAG_WITH_BREAD,
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
        if (countdown.isActive() && !countdown.hasReachedZero()) {
            countdown.step();
        } else {
            switch (state) {
                case RESTING_IN_HOUSE -> {
                    if (countdown.hasReachedZero()) {
                        if (home.has(WATER, FLOUR) && home.isProductionEnabled() && home.getFlag().hasPlaceForMoreCargo()) {
                            state = GOING_OUT_TO_BAKE_BREAD;
                            countdown.countFrom(PRODUCTION_TIME);
                            productivityMeasurer.nextProductivityCycle();
                            walkHalfWayOffroadTo(home.getPosition().downLeft(), OffroadOption.DONT_WALK_VIA_FLAG);
                        } else {
                            productivityMeasurer.reportUnproductivity();
                        }
                    }
                }

                case WAITING_FOR_SPACE_ON_FLAG -> {
                    if (home.getFlag().hasPlaceForMoreCargo()) {
                        var cargo = new Cargo(BREAD, map);
                        setCargo(cargo);
                        home.getFlag().promiseCargo(getCargo());

                        state = State.GOING_TO_FLAG_WITH_BREAD;
                        setTarget(home.getFlag().getPosition());
                    }
                }

                case PUTTING_BREAD_INTO_OVEN -> {
                    if (countdown.hasReachedZero()) {
                        direction = Direction.LEFT;
                        state = WAITING_FOR_BREAD_TO_BAKE;
                        countdown.countFrom(TIME_FOR_BREAD_TO_BAKE);
                    }
                }

                case WAITING_FOR_BREAD_TO_BAKE -> {
                    if (countdown.hasReachedZero()) {
                        state = TAKING_BREAD_OUT_OF_OVEN;
                        countdown.countFrom(TIME_TO_TAKE_BREAD_OUT_OF_OVEN);
                        doAction(OPEN_OVEN);
                        map.reportChangedBuilding(home);
                    }
                }

                case TAKING_BREAD_OUT_OF_OVEN -> {
                    if (countdown.hasReachedZero()) {
                        home.consume(WATER, FLOUR);
                        setCargo(new Cargo(BREAD, map));
                        productivityMeasurer.reportProductivity();
                        map.getStatisticsManager().breadProduced(player, map.getTime());

                        state = GOING_BACK_TO_HOUSE_WITH_BREAD;
                        returnToFixedPoint();
                    }
                }

                case WAITING_IN_HOUSE_WITH_BREAD -> {
                    if (countdown.hasReachedZero()) {
                        if (home.getFlag().hasPlaceForMoreCargo()) {
                            home.getFlag().promiseCargo(carriedCargo);

                            state = GOING_TO_FLAG_WITH_BREAD;
                            setTarget(home.getFlag().getPosition());

                        }
                    }
                }

                case DEAD -> {
                    if (countdown.hasReachedZero()) {
                        map.removeWorker(this);
                    }
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
    void onWalkedHalfWay() {
        if (state == GOING_OUT_TO_BAKE_BREAD) {
            state = PUTTING_BREAD_INTO_OVEN;
            countdown.countFrom(TIME_TO_PUT_BREAD_INTO_OVEN);
            doAction(OPEN_OVEN);
        }
    }

    @Override
    protected void onArrival() {
        switch (state) {
            case GOING_BACK_TO_HOUSE_WITH_BREAD -> {
                state = WAITING_IN_HOUSE_WITH_BREAD;
                countdown.countFrom(TIME_TO_WAIT_IN_HOUSE_IN_BREAD);

                goInside();
            }

            case GOING_TO_FLAG_WITH_BREAD -> {
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
                ? format("Baker %s (%s)", position, state)
                : format("Baker %s - %s (%s)", position, getNextPoint(), state);
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {
        if (state == WALKING_TO_TARGET && map.isFlagAtPoint(position) && !map.arePointsConnectedByRoads(position, getTarget())) {
            clearTargetBuilding();
            returnToStorage();
        }
    }

    @Override
    public int getProductivity() {

        // Measure productivity across the length of four rest-work periods
        return (int)
                (((double)productivityMeasurer.getSumMeasured() /
                        (productivityMeasurer.getNumberOfCycles())) * 100);
    }

    @Override
    public void goToOtherStorage(Building building) {
        state = GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;
        setTarget(building.getFlag().getPosition());
    }

    @Override
    public boolean isWorking() {
        return state == GOING_OUT_TO_BAKE_BREAD ||
                state == PUTTING_BREAD_INTO_OVEN ||
                state == WAITING_FOR_BREAD_TO_BAKE;
    }

    public boolean isPuttingDoughIntoOven() {
        return state == PUTTING_BREAD_INTO_OVEN;
    }

    public boolean isTakingBreadOutOfOven() {
        return state == TAKING_BREAD_OUT_OF_OVEN;
    }
}
