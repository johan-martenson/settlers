package org.appland.settlers.model;

import java.util.EnumSet;
import java.util.Set;

public enum DetailedVegetation {
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

    public static final Set<DetailedVegetation> CAN_BUILD_ON = EnumSet.of(
            SAVANNAH,
            BUILDABLE_WATER,
            MEADOW_1,
            MEADOW_2,
            MEADOW_3,
            STEPPE,
            FLOWER_MEADOW,
            MOUNTAIN_MEADOW,
            BUILDABLE_MOUNTAIN);

    public static final Set<DetailedVegetation> CAN_BUILD_ROAD_ON = EnumSet.of(
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

    public static final Set<DetailedVegetation> CAN_WALK_ON = EnumSet.of(
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

    public static final Set<DetailedVegetation> WATER_VEGETATION = EnumSet.of(WATER, WATER_2, BUILDABLE_WATER);

    public static final Set<DetailedVegetation> MINABLE_MOUNTAIN = EnumSet.of(MOUNTAIN_1, MOUNTAIN_2, MOUNTAIN_3, MOUNTAIN_4);

    public static final Set<DetailedVegetation> WILD_ANIMAL_CAN_NOT_WALK_ON = EnumSet.of(
            WATER,
            WATER_2,
            BUILDABLE_WATER,
            LAVA,
            LAVA_2,
            LAVA_3,
            LAVA_4
    );

    public static final Set<DetailedVegetation> CAN_USE_WELL = EnumSet.of(
            MEADOW_1,
            MEADOW_2,
            MEADOW_3
    );

    static final Set<DetailedVegetation> DEAD_TREE_NOT_ALLOWED = EnumSet.of(SNOW, WATER, WATER_2, BUILDABLE_WATER, MAGENTA);

    public boolean canWalkOn() {
        return CAN_WALK_ON.contains(this);
    }
}
