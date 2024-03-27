package org.appland.settlers.model;

import org.appland.settlers.model.buildings.Building;

public class UnderAttackMessage implements Message {
    private final Building building;

    public UnderAttackMessage(Building building) {
        this.building = building;
    }

    public Building getBuilding() {
        return building;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.UNDER_ATTACK;
    }

    @Override
    public String toString() {
        return "Message: " + building.getClass().getSimpleName() + " " + building.getPosition() + " is under attack";
    }
}
