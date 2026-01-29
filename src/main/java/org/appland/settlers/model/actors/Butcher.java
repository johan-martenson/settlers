package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.WorkerAction;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;

import static java.lang.String.format;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Butcher.State.*;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Butcher extends Worker {
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
    private static final int PRODUCTION_TIME = 48;
    private static final int RESTING_TIME    = 99;

    private final Countdown countdown = new Countdown();
    private final ProductivityMeasurer productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME, null);

    private State state = WALKING_TO_TARGET;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        SLAUGHTERING_PIG,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        RETURNING_TO_STORAGE
    }

    public Butcher(Player player, GameMap map) {
        super(player, map);
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
                    if (home.getAmount(PIG) > 0 && home.isProductionEnabled()) {
                        state = State.SLAUGHTERING_PIG;
                        countdown.countFrom(PRODUCTION_TIME);
                        player.reportChangedBuilding(home);
                        map.reportWorkerStartedAction(this, WorkerAction.SLAUGHTERING);
                        goOutside();
                    } else {
                        // Report that the butcher lacked resources and couldn't do his job
                        productivityMeasurer.reportUnproductivity();
                    }
                } else {
                    countdown.step();
                }
            }

            case SLAUGHTERING_PIG -> {
                if (countdown.hasReachedZero()) {

                    // Consume the resource
                    home.consumeOne(PIG);

                    // Report that the butcher produced one piece of meat
                    productivityMeasurer.reportProductivity();
                    productivityMeasurer.nextProductivityCycle();

                    map.getStatisticsManager().meatProduced(player, map.getTime());

                    // Handle transportation
                    if (home.getFlag().hasPlaceForMoreCargo()) {
                        setCargo(new Cargo(MEAT, map));

                        // Go out to the flag to deliver the meat
                        state = State.GOING_TO_FLAG_WITH_CARGO;
                        setTarget(home.getFlag().getPosition());
                        home.getFlag().promiseCargo(carriedCargo);
                    } else {
                        state = WAITING_FOR_SPACE_ON_FLAG;
                        goInside();
                    }
                } else {
                    countdown.step();
                }
            }

            case WAITING_FOR_SPACE_ON_FLAG -> {
                if (home.getFlag().hasPlaceForMoreCargo()) {
                    setCargo(new Cargo(MEAT, map));

                    // Go out to the flag to deliver the meat
                    state = GOING_TO_FLAG_WITH_CARGO;
                    setTarget(home.getFlag().getPosition());
                    home.getFlag().promiseCargo(getCargo());
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

    private boolean isMeatReceiver(Building building) {
        if (building.isReady() && building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(MEAT);
        }

        if (building.isReady() && building.needsMaterial(MEAT)) {
            return true;
        }

        return false;
    }

    @Override
    protected void onArrival() {
        switch (state) {
            case GOING_TO_FLAG_WITH_CARGO -> {
                var flag = map.getFlagAtPoint(getPosition());

                carriedCargo.setPosition(getPosition());
                carriedCargo.transportToReceivingBuilding(this::isMeatReceiver);

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
                var storehouse = (Storehouse) map.getBuildingAtPoint(getPosition());
                storehouse.depositWorker(this);
            }

            case GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE -> {

                // Go to the closest storage
                var storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, BUTCHER);

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
    public String toString() {
        return isExactlyAtPoint()
                ? format("Butcher %s", position)
                : format("Butcher %s - %s", position, getNextPoint());
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, BUTCHER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, player, BUTCHER);

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

        // Return to storage if the planned path no longer exists
        if (state == WALKING_TO_TARGET &&
            map.isFlagAtPoint(position) &&
            !map.arePointsConnectedByRoads(position, target)) {

            // Don't try to enter the slaughterhouse upon arrival
            clearTargetBuilding();

            // Go back to the storage
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
        state = State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;
        setTarget(building.getFlag().getPosition());
    }

    @Override
    public boolean isWorking() {
        return state == State.SLAUGHTERING_PIG;
    }

    public boolean isSlaughtering() {
        return state == State.SLAUGHTERING_PIG;
    }
}
