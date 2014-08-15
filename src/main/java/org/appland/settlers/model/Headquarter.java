package org.appland.settlers.model;

import java.util.Map;
import static org.appland.settlers.model.Building.ConstructionState.DONE;

import static org.appland.settlers.model.Material.*;
import org.appland.settlers.policy.InitialState;

@HouseSize(size = Size.LARGE)
@MilitaryBuilding(maxHostedMilitary = 0, defenceRadius = 20)
public class Headquarter extends Storage {

    public Headquarter() {
        super();

        setHeadquarterDefaultInventory(inventory);

        constructionState = DONE;
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

        inventory.put(FORESTER, InitialState.STORAGE_INITIAL_FORESTER);
        inventory.put(WOODCUTTER_WORKER, InitialState.STORAGE_INITIAL_WOODCUTTER_WORKER);
        inventory.put(STONEMASON, InitialState.STORAGE_INITIAL_STONEMASON);
        inventory.put(FARMER, InitialState.STORAGE_INITIAL_FARMER);
        inventory.put(SAWMILL_WORKER, InitialState.STORAGE_INITIAL_SAWMILL_WORKER);
        inventory.put(WELL_WORKER, InitialState.STORAGE_INITIAL_WELL_WORKER);
    }

    @Override
    public String toString() {
        return "Headquarter with inventory " + this.inventory;
    }
}
