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
public abstract class Worker implements Actor, Piece {
    private Player player;

    enum States {
        WALKING_AND_EXACTLY_AT_POINT, 
        WALKING_BETWEEN_POINTS, 
        IDLE_OUTSIDE, 
        IDLE_INSIDE
    }
    
    private final static Logger log = Logger.getLogger(Worker.class.getName());
    private final Countdown     walkCountdown;

    protected GameMap     map;
    
    private List<Point> path;
    private States      state;
    private Cargo       carriedCargo;
    private Building    buildingToEnter;
    private Point       position;
    private Point       target;
    private Building    home;

    
    public Worker(Player p, GameMap m) {
        player          = p;
        target          = null;
        position        = null;
        path            = null;
        buildingToEnter = null;
        home            = null;
        map             = m;

        walkCountdown  = new Countdown();

        state = IDLE_OUTSIDE;
    }

    @Override
    public void stepTime() throws Exception {
        if (state == WALKING_AND_EXACTLY_AT_POINT) {
            position = path.get(0);
            path.remove(0);

            updateCargoPosition();

            if (position.equals(target)) {
                state = IDLE_OUTSIDE;

                handleArrival();
            } else {
                walkCountdown.countFrom(getSpeed() - 3);
                
                state = WALKING_BETWEEN_POINTS;
            }
        } else if (state == WALKING_BETWEEN_POINTS) {
            if (walkCountdown.reachedZero()) {
                
                state = WALKING_AND_EXACTLY_AT_POINT;
            } else {
                walkCountdown.step();
            }
        } else if (state == IDLE_OUTSIDE) {            
            onIdle();
        } else if (state == IDLE_INSIDE) {
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
                
            if (buildingToEnter != null) {
                str += " for " + buildingToEnter;
            }

            if (carriedCargo != null) {
                str += " carrying " + carriedCargo;
            }
            
            return str;
        }
        
        return "Idle courier at " + getPosition();
    }

    protected void onArrival() throws Exception {
        log.log(Level.FINE, "On handle hook arrival with nothing to do");
    }
    
    protected void onIdle() throws Exception {
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
                building.deployMilitary((Military) this);
                enterBuilding(building);
            } else {
                building.assignWorker(this);
                enterBuilding(building);
            }

            buildingToEnter = null;
        }

        /* This lets subclasses add their own logic */
        onArrival();
    }

    public void setPosition(Point p) {
        position = p;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    public boolean isArrived() {
        log.log(Level.FINE, "Checking if worker has arrived");
        log.log(Level.FINER, "Worker is at {0} and target is {1}", new Object[]{position, target});

        if (state == IDLE_INSIDE || state == IDLE_OUTSIDE) {
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
        return state == WALKING_AND_EXACTLY_AT_POINT || state == WALKING_BETWEEN_POINTS;
    }

    private int getSpeed() {
        Walker w = this.getClass().getAnnotation(Walker.class);

        return w.speed();
    }

    public void setTargetBuilding(Building b) throws InvalidRouteException {
        buildingToEnter = b;
        setTarget(b.getPosition());
    }

    public Building getTargetBuilding() {
        return buildingToEnter;
    }

    public boolean isExactlyAtPoint() {
        return state != WALKING_BETWEEN_POINTS;
    }

    public Point getLastPoint() {
        return position;
    }

    public Point getNextPoint() throws Exception {
        if (path == null || path.isEmpty()) {
            throw new Exception("No next point set. Target is " + getTarget());
        }

        return path.get(0);
    }

    public int getPercentageOfDistanceTraveled() {
        if (state != WALKING_BETWEEN_POINTS) {
            return 100;
        }
    
        return (int)(((double)(getSpeed() - walkCountdown.getCount() - 2) / (double)getSpeed()) * 100);
    }

    public void enterBuilding(Building b) throws Exception {
        if (!getPosition().equals(b.getPosition())) {
            throw new Exception("Can't enter " + b + " when worker is at " + getPosition());
        }
        
        state = IDLE_INSIDE;
        
        home = b;
        
        /* Allow subclasses to add logic */
        onEnterBuilding(b);
    }
    
    public boolean isInsideBuilding() {
        return state == IDLE_INSIDE;
    }

    public boolean isAt(Point p2) {
        return isExactlyAtPoint() && position.equals(p2);
    }

    public Cargo getCargo() {
        return carriedCargo;
    }

    protected void setOffroadTarget(Point p) {
        setOffroadTarget(p, null);
    }
    
    protected void setOffroadTarget(Point p, Point via) {
        boolean wasInside = false;
        
        log.log(Level.FINE, "Setting {0} as offroad target, via {1}", new Object[] {p, via});
        
        target = p;
        
        if (state == IDLE_INSIDE) {
            wasInside = true;
        }

        if (position.equals(p)) {
            try {
                state = IDLE_OUTSIDE;
                
                handleArrival();
            } catch (Exception ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            if (wasInside && !target.equals(home.getFlag().getPosition())) {                
                if (via != null) {
                    path = map.findWayOffroad(home.getFlag().getPosition(), p, via, null);
                } else {
                    path = map.findWayOffroad(home.getFlag().getPosition(), p, null);
                }

                path.add(0, home.getPosition());
            } else {
                if (via != null) {
                    path = map.findWayOffroad(getPosition(), p, via, null);
                } else {
                    path = map.findWayOffroad(getPosition(), p, null);
                }
            }
                
            log.log(Level.FINER, "Way to target is {0}", path);
            state = WALKING_AND_EXACTLY_AT_POINT;
        }
    }
    
    protected void setTarget(Point p) throws InvalidRouteException {
        if (state == IDLE_INSIDE) {
            if (!p.equals(home.getFlag().getPosition())) {        
                setTarget(p, home.getFlag().getPosition());
            } else {
                setTarget(p, null);
            }
        } else {
            setTarget(p, null);
        }
    }
    
    protected void setTarget(Point p, Point via) throws InvalidRouteException {
        log.log(Level.FINE, "Setting {0} as target, via {1}", new Object[] {p, via});
        
        target = p;

        if (position.equals(p)) {
            try {
                state = IDLE_OUTSIDE;
                
                handleArrival();
            } catch (Exception ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Point start = getPosition();
            
            if (via != null) {
                path = map.findWayWithExistingRoads(start, target, via);
            } else  {
                path = map.findWayWithExistingRoads(start, target);
            }
            
            if (path == null) {
                throw new InvalidRouteException("No way on existing roads from " + start + " to " + target);
            }
                
            log.log(Level.FINE, "Way to target is {0}", path);
            state = WALKING_AND_EXACTLY_AT_POINT;
        }
    }
    
    public Point getTarget() {
        return target;
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

    public List<Point> getPlannedPath() {
        return path;
    }

    protected void returnHomeOffroad() {
        setOffroadTarget(home.getPosition(), home.getFlag().getPosition());
    }

    protected void returnHome() throws InvalidRouteException {
        if (getPosition().equals(home.getFlag().getPosition())) {
            setTarget(home.getPosition());
        } else {
            setTarget(home.getPosition(), home.getFlag().getPosition());
        }
    }

    protected void setHome(Building h) {
        home = h;
    }

    void returnToStorage() throws Exception {
        onReturnToStorage();
    }

    protected void onReturnToStorage() throws Exception {
        
    }

    public Player getPlayer() {
        return player;
    }
}
