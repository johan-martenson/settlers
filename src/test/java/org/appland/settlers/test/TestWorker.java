/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.SawmillWorker;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestWorker {
    
    @Test
    public void testWorkerCannotEnterBuildingWhenItsNotAtRightPosition() throws Exception {
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);
        
        SawmillWorker worker = new SawmillWorker(map);
        map.placeWorker(worker, sawmill.getFlag());

        try {
            worker.enterBuilding(sawmill);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertFalse(worker.isInsideBuilding());
    }

    @Test
    public void testWorkerCanEnterBuilding() {
        // TODO: Implement
    }
}
