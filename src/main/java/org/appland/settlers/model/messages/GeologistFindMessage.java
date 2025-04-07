package org.appland.settlers.model.messages;

import org.appland.settlers.model.Material;
import org.appland.settlers.model.Point;

import java.util.Objects;

public final class GeologistFindMessage extends Message {
    private final Point point;
    private final Material material;

    public GeologistFindMessage(Point point, Material material) {
        this.point = point;
        this.material = material;
    }

    public MessageType getMessageType() {
        return MessageType.GEOLOGIST_FIND;
    }

    @Override
    public String toString() {
        return "Message: Geologist found " + material.name().toLowerCase() + " at " + point;
    }

    public Point point() {
        return point;
    }

    public Material material() {
        return material;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GeologistFindMessage) obj;
        return Objects.equals(this.point, that.point) &&
                Objects.equals(this.material, that.material);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, material);
    }
}
