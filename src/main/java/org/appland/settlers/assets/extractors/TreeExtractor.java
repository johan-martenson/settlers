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
    public static void extractTrees(String fromDir, String toDir, Palette defaultPalette) throws UnknownResourceTypeException, IOException, InvalidFormatException {
        var mapBobsLst = LstDecoder.loadLstFile(fromDir + "/" + MapBobsLst.FILENAME, defaultPalette);

        // Collect tree images
        var treeImageCollection = new TreeImageCollection("trees");

        /* Extract animation for tree type 1 in wind -- cypress (?) */
        treeImageCollection.addImagesForTree(Tree.TreeType.CYPRESS, getImagesAt(mapBobsLst, MapBobsLst.CYPRESS_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.CYPRESS, getImagesAt(mapBobsLst, MapBobsLst.CYPRESS_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.CYPRESS, getImagesAt(mapBobsLst, MapBobsLst.CYPRESS_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.CYPRESS, getImagesAt(mapBobsLst, MapBobsLst.CYPRESS_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CYPRESS, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CYPRESS, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CYPRESS, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CYPRESS, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_SHADOW_SMALLEST));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CYPRESS, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_SHADOW_SMALL));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CYPRESS, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_SHADOW_MEDIUM));

        /* Extract animation for tree type 2 in wind -- birch, for sure */
        treeImageCollection.addImagesForTree(Tree.TreeType.BIRCH, getImagesAt(mapBobsLst, MapBobsLst.BIRCH_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.BIRCH, getImagesAt(mapBobsLst, MapBobsLst.BIRCH_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.BIRCH, getImagesAt(mapBobsLst, MapBobsLst.BIRCH_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.BIRCH, getImagesAt(mapBobsLst, MapBobsLst.BIRCH_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.BIRCH, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.BIRCH_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.BIRCH, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.BIRCH_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.BIRCH, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.BIRCH_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.BIRCH, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.BIRCH_SHADOW_SMALLEST));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.BIRCH, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.BIRCH_SHADOW_SMALL));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.BIRCH, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.BIRCH_SHADOW_MEDIUM));

        /* Extract animation for tree type 3 in wind -- oak */
        treeImageCollection.addImagesForTree(Tree.TreeType.OAK, getImagesAt(mapBobsLst, MapBobsLst.OAK_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.OAK, getImagesAt(mapBobsLst, MapBobsLst.OAK_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.OAK, getImagesAt(mapBobsLst, MapBobsLst.OAK_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.OAK, getImagesAt(mapBobsLst, MapBobsLst.OAK_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.OAK, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.OAK_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.OAK, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.OAK_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.OAK, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.OAK_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.OAK, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.OAK_SHADOW_SMALLEST));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.OAK, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.OAK_SHADOW_SMALL));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.OAK, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.OAK_SHADOW_MEDIUM));

        /* Extract animation for tree type 4 in wind -- short palm */
        treeImageCollection.addImagesForTree(Tree.TreeType.PALM_1, getImagesAt(mapBobsLst, MapBobsLst.PALM_1_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.PALM_1, getImagesAt(mapBobsLst, MapBobsLst.PALM_1_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.PALM_1, getImagesAt(mapBobsLst, MapBobsLst.PALM_1_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.PALM_1, getImagesAt(mapBobsLst, MapBobsLst.PALM_1_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_1, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PALM_1_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_1, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PALM_1_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_1, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PALM_1_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_1, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PALM_1_SHADOW_SMALLEST));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_1, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PALM_1_SHADOW_SMALL));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_1, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PALM_1_SHADOW_ALMOST_GROWN));

        /* Extract animation for tree type 5 in wind -- tall palm */
        treeImageCollection.addImagesForTree(Tree.TreeType.PALM_2, getImagesAt(mapBobsLst, MapBobsLst.PALM_2_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.PALM_2, getImagesAt(mapBobsLst, MapBobsLst.PALM_2_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.PALM_2, getImagesAt(mapBobsLst, MapBobsLst.PALM_2_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.PALM_2, getImagesAt(mapBobsLst, MapBobsLst.PALM_2_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_2, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PALM_2_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_2, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PALM_2_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_2, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PALM_2_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_2, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PALM_2_SHADOW_SMALLEST));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_2, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PALM_2_SHADOW_SMALL));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_2, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PALM_2_SHADOW_ALMOST_GROWN));

        /* Extract animation for tree type 6 in wind -- fat palm - pineapple */
        treeImageCollection.addImagesForTree(Tree.TreeType.PINE_APPLE, getImagesAt(mapBobsLst, MapBobsLst.PINE_APPLE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.PINE_APPLE, getImagesAt(mapBobsLst, MapBobsLst.PINE_APPLE_SHADOW_ANIMATION, 8));

        /* Extract animation for tree type 7 in wind -- pine */
        treeImageCollection.addImagesForTree(Tree.TreeType.PINE, getImagesAt(mapBobsLst, MapBobsLst.PINE_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.PINE, getImagesAt(mapBobsLst, MapBobsLst.PINE_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.PINE, getImagesAt(mapBobsLst, MapBobsLst.PINE_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.PINE, getImagesAt(mapBobsLst, MapBobsLst.PINE_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PINE, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PINE_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PINE, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PINE_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PINE, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PINE_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PINE, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PINE_SMALLEST_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PINE, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PINE_SMALL_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PINE, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PINE_ALMOST_GROWN_SHADOW));

        /* Extract animation for tree type 8 in wind -- cherry */
        treeImageCollection.addImagesForTree(Tree.TreeType.CHERRY, getImagesAt(mapBobsLst, MapBobsLst.CHERRY_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.CHERRY, getImagesAt(mapBobsLst, MapBobsLst.CHERRY_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.CHERRY, getImagesAt(mapBobsLst, MapBobsLst.CHERRY_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.CHERRY, getImagesAt(mapBobsLst, MapBobsLst.CHERRY_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CHERRY, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.CHERRY_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CHERRY, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.CHERRY_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CHERRY, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.CHERRY_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CHERRY, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.CHERRY_SMALLEST_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CHERRY, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.CHERRY_SMALL_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CHERRY, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.CHERRY_ALMOST_GROWN_SHADOW));

        /* Extract animation for tree type 9 in wind -- fir (?) */
        treeImageCollection.addImagesForTree(Tree.TreeType.FIR, getImagesAt(mapBobsLst, MapBobsLst.FIR_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.FIR, getImagesAt(mapBobsLst, MapBobsLst.FIR_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.FIR, getImagesAt(mapBobsLst, MapBobsLst.FIR_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.FIR, getImagesAt(mapBobsLst, MapBobsLst.FIR_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.FIR, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.FIR_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.FIR, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.FIR_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.FIR, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.FIR_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.FIR, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.FIR_SMALLEST_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.FIR, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.FIR_SMALL_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.FIR, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.FIR_ALMOST_GROWN_SHADOW));

        treeImageCollection.writeImageAtlas(toDir, defaultPalette);
    }
}
