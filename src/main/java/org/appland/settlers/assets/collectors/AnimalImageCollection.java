package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.Bitmap;
import org.appland.settlers.assets.CompassDirection;
import org.appland.settlers.assets.ImageBoard;
import org.appland.settlers.assets.NormalizedImageList;
import org.appland.settlers.assets.Palette;
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

public class AnimalImageCollection {
    private final String name;
    private final Map<CompassDirection, List<Bitmap>> directionToImageMap;
    private final Map<CompassDirection, Bitmap> shadowImages;

    public AnimalImageCollection(String name) {
        this.name = name;
        directionToImageMap = new EnumMap<>(CompassDirection.class);

        for (CompassDirection compassDirection : CompassDirection.values()) {
            this.directionToImageMap.put(compassDirection, new ArrayList<>());
        }
        shadowImages = new EnumMap<>(CompassDirection.class);
    }

    public void addImage(CompassDirection compassDirection, Bitmap workerImage) {
        this.directionToImageMap.get(compassDirection).add(workerImage);
    }

    public void writeImageAtlas(String directory, Palette palette) throws IOException {

        // Write the image atlas, one row per direction, and collect metadata to write as json
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();
        JSONObject jsonImages = new JSONObject();

        jsonImageAtlas.put("images", jsonImages);

        // Fill in the images into the image atlas
        Point cursor = new Point(0, 0);

        // Fill in animal walking in each direction
        for (CompassDirection compassDirection : CompassDirection.values()) {

            cursor.x = 0;

            List<Bitmap> directionImages = directionToImageMap.get(compassDirection);
            NormalizedImageList directionNormalizedList = new NormalizedImageList(directionImages);
            List<Bitmap> normalizedDirectionImages = directionNormalizedList.getNormalizedImages();

            imageBoard.placeImageSeries(normalizedDirectionImages, cursor, ImageBoard.LayoutDirection.ROW);

            JSONObject jsonDirectionInfo = imageBoard.imageSeriesLocationToJson(normalizedDirectionImages);

            jsonImages.put(compassDirection.name().toUpperCase(), jsonDirectionInfo);

            cursor.y = cursor.y + directionNormalizedList.getImageHeight();
        }

        // Fill in shadows if they exist. One per direction
        if (!shadowImages.isEmpty()) {
            cursor.x = 0;

            JSONObject jsonShadowImages = new JSONObject();

            jsonImageAtlas.put("shadowImages", jsonShadowImages);

            for (Map.Entry<CompassDirection, Bitmap> entry : shadowImages.entrySet()) {
                CompassDirection compassDirection = entry.getKey();
                Bitmap shadowImage = entry.getValue();

                imageBoard.placeImage(shadowImage, cursor);

                jsonShadowImages.put(compassDirection.name().toUpperCase(), imageBoard.imageLocationToJson(shadowImage));

                cursor.x = cursor.x + shadowImage.getWidth();
            }
        }

        // Write the image atlas
        imageBoard.writeBoardToBitmap(palette).writeToFile(directory + "/" + "image-atlas-" + name.toLowerCase() + ".png");

        Path filePath = Paths.get(directory, "image-atlas-" + name.toLowerCase() + ".json");

        Files.writeString(filePath, jsonImageAtlas.toJSONString());
    }

    public void addImages(CompassDirection compassDirection, List<Bitmap> images) {
        this.directionToImageMap.get(compassDirection).addAll(images);
    }

    public void addShadowImage(CompassDirection compassDirection, Bitmap image) {
        shadowImages.put(compassDirection, image);
    }
}
