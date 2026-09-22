package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.WorkerAction;

import java.util.Objects;

import static org.appland.settlers.model.Material.BUILDER;
import static org.appland.settlers.model.Material.HAMMER;

@Walker(speed = 10)
public class Builder extends Worker {
    private static final int TIME_TO_HAMMER = 19;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown = new Countdown();

    private Building building;
    public State state = State.WALKING_TO_BUILDING_TO_CONSTRUCT;

    private enum State {
        GOING_TO_HAMMER,
        HAMMERING,
        WALKING_TO_FLAG_TO_GO_BACK_TO_STORAGE,
        RETURNING_TO_STORAGE,
        GOING_TO_DIE,
        DEAD,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        WALKING_TO_FINISHED_STOREHOUSE, WALKING_TO_BUILDING_TO_CONSTRUCT
    }

    public Builder(Player player, GameMap map) {
        super(player, map);
    }

    @Override
    protected void onIdle() {
        if ((state == State.HAMMERING || state == State.GOING_TO_HAMMER) && building.isReady()) {
            if (map.findWayOffroad(position, building.getFlag().getPosition(), null) != null) {
                if (building instanceof Storehouse) {
                    setOffroadTarget(building.getPosition(), building.getFlag().getPosition());
                    state = State.WALKING_TO_FINISHED_STOREHOUSE;
                } else {
                    setOffroadTarget(building.getFlag().getPosition());
                    state = State.WALKING_TO_FLAG_TO_GO_BACK_TO_STORAGE;
                }
            } else {
                setDead();

                state = State.DEAD;
                countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
            }
        } else if (state == State.HAMMERING) {
            if (countdown.hasReachedZero()) {
                Point nextHammerPoint;
                var buildingPoint = building.getPosition();

                if (position.equals(buildingPoint.downLeft())) {
                    nextHammerPoint = buildingPoint.downLeft().left();
                } else if (position.equals(buildingPoint.downLeft().left())) {
                    nextHammerPoint = buildingPoint.upRight();
                } else {
                    nextHammerPoint = buildingPoint.downLeft();
                }

                // TODO: 1) avoid expensive way finding, 2) choose other point if the selected one can't be reached

                if (map.findWayOffroad(position, nextHammerPoint, null) != null) {
                    setOffroadTarget(nextHammerPoint);
                    state = State.GOING_TO_HAMMER;
                } else {
                    countdown.countFrom(TIME_TO_HAMMER);
                }
            } else {
                countdown.step();
            }
        } else if (state == State.DEAD) {
            if (countdown.hasReachedZero()) {
                map.removeWorker(this);
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() {
        switch (state) {
            case WALKING_TO_FINISHED_STOREHOUSE -> {
                if (building.isReady()) {
                    var storehouse = map.getBuildingAtPoint(position);
                    var storehouseWorker = new StorehouseWorker(player, map);
                    map.placeWorkerFromStepTime(storehouseWorker, storehouse);
                    storehouseWorker.enterBuilding(building);
                    building.assignWorker(storehouseWorker);
                    storehouse.promiseDelivery(HAMMER);
                    storehouse.putCargo(new Cargo(HAMMER, map));
                    map.removeWorker(this);
                } else {
                    returnToStorage();
                }
            }

            case WALKING_TO_BUILDING_TO_CONSTRUCT -> {
                building = map.getBuildingAtPoint(position);

                if (building != null) {
                    building.startConstruction();

                    if (map.findWayOffroad(position, building.getPosition().downLeft(), null) != null) {
                        setOffroadTarget(building.getPosition().downLeft());
                        state = State.GOING_TO_HAMMER;
                    } else {
                        countdown.countFrom(TIME_TO_HAMMER);
                        doAction(WorkerAction.HAMMERING_HOUSE_HIGH_AND_LOW);
                        state = State.HAMMERING;
                    }
                } else {
                    returnToStorage();
                }
            }
            case GOING_TO_HAMMER -> {
                if (!building.isReady()) {
                    state = State.HAMMERING;
                    doAction(WorkerAction.HAMMERING_HOUSE_HIGH_AND_LOW);
                    countdown.countFrom(TIME_TO_HAMMER);
                } else if (building instanceof Storehouse) {
                    state = State.WALKING_TO_FINISHED_STOREHOUSE;
                    setOffroadTarget(building.getPosition(), building.getPosition().downLeft());
                } else {
                    setOffroadTarget(building.getFlag().getPosition());
                    state = State.WALKING_TO_FLAG_TO_GO_BACK_TO_STORAGE;
                }
            }
            case WALKING_TO_FLAG_TO_GO_BACK_TO_STORAGE -> returnToStorage();
            case RETURNING_TO_STORAGE -> {
                var storehouse = (Storehouse) map.getBuildingAtPoint(position);
                storehouse.depositWorker(this);
            }
            case GOING_TO_DIE -> {
                setDead();
                state = State.DEAD;
                countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
            }
            case GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE -> {
                var storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, BUILDER);

                if (storehouse != null) {
                    state = State.RETURNING_TO_STORAGE;
                    setTarget(storehouse.getPosition());
                } else {
                    state = State.GOING_TO_DIE;
                    var point = findPlaceToDie();
                    setOffroadTarget(point);
                }
            }
            default -> {}
        }
    }

    @Override
    protected void onReturnToStorage() {

        // Wait until next flag to find out that the building is gone and then go back
        if (state == State.WALKING_TO_BUILDING_TO_CONSTRUCT &&
            (!isExactlyAtPoint() || !map.isFlagAtPoint(position))) {
            return;
        }

        var storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(position, null, map, BUILDER);

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;
            setTarget(storage.getPosition());
        } else {
            storage = (Storehouse) GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(position, null, player, BUILDER);

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;
                setOffroadTarget(storage.getPosition());
            } else {
                var point = findPlaceToDie();

                if (!Objects.equals(position, point)) {
                    state = State.GOING_TO_DIE;
                    setOffroadTarget(point, position);
                } else {
                    setDead();
                    state = State.DEAD;
                    countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
                }
            }
        }
    }

    /**
     * Determines if the builder is hammering.
     *
     * @return true if hammering, false otherwise.
     */
    public boolean isHammering() {
        return state == State.HAMMERING;
    }

    @Override
    public String toString() {
        return String.format("Builder for %s", building);
    }

    /**
     * Sends the builder to another storage.
     *
     * @param building The building to send the builder to.
     */
    @Override
    public void goToOtherStorage(Building building) {
        state = State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;
        setTarget(building.getFlag().getPosition());
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {

        // Return to storage if the planned path no longer exists
        switch (state) {
            case WALKING_TO_BUILDING_TO_CONSTRUCT -> {
                if (map.isFlagAtPoint(position) && !map.arePointsConnectedByRoads(position, target)) {
                    getTargetBuilding().cancelPromisedBuilder(this);
                    clearTargetBuilding();
                    returnToStorage();
                }
            }

            case WALKING_TO_FINISHED_STOREHOUSE -> {
                if ((position.equals(target.downRight()) && !map.isFlagAtPoint(position)) ||
                    (map.isFlagAtPoint(position) && !map.arePointsConnectedByRoads(position, target))) {
                    clearTargetBuilding();
                    returnToStorage();
                }
            }
        }
    }
}
