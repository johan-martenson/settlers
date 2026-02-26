package org.appland.settlers.maps.utils;

import org.appland.settlers.maps.MapLoader;
import org.appland.settlers.maps.Texture;
import org.appland.settlers.maps.Utils;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Vegetation;

import java.util.List;

public class AsciiDrawer {

    public static void main(String[] args) throws Exception {
        var mapLoader = new MapLoader();

        // Load mapfile
        var mapFile = mapLoader.loadMapFromFile(args[0]);

        var map = mapLoader.convertMapFileToGameMap(mapFile);

        // Draw the legend
        drawLegend();

        // Draw the map file
        System.out.println();
        System.out.println("Map file");
        drawAsciiFromTileArrays(mapFile.getWidth(), mapFile.getHeight(), mapFile.getTilesBelow(), mapFile.getTilesDownRight());

        // Draw the game map
        System.out.println();
        System.out.println();
        System.out.println("Game map");
        drawAsciiFromGameMap(mapFile.getWidth(), mapFile.getHeight(), map);

    }

    private static final boolean USE_COLOR = true;

    private static final String RESET = "\u001B[0m";

    private static final String BLUE   = "\u001B[34m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String WHITE  = "\u001B[37m";
    private static final String CYAN   = "\u001B[36m";
    private static final String RED    = "\u001B[31m";
    private static final String DIM = "\u001B[2m";

    private static String dim(String text) {
        if (!USE_COLOR) return text;
        return DIM + text + RESET;
    }

    private static String color(String ansi, String text) {
        if (!USE_COLOR) return text;
        return ansi + text + RESET;
    }

    static private void drawLegend() {
        System.out.println("Legend:");

        for (var vegetation : Vegetation.values()) {
            System.out.println(" - " + vegetation.name() + ": " + terrainSymbol(vegetation));
        }
    }

    public static void drawAsciiFromTileArrays(
            int fileWidth,
            int fileHeight,
            List<Texture> tilesBelow,
            List<Texture> tilesDownRight
    ) {
        System.out.println("=== FILE SPACE (TIGHT) ===");

        for (int fileY = 0; fileY < fileHeight; fileY++) {

            boolean oddRow = (fileY & 1) == 1;

            if (oddRow) System.out.print("  "); // reduced indent

            // ---- Upward triangles ----
            for (int fileX = 0; fileX < fileWidth; fileX++) {

                int index = fileY * fileWidth + fileX;

                if (fileX + 1 < fileWidth && fileY + 1 < fileHeight) {
                    String s = terrainSymbol(Utils.convertTextureToVegetation(tilesDownRight.get(index)));
                    System.out.print(
                            dim("/") +
                                    s +
                                    dim("\\")
                    );
                }
            }

            System.out.println();

            if (oddRow) System.out.print("  ");

            // ---- Downward triangles ----
            for (int fileX = 0; fileX < fileWidth; fileX++) {

                int index = fileY * fileWidth + fileX;

                if (fileY + 1 < fileHeight) {

                    boolean valid = ((fileY & 1) == 0)
                            ? fileX + 1 < fileWidth
                            : fileX - 1 >= 0;

                    if (valid) {
                        String s = terrainSymbol(Utils.convertTextureToVegetation(tilesBelow.get(index)));
                        System.out.print(
                                dim("\\") +
                                        s +
                                        dim("/")
                        );
                    }
                }
            }

            System.out.println();
        }

        System.out.println();
    }

    public static void drawAsciiFromGameMap(
            int fileWidth,
            int fileHeight,
            GameMap map
    ) {
        System.out.println("=== ENGINE SPACE (TIGHT) ===");

        for (int fileY = 0; fileY < fileHeight; fileY++) {

            boolean oddRow = (fileY & 1) == 1;

            if (oddRow) System.out.print("  ");

            // Upward
            for (int fileX = 0; fileX < fileWidth; fileX++) {

                if (fileX + 1 < fileWidth && fileY + 1 < fileHeight) {

                    var engine =
                            GeometryMapping.mapFilePointToGamePoint(fileX, fileY, fileHeight);

                    String s = terrainSymbol(map.getVegetationDownRight(engine));
                    System.out.print(
                            dim("/") +
                                    s +
                                    dim("\\")
                    );
                }
            }

            System.out.println();

            if (oddRow) System.out.print("  ");

            // Downward
            for (int fileX = 0; fileX < fileWidth; fileX++) {

                if (fileY + 1 < fileHeight) {

                    boolean valid = ((fileY & 1) == 0)
                            ? fileX + 1 < fileWidth
                            : fileX - 1 >= 0;

                    if (valid) {

                        var engine =
                                GeometryMapping.mapFilePointToGamePoint(fileX, fileY, fileHeight);

                        String s = terrainSymbol(map.getVegetationBelow(engine));
                        System.out.print(
                                dim("\\") +
                                        s +
                                        dim("/")
                        );
                    }
                }
            }

            System.out.println();
        }

        System.out.println();
    }

    private static String terrainSymbol(Vegetation v) {
        if (v == null) return "  ";

        return switch (v) {

            // 🌊 WATER
            case WATER, WATER_2 ->
                    color(BLUE, "~~");

            case BUILDABLE_WATER ->
                    color(CYAN, "≈≈");

            // 🌿 MEADOW / GREEN
            case MEADOW_1, MEADOW_2, MEADOW_3 ->
                    color(GREEN, "..");

            case FLOWER_MEADOW ->
                    color(GREEN, "**");

            case MOUNTAIN_MEADOW ->
                    color(GREEN, "^.");

            // 🏔 MOUNTAINS
            case MOUNTAIN_1, MOUNTAIN_2, MOUNTAIN_3, MOUNTAIN_4 ->
                    color(WHITE, "^^");

            case BUILDABLE_MOUNTAIN ->
                    color(YELLOW, "△△");

            // ❄ SNOW
            case SNOW ->
                    color(WHITE, "**");

            // 🌾 STEPPE / SAVANNAH
            case SAVANNAH ->
                    color(YELLOW, "::");

            case STEPPE ->
                    color(YELLOW, "..");

            // 🏜 DESERT
            case DESERT_1, DESERT_2 ->
                    color(YELLOW, "##");

            // 🌫 SWAMP
            case SWAMP ->
                    color(CYAN, "%%");

            // 🌋 LAVA
            case LAVA_1 ->
                    color(RED, "~~");

            case LAVA_2 ->
                    color(RED, "==");

            case LAVA_3 ->
                    color(RED, "##");

            case LAVA_4 ->
                    color(RED, "!!");

            // 🧪 Special / debug
            case MAGENTA ->
                    color("\u001B[35m", "MM");

            default ->
                    color(RED, "??");
        };
    }}
