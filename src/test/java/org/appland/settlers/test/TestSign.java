/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Headquarter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Size.SMALL;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author johan
 */
public class TestSign {

    @Test
    public void testSignExpires() throws Exception {

        // Create a new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 20, 21);

        // Place headquarter
        var point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place sign
        var point1 = new Point(6, 6);
        map.placeSign(IRON, SMALL, point1);

        // Verify that the sign remains for the right time
        for (int i = 0; i < 2000; i++) {
            assertTrue(map.isSignAtPoint(point1));

            map.stepTime();
        }

        assertFalse(map.isSignAtPoint(point1));
    }
}
