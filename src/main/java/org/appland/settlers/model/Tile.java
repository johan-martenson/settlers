/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

/**
 *
 * @author johan
 */
public class Tile { // FIXME: is this class needed or can all members be moved to MapPoint and the tiles are just vegetation enums?

    private Vegetation vegetationType;

    public Tile(Vegetation vegetation) {
        vegetationType = vegetation;
    }

    public void setVegetationType(Vegetation vegetation) {
        vegetationType = vegetation;
    }

    public Vegetation getVegetationType() {
        return vegetationType;
    }

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

    @Override
    public String toString() {
        return vegetationType.name() + " tile";
    }

}
