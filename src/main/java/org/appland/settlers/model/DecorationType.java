package org.appland.settlers.model;

import java.util.EnumSet;
import java.util.Set;

public enum DecorationType {

    // Ordered according to MapBobs0.lst
    MINI_BROWN_MUSHROOM, // 283
    TOADSTOOL,
    MINI_STONE,
    SMALL_STONE,
    STONE,
    DEAD_TREE_LYING_DOWN,
    DEAD_TREE,
    ANIMAL_SKELETON_1,
    ANIMAL_SKELETON_2,
    FLOWERS,
    LARGE_BUSH_1,
    PILE_OF_STONES,
    CACTUS_1,
    CACTUS_2,
    CATTAIL_1,
    CATTAIL_2,

    LARGE_BUSH_2, // 322
    BUSH_3,
    SMALL_BUSH,
    CATTAIL_3,
    CATTAIL_4,

    BROWN_MUSHROOM, // 329
    MINI_STONE_WITH_GRASS,
    SMALL_STONE_WITH_GRASS,
    SOME_SMALL_STONES_1,
    SOME_SMALL_STONES_2,
    SOME_SMALL_STONES_3,
    SPARSE_BUSH,
    SOME_WATER,
    LITTLE_GRASS,
    SNOWMAN,

    PORTAL, // 339
    SHINING_PORTAL,

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
    SOME_SMALL_STONES,
    SOME_SMALLER_STONES,
    FEW_SMALL_STONES,
    MINI_GRASS,

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


    GRASS_1,
    BUSH,
    MINI_BUSH,
    GRASS_2,
    DEAD_TREE_2,
    DEAD_TREE_3,
    SOLDIER_SKELETON,
    TREE_STUB;

    public static final Set<DecorationType> NO_IMPACT_ON_GAME = EnumSet.of(
                MINI_BROWN_MUSHROOM,
                TOADSTOOL,
                MINI_STONE,
                SMALL_STONE,
                STONE,
                ANIMAL_SKELETON_1,
                ANIMAL_SKELETON_2,
                FLOWERS,
                LARGE_BUSH_1,
                PILE_OF_STONES,
                CATTAIL_1,
                CATTAIL_2,
                LARGE_BUSH_2,
                BUSH_3,
                SMALL_BUSH,
                CATTAIL_3,
                CATTAIL_4,
                BROWN_MUSHROOM,
                MINI_STONE_WITH_GRASS,
                SMALL_STONE_WITH_GRASS,
                SOME_SMALL_STONES_1,
                SOME_SMALL_STONES_2,
                SOME_SMALL_STONES_3,
                SPARSE_BUSH,
                SOME_WATER,
                LITTLE_GRASS
            );

    public boolean canPlaceFlagOn() {
        return switch (this) {
            case CACTUS_1, CACTUS_2, SNOWMAN, PORTAL, SHINING_PORTAL -> false;
            default -> true;
        };
    }

    public boolean canPlaceBuildingOn() {
        return switch (this) {
            case CACTUS_1 , CACTUS_2, SNOWMAN, PORTAL, SHINING_PORTAL -> false;
            default -> true;
        };
    }

    public boolean canBuildRoadOn() {
        return switch (this) {
            case CACTUS_1, CACTUS_2, SNOWMAN, PORTAL, SHINING_PORTAL -> false;
            default -> true;
        };
    }
}
