/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STORAGE_WORKER;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.StorageWorker;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
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
public class TestStorage {
    
    @Test
    public void testUnfinishedStorageNotNeedsWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building storage = map.placeBuilding(new Storage(), point3);

        /* Unfinished samwill doesn't need worker */
        assertFalse(storage.needsWorker());
        assertFalse(storage.needsWorker(STORAGE_WORKER));
    }
    
    @Test
    public void testStorageNeedsWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building storage = map.placeBuilding(new Storage(), point3);
        
        /* Finish construction of the storage */
        Utils.constructMediumHouse(storage);
        
        assertEquals(storage.getConstructionState(), DONE);
        
        assertTrue(storage.needsWorker());
        assertTrue(storage.needsWorker(STORAGE_WORKER));
    }
    
    @Test
    public void testStorageWorkerGetsAssignedToFinishedStorage() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building storage = map.placeBuilding(new Storage(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the storage */
        Utils.constructMediumHouse(storage);
        
        /* Run game logic once to let the headquarter assign a storage worker to the storage */
        map.stepTime();
        
        Worker sw = null;
        
        for (Worker w : map.getAllWorkers()) {
            if (w instanceof StorageWorker) {
                sw = w;
            }
        }
        
        assertNotNull(sw);
        
        assertTrue(sw instanceof StorageWorker);
        assertEquals(sw.getTarget(), storage.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, sw, storage.getPosition());
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(storage.getWorker(), sw);
    }

    @Test
    public void testStorageWorkerRests() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building storage = map.placeBuilding(new Storage(), point3);

        Utils.constructMediumHouse(storage);
        
        StorageWorker sw = new StorageWorker(map);
        
        Utils.occupyBuilding(sw, storage, map);
        
        /* Verify that the storage worker rests */
        int i;
        for (i = 0; i < 50; i++) {
            assertTrue(sw.isInsideBuilding());
            map.stepTime();
        }
    }

    @Test
    public void testStorageWorkerRestsThenDeliversCargo() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        Point point1 = new Point(11, 9);
        Building wc = map.placeBuilding(new Woodcutter(), point1.upLeft());
        
        Point point2 = new Point(9, 9);
        
        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building storage = map.placeBuilding(new Storage(), point3.upLeft());

        map.placeRoad(point1, point2, point3);
        
        Utils.constructMediumHouse(storage);
        
        StorageWorker sw = new StorageWorker(map);
        
        Utils.occupyBuilding(sw, storage, map);
        
        /* The storage worker rests */
        Utils.fastForward(19, map);
        
        /* Put plancks in the storage */
        storage.putCargo(new Cargo(PLANCK, map));
        
        /* The storage worker delivers stone or plancks to the woodcutter */
        assertTrue(sw.isInsideBuilding());
        
        map.stepTime();
        
        assertFalse(sw.isInsideBuilding());
        assertNotNull(sw.getCargo());
        assertEquals(sw.getTarget(), storage.getFlag().getPosition());
        assertTrue(storage.getFlag().getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, sw, storage.getFlag().getPosition());
        
        assertNull(sw.getCargo());
        assertFalse(storage.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testStorageWorkerGoesBackToStorageAfterDelivery() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        Point point1 = new Point(11, 9);
        Building wc = map.placeBuilding(new Woodcutter(), point1.upLeft());
        
        Point point2 = new Point(9, 9);
        
        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building storage = map.placeBuilding(new Storage(), point3.upLeft());

        map.placeRoad(point1, point2, point3);
        
        Utils.constructMediumHouse(storage);
        
        StorageWorker sw = new StorageWorker(map);
        
        Utils.occupyBuilding(sw, storage, map);
        
        /* The storage worker rests */
        Utils.fastForward(19, map);
        
        /* Put plancks in the storage */
        storage.putCargo(new Cargo(PLANCK, map));
        
        /* The storage worker delivers stone or plancks to the woodcutter */
        assertTrue(sw.isInsideBuilding());
        
        map.stepTime();
        
        assertFalse(sw.isInsideBuilding());
        assertNotNull(sw.getCargo());
        assertEquals(sw.getTarget(), storage.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, sw, storage.getFlag().getPosition());
        
        /* Verify that the storage worker goes back to the storage */
        assertEquals(sw.getTarget(), storage.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, sw);
        
        assertTrue(sw.isInsideBuilding());
    }

    @Test
    public void testStorageWorkerRestsInStorageAfterDelivery() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        Point point1 = new Point(11, 9);
        Building wc = map.placeBuilding(new Woodcutter(), point1.upLeft());
        
        Point point2 = new Point(9, 9);
        
        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building storage = map.placeBuilding(new Storage(), point3.upLeft());

        map.placeRoad(point1, point2, point3);
        
        Utils.constructMediumHouse(storage);
        
        StorageWorker sw = new StorageWorker(map);
        
        Utils.occupyBuilding(sw, storage, map);
        
        /* The storage worker rests */
        Utils.fastForward(19, map);
        
        /* Put plancks in the storage */
        storage.putCargo(new Cargo(PLANCK, map));
        
        /* The storage worker delivers stone or plancks to the woodcutter */
        assertTrue(sw.isInsideBuilding());
        
        map.stepTime();
        
        assertFalse(sw.isInsideBuilding());
        assertEquals(sw.getTarget(), storage.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, sw, storage.getFlag().getPosition());
        
        assertNull(sw.getCargo());

        /* Let the storage worker go back to the storage */
        assertEquals(sw.getTarget(), storage.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, sw);
        
        /* Verify that the storage worker stays in the storage and rests */
        int i;
        for (i = 0; i < 20; i++) {
            assertTrue(sw.isInsideBuilding());
            map.stepTime();
        }
    }
}
