package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;

public record BuildingLostMessage(Building building) implements Message {

    @Override
    public MessageType getMessageType() {
        return MessageType.BUILDING_LOST;
    }

    @Override
    public String toString() {
        return "Message: " + building.getClass().getSimpleName() + " " + building.getPosition() + " lost to enemy";
    }
}
