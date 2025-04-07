package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Catapult;

import java.util.Objects;

public final class BombardedByCatapultMessage extends Message {
    private final Catapult catapult;
    private final Building building;

    public BombardedByCatapultMessage(Catapult catapult, Building building) {
        this.catapult = catapult;
        this.building = building;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.BOMBARDED_BY_CATAPULT;
    }

    @Override
    public String toString() {
        return "Message: " + building.getClass().getSimpleName() + " " + building.getPosition() + " hit by catapult " + catapult.getPosition();
    }

    public Catapult catapult() {
        return catapult;
    }

    public Building building() {
        return building;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BombardedByCatapultMessage) obj;
        return Objects.equals(this.catapult, that.catapult) &&
                Objects.equals(this.building, that.building);
    }

    @Override
    public int hashCode() {
        return Objects.hash(catapult, building);
    }

}
