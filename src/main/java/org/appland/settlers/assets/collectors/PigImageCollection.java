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
    private final Map<Pig.PigAge, Map<Pig.PigAction, List<Bitmap>>> animations = new EnumMap<>(Pig.PigAge.class);

    private Bitmap adultShadow;
    private Bitmap pigletShadow;

    public void addAnimation(Pig.PigAge age, Pig.PigAction action, List<Bitmap> images) {
        animations
                .computeIfAbsent(age, k -> new EnumMap<>(Pig.PigAction.class))
                .computeIfAbsent(action, k -> new ArrayList<>())
                .addAll(images);
    }

    public void addShadows(Bitmap adultShadow, Bitmap pigletShadow) {
        this.adultShadow = adultShadow;
        this.pigletShadow = pigletShadow;
    }

    public void writeImageAtlas(String directory, Palette palette) throws IOException {
        var imageBoard = new ImageBoard();

        animations.forEach((age, actions) ->
                actions.forEach((action, images) ->
                        imageBoard.placeImageSeriesBottom(
                                ImageTransformer.normalizeImageSeries(images),
                                "animations",
                                age.name().toUpperCase(),
                                action.name().toUpperCase()
                        )
                )
        );

        if (adultShadow != null) {
            imageBoard.placeImagesAsRow(List.of(
                    ImageBoard.makeImagePathPair(adultShadow, "shadows", "ADULT")
            ));
        }

        if (pigletShadow != null) {
            imageBoard.placeImagesAsRow(List.of(
                    ImageBoard.makeImagePathPair(pigletShadow, "shadows", "PIGLET")
            ));
        }

        imageBoard.writeBoard(directory, "image-atlas-pig", palette);
    }
}
