package org.appland.settlers.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameMap {

	private Map<Building, Point> buildings;
	private List<Road> roads;
	private List<Flag> flags;
	private Map<Point, List<Point>> roadNetwork;
	private Map<Road, Worker> roadToWorkerMap;

	private static Logger log = Logger.getLogger(GameMap.class.getName());
	
	private GameMap() {
		buildings = new HashMap<Building, Point>();
		roads = new ArrayList<Road>();
		flags = new ArrayList<Flag>();
		roadNetwork = new HashMap<Point, List<Point>>();
		roadToWorkerMap = new HashMap<Road, Worker>();
		
		/* Increase the log level */
		log.setLevel(Level.FINEST);
		
		Handler[] handlers = log.getHandlers();
		for(Handler h: handlers) {
		    h.setLevel(Level.FINEST);
		}
	}
	
	public static GameMap createGameMap() {
		return new GameMap();
	}

	public void placeBuilding(Building hq, Point p) {
		buildings.put(hq, p);
		
		Flag flag = hq.getFlag();
		
		flag.setPosition(p);
		
		placeFlag(flag);
	}
	
	public void placeRoad(Point start, Point end) throws InvalidEndPointException {
	
		boolean validStart = false;
		boolean validEnd = false;
		
		for (Building b : buildings.keySet()) {
			Point place = buildings.get(b);
			
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
			Road r = Road.createRoad(start, end);
			roads.add(r);
			
			if (!roadNetwork.containsKey(start)) {
				roadNetwork.put(start, new ArrayList<Point>());
			}
			
			if (!roadNetwork.containsKey(end)) {
				roadNetwork.put(end, new ArrayList<Point>());
			}
			
			roadNetwork.get(start).add(end);
			roadNetwork.get(end).add(start);
			
		} else {
			throw new InvalidEndPointException();
		}
		
	}

	public List<Road> getRoads() {
		return roads;
	}

	public void placeFlag(Point p) {
		flags.add(Flag.createFlag(p));
	}

	public List<Point> findWay(Point start, Point end) throws InvalidRouteException {
		log.log(Level.INFO, "Finding way from {0} to {1}", new Object[] {start, end});
		
		List<Point> result = findWayWithMemory(start, end, new ArrayList<Point>());
		
		if (result == null) {
			log.log(Level.WARNING, "Failed to find a way from {0} to {1}");
			throw new InvalidRouteException("No route found from " + start + " to " + end + ".");
		}
		
		log.log(Level.FINE, "Returning found way {0}", result);
		return result;
	}
	
	private List<Point> findWayWithMemory(Point start, Point end, List<Point> visited) throws InvalidRouteException {
		log.log(Level.INFO, "Finding way from {0} to {1}, already visited {2}", new Object[] {start, end, visited});
		
		if (start.equals(end)) {
			throw new InvalidRouteException("Start and end are the same.");
		}
	
		if (!roadNetwork.containsKey(start)) {
			throw new InvalidRouteException(start + " has no connecting roads.");
		}
		
		List<Point> connectingRoads = roadNetwork.get(start);
		
		if (connectingRoads == null || connectingRoads.isEmpty()) {
			throw new InvalidRouteException(start + " has no connecting roads.");
		}
		
		for (Point otherEnd : connectingRoads) {
			List<Point> result = new ArrayList<Point>();
			
			if (visited.contains(otherEnd)) {
				continue;
			}
			
			if (otherEnd.equals(end)) {
				result.add(start);
				result.add(end);
				return result;
			}
			
			visited.add(start);
			
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
			findWay(point, point2);
		} catch (InvalidRouteException e) {
			return false;
		}

		return true;
	}

	public Road getRoad(Point startPosition, Point wcSpot) {
		for (Road r : roads) {
			if ((r.start.equals(startPosition) && r.end.equals(wcSpot)) ||
				(r.end.equals(startPosition) && r.start.equals(wcSpot))) {
				return r;
			}
		}

		return null;
	}

	public void assignWorkerToRoad(Worker wr, Road road) {
		roadToWorkerMap.put(road, wr);
		wr.setRoad(road);
	}

	public Worker getNextWorkerForCargo(Cargo c) {
		System.out.println("Next worker for " + c + " of type " + c.getMaterial());
		
		List<Road> roads = c.getPlannedRoads();
		
		Road nextRoad = roads.get(0);
		
		System.out.println("In getNextWorkerForCargo, with road " + nextRoad);
		
		Worker w = getWorkerForRoad(nextRoad);
		
		if (w == null) {
			nextRoad = reverseRoad(nextRoad);
			System.out.println("Reversed road: "  + nextRoad);
			
			w = getWorkerForRoad(nextRoad);
		}
		
		System.out.println("Got worker " + w);
		
		System.out.println(roadToWorkerMap.toString());
		
		return w;
	}

	private Road reverseRoad(Road nextRoad) {
		Point start, end;
		
		start = nextRoad.start;
		end = nextRoad.end;
		
		return Road.createRoad(end, start);
	}

	private Worker getWorkerForRoad(Road nextRoad) {
		System.out.println("In getWorkerForRoad");
		System.out.println(roadToWorkerMap.keySet());
		
		
		for (Road r : roadToWorkerMap.keySet()) {
			if (!nextRoad.equals(r)) {
				System.out.println(" Not equals: " + nextRoad + " and " + r);
			} else {
				System.out.println("Equal! " + nextRoad + " equals " + r);
			}
		}
		
		return roadToWorkerMap.get(nextRoad);
	}

	public List<Road> findWayInRoads(Point position, Flag flag) throws InvalidRouteException {
		log.log(Level.INFO, "Finding the way from {0} to {1}", new Object[] {position, flag});
		
		List<Point> points = findWay(position, flag.getPosition());
		List<Road> roads = new ArrayList<Road>();
		
		if (points.size() == 2) {
			log.log(Level.FINE, "Route found has only one road segment");
			roads.add(Road.createRoad(points.get(0), points.get(1)));
			
			log.log(Level.FINE, "Returning route {0}", roads);
			return roads;
		}
		
		Point start = points.get(0);
		
		int i;
		for (i = 1; i < points.size() - 1; i++) {
			roads.add(Road.createRoad(start, points.get(i)));
			System.out.println(" -- " + i + ", " + points.size());
		}
		
		roads.add(Road.createRoad(points.get(i), points.get(i + 1)));
		
		log.log(Level.FINE, "Returning route {0}", roads);
		return roads;
	}

	public Flag getFlagForPosition(Point position) {
		log.log(Level.INFO, "Getting flag for position {0}", position);
		
		for (Flag f : flags) {
			log.log(Level.FINEST, "Matching against {0}", f);
			
			if (f.getPosition().equals(position)) {
				log.log(Level.FINE, "Found match {0} for position {1}", new Object[] {f, position});
				return f;
			}
		}

		return null;
	}
	
	public void placeFlag(Flag f) {
		this.flags.add(f);
	}

    public Storage getClosestStorage() {
	Storage stg = null;

	// TODO: Change to find the closest storage
	
	for (Building b : buildings.keySet()) {
	    if (b instanceof Storage || b instanceof Headquarter) {
		stg = (Storage) b;
	    }
	}

	return stg;
    }
}
