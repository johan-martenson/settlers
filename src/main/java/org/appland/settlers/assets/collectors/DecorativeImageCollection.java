package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.Bitmap;
import org.appland.settlers.assets.ImageBoard;
import org.appland.settlers.assets.Palette;
import org.appland.settlers.model.DecorationType;
import org.json.simple.JSONObject;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;

public class DecorativeImageCollection {
    private final Map<DecorationType, DecorationTypeImage> decorationImages;

    public DecorativeImageCollection() {
        decorationImages = new EnumMap<>(DecorationType.class);
    }

    public void addDecorationTypeImage(DecorationType decorationType, Bitmap image) {
        decorationImages.put(decorationType, new DecorationTypeImage(image));
    }

    public void addDecorationImageWithShadow(DecorationType decorationType, Bitmap image, Bitmap shadowImage) {
        decorationImages.put(decorationType, new DecorationTypeImage(image, shadowImage));
    }

    public void writeImageAtlas(String dir, Palette palette) throws IOException {

        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();

        Point cursor = new Point(0, 0);

        for (Map.Entry<DecorationType, DecorationTypeImage> entry : this.decorationImages.entrySet()) {
            DecorationType decorationType = entry.getKey();
            Bitmap image = entry.getValue().image;
            Bitmap shadowImage = entry.getValue().shadowImage;

            int rowHeight = 0;
            cursor.x = 0;

            JSONObject jsonDecorationType = new JSONObject();

            jsonImageAtlas.put(decorationType.name().toUpperCase(), jsonDecorationType);

            // DecorationType image
            imageBoard.placeImage(image, cursor);

            jsonDecorationType.put("image", imageBoard.imageLocationToJson(image));

            rowHeight = image.getHeight();

            cursor.x = cursor.x + image.getWidth();

            // DecorationType shadow image
            if (shadowImage != null) {
                imageBoard.placeImage(shadowImage, cursor);

                jsonDecorationType.put("shadowImage", imageBoard.imageLocationToJson(shadowImage));

                rowHeight = Math.max(rowHeight, shadowImage.getHeight());
            }

            cursor.y = cursor.y + rowHeight;
        }

        // Write to file
        imageBoard.writeBoardToBitmap(palette).writeToFile(dir + "/image-atlas-decorations.png");

        Files.writeString(Paths.get(dir, "image-atlas-decorations.json"), jsonImageAtlas.toJSONString());
    }

    private static class DecorationTypeImage {

        private final Bitmap image;
        private final Bitmap shadowImage;

        public DecorationTypeImage(Bitmap image) {
            this.image = image;
            shadowImage = null;
        }

        public DecorationTypeImage(Bitmap image, Bitmap shadowImage) {
            this.image = image;
            this.shadowImage = shadowImage;
        }
    }
}
