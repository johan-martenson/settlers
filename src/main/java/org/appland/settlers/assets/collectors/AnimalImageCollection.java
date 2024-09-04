package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.CompassDirection;
import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.utils.ImageTransformer;
import org.appland.settlers.model.Material;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AnimalImageCollection {
    private final String name;
    private final Map<CompassDirection, List<Bitmap>> directionToImageMap = new EnumMap<>(CompassDirection.class);
    private final Map<CompassDirection, Bitmap> shadowImages = new EnumMap<>(CompassDirection.class);
    private final Map<Material, Bitmap> cargoImages = new EnumMap<>(Material.class);
    private final Map<Nation, Map<Material, Bitmap>> nationCargoImages = new EnumMap<>(Nation.class);

    public AnimalImageCollection(String name) {
        this.name = name;

        for (CompassDirection compassDirection : CompassDirection.values()) {
            this.directionToImageMap.put(compassDirection, new ArrayList<>());
        }
    }

    /**
     * Adds an image to the collection for a specific compass direction.
     *
     * @param compassDirection the compass direction
     * @param image            the bitmap image to add
     */
    public void addImage(CompassDirection compassDirection, Bitmap image) {
        directionToImageMap
                .computeIfAbsent(compassDirection, k -> new ArrayList<>())
                .add(image);
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

        directionToImageMap.forEach((direction, images) -> imageBoard.placeImageSeriesBottom(
                ImageTransformer.normalizeImageSeries(images),
                "images",
                direction.name().toUpperCase()));

        imageBoard.placeImagesAsRow(
                shadowImages.entrySet().stream()
                        .map(entry -> ImageBoard.makeImagePathPair(
                                entry.getValue(),
                                "shadowImages",
                                entry.getKey().name().toUpperCase()))
                        .toList());

        imageBoard.placeImagesAsRow(
                cargoImages.entrySet().stream()
                        .map(entry -> ImageBoard.makeImagePathPair(
                                entry.getValue(),
                                "cargos",
                                entry.getKey().name().toUpperCase()))
                        .toList());

        nationCargoImages.forEach((nation, materialMap) -> imageBoard.placeImagesAsRow(
                materialMap.entrySet().stream()
                        .map(entry -> ImageBoard.makeImagePathPair(
                                entry.getValue(),
                                "nationSpecific",
                                nation.name().toUpperCase(),
                                entry.getKey().name().toUpperCase()
                                ))
                        .toList()));

        imageBoard.writeBoard(directory, String.format("image-atlas-%s", name.toLowerCase()), palette);
    }

    /**
     * Adds a list of images to the collection for a specific compass direction.
     *
     * @param compassDirection the compass direction
     * @param images           the list of bitmap images to add
     */
    public void addImages(CompassDirection compassDirection, List<Bitmap> images) {
        directionToImageMap
                .computeIfAbsent(compassDirection, k -> new ArrayList<>())
                .addAll(images);
    }

    /**
     * Adds a shadow image to the collection for a specific compass direction.
     *
     * @param compassDirection the compass direction
     * @param image            the bitmap image to add
     */
    public void addShadowImage(CompassDirection compassDirection, Bitmap image) {
        shadowImages.put(compassDirection, image);
    }

    /**
     * Adds a cargo image to the collection for a specific material.
     *
     * @param material the material
     * @param image    the bitmap image to add
     */
    public void addCargoImage(Material material, Bitmap image) {
        cargoImages.put(material, image);
    }

    /**
     * Adds a nation-specific cargo image to the collection.
     *
     * @param nation   the nation
     * @param material the material
     * @param image    the bitmap image to add
     */
    public void addNationSpecificCargoImage(Nation nation, Material material, Bitmap image) {
        nationCargoImages
                .computeIfAbsent(nation, k -> new EnumMap<>(Material.class))
                .put(material, image);
    }
}
