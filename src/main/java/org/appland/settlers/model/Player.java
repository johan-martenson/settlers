package org.appland.settlers.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
    private Color                color;
    private String               name;

    private final List<Building>          buildings;
    private final Set<Point>              discoveredLand;
    private final List<Material>          transportPriorities;
    private final Collection<Point>       ownedLand;
    private final List<Collection<Point>> borders;
    private final Map<Class<? extends Building>, Integer> foodQuota;
    private final Map<Class<? extends Building>, Integer> coalQuota;
    private final Map<Material, Integer>  producedMaterials;
    private final List<Message> messages;
    private boolean treeConservationProgramActive;


    public Player(String name, Color color) {
        this.name           = name;
        this.color          = color;
        buildings           = new LinkedList<>();
        fieldOfView         = new LinkedList<>();
        discoveredLand      = new HashSet<>();
        transportPriorities = new LinkedList<>();
        ownedLand           = new ArrayList<>();
        borders             = new ArrayList<>();
        producedMaterials   = new HashMap<>();

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

        /* There are no messages at start */
        messages = new ArrayList<>();

        /* The tree conservation program is not active at start */
        treeConservationProgramActive = false;
    }

    public String getName() {
        return name;
    }

    void removeBuilding(Building building) {
        buildings.remove(building);
    }

    void addBuilding(Building house) {
        buildings.add(house);
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public boolean isWithinBorder(Point point) {
        return ownedLand.contains(point);
    }

    public List<Collection<Point>> getBorders() {
        return borders;
    }

    public Collection<Point> getLandInPoints() {
        return ownedLand;
    }

    public List<Point> getFieldOfView() {
        return fieldOfView;
    }

    private List<Point> calculateFieldOfView(Collection<Point> discoveredLand) {
        return GameUtils.hullWanderer(discoveredLand);
    }

    private void updateDiscoveredLand() {
        for (Building building : buildings) {
            if (building.isMilitaryBuilding() && building.occupied()) {
                discoveredLand.addAll(building.getDiscoveredLand());
            }
        }
    }

    public Set<Point> getDiscoveredLand() {
        return discoveredLand;
    }

    void discover(Point point) {
        if (!discoveredLand.contains(point)) {
            discoveredLand.add(point);

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
        for (Building building : getBuildings()) {
            if (!building.isMilitaryBuilding()) {
                continue;
            }

            if (building.canAttack(buildingToAttack) && building.getNumberOfHostedMilitary() > 1) {
                availableAttackers += building.getNumberOfHostedMilitary() - 1;
            }
        }

        return availableAttackers;
    }

    public void attack(Building buildingToAttack, int nrAttackers) throws Exception {
        List<Building> eligibleBuildings = new LinkedList<>();

        /* Find all eligible buildings to attack from */
        for (Building building : getBuildings()) {
            if (!building.isMilitaryBuilding()) {
                continue;
            }

            if (!building.canAttack(buildingToAttack)) {
                continue;
            }

            eligibleBuildings.add(building);
        }

        /* Retrieve militaries from the buildings */
        int allocated = 0;

        Set<Point> reservedSpots = new HashSet<>();
        Point center             = buildingToAttack.getFlag().getPosition();

        for (Building building : eligibleBuildings) {

            if (allocated == nrAttackers) {
                break;
            }

            if (building.getNumberOfHostedMilitary() < 2) {
                continue;
            }

            boolean foundSpot;

            while (building.getNumberOfHostedMilitary() > 1) {
                if (allocated == nrAttackers) {
                    break;
                }

                /* Retrieve a military from the building */
                Military military = building.retrieveMilitary();

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

                            if (map.findWayOffroad(building.getPosition(), point, null) == null) {
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

        buildingToAttack.getPlayer().reportUnderAttack(buildingToAttack);
    }

    void setMap(GameMap map) {
        this.map = map;
    }

    void setLands(List<Land> updatedLands) {

        /* Update full list of owned land and the list of borders */
        ownedLand.clear();
        borders.clear();
        for (Land land : updatedLands) {
            ownedLand.addAll(land.getPointsInLand());
            borders.addAll(land.getBorders());
        }

        if (!updatedLands.isEmpty()) {

            /* Update field of view */
            updateDiscoveredLand();

            fieldOfView = calculateFieldOfView(discoveredLand);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    Storage getClosestStorage(Point position, Building avoid) throws InvalidRouteException {
        Storage storage = null;
        int distance = Integer.MAX_VALUE;

        for (Building building : getBuildings()) {
            if (building.equals(avoid)) {
                continue;
            }

            if (! (building instanceof Storage)) {
                continue;
            }

            if (building.getFlag().getPosition().equals(position)) {
                storage = (Storage)building;
                break;
            }

            if (position.equals(building.getFlag().getPosition())) {
                return (Storage)building;
            }

            List<Point> path = map.findWayWithExistingRoads(position, building.getFlag().getPosition());

            if (path == null) {
                continue;
            }

            if (path.size() < distance) {
                distance = path.size();
                storage = (Storage) building;
            }
        }

        return storage;
    }

    public Map<Material, Integer> getInventory() {

        Map<Material, Integer> result = new HashMap<>();
        int current;

        for (Building building : getBuildings()) {

            if ( !( building instanceof Storage)) {
                continue;
            }

            for (Material material : Material.values()) {
                if (!result.containsKey(material)) {
                    result.put(material, 0);
                }

                current = result.get(material);

                current = current + building.getAmount(material);

                result.put(material, current);
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

    public void setTransportPriority(int priority, Material material) throws InvalidUserActionException {

        /* Throw an exception if the material is a worker because worker transport priorities cannot be set */
        if (material.isWorker()) {
            throw new InvalidUserActionException("Cannot set priority for worker " + material);
        }

        /* Throw an exception if the priority is negative or too large */
        if (priority < 0) {
            throw new InvalidUserActionException("Cannot set a negative transport priority (" + priority + ") for " + material);
        } else if (priority >= Material.getTransportableItems().size()) {
            throw new InvalidUserActionException("Cannot set a higher transport priority (" + priority + ") than the amount of transportable items");
        }

        transportPriorities.remove(material);

        transportPriorities.add(priority, material);
    }

    int getTransportPriority(Cargo crop) {
        int i = 0;

        for (Material material : transportPriorities) {
            if (crop.getMaterial() == material) {
                return i;
            }

            i++;
        }

        return Integer.MAX_VALUE;
    }

    public List<Material> getTransportPriorityList() {
        return transportPriorities;
    }

    public Player getPlayerAtPoint(Point point) {

        /* Don't allow lookup of points the player hasn't discovered yet */
        if (!getDiscoveredLand().contains(point)) {
            return null;
        }

        /* Check each player if they own the point and return the player that does */
        for (Player player : map.getPlayers()) {
            if (player.isWithinBorder(point)) {
                return player;
            }
        }

        /* Return null if no player owns the point */
        return null;
    }

    Storage getClosestStorageOffroad(Point position) {
        Building storage = null;
        double distance = Double.MAX_VALUE;

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

    public Collection<Point> getAvailableFlagPoints() {
        return map.getAvailableFlagPoints(this);
    }

    public Map<Point, Size> getAvailableHousePoints() {
        return map.getAvailableHousePoints(this);
    }

    public Collection<Point> getAvailableMiningPoints() {
        return map.getAvailableMinePoints(this);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProducedMaterial(Material material) {
        return producedMaterials.getOrDefault(material, 0);
    }

    public void reportProduction(Material material) {
        int amount = producedMaterials.getOrDefault(material, 0);

        producedMaterials.put(material, amount + 1);
    }

    public List<Message> getMessages() {
        return messages;
    }

    void reportMilitaryBuildingReady(Building building) {
        messages.add(new MilitaryBuildingReadyMessage(building));
    }

    void reportMilitaryBuildingOccupied(Building building) {
        messages.add(new MilitaryBuildingOccupiedMessage(building));
    }

    void reportNoMoreResourcesForBuilding(Building building) {
        messages.add(new NoMoreResourcesMessage(building));
    }

    void reportGeologicalFinding(Point point, Material foundMaterial) {
        messages.add(new GeologistFindMessage(point, foundMaterial));
    }

    void reportBuildingLost(Building building) {
        messages.add(new BuildingLostMessage(building));
    }

    void reportBuildingCaptured(Building building) {
        messages.add(new BuildingCapturedMessage(building));
    }

    void reportUnderAttack(Building building) {
        messages.add(new UnderAttackMessage(building));
    }

    void reportStorageReady(Storage storage) {
        messages.add(new StoreHouseIsReadyMessage(storage));
    }

    public void activateTreeConservationProgram(Building building) {
        if (!treeConservationProgramActive) {
            messages.add(new TreeConservationProgramActivatedMessage(building));
        }

        treeConservationProgramActive = true;
    }

    public boolean isTreeConservationProgramActive() {
        return treeConservationProgramActive;
    }

    public void deactivateTreeConservationProgram(Building building) {
        if (treeConservationProgramActive) {
            messages.add(new TreeConservationProgramDeactivatedMessage(building));
        }

        treeConservationProgramActive = false;
    }
}
