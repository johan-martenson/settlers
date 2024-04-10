package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.model.PlayerColor;
import org.json.simple.JSONObject;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        // Create the image atlas
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();

        // Fill in the image atlas
        Point cursor = new Point(0, 0);
        for (Nation nation : Nation.values()) {
            cursor.x = 0;

            JSONObject jsonNation = new JSONObject();

            jsonImageAtlas.put(nation.name().toUpperCase(), jsonNation);

            for (var playerColor : PlayerColor.values()) {
                JSONObject jsonPlayer = new JSONObject();

                jsonNation.put(playerColor.name().toUpperCase(), jsonPlayer);

                BorderForNation borderForNation = borderMap.get(nation);

                // Land border
                jsonPlayer.put("landBorder", imageBoard.placeImage(borderForNation.landBorder.getBitmapForPlayer(playerColor), cursor));

                cursor.x = cursor.x + borderForNation.landBorder.getWidth();

                // Coast border
                jsonPlayer.put("coastBorder", imageBoard.placeImage(borderForNation.coastBorder.getBitmapForPlayer(playerColor), cursor));

                cursor.y = cursor.y + Math.max(borderForNation.landBorder.getHeight(), borderForNation.coastBorder.getHeight());
            }
        }

        // Write to file
        imageBoard.writeBoardToBitmap(palette).writeToFile(toDir + "/image-atlas-border.png");

        Files.writeString(Paths.get(toDir, "image-atlas-border.json"), jsonImageAtlas.toJSONString());
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
