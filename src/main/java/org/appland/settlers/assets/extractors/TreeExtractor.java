package org.appland.settlers.assets.extractors;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.UnknownResourceTypeException;
import org.appland.settlers.assets.collectors.TreeImageCollection;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.gamefiles.MapBobsLst;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.model.Tree;

import java.io.IOException;

import static org.appland.settlers.assets.Utils.getImageAt;
import static org.appland.settlers.assets.Utils.getImagesAt;

public class TreeExtractor {
    private static void log(String log) {
        System.out.println(log);
    }

    public static void extractTrees(String fromDir, String toDir, Palette defaultPalette) throws UnknownResourceTypeException, IOException, InvalidFormatException {
        log("");
        log("Extracting trees:");

        var mapBobsLst = LstDecoder.loadLstFile(fromDir + "/" + MapBobsLst.FILENAME, defaultPalette);

        // Collect tree images
        var trees = new TreeImageCollection("trees");

        // Extract animation for tree type 1 in wind -- cypress (?)
        trees.addImagesForTree(Tree.TreeType.CYPRESS, getImagesAt(mapBobsLst, MapBobsLst.CYPRESS_TREE_ANIMATION));
        trees.addImagesForTreeShadow(Tree.TreeType.CYPRESS, getImagesAt(mapBobsLst, MapBobsLst.CYPRESS_TREE_SHADOW_ANIMATION));

        trees.addImagesForTreeFalling(Tree.TreeType.CYPRESS, getImagesAt(mapBobsLst, MapBobsLst.CYPRESS_FALLING));
        trees.addImagesForTreeFallingShadow(Tree.TreeType.CYPRESS, getImagesAt(mapBobsLst, MapBobsLst.CYPRESS_FALLING_SHADOW));

        trees.addImageForGrowingTree(Tree.TreeType.CYPRESS, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_SMALLEST));
        trees.addImageForGrowingTree(Tree.TreeType.CYPRESS, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_SMALL));
        trees.addImageForGrowingTree(Tree.TreeType.CYPRESS, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_ALMOST_GROWN));

        trees.addImageForGrowingTreeShadow(Tree.TreeType.CYPRESS, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_SHADOW_SMALLEST));
        trees.addImageForGrowingTreeShadow(Tree.TreeType.CYPRESS, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_SHADOW_SMALL));
        trees.addImageForGrowingTreeShadow(Tree.TreeType.CYPRESS, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_SHADOW_MEDIUM));

        log(" - Cypress");

        // Extract animation for tree type 2 in wind -- birch, for sure
        trees.addImagesForTree(Tree.TreeType.BIRCH, getImagesAt(mapBobsLst, MapBobsLst.BIRCH_TREE_ANIMATION));
        trees.addImagesForTreeShadow(Tree.TreeType.BIRCH, getImagesAt(mapBobsLst, MapBobsLst.BIRCH_TREE_SHADOW_ANIMATION));

        trees.addImagesForTreeFalling(Tree.TreeType.BIRCH, getImagesAt(mapBobsLst, MapBobsLst.BIRCH_FALLING));
        trees.addImagesForTreeFallingShadow(Tree.TreeType.BIRCH, getImagesAt(mapBobsLst, MapBobsLst.BIRCH_FALLING_SHADOW));

        trees.addImageForGrowingTree(Tree.TreeType.BIRCH, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.BIRCH_SMALLEST));
        trees.addImageForGrowingTree(Tree.TreeType.BIRCH, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.BIRCH_SMALL));
        trees.addImageForGrowingTree(Tree.TreeType.BIRCH, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.BIRCH_ALMOST_GROWN));

        trees.addImageForGrowingTreeShadow(Tree.TreeType.BIRCH, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.BIRCH_SHADOW_SMALLEST));
        trees.addImageForGrowingTreeShadow(Tree.TreeType.BIRCH, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.BIRCH_SHADOW_SMALL));
        trees.addImageForGrowingTreeShadow(Tree.TreeType.BIRCH, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.BIRCH_SHADOW_MEDIUM));

        log(" - Birch");

        // Extract animation for tree type 3 in wind -- oak
        trees.addImagesForTree(Tree.TreeType.OAK, getImagesAt(mapBobsLst, MapBobsLst.OAK_TREE_ANIMATION));
        trees.addImagesForTreeShadow(Tree.TreeType.OAK, getImagesAt(mapBobsLst, MapBobsLst.OAK_TREE_SHADOW_ANIMATION));

        trees.addImagesForTreeFalling(Tree.TreeType.OAK, getImagesAt(mapBobsLst, MapBobsLst.OAK_FALLING));
        trees.addImagesForTreeFallingShadow(Tree.TreeType.OAK, getImagesAt(mapBobsLst, MapBobsLst.OAK_FALLING_SHADOW));

        trees.addImageForGrowingTree(Tree.TreeType.OAK, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.OAK_SMALLEST));
        trees.addImageForGrowingTree(Tree.TreeType.OAK, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.OAK_SMALL));
        trees.addImageForGrowingTree(Tree.TreeType.OAK, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.OAK_ALMOST_GROWN));

        trees.addImageForGrowingTreeShadow(Tree.TreeType.OAK, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.OAK_SHADOW_SMALLEST));
        trees.addImageForGrowingTreeShadow(Tree.TreeType.OAK, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.OAK_SHADOW_SMALL));
        trees.addImageForGrowingTreeShadow(Tree.TreeType.OAK, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.OAK_SHADOW_MEDIUM));

        log(" - Oak");

        // Extract animation for tree type 4 in wind -- short palm
        trees.addImagesForTree(Tree.TreeType.PALM_1, getImagesAt(mapBobsLst, MapBobsLst.PALM_1_TREE_ANIMATION));
        trees.addImagesForTreeShadow(Tree.TreeType.PALM_1, getImagesAt(mapBobsLst, MapBobsLst.PALM_1_TREE_SHADOW_ANIMATION));

        trees.addImagesForTreeFalling(Tree.TreeType.PALM_1, getImagesAt(mapBobsLst, MapBobsLst.PALM_1_FALLING));
        trees.addImagesForTreeFallingShadow(Tree.TreeType.PALM_1, getImagesAt(mapBobsLst, MapBobsLst.PALM_1_FALLING_SHADOW));

        trees.addImageForGrowingTree(Tree.TreeType.PALM_1, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PALM_1_SMALLEST));
        trees.addImageForGrowingTree(Tree.TreeType.PALM_1, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PALM_1_SMALL));
        trees.addImageForGrowingTree(Tree.TreeType.PALM_1, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PALM_1_ALMOST_GROWN));

        trees.addImageForGrowingTreeShadow(Tree.TreeType.PALM_1, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PALM_1_SHADOW_SMALLEST));
        trees.addImageForGrowingTreeShadow(Tree.TreeType.PALM_1, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PALM_1_SHADOW_SMALL));
        trees.addImageForGrowingTreeShadow(Tree.TreeType.PALM_1, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PALM_1_SHADOW_ALMOST_GROWN));

        log(" - Short palm tree");

        // Extract animation for tree type 5 in wind -- tall palm
        trees.addImagesForTree(Tree.TreeType.PALM_2, getImagesAt(mapBobsLst, MapBobsLst.PALM_2_TREE_ANIMATION));
        trees.addImagesForTreeShadow(Tree.TreeType.PALM_2, getImagesAt(mapBobsLst, MapBobsLst.PALM_2_TREE_SHADOW_ANIMATION));

        trees.addImagesForTreeFalling(Tree.TreeType.PALM_2, getImagesAt(mapBobsLst, MapBobsLst.PALM_2_FALLING));
        trees.addImagesForTreeFallingShadow(Tree.TreeType.PALM_2, getImagesAt(mapBobsLst, MapBobsLst.PALM_2_FALLING_SHADOW));

        trees.addImageForGrowingTree(Tree.TreeType.PALM_2, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PALM_2_SMALLEST));
        trees.addImageForGrowingTree(Tree.TreeType.PALM_2, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PALM_2_SMALL));
        trees.addImageForGrowingTree(Tree.TreeType.PALM_2, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PALM_2_ALMOST_GROWN));

        trees.addImageForGrowingTreeShadow(Tree.TreeType.PALM_2, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PALM_2_SHADOW_SMALLEST));
        trees.addImageForGrowingTreeShadow(Tree.TreeType.PALM_2, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PALM_2_SHADOW_SMALL));
        trees.addImageForGrowingTreeShadow(Tree.TreeType.PALM_2, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PALM_2_SHADOW_ALMOST_GROWN));

        log(" - Tall palm tree");

        // Extract animation for tree type 6 in wind -- fat palm - pineapple
        trees.addImagesForTree(Tree.TreeType.PINE_APPLE, getImagesAt(mapBobsLst, MapBobsLst.PINE_APPLE_ANIMATION));
        trees.addImagesForTreeShadow(Tree.TreeType.PINE_APPLE, getImagesAt(mapBobsLst, MapBobsLst.PINE_APPLE_SHADOW_ANIMATION));

        log(" - Pineapple");

        // Extract animation for tree type 7 in wind -- pine
        trees.addImagesForTree(Tree.TreeType.PINE, getImagesAt(mapBobsLst, MapBobsLst.PINE_TREE_ANIMATION));
        trees.addImagesForTreeShadow(Tree.TreeType.PINE, getImagesAt(mapBobsLst, MapBobsLst.PINE_TREE_SHADOW_ANIMATION));

        trees.addImagesForTreeFalling(Tree.TreeType.PINE, getImagesAt(mapBobsLst, MapBobsLst.PINE_FALLING));
        trees.addImagesForTreeFallingShadow(Tree.TreeType.PINE, getImagesAt(mapBobsLst, MapBobsLst.PINE_FALLING_SHADOW));

        trees.addImageForGrowingTree(Tree.TreeType.PINE, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PINE_SMALLEST));
        trees.addImageForGrowingTree(Tree.TreeType.PINE, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PINE_SMALL));
        trees.addImageForGrowingTree(Tree.TreeType.PINE, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PINE_ALMOST_GROWN));

        trees.addImageForGrowingTreeShadow(Tree.TreeType.PINE, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PINE_SMALLEST_SHADOW));
        trees.addImageForGrowingTreeShadow(Tree.TreeType.PINE, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PINE_SMALL_SHADOW));
        trees.addImageForGrowingTreeShadow(Tree.TreeType.PINE, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PINE_ALMOST_GROWN_SHADOW));

        log(" - Pine");

        // Extract animation for tree type 8 in wind -- cherry
        trees.addImagesForTree(Tree.TreeType.CHERRY, getImagesAt(mapBobsLst, MapBobsLst.CHERRY_TREE_ANIMATION));
        trees.addImagesForTreeShadow(Tree.TreeType.CHERRY, getImagesAt(mapBobsLst, MapBobsLst.CHERRY_TREE_SHADOW_ANIMATION));

        trees.addImagesForTreeFalling(Tree.TreeType.CHERRY, getImagesAt(mapBobsLst, MapBobsLst.CHERRY_FALLING));
        trees.addImagesForTreeFallingShadow(Tree.TreeType.CHERRY, getImagesAt(mapBobsLst, MapBobsLst.CHERRY_FALLING_SHADOW));

        trees.addImageForGrowingTree(Tree.TreeType.CHERRY, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.CHERRY_SMALLEST));
        trees.addImageForGrowingTree(Tree.TreeType.CHERRY, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.CHERRY_SMALL));
        trees.addImageForGrowingTree(Tree.TreeType.CHERRY, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.CHERRY_ALMOST_GROWN));

        trees.addImageForGrowingTreeShadow(Tree.TreeType.CHERRY, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.CHERRY_SMALLEST_SHADOW));
        trees.addImageForGrowingTreeShadow(Tree.TreeType.CHERRY, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.CHERRY_SMALL_SHADOW));
        trees.addImageForGrowingTreeShadow(Tree.TreeType.CHERRY, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.CHERRY_ALMOST_GROWN_SHADOW));

        log(" - Cherry");

        // Extract animation for tree type 9 in wind -- fir (?)
        trees.addImagesForTree(Tree.TreeType.FIR, getImagesAt(mapBobsLst, MapBobsLst.FIR_TREE_ANIMATION));
        trees.addImagesForTreeShadow(Tree.TreeType.FIR, getImagesAt(mapBobsLst, MapBobsLst.FIR_TREE_SHADOW_ANIMATION));

        trees.addImagesForTreeFalling(Tree.TreeType.FIR, getImagesAt(mapBobsLst, MapBobsLst.FIR_FALLING));
        trees.addImagesForTreeFallingShadow(Tree.TreeType.FIR, getImagesAt(mapBobsLst, MapBobsLst.FIR_FALLING_SHADOW));

        trees.addImageForGrowingTree(Tree.TreeType.FIR, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.FIR_SMALLEST));
        trees.addImageForGrowingTree(Tree.TreeType.FIR, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.FIR_SMALL));
        trees.addImageForGrowingTree(Tree.TreeType.FIR, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.FIR_ALMOST_GROWN));

        trees.addImageForGrowingTreeShadow(Tree.TreeType.FIR, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.FIR_SMALLEST_SHADOW));
        trees.addImageForGrowingTreeShadow(Tree.TreeType.FIR, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.FIR_SMALL_SHADOW));
        trees.addImageForGrowingTreeShadow(Tree.TreeType.FIR, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.FIR_ALMOST_GROWN_SHADOW));

        log(" - Fir");

        trees.writeImageAtlas(toDir, defaultPalette);
    }
}
