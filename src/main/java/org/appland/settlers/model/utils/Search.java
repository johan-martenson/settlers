package org.appland.settlers.model.utils;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Point;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class Search {
    public record Edge(Point point, int cost) {}

    public static Iterable<Point> roadNeighbors(GameMap map, Point point) {
        var roads = map.getMapPoint(point).getConnectedRoads();
        var neighbors = new ArrayList<Point>(roads.size());

        for (var road : roads) {
            neighbors.add(road.getOtherPoint(point));
        }

        return neighbors;
    }

    public static Iterable<Edge> offroadEdges(GameMap map, Point point) {
        var neighbors = map.getPossibleAdjacentOffRoadConnections(point);
        var edges = new ArrayList<Edge>(neighbors.size());

        for (var neighbor : neighbors) {
            edges.add(new Edge(neighbor, 1)); // uniform cost
        }

        return edges;
    }

    /**
     * Utility function - returns a list of road ends that can be reached directly from the current point by following
     * each road from start to end.
     * @param map
     * @param point
     * @return
     */
    public static Collection<Edge> roadEdges(GameMap map, Point point) {
        return roadEdges(map, point, null);
    }

    /**
     * Utility function - returns a list of road ends that can be reached directly from the current point by following
     * each road from start to end.
     * @param map
     * @param point
     * @param avoidSet
     * @return
     */
    public static Collection<Edge> roadEdges(GameMap map, Point point, Set<Point> avoidSet) {
        var roads = map.getMapPoint(point).getConnectedRoads();
        var edges = new ArrayList<Edge>(roads.size());

        for (var road : roads) {
            var otherPoint = road.getOtherPoint(point);

            if (avoidSet == null || !avoidSet.contains(otherPoint)) {
                edges.add(new Edge(road.getOtherPoint(point), road.getWayPoints().size() - 1));
            }
        }

        return edges;
    }

    /**
     * Shortest path to known goal
     * @param start
     * @param isGoal
     * @param neighbors
     * @param heuristic
     * @return
     */
    public static List<Point> aStar(
            Point start,
            Function<Point, Boolean> isGoal,
            Function<Point, Iterable<Edge>> neighbors,
            Function<Point, Integer> heuristic
    ) {
        var queue = new PriorityQueue<GameUtils.PointAndCost>();
        var cost = new HashMap<Point, Integer>();
        var cameFrom = new HashMap<Point, Point>();

        queue.add(new GameUtils.PointAndCost(start, heuristic.apply(start)));
        cost.put(start, 0);

        while (!queue.isEmpty()) {
            var current = queue.poll();
            var currentPoint = current.point;

            if (isGoal.apply(currentPoint)) {
                var path = new LinkedList<Point>();
                var step = currentPoint;

                while (step != null) {
                    path.addFirst(step);
                    step = cameFrom.get(step);
                }

                return path;
            }

            for (var edge : neighbors.apply(currentPoint)) {
                var neighbor = edge.point();
                int newCost = cost.get(currentPoint) + edge.cost();

                if (newCost < cost.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cost.put(neighbor, newCost);
                    cameFrom.put(neighbor, currentPoint);

                    queue.add(new GameUtils.PointAndCost(neighbor, newCost + heuristic.apply(neighbor)));
                }
            }
        }

        return null;
    }

    /**
     * Uniform cost, nearest-any-match
     * @param start
     * @param neighbors
     * @param onVisit
     */
    public static void bfs(
            Point start,
            Function<Point, Iterable<Point>> neighbors,
            Consumer<Point> onVisit
    ) {
        var queue = new ArrayDeque<Point>();
        var visited = new HashSet<Point>();

        queue.add(start);

        while (!queue.isEmpty()) {
            var point = queue.removeFirst();

            if (!visited.add(point)) {
                continue;
            }

            onVisit.accept(point);

            neighbors.apply(point).forEach(queue::add);
        }
    }

    /**
     * Weighted nearest-any-match (bfs). Returns the first reached match.
     * @param start
     * @param tryMatch
     * @param neighbors
     * @return
     * @param <T>
     */
    public static <T> T dijkstraClosest(
            Point start,
            Function<Point, T> tryMatch,
            Function<Point, Iterable<Edge>> neighbors
    ) {
        var queue = new PriorityQueue<GameUtils.PointAndCost>();
        var cost = new HashMap<Point, Integer>();
        var visited = new HashSet<Point>();

        queue.add(new GameUtils.PointAndCost(start, 0));
        cost.put(start, 0);

        while (!queue.isEmpty()) {
            var current = queue.poll();
            var currentPoint = current.point;

            if (!visited.add(currentPoint)) {
                continue;
            }

            // Try matching at this point
            var result = tryMatch.apply(currentPoint);
            if (result != null) {
                return result;
            }

            for (var edge : neighbors.apply(currentPoint)) {
                var neighbor = edge.point();
                var newCost = cost.get(currentPoint) + edge.cost();

                if (newCost < cost.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cost.put(neighbor, newCost);
                    queue.add(new GameUtils.PointAndCost(neighbor, newCost));
                }
            }
        }

        return null;
    }

    /**
     * Breadth-first search. Visit all points, no specific goal.
     * @param start
     * @param neighbors
     * @param visitor
     */
    public static void bfsTraverse(
            Point start,
            Function<Point, Iterable<Point>> neighbors,
            Consumer<Point> visitor
    ) {
        var queue = new ArrayDeque<Point>();
        var visited = new HashSet<Point>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            var current = queue.removeFirst();

            visitor.accept(current);

            for (var neighbor : neighbors.apply(current)) {
                if (visited.add(neighbor)) {
                    queue.addLast(neighbor);
                }
            }
        }
    }

    /**
     * Find if two points are connected, using a breadth-first search. This avoid the overhead of A* for simple searches.
     * @param start
     * @param isGoal
     * @param neighbors
     * @return
     */
    public static boolean bfsConnected(
            Point start,
            Function<Point, Boolean> isGoal,
            Function<Point, Iterable<Point>> neighbors
    ) {
        var queue = new ArrayDeque<Point>();
        var visited = new HashSet<Point>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            var current = queue.removeFirst();

            if (isGoal.apply(current)) {
                return true;
            }

            for (var neighbor : neighbors.apply(current)) {
                if (visited.add(neighbor)) {
                    queue.addLast(neighbor);
                }
            }
        }

        return false;
    }
}
