package org.appland.settlers.test;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.appland.settlers.model.InvalidNumberOfPlayersException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Storage;
import org.junit.Before;
import org.junit.Test;

public class MilitaryCreationAndPromotionTest {
	
	Storage storage;
	Map<Material, Integer> inventory;
	
	@Before
	public void setupTest() throws InvalidNumberOfPlayersException {
		storage = new Storage();
		inventory = storage.getInventory();
	}
	
	@Test
	public void createPrivate() {
		int numberOfPrivates = inventory.get(Material.PRIVATE);
		
		inventory.put(Material.BEER, 1);
		inventory.put(Material.SWORD, 2);
		inventory.put(Material.SHIELD, 3);
		
		assertTrue(inventory.get(Material.PRIVATE) == numberOfPrivates);
		assertTrue(inventory.get(Material.BEER) == 1);
		assertTrue(inventory.get(Material.SWORD) == 2);
		assertTrue(inventory.get(Material.SHIELD) == 3);

		Utils.fastForward(110, storage);
		
		assertTrue(inventory.get(Material.PRIVATE) == numberOfPrivates + 1);
		assertTrue(inventory.get(Material.BEER) == 0);
		assertTrue(inventory.get(Material.SWORD) == 1);
		assertTrue(inventory.get(Material.SHIELD) == 2);
	}
	
	@Test
	public void promoteSinglePrivate() {
		inventory.put(Material.GOLD, 1);
		inventory.put(Material.PRIVATE, 1);
		inventory.put(Material.SERGEANT, 0);
		inventory.put(Material.GENERAL, 0);
		
		assertTrue(1 == inventory.get(Material.GOLD));
		assertTrue(1 == inventory.get(Material.PRIVATE));
		assertTrue(0 == inventory.get(Material.SERGEANT));
		assertTrue(0 == inventory.get(Material.GENERAL));
		
		Utils.fastForward(110, storage);
		
		assertTrue(0 == inventory.get(Material.GOLD));
		assertTrue(0 == inventory.get(Material.PRIVATE));
		assertTrue(1 == inventory.get(Material.SERGEANT));
		assertTrue(0 == inventory.get(Material.GENERAL));
	}
	
	@Test
	public void promoteGroupOfPrivates() {
		inventory.put(Material.GOLD, 10);
		inventory.put(Material.PRIVATE, 5);
		inventory.put(Material.SERGEANT, 0);
		inventory.put(Material.GENERAL, 0);
		
		assertTrue(10 == inventory.get(Material.GOLD));
		assertTrue(5 == inventory.get(Material.PRIVATE));
		assertTrue(0 == inventory.get(Material.SERGEANT));
		assertTrue(0 == inventory.get(Material.GENERAL));

		Utils.fastForward(110, storage);
		
		assertTrue(9 == inventory.get(Material.GOLD));
		assertTrue(4 == inventory.get(Material.PRIVATE));
		assertTrue(1 == inventory.get(Material.SERGEANT));
		assertTrue(0 == inventory.get(Material.GENERAL));		
	}
	
	@Test
	public void promotePrivateAndSergeant() {
		inventory.put(Material.GOLD, 10);
		inventory.put(Material.PRIVATE, 5);
		inventory.put(Material.SERGEANT, 3);
		inventory.put(Material.GENERAL, 0);
		
		assertTrue(10 == inventory.get(Material.GOLD));
		assertTrue(5 == inventory.get(Material.PRIVATE));
		assertTrue(3 == inventory.get(Material.SERGEANT));
		assertTrue(0 == inventory.get(Material.GENERAL));
		
		Utils.fastForward(110, storage);
		
		assertTrue(8 == inventory.get(Material.GOLD));
		assertTrue(4 == inventory.get(Material.PRIVATE));
		assertTrue(3 == inventory.get(Material.SERGEANT));
		assertTrue(1 == inventory.get(Material.GENERAL));		
		
	}
	
	@Test
	public void promoteWithoutMilitary() {
		inventory.put(Material.GOLD, 10);
		inventory.put(Material.PRIVATE, 0);
		inventory.put(Material.SERGEANT, 0);
		inventory.put(Material.GENERAL, 0);
		
		Utils.fastForward(100, storage);
		
		assertTrue(10 == inventory.get(Material.GOLD));
		assertTrue(0 == inventory.get(Material.PRIVATE));
		assertTrue(0 == inventory.get(Material.SERGEANT));
		assertTrue(0 == inventory.get(Material.GENERAL));		
	}
	
	@Test
	public void promoteWithOnlyGenerals() {
		inventory.put(Material.GOLD, 10);
		inventory.put(Material.PRIVATE, 0);
		inventory.put(Material.SERGEANT, 0);
		inventory.put(Material.GENERAL, 10);
		
		Utils.fastForward(100, storage);
		
		assertTrue(10 == inventory.get(Material.GOLD));
		assertTrue(0 == inventory.get(Material.PRIVATE));
		assertTrue(0 == inventory.get(Material.SERGEANT));
		assertTrue(10 == inventory.get(Material.GENERAL));		

	}
	
	@Test
	public void promoteWithoutGold() {
		inventory.put(Material.GOLD, 0);
		inventory.put(Material.PRIVATE, 5);
		inventory.put(Material.SERGEANT, 0);
		inventory.put(Material.GENERAL, 0);
		
		Utils.fastForward(100, storage);
		
		assertTrue(0 == inventory.get(Material.GOLD));
		assertTrue(5 == inventory.get(Material.PRIVATE));
		assertTrue(0 == inventory.get(Material.SERGEANT));
		assertTrue(0 == inventory.get(Material.GENERAL));		

	}
}
