package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.IronFounder.State.*;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class IronFounder extends Worker {
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
    private static final int PRODUCTION_TIME = 49;
    private static final int RESTING_TIME = 99;

    private final Countdown countdown = new Countdown();
    private final ProductivityMeasurer productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME, null);

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        MELTING_IRON,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        RETURNING_TO_STORAGE
    }

    private State state = State.WALKING_TO_TARGET;

    public IronFounder(Player player, GameMap map) {
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
                    state = MELTING_IRON;
                    countdown.countFrom(PRODUCTION_TIME);
                } else {
                    countdown.step();
                }
            }

            case MELTING_IRON -> {
                if (home.getAmount(COAL) > 0 && home.getAmount(IRON) > 0 && home.isProductionEnabled()) {
                    if (countdown.hasReachedZero()) {

                        // Consume the resources
                        home.consumeOne(COAL);
                        home.consumeOne(IRON);

                        // Report production
                        productivityMeasurer.reportProductivity();
                        productivityMeasurer.nextProductivityCycle();
                        map.getStatisticsManager().ironBarProduced(player, map.getTime());

                        // Handle transportation
                        var flag = home.getFlag();
                        if (flag.hasPlaceForMoreCargo()) {
                            var cargo = new Cargo(IRON_BAR, map);
                            setCargo(cargo);
                            state = GOING_TO_FLAG_WITH_CARGO;
                            setTarget(flag.getPosition());
                            flag.promiseCargo(cargo);
                        } else {
                            state = State.WAITING_FOR_SPACE_ON_FLAG;
                        }
                    } else {
                        countdown.step();
                    }
                } else {
                    // Report lack of resources
                    productivityMeasurer.reportUnproductivity();
                }
            }

            case WAITING_FOR_SPACE_ON_FLAG -> {
                var flag = home.getFlag();
                if (flag.hasPlaceForMoreCargo()) {
                    var cargo = new Cargo(IRON_BAR, map);
                    setCargo(cargo);
                    state = GOING_TO_FLAG_WITH_CARGO;
                    setTarget(flag.getPosition());
                    flag.promiseCargo(cargo);
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

    private boolean isIronBarReceiver(Building building) {
        if (building.isReady() && building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(IRON_BAR);
        }

        return building.isReady() && building.needsMaterial(IRON_BAR);
    }

    @Override
    protected void onArrival() {
        switch (state) {
            case GOING_TO_FLAG_WITH_CARGO -> {
                var flag = map.getFlagAtPoint(position);
                var cargo = getCargo();

                cargo.setPosition(position);
                cargo.transportToReceivingBuilding(this::isIronBarReceiver);

                flag.putCargo(cargo);
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
                var storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, IRON_FOUNDER);

                if (storehouse != null) {
                    state = RETURNING_TO_STORAGE;
                    setTarget(storehouse.getPosition());
                } else {
                    state = GOING_TO_DIE;
                    var point = findPlaceToDie();
                    setOffroadTarget(point);
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
    public String toString() {
        return isExactlyAtPoint()
                ? String.format("Iron founder %s", position)
                : String.format("Iron founder %s - %s", position, getNextPoint());
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, IRON_FOUNDER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, getPlayer(), IRON_FOUNDER);

            if (storage != null) {
                state = RETURNING_TO_STORAGE;
                setOffroadTarget(storage.getPosition());
            } else {
                var point = findPlaceToDie();
                setOffroadTarget(point, position.downRight());
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

            // Don't try to enter the iron foundry upon arrival
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
}
