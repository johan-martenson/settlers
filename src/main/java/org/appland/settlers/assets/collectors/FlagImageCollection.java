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
    private final Map<Nation, Map<Flag.FlagType, List<Bitmap>>> flagMap = new EnumMap<>(Nation.class);
    private final Map<Nation, Map<Flag.FlagType, List<Bitmap>>> flagShadowMap = new EnumMap<>(Nation.class);

    public void writeImageAtlas(String directory, Palette palette) throws IOException {
        ImageBoard imageBoard = new ImageBoard();

        Arrays.stream(Nation.values()).forEach(nation ->
                Arrays.stream(Flag.FlagType.values()).forEach(flagType -> {
                            Arrays.stream(PlayerColor.values()).forEach(playerColor ->
                                    imageBoard.placeImageSeriesBottom(
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
        flagMap.computeIfAbsent(nation, k -> new EnumMap<>(Flag.FlagType.class))
                .computeIfAbsent(flagType, k -> new ArrayList<>())
                .addAll(images);
    }

    public void addImagesForFlagShadow(Nation nation, Flag.FlagType flagType, List<Bitmap> images) {
        flagShadowMap.computeIfAbsent(nation, k -> new EnumMap<>(Flag.FlagType.class))
                .computeIfAbsent(flagType, k -> new ArrayList<>())
                .addAll(images);
    }
}
