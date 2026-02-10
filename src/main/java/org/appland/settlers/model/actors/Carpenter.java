package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;

import static java.lang.String.format;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.WorkerAction.SAWING;
import static org.appland.settlers.model.actors.Carpenter.State.*;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Carpenter extends Worker {
    private static final int PRODUCTION_TIME = 49;
    private static final int RESTING_TIME    = 99;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown = new Countdown();
    private final ProductivityMeasurer productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME, null);

    private State state = WALKING_TO_TARGET;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        CUTTING_WOOD,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        RETURNING_TO_STORAGE
    }

    public Carpenter(Player player, GameMap map) {
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
                    state = CUTTING_WOOD;
                    countdown.countFrom(PRODUCTION_TIME);

                    goOutside();
                    doAction(SAWING);

                    productivityMeasurer.nextProductivityCycle();

                    map.reportChangedBuilding(home);
                } else {
                    countdown.step();
                }
            }

            case CUTTING_WOOD -> {
                if (home.getAmount(WOOD) > 0 && home.isProductionEnabled()) {
                    if (countdown.hasReachedZero()) {
                        System.out.println("DONE SAWING. GOING INSIDE");
                        home.consumeOne(WOOD);
                        goInside();

                        productivityMeasurer.reportProductivity();

                        map.getStatisticsManager().plankProduced(player, map.getTime());

                        // Handle transportation
                        if (home.getFlag().hasPlaceForMoreCargo()) {
                            var cargo = new Cargo(PLANK, map);

                            setCargo(cargo);
                            // Go out to delivery the plank to the flag
                            state = GOING_TO_FLAG_WITH_CARGO;
                            setTarget(home.getFlag().getPosition());

                            home.getFlag().promiseCargo(getCargo());
                        } else {
                            state = WAITING_FOR_SPACE_ON_FLAG;
                        }
                    } else {
                        countdown.step();
                    }
                } else {

                    // Report the that the sawmill worker was unproductive
                    productivityMeasurer.reportUnproductivity();
                }
            }

            case WAITING_FOR_SPACE_ON_FLAG -> {
                if (home.getFlag().hasPlaceForMoreCargo()) {

                    // Go out to delivery the plank to the flag
                    state = GOING_TO_FLAG_WITH_CARGO;
                    setTarget(home.getFlag().getPosition());

                    setCargo(new Cargo(PLANK, map));
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

    public boolean isPlankReceiver(Building building) {
        if (building instanceof Storehouse storehouse && storehouse.isReady() && !storehouse.isDeliveryBlocked(PLANK)) {
            return true;
        }

        return building.needsMaterial(PLANK);

        // TODO: also handle the ship/boat construction case
    }

    @Override
    protected void onArrival() {
        switch (state) {
            case GOING_TO_FLAG_WITH_CARGO -> {
                var flag = map.getFlagAtPoint(position);

                var cargo = getCargo();

                cargo.setPosition(position);
                cargo.transportToReceivingBuilding(this::isPlankReceiver);

                flag.putCargo(getCargo());

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
                Storehouse storehouse = (Storehouse) map.getBuildingAtPoint(position);
                storehouse.depositWorker(this);
            }

            case GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE -> {
                Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, CARPENTER);

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
                ? format("Sawmill worker %s", position)
                : format("Sawmill worker %s - %s", position, getNextPoint());
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, CARPENTER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;
            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, getPlayer(), CARPENTER);

            if (storage != null) {
                state = RETURNING_TO_STORAGE;
                setOffroadTarget(storage.getPosition());
            } else {
                Point point = findPlaceToDie();

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
            !map.arePointsConnectedByRoads(position, getTarget())) {

            // Don't try to enter the sawmill upon arrival
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
        return state == State.CUTTING_WOOD;
    }
}
