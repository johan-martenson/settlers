/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.StorageWorker;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.STORAGE_WORKER;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author johan
 */
public class TestStorage {

    @Test
    public void testStorageOnlyNeedsFourPlancksAndThreeStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing storage */
        Point point22 = new Point(6, 22);
        Building storage0 = map.placeBuilding(new Storage(player0), point22);

        /* Deliver four planck and three stone */
        Cargo planckCargo = new Cargo(PLANCK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        storage0.putCargo(planckCargo);
        storage0.putCargo(planckCargo);
        storage0.putCargo(planckCargo);
        storage0.putCargo(planckCargo);
        storage0.putCargo(stoneCargo);
        storage0.putCargo(stoneCargo);
        storage0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the storage */
        for (int i = 0; i < 150; i++) {
            assertTrue(storage0.underConstruction());

            map.stepTime();
        }

        assertTrue(storage0.ready());
    }

    @Test
    public void testStorageCannotBeConstructedWithTooFewPlancks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing storage */
        Point point22 = new Point(6, 22);
        Building storage0 = map.placeBuilding(new Storage(player0), point22);

        /* Deliver three plancks and three stone */
        Cargo planckCargo = new Cargo(PLANCK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        storage0.putCargo(planckCargo);
        storage0.putCargo(planckCargo);
        storage0.putCargo(planckCargo);
        storage0.putCargo(stoneCargo);
        storage0.putCargo(stoneCargo);
        storage0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the storage */
        for (int i = 0; i < 500; i++) {
            assertTrue(storage0.underConstruction());

            map.stepTime();
        }

        assertFalse(storage0.ready());
    }

    @Test
    public void testStorageCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing storage */
        Point point22 = new Point(6, 22);
        Building storage0 = map.placeBuilding(new Storage(player0), point22);

        /* Deliver four plancks and two stones */
        Cargo planckCargo = new Cargo(PLANCK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        storage0.putCargo(planckCargo);
        storage0.putCargo(planckCargo);
        storage0.putCargo(planckCargo);
        storage0.putCargo(planckCargo);
        storage0.putCargo(stoneCargo);
        storage0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the storage */
        for (int i = 0; i < 500; i++) {
            assertTrue(storage0.underConstruction());

            map.stepTime();
        }

        assertFalse(storage0.ready());
    }

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

        /* Unfinished sawmill doesn't need worker */
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
        for (int i = 0; i < 50; i++) {
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
        for (int i = 0; i < 20; i++) {
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
    public void testStorageWorkerDoesNotGoBackToUnfinishedStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing storage */
        Point point26 = new Point(17, 17);
        Building storage0 = map.placeBuilding(new Storage(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorageWorker(player0, map), storage0, map);

        /* Place second storage */
        Point point2 = new Point(15, 15);
        Building storage1 = map.placeBuilding(new Storage(player0), point2);

        /* Connect the storages */
        Road road0 = map.placeAutoSelectedRoad(player0, storage0.getFlag(), storage1.getFlag());

        /* Destroy the storage */
        Worker storageWorker = storage0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storage0.getPosition());

        storage0.tearDown();

        /* Verify that the storage worker avoids the second storage because it's
           burning, although it's close
        */
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storage1.getPosition());
    }

    @Test
    public void testStorageWorkerDoesNotGoBackToBurningStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing storage */
        Point point26 = new Point(17, 17);
        Building storage0 = map.placeBuilding(new Storage(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorageWorker(player0, map), storage0, map);

        /* Place second storage */
        Point point2 = new Point(15, 15);
        Building storage1 = map.placeBuilding(new Storage(player0), point2);

        /* Connect the storages */
        Road road0 = map.placeAutoSelectedRoad(player0, storage0.getFlag(), storage1.getFlag());

        /* Finish construction of the second storage */
        Utils.constructHouse(storage1, map);

        /* Destroy the second storage */
        storage1.tearDown();

        /* Destroy the storage */
        Worker storageWorker = storage0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storage0.getPosition());

        storage0.tearDown();

        /* Verify that the storage worker avoids the second storage because it's
           burning, although it's close
        */
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storage1.getPosition());
    }

    @Test
    public void testStorageWorkerDoesNotGoBackToDestroyedStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing storage */
        Point point26 = new Point(17, 17);
        Building storage0 = map.placeBuilding(new Storage(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorageWorker(player0, map), storage0, map);

        /* Place second storage */
        Point point2 = new Point(15, 15);
        Building storage1 = map.placeBuilding(new Storage(player0), point2);

        /* Connect the storages */
        Road road0 = map.placeAutoSelectedRoad(player0, storage0.getFlag(), storage1.getFlag());

        /* Finish construction of the second storage */
        Utils.constructHouse(storage1, map);

        /* Destroy the second storage */
        storage1.tearDown();

        /* Wait for the second storage to burn down */
        Utils.waitForBuildingToBurnDown(storage1, map);

        /* Destroy the storage */
        Worker storageWorker = storage0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storage0.getPosition());

        storage0.tearDown();

        /* Verify that the storage worker avoids the second storage because it's
           destroyed, although it's close
        */
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storage1.getPosition());
    }

    @Test
    public void testStorageWorkerDoesNotGoBackOffroadToUnfinishedStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing storage */
        Point point26 = new Point(17, 17);
        Building storage0 = map.placeBuilding(new Storage(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorageWorker(player0, map), storage0, map);

        /* Place second storage */
        Point point2 = new Point(15, 15);
        Building storage1 = map.placeBuilding(new Storage(player0), point2);

        /* Destroy the storage */
        Worker storageWorker = storage0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storage0.getPosition());

        storage0.tearDown();

        /* Verify that the storage worker avoids the second storage because it's
           burning, although it's close
        */
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storage1.getPosition());
    }

    @Test
    public void testStorageWorkerDoesNotGoBackOffroadToBurningStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing storage */
        Point point26 = new Point(17, 17);
        Building storage0 = map.placeBuilding(new Storage(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorageWorker(player0, map), storage0, map);

        /* Place second storage */
        Point point2 = new Point(15, 15);
        Building storage1 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the second storage */
        Utils.constructHouse(storage1, map);

        /* Destroy the second storage */
        storage1.tearDown();

        /* Destroy the storage */
        Worker storageWorker = storage0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storage0.getPosition());

        storage0.tearDown();

        /* Verify that the storage worker avoids the second storage because it's
           burning, although it's close
        */
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storage1.getPosition());
    }

    @Test
    public void testStorageWorkerDoesNotGoBackOffroadToDestroyedStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing storage */
        Point point26 = new Point(17, 17);
        Building storage0 = map.placeBuilding(new Storage(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorageWorker(player0, map), storage0, map);

        /* Place second storage */
        Point point2 = new Point(15, 15);
        Building storage1 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the second storage */
        Utils.constructHouse(storage1, map);

        /* Destroy the second storage */
        storage1.tearDown();

        /* Wait for the second storage to burn down */
        Utils.waitForBuildingToBurnDown(storage1, map);

        /* Destroy the storage */
        Worker storageWorker = storage0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storage0.getPosition());

        storage0.tearDown();

        /* Verify that the storage worker avoids the second storage because it's
           destroyed, although it's close
        */
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storage1.getPosition());
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

        /* Place storage */
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
        Player player2 = new Player("Player 2", RED);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 2's headquarter */
        Building headquarter2 = new Headquarter(player2);
        Point point10 = new Point(70, 70);
        map.placeBuilding(headquarter2, point10);

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

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testRoadCloseToOpponentGetsPopulatedFromCorrectPlayer() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);
        Player player2 = new Player("Player 2", RED);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place player 2's headquarter */
        Building headquarter2 = new Headquarter(player2);
        Point point10 = new Point(70, 70);
        map.placeBuilding(headquarter2, point10);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = new Fortress(player0);
        map.placeBuilding(fortress0, point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0, map);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 9, fortress0, map);

        /* Connect the fortress with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        /* Occupy the road */
        Utils.occupyRoad(road0, map);

        /* Place barracks close to the new border */
        Point point4 = new Point(34, 18);
        Barracks barracks0 = map.placeBuilding(new Barracks(player1), point4);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);

        /* Connect the barracks with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player1, headquarter1.getFlag(), barracks0.getFlag());

        /* Occupy the road */
        Utils.occupyRoad(road1, map);

        /* Capture the barracks for player 0 */
        player0.attack(barracks0, 2);

        /* Wait for player 0 to take over the barracks */
        for (int i = 0; i < 2000; i++) {

            if (barracks0.getPlayer().equals(player0) && barracks0.getNumberOfHostedMilitary() > 0) {
                break;
            }

            map.stepTime();
        }

        assertEquals(barracks0.getPlayer(), player0);
        assertTrue(barracks0.getNumberOfHostedMilitary() > 0);

        /* Connect the captured barracks with the headquarter */
        Road road4 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), fortress0.getFlag());

        /* Occupy the road */
        Utils.occupyRoad(road4, map);

        /* Place flag */
        Point point5 = new Point(32, 18);
        Flag flag0 = map.placeFlag(player0, point5);

        /* Place road */
        Road road3 = map.placeAutoSelectedRoad(player0, flag0, barracks0.getFlag());

        /* Verify that player 1's headquarter is closer to the road */
        for (Point point : road3.getWayPoints()) {

            assertTrue(point.distance(headquarter1.getPosition()) < point.distance(headquarter0.getPosition()));
        }

        /* Verify that the barracks gets populated from the right headquarter only */
        int player0Couriers = Utils.findWorkersOfTypeOutsideForPlayer(Courier.class, player0, map).size();
        int player1Couriers = Utils.findWorkersOfTypeOutsideForPlayer(Courier.class, player1, map).size();

        for (int i = 0; i < 1000; i++) {
            Courier courier = road3.getCourier();

            if (courier != null && road3.getWayPoints().contains(courier.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(road3.getCourier());
        assertTrue(road3.getWayPoints().contains(road3.getCourier().getPosition()));
        assertEquals(road3.getCourier().getPlayer(), player0);
        assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Courier.class, player0, map).size(), player0Couriers + 1);
    }

    @Test
    public void testStorageWorkerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing storage */
        Point point2 = new Point(14, 4);
        Building storage0 = map.placeBuilding(new Storage(player0), point2.upLeft());

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, storage0.getFlag());

        /* Wait for the storage worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(StorageWorker.class, 1, player0, map);

        StorageWorker storageWorker = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof StorageWorker && storage0.getPosition().equals(w.getTarget())) {
                storageWorker = (StorageWorker) w;
            }
        }

        assertNotNull(storageWorker);
        assertEquals(storageWorker.getTarget(), storage0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the storage worker has started walking */
        assertFalse(storageWorker.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the storage worker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, flag0.getPosition());

        assertEquals(storageWorker.getPosition(), flag0.getPosition());

        /* Verify that the storage worker returns to the headquarter when it reaches the flag */
        assertEquals(storageWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, headquarter0.getPosition());
    }

    @Test
    public void testStorageWorkerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing storage */
        Point point2 = new Point(14, 4);
        Building storage0 = map.placeBuilding(new Storage(player0), point2.upLeft());

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, storage0.getFlag());

        /* Wait for the storage worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(StorageWorker.class, 1, player0, map);

        StorageWorker storageWorker = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof StorageWorker && storage0.getPosition().equals(w.getTarget())) {
                storageWorker = (StorageWorker) w;
            }
        }

        assertNotNull(storageWorker);
        assertEquals(storageWorker.getTarget(), storage0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the storageWorker has started walking */
        assertFalse(storageWorker.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the storage worker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, flag0.getPosition());

        assertEquals(storageWorker.getPosition(), flag0.getPosition());

        /* Verify that the storage worker continues to the final flag */
        assertEquals(storageWorker.getTarget(), storage0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, storage0.getFlag().getPosition());

        /* Verify that the storage worker goes out to storage instead of going directly back */
        assertNotEquals(storageWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testStorageWorkerReturnsToStorageIfStorageIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing storage */
        Point point2 = new Point(14, 4);
        Building storage0 = map.placeBuilding(new Storage(player0), point2.upLeft());

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, storage0.getFlag());

        /* Wait for the storage worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(StorageWorker.class, 1, player0, map);

        StorageWorker storageWorker = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof StorageWorker && storage0.getPosition().equals(w.getTarget())) {
                storageWorker = (StorageWorker) w;
            }
        }

        assertNotNull(storageWorker);
        assertEquals(storageWorker.getTarget(), storage0.getPosition());

        /* Wait for the storageWorker to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, flag0.getPosition());

        map.stepTime();

        /* See that the storage worker has started walking */
        assertFalse(storageWorker.isExactlyAtPoint());

        /* Tear down the storage */
        storage0.tearDown();

        /* Verify that the storage worker continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, storage0.getFlag().getPosition());

        assertEquals(storageWorker.getPosition(), storage0.getFlag().getPosition());

        /* Verify that the storage worker goes back to storage */
        assertEquals(storageWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testStorageWorkerGoesOffroadBackToClosestStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing storage */
        Point point26 = new Point(17, 17);
        Building storage0 = map.placeBuilding(new Storage(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorageWorker(player0, map), storage0, map);

        /* Place a second storage closer to the storage */
        Point point2 = new Point(13, 13);
        Storage storage1 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage1, map);

        /* Destroy the storage */
        Worker storageWorker = storage0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storage0.getPosition());

        storage0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getTarget(), storage1.getPosition());

        int amount = storage1.getAmount(STORAGE_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, storage1.getPosition());

        /* Verify that the storage worker is stored correctly in the headquarter */
        assertEquals(storage1.getAmount(STORAGE_WORKER), amount + 1);
    }

    @Test
    public void testStorageWorkerReturnsOffroadAndAvoidsBurningStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing storage */
        Point point26 = new Point(17, 17);
        Building storage0 = map.placeBuilding(new Storage(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorageWorker(player0, map), storage0, map);

        /* Place a second storage closer to the storage */
        Point point2 = new Point(13, 13);
        Storage storage1 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage1, map);

        /* Destroy the storage */
        storage1.tearDown();

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
    public void testStorageWorkerReturnsOffroadAndAvoidsDestroyedStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing storage */
        Point point26 = new Point(17, 17);
        Building storage0 = map.placeBuilding(new Storage(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorageWorker(player0, map), storage0, map);

        /* Place a second storage closer to the storage */
        Point point2 = new Point(13, 13);
        Storage storage1 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage1, map);

        /* Destroy the storage */
        storage1.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storage1, map);

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
    public void testStorageWorkerReturnsOffroadAndAvoidsUnfinishedStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing storage */
        Point point26 = new Point(17, 17);
        Building storage0 = map.placeBuilding(new Storage(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorageWorker(player0, map), storage0, map);

        /* Place a second storage closer to the storage */
        Point point2 = new Point(13, 13);
        Storage storage1 = map.placeBuilding(new Storage(player0), point2);

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

        /* Verify that the storageWorker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(STORAGE_WORKER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(17, 17);
        Building storage0 = map.placeBuilding(new Storage(player0), point26);

        /* Place road to connect the headquarter and the storage */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), storage0.getFlag());

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(StorageWorker.class, 1, player0, map).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, storage0.getFlag().getPosition());

        /* Tear down the building */
        storage0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquarter instead of entering
        */
        assertEquals(worker.getTarget(), storage0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, storage0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testStorageCannotProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storage */
        Point point1 = new Point(10, 10);
        Building storage0 = map.placeBuilding(new Storage(player0), point1);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Populate the storage */
        Worker storageWorker0 = Utils.occupyBuilding(new StorageWorker(player0, map), storage0, map);

        /* Verify that the storage can produce */
        assertFalse(storage0.canProduce());
    }
}
