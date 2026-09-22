package org.appland.settlers.computer.util;

import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Building;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.appland.settlers.computer.util.GamePlay.findHeadquarter;
import static org.appland.settlers.model.GameUtils.PointAndCost;

public class PathFinding {
    public static boolean canConnectPointToHeadquarters(Point start, Player player, boolean debug) throws InvalidUserActionException {
        var map = player.getMap();
        var headquarter = findHeadquarter(player);

        return canConnectTo(start, player, map, headquarter.getFlag().getPosition(), null);
    }

    /**
     * Checks if it's possible to place a new road from the given starting point to a flag that's in turn connected to the headquarters.
     *
     * @param start
     * @param player
     * @param map
     * @param to
     * @return
     * @throws InvalidUserActionException
     */
    public static boolean canConnectTo(
            Point start,
            Player player,
            GameMap map,
            Point to,
            Collection<Point> avoid
    ) throws InvalidUserActionException {
        var visited = new HashSet<Point>();
        var queue = new ArrayDeque<Point>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            var point = queue.removeFirst();

            // TODO: it's also possible to connect to a point that is on a road and where placing a flag is possible

            if (map.isFlagAtPoint(point) && map.arePointsConnectedByRoads(point, to)) {
                return true;
            }

            for (var next : map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player, point)) {
                if (avoid != null && avoid.contains(next)) {
                    continue;
                }

                if (visited.add(next)) {
                    queue.addLast(next);
                }
            }
        }

        return false;
    }

    public record DistanceAndPrevious<T>(Map<Point, T> distance, Map<Point, Point> previous) {
    }

    public static DistanceAndPrevious<Double> dijkstraSearch(Point start, Function<Point, Collection<PointAndCost>> getNeighbors) {
        var distance = new HashMap<Point, Double>();
        var previous = new HashMap<Point, Point>();
        var priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(PointAndCost::cost));

        // Start search at:
        distance.put(start, 0.0);
        priorityQueue.add(new PointAndCost(start, 0));

        // Search through all points
        while (!priorityQueue.isEmpty()) {
            var current = priorityQueue.poll();

            if (current.cost > distance.get(current.point)) {
                continue;
            }

            for (var neighborWithCost : getNeighbors.apply(current.point)) {
                var newDistance = current.cost + neighborWithCost.cost;

                if (newDistance < distance.getOrDefault(neighborWithCost.point, Double.MAX_VALUE)) {
                    distance.put(neighborWithCost.point, newDistance);
                    previous.put(neighborWithCost.point, current.point);

                    priorityQueue.add(new PointAndCost(neighborWithCost.point, newDistance));

                    // TODO: remove any stored versions of the neighbor with a higher cost
                }
            }
        }

        return new DistanceAndPrevious<>(distance, previous);
    }

    public static List<Point> findWayToConnectToBuilding(Point from, Building building, Player player, double roadPreference) {
        return findWayToConnectToBuilding(from, building.getPosition(), player, roadPreference);
    }

    public static List<Point> findWayToConnectToBuilding(Point from, Point to, Player player, double roadPreference) {
        var map = player.getMap();

        var distanceAndPrevious = dijkstraSearch(
                from,
                point -> {
                    var neighbors = new ArrayList<PointAndCost>();

                    if (map.isFlagAtPoint(point)) {

                        // Add connections through the road. Both at the end flags and where a new flag can be placed.
                        // Also handle the case where the road is a driveway (length is 2) and the other end is the
                        // target building.
                        neighbors.addAll(
                                map.getRoadsFromFlag(point).stream()
                                        .filter(road ->
                                                road.getLength() > 2
                                                        || Objects.equals(
                                                        road.getOtherPoint(point),
                                                        to))
                                        .flatMap(road -> {
                                            // Special case: allow traversing a short road directly to the
                                            // flag adjacent to the destination building.
                                            if (road.getLength() <= 2) {
                                                return Stream.of(new PointAndCost(
                                                        road.getOtherPoint(point),
                                                        road.getLength() * roadPreference));
                                            }

                                            var points = road.getWayPoints().getFirst().equals(point)
                                                    ? road.getWayPoints()
                                                    : road.getWayPoints().reversed();

                                            return IntStream.range(2, points.size())
                                                    .filter(i -> {
                                                        var roadPoint = points.get(i);
                                                        return map.isFlagAtPoint(roadPoint)
                                                                || map.isAvailableFlagPoint(player, roadPoint);
                                                    })
                                                    .mapToObj(i -> new PointAndCost(points.get(i), i * roadPreference));
                                        })
                                        .toList()
                        );

                        // Add connections if a new road would be placed
                        try {
                            neighbors.addAll(
                                    map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player, point).stream()
                                            .map(p -> new PointAndCost(p, 1))
                                            .toList()
                            );
                        } catch (InvalidUserActionException e) {
                            System.out.println(e);
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    } else if (map.isRoadAtPoint(point) && map.isAvailableFlagPoint(player, point)) {

                        // Add connections through the road, both to existing flags and to places where a new flag can be placed.
                        var road = map.getRoadAtPoint(point);
                        var roadPoints = road.getWayPoints();
                        var index = roadPoints.indexOf(point);

                        neighbors.addAll(
                                Stream.concat(
                                                // Walk toward the start of the road, skipping the adjacent point.
                                                IntStream.range(2, index + 1)
                                                        .mapToObj(distance -> new PointAndCost(
                                                                roadPoints.get(index - distance),
                                                                distance)),

                                                // Walk toward the end of the road, skipping the adjacent point.
                                                IntStream.range(2, roadPoints.size() - index)
                                                        .mapToObj(distance -> new PointAndCost(
                                                                roadPoints.get(index + distance),
                                                                distance))
                                        )
                                        .filter(pc ->
                                                map.isFlagAtPoint(pc.point())
                                                        || map.isAvailableFlagPoint(player, pc.point()))
                                        .toList()
                        );

                        // Add connections if a new road is placed
                        try {
                            neighbors.addAll(
                                    map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player, point).stream()
                                            .map(p -> new PointAndCost(p, 1))
                                            .toList()
                            );
                        } catch (InvalidUserActionException e) {
                            System.out.println(e);
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    } else {
                        // Not at a flag and not on a road...

                        // ...add connections if a new road would be placed
                        try {
                            neighbors.addAll(
                                    map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player, point).stream()
                                            .map(p -> new PointAndCost(p, 1))
                                            .toList()
                            );
                        } catch (InvalidUserActionException e) {
                            System.out.println(e);
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }

                    return neighbors;
                }
        );

        return reconstructPath(distanceAndPrevious, to, from);
    }

    public static List<Point> findWayToConnectToBuilding(Flag from, Building to) throws InvalidUserActionException {
        System.out.println("Finding way to building at: " + from.getPosition() + " to " + to.getPosition());

        return findWayToConnectToBuilding(from.getPosition(), to.getPosition(), from.getPlayer(), 1.0);
    }

    public static <T> List<Point> reconstructPath(DistanceAndPrevious<T> distanceAndPrevious, Point to, Point start) {

        // No path exists.
        if (!to.equals(start) && !distanceAndPrevious.previous().containsKey(to)) {
            return List.of();
        }

        var path = new ArrayList<Point>();
        Point current = to;

        while (current != null) {
            path.add(current);

            if (current.equals(start)) {
                break;
            }

            current = distanceAndPrevious.previous().get(current);
        }

        Collections.reverse(path);
        return path;
    }
}
