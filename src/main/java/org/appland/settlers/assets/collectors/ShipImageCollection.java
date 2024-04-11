package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.CompassDirection;
import org.appland.settlers.assets.ImageWithShadow;
import org.appland.settlers.assets.ShipConstructionProgress;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ShipImageCollection {

    private final Map<CompassDirection, ImageWithShadow> images;
    private final Map<ShipConstructionProgress, ImageWithShadow> underConstructionImages;

    public ShipImageCollection() {
        images = new EnumMap<>(CompassDirection.class);
        underConstructionImages = new EnumMap<>(ShipConstructionProgress.class);
    }

    public void addShipImageWithShadow(CompassDirection compassDirection, Bitmap image, Bitmap shadowImage) {
        this.images.put(compassDirection, new ImageWithShadow(image, shadowImage));
    }

    public void writeImageAtlas(String toDir, Palette palette) throws IOException {
        ImageBoard imageBoard = new ImageBoard();

        images.forEach((direction, imageAndShadow) -> imageBoard.placeImagesAsRow(
                List.of(
                        ImageBoard.makeImagePathPair(
                                imageAndShadow.image,
                                "ready",
                                direction.name().toUpperCase(),
                                "image"
                        ),
                        ImageBoard.makeImagePathPair(
                                imageAndShadow.shadowImage,
                                "ready",
                                direction.name().toUpperCase(),
                                "shadowImage"
                        )
                )
        ));

        underConstructionImages.forEach(
                (progress, imageAndShadow) -> imageBoard.placeImagesAsRow(
                List.of(
                        ImageBoard.makeImagePathPair(
                                imageAndShadow.image,
                                "underConstruction",
                                progress.name().toUpperCase(),
                                "image"
                        ),
                        ImageBoard.makeImagePathPair(
                                imageAndShadow.shadowImage,
                                "underConstruction",
                                progress.name().toUpperCase(),
                                "shadowImage"
                        )
                )
        ));

        imageBoard.writeBoard(toDir, "image-atlas-ship", palette);
    }

    public void addShipUnderConstructionImageWithShadow(ShipConstructionProgress progress, Bitmap image, Bitmap shadowImage) {
        underConstructionImages.put(progress, new ImageWithShadow(image, shadowImage));
    }
}
