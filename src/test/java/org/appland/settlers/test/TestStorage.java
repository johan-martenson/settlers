/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STORAGE_WORKER;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import org.appland.settlers.model.Player;
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building storage = map.placeBuilding(new Storage(player0), point3);

        /* Unfinished samwill doesn't need worker */
        assertFalse(storage.needsWorker());
    }
    
    @Test
    public void testStorageNeedsWorker() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building storage = map.placeBuilding(new Storage(player0), point3);
        
        /* Finish construction of the storage */
        Utils.constructHouse(storage, map);
        
        assertTrue(storage.ready());
        
        assertTrue(storage.needsWorker());
    }
    
    @Test
    public void testStorageWorkerGetsAssignedToFinishedStorage() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building storage = map.placeBuilding(new Storage(player0), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building storage = map.placeBuilding(new Storage(player0), point3);

        Utils.constructHouse(storage, map);
        
        StorageWorker sw = new StorageWorker(player0, map);
        
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(11, 9);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1.upLeft());
        
        Point point2 = new Point(9, 9);
        
        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building storage = map.placeBuilding(new Storage(player0), point3.upLeft());

        map.placeRoad(player0, point1, point2, point3);
        
        Utils.constructHouse(storage, map);
        
        StorageWorker sw = new StorageWorker(player0, map);
        
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(11, 9);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1.upLeft());
        
        Point point2 = new Point(9, 9);
        
        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building storage = map.placeBuilding(new Storage(player0), point3.upLeft());

        map.placeRoad(player0, point1, point2, point3);
        
        Utils.constructHouse(storage, map);
        
        StorageWorker sw = new StorageWorker(player0, map);
        
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(11, 9);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1.upLeft());
        
        Point point2 = new Point(9, 9);
        
        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building storage = map.placeBuilding(new Storage(player0), point3.upLeft());

        map.placeRoad(player0, point1, point2, point3);
        
        Utils.constructHouse(storage, map);
        
        StorageWorker sw = new StorageWorker(player0, map);
        
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing storage */
        Point point26 = new Point(8, 8);
        Building storage0 = map.placeBuilding(new Storage(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorageWorker(player0, map), storage0, map);
        
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing storage */
        Point point26 = new Point(8, 8);
        Building storage0 = map.placeBuilding(new Storage(player0), point26);

        /* Connect the storage with the headquarter */
        map.placeAutoSelectedRoad(player0, storage0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorageWorker(player0, map), storage0, map);
        
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing storage */
        Point point26 = new Point(8, 8);
        Building storage0 = map.placeBuilding(new Storage(player0), point26);

        /* Connect the storage with the headquarter */
        map.placeAutoSelectedRoad(player0, storage0.getFlag(), headquarter0.getFlag());
        
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing storage */
        Point point26 = new Point(8, 8);
        Building storage0 = map.placeBuilding(new Storage(player0), point26);
        
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing storage */
        Point point26 = new Point(8, 8);
        Building storage0 = map.placeBuilding(new Storage(player0), point26);
        
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place storage */
        Point point1 = new Point(8, 6);
        Building storage0 = map.placeBuilding(new Storage(player0), point1);
        
        /* Connect the storage and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the storage */
        Utils.constructHouse(storage0, map);
        
        /* Assign a worker to the storage */
        StorageWorker storageWorker = new StorageWorker(player0, map);
        
        Utils.occupyBuilding(storageWorker, storage0, map);

        /* Verify that production can't be stopped */
        storage0.stopProduction();
    }

    @Test
    public void testAssignedStorageWorkerHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place storage*/
        Point point1 = new Point(20, 14);
        Building storage0 = map.placeBuilding(new Storage(player0), point1);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);
        
        /* Connect the storage with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), storage0.getFlag());

        /* Wait for storage worker to get assigned and leave the headquarter */
        List<StorageWorker> workers = Utils.waitForWorkersOutsideBuilding(StorageWorker.class, 1, player0, map);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        StorageWorker worker = workers.get(0);

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = new Fortress(player0);
        map.placeBuilding(fortress0, point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0, map);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0, map);

        /* Place storage close to the new border */
        Point point4 = new Point(28, 18);
        Storage storage0 = map.placeBuilding(new Storage(player0), point4);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Occupy the storage */
        StorageWorker worker = Utils.occupyBuilding(new StorageWorker(player0, map), storage0, map);

        /* Connect the storage to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storage0.getFlag(), headquarter0.getFlag());

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down*/
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }
}
