package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.OffroadOption;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.WorkerAction;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Catapult;
import org.appland.settlers.model.buildings.Storehouse;

import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.STONEMASON;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Stonemason extends Worker {
    private static final int TIME_TO_REST = 99;
    private static final int TIME_TO_GET_STONE = 49;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown = new Countdown();
    private final ProductivityMeasurer productivityMeasurer = new ProductivityMeasurer(TIME_TO_REST + TIME_TO_GET_STONE, null);

    private State state = State.WALKING_TO_TARGET;
    private Point stoneTarget = null;

    private enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_GET_STONE,
        GETTING_STONE,
        GOING_BACK_TO_HOUSE_WITH_CARGO,
        IN_HOUSE_WITH_CARGO,
        GOING_OUT_TO_PUT_CARGO,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        RETURNING_TO_STORAGE
    }

    public Stonemason(Player player, GameMap map) {
        super(player, map);
    }

    public boolean isGettingStone() {
        return state == State.GETTING_STONE;
    }

    @Override
    protected void onEnterBuilding(Building building) {
        state = State.RESTING_IN_HOUSE;
        countdown.countFrom(TIME_TO_REST);
        productivityMeasurer.setBuilding(building);
    }

    // FIXME: HOTSPOT - allocations
    @Override
    protected void onIdle() {
        switch (state) {
            case RESTING_IN_HOUSE -> {
                if (home.isProductionEnabled()) {
                    if (countdown.hasReachedZero()) {
                        var distance = Double.MAX_VALUE;
                        var homePoint = home.getPosition();

                        // Look for stones within range
                        for (var point : map.getPointsWithinRadius(homePoint, 8)) {

                            // Filter points without stones
                            if (!map.isStoneAtPoint(point)) {
                                continue;
                            }

                            // Is the stone reachable?
                            var path = map.findWayOffroad(home.getPosition(), null, point, null, OffroadOption.CAN_END_ON_STONE);

                            if (path == null) {
                                continue;
                            }

                            var distanceToStone = path.size();

                            // Check if this is the closest access point this far
                            if (distanceToStone < distance) {
                                distance = distanceToStone;
                                stoneTarget = point;
                            }
                        }

                        // Report that there are no resources if no point is found
                        if (stoneTarget == null) {
                            productivityMeasurer.reportUnproductivity();

                            // Only report once
                            if (!home.isOutOfNaturalResources()) {
                                home.reportNoMoreNaturalResources();
                                player.reportNoMoreResourcesForBuilding(home);
                            }

                            return;
                        }

                        state = State.GOING_OUT_TO_GET_STONE;
                        setOffroadTarget(stoneTarget, OffroadOption.CAN_END_ON_STONE);
                    } else {
                        countdown.step();
                    }
                }
            }

            case GETTING_STONE -> {
                if (countdown.hasReachedZero()) {

                    // Remove a piece of the stone if it still exists
                    if (map.isStoneAtPoint(stoneTarget)) {
                        map.removePartOfStone(stoneTarget);

                        setCargo(new Cargo(STONE, map));
                        state = State.GOING_BACK_TO_HOUSE_WITH_CARGO;

                        map.getStatisticsManager().stoneProduced(player, map.getTime());
                    } else {
                        state = State.GOING_BACK_TO_HOUSE;
                    }

                    stoneTarget = null;

                    returnHomeOffroad();
                } else {
                    countdown.step();
                }
            }
            case IN_HOUSE_WITH_CARGO -> {

                // Report that the stonemason produced a stone
                productivityMeasurer.reportProductivity();
                productivityMeasurer.nextProductivityCycle();

                // Handle transportation
                if (home.getFlag().hasPlaceForMoreCargo()) {

                    // Go out to the flag to deliver the stone
                    state = State.GOING_OUT_TO_PUT_CARGO;
                    setTarget(home.getFlag().getPosition());

                    home.getFlag().promiseCargo(getCargo());
                } else {
                    state = Stonemason.State.WAITING_FOR_SPACE_ON_FLAG;
                }
            }

            case WAITING_FOR_SPACE_ON_FLAG -> {
                if (home.getFlag().hasPlaceForMoreCargo()) {

                    // Go out to the flag to deliver the stone
                    state = State.GOING_OUT_TO_PUT_CARGO;
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

    public boolean isReceiverForStone(Building building) {
        if (building.isReady() && building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(STONE);
        }

        if (building.isUnderConstruction() && building.needsMaterial(STONE)) {
            return true;
        }

        if (building instanceof Catapult catapult) {
            return catapult.isReady() && catapult.needsMaterial(STONE);
        }

        return false;
    }

    @Override
    public void onArrival() {
        switch (state) {
            case GOING_OUT_TO_PUT_CARGO -> {
                carriedCargo.setPosition(position);
                carriedCargo.transportToReceivingBuilding(this::isReceiverForStone);
                home.getFlag().putCargo(carriedCargo);
                carriedCargo = null;

                state = State.GOING_BACK_TO_HOUSE;
                setTarget(home.getPosition());
            }

            case GOING_BACK_TO_HOUSE -> {
                state = State.RESTING_IN_HOUSE;
                enterBuilding(home);
                countdown.countFrom(TIME_TO_REST);
            }

            case GOING_OUT_TO_GET_STONE -> {
                state = State.GETTING_STONE;
                map.reportWorkerStartedAction(this, WorkerAction.HACKING_STONE);
                countdown.countFrom(TIME_TO_GET_STONE);
            }

            case GOING_BACK_TO_HOUSE_WITH_CARGO -> {
                enterBuilding(home);
                state = State.IN_HOUSE_WITH_CARGO;
            }

            case RETURNING_TO_STORAGE -> {
                var storehouse = (Storehouse) map.getBuildingAtPoint(position);
                storehouse.depositWorker(this);
            }

            case GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE -> {

                // Go to the closest storage
                Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, STONEMASON);

                if (storehouse != null) {
                    state = State.RETURNING_TO_STORAGE;
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
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, STONEMASON);

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;
            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, player, STONEMASON);

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;
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
        if (state == State.WALKING_TO_TARGET &&
            map.isFlagAtPoint(position) &&
            !map.arePointsConnectedByRoads(position, getTarget())) {

            // Don't try to enter the quarry upon arrival
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
