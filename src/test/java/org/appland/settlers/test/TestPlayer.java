/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Player;
import org.appland.settlers.model.Woodcutter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestPlayer {
    
    @Test
    public void testPlayerIsSetWhenBuildingIsCreated() {

        /* Create player */
        Player player0 = new Player("Player 0");

        /* Create building with the player set */
        Woodcutter woodcutter0 = new Woodcutter(player0);

        /* Verify that the player is set in the building */
        assertNotNull(woodcutter0.getPlayer());
        assertEquals(woodcutter0.getPlayer(), player0);
    }

    @Test
    public void testNameIsSetInPlayer() {

        /* Create player */
        Player player0 = new Player("Player 0");

        /* Verify that the name is set */
        assertEquals(player0.getName(), "Player 0");
    }

    @Test
    public void testCreatePlayer() {
        
        /* Create player 'player one' */
        Player p = new Player("Player one");
    }
    
    @Test
    public void testCreateHouseWithPlayer() {

        /* Create player 'player one' */
        Player p = new Player("Player one");
        
        /* Create house belonging to player one */
        Woodcutter woodcutter0 = new Woodcutter(p);
    }

    @Test
    public void testPlayerIsAlsoSetInBuildingsFlag() {

        /* Create player 'player one' */
        Player p = new Player("Player one");
        
        /* Create house belonging to player one */
        Woodcutter woodcutter0 = new Woodcutter(p);
        
        /* Verify that the building's flag has the player set correctly */
        assertEquals(woodcutter0.getFlag().getPlayer(), p);
    }
}
