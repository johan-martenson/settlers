package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;

import java.util.Objects;

public final class UnderAttackMessage extends Message {
    private final Building building;

    public UnderAttackMessage(Building building) {
        this.building = building;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.UNDER_ATTACK;
    }

    @Override
    public String toString() {
        return "Message: " + building.getClass().getSimpleName() + " " + building.getPosition() + " is under attack";
    }

    public Building building() {
        return building;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (UnderAttackMessage) obj;
        return Objects.equals(this.building, that.building);
    }

    @Override
    public int hashCode() {
        return Objects.hash(building);
    }

}
