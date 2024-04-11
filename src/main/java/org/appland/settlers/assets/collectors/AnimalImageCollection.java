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
        ImageBoard imageBoard = new ImageBoard();

        directionToImageMap.forEach(
                (direction, images) -> imageBoard.placeImageSeriesBottom(
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

        imageBoard.writeBoard(directory, "image-atlas-" + name.toLowerCase(), palette);
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
