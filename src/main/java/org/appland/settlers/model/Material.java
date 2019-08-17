package org.appland.settlers.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Material {

    SWORD,
    SHIELD,
    BEER,
    GOLD,
    IRON,
    COAL,
    WOOD,
    PLANK,
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
    HUNTER,
    OFFICER,
    CORPORAL;

    private final static List<Material> minerals = Arrays.asList(GOLD, IRON, COAL, STONE);
    private final static List<Material> transportableItems = Collections.unmodifiableList(Arrays.asList(
        SWORD,
        SHIELD,
        BEER,
        GOLD,
        IRON,
        COAL,
        WOOD,
        PLANK,
        STONE,
        WHEAT,
        WATER,
        FLOUR,
        BREAD,
        IRON_BAR,
        FISH,
        COIN,
        PIG,
        MEAT
    ));

    static List<Material> getTransportableItems() {
        return transportableItems;
    }

    static Iterable<Material> getMinerals() {
        return minerals;
    }

    public boolean isWorker() {
        switch (this) {
            case DONKEY:
            case PRIVATE:
            case SERGEANT:
            case GENERAL:
            case COURIER:
            case FORESTER:
            case WOODCUTTER_WORKER:
            case STONEMASON:
            case FARMER:
            case SAWMILL_WORKER:
            case WELL_WORKER:
            case MILLER:
            case BAKER:
            case STORAGE_WORKER:
            case FISHERMAN:
            case MINER:
            case IRON_FOUNDER:
            case BREWER:
            case MINTER:
            case ARMORER:
            case PIG_BREEDER:
            case BUTCHER:
            case GEOLOGIST:
            case DONKEY_BREEDER:
            case CATAPULT_WORKER:
            case SCOUT:
            case HUNTER:
            case OFFICER:
            case CORPORAL:
                return true;
        }

        return false;
    }
}
