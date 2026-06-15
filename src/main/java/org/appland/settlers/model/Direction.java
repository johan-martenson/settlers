package org.appland.settlers.model;

import org.appland.settlers.assets.CompassDirection;

public enum Direction {
    UP,
    DOWN,
    RIGHT,
    LEFT,
    UP_LEFT,
    UP_RIGHT,
    DOWN_LEFT,
    DOWN_RIGHT;

    public CompassDirection toCompassDirection() {
        return switch (this) {
            case UP_LEFT -> CompassDirection.NORTH_WEST;
            case UP_RIGHT -> CompassDirection.NORTH_EAST;
            case RIGHT -> CompassDirection.EAST;
            case DOWN_RIGHT ->  CompassDirection.SOUTH_EAST;
            case DOWN_LEFT ->  CompassDirection.SOUTH_WEST;
            case LEFT -> CompassDirection.WEST;
            default -> throw new InvalidGameLogicException("Invalid direction: " + this);
        };
    }
}
