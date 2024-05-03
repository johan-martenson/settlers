package org.appland.settlers.model.messages;

import org.appland.settlers.model.Material;
import org.appland.settlers.model.Point;

public record GeologistFindMessage(Point point, Material material) implements Message {

    @Override
    public MessageType getMessageType() {
        return MessageType.GEOLOGIST_FIND;
    }

    @Override
    public String toString() {
        return "Message: Geologist found " + material.name().toLowerCase() + " at " + point;
    }
}
