/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.TransportCategory;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Scout;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.StorehouseWorker;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.buildings.Well;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.GENERAL_RANK;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestStorehouse {

    /*
    TODO:
      - material can be pushed out:
          - push cargo from headquartersto storehouse DONE
          - push worker from headquartersto storehouse DONE
          - push cargo from headquarterswithout any place to store - headquartersflag fills up DONE
          - push out follows priority order DONE
      - material can be blocked:
          - deliveries go to another storehouse DONE
          - test for each type of house/worker: (DONE)
            - flags fill up and then deliveries stop if there is nowhere to put them
            - push worker from headquarterswithout any place to store - worker goes away and dies
            - push worker from headquarterswithout blocking - worker goes out and in again
            - when house is burned and storing of worker is blocked, worker goes to other storehouse
            - when house is burned, storing of worker is blocked, and there is no other place to store - worker walks away and dies
      - push out and block at the same time - material and worker
     */

    @Test
    public void testStorageOnlyNeedsFourPlanksAndThreeStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 12);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Deliver four plank and three stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(stoneCargo);
        storehouse0.putCargo(stoneCargo);
        storehouse0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(storehouse0);

        /* Verify that this is enough to construct the storage */
        for (int i = 0; i < 150; i++) {
            assertTrue(storehouse0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(storehouse0.isReady());
    }

    @Test
    public void testStorageCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 12);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Deliver three planks and three stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(stoneCargo);
        storehouse0.putCargo(stoneCargo);
        storehouse0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(storehouse0);

        /* Verify that this is not enough to construct the storage */
        for (int i = 0; i < 500; i++) {
            assertTrue(storehouse0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(storehouse0.isReady());
    }

    @Test
    public void testStorageCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 12);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Deliver four planks and two stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(stoneCargo);
        storehouse0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(storehouse0);

        /* Verify that this is not enough to construct the storage */
        for (int i = 0; i < 500; i++) {
            assertTrue(storehouse0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(storehouse0.isReady());
    }

    @Test
    public void testStorageIsConstructedWithRequiredResources() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 12);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Deliver four planks and two stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(stoneCargo);
        storehouse0.putCargo(stoneCargo);
        storehouse0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(storehouse0);

        /* Verify that this is not enough to construct the storage */
        for (int i = 0; i < 1000; i++) {

            if (storehouse0.isReady()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(storehouse0.isReady());
    }

    @Test
    public void testUnfinishedStorageNotNeedsWorker() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storage */
        Point point3 = new Point(7, 9);
        Storehouse storage = map.placeBuilding(new Storehouse(player0), point3);

        /* Verify that an unfinished storage doesn't need a worker */
        assertFalse(storage.needsWorker());
    }

    @Test
    public void testStorageNeedsWorker() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storage */
        Point point3 = new Point(7, 9);
        Storehouse storage = map.placeBuilding(new Storehouse(player0), point3);

        /* Finish construction of the storage */
        Utils.constructHouse(storage);

        assertTrue(storage.isReady());

        /* Verify that the finished storage needs a worker */
        assertTrue(storage.needsWorker());
    }

    @Test
    public void testStorageWorkerGetsAssignedToFinishedStorage() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storage */
        Point point3 = new Point(7, 9);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point3);

        /* Connect the storage with the headquarters*/
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse);

        /* Run game logic once to let the headquartersassign a storage worker to the storage */
        map.stepTime();

        Worker storageWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof StorehouseWorker) {
                storageWorker = worker;
            }
        }

        assertNotNull(storageWorker);

        assertEquals(storageWorker.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, storehouse.getPosition());

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storehouse.getWorker(), storageWorker);
    }

    @Test
    public void testStorageWorkerIsNotASoldier() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storage */
        Point point3 = new Point(7, 9);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point3);

        /* Connect the storage with the headquarters*/
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse);

        /* Run game logic once to let the headquartersassign a storage worker to the storage */
        map.stepTime();

        Worker storageWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof StorehouseWorker) {
                storageWorker = worker;
            }
        }

        assertNotNull(storageWorker);

        assertEquals(storageWorker.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, storehouse.getPosition());

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storehouse.getWorker(), storageWorker);
    }

    @Test
    public void testStorageWorkerRests() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storage */
        Point point3 = new Point(7, 9);
        Storehouse storage = map.placeBuilding(new Storehouse(player0), point3);

        Utils.constructHouse(storage);

        StorehouseWorker storehouseWorker0 = new StorehouseWorker(player0, map);

        Utils.occupyBuilding(storehouseWorker0, storage);

        /* Verify that the storage worker rests */
        for (int i = 0; i < 50; i++) {
            assertTrue(storehouseWorker0.isInsideBuilding());
            map.stepTime();
        }
    }

    @Test
    public void testStorageWorkerRestsThenDeliversCargo() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(11, 9);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1.upLeft());

        /* Place storage */
        Point point3 = new Point(7, 9);
        Storehouse storage = map.placeBuilding(new Storehouse(player0), point3.upLeft());

        /* Connect the storage with the woodcutter */
        Point point2 = new Point(9, 9);
        map.placeRoad(player0, point1, point2, point3);

        /* Finish construction of the storage */
        Utils.constructHouse(storage);

        /* Occupy the storage worker */
        StorehouseWorker storehouseWorker0 = new StorehouseWorker(player0, map);
        Utils.occupyBuilding(storehouseWorker0, storage);

        /* The storage worker rests */
        Utils.fastForward(19, map);

        /* Put planks in the storage */
        storage.putCargo(new Cargo(PLANK, map));

        /* The storage worker delivers stone or planks to the woodcutter */
        assertTrue(storehouseWorker0.isInsideBuilding());

        map.stepTime();

        assertFalse(storehouseWorker0.isInsideBuilding());
        assertNotNull(storehouseWorker0.getCargo());
        assertEquals(storehouseWorker0.getTarget(), storage.getFlag().getPosition());
        assertTrue(storage.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, storehouseWorker0, storage.getFlag().getPosition());

        assertNull(storehouseWorker0.getCargo());
        assertFalse(storage.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testStorageWorkerGoesBackToStorageAfterDelivery() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(11, 9);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1.upLeft());

        /* Place storage */
        Point point3 = new Point(7, 9);
        Storehouse storage = map.placeBuilding(new Storehouse(player0), point3.upLeft());

        /* Connect the storage with woodcutter */
        Point point2 = new Point(9, 9);
        map.placeRoad(player0, point1, point2, point3);

        Utils.constructHouse(storage);

        StorehouseWorker storehouseWorker0 = new StorehouseWorker(player0, map);

        Utils.occupyBuilding(storehouseWorker0, storage);

        /* The storage worker rests */
        Utils.fastForward(19, map);

        /* Put planks in the storage */
        storage.putCargo(new Cargo(PLANK, map));

        /* The storage worker delivers stone or planks to the woodcutter */
        assertTrue(storehouseWorker0.isInsideBuilding());

        map.stepTime();

        assertFalse(storehouseWorker0.isInsideBuilding());
        assertNotNull(storehouseWorker0.getCargo());
        assertEquals(storehouseWorker0.getTarget(), storage.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, storehouseWorker0, storage.getFlag().getPosition());

        /* Verify that the storage worker goes back to the storage */
        assertEquals(storehouseWorker0.getTarget(), storage.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, storehouseWorker0);

        assertTrue(storehouseWorker0.isInsideBuilding());
    }

    @Test
    public void testStorageWorkerRestsInStorageAfterDelivery() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(11, 9);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1.upLeft());

        /* Place storage */
        Point point3 = new Point(7, 9);
        Storehouse storage = map.placeBuilding(new Storehouse(player0), point3.upLeft());

        /* Connect the storage with the woodcutter */
        Point point2 = new Point(9, 9);
        map.placeRoad(player0, point1, point2, point3);

        Utils.constructHouse(storage);

        StorehouseWorker storehouseWorker0 = new StorehouseWorker(player0, map);

        Utils.occupyBuilding(storehouseWorker0, storage);

        /* The storage worker rests */
        Utils.fastForward(19, map);

        /* Put planks in the storage */
        storage.putCargo(new Cargo(PLANK, map));

        /* The storage worker delivers stone or planks to the woodcutter */
        assertTrue(storehouseWorker0.isInsideBuilding());

        map.stepTime();

        assertFalse(storehouseWorker0.isInsideBuilding());
        assertEquals(storehouseWorker0.getTarget(), storage.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, storehouseWorker0, storage.getFlag().getPosition());

        assertNull(storehouseWorker0.getCargo());

        /* Let the storage worker go back to the storage */
        assertEquals(storehouseWorker0.getTarget(), storage.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, storehouseWorker0);

        /* Verify that the storage worker stays in the storage and rests */
        for (int i = 0; i < 20; i++) {
            assertTrue(storehouseWorker0.isInsideBuilding());
            map.stepTime();
        }
    }

    @Test
    public void testStorageWorkerGoesBackToStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(8, 8);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        /* Destroy the storage */
        Worker storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters*/
        assertFalse(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(STOREHOUSE_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, headquarter0.getPosition());

        /* Verify that the storage worker is stored correctly in the headquarters*/
        assertEquals(headquarter0.getAmount(STOREHOUSE_WORKER), amount + 1);
    }

    @Test
    public void testStorageWorkerDoesNotGoBackToUnfinishedStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point25 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(17, 17);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        /* Place second storage */
        Point point2 = new Point(15, 15);
        Storehouse storage1 = map.placeBuilding(new Storehouse(player0), point2);

        /* Connect the storage buildings */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), storage1.getFlag());

        /* Destroy the storage */
        Worker storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        /* Verify that the storage worker avoids the second storage because it's burning, although it's close */
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storage1.getPosition());
    }

    @Test
    public void testStorageWorkerDoesNotGoBackToBurningStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point25 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(17, 17);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        /* Place second storage */
        Point point2 = new Point(15, 15);
        Storehouse storage1 = map.placeBuilding(new Storehouse(player0), point2);

        /* Connect the storage buildings */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), storage1.getFlag());

        /* Finish construction of the second storage */
        Utils.constructHouse(storage1);

        /* Destroy the second storage */
        storage1.tearDown();

        /* Destroy the storage */
        Worker storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        /* Verify that the storage worker avoids the second storage because it's burning, although it's close */
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storage1.getPosition());
    }

    @Test
    public void testStorageWorkerDoesNotGoBackToDestroyedStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(17, 17);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        /* Place second storage */
        Point point2 = new Point(15, 15);
        Storehouse storage1 = map.placeBuilding(new Storehouse(player0), point2);

        /* Connect the storage buildings */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), storage1.getFlag());

        /* Finish construction of the second storage */
        Utils.constructHouse(storage1);

        /* Destroy the second storage */
        storage1.tearDown();

        /* Wait for the second storage to burn down */
        Utils.waitForBuildingToBurnDown(storage1);

        /* Destroy the storage */
        Worker storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        /* Verify that the storage worker avoids the second storage because it's destroyed, although it's close */
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storage1.getPosition());
    }

    @Test
    public void testStorageWorkerDoesNotGoBackOffroadToUnfinishedStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(17, 17);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        /* Place second storage */
        Point point2 = new Point(15, 15);
        Storehouse storage1 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the storage */
        Worker storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        /* Verify that the storage worker avoids the second storage because it's burning, although it's close */
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storage1.getPosition());
    }

    @Test
    public void testStorageWorkerDoesNotGoBackOffroadToBurningStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point25 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(17, 17);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        /* Place second storage */
        Point point2 = new Point(15, 15);
        Storehouse storage1 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the second storage */
        Utils.constructHouse(storage1);

        /* Destroy the second storage */
        storage1.tearDown();

        /* Destroy the storage */
        Worker storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        /* Verify that the storage worker avoids the second storage because it's burning, although it's close */
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storage1.getPosition());
    }

    @Test
    public void testStorageWorkerDoesNotGoBackOffroadToDestroyedStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point25 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(17, 17);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        /* Place second storage */
        Point point2 = new Point(15, 15);
        Storehouse storage1 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the second storage */
        Utils.constructHouse(storage1);

        /* Destroy the second storage */
        storage1.tearDown();

        /* Wait for the second storage to burn down */
        Utils.waitForBuildingToBurnDown(storage1);

        /* Destroy the storage */
        Worker storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        /* Verify that the storage worker avoids the second storage because it's destroyed, although it's close */
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storage1.getPosition());
    }

    @Test
    public void testStorageWorkerGoesBackOnToStorageOnRoadsIfPossibleWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(8, 8);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        /* Connect the storage with the headquarters*/
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        /* Destroy the storage */
        Worker storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters*/
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(8, 8);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        /* Connect the storage with the headquarters*/
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        assertTrue(storehouse0.isBurningDown());

        /* Wait for the storage to stop burning */
        Utils.fastForward(50, map);

        assertTrue(storehouse0.isDestroyed());

        /* Wait for the storage to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), storehouse0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(storehouse0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(8, 8);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(storehouse0.getPosition(), storehouse0.getFlag().getPosition()));

        map.removeFlag(storehouse0.getFlag());

        assertNull(map.getRoad(storehouse0.getPosition(), storehouse0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(8, 8);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(storehouse0.getPosition(), storehouse0.getFlag().getPosition()));

        storehouse0.tearDown();

        assertNull(map.getRoad(storehouse0.getPosition(), storehouse0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInStorageCannotBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storage */
        Point point1 = new Point(10, 6);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point1);

        /* Connect the storage and the headquarters*/
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter.getFlag());

        /* Finish the storage */
        Utils.constructHouse(storehouse0);

        /* Assign a worker to the storage */
        StorehouseWorker storehouseWorker = new StorehouseWorker(player0, map);

        Utils.occupyBuilding(storehouseWorker, storehouse0);

        /* Verify that production can't be stopped */
        try {
            storehouse0.stopProduction();

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testProductionInStorageCannotBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storage */
        Point point1 = new Point(10, 6);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point1);

        /* Connect the storage and the headquarters*/
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter.getFlag());

        /* Finish the storage */
        Utils.constructHouse(storehouse0);

        /* Assign a worker to the storage */
        StorehouseWorker storehouseWorker = new StorehouseWorker(player0, map);

        Utils.occupyBuilding(storehouseWorker, storehouse0);

        /* Verify that production can't be resumed */
        try {
            storehouse0.resumeProduction();

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testAssignedStorageWorkerHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarters*/
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storage */
        Point point1 = new Point(20, 14);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point1);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Connect the storage with the headquarters*/
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), storehouse0.getFlag());

        /* Wait for storage worker to get assigned and leave the headquarters*/
        List<StorehouseWorker> workers = Utils.waitForWorkersOutsideBuilding(StorehouseWorker.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        StorehouseWorker worker = workers.getFirst();

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        Player player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters*/
        Point point0 = new Point(7, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 2's headquarters*/
        Point point10 = new Point(70, 70);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        /* Place player 1's headquarters*/
        Point point1 = new Point(37, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 9);
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place storage close to the new border */
        Point point4 = new Point(28, 18);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point4);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Occupy the storage */
        StorehouseWorker worker = Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testRoadCloseToOpponentGetsPopulatedFromCorrectPlayer() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        Player player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters*/
        Point point0 = new Point(13, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters*/
        Point point1 = new Point(45, 17);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place player 2's headquarters*/
        Point point10 = new Point(70, 70);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        /* Clear the inventories of soldiers */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter2, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 9, fortress0);

        /* Connect the fortress with the headquarters*/
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        /* Occupy the road */
        Utils.occupyRoad(road0, map);

        /* Place barracks close to the new border */
        Point point4 = new Point(34, 18);
        Barracks barracks0 = map.placeBuilding(new Barracks(player1), point4);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Connect the barracks with the headquarters */
        Road road1 = map.placeAutoSelectedRoad(player1, headquarter1.getFlag(), barracks0.getFlag());

        /* Occupy the road */
        Utils.occupyRoad(road1, map);

        /* Capture the barracks for player 0 */
        player0.attack(barracks0, 2, AttackStrength.STRONG);

        /* Wait for the attackers to come out */
        List<Soldier> attackers = Utils.waitForWorkersOutsideBuilding(Soldier.class, 2, player0);

        Soldier mainAttacker = Utils.getMainAttacker(barracks0, attackers);

        /* Wait for the attacker to reach the flag of the barracks */
        assertEquals(mainAttacker.getTarget(), barracks0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, mainAttacker, barracks0.getFlag().getPosition());

        /* Wait for player 0 to take over the barracks */
        for (int i = 0; i < 10000; i++) {
            if (barracks0.getPlayer().equals(player0) && barracks0.getNumberOfHostedSoldiers() > 0) {
                break;
            }

            map.stepTime();
        }

        assertEquals(barracks0.getPlayer(), player0);
        assertTrue(barracks0.getNumberOfHostedSoldiers() > 0);

        /* Connect the captured barracks with the headquarters */
        Road road4 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), fortress0.getFlag());

        /* Occupy the road */
        Utils.occupyRoad(road4, map);

        /* Place flag */
        Point point5 = new Point(32, 18);
        Flag flag0 = map.placeFlag(player0, point5);

        /* Place road */
        Road road3 = map.placeAutoSelectedRoad(player0, flag0, barracks0.getFlag());

        /* Verify that player 1's headquartersis closer to the road */
        for (Point point : road3.getWayPoints()) {

            assertTrue(point.distance(headquarter1.getPosition()) < point.distance(headquarter0.getPosition()));
        }

        /* Verify that the barracks gets populated from the right headquartersonly */
        int player0Couriers = Utils.findWorkersOfTypeOutsideForPlayer(Courier.class, player0).size();
        int player1Couriers = Utils.findWorkersOfTypeOutsideForPlayer(Courier.class, player1).size();

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
        assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Courier.class, player0).size(), player0Couriers + 1);
    }

    @Test
    public void testStorageWorkerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place storage */
        Point point2 = new Point(14, 4);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2.upLeft());

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Connect headquartersand first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, storehouse0.getFlag());

        /* Wait for the storage worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(StorehouseWorker.class, 1, player0);

        StorehouseWorker storehouseWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof StorehouseWorker && storehouse0.getPosition().equals(worker.getTarget())) {
                storehouseWorker = (StorehouseWorker) worker;
            }
        }

        assertNotNull(storehouseWorker);
        assertEquals(storehouseWorker.getTarget(), storehouse0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, storehouseWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the storage worker has started walking */
        assertFalse(storehouseWorker.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the storage worker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, storehouseWorker, flag0.getPosition());

        assertEquals(storehouseWorker.getPosition(), flag0.getPosition());

        /* Verify that the storage worker returns to the headquarterswhen it reaches the flag */
        assertEquals(storehouseWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, storehouseWorker, headquarter0.getPosition());
    }

    @Test
    public void testStorageWorkerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place storage */
        Point point2 = new Point(14, 4);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2.upLeft());

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Connect headquartersand first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, storehouse0.getFlag());

        /* Wait for the storage worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(StorehouseWorker.class, 1, player0);

        StorehouseWorker storehouseWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof StorehouseWorker && storehouse0.getPosition().equals(worker.getTarget())) {
                storehouseWorker = (StorehouseWorker) worker;
            }
        }

        assertNotNull(storehouseWorker);
        assertEquals(storehouseWorker.getTarget(), storehouse0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, storehouseWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the storage worker has started walking */
        assertFalse(storehouseWorker.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the storage worker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, storehouseWorker, flag0.getPosition());

        assertEquals(storehouseWorker.getPosition(), flag0.getPosition());

        /* Verify that the storage worker continues to the final flag */
        assertEquals(storehouseWorker.getTarget(), storehouse0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, storehouseWorker, storehouse0.getFlag().getPosition());

        /* Verify that the storage worker goes out to storage instead of going directly back */
        assertNotEquals(storehouseWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testStorageWorkerReturnsToStorageIfStorageIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place storage */
        Point point2 = new Point(14, 4);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2.upLeft());

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Connect headquartersand first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, storehouse0.getFlag());

        /* Wait for the storage worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(StorehouseWorker.class, 1, player0);

        StorehouseWorker storehouseWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof StorehouseWorker && storehouse0.getPosition().equals(worker.getTarget())) {
                storehouseWorker = (StorehouseWorker) worker;
            }
        }

        assertNotNull(storehouseWorker);
        assertEquals(storehouseWorker.getTarget(), storehouse0.getPosition());

        /* Wait for the storage worker to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, storehouseWorker, flag0.getPosition());

        map.stepTime();

        /* See that the storage worker has started walking */
        assertFalse(storehouseWorker.isExactlyAtPoint());

        /* Tear down the storage */
        storehouse0.tearDown();

        /* Verify that the storage worker continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, storehouseWorker, storehouse0.getFlag().getPosition());

        assertEquals(storehouseWorker.getPosition(), storehouse0.getFlag().getPosition());

        /* Verify that the storage worker goes back to storage */
        assertEquals(storehouseWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testStorageWorkerGoesOffroadBackToClosestStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(17, 17);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        /* Place a second storage closer to the storage */
        Point point2 = new Point(13, 13);
        Storehouse storehouse1 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse1);

        /* Destroy the storage */
        Worker storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters*/
        assertFalse(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getTarget(), storehouse1.getPosition());

        int amount = storehouse1.getAmount(STOREHOUSE_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, storehouse1.getPosition());

        /* Verify that the storage worker is stored correctly in the headquarters*/
        assertEquals(storehouse1.getAmount(STOREHOUSE_WORKER), amount + 1);
    }

    @Test
    public void testStorageWorkerReturnsOffroadAndAvoidsBurningStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(17, 17);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        /* Place a second storage closer to the storage */
        Point point2 = new Point(13, 13);
        Storehouse storehouse1 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse1);

        /* Destroy the storage */
        storehouse1.tearDown();

        /* Destroy the storage */
        Worker storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters*/
        assertFalse(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(STOREHOUSE_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, headquarter0.getPosition());

        /* Verify that the storage worker is stored correctly in the headquarters*/
        assertEquals(headquarter0.getAmount(STOREHOUSE_WORKER), amount + 1);
    }

    @Test
    public void testStorageWorkerReturnsOffroadAndAvoidsDestroyedStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(17, 17);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        /* Place a second storage closer to the storage */
        Point point2 = new Point(13, 13);
        Storehouse storehouse1 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse1);

        /* Destroy the storage */
        storehouse1.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse1);

        /* Destroy the storage */
        Worker storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters*/
        assertFalse(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(STOREHOUSE_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, headquarter0.getPosition());

        /* Verify that the storage worker is stored correctly in the headquarters*/
        assertEquals(headquarter0.getAmount(STOREHOUSE_WORKER), amount + 1);
    }

    @Test
    public void testStorageWorkerReturnsOffroadAndAvoidsUnfinishedStorageWhenStorageIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point25 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(17, 17);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Occupy the storage */
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        /* Place a second storage closer to the storage */
        Point point2 = new Point(13, 13);
        Storehouse storehouse1 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the storage */
        Worker storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters*/
        assertFalse(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(STOREHOUSE_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker, headquarter0.getPosition());

        /* Verify that the storage worker is stored correctly in the headquarters*/
        assertEquals(headquarter0.getAmount(STOREHOUSE_WORKER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place storage */
        Point point26 = new Point(17, 17);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        /* Place road to connect the headquartersand the storage */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), storehouse0.getFlag());

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(StorehouseWorker.class, 1, player0).getFirst();

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, storehouse0.getFlag().getPosition());

        /* Tear down the building */
        storehouse0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquartersinstead of entering
        */
        assertEquals(worker.getTarget(), storehouse0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, storehouse0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testStorageCannotProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storage */
        Point point1 = new Point(10, 10);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point1);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Populate the storage */
        Worker storageWorker0 = Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        /* Verify that the storage can produce */
        assertFalse(storehouse0.canProduce());
    }

    @Test
    public void testStorageReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storage */
        Point point1 = new Point(6, 12);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point1);

        /* Construct the storage */
        Utils.constructHouse(storehouse0);

        /* Verify that the reported output is correct */
        assertEquals(storehouse0.getProducedMaterial().length, 0);
    }

    @Test
    public void testStorageReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storage */
        Point point1 = new Point(6, 12);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(storehouse0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(storehouse0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(storehouse0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(storehouse0.getCanHoldAmount(PLANK), 4);
        assertEquals(storehouse0.getCanHoldAmount(STONE), 3);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(storehouse0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testStorageReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storage */
        Point point1 = new Point(6, 12);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point1);

        /* Construct the storage */
        Utils.constructHouse(storehouse0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(storehouse0.getTypesOfMaterialNeeded().size(), 0);

        for (Material material : Material.values()) {
            assertEquals(storehouse0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testStorehouseWaitsWhenFlagIsFull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Make sure there is enough construction material in the headquarters*/
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Place storehouse */
        Point point1 = new Point(16, 6);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Connect the storehouse with the headquarters*/
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Wait for the storehouse to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(storehouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        /* Make sure there is enough construction material in the headquarters */
        Utils.adjustInventoryTo(storehouse, PLANK, 50);
        Utils.adjustInventoryTo(storehouse, STONE, 50);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, storehouse.getFlag(), headquarter);

        /* Block storage of flour to keep the flag filled up */
        storehouse.blockDeliveryOfMaterial(FLOUR);

        /* Remove the road */
        map.removeRoad(road0);

        /* Place fortress */
        Point point2 = new Point(12, 10);
        Fortress fortress = map.placeBuilding(new Fortress(player0), point2);

        /* Connect the fortress with the storehouse */
        Road road1 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), storehouse.getFlag());

        /* Verify that the storehouse waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(storehouse.getFlag().getStackedCargo().size(), 8);
            assertNull(storehouse.getWorker().getCargo());

            map.stepTime();
        }

        /* Remove one of the cargos */
        Cargo cargo = storehouse.getFlag().getStackedCargo().getFirst();
        storehouse.getFlag().retrieveCargo(cargo);

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, storehouse.getWorker());
    }

    @Test
    public void testStorehouseDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Make sure there is enough construction material in the headquarters*/
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Place storehouse */
        Point point1 = new Point(16, 6);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Connect the storehouse with the headquarters*/
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Make sure there is enough construction material in the headquarters*/
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Wait for the storehouse to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(storehouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        /* Make sure there is enough construction material in the storehouse */
        Utils.adjustInventoryTo(storehouse, PLANK, 50);
        Utils.adjustInventoryTo(storehouse, STONE, 50);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, storehouse.getFlag(), headquarter);

        /* Block storage of flour in the storehouse to keep the flag filled up */
        storehouse.blockDeliveryOfMaterial(FLOUR);

        /* Remove the road */
        map.removeRoad(road0);

        /* Place fortress */
        Point point2 = new Point(12, 10);
        Fortress fortress = map.placeBuilding(new Fortress(player0), point2);

        /* Connect the fortress with the storehouse */
        Road road1 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), storehouse.getFlag());

        /* The storehouse waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(storehouse.getFlag().getStackedCargo().size(), 8);
            assertNull(storehouse.getWorker().getCargo());

            map.stepTime();
        }

        /* Remove a cargo from the flag */
        Cargo cargo = storehouse.getFlag().getStackedCargo().getFirst();
        storehouse.getFlag().retrieveCargo(cargo);

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 7);

        /* Wait for the worker to put the cargo on the flag */
        assertTrue(fortress.needsMaterial(PLANK));

        Cargo newCargo = Utils.fastForwardUntilWorkerCarriesCargo(map, storehouse.getWorker());

        assertEquals(storehouse.getWorker().getTarget(), storehouse.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, storehouse.getWorker(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 8);

        /* Verify that the storehouse doesn't produce anything because the flag is full until the courier comes and removes a cargo */
        for (int i = 0; i < 400; i++) {

            if (storehouse.getFlag().getStackedCargo().size() < 8) {
                break;
            }

            assertEquals(storehouse.getFlag().getStackedCargo().size(), 8);
            assertNull(storehouse.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testPushedOutCargoGoesToOtherStorehouse() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(16, 6);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Connect the storehouse with the headquarters*/
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Make sure there is enough construction material in the headquarters*/
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Wait for the storehouse to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(storehouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        /* Push out fish from the headquarters*/
        Utils.adjustInventoryTo(headquarter, FISH, 10);

        headquarter.pushOutAll(FISH);

        /* Verify that all the fish gets transported to the storehouse */
        assertEquals(storehouse.getAmount(FISH), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, headquarter.getWorker(), FISH);

        assertEquals(headquarter.getWorker().getCargo().getMaterial(), FISH);

        Utils.waitForBuildingToGetAmountOfMaterial(headquarter, FISH, 0);

        assertEquals(headquarter.getAmount(FISH), 0);

        Utils.waitForBuildingToGetAmountOfMaterial(storehouse, FISH, 10);

        assertEquals(storehouse.getAmount(FISH), 10);
    }

    @Test
    public void testPushedOutWorkerGoesToOtherStorehouseWhenOwnStoreIsBlocked() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(16, 6);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Connect the storehouse with the headquarters*/
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Make sure there is enough construction material in the headquarters*/
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Wait for the storehouse to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(storehouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        /* Push out fish from the headquarters*/
        Utils.adjustInventoryTo(headquarter, SCOUT, 10);

        headquarter.pushOutAll(SCOUT);
        headquarter.blockDeliveryOfMaterial(SCOUT);

        /* Verify that all the scout goes to the storehouse */
        assertEquals(storehouse.getAmount(SCOUT), 0);

        Worker scout = Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0).getFirst();

        assertEquals(scout.getPosition(), headquarter.getPosition());
        assertNull(headquarter.getWorker().getCargo());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, headquarter.getFlag().getPosition());

        assertEquals(scout.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(scout));

        Utils.waitForBuildingToGetAmountOfMaterial(headquarter, SCOUT, 0);

        assertEquals(headquarter.getAmount(SCOUT), 0);

        Utils.waitForBuildingToGetAmountOfMaterial(storehouse, SCOUT, 10);

        assertEquals(storehouse.getAmount(SCOUT), 10);
    }

    @Test
    public void testPushedOutMaterialFollowsPriorityOrder() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(16, 6);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Connect the storehouse with the headquarters*/
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Make sure there is enough construction material in the headquarters*/
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Wait for the storehouse to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(storehouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        /* Push out fish from the headquarters*/
        Utils.adjustInventoryTo(headquarter, FISH, 10);
        Utils.adjustInventoryTo(headquarter, COIN, 10);

        headquarter.pushOutAll(FISH);
        headquarter.pushOutAll(COIN);

        /* Set transport priority for fish above coin */
        player0.setTransportPriority(0, TransportCategory.FOOD);
        player0.setTransportPriority(1, TransportCategory.COIN);

        /* Verify that all the fish gets transported to the storehouse before the coins */
        assertEquals(storehouse.getAmount(FISH), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, headquarter.getWorker(), FISH);

        assertEquals(headquarter.getWorker().getCargo().getMaterial(), FISH);

        Utils.waitForBuildingToGetAmountOfMaterial(headquarter, FISH, 0);

        assertEquals(headquarter.getAmount(FISH), 0);
        assertEquals(headquarter.getAmount(COIN), 10);
    }

    @Test
    public void testDeliveriesGoToOtherStorehouseWhenDeliveryIsBlocked() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(16, 6);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place well */
        Point point2 = new Point(9, 7);
        Well well = map.placeBuilding(new Well(player0), point2);

        /* Make sure there is enough construction material in the headquarters*/
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Connect the storehouse with the headquarters*/
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Wait for the storehouse to get constructed */
        Utils.waitForBuildingToBeConstructed(storehouse);

        /* Connect the well with the headquarters*/
        Road road1 = map.placeAutoSelectedRoad(player0, well.getFlag(), headquarter.getFlag());

        /* Wait for the well to get constructed */
        Utils.waitForBuildingToBeConstructed(well);

        Utils.waitForNonMilitaryBuildingToGetPopulated(well);

        /* Verify that when delivery is blocked for water in the headquarter,
           all deliveries from the well go to the storehouse even if it's further away
        */
        assertTrue(well.isReady());
        assertNotNull(well.getWorker());

        headquarter.blockDeliveryOfMaterial(WATER);

        Utils.adjustInventoryTo(storehouse, WATER, 0);

        for (int i = 0; i < 10; i++) {

            /* Wait for the well worker to produce a water cargo */
            Cargo cargo = Utils.fastForwardUntilWorkerCarriesCargo(map, well.getWorker(), WATER);

            /* Wait for the courier for the road between the well and the headquartersto pick up the water cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, road1.getCourier(), cargo);

            assertEquals(road1.getCourier().getTarget(), headquarter.getFlag().getPosition());

            /* Verify that the cargo is put on the headquarter's flag and picked up by the second courier,
               instead of delivered to the headquarter
             */
            Utils.fastForwardUntilWorkerReachesPoint(map, road1.getCourier(), headquarter.getFlag().getPosition());

            assertTrue(headquarter.getFlag().getStackedCargo().contains(cargo));

            Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier(), cargo);

            assertEquals(road0.getCourier().getTarget(), storehouse.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getPosition());

            assertNull(road0.getCourier().getCargo());
            assertEquals(storehouse.getAmount(WATER), i + 1);
        }
    }

    @Test
    public void testPushedOutMaterialStopsWhenFlagFillsUp() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters*/
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Make sure there is enough construction material in the headquarters */
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Verify that pushing out planks will fill up the flag and then stop */
        assertEquals(headquarter.getFlag().getStackedCargo().size(), 0);

        headquarter.pushOutAll(PLANK);
        headquarter.blockDeliveryOfMaterial(PLANK);

        Utils.waitForFlagToGetStackedCargo(map, headquarter.getFlag(), 8);

        assertEquals(headquarter.getFlag().getStackedCargo().size(), 8);
        assertEquals(headquarter.getWorker().getTarget(), headquarter.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, headquarter.getWorker(), headquarter.getPosition());

        for (int i = 0; i < 200; i++) {
            assertTrue(headquarter.getWorker().isInsideBuilding());
            assertNull(headquarter.getWorker().getCargo());

            map.stepTime();
        }
    }
    @Test
    public void testStorehouseWorkerReturnsCargoIfItIsStillOnTheFlagAndItsTargetBecomesUnreachable() throws InvalidUserActionException {

        /* Creating new game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(5, 7);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 6);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place flag */
        Point point2 = new Point(14, 8);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place road between the headquarters and the flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Place road between the flag and the woodcutter */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Wait for the first road to get assigned a courier */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition().left());

        assertEquals(courier.getPosition(), flag0.getPosition().left());

        /* Set the amount of planks */
        Utils.adjustInventoryTo(headquarter0, PLANK, 20);

        /* Place woodcutter */
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point2.upLeft());

        /* Wait for the courier to start walking to the headquarters' flag to pick up a cargo for the woodcutter */
        Utils.waitForWorkerToSetTarget(map, courier, headquarter0.getFlag().getPosition());

        assertEquals(headquarter0.getAmount(PLANK), 19);
        assertNull(courier.getCargo());

        map.stepTime();

        /* Remove the second road */
        map.removeRoad(road1);

        /* Verify that the storehouse worker brings the cargo back to the storehouse */
        StorehouseWorker storehouseWorker = (StorehouseWorker) headquarter0.getWorker();

        Utils.waitForWorkerToSetTarget(map, storehouseWorker, headquarter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, storehouseWorker, headquarter0.getFlag().getPosition());

        assertNotNull(storehouseWorker.getCargo());
        assertEquals(storehouseWorker.getCargo().getMaterial(), PLANK);
        assertEquals(storehouseWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, storehouseWorker, headquarter0.getPosition());

        assertNull(storehouseWorker.getCargo());
        assertEquals(headquarter0.getAmount(PLANK), 20);
    }
}
