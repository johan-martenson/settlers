package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Building;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.appland.settlers.model.GameUtils.calculateAngle;
import static org.appland.settlers.model.Material.SCOUT;
import static org.appland.settlers.model.actors.Scout.State.*;
import static org.appland.settlers.utils.StatsConstants.AGGREGATED_EACH_STEP_TIME_GROUP;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Scout extends Worker {
    private static final int DISCOVERY_RADIUS = 4;
    private static final int LOOKOUT_TOWER_DISCOVER_RADIUS = 9;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
    private static final int MINIMUM_DISTANCE_TO_BORDER = 6;
    private static final Random RANDOM = new Random(4);
    private static final int LENGTH_TO_WALK = 30;

    protected enum State {
        WALKING_TO_TARGET,
        GOING_TO_NEXT_POINT,
        RETURNING_TO_FLAG,
        WALKING_TO_ASSIGNED_LOOKOUT_TOWER,
        WORKING_IN_LOOKOUT_TOWER,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        RETURNING_TO_STORAGE
    }

    private final Countdown countdown = new Countdown();

    private State state = WALKING_TO_TARGET;
    private Point flagPoint;
    private int   segmentCount = 0;
    private int   directionX;
    private int   directionY;
    private Point previousPosition;

    public Scout(Player player, GameMap map) {
        super(player, map);
    }

    @Override
    protected void onIdle() {
         if (state == DEAD) {
            if (countdown.hasReachedZero()) {
                map.removeWorker(this);
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() {
        var stats = map.getStats();
        var duration = stats.measureCumulativeDuration("Scout.onArrival", AGGREGATED_EACH_STEP_TIME_GROUP);

        map.discoverPointsWithinRadius(getPlayer(), position, DISCOVERY_RADIUS);

        switch (state) {
            case WALKING_TO_ASSIGNED_LOOKOUT_TOWER -> {
                enterBuilding(getTargetBuilding());
                duration.after("Enter lookout tower");
            }

            case WALKING_TO_TARGET -> {
                flagPoint = position;
                var borderPoint = findDirectionToBorder();
                duration.after("Find direction to border");

                calculateDirection(borderPoint);
                duration.after("Calculate direction");

                avoidGoingTooCloseToBorder();
                duration.after("Avoid going too close to border");

                var point = findNextPoint();
                duration.after("Find next point");

                if (!map.isWithinMap(point)) {
                    state = RETURNING_TO_FLAG;
                    setOffroadTarget(flagPoint);
                } else {
                    state = GOING_TO_NEXT_POINT;
                    setOffroadTarget(point);
                }
                previousPosition = position;
                duration.after("Set offroad target 0");
            }

            case GOING_TO_NEXT_POINT -> {
                segmentCount++;

                if (segmentCount == LENGTH_TO_WALK) {
                    state = RETURNING_TO_FLAG;
                    setOffroadTarget(flagPoint);
                    return;
                }

                avoidGoingTooCloseToBorder();
                duration.after("Avoid going too close to border");

                var point = findNextPoint();
                duration.after("Find next point");

                if (point == null) {
                    state = RETURNING_TO_FLAG;
                    setOffroadTarget(flagPoint);
                } else {
                    state = GOING_TO_NEXT_POINT;
                    setOffroadTarget(point);
                }

                previousPosition = position;
                duration.after("Set offroad target 1");
            }

            case RETURNING_TO_FLAG -> {
                state = RETURNING_TO_STORAGE;
                var storage = GameUtils.getClosestStorageConnectedByRoads(flagPoint, getPlayer());
                duration.after("Find closest storage connected by roads");

                if (storage != null) {
                    setTarget(storage.getPosition());
                } else {
                    storage = GameUtils.getClosestStorageOffroad(getPlayer(), flagPoint);
                    setOffroadTarget(storage.getPosition(), storage.getFlag().getPosition());
                }

                duration.after("Set offroad target 2");
            }

            case RETURNING_TO_STORAGE -> {
                var storage = map.getBuildingAtPoint(position);
                storage.putCargo(new Cargo(SCOUT, map));
                enterBuilding(storage);
                map.removeWorker(this);
                duration.after("Enter storage");
            }

            case GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE -> {
                var storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, SCOUT);
                duration.after("Find closest storage connected by roads and delivery is possible");

                if (storehouse != null) {
                    state = RETURNING_TO_STORAGE;
                    setTarget(storehouse.getPosition());
                } else {
                    state = GOING_TO_DIE;
                    var point = findPlaceToDie();
                    setOffroadTarget(point);
                }

                duration.after("Set offroad target 3");
            }

            case GOING_TO_DIE -> {
                setDead();
                state = DEAD;
                countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
            }
        }

        duration.after("End of method");
        duration.report();
    }

    private void calculateDirection(Point borderPoint) {
        directionX = borderPoint.x - position.x;
        directionY = borderPoint.y - position.y;
    }

    private void avoidGoingTooCloseToBorder() {
        if ((position.x < MINIMUM_DISTANCE_TO_BORDER && directionX < 0) ||
            (map.getWidth() - position.x < MINIMUM_DISTANCE_TO_BORDER && directionX > 0)) {
            directionX = -directionX;
        }

        if ((position.y < MINIMUM_DISTANCE_TO_BORDER && directionY < 0) ||
            (map.getHeight() - position.y < MINIMUM_DISTANCE_TO_BORDER && directionY > 0)) {
            directionY = -directionY;
        }
    }

    @Override
    public void onSetTargetBuilding(Building building) {

        // If this is called it means that the scout will be assigned to a lookout tower and will not walk around exploring
        state = WALKING_TO_ASSIGNED_LOOKOUT_TOWER;
    }

    @Override
    protected void onEnterBuilding(Building building) {

        // Discover the area around the tower
        for (var point : GameUtils.getHexagonAreaAroundPoint(building.getPosition(), LOOKOUT_TOWER_DISCOVER_RADIUS, getMap())) {
            player.discover(point);
        }

        state = WORKING_IN_LOOKOUT_TOWER;
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {
        map.discoverPointsWithinRadius(getPlayer(), position, DISCOVERY_RADIUS);

        if (state == RETURNING_TO_STORAGE ||
            state == WALKING_TO_TARGET ||
            state == WALKING_TO_ASSIGNED_LOOKOUT_TOWER ||
            state == GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            // Return to storage if the planned path no longer exists
            if (map.isFlagAtPoint(position) && !map.arePointsConnectedByRoads(position, getTarget())) {
                returnToStorage();
            }

            // Return to storage if the planned path no longer exists
            if (state == WALKING_TO_ASSIGNED_LOOKOUT_TOWER &&
                    map.isFlagAtPoint(position) &&
                    !map.arePointsConnectedByRoads(position, getTarget())) {
                clearTargetBuilding();
                returnToStorage();
            }
        }
    }

    record EntityAndScore<T>(T entity, double score) {

        @Override
        public String toString() {
                return entity + ", " + score;
            }
        }

    private Point findNextPoint() {

        // 1. Get list of available next adjacent off-road points
        Collection<Point> possibleAdjacentNextSteps = map.getPossibleAdjacentOffRoadConnections(position);

        // 2. Calculate scores for each point based on how closely they match the existing direction
        List<EntityAndScore<Point>> pointsAndScores = new ArrayList<>();

        double angleForExistingDirection = calculateAngle(directionX, directionY);

        for (var point : possibleAdjacentNextSteps) {
            var candidateDirectionX = point.x - position.x;
            var candidateDirectionY = point.y - position.y;

            var angleForCandidateDirection = calculateAngle(candidateDirectionX, candidateDirectionY);

            // Calculate score - lower is better
            // FIXME: this doesn't correctly score angles that are close but on opposite side of the positive X axis
            var score = Math.abs(angleForExistingDirection - angleForCandidateDirection);

            pointsAndScores.add(new EntityAndScore<>(point, score));
        }

        // 3. Sort by how closely they match the existing direction
        pointsAndScores.sort(Comparator.comparingDouble(pointEntityAndScore -> pointEntityAndScore.score));

        // 4. Go through the points and select which one to pick
        for (int i = 0; i < pointsAndScores.size(); i++) {
            var point = pointsAndScores.get(i).entity;
            boolean isLastOption = i == pointsAndScores.size() - 1;

            // Don't go backwards unless it's the only option
            if (point.equals(previousPosition) && !isLastOption) {
                continue;
            }

            // If it's the last option - take it
            if (isLastOption) {
                return point;
            }

            // Select the point in the right direction most of the time but sometimes skip to a less suitable point
            if (RANDOM.nextInt(11) < 8) {
                return point;
            }
        }

        // 5. Return null if there is no point to go to
        return null;
    }

    private Point findDirectionToBorder() {
        return getPlayer().getBorderPoints().stream()
                .min(Comparator.comparingDouble(position::distance))
                .orElse(null);
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, SCOUT);
        clearTargetBuilding();

        if (storage != null) {
            state = RETURNING_TO_STORAGE;
            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, getPlayer(), SCOUT);

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
    public void goToOtherStorage(Building building) {
        state = GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;
        setTarget(building.getFlag().getPosition());
    }
}
