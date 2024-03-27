package org.appland.settlers.model;

import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Catapult;

public class BombardedByCatapultMessage implements Message {
    private final Catapult catapult;
    private final Building hitBuilding;

    public BombardedByCatapultMessage(Catapult catapult, Building hitBuilding) {
        this.catapult = catapult;
        this.hitBuilding = hitBuilding;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.BOMBARDED_BY_CATAPULT;
    }

    public Catapult getCatapult() {
        return catapult;
    }

    public Building getHitBuilding() {
        return hitBuilding;
    }

    @Override
    public String toString() {
        return "Message: " + hitBuilding.getClass().getSimpleName() + " " + hitBuilding.getPosition() + " hit by catapult " + catapult.getPosition();
    }
}
