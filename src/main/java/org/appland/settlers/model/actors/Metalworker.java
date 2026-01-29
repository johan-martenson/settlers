package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.WorkerAction;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;

import java.util.EnumMap;
import java.util.Map;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Metalworker.State.*;

@Walker(speed = 10)
public class Metalworker extends Worker {
    private static final int PRODUCTION_TIME = 49;
    private static final int RESTING_TIME = 99;
    private static final int TIME_FOR_ACTION = 29;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown = new Countdown();
    private final ProductivityMeasurer productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME, null);
    private final Map<Material, Integer> producedDuringPeriod = new EnumMap<>(Material.class);

    private State state = State.WALKING_TO_TARGET;
    private int currentToolIndex = 0;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        HAMMERING,
        SAWING,
        WIPING_SWEAT,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        RETURNING_TO_STORAGE
    }

    public Metalworker(Player player, GameMap map) {
        super(player, map);
    }

    private Material getNextTool() {

        // Reset the production period if needed
        boolean quotaLeft = false;
        for (var tool : TOOLS) {
            if (player.getProductionQuotaForTool(tool) > producedDuringPeriod.getOrDefault(tool, 0)) {
                quotaLeft = true;

                break;
            }
        }

        if (!quotaLeft) {
            currentToolIndex = 0;
            producedDuringPeriod.clear();
        }

        // Find the next tool to produce
        int amountTools = TOOLS.size();

        for (int i = 0; i < amountTools; i++) {
            int toolIndex = (currentToolIndex + i) % TOOLS.size();

            var tool = TOOLS.get(toolIndex);

            int quotaForTool = player.getProductionQuotaForTool(tool);
            int amountProducedForTool = producedDuringPeriod.getOrDefault(tool, 0);

            if (amountProducedForTool < quotaForTool) {
                producedDuringPeriod.merge(tool, 1, Integer::sum);
                currentToolIndex = toolIndex + 1;

                return tool;
            }
        }

        // Return null if there is no quota configured for any tool
        return null;
    }

    @Override
    protected void onEnterBuilding(Building building) {
        state = RESTING_IN_HOUSE;
        countdown.countFrom(RESTING_TIME);

        productivityMeasurer.setBuilding(building);
    }

    @Override
    protected void onIdle() {
        System.out.println();
        System.out.println(state);
        System.out.println(countdown.getCount());

        switch (state) {
            case RESTING_IN_HOUSE -> {
                if (countdown.hasReachedZero()) {
                    if (home.getAmount(PLANK) > 0 &&
                        home.getAmount(IRON_BAR) > 0 &&
                        home.getFlag().hasPlaceForMoreCargo() &&
                        home.isProductionEnabled()) {
                        state = HAMMERING;
                        countdown.countFrom(TIME_FOR_ACTION);
                        goOutside();
                        player.reportWorkerStartedAction(this, WorkerAction.HAMMER_TO_MAKE_TOOL);
                        map.reportWorkerWentOutside(this);
                        player.reportChangedBuilding(home);
                    }

                    productivityMeasurer.nextProductivityCycle();
                } else {
                    countdown.step();
                }
            }

            case HAMMERING -> {
                if (countdown.hasReachedZero()) {
                    state = SAWING;
                    countdown.countFrom(TIME_FOR_ACTION);
                    player.reportWorkerStartedAction(this, WorkerAction.SAWING_TO_MAKE_TOOL);
                } else {
                    countdown.step();
                }
            }

            case SAWING -> {
                if (countdown.hasReachedZero()) {
                    state = WIPING_SWEAT;
                    countdown.countFrom(TIME_FOR_ACTION);
                    player.reportWorkerStartedAction(this, WorkerAction.WIPE_OFF_SWEAT_TO_MAKE_TOOL);
                } else {
                    countdown.step();
                }
            }

            case WAITING_FOR_SPACE_ON_FLAG -> {
                if (home.getFlag().hasPlaceForMoreCargo()) {
                    var nextTool = getNextTool();

                    carriedCargo = new Cargo(nextTool, map);
                    home.getFlag().promiseCargo(getCargo());

                    // Go place the tool at the flag
                    state = State.GOING_TO_FLAG_WITH_CARGO;
                    setTarget(home.getFlag().getPosition());
                }
            }

            case WIPING_SWEAT -> {
                if (countdown.hasReachedZero()) {

                    // Wait if all tool quotas are zero
                    boolean anyToolQuotaAboveZero = false;
                    for (var tool : TOOLS) {
                        if (player.getProductionQuotaForTool(tool) > 0) {
                            anyToolQuotaAboveZero = true;

                            break;
                        }
                    }

                    if (!anyToolQuotaAboveZero) {
                        return;
                    }

                    // Consume the ingredients
                    home.consumeOne(PLANK);
                    home.consumeOne(IRON_BAR);

                    // Report the production
                    productivityMeasurer.reportProductivity();

                    map.getStatisticsManager().toolProduced(player, map.getTime());

                    // Handle transportation of the produced tool
                    if (!home.getFlag().hasPlaceForMoreCargo()) {
                        state = WAITING_FOR_SPACE_ON_FLAG;
                    } else {
                        var nextTool = getNextTool();

                        carriedCargo = new Cargo(nextTool, map);

                        // Go place the tool at the flag
                        state = GOING_TO_FLAG_WITH_CARGO;
                        System.out.println("Metalworker at %s, going to %s");
                        setTarget(home.getFlag().getPosition());

                        home.getFlag().promiseCargo(getCargo());
                    }
                } else {
                    countdown.step();
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
                Flag flag = map.getFlagAtPoint(position);

                carriedCargo.setPosition(position);
                carriedCargo.transportToStorage();
                flag.putCargo(carriedCargo);
                carriedCargo = null;

                state = State.GOING_BACK_TO_HOUSE;

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
                var storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, METALWORKER);

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
                state = DEAD;
                countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
            }
        }
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, METALWORKER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, player, METALWORKER);

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
    protected void onWalkingAndAtFixedPoint() {

        // Return to storage if the planned path no longer exists
        if (state == WALKING_TO_TARGET &&
                map.isFlagAtPoint(position) &&
                !map.arePointsConnectedByRoads(position, getTarget())) {

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

    public boolean isHammering() {
        return state == HAMMERING;
    }

    public boolean isSawing() {
        return state == SAWING;
    }

    public boolean isWipingSweat() {
        return state == WIPING_SWEAT;
    }

    @Override
    public boolean isWorking() {
        return state == HAMMERING || state == SAWING || state == WIPING_SWEAT;
    }
}
