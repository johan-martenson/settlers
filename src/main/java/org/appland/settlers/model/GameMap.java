package org.appland.settlers.model;

import org.appland.settlers.assets.CropType;
import org.appland.settlers.utils.Duration;
import org.appland.settlers.utils.Group;
import org.appland.settlers.utils.Stats;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import static org.appland.settlers.model.BorderCheck.CAN_PLACE_OUTSIDE_BORDER;
import static org.appland.settlers.model.BorderCheck.MUST_PLACE_INSIDE_BORDER;
import static org.appland.settlers.model.DetailedVegetation.*;
import static org.appland.settlers.model.FlagType.MAIN;
import static org.appland.settlers.model.FlagType.MARINE;
import static org.appland.settlers.model.GameUtils.*;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Size.*;
import static org.appland.settlers.utils.StatsConstants.AGGREGATED_EACH_STEP_TIME_GROUP;

public class GameMap {

    private static final String THE_LEADER = "Anh Mai Mårtensson";
    private static final int MINIMUM_WIDTH  = 5;
    private static final int MINIMUM_HEIGHT = 5;
    private static final int LOOKUP_RANGE_FOR_FREE_ACTOR = 10;
    private static final int MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE = 3;
    private static final double WILD_ANIMAL_NATURAL_DENSITY = 0.001;
    private static final int WILD_ANIMAL_TIME_BETWEEN_REPOPULATION = 400;
    private static final DetailedVegetation DEFAULT_VEGETATION = MEADOW_1;

    private final ConnectionsProvider OFFROAD_CONNECTIONS_PROVIDER = new ConnectionsProvider() {

        @Override
        public Iterable<Point> getPossibleConnections(Point start, Point goal) {
            return getPossibleAdjacentOffRoadConnections(start);
        }

        @Override
        public int realDistance(Point currentPoint, Point neighbor) {
            return 1;
        }
    };



    private final List<Worker>         workers;
    private final int                  height;
    private final int                  width;
    private final List<Road>           roads;
    private final Countdown            animalCountdown;
    private final List<Crop>           crops;
    private final List<Building>       buildings;
    private final List<Building>       buildingsToRemove;
    private final List<Building>       buildingsToAdd;
    private final List<Projectile>     projectilesToRemove;
    private final List<WildAnimal>     animalsToRemove;
    private final List<Flag>           flags;
    private final List<Sign>           signs;
    private final List<Projectile>     projectiles;
    private final List<WildAnimal>     wildAnimals;
    private final List<Sign>           signsToRemove;
    private final List<Worker>         workersToRemove;
    private final List<Crop>           cropsToRemove;
    private final MapPoint[]           pointToGameObject;
    private final List<Tree>           trees;
    private final List<Stone>          stones;
    private final List<Worker>         workersToAdd;
    private final List<Player>         players;
    private final Random               random;
    private final List<Point>          startingPoints;
    private final ConnectionsProvider  pathOnExistingRoadsProvider;
    private final int                  statisticsCollectionPeriod;
    private final Map<Integer, DetailedVegetation> tileBelowMap;
    private final Map<Integer, DetailedVegetation> tileDownRightMap;

    private final StatisticsManager statisticsManager;
    private final Set<Worker> workersWithNewTargets;
    private final Set<Building> newBuildings;
    private final List<Building> changedBuildings;
    private final Set<Flag> newFlags;
    private final Set<Flag> removedFlags;
    private final Set<Road> removedRoads;
    private final Set<Road> newRoads;
    private final Set<Worker> removedWorkers;
    private final Set<Building> removedBuildings;
    private final Set<Tree> newTrees;
    private final Set<Tree> removedTrees;
    private final Set<Stone> removedStones;
    private final Set<Sign> newSigns;
    private final Set<Sign> removedSigns;
    private final Set<Crop> newCrops;
    private final Set<Crop> removedCrops;
    private final Set<Road> promotedRoads;
    private final List<Point> removedDeadTrees;
    private final Stats     stats;
    private final Group     collectEachStepTimeGroup;
    private final Set<Flag> changedFlags;
    private final List<Point> deadTrees;
    private final List<Ship> ships;
    private final Set<Point> possiblePlacesForHarbor;
    private final List<Crop> harvestedCrops;
    private final List<Ship> newShips;
    private final List<Ship> finishedShips;
    private final List<Ship> shipsWithNewTargets;
    private final Map<Worker, WorkerAction> workersWithStartedActions;
    private final Map<Point, DecorationType> decorations;
    private final List<Point> removedDecorations;
    private final Set<GameChangesList.NewAndOldBuilding> upgradedBuildings;
    private final Set<Stone> changedStones;

    private Player winner;
    private long time;
    private boolean winnerReported;
    private boolean isBorderUpdated;

    /**
     * Creates a new game map
     *
     * @param players The players in the new game
     * @param width The width of the new game map
     * @param height The height of the new game map
     * @throws InvalidUserActionException An exception is thrown if the given width and height are too small or too large
     */
    public GameMap(List<Player> players, int width, int height) throws InvalidUserActionException {

        if (players.isEmpty()) {
            throw new InvalidUserActionException("Can't create game map with no players");
        }

        this.players = players;
        this.width = width;
        this.height = height;

        if (width < MINIMUM_WIDTH || height < MINIMUM_HEIGHT) {
            throw new InvalidUserActionException("Can't create too small map (" + width + "x" + height + ")");
        }

        buildings           = new ArrayList<>();
        buildingsToRemove   = new LinkedList<>();
        buildingsToAdd      = new LinkedList<>();
        projectilesToRemove = new LinkedList<>();
        animalsToRemove     = new LinkedList<>();
        cropsToRemove       = new LinkedList<>();
        roads               = new ArrayList<>();
        flags               = new ArrayList<>();
        signs               = new ArrayList<>();
        projectiles         = new ArrayList<>();
        wildAnimals         = new ArrayList<>();
        signsToRemove       = new LinkedList<>();
        workers             = new ArrayList<>();
        workersToRemove     = new LinkedList<>();
        trees               = new ArrayList<>();
        stones              = new ArrayList<>();
        crops               = new ArrayList<>();
        workersToAdd        = new LinkedList<>();
        animalCountdown     = new Countdown();
        random              = new Random(1);
        startingPoints      = new ArrayList<>();
        tileBelowMap        = new HashMap<>();
        tileDownRightMap    = new HashMap<>();
        removedDecorations  = new ArrayList<>();

        statisticsManager   = new StatisticsManager();

        pointToGameObject   = populateMapPoints();

        pathOnExistingRoadsProvider = new PathOnExistingRoadsProvider(this);

        /* Set grass as vegetation on all tiles */
        constructDefaultTiles();

        /* Add initial measurement */
        statisticsManager.addZeroInitialMeasurementForPlayers(players);

        /* Set the timekeeper to 1 */
        time = 1;

        /* Set the initial production statistics collection period */
        statisticsCollectionPeriod = 500;

        /* Give the players a reference to the map */
        for (Player player : players) {
            player.setMap(this);
        }

        /* Verify that all players have unique colors */
        if (!allPlayersHaveUniqueColor()) {
            throw new InvalidUserActionException("Each player must have a unique color");
        }

        /* There is no winner when the game starts */
        winner = null;

        workersWithNewTargets = new HashSet<>();
        changedBuildings = new ArrayList<>();
        newFlags = new HashSet<>();
        removedFlags = new HashSet<>();
        newBuildings = new HashSet<>();
        removedRoads = new HashSet<>();
        newRoads = new HashSet<>();
        removedWorkers = new HashSet<>();
        removedBuildings = new HashSet<>();
        newTrees = new HashSet<>();
        removedTrees = new HashSet<>();
        removedStones = new HashSet<>();
        newSigns = new HashSet<>();
        removedSigns = new HashSet<>();
        newCrops = new HashSet<>();
        removedCrops = new HashSet<>();
        promotedRoads = new HashSet<>();
        changedFlags = new HashSet<>();
        removedDeadTrees = new ArrayList<>();
        possiblePlacesForHarbor = new HashSet<>();
        harvestedCrops = new ArrayList<>();
        deadTrees = new ArrayList<>();
        ships = new ArrayList<>();
        newShips = new ArrayList<>();
        finishedShips = new ArrayList<>();
        shipsWithNewTargets = new ArrayList<>();
        workersWithStartedActions = new HashMap<>();
        upgradedBuildings = new HashSet<>();
        changedStones = new HashSet<>();

        winnerReported = false;

        isBorderUpdated = false;

        /* Prepare for collecting statistics on execution */
        stats = new Stats();

        collectEachStepTimeGroup = stats.createVariableGroupIfAbsent(AGGREGATED_EACH_STEP_TIME_GROUP);
        decorations = new HashMap<>();
    }

    // FIXME: HOTSPOT FOR ALLOCATION
    private void constructDefaultTiles() {

        for (int y = 0; y <= height; y++) {

            int xStart = 0;
            int xEnd   = width;

            if (y % 2 != 0) {
                xStart = -1;
                xEnd   = width + 1;
            }

            for (int x = xStart; x <= xEnd + 1; x++) {
                tileBelowMap.put(y * width + x, DEFAULT_VEGETATION);
                tileDownRightMap.put(y * width + x, DEFAULT_VEGETATION);
            }
        }
    }

    void reportBuildingConstructed(Building building) {
        changedBuildings.add(building);
    }

    /**
     * Finds the shortest possible placement for a new road between the given points for the given player
     *
     * @param player The player the road is for
     * @param start The start of the road
     * @param goal The end of the road
     * @param avoid Points that the road must avoid
     * @return A path a new road can follow
     */
    public List<Point> findAutoSelectedRoad(final Player player, Point start, Point goal, Set<Point> avoid) {
        return findShortestPath(start, goal, avoid, new ConnectionsProvider() {

            @Override
            public Iterable<Point> getPossibleConnections(Point point, Point goal) {
                return getPossibleAdjacentRoadConnections(player, point, goal);
            }

            @Override
            public int realDistance(Point currentPoint, Point neighbor) {
                return 1;
            }
        });
    }

    /**
     * Returns a road that covers the given point but does not start and end at it
     *
     * @param point A point on the map
     * @return Returns the road if there is a road at the given point
     */
    public Road getRoadAtPoint(Point point) {

        MapPoint mapPoint = getMapPoint(point);

        /* Don't include start and end points of roads so ignore the point if there is a flag */
        if (mapPoint.isFlag()) {
            return null;
        }

        Set<Road> connectedRoads = mapPoint.getConnectedRoads();

        /* Return null if there is no connected road */
        if (connectedRoads.isEmpty()) {
            return null;
        }

        /* Return the first found connected road */
        return connectedRoads.iterator().next();
    }

    /**
     * Removes the given road from the map
     *
     * @param road The road to remove
     */
    public void removeRoad(Road road) throws InvalidUserActionException {

        /* Don't allow removing the driveway for an existing building */
        if (isBuildingAtPoint(road.getStart()) || isBuildingAtPoint(road.getEnd())) {
            throw new InvalidUserActionException("Cannot remove a driveway");
        }

        doRemoveRoad(road);
    }

    void doRemoveRoad(Road road) {
        if (road.getCourier() != null) {
            road.getCourier().returnToStorage();
        }

        removeRoadButNotWorker(road);
    }

    private void removeRoadButNotWorker(Road road) {

        roads.remove(road);

        for (Point point : road.getWayPoints()) {
            MapPoint mapPoint = getMapPoint(point);

            mapPoint.removeConnectingRoad(road);
        }

        /* Report that the road is removed */
        removedRoads.add(road);
    }

    /**
     * Sets the possible starting points of the map
     *
     * @param points The points where players' headquarters should be placed
     */
    public void setStartingPoints(List<Point> points) {
        startingPoints.addAll(points);
    }

    /**
     * Returns the possible starting points of the map. A starting point is where a headquarters can be placed
     *
     * @return The points where player's headquarters should be placed
     */
    public List<Point> getStartingPoints() {
        return startingPoints;
    }

    /**
     * Sets the players of the game. Any players set before this will be removed.
     *
     * @param newPlayers Set players for the game
     */
    public void setPlayers(List<Player> newPlayers) {
        players.clear();

        players.addAll(newPlayers);

        for (Player player : players) {
            player.setMap(this);
        }
    }

    /**
     * Moves time one step ahead for all parts of the game
     *
     * @throws InvalidUserActionException Reports any invalid user actions encountered while updating the game
     */
    public void stepTime() throws InvalidUserActionException {

        Duration duration = new Duration("GameMap.stepTime");

        projectilesToRemove.clear();
        workersToRemove.clear();
        workersToAdd.clear();
        signsToRemove.clear();
        buildingsToRemove.clear();
        animalsToRemove.clear();
        cropsToRemove.clear();
        buildingsToAdd.clear();

        /* Only clear monitoring events that are generated by the game itself */
        removedWorkers.clear();

        for (Projectile projectile : projectiles) {
            projectile.stepTime();
        }

        duration.after("Projectiles step time");

        for (Worker worker : workers) {
            worker.stepTime();
        }

        duration.after("Workers step time");

        for (Ship ship : ships) {
            ship.stepTime();
        }

        duration.after("Ships step time");

        for (Building building : buildings) {
            building.stepTime();
        }

        duration.after("Building step time");

        for (Tree tree : trees) {
            tree.stepTime();
        }

        duration.after("Trees step time");

        for (Crop crop : crops) {
            crop.stepTime();
        }

        duration.after("Crops step time");

        for (Sign sign : signs) {
            sign.stepTime();
        }

        duration.after("Signs step time");

        for (WildAnimal wildAnimal : wildAnimals) {
            wildAnimal.stepTime();
        }

        duration.after("Wild animals step time");

        /* Possibly add wild animals */
        handleWildAnimalPopulation();

        duration.after("Handle wild animal population");

        /* Remove completely mined stones */
        List<Stone> stonesToRemove = new ArrayList<>();
        for (Stone stone : stones) {
            if (stone.noMoreStone()) {
                stonesToRemove.add(stone);
            }
        }

        stones.removeAll(stonesToRemove);

        for (Stone stone : stonesToRemove) {
            getMapPoint(stone.getPosition()).removeStone();
        }

        duration.after("Remove completely mined stones");

        /* Resume transport of stuck cargo */
        for (Flag flag : flags) {
            for (Cargo cargo : flag.getStackedCargo()) {

                // FIXME: do we need to re-route all cargos each time?
                cargo.rerouteIfNeeded();
            }
        }

        duration.after("Re-route cargos");

        /* Remove workers that are invalid after the round */
        workers.removeAll(workersToRemove);

        /* Add workers that were placed during the round */
        workers.addAll(workersToAdd);

        /* Remove crops that were removed during this round */
        crops.removeAll(cropsToRemove);

        /* Remove signs that have expired during this round */
        signs.removeAll(signsToRemove);

        /* Remove wild animals that have been killed and turned to cargo */
        wildAnimals.removeAll(animalsToRemove);

        /* Update buildings list to handle upgraded buildings where the old building gets removed and a new building is added */
        buildings.removeAll(buildingsToRemove);

        buildings.addAll(buildingsToAdd);

        /* Remove buildings that have been destroyed some time ago */
        buildings.removeAll(buildingsToRemove);

        for (Building building : buildingsToRemove) {
            building.getPlayer().removeBuilding(building);
        }

        /* Remove projectiles that have hit the ground */
        projectiles.removeAll(projectilesToRemove);

        duration.after("Add and remove objects during step time");

        /* Declare a winner if there is only one player still alive */
        int playersWithBuildings = 0;
        Player playerWithBuildings = null;

        for (Player player : players) {
            if (player.isAlive()) {
                playersWithBuildings++;

                playerWithBuildings = player;
            }
        }

        /* There can only be a winner if there originally were more than one player */
        if (playersWithBuildings == 1 && players.size() > 1) {
            winner = playerWithBuildings;

            if (!winnerReported) {
                for (Player player : players) {
                    player.reportWinner(winner);
                }

                winnerReported = true;
            }
        }

        duration.after("Manage winning");

        /* Collect statistics */
        if (time % statisticsCollectionPeriod == 0) {
            statisticsManager.collectFromPlayers(time, players);
        }

        duration.after("Collect statistics");

        /* Notify the players that one more step has been done */
        for (Player player : players) {
            player.manageTreeConservationProgram();
        }

        duration.after("Manage tree conservation program");

        /* Add worker events to the players if any */
        List<BorderChange> borderChanges = null;
        for (Player player : players) {

            if (!player.hasMonitor()) {
                continue;
            }

            changedStones.forEach(stone -> {
                if (player.getDiscoveredLand().contains(stone.getPosition())) {
                    player.reportChangedStone(stone);
                }
            });

            if (isBorderUpdated && borderChanges == null) {
                borderChanges = collectBorderChangesFromEachPlayer();
            }

            if (isBorderUpdated) {
                player.reportChangedBorders(borderChanges);
            }

            changedFlags.forEach(flag -> {
                if (player.getDiscoveredLand().contains(flag.getPosition())) {
                    player.reportChangedFlag(flag);
                }
            });

            workersWithNewTargets.forEach(worker -> {
                if (player.getDiscoveredLand().contains(worker.getPosition())) {
                    player.reportWorkerWithNewTarget(worker);
                }
            });

            removedWorkers.forEach(worker -> {
                if (player.getDiscoveredLand().contains(worker.getPosition())) {
                    player.reportRemovedWorker(worker);
                }
            });

            changedBuildings.forEach(building -> {
                if (player.getDiscoveredLand().contains(building.getPosition())) {
                    player.reportChangedBuilding(building);
                }
            });

            removedBuildings.forEach(building -> {
                if (player.getDiscoveredLand().contains(building.getPosition())) {
                    player.reportRemovedBuilding(building);
                }
            });

            newFlags.forEach(flag -> {
                if (player.getDiscoveredLand().contains(flag.getPosition())) {
                    player.reportNewFlag(flag);
                }
            });

            removedFlags.forEach(flag -> {
                if (player.getDiscoveredLand().contains(flag.getPosition())) {
                    player.reportRemovedFlag(flag);
                }
            });

            newRoads.forEach(road -> {
                if (setContainsAny(player.getDiscoveredLand(), road.getWayPoints())) {
                    player.reportNewRoad(road);
                }
            });

            removedRoads.forEach(road -> {
                if (setContainsAny(player.getDiscoveredLand(), road.getWayPoints())) {
                    player.reportRemovedRoad(road);
                }
            });

            newBuildings.forEach(building -> {
                if (player.getDiscoveredLand().contains(building.getPosition())) {
                    player.reportNewBuilding(building);
                }
            });

            newTrees.forEach(tree -> {
                if (player.getDiscoveredLand().contains(tree.getPosition())) {
                    player.reportNewTree(tree);
                }
            });

            removedTrees.forEach(tree -> {
                if (player.getDiscoveredLand().contains(tree.getPosition())) {
                    player.reportRemovedTree(tree);
                }
            });

            removedDeadTrees.forEach(point -> {
                if (player.getDiscoveredLand().contains(point)) {
                    player.reportRemovedDeadTree(point);
                }
            });

            removedStones.forEach(stone -> {
                if (player.getDiscoveredLand().contains(stone.getPosition())) {
                    player.reportRemovedStone(stone);
                }
            });

            newSigns.forEach(sign -> {
                if (player.getDiscoveredLand().contains(sign.getPosition())) {
                    player.reportNewSign(sign);
                }
            });

            removedSigns.forEach(sign -> {
                if (player.getDiscoveredLand().contains(sign.getPosition())) {
                    player.reportRemovedSign(sign);
                }
            });

            newCrops.forEach(crop -> {
                if (player.getDiscoveredLand().contains(crop.getPosition())) {
                    player.reportNewCrop(crop);
                }
            });

            removedCrops.forEach(crop -> {
                if (player.getDiscoveredLand().contains(crop.getPosition())) {
                    player.reportRemovedCrop(crop);
                }
            });

            promotedRoads.forEach(promotedRoad -> {
                if (setContainsAny(player.getDiscoveredLand(), promotedRoad.getWayPoints())) {
                    player.reportPromotedRoad(promotedRoad);
                }
            });

            newShips.forEach(ship -> {
                if (player.getDiscoveredLand().contains(ship.getPosition())) {
                    player.reportNewShip(ship);
                }
            });

            finishedShips.forEach(ship -> {
                if (player.getDiscoveredLand().contains(ship.getPosition())) {
                    player.reportFinishedShip(ship);
                }
            });

            shipsWithNewTargets.forEach(ship -> {
                if (player.getDiscoveredLand().contains(ship.getPosition())) {
                    player.reportShipWithNewTarget(ship);
                }
            });

            workersWithStartedActions.forEach((worker, action) -> {
                if (player.getDiscoveredLand().contains(worker.getPosition())) {
                    player.reportWorkerStartedAction(worker, action);
                }
            });

            harvestedCrops.forEach(crop -> {
                if (player.getDiscoveredLand().contains(crop.getPosition())) {
                    player.reportHarvestedCrop(crop);
                }
            });

            removedDecorations.forEach(point -> {
                if (player.getDiscoveredLand().contains(point)) {
                    player.reportRemovedDecoration(point);
                }
            });

            upgradedBuildings.forEach(newAndOldBuilding -> {
                if (player.getDiscoveredLand().contains(newAndOldBuilding.newBuilding.getPosition())) {
                    player.reportUpgradedBuilding(newAndOldBuilding.oldBuilding, newAndOldBuilding.newBuilding);
                }
            });

            player.sendMonitoringEvents(time);
        }

        duration.after("Send monitoring events");

        /* Clear the monitoring events that are generated by the players */
        newFlags.clear();
        removedFlags.clear();
        newBuildings.clear();
        changedBuildings.clear();
        newRoads.clear();
        removedRoads.clear();
        workersWithNewTargets.clear();
        removedBuildings.clear();
        newTrees.clear();
        removedTrees.clear();
        removedStones.clear();
        newSigns.clear();
        removedSigns.clear();
        newCrops.clear();
        removedCrops.clear();
        promotedRoads.clear();
        changedFlags.clear();
        removedDeadTrees.clear();
        harvestedCrops.clear();
        newShips.clear();
        finishedShips.clear();
        shipsWithNewTargets.clear();
        workersWithStartedActions.clear();
        removedDecorations.clear();
        upgradedBuildings.clear();
        changedStones.clear();

        duration.after("Clear monitoring tracking lists");

        if (winner != null) {
            winnerReported = true;
        }

        isBorderUpdated = false;

        /* Step the timekeeper */
        time = time + 1;

        duration.after("Final updates");

        duration.reportStats(stats);

        /* Collect variables accumulated during stepTime and reset their collection */

        collectEachStepTimeGroup.collectionPeriodDone();
    }

    private List<BorderChange> collectBorderChangesFromEachPlayer() {
        List<BorderChange> borderChanges;
        borderChanges = new ArrayList<>();

        for (Player player : players) {
            BorderChange borderChange = player.getBorderChange();

            if (borderChange != null) {
                borderChanges.add(borderChange);
            }
        }
        return borderChanges;
    }

    /**
     * Places the given building on the given point
     *
     * @param house The house to place
     * @param point The position of the house
     * @param <T> The type of house
     * @return The house placed
     * @throws InvalidUserActionException Any exceptions encountered while placing the building
     */
    public <T extends Building> T placeBuilding(T house, Point point) throws InvalidUserActionException {
        return placeBuilding(house, point, MUST_PLACE_INSIDE_BORDER);
    }

    <T extends Building> T placeBuilding(T house, Point point, BorderCheck borderCheck) throws InvalidUserActionException {

        /* Verify that the building is not already placed on the map */
        if (buildings.contains(house)) {
            throw new InvalidUserActionException("Can't place " + house + " as it is already placed.");
        }

        /* Verify that the house's player is valid */
        if (!players.contains(house.getPlayer())) {
            throw new InvalidGameLogicException("Can't place " + house + ", player " + house.getPlayer() + " is not valid.");
        }

        /* Handle the first building separately */
        boolean isFirstHouse = house.getPlayer().getBuildings().isEmpty();

        /* The first building place by each player must be a headquarters */
        if (isFirstHouse && !house.isHeadquarter()) {
            throw new InvalidUserActionException("Can not place " + house + " as initial building");
        }

        /* Only one headquarters can be placed per player */
        if (house.isHeadquarter() && !isFirstHouse) {
            throw new InvalidUserActionException("Can only have one headquarter placed per player");
        }

        /* Don't allow placing a headquarters so that it's flag ends up too close to the border */
        if (isFirstHouse && (width - point.x < 4 || point.y < 4)) {
            throw new InvalidUserActionException("Cannot place headquarter too close to the border so there is no space for its flag.");
        }

        /* Verify that the point is available for the chosen building */
        if (house.isMine()) {
            if (!isAvailableMinePoint(house.getPlayer(), point)) {
                throw new InvalidUserActionException("Cannot place " + house + " at non mining point.");
            }
        } else {

            if (isFirstHouse) {
                borderCheck = CAN_PLACE_OUTSIDE_BORDER;
            }

            Size canBuild = isAvailableHousePoint(house.getPlayer(), point, borderCheck);

            if (canBuild == null || !canBuild.contains(house.getSize())) {
                String name = house.getClass().getSimpleName();
                Size size = house.getSize();

                throw new InvalidUserActionException("Cannot place " + name + " of size " + size + " at " + point + ", only " + canBuild + ".");
            }
        }

        if (house.isHarbor()) {
            if (!isAvailableHarborPoint(point)) {
                throw new InvalidUserActionException("Cannot place harbor on non-harbor point");
            }
        }

        /* Ensure harbors can only be built on selected places */
        MapPoint mapPoint = getMapPoint(point);

        if (house instanceof Harbor) {
            if (!mapPoint.isHarborPossible()) {
                throw new InvalidUserActionException("Cannot place harbor on " + point);
            }
        }

        /* In case of headquarter and harbors, verify that the building is not placed within another player's border
         *     -- normally this is done by isAvailableHousePoint
         */
        if (house.isHeadquarter() || house.isHarbor()) {
            for (Player player : players) {
                if (!player.equals(house.getPlayer()) && player.isWithinBorder(point)) {
                    throw new InvalidUserActionException("Can't place building on " + point + " within another player's border");
                }
            }
        }

        /* All checks have passed so place the building */
        doPlaceBuilding(house, point, isFirstHouse);

        return house;
    }

    private Building doPlaceBuilding(Building house, Point point, boolean isFirstHouse) throws InvalidUserActionException {
        MapPoint mapPoint = getMapPoint(point);
        MapPoint mapPointDownRight = getMapPoint(point.downRight());

        /* Handle the case where there is a sign at the site */
        if (mapPoint.isSign()) {
            removeSign(getSignAtPoint(point));
        }

        /* Handle the case where there is a decoration at the site */
        if (mapPoint.isDecoration()) {
            removeDecorationAtPoint(point);
        }

        /* Use the existing flag if it exists, otherwise place a new flag */
        if (mapPointDownRight.isFlag()) {
            house.setFlag(getFlagAtPoint(point.downRight()));
        } else {
            Flag flag = house.getFlag();

            flag.setPosition(point.downRight());

            if (isFirstHouse) {
                doPlaceFlagRegardlessOfBorder(flag);
            } else {
                doPlaceFlag(flag);
            }
        }

        /* Update the building with its position and a reference to the map */
        house.setPosition(point);
        house.setMap(this);

        /* Add building to the global list of buildings */
        buildings.add(house);

        /* Add building to the player's list of buildings */
        house.getPlayer().addBuilding(house);

        /* Initialize the border if it's the first house */
        if (isFirstHouse) {
            updateBorder(house, BorderChangeCause.MILITARY_BUILDING_OCCUPIED);
        }

        /* Store in the point that there is now a building there */
        mapPoint.setBuilding(house);

        /* Create a road between the flag and the building */
        placeDriveWay(house);

        /* Note when the building was placed, so we can compare the age of buildings */
        house.setGeneration(time);

        /* Report the placed building */
        reportPlacedBuilding(house);

        return house;
    }

    void updateBorder(Building buildingCausedUpdate, BorderChangeCause cause) {

        /* Build map Point->Building, picking buildings with the highest claim */
        Map<Point, Building>    claims       = new HashMap<>();
        Map<Player, List<Land>> updatedLands = new HashMap<>();
        Set<Building>           allBuildings = new HashSet<>();

        allBuildings.addAll(getBuildings());
        allBuildings.addAll(buildingsToAdd);

        // Written this way to improve performance
        buildingsToRemove.forEach(allBuildings::remove);

        /* Calculate claims for all military buildings */
        for (Building building : allBuildings) {

            /* Filter non-military buildings */
            if (!building.isMilitaryBuilding()) {
                continue;
            }

            /* Harbors behave differently when they are the start of a new settlement
             *    -- like headquarters, but they start out as under construction and get their own border immediately
             */
            boolean harborWithOwnSettlement = building.isHarbor() && ((Harbor)building).isOwnSettlement();

            /* Filter buildings that are not yet fully built */
            if (!building.isReady() && !harborWithOwnSettlement) {
                continue;
            }

            /* Filter buildings that are not yet occupied */
            if (!building.isOccupied() && !harborWithOwnSettlement) {
                continue;
            }

            /* Store the claim for each military building. This iterates over a collection and the order may be non-deterministic */
            for (Point point : building.getDefendedLand()) {

                Building previousBuilding = claims.get(point);

                /* Handle the case where there is an existing claim for the point */
                if (previousBuilding != null) {

                    double previousClaim = calculateClaim(previousBuilding, point);
                    double currentClaim = calculateClaim(building, point);

                    /* Do nothing if the existing claim is stronger than the new claim */
                    if (currentClaim < previousClaim) {
                        continue;
                    }

                    /* Do nothing if the claims are equal but the existing building is older */
                    if (currentClaim == previousClaim && building.getGeneration() > previousBuilding.getGeneration()) {
                        continue;
                    }
                }

                /* Set the new claim for the building */
                claims.put(point, building);
            }
        }

        /* Assign points to players */
        List<Point> toInvestigate = new ArrayList<>();
        Set<Point>  localCleared  = new HashSet<>();
        Set<Point>  globalCleared = new HashSet<>();
        Set<Point>  pointsInLand  = new HashSet<>();
        Set<Point>  borders       = new LinkedHashSet<>();

        /* This iterates over a set and the order may be non-deterministic */
        for (Entry<Point, Building> pair : claims.entrySet()) {

            Point    root     = pair.getKey();
            Building building = pair.getValue();

            if (!isWithinMap(root)) {
                continue;
            }

            if (globalCleared.contains(root)) {
                continue;
            }

            Player player = building.getPlayer();

            pointsInLand.clear();

            toInvestigate.clear();
            toInvestigate.add(root);

            localCleared.clear();

            borders.clear();

            /* Investigate each un-broken landmass */
            while (!toInvestigate.isEmpty()) {
                Point point = toInvestigate.get(0);

                /* Go through the adjacent points */
                for (Point p : point.getAdjacentPointsExceptAboveAndBelow()) {
                    if (!globalCleared.contains(p) &&
                        !localCleared.contains(p)  &&
                        !toInvestigate.contains(p) &&
                        isWithinMap(p) &&
                        claims.containsKey(p)) {
                        toInvestigate.add(p);
                    }

                    /* Filter points outside the map */
                    if (!isWithinMap(p)) {
                        borders.add(point);

                        globalCleared.add(p);

                    /* Add points outside the claimed areas to the border */
                    } else if (!claims.containsKey(p)) {
                        borders.add(p);

                        globalCleared.add(p);

                    /* Add the point to the border if it belongs to another player */
                    } else if (!claims.get(p).getPlayer().equals(player)) {
                        borders.add(p);
                    }
                }

                /* Add claimed points to the points of the current land */
                if (claims.containsKey(point)) {
                    if (claims.get(point).getPlayer().equals(player)) {
                        pointsInLand.add(point);

                        globalCleared.add(point);
                    }
                }

                /* Clear the local variables */
                localCleared.add(point);
                toInvestigate.remove(point);
            }

            /* Filter out the border points from the land */
            pointsInLand.removeAll(borders);

            /* Save result as a land */
            if (!updatedLands.containsKey(player)) {
                updatedLands.put(player, new ArrayList<>());
            }

            updatedLands.get(player).add(new Land(pointsInLand, borders));

            /* Remember that the border was updated, so we can notify monitored players */
            isBorderUpdated = true;
        }

        /* Update lands in each player */
        List<Player> playersToUpdate = new ArrayList<>(players);

        /* This iterates over a set and the order may be non-deterministic */
        for (Entry<Player, List<Land>> pair : updatedLands.entrySet()) {
            pair.getKey().setLands(pair.getValue(), buildingCausedUpdate, cause);

            playersToUpdate.remove(pair.getKey());
        }

        /* Clear the players that no longer have any land */
        for (Player player : playersToUpdate) {
            player.setLands(new ArrayList<>(), buildingCausedUpdate, cause);
        }

        /* Destroy buildings now outside their player's borders */
        for (Building building : buildings) {

            /* Filter buildings that are already burning down */
            if (building.isBurningDown()) {
                continue;
            }

            /* Filter buildings that are already destroyed */
            if (building.isDestroyed()) {
                continue;
            }

            /* Filter military buildings that are occupied - they should remain */
            if (building.isMilitaryBuilding() && building.isOccupied()) {
                continue;
            }

            /* Filter harbors that support their own settlement and are not destroyed */
            if (building.isHarbor() && ((Harbor)building).isOwnSettlement() &&
                !building.isBurningDown() && !building.isDestroyed()) {
                continue;
            }

            Player player = building.getPlayer();

            if (player.isWithinBorder(building.getPosition()) && player.isWithinBorder(building.getFlag().getPosition())) {
                continue;
            }

            try {
                building.tearDown();
            } catch (InvalidUserActionException e) {
                InvalidGameLogicException invalidGameLogicException = new InvalidGameLogicException("During update border");
                invalidGameLogicException.initCause(e);

                throw invalidGameLogicException;
            }
        }

        /* Remove flags now outside the borders */
        List<Flag> flagsToRemove = new LinkedList<>();

        for (Flag flag : flags) {
            Player player = flag.getPlayer();

            if (!player.isWithinBorder(flag.getPosition())) {
                flagsToRemove.add(flag);
            }
        }

        /* Remove the flags now outside any border */
        for (Flag flag : flagsToRemove) {
            removeFlagWithoutSideEffects(flag);
        }

        /* Remove any roads now outside the borders */
        Set<Road> roadsToRemove = new HashSet<>();

        for (Road road : roads) {

            /* Only remove each road once */
            if (roadsToRemove.contains(road)) {
                continue;
            }

            Player player = road.getPlayer();

            for (Point point : road.getWayPoints()) {

                /* Filter points within the border */
                if (player.isWithinBorder(point)) {
                    continue;
                }

                /* Keep the driveways for military buildings */
                if (road.getWayPoints().size() == 2) {

                    /* Check if the connected building is military */
                    MapPoint mapPointStart = getMapPoint(road.getStart());
                    MapPoint mapPointEnd = getMapPoint(road.getEnd());

                    // FIXME: this should end up also not removing roads to unfinished/unoccupied military buildings. Test and fix.
                    if (mapPointStart.isMilitaryBuilding() || mapPointEnd.isMilitaryBuilding()) {
                        continue;
                    }
                }

                /* Remember to remove the road */
                roadsToRemove.add(road);
            }
        }

        /* Remove the roads */
        for (Road road : roadsToRemove) {
            doRemoveRoad(road);
        }

        /* Update statistics collection of land per player */
        statisticsManager.collectLandStatisticsFromPlayers(time, players);
    }

    private Road placeDriveWay(Building building) {
        List<Point> wayPoints = new ArrayList<>();

        wayPoints.add(building.getPosition());
        wayPoints.add(building.getFlag().getPosition());

        Road road = doPlaceRoad(building.getPlayer(), wayPoints);

        road.setDriveway();

        return road;
    }

    /**
     * Places a road according to the given points
     *
     * @param player The player that will own the new road
     * @param points The points of the new road
     * @return The newly placed road
     * @throws InvalidUserActionException Thrown if the given player is not part of the game
     */
    public Road placeRoad(Player player, Point... points) throws InvalidUserActionException {
        if (!players.contains(player)) {
            throw new InvalidUserActionException("Can't place road at " + Arrays.asList(points) + " because the player is invalid.");
        }

        return placeRoad(player, Arrays.asList(points));
    }

    /**
     * Places a road according to the given points
     *
     * @param player The player that will own the new road
     * @param wayPoints The points of the new road
     * @return The newly placed road
     * @throws InvalidUserActionException Thrown if the road is too short or outside the player's border
     */
    public Road placeRoad(Player player, List<Point> wayPoints) throws InvalidUserActionException {
        /* Only allow roads that are at least three points long
         *   -- Driveways are shorter, but they are created with a separate method
         */
        if (wayPoints.size() < 3) {
            throw new InvalidUserActionException("Cannot place road with less than three points.");
        }

        /* Verify that all points of the road are within the border */
        for (Point point : wayPoints) {
            if (!player.isWithinBorder(point)) {
                throw new InvalidUserActionException("Can't place road " + wayPoints + " with " + point + " outside the border");
            }
        }

        Point start = wayPoints.get(0);
        Point end   = wayPoints.get(wayPoints.size() - 1);

        MapPoint mapPointStart = getMapPoint(start);
        MapPoint mapPointEnd = getMapPoint(end);

        if (!mapPointStart.isFlag()) {
            throw new InvalidUserActionException("There must be a flag at the endpoint: " + start);
        }

        if (!mapPointEnd.isFlag()) {
            throw new InvalidUserActionException("There must be a flag at the endpoint: " + end);
        }

        /* Verify that the road does not overlap itself */
        if (!areAllUnique(wayPoints)) {
            throw new InvalidUserActionException("Cannot create a road that overlaps itself");
        }

        /* Verify that the road has at least one free point between the endpoints so the courier has somewhere to stand */
        if (wayPoints.size() < 3) {
            throw new InvalidUserActionException("Road " + wayPoints + " is too short.");
        }

        /* Verify that all points are possible as road */
        for (Point point : wayPoints) {
            if (point.equals(start)) {
                continue;
            }

            if (point.equals(end) && isPossibleAsEndPointInRoad(player, point)) {
                continue;
            }

            if (isPossibleAsAnyPointInRoad(player, point)) {
                continue;
            }

            throw new InvalidUserActionException(point + " in road is invalid");
        }

        return doPlaceRoad(player, wayPoints);
    }

    private Road doPlaceRoad(Player player, List<Point> wayPoints) {

        Road road = new Road(player, wayPoints);

        /* Set the map field in the road */
        road.setMap(this);

        roads.add(road);

        for (Point point : road.getWayPoints()) {
            MapPoint mapPoint = getMapPoint(point);

            mapPoint.addConnectingRoad(road);

            if (mapPoint.isDecoration()) {
                removeDecorationAtPoint(point);
            }
        }

        /* Report that the road is created */
        newRoads.add(road);

        return road;
    }

    /**
     * Places the shortest possible new road between the given points
     *
     * @param player The player that will own the new road
     * @param start The starting point of the road
     * @param end The end point of the road
     * @return The newly placed road
     * @throws InvalidUserActionException Any exception encountered while placing the road
     */
    public Road placeAutoSelectedRoad(Player player, Flag start, Flag end) throws InvalidUserActionException {
        return placeAutoSelectedRoad(player, start.getPosition(), end.getPosition());
    }

    /**
     * Places the shortest possible new road between the given flags
     *
     * @param player The player that will own the new road
     * @param start The start of the road
     * @param end The end of the road
     * @return The newly placed road
     * @throws InvalidUserActionException Any exception encountered while placing the new road
     */
    public Road placeAutoSelectedRoad(Player player, Point start, Point end) throws InvalidUserActionException {

        /* Throw an exception if the start and end are the same */
        if (start.equals(end)) {
            throw new InvalidUserActionException("An automatically placed road must have different start and end points.");
        }

        List<Point> wayPoints = findAutoSelectedRoad(player, start, end, null);

    	if (wayPoints == null) {
            throw new InvalidUserActionException("Can't place a road without points");
        }

        return placeRoad(player, wayPoints);
    }

    /**
     * Returns all the roads on the map
     *
     * @return The roads on the map
     */
    public List<Road> getRoads() {
        return roads;
    }

    /**
     * Finds the shortest way to walk between the given points without using the points to be avoided. Returns a list
     * with each step
     *
     * @param start The starting point
     * @param end The goal to reach
     * @param via Any points that need to be included in the path
     * @return The path if any is found, otherwise null
     */
    public List<Point> findWayWithExistingRoads(Point start, Point end, Point via) {
        if (start.equals(via)) {
            return findWayWithExistingRoads(start, end);
        } else if (via.equals(end)) {
            return findWayWithExistingRoads(start, end);
        }

        List<Point> path1 = findWayWithExistingRoads(start, via);
        List<Point> path2 = findWayWithExistingRoads(via, end);

        path2.remove(0);

        path1.addAll(path2);

        return path1;
    }

    /**
     * Finds the shortest way to walk between the given points and returns a list with each step
     *
     * @param start The starting point
     * @param end The point to reach
     * @return The found path, or null if no path exists
     */
    public List<Point> findWayWithExistingRoads(Point start, Point end) throws InvalidGameLogicException {
        if (start.equals(end)) {
            throw new InvalidGameLogicException("Start and end are the same.");
        }

        // TODO: change to using GameUtils::findShortestDetailedPathViaRoads which only looks at start&end of roads
        return findShortestPath(start, end, null, pathOnExistingRoadsProvider);
    }

    /**
     * Returns the road between the given start and end points. If several roads exist the first found road will be
     * returned
     *
     * @param start The starting point for the road
     * @param end The end point of the road
     * @return A road between the two endpoints if any exists, otherwise null.
     */
    // FIXME: this doesn't work for flags, where multiple roads intersect. Verify who uses this method.
    public Road getRoad(Point start, Point end) {
        for (Road road : roads) {
            if ((road.getStart().equals(start) && road.getEnd().equals(end)) ||
                (road.getEnd().equals(start) && road.getStart().equals(end))) {
                return road;
            }
        }

        return null;
    }

    /**
     * Places a flag on the map
     *
     * @param player The player that wants to place the flag
     * @param point The position for the flag
     * @return The placed flag
     * @throws InvalidUserActionException Thrown if the given player is not part of the game
     */
    public Flag placeFlag(Player player, Point point) throws InvalidUserActionException {

        /* Verify that the player is valid */
        if (!players.contains(player)) {
            throw new InvalidUserActionException("Can't place flag at " + point + " because the player is invalid.");
        }

        return placeFlag(new Flag(player, point));
    }

    private Flag placeFlag(Flag flag) throws InvalidUserActionException {

        if (!isAvailableFlagPoint(flag.getPlayer(), flag.getPosition(), true)) {
            throw new InvalidUserActionException("Can't place " + flag + " on occupied point");
        }

        return doPlaceFlag(flag);
    }

    private Flag doPlaceFlagRegardlessOfBorder(Flag flag) throws InvalidUserActionException {

        if (!isAvailableFlagPoint(flag.getPlayer(), flag.getPosition(), false)) {
            throw new InvalidUserActionException("Can't place " + flag + " on occupied point");
        }

        return doPlaceFlag(flag);
    }

    private Flag doPlaceFlag(Flag flag) {

        Point flagPoint = flag.getPosition();
        MapPoint mapPoint = getMapPoint(flagPoint);

        /* Handle the case where the flag is placed on a sign */
        if (mapPoint.isSign()) {
            removeSign(getSignAtPoint(flagPoint));
        }

        /* Handle the case where the flag is placed on a dead tree */
        if (mapPoint.isDeadTree()) {
            mapPoint.removeDeadTree();

            deadTrees.remove(flagPoint);

            /* Report that a dead tree was removed */
            removedDeadTrees.add(flagPoint);
        }

        /* Handle the case whe the flag is placed on a decoration */
        if (mapPoint.isDecoration()) {
            removeDecorationAtPoint(flagPoint);
        }

        /* Handle the case where the flag is on an existing road that will be split */
        if (mapPoint.isRoad()) {

            Road existingRoad = mapPoint.getRoad();
            Courier courier   = existingRoad.getCourier();

            List<Point> points = existingRoad.getWayPoints();

            int index = points.indexOf(flagPoint);

            removeRoadButNotWorker(existingRoad);

            mapPoint.setFlag(flag);
            flags.add(flag);

            Road newRoad1 = doPlaceRoad(flag.getPlayer(), points.subList(0, index + 1));
            Road newRoad2 = doPlaceRoad(flag.getPlayer(), points.subList(index, points.size()));

            /* Make the new roads into main roads if the original road was a main road */
            if (existingRoad.isMainRoad()) {
                newRoad1.makeMainRoad();
                newRoad2.makeMainRoad();

                flag.setType(MAIN);
            }

            /* Re-assign the courier to one of the new roads */
            if (courier != null) {
                Road roadToAssign;

                /* If the courier is idle, place it on the road it is on */
                if (courier.isIdle()) {
                    Point currentPosition = courier.getPosition();

                    if (newRoad1.getWayPoints().contains(currentPosition)) {
                        roadToAssign = newRoad1;
                    } else {
                        roadToAssign = newRoad2;
                    }

                /* If the courier is working... */
                } else {
                    Point lastPoint = courier.getLastPoint();
                    Point nextPoint = courier.getNextPoint();

                    MapPoint mapPointLast = getMapPoint(lastPoint);
                    MapPoint mapPointNext = getMapPoint(nextPoint);

                    /* If the courier is on the road between one of the flags and a building, pick the road with the flag */

                    /*    - Courier walking from flag to building */
                    if (mapPointLast.isFlag() && mapPointNext.isBuilding() && nextPoint.equals(lastPoint.upLeft())) {
                        if (lastPoint.equals(newRoad1.getStart()) || lastPoint.equals(newRoad1.getEnd())) {
                            roadToAssign = newRoad1;
                        } else {
                            roadToAssign = newRoad2;
                        }

                    /*    - Courier walking from building to flag */
                    } else if (mapPointLast.isBuilding() && mapPointNext.isFlag()) {
                        if (nextPoint.equals(newRoad1.getStart()) || nextPoint.equals(newRoad1.getEnd())) {
                            roadToAssign = newRoad1;
                        } else {
                            roadToAssign = newRoad2;
                        }
                    } else {

                        /* Pick the road the worker's last point was on if the next point is the new flag point */
                        if (nextPoint.equals(flagPoint)) {
                            if (newRoad1.getWayPoints().contains(lastPoint)) {
                                roadToAssign = newRoad1;
                            } else {
                                roadToAssign = newRoad2;
                            }

                        /* Pick the road the worker's next point is on if the next point is not the new flag point */
                        } else {
                            if (newRoad1.getWayPoints().contains(nextPoint)) {
                                roadToAssign = newRoad1;
                            } else {
                                roadToAssign = newRoad2;
                            }
                        }
                    }
                }

                courier.assignToRoad(roadToAssign);
            }
        } else {
            mapPoint.setFlag(flag);
            flags.add(flag);
        }

        /* Make it a marine flag if it's next to water */
        if (areAnyOneOf(getSurroundingTiles(flagPoint), WATER_VEGETATION)) {
            flag.setType(MARINE);
        }

        /* Report the placed flag */
        reportPlacedFlag(flag);

        return flag;
    }

    /**
     * Returns a list of all the buildings on the map
     *
     * @return All buildings on the map
     */
    public List<Building> getBuildings() {
        return buildings;
    }

    /**
     * Returns a list of all the flags on the map
     *
     * @return All flags on the map
     */
    public List<Flag> getFlags() {
        return flags;
    }

    /**
     * Places a worker on the map
     *
     * @param worker The worker to place
     * @param endPoint The flag or building to place the worker on
     */
    public void placeWorker(Worker worker, EndPoint endPoint) {
        worker.setPosition(endPoint.getPosition());
        workers.add(worker);
    }

    /**
     * Returns a list of all the workers on the map
     *
     * @return All the workers on the map
     */
    public List<Worker> getWorkers() {
        return workers;
    }

    /**
     * Returns a list of the points where the given player can place a flag
     *
     * @param player The player that wants to place a flag
     * @return A list of all the places on the map where the player can place a flag
     */
    public Collection<Point> getAvailableFlagPoints(Player player) {
        Set<Point> points = new HashSet<>();

        /* This iterates over a set and the order may be non-deterministic */
        for (Point point : player.getLandInPoints()) {
            if (!isAvailableFlagPoint(player, point)) {
                    continue;
                }

            points.add(point);
        }

        return points;
    }

    /**
     * Returns true if it's possible for the given player to place a flag at the given point
     *
     * @param player The player that wants to place a flag
     * @param point The position of a potential new flag
     * @return True if it's possible for the player to place a flag on the point, otherwise false
     */
    public boolean isAvailableFlagPoint(Player player, Point point) {
        return isAvailableFlagPoint(player, point, true);
    }

    private boolean isAvailableFlagPoint(Player player, Point point, boolean checkBorder) {
        MapPoint mapPoint = getMapPoint(point);

        if (!isWithinMap(point)) {
            return false;
        }

        if (checkBorder && !player.isWithinBorder(point)) {
            return false;
        }

        if (mapPoint.isFlag()) {
            return false;
        }

        if (mapPoint.isStone()) {
            return false;
        }

        if (mapPoint.isTree()) {
            return false;
        }

        if (mapPoint.isBuilding()) {
            return false;
        }

        if (mapPoint.isShipUnderConstruction()) {
            return false;
        }

        DetailedVegetation detailedVegetationUpLeft = getDetailedVegetationUpLeft(point);
        DetailedVegetation detailedVegetationAbove = getDetailedVegetationAbove(point);
        DetailedVegetation detailedVegetationUpRight = getDetailedVegetationUpRight(point);
        DetailedVegetation detailedVegetationDownRight = getDetailedVegetationDownRight(point);
        DetailedVegetation detailedVegetationBelow = getDetailedVegetationBelow(point);
        DetailedVegetation detailedVegetationDownLeft = getDetailedVegetationDownLeft(point);

        if (!CAN_BUILD_ROAD_ON.contains(detailedVegetationUpLeft)    &&
            !CAN_BUILD_ROAD_ON.contains(detailedVegetationAbove)     &&
            !CAN_BUILD_ROAD_ON.contains(detailedVegetationUpRight)   &&
            !CAN_BUILD_ROAD_ON.contains(detailedVegetationDownRight) &&
            !CAN_BUILD_ROAD_ON.contains(detailedVegetationBelow)     &&
            !CAN_BUILD_ROAD_ON.contains(detailedVegetationDownLeft)) {
            return false;
        }

        if (mapPoint.isUnHarvestedCrop()) {
            return false;
        }

        Point pointRight = point.right();
        Point pointLeft = point.left();
        Point pointUpLeft = point.upLeft();
        Point pointUpRight = point.upRight();
        Point pointDownRight = point.downRight();
        Point pointDownLeft = point.downLeft();

        MapPoint mapPointRight = getMapPoint(pointRight);
        MapPoint mapPointLeft = getMapPoint(pointLeft);
        MapPoint mapPointUpLeft = getMapPoint(pointUpLeft);
        MapPoint mapPointUpRight = getMapPoint(pointUpRight);
        MapPoint mapPointDownRight = getMapPoint(pointDownRight);
        MapPoint mapPointDownLeft = getMapPoint(pointDownLeft);

        if (player.isWithinBorder(pointUpLeft) && mapPointUpLeft.isFlag()) {
            return false;
        }

        if (player.isWithinBorder(pointUpRight) && mapPointUpRight.isFlag()) {
            return false;
        }

        if (player.isWithinBorder(pointDownRight) && mapPointDownRight.isFlag()) {
            return false;
        }

        if (player.isWithinBorder(pointDownLeft) && mapPointDownLeft.isFlag()) {
            return false;
        }

        if (player.isWithinBorder(pointRight) && mapPointRight.isFlag()) {
            return false;
        }

        if (player.isWithinBorder(pointLeft) && mapPointLeft.isFlag()) {
            return false;
        }

        if (player.isWithinBorder(pointDownRight) && mapPointDownRight.isBuildingOfSize(LARGE)) {
            return false;
        }

        if (player.isWithinBorder(pointRight) && mapPointRight.isBuildingOfSize(LARGE)) {
            return false;
        }

        if (player.isWithinBorder(pointDownLeft) && mapPointDownLeft.isBuildingOfSize(LARGE)) {
            return false;
        }

        return true;
    }

    /**
     * Returns a map of the points within the given player's border and the corresponding available construction. If
     * there is no possibility to build for a point its value will be null
     *
     * @param player The player that wants to place houses
     * @return A list of all the places where the player can place houses
     */
    public Map<Point, Size> getAvailableHousePoints(Player player) {
        Map<Point, Size> housePoints = new HashMap<>();

        /* This iterates over a set and the order is non-deterministic */
        for (Point point : player.getLandInPoints()) {
            Size availableHouse = isAvailableHousePoint(player, point);

            if (availableHouse != null) {
                housePoints.put(point, availableHouse);
            }
        }

        return housePoints;
    }

    private List<Point> getPossibleAdjacentRoadConnections(Player player, Point start, Point end) {
        Point[] adjacentPoints = {
            new Point(start.x - 2, start.y),
            new Point(start.x + 2, start.y),
            new Point(start.x - 1, start.y - 1),
            new Point(start.x - 1, start.y + 1),
            new Point(start.x + 1, start.y - 1),
            new Point(start.x + 1, start.y + 1),
        };

        List<Point> adjacentPossibleRoadConnections = new ArrayList<>();

        for (Point point : adjacentPoints) {
            if (point.equals(end) && isPossibleAsEndPointInRoad(player, point)) {
                adjacentPossibleRoadConnections.add(point);
            } else if (!point.equals(end) && isPossibleAsAnyPointInRoad(player, point)) {
                adjacentPossibleRoadConnections.add(point);
            }
        }

        return adjacentPossibleRoadConnections;
    }

    // FIXME: HOTSPOT - allocations
    Collection<Point> getPossibleAdjacentOffRoadConnections(Point from) {
        List<Point>  possibleAdjacentOffRoadConnections = new ArrayList<>();

        MapPoint mapPoint = getMapPoint(from);

        /* Houses can only be left via the driveway so handle this case separately */
        if (mapPoint.isBuilding()) {
            possibleAdjacentOffRoadConnections.add(from.downRight());

            return possibleAdjacentOffRoadConnections;
        }

        /* Find out which adjacent points are possible off-road connections */
        Point[] adjacentPoints  = from.getAdjacentPointsExceptAboveAndBelow();

        DetailedVegetation detailedVegetationUpLeft = getDetailedVegetationUpLeft(from);
        DetailedVegetation detailedVegetationAbove = getDetailedVegetationAbove(from);
        DetailedVegetation detailedVegetationUpRight = getDetailedVegetationUpRight(from);
        DetailedVegetation detailedVegetationDownRight = getDetailedVegetationDownRight(from);
        DetailedVegetation detailedVegetationBelow = getDetailedVegetationBelow(from);
        DetailedVegetation detailedVegetationDownLeft = getDetailedVegetationDownLeft(from);

        boolean cannotWalkOnTileUpLeft    = !CAN_WALK_ON.contains(detailedVegetationUpLeft);
        boolean cannotWalkOnTileDownLeft  = !CAN_WALK_ON.contains(detailedVegetationDownLeft);
        boolean cannotWalkOnTileUpRight   = !CAN_WALK_ON.contains(detailedVegetationUpRight);
        boolean cannotWalkOnTileDownRight = !CAN_WALK_ON.contains(detailedVegetationDownRight);
        boolean cannotWalkOnTileAbove     = !CAN_WALK_ON.contains(detailedVegetationAbove);
        boolean cannotWalkOnTileBelow     = !CAN_WALK_ON.contains(detailedVegetationBelow);

        for (Point adjacentPoint : adjacentPoints) {

            /* Filter points outside the map */
            if (!isWithinMap(adjacentPoint)) {
                continue;
            }

            MapPoint mapPointAdjacent = getMapPoint(adjacentPoint);

            /* Filter points with stones */
            if (mapPointAdjacent.isStone()) {
                continue;
            }

            /* Buildings can only be reached from their flags */
            if (mapPointAdjacent.isBuilding() && !adjacentPoint.downRight().equals(from)) {
                continue;
            }

            /* Filter points separated by vegetation that can't be walked on */

            if (adjacentPoint.isLeftOf(from) && cannotWalkOnTileUpLeft && cannotWalkOnTileDownLeft) {
                continue;
            }

            if (adjacentPoint.isUpLeftOf(from) && cannotWalkOnTileUpLeft && cannotWalkOnTileAbove) {
                continue;
            }

            if (adjacentPoint.isUpRightOf(from) && cannotWalkOnTileUpRight && cannotWalkOnTileAbove) {
                continue;
            }

            if (adjacentPoint.isRightOf(from) && cannotWalkOnTileUpRight && cannotWalkOnTileDownRight) {
                continue;
            }

            if (adjacentPoint.isDownRightOf(from) && cannotWalkOnTileDownRight && cannotWalkOnTileBelow) {
                continue;
            }

            if (adjacentPoint.isDownLeftOf(from) && cannotWalkOnTileDownLeft && cannotWalkOnTileBelow) {
                continue;
            }

            /* Add the point to the list if it passed the filters */
            possibleAdjacentOffRoadConnections.add(adjacentPoint);
        }

        return possibleAdjacentOffRoadConnections;
    }

    /**
     * Returns true if there is a flag at the given point
     *
     * @param point The point where there might be a flag
     * @return Returns true if there is a flag at the given point, otherwise false
     */
    public boolean isFlagAtPoint(Point point) {
        return getMapPoint(point).isFlag();
    }

    /**
     * Returns true if the given point is within the map
     *
     * @param point The point that might be on the map
     * @return Returns true if the point is within the map, otherwise false
     */
    public boolean isWithinMap(Point point) {
        return point.x > 0 && point.x < width && point.y > 0 && point.y < height;
    }

    /**
     * Creates an array with all Map Point instances. They are indexed like this:
     *
     * 4     5
     *    2     3
     * 0     1
     *
     * To address a Map Point:
     *  - Data row length depends on the width of the game map
     *     - For even width: dataRowLength = width / 2
     *     - For odd width: dataRowLength = (width + 1) / 2
     *  - For row where y is even: mapPoints[y * dataRowLength + (x / 2)]
     *  - For row where y is odd: mapPoints[y * dataRowLength + ((x - 1) / 2)]
     *
     * @return Array of map points
     */
    private MapPoint[] populateMapPoints() {
        int dataRowLength = isEven(width) ? width / 2 : (width + 1) / 2;

        MapPoint[] mapPoints = new MapPoint[dataRowLength * (height + 1)];

        /* Walk row by row. Start at 0 and include the final row at #height */
        for (int y = 0; y < height + 1; y++) {

            int xOffset = isEven(y) ? 0 : 1;

            for (int x = xOffset; x < width; x += 2) {
                Point point = new Point(x, y);

                if (isEven(point.y)) {
                    mapPoints[point.y * dataRowLength + point.x / 2] = new MapPoint(point);
                } else {
                    mapPoints[point.y * dataRowLength + (point.x - 1) / 2] = new MapPoint(point);
                }
            }
        }

        return mapPoints;
    }

    /**
     * Returns the flag at the given point
     *
     * @param point The point to get the flag from
     * @return Returns the flag at the given point, or null if there is no flag at the given point
     */
    public Flag getFlagAtPoint(Point point) {
        MapPoint mapPoint = getMapPoint(point);

        return mapPoint.getFlag();
    }

    private boolean isPossibleAsEndPointInRoad(Player player, Point point) {

        if (!isWithinMap(point)) {
            return false;
        }

        if (!player.isWithinBorder(point)) {
            return false;
        }

        MapPoint mapPoint = getMapPoint(point);

        if (mapPoint.isFlag()) {
            return true;
        }

        if (mapPoint.isRoad() && isAvailableFlagPoint(player, point)) {
            return true;
        }

        if (isPossibleAsAnyPointInRoad(player, point)) {
            return true;
        }

        return false;
    }

    private boolean isPossibleAsAnyPointInRoad(Player player, Point point) {
        MapPoint mapPoint = getMapPoint(point);

        if (!isWithinMap(point)) {
            return false;
        }

        if (!player.isWithinBorder(point)) {
            return false;
        }

        if (mapPoint.isRoad()) {
            return false;
        }

        if (mapPoint.isFlag()) {
            return false;
        }

        if (mapPoint.isStone()) {
            return false;
        }

        if (mapPoint.isBuilding()) {
            return false;
        }

        if (mapPoint.isTree()) {
            return false;
        }

        if (mapPoint.isCrop()) {
            return false;
        }

        if (!player.isWithinBorder(point)) {
            return false;
        }

        Collection<DetailedVegetation> surroundingVegetation = getSurroundingTiles(point);

        if (areNonePartOf(surroundingVegetation, CAN_BUILD_ROAD_ON)) {
            return false;
        }

        return true;
    }

    /**
     * Returns the adjacent points of the given point that can be used to connect a road being placed. Flags that could
     * be used as endpoints are not included.
     *
     * @param player The player to get the possible road connections for
     * @param from The starting point for the possible road connections
     * @return A list of the adjacent points that it's possible to build a road to from the given point
     */
    public List<Point> getPossibleRoadConnectionsExcludingEndpoints(Player player, Point from) {
        List<Point> resultList = new ArrayList<>();

        Point pointUpLeft = from.upLeft();
        Point pointUpRight = from.upRight();
        Point pointRight = from.right();
        Point pointDownRight = from.downRight();
        Point pointDownLeft = from.downLeft();
        Point pointLeft = from.left();

        if (isPossibleAsAnyPointInRoad(player, pointUpLeft)) {
            resultList.add(pointUpLeft);
        }

        if (isPossibleAsAnyPointInRoad(player, pointUpRight)) {
            resultList.add(pointUpRight);
        }

        if (isPossibleAsAnyPointInRoad(player, pointRight)) {
            resultList.add(pointRight);
        }

        if (isPossibleAsAnyPointInRoad(player, pointDownRight)) {
            resultList.add(pointDownRight);
        }

        if (isPossibleAsAnyPointInRoad(player, pointDownLeft)) {
            resultList.add(pointDownLeft);
        }

        if (isPossibleAsAnyPointInRoad(player, pointLeft)) {
            resultList.add(pointLeft);
        }

        return resultList;
    }

    /**
     * Returns the adjacent points of the given point that can be used to connect a road being placed. This method
     * also includes flags that could be endpoints for the road.
     *
     * @param player The player to get the possible adjacent road connections for
     * @param from The point to get the possible adjacent road connections for
     * @return A list of the adjacent points that it's possible to build a road to
     */
    public List<Point> getPossibleAdjacentRoadConnectionsIncludingEndpoints(Player player, Point from) throws InvalidUserActionException {
        if (!isWithinMap(from)) {
            throw new InvalidUserActionException("Cannot get adjacent road connections from a point outside the map");
        }

        List<Point> resultList = new ArrayList<>();

        Point pointUpLeft = from.upLeft();
        Point pointUpRight = from.upRight();
        Point pointRight = from.right();
        Point pointDownRight = from.downRight();
        Point pointDownLeft = from.downLeft();
        Point pointLeft = from.left();

        if (isPossibleAsAnyPointInRoad(player, pointUpLeft) || isPossibleAsEndPointInRoad(player, pointUpLeft)) {
            resultList.add(pointUpLeft);
        }

        if (isPossibleAsAnyPointInRoad(player, pointUpRight) || isPossibleAsEndPointInRoad(player, pointUpRight)) {
            resultList.add(pointUpRight);
        }

        if (isPossibleAsAnyPointInRoad(player, pointRight) || isPossibleAsEndPointInRoad(player, pointRight)) {
            resultList.add(pointRight);
        }

        if (isPossibleAsAnyPointInRoad(player, pointDownRight) || isPossibleAsEndPointInRoad(player, pointDownRight)) {
            resultList.add(pointDownRight);
        }

        if (isPossibleAsAnyPointInRoad(player, pointDownLeft) || isPossibleAsEndPointInRoad(player, pointDownLeft)) {
            resultList.add(pointDownLeft);
        }

        if (isPossibleAsAnyPointInRoad(player, pointLeft) || isPossibleAsEndPointInRoad(player, pointLeft)) {
            resultList.add(pointLeft);
        }

        return resultList;
    }

    /**
     * Returns the building at the given point. If there is no building, null is returned
     *
     * @param point a point on the map
     * @return the building at the given point
     */
    public Building getBuildingAtPoint(Point point) {
        MapPoint mapPoint = getMapPoint(point);

        return mapPoint.getBuilding();
    }

    /**
     * Returns true if there is a building at the given point
     *
     * @param point a point on the map
     * @return true if there is a building on the given point
     */
    public boolean isBuildingAtPoint(Point point) {
        MapPoint mapPoint = getMapPoint(point);

        return mapPoint.isBuilding();
    }

    /**
     * Returns true if there is a road at the given point
     *
     * @param point a point on the map
     * @return true if there is a road at the given point
     */
    public boolean isRoadAtPoint(Point point) {
        MapPoint mapPoint = getMapPoint(point);

        return !mapPoint.getConnectedNeighbors().isEmpty();
    }

    /**
     * Returns true if there is a tree at the given point
     *
     * @param point a point on the map
     * @return true if there is a tree at the point
     */
    public boolean isTreeAtPoint(Point point) {
        MapPoint mapPoint = getMapPoint(point);

        return mapPoint.getTree() != null;
    }

    /**
     * Finds an off-road way from the start to the goal via the given point and without
     * reaching any points to avoid
     *
     * @param start The start of the trip
     * @param goal The end of the trip
     * @param via The point to pass by
     * @param avoid The points to avoid
     * @return The path found or null
     */
    public List<Point> findWayOffroad(Point start, Point goal, Point via, Set<Point> avoid) {

        /* Handle the case where the "via" point is equal to the start or the goal */
        if (start.equals(via)) {
            return findWayOffroad(start, goal, avoid);
        } else if (via.equals(goal)) {
            return findWayOffroad(start, goal, avoid);
        }

        /* Calculate and join each step */
        List<Point> path1 = findWayOffroad(start, via, avoid);
        List<Point> path2 = findWayOffroad(via, goal, avoid);

        /* Return null if one of there is no way for one of the steps */
        if (path1 == null || path2 == null) {
            return null;
        }

        /* Join the steps */
        path2.removeFirst();

        path1.addAll(path2);

        return path1;
    }

    /**
     * Finds an off-road way from the start to the goal without using any points to avoid
     *
     * @param start The start of the trip
     * @param goal The end of the trip
     * @param avoid The points to avoid
     * @return The path found or null if there is no way
     */
    public List<Point> findWayOffroad(Point start, Point goal, Set<Point> avoid) {
        return findShortestPath(start, goal, avoid, OFFROAD_CONNECTIONS_PROVIDER);
    }

    public List<Point> findWayOffroad(Point start, Point via, Point goal, Set<Point> avoid, OffroadOption offroadOption) {
        var offroadConnectionsWithOptions = new ConnectionsProvider() {

            @Override
            public Iterable<Point> getPossibleConnections(Point start, Point end) {
                List<Point>  possibleAdjacentOffRoadConnections = new ArrayList<>();

                MapPoint mapPoint = getMapPoint(start);

                /* Houses can only be left via the driveway so handle this case separately */
                if (mapPoint.isBuilding()) {
                    possibleAdjacentOffRoadConnections.add(start.downRight());

                    return possibleAdjacentOffRoadConnections;
                }

                /* Find out which adjacent points are possible off-road connections */
                Point[] adjacentPoints  = start.getAdjacentPointsExceptAboveAndBelow();

                DetailedVegetation detailedVegetationUpLeft = getDetailedVegetationUpLeft(start);
                DetailedVegetation detailedVegetationAbove = getDetailedVegetationAbove(start);
                DetailedVegetation detailedVegetationUpRight = getDetailedVegetationUpRight(start);
                DetailedVegetation detailedVegetationDownRight = getDetailedVegetationDownRight(start);
                DetailedVegetation detailedVegetationBelow = getDetailedVegetationBelow(start);
                DetailedVegetation detailedVegetationDownLeft = getDetailedVegetationDownLeft(start);

                boolean cannotWalkOnTileUpLeft    = !CAN_WALK_ON.contains(detailedVegetationUpLeft);
                boolean cannotWalkOnTileDownLeft  = !CAN_WALK_ON.contains(detailedVegetationDownLeft);
                boolean cannotWalkOnTileUpRight   = !CAN_WALK_ON.contains(detailedVegetationUpRight);
                boolean cannotWalkOnTileDownRight = !CAN_WALK_ON.contains(detailedVegetationDownRight);
                boolean cannotWalkOnTileAbove     = !CAN_WALK_ON.contains(detailedVegetationAbove);
                boolean cannotWalkOnTileBelow     = !CAN_WALK_ON.contains(detailedVegetationBelow);

                for (Point adjacentPoint : adjacentPoints) {

                    /* Filter points outside the map */
                    if (!isWithinMap(adjacentPoint)) {
                        continue;
                    }

                    MapPoint mapPointAdjacent = getMapPoint(adjacentPoint);

                    /* Filter points with stones */
                    if (mapPointAdjacent.isStone()) {
                        if (offroadOption != OffroadOption.CAN_END_ON_STONE || !adjacentPoint.equals(end)) {
                            continue;
                        }
                    }

                    /* Buildings can only be reached from their flags */
                    if (mapPointAdjacent.isBuilding() && !adjacentPoint.downRight().equals(start)) {
                        continue;
                    }

                    /* Filter points separated by vegetation that can't be walked on */

                    if (adjacentPoint.isLeftOf(start) && cannotWalkOnTileUpLeft && cannotWalkOnTileDownLeft) {
                        continue;
                    }

                    if (adjacentPoint.isUpLeftOf(start) && cannotWalkOnTileUpLeft && cannotWalkOnTileAbove) {
                        continue;
                    }

                    if (adjacentPoint.isUpRightOf(start) && cannotWalkOnTileUpRight && cannotWalkOnTileAbove) {
                        continue;
                    }

                    if (adjacentPoint.isRightOf(start) && cannotWalkOnTileUpRight && cannotWalkOnTileDownRight) {
                        continue;
                    }

                    if (adjacentPoint.isDownRightOf(start) && cannotWalkOnTileDownRight && cannotWalkOnTileBelow) {
                        continue;
                    }

                    if (adjacentPoint.isDownLeftOf(start) && cannotWalkOnTileDownLeft && cannotWalkOnTileBelow) {
                        continue;
                    }

                    /* Add the point to the list if it passed the filters */
                    possibleAdjacentOffRoadConnections.add(adjacentPoint);
                }

                return possibleAdjacentOffRoadConnections;
            }

            @Override
            public int realDistance(Point currentPoint, Point neighbor) {
                return GameUtils.distanceInGameSteps(currentPoint, neighbor);
            }
        };

        List<Point> step0 = new ArrayList<>();

        if (via != null) {
            step0 = findShortestPath(start, via, avoid, offroadConnectionsWithOptions);
        }

        if (step0 == null) {
            return null;
        }

        List<Point> step1;

        if (via != null) {
            step1 = findShortestPath(via, goal, avoid, offroadConnectionsWithOptions);
        } else {
            step1 = findShortestPath(start, goal, avoid, offroadConnectionsWithOptions);
        }

        if (step1 == null) {
            return null;
        }

        if (!step0.isEmpty()) {
            step1.removeFirst();
        }

        step0.addAll(step1);

        return step0;
    }

    /**
     * Places a tree at the given point
     * @param point The point to place the tree at
     * @param treeType The type of the tree
     * @param treeSize The size of the tree
     * @return The placed tree
     * @throws InvalidUserActionException Thrown if the tree would be placed on a flag, road, or stone
     */
    public Tree placeTree(Point point, Tree.TreeType treeType, TreeSize treeSize) throws InvalidUserActionException {
        MapPoint mapPoint = getMapPoint(point);

        if (mapPoint.isFlag()) {
            throw new InvalidUserActionException("Can't place tree on " + point + " on existing flag");
        } else if (mapPoint.isRoad()) {
            throw new InvalidUserActionException("Can't place tree on " + point + " on existing road");
        } else if (mapPoint.isStone()) {
            throw new InvalidUserActionException("Can't place tree on " + point + " on existing stone");
        }

        Tree tree = new Tree(point, treeType, treeSize);

        mapPoint.setTree(tree);

        trees.add(tree);

        if (mapPoint.isDecoration()) {
            this.removeDecorationAtPoint(point);
        }

        /* Report that a new tree is planted */
        newTrees.add(tree);

        return tree;
    }

    /**
     * Returns the list of trees on the map
     *
     * @return The list of trees
     */
    public Collection<Tree> getTrees() {
        return trees;
    }

    void removeTree(Point position) {
        MapPoint mapPoint = getMapPoint(position);

        Tree tree = mapPoint.getTree();

        mapPoint.removeTree();

        trees.remove(tree);

        /* Report that the tree was removed */
        removedTrees.add(tree);
    }

    public Tree getTreeAtPoint(Point point) {
        MapPoint mapPoint = getMapPoint(point);

        return mapPoint.getTree();
    }

    /**
     * Places a stone at the given point
     *
     * @param point The point to place the stone on
     * @param stoneType
     * @param amount
     * @return The placed stone
     */
    public Stone placeStone(Point point, StoneType stoneType, int amount) {
        MapPoint mapPoint = getMapPoint(point);

        Stone stone = new Stone(point, stoneType, amount);

        mapPoint.setStone(stone);

        stones.add(stone);

        return stone;
    }

    /**
     * Places a crop at the given point
     *
     * @param point    The point to place the crop on
     * @param cropType
     * @return The placed crop
     * @throws InvalidUserActionException Throws exception if the crop cannot be placed
     */
    public Crop placeCrop(Point point, CropType cropType) throws InvalidUserActionException {
        MapPoint mapPoint = getMapPoint(point);

        if (mapPoint.isUnHarvestedCrop()) {
            throw new InvalidUserActionException("Can't place crop on non-harvested crop at " + point);
        }

        Crop crop = new Crop(point, this, cropType);

        mapPoint.setCrop(crop);

        crops.add(crop);

        /* Report that a new crop was planted */
        newCrops.add(crop);

        return crop;
    }

    /**
     * Returns true if there is a crop at the point
     *
     * @param point The point where there might be a crop
     * @return True if there is a crop at the point
     */
    public boolean isCropAtPoint(Point point) {
        MapPoint mapPoint = getMapPoint(point);

        return mapPoint.getCrop() != null;
    }

    /**
     * Returns true if there is a stone at the point
     *
     * @param point The point where there might be a stone
     * @return True if there is a stone at the point
     */
    public boolean isStoneAtPoint(Point point) {
        MapPoint mapPoint = getMapPoint(point);

        return mapPoint.getStone() != null;
    }

    Cargo removePartOfStone(Point position) {
        MapPoint mapPoint = getMapPoint(position);

        Stone stone = mapPoint.getStone();

        if (stone.noMoreStone()) {
            return null;
        }

        stone.removeOnePart();

        if (stone.noMoreStone()) {
            mapPoint.setStone(null);

            /* Report that the stone was removed */
            removedStones.add(stone);
        } else {
            changedStones.add(stone);
        }

        return new Cargo(Material.STONE, this);
    }

    /**
     * Returns a list of points within the given radius to the given point
     *
     * @param point The center point
     * @param radius The radius to collect points within
     * @return The list of points within the radius
     */
    public List<Point> getPointsWithinRadius(Point point, int radius) {
        List<Point> pointsWithinRadius = new ArrayList<>();

        boolean rowFlip = false;

        for (int y = point.y - radius; y <= point.y + radius; y++) {
            int startX = point.x - radius;

            if (rowFlip) {
                startX++;
            }

            for (int x = startX; x <= point.x + radius; x += 2) {

                Point p = new Point(x, y);

                if (isWithinMap(p) && point.distance(p) <= radius) {
                    pointsWithinRadius.add(p);
                }
            }

            rowFlip = !rowFlip;
        }

        return pointsWithinRadius;
    }

    /**
     * Returns the stones on the map
     *
     * @return The list of stones on the map
     */
    public List<Stone> getStones() {
        return stones;
    }

    /**
     * Returns the crops at a given point
     *
     * @param point The point to get crops at
     * @return The crops at the given point
     */
    public Crop getCropAtPoint(Point point) {
        return getMapPoint(point).getCrop();
    }

    /**
     * Returns the crops on the map
     *
     * @return List of crops
     */
    public Iterable<Crop> getCrops() {
        return crops;
    }

    /**
     * Removes the given flag from the game
     *
     * @param flag The flag to remove
     * @throws InvalidUserActionException Thrown if the flag to remove is null
     */
    public void removeFlag(Flag flag) throws InvalidUserActionException {

        if (flag == null) {
            throw new InvalidUserActionException("Cannot remove flag that is null");
        }

        MapPoint mapPointUpLeft = getMapPoint(flag.getPosition().upLeft());
        MapPoint mapPoint = getMapPoint(flag.getPosition());

        /* Destroy the house if the flag is connected to a house */
        if (mapPointUpLeft.isBuilding() && flag.equals(mapPointUpLeft.getBuilding().getFlag())) {
            Building attachedBuilding = mapPointUpLeft.getBuilding();

            if (!attachedBuilding.isBurningDown() && !attachedBuilding.isDestroyed()) {
                attachedBuilding.tearDown();
            }
        }

        /* Remove the road if the flag is an endpoint to a road */
        List<Road> roadsToRemove = new LinkedList<>();
        for (Road road : mapPoint.getConnectedRoads()) {
            if (road.getStartFlag().equals(flag) || road.getEndFlag().equals(flag)) {
                roadsToRemove.add(road);
            }
        }

        /* Remove roads connected to the flag */
        for (Road road : roadsToRemove) {
            removeRoad(road);
        }

        /* Break any promised deliveries */
        flag.onRemove();

        removeFlagWithoutSideEffects(flag);
    }

    private void removeFlagWithoutSideEffects(Flag flag) {
        MapPoint mapPoint = getMapPoint(flag.getPosition());

        /* Remove the flag */
        mapPoint.removeFlag();

        /* Report the removed flag */
        reportRemovedFlag(flag);

        flags.remove(flag);
    }

    /**
     * Returns the amount of the given mineral at the given point
     *
     * @param mineral Type of mineral
     * @param point The point to get the amount of mineral at
     * @return The amount of the given mineral at the given point
     */
    public int getAmountOfMineralAtPoint(Material mineral, Point point) {
        MapPoint mapPoint = getMapPoint(point);

        return mapPoint.getAmountOfMineral(mineral);
    }

    /**
     * Returns the amount of fish at a given point
     *
     * @param point The point to get the amount of fish for
     * @return The amount of fish at the given point
     */
    public int getAmountFishAtPoint(Point point) {

        /* Return zero if the point is not next to any water */
        if (!isNextToAnyWater(point)) {
            return 0;
        }

        MapPoint mapPoint = getMapPoint(point);

        return mapPoint.getAmountOfFish();
    }

    /**
     * Catches a fish at the given point
     *
     * @param point Where to catch the fish
     * @return A cargo containing the fish
     */
    public Cargo catchFishAtPoint(Point point) {
        MapPoint mapPoint = getMapPoint(point);

        if (mapPoint.getAmountOfFish() ==0) {
            throw new InvalidGameLogicException("Can't find any fish to catch at " + point);
        }

        mapPoint.consumeOneFish();

        return new Cargo(FISH, this);
    }

    /**
     * Mines one bit of ore from the given point
     *
     * @param mineral the type of mineral to attempt to mine
     * @param point the point to mine at
     * @return a cargo containing the mined ore
     */
    public Cargo mineMineralAtPoint(Material mineral, Point point) {
        MapPoint mapPoint = getMapPoint(point);

        mapPoint.mineMineral();

        return new Cargo(mineral, this);
    }

    /**
     * Returns the sign at the given point
     *
     * @param point the point where the sign is
     * @return the sign at the given point
     */
    public Sign getSignAtPoint(Point point) {
        return getMapPoint(point).getSign();
    }

    public MapPoint getMapPoint(Point point) {
        int dataRowLength = isEven(width) ? width / 2 : (width + 1) / 2;

        int index;

        if (isEven(point.y)) {
            index = point.y * dataRowLength + point.x / 2;
        } else {
            index = point.y * dataRowLength + (point.x - 1) / 2;
        }

        if (index < 0 || index >= this.pointToGameObject.length) {
            return null;
        }

        return pointToGameObject[index];
    }

    /**
     * Places a sign at the given point, with the given type of mineral and the
     * given amount
     *  @param mineral the type of mineral
     * @param amount the amount of mineral
     * @param point the point where the mineral was found
     * @return The placed sign
     */
    public Sign placeSign(Material mineral, Size amount, Point point) {
        MapPoint mapPoint = getMapPoint(point);

        Sign sign = new Sign(mineral, amount, point, this);

        mapPoint.setSign(sign);

        signs.add(sign);

        /* Report that the sign is placed */
        newSigns.add(sign);

        return sign;
    }

    /**
     * Returns true if there is a sign on the given point
     * @param point the point where the sign may be
     * @return true if there is a sign on the point
     */
    public boolean isSignAtPoint(Point point) {
        return getMapPoint(point).getSign() != null;
    }

    /**
     * Returns all the signs on the map
     *
     * @return the signs on the map
     */
    public Collection<Sign> getSigns() {
        return signs;
    }

    /**
     * Places an empty sign on the given point
     *
     * @param point the point to place th empty sign
     */
    public void placeEmptySign(Point point) {
        placeSign(null, null, point);
    }

    void removeSignWithinStepTime(Sign sign) {
        MapPoint mapPoint = getMapPoint(sign.getPosition());

        mapPoint.setSign(null);

        signsToRemove.add(sign);

        /* Report that this sign will be removed */
        removedSigns.add(sign);
    }

    private void removeSign(Sign sign) {
        MapPoint mapPoint = getMapPoint(sign.getPosition());

        mapPoint.setSign(null);

        signs.remove(sign);

        /* Report that the sign was removed */
        removedSigns.add(sign);
    }

    void removeWorker(Worker worker) {
        workersToRemove.add(worker);

        /* Report that the worker was removed */
        removedWorkers.add(worker);
    }

    void removeBuilding(Building building) {
        MapPoint mapPoint = getMapPoint(building.getPosition());

        mapPoint.removeBuilding();

        /* Remove planned buildings directly, otherwise add to list and remove in next stepTime() */
        if (building.isPlanned()) {
            buildings.remove(building);
        } else {
            buildingsToRemove.add(building);
        }
    }

    /**
     * Returns the width of the current map.
     *
     * @return the width of the map
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the current map.
     *
     * @return the height of the map
     */
    public int getHeight() {
        return height;
    }

    void placeWorkerFromStepTime(Worker worker, Building home) {
        worker.setPosition(home.getPosition());
        workersToAdd.add(worker);
    }

    void discoverPointsWithinRadius(Player player, Point center, int radius) {
        for (Point point : getPointsWithinRadius(center, radius)) {
            player.discover(point);
        }
    }

    /**
     * Returns the list of players in the game
     *
     * @return The list of players
     */
    public List<Player> getPlayers() {
        return players;
    }

    private double calculateClaim(Building building, Point point) {
        double radius = building.getDefenceRadius();
        double distance = building.getPosition().distance(point);

        return radius / distance;
    }

    private boolean allPlayersHaveUniqueColor() {
        List<Color> usedColors = new ArrayList<>();

        for (Player player : players) {
            if (usedColors.contains(player.getColor())) {
                return false;
            }

            usedColors.add(player.getColor());
        }

        return true;
    }

    /**
     * Tells whether a house can be placed on the given point and if so, what
     * size of house.
     *
     * @param player The player that would build the house
     * @param point The point that the house would be placed on
     * @return The max size of the potential house, otherwise null.
     */
    public Size isAvailableHousePoint(Player player, Point point) {
        return isAvailableHousePoint(player, point, MUST_PLACE_INSIDE_BORDER);
    }

    private Size isAvailableHousePoint(Player player, Point point, BorderCheck borderCheck) {
        Point pointDown = point.down();
        Point pointDownRight = point.downRight();
        Point pointUpRight = point.upRight();
        Point pointUpRightUpRight = point.upRightUpRight();
        Point pointDownLeftDownLeft = point.downLeftDownLeft();
        Point pointDownRightDownRight = point.downRightDownRight();
        Point pointDownLeftLeft = point.downLeftLeft();

        MapPoint houseMapPoint = getMapPoint(point);
        MapPoint mapPointDown = getMapPoint(pointDown);
        MapPoint mapPointDownRight = getMapPoint(pointDownRight);
        MapPoint mapPointUpRight = getMapPoint(pointUpRight);
        MapPoint mapPointUpRightUpRight = getMapPoint(pointUpRightUpRight);
        MapPoint mapPointDownLeftDownLeft = getMapPoint(pointDownLeftDownLeft);
        MapPoint mapPointDownRightDownRight = getMapPoint(pointDownRightDownRight);
        MapPoint mapPointDownLeftLeft = getMapPoint(pointDownLeftLeft);

        /* ALL CONDITIONS FOR SMALL */

        /* Can't build on a point outside the map */
        if (houseMapPoint == null) {
            return null;
        }

        /* The flag point also needs to be on the map */
        if (mapPointDownRight == null) {
            return null;
        }

        /* Make sure all houses except for the headquarters are placed within the player's border */
        if (borderCheck == MUST_PLACE_INSIDE_BORDER && !player.isWithinBorder(point)) {
            return null;
        }

        if (houseMapPoint.isDeadTree()) {
            return null;
        }

        if (houseMapPoint.isBuilding()) {
            return null;
        }

        if (houseMapPoint.isFlag()) {
            return null;
        }

        if (houseMapPoint.isStone()) {
            return null;
        }

        if (houseMapPoint.isTree()) {
            return null;
        }

        if (houseMapPoint.isRoad()) {
            return null;
        }

        if (houseMapPoint.isCrop()) {
            return null;
        }

        /* Check that the surrounding vegetation allows for placing a small house */
        Collection<DetailedVegetation> surroundingVegetation = getSurroundingTiles(point);

        for (DetailedVegetation vegetation : surroundingVegetation) {
            if (!CAN_BUILD_ON.contains(vegetation)) {
                return null;
            }
        }

        /* It's not possible to build a house left/right or diagonally of a stone or building */
        Point pointLeft = point.left();
        Point pointRight = point.right();
        Point pointUpLeft = point.upLeft();
        Point pointDownLeft = point.downLeft();

        MapPoint mapPointLeft = getMapPoint(pointLeft);
        MapPoint mapPointRight = getMapPoint(pointRight);
        MapPoint mapPointUpLeft = getMapPoint(pointUpLeft);
        MapPoint mapPointDownLeft = getMapPoint(pointDownLeft);

        if (mapPointLeft != null && mapPointLeft.isBuilding()) {
            return null;
        }

        if (mapPointLeft != null && mapPointLeft.isStone()) {
            return null;
        }

        if (mapPointUpLeft != null && mapPointUpLeft.isBuilding()) {
            return null;
        }

        if (mapPointUpLeft != null && mapPointUpLeft.isStone()) {
            return null;
        }

        if (mapPointUpRight != null && mapPointUpRight.isBuilding()) {
            return null;
        }

        if (mapPointUpRight != null && mapPointUpRight.isStone()) {
            return null;
        }

        if (mapPointRight != null && mapPointRight.isBuilding()) {
            return null;
        }

        if (mapPointRight != null && mapPointRight.isStone()) {
            return null;
        }

        /* Can't place a building with another building down-right.
        * - No need to verify that mapPointDownRight exists. This is checked before.
        */
        if (mapPointDownRight.isBuilding()) {
            return null;
        }

        /* Can't place a building with a stone down-right
        * - No need to verify that MapPointDownRight exists. This is checked before.
        *   */
        if (mapPointDownRight.isStone()) {
            return null;
        }

        if (mapPointDownLeft != null && mapPointDownLeft.isBuilding()) {
            return null;
        }

        if (mapPointDownLeft != null && mapPointDownLeft.isStone()) {
            return null;
        }

        /* Cannot place a house down-left of a flag */
        if (player.isWithinBorder(pointUpRight) && mapPointUpRight != null && mapPointUpRight.isFlag()) {
            return null;
        }

        /* Cannot place a house down-left-down-left of a large house */
        if (player.isWithinBorder(pointUpRightUpRight) && mapPointUpRightUpRight.isBuildingOfSize(LARGE)) {
            return null;
        }

        /* Cannot place a house above another large house */
        if (player.isWithinBorder(pointDown) && mapPointDown.isBuildingOfSize(LARGE)) {
            return null;
        }

        /* Cannot place a house up-left-left of a large house */
        Point pointDownRightRight = point.downRightRight();
        MapPoint mapPointDownRightRight = getMapPoint(pointDownRightRight);

        if (player.isWithinBorder(pointDownRightRight) && mapPointDownRightRight.isBuildingOfSize(LARGE)) {
            return null;
        }

        /* Can't place a building up-left-up-left of a large building */
        if (mapPointDownRightDownRight != null && mapPointDownRightDownRight.isBuildingOfSize(LARGE)) {
            return null;
        }

        /* Can't place a building up-right-up-right of a large building */
        if (mapPointDownLeftDownLeft != null && mapPointDownLeftDownLeft.isBuildingOfSize(LARGE)) {
            return null;
        }

        /* It must be possible to place a flag for a new building if there isn't already a flag */
        if (!mapPointDownRight.isFlag() && !isAvailableFlagPoint(player, pointDownRight, borderCheck == MUST_PLACE_INSIDE_BORDER)) {
            return null;
        }

        /* ADDITIONAL CONDITIONS FOR MEDIUM */

        /* A large building can't have a tree directly left, right, or diagonally */
        if (mapPointLeft != null && mapPointLeft.isTree()) {
            return SMALL;
        }

        if (mapPointRight != null && mapPointRight.isTree()) {
            return SMALL;
        }

        if (mapPointUpLeft != null && mapPointUpLeft.isTree()) {
            return SMALL;
        }

        if (mapPointUpRight != null && mapPointUpRight.isTree()) {
            return SMALL;
        }

        if (mapPointDownLeft != null && mapPointDownLeft.isTree()) {
            return SMALL;
        }

        /* Can only place a small building if the point down-right is tree
        *  - No need to check that mapPointDownRight exists. This is checked earlier.
        *  */
        if (mapPointDownRight.isTree()) {
            return SMALL;
        }

        /* Can only place small building up-right-right of large building */
        if (mapPointDownLeftLeft != null && mapPointDownLeftLeft.isBuildingOfSize(LARGE)) {
            return SMALL;
        }

        /* ADDITIONAL CONDITIONS FOR LARGE */

        if (player.isWithinBorder(pointUpLeft) && mapPointUpLeft != null && mapPointUpLeft.isFlag()) {
            return MEDIUM;
        }

        if (player.isWithinBorder(pointDown) && mapPointDown.isBuilding()) {
            return MEDIUM;
        }

        if (player.isWithinBorder(pointLeft) && mapPointLeft != null && mapPointLeft.isFlag()) {
            return MEDIUM;
        }

        Point pointUpRightRight = point.upRightRight();
        MapPoint mapPointUpRightRight = getMapPoint(pointUpRightRight);

        if (player.isWithinBorder(pointUpRightRight) && mapPointUpRightRight.isBuilding()) {
            if (!mapPointUpRightRight.isBuildingOfSize(SMALL)) {
                return MEDIUM;
            }
        }

        if (player.isWithinBorder(pointUpRightUpRight) && mapPointUpRightUpRight.isBuilding()) {
            if (!mapPointUpRightUpRight.isBuildingOfSize(SMALL)) {
                return MEDIUM;
            }
        }

        Point pointRightRight = point.rightRight();
        MapPoint mapPointRightRight = getMapPoint(pointRightRight);

        if (player.isWithinBorder(pointRightRight) && mapPointRightRight.isBuildingOfSize(LARGE)) {
            return MEDIUM;
        }

        /* A large building needs free area on the surrounding buildable vegetation */
        DetailedVegetation detailedVegetationUpLeft = getDetailedVegetationUpLeft(point);
        DetailedVegetation detailedVegetationUpRight = getDetailedVegetationUpRight(point);
        DetailedVegetation detailedVegetationDownRight = getDetailedVegetationDownRight(point);
        DetailedVegetation detailedVegetationBelow = getDetailedVegetationBelow(point);
        DetailedVegetation detailedVegetationDownLeft = getDetailedVegetationDownLeft(point);
        DetailedVegetation detailedVegetationAbove = getDetailedVegetationAbove(point);

        if (!CAN_BUILD_ON.contains(detailedVegetationUpLeft) ||
            !CAN_BUILD_ON.contains(detailedVegetationUpRight) ||
            !CAN_BUILD_ON.contains(detailedVegetationDownRight) ||
            !CAN_BUILD_ON.contains(detailedVegetationBelow) ||
            !CAN_BUILD_ON.contains(detailedVegetationDownLeft) ||
            !CAN_BUILD_ON.contains(detailedVegetationAbove)) {
            return MEDIUM;
        }

        /* Large buildings cannot be built if the height difference to close points is too large */
        int heightAtPoint = houseMapPoint.getHeight();
        if (Math.abs(heightAtPoint - mapPointLeft.getHeight())      > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE ||
            Math.abs(heightAtPoint - mapPointUpLeft.getHeight())    > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE ||
            Math.abs(heightAtPoint - mapPointUpRight.getHeight())   > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE ||
            Math.abs(heightAtPoint - mapPointRight.getHeight())     > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE ||
            Math.abs(heightAtPoint - mapPointDownRight.getHeight()) > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE ||
            Math.abs(heightAtPoint - mapPointDownLeft.getHeight())  > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE) {
            return MEDIUM;
        }

        return LARGE;
    }

    /**
     * Returns the stone at the given point if there is one. Otherwise null.
     *
     * @param point The point with the stone
     * @return Stone on the given point
     */
    public Stone getStoneAtPoint(Point point) {
        return getMapPoint(point).getStone();
    }

    /**
     * Returns a list of roads that connect to the given flag
     *
     * @param flag to connect to
     * @return List of roads that connect to the flag
     */
    public List<Road> getRoadsFromFlag(Flag flag) {
        return getMapPoint(flag.getPosition()).getConnectedRoadsAsList();
    }

    /**
     * Tells whether the given point is available to construct a mine on
     *
     * @param player The player who may construct the mine
     * @param point The point that may be available for mine construction
     * @return true if a mine can be constructed on the point
     */
    public boolean isAvailableMinePoint(Player player, Point point) {

        /* Return false if the point is outside the border */
        if (!player.isWithinBorder(point)) {
            return false;
        }

        /* Return false if the point is not on a mountain */
        if (!isOnMineableMountain(point)) {
            return false;
        }

        /* Return false if the point is on a flag */
        MapPoint mapPoint = getMapPoint(point);

        if (mapPoint.isFlag()) {
            return false;
        }

        /* Return false if the point is on a tree */
        if (mapPoint.isTree()) {
            return false;
        }

        /* Return false if the point is on a stone */
        if (mapPoint.isStone()) {
            return false;
        }

        /* Return false if the point is on a road */
        if (mapPoint.isRoad()) {
            return false;
        }

        /* Return false if it's not possible to place a flag */
        Point flagPoint = point.downRight();
        MapPoint mapPointDownRight = getMapPoint(flagPoint);

        if (!mapPointDownRight.isFlag() && !isAvailableFlagPoint(player, flagPoint)) {
            return false;
        }

        return true;
    }

    /**
     * Returns the available mine points for the given player
     *
     * @param player The player to return mine points for
     * @return List of available mine points
     */
    public List<Point> getAvailableMinePoints(Player player) {

        List<Point> availableMinePoints = new LinkedList<>();

        /* Find available points for mine in the owned land

           This iterates over a collection and the order is non-deterministic
        */
        for (Point point : player.getLandInPoints()) {

            /* Add the point if it's possible to build a mine there */
            if (isAvailableMinePoint(player, point)) {
                availableMinePoints.add(point);
            }
        }

        return availableMinePoints;
    }

    /**
     * Returns a list of the projectiles
     * @return List of projectiles
     */
    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    void placeProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }

    void removeProjectileFromWithinStepTime(Projectile projectile) {
        projectilesToRemove.add(projectile);
    }

    /**
     * Returns a list with the wild animals
     * @return List of wild animals
     */
    public List<WildAnimal> getWildAnimals() {
        return wildAnimals;
    }

    /**
     * Places a wild animal at the given point
     *
     * @param point The point to place the new wild animal at
     * @return The placed wild animal
     */
    public WildAnimal placeWildAnimal(Point point) {

        /* Place the new wild animal */
        WildAnimal animal = new WildAnimal(this);

        animal.setPosition(point);
        wildAnimals.add(animal);

        return animal;
    }

    /**
     * Places a wild animal at the given point
     *
     * @param point The point to place the new wild animal at
     * @return The placed wild animal
     */
    public WildAnimal placeWildAnimal(Point point, WildAnimal.Type type) {

        /* Place the new wild animal */
        WildAnimal animal = new WildAnimal(this, type);

        animal.setPosition(point);
        wildAnimals.add(animal);

        return animal;
    }

    private void handleWildAnimalPopulation() {

        double density = (double)wildAnimals.size() / (width * height);

        if (density < WILD_ANIMAL_NATURAL_DENSITY) {
            if (animalCountdown.hasReachedZero()) {

                /* Find point to place new wild animal on */
                Point point = findRandomPossiblePointToPlaceFreeMovingActor();

                if (point == null) {
                    return;
                }

                /* Place the new wild animal */
                placeWildAnimal(point);

                animalCountdown.countFrom(WILD_ANIMAL_TIME_BETWEEN_REPOPULATION);
            } else if (!animalCountdown.isActive()) {
                animalCountdown.countFrom(WILD_ANIMAL_TIME_BETWEEN_REPOPULATION);
            } else {
                animalCountdown.step();
            }
        }
    }

    private Point findRandomPossiblePointToPlaceFreeMovingActor() {

        /* Pick centered point randomly */
        double x = random.nextDouble() * getWidth();
        double y = random.nextDouble() * getHeight();

        Point point = getClosestPoint(x, y);

        /* Go through the full map and look for a suitable point */
        for (Point p : getPointsWithinRadius(point, LOOKUP_RANGE_FOR_FREE_ACTOR)) {

            MapPoint mapPoint = getMapPoint(p);

            /* Filter buildings */
            if (mapPoint.isBuilding()) {
                continue;
            }

            /* Filter stones */
            if (mapPoint.isStone()) {
                continue;
            }

            /* Filter terrain the animal can't walk on */
            if (WildAnimal.cannotWalkOnAny(getSurroundingTiles(p))) {
                continue;
            }

            return p;
        }

        return null;
    }

    void removeWildAnimalWithinStepTime(WildAnimal animal) {
        animalsToRemove.add(animal);
    }

    void removeCropWithinStepTime(Crop crop) {
        cropsToRemove.add(crop);

        getMapPoint(crop.getPosition()).setCrop(null);

        /* Report that the crop was removed */
        removedCrops.add(crop);
    }

    void replaceBuilding(Building upgradedBuilding, Point position) {

        MapPoint mapPoint = getMapPoint(position);

        /* Plan to remove the pre-upgrade building */
        Building oldBuilding = mapPoint.getBuilding();
        buildingsToRemove.add(oldBuilding);

        /* Put the upgraded building in place  */
        upgradedBuilding.setPosition(position);
        upgradedBuilding.setFlag(oldBuilding.getFlag());

        /* Update the map point */
        mapPoint.removeBuilding();
        mapPoint.setBuilding(upgradedBuilding);

        /* Update the player's list of buildings */
        upgradedBuilding.getPlayer().removeBuilding(oldBuilding);
        upgradedBuilding.getPlayer().addBuilding(upgradedBuilding);

        /* Plan to add the upgraded building */
        buildingsToAdd.add(upgradedBuilding);

        /* Report that the building has been upgraded */
        upgradedBuildings.add(new GameChangesList.NewAndOldBuilding(oldBuilding, upgradedBuilding));
    }

    /**
     * Returns the winning player of the game if there is a winner. Otherwise null.
     * @return Returns the winning player of the game if there is a winner. Otherwise null.
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Determines whether two points are connected by roads.
     *
     * @param start The point to start form
     * @param end The point to reach
     * @return true if the two points are connected by roads.
     */
    public boolean arePointsConnectedByRoads(Point start, Point end) {
        return GameUtils.arePointsConnectedByRoads(start, end, this);
    }

    /**
     * Finds the shortest path following roads between two points. The points must be flags or houses.
     *
     * @param start The flag or building to start from
     * @param end The flag or building to reach
     * @return the list of points with flags or buildings to pass by
     */
    //FIXME: ALLOCATION HOTSPOT
    public List<Point> findWayWithExistingRoadsInFlagsAndBuildings(EndPoint start, EndPoint end, Point... avoid) {
        return findShortestPathViaRoads(start.getPosition(), end.getPosition(), this, avoid);
    }

    public boolean isValidRouteThroughFlagsAndBuildingsViaRoads(Point... points) {
        return isValidRouteThroughFlagsAndBuildingsViaRoads(Arrays.asList(points));
    }

    public boolean isValidRouteThroughFlagsAndBuildingsViaRoads(List<Point> points) {

        Point previous = null;

        for (Point point : points) {

            if (previous != null) {
                MapPoint mp = getMapPoint(previous);

                if (!mp.getConnectedFlagsAndBuildings().contains(point)) {
                    return false;
                }
            }

            previous = point;
        }

        return true;
    }

    /**
     * Determines if two points with flags or buildings are connected by roads
     *
     * @param from A flag or building
     * @param to A flag or building
     * @return true if the given endpoints are connected
     */
    public boolean areFlagsOrBuildingsConnectedViaRoads(EndPoint from, EndPoint to) {
        return areBuildingsOrFlagsConnected(from, to, this);
    }

    /**
     * Returns the height of the given point
     *
     * @param point The point to get the height for
     * @return The height of the given point
     */
    public int getHeightAtPoint(Point point) {
        return getMapPoint(point).getHeight();
    }

    /**
     * Sets the height at the given point
     *
     * @param point The point to set the height for
     * @param height The height to set at the given point
     */
    public void setHeightAtPoint(Point point, int height) {
        getMapPoint(point).setHeight(height);
    }

    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    public long getCurrentTime() {
        return time;
    }

    void reportWorkerWithNewTarget(Worker worker) {
        workersWithNewTargets.add(worker);
    }

    private <T extends Building> void reportPlacedBuilding(T house) {
        newBuildings.add(house);
    }

    private void reportPlacedFlag(Flag flag) {
        newFlags.add(flag);
    }

    private void reportRemovedFlag(Flag flag) {
        removedFlags.add(flag);
    }

    void reportTornDownBuilding(Building building) {
        changedBuildings.add(building);
    }

    void reportBuildingBurnedDown(Building building) {
        changedBuildings.add(building);
    }

    void reportBuildingRemoved(Building building) {
        removedBuildings.add(building);
    }

    void reportWorkerEnteredBuilding(Worker worker) {
        removedWorkers.add(worker);
    }

    public void reportPromotedRoad(Road road) {
        promotedRoads.add(road);
    }

    public void setMineralAmount(Point point, Material mineral, Size amount) {
        MapPoint mapPoint = getMapPoint(point);

        mapPoint.setMineralAmount(mineral, amount);
    }

    /**
     * Changes the tiles surrounding the given point to contain large amounts of
     * the given mineral.
     *
     * @param point Point to surround with large quantities of mineral
     * @param mineral The type of mineral
     */
    public void surroundPointWithMineral(Point point, Material mineral) {
        surroundPointWithMineral(point, mineral, LARGE);
    }

    public void surroundPointWithMineral(Point point, Material mineral, Size amount) {
        MapPoint mapPoint = getMapPoint(point);
        MapPoint mapPointDownLeft = getMapPoint(point.downLeft());
        MapPoint mapPointLeft = getMapPoint(point.left());
        MapPoint mapPointUpLeft = getMapPoint(point.upLeft());
        MapPoint mapPointAbove = getMapPoint(point.up());
        MapPoint mapPointUpRight = getMapPoint(point.upRight());
        MapPoint mapPointRight = getMapPoint(point.right());
        MapPoint mapPointDownRight = getMapPoint(point.downRight());
        MapPoint mapPointBelow = getMapPoint(point.down());

        mapPoint.setMineralAmount(mineral, amount);
        mapPointDownLeft.setMineralAmount(mineral, amount);
        mapPointLeft.setMineralAmount(mineral, amount);
        mapPointUpLeft.setMineralAmount(mineral, amount);
        mapPointAbove.setMineralAmount(mineral, amount);
        mapPointUpRight.setMineralAmount(mineral, amount);
        mapPointRight.setMineralAmount(mineral, amount);
        mapPointDownRight.setMineralAmount(mineral, amount);
        mapPointBelow.setMineralAmount(mineral, amount);
    }

    public boolean isNextToAnyWater(Point point) {

        DetailedVegetation detailedVegetationUpLeft = getDetailedVegetationUpLeft(point);
        DetailedVegetation detailedVegetationAbove = getDetailedVegetationAbove(point);
        DetailedVegetation detailedVegetationUpRight = getDetailedVegetationUpRight(point);
        DetailedVegetation detailedVegetationDownRight = getDetailedVegetationDownRight(point);
        DetailedVegetation detailedVegetationBelow = getDetailedVegetationBelow(point);
        DetailedVegetation detailedVegetationDownLeft = getDetailedVegetationDownLeft(point);

        if (WATER_VEGETATION.contains(detailedVegetationUpLeft)) {
            return true;
        }

        if (WATER_VEGETATION.contains(detailedVegetationAbove)) {
            return true;
        }

        if (WATER_VEGETATION.contains(detailedVegetationUpRight)) {
            return true;
        }

        if (WATER_VEGETATION.contains(detailedVegetationDownRight)) {
            return true;
        }

        if (WATER_VEGETATION.contains(detailedVegetationBelow)) {
            return true;
        }

        if (WATER_VEGETATION.contains(detailedVegetationDownLeft)) {
            return true;
        }

        return false;
    }

    /**
     * Returns true if the given point is surrounded by mountain tiles
     *
     * @param point Point that may be on a mineable mountain
     * @return True if the given point is on a mineable mountain, otherwise false
     */
    public boolean isOnMineableMountain(Point point) {

        DetailedVegetation detailedVegetationUpLeft = getDetailedVegetationUpLeft(point);
        DetailedVegetation detailedVegetationAbove = getDetailedVegetationAbove(point);
        DetailedVegetation detailedVegetationUpRight = getDetailedVegetationUpRight(point);
        DetailedVegetation detailedVegetationDownRight = getDetailedVegetationDownRight(point);
        DetailedVegetation detailedVegetationBelow = getDetailedVegetationBelow(point);
        DetailedVegetation detailedVegetationDownLeft = getDetailedVegetationDownLeft(point);

        if (MINABLE_MOUNTAIN.contains(detailedVegetationUpLeft)    &&
            MINABLE_MOUNTAIN.contains(detailedVegetationAbove)     &&
            MINABLE_MOUNTAIN.contains(detailedVegetationUpRight)   &&
            MINABLE_MOUNTAIN.contains(detailedVegetationDownRight) &&
            MINABLE_MOUNTAIN.contains(detailedVegetationBelow)     &&
            MINABLE_MOUNTAIN.contains(detailedVegetationDownLeft)) {
            return true;
        }

        return false;
    }

    /**
     * Returns true if the given point is surrounded by water tiles
     *
     * @param point Point that may be surrounded by water
     * @return True if the given point is surrounded by water, otherwise false
     */
    public boolean isInWater(Point point) {

        DetailedVegetation detailedVegetationUpLeft = getDetailedVegetationUpLeft(point);
        DetailedVegetation detailedVegetationAbove = getDetailedVegetationAbove(point);
        DetailedVegetation detailedVegetationUpRight = getDetailedVegetationUpRight(point);
        DetailedVegetation detailedVegetationDownRight = getDetailedVegetationDownRight(point);
        DetailedVegetation detailedVegetationBelow = getDetailedVegetationBelow(point);
        DetailedVegetation detailedVegetationDownLeft = getDetailedVegetationDownLeft(point);

        if (WATER_VEGETATION.contains(detailedVegetationUpLeft)    &&
            WATER_VEGETATION.contains(detailedVegetationAbove)     &&
            WATER_VEGETATION.contains(detailedVegetationUpRight)   &&
            WATER_VEGETATION.contains(detailedVegetationDownRight) &&
            WATER_VEGETATION.contains(detailedVegetationBelow)     &&
            WATER_VEGETATION.contains(detailedVegetationDownLeft)) {
            return true;
        }

        return false;
    }

    /**
     * Surrounds the given point with the chosen type of vegetation
     *
     * @param point Point to surround with vegetation
     * @param vegetation Vegetation to surround the point with
     */
    public void surroundWithVegetation(Point point, DetailedVegetation vegetation) {
        setDetailedVegetationUpLeft(point, vegetation);
        setDetailedVegetationAbove(point, vegetation);
        setDetailedVegetationUpRight(point, vegetation);
        setDetailedVegetationDownRight(point, vegetation);
        setDetailedVegetationBelow(point, vegetation);
        setDetailedVegetationDownLeft(point, vegetation);
    }

    boolean isSurroundedBy(Point point, DetailedVegetation vegetation) {

        return getDetailedVegetationUpLeft(point)    == vegetation &&
               getDetailedVegetationAbove(point)     == vegetation &&
               getDetailedVegetationUpRight(point)   == vegetation &&
               getDetailedVegetationDownRight(point) == vegetation &&
               getDetailedVegetationBelow(point)     == vegetation &&
               getDetailedVegetationDownLeft(point)  == vegetation;
    }

    /**
     * Returns a list of the tiles surrounding the given point
     *
     * @param point Point to get surrounding tiles for
     * @return List of tiles surrounding the given point
     */
    public List<DetailedVegetation> getSurroundingTiles(Point point) {
        List<DetailedVegetation> result = new LinkedList<>();

        DetailedVegetation detailedVegetationUpLeft = getDetailedVegetationUpLeft(point);
        DetailedVegetation detailedVegetationAbove = getDetailedVegetationAbove(point);
        DetailedVegetation detailedVegetationUpRight = getDetailedVegetationUpRight(point);
        DetailedVegetation detailedVegetationDownRight = getDetailedVegetationDownRight(point);
        DetailedVegetation detailedVegetationBelow = getDetailedVegetationBelow(point);
        DetailedVegetation detailedVegetationDownLeft = getDetailedVegetationDownLeft(point);


        if (detailedVegetationUpLeft != null) {
            result.add(detailedVegetationUpLeft);
        }

        if (detailedVegetationAbove != null) {
            result.add(detailedVegetationAbove);
        }

        if (detailedVegetationUpRight != null) {
            result.add(detailedVegetationUpRight);
        }

        if (detailedVegetationDownRight != null) {
            result.add(detailedVegetationDownRight);
        }

        if (detailedVegetationBelow != null) {
            result.add(detailedVegetationBelow);
        }

        if (detailedVegetationDownLeft != null) {
            result.add(detailedVegetationDownLeft);
        }

        return result;
    }

    /**
     * Returns true if the given point is on vegetation where houses can be built
     *
     * @param point Point that may be on buildable land
     * @return True if the given point is on buildable land, otherwise false
     */
    public boolean isOnBuildable(Point point) {

        DetailedVegetation detailedVegetationUpLeft = getDetailedVegetationUpLeft(point);
        DetailedVegetation detailedVegetationAbove = getDetailedVegetationAbove(point);
        DetailedVegetation detailedVegetationUpRight = getDetailedVegetationUpRight(point);
        DetailedVegetation detailedVegetationDownRight = getDetailedVegetationDownRight(point);
        DetailedVegetation detailedVegetationBelow = getDetailedVegetationBelow(point);
        DetailedVegetation detailedVegetationDownLeft = getDetailedVegetationDownLeft(point);

        return CAN_BUILD_ON.contains(detailedVegetationUpLeft)    &&
               CAN_BUILD_ON.contains(detailedVegetationAbove)     &&
               CAN_BUILD_ON.contains(detailedVegetationUpRight)   &&
               CAN_BUILD_ON.contains(detailedVegetationDownRight) &&
               CAN_BUILD_ON.contains(detailedVegetationBelow)     &&
               CAN_BUILD_ON.contains(detailedVegetationDownLeft);
    }

    public void fillMapWithVegetation(DetailedVegetation detailedVegetation) {
        tileBelowMap.replaceAll((k, v) -> detailedVegetation);

        tileDownRightMap.replaceAll((k, v) -> detailedVegetation);
    }

    public Stats getStats() {
        return stats;
    }

    void reportChangedFlag(Flag flag) {
        changedFlags.add(flag);
    }

    public List<Point> findDetailedWayWithExistingRoadsInFlagsAndBuildings(EndPoint start, Building end, Point... avoid) {
        return findShortestDetailedPathViaRoads(start, end, this, avoid);
    }

    public List<Point> getDeadTrees() {
        return deadTrees;
    }

    public void placeDeadTree(Point point) throws InvalidUserActionException {
        MapPoint mapPoint = getMapPoint(point);

        if (mapPoint.isBuilding()) {
            throw new InvalidUserActionException("Can't place dead tree on building");
        }

        if (mapPoint.isFlag()) {
            throw new InvalidUserActionException("Can't place dead tree on a flag");
        }

        if (mapPoint.isRoad()) {
            throw new InvalidUserActionException("Can't place dead tree on road");
        }

        if (mapPoint.isTree()) {
            throw new InvalidUserActionException("Can't place dead tree on tree");
        }

        if (mapPoint.isStone()) {
            throw new InvalidUserActionException("Can't place dead tree on stone");
        }

        // TODO: make "allow list" instead of "disallow list". One of allow tiles surrounding the tree should be enough
        for (DetailedVegetation vegetation : getSurroundingTiles(point)) {
            if (DEAD_TREE_NOT_ALLOWED.contains(vegetation)) {
                throw new InvalidUserActionException("Can't place dead tree on " + vegetation);
            }
        }

        mapPoint.setDeadTree();

        deadTrees.add(point);
    }

    public boolean isDeadTree(Point point) {
        MapPoint mapPoint = getMapPoint(point);

        return mapPoint.isDeadTree();
    }

    public DetailedVegetation getDetailedVegetationDownLeft(Point point) {
        return tileDownRightMap.get(point.y * width + point.x - 2);
    }

    public void setDetailedVegetationDownLeft(Point point, DetailedVegetation detailedVegetation) {
        tileDownRightMap.put(point.y * width + point.x - 2, detailedVegetation);
    }

    public void setDetailedVegetationUpLeft(Point point, DetailedVegetation detailedVegetation) {
        tileBelowMap.put((point.y + 1) * width + point.x - 1, detailedVegetation);
    }

    public void setDetailedVegetationAbove(Point point, DetailedVegetation detailedVegetation) {
        tileDownRightMap.put((point.y + 1) * width + point.x - 1, detailedVegetation);
    }

    public void setDetailedVegetationUpRight(Point point, DetailedVegetation detailedVegetation) {
        tileBelowMap.put((point.y + 1) * width + point.x + 1, detailedVegetation);
    }

    public void setDetailedVegetationDownRight(Point point, DetailedVegetation detailedVegetation) {
        tileDownRightMap.put(point.y * width + point.x, detailedVegetation);
    }

    public void setDetailedVegetationBelow(Point point, DetailedVegetation detailedVegetation) {
        tileBelowMap.put(point.y * width + point.x, detailedVegetation);
    }

    public DetailedVegetation getDetailedVegetationUpLeft(Point point) {
        return tileBelowMap.get((point.y + 1) * width + point.x - 1);
    }

    public DetailedVegetation getDetailedVegetationAbove(Point point) {
        return tileDownRightMap.get((point.y + 1) * width + point.x - 1);
    }

    public DetailedVegetation getDetailedVegetationUpRight(Point point) {
        return tileBelowMap.get((point.y + 1) * width + point.x + 1);
    }

    public DetailedVegetation getDetailedVegetationDownRight(Point point) {
        return tileDownRightMap.get(point.y * width + point.x);
    }

    public DetailedVegetation getDetailedVegetationBelow(Point point) {
        return tileBelowMap.get(point.y * width + point.x);
    }

    void reportBuildingUnderConstruction(Building building) {
        changedBuildings.add(building);
    }

    public List<Ship> getShips() {
        return ships;
    }

    public Ship placeShip(Player player, Point point) {
        Ship ship = new Ship(player, this);

        ship.setPosition(point);

        ships.add(ship);

        MapPoint mapPoint = getMapPoint(point);

        mapPoint.setShipUnderConstruction();

        newShips.add(ship);

        return ship;
    }

    public boolean isAvailableHarborPoint(Point point) {
        MapPoint mapPoint = getMapPoint(point);

        return mapPoint.isHarborPossible();
    }

    public void setPossiblePlaceForHarbor(Point point) throws InvalidUserActionException {

        /* The building and the flag can't be completely surrounded by water */
        // TODO: check for all types of non-buildable terrain as well
        if (isAny(getSurroundingTiles(point), WATER)) {
            throw new InvalidUserActionException("Can't mark a possible point at " + point + " for harbor without access to land");
        }

        MapPoint mapPoint = getMapPoint(point);

        mapPoint.setHarborIsPossible();

        possiblePlacesForHarbor.add(point);
    }

    void reportShipReady(Ship ship) {
        MapPoint mapPoint = getMapPoint(ship.getPosition());

        mapPoint.setShipDone();

        finishedShips.add(ship);
    }

    public Set<Point> getPossiblePlacesForHarbor() {
        return possiblePlacesForHarbor;
    }

    public List<Point> findWayForShip(Point from, Point to) {
        return findShortestPath(from, to, null, new ConnectionsProvider() {

            @Override
            public Iterable<Point> getPossibleConnections(Point point, Point goal) {
                Set<Point> possibleConnections = new HashSet<>();

                DetailedVegetation vegetationUpLeft = getDetailedVegetationUpLeft(point);
                DetailedVegetation vegetationAbove = getDetailedVegetationAbove(point);
                DetailedVegetation vegetationUpRight = getDetailedVegetationUpRight(point);
                DetailedVegetation vegetationDownRight = getDetailedVegetationDownRight(point);
                DetailedVegetation vegetationBelow = getDetailedVegetationBelow(point);
                DetailedVegetation vegetationDownLeft = getDetailedVegetationDownLeft(point);

                Point pointLeft = point.left();
                if (isWithinMap(pointLeft) && (vegetationUpLeft == WATER || vegetationDownLeft == WATER)) {
                    possibleConnections.add(pointLeft);
                }

                Point pointUpLeft = point.upLeft();
                if (isWithinMap(pointUpLeft) && (vegetationUpLeft == WATER || vegetationAbove == WATER)) {
                    possibleConnections.add(pointUpLeft);
                }

                Point pointUpRight = point.upRight();
                if (isWithinMap(pointUpRight) && (vegetationAbove == WATER || vegetationUpRight == WATER)) {
                    possibleConnections.add(pointUpRight);
                }

                Point pointRight = point.right();
                if (isWithinMap(pointRight) && (vegetationUpRight == WATER || vegetationDownRight == WATER)) {
                    possibleConnections.add(pointRight);
                }

                Point pointDownRight = point.downRight();
                if (isWithinMap(pointDownRight) && (vegetationDownRight == WATER || vegetationBelow == WATER)) {
                    possibleConnections.add(pointDownRight);
                }

                Point pointDownLeft = point.downLeft();
                if (isWithinMap(pointDownLeft) && (vegetationBelow == WATER || vegetationDownLeft == WATER)) {
                    possibleConnections.add(pointDownLeft);
                }

                return possibleConnections;
            }

            @Override
            public int realDistance(Point currentPoint, Point neighbor) {
                return 1;
            }
        });
    }

    void reportHarvestedCrop(Crop crop) {
        harvestedCrops.add(crop);
    }

    void reportShipWithNewTarget(Ship ship) {
        shipsWithNewTargets.add(ship);
    }

    public void reportWorkerStartedAction(Worker worker, WorkerAction action) {
        workersWithStartedActions.put(worker, action);
    }

    public void placeDecoration(Point point, DecorationType decoration) {
        MapPoint mapPoint = getMapPoint(point);

        mapPoint.setDecoration(decoration);

        decorations.put(point, decoration);
    }

    public boolean isDecoratedAtPoint(Point point) {
        return decorations.containsKey(point);
    }

    public DecorationType getDecorationAtPoint(Point point) {
        return decorations.get(point);
    }

    public Map<Point, DecorationType> getDecorations() {
        return decorations;
    }

    private void removeDecorationAtPoint(Point point) {
        MapPoint mapPoint = getMapPoint(point);

        mapPoint.removeDecoration();

        decorations.remove(point);

        removedDecorations.add(point);
    }

    public boolean isSurroundedByNonWalkableTerrain(Point point) {
        return getSurroundingTiles(point).stream().noneMatch(DetailedVegetation::canWalkOn);
    }
}
