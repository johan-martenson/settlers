package org.appland.settlers.test.maps;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.maps.utils.GeometryMapping;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Vegetation;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestGeometryMapping {

    /*
     * Helper enum values for testing.
     */
    private static final Vegetation A = Vegetation.LAVA_1;
    private static final Vegetation B = Vegetation.LAVA_2;
    private static final Vegetation C = Vegetation.LAVA_3;

    /*
     * Small helper to build a predictable 1D array
     */
    private Vegetation[] createTestArray(int fileWidth, int fileHeight) {
        var arr = new Vegetation[fileWidth * fileHeight];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = (i % 2 == 0) ? A : B;
        }

        return arr;
    }

    // ============================================================
    // 1️⃣ Forward mapping tests
    // ============================================================

    @Test
    public void mapFilePointToGamePoint_evenHeight() {
        int fileWidth = 4;
        int fileHeight = 6; // even

        for (int fileY = 0; fileY < fileHeight; fileY++) {
            for (int fileX = 0; fileX < fileWidth; fileX++) {
                var engine = GeometryMapping.mapFilePointToGamePoint(fileX, fileY, fileHeight);
                var engineWidth = GeometryMapping.gameWidthFromFileWidth(fileWidth);

                assertTrue(engine.x >= 0);
                assertTrue(engine.y >= 0);
                assertTrue(engine.x <= engineWidth);
                assertTrue(engine.y < fileHeight);
            }
        }
    }

    @Test
    public void mapFilePointToGamePoint_oddHeight() {
        int fileWidth = 4;
        int fileHeight = 5; // odd

        for (int fileY = 0; fileY < fileHeight; fileY++) {
            for (int fileX = 0; fileX < fileWidth; fileX++) {
                var engine = GeometryMapping.mapFilePointToGamePoint(fileX, fileY, fileHeight);

                assertTrue(engine.x >= 0);
                assertTrue(engine.y >= 0);
                assertTrue(engine.y < fileHeight);
            }
        }
    }

    // ============================================================
    // 2️⃣ Round-trip coordinate test
    // ============================================================

    @Test
    public void coordinateRoundTrip_evenHeight() {
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
    // 3️⃣ Index mapping tests
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
    // 4️⃣ Engine vertex validity test
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
    // 5️⃣ layoutTilesInEngine test
    // ============================================================

    @Test
    public void layoutTiles_placesVegetationCorrectly() throws InvalidUserActionException {
        int fileWidth = 4;
        int fileHeight = 5;

        Vegetation[] tilesBelow = createTestArray(fileWidth, fileHeight);
        Vegetation[] tilesDownRight = createTestArray(fileWidth, fileHeight);

        GameMap map = new GameMap(
                List.of(new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN)),
                2 * fileWidth + 1,
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

                // Down-right triangle always exists except last row/col
                if (fileX + 1 < fileWidth && fileY + 1 < fileHeight) {
                    assertEquals(tilesDownRight[index], map.getVegetationDownRight(engine));
                }

                // Below triangle conditional
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

    @Test
    public void yInversion_isCorrectForOddFileHeight() {
        int fileHeight = 7;

        var bottom = GeometryMapping.mapFilePointToGamePoint(0, 0, fileHeight);
        var top = GeometryMapping.mapFilePointToGamePoint(0, fileHeight - 1, fileHeight);

        assertEquals(fileHeight - 1, bottom.y);
        assertEquals(0, top.y);
    }

    @Test
    public void yInversion_isCorrectForEvenFileHeight() {
        int fileHeight = 6;

        var bottom = GeometryMapping.mapFilePointToGamePoint(0, 0, fileHeight);
        var top = GeometryMapping.mapFilePointToGamePoint(0, fileHeight - 1, fileHeight);

        assertEquals(fileHeight - 1, bottom.y);
        assertEquals(0, top.y);
    }

    @Test
    public void gameWidthFromFileWidth_basicCases() {
        assertEquals(1, GeometryMapping.gameWidthFromFileWidth(1));
        assertEquals(3, GeometryMapping.gameWidthFromFileWidth(2));
        assertEquals(5, GeometryMapping.gameWidthFromFileWidth(3));
        assertEquals(9, GeometryMapping.gameWidthFromFileWidth(5));
    }

    @Test
    public void gameHeightFromFileHeight_basicCases() {
        assertEquals(1, GeometryMapping.gameHeightFromFileHeight(1));
        assertEquals(5, GeometryMapping.gameHeightFromFileHeight(5));
        assertEquals(8, GeometryMapping.gameHeightFromFileHeight(8));
    }

    @Test
    public void engineDimensions_evenHeight() {
        int fileWidth = 6;
        int fileHeight = 8;

        int engineWidth = GeometryMapping.gameWidthFromFileWidth(fileWidth);
        int engineHeight = GeometryMapping.gameHeightFromFileHeight(fileHeight);

        assertEquals(11, engineWidth);
        assertEquals(8, engineHeight);
    }

    @Test
    public void engineDimensions_oddHeight() {
        int fileWidth = 6;
        int fileHeight = 7;

        int engineWidth = GeometryMapping.gameWidthFromFileWidth(fileWidth);
        int engineHeight = GeometryMapping.gameHeightFromFileHeight(fileHeight);

        assertEquals(11, engineWidth);
        assertEquals(7, engineHeight);
    }

    @Test
    public void mappedPointsStayWithinEngineBounds() {
        int fileWidth = 7;
        int fileHeight = 9;

        int engineWidth = GeometryMapping.gameWidthFromFileWidth(fileWidth);
        int engineHeight = GeometryMapping.gameHeightFromFileHeight(fileHeight);

        for (int fileY = 0; fileY < fileHeight; fileY++) {
            for (int fileX = 0; fileX < fileWidth; fileX++) {

                var engine = GeometryMapping.mapFilePointToGamePoint(
                                fileX,
                                fileY,
                                fileHeight
                        );

                assertTrue(engine.x >= 0);
                assertTrue(engine.x < engineWidth);

                assertTrue(engine.y >= 0);
                assertTrue(engine.y < engineHeight);
            }
        }
    }

    @Test
    public void rightmostFileColumnMapsInsideEngineWidth() {
        int fileWidth = 10;
        int fileHeight = 6;

        int engineWidth = GeometryMapping.gameWidthFromFileWidth(fileWidth);

        for (int fileY = 0; fileY < fileHeight; fileY++) {
            var engine = GeometryMapping.mapFilePointToGamePoint(
                            fileWidth - 1,
                            fileY,
                            fileHeight
                    );

            System.out.println(fileWidth);
            System.out.println(engine);
            assertTrue(engine.x < engineWidth);
        }
    }
}

