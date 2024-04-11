package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.RoadConnectionDifference;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RoadBuildingImageCollection {
    private Bitmap startPointImage;
    private Bitmap sameLevelConnectionImage;
    private final Map<RoadConnectionDifference, Bitmap> upwardsConnectionImages;
    private final Map<RoadConnectionDifference, Bitmap> downwardsConnectionImages;

    public RoadBuildingImageCollection() {
        downwardsConnectionImages = new EnumMap<>(RoadConnectionDifference.class);
        upwardsConnectionImages = new EnumMap<>(RoadConnectionDifference.class);
    }

    public void addStartPointImage(Bitmap image) {
        this.startPointImage = image;
    }

    public void addSameLevelConnectionImage(Bitmap image) {
        this.sameLevelConnectionImage = image;
    }

    public void addUpwardsConnectionImage(RoadConnectionDifference difference, Bitmap image) {
        this.upwardsConnectionImages.put(difference, image);
    }

    public void addDownwardsConnectionImage(RoadConnectionDifference difference, Bitmap image) {
        this.downwardsConnectionImages.put(difference, image);
    }

    public void writeImageAtlas(String directory, Palette palette) throws IOException {
        ImageBoard imageBoard = new ImageBoard();

        imageBoard.placeImagesAsRow(
                List.of(
                        ImageBoard.makeImagePathPair(startPointImage, "startPoint"),
                        ImageBoard.makeImagePathPair(sameLevelConnectionImage, "sameLevelConnection")
                ));

        imageBoard.placeImagesAsRow(
                upwardsConnectionImages.entrySet().stream()
                        .map(entry -> ImageBoard.makeImagePathPair(
                                        entry.getValue(),
                                        "upwardsConnections",
                                        entry.getKey().name().toUpperCase()))
                        .toList());

        imageBoard.placeImagesAsRow(
                downwardsConnectionImages.entrySet().stream()
                        .map(entry -> ImageBoard.makeImagePathPair(
                                entry.getValue(),
                                "downwardsConnections",
                                entry.getKey().name().toUpperCase()))
                        .toList());

        imageBoard.writeBoard(directory, "image-atlas-road-building", palette);
    }
}
