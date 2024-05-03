package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.utils.ImageTransformer;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.PlayerColor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class FlagImageCollection {
    private final Map<Nation, Map<Flag.FlagType, List<Bitmap>>> flagMap;
    private final Map<Nation, Map<Flag.FlagType, List<Bitmap>>> flagShadowMap;

    public FlagImageCollection() {
        flagMap = new EnumMap<>(Nation.class);
        flagShadowMap = new EnumMap<>(Nation.class);

        for (Nation nation : Nation.values()) {
            flagMap.put(nation, new EnumMap<>(Flag.FlagType.class));
            flagShadowMap.put(nation, new EnumMap<>(Flag.FlagType.class));

            for (Flag.FlagType flagType : Flag.FlagType.values()) {
                flagMap.get(nation).put(flagType, new ArrayList<>());
                flagShadowMap.get(nation).put(flagType, new ArrayList<>());
            }
        }
    }

    public void writeImageAtlas(String directory, Palette palette) throws IOException {
        ImageBoard imageBoard = new ImageBoard();

        Arrays.stream(Nation.values())
                .forEach(nation -> Arrays.stream(Flag.FlagType.values())
                        .forEach(flagType -> {
                            Arrays.stream(PlayerColor.values())
                                    .forEach(playerColor -> imageBoard.placeImageSeriesBottom(
                                            ImageTransformer.normalizeImageSeries(
                                                    ImageTransformer.drawForPlayer(playerColor, flagMap.get(nation).get(flagType))),
                                            nation.name().toUpperCase(),
                                            flagType.name().toUpperCase(),
                                            playerColor.name().toUpperCase()));

                            imageBoard.placeImageSeriesBottom(
                                    ImageTransformer.normalizeImageSeries(
                                            flagShadowMap.get(nation).get(flagType)),
                                    nation.name().toUpperCase(),
                                    flagType.name().toUpperCase(),
                                    "shadows");
                        }));

        imageBoard.writeBoard(directory, "image-atlas-flags", palette);
    }

    public void addImagesForFlag(Nation nation, Flag.FlagType flagType, List<PlayerBitmap> images) {
        this.flagMap.get(nation).get(flagType).addAll(images);
    }

    public void addImagesForFlagShadow(Nation nation, Flag.FlagType flagType, List<Bitmap> images) {
        this.flagShadowMap.get(nation).get(flagType).addAll(images);
    }
}
