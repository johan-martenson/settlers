package org.appland.settlers.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public enum Material {

    SWORD,
    SHIELD,
    BEER,
    GOLD,
    IRON,
    COAL,
    WOOD,
    PLANCK,
    STONE,
    WHEAT,
    WATER,
    FLOUR,
    BREAD,
    IRON_BAR,
    FISH,
    COIN,
    PIG,
    MEAT,
    DONKEY,

    PRIVATE,
    SERGEANT,
    GENERAL,
    COURIER,
    FORESTER,
    WOODCUTTER_WORKER,
    STONEMASON,
    FARMER,
    SAWMILL_WORKER,
    WELL_WORKER,
    MILLER,
    BAKER,
    STORAGE_WORKER,
    FISHERMAN,
    MINER,
    IRON_FOUNDER,
    BREWER,
    MINTER,
    ARMORER,
    PIG_BREEDER,
    BUTCHER,
    GEOLOGIST,
    DONKEY_BREEDER,
    CATAPULT_WORKER,
    SCOUT,
    HUNTER;

    private final static List<Material> minerals = Arrays.asList(new Material[] {GOLD, IRON, COAL, STONE});

    static Iterable<Material> getMinerals() {
        return minerals;
    }
}
