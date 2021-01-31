package org.appland.settlers.model;

import java.util.List;

public class Ship extends Worker {
    private static final int TIME_TO_BUILD_SHIP = 100;
    private final Countdown countdown;

    private State state;

    private enum State {
        WAITING_FOR_MISSION, UNDER_CONSTRUCTION
    }

    Ship(Player player, GameMap map) {
        super(player, map);

        state = State.UNDER_CONSTRUCTION;

        countdown = new Countdown();

        countdown.countFrom(TIME_TO_BUILD_SHIP);
    }

    @Override
    void onIdle() {
        if (state == State.UNDER_CONSTRUCTION) {
            if (countdown.hasReachedZero()) {

                /* Find a point for the finished ship to move to */
                Point pointForFinishedShip = null;

                for (Point point : GameUtils.getHexagonAreaAroundPoint(getPosition(), 5, map)) {

                    List<DetailedVegetation> surroundingVegetation = map.getSurroundingTiles(point);

                    /* Filter points not connected to land */
                    if (!GameUtils.areAnyOneOf(surroundingVegetation, DetailedVegetation.CAN_WALK_ON)) {
                        continue;
                    }

                    /* Filter points not connected to water where the ship can sail */
                    if (!GameUtils.isAny(surroundingVegetation, DetailedVegetation.WATER_2)) {
                        continue;
                    }

                    pointForFinishedShip = point;

                    break;
                }

                if (pointForFinishedShip != null) {
                    setPosition(pointForFinishedShip);

                    state = State.WAITING_FOR_MISSION;
                }
            } else {
                countdown.step();
            }
        }
    }

    public boolean isUnderConstruction() {
        return state == State.UNDER_CONSTRUCTION;
    }

    public boolean isReady() {
        return state == State.WAITING_FOR_MISSION;
    }
}
