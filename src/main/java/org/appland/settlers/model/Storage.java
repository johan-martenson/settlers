package org.appland.settlers.model;

import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.SWORD;

import static org.appland.settlers.model.Size.MEDIUM;

import static org.appland.settlers.model.Utils.createEmptyMaterialIntMap;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.appland.settlers.policy.ProductionDelays;

@HouseSize(size=MEDIUM)
public class Storage extends Building implements Actor {
	
    protected Map<Material, Integer> inventory;
	private int promotionCountdown;
	private int draftCountdown;
	
	private Logger log = Logger.getLogger(Storage.class.getName());
	
	protected Storage() {
	    inventory = createEmptyMaterialIntMap();
		
	    promotionCountdown = -1;
	    draftCountdown = -1;
	}

	public Map<Material, Integer> getInventory() {
		return inventory;
	}

	/* This method updates the inventory as a side effect, without any locking */
	private void draftMilitary() {
		int swords = inventory.get(SWORD);
		int shields = inventory.get(SHIELD);
		int beer = inventory.get(BEER);
		
		int privatesToAdd = Math.min(swords, shields);
		
		privatesToAdd = Math.min(privatesToAdd, beer);
		
		int existingPirates = inventory.get(PRIVATE);
		
		inventory.put(PRIVATE, existingPirates + privatesToAdd);
		inventory.put(BEER, beer - privatesToAdd);
		inventory.put(SHIELD, shields - privatesToAdd);
		inventory.put(SWORD, swords - privatesToAdd);
	}

    @Override
	public void stepTime() {
		
		/* Handle promotion with delay */
		if (isPromotionPossible(inventory)) {
			if (promotionCountdown == 0) {
				doPromoteMilitary();
				promotionCountdown = ProductionDelays.PROMOTION_DELAY;
			} else if (promotionCountdown == -1){
				promotionCountdown = ProductionDelays.PROMOTION_DELAY;
			} else {
				promotionCountdown--;
			}
		} else {
			promotionCountdown = -1;
		}
		
		/* Handle draft with delay */
		if (isDraftPossible(inventory)) {
			if (draftCountdown == 0) {
				draftMilitary();
				draftCountdown = ProductionDelays.DRAFT_DELAY;
			} else if (draftCountdown == -1) {
				draftCountdown = ProductionDelays.DRAFT_DELAY;
			} else {
				draftCountdown--;
			}
		}
	}

	/* TODO: Write unit tests */
	public boolean isDraftPossible(Map<Material, Integer> inventory) {
            return inventory.get(BEER) > 0 && 
                    inventory.get(SWORD) > 0 &&
                    inventory.get(SHIELD) > 0;
	}
	
	/* TODO: Write unit tests */
	public boolean isPromotionPossible(Map<Material, Integer> inventory) {
            return inventory.get(GOLD) > 0 &&
                    (inventory.get(PRIVATE) > 0 ||
                    inventory.get(SERGEANT) > 0);
	}
	
	private void doPromoteMilitary() {	
		int gold = inventory.get(GOLD);
		int privates = inventory.get(PRIVATE);
		int sergeants = inventory.get(SERGEANT);
		int generals = inventory.get(GENERAL);
		
		if (gold > 0 && privates > 0) {
			sergeants++;
			privates--;
			gold--;
		}
		
		if (gold > 0 && sergeants > 1) {
			generals++;
			sergeants--;
			gold--;
		}
		
		inventory.put(PRIVATE, privates);
		inventory.put(SERGEANT, sergeants);
		inventory.put(GENERAL, generals);
		inventory.put(GOLD, gold);
	}

	public void deposit(Cargo c) {
		log.log(Level.INFO, "Depositing cargo {0}", c);
		
		int amount = inventory.get(c.getMaterial());
		
		inventory.put(c.getMaterial(), amount + 1);
		
		log.log(Level.FINE, "Inventory is {0} after deposit", inventory);
	}

	public Cargo retrieve(Material wood) {
		log.log(Level.INFO, "Retrieving one piece of {0}", wood);
		
		int amount = inventory.get(wood);
		inventory.put(wood, amount - 1);
		
		Cargo c = Cargo.createCargo(wood);
		
		c.setPosition(getFlag().getPosition());
		
		return c;
	}

	public static Storage createStorage() {
		return new Storage();
	}
}
