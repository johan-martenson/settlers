package org.appland.settlers.maps.utils;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Vegetation;

import java.util.ArrayList;
import java.util.List;

/**
 * GeometryMapping
 *
 * ===============================================================
 *  Settlers II Map File Geometry → Game Engine Geometry
 * ===============================================================
 *
 * ---------------------------------------------------------------
 *  1️⃣ Map File Geometry
 * ---------------------------------------------------------------
 *
 * - The map file stores a vertex grid (hex corner lattice).
 * - Coordinates use a top-left origin.
 * - Rows are horizontally staggered.
 *
 * File space (origin at top-left):
 *
 *   fileY = 0   o---o---o---o
 *                \ / \ / \ /
 *   fileY = 1     o---o---o---o
 *                / \ / \ / \ /
 *   fileY = 2   o---o---o---o
 *
 *      fileX →
 *
 * - fileIndex = fileY * fileWidth + fileX
 *
 *
 * ---------------------------------------------------------------
 *  2️⃣ Game Engine Geometry
 * ---------------------------------------------------------------
 *
 * - Bottom-left origin
 * - Y increases upward
 * - Hex lattice embedded in square grid
 * - Only every second X coordinate is a valid vertex
 *
 * Engine embedding:
 *
 *   engineWidth  = 2 * fileWidth
 *   engineHeight = fileHeight + 1
 *
 * Note: one extra line at the bottom to anchor belowTiles & downRightTiles because tiles are downward from points
 * in the map file
 *
 * Example (fileWidth=4, fileHeight=4):
 *
 *   engineY=4  o---o---o---o---o
 *   engineY=3    o---o---o---o---o
 *   engineY=2  o---o---o---o---o
 *   engineY=1    o---o---o---o---o
 *   engineY=0  o---o---o---o---o   <--- Extra line, to anchor tiles.
 *
 * Every second X column is used.
 *
 *
 * ---------------------------------------------------------------
 *  3️⃣ Forward Mapping (File → Engine)
 * ---------------------------------------------------------------
 *
 * Y inversion:
 *
 *   engineY = fileHeight - fileY
 *
 * Horizontal scaling and row staggering:
 *
 *   engineX = 2 * fileX + (fileY % 2)
 *
 * No global offset is required.
 *
 *
 * ---------------------------------------------------------------
 *  4️⃣ Inverse Mapping (Engine → File)
 * ---------------------------------------------------------------
 *
 *   fileY = fileHeight - engineY
 *   fileX = (engineX - (fileY % 2)) / 2
 *
 * Valid vertex condition:
 *
 *   (engineX - (fileY % 2)) % 2 == 0
 *
 *
 * ---------------------------------------------------------------
 *  5️⃣ Properties
 * ---------------------------------------------------------------
 *
 * - Bijective over valid vertices
 * - No parity hacks
 * - No global offsets
 * - Works for both even and odd file heights
 * - Width/height formulas are stable and independent
 *
 */
public class GeometryMapping {

    /* ============================================================
       Layout Triangles
       ============================================================ */

    public static List<Point> gameMapPointsFromFileDimensions(int fileWidth, int fileHeight) {
        var result = new ArrayList<Point>();

        for (int fileY = 0; fileY < fileHeight; fileY++) {
            for (int fileX = 0; fileX < fileWidth; fileX += 1) {
                result.add(mapFilePointToGamePoint(fileX, fileY, fileHeight));
            }
        }

        return result;
    }

    public static void layoutTilesInGameMap(
            int fileWidth,
            int fileHeight,
            Vegetation[] tilesBelow,
            Vegetation[] tilesDownRight,
            GameMap map
    ) {
        for (int fileY = 0; fileY < fileHeight; fileY++) {
            int engineY = fileHeight - 1 - fileY;
            int rowShift = fileY & 1;

            for (int fileX = 0; fileX < fileWidth; fileX++) {
                int fileIndex = fileY * fileWidth + fileX;
                int engineX = 2 * fileX + rowShift;

                var enginePoint = new Point(engineX, engineY);

                /* ---- Below triangle ---- */
                if (fileY + 1 < fileHeight) {
                    boolean valid = ((fileY & 1) == 0)
                                    ? fileX + 1 < fileWidth
                                    : fileX - 1 >= 0;

                    if (valid) {
                        map.setVegetationBelow(enginePoint, tilesBelow[fileIndex]);
                    }
                }

                /* ---- Down-right triangle ---- */
                if (fileX + 1 < fileWidth && fileY + 1 < fileHeight) {
                    map.setVegetationDownRight(enginePoint, tilesDownRight[fileIndex]);
                }
            }
        }
    }

    /* ============================================================
       Forward Mapping
       ============================================================ */

    public static Point mapFilePointToGamePoint(
            int fileX,
            int fileY,
            int fileHeight
    ) {
        int engineY = fileHeight - 1 - fileY;
        int engineX = 2 * fileX + (fileY & 1);

        return new Point(engineX, engineY);
    }

    public static java.awt.Point mapFileIndexToGamePoint(
            int fileIndex,
            int fileWidth,
            int fileHeight
    ) {
        int fileY = fileIndex / fileWidth;
        int fileX = fileIndex % fileWidth;

        int engineY = fileHeight - 1 - fileY;
        int engineX = 2 * fileX + (fileY & 1);

        return new java.awt.Point(engineX, engineY);
    }

    /* ============================================================
       Inverse Mapping
       ============================================================ */

    public static java.awt.Point gamePointToMapFilePoint(
            int engineX,
            int engineY,
            int fileWidth,
            int fileHeight
    ) {
        int fileY = fileHeight - 1 - engineY;
        int numerator = engineX - (fileY & 1);

        if ((numerator & 1) != 0) {
            throw new IllegalArgumentException("Not a valid vertex coordinate");
        }

        int fileX = numerator / 2;

        if (fileX < 0 || fileX >= fileWidth || fileY < 0 || fileY >= fileHeight) {
            throw new IllegalArgumentException("Out of bounds");
        }

        return new java.awt.Point(fileX, fileY);
    }

    public static int gamePointToMapFileIndex(
            int engineX,
            int engineY,
            int fileWidth,
            int fileHeight
    ) {
        var filePoint = gamePointToMapFilePoint(
                engineX,
                engineY,
                fileWidth,
                fileHeight
        );

        return filePoint.y * fileWidth + filePoint.x;
    }

    /* ============================================================
       Dimension Conversion
       ============================================================ */

    public static int gameWidthFromFileWidth(int fileWidth) {
        if (fileWidth <= 0) {
            throw new IllegalArgumentException("fileWidth must be > 0");
        }

        return 2 * fileWidth;
    }

    public static int gameHeightFromFileHeight(int fileHeight) {
        if (fileHeight <= 0) {
            throw new IllegalArgumentException("fileHeight must be > 0");
        }

        return fileHeight + 1;
    }

    public static int mapFileWidthFromGameWidth(int gameWidth) {
        return gameWidth / 2;
    }

    public static int mapFileHeightFromGameHeight(int gameHeight) {
        return gameHeight;
    }
}