/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Tile.Vegetation.WATER;

/**
 *
 * @author johan
 */
public class Tile {
    private static final int DEFAULT_AMOUNT_FISH   = 10;
    private static final int SMALL_AMOUNT_OF_GOLD  = 5;
    private static final int MEDIUM_AMOUNT_OF_GOLD = 10;
    private static final int LARGE_AMOUNT_OF_GOLD  = 15;

    private Vegetation vegetationType;
    private int        amountGold;
    private int        amountFish;
    private int        amountIron;
    private int        amountGranite;
    private int        amountCoal;

    public Tile(Vegetation vegetation) {
        vegetationType = vegetation;

        amountGold    = 0;
        amountIron    = 0;
        amountCoal    = 0;
        amountGranite = 0;

        amountFish = 0;
    }

    public void setVegetationType(Vegetation vegetation) {
        vegetationType = vegetation;

        if (vegetationType == WATER) {
            amountFish = DEFAULT_AMOUNT_FISH;
        }
    }

    public Vegetation getVegetationType() {
        return vegetationType;
    }

    void consumeFish() {
        amountFish--;
    }

    void mine(Material mineral) {
        switch (mineral) {
        case GOLD:
            amountGold--;
            break;
        case IRON:
            amountIron--;
            break;
        case COAL:
            amountCoal--;
            break;
        case STONE:
            amountGranite--;
        }
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

    public void setAmountMineral(Material mineral, Size amount) {
        int nrAmount = 0;

        switch (amount) {
        case SMALL:
            nrAmount = SMALL_AMOUNT_OF_GOLD;
            break;
        case MEDIUM:
            nrAmount = MEDIUM_AMOUNT_OF_GOLD;
            break;
        case LARGE:
            nrAmount = LARGE_AMOUNT_OF_GOLD;
        }

        switch (mineral) {
        case GOLD:
            amountGold = nrAmount;
            break;
        case IRON:
            amountIron = nrAmount;
            break;
        case COAL:
            amountCoal = nrAmount;
            break;
        case STONE:
            amountGranite = nrAmount;
            break;
        default:

        }
    }

    int getAmountOfMineral(Material mineral) {
        switch (mineral) {
        case GOLD:
            return amountGold;
        case IRON:
            return amountIron;
        case COAL:
            return amountCoal;
        case STONE:
            return amountGranite;
        default:
            return 0;
        }
    }

    int getAmountFish() {
        return amountFish;
    }
}
