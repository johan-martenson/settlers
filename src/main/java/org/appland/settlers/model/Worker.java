/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.appland.settlers.model.Worker.States.IDLE_INSIDE;
import static org.appland.settlers.model.Worker.States.IDLE_OUTSIDE;
import static org.appland.settlers.model.Worker.States.WALKING_AND_EXACTLY_AT_POINT;
import static org.appland.settlers.model.Worker.States.WALKING_BETWEEN_POINTS;

/**
 *
 * @author johan
 */
public abstract class Worker implements Actor {
    private final Player player;

    enum States {
        WALKING_AND_EXACTLY_AT_POINT,
        WALKING_BETWEEN_POINTS,
        IDLE_OUTSIDE,
        IDLE_INSIDE,
        WALKING_HALF_WAY,
        WALKING_HALFWAY_AND_EXACTLY_AT_POINT,
        IDLE_HALF_WAY
    }

    private final static int SPEED_ADJUST = 1;
    private final static Logger log = Logger.getLogger(Worker.class.getName());
    private final Countdown     walkCountdown;

    GameMap     map;

    private List<Point> path;
    private States      state;
    private Cargo       carriedCargo;
    private Building    buildingToEnter;
    private Point       position;
    private Point       target;
    private Building    home;

    class ProductivityMeasurer {
        private final int   cycleLength;
        private int   currentProductivityMeasurement;
        private int   productionCycle;
        private final int[] productiveTime;
        private int   currentUnproductivityMeasurement;

        ProductivityMeasurer(int cycleLength) {
            this.cycleLength = cycleLength;
            this.currentProductivityMeasurement = 0;
            this.productiveTime = new int[] {0, 0, 0, 0};
            this.productionCycle = 0;
            this.currentUnproductivityMeasurement = 0;
        }

        void nextProductivityCycle() {

        /* Store the productivity measurement */
            productiveTime[productionCycle] = currentProductivityMeasurement;

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
            return  measuredLength >= cycleLength;
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
    }

    @Override
    public void stepTime() throws Exception {

        if (state == WALKING_AND_EXACTLY_AT_POINT) {

            /* Arrival at target is already handled so in this branch the
             * worker is at a fixed point but not the target point
             * */

            /* Start the next part of the road */
            walkCountdown.countFrom(getSpeed() - SPEED_ADJUST);

            state = WALKING_BETWEEN_POINTS;
        } else if (state == WALKING_BETWEEN_POINTS) {
            walkCountdown.step();

            if (walkCountdown.reachedZero()) {

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

    void onArrival() throws Exception {
        log.log(Level.FINE, "On handle hook arrival with nothing to do");
    }

    void onIdle() throws Exception {
        log.log(Level.FINE, "On idle hook with nothing to do");
    }

    void onEnterBuilding(Building building) throws Exception {
        log.log(Level.FINE, "On enter building hook with nothing to do");
    }

    private void handleArrival() throws Exception {
        log.log(Level.FINE, "Arrived at target: {0}", target);

        /* If there is a building set as target, enter it */
        if (getTargetBuilding() != null) {
            Building building = getTargetBuilding();

            /* Enter the building for non-military workers. Militaries enter on their own */
            if ( !(this instanceof Military)) {

                /* Go back to storage if the building is not ok to enter */
                if (building.burningDown() || building.destroyed()) {

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
        onArrival();
    }

    public void setPosition(Point point) {
        position = point;
    }

    public Point getPosition() {
        return position;
    }

    public boolean isArrived() {
        log.log(Level.FINE, "Checking if worker has arrived");
        log.log(Level.FINER, "Worker is at {0} and target is {1}", new Object[]{position, target});

        if (state == IDLE_INSIDE || state == IDLE_OUTSIDE) {
            return true;
        }

        log.log(Level.FINER, "Worker has not arrived at target");
        return false;
    }

    public void setMap(GameMap map) {
        log.log(Level.FINE, "Setting map to {0}", map);
        this.map = map;
    }

    public boolean isTraveling() {
        return state == WALKING_AND_EXACTLY_AT_POINT || state == WALKING_BETWEEN_POINTS;
    }

    private int getSpeed() {
        Walker walker = this.getClass().getAnnotation(Walker.class);

        return walker.speed();
    }

    public void setTargetBuilding(Building building) throws Exception {
        buildingToEnter = building;
        setTarget(building.getPosition());
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

    public Point getNextPoint() throws Exception {
        if (path == null || path.isEmpty()) {
            throw new Exception("No next point set. Target is " + getTarget());
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

        return (int)(((double)(getSpeed() - walkCountdown.getCount()) / (double)getSpeed()) * 100);
    }

    public void enterBuilding(Building building) throws Exception {
        if (!getPosition().equals(building.getPosition())) {
            throw new Exception("Can't enter " + building + " when worker is at " + getPosition());
        }

        state = IDLE_INSIDE;

        home = building;

        /* Allow subclasses to add logic */
        onEnterBuilding(building);
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

    void setOffroadTarget(Point point) throws Exception {
        setOffroadTarget(point, null);
    }

    void setOffroadTarget(Point point, Point via) throws Exception {
        boolean wasInside = false;

        log.log(Level.FINE, "Setting {0} as offroad target, via {1}", new Object[] {point, via});

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

            log.log(Level.FINER, "Way to target is {0}", path);
            state = WALKING_AND_EXACTLY_AT_POINT;
        }
    }

    void setTarget(Point point) throws Exception {
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

    void setTarget(Point point, Point via) throws Exception {
        log.log(Level.FINE, "Setting {0} as target, via {1}", new Object[] {point, via});

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

            log.log(Level.FINE, "Way to target is {0}", path);
            state = WALKING_AND_EXACTLY_AT_POINT;
        }
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

    void returnHomeOffroad() throws Exception {
        setOffroadTarget(home.getPosition(), home.getFlag().getPosition());
    }

    void returnHome() throws Exception {
        if (getPosition().equals(home.getFlag().getPosition())) {
            setTarget(home.getPosition());
        } else {
            setTarget(home.getPosition(), home.getFlag().getPosition());
        }
    }

    void setHome(Building building) {
        home = building;
    }

    void returnToStorage() throws Exception {
        onReturnToStorage();
    }

    void onReturnToStorage() throws Exception {

    }

    public Player getPlayer() {
        return player;
    }

    void onWalkingAndAtFixedPoint() throws Exception {}

    void walkHalfWayOffroadTo(Point point) throws Exception {

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
        state = States.WALKING_BETWEEN_POINTS;
    }

    GameMap getMap() {
        return map;
    }

    void cancelWalkingToTarget() {
        state = States.IDLE_OUTSIDE;
    }

    void clearTargetBuilding() {
        this.buildingToEnter = null;
    }

    int getProductivity() {
        return 0;
    }
}

