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
import static org.appland.settlers.model.Fisherman.State.GOING_TO_FLAG;
import static org.appland.settlers.model.Fisherman.State.IN_HOUSE_WITH_FISH;
import static org.appland.settlers.model.Fisherman.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Fisherman.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Fisherman.State.WALKING_TO_TARGET;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Fisherman extends Worker {
    private static final int TIME_TO_FISH = 19;
    private static final int TIME_TO_REST = 99;

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;

    private State  state;

    private Point getFishingSpot() {
        Iterable<Point> adjacentPoints = map.getPointsWithinRadius(getHome().getPosition(), 4);

        for (Point point : adjacentPoints) {
            if (map.isBuildingAtPoint(point)) {
                continue;
            }

            if (map.isStoneAtPoint(point)) {
                continue;
            }

            if (map.getAmountFishAtPoint(point) == 0) {
                continue;
            }

            if (!map.getTerrain().isNextToWater(point)) {
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

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_FISH,
        FISHING,
        GOING_BACK_TO_HOUSE_WITH_FISH,
        IN_HOUSE_WITH_FISH,
        GOING_TO_FLAG,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }

    public Fisherman(Player player, GameMap map) {
        super(player, map);

        state = WALKING_TO_TARGET;

        countdown = new Countdown();

        productivityMeasurer = new ProductivityMeasurer(TIME_TO_FISH + TIME_TO_REST);
    }

    public boolean isFishing() {
        return state == FISHING;
    }

    @Override
    protected void onEnterBuilding(Building building) {
        if (building instanceof Fishery) {
            setHome(building);
        }

        state = RESTING_IN_HOUSE;

        countdown.countFrom(TIME_TO_REST);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE && getHome().isProductionEnabled()) {
            if (!getHome().outOfNaturalResources()) {
                if (countdown.reachedZero()) {
                    Point point = getFishingSpot();

                    if (point == null) {

                        /* Report that there's no more fish */
                        getHome().reportNoMoreNaturalResources();

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
            if (countdown.reachedZero()) {

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
            state = GOING_TO_FLAG;

            setTarget(getHome().getFlag().getPosition());
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == GOING_OUT_TO_FISH) {
            state = FISHING;

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
            Storage storage = (Storage)map.getBuildingAtPoint(getPosition());

            storage.depositWorker(this);
        }
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = GameUtils.getClosestStorage(getPosition(), getPlayer());

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroad(getPlayer(), getPosition());

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws Exception {

        /* Return to storage if the planned path no longer exists */
        if (state == State.WALKING_TO_TARGET &&
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
                        (double)(productivityMeasurer.getNumberOfCycles())) * 100);
    }
}
