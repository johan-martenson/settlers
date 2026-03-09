package org.appland.settlers.model;

import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Harbor;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.utils.Search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.lang.Math.*;
import static java.lang.String.format;
import static org.appland.settlers.model.Direction.*;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.utils.Search.*;

/**
 *
 * @author johan
 */
public class GameUtils {

    public static <T extends Building> T findClosestBuildingViaRoads(
            Point start,
            GameMap map,
            Building avoid,
            Function<Building, Boolean> predicate
    ) {
        return dijkstraClosest(
                start,
                point -> {
                    var mapPoint = map.getMapPoint(point);

                    if (!mapPoint.isBuilding()) {
                        return null;
                    }

                    var building = mapPoint.getBuilding();

                    if (!building.equals(avoid) && predicate.apply(building)) {
                        @SuppressWarnings("unchecked")
                        var result = (T) building;
                        return result;
                    }

                    return null;
                },
                p -> roadEdges(map, p)
        );
    }

    /**
     * Checks if all provided collections are empty.
     *
     * @param collections Varargs of collections to be checked.
     * @return True if all collections are empty, false otherwise.
     */
    public static boolean allCollectionsEmpty(Collection<?>... collections) {
        return Arrays.stream(collections).allMatch(Collection::isEmpty);
    }

    /**
     * Checks if all provided maps are empty.
     *
     * @param maps Varargs of maps to be checked.
     * @return True if all maps are empty, false otherwise.
     */
    public static boolean allMapsEmpty(Map<?, ?>... maps) {
        return Arrays.stream(maps).allMatch(Map::isEmpty);
    }

    /**
     * Returns the headquarters for the given player
     *
     */
    public static Headquarter getHeadquarterForPlayer(Player player) {
        for (var building : player.getBuildings()) {
            if (building instanceof Headquarter) {
                return (Headquarter) building;
            }
        }

        return null;
    }

    /**
     * Returns the distance to the closest border point for the given point
     *
     */
    public static int getDistanceToBorder(Point infoPoint, Player player) {
        int distance = Integer.MAX_VALUE;

        for (var point : player.getBorderPoints()) {
            int tmpDistance = distanceInGameSteps(point, infoPoint);

            if (tmpDistance < distance) {
                distance = tmpDistance;
            }
        }

        return distance;
    }

    public static <T> void setAll(Collection<T> collectionToSet, Collection<T> source) {
        collectionToSet.clear();
        collectionToSet.addAll(source);
    }

    public static void discoverFullMap(Player player) {
        player.getMap().getPointsInMap().forEach(player::discover);
    }

    /**
     * Checks if any of the points in the specified list exists in the given set.
     *
     * @param set   The set of discovered land points.
     * @param list  The list of waypoints to check for presence in the set.
     * @return True if any of the points are in the set, false otherwise.
     */
    public static boolean setContainsAny(Set<Point> set, List<Point> list) {
        return list.stream().anyMatch(set::contains);
    }

    public static Direction getDirectionBetweenPoints(Point from, Point to) {
        var direction = (Direction) null;

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

    /**
     * Finds the closest house or road from a starting point that matches the given criteria.
     *
     * @param start   The starting point for the search.
     * @param isMatch A function to determine if a HouseOrRoad matches the criteria.
     * @param map     The game map to search within.
     * @return An Optional containing the closest matching HouseOrRoad, or an empty Optional if none found.
     */
    public static Optional<HouseOrRoad> getClosestHouseOrRoad(
            Point start,
            Function<HouseOrRoad, Boolean> isMatch,
            GameMap map
    ) {
        var result = dijkstraClosest(
                start,
                point -> {

                    // Check building
                    if (map.isBuildingAtPoint(point)) {
                        var building = map.getBuildingAtPoint(point);
                        var houseOrRoad = new HouseOrRoad(building);

                        if (isMatch.apply(houseOrRoad)) {
                            return houseOrRoad;
                        }
                    }

                    // Check connected roads
                    for (var road : map.getMapPoint(point).getConnectedRoads()) {
                        var houseOrRoad = new HouseOrRoad(road);

                        if (isMatch.apply(houseOrRoad)) {
                            return houseOrRoad;
                        }
                    }

                    return null;
                },
                p -> roadEdges(map, p)
        );

        return Optional.ofNullable(result);
    }

    public static Set<Point> getHexagonAreaAroundPoint(Point position, int radius, GameMap map) {
        var area = new HashSet<Point>();

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

    /**
     * Finds the closest storehouse off-road where delivery is possible.
     *
     * @param point   The starting point to search from.
     * @param avoid   The building to avoid during the search.
     * @param player  The player owning the buildings.
     * @param material The material to be delivered.
     * @return The closest storehouse where delivery is possible, or null if no storehouse is found.
     */
    public static Building getClosestStorageOffroadWhereDeliveryIsPossible(Point point, Building avoid, Player player, Material material) {
        var map = player.getMap();

        return map.getBuildings().stream()
                .filter(building -> player.equals(building.getPlayer())) // Filter buildings that belong to another player
                .filter(building -> !building.equals(avoid)) // Filter buildings to avoid
                .filter(Building::isReady) // Filter buildings that are not operational
                .filter(Building::isStorehouse) // Filter buildings that are not storehouses
                .map(Storehouse.class::cast) // Cast Building to Storehouse
                .filter(storehouse -> !storehouse.isDeliveryBlocked(material)) // Filter storehouses where delivery is blocked
                .map(storehouse -> {
                    if (storehouse.getFlag().getPosition().equals(point)) {
                        return new Tuple<>(storehouse, 0);
                    }

                    var path = map.findWayOffroad(point, storehouse.getFlag().getPosition(), null);

                    return new Tuple<>(storehouse, path != null ? path.size() : Integer.MAX_VALUE);
                })
                .filter(tuple -> tuple.t2() != Integer.MAX_VALUE)
                .min(Comparator.comparingInt(Tuple::t2))
                .map(Tuple::t1)
                .orElse(null); // Return null if no storehouse is found
    }

    /**
     * Determines if all items in a collection are equal to a given item.
     *
     * @param collection The collection to check.
     * @param item       The item to compare against.
     * @param <T>        The type of the item.
     * @return True if all items are equal to the specified item, false otherwise.
     */
    public static <T> boolean isAll(Collection<T> collection, T item) {
        return collection.stream().allMatch(item::equals);
    }

    /**
     * Determines if some, but not all, items in a collection are equal to a given item.
     *
     * @param collection The collection to check.
     * @param item       The item to compare against.
     * @param <T>        The type of the item.
     * @return True if some but not all items are equal to the specified item, false otherwise.
     */
    public static <T> boolean isSomeButNotAll(Collection<T> collection, T item) {
        return collection.stream().anyMatch(item::equals) &&
               collection.stream().anyMatch(e -> !item.equals(e));
    }

    /**
     * Upgrades a military building, transferring soldiers and resources.
     *
     * @param fromBuilding The original building to be upgraded.
     * @param upgraded     The new upgraded building.
     */
    public static void upgradeMilitaryBuilding(Building fromBuilding, Building upgraded) {

        // Set the map in the upgraded building
        upgraded.setMap(fromBuilding.getMap());

        // Pre-construct the upgraded building
        upgraded.setConstructionReady();

        // Set the position of the upgraded building so the soldiers can enter
        upgraded.setPosition(fromBuilding.getPosition());

        // Replace the buildings on the map
        fromBuilding.getMap().replaceBuilding(upgraded, fromBuilding.getPosition());

        // Ensure that the new building is occupied
        if (fromBuilding.isOccupied()) {
            upgraded.setOccupied();
        }

        // Move the soldiers to the new building
        int currentMilitary = fromBuilding.getNumberOfHostedSoldiers();

        for (int i = 0; i < currentMilitary; i++) {

            // Move one military from the old to the new building
            var military = fromBuilding.retrieveHostedSoldier();

            upgraded.promiseSoldier(military);
            military.enterBuilding(upgraded);
        }

        // Make sure the border is updated only once
        if (upgraded.getNumberOfHostedSoldiers() == 0) {
            fromBuilding.getMap().updateBorder(fromBuilding, BorderChangeCause.MILITARY_BUILDING_OCCUPIED);
        }

        // Move the coins to the new building
        int amountCoins = fromBuilding.getAmount(COIN);
        for (int i = 0; i < amountCoins; i++) {
            var coinCargo = new Cargo(COIN, fromBuilding.getMap());

            upgraded.promiseDelivery(COIN);
            upgraded.putCargo(coinCargo);
        }
    }

    /**
     * Checks if an integer is even.
     *
     * @param i The integer to check.
     * @return True if the integer is even, false otherwise.
     */
    public static boolean isEven(int i) {
        return i % 2 == 0;
    }

    /**
     * Determines if any of the items in a list are part of a given set.
     *
     * @param <T>   The type of item.
     * @param set   The collection to check against.
     * @param items The list of items to check.
     * @return True if any of the items are in the collection, false otherwise.
     */
    public static <T> boolean containsAny(Set<T> set, Collection<T> items) {
        return items.stream().anyMatch(set::contains);
    }

    /**
     * Determines if all points in a collection are unique.
     *
     * @param points The collection of points to check.
     * @return True if all points are unique, false otherwise.
     */
    static boolean areAllUnique(Collection<Point> points) {
        var pointsSet = new HashSet<>(points);
        return points.size() == pointsSet.size();
    }

    public static Direction getDirection(Point from, Point to) {
        int deltaX = to.x - from.x;
        int deltaY = to.y - from.y;

        // To the right
        if (deltaX > 0) {

            // Above
            if (deltaY > 0) {
                if (deltaY > deltaX * 2) {
                    return UP;
                }

                if (deltaX > deltaY * 2) {
                    return RIGHT;
                }

                return UP_RIGHT;

            // Below
            } else {
                if (abs(deltaY) > deltaX * 2) {
                    return DOWN;
                }

                if (deltaX > abs(deltaY) * 2) {
                    return RIGHT;
                }

                return DOWN_RIGHT;

            }

        // To the left
        } else {

            // Above
            if (deltaY > 0) {
                if (deltaY > abs(deltaX) * 2) {
                    return UP;
                }

                if (abs(deltaX) > deltaY * 2) {
                    return LEFT;
                }

                return UP_LEFT;

            // Below
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

    /**
     * Finds the closest water point from a given position on the map.
     *
     * @param position The starting position to search from.
     * @param map      The game map containing water tiles.
     * @return The closest water point, or null if none is found.
     */
    public static Point getClosestWaterPoint(Point position, GameMap map) {
        return GameUtils.getHexagonAreaAroundPoint(position, 4, map).stream()
                .filter(point -> map.getSurroundingTiles(point).contains(Vegetation.WATER))
                .min(Comparator.comparingInt(point -> distanceInGameSteps(position, point)))
                .orElse(null);
    }

    /**
     * Finds the closest water point for a given building on the map.
     *
     * @param building The building for which the closest water point is to be found.
     * @return The closest water point, or null if none is found.
     */
    public static Point getClosestWaterPointForBuilding(Building building) {
        return getClosestWaterPoint(building.getPosition(), building.getMap());
    }

    /**
     * Finds the minimum value among the provided integers.
     *
     * @param numbers The integers to compare.
     * @return The minimum value among the numbers.
     */
    public static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(numbers[0]);
    }

    /**
     * Finds the closest harbor off-road for a given player within a specified radius.
     *
     * @param player   The player for whom to find the closest harbor.
     * @param position The starting position to search from.
     * @param radius   The radius within which to search for harbors.
     * @return The closest harbor off-road or null if no harbor is found within the radius.
     */
    public static Harbor getClosestHarborOffroadForPlayer(Player player, Point position, int radius) {
        var map = player.getMap();

        return player.getBuildings().stream()
                .filter(Building::isReady)
                .filter(Building::isHarbor)
                .map(Harbor.class::cast)
                .map(harbor -> {
                    var path = map.findWayOffroad(position, harbor.getPosition(), null);

                    if (path == null) {
                        return new Tuple<>(harbor, Integer.MAX_VALUE);
                    }

                    return new Tuple<>(harbor, path.size());
                })
                .filter(tuple -> tuple.t2() <= radius)
                .min(Comparator.comparingInt(Tuple::t2))
                .map(Tuple::t1)
                .orElse(null);
    }

    interface ConnectionsProvider {
        Iterable<Point> getPossibleConnections(Point start, Point goal);
    }

    /**
     * Represents a point and its estimated cost for pathfinding.
     * Used in priority queues for pathfinding algorithms.
     */
    public static class PointAndCost implements Comparable<PointAndCost> {
        public final Point point;
        private final int cost;

        public PointAndCost(Point point, int cost) {
            this.point = point;
            this.cost = cost;
        }

        // TODO: align with implementation of equals to make them consistent!
        @Override
        public int compareTo(PointAndCost pointAndCost) {
            return Integer.compare(this.cost, pointAndCost.cost);
        }

        @Override
        public String toString() {
            return format("Point: %s, cost: %d", point, cost);
        }
    }

    /**
     * Finds the shortest path between two points, avoiding specified points.
     *
     * @param start             The starting point.
     * @param goal              The goal point.
     * @param avoid             Set of points to avoid during pathfinding.
     * @param connectionProvider Provides connections between points.
     * @return The shortest path as a list of points, or null if no path is found.
     */
    static List<Point> findShortestPath(
            Point start,
            Point goal,
            Set<Point> avoid,
            ConnectionsProvider connectionProvider
    ) {
        return aStar(
                start,
                goal::equals,
                point -> {
                    var connections = connectionProvider.getPossibleConnections(point, goal);
                    var edges = new ArrayList<Search.Edge>();

                    for (var neighbor : connections) {
                        if (avoid != null && avoid.contains(neighbor)) {
                            continue;
                        }

                        edges.add(new Search.Edge(neighbor, distanceInGameSteps(point, neighbor)));
                    }

                    return edges;
                },
                point -> distanceInGameSteps(point, goal)
        );
    }

    /**
     * Finds the closest point to the given coordinates.
     *
     * @param px The x-coordinate.
     * @param py The y-coordinate.
     * @return The closest point as a Point object.
     */
    public static Point getClosestPoint(double px, double py) {

        // Round to integers
        int roundedX = (int) round(px);
        int roundedY = (int) round(py);

        // Calculate the error
        double errorX = abs(px - roundedX);
        double errorY = abs(py - roundedY);

        // Adjust the values if needed to avoid invalid points
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

    /**
     * Provides possible connections between points using existing roads on the map.
     */
    public static class PathOnExistingRoadsProvider implements ConnectionsProvider {
        private final GameMap map;

        public PathOnExistingRoadsProvider(GameMap map) {
            this.map = map;
        }

        @Override
        public Iterable<Point> getPossibleConnections(Point start, Point goal) {
            System.out.println("GET MAP POINT: " + map.getMapPoint(start));

            return map.getMapPoint(start).getConnectedNeighbors();
        }
    }

    /**
     * Finds the shortest path following roads between any two points. The points
     * must be flags or buildings. The path returned will only contain points with flags or buildings.
     *
     * @param start The point to start from.
     * @param goal  The point to reach.
     * @param map   The instance of the map.
     * @param avoid List of points to avoid when finding the path.
     * @return The list of flag points to pass (including the starting point) required to travel from start to goal.
     */
    static List<Point> findShortestPathViaRoads(Point start, Point goal, GameMap map, Point... avoid) {
        var avoidSet = avoid.length == 0 ? Set.of() : Set.of(avoid);

        return aStar(
                start,
                goal::equals,
                p -> roadEdges(map, p).stream()
                        .filter(e -> !avoidSet.contains(e.point()))
                        .toList(),
                p -> distanceInGameSteps(p, goal)
        );
    }

    /**
     * Determines whether any two points are connected by roads. The points
     * don't need to be flags or buildings but can be any point on a road.
     *
     * @param start The point to start from.
     * @param goal  The point to reach.
     * @param map   The game map instance.
     * @return True if the start and end are connected, false otherwise.
     */
    static boolean arePointsConnectedByRoads(Point start, Point goal, GameMap map) {
        var path = aStar(
                start,
                goal::equals,
                p -> roadEdges(map, p),
                p -> distanceInGameSteps(p, goal)
        );

        return path != null;
    }

    /**
     * Returns a detailed path including points between flags or buildings. Can
     * only be called with a building or flag as start and end point.
     *
     * @param startEndPoint Flag or building to start from.
     * @param goalEndPoint  Flag or building to reach.
     * @param map           The instance of the map.
     * @return A detailed list with the steps required to travel from the start to the goal.
     */
    static List<Point> findShortestDetailedPathViaRoads(
            EndPoint startEndPoint,
            EndPoint goalEndPoint,
            GameMap map,
            Point... avoid
    ) {
        var start = startEndPoint.getPosition();
        var goal = goalEndPoint.getPosition();

        var avoidSet = avoid.length == 0
                ? null
                : new HashSet<>(Arrays.asList(avoid));

        var coarsePath = aStar(
                start,
                goal::equals,
                p -> roadEdges(map, p, avoidSet),
                p -> distanceInGameSteps(p, goal)
        );

        if (coarsePath == null) {
            return null;
        }

        var detailedPath = new LinkedList<Point>();

        for (int i = 0; i < coarsePath.size() - 1; i++) {
            var from = coarsePath.get(i);
            var to = coarsePath.get(i + 1);

            for (var road : map.getMapPoint(from).getConnectedRoads()) {
                if (!road.getOtherPoint(from).equals(to)) {
                    continue;
                }

                var wayPoints = road.getWayPoints();

                if (wayPoints.getFirst().equals(from)) {
                    for (int j = 0; j < wayPoints.size() - 1; j++) {
                        detailedPath.addLast(wayPoints.get(j));
                    }
                } else {
                    for (int j = wayPoints.size() - 1; j > 0; j--) {
                        detailedPath.addLast(wayPoints.get(j));
                    }
                }

                break;
            }
        }

        detailedPath.addLast(goal);

        return detailedPath;
    }

    /**
     * Determines if two points with flags or buildings are connected by roads.
     *
     * @param start Flag or building to start from.
     * @param end   Flag or building to reach.
     * @param map   The instance of the map.
     * @return True if the start and end are connected, false otherwise.
     */
    public static boolean areBuildingsOrFlagsConnected(EndPoint start, EndPoint end, GameMap map) {
        return areBuildingsOrFlagsConnected(start.getPosition(), end.getPosition(), map);
    }

    /**
     * Determines if two points where there are buildings or flags are connected.
     * <p>
     * Note: this method does not work for points that are not flags or buildings.
     * @param start
     * @param end
     * @param map
     * @return
     */
    public static boolean areBuildingsOrFlagsConnected(Point start, Point end, GameMap map) {
        return bfsConnected(start, end::equals, p -> roadNeighbors(map, p));
    }

    /**
     * Finds the closest storehouse off-road for a given player from a specified point.
     *
     * @param player The player for whom the closest storehouse is to be found.
     * @param point  The starting point to search from.
     * @return The closest Storehouse building that is accessible off-road, or null if none are available.
     */
    public static Storehouse getClosestStorageOffroad(Player player, Point point) {
        var map = player.getMap();

        return player.getBuildings().stream()
                .filter(Building::isReady)
                .filter(Building::isStorehouse)
                .map(building -> new Tuple<>(
                        (Storehouse) building,
                        map.findWayOffroad(point, building.getPosition(), null)))
                .filter(entry -> entry.t2() != null)
                .min(Comparator.comparingInt(entry -> entry.t2().size()))
                .map(Tuple::t1)
                .orElse(null);
    }

    /**
     * Finds the closest storehouse connected by roads for a given player from a specified point.
     *
     * @param point  The starting point to search from.
     * @param player The player for whom the closest storehouse is to be found.
     * @return The closest Storehouse building that is connected by roads, or null if none are available.
     */
    public static Storehouse getClosestStorageConnectedByRoads(Point point, Player player) {
        return getClosestStorageConnectedByRoads(point, null, player);
    }

    /**
     * Finds the closest storehouse connected by roads to the given point for a specified player.
     *
     * @param point  The starting point from which to find the closest storehouse.
     * @param avoid  The building to avoid in the search.
     * @param player The player whose buildings will be considered in the search.
     * @return The closest storehouse connected by roads, or null if none is found.
     */
    public static Storehouse getClosestStorageConnectedByRoads(Point point, Building avoid, Player player) {
        return findClosestBuildingViaRoads(
                point,
                player.getMap(),
                avoid,
                building -> building.isReady() && building.isStorehouse()
        );
    }

    /**
     * Finds the closest storehouse connected by roads to the given point for a specified map.
     *
     * @param point The starting point from which to find the closest storehouse.
     * @param map   The game map containing the buildings.
     * @return The closest storehouse connected by roads, or null if none is found.
     */
    public static Storehouse getClosestStorageConnectedByRoads(Point point, GameMap map) {
        return getClosestStorageConnectedByRoads(point, null, map);
    }

    /**
     * A utility class representing a tuple of two elements.
     *
     * @param <T1> The type of the first element.
     * @param <T2> The type of the second element.
     */
    public record Tuple<T1, T2>(T1 t1, T2 t2) { }

    /**
     * Finds the closest storehouse connected by roads to the given point.
     *
     * @param point The starting point from which to find the closest storehouse.
     * @param avoid The building to avoid in the search.
     * @param map   The game map containing buildings and roads.
     * @return The closest storehouse connected by roads, or null if none is found.
     */
    public static Storehouse getClosestStorageConnectedByRoads(Point point, Building avoid, GameMap map) {
        return findClosestBuildingViaRoads(
                point,
                map,
                avoid,
                building -> building.isReady() && building.isStorehouse()
        );
    }

    /**
     * Finds the closest storehouse connected by roads where delivery is possible.
     *
     * @param point    The starting point from which to find the closest storehouse.
     * @param avoid    The building to avoid in the search.
     * @param map      The game map containing the buildings.
     * @param material The material for which delivery is possible.
     * @return The closest storehouse connected by roads where delivery is possible, or null if none is found.
     */
    public static Storehouse getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(Point point, Building avoid, GameMap map, Material material) {
        return findClosestBuildingViaRoads(
                point,
                map,
                avoid,
                building -> building.isReady() && building.isStorehouse() && !((Storehouse) building).isDeliveryBlocked(material)
        );
    }

    /**
     * Finds flags that are reachable from a starting point for a specific player.
     *
     * @param player The player whose flags are being searched.
     * @param start  The starting point for the search.
     * @return A set of flags reachable from the starting point.
     */
    public static Set<Flag> findFlagsReachableFromPoint(Player player, Point start) {
        var map = player.getMap();
        var reachable = new HashSet<Flag>();

        bfsTraverse(
                start,
                p -> roadNeighbors(map, p),
                point -> {
                    var mapPoint = map.getMapPoint(point);
                    if (mapPoint.isFlag()) {
                        reachable.add(mapPoint.getFlag());
                    }
                }
        );

        return reachable;
    }

    /**
     * Gets buildings within reach of a specified flag.
     *
     * @param flag The flag from which reachability is determined.
     * @return A set of buildings reachable from the flag.
     */
    public static Set<Building> getBuildingsWithinReach(Flag flag) {
        return getBuildingsWithinReach(flag.getPosition(), flag.getPlayer());
    }

    /**
     * Gets buildings within reach of a specified starting position and player.
     *
     * @param startPosition The starting position for reachability.
     * @param player        The player owning the map.
     * @return A set of buildings reachable from the starting position.
     */
    public static Set<Building> getBuildingsWithinReach(Point startPosition, Player player) {
        var map = player.getMap();
        var reachable = new HashSet<Building>();

        bfsTraverse(
                startPosition,
                p -> {
                    var roads = map.getMapPoint(p).getConnectedRoads();
                    var neighbors = new ArrayList<Point>(roads.size());

                    for (var road : roads) {
                        neighbors.add(road.getOtherPoint(p));
                    }

                    return neighbors;
                },
                point -> {
                    var mapPoint = map.getMapPoint(point);
                    if (mapPoint.isBuilding()) {
                        reachable.add(mapPoint.getBuilding());
                    }
                }
        );

        return reachable;
    }

    /**
     * Calculates the distance in game steps between two points. Game steps means that only
     * movement horizontally and diagonally is allowed, not vertically.
     *
     * @param start The starting point.
     * @param end   The end point.
     * @return The distance in game steps between the two points.
     */
    public static int distanceInGameSteps(Point start, Point end) {
        int distanceX = abs(start.x - end.x);
        int distanceY = abs(start.y - end.y);

        if (distanceX > distanceY) {
            return distanceY + (distanceX - distanceY) / 2;
        } else {
            return distanceY;
        }
    }

    /**
     * Calculates the angle between two directions given x and y components.
     *
     * @param directionX The x-component of the direction.
     * @param directionY The y-component of the direction.
     * @return The angle in radians between the directions.
     */
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

    /**
     * Utility class that holds a road or a house.
     */
    public static class HouseOrRoad {
        public final Building building;
        public final Road road;

        HouseOrRoad(Building building) {
            this.building = building;
            this.road = null;
        }

        HouseOrRoad(Road road) {
            this.road = road;
            this.building = null;
        }

        public boolean isBuilding() {
            return building != null;
        }

        public boolean isRoad() {
            return road != null;
        }

        public boolean isStorehouse() {
            return building != null && building.isStorehouse();
        }
    }
}
