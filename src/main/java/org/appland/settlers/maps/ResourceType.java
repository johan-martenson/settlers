/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.maps;

import org.appland.settlers.model.Material;

/**
 *
 * @author johan
 */
public enum ResourceType {
    WATER,
    FISH,
    COAL,
    IRON_ORE,
    GOLD,
    GRANITE;

    public static ResourceType resourceTypeFromInt(int type) {
        if (type == 33) { // 0x20, 0x21 -- 32-33
            return WATER;
        } else if (type == 135) { // > 0x80, < 0x90 -- 128-144
            return FISH;
        } else if (type >= 64 && type <= 71) { // > 0x40, < 0x48 -- 64-72
            return COAL;
        } else if (type >= 72 && type <= 79) { // > 0x48, < 0x50 -- 72-80
            return IRON_ORE;
        } else if (type >= 80 && type <= 87) { // > 0x50, < 0x58 -- 80-88
            return GOLD;
        } else if (type >= 88 && type <= 95) { // > 0x58, < 0x60 -- 88-96
            return GRANITE;
        } else {
            return null;
        }
    }

    Material getMineralType() {
        if (this == COAL) {
            return Material.COAL;
        } else if (this == IRON_ORE) {
            return Material.IRON;
        } else if (this == GOLD) {
            return Material.GOLD;
        } else if (this == GRANITE) {
            return Material.STONE;
        }

        return null;
    }
}
