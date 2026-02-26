package org.appland.settlers.maps.utils;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Vegetation;

/**
 * GeometryMapping
 *
 * <p><b>Understanding of the Settlers II Map File Geometry</b>
 *
 * <ul>
 *   <li>
 *     The map file stores a <b>vertex grid</b>, not a square tile grid.
 *     Each point represents a corner of hex tiles.
 *   </li>
 *
 *   <li>
 *     The grid is a <b>staggered hex lattice</b>.
 *     Every second row is horizontally shifted.
 *   </li>
 *
 *   <li>
 *     The map file origin (0,0) is located at the <b>top-left corner</b>.
 *   </li>
 *
 *   <li>
 *     The layout of vertices in file space looks like:
 *
 * <pre>
 *  fileY = 0  o---o---o---o   ← top row (upper-left corner is here)
 *              \ / \ / \ /
 *  fileY = 1   o---o---o---o
 *               \ / \ / \ /
 *  fileY = 2    o---o---o---o
 *
 *      fileX →
 * </pre>
 * <p>
 *     Even and odd rows are horizontally staggered.
 *   </li>
 *
 *   <li>
 *     Two triangle arrays exist per vertex:
 *     <ul>
 *       <li><b>tilesBelow</b> — downward-facing triangle whose top vertex is at (fileX, fileY)</li>
 *       <li><b>tilesDownRight</b> — upward-facing triangle whose upper-left vertex is at (fileX, fileY)</li>
 *     </ul>
 *   </li>
 *
 *   <li>
 *     Not every vertex owns a downward triangle.
 *     Due to staggering:
 *
 * <pre>
 *  If fileY is even:
 *      below triangle exists only if fileX + 1 < fileWidth
 *
 *  If fileY is odd:
 *      below triangle exists only if fileX - 1 >= 0
 * </pre>
 * <p>
 *     This prevents triangles from extending outside the map boundary.
 *   </li>
 *
 *   <li>
 *     The map file stores data in <b>row-major order</b>:
 *
 * <pre>
 *  fileIndex = fileY * fileWidth + fileX
 * </pre>
 *   </li>
 * </ul>
 *
 *
 * <p><b>Game Engine Coordinate System</b>
 *
 * <ul>
 *   <li>
 *     The engine uses a <b>bottom-left origin</b>.
 *   </li>
 *
 *   <li>
 *     Y increases upward.
 *   </li>
 *
 *   <li>
 *     The hex lattice is embedded into a square grid where:
 *     <ul>
 *       <li>Only every second X coordinate represents a valid vertex.</li>
 *       <li>Row staggering is preserved.</li>
 *     </ul>
 *   </li>
 *
 *   <li>
 *     Engine bounding box:
 *
 * <pre>
 *  engineWidth  = 2 * fileWidth + 1
 *  engineHeight = fileHeight
 * </pre>
 * <p>
 *     Some X columns may remain unused depending on parity.
 *   </li>
 * </ul>
 *
 *
 * <p><b>Mapping From File Coordinates to Engine Coordinates</b>
 *
 * <ul>
 *   <li>
 *     The Y-axis must be inverted:
 *
 * <pre>
 *  engineY = fileHeight - 1 - fileY
 * </pre>
 *   </li>
 *
 *   <li>
 *     Horizontal scaling is required because engine vertices occupy
 *     every second X column:
 *
 * <pre>
 *  engineX = 2 * fileX + rowShift + globalOffset
 * </pre>
 *   </li>
 *
 *   <li>
 *     Row staggering:
 *
 * <pre>
 *  rowShift = fileY % 2
 * </pre>
 *   </li>
 *
 *   <li>
 *     Parity must remain stable after Y inversion.
 *     To ensure correctness for both even and odd map heights:
 *
 * <pre>
 *  globalOffset = (fileHeight - 1) % 2
 * </pre>
 *   </li>
 *
 *   <li>
 *     Final forward mapping:
 *
 * <pre>
 *  engineY = fileHeight - 1 - fileY
 *  engineX = 2 * fileX + (fileY % 2) + (fileHeight - 1) % 2
 * </pre>
 *   </li>
 * </ul>
 *
 *
 * <p><b>Inverse Mapping From Engine to File Coordinates</b>
 *
 * <ul>
 *   <li>
 *     Recover fileY:
 *
 * <pre>
 *  fileY = fileHeight - 1 - engineY
 * </pre>
 *   </li>
 *
 *   <li>
 *     Recover fileX:
 *
 * <pre>
 *  fileX = (engineX - globalOffset - (fileY % 2)) / 2
 * </pre>
 *   </li>
 *
 *   <li>
 *     A coordinate is a valid vertex only if:
 *
 * <pre>
 *  (engineX - globalOffset - (fileY % 2)) % 2 == 0
 * </pre>
 *   </li>
 * </ul>
 *
 *
 * <p><b>Key Properties of the Mapping</b>
 *
 * <ul>
 *   <li>Bijective over valid vertices</li>
 *   <li>Preserves adjacency relationships</li>
 *   <li>Preserves triangle orientation</li>
 *   <li>Works for both even and odd file heights</li>
 *   <li>Maintains correct staggered hex geometry</li>
 * </ul>
 *
 *
 * <p><b>Visual Illustration: File Space → Engine Space</b>
 *
 * <p>Example: fileWidth = 4, fileHeight = 5
 *
 * <p>File coordinate system (origin at upper-left):
 *
 * <pre>
 *  fileY = 0     o---o---o---o
 *                 \ / \ / \ /
 *  fileY = 1      o---o---o---o
 *                  \ / \ / \ /
 *  fileY = 2       o---o---o---o
 *                   \ / \ / \ /
 *  fileY = 3        o---o---o---o
 *                    \ / \ / \ /
 *  fileY = 4         o---o---o---o
 *
 *      fileX →
 * </pre>
 * <p>
 * Even and odd rows are staggered.
 * The origin (0,0) is the upper-left vertex.
 *
 *
 * <p>After transformation to engine space:
 *
 * <pre>
 *  engineY = fileHeight - 1 - fileY
 *
 *  engineX = 2 * fileX + (fileY % 2) + globalOffset
 *  globalOffset = (fileHeight - 1) % 2
 * </pre>
 * <p>
 * For fileHeight = 5:
 *
 * <pre>
 *  globalOffset = (5 - 1) % 2 = 0
 * </pre>
 * <p>
 * <p>
 * Engine coordinate system (origin at bottom-left):
 *
 * <pre>
 *  engineY = 4    o---o---o---o---?
 *  engineY = 3     o---o---o---o
 *  engineY = 2    o---o---o---o---?
 *  engineY = 1     o---o---o---o
 *  engineY = 0    o---o---o---o---?
 *
 *      engineX →
 *
 *  (Only every second X column is used as a vertex.)
 * </pre>
 * <p>
 * Notice:
 * - Y is flipped.
 * - Horizontal spacing doubled.
 * - Row staggering preserved.
 * - One possible unused column appears depending on parity.
 *
 *
 * <p><b>Worked Numeric Example (4×5 map)</b>
 * <p>
 * Given:
 *
 * <pre>
 *  fileWidth  = 4
 *  fileHeight = 5
 *  globalOffset = (5 - 1) % 2 = 0
 * </pre>
 * <p>
 * <p>
 * Example 1:
 *
 * <pre>
 *  fileX = 0, fileY = 0
 *
 *  engineY = 5 - 1 - 0 = 4
 *  engineX = 2*0 + (0 % 2) + 0 = 0
 *
 *  → engine = (0, 4)
 * </pre>
 * <p>
 * <p>
 * Example 2:
 *
 * <pre>
 *  fileX = 1, fileY = 0
 *
 *  engineY = 4
 *  engineX = 2*1 + 0 = 2
 *
 *  → engine = (2, 4)
 * </pre>
 * <p>
 * <p>
 * Example 3:
 *
 * <pre>
 *  fileX = 0, fileY = 1
 *
 *  engineY = 5 - 1 - 1 = 3
 *  engineX = 2*0 + 1 = 1
 *
 *  → engine = (1, 3)
 * </pre>
 * <p>
 * <p>
 * Example 4:
 *
 * <pre>
 *  fileX = 3, fileY = 4
 *
 *  engineY = 5 - 1 - 4 = 0
 *  engineX = 2*3 + (4 % 2) = 6
 *
 *  → engine = (6, 0)
 * </pre>
 *
 *
 * <p>Observe:
 * - Adjacent file vertices remain adjacent in engine space.
 * - Row staggering is preserved exactly.
 * - The mapping is bijective over valid vertices.
 *
 *
 * <p><b>Parity Invariant</b>
 * <p>
 * A coordinate in engine space corresponds to a valid file vertex iff:
 *
 * <pre>
 *  (engineX + engineY) % 2 == (fileHeight - 1) % 2
 * </pre>
 * <p>
 * This invariant guarantees that:
 * - Only correct vertex positions are used.
 * - Triangle adjacency relationships are preserved.
 */
public class GeometryMapping {

    /**
     * Lays out the map file triangle arrays (tilesBelow and tilesDownRight),
     * provided as one-dimensional row-major arrays, into the game engine
     * coordinate system.
     *
     * <p>The map file stores vertices in row-major order:
     *
     * <pre>
     * fileIndex = fileY * fileWidth + fileX
     * </pre>
     *
     * <p>The hex lattice uses staggered rows based on fileY parity.
     * The engine uses bottom-left origin, so Y must be inverted.
     * To preserve parity after inversion for both even and odd heights:
     *
     * <pre>
     * globalOffset = (fileHeight - 1) % 2
     *
     * engineY = fileHeight - 1 - fileY
     * engineX = 2 * fileX + (fileY % 2) + globalOffset
     * </pre>
     *
     * <p>Boundary checks prevent creation of invalid triangles at map edges.
     *
     * @param fileWidth      Width of the map file vertex grid.
     * @param fileHeight     Height of the map file vertex grid.
     * @param tilesBelow     Row-major array of downward triangles.
     * @param tilesDownRight Row-major array of upward triangles.
     */
    public static void layoutTilesInGameMap(
            int fileWidth,
            int fileHeight,
            Vegetation[] tilesBelow,
            Vegetation[] tilesDownRight,
            GameMap map
    ) {
        int globalOffset = (fileHeight - 1) & 1;

        for (int fileY = 0; fileY < fileHeight; fileY++) {
            int engineY = fileHeight - 1 - fileY;
            int rowShift = fileY & 1;

            for (int fileX = 0; fileX < fileWidth; fileX++) {
                int fileIndex = fileY * fileWidth + fileX;

                int engineX = 2 * fileX + rowShift + globalOffset;

                var enginePoint = new Point(engineX, engineY);

                /*
                 * Below tile (downward triangle)
                 * Top vertex at (fileX, fileY)
                 */
                if (fileY + 1 < fileHeight) {
                    boolean valid;

                    if ((fileY & 1) == 0) {
                        valid = (fileX + 1 < fileWidth);
                    } else {
                        valid = (fileX - 1 >= 0);
                    }

                    if (valid) {
                        map.setVegetationBelow(enginePoint, tilesBelow[fileIndex]);
                    }
                }

                /*
                 * Down-right tile (upward triangle)
                 * Upper-left vertex at (fileX, fileY)
                 */
                if (fileX + 1 < fileWidth && fileY + 1 < fileHeight) {
                    map.setVegetationDownRight(enginePoint, tilesDownRight[fileIndex]);
                }
            }
        }
    }

    /**
     * Converts a map file vertex coordinate (fileX, fileY)
     * into the corresponding engine coordinate.
     *
     * <p>The map file uses a top-left origin and a staggered hex vertex grid.
     * The engine uses a bottom-left origin and a square grid where valid
     * vertices occupy every second X column.
     *
     * <p>The transformation preserves the hex lattice geometry by:
     *
     * <pre>
     * globalOffset = (fileHeight - 1) % 2
     *
     * engineY = fileHeight - 1 - fileY
     * engineX = 2 * fileX + (fileY % 2) + globalOffset
     * </pre>
     *
     * <p>The globalOffset ensures that parity remains stable after Y inversion
     * for both even and odd file heights.
     *
     * @param fileX      X coordinate in the map file vertex grid.
     * @param fileY      Y coordinate in the map file vertex grid.
     * @param fileHeight Total height of the map file vertex grid.
     * @return Corresponding engine coordinate.
     */
    public static Point mapFilePointToGamePoint(
            int fileX,
            int fileY,
            int fileHeight
    ) {
        int globalOffset = (fileHeight - 1) & 1;

        int engineY = fileHeight - 1 - fileY;
        int engineX = 2 * fileX + (fileY & 1) + globalOffset;

        return new Point(engineX, engineY);
    }

    /**
     * Converts a linear map file vertex index into an engine coordinate.
     *
     * <p>The map file stores vertex data in row-major order:
     *
     * <pre>
     * fileY = fileIndex / fileWidth
     * fileX = fileIndex % fileWidth
     * </pre>
     *
     * <p>The vertex is then transformed to engine coordinates using:
     *
     * <pre>
     * globalOffset = (fileHeight - 1) % 2
     *
     * engineY = fileHeight - 1 - fileY
     * engineX = 2 * fileX + (fileY % 2) + globalOffset
     * </pre>
     *
     * <p>This preserves the staggered hex lattice while inverting Y.
     *
     * @param fileIndex  Linear index in the map file vertex arrays.
     * @param fileWidth  Width of the map file vertex grid.
     * @param fileHeight Height of the map file vertex grid.
     * @return Corresponding engine coordinate.
     */
    public static java.awt.Point mapFileIndexToGamePoint(
            int fileIndex,
            int fileWidth,
            int fileHeight
    ) {
        int fileY = fileIndex / fileWidth;
        int fileX = fileIndex % fileWidth;

        int globalOffset = (fileHeight - 1) & 1;

        int engineY = fileHeight - 1 - fileY;
        int engineX = 2 * fileX + (fileY & 1) + globalOffset;

        return new java.awt.Point(engineX, engineY);
    }


    /**
     * Converts an engine coordinate back into a map file vertex coordinate.
     *
     * <p>The inverse mapping reconstructs the original hex lattice coordinates
     * by reversing Y inversion and undoing the staggered X shift.
     *
     * <p>The forward mapping is:
     *
     * <pre>
     * globalOffset = (fileHeight - 1) % 2
     *
     * engineY = fileHeight - 1 - fileY
     * engineX = 2 * fileX + (fileY % 2) + globalOffset
     * </pre>
     *
     * <p>The inverse mapping is therefore:
     *
     * <pre>
     * fileY = fileHeight - 1 - engineY
     * fileX = (engineX - globalOffset - (fileY % 2)) / 2
     * </pre>
     *
     * <p>A coordinate is valid only if:
     *
     * <pre>
     * (engineX - globalOffset - (fileY % 2)) % 2 == 0
     * </pre>
     * <p>
     * Otherwise the engine coordinate does not correspond to a valid file vertex.
     *
     * @param engineX    X coordinate in engine space.
     * @param engineY    Y coordinate in engine space.
     * @param fileWidth  Width of the map file vertex grid.
     * @param fileHeight Height of the map file vertex grid.
     * @return Corresponding map file coordinate.
     * @throws IllegalArgumentException if the coordinate is invalid or out of bounds.
     */
    public static java.awt.Point gamePointToMapFilePoint(
            int engineX,
            int engineY,
            int fileWidth,
            int fileHeight
    ) {
        int globalOffset = (fileHeight - 1) & 1;

        int fileY = fileHeight - 1 - engineY;

        int numerator = engineX - globalOffset - (fileY & 1);

        if ((numerator & 1) != 0) {
            throw new IllegalArgumentException("Not a valid vertex coordinate");
        }

        int fileX = numerator / 2;

        if (fileX < 0 || fileX >= fileWidth || fileY < 0 || fileY >= fileHeight) {
            throw new IllegalArgumentException("Out of bounds");
        }

        return new java.awt.Point(fileX, fileY);
    }

    /**
     * Converts an engine coordinate into a linear map file vertex index.
     *
     * <p>This first computes the inverse vertex mapping:
     *
     * <pre>
     * fileY = fileHeight - 1 - engineY
     * fileX = (engineX - globalOffset - (fileY % 2)) / 2
     * </pre>
     *
     * <p>The linear index is then computed in row-major order:
     *
     * <pre>
     * fileIndex = fileY * fileWidth + fileX
     * </pre>
     *
     * @param engineX    X coordinate in engine space.
     * @param engineY    Y coordinate in engine space.
     * @param fileWidth  Width of the map file vertex grid.
     * @param fileHeight Height of the map file vertex grid.
     * @return Corresponding linear map file vertex index.
     * @throws IllegalArgumentException if the coordinate is invalid.
     */
    public static int gamePointToMapFileIndex(
            int engineX,
            int engineY,
            int fileWidth,
            int fileHeight
    ) {
        var filePoint = gamePointToMapFilePoint(engineX, engineY, fileWidth, fileHeight);

        return filePoint.y * fileWidth + filePoint.x;
    }

    /**
     * Returns the engine width corresponding to a given file width.
     * <p>
     * Math:
     * Each file column maps to x = 2 * fileX (or 2 * fileX - 1 depending on parity).
     * The rightmost fileX = fileWidth - 1
     * Therefore:
     * <p>
     * maxEngineX = 2 * (fileWidth - 1)
     * <p>
     * Since x coordinates are zero-based:
     * <p>
     * engineWidth = maxEngineX + 1
     * = 2 * fileWidth - 1
     */
    public static int gameWidthFromFileWidth(int fileWidth) {
        if (fileWidth <= 0) {
            throw new IllegalArgumentException("fileWidth must be > 0");
        }
        return 2 * fileWidth - 1;
    }

    /**
     * Returns the engine height corresponding to a given file height.
     * <p>
     * Math:
     * The file grid is vertically flipped into engine space,
     * but no scaling occurs.
     * <p>
     * Therefore:
     * <p>
     * engineHeight = fileHeight
     */
    public static int gameHeightFromFileHeight(int fileHeight) {
        if (fileHeight <= 0) {
            throw new IllegalArgumentException("fileHeight must be > 0");
        }
        return fileHeight;
    }
}
