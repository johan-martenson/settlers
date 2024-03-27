package org.appland.settlers.model.messages;

import org.appland.settlers.model.actors.Ship;

public class ShipReadyForExpeditionMessage implements Message {

    private final Ship ship;

    public ShipReadyForExpeditionMessage(Ship ship) {
        this.ship = ship;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.SHIP_READY_FOR_EXPEDITION;
    }

    public Ship getShip() {
        return ship;
    }
}
