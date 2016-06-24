/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author johan
 */
public class Land {

    private Set<Point>        points;
    private List<List<Point>> border;

    Land(Collection<Point> pointsInLand, Collection<Point> borderPoints) {
        points = new HashSet<>();
        border = new ArrayList<>();

        points.addAll(pointsInLand);

        List<Point> borderPointsToPrune = new LinkedList<>();

        /* Prune outliers */
        for (Point borderPoint : borderPoints) {
            boolean keepPoint = false;

            for (Point p : borderPoint.getAdjacentPoints()) {
                if (pointsInLand.contains(p) && !borderPoints.contains(p)) {
                    keepPoint = true;
                }
            }

            if (!keepPoint) {
                borderPointsToPrune.add(borderPoint);
            }
        }

        borderPoints.removeAll(borderPointsToPrune);

        /* Separate border points into consistent borders */
        while (!borderPoints.isEmpty()) {
            Point root = borderPoints.iterator().next();
            List<Point> collectingBorder = new LinkedList<>();

            collectingBorder.add(root);
            borderPoints.remove(root);

            Point closePoint;
            Point lessClosePoint;

            List<Point> toRemoveFromBorder = new LinkedList<>();

            while (true) {
                closePoint = null;
                lessClosePoint = null;

                toRemoveFromBorder.clear();

                for (Point p : borderPoints) {
                    if (p.distance(collectingBorder.get(0)) < 1.5) {
                        closePoint = p;
                        break;
                    }

                    if (p.distance(collectingBorder.get(0)) < 2.1) {
                        lessClosePoint = p;
                    }
                }

                if (closePoint != null) {
                    collectingBorder.add(0, closePoint);

                    borderPoints.remove(closePoint);
                } else if (lessClosePoint != null) {
                    collectingBorder.add(0, lessClosePoint);

                    borderPoints.remove(lessClosePoint);
                } else {
                    break;
                }
            }

            if (collectingBorder.size() > 2) {
                border.add(collectingBorder);
            }
        }
    
    }

    List<List<Point>> getBorders() {
        return Collections.unmodifiableList(border);
    }

    public Set<Point> getPointsInLand() {
        return Collections.unmodifiableSet(points);
    }

    public boolean isWithinBorder(Point position) {
        return points.contains(position);
    }

    @Override
    public String toString() {
        return "Border: " + border + ", containing" + points;
    }
}
