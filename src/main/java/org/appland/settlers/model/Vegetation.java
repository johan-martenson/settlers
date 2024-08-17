package org.appland.settlers.model;

import java.util.EnumSet;
import java.util.Set;

public enum Vegetation {
    SAVANNAH,
    MOUNTAIN_1,
    SNOW,
    SWAMP,
    DESERT_1,
    WATER,
    BUILDABLE_WATER,
    DESERT_2,
    MEADOW_1,
    MEADOW_2,
    MEADOW_3,
    MOUNTAIN_2,
    MOUNTAIN_3,
    MOUNTAIN_4,
    STEPPE,
    FLOWER_MEADOW,
    LAVA,
    MAGENTA,
    MOUNTAIN_MEADOW,
    WATER_2,
    LAVA_2,
    LAVA_3,
    LAVA_4,
    BUILDABLE_MOUNTAIN;

    public static final Set<Vegetation> CAN_BUILD_ON = EnumSet.of(
            SAVANNAH,
            BUILDABLE_WATER,
            MEADOW_1,
            MEADOW_2,
            MEADOW_3,
            STEPPE,
            FLOWER_MEADOW,
            MOUNTAIN_MEADOW,
            BUILDABLE_MOUNTAIN);

    public static final Set<Vegetation> CAN_BUILD_ROAD_ON = EnumSet.of(
            SAVANNAH,
            MOUNTAIN_1,
            DESERT_1,
            BUILDABLE_WATER,
            DESERT_2,
            MEADOW_1,
            MEADOW_2,
            MEADOW_3,
            MOUNTAIN_2,
            MOUNTAIN_3,
            MOUNTAIN_4,
            STEPPE,
            FLOWER_MEADOW,
            MAGENTA,
            MOUNTAIN_MEADOW,
            BUILDABLE_MOUNTAIN
    );

    public static final Set<Vegetation> CAN_WALK_ON = EnumSet.of(
            SAVANNAH,
            MOUNTAIN_1,
            DESERT_1,
            BUILDABLE_WATER,
            DESERT_2,
            MEADOW_1,
            MEADOW_2,
            MEADOW_3,
            MOUNTAIN_2,
            MOUNTAIN_3,
            MOUNTAIN_4,
            STEPPE,
            FLOWER_MEADOW,
            MAGENTA,
            MOUNTAIN_MEADOW,
            BUILDABLE_MOUNTAIN
    );

    public static final Set<Vegetation> WATER_VEGETATION = EnumSet.of(WATER, WATER_2, BUILDABLE_WATER);

    public static final Set<Vegetation> MINABLE_MOUNTAIN = EnumSet.of(MOUNTAIN_1, MOUNTAIN_2, MOUNTAIN_3, MOUNTAIN_4);

    public static final Set<Vegetation> WILD_ANIMAL_CAN_NOT_WALK_ON = EnumSet.of(
            WATER,
            WATER_2,
            BUILDABLE_WATER,
            LAVA,
            LAVA_2,
            LAVA_3,
            LAVA_4
    );

    public static final Set<Vegetation> CAN_USE_WELL = EnumSet.of(
            MEADOW_1,
            MEADOW_2,
            MEADOW_3
    );

    static final Set<Vegetation> DEAD_TREE_NOT_ALLOWED = EnumSet.of(SNOW, WATER, WATER_2, BUILDABLE_WATER, MAGENTA);

    public boolean canWalkOn() {
        return CAN_WALK_ON.contains(this);
    }

    public int toInt() {
        return switch (this) {
            case SAVANNAH -> 0;
            case MOUNTAIN_1 -> 1;
            case SNOW -> 2;
            case SWAMP -> 3;
            case DESERT_1 -> 4;
            case WATER -> 5;
            case BUILDABLE_WATER -> 6;
            case DESERT_2 -> 7;
            case MEADOW_1 -> 8;
            case MEADOW_2 -> 9;
            case MEADOW_3 -> 10;
            case MOUNTAIN_2 -> 11;
            case MOUNTAIN_3 -> 12;
            case MOUNTAIN_4 -> 13;
            case STEPPE -> 14;
            case FLOWER_MEADOW -> 15;
            case LAVA -> 16;
            case MAGENTA -> 17;
            case MOUNTAIN_MEADOW -> 18;
            case WATER_2 -> 19;
            case LAVA_2 -> 20;
            case LAVA_3 -> 21;
            case LAVA_4 -> 22;
            case BUILDABLE_MOUNTAIN -> 23;
        };
    }
}
