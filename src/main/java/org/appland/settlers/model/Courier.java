package org.appland.settlers.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.Courier.States.GOING_TO_FLAG_TO_DELIVER_CARGO;
import static org.appland.settlers.model.Courier.States.GOING_TO_FLAG_TO_PICK_UP_CARGO;
import static org.appland.settlers.model.Courier.States.IDLE_AT_ROAD;
import static org.appland.settlers.model.Courier.States.RETURNING_TO_IDLE_SPOT;
import static org.appland.settlers.model.Courier.States.WALKING_TO_ROAD;

@Walker(speed = 10)
public class Courier extends Worker {
    private final static Logger log = Logger.getLogger(Courier.class.getSimpleName());

    private Cargo intendedCargo;
    private Road assignedRoad;
    private States state;
    private Point idlePoint;

    private Point findIdlePointAtRoad(Road road) {
        List<Point> wayPoints = road.getWayPoints();
        
        if (wayPoints.size() < 3) {
            return wayPoints.get(0);
        }

        return wayPoints.get((int)(wayPoints.size() / 2));
    }

    private boolean isOnRoad(Road road) {
        return road.getWayPoints().contains(getPosition());
    }

    public boolean isWalkingToIdlePoint() {
        return state == RETURNING_TO_IDLE_SPOT;
    }

    public boolean isWalkingToRoad() {
        return state == WALKING_TO_ROAD;
    }

    public boolean isIdle() {
        return state == IDLE_AT_ROAD;
    }

    enum States {
        WALKING_TO_ROAD, IDLE_AT_ROAD, GOING_TO_FLAG_TO_PICK_UP_CARGO, GOING_TO_FLAG_TO_DELIVER_CARGO, RETURNING_TO_IDLE_SPOT
    }
    
    public Courier(GameMap map) {
        super(map);
        
        intendedCargo = null;
        assignedRoad  = null;

        state = States.WALKING_TO_ROAD;
    }

    @Override
    protected void onIdle() {

        if (state == IDLE_AT_ROAD) {            
            Flag start = assignedRoad.getStartFlag();
            Flag end   = assignedRoad.getEndFlag();

            if (start.hasCargoWaitingForRoad(assignedRoad)) {
                try {
                    Cargo cargo = start.getCargoWaitingForRoad(assignedRoad);
                    
                    planToPickUpCargo(cargo, start);
                    
                    state = GOING_TO_FLAG_TO_PICK_UP_CARGO;
                } catch (InvalidRouteException ex) {
                    Logger.getLogger(Courier.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (end.hasCargoWaitingForRoad(assignedRoad)) {
                try {
                    Cargo cargo = end.getCargoWaitingForRoad(assignedRoad);
                    
                    planToPickUpCargo(cargo, end);
                    state = GOING_TO_FLAG_TO_PICK_UP_CARGO;
                } catch (InvalidRouteException ex) {
                    Logger.getLogger(Courier.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void planToPickUpCargo(Cargo cargo, Flag flag) throws InvalidRouteException {
        cargo.promiseDelivery();

        intendedCargo = cargo;
    
        setTarget(flag.getPosition());
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
        setTarget(otherEnd.getPosition());
    }

    public void pickUpCargoForRoad(Flag flag, Road r) throws Exception {
        Cargo cargoToPickUp = null;
        
        if (!getPosition().equals(flag.getPosition())) {
            throw new Exception("Not at " + flag);
        }
        
        for (Cargo c : flag.getStackedCargo()) {
            if (r.getWayPoints().contains(c.getNextStep())) {
                cargoToPickUp = c;
            }
        }

        pickUpCargoFromFlag(cargoToPickUp, flag);
    }

    public Road getAssignedRoad() {
        return assignedRoad;
    }

    public void assignToRoad(Road newRoad) throws Exception {
        if (getTargetBuilding() != null) {
            throw new Exception("Can't set road as target while flag or building are already targetted");
        }

        Road currentAssignedRoad = assignedRoad;

        assignedRoad = newRoad;    
        idlePoint = findIdlePointAtRoad(newRoad);

        newRoad.setCourier(this);
        
        /* Fulfill delivery if it has been started */
        if (state == GOING_TO_FLAG_TO_DELIVER_CARGO) {
            
            /* Change the target if it doesn't match any of the end points of the new road */
            if (!getTarget().equals(newRoad.getStart()) && !getTarget().equals(newRoad.getEnd())) {
                if (newRoad.getStart().equals(currentAssignedRoad.getStart())) {
                    setTarget(newRoad.getEnd());
                } else {
                    setTarget(newRoad.getStart());
                }
            }

        /* If the courier is going to pick up a new cargo, cancel and go to the new road */
        } else if (state == GOING_TO_FLAG_TO_PICK_UP_CARGO) {
            intendedCargo.clearPromisedDelivery();
            
            intendedCargo = null;
            
            setTarget(idlePoint);

            state = WALKING_TO_ROAD;
        
        /* For the other states, just go to the new road */
        } else {
            setTarget(idlePoint);

            state = WALKING_TO_ROAD;    
        }
    }

    public void putDownCargo() throws Exception {
        Cargo cargo = getCargo();
        Flag flag = map.getFlagAtPoint(getPosition());
        
        flag.putCargo(cargo);
        cargo.setPosition(getPosition());

        setCargo(null);
    }

    @Override
    protected void onArrival() {
        if (state == WALKING_TO_ROAD) {
            state = IDLE_AT_ROAD;
        } else if (state == GOING_TO_FLAG_TO_PICK_UP_CARGO) {
            if (map.isFlagAtPoint(getPosition())) {
                try {                    
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
                    }
                    
                    state = GOING_TO_FLAG_TO_DELIVER_CARGO;
                } catch (Exception ex) {
                    Logger.getLogger(Courier.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (state == GOING_TO_FLAG_TO_DELIVER_CARGO) {
            try {
                Cargo cargoToDeliver = getCargo();

                if (cargoToDeliver.isAtTarget()) {
                    deliverToTarget(cargoToDeliver.getTarget());
                } else {
                    putDownCargo();
                }
                
                Flag flag     = map.getFlagAtPoint(getPosition());
                Flag otherEnd = assignedRoad.getOtherFlag(flag);

                if (flag.hasCargoWaitingForRoad(getAssignedRoad())) {
                    pickUpCargoForRoad(flag, assignedRoad);
                    
                    setTarget(otherEnd.getPosition());
                    
                    state = GOING_TO_FLAG_TO_DELIVER_CARGO;
                } else if (otherEnd.hasCargoWaitingForRoad(assignedRoad)) {
                    Cargo cargoToPickUp = otherEnd.getCargoWaitingForRoad(assignedRoad);
                    
                    planToPickUpCargo(cargoToPickUp, otherEnd);
                    
                    state = GOING_TO_FLAG_TO_PICK_UP_CARGO;
                } else {
                    setTarget(idlePoint);
                    state = RETURNING_TO_IDLE_SPOT;
                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        } else if (state == RETURNING_TO_IDLE_SPOT) {
            state = IDLE_AT_ROAD;
        }
    }
}
