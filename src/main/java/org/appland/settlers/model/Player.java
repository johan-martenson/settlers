package org.appland.settlers.model;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.actors.Ship;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Brewery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Catapult;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.buildings.DonkeyFarm;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.GraniteMine;
import org.appland.settlers.model.buildings.Harbor;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.IronMine;
import org.appland.settlers.model.buildings.IronSmelter;
import org.appland.settlers.model.buildings.Metalworks;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.buildings.PigFarm;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.messages.BombardedByCatapultMessage;
import org.appland.settlers.model.messages.BuildingCapturedMessage;
import org.appland.settlers.model.messages.BuildingLostMessage;
import org.appland.settlers.model.messages.GameEndedMessage;
import org.appland.settlers.model.messages.GeologistFindMessage;
import org.appland.settlers.model.messages.HarborIsFinishedMessage;
import org.appland.settlers.model.messages.Message;
import org.appland.settlers.model.messages.MilitaryBuildingCausedLostLandMessage;
import org.appland.settlers.model.messages.MilitaryBuildingOccupiedMessage;
import org.appland.settlers.model.messages.MilitaryBuildingReadyMessage;
import org.appland.settlers.model.messages.NoMoreResourcesMessage;
import org.appland.settlers.model.messages.ShipHasReachedDestinationMessage;
import org.appland.settlers.model.messages.ShipReadyForExpeditionMessage;
import org.appland.settlers.model.messages.StoreHouseIsReadyMessage;
import org.appland.settlers.model.messages.TreeConservationProgramActivatedMessage;
import org.appland.settlers.model.messages.TreeConservationProgramDeactivatedMessage;
import org.appland.settlers.model.messages.UnderAttackMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.entry;
import static org.appland.settlers.model.Material.*;

/**
 * @author johan
 *
 */
public class Player {
    private static final int PLANKS_THRESHOLD_FOR_TREE_CONSERVATION_PROGRAM = 10;
    private static final int MAX_PRODUCTION_QUOTA = 10;
    private static final int MIN_PRODUCTION_QUOTA = 0;

    private PlayerType  playerType;
    private GameMap     map;
    private PlayerColor color;
    private Nation      nation;
    private String      name;
    private boolean     treeConservationProgramActive;
    private boolean     treeConservationProgramEnabled;
    private int         strengthWhenPopulatingMilitaryBuildings;
    private int         defenseStrength;
    private int         defenseFromSurroundingBuildings;
    private int         amountWhenPopulatingCloseToBorder;
    private int         amountWhenPopulatingAwayFromToBorder;
    private int         amountWhenPopulatingFarFromBorder;
    private int         amountSoldiersAvailableForAttack;
    private boolean     transportPriorityChanged = false;

    private final List<BorderChange> changedBorders = new ArrayList<>();
    private final List<Building> buildings = new ArrayList<>();
    private final Set<Point> discoveredLand = new HashSet<>();
    private final List<Material> transportPriorities = new ArrayList<>();
    private final Set<Point> ownedLand = new HashSet<>();
    private final Map<Class<? extends Building>, Integer> foodAllocation = new HashMap<>();
    private final Map<Class<? extends Building>, Integer> coalAllocation = new HashMap<>();
    private final Map<Material, Integer> producedMaterials = new EnumMap<>(Material.class);
    private final List<Message> messages = new ArrayList<>();
    private final Set<PlayerGameViewMonitor> gameViewMonitors = new HashSet<>();
    private final List<Worker> workersWithNewTargets = new ArrayList<>();
    private final Set<Building> changedBuildings = new HashSet<>();
    private final List<Flag> newFlags = new ArrayList<>();
    private final List<Flag> removedFlags = new ArrayList<>();
    private final List<Building> newBuildings = new ArrayList<>();
    private final List<Building> removedBuildings = new ArrayList<>();
    private final List<Road> removedRoads = new ArrayList<>();
    private final List<Road> newRoads = new ArrayList<>();
    private final List<Worker> removedWorkers = new ArrayList<>();
    private final List<Tree> newTrees = new ArrayList<>();
    private final List<Tree> removedTrees = new ArrayList<>();
    private final List<Stone> removedStones = new ArrayList<>();
    private final List<Sign> newSigns = new ArrayList<>();
    private final List<Sign> removedSigns = new ArrayList<>();
    private final List<Crop> newCrops = new ArrayList<>();
    private final List<Crop> removedCrops = new ArrayList<>();
    private final Set<Point> newDiscoveredLand = new HashSet<>();
    private final List<Point> addedBorder = new ArrayList<>();
    private final List<Point> removedBorder = new ArrayList<>();
    private final List<Stone> newStones = new ArrayList<>();
    private final Set<Point> borderPoints = new HashSet<>();
    private final List<Worker> newWorkers = new ArrayList<>();
    private final Set<Point> changedAvailableConstruction = new HashSet<>();
    private final List<Point> newOwnedLand = new ArrayList<>();
    private final List<Point> newLostLand = new ArrayList<>();
    private final List<Message> newMessages = new ArrayList<>();
    private final List<Road> promotedRoads = new ArrayList<>();
    private final Map<Material, Integer> toolProductionQuotas = new EnumMap<>(Material.class);
    private final List<TransportCategory> transportCategoryPriorities = new ArrayList<>();
    private final Collection<Flag> changedFlags = new HashSet<>();
    private final List<Point> removedDeadTrees = new ArrayList<>();
    private final List<Point> discoveredDeadTrees = new ArrayList<>();
    private final List<Crop> harvestedCrops = new ArrayList<>();
    private final List<Ship> newShips = new ArrayList<>();
    private final List<Ship> finishedShips = new ArrayList<>();
    private final List<Ship> shipsWithNewTargets = new ArrayList<>();
    private final Map<Worker, WorkerAction> workersWithStartedActions = new HashMap<>();
    private final List<Point> removedDecorations = new ArrayList<>();
    private final Map<Point, DecorationType> newDecorations = new HashMap<>();
    private final Set<Object> detailedMonitoring = new HashSet<>();
    private final Collection<GameChangesList.NewAndOldBuilding> upgradedBuildings = new ArrayList<>();
    private final Set<Message> removedMessages = new HashSet<>();
    private final Map<Class<? extends Building>, Integer> wheatAllocation = new HashMap<>();
    private final Map<Class<? extends Building>, Integer> waterAllocation = new HashMap<>();
    private final Map<Class<? extends Building>, Integer> ironBarAllocation = new HashMap<>();
    private final Set<Stone> changedStones = new HashSet<>();
    private final Set<Tree> newFallingTrees = new HashSet<>();
    private final Collection<PlayerChangeListener> playerChangeListeners = new HashSet<>();
    private final Set<Message> readMessages = new HashSet<>();

    public Player(String name, PlayerColor color, Nation nation, PlayerType playerType) {
        this.name = name;
        this.color = color;
        this.nation = nation;
        this.playerType = playerType;

        /* Create the food quota and set it to equal distribution */
        foodAllocation.putAll(Map.ofEntries(
                entry(GoldMine.class, 1),
                entry(IronMine.class, 1),
                entry(CoalMine.class, 1),
                entry(GraniteMine.class, 1)
        ));

        /* Create the coal quota and set it to equal distribution */
        coalAllocation.putAll(Map.ofEntries(
                entry(IronSmelter.class, 1),
                entry(Mint.class, 1),
                entry(Armory.class, 1)
        ));

        /* Create the wheat quota and set it to equal distribution */
        wheatAllocation.putAll(Map.ofEntries(
                entry(Mill.class, 1),
                entry(DonkeyFarm.class, 1),
                entry(PigFarm.class, 1),
                entry(Brewery.class, 1)
        ));

        /* Create the water quota and set it to equal distribution */
        waterAllocation.putAll(Map.ofEntries(
                entry(Bakery.class, 1),
                entry(DonkeyFarm.class, 1),
                entry(PigFarm.class, 1),
                entry(Brewery.class, 1)
        ));

        /* Create the iron bar quota and set it to equal distribution */
        ironBarAllocation.put(Armory.class, 1);
        ironBarAllocation.put(Metalworks.class, 1);

        /* Set the initial transport priority */
        transportCategoryPriorities.addAll(Arrays.asList(TransportCategory.values()));

        setTransportPriorityForMaterials();

        /* The tree conservation program is not active at start */
        treeConservationProgramActive = false;

        /* The tree conservation program is enabled by default */
        treeConservationProgramEnabled = true;

        /* Set default military settings */
        strengthWhenPopulatingMilitaryBuildings = 5;
        defenseStrength = 5;
        defenseFromSurroundingBuildings = 5;
        amountWhenPopulatingCloseToBorder = 10;
        amountWhenPopulatingAwayFromToBorder = 10;
        amountWhenPopulatingFarFromBorder = 10;
        amountSoldiersAvailableForAttack = 10;

        /* Set default production of all tools */
        TOOLS.forEach(tool -> toolProductionQuotas.put(tool, MAX_PRODUCTION_QUOTA));
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

    public void removeBuilding(Building building) {
        buildings.remove(building);
    }

    public void addBuilding(Building house) {
        buildings.add(house);
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public boolean isWithinBorder(Point point) {
        return ownedLand.contains(point);
    }

    public Set<Point> getOwnedLand() {
        return ownedLand;
    }

    public Set<Point> getDiscoveredLand() {
        return discoveredLand;
    }

    public void discover(Point point) {
        if (!discoveredLand.contains(point)) {
            discoveredLand.add(point);

            /* Report that this is a newly discovered point */
            if (hasMonitor()) {
                newDiscoveredLand.add(point);
            }
        }
    }

    public int getAvailableAttackersForBuilding(Building buildingToAttack) throws InvalidUserActionException {
        if (!buildingToAttack.isMilitaryBuilding()) {
            throw new InvalidUserActionException("Cannot get available attackers for non-military building");
        }

        if (equals(buildingToAttack.getPlayer())) {
            throw new InvalidUserActionException("Cannot get available attackers for own building");
        }

        /* Count soldiers in military buildings that can reach the building */
        return (int) (getBuildings().stream()
                .filter(Building::isMilitaryBuilding)
                .filter(building -> building.canAttack(buildingToAttack))
                .mapToInt(building -> building instanceof Headquarter headquarter
                        ? headquarter.getAmount(PRIVATE) + headquarter.getAmount(PRIVATE_FIRST_CLASS)
                        + headquarter.getAmount(SERGEANT) + headquarter.getAmount(OFFICER) + headquarter.getAmount(GENERAL)
                        : Math.max(building.getNumberOfHostedSoldiers() - 1, 0))
                .sum() * (amountSoldiersAvailableForAttack / 10.0));
    }

    public void attack(Building buildingToAttack, int nrAttackers, AttackStrength strength) throws InvalidUserActionException {

        // Check that the attack is allowed
        if (!buildingToAttack.isMilitaryBuilding()) {
            throw new InvalidUserActionException("Cannot attack non-military building %s".formatted(buildingToAttack));
        }

        if (buildingToAttack.getPlayer().equals(this)) {
            throw new InvalidUserActionException("Can only attack other players");
        }

        // Find buildings that can support the attack
        List<Building> eligibleBuildings = getBuildings().stream()
                .filter(building -> building.isMilitaryBuilding()
                        && building.canAttack(buildingToAttack)
                        && building.getNumberOfHostedSoldiers() >= 2)
                .toList();

        // Find soldiers that can do the attack
        List<Soldier> availableAttackers = new ArrayList<>();

        for (Building building : eligibleBuildings) {
            if (building.getNumberOfHostedSoldiers() < 2) {
                continue;
            }

            var availableAttackersFromBuilding = new ArrayList<Soldier>();

            if (building instanceof Headquarter headquarter) {
                var attackStrengthInt = switch (strength) {
                    case STRONG -> 10;
                    case WEAK -> 0;
                };

                GameUtils.strengthToRank(attackStrengthInt).forEach(rank -> {
                    for (int i = 0; i < headquarter.getAmount(rank.toMaterial()); i++) {
                        Soldier attacker = new Soldier(this, rank, map);

                        attacker.setHome(headquarter);
                        attacker.setPosition(headquarter.getPosition());

                        availableAttackersFromBuilding.add(attacker);
                    }
                });
            } else {
                availableAttackersFromBuilding.addAll(building.getHostedSoldiers());
                availableAttackersFromBuilding.sort(GameUtils.strengthSorter);
            }

            if (availableAttackersFromBuilding.isEmpty()) {
                continue;
            }

            /* Leave one soldier so it can guard the military building */
            if (strength == AttackStrength.STRONG) {
                availableAttackersFromBuilding.removeFirst();
            } else {
                availableAttackersFromBuilding.removeLast();
            }

            availableAttackers.addAll(availableAttackersFromBuilding);
        }

        var limitedAttackers = availableAttackers.subList(0, (int)((amountSoldiersAvailableForAttack / 10.0) * availableAttackers.size()));

        /* It's not possible to attack if there are no available attackers */
        if (limitedAttackers.isEmpty()) {
            throw new InvalidUserActionException("Player '%s' can't attack building '%s'".formatted(this, buildingToAttack));
        }

        /* Sort primarily by strength and secondarily by distance */
        List<GameUtils.SoldierAndDistance> availableAttackersWithDistance = new ArrayList<>(limitedAttackers
                .stream()
                .map(soldier -> new GameUtils.SoldierAndDistance(
                        soldier,
                        GameUtils.distanceInGameSteps(soldier.getPosition(), buildingToAttack.getPosition())))
                .toList());

        if (strength == AttackStrength.STRONG) {
            availableAttackersWithDistance.sort(GameUtils.strongerAndShorterDistanceSorter);
        } else {
            availableAttackersWithDistance.sort(GameUtils.weakerAndShorterDistanceSorter);
        }

        /* Run the attack with the most suitable soldiers */
        availableAttackersWithDistance.stream().limit(nrAttackers).forEach(soldierAndDistance -> {
            var soldier = soldierAndDistance.soldier();

            soldier.getHome().retrieveHostedSoldier(soldier);

            soldier.attack(buildingToAttack);
        });

        buildingToAttack.getPlayer().reportUnderAttack(buildingToAttack);
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    void setLands(List<Land> updatedLands, Building building, BorderChangeCause cause) {

        /* Remember how the owned land & border was before the update */
        Set<Point> oldOwnedLand = new HashSet<>(ownedLand);
        Set<Point> oldBorder = new HashSet<>(borderPoints);

        /* Figure out the new land & border */
        Set<Point> newBorder = new HashSet<>();
        Set<Point> newOwnedLand = new HashSet<>();

        for (Land land : updatedLands) {
            land.getBorders().forEach(newBorder::addAll);
            newOwnedLand.addAll(land.getPointsInLand());
        }

        /* Figure out the border & land that has been added & removed */
        Set<Point> addedBorder = new HashSet<>(newBorder);
        Set<Point> addedOwnedLand = new HashSet<>(newOwnedLand);
        Set<Point> removedBorder = new HashSet<>(oldBorder);
        Set<Point> removedOwnedLand = new HashSet<>(oldOwnedLand);

        addedBorder.removeAll(oldBorder);
        addedOwnedLand.removeAll(oldOwnedLand);

        removedBorder.removeAll(newBorder);
        removedOwnedLand.removeAll(newOwnedLand);

        this.addedBorder.clear();
        this.removedBorder.clear();

        this.addedBorder.addAll(addedBorder);
        this.removedBorder.addAll(removedBorder);

        /* Update full list of owned land and the list of borders */
        this.borderPoints.clear();
        this.ownedLand.clear();

        this.borderPoints.addAll(newBorder);
        this.ownedLand.addAll(newOwnedLand);

        if (!addedOwnedLand.isEmpty()) {
            var previousDiscoveredLand = new HashSet<>(discoveredLand);

            /* Update field of view */
            buildings.stream()
                    .filter(b -> b.isMilitaryBuilding() && b.isOccupied())
                    .flatMap(b -> b.getDiscoveredLand().stream())
                    .forEach(discoveredLand::add);

            // Notify monitors of new discoveries
            if (hasMonitor()) {
                newDiscoveredLand.addAll(discoveredLand);
                newDiscoveredLand.removeAll(previousDiscoveredLand);
            }
        }

        /* Report lost land */
        if (!removedOwnedLand.isEmpty() && cause == BorderChangeCause.MILITARY_BUILDING_OCCUPIED) {
            reportThisBuildingHasCausedLostLand(building);
        }

        /* Monitor the added/lost land */
        if (hasMonitor()) {
            this.newOwnedLand.clear();
            this.newOwnedLand.addAll(addedOwnedLand);

            this.newLostLand.clear();
            this.newLostLand.addAll(removedOwnedLand);
        }
    }

    private void reportThisBuildingHasCausedLostLand(Building building) {
        messages.add(new MilitaryBuildingCausedLostLandMessage(building));
    }

    @Override
    public String toString() {
        return name;
    }

    public PlayerColor getColor() {
        return color;
    }

    public Storehouse getClosestStorage(Point position, Building avoid) {
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

        buildings.stream()
                .filter(building -> !building.isStorehouse())
                .forEach(building -> Arrays.stream(values())
                        .forEach(material -> result.put(
                                material,
                                result.getOrDefault(material, 0) + building.getAmount(material))));

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
            throw new InvalidUserActionException("Cannot set a negative transport priority (%d) for %s".formatted(priority, category));
        } else if (priority >= TransportCategory.values().length) {
            throw new InvalidUserActionException("Cannot set a higher transport priority (%d) than the amount of transportable items".formatted(priority));
        }

        transportCategoryPriorities.remove(category);
        transportCategoryPriorities.add(priority, category);

        transportPriorities.clear();

        setTransportPriorityForMaterials();

        transportPriorityChanged = true;
    }

    public int getTransportPriority(Cargo cargo) {
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

    public List<Material> getTransportPrioritiesForEachMaterial() {
        return transportPriorities;
    }

    public Player getPlayerAtPoint(Point point) {

        /* Don't allow lookup of points the player hasn't discovered yet */
        if (!discoveredLand.contains(point)) {
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

    public Storehouse getClosestStorageOffroad(Point position) {
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
        return buildings.stream().anyMatch(Building::isReady);
    }

    public void setColor(PlayerColor color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;

        playerChangeListeners.forEach(PlayerChangeListener::onPlayerChanged);
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

    public void reportMilitaryBuildingReady(Building building) {
        MilitaryBuildingReadyMessage message = new MilitaryBuildingReadyMessage(building);

        messages.add(message);

        if (hasMonitor()) {
            newMessages.add(message);
        }
    }

    public void reportMilitaryBuildingOccupied(Building building) {
        MilitaryBuildingOccupiedMessage message = new MilitaryBuildingOccupiedMessage(building);

        messages.add(message);

        if (hasMonitor()) {
            newMessages.add(message);
        }
    }

    public void reportNoMoreResourcesForBuilding(Building building) {
        NoMoreResourcesMessage message = new NoMoreResourcesMessage(building);

        messages.add(message);

        if (hasMonitor()) {
            newMessages.add(message);
        }
    }

    public void reportGeologicalFinding(Point point, Material foundMaterial) {
        GeologistFindMessage message = new GeologistFindMessage(point, foundMaterial);

        messages.add(message);

        if (hasMonitor()) {
            newMessages.add(message);
        }
    }

    public void reportBuildingLost(Building building) {
        BuildingLostMessage message = new BuildingLostMessage(building);

        messages.add(message);

        if (hasMonitor()) {
            newMessages.add(message);
        }
    }

    public void reportBuildingCaptured(Building building) {
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

    public void reportStorageReady(Storehouse storehouse) {
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
        addedBorder.clear();
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
        changedStones.clear();
        newFallingTrees.clear();
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
        // Missing discoveredDeadTrees, newWorkers
        if (GameUtils.allCollectionsEmpty(newFlags, removedFlags, newBuildings, newRoads, removedRoads, removedWorkers,
                changedBuildings, removedBuildings, newTrees, removedTrees, removedStones, newSigns,
                removedSigns, newCrops, removedCrops, newDiscoveredLand, addedBorder, removedBorder,
                workersWithNewTargets, changedBorders, newStones, newMessages, promotedRoads, changedFlags,
                removedDeadTrees, harvestedCrops, newShips,
                finishedShips, shipsWithNewTargets,
                removedDecorations, upgradedBuildings, changedAvailableConstruction) &&
            GameUtils.allMapsEmpty(workersWithStartedActions, newDecorations) &&
            !transportPriorityChanged) {
            return;
        }

        /* Don't send reports about changes in the old building when an upgrade is finished */
        upgradedBuildings.forEach(newAndOldBuilding -> {
            changedBuildings.remove(newAndOldBuilding.oldBuilding);
            changedBuildings.remove(newAndOldBuilding.newBuilding);
        });

        /* If the player has discovered new land - find out what is on that land */
        if (!newDiscoveredLand.isEmpty()) {

            // Find out what's on the newly discovered land
            newDiscoveredLand.forEach(point -> {
                MapPoint mapPoint = map.getMapPoint(point);

                // Collect information based on mapPoint properties
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
            });

            // Find discovered borders for other players
            map.getPlayers().stream()
                    .filter(player -> !player.equals(this))
                    .forEach(player -> {
                        Set<Point> borderForPlayer = player.getBorderPoints();
                        List<Point> discoveredBorder = newDiscoveredLand.stream()
                                .filter(borderForPlayer::contains)
                                .collect(Collectors.toList());

                        if (!discoveredBorder.isEmpty()) {
                            changedBorders.add(new BorderChange(player, discoveredBorder, new ArrayList<>()));
                        }
                    });

            // Find discovered workers
            map.getWorkers().stream()
                    .filter(worker -> !worker.getPlayer().equals(this))
                    .forEach(worker -> newDiscoveredLand.stream()
                            .filter(point -> worker.getPosition().equals(point))
                            .forEach(point -> newWorkers.add(worker)));
        }

        // Handle changes in available construction

        // New and removed flags change what construction is possible
        Stream.of(newFlags, removedFlags)
                .flatMap(Collection::stream)
                .forEach(this::addChangedAvailableConstructionForFlag);

        // New and removed trees change what construction is possible
        Stream.of(newTrees, removedTrees)
                .flatMap(Collection::stream)
                .forEach(this::addChangedAvailableConstructionForTree);

        // A building that's destroyed frees up new space for available construction
        changedBuildings.stream()
                .filter(Building::isDestroyed)
                .forEach(this::addChangedAvailableConstructionForSmallBuilding);

        // Handle new and removed buildings together - both cause a change to available construction for the same points
        Stream.of(newBuildings, removedBuildings)
                .flatMap(Collection::stream)
                .forEach(building -> {
                    addChangedAvailableConstructionForSmallBuilding(building);

                    if (building.getSize() == Size.MEDIUM) {
                        addChangedAvailableConstructionForMediumBuilding(building);
                    }

                    if (building.getSize() == Size.LARGE) {
                        addChangedAvailableConstructionForLargeBuilding(building);
                    }
                });

        // Update available construction for new roads
        newRoads.stream()
                .flatMap(road -> road.getWayPoints().stream())
                .forEach(changedAvailableConstruction::add);

        // Update available construction for removed roads
        // TODO: also consider points up-left. Previously they couldn't have houses because of the flag being blocked
        removedRoads.stream()
                .flatMap(road -> road.getWayPoints().stream())
                .forEach(changedAvailableConstruction::add);

        // Update available construction for new and removed crops
        // TODO: Check that it's really only the point of the crop that is affected
        // TODO: Check that there is no change in available construction between different crop states, e.g.
        //       place flag on expired crop
        Stream.of(newCrops, removedCrops)
                .flatMap(Collection::stream)
                .forEach(this::addChangedAvailableConstructionForCrop);

        // Update available construction when stones have been removed
        removedStones.forEach(this::addChangedAvailableConstructionForStone);

        /* Add changed available construction if the border has been extended */
        if (!addedBorder.isEmpty()) {

            // Report changed available construction because of the border change
            changedAvailableConstruction.addAll(newOwnedLand);
            changedAvailableConstruction.addAll(newLostLand);

            // It's now possible to build on point up-left from previous border points as the flag now has space.
            removedBorder.stream()
                    .map(Point::upLeft)
                    .filter(pointUpLeft -> !newOwnedLand.contains(pointUpLeft))
                    .forEach(changedAvailableConstruction::add);
        }

        /* Create the event message */
        GameChangesList gameChangesToReport = new GameChangesList(time,
                workersWithNewTargets,
                newFlags,
                removedFlags,
                newBuildings,
                changedBuildings,
                removedBuildings,
                newRoads,
                removedRoads,
                removedWorkers,
                newTrees,
                removedTrees,
                removedStones,
                newSigns,
                removedSigns,
                newCrops,
                removedCrops,
                newDiscoveredLand,
                changedBorders,
                newStones,
                newWorkers,
                changedAvailableConstruction,
                newMessages,
                promotedRoads,
                changedFlags,
                removedDeadTrees,
                discoveredDeadTrees,
                harvestedCrops,
                newShips,
                finishedShips,
                shipsWithNewTargets,
                workersWithStartedActions,
                removedDecorations,
                newDecorations,
                upgradedBuildings,
                removedMessages,
                changedStones,
                newFallingTrees,
                transportPriorityChanged,
                readMessages);

        /* Send the event to all monitors */
        gameViewMonitors.forEach(monitor -> monitor.onViewChangesForPlayer(this, gameChangesToReport));

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
        addedBorder.clear();
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
        changedStones.clear();
        newFallingTrees.clear();
        readMessages.clear();
        transportPriorityChanged = false;
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
        if (addedBorder.isEmpty() && removedBorder.isEmpty()) {
            return null;
        }

        return new BorderChange(this, addedBorder, removedBorder);
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
            throw new InvalidUserActionException("Cannot set quota %d above max quota at %d".formatted(quota, MAX_PRODUCTION_QUOTA));
        }

        if (quota < MIN_PRODUCTION_QUOTA) {
            throw new InvalidUserActionException("Cannot set quota %d below min quota at %d".formatted(quota, MIN_PRODUCTION_QUOTA));
        }

        if (!isTool(tool)) {
            throw new InvalidUserActionException("Cannot set quota for material that is not a tool: %s".formatted(tool));
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

        playerChangeListeners.forEach(PlayerChangeListener::onPlayerChanged);
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
        if (building.isMilitaryBuilding() && building.getHostedSoldiers().size() > 1) {

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
        if (building.isMilitaryBuilding() && !building.getHostedSoldiers().isEmpty()) {
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
        if (building.isMilitaryBuilding() && !building.getHostedSoldiers().isEmpty()) {
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

    public void setStrengthOfSoldiersPopulatingBuildings(int strength) throws InvalidUserActionException {
        if (strength < 0 || strength > 10) {
            throw new InvalidUserActionException("Can't set strength of soldiers when populating buildings to: " + strength);
        }

        strengthWhenPopulatingMilitaryBuildings = strength;
    }

    public int getStrengthOfSoldiersPopulatingBuildings() {
        return strengthWhenPopulatingMilitaryBuildings;
    }

    public int getDefenseStrength() {
        return defenseStrength;
    }

    public void setDefenseStrength(int defenseStrength) throws InvalidUserActionException {
        if (defenseStrength < 0 || defenseStrength > 10) {
            throw new InvalidUserActionException("Can't set the defense strength to: " + defenseStrength);
        }

        this.defenseStrength = defenseStrength;
    }

    public void setDefenseFromSurroundingBuildings(int strength) throws InvalidUserActionException {
        if (strength < 0 || strength > 10) {
            throw new InvalidUserActionException("Can't set strength of defense from surrounding buildings to: " + strength);
        }

        defenseFromSurroundingBuildings = strength;
    }

    public int getDefenseFromSurroundingBuildings() {
        return defenseFromSurroundingBuildings;
    }

    public void reportChangedStone(Stone stone) {
        changedStones.add(stone);
    }
    public int getAmountOfSoldiersWhenPopulatingCloseToBorder() {
        return amountWhenPopulatingCloseToBorder;
    }

    public void setAmountOfSoldiersWhenPopulatingCloseToBorder(int amount) throws InvalidUserActionException {
        if (amount < 0 || amount > 10) {
            throw new InvalidUserActionException("Can't set amount of soldiers when populating close to border to: " + amount);
        }

        amountWhenPopulatingCloseToBorder = amount;
    }

    public int getAmountOfSoldiersWhenPopulatingAwayFromBorder() {
        return amountWhenPopulatingAwayFromToBorder;
    }

    public int getAmountOfSoldiersWhenPopulatingFarFromBorder() {
        return amountWhenPopulatingFarFromBorder;
    }

    public void setAmountOfSoldiersWhenPopulatingAwayFromBorder(int amount) throws InvalidUserActionException {
        if (amount < 0 || amount > 10) {
            throw new InvalidUserActionException("Can't set amount of soldiers when populating closer to border to: " + amount);
        }

        amountWhenPopulatingAwayFromToBorder = amount;
    }

    public void setAmountOfSoldiersWhenPopulatingFarFromBorder(int amount) throws InvalidUserActionException {
        if (amount < 0 || amount > 10) {
            throw new InvalidUserActionException("Can't set amount of soldiers when populating far from border to: " + amount);
        }

        amountWhenPopulatingFarFromBorder = amount;
    }

    public int getAmountOfSoldiersAvailableForAttack() {
        return amountSoldiersAvailableForAttack;
    }

    public void setAmountOfSoldiersAvailableForAttack(int amount) throws InvalidUserActionException {
        if (amount < 0 || amount > 10) {
            throw new InvalidUserActionException("Can't set amount of soldiers to include in attacks to: " + amount);
        }

        amountSoldiersAvailableForAttack = amount;
    }

    public void reportNewDecoration(Point point, DecorationType decoration) {
        newDecorations.put(point, decoration);
    }

    public void reportNewFallingTree(Tree tree) {
        newFallingTrees.add(tree);
    }

    public void setPlayerColor(PlayerColor playerColor) {
        color = playerColor;

        playerChangeListeners.forEach(PlayerChangeListener::onPlayerChanged);
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public void addPlayerChangeListener(PlayerChangeListener playerChangeListener) {
        playerChangeListeners.add(playerChangeListener);
    }

    public void removePlayerChangeListener(PlayerChangeListener playerChangeListener) {
        playerChangeListeners.remove(playerChangeListener);
    }

    public void markMessageAsRead(Message message) {
        if (!message.isRead()) {
            readMessages.add(message);
        }

        message.isRead = true;
    }
}
