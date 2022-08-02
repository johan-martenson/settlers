package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.Bitmap;
import org.appland.settlers.assets.FireSize;
import org.appland.settlers.assets.ImageBoard;
import org.appland.settlers.assets.NormalizedImageList;
import org.appland.settlers.assets.Palette;
import org.appland.settlers.model.Size;
import org.json.simple.JSONObject;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.appland.settlers.assets.ImageBoard.LayoutDirection.ROW;

public class FireImageCollection {
    private final Map<FireSize, List<Bitmap>> fireMap;
    private final Map<Size, Bitmap> burntDownMap;
    private final Map<FireSize, List<Bitmap>> fireShadowMap;

    public FireImageCollection() {
        fireMap = new HashMap<>();
        fireShadowMap = new HashMap<>();
        burntDownMap = new HashMap<>();
    }

    public void writeImageAtlas(String directory, Palette palette) throws IOException {

        // Write the image atlas, one row per fire animation size, and a final row with the burnt down images
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();

        JSONObject jsonFireAnimations = new JSONObject();
        JSONObject jsonBurntDownImages = new JSONObject();

        jsonImageAtlas.put("fires", jsonFireAnimations);
        jsonImageAtlas.put("burntDown", jsonBurntDownImages);

        Point cursor = new Point(0, 0);
        for (FireSize fireSize : FireSize.values()) {

            cursor.x = 0;

            JSONObject jsonFireSize = new JSONObject();

            jsonFireAnimations.put(fireSize.name().toUpperCase(), jsonFireSize);

            // Fire images
            List<Bitmap> images = this.fireMap.get(fireSize);
            NormalizedImageList normalizedImageList = new NormalizedImageList(images);
            List<Bitmap> normalizedImages = normalizedImageList.getNormalizedImages();

            imageBoard.placeImageSeries(normalizedImages, cursor, ROW);

            jsonFireSize.put("image", imageBoard.imageSeriesLocationToJson(normalizedImages));

            cursor.x = cursor.x + normalizedImageList.size() * normalizedImageList.getImageWidth();

            // Fire shadow images
            if (fireShadowMap.containsKey(fireSize)) {
                List<Bitmap> shadowImages = this.fireShadowMap.get(fireSize);
                NormalizedImageList normalizedShadowImageList = new NormalizedImageList(shadowImages);
                List<Bitmap> normalizedShadowImages = normalizedShadowImageList.getNormalizedImages();

                imageBoard.placeImageSeries(normalizedShadowImages, cursor, ROW);

                jsonFireSize.put("shadowImage", imageBoard.imageSeriesLocationToJson(normalizedShadowImages));
            }

            cursor.y = cursor.y + normalizedImageList.getImageHeight();
        }

        cursor.x = 0;
        for (Map.Entry<Size, Bitmap> entry : burntDownMap.entrySet()) {
            Size size = entry.getKey();
            Bitmap image = entry.getValue();

            imageBoard.placeImage(image, cursor);

            JSONObject jsonBurntDownImage = imageBoard.imageLocationToJson(image);

            jsonBurntDownImages.put(size.name().toUpperCase(), jsonBurntDownImage);

            cursor.x = cursor.x + image.getWidth();
        }

        imageBoard.writeBoardToBitmap(palette).writeToFile(directory + "/" + "image-atlas-fire.png");

        // Write a JSON file that specifies where each image is in pixels
        Path filePath = Paths.get(directory, "image-atlas-fire.json");

        Files.writeString(filePath, jsonImageAtlas.toJSONString());
    }

    public void addImagesForFire(FireSize fireSize, List<Bitmap> imagesFromResourceLocations) {
        this.fireMap.put(fireSize, imagesFromResourceLocations);
    }

    public void addBurntDownImage(Size size, Bitmap image) {
        this.burntDownMap.put(size, image);
    }

    public void addImagesForFireWithShadow(FireSize fireSize, List<Bitmap> images, List<Bitmap> shadowImages) {
        this.fireMap.put(fireSize, images);
        this.fireShadowMap.put(fireSize, shadowImages);
    }
}
