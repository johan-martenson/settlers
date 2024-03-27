package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;

public class BuildingCapturedMessage implements Message {
    private final Building building;

    public BuildingCapturedMessage(Building building) {
        this.building = building;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.BUILDING_CAPTURED;
    }

    public Building getBuilding() {
        return building;
    }

    @Override
    public String toString() {
        return "Message: " + building.getClass().getSimpleName() + " " + building.getPosition() + " captured by enemy";
    }
}
