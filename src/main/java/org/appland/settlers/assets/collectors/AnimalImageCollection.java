package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.CompassDirection;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.utils.NormalizedImageList;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.model.Material;
import org.json.simple.JSONObject;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AnimalImageCollection {
    private final String name;
    private final Map<CompassDirection, List<Bitmap>> directionToImageMap;
    private final Map<CompassDirection, Bitmap> shadowImages;
    private final Map<Material, Bitmap> cargoImages;
    private final Map<Nation, Map<Material, Bitmap>> nationCargoImages;

    public AnimalImageCollection(String name) {
        this.name = name;
        directionToImageMap = new EnumMap<>(CompassDirection.class);

        for (CompassDirection compassDirection : CompassDirection.values()) {
            this.directionToImageMap.put(compassDirection, new ArrayList<>());
        }
        shadowImages = new EnumMap<>(CompassDirection.class);
        cargoImages = new EnumMap<>(Material.class);
        nationCargoImages = new EnumMap<>(Nation.class);
    }

    public void addImage(CompassDirection compassDirection, Bitmap workerImage) {
        this.directionToImageMap.get(compassDirection).add(workerImage);
    }

    public void writeImageAtlas(String directory, Palette palette) throws IOException {

        // Write the image atlas, one row per direction, and collect metadata to write as json
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();
        JSONObject jsonImages = new JSONObject();

        jsonImageAtlas.put("images", jsonImages);

        // Fill in the images into the image atlas
        Point cursor = new Point(0, 0);

        // Fill in animal walking in each direction
        for (CompassDirection compassDirection : CompassDirection.values()) {

            cursor.x = 0;

            List<Bitmap> directionImages = directionToImageMap.get(compassDirection);
            NormalizedImageList directionNormalizedList = new NormalizedImageList(directionImages);
            List<Bitmap> normalizedDirectionImages = directionNormalizedList.getNormalizedImages();

            imageBoard.placeImageSeries(normalizedDirectionImages, cursor, ImageBoard.LayoutDirection.ROW);

            JSONObject jsonDirectionInfo = imageBoard.imageSeriesLocationToJson(normalizedDirectionImages);

            jsonImages.put(compassDirection.name().toUpperCase(), jsonDirectionInfo);

            cursor.y = cursor.y + directionNormalizedList.getImageHeight();
        }

        // Fill in shadows if they exist. One per direction
        if (!shadowImages.isEmpty()) {
            cursor.x = 0;

            JSONObject jsonShadowImages = new JSONObject();

            jsonImageAtlas.put("shadowImages", jsonShadowImages);

            for (Map.Entry<CompassDirection, Bitmap> entry : shadowImages.entrySet()) {
                CompassDirection compassDirection = entry.getKey();
                Bitmap shadowImage = entry.getValue();

                imageBoard.placeImage(shadowImage, cursor);

                jsonShadowImages.put(compassDirection.name().toUpperCase(), imageBoard.imageLocationToJson(shadowImage));

                cursor.x = cursor.x + shadowImage.getWidth();
            }
        }

        // Write the cargos (if any)
        if (!cargoImages.isEmpty()) {
            JSONObject jsonCargos = new JSONObject();

            jsonImageAtlas.put("cargos", jsonCargos);

            cursor.y = imageBoard.getCurrentHeight();
            cursor.x = 0;

            for (Map.Entry<Material, Bitmap> entry : cargoImages.entrySet()) {
                Material material = entry.getKey();
                Bitmap image = entry.getValue();

                imageBoard.placeImage(image, cursor);

                jsonCargos.put(material.name().toUpperCase(), imageBoard.imageLocationToJson(image));

                cursor.x += image.getWidth();
            }
        }

        // Write nation-specific cargos (if any)
        if (!nationCargoImages.isEmpty()) {
            JSONObject jsonNationSpecific = new JSONObject();

            jsonImageAtlas.put("nationSpecific", jsonNationSpecific);

            cursor.y = imageBoard.getCurrentHeight();
            cursor.x = 0;

            for (Map.Entry<Nation, Map<Material, Bitmap>> nationEntry : nationCargoImages.entrySet()) {
                Nation nation = nationEntry.getKey();
                Map<Material, Bitmap> materialBitmapMap = nationEntry.getValue();

                JSONObject jsonNationCargos = new JSONObject();

                jsonNationSpecific.put(nation.name().toUpperCase(), jsonNationCargos);

                for (Map.Entry<Material, Bitmap> entry : materialBitmapMap.entrySet()) {
                    Material material = entry.getKey();
                    Bitmap image = entry.getValue();

                    imageBoard.placeImage(image, cursor);

                    jsonNationCargos.put(material.name().toUpperCase(), imageBoard.imageLocationToJson(image));

                    cursor.x += image.getWidth();
                }
            }
        }

        // Write the image atlas
        imageBoard.writeBoardToBitmap(palette).writeToFile(directory + "/" + "image-atlas-" + name.toLowerCase() + ".png");

        Path filePath = Paths.get(directory, "image-atlas-" + name.toLowerCase() + ".json");

        Files.writeString(filePath, jsonImageAtlas.toJSONString());
    }

    public void addImages(CompassDirection compassDirection, List<Bitmap> images) {
        this.directionToImageMap.get(compassDirection).addAll(images);
    }

    public void addShadowImage(CompassDirection compassDirection, Bitmap image) {
        shadowImages.put(compassDirection, image);
    }

    public void addCargoImage(Material material, Bitmap image) {
        cargoImages.put(material, image);
    }

    public void addNationSpecificCargoImage(Nation nation, Material material, Bitmap image) {
        if (!nationCargoImages.containsKey(nation)) {
            nationCargoImages.put(nation, new EnumMap<>(Material.class));
        }

        nationCargoImages.get(nation).put(material, image);
    }
}
