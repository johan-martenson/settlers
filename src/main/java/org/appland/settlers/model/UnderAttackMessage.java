package org.appland.settlers.model;

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
}
