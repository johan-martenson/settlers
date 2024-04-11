package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.StoneAmount;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.model.Stone;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class StonesImageCollection {
    private final Map<Stone.StoneType, Map<StoneAmount, Bitmap>> stoneMap;
    private final Map<Stone.StoneType, Map<StoneAmount, Bitmap>> stoneShadowMap;

    public StonesImageCollection() {
        stoneMap = new EnumMap<>(Stone.StoneType.class);
        stoneShadowMap = new EnumMap<>(Stone.StoneType.class);

        for (Stone.StoneType type : Stone.StoneType.values()) {
            stoneMap.put(type, new EnumMap<>(StoneAmount.class));
            stoneShadowMap.put(type, new EnumMap<>(StoneAmount.class));
        }
    }

    public void addImage(Stone.StoneType type, StoneAmount amount, Bitmap image) {
        stoneMap.get(type).put(amount, image);
    }

    public void addShadowImage(Stone.StoneType type, StoneAmount amount, Bitmap image) {
        stoneShadowMap.get(type).put(amount, image);
    }

    public void writeImageAtlas(String toDir, Palette palette) throws IOException {
        ImageBoard imageBoard = new ImageBoard();

        Arrays.stream(Stone.StoneType.values())
                .forEach(stoneType -> Arrays.stream(StoneAmount.values())
                        .forEach(stoneAmount -> {
                            imageBoard.placeImageBottom(
                                    stoneMap.get(stoneType).get(stoneAmount),
                                    stoneType.name().toUpperCase(),
                                    stoneAmount.name().toUpperCase(),
                                    "image"
                            );
                            imageBoard.placeImageBottom(
                                    stoneShadowMap.get(stoneType).get(stoneAmount),
                                    stoneType.name().toUpperCase(),
                                    stoneAmount.name().toUpperCase(),
                                    "shadowImage"
                            );
                        }));

        imageBoard.writeBoard(toDir, "image-atlas-stones", palette);
    }
}
