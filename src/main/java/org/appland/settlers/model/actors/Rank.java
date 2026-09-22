package org.appland.settlers.model.actors;

import org.appland.settlers.model.InvalidGameLogicException;
import org.appland.settlers.model.Material;

import static org.appland.settlers.model.Material.*;

public enum Rank {
    PRIVATE_RANK,
    SERGEANT_RANK,
    OFFICER_RANK,
    PRIVATE_FIRST_CLASS_RANK,
    GENERAL_RANK;

    public static Rank intToRank(int soldierInt) {
        return switch (soldierInt) {
            case 0, 1 -> PRIVATE_RANK;
            case 2, 3 -> PRIVATE_FIRST_CLASS_RANK;
            case 4, 5, 6 -> SERGEANT_RANK;
            case 7, 8 -> OFFICER_RANK;
            case 9, 10 -> GENERAL_RANK;
            default -> throw new InvalidGameLogicException(String.format("Can't translate %d to rank", soldierInt));
        };
    }

    public String getSimpleName() {
        return switch (this) {
            case PRIVATE_RANK -> "Private";
            case PRIVATE_FIRST_CLASS_RANK -> "Private first class";
            case SERGEANT_RANK -> "Sergeant";
            case OFFICER_RANK -> "Officer";
            case GENERAL_RANK -> "General";
        };
    }

    public Material toMaterial() {
        return switch (this) {
            case PRIVATE_RANK -> PRIVATE;
            case PRIVATE_FIRST_CLASS_RANK -> PRIVATE_FIRST_CLASS;
            case SERGEANT_RANK -> SERGEANT;
            case OFFICER_RANK -> OFFICER;
            case GENERAL_RANK -> GENERAL;
        };
    }

    public int toInt() {
        return switch (this) {
            case PRIVATE_RANK -> 0;
            case PRIVATE_FIRST_CLASS_RANK -> 1;
            case SERGEANT_RANK -> 2;
            case OFFICER_RANK -> 3;
            case GENERAL_RANK -> 4;
        };
    }

    public int getMaxHealth() {
        return switch (this) {
            case PRIVATE_RANK -> 3;
            case PRIVATE_FIRST_CLASS_RANK -> 4;
            case SERGEANT_RANK -> 5;
            case OFFICER_RANK -> 6;
            case GENERAL_RANK -> 7;
        };
    }

    int getMaxCombatRoll() {
        return switch (this) {
            case PRIVATE_RANK -> 3;
            case PRIVATE_FIRST_CLASS_RANK -> 4;
            case SERGEANT_RANK -> 5;
            case OFFICER_RANK -> 6;
            case GENERAL_RANK -> 7;
        };
    }
}
