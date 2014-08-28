/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Building.ConstructionState;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.StorageWorker;
import org.appland.settlers.model.Woodcutter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
        GameMap map = new GameMap(15, 15);
        Headquarter hq = new Headquarter();
        Point hqPoint = new Point(5, 5);

        map.placeBuilding(hq, hqPoint);

        assertTrue(hq.ready());
    }

    @Test
    public void testHeadquarterNeedsNoWorker() throws Exception {
        GameMap map = new GameMap(15, 15);
        Headquarter hq = new Headquarter();
        Point hqPoint = new Point(5, 5);

        map.placeBuilding(hq, hqPoint);

        assertFalse(hq.needsWorker());
    }

    @Test
    public void testHeadquarterGetsWorkerAutomatically() throws Exception {
        GameMap map = new GameMap(15, 15);
        Headquarter hq = new Headquarter();
        Point hqPoint = new Point(5, 5);

        map.placeBuilding(hq, hqPoint);

        assertFalse(hq.needsWorker());
        assertNotNull(hq.getWorker());
    }

    @Test
    public void testHeadquartersStorageWorkerDeliversCargo() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        Point point1 = new Point(11, 9);
        Building wc = map.placeBuilding(new Woodcutter(), point1.upLeft());
        
        Point point2 = new Point(9, 9);

        map.placeAutoSelectedRoad(hq.getFlag(), wc.getFlag());
                
        /* The storage worker rests */
        Utils.fastForward(19, map);
        
        /* Verify that the hq has plancks */
        assertTrue(hq.getAmount(PLANCK) > 0);
        
        /* The storage worker delivers stone or plancks to the woodcutter */
        assertTrue(hq.getWorker() instanceof StorageWorker);
        
        StorageWorker sw = (StorageWorker)hq.getWorker();
        
        assertTrue(sw.isInsideBuilding());
        
        map.stepTime();
        
        assertFalse(sw.isInsideBuilding());
        assertNotNull(sw.getCargo());
        assertEquals(sw.getTarget(), hq.getFlag().getPosition());
        assertTrue(hq.getFlag().getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, sw, hq.getFlag().getPosition());
        
        assertNull(sw.getCargo());
        assertFalse(hq.getFlag().getStackedCargo().isEmpty());
    }
}
