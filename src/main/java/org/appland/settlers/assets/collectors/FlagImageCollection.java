package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.utils.ImageTransformer;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.PlayerColor;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        // Write the image atlas, one row per flag, and collect metadata to write as json
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();

        Arrays.stream(Nation.values()).forEach(nation -> {
            JSONObject jsonNationInfo = new JSONObject();

            jsonImageAtlas.put(nation.name().toUpperCase(), jsonNationInfo);

            Arrays.stream(Flag.FlagType.values()).forEach(flagType -> {
                JSONObject jsonFlagType = new JSONObject();

                jsonNationInfo.put(flagType.name().toUpperCase(), jsonFlagType);

                Arrays.stream(PlayerColor.values()).forEach(playerColor -> {
                    JSONObject jsonPlayer = new JSONObject();

                    jsonFlagType.put(playerColor.name().toUpperCase(),
                            imageBoard.placeImageSeriesBottom(
                                    ImageTransformer.normalizeImageSeries(
                                            ImageTransformer.drawForPlayer(playerColor, flagMap.get(nation).get(flagType)))));
                });

                jsonFlagType.put(
                        "shadows",
                        imageBoard.placeImageSeriesBottom(
                                ImageTransformer.normalizeImageSeries(
                                        flagShadowMap.get(nation).get(flagType))));
            });
        });

        // Write the image for the image atlas
        imageBoard.writeBoardToBitmap(palette).writeToFile(directory + "/" + "image-atlas-flags.png");

        // Write a JSON file that specifies where each image is in pixels
        Path filePath = Paths.get(directory, "image-atlas-flags.json");

        Files.writeString(filePath, jsonImageAtlas.toJSONString());
    }

    public void addImagesForFlag(Nation nation, Flag.FlagType flagType, List<Bitmap> images) {
        this.flagMap.get(nation).get(flagType).addAll(images);
    }

    public void addImagesForFlagShadow(Nation nation, Flag.FlagType flagType, List<Bitmap> images) {
        this.flagShadowMap.get(nation).get(flagType).addAll(images);
    }
}
