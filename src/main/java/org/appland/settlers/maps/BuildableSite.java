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
        return switch (i) {
            case 1 -> FLAG;
            case 2 -> HUT;
            case 3 -> HOUSE;
            case 4 -> CASTLE;
            case 5 -> MINE;
            case 9 -> FLAG_NEXT_TO_INACCESSIBLE_TERRAIN;
            case 12 -> CASTLE_NEAR_WATER;
            case 13 -> MINE_NEAR_WATER;
            case 104 -> OCCUPIED_BY_TREE;
            case 120 -> OCCUPIED_BY_INACCESSIBLE_TERRAIN;
            default -> null;
        };
    }
}
