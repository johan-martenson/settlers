package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.CompassDirection;
import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.utils.ImageTransformer;
import org.appland.settlers.model.Material;

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

        // Fill in the images into the image atlas
        Point cursor = new Point(0, 0);

        // Fill in animal walking in each direction
        directionToImageMap.forEach(
                (direction, images) -> imageBoard.placeImageSeriesBottom(
                        ImageTransformer.normalizeImageSeries(images),
                        "images",
                        direction.name().toUpperCase()
                ));

        // Fill in shadows if they exist. One per direction
        cursor.y = imageBoard.getCurrentHeight();

        if (!shadowImages.isEmpty()) {
            cursor.x = 0;

            for (Map.Entry<CompassDirection, Bitmap> entry : shadowImages.entrySet()) {
                CompassDirection compassDirection = entry.getKey();
                Bitmap shadowImage = entry.getValue();

                imageBoard.placeImage(shadowImage, cursor, "shadowImages", compassDirection.name().toUpperCase());

                cursor.x = cursor.x + shadowImage.getWidth();
            }
        }

        // Write the cargos (if any)
        if (!cargoImages.isEmpty()) {
            cursor.y = imageBoard.getCurrentHeight();
            cursor.x = 0;

            for (Map.Entry<Material, Bitmap> entry : cargoImages.entrySet()) {
                Material material = entry.getKey();
                Bitmap image = entry.getValue();

                imageBoard.placeImage(image, cursor, "cargos", material.name().toUpperCase());

                cursor.x += image.getWidth();
            }
        }

        // Write nation-specific cargos (if any)
        if (!nationCargoImages.isEmpty()) {
            cursor.y = imageBoard.getCurrentHeight();
            cursor.x = 0;

            for (Map.Entry<Nation, Map<Material, Bitmap>> nationEntry : nationCargoImages.entrySet()) {
                Nation nation = nationEntry.getKey();
                Map<Material, Bitmap> materialBitmapMap = nationEntry.getValue();

                for (Map.Entry<Material, Bitmap> entry : materialBitmapMap.entrySet()) {
                    Material material = entry.getKey();
                    Bitmap image = entry.getValue();

                    imageBoard.placeImage(
                            image,
                            cursor,
                            "nationSpecific",
                            nation.name().toUpperCase(),
                            material.name().toUpperCase()
                    );

                    cursor.x += image.getWidth();
                }
            }
        }

        // Write the image atlas
        imageBoard.writeBoardToBitmap(palette).writeToFile(directory + "/" + "image-atlas-" + name.toLowerCase() + ".png");

        Path filePath = Paths.get(directory, "image-atlas-" + name.toLowerCase() + ".json");

        Files.writeString(filePath, imageBoard.getMetadataAsJson().toJSONString());
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
