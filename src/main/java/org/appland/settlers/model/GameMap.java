package org.appland.settlers.model;

import org.appland.settlers.model.GameUtils.ConnectionsProvider;
import org.appland.settlers.model.Tile.Vegetation;
import org.appland.settlers.policy.Constants;

import java.awt.*;
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
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.MEDIUM;
import static org.appland.settlers.model.Size.SMALL;
import static org.appland.settlers.model.Tile.Vegetation.MOUNTAIN;
import static org.appland.settlers.model.Tile.Vegetation.WATER;

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

    private final String theLeader = "Mai Thi Van Anh";

    private static final Logger log = Logger.getLogger(GameMap.class.getName());

    private final int MINIMUM_WIDTH  = 5;
    private final int MINIMUM_HEIGHT = 5;
    private final int LOOKUP_RANGE_FOR_FREE_ACTOR = 10;

    private Player winner;

    public List<Point> findAutoSelectedRoad(final Player player, Point start,
            Point goal, Collection<Point> avoid) {
        return findShortestPath(start, goal, avoid, new GameUtils.ConnectionsProvider() {

            @Override
            public Iterable<Point> getPossibleConnections(Point start, Point goal) {
                try {
                    return getPossibleAdjacentRoadConnections(player, start, goal);
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

    private boolean pointIsOnRoad(Point point) {
        return getRoadAtPoint(point) != null;
    }

    public Road getRoadAtPoint(Point point) {
        MapPoint mp = pointToGameObject.get(point);

        Iterable<Road> roadsFromPoint = mp.getConnectedRoads();

        for (Road road : roadsFromPoint) {
            if (!road.getFlags()[0].getPosition().equals(point) &&
                !road.getFlags()[1].getPosition().equals(point)) {

                return road;
            }
        }

        return null;
    }

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
    }

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

        pointToGameObject   = populateMapPoints(buildFullGrid());

        pathOnExistingRoadsProvider = new GameUtils.PathOnExistingRoadsProvider(pointToGameObject);
        connectedFlagsAndBuildingsProvider = new GameUtils.ConnectedFlagsAndBuildingsProvider(pointToGameObject);

        /* Give the players a reference to the map */
        for (Player player : players) {
            player.setMap(this);
        }

        /* Verify that all players have unique colors */
        if (!allPlayersHaveUniqueColor()) {
            throw new Exception("Each player must have a unique color");
        }

        /* Set a constant initial seed for the random generator to get a
           deterministic behavior */
        random.setSeed(1);

        /* There is no winner when the game starts */
        winner = null;
    }

    public void setStartingPoints(List<Point> points) {
        startingPoints.addAll(points);
    }

    public List<Point> getStartingPoints() {
        return startingPoints;
    }

    public void setPlayers(List<Player> newPlayers) {
        players.clear();

        players.addAll(newPlayers);

        for (Player player : players) {
            player.setMap(this);
        }
    }

    public void stepTime() throws Exception {

        projectilesToRemove.clear();
        workersToRemove.clear();
        workersToAdd.clear();
        signsToRemove.clear();
        buildingsToRemove.clear();
        animalsToRemove.clear();
        cropsToRemove.clear();
        buildingsToAdd.clear();

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

        /* Resume transport of stuck cargos */
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

        /* Update buildings list to handle upgraded buildings where the old
           building gets removed and a new building is added */
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
    }

    public <T extends Building> T placeBuilding(T house, Point point) throws Exception {
        log.log(Level.INFO, "Placing {0} at {1}", new Object[]{house, point});

        boolean firstHouse = false;

        if (buildings.contains(house)) {
            throw new Exception("Can't place " + house + " as it is already placed.");
        }

        /* Verify that the house's player is valid */
        if (!players.contains(house.getPlayer())) {
            throw new Exception("Can't place " + house + ", player " + house.getPlayer() + " is not valid.");
        }

        /* Handle the first building separately */
        if (house.getPlayer().getBuildings().isEmpty()) {
            if (! (house instanceof Headquarter)) {
                throw new Exception("Can not place " + house + " as initial building");
            }

            firstHouse = true;
        }

        /* Only one headquarter can be placed per player */
        if (house instanceof Headquarter) {
            boolean headquarterPlaced = false;

            for (Building building : house.getPlayer().getBuildings()) {
                if (building instanceof Headquarter) {
                    headquarterPlaced = true;

                    break;
                }
            }

            if (headquarterPlaced) {
                throw new Exception("Can only have one headquarter placed per player");
            }
        }

        if (!firstHouse && !house.getPlayer().isWithinBorder(point)) {
            throw new Exception("Can't place building on " + point + " because it's outside the border");
        }

        /* Verify that the building is not placed within another player's border */
        for (Player player : players) {
            if (!player.equals(house.getPlayer()) && player.isWithinBorder(point)) {
                throw new Exception("Can't place building on " + point + " within another player's border");
            }
        }

        if (!isVegetationCorrect(house, point)) {
            throw new Exception("Can't place building on " + point + ".");
        }

        /* Verify that the flag can be placed */
        if (!isFlagAtPoint(point.downRight()) &&
            !firstHouse &&
            !isAvailableFlagPoint(house.getPlayer(), point.downRight())) {
            throw new Exception("Can't place flag for building at " + point);
        }

        /* Handle the case where there is a sign at the site */
        if (isSignAtPoint(point)) {
            removeSign(getSignAtPoint(point));
        }

        /* Verify that there is no crop blocking the construction */
        if (isCropAtPoint(point)) {
            throw new InvalidUserActionException("Cannot place building on crop");
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

        house.setPosition(point);
        house.setMap(this);

        /* Add building to the global list of buildings */
        buildings.add(house);

        /* Add building to the player's list of buildings */
        house.getPlayer().addBuilding(house);

        /* Initialize the border if it's the first house and it's a headquarter
           or if it's a military building
        */
        if (firstHouse) {
            updateBorder();
        }

        getMapPoint(point).setBuilding(house);

        placeDriveWay(house);

        return house;
    }

    void updateBorder() throws Exception {

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
            if (!building.isMilitaryBuilding() || !building.ready() || !building.occupied()) {
                continue;
            }

            /* Store the claim for each military building
               This iterates over a collection and the order may be
               non-deterministic
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
        Set<Point> localCleared = new HashSet<>();
        Set<Point> globalCleared = new HashSet<>();
        Set<Point> pointsInLand = new HashSet<>();
        Set<Point> borders = new LinkedHashSet<>();

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
                for (Point p : point.getAdjacentPoints()) {
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
                        borders.add(point);

                        globalCleared.add(p);

                    /* Add the point to the border if it belongs to another player */
                    } else if (!claims.get(p).getPlayer().equals(player)) {
                        borders.add(point);
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
                updatedLands.put(player, new ArrayList<Land>());
            }

            updatedLands.get(player).add(new Land(pointsInLand, borders));
        }

        /* Update lands in each player */
        List<Player> playersToUpdate = new ArrayList<>(players);

        /* This iterates over a set and the order may be non-deterministic */
        for (Entry<Player, List<Land>> pair : updatedLands.entrySet()) {
            pair.getKey().setLands(pair.getValue());

            playersToUpdate.remove(pair.getKey());
        }

        /* Clear the players that no longer have any land */
        for (Player player : playersToUpdate) {
            player.setLands(new ArrayList<Land>());
        }

        /* Destroy buildings now outside of the borders */
        for (Building building : buildings) {
            if (building.burningDown()) {
                continue;
            }

            if (building.isMilitaryBuilding()) {
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
    }

    private Road placeDriveWay(Building building) throws Exception {
        List<Point> wayPoints = new ArrayList<>();

        wayPoints.add(building.getPosition());
        wayPoints.add(building.getFlag().getPosition());

        Road road = new Road(building.getPlayer(), building, wayPoints, building.getFlag());

        road.setNeedsCourier(false);

        roads.add(road);

        addRoadToMapPoints(road);

        return road;
    }

    public Road placeRoad(Player player, Point... points) throws Exception {
        if (!players.contains(player)) {
            throw new Exception("Can't place road at " + Arrays.asList(points) + " because the player is invalid.");
        }

        return placeRoad(player, Arrays.asList(points));
    }

    public Road placeRoad(Player player, List<Point> wayPoints) throws Exception {
        log.log(Level.INFO, "Placing road through {0}", wayPoints);

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

        /* Verify that all points of the road are within the border */
        for (Point point : wayPoints) {
            if (!player.isWithinBorder(point)) {
                throw new Exception("Can't place road " + wayPoints + "with " + point + " outside the border");
            }
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

        return road;
    }

    public Road placeAutoSelectedRoad(Player player, Flag start, Flag end) throws Exception {
        return placeAutoSelectedRoad(player, start.getPosition(), end.getPosition());
    }

    public Road placeAutoSelectedRoad(Player player, Point start, Point end) throws Exception {
        List<Point> wayPoints = findAutoSelectedRoad(player, start, end, null);

	if (wayPoints == null) {
            throw new InvalidEndPointException(end);
        }

        return placeRoad(player, wayPoints);
    }

    public List<Road> getRoads() {
        return roads;
    }

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

    public List<Point> findWayWithExistingRoads(Point start, Point end) throws InvalidRouteException {
        if (start.equals(end)) {
            throw new InvalidRouteException("Start and end are the same.");
        }

        return findShortestPath(start, end, null, pathOnExistingRoadsProvider);
    }

    public Road getRoad(Point start, Point end) {
        for (Road road : roads) {
            if ((road.getStart().equals(start) && road.getEnd().equals(end)) ||
                (road.getEnd().equals(start) && road.getStart().equals(end))) {
                return road;
            }
        }

        return null;
    }

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
        log.log(Level.INFO, "Placing {0}", new Object[]{flag});

        Point flagPoint = flag.getPosition();

        if (!isAvailableFlagPoint(flag.getPlayer(), flagPoint, checkBorder)) {
            throw new Exception("Can't place " + flag + " on occupied point");
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

                /* Of the courier is idle, place it on the road it is on */
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

                    /* If the courier is on the road between one of the flags and
                    a building, pick the road with the flag */

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

                        /* Pick the road the worker's last point was on if the next
                           point is the new flag point */
                        if (nextPoint.equals(flagPoint)) {
                            if (newRoad1.getWayPoints().contains(lastPoint)) {
                                roadToAssign = newRoad1;
                            } else {
                                roadToAssign = newRoad2;
                            }

                        /* Pick the road the worker's next point is on if the next
                           point is not the new flag point */
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

        return flag;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public void placeWorker(Worker worker, EndPoint endPoint) {
        worker.setPosition(endPoint.getPosition());
        workers.add(worker);
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    public Collection<Point> getAvailableFlagPoints(Player player) {
        Set<Point> points = new HashSet<>();

        for (Land land : player.getLands()) {

            /* This iterates over a set and the order may be non-deterministic */
            for (Point point : land.getPointsInLand()) {
                if (!isAvailableFlagPoint(player, point)) {
                    continue;
                }

                points.add(point);
            }
        }

        return points;
    }

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

        /* Cannot build flag if all adjacent tiles are unbuildable */
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

        boolean diagonalFlagExists = false;

        for (Point d : point.getDiagonalPoints()) {
            if (player.isWithinBorder(d) && isFlagAtPoint(d)) {
                diagonalFlagExists = true;

                break;
            }
        }

        if (diagonalFlagExists) {
            return false;
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

    public Map<Point, Size> getAvailableHousePoints(Player player) {
        Map<Point, Size> housePoints = new HashMap<>();

        for (Land land : player.getLands()) {
            for (Point point : land.getPointsInLand()) {
                Size result = isAvailableHousePoint(player, point);

                if (result != null) {
                    housePoints.put(point, result);
                }
            }
        }

        return housePoints;
    }

    public List<Point> getPossibleAdjacentRoadConnections(Player player, Point start, Point end) {
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

        /* Find out which adjacent points are possible offroad connections */
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

    private boolean isVegetationCorrect(Building house, Point site) throws Exception {
        Size size = house.getSize();

        if (house.isMine()) {
            return terrain.isOnMountain(site);
        } else {
            switch (size) {
            case SMALL:
            case MEDIUM:

                /* Cannot build houses on mining mountain */
                if (terrain.isOnMountain(site)) {
                    return false;
                }

                /* Cannot build next to deep water */
                if (terrain.isNextToDeepWater(site)) {
                    return false;
                }

                /* Cannot build next to magenta */
                if (terrain.isNextToMagenta(site)) {
                    return false;
                }

                /* Cannot build next to swamp */
                if (terrain.isNextToSwamp(site)) {
                    return false;
                }

                /* Cannot build houses in the desert */
                if (terrain.isNextToDesert(site)) {
                    return false;
                }

                /* Cannot build houses next to snow */
                if (terrain.isNextToSnow(site)) {
                    return false;
                }

                /* Cannot build houses next to lava */
                if (terrain.isNextToLava(site)) {
                    return false;
                }

                if (terrain.isNextToWater(site)) {
                    return false;
                }

                if (terrain.isOnEdgeOf(site, MOUNTAIN)) {
                    return false;
                }

                return true;
            case LARGE:
                if (!terrain.getTileUpLeft(site.upLeft()).getVegetationType().canBuildFlags()   ||
                    !terrain.getTileAbove(site.upLeft()).getVegetationType().canBuildFlags()    ||
                    !terrain.getTileUpLeft(site.upRight()).getVegetationType().canBuildFlags()  ||
                    !terrain.getTileAbove(site.upRight()).getVegetationType().canBuildFlags()   ||
                    !terrain.getTileUpRight(site.upRight()).getVegetationType().canBuildFlags() ||
                    !terrain.isOnBuildable(site.left())      || !terrain.isOnBuildable(site.right()) ||
                    !terrain.isOnBuildable(site.downRight()) || !terrain.isOnBuildable(site.downLeft())) {
                    return false;
                }

                /* Large buildings cannot be built if the height difference to close points is too large */
                int heightAtPoint = getHeightAtPoint(site);
                if (Math.abs(heightAtPoint - getHeightAtPoint(site.left()))      > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE ||
                    Math.abs(heightAtPoint - getHeightAtPoint(site.upLeft()))    > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE ||
                    Math.abs(heightAtPoint - getHeightAtPoint(site.upRight()))   > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE ||
                    Math.abs(heightAtPoint - getHeightAtPoint(site.right()))     > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE ||
                    Math.abs(heightAtPoint - getHeightAtPoint(site.downRight())) > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE ||
                    Math.abs(heightAtPoint - getHeightAtPoint(site.downLeft()))  > MAX_HEIGHT_DIFFERENCE_FOR_LARGE_HOUSE) {
                    return false;
                }

                return terrain.isOnBuildable(site);
            default:
                throw new Exception("Can't handle house with unexpected size " + size);
            }
        }
    }

    public boolean isFlagAtPoint(Point point) {
        return pointToGameObject.get(point).isFlag();
    }

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

    public Flag getFlagAtPoint(Point point) throws Exception {
        MapPoint mp = pointToGameObject.get(point);

        if (!mp.isFlag()) {
            throw new Exception("There is no flag at " + point);
        }

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

        if (isPossibleAsAnyPointInRoad(player, point)) {
            return true;
        }

        return false;
    }

    private boolean isPossibleAsAnyPointInRoad(Player player, Point point) {
        MapPoint mp = pointToGameObject.get(point);

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

    public List<Point> getPossibleAdjacentRoadConnectionsIncludingEndpoints(Player player, Point from) {
        Point[] adjacentPoints  = from.getAdjacentPoints();
        List<Point>  resultList = new ArrayList<>();

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
     * Finds an offroad way from the start to the goal via the given point and without
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
     * Finds an offroad way from the start to the goal without using any points to avoid
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
    }

    Tree getTreeAtPoint(Point point) {
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

            if (!attachedBuilding.burningDown() && !attachedBuilding.destroyed()) {
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

        flags.remove(flag);
    }

    boolean isNextToWater(Point point) {
        for (Tile tile : terrain.getSurroundingTiles(point)) {
            if (tile.getVegetationType() == WATER) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the amount of the given mineral at the given point
     *
     * @param mineral Type of mineral
     * @param point The point to get the amount of mineral at
     * @return The amount of the given mineral at the given point
     */
    public int getAmountOfMineralAtPoint(Material mineral, Point point) {
        int amount = 0;

        for (Tile tile : terrain.getSurroundingTiles(point)) {
            amount += tile.getAmountOfMineral(mineral);
        }

        return amount;
    }

    /**
     * Returns the amount of fish at a given point
     *
     * @param point The point to get the amount of fish for
     * @return The amount of fish at the given point
     */
    public int getAmountFishAtPoint(Point point) {
        int amount = 0;

        for (Tile tile : terrain.getSurroundingTiles(point)) {
            amount += tile.getAmountFish();
        }

        return amount;
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
     *
     * @param mineral the type of mineral
     * @param amount the amount of mineral
     * @param point the point where the mineral was found
     */
    public void placeSign(Material mineral, Size amount, Point point) {
        Sign sign = new Sign(mineral, amount, point, this);

        getMapPoint(point).setSign(sign);

        signs.add(sign);
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
    }

    private void removeSign(Sign sign) {
        MapPoint mp = getMapPoint(sign.getPosition());

        mp.setSign(null);

        signs.remove(sign);
    }

    void removeWorker(Worker worker) {
        workersToRemove.add(worker);
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

        Point flagPoint = point.downRight();

        /* ALL CONDITIONS FOR SMALL */
        if (!isWithinMap(point.downRight())) {
            return null;
        }

        if (!player.isWithinBorder(point)) {
            return null;
        }

        if (isBuildingAtPoint(point)) {
            return null;
        }

        if (isFlagAtPoint(point)) {
            return null;
        }

        if (isStoneAtPoint(point)) {
            return null;
        }

        if (isTreeAtPoint(point)) {
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

        if (isRoadAtPoint(point)) {
            return null;
        }

        if (!isFlagAtPoint(flagPoint) && !isAvailableFlagPoint(player, flagPoint)) {
            return null;
        }

        if (isCropAtPoint(point)) {
            return null;
        }

        for (Point d : point.getDiagonalPointsAndSides()) {
            if (!player.isWithinBorder(d)) {
                continue;
            }

            if (isBuildingAtPoint(d)) {
                return null;
            }

            /* It's not possible to build a house next to a stone */
            if (isStoneAtPoint(d)) {
                return null;
            }
        }

        if (player.isWithinBorder(point.upRight()) && isFlagAtPoint(point.upRight())) {
            return null;
        }

        if (player.isWithinBorder(point.up().right()) && isBuildingAtPoint(point.up().right())) {
            if (getBuildingAtPoint(point.up().right()).getSize() == LARGE) {
                return null;
            }
        }

        if (player.isWithinBorder(point.down()) && isBuildingAtPoint(point.down())) {
            if (getBuildingAtPoint(point.down()).getSize() == LARGE) {
                return null;
            }
        }

        if (player.isWithinBorder(point.downRight().right()) && isBuildingAtPoint(point.downRight().right())) {
            if (getBuildingAtPoint(point.downRight().right()).getSize() == LARGE) {
                return null;
            }
        }

        if (player.isWithinBorder(point.down().right()) && isBuildingAtPoint(point.down().right())) {
            if (getBuildingAtPoint(point.down().right()).getSize() == LARGE) {
                return null;
            }
        }

        /* ADDITIONAL CONDITIONS FOR MEDIUM */

        /* ADDITIONAL CONDITIONS FOR LARGE */
        /* A large building can't have a tree directly left or right */
        if ((isWithinMap(point.left())  && isTreeAtPoint(point.left())) ||
            (isWithinMap(point.right()) && isTreeAtPoint(point.right()))) {
            return SMALL;
        }

        for (Point d : point.getDiagonalPoints()) {

            /* It's not possible to build a house next to a tree */
            if (isTreeAtPoint(d)) {
                return SMALL;
            }
        }

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

        if (player.isWithinBorder(point.downRight().down()) && isBuildingAtPoint(point.downRight().down())) {
            if (getBuildingAtPoint(point.downRight().down()).getSize() == LARGE) {
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
        return getMapPoint(flag.getPosition()).getConnectedRoads();
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
           This iterates over a collection and the order may be
           non-deterministic
        */
        for (Land land : player.getLands()) {
            for (Point point : land.getPointsInLand()) {

                /* Add the point if it's possible to build a mine there */
                if (isAvailableMinePoint(player, point)) {
                    availableMinePoints.add(point);
                }
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

            /* Filter lakes */
            if (getTerrain().isInWater(p)) {
                continue;
            }

            return p;
        }

        return null;
    }

    void removeWildAnimalWithinStepTime(WildAnimal animal) {
        animalsToRemove.add(animal);
    }

    /**
     * Places a hexagon-shaped mountain on the map with the given point in center.
     * @param point The center for the hexagon-shaped mountain
     */
    public void placeMountainHexagonOnMap(Point point) {

        terrain.placeMountainOnTile(point, point.left(), point.upLeft());
        terrain.placeMountainOnTile(point, point.upLeft(), point.upRight());
        terrain.placeMountainOnTile(point, point.upRight(), point.right());
        terrain.placeMountainOnTile(point, point.right(), point.downRight());
        terrain.placeMountainOnTile(point, point.downRight(), point.downLeft());
        terrain.placeMountainOnTile(point, point.downLeft(), point.left());
    }

    /**
     * Changes the tiles surrounding the given point to contain large amounts of
     * the given mineral.
     * @param point Point to surround with large quantities of mineral
     * @param material The type of mineral
     */
    public void surroundPointWithMineral(Point point, Material material) {
        for (Tile tile : terrain.getSurroundingTiles(point)) {
            tile.setAmountMineral(material, LARGE);
        }
    }

    /**
     * Changes the vegetation of the tiles surrounding the given point to water.
     * @param point Point to surround with water
     */
    public void surroundPointWithWater(Point point) {
        for (Tile tile : terrain.getSurroundingTiles(point)) {
            tile.setVegetationType(WATER);
        }
    }

    /**
     * Changes the vegetation of the tiles surrounding the given point to grass.
     * @param point Point to surround with grass
     */
    public void surroundPointWithGrass(Point point) {
        for (Tile tile : terrain.getSurroundingTiles(point)) {
            tile.setVegetationType(Vegetation.GRASS);
        }
    }

    void removeCropWithinStepTime(Crop crop) {
        cropsToRemove.add(crop);

        getMapPoint(crop.getPosition()).setCrop(null);
    }

    void replaceBuilding(Building upgradedBuilding, Point position) throws Exception {

        Building oldBuilding = getBuildingAtPoint(position);
        buildingsToRemove.add(oldBuilding);

        upgradedBuilding.setPosition(position);
        upgradedBuilding.setFlag(oldBuilding.getFlag());

        getMapPoint(position).removeBuilding();
        getMapPoint(position).setBuilding(upgradedBuilding);

        upgradedBuilding.getPlayer().removeBuilding(oldBuilding);
        upgradedBuilding.getPlayer().addBuilding(upgradedBuilding);

        buildingsToAdd.add(upgradedBuilding);
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
     * Finds the shortest path following roads between any two points. The points
     * don't need to be flags or buildings but can be any point on a road.
     *
     * @param start The flag or building to start from
     * @param end The flag or building to reach
     * @return the list of points with flags or buildings to pass by
     */
    public List<Point> findWayWithExistingRoadsInFlagsAndBuildings(EndPoint start, EndPoint end) {
        return GameUtils.findShortestPathViaRoads(start.getPosition(), end.getPosition(), pointToGameObject);
    }

    /**
     * Determines whether a list of points describes a valid path via roads.
     *
     * @param points List of each point in the planned path
     * @return true if the list of points follows existing roads
     */
    boolean isValidRouteViaRoads(Point... points) {

        Point previous = null;

        for (Point point : points) {

            if (previous != null) {
                MapPoint mp = pointToGameObject.get(previous);

                if (!mp.getConnectedNeighbors().contains(point)) {
                    return false;
                }
            }

            previous = point;
        }

        return true;
    }

    /**
     * Determines whether a list of points describes a valid path via roads.
     *
     * @param points List of each point in the planned path
     * @return true if the list of points follows existing roads
     */
    public boolean isValidRouteViaRoads(List<Point> points) {

        Point previous = null;

        for (Point point : points) {

            if (previous != null) {
                MapPoint mp = pointToGameObject.get(previous);

                if (!mp.getConnectedNeighbors().contains(point)) {
                    return false;
                }
            }
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

    public int getHeightAtPoint(Point point) {
        return getMapPoint(point).getHeight();
    }

    public void setHeightAtPoint(Point point, int height) {
        getMapPoint(point).setHeight(height);
    }
}
