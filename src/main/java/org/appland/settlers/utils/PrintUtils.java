package org.appland.settlers.utils;

import org.appland.settlers.maps.MapFile;
import org.appland.settlers.maps.MapFilePoint;
import org.appland.settlers.maps.Texture;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Size;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PrintUtils {

    /**
     * Renders the MapFile instance to a String array with highlights, if included.
     *
     * @param mapFile    the map file to render
     * @param highlights the points to highlight
     * @param debug      whether to print debug information
     * @return a 2D array representing the rendered map
     */
    public String[][] renderMapFileToStringArray(MapFile mapFile, List<Point> highlights, boolean debug) {
        int maxWidth = mapFile.getWidth() * 2 + 2;
        int maxHeight = mapFile.getHeight() + 1;

        if (debug) {
            System.out.printf("Width: %d, height: %d%n", maxWidth, maxHeight);
        }

        String[][] bfr = new String[maxHeight][maxWidth * 2];

        for (MapFilePoint mapFilePoint : mapFile.getMapFilePoints()) {
            int x = mapFilePoint.getGamePointPosition().x;
            int y = mapFilePoint.getGamePointPosition().y;

            // Skip points that will not appear on screen
            if (x >= maxWidth || y >= maxHeight) {
                continue;
            }

            // Draw water
            if (Texture.isWater(mapFilePoint.getVegetationBelow()) && y > 0) {
                bfr[y - 1][x] = " ";
            } else if (y > 0 && bfr[y - 1][x] == null) {
                bfr[y - 1][x] = ".";
            }

            if (y > 0 && x < maxWidth - 2 && Texture.isWater(mapFilePoint.getVegetationDownRight())) {
                bfr[y - 1][x + 1] = " ";
            } else if (y > 0 && x < maxWidth - 2 && bfr[y - 1][x + 1] == null) {
                bfr[y - 1][x + 1] = ".";
            }

            // Place stones
            if (mapFilePoint.hasStone()) {
                bfr[y][x] = "O";
            }

            // Place trees
            if (mapFilePoint.hasTree()) {
                bfr[y][x] = "T";
            }
        }

        // Add highlights
        if (highlights != null) {
            for (java.awt.Point highlight : highlights) {
                bfr[highlight.y - 1][highlight.x - 1] = "*";
                bfr[highlight.y - 1][highlight.x + 1] = "*";
                bfr[highlight.y + 1][highlight.x - 1] = "*";
                bfr[highlight.y + 1][highlight.x + 1] = "*";
                bfr[highlight.y + 1][highlight.x] = "*";
                bfr[highlight.y - 1][highlight.x] = "*";
                bfr[highlight.y][highlight.x - 1] = "*";
                bfr[highlight.y][highlight.x + 1] = "*";

                bfr[highlight.y][highlight.x] = "X";
            }
        }

        return bfr;
    }

    /**
     * Returns a string representation of a tree, stone, or nothing.
     *
     * @param mapFile the map file
     * @param point   the point to check
     * @return a string representing a tree, stone, or empty
     */
    public String treeOrStoneOrNoneToString(MapFile mapFile, Point point) {
        MapFilePoint mapFilePoint = mapFile.getMapFilePoint(point);

        if (mapFilePoint.hasTree()) {
            return "tree ";
        } else if (mapFilePoint.hasStone()) {
            return "stone";
        }

        return "  x  ";
    }

    /**
     * Prints a list of points as a bullet list.
     *
     * @param points the list of points to print
     */
    public void printPointsAsItems(List<Point> points) {
        points.forEach(point -> System.out.printf(" - %s%n", point));
    }

    /**
     * Prints the starting points from the GameMap instance.
     *
     * @param map the GameMap instance
     */
    public void printStartingPointsFromGameMap(GameMap map) {
        System.out.println();
        System.out.println("Starting points:");
        printPointsAsItems(map.getStartingPoints());
    }

    /**
     * Prints a rendered map file to the console.
     *
     * @param mapFile      the map file to print
     * @param consoleWidth the width of the console
     * @param debug        whether to print debug information
     */
    public void printMapFile(MapFile mapFile, int consoleWidth, boolean debug) {
        String[][] mapFileRender = renderMapFileToStringArray(mapFile, mapFile.getGamePointStartingPoints(), debug);

        // Print the render of the map file
        StringBuilder sb = new StringBuilder();
        for (String[] row : mapFileRender) {
            for (int index = 0; index < consoleWidth && index < row.length; index++) {
                sb.append(Objects.requireNonNullElse(row[index], " "));
            }
            sb.append(System.lineSeparator());
        }

        System.out.print(sb);
    }

    /**
     * Prints basic information about the map file.
     *
     * @param mapFile the map file
     */
    public void printMapInfo(MapFile mapFile) {
        System.out.printf("""
                About the map:
                 - Title: %s
                 - Author: %s
                 - Width: %d
                 - Height: %d
                 - Max number of players: %d
                """, mapFile.getTitle(), mapFile.getAuthor(), mapFile.getWidth(), mapFile.getHeight(), mapFile.getMaxNumberOfPlayers());
    }

    /**
     * Prints all points in the map file.
     *
     * @param mapFile the map file
     */
    public void printMapFilePointList(MapFile mapFile) {
        System.out.println();
        System.out.println("All spots in the map file");

        for (MapFilePoint mapFilePoint : mapFile.getMapFilePoints()) {
            Point point = mapFilePoint.getGamePointPosition();

            MapFilePoint spotLeft = mapFile.getMapFilePoint(point.left());
            MapFilePoint spotUpLeft = mapFile.getMapFilePoint(point.upLeft());
            MapFilePoint spotDownLeft = mapFile.getMapFilePoint(point.downLeft());
            MapFilePoint spotRight = mapFile.getMapFilePoint(point.right());
            MapFilePoint spotUpRight = mapFile.getMapFilePoint(point.upRight());
            MapFilePoint spotDownRight = mapFile.getMapFilePoint(point.downRight());

            System.out.print(" - " + point + " available: " + mapFilePoint.getBuildableSite() +
                    ", height: " + mapFilePoint.getHeight() +
                    ", height differences:");

            if (spotLeft != null) {
                System.out.print(" " + (mapFilePoint.getHeight() - spotLeft.getHeight()));
            }

            if (spotUpLeft != null) {
                System.out.print(" " + (mapFilePoint.getHeight() - spotUpLeft.getHeight()));
            }

            if (spotDownLeft!= null) {
                System.out.print(" " + (mapFilePoint.getHeight() - spotDownLeft.getHeight()));
            }

            if (spotRight != null) {
                System.out.print(" " + (mapFilePoint.getHeight() - spotRight.getHeight()));
            }

            if (spotUpRight != null) {
                System.out.print(" " + (mapFilePoint.getHeight() - spotUpRight.getHeight()));
            }

            if (spotDownRight!= null) {
                System.out.print(" " + (mapFilePoint.getHeight() - spotDownRight.getHeight()));
            }

            System.out.println();
        }
    }

    /**
     * Prints information about a specific point in the map file.
     *
     * @param mapFilePoint the point to print
     */
    public void printMapFilePoint(MapFilePoint mapFilePoint) {
        if (mapFilePoint.hasTree()) {
            System.out.println(" - Tree");
        }

        if (mapFilePoint.hasStone()) {
            System.out.println(" - Stone");
        }

        if (mapFilePoint.getBuildableSite() != null) {
            System.out.printf(" - Can build: %s%n", mapFilePoint.getBuildableSite());
        }
    }

    /**
     * Prints the surrounding terrain from the map file.
     *
     * @param point   the central point
     * @param mapFile the map file
     */
    public void printSurroundingTerrainFromMapFile(Point point, MapFile mapFile) {
        System.out.printf("""
                - Surrounding terrain:
                  -- Above 1: %s %s %s %s %s
                  -- Above 2: %s %s %s %s
                  -- Below 1: %s %s %s %s %s
                  -- Below 2: %s %s %s %s
                """,
                mapFile.getMapFilePoint(point.upLeft().upLeft()).getVegetationBelow(),
                mapFile.getMapFilePoint(point.upLeft().upLeft()).getVegetationDownRight(),
                mapFile.getMapFilePoint(point.up()).getVegetationBelow(),
                mapFile.getMapFilePoint(point.up()).getVegetationDownRight(),
                mapFile.getMapFilePoint(point.upRight().upRight()).getVegetationBelow(),
                mapFile.getMapFilePoint(point.upLeft()).getVegetationBelow(),
                mapFile.getMapFilePoint(point.upLeft()).getVegetationDownRight(),
                mapFile.getMapFilePoint(point.upRight()).getVegetationBelow(),
                mapFile.getMapFilePoint(point.upRight()).getVegetationDownRight(),
                mapFile.getMapFilePoint(point.left()).getVegetationBelow(),
                mapFile.getMapFilePoint(point.left()).getVegetationDownRight(),
                mapFile.getMapFilePoint(point).getVegetationBelow(),
                mapFile.getMapFilePoint(point).getVegetationDownRight(),
                mapFile.getMapFilePoint(point.right()).getVegetationBelow(),
                mapFile.getMapFilePoint(point.downLeft()).getVegetationBelow(),
                mapFile.getMapFilePoint(point.downLeft()).getVegetationDownRight(),
                mapFile.getMapFilePoint(point.downRight()).getVegetationBelow(),
                mapFile.getMapFilePoint(point.downRight()).getVegetationDownRight()
        );
    }

    /**
     * Prints surrounding available buildings from the map file.
     *
     * @param point   the central point
     * @param mapFile the map file
     */
    public void printSurroundingAvailableBuildingsFromMapFile(Point point, MapFile mapFile) {
        System.out.printf("""
                - Surrounding available buildings:
                  -- Above 1: %-20s %-20s %-20s
                  -- Above 2:           %-20s %-20s
                  -- Same:    %-20s POINT  %-20s
                  -- Below 1:           %-20s %-20s
                  -- Below 2: %-20s %-20s %-20s
                """,
                mapFile.getMapFilePoint(point.upLeft().upLeft()).getBuildableSite(),
                mapFile.getMapFilePoint(point.up()).getBuildableSite(),
                mapFile.getMapFilePoint(point.upRight().upRight()).getBuildableSite(),
                mapFile.getMapFilePoint(point.upLeft()).getBuildableSite(),
                mapFile.getMapFilePoint(point.upRight()).getBuildableSite(),
                mapFile.getMapFilePoint(point.left()).getBuildableSite(),
                mapFile.getMapFilePoint(point.right()).getBuildableSite(),
                mapFile.getMapFilePoint(point.downLeft()).getBuildableSite(),
                mapFile.getMapFilePoint(point.downRight()).getBuildableSite(),
                mapFile.getMapFilePoint(point.downLeft().downLeft()).getBuildableSite(),
                mapFile.getMapFilePoint(point.down()).getBuildableSite(),
                mapFile.getMapFilePoint(point.downRight().downRight()).getBuildableSite()
        );
    }

    /**
     * Prints surrounding stones and trees from the map file.
     *
     * @param point   the central point
     * @param mapFile the map file
     */
    public void printSurroundingStonesAndTreesFromMapFile(Point point, MapFile mapFile) {
        System.out.printf("""
                - Surrounding stones and trees:
                  -- Above 1: %s %s %s
                  -- Above 2: %s %s
                  -- Same:    %s POINT %s
                  -- Below 1: %s %s
                  -- Below 2: %s %s %s
                """,
                treeOrStoneOrNoneToString(mapFile, point.upLeft().upLeft()),
                treeOrStoneOrNoneToString(mapFile, point.up()),
                treeOrStoneOrNoneToString(mapFile, point.upRight().upRight()),
                treeOrStoneOrNoneToString(mapFile, point.upLeft()),
                treeOrStoneOrNoneToString(mapFile, point.upRight()),
                treeOrStoneOrNoneToString(mapFile, point.left()),
                treeOrStoneOrNoneToString(mapFile, point.right()),
                treeOrStoneOrNoneToString(mapFile, point.downLeft()),
                treeOrStoneOrNoneToString(mapFile, point.downRight()),
                treeOrStoneOrNoneToString(mapFile, point.downLeft().downLeft()),
                treeOrStoneOrNoneToString(mapFile, point.down()),
                treeOrStoneOrNoneToString(mapFile, point.downRight().downRight())
        );
    }

    /**
     * Prints surrounding heights from the map file.
     *
     * @param point   the central point
     * @param mapFile the map file
     */
    public void printSurroundingHeightsFromMapFile(Point point, MapFile mapFile) {
        System.out.printf("""
                - Surrounding heights:
                  -- Above 1: %d %d %d
                  -- Above 2: %d %d
                  -- Same:    %d %d %d
                  -- Below 1: %d %d
                  -- Below 2: %d %d %d
                """,
                mapFile.getMapFilePoint(point.upLeft().upLeft()).getHeight(),
                mapFile.getMapFilePoint(point.up()).getHeight(),
                mapFile.getMapFilePoint(point.upRight().upRight()).getHeight(),
                mapFile.getMapFilePoint(point.upLeft()).getHeight(),
                mapFile.getMapFilePoint(point.upRight()).getHeight(),
                mapFile.getMapFilePoint(point.left()).getHeight(),
                mapFile.getMapFilePoint(point).getHeight(),
                mapFile.getMapFilePoint(point.right()).getHeight(),
                mapFile.getMapFilePoint(point.downLeft()).getHeight(),
                mapFile.getMapFilePoint(point.downRight()).getHeight(),
                mapFile.getMapFilePoint(point.downLeft().downLeft()).getHeight(),
                mapFile.getMapFilePoint(point.down()).getHeight(),
                mapFile.getMapFilePoint(point.downRight().downRight()).getHeight()
        );
    }

    /**
     * Prints a point from the game map.
     *
     * @param point the point
     * @param map   the game map
     */
    public void printGameMapPoint(Point point, GameMap map) {
        if (map.isTreeAtPoint(point)) {
            System.out.println(" - Tree");
        }

        if (map.isStoneAtPoint(point)) {
            System.out.println(" - Stone");
        }
    }

    /**
     * Prints surrounding terrain from the game map.
     *
     * @param point the central point
     * @param map   the game map
     */
    public void printSurroundingTerrainFromGameMap(Point point, GameMap map) {
        System.out.printf("""
                - Surrounding terrain:
                  -- Above 1: %s %s %s
                  -- Above 2: %s %s %s
                  -- Above 3: %s %s %s
                  -- Below 1: %s %s %s
                  -- Below 2: %s %s %s
                  -- Below 3: %s %s %s
                """,
                map.getVegetationUpLeft(point.up()),
                map.getVegetationAbove(point.up()),
                map.getVegetationUpRight(point.up()),
                map.getVegetationDownLeft(point.up()),
                map.getVegetationBelow(point.up()),
                map.getVegetationDownRight(point.up()),
                map.getVegetationUpLeft(point),
                map.getVegetationAbove(point),
                map.getVegetationUpRight(point),
                map.getVegetationDownLeft(point),
                map.getVegetationBelow(point),
                map.getVegetationDownRight(point),
                map.getVegetationUpLeft(point.down()),
                map.getVegetationAbove(point.down()),
                map.getVegetationUpRight(point.down()),
                map.getVegetationDownLeft(point.down()),
                map.getVegetationBelow(point.down()),
                map.getVegetationDownRight(point.down())
        );
    }

    /**
     * Prints surrounding available buildings from the game map.
     *
     * @param point                the central point
     * @param availableHousePoints available house points
     */
    public void printSurroundingAvailableBuildingsFromGameMap(Point point, Map<Point, Size> availableHousePoints) {
        System.out.printf("""
                - Surrounding available buildings:
                  -- Above 1: %-20s %-20s %-20s
                  -- Above 2:           %-20s %-20s
                  -- Same:    %-20s POINT %-20s
                  -- Below 1:           %-20s %-20s
                  -- Below 2: %-20s %-20s %-20s
                """,
                availableHousePoints.get(point.upLeft().upLeft()),
                availableHousePoints.get(point.up()),
                availableHousePoints.get(point.upRight().upRight()),
                availableHousePoints.get(point.upLeft()),
                availableHousePoints.get(point.upRight()),
                availableHousePoints.get(point.left()),
                availableHousePoints.get(point.right()),
                availableHousePoints.get(point.downLeft()),
                availableHousePoints.get(point.downRight()),
                availableHousePoints.get(point.downLeft().downLeft()),
                availableHousePoints.get(point.down()),
                availableHousePoints.get(point.downRight().downRight())
        );
    }
}
