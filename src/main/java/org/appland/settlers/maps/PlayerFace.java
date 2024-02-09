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
public enum PlayerFace {
    ROMAN_OCTAVIANUS(0),
    ROMAN_JULIUS(1),
    BRUTUS(2),
    VIKING_ERIK(3),
    VIKING_KNUT(4),
    VIKING_OLOF(5),
    JAPANESE_YAMAUCHI(6),
    JAPANESE_TSUNAMI(7),
    JAPANESE_HAKIRAWASHI(8),
    NUBIAN_SHAKA(9),
    NUBIAN_TODO(10),
    NUBIAN_MNGA_TSCHA(11);

    private final int id;

    PlayerFace(int id) {
        this.id = id;
    }

    static PlayerFace playerFaceFromShort(short s) {
        return switch (s) {
            case 0 -> PlayerFace.ROMAN_OCTAVIANUS;
            case 1 -> PlayerFace.ROMAN_JULIUS;
            case 2 -> PlayerFace.BRUTUS;
            case 3 -> PlayerFace.VIKING_ERIK;
            case 4 -> PlayerFace.VIKING_KNUT;
            case 5 -> PlayerFace.VIKING_OLOF;
            case 6 -> PlayerFace.JAPANESE_YAMAUCHI;
            case 7 -> PlayerFace.JAPANESE_TSUNAMI;
            case 8 -> PlayerFace.JAPANESE_HAKIRAWASHI;
            case 9 -> PlayerFace.NUBIAN_SHAKA;
            case 10 -> PlayerFace.NUBIAN_TODO;
            case 11 -> PlayerFace.NUBIAN_MNGA_TSCHA;
            default -> null;
        };
    }
}
