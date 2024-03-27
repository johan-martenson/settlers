package org.appland.settlers.model;

import org.appland.settlers.model.buildings.Building;

public class MilitaryBuildingOccupiedMessage implements Message {
    private final Building building;

    public MilitaryBuildingOccupiedMessage(Building building) {
        this.building = building;
    }

    public Building getBuilding() {
        return building;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.MILITARY_BUILDING_OCCUPIED;
    }

    @Override
    public String toString() {
        return "Message: " + building.getClass().getSimpleName() + " " + building.getPosition() + " is occupied";
    }
}
