package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;

public record MilitaryBuildingOccupiedMessage(Building building) implements Message {

    @Override
    public MessageType getMessageType() {
        return MessageType.MILITARY_BUILDING_OCCUPIED;
    }

    @Override
    public String toString() {
        return "Message: " + building.getClass().getSimpleName() + " " + building.getPosition() + " is occupied";
    }
}
