package org.appland.settlers.model;

import org.appland.settlers.model.actors.Armorer;
import org.appland.settlers.model.actors.Baker;
import org.appland.settlers.model.actors.Brewer;
import org.appland.settlers.model.actors.Builder;
import org.appland.settlers.model.actors.Butcher;
import org.appland.settlers.model.actors.CatapultWorker;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.DonkeyBreeder;
import org.appland.settlers.model.actors.Farmer;
import org.appland.settlers.model.actors.Fisherman;
import org.appland.settlers.model.actors.Forester;
import org.appland.settlers.model.actors.Geologist;
import org.appland.settlers.model.actors.Hunter;
import org.appland.settlers.model.actors.IronFounder;
import org.appland.settlers.model.actors.Metalworker;
import org.appland.settlers.model.actors.Miller;
import org.appland.settlers.model.actors.Miner;
import org.appland.settlers.model.actors.Minter;
import org.appland.settlers.model.actors.PigBreeder;
import org.appland.settlers.model.actors.Carpenter;
import org.appland.settlers.model.actors.Scout;
import org.appland.settlers.model.actors.Shipwright;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.Stonemason;
import org.appland.settlers.model.actors.StorehouseWorker;
import org.appland.settlers.model.actors.WellWorker;
import org.appland.settlers.model.actors.WoodcutterWorker;
import org.appland.settlers.model.actors.Worker;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
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
    CARPENTER,
    WELL_WORKER,
    MILLER,
    BAKER,
    STOREHOUSE_WORKER,
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
    SHIPWRIGHT, BOAT, PLANER, HELPER;

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
    ));

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
            CARPENTER,
            WELL_WORKER,
            MILLER,
            BAKER,
            STOREHOUSE_WORKER,
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

    public static final List<Material> TOOLS = Arrays.asList(
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

    static final List<Material> SOLDIERS = Arrays.asList(
        PRIVATE,
        PRIVATE_FIRST_CLASS,
        SERGEANT,
        OFFICER,
        GENERAL
    );

    static final Set<Material> TOOLS_SET = EnumSet.copyOf(TOOLS);

    private static final List<Material> transportableItems = List.of(SWORD, SHIELD, BEER, GOLD, IRON, COAL, WOOD, PLANK, STONE, WHEAT, WATER, FLOUR, BREAD, IRON_BAR, FISH, COIN, PIG, MEAT);

    static List<Material> getTransportableItems() {
        return transportableItems;
    }

    public static Iterable<Material> getMinerals() {
        return MINERALS;
    }

    public static boolean isTool(Material tool) {
        return TOOLS_SET.contains(tool);
    }

    public static Material workerToMaterial(Worker worker) {
        return switch (worker) {
            case Soldier military -> military.getRank().toMaterial();
            case Forester forester -> FORESTER;
            case WellWorker wellWorker -> WELL_WORKER;
            case WoodcutterWorker woodcutterWorker -> WOODCUTTER_WORKER;
            case StorehouseWorker storehouseWorker -> STOREHOUSE_WORKER;
            case Butcher butcher -> BUTCHER;
            case Carpenter carpenter -> CARPENTER;
            case Stonemason stonemason -> STONEMASON;
            case PigBreeder pigBreeder -> PIG_BREEDER;
            case Minter minter -> MINTER;
            case Miller miller -> MILLER;
            case IronFounder ironFounder -> IRON_FOUNDER;
            case Miner miner -> MINER;
            case Fisherman fisherman -> FISHERMAN;
            case Farmer farmer -> FARMER;
            case Brewer brewer -> BREWER;
            case Baker baker -> BAKER;
            case Armorer armorer -> ARMORER;
            case Geologist geologist -> GEOLOGIST;
            case DonkeyBreeder donkeyBreeder -> DONKEY_BREEDER;
            case Scout scout -> SCOUT;
            case Hunter hunter -> HUNTER;
            case Metalworker metalworker -> METALWORKER;
            case Builder builder -> BUILDER;
            case Shipwright shipwright -> SHIPWRIGHT;
            case Courier courier -> COURIER;
            case CatapultWorker catapultWorker -> CATAPULT_WORKER;
            default -> throw new InvalidGameLogicException(
                    String.format("Can't map worker of type %s to material", worker.getClass().getSimpleName()));
        };
    }

    public boolean isWorker() {
        return WORKERS.contains(this);
    }

    public String getSimpleName() {
        String nameWithSpaces = name().replace("_", " ");

        return nameWithSpaces.toLowerCase();
    }

    public boolean isMilitary() {
        return SOLDIERS.contains(this);
    }

    public Soldier.Rank toRank() {
        return switch (this) {
            case PRIVATE -> Soldier.Rank.PRIVATE_RANK;
            case PRIVATE_FIRST_CLASS -> Soldier.Rank.PRIVATE_FIRST_CLASS_RANK;
            case SERGEANT -> Soldier.Rank.SERGEANT_RANK;
            case OFFICER -> Soldier.Rank.OFFICER_RANK;
            case GENERAL -> Soldier.Rank.GENERAL_RANK;
            default -> throw new InvalidGameLogicException("Can't translate " + this + " to rank");
        };
    }

    public boolean isFood() {
        return this == FISH || this == BREAD || this == MEAT;
    }
}
