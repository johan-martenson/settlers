package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.model.Material;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;

public class CargoImageCollection {
    private final Map<Material, Bitmap> cargos;
    private final Map<Nation, Map<Material, Bitmap>> nationCargos;

    public CargoImageCollection() {
        nationCargos = new EnumMap<>(Nation.class);
        cargos = new EnumMap<>(Material.class);

        for (Nation nation : Nation.values()) {
            nationCargos.put(nation, new EnumMap<>(Material.class));
        }
    }

    public void addCargoImage(Material material, Bitmap image) {
        cargos.put(material, image);
    }

    public void addCargoImageForNation(Nation nation, Material material, Bitmap image) {
        nationCargos.get(nation).put(material, image);
    }

    public void writeImageAtlas(String toDir, Palette palette) throws IOException {
        ImageBoard imageBoard = new ImageBoard();

        // Fill in the image atlas
        Point cursor = new Point(0, 0);

        // Generic (non-nation specific) cargo images
        imageBoard.placeImagesAsColumn(
                cargos.entrySet().stream().map(
                        (entry) -> ImageBoard.makeImagePathPair(
                                entry.getValue(),
                                "generic",
                                entry.getKey().name().toUpperCase()
                        ))
                        .toList());

        cursor.y = 0;
        cursor.x = imageBoard.getCurrentWidth();

        for (Nation nation : Nation.values()) {
            for (Map.Entry<Material, Bitmap> entry : nationCargos.get(nation).entrySet()) {
                Material material = entry.getKey();
                Bitmap image = entry.getValue();

                imageBoard.placeImage(
                        image,
                        cursor,
                        "nationSpecific",
                        material.name().toUpperCase()
                );

                cursor.y = cursor.y + image.getHeight();
            }
        }

        // Write the image atlas to file
        imageBoard.writeBoard(toDir, "image-atlas-cargos", palette);

        // Write cargo icons
        Path cargoIconDir = Paths.get(toDir, "cargo-icons");

        Files.createDirectory(cargoIconDir);

        // Write generic material icons
        for (Map.Entry<Material, Bitmap> entry : cargos.entrySet()) {
            Path iconPath = Paths.get(cargoIconDir.toString(), entry.getKey().name().toUpperCase() + ".png");

            entry.getValue().writeToFile(iconPath);
        }

        // Write nation-specific material icons
        for (Nation nation : Nation.values()) {
            Path nationIconPath = Paths.get(cargoIconDir.toString(), nation.name().toUpperCase());

            if (!Files.isDirectory(nationIconPath)) {
                Files.createDirectory(nationIconPath);
            }

            for (Map.Entry<Material, Bitmap> entry : nationCargos.get(nation).entrySet()) {
                Path iconPath = Paths.get(nationIconPath.toString(), entry.getKey().name().toUpperCase() + ".png");

                entry.getValue().writeToFile(iconPath);
            }
        }
    }
}
