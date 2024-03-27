package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;

public class BuildingLostMessage implements Message {
    private final Building building;

    public BuildingLostMessage(Building building) {
        this.building = building;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.BUILDING_LOST;
    }

    public Building getBuilding() {
        return building;
    }

    @Override
    public String toString() {
        return "Message: " + building.getClass().getSimpleName() + " " + building.getPosition() + " lost to enemy";
    }
}
