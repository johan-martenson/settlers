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
    private final Map<Nation, BorderForNation> borderMap;

    public BorderImageCollector() {
        borderMap = new EnumMap<>(Nation.class);

        for (Nation nation : Nation.values()) {
            borderMap.put(nation, new BorderForNation());
        }
    }

    public void addLandBorderImage(Nation nation, PlayerBitmap image) {
        borderMap.get(nation).setLandBorder(image);
    }

    public void addWaterBorderImage(Nation nation, PlayerBitmap image) {
        borderMap.get(nation).setCoastBorder(image);
    }

    public void writeImageAtlas(String toDir, Palette palette) throws IOException {
        ImageBoard imageBoard = new ImageBoard();

        Arrays.stream(Nation.values()).forEach(nation -> Arrays.stream(PlayerColor.values())
                .forEach(playerColor -> imageBoard.placeImagesAsRow(
                        List.of(
                                ImageBoard.makeImagePathPair(
                                        borderMap.get(nation).landBorder.getBitmapForPlayer(playerColor),
                                        nation.name().toUpperCase(),
                                        playerColor.name().toUpperCase(),
                                        "landBorder"
                                ),
                                ImageBoard.makeImagePathPair(
                                        borderMap.get(nation).coastBorder.getBitmapForPlayer(playerColor),
                                        nation.name().toUpperCase(),
                                        playerColor.name().toUpperCase(),
                                        "coastBorder"
                                )))));

        imageBoard.writeBoard(toDir, "image-atlas-border", palette);
    }

    private static class BorderForNation {
        private PlayerBitmap landBorder;
        private PlayerBitmap coastBorder;

        public void setLandBorder(PlayerBitmap image) {
            landBorder = image;
        }

        public void setCoastBorder(PlayerBitmap image) {
            coastBorder = image;
        }
    }
}
