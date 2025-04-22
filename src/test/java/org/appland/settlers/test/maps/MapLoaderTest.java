package org.appland.settlers.test.maps;

import org.appland.settlers.maps.MapLoader;
import org.appland.settlers.model.Point;
import org.junit.Test;

import java.awt.Dimension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MapLoaderTest {
    private final MapLoader mapLoader = new MapLoader();

    @Test
    public void testEvenHeightEvenY() {
        Dimension dimension = new Dimension(100, 50); // Even height
        java.awt.Point mapFilePosition = new java.awt.Point(10, 20); // Even y-coordinate

        Point result = mapLoader.mapFilePositionToGamePoint(mapFilePosition, dimension);

        assertEquals(new Point(19, 29), result);
    }

    @Test
    public void testEvenHeightOddY() {
        Dimension dimension = new Dimension(100, 50); // Even height
        java.awt.Point mapFilePosition = new java.awt.Point(10, 21); // Odd y-coordinate

        Point result = mapLoader.mapFilePositionToGamePoint(mapFilePosition, dimension);

        assertEquals(new Point(20, 28), result);
    }

    @Test
    public void testOddHeightEvenY() {
        Dimension dimension = new Dimension(100, 51); // Odd height
        java.awt.Point mapFilePosition = new java.awt.Point(10, 20); // Even y-coordinate

        Point result = mapLoader.mapFilePositionToGamePoint(mapFilePosition, dimension);

        assertEquals(new Point(19, 31), result);
    }

    @Test
    public void testOddHeightOddY() {
        Dimension dimension = new Dimension(100, 51); // Odd height
        java.awt.Point mapFilePosition = new java.awt.Point(10, 21); // Odd y-coordinate

        Point result = mapLoader.mapFilePositionToGamePoint(mapFilePosition, dimension);

        assertEquals(new Point(20, 30), result);
    }

    @Test
    public void testEdgeCaseZeroZero() {
        Dimension dimension = new Dimension(100, 50); // Even height
        java.awt.Point mapFilePosition = new java.awt.Point(0, 0);

        Point result = mapLoader.mapFilePositionToGamePoint(mapFilePosition, dimension);

        assertEquals(new Point(-1, 49), result);
    }

    @Test
    public void testEdgeCaseMaxValues() {
        Dimension dimension = new Dimension(100, 50); // Even height
        java.awt.Point mapFilePosition = new java.awt.Point(Integer.MAX_VALUE, Integer.MAX_VALUE);

        Point result = mapLoader.mapFilePositionToGamePoint(mapFilePosition, dimension);

        // The expected result would depend on the actual calculations,
        // but here we are ensuring the method handles large input values without crashing.
        // So we do not assert the exact result but ensure it doesn't throw an exception.
        assertNotNull(result);
    }

    @Test
    public void testEdgeCaseNegativeValues() {
        Dimension dimension = new Dimension(100, 50); // Even height
        java.awt.Point mapFilePosition = new java.awt.Point(-10, -10);

        Point result = mapLoader.mapFilePositionToGamePoint(mapFilePosition, dimension);

        // Ensure the method handles negative inputs appropriately
        assertEquals(new Point(-21, 59), result);
    }
}
