package org.appland.settlers.model.messages;

import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Ship;

public record ShipHasReachedDestinationMessage(Ship ship, Point position) implements Message {

    @Override
    public MessageType getMessageType() {
        return Message.MessageType.SHIP_HAS_REACHED_DESTINATION;
    }

    @Override
    public String toString() {
        return "Message: ship at " + position + " has reached its destination";
    }
}
