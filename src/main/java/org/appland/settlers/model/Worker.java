/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author johan
 */
public abstract class Worker implements Actor {

    protected Cargo       carriedCargo;
    protected Building    targetBuilding;
    protected Road        assignedRoad;
    protected Road        targetRoad;
    private Flag          targetFlag;
    protected GameMap     map;
    protected List<Point> path;

    private static Logger log = Logger.getLogger(Worker.class.getName());
    protected Point       position;
    protected Point       target;
    private boolean       traveling;
    private Countdown     walkCountdown;
    private Building      home;
    private boolean       exactlyAtPoint;

    public Worker() {
        this(null);
    }
    
    public Worker(GameMap m) {
        traveling = false;
        target = null;
        position = null;
        path = null;
        targetRoad = null;
        targetBuilding = null;
        targetFlag = null;
        home = null;
        map = m;
        
        walkCountdown = new Countdown();
        exactlyAtPoint = true;
    }

    @Override
    public void stepTime() {
        log.log(Level.INFO, "Stepping time");
        
        if (traveling && path != null) {
            log.log(Level.FINE, "There is a path set: {0}", path);

            if (walkCountdown.reachedZero()) {
                reachedNextStep();
            } else if (walkCountdown.isInactive()) {
                if (isArrived()) {
                    try {
                        handleArrival();
                    } catch (Exception ex) {
                        Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    startWalking();
                }
            } else {
                log.log(Level.FINE, "Continuing to walk, currently at {0}", position);
                
                exactlyAtPoint = false;
                walkCountdown.step();
            }
        } else {
            onIdle();
        }
    }

    @Override
    public String toString() {
        if (isTraveling()) {
            String str = "";
            if (isExactlyAtPoint()) {            
                str = "Courier at " + getPosition() + " traveling to " + target;
            } else {
                str = "Courier latest at " + getLastPoint() + " traveling to " + target;
            }
                
            if (targetRoad != null) {
                str += " for " + targetRoad;
            } else if (targetFlag != null) {
                str += " for " + targetFlag;
            } else if (targetBuilding != null) {
                str += " for " + targetBuilding;
            }

            if (carriedCargo != null) {
                str += " carrying " + carriedCargo;
            }
            
            return str;
        }
        
        return "Idle courier at " + getPosition() + "(road: " + getAssignedRoad() + ")";
    }

    protected void onArrival() {
        log.log(Level.FINE, "On handle hook arrival with nothing to do");
    }
    
    protected void onIdle() {
        log.log(Level.FINE, "On idle hook with nothing to do");
    }
    
    protected void onEnterBuilding(Building b) {
        log.log(Level.FINE, "On enter building hook with nothing to do");
    }
    
    private void reachedNextStep() {
        log.log(Level.INFO, "Worker {0} has reached {1}", new Object[]{this, path.get(0)});

        try {
            position = path.get(0);
            path.remove(0);

            exactlyAtPoint = true;

            if (carriedCargo != null) {
                carriedCargo.setPosition(getPosition());
            }

            if (isArrived()) {
                handleArrival();
            }

            walkCountdown.countFrom(getSpeed() - 2);
        } catch (Exception ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
            
    private void handleArrival() throws Exception {
        log.log(Level.FINE, "Arrived at target: {0}", target);

        path      = null;
        traveling = false;

        /* Handle couriers separately */
        if (getTargetBuilding() != null) {
            Building building = getTargetBuilding();

            stopTraveling();
            
            if (this instanceof Military) {
                building.hostMilitary((Military) this);
            } else {
                building.assignWorker(this);
                enterBuilding(building);
            }
        }

        /* This lets subclasses add their own logic */
        onArrival();
    }

    public void setPosition(Point p) {
        position = p;
    }

    public Point getPosition() {
        return position;
    }

    public Flag getTargetFlag() {
        return targetFlag;
    }

    public boolean isArrived() {
        log.log(Level.FINE, "Checking if worker has arrived");
        log.log(Level.FINER, "Worker is at {0} and target is {1}", new Object[]{position, target});

        if (!traveling || target == null) {
            return true;
        }
        
        if (!isExactlyAtPoint()) {
            return false;
        }
        
        if (target != null && target.equals(position)) {
            log.log(Level.INFO, "Worker has arrived at target {0}", target);
            return true;
        }

        log.log(Level.INFO, "Worker has not arrived at target");
        return false;
    }

    public void setMap(GameMap map) {
        log.log(Level.FINE, "Setting map to {0}", map);
        this.map = map;
    }

    public void setTargetFlag(Flag t) throws InvalidRouteException {
        log.log(Level.INFO, "Setting target flag to {0}, previous target was {1}", new Object[]{t, target});
        
        targetFlag = t;
        
        setTarget(t.getPosition());        
    }

    public boolean isTraveling() {
        return traveling;
    }

    public void stopTraveling() {
        traveling = false;
        target = null;
        targetRoad = null;
        targetBuilding = null;
        targetFlag = null;
    }

    private int getSpeed() {
        Walker w = this.getClass().getAnnotation(Walker.class);

        return w.speed();
    }

    public void setTargetBuilding(Building b) throws InvalidRouteException {
        targetBuilding = b;
        setTargetFlag(b.getFlag());
    }

    public Building getTargetBuilding() {
        return targetBuilding;
    }

    public boolean isExactlyAtPoint() {
        return !traveling || exactlyAtPoint;
    }

    public Point getLastPoint() {
        return position;
    }

    public Point getNextPoint() throws Exception {
        if (path == null || path.isEmpty()) {
            throw new Exception("No next point set. Target is " + getTargetFlag());
        }

        return path.get(0);
    }

    public int getPercentageOfDistanceTraveled() {
        if (!traveling) {
            return 100;
        }
    
        return (int)(((double)(getSpeed() - walkCountdown.getCount() - 2) / (double)getSpeed()) * 100);
    }

    public void enterBuilding(Building b) {
        home = b;
        
        /* Allow subclasses to add logic */
        onEnterBuilding(b);
    }
    
    public boolean isInsideBuilding() {
        return home != null;
    }

    public boolean isAt(Point p2) {
        return isExactlyAtPoint() && position.equals(p2);
    }

    protected void setAssignedRoad(Road road) {
        assignedRoad = road;
    }

    protected Road getAssignedRoad() {
        return assignedRoad;
    }

    public void putDownCargo() {
        targetFlag.putCargo(carriedCargo);
        carriedCargo.setPosition(position);
        
        List<Road> plannedRoadForCargo = carriedCargo.getPlannedRoads();
        plannedRoadForCargo.remove(0);
        carriedCargo.setPlannedRoads(plannedRoadForCargo);

        carriedCargo = null;
    }

    public Cargo getCargo() {
        return carriedCargo;
    }

    protected void pickUpCargoForRoad(Flag flag, Road r) throws Exception {
        Cargo cargoToPickUp = null;
        
        if (!position.equals(flag.getPosition())) {
            throw new Exception("Not at " + flag);
        }
        
        for (Cargo c : flag.getStackedCargo()) {
            if (c.getPlannedRoads().get(0).equals(r)) {
                cargoToPickUp = c;
            }
        }

        pickUpCargoFromFlag(cargoToPickUp, flag);
    }

    protected void pickUpCargoFromFlag(Cargo c, Flag flag) throws Exception {
        carriedCargo = flag.retrieveCargo(c);

        Flag otherEnd = getAssignedRoad().getOtherFlag(flag);
        setTargetFlag(otherEnd);
    }

    private void startWalking() {
        log.log(Level.FINE, "Starting to walk, currently at {0}", position);

        walkCountdown.countFrom(getSpeed() - 2);
        
        exactlyAtPoint = false;
    }

    protected void setOffroadTarget(Point p) {
        log.log(Level.FINE, "Setting {0} as offroad target", p);
        
        target = p;
        
        path = map.findWayOffroad(position, target, null);
        
        path.remove(0);
        log.log(Level.FINER, "Way to target is {0}", path);
        
        traveling = true;
        
        if (isInsideBuilding()) {
            leaveBuilding();
        }
    }
    
    protected void setTarget(Point p) throws InvalidRouteException {
        target = p;

        if (target.equals(getPosition())) {
            try {
                handleArrival();
                return;
            } catch (Exception ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        path = map.findWayWithExistingRoads(position, target);
        path.remove(0);
        log.log(Level.FINE, "Way to target is {0}", path);
        
        traveling = true;    
        
        if (isInsideBuilding()) {
            leaveBuilding();
        }
    }
    
    public Point getTarget() {
        return target;
    }

    private void leaveBuilding() {
        home = null;
    }

    public Building getHome() {
        return home;
    }
}
