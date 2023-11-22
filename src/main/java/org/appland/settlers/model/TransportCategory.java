package org.appland.settlers.model;

import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.CLEAVER;
import static org.appland.settlers.model.Material.CRUCIBLE;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.FISHING_ROD;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.ROLLING_PIN;
import static org.appland.settlers.model.Material.SAW;
import static org.appland.settlers.model.Material.SCYTHE;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.TONGS;

public enum TransportCategory {
    FOOD,
    WEAPONS,
    COAL,
    GOLD,
    IRON,
    IRON_BAR,
    COIN,
    WHEAT,
    WATER,
    BEER,
    PLANK,
    WOOD,
    STONE,
    FLOUR,
    PIG,
    TOOLS,
    BOAT;

    public Material[] getMaterials() {

        if (this == FOOD) {
            return new Material[]{BREAD, MEAT, FISH};
        } else if (this == WEAPONS) {
            return new Material[]{SWORD, SHIELD};
        } else if (this == COAL) {
            return new Material[]{Material.COAL};
        } else if (this == GOLD) {
            return new Material[]{Material.GOLD};
        } else if (this == IRON) {
            return new Material[]{Material.IRON};
        } else if (this == IRON_BAR) {
            return new Material[]{Material.IRON_BAR};
        } else if (this == COIN) {
            return new Material[]{Material.COIN};
        } else if (this == WHEAT) {
            return new Material[]{Material.WHEAT};
        } else if (this == WATER) {
            return new Material[]{Material.WATER};
        } else if (this == BEER) {
            return new Material[]{Material.BEER};
        } else if (this == PLANK) {
            return new Material[]{Material.PLANK};
        } else if (this == WOOD) {
            return new Material[]{Material.WOOD};
        } else if (this == STONE) {
            return new Material[]{Material.STONE};
        } else if (this == FLOUR) {
            return new Material[]{Material.FLOUR};
        } else if (this == PIG) {
            return new Material[]{Material.PIG};
        } else if (this == TOOLS) {
            return new Material[]{Material.AXE, Material.SHOVEL, Material.PICK_AXE, FISHING_ROD, Material.BOW, SAW, CLEAVER, ROLLING_PIN, CRUCIBLE, TONGS, SCYTHE};
        } else if (this == BOAT) {
            return new Material[]{Material.BOAT};
        }

        return null;
    }
}
