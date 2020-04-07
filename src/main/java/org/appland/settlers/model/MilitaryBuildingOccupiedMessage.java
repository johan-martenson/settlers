package org.appland.settlers.model;

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
}
