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

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;

    private State state;

    public Miller(Player player, GameMap map) {
        super(player, map);

        countdown = new Countdown();
        state = State.WALKING_TO_TARGET;

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME, null);
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

                player.reportChangedBuilding(getHome());

                countdown.countFrom(PRODUCTION_TIME);
            } else {
                countdown.step();
            }
        } else if (state == WAITING_FOR_SPACE_ON_FLAG) {
            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                var cargo = new Cargo(FLOUR, map);

                setCargo(cargo);

                getHome().getFlag().promiseCargo(getCargo());

                state = GOING_TO_FLAG_WITH_CARGO;

                setTarget(getHome().getFlag().getPosition());
            }

        } else if (state == State.GRINDING_WHEAT) {
            if (getHome().getAmount(WHEAT) > 0 && getHome().isProductionEnabled()) {
                if (countdown.hasReachedZero()) {

                    // Consume the wheat
                    getHome().consumeOne(WHEAT);

                    player.reportChangedBuilding(getHome());

                    // Go out to the flag to deliver the flour
                    if (getHome().getFlag().hasPlaceForMoreCargo()) {
                        var cargo = new Cargo(FLOUR, map);

                        cargo.setPosition(getPosition());

                        setCargo(cargo);

                        setTarget(getHome().getFlag().getPosition());

                        state = GOING_TO_FLAG_WITH_CARGO;

                        getHome().getFlag().promiseCargo(getCargo());

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
            var flag = getHome().getFlag();
            var cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToReceivingBuilding(this::isFlourReceiver);

            flag.putCargo(getCargo());

            setCargo(null);

            returnHome();

            state = GOING_BACK_TO_HOUSE;
        } else if (state == GOING_BACK_TO_HOUSE) {
            enterBuilding(getHome());

            state = RESTING_IN_HOUSE;
            countdown.countFrom(RESTING_TIME);
        } else if (state == RETURNING_TO_STORAGE) {
            var storehouse = (Storehouse) map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        } else if (state == State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            // Go to the closest storage
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, MILLER);

            if (storehouse != null) {
                state = RETURNING_TO_STORAGE;

                setTarget(storehouse.getPosition());
            } else {
                state = State.GOING_TO_DIE;

                var point = findPlaceToDie();

                setOffroadTarget(point);
            }
        } else if (state == State.GOING_TO_DIE) {
            setDead();

            state = State.DEAD;

            countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
        }
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, MILLER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), MILLER);

            if (storage != null) {
                state = RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            } else {
                var point = findPlaceToDie();

                setOffroadTarget(point, getPosition().downRight());

                state = State.GOING_TO_DIE;
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {

        // Return to storage if the planned path no longer exists
        if (state == WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

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
