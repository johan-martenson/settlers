package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.SignType;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.model.Size;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class SignImageCollection {
    private final Map<SignType, Map<Size, Bitmap>> signTypeToImageMap = new EnumMap<>(SignType.class);

    private Bitmap shadowImage;

    /**
     * Adds an image for a specific sign type and size.
     *
     * @param signType the sign type
     * @param size     the size of the sign
     * @param image    the bitmap image to add
     */
    public void addImage(SignType signType, Size size, Bitmap image) {
        signTypeToImageMap.computeIfAbsent(signType, k -> new EnumMap<>(Size.class)).put(size, image);
    }

    /**
     * Writes the image atlas to the specified directory using the given palette.
     *
     * @param directory the directory to save the atlas
     * @param palette   the palette to use for the images
     * @throws IOException if an I/O error occurs
     */
    public void writeImageAtlas(String directory, Palette palette) throws IOException {
        ImageBoard imageBoard = new ImageBoard();

        signTypeToImageMap.forEach((signType, sizeBitmapMap) ->
                sizeBitmapMap.forEach((size, image) ->
                        imageBoard.placeImageRightOf(
                                image,
                                "images",
                                signType.name().toUpperCase(),
                                size.name().toUpperCase()
                        )
                )
        );

        if (shadowImage != null) {
            imageBoard.placeImageBottom(shadowImage, "shadowImage");
        }

        imageBoard.writeBoard(directory, "image-atlas-signs", palette);
    }

    /**
     * Adds a shadow image.
     *
     * @param shadowImage the shadow bitmap image to add
     */
    public void addShadowImage(Bitmap shadowImage) {
        this.shadowImage = shadowImage;
    }
}
