package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.SignType;
import org.appland.settlers.model.Size;
import org.json.simple.JSONObject;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SignImageCollection {
    private static final List<SignType> SIGN_TYPES = Arrays.asList(SignType.values());

    private final Map<SignType, Map<Size, Bitmap>> signTypeToImageMap;

    private Bitmap shadowImage;

    public SignImageCollection() {
        signTypeToImageMap = new EnumMap<>(SignType.class);

        Arrays.asList(SignType.values()).forEach(signType -> this.signTypeToImageMap.put(signType, new EnumMap<>(Size.class)));
    }

    public void addImage(SignType signType, Size size, Bitmap image) {
        this.signTypeToImageMap.get(signType).put(size, image);
    }

    public void writeImageAtlas(String toDir, Palette palette) throws IOException {

        // Create the image atlas and meta-data
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();
        JSONObject jsonImages = new JSONObject();

        jsonImageAtlas.put("images", jsonImages);

        // Fill in the image atlas
        Point cursor = new Point(0, 0);
        int rowHeight = 0;

        for (SignType signType : SIGN_TYPES) {

            cursor.x = 0;

            JSONObject jsonMaterial = new JSONObject();

            jsonImages.put(signType.name().toLowerCase(), jsonMaterial);

            for (Size size : signTypeToImageMap.get(signType).keySet()) {
                Bitmap image = signTypeToImageMap.get(signType).get(size);

                if (image == null) {
                    continue;
                }

                imageBoard.placeImage(image, cursor);

                JSONObject jsonSign = imageBoard.imageLocationToJson(image);

                jsonMaterial.put(size.name().toUpperCase(), jsonSign);

                rowHeight = Math.max(rowHeight, image.getHeight());

                cursor.x = cursor.x + image.getWidth();
            }

            cursor.y = cursor.y + rowHeight;
        }

        // Write the shadow image
        cursor.x = 0;

        imageBoard.placeImage(shadowImage, cursor);

        jsonImageAtlas.put("shadowImage", imageBoard.imageLocationToJson(shadowImage));

        // Write the image atlas image to file
        imageBoard.writeBoardToBitmap(palette).writeToFile(toDir + "/image-atlas-signs.png");

        Files.writeString(Paths.get(toDir, "image-atlas-signs.json"), jsonImageAtlas.toJSONString());
    }

    public void addShadowImage(Bitmap shadowImage) {
        this.shadowImage = shadowImage;
    }
}
