package org.appland.settlers.model.messages;

import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Ship;

import java.util.Objects;

public final class ShipHasReachedDestinationMessage extends Message {
    private final Ship ship;
    private final Point position;

    public ShipHasReachedDestinationMessage(Ship ship, Point position) {
        this.ship = ship;
        this.position = position;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.SHIP_HAS_REACHED_DESTINATION;
    }

    @Override
    public String toString() {
        return "Message: ship at " + position + " has reached its destination";
    }

    public Ship ship() {
        return ship;
    }

    public Point position() {
        return position;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ShipHasReachedDestinationMessage) obj;
        return Objects.equals(this.ship, that.ship) &&
                Objects.equals(this.position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ship, position);
    }

}
