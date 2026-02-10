package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.EndPoint;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidGameLogicException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Storehouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.appland.settlers.model.WorkerAction.*;
import static org.appland.settlers.model.actors.Courier.BodyType.FAT;
import static org.appland.settlers.model.actors.Courier.BodyType.THIN;
import static org.appland.settlers.model.actors.Courier.States.*;

@Walker(speed = 10)
public class Courier extends Worker {
    private static final Random RANDOM = new Random(1);
    private static final int TIME_TO_CHEW_GUM = 29;
    private static final int TIME_TO_READ_PAPER = 29;
    private static final int TIME_TO_TOUCH_NOSE = 29;
    private static final int TIME_TO_JUMP_SKIP_ROPE = 29;
    private static final int TIME_TO_SIT_DOWN = 29;
    private static final int TIME_WALK_TO_FLAG = 10;

    private final BodyType bodyType;
    private final Countdown countdown = new Countdown();

    private Cargo intendedCargo = null;
    private Road assignedRoad = null;
    private States state = WALKING_TO_ROAD;
    private Point idlePoint;
    private Cargo lastCargo = null;
    private Flag waitToGoToFlag;

    protected boolean shouldDoSpecialActions = true;

    protected enum States {
        WALKING_TO_ROAD,
        IDLE_AT_ROAD,
        IDLE_CHEWING_GUM,
        GOING_TO_FLAG_TO_PICK_UP_CARGO,
        GOING_TO_FLAG_TO_DELIVER_CARGO,
        RETURNING_TO_IDLE_SPOT,
        GOING_TO_BUILDING_TO_DELIVER_CARGO,
        GOING_BACK_TO_ROAD,
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_OFFROAD_TO_FLAG_THEN_GOING_TO_BUILDING_TO_DELIVER_CARGO,
        IDLE_READING_PAPER,
        IDLE_TOUCHING_NOSE,
        IDLE_JUMPING_SKIP_ROPE,
        IDLE_SITTING_DOWN,
        WAITING_FOR_FIGHTING_AT_FLAG,
        RETURNING_TO_STORAGE
    }

    public Courier(Player player, GameMap map) {
        super(player, map);

        bodyType = RANDOM.nextBoolean() ? THIN : FAT;
    }

    @Override
    protected void onIdle() {
        switch (state) {
            case IDLE_AT_ROAD, IDLE_CHEWING_GUM, IDLE_READING_PAPER, IDLE_TOUCHING_NOSE, IDLE_JUMPING_SKIP_ROPE, IDLE_SITTING_DOWN -> {
                var start = map.getFlagAtPoint(assignedRoad.getStart());
                var end = map.getFlagAtPoint(assignedRoad.getEnd());

                // TODO: REMOVE!
                if (start == null || end == null) {
                    System.out.println(this);
                    System.out.println(assignedRoad);
                    System.out.println(assignedRoad.getStart());
                    System.out.println(assignedRoad.getEnd());
                    System.out.println(getCargo());
                    System.out.println(start);
                    System.out.println(end);
                }

                // Find cargo to carry
                var cargoAtStart = findCargoToCarry(start);
                var cargoAtEnd = findCargoToCarry(end);

                // Pick up cargo if available
                if (cargoAtStart != null && !(start.isFightingAtFlag() && start.getPosition().distance(getPosition()) == 1)) {
                    cargoAtStart.promisePickUp();
                    intendedCargo = cargoAtStart;
                    setTarget(start.getPosition());

                    state = GOING_TO_FLAG_TO_PICK_UP_CARGO;
                } else if (cargoAtEnd != null && !(end.isFightingAtFlag() && end.getPosition().distance(getPosition()) == 1)) {
                    cargoAtEnd.promisePickUp();
                    intendedCargo = cargoAtEnd;
                    setTarget(end.getPosition());

                    state = GOING_TO_FLAG_TO_PICK_UP_CARGO;
                }

                // Handle special actions
                if (shouldDoSpecialActions && state == IDLE_AT_ROAD) {
                    if (RANDOM.nextInt(135) == 5 && bodyType == FAT) {
                        doAction(CHEW_GUM);
                        state = IDLE_CHEWING_GUM;
                        countdown.countFrom(TIME_TO_CHEW_GUM);
                    } else if (RANDOM.nextInt(135) == 5 && bodyType == THIN) {
                        doAction(READ_NEWSPAPER);
                        state = IDLE_READING_PAPER;
                        countdown.countFrom(TIME_TO_READ_PAPER);
                    } else if (RANDOM.nextInt(135) == 5 && bodyType == THIN) {
                        doAction(TOUCH_NOSE);
                        state = IDLE_TOUCHING_NOSE;
                        countdown.countFrom(TIME_TO_TOUCH_NOSE);
                    } else if (RANDOM.nextInt(115) == 5 && bodyType == THIN) {
                        doAction(JUMP_SKIP_ROPE);
                        state = IDLE_JUMPING_SKIP_ROPE;
                        countdown.countFrom(TIME_TO_JUMP_SKIP_ROPE);
                    } else if (RANDOM.nextInt(115) == 5 && bodyType == FAT) {
                        doAction(SIT_DOWN);
                        state = IDLE_SITTING_DOWN;
                        countdown.countFrom(TIME_TO_SIT_DOWN);
                    }
                } else {
                    if (state == IDLE_CHEWING_GUM) {
                        if (countdown.hasReachedZero()) {
                            state = IDLE_AT_ROAD;
                        } else {
                            countdown.step();
                        }
                    } else if (state == IDLE_READING_PAPER) {
                        if (countdown.hasReachedZero()) {
                            state = IDLE_AT_ROAD;
                        } else {
                            countdown.step();
                        }
                    } else if (state == IDLE_TOUCHING_NOSE) {
                        if (countdown.hasReachedZero()) {
                            state = IDLE_AT_ROAD;
                        } else {
                            countdown.step();
                        }
                    } else if (state == IDLE_JUMPING_SKIP_ROPE) {
                        if (countdown.hasReachedZero()) {
                            state = IDLE_AT_ROAD;
                        } else {
                            countdown.step();
                        }
                    } else if (state == IDLE_SITTING_DOWN) {
                        if (countdown.hasReachedZero()) {
                            state = IDLE_AT_ROAD;
                        } else {
                            countdown.step();
                        }
                    }
                }
            }
            case WAITING_FOR_FIGHTING_AT_FLAG -> {
                if (!waitToGoToFlag.isFightingAtFlag()) {
                    if (getCargo() != null) {
                        if (waitToGoToFlag.hasPlaceForMoreCargo()) {
                            state = GOING_TO_FLAG_TO_DELIVER_CARGO;
                            setTarget(waitToGoToFlag.getPosition());
                            waitToGoToFlag.promiseCargo(getCargo());
                        } else {
                            state = WAITING_FOR_SPACE_ON_FLAG;
                        }
                    } else {
                        var cargo = findCargoToCarry(waitToGoToFlag);
                        intendedCargo = cargo;

                        if (cargo != null) {
                            cargo.promisePickUp();
                            state = GOING_TO_FLAG_TO_PICK_UP_CARGO;
                            setTarget(waitToGoToFlag.getPosition());
                        } else {
                            state = RETURNING_TO_IDLE_SPOT;
                            setTarget(idlePoint);
                        }
                    }
                }
            }
            case WAITING_FOR_SPACE_ON_FLAG -> {
                if (waitToGoToFlag.hasPlaceForMoreCargo()) {
                    state = GOING_TO_FLAG_TO_DELIVER_CARGO;
                    setTarget(waitToGoToFlag.getPosition());
                    waitToGoToFlag.promiseCargo(getCargo());
                }
            }
        }
    }

    public Cargo getPromisedDelivery() {
        return intendedCargo;
    }

    public Road getAssignedRoad() {
        return assignedRoad;
    }

    public void assignToRoad(Road newRoad) {
        if (targetBuilding != null) {
            throw new InvalidGameLogicException("Can't set road as target while flag or building are already targeted");
        }

        var previousRoad = assignedRoad;

        assignedRoad = newRoad;
        idlePoint = findIdlePointAtRoad(newRoad);

        newRoad.setCourier(this);

        switch (state) {
            case GOING_TO_FLAG_TO_DELIVER_CARGO -> {

                // Change the target if it doesn't match any of the end points of the new road
                if (!getTarget().equals(newRoad.getStart()) && !getTarget().equals(newRoad.getEnd())) {
                    if (newRoad.getStart().equals(previousRoad.getStart()) || newRoad.getStart().equals(previousRoad.getEnd())) {
                        setTarget(newRoad.getEnd());
                    } else {
                        setTarget(newRoad.getStart());
                    }
                }
            }
            case GOING_TO_BUILDING_TO_DELIVER_CARGO -> {
                var plannedPath = getPlannedPath();

                // Deliver cargo to the closest flag in the road if none of the flags are next to the current targeted building
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
            }
            case GOING_TO_FLAG_TO_PICK_UP_CARGO -> {
                intendedCargo.cancelPromisedPickUp();
                intendedCargo = null;

                state = WALKING_TO_ROAD;
                setTarget(idlePoint);
            }
            default -> {
                state = WALKING_TO_ROAD;
                setTarget(idlePoint);
            }
        }
    }

    @Override
    protected void onArrival() {
        switch (state) {
            case WALKING_TO_ROAD, RETURNING_TO_IDLE_SPOT -> state = IDLE_AT_ROAD;
            case GOING_TO_FLAG_TO_PICK_UP_CARGO -> {
                var flag = map.getFlagAtPoint(getPosition());
                var cargoToPickUp = findCargoToCarry(flag);

                if (intendedCargo != null) {
                    intendedCargo.cancelPromisedPickUp();
                    intendedCargo = null;
                }

                if (cargoToPickUp != null) {
                    pickUpCargoAndGoDeliver(cargoToPickUp);
                } else {
                    setTarget(idlePoint);
                    state = RETURNING_TO_IDLE_SPOT;
                }
            }
            case GOING_TO_BUILDING_TO_DELIVER_CARGO -> {
                var building = map.getBuildingAtPoint(getPosition());

                // Cannot deliver if the building has just been torn down
                if (building == null || building.isBurningDown() || building.isDestroyed()) {

                    // Return to the headquarters off-road because the driveway is gone
                    state = GOING_OFFROAD_TO_FLAG_THEN_GOING_TO_BUILDING_TO_DELIVER_CARGO;
                    setOffroadTarget(getPosition().downRight());

                    // Deliver the cargo normally
                } else {
                    deliverCargo();

                    state = GOING_BACK_TO_ROAD;
                    setTarget(getPosition().downRight());
                }
            }
            case GOING_TO_FLAG_TO_DELIVER_CARGO -> {
                deliverCargo();

                var cargoToPickUp = findCargoToCarry(map.getFlagAtPoint(getPosition()));

                if (cargoToPickUp != null) {
                    pickUpCargoAndGoDeliver(cargoToPickUp);
                } else {
                    state = RETURNING_TO_IDLE_SPOT;
                    setTarget(idlePoint);
                }
            }
            case GOING_BACK_TO_ROAD -> {
                var flag = map.getFlagAtPoint(getPosition());
                var cargoToPickUp = findCargoToCarry(flag);

                if (cargoToPickUp != null) {
                    pickUpCargoAndGoDeliver(cargoToPickUp);
                } else {
                    state = RETURNING_TO_IDLE_SPOT;
                    setTarget(idlePoint);
                }
            }
            case RETURNING_TO_STORAGE -> {
                var storehouse = (Storehouse) map.getBuildingAtPoint(getPosition());
                storehouse.depositWorker(this);
            }
            case GOING_OFFROAD_TO_FLAG_THEN_GOING_TO_BUILDING_TO_DELIVER_CARGO -> {

                // Return the cargo to the headquarters
                getCargo().transportToStorage();

                state = GOING_TO_BUILDING_TO_DELIVER_CARGO;
                setTarget(getCargo().getTarget().getPosition());
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {
        var position = getPosition();
        var plannedPath = getPlannedPath();
        var mapPoint = map.getMapPoint(position);
        var cargo = getCargo();
        var targetBuilding = cargo != null ? cargo.getTarget() : null;
        var isAtPointBeforeFlag = !plannedPath.isEmpty() && map.isFlagAtPoint(plannedPath.getFirst());

        switch (state) {
            case GOING_TO_FLAG_TO_DELIVER_CARGO -> {
                if (isAtPointBeforeFlag) {
                    var nextPoint = plannedPath.getFirst();
                    var mapPointNext = map.getMapPoint(nextPoint);
                    var flag = mapPointNext.getFlag();

                    if (!flag.hasPlaceForMoreCargo()) {
                        state = WAITING_FOR_SPACE_ON_FLAG;
                        waitToGoToFlag = flag;

                        stopWalkingToTarget();
                    } else if (flag.isFightingAtFlag()) {
                        state = WAITING_FOR_FIGHTING_AT_FLAG;

                        waitToGoToFlag = flag;

                        stopWalkingToTarget();
                    } else {
                        flag.promiseCargo(getCargo());
                    }
                } else if (mapPoint.isFlag()) {
                    if (targetBuilding != null &&
                            (targetBuilding.isBurningDown() ||
                                    targetBuilding.isDestroyed() ||
                                    !targetBuilding.equals(map.getBuildingAtPoint(getCargo().getTarget().getPosition())))) {

                        var material = getCargo().getMaterial();

                        var storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, material);

                        // Return the cargo to the storage if possible
                        if (storage != null) {
                            cargo.setTarget(storage);

                            // Deliver the cargo either to the storage's flag or directly to the storage
                            deliverToFlagOrBuilding(cargo);

                            // Drop the cargo if it cannot be delivered or returned
                        } else  {
                            setCargo(null);

                            state = RETURNING_TO_IDLE_SPOT;

                            setTarget(idlePoint);
                        }
                    }
                }
            }
            case GOING_TO_FLAG_TO_PICK_UP_CARGO -> {
                if (isAtPointBeforeFlag) {
                    var nextPoint = plannedPath.getFirst();
                    var mapPointNext = map.getMapPoint(nextPoint);
                    var flag = mapPointNext.getFlag();

                    if (flag.isFightingAtFlag()) {
                        state = WAITING_FOR_FIGHTING_AT_FLAG;

                        waitToGoToFlag = flag;

                        stopWalkingToTarget();
                    }
                }
            }
            case GOING_TO_BUILDING_TO_DELIVER_CARGO -> {
                if (mapPoint.isFlag()) {

                    // Is the courier at the flag of the building and should the door be open for a delivery?
                    if (targetBuilding != null &&
                            getPosition().equals(targetBuilding.getPosition().downRight()) &&
                            targetBuilding.isReady()) {
                        targetBuilding.openDoor(TIME_WALK_TO_FLAG * 2);

                    // Has the building been torn down?
                    }

                    if (targetBuilding != null &&
                            (targetBuilding.isBurningDown() ||
                                    targetBuilding.isDestroyed() ||
                                    !targetBuilding.equals(map.getBuildingAtPoint(getCargo().getTarget().getPosition())))) {

                        var material = getCargo().getMaterial();

                        var storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, material);

                        // Return the cargo to the storage if possible
                        if (storage != null) {
                            cargo.setTarget(storage);

                            // Deliver the cargo either to the storage's flag or directly to the storage
                            deliverToFlagOrBuilding(cargo);

                            // Drop the cargo if it cannot be delivered or returned
                        } else  {
                            setCargo(null);

                            state = RETURNING_TO_IDLE_SPOT;
                            setTarget(idlePoint);
                        }
                    }
                }
            }
            default -> { }
        }
    }

    @Override
    protected void onReturnToStorage() {

        // Cancel any promised deliveries
        var cargo = getCargo();

        if (cargo != null && cargo.getTarget() != null) {
            var building = cargo.getTarget();

            if (!building.isStorehouse()) {
                building.cancelPromisedDelivery(cargo);
            }
        }

        // Return to storage
        var storage = getPlayer().getClosestStorage(getPosition(), null);
        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            getPlayer().getBuildings().stream()
                    .filter(Building::isStorehouse)
                    .findFirst()
                    .ifPresent(building -> {
                        state = RETURNING_TO_STORAGE;
                        setOffroadTarget(building.getPosition());
                    });
        }
    }

    @Override
    public String toString() {
        return isExactlyAtPoint() ?
                (getCargo() == null ?
                        String.format("Courier for %s at %s", assignedRoad, getPosition()) :
                        String.format("Courier for %s at %s carrying %s", assignedRoad, getPosition(), getCargo().getMaterial())) :
                (getCargo() == null ?
                        String.format("Courier for %s walking %s - %s", assignedRoad, getPosition(), getNextPoint()) :
                        String.format("Courier for %s walking %s - %s carrying %s", assignedRoad, getPosition(), getNextPoint(), getCargo().getMaterial()));
    }

    private void deliverCargo() {

        // Deliver cargo
        var cargo = getCargo();
        var currentPosition = getPosition();
        var endPoint = getEndPointAtPoint(currentPosition);

        endPoint.putCargo(cargo);
        cargo.setPosition(currentPosition);

        lastCargo = cargo;

        setCargo(null);
    }

    private void pickUpCargoAndGoDeliver(Cargo cargoToPickUp) {
        var point = getPosition();
        var endPoint = map.getFlagAtPoint(point);

        // Pick up the right cargo if we have promised to do so
        if (intendedCargo != null) {
            setCargo(endPoint.retrieveCargo(intendedCargo));

            intendedCargo = null;
            getCargo().cancelPromisedPickUp();

        // Pick up the cargo where we stand if needed
        } else if (cargoToPickUp != null) {
            if (!point.equals(endPoint.getPosition())) {
                throw new InvalidGameLogicException(String.format("Not at %s", endPoint));
            }

            endPoint.retrieveCargo(cargoToPickUp);
            setCargo(cargoToPickUp);
        }

        // Deliver the cargo to the other flag or all the way to the building
        deliverToFlagOrBuilding(getCargo());
    }

    private void deliverToFlagOrBuilding(Cargo cargo) {
        var cargoTarget = cargo.getTarget().getPosition();

        if (cargoTarget.downRight().equals(assignedRoad.getStart()) ||
            cargoTarget.downRight().equals(assignedRoad.getEnd())) {
            state = GOING_TO_BUILDING_TO_DELIVER_CARGO;

            var roadPoints = assignedRoad.getWayPoints();
            var toWalk = new ArrayList<>(roadPoints);

            if (!roadPoints.getFirst().equals(getPosition())) {
                Collections.reverse(toWalk);
            }

            toWalk.add(cargoTarget);

            setTargetWithPath(toWalk);
        } else {
            state = GOING_TO_FLAG_TO_DELIVER_CARGO;

            if (assignedRoad.getWayPoints().getFirst().equals(getPosition())) {
                setTargetWithPath(assignedRoad.getWayPoints());
            } else {
                var toWalk = new LinkedList<>(assignedRoad.getWayPoints());

                Collections.reverse(toWalk);

                setTargetWithPath(toWalk);
            }
        }
    }

    private Point findIdlePointAtRoad(Road road) {
        var wayPoints = road.getWayPoints();

        return wayPoints.get(wayPoints.size() / 2);
    }

    public boolean isWalkingToIdlePoint() {
        return state == RETURNING_TO_IDLE_SPOT;
    }

    public boolean isWalkingToRoad() {
        return state == WALKING_TO_ROAD;
    }

    public boolean isIdle() {
        return state == IDLE_AT_ROAD ||
                state == IDLE_CHEWING_GUM ||
                state == IDLE_READING_PAPER ||
                state == IDLE_TOUCHING_NOSE ||
                state == IDLE_SITTING_DOWN ||
                state == IDLE_JUMPING_SKIP_ROPE;
    }

    private EndPoint getEndPointAtPoint(Point point) {
        var mapPoint = map.getMapPoint(point);
        return mapPoint.isFlag() ? mapPoint.getFlag() : mapPoint.getBuilding();
    }

    public Cargo findCargoToCarry(Flag flag) {
        var map = getMap();
        var point = flag.getPosition();
        var mapPoint = map.getMapPoint(point);

        // Look for the most prioritized cargo. Lower is better.
        int priority = Integer.MAX_VALUE;
        Cargo waitingCargo = null;

        var otherEndOfRoad = getAssignedRoad().getOtherEndPoint(flag);

        for (var cargo : flag.getStackedCargo()) {
            var target = cargo.getTarget();

            // Filter cargos where pickup is already planned
            if (cargo.isPickupPromised() && !Objects.equals(cargo, intendedCargo)) {
                continue;
            }

            // Filter cargos without a target
            if (target == null) {
                continue;
            }

            // Try to avoid picking up the cargo that the courier just dropped off
            if (cargo.equals(lastCargo)) {
                if (mapPoint.getConnectedRoads().size() > 1) {
                    List<Point> otherRoute = map.findDetailedWayWithExistingRoadsInFlagsAndBuildings(flag, target, otherEndOfRoad.getPosition());

                    if (otherRoute != null) {
                        continue;
                    }
                }
            }

            // Filter cargos that will not benefit from going through the courier's road
            var bestPath = map.findDetailedWayWithExistingRoadsInFlagsAndBuildings(flag, target);
            var pathThroughRoad = map.findDetailedWayWithExistingRoadsInFlagsAndBuildings(otherEndOfRoad, target, point);

            // Filter cargos where there is no road available
            if (bestPath == null) {
                continue;
            }

            // Filter cargos where going through the couriers road doesn't lead to the target
            if (pathThroughRoad == null) {
                continue;
            }

            // Let the best courier do the delivery if it's available
            var optimalRoad = map.getRoadAtPoint(bestPath.get(1));
            var courierForOptimalRoad = optimalRoad.getCourier();
            var donkeyForOptimalRoad = optimalRoad.getDonkey();

            if (!getAssignedRoad().equals(optimalRoad)) {
                if ((courierForOptimalRoad != null && courierForOptimalRoad.isIdle()) ||
                    (donkeyForOptimalRoad  != null && donkeyForOptimalRoad.isIdle())) {
                    continue;
                }
            }

            // Avoid roads that are more than double as long as the most optimal road
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

    public BodyType getBodyType() {
        return bodyType;
    }

    public boolean isChewingGum() {
        return state == IDLE_CHEWING_GUM;
    }

    public boolean isReadingPaper() {
        return state == IDLE_READING_PAPER;
    }

    public boolean isTouchingNose() {
        return state == IDLE_TOUCHING_NOSE;
    }

    public boolean isJumpingSkipRope() {
        return state == IDLE_JUMPING_SKIP_ROPE;
    }

    public boolean isSittingDown() {
        return state == IDLE_SITTING_DOWN;
    }

    public void returnToStorage(Building building) {
        setTarget(building.getPosition());
        state = RETURNING_TO_STORAGE;
    }

    public enum BodyType {
        THIN,
        FAT
    }
}
