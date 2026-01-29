package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.model.Crop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CropImageCollection {
    private final Map<Crop.CropType, Map<Crop.GrowthState, Bitmap>> cropMap = new EnumMap<>(Crop.CropType.class);
    private final Map<Crop.CropType, Map<Crop.GrowthState, Bitmap>> cropShadowMap = new EnumMap<>(Crop.CropType.class);

    /**
     * Adds an image for a specific crop type and growth state.
     *
     * @param type    the crop type
     * @param growth  the growth state
     * @param image   the bitmap image to add
     */
    public void addImage(Crop.CropType type, Crop.GrowthState growth, Bitmap image) {
        cropMap.computeIfAbsent(type, k -> new EnumMap<>(Crop.GrowthState.class)).put(growth, image);
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

        cropMap.forEach((cropType, growthMap) -> {
                    List<ImageBoard.ImagePathPair> imagePairs = new ArrayList<>();

            Stream.of(Crop.GrowthState.values()).forEach(
                    (growthState -> {
                        imagePairs.add(
                                ImageBoard.makeImagePathPair(
                                        growthMap.get(growthState),
                                        cropType.name(),
                                        growthState.name().toUpperCase(),
                                        "image"
                                ));

                        imagePairs.add(
                                ImageBoard.makeImagePathPair(
                                        cropShadowMap.get(cropType).get(growthState),
                                        cropType.name(),
                                        growthState.name().toUpperCase(),
                                        "shadowImage"
                                ));
                    }));

            imageBoard.placeImagesAsRow(imagePairs);
        });

        imageBoard.writeBoard(toDir, "image-atlas-crops", palette);
    }

    /**
     * Adds a shadow image for a specific crop type and growth state.
     *
     * @param cropType    the crop type
     * @param growthState the growth state
     * @param image       the bitmap image to add
     */
    public void addShadowImage(Crop.CropType cropType, Crop.GrowthState growthState, Bitmap image) {
        cropShadowMap.computeIfAbsent(cropType, k -> new EnumMap<>(Crop.GrowthState.class)).put(growthState, image);
    }
}
