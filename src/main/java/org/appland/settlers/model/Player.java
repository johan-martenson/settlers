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
    private List<Point>          fieldOfView;

    private final String         name;
    private final List<Building> buildings;
    private final List<Point>    discoveredLand;

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

    public boolean isWithinBorder(Point position) {
        for (Land land : ownedLands) {
            if (land.isWithinBorder(position)) {
                return true;
            }
        }

        return false;
    }

    public List<Collection<Point>> getBorders() {        
        List<Collection<Point>> result = new LinkedList<>();

        for (Land land : ownedLands) {
            result.add(land.getBorder());
        }

        return result;
    }

    void updateBorder() {
        if (getBuildings().isEmpty()) {
            return;
        }
        
        ownedLands = Land.calculateLandWithinBorders(getBuildings());

        /* Update field of view */
        updateDiscoveredLand();
        
        fieldOfView = calculateFieldOfView(discoveredLand);
    }

    Iterable<Land> getLands() {
        return ownedLands;
    }

    public List<Point> getFieldOfView() {
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

    public List<Point> getDiscoveredLand() {
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

            if (b.canAttack(buildingToAttack) && b.getHostedMilitary() > 1) {
                availableAttackers += b.getHostedMilitary() - 1;
            }
        }

        return availableAttackers;
    }

    public void attack(Building barracks1) {
        
    }
}
