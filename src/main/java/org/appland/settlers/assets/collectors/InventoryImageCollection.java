package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.model.Material;
import org.json.simple.JSONObject;

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

    public void writeImageAtlas(String toDir, Palette defaultPalette) throws IOException {

        // Create the image atlas
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();

        // Fill in the image atlas
        JSONObject jsonGeneric = new JSONObject();
        JSONObject jsonNationSpecific = new JSONObject();

        jsonImageAtlas.put("generic", jsonGeneric);
        jsonImageAtlas.put("nationSpecific", jsonNationSpecific);

        // Fill in generic icons
        for (Map.Entry<Material, Bitmap> entry : icons.entrySet()) {
            Material material = entry.getKey();
            Bitmap image = entry.getValue();

            imageBoard.placeImageBottom(image);

            JSONObject jsonIconImage = imageBoard.imageLocationToJson(image);

            jsonGeneric.put(material.name().toUpperCase(), jsonIconImage);
        }

        // Fill in nation-specific icons
        for (Map.Entry<Nation, Map<Material, Bitmap>> entry : nationIcons.entrySet()) {
            Nation nation = entry.getKey();
            Map<Material, Bitmap> iconsForNation = entry.getValue();

            JSONObject jsonNation = new JSONObject();

            jsonNationSpecific.put(nation.name().toUpperCase(), jsonNation);

            for (Map.Entry<Material, Bitmap> materialBitmapEntry : iconsForNation.entrySet()) {
                imageBoard.placeImageBottom(materialBitmapEntry.getValue());

                JSONObject jsonIcon = imageBoard.imageLocationToJson(materialBitmapEntry.getValue());

                jsonNation.put(materialBitmapEntry.getKey().name().toUpperCase(), jsonIcon);
            }
        }

        // Write the image atlas to file
        imageBoard.writeBoardToBitmap(defaultPalette).writeToFile(toDir + "/image-atlas-inventory-icons.png");

        Files.writeString(Paths.get(toDir, "image-atlas-inventory-icons.json"), jsonImageAtlas.toJSONString());

        // Write inventory icons
        Path inventoryIconDir = Paths.get(toDir, "inventory-icons");

        Files.createDirectory(inventoryIconDir);

        // Write generic inventory icons
        for (Map.Entry<Material, Bitmap> entry : icons.entrySet()) {
            Path iconPath = Paths.get(inventoryIconDir.toString(), entry.getKey().name().toUpperCase() + ".png");

            entry.getValue().writeToFile(iconPath);
        }

        // Write nation-specific inventory icons
        for (Map.Entry<Nation, Map<Material, Bitmap>> entry : nationIcons.entrySet()) {
            Path nationSpecificPath = Paths.get(inventoryIconDir.toString(), entry.getKey().name().toUpperCase());

            if (!Files.isDirectory(nationSpecificPath)) {
                Files.createDirectory(nationSpecificPath);
            }

            for (Map.Entry<Material, Bitmap> materialBitmapEntry : entry.getValue().entrySet()) {
                Path iconPath = Paths.get(nationSpecificPath.toString(), materialBitmapEntry.getKey().name().toUpperCase() + ".png");

                materialBitmapEntry.getValue().writeToFile(iconPath);
            }
        }
    }
}
