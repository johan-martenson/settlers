package org.appland.settlers.test.maps;

import org.appland.settlers.maps.utils.GeometryMapping;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Vegetation;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

import static org.appland.settlers.assets.Nation.ROMANS;
import static org.appland.settlers.model.PlayerColor.BLUE;
import static org.appland.settlers.model.PlayerType.HUMAN;
import static org.appland.settlers.model.Vegetation.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests GeometryMapping without globalOffset.
 *
 * Mapping:
 *   engineX = 2 * fileX + (fileY % 2)
 *   engineY = fileHeight - 1 - fileY
 *
 * Dimensions:
 *   engineWidth  = 2 * fileWidth
 *   engineHeight = fileHeight
 */
public class TestGeometryMapping {
    private static final Vegetation A = Vegetation.LAVA_1;
    private static final Vegetation B = Vegetation.LAVA_2;

    private Vegetation[] createTestArray(int fileWidth, int fileHeight) {
        var arr = new Vegetation[fileWidth * fileHeight];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = (i % 2 == 0) ? A : B;
        }

        return arr;
    }

    // ============================================================
    // 1️⃣ Forward mapping bounds tests
    // ============================================================

    @Test
    public void testMapFilePointToGamePointIsWithinMapForEvenHeightFile() {
        int fileWidth = 4;
        int fileHeight = 6;

        int engineWidth = GeometryMapping.gameWidthFromFileWidth(fileWidth);
        int engineHeight = GeometryMapping.gameHeightFromFileHeight(fileHeight);

        for (int fileY = 0; fileY < fileHeight; fileY++) {
            for (int fileX = 0; fileX < fileWidth; fileX++) {
                var engine = GeometryMapping.mapFilePointToGamePoint(fileX, fileY, fileHeight);

                assertTrue(engine.x >= 0);
                assertTrue(engine.x < engineWidth);

                assertTrue(engine.y >= 0);
                assertTrue(engine.y < engineHeight);
            }
        }
    }

    @Test
    public void testGameMapHasCorrectPointsForOddHeightMap() throws InvalidUserActionException {
        int fileWidth = 5;
        int fileHeight = 5;

        var tilesBelow = new Vegetation[] {WATER, MOUNTAIN_1, DESERT_1, DESERT_2, BUILDABLE_MOUNTAIN,
                WATER, MOUNTAIN_1, DESERT_1, DESERT_2, BUILDABLE_MOUNTAIN,
                WATER, MOUNTAIN_1, DESERT_1, DESERT_2, BUILDABLE_MOUNTAIN,
                WATER, MOUNTAIN_1, DESERT_1, DESERT_2, BUILDABLE_MOUNTAIN,
                WATER, MOUNTAIN_1, DESERT_1, DESERT_2, BUILDABLE_MOUNTAIN};
        var tilesDownRight = new Vegetation[] {LAVA_1, LAVA_2, LAVA_3, LAVA_4, DESERT_2,
                LAVA_1, LAVA_2, LAVA_3, LAVA_4, DESERT_2,
                LAVA_1, LAVA_2, LAVA_3, LAVA_4, DESERT_2,
                LAVA_1, LAVA_2, LAVA_3, LAVA_4, DESERT_2,
                LAVA_1, LAVA_2, LAVA_3, LAVA_4, DESERT_2};

        var map = new GameMap(
                List.of(new Player("P0", BLUE, ROMANS, HUMAN)),
                GeometryMapping.gameWidthFromFileWidth(fileWidth),
                GeometryMapping.gameHeightFromFileHeight(fileHeight));

        GeometryMapping.layoutTilesInGameMap(fileWidth, fileHeight, tilesBelow, tilesDownRight, map);

        assertEquals(map.getPointsInMap().size(), 30);

        assertTrue(map.getPointsInMap().contains(new Point(0, 5)));
        assertTrue(map.getPointsInMap().contains(new Point(2, 5)));
        assertTrue(map.getPointsInMap().contains(new Point(4, 5)));
        assertTrue(map.getPointsInMap().contains(new Point(6, 5)));
        assertTrue(map.getPointsInMap().contains(new Point(8, 5)));

        assertTrue(map.getPointsInMap().contains(new Point(1, 4)));
        assertTrue(map.getPointsInMap().contains(new Point(3, 4)));
        assertTrue(map.getPointsInMap().contains(new Point(5, 4)));
        assertTrue(map.getPointsInMap().contains(new Point(7, 4)));
        assertTrue(map.getPointsInMap().contains(new Point(9, 4)));

        assertTrue(map.getPointsInMap().contains(new Point(0, 3)));
        assertTrue(map.getPointsInMap().contains(new Point(2, 3)));
        assertTrue(map.getPointsInMap().contains(new Point(4, 3)));
        assertTrue(map.getPointsInMap().contains(new Point(6, 3)));
        assertTrue(map.getPointsInMap().contains(new Point(8, 3)));

        assertTrue(map.getPointsInMap().contains(new Point(1, 2)));
        assertTrue(map.getPointsInMap().contains(new Point(3, 2)));
        assertTrue(map.getPointsInMap().contains(new Point(5, 2)));
        assertTrue(map.getPointsInMap().contains(new Point(7, 2)));
        assertTrue(map.getPointsInMap().contains(new Point(9, 2)));

        assertTrue(map.getPointsInMap().contains(new Point(0, 1)));
        assertTrue(map.getPointsInMap().contains(new Point(2, 1)));
        assertTrue(map.getPointsInMap().contains(new Point(4, 1)));
        assertTrue(map.getPointsInMap().contains(new Point(6, 1)));
        assertTrue(map.getPointsInMap().contains(new Point(8, 1)));

        assertTrue(map.getPointsInMap().contains(new Point(1, 0)));
        assertTrue(map.getPointsInMap().contains(new Point(3, 0)));
        assertTrue(map.getPointsInMap().contains(new Point(5, 0)));
        assertTrue(map.getPointsInMap().contains(new Point(7, 0)));
        assertTrue(map.getPointsInMap().contains(new Point(9, 0)));
    }

    @Test
    public void testGameMapHasCorrectPointsForEvenHeightMap() throws InvalidUserActionException {
        int fileWidth = 5;
        int fileHeight = 6;

        var tilesBelow = new Vegetation[] {WATER, MOUNTAIN_1, DESERT_1, DESERT_2, BUILDABLE_MOUNTAIN,
                WATER, MOUNTAIN_1, DESERT_1, DESERT_2, BUILDABLE_MOUNTAIN,
                WATER, MOUNTAIN_1, DESERT_1, DESERT_2, BUILDABLE_MOUNTAIN,
                WATER, MOUNTAIN_1, DESERT_1, DESERT_2, BUILDABLE_MOUNTAIN,
                WATER, MOUNTAIN_1, DESERT_1, DESERT_2, BUILDABLE_MOUNTAIN,
                WATER, MOUNTAIN_1, DESERT_1, DESERT_2, BUILDABLE_MOUNTAIN};
        var tilesDownRight = new Vegetation[] {LAVA_1, LAVA_2, LAVA_3, LAVA_4, DESERT_2,
                LAVA_1, LAVA_2, LAVA_3, LAVA_4, DESERT_2,
                LAVA_1, LAVA_2, LAVA_3, LAVA_4, DESERT_2,
                LAVA_1, LAVA_2, LAVA_3, LAVA_4, DESERT_2,
                LAVA_1, LAVA_2, LAVA_3, LAVA_4, DESERT_2,
                LAVA_1, LAVA_2, LAVA_3, LAVA_4, DESERT_2};

        var map = new GameMap(
                List.of(new Player("P0", BLUE, ROMANS, HUMAN)),
                GeometryMapping.gameWidthFromFileWidth(fileWidth),
                GeometryMapping.gameHeightFromFileHeight(fileHeight));

        GeometryMapping.layoutTilesInGameMap(fileWidth, fileHeight, tilesBelow, tilesDownRight, map);

        assertEquals(map.getPointsInMap().size(), 35);

        assertTrue(map.getPointsInMap().contains(new Point(0, 6)));
        assertTrue(map.getPointsInMap().contains(new Point(2, 6)));
        assertTrue(map.getPointsInMap().contains(new Point(4, 6)));
        assertTrue(map.getPointsInMap().contains(new Point(6, 6)));
        assertTrue(map.getPointsInMap().contains(new Point(8, 6)));

        assertTrue(map.getPointsInMap().contains(new Point(1, 5)));
        assertTrue(map.getPointsInMap().contains(new Point(3, 5)));
        assertTrue(map.getPointsInMap().contains(new Point(5, 5)));
        assertTrue(map.getPointsInMap().contains(new Point(7, 5)));
        assertTrue(map.getPointsInMap().contains(new Point(9, 5)));

        assertTrue(map.getPointsInMap().contains(new Point(0, 4)));
        assertTrue(map.getPointsInMap().contains(new Point(2, 4)));
        assertTrue(map.getPointsInMap().contains(new Point(4, 4)));
        assertTrue(map.getPointsInMap().contains(new Point(6, 4)));
        assertTrue(map.getPointsInMap().contains(new Point(8, 4)));

        assertTrue(map.getPointsInMap().contains(new Point(1, 3)));
        assertTrue(map.getPointsInMap().contains(new Point(3, 3)));
        assertTrue(map.getPointsInMap().contains(new Point(5, 3)));
        assertTrue(map.getPointsInMap().contains(new Point(7, 3)));
        assertTrue(map.getPointsInMap().contains(new Point(9, 3)));

        assertTrue(map.getPointsInMap().contains(new Point(0, 2)));
        assertTrue(map.getPointsInMap().contains(new Point(2, 2)));
        assertTrue(map.getPointsInMap().contains(new Point(4, 2)));
        assertTrue(map.getPointsInMap().contains(new Point(6, 2)));
        assertTrue(map.getPointsInMap().contains(new Point(8, 2)));

        assertTrue(map.getPointsInMap().contains(new Point(1, 1)));
        assertTrue(map.getPointsInMap().contains(new Point(3, 1)));
        assertTrue(map.getPointsInMap().contains(new Point(5, 1)));
        assertTrue(map.getPointsInMap().contains(new Point(7, 1)));
        assertTrue(map.getPointsInMap().contains(new Point(9, 1)));

        assertTrue(map.getPointsInMap().contains(new Point(0, 0)));
        assertTrue(map.getPointsInMap().contains(new Point(2, 0)));
        assertTrue(map.getPointsInMap().contains(new Point(4, 0)));
        assertTrue(map.getPointsInMap().contains(new Point(6, 0)));
        assertTrue(map.getPointsInMap().contains(new Point(8, 0)));
    }

    @Test
    public void testMapFilePointToGamePointIsWithinMapForOddHeightFile() {
        int fileWidth = 4;
        int fileHeight = 5;

        int engineWidth = GeometryMapping.gameWidthFromFileWidth(fileWidth);

        for (int fileY = 0; fileY < fileHeight; fileY++) {
            for (int fileX = 0; fileX < fileWidth; fileX++) {
                var engine = GeometryMapping.mapFilePointToGamePoint(fileX, fileY, fileHeight);

                assertTrue(engine.x >= 0);
                assertTrue(engine.x < engineWidth);
                assertTrue(engine.y >= 0);
                assertTrue(engine.y < fileHeight);
            }
        }
    }

    // ============================================================
    // 2️⃣ Round-trip tests
    // ============================================================

    @Test
    public void testMappingRoundTripForEvenHeightFile() {
        int fileWidth = 6;
        int fileHeight = 8;

        for (int fileY = 0; fileY < fileHeight; fileY++) {
            for (int fileX = 0; fileX < fileWidth; fileX++) {
                var engine = GeometryMapping.mapFilePointToGamePoint(fileX, fileY, fileHeight);
                var file = GeometryMapping.gamePointToMapFilePoint(engine.x, engine.y, fileWidth, fileHeight);

                assertEquals(fileX, file.x);
                assertEquals(fileY, file.y);
            }
        }
    }

    @Test
    public void coordinateRoundTrip_oddHeight() {
        int fileWidth = 6;
        int fileHeight = 7;

        for (int fileY = 0; fileY < fileHeight; fileY++) {
            for (int fileX = 0; fileX < fileWidth; fileX++) {
                var engine = GeometryMapping.mapFilePointToGamePoint(fileX, fileY, fileHeight);
                var file = GeometryMapping.gamePointToMapFilePoint(engine.x, engine.y, fileWidth, fileHeight);

                assertEquals(fileX, file.x);
                assertEquals(fileY, file.y);
            }
        }
    }

    // ============================================================
    // 3️⃣ Index mapping round-trip
    // ============================================================

    @Test
    public void indexRoundTrip() {
        int fileWidth = 5;
        int fileHeight = 6;

        int total = fileWidth * fileHeight;

        for (int index = 0; index < total; index++) {
            var engine = GeometryMapping.mapFileIndexToGamePoint(index, fileWidth, fileHeight);
            int recovered = GeometryMapping.gamePointToMapFileIndex(engine.x, engine.y, fileWidth, fileHeight);

            assertEquals(index, recovered);
        }
    }

    // ============================================================
    // 4️⃣ Uniqueness test
    // ============================================================

    @Test
    public void engineVerticesAreUnique() {
        int fileWidth = 5;
        int fileHeight = 6;

        var seen = new HashSet<Point>();

        for (int fileY = 0; fileY < fileHeight; fileY++) {
            for (int fileX = 0; fileX < fileWidth; fileX++) {
                var engine = GeometryMapping.mapFilePointToGamePoint(fileX, fileY, fileHeight);
                assertTrue(seen.add(engine));
            }
        }
    }

    // ============================================================
    // 5️⃣ layoutTiles test
    // ============================================================

    @Test
    public void layoutTiles_placesVegetationCorrectly() throws InvalidUserActionException {
        int fileWidth = 4;
        int fileHeight = 5;

        var tilesBelow = createTestArray(fileWidth, fileHeight);
        var tilesDownRight = createTestArray(fileWidth, fileHeight);

        int engineWidth = GeometryMapping.gameWidthFromFileWidth(fileWidth);

        var map = new GameMap(
                List.of(new Player("Player 0", BLUE, ROMANS, HUMAN)),
                engineWidth,
                fileHeight
        );

        GeometryMapping.layoutTilesInGameMap(
                fileWidth,
                fileHeight,
                tilesBelow,
                tilesDownRight,
                map
        );

        for (int fileY = 0; fileY < fileHeight; fileY++) {
            for (int fileX = 0; fileX < fileWidth; fileX++) {
                int index = fileY * fileWidth + fileX;
                var engine = GeometryMapping.mapFilePointToGamePoint(fileX, fileY, fileHeight);

                if (fileX + 1 < fileWidth && fileY + 1 < fileHeight) {
                    assertEquals(tilesDownRight[index], map.getVegetationDownRight(engine));
                }

                if (fileY + 1 < fileHeight) {
                    boolean valid = ((fileY & 1) == 0)
                                    ? fileX + 1 < fileWidth
                                    : fileX - 1 >= 0;

                    if (valid) {
                        assertEquals(tilesBelow[index], map.getVegetationBelow(engine));
                    }
                }
            }
        }
    }

    // ============================================================
    // 6️⃣ Y inversion tests
    // ============================================================

    @Test
    public void yInversion_evenHeight() {
        int fileHeight = 6;

        var bottom = GeometryMapping.mapFilePointToGamePoint(0, 0, fileHeight);
        var top = GeometryMapping.mapFilePointToGamePoint(0, fileHeight - 1, fileHeight);

        assertEquals(fileHeight - 1, bottom.y);
        assertEquals(0, top.y);
    }

    @Test
    public void yInversion_oddHeight() {
        int fileHeight = 7;

        var bottom = GeometryMapping.mapFilePointToGamePoint(0, 0, fileHeight);
        var top = GeometryMapping.mapFilePointToGamePoint(0, fileHeight - 1, fileHeight);

        assertEquals(fileHeight - 1, bottom.y);
        assertEquals(0, top.y);
    }

    // ============================================================
    // 7️⃣ Dimension tests
    // ============================================================

    @Test
    public void gameWidthFromFileWidth_basicCases() {
        assertEquals(2, GeometryMapping.gameWidthFromFileWidth(1));
        assertEquals(4, GeometryMapping.gameWidthFromFileWidth(2));
        assertEquals(6, GeometryMapping.gameWidthFromFileWidth(3));
        assertEquals(10, GeometryMapping.gameWidthFromFileWidth(5));
    }

    @Test
    public void gameHeightFromFileHeight_basicCases() {
        assertEquals(2, GeometryMapping.gameHeightFromFileHeight(1));
        assertEquals(6, GeometryMapping.gameHeightFromFileHeight(5));
        assertEquals(9, GeometryMapping.gameHeightFromFileHeight(8));
    }

    @Test
    public void mappedPointsStayWithinEngineBounds() {
        int fileWidth = 7;
        int fileHeight = 9;

        int engineWidth = GeometryMapping.gameWidthFromFileWidth(fileWidth);
        int engineHeight = GeometryMapping.gameHeightFromFileHeight(fileHeight);

        for (int fileY = 0; fileY < fileHeight; fileY++) {
            for (int fileX = 0; fileX < fileWidth; fileX++) {
                var engine = GeometryMapping.mapFilePointToGamePoint(fileX, fileY, fileHeight);

                assertTrue(engine.x >= 0);
                assertTrue(engine.x < engineWidth);
                assertTrue(engine.y >= 0);
                assertTrue(engine.y < engineHeight);
            }
        }
    }
}