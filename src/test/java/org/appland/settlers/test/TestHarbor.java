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
import org.appland.settlers.model.DetailedVegetation;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Harbor;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Scout;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.StorageWorker;
import org.appland.settlers.model.Storehouse;
import org.appland.settlers.model.TransportCategory;
import org.appland.settlers.model.Well;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.SCOUT;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.STORAGE_WORKER;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author johan
 */
public class TestHarbor {

    /*
    TODO:
      - where harbor can be placed
      - start expedition (in separate file?)
      - harbor is large building
     */

    @Test
    public void testCanMarkAvailablePlaceForHarborOnMap() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Verify that it's possible to mark that it's possible to place a harbor at a point on the map */
        Point point0 = new Point(10, 10);

        assertFalse(map.isAvailableHarborPoint(point0));

        map.setPossiblePlaceForHarbor(point0);

        assertTrue(map.isAvailableHarborPoint(point0));
    }

    @Test
    public void testCannotPlaceHarborWithoutMarkingFirst() throws InvalidEndPointException, InvalidRouteException, InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to place a harbor on a point that hasn't been marked */
        Point point1 = new Point(10, 8);

        assertFalse(map.isAvailableHarborPoint(point1));

        try {
            Harbor harbor0 = map.placeBuilding(new Harbor(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
        assertNull(map.getBuildingAtPoint(point1));
        assertFalse(map.isAvailableHarborPoint(point1));
    }

    @Test
    public void testHarborOnlyNeedsFourPlanksAndSixStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(12, 12);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 12);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Point point3 = new Point(6, 12);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point3);

        /* Deliver four plank and three stone */
        Utils.deliverCargos(harbor0, PLANK, 4);
        Utils.deliverCargos(harbor0, STONE, 6);

        /* Assign builder */
        Utils.assignBuilder(harbor0);

        /* Verify that this is enough to construct the harbor */
        for (int i = 0; i < 150; i++) {
            assertTrue(harbor0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(harbor0.isReady());
    }

    @Test
    public void testHarborCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(12, 12);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 12);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Point point3 = new Point(6, 12);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point3);

        /* Deliver four plank and three stone */
        Utils.deliverCargos(harbor0, PLANK, 3);
        Utils.deliverCargos(harbor0, STONE, 6);

        /* Assign builder */
        Utils.assignBuilder(harbor0);

        /* Verify that this is not enough to construct the harbor */
        for (int i = 0; i < 500; i++) {
            assertTrue(harbor0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(harbor0.isReady());
    }

    @Test
    public void testHarborCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(12, 12);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 12);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Point point3 = new Point(6, 12);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point3);

        /* Deliver four plank and three stone */
        Utils.deliverCargos(harbor0, PLANK, 4);
        Utils.deliverCargos(harbor0, STONE, 5);

        /* Assign builder */
        Utils.assignBuilder(harbor0);

        /* Verify that this is not enough to construct the harbor */
        for (int i = 0; i < 500; i++) {
            assertTrue(harbor0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(harbor0.isReady());
    }

    @Test
    public void testHarborIsConstructedWithRequiredResources() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(12, 12);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 12);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Point point3 = new Point(6, 12);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point3);

        /* Deliver four plank and three stone */
        Utils.deliverCargos(harbor0, PLANK, 4);
        Utils.deliverCargos(harbor0, STONE, 6);

        /* Assign builder */
        Utils.assignBuilder(harbor0);

        /* Verify that this is not enough to construct the harbor */
        for (int i = 0; i < 1000; i++) {

            if (harbor0.isReady()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(harbor0.isReady());
    }

    @Test
    public void testUnfinishedHarborNotNeedsWorker() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(13, 9);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(7, 9);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Point point3 = new Point(7, 9);
        Harbor harbor = map.placeBuilding(new Harbor(player0), point3);

        /* Verify that an unfinished harbor doesn't need a worker */
        assertFalse(harbor.needsWorker());
    }

    @Test
    public void testHarborNeedsWorker() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(13, 9);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(7, 9);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Point point3 = new Point(7, 9);
        Harbor harbor = map.placeBuilding(new Harbor(player0), point3);

        /* Connect the harbor with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed */
        Utils.waitForBuildingToBeConstructed(harbor);

        assertTrue(harbor.isReady());

        /* Verify that the finished harbor needs a worker */
        assertTrue(harbor.needsWorker());
    }

    @Test
    public void testStorageWorkerGetsAssignedToFinishedHarbor() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(13, 9);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(7, 9);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Point point3 = new Point(7, 9);
        Harbor harbor = map.placeBuilding(new Harbor(player0), point3);

        /* Connect the harbor with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Finish construction of the harbor */
        Utils.waitForBuildingToBeConstructed(harbor);

        /* Run game logic once to let the headquarter assign a harbor worker to the harbor */
        StorageWorker harborWorker = Utils.waitForWorkerOutsideBuilding(StorageWorker.class, player0);

        assertNotNull(harborWorker);
        assertEquals(harborWorker.getTarget(), harbor.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker, harbor.getPosition());

        assertTrue(harborWorker.isInsideBuilding());
        assertEquals(harbor.getWorker(), harborWorker);
    }

    @Test
    public void testStorageWorkerIsNotASoldier() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(13, 9);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(7, 9);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Point point3 = new Point(7, 9);
        Harbor harbor = map.placeBuilding(new Harbor(player0), point3);

        /* Connect the harbor with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed */
        Utils.waitForBuildingToBeConstructed(harbor);

        /* Run game logic once to let the headquarter assign a harbor worker to the harbor */
        StorageWorker harborWorker = Utils.waitForWorkerOutsideBuilding(StorageWorker.class, player0);

        assertNotNull(harborWorker);
        assertEquals(harborWorker.getTarget(), harbor.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker, harbor.getPosition());

        assertTrue(harborWorker.isInsideBuilding());
        assertEquals(harbor.getWorker(), harborWorker);
    }

    @Test
    public void testStorageWorkerRests() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(13, 9);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(7, 9);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Point point3 = new Point(7, 9);
        Harbor harbor = map.placeBuilding(new Harbor(player0), point3);

        /* Connect the harbor with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed */
        Utils.waitForBuildingToBeConstructed(harbor);

        /* Wait for a storage worker to start walking to the harbor */
        StorageWorker harborWorker0 = Utils.waitForWorkerOutsideBuilding(StorageWorker.class, player0);

        /* Wait for the storage worker to reach the harbor */
        assertEquals(harborWorker0.getTarget(), harbor.getPosition());
        assertFalse(harborWorker0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker0, harbor.getPosition());

        /* Verify that the harbor worker rests */
        for (int i = 0; i < 50; i++) {
            assertTrue(harborWorker0.isInsideBuilding());
            map.stepTime();
        }
    }

    @Test
    public void testStorageWorkerRestsThenDeliversCargo() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(12, 12);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 10);
        map.setPossiblePlaceForHarbor(point1);

        /* Place a lake */
        Point point2 = new Point(13, 9);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point3 = new Point(7, 9);
        map.setPossiblePlaceForHarbor(point3);

        /* Place headquarter */
        Point point4 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point4);

        /* Place woodcutter */
        Point point5 = new Point(11, 9);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point5.upLeft());

        /* Place harbor */
        Point point6 = new Point(7, 9);
        Harbor harbor = map.placeBuilding(new Harbor(player0), point6.upLeft());

        /* Connect the harbor with the woodcutter */
        Point point7 = new Point(9, 9);
        map.placeRoad(player0, point5, point7, point6);

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor);

        /* Occupy the harbor worker */
        StorageWorker harborWorker0 = new StorageWorker(player0, map);
        Utils.occupyBuilding(harborWorker0, harbor);

        /* The harbor worker rests */
        Utils.fastForward(19, map);

        /* Put planks in the harbor */
        harbor.putCargo(new Cargo(PLANK, map));

        /* The harbor worker delivers stone or planks to the woodcutter */
        assertTrue(harborWorker0.isInsideBuilding());

        map.stepTime();

        assertFalse(harborWorker0.isInsideBuilding());
        assertNotNull(harborWorker0.getCargo());
        assertEquals(harborWorker0.getTarget(), harbor.getFlag().getPosition());
        assertTrue(harbor.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker0, harbor.getFlag().getPosition());

        assertNull(harborWorker0.getCargo());
        assertFalse(harbor.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testStorageWorkerGoesBackToHarborAfterDelivery() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(10, 14);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 10);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place woodcutter */
        Point point3 = new Point(11, 9);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point3.upLeft());

        /* Place harbor */
        Point point4 = new Point(7, 9);
        Harbor harbor = map.placeBuilding(new Harbor(player0), point4.upLeft());

        /* Connect the harbor with woodcutter */
        Point point5 = new Point(9, 9);
        map.placeRoad(player0, point3, point5, point4);

        /* Construct the harbor */
        Utils.constructHouse(harbor);

        /* Occupy the harbor */
        StorageWorker harborWorker0 = new StorageWorker(player0, map);

        Utils.occupyBuilding(harborWorker0, harbor);

        /* The harbor worker rests */
        Utils.fastForward(19, map);

        /* Put planks in the harbor */
        harbor.putCargo(new Cargo(PLANK, map));

        /* The harbor worker delivers stone or planks to the woodcutter */
        assertTrue(harborWorker0.isInsideBuilding());

        map.stepTime();

        assertFalse(harborWorker0.isInsideBuilding());
        assertNotNull(harborWorker0.getCargo());
        assertEquals(harborWorker0.getTarget(), harbor.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker0, harbor.getFlag().getPosition());

        /* Verify that the harbor worker goes back to the harbor */
        assertEquals(harborWorker0.getTarget(), harbor.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, harborWorker0);

        assertTrue(harborWorker0.isInsideBuilding());
    }

    @Test
    public void testStorageWorkerRestsInHarborAfterDelivery() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(10, 14);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 10);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place woodcutter */
        Point point3 = new Point(11, 9);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point3.upLeft());

        /* Place harbor */
        Point point4 = new Point(7, 9);
        Harbor harbor = map.placeBuilding(new Harbor(player0), point4.upLeft());

        /* Connect the harbor with the woodcutter */
        Point point5 = new Point(9, 9);
        map.placeRoad(player0, point3, point5, point4);

        /* Construct the harbor */
        Utils.constructHouse(harbor);

        /* Occupy the harbor */
        StorageWorker harborWorker0 = new StorageWorker(player0, map);

        Utils.occupyBuilding(harborWorker0, harbor);

        /* The harbor worker rests */
        Utils.fastForward(19, map);

        /* Put planks in the harbor */
        harbor.putCargo(new Cargo(PLANK, map));

        /* The harbor worker delivers stone or planks to the woodcutter */
        assertTrue(harborWorker0.isInsideBuilding());

        map.stepTime();

        assertFalse(harborWorker0.isInsideBuilding());
        assertEquals(harborWorker0.getTarget(), harbor.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker0, harbor.getFlag().getPosition());

        assertNull(harborWorker0.getCargo());

        /* Let the harbor worker go back to the harbor */
        assertEquals(harborWorker0.getTarget(), harbor.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, harborWorker0);

        /* Verify that the harbor worker stays in the harbor and rests */
        for (int i = 0; i < 20; i++) {
            assertTrue(harborWorker0.isInsideBuilding());
            map.stepTime();
        }
    }

    @Test
    public void testStorageWorkerGoesBackToHarborWhenHarborIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(12, 8);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(8, 8);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Point point3 = new Point(8, 8);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point3);

        /* Connect the harbor with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor0.getFlag(), headquarter0.getFlag());

        /* Wait for the harbor to get constructed and occupied*/
        Utils.waitForBuildingToBeConstructed(harbor0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor0);

        /* Destroy the harbor */
        Worker harborWorker = harbor0.getWorker();

        assertTrue(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getPosition(), harbor0.getPosition());

        harbor0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(STORAGE_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker, headquarter0.getPosition());

        /* Verify that the harbor worker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(STORAGE_WORKER), amount + 1);
    }

    @Test
    public void testStorageWorkerDoesNotGoBackToUnfinishedStorageWhenHarborIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(21, 17);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(17, 17);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Point point3 = new Point(17, 17);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point3);

        /* Connect the harbor with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor0.getFlag(), headquarter0.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor0);

        /* Place storehouse */
        Point point4 = new Point(15, 15);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point4);

        /* Connect the harbor buildings */
        Road road1 = map.placeAutoSelectedRoad(player0, harbor0.getFlag(), storehouse0.getFlag());

        /* Destroy the harbor */
        Worker harborWorker = harbor0.getWorker();

        assertTrue(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getPosition(), harbor0.getPosition());

        harbor0.tearDown();

        /* Verify that the harbor worker avoids the second harbor because it's burning, although it's close */
        assertFalse(harborWorker.isInsideBuilding());
        assertNotEquals(harborWorker.getTarget(), storehouse0.getPosition());
    }

    @Test
    public void testStorageWorkerDoesNotGoBackToBurningStorehouseWhenHarborIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(21, 17);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(17, 17);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Point point3 = new Point(17, 17);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point3);

        /* Connect the harbor with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor0.getFlag(), headquarter0.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor0);

        /* Place storehouse */
        Point point4 = new Point(15, 15);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Connect the harbor with the storehouse */
        Road road1 = map.placeAutoSelectedRoad(player0, harbor0.getFlag(), storehouse.getFlag());

        /* Finish construction of the storehouse */
        Utils.constructHouse(storehouse);

        /* Destroy the storehouse */
        storehouse.tearDown();

        /* Destroy the harbor */
        Worker harborWorker = harbor0.getWorker();

        assertTrue(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getPosition(), harbor0.getPosition());

        harbor0.tearDown();

        /* Verify that the harbor worker avoids the second harbor because it's burning, although it's close */
        assertFalse(harborWorker.isInsideBuilding());
        assertNotEquals(harborWorker.getTarget(), storehouse.getPosition());
    }

    @Test
    public void testStorageWorkerDoesNotGoBackToDestroyedStorehouseWhenHarborIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(21, 17);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(17, 17);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Point point3 = new Point(17, 17);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point3);

        /* Connect the harbor with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor0.getFlag(), headquarter0.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor0);

        /* Place storehouse */
        Point point4 = new Point(15, 15);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Connect the harbor and the storehouse */
        Road road1 = map.placeAutoSelectedRoad(player0, harbor0.getFlag(), storehouse.getFlag());

        /* Finish construction of the storehouse */
        Utils.constructHouse(storehouse);

        /* Destroy the storehouse */
        storehouse.tearDown();

        /* Wait for the second harbor to burn down */
        Utils.waitForBuildingToBurnDown(storehouse);

        /* Destroy the harbor */
        Worker harborWorker = harbor0.getWorker();

        assertTrue(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getPosition(), harbor0.getPosition());

        harbor0.tearDown();

        /* Verify that the harbor worker avoids the storehouse because it's destroyed, although it's close */
        assertFalse(harborWorker.isInsideBuilding());
        assertNotEquals(harborWorker.getTarget(), storehouse.getPosition());
    }

    @Test
    public void testStorageWorkerDoesNotGoBackOffroadToUnfinishedStorehouseWhenHarborIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(21, 17);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(17, 17);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Point point3 = new Point(17, 17);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point3);

        /* Connect the harbor with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor0.getFlag(), headquarter0.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor0);

        /* Remove the road */
        map.removeRoad(road0);

        /* Place storehouse */
        Point point4 = new Point(15, 15);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Destroy the harbor */
        Worker harborWorker = harbor0.getWorker();

        assertTrue(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getPosition(), harbor0.getPosition());

        harbor0.tearDown();

        /* Verify that the harbor worker avoids the storehouse because it's unfinished, although it's close */
        assertFalse(harborWorker.isInsideBuilding());
        assertNotEquals(harborWorker.getTarget(), storehouse.getPosition());
    }

    @Test
    public void testStorageWorkerDoesNotGoBackOffroadToBurningStorehouseWhenHarborIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(21, 17);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(17, 17);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point1);

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Occupy the harbor */
        Utils.occupyBuilding(new StorageWorker(player0, map), harbor0);

        /* Place storehouse */
        Point point3 = new Point(15, 15);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point3);

        /* Finish construction of the storehouse */
        Utils.constructHouse(storehouse0);

        /* Destroy the storehouse */
        storehouse0.tearDown();

        /* Destroy the harbor */
        Worker harborWorker = harbor0.getWorker();

        assertTrue(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getPosition(), harbor0.getPosition());

        harbor0.tearDown();

        /* Verify that the harbor worker avoids the burning storehouse because it's burning, although it's close */
        assertFalse(harborWorker.isInsideBuilding());
        assertNotEquals(harborWorker.getTarget(), storehouse0.getPosition());
    }

    @Test
    public void testStorageWorkerDoesNotGoBackOffroadToDestroyedStorehouseWhenHarborIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(21, 17);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(17, 17);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point25 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place harbor */
        Point point26 = new Point(17, 17);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point26);

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Occupy the harbor */
        Utils.occupyBuilding(new StorageWorker(player0, map), harbor0);

        /* Place storehouse */
        Point point2 = new Point(15, 15);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storehouse */
        Utils.constructHouse(storehouse);

        /* Destroy the storehouse */
        storehouse.tearDown();

        /* Wait for the storehouse to burn down */
        Utils.waitForBuildingToBurnDown(storehouse);

        /* Destroy the harbor */
        Worker harborWorker = harbor0.getWorker();

        assertTrue(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getPosition(), harbor0.getPosition());

        harbor0.tearDown();

        /* Verify that the harbor worker avoids the storehouse because it's destroyed, although it's close */
        assertFalse(harborWorker.isInsideBuilding());
        assertNotEquals(harborWorker.getTarget(), storehouse.getPosition());
    }

    @Test
    public void testStorageWorkerGoesBackOnToHarborOnRoadsIfPossibleWhenHarborIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(12, 8);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(8, 8);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place harbor */
        Point point26 = new Point(8, 8);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point26);

        /* Connect the harbor with the headquarter */
        map.placeAutoSelectedRoad(player0, harbor0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Occupy the harbor */
        Utils.occupyBuilding(new StorageWorker(player0, map), harbor0);

        /* Destroy the harbor */
        Worker harborWorker = harbor0.getWorker();

        assertTrue(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getPosition(), harbor0.getPosition());

        harbor0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point p : harborWorker.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test
    public void testDestroyedHarborIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(12, 8);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(8, 8);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place harbor */
        Point point26 = new Point(8, 8);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point26);

        /* Connect the harbor with the headquarter */
        map.placeAutoSelectedRoad(player0, harbor0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Destroy the harbor */
        harbor0.tearDown();

        assertTrue(harbor0.isBurningDown());

        /* Wait for the harbor to stop burning */
        Utils.fastForward(50, map);

        assertTrue(harbor0.isDestroyed());

        /* Wait for the harbor to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), harbor0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(harbor0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(12, 8);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(8, 8);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place harbor */
        Point point26 = new Point(8, 8);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point26);

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(harbor0.getPosition(), harbor0.getFlag().getPosition()));

        map.removeFlag(harbor0.getFlag());

        assertNull(map.getRoad(harbor0.getPosition(), harbor0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(12, 8);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(8, 8);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place harbor */
        Point point26 = new Point(8, 8);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point26);

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(harbor0.getPosition(), harbor0.getFlag().getPosition()));

        harbor0.tearDown();

        assertNull(map.getRoad(harbor0.getPosition(), harbor0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInHarborCannotBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a lake */
        Point point0 = new Point(14, 6);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(10, 6);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Point point3 = new Point(10, 6);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point3);

        /* Connect the harbor and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor0.getFlag(), headquarter.getFlag());

        /* Finish the harbor */
        Utils.constructHouse(harbor0);

        /* Assign a worker to the harbor */
        StorageWorker harborWorker = new StorageWorker(player0, map);

        Utils.occupyBuilding(harborWorker, harbor0);

        /* Verify that production can't be stopped */
        try {
            harbor0.stopProduction();

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testProductionInHarborCannotBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a lake */
        Point point0 = new Point(14, 6);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(10, 6);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor0.getFlag(), headquarter.getFlag());

        /* Finish the harbor */
        Utils.constructHouse(harbor0);

        /* Assign a worker to the harbor */
        StorageWorker harborWorker = new StorageWorker(player0, map);

        Utils.occupyBuilding(harborWorker, harbor0);

        /* Verify that production can't be resumed */
        try {
            harbor0.resumeProduction();

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testAssignedStorageWorkerHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place a lake */
        Point point0 = new Point(14, 6);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(20, 14);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point1);

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Connect the harbor with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), harbor0.getFlag());

        /* Wait for harbor worker to get assigned and leave the headquarter */
        List<StorageWorker> workers = Utils.waitForWorkersOutsideBuilding(StorageWorker.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        StorageWorker worker = workers.get(0);

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnHarborEvenWithoutRoadsAndEnemiesStorehouseIsCloser() throws Exception {

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

        /* Place a lake */
        Point pointX = new Point(32, 18);
        Utils.surroundPointWithDetailedVegetation(pointX, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point pointY = new Point(28, 18);
        map.setPossiblePlaceForHarbor(pointY);

        /* Place player 0's headquarter */
        Point point0 = new Point(7, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 2's headquarter */
        Point point10 = new Point(70, 70);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 9);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place harbor close to the new border */
        Point point4 = new Point(28, 18);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point4);

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Occupy the harbor */
        StorageWorker worker = Utils.occupyBuilding(new StorageWorker(player0, map), harbor0);

        /* Verify that the worker goes back to its own harbor when the fortress is torn down */
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
        Point point0 = new Point(13, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(45, 17);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place player 2's headquarter */
        Point point10 = new Point(70, 70);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 9, fortress0);

        /* Connect the fortress with the headquarter */
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(17, 5);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(13, 5);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place first flag */
        Point point3 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point3);

        /* Place harbor */
        Point point4 = new Point(14, 4);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point4.upLeft());

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, harbor0.getFlag());

        /* Wait for the harbor worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(StorageWorker.class, 1, player0);

        StorageWorker harborWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof StorageWorker && harbor0.getPosition().equals(worker.getTarget())) {
                harborWorker = (StorageWorker) worker;
            }
        }

        assertNotNull(harborWorker);
        assertEquals(harborWorker.getTarget(), harbor0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the harbor worker has started walking */
        assertFalse(harborWorker.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the harbor worker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker, flag0.getPosition());

        assertEquals(harborWorker.getPosition(), flag0.getPosition());

        /* Verify that the harbor worker returns to the headquarter when it reaches the flag */
        assertEquals(harborWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker, headquarter0.getPosition());
    }

    @Test
    public void testStorageWorkerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(17, 5);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(13, 5);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place first flag */
        Point point3 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point3);

        /* Place harbor */
        Point point4 = new Point(14, 4);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point4.upLeft());

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, harbor0.getFlag());

        /* Wait for the harbor worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(StorageWorker.class, 1, player0);

        StorageWorker harborWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof StorageWorker && harbor0.getPosition().equals(worker.getTarget())) {
                harborWorker = (StorageWorker) worker;
            }
        }

        assertNotNull(harborWorker);
        assertEquals(harborWorker.getTarget(), harbor0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the harbor worker has started walking */
        assertFalse(harborWorker.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the harbor worker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker, flag0.getPosition());

        assertEquals(harborWorker.getPosition(), flag0.getPosition());

        /* Verify that the harbor worker continues to the final flag */
        assertEquals(harborWorker.getTarget(), harbor0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker, harbor0.getFlag().getPosition());

        /* Verify that the harbor worker goes out to harbor instead of going directly back */
        assertNotEquals(harborWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testStorageWorkerReturnsToHarborIfHarborIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(17, 5);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(13, 5);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place first flag */
        Point point3 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point3);

        /* Place harbor */
        Point point4 = new Point(14, 4);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point4.upLeft());

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, harbor0.getFlag());

        /* Wait for the harbor worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(StorageWorker.class, 1, player0);

        StorageWorker harborWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof StorageWorker && harbor0.getPosition().equals(worker.getTarget())) {
                harborWorker = (StorageWorker) worker;
            }
        }

        assertNotNull(harborWorker);
        assertEquals(harborWorker.getTarget(), harbor0.getPosition());

        /* Wait for the harbor worker to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker, flag0.getPosition());

        map.stepTime();

        /* See that the harbor worker has started walking */
        assertFalse(harborWorker.isExactlyAtPoint());

        /* Tear down the harbor */
        harbor0.tearDown();

        /* Verify that the harbor worker continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker, harbor0.getFlag().getPosition());

        assertEquals(harborWorker.getPosition(), harbor0.getFlag().getPosition());

        /* Verify that the harbor worker goes back to harbor */
        assertEquals(harborWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testStorageWorkerGoesOffroadBackToClosestStorehouseWhenHarborIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(21, 13);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(17, 13);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place harbor */
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point1);

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Occupy the harbor */
        Utils.occupyBuilding(new StorageWorker(player0, map), harbor0);

        /* Place a storehouse closer to the harbor */
        Point point2 = new Point(13, 13);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storehouse */
        Utils.constructHouse(storehouse);

        /* Destroy the harbor */
        Worker harborWorker = harbor0.getWorker();

        assertTrue(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getPosition(), harbor0.getPosition());

        harbor0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getTarget(), storehouse.getPosition());

        int amount = storehouse.getAmount(STORAGE_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker, storehouse.getPosition());

        /* Verify that the harbor worker is stored correctly in the headquarter */
        assertEquals(storehouse.getAmount(STORAGE_WORKER), amount + 1);
    }

    @Test
    public void testStorageWorkerReturnsOffroadAndAvoidsBurningStorehouseWhenHarborIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(21, 17);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(17, 17);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place harbor */
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point1);

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Occupy the harbor */
        Utils.occupyBuilding(new StorageWorker(player0, map), harbor0);

        /* Place a storehouse closer to the harbor */
        Point point2 = new Point(13, 13);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storehouse */
        Utils.constructHouse(storehouse);

        /* Destroy the storehouse */
        storehouse.tearDown();

        /* Destroy the harbor */
        Worker harborWorker = harbor0.getWorker();

        assertTrue(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getPosition(), harbor0.getPosition());

        harbor0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(STORAGE_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker, headquarter0.getPosition());

        /* Verify that the harbor worker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(STORAGE_WORKER), amount + 1);
    }

    @Test
    public void testStorageWorkerReturnsOffroadAndAvoidsDestroyedStorehouseWhenHarborIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(21, 17);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(17, 17);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place harbor */
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point1);

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Occupy the harbor */
        Utils.occupyBuilding(new StorageWorker(player0, map), harbor0);

        /* Place a storehouse closer to the harbor */
        Point point2 = new Point(13, 13);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storehouse */
        Utils.constructHouse(storehouse);

        /* Destroy the storehouse */
        storehouse.tearDown();

        /* Wait for the harbor to burn down */
        Utils.waitForBuildingToBurnDown(storehouse);

        /* Destroy the harbor */
        Worker harborWorker = harbor0.getWorker();

        assertTrue(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getPosition(), harbor0.getPosition());

        harbor0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(STORAGE_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker, headquarter0.getPosition());

        /* Verify that the harbor worker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(STORAGE_WORKER), amount + 1);
    }

    @Test
    public void testStorageWorkerReturnsOffroadAndAvoidsUnfinishedStorehouseWhenHarborIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(29, 9);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(23, 9);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        assertEquals(map.isAvailableHousePoint(player0, point1), Size.LARGE);
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point1);

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Occupy the harbor */
        Utils.occupyBuilding(new StorageWorker(player0, map), harbor0);

        /* Place a storehouse closer to the harbor */
        Point point3 = new Point(19, 9);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point3);

        /* Destroy the harbor */
        Worker harborWorker = harbor0.getWorker();

        assertTrue(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getPosition(), harbor0.getPosition());

        harbor0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(harborWorker.isInsideBuilding());
        assertEquals(harborWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(STORAGE_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, harborWorker, headquarter0.getPosition());

        /* Verify that the harbor worker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(STORAGE_WORKER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(21, 17);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(17, 17);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place harbor */
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point1);

        /* Place road to connect the headquarter and the harbor */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), harbor0.getFlag());

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(StorageWorker.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, harbor0.getFlag().getPosition());

        /* Tear down the building */
        harbor0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquarter instead of entering
        */
        assertEquals(worker.getTarget(), harbor0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, harbor0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testHarborCannotProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(14, 10);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(10, 10);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point1);

        /* Finish construction of the harbor */
        Utils.constructHouse(harbor0);

        /* Populate the harbor */
        Worker harborWorker0 = Utils.occupyBuilding(new StorageWorker(player0, map), harbor0);

        /* Verify that the harbor can produce */
        assertFalse(harbor0.canProduce());
    }

    @Test
    public void testHarborReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(10, 12);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 12);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point1);

        /* Construct the harbor */
        Utils.constructHouse(harbor0);

        /* Verify that the reported output is correct */
        assertEquals(harbor0.getProducedMaterial().length, 0);
    }

    @Test
    public void testHarborReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(10, 12);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 12);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(harbor0.getMaterialNeeded().size(), 2);
        assertTrue(harbor0.getMaterialNeeded().contains(PLANK));
        assertTrue(harbor0.getMaterialNeeded().contains(STONE));
        assertEquals(harbor0.getTotalAmountNeeded(PLANK), 4);
        assertEquals(harbor0.getTotalAmountNeeded(STONE), 6);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(harbor0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testHarborReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(10, 12);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 12);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point1);

        /* Construct the harbor */
        Utils.constructHouse(harbor0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(harbor0.getMaterialNeeded().size(), 0);

        for (Material material : Material.values()) {
            assertEquals(harbor0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testHarborWaitsWhenFlagIsFull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place a lake */
        Point point0 = new Point(20, 6);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(16, 6);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Make sure there is enough construction material in the headquarter */
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(harbor);
        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Make sure there is enough construction material in the headquarter */
        Utils.adjustInventoryTo(harbor, PLANK, 50);
        Utils.adjustInventoryTo(harbor, STONE, 50);
        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, harbor.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Place fortress */
        Point point3 = new Point(12, 10);
        Fortress fortress = map.placeBuilding(new Fortress(player0), point3);

        /* Connect the fortress with the harbor */
        Road road1 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), harbor.getFlag());

        /* Verify that the harbor waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(harbor.getFlag().getStackedCargo().size(), 8);
            assertNull(harbor.getWorker().getCargo());

            map.stepTime();
        }

        /* Remove one of the cargos */
        Cargo cargo = harbor.getFlag().getStackedCargo().get(0);
        harbor.getFlag().retrieveCargo(cargo);

        assertEquals(harbor.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, harbor.getWorker());
    }

    @Test
    public void testHarborDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place a lake */
        Point point0 = new Point(20, 6);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(16, 6);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Make sure there is enough construction material in the headquarter */
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Make sure there is enough construction material in the headquarter */
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Wait for the harbor to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(harbor);
        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Make sure there is enough construction material in the harbor */
        Utils.adjustInventoryTo(harbor, PLANK, 50);
        Utils.adjustInventoryTo(harbor, STONE, 50);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, harbor.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Place fortress */
        Point point3 = new Point(12, 10);
        Fortress fortress = map.placeBuilding(new Fortress(player0), point3);

        /* Connect the fortress with the harbor */
        Road road1 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), harbor.getFlag());

        /* The harbor waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(harbor.getFlag().getStackedCargo().size(), 8);
            assertNull(harbor.getWorker().getCargo());

            map.stepTime();
        }

        /* Remove a cargo from the flag */
        Cargo cargo = harbor.getFlag().getStackedCargo().get(0);
        harbor.getFlag().retrieveCargo(cargo);

        assertEquals(harbor.getFlag().getStackedCargo().size(), 7);

        /* Wait for the worker to put the cargo on the flag */
        assertTrue(fortress.needsMaterial(PLANK));

        Cargo newCargo = Utils.fastForwardUntilWorkerCarriesCargo(map, harbor.getWorker());

        assertEquals(harbor.getWorker().getTarget(), harbor.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, harbor.getWorker(), harbor.getFlag().getPosition());

        assertEquals(harbor.getFlag().getStackedCargo().size(), 8);

        /* Verify that the harbor doesn't produce anything because the flag is full until the courier comes and removes a cargo */
        for (int i = 0; i < 400; i++) {

            if (harbor.getFlag().getStackedCargo().size() < 8) {
                break;
            }

            assertEquals(harbor.getFlag().getStackedCargo().size(), 8);
            assertNull(harbor.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testPushedOutCargoGoesToOtherHarbor() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place a lake */
        Point point0 = new Point(20, 6);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(16, 6);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Make sure there is enough construction material in the headquarter */
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Wait for the harbor to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(harbor);
        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Push out fish from the headquarter */
        Utils.adjustInventoryTo(headquarter, FISH, 10);

        headquarter.pushOutAll(FISH);

        /* Verify that all the fish gets transported to the harbor */
        assertEquals(harbor.getAmount(FISH), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, headquarter.getWorker(), FISH);

        assertEquals(headquarter.getWorker().getCargo().getMaterial(), FISH);

        Utils.waitForBuildingToGetAmountOfMaterial(headquarter, FISH, 0);

        assertEquals(headquarter.getAmount(FISH), 0);

        Utils.waitForBuildingToGetAmountOfMaterial(harbor, FISH, 10);

        assertEquals(harbor.getAmount(FISH), 10);
    }

    @Test
    public void testPushedOutWorkerGoesToOtherHarborWhenOwnStoreIsBlocked() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place a lake */
        Point point0 = new Point(20, 6);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(16, 6);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Make sure there is enough construction material in the headquarter */
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Wait for the harbor to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(harbor);
        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Push out fish from the headquarter */
        Utils.adjustInventoryTo(headquarter, SCOUT, 10);

        headquarter.pushOutAll(SCOUT);
        headquarter.blockDeliveryOfMaterial(SCOUT);

        /* Verify that all the scout goes to the harbor */
        assertEquals(harbor.getAmount(SCOUT), 0);

        Worker scout = Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0).get(0);

        assertEquals(scout.getPosition(), headquarter.getPosition());
        assertNull(headquarter.getWorker().getCargo());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, headquarter.getFlag().getPosition());

        assertEquals(scout.getTarget(), harbor.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, harbor.getPosition());

        assertFalse(map.getWorkers().contains(scout));

        Utils.waitForBuildingToGetAmountOfMaterial(headquarter, SCOUT, 0);

        assertEquals(headquarter.getAmount(SCOUT), 0);

        Utils.waitForBuildingToGetAmountOfMaterial(harbor, SCOUT, 10);

        assertEquals(harbor.getAmount(SCOUT), 10);
    }

    @Test
    public void testPushedOutMaterialFollowsPriorityOrder() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place a lake */
        Point point0 = new Point(20, 6);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(16, 6);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Make sure there is enough construction material in the headquarter */
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Wait for the harbor to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(harbor);
        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Push out fish from the headquarter */
        Utils.adjustInventoryTo(headquarter, FISH, 10);
        Utils.adjustInventoryTo(headquarter, COIN, 10);

        headquarter.pushOutAll(FISH);
        headquarter.pushOutAll(COIN);

        /* Set transport priority for fish above coin */
        player0.setTransportPriority(0, TransportCategory.FOOD);
        player0.setTransportPriority(1, TransportCategory.COIN);

        /* Verify that all the fish gets transported to the harbor before the coins */
        assertEquals(harbor.getAmount(FISH), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, headquarter.getWorker(), FISH);

        assertEquals(headquarter.getWorker().getCargo().getMaterial(), FISH);

        Utils.waitForBuildingToGetAmountOfMaterial(headquarter, FISH, 0);

        assertEquals(headquarter.getAmount(FISH), 0);
        assertEquals(headquarter.getAmount(COIN), 10);
    }

    @Test
    public void testDeliveriesGoToOtherHarborWhenDeliveryIsBlocked() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place a lake */
        Point point0 = new Point(20, 6);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(16, 6);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Place well */
        Point point3 = new Point(9, 7);
        Well well = map.placeBuilding(new Well(player0), point3);

        /* Make sure there is enough construction material in the headquarter */
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Connect the harbor with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed */
        Utils.waitForBuildingToBeConstructed(harbor);

        /* Connect the well with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, well.getFlag(), headquarter.getFlag());

        /* Wait for the well to get constructed */
        Utils.waitForBuildingToBeConstructed(well);

        Utils.waitForNonMilitaryBuildingToGetPopulated(well);

        /* Verify that when delivery is blocked for water in the headquarter,
           all deliveries from the well go to the harbor even if it's further away
        */
        assertTrue(well.isReady());
        assertNotNull(well.getWorker());

        headquarter.blockDeliveryOfMaterial(WATER);

        Utils.adjustInventoryTo(harbor, WATER, 0);

        for (int i = 0; i < 10; i++) {

            /* Wait for the well worker to produce a water cargo */
            Cargo cargo = Utils.fastForwardUntilWorkerCarriesCargo(map, well.getWorker(), WATER);

            /* Wait for the courier for the road between the well and the headquarter to pick up the water cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, road1.getCourier(), cargo);

            assertEquals(road1.getCourier().getTarget(), headquarter.getFlag().getPosition());

            /* Verify that the cargo is put on the headquarter's flag and picked up by the second courier,
               instead of delivered to the headquarter
             */
            Utils.fastForwardUntilWorkerReachesPoint(map, road1.getCourier(), headquarter.getFlag().getPosition());

            assertTrue(headquarter.getFlag().getStackedCargo().contains(cargo));

            Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier(), cargo);

            assertEquals(road0.getCourier().getTarget(), harbor.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), harbor.getPosition());

            assertNull(road0.getCourier().getCargo());
            assertEquals(harbor.getAmount(WATER), i + 1);
        }
    }

    @Test
    public void testPushedOutMaterialStopsWhenFlagFillsUp() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Make sure there is enough construction material in the headquarter */
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Verify that pushing out planks will fill up the flag and then stop */
        assertEquals(headquarter.getFlag().getStackedCargo().size(), 0);

        headquarter.pushOutAll(PLANK);

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
}
