package org.appland.settlers.maps;

import org.appland.settlers.model.Tree;
import org.appland.settlers.model.TreeSize;

import java.util.HashMap;
import java.util.Map;

public class TreeTranslator {

    public static final Map<Integer, Tree.TreeType> DEFAULT_ID_TO_TREE_TYPE_MAP;
    public static final Map<Integer, TreeSize> TREE_SIZE_MAP;

    static {
        DEFAULT_ID_TO_TREE_TYPE_MAP = new HashMap<>();

        DEFAULT_ID_TO_TREE_TYPE_MAP.put(0, Tree.TreeType.PINE);
        DEFAULT_ID_TO_TREE_TYPE_MAP.put(1, Tree.TreeType.BIRCH);
        DEFAULT_ID_TO_TREE_TYPE_MAP.put(2, Tree.TreeType.OAK);
        DEFAULT_ID_TO_TREE_TYPE_MAP.put(3, Tree.TreeType.PALM_1);
        DEFAULT_ID_TO_TREE_TYPE_MAP.put(4, Tree.TreeType.PALM_2);
        DEFAULT_ID_TO_TREE_TYPE_MAP.put(5, Tree.TreeType.PINE_APPLE);
        DEFAULT_ID_TO_TREE_TYPE_MAP.put(6, Tree.TreeType.CYPRESS);
        DEFAULT_ID_TO_TREE_TYPE_MAP.put(7, Tree.TreeType.CHERRY);
        DEFAULT_ID_TO_TREE_TYPE_MAP.put(8, Tree.TreeType.FIR);

        TREE_SIZE_MAP = new HashMap<>();

        TREE_SIZE_MAP.put(0, TreeSize.NEWLY_PLANTED);
        TREE_SIZE_MAP.put(1, TreeSize.SMALL);
        TREE_SIZE_MAP.put(2, TreeSize.MEDIUM);
        TREE_SIZE_MAP.put(3, TreeSize.FULL_GROWN);
    }
}
