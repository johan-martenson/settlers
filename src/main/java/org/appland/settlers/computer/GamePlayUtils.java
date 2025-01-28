/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.computer;

import org.appland.settlers.model.EndPoint;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Headquarter;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for various game functions like managing roads, buildings, and distances between points.
 */
public class GamePlayUtils {
    private static final int THRESHOLD_CLOSE_TO_ENEMY = 10;
    private static final int THRESHOLD_VERY_CLOSE_TO_ENEMY = 5;

    /**
     * Finds the first available spot for a building for a player on the map.
     *
     * @param map The game map.
     * @param player The player to find a spot for.
     * @return The first available Point for a building.
     */
    public static Point findAvailableSpotForBuilding(GameMap map, Player player) {
        return map.getAvailableHousePoints(player).keySet().iterator().next();
    }

    /**
     * Finds available house points within a specified radius.
     *
     * @param map The game map.
     * @param player The player.
     * @param position The position to search from.
     * @param size The size of the building.
     * @param radius The radius to search within.
     * @return A list of available points for a house.
     */
    public static List<Point> findAvailableHousePointsWithinRadius(GameMap map, Player player, Point position, Size size, int radius) {
        return map.getPointsWithinRadius(position, radius).stream()
                .filter(point -> {
                    Size availableSize = map.isAvailableHousePoint(player, point);
                    return availableSize != null && availableSize.contains(size);
                })
                .collect(Collectors.toList());
    }

    /**
     * Finds the headquarters of a player.
     *
     * @param player The player to search for.
     * @return The headquarters building of the player, or null if not found.
     */
    public static Headquarter findHeadquarter(Player player) {
        return player.getBuildings().stream()
                .filter(Headquarter.class::isInstance)
                .map(Headquarter.class::cast)
                .findFirst()
                .orElse(null);
    }

    /**
     * Fills a road with flags where available.
     *
     * @param map The game map.
     * @param road The road to fill with flags.
     * @throws Exception If an error occurs while placing flags.
     */
    public static void fillRoadWithFlags(GameMap map, Road road) throws Exception {
        if (road != null) {
            Player player = road.getPlayer();

            for (Point point : road.getWayPoints()) {
                if (map.isAvailableFlagPoint(player, point)) {
                    map.placeFlag(player, point);
                }
            }
        }
    }

    /**
     * Removes a road without affecting other roads and buildings.
     *
     * @param map The game map.
     * @param flag The flag to start from.
     * @throws Exception If an error occurs during road removal.
     */
    public static void removeRoadWithoutAffectingOthers(GameMap map, Flag flag) throws Exception {
        while (map.getRoadsFromFlag(flag).size() == 1) {
            Road road = map.getRoadsFromFlag(flag).iterator().next();
            EndPoint otherSide = road.getOtherEndPoint(flag);

            if (map.isBuildingAtPoint(otherSide.getPosition())) {
                break;
            }

            Flag otherFlag = map.getFlagAtPoint(otherSide.getPosition());
            map.removeFlag(flag);
            flag = otherFlag;
        }
    }

    /**
     * Gets the distance from a position to the player's own border.
     *
     * @param position The position to measure from.
     * @param player0 The player whose border is considered.
     * @return The shortest distance to the border.
     */
    public static double getDistanceToOwnBorder(Point position, Player player0) {
        return player0.getBorderPoints().stream()
                .mapToDouble(position::distance)
                .min()
                .orElse(Double.MAX_VALUE);
    }

    /**
     * Connects a point to a building by placing a road between them.
     *
     * @param player The player placing the road.
     * @param map The game map.
     * @param start The starting point.
     * @param building The building to connect to.
     * @return The placed road, or null if no road could be placed.
     * @throws Exception If an error occurs during road placement.
     */
    public static Road connectPointToBuilding(Player player, GameMap map, Point start, Building building) throws Exception {
        Point via = pointToConnectViaToGetToBuilding(player, map, start, building);

        return via != null ? map.placeAutoSelectedRoad(player, start, via) : null;
    }

    /**
     * Finds the best point to connect to a building from a given point.
     *
     * @param player The player.
     * @param map The game map.
     * @param start The starting point.
     * @param building The building to connect to.
     * @return The best point to connect to or null if no connection is possible.
     */
    public static Point pointToConnectViaToGetToBuilding(Player player, GameMap map, Point start, Building building) {
        Point end = building.getFlag().getPosition();

        if (map.arePointsConnectedByRoads(start, end)) {
            return null;
        }

        var viaPointAndDistance = map.getPointsWithinRadius(start, 15).stream()
                .filter(point -> !point.equals(end))
                .filter(map::isFlagAtPoint)
                .filter(point -> map.arePointsConnectedByRoads(point, end))
                .map(point -> new GameUtils.Tuple<>(point, map.findAutoSelectedRoad(player, start, point, null)))
                .filter(pointAndList -> pointAndList.t2() != null)
                .min(Comparator.comparingInt(pointAndList -> pointAndList.t2().size()))
                .orElse(null);

        var newRoadToEnd = map.findAutoSelectedRoad(player, start, end, null);

        if (viaPointAndDistance == null && newRoadToEnd == null) {
            return null;
        } else if (viaPointAndDistance != null && newRoadToEnd == null) {
            return viaPointAndDistance.t1();
        } else if (viaPointAndDistance == null && newRoadToEnd != null) {
            return end;
        } else if (viaPointAndDistance.t2().size() > 3) {
            return end;
        }

        return null;
    }

    /**
     * Finds a connection point or an existing road between two points.
     *
     * @param player The player.
     * @param map The game map.
     * @param start The starting point.
     * @param end The destination point.
     * @return The point to connect to, or the end point if no intermediate point is found.
     */
    public static Point findConnectionToDestinationOrExistingRoad(Player player, GameMap map, Point start, Point end) {
        return map.getPointsWithinRadius(start, 15).stream()
                .filter(point -> !point.equals(end) && map.isFlagAtPoint(point) && map.arePointsConnectedByRoads(point, end))
                .min(Comparator.comparingDouble(point -> map.findAutoSelectedRoad(player, start, point, null).size()))
                .orElse(end);
    }

    /**
     * Finds visible opponent buildings on the map.
     *
     * @param map The game map.
     * @param player The player looking for opponents.
     * @return A list of visible opponent buildings.
     */
    public static List<Building> findVisibleOpponentBuildings(GameMap map, Player player) {
        Set<Point> visibleLand = new HashSet<>(player.getDiscoveredLand());
        visibleLand.removeAll(player.getLandInPoints());

        return map.getBuildings().stream()
                .filter(b -> visibleLand.contains(b.getPosition()))
                .toList();
    }

    /**
     * Repairs the connection between two flags by placing a road between them.
     *
     * @param map The game map.
     * @param player The player placing the road.
     * @param from The starting flag.
     * @param to The destination flag.
     * @throws Exception If an error occurs during road placement.
     */
    public static void repairConnection(GameMap map, Player player, Flag from, Flag to) throws Exception {
        Set<Flag> fromFlags = findConnectedFlags(map, from);
        Set<Flag> toFlags = findConnectedFlags(map, to);

        int distance = Integer.MAX_VALUE;
        Flag flag1 = null;
        Flag flag2 = null;

        for (Flag fromFlag : fromFlags) {
            for (Flag toFlag : toFlags) {
                int totalDistance = 0;

                if (!fromFlag.equals(from)) {
                    List<Point> path1 = map.findWayWithExistingRoads(from.getPosition(), fromFlag.getPosition());
                    if (path1 != null) {
                        totalDistance += path1.size();
                    }
                }

                if (!toFlag.equals(to)) {
                    List<Point> path2 = map.findWayWithExistingRoads(to.getPosition(), toFlag.getPosition());
                    if (path2 != null) {
                        totalDistance += path2.size();
                    }
                }

                List<Point> path = map.findAutoSelectedRoad(player, fromFlag.getPosition(), toFlag.getPosition(), null);
                if (path != null && totalDistance + path.size() < distance) {
                    distance = totalDistance + path.size();
                    flag1 = fromFlag;
                    flag2 = toFlag;
                }
            }
        }

        if (flag1 != null && flag2 != null) {
            Road road = map.placeAutoSelectedRoad(player, flag1, flag2);
            fillRoadWithFlags(map, road);
        }
    }

    public static Set<Flag> findConnectedFlags(GameMap map, Flag from) {
        Set<Flag> fromFlags = new HashSet<>();
        LinkedList<Flag> flagsToSearch = new LinkedList<>();
        Set<Road> searchedRoads = new HashSet<>();
        flagsToSearch.add(from);

        while (!flagsToSearch.isEmpty()) {
            Flag flag = flagsToSearch.removeFirst();
            fromFlags.add(flag);

            for (Road road : map.getRoadsFromFlag(flag)) {
                if (searchedRoads.contains(road)) {
                    continue;
                }

                searchedRoads.add(road);
                EndPoint ep = road.getOtherEndPoint(flag);

                if (!map.isBuildingAtPoint(ep.getPosition())) {
                    flagsToSearch.add(map.getFlagAtPoint(ep.getPosition()));
                }
            }
        }

        return fromFlags;
    }

    /**
     * Checks if all buildings in the list are ready.
     *
     * @param buildings The list of buildings.
     * @param <T> The building type.
     * @return True if all buildings are ready, false otherwise.
     */
    public static <T extends Building> boolean buildingsAreReady(List<T> buildings) {
        return buildings.stream().allMatch(Building::isReady);
    }

    /**
     * Checks if a specific type of building exists in the list.
     *
     * @param buildings The list of buildings.
     * @param class1 The class type of the building.
     * @param <T> The building type.
     * @return True if the building type exists, false otherwise.
     */
    public static <T> boolean buildingTypeExists(List<Building> buildings, Class<T> class1) {
        return buildings.stream().anyMatch(b -> b.getClass().equals(class1));
    }

    /**
     * Gets a list of buildings of a specific type.
     *
     * @param buildings The list of buildings.
     * @param class1 The class type of the buildings.
     * @param <T> The building type.
     * @return A list of buildings of the specified type.
     */
    public static <T> List<T> getBuildingsOfType(List<Building> buildings, Class<T> class1) {
        return buildings.stream()
                .filter(b -> b.getClass().equals(class1))
                .map(class1::cast)
                .toList();
    }

    /**
     * Finds a point for building close to a specific point.
     *
     * @param point The point to search near.
     * @param neededSize The size of the building.
     * @param controlledPlayer The player.
     * @param map The game map.
     * @return The best point for building, or null if none found.
     */
    public static Point findPointForBuildingCloseToPoint(Point point, Size neededSize, Player controlledPlayer, GameMap map) {
        return controlledPlayer.getLandInPoints().stream()
                .filter(p -> {
                    Size availableSize = map.isAvailableHousePoint(controlledPlayer, p);
                    return availableSize != null && availableSize.contains(neededSize);
                })
                .min(Comparator.comparingDouble(p -> p.distance(point)))
                .orElse(null);
    }

    /**
     * Checks if there is at least one ready building in the list.
     *
     * @param wells The list of buildings.
     * @param <T> The building type.
     * @return True if there is at least one ready building, false otherwise.
     */
    public static <T extends Building> boolean listContainsAtLeastOneReadyBuilding(List<T> wells) {
        return wells.stream().anyMatch(Building::isReady);
    }

    /**
     * Checks if the player has stone within their area.
     *
     * @param map The game map.
     * @param player The player.
     * @return True if there is stone within the player's area, false otherwise.
     */
    public static boolean hasStoneWithinArea(GameMap map, Player player) {
        return player.getLandInPoints().stream().anyMatch(map::isStoneAtPoint);
    }

    /**
     * Checks if a building is done (ready).
     *
     * @param building The building to check.
     * @return True if the building is ready, false otherwise.
     */
    public static boolean buildingDone(Building building) {
        return building != null && building.isReady();
    }

    /**
     * Checks if a building is in place on the map.
     *
     * @param building The building to check.
     * @return True if the building is in place, false otherwise.
     */
    public static boolean buildingInPlace(Building building) {
        return building != null && Objects.equals(building.getMap().getBuildingAtPoint(building.getPosition()), building);
    }

    /**
     * Places a building close to another building and connects it to the headquarters.
     *
     * @param player The player placing the building.
     * @param buildingCloseBy The nearby building.
     * @param building The building to place.
     * @param <T> The building type.
     * @return The placed building, or null if no placement is possible.
     * @throws Exception If an error occurs during building placement.
     */
    public static <T extends Building> T placeBuilding(Player player, Building buildingCloseBy, T building) throws Exception {
        GameMap map = player.getMap();
        Size size = building.getSize();

        Point location = findPointForBuildingCloseToPoint(buildingCloseBy.getPosition(), size, player, map);
        if (location == null) {
            return null;
        }

        map.placeBuilding(building, location);
        Road road = connectPointToBuilding(player, map, building.getFlag().getPosition(), buildingCloseBy);
        fillRoadWithFlags(map, road);

        return building;
    }

    /**
     * Finds the distance to known enemies within a range of a building.
     *
     * @param building The building.
     * @param range The range to check.
     * @return The distance to known enemies, or Integer.MAX_VALUE if no enemies are nearby.
     */
    public static int distanceToKnownEnemiesWithinRange(Building building, int range) {
        return distanceToKnownEnemiesWithinRange(building.getMap(), building.getPlayer(), building.getPosition(), range);
    }

    /**
     * Finds the distance to known enemies within a range of a point.
     *
     * @param map The game map.
     * @param player The player.
     * @param point The point to check.
     * @param range The range to check.
     * @return The distance to known enemies, or Integer.MAX_VALUE if no enemies are nearby.
     */
    public static int distanceToKnownEnemiesWithinRange(GameMap map, Player player, Point point, int range) {
        return map.getPointsWithinRadius(point, range).stream()
                .filter(p -> player.getDiscoveredLand().contains(p) && !player.getLandInPoints().contains(p))
                .filter(p -> map.getPlayers().stream()
                        .anyMatch(otherPlayer -> !player.equals(otherPlayer) && otherPlayer.getLandInPoints().contains(p)))
                .mapToInt(p -> (int) point.distance(p))
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    /**
     * Finds the closest enemy military building to a player.
     *
     * @param player The player.
     * @return The closest enemy military building, or null if none are close.
     */
    public static Building getCloseEnemyBuilding(Player player) {
        GameMap map = player.getMap();
        double distanceToBorder = Double.MAX_VALUE;
        Building closeEnemyBuilding = null;

        for (Point p : player.getDiscoveredLand()) {
            Player owner = player.getPlayerAtPoint(p);

            if (owner == null || owner.equals(player) || !map.isBuildingAtPoint(p)) {
                continue;
            }

            Building tmpBuilding = map.getBuildingAtPoint(p);

            if (!tmpBuilding.isMilitaryBuilding()) {
                continue;
            }

            double tmpDistanceToBorder = getDistanceToOwnBorder(p, player);

            if (tmpDistanceToBorder < distanceToBorder) {
                closeEnemyBuilding = tmpBuilding;
                distanceToBorder = tmpDistanceToBorder;
            }

            if (tmpDistanceToBorder < THRESHOLD_VERY_CLOSE_TO_ENEMY) {
                return tmpBuilding;
            }
        }

        return distanceToBorder < THRESHOLD_CLOSE_TO_ENEMY ? closeEnemyBuilding : null;
    }

    /**
     * Gets all military buildings of a player.
     *
     * @param player The player.
     * @return A set of military buildings.
     */
    public static Set<Building> getMilitaryBuildingsForPlayer(Player player) {
        return player.getBuildings().stream()
                .filter(building -> building.isMilitaryBuilding() && !building.isBurningDown() && !building.isDestroyed())
                .collect(Collectors.toSet());
    }

    /**
     * Gets all discovered enemy military buildings for a player.
     *
     * @param player The player.
     * @return A set of discovered enemy military buildings.
     */
    public static Set<Building> getDiscoveredEnemyMilitaryBuildingsForPlayer(Player player) {
        return player.getMap().getPlayers().stream()
                .filter(enemyPlayer -> !player.equals(enemyPlayer))
                .flatMap(enemyPlayer -> getMilitaryBuildingsForPlayer(enemyPlayer).stream())
                .filter(enemyBuilding -> player.getDiscoveredLand().contains(enemyBuilding.getPosition()))
                .collect(Collectors.toSet());
    }
}
