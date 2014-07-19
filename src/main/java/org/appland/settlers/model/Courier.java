package org.appland.settlers.model;

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
                Road assignedRoad = getAssignedRoad();
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
}
