package org.appland.settlers.model;

import java.util.List;
import static org.appland.settlers.model.Courier.States.GOING_BACK_TO_ROAD;
import static org.appland.settlers.model.Courier.States.GOING_TO_BUILDING_TO_DELIVER_CARGO;
import static org.appland.settlers.model.Courier.States.GOING_TO_FLAG_TO_DELIVER_CARGO;
import static org.appland.settlers.model.Courier.States.GOING_TO_FLAG_TO_PICK_UP_CARGO;
import static org.appland.settlers.model.Courier.States.IDLE_AT_ROAD;
import static org.appland.settlers.model.Courier.States.RETURNING_TO_IDLE_SPOT;
import static org.appland.settlers.model.Courier.States.WALKING_TO_ROAD;
import static org.appland.settlers.model.Courier.States.RETURNING_TO_STORAGE;

@Walker(speed = 10)
public class Courier extends Worker {

    private Cargo  intendedCargo;
    private Road   assignedRoad;
    private States state;
    private Point  idlePoint;

    private Point findIdlePointAtRoad(Road road) {
        List<Point> wayPoints = road.getWayPoints();

        if (wayPoints.size() < 3) {
            return wayPoints.get(0);
        }

        return wayPoints.get(wayPoints.size() / 2);
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

    private EndPoint getEndPointAtPoint(Point currentPosition) throws Exception {
        if (map.isFlagAtPoint(currentPosition)) {
            return map.getFlagAtPoint(currentPosition);
        } else if (map.isBuildingAtPoint(currentPosition)) {
            return map.getBuildingAtPoint(currentPosition);
        }

        throw new Exception("No endpoint at " + currentPosition);
    }

    protected enum States {

        WALKING_TO_ROAD, IDLE_AT_ROAD, GOING_TO_FLAG_TO_PICK_UP_CARGO,
        GOING_TO_FLAG_TO_DELIVER_CARGO, RETURNING_TO_IDLE_SPOT,
        GOING_TO_BUILDING_TO_DELIVER_CARGO, GOING_BACK_TO_ROAD,
        RETURNING_TO_STORAGE
    }

    public Courier(Player player, GameMap map) {
        super(player, map);

        intendedCargo = null;
        assignedRoad = null;

        state = WALKING_TO_ROAD;
    }

    @Override
    protected void onIdle() throws Exception {

        if (state == IDLE_AT_ROAD) {
            EndPoint start = assignedRoad.getStartFlag();
            EndPoint end = assignedRoad.getEndFlag();

            if (start.hasCargoWaitingForRoad(assignedRoad)) {
                Cargo cargo = start.getCargoWaitingForRoad(assignedRoad);

                planToPickUpCargo(cargo, start);

                state = GOING_TO_FLAG_TO_PICK_UP_CARGO;
            } else if (end.hasCargoWaitingForRoad(assignedRoad)) {
                Cargo cargo = end.getCargoWaitingForRoad(assignedRoad);

                planToPickUpCargo(cargo, end);

                state = GOING_TO_FLAG_TO_PICK_UP_CARGO;
            }
        }
    }

    private void planToPickUpCargo(Cargo cargo, EndPoint flag) throws Exception {
        cargo.promiseDelivery();

        intendedCargo = cargo;

        setTarget(flag.getPosition());
    }

    public Cargo getPromisedDelivery() {
        return intendedCargo;
    }

    private void pickUpCargoForRoad(EndPoint flag, Road r) throws Exception {
        Cargo cargoToPickUp = null;

        if (!getPosition().equals(flag.getPosition())) {
            throw new Exception("Not at " + flag);
        }

        cargoToPickUp = flag.getCargoWaitingForRoad(r);

        cargoToPickUp.promiseDelivery();

        flag.retrieveCargo(cargoToPickUp);

        setCargo(cargoToPickUp);

        getCargo().clearPromisedDelivery();
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

            /* Fulfill delivery if it has been started */
        } else if (state == GOING_TO_BUILDING_TO_DELIVER_CARGO) {
            List<Point> plannedPath = getPlannedPath();

            /* Deliver cargo to the closest flag in the road if none of the 
             flags are next to the current targeted building 
             */
            if (!getTarget().equals(newRoad.getStart().upLeft())
                    && !getTarget().equals(newRoad.getEnd().upLeft())) {
                int indexOfStart = plannedPath.indexOf(newRoad.getStart());
                int indexOfEnd = plannedPath.indexOf(newRoad.getEnd());

                state = GOING_TO_FLAG_TO_DELIVER_CARGO;

                if (indexOfStart < indexOfEnd) {
                    setTarget(newRoad.getStart());
                } else {
                    setTarget(newRoad.getEnd());
                }
            }

            /* If the courier is on the road closest to the building, keep 
             the state and target so that it goes to the building and 
             delivers the cargo
             */
            /* If the courier is going to pick up a new cargo, cancel and go to the new road */
        } else if (state == GOING_TO_FLAG_TO_PICK_UP_CARGO) {
            intendedCargo.clearPromisedDelivery();

            intendedCargo = null;

            state = WALKING_TO_ROAD;

            setTarget(idlePoint);

            /* For the other states, just go to the new road */
        } else {
            state = WALKING_TO_ROAD;

            setTarget(idlePoint);
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == WALKING_TO_ROAD) {
            state = IDLE_AT_ROAD;
        } else if (state == GOING_TO_FLAG_TO_PICK_UP_CARGO) {
            pickUpCargo();
        } else if (state == GOING_TO_BUILDING_TO_DELIVER_CARGO) {
            deliverCargo();

            state = GOING_BACK_TO_ROAD;

            setTarget(getPosition().downRight());
        } else if (state == GOING_TO_FLAG_TO_DELIVER_CARGO) {
            deliverCargo();

            if (map.getFlagAtPoint(getPosition()).hasCargoWaitingForRoad(assignedRoad)) {
                pickUpCargo();
            } else {            
                planAfterDelivery();
            }
        } else if (state == GOING_BACK_TO_ROAD) {
            if (map.getFlagAtPoint(getPosition()).hasCargoWaitingForRoad(assignedRoad)) {
                pickUpCargo();
            } else {
                state = RETURNING_TO_IDLE_SPOT;
                setTarget(idlePoint);
            }
        } else if (state == RETURNING_TO_IDLE_SPOT) {
            state = IDLE_AT_ROAD;
        } else if (state == RETURNING_TO_STORAGE) {
            Storage storage = (Storage) map.getBuildingAtPoint(getPosition());

            storage.depositWorker(this);
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws Exception {
        if (getCargo() != null) {
            getCargo().setPosition(getPosition());
        }
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = getPlayer().getClosestStorage(getPosition(), null);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            for (Building b : getPlayer().getBuildings()) {
                if (b instanceof Storage) {
                    state = RETURNING_TO_STORAGE;

                    setOffroadTarget(b.getPosition());

                    break;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Courier " + state;
    }

    private void deliverCargo() throws Exception {

        /* Deliver cargo */
        Cargo cargo = getCargo();
        Point currentPosition = getPosition();
        EndPoint endPoint = getEndPointAtPoint(currentPosition);

        endPoint.putCargo(cargo);
        cargo.setPosition(currentPosition);

        setCargo(null);
    }
    
    private void planAfterDelivery() throws Exception {
        Point currentPosition = getPosition();

        EndPoint flag;

        if (map.isBuildingAtPoint(currentPosition)) {
            flag = getEndPointAtPoint(currentPosition.downRight());
        } else {
            flag = getEndPointAtPoint(currentPosition);
        }

        EndPoint otherEnd = assignedRoad.getOtherFlag(flag);

        /* Plan to pick up cargo at other end if needed */
        if (otherEnd.hasCargoWaitingForRoad(assignedRoad)) {
            Cargo cargoToPickUp = otherEnd.getCargoWaitingForRoad(assignedRoad);

            planToPickUpCargo(cargoToPickUp, otherEnd);

            state = GOING_TO_FLAG_TO_PICK_UP_CARGO;
        
        /* Go back to the idle spot if neither end has any cargo to be picked up */
        } else {
            state = RETURNING_TO_IDLE_SPOT;

            setTarget(idlePoint);
        }
    }

    private void pickUpCargo() throws Exception {
        
        EndPoint endPoint = getEndPointAtPoint(getPosition());
        EndPoint otherEnd = assignedRoad.getOtherFlag(endPoint);

        /* Pick up the right cargo if we have promised to do so */
        if (intendedCargo != null) {
            setCargo(endPoint.retrieveCargo(intendedCargo));

            intendedCargo = null;
            getCargo().clearPromisedDelivery();

            /* Pick up the cargo where we stand if needed */
        } else if (endPoint.hasCargoWaitingForRoad(assignedRoad)) {
            pickUpCargoForRoad(endPoint, assignedRoad);
        }
        
        /* If the intended building is directly after the flag, deliver it all the way */
        List<Point> plannedPath = getCargo().getPlannedSteps();

        int index = plannedPath.indexOf(otherEnd.getPosition());
        Point lastPoint = plannedPath.get(plannedPath.size() - 1);

        if (map.isBuildingAtPoint(lastPoint) && index == plannedPath.size() - 2) {
            state = GOING_TO_BUILDING_TO_DELIVER_CARGO;

            setTarget(lastPoint);
        } else {
            state = GOING_TO_FLAG_TO_DELIVER_CARGO;
            
            setTarget(otherEnd.getPosition());
        }
    }
}
