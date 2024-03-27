package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;

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

    @Override
    public String toString() {
        return "Message: No more resources in " + building.getClass().getSimpleName() + " at " + building.getPosition();
    }
}
