/**
 *
 */
package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author johan
 *
 */
public class Player {

    private List<Point>          fieldOfView;
    private GameMap              map;

    private final List<Land>     ownedLands;
    private final String         name;
    private final List<Building> buildings;
    private final Set<Point>     discoveredLand;

    public Player(String n) {
        name           = n;
        buildings      = new LinkedList<>();
        ownedLands     = new LinkedList<>();
        fieldOfView    = new LinkedList<>();
        discoveredLand = new HashSet<>();
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
            result.addAll(land.getBorders());
        }

        return result;
    }

    public Iterable<Land> getLands() {
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
        return Arrays.asList(discoveredLand.toArray(new Point[1]));
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

    public void attack(Building buildingToAttack, int nrAttackers) {
        List<Building> eligibleBuildings = new LinkedList<>();
        List<Point> meetups = new ArrayList<>();

        /* Find all eligible buildings to attack from */
        for (Building b : getBuildings()) {
            if (!b.isMilitaryBuilding()) {
                continue;
            }

            if (!b.canAttack(buildingToAttack)) {
                continue;
            }

            eligibleBuildings.add(b);
        }

        /* Construct list of targets */
        meetups = Military.getListOfPossibleMeetingPoints(buildingToAttack);

        /* Retrieve militaries from the buildings */
        int allocated = 0;

        for (Building b : eligibleBuildings) {
            if (allocated == nrAttackers) {
                break;
            }

            if (b.getHostedMilitary() < 2) {
                continue;
            }

            while (b.getHostedMilitary() > 1) {
                if (allocated == nrAttackers) {
                    break;
                }

                /* Retrieve a military from the building */
                Military military = b.retrieveMilitary();

                /* Make the military move to close to the building to attack */
                military.attack(buildingToAttack, meetups.get(0));

                meetups.remove(0);

                allocated++;
            }
        }
    }

    void sendDefense(Building buildingToAttack, Military opponent) {
        Military military = buildingToAttack.retrieveMilitary();

        military.fight(opponent);
    }

    void setMap(GameMap m) {
        map = m;
    }

    void setLands(List<Land> value) {
        ownedLands.clear();
        ownedLands.addAll(value);

        /* Update field of view */
        updateDiscoveredLand();
        
        fieldOfView = calculateFieldOfView(discoveredLand);
    }

    @Override
    public String toString() {
        return name;
    }
}
