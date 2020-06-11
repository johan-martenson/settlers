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

import static org.appland.settlers.model.Material.PLANK;

/**
 * @author johan
 *
 */
public class Player {

    private static final int PLANKS_THRESHOLD_FOR_TREE_CONSERVATION_PROGRAM = 10;
    private GameMap map;
    private Color   color;
    private String  name;
    private boolean treeConservationProgramActive;

    private final List<Building>          buildings;
    private final Set<Point>              discoveredLand;
    private final List<Material>          transportPriorities;
    private final Collection<Point>       ownedLand;
    private final Map<Class<? extends Building>, Integer> foodQuota;
    private final Map<Class<? extends Building>, Integer> coalQuota;
    private final Map<Material, Integer>  producedMaterials;
    private final List<Message> messages;
    private final Set<PlayerGameViewMonitor> gameViewMonitors;
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
    private final List<Stone> newStones;

    private List<BorderChange> changedBorders;
    private final Set<Point> borderPoints;
    private final List<Worker> newWorkers;
    private final Set<Point> changedAvailableConstruction;
    private final List<Point> newOwnedLand;
    private final List<Point> newLostLand;
    private final List<Message> newMessages;
    private final List<Road> promotedRoads;
    private boolean treeConservationProgramEnabled;

    public Player(String name, Color color) {
        this.name           = name;
        this.color          = color;
        buildings           = new LinkedList<>();
        discoveredLand      = new HashSet<>();
        transportPriorities = new LinkedList<>();
        ownedLand           = new ArrayList<>();
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

        /* The tree conservation program is enabled by default */
        treeConservationProgramEnabled = true;

        /* Prepare for monitors of the game */
        gameViewMonitors = new HashSet<>();

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
        changedBorders = null;
        newStones = new ArrayList<>();
        borderPoints = new HashSet<>();
        newWorkers = new ArrayList<>();
        changedAvailableConstruction = new HashSet<>();
        newOwnedLand = new ArrayList<>();
        newLostLand = new ArrayList<>();
        newMessages = new ArrayList<>();
        promotedRoads = new ArrayList<>();
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

    public Collection<Point> getLandInPoints() {
        return ownedLand;
    }

    private List<Point> calculateFieldOfView(Collection<Point> discoveredLand) {
        return GameUtils.hullWanderer(discoveredLand);
    }

    private void updateDiscoveredLand() {
        for (Building building : buildings) {
            if (building.isMilitaryBuilding() && building.isOccupied()) {

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

        /* Count soldiers in military buildings that can reach the building */
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

        /* Can only attack military buildings */
        if (!buildingToAttack.isMilitaryBuilding()) {
            throw new Exception("Cannot attack non-military building " + buildingToAttack);
        }

        /* A player cannot attack himself */
        if (buildingToAttack.getPlayer().equals(this)) {
            throw new Exception("Can only attack other players");
        }

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

        /* Retrieve soldiers from the buildings */
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

    void setLands(List<Land> updatedLands, Building building, BorderChangeCause cause) {

        /* Report the new border and the removed border */
        if (hasMonitor()) {
            Set<Point> fullNewBorderCalc = new HashSet<>();
            Set<Point> fullOldBorderCalc = new HashSet<>(borderPoints);

            for (Land land : updatedLands) {
                for (List<Point> border : land.getBorders()) {
                    fullNewBorderCalc.addAll(border);
                }
            }

            Set<Point> newBorderCalc = new HashSet<>(fullNewBorderCalc);
            newBorderCalc.removeAll(fullOldBorderCalc);

            Set<Point> removedBorderCalc = new HashSet<>(fullOldBorderCalc);
            removedBorderCalc.removeAll(fullNewBorderCalc);

            newBorder.addAll(newBorderCalc);
            removedBorder.addAll(removedBorderCalc);
        }

        /* Update full list of owned land and the list of borders */
        List<Point> updatedOwnedLand = new ArrayList<>();

        borderPoints.clear();

        for (Land land : updatedLands) {
            updatedOwnedLand.addAll(land.getPointsInLand());

            for (Collection<Point> borderForLand : land.getBorders()) {
                borderPoints.addAll(borderForLand);
            }
        }

        if (!updatedLands.isEmpty()) {

            /* Update field of view */
            updateDiscoveredLand();
        }

        /* Calculate and remember the new owned land */
        List<Point> calcNewOwnedLand = new ArrayList<>(updatedOwnedLand);
        calcNewOwnedLand.removeAll(ownedLand);

        List<Point> calcNewLostLand = new ArrayList<>(ownedLand);
        calcNewLostLand.removeAll(updatedOwnedLand);

        /* Report lost land */
        if (!calcNewLostLand.isEmpty() && cause == BorderChangeCause.MILITARY_BUILDING_OCCUPIED) {
            reportThisBuildingHasCausedLostLand(building);
        }

        /* Make the updated land the current */
        ownedLand.clear();
        ownedLand.addAll(updatedOwnedLand);

        if (hasMonitor()) {
            newOwnedLand.clear();
            newOwnedLand.addAll(calcNewOwnedLand);

            newLostLand.clear();
            newLostLand.addAll(calcNewLostLand);
        }
    }

    private void reportThisBuildingHasCausedLostLand(Building building) {
        messages.add(new MilitaryBuildingCausedLostLandMessage(building));
    }

    @Override
    public String toString() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    Storehouse getClosestStorage(Point position, Building avoid) throws InvalidRouteException {
        Storehouse storehouse = null;
        int distance = Integer.MAX_VALUE;

        for (Building building : getBuildings()) {
            if (building.equals(avoid)) {
                continue;
            }

            /* Filter storage buildings that are not fully constructed */
            if (!building.isReady()) {
                continue;
            }

            if (! (building instanceof Storehouse)) {
                continue;
            }

            if (building.getFlag().getPosition().equals(position)) {
                storehouse = (Storehouse)building;
                break;
            }

            if (position.equals(building.getFlag().getPosition())) {
                return (Storehouse)building;
            }

            List<Point> path = map.findWayWithExistingRoads(position, building.getFlag().getPosition());

            if (path == null) {
                continue;
            }

            if (path.size() < distance) {
                distance = path.size();
                storehouse = (Storehouse) building;
            }
        }

        return storehouse;
    }

    public Map<Material, Integer> getInventory() {

        Map<Material, Integer> result = new HashMap<>();
        int current;

        for (Building building : getBuildings()) {

            if ( !( building instanceof Storehouse)) {
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

    Storehouse getClosestStorageOffroad(Point position) {
        Building storage = null;
        double distance = Double.MAX_VALUE;

        for (Building building : buildings) {

            /* Filter non-storage buildings */
            if (! (building instanceof Storehouse)) {
                continue;
            }

            double tmpDistance = position.distance(building.getPosition());

            if (tmpDistance < distance) {
                distance = tmpDistance;
                storage = building;
            }
        }

        return (Storehouse)storage;
    }

    boolean isAlive() {
        for (Building building : buildings) {
            if (building.isReady()) {
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
        MilitaryBuildingReadyMessage message = new MilitaryBuildingReadyMessage(building);

        messages.add(message);

        if (hasMonitor()) {
            newMessages.add(message);
        }
    }

    void reportMilitaryBuildingOccupied(Building building) {
        MilitaryBuildingOccupiedMessage message = new MilitaryBuildingOccupiedMessage(building);

        messages.add(message);

        if (hasMonitor()) {
            newMessages.add(message);
        }
    }

    void reportNoMoreResourcesForBuilding(Building building) {
        NoMoreResourcesMessage message = new NoMoreResourcesMessage(building);

        messages.add(message);

        if (hasMonitor()) {
            newMessages.add(message);
        }
    }

    void reportGeologicalFinding(Point point, Material foundMaterial) {
        GeologistFindMessage message = new GeologistFindMessage(point, foundMaterial);

        messages.add(message);

        if (hasMonitor()) {
            newMessages.add(message);
        }
    }

    void reportBuildingLost(Building building) {
        BuildingLostMessage message = new BuildingLostMessage(building);

        messages.add(message);

        if (hasMonitor()) {
            newMessages.add(message);
        }
    }

    void reportBuildingCaptured(Building building) {
        BuildingCapturedMessage message = new BuildingCapturedMessage(building);

        messages.add(message);

        if (hasMonitor()) {
            newMessages.add(message);
        }
    }

    void reportUnderAttack(Building building) {
        UnderAttackMessage message = new UnderAttackMessage(building);

        messages.add(message);

        if (hasMonitor()) {
            newMessages.add(message);
        }
    }

    void reportStorageReady(Storehouse storehouse) {
        StoreHouseIsReadyMessage message = new StoreHouseIsReadyMessage(storehouse);

        messages.add(message);

        if (hasMonitor()) {
            newMessages.add(message);
        }
    }

    public void activateTreeConservationProgram() {
        if (!treeConservationProgramActive) {
            TreeConservationProgramActivatedMessage message = new TreeConservationProgramActivatedMessage();

            messages.add(message);

            if (hasMonitor()) {
                newMessages.add(message);
            }
        }

        treeConservationProgramActive = true;
    }

    public boolean isTreeConservationProgramActive() {
        return treeConservationProgramActive;
    }

    public void deactivateTreeConservationProgram() {
        if (treeConservationProgramActive) {
            TreeConservationProgramDeactivatedMessage message = new TreeConservationProgramDeactivatedMessage();

            messages.add(message);

            if (hasMonitor()) {
                newMessages.add(message);
            }
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
            workersWithNewTargets.isEmpty() && changedBorders.isEmpty() && newStones.isEmpty() &&
            newMessages.isEmpty() && promotedRoads.isEmpty()) {
            return;
        }

        /* If the player has discovered new land - find out what is on that land */
        if (!newDiscoveredLand.isEmpty()) {
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

                changedBorders.add(new BorderChange(player, newDiscoveredLand, new ArrayList<>()));
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
        }

        /* Handle any changes in available construction */
        for (Flag newFlag : newFlags) {
            addChangedAvailableConstructionForFlag(newFlag);
        }

        for (Flag removedFlag : removedFlags) {
            addChangedAvailableConstructionForFlag(removedFlag);
        }

        for (Tree newTree : newTrees) {
            addChangedAvailableConstructionForTree(newTree);
        }

        for (Tree removedTree : removedTrees) {
            addChangedAvailableConstructionForTree(removedTree);
        }

        for (Building newBuilding : newBuildings) {
            Point point = newBuilding.getPosition();

            addChangedAvailableConstructionForSmallBuilding(newBuilding);

            /* Handle medium building */
            if (newBuilding.getSize() == Size.MEDIUM) {
                addChangedAvailableConstructionForMediumBuilding(newBuilding);
            }

            /* Handle large building
            Ref: TestPlacement: testAvailableConstructionAroundLargeHouse
             - left: none
             - up-left: none
             - up-right: none
             - right: none
             - down-right: none (is the flag)
             - down-left: none

             - left-down-left: medium building | flag
             - left-left: medium building | flag
             - left-up-left: flag
             - up-left-up-left: flag
             - up: flag
             - up-right-up-right: flag
             - up-right-right: small house | flag
             - right-right: small house | flag
             - right-down-right: ?
            */
            if (newBuilding.getSize() == Size.LARGE) {
                addChangedAvailableConstructionForLargeBuilding(newBuilding);
            }
        }

        for (Building changedBuilding : changedBuildings) {
            addChangedAvailableConstructionForSmallBuilding(changedBuilding);
        }

        for (Building removedBuilding : removedBuildings) {
            addChangedAvailableConstructionForSmallBuilding(removedBuilding);

            if (removedBuilding.getSize() == Size.MEDIUM) {
                addChangedAvailableConstructionForMediumBuilding(removedBuilding);
            }

            if (removedBuilding.getSize() == Size.LARGE) {
                addChangedAvailableConstructionForLargeBuilding(removedBuilding);
            }
        }

        for (Road road : newRoads) {

            /*
             - Each endpoint is now a flag
             - Cannot place anything on the points on the road next to the flags
             - All other points can have flags (if nothing else prevents it)

            TODO: verify that no other possible construction is affected. What happens with these?

             /       \       \      /            __
            /         \      /      \     __/      \

             */
            changedAvailableConstruction.addAll(road.getWayPoints());
        }

        for (Road road : removedRoads) {
            changedAvailableConstruction.addAll(road.getWayPoints());
        }

        /*
        TODO: Check that it's really only the point of the crop that is affected
         */
        for (Crop crop : newCrops) {
            addChangedAvailableConstructionForCrop(crop);
        }

        for (Crop crop : removedCrops) {
            addChangedAvailableConstructionForCrop(crop);
        }

        for (Stone stone : removedStones) {
            addChangedAvailableConstructionForStone(stone);
        }

        /* Add changed available construction if the border has been extended */
        if (!newBorder.isEmpty()) {
            changedAvailableConstruction.addAll(newOwnedLand);
            changedAvailableConstruction.addAll(newLostLand);
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
                new ArrayList<>(newStones), newWorkers,
                new ArrayList<>(changedAvailableConstruction),
                new ArrayList<>(newMessages),
                new ArrayList<>(promotedRoads));

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
        changedAvailableConstruction.clear();
        newOwnedLand.clear();
        newLostLand.clear();
        newMessages.clear();
        newStones.clear();
        promotedRoads.clear();
    }

    private void addChangedAvailableConstructionForStone(Stone stone) {
        Point point = stone.getPosition();

        changedAvailableConstruction.add(point);
        changedAvailableConstruction.add(point.upLeft()); // Is only flag when stone exists
        changedAvailableConstruction.add(point.upRight()); // Is only flag when stone exists
        changedAvailableConstruction.add(point.downLeft()); // Is only flag when stone exists
        changedAvailableConstruction.add(point.downRight()); // Is only flag when stone exists
        changedAvailableConstruction.add(point.left()); // Is only flag when stone exists
        changedAvailableConstruction.add(point.right()); // Is only flag when stone exists
    }

    private void addChangedAvailableConstructionForCrop(Crop crop) {
        Point point = crop.getPosition();

        changedAvailableConstruction.add(point); // Nothing can be constructed on the crop
        changedAvailableConstruction.add(point.upLeft()); // Can't place a building up-left because the flag would be on the crop
    }

    private void addChangedAvailableConstructionForLargeBuilding(Building building) {
        Point point = building.getPosition();

        changedAvailableConstruction.add(point.left().downLeft()); // Only medium building or flag
        changedAvailableConstruction.add(point.left().left()); // Only medium building or flag
        changedAvailableConstruction.add(point.left().upLeft()); // Only flag
        changedAvailableConstruction.add(point.upLeft().upLeft()); // Only flag
        changedAvailableConstruction.add(point.up()); // Only medium building or flag
        changedAvailableConstruction.add(point.upRight().upRight()); // Only flag
        changedAvailableConstruction.add(point.upRight().right()); // Only small building or flag
        changedAvailableConstruction.add(point.right().right()); // Only small building or flag

        // Not certain
        changedAvailableConstruction.add(point.downLeft().downLeft()); // ?
    }

    private void addChangedAvailableConstructionForMediumBuilding(Building building) {
        Point point = building.getPosition();

        changedAvailableConstruction.add(point.left().downLeft()); // _SEEMS LIKE_ only medium building or flag
        changedAvailableConstruction.add(point.downLeft().downLeft()); // _SEEMS LIKE_ only medium building or flag
    }

    private void addChangedAvailableConstructionForSmallBuilding(Building building) {
        Point point = building.getPosition();

        /* Handle small building first */
        changedAvailableConstruction.add(point);
        changedAvailableConstruction.add(point.left()); // Only flag
        changedAvailableConstruction.add(point.upLeft()); // Only flag
        changedAvailableConstruction.add(point.upRight()); // Only flag
        changedAvailableConstruction.add(point.right()); // Nothing can be built
        changedAvailableConstruction.add(point.down()); // Only medium building, not flag
        changedAvailableConstruction.add(point.up()); // Medium building or flag (?)

        // TODO: add tests for these!
        changedAvailableConstruction.add(point.upLeft().upLeft()); // Only medium building or flag
        changedAvailableConstruction.add(point.upRight().upRight()); // Only medium building or flag
        changedAvailableConstruction.add(point.upLeft().left()); // Only medium building or flag
    }

    private void addChangedAvailableConstructionForTree(Tree newTree) {
        Point point = newTree.getPosition();

        changedAvailableConstruction.add(point);
        changedAvailableConstruction.add(point.upLeft()); // From building to only flag
        changedAvailableConstruction.add(point.downRight()); // Building still ok. Flag?
        changedAvailableConstruction.add(point.downLeft()); // Small building still ok. Medium and large?
        changedAvailableConstruction.add(point.left()); // Small building or flag
        changedAvailableConstruction.add(point.right()); // Small building or flag
        changedAvailableConstruction.add(point.upRight()); // Small building or flag

        // Unaffected:
        // Left left: large building and flag ok
        // Right right: large building and flag ok
        // Down: large building and flag ok
        // Up: large building and flag (?) still ok
        // Up right, up right: large building and flag ok
        // Down right, down right: large building still ok
    }

    private void addChangedAvailableConstructionForFlag(Flag flag) {
        Point point = flag.getPosition();

        changedAvailableConstruction.add(point);
        changedAvailableConstruction.add(point.downRight()); // From flag to no flag, large building still ok
        changedAvailableConstruction.add(point.up()); // From building to flag
        changedAvailableConstruction.add(point.downLeft()); // Change to medium building, not flag
        changedAvailableConstruction.add(point.right()); // Change to medium building, not flag
        changedAvailableConstruction.add(point.left()); // Change to nothing allowed
        changedAvailableConstruction.add(point.left().upLeft()); // Change from small building to flag
        changedAvailableConstruction.add(point.upLeft()); // Change to only allow buildings (too close for flag)
        changedAvailableConstruction.add(point.upRight()); // Change from mine to none
        changedAvailableConstruction.add(point.upLeft().upLeft()); // Change to only flag
    }

    public Set<Point> getBorderPoints() {
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

    void manageTreeConservationProgram() {

        /* Enable/disable the tree conservation program if needed */
        if (shouldConserveTrees()) {

            if (!treeConservationProgramActive && treeConservationProgramEnabled) {
                activateTreeConservationProgram();
            }
        } else {
            if (treeConservationProgramActive) {
                deactivateTreeConservationProgram();
            }
        }
    }

    private boolean shouldConserveTrees() {
        int amountPlanks = 0;

        /* Go through each Storehouse and count the amount of planks */
        for (Building building : buildings) {

            /* Filter other houses */
            if (! (building instanceof Storehouse)) {
                continue;
            }

            /* Filter non-ready store houses */
            if (!building.isReady()) {
                continue;
            }

            amountPlanks = amountPlanks + building.getAmount(PLANK);

            if (amountPlanks > PLANKS_THRESHOLD_FOR_TREE_CONSERVATION_PROGRAM) {
                return false;
            }
        }

        return true;
    }

    public boolean canAttack(Building building) throws Exception {

        /* Can only attack military buildings */
        if (!building.isMilitaryBuilding()) {
            return false;
        }

        /* Can not attack itself */
        if (this.equals(building.getPlayer())) {
            return false;
        }

        /* Can only attack buildings that are occupied */
        if (!building.isOccupied()) {
            return false;
        }

        /* Check that there are available attackers */
        if (getAvailableAttackersForBuilding(building) == 0) {
            return false;
        }

        return true;
    }

    public void reportPromotedRoad(Road road) {
        promotedRoads.add(road);
    }

    public void enableTreeConservationProgram() {
        treeConservationProgramEnabled = true;
    }

    public boolean isTreeConservationProgramEnabled() {
        return treeConservationProgramEnabled;
    }

    public void disableTreeConservationProgram() {
        treeConservationProgramEnabled = false;
        treeConservationProgramActive = false;
    }
}
