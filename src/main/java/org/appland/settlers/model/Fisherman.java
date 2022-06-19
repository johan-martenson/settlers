/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Fisherman.State.FISHING;
import static org.appland.settlers.model.Fisherman.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Fisherman.State.GOING_BACK_TO_HOUSE_WITH_FISH;
import static org.appland.settlers.model.Fisherman.State.GOING_OUT_TO_FISH;
import static org.appland.settlers.model.Fisherman.State.GOING_TO_DIE;
import static org.appland.settlers.model.Fisherman.State.GOING_TO_FLAG;
import static org.appland.settlers.model.Fisherman.State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;
import static org.appland.settlers.model.Fisherman.State.IN_HOUSE_WITH_FISH;
import static org.appland.settlers.model.Fisherman.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Fisherman.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Fisherman.State.WAITING_FOR_SPACE_ON_FLAG;
import static org.appland.settlers.model.Fisherman.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Material.FISHERMAN;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Fisherman extends Worker {
    private static final int TIME_TO_FISH = 19;
    private static final int TIME_TO_REST = 99;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
    private static final int FISHING_RADIUS = 8;

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;

    private State  state;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_FISH,
        FISHING,
        GOING_BACK_TO_HOUSE_WITH_FISH,
        IN_HOUSE_WITH_FISH,
        GOING_TO_FLAG,
        GOING_BACK_TO_HOUSE,
        WAITING_FOR_SPACE_ON_FLAG, GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE, GOING_TO_DIE, DEAD, RETURNING_TO_STORAGE
    }

    public Fisherman(Player player, GameMap map) {
        super(player, map);

        state = WALKING_TO_TARGET;

        countdown = new Countdown();

        productivityMeasurer = new ProductivityMeasurer(TIME_TO_FISH + TIME_TO_REST, null);
    }

    public boolean isFishing() {
        return state == FISHING;
    }

    @Override
    protected void onEnterBuilding(Building building) {
        if (building.isReady()) {
            state = RESTING_IN_HOUSE;

            countdown.countFrom(TIME_TO_REST);

            productivityMeasurer.setBuilding(building);
        }
    }

    @Override
    protected void onIdle() {
        if (state == RESTING_IN_HOUSE && getHome().isProductionEnabled()) {
            if (!getHome().isOutOfNaturalResources()) {
                if (countdown.hasReachedZero()) {

                    Point point = getFishingSpot();

                    if (point == null) {

                        /* Report that there's no more fish */
                        getHome().reportNoMoreNaturalResources();

                        getPlayer().reportNoMoreResourcesForBuilding(getHome());

                        return;
                    }

                    setOffroadTarget(point);

                    state = GOING_OUT_TO_FISH;
                } else {
                    countdown.step();
                }
            } else {

                /* Report that there was no fish available so the fisherman couldn't fish */
                productivityMeasurer.reportUnproductivity();
            }
        } else if (state == FISHING) {
            if (countdown.hasReachedZero()) {

                Cargo cargo = map.catchFishAtPoint(getPosition());

                setCargo(cargo);

                state = GOING_BACK_TO_HOUSE_WITH_FISH;
                returnHomeOffroad();

                /* Report that the fisherman produced a fish */
                productivityMeasurer.reportProductivity();
                productivityMeasurer.nextProductivityCycle();
            } else {
                countdown.step();
            }
        } else if (state == IN_HOUSE_WITH_FISH) {

            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                state = GOING_TO_FLAG;

                setTarget(getHome().getFlag().getPosition());

                getHome().getFlag().promiseCargo(getCargo());
            } else {
                state = WAITING_FOR_SPACE_ON_FLAG;
            }
        } else if (state == WAITING_FOR_SPACE_ON_FLAG) {

            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                state = GOING_TO_FLAG;

                setTarget(getHome().getFlag().getPosition());

                getHome().getFlag().promiseCargo(getCargo());
            }
        } else if (state == Fisherman.State.DEAD) {
            if (countdown.hasReachedZero()) {
                map.removeWorker(this);
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() {
        if (state == GOING_OUT_TO_FISH) {
            state = FISHING;

            map.reportWorkerStartedAction(this, WorkerAction.FISHING);

            countdown.countFrom(TIME_TO_FISH);
        } else if (state == GOING_BACK_TO_HOUSE) {
            state = RESTING_IN_HOUSE;

            enterBuilding(getHome());

            countdown.countFrom(TIME_TO_REST);
        } else if (state == GOING_BACK_TO_HOUSE_WITH_FISH) {
            enterBuilding(getHome());

            state = IN_HOUSE_WITH_FISH;
        } else if (state == GOING_TO_FLAG) {
            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToStorage();
            getHome().getFlag().putCargo(cargo);

            setCargo(null);

            returnHome();

            state = GOING_BACK_TO_HOUSE;
        } else if (state == RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        } else if (state == GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, FISHERMAN);

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

            state = State.DEAD;

            countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
        }
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, FISHERMAN);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), FISHERMAN);

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
    protected void onWalkingAndAtFixedPoint() {

        /* Return to storage if the planned path no longer exists */
        if (state == WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter the fishery upon arrival */
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
        state = GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }

    private Point getFishingSpot() {
        Iterable<Point> fishingArea = GameUtils.getHexagonAreaAroundPoint(getHome().getPosition(), FISHING_RADIUS, map);

        for (Point point : fishingArea) {

            MapPoint mapPoint = map.getMapPoint(point);

            if (mapPoint.isBuilding()) {
                continue;
            }

            if (mapPoint.isStone()) {
                continue;
            }

            if (map.getAmountFishAtPoint(point) == 0) {
                continue;
            }

            if (!map.isNextToAnyWater(point)) {
                continue;
            }

            /* Filter out points that the fisherman can't reach */
            if (map.findWayOffroad(getHome().getFlag().getPosition(), point, null) == null) {
                continue;
            }

            return point;
        }

        return null;
    }
}
