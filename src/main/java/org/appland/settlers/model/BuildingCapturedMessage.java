package org.appland.settlers.model;

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
}
