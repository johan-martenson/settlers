package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.WorkerAction;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.PigFarm;
import org.appland.settlers.model.buildings.Storehouse;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.PigBreeder.State.*;

/**
 * @author johan
 */
@Walker(speed = 10)
public class
PigBreeder extends Worker {
    private static final int TIME_TO_REST = 99;
    private static final int TIME_TO_FEED = 19;
    private static final int TIME_TO_PREPARE_PIG = 19;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    public State state = State.WALKING_TO_TARGET;

    private final Countdown countdown = new Countdown();
    private final org.appland.settlers.model.utils.ProductivityMeasurer productivityMeasurer = new org.appland.settlers.model.utils.ProductivityMeasurer();

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_FEED,
        FEEDING,
        GOING_BACK_TO_HOUSE_AFTER_FEEDING,
        PREPARING_PIG_FOR_DELIVERY,
        GOING_BACK_TO_HOUSE,
        GOING_OUT_TO_PUT_CARGO,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        RESTING_BEFORE_STARTING_FIRST_PRODUCTION_CYCLE,
        WAITING_FOR_RESOURCES,
        RETURNING_TO_STORAGE
    }

    public PigBreeder(Player player, GameMap map) {
        super(player, map);
    }

    public boolean isFeeding() {
        return state == FEEDING;
    }

    @Override
    protected void onEnterBuilding(Building building) {
        if (state == WALKING_TO_TARGET) {
            productivityMeasurer.reset();
            state = State.RESTING_BEFORE_STARTING_FIRST_PRODUCTION_CYCLE;
        } else {
            state = RESTING_IN_HOUSE;
        }

        countdown.countFrom(TIME_TO_REST);
    }

    @Override
    public void stepTime() throws InvalidUserActionException {
        super.stepTime();

        productivityMeasurer.reportProductivity(
                state == RESTING_IN_HOUSE ||
                state == GOING_OUT_TO_FEED ||
                state == FEEDING ||
                state == GOING_BACK_TO_HOUSE_AFTER_FEEDING ||
                state == PREPARING_PIG_FOR_DELIVERY ||
                state == GOING_OUT_TO_PUT_CARGO ||
                state == GOING_BACK_TO_HOUSE
        );

        if (home instanceof PigFarm pigFarm) {
            pigFarm.setNumberOfPigs(productivityToNumberOfPigs());
        }
    }

    private int productivityToNumberOfPigs() {
        var productivity = productivityMeasurer.productivity();

        if (productivity < 20) {
            return 1;
        } else if (productivity < 40) {
            return 2;
        } else if (productivity < 60) {
            return 3;
        } else if (productivity < 80) {
            return 4;
        } else {
            return 5;
        }
    }

    @Override
    protected void onIdle() {
        switch (state) {
            case RESTING_IN_HOUSE, RESTING_BEFORE_STARTING_FIRST_PRODUCTION_CYCLE -> {
                if (countdown.hasReachedZero() && home.isProductionEnabled()) {
                    if (home.getAmount(WATER) > 0 && home.getAmount(WHEAT) > 0) {
                        state = State.GOING_OUT_TO_FEED;
                        setOffroadTarget(home.getPosition().downRight());
                    } else {
                        state = WAITING_FOR_RESOURCES;
                    }
                } else if (home.isProductionEnabled()) {
                    countdown.step();
                }
            }

            case WAITING_FOR_RESOURCES -> {
                if (home.isProductionEnabled() && home.getAmount(WATER) > 0 && home.getAmount(WHEAT) > 0) {
                    state = GOING_OUT_TO_FEED;
                    setOffroadTarget(home.getPosition().downRight());
                }
            }

            case FEEDING -> {
                if (countdown.hasReachedZero()) {
                    home.consumeOne(WATER);
                    home.consumeOne(WHEAT);

                    state = GOING_BACK_TO_HOUSE_AFTER_FEEDING;
                    returnHomeOffroad();
                } else {
                    countdown.step();
                }
            }

            case PREPARING_PIG_FOR_DELIVERY -> {
                if (countdown.hasReachedZero()) {
                    map.getStatisticsManager().pigGrown(player, map.getTime());

                    if (home.getFlag().hasPlaceForMoreCargo()) {
                        var cargo = new Cargo(PIG, map);
                        setCargo(cargo);
                        home.getFlag().promiseCargo(carriedCargo);

                        state = GOING_OUT_TO_PUT_CARGO;
                        setTarget(home.getFlag().getPosition());

                        player.reportChangedBuilding(home);
                    } else {
                        state = WAITING_FOR_SPACE_ON_FLAG;
                    }
                } else {
                    countdown.step();
                }
            }

            case WAITING_FOR_SPACE_ON_FLAG -> {
                if (home.getFlag().hasPlaceForMoreCargo()) {
                    var cargo = new Cargo(PIG, map);
                    setCargo(cargo);
                    home.getFlag().promiseCargo(carriedCargo);

                    state = GOING_OUT_TO_PUT_CARGO;
                    setTarget(home.getFlag().getPosition());
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

    private boolean isPigReceiver(Building building) {
        if (building.isReady()) {
            if (building instanceof Storehouse storehouse) {
                return !storehouse.isDeliveryBlocked(PIG);
            }

            return building.needsMaterial(PIG);
        }

        return false;
    }

    @Override
    public void onArrival() {
        switch (state) {
            case GOING_OUT_TO_PUT_CARGO -> {
                carriedCargo.setPosition(position);
                carriedCargo.transportToReceivingBuilding(this::isPigReceiver);
                home.getFlag().putCargo(carriedCargo);
                carriedCargo = null;

                state = GOING_BACK_TO_HOUSE;
                setTarget(home.getPosition());
            }

            case GOING_BACK_TO_HOUSE -> {
                state = RESTING_IN_HOUSE;
                enterBuilding(home);
                countdown.countFrom(TIME_TO_REST);
            }

            case GOING_BACK_TO_HOUSE_AFTER_FEEDING -> {
                enterBuilding(home);
                state = PREPARING_PIG_FOR_DELIVERY;
                countdown.countFrom(TIME_TO_PREPARE_PIG);
            }

            case GOING_OUT_TO_FEED -> {
                countdown.countFrom(TIME_TO_FEED);
                state = FEEDING;
                doAction(WorkerAction.FEED_THE_PIGS);
            }

            case RETURNING_TO_STORAGE -> {
                var storehouse = (Storehouse) map.getBuildingAtPoint(position);
                storehouse.depositWorker(this);
            }

            case GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE -> {
                var storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, PIG_BREEDER);

                if (storehouse != null) {
                    state = RETURNING_TO_STORAGE;
                    setTarget(storehouse.getPosition());
                } else {
                    state = State.GOING_TO_DIE;
                    setOffroadTarget(findPlaceToDie());
                }
            }

            case GOING_TO_DIE -> {
                setDead();
                state = State.DEAD;
                countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
            }
        }
    }

    @Override
    protected void onReturnToStorage() {
        var storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, PIG_BREEDER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;
            setTarget(storage.getPosition());
        } else {
            storage = (Storehouse) GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, player, PIG_BREEDER);

            if (storage != null) {
                state = RETURNING_TO_STORAGE;
                setOffroadTarget(storage.getPosition());
            } else {
                setOffroadTarget(findPlaceToDie(), position.downRight());
                state = State.GOING_TO_DIE;
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {
        if (state == WALKING_TO_TARGET &&
                map.isFlagAtPoint(position) &&
                !map.arePointsConnectedByRoads(position, target)) {
            clearTargetBuilding();
            returnToStorage();
        }
    }

    @Override
    public int getProductivity() {
        return productivityMeasurer.productivity();
    }

    @Override
    public void goToOtherStorage(Building building) {
        state = PigBreeder.State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;
        setTarget(building.getFlag().getPosition());
    }

    @Override
    public boolean isWorking() {
        return state == GOING_OUT_TO_FEED ||
                state == FEEDING ||
                state == GOING_BACK_TO_HOUSE_AFTER_FEEDING ||
                state == PREPARING_PIG_FOR_DELIVERY;
    }

    public boolean isPreparingPigForDelivery() {
        return state == PREPARING_PIG_FOR_DELIVERY;
    }
}
