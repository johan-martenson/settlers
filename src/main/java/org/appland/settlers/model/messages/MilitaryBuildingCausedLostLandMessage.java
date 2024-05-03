package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;

public record MilitaryBuildingCausedLostLandMessage(Building building) implements Message {

    @Override
    public MessageType getMessageType() {
        return MessageType.MILITARY_BUILDING_CAUSED_LOST_LAND;
    }

    @Override
    public String toString() {
        return "Message: " + building.getClass().getSimpleName() + " " + building.getPosition() + " has caused lost land";
    }
}
