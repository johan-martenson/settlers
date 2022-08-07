package org.appland.settlers.assets.gamefiles;

import org.appland.settlers.model.Material;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.appland.settlers.model.Material.AXE;
import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.BOAT;
import static org.appland.settlers.model.Material.BOW;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.CLEAVER;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.CRUCIBLE;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.FISHING_ROD;
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.HAMMER;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.IRON_BAR;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.PICK_AXE;
import static org.appland.settlers.model.Material.PIG;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.ROLLING_PIN;
import static org.appland.settlers.model.Material.SAW;
import static org.appland.settlers.model.Material.SCYTHE;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.SHOVEL;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.TONGS;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Material.WOOD;

public class CarrierBob {
    public static final String FILENAME = "DATA/BOBS/CARRIER.BOB";
    public static final Map<Integer, Material> CARGO_BOB_ID_TO_MATERIAL_MAP = new HashMap<>();

    static {
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(0, BEER);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(1, TONGS);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(2, HAMMER);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(3, AXE);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(4, SAW);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(5, PICK_AXE);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(6, SHOVEL);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(7, CRUCIBLE);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(8, FISHING_ROD);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(9, SCYTHE);

        // TODO: add empty water bucket

        CARGO_BOB_ID_TO_MATERIAL_MAP.put(11, WATER);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(12, CLEAVER);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(13, ROLLING_PIN);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(14, BOW);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(15, BOAT);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(16, SWORD);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(17, IRON_BAR);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(18, FLOUR);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(19, FISH);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(20, BREAD);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(21, SHIELD);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(22, WOOD);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(23, PLANK);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(24, STONE);

        // Viking shield, africans shield

        CARGO_BOB_ID_TO_MATERIAL_MAP.put(27, WHEAT);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(28, COIN);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(29, GOLD);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(30, IRON);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(31, COAL);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(32, MEAT);
        CARGO_BOB_ID_TO_MATERIAL_MAP.put(33, PIG);

        // Japanese shield
    }

    public static final Map<Material, Integer> MATERIAL_BOB_ID_MAP = new EnumMap<>(Material.class);

    static {
        MATERIAL_BOB_ID_MAP.put(BEER, 0);
        MATERIAL_BOB_ID_MAP.put(TONGS, 1);
        MATERIAL_BOB_ID_MAP.put(HAMMER, 2);
        MATERIAL_BOB_ID_MAP.put(AXE, 3);
        MATERIAL_BOB_ID_MAP.put(SAW, 4);
        MATERIAL_BOB_ID_MAP.put(PICK_AXE, 5);
        MATERIAL_BOB_ID_MAP.put(SHOVEL, 6);
        MATERIAL_BOB_ID_MAP.put(CRUCIBLE, 7);
        MATERIAL_BOB_ID_MAP.put(FISHING_ROD, 8);
        MATERIAL_BOB_ID_MAP.put(SCYTHE, 9);

        // TODO: add empty water bucket

        MATERIAL_BOB_ID_MAP.put(WATER, 11);
        MATERIAL_BOB_ID_MAP.put(CLEAVER, 12);
        MATERIAL_BOB_ID_MAP.put(ROLLING_PIN, 13);
        MATERIAL_BOB_ID_MAP.put(BOW, 14);
        MATERIAL_BOB_ID_MAP.put(BOAT, 15);
        MATERIAL_BOB_ID_MAP.put(SWORD, 16);
        MATERIAL_BOB_ID_MAP.put(IRON_BAR, 17);
        MATERIAL_BOB_ID_MAP.put(FLOUR, 18);
        MATERIAL_BOB_ID_MAP.put(FISH, 19);
        MATERIAL_BOB_ID_MAP.put(BREAD, 20);
        MATERIAL_BOB_ID_MAP.put(SHIELD, 21);
        MATERIAL_BOB_ID_MAP.put(WOOD, 22);
        MATERIAL_BOB_ID_MAP.put(PLANK, 23);
        MATERIAL_BOB_ID_MAP.put(STONE, 24);

        // Viking shield, africans shield

        MATERIAL_BOB_ID_MAP.put(WHEAT, 27);
        MATERIAL_BOB_ID_MAP.put(COIN, 28);
        MATERIAL_BOB_ID_MAP.put(GOLD, 29);
        MATERIAL_BOB_ID_MAP.put(IRON, 30);
        MATERIAL_BOB_ID_MAP.put(COAL, 31);
        MATERIAL_BOB_ID_MAP.put(MEAT, 32);
        MATERIAL_BOB_ID_MAP.put(PIG, 33);

        // Japanese shield
    }
}
