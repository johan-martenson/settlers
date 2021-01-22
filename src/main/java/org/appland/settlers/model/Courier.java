package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.appland.settlers.model.Courier.States.GOING_BACK_TO_ROAD;
import static org.appland.settlers.model.Courier.States.GOING_OFFROAD_TO_FLAG_THEN_GOING_TO_BUILDING_TO_DELIVER_CARGO;
import static org.appland.settlers.model.Courier.States.GOING_TO_BUILDING_TO_DELIVER_CARGO;
import static org.appland.settlers.model.Courier.States.GOING_TO_FLAG_TO_DELIVER_CARGO;
import static org.appland.settlers.model.Courier.States.GOING_TO_FLAG_TO_PICK_UP_CARGO;
import static org.appland.settlers.model.Courier.States.IDLE_AT_ROAD;
import static org.appland.settlers.model.Courier.States.RETURNING_TO_IDLE_SPOT;
import static org.appland.settlers.model.Courier.States.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Courier.States.WAITING_FOR_SPACE_ON_FLAG;
import static org.appland.settlers.model.Courier.States.WALKING_TO_ROAD;

@Walker(speed = 10)
public class Courier extends Worker {

    private Cargo  intendedCargo;
    private Road   assignedRoad;
    private States state;
    private Point  idlePoint;
    private Cargo  lastCargo;

    protected enum States {

        WALKING_TO_ROAD,
        IDLE_AT_ROAD,
        GOING_TO_FLAG_TO_PICK_UP_CARGO,
        GOING_TO_FLAG_TO_DELIVER_CARGO,
        RETURNING_TO_IDLE_SPOT,
        GOING_TO_BUILDING_TO_DELIVER_CARGO,
        GOING_BACK_TO_ROAD,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_OFFROAD_TO_FLAG_THEN_GOING_TO_BUILDING_TO_DELIVER_CARGO,
        RETURNING_TO_STORAGE
    }

    public Courier(Player player, GameMap map) {
        super(player, map);

        intendedCargo = null;
        assignedRoad = null;
        lastCargo = null;

        state = WALKING_TO_ROAD;
    }

    @Override
    protected void onIdle() throws InvalidRouteException {

        if (state == IDLE_AT_ROAD) {
            Flag start = map.getFlagAtPoint(assignedRoad.getStart());
            Flag end   = map.getFlagAtPoint(assignedRoad.getEnd());

            Cargo cargoAtStart = findCargoToCarry(start);

            if (cargoAtStart != null) {

                cargoAtStart.promisePickUp();
                intendedCargo = cargoAtStart;
                setTarget(start.getPosition());

                state = GOING_TO_FLAG_TO_PICK_UP_CARGO;
            } else {

                Cargo cargoAtEnd = findCargoToCarry(end);

                if (cargoAtEnd != null) {

                    cargoAtEnd.promisePickUp();
                    intendedCargo = cargoAtEnd;
                    setTarget(end.getPosition());

                    state = GOING_TO_FLAG_TO_PICK_UP_CARGO;
                }
            }
        } else if (state == WAITING_FOR_SPACE_ON_FLAG) {
            Flag flag = map.getFlagAtPoint(getCargo().getNextFlagOrBuilding());

            if (flag.hasPlaceForMoreCargo()) {
                state = GOING_TO_FLAG_TO_DELIVER_CARGO;

                setTarget(flag.getPosition());

                flag.promiseCargo(getCargo());
            }
        }
    }

    public Cargo getPromisedDelivery() {
        return intendedCargo;
    }

    public Road getAssignedRoad() {
        return assignedRoad;
    }

    public void assignToRoad(Road newRoad) throws InvalidRouteException {
        if (getTargetBuilding() != null) {
            throw new InvalidGameLogicException("Can't set road as target while flag or building are already targeted");
        }

        Road previousRoad = assignedRoad;

        assignedRoad = newRoad;
        idlePoint = findIdlePointAtRoad(newRoad);

        newRoad.setCourier(this);

        /* Fulfill delivery if it has been started */
        if (state == GOING_TO_FLAG_TO_DELIVER_CARGO) {

            /* Change the target if it doesn't match any of the end points of the new road */
            if (!getTarget().equals(newRoad.getStart()) && !getTarget().equals(newRoad.getEnd())) {

                if (newRoad.getStart().equals(previousRoad.getStart()) || newRoad.getStart().equals(previousRoad.getEnd())) {
                    setTarget(newRoad.getEnd());
                } else {
                    setTarget(newRoad.getStart());
                }
            }

        /* Fulfill delivery if it has been started */
        } else if (state == GOING_TO_BUILDING_TO_DELIVER_CARGO) {
            List<Point> plannedPath = getPlannedPath();

            /* Deliver cargo to the closest flag in the road if none of the flags are next to the current targeted building */
            if (!getTarget().equals(newRoad.getStart().upLeft()) && !getTarget().equals(newRoad.getEnd().upLeft())) {
                int indexOfStart = plannedPath.indexOf(newRoad.getStart());
                int indexOfEnd = plannedPath.indexOf(newRoad.getEnd());

                state = GOING_TO_FLAG_TO_DELIVER_CARGO;

                if (indexOfStart == -1) {
                    setTarget(newRoad.getEnd());
                } else if (indexOfEnd == -1) {
                    setTarget(newRoad.getStart());
                }
            }

        /*
         *  If the courier is on the road closest to the building, keep the state and target so that it goes to the building and delivers the cargo
         *
         * If the courier is going to pick up a new cargo, cancel and go to the new road
         *  */
        } else if (state == GOING_TO_FLAG_TO_PICK_UP_CARGO) {
            intendedCargo.cancelPromisedPickUp();

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
    protected void onArrival() throws InvalidRouteException {

        if (state == WALKING_TO_ROAD) {
            state = IDLE_AT_ROAD;
        } else if (state == GOING_TO_FLAG_TO_PICK_UP_CARGO) {
            Flag flag = map.getFlagAtPoint(getPosition());
            Cargo cargoToPickUp = findCargoToCarry(flag);

            pickUpCargoAndGoDeliver(cargoToPickUp);
        } else if (state == GOING_TO_BUILDING_TO_DELIVER_CARGO) {

            Building building = map.getBuildingAtPoint(getPosition());

            /* Cannot delivery if the building has just been torn down */
            if (building.isBurningDown()) {

                /* Return to the headquarter off-road because the driveway is gone */
                state = GOING_OFFROAD_TO_FLAG_THEN_GOING_TO_BUILDING_TO_DELIVER_CARGO;

                setOffroadTarget(getPosition().downRight());

            /* Deliver the cargo normally */
            } else {
                deliverCargo();

                state = GOING_BACK_TO_ROAD;

                setTarget(getPosition().downRight());
            }
        } else if (state == GOING_TO_FLAG_TO_DELIVER_CARGO) {
            deliverCargo();

            Cargo cargoToPickUp = findCargoToCarry(map.getFlagAtPoint(getPosition()));

            if (cargoToPickUp != null) {
                pickUpCargoAndGoDeliver(cargoToPickUp);
            } else {

                state = RETURNING_TO_IDLE_SPOT;
                setTarget(idlePoint);
            }
        } else if (state == GOING_BACK_TO_ROAD) {

            Flag flag = map.getFlagAtPoint(getPosition());
            Cargo cargoToPickUp = findCargoToCarry(flag);

            if (cargoToPickUp != null) {
                pickUpCargoAndGoDeliver(cargoToPickUp);
            } else {
                state = RETURNING_TO_IDLE_SPOT;
                setTarget(idlePoint);
            }
        } else if (state == RETURNING_TO_IDLE_SPOT) {
            state = IDLE_AT_ROAD;
        } else if (state == RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse) map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        } else if (state == GOING_OFFROAD_TO_FLAG_THEN_GOING_TO_BUILDING_TO_DELIVER_CARGO) {
            state = GOING_TO_BUILDING_TO_DELIVER_CARGO;

            /* Return the cargo to the headquarter */
            getCargo().transportToStorage();

            setTarget(getAssignedRoad().getOtherPoint(getPosition()));
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws InvalidRouteException {
        Point position = getPosition();
        MapPoint mapPoint = map.getMapPoint(position);
        Cargo cargo = getCargo();

        if (cargo == null) {
            return;
        }

        // TODO: verify that this call can be removed. There should be no need for cargos to know where they are when they are being carried
        cargo.setPosition(getPosition());

        List<Point> plannedPath = getPlannedPath();

        Building cargoTargetBuilding = getCargo().getTarget();

        /* If at the point before the flag */
        if (!plannedPath.isEmpty() && map.isFlagAtPoint(plannedPath.get(0))) {
            Point nextPoint = plannedPath.get(0);
            MapPoint mapPointNext = map.getMapPoint(nextPoint);

            Flag flag = mapPointNext.getFlag();

            if (state == GOING_TO_FLAG_TO_DELIVER_CARGO) {

                /* Wait if there is no space at the flag to put down the cargo */
                if (!flag.hasPlaceForMoreCargo()) {
                    state = WAITING_FOR_SPACE_ON_FLAG;

                    stopWalkingToTarget();
                } else {
                    flag.promiseCargo(getCargo());
                }
            }
        }

        /* Return the cargo to storage if the building is torn down */
        // TODO: handle the case where the building has had time to get fully removed
        else if (state != RETURNING_TO_STORAGE && mapPoint.isFlag() &&
                 (cargoTargetBuilding.isBurningDown() ||
                  cargoTargetBuilding.isDestroyed()   ||
                  !cargoTargetBuilding.equals(map.getBuildingAtPoint(cargoTargetBuilding.getPosition())))) {

            Material material = getCargo().getMaterial();

            Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, material);

            /* Return the cargo to the storage if possible */
            if (storage != null) {
                cargo.setTarget(storage);

                /* Deliver the cargo either to the storage's flag or directly to the storage */
                deliverToFlagOrBuilding(cargo);

            /* Drop the cargo if it cannot be delivered or returned */
            } else  {
                setCargo(null);

                state = RETURNING_TO_IDLE_SPOT;

                setTarget(idlePoint);
            }
        }
    }

    @Override
    protected void onReturnToStorage() throws InvalidRouteException {

        /* Cancel any promised deliveries */
        Cargo cargo = getCargo();

        if (cargo != null && cargo.getTarget() != null) {
            Building building = cargo.getTarget();

            if (!building.isStorehouse()) {
                building.cancelPromisedDelivery(cargo);
            }
        }

        /* Return to storage */
        Building storage = getPlayer().getClosestStorage(getPosition(), null);
        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            for (Building building : getPlayer().getBuildings()) {
                if (building.isStorehouse()) {
                    state = RETURNING_TO_STORAGE;

                    setOffroadTarget(building.getPosition());

                    break;
                }
            }
        }
    }

    @Override
    public String toString() {
        if (isExactlyAtPoint()) {
            return "Courier " + getPosition();
        } else {
            return "Courier " + getPosition() + " - " + getNextPoint();
        }
    }

    private void deliverCargo() throws InvalidRouteException {

        /* Deliver cargo */
        Cargo cargo = getCargo();
        Point currentPosition = getPosition();
        EndPoint endPoint = getEndPointAtPoint(currentPosition);

        endPoint.putCargo(cargo);
        cargo.setPosition(currentPosition);

        lastCargo = cargo;

        setCargo(null);
    }

    private void pickUpCargoAndGoDeliver(Cargo cargoToPickUp) throws InvalidRouteException {

        Point point = getPosition();
        Flag endPoint = map.getFlagAtPoint(point);

        /* Pick up the right cargo if we have promised to do so */
        if (intendedCargo != null) {
            setCargo(endPoint.retrieveCargo(intendedCargo));

            intendedCargo = null;
            getCargo().cancelPromisedPickUp();

        /* Pick up the cargo where we stand if needed */
        } else if (cargoToPickUp != null) {

            if (!point.equals(endPoint.getPosition())) {
                throw new InvalidGameLogicException("Not at " + endPoint);
            }

            endPoint.retrieveCargo(cargoToPickUp);
            setCargo(cargoToPickUp);
        }

        /* Deliver the cargo to the other flag or all the way to the building */
        deliverToFlagOrBuilding(getCargo());
    }

    private void deliverToFlagOrBuilding(Cargo cargo) throws InvalidRouteException {

        /* If the intended building is directly after the flag, deliver it all the way */
        Point cargoTarget = cargo.getTarget().getPosition();

        if (cargoTarget.downRight().equals(assignedRoad.getStart()) ||
            cargoTarget.downRight().equals(assignedRoad.getEnd())) {
            state = GOING_TO_BUILDING_TO_DELIVER_CARGO;

            List<Point> roadPoints = assignedRoad.getWayPoints();
            List<Point> toWalk = new ArrayList<>(roadPoints);

            if (!roadPoints.get(0).equals(getPosition())) {
                Collections.reverse(toWalk);
            }

            toWalk.add(cargoTarget);

            setTargetWithPath(toWalk);
        } else {
            state = GOING_TO_FLAG_TO_DELIVER_CARGO;

            if (assignedRoad.getWayPoints().get(0).equals(getPosition())) {
                setTargetWithPath(assignedRoad.getWayPoints());
            } else {
                List<Point> toWalk = new LinkedList<>(assignedRoad.getWayPoints());

                Collections.reverse(toWalk);

                setTargetWithPath(toWalk);
            }
        }
    }

    private Point findIdlePointAtRoad(Road road) {
        List<Point> wayPoints = road.getWayPoints();

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

    private EndPoint getEndPointAtPoint(Point point) {
        MapPoint mapPoint = map.getMapPoint(point);

        if (mapPoint.isFlag()) {
            return mapPoint.getFlag();
        }

        return mapPoint.getBuilding();
    }

    public Cargo findCargoToCarry(Flag flag) {
        GameMap map = getMap();

        Point point = flag.getPosition();
        MapPoint mapPoint = map.getMapPoint(point);

        Cargo waitingCargo = null;

        /* Look for the most prioritized cargo. Lower is better. */
        int priority = Integer.MAX_VALUE;

        /* Look up the other end of the road */
        EndPoint otherEndOfRoad = getAssignedRoad().getOtherEndPoint(flag);

        /* Go through the cargos and look for the best cargo to pick up */
        for (Cargo cargo : flag.getStackedCargo()) {

            /* Get the target for the cargo */
            Building target = cargo.getTarget();

            /* Filter cargos where pickup is already planned */
            if (cargo.isPickupPromised()) {
                continue;
            }

            /* Filter cargos without a target */
            if (target == null) {
                continue;
            }

            /* Try to avoid picking up the cargo that the courier just dropped off */
            if (cargo.equals(lastCargo)) {

                /* Does the flag have other roads? */
                if (mapPoint.getConnectedRoads().size() > 1) {

                    /* Is there another way for the cargo to get delivered? */
                    List<Point> otherRoute = map.findDetailedWayWithExistingRoadsInFlagsAndBuildings(flag, target, otherEndOfRoad.getPosition());

                    if (otherRoute != null) {
                        continue;
                    }
                }
            }

            /* Filter cargos that will not benefit from going through the courier's road */
            List<Point> bestPath = map.findDetailedWayWithExistingRoadsInFlagsAndBuildings(flag, target);

            List<Point> pathThroughRoad = map.findDetailedWayWithExistingRoadsInFlagsAndBuildings(otherEndOfRoad, target, point);

            /* Filter cargos where there is no road available */
            if (bestPath == null) {
                continue;
            }

            /* Filter cargos where going through the couriers road doesn't lead to the target */
            if (pathThroughRoad == null) {
                continue;
            }

            /* Let the best courier do the delivery if it's available */
            Road optimalRoad = map.getRoadAtPoint(bestPath.get(1));

            Courier courierForOptimalRoad = optimalRoad.getCourier();
            Donkey donkeyForOptimalRoad = optimalRoad.getDonkey();

            /* If the courier's road is not the optimal road - see if the optimal courier or donkey is idle */
            if (!getAssignedRoad().equals(optimalRoad)) {
                if ((courierForOptimalRoad != null && courierForOptimalRoad.isIdle()) ||
                    (donkeyForOptimalRoad  != null && donkeyForOptimalRoad.isIdle())) {
                    continue;
                }
            }

            /* Avoid roads that are more than double as long as the most optimal road */
            if (pathThroughRoad.size() > bestPath.size() * 2) {
                continue;
            }

            int candidatePriority = player.getTransportPriority(cargo);

            if (candidatePriority < priority) {
                priority = candidatePriority;
                waitingCargo = cargo;
            }

            if (priority == 0) {
                break;
            }
        }

        return waitingCargo;
    }
}
