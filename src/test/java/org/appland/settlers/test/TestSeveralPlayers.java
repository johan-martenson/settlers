/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import java.util.LinkedList;
import java.util.List;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestSeveralPlayers {

    @Test(expected = Exception.class)
    public void testChooseNoPlayers() throws Exception {

        /* Create empty player list */
        List<Player> players = new LinkedList<>();

        /* Create game map choosing no players */
        GameMap map = new GameMap(players, 20, 20);
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
}
