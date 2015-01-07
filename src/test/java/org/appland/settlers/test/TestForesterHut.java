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
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Forester;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidStateForProduction;
import static org.appland.settlers.model.Material.FORESTER;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Worker;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TestForesterHut {

    @Test
    public void testConstructForester() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        ForesterHut f = new ForesterHut(null);

        assertTrue(f.underConstruction());

        assertFalse(f.needsWorker());

        Utils.constructHouse(f, null);

        /* Verify that the forrester is unoccupied when it's newly constructed */
        assertTrue(f.needsWorker());

        /* Verify that the ForesterHut requires a worker */
        assertTrue(f.needsWorker());

        Forester forester = new Forester(null, null);

        /* Assign worker */
        f.assignWorker(forester);

        assertFalse(f.needsWorker());
        assertTrue(f.getWorker().equals(forester));
    }

    @Test(expected = Exception.class)
    public void testPromiseWorkerToUnfinishedForester() throws Exception {
        ForesterHut f = new ForesterHut(null);

        assertTrue(f.underConstruction());

        f.promiseWorker(new Forester(null, null));
    }

    @Test(expected = Exception.class)
    public void testAssignWorkerToUnfinishedForester() throws Exception {
        ForesterHut f = new ForesterHut(null);

        assertTrue(f.underConstruction());

        f.assignWorker(new Forester(null, null));
    }

    @Test(expected = Exception.class)
    public void testAssignWorkerTwice() throws Exception {
        ForesterHut f = new ForesterHut(null);

        Utils.constructHouse(f, null);

        f.assignWorker(new Forester(null, null));

        f.assignWorker(new Forester(null, null));
    }

    @Test(expected = Exception.class)
    public void testPromiseWorkerTwice() throws Exception {
        ForesterHut f = new ForesterHut(null);

        Utils.constructHouse(f, null);

        f.promiseWorker(new Forester(null, null));

        f.promiseWorker(new Forester(null, null));
    }

    @Test
    public void testForesterHutIsNotMilitary() throws Exception {
        ForesterHut f = new ForesterHut(null);

        Utils.constructHouse(f, null);

        assertFalse(f.isMilitaryBuilding());
        assertEquals(f.getHostedMilitary(), 0);
        assertEquals(f.getMaxHostedMilitary(), 0);
    }

    @Test
    public void testForesterHutUnderConstructionNotNeedsWorker() {
        ForesterHut f = new ForesterHut(null);

        assertFalse(f.needsWorker());
    }

    @Test
    public void testForesterIsAssignedToForesterHut() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Finish the forester hut */
        Utils.constructHouse(foresterHut, map);
        
        /* Run game logic twice, once to place courier and once to place forester */
        Utils.fastForward(2, map);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Forester.class);
    }

    @Test
    public void testOnlyOneForesterIsAssignedToForesterHut() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Construct the forester hut */
        constructHouse(foresterHut, map);
        
        /* Run game logic twice, once to place courier and once to place forester */
        Utils.fastForward(2, map);

        assertEquals(map.getWorkers().size(), 3);

        /* Keep running the game loop and make sure no more workers are allocated */
        Utils.fastForward(200, map);

        assertEquals(map.getWorkers().size(), 3);
    }

    @Test
    public void testArrivedForesterRestsInHutAndThenLeaves() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut, map);
        
        /* Manually place forester */
        Forester forester = new Forester(player0, map);

        Utils.occupyBuilding(forester, foresterHut, map);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Run the game logic 99 times and make sure the forester stays in the hut */        
        int i;
        for (i = 0; i < 99; i++) {
            assertTrue(forester.isInsideBuilding());
            map.stepTime();
        }
        
        assertTrue(forester.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();        
        
        assertFalse(forester.isInsideBuilding());
    }

    @Test
    public void testForesterFindsSpotToPlantNewTree() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut, map);
        
        /* Manually place forester */
        Forester forester = new Forester(player0, map);

        Utils.occupyBuilding(forester, foresterHut, map);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Let the forester rest */
        Utils.fastForward(99, map);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(forester.isInsideBuilding());    

        Point point = forester.getTarget();
        assertNotNull(point);
        
        assertFalse(map.isBuildingAtPoint(point));
        assertFalse(map.isRoadAtPoint(point));
        assertFalse(map.isFlagAtPoint(point));
        assertFalse(map.isTreeAtPoint(point));
        assertTrue(forester.isTraveling());
    }

    @Test
    public void testForesterReachesPointToPlantTree() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut, map);
        
        /* Manually place forester */
        Forester forester = new Forester(player0, map);

        Utils.occupyBuilding(forester, foresterHut, map);
        
        /* Let the forester rest */
        Utils.fastForward(99, map);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(forester.isInsideBuilding());    

        Point point = forester.getTarget();

        assertTrue(forester.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, forester);
        
        assertEquals(forester.getPosition(), point);
        assertFalse(forester.isTraveling());
        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
    }

    @Test
    public void testForesterPlantsTree() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut, map);
        
        /* Manually place forester */
        Forester forester = new Forester(player0, map);

        Utils.occupyBuilding(forester, foresterHut, map);
        
        /* Let the forester rest */
        Utils.fastForward(99, map);
                
        assertTrue(forester.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(forester.isInsideBuilding());    

        Point point = forester.getTarget();

        assertTrue(forester.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, forester);
        
        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));        
        assertTrue(forester.isPlanting());
        
        int i;
        for (i = 0; i < 19; i++) {
            assertTrue(forester.isPlanting());
            map.stepTime();
        }

        assertTrue(forester.isPlanting());
        assertFalse(map.isTreeAtPoint(point));

        map.stepTime();
        
        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));
        assertNull(forester.getCargo());
    }

    @Test
    public void testForesterReturnsHomeAfterPlantingTree() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut, map);
        
        /* Manually place forester */
        Forester forester = new Forester(player0, map);

        Utils.occupyBuilding(forester, foresterHut, map);
        
        /* Let the forester rest */
        Utils.fastForward(99, map);
                
        assertTrue(forester.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(forester.isInsideBuilding());    

        Point point = forester.getTarget();

        assertTrue(forester.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, forester);
        
        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());
        
        /* Wait for the forester to plant the tree */
        Utils.fastForward(19, map);
        
        assertTrue(forester.isPlanting());
        assertFalse(map.isTreeAtPoint(point));

        map.stepTime();
        
        /* Verify that the forester goes back home */
        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));

        assertEquals(forester.getTarget(), foresterHut.getPosition());
        assertTrue(forester.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());        
        assertTrue(forester.isInsideBuilding());
    }
    
    @Test
    public void testForesterHutProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut, map);
        
        /* Manually place forester */
        Forester forester = new Forester(player0, map);

        Utils.occupyBuilding(forester, foresterHut, map);
        
        assertTrue(forester.isInsideBuilding());
        assertNull(forester.getCargo());

        /* Verify that the forester doesn't produce anything */
        int i;
        for (i = 0; i < 100; i++) {
            map.stepTime();
            assertNull(forester.getCargo());
            assertTrue(foresterHut.getFlag().getStackedCargo().isEmpty());
        }
    }

    @Test
    public void testForesterStaysInsideWhenThereAreNoSpotsAvailable() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut, map);

        /* Put trees around the forester hut */
        for (Point p : map.getPointsWithinRadius(foresterHut.getPosition(), 4)) {
            if (p.equals(point1)) {
                continue;
            }
            
            if (map.isBuildingAtPoint(p) || map.isFlagAtPoint(p) || map.isRoadAtPoint(p) || map.isStoneAtPoint(p)) {
                continue;
            }
            
            map.placeTree(p);
        }
        
        /* Manually place forester */
        Forester forester = new Forester(player0, map);

        Utils.occupyBuilding(forester, foresterHut, map);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Verify that the forester stays in the hut */
        int i;
        for (i = 0; i < 200; i++) {
            assertTrue(forester.isInsideBuilding());
            map.stepTime();
        }
    }

    @Test
    public void testForesterDoesNotPlantTreeOnStone() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut, map);

        /* Put trees around the forester hut */
        for (Point p : map.getPointsWithinRadius(foresterHut.getPosition(), 4)) {
            if (p.equals(point1)) {
                continue;
            }
            
            if (map.isBuildingAtPoint(p) || map.isFlagAtPoint(p) || map.isRoadAtPoint(p) || map.isStoneAtPoint(p)) {
                continue;
            }
            
            map.placeStone(p);
        }
        
        /* Manually place forester */
        Forester forester = new Forester(player0, map);

        Utils.occupyBuilding(forester, foresterHut, map);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Wait for the forester to rest */        
        Utils.fastForward(99, map);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Step once and make sure the forester stays in the hut */
        map.stepTime();
        
        assertTrue(forester.isInsideBuilding());
    }

    @Test
    public void testForesterGoesBackToStorageWhenForesterHutIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing forester hut */
        Point point26 = new Point(8, 8);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point26);

        /* Finish construction of the forester hut */
        Utils.constructHouse(foresterHut0, map);

        /* Occupy the forester hut */
        Utils.occupyBuilding(new Forester(player0, map), foresterHut0, map);
        
        /* Destroy the forester hut */
        Worker forester = foresterHut0.getWorker();
        
        assertTrue(forester.isInsideBuilding());
        assertEquals(forester.getPosition(), foresterHut0.getPosition());

        foresterHut0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(FORESTER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(FORESTER), amount + 1);
    }

    @Test
    public void testForesterGoesBackOnToStorageOnRoadsIfPossibleWhenForesterHutIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing forester hut */
        Point point26 = new Point(8, 8);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point26);

        /* Connect the forester hut with the headquarter */
        map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the forester hut */
        Utils.constructHouse(foresterHut0, map);

        /* Occupy the forester hut */
        Utils.occupyBuilding(new Forester(player0, map), foresterHut0, map);
        
        /* Destroy the forester hut */
        Worker forester = foresterHut0.getWorker();
        
        assertTrue(forester.isInsideBuilding());
        assertEquals(forester.getPosition(), foresterHut0.getPosition());

        foresterHut0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), headquarter0.getPosition());
    
        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point p : forester.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test
    public void testProductionInForesterHutCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place forester hut */
        Point point1 = new Point(8, 6);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);
        
        /* Connect the forester hut and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the forester hut */
        Utils.constructHouse(foresterHut0, map);
        
        /* Assign a worker to the forester hut */
        Forester forester = new Forester(player0, map);
        
        Utils.occupyBuilding(forester, foresterHut0, map);
        
        assertTrue(forester.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the forester to leave the forester hut */
        for (int i = 0; i < 300; i++) {
            if (!forester.isInsideBuilding()) {
                break;
            }
        
            map.stepTime();
        }

        assertFalse(forester.isInsideBuilding());
        
        /* Wait for the forester to go back to the forester hut */
        for (int i = 0; i < 300; i++) {
            if (forester.isInsideBuilding()) {
                break;
            }
        
            map.stepTime();
        }

        assertTrue(forester.isInsideBuilding());

        /* Stop production and verify that no tree is planted */
        foresterHut0.stopProduction();
        
        assertFalse(foresterHut0.isProductionEnabled());
        
        for (int i = 0; i < 300; i++) {
            assertTrue(forester.isInsideBuilding());
            
            map.stepTime();
        }
    }

    @Test
    public void testProductionInForesterHutCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place forester hut */
        Point point1 = new Point(8, 6);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);
        
        /* Connect the forester hut and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the forester hut */
        Utils.constructHouse(foresterHut0, map);
        
        /* Assign a worker to the forester hut */
        Forester forester = new Forester(player0, map);
        
        Utils.occupyBuilding(forester, foresterHut0, map);
        
        assertTrue(forester.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the forester to leave the forester hut */
        for (int i = 0; i < 300; i++) {
            if (!forester.isInsideBuilding()) {
                break;
            }
        
            map.stepTime();
        }

        assertFalse(forester.isInsideBuilding());
        
        /* Wait for the forester to go back to the forester hut */
        for (int i = 0; i < 300; i++) {
            if (forester.isInsideBuilding()) {
                break;
            }
        
            map.stepTime();
        }

        assertTrue(forester.isInsideBuilding());

        /* Stop production */
        foresterHut0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(forester.getCargo());
            
            map.stepTime();
        }

        /* Resume production and verify that the forester plants trees again */
        foresterHut0.resumeProduction();

        assertTrue(foresterHut0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            if (!forester.isInsideBuilding()) {
                break;
            }
            
            map.stepTime();
        }

        assertFalse(forester.isInsideBuilding());
    }

    @Test
    public void testAssignedforesterHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place forester hut*/
        Point point1 = new Point(20, 14);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Finish construction of the forester hut */
        Utils.constructHouse(foresterHut0, map);
        
        /* Connect the forester hut with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), foresterHut0.getFlag());

        /* Wait for forester to get assigned and leave the headquarter */
        List<Forester> workers = Utils.waitForWorkersOutsideBuilding(Forester.class, 1, player0, map);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Forester worker = workers.get(0);

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

        /* Place forester hut close to the new border */
        Point point4 = new Point(28, 18);
        ForesterHut foresterHut0 = map.placeBuilding(new ForesterHut(player0), point4);

        /* Finish construction of the forester hut */
        Utils.constructHouse(foresterHut0, map);

        /* Occupy the forester hut */
        Forester worker = Utils.occupyBuilding(new Forester(player0, map), foresterHut0, map);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down*/
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }
}
