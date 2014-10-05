package org.appland.settlers.model;

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
    GEOLOGIST;

    private static List<Material> minerals = null;
    
    static Iterable<Material> getMinerals() {
        if (minerals == null) {
            minerals = new LinkedList<>();
            
            minerals.add(GOLD);
            minerals.add(IRON);
            minerals.add(COAL);
            minerals.add(STONE);
        }
        
        return minerals;
    }
}
