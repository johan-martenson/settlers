/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.round;
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

/**
 *
 * @author johan
 */
public class GameUtils {

    static boolean isUnique(List<Point> wayPoints) {
        List<Point> safeCopy = new ArrayList<>();
        
        safeCopy.addAll(wayPoints);
        
        Collections.sort(safeCopy, new SortPointsByY());

        Point prev = null;
        for (Point iter : safeCopy) {
            if (prev == null) {
                prev = iter;
                
                continue;
            }

            if (prev == iter) {
                return false;
            }
            
            prev = iter;
        }
        
        return true;
    }

    protected interface ConnectionsProvider {
        Iterable<Point> getPossibleConnections(Point start, Point goal);

        public Double distance(Point currentPoint, Point neighbor);
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
    
    static Map<Material, Integer> createEmptyMaterialIntMap() {
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

    private class PointCostPair {
        Point point;
        double cost;

        public PointCostPair(Point point, double cost) {
            this.point = point;
            this.cost  = cost;
        }
    }

    private class CostComparator implements Comparator<PointCostPair> {

        @Override
        public int compare(PointCostPair pcp1, PointCostPair pcp2) {

            if (pcp1 == null && pcp2 == null) {
                return 0;
            } else if (pcp1 == null) {
                return -1;
            } else if (pcp2 == null) {
                return 1;
            } else if (pcp1.cost < pcp2.cost) {
                return 1;
            } else if (pcp1.cost > pcp2.cost){
                return -1;
            } else {
                return 0;
            }
        }
    }

    public static List<Point> findShortestPath(Point start, Point goal, 
            Collection<Point> avoid, ConnectionsProvider pf) {
        Set<Point>          evaluated       = new HashSet<>();
        Set<Point>          toEvaluate      = new HashSet<>();
        Map<Point, Double>  realCostToPoint = new HashMap<>();
        Map<Point, Double>  estFullCost     = new HashMap<>();
        Map<Point, Point>   cameFrom        = new HashMap<>();

        /* Define starting parameters */
        toEvaluate.add(start);
        realCostToPoint.put(start, (double)0);
        estFullCost.put(start, realCostToPoint.get(start) + start.distance(goal));

        /* Declare variables outside of the loop to keep memory churn down */
        Point currentPoint;
        double currentValue;

        double tmpEstCost;

        double tentative_cost;

        while (!toEvaluate.isEmpty()) {
            currentPoint = null;
            currentValue = -1;

            /* Find the point with the lowest estimated full cost */
            for (Point itP : toEvaluate) {

                tmpEstCost = estFullCost.get(itP);

                if (currentPoint == null) {
                    currentPoint = itP;
                    currentValue = tmpEstCost;
                }

                if (currentValue > tmpEstCost) {
                    currentValue = tmpEstCost;
                    currentPoint = itP;
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

            /* Do not re-evalute the same point */
            toEvaluate.remove(currentPoint);
            evaluated.add(currentPoint);

            /* Evaluate each direct neighbor */
            for (Point neighbor : pf.getPossibleConnections(currentPoint, goal)) {

                /* Skip already evaluated points */
                if (evaluated.contains(neighbor)) {
                    continue;
                }

                /* Skip points we should avoid */
                if (avoid != null && avoid.contains(neighbor)) {
                    continue;
                }

                /* Calculate the real cost to reach the neighbor from the start */
                tentative_cost = realCostToPoint.get(currentPoint) + 
                        pf.distance(currentPoint, neighbor);

                /* Check if the neighbor hasn't been evaluated yet or if we 
                   have found a cheaper way to reach it */
                if (!toEvaluate.contains(neighbor) || tentative_cost < realCostToPoint.get(neighbor)) {

                    /* Keep track of how the neighbor was reached */
                    cameFrom.put(neighbor, currentPoint);

                    /* Remember the cost to reach the neighbor */
                    realCostToPoint.put(neighbor, tentative_cost);

                    /* Remember the estimated full cost to go via the neighbor */
                    estFullCost.put(neighbor, realCostToPoint.get(neighbor) + neighbor.distance(goal));

                    /* Add the neighbor to the evaluation list */
                    toEvaluate.add(neighbor);
                }
            }
        }
        
        return null;
    }

    public static List<Point> hullWanderer(Collection<Point> pts) {
        List<Point> points = new ArrayList<>();
        List<Point> hull = new LinkedList<>();
        
        points.addAll(pts);
        
        Collections.sort(points, new SortPointsByY());
        
        Point lowestLeft = points.get(0);
        Point current = lowestLeft;
        Point previous = lowestLeft.down();
        while (true) {
            hull.add(current);
            
            for (Point it : getSurroundingPointsCounterClockwise(current, previous)) {
                if (pts.contains(it)) {
                    previous = current;
                    current = it;
                    break;
                }
            }

            if (current.equals(lowestLeft)) {
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
}
