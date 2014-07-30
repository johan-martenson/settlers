package org.appland.settlers.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cargo {

    private Material    material;
    private Building    target;
    private Point       position;
    private List<Point> path;
    
    private static final Logger log = Logger.getLogger(GameMap.class.getName());
    private boolean deliveryPromised;

    public Cargo(Material m) {
        log.log(Level.INFO, "Creating cargo of {0}", m);

        material = m;
        deliveryPromised = false;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setTarget(Building target, GameMap map) throws InvalidRouteException, Exception {
        log.log(Level.INFO, "Setting target to {0}", target);
        this.target = target;
        
        path = map.findWayWithExistingRoads(position, target.getPosition());
    }

    public boolean isAtTarget() {
        log.log(Level.INFO, "Checking if target ({0}) equals position ({1})", new Object[]{target.getFlag(), position});

        if (position.equals(target.getFlag().getPosition())) {
            return true;
        } else {
            return false;
        }
    }

    public Building getTarget() {
        return target;
    }

    public void setPlannedSteps(List<Point> steps) {
        log.log(Level.INFO, "Setting planned route to {0}", steps);
        path = steps;
    }

    public List<Point> getPlannedSteps() {
        return path;
    }

    public Point getNextStep() {
        if (path == null || path.isEmpty()) {
            return null;
        }
        
        return path.get(0);
    }
    
    public void setPosition(Point p) {
        log.log(Level.INFO, "Setting position to {0}", p);
        position = p;
        
        if (path != null && path.size() > 0 && path.get(0).equals(p)) {
            path.remove(0);
        }
    }

    @Override
    public String toString() {
        return material.name() + " cargo to " + target;
    }

    public Point getPosition() {
        return position;
    }

    void promiseDelivery() {
        deliveryPromised = true;
    }
    
    public boolean isDeliveryPromised() {
        return deliveryPromised;
    }

    void clearPromisedDelivery() {
        deliveryPromised = false;
    }
}
