/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
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

    public static boolean setContainsAny(Set<Point> discoveredLand, List<Point> wayPoints) {
        for (Point point : wayPoints) {
            if (discoveredLand.contains(point)) {
                return true;
            }
        }

        return false;
    }

    public static Collection<Point> getHexagonAroundPoint(Point point, int radius) {
        Set<Point> hexagonBorder = new HashSet<>();

        int upperY = point.y;
        int lowerY = point.y;
        for (int x = point.x - (radius * 2); x < point.x - radius; x++) {
            hexagonBorder.add(new Point(x, upperY));
            hexagonBorder.add(new Point(x, lowerY));

            upperY++;
            lowerY--;
        }

        upperY = point.y + radius;
        lowerY = point.y - radius;
        for (int x = point.x + radius; x < point.x + (radius * 2); x++) {
            hexagonBorder.add(new Point(x, upperY));
            hexagonBorder.add(new Point(x, lowerY));

            upperY--;
            lowerY++;
        }

        for (int x = point.x - radius; x < point.x + radius; x += 2) {
            hexagonBorder.add(new Point(x, point.y + radius));
            hexagonBorder.add(new Point(x, point.y - radius));
        }

        return hexagonBorder;
    }

    public static Collection<Point> getHexagonAreaAroundPoint(Point position, int i, GameMap map) {
        Set<Point> area = new HashSet<>();

        int xStart = position.x - i;
        int xEnd = position.x + i;

        for (int y = position.y - i; y < position.y; y++) {
            for (int x = xStart; x <= xEnd; x += 2) {
                if (x < 0 || y < 0 || x > map.getWidth() || y > map.getHeight()) {
                    continue;
                }

                area.add(new Point(x, y));
            }

            xStart--;
            xEnd++;
        }

        xStart = position.x - i;
        xEnd = position.x + i;

        for (int y = position.y + i; y >= position.y; y--) {
            for (int x = xStart; x <= xEnd; x += 2) {
                if (x < 0 || y < 0 || x > map.getWidth() || y > map.getHeight()) {
                    continue;
                }

                area.add(new Point(x, y));
            }

            xStart--;
            xEnd++;
        }

        return area;
    }

    public static Building getClosestStorageOffroadWhereDeliveryIsPossible(Point point, Building avoid, Player player, Material material) {
        Storehouse storehouse = null;
        int distance = Integer.MAX_VALUE;

        GameMap map = player.getMap();

        for (Building building : map.getBuildings()) {

            /* Filter buildings that belong to another player */
            if (!player.equals(building.getPlayer())) {
                continue;
            }

            /* Filter buildings to avoid */
            if (building.equals(avoid)) {
                continue;
            }

            /* Filter buildings that are destroyed */
            if (building.isBurningDown() || building.isDestroyed() || building.isUnderConstruction()) {
                continue;
            }

            /* Filter buildings that are not storehouses */
            if (!building.isStorehouse()) {
                continue;
            }

            /* Filter storehouses where the delivery is not allowed */
            if (((Storehouse)building).isDeliveryBlocked(material)) {
                continue;
            }

            /* If the building has its flag on the point we know we have found the closest building */
            if (building.getFlag().getPosition().equals(point)) {
                storehouse = (Storehouse)building;
                break;
            }

            List<Point> path = map.findWayOffroad(point, building.getFlag().getPosition(), null);

            if (path == null) {
                continue;
            }

            if (path.size() < distance) {
                distance = path.size();
                storehouse = (Storehouse) building;
            }
        }

        return storehouse;
    }

    public static boolean isAll(Collection<Vegetation> surroundingVegetation, Vegetation vegetation) {
        for (Vegetation vegetationInList : surroundingVegetation) {
            if (vegetationInList != vegetation) {
                return false;
            }
        }

        return true;
    }

    public static boolean isAny(Collection<Vegetation> surroundingVegetation, Vegetation vegetation) {
        for (Vegetation vegetationInList : surroundingVegetation) {
            if (vegetationInList == vegetation) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSomeButNotAll(Collection<Vegetation> surroundingVegetation, Vegetation vegetation) {
        boolean foundMatch = false;
        boolean foundNotMatch = false;

        for (Vegetation vegetationInList : surroundingVegetation) {
            if (vegetationInList == vegetation) {
                foundMatch = true;

                continue;
            }

            foundNotMatch = true;
        }

        return foundMatch && foundNotMatch;
    }

    static class Line {
        final double k;
        final double m;

        Line (java.awt.Point point1, java.awt.Point point2) {
            double newK = (double)(point1.y - point2.y) / (point1.x - point2.x);

            // y = kx + m, m = y - kx
            double newM = point1.y - newK * point1.x;

            k = newK;
            m = newM;
        }

        Line(double k, double m) {
            this.k = k;
            this.m = m;
        }

        public Line(java.awt.Point point, int directionX, int directionY) {
            k = (double)directionY / directionX;
            m = point.y - point.x * k;
        }

        Line getOrthogonalLineThroughPoint(java.awt.Point point) {
            double orthogonalK = 1/ k;

            // y = kx + m, m = y - kx
            return new Line(orthogonalK, point.y - orthogonalK * point.x);
        }

        java.awt.Point getPointOnLineAtX(int x) {
            return new java.awt.Point(x, (int)(k * x + m));
        }

        public double getYForX(int x) {
            return k * x + m;
        }

        public Point goFromPointWithPositiveXWithLength(Point position, int length) {
            double x = Math.sqrt(length * length / (1 + k * k));
            double y = k * x;

            return Point.fitToGamePoint(position.x + x, position.y + y);
        }

        public Point goFromPointWithNegativeXWithLength(Point position, int length) {
            double x = - Math.sqrt(length * length / (1 + k * k));
            double y = k * x;

            return Point.fitToGamePoint(position.x + x, position.y + y);
        }
    }

    static boolean areAllUnique(Collection<Point> points) {

        Set<Point> pointsSet = new HashSet<>(points);

        return points.size() == pointsSet.size();
    }

    interface ConnectionsProvider {
        Iterable<Point> getPossibleConnections(Point start, Point goal);

        int realDistance(Point currentPoint, Point neighbor);
    }

    static class PointAndCost implements Comparable<PointAndCost> {

        private final Point point;
        private final int estimatedFullCostThroughPoint;

        PointAndCost(Point point, int estimatedFullCostThroughPoint) {
            this.point = point;
            this.estimatedFullCostThroughPoint = estimatedFullCostThroughPoint;
        }

        @Override
        public int compareTo(PointAndCost pointAndCost) {
            if (estimatedFullCostThroughPoint < pointAndCost.estimatedFullCostThroughPoint) {
                return -1;
            } else if (estimatedFullCostThroughPoint > pointAndCost.estimatedFullCostThroughPoint) {
                return 1;
            }

            return 0;
        }
    }

    // FIXME: HOTSPOT
    static List<Point> findShortestPath(Point start, Point goal, Set<Point> avoid, ConnectionsProvider connectionProvider) {
        Map<Point, Integer> costToGetToPoint = new HashMap<>();
        Map<Point, Point>   cameFrom         = new HashMap<>();
        int                 bestCaseCost;

        PriorityQueue<PointAndCost> toEvaluatePriorityQueue = new PriorityQueue<>();

        /* Define starting parameters */
        bestCaseCost = getDistanceInGameSteps(start, goal);
        costToGetToPoint.put(start, 0);

        PointAndCost startingPointAndCost = new PointAndCost(start, bestCaseCost);

        toEvaluatePriorityQueue.add(startingPointAndCost);

        /* Declare variables outside of the loop to keep memory churn down */
        PointAndCost currentPoint;
        int newCostToGetToPoint;

        /* Keep going through points yet to be evaluated until either a perfect match is found or all points have been done */
        while (!toEvaluatePriorityQueue.isEmpty()) {

            /* Find the point to evaluate with the lowest estimated full cost */
            currentPoint = toEvaluatePriorityQueue.poll();

            /* Handle if the goal is reached */
            if (goal.equals(currentPoint.point)) {
                Point previousPoint = currentPoint.point;
                List<Point> path = new ArrayList<>();

                /* Re-construct the path taken */
                while (previousPoint != start) {
                    path.add(0, previousPoint);

                    previousPoint = cameFrom.get(previousPoint);
                }

                path.add(0, start);

                return path;
            }

            /* Evaluate each direct neighbor */
            for (Point neighbor : connectionProvider.getPossibleConnections(currentPoint.point, goal)) {

                /* Skip points we should avoid */
                if (avoid != null && avoid.contains(neighbor)) {
                    continue;
                }

                /* Calculate the real cost to reach the neighbor from the start */
                newCostToGetToPoint = costToGetToPoint.get(currentPoint.point) + connectionProvider.realDistance(currentPoint.point, neighbor);

                /* Check if the neighbor hasn't been evaluated yet or if we have found a cheaper way to reach it */
                int currentCostToGetToPoint = costToGetToPoint.getOrDefault(neighbor, Integer.MAX_VALUE);

                if (newCostToGetToPoint < currentCostToGetToPoint) {

                    /* Keep track of how the neighbor was reached */
                    cameFrom.put(neighbor, currentPoint.point);

                    /* Remember the cost to reach the neighbor */
                    costToGetToPoint.put(neighbor, newCostToGetToPoint);

                    /* Remember the estimated full cost to go via the neighbor */
                    int estimatedFullCostThroughPoint = costToGetToPoint.get(neighbor) + getDistanceInGameSteps(neighbor, goal);

                    /* Add the neighbor to the evaluation list */
                    PointAndCost neighborPointAndCost = new PointAndCost(neighbor, estimatedFullCostThroughPoint);

                    toEvaluatePriorityQueue.add(neighborPointAndCost);
                }
            }
        }

        return null;
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
            MapPoint mapPoint = pointToGameObject.get(start);

            return mapPoint.getConnectedNeighbors();
        }

        @Override
        public int realDistance(Point currentPoint, Point neighbor) {
            return 1;
        }
    }

    public static class ConnectedFlagsAndBuildingsProvider implements ConnectionsProvider {

        private final Map<Point, MapPoint> pointToGameObject;

        public ConnectedFlagsAndBuildingsProvider(Map<Point, MapPoint> pointToGameObject) {
            this.pointToGameObject = pointToGameObject;
        }

        @Override
        public Iterable<Point> getPossibleConnections(Point start, Point goal) {
            MapPoint mapPoint = pointToGameObject.get(start);

            return mapPoint.getConnectedFlagsAndBuildings();
        }

        @Override
        public int realDistance(Point currentPoint, Point neighbor) {

            MapPoint mapPoint = pointToGameObject.get(currentPoint);

            int distance = Integer.MAX_VALUE;

            /* Find the shortest road that connects the two points */
            for (Road road : mapPoint.getConnectedRoads()) {
                if (!road.getStart().equals(currentPoint) && !road.getEnd().equals(currentPoint)) {
                    continue;
                }

                if (!road.getStart().equals(neighbor) && !road.getEnd().equals(neighbor)) {
                    continue;
                }

                /* Count the number of segments to walk (don't include the starting point) */
                int tmpDistance = road.getWayPoints().size() - 1;

                if (tmpDistance < distance) {
                    distance = tmpDistance;
                }

                if (distance == 2) {
                    return 2;
                }
            }

            return distance;
        }
    }

    /**
     * Finds the shortest path following roads between any two points. The points
     * must  be flags or buildings.
     *
     * The path returned will only contain points with flags or buildings.
     *
     * @param start The point to start from
     * @param goal The point to reach
     * @param mapPoints The map with information about each point on the map
     * @return the list of flag points to pass (included the starting point) required to travel from start to goal
     */
    // FIXME: ALLOCATION HOTSPOT
    static List<Point> findShortestPathViaRoads(Point start, Point goal, Map<Point, MapPoint> mapPoints) {
        Set<Point>          evaluated        = new HashSet<>();
        Map<Point, Integer> costToGetToPoint = new HashMap<>();
        Map<Point, Point>   cameFrom         = new HashMap<>();
        int                 bestCaseCost;

        PriorityQueue<PointAndCost> toEvaluatePriorityQueue = new PriorityQueue<>();

        /* Define starting parameters */
        bestCaseCost = getDistanceInGameSteps(start, goal);
        costToGetToPoint.put(start, 0);

        PointAndCost startingPointAndCost = new PointAndCost(start, bestCaseCost);

        toEvaluatePriorityQueue.add(startingPointAndCost);

        /* Declare variables outside of the loop to keep memory churn down */
        PointAndCost currentPoint;
        int newCostToGetToPoint;

        while (!toEvaluatePriorityQueue.isEmpty()) {

            /* Find the point to evaluate with the lowest estimated full cost */
            currentPoint = toEvaluatePriorityQueue.poll();

            /* Handle if the goal is reached */
            if (goal.equals(currentPoint.point)) {
                Point previousPoint = currentPoint.point;
                List<Point> path = new ArrayList<>();

                /* Re-construct the path taken */
                while (previousPoint != start) {
                    path.add(0, previousPoint);

                    previousPoint = cameFrom.get(previousPoint);
                }

                path.add(0, start);

                return path;
            }

            /* Do not re-evaluate the same point */
            evaluated.add(currentPoint.point);

            /* Evaluate each neighbor directly connected by a road */
            MapPoint mapPoint = mapPoints.get(currentPoint.point);
            for (Road road : mapPoint.getConnectedRoads()) {

                Point neighbor = road.getOtherPoint(currentPoint.point);

                /* Skip already evaluated points */
                if (evaluated.contains(neighbor)) {
                    continue;
                }

                /* Calculate the real cost to reach the neighbor from the start */
                newCostToGetToPoint = costToGetToPoint.get(currentPoint.point) + road.getWayPoints().size() - 1;

                /* Check if the neighbor hasn't been evaluated yet or if we have found a cheaper way to reach it */
                int currentCostToGetToPoint = costToGetToPoint.getOrDefault(neighbor, Integer.MAX_VALUE);

                if (newCostToGetToPoint < currentCostToGetToPoint) {

                    /* Keep track of how the neighbor was reached */
                    cameFrom.put(neighbor, currentPoint.point);

                    /* Remember the cost to reach the neighbor */
                    costToGetToPoint.put(neighbor, newCostToGetToPoint);

                    /* Remember the estimated full cost to go via the neighbor */
                    int estimatedFullCostThroughPoint = costToGetToPoint.get(neighbor) + getDistanceInGameSteps(neighbor, goal);

                    /* Add the neighbor to the evaluation list */
                    PointAndCost neighborPointAndCost = new PointAndCost(neighbor, estimatedFullCostThroughPoint);

                    toEvaluatePriorityQueue.add(neighborPointAndCost);
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
    static boolean arePointsConnectedByRoads(Point start, Point goal, Map<Point, MapPoint> mapPoints) {
        Set<Point>         evaluated         = new HashSet<>();
        Set<Point>         toEvaluate        = new HashSet<>();
        Map<Point, Double> realCostToPoint   = new HashMap<>();
        Map<Point, Double> estimatedFullCost = new HashMap<>();
        double             bestCaseCost;

        /* Define starting parameters */
        bestCaseCost = getDistanceInGameSteps(start, goal);
        toEvaluate.add(start);
        realCostToPoint.put(start, (double)0);
        estimatedFullCost.put(start, bestCaseCost);

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
            MapPoint mapPoint = mapPoints.get(currentPoint);
            for (Road road : mapPoint.getConnectedRoads()) {

                Point neighbor = road.getOtherPoint(currentPoint);

                /* Skip already evaluated points */
                if (evaluated.contains(neighbor)) {
                    continue;
                }

                /* Calculate the real cost to reach the neighbor from the start */
                tentativeCost = realCostToPoint.get(currentPoint) +
                        road.getWayPoints().size() - 1;

                /* Check if the neighbor hasn't been evaluated yet or if we have found a cheaper way to reach it */
                if (!toEvaluate.contains(neighbor) || tentativeCost < realCostToPoint.get(neighbor)) {

                    /* Remember the cost to reach the neighbor */
                    realCostToPoint.put(neighbor, tentativeCost);

                    /* Remember the estimated full cost to go via the neighbor */
                    estimatedFullCost.put(neighbor, realCostToPoint.get(neighbor) + getDistanceInGameSteps(neighbor, goal));

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
    static List<Point> findShortestDetailedPathViaRoads(EndPoint startEndPoint, EndPoint goalEndPoint, Map<Point, MapPoint> mapPoints) {
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
                    int numberWayPoints = road.getWayPoints().size();

                    if (road.getStart().equals(currentPoint)) {

                        for (int i = 1; i < numberWayPoints; i++) {
                            path.add(0, road.getWayPoints().get(i));
                        }

                        currentPoint = road.getEnd();
                    } else {
                        for (int i = numberWayPoints - 2; i >= 0; i--) {
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
            MapPoint mapPoint = mapPoints.get(currentPoint);
            for (Road road : mapPoint.getConnectedRoads()) {

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
    public static boolean areBuildingsOrFlagsConnected(EndPoint startEndPoint, EndPoint goalEndPoint, Map<Point, MapPoint> mapPoints) {
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

                    if (currentEstimatedCost == bestCaseCost) { // FIXME: compares two doubles
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
            MapPoint mapPoint = mapPoints.get(currentPoint);
            for (Road road : mapPoint.getConnectedRoads()) {

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

    static Storehouse getClosestStorageOffroad(Player player, Point point) {
        int distance = Integer.MAX_VALUE;
        Storehouse storehouse = null;
        GameMap map = player.getMap();

        for (Building building : player.getBuildings()) {

            /* Filter buildings that are not ready */
            if (building.isBurningDown() || building.isDestroyed() || building.isUnderConstruction()) {
                continue;
            }

            /* Filter other buildings than storage buildings */
            if (!building.isStorehouse()) {
                continue;
            }

            List<Point> pathToStorage = map.findWayOffroad(point, building.getPosition(), null);

            /* Filter storage buildings that can't be reached */
            if (pathToStorage == null) {
                continue;
            }

            int currentDistance = pathToStorage.size();

            if (currentDistance < distance) {
                storehouse = (Storehouse)building;
                distance = currentDistance;
            }
        }

        return storehouse;
    }

    public static Storehouse getClosestStorageConnectedByRoads(Point point, Player player) throws InvalidRouteException {
        return getClosestStorageConnectedByRoads(point, null, player);
    }

    // FIXME: HOTSPOT
    public static Storehouse getClosestStorageConnectedByRoads(Point point, Building avoid, Player player) throws InvalidRouteException {
        Storehouse storehouse = null;
        int distance = Integer.MAX_VALUE;
        GameMap map = player.getMap();

        for (Building building : player.getBuildings()) {

            /* Filter buildings to avoid */
            if (building.equals(avoid)) {
                continue;
            }

            /* Filter buildings that are under construction or destroyed */
            if (building.isBurningDown() || building.isDestroyed() || building.isUnderConstruction()) {
                continue;
            }

            if (building.isStorehouse()) {
                if (building.getFlag().getPosition().equals(point)) {
                    storehouse = (Storehouse)building;
                    break;
                }

                /* Filter buildings that cannot be closer than the current */
                int bestCaseDistance = getDistanceInGameSteps(point, building.getFlag().getPosition()) + 1;

                if (bestCaseDistance > distance) {
                    continue;
                }

                List<Point> path = map.findWayWithExistingRoads(point, building.getFlag().getPosition());

                /* Filter points that can't be reached */
                if (path == null) {
                    continue;
                }

                if (path.size() < distance) {
                    distance = path.size();
                    storehouse = (Storehouse) building;
                }
            }
        }

        return storehouse;
    }

    public static Storehouse getClosestStorageConnectedByRoads(Point point, GameMap map) throws InvalidRouteException {
        return getClosestStorageConnectedByRoads(point, null, map);
    }

    public static Storehouse getClosestStorageConnectedByRoads(Point point, Building avoid, GameMap map) throws InvalidRouteException {
        Storehouse storehouse = null;
        int distance = Integer.MAX_VALUE;

        for (Building building : map.getBuildings()) {

            /* Filter buildings to avoid */
            if (building.equals(avoid)) {
                continue;
            }

            /* Filter buildings that are under construction or destroyed */
            if (building.isBurningDown() || building.isDestroyed() || building.isUnderConstruction()) {
                continue;
            }

            /* Filter buildings that are not storehouses */
            if (!building.isStorehouse()) {
                continue;
            }

            /* If the building has its flag on the point we know we have found the closest building */
            if (building.getFlag().getPosition().equals(point)) {
                storehouse = (Storehouse)building;
                break;
            }

            /* Filter buildings that cannot be closer than the current */
            int bestCaseDistance = getDistanceInGameSteps(point, building.getFlag().getPosition()) + 1;

            if (bestCaseDistance > distance) {
                continue;
            }

            List<Point> path = map.findWayWithExistingRoads(point, building.getFlag().getPosition());

            if (path == null) {
                continue;
            }

            if (path.size() < distance) {
                distance = path.size();
                storehouse = (Storehouse) building;
            }
        }

        return storehouse;
    }

    public static Storehouse getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(Point point, Building avoid, GameMap map, Material material) throws InvalidRouteException {
        Storehouse storehouse = null;
        int distance = Integer.MAX_VALUE;

        for (Building building : map.getBuildings()) {

            /* Filter buildings to avoid */
            if (building.equals(avoid)) {
                continue;
            }

            /* Filter buildings that are destroyed */
            if (building.isBurningDown() || building.isDestroyed() || building.isUnderConstruction()) {
                continue;
            }

            /* Filter buildings that are not storehouses */
            if (!building.isStorehouse()) {
                continue;
            }

            /* Filter storehouses where the delivery is not allowed */
            if (((Storehouse)building).isDeliveryBlocked(material)) {
                continue;
            }

            /* If the building has its flag on the point we know we have found the closest building */
            if (building.getFlag().getPosition().equals(point)) {
                storehouse = (Storehouse)building;
                break;
            }

            List<Point> path = map.findWayWithExistingRoads(point, building.getFlag().getPosition());

            if (path == null) {
                continue;
            }

            if (path.size() < distance) {
                distance = path.size();
                storehouse = (Storehouse) building;
            }
        }

        return storehouse;
    }

    public static Set<Building> getBuildingsWithinReach(Flag startFlag) {
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

            MapPoint mapPoint = map.getMapPoint(point);

            /* Test if this point is connected to a building */
            if (mapPoint.isBuilding()) {
                reachable.add(mapPoint.getBuilding());
            }

            /* Remember that this point has been tested */
            visited.add(point);

            /* Go through the neighbors and add the new points to the list to be evaluated */
            for (Road road : mapPoint.getConnectedRoads()) {

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

    static int getDistanceInGameSteps(Point start, Point end) {
        int distanceX = abs(start.x - end.x);
        int distanceY = abs(start.y - end.y);

        if (distanceX > distanceY) {
            return distanceY + (distanceX - distanceY) / 2;
        } else {
            return distanceY;
        }
    }

    public static double calculateAngle(int directionX, int directionY) {
        double angle;

        if (directionX == 0 && directionY > 0) {
            angle = Math.PI / 2;
        } else if (directionX == 0 && directionY < 0) {
            angle = 1.5 * Math.PI;
        } else if (directionY == 0 && directionX > 0) {
            angle = 0;
        } else if (directionY == 0 && directionX < 0) {
            angle = Math.PI;
        } else {

            double length = Math.sqrt(directionX*directionX + directionY*directionY);

            angle = Math.atan((double)directionY / (double)directionX);

            if (directionX < 0 && directionY > 0) {
                angle = angle + Math.PI;
            }

            if (directionX < 0 && directionY < 0) {
                angle = angle + Math.PI;
            }

            if (directionX > 0 && directionY < 0) {
                angle = angle = 2*Math.PI;
            }
        }

        return angle;
    }
}
