package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;

public record NoMoreResourcesMessage(Building building) implements Message {

    @Override
    public MessageType getMessageType() {
        return MessageType.NO_MORE_RESOURCES;
    }

    @Override
    public String toString() {
        return "Message: No more resources in " + building.getClass().getSimpleName() + " at " + building.getPosition();
    }
}
