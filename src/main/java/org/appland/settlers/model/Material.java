package org.appland.settlers.model;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    PRIVATE_FIRST_CLASS,
    AXE,
    SHOVEL,
    PICK_AXE,
    FISHING_ROD,
    BOW,
    SAW,
    CLEAVER,
    ROLLING_PIN,
    CRUCIBLE,
    TONGS,
    SCYTHE,
    METALWORKER,
    BUILDER,
    HAMMER,
    SHIPWRIGHT, BOAT, PLANER;

    public static final Set<? extends Material> TRANSPORTABLE_GOODS = EnumSet.copyOf(Arrays.asList(
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
            AXE,
            SHOVEL,
            PICK_AXE,
            FISHING_ROD,
            BOW,
            SAW,
            CLEAVER,
            ROLLING_PIN,
            CRUCIBLE,
            TONGS,
            SCYTHE
    )
    );

    public static final Set<? extends Material> WORKERS = EnumSet.copyOf(Arrays.asList(
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
            PRIVATE_FIRST_CLASS,
            METALWORKER,
            BUILDER,
            SHIPWRIGHT
    ));

    private static final List<Material> MINERALS = Arrays.asList(GOLD, IRON, COAL, STONE);

    static final List<Material> TOOLS = Arrays.asList(
            AXE,
            SHOVEL,
            PICK_AXE,
            FISHING_ROD,
            BOW,
            SAW,
            CLEAVER,
            ROLLING_PIN,
            CRUCIBLE,
            TONGS,
            SCYTHE);

    static final Set<Material> TOOLS_SET = EnumSet.copyOf(TOOLS);

    private static final List<Material> transportableItems = List.of(SWORD, SHIELD, BEER, GOLD, IRON, COAL, WOOD, PLANK, STONE, WHEAT, WATER, FLOUR, BREAD, IRON_BAR, FISH, COIN, PIG, MEAT);

    static List<Material> getTransportableItems() {
        return transportableItems;
    }

    static Iterable<Material> getMinerals() {
        return MINERALS;
    }

    public static boolean isTool(Material tool) {
        return TOOLS_SET.contains(tool);
    }

    public static Material workerToMaterial(Worker worker) {

        if (Objects.equals(worker.getClass(), Builder.class)) {
            return BUILDER;
        }

        // TODO: handle remaining types of workers

        return null;
    }

    public boolean isWorker() {
        return WORKERS.contains(this);
    }

    public String getSimpleName() {
        String nameWithSpaces = name().replace("_", " ");

        return nameWithSpaces.toLowerCase();
    }
}
