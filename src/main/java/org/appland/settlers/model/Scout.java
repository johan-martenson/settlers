/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static org.appland.settlers.model.Material.SCOUT;
import static org.appland.settlers.model.Scout.State.GOING_TO_NEXT_POINT;
import static org.appland.settlers.model.Scout.State.RETURNING_TO_FLAG;
import static org.appland.settlers.model.Scout.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Scout.State.WALKING_TO_ASSIGNED_LOOKOUT_TOWER;
import static org.appland.settlers.model.Scout.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Scout.State.WORKING_IN_LOOKOUT_TOWER;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Scout extends Worker {

    private static final int LOOKOUT_TOWER_DISCOVER_RADIUS = 9;

    protected enum State {
        WALKING_TO_TARGET,
        GOING_TO_NEXT_POINT,
        RETURNING_TO_FLAG,
        WALKING_TO_ASSIGNED_LOOKOUT_TOWER, WORKING_IN_LOOKOUT_TOWER, RETURNING_TO_STORAGE
    }

    private static final int DISCOVERY_RADIUS = 4;
    private static final int LENGTH_TO_PLAN_HEAD = 4;

    private final Random random;

    private State state;
    private Point flagPoint;
    private int   segmentCount;
    private int   directionX;
    private int   directionY;

    public Scout(Player player, GameMap map) {
        super(player, map);

        state        = WALKING_TO_TARGET;
        segmentCount = 0;

        random = new Random(4);
    }

    @Override
    protected void onArrival() throws Exception {

        map.discoverPointsWithinRadius(getPlayer(), getPosition(), DISCOVERY_RADIUS);

        if (state == WALKING_TO_ASSIGNED_LOOKOUT_TOWER) {
            enterBuilding(getTargetBuilding());
        } else if (state == WALKING_TO_TARGET) {

            flagPoint = getPosition();

            Point borderPoint = findDirectionToBorder();

            /* Calculate direction */
            directionX = borderPoint.x - getPosition().x;
            directionY = borderPoint.y - getPosition().y;

            Point point = findNextPoint();

            if (!map.isWithinMap(point)) {
                state = RETURNING_TO_FLAG;

                setOffroadTarget(flagPoint);
            } else {
                state = GOING_TO_NEXT_POINT;

                setOffroadTarget(point);
            }
        } else if (state == GOING_TO_NEXT_POINT) {

            segmentCount++;

            if (segmentCount == 8) {
                state = RETURNING_TO_FLAG;

                setOffroadTarget(flagPoint);

                return;
            }

            Point point = findNextPoint();

            if (!map.isWithinMap(point)) {
                state = RETURNING_TO_FLAG;

                setOffroadTarget(flagPoint);
            } else {
                state = GOING_TO_NEXT_POINT;

                setOffroadTarget(point);
            }
        } else if (state == RETURNING_TO_FLAG) {
            state = RETURNING_TO_STORAGE;

            Building storage = GameUtils.getClosestStorageConnectedByRoads(flagPoint, getPlayer());

            if (storage != null) {
                setTarget(storage.getPosition());
            } else {
                storage = GameUtils.getClosestStorageOffroad(getPlayer(), flagPoint);

                setOffroadTarget(storage.getPosition(), storage.getFlag().getPosition());
            }
        } else if (state == RETURNING_TO_STORAGE) {
            Building storage = map.getBuildingAtPoint(getPosition());

            storage.putCargo(new Cargo(SCOUT, map));

            enterBuilding(storage);
        }
    }

    @Override
    public void onSetTargetBuilding(Building building) {

        /* If this is called it means that the scout will be assigned to a lookout tower and will not walk around exploring */
        state = WALKING_TO_ASSIGNED_LOOKOUT_TOWER;
    }

    @Override
    void onEnterBuilding(Building building) throws Exception {

        /* The scout has reached the lookout tower it's assigned to */
        setHome(building);

        /* Discover the area around the tower */
        for (Point point : map.getPointsWithinRadius(building.getPosition(), LOOKOUT_TOWER_DISCOVER_RADIUS)) {
            getPlayer().discover(point);
        }
        
        state = WORKING_IN_LOOKOUT_TOWER;
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws Exception {

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

        Point position = getPosition();
        Point targetPoint = null;

        if (Math.abs(directionX) > 0.001) {

            GameUtils.Line direction = new GameUtils.Line(position, directionX, directionY);

            int targetX;
            final double scale = Math.sqrt(LENGTH_TO_PLAN_HEAD * LENGTH_TO_PLAN_HEAD - directionX * directionX - directionY * directionY);
            targetX = position.x + (int) (scale * directionX);

            targetPoint = Point.fitToGamePoint(targetX, direction.getYforX(targetX));
        } else {

            if (directionY > 0) {
                targetPoint = new Point(position.x, position.y + LENGTH_TO_PLAN_HEAD);
            } else {
                targetPoint = new Point(position.x, position.y - LENGTH_TO_PLAN_HEAD);
            }
        }

        /* Set a box around the target point and try to pick any point inside the box */
        List<Point> possibleTargets = new ArrayList<>();
        for (int i = targetPoint.x - 5; i < targetPoint.x + 5; i++) {
            for (int j = targetPoint.y - 5; j < targetPoint.y + 5; j++) {

                /* Filter not allowed points */
                if (!Point.isValid(i, j)) {
                    continue;
                }

                /* Filter points outside the map */
                Point possibleTarget = new Point(i, j);
                if (!map.isWithinMap(possibleTarget)) {
                    continue;
                }

                /* Filter the current position */
                if (position.equals(possibleTarget)) {
                    continue;
                }

                /* Filter points the scout cannot reach */
                if (map.findWayOffroad(position, possibleTarget, null) != null) {
                    possibleTargets.add(possibleTarget);
                }
            }
        }

        /* Pick one of the possible targets */
        Point target = possibleTargets.get((int)Math.floor(possibleTargets.size() * random.nextDouble()));

        return target;
    }

    private Point findDirectionToBorder() {
        List<Collection<Point>> borders = getPlayer().getBorders();
        Point closestPointOnBorder      = null;
        double distanceToBorder         = Integer.MAX_VALUE;

        for (Collection<Point> border : borders) {
            for (Point point : border) {
                if (getPosition().distance(point) < distanceToBorder) {
                    distanceToBorder = getPosition().distance(point);
                    closestPointOnBorder = point;
                }
            }
        }

        return closestPointOnBorder;
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = getPlayer().getClosestStorage(getPosition(), getHome());

        state = State.RETURNING_TO_STORAGE;

        clearTargetBuilding();

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroad(getPlayer(), getPosition());

            state = RETURNING_TO_STORAGE;

            setOffroadTarget(storage.getPosition());
        }
    }
}
