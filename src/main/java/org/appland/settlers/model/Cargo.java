package org.appland.settlers.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cargo implements Piece {

    private static final Logger log = Logger.getLogger(GameMap.class.getName());

    private final Material material;
    private final GameMap  map;

    private Building    target;
    private Point       position;
    private List<Point> path;
    private boolean     deliveryPromised;

    public Cargo(Material materialToSet, GameMap mapToSet) {
        log.log(Level.FINE, "Creating cargo of {0}", materialToSet);

        material         = materialToSet;
        deliveryPromised = false;
        map              = mapToSet;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setTarget(Building target) throws InvalidRouteException, Exception {
        log.log(Level.FINE, "Setting target to {0}", target);
        this.target = target;
        
        path = map.findWayWithExistingRoads(position, target.getPosition());

        path.remove(0);
    }

    public boolean isAtTarget() {
        log.log(Level.FINE, "Checking if target ({0}) equals position ({1})", new Object[]{target.getFlag(), position});

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
        log.log(Level.FINE, "Setting planned route to {0}", steps);
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
        log.log(Level.FINE, "Setting position to {0}", p);
        position = p;
        
        if (path != null && path.size() > 0 && path.get(0).equals(p)) {
            path.remove(0);
        }
    }

    @Override
    public String toString() {
        return material.name() + " cargo to " + target;
    }

    @Override
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

    void transportToStorage() throws Exception {
        Storage stg = map.getClosestStorage(getPosition());

        if (stg != null) {
            setTarget(stg);
        } else {
            target = null;
        }
    }

    void resumeTransport() {
        Storage stg = map.getClosestStorage(getPosition());
        
        if (stg != null) {
            try {
                setTarget(stg);
            } catch (Exception ex) {
                Logger.getLogger(Cargo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void returnToStorage() {
        Storage stg = map.getClosestStorage(getPosition());

        if (stg != null) {
            try {
                setTarget(stg);
            } catch (Exception ex) {
                Logger.getLogger(Cargo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void rerouteIfNeeded() throws InvalidRouteException {

        /* Handle the case where the targeted building cannot receive the cargo */
        if (getTarget() == null) {
            resumeTransport();
        } else if (!map.getBuildings().contains(getTarget())) {
            returnToStorage();
        } else if (getTarget().burningDown()) {
            returnToStorage();
        } else if (getTarget().destroyed()) {
            returnToStorage();
        } else {

            /* Find the best way from this flag */
            List<Point> closestPath = map.findWayWithExistingRoads(getPosition(), getTarget().getPosition());

            /* Return the cargo to storage if there is no available route to the target */
            if (closestPath == null) {

                /* Break the promise to deliver to the target */
                getTarget().cancelPromisedDelivery(this);

                /* Return the cargo to the storage */
                returnToStorage();
            } else {

                /* Update the planned route to use the closest way */
                closestPath.remove(0);

                path = closestPath;
            }
        }
    }
}
