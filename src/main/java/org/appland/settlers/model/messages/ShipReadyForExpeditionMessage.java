package org.appland.settlers.model.messages;

import org.appland.settlers.model.actors.Ship;

import java.util.Objects;

public final class ShipReadyForExpeditionMessage extends Message {
    private final Ship ship;

    public ShipReadyForExpeditionMessage(Ship ship) {
        this.ship = ship;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.SHIP_READY_FOR_EXPEDITION;
    }

    public Ship ship() {
        return ship;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ShipReadyForExpeditionMessage) obj;
        return Objects.equals(this.ship, that.ship);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ship);
    }

    @Override
    public String toString() {
        return "ShipReadyForExpeditionMessage[" +
                "ship=" + ship + ']';
    }

}
