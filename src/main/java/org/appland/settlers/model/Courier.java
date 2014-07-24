package org.appland.settlers.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Walker(speed = 10)
public class Courier extends Worker {

    private final static Logger log = Logger.getLogger(Courier.class.getSimpleName());

    private Cargo intendedCargo;
    private Road assignedRoad;
    private Road targetRoad;
    private Flag targetFlag;

    public Courier(GameMap map) {
        super(map);
        
        intendedCargo = null;
        assignedRoad  = null;
        targetRoad    = null;
        targetFlag    = null;
    }

    @Override
    protected void onIdle() {
        if (!isAssignedToRoad()) {
            return;
        }
        
        try {
            if (map.isFlagAtPoint(getPosition())) {
                Flag flag         = map.getFlagAtPoint(getPosition());
                Flag otherEnd     = assignedRoad.getOtherFlag(flag);
                
                /* Pick up the right cargo if we have promised to do so */
                if (intendedCargo != null) {
                    pickUpCargoFromFlag(intendedCargo, flag);
                    
                    intendedCargo = null;
                    getCargo().clearPromisedDelivery();
                /* Pick up the cargo where we stand if needed */
                } else if (flag.hasCargoWaitingForRoad(assignedRoad)) {
                    pickUpCargoForRoad(flag, assignedRoad);
                    
                /* See if there is a cargo at the other end to pick up */
                } else if (otherEnd.hasCargoWaitingForRoad(assignedRoad)) {
                    Cargo cargo = otherEnd.getCargoWaitingForRoad(assignedRoad);
                    
                    planToPickUpCargo(cargo, otherEnd);
                }
            } else {
                Road r = getAssignedRoad();
                Flag start = r.getStartFlag();
                Flag end = r.getEndFlag();
                
                if (start.hasCargoWaitingForRoad(r)) {
                    Cargo cargo = start.getCargoWaitingForRoad(r);
                    
                    planToPickUpCargo(cargo, start);
                } else if (end.hasCargoWaitingForRoad(r)) {
                    Cargo cargo = end.getCargoWaitingForRoad(r);
                    
                    planToPickUpCargo(cargo, end);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAssignedToRoad() {
        return getAssignedRoad() != null;
    }
    
    private void planToPickUpCargo(Cargo cargo, Flag flag) throws InvalidRouteException {
        cargo.promiseDelivery();

        intendedCargo = cargo;

        setTargetFlag(flag);    
    }

    public Cargo getPromisedDelivery() {
        return intendedCargo;
    }

    public void deliverToTarget(Building targetBuilding) throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
        targetBuilding.deliver(this.getCargo());

        setCargo(null);
    }

    public void pickUpCargoFromFlag(Cargo c, Flag flag) throws Exception {
        setCargo(flag.retrieveCargo(c));

        Flag otherEnd = getAssignedRoad().getOtherFlag(flag);
        setTargetFlag(otherEnd);
    }

    public void pickUpCargoForRoad(Flag flag, Road r) throws Exception {
        Cargo cargoToPickUp = null;
        
        if (!getPosition().equals(flag.getPosition())) {
            throw new Exception("Not at " + flag);
        }
        
        for (Cargo c : flag.getStackedCargo()) {
            if (c.getPlannedRoads().get(0).equals(r)) {
                cargoToPickUp = c;
            }
        }

        pickUpCargoFromFlag(cargoToPickUp, flag);
    }

    public Road getAssignedRoad() {
        return assignedRoad;
    }

    public void setAssignedRoad(Road road) {
        assignedRoad = road;
    }

    public Road getTargetRoad() {
        return targetRoad;
    }

    public void setTargetRoad(Road r) throws Exception {
        if (getTargetFlag() != null || getTargetBuilding() != null) {
            throw new Exception("Can't set road as target while flag or building are already targetted");
        }

        targetRoad = r;
        
        if (isAt(r.getStart()) || isAt(r.getEnd())) {            
            setTarget(getPosition());
            
            return;
        }

        /* Find closest endpoint for the road */
        List<Point> path1 = map.findWayWithExistingRoads(getPosition(), r.getStart());
        List<Point> path2 = map.findWayWithExistingRoads(getPosition(), r.getEnd());

        if (path1.size() < path2.size()) {
            setTarget(r.getStart());
        } else {
            setTarget(r.getEnd());
        }    
    }

    @Override
    protected void onArrival() {
        try {
            Cargo cargo = getCargo();
            if (cargo != null) {
                if (cargo.isAtTarget()) {
                    deliverToTarget(cargo.getTarget());
                } else {
                    putDownCargo();
                }
            } else if (getTargetRoad() != null) {
                map.assignCourierToRoad(this, getTargetRoad());
                stopTraveling();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    @Override
    public void stopTraveling() {
        targetFlag = null;
        targetRoad = null;
        
        super.stopTraveling();
    }
    
    public void putDownCargo() {
        Cargo cargo = getCargo();
        
        getTargetFlag().putCargo(cargo);
        cargo.setPosition(getPosition());
        
        List<Road> plannedRoadForCargo = cargo.getPlannedRoads();
        plannedRoadForCargo.remove(0);
        cargo.setPlannedRoads(plannedRoadForCargo);

        setCargo(null);
    }

    public Flag getTargetFlag() {
        return targetFlag;
    }

    public void setTargetFlag(Flag t) throws InvalidRouteException {
        log.log(Level.INFO, "Setting target flag to {0}, previous target was {1}", new Object[]{t, getTarget()});
        
        targetFlag = t;
        
        setTarget(t.getPosition());
    }
}
