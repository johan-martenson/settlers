/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestSeveralPlayers {
    
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
