package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.model.PlayerColor;

import java.awt.Point;
import java.io.IOException;
import java.util.EnumMap;
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

        Point cursor = new Point(0, 0);
        for (Nation nation : Nation.values()) {
            cursor.x = 0;

            for (var playerColor : PlayerColor.values()) {
                BorderForNation borderForNation = borderMap.get(nation);

                imageBoard.placeImage(
                        borderForNation.landBorder.getBitmapForPlayer(playerColor),
                        cursor,
                        nation.name().toUpperCase(),
                        playerColor.name().toUpperCase(),
                        "landBorder"
                );

                cursor.x = cursor.x + borderForNation.landBorder.getWidth();

                imageBoard.placeImage(
                        borderForNation.coastBorder.getBitmapForPlayer(playerColor),
                        cursor,
                        nation.name().toUpperCase(),
                        playerColor.name().toUpperCase(),
                        "coastBorder"
                        );

                cursor.y = cursor.y + Math.max(borderForNation.landBorder.getHeight(), borderForNation.coastBorder.getHeight());
            }
        }

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
