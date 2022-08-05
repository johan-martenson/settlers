package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.Bitmap;
import org.appland.settlers.assets.CropType;
import org.appland.settlers.assets.ImageBoard;
import org.appland.settlers.assets.Palette;
import org.appland.settlers.model.Crop;
import org.json.simple.JSONObject;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;

public class CropImageCollection {
    private final Map<CropType, Map<Crop.GrowthState, Bitmap>> cropMap;
    private final Map<CropType, Map<Crop.GrowthState, Bitmap>> cropShadowMap;

    public CropImageCollection() {
        cropMap = new EnumMap<>(CropType.class);
        cropShadowMap = new EnumMap<>(CropType.class);

        for (CropType type : CropType.values()) {
            cropMap.put(type, new EnumMap<>(Crop.GrowthState.class));
            cropShadowMap.put(type, new EnumMap<>(Crop.GrowthState.class));
        }
    }

    public void addImage(CropType type, Crop.GrowthState growth, Bitmap image) {
        cropMap.get(type).put(growth, image);
    }

    public void writeImageAtlas(String toDir, Palette palette) throws IOException {

        // Create the image atlas
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();

        // Fill in the image atlas
        Point cursor = new Point(0, 0);
        int maxHeightRow = 0;

        // Make two rows, one for each crop type
        for (Map.Entry<CropType, Map<Crop.GrowthState, Bitmap>> entryForCropType : this.cropMap.entrySet()) {

            JSONObject jsonCropType = new JSONObject();

            jsonImageAtlas.put(entryForCropType.getKey().name().toUpperCase(), jsonCropType);

            cursor.x = 0;

            // Add the crop images
            for (Crop.GrowthState cropGrowth : Crop.GrowthState.values()) {

                Bitmap image = entryForCropType.getValue().get(cropGrowth);

                imageBoard.placeImage(image, cursor);

                JSONObject jsonCropGrowthState = new JSONObject();
                JSONObject jsonCropImage = imageBoard.imageLocationToJson(image);

                jsonCropType.put(cropGrowth.name().toUpperCase(), jsonCropGrowthState);
                jsonCropGrowthState.put("image", jsonCropImage);

                maxHeightRow = Math.max(maxHeightRow, image.getHeight());

                cursor.x = cursor.x + image.getWidth();
            }

            // Add the crop shadow images
            for (Crop.GrowthState cropGrowth : Crop.GrowthState.values()) {

                Bitmap image = this.cropShadowMap.get(entryForCropType.getKey()).get(cropGrowth);

                imageBoard.placeImage(image, cursor);

                JSONObject jsonCropShadowImage = imageBoard.imageLocationToJson(image);

                ((JSONObject) jsonCropType.get(cropGrowth.name().toUpperCase())).put("shadowImage", jsonCropShadowImage);

                maxHeightRow = Math.max(maxHeightRow, image.getHeight());

                cursor.x = cursor.x + image.getWidth();
            }

            cursor.y = cursor.y + maxHeightRow;
        }

        // Write the image atlas to file(s)
        imageBoard.writeBoardToBitmap(palette).writeToFile(toDir + "/image-atlas-crops.png");

        Files.writeString(Paths.get(toDir, "image-atlas-crops.json"), jsonImageAtlas.toJSONString());
    }

    public void addShadowImage(CropType cropType, Crop.GrowthState growthState, Bitmap image) {
        cropShadowMap.get(cropType).put(growthState, image);
    }
}
