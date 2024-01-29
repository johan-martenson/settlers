package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.StoneAmount;
import org.appland.settlers.model.StoneType;
import org.json.simple.JSONObject;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;

public class StonesImageCollection {
    private final Map<StoneType, Map<StoneAmount, Bitmap>> stoneMap;
    private final Map<StoneType, Map<StoneAmount, Bitmap>> stoneShadowMap;

    public StonesImageCollection() {
        stoneMap = new EnumMap<>(StoneType.class);
        stoneShadowMap = new EnumMap<>(StoneType.class);

        for (StoneType type : StoneType.values()) {
            stoneMap.put(type, new EnumMap<>(StoneAmount.class));
            stoneShadowMap.put(type, new EnumMap<>(StoneAmount.class));
        }
    }

    public void addImage(StoneType type, StoneAmount amount, Bitmap image) {
        stoneMap.get(type).put(amount, image);
    }

    public void addShadowImage(StoneType type, StoneAmount amount, Bitmap image) {
        stoneShadowMap.get(type).put(amount, image);
    }

    public void writeImageAtlas(String toDir, Palette palette) throws IOException {

        // Create the image atlas
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();

        // Fill in the image atlas
        Point cursor = new Point(0, 0);

        for (StoneType stoneType : StoneType.values()) {

            int rowHeight = 0;

            cursor.x = 0;

            JSONObject jsonStoneType = new JSONObject();

            jsonImageAtlas.put(stoneType.name().toUpperCase(), jsonStoneType);

            // Stone images for the stone type
            for (StoneAmount stoneAmount : StoneAmount.values()) {

                Bitmap image = this.stoneMap.get(stoneType).get(stoneAmount);

                imageBoard.placeImage(image, cursor);

                JSONObject jsonStoneAmount = new JSONObject();
                JSONObject jsonStoneImage = imageBoard.imageLocationToJson(image);

                jsonStoneAmount.put("image", jsonStoneImage);
                jsonStoneType.put(stoneAmount.name().toUpperCase(), jsonStoneAmount);

                rowHeight = Math.max(rowHeight, image.getHeight());

                cursor.x = cursor.x + image.getWidth();
            }

            // Stone shadow images for the stone type
            for (StoneAmount stoneAmount : StoneAmount.values()) {

                Bitmap shadowImage = this.stoneShadowMap.get(stoneType).get(stoneAmount);

                imageBoard.placeImage(shadowImage, cursor);

                JSONObject jsonShadowImage = imageBoard.imageLocationToJson(shadowImage);

                ((JSONObject) jsonStoneType.get(stoneAmount.name().toUpperCase())).put("shadowImage", jsonShadowImage);

                rowHeight = Math.max(rowHeight, shadowImage.getHeight());

                cursor.x = cursor.x + shadowImage.getWidth();
            }

            cursor.y = cursor.y + rowHeight;
        }

        // Write the image atlas to disk
        imageBoard.writeBoardToBitmap(palette).writeToFile(toDir + "/image-atlas-stones.png");

        Files.writeString(Paths.get(toDir, "image-atlas-stones.json"), jsonImageAtlas.toJSONString());
    }
}
