/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Tile;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author johan
 */
public class TestTerrain {

    @Test(expected = Exception.class)
    public void testGetInvalidTile() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Test getting a tile */
        Point point = new Point(5, 6);
        Tile tile = map.getTerrain().getTileUpRight(point);
    }
}
