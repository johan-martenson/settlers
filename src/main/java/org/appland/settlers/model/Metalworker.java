package org.appland.settlers.model;

import java.util.Arrays;
import java.util.List;

import static org.appland.settlers.model.Material.AXE;
import static org.appland.settlers.model.Material.BOW;
import static org.appland.settlers.model.Material.CLEAVER;
import static org.appland.settlers.model.Material.CRUCIBLE;
import static org.appland.settlers.model.Material.FISHING_ROD;
import static org.appland.settlers.model.Material.IRON_BAR;
import static org.appland.settlers.model.Material.METALWORKER;
import static org.appland.settlers.model.Material.PICK_AXE;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.ROLLING_PIN;
import static org.appland.settlers.model.Material.SAW;
import static org.appland.settlers.model.Material.SCYTHE;
import static org.appland.settlers.model.Material.SHOVEL;
import static org.appland.settlers.model.Material.TONGS;
import static org.appland.settlers.model.Metalworker.State.DEAD;
import static org.appland.settlers.model.Metalworker.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Metalworker.State.GOING_TO_DIE;
import static org.appland.settlers.model.Metalworker.State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;
import static org.appland.settlers.model.Metalworker.State.GOING_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.Metalworker.State.MAKING_TOOL;
import static org.appland.settlers.model.Metalworker.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Metalworker.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Metalworker.State.WAITING_FOR_SPACE_ON_FLAG;
import static org.appland.settlers.model.Metalworker.State.WALKING_TO_TARGET;

@Walker(speed = 10)
public class Metalworker extends Worker {
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
    private static final List<Material> TOOLS = Arrays.asList(
            AXE,
            SHOVEL,
            PICK_AXE,
            FISHING_ROD,
            BOW,
            SAW,
            CLEAVER,
            ROLLING_PIN,
            CRUCIBLE,
            TONGS,
            SCYTHE);

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;

    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME    = 99;

    private State state;
    private Material currentTool;

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
        state = WALKING_TO_TARGET;

        productivityMeasurer = new ProductivityMeasurer(RESTING_TIME + PRODUCTION_TIME);

        currentTool = TOOLS.get(TOOLS.size() - 1);
    }

    private Material getNextTool(Material currentMaterial) {
        int index = TOOLS.indexOf(currentMaterial);

        if (index < TOOLS.size() - 1) {
            return TOOLS.get(index + 1);
        } else {
            return TOOLS.get(0);
        }
    }

    @Override
    protected void onEnterBuilding(Building building) {
        if (building instanceof Metalworks) {
            setHome(building);
        }

        state = RESTING_IN_HOUSE;
        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws InvalidRouteException {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.hasReachedZero()) {
                state = MAKING_TOOL;
                countdown.countFrom(PRODUCTION_TIME);

                productivityMeasurer.nextProductivityCycle();
            } else {
                countdown.step();
            }
        } else if (state == WAITING_FOR_SPACE_ON_FLAG) {

            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                Material nextTool = getNextTool(currentTool);
                currentTool = nextTool;

                Cargo cargo = new Cargo(nextTool, map);
                setCargo(cargo);

                /* Go place the tool at the flag */
                state = GOING_TO_FLAG_WITH_CARGO;

                setTarget(getHome().getFlag().getPosition());

                getHome().getFlag().promiseCargo();
            }

        } else if (state == MAKING_TOOL) {
            if (getHome().getAmount(PLANK) > 0 && getHome().getAmount(IRON_BAR) > 0 && getHome().isProductionEnabled()) {
                if (countdown.hasReachedZero()) {

                    /* Consume the ingredients */
                    getHome().consumeOne(PLANK);
                    getHome().consumeOne(IRON_BAR);

                    /* Report the production */
                    productivityMeasurer.reportProductivity();

                    /* Handle transportation of the produced tool */
                    if (!getHome().getFlag().hasPlaceForMoreCargo()) {
                        state = WAITING_FOR_SPACE_ON_FLAG;
                    } else {
                        Material nextTool = getNextTool(currentTool);
                        currentTool = nextTool;

                        Cargo cargo = new Cargo(nextTool, map);
                        setCargo(cargo);

                        /* Go place the tool at the flag */
                        state = GOING_TO_FLAG_WITH_CARGO;

                        setTarget(getHome().getFlag().getPosition());

                        getHome().getFlag().promiseCargo();
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
    protected void onArrival() throws InvalidRouteException {
        if (state == GOING_TO_FLAG_WITH_CARGO) {
            Flag flag = map.getFlagAtPoint(getPosition());

            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToStorage();

            flag.putCargo(getCargo());

            setCargo(null);

            state = GOING_BACK_TO_HOUSE;

            returnHome();
        } else if (state == GOING_BACK_TO_HOUSE) {
            enterBuilding(getHome());

            state = RESTING_IN_HOUSE;

            countdown.countFrom(RESTING_TIME);
        } else if (state == RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        } else if (state == GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, METALWORKER);

            if (storehouse != null) {

                state = RETURNING_TO_STORAGE;

                setTarget(storehouse.getPosition());
            } else {
                state = GOING_TO_DIE;

                Point point = findPlaceToDie();

                setOffroadTarget(point);
            }
        } else if (state == GOING_TO_DIE) {
            setDead();

            state = DEAD;

            countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
        }
    }

    @Override
    protected void onReturnToStorage() throws InvalidRouteException {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, METALWORKER);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), METALWORKER);

            if (storage != null) {
                state = RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            } else {
                Point point = findPlaceToDie();

                setOffroadTarget(point, getPosition().downRight());

                state = GOING_TO_DIE;
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws InvalidRouteException {

        /* Return to storage if the planned path no longer exists */
        if (state == WALKING_TO_TARGET &&
                map.isFlagAtPoint(getPosition()) &&
                !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter upon arrival */
            clearTargetBuilding();

            /* Go back to the storage */
            returnToStorage();
        }
    }

    @Override
    int getProductivity() {

        /* Measure productivity across the length of four rest-work periods */
        return (int)
                (((double)productivityMeasurer.getSumMeasured() /
                        (productivityMeasurer.getNumberOfCycles())) * 100);
    }

    @Override
    public void goToOtherStorage(Building building) throws InvalidRouteException {
        state = GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
