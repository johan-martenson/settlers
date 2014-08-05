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
import java.util.Stack;

/**
 *
 * @author johan
 */
public class GameUtils {

    public interface ConnectionsProvider {
        Iterable<Point> getPossibleConnections(Point start, Point goal);
    }

    static class SortPointsByPolarOrder implements Comparator<Point> {
        private final Point center;

        public SortPointsByPolarOrder(Point p) {
            center = p;
        }
        
        @Override
        public int compare(Point t, Point t1) {
            double a = getAngle(t);
            double a1 = getAngle(t1);
            
            if (a > a1) {
                return 1;
            } else if (a < a1) {
                return -1;
            } else {
                return 0;
            }
        }
        
        private double getAngle(Point p) {
            double dy = p.y - center.y;
            double dx = p.x - center.x;
            double hy = Math.sqrt(dy*dy + dx*dx);
            
            double angle = dy / hy;
            
            if (angle < 0) {
                angle = Math.PI - angle;
            }

            return angle;
        }
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
            
            if (t.x < t1.x) {
                return -1;
            }
            
            if (t.x > t1.x) {
                return 1;
            }
            
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }

            if (! (o instanceof Point)) {
                return false;
            }

            return equals(o);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            return hash;
        }
    }
    
    static Map createEmptyMaterialIntMap() {
        Map<Material, Integer> result = new HashMap<>();
        for (Material m : Material.values()) {
            result.put(m, 0);
        }
        return result;
    }

    static boolean isQueueEmpty(Map<Material, Integer> queue) {
        boolean isEmpty = true;

        for (Integer amount : queue.values()) {
            if (amount != 0) {
                isEmpty = false;
            }
        }

        return isEmpty;
    }

    public static List<Point> findShortestPath(Point start, Point goal, Collection<Point> avoid, ConnectionsProvider pf) {
        Set<Point> evaluated         = new HashSet<>();
        Set<Point> toEvaluate        = new HashSet<>();
        Map<Point, Double>  cost     = new HashMap<>();
        Map<Point, Double>  fullCost = new HashMap<>();
        Map<Point, Point>   cameFrom = new HashMap<>();
        
        if (avoid != null) {        
            evaluated.addAll(avoid);
        }
        
        toEvaluate.add(start);
        cost.put(start, (double)0);
        fullCost.put(start, cost.get(start) + start.distance(goal));
        
        while (!toEvaluate.isEmpty()) {
            Point currentPoint = null;
            double currentValue = -1;
            
            for (Map.Entry<Point, Double> pair : fullCost.entrySet()) {
                
                if (!toEvaluate.contains(pair.getKey())) {
                    continue;
                }
                
                if (currentPoint == null) {
                    currentPoint = pair.getKey();
                    currentValue = pair.getValue();
                }

                if (currentValue > pair.getValue()) {
                    currentValue = pair.getValue();
                    currentPoint = pair.getKey();
                }
            }

            if (currentPoint.equals(goal)) {
                List<Point> path = new ArrayList<>();
                
                while (currentPoint != start) {
                    path.add(0, currentPoint);
                    
                    currentPoint = cameFrom.get(currentPoint);
                }
                
                path.add(0, start);

                return path;
            }
            
            toEvaluate.remove(currentPoint);
            evaluated.add(currentPoint);
            
            for (Point neighbor : pf.getPossibleConnections(currentPoint, goal)) {
                if (evaluated.contains(neighbor)) {
                    continue;
                }
            
                double tentative_cost = cost.get(currentPoint) + currentPoint.distance(neighbor);

                if (!toEvaluate.contains(neighbor) || tentative_cost < cost.get(neighbor)) {
                    cameFrom.put(neighbor, currentPoint);
                    cost.put(neighbor, tentative_cost);
                    fullCost.put(neighbor, cost.get(neighbor) + neighbor.distance(goal));
                    
                    toEvaluate.add(neighbor);
                }
            }
        }
        
        return null;
    }

    public static Collection<Point> findHullSimple(Collection<Point> pts) {
        List<Point> points = new ArrayList<>();
        List<Point> hull = new LinkedList<>();
        
        points.addAll(pts);
        
        Collections.sort(points, new SortPointsByY());
        
        Point lowestLeft = points.get(0);
        
        /* Follow lowest row */
        for (Point next : points) {
            if (next.y != lowestLeft.y) {
                break;
            }
        
            hull.add(next);
        }

        /* Walk the right side upwards */
        int startIndex = points.indexOf(hull.get(hull.size() - 1));
        Point previous = null;
        for (Point p : points.subList(startIndex + 1, points.size())) {
            if (previous == null) {
                previous = p;
                
                continue;
            }

            if (p.y != previous.y) {
                hull.add(previous);
            }
        
            previous = p;
        }
        
        hull.add(points.get(points.size() - 1));

        /* Walk the top row */
        Collections.reverse(points);
        Point highestPoint = points.get(0);
        
        
        for (Point next : points.subList(1, points.size())) {
            if (next.y != highestPoint.y) {
                break;
            }

            hull.add(next);
        }

        /* Walk the left side downwards */
        startIndex = points.indexOf(hull.get(hull.size() - 1));
        previous = null;
        for (Point p : points.subList(startIndex + 1, points.size())) {
            if (previous == null) {
                previous = p;
                
                continue;
            }
            
            if (p.y != previous.y) {
                hull.add(previous);
            }
        
            previous = p;
        }

        return hull;
    }
}
