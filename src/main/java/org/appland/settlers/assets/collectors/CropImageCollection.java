package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.model.Crop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

public class CropImageCollection {
    private final Map<Crop.CropType, Map<Crop.GrowthState, Bitmap>> cropMap;
    private final Map<Crop.CropType, Map<Crop.GrowthState, Bitmap>> cropShadowMap;

    public CropImageCollection() {
        cropMap = new EnumMap<>(Crop.CropType.class);
        cropShadowMap = new EnumMap<>(Crop.CropType.class);

        for (Crop.CropType type : Crop.CropType.values()) {
            cropMap.put(type, new EnumMap<>(Crop.GrowthState.class));
            cropShadowMap.put(type, new EnumMap<>(Crop.GrowthState.class));
        }
    }

    public void addImage(Crop.CropType type, Crop.GrowthState growth, Bitmap image) {
        cropMap.get(type).put(growth, image);
    }

    public void writeImageAtlas(String toDir, Palette palette) throws IOException {
        ImageBoard imageBoard = new ImageBoard();

        for (var entry : this.cropMap.entrySet()) {
            var list = new ArrayList<ImageBoard.ImagePathPair>();

            Stream.of(Crop.GrowthState.values()).forEach(
                    (growthState -> {
                        list.add(
                                ImageBoard.makeImagePathPair(
                                        entry.getValue().get(growthState),
                                        entry.getKey().name(),
                                        growthState.name().toUpperCase(),
                                        "image"
                                ));

                        list.add(
                                ImageBoard.makeImagePathPair(
                                        cropShadowMap.get(entry.getKey()).get(growthState),
                                        entry.getKey().name(),
                                        growthState.name().toUpperCase(),
                                        "shadowImage"
                                ));
                    }));

            imageBoard.placeImagesAsRow(list);
        }

        imageBoard.writeBoard(toDir, "image-atlas-crops", palette);
    }

    public void addShadowImage(Crop.CropType cropType, Crop.GrowthState growthState, Bitmap image) {
        cropShadowMap.get(cropType).put(growthState, image);
    }
}
