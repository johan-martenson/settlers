package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;

public class StoreHouseIsReadyMessage implements Message {
    private final Building building;

    public StoreHouseIsReadyMessage(Building building) {
        this.building = building;
    }

    public Building getBuilding() {
        return building;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.STORE_HOUSE_IS_READY;
    }

    @Override
    public String toString() {
        return "Message: Storehouse " + building.getPosition() + " is ready";
    }
}
