package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.FireSize;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.utils.ImageTransformer;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.SmokeType;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class FireImageCollection {
    private final Map<FireSize, List<Bitmap>> fireMap = new EnumMap<>(FireSize.class);
    private final Map<Size, Bitmap> burntDownMap = new EnumMap<>(Size.class);
    private final Map<FireSize, List<Bitmap>> fireShadowMap = new EnumMap<>(FireSize.class);
    private final Map<SmokeType, List<Bitmap>> smokeAnimations = new EnumMap<>(SmokeType.class);

    /**
     * Writes the image atlas to the specified directory using the given palette.
     *
     * @param directory the directory to save the atlas
     * @param palette   the palette to use for the images
     * @throws IOException if an I/O error occurs
     */
    public void writeImageAtlas(String directory, Palette palette) throws IOException {
        var imageBoard = new ImageBoard();

        Arrays.stream(FireSize.values()).forEach(fireSize -> {
                    imageBoard.placeImageSeriesBottom(
                            ImageTransformer.normalizeImageSeries(fireMap.get(fireSize)),
                            "fires",
                            fireSize.name().toUpperCase(),
                            "image");

                    if (fireShadowMap.containsKey(fireSize)) {
                        imageBoard.placeImageSeriesBottom(
                                ImageTransformer.normalizeImageSeries(fireShadowMap.get(fireSize)),
                                "fires",
                                fireSize.name().toUpperCase(),
                                "shadowImage");
                    }
                });

        imageBoard.placeImagesAsRow(
                burntDownMap.entrySet().stream()
                        .map(entry -> ImageBoard.makeImagePathPair(
                                        entry.getValue(),
                                        "burntDown",
                                        entry.getKey().name().toUpperCase()))
                        .toList());

        smokeAnimations.entrySet().forEach(entry ->
                imageBoard.placeImageSeriesBottom(
                        ImageTransformer.normalizeImageSeries(entry.getValue()),
                        "smoke",
                        entry.getKey().name().toUpperCase()));

        imageBoard.writeBoard(directory, "image-atlas-fire", palette);
    }

    /**
     * Adds images for a specific fire size.
     *
     * @param fireSize                    the size of the fire
     * @param imagesFromResourceLocations the list of bitmap images
     */
    public void addImagesForFire(FireSize fireSize, List<Bitmap> imagesFromResourceLocations) {
        this.fireMap.put(fireSize, imagesFromResourceLocations);
    }

    /**
     * Adds a burnt-down image for a specific size.
     *
     * @param size  the size
     * @param image the bitmap image to add
     */
    public void addBurntDownImage(Size size, Bitmap image) {
        this.burntDownMap.put(size, image);
    }

    /**
     * Adds images and their shadows for a specific fire size.
     *
     * @param fireSize    the size of the fire
     * @param images      the list of bitmap images
     * @param shadowImages the list of shadow images
     */
    public void addImagesForFireWithShadow(FireSize fireSize, List<Bitmap> images, List<Bitmap> shadowImages) {
        this.fireMap.put(fireSize, images);
        this.fireShadowMap.put(fireSize, shadowImages);
    }

    public void addSmokeAnimation(SmokeType smokeType, List<Bitmap> image) {
        smokeAnimations.put(smokeType, image);
    }
}
