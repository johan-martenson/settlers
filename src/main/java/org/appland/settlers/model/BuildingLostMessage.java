package org.appland.settlers.model;

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
}
