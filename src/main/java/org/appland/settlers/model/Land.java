package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Land {
    private final Set<Point> points;
    private final List<List<Point>> borders;

    public Land(Collection<Point> pointsInLand, Collection<Point> borderPoints) {
        this.points = new HashSet<>(pointsInLand);
        this.borders = calculateBorders(pointsInLand, new HashSet<>(borderPoints));
    }

    private static List<List<Point>> calculateBorders(Collection<Point> pointsInLand, Set<Point> borderPoints) {
        List<List<Point>> calculatedBorders = new ArrayList<>();

        // Prune outliers
        List<Point> borderPointsToPrune = new LinkedList<>();

        for (var borderPoint : borderPoints) {
            boolean keepPoint = false;

            for (var point : borderPoint.getAdjacentPoints()) {
                if (pointsInLand.contains(point) && !borderPoints.contains(point)) {
                    keepPoint = true;

                    break;
                }
            }

            if (!keepPoint) {
                borderPointsToPrune.add(borderPoint);
            }
        }

        borderPoints.removeAll(borderPointsToPrune);

        /* Separate border points into consistent borders */
        while (!borderPoints.isEmpty()) {
            var root = borderPoints.iterator().next();
            List<Point> collectingBorder = new LinkedList<>();

            collectingBorder.add(root);
            borderPoints.remove(root);

            Point closePoint;
            Point lessClosePoint;

            while (true) {
                closePoint = null;
                lessClosePoint = null;

                for (Point p : borderPoints) {
                    if (p.distance(collectingBorder.getFirst()) < 1.5) {
                        closePoint = p;
                        break;
                    }

                    if (p.distance(collectingBorder.getFirst()) < 2.1) {
                        lessClosePoint = p;
                    }
                }

                if (closePoint != null) {
                    collectingBorder.addFirst(closePoint);

                    borderPoints.remove(closePoint);
                } else if (lessClosePoint != null) {
                    collectingBorder.addFirst(lessClosePoint);

                    borderPoints.remove(lessClosePoint);
                } else {
                    break;
                }
            }

            if (collectingBorder.size() > 2) {
                calculatedBorders.add(collectingBorder);
            }
        }

        return calculatedBorders;
    }

    public Set<Point> ownedLand() {
        return points;
    }

    public List<List<Point>> borders() {
        return borders;
    }

    @Override
    public String toString() {
        return "Border: " + borders + ", containing " + points;
    }
}
