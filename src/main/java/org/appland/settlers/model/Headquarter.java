package org.appland.settlers.model;

import java.util.Map;

import org.appland.settlers.policy.InitialState;

import static org.appland.settlers.model.Material.*;

@HouseSize(size=Size.LARGE)
public class Headquarter extends Storage {

    private Headquarter() {
	super();
	
	setHeadquarterDefaultInventory(inventory);
    }
    
    public static Storage createHeadquarter() {
	return new Headquarter();
    }

    private void setHeadquarterDefaultInventory(Map<Material, Integer> inventory) {
	inventory.put(SHIELD, InitialState.STORAGE_INITIAL_SHIELDS);
	inventory.put(SWORD, InitialState.STORAGE_INIITAL_SWORDS);
	inventory.put(BEER, InitialState.STORAGE_INITIAL_BEER);
	inventory.put(GOLD, InitialState.STORAGE_INITIAL_GOLD);
	inventory.put(PRIVATE, InitialState.STORAGE_INITIAL_PRIVATE);
	inventory.put(SERGEANT, InitialState.STORAGE_INITIAL_SERGEANT);
	inventory.put(GENERAL, InitialState.STORAGE_INITIAL_GENERAL);
    }

	public String toString() {
		return "Headquarter with inventory " + this.inventory; 
	}
}
