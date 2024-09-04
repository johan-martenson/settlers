package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.model.Material;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class InventoryImageCollection {
    private final Map<Material, Bitmap> icons = new HashMap<>();
    private final Map<Nation, Map<Material, Bitmap>> nationIcons = new HashMap<>();

    /**
     * Adds a generic icon for the specified material.
     *
     * @param material the material
     * @param image    the bitmap image to add
     */
    public void addIcon(Material material, Bitmap image) {
        this.icons.put(material, image);
    }

    /**
     * Adds a nation-specific icon for the specified material and nation.
     *
     * @param material the material
     * @param nation   the nation
     * @param image    the bitmap image to add
     */
    public void addNationSpecificIcon(Material material, Nation nation, Bitmap image) {
        nationIcons.computeIfAbsent(nation, k -> new HashMap<>()).put(material, image);
    }

    /**
     * Writes the image atlas and inventory icons to the specified directory using the given palette.
     *
     * @param directory the directory to save the atlas and icons
     * @param palette   the palette to use for the images
     * @throws IOException if an I/O error occurs
     */
    public void writeImageAtlas(String directory, Palette palette) throws IOException {
        ImageBoard imageBoard = new ImageBoard();

        icons.forEach((material, image) -> imageBoard.placeImageBottom(
                image,
                "generic",
                material.name().toUpperCase()));

        nationIcons.forEach((nation, materialMap) -> materialMap.forEach((material, image) ->
                imageBoard.placeImageBottom(
                        image,
                        "nationSpecific",
                        nation.name().toUpperCase(),
                        material.name().toUpperCase())));

        imageBoard.writeBoard(directory, "image-atlas-inventory-icons", palette);

        // Write inventory icons
        Path inventoryIconDir = Paths.get(directory, "inventory-icons");

        Files.createDirectory(inventoryIconDir);

        // Write generic inventory icons
        for (var entry : icons.entrySet()) {
            entry.getValue().writeToFile(
                    inventoryIconDir.resolve(String.format("%s.png", entry.getKey().name().toUpperCase())));
        }

        // Write nation-specific inventory icons
        for (var entry : nationIcons.entrySet()) {
            var nationSpecificPath = inventoryIconDir.resolve(entry.getKey().name().toUpperCase());
            Files.createDirectories(nationSpecificPath);

            for (var materialBitmapEntry : entry.getValue().entrySet()) {
                materialBitmapEntry.getValue().writeToFile(
                        nationSpecificPath.resolve(String.format("%s.png", materialBitmapEntry.getKey().name().toUpperCase())));
            }
        }
    }
}
