package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.model.PlayerColor;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BorderImageCollector {
    private final Map<Nation, BorderForNation> borderMap = new EnumMap<>(Nation.class);

    /**
     * Adds a summer border image for a specific nation.
     *
     * @param nation the nation
     * @param image  the image to be added
     */
    public void addSummerBorderImage(Nation nation, PlayerBitmap image) {
        borderMap.computeIfAbsent(nation, k -> new BorderForNation()).setSummerBorder(image);
    }

    /**
     * Adds a winter border image for a specific nation.
     *
     * @param nation the nation
     * @param image  the image to be added
     */
    public void addWinterBorderImage(Nation nation, PlayerBitmap image) {
        borderMap.computeIfAbsent(nation, k -> new BorderForNation()).setCoastBorder(image);
    }

    /**
     * Writes the image atlas to the specified directory using the given palette.
     *
     * @param toDir   the directory to save the atlas
     * @param palette the palette to use for the images
     * @throws IOException if an I/O error occurs
     */
    public void writeImageAtlas(String toDir, Palette palette) throws IOException {
        ImageBoard imageBoard = new ImageBoard();

        Arrays.stream(Nation.values())
                .forEach(nation -> Arrays.stream(PlayerColor.values())
                        .forEach(playerColor -> imageBoard.placeImagesAsRow(
                                List.of(
                                        ImageBoard.makeImagePathPair(
                                                borderMap.get(nation).landBorder.getBitmapForPlayer(playerColor),
                                                nation.name().toUpperCase(),
                                                playerColor.name().toUpperCase(),
                                                "summerBorder"
                                        ),
                                        ImageBoard.makeImagePathPair(
                                                borderMap.get(nation).coastBorder.getBitmapForPlayer(playerColor),
                                                nation.name().toUpperCase(),
                                                playerColor.name().toUpperCase(),
                                                "winterBorder"
                                        )))));

        imageBoard.writeBoard(toDir, "image-atlas-border", palette);
    }

    private static class BorderForNation {
        private PlayerBitmap landBorder;
        private PlayerBitmap coastBorder;

        public void setSummerBorder(PlayerBitmap image) {
            landBorder = image;
        }

        public void setCoastBorder(PlayerBitmap image) {
            coastBorder = image;
        }
    }
}
