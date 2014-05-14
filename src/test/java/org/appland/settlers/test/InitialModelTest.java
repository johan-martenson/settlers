package org.appland.settlers.test;

import static org.junit.Assert.*;

import java.util.Map;

import org.appland.settlers.controller.Controller;
import org.appland.settlers.model.Game;
import org.appland.settlers.model.InvalidNumberOfPlayersException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Storage;
import org.junit.Before;
import org.junit.Test;

public class InitialModelTest {

	Controller c = null;
	
	@Before
	public void setup() {
		c = new Controller();
	}
	
	@Test(expected=InvalidNumberOfPlayersException.class)
	public void testZeroPlayers() throws Exception {
		c.createInitialGame(0);
	}
	
	@Test(expected=InvalidNumberOfPlayersException.class)
	public void testOneTooFewPlayers() throws Exception {
		c.createInitialGame(1);
	}
	
	@Test(expected=InvalidNumberOfPlayersException.class)
	public void testNegativeNrPlayers() throws Exception {
		c.createInitialGame(-1); 
	}

	@Test(expected=InvalidNumberOfPlayersException.class)
	public void testTooManyPlayers() throws Exception {
		c.createInitialGame(5);
	}
	
	@Test
	public void testCorrectInitialModel() throws InvalidNumberOfPlayersException {
		Game game = c.createInitialGame(2);
		
		/* Verify that the initial game instance is valid */
		assertNotNull(game);
		
		assertTrue(2 == game.getPlayers().size());
		
		/* Verify that the number of players are correct */
		Player player = game.getPlayers().get(0);
		
		/* Verify that there is only a single storage when the game begins */
		assertTrue(1 == player.getStorages().size());
		
		/* Verify that the initial inventory is correct */
		Storage storage = player.getStorages().get(0);
		
		Map<Material, Integer> inventory = storage.getInventory();
		
		assertNotNull(inventory);
		
		assertTrue(0 == inventory.get(Material.SHIELD));
		assertTrue(0 == inventory.get(Material.SWORD));
		assertTrue(0 == inventory.get(Material.PRIVATE));
		assertTrue(0 == inventory.get(Material.SERGEANT));
		assertTrue(0 == inventory.get(Material.GENERAL));
		assertTrue(0 == inventory.get(Material.BEER));
		assertTrue(0 == inventory.get(Material.GOLD));
	}
}
