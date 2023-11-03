/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.round;
import static org.appland.settlers.model.Direction.DOWN;
import static org.appland.settlers.model.Direction.DOWN_LEFT;
import static org.appland.settlers.model.Direction.DOWN_RIGHT;
import static org.appland.settlers.model.Direction.LEFT;
import static org.appland.settlers.model.Direction.RIGHT;
import static org.appland.settlers.model.Direction.UP;
import static org.appland.settlers.model.Direction.UP_LEFT;
import static org.appland.settlers.model.Direction.UP_RIGHT;
import static org.appland.settlers.model.Material.COIN;

/**
 *
 * @author johan
 */
public class GameUtils {

    public static boolean setContainsNone(Set<Point> set, List<Point> needles) {
        for (Point point : needles) {
            if (set.contains(point)) {
                return false;
            }
        }

        return true;
    }

    public static boolean setContainsAny(Set<Point> discoveredLand, List<Point> wayPoints) {
        for (Point point : wayPoints) {
            if (discoveredLand.contains(point)) {
                return true;
            }
        }

        return false;
    }

    static Direction getDirectionBetweenPoints(Point from, Point to) {
        Direction direction = null;

        if (to.x == from.x + 2 && to.y == from.y) {
            direction = RIGHT;
        } else if (to.x == from.x + 1 && to.y == from.y + 1) {
            direction = UP_RIGHT;
        } else if (to.x == from.x + 1 && to.y == from.y - 1) {
            direction = DOWN_RIGHT;
        } else if (to.x == from.x - 2 && to.y == from.y) {
            direction = LEFT;
        } else if (to.x == from.x - 1 && to.y == from.y + 1) {
            direction = UP_LEFT;
        } else if (to.x == from.x - 1 && to.y == from.y - 1) {
            direction = DOWN_LEFT;
        }

        if (direction == null) {
            throw new RuntimeException("Direction is null which should be impossible. From " + from + " to " + to);
        }

        return direction;
    }

    public static class BuildingAndData<B extends Building, D> {
        private final D data;
        private final B building;

        public BuildingAndData(B building, D data) {
            this.data = data;
            this.building = building;
        }

        public D getData() {
            return data;
        }

        public B getBuilding() {
            return building;
        }
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

    public static Set<Point> getHexagonAreaAroundPoint(Point position, int radius, GameMap map) {
        Set<Point> area = new HashSet<>();

        int xStart = position.x - radius;
        int xEnd = position.x + radius;

        for (int y = position.y - radius; y < position.y; y++) {
            for (int x = xStart; x <= xEnd; x += 2) {
                if (x < 0 || y < 0 || x > map.getWidth() || y > map.getHeight()) {
                    continue;
                }

                area.add(new Point(x, y));
            }

            xStart--;
            xEnd++;
        }

        xStart = position.x - radius;
        xEnd = position.x + radius;

        for (int y = position.y + radius; y >= position.y; y--) {
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

            /* Filter buildings that are not operational */
            if (!building.isReady()) {
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

    public static <T> boolean isAll(Collection<T> collection, T item) {
        for (T itemInList : collection) {
            if (!Objects.equals(itemInList, item)) {
                return false;
            }
        }

        return true;
    }

    public static <T> boolean isAny(Collection<T> collection, T item) {
        for (T itemInList : collection) {
            if (Objects.equals(itemInList, item)) {
                return true;
            }
        }

        return false;
    }

    public static <T> boolean isSomeButNotAll(Collection<T> collection, T item) {
        boolean foundMatch = false;
        boolean foundNotMatch = false;

        for (T itemInList : collection) {
            if (Objects.equals(itemInList, item)) {
                foundMatch = true;

                continue;
            }

            foundNotMatch = true;
        }

        return foundMatch && foundNotMatch;
    }

    public static void upgradeMilitaryBuilding(Building fromBuilding, Building upgraded) {

        /* Set the map in the upgraded building */
        upgraded.setMap(fromBuilding.getMap());

        /* Pre-construct the upgraded building */
        upgraded.setConstructionReady();

        /* Set the position of the upgraded building so the soldiers can enter */
        upgraded.setPosition(fromBuilding.getPosition());

        /* Replace the buildings on the map */
        fromBuilding.getMap().replaceBuilding(upgraded, fromBuilding.getPosition());

        /* Ensure that the new building is occupied */
        if (fromBuilding.isOccupied()) {
            upgraded.setOccupied();
        }

        /* Move the soldiers to the new building */
        int currentMilitary = fromBuilding.getNumberOfHostedMilitary();

        for (int i = 0; i < currentMilitary; i++) {

            /* Move one military from the old to the new building */
            Military military = fromBuilding.retrieveHostedSoldier();

            upgraded.promiseMilitary(military);
            military.enterBuilding(upgraded);
        }

        /* Make sure the border is updated only once */
        if (upgraded.getNumberOfHostedMilitary() == 0) {
            fromBuilding.getMap().updateBorder(fromBuilding, BorderChangeCause.MILITARY_BUILDING_OCCUPIED);
        }

        /* Move the coins to the new building */
        int amountCoins = fromBuilding.getAmount(COIN);
        for (int i = 0; i < amountCoins; i++) {

            /* Put one coin in the new building */
            Cargo coinCargo = new Cargo(COIN, fromBuilding.getMap());

            upgraded.promiseDelivery(COIN);

            upgraded.putCargo(coinCargo);
        }
    }

    public static boolean isEven(int i) {
        return i % 2 == 0;
    }

    /**
     * Returns true if each item in the collection is part of the given set
     *
     * @param items The items to determine if the given collection contains
     * @param collection The collection that may contain all the given items
     * @param <T> The type of item
     * @return True if the set of items is a subset of the collection
     */
    public static <T> boolean areAllOneOf(Collection<T> items, Set<T> collection) {
        for (T itemInList : items) {
            if (!collection.contains(itemInList)) {
                return false;
            }
        }

        return true;
    }

    public static <T> boolean areNonePartOf(Collection<T> items, Set<T> collection) {
        for (T item : items) {
            if (collection.contains(item)) {
                return false;
            }
        }

        return true;
    }

    public static <T> boolean areAnyOneOf(List<T> items, Set<T> collection) {
        for (T item : items) {
            if (collection.contains(item)) {
                return true;
            }
        }

        return false;
    }

    static boolean areAllUnique(Collection<Point> points) {

        Set<Point> pointsSet = new HashSet<>(points);

        return points.size() == pointsSet.size();
    }

    public static Direction getDirection(Point from, Point to) {
        int deltaX = to.x - from.x;
        int deltaY = to.y - from.y;

        /* To the right */
        if (deltaX > 0) {

            /* Above */
            if (deltaY > 0) {

                if (deltaY > deltaX * 2) {
                    return UP;
                }

                if (deltaX > deltaY * 2) {
                    return RIGHT;
                }

                return UP_RIGHT;

            /* Below */
            } else {

                if (abs(deltaY) > deltaX * 2) {
                    return DOWN;
                }

                if (deltaX > abs(deltaY) * 2) {
                    return RIGHT;
                }

                return DOWN_RIGHT;

            }

        /* To the left */
        } else {

            /* Above */
            if (deltaY > 0) {

                if (deltaY > abs(deltaX) * 2) {
                    return UP;
                }

                if (abs(deltaX) > deltaY * 2) {
                    return LEFT;
                }

                return UP_LEFT;

            /* Below */
            } else {

                if (deltaY < deltaX * 2) {
                    return DOWN;
                }

                if (deltaX < deltaY * 2) {
                    return LEFT;
                }

                return DOWN_LEFT;
            }
        }
    }

    public static Point getClosestWaterPoint(Point position, GameMap map) {
        int distanceToClosestWater = Integer.MAX_VALUE;
        Point pointClosestWater = null;

        for (Point point : GameUtils.getHexagonAreaAroundPoint(position, 4, map)) {

            /* Filter points that are not connected to water */
            if (!isAny(map.getSurroundingTiles(point), DetailedVegetation.WATER)) {
                continue;
            }

            int candidateDistance = getDistanceInGameSteps(position, point);

            /* Filter points that are not closer than the current best pick */
            if (candidateDistance >= distanceToClosestWater){
                continue;
            }

            distanceToClosestWater = candidateDistance;
            pointClosestWater = point;
        }

        return pointClosestWater;
    }

    public static Point getClosestWaterPointForBuilding(Building building) {
        GameMap map = building.getMap();

        Point position = building.getPosition();

        return getClosestWaterPoint(position, map);
    }

    public static int min(int... numbers) {
        int minimum = numbers[0];

        for (int number : numbers) {
            minimum = Math.min(minimum, number);
        }

        return minimum;
    }

    public static Harbor getClosestHarborOffroadForPlayer(Player player, Point position, int radius) {
        GameMap map = player.getMap();

        Harbor closestHarbor = null;
        int distanceToClosestHarbor = Integer.MAX_VALUE;

        for (Point point : GameUtils.getHexagonAreaAroundPoint(position, radius, map)) {

            Building building = map.getBuildingAtPoint(point);

            /* Filter points without a building */
            if (building == null) {
                continue;
            }

            /* Filter buildings that are not harbors */
            if (!Objects.equals(building.getClass(), Harbor.class)) {
                continue;
            }

            int candidateDistance = getDistanceInGameSteps(position, building.getPosition());

            /* Filter buildings that are further away than the current candidate */
            if (candidateDistance >= distanceToClosestHarbor) {
                continue;
            }

            closestHarbor = (Harbor) building;
            distanceToClosestHarbor = candidateDistance;
        }

        return closestHarbor;
    }

    public static void putCargos(Material material, int amount, Building building) {
        GameMap map = building.getMap();

        for (int i = 0; i < amount; i++) {
            Cargo cargo = new Cargo(material, map);

            building.promiseDelivery(material);

            building.putCargo(cargo);
        }
    }

    public static void putCargosOnFlag(Material material, int amount, Building building, Flag flag, GameMap map) {
        for (int i = 0; i < amount; i++) {
            Cargo cargo = new Cargo(material, map);

            cargo.setPosition(flag.getPosition());
            cargo.setTarget(building);

            building.promiseDelivery(material);

            flag.promiseCargo(cargo);

            flag.putCargo(cargo);
        }
    }

    public static void retrieveCargos(Storehouse storehouse, Material material, int amount) {
        for (int i = 0; i < amount; i++) {
            storehouse.retrieve(material);
        }
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

        @Override
        public String toString() {
            return " Point: " + point + ", cost: " + estimatedFullCostThroughPoint;
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

        /* Declare variables outside the loop to keep memory churn down */
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
                    int estimatedFullCostThroughPoint = newCostToGetToPoint + getDistanceInGameSteps(neighbor, goal);

                    /* Add the neighbor to the evaluation list */
                    PointAndCost neighborPointAndEstimatedCost = new PointAndCost(neighbor, estimatedFullCostThroughPoint);

                    toEvaluatePriorityQueue.add(neighborPointAndEstimatedCost);
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

        private final GameMap map;

        public PathOnExistingRoadsProvider(GameMap map) {
            this.map = map;
        }

        @Override
        public Iterable<Point> getPossibleConnections(Point start, Point goal) {
            MapPoint mapPoint = map.getMapPoint(start);

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
     * @param map The instance of the map
     * @param avoid List of points to avoid when finding the path
     * @return the list of flag points to pass (included the starting point) required to travel from start to goal
     */
    // FIXME: ALLOCATION HOTSPOT
    static List<Point> findShortestPathViaRoads(Point start, Point goal, GameMap map, Point... avoid) {
        Set<Point>          evaluated        = new HashSet<>();
        Map<Point, Integer> costToGetToPoint = new HashMap<>();
        Map<Point, Point>   cameFrom         = new HashMap<>();
        int                 bestCaseCost;

        PriorityQueue<PointAndCost> toEvaluatePriorityQueue = new PriorityQueue<>();

        Set<Point> avoidSet = null;

        if (avoid.length != 0) {
            avoidSet = new HashSet<>(Arrays.asList(avoid));
        }

        /* Define starting parameters */
        bestCaseCost = getDistanceInGameSteps(start, goal);
        costToGetToPoint.put(start, 0);

        PointAndCost startingPointAndCost = new PointAndCost(start, bestCaseCost);

        toEvaluatePriorityQueue.add(startingPointAndCost);

        /* Declare variables outside the loop to keep memory churn down */
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
            MapPoint mapPoint = map.getMapPoint(currentPoint.point);
            for (Road road : mapPoint.getConnectedRoads()) {

                Point neighbor = road.getOtherPoint(currentPoint.point);

                /* Skip already evaluated points */
                if (evaluated.contains(neighbor)) {
                    continue;
                }

                /* Skip points to avoid */
                if (avoidSet != null && avoidSet.contains(neighbor)) {
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
                    int estimatedFullCostThroughPoint = newCostToGetToPoint + getDistanceInGameSteps(neighbor, goal);

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
     * @param map The game map instance
     * @return true if the start and end are connected
     */
    static boolean arePointsConnectedByRoads(Point start, Point goal, GameMap map) {
        Map<Point, Integer> costToGetToPoint = new HashMap<>();
        int                 bestCaseCost;

        PriorityQueue<PointAndCost> toEvaluatePriorityQueue = new PriorityQueue<>();

        /* Define starting parameters */
        bestCaseCost = getDistanceInGameSteps(start, goal);
        costToGetToPoint.put(start, 0);

        PointAndCost startingPointAndCost = new PointAndCost(start, bestCaseCost);

        toEvaluatePriorityQueue.add(startingPointAndCost);

        /* Declare variables outside the loop to keep memory churn down */
        PointAndCost currentPoint;
        int newCostToGetToPoint;

        while (!toEvaluatePriorityQueue.isEmpty()) {

            /* Find the point with the lowest estimated full cost */
            currentPoint = toEvaluatePriorityQueue.poll();

            /* Handle if the goal is reached */
            if (goal.equals(currentPoint.point)) {
                return true;
            }

            /* Evaluate each direct neighbor */
            MapPoint mapPoint = map.getMapPoint(currentPoint.point);

            for (Road road : mapPoint.getConnectedRoads()) {

                Point neighbor = road.getOtherPoint(currentPoint.point);

                /* Calculate the real cost to reach the neighbor from the start */
                newCostToGetToPoint = costToGetToPoint.get(currentPoint.point) + road.getWayPoints().size() - 1;

                /* Check if the neighbor hasn't been evaluated yet or if we have found a cheaper way to reach it */
                int currentCostToGetToPoint = costToGetToPoint.getOrDefault(neighbor, Integer.MAX_VALUE);

                /* Check if the neighbor hasn't been evaluated yet or if we have found a cheaper way to reach it */
                if (newCostToGetToPoint < currentCostToGetToPoint) {

                    /* Remember the cost to reach the neighbor */
                    costToGetToPoint.put(neighbor, newCostToGetToPoint);

                    /* Remember the estimated full cost to go via the neighbor */
                    int estimatedFullCostThroughPoint = newCostToGetToPoint + getDistanceInGameSteps(neighbor, goal);

                    /* Add the neighbor to the evaluation list */
                    PointAndCost neighborPointAndCost = new PointAndCost(neighbor, estimatedFullCostThroughPoint);

                    toEvaluatePriorityQueue.add(neighborPointAndCost);
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
     * @param map The instance of the map
     * @return a detailed list with the steps required to travel from the start to the goal.
     */
    static List<Point> findShortestDetailedPathViaRoads(EndPoint startEndPoint, EndPoint goalEndPoint, GameMap map, Point... avoid) {
        Set<Point>         evaluated         = new HashSet<>();
        Set<Point>         toEvaluate        = new HashSet<>();
        Map<Point, Double> realCostToPoint   = new HashMap<>();
        Map<Point, Double> estimatedFullCost = new HashMap<>();
        Map<Point, Road>   cameVia           = new HashMap<>();
        double             bestCaseCost;

        Set<Point> avoidSet = null;

        if (avoid.length != 0) {
            avoidSet = new HashSet<>(Arrays.asList(avoid));
        }

        Point start = startEndPoint.getPosition();
        Point goal = goalEndPoint.getPosition();

        /* Define starting parameters */
        bestCaseCost = start.distance(goal);
        toEvaluate.add(start);
        realCostToPoint.put(start, (double)0);
        estimatedFullCost.put(start, realCostToPoint.get(start) + start.distance(goal));

        /* Declare variables outside the loop to keep memory churn down */
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
            if (Objects.equals(currentPoint, goal)) {
                List<Point> path = new ArrayList<>();

                /* Re-construct the path taken, backwards from the goal */
                while (!currentPoint.equals(start)) {
                    Road road = cameVia.get(currentPoint);

                    /* Follow the road and add up the points */
                    int numberWayPoints = road.getWayPoints().size();

                    List<Point> roadPoints = road.getWayPoints();

                    if (roadPoints.get(0).equals(currentPoint)) {

                        for (int i = 0; i < numberWayPoints - 1; i++) {
                            path.add(0, roadPoints.get(i));
                        }

                        currentPoint = roadPoints.get(roadPoints.size() - 1);
                    } else {
                        for (int i = numberWayPoints - 1; i > 0; i--) {
                            path.add(0, roadPoints.get(i));
                        }

                        currentPoint = roadPoints.get(0);
                    }
                }

                path.add(0, currentPoint);

                return path;
            }

            /* Do not re-evaluate the same point */
            toEvaluate.remove(currentPoint);
            evaluated.add(currentPoint);

            /* Evaluate each direct neighbor */
            MapPoint mapPoint = map.getMapPoint(currentPoint);
            for (Road road : mapPoint.getConnectedRoads()) {

                Point neighbor = road.getOtherPoint(currentPoint);

                /* Skip already evaluated points */
                if (evaluated.contains(neighbor)) {
                    continue;
                }

                /* Skip points to avoid */
                if (avoidSet != null && avoidSet.contains(neighbor)) {
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
     * @param map The instance of the map
     * @return true if the start and end are connected
     */
    public static boolean areBuildingsOrFlagsConnected(EndPoint startEndPoint, EndPoint goalEndPoint, GameMap map) {
        Set<Point>          evaluated        = new HashSet<>();
        Map<Point, Integer> costToGetToPoint = new HashMap<>();
        int                 bestCaseCost;

        PriorityQueue<PointAndCost> toEvaluatePriorityQueue = new PriorityQueue<>();

        /* Define starting parameters */
        Point start = startEndPoint.getPosition();
        Point goal = goalEndPoint.getPosition();

        bestCaseCost = getDistanceInGameSteps(start, goal);
        costToGetToPoint.put(start, 0);

        PointAndCost startingPointAndCost = new PointAndCost(start, bestCaseCost);

        toEvaluatePriorityQueue.add(startingPointAndCost);

        /* Declare variables outside the loop to keep memory churn down */
        PointAndCost currentPoint;
        int newCostToGetToPoint;

        while (!toEvaluatePriorityQueue.isEmpty()) {

            /* Find the point to evaluate with the lowest estimated full cost */
            currentPoint = toEvaluatePriorityQueue.poll();

            /* Handle if the goal is reached */
            if (goal.equals(currentPoint.point)) {
                return true;
            }

            /* Do not re-evaluate the same point */
            evaluated.add(currentPoint.point);

            /* Evaluate each direct neighbor */
            MapPoint mapPoint = map.getMapPoint(currentPoint.point);
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

                    /* Remember the cost to reach the neighbor */
                    costToGetToPoint.put(neighbor, newCostToGetToPoint);

                    /* Remember the estimated full cost to go via the neighbor */
                    int estimatedFullCostThroughPoint = newCostToGetToPoint + getDistanceInGameSteps(neighbor, goal);

                    /* Add the neighbor to the evaluation list */
                    PointAndCost neighborPointAndCost = new PointAndCost(neighbor, estimatedFullCostThroughPoint);

                    toEvaluatePriorityQueue.add(neighborPointAndCost);
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
            if (!building.isReady()) {
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

    public static Storehouse getClosestStorageConnectedByRoads(Point point, Player player) {
        return getClosestStorageConnectedByRoads(point, null, player);
    }

    // FIXME: HOTSPOT
    public static Storehouse getClosestStorageConnectedByRoads(Point point, Building avoid, Player player) {
        Storehouse storehouse = null;
        int distance = Integer.MAX_VALUE;
        GameMap map = player.getMap();

        for (Building building : player.getBuildings()) {

            /* Filter buildings to avoid */
            if (building.equals(avoid)) {
                continue;
            }

            /* Filter buildings that are not ready */
            if (!building.isReady()) {
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

    public static Storehouse getClosestStorageConnectedByRoads(Point point, GameMap map) {
        return getClosestStorageConnectedByRoads(point, null, map);
    }

    public static Storehouse getClosestStorageConnectedByRoads(Point point, Building avoid, GameMap map) {
        Storehouse storehouse = null;
        int distance = Integer.MAX_VALUE;

        for (Building building : map.getBuildings()) {

            /* Filter buildings to avoid */
            if (building.equals(avoid)) {
                continue;
            }

            /* Filter buildings that are under construction or destroyed */
            if (!building.isReady()) {
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

    public static Storehouse getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(Point point, Building avoid, GameMap map, Material material) {
        Storehouse storehouse = null;
        int distance = Integer.MAX_VALUE;

        for (Building building : map.getBuildings()) {

            /* Filter buildings to avoid */
            if (building.equals(avoid)) {
                continue;
            }

            /* Filter buildings that are destroyed */
            if (!building.isReady()) {
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

    public static Set<Flag> findFlagsReachableFromPoint(Player player, Point start) {
        List<Point> toEvaluate = new LinkedList<>();
        Set<Point>  visited    = new HashSet<>();
        Set<Flag>   reachable  = new HashSet<>();
        GameMap     map        = player.getMap();

        toEvaluate.add(start);

        /* Declare variables outside the loop to keep memory churn down */
        Point point;
        Point oppositePoint;

        while (!toEvaluate.isEmpty()) {

            point = toEvaluate.get(0);
            toEvaluate.remove(point);

            MapPoint mapPoint = map.getMapPoint(point);

            /* Test if this point is connected to a building */
            if (mapPoint.isFlag()) {
                reachable.add(mapPoint.getFlag());
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

    public static Set<Building> getBuildingsWithinReach(Flag startFlag) {
        List<Point>   toEvaluate = new LinkedList<>();
        Set<Point>    visited    = new HashSet<>();
        Set<Building> reachable  = new HashSet<>();
        Player        player     = startFlag.getPlayer();
        GameMap       map        = player.getMap();

        toEvaluate.add(startFlag.getPosition());

        /* Declare variables outside the loop to keep memory churn down */
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

    public static int getDistanceInGameSteps(Point start, Point end) {
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

            angle = Math.atan((double)directionY / (double)directionX);

            if (directionX < 0 && directionY > 0) {
                angle = angle + Math.PI;
            }

            if (directionX < 0 && directionY < 0) {
                angle = angle + Math.PI;
            }

            if (directionX > 0 && directionY < 0) {
                angle = angle - 2 * Math.PI;
            }
        }

        return angle;
    }
}
