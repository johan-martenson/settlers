package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

public class GameMap {

    private List<Building>          buildings;
    private List<Building>          buildingsToRemove;
    private List<Road>              roads;
    private List<Flag>              flags;
    private List<Sign>              signs;
    private List<Sign>              signsToRemove;
    private List<Worker>            workers;
    private List<Worker>            workersToRemove;
    private String                  theLeader = "Mai Thi Van Anh";
    private final int               height;
    private final int               width;
    private Terrain                 terrain;
    private List<Point>             fullGrid;
    private Map<Point, MapPoint>    pointToGameObject;
    private List<Tree>              trees;
    private List<Stone>             stones;
    private List<Crop>              crops;
    private List<Land>              ownedLands;
    private List<Point>             fieldOfView;
    private List<Point>             discoveredLand;
    private List<Worker>            workersToAdd;
    private List<Player>            players;

    private static final Logger log = Logger.getLogger(GameMap.class.getName());

    private final int MINIMUM_WIDTH  = 5;
    private final int MINIMUM_HEIGHT = 5;

    public List<Point> findAutoSelectedRoad(Point start, Point goal, Collection<Point> avoid) {
        return findShortestPath(start, goal, avoid, new GameUtils.ConnectionsProvider() {

            @Override
            public Iterable<Point> getPossibleConnections(Point start, Point goal) {
                try {
                    return getPossibleAdjacentRoadConnections(start, goal);
                } catch (Exception ex) {
                    Logger.getLogger(GameMap.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                return new LinkedList<>();
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

    private void removeRoadButNotWorker(Road r) throws Exception {
        roads.remove(r);

        for (Point p : r.getWayPoints()) {
            MapPoint mp = pointToGameObject.get(p);

            mp.removeConnectingRoad(r);
        }
    }

    public GameMap(int w, int h) throws Exception {
        width = w;
        height = h;

        if (width < MINIMUM_WIDTH || height < MINIMUM_HEIGHT) {
            throw new Exception("Can't create too small map (" + width + "x" + height + ")");
        }
        
        buildings         = new ArrayList<>();
        buildingsToRemove = new LinkedList<>();
        roads             = new ArrayList<>();
        flags             = new ArrayList<>();
        signs             = new ArrayList<>();
        signsToRemove     = new LinkedList<>();
        workers           = new ArrayList<>();
        workersToRemove   = new LinkedList<>();
        terrain           = new Terrain(width, height);
        trees             = new ArrayList<>();
        stones            = new ArrayList<>();
        crops             = new ArrayList<>();
        discoveredLand    = new LinkedList<>();
        workersToAdd      = new LinkedList<>();
        ownedLands        = new LinkedList<>();
        players           = new LinkedList<>();

        fullGrid          = buildFullGrid();
        pointToGameObject = populateMapPoints(fullGrid);

        /* Add default player */
        players.add(new Player(theLeader));
    }

    public void stepTime() {
        workersToRemove.clear();
        workersToAdd.clear();
        signsToRemove.clear();
        buildingsToRemove.clear();
        
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
        
        /* Remove signs that have expired during this round */
        signs.removeAll(signsToRemove);
        
        /* Remove buildings that have been destroyed some time ago */
        buildings.removeAll(buildingsToRemove);
    }

    public Building placeBuilding(Building house, Point p) throws Exception {
        log.log(Level.INFO, "Placing {0} at {1}", new Object[]{house, p});
        
        boolean firstHouse = false;
        
        if (buildings.contains(house)) {
            throw new Exception("Can't place " + house + " as it is already placed.");
        }

        /* Verify that the house's player is valid if it is set */
        if (house.getPlayer() != null && !players.contains(house.getPlayer())) {
            throw new Exception("Can't place " + house + ", player " + house.getPlayer() + " is not valid.");
        }
        
        /* Handle the first building separately */
        if (buildings.isEmpty()) {
            if (! (house instanceof Headquarter)) {
                throw new Exception("Can not place " + house + " as initial building");
            }
            
            firstHouse = true;
        }
        
        if (!firstHouse && !isWithinBorder(p)) {
            throw new Exception("Can't place building on " + p + " because it's outside the border");
        }

        if (!isVegetationCorrect(house, p)) {
            throw new Exception("Can't place building on " + p + ".");
        }
        
        /* Handle the case where there is a sign at the site */
        if (isSignAtPoint(p)) {
            removeSign(getSignAtPoint(p));
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
        
        buildings.add(house);

        /* Set the player in the building */
        house.setPlayer(players.get(0));
        
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
        
        /* Re-calculate borders */
        ownedLands = Land.calculateLandWithinBorders(getBuildings());
        
        /* Update field of view */
        updateDiscoveredLand();
        fieldOfView = calculateFieldOfView(discoveredLand);

        /* Destroy buildings now outside of the borders */
        for (Building b : buildings) {
            if (b.burningDown()) {
                continue;
            }
            
            if (!isWithinBorder(b.getPosition()) || !isWithinBorder(b.getFlag().getPosition())) {
                b.tearDown();
            }
        }

        /* Remove flags now outside of the borders */
        List<Flag> flagsToRemove = new LinkedList<>();

        for (Flag f : flags) {
            if (!isWithinBorder(f.getPosition())) {
                flagsToRemove.add(f);
            }
        }

        flags.removeAll(flagsToRemove);
        
        /* Remove any roads now outside of the borders */
        List<Road> roadsToRemove = new LinkedList<>();
        
        for (Road r : roads) {
            for (Point p : r.getWayPoints()) {
                if (!isWithinBorder(p)) {
                    roadsToRemove.add(r);
                }
            }
        }
    
        roads.removeAll(roadsToRemove);
    }
    
    private Road placeDriveWay(Building building) throws Exception {
        List<Point> wayPoints = new ArrayList<>();
        
        wayPoints.add(building.getPosition());
        wayPoints.add(building.getFlag().getPosition());
        
        Road road = new Road(building, wayPoints, building.getFlag());
        
        road.setNeedsCourier(false);
        
        roads.add(road);
    
        addRoadToMapPoints(road);
        
        return road;
    }

    public Road placeRoad(Point... points) throws Exception {
        return placeRoad(Arrays.asList(points));
    }
    
    public Road placeRoad(List<Point> wayPoints) throws Exception {
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
            if (!isWithinBorder(p)) {
                throw new Exception("Can't place road with " + p + " outside the border");
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
            
            if (p.equals(end) && isPossibleAsEndPointInRoad(p)) {
                continue;
            }
            
            if (isPossibleAsAnyPointInRoad(p)) {
                continue;
            }

            throw new Exception(p + " in road is invalid");
        }
        
        Flag startFlag = getFlagAtPoint(start);
        Flag endFlag   = getFlagAtPoint(end);

        Road road = new Road(startFlag, wayPoints, endFlag);

        /* Set the default player if there is only one player */
        if (players.size() == 1) {
            road.setPlayer(players.get(0));
        }

        roads.add(road);

        addRoadToMapPoints(road);
        
        return road;
    }

    public Road placeAutoSelectedRoad(Flag startFlag, Flag endFlag) throws Exception {
        return placeAutoSelectedRoad(startFlag.getPosition(), endFlag.getPosition());
    }
    
    public Road placeAutoSelectedRoad(Point start, Point end) throws Exception {
        List<Point> wayPoints = findAutoSelectedRoad(start, end, null);
        
	if (wayPoints == null) {
            throw new InvalidEndPointException(end);
        }

        return placeRoad(wayPoints);
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

        return findShortestPath(start, end, null, new ConnectionsProvider() {

            @Override
            public Iterable<Point> getPossibleConnections(Point start, Point goal) {
                MapPoint mp = pointToGameObject.get(start);
                    
                return mp.getConnectedNeighbors();
            }
        });
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

    public Flag placeFlag(Player player, Point p) throws Exception {

        /* Verify that the player is valid */
        if (!players.contains(player)) {
            throw new Exception("Can't place flag at " + p + " because the player is invalid.");
        }

        return placeFlag(new Flag(p));
    }

    public Flag placeFlag(Point p) throws Exception {
        return placeFlag(new Flag(p));
    }
    
    public Flag placeFlag(Flag f) throws Exception {
        return doPlaceFlag(f, true);
    }
    
    private Flag placeFlagRegardlessOfBorder(Flag flag) throws Exception {
        return doPlaceFlag(flag, false);
    }    
    
    private Flag doPlaceFlag(Flag f, boolean checkBorder) throws Exception {
        log.log(Level.INFO, "Placing {0}", new Object[]{f});
        
        Point flagPoint = f.getPosition();
        
        if (isAlreadyPlaced(f)) {
            throw new Exception("Flag " + f + " is already placed on the map");
        }

        if (!isPossibleFlagPoint(flagPoint)) {
            throw new Exception("Can't place " + f + " on occupied point");
        }
        
        if (checkBorder && !isWithinBorder(f.getPosition())) {
            throw new Exception("Can't place flag at " + f.getPosition() + " outside of the border");
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

            pointToGameObject.get(f.getPosition()).setFlag(f);
            flags.add(f);

            Road newRoad1 = placeRoad(points.subList(0, index + 1));
            Road newRoad2 = placeRoad(points.subList(index, points.size()));
            
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
            pointToGameObject.get(f.getPosition()).setFlag(f);
            flags.add(f);
        }

        /* Set the default player if there is only one player */
        if (players.size() == 1) {
            f.setPlayer(players.get(0));
        }
        
        return f;
    }

    public Storage getClosestStorage(Point p) {
        return getClosestStorage(p, null);
    }
    
    public Storage getClosestStorage(Point p, Building avoid) {
        Storage stg = null;
        int distance = Integer.MAX_VALUE;

        for (Building b : buildings) {
            if (b.equals(avoid)) {
                continue;
            }
            
            if (b instanceof Storage) {
                try {
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
                } catch (InvalidRouteException ex) {}
            }
        }

        return stg;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public List<Flag> getFlags() {
        return flags;
    }
    
    public void placeWorker(Worker w, EndPoint e) {
        w.setPosition(e.getPosition());
        workers.add(w);
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    public List<Storage> getStorages() {
        List<Storage> storages = new ArrayList<>();

        for (Building b : buildings) {
            if (b instanceof Storage) {
                storages.add((Storage) b);
            }
        }

        return storages;
    }

    public List<Point> getAvailableFlagPoints() throws Exception {
        List<Point> points = new LinkedList<>();

        for (Land land : ownedLands) {
            for (Point p : land.getPointsInLand()) {
                if (!isAvailableFlagPoint(p)) {
                    continue;
                }
                
                points.add(p);
            }
        }
    
        return points;
    }
    
    private boolean isAvailableFlagPoint(Point p) throws Exception {
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
        
        boolean diagonalFlagExists = false;

        for (Point d : p.getDiagonalPoints()) {
            if (isWithinBorder(d) && isFlagAtPoint(d)) {
                diagonalFlagExists = true;
            }
        }

        if (diagonalFlagExists) {
            return false;
        }

        if (isWithinBorder(p.right()) && isFlagAtPoint(p.right())) {
            return false;
        }

        if (isWithinBorder(p.left()) && isFlagAtPoint(p.left())) {
            return false;
        }

        if (isBuildingAtPoint(p)) {
            return false;
        }

        if (isWithinBorder(p.downRight()) && isBuildingAtPoint(p.downRight())) {
            if (getBuildingAtPoint(p.downRight()).getSize() == LARGE) {
                return false;
            }
        }

        if (isWithinBorder(p.right()) && isBuildingAtPoint(p.right())) {
            if (getBuildingAtPoint(p.right()).getSize() == LARGE) {
                return false;
            }
        }

        if (isWithinBorder(p.downLeft()) && isBuildingAtPoint(p.downLeft())) {
            if (getBuildingAtPoint(p.downLeft()).getSize() == LARGE) {
                return false;
            }
        }

        return true;
    }

    public Set<Building> getBuildingsWithinReach(Flag startFlag) {
        List<Point> toEvaluate = new LinkedList<>();
        List<Point> visited = new LinkedList<>();
        Set<Building> reachable = new HashSet<>();
        
        toEvaluate.add(startFlag.getPosition());
        
        while (!toEvaluate.isEmpty()) {
            Point point = toEvaluate.get(0);
            toEvaluate.remove(point);
            
            if (isBuildingAtPoint(point)) {
                reachable.add(getBuildingAtPoint(point));
            }
            
            visited.add(point);
            
            MapPoint mp = pointToGameObject.get(point);
            Set<Point> neighbors = new HashSet<>();
            neighbors.addAll(mp.getConnectedNeighbors());
            
            neighbors.removeAll(visited);
            toEvaluate.addAll(neighbors);
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

    public Map<Point, Size> getAvailableHousePoints() throws Exception {
        Map<Point, Size> housePoints = new HashMap<>();
        boolean diagonalHouse;

        for (Land land : ownedLands) {
            for (Point point : land.getPointsInLand()) {

                /* ALL CONDITIONS FOR SMALL */
                if (isBuildingAtPoint(point)) {
                    continue;
                }

                if (isFlagAtPoint(point)) {
                    continue;
                }
                
                if (isStoneAtPoint(point)) {
                    continue;
                }
                
                if (isTreeAtPoint(point)) {
                    continue;
                }
                
                if (terrain.isInWater(point)) {
                    continue;
                }
                
                if (isRoadAtPoint(point)) {
                    continue;
                }
                
                diagonalHouse = false;
                
                for (Point d : point.getDiagonalPointsAndSides()) {
                    if (!isWithinBorder(d)) {
                        continue;
                    }
                
                    if (isBuildingAtPoint(d)) {
                        diagonalHouse = true;
                    }
                }
                
                if (diagonalHouse) {
                    continue;
                }
                
                if (isWithinBorder(point.upRight()) && isFlagAtPoint(point.upRight())) {
                    continue;
                }

                if (isWithinBorder(point.up().right()) && isBuildingAtPoint(point.up().right())) {
                    if (getBuildingAtPoint(point.up().right()).getSize() == LARGE) {
                        continue;
                    }
                }

                if (isWithinBorder(point.down()) && isBuildingAtPoint(point.down())) {
                    if (getBuildingAtPoint(point.down()).getSize() == LARGE) {
                        continue;
                    }
                }

                if (isWithinBorder(point.downRight().right()) && isBuildingAtPoint(point.downRight().right())) {
                    if (getBuildingAtPoint(point.downRight().right()).getSize() == LARGE) {
                        continue;
                    }
                }

                if (isWithinBorder(point.down().right()) && isBuildingAtPoint(point.down().right())) {
                    if (getBuildingAtPoint(point.down().right()).getSize() == LARGE) {
                        continue;
                    }
                }
                
                housePoints.put(point, SMALL);
                
                /* ADDITIONAL CONDITIONS FOR MEDIUM */
                
                housePoints.put(point, MEDIUM);
                
                /* ADDITIONAL CONDITIONS FOR LARGE */
                if (isWithinBorder(point.upLeft()) && isFlagAtPoint(point.upLeft())) {
                    continue;
                }

                if (isWithinBorder(point.down()) && isBuildingAtPoint(point.down())) {
                    continue;
                }

                if (isWithinBorder(point.left()) && isFlagAtPoint(point.left())) {
                    continue;
                }
                
                if (isWithinBorder(point.upRight().right()) && isBuildingAtPoint(point.upRight().right())) {
                    if (getBuildingAtPoint(point.upRight().right()).getSize() != SMALL) {
                        continue;
                    }
                }

                if (isWithinBorder(point.up().right()) && isBuildingAtPoint(point.up().right())) {
                    if (getBuildingAtPoint(point.up().right()).getSize() != SMALL) {
                        continue;
                    }
                }

                if (isWithinBorder(point.right().right()) && isBuildingAtPoint(point.right().right())) {
                    if (getBuildingAtPoint(point.right().right()).getSize() == LARGE) {
                        continue;
                    }
                }

                if (isWithinBorder(point.downRight().down()) && isBuildingAtPoint(point.downRight().down())) {
                    if (getBuildingAtPoint(point.downRight().down()).getSize() == LARGE) {
                        continue;
                    }
                }

                housePoints.put(point, LARGE);
            }
        }

        return housePoints;
    }

    public List<Point> getPossibleAdjacentRoadConnections(Point point, Point end) throws Exception {
        Point[] adjacentPoints  = point.getAdjacentPoints();
        List<Point>  resultList = new ArrayList<>();
        
        for (Point p : adjacentPoints) {
            if (p.equals(end) && isPossibleAsEndPointInRoad(p)) {
                resultList.add(p);
            } else if (isPossibleAsAnyPointInRoad(p)) {
                resultList.add(p);
            }
        }
    
        resultList.remove(point.up());
        
        resultList.remove(point.down());
        
        return resultList;
    }

    private Iterable<Point> getPossibleAdjacentOffRoadConnections(Point point) throws Exception {
        Point[] adjacentPoints  = point.getAdjacentPoints();
        List<Point>  resultList = new ArrayList<>();
        
        for (Point p : adjacentPoints) {
            if (!isWithinMap(p)) {
                continue;
            }
            
            if (isBuildingAtPoint(p)) {
                if (!getBuildingAtPoint(p).getPosition().equals(p)) {
                    continue;
                }
            }
            
            if (terrain.isInWater(p)) {
                continue;
            }
            
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
                return terrain.isOnGrass(site);
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

    private boolean isAlreadyPlaced(Flag f) {
        return flags.contains(f);
    }

    boolean isWithinMap(Point p) {
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
    
    private boolean isPossibleAsEndPointInRoad(Point p) throws Exception {
        if (!isWithinMap(p)) {
            return false;
        }

        if (isPossibleAsAnyPointInRoad(p)) {
            return true;
        }
        
        MapPoint mp = pointToGameObject.get(p);
        
        if (mp.isFlag()) {
            return true;
        }
        
        return false;
    }

    private boolean isPossibleAsAnyPointInRoad(Point p) throws Exception {
        MapPoint mp = pointToGameObject.get(p);

        if (!isWithinBorder(p)) {
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

        if (terrain.isInWater(p)) {
            return false;
        }

        if (mp.isCrop(p)) {
            return false;
        }

        return true;
    }

    public List<Point> getPossibleRoadConnectionsExcludingEndpoints(Point point) throws Exception {
        Point[] adjacentPoints  = point.getAdjacentPoints();
        List<Point>  resultList = new ArrayList<>();
        
        for (Point p : adjacentPoints) {
            if (isPossibleAsAnyPointInRoad(p)) {
                resultList.add(p);
            }
        }
    
        resultList.remove(point.up());
        
        resultList.remove(point.down());
        
        return resultList;        
    }
    
    public List<Point> getPossibleAdjacentRoadConnectionsIncludingEndpoints(Point point) throws Exception {
        Point[] adjacentPoints  = point.getAdjacentPoints();
        List<Point>  resultList = new ArrayList<>();
        
        for (Point p : adjacentPoints) {
            if (isPossibleAsEndPointInRoad(p)) {
                resultList.add(p);
            } else if (isPossibleAsAnyPointInRoad(p)) {
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

    public List<Point> findWayOffroad(Point start, Point goal, Point via, Collection<Point> avoid) {
        List<Point> path1 = findWayOffroad(start, via, avoid);
        List<Point> path2 = findWayOffroad(via, goal, avoid);
        
        path2.remove(0);
        
        path1.addAll(path2);
        
        return path1;
    }
    
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
        });
    }

    public Tree placeTree(Point position) throws Exception {
        MapPoint mp = pointToGameObject.get(position);
    
        if (mp.isFlag() || mp.isRoad() || mp.isBuilding() || mp.isStone()) {
            throw new Exception("Can't place tree on " + position);
        }
        
        Tree tree = new Tree(position);
        
        mp.setTree(tree);
        
        trees.add(tree);
        
        return tree;
    }

    public Collection<Tree> getTrees() {
        return trees;
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
        
        Crop crop = new Crop(point);

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

    int getDistanceForPath(List<Point> path) {
        int distance = 0;
        Point previous = null;
        
        for (Point current : path) {
            if (previous == null) {
                previous = current;

                continue;
            }

            distance += previous.distance(current);
        }
        
        return distance;
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
        return stones;
    }

    public Crop getCropAtPoint(Point point) {
        return pointToGameObject.get(point).getCrop();
    }

    public Iterable<Crop> getCrops() {
        return crops;
    }

    private void removeStone(Stone s) {
        MapPoint mp = pointToGameObject.get(s.getPosition());
        
        mp.setStone(null);
        
        stones.remove(s);
    }

    public List<Collection<Point>> getBorders() {
        List<Collection<Point>> result = new LinkedList<>();

        for (Land land : ownedLands) {
            result.add(land.getBorder());
        }

        return result;
    }

    public boolean isWithinBorder(Point position) {
        for (Land land : ownedLands) {
            if (land.isWithinBorder(position)) {
                return true;
            }
        }

        return false;
    }

    private boolean isPossibleFlagPoint(Point flagPoint) throws Exception {
        MapPoint mp = pointToGameObject.get(flagPoint);
        
        if (mp.isStone() || mp.isTree() || mp.isBuilding()) {
            return false;
        }

        if (!isAvailableFlagPoint(flagPoint)) {
            return false;
        }

        return true;
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
        
        /* Remove the flag */
        mp.removeFlag();
        
        flags.remove(flag);
    }

    public List<Point> getFieldOfView() {
        return fieldOfView;
    }

    public List<Point> getDiscoveredLand() {
        return discoveredLand;
    }

    private void updateDiscoveredLand() {
        for (Building b : buildings) {
            if (b.isMilitaryBuilding()) {
                discoveredLand.addAll(b.getDiscoveredLand());
            }
        }
    }

    private List<Point> calculateFieldOfView(Collection<Point> discoveredLand) {
        return GameUtils.hullWanderer(discoveredLand);
    }

    boolean isNextToWater(Point p) throws Exception {
        for (Tile t : terrain.getSurroundingTiles(p)) {
            if (t.getVegetationType() == Vegetation.WATER) {
                return true;
            }
        }
        
        return false;
    }

    public int getAmountOfMineralAtPoint(Material mineral, Point point) throws Exception {
        int amount = 0;
        
        for (Tile t : terrain.getSurroundingTiles(point)) {
            amount += t.getAmountOfMineral(mineral);
        }
        
        return amount;
    }

    public int getAmountFishAtPoint(Point point) throws Exception {
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
        return signs;
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

    void removeBuilding(Building b) throws Exception {
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

    void discoverPointsWithinRadius(Point center, int radius) {
        for (Point p : getPointsWithinRadius(center, radius)) {
            if (!discoveredLand.contains(p)) {
                discoveredLand.add(p);
            }
        }

        fieldOfView = calculateFieldOfView(discoveredLand);
    }

    public void placeRoad(Player player1, Point... points) throws Exception {
        if (!players.contains(player1)) {
            throw new Exception("Can't place road at " + points + " because the player is invalid.");
        }
    }

    public List<Player> getPlayers() {
        return players;
    }
}
