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
import org.junit.Test;
import static org.junit.Assert.assertEquals;

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
    public void testChooseOnePlayer() throws Exception {

        /* Create player list with one player */
        List<Player> players = new LinkedList<>();
        players.add(new Player("Player 0"));

        /* Create game map choosing no players */
        GameMap map = new GameMap(players, 20, 20);

        /* Verify that there is one player in the game map */
        assertEquals(map.getPlayers().size(), 1);

        /* Verify that the one player is correct */
        assertEquals(map.getPlayers().get(0), players.get(0));
    }
}
