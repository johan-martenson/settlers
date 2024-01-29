package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.CompassDirection;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.ImageWithShadow;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.ShipConstructionProgress;
import org.json.simple.JSONObject;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;

public class ShipImageCollection {

    private final Map<CompassDirection, ImageWithShadow> images;
    private final Map<ShipConstructionProgress, ImageWithShadow> underConstructionImages;

    public ShipImageCollection() {
        images = new EnumMap<>(CompassDirection.class);
        underConstructionImages = new EnumMap<>(ShipConstructionProgress.class);
    }

    public void addShipImageWithShadow(CompassDirection compassDirection, Bitmap image, Bitmap shadowImage) {
        this.images.put(compassDirection, new ImageWithShadow(image, shadowImage));
    }

    public void writeImageAtlas(String toDir, Palette palette) throws IOException {

        // Create the image atlas
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();
        JSONObject jsonReady = new JSONObject();
        JSONObject jsonUnderConstruction = new JSONObject();

        jsonImageAtlas.put("ready", jsonReady);
        jsonImageAtlas.put("underConstruction", jsonUnderConstruction);

        // Fill in the image atlas
        Point cursor = new Point(0, 0);

        for (Map.Entry<CompassDirection, ImageWithShadow> entry : images.entrySet()) {
            CompassDirection compassDirection = entry.getKey();
            ImageWithShadow imageWithShadow = entry.getValue();

            cursor.x = 0;

            JSONObject jsonDirection = new JSONObject();

            jsonReady.put(compassDirection.name().toUpperCase(), jsonDirection);

            // Place the image
            imageBoard.placeImage(imageWithShadow.image, cursor);

            jsonDirection.put("image", imageBoard.imageLocationToJson(imageWithShadow.image));

            cursor.x = cursor.x + imageWithShadow.image.getWidth();

            // Place the shadow image
            imageBoard.placeImage(imageWithShadow.shadowImage, cursor);

            jsonDirection.put("shadowImage", imageBoard.imageLocationToJson(imageWithShadow.shadowImage));

            cursor.y = cursor.y + Math.max(imageWithShadow.image.getHeight(), imageWithShadow.shadowImage.getHeight());
        }

        cursor.x = 0;

        // Fill in ship under construction
        for (ShipConstructionProgress progress : ShipConstructionProgress.values()) {

            if (!underConstructionImages.containsKey(progress)) {
                continue;
            }

            cursor.x = 0;

            ImageWithShadow imageWithShadow = underConstructionImages.get(progress);

            JSONObject jsonProgress = new JSONObject();

            jsonUnderConstruction.put(progress.name().toUpperCase(), jsonProgress);

            // Fill in image
            imageBoard.placeImage(imageWithShadow.image, cursor);

            jsonProgress.put("image", imageBoard.imageLocationToJson(imageWithShadow.image));

            cursor.x = cursor.x + imageWithShadow.image.getWidth();

            // Fill in shadow image
            imageBoard.placeImage(imageWithShadow.shadowImage, cursor);

            jsonProgress.put("shadowImage", imageBoard.imageLocationToJson(imageWithShadow.shadowImage));

            cursor.y = cursor.y + Math.max(imageWithShadow.image.getHeight(), imageWithShadow.shadowImage.getHeight());
        }

        // Write the image atlas to file
        imageBoard.writeBoardToBitmap(palette).writeToFile(toDir + "/image-atlas-ship.png");

        Files.writeString(Paths.get(toDir, "image-atlas-ship.json"), jsonImageAtlas.toJSONString());
    }

    public void addShipUnderConstructionImageWithShadow(ShipConstructionProgress progress, Bitmap image, Bitmap shadowImage) {
        underConstructionImages.put(progress, new ImageWithShadow(image, shadowImage));
    }
}
