package org.appland.settlers.model;

import org.appland.settlers.model.buildings.Building;

public class MilitaryBuildingReadyMessage implements Message {
    private final Building building;

    public MilitaryBuildingReadyMessage(Building building) {
        this.building = building;
    }

    public Building getBuilding() {
        return building;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.MILITARY_BUILDING_READY;
    }

    @Override
    public String toString() {
        return "Message: " + building.getClass().getSimpleName() + " " + building.getPosition() + " is ready";
    }
}
