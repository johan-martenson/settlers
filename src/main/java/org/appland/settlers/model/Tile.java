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
    
    public Tile(Vegetation vegetation) {
        vegetationType = vegetation;
        
        amountGold = 0;
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

    void mineGold() {
        amountGold--;
    }

    public enum Vegetation {
        WATER, GRASS, SWAMP, MOUNTAIN
    }

    @Override
    public String toString() {
        return vegetationType.name() + " tile";
    }

    public void setAmountGold(Size amount) {
        switch (amount) {
        case SMALL:
            amountGold = SMALL_AMOUNT_OF_GOLD;
            break;
        case MEDIUM:
            amountGold = MEDIUM_AMOUNT_OF_GOLD;
            break;
        case LARGE:
            amountGold = LARGE_AMOUNT_OF_GOLD;
        }
    }
    
    int getAmountGold() {
        return amountGold;
    }
    
    int getAmountFish() {
        return amountFish;
    }
}
