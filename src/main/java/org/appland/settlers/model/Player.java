/**
 *
 */
package org.appland.settlers.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author johan
 *
 */
public class Player {

    private List<Land>           ownedLands;
    private final String         name;
    private final List<Building> buildings;
    private List<Point>          fieldOfView;
    private List<Point>          discoveredLand;

    public Player(String n) {
        name           = n;
        buildings      = new LinkedList<>();
        ownedLands     = new LinkedList<>();
        fieldOfView    = new LinkedList<>();
        discoveredLand = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    void removeBuilding(Building b) {
        buildings.remove(b);
    }

    void addBuilding(Building house) {
        buildings.add(house);
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    boolean isWithinBorder(Point position) {
        for (Land land : ownedLands) {
            if (land.isWithinBorder(position)) {
                return true;
            }
        }

        return false;
    }

    List<Collection<Point>> getBorders() {        
        List<Collection<Point>> result = new LinkedList<>();

        for (Land land : ownedLands) {
            result.add(land.getBorder());
        }

        return result;
    }

    void updateBorder() {
        ownedLands = Land.calculateLandWithinBorders(getBuildings());

        /* Update field of view */
        updateDiscoveredLand();

        fieldOfView = calculateFieldOfView(discoveredLand);
    }

    Iterable<Land> getLands() {
        return ownedLands;
    }

    List<Point> getFieldOfView() {
        return fieldOfView;
    }

    private List<Point> calculateFieldOfView(Collection<Point> discoveredLand) {
        return GameUtils.hullWanderer(discoveredLand);
    }

    private void updateDiscoveredLand() {
        for (Building b : buildings) {
            if (b.isMilitaryBuilding()) {
                discoveredLand.addAll(b.getDiscoveredLand());
            }
        }
    }

    List<Point> getDiscoveredLand() {
        return discoveredLand;
    }

    void discover(Point p) {
        if (!discoveredLand.contains(p)) {
            discoveredLand.add(p);

            fieldOfView = calculateFieldOfView(discoveredLand);
        }
    }    

    public int getAvailableAttackersForBuilding(Building buildingToAttack) throws Exception {
        int availableAttackers = 0;

        if (!buildingToAttack.isMilitaryBuilding()) {
            throw new Exception("Cannot get available attackers for non-military building");
        }

        if (equals(buildingToAttack.getPlayer())) {
            throw new Exception("Cannot get available attackers for own building");
        }

        /* Count militaries in military buildings that can reach the building */
        for (Building b : getBuildings()) {
            if (!b.isMilitaryBuilding()) {
                continue;
            }

            if (b.canAttack(buildingToAttack)) {
                availableAttackers += b.getHostedMilitary() - 1;
            }
        }

        return availableAttackers;
    }
}
