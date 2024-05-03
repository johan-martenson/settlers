package org.appland.settlers.model.messages;

import org.appland.settlers.model.actors.Ship;

public record ShipReadyForExpeditionMessage(Ship ship) implements Message {

    @Override
    public MessageType getMessageType() {
        return MessageType.SHIP_READY_FOR_EXPEDITION;
    }
}
