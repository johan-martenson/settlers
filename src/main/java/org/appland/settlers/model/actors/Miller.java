package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Miller.State.*;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Miller extends Worker {
    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GRINDING_WHEAT,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        WAITING_FOR_SPACE_ON_FLAG
    }

    private static final int PRODUCTION_TIME = 49;
    private static final int RESTING_TIME = 99;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown = new Countdown();
    private final ProductivityMeasurer productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME, null);

    private State state = State.WALKING_TO_TARGET;

    public Miller(Player player, GameMap map) {
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
        if (state == RESTING_IN_HOUSE) {
            if (countdown.hasReachedZero()) {
                state = State.GRINDING_WHEAT;
                countdown.countFrom(PRODUCTION_TIME);

                player.reportChangedBuilding(home);
            } else {
                countdown.step();
            }
        } else if (state == WAITING_FOR_SPACE_ON_FLAG) {
            if (home.getFlag().hasPlaceForMoreCargo()) {
                var cargo = new Cargo(FLOUR, map);

                setCargo(cargo);
                home.getFlag().promiseCargo(cargo);

                state = GOING_TO_FLAG_WITH_CARGO;
                setTarget(home.getFlag().getPosition());
            }

        } else if (state == State.GRINDING_WHEAT) {
            if (home.getAmount(WHEAT) > 0 && home.isProductionEnabled()) {
                if (countdown.hasReachedZero()) {

                    // Consume the wheat
                    home.consumeOne(WHEAT);

                    player.reportChangedBuilding(home);

                    // Go out to the flag to deliver the flour
                    if (home.getFlag().hasPlaceForMoreCargo()) {
                        var cargo = new Cargo(FLOUR, map);

                        cargo.setPosition(position);

                        setCargo(cargo);
                        home.getFlag().promiseCargo(carriedCargo);

                        state = GOING_TO_FLAG_WITH_CARGO;
                        setTarget(home.getFlag().getPosition());

                    // Wait for space on the flag if it's full
                    } else {
                        state = WAITING_FOR_SPACE_ON_FLAG;
                    }

                    // Report that the miller produced flour
                    productivityMeasurer.reportProductivity();
                    productivityMeasurer.nextProductivityCycle();
                } else {
                    countdown.step();
                }
            } else {

                // Report that the miller couldn't produce flour because it had no wheat
                productivityMeasurer.reportUnproductivity();
            }
        } else if (state == State.DEAD) {
            if (countdown.hasReachedZero()) {
                map.removeWorker(this);
            } else {
                countdown.step();
            }
        }
    }

    private boolean isFlourReceiver(Building building) {
        if (building.isReady() && building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(FLOUR);
        }

        if (building.isReady() && building.needsMaterial(FLOUR)) {
            return true;
        }

        return false;
    }

    @Override
    protected void onArrival() {
        if (state == GOING_TO_FLAG_WITH_CARGO) {
            var flag = home.getFlag();

            carriedCargo.setPosition(position);
            carriedCargo.transportToReceivingBuilding(this::isFlourReceiver);

            flag.putCargo(carriedCargo);

            carriedCargo = null;

            state = GOING_BACK_TO_HOUSE;
            returnHome();
        } else if (state == GOING_BACK_TO_HOUSE) {
            enterBuilding(home);

            state = RESTING_IN_HOUSE;
            countdown.countFrom(RESTING_TIME);
        } else if (state == RETURNING_TO_STORAGE) {
            var storehouse = (Storehouse) map.getBuildingAtPoint(position);
            storehouse.depositWorker(this);
        } else if (state == State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            // Go to the closest storage
            var storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, MILLER);

            if (storehouse != null) {
                state = RETURNING_TO_STORAGE;
                setTarget(storehouse.getPosition());
            } else {
                state = State.GOING_TO_DIE;
                setOffroadTarget(findPlaceToDie());
            }
        } else if (state == State.GOING_TO_DIE) {
            setDead();

            state = State.DEAD;
            countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
        }
    }

    @Override
    protected void onReturnToStorage() {
        var storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, MILLER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;
            setTarget(storage.getPosition());
        } else {
            storage = (Storehouse) GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, player, MILLER);

            if (storage != null) {
                state = RETURNING_TO_STORAGE;
                setOffroadTarget(storage.getPosition());
            } else {
                state = State.GOING_TO_DIE;
                setOffroadTarget(findPlaceToDie(), position.downRight());
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {

        // Return to storage if the planned path no longer exists
        if (state == WALKING_TO_TARGET &&
            map.isFlagAtPoint(position) &&
            !map.arePointsConnectedByRoads(position, target)) {

            // Don't try to enter the mill upon arrival
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
        return state == GRINDING_WHEAT;
    }
}
