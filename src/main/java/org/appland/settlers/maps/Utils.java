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
public class Utils {

    static String getHex(byte[] blockHeader1) {
        var hex = new StringBuilder();

        for (byte b : blockHeader1) {
            hex.append(String.format("%02X", b & 0xff));
        }

        return hex.toString();
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
        var intHexString = Integer.toHexString(s);

        return intHexString.substring(4);
    }
}
