package org.appland.settlers.assets;

import java.awt.Dimension;
import java.awt.Point;

public class Area {
    final int x;
    final int y;
    final int width;
    final int height;

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
