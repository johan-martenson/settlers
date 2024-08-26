/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.maps;

import org.appland.settlers.model.Vegetation;
import org.appland.settlers.model.Material;

/**
 *
 * @author johan
 */
class Utils {

    static String getHex(byte[] blockHeader1) {
        StringBuilder hex = new StringBuilder();

        for (byte b : blockHeader1) {
            hex.append(String.format("%02X", b & 0xff));
        }

        return hex.toString();
    }

    static Vegetation convertTextureToVegetation(Texture texture) {
        switch (texture) {
            case SAVANNAH:             return Vegetation.SAVANNAH;           // Savannah - can build houses
            case MOUNTAIN_1:           return Vegetation.MOUNTAIN_1;         // Mountain 1 - mining
            case SNOW:                 return Vegetation.SNOW;               // Snow - can't walk on the snow
            case SWAMP:                return Vegetation.SWAMP;              // Swamp - can't walk on swamp?
            case DESERT_1:             return Vegetation.DESERT_1;           // Desert 1 - flags
            case WATER:                return Vegetation.WATER;              // Water - no walking, sailing
            case BUILDABLE_WATER:      return Vegetation.BUILDABLE_WATER;    // Buildable water - can build houses
            case DESERT_2:             return Vegetation.DESERT_2;           // Desert 2 - flags
            case MEADOW_1:             return Vegetation.MEADOW_1;           // Meadow 1 - can build houses
            case MEADOW_2:             return Vegetation.MEADOW_2;           // Meadow 2 - can build houses
            case MEADOW_3:             return Vegetation.MEADOW_3;           // Meadow 3 - can build houses
            case MOUNTAIN_2:           return Vegetation.MOUNTAIN_2;         // Mountain 2 - mining
            case MOUNTAIN_3:           return Vegetation.MOUNTAIN_3;         // Mountain 3 - mining
            case MOUNTAIN_4:           return Vegetation.MOUNTAIN_4;         // Mountain 4 - mining
            case STEPPE:               return Vegetation.STEPPE;             // Steppe - can build houses
            case FLOWER_MEADOW:        return Vegetation.FLOWER_MEADOW;      // Flower meadow - can build houses
            case LAVA:                 return Vegetation.LAVA_1;               // Lava - no walking
            case MAGENTA:              return Vegetation.MAGENTA;            // Magenta - build flags
            case MOUNTAIN_MEADOW:      return Vegetation.MOUNTAIN_MEADOW;    // Mountain meadow - can build houses
            case WATER_2:              return Vegetation.WATER_2;            // Water - no walking, no building, no sailing
            case LAVA_2:               return Vegetation.LAVA_2;             // Lava 2 - no walking, building
            case LAVA_3:               return Vegetation.LAVA_3;             // Lava 3 - no walking, building
            case LAVA_4:               return Vegetation.LAVA_4;             // Lava 4 - no walking, building
            case BUILDABLE_MOUNTAIN:   return Vegetation.BUILDABLE_MOUNTAIN; // Buildable mountain can build houses, walking, no mining

            default:
                System.out.println("Can't handle texture " + texture);
                System.exit(1);

                return null;
        }
    }

    static Material resourceTypeToMaterial(ResourceType mineralType) {
        return switch (mineralType) {
            case COAL -> Material.COAL;
            case IRON_ORE -> Material.IRON;
            case GOLD -> Material.GOLD;
            case GRANITE -> Material.STONE;
            default -> null;
        };
    }

    public static boolean isEven(int number) {
        return number % 2 == 0;
    }

    public static String shortAsHex(short s) {
        String intHexString = Integer.toHexString(s);

        return intHexString.substring(4);
    }
}
