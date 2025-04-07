package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;

import java.util.Objects;

public final class BuildingCapturedMessage extends Message {
    private final Building building;

    public BuildingCapturedMessage(Building building) {
        this.building = building;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.BUILDING_CAPTURED;
    }

    @Override
    public String toString() {
        return "Message: " + building.getClass().getSimpleName() + " " + building.getPosition() + " captured by enemy";
    }

    public Building building() {
        return building;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BuildingCapturedMessage) obj;
        return Objects.equals(this.building, that.building);
    }

    @Override
    public int hashCode() {
        return Objects.hash(building);
    }

}
