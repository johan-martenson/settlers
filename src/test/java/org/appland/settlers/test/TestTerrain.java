/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Tile;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestTerrain {

    @Test(expected = Exception.class)
    public void testGetInvalidTile() throws Exception {
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point p = new Point(5, 6);
        
        Tile tile = map.getTerrain().getTile(p, p.upRight(), p.right());
    }
}
