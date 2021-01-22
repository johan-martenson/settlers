package org.appland.settlers.model;

import java.util.Objects;

import static org.appland.settlers.model.Material.BUILDER;

@Walker(speed = 10)
public class Builder extends Worker {

    private static final int TIME_TO_HAMMER = 19;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown;

    private Building building;

    private enum State {
        GOING_TO_HAMMER, HAMMERING, WALKING_TO_FLAG_TO_GO_BACK_TO_STORAGE, RETURNING_TO_STORAGE, GOING_TO_DIE, DEAD, WALKING_TO_BUILDING_TO_CONSTRUCT
    }

    private State state;

    public Builder(Player player, GameMap map) {
        super(player, map);

        state = State.WALKING_TO_BUILDING_TO_CONSTRUCT;
        countdown = new Countdown();
    }

    @Override
    void onIdle() throws InvalidRouteException {

        if (building.isReady()) {
            if (map.findWayOffroad(getPosition(), building.getFlag().getPosition(), null) != null) {
                setOffroadTarget(building.getFlag().getPosition());

                state = State.WALKING_TO_FLAG_TO_GO_BACK_TO_STORAGE;
            } else {
                setDead();

                state = State.DEAD;

                countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
            }
        } else if (state == State.HAMMERING) {
            if (countdown.hasReachedZero()) {
                Point nextHammerPoint;
                Point buildingPoint = building.getPosition();
                Point position = getPosition();

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
        }
    }

    @Override
    void onArrival() throws InvalidRouteException {

        if (state == State.WALKING_TO_BUILDING_TO_CONSTRUCT) {
            building = map.getBuildingAtPoint(getPosition());

            if (building != null) {
                building.startConstruction();

                if (map.findWayOffroad(getPosition(), building.getPosition().downLeft(), null) != null) {
                    setOffroadTarget(building.getPosition().downLeft());
                    state = State.GOING_TO_HAMMER;
                } else {
                    countdown.countFrom(TIME_TO_HAMMER);

                    state = State.HAMMERING;
                }
            } else {
                returnToStorage();
            }
        } else if (state == State.GOING_TO_HAMMER) {

            if (!building.isReady()) {
                state = State.HAMMERING;

                countdown.countFrom(TIME_TO_HAMMER);
            } else {
                setOffroadTarget(building.getFlag().getPosition());

                state = State.WALKING_TO_FLAG_TO_GO_BACK_TO_STORAGE;
            }
        } else if (state == State.WALKING_TO_FLAG_TO_GO_BACK_TO_STORAGE) {

            /* Return to storage
            *
            * NOTE: don't set state here. State is set inside the method
            *
            *  */
            returnToStorage();
        } else if (state == State.RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        } else if (state == State.GOING_TO_DIE) {
            setDead();

            state = State.DEAD;

            countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
        }
    }

    @Override
    protected void onReturnToStorage() throws InvalidRouteException {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, BUILDER);

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), BUILDER);

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            } else {
                Point point = findPlaceToDie();

                if (!Objects.equals(getPosition(), point)) {
                    state = State.GOING_TO_DIE;

                    setOffroadTarget(point, getPosition());
                } else {
                    setDead();

                    state = State.DEAD;

                    countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
                }
            }
        }
    }

    public boolean isHammering() {
        return state == State.HAMMERING;
    }

    public String toString() {
        return "Builder for " + building;
    }
}
