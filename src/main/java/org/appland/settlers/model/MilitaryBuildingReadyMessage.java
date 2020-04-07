package org.appland.settlers.model;

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
}
