package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.utils.ImageTransformer;
import org.appland.settlers.model.Tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class TreeImageCollection {
    private final String name;
    private final Map<Tree.TreeType, List<Bitmap>> grownTreeMap;
    private final Map<Tree.TreeType, Map<Tree.TreeSize, Bitmap>> growingTreeMap;
    private final Map<Tree.TreeType, List<Bitmap>> treeFalling;
    private final Map<Tree.TreeType, List<Bitmap>> grownTreeShadowMap;
    private final Map<Tree.TreeType, List<Bitmap>> treeFallingShadow;
    private final Map<Tree.TreeType, Map<Tree.TreeSize, Bitmap>> growingTreeShadowMap;

    public TreeImageCollection(String name) {
        this.name = name;

        grownTreeMap = new EnumMap<>(Tree.TreeType.class);
        growingTreeMap = new EnumMap<>(Tree.TreeType.class);
        treeFalling = new EnumMap<>(Tree.TreeType.class);
        grownTreeShadowMap = new EnumMap<>(Tree.TreeType.class);
        treeFallingShadow = new EnumMap<>(Tree.TreeType.class);
        growingTreeShadowMap = new EnumMap<>(Tree.TreeType.class);

        for (Tree.TreeType treeType : Tree.TreeType.values()) {
            grownTreeMap.put(treeType, new ArrayList<>());
            grownTreeShadowMap.put(treeType, new ArrayList<>());
        }
    }

    public void writeImageAtlas(String directory, Palette palette) throws IOException {
        ImageBoard imageBoard = new ImageBoard();

        grownTreeMap.forEach((treeType, images) -> imageBoard.placeImageSeriesBottom(
                ImageTransformer.normalizeImageSeries(images),
                "grownTrees",
                treeType.name().toUpperCase()));

        grownTreeShadowMap.forEach((treeType, shadowImages) -> imageBoard.placeImageSeriesBottom(
                ImageTransformer.normalizeImageSeries(shadowImages),
                "grownTreeShadows",
                treeType.name().toUpperCase()));

        growingTreeMap.forEach((treeType, sizeMap) -> imageBoard.placeImagesAsRow(
                sizeMap.entrySet().stream()
                        .map((entry) -> ImageBoard.makeImagePathPair(
                                entry.getValue(),
                                "growingTrees",
                                treeType.name().toUpperCase(),
                                entry.getKey().name().toUpperCase()
                        )).toList()
        ));

        growingTreeShadowMap.forEach((treeType, sizeMap) -> imageBoard.placeImagesAsRow(
                sizeMap.entrySet().stream()
                        .map((entry) -> ImageBoard.makeImagePathPair(
                                entry.getValue(),
                                "growingTreeShadows",
                                treeType.name().toUpperCase(),
                                entry.getKey().name().toUpperCase()
                        )).toList()
        ));

        treeFalling.forEach((treeType, images) -> imageBoard.placeImageSeriesBottom(
                ImageTransformer.normalizeImageSeries(images),
                "fallingTrees",
                treeType.name().toUpperCase()));

        treeFallingShadow.forEach((treeType, images) -> imageBoard.placeImageSeriesBottom(
                ImageTransformer.normalizeImageSeries(images),
                "fallingTreeShadows",
                treeType.name().toUpperCase()));

        imageBoard.writeBoard(directory, "image-atlas-" + name.toLowerCase(), palette);
    }

    public void addImagesForTree(Tree.TreeType treeType, List<Bitmap> imagesFromResourceLocations) {
        this.grownTreeMap.get(treeType).addAll(imagesFromResourceLocations);
    }

    public void addImageForGrowingTree(Tree.TreeType type, Tree.TreeSize treeSize, Bitmap image) {
        if (!growingTreeMap.containsKey(type)) {
            growingTreeMap.put(type, new EnumMap<>(Tree.TreeSize.class));
        }

        growingTreeMap.get(type).put(treeSize, image);
    }

    public void addImagesForTreeFalling(Tree.TreeType type, List<Bitmap> images) {
        treeFalling.put(type, images);
    }

    public void addImagesForTreeShadow(Tree.TreeType type, List<Bitmap> images) {
        grownTreeShadowMap.get(type).addAll(images);
    }

    public void addImagesForTreeFallingShadow(Tree.TreeType type, List<Bitmap> images) {
        treeFallingShadow.put(type, images);
    }

    public void addImageForGrowingTreeShadow(Tree.TreeType treeType, Tree.TreeSize treeSize, Bitmap image) {
        if (!growingTreeShadowMap.containsKey(treeType)) {
            growingTreeShadowMap.put(treeType, new EnumMap<>(Tree.TreeSize.class));
        }

        growingTreeShadowMap.get(treeType).put(treeSize, image);
    }
}
