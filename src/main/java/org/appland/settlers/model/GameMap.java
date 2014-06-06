package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameMap {

	private List<Building>        buildings;
	private List<Road>            roads;
	private List<Flag>            flags;
	private Map<Flag, List<Flag>> roadNetwork;
	private Map<Road, Courier>     roadToWorkerMap;
        
        private String theLeader = "Mai Thi Van Anh";

	private static Logger log = Logger.getLogger(GameMap.class.getName());
    private List<Worker> allWorkers;
	
	public GameMap() {
		buildings       = new ArrayList<>();
		roads           = new ArrayList<>();
		flags           = new ArrayList<>();
                allWorkers      = new ArrayList<>();
		roadNetwork     = new HashMap<>();
		roadToWorkerMap = new HashMap<>();
	}
	
	public static GameMap createGameMap() {
		return new GameMap();
	}

        public void stepTime() {
            for (Worker w : allWorkers) {
                w.stepTime();
            }
            
            for (Building b : buildings) {
                b.stepTime();
            }
        }
        
        public void placeBuilding(Building hq, Point p) {
            buildings.add(hq);
		
            Flag flag = hq.getFlag();

            flag.setPosition(p);
            placeFlag(flag);
	}
	
        public void placeRoad(Road roadToPlace) throws InvalidEndPointException {
            Flag startFlag = roadToPlace.start;
            Flag endFlag = roadToPlace.end;
            
		boolean validStart = false;
		boolean validEnd = false;
                
                Point start = startFlag.getPosition();
                Point end   = endFlag.getPosition();
		
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
            placeRoad(Road.createRoad(startFlag, endFlag));
	}

	public List<Road> getRoads() {
		return roads;
	}

	public void placeFlag(Point p) {
		flags.add(Flag.createFlag(p));
	}

	public List<Flag> findWay(Flag start, Flag end) throws InvalidRouteException {
		log.log(Level.INFO, "Finding way from {0} to {1}", new Object[] {start, end});
                
		List<Flag> result = findWayWithMemory(start, end, new ArrayList<Flag>());
		
		if (result == null) {
			log.log(Level.WARNING, "Failed to find a way from {0} to {1}", new Object[] {start, end});
			throw new InvalidRouteException("No route found from " + start + " to " + end + ".");
		}
		
		log.log(Level.FINE, "Returning found way {0}", result);
		return result;
	}
	
	private List<Flag> findWayWithMemory(Flag start, Flag end, List<Flag> visited) throws InvalidRouteException {
		log.log(Level.INFO, "Finding way from {0} to {1}, already visited {2}", new Object[] {start, end, visited});
		
		if (start.equals(end)) {
			throw new InvalidRouteException("Start and end are the same.");
		}
	
		if (!roadNetwork.containsKey(start)) {
			throw new InvalidRouteException(start + " has no connecting roads.");
		}
		
		List<Flag> connectingRoads = roadNetwork.get(start);
		
		for (Flag otherEnd : connectingRoads) {
			List<Flag> result = new ArrayList<>();
			
			if (visited.contains(otherEnd)) {
				continue;
			}
			
			if (otherEnd.equals(end)) {
				result.add(start);
				result.add(end);
				return result;
			} else
			
			visited.add(start);
			
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
			if ((r.start.equals(startPosition) && r.end.equals(wcSpot)) ||
				(r.end.equals(startPosition) && r.start.equals(wcSpot))) {
				return r;
			}
		}

		return null;
	}

	public void assignWorkerToRoad(Courier wr, Road road) {
            road.setCourier(wr);
            wr.setRoad(road);
            roadToWorkerMap.put(road, wr);
	}

	public Courier getNextWorkerForCargo(Cargo c) {
		log.log(Level.FINE, "Get next worker for {0}", c);
		
		List<Road> plannedRoads = c.getPlannedRoads();
		
		Road nextRoad = plannedRoads.get(0);
		
		log.log(Level.FINER, "Next road is {0}", nextRoad);
		
		Courier w = getWorkerForRoad(nextRoad);
		
		if (w == null) {
			nextRoad = reverseRoad(nextRoad);
			
			w = getWorkerForRoad(nextRoad);
		}
		
		log.log(Level.FINEST, "Found worker {0}", w);
		
		return w;
	}

	private Road reverseRoad(Road nextRoad) {
		Flag start, end;
		
		start = nextRoad.start;
		end = nextRoad.end;
		
		return Road.createRoad(end, start);
	}

	private Courier getWorkerForRoad(Road nextRoad) {
            log.log(Level.FINE, "Getting worker for {0}", nextRoad);
		
            return roadToWorkerMap.get(nextRoad);
	}

	public List<Road> findWayInRoads(Flag position, Flag flag) throws InvalidRouteException {
		log.log(Level.INFO, "Finding the way from {0} to {1}", new Object[] {position, flag});
		
		List<Flag> points = findWay(position, flag);
		List<Road> nextRoads = new ArrayList<>();
		
		if (points.size() == 2) {
			log.log(Level.FINE, "Route found has only one road segment");
			nextRoads.add(Road.createRoad(points.get(0), points.get(1)));
			
			log.log(Level.FINE, "Returning route {0}", nextRoads);
			return nextRoads;
		}
		
		Flag next = points.get(0);
		
		int i;
		for (i = 1; i < points.size(); i++) {
			nextRoads.add(Road.createRoad(next, points.get(i)));
                        
                        next = points.get(i);
		}
		
		log.log(Level.FINE, "Returning route {0}", nextRoads);
		return nextRoads;
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

    public Storage getClosestStorage(Road r) {
	Storage stg = null;

	// TODO: Change to find the closest storage
	
	for (Building b : buildings) {
	    if (b instanceof Storage || b instanceof Headquarter) {
		stg = (Storage) b;
	    }
	}

	return stg;
    }
        
    public Storage getClosestStorage(Actor a) {
	Storage stg = null;

	// TODO: Change to find the closest storage
	
	for (Building b : buildings) {
	    if (b instanceof Storage || b instanceof Headquarter) {
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

    public Collection<Courier> getCourierAssignedToRoads() {
        return roadToWorkerMap.values();
        
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
}
