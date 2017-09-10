/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.Collection;
import java.util.List;
import static org.appland.settlers.model.Scout.State.GOING_TO_NEXT_POINT;
import static org.appland.settlers.model.Scout.State.RETURNING_TO_FLAG;
import static org.appland.settlers.model.Scout.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Scout.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Material.SCOUT;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Scout extends Worker {

    protected enum State {
        WALKING_TO_TARGET,
        GOING_TO_NEXT_POINT,
        GOING_BACK_TO_FLAG,
        RETURNING_TO_FLAG,
        RETURNING_TO_STORAGE
    }

    private static final int DISCOVERY_RADIUS = 4;

    private State state;
    private Point flagPoint;
    private int   segmentCount;
    private int   directionX;
    private int   directionY;

    public Scout(Player player, GameMap m) {
        super(player, m);

        state        = WALKING_TO_TARGET;
        segmentCount = 0;
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == WALKING_TO_TARGET) {

            flagPoint = getPosition();

            map.discoverPointsWithinRadius(getPlayer(), getPosition(), DISCOVERY_RADIUS);

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
            map.discoverPointsWithinRadius(getPlayer(), getPosition(), DISCOVERY_RADIUS);

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

            setTarget(map.getClosestStorage(flagPoint).getPosition());
        } else if (state == RETURNING_TO_STORAGE) {
            Building storage = map.getBuildingAtPoint(getPosition());

            storage.putCargo(new Cargo(SCOUT, map));

            enterBuilding(storage);
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws Exception {

        /* Return to storage if the planned path no longer exists */
        if (map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {
            returnToStorage();
        }
    }

    private Point findNextPoint() {
        Point pos = getPosition();
        Point next = null;

        if (Math.abs(directionX) > Math.abs(directionY)) {
            if (directionX > 0) {
                next = pos.right();
            } else {
                next = pos.left();
            }
        } else {
            if (directionY > 0) {
                next = pos.up();
            } else {
                next = pos.down();
            }
        }

        return next;
    }

    private Point findDirectionToBorder() {
        List<Collection<Point>> borders = getPlayer().getBorders();
        Point closestPointOnBorder      = null;
        double distanceToBorder         = Integer.MAX_VALUE;

        for (Collection<Point> border : borders) {
            for (Point p : border) {
                if (getPosition().distance(p) < distanceToBorder) {
                    distanceToBorder = getPosition().distance(p);
                    closestPointOnBorder = p;
                }
            }
        }

        return closestPointOnBorder;
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building stg = getPlayer().getClosestStorage(getPosition(), getHome());

        state = State.RETURNING_TO_STORAGE;

        if (stg != null) {
            setTarget(stg.getPosition());
        } else {
            stg = GameUtils.getClosestStorageOffroad(getPlayer(), getPosition());

            setOffroadTarget(stg.getPosition());
        }

    }
}
