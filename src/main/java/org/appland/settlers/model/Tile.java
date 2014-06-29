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
public class Tile {
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
        WATER, GRASS, SWAMP
    }

    @Override
    public String toString() {
        return vegetationType.name() + " tile";
    }
}
