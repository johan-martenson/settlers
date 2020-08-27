/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import org.appland.settlers.utils.CumulativeDuration;
import org.appland.settlers.utils.Stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.appland.settlers.model.Material.SCOUT;
import static org.appland.settlers.model.Scout.State.DEAD;
import static org.appland.settlers.model.Scout.State.GOING_TO_DIE;
import static org.appland.settlers.model.Scout.State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;
import static org.appland.settlers.model.Scout.State.GOING_TO_NEXT_POINT;
import static org.appland.settlers.model.Scout.State.RETURNING_TO_FLAG;
import static org.appland.settlers.model.Scout.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Scout.State.WALKING_TO_ASSIGNED_LOOKOUT_TOWER;
import static org.appland.settlers.model.Scout.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Scout.State.WORKING_IN_LOOKOUT_TOWER;
import static org.appland.settlers.utils.StatsConstants.AGGREGATED_EACH_STEP_TIME_GROUP;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Scout extends Worker {

    private static final int DISCOVERY_RADIUS = 4;
    private static final int LENGTH_TO_PLAN_HEAD = 7;
    private static final int LOOKOUT_TOWER_DISCOVER_RADIUS = 9;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;
    private static final int MINIMUM_DISTANCE_TO_BORDER = 6;
    private static final Random random = new Random(4);

    protected enum State {
        WALKING_TO_TARGET,
        GOING_TO_NEXT_POINT,
        RETURNING_TO_FLAG,
        WALKING_TO_ASSIGNED_LOOKOUT_TOWER, WORKING_IN_LOOKOUT_TOWER, GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE, GOING_TO_DIE, DEAD, RETURNING_TO_STORAGE
    }

    private final Countdown countdown;

    private State state;
    private Point flagPoint;
    private int   segmentCount;
    private int   directionX;
    private int   directionY;

    public Scout(Player player, GameMap map) {
        super(player, map);

        state = WALKING_TO_TARGET;
        segmentCount = 0;

        countdown = new Countdown();
    }

    @Override
    void onIdle() {
         if (state == DEAD) {
            if (countdown.hasReachedZero()) {
                map.removeWorker(this);
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() throws InvalidRouteException {
        Stats stats = map.getStats();

        CumulativeDuration duration = stats.measureCumulativeDuration("Scout.onArrival", AGGREGATED_EACH_STEP_TIME_GROUP);

        map.discoverPointsWithinRadius(getPlayer(), getPosition(), DISCOVERY_RADIUS);

        if (state == WALKING_TO_ASSIGNED_LOOKOUT_TOWER) {
            enterBuilding(getTargetBuilding());

            duration.after("Enter lookout tower");
        } else if (state == WALKING_TO_TARGET) {

            /* Remember where the scouting started so the scout can go back */
            flagPoint = getPosition();

            /* Calculate direction */
            Point borderPoint = findDirectionToBorder();

            duration.after("Find direction to border");

            calculateDirection(borderPoint);

            duration.after("Calculate direction");

            /* Update direction if the scout is too close to the border */
            avoidGoingTooCloseToBorder();

            duration.after("Avoid going too close to border");

            /* Find the first point to go to */
            Point point = findNextPoint();

            duration.after("Find next point");

            if (!map.isWithinMap(point)) {
                state = RETURNING_TO_FLAG;

                setOffroadTarget(flagPoint);
            } else {
                state = GOING_TO_NEXT_POINT;

                setOffroadTarget(point);
            }

            duration.after("Set offroad target 0");
        } else if (state == GOING_TO_NEXT_POINT) {

            segmentCount++;

            if (segmentCount == 8) {
                state = RETURNING_TO_FLAG;

                setOffroadTarget(flagPoint);

                return;
            }

            avoidGoingTooCloseToBorder();

            duration.after("Avoid going too close to border");

            Point point = findNextPoint();

            duration.after("Find next point");

            if (point == null) {
                state = RETURNING_TO_FLAG;

                setOffroadTarget(flagPoint);
            } else {
                state = GOING_TO_NEXT_POINT;

                setOffroadTarget(point);
            }

            duration.after("Set offroad target 1");
        } else if (state == RETURNING_TO_FLAG) {
            state = RETURNING_TO_STORAGE;

            // FIXME: add test and fix so returning scout can't go to storage where storage is not allowed
            Building storage = GameUtils.getClosestStorageConnectedByRoads(flagPoint, getPlayer());

            duration.after("Find closest storage connected by roads");

            if (storage != null) {
                setTarget(storage.getPosition());
            } else {
                storage = GameUtils.getClosestStorageOffroad(getPlayer(), flagPoint);

                setOffroadTarget(storage.getPosition(), storage.getFlag().getPosition());
            }

            duration.after("Set offroad target 2");
        } else if (state == RETURNING_TO_STORAGE) {
            Building storage = map.getBuildingAtPoint(getPosition());

            storage.putCargo(new Cargo(SCOUT, map));

            enterBuilding(storage);

            map.removeWorker(this);

            duration.after("Enter storage");
        } else if (state == GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, SCOUT);

            duration.after("Find closest storage connected by roads and delivery is possible");

            if (storehouse != null) {
                state = RETURNING_TO_STORAGE;

                setTarget(storehouse.getPosition());
            } else {
                state = GOING_TO_DIE;

                Point point = findPlaceToDie();

                setOffroadTarget(point);
            }

            duration.after("Set offroad target 3");
        } else if (state == GOING_TO_DIE) {
            setDead();

            state = DEAD;

            countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
        }

        duration.after("End of method");

        duration.report();
    }

    private void calculateDirection(Point borderPoint) {
        directionX = borderPoint.x - getPosition().x;
        directionY = borderPoint.y - getPosition().y;
    }

    private void avoidGoingTooCloseToBorder() {
        Point position = getPosition();

        if (position.x < MINIMUM_DISTANCE_TO_BORDER && directionX < 0) {
            directionX = -directionX;
        } else if (map.getWidth() - position.x < MINIMUM_DISTANCE_TO_BORDER && directionX > 0) {
            directionX = -directionX;
        }

        if (position.y < MINIMUM_DISTANCE_TO_BORDER && directionY < 0) {
            directionY = -directionY;
        } else if (map.getHeight() - position.y < MINIMUM_DISTANCE_TO_BORDER && directionY > 0) {
            directionY = -directionY;
        }
    }

    @Override
    public void onSetTargetBuilding(Building building) {

        /* If this is called it means that the scout will be assigned to a lookout tower and will not walk around exploring */
        state = WALKING_TO_ASSIGNED_LOOKOUT_TOWER;
    }

    @Override
    void onEnterBuilding(Building building) {

        /* Discover the area around the tower */
        for (Point point : GameUtils.getHexagonAreaAroundPoint(building.getPosition(), LOOKOUT_TOWER_DISCOVER_RADIUS, getMap())) {
            getPlayer().discover(point);
        }
        
        state = WORKING_IN_LOOKOUT_TOWER;
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws InvalidRouteException {

        /* Discover each point the scout walks on */
        map.discoverPointsWithinRadius(getPlayer(), getPosition(), DISCOVERY_RADIUS);

        /* Return to storage if the planned path no longer exists */
        if (map.isFlagAtPoint(getPosition()) && !map.arePointsConnectedByRoads(getPosition(), getTarget())) {
            returnToStorage();
        }

        /* Return to storage if the planned path no longer exists */
        if (state == WALKING_TO_ASSIGNED_LOOKOUT_TOWER &&
                map.isFlagAtPoint(getPosition()) &&
                !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter upon arrival */
            clearTargetBuilding();

            /* Go back to the storage */
            returnToStorage();
        }
    }

    private Point findNextPoint() {

        CumulativeDuration duration = map.getStats().measureCumulativeDuration("Scout.findNextPoint", AGGREGATED_EACH_STEP_TIME_GROUP);

        Point position = getPosition();

        /* Find the point to aim for */
        Point targetPoint = getPointInDirection(position, LENGTH_TO_PLAN_HEAD, directionX, directionY);

        duration.after("Got point in direction");

        /* Try to find a position somewhere between the scout's current position and the target point */
        List<Point> points = findBoxOfPointsAroundPoint(targetPoint, 5);

        Point target = findRandomPointToWalkToOffroad(points, position);

        duration.after("Find point try 1");

        /* Return the point if it's found */
        if (target != null) {

            duration.report();

            return target;
        }

        /* Try again with a larger box and return null if there is no point found */
        points = findBoxOfPointsAroundPoint(targetPoint, 7);

        target = findRandomPointToWalkToOffroad(points, position);

        duration.after("Find point try 2");

        duration.report();

        return target;
    }

    private List<Point> findBoxOfPointsAroundPoint(Point point, int distance) {
        List<Point> points = new ArrayList<>();

        for (int i = point.x - distance; i < point.x + distance; i++) {
            for (int j = point.y - distance; j < point.y + distance; j++) {

                /* Filter not allowed points */
                if (!Point.isValid(i, j)) {
                    continue;
                }

                points.add(new Point(i, j));
            }
        }

        return points;
    }

    private Point findRandomPointToWalkToOffroad(List<Point> points, Point position) {

        int offset = random.nextInt(points.size());

        for (int i = 0; i < points.size(); i++) {
            int index = i + offset;

            if (index >= points.size()) {
                index = index - points.size();
            }

            /* Filter points outside the map */
            Point point = points.get(index);

            if (!map.isWithinMap(point)) {
                continue;
            }

            /* Filter the current position */
            if (position.equals(point)) {
                continue;
            }

            /* Filter points the scout cannot reach */
            if (map.findWayOffroad(position, point, null) == null) {
                continue;
            }

            return point;
        }

        return null;
    }

    private Point getPointInDirection(Point position, int length, int dirX, int dirY) {
        Point targetPoint;
        if (Math.abs(dirX) > 0.001) {

            GameUtils.Line direction = new GameUtils.Line(position, dirX, dirY);

            if (dirX > 0) {
                targetPoint = direction.goFromPointWithPositiveXWithLength(position, length);
            } else {
                targetPoint = direction.goFromPointWithNegativeXWithLength(position, length);
            }
        } else {

            if (dirY > 0) {
                targetPoint = Point.fitToGamePoint(position.x, position.y + length);
            } else {
                targetPoint = Point.fitToGamePoint(position.x, position.y - length);
            }
        }

        return targetPoint;
    }

    private Point findDirectionToBorder() {
        Point position = getPosition();
        Point closestPointOnBorder = null;
        double distanceToBorder = Integer.MAX_VALUE;

        for (Point point : getPlayer().getBorderPoints()) {
            double distanceForPoint = position.distance(point);

            if (distanceForPoint < distanceToBorder) {
                distanceToBorder = distanceForPoint;
                closestPointOnBorder = point;
            }
        }

        return closestPointOnBorder;
    }

    @Override
    protected void onReturnToStorage() throws InvalidRouteException {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, SCOUT);

        state = RETURNING_TO_STORAGE;

        clearTargetBuilding();

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), SCOUT);

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
    public void goToOtherStorage(Building building) throws InvalidRouteException {
        state = GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
