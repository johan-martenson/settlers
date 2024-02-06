package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.FireSize;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.utils.ImageTransformer;
import org.appland.settlers.model.Size;
import org.json.simple.JSONObject;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class FireImageCollection {
    private final Map<FireSize, List<Bitmap>> fireMap;
    private final Map<Size, Bitmap> burntDownMap;
    private final Map<FireSize, List<Bitmap>> fireShadowMap;

    public FireImageCollection() {
        fireMap = new EnumMap<>(FireSize.class);
        fireShadowMap = new EnumMap<>(FireSize.class);
        burntDownMap = new EnumMap<>(Size.class);
    }

    public void writeImageAtlas(String directory, Palette palette) throws IOException {

        // Write the image atlas, one row per fire animation size, and a final row with the burnt down images
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();

        JSONObject jsonFireAnimations = new JSONObject();
        JSONObject jsonBurntDownImages = new JSONObject();

        jsonImageAtlas.put("fires", jsonFireAnimations);
        jsonImageAtlas.put("burntDown", jsonBurntDownImages);

        Arrays.stream(FireSize.values())
                .forEach(fireSize -> {
                    JSONObject jsonFireSize = new JSONObject();

                    jsonFireAnimations.put(fireSize.name().toUpperCase(), jsonFireSize);

                    // Fire images
                    jsonFireSize.put("image",
                            imageBoard.placeImageSeriesBottom(
                                    ImageTransformer.normalizeImageSeries(fireMap.get(fireSize))));

                    // Fire shadow images
                    if (fireShadowMap.containsKey(fireSize)) {
                        jsonFireSize.put("shadowImage",
                                imageBoard.placeImageSeriesBottom(
                                        ImageTransformer.normalizeImageSeries(fireShadowMap.get(fireSize))));
                    }
                });

        Point cursor = new Point(0, imageBoard.getCurrentHeight());
        for (Map.Entry<Size, Bitmap> entry : burntDownMap.entrySet()) {
            Size size = entry.getKey();
            Bitmap image = entry.getValue();

            jsonBurntDownImages.put(
                    size.name().toUpperCase(),
                    imageBoard.placeImage(image, cursor)
            );

            cursor.x += image.getWidth();
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
