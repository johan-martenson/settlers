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
    private final Map<RoadConnectionDifference, Bitmap> upwardsConnectionImages = new EnumMap<>(RoadConnectionDifference.class);
    private final Map<RoadConnectionDifference, Bitmap> downwardsConnectionImages = new EnumMap<>(RoadConnectionDifference.class);

    /**
     * Adds the start point image for road building.
     *
     * @param image the bitmap image to add
     */
    public void addStartPointImage(Bitmap image) {
        this.startPointImage = image;
    }

    /**
     * Adds the same-level connection image for road building.
     *
     * @param image the bitmap image to add
     */
    public void addSameLevelConnectionImage(Bitmap image) {
        this.sameLevelConnectionImage = image;
    }

    /**
     * Adds an upwards connection image for a specific road connection difference.
     *
     * @param difference the road connection difference
     * @param image      the bitmap image to add
     */
    public void addUpwardsConnectionImage(RoadConnectionDifference difference, Bitmap image) {
        this.upwardsConnectionImages.put(difference, image);
    }

    /**
     * Adds a downwards connection image for a specific road connection difference.
     *
     * @param difference the road connection difference
     * @param image      the bitmap image to add
     */
    public void addDownwardsConnectionImage(RoadConnectionDifference difference, Bitmap image) {
        this.downwardsConnectionImages.put(difference, image);
    }

    /**
     * Writes the image atlas to the specified directory using the given palette.
     *
     * @param directory the directory to save the atlas
     * @param palette   the palette to use for the images
     * @throws IOException if an I/O error occurs
     */
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
