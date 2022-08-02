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
        switch(s) {
            case 0:
                return PlayerFace.ROMAN_OCTAVIANUS;
            case 1:
                return PlayerFace.ROMAN_JULIUS;
            case 2:
                return PlayerFace.BRUTUS;
            case 3:
                return PlayerFace.VIKING_ERIK;
            case 4:
                return PlayerFace.VIKING_KNUT;
            case 5:
                return PlayerFace.VIKING_OLOF;
            case 6:
                return PlayerFace.JAPANESE_YAMAUCHI;
            case 7:
                return PlayerFace.JAPANESE_TSUNAMI;
            case 8:
                return PlayerFace.JAPANESE_HAKIRAWASHI;
            case 9:
                return PlayerFace.NUBIAN_SHAKA;
            case 10:
                return PlayerFace.NUBIAN_TODO;
            case 11:
                return PlayerFace.NUBIAN_MNGA_TSCHA;
            default:
                return null;
        }
    }
}
