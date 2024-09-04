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
    private final Map<Tree.TreeType, List<Bitmap>> grownTreeMap = new EnumMap<>(Tree.TreeType.class);
    private final Map<Tree.TreeType, Map<Tree.TreeSize, Bitmap>> growingTreeMap = new EnumMap<>(Tree.TreeType.class);
    private final Map<Tree.TreeType, List<Bitmap>> treeFalling = new EnumMap<>(Tree.TreeType.class);
    private final Map<Tree.TreeType, List<Bitmap>> grownTreeShadowMap = new EnumMap<>(Tree.TreeType.class);
    private final Map<Tree.TreeType, List<Bitmap>> treeFallingShadow = new EnumMap<>(Tree.TreeType.class);
    private final Map<Tree.TreeType, Map<Tree.TreeSize, Bitmap>> growingTreeShadowMap = new EnumMap<>(Tree.TreeType.class);

    public TreeImageCollection(String name) {
        this.name = name;
    }

    /**
     * Writes the image atlas to the specified directory using the given palette.
     *
     * @param directory the directory to save the atlas
     * @param palette   the palette to use for the images
     * @throws IOException if an I/O error occurs
     */
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

        imageBoard.writeBoard(directory, String.format("image-atlas-%s", name.toLowerCase()), palette);
    }

    /**
     * Adds images for a fully grown tree of a specific type.
     *
     * @param treeType the type of the tree
     * @param images   the list of bitmap images
     */
    public void addImagesForTree(Tree.TreeType treeType, List<Bitmap> images) {
        grownTreeMap.computeIfAbsent(treeType, k -> new ArrayList<>()).addAll(images);
    }

    /**
     * Adds an image for a growing tree of a specific type and size.
     *
     * @param type     the type of the tree
     * @param treeSize the size of the tree
     * @param image    the bitmap image to add
     */
    public void addImageForGrowingTree(Tree.TreeType type, Tree.TreeSize treeSize, Bitmap image) {
        growingTreeMap.computeIfAbsent(type, k -> new EnumMap<>(Tree.TreeSize.class)).put(treeSize, image);
    }

    /**
     * Adds images for a tree falling animation of a specific type.
     *
     * @param type   the type of the tree
     * @param images the list of bitmap images
     */
    public void addImagesForTreeFalling(Tree.TreeType type, List<Bitmap> images) {
        treeFalling.put(type, images);
    }

    /**
     * Adds shadow images for a fully grown tree of a specific type.
     *
     * @param type   the type of the tree
     * @param images the list of bitmap images
     */
    public void addImagesForTreeShadow(Tree.TreeType type, List<Bitmap> images) {
        grownTreeShadowMap.computeIfAbsent(type, k -> new ArrayList<>()).addAll(images);
    }

    /**
     * Adds shadow images for a tree falling animation of a specific type.
     *
     * @param type   the type of the tree
     * @param images the list of bitmap images
     */
    public void addImagesForTreeFallingShadow(Tree.TreeType type, List<Bitmap> images) {
        treeFallingShadow.put(type, images);
    }

    /**
     * Adds a shadow image for a growing tree of a specific type and size.
     *
     * @param treeType the type of the tree
     * @param treeSize the size of the tree
     * @param image    the bitmap image to add
     */
    public void addImageForGrowingTreeShadow(Tree.TreeType treeType, Tree.TreeSize treeSize, Bitmap image) {
        growingTreeShadowMap.computeIfAbsent(treeType, k -> new EnumMap<>(Tree.TreeSize.class)).put(treeSize, image);
    }
}
