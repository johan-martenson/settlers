/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.maps;

/**
 *
 * @author johan
 */
public enum TerrainType {
    GREENLAND(0),
    WASTELAND(1),
    WINTER(2);

    private final int id;

    TerrainType(int id) {
        this.id = id;
    }

    static TerrainType fromUint8(short b) {
        for (TerrainType value : TerrainType.values()) {
            if (value.id == (int)b) {
                return value;
            }
        }

        return null;
    }
}
