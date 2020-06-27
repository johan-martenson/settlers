package org.appland.settlers.model;

public enum Vegetation {
                       // House    Flag   Mine  Sail   Walk     House    Flag   Mine  Sail   Walk
    WATER              (true,  false, false, false, false),  //  -        -      -     -       -
    GRASS              (true,  true,  false, false, true),   //  X        X      -     -       X
    SWAMP              (false, false, false, false, false),  //  -        -      -     -       -
    MOUNTAIN           (false, true,  true,  false, true),   //  -        X      X     -       X
    SAVANNAH           (true,  true,  false, false, true),   //  X        X      -     -       X
    SNOW               (false, false, false, false, false),  //  -        -      -     -       -
    DESERT             (false, true,  false, false, true),   //  -        X      -     -       X
    DEEP_WATER         (false, false, false, true,  false),  //  -        -      -     X       -
    SHALLOW_WATER      (true,  true,  false, false, true),   //  X        X      -     -       X
    STEPPE             (true,  true,  false, false, true),   //  X        X      -     -       X
    LAVA               (false, false, false, false, false),  //  -        -      -     -       -
    MAGENTA            (false, true,  false, false, true),   //  -        X      -     -       X
    MOUNTAIN_MEADOW    (true,  true,  false, false, true),   //  X        X      -     -       X
    BUILDABLE_MOUNTAIN (true,  true,  false, false, true)    //  X        X      -     -       X
    ;

    private final boolean canWalk;
    private final boolean canSail;
    private final boolean canBuild;
    private final boolean canPlaceFlag;
    private final boolean canPlaceMine;

    Vegetation(boolean canBuild, boolean canPlaceFlag, boolean canPlaceMine, boolean canSail, boolean canWalk) {
        this.canBuild = canBuild;
        this.canPlaceFlag = canPlaceFlag;
        this.canPlaceMine = canPlaceMine;
        this.canSail = canSail;
        this.canWalk = canWalk;
    }

    public boolean isBuildable() {
        return canBuild;
    }

    public boolean canBuildFlags() {
        return canPlaceFlag;
    }
}
