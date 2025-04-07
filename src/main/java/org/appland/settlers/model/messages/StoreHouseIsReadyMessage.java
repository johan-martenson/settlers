package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;

import java.util.Objects;

public final class StoreHouseIsReadyMessage extends Message {
    private final Building building;

    public StoreHouseIsReadyMessage(Building building) {
        this.building = building;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.STORE_HOUSE_IS_READY;
    }

    @Override
    public String toString() {
        return "Message: Storehouse " + building.getPosition() + " is ready";
    }

    public Building building() {
        return building;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (StoreHouseIsReadyMessage) obj;
        return Objects.equals(this.building, that.building);
    }

    @Override
    public int hashCode() {
        return Objects.hash(building);
    }

}
