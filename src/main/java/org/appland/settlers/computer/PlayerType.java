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
        return switch (computerPlayerName) {
            case "building" -> BUILDING;
            case "expansion" -> EXPANDING;
            case "attacking" -> ATTACKING;
            case "minerals" -> MINERALS;
            case "food" -> FOOD_PRODUCER;
            case "coins" -> COIN_PRODUCER;
            case "military" -> MILITARY_PRODUCER;
            case "composite" -> COMPOSITE_PLAYER;
            default -> null;
        };
    }
}
