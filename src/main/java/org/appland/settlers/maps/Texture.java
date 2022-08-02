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
public enum Texture {                 // Build road   Build    Crops     Mine     Ship sail
    SAVANNAH(0),                   //    X           X         X        -           -
    MOUNTAIN_1(1),                 //    X           -         -        X           -
    SNOW(2),                       //    -           -         -        -           -
    SWAMP(3),                      //    -           -         -        -           -
    DESERT_1(4),                   //    X           -         -        -           -
    WATER(5),                      //    -           -         -        -           X
    BUILDABLE_WATER(6),            //    X           X         -        -           -
    DESERT_2(7),                   //    X           -         -        -           -
    MEADOW_1(8),                   //    X           X         X        -           -
    MEADOW_2(9),                   //    X           X         X        -           -
    MEADOW_3(10),                  //    X           X         X        -           -
    MOUNTAIN_2(11),                //    X           -         -        X           -
    MOUNTAIN_3(12),                //    X           -         -        X           -
    MOUNTAIN_4(13),                //    X           -         -        X           -
    STEPPE(14),                    //    X           X         X        -           -
    FLOWER_MEADOW(15),             //    X           X         X        -           -
    LAVA(16),                      //    -           -         -        -           -
    MAGENTA(17),                   //    X           -         -        -           -
    MOUNTAIN_MEADOW(18),           //    X           X         -        -           -
    WATER_2(19),                   //    -           -         -        -           -
    LAVA_2(20),                    //    -           -         -        -           -
    LAVA_3(21),                    //    -           -         -        -           -
    LAVA_4(22),                    //    -           -         -        -           -
    BUILDABLE_MOUNTAIN(23);        //    X           X         -        -           -

    private final int id;

    Texture(int id) {
        this.id = id;
    }

    public static Texture textureFromUint8(int textureUint8) {

        // Should only consider the lower six bits, i.e. & 0x3F. 0x40 marks harbour, and 0x80 is unknown

        int i = textureUint8 & 0x3F;

        switch (i) {
            case 0:
                return SAVANNAH;
            case 1:
                return MOUNTAIN_1;
            case 2:
                return SNOW;
            case 3:
                return SWAMP;
            case 4:
                return DESERT_1;
            case 5:
                return WATER;
            case 6:
                return BUILDABLE_WATER;
            case 7:
                return DESERT_2;
            case 8:
                return MEADOW_1;
            case 9:
                return MEADOW_2;
            case 10:
                return MEADOW_3;
            case 11:
                return MOUNTAIN_2;
            case 12:
                return MOUNTAIN_3;
            case 13:
                return MOUNTAIN_4;
            case 14:
                return STEPPE;
            case 15:
                return FLOWER_MEADOW;
            case 16:
                return LAVA;
            case 17:
                return MAGENTA;
            case 18:
                return MOUNTAIN_MEADOW;
            case 19:
                return WATER_2;
            case 20:
                return LAVA_2;
            case 21:
                return LAVA_3;
            case 22:
                return LAVA_4;
            case 23:
                return BUILDABLE_MOUNTAIN;
            default:
                return null;
        }
    }

    public static boolean isWater(Texture texture) {
        return texture == WATER || texture == WATER_2 || texture == BUILDABLE_WATER;
    }
}
