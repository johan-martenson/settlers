package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Brewer.State.*;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Brewer extends Worker {
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
    private static final int PRODUCTION_TIME = 49;
    private static final int RESTING_TIME = 99;

    private final Countdown countdown = new Countdown();
    private final ProductivityMeasurer productivityMeasurer;

    private State state = State.WALKING_TO_TARGET;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        BREWING_BEER,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        RETURNING_TO_STORAGE
    }

    public Brewer(Player player, GameMap map) {
        super(player, map);

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
        switch (state) {
            case RESTING_IN_HOUSE -> {
                if (countdown.hasReachedZero()) {
                    state = BREWING_BEER;
                    countdown.countFrom(PRODUCTION_TIME);
                    player.reportChangedBuilding(home);

                    productivityMeasurer.nextProductivityCycle();
                } else {
                    countdown.step();
                }
            }
            case WAITING_FOR_SPACE_ON_FLAG -> {
                if (home.getFlag().hasPlaceForMoreCargo()) {
                    var cargo = new Cargo(BEER, map);
                    setCargo(cargo);
                    home.getFlag().promiseCargo(carriedCargo);

                    // Go place the beer at the flag
                    state = State.GOING_TO_FLAG_WITH_CARGO;
                    setTarget(home.getFlag().getPosition());
                }
            }
            case BREWING_BEER -> {
                if (home.getAmount(WATER) > 0 && home.getAmount(WHEAT) > 0 && home.isProductionEnabled()) {
                    if (countdown.hasReachedZero()) {

                        // Consume the ingredients
                        home.consumeOne(WATER);
                        home.consumeOne(WHEAT);

                        // Report the production
                        productivityMeasurer.reportProductivity();

                        map.getStatisticsManager().beerProduced(player, map.getTime());

                        // Handle transportation of the produced beer
                        if (!home.getFlag().hasPlaceForMoreCargo()) {
                            state = WAITING_FOR_SPACE_ON_FLAG;
                        } else {
                            var cargo = new Cargo(BEER, map);
                            setCargo(cargo);

                            // Go place the beer at the flag
                            state = GOING_TO_FLAG_WITH_CARGO;

                            setTarget(home.getFlag().getPosition());

                            home.getFlag().promiseCargo(carriedCargo);
                        }
                    } else {
                        countdown.step();
                    }
                } else {

                    // Report the that the brewer was unproductive
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

    @Override
    protected void onArrival() {
        switch (state) {
            case GOING_TO_FLAG_WITH_CARGO -> {
                carriedCargo.setPosition(position);
                carriedCargo.transportToStorage();

                var flag = map.getFlagAtPoint(position);
                flag.putCargo(carriedCargo);

                setCargo(null);

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

                // Go to the closest storage
                var storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, BREWER);

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
        var storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, BREWER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            storage = (Storehouse) GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, player, BREWER);

            if (storage != null) {
                state = RETURNING_TO_STORAGE;
                setOffroadTarget(storage.getPosition());
            } else {
                state = GOING_TO_DIE;
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

            // Don't try to enter upon arrival
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
        state = GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;
        setTarget(building.getFlag().getPosition());
    }

    @Override
    public boolean isWorking() {
        return state == BREWING_BEER;
    }
}
