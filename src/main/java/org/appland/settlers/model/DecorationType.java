package org.appland.settlers.model;

import java.util.EnumSet;
import java.util.Set;

public enum DecorationType {

    // Ordered according to MapBobs0.lst
    MINI_BROWN_MUSHROOM,
    TOADSTOOL,
    MINI_STONE,
    SMALL_STONE,
    STONE,
    DEAD_TREE_LYING_DOWN,
    ANIMAL_SKELETON_1,
    ANIMAL_SKELETON_2,
    FLOWERS,
    LARGE_BUSH,
    PILE_OF_STONES,
    CACTUS_1,
    CACTUS_2,
    CATTAIL,
    STONE_REMAINING_STYLE_1,

    // Gap 5 - stones to be cut

    STONE_REMAINING_STYLE_2,

    // Gap 5 - stones to be cut

    CUT_TREE_REMAINING,

    // Gap 4 - corn style 1 growing

    CORN_REMAINING_STYLE_1,

    // Gap 4 - corn style 2 growing

    CORN_REMAINING_STYLE_2,

    HUMAN_SKELETON_1,
    HUMAN_SKELETON_2,
    BROWN_MUSHROOM,
    MINI_STONE_WITH_GRASS,
    SMALL_STONE_WITH_GRASS,
    SOME_SMALL_STONES,
    SOME_SMALLER_STONES,
    FEW_SMALL_STONES,
    SPARSE_BUSH,
    SOME_WATER,
    MINI_GRASS,
    SNOWMAN,
    PORTAL,
    SHINING_PORTAL,

    // Mis1Bobs.lst
    SHARP_STONES_1,
    SHARP_STONES_2,
    SHARP_STONES_3,
    SHARP_STONES_4,
    SHARP_STONES_5,
    SHARP_STONES_6,
    SHARP_STONES_7,
    SKELETON_TUNNEL,

    // Other
    STRANDED_SHIP,
    GUARDHOUSE_RUIN,
    SMALL_VIKING_WITH_BOAT,
    WHALE_SKELETON_HEAD_RIGHT,
    WHALE_SKELETON_HEAD_LEFT,
    WATCHTOWER_RUIN,
    FORTRESS_RUIN,
    TENT_HEADQUARTERS,
    TENT,
    SCROLLS,
    CAVE,



    LARGER_STONES,
    DEAD_TREE,


    GRASS_1,
    BUSH,
    SMALL_BUSH,
    MINI_BUSH,
    GRASS_2,
    LITTLE_GRASS,
    DEAD_TREE_2,
    DEAD_TREE_3,
    SOLDIER_SKELETON,
    TREE_STUB;

    public static final Set<DecorationType> NO_IMPACT_ON_GAME;

    static {
        NO_IMPACT_ON_GAME = EnumSet.of(
                MINI_BROWN_MUSHROOM,
                TOADSTOOL,
                MINI_STONE,
                SMALL_STONE,
                STONE,
                ANIMAL_SKELETON_2,
                FLOWERS,
                LARGE_BUSH,
                PILE_OF_STONES,
                CATTAIL,
                GRASS_1,
                GRASS_2,
                MINI_GRASS,
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
