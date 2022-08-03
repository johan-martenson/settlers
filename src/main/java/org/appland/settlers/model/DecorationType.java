package org.appland.settlers.model;

import java.util.EnumSet;
import java.util.Set;

public enum DecorationType {

    LARGER_STONES,

    STRANDED_SHIP,
    DEAD_TREE,
    SKELETON,
    TENT,
    GUARDHOUSE_RUIN,
    SMALL_VIKING_WITH_BOAT,
    WHALE_SKELETON_HEAD_RIGHT,
    WHALE_SKELETON_HEAD_LEFT,
    BROWN_MUSHROOM,
    MINI_BROWN_MUSHROOM,
    TOADSTOOL,
    MINI_STONE,
    SMALL_STONE,
    STONE,
    DEAD_TREE_LYING_DOWN,
    SMALL_SKELETON,
    FLOWERS,
    LARGE_BUSH,
    PILE_OF_STONES,
    CACTUS_1,
    CACTUS_2,
    CATTAIL,
    GRASS_1,
    BUSH,
    SMALL_BUSH,
    MINI_BUSH,
    GRASS_2,
    MINI_GRASS,
    PORTAL,
    SHINING_PORTAL,
    MINI_STONE_WITH_GRASS,
    SMALL_STONE_WITH_GRASS,
    SOME_SMALL_STONES,
    SOME_SMALLER_STONES,
    FEW_SMALL_STONES,
    SPARSE_BUSH,
    SOME_WATER,
    LITTLE_GRASS,
    SNOWMAN,
    TENT_HEADQUARTERS, SHARP_STONES_1, SHARP_STONES_2, SHARP_STONES_3, SHARP_STONES_4, SHARP_STONES_5, SHARP_STONES_6, SHARP_STONES_7, DEAD_TREE_2, DEAD_TREE_3, SKELETON_TUNNEL, WATCHTOWER_RUIN, FORTRESS_RUIN, SOLDIER_SKELETON, SCROLLS, CAVE;

    public static final Set<DecorationType> NO_IMPACT_ON_GAME;

    static {
        NO_IMPACT_ON_GAME = EnumSet.of(
                MINI_BROWN_MUSHROOM,
                TOADSTOOL,
                MINI_STONE,
                SMALL_STONE,
                STONE,
                SMALL_SKELETON,
                FLOWERS,
                LARGE_BUSH,
                CATTAIL,
                GRASS_1,
                GRASS_2,
                BUSH,
                SMALL_BUSH,
                MINI_BUSH,
                BROWN_MUSHROOM,
                MINI_STONE_WITH_GRASS,
                SMALL_STONE_WITH_GRASS,
                SOME_SMALL_STONES,
                SOME_SMALLER_STONES,
                FEW_SMALL_STONES,
                SPARSE_BUSH,
                SOME_WATER,
                LITTLE_GRASS
        );
    }
}
