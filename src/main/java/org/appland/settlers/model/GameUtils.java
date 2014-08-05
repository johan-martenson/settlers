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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author johan
 */
public class GameUtils {

    public interface ConnectionsProvider {
        Iterable<Point> getPossibleConnections(Point start, Point goal);
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

}
