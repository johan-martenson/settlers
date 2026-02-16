package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.utils.ImageTransformer;
import org.appland.settlers.model.actors.Pig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PigImageCollection {
    private final Map<Pig.PigAction, List<Bitmap>> animations = new EnumMap<>(Pig.PigAction.class);
    private Bitmap adultShadow;
    private Bitmap pigletShadow;

    public void addAnimation(Pig.PigAction action, List<Bitmap> images) {
        animations
                .computeIfAbsent(action, k -> new ArrayList<>())
                .addAll(images);
    }

    public void addShadows(Bitmap adultShadow, Bitmap pigletShadow) {
        this.adultShadow = adultShadow;
        this.pigletShadow = pigletShadow;
    }

    public void writeImageAtlas(String directory, Palette palette) throws IOException {
        var imageBoard = new ImageBoard();

        animations.forEach((action, images) -> imageBoard.placeImageSeriesBottom(
                ImageTransformer.normalizeImageSeries(images),
                "animations",
                action.name().toLowerCase()));

        if (adultShadow != null) {
            imageBoard.placeImagesAsRow(List.of(
                    ImageBoard.makeImagePathPair(adultShadow, "shadows", "adult")
            ));
        }

        if (pigletShadow != null) {
            imageBoard.placeImagesAsRow(List.of(
                    ImageBoard.makeImagePathPair(pigletShadow, "shadows", "piglet")
            ));
        }

        imageBoard.writeBoard(directory, "image-atlas-pig", palette);
    }
}
