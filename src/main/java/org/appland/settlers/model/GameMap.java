package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameMap {

    private List<Building>        buildings;
    private List<Road>            roads;
    private List<Flag>            flags;
    private Map<Flag, List<Flag>> roadNetwork;
    private Map<Road, Courier>    roadToWorkerMap;
    private List<Worker>          allWorkers;
    private String                theLeader = "Mai Thi Van Anh";
    private final int             height;
    private final int             width;
    private List<Point>           availableFlagPoints;

    private static Logger log = Logger.getLogger(GameMap.class.getName());

    private final int MINIMUM_WIDTH  = 5;
    private final int MINIMUM_HEIGHT = 5;
    
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
        roadNetwork         = new HashMap<>();
        roadToWorkerMap     = new HashMap<>();
        availableFlagPoints = calculateAvailableFlagPoints();
    }

    public void stepTime() {
        for (Worker w : allWorkers) {
            w.stepTime();
        }

        for (Building b : buildings) {
            b.stepTime();
        }
    }

    public void placeBuilding(Building house, Point p) throws Exception {
        if (buildings.contains(house)) {
            throw new Exception("Can't place " + house + " as it is already placed.");
        }

        if (isPointReserved(p)) {
            throw new Exception("Can't place building on " + p);
        }
        
        buildings.add(house);

        Flag flag = house.getFlag();

        flag.setPosition(p);
        placeFlag(flag);
        
        reserveSpaceForBuilding(house);
    }

    public void placeRoad(Road roadToPlace) throws InvalidEndPointException {
        Flag startFlag = roadToPlace.start;
        Flag endFlag = roadToPlace.end;

        boolean validStart = false;
        boolean validEnd = false;

        Point start = startFlag.getPosition();
        Point end = endFlag.getPosition();

        for (Building b : buildings) {
            Point place = b.getFlag().getPosition();

            if (place.equals(start)) {
                validStart = true;
            } else if (place.equals(end)) {
                validEnd = true;
            }
        }

        for (Flag f : flags) {
            Point p = f.getPosition();

            if (p.x == start.x && p.y == start.y) {
                validStart = true;
            } else if (p.x == end.x && p.y == end.y) {
                validEnd = true;
            }
        }

        if (validStart && validEnd) {
            roads.add(roadToPlace);

            if (!roadNetwork.containsKey(startFlag)) {
                roadNetwork.put(startFlag, new ArrayList<Flag>());
            }

            if (!roadNetwork.containsKey(endFlag)) {
                roadNetwork.put(endFlag, new ArrayList<Flag>());
            }

            roadNetwork.get(startFlag).add(endFlag);
            roadNetwork.get(endFlag).add(startFlag);

        } else {
            throw new InvalidEndPointException();
        }
    }

    public void placeRoad(Flag startFlag, Flag endFlag) throws InvalidEndPointException {
        placeRoad(new Road(startFlag, endFlag));
    }

    public List<Road> getRoads() {
        return roads;
    }

    public List<Flag> findWay(Flag start, Flag end) throws InvalidRouteException {
        log.log(Level.INFO, "Finding way from {0} to {1}", new Object[]{start, end});

        List<Flag> result = findWayWithMemory(start, end, new ArrayList<Flag>());

        if (result == null) {
            log.log(Level.WARNING, "Failed to find a way from {0} to {1}", new Object[]{start, end});
            throw new InvalidRouteException("No route found from " + start + " to " + end + ".");
        }

        log.log(Level.FINE, "Returning found way {0}", result);
        return result;
    }

    private List<Flag> findWayWithMemory(Flag start, Flag end, List<Flag> visited) throws InvalidRouteException {
        log.log(Level.INFO, "Finding way from {0} to {1}, already visited {2}", new Object[]{start, end, visited});

        if (start.equals(end)) {
            throw new InvalidRouteException("Start and end are the same.");
        }

        if (!roadNetwork.containsKey(start)) {
            throw new InvalidRouteException(start + " has no connecting roads.");
        }

        List<Flag> connectingRoads = getDirectlyConnectedFlags(start);

        for (Flag otherEnd : connectingRoads) {
            List<Flag> result = new ArrayList<>();

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

            List<Flag> tmp = findWayWithMemory(otherEnd, end, visited);

            if (tmp != null) {
                result.add(start);

                result.addAll(tmp);

                return result;
            }
        }

        return null;
    }

    public boolean routeExist(Flag point, Flag point2) throws InvalidRouteException {
        try {
            findWay(point, point2);
        } catch (InvalidRouteException e) {
            return false;
        }

        return true;
    }

    public Road getRoad(Flag startPosition, Flag wcSpot) {
        for (Road r : roads) {
            if ((r.start.equals(startPosition) && r.end.equals(wcSpot))
                    || (r.end.equals(startPosition) && r.start.equals(wcSpot))) {
                return r;
            }
        }

        return null;
    }

    public void assignWorkerToRoad(Courier wr, Road road) throws Exception {
        Flag courierFlag = wr.getPosition();

        if (!road.getFlags()[0].equals(courierFlag) && !road.getFlags()[1].equals(courierFlag)) {
            throw new Exception("Can't assign " + wr + " to " + road);
        }

        road.setCourier(wr);
        wr.setRoad(road);
        roadToWorkerMap.put(road, wr);

        if (!allWorkers.contains(wr)) {
            throw new Exception("Can't assign " + wr + " to " + road
                    + ". Worker is not placed on the map");
        }
    }

    public Courier getWorkerForRoad(Road nextRoad) {
        log.log(Level.FINE, "Getting worker for {0}", nextRoad);

        return roadToWorkerMap.get(nextRoad);
    }

    public List<Road> findWayInRoads(Flag position, Flag flag) throws InvalidRouteException {
        log.log(Level.INFO, "Finding the way from {0} to {1}", new Object[]{position, flag});

        List<Flag> points = findWay(position, flag);
        List<Road> nextRoads = new ArrayList<>();

        if (points.size() == 2) {
            log.log(Level.FINE, "Route found has only one road segment");
            nextRoads.add(getRoad(points.get(0), points.get(1)));

            log.log(Level.FINE, "Returning route {0}", nextRoads);
            return nextRoads;
        }

        Flag next = points.get(0);

        int i;
        for (i = 1; i < points.size(); i++) {
            nextRoads.add(getRoad(next, points.get(i)));

            next = points.get(i);
        }

        log.log(Level.FINE, "Returning route {0}", nextRoads);
        return nextRoads;
    }

    public void placeFlag(Flag f) throws Exception {
        if (isPointReserved(f.getPosition())) {
            throw new Exception("Can't place " + f + " on occupied point");
        }
        
        if (flags.contains(f)) {
            throw new Exception("Flag " + f + " is already placed on the map");
        }
        
        this.flags.add(f);
        
        reserveSpaceForFlag(f);
    }

    public Storage getClosestStorage(Road r) {
        Storage stg = null;

	// TODO: Change to find the closest storage
        for (Building b : buildings) {
            if (b instanceof Storage) {
                stg = (Storage) b;
            }
        }

        return stg;
    }

    public Storage getClosestStorage(Actor a) {
        Storage stg = null;

	// TODO: Change to find the closest storage
        for (Building b : buildings) {
            if (b instanceof Storage) {
                stg = (Storage) b;
            }
        }

        return stg;
    }

    public List<Courier> getIdleWorkers() {
        List<Courier> result = new ArrayList<>();

        for (Courier w : roadToWorkerMap.values()) {
            if (w.getCargo() == null) {
                result.add(w);
            }
        }

        return result;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public List<Courier> getWorkersAtTarget() {
        List<Courier> result = new ArrayList<>();

        for (Courier w : roadToWorkerMap.values()) {
            if (w.isArrived()) {
                result.add(w);
            }
        }

        return result;
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

    public List<Road> getRoadsWithoutWorker() {
        List<Road> result = new ArrayList<>();
        Collection<Road> roadsWithWorkers = roadToWorkerMap.keySet();

        for (Road r : roads) {
            if (!roadsWithWorkers.contains(r)) {
                result.add(r);
            }
        }

        return result;
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public void placeWorker(Worker w, Flag f) {
        w.setPosition(f);
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

    public Building getBuildingByFlag(Flag targetFlag) {
        Building result = null;

        for (Building b : buildings) {
            if (b.getFlag().equals(targetFlag)) {
                result = b;

                break;
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

    public Set<Building> getBuildingsWithinReach(Flag startFlag) {
        return getBuildingsWithinReachWithMemory(startFlag, new ArrayList<Flag>());
    }
    
    private Set<Building> getBuildingsWithinReachWithMemory(Flag start, List<Flag> visited) {
        Set<Building> result = new HashSet<>();

        if (buildingExistsAtFlag(start)) {
            result.add(getBuildingByFlag(start));
        }
        
        List<Flag> connectedFlags = getDirectlyConnectedFlags(start);

        for (Flag f : connectedFlags) {
            
            if (visited.contains(f)) {
                continue;
            }
            
            List<Flag> visitedCopy = new ArrayList<>();
            visitedCopy.addAll(visited);
            visitedCopy.add(f);
            
            Set<Building> tmp = getBuildingsWithinReachWithMemory(f, visitedCopy);
            
            result.addAll(tmp);
        }
        
        return result;
    }

    private List<Flag> getDirectlyConnectedFlags(Flag start) {
        List<Flag> connectedFlags = roadNetwork.get(start);
        
        if (connectedFlags == null) {
            return new ArrayList<>();
        }

        return roadNetwork.get(start);
    }

    private boolean buildingExistsAtFlag(Flag start) {
        return getBuildingByFlag(start) != null;
    }

    public List<Point> getAvailableFlagPoints() {
        return availableFlagPoints;
    }
    
    private List<Point> calculateAvailableFlagPoints() {
        List<Point> result = new ArrayList<>();
        boolean rowFlip    = true;
        boolean columnFlip;
        
        int x, y;
        for (x = 1; x < width; x++) {
            columnFlip = rowFlip;
            
            for (y = 1; y < height; y++) {
                if (columnFlip) {
                    result.add(new Point(x, y));
                }
                
                columnFlip = !columnFlip;
            }
        
            rowFlip = !rowFlip;
        }
        
        return result;
    }

    private void reserveSpaceForBuilding(Building hq) {
        Point p = hq.getFlag().getPosition();
        
        switch(hq.getHouseSize(hq)) {
        case SMALL:
        case MEDIUM:
            reserveSpaceForSmallHouse(p);
            break;
        case LARGE:
            reserveSpaceForLargeHouse(p);
            break;
        }
    }
    
    private void reserveSpaceForFlag(Flag f) {
        Point p = f.getPosition();
        
        reservePoint(p.x - 1, p.y - 1);
        reservePoint(p.x + 1, p.y - 1);

        reservePoint(p);

        reservePoint(p.x - 1, p.y + 1);
        reservePoint(p.x + 1, p.y + 1);

    }
    
    private void reservePoint(Point p) {
        if (availableFlagPoints.contains(p)) {
            availableFlagPoints.remove(p);
        }
    }
    
    private void reservePoint(int x, int y) {
        reservePoint(new Point(x, y));
    }

    private boolean isPointReserved(Point position) {
        return !availableFlagPoints.contains(position);
    }

    private void reserveSpaceForLargeHouse(Point p) {
        reservePoint(p.x - 1, p.y - 1);
        reservePoint(p.x + 1, p.y - 1);

        reservePoint(p.x - 2, p.y);
        reservePoint(p);

        reservePoint(p.x - 3, p.y + 1);
        reservePoint(p.x - 1, p.y + 1);
        reservePoint(p.x + 1, p.y + 1);

        reservePoint(p.x - 4, p.y + 2);
        reservePoint(p.x - 2, p.y + 2);
        reservePoint(p.x,     p.y + 2);

        reservePoint(p.x - 3, p.y + 3);
        reservePoint(p.x - 1, p.y + 3);

        reservePoint(p.x - 2, p.y + 4);
    }

    private void reserveSpaceForSmallHouse(Point p) {
        reservePoint(p.x + 1, p.y - 1);

        reservePoint(p.x - 2, p.y);
        reservePoint(p);

        reservePoint(p.x - 3, p.y + 1);
        reservePoint(p.x - 1, p.y + 1);
        reservePoint(p.x + 1, p.y + 1);

        reservePoint(p.x - 2, p.y + 2);
        reservePoint(p.x,     p.y + 2);
    }
}
