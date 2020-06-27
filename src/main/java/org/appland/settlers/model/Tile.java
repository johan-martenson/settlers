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

    @Override
    public String toString() {
        return vegetationType.name() + " tile";
    }

}
