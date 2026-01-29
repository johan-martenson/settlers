package org.appland.settlers.assets.collectors;

import org.appland.settlers.model.StoneAmount;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.model.Stone;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class StonesImageCollection {
    private final Map<Stone.StoneType, Map<StoneAmount, Bitmap>> stoneMap = new EnumMap<>(Stone.StoneType.class);
    private final Map<Stone.StoneType, Map<StoneAmount, Bitmap>> stoneShadowMap = new EnumMap<>(Stone.StoneType.class);

    /**
     * Adds an image for a specific stone type and amount.
     *
     * @param type   the stone type
     * @param amount the stone amount
     * @param image  the bitmap image to add
     */
    public void addImage(Stone.StoneType type, StoneAmount amount, Bitmap image) {
        stoneMap.computeIfAbsent(type, k -> new EnumMap<>(StoneAmount.class)).put(amount, image);
    }

    /**
     * Adds a shadow image for a specific stone type and amount.
     *
     * @param type   the stone type
     * @param amount the stone amount
     * @param image  the bitmap image to add
     */
    public void addShadowImage(Stone.StoneType type, StoneAmount amount, Bitmap image) {
        stoneShadowMap.computeIfAbsent(type, k -> new EnumMap<>(StoneAmount.class)).put(amount, image);
    }

    /**
     * Writes the image atlas to the specified directory using the given palette.
     *
     * @param toDir   the directory to save the atlas
     * @param palette the palette to use for the images
     * @throws IOException if an I/O error occurs
     */
    public void writeImageAtlas(String toDir, Palette palette) throws IOException {
        var imageBoard = new ImageBoard();

        for (var stoneType : Stone.StoneType.values()) {
            for (var stoneAmount : StoneAmount.values()) {
                var image = stoneMap.get(stoneType).get(stoneAmount);
                var shadowImage = stoneShadowMap.get(stoneType).get(stoneAmount);

                imageBoard.placeImageBottom(
                        image,
                        stoneType.name().toUpperCase(),
                        stoneAmount.name().toUpperCase(),
                        "image");

                if (shadowImage != null) {
                    imageBoard.placeImageBottom(
                            shadowImage,
                            stoneType.name().toUpperCase(),
                            stoneAmount.name().toUpperCase(),
                            "shadowImage");
                }
            }
        }

        imageBoard.writeBoard(toDir, "image-atlas-stones", palette);
    }
}
