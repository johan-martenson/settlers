/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.SawmillWorker;
import org.appland.settlers.model.Storehouse;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.SAWMILL_WORKER;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
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
public class TestSawmill {

    @Test
    public void testSawmillOnlyNeedsTwoPlanksAndTwoStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing sawmill */
        Point point22 = new Point(6, 12);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point22);

        /* Deliver two plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        sawmill0.putCargo(plankCargo);
        sawmill0.putCargo(plankCargo);
        sawmill0.putCargo(stoneCargo);
        sawmill0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the sawmill */
        for (int i = 0; i < 150; i++) {
            assertTrue(sawmill0.underConstruction());

            map.stepTime();
        }

        assertTrue(sawmill0.isReady());
    }

    @Test
    public void testSawmillCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing sawmill */
        Point point22 = new Point(6, 12);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point22);

        /* Deliver one plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        sawmill0.putCargo(plankCargo);
        sawmill0.putCargo(stoneCargo);
        sawmill0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the sawmill */
        for (int i = 0; i < 500; i++) {
            assertTrue(sawmill0.underConstruction());

            map.stepTime();
        }

        assertFalse(sawmill0.isReady());
    }

    @Test
    public void testSawmillCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing sawmill */
        Point point22 = new Point(6, 12);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point22);

        /* Deliver two planks and one stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        sawmill0.putCargo(plankCargo);
        sawmill0.putCargo(plankCargo);
        sawmill0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the sawmill */
        for (int i = 0; i < 500; i++) {
            assertTrue(sawmill0.underConstruction());

            map.stepTime();
        }

        assertFalse(sawmill0.isReady());
    }

    @Test
    public void testSawmillNeedsWorker() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Unfinished sawmill doesn't need worker */
        assertFalse(sawmill.needsWorker());

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill);

        assertTrue(sawmill.needsWorker());
    }

    @Test
    public void testHeadquarterHasAtLeastOneSawmillWorkerAtStart() {
        Headquarter headquarter = new Headquarter(null);

        assertTrue(headquarter.getAmount(SAWMILL_WORKER) >= 1);
    }

    @Test
    public void testSawmillGetsAssignedWorker() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Place a road between the headquarter and the sawmill */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill);

        assertTrue(sawmill.needsWorker());

        /* Verify that a sawmill worker leaves the headquarter */
        assertEquals(map.getWorkers().size(), 1);

        Utils.fastForward(3, map);

        assertEquals(map.getWorkers().size(), 3);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), SawmillWorker.class);

        /* Let the sawmill worker reach the sawmill */
        SawmillWorker sawmillWorker0 = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof SawmillWorker) {
                sawmillWorker0 = (SawmillWorker)worker;
            }
        }

        assertNotNull(sawmillWorker0);
        assertEquals(sawmillWorker0.getTarget(), sawmill.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, sawmillWorker0);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);
    }

    @Test
    public void testSawmillWorkerGetsCreatedFromSaw() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all sawmill workers from the headquarter and add a saw */
        Utils.adjustInventoryTo(building0, SAWMILL_WORKER, 0);
        Utils.adjustInventoryTo(building0, Material.SAW, 1);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Place a road between the headquarter and the sawmill */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill);

        assertTrue(sawmill.needsWorker());

        /* Verify that a sawmill worker leaves the headquarter */
        assertEquals(map.getWorkers().size(), 1);

        Utils.fastForward(3, map);

        assertEquals(map.getWorkers().size(), 3);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), SawmillWorker.class);

        /* Let the sawmill worker reach the sawmill */
        SawmillWorker sawmillWorker0 = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof SawmillWorker) {
                sawmillWorker0 = (SawmillWorker)worker;
            }
        }

        assertNotNull(sawmillWorker0);
        assertEquals(sawmillWorker0.getTarget(), sawmill.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, sawmillWorker0);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);
    }

    @Test
    public void testOccupiedSawmillWithoutWoodProducesNothing() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill);

        /* Occupy the sawmill */
        Worker sawmillWorker0 = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        /* Verify that the sawmill doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertNull(sawmillWorker0.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedSawmillProducesNothing() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill);

        /* Verify that the sawmill doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedSawmillWithWoodProducesPlanks() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill);

        /* Occupy the sawmill */
        Worker sawmillWorker0 = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        /* Deliver wood to the sawmill */
        sawmill.putCargo(new Cargo(WOOD, map));
        sawmill.putCargo(new Cargo(WOOD, map));

        /* Verify that the sawmill produces planks */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertNull(sawmillWorker0.getCargo());
        }

        map.stepTime();

        assertNotNull(sawmillWorker0.getCargo());
        assertEquals(sawmillWorker0.getCargo().getMaterial(), PLANK);
        assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testSawmillWorkerLeavesPlanksAtTheFlag() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Place a road between the headquarter and the sawmill */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill);

        /* Occupy the sawmill */
        Worker sawmillWorker0 = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        /* Deliver wood to the sawmill */
        sawmill.putCargo(new Cargo(WOOD, map));
        sawmill.putCargo(new Cargo(WOOD, map));

        /* Verify that the sawmill produces planks */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertNull(sawmillWorker0.getCargo());
        }

        map.stepTime();

        assertNotNull(sawmillWorker0.getCargo());
        assertEquals(sawmillWorker0.getCargo().getMaterial(), PLANK);
        assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());

        /* Verify that the sawmill worker leaves the cargo at the flag */
        assertEquals(sawmillWorker0.getTarget(), sawmill.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker0, sawmill.getFlag().getPosition());

        assertFalse(sawmill.getFlag().getStackedCargo().isEmpty());
        assertNull(sawmillWorker0.getCargo());
        assertEquals(sawmillWorker0.getTarget(), sawmill.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, sawmillWorker0);

        assertTrue(sawmillWorker0.isInsideBuilding());
    }

    @Test
    public void testProductionOfOnePlankConsumesOneWood() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill);

        /* Occupy the sawmill */
        Worker sawmillWorker0 = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill);

        /* Deliver wood to the sawmill */
        sawmill.putCargo(new Cargo(WOOD, map));

        /* Wait until the sawmill worker produces a plank */
        assertEquals(sawmill.getAmount(WOOD), 1);

        Utils.fastForward(150, map);

        assertEquals(sawmill.getAmount(WOOD), 0);
        assertTrue(sawmill.needsMaterial(WOOD));
    }

    @Test
    public void testProductionCountdownStartsWhenWoodIsAvailable() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill);

        /* Occupy the sawmill */
        Worker sawmillWorker0 = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill);

        /* Fast forward so that the sawmill worker would produced planks
           if it had had any wood
        */
        Utils.fastForward(150, map);

        assertNull(sawmillWorker0.getCargo());

        /* Deliver wood to the sawmill */
        sawmill.putCargo(new Cargo(WOOD, map));

        /* Verify that it takes 50 steps for the sawmill worker to produce the plank */
        for (int i = 0; i < 50; i++) {
            assertNull(sawmillWorker0.getCargo());
            map.stepTime();
        }

        assertNotNull(sawmillWorker0.getCargo());
    }

    @Test
    public void testSawmillWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(8, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0);

        /* Occupy the sawmill */
        Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0);

        /* Deliver material to the sawmill */
        Cargo woodCargo = new Cargo(WOOD, map);

        sawmill0.putCargo(woodCargo);
        sawmill0.putCargo(woodCargo);

        /* Let the sawmill worker rest */
        Utils.fastForward(100, map);

        /* Wait for the sawmill worker to produce a new plank cargo */
        Utils.fastForward(50, map);

        Worker sawmillWorker = sawmill0.getWorker();

        assertNotNull(sawmillWorker.getCargo());

        /* Verify that the sawmill worker puts the plank cargo at the flag */
        assertEquals(sawmillWorker.getTarget(), sawmill0.getFlag().getPosition());
        assertTrue(sawmill0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, sawmill0.getFlag().getPosition());

        assertNull(sawmillWorker.getCargo());
        assertFalse(sawmill0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the sawmill */
        assertEquals(sawmillWorker.getTarget(), sawmill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, sawmill0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(sawmillWorker.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(sawmillWorker.getTarget(), sawmill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, sawmill0.getFlag().getPosition());

        assertNull(sawmillWorker.getCargo());
        assertEquals(sawmill0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(8, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0);

        /* Deliver material to the sawmill */
        Cargo woodCargo = new Cargo(WOOD, map);

        sawmill0.promiseDelivery(WOOD);
        sawmill0.putCargo(woodCargo);

        sawmill0.promiseDelivery(WOOD);
        sawmill0.putCargo(woodCargo);

        /* Occupy the sawmill */
        Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0);

        /* Let the sawmill worker rest */
        Utils.fastForward(100, map);

        /* Wait for the sawmill worker to produce a new plank cargo */
        Utils.fastForward(50, map);

        Worker sawmillWorker = sawmill0.getWorker();

        assertNotNull(sawmillWorker.getCargo());

        /* Verify that the sawmill worker puts the plank cargo at the flag */
        assertEquals(sawmillWorker.getTarget(), sawmill0.getFlag().getPosition());
        assertTrue(sawmill0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, sawmill0.getFlag().getPosition());

        assertNull(sawmillWorker.getCargo());
        assertFalse(sawmill0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = sawmill0.getFlag().getStackedCargo().get(0);

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), sawmill0.getFlag().getPosition());

        /* Connect the sawmill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), sawmill0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), sawmill0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        for (int i = 0; i < 1000; i++) {

            if (courier.getTarget().equals(sawmill0.getFlag().getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(courier.getTarget(), sawmill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(PLANK);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(PLANK), amount + 1);
    }

    @Test
    public void testSawmillWorkerGoesBackToStorageWhenSawmillIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(8, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0);

        /* Occupy the sawmill */
        Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0);

        /* Destroy the sawmill */
        Worker sawmillWorker = sawmill0.getWorker();

        assertTrue(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getPosition(), sawmill0.getPosition());

        sawmill0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(SAWMILL_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, headquarter0.getPosition());

        /* Verify that the sawmill worker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(SAWMILL_WORKER), amount + 1);
    }

    @Test
    public void testSawmillWorkerGoesBackOnToStorageOnRoadsIfPossibleWhenSawmillIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(8, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Connect the sawmill with the headquarter */
        map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0);

        /* Occupy the sawmill */
        Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0);

        /* Destroy the sawmill */
        Worker sawmillWorker = sawmill0.getWorker();

        assertTrue(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getPosition(), sawmill0.getPosition());

        sawmill0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point point : sawmillWorker.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testDestroyedSawmillIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(8, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Connect the sawmill with the headquarter */
        map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0);

        /* Destroy the sawmill */
        sawmill0.tearDown();

        assertTrue(sawmill0.isBurningDown());

        /* Wait for the sawmill to stop burning */
        Utils.fastForward(50, map);

        assertTrue(sawmill0.isDestroyed());

        /* Wait for the sawmill to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), sawmill0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(sawmill0));
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(8, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(sawmill0.getPosition(), sawmill0.getFlag().getPosition()));

        map.removeFlag(sawmill0.getFlag());

        assertNull(map.getRoad(sawmill0.getPosition(), sawmill0.getFlag().getPosition()));
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(8, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(sawmill0.getPosition(), sawmill0.getFlag().getPosition()));

        sawmill0.tearDown();

        assertNull(map.getRoad(sawmill0.getPosition(), sawmill0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInSawmillCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(10, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        /* Connect the sawmill and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter.getFlag());

        /* Finish the sawmill */
        Utils.constructHouse(sawmill0);

        /* Assign a worker to the sawmill */
        SawmillWorker sawmillWorker = new SawmillWorker(player0, map);

        Utils.occupyBuilding(sawmillWorker, sawmill0);

        assertTrue(sawmillWorker.isInsideBuilding());

        /* Deliver wood to the sawmill */
        sawmill0.putCargo(new Cargo(WOOD, map));

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the sawmill worker to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, sawmillWorker);

        assertEquals(sawmillWorker.getCargo().getMaterial(), PLANK);

        /* Wait for the worker to deliver the cargo */
        assertEquals(sawmillWorker.getTarget(), sawmill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, sawmill0.getFlag().getPosition());

        /* Stop production and verify that no plank is produced */
        sawmill0.stopProduction();

        assertFalse(sawmill0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(sawmillWorker.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInSawmillCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(10, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        /* Connect the sawmill and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter.getFlag());

        /* Finish the sawmill */
        Utils.constructHouse(sawmill0);

        /* Assign a worker to the sawmill */
        SawmillWorker sawmillWorker = new SawmillWorker(player0, map);

        Utils.occupyBuilding(sawmillWorker, sawmill0);

        assertTrue(sawmillWorker.isInsideBuilding());

        /* Deliver wood to the sawmill */
        sawmill0.putCargo(new Cargo(WOOD, map));

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the sawmill worker to produce plank */
        Utils.fastForwardUntilWorkerProducesCargo(map, sawmillWorker);

        assertEquals(sawmillWorker.getCargo().getMaterial(), PLANK);

        /* Wait for the worker to deliver the cargo */
        assertEquals(sawmillWorker.getTarget(), sawmill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, sawmill0.getFlag().getPosition());

        /* Stop production */
        sawmill0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(sawmillWorker.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the sawmill produces plank again */
        sawmill0.resumeProduction();

        assertTrue(sawmill0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, sawmillWorker);

        assertNotNull(sawmillWorker.getCargo());
    }

    @Test
    public void testAssignedSawmillWorkerHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(20, 14);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0);

        /* Connect the sawmill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), sawmill0.getFlag());

        /* Wait for sawmill worker to get assigned and leave the headquarter */
        List<SawmillWorker> workers = Utils.waitForWorkersOutsideBuilding(SawmillWorker.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        SawmillWorker worker = workers.get(0);

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

        /* Place player 2's headquarter */
        Headquarter headquarter2 = new Headquarter(player2);
        Point point10 = new Point(70, 70);
        map.placeBuilding(headquarter2, point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(11, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 9);
        Building fortress0 = new Fortress(player0);
        map.placeBuilding(fortress0, point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place sawmill close to the new border */
        Point point4 = new Point(28, 18);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point4);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0);

        /* Occupy the sawmill */
        SawmillWorker worker = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testSawmillWorkerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing sawmill */
        Point point2 = new Point(14, 4);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, sawmill0.getFlag());

        /* Wait for the sawmill worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(SawmillWorker.class, 1, player0);

        SawmillWorker sawmillWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof SawmillWorker) {
                sawmillWorker = (SawmillWorker) worker;
            }
        }

        assertNotNull(sawmillWorker);
        assertEquals(sawmillWorker.getTarget(), sawmill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the sawmill worker has started walking */
        assertFalse(sawmillWorker.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the sawmill worker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, flag0.getPosition());

        assertEquals(sawmillWorker.getPosition(), flag0.getPosition());

        /* Verify that the sawmill worker returns to the headquarter when it reaches the flag */
        assertEquals(sawmillWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, headquarter0.getPosition());
    }

    @Test
    public void testSawmillWorkerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing sawmill */
        Point point2 = new Point(14, 4);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, sawmill0.getFlag());

        /* Wait for the sawmill worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(SawmillWorker.class, 1, player0);

        SawmillWorker sawmillWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof SawmillWorker) {
                sawmillWorker = (SawmillWorker) worker;
            }
        }

        assertNotNull(sawmillWorker);
        assertEquals(sawmillWorker.getTarget(), sawmill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the sawmillWorker has started walking */
        assertFalse(sawmillWorker.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the sawmill worker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, flag0.getPosition());

        assertEquals(sawmillWorker.getPosition(), flag0.getPosition());

        /* Verify that the sawmill worker continues to the final flag */
        assertEquals(sawmillWorker.getTarget(), sawmill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, sawmill0.getFlag().getPosition());

        /* Verify that the sawmill worker goes out to sawmill instead of going directly back */
        assertNotEquals(sawmillWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testSawmillWorkerReturnsToStorageIfSawmillIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing sawmill */
        Point point2 = new Point(14, 4);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, sawmill0.getFlag());

        /* Wait for the sawmill worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(SawmillWorker.class, 1, player0);

        SawmillWorker sawmillWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof SawmillWorker) {
                sawmillWorker = (SawmillWorker) worker;
            }
        }

        assertNotNull(sawmillWorker);
        assertEquals(sawmillWorker.getTarget(), sawmill0.getPosition());

        /* Wait for the sawmill worker to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, flag0.getPosition());

        map.stepTime();

        /* See that the sawmill worker has started walking */
        assertFalse(sawmillWorker.isExactlyAtPoint());

        /* Tear down the sawmill */
        sawmill0.tearDown();

        /* Verify that the sawmill worker continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, sawmill0.getFlag().getPosition());

        assertEquals(sawmillWorker.getPosition(), sawmill0.getFlag().getPosition());

        /* Verify that the sawmill worker goes back to storage */
        assertEquals(sawmillWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testSawmillWorkerGoesOffroadBackToClosestStorageWhenSawmillIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(17, 17);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0);

        /* Occupy the sawmill */
        Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0);

        /* Place a second storage closer to the sawmill */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the sawmill */
        Worker sawmillWorker = sawmill0.getWorker();

        assertTrue(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getPosition(), sawmill0.getPosition());

        sawmill0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(SAWMILL_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, storehouse0.getPosition());

        /* Verify that the sawmill worker is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(SAWMILL_WORKER), amount + 1);
    }

    @Test
    public void testSawmillWorkerReturnsOffroadAndAvoidsBurningStorageWhenSawmillIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 17);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(17, 17);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0);

        /* Occupy the sawmill */
        Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0);

        /* Place a second storage closer to the sawmill */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Destroy the sawmill */
        Worker sawmillWorker = sawmill0.getWorker();

        assertTrue(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getPosition(), sawmill0.getPosition());

        sawmill0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(SAWMILL_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, headquarter0.getPosition());

        /* Verify that the sawmill worker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(SAWMILL_WORKER), amount + 1);
    }

    @Test
    public void testSawmillWorkerReturnsOffroadAndAvoidsDestroyedStorageWhenSawmillIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(17, 17);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0);

        /* Occupy the sawmill */
        Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0);

        /* Place a second storage closer to the sawmill */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

        /* Destroy the sawmill */
        Worker sawmillWorker = sawmill0.getWorker();

        assertTrue(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getPosition(), sawmill0.getPosition());

        sawmill0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(SAWMILL_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, headquarter0.getPosition());

        /* Verify that the sawmill worker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(SAWMILL_WORKER), amount + 1);
    }

    @Test
    public void testSawmillWorkerReturnsOffroadAndAvoidsUnfinishedStorageWhenSawmillIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(17, 17);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0);

        /* Occupy the sawmill */
        Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0);

        /* Place a second storage closer to the sawmill */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the sawmill */
        Worker sawmillWorker = sawmill0.getWorker();

        assertTrue(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getPosition(), sawmill0.getPosition());

        sawmill0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(SAWMILL_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, headquarter0.getPosition());

        /* Verify that the sawmill worker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(SAWMILL_WORKER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place sawmill */
        Point point26 = new Point(17, 17);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Place road to connect the headquarter and the sawmill */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), sawmill0.getFlag());

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(SawmillWorker.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, sawmill0.getFlag().getPosition());

        /* Tear down the building */
        sawmill0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquarter instead of entering
        */
        assertEquals(worker.getTarget(), sawmill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, sawmill0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testSawmillWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point1);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill);

        /* Populate the sawmill */
        Worker sawmillWorker0 = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        /* Verify that the productivity is 0% when the sawmill doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertNull(sawmillWorker0.getCargo());
            assertEquals(sawmill.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testSawmillWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point1);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill);

        /* Populate the sawmill */
        Worker sawmillWorker0 = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        /* Connect the sawmill with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), sawmill.getFlag());

        /* Make the sawmill create some bread with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (sawmill.needsMaterial(WOOD)) {
                sawmill.putCargo(new Cargo(WOOD, map));
            }
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(sawmill.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (sawmill.needsMaterial(WOOD)) {
                sawmill.putCargo(new Cargo(WOOD, map));
            }

            assertEquals(sawmill.getProductivity(), 100);
        }
    }

    @Test
    public void testSawmillLosesProductivityWhenResourcesRunOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point1);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill);

        /* Populate the sawmill */
        Worker sawmillWorker0 = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        /* Connect the sawmill with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), sawmill.getFlag());

        /* Make the sawmill create some planks with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (sawmill.needsMaterial(WOOD) && sawmill.getAmount(WOOD) < 2) {
                sawmill.putCargo(new Cargo(WOOD, map));
            }
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(sawmill.getProductivity(), 100);

        for (int i = 0; i < 5000; i++) {
            map.stepTime();
        }

        assertEquals(sawmill.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedSawmillHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point1);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill);

        /* Verify that the unoccupied sawmill is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(sawmill.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testSawmillCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(10, 10);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0);

        /* Populate the sawmill */
        Worker sawmillWorker0 = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0);

        /* Verify that the sawmill can produce */
        assertTrue(sawmill0.canProduce());
    }

    @Test
    public void testSawmillReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(6, 12);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        /* Construct the sawmill */
        Utils.constructHouse(sawmill0);

        /* Verify that the reported output is correct */
        assertEquals(sawmill0.getProducedMaterial().length, 1);
        assertEquals(sawmill0.getProducedMaterial()[0], PLANK);
    }

    @Test
    public void testSawmillReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(6, 12);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(sawmill0.getMaterialNeeded().size(), 2);
        assertTrue(sawmill0.getMaterialNeeded().contains(PLANK));
        assertTrue(sawmill0.getMaterialNeeded().contains(STONE));
        assertEquals(sawmill0.getTotalAmountNeeded(PLANK), 2);
        assertEquals(sawmill0.getTotalAmountNeeded(STONE), 2);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(sawmill0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testSawmillReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(6, 12);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        /* Construct the sawmill */
        Utils.constructHouse(sawmill0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(sawmill0.getMaterialNeeded().size(), 1);
        assertTrue(sawmill0.getMaterialNeeded().contains(WOOD));
        assertEquals(sawmill0.getTotalAmountNeeded(WOOD), 6);

        for (Material material : Material.values()) {
            if (material == WOOD) {
                continue;
            }

            assertEquals(sawmill0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testSawmillWaitsWhenFlagIsFull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(16, 6);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point1);

        /* Connect the sawmill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter.getFlag());

        /* Wait for the sawmill to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(sawmill);
        Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill);

        /* Give material to the sawmill */
        Utils.putCargoToBuilding(sawmill, WOOD);
        Utils.putCargoToBuilding(sawmill, WOOD);
        Utils.putCargoToBuilding(sawmill, WOOD);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, sawmill.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the sawmill waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(sawmill.getFlag().getStackedCargo().size(), 8);
            assertNull(sawmill.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the sawmill with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(sawmill.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(sawmill.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(sawmill.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, sawmill.getWorker(), PLANK);
    }

    @Test
    public void testSawmillDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(16, 6);
        Sawmill sawmill = map.placeBuilding(new Sawmill(player0), point1);

        /* Connect the sawmill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter.getFlag());

        /* Wait for the sawmill to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(sawmill);
        Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill);

        /* Give material to the sawmill */
        Utils.putCargoToBuilding(sawmill, WOOD);
        Utils.putCargoToBuilding(sawmill, WOOD);
        Utils.putCargoToBuilding(sawmill, WOOD);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, sawmill.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The sawmill waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(sawmill.getFlag().getStackedCargo().size(), 8);
            assertNull(sawmill.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the sawmill with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(sawmill.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(sawmill.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(sawmill.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, sawmill.getWorker(), PLANK);

        /* Wait for the worker to put the cargo on the flag */
        assertEquals(sawmill.getWorker().getTarget(), sawmill.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmill.getWorker(), sawmill.getFlag().getPosition());

        assertEquals(sawmill.getFlag().getStackedCargo().size(), 8);

        /* Verify that the sawmill doesn't produce anything because the flag is full */
        for (int i = 0; i < 400; i++) {
            assertEquals(sawmill.getFlag().getStackedCargo().size(), 8);
            assertNull(sawmill.getWorker().getCargo());

            map.stepTime();
        }
    }
}
