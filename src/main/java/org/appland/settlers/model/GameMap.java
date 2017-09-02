package org.appland.settlers.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import org.appland.settlers.model.GameUtils.ConnectionsProvider;
import static org.appland.settlers.model.GameUtils.findShortestPath;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.MEDIUM;
import static org.appland.settlers.model.Size.SMALL;
import org.appland.settlers.model.Tile.Vegetation;
import static org.appland.settlers.model.Tile.Vegetation.MOUNTAIN;
import org.appland.settlers.policy.Constants;

public class GameMap {

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
    private final List<Point>          fullGrid;
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
        MapPoint p = pointToGameObject.get(point);

        Iterable<Road> roadsFromPoint = p.getConnectedRoads();

        for (Road r : roadsFromPoint) {
            if (!r.getFlags()[0].getPosition().equals(point) &&
                !r.getFlags()[1].getPosition().equals(point)) {

                return r;
            }
        }

        return null;
    }

    public void removeRoad(Road r) throws Exception {

        if (r.getCourier() != null) {
            r.getCourier().returnToStorage();
        }

        removeRoadButNotWorker(r);
    }

    private synchronized void removeRoadButNotWorker(Road r) throws Exception {

        roads.remove(r);

        for (Point p : r.getWayPoints()) {
            MapPoint mp = pointToGameObject.get(p);

            mp.removeConnectingRoad(r);
        }
    }

    public GameMap(List<Player> playersToSet, int w, int h) throws Exception {

        if (playersToSet.isEmpty()) {
            throw new Exception("Can't create game map with no players");
        }

        players = playersToSet;
        width = w;
        height = h;

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

        fullGrid            = buildFullGrid();
        pointToGameObject   = populateMapPoints(fullGrid);

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

        for (Projectile p : projectiles) {
            p.stepTime();
        }

        for (Worker w : workers) {
            w.stepTime();
        }

        for (Building b : buildings) {
            b.stepTime();
        }

        for (Tree t : trees) {
            t.stepTime();
        }

        for (Crop c : crops) {
            c.stepTime();
        }

        for (Sign s : signs) {
            s.stepTime();
        }

        for (WildAnimal w : wildAnimals) {
            w.stepTime();
        }

        /* Possibly add wild animals */
        handleWildAnimalPopulation();

        /* Remove completely mined stones */
        List<Stone> stonesToRemove = new ArrayList<>();
        for (Stone s : stones) {
            if (s.noMoreStone()) {
                stonesToRemove.add(s);
            }
        }

        for (Stone s : stonesToRemove) {
            removeStone(s);
        }

        /* Resume transport of stuck cargos */
        for (Flag f : flags) {
            for (Cargo cargo : f.getStackedCargo()) {
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
        for (Building removedBuilding: buildingsToRemove) {
            buildings.remove(removedBuilding);
        }

        for (Building addedBuilding: buildingsToAdd) {
            buildings.add(addedBuilding);
        }

        /* Remove buildings that have been destroyed some time ago */
        buildings.removeAll(buildingsToRemove);

        for (Building b : buildingsToRemove) {
            b.getPlayer().removeBuilding(b);
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

    public synchronized <T extends Building> T placeBuilding(T house, Point p) throws Exception {
        log.log(Level.INFO, "Placing {0} at {1}", new Object[]{house, p});

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

            for (Building b : house.getPlayer().getBuildings()) {
                if (b instanceof Headquarter) {
                    headquarterPlaced = true;

                    break;
                }
            }

            if (headquarterPlaced) {
                throw new Exception("Can only have one headquarter placed per player");
            }
        }

        if (!firstHouse && !house.getPlayer().isWithinBorder(p)) {
            throw new Exception("Can't place building on " + p + " because it's outside the border");
        }

        /* Verify that the building is not placed within another player's border */
        for (Player player : players) {
            if (!player.equals(house.getPlayer()) && player.isWithinBorder(p)) {
                throw new Exception("Can't place building on " + p + " within another player's border");
            }
        }

        if (!isVegetationCorrect(house, p)) {
            throw new Exception("Can't place building on " + p + ".");
        }

        /* Verify that the flag can be placed */
        if (!isFlagAtPoint(p.downRight()) && 
            !firstHouse && 
            !isAvailableFlagPoint(house.getPlayer(), p.downRight())) {
            throw new Exception("Can't place flag for building at " + p);
        }

        /* Handle the case where there is a sign at the site */
        if (isSignAtPoint(p)) {
            removeSign(getSignAtPoint(p));
        }

        /* Verify that there is no crop blocking the construction */
        if (isCropAtPoint(p)) {
            throw new InvalidUserActionException("Cannot place building on crop");
        }

        /* Use the existing flag if it exists, otherwise place a new flag */
        if (isFlagAtPoint(p.downRight())) {
            house.setFlag(getFlagAtPoint(p.downRight()));
        } else {
            Flag flag = house.getFlag();

            flag.setPosition(p.downRight());

            if (firstHouse) {
                placeFlagRegardlessOfBorder(flag);
            } else {
                placeFlag(flag);
            }
        }

        house.setPosition(p);
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

        getMapPoint(p).setBuilding(house);

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
        for (Building b : allBuildings) {

            /* Filter non-military buildings and un-occupied military buildings */
            if (!b.isMilitaryBuilding() || !b.ready() || !b.occupied()) {
                continue;
            }

            /* Store the claim for each military building
               This iterates over a collection and the order may be
               non-deterministic
            */
            for (Point p : b.getDefendedLand()) {
                if (!claims.containsKey(p)) {
                    claims.put(p, b);
                } else if (calculateClaim(b, p) > calculateClaim(claims.get(p), p)) {
                    claims.put(p, b);
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
                        if (!borders.contains(point)) {
                            borders.add(point);
                        }

                        globalCleared.add(p);

                    /* Add points outside the claimed areas to the border */
                    } else if (!claims.containsKey(p)) {
                        if (!borders.contains(point)) {
                            borders.add(point);
                        }

                        globalCleared.add(p);

                    /* Add the point to the border if it belongs to another player */
                    } else if (!claims.get(p).getPlayer().equals(player)) {
                        if (!borders.contains(point)) {
                            borders.add(point);
                        }
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
        for (Building b : buildings) {
            if (b.burningDown()) {
                continue;
            }

            if (b.isMilitaryBuilding()) {
                continue;
            }

            Player player = b.getPlayer();

            if (!player.isWithinBorder(b.getPosition()) || !player.isWithinBorder(b.getFlag().getPosition())) {
                b.tearDown();
            }
        }

        /* Remove flags now outside of the borders */
        List<Flag> flagsToRemove = new LinkedList<>();

        for (Flag f : flags) {
            Player player = f.getPlayer();

            if (!player.isWithinBorder(f.getPosition())) {
                flagsToRemove.add(f);
            }
        }

        /* Remove the flags */
        for (Flag f : flagsToRemove) {
            removeFlagWithoutSideEffects(f);
        }

        /* Remove any roads now outside of the borders */
        Set<Road> roadsToRemove = new HashSet<>();

        for (Road r : roads) {

            /* Only remove each road once */
            if (roadsToRemove.contains(r)) {
                continue;
            }

            Player player = r.getPlayer();

            for (Point p : r.getWayPoints()) {

                /* Filter points within the border */
                if (player.isWithinBorder(p)) {
                    continue;
                }

                /* Keep the driveways for military buildings */
                if (r.getWayPoints().size() == 2) {

                    /* Check if the connected building is military */
                    if ((isBuildingAtPoint(r.getStart()) && 
                         getBuildingAtPoint(r.getStart()).isMilitaryBuilding()) ||
                        (isBuildingAtPoint(r.getEnd())   && 
                         getBuildingAtPoint(r.getEnd()).isMilitaryBuilding())) {
                        continue;
                    }
                }

                /* Remember to remove the road */
                roadsToRemove.add(r);
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

    public synchronized Road placeRoad(Player player, List<Point> wayPoints) throws Exception {
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
        for (Point p : wayPoints) {
            if (!player.isWithinBorder(p)) {
                throw new Exception("Can't place road " + wayPoints + "with " + p + " outside the border");
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

        for (Point p : wayPoints) {
            if (p.equals(start)) {
                continue;
            }

            if (p.equals(end) && isPossibleAsEndPointInRoad(player, p)) {
                continue;
            }

            if (isPossibleAsAnyPointInRoad(player, p)) {
                continue;
            }

            throw new Exception(p + " in road is invalid");
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
        return Collections.unmodifiableList(roads);
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

    public Road getRoad(Point start, Point end) throws Exception {
        for (Road r : roads) {
            if ((r.getStart().equals(start) && r.getEnd().equals(end))
                    || (r.getEnd().equals(start) && r.getStart().equals(end))) {
                return r;
            }
        }

        return null;
    }

    public synchronized Flag placeFlag(Player player, Point p) throws Exception {

        /* Verify that the player is valid */
        if (!players.contains(player)) {
            throw new Exception("Can't place flag at " + p + " because the player is invalid.");
        }

        return placeFlag(new Flag(player, p));
    }

    private Flag placeFlag(Flag f) throws Exception {
        return doPlaceFlag(f, true);
    }

    private Flag placeFlagRegardlessOfBorder(Flag flag) throws Exception {
        return doPlaceFlag(flag, false);
    }    

    private synchronized Flag doPlaceFlag(Flag flag, boolean checkBorder) throws Exception {
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

            if (index < 2 || points.size() - index < 3) {
                throw new Exception("Splitting road creates too short roads");
            }

            removeRoadButNotWorker(existingRoad);

            pointToGameObject.get(flag.getPosition()).setFlag(flag);
            flags.add(flag);

            Road newRoad1 = placeRoad(flag.getPlayer(), points.subList(0, index + 1));
            Road newRoad2 = placeRoad(flag.getPlayer(), points.subList(index, points.size()));

            /* Re-assign the courier to one of the new roads */
            if (courier != null) {
                Road roadToAssign = null;

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

    public Storage getClosestStorage(Point p) throws InvalidRouteException {
        return getClosestStorage(p, null);
    }

    public Storage getClosestStorage(Point p, Building avoid) throws InvalidRouteException {
        Storage stg = null;
        int distance = Integer.MAX_VALUE;

        for (Building b : buildings) {
            if (b.equals(avoid)) {
                continue;
            }

            if (b instanceof Storage) {
                if (b.getFlag().getPosition().equals(p)) {
                    stg = (Storage)b;
                    break;
                }

                List<Point> path = findWayWithExistingRoads(p, b.getFlag().getPosition());

                if (path == null) {
                    continue;
                }

                if (path.size() < distance) {
                    distance = path.size();
                    stg = (Storage) b;
                }
            }
        }

        return stg;
    }

    public List<Building> getBuildings() {
        return Collections.unmodifiableList(buildings);
    }

    public List<Flag> getFlags() {
        return Collections.unmodifiableList(flags);
    }

    public synchronized void placeWorker(Worker w, EndPoint e) {
        w.setPosition(e.getPosition());
        workers.add(w);
    }

    public List<Worker> getWorkers() {
        return Collections.unmodifiableList(workers);
    }

    public List<Point> getAvailableFlagPoints(Player player) throws Exception {
        List<Point> points = new LinkedList<>();

        for (Land land : player.getLands()) {

            /* This iterates over a set and the order may be non-deterministic */
            for (Point p : land.getPointsInLand()) {
                if (!isAvailableFlagPoint(player, p)) {
                    continue;
                }

                points.add(p);
            }
        }

        return points;
    }

    public boolean isAvailableFlagPoint(Player player, Point p) {
        return isAvailableFlagPoint(player, p, true);
    }

    private boolean isAvailableFlagPoint(Player player, Point p, boolean checkBorder) {
        if (!isWithinMap(p)) {
            return false;
        }

        if (checkBorder && !player.isWithinBorder(p)) {
            return false;
        }

        if (isFlagAtPoint(p)) {
            return false;
        }

        if (isStoneAtPoint(p)) {
            return false;
        }

        if (isTreeAtPoint(p)) {
            return false;
        }

        if (terrain.isInWater(p)) {
            return false;
        }

        if (isCropAtPoint(p) &&
            getCropAtPoint(p).getGrowthState() != Crop.GrowthState.HARVESTED) {
            return false;
        }

        boolean diagonalFlagExists = false;

        for (Point d : p.getDiagonalPoints()) {
            if (player.isWithinBorder(d) && isFlagAtPoint(d)) {
                diagonalFlagExists = true;

                break;
            }
        }

        if (diagonalFlagExists) {
            return false;
        }

        if (player.isWithinBorder(p.right()) && isFlagAtPoint(p.right())) {
            return false;
        }

        if (player.isWithinBorder(p.left()) && isFlagAtPoint(p.left())) {
            return false;
        }

        if (isBuildingAtPoint(p)) {
            return false;
        }

        if (player.isWithinBorder(p.downRight()) && isBuildingAtPoint(p.downRight())) {
            if (getBuildingAtPoint(p.downRight()).getSize() == LARGE) {
                return false;
            }
        }

        if (player.isWithinBorder(p.right()) && isBuildingAtPoint(p.right())) {
            if (getBuildingAtPoint(p.right()).getSize() == LARGE) {
                return false;
            }
        }

        if (player.isWithinBorder(p.downLeft()) && isBuildingAtPoint(p.downLeft())) {
            if (getBuildingAtPoint(p.downLeft()).getSize() == LARGE) {
                return false;
            }
        }

        return true;
    }

    public Set<Building> getBuildingsWithinReach(Flag startFlag) {
        List<Point> toEvaluate  = new LinkedList<>();
        Set<Point> visited      = new HashSet<>();
        Set<Building> reachable = new HashSet<>();

        toEvaluate.add(startFlag.getPosition());

        /* Declare variables outside of the loop to keep memory churn down */
        Point point;
        Point oppositePoint;

        while (!toEvaluate.isEmpty()) {

            point = toEvaluate.get(0);
            toEvaluate.remove(point);

            /* Test if this point is connected to a building */
            if (isBuildingAtPoint(point)) {
                reachable.add(getBuildingAtPoint(point));
            }

            /* Remember that this point has been tested */
            visited.add(point);

            /* Go through the neighbors and add the new points to the list to be evaluated */
            for (Road road : getMapPoint(point).getConnectedRoads()) {

                oppositePoint = road.getOtherPoint(point);

                /* Filter already visited */
                if (visited.contains(oppositePoint)) {
                    continue;
                }

                /* Add the point to the list */
                toEvaluate.add(oppositePoint);
            }
        }

        return reachable;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    private List<Point> buildFullGrid() {
        List<Point> result = new ArrayList<>();
        boolean rowFlip    = true;
        boolean columnFlip;

        /* Place all possible flag points in the list */
        int x, y;
        for (y = 1; y < height; y++) {
            columnFlip = rowFlip;

            for (x = 1; x < width; x++) {
                if (columnFlip) {
                    result.add(new Point(x, y));
                }

                columnFlip = !columnFlip;
            }

            rowFlip = !rowFlip;
        }

        return result;
    }

    public Map<Point, Size> getAvailableHousePoints(Player player) throws Exception {
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

    public List<Point> getPossibleAdjacentRoadConnections(Player player, Point point, Point end) throws Exception {
        Point[] adjacentPoints = new Point[] {
            new Point(point.x - 2, point.y),
            new Point(point.x + 2, point.y),
            new Point(point.x - 1, point.y - 1),
            new Point(point.x - 1, point.y + 1),
            new Point(point.x + 1, point.y - 1),
            new Point(point.x + 1, point.y + 1),
        };

        List<Point> resultList = new ArrayList<>();

        for (Point p : adjacentPoints) {
            if (p.equals(end) && isPossibleAsEndPointInRoad(player, p)) {
                resultList.add(p);
            } else if (!p.equals(end) && isPossibleAsAnyPointInRoad(player, p)) {
                resultList.add(p);
            }
        }

        return resultList;
    }

    private Iterable<Point> getPossibleAdjacentOffRoadConnections(Point point) throws Exception {
        Point[] adjacentPoints  = point.getAdjacentPoints();
        List<Point>  resultList = new ArrayList<>();

        /* Houses can only be left via the driveway so handle this case separately*/
        if (isBuildingAtPoint(point) && getBuildingAtPoint(point).getPosition().equals(point)) {
            resultList.add(point.downRight());

            return resultList;
        }

        /* Find out which adjacent points are possible offroad connections */
        for (Point p : adjacentPoints) {

            /* Filter points outside the map */
            if (!isWithinMap(p)) {
                continue;
            }

            /* Buildings can only be reached from their flags */
            if (isBuildingAtPoint(p) && !getBuildingAtPoint(p).getFlag().getPosition().equals(point)) {
                continue;
            }

            /* Filter points in water */
            if (terrain.isInWater(p)) {
                continue;
            }

            /* Filter points with stones */
            if (isStoneAtPoint(p)) {
                continue;
            }

            /* Add the point to the list if it passed the filters */
            resultList.add(p);
        }

        resultList.remove(point.up());

        resultList.remove(point.down());

        return resultList;
    }

    private boolean isVegetationCorrect(Building house, Point site) throws Exception {
        Size size = house.getSize();

        if (house.isMine()) {
            return terrain.isOnMountain(site);
        } else {        
            switch (size) {
            case SMALL:
            case MEDIUM:
                if (terrain.isOnMountain(site)) {
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
                boolean wideAreaClear = true;

                for (Point p : site.getAdjacentPoints()) {
                    if (!terrain.isOnGrass(p)) {
                        wideAreaClear = false;

                        break;
                    }
                }

                return terrain.isOnGrass(site) && wideAreaClear;
            default:
                throw new Exception("Can't handle house with unexpected size " + size);
            }
        }
    }

    public boolean isFlagAtPoint(Point p) {
        return pointToGameObject.get(p).isFlag();
    }

    public boolean isWithinMap(Point p) {
        return p.x > 0 && p.x < width && p.y > 0 && p.y < height;
    }

    private void addRoadToMapPoints(Road road) throws Exception {    
        for (Point p : road.getWayPoints()) {
            MapPoint mapPoint = pointToGameObject.get(p);

            mapPoint.addConnectingRoad(road);
        }
    }

    private Map<Point, MapPoint> populateMapPoints(List<Point> fullGrid) {
        Map<Point, MapPoint> resultMap = new HashMap<>();

        for (Point p : fullGrid) {
            resultMap.put(p, new MapPoint(p));
        }

        return resultMap;
    }

    public Flag getFlagAtPoint(Point p) throws Exception {
        MapPoint mp = pointToGameObject.get(p);

        if (!mp.isFlag()) {
            throw new Exception("There is no flag at " + p);
        }

        return mp.getFlag();
    }

    private boolean isPossibleAsEndPointInRoad(Player player, Point p) throws Exception {

        if (!isWithinMap(p)) {
            return false;
        }

        MapPoint mp = pointToGameObject.get(p);

        if (mp.isFlag() && player.isWithinBorder(p)) {
            return true;
        }

        if (isPossibleAsAnyPointInRoad(player, p)) {
            return true;
        }

        return false;
    }

    private boolean isPossibleAsAnyPointInRoad(Player player, Point point) throws Exception {
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

        if (mp.isCrop(point)) {
            return false;
        }

        if (!player.isWithinBorder(point)) {
            return false;
        }

        if (terrain.isInWater(point)) {
            return false;
        }

        return true;
    }

    public List<Point> getPossibleRoadConnectionsExcludingEndpoints(Player player, Point point) throws Exception {
        Point[] adjacentPoints  = point.getAdjacentPoints();
        List<Point>  resultList = new ArrayList<>();

        for (Point p : adjacentPoints) {
            if (isPossibleAsAnyPointInRoad(player, p)) {
                resultList.add(p);
            }
        }

        resultList.remove(point.up());

        resultList.remove(point.down());

        return resultList;        
    }

    public List<Point> getPossibleAdjacentRoadConnectionsIncludingEndpoints(Player player, Point point) throws Exception {
        Point[] adjacentPoints  = point.getAdjacentPoints();
        List<Point>  resultList = new ArrayList<>();

        for (Point p : adjacentPoints) {
            if (isPossibleAsEndPointInRoad(player, p)) {
                resultList.add(p);
            } else if (isPossibleAsAnyPointInRoad(player, p)) {
                resultList.add(p);
            }
        }

        resultList.remove(point.up());

        resultList.remove(point.down());

        return resultList;
    }

    public Building getBuildingAtPoint(Point p) {
        MapPoint mp = pointToGameObject.get(p);

        return mp.getBuilding();
    }

    public boolean isBuildingAtPoint(Point p) {
        return getBuildingAtPoint(p) != null;
    }

    public boolean isRoadAtPoint(Point p) {
        MapPoint mp = pointToGameObject.get(p);

        return !mp.getConnectedNeighbors().isEmpty();
    }

    public boolean isTreeAtPoint(Point point) {
        MapPoint mp = pointToGameObject.get(point);

        return mp.getTree() != null;
    }

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

    public List<Point> findWayOffroad(Point start, Point goal, 
            Collection<Point> avoid) {
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

    public Tree placeTree(Point position) throws Exception {
        MapPoint mp = pointToGameObject.get(position);

        if (mp.isFlag()) {
            throw new Exception("Can't place tree on " + position + " on existing flag");
        } else if (mp.isRoad()) {
            throw new Exception("Can't place tree on " + position + " on existing road");
        } else if (mp.isBuilding()) {
            throw new Exception("Can't place tree on " + position + " on existing building");
        } else if (mp.isStone()) {
            throw new Exception("Can't place tree on " + position + " on existing stone");
        }

        if (getTerrain().isOnMountain(position)) {
            throw new Exception("Can't place tree on a mountain");
        }

        Tree tree = new Tree(position);

        mp.setTree(tree);

        trees.add(tree);

        return tree;
    }

    public Collection<Tree> getTrees() {
        return Collections.unmodifiableCollection(trees);
    }

    void removeTree(Point position) {
        MapPoint mp = pointToGameObject.get(position);

        Tree tree = mp.getTree();

        mp.removeTree();

        trees.remove(tree);
    }

    Tree getTreeAtPoint(Point p) {
        MapPoint mp = pointToGameObject.get(p);

        return mp.getTree();
    }

    public Stone placeStone(Point point) {
        MapPoint mp = pointToGameObject.get(point);

        Stone stone = new Stone(point);

        mp.setStone(stone);

        stones.add(stone);

        return stone;
    }

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

    public boolean isCropAtPoint(Point p) {
        MapPoint mp = pointToGameObject.get(p);

        return mp.getCrop() != null;
    }

    public boolean isStoneAtPoint(Point point2) {
        MapPoint mp = pointToGameObject.get(point2);

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

    public List<Point> getPointsWithinRadius(Point point, int radius) {
        List<Point> result = new ArrayList<>();

        int x;
        int y;
        boolean rowFlip = false;

        for (y = point.y - radius; y <= point.y + radius; y++) {
            int startX = point.x - radius;

            if (rowFlip) {
                startX++;
            }

            for (x = startX; x <= point.x + radius; x += 2) {
                Point p = new Point(x, y);
                if (isWithinMap(p) && point.distance(p) <= radius) {
                    result.add(p);
                }
            }

            rowFlip = !rowFlip;
        }

        return result;
    }

    public List<Stone> getStones() {
        return Collections.unmodifiableList(stones);
    }

    public Crop getCropAtPoint(Point point) {
        return pointToGameObject.get(point).getCrop();
    }

    public Iterable<Crop> getCrops() {
        return Collections.unmodifiableList(crops);
    }

    private void removeStone(Stone s) {
        MapPoint mp = pointToGameObject.get(s.getPosition());

        mp.setStone(null);

        stones.remove(s);
    }

    public void removeFlag(Flag flag) throws Exception {
        MapPoint mpUpLeft = pointToGameObject.get(flag.getPosition().upLeft());
        MapPoint mp = pointToGameObject.get(flag.getPosition());

        /* Destroy the house if the flag is connected to a house */
        if (mpUpLeft.isBuilding() && flag.equals(mpUpLeft.getBuilding().getFlag())) {
            Building attachedBuilding = mpUpLeft.getBuilding();

            attachedBuilding.tearDown();
        }

        /* Remove the road if the flag is an endpoint to a road */
        List<Road> roadsToRemove = new LinkedList<>();
        for (Road r : mp.getConnectedRoads()) {
            if (r.getStartFlag().equals(flag) || r.getEndFlag().equals(flag)) {
                roadsToRemove.add(r);
            }
        }

        for (Road r : roadsToRemove) {
            removeRoad(r);
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
        for (Tile t : terrain.getSurroundingTiles(point)) {
            if (t.getVegetationType() == Vegetation.WATER) {
                return true;
            }
        }

        return false;
    }

    public int getAmountOfMineralAtPoint(Material mineral, Point point) {
        int amount = 0;

        for (Tile t : terrain.getSurroundingTiles(point)) {
            amount += t.getAmountOfMineral(mineral);
        }

        return amount;
    }

    public int getAmountFishAtPoint(Point point) {
        int amount = 0;

        for (Tile t : terrain.getSurroundingTiles(point)) {
            amount += t.getAmountFish();
        }

        return amount;
    }

    public Cargo catchFishAtPoint(Point position) throws Exception {
        for (Tile t : terrain.getSurroundingTiles(position)) {
            if (t.getAmountFish() > 0) {
                t.consumeFish();

                return new Cargo(FISH, this);
            }
        }

        throw new Exception("Can't find any fish to catch at " + position);
    }

    public Cargo mineMineralAtPoint(Material mineral, Point position) throws Exception {
        for (Tile t : terrain.getSurroundingTiles(position)) {
            if (t.getAmountOfMineral(mineral) > 0) {
                t.mine(mineral);

                return new Cargo(mineral, this);
            }
        }

        throw new Exception("Can't find any gold to mine at " + position);
    }

    public Sign getSignAtPoint(Point point) {
        return getMapPoint(point).getSign();
    }

    private MapPoint getMapPoint(Point point) {
        return pointToGameObject.get(point);
    }

    public void placeSign(Material mineral, Size amount, Point point) {
        Sign sign = new Sign(mineral, amount, point, this);

        getMapPoint(point).setSign(sign);

        signs.add(sign);
    }

    public boolean isSignAtPoint(Point point) {
        return getMapPoint(point).getSign() != null;
    }

    public Collection<Sign> getSigns() {
        return Collections.unmodifiableCollection(signs);
    }

    public void placeEmptySign(Point point) {
        placeSign(null, null, point);
    }

    void removeSignWithinStepTime(Sign sign) {
        MapPoint mp = getMapPoint(sign.getPosition());

        mp.setSign(null);

        signsToRemove.add(sign);
    }

    void removeSign(Sign sign) {
        MapPoint mp = getMapPoint(sign.getPosition());

        mp.setSign(null);

        signs.remove(sign);
    }

    void removeWorker(Worker w) {
        workersToRemove.add(w);
    }

    void removeBuilding(Building b) {
        MapPoint mp = getMapPoint(b.getPosition());

        mp.removeBuilding();

        buildingsToRemove.add(b);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    void placeWorkerFromStepTime(Donkey donkey, Building home) {
        donkey.setPosition(home.getPosition());
        workersToAdd.add(donkey);
    }

    void discoverPointsWithinRadius(Player player, Point center, int radius) {
        for (Point p : getPointsWithinRadius(center, radius)) {
            player.discover(p);
        }
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    private double calculateClaim(Building b, Point p) {
        double radius = b.getDefenceRadius();
        double distance = b.getPosition().distance(p);

        return radius / distance;
    }

    private boolean allPlayersHaveUniqueColor() {
        List<Color> usedColors = new ArrayList<>();

        for (Player p : players) {
            if (usedColors.contains(p.getColor())) {
                return false;
            }

            usedColors.add(p.getColor());
        }

        return true;
    }

    /**
     * Tells whether a house can be placed on the given point and if so, what
     * size of house.
     * @param player The player that would build the house
     * @param point The point that the house would be placed on
     * @return The max size of the potential house, otherwise null.
     */
    public Size isAvailableHousePoint(Player player, Point point) {

        Size result     = null;
        Point flagPoint = point.downRight();

        /* ALL CONDITIONS FOR SMALL */
        if (!isWithinMap(point.downRight())) {
            return result;
        }

        if (!player.isWithinBorder(point)) {
            return result;
        }

        if (isBuildingAtPoint(point)) {
            return result;
        }

        if (isFlagAtPoint(point)) {
            return result;
        }

        if (isStoneAtPoint(point)) {
            return result;
        }

        if (isTreeAtPoint(point)) {
            return result;
        }

        if (terrain.isOnMountain(point)) {
            return result;
        }

        if (terrain.isNextToWater(point)) {
            return result;
        }

        if (terrain.isOnEdgeOf(point, MOUNTAIN)) {
            return result;
        }

        if (isRoadAtPoint(point)) {
            return result;
        }

        if (!isFlagAtPoint(flagPoint) && !isAvailableFlagPoint(player, flagPoint)) {
            return result;
        }

        if (isCropAtPoint(point)) {
            return result;
        }

        for (Point d : point.getDiagonalPointsAndSides()) {
            if (!player.isWithinBorder(d)) {
                continue;
            }

            if (isBuildingAtPoint(d)) {
                return result;
            }

            /* It's not possible to build a house next to a stone */
            if (isStoneAtPoint(d)) {
                return result;
            }
        }

        if (player.isWithinBorder(point.upRight()) && isFlagAtPoint(point.upRight())) {
            return result;
        }

        if (player.isWithinBorder(point.up().right()) && isBuildingAtPoint(point.up().right())) {
            if (getBuildingAtPoint(point.up().right()).getSize() == LARGE) {
                return result;
            }
        }

        if (player.isWithinBorder(point.down()) && isBuildingAtPoint(point.down())) {
            if (getBuildingAtPoint(point.down()).getSize() == LARGE) {
                return result;
            }
        }

        if (player.isWithinBorder(point.downRight().right()) && isBuildingAtPoint(point.downRight().right())) {
            if (getBuildingAtPoint(point.downRight().right()).getSize() == LARGE) {
                return result;
            }
        }

        if (player.isWithinBorder(point.down().right()) && isBuildingAtPoint(point.down().right())) {
            if (getBuildingAtPoint(point.down().right()).getSize() == LARGE) {
                return result;
            }
        }

        result = SMALL;

        /* ADDITIONAL CONDITIONS FOR MEDIUM */

        result = MEDIUM;

        /* ADDITIONAL CONDITIONS FOR LARGE */
        if (player.isWithinBorder(point.upLeft()) && isFlagAtPoint(point.upLeft())) {
            return result;
        }

        if (player.isWithinBorder(point.down()) && isBuildingAtPoint(point.down())) {
            return result;
        }

        if (player.isWithinBorder(point.left()) && isFlagAtPoint(point.left())) {
            return result;
        }

        if (player.isWithinBorder(point.upRight().right()) && isBuildingAtPoint(point.upRight().right())) {
            if (getBuildingAtPoint(point.upRight().right()).getSize() != SMALL) {
                return result;
            }
        }

        if (player.isWithinBorder(point.up().right()) && isBuildingAtPoint(point.up().right())) {
            if (getBuildingAtPoint(point.up().right()).getSize() != SMALL) {
                return result;
            }
        }

        if (player.isWithinBorder(point.right().right()) && isBuildingAtPoint(point.right().right())) {
            if (getBuildingAtPoint(point.right().right()).getSize() == LARGE) {
                return result;
            }
        }

        if (player.isWithinBorder(point.downRight().down()) && isBuildingAtPoint(point.downRight().down())) {
            if (getBuildingAtPoint(point.downRight().down()).getSize() == LARGE) {
                return result;
            }
        }

        /* A large building needs a larger free area on grass */
        boolean wideAreaClear = true;

        for (Point p : point.getAdjacentPoints()) {
            if (!terrain.isOnGrass(p)) {
                wideAreaClear = false;

                break;
            }
        }

        if (!terrain.isOnGrass(point) || !wideAreaClear) {
            return result;
        }

        result = LARGE;

        return result;
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
        return Collections.unmodifiableList(projectiles);
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
        return Collections.unmodifiableList(wildAnimals);
    }

    private void handleWildAnimalPopulation() throws Exception {

        double density = (double)wildAnimals.size() / (double)(width * height);

        if (density < Constants.WILD_ANIMAL_NATURAL_DENSITY) {
            if (animalCountdown.reachedZero()) {

                /* Find point to place new wild animal on */
                Point point = findRandomPossiblePointToPlaceFreeMovingActor();

                if (point == null) {
                    return;
                }

                /* Place the new wild animal */
                WildAnimal animal = new WildAnimal(this);

                animal.setPosition(point);
                wildAnimals.add(animal);

                animalCountdown.countFrom(Constants.WILD_ANIMAL_TIME_BETWEEN_REPOPULATION);
            } else if (!animalCountdown.isActive()) {
                animalCountdown.countFrom(Constants.WILD_ANIMAL_TIME_BETWEEN_REPOPULATION);
            } else {
                animalCountdown.step();
            }
        }
    }

    Point findRandomPossiblePointToPlaceFreeMovingActor() throws Exception {

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
     * @throws Exception 
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
     * @throws Exception 
     */
    public void surroundPointWithMineral(Point point, Material material) {
        for (Tile t : terrain.getSurroundingTiles(point)) {
            t.setAmountMineral(material, LARGE);
        }
    }

    /**
     * Changes the vegetation of the tiles surrounding the given point to water.
     * @param point Point to surround with water
     */
    public void surroundPointWithWater(Point point) {
        for (Tile t : terrain.getSurroundingTiles(point)) {
            t.setVegetationType(Vegetation.WATER);
        }
    }

    /**
     * Changes the vegetation of the tiles surrounding the given point to grass.
     * @param point Point to surround with grass
     * @throws Exception 
     */
    public void surroundPointWithGrass(Point point) {
        for (Tile t : terrain.getSurroundingTiles(point)) {
            t.setVegetationType(Vegetation.GRASS);
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
     * @return Returns the winngin player of the game if there is a winner. Otherwise null.
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
}
