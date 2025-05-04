package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.Direction;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidGameLogicException;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.OffroadOption;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.utils.Duration;
import org.appland.settlers.utils.StatsConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.lang.String.format;

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
    protected final GameMap map;

    protected Point     position = null;
    protected Building  home = null;
    protected Cargo     carriedCargo;
    protected Direction direction = Direction.DOWN_RIGHT;

    private final Countdown walkCountdown = new Countdown();

    private boolean     dead = false;
    private List<Point> path = null;
    private State       state = State.IDLE_OUTSIDE;
    private Building    targetBuilding = null;
    private Point       target = null;

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
            int previousMeasurement = productiveTime[productionCycle];
            productiveTime[productionCycle] = currentProductivityMeasurement;

            if (previousMeasurement != currentProductivityMeasurement && building != null) {
                building.getPlayer().reportChangedBuilding(building);
            }

            currentProductivityMeasurement = 0;
            currentUnproductivityMeasurement = 0;

            productionCycle = (productionCycle + 1) % productiveTime.length;
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
            return Arrays.stream(productiveTime).sum();
        }

        int getNumberOfCycles() {
            return productiveTime.length;
        }

        public void setBuilding(Building building) {
            this.building = building;
        }
    }

    Worker(Player player, GameMap map) {
        this.player = player;
        this.map = map;
    }

    public void stepTime() throws InvalidUserActionException {
        var stats = map.getStats();
        var counterName = format("Worker.%s.stepTime", getClass().getSimpleName());

        stats.createVariableGroupIfAbsent(StatsConstants.AGGREGATED_EACH_STEP_TIME_GROUP);
        stats.addPeriodicCounterVariableIfAbsent(counterName);
        stats.addVariableToGroup(counterName, StatsConstants.AGGREGATED_EACH_STEP_TIME_GROUP);

        var duration = new Duration(counterName);

        switch (state) {
            case WALKING_AND_EXACTLY_AT_POINT -> {
                // Arrival at target is already handled.
                // In this branch the worker is at a fixed point but not the target.

                walkCountdown.countFrom(getSpeed() - SPEED_ADJUST);

                var next = path.getFirst();

                if (position.equals(next)) {
                    throw new RuntimeException(format("They are the same! I am %s", this));
                }

                if (position.distance(next) > 2) {
                    throw new RuntimeException(format("Too big distance! I am %s", this));
                }

                direction = GameUtils.getDirectionBetweenPoints(position, next);
                state = State.WALKING_BETWEEN_POINTS;
            }

            case WALKING_BETWEEN_POINTS -> {
                walkCountdown.step();

                if (walkCountdown.hasReachedZero()) {
                    position = path.removeFirst();
                    var upLeft = position.upLeft();
                    updateCargoPosition();

                    state = State.WALKING_AND_EXACTLY_AT_POINT;
                    onWalkingAndAtFixedPoint();

                    // Handle the arrival if the worker is at the target
                    if (position.equals(target)) {
                        state = State.IDLE_OUTSIDE;
                        handleArrival();

                    // Open the door if the worker is about to go to a house
                    } else if (upLeft.equals(target)) {
                        var house = map.getBuildingAtPoint(upLeft);

                        if (house != null && house.isReady()) {
                            house.openDoor(20);
                        }
                    }
                }
            }

            case WALKING_HALFWAY_AND_EXACTLY_AT_POINT -> {
                walkCountdown.countFrom(getSpeed() - SPEED_ADJUST);
                state = State.WALKING_HALF_WAY;
            }

            case WALKING_HALF_WAY -> {
                walkCountdown.step();

                if (getPercentageOfDistanceTraveled() >= 50) {
                    onWalkedHalfWay();
                    state = State.IDLE_HALF_WAY;
                }
            }

            case IDLE_INSIDE, IDLE_OUTSIDE, IDLE_HALF_WAY -> onIdle();
        }

        duration.after("stepTime");

        stats.reportVariableValue(counterName, duration.getFullDuration());
    }

    @Override
    public String toString() {
        if (isTraveling()) {
            var builder = new StringBuilder();
            builder.append(isExactlyAtPoint()
                    ? format("Worker at %s traveling to %s", position, target)
                    : format("Worker latest at %s traveling to %s", getLastPoint(), target));

            if (targetBuilding != null) {
                builder.append(format(" for %s", targetBuilding));
            }

            if (carriedCargo != null) {
                builder.append(format(" carrying %s", carriedCargo));
            }

            return builder.toString();
        }

        return "Idle worker at %s".formatted(position);
    }

    protected void onArrival() throws InvalidUserActionException {
        // Empty method for subclasses to override if needed
    }

    protected void onIdle() throws InvalidUserActionException {
        // Empty method for subclasses to override if needed
    }

    protected void onEnterBuilding(Building building) {
        // Empty method for subclasses to override if needed
    }

    private void handleArrival() {
        if (targetBuilding instanceof Storehouse storehouse && targetBuilding.isOccupied()) {
            storehouse.depositWorker(this);
            return;
        }

        else if (targetBuilding != null) {

            /* Enter the building unless it's a soldier or a builder.
             * Soldiers enter on their own and builders should not enter.
             * */
            if (!isSoldier() && !(this instanceof Builder)) {
                Building building = targetBuilding;

                // Go back to storage if the building is not ok to enter
                if (building.isBurningDown() || building.isDestroyed()) {
                    targetBuilding = null;
                    returnToStorage();
                    return;

                // Enter the building
                } else {
                    building.assignWorker(this);
                    enterBuilding(building);
                }
            }

            targetBuilding = null;
        }

        // This lets subclasses add their own logic
        try {
            onArrival();
        } catch (InvalidUserActionException e) {
            throw new InvalidGameLogicException(format("Error during arrival %s", e));
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
        return state == State.IDLE_INSIDE || state == State.IDLE_OUTSIDE;
    }

    public boolean isTraveling() {
        return state == State.WALKING_AND_EXACTLY_AT_POINT || state == State.WALKING_BETWEEN_POINTS;
    }

    private int getSpeed() {
        return getClass().getAnnotation(Walker.class).speed();
    }

    public void setTargetBuilding(Building building) {
        targetBuilding = building;
        setTarget(building.getPosition());

        // Let sub classes add logic
        onSetTargetBuilding(building);
    }

    void onSetTargetBuilding(Building building) {
        // Empty method for subclasses to override if needed
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

        return path.getFirst();
    }

    public int getPercentageOfDistanceTraveled() {
        if (!Set.of(
                State.WALKING_BETWEEN_POINTS,
                State.WALKING_HALF_WAY,
                State.WALKING_HALFWAY_AND_EXACTLY_AT_POINT,
                State.IDLE_HALF_WAY
        ).contains(state)) {
            return 100;
        }

        return (int)(((double)(getSpeed() - walkCountdown.getCount()) / getSpeed()) * 100);
    }

    public void enterBuilding(Building building) {
        if (!position.equals(building.getPosition())) {
            throw new InvalidGameLogicException(format("Can't enter %s when worker is at %s", building, position));
        }

        state = State.IDLE_INSIDE;

        home = building;

        building.closeDoor();

        // Allow subclasses to add logic
        onEnterBuilding(building);

        // Report that the worker entered a building
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

    void setOffroadTarget(Point point, OffroadOption offroadOption) {
        setOffroadTarget(point, null, offroadOption);
    }

    void setOffroadTarget(Point point) {
        setOffroadTarget(point, null, null);
    }

    void setOffroadTarget(Point point, Point via) {
        setOffroadTarget(point, via, null);
    }

    // FIXME: HOTSPOT - allocations
    void setOffroadTarget(Point point, Point via, OffroadOption offroadOption) {
        boolean wasInside = false;

        target = point;

        if (state == State.IDLE_INSIDE && map.getBuildingAtPoint(position).isReady()) {
            wasInside = true;

            getHome().openDoor(10);
        }

        if (position.equals(point)) {
            state = State.IDLE_OUTSIDE;

            handleArrival();
        } else {
            if (wasInside && !target.equals(home.getFlag().getPosition())) {

                // Get from the flag to the target
                path = map.findWayOffroad(home.getFlag().getPosition(), via, point, null, offroadOption);

                // Add the initial step of going from the home to the flag
                path.addFirst(home.getPosition());
            } else {
                path = map.findWayOffroad(position, via, point, null, offroadOption);
            }

            // Remove the current position so the path only contains the steps to take
            path.removeFirst();

            state = State.WALKING_AND_EXACTLY_AT_POINT;

            direction = GameUtils.getDirection(position, path.getFirst());

            // Report the new target so it can be monitored
            map.reportWorkerWithNewTarget(this);
        }
    }

    protected void setOffroadTargetWithPath(List<Point> pathToWalk) {
        target = pathToWalk.getLast();
        path = pathToWalk;

        if (position.equals(target)) {
            state = State.IDLE_OUTSIDE;

            handleArrival();
        } else {

            if (!path.getFirst().equals(position)) {
                throw new RuntimeException("The path must start with the current position");
            }

            path.removeFirst();

            state = State.WALKING_AND_EXACTLY_AT_POINT;

            // Report the new target so it can be monitored
            map.reportWorkerWithNewTarget(this);
        }
    }

    void setTargetWithPath(List<Point> pathToWalk) {
        target = pathToWalk.getLast();
        path = new ArrayList<>(pathToWalk);

        if (!path.getFirst().equals(position)) {
            throw new RuntimeException("The path must start with the current position");
        }

        path.removeFirst();

        if (position.equals(target)) {
            state = State.IDLE_OUTSIDE;

            handleArrival();
        } else {
            state = State.WALKING_AND_EXACTLY_AT_POINT;

            direction = GameUtils.getDirectionBetweenPoints(position, path.getFirst());

            // Report the new target so it can be monitored
            map.reportWorkerWithNewTarget(this);
        }
    }

    public void setTarget(Point point) {
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
        this.target = point;

        if (state == State.IDLE_INSIDE) {
            if (!map.isBuildingAtPoint(position)) {
                System.out.println("No building at point!");
                System.exit(1);
            }

            if (map.getBuildingAtPoint(position).isReady()) {
                getHome().openDoor(10);
            }
        }

        var upLeft = position.upLeft();

        if (point.equals(upLeft)) {
            var house = map.getBuildingAtPoint(upLeft);

            if (house != null && house.isReady()) {
                house.openDoor();
            }
        }

        if (position.equals(point)) {
            state = State.IDLE_OUTSIDE;
            handleArrival();
        } else {
            path = (via == null)
                    ? map.findWayWithExistingRoads(position, target)
                    : map.findWayWithExistingRoads(position, target, via);

            if (path == null) {
                throw new InvalidGameLogicException("No way on existing roads from " + position + " to " + target);
            }

            path.removeFirst();
            state = State.WALKING_AND_EXACTLY_AT_POINT;
            direction = GameUtils.getDirectionBetweenPoints(position, path.getFirst());
            map.reportWorkerWithNewTarget(this);
        }
    }

    void stopWalkingToTarget() {
        state = State.IDLE_OUTSIDE;
        path.clear();
        target = null;
        map.reportWorkerWithNewTarget(this);
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
            carriedCargo.setPosition(position);
        }
    }

    private void updateCargoPosition() {
        if (carriedCargo != null) {
            carriedCargo.setPosition(position);
        }
    }

    public List<Point> getPlannedPath() {
        return path;
    }

    void returnHomeOffroad() {
        setOffroadTarget(home.getPosition(), home.getFlag().getPosition());
    }

    void returnHome() {
        if (position.equals(home.getFlag().getPosition())) {
            setTarget(home.getPosition());
        } else {
            setTarget(home.getPosition(), home.getFlag().getPosition());
        }
    }

    public void setHome(Building building) {
        home = building;
    }

    public void returnToStorage() {
        onReturnToStorage();
    }

    void onReturnToStorage() {
        // Empty method for subclasses to override if needed
    }

    public Player getPlayer() {
        return player;
    }

    void onWalkingAndAtFixedPoint() {
        // Empty method for subclasses to override if needed
    }

    void walkHalfWayOffroadTo(Point point, OffroadOption offroadOption) {

        // Walk halfway to the given target
        setOffroadTarget(point, OffroadOption.CAN_END_ON_STONE);

        state = State.WALKING_HALFWAY_AND_EXACTLY_AT_POINT;
    }

    void onWalkedHalfWay() {
        // Empty method for subclasses to override if needed
    }

    void returnToFixedPoint() {
        var previousTarget = target;
        var previousLastPoint = getLastPoint();

        // Change the previous position to make the worker leave the previous target
        position = previousTarget;

        // Change the target to make the worker walk back
        target = previousLastPoint;

        // Change the planned path
        path.clear();
        path.add(previousLastPoint);

        // Set the state to be walking between two fixed points
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

    public int getProductivity() {
        return 0;
    }

    public void goToOtherStorage(Building building) {
        // Empty method for subclasses to override if needed
    }

    public boolean isDead() {
        return dead;
    }

    protected Point findPlaceToDie() {
        Collection<Point> area = GameUtils.getHexagonAreaAroundPoint(position, 8, map);

        return area.stream()
                .map(point -> map.findWayOffroad(position, point, null))
                .filter(Objects::nonNull)
                .filter(path -> !path.isEmpty())
                .map(List::getLast)
                .findFirst()
                .orElse(null);
    }

    protected void setDead() {
        dead = true;
        map.placeDecoration(position, DecorationType.ANIMAL_SKELETON_1);
    }

    public void goToStorehouse(Storehouse storehouse) {
        setTarget(storehouse.getPosition());
        targetBuilding = storehouse;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isWorking() {
        return false;
    }
}

