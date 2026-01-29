package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.model.Material;

import java.awt.Point;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class CargoImageCollection {
    private final Map<Material, Bitmap> cargos = new EnumMap<>(Material.class);
    private final Map<Nation, Map<Material, Bitmap>> nationCargos = new EnumMap<>(Nation.class);

    /**
     * Adds a cargo image for a generic material.
     *
     * @param material the material
     * @param image    the bitmap image to add
     */
    public void addCargoImage(Material material, Bitmap image) {
        cargos.put(material, image);
    }

    /**
     * Adds a cargo image for a specific nation and material.
     *
     * @param nation   the nation
     * @param material the material
     * @param image    the bitmap image to add
     */
    public void addCargoImageForNation(Nation nation, Material material, Bitmap image) {
        nationCargos.computeIfAbsent(nation, k -> new EnumMap<>(Material.class))
                    .put(material, image);
    }

    /**
     * Writes the image atlas and individual cargo icons to the specified directory using the given palette.
     *
     * @param toDir   the directory to save the atlas and icons
     * @param palette the palette to use for the images
     * @throws IOException if an I/O error occurs
     */
    public void writeImageAtlas(String toDir, Palette palette) throws IOException {
        var imageBoard = new ImageBoard();

        // Fill in the image atlas
        var cursor = new Point(0, 0);

        // Generic (non-nation specific) cargo images
        imageBoard.placeImagesAsColumn(
                cargos.entrySet().stream()
                        .map(entry -> ImageBoard.makeImagePathPair(
                                entry.getValue(),
                                "generic",
                                entry.getKey().name().toUpperCase()))
                        .toList());

        cursor.y = 0;
        cursor.x = imageBoard.getCurrentWidth();

        for (var nation : Nation.values()) {
            for (var entry : nationCargos.get(nation).entrySet()) {
                var material = entry.getKey();
                var image = entry.getValue();

                imageBoard.placeImage(
                        image,
                        cursor,
                        "nationSpecific",
                        nation.name().toUpperCase(),
                        material.name().toUpperCase()
                );

                cursor.y = cursor.y + image.getHeight();
            }
        }

        imageBoard.writeBoard(toDir, "image-atlas-cargos", palette);
    }
}
