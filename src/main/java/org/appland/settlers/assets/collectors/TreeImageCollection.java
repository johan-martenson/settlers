package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.utils.NormalizedImageList;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.model.Tree;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        // Write the image atlas, one row per tree, and collect metadata to write as json
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();

        JSONObject jsonGrownTrees = new JSONObject();
        JSONObject jsonGrownTreeShadows = new JSONObject();
        JSONObject jsonGrowingTrees = new JSONObject();
        JSONObject jsonGrowingTreeShadows = new JSONObject();
        JSONObject jsonFallingTrees = new JSONObject();
        JSONObject jsonFallingTreeShadows = new JSONObject();

        jsonImageAtlas.put("grownTrees", jsonGrownTrees);
        jsonImageAtlas.put("grownTreeShadows", jsonGrownTreeShadows);
        jsonImageAtlas.put("growingTrees", jsonGrowingTrees);
        jsonImageAtlas.put("growingTreeShadows", jsonGrowingTreeShadows);
        jsonImageAtlas.put("fallingTrees", jsonFallingTrees);
        jsonImageAtlas.put("fallingTreeShadows", jsonFallingTreeShadows);

        int y = 0;
        int x;
        int rowHeight = 0;
        for (Tree.TreeType treeType : Tree.TreeType.values()) {

            x = 0;

            // Grown tree animation
            List<Bitmap> images = this.grownTreeMap.get(treeType);
            NormalizedImageList normalizedImageList = new NormalizedImageList(images);
            List<Bitmap> normalizedImages = normalizedImageList.getNormalizedImages();

            imageBoard.placeImageSeries(normalizedImages, x, y, ImageBoard.LayoutDirection.ROW);

            JSONObject jsonGrownTreeInfo = imageBoard.imageSeriesLocationToJson(normalizedImages);

            jsonGrownTrees.put(treeType.name().toUpperCase(), jsonGrownTreeInfo);

            x = normalizedImageList.size() * normalizedImageList.getImageWidth();
            rowHeight = normalizedImageList.getImageHeight();

            // Grown tree shadow animation
            List<Bitmap> shadowImages = grownTreeShadowMap.get(treeType);
            NormalizedImageList normalizedShadowImageList = new NormalizedImageList(shadowImages);
            List<Bitmap> normalizedShadowImages = normalizedShadowImageList.getNormalizedImages();

            imageBoard.placeImageSeries(normalizedShadowImages, x, y, ImageBoard.LayoutDirection.ROW);

            JSONObject jsonGrownTreeShadowInfo = imageBoard.imageSeriesLocationToJson(normalizedShadowImages);

            jsonGrownTreeShadows.put(treeType.name().toUpperCase(), jsonGrownTreeShadowInfo);

            x = x + normalizedShadowImageList.size() * normalizedShadowImageList.getImageWidth();
            rowHeight = Math.max(rowHeight, normalizedShadowImageList.getImageHeight());

            // Growing tree
            if (growingTreeMap.containsKey(treeType)) {

                JSONObject jsonGrowingTreeType = new JSONObject();
                JSONObject jsonGrowingTreeShadowType = new JSONObject();

                jsonGrowingTrees.put(treeType.name().toUpperCase(), jsonGrowingTreeType);
                jsonGrowingTreeShadows.put(treeType.name().toUpperCase(), jsonGrowingTreeShadowType);

                // Growing tree
                for (Map.Entry<Tree.TreeSize, Bitmap> entry : growingTreeMap.get(treeType).entrySet()) {
                    Tree.TreeSize treeSize = entry.getKey();
                    Bitmap image = entry.getValue();

                    imageBoard.placeImage(image, x, y);

                    JSONObject jsonGrowingTreeImage = imageBoard.imageLocationToJson(image);

                    jsonGrowingTreeType.put(treeSize.name().toUpperCase(), jsonGrowingTreeImage);

                    x = x + image.getWidth();
                    rowHeight = Math.max(rowHeight, image.getHeight());
                }

                // Growing tree's shadow
                for (Map.Entry<Tree.TreeSize, Bitmap> entry : growingTreeShadowMap.get(treeType).entrySet()) {
                    Tree.TreeSize treeSize = entry.getKey();
                    Bitmap shadowImage = entry.getValue();

                    imageBoard.placeImage(shadowImage, x, y);

                    JSONObject jsonGrowingTreeShadowImage = imageBoard.imageLocationToJson(shadowImage);

                    jsonGrowingTreeShadowType.put(treeSize.name().toUpperCase(), jsonGrowingTreeShadowImage);

                    x = x + shadowImage.getWidth();
                    rowHeight = Math.max(rowHeight, shadowImage.getHeight());
                }
            }

            // Falling tree animation
            if (treeFalling.containsKey(treeType)) {

                // Falling tree animation
                List<Bitmap> fallingTreeImages = treeFalling.get(treeType);
                NormalizedImageList normalizedFallingTreeImageList = new NormalizedImageList(fallingTreeImages);
                List<Bitmap> normalizedFallingTreeImages = normalizedFallingTreeImageList.getNormalizedImages();

                imageBoard.placeImageSeries(normalizedFallingTreeImages, x, y, ImageBoard.LayoutDirection.ROW);

                JSONObject jsonFallingTreeImages = imageBoard.imageSeriesLocationToJson(normalizedFallingTreeImages); //new JSONObject();

                jsonFallingTrees.put(treeType.name().toUpperCase(), jsonFallingTreeImages);

                x = x + normalizedFallingTreeImageList.size() * normalizedFallingTreeImageList.getImageWidth();
                rowHeight = Math.max(rowHeight, normalizedFallingTreeImageList.getImageHeight());

                // Falling tree's shadow animation
                NormalizedImageList normalizedFallingTreeShadowList = new NormalizedImageList(treeFallingShadow.get(treeType));
                List<Bitmap> normalizedFallingTreeShadowImages = normalizedFallingTreeShadowList.getNormalizedImages();

                imageBoard.placeImageSeries(normalizedFallingTreeShadowImages, x, y, ImageBoard.LayoutDirection.ROW);

                JSONObject jsonFallingTreeShadowImages = imageBoard.imageSeriesLocationToJson(normalizedFallingTreeShadowImages);

                jsonFallingTreeShadows.put(treeType.name().toUpperCase(), jsonFallingTreeShadowImages);

                x = x + normalizedFallingTreeShadowList.size() * normalizedFallingTreeShadowList.getImageWidth();
                rowHeight = Math.max(rowHeight, normalizedFallingTreeShadowList.getImageHeight());
            }

            y = y + rowHeight;
        }

        imageBoard.writeBoardToBitmap(palette).writeToFile(directory + "/image-atlas-" + name.toLowerCase() + ".png");

        // Write a JSON file that specifies where each image is in pixels
        Path filePath = Paths.get(directory, "image-atlas-" + name.toLowerCase() + ".json");

        Files.writeString(filePath, jsonImageAtlas.toJSONString());
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
