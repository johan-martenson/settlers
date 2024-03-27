package org.appland.settlers.model.messages;

import org.appland.settlers.model.Material;
import org.appland.settlers.model.Point;

public class GeologistFindMessage implements Message {
    private final Point point;
    private final Material material;

    public GeologistFindMessage(Point point, Material material) {
        this.point = point;
        this.material = material;
    }

    public Point getPoint() {
        return point;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.GEOLOGIST_FIND;
    }

    @Override
    public String toString() {
        return "Message: Geologist found " + material.name().toLowerCase() + " at " + point;
    }
}
