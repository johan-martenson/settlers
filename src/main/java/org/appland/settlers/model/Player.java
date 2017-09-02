/**
 *
 */
package org.appland.settlers.model;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    private final Color          color;
    private final List<Material> transportPriorities;
    private final Map<Class<? extends Building>, Integer> foodQuota;
    private final Map<Class<? extends Building>, Integer> coalQuota;

    public Player(String n, Color c) {
        name                = n;
        color               = c;
        buildings           = new LinkedList<>();
        ownedLands          = new LinkedList<>();
        fieldOfView         = new LinkedList<>();
        discoveredLand      = new HashSet<>();
        transportPriorities = new LinkedList<>();

        /* Create the food quota and set it to equal distribution */
        foodQuota = new HashMap<>();

        foodQuota.put(GoldMine.class, 1);
        foodQuota.put(IronMine.class, 1);
        foodQuota.put(CoalMine.class, 1);
        foodQuota.put(GraniteMine.class, 1);

        /* Create the coal quota and set it to equal distribution */
        coalQuota = new HashMap<>();

        coalQuota.put(IronSmelter.class, 1);
        coalQuota.put(Mint.class, 1);
        coalQuota.put(Armory.class, 1);

        /* Set the initial transport priority */
        transportPriorities.addAll(Arrays.asList(Material.values()));
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
        return Collections.unmodifiableList(buildings);
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

    public Collection<Land> getLands() {
        return Collections.unmodifiableCollection(ownedLands);
    }

    public List<Point> getFieldOfView() {
        return Collections.unmodifiableList(fieldOfView);
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

    public Set<Point> getDiscoveredLand() {
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

            if (b.canAttack(buildingToAttack) && b.getNumberOfHostedMilitary() > 1) {
                availableAttackers += b.getNumberOfHostedMilitary() - 1;
            }
        }

        return availableAttackers;
    }

    public void attack(Building buildingToAttack, int nrAttackers) throws Exception {
        List<Building> eligibleBuildings = new LinkedList<>();

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

        /* Retrieve militaries from the buildings */
        int allocated = 0;

        Set<Point> reservedSpots = new HashSet<>();
        Point center             = buildingToAttack.getFlag().getPosition();

        for (Building b : eligibleBuildings) {

            if (allocated == nrAttackers) {
                break;
            }

            if (b.getNumberOfHostedMilitary() < 2) {
                continue;
            }

            boolean foundSpot;

            while (b.getNumberOfHostedMilitary() > 1) {
                if (allocated == nrAttackers) {
                    break;
                }

                /* Retrieve a military from the building */
                Military military = b.retrieveMilitary();

                /* Make the military move to close to the building to attack */
                foundSpot = false;

                if (!reservedSpots.contains(center)) {
                    military.attack(buildingToAttack, center);

                    reservedSpots.add(center);
                } else {
                    for (int radius = 0; radius < 20; radius++) {
                        for (Point point : map.getPointsWithinRadius(center, radius)) {

                            if (reservedSpots.contains(point)) {
                                continue;
                            }

                            if (map.findWayOffroad(b.getPosition(), point, null) == null) {
                                continue;
                            }

                            military.attack(buildingToAttack, point);

                            reservedSpots.add(point);

                            foundSpot = true;

                            break;
                        }

                        if (foundSpot) {
                            break;
                        }
                    }
                }

                allocated++;
            }
        }
    }

    void setMap(GameMap m) {
        map = m;
    }

    void setLands(List<Land> value) {
        ownedLands.clear();
        ownedLands.addAll(value);

        /* Stop here if there is no owned land */
        if (ownedLands.isEmpty()) {
            return;
        }

        /* Update field of view */
        updateDiscoveredLand();

        fieldOfView = calculateFieldOfView(discoveredLand);
    }

    @Override
    public String toString() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    Building getClosestStorage(Point position, Building avoid) throws InvalidRouteException {
        Storage storage = null;
        int distance = Integer.MAX_VALUE;

        for (Building b : getBuildings()) {
            if (b.equals(avoid)) {
                continue;
            }

            if (! (b instanceof Storage)) {
                continue;
            }

            if (b.getFlag().getPosition().equals(position)) {
                storage = (Storage)b;
                break;
            }

            if (position.equals(b.getFlag().getPosition())) {
                return b;
            }

            List<Point> path = map.findWayWithExistingRoads(position, b.getFlag().getPosition());

            if (path == null) {
                continue;
            }

            if (path.size() < distance) {
                distance = path.size();
                storage = (Storage) b;
            }
        }

        return storage;
    }

    public Map<Material, Integer> getInventory() {

        Map<Material, Integer> result = new HashMap<>();
        int current;

        for (Building b : getBuildings()) {

            if ( !( b instanceof Storage)) {
                continue;
            }

            for (Material m : Material.values()) {
                if (!result.containsKey(m)) {
                    result.put(m, 0);
                }

                current = result.get(m);

                current = current + b.getAmount(m);

                result.put(m, current);
            }
        }

        return result;
    }

    int getFoodQuota(Class<? extends Building> aClass) {
        return foodQuota.get(aClass);
    }

    public void setFoodQuota(Class<? extends Building> aClass, int i) {
        foodQuota.put(aClass, i);
    }

    public void setCoalQuota(Class<? extends Building> aClass, int i) {
        coalQuota.put(aClass, i);
    }

    int getCoalQuota(Class<? extends Building> aClass) {
        return coalQuota.get(aClass);
    }

    public GameMap getMap() {
        return map;
    }

    public void setTransportPriority(int priority, Material material) {
        synchronized (transportPriorities) {
            transportPriorities.remove(material);

            transportPriorities.add(priority, material);
        }
    }

    int getTransportPriority(Cargo c) {
        int i = 0;

        for (Material m : transportPriorities) {
            if (c.getMaterial() == m) {
                return i;
            }

            i++;
        }

        return Integer.MAX_VALUE;
    }

    public List<Material> getTransportPriorityList() {
        return transportPriorities;
    }

    public Player getPlayerAtPoint(Point p) {

        /* Don't allow lookup of points the player hasn't discovered yet */
        if (!getDiscoveredLand().contains(p)) {
            return null;
        }

        /* Check each player if they own the point and return the player that 
           does
        */
        for (Player player : map.getPlayers()) {
            if (player.isWithinBorder(p)) {
                return player;
            }
        }

        /* Return null if no player owns the point */
        return null;
    }

    Storage getClosestStorageOffroad(Point position) {
        Building storage = null;
        Double distance = Double.MAX_VALUE;

        for (Building building : buildings) {

            /* Filter non-storage buildings */
            if (! (building instanceof Storage)) {
                continue;
            }

            double tmpDistance = position.distance(building.getPosition());

            if (tmpDistance < distance) {
                distance = tmpDistance;
                storage = building;
            }
        }

        return (Storage)storage;
    }

    boolean isAlive() {
        for (Building building : buildings) {
            if (building.ready()) {
                return true;
            }
        }

        return false;
    }
}
