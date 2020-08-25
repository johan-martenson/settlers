package org.appland.settlers.model;

public enum Vegetation {
                       // House    Flag   Mine  Sail   Walk     House    Flag   Mine  Sail   Walk   Walk (animal)
    WATER              (true,  false, false, false, false, false),  //  -        -      -     -       -
    GRASS              (true,  true,  false, false, true, true),   //  X        X      -     -       X
    SWAMP              (false, false, false, false, false, true),  //  -        -      -     -       -
    MOUNTAIN           (false, true,  true,  false, true, true),   //  -        X      X     -       X
    SAVANNAH           (true,  true,  false, false, true, true),   //  X        X      -     -       X
    SNOW               (false, false, false, false, false, true),  //  -        -      -     -       -
    DESERT             (false, true,  false, false, true, true),   //  -        X      -     -       X
    DEEP_WATER         (false, false, false, true,  false, false),  //  -        -      -     X       -
    SHALLOW_WATER      (true,  true,  false, false, true, false),   //  X        X      -     -       X
    STEPPE             (true,  true,  false, false, true, true),   //  X        X      -     -       X
    LAVA               (false, false, false, false, false, false),  //  -        -      -     -       -
    MAGENTA            (false, true,  false, false, true, true),   //  -        X      -     -       X
    MOUNTAIN_MEADOW    (true,  true,  false, false, true, true),   //  X        X      -     -       X
    BUILDABLE_MOUNTAIN (true,  true,  false, false, true, true)    //  X        X      -     -       X
    ;

    private final boolean canWalk;
    private final boolean canSail;
    private final boolean canBuild;
    private final boolean canPlaceFlag;
    private final boolean canPlaceMine;
    private final boolean canAnimalWalk;

    Vegetation(boolean canBuild, boolean canPlaceFlag, boolean canPlaceMine, boolean canSail, boolean canWalk, boolean canAnimalWalk) {
        this.canBuild = canBuild;
        this.canPlaceFlag = canPlaceFlag;
        this.canPlaceMine = canPlaceMine;
        this.canSail = canSail;
        this.canWalk = canWalk;
        this.canAnimalWalk = canAnimalWalk;
    }

    public boolean isBuildable() {
        return canBuild;
    }

    public boolean canBuildFlags() {
        return canPlaceFlag;
    }

    public boolean isAnyWater() {
        return this == SHALLOW_WATER || this == WATER || this == DEEP_WATER;
    }

    public boolean canWalkOn() {
        return canWalk;
    }

    public boolean canAnimalWalkOn() {
        return canAnimalWalk;
    }
}
