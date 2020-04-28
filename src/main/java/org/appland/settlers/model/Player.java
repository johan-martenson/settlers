package org.appland.settlers.model;

import java.awt.Color;
import java.util.ArrayList;
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
    private Color                color;
    private String               name;
    private boolean treeConservationProgramActive;

    private final List<Building>          buildings;
    private final Set<Point>              discoveredLand;
    private final List<Material>          transportPriorities;
    private final Collection<Point>       ownedLand;
    private final List<Collection<Point>> borders;
    private final Map<Class<? extends Building>, Integer> foodQuota;
    private final Map<Class<? extends Building>, Integer> coalQuota;
    private final Map<Material, Integer>  producedMaterials;
    private final List<Message> messages;
    private final List<PlayerGameViewMonitor> gameViewMonitors;
    private final List<Worker> workersWithNewTargets;
    private final List<Building> changedBuildings;
    private final List<Flag> newFlags;
    private final List<Flag> removedFlags;
    private final List<Building> newBuildings;
    private final List<Building> removedBuildings;
    private final List<Road> removedRoads;
    private final List<Road> newRoads;
    private final List<Worker> removedWorkers;
    private final List<Tree> newTrees;
    private final List<Tree> removedTrees;
    private final List<Stone> removedStones;
    private final List<Sign> newSigns;
    private final List<Sign> removedSigns;
    private final List<Crop> newCrops;
    private final List<Crop> removedCrops;
    private final Set<Point> newDiscoveredLand;
    private final List<Point> newBorder;
    private final List<Point> removedBorder;
    private final List<Worker> workersEnteredBuildings;
    private final List<Stone> newStones;

    private List<BorderChange> changedBorders;
    private final Set<Point> borderPoints;
    private final List<Worker> newWorkers;

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

        /* Prepare for monitors of the game */
        gameViewMonitors = new ArrayList<>();

        workersWithNewTargets = new ArrayList<>();
        changedBuildings = new ArrayList<>();
        newFlags = new ArrayList<>();
        removedFlags = new ArrayList<>();
        newBuildings = new ArrayList<>();
        removedBuildings = new ArrayList<>();
        removedRoads = new ArrayList<>();
        newRoads = new ArrayList<>();
        removedWorkers = new ArrayList<>();
        newTrees = new ArrayList<>();
        removedTrees = new ArrayList<>();
        removedStones = new ArrayList<>();
        newSigns = new ArrayList<>();
        removedSigns = new ArrayList<>();
        newCrops = new ArrayList<>();
        removedCrops = new ArrayList<>();
        newDiscoveredLand = new HashSet<>();
        newBorder = new ArrayList<>();
        removedBorder = new ArrayList<>();
        workersEnteredBuildings = new ArrayList<>();
        changedBorders = null;
        newStones = new ArrayList<>();
        borderPoints = new HashSet<>();
        newWorkers = new ArrayList<>();
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

                Collection<Point> landDiscoveredByBuilding = building.getDiscoveredLand();

                /* Remember the points that are newly discovered */
                if (!gameViewMonitors.isEmpty()) {
                    for (Point point : landDiscoveredByBuilding) {
                        if (!discoveredLand.contains(point)) {
                            newDiscoveredLand.add(point);
                        }
                    }
                }

                discoveredLand.addAll(landDiscoveredByBuilding);
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

            /* Report that this is a newly discovered point */
            if (hasMonitor()) {
                newDiscoveredLand.add(point);
            }
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

    public void setMap(GameMap map) {
        this.map = map;
    }

    void setLands(List<Land> updatedLands) {

        /* Report the new border and the removed border */
        if (hasMonitor()) {
            Set<Point> fullNewBorderCalc = new HashSet<>();
            Set<Point> fullOldBorderCalc = new HashSet<>();
            Set<Point> newBorderCalc = new HashSet<>();
            Set<Point> removedBorderCalc = new HashSet<>();

            for (Land land : updatedLands) {
                for (List<Point> border : land.getBorders()) {
                    fullNewBorderCalc.addAll(border);
                }
            }

            for (Collection<Point> oldBorder : borders) {
                fullOldBorderCalc.addAll(oldBorder);
            }

            newBorderCalc.addAll(fullNewBorderCalc);
            newBorderCalc.removeAll(fullOldBorderCalc);

            removedBorderCalc.addAll(fullOldBorderCalc);
            removedBorderCalc.removeAll(fullNewBorderCalc);

            newBorder.addAll(newBorderCalc);
            removedBorder.addAll(removedBorderCalc);
        }

        /* Update full list of owned land and the list of borders */
        ownedLand.clear();
        borders.clear();

        for (Land land : updatedLands) {
            ownedLand.addAll(land.getPointsInLand());
            borders.addAll(land.getBorders());

            for (Collection<Point> borderForLand : land.getBorders()) {
                borderPoints.addAll(borderForLand);
            }
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

            /* Filter storage buildings that are not fully constructed */
            if (!building.ready()) {
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

    public void monitorGameView(PlayerGameViewMonitor monitor) {
        gameViewMonitors.add(monitor);
    }

    public boolean hasMonitor() {
        return !gameViewMonitors.isEmpty();
    }

    public void reportWorkerWithNewTarget(Worker worker) {
        if (worker.getPlannedPath().isEmpty() || worker.getPlannedPath().size() == 0) {
            return;
        }

        workersWithNewTargets.add(worker);
    }

    public void sendMonitoringEvents(long time) {

        /* Don't send an event if there is no new information */
        if (newFlags.isEmpty() && removedFlags.isEmpty() && newBuildings.isEmpty() &&
            newRoads.isEmpty() && removedRoads.isEmpty() && removedWorkers.isEmpty() &&
            changedBuildings.isEmpty() && removedBuildings.isEmpty() && newTrees.isEmpty() &&
            removedTrees.isEmpty() && removedStones.isEmpty() && newSigns.isEmpty() &&
            removedSigns.isEmpty() && newCrops.isEmpty() && removedCrops.isEmpty() &&
            newDiscoveredLand.isEmpty() && newBorder.isEmpty() && removedBorder.isEmpty() &&
            workersWithNewTargets.isEmpty() && changedBorders.isEmpty() && newStones.isEmpty()) {
            return;
        }

        /* If the player has discovered new land - find out what is on that land */
        for (Point point : newDiscoveredLand) {
            GameMap.PointInformation pointInformation = map.whatIsAtPoint(point);

            if (pointInformation == GameMap.PointInformation.TREE) {
                newTrees.add(map.getTreeAtPoint(point));
            }

            if (pointInformation == GameMap.PointInformation.STONE) {
                newStones.add(map.getStoneAtPoint(point));
            }

            if (pointInformation == GameMap.PointInformation.FLAG) {
                newFlags.add(map.getFlagAtPoint(point));
            }

            if (pointInformation == GameMap.PointInformation.BUILDING) {
                newBuildings.add(map.getBuildingAtPoint(point));
            }

            if (pointInformation == GameMap.PointInformation.ROAD) {
                newRoads.add(map.getRoadAtPoint(point));
            }

            if (pointInformation == GameMap.PointInformation.FLAG_AND_ROADS) {
                Flag flag = map.getFlagAtPoint(point);
                newFlags.add(flag);
                newRoads.addAll(map.getRoadsFromFlag(flag));
            }

            if (pointInformation == GameMap.PointInformation.SIGN) {
                Sign sign = map.getSignAtPoint(point);
                newSigns.add(sign);
            }

            if (pointInformation == GameMap.PointInformation.CROP) {
                Crop crop = map.getCropAtPoint(point);
                newCrops.add(crop);
            }
        }

        /* Find any discovered border for other players */
        for (Player player : map.getPlayers()) {
            if (player.equals(this)) {
                continue;
            }

            Set<Point> borderForPlayer = player.getBorderPoints();
            List<Point> discoveredBorder = new ArrayList<>();

            for (Point point : newDiscoveredLand) {
                if (borderForPlayer.contains(point)) {
                    discoveredBorder.add(point);
                }
            }

            if (discoveredBorder.isEmpty()) {
                continue;
            }

            changedBorders.add(new BorderChange(player, newDiscoveredLand, Collections.EMPTY_LIST));
        }

        /* Find any discovered workers */
        for (Worker worker : map.getWorkers()) {
            if (worker.getPlayer().equals(this)) {
                continue;
            }

            for (Point point : newDiscoveredLand) {
                if (worker.getPosition().equals(point)) {
                    newWorkers.add(worker);
                }
            }
        }

        /* Create the event message */
        GameChangesList gameChangesToReport = new GameChangesList(time,
                new ArrayList<>(workersWithNewTargets),
                new ArrayList<>(newFlags),
                new ArrayList<>(removedFlags),
                new ArrayList<>(newBuildings),
                new ArrayList<>(changedBuildings),
                new ArrayList<>(removedBuildings),
                new ArrayList<>(newRoads),
                new ArrayList<>(removedRoads),
                new ArrayList<>(removedWorkers),
                new ArrayList<>(newTrees),
                new ArrayList<>(removedTrees),
                new ArrayList<>(removedStones),
                new ArrayList<>(newSigns),
                new ArrayList<>(removedSigns),
                new ArrayList<>(newCrops),
                new ArrayList<>(removedCrops),
                new ArrayList<>(newDiscoveredLand),
                new ArrayList<>(changedBorders),
                new ArrayList<>(newStones), newWorkers);

        /* Send the event to all monitors */
        for (PlayerGameViewMonitor monitor : gameViewMonitors) {
            monitor.onViewChangesForPlayer(this, gameChangesToReport);
        }

        /* Clear out the lists to not pollute the next event with old information */
        newFlags.clear();
        removedFlags.clear();
        newBuildings.clear();
        removedBuildings.clear();
        newRoads.clear();
        removedRoads.clear();
        workersWithNewTargets.clear();
        removedWorkers.clear();
        changedBuildings.clear();
        newTrees.clear();
        removedTrees.clear();
        removedStones.clear();
        newSigns.clear();
        removedSigns.clear();
        newCrops.clear();
        removedCrops.clear();
        newDiscoveredLand.clear();
        newBorder.clear();
        removedBorder.clear();
    }

    private Set<Point> getBorderPoints() {
        return borderPoints;
    }

    public void reportChangedBuilding(Building building) {
        changedBuildings.add(building);
    }

    public void reportNewFlag(Flag flag) {
        newFlags.add(flag);
    }

    public void reportRemovedFlag(Flag flag) {
        removedFlags.add(flag);
    }

    public void reportNewBuilding(Building building) {
        newBuildings.add(building);
    }

    public void reportNewRoad(Road road) {
        newRoads.add(road);
    }

    public void reportRemovedRoad(Road road) {
        removedRoads.add(road);
    }

    public void reportRemovedWorker(Worker worker) {
        removedWorkers.add(worker);
    }

    public void reportRemovedBuilding(Building building) {
        removedBuildings.add(building);
    }

    public void reportNewTree(Tree tree) {
        newTrees.add(tree);
    }

    public void reportRemovedTree(Tree tree) {
        removedTrees.add(tree);
    }

    public void reportRemovedStone(Stone stone) {
        removedStones.add(stone);
    }

    public void reportNewSign(Sign sign) {
        newSigns.add(sign);
    }

    public void reportRemovedSign(Sign sign) {
        removedSigns.add(sign);
    }

    public void reportNewCrop(Crop crop) {
        newCrops.add(crop);
    }

    public void reportRemovedCrop(Crop crop) {
        removedCrops.add(crop);
    }

    public void reportChangedBorders(List<BorderChange> borderChanges) {
        changedBorders = borderChanges;
    }

    public BorderChange getBorderChange() {
        if (newBorder.isEmpty() && removedBorder.isEmpty()) {
            return null;
        }

        return new BorderChange(this, newBorder, removedBorder);
    }
}
