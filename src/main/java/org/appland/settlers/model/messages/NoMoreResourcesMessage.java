package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;

import java.util.Objects;

public final class NoMoreResourcesMessage extends Message {
    private final Building building;

    public NoMoreResourcesMessage(Building building) {
        this.building = building;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.NO_MORE_RESOURCES;
    }

    @Override
    public String toString() {
        return "Message: No more resources in " + building.getClass().getSimpleName() + " at " + building.getPosition();
    }

    public Building building() {
        return building;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (NoMoreResourcesMessage) obj;
        return Objects.equals(this.building, that.building);
    }

    @Override
    public int hashCode() {
        return Objects.hash(building);
    }

}
