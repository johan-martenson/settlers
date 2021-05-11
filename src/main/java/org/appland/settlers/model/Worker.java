/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import org.appland.settlers.utils.Duration;
import org.appland.settlers.utils.Stats;
import org.appland.settlers.utils.StatsConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author johan
 */
public abstract class Worker {

    private enum State {
        WALKING_AND_EXACTLY_AT_POINT,
        WALKING_BETWEEN_POINTS,
        IDLE_OUTSIDE,
        IDLE_INSIDE,
        WALKING_HALF_WAY,
        WALKING_HALFWAY_AND_EXACTLY_AT_POINT,
        IDLE_HALF_WAY
    }

    private static final int SPEED_ADJUST = 1;

    protected final Player player;

    private final Countdown walkCountdown;

    final GameMap map;

    private boolean     dead;
    private List<Point> path;
    private State       state;
    private Cargo       carriedCargo;
    private Building    targetBuilding;
    private Point       position;
    private Point       target;
    private Building    home;

    static class ProductivityMeasurer {
        private final int   cycleLength;
        private final int[] productiveTime;

        private Building building;
        private int      currentProductivityMeasurement;
        private int      productionCycle;
        private int      currentUnproductivityMeasurement;

        ProductivityMeasurer(int cycleLength, Building building) {
            this.cycleLength = cycleLength;
            this.building = building;

            currentProductivityMeasurement = 0;
            productiveTime = new int[] {0, 0, 0, 0};
            productionCycle = 0;
            currentUnproductivityMeasurement = 0;
        }

        void nextProductivityCycle() {

            /* Store the productivity measurement */
            int productionMeasurementToBeReplaced = productiveTime[productionCycle];
            productiveTime[productionCycle] = currentProductivityMeasurement;

            /* Create a monitoring event if the productivity changed */
            if (productionMeasurementToBeReplaced != currentProductivityMeasurement && building != null) {
                building.getPlayer().reportChangedBuilding(building);
            }

            currentProductivityMeasurement = 0;
            currentUnproductivityMeasurement = 0;

            /* Sample the next production cycle */
            productionCycle++;

            if (productionCycle >= productiveTime.length) {
                productionCycle = 0;
            }
        }

        boolean isProductivityCycleReached() {
            int measuredLength = currentProductivityMeasurement + currentUnproductivityMeasurement;
            return measuredLength >= cycleLength;
        }

        void reportProductivity() {
            currentProductivityMeasurement++;
        }

        void reportUnproductivity() {
            currentUnproductivityMeasurement++;

            if (isProductivityCycleReached()) {
                nextProductivityCycle();
            }
        }

        int getSumMeasured() {
            int sum = 0;

            for (int measurement : productiveTime) {
                sum = sum + measurement;
            }

            return sum;
        }

        int getNumberOfCycles() {
            return productiveTime.length;
        }

        public void setBuilding(Building building) {
            this.building = building;
        }
    }

    Worker(Player player, GameMap map) {
        this.player    = player;
        target         = null;
        position       = null;
        path           = null;
        targetBuilding = null;
        home           = null;
        this.map       = map;

        walkCountdown  = new Countdown();

        state = State.IDLE_OUTSIDE;

        dead = false;
    }

    public void stepTime() throws InvalidUserActionException {

        Stats stats = map.getStats();

        String counterName = "Worker." + getClass().getSimpleName() + ".stepTime";

        stats.createVariableGroupIfAbsent(StatsConstants.AGGREGATED_EACH_STEP_TIME_GROUP);
        stats.addPeriodicCounterVariableIfAbsent(counterName);
        stats.addVariableToGroup(counterName, StatsConstants.AGGREGATED_EACH_STEP_TIME_GROUP);

        Duration duration = new Duration(counterName);

        if (state == State.WALKING_AND_EXACTLY_AT_POINT) {

            /* Arrival at target is already handled so in this branch the worker is at a fixed point but not the target point */

            /* Start the next part of the road */
            walkCountdown.countFrom(getSpeed() - SPEED_ADJUST);

            state = State.WALKING_BETWEEN_POINTS;

        } else if (state == State.WALKING_BETWEEN_POINTS) {
            walkCountdown.step();

            if (walkCountdown.hasReachedZero()) {

                state = State.WALKING_AND_EXACTLY_AT_POINT;

                /* Update the worker's position */
                position = path.get(0);
                path.remove(0);

                /* Update the cargo's position */
                updateCargoPosition();

                /* Call the sub class to let it react */
                onWalkingAndAtFixedPoint();

                /* Handle the arrival if the worker is at the target */
                if (position.equals(target)) {

                    /* Set the state to idle and outside */
                    state = State.IDLE_OUTSIDE;

                    /* Enter buildings if required and give the sub classes a change to act */
                    handleArrival();
                }
            }
        } else if (state == State.WALKING_HALFWAY_AND_EXACTLY_AT_POINT) {

            walkCountdown.countFrom(getSpeed() - SPEED_ADJUST);

            state = State.WALKING_HALF_WAY;
        } else if (state == State.WALKING_HALF_WAY) {

            walkCountdown.step();

            /* The worker has walked half way */
            if (getPercentageOfDistanceTraveled() >= 50) {
                onWalkedHalfWay();

                state = State.IDLE_HALF_WAY;
            }
        } else if (state == State.IDLE_OUTSIDE) {
            onIdle();
        } else if (state == State.IDLE_INSIDE) {
            onIdle();
        } else if (state == State.IDLE_HALF_WAY) {
            onIdle();
        }

        duration.after("stepTime");

        map.getStats().reportVariableValue(counterName, duration.getFullDuration());
    }

    @Override
    public String toString() {
        if (isTraveling()) {
            String str;
            if (isExactlyAtPoint()) {
                str = "Worker at " + getPosition() + " traveling to " + target;
            } else {
                str = "Worker latest at " + getLastPoint() + " traveling to " + target;
            }

            if (targetBuilding != null) {
                str += " for " + targetBuilding;
            }

            if (carriedCargo != null) {
                str += " carrying " + carriedCargo;
            }

            return str;
        }

        return "Idle worker at " + getPosition();
    }

    void onArrival() throws InvalidUserActionException {
    }

    void onIdle() throws InvalidUserActionException {
    }

    void onEnterBuilding(Building building) {
    }

    private void handleArrival() {

        /* Just enter the storage and do nothing more */
        if (targetBuilding != null && targetBuilding instanceof Storehouse && targetBuilding.isOccupied()) {
            Storehouse storehouse = (Storehouse) targetBuilding;

            storehouse.depositWorker(this);

            return;
        }

        /* If there is a building set as target, enter it */
        if (getTargetBuilding() != null) {
            Building building = getTargetBuilding();

            /* Enter the building unless its a soldier or a builder.
             * Soldiers enter on their own and builders should not enter.
             * */
            if (!isSoldier() && !(this instanceof Builder)) {

                /* Go back to storage if the building is not ok to enter */
                if (building.isBurningDown() || building.isDestroyed()) {

                    targetBuilding = null;

                    returnToStorage();

                    return;

                /* Enter the building */
                } else {
                    building.assignWorker(this);
                    enterBuilding(building);
                }
            }

            targetBuilding = null;
        }

        /* This lets subclasses add their own logic */
        try {
            onArrival();
        } catch (InvalidUserActionException e) {
            InvalidGameLogicException invalidGameLogicException = new InvalidGameLogicException("");

            invalidGameLogicException.initCause(e);

            throw invalidGameLogicException;
        }
    }

    public boolean isSoldier() {
        return false;
    }

    public void setPosition(Point point) {
        position = point;
    }

    public Point getPosition() {
        return position;
    }

    public boolean isArrived() {

        if (state == State.IDLE_INSIDE || state == State.IDLE_OUTSIDE) {
            return true;
        }

        return false;
    }

    public boolean isTraveling() {
        return state == State.WALKING_AND_EXACTLY_AT_POINT || state == State.WALKING_BETWEEN_POINTS;
    }

    private int getSpeed() {
        Walker walker = getClass().getAnnotation(Walker.class);

        return walker.speed();
    }

    public void setTargetBuilding(Building building) {
        targetBuilding = building;
        setTarget(building.getPosition());

        /* Let sub classes add logic */
        onSetTargetBuilding(building);
    }

    void onSetTargetBuilding(Building building) {
    }

    public Building getTargetBuilding() {
        return targetBuilding;
    }

    public boolean isExactlyAtPoint() {
        return state != State.WALKING_BETWEEN_POINTS && state != State.WALKING_HALF_WAY && state != State.IDLE_HALF_WAY;
    }

    public Point getLastPoint() {
        return position;
    }

    public Point getNextPoint() {
        if (path == null || path.isEmpty()) {
            return null;
        }

        return path.get(0);
    }

    public int getPercentageOfDistanceTraveled() {
        if (state != State.WALKING_BETWEEN_POINTS  &&
            state != State.WALKING_HALF_WAY &&
            state != State.WALKING_HALFWAY_AND_EXACTLY_AT_POINT &&
            state != State.IDLE_HALF_WAY) {
            return 100;
        }

        return (int)(((double)(getSpeed() - walkCountdown.getCount()) / getSpeed()) * 100);
    }

    public void enterBuilding(Building building) {
        if (!getPosition().equals(building.getPosition())) {
            throw new InvalidGameLogicException("Can't enter " + building + " when worker is at " + getPosition());
        }

        state = State.IDLE_INSIDE;

        home = building;

        /* Allow subclasses to add logic */
        onEnterBuilding(building);

        /* Report that the worker entered a building */
        map.reportWorkerEnteredBuilding(this);
    }

    public boolean isInsideBuilding() {
        return state == State.IDLE_INSIDE;
    }

    public boolean isAt(Point point) {
        return isExactlyAtPoint() && position.equals(point);
    }

    public Cargo getCargo() {
        return carriedCargo;
    }

    void setOffroadTarget(Point point) {
        setOffroadTarget(point, null);
    }

    // FIXME: HOTSPOT - allocations
    void setOffroadTarget(Point point, Point via) {
        boolean wasInside = false;

        target = point;

        if (state == State.IDLE_INSIDE) {
            wasInside = true;
        }

        if (position.equals(point)) {
            state = State.IDLE_OUTSIDE;

            handleArrival();
        } else {
            if (wasInside && !target.equals(home.getFlag().getPosition())) {
                if (via != null) {
                    path = map.findWayOffroad(home.getFlag().getPosition(), point, via, null);
                } else {
                    path = map.findWayOffroad(home.getFlag().getPosition(), point, null);
                }

                path.add(0, home.getPosition());
            } else {
                if (via != null) {
                    path = map.findWayOffroad(getPosition(), point, via, null);
                } else {
                    path = map.findWayOffroad(getPosition(), point, null);
                }
            }

            path.remove(0);

            state = State.WALKING_AND_EXACTLY_AT_POINT;

            /* Report the new target so it can be monitored */
            getMap().reportWorkerWithNewTarget(this);
        }
    }

    protected void setOffroadTargetWithPath(List<Point> pathToWalk) {
        target = pathToWalk.get(pathToWalk.size() - 1);
        path = pathToWalk;

        if (position.equals(target)) {
            state = State.IDLE_OUTSIDE;

            handleArrival();
        } else {
            state = State.WALKING_AND_EXACTLY_AT_POINT;

            /* Report the new target so it can be monitored */
            getMap().reportWorkerWithNewTarget(this);
        }
    }

    void setTargetWithPath(List<Point> pathToWalk) {
        target = pathToWalk.get(pathToWalk.size() - 1);
        path = new ArrayList<>(pathToWalk);

        path.remove(0);

        if (position.equals(target)) {
            state = State.IDLE_OUTSIDE;

            handleArrival();
        } else {
            state = State.WALKING_AND_EXACTLY_AT_POINT;

            /* Report the new target so it can be monitored */
            getMap().reportWorkerWithNewTarget(this);
        }
    }

    void setTarget(Point point) {
        if (state == State.IDLE_INSIDE) {
            if (!point.equals(home.getFlag().getPosition())) {
                setTarget(point, home.getFlag().getPosition());
            } else {
                setTarget(point, null);
            }
        } else {
            setTarget(point, null);
        }
    }

    void setTarget(Point point, Point via) {

        target = point;

        if (position.equals(point)) {
            state = State.IDLE_OUTSIDE;

            handleArrival();
        } else {
            Point start = getPosition();

            if (via != null) {
                path = map.findWayWithExistingRoads(start, target, via);
            } else  {
                path = map.findWayWithExistingRoads(start, target);
            }

            if (path == null) {
                throw new InvalidGameLogicException("No way on existing roads from " + start + " to " + target);
            }

            /* Remove the current point from the path */
            path.remove(0);

            state = State.WALKING_AND_EXACTLY_AT_POINT;

            /* Report the new target so it can be monitored */
            getMap().reportWorkerWithNewTarget(this);
        }
    }

    void stopWalkingToTarget() {
        state = State.IDLE_OUTSIDE;

        path.clear();
        target = null;

        /* Report that this worker stopped walking */
        getMap().reportWorkerWithNewTarget(this);
    }

    public Point getTarget() {
        return target;
    }

    public Building getHome() {
        return home;
    }

    void setCargo(Cargo cargo) {
        carriedCargo = cargo;

        if (carriedCargo != null) {
            carriedCargo.setPosition(getPosition());
        }
    }

    private void updateCargoPosition() {
        if (carriedCargo != null) {
            carriedCargo.setPosition(getPosition());
        }
    }

    public List<Point> getPlannedPath() {
        return path;
    }

    void returnHomeOffroad() {
        setOffroadTarget(home.getPosition(), home.getFlag().getPosition());
    }

    void returnHome() {
        if (getPosition().equals(home.getFlag().getPosition())) {
            setTarget(home.getPosition());
        } else {
            setTarget(home.getPosition(), home.getFlag().getPosition());
        }
    }

    void setHome(Building building) {
        home = building;
    }

    void returnToStorage() {
        onReturnToStorage();
    }

    void onReturnToStorage() {

    }

    public Player getPlayer() {
        return player;
    }

    void onWalkingAndAtFixedPoint() {}

    void walkHalfWayOffroadTo(Point point) {

        /* Walk half way to the given target */
        setOffroadTarget(point);

        state = State.WALKING_HALFWAY_AND_EXACTLY_AT_POINT;
    }

    void onWalkedHalfWay() {}

    void returnToFixedPoint() {

        Point previousTarget = target;
        Point previousLastPoint = getLastPoint();

        /* Change the previous position to make the worker leave the previous target */
        position = previousTarget;

        /* Change the target to make the worker walk back */
        target = previousLastPoint;

        /* Change the planned path */
        path.clear();
        path.add(previousLastPoint);

        /* Set the state to be walking between two fixed points */
        state = State.WALKING_BETWEEN_POINTS;
    }

    GameMap getMap() {
        return map;
    }

    void cancelWalkingToTarget() {
        state = State.IDLE_OUTSIDE;
    }

    void clearTargetBuilding() {
        targetBuilding = null;
    }

    int getProductivity() {
        return 0;
    }

    public void goToOtherStorage(Building building) {
    }

    public boolean isDead() {
        return dead;
    }

    protected Point findPlaceToDie() {
        Collection<Point> area = GameUtils.getHexagonAreaAroundPoint(getPosition(), 8, map);

        for (Point point : area) {
            List<Point> path = map.findWayOffroad(getPosition(), point, null); // FIXME: this hides a field

            if (path == null) {
                continue;
            }

            if (path.isEmpty()) {
                continue;
            }

            return point;
        }

        return null;
    }

    protected void setDead() {
        dead = true;
    }

    void goToStorehouse(Storehouse storehouse) {
        setTarget(storehouse.getPosition());

        targetBuilding = storehouse;
    }
}

