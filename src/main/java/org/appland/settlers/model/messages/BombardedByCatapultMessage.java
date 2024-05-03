package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Catapult;

public record BombardedByCatapultMessage(Catapult catapult, Building hitBuilding) implements Message {

    @Override
    public MessageType getMessageType() {
        return MessageType.BOMBARDED_BY_CATAPULT;
    }

    @Override
    public String toString() {
        return "Message: " + hitBuilding.getClass().getSimpleName() + " " + hitBuilding.getPosition() + " hit by catapult " + catapult.getPosition();
    }
}
