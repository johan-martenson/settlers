package org.appland.settlers.model;

public class NoMoreResourcesMessage implements Message {
    private final Building building;

    public NoMoreResourcesMessage(Building building) {
        this.building = building;
    }

    public Building getBuilding() {
        return building;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.NO_MORE_RESOURCES;
    }
}
