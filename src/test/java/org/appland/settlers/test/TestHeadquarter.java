/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Building.ConstructionState;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
import org.appland.settlers.model.Point;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestHeadquarter {

    @Test
    public void testInitialInventory() {
        Headquarter hq = new Headquarter();

        assertTrue(hq.getAmount(WOOD) == 4);
        assertTrue(hq.getAmount(PLANCK) == 10);
        assertTrue(hq.getAmount(STONE) == 10);
    }

    @Test
    public void testHeadquarterIsReadyDirectly() throws Exception {
        GameMap map = new GameMap();
        Headquarter hq = new Headquarter();
        Point hqPoint = new Point(1, 1);

        map.placeBuilding(hq, hqPoint);

        assertTrue(hq.getConstructionState() == ConstructionState.DONE);
    }

    @Test
    public void testHeadquarterNeedsNoWorker() throws Exception {
        GameMap map = new GameMap();
        Headquarter hq = new Headquarter();
        Point hqPoint = new Point(1, 1);

        map.placeBuilding(hq, hqPoint);

        assertFalse(hq.needsWorker());
    }
}
