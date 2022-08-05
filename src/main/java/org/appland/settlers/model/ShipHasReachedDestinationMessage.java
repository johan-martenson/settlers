package org.appland.settlers.model;

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
