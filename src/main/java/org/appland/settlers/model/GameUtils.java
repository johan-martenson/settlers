/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.round;

/**
 *
 * @author johan
 */
public class GameUtils {

    static boolean isUnique(List<Point> wayPoints) {

        Set<Point> pointsSet = new HashSet<>(wayPoints);

        return wayPoints.size() == pointsSet.size();
    }

    interface ConnectionsProvider {
        Iterable<Point> getPossibleConnections(Point start, Point goal);

        Double realDistance(Point currentPoint, Point neighbor);

        Double estimateDistance(Point from, Point to);
    }

    static class SortPointsByY implements Comparator<Point> {

        @Override
        public int compare(Point t, Point t1) {
            if (t.y < t1.y) {
                return -1;
            }

            if (t.y > t1.y) {
                return 1;
            }

            return Integer.compare(t.x, t1.x);

        }
    }

    public static List<Point> findShortestPath(Point start, Point goal,
            Collection<Point> avoid, ConnectionsProvider connectionProvider) {
        Set<Point>          evaluated         = new HashSet<>();
        Set<Point>          toEvaluate        = new HashSet<>();
        Map<Point, Double>  realCostToPoint   = new HashMap<>();
        Map<Point, Double>  estimatedFullCost = new HashMap<>();
        Map<Point, Point>   cameFrom          = new HashMap<>();
        double              bestCaseCost;

        /* Define starting parameters */
        bestCaseCost = start.distance(goal);
        toEvaluate.add(start);
        realCostToPoint.put(start, (double)0);
        estimatedFullCost.put(start, realCostToPoint.get(start) + start.distance(goal));

        /* Declare variables outside of the loop to keep memory churn down */
        Point currentPoint;
        double currentEstimatedCost;

        double tmpEstimatedCost;

        double tentativeCost;

        while (!toEvaluate.isEmpty()) {
            currentPoint = null;
            currentEstimatedCost = Double.MAX_VALUE;

            /* Find the point with the lowest estimated full cost */
            for (Point iteratedPoint : toEvaluate) {

                tmpEstimatedCost = estimatedFullCost.get(iteratedPoint);

                if (currentEstimatedCost > tmpEstimatedCost) {
                    currentEstimatedCost = tmpEstimatedCost;
                    currentPoint = iteratedPoint;

                    if (currentEstimatedCost == bestCaseCost) {
                        break;
                    }
                }
            }

            /* Handle if the goal is reached */
            if (currentPoint.equals(goal)) {
                List<Point> path = new ArrayList<>();

                /* Re-construct the path taken */
                while (currentPoint != start) {
                    path.add(0, currentPoint);

                    currentPoint = cameFrom.get(currentPoint);
                }

                path.add(0, start);

                return path;
            }

            /* Do not re-evaluate the same point */
            toEvaluate.remove(currentPoint);
            evaluated.add(currentPoint);

            /* Evaluate each direct neighbor */
            for (Point neighbor : connectionProvider.getPossibleConnections(currentPoint, goal)) {

                /* Skip already evaluated points */
                if (evaluated.contains(neighbor)) {
                    continue;
                }

                /* Skip points we should avoid */
                if (avoid != null && avoid.contains(neighbor)) {
                    continue;
                }

                /* Calculate the real cost to reach the neighbor from the start */
                tentativeCost = realCostToPoint.get(currentPoint) +
                        connectionProvider.realDistance(currentPoint, neighbor);

                /* Check if the neighbor hasn't been evaluated yet or if we
                   have found a cheaper way to reach it */
                if (!toEvaluate.contains(neighbor) || tentativeCost < realCostToPoint.get(neighbor)) {

                    /* Keep track of how the neighbor was reached */
                    cameFrom.put(neighbor, currentPoint);

                    /* Remember the cost to reach the neighbor */
                    realCostToPoint.put(neighbor, tentativeCost);

                    /* Remember the estimated full cost to go via the neighbor */
                    estimatedFullCost.put(neighbor, realCostToPoint.get(neighbor) + neighbor.distance(goal));

                    /* Add the neighbor to the evaluation list */
                    toEvaluate.add(neighbor);
                }
            }
        }

        return null;
    }

    public static List<Point> hullWanderer(Collection<Point> points) {
        List<Point> hull = new LinkedList<>();

        /* Sort the points bottom to top, and left to right */
        List<Point> sortedPoints = new ArrayList<>(points);

        Collections.sort(sortedPoints, new SortPointsByY());

        /* Start with the bottom left point */
        Point bottomLeft = sortedPoints.get(0);
        Point current = bottomLeft;
        Point previous = bottomLeft.downRight();
        while (true) {
            hull.add(current);

            for (Point it : getSurroundingPointsCounterClockwise(current, previous)) {
                if (points.contains(it)) {
                    previous = current;
                    current = it;
                    break;
                }
            }

            if (current.equals(bottomLeft)) {
                break;
            }
        }

        return hull;
    }

    private static Iterable<Point> getSurroundingPointsCounterClockwise(Point center, Point arm) {
        List<Point> surrounding = new LinkedList<>();
        List<Point> result = new LinkedList<>();

        surrounding.add(center.down());
        surrounding.add(center.downRight());
        surrounding.add(center.right());
        surrounding.add(center.upRight());
        surrounding.add(center.up());
        surrounding.add(center.upLeft());
        surrounding.add(center.left());
        surrounding.add(center.downLeft());

        int armIndex = surrounding.indexOf(arm);

        result.addAll(surrounding.subList(armIndex + 1, surrounding.size()));
        result.addAll(surrounding.subList(0, armIndex + 1));

        return result;
    }

    public static Point getClosestPoint(double px, double py) {

        /* Round to integers */
        int roundedX = (int) round(px);
        int roundedY = (int) round(py);

        /* Calculate the error */
        double errorX = abs(px - roundedX);
        double errorY = abs(py - roundedY);

        /* Adjust the values if needed to avoid invalid points */
        if ((roundedX + roundedY) % 2 != 0) {
            if (errorX < errorY) {
                if (roundedY > py) {
                    roundedY = (int) floor(py);
                } else {
                    roundedY = (int) ceil(py);
                }
            } else if (errorX > errorY) {
                if (roundedX > px) {
                    roundedX = (int) floor(px);
                } else {
                    roundedX = (int) ceil(px);
                }
            } else {
                roundedX++;
            }
        }

        return new Point(roundedX, roundedY);
    }

    public static class PathOnExistingRoadsProvider implements ConnectionsProvider {

        private final Map<Point, MapPoint> pointToGameObject;

        public PathOnExistingRoadsProvider(Map<Point, MapPoint> pointToGameObject) {
            this.pointToGameObject = pointToGameObject;
        }

        @Override
        public Iterable<Point> getPossibleConnections(Point start, Point goal) {
            MapPoint mp = pointToGameObject.get(start);

            return mp.getConnectedNeighbors();
        }

        @Override
        public Double realDistance(Point currentPoint, Point neighbor) {
            return (double)1;
        }

        @Override
        public Double estimateDistance(Point from, Point to) {
            return from.distance(to);
        }
    }

    public static class ConnectedFlagsAndBuildingsProvider implements ConnectionsProvider {

        private final Map<Point, MapPoint> pointToGameObject;

        public ConnectedFlagsAndBuildingsProvider(Map<Point, MapPoint> pointToGameObject) {
            this.pointToGameObject = pointToGameObject;
        }

        @Override
        public Iterable<Point> getPossibleConnections(Point start, Point goal) {
            MapPoint mp = pointToGameObject.get(start);

            return mp.getConnectedFlagsAndBuildings();
        }

        @Override
        public Double realDistance(Point currentPoint, Point neighbor) {

            MapPoint mp = pointToGameObject.get(currentPoint);

            int distance = Integer.MAX_VALUE;

            /* Find the shortest road that connects the two points */
            for (Road road : mp.getConnectedRoads()) {
                if (!road.getStart().equals(currentPoint) && !road.getEnd().equals(currentPoint)) {
                    continue;
                }

                if (!road.getStart().equals(neighbor) && !road.getEnd().equals(neighbor)) {
                    continue;
                }

                /* Count the number of segments to walk and don't include the starting point */
                int tmpDistance = road.getWayPoints().size() - 1;

                if (tmpDistance < distance) {
                    distance = tmpDistance;
                }

                if (distance == 2) {
                    return (double)2;
                }
            }

            return (double)distance;
        }

        @Override
        public Double estimateDistance(Point from, Point to) {
            return from.distance(to);
        }
    }

    /**
     * Finds the shortest path following roads between any two points. The points
     * don't need to be flags or buildings but can be any point on a road.
     *
     * Only points with flags or buildings are returned.
     *
     * @param start The point to start from
     * @param goal The point to reach
     * @param mapPoints The map with information about each point on the map
     * @return the detailed list of steps required to travel from start to goal
     */
    static List<Point> findShortestPathViaRoads(Point start, Point goal,
            Map<Point, MapPoint> mapPoints) {
        Set<Point>         evaluated         = new HashSet<>();
        Set<Point>         toEvaluate        = new HashSet<>();
        Map<Point, Double> realCostToPoint   = new HashMap<>();
        Map<Point, Double> estimatedFullCost = new HashMap<>();
        Map<Point, Point>  cameFrom          = new HashMap<>();
        double             bestCaseCost;

        /* Define starting parameters */
        bestCaseCost = start.distance(goal);
        toEvaluate.add(start);
        realCostToPoint.put(start, (double)0);
        estimatedFullCost.put(start, realCostToPoint.get(start) + start.distance(goal));

        /* Declare variables outside of the loop to keep memory churn down */
        Point currentPoint;
        double currentEstimatedCost;

        double tmpEstimatedCost;

        double tentativeCost;

        while (!toEvaluate.isEmpty()) {
            currentPoint = null;
            currentEstimatedCost = Double.MAX_VALUE;

            /* Find the point with the lowest estimated full cost */
            for (Point iteratedPoint : toEvaluate) {

                tmpEstimatedCost = estimatedFullCost.get(iteratedPoint);

                if (currentEstimatedCost > tmpEstimatedCost) {
                    currentEstimatedCost = tmpEstimatedCost;
                    currentPoint = iteratedPoint;

                    if (currentEstimatedCost == bestCaseCost) {
                        break;
                    }
                }
            }

            /* Handle if the goal is reached */
            if (currentPoint.equals(goal)) {
                List<Point> path = new ArrayList<>();

                /* Re-construct the path taken */
                while (currentPoint != start) {
                    path.add(0, currentPoint);

                    currentPoint = cameFrom.get(currentPoint);
                }

                path.add(0, start);

                return path;
            }

            /* Do not re-evaluate the same point */
            toEvaluate.remove(currentPoint);
            evaluated.add(currentPoint);

            /* Evaluate each direct neighbor */
            MapPoint mp = mapPoints.get(currentPoint);
            for (Road road : mp.getConnectedRoads()) {

                Point neighbor = road.getOtherPoint(currentPoint);

                /* Skip already evaluated points */
                if (evaluated.contains(neighbor)) {
                    continue;
                }

                /* Calculate the real cost to reach the neighbor from the start */
                tentativeCost = realCostToPoint.get(currentPoint) +
                        road.getWayPoints().size() - 1;

                /* Check if the neighbor hasn't been evaluated yet or if we
                   have found a cheaper way to reach it */
                if (!toEvaluate.contains(neighbor) || tentativeCost < realCostToPoint.get(neighbor)) {

                    /* Keep track of how the neighbor was reached */
                    cameFrom.put(neighbor, currentPoint);

                    /* Remember the cost to reach the neighbor */
                    realCostToPoint.put(neighbor, tentativeCost);

                    /* Remember the estimated full cost to go via the neighbor */
                    estimatedFullCost.put(neighbor, realCostToPoint.get(neighbor) + neighbor.distance(goal));

                    /* Add the neighbor to the evaluation list */
                    toEvaluate.add(neighbor);
                }
            }
        }

        return null;
    }

    /**
     * Determines whether any two points are connected by roads. The points
     * don't need to be flags or buildings but can be any point on a road.
     *
     * @param start The point to start from
     * @param goal The point to reach
     * @param mapPoints The map with information about each point on the map
     * @return true if the start and end are connected
     */
    static boolean arePointsConnectedByRoads(Point start, Point goal,
            Map<Point, MapPoint> mapPoints) {
        Set<Point>         evaluated         = new HashSet<>();
        Set<Point>         toEvaluate        = new HashSet<>();
        Map<Point, Double> realCostToPoint   = new HashMap<>();
        Map<Point, Double> estimatedFullCost = new HashMap<>();
        double             bestCaseCost;

        /* Define starting parameters */
        bestCaseCost = start.distance(goal);
        toEvaluate.add(start);
        realCostToPoint.put(start, (double)0);
        estimatedFullCost.put(start, realCostToPoint.get(start) + start.distance(goal));

        /* Declare variables outside of the loop to keep memory churn down */
        Point currentPoint;
        double currentEstimatedCost;

        double tmpEstimatedCost;

        double tentativeCost;

        while (!toEvaluate.isEmpty()) {
            currentPoint = null;
            currentEstimatedCost = Double.MAX_VALUE;

            /* Find the point with the lowest estimated full cost */
            for (Point iteratedPoint : toEvaluate) {

                tmpEstimatedCost = estimatedFullCost.get(iteratedPoint);

                if (currentEstimatedCost > tmpEstimatedCost) {
                    currentEstimatedCost = tmpEstimatedCost;
                    currentPoint = iteratedPoint;

                    if (currentEstimatedCost == bestCaseCost) {
                        break;
                    }
                }
            }

            /* Handle if the goal is reached */
            if (currentPoint.equals(goal)) {
                return true;
            }

            /* Do not re-evaluate the same point */
            toEvaluate.remove(currentPoint);
            evaluated.add(currentPoint);

            /* Evaluate each direct neighbor */
            MapPoint mp = mapPoints.get(currentPoint);
            for (Road road : mp.getConnectedRoads()) {

                Point neighbor = road.getOtherPoint(currentPoint);

                /* Skip already evaluated points */
                if (evaluated.contains(neighbor)) {
                    continue;
                }

                /* Calculate the real cost to reach the neighbor from the start */
                tentativeCost = realCostToPoint.get(currentPoint) +
                        road.getWayPoints().size() - 1;

                /* Check if the neighbor hasn't been evaluated yet or if we
                   have found a cheaper way to reach it */
                if (!toEvaluate.contains(neighbor) || tentativeCost < realCostToPoint.get(neighbor)) {

                    /* Remember the cost to reach the neighbor */
                    realCostToPoint.put(neighbor, tentativeCost);

                    /* Remember the estimated full cost to go via the neighbor */
                    estimatedFullCost.put(neighbor, realCostToPoint.get(neighbor) + neighbor.distance(goal));

                    /* Add the neighbor to the evaluation list */
                    toEvaluate.add(neighbor);
                }
            }
        }

        return false;
    }

    /**
     * Returns a detailed path including points between flags or buildings. Can
     * only be called with a building or flag as start and end point.
     *
     * @param startEndPoint Flag or building to start from
     * @param goalEndPoint Flag or building to reach
     * @param mapPoints The map with information about each point on the game map
     * @return a detailed list with the steps required to travel from the start to the goal.
     */
    static List<Point> findShortestDetailedPathViaRoads(EndPoint startEndPoint, EndPoint goalEndPoint,
            Map<Point, MapPoint> mapPoints) {
        Set<Point>         evaluated         = new HashSet<>();
        Set<Point>         toEvaluate        = new HashSet<>();
        Map<Point, Double> realCostToPoint   = new HashMap<>();
        Map<Point, Double> estimatedFullCost = new HashMap<>();
        Map<Point, Road>   cameVia           = new HashMap<>();
        double             bestCaseCost;

        Point start = startEndPoint.getPosition();
        Point goal = goalEndPoint.getPosition();

        /* Define starting parameters */
        bestCaseCost = start.distance(goal);
        toEvaluate.add(start);
        realCostToPoint.put(start, (double)0);
        estimatedFullCost.put(start, realCostToPoint.get(start) + start.distance(goal));

        /* Declare variables outside of the loop to keep memory churn down */
        Point currentPoint;
        double currentEstimatedCost;

        double tmpEstimatedCost;

        double tentativeCost;

        while (!toEvaluate.isEmpty()) {
            currentPoint = null;
            currentEstimatedCost = Double.MAX_VALUE;

            /* Find the point with the lowest estimated full cost */
            for (Point iteratedPoint : toEvaluate) {

                tmpEstimatedCost = estimatedFullCost.get(iteratedPoint);

                if (currentEstimatedCost > tmpEstimatedCost) {
                    currentEstimatedCost = tmpEstimatedCost;
                    currentPoint = iteratedPoint;

                    if (currentEstimatedCost == bestCaseCost) {
                        break;
                    }
                }
            }

            /* Handle if the goal is reached */
            if (currentPoint.equals(goal)) {
                List<Point> path = new ArrayList<>();

                /* Re-construct the path taken */
                while (currentPoint != start) {
                    path.add(0, currentPoint);

                    Road road = cameVia.get(currentPoint);

                    /* Follow the road and add up the points */
                    if (road.getStart().equals(currentPoint)) {
                        for (int i = 1; i < road.getWayPoints().size(); i++) {
                            path.add(0, road.getWayPoints().get(i));
                        }

                        currentPoint = road.getEnd();
                    } else {
                        for (int i = road.getWayPoints().size() - 2; i >= 0; i++) {
                            path.add(0, road.getWayPoints().get(i));
                        }

                        currentPoint = road.getStart();
                    }
                }

                path.add(0, start);

                return path;
            }

            /* Do not re-evaluate the same point */
            toEvaluate.remove(currentPoint);
            evaluated.add(currentPoint);

            /* Evaluate each direct neighbor */
            MapPoint mp = mapPoints.get(currentPoint);
            for (Road road : mp.getConnectedRoads()) {

                Point neighbor = road.getOtherPoint(currentPoint);

                /* Skip already evaluated points */
                if (evaluated.contains(neighbor)) {
                    continue;
                }

                /* Calculate the real cost to reach the neighbor from the start */
                tentativeCost = realCostToPoint.get(currentPoint) +
                        road.getWayPoints().size() - 1;

                /* Check if the neighbor hasn't been evaluated yet or if we
                 * have found a cheaper way to reach it
                */
                if (!toEvaluate.contains(neighbor) || tentativeCost < realCostToPoint.get(neighbor)) {

                    /* Keep track of how the neighbor was reached */
                    cameVia.put(neighbor, road);

                    /* Remember the cost to reach the neighbor */
                    realCostToPoint.put(neighbor, tentativeCost);

                    /* Remember the estimated full cost to go via the neighbor */
                    estimatedFullCost.put(neighbor, realCostToPoint.get(neighbor) + neighbor.distance(goal));

                    /* Add the neighbor to the evaluation list */
                    toEvaluate.add(neighbor);
                }
            }
        }

        return null;
    }

    /**
     * Determines if two points with flags or buildings are connected by roads.
     *
     * @param startEndPoint Flag or building to start from
     * @param goalEndPoint Flag or building to reach
     * @param mapPoints The map with information about each point on the game map
     * @return true if the start and end are connected
     */
    public static boolean areBuildingsOrFlagsConnected(EndPoint startEndPoint, EndPoint goalEndPoint,
            Map<Point, MapPoint> mapPoints) {
        Set<Point>         evaluated         = new HashSet<>();
        Set<Point>         toEvaluate        = new HashSet<>();
        Map<Point, Double> realCostToPoint   = new HashMap<>();
        Map<Point, Double> estimatedFullCost = new HashMap<>();
        double             bestCaseCost;

        Point start = startEndPoint.getPosition();
        Point goal = goalEndPoint.getPosition();

        /* Define starting parameters */
        bestCaseCost = start.distance(goal);
        toEvaluate.add(start);
        realCostToPoint.put(start, (double)0);
        estimatedFullCost.put(start, realCostToPoint.get(start) + start.distance(goal));

        /* Declare variables outside of the loop to keep memory churn down */
        Point currentPoint;
        double currentEstimatedCost;

        double tmpEstimatedCost;

        double tentativeCost;

        while (!toEvaluate.isEmpty()) {
            currentPoint = null;
            currentEstimatedCost = Double.MAX_VALUE;

            /* Find the point with the lowest estimated full cost */
            for (Point iteratedPoint : toEvaluate) {

                tmpEstimatedCost = estimatedFullCost.get(iteratedPoint);

                if (currentEstimatedCost > tmpEstimatedCost) {
                    currentEstimatedCost = tmpEstimatedCost;
                    currentPoint = iteratedPoint;

                    if (currentEstimatedCost == bestCaseCost) {
                        break;
                    }
                }
            }

            /* Handle if the goal is reached */
            if (currentPoint.equals(goal)) {
                return true;
            }

            /* Do not re-evaluate the same point */
            toEvaluate.remove(currentPoint);
            evaluated.add(currentPoint);

            /* Evaluate each direct neighbor */
            MapPoint mp = mapPoints.get(currentPoint);
            for (Road road : mp.getConnectedRoads()) {

                Point neighbor = road.getOtherPoint(currentPoint);

                /* Skip already evaluated points */
                if (evaluated.contains(neighbor)) {
                    continue;
                }

                /* Calculate the real cost to reach the neighbor from the start */
                tentativeCost = realCostToPoint.get(currentPoint) +
                        road.getWayPoints().size() - 1;

                /* Check if the neighbor hasn't been evaluated yet or if we
                 * have found a cheaper way to reach it
                */
                if (!toEvaluate.contains(neighbor) || tentativeCost < realCostToPoint.get(neighbor)) {

                    /* Remember the cost to reach the neighbor */
                    realCostToPoint.put(neighbor, tentativeCost);

                    /* Remember the estimated full cost to go via the neighbor */
                    estimatedFullCost.put(neighbor, realCostToPoint.get(neighbor) + neighbor.distance(goal));

                    /* Add the neighbor to the evaluation list */
                    toEvaluate.add(neighbor);
                }
            }
        }

        return false;
    }

    static Storage getClosestStorageOffroad(Player player, Point point) {
        int distance = Integer.MAX_VALUE;
        Storage storage = null;
        GameMap map = player.getMap();

        for (Building building : player.getBuildings()) {

            /* Filter buildings that are not ready */
            if (building.burningDown() || building.destroyed() || building.underConstruction()) {
                continue;
            }

            /* Filter other buildings than storages */
            if (! (building instanceof Storage)) {
                continue;
            }

            List<Point> pathToStorage = map.findWayOffroad(point, building.getPosition(), null);

            /* Filter storages that can't be reached */
            if (pathToStorage == null) {
                continue;
            }

            int currentDistance = pathToStorage.size();

            if (currentDistance < distance) {
                storage = (Storage)building;
                distance = currentDistance;
            }
        }

        return storage;
    }

    public static Storage getClosestStorage(Point point, Player player) throws InvalidRouteException {
        return getClosestStorage(point, null, player);
    }

    public static Storage getClosestStorage(Point point, Building avoid, Player player) throws InvalidRouteException {
        Storage storage = null;
        int distance = Integer.MAX_VALUE;
        GameMap map = player.getMap();

        for (Building building : player.getBuildings()) {

            /* Filter buildings to avoid */
            if (building.equals(avoid)) {
                continue;
            }

            /* Filter buildings that are destroyed */
            if (building.burningDown() ||
                building.destroyed()   ||
                building.underConstruction()) {
                continue;
            }

            if (building instanceof Storage) {
                if (building.getFlag().getPosition().equals(point)) {
                    storage = (Storage)building;
                    break;
                }

                List<Point> path = map.findWayWithExistingRoads(point, building.getFlag().getPosition());

                if (path == null) {
                    continue;
                }

                if (path.size() < distance) {
                    distance = path.size();
                    storage = (Storage) building;
                }
            }
        }

        return storage;
    }


    public static Storage getClosestStorage(Point point, GameMap map) throws InvalidRouteException {
        return getClosestStorage(point, null, map);
    }

    public static Storage getClosestStorage(Point point, Building avoid, GameMap map) throws InvalidRouteException {
        Storage storage = null;
        int distance = Integer.MAX_VALUE;

        for (Building building : map.getBuildings()) {

            /* Filter buildings to avoid */
            if (building.equals(avoid)) {
                continue;
            }

            /* Filter buildings that are destroyed */
            if (building.burningDown() ||
                building.destroyed()   ||
                building.underConstruction()) {
                continue;
            }

            if (building instanceof Storage) {
                if (building.getFlag().getPosition().equals(point)) {
                    storage = (Storage)building;
                    break;
                }

                List<Point> path = map.findWayWithExistingRoads(point, building.getFlag().getPosition());

                if (path == null) {
                    continue;
                }

                if (path.size() < distance) {
                    distance = path.size();
                    storage = (Storage) building;
                }
            }
        }

        return storage;
    }

    static public Set<Building> getBuildingsWithinReach(Flag startFlag) {
        List<Point>   toEvaluate = new LinkedList<>();
        Set<Point>    visited    = new HashSet<>();
        Set<Building> reachable  = new HashSet<>();
        Player        player     = startFlag.getPlayer();
        GameMap       map        = player.getMap();

        toEvaluate.add(startFlag.getPosition());

        /* Declare variables outside of the loop to keep memory churn down */
        Point point;
        Point oppositePoint;

        while (!toEvaluate.isEmpty()) {

            point = toEvaluate.get(0);
            toEvaluate.remove(point);

            /* Test if this point is connected to a building */
            if (map.isBuildingAtPoint(point)) {
                reachable.add(map.getBuildingAtPoint(point));
            }

            /* Remember that this point has been tested */
            visited.add(point);

            /* Go through the neighbors and add the new points to the list to be evaluated */
            for (Road road : map.getMapPoint(point).getConnectedRoads()) {

                oppositePoint = road.getOtherPoint(point);

                /* Filter already visited */
                if (visited.contains(oppositePoint)) {
                    continue;
                }

                /* Add the point to the list */
                toEvaluate.add(oppositePoint);
            }
        }

        return reachable;
    }
}
