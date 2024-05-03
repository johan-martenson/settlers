package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;

public record StoreHouseIsReadyMessage(Building building) implements Message {

    @Override
    public MessageType getMessageType() {
        return MessageType.STORE_HOUSE_IS_READY;
    }

    @Override
    public String toString() {
        return "Message: Storehouse " + building.getPosition() + " is ready";
    }
}
