package org.appland.settlers.model.actors;

import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Storehouse;

import java.util.EnumMap;
import java.util.Map;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Metalworker.State.*;

@Walker(speed = 10)
public class Metalworker extends Worker {

    private static final int PRODUCTION_TIME = 49;
    private static final int RESTING_TIME = 99;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;
    private final Map<Material, Integer> producedDuringPeriod;

    private State state;
    private int currentToolIndex;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        MAKING_TOOL,
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

        countdown = new Countdown();
        state = State.WALKING_TO_TARGET;

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME, null);

        currentToolIndex = 0;
        producedDuringPeriod = new EnumMap<>(Material.class);
    }

    private Material getNextTool() {

        /* Reset the production period if needed */
        boolean quotaLeft = false;
        for (Material tool : TOOLS) {
            if (getPlayer().getProductionQuotaForTool(tool) > producedDuringPeriod.getOrDefault(tool, 0)) {
                quotaLeft = true;

                break;
            }
        }

        if (!quotaLeft) {
            currentToolIndex = 0;

            producedDuringPeriod.clear();
        }

        /* Find the next tool to produce */
        int amountTools = TOOLS.size();

        for (int i = 0; i < amountTools; i++) {
            int toolIndex = (currentToolIndex + i) % TOOLS.size();

            Material tool = TOOLS.get(toolIndex);

            int quotaForTool = getPlayer().getProductionQuotaForTool(tool);
            int amountProducedForTool = producedDuringPeriod.getOrDefault(tool, 0);

            if (amountProducedForTool < quotaForTool) {
                producedDuringPeriod.put(tool, amountProducedForTool + 1);

                currentToolIndex = toolIndex + 1;

                return tool;
            }
        }

        /* Return null if there is no quota configured for any tool */
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
        if (state == RESTING_IN_HOUSE) {
            if (countdown.hasReachedZero()) {
                state = State.MAKING_TOOL;
                countdown.countFrom(PRODUCTION_TIME);

                productivityMeasurer.nextProductivityCycle();
            } else {
                countdown.step();
            }
        } else if (state == State.WAITING_FOR_SPACE_ON_FLAG) {

            if (home.getFlag().hasPlaceForMoreCargo()) {
                Material nextTool = getNextTool();

                Cargo cargo = new Cargo(nextTool, map);
                setCargo(cargo);

                /* Go place the tool at the flag */
                state = State.GOING_TO_FLAG_WITH_CARGO;

                setTarget(home.getFlag().getPosition());

                home.getFlag().promiseCargo(getCargo());
            }

        } else if (state == MAKING_TOOL) {
            if (home.getAmount(PLANK) > 0 && home.getAmount(IRON_BAR) > 0 && home.isProductionEnabled()) {
                if (countdown.hasReachedZero()) {

                    /* Wait if all tool quotas are zero */
                    boolean anyToolQuotaAboveZero = false;
                    for (Material tool : TOOLS) {
                        if (getPlayer().getProductionQuotaForTool(tool) > 0) {
                            anyToolQuotaAboveZero = true;

                            break;
                        }
                    }

                    if (!anyToolQuotaAboveZero) {
                        return;
                    }

                    /* Consume the ingredients */
                    home.consumeOne(PLANK);
                    home.consumeOne(IRON_BAR);

                    /* Report the production */
                    productivityMeasurer.reportProductivity();

                    map.getStatisticsManager().toolProduced(player, map.getTime());

                    /* Handle transportation of the produced tool */
                    if (!home.getFlag().hasPlaceForMoreCargo()) {
                        state = WAITING_FOR_SPACE_ON_FLAG;
                    } else {
                        Material nextTool = getNextTool();

                        Cargo cargo = new Cargo(nextTool, map);
                        setCargo(cargo);

                        /* Go place the tool at the flag */
                        state = GOING_TO_FLAG_WITH_CARGO;

                        setTarget(home.getFlag().getPosition());

                        home.getFlag().promiseCargo(getCargo());
                    }
                } else {
                    countdown.step();
                }
            } else {

                /* Report the that the brewer was unproductive */
                productivityMeasurer.reportUnproductivity();
            }
        } else if (state == DEAD) {
            if (countdown.hasReachedZero()) {
                map.removeWorker(this);
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() {
        if (state == State.GOING_TO_FLAG_WITH_CARGO) {
            Flag flag = map.getFlagAtPoint(position);

            Cargo cargo = getCargo();

            cargo.setPosition(position);
            cargo.transportToStorage();

            flag.putCargo(getCargo());

            setCargo(null);

            state = State.GOING_BACK_TO_HOUSE;

            returnHome();
        } else if (state == State.GOING_BACK_TO_HOUSE) {
            enterBuilding(home);

            state = RESTING_IN_HOUSE;

            countdown.countFrom(RESTING_TIME);
        } else if (state == RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(position);

            storehouse.depositWorker(this);
        } else if (state == State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, METALWORKER);

            if (storehouse != null) {

                state = RETURNING_TO_STORAGE;

                setTarget(storehouse.getPosition());
            } else {
                state = State.GOING_TO_DIE;

                Point point = findPlaceToDie();

                setOffroadTarget(point);
            }
        } else if (state == State.GOING_TO_DIE) {
            setDead();

            state = DEAD;

            countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
        }
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, METALWORKER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, getPlayer(), METALWORKER);

            if (storage != null) {
                state = RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            } else {
                Point point = findPlaceToDie();

                setOffroadTarget(point, position.downRight());

                state = GOING_TO_DIE;
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {

        /* Return to storage if the planned path no longer exists */
        if (state == WALKING_TO_TARGET &&
                map.isFlagAtPoint(position) &&
                !map.arePointsConnectedByRoads(position, getTarget())) {

            /* Don't try to enter upon arrival */
            clearTargetBuilding();

            /* Go back to the storage */
            returnToStorage();
        }
    }

    @Override
    public int getProductivity() {

        /* Measure productivity across the length of four rest-work periods */
        return (int)
                (((double)productivityMeasurer.getSumMeasured() /
                        (productivityMeasurer.getNumberOfCycles())) * 100);
    }

    @Override
    public void goToOtherStorage(Building building) {
        state = GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
