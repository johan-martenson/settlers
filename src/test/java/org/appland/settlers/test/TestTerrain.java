/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Tile;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestTerrain {

    @Test(expected = Exception.class)
    public void testGetInvalidTile() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point p = new Point(5, 6);
        
        Tile tile = map.getTerrain().getTile(p, p.upRight(), p.right());
    }
}
