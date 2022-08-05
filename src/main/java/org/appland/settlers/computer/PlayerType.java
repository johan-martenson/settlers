/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.computer;

/**
 *
 * @author johan
 */
public enum PlayerType {
    BUILDING,
    EXPANDING,
    ATTACKING,
    MINERALS,
    FOOD_PRODUCER,
    COIN_PRODUCER,
    MILITARY_PRODUCER,
    COMPOSITE_PLAYER;

    public static PlayerType playerTypeFromString(String computerPlayerName) {
        switch (computerPlayerName) {
            case "building":
                return BUILDING;
            case "expansion":
                return EXPANDING;
            case "attacking":
                return ATTACKING;
            case "minerals":
                return MINERALS;
            case "food":
                return FOOD_PRODUCER;
            case "coins":
                return COIN_PRODUCER;
            case "military":
                return MILITARY_PRODUCER;
            case "composite":
                return COMPOSITE_PLAYER;
            default:
                return null;
        }
    }
}
