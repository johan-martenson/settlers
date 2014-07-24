package org.appland.settlers.model;

import java.util.List;

@Walker(speed = 10)
public class Courier extends Worker {
    private Cargo intendedCargo;

    public Courier(GameMap map) {
        super(map);
        
        intendedCargo = null;
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
                    carriedCargo.clearPromisedDelivery();
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

        carriedCargo = null;
    }

    @Override
    public void pickUpCargoFromFlag(Cargo c, Flag flag) throws Exception {
        super.pickUpCargoFromFlag(c, flag);
    }

    @Override
    public void pickUpCargoForRoad(Flag flag, Road r) throws Exception {
        super.pickUpCargoForRoad(flag, r);
    }

    @Override
    public Road getAssignedRoad() {
        return super.getAssignedRoad();
    }

    @Override
    public void setAssignedRoad(Road road) {
        super.setAssignedRoad(road);
    }

    @Override
    public Road getTargetRoad() {
        return super.getTargetRoad();
    }

    public void setTargetRoad(Road r) throws Exception {
        if (getTargetFlag() != null || getTargetBuilding() != null) {
            throw new Exception("Can't set road as target while flag or building are already targetted");
        }

        targetRoad = r;
        
        if (position.equals(r.getStart()) || position.equals(r.getEnd())) {            
            setTarget(position);
            
            return;
        }

        /* Find closest endpoint for the road */
        List<Point> path1 = map.findWayWithExistingRoads(position, r.getStart());
        List<Point> path2 = map.findWayWithExistingRoads(position, r.getEnd());

        if (path1.size() < path2.size()) {
            setTarget(r.getStart());
        } else {
            setTarget(r.getEnd());
        }    
    }

    @Override
    protected void onArrival() {
        try {
            if (carriedCargo != null) {
                if (carriedCargo.isAtTarget()) {
                    deliverToTarget(carriedCargo.getTarget());
                } else {
                    putDownCargo();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
