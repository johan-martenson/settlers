package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Vegetation;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;

import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WELL_WORKER;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class WellWorker extends Worker {
    private static final int PRODUCTION_TIME = 49;
    private static final int RESTING_TIME = 99;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown = new Countdown();
    private final ProductivityMeasurer productivityMeasurer;

    private State state = State.WALKING_TO_TARGET;

    public WellWorker(Player player, GameMap map) {
        super(player, map);

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME, null);
    }

    private enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        DRAWING_WATER,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        RETURNING_TO_STORAGE
    }

    @Override
    protected void onEnterBuilding(Building building) {
        state = State.RESTING_IN_HOUSE;

        countdown.countFrom(RESTING_TIME);

        productivityMeasurer.setBuilding(building);
    }

    @Override
    protected void onIdle() {
        switch (state) {
            case RESTING_IN_HOUSE -> {
                if (countdown.hasReachedZero() && home.isProductionEnabled() && isWaterInGround()) {
                    state = State.DRAWING_WATER;
                    countdown.countFrom(PRODUCTION_TIME);
                } else if (home.isProductionEnabled()) {
                    countdown.step();
                } else {
                    productivityMeasurer.reportUnproductivity();
                }
            }

            case DRAWING_WATER -> {
                if (countdown.hasReachedZero()) {
                    productivityMeasurer.reportProductivity();
                    productivityMeasurer.nextProductivityCycle();
                    map.getStatisticsManager().waterProduced(player, map.getTime());

                    if (home.getFlag().hasPlaceForMoreCargo()) {
                        var cargo = new Cargo(WATER, map);
                        setCargo(cargo);

                        setTarget(home.getFlag().getPosition());
                        state = State.GOING_TO_FLAG_WITH_CARGO;

                        home.getFlag().promiseCargo(cargo);
                    } else {
                        state = State.WAITING_FOR_SPACE_ON_FLAG;
                    }
                } else {
                    countdown.step();
                }
            }

            case WAITING_FOR_SPACE_ON_FLAG -> {
                if (home.getFlag().hasPlaceForMoreCargo()) {
                    var cargo = new Cargo(WATER, map);
                    setCargo(cargo);

                    setTarget(home.getFlag().getPosition());
                    state = State.GOING_TO_FLAG_WITH_CARGO;

                    home.getFlag().promiseCargo(cargo);
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

    public boolean isReceiverForWater(Building building) {
        if (building.isReady() && building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(WATER);
        }

        if (building.isReady() && building.needsMaterial(WATER)) {
            return true;
        }

        return false;
    }

    @Override
    protected void onArrival() {
        switch (state) {
            case GOING_TO_FLAG_WITH_CARGO -> {
                var flag = home.getFlag();
                var cargo = getCargo();

                cargo.setPosition(position);
                cargo.transportToReceivingBuilding(this::isReceiverForWater);

                flag.putCargo(cargo);

                setCargo(null);
                returnHome();

                state = State.GOING_BACK_TO_HOUSE;
            }

            case GOING_BACK_TO_HOUSE -> {
                enterBuilding(home);

                state = State.RESTING_IN_HOUSE;
                countdown.countFrom(RESTING_TIME);
            }

            case RETURNING_TO_STORAGE -> {
                var storehouse = (Storehouse) map.getBuildingAtPoint(position);
                storehouse.depositWorker(this);
            }

            case GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE -> {
                var storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, WELL_WORKER);

                if (storehouse != null) {
                    state = State.RETURNING_TO_STORAGE;
                    setTarget(storehouse.getPosition());
                } else {
                    state = State.GOING_TO_DIE;
                    var point = findPlaceToDie();
                    setOffroadTarget(point);
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
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, WELL_WORKER);

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, getPlayer(), WELL_WORKER);

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            } else {
                Point point = findPlaceToDie();

                setOffroadTarget(point, position.downRight());

                state = State.GOING_TO_DIE;
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {

        // Return to storage if the planned path no longer exists
        if (state == State.WALKING_TO_TARGET &&
            map.isFlagAtPoint(position) &&
            !map.arePointsConnectedByRoads(position, getTarget())) {

            // Don't try to enter the well upon arrival
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

    private boolean isWaterInGround() {
        return !map.isSurroundedBy(home.getPosition(), Vegetation.BUILDABLE_MOUNTAIN);
    }

    @Override
    public void goToOtherStorage(Building building) {
        state = State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
