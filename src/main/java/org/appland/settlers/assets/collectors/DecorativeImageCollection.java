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
    private final Map<DecorationType, DecorationTypeImage> decorationImages;

    public DecorativeImageCollection() {
        decorationImages = new EnumMap<>(DecorationType.class);
    }

    public void addDecorationImage(DecorationType decorationType, Bitmap image) {
        decorationImages.put(decorationType, new DecorationTypeImage(image));
    }

    public void addDecorationImageWithShadow(DecorationType decorationType, Bitmap image, Bitmap shadowImage) {
        decorationImages.put(decorationType, new DecorationTypeImage(image, shadowImage));
    }

    public void writeImageAtlas(String dir, Palette palette) throws IOException {
        ImageBoard imageBoard = new ImageBoard();

        var list = new ArrayList<ImageBoard.ImagePathPair>();

        for (var entry : decorationImages.entrySet()) {
            list.add(ImageBoard.makeImagePathPair(
                            entry.getValue().image,
                            entry.getKey().name().toUpperCase(),
                            "image"));

            if (entry.getValue().shadowImage != null) {
                list.add(ImageBoard.makeImagePathPair(
                        entry.getValue().shadowImage,
                        entry.getKey().name().toUpperCase(),
                        "shadowImage"));
            }
        }

        imageBoard.placeImagesAsColumn(list);

        imageBoard.writeBoard(dir, "image-atlas-decorations", palette);
    }

    private static class DecorationTypeImage {

        private final Bitmap image;
        private final Bitmap shadowImage;

        public DecorationTypeImage(Bitmap image) {
            this.image = image;
            shadowImage = null;
        }

        public DecorationTypeImage(Bitmap image, Bitmap shadowImage) {
            this.image = image;
            this.shadowImage = shadowImage;
        }
    }
}
