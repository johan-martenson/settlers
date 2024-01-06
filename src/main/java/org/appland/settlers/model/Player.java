package org.appland.settlers.model;

import org.appland.settlers.assets.Nation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.appland.settlers.model.Material.PLANK;

/**
 * @author johan
 *
 */
public class Player {

    private static final int PLANKS_THRESHOLD_FOR_TREE_CONSERVATION_PROGRAM = 10;
    private static final int MAX_PRODUCTION_QUOTA = 10;
    private static final int MIN_PRODUCTION_QUOTA = 0;

    private GameMap map;
    private Color   color;
    private Nation  nation;
    private String  name;
    private boolean treeConservationProgramActive;
    private boolean treeConservationProgramEnabled;

    private final List<BorderChange> changedBorders;
    private final List<Building> buildings;
    private final Set<Point> discoveredLand;
    private final List<Material> transportPriorities;
    private final Set<Point> ownedLand;
    private final Map<Class<? extends Building>, Integer> foodAllocation;
    private final Map<Class<? extends Building>, Integer> coalAllocation;
    private final Map<Material, Integer> producedMaterials;
    private final List<Message> messages;
    private final Set<PlayerGameViewMonitor> gameViewMonitors;
    private final List<Worker> workersWithNewTargets;
    private final Set<Building> changedBuildings;
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
    private final Set<Point> borderPoints;
    private final List<Worker> newWorkers;
    private final Set<Point> changedAvailableConstruction;
    private final List<Point> newOwnedLand;
    private final List<Point> newLostLand;
    private final List<Message> newMessages;
    private final List<Road> promotedRoads;
    private final Map<Material, Integer> toolProductionQuotas;
    private final List<TransportCategory> transportCategoryPriorities;
    private final Collection<Flag> changedFlags;
    private final List<Point> removedDeadTrees;
    private final List<Point> discoveredDeadTrees;
    private final List<Crop> harvestedCrops;
    private final List<Ship> newShips;
    private final List<Ship> finishedShips;
    private final List<Ship> shipsWithNewTargets;
    private final Map<Worker, WorkerAction> workersWithStartedActions;
    private final List<Point> removedDecorations;
    private final Map<Point, DecorationType> newDecorations;
    private final Set<Object> detailedMonitoring;
    private final Collection<GameChangesList.NewAndOldBuilding> upgradedBuildings;
    private final Set<Message> removedMessages;
    private final Map<Class<? extends Building>, Integer> wheatAllocation;
    private final Map<Class<? extends Building>, Integer> waterAllocation;
    private final Map<Class<? extends Building>, Integer> ironBarAllocation;

    public Player(String name, Color color) {
        this.name           = name;
        this.color          = color;

        this.nation         = Nation.ROMANS;

        buildings           = new LinkedList<>();
        discoveredLand      = new HashSet<>();
        transportPriorities = new LinkedList<>();
        ownedLand           = new HashSet<>();
        producedMaterials   = new EnumMap<>(Material.class);
        detailedMonitoring  = new HashSet<>();
        foodAllocation      = new HashMap<>();
        coalAllocation      = new HashMap<>();
        wheatAllocation     = new HashMap<>();
        waterAllocation     = new HashMap<>();
        ironBarAllocation   = new HashMap<>();
        messages            = new ArrayList<>();
        gameViewMonitors    = new HashSet<>();

        transportCategoryPriorities = new ArrayList<>();

        /* Create the food quota and set it to equal distribution */
        foodAllocation.put(GoldMine.class, 1);
        foodAllocation.put(IronMine.class, 1);
        foodAllocation.put(CoalMine.class, 1);
        foodAllocation.put(GraniteMine.class, 1);

        /* Create the coal quota and set it to equal distribution */
        coalAllocation.put(IronSmelter.class, 1);
        coalAllocation.put(Mint.class, 1);
        coalAllocation.put(Armory.class, 1);

        /* Create the wheat quota and set it to equal distribution */
        wheatAllocation.put(Mill.class, 1);
        wheatAllocation.put(DonkeyFarm.class, 1);
        wheatAllocation.put(PigFarm.class, 1);
        wheatAllocation.put(Brewery.class, 1);

        /* Create the water quota and set it to equal distribution */
        waterAllocation.put(Bakery.class, 1);
        waterAllocation.put(DonkeyFarm.class, 1);
        waterAllocation.put(PigFarm.class, 1);
        waterAllocation.put(Brewery.class, 1);

        /* Create the iron bar quota and set it to equal distribution */
        ironBarAllocation.put(Armory.class, 1);
        ironBarAllocation.put(Metalworks.class, 1);

        /* Set the initial transport priority */
        transportCategoryPriorities.addAll(Arrays.asList(TransportCategory.values()));

        setTransportPriorityForMaterials();

        /* There are no messages at start */

        /* The tree conservation program is not active at start */
        treeConservationProgramActive = false;

        /* The tree conservation program is enabled by default */
        treeConservationProgramEnabled = true;

        /* Prepare for monitors of the game */
        workersWithNewTargets = new ArrayList<>();
        changedBuildings = new HashSet<>();
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
        changedBorders = new ArrayList<>();
        newStones = new ArrayList<>();
        borderPoints = new HashSet<>();
        newWorkers = new ArrayList<>();
        changedAvailableConstruction = new HashSet<>();
        newOwnedLand = new ArrayList<>();
        newLostLand = new ArrayList<>();
        newMessages = new ArrayList<>();
        promotedRoads = new ArrayList<>();
        toolProductionQuotas = new EnumMap<>(Material.class);
        changedFlags = new HashSet<>();
        removedDeadTrees = new ArrayList<>();
        discoveredDeadTrees = new ArrayList<>();
        harvestedCrops = new ArrayList<>();
        newShips = new ArrayList<>();
        finishedShips = new ArrayList<>();
        shipsWithNewTargets = new ArrayList<>();
        workersWithStartedActions = new HashMap<>();
        removedDecorations = new ArrayList<>();
        newDecorations = new HashMap<>();
        upgradedBuildings = new ArrayList<>();
        removedMessages = new HashSet<>();

        /* Set default production of all tools */
        for (Material tool : Material.TOOLS) {
            toolProductionQuotas.put(tool, MAX_PRODUCTION_QUOTA);
        }
    }

    private void setTransportPriorityForMaterials() {

        transportPriorities.clear();

        for (TransportCategory transportCategory : transportCategoryPriorities) {
            transportPriorities.addAll(Arrays.asList(transportCategory.getMaterials()));
        }
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

    public Set<Point> getLandInPoints() {
        return ownedLand;
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

    public int getAvailableAttackersForBuilding(Building buildingToAttack) throws InvalidUserActionException {
        int availableAttackers = 0;

        if (!buildingToAttack.isMilitaryBuilding()) {
            throw new InvalidUserActionException("Cannot get available attackers for non-military building");
        }

        if (equals(buildingToAttack.getPlayer())) {
            throw new InvalidUserActionException("Cannot get available attackers for own building");
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

    public void attack(Building buildingToAttack, int nrAttackers) throws InvalidUserActionException {

        /* Can only attack military buildings */
        if (!buildingToAttack.isMilitaryBuilding()) {
            throw new InvalidUserActionException("Cannot attack non-military building " + buildingToAttack);
        }

        /* A player cannot attack himself */
        if (buildingToAttack.getPlayer().equals(this)) {
            throw new InvalidUserActionException("Can only attack other players");
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
                Military military = building.retrieveHostedSoldier();

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

        /* Update full list of owned land and the list of borders */
        Set<Point> updatedOwnedLand = new HashSet<>();

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
        Set<Point> calcNewOwnedLand = new HashSet<>(updatedOwnedLand);
        calcNewOwnedLand.removeAll(ownedLand);

        Set<Point> calcNewLostLand = new HashSet<>(ownedLand);
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

    Storehouse getClosestStorage(Point position, Building avoid) {
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

            if (!building.isStorehouse()) {
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

        Map<Material, Integer> result = new EnumMap<>(Material.class);
        int current;

        for (Building building : getBuildings()) {

            if (!building.isStorehouse()) {
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

    public int getFoodQuota(Class<? extends Building> aClass) {
        return foodAllocation.get(aClass);
    }

    public void setFoodQuota(Class<? extends Building> aClass, int i) {
        foodAllocation.put(aClass, i);
    }

    public void setCoalQuota(Class<? extends Building> aClass, int i) {
        coalAllocation.put(aClass, i);
    }

    public int getCoalQuota(Class<? extends Building> aClass) {
        return coalAllocation.get(aClass);
    }

    public GameMap getMap() {
        return map;
    }

    public void setTransportPriority(int priority, TransportCategory category) throws InvalidUserActionException {

        /* Throw an exception if the priority is negative or too large */
        if (priority < 0) {
            throw new InvalidUserActionException("Cannot set a negative transport priority (" + priority + ") for " + category);
        } else if (priority >= TransportCategory.values().length) {
            throw new InvalidUserActionException("Cannot set a higher transport priority (" + priority + ") than the amount of transportable items");
        }

        transportCategoryPriorities.remove(category);
        transportCategoryPriorities.add(priority, category);

        transportPriorities.clear();

        setTransportPriorityForMaterials();
    }

    int getTransportPriority(Cargo cargo) {
        int i = 0;

        for (Material material : transportPriorities) {
            if (cargo.getMaterial() == material) {
                return i;
            }

            i++;
        }

        return Integer.MAX_VALUE;
    }

    public List<TransportCategory> getTransportPriorities() {
        return transportCategoryPriorities;
    }

    List<Material> getTransportPrioritiesForEachMaterial() {
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
            if (!building.isStorehouse()) {
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

    public void setColor(Color color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProducedMaterial(Material material) {
        return producedMaterials.getOrDefault(material, 0);
    }

    public void reportProduction(Material material, Building building) {
        int amount = producedMaterials.getOrDefault(material, 0);

        producedMaterials.put(material, amount + 1);

        if (detailedMonitoring.contains(building)) {
            changedBuildings.add(building);
        }
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
        workersWithNewTargets.add(worker);
    }

    public void sendMonitoringEvents(long time) {

        /* Don't send an event if there is no new information */
        // Missing discoveredDeadTrees, changedAvailableConstruction, newWorkers
        if (newFlags.isEmpty() && removedFlags.isEmpty() && newBuildings.isEmpty() &&
            newRoads.isEmpty() && removedRoads.isEmpty() && removedWorkers.isEmpty() &&
            changedBuildings.isEmpty() && removedBuildings.isEmpty() && newTrees.isEmpty() &&
            removedTrees.isEmpty() && removedStones.isEmpty() && newSigns.isEmpty() &&
            removedSigns.isEmpty() && newCrops.isEmpty() && removedCrops.isEmpty() &&
            newDiscoveredLand.isEmpty() && newBorder.isEmpty() && removedBorder.isEmpty() &&
            workersWithNewTargets.isEmpty() && changedBorders.isEmpty() && newStones.isEmpty() &&
            newMessages.isEmpty() && promotedRoads.isEmpty() && changedFlags.isEmpty() &&
            removedDeadTrees.isEmpty() && harvestedCrops.isEmpty() && newShips.isEmpty() &&
            finishedShips.isEmpty() && shipsWithNewTargets.isEmpty() && workersWithStartedActions.isEmpty() &&
            removedDecorations.isEmpty() && newDecorations.isEmpty() &&upgradedBuildings.isEmpty()) {
            return;
        }

        /* Don't send reports about changes in the old building when an upgrade is finished */
        upgradedBuildings.forEach(newAndOldBuilding -> {
            changedBuildings.remove(newAndOldBuilding.oldBuilding);
            changedBuildings.remove(newAndOldBuilding.newBuilding);
        });

        /* If the player has discovered new land - find out what is on that land */
        if (!newDiscoveredLand.isEmpty()) {
            for (Point point : newDiscoveredLand) {
                MapPoint mapPoint = map.getMapPoint(point);

                if (mapPoint.isDeadTree()) {
                    discoveredDeadTrees.add(point);
                }

                if (mapPoint.isTree()) {
                    newTrees.add(mapPoint.getTree());
                }

                if (mapPoint.isStone()) {
                    newStones.add(mapPoint.getStone());
                }

                if (mapPoint.isFlag()) {
                    newFlags.add(mapPoint.getFlag());
                }

                if (mapPoint.isBuilding()) {
                    newBuildings.add(mapPoint.getBuilding());
                }

                if (mapPoint.isRoad()) {
                    newRoads.addAll(mapPoint.getConnectedRoads());
                }

                if (mapPoint.isSign()) {
                    newSigns.add(mapPoint.getSign());
                }

                if (mapPoint.isCrop()) {
                    newCrops.add(mapPoint.getCrop());
                }

                if (mapPoint.isDecoration()) {
                    newDecorations.put(point, mapPoint.getDecoration());
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

                changedBorders.add(new BorderChange(player, discoveredBorder, new ArrayList<>()));
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

            /* Changes to buildings that are not military and not destroyed do not affect available construction */
            if (!changedBuilding.isMilitaryBuilding() &&
                (changedBuilding.isReady() || changedBuilding.isUnderConstruction())) {
                continue;
            }

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

            removedBorder.forEach(newBorderPoint -> {
                var pointUpLeft = newBorderPoint.upLeft();

                if (!newOwnedLand.contains(pointUpLeft)) {
                    changedAvailableConstruction.add(pointUpLeft);
                }
            });
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
                new ArrayList<>(newStones),
                new ArrayList<>(newWorkers),
                new ArrayList<>(changedAvailableConstruction),
                new ArrayList<>(newMessages),
                new ArrayList<>(promotedRoads),
                new ArrayList<>(changedFlags),
                new ArrayList<>(removedDeadTrees),
                new ArrayList<>(discoveredDeadTrees),
                new ArrayList<>(harvestedCrops),
                new ArrayList<>(newShips),
                new ArrayList<>(finishedShips),
                new ArrayList<>(shipsWithNewTargets),
                new HashMap<>(workersWithStartedActions),
                new ArrayList<>(removedDecorations),
                newDecorations,
                new ArrayList<>(upgradedBuildings),
                new ArrayList<>(removedMessages));

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
        changedBorders.clear();
        changedFlags.clear();
        removedDeadTrees.clear();
        discoveredDeadTrees.clear();
        harvestedCrops.clear();
        newShips.clear();
        finishedShips.clear();
        shipsWithNewTargets.clear();
        workersWithStartedActions.clear();
        newWorkers.clear();
        removedDecorations.clear();
        newDecorations.clear();
        upgradedBuildings.clear();
        removedMessages.clear();
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

        changedAvailableConstruction.add(point.downLeftLeft()); // Only medium building or flag
        changedAvailableConstruction.add(point.leftLeft()); // Only medium building or flag
        changedAvailableConstruction.add(point.upLeftLeft()); // Only flag
        changedAvailableConstruction.add(point.upLeftUpLeft()); // Only flag
        changedAvailableConstruction.add(point.up()); // Only medium building or flag
        changedAvailableConstruction.add(point.upRightUpRight()); // Only flag
        changedAvailableConstruction.add(point.upRightRight()); // Only small building or flag
        changedAvailableConstruction.add(point.rightRight()); // Only small building or flag

        // Not certain
        changedAvailableConstruction.add(point.downLeftDownLeft()); // ?
    }

    private void addChangedAvailableConstructionForMediumBuilding(Building building) {
        Point point = building.getPosition();

        changedAvailableConstruction.add(point.downLeftLeft()); // _SEEMS LIKE_ only medium building or flag
        changedAvailableConstruction.add(point.downLeftDownLeft()); // _SEEMS LIKE_ only medium building or flag
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
        changedAvailableConstruction.add(point.upLeftUpLeft()); // Only medium building or flag
        changedAvailableConstruction.add(point.upRightUpRight()); // Only medium building or flag
        changedAvailableConstruction.add(point.upLeftLeft()); // Only medium building or flag
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
        // Left-left: large building and flag ok
        // Right-right: large building and flag ok
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
        changedAvailableConstruction.add(point.upLeftLeft()); // Change from small building to flag
        changedAvailableConstruction.add(point.upLeft()); // Change to only allow buildings (too close for flag)
        changedAvailableConstruction.add(point.upRight()); // Change from mine to none
        changedAvailableConstruction.add(point.upLeftUpLeft()); // Change to only flag
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

        for (BorderChange borderChange : borderChanges) {
            List<Point> added = new ArrayList<>();
            List<Point> removed = new ArrayList<>();

            if (borderChange.getPlayer().equals(this)) {
                changedBorders.add(borderChange);

                continue;
            }

            for (Point point : borderChange.getNewBorder()) {
                if (discoveredLand.contains(point)) {
                    added.add(point);
                }
            }

            for (Point point : borderChange.getRemovedBorder()) {
                if (discoveredLand.contains(point)) {
                    removed.add(point);
                }
            }

            if (added.isEmpty() && removed.isEmpty()) {
                continue;
            }

            BorderChange borderChangeToAdd = new BorderChange(borderChange.getPlayer(), added, removed);

            changedBorders.add(borderChangeToAdd);
        }
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
            if (!building.isStorehouse()) {
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

    public boolean canAttack(Building building) throws InvalidUserActionException {

        /* Can only attack military buildings */
        if (!building.isMilitaryBuilding()) {
            return false;
        }

        /* Can not attack itself */
        if (equals(building.getPlayer())) {
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

    public void reportWinner(Player winner) {
        messages.add(new GameEndedMessage(winner));
    }

    public void reportHitByCatapult(Catapult catapult, Building hitBuilding) {
        messages.add(new BombardedByCatapultMessage(catapult, hitBuilding));
    }

    public void setProductionQuotaForTool(Material tool, int quota) throws InvalidUserActionException {

        if (quota > MAX_PRODUCTION_QUOTA) {
            throw new InvalidUserActionException("Cannot set quota " + quota + " above max quota at " + MAX_PRODUCTION_QUOTA);
        }

        if (quota < MIN_PRODUCTION_QUOTA) {
            throw new InvalidUserActionException("Cannot set quota " + quota + " below min quota at " + MIN_PRODUCTION_QUOTA);
        }

        if (!Material.isTool(tool)) {
            throw new InvalidUserActionException("Cannot set quota for material that is not a tool: " + tool);
        }

        toolProductionQuotas.put(tool, quota);
    }

    public int getProductionQuotaForTool(Material tool) {
        return toolProductionQuotas.getOrDefault(tool, 0);
    }

    public void reportChangedFlag(Flag flag) {
        changedFlags.add(flag);
    }

    public void reportRemovedDeadTree(Point point) {
        removedDeadTrees.add(point);
    }


    public void reportShipReadyForExpedition(Ship ship) {
        ShipReadyForExpeditionMessage message = new ShipReadyForExpeditionMessage(ship);

        messages.add(message);
    }

    public void reportShipReachedDestination(Ship ship) {
        ShipHasReachedDestinationMessage message = new ShipHasReachedDestinationMessage(ship, ship.getPosition());

        messages.add(message);
    }

    public void reportHarborReady(Harbor harbor) {
        HarborIsFinishedMessage message = new HarborIsFinishedMessage(harbor);

        messages.add(message);
    }

    public void reportHarvestedCrop(Crop crop) {
        harvestedCrops.add(crop);
    }

    public void reportNewShip(Ship ship) {
        newShips.add(ship);
    }

    public void reportFinishedShip(Ship ship) {
        finishedShips.add(ship);
    }

    public void reportShipWithNewTarget(Ship ship) {
        shipsWithNewTargets.add(ship);
    }

    public void reportWorkerStartedAction(Worker worker, WorkerAction action) {
        workersWithStartedActions.put(worker, action);
    }

    public void reportRemovedDecoration(Point point) {
        removedDecorations.add(point);
    }

    public Nation getNation() {
        return nation;
    }

    public void setNation(Nation nation) {
        this.nation = nation;
    }

    public Optional<Building> getHeadquarter() {
        return buildings.stream().filter(Building::isHeadquarter).findFirst();
    }

    public void addDetailedMonitoring(Building building) {
        detailedMonitoring.add(building);
    }

    public void removeDetailedMonitoring(Building building) {
        detailedMonitoring.remove(building);
    }

    public void removeDetailedMonitoring(Flag flag) {
        detailedMonitoring.remove(flag);
    }

    public void reportChangedInventory(Building building) {
        if (detailedMonitoring.contains(building)) {
            changedBuildings.add(building);
        }
    }

    public void reportSoldierEnteredBuilding(Building building) {
        if (detailedMonitoring.contains(building)) {
            changedBuildings.add(building);
        }

        /* Does this soldier affect the number of available attackers in another building? */
        if (building.isMilitaryBuilding() && building.getHostedMilitary().size() > 1) {

            for (Object monitoredObject : detailedMonitoring) {
                if (monitoredObject instanceof Building monitoredBuilding) {
                    if (!monitoredBuilding.isMilitaryBuilding()) {
                        continue;
                    }

                    if (!monitoredBuilding.isOccupied()) {
                        continue;
                    }

                    if (monitoredBuilding.getPlayer().equals(this)) {
                        continue;
                    }

                    if (building.canAttack(monitoredBuilding)) {
                        changedBuildings.add(monitoredBuilding);
                    }
                }
            }
        }
    }

    public void reportBuildingEvacuated(Building building) {
        if (detailedMonitoring.contains(building)) {
            changedBuildings.add(building);
        }
    }

    public void reportBuildingEvacuationCanceled(Building building) {
        if (detailedMonitoring.contains(building)) {
            changedBuildings.add(building);
        }
    }

    public void reportDisabledPromotions(Building building) {
        if (detailedMonitoring.contains(building)) {
            changedBuildings.add(building);
        }
    }

    public void reportEnabledPromotions(Building building) {
        if (detailedMonitoring.contains(building)) {
            changedBuildings.add(building);
        }
    }

    public void reportProductionResumed(Building building) {
        if (detailedMonitoring.contains(building)) {
            changedBuildings.add(building);
        }
    }

    public void reportProductionStopped(Building building) {
        if (detailedMonitoring.contains(building)) {
            changedBuildings.add(building);
        }
    }

    public void reportUpgradeStarted(Building building) {
        if (detailedMonitoring.contains(building)) {
            changedBuildings.add(building);
        }
    }

    public void reportChangedReserveAmount(Building building) {
        if (detailedMonitoring.contains(building)) {
            changedBuildings.add(building);
        }
    }

    public void reportSoldierLeftBuilding(Building building) {

        /* Does this soldier affect the number of available attackers in another building? */
        if (building.isMilitaryBuilding() && !building.getHostedMilitary().isEmpty()) {

            for (Object monitoredObject : detailedMonitoring) {
                if (monitoredObject instanceof Building monitoredBuilding) {
                    if (!monitoredBuilding.isMilitaryBuilding()) {
                        continue;
                    }

                    if (!monitoredBuilding.isOccupied()) {
                        continue;
                    }

                    if (monitoredBuilding.getPlayer().equals(this)) {
                        continue;
                    }

                    if (building.canAttack(monitoredBuilding)) {
                        changedBuildings.add(monitoredBuilding);
                    }
                }
            }
        }
    }

    public void reportBuildingTornDown(Building building) {

        /* Does this soldier affect the number of available attackers in another building? */
        if (building.isMilitaryBuilding() && !building.getHostedMilitary().isEmpty()) {

            for (Object monitoredObject : detailedMonitoring) {
                if (monitoredObject instanceof Building monitoredBuilding) {
                    if (!monitoredBuilding.isMilitaryBuilding()) {
                        continue;
                    }

                    if (!monitoredBuilding.isOccupied()) {
                        continue;
                    }

                    if (monitoredBuilding.getPlayer().equals(this)) {
                        continue;
                    }

                    if (building.canAttack(monitoredBuilding)) {
                        changedBuildings.add(monitoredBuilding);
                    }
                }
            }
        }
    }

    public void reportUpgradedBuilding(Building fromBuilding, Building upgraded) {
        upgradedBuildings.add(new GameChangesList.NewAndOldBuilding(fromBuilding, upgraded));
    }

    public void removeMessage(Message gameMessage) {
        messages.remove(gameMessage);

        removedMessages.add(gameMessage);
    }

    public void addDetailedMonitoring(Flag flag) {
        detailedMonitoring.add(flag);
    }

    public void setWheatQuota(Class<? extends Building> aClass, int amount) {
        wheatAllocation.put(aClass, amount);
    }

    public int getWheatQuota(Class<? extends Building> aClass) {
        return wheatAllocation.get(aClass);
    }

    public void setWaterQuota(Class<? extends Building> aClass, int amount) {
        waterAllocation.put(aClass, amount);
    }

    public int getWaterQuota(Class<? extends Building> aClass) {
        return waterAllocation.get(aClass);
    }

    public void setIronBarQuota(Class<? extends Building> buildingClass, int amount) {
        ironBarAllocation.put(buildingClass, amount);
    }

    public int getIronBarQuota(Class<? extends Building> buildingClass) {
        return ironBarAllocation.get(buildingClass);
    }
}
