package org.appland.settlers.model;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.Building.ConstructionState.DONE;

import static org.appland.settlers.model.Material.*;
import org.appland.settlers.policy.InitialState;

@HouseSize(size = Size.LARGE)
@MilitaryBuilding(maxHostedMilitary = 0, defenceRadius = 20)
public class Headquarter extends Storage {

    public Headquarter() {
        super();

        try {
            setHeadquarterDefaultInventory(inventory);
            constructionState = DONE;
        } catch (Exception ex) {
            Logger.getLogger(Headquarter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void setMap(GameMap m) {
        super.setMap(m);
            
        try {
            Worker w = new StorageWorker(m);
            map.placeWorker(w, this);
            
            w.enterBuilding(this);

            assignWorker(w);
        } catch (Exception ex) {
            Logger.getLogger(Headquarter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setHeadquarterDefaultInventory(Map<Material, Integer> inventory) {
        inventory.put(SHIELD, InitialState.STORAGE_INITIAL_SHIELDS);
        inventory.put(SWORD, InitialState.STORAGE_INIITAL_SWORDS);
        inventory.put(BEER, InitialState.STORAGE_INITIAL_BEER);
        inventory.put(GOLD, InitialState.STORAGE_INITIAL_GOLD);

        inventory.put(PRIVATE, InitialState.STORAGE_INITIAL_PRIVATE);
        inventory.put(SERGEANT, InitialState.STORAGE_INITIAL_SERGEANT);
        inventory.put(GENERAL, InitialState.STORAGE_INITIAL_GENERAL);

        inventory.put(WOOD, InitialState.STORAGE_INITIAL_WOOD);
        inventory.put(PLANCK, InitialState.STORAGE_INITIAL_PLANCKS);
        inventory.put(STONE, InitialState.STORAGE_INITIAL_STONES);
        inventory.put(FISH, InitialState.STORAGE_INITIAL_FISH);

        inventory.put(FORESTER, InitialState.STORAGE_INITIAL_FORESTER);
        inventory.put(WOODCUTTER_WORKER, InitialState.STORAGE_INITIAL_WOODCUTTER_WORKER);
        inventory.put(STONEMASON, InitialState.STORAGE_INITIAL_STONEMASON);
        inventory.put(FARMER, InitialState.STORAGE_INITIAL_FARMER);
        inventory.put(SAWMILL_WORKER, InitialState.STORAGE_INITIAL_SAWMILL_WORKER);
        inventory.put(WELL_WORKER, InitialState.STORAGE_INITIAL_WELL_WORKER);
        inventory.put(MILLER, InitialState.STORAGE_INITIAL_MILLER);
        inventory.put(BAKER, InitialState.STORAGE_INITIAL_BAKER);
        inventory.put(STORAGE_WORKER, InitialState.STORAGE_INITIAL_STORAGE_WORKER);
        inventory.put(FISHERMAN, InitialState.STORAGE_INITIAL_FISHERMAN);
        inventory.put(MINER, InitialState.STORAGE_INITIAL_MINER);
        inventory.put(IRON_FOUNDER, InitialState.STORAGE_INITIAL_IRON_FOUNDER);
        inventory.put(BREWER, InitialState.STORAGE_INITIAL_BREWER);
        inventory.put(MINTER, InitialState.STORAGE_INITIAL_MINTER);
    }

    @Override
    public String toString() {
        return "Headquarter with inventory " + this.inventory;
    }
}
