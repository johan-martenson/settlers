package org.appland.settlers.assets;

import java.awt.Dimension;
import java.awt.Point;

/**
 * Represents a rectangular area with its upper-left corner coordinates and dimensions.
 */
public record Area(int x, int y, int width, int height) {

    /**
     * Returns the dimensions of the area as a {@link Dimension} object.
     *
     * @return A {@link Dimension} object representing the width and height of the area.
     */
    public Dimension getDimension() {
        return new Dimension(width, height);
    }

    /**
     * Returns the upper-left coordinate of the area as a {@link Point}.
     *
     * @return A {@link Point} representing the upper-left corner (x, y) of the area.
     */
    public Point getUpperLeftCoordinate() {
        return new Point(x, y);
    }
}
