package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;

public record BuildingCapturedMessage(Building building) implements Message {

    @Override
    public MessageType getMessageType() {
        return MessageType.BUILDING_CAPTURED;
    }

    @Override
    public String toString() {
        return "Message: " + building.getClass().getSimpleName() + " " + building.getPosition() + " captured by enemy";
    }
}
