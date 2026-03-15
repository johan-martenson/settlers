package org.appland.settlers.test.maps;

import org.appland.settlers.maps.MapLoader;
import org.appland.settlers.maps.utils.GeometryMapping;
import org.appland.settlers.model.Point;
import org.junit.Test;

import java.awt.Dimension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MapLoaderTest {
    private final MapLoader mapLoader = new MapLoader();

    @Test
    public void testEvenHeightEvenY() {
        var dimension = new Dimension(100, 50); // Even height
        java.awt.Point mapFilePosition = new java.awt.Point(10, 20); // Even y-coordinate

        var result = GeometryMapping.mapFilePointToGamePoint(mapFilePosition, dimension.height);

        assertEquals(new Point(20, 30), result);
    }

    @Test
    public void testEvenHeightOddY() {
        var dimension = new Dimension(100, 50); // Even height
        java.awt.Point mapFilePosition = new java.awt.Point(10, 21); // Odd y-coordinate

        var result = GeometryMapping.mapFilePointToGamePoint(mapFilePosition, dimension.height);

        assertEquals(new Point(21, 29), result);
    }

    @Test
    public void testOddHeightEvenY() {
        var dimension = new Dimension(100, 51); // Odd height
        java.awt.Point mapFilePosition = new java.awt.Point(10, 20); // Even y-coordinate

        var result = GeometryMapping.mapFilePointToGamePoint(mapFilePosition, dimension.height);

        assertEquals(new Point(20, 31), result);
    }

    @Test
    public void testOddHeightOddY() {
        var dimension = new Dimension(100, 51); // Odd height
        java.awt.Point mapFilePosition = new java.awt.Point(10, 21); // Odd y-coordinate

        var result = GeometryMapping.mapFilePointToGamePoint(mapFilePosition, dimension.height);

        assertEquals(new Point(21, 30), result);
    }

    @Test
    public void testEdgeCaseZeroZero() {
        var dimension = new Dimension(100, 50); // Even height
        java.awt.Point mapFilePosition = new java.awt.Point(0, 0);

        var result = GeometryMapping.mapFilePointToGamePoint(mapFilePosition, dimension.height);

        assertEquals(new Point(0, 50), result);
    }

    @Test
    public void testEdgeCaseMaxValues() {
        var dimension = new Dimension(100, 50); // Even height
        java.awt.Point mapFilePosition = new java.awt.Point(Integer.MAX_VALUE, Integer.MAX_VALUE);

        var result = GeometryMapping.mapFilePointToGamePoint(mapFilePosition, dimension.height);

        // The expected result would depend on the actual calculations,
        // but here we are ensuring the method handles large input values without crashing.
        // So we do not assert the exact result but ensure it doesn't throw an exception.
        assertNotNull(result);
    }

    @Test
    public void testEdgeCaseNegativeValues() {
        var dimension = new Dimension(100, 50); // Even height
        java.awt.Point mapFilePosition = new java.awt.Point(-10, -10);

        var result = GeometryMapping.mapFilePointToGamePoint(mapFilePosition, dimension.height);

        // Ensure the method handles negative inputs appropriately
        assertEquals(new Point(-20, 60), result);
    }
}
