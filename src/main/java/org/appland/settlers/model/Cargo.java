package org.appland.settlers.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cargo {

    private Material   material;
    private List<Road> plannedRoads;
    private Building   target;
    private Point      position;

    private static Logger log = Logger.getLogger(GameMap.class.getName());
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
        this.plannedRoads = map.findWayInRoads(position, target.getFlag().getPosition());
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

    public void setPlannedRoads(List<Road> roads) {
        log.log(Level.INFO, "Setting planned route to {0}", roads);
        this.plannedRoads = roads;
    }

    public List<Road> getPlannedRoads() {
        return plannedRoads;
    }

    public void setPosition(Point p) {
        log.log(Level.INFO, "Setting position to {0}", p);
        this.position = p;
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
