package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.Crop.GrowthState.HARVESTED;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.MEDIUM;
import static org.appland.settlers.model.Size.SMALL;

public class GameMap {

    private List<Building>        buildings;
    private List<Road>            roads;
    private List<Flag>            flags;
    private List<Worker>          allWorkers;
    private String                theLeader = "Mai Thi Van Anh";
    private final int             height;
    private final int             width;
    private List<Point>           availableFlagPoints;
    private Terrain               terrain;
    private Map<Point, Size>      availableHouseSites;
    private List<Point>           fullGrid;
    private List<Point>           reservedPoints;
    private Map<Point, MapPoint>  pointToGameObject;
    private List<Tree>            trees;
    private List<Stone>           stones;
    private List<Crop>            crops;    

    private static Logger log = Logger.getLogger(GameMap.class.getName());

    private final int MINIMUM_WIDTH  = 5;
    private final int MINIMUM_HEIGHT = 5;
    
    public List<Point> findAutoSelectedRoad(Point start, Point goal, Collection<Point> avoid) {
        Set<Point> evaluated         = new HashSet<>();
        Set<Point> toEvaluate        = new HashSet<>();
        Map<Point, Double>  cost     = new HashMap<>();
        Map<Point, Double>  fullCost = new HashMap<>();
        Map<Point, Point>   cameFrom = new HashMap<>();
        
        if (avoid != null) {        
            evaluated.addAll(avoid);
        }
        
        toEvaluate.add(start);
        cost.put(start, (double)0);
        fullCost.put(start, cost.get(start) + start.distance(goal));
        
        while (!toEvaluate.isEmpty()) {
            Point currentPoint = null;
            double currentValue = -1;
            
            for (Entry<Point, Double> pair : fullCost.entrySet()) {
                
                if (!toEvaluate.contains(pair.getKey())) {
                    continue;
                }
                
                if (currentPoint == null) {
                    currentPoint = pair.getKey();
                    currentValue = pair.getValue();
                }

                if (currentValue > pair.getValue()) {
                    currentValue = pair.getValue();
                    currentPoint = pair.getKey();
                }
            }

            if (currentPoint.equals(goal)) {
                List<Point> path = new ArrayList<>();
                
                while (currentPoint != start) {
                    path.add(0, currentPoint);
                    
                    currentPoint = cameFrom.get(currentPoint);
                }
                
                path.add(0, start);

                return path;
            }
            
            toEvaluate.remove(currentPoint);
            evaluated.add(currentPoint);
            
            for (Point neighbor : getPossibleAdjacentRoadConnections(currentPoint, goal)) {
                if (evaluated.contains(neighbor)) {
                    continue;
                }
            
                double tentative_cost = cost.get(currentPoint) + currentPoint.distance(neighbor);

                if (!toEvaluate.contains(neighbor) || tentative_cost < cost.get(neighbor)) {
                    cameFrom.put(neighbor, currentPoint);
                    cost.put(neighbor, tentative_cost);
                    fullCost.put(neighbor, cost.get(neighbor) + neighbor.distance(goal));
                    
                    toEvaluate.add(neighbor);
                }
            }
        }
        
        return null;
    }

    private Integer estimateDistance(Point start, Point goal) {
        int deltaX = start.x - goal.x;
        int deltaY = start.y - goal.y;
        
        return deltaX * deltaX + deltaY + deltaY;
        
        //return min(abs(start.x - goal.x), abs(start.y - goal.y));
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

    private void removeRoad(Road r) throws Exception {        
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
        
        buildings           = new ArrayList<>();
        roads               = new ArrayList<>();
        flags               = new ArrayList<>();
        allWorkers          = new ArrayList<>();
        terrain             = new Terrain(width, height);
        reservedPoints      = new ArrayList<>();
        trees               = new ArrayList<>();
        stones              = new ArrayList<>();
        crops               = new ArrayList<>();
        
        fullGrid            = buildFullGrid();
        pointToGameObject   = populateMapPoints(fullGrid);
        availableFlagPoints = calculateAvailableFlagPoints();
        availableHouseSites = calculateAvailableHouseSites();
    }

    public void stepTime() {
        for (Worker w : allWorkers) {
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
    }

    public Building placeBuilding(Building house, Point p) throws Exception {
        house.setMap(this);
        
        if (buildings.contains(house)) {
            throw new Exception("Can't place " + house + " as it is already placed.");
        }

        if (!canPlaceHouse(house, p)) {
            throw new Exception("Can't place building on " + p + ".");
        }
        
        house.setPosition(p);
        
        buildings.add(house);

        Flag flag = house.getFlag();

        flag.setPosition(p.downRight());
        placeFlag(flag);

        reserveSpaceForBuilding(house);
        
        placeDriveWay(house);
        
        return house;
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
        List<Point> path1 = findWayWithExistingRoads(start, via);
        List<Point> path2 = findWayWithExistingRoads(via, end);
        
        path2.remove(0);
        
        path1.addAll(path2);
        
        return path1;
    }
    
    public List<Point> findWayWithExistingRoads(Point start, Point end) throws InvalidRouteException {
        log.log(Level.INFO, "Finding way from {0} to {1}", new Object[]{start, end});

        if (start.equals(end)) {
            throw new InvalidRouteException("Start and end are the same.");
        }
        
        List<Point> result = findWayWithMemory(start, end, new ArrayList<Point>());

        if (result == null) {
            log.log(Level.WARNING, "Failed to find a way from {0} to {1}", new Object[]{start, end});
            throw new InvalidRouteException("No route found from " + start + " to " + end + ".");
        }

        log.log(Level.FINE, "Returning found way {0}", result);
        return result;
    }

    private List<Point> findWayWithMemory(Point start, Point end, List<Point> visited) throws InvalidRouteException {
        log.log(Level.FINE, "Finding way from {0} to {1}, already visited {2}", new Object[]{start, end, visited});

        MapPoint mp = pointToGameObject.get(start);
        
        Collection<Point> connectingRoads = mp.getConnectedNeighbors();

        for (Point otherEnd : connectingRoads) {
            List<Point> result = new ArrayList<>();

            if (visited.contains(otherEnd)) {
                continue;
            }

            if (otherEnd.equals(end)) {
                result.add(start);
                result.add(end);
                return result;
            } else {
                visited.add(start);
            }

            List<Point> tmp = findWayWithMemory(otherEnd, end, visited);

            if (tmp != null) {
                result.add(start);

                result.addAll(tmp);

                return result;
            }
        }

        return null;
    }

    public boolean routeExist(Point point, Point point2) throws InvalidRouteException {
        try {
            findWayWithExistingRoads(point, point2);
        } catch (InvalidRouteException e) {
            return false;
        }

        return true;
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

    public Flag placeFlag(Point p) throws Exception {
        return placeFlag(new Flag(p));
    }
    
    public Flag placeFlag(Flag f) throws Exception {
        Point flagPoint = f.getPosition();
        
        if (!isAvailablePointForFlag(flagPoint)) {
            throw new Exception("Can't place " + f + " on occupied point");
        }
        
        if (isAlreadyPlaced(f)) {
            throw new Exception("Flag " + f + " is already placed on the map");
        }

        /* Handle the case where the flag is on an existing road that will be split */
        if (pointIsOnRoad(flagPoint)) {
            Road existingRoad = getRoadAtPoint(flagPoint);
            Courier courier   = existingRoad.getCourier();

            List<Point> points = existingRoad.getWayPoints();

            int index = points.indexOf(flagPoint);

            removeRoad(existingRoad);

            pointToGameObject.get(f.getPosition()).setFlag(f);
            flags.add(f);

            reserveSpaceForFlag(f);            
            
            Road newRoad1 = placeRoad(points.subList(0, index + 1));
            Road newRoad2 = placeRoad(points.subList(index, points.size()));
            
            /* Re-assign the courier to one of the new roads */
            if (courier != null) {
                Road roadToAssign = newRoad1;
                
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

            reserveSpaceForFlag(f);
        }

        return f;
    }

    public Storage getClosestStorage(Road r) {
        return getClosestStorage(r.getStart());
    }

    public Storage getClosestStorage(Point p) {
        Storage stg = null;
        int distance = Integer.MAX_VALUE;

        for (Building b : buildings) {
            if (b instanceof Storage) {
                try {
                    if (b.getFlag().getPosition().equals(p)) {
                        stg = (Storage)b;
                        break;
                    }
                    
                    List<Point> path = findWayWithExistingRoads(p, b.getFlag().getPosition());
                    
                    if (path.size() < distance) {
                        distance = path.size();
                        stg = (Storage) b;
                    }
                } catch (InvalidRouteException ex) {}
            }
        }

        return stg;
    }

    public List<Courier> getIdleWorkers() {
        List<Courier> result = new ArrayList<>();

        for (Worker w : allWorkers) {
            if (!(w instanceof Courier)) {
                continue;
            }
            
            if (w.getCargo() != null) {
                continue;
            }

            if (w.isTraveling()) {
                continue;
            }

            result.add((Courier)w);
        }

        return result;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public List<Building> getBuildingsWithNewProduce() {
        List<Building> result = new ArrayList<>();

        for (Building b : buildings) {
            if (b.isCargoReady()) {
                result.add(b);
            }
        }

        return result;
    }

    public List<Road> getRoadsThatNeedCouriers() {
        List<Road> result = new ArrayList<>();

        for (Road r : roads) {
            if (r.needsCourier()) {
                result.add(r);
            }
        }

        return result;
    }

    public List<Flag> getFlags() {
        return flags;
    }
    
    public void placeWorker(Worker w, Flag f) {
        w.setPosition(f.getPosition());
        allWorkers.add(w);
    }

    public List<Worker> getAllWorkers() {
        return allWorkers;
    }

    public List<Worker> getTravelingWorkers() {
        List<Worker> result = new ArrayList<>();

        for (Worker w : getAllWorkers()) {
            if (w.isTraveling()) {
                result.add(w);
            }
        }

        return result;
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

    public List<Point> getAvailableFlagPoints() {
        return availableFlagPoints;
    }
    
    public Set<Building> getBuildingsWithinReach(Flag startFlag) {
        return getBuildingsWithinReachWithMemory(startFlag.getPosition(), new ArrayList<Point>());
    }

    public Terrain getTerrain() {
        return terrain;
    }
    
    /* TODO: Replace this with an implementation of a* */
    private Set<Building> getBuildingsWithinReachWithMemory(Point start, List<Point> visited) {
        Set<Building> result = new HashSet<>();

        if (isFlagAtPoint(start) && isBuildingAtPoint(start.upLeft())) {
            try {
                Flag flag = getFlagAtPoint(start);
                Building building = getBuildingAtPoint(start.upLeft());
                
                if (flag.equals(building.getFlag())) {
                    result.add(getBuildingAtPoint(start.upLeft()));
                }
            } catch (Exception ex) {
                Logger.getLogger(GameMap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        MapPoint mp = pointToGameObject.get(start);
        
        Set<Point> connectedFlags = mp.getConnectedNeighbors();

        for (Point p : connectedFlags) {
            
            if (visited.contains(p)) {
                continue;
            }
            
            List<Point> visitedCopy = new ArrayList<>();
            visitedCopy.addAll(visited);
            visitedCopy.add(p);
            
            Set<Building> tmp = getBuildingsWithinReachWithMemory(p, visitedCopy);
            
            result.addAll(tmp);
        }
        
        return result;
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
    
    private List<Point> calculateAvailableFlagPoints() {
        List<Point> result      = new ArrayList<>();
        
        for (Point p : fullGrid) {
            /* Remove spots surrounded by water */
            if (!terrain.terrainMakesFlagPossible(p)) {
                continue;
            }            

            result.add(p);
        }
    
        return result;
    }

    private void reserveSpaceForBuilding(Building house) throws Exception {
        Point flagPoint  = house.getFlag().getPosition();
        Point housePoint = flagPoint.upLeft();
        
        /* Exact point points to house */
        pointToGameObject.get(housePoint).setBuilding(house);
        
        switch(house.getHouseSize()) {
        case SMALL:
            reserveSpaceForSmallHouse(house, housePoint);
            break;
        case MEDIUM:
            reserveSpaceForMediumHouse(house, housePoint);
            break;
        case LARGE:
            reserveSpaceForLargeHouse(house, housePoint);
            break;
        }
    }
    
    private void reserveSpaceForFlag(Flag f) {
        Point p = f.getPosition();
        Point diag1 = new Point(p.x - 1, p.y - 1);
        Point diag2 = new Point(p.x + 1, p.y - 1);
        Point diag3 = new Point(p.x - 1, p.y + 1);
        Point diag4 = new Point(p.x + 1, p.y + 1);
        
        markUnavailableForFlag(p);
        markUnavailableForFlag(diag1);
        markUnavailableForFlag(diag2);
        markUnavailableForFlag(diag3);
        markUnavailableForFlag(diag4);
        
        markUnavailableForHouse(p);
        
        setPointCovered(p);
    }
    
    private void setPointCovered(Point p) {
        reservedPoints.add(p);
        markUnavailableForFlag(p);
        markUnavailableForHouse(p);
    }
    
    private boolean isPointCovered(Point point) {
        return reservedPoints.contains(point);
    }

    private void reserveSpaceForLargeHouse(Building house, Point site) throws Exception {

        /* Mark map points that are covered by the house. The site itself is already marked */
        pointToGameObject.get(site.upRight()).setBuilding(house);
        pointToGameObject.get(site.upLeft()).setBuilding(house);
        
        /* Mark all points that this house covers */
        setPointCovered(site);
        
        /* Houses and flags can't be placed exactly where this house is */
        markUnavailableForFlag(site);
        markUnavailableForHouse(site);
        
        /* Mark spots where flags can't be built */
        markUnavailableForFlag(site.upLeft());
        markUnavailableForFlag(site.downLeft());
        markUnavailableForFlag(site.left());
        markUnavailableForFlag(site.downRight());
        markUnavailableForFlag(site.down());
        markUnavailableForFlag(site.right());
        markUnavailableForFlag(site.upRight());
        
        /* Mark spots where houses can't be built */
        markMaxHouseSizeForSpot(site.left().downLeft(), MEDIUM);
        markMaxHouseSizeForSpot(site.left().left(), MEDIUM);
        markMaxHouseSizeForSpot(site.upLeft().up(), MEDIUM);
        
        markUnavailableForHouse(site.left().down());
        markUnavailableForHouse(site.left().upLeft());
        markUnavailableForHouse(site.left().up());
        markUnavailableForHouse(site.up());
        markUnavailableForHouse(site.upRight());
        markUnavailableForHouse(site.upLeft());
        markUnavailableForHouse(site.downLeft());
        markUnavailableForHouse(site.left());
        markUnavailableForHouse(site.down());
        markUnavailableForHouse(site.right());
    }

    private void reserveSpaceForMediumHouse(Building house, Point site) throws Exception {
        
        /* Mark all points that this house covers */
        setPointCovered(site);
        setPointCovered(site.downRight());

        /* Mark spots where flags can't be built */
        markUnavailableForFlag(site.right().down());
        markUnavailableForFlag(site.right().downRight());
        markUnavailableForFlag(site.right());
        markUnavailableForFlag(site.downLeft());
        
        /* Mark spots where houses can't be built */
        markMaxHouseSizeForSpot(site.right().down(), MEDIUM);
        markMaxHouseSizeForSpot(site.right().downRight(), MEDIUM);
        markMaxHouseSizeForSpot(site.down(), MEDIUM);
        markMaxHouseSizeForSpot(site.left().downLeft(), MEDIUM);
        markMaxHouseSizeForSpot(site.left().down(), MEDIUM);
        markMaxHouseSizeForSpot(site.up(), MEDIUM);
        
        markUnavailableForHouse(site.downLeft());
        markUnavailableForHouse(site.left());
        markUnavailableForHouse(site.upLeft());
        markUnavailableForHouse(site.upRight());
        markUnavailableForHouse(site.right());
    }
    
    private void reserveSpaceForSmallHouse(Building house, Point site) throws Exception {
        
        /* Mark all points that this house covers */
        setPointCovered(site);

        /* Houses and flags can't be placed exactly where this house is */
        markUnavailableForFlag(site);
        markUnavailableForHouse(site);

        /* Mark spots where houses can't be built */
        markMaxHouseSizeForSpot(site.right().down(), MEDIUM);
        markMaxHouseSizeForSpot(site.right().downRight(), MEDIUM);
        markMaxHouseSizeForSpot(site.up(), MEDIUM);
        
        markUnavailableForHouse(site.down());
        markUnavailableForHouse(site.downLeft());
        markUnavailableForHouse(site.left());
        markUnavailableForHouse(site.upLeft());
        markUnavailableForHouse(site.upRight());
        markUnavailableForHouse(site.right());
        
        /* Mark spots where flags can't be built */
        markUnavailableForFlag(site.right().downRight());
        markUnavailableForFlag(site.downLeft());
    
    
    }

    public void terrainIsUpdated() {
        availableFlagPoints = calculateAvailableFlagPoints();
        
        availableHouseSites = calculateAvailableHouseSites();
    }

    public Map<Point, Size> getAvailableHousePoints() {
        return availableHouseSites;
    }

    public List<Point> getPossibleAdjacentRoadConnections(Point point, Point end) {
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

    private Iterable<Point> getPossibleAdjacentOffRoadConnections(Point point) {
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
    
    private Map<Point, Size> calculateAvailableHouseSites() {
        Map<Point, Size> result = new HashMap<>();
        
        for (Point p : fullGrid) {
            if (canBuildLargeHouse(p)) {
                result.put(p, LARGE);
            } else if (canBuildMediumHouse(p)) {
                result.put(p, MEDIUM);
            } else if (canBuildSmallHouse(p)) {
                result.put(p, SMALL);
            }
        }

        return result;
    }

    private boolean canBuildSmallHouse(Point site) {
        return terrain.isOnGrass(site) && !isPointCovered(site);
    }

    private boolean canBuildLargeHouse(Point site) {
        boolean closeAreaClear = terrain.isOnGrass(site);
        boolean wideAreaClear = true;
    
        if (isPointCovered(site)) {
            return false;
        }
        
        for (Point p : site.getAdjacentPoints()) {
            if (!terrain.isOnGrass(p)) {
                wideAreaClear = false;
                
                break;
            }
        
            if (isPointCovered(p)) {
                wideAreaClear = false;
                
                break;
            }
        }
    
        return closeAreaClear && wideAreaClear;
    }

    private boolean canBuildMediumHouse(Point site) {
        boolean areaClear;
        boolean borderClear;
        
        if (isPointCovered(site)) {
            return false;
        }
        
        areaClear = terrain.isOnGrass(site);
        
        borderClear = true;
        
        for(Point p : site.getAdjacentPoints()) {
            if (isPointCovered(p)) {
                borderClear = false;
                
                break;
            }
        }
        
        return areaClear && borderClear;
    }

    private boolean canPlaceHouse(Building house, Point site) throws Exception {
        Size size = house.getHouseSize();
    
        switch (size) {
        case SMALL:
            return canBuildSmallHouse(site);
        case MEDIUM:
            return canBuildMediumHouse(site);
        case LARGE:
            return canBuildLargeHouse(site);
        default:
            throw new Exception("Can't handle house with unexpected size " + size);
        }
    }

    private void markUnavailableForHouse(Point point) {
        availableHouseSites.remove(point);
    }

    private void markUnavailableForFlag(Point point) {
        availableFlagPoints.remove(point);
    }

    public boolean isFlagAtPoint(Point p) {
        return pointToGameObject.get(p).isFlag();
    }

    private boolean isAlreadyPlaced(Flag f) {
        return flags.contains(f);
    }

    private void markMaxHouseSizeForSpot(Point down, Size size) {
        Size currentSize = availableHouseSites.get(down);

        if (size == SMALL) {
            availableHouseSites.put(down, size);
        } else if (size == MEDIUM && currentSize == LARGE) {
            availableHouseSites.put(down, size);
        }
    }

    private boolean isAvailablePointForFlag(Point position) {
        return availableFlagPoints.contains(position);
    }

    private boolean isWithinMap(Point p) {
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
    
    private boolean isPossibleAsEndPointInRoad(Point p) {
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

    private boolean isPossibleAsAnyPointInRoad(Point p) {
        MapPoint mp = pointToGameObject.get(p);

        if (!isWithinMap(p)) {
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
        
        return true;
    }

    public List<Point> getPossibleRoadConnectionsExcludingEndpoints(Point point) {
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
    
    public List<Point> getPossibleAdjacentRoadConnectionsIncludingEndpoints(Point point) {
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

    private List<Road> wayPointsToRoads(List<Point> path) throws Exception {
        List<Road> result = new ArrayList<>();
        
        Point previous = null;

        for (Point p : path) {
            if (previous == null) {
                previous = p;
                
                continue;
            }
            
            if (!isFlagAtPoint(p)) {
                continue;
            }
            
            result.add(getRoad(previous, p));

            previous = p;
        }    

        return result;
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
        Set<Point> evaluated        = new HashSet<>();
        Set<Point> toEvaluate       = new HashSet<>();
        Map<Point, Double> cost     = new HashMap<>();
        Map<Point, Double> fullCost = new HashMap<>();
        Map<Point, Point> cameFrom  = new HashMap<>();
        
        if (avoid != null) {        
            evaluated.addAll(avoid);
        }

        toEvaluate.add(start);
        cost.put(start, (double)0);
        fullCost.put(start, cost.get(start) + start.distance(goal));

        while (!toEvaluate.isEmpty()) {
            Point currentPoint = null;
            double currentValue = -1;
            
            for (Entry<Point, Double> pair : fullCost.entrySet()) {
                
                if (!toEvaluate.contains(pair.getKey())) {
                    continue;
                }
                
                if (currentPoint == null) {
                    currentPoint = pair.getKey();
                    currentValue = pair.getValue();
                }

                if (currentValue > pair.getValue()) {
                    currentValue = pair.getValue();
                    currentPoint = pair.getKey();
                }
            }

            if (currentPoint.equals(goal)) {
                List<Point> path = new ArrayList<>();
                
                while (currentPoint != start) {
                    path.add(0, currentPoint);
                    
                    currentPoint = cameFrom.get(currentPoint);
                }
                
                path.add(0, start);

                return path;
            }
            
            toEvaluate.remove(currentPoint);
            evaluated.add(currentPoint);
            
            for (Point neighbor : getPossibleAdjacentOffRoadConnections(currentPoint)) {
                if (evaluated.contains(neighbor)) {
                    continue;
                }
            
                double tentative_cost = cost.get(currentPoint) + currentPoint.distance(neighbor);

                if (!toEvaluate.contains(neighbor) || tentative_cost < cost.get(neighbor)) {
                    cameFrom.put(neighbor, currentPoint);
                    cost.put(neighbor, tentative_cost);
                    fullCost.put(neighbor, cost.get(neighbor) + estimateDistance(neighbor, goal));
                    
                    toEvaluate.add(neighbor);
                }
            }
        }
        
        return null;
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

    public Iterable<Tree> getTrees() {
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

    public Iterable<Point> getPointsWithinRadius(Point point, int radius) {
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

    public Iterable<Stone> getStones() {
        return stones;
    }

    public Crop getCropAtPoint(Point point) {
        return pointToGameObject.get(point).getCrop();
    }

    public Iterable<Crop> getCrops() {
        return crops;
    }
}
