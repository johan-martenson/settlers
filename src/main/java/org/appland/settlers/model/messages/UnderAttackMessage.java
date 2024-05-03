package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;

public record UnderAttackMessage(Building building) implements Message {

    @Override
    public MessageType getMessageType() {
        return MessageType.UNDER_ATTACK;
    }

    @Override
    public String toString() {
        return "Message: " + building.getClass().getSimpleName() + " " + building.getPosition() + " is under attack";
    }
}
