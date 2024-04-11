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

    private final Map<Material, Bitmap> icons;
    private final Map<Nation, Map<Material, Bitmap>> nationIcons;

    public InventoryImageCollection() {
        icons = new HashMap<>();
        nationIcons = new HashMap<>();
    }

    public void addIcon(Material material, Bitmap image) {
        this.icons.put(material, image);
    }

    public void addNationSpecificIcon(Material material, Nation nation, Bitmap image) {
        if (!this.nationIcons.containsKey(nation)) {
            this.nationIcons.put(nation, new HashMap<>());
        }

        this.nationIcons.get(nation).put(material, image);
    }

    public void writeImageAtlas(String directory, Palette palette) throws IOException {
        ImageBoard imageBoard = new ImageBoard();

        icons.forEach((material, image) -> imageBoard.placeImageBottom(
                image,
                "generic",
                material.name().toUpperCase()));

        nationIcons.forEach((nation, materialMap) -> materialMap
                        .forEach((material, image) -> imageBoard.placeImageBottom(
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
                    Paths.get(inventoryIconDir.toString(), entry.getKey().name().toUpperCase() + ".png")
            );
        }

        // Write nation-specific inventory icons
        for (var entry : nationIcons.entrySet()) {
            Path nationSpecificPath = Paths.get(inventoryIconDir.toString(), entry.getKey().name().toUpperCase());

            if (!Files.isDirectory(nationSpecificPath)) {
                Files.createDirectory(nationSpecificPath);
            }

            for (var materialBitmapEntry : entry.getValue().entrySet()) {
                materialBitmapEntry.getValue().writeToFile(
                        Paths.get(nationSpecificPath.toString(), materialBitmapEntry.getKey().name().toUpperCase() + ".png")
                );
            }
        }
    }
}
