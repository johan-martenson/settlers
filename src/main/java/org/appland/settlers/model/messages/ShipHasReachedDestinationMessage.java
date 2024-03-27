package org.appland.settlers.model.messages;

import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Ship;

public class ShipHasReachedDestinationMessage implements Message {
    private final Ship ship;
    private final Point position;

    public ShipHasReachedDestinationMessage(Ship ship, Point point) {
        this.ship = ship;
        this.position = point;
    }

    @Override
    public MessageType getMessageType() {
        return Message.MessageType.SHIP_HAS_REACHED_DESTINATION;
    }

    public Ship getShip() {
        return ship;
    }

    public Point getPosition() {
        return position;
    }
}
