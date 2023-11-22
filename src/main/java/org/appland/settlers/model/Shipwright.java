package org.appland.settlers.model;

import java.util.List;
import java.util.Set;

import static org.appland.settlers.model.Material.*;

@Walker(speed = 10)
public class Shipwright extends Worker {
    private static final int TIME_TO_REST = 99;
    private static final int TIME_TO_HAMMER = 19;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
    private static final int TIME_TO_BUILD_SHIP = 99;
    private static final int TIME_TO_MAKE_BOAT = 50;
    private static final int PLANKS_NEEDED_FOR_BOAT = 2;
    private static final int PLANKS_NEEDED_FOR_SHIP = 4;

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;

    private State state;
    private Ship ship;
    private Shipyard shipyard;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_BUILD_SHIP,
        HAMMERING,
        GOING_BACK_TO_HOUSE,
        IN_HOUSE_WITH_CARGO,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        MAKING_BOAT,
        GOING_TO_FLAG_WITH_CARGO,
        RETURNING_TO_STORAGE
    }

    public Shipwright(Player player, GameMap map) {
        super(player, map);

        state = State.WALKING_TO_TARGET;
        countdown = new Countdown();

        productivityMeasurer = new ProductivityMeasurer(TIME_TO_REST + TIME_TO_BUILD_SHIP, null);
    }

    @Override
    protected void onEnterBuilding(Building building) {
        state = State.RESTING_IN_HOUSE;

        countdown.countFrom(TIME_TO_REST);

        productivityMeasurer.setBuilding(building);

        shipyard = (Shipyard) building;
    }

    @Override
    protected void onIdle() {

        if (state == State.RESTING_IN_HOUSE) {
            if (countdown.hasReachedZero() && getHome().isProductionEnabled()) {
                if (shipyard.isProducingShips()) {
                    if  (getHome().getAmount(PLANK) >= PLANKS_NEEDED_FOR_SHIP) {

                        Point pointToBuildShip = findPlaceToBuildShip();

                        if (pointToBuildShip != null) {
                            state = State.GOING_OUT_TO_BUILD_SHIP;

                            setOffroadTarget(pointToBuildShip);

                            getHome().consumeOne(PLANK);
                            getHome().consumeOne(PLANK);
                            getHome().consumeOne(PLANK);
                            getHome().consumeOne(PLANK);
                        } else {

                            /* Report that it's not possible to harvest or plant */
                            productivityMeasurer.reportUnproductivity();

                            return;
                        }
                    }
                } else {
                    state = State.MAKING_BOAT;

                    countdown.countFrom(TIME_TO_MAKE_BOAT);
                }
            } else if (getHome().isProductionEnabled()) {
                countdown.step();
            } else {

                /* Report that the shipwright isn't working (or resting) */
                productivityMeasurer.reportUnproductivity();
            }
        } else if (state == State.HAMMERING) {
            if (countdown.hasReachedZero()) {
                state = State.GOING_BACK_TO_HOUSE;

                returnHomeOffroad();
            } else {
                countdown.step();
            }
        } else if (state == State.IN_HOUSE_WITH_CARGO) {

            if (getHome().getFlag().hasPlaceForMoreCargo()) {

                setTarget(getHome().getFlag().getPosition());

                state = State.GOING_TO_FLAG_WITH_CARGO;

                /* Tell the flag that the cargo will be delivered */
                getHome().getFlag().promiseCargo(getCargo());
            } else {
                state = State.WAITING_FOR_SPACE_ON_FLAG;
            }
        } else if (state == State.WAITING_FOR_SPACE_ON_FLAG) {
            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                Cargo cargo = new Cargo(BOAT, map);

                setCargo(cargo);

                state = State.GOING_TO_FLAG_WITH_CARGO;

                setTarget(getHome().getFlag().getPosition());

                /* Tell the flag that the cargo will be delivered */
                getHome().getFlag().promiseCargo(getCargo());
            }
        } else if (state == State.DEAD) {
            if (countdown.hasReachedZero()) {
                map.removeWorker(this);
            } else {
                countdown.step();
            }
        } else if (state == State.MAKING_BOAT) {
            if (countdown.hasReachedZero()) {
                if (getHome().getAmount(PLANK) >= PLANKS_NEEDED_FOR_BOAT) {

                    /* Report that the shipwright produced a boat */
                    productivityMeasurer.reportProductivity();
                    productivityMeasurer.nextProductivityCycle();

                    /* Consume the planks */
                    getHome().consumeOne(PLANK);
                    getHome().consumeOne(PLANK);

                    /* Handle transportation */
                    if (getHome().getFlag().hasPlaceForMoreCargo()) {
                        Cargo cargo = new Cargo(BOAT, map);

                        setCargo(cargo);

                        /* Go out to the flag to deliver the water */
                        setTarget(getHome().getFlag().getPosition());

                        state = State.GOING_TO_FLAG_WITH_CARGO;

                        getHome().getFlag().promiseCargo(getCargo());
                    } else {
                        state = State.WAITING_FOR_SPACE_ON_FLAG;
                    }
                } else {

                    /* Report the that the shipwright was unproductive */
                    productivityMeasurer.reportUnproductivity();
                }
            } else {
                countdown.step();
            }
        }
    }

    private Point findPlaceToBuildShip() {
        Set<Point> largeSurroundingArea = GameUtils.getHexagonAreaAroundPoint(getHome().getPosition(), 8, map);

        /* Find points that are on the water's edge */
        for (Point point : largeSurroundingArea) {
            List<DetailedVegetation> surroundingVegetation = map.getSurroundingTiles(point);

            /* Filter points that are not on the water's edge */
            if (!GameUtils.isSomeButNotAll(surroundingVegetation, DetailedVegetation.WATER)) {
                continue;
            }

            /* Filter points that can't be reached */
            // TODO: test that shipwright doesn't pick point it cannot go to
            if (map.findWayOffroad(getHome().getFlag().getPosition(), point, null) == null) {
                continue;
            }

            return point;
        }

        return null;
    }

    private boolean isBoatReceiver(Building building) {
        if (building.isReady() && building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(BOAT);
        }

        // TODO: add direct delivery to roads in shallow water

        return false;
    }

    @Override
    public void onArrival() {

        if (state == State.GOING_TO_FLAG_WITH_CARGO) {

            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToReceivingBuilding(this::isBoatReceiver);
            getHome().getFlag().putCargo(cargo);

            setCargo(null);

            state = State.GOING_BACK_TO_HOUSE;

            setTarget(getHome().getPosition());
        } else if (state == State.GOING_BACK_TO_HOUSE) {
            state = State.RESTING_IN_HOUSE;

            enterBuilding(getHome());

            countdown.countFrom(TIME_TO_REST);
        } else if (state == State.GOING_OUT_TO_BUILD_SHIP) {
            ship = map.placeShip(getPlayer(), getPosition());

            state = State.HAMMERING;

            countdown.countFrom(TIME_TO_HAMMER);
        } else if (state == State.RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        } else if (state == State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, SHIPWRIGHT);

            if (storehouse != null) {
                state = State.RETURNING_TO_STORAGE;

                setTarget(storehouse.getPosition());
            } else {
                state = State.GOING_TO_DIE;

                Point point = findPlaceToDie();

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
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, SHIPWRIGHT);

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), SHIPWRIGHT);

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            } else {
                Point point = findPlaceToDie();

                setOffroadTarget(point, getPosition().downRight());

                state = State.GOING_TO_DIE;
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {

        /* Return to storage if the planned path no longer exists */
        if (state == State.WALKING_TO_TARGET &&
                map.isFlagAtPoint(getPosition()) &&
                !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter the shipyard upon arrival */
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
    public void goToOtherStorage(Building building) {
        state = State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }

    public boolean isHammering() {
        return state == State.HAMMERING;
    }
}
