/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author johan
 */
class Land {

    List<Point> points;
    List<Point> border;

    Land(Collection<Point> pointsInLand) {
        points = new ArrayList<>();

        points.addAll(pointsInLand);

        border = calculateBorder(points);
    }

    private List<Point> calculateBorder(Collection<Point> occupiedPoints) {
        return GameUtils.hullWanderer(occupiedPoints);
    }

    List<Point> getBorder() {
        return border;
    }

    List<Point> getPointsInLand() {
        return points;
    }

    boolean isWithinBorder(Point position) {
        return points.contains(position);
    }

    static List<Land> calculateLandWithinBorders(Collection<Building> militaryBuildings) {
        List<Land> result = new LinkedList<>();
        List<Building> readyMilitaryBuildings = new LinkedList<>();

        for (Building b : militaryBuildings) {
            if (b.ready()) {
                readyMilitaryBuildings.add(b);
            }
        }

        while (!readyMilitaryBuildings.isEmpty()) {
            Building root = readyMilitaryBuildings.get(0);
            readyMilitaryBuildings.remove(0);

            Set<Point> land = new HashSet<>();

            land.addAll(root.getDefendedLand());

            while (true) {
                boolean addedToBorder = false;
                List<Building> buildingsAlreadyAdded = new LinkedList<>();

                for (Building b : readyMilitaryBuildings) {
                    if (b.occupied() && land.contains(b.getPosition())) {
                        land.addAll(b.getDefendedLand());

                        addedToBorder = true;
                        buildingsAlreadyAdded.add(b);
                    }
                }

                readyMilitaryBuildings.removeAll(buildingsAlreadyAdded);

                if (!addedToBorder) {
                    break;
                }
            }

            result.add(new Land(land));
        }

        return result;
    }

    @Override
    public String toString() {
        return "Border: " + border + ", containing" + points;
    }
}
