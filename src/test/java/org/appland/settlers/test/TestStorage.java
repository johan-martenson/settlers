/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
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
        Utils.constructHouse(storage, map);
        
        assertTrue(storage.ready());
        
        assertTrue(storage.needsWorker());
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
        Utils.constructHouse(storage, map);
        
        /* Run game logic once to let the headquarter assign a storage worker to the storage */
        map.stepTime();
        
        Worker sw = null;
        
        for (Worker w : map.getWorkers()) {
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

        Utils.constructHouse(storage, map);
        
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
        
        Utils.constructHouse(storage, map);
        
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
        
        Utils.constructHouse(storage, map);
        
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
        
        Utils.constructHouse(storage, map);
        
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

    @Test
    public void testStorageWorkerGoesBackToStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing storage */
        Point point26 = new Point(8, 8);
        Building storage0 = map.placeBuilding(new Storage(), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorageWorker(map), storage0, map);
        
        /* Destroy the storage */
        Worker storageWorker = storage0.getWorker();
        
        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storage0.getPosition());

        storage0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(STORAGE_WORKER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, headquarter0.getPosition());

        /* Verify that the storage worker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(STORAGE_WORKER), amount + 1);
    }

    @Test
    public void testStorageWorkerGoesBackOnToStorageOnRoadsIfPossibleWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing storage */
        Point point26 = new Point(8, 8);
        Building storage0 = map.placeBuilding(new Storage(), point26);

        /* Connect the storage with the headquarter */
        map.placeAutoSelectedRoad(storage0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorageWorker(map), storage0, map);
        
        /* Destroy the storage */
        Worker storageWorker = storage0.getWorker();
        
        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storage0.getPosition());

        storage0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getTarget(), headquarter0.getPosition());
    
        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point p : storageWorker.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test
    public void testDestroyedStorageIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing storage */
        Point point26 = new Point(8, 8);
        Building storage0 = map.placeBuilding(new Storage(), point26);

        /* Connect the storage with the headquarter */
        map.placeAutoSelectedRoad(storage0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

        assertTrue(storage0.burningDown());

        /* Wait for the storage to stop burning */
        Utils.fastForward(50, map);
        
        assertTrue(storage0.destroyed());
        
        /* Wait for the storage to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), storage0);
            
            map.stepTime();
        }
        
        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(storage0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing storage */
        Point point26 = new Point(8, 8);
        Building storage0 = map.placeBuilding(new Storage(), point26);
        
        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(storage0.getPosition(), storage0.getFlag().getPosition()));
        
        map.removeFlag(storage0.getFlag());

        assertNull(map.getRoad(storage0.getPosition(), storage0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing storage */
        Point point26 = new Point(8, 8);
        Building storage0 = map.placeBuilding(new Storage(), point26);
        
        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(storage0.getPosition(), storage0.getFlag().getPosition()));
        
        storage0.tearDown();

        assertNull(map.getRoad(storage0.getPosition(), storage0.getFlag().getPosition()));
    }

    @Test (expected = Exception.class)
    public void testProductionInStorageCannotBeStopped() throws Exception {

        /* Create game map */
        GameMap map = new GameMap(20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        
        /* Place storage */
        Point point1 = new Point(8, 6);
        Building storage0 = map.placeBuilding(new Storage(), point1);
        
        /* Connect the storage and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);
        
        /* Finish the storage */
        Utils.constructHouse(storage0, map);
        
        /* Assign a worker to the storage */
        StorageWorker storageWorker = new StorageWorker(map);
        
        Utils.occupyBuilding(storageWorker, storage0, map);

        /* Verify that production can't be stopped */
        storage0.stopProduction();
    }
}
