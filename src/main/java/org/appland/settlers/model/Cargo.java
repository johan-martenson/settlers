package org.appland.settlers.model;

import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cargo {

    private Material material;
    private List<Road> plannedRoads;
    private Building target;
    private Flag position;

    private static Logger log = Logger.getLogger(GameMap.class.getName());

    public Cargo(Material m) {
        log.log(Level.INFO, "Creating cargo of {0}", m);

        material = m;

        /* Increase the log level */
        log.setLevel(Level.FINEST);

        Handler[] handlers = log.getHandlers();
        for (Handler h : handlers) {
            h.setLevel(Level.FINEST);
        }
    }

    public static List<Cargo> buildCargoList(Map<Material, Integer> result) {
        // TODO Auto-generated method stub
        return null;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setTarget(Building target, GameMap map) throws InvalidRouteException {
        log.log(Level.INFO, "Setting target to {0}", target);
        this.target = target;
        this.plannedRoads = map.findWayInRoads(position, target.getFlag());
    }

    public boolean isAtTarget() {
        log.log(Level.INFO, "Checking if target ({0}) equals position ({1})", new Object[]{target.getFlag(), position});

        if (position.equals(target.getFlag())) {
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

    public void setPosition(Flag p) {
        log.log(Level.INFO, "Setting position to {0}", p);
        this.position = p;
    }

    @Override
    public String toString() {
        return material.name() + " cargo to " + target;
    }

    public Flag getPosition() {
        return position;
    }
}
