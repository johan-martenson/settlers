package org.appland.settlers.model;

import org.appland.settlers.model.GameUtils.ConnectionsProvider;
import org.appland.settlers.policy.Constants;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.appland.settlers.model.Crop.GrowthState.HARVESTED;
import static org.appland.settlers.model.GameUtils.findShortestPath;
import static org.appland.settlers.model.GameUtils.getDistanceInGameSteps;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.MEDIUM;
import static org.appland.settlers.model.Size.SMALL;
import static org.appland.settlers.model.Tile.Vegetation.MOUNTAIN;

public class GameMap {

    private static final int MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE = 3;

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
    private final Terrain              terrain;
    private final Map<Point, MapPoint> pointToGameObject;
    private final List<Tree>           trees;
    private final List<Stone>          stones;
    private final List<Worker>         workersToAdd;
    private final List<Player>         players;
    private final Random               random;
    private final List<Point>          startingPoints;
    private final ConnectionsProvider  pathOnExistingRoadsProvider;
    private final ConnectionsProvider  connectedFlagsAndBuildingsProvider;

    private final String theLeader = "Anh Mai Mårtensson";
    private final int MINIMUM_WIDTH  = 5;
    private final int MINIMUM_HEIGHT = 5;
    private final int LOOKUP_RANGE_FOR_FREE_ACTOR = 10;
    private final int statisticsCollectionPeriod;

    private Player winner;
    private final StatisticsManager statisticsManager;
    private long time;
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

    PointInformation whatIsAtPoint(Point point) {
        MapPoint mp = getMapPoint(point);

        if (mp == null) {
            return PointInformation.OUTSIDE_MAP;
        }

        if (mp.isTree()) {
            return PointInformation.TREE;
        }

        if (mp.isStone()) {
            return PointInformation.STONE;
        }

        if (mp.isFlag()) {

            if (mp.isRoad()) {
                return PointInformation.FLAG_AND_ROADS;
            }

            return PointInformation.FLAG;
        }

        if (mp.isBuilding()) {
            return PointInformation.BUILDING;
        }

        if (mp.isRoad()) {
            return PointInformation.ROAD;
        }

        if (mp.isSign()) {
            return PointInformation.SIGN;
        }

        if (mp.isCrop()) {
            return PointInformation.CROP;
        }

        return PointInformation.NONE;
    }

    enum PointInformation {
        NONE,
        STONE, FLAG, BUILDING, ROAD, FLAG_AND_ROADS, SIGN, CROP, TREE, OUTSIDE_MAP;
    }

    /**
     * Creates a new game map
     *
     * @param players The players in the new game
     * @param width The width of the new game map
     * @param height The height of the new game map
     * @throws Exception An exception is thrown if the given width and height are too small or too large
     */
    public GameMap(List<Player> players, int width, int height) throws Exception {

        if (players.isEmpty()) {
            throw new Exception("Can't create game map with no players");
        }

        this.players = players;
        this.width = width;
        this.height = height;

        if (width < MINIMUM_WIDTH || height < MINIMUM_HEIGHT) {
            throw new Exception("Can't create too small map (" + width + "x" + height + ")");
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
        terrain             = new Terrain(width, height);
        trees               = new ArrayList<>();
        stones              = new ArrayList<>();
        crops               = new ArrayList<>();
        workersToAdd        = new LinkedList<>();
        animalCountdown     = new Countdown();
        random              = new Random();
        startingPoints      = new ArrayList<>();

        statisticsManager   = new StatisticsManager();

        pointToGameObject   = populateMapPoints(buildFullGrid());

        pathOnExistingRoadsProvider = new GameUtils.PathOnExistingRoadsProvider(pointToGameObject);
        connectedFlagsAndBuildingsProvider = new GameUtils.ConnectedFlagsAndBuildingsProvider(pointToGameObject);

        /* Add initial measurement */
        statisticsManager.addZeroInitialMeasurementForPlayers(players);

        /* Set the time keeper to 1 */
        time = 1;

        /* Set the initial production statistics collection period */
        statisticsCollectionPeriod = 500;

        /* Give the players a reference to the map */
        for (Player player : players) {
            player.setMap(this);
        }

        /* Verify that all players have unique colors */
        if (!allPlayersHaveUniqueColor()) {
            throw new Exception("Each player must have a unique color");
        }

        /* Set a constant initial seed for the random generator to get a deterministic behavior */
        random.setSeed(1);

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
    public List<Point> findAutoSelectedRoad(final Player player, Point start,
                                            Point goal, Collection<Point> avoid) {
        return findShortestPath(start, goal, avoid, new GameUtils.ConnectionsProvider() {

            @Override
            public Iterable<Point> getPossibleConnections(Point point, Point goal) {
                try {
                    return getPossibleAdjacentRoadConnections(player, point, goal);
                } catch (Exception ex) {
                    Logger.getLogger(GameMap.class.getName()).log(Level.SEVERE, null, ex);
                }

                return new LinkedList<>();
            }

            @Override
            public Double realDistance(Point currentPoint, Point neighbor) {
                return (double)1;
            }

            @Override
            public Double estimateDistance(Point from, Point to) {
                return (double)getDistanceInGameSteps(from, to);
            }
        });
    }

    private boolean pointIsOnRoad(Point point) {
        return getRoadAtPoint(point) != null;
    }

    /**
     * Returns a road that covers the given point but does not start and end at it
     *
     * @param point A point on the map
     * @return Returns the road if there is a road at the given point
     */
    public Road getRoadAtPoint(Point point) {

        /* Don't include start and end points of roads so ignore the point if there is a flag */
        if (isFlagAtPoint(point)) {
            return null;
        }

        Set<Road> connectedRoads = getMapPoint(point).getConnectedRoads();

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
     * @throws Exception If there is a failure in making the courier return to storage or removing the road
     */
    public void removeRoad(Road road) throws Exception {

        if (road.getCourier() != null) {
            road.getCourier().returnToStorage();
        }

        removeRoadButNotWorker(road);
    }

    private void removeRoadButNotWorker(Road road) throws Exception {

        roads.remove(road);

        for (Point point : road.getWayPoints()) {
            MapPoint mp = pointToGameObject.get(point);

            mp.removeConnectingRoad(road);
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
     * Returns the possible starting points of the map. A starting point is where a headquarter can be placed
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
     * @throws Exception Any exception encountered while updating the game
     */
    public void stepTime() throws Exception {

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

        for (Worker worker : workers) {
            worker.stepTime();
        }

        for (Building building : buildings) {
            building.stepTime();
        }

        for (Tree tree : trees) {
            tree.stepTime();
        }

        for (Crop crop : crops) {
            crop.stepTime();
        }

        for (Sign sign : signs) {
            sign.stepTime();
        }

        for (WildAnimal wildAnimal : wildAnimals) {
            wildAnimal.stepTime();
        }

        /* Possibly add wild animals */
        handleWildAnimalPopulation();

        /* Remove completely mined stones */
        List<Stone> stonesToRemove = new ArrayList<>();
        for (Stone stone : stones) {
            if (stone.noMoreStone()) {
                stonesToRemove.add(stone);
            }
        }

        for (Stone stone : stonesToRemove) {
            removeStone(stone);
        }

        /* Resume transport of stuck cargo */
        for (Flag flag : flags) {
            for (Cargo cargo : flag.getStackedCargo()) {
                cargo.rerouteIfNeeded();
            }
        }

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
        }

        /* Collect statistics */
        if (time % statisticsCollectionPeriod == 0) {
            statisticsManager.collectFromPlayers(time, players);
        }

        /* Notify the players that one more step has been done */
        for (Player player : players) {
            player.manageTreeConservationProgram();
        }

        /* Add worker events to the players if any */
        List<BorderChange> borderChanges = null;
        for (Player player : players) {

            if (!player.hasMonitor()) {
                continue;
            }

            if (borderChanges == null) {
                borderChanges = collectBorderChangesFromEachPlayer();
            }

            player.reportChangedBorders(borderChanges);

            for (Worker worker : workersWithNewTargets) {
                if (!player.getDiscoveredLand().contains(worker.getPosition())) {
                    continue;
                }

                player.reportWorkerWithNewTarget(worker);
            }

            for (Worker worker : removedWorkers) {
                if (!player.getDiscoveredLand().contains(worker.getPosition())) {
                    continue;
                }

                player.reportRemovedWorker(worker);
            }

            for (Building building : changedBuildings) {

                if (!player.getDiscoveredLand().contains(building.getPosition())) {
                    continue;
                }

                player.reportChangedBuilding(building);
            }

            for (Building building : removedBuildings) {
                if (!player.getDiscoveredLand().contains(building.getPosition())) {
                    continue;
                }

                player.reportRemovedBuilding(building);
            }

            for (Flag flag : newFlags) {
                if (!player.getDiscoveredLand().contains(flag.getPosition())) {
                    continue;
                }

                player.reportNewFlag(flag);
            }

            for (Flag flag : removedFlags) {
                if (!player.getDiscoveredLand().contains(flag.getPosition())) {
                    continue;
                }

                player.reportRemovedFlag(flag);
            }

            for (Road road : newRoads) {
                if (!GameUtils.setContainsAny(player.getDiscoveredLand(), road.getWayPoints())) {
                    continue;
                }

                player.reportNewRoad(road);
            }

            for (Road road : removedRoads) {
                if (!GameUtils.setContainsAny(player.getDiscoveredLand(), road.getWayPoints())) {
                    continue;
                }

                player.reportRemovedRoad(road);
            }

            for (Building building : newBuildings) {
                if (!player.getDiscoveredLand().contains(building.getPosition())) {
                    continue;
                }

                player.reportNewBuilding(building);
            }

            for (Tree tree : newTrees) {
                if (!player.getDiscoveredLand().contains(tree.getPosition())) {
                    continue;
                }

                player.reportNewTree(tree);
            }

            for (Tree tree : removedTrees) {
                if (!player.getDiscoveredLand().contains(tree.getPosition())) {
                    continue;
                }

                player.reportRemovedTree(tree);
            }

            for (Stone stone : removedStones) {
                if (!player.getDiscoveredLand().contains(stone.getPosition())) {
                    continue;
                }

                player.reportRemovedStone(stone);
            }

            for (Sign sign : newSigns) {
                if (!player.getDiscoveredLand().contains(sign.getPosition())) {
                    continue;
                }

                player.reportNewSign(sign);
            }

            for (Sign sign : removedSigns) {
                if (!player.getDiscoveredLand().contains(sign.getPosition())) {
                    continue;
                }

                player.reportRemovedSign(sign);
            }

            for (Crop crop : newCrops) {
                if (!player.getDiscoveredLand().contains(crop.getPosition())) {
                    continue;
                }

                player.reportNewCrop(crop);
            }

            for (Crop crop : removedCrops) {
                if (!player.getDiscoveredLand().contains(crop.getPosition())) {
                    continue;
                }

                player.reportRemovedCrop(crop);
            }

            player.sendMonitoringEvents(time);
        }

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

        /* Step the time keeper */
        time = time + 1;
    }

    private List<BorderChange> collectBorderChangesFromEachPlayer() {
        List<BorderChange> borderChanges;
        borderChanges = new ArrayList<>();

        for (Player player1 : players) {
            BorderChange borderChange = player1.getBorderChange();

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
     * @throws Exception Any exceptions encountered while placing the building
     */
    public <T extends Building> T placeBuilding(T house, Point point) throws Exception {

        boolean firstHouse = false;

        /* Verify that the building is not already placed on the map */
        if (buildings.contains(house)) {
            throw new InvalidUserActionException("Can't place " + house + " as it is already placed.");
        }

        /* Verify that the house's player is valid */
        if (!players.contains(house.getPlayer())) {
            throw new InvalidGameLogicException("Can't place " + house + ", player " + house.getPlayer() + " is not valid.");
        }

        /* Handle the first building separately */
        if (house.getPlayer().getBuildings().isEmpty()) {
            if (! (house instanceof Headquarter)) {
                throw new InvalidUserActionException("Can not place " + house + " as initial building");
            }

            firstHouse = true;
        }

        /* Only one headquarter can be placed per player */
        if (house instanceof Headquarter) {
            for (Building building : house.getPlayer().getBuildings()) {
                if (building instanceof Headquarter) {
                    throw new InvalidUserActionException("Can only have one headquarter placed per player");
                }
            }
        }

        /* Verify that the point is available for the chosen building */
        if (house.isMine()) {
            if (!isAvailableMinePoint(house.getPlayer(), point)) {
                throw new InvalidUserActionException("Cannot place " + house + " at non mining point.");

            }
        } else {
            Size buildingAvailable = isAvailableHousePoint(house.getPlayer(), point, firstHouse);

            if (buildingAvailable == null || !buildingAvailable.contains(house.getSize())) {
                throw new InvalidUserActionException("Cannot place " + house.getSize() + " building, only " + buildingAvailable + ".");
            }
        }

        /* In case of headquarter, verify that the building is not placed within another player's border -- normally
        *  this is done by isAvailableHousePoint
        */
        if (house instanceof Headquarter) {
            for (Player player : players) {
                if (!player.equals(house.getPlayer()) && player.isWithinBorder(point)) {
                    throw new InvalidUserActionException("Can't place building on " + point + " within another player's border");
                }
            }
        }

        /* Handle the case where there is a sign at the site */
        if (isSignAtPoint(point)) {
            removeSign(getSignAtPoint(point));
        }

        /* Use the existing flag if it exists, otherwise place a new flag */
        if (isFlagAtPoint(point.downRight())) {
            house.setFlag(getFlagAtPoint(point.downRight()));
        } else {
            Flag flag = house.getFlag();

            flag.setPosition(point.downRight());

            if (firstHouse) {
                placeFlagRegardlessOfBorder(flag);
            } else {
                placeFlag(flag);
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
        if (firstHouse) {
            updateBorder(house, BorderChangeCause.MILITARY_BUILDING_OCCUPIED);
        }

        /* Store in the point that there is now a building there */
        getMapPoint(point).setBuilding(house);

        /* Create a road between the flag and the building */
        placeDriveWay(house);

        /* Report the placed building */
        reportPlacedBuilding(house);

        return house;
    }

    void updateBorder(Building buildingCausedUpdate, BorderChangeCause cause) throws Exception {

        /* Build map Point->Building, picking buildings with the highest claim */
        Map<Point, Building>    claims       = new HashMap<>();
        Map<Player, List<Land>> updatedLands = new HashMap<>();
        List<Building>          allBuildings = new LinkedList<>();

        allBuildings.addAll(getBuildings());
        allBuildings.addAll(buildingsToAdd);
        allBuildings.removeAll(buildingsToRemove);

        /* Calculate claims for all military buildings */
        for (Building building : allBuildings) {

            /* Filter non-military buildings and un-occupied military buildings */
            if (!building.isMilitaryBuilding() || !building.isReady() || !building.isOccupied()) {
                continue;
            }

            /* Store the claim for each military building.

               This iterates over a collection and the order may be non-deterministic
            */
            for (Point point : building.getDefendedLand()) {
                if (!claims.containsKey(point)) {
                    claims.put(point, building);
                } else if (calculateClaim(building, point) > calculateClaim(claims.get(point), point)) {
                    claims.put(point, building);
                }
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

        /* Destroy buildings now outside of the borders */
        for (Building building : buildings) {
            if (building.isBurningDown()) {
                continue;
            }

            if (building.isMilitaryBuilding() && building.isOccupied()) {
                continue;
            }

            Player player = building.getPlayer();

            if (!player.isWithinBorder(building.getPosition()) || !player.isWithinBorder(building.getFlag().getPosition())) {
                building.tearDown();
            }
        }

        /* Remove flags now outside of the borders */
        List<Flag> flagsToRemove = new LinkedList<>();

        for (Flag flag : flags) {
            Player player = flag.getPlayer();

            if (!player.isWithinBorder(flag.getPosition())) {
                flagsToRemove.add(flag);
            }
        }

        /* Remove the flags */
        for (Flag flag : flagsToRemove) {
            removeFlagWithoutSideEffects(flag);
        }

        /* Remove any roads now outside of the borders */
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
                    if ((isBuildingAtPoint(road.getStart()) &&
                         getBuildingAtPoint(road.getStart()).isMilitaryBuilding()) ||
                        (isBuildingAtPoint(road.getEnd())   &&
                         getBuildingAtPoint(road.getEnd()).isMilitaryBuilding())) {
                        continue;
                    }
                }

                /* Remember to remove the road */
                roadsToRemove.add(road);
            }
        }

        /* Remove the roads */
        for (Road road : roadsToRemove) {
            removeRoad(road);
        }

        /* Update statistics collection of land per player */
        statisticsManager.collectLandStatisticsFromPlayers(time, players);
    }

    private Road placeDriveWay(Building building) throws Exception {
        List<Point> wayPoints = new ArrayList<>();

        wayPoints.add(building.getPosition());
        wayPoints.add(building.getFlag().getPosition());

        Road road = new Road(building.getPlayer(), building, wayPoints, building.getFlag());

        road.setNeedsCourier(false);

        roads.add(road);

        addRoadToMapPoints(road);

        /* Report that the driveway has been added */
        newRoads.add(road);

        return road;
    }

    /**
     * Places a road according to the given points
     *
     * @param player The player that will own the new road
     * @param points The points of the new road
     * @return The newly placed road
     * @throws Exception Any exceptions encountered while placing the new road
     */
    public Road placeRoad(Player player, Point... points) throws Exception {
        if (!players.contains(player)) {
            throw new Exception("Can't place road at " + Arrays.asList(points) + " because the player is invalid.");
        }

        return placeRoad(player, Arrays.asList(points));
    }

    /**
     * Places a road according to  he given points
     *
     * @param player The player that will own the new road
     * @param wayPoints The points of the new road
     * @return The newly placed road
     * @throws Exception Any exceptions encountered while placing the new road
     */
    public Road placeRoad(Player player, List<Point> wayPoints) throws Exception {

        /* Only allow roads that are at least three points long
        *   -- Driveways are shorter but they are created with a separate method
        */
        if (wayPoints.size() < 3) {
            throw new InvalidUserActionException("Cannot place road with less than three points.");
        }

        /* Verify that all points of the road are within the border */
        for (Point point : wayPoints) {
            if (!player.isWithinBorder(point)) {
                throw new InvalidUserActionException("Can't place road " + wayPoints + "with " + point + " outside the border");
            }
        }

        Point start = wayPoints.get(0);
        Point end   = wayPoints.get(wayPoints.size() - 1);

        if (!isFlagAtPoint(start)) {
            throw new InvalidEndPointException(start);
        }

        if (!isFlagAtPoint(end)) {
            throw new InvalidEndPointException(end);
        }

        if (start.equals(end)) {
            throw new InvalidEndPointException();
        }


        /* Verify that the road does not overlap itself */
        if (!GameUtils.isUnique(wayPoints)) {
            throw new Exception("Cannot create a road that overlaps itself");
        }

        /*
           Verify that the road has at least one free point between the
           endpoints so the courier has somewhere to stand
        */
        if (wayPoints.size() < 3) {
            throw new Exception("Road " + wayPoints + " is too short.");
        }

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

            throw new Exception(point + " in road is invalid");
        }

        Flag startFlag = getFlagAtPoint(start);
        Flag endFlag   = getFlagAtPoint(end);

        Road road = new Road(player, startFlag, wayPoints, endFlag);

        roads.add(road);

        addRoadToMapPoints(road);

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
     * @throws Exception Any exception encountered while placing the road
     */
    public Road placeAutoSelectedRoad(Player player, Flag start, Flag end) throws Exception {
        return placeAutoSelectedRoad(player, start.getPosition(), end.getPosition());
    }

    /**
     * Places the shortest possible new road between the given flags
     *
     * @param player The player that will own the new road
     * @param start The start of the road
     * @param end The end of the road
     * @return The newly placed road
     * @throws Exception Any exception encountered while placing the new road
     */
    public Road placeAutoSelectedRoad(Player player, Point start, Point end) throws Exception {

        /* Throw an exception if the start and end are the same */
        if (start.equals(end)) {
            throw new InvalidEndPointException("An automatically placed road must have different start and end points.");
        }

        List<Point> wayPoints = findAutoSelectedRoad(player, start, end, null);

    	if (wayPoints == null) {
            throw new InvalidEndPointException(end);
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
     * @throws InvalidRouteException If the start and end points are the same
     */
    public List<Point> findWayWithExistingRoads(Point start, Point end, Point via) throws InvalidRouteException {
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
     * @throws InvalidRouteException If the start and end points are the same
     */
    public List<Point> findWayWithExistingRoads(Point start, Point end) throws InvalidRouteException {
        if (start.equals(end)) {
            throw new InvalidRouteException("Start and end are the same.");
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
    // TODO: this doesn't work for flags, where multiple roads intersect. Verify who uses this method.
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
     * @throws Exception Any exception encountered while placing the flag
     */
    public Flag placeFlag(Player player, Point point) throws Exception {

        /* Verify that the player is valid */
        if (!players.contains(player)) {
            throw new Exception("Can't place flag at " + point + " because the player is invalid.");
        }

        return placeFlag(new Flag(player, point));
    }

    private Flag placeFlag(Flag flag) throws Exception {
        return doPlaceFlag(flag, true);
    }

    private Flag placeFlagRegardlessOfBorder(Flag flag) throws Exception {
        return doPlaceFlag(flag, false);
    }

    private Flag doPlaceFlag(Flag flag, boolean checkBorder) throws Exception {

        Point flagPoint = flag.getPosition();

        if (!isAvailableFlagPoint(flag.getPlayer(), flagPoint, checkBorder)) {
            throw new InvalidUserActionException("Can't place " + flag + " on occupied point");
        }

        /* Handle the case where the flag is placed on a sign */
        if (isSignAtPoint(flagPoint)) {
            removeSign(getSignAtPoint(flagPoint));
        }

        /* Handle the case where the flag is on an existing road that will be split */
        if (pointIsOnRoad(flagPoint)) {

            Road existingRoad = getRoadAtPoint(flagPoint);
            Courier courier   = existingRoad.getCourier();

            List<Point> points = existingRoad.getWayPoints();

            int index = points.indexOf(flagPoint);

            removeRoadButNotWorker(existingRoad);

            pointToGameObject.get(flag.getPosition()).setFlag(flag);
            flags.add(flag);

            Road newRoad1 = placeRoad(flag.getPlayer(), points.subList(0, index + 1));
            Road newRoad2 = placeRoad(flag.getPlayer(), points.subList(index, points.size()));

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

                    /* If the courier is on the road between one of the flags and a building, pick the road with the flag */

                    /*    - Courier walking from flag to building */
                    if (isFlagAtPoint(lastPoint) && isBuildingAtPoint(nextPoint) && nextPoint.equals(lastPoint.upLeft())) {
                        if (lastPoint.equals(newRoad1.getStart()) || lastPoint.equals(newRoad1.getEnd())) {
                            roadToAssign = newRoad1;
                        } else {
                            roadToAssign = newRoad2;
                        }

                    /*    - Courier walking from building to flag */
                    } else if (isBuildingAtPoint(lastPoint) && isFlagAtPoint(nextPoint)) {
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
            pointToGameObject.get(flag.getPosition()).setFlag(flag);
            flags.add(flag);
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
     * Returns the a list of the points where the given player can place a flag
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
        if (!isWithinMap(point)) {
            return false;
        }

        if (checkBorder && !player.isWithinBorder(point)) {
            return false;
        }

        if (isFlagAtPoint(point)) {
            return false;
        }

        if (isStoneAtPoint(point)) {
            return false;
        }

        if (isTreeAtPoint(point)) {
            return false;
        }

        /* Cannot build flag if construction is not possible on all adjacent tiles */
        if (!canBuildFlagOn(terrain.getTileAbove(point)) &&
            !canBuildFlagOn(terrain.getTileUpRight(point)) &&
            !canBuildFlagOn(terrain.getTileDownRight(point)) &&
            !canBuildFlagOn(terrain.getTileBelow(point)) &&
            !canBuildFlagOn(terrain.getTileDownLeft(point)) &&
            !canBuildFlagOn(terrain.getTileUpLeft(point))) {
            return false;
        }

        if (isCropAtPoint(point) &&
            getCropAtPoint(point).getGrowthState() != Crop.GrowthState.HARVESTED) {
            return false;
        }

        for (Point d : point.getDiagonalPoints()) {
            if (player.isWithinBorder(d) && isFlagAtPoint(d)) {
                return false;
            }
        }

        if (player.isWithinBorder(point.right()) && isFlagAtPoint(point.right())) {
            return false;
        }

        if (player.isWithinBorder(point.left()) && isFlagAtPoint(point.left())) {
            return false;
        }

        if (isBuildingAtPoint(point)) {
            return false;
        }

        if (player.isWithinBorder(point.downRight()) && isBuildingAtPoint(point.downRight())) {
            if (getBuildingAtPoint(point.downRight()).getSize() == LARGE) {
                return false;
            }
        }

        if (player.isWithinBorder(point.right()) && isBuildingAtPoint(point.right())) {
            if (getBuildingAtPoint(point.right()).getSize() == LARGE) {
                return false;
            }
        }

        if (player.isWithinBorder(point.downLeft()) && isBuildingAtPoint(point.downLeft())) {
            if (getBuildingAtPoint(point.downLeft()).getSize() == LARGE) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the terrain instance that's used to read and modify the vegetation
     *
     * @return Returns the terrain object for the game map
     */
    public Terrain getTerrain() {
        return terrain;
    }

    private List<Point> buildFullGrid() {
        List<Point> result = new ArrayList<>();
        boolean rowFlip    = true;
        boolean columnFlip;

        /* Place all possible flag points in the list */
        for (int y = 1; y < height; y++) {
            columnFlip = rowFlip;

            for (int x = 1; x < width; x++) {
                if (columnFlip) {
                    result.add(new Point(x, y));
                }

                columnFlip = !columnFlip;
            }

            rowFlip = !rowFlip;
        }

        return result;
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
            Size result = isAvailableHousePoint(player, point);

            if (result != null) {
                    housePoints.put(point, result);
                }
        }

        return housePoints;
    }

    private List<Point> getPossibleAdjacentRoadConnections(Player player, Point start, Point end) {
        Point[] adjacentPoints = new Point[] {
            new Point(start.x - 2, start.y),
            new Point(start.x + 2, start.y),
            new Point(start.x - 1, start.y - 1),
            new Point(start.x - 1, start.y + 1),
            new Point(start.x + 1, start.y - 1),
            new Point(start.x + 1, start.y + 1),
        };

        List<Point> resultList = new ArrayList<>();

        for (Point point : adjacentPoints) {
            if (point.equals(end) && isPossibleAsEndPointInRoad(player, point)) {
                resultList.add(point);
            } else if (!point.equals(end) && isPossibleAsAnyPointInRoad(player, point)) {
                resultList.add(point);
            }
        }

        return resultList;
    }

    private Iterable<Point> getPossibleAdjacentOffRoadConnections(Point from) {
        Point[] adjacentPoints  = from.getAdjacentPoints();
        List<Point>  resultList = new ArrayList<>();

        /* Houses can only be left via the driveway so handle this case separately */
        if (isBuildingAtPoint(from) && getBuildingAtPoint(from).getPosition().equals(from)) {
            resultList.add(from.downRight());

            return resultList;
        }

        /* Find out which adjacent points are possible off-road connections */
        for (Point point : adjacentPoints) {

            /* Filter points outside the map */
            if (!isWithinMap(point)) {
                continue;
            }

            /* Buildings can only be reached from their flags */
            if (isBuildingAtPoint(point) && !getBuildingAtPoint(point).getFlag().getPosition().equals(from)) {
                continue;
            }

            /* Filter points separated by vegetation that can't be walked on */
            if ((point.isLeftOf(from)                       &&
                 !canWalkOn(terrain.getTileUpLeft(from))    &&
                 !canWalkOn(terrain.getTileDownLeft(from)))      ||
                (point.isUpLeftOf(from)                     &&
                 !canWalkOn(terrain.getTileUpLeft(from))    &&
                 !canWalkOn(terrain.getTileAbove(from)))         ||
                (point.isUpRightOf(from)                    &&
                 !canWalkOn(terrain.getTileUpRight(from))   &&
                 !canWalkOn(terrain.getTileAbove(from)))         ||
                (point.isRightOf(from)                      &&
                 !canWalkOn(terrain.getTileUpRight(from))   &&
                 !canWalkOn(terrain.getTileDownRight(from)))     ||
                (point.isDownRightOf(from)                  &&
                 !canWalkOn(terrain.getTileDownRight(from)) &&
                 !canWalkOn(terrain.getTileBelow(from)))         ||
                (point.isDownLeftOf(from)                   &&
                 !canWalkOn(terrain.getTileDownLeft(from))  &&
                 !canWalkOn(terrain.getTileBelow(from)))) {
                continue;
            }

            /* Filter points with stones */
            if (isStoneAtPoint(point)) {
                continue;
            }

            /* Add the point to the list if it passed the filters */
            resultList.add(point);
        }

        resultList.remove(from.up());

        resultList.remove(from.down());

        return resultList;
    }

    private boolean canBuildFlagOn(Tile tile) {
        switch (tile.getVegetationType()) {
            case SWAMP:
            case SNOW:
            case WATER:
            case LAVA:
            case DEEP_WATER:
                return false;
            default:
                return true;
        }
    }

    private boolean canWalkOn(Tile tile) {

        switch (tile.getVegetationType()) {
            case WATER:
            case SWAMP:
            case DEEP_WATER:
            case LAVA:
            case SNOW:
                return false;
            default:
                return true;
        }
    }

    /**
     * Returns true if there is a flag at the given point
     *
     * @param point The point where there might be a flag
     * @return Returns true if there is a flag at the given point, otherwise false
     */
    public boolean isFlagAtPoint(Point point) {
        return pointToGameObject.get(point).isFlag();
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

    private void addRoadToMapPoints(Road road) {
        for (Point point : road.getWayPoints()) {
            MapPoint mapPoint = pointToGameObject.get(point);

            mapPoint.addConnectingRoad(road);
        }
    }

    private Map<Point, MapPoint> populateMapPoints(List<Point> fullGrid) {
        Map<Point, MapPoint> resultMap = new HashMap<>();

        for (Point point : fullGrid) {
            resultMap.put(point, new MapPoint(point));
        }

        return resultMap;
    }

    /**
     * Returns the flag at the given point
     *
     * @param point The point to get the flag from
     * @return Returns the flag at the given point, or null if there is no flag at the given point
     */
    public Flag getFlagAtPoint(Point point) {
        MapPoint mp = pointToGameObject.get(point);

        return mp.getFlag();
    }

    private boolean isPossibleAsEndPointInRoad(Player player, Point point) {

        if (!isWithinMap(point)) {
            return false;
        }

        MapPoint mp = pointToGameObject.get(point);

        if (mp.isFlag() && player.isWithinBorder(point)) {
            return true;
        }

        if (isRoadAtPoint(point) && isAvailableFlagPoint(player, point)) {
            return true;
        }

        if (isPossibleAsAnyPointInRoad(player, point)) {
            return true;
        }

        return false;
    }

    private boolean isPossibleAsAnyPointInRoad(Player player, Point point) {
        MapPoint mp = pointToGameObject.get(point);

        if (!isWithinMap(point)) {
            return false;
        }

        if (mp.isRoad()) {
            return false;
        }

        if (mp.isFlag()) {
            return false;
        }

        if (mp.isStone()) {
            return false;
        }

        if (mp.isBuilding()) {
            return false;
        }

        if (mp.isTree()) {
            return false;
        }

        if (mp.isCrop()) {
            return false;
        }

        if (!player.isWithinBorder(point)) {
            return false;
        }

        if (terrain.isInWater(point)) {
            return false;
        }

        /* Can't build road on snow */
        if (terrain.isOnSnow(point)) {
            return false;
        }

        /* Can't build road on lava */
        if (terrain.isOnLava(point)) {
            return false;
        }

        /* Can't place road in deep water */
        if (terrain.isInDeepWater(point)) {
            return false;
        }

        /* Can't place road in swamp */
        if (terrain.isInSwamp(point)) {
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
        Point[] adjacentPoints  = from.getAdjacentPoints();
        List<Point>  resultList = new ArrayList<>();

        for (Point point : adjacentPoints) {
            if (isPossibleAsAnyPointInRoad(player, point)) {
                resultList.add(point);
            }
        }

        resultList.remove(from.up());

        resultList.remove(from.down());

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
        Point[] adjacentPoints  = from.getAdjacentPoints();
        List<Point>  resultList = new ArrayList<>();

        if (!isWithinMap(from)) {
            throw new InvalidUserActionException("Cannot get adjacent road connections from a point outside the map");
        }

        for (Point point : adjacentPoints) {
            if (isPossibleAsEndPointInRoad(player, point)) {
                resultList.add(point);
            } else if (isPossibleAsAnyPointInRoad(player, point)) {
                resultList.add(point);
            }
        }

        resultList.remove(from.up());

        resultList.remove(from.down());

        return resultList;
    }

    /**
     * Returns the building at the given point. If there is no building, null is returned
     *
     * @param point a point on the map
     * @return the building at the given point
     */
    public Building getBuildingAtPoint(Point point) {
        MapPoint mp = pointToGameObject.get(point);

        return mp.getBuilding();
    }

    /**
     * Returns true if there is a building at the given point
     *
     * @param point a point on the map
     * @return true if there is a building on the given point
     */
    public boolean isBuildingAtPoint(Point point) {
        return getBuildingAtPoint(point) != null;
    }

    /**
     * Returns true if there is a road at the given point
     *
     * @param point a point on the map
     * @return true if there is a road at the given point
     */
    public boolean isRoadAtPoint(Point point) {
        MapPoint mp = pointToGameObject.get(point);

        return !mp.getConnectedNeighbors().isEmpty();
    }

    /**
     * Returns true if there is a tree at the given point
     *
     * @param point a point on the map
     * @return true if there is a tree at the point
     */
    public boolean isTreeAtPoint(Point point) {
        MapPoint mp = pointToGameObject.get(point);

        return mp.getTree() != null;
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
    public List<Point> findWayOffroad(Point start, Point goal, Point via,
            Collection<Point> avoid) {

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
        path2.remove(0);

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
    public List<Point> findWayOffroad(Point start, Point goal, Collection<Point> avoid) {
        return GameUtils.findShortestPath(start, goal, avoid, new ConnectionsProvider() {

            @Override
            public Iterable<Point> getPossibleConnections(Point start, Point goal) {
                try {
                    return getPossibleAdjacentOffRoadConnections(start);
                } catch (Exception ex) {
                    Logger.getLogger(GameMap.class.getName()).log(Level.SEVERE, null, ex);
                }

                return new LinkedList<>();
            }

            @Override
            public Double realDistance(Point currentPoint, Point neighbor) {
                return (double)1;
            }

            @Override
            public Double estimateDistance(Point from, Point to) {
                return from.distance(to);
            }
        });
    }

    /**
     * Places a tree at the given point
     * @param point The point to place the tree at
     * @return The placed tree
     * @throws Exception Throws exception if the tree cannot be placed
     */
    public Tree placeTree(Point point) throws Exception {
        MapPoint mp = pointToGameObject.get(point);

        if (mp.isFlag()) {
            throw new Exception("Can't place tree on " + point + " on existing flag");
        } else if (mp.isRoad()) {
            throw new Exception("Can't place tree on " + point + " on existing road");
        } else if (mp.isStone()) {
            throw new Exception("Can't place tree on " + point + " on existing stone");
        }

        Tree tree = new Tree(point);

        mp.setTree(tree);

        trees.add(tree);

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
        MapPoint mp = pointToGameObject.get(position);

        Tree tree = mp.getTree();

        mp.removeTree();

        trees.remove(tree);

        /* Report that the tree was removed */
        removedTrees.add(tree);
    }

    public Tree getTreeAtPoint(Point point) {
        MapPoint mp = pointToGameObject.get(point);

        return mp.getTree();
    }

    /**
     * Places a stone at the given point
     *
     * @param point The point to place the stone on
     * @return The placed stone
     */
    public Stone placeStone(Point point) {
        MapPoint mp = pointToGameObject.get(point);

        Stone stone = new Stone(point);

        mp.setStone(stone);

        stones.add(stone);

        return stone;
    }

    /**
     * Places a crop at the given point
     *
     * @param point The point to place the crop on
     * @return The placed crop
     * @throws Exception Throws exception if the crop cannot be placed
     */
    public Crop placeCrop(Point point) throws Exception {
        MapPoint mp = pointToGameObject.get(point);

        if (isCropAtPoint(point)) {
            Crop crop = mp.getCrop();

            if (crop.getGrowthState() != HARVESTED) {
                throw new Exception("Can't place crop on non-harvested crop at " + point);
            }
        }

        Crop crop = new Crop(point, this);

        mp.setCrop(crop);

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
        MapPoint mp = pointToGameObject.get(point);

        return mp.getCrop() != null;
    }

    /**
     * Returns true if there is a stone at the point
     *
     * @param point The point where there might be a stone
     * @return True if there is a stone at the point
     */
    public boolean isStoneAtPoint(Point point) {
        MapPoint mp = pointToGameObject.get(point);

        return mp.getStone() != null;
    }

    Cargo removePartOfStone(Point position) {
        MapPoint mp = pointToGameObject.get(position);

        Stone stone = mp.getStone();

        if (stone.noMoreStone()) {
            return null;
        }

        stone.removeOnePart();

        if (stone.noMoreStone()) {
            mp.setStone(null);

            /* Report that the stone was removed */
            removedStones.add(stone);
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
        List<Point> result = new ArrayList<>();

        boolean rowFlip = false;

        for (int y = point.y - radius; y <= point.y + radius; y++) {
            int startX = point.x - radius;

            if (rowFlip) {
                startX++;
            }

            for (int x = startX; x <= point.x + radius; x += 2) {

                Point p = new Point(x, y);

                if (isWithinMap(p) && point.distance(p) <= radius) {
                    result.add(p);
                }
            }

            rowFlip = !rowFlip;
        }

        return result;
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
        return pointToGameObject.get(point).getCrop();
    }

    /**
     * Returns the crops on the map
     *
     * @return List of crops
     */
    public Iterable<Crop> getCrops() {
        return crops;
    }

    private void removeStone(Stone stone) {
        MapPoint mp = pointToGameObject.get(stone.getPosition());

        mp.setStone(null);

        stones.remove(stone);
    }

    /**
     * Removes the given flag from the game
     *
     * @param flag The flag to remove
     * @throws Exception Throws exception if there is a fault when removing connected roads
     */
    public void removeFlag(Flag flag) throws Exception{
        MapPoint mpUpLeft = pointToGameObject.get(flag.getPosition().upLeft());
        MapPoint mp = pointToGameObject.get(flag.getPosition());

        /* Destroy the house if the flag is connected to a house */
        if (mpUpLeft.isBuilding() && flag.equals(mpUpLeft.getBuilding().getFlag())) {
            Building attachedBuilding = mpUpLeft.getBuilding();

            if (!attachedBuilding.isBurningDown() && !attachedBuilding.isDestroyed()) {
                attachedBuilding.tearDown();
            }
        }

        /* Remove the road if the flag is an endpoint to a road */
        List<Road> roadsToRemove = new LinkedList<>();
        for (Road road : mp.getConnectedRoads()) {
            if (road.getStartFlag().equals(flag) || road.getEndFlag().equals(flag)) {
                roadsToRemove.add(road);
            }
        }

        for (Road road : roadsToRemove) {
            removeRoad(road);
        }

        removeFlagWithoutSideEffects(flag);
    }

    private void removeFlagWithoutSideEffects(Flag flag) {
        MapPoint mp = pointToGameObject.get(flag.getPosition());

        /* Remove the flag */
        mp.removeFlag();

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

        return terrain.getTileUpLeft(point).getAmountOfMineral(mineral)    +
               terrain.getTileAbove(point).getAmountOfMineral(mineral)     +
               terrain.getTileUpRight(point).getAmountOfMineral(mineral)   +
               terrain.getTileDownRight(point).getAmountOfMineral(mineral) +
               terrain.getTileBelow(point).getAmountOfMineral(mineral)     +
               terrain.getTileDownLeft(point).getAmountOfMineral(mineral);
    }

    /**
     * Returns the amount of fish at a given point
     *
     * @param point The point to get the amount of fish for
     * @return The amount of fish at the given point
     */
    public int getAmountFishAtPoint(Point point) {

        return terrain.getTileUpLeft(point).getAmountFish()   +
               terrain.getTileAbove(point).getAmountFish()    +
               terrain.getTileUpRight(point).getAmountFish()  +
               terrain.getTileDownLeft(point).getAmountFish() +
               terrain.getTileBelow(point).getAmountFish()    +
               terrain.getTileDownLeft(point).getAmountFish();
    }

    /**
     * Catches a fish at the given point
     *
     * @param point Where to catch the fish
     * @return A cargo containing the fish
     * @throws Exception Thrown if there was no fish to catch
     */
    public Cargo catchFishAtPoint(Point point) throws Exception {
        for (Tile tile : terrain.getSurroundingTiles(point)) {
            if (tile.getAmountFish() > 0) {
                tile.consumeFish();

                return new Cargo(FISH, this);
            }
        }

        throw new Exception("Can't find any fish to catch at " + point);
    }

    /**
     * Mines one bit of ore from the given point
     *
     * @param mineral the type of mineral to attempt to mine
     * @param point the point to mine at
     * @return a cargo containing the mined ore
     * @throws Exception is thrown if there is no ore to mine
     */
    public Cargo mineMineralAtPoint(Material mineral, Point point) throws Exception {
        for (Tile tile : terrain.getSurroundingTiles(point)) {
            if (tile.getAmountOfMineral(mineral) > 0) {
                tile.mine(mineral);

                return new Cargo(mineral, this);
            }
        }

        throw new Exception("Can't find any gold to mine at " + point);
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

    MapPoint getMapPoint(Point point) {
        return pointToGameObject.get(point);
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
        Sign sign = new Sign(mineral, amount, point, this);

        getMapPoint(point).setSign(sign);

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
        MapPoint mp = getMapPoint(sign.getPosition());

        mp.setSign(null);

        signsToRemove.add(sign);

        /* Report that this sign will be removed */
        removedSigns.add(sign);
    }

    private void removeSign(Sign sign) {
        MapPoint mp = getMapPoint(sign.getPosition());

        mp.setSign(null);

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
        MapPoint mp = getMapPoint(building.getPosition());

        mp.removeBuilding();

        buildingsToRemove.add(building);
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

    void placeWorkerFromStepTime(Donkey donkey, Building home) {
        donkey.setPosition(home.getPosition());
        workersToAdd.add(donkey);
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
        return isAvailableHousePoint(player, point, false);
    }

    private Size isAvailableHousePoint(Player player, Point point, boolean isFirstHouse) {
        Point flagPoint = point.downRight();
        MapPoint houseMapPoint = getMapPoint(point);
        MapPoint flagMapPoint = getMapPoint(point.downRight());
        MapPoint mapPointDown = pointToGameObject.get(point.down());
        MapPoint mapPointUpRight = pointToGameObject.get(point.upRight());
        MapPoint mapPointUpRightUpRight = pointToGameObject.get(point.up().right());
        MapPoint mapPointDownLeftDownLeft = getMapPoint(point.downLeft().downLeft());
        MapPoint mapPointDownRightDownRight = getMapPoint(point.down().right());
        MapPoint mapPointDownLeftLeft = pointToGameObject.get(point.downLeft().left());

        /* ALL CONDITIONS FOR SMALL */

        /* Can't build on a point outside the map */
        if (houseMapPoint == null) {
            return null;
        }

        /* The flag point also needs to be on the map */
        if (flagMapPoint == null) {
            return null;
        }

        /* Make sure all houses except for the headquarter are placed within the player's border */
        if (!isFirstHouse && !player.isWithinBorder(point)) {
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

        // Future improvement collapse these to avoid iterating through tiles over and over again
        if (terrain.isOnMountain(point)) {
            return null;
        }

        if (terrain.isNextToDeepWater(point)) {
            return null;
        }

        if (terrain.isNextToMagenta(point)) {
            return null;
        }

        if (terrain.isNextToSwamp(point)) {
            return null;
        }

        if (terrain.isInDeepWater(point)) {
            return null;
        }

        if (terrain.isNextToWater(point)) {
            return null;
        }

        if (terrain.isNextToDesert(point)) {
            return null;
        }

        if (terrain.isNextToSnow(point)) {
            return null;
        }

        if (terrain.isNextToLava(point)) {
            return null;
        }

        if (terrain.isOnEdgeOf(point, MOUNTAIN)) {
            return null;
        }

        if (houseMapPoint.isRoad()) {
            return null;
        }

        if (!flagMapPoint.isFlag() && !isAvailableFlagPoint(player, flagPoint, !isFirstHouse)) {
            return null;
        }

        if (houseMapPoint.isCrop()) {
            return null;
        }

        /* It's not possible to build a house left/right or diagonally of a stone or building */
        for (Point d : point.getDiagonalPointsAndSides()) {
            if (!player.isWithinBorder(d)) {
                continue;
            }

            MapPoint adjacentMapPoint = getMapPoint(d);

            /* It's not possible to build a house next to another house */
            if (adjacentMapPoint.isBuilding()) {
                return null;
            }

            /* It's not possible to build a house next to a stone */
            if (adjacentMapPoint.isStone()) {
                return null;
            }
        }

        if (player.isWithinBorder(point.upRight()) && mapPointUpRight.isFlag()) {
            return null;
        }

        if (player.isWithinBorder(point.up().right()) && mapPointUpRightUpRight.isBuildingOfSize(LARGE)) {
            return null;
        }

        if (player.isWithinBorder(point.down()) && mapPointDown.isBuildingOfSize(LARGE)) {
            return null;
        }

        if (player.isWithinBorder(point.downRight().right()) && isBuildingAtPoint(point.downRight().right())) {
            if (getBuildingAtPoint(point.downRight().right()).getSize() == LARGE) {
                return null;
            }
        }

        /* Can't place a building up-left-up-left of a large building */
        if (mapPointDownRightDownRight != null && mapPointDownRightDownRight.isBuildingOfSize(LARGE)) {
            return null;
        }

        /* Can't place a building up-right-up-right of a large building */
        if (mapPointDownLeftDownLeft != null && mapPointDownLeftDownLeft.isBuildingOfSize(LARGE)) {
            return null;
        }

        /* ADDITIONAL CONDITIONS FOR MEDIUM */

        /* A large building can't have a tree directly left or right */
        if ((isWithinMap(point.left())  && isTreeAtPoint(point.left())) ||
            (isWithinMap(point.right()) && isTreeAtPoint(point.right()))) {
            return SMALL;
        }

        for (Point d : point.getDiagonalPoints()) {

            /* It's not possible to build a medium house next to a tree */
            if (isTreeAtPoint(d)) {
                return SMALL;
            }
        }

        /* Can only place small building up-right-right of large building */
        if (mapPointDownLeftLeft != null && mapPointDownLeftLeft.isBuildingOfSize(LARGE)) {
            return SMALL;
        }

        /* ADDITIONAL CONDITIONS FOR LARGE */

        if (player.isWithinBorder(point.upLeft()) && isFlagAtPoint(point.upLeft())) {
            return MEDIUM;
        }

        if (player.isWithinBorder(point.down()) && isBuildingAtPoint(point.down())) {
            return MEDIUM;
        }

        if (player.isWithinBorder(point.left()) && isFlagAtPoint(point.left())) {
            return MEDIUM;
        }

        if (player.isWithinBorder(point.upRight().right()) && isBuildingAtPoint(point.upRight().right())) {
            if (getBuildingAtPoint(point.upRight().right()).getSize() != SMALL) {
                return MEDIUM;
            }
        }

        if (player.isWithinBorder(point.up().right()) && isBuildingAtPoint(point.up().right())) {
            if (getBuildingAtPoint(point.up().right()).getSize() != SMALL) {
                return MEDIUM;
            }
        }

        if (player.isWithinBorder(point.right().right()) && isBuildingAtPoint(point.right().right())) {
            if (getBuildingAtPoint(point.right().right()).getSize() == LARGE) {
                return MEDIUM;
            }
        }

        /* A large building needs a larger free area on buildable vegetation */
        // TODO: check if it's possible to also build large house close to other sides where only flags&roads are possible
        if (!terrain.getTileUpLeft(point.upLeft()).getVegetationType().canBuildFlags()   ||
            !terrain.getTileAbove(point.upLeft()).getVegetationType().canBuildFlags()    ||
            !terrain.getTileUpLeft(point.upRight()).getVegetationType().canBuildFlags()  ||
            !terrain.getTileAbove(point.upRight()).getVegetationType().canBuildFlags()   ||
            !terrain.getTileUpRight(point.upRight()).getVegetationType().canBuildFlags() ||
            !terrain.isOnBuildable(point.left())      || !terrain.isOnBuildable(point.right()) ||
            !terrain.isOnBuildable(point.downRight()) || !terrain.isOnBuildable(point.downLeft())) {
            return MEDIUM;
        }

        if (!terrain.isOnBuildable(point)) {
            return MEDIUM;
        }

        /* Large buildings cannot be built if the height difference to close points is too large */
        int heightAtPoint = getHeightAtPoint(point);
        if (Math.abs(heightAtPoint - getHeightAtPoint(point.left()))      > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE ||
            Math.abs(heightAtPoint - getHeightAtPoint(point.upLeft()))    > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE ||
            Math.abs(heightAtPoint - getHeightAtPoint(point.upRight()))   > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE ||
            Math.abs(heightAtPoint - getHeightAtPoint(point.right()))     > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE ||
            Math.abs(heightAtPoint - getHeightAtPoint(point.downRight())) > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE ||
            Math.abs(heightAtPoint - getHeightAtPoint(point.downLeft()))  > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE) {
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
     * @param flag that the connect to
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
        if (!getTerrain().isOnMountain(point)) {
            return false;
        }

        /* Return false if the point is on a flag */
        if (isFlagAtPoint(point)) {
            return false;
        }

        /* Return false if the point is on a road */
        if (isRoadAtPoint(point)) {
            return false;
        }

        /* Return false if it's not possible to place a flag */
        Point flagPoint = point.downRight();

        if (!isFlagAtPoint(flagPoint) && !isAvailableFlagPoint(player, flagPoint)) {
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

    void placeProjectile(Projectile projectile, Point position) {
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

    private void handleWildAnimalPopulation() {

        double density = (double)wildAnimals.size() / (double)(width * height);

        if (density < Constants.WILD_ANIMAL_NATURAL_DENSITY) {
            if (animalCountdown.reachedZero()) {

                /* Find point to place new wild animal on */
                Point point = findRandomPossiblePointToPlaceFreeMovingActor();

                if (point == null) {
                    return;
                }

                /* Place the new wild animal */
                placeWildAnimal(point);

                animalCountdown.countFrom(Constants.WILD_ANIMAL_TIME_BETWEEN_REPOPULATION);
            } else if (!animalCountdown.isActive()) {
                animalCountdown.countFrom(Constants.WILD_ANIMAL_TIME_BETWEEN_REPOPULATION);
            } else {
                animalCountdown.step();
            }
        }
    }

    private Point findRandomPossiblePointToPlaceFreeMovingActor() {

        /* Pick centered point randomly */
        double x = random.nextDouble() * getWidth();
        double y = random.nextDouble() * getHeight();

        Point point = GameUtils.getClosestPoint(x, y);

        /* Go through the full map and look for a suitable point */
        for (Point p : getPointsWithinRadius(point, LOOKUP_RANGE_FOR_FREE_ACTOR)) {

            /* Filter buildings */
            if (isBuildingAtPoint(p)) {
                continue;
            }

            /* Filter stones */
            if (isStoneAtPoint(p)) {
                continue;
            }

            /* Filter terrain the animal can't walk on */
            if (WildAnimal.cannotWalkOn(getTerrain().getSurroundingTiles(p))) {
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

    void replaceBuilding(Building upgradedBuilding, Point position) throws Exception {

        /* Plan to remove the pre-upgrade building */
        Building oldBuilding = getBuildingAtPoint(position);
        buildingsToRemove.add(oldBuilding);

        /* Put the upgraded building in place  */
        upgradedBuilding.setPosition(position);
        upgradedBuilding.setFlag(oldBuilding.getFlag());

        /* Update the map point */
        getMapPoint(position).removeBuilding();
        getMapPoint(position).setBuilding(upgradedBuilding);

        /* Update the player's list of buildings */
        upgradedBuilding.getPlayer().removeBuilding(oldBuilding);
        upgradedBuilding.getPlayer().addBuilding(upgradedBuilding);

        /* Plan to add the upgraded building */
        buildingsToAdd.add(upgradedBuilding);

        /* Report that the building has changed */
        changedBuildings.add(upgradedBuilding);
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
        return GameUtils.arePointsConnectedByRoads(start, end, pointToGameObject);
    }

    /**
     * Finds the shortest path following roads between two points. The points must be flags or houses.
     *
     * @param start The flag or building to start from
     * @param end The flag or building to reach
     * @return the list of points with flags or buildings to pass by
     */
    public List<Point> findWayWithExistingRoadsInFlagsAndBuildings(EndPoint start, EndPoint end) {
        return GameUtils.findShortestPathViaRoads(start.getPosition(), end.getPosition(), pointToGameObject);
    }

    public boolean isValidRouteThroughFlagsAndBuildingsViaRoads(Point... points) {
        return isValidRouteThroughFlagsAndBuildingsViaRoads(Arrays.asList(points));
    }

    public boolean isValidRouteThroughFlagsAndBuildingsViaRoads(List<Point> points) {

        Point previous = null;

        for (Point point : points) {

            if (previous != null) {
                MapPoint mp = pointToGameObject.get(previous);

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
        return GameUtils.areBuildingsOrFlagsConnected(from, to, pointToGameObject);
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
        return this.statisticsManager;
    }

    public long getCurrentTime() {
        return time;
    }

    public void reportWorkerWithNewTarget(Worker worker) {
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

    public void reportTornDownBuilding(Building building) {
        changedBuildings.add(building);
    }

    public void reportBuildingBurnedDown(Building building) {
        changedBuildings.add(building);
    }

    public void reportBuildingRemoved(Building building) {
        removedBuildings.add(building);
    }

    public void reportWorkerEnteredBuilding(Worker worker) {
        removedWorkers.add(worker);
    }

}
