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
public enum BuildableSite {
    FLAG,
    HUT,
    HOUSE,
    CASTLE,
    MINE,
    FLAG_NEXT_TO_INACCESSIBLE_TERRAIN,
    CASTLE_NEAR_WATER,
    MINE_NEAR_WATER,
    OCCUPIED_BY_TREE,
    OCCUPIED_BY_INACCESSIBLE_TERRAIN;

    static BuildableSite buildableSiteFromInt(short i) {
        switch (i) {
            case 0:
                return null;
            case 1:
                return FLAG;
            case 2:
                return HUT;
            case 3:
                return HOUSE;
            case 4:
                return CASTLE;
            case 5:
                return MINE;
            case 9:
                return FLAG_NEXT_TO_INACCESSIBLE_TERRAIN;
            case 12:
                return CASTLE_NEAR_WATER;
            case 13:
                return MINE_NEAR_WATER;
            case 104:
                return OCCUPIED_BY_TREE;
            case 120:
                return OCCUPIED_BY_INACCESSIBLE_TERRAIN;
            default:
                return null;
        }
    }
}
