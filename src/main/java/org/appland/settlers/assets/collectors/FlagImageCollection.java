package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.Bitmap;
import org.appland.settlers.assets.ImageBoard;
import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.NormalizedImageList;
import org.appland.settlers.assets.Palette;
import org.appland.settlers.model.FlagType;
import org.json.simple.JSONObject;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class FlagImageCollection {
    private final Map<Nation, Map<FlagType, List<Bitmap>>> flagMap;
    private final Map<Nation, Map<FlagType, List<Bitmap>>> flagShadowMap;

    public FlagImageCollection() {
        flagMap = new EnumMap<>(Nation.class);
        flagShadowMap = new EnumMap<>(Nation.class);

        for (Nation nation : Nation.values()) {

            flagMap.put(nation, new EnumMap<>(FlagType.class));
            flagShadowMap.put(nation, new EnumMap<>(FlagType.class));

            for (FlagType flagType : FlagType.values()) {
                flagMap.get(nation).put(flagType, new ArrayList<>());
                flagShadowMap.get(nation).put(flagType, new ArrayList<>());
            }
        }
    }

    public void writeImageAtlas(String directory, Palette palette) throws IOException {

        // Write the image atlas, one row per flag, and collect metadata to write as json
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();

        Point cursor = new Point(0, 0);
        for (Nation nation : Nation.values()) {

            JSONObject jsonNationInfo = new JSONObject();

            jsonImageAtlas.put(nation.name().toUpperCase(), jsonNationInfo);

            cursor.x = 0;

            // Add each flag type for the nation
            for (FlagType flagType : FlagType.values()) {

                JSONObject jsonFlagType = new JSONObject();

                jsonNationInfo.put(flagType.name().toUpperCase(), jsonFlagType);

                // Flag image
                List<Bitmap> images = this.flagMap.get(nation).get(flagType);
                NormalizedImageList normalizedFlagImageList = new NormalizedImageList(images);
                List<Bitmap> normalizedFlagImages = normalizedFlagImageList.getNormalizedImages();

                imageBoard.placeImageSeries(normalizedFlagImages, cursor, ImageBoard.LayoutDirection.ROW);


                JSONObject jsonFlagInfo = imageBoard.imageSeriesLocationToJson(normalizedFlagImages);

                jsonFlagType.put("images", jsonFlagInfo);

                cursor.x = cursor.x + normalizedFlagImageList.size() * normalizedFlagImageList.getImageWidth();

                // Flag shadow image
                List<Bitmap> normalFlagShadowImages = this.flagShadowMap.get(nation).get(flagType);
                NormalizedImageList normalizedFlagShadowImageList = new NormalizedImageList(normalFlagShadowImages);
                List<Bitmap> normalizedFlagShadowImages = normalizedFlagShadowImageList.getNormalizedImages();

                imageBoard.placeImageSeries(normalizedFlagShadowImages, cursor, ImageBoard.LayoutDirection.ROW);

                JSONObject jsonFlagShadowInfo = imageBoard.imageSeriesLocationToJson(normalizedFlagShadowImages);

                jsonFlagType.put("shadows", jsonFlagShadowInfo);

                cursor.x = 0;
                cursor.y = cursor.y + Math.max(normalizedFlagImageList.getImageHeight(), normalizedFlagShadowImageList.getImageHeight());
            }
        }

        imageBoard.writeBoardToBitmap(palette).writeToFile(directory + "/" + "image-atlas-flags.png");

        // Write a JSON file that specifies where each image is in pixels
        Path filePath = Paths.get(directory, "image-atlas-flags.json");

        Files.writeString(filePath, jsonImageAtlas.toJSONString());
    }

    public void addImagesForFlag(Nation nation, FlagType flagType, List<Bitmap> images) {
        this.flagMap.get(nation).get(flagType).addAll(images);
    }

    public void addImagesForFlagShadow(Nation nation, FlagType flagType, List<Bitmap> images) {
        this.flagShadowMap.get(nation).get(flagType).addAll(images);
    }
}
