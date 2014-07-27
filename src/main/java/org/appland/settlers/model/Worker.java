/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.Worker.States.IDLE_INSIDE;
import static org.appland.settlers.model.Worker.States.IDLE_OUTSIDE;
import static org.appland.settlers.model.Worker.States.WALKING_AND_EXACTLY_AT_POINT;
import static org.appland.settlers.model.Worker.States.WALKING_BETWEEN_POINTS;

/**
 *
 * @author johan
 */
public abstract class Worker implements Actor {
    enum States {
        WALKING_AND_EXACTLY_AT_POINT, WALKING_BETWEEN_POINTS, IDLE_OUTSIDE, IDLE_INSIDE
    }
    
    private States        workerState;
    private Cargo         carriedCargo;
    private Building      targetBuilding;
    protected GameMap     map;
    protected List<Point> path;

    private final static Logger log = Logger.getLogger(Worker.class.getName());

    private Point         workerPosition;
    private Point         target;
    private Countdown     walkCountdown;
    private Building      home;

    public Worker() {
        this(null);
    }
    
    public Worker(GameMap m) {
        target         = null;
        workerPosition = null;
        path           = null;
        targetBuilding = null;
        home           = null;
        map            = m;
        
        walkCountdown  = new Countdown();
        
        workerState = IDLE_OUTSIDE;
    }

    @Override
    public void stepTime() {
        log.log(Level.FINE, "Stepping time");

        if (workerState == WALKING_AND_EXACTLY_AT_POINT) {
            workerPosition = path.get(0);
            path.remove(0);

            updateCargoPosition();

            if (workerPosition.equals(target)) {
                try {
                    workerState = IDLE_OUTSIDE;

                    handleArrival();
                } catch (Exception ex) {
                    Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                walkCountdown.countFrom(getSpeed() - 3);
                
                workerState = WALKING_BETWEEN_POINTS;
            }
        } else if (workerState == WALKING_BETWEEN_POINTS) {
            if (walkCountdown.reachedZero()) {
                
                workerState = WALKING_AND_EXACTLY_AT_POINT;
            } else {
                walkCountdown.step();
            }
        } else if (workerState == IDLE_OUTSIDE) {            
            onIdle();
        } else if (workerState == IDLE_INSIDE) {
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
                
            if (targetBuilding != null) {
                str += " for " + targetBuilding;
            }

            if (carriedCargo != null) {
                str += " carrying " + carriedCargo;
            }
            
            return str;
        }
        
        return "Idle courier at " + getPosition();
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
            
    private void handleArrival() throws Exception {
        log.log(Level.FINE, "Arrived at target: {0}", target);

        /* If there is a building set as target, enter it */
        if (getTargetBuilding() != null) {
            Building building = getTargetBuilding();

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
        workerPosition = p;
    }

    public Point getPosition() {
        return workerPosition;
    }

    public boolean isArrived() {
        log.log(Level.FINE, "Checking if worker has arrived");
        log.log(Level.FINER, "Worker is at {0} and target is {1}", new Object[]{workerPosition, target});

        if (workerState == IDLE_INSIDE || workerState == IDLE_OUTSIDE) {
            return true;
        }
        
        log.log(Level.FINER, "Worker has not arrived at target");
        return false;
    }

    public void setMap(GameMap map) {
        log.log(Level.FINE, "Setting map to {0}", map);
        this.map = map;
    }

    public boolean isTraveling() {
        return workerState == WALKING_AND_EXACTLY_AT_POINT || workerState == WALKING_BETWEEN_POINTS;
    }

    private int getSpeed() {
        Walker w = this.getClass().getAnnotation(Walker.class);

        return w.speed();
    }

    public void setTargetBuilding(Building b) throws InvalidRouteException {
        targetBuilding = b;
        setTarget(b.getFlag().getPosition());
    }

    public Building getTargetBuilding() {
        return targetBuilding;
    }

    public boolean isExactlyAtPoint() {
        return workerState != WALKING_BETWEEN_POINTS;
    }

    public Point getLastPoint() {
        return workerPosition;
    }

    public Point getNextPoint() throws Exception {
        if (path == null || path.isEmpty()) {
            throw new Exception("No next point set. Target is " + getTarget());
        }

        return path.get(0);
    }

    public int getPercentageOfDistanceTraveled() {
        if (workerState != WALKING_BETWEEN_POINTS) {
            return 100;
        }
    
        return (int)(((double)(getSpeed() - walkCountdown.getCount() - 2) / (double)getSpeed()) * 100);
    }

    public void enterBuilding(Building b) {
        workerState = IDLE_INSIDE;
        
        home = b;
        
        /* Allow subclasses to add logic */
        onEnterBuilding(b);
    }
    
    public boolean isInsideBuilding() {
        return workerState == IDLE_INSIDE;
    }

    public boolean isAt(Point p2) {
        return isExactlyAtPoint() && workerPosition.equals(p2);
    }

    public Cargo getCargo() {
        return carriedCargo;
    }

    protected void setOffroadTarget(Point p) {
        log.log(Level.FINE, "Setting {0} as offroad target", p);
        
        if (workerState == IDLE_INSIDE) {
            leaveBuilding();
        }

        workerState = WALKING_AND_EXACTLY_AT_POINT;
        target = p;
        
        if (workerPosition.equals(p)) {            
            try {
                handleArrival();
            } catch (Exception ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            path = map.findWayOffroad(workerPosition, p, null);

            log.log(Level.FINER, "Way to target is {0}", path);
        }
    }
    
    protected void setTarget(Point p) throws InvalidRouteException {
        target = p;

        if (workerState == IDLE_INSIDE) {
            leaveBuilding();
        }

        workerState = WALKING_AND_EXACTLY_AT_POINT;
        target = p;

        if (workerPosition.equals(p)) {
            try {
                handleArrival();
            } catch (Exception ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {        
            path = map.findWayWithExistingRoads(workerPosition, target);

            log.log(Level.FINE, "Way to target is {0}", path);
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

    protected void setCargo(Cargo cargo) {
        carriedCargo = cargo;
    }

    private void updateCargoPosition() {
        if (carriedCargo != null) {
            carriedCargo.setPosition(getPosition());
        }
    }
}
