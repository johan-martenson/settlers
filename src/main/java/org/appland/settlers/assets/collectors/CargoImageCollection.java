package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.model.Material;
import org.json.simple.JSONObject;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class CargoImageCollection {
    private final Map<Material, Bitmap> cargos;
    private final Map<Nation, Map<Material, Bitmap>> nationCargos;
    private final Map<Bitmap, Material> imageToMaterial;

    public CargoImageCollection() {
        nationCargos = new EnumMap<>(Nation.class);
        cargos = new EnumMap<>(Material.class);

        for (Nation nation : Nation.values()) {
            nationCargos.put(nation, new EnumMap<>(Material.class));
        }

        imageToMaterial = new HashMap<>();
    }

    public void addCargoImage(Material material, Bitmap image) {
        cargos.put(material, image);
        imageToMaterial.put(image, material);
    }

    public void addCargoImageForNation(Nation nation, Material material, Bitmap image) {
        nationCargos.get(nation).put(material, image);
    }

    public void writeImageAtlas(String toDir, Palette palette) throws IOException {

        // Create the image atlas
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();

        // Fill in the image atlas
        JSONObject jsonGeneric = new JSONObject();
        JSONObject jsonNationSpecific = new JSONObject();

        jsonImageAtlas.put("generic", jsonGeneric);
        jsonImageAtlas.put("nationSpecific", jsonNationSpecific);

        Point cursor = new Point(0, 0);
        int maxGenericCargoWidth = 0;

        // Generic (non-nation specific) cargo images
        for (Map.Entry<Material, Bitmap> entry : cargos.entrySet()) {

            Material material = entry.getKey();
            Bitmap image = entry.getValue();

            imageBoard.placeImage(image, cursor);

            JSONObject jsonCargoImage = imageBoard.imageLocationToJson(image);

            jsonGeneric.put(material.name().toUpperCase(), jsonCargoImage);

            maxGenericCargoWidth = Math.max(maxGenericCargoWidth, image.getWidth());

            cursor.y = cursor.y + image.getHeight();
        }

        cursor.y = 0;
        cursor.x = maxGenericCargoWidth;

        for (Nation nation : Nation.values()) {

            JSONObject jsonNation = new JSONObject();

            jsonNationSpecific.put(nation.name().toUpperCase(), jsonNation);

            for (Map.Entry<Material, Bitmap> entry : nationCargos.get(nation).entrySet()) {

                Material material = entry.getKey();
                Bitmap image = entry.getValue();

                imageBoard.placeImage(image, cursor);

                JSONObject jsonCargoImage = imageBoard.imageLocationToJson(image);

                jsonNation.put(material.name().toUpperCase(), jsonCargoImage);

                cursor.y = cursor.y + image.getHeight();
            }
        }

        // Write the image atlas to file
        imageBoard.writeBoardToBitmap(palette).writeToFile(toDir + "/image-atlas-cargos.png");

        Files.writeString(Paths.get(toDir, "image-atlas-cargos.json"), jsonImageAtlas.toJSONString());

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
