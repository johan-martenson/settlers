/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.SAWMILL_WORKER;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.SawmillWorker;
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
public class TestSawmill {

    @Test
    public void testSawmillOnlyNeedsTwoPlancksAndTwoStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing sawmill */
        Point point22 = new Point(6, 22);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point22);
        
        /* Deliver two planck and two stone */
        Cargo planckCargo = new Cargo(PLANCK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        sawmill0.putCargo(planckCargo);
        sawmill0.putCargo(planckCargo);
        sawmill0.putCargo(stoneCargo);
        sawmill0.putCargo(stoneCargo);
    
        /* Verify that this is enough to construct the sawmill */
        for (int i = 0; i < 150; i++) {
            assertTrue(sawmill0.underConstruction());
            
            map.stepTime();
        }

        assertTrue(sawmill0.ready());
    }

    @Test
    public void testSawmillCannotBeConstructedWithTooFewPlancks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing sawmill */
        Point point22 = new Point(6, 22);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point22);
        
        /* Deliver one planck and two stone */
        Cargo planckCargo = new Cargo(PLANCK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        sawmill0.putCargo(planckCargo);
        sawmill0.putCargo(stoneCargo);
        sawmill0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the sawmill */
        for (int i = 0; i < 500; i++) {
            assertTrue(sawmill0.underConstruction());

            map.stepTime();
        }

        assertFalse(sawmill0.ready());
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
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing sawmill */
        Point point22 = new Point(6, 22);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point22);
        
        /* Deliver two plancks and one stone */
        Cargo planckCargo = new Cargo(PLANCK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        sawmill0.putCargo(planckCargo);
        sawmill0.putCargo(planckCargo);
        sawmill0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the sawmill */
        for (int i = 0; i < 500; i++) {
            assertTrue(sawmill0.underConstruction());

            map.stepTime();
        }

        assertFalse(sawmill0.ready());
    }

    @Test
    public void testSawmillNeedsWorker() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Unfinished samwill doesn't need worker */
        assertFalse(sawmill.needsWorker());
        
        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill, map);
        
        assertTrue(sawmill.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneSawmillWorkerAtStart() {
        Headquarter hq = new Headquarter(null);
        
        assertEquals(hq.getAmount(SAWMILL_WORKER), 1);
    }
    
    @Test
    public void testSawmillGetsAssignedWorker() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

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
        Utils.constructHouse(sawmill, map);
        
        assertTrue(sawmill.needsWorker());

        /* Verify that a sawmill worker leaves the hq */        
        assertEquals(map.getWorkers().size(), 1);

        Utils.fastForward(3, map);
        
        assertEquals(map.getWorkers().size(), 3);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), SawmillWorker.class);
        
        /* Let the sawmill worker reach the sawmill */
        SawmillWorker sw = null;
        
        for (Worker w : map.getWorkers()) {
            if (w instanceof SawmillWorker) {
                sw = (SawmillWorker)w;
            }
        }
        
        assertNotNull(sw);
        assertEquals(sw.getTarget(), sawmill.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, sw);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sw);
    }
    
    @Test
    public void testOccupiedSawmillWithoutWoodProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill, map);

        /* Occupy the sawmill */
        Worker sw = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill, map);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sw);        

        /* Verify that the sawmill doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertNull(sw.getCargo());
            map.stepTime();
        }
    }
    
    @Test
    public void testUnoccupiedSawmillProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill, map);

        /* Verify that the sawmill doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedSawmillWithWoodProducesPlancks() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill, map);
        
        /* Occupy the sawmill */
        Worker sw = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill, map);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sw);        

        /* Deliver wood to the sawmill */
        sawmill.putCargo(new Cargo(WOOD, map));
        sawmill.putCargo(new Cargo(WOOD, map));
        
        /* Verify that the sawmill produces plancks */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertNull(sw.getCargo());
        }

        map.stepTime();

        assertNotNull(sw.getCargo());
        assertEquals(sw.getCargo().getMaterial(), PLANCK);
        assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testSawmillWorkerLeavesPlancksAtTheFlag() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

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
        Utils.constructHouse(sawmill, map);

        /* Occupy the sawmill */
        Worker sw = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill, map);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sw);        

        /* Deliver wood to the sawmill */
        sawmill.putCargo(new Cargo(WOOD, map));
        sawmill.putCargo(new Cargo(WOOD, map));
        
        /* Verify that the sawmill produces plancks */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertNull(sw.getCargo());
        }

        map.stepTime();
        
        assertNotNull(sw.getCargo());
        assertEquals(sw.getCargo().getMaterial(), PLANCK);
        assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
        
        /* Verify that the sawmill worker leaves the cargo at the flag */
        assertEquals(sw.getTarget(), sawmill.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, sw, sawmill.getFlag().getPosition());
        
        assertFalse(sawmill.getFlag().getStackedCargo().isEmpty());
        assertNull(sw.getCargo());
        assertEquals(sw.getTarget(), sawmill.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, sw);
        
        assertTrue(sw.isInsideBuilding());
    }

    @Test
    public void testProductionOfOnePlanckConsumesOneWood() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill, map);
        
        /* Occupy the sawmill */
        Worker sw = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill, map);
        
        /* Deliver wood to the sawmill */
        sawmill.putCargo(new Cargo(WOOD, map));
        
        /* Wait until the sawmill worker produces a planck */
        assertEquals(sawmill.getAmount(WOOD), 1);
        
        Utils.fastForward(150, map);
        
        assertEquals(sawmill.getAmount(WOOD), 0);
        assertTrue(sawmill.needsMaterial(WOOD));
    }

    @Test
    public void testProductionCountdownStartsWhenWoodIsAvailable() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill, map);
        
        /* Occupy the sawmill */
        Worker sw = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill, map);
        
        /* Fast forward so that the sawmill worker would produced plancks
           if it had had any wood
        */
        Utils.fastForward(150, map);
        
        assertNull(sw.getCargo());
        
        /* Deliver wood to the sawmill */
        sawmill.putCargo(new Cargo(WOOD, map));
        
        /* Verify that it takes 50 steps for the sawmill worker to produce the planck */
        int i;
        for (i = 0; i < 50; i++) {
            assertNull(sw.getCargo());
            map.stepTime();
        }
        
        assertNotNull(sw.getCargo());
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
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(8, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0, map);

        /* Occupy the sawmill */
        Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0, map);

        /* Deliver material to the sawmill */
        Cargo woodCargo = new Cargo(WOOD, map);
        
        sawmill0.putCargo(woodCargo);
        sawmill0.putCargo(woodCargo);
        
        /* Let the sawmill worker rest */
        Utils.fastForward(100, map);

        /* Wait for the sawmill worker to produce a new planck cargo */
        Utils.fastForward(50, map);

        Worker sawmillWorker = sawmill0.getWorker();

        assertNotNull(sawmillWorker.getCargo());

        /* Verify that the sawmill worker puts the planck cargo at the flag */
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
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(8, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0, map);

        /* Deliver material to the sawmill */
        Cargo woodCargo = new Cargo(WOOD, map);
        
        sawmill0.putCargo(woodCargo);
        sawmill0.putCargo(woodCargo);

        /* Occupy the sawmill */
        Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0, map);

        /* Let the sawmill worker rest */
        Utils.fastForward(100, map);

        /* Wait for the sawmill worker to produce a new planck cargo */
        Utils.fastForward(50, map);

        Worker sawmillWorker = sawmill0.getWorker();

        assertNotNull(sawmillWorker.getCargo());

        /* Verify that the sawmill worker puts the planck cargo at the flag */
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
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(sawmill0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));
    
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
    
        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), sawmill0.getFlag().getPosition());
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);
        
        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());
        
        int amount = headquarter0.getAmount(PLANCK);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());
        
        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(PLANCK), amount + 1);
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
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(8, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0, map);

        /* Occupy the sawmill */
        Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0, map);
        
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
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(8, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Connect the sawmill with the headquarter */
        map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0, map);

        /* Occupy the sawmill */
        Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0, map);
        
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
        for (Point p : sawmillWorker.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
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
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(8, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        /* Connect the sawmill with the headquarter */
        map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0, map);

        /* Destroy the sawmill */
        sawmill0.tearDown();

        assertTrue(sawmill0.burningDown());

        /* Wait for the sawmill to stop burning */
        Utils.fastForward(50, map);
        
        assertTrue(sawmill0.destroyed());
        
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
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(8, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);
        
        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0, map);

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
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point26 = new Point(8, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point26);
        
        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0, map);

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
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place sawmill */
        Point point1 = new Point(8, 6);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point1);
        
        /* Connect the sawmill and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the sawmill */
        Utils.constructHouse(sawmill0, map);
        
        /* Assign a worker to the sawmill */
        SawmillWorker sawmillWorker = new SawmillWorker(player0, map);
        
        Utils.occupyBuilding(sawmillWorker, sawmill0, map);
        
        assertTrue(sawmillWorker.isInsideBuilding());

        /* Deliver wood to the sawmill */
        sawmill0.putCargo(new Cargo(WOOD, map));

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the sawmill worker to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, sawmillWorker);
        
        assertEquals(sawmillWorker.getCargo().getMaterial(), PLANCK);

        /* Wait for the worker to deliver the cargo */
        assertEquals(sawmillWorker.getTarget(), sawmill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, sawmill0.getFlag().getPosition());

        /* Stop production and verify that no planck is produced */
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
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place sawmill */
        Point point1 = new Point(8, 6);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point1);
        
        /* Connect the sawmill and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the sawmill */
        Utils.constructHouse(sawmill0, map);
        
        /* Assign a worker to the sawmill */
        SawmillWorker sawmillWorker = new SawmillWorker(player0, map);
        
        Utils.occupyBuilding(sawmillWorker, sawmill0, map);
        
        assertTrue(sawmillWorker.isInsideBuilding());

        /* Deliver wood to the sawmill */
        sawmill0.putCargo(new Cargo(WOOD, map));

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the sawmill worker to produce planck */
        Utils.fastForwardUntilWorkerProducesCargo(map, sawmillWorker);

        assertEquals(sawmillWorker.getCargo().getMaterial(), PLANCK);

        /* Wait for the worker to deliver the cargo */
        assertEquals(sawmillWorker.getTarget(), sawmill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, sawmill0.getFlag().getPosition());

        /* Stop production */
        sawmill0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(sawmillWorker.getCargo());
            
            map.stepTime();
        }

        /* Resume production and verify that the sawmill produces planck again */
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
        Point hqPoint = new Point(15, 15);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place sawmill*/
        Point point1 = new Point(20, 14);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0, map);
        
        /* Connect the sawmill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), sawmill0.getFlag());

        /* Wait for sawmill worker to get assigned and leave the headquarter */
        List<SawmillWorker> workers = Utils.waitForWorkersOutsideBuilding(SawmillWorker.class, 1, player0, map);

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
        Building headquarter2 = new Headquarter(player2);
        Point point10 = new Point(70, 70);
        map.placeBuilding(headquarter2, point10);

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

        /* Place sawmill close to the new border */
        Point point4 = new Point(28, 18);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point4);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0, map);

        /* Occupy the sawmill */
        SawmillWorker worker = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0, map);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down*/
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }
}
