package org.appland.settlers.assets;

import java.awt.Dimension;
import java.awt.Point;

public class Area {
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public Area(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Dimension getDimension() {
        return new Dimension(width, height);
    }

    public Point getUpperLeftCoordinate() {
        return new Point(x, y);
    }
}
