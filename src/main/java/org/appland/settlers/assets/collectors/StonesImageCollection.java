package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.StoneAmount;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.model.StoneType;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
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
        Arrays.stream(StoneType.values()).forEach(stoneType -> {
            JSONObject jsonStoneType = new JSONObject();

            jsonImageAtlas.put(stoneType.name().toUpperCase(), jsonStoneType);

            Arrays.stream(StoneAmount.values()).forEach(stoneAmount -> {
                JSONObject jsonStoneAmount = new JSONObject();

                jsonStoneType.put(stoneAmount.name().toUpperCase(), jsonStoneAmount);

                jsonStoneAmount.put("image", imageBoard.placeImageBottom(stoneMap.get(stoneType).get(stoneAmount)));
                jsonStoneAmount.put("shadowImage", imageBoard.placeImageBottom(stoneShadowMap.get(stoneType).get(stoneAmount)));
            });
        });

        // Write the image atlas to disk
        imageBoard.writeBoardToBitmap(palette).writeToFile(toDir + "/image-atlas-stones.png");

        Files.writeString(Paths.get(toDir, "image-atlas-stones.json"), jsonImageAtlas.toJSONString());
    }
}
