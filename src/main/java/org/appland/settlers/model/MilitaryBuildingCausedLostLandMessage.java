package org.appland.settlers.model;

public class MilitaryBuildingCausedLostLandMessage implements Message {
    private final Building building;

    public MilitaryBuildingCausedLostLandMessage(Building building) {
        this.building = building;
    }

    public Building getBuilding() {
        return building;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.MILITARY_BUILDING_CAUSED_LOST_LAND;
    }
}
