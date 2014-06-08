/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author johan
 */
public class Worker implements Actor {
    Road targetRoad;
    protected GameMap    map;
    protected List<Flag> path;

    private static Logger log = Logger.getLogger(Worker.class.getName());
    protected Flag  position;
    protected Flag  target;
    private boolean traveling;
    private int     walkCountdown;
    private Building targetBuilding;
    
    public Worker() {
        traveling      = false;
        target         = null;
        position       = null;
        path           = null;
        map            = null;
        targetRoad     = null;
        targetBuilding = null;
        
        walkCountdown = -1;
    }
    
    @Override
    public void stepTime() {
        log.log(Level.INFO, "Stepping time");
        
        if (path != null) {
            log.log(Level.FINE, "There is a path set: {0}", path);

            if (walkCountdown == 0) {
                log.log(Level.FINE, "Reached next step: {0}", position);
                reachedNextStep();
            } else if (walkCountdown == -1) {
                log.log(Level.FINE, "Starting to walk, currently at {0}", position);
                walkCountdown = getSpeed() - 2;
            } else {
                log.log(Level.FINE, "Continuing to walk, currently at {0}", position);
                walkCountdown--;
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
    
    private void reachedNextStep() {
        log.log(Level.INFO, "Worker {0} has reached {1}", new Object[] {this, path.get(0)});
        
        position = path.get(0);
        
        path.remove(0);
        walkCountdown = getSpeed() - 2;
        
        checkForArrival();
    }

    private void checkForArrival() {
        if (position == target) {
            log.log(Level.FINE, "Arrived at target: {0}", target);
            path = null;
        }
    }

    public void setTargetRoad(Road r) throws Exception {
        if (target != null) {
            throw new Exception("Can't have both flag and road as target");
        }
        
        targetRoad = r;
        traveling = true;
        
        if (r.start.equals(position) || r.end.equals(position)) {
            path = null;
        } else {
            List<Flag> path1 = map.findWay(position, r.start);
            List<Flag> path2 = map.findWay(position, r.end);

            if (path1.size() < path2.size()) {
                path = path1;
                target = r.start;
            } else {
                path = path2;
                target = r.end;
            }
        }
    }

    public void setPosition(Flag flag) {
        position = flag;
    }

    public Flag getPosition() {
        return position;
    }

    public Road getTargetRoad() {
        return targetRoad;
    }

    public Flag getTarget() {
        return target;
    }

    public boolean isArrived() {
        log.log(Level.INFO, "Checking if worker has arrived");
        log.log(Level.FINE, "Worker is at {0} and target is {1}", new Object[]{position, target});

        /* A traveling worker can target a road */
        if (traveling && targetRoad != null && 
            (targetRoad.start.equals(position) || targetRoad.end.equals(position))) {
            return true;
        }

        /* A worker can be idle and not at either of the road's flags */
        if (target == null || position == null) {
            return false;
        }

        if (target.equals(position)) {
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
        log.log(Level.INFO, "Setting target to {0}, previous target was {1}", new Object[]{t, target});

        target = t;
        path = map.findWay(position, target);
        path.remove(0);
        log.log(Level.FINE, "Way to target is {0}", path);

        traveling = true;
    }

    public void assignToBuilding(Building b) {
        
    }
    
    public boolean isTraveling() {
        return traveling;
    }
    
    public void stopTraveling() {
        traveling  = false;
        target     = null;
        targetRoad = null;
    }

    private int getSpeed() {
        Walker w = this.getClass().getAnnotation(Walker.class);

        return w.speed();
    }

    public void setTargetBuilding(Building b) throws InvalidRouteException {
        targetBuilding = b;
        setTargetFlag(b.getFlag());
    }
}
