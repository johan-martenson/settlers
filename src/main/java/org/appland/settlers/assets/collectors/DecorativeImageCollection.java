package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.model.DecorationType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class DecorativeImageCollection {
    private final Map<DecorationType, DecorationTypeImage> decorationImages = new EnumMap<>(DecorationType.class);


    /**
     * Adds a decoration image for the specified decoration type.
     *
     * @param decorationType the type of decoration
     * @param image          the bitmap image to add
     */
    public void addDecorationImage(DecorationType decorationType, Bitmap image) {
        decorationImages.put(decorationType, new DecorationTypeImage(image, null));
    }

    /**
     * Adds a decoration image with an optional shadow image for the specified decoration type.
     *
     * @param decorationType the type of decoration
     * @param image          the bitmap image to add
     * @param shadowImage    the shadow bitmap image to add (nullable)
     */
    public void addDecorationImageWithShadow(DecorationType decorationType, Bitmap image, Bitmap shadowImage) {
        decorationImages.put(decorationType, new DecorationTypeImage(image, shadowImage));
    }

    /**
     * Writes the image atlas to the specified directory using the given palette.
     *
     * @param dir     the directory to save the atlas
     * @param palette the palette to use for the images
     * @throws IOException if an I/O error occurs
     */
    public void writeImageAtlas(String dir, Palette palette) throws IOException {
        var imageBoard = new ImageBoard();
        var list = new ArrayList<ImageBoard.ImagePathPair>();

        decorationImages.forEach((decoration, decorationImages) -> {
            list.add(ImageBoard.makeImagePathPair(
                    decorationImages.image(),
                    decoration.name().toUpperCase(),
                    "image"));

            if (decorationImages.shadowImage() != null) {
                list.add(ImageBoard.makeImagePathPair(
                        decorationImages.shadowImage(),
                        decoration.name().toUpperCase(),
                        "shadowImage"));
            }
        });

        imageBoard.placeImagesAsColumn(list);

        imageBoard.writeBoard(dir, "image-atlas-decorations", palette);
    }

    private record DecorationTypeImage (Bitmap image, Bitmap shadowImage){ }
}
