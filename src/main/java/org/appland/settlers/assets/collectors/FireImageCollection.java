package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.FireSize;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.utils.ImageTransformer;
import org.appland.settlers.model.Size;

import java.io.IOException;
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
        ImageBoard imageBoard = new ImageBoard();

        Arrays.stream(FireSize.values())
                .forEach(fireSize -> {
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
                                        entry.getKey().name().toUpperCase()
                                )
                        )
                        .toList());

        imageBoard.writeBoard(directory, "image-atlas-fire", palette);
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
