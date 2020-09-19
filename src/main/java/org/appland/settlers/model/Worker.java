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

import static org.appland.settlers.model.Worker.States.IDLE_INSIDE;
import static org.appland.settlers.model.Worker.States.IDLE_OUTSIDE;
import static org.appland.settlers.model.Worker.States.WALKING_AND_EXACTLY_AT_POINT;
import static org.appland.settlers.model.Worker.States.WALKING_BETWEEN_POINTS;

/**
 *
 * @author johan
 */
public abstract class Worker {

    enum States {
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

    private boolean     dead;
    private List<Point> path;
    private States      state;
    private Cargo       carriedCargo;
    private Building    buildingToEnter;
    private Point       position;
    private Point       target;
    private Building    home;

    final GameMap map;

    static class ProductivityMeasurer {
        private final int   cycleLength;
        private Building building;
        private int   currentProductivityMeasurement;
        private int   productionCycle;
        private final int[] productiveTime;
        private int   currentUnproductivityMeasurement;

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
        this.player     = player;
        target          = null;
        position        = null;
        path            = null;
        buildingToEnter = null;
        home            = null;
        this.map        = map;

        walkCountdown  = new Countdown();

        state = IDLE_OUTSIDE;

        dead = false;
    }

    public void stepTime() throws InvalidRouteException, InvalidUserActionException {

        Stats stats = map.getStats();

        String counterName = "Worker." + getClass().getSimpleName() + ".stepTime";

        stats.createVariableGroupIfAbsent(StatsConstants.AGGREGATED_EACH_STEP_TIME_GROUP);
        stats.addPeriodicCounterVariableIfAbsent(counterName);
        stats.addVariableToGroup(counterName, StatsConstants.AGGREGATED_EACH_STEP_TIME_GROUP);

        Duration duration = new Duration(counterName);

        if (state == WALKING_AND_EXACTLY_AT_POINT) {

            /* Arrival at target is already handled so in this branch the worker is at a fixed point but not the target point */

            /* Start the next part of the road */
            walkCountdown.countFrom(getSpeed() - SPEED_ADJUST);

            state = WALKING_BETWEEN_POINTS;

        } else if (state == WALKING_BETWEEN_POINTS) {
            walkCountdown.step();

            if (walkCountdown.hasReachedZero()) {

                state = WALKING_AND_EXACTLY_AT_POINT;

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
                    state = IDLE_OUTSIDE;

                    /* Enter buildings if required and give the sub classes a change to act */
                    handleArrival();
                }
            }
        } else if (state == States.WALKING_HALFWAY_AND_EXACTLY_AT_POINT) {

            walkCountdown.countFrom(getSpeed() - SPEED_ADJUST);

            state = States.WALKING_HALF_WAY;
        } else if (state == States.WALKING_HALF_WAY) {

            walkCountdown.step();

            /* The worker has walked half way */
            if (getPercentageOfDistanceTraveled() >= 50) {
                onWalkedHalfWay();

                state = States.IDLE_HALF_WAY;
            }
        } else if (state == IDLE_OUTSIDE) {
            onIdle();
        } else if (state == IDLE_INSIDE) {
            onIdle();
        } else if (state == States.IDLE_HALF_WAY) {
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
                str = "Courier at " + getPosition() + " traveling to " + target;
            } else {
                str = "Courier latest at " + getLastPoint() + " traveling to " + target;
            }

            if (buildingToEnter != null) {
                str += " for " + buildingToEnter;
            }

            if (carriedCargo != null) {
                str += " carrying " + carriedCargo;
            }

            return str;
        }

        return "Idle courier at " + getPosition();
    }

    void onArrival() throws InvalidRouteException, InvalidUserActionException {
    }

    void onIdle() throws InvalidRouteException, InvalidUserActionException {
    }

    void onEnterBuilding(Building building) throws InvalidRouteException {
    }

    private void handleArrival() throws InvalidRouteException {

        /* If there is a building set as target, enter it */
        if (getTargetBuilding() != null) {
            Building building = getTargetBuilding();

            /* Enter the building for non-military workers. soldiers enter on their own */
            if ( !isMilitary()) {

                /* Go back to storage if the building is not ok to enter */
                if (building.isBurningDown() || building.isDestroyed()) {

                    buildingToEnter = null;

                    returnToStorage();

                    return;

                /* Enter the building */
                } else {
                    building.assignWorker(this);
                    enterBuilding(building);
                }
            }

            buildingToEnter = null;
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

    boolean isMilitary() {
        return false;
    }

    public void setPosition(Point point) {
        position = point;
    }

    public Point getPosition() {
        return position;
    }

    public boolean isArrived() {

        if (state == IDLE_INSIDE || state == IDLE_OUTSIDE) {
            return true;
        }

        return false;
    }

    public boolean isTraveling() {
        return state == WALKING_AND_EXACTLY_AT_POINT || state == WALKING_BETWEEN_POINTS;
    }

    private int getSpeed() {
        Walker walker = getClass().getAnnotation(Walker.class);

        return walker.speed();
    }

    public void setTargetBuilding(Building building) throws InvalidRouteException {
        buildingToEnter = building;
        setTarget(building.getPosition());

        /* Let sub classes add logic */
        onSetTargetBuilding(building);
    }

    void onSetTargetBuilding(Building building) {
    }

    public Building getTargetBuilding() {
        return buildingToEnter;
    }

    public boolean isExactlyAtPoint() {
        return state != WALKING_BETWEEN_POINTS && state != States.WALKING_HALF_WAY && state != States.IDLE_HALF_WAY;
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
        if (state != WALKING_BETWEEN_POINTS  &&
            state != States.WALKING_HALF_WAY &&
            state != States.WALKING_HALFWAY_AND_EXACTLY_AT_POINT &&
            state != States.IDLE_HALF_WAY) {
            return 100;
        }

        return (int)(((double)(getSpeed() - walkCountdown.getCount()) / getSpeed()) * 100);
    }

    public void enterBuilding(Building building) throws InvalidRouteException {
        if (!getPosition().equals(building.getPosition())) {
            throw new InvalidGameLogicException("Can't enter " + building + " when worker is at " + getPosition());
        }

        state = IDLE_INSIDE;

        home = building;

        /* Allow subclasses to add logic */
        onEnterBuilding(building);

        /* Report that the worker entered a building */
        map.reportWorkerEnteredBuilding(this);
    }

    public boolean isInsideBuilding() {
        return state == IDLE_INSIDE;
    }

    public boolean isAt(Point point) {
        return isExactlyAtPoint() && position.equals(point);
    }

    public Cargo getCargo() {
        return carriedCargo;
    }

    void setOffroadTarget(Point point) throws InvalidRouteException {
        setOffroadTarget(point, null);
    }

    // FIXME: HOTSPOT - allocations
    void setOffroadTarget(Point point, Point via) throws InvalidRouteException {
        boolean wasInside = false;

        target = point;

        if (state == IDLE_INSIDE) {
            wasInside = true;
        }

        if (position.equals(point)) {
            state = IDLE_OUTSIDE;

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

            state = WALKING_AND_EXACTLY_AT_POINT;

            /* Report the new target so it can be monitored */
            getMap().reportWorkerWithNewTarget(this);
        }
    }

    protected void setOffroadTargetWithPath(List<Point> pathToWalk) throws InvalidRouteException {
        target = pathToWalk.get(pathToWalk.size() - 1);
        path = pathToWalk;

        if (position.equals(target)) {
            state = IDLE_OUTSIDE;

            handleArrival();
        } else {
            state = WALKING_AND_EXACTLY_AT_POINT;

            /* Report the new target so it can be monitored */
            getMap().reportWorkerWithNewTarget(this);
        }
    }

    void setTargetWithPath(List<Point> pathToWalk) throws InvalidRouteException {
        target = pathToWalk.get(pathToWalk.size() - 1);
        path = new ArrayList<>(pathToWalk);

        path.remove(0);

        if (position.equals(target)) {
            state = IDLE_OUTSIDE;

            handleArrival();
        } else {
            state = WALKING_AND_EXACTLY_AT_POINT;

            /* Report the new target so it can be monitored */
            getMap().reportWorkerWithNewTarget(this);
        }
    }

    void setTarget(Point point) throws InvalidRouteException {
        if (state == IDLE_INSIDE) {
            if (!point.equals(home.getFlag().getPosition())) {
                setTarget(point, home.getFlag().getPosition());
            } else {
                setTarget(point, null);
            }
        } else {
            setTarget(point, null);
        }
    }

    void setTarget(Point point, Point via) throws InvalidRouteException {

        target = point;

        if (position.equals(point)) {
            state = IDLE_OUTSIDE;

            handleArrival();
        } else {
            Point start = getPosition();

            if (via != null) {
                path = map.findWayWithExistingRoads(start, target, via);
            } else  {
                path = map.findWayWithExistingRoads(start, target);
            }

            if (path == null) {
                throw new InvalidRouteException("No way on existing roads from " + start + " to " + target);
            }

            /* Remove the current point from the path */
            path.remove(0);

            state = WALKING_AND_EXACTLY_AT_POINT;

            /* Report the new target so it can be monitored */
            getMap().reportWorkerWithNewTarget(this);
        }
    }

    void stopWalkingToTarget() {
        state = IDLE_OUTSIDE;

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

    void returnHomeOffroad() throws InvalidRouteException {
        setOffroadTarget(home.getPosition(), home.getFlag().getPosition());
    }

    void returnHome() throws InvalidRouteException {
        if (getPosition().equals(home.getFlag().getPosition())) {
            setTarget(home.getPosition());
        } else {
            setTarget(home.getPosition(), home.getFlag().getPosition());
        }
    }

    void setHome(Building building) {
        home = building;
    }

    void returnToStorage() throws InvalidRouteException {
        onReturnToStorage();
    }

    void onReturnToStorage() throws InvalidRouteException {

    }

    public Player getPlayer() {
        return player;
    }

    void onWalkingAndAtFixedPoint() throws InvalidRouteException {}

    void walkHalfWayOffroadTo(Point point) throws InvalidRouteException {

        /* Walk half way to the given target */
        setOffroadTarget(point);

        state = States.WALKING_HALFWAY_AND_EXACTLY_AT_POINT;
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
        state = WALKING_BETWEEN_POINTS;
    }

    GameMap getMap() {
        return map;
    }

    void cancelWalkingToTarget() {
        state = IDLE_OUTSIDE;
    }

    void clearTargetBuilding() {
        buildingToEnter = null;
    }

    int getProductivity() {
        return 0;
    }

    public void goToOtherStorage(Building building) throws InvalidRouteException {
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
}

