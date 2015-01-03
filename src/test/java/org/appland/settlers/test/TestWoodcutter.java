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
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Material.WOODCUTTER_WORKER;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Size;
import static org.appland.settlers.model.Size.MEDIUM;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.WoodcutterWorker;
import org.appland.settlers.model.Worker;
import static org.appland.settlers.test.Utils.constructHouse;
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
public class TestWoodcutter {
    
    @Test
    public void testUnfinishedWoodcutterNeedsNoWoodcutter() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);
        
        Point point1 = new Point(8, 6);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(wc.underConstruction());

        assertFalse(wc.needsWorker());
    }

    @Test
    public void testFinishedWoodcutterNeedsWoodcutterWorker() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);
        
        Point point1 = new Point(8, 6);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);

        Utils.constructHouse(wc, map);
        
        assertTrue(wc.needsWorker());
    }
    
    @Test
    public void testWoodcutterIsAssignedToFinishedHouse() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(8, 6);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);

        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the woodcutter */
        Utils.constructHouse(wc, map);
        
        /* Run game logic twice, once to place courier and once to place woodcutter worker */
        Utils.fastForward(2, map);

        boolean foundWoodcutter = false;
        for (Worker w : map.getWorkers()) {
            if (w instanceof WoodcutterWorker) {
                foundWoodcutter = true;
            }
        }
        
        assertTrue(foundWoodcutter);    
    }
    
    @Test
    public void testOnlyOneWoodcutterIsAssignedToHouse() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(8, 6);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);

        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Construct the forester hut */
        constructHouse(wc, map);
        
        /* Run game logic twice, once to place courier and once to place forester */
        Utils.fastForward(2, map);

        int nrWorkers = map.getWorkers().size();

        /* Keep running the gameloop and make sure no more workers are allocated */
        int i;
        for (i = 0; i < 20; i++) {
            Utils.fastForward(10, map);
        }

        assertEquals(map.getWorkers().size(), nrWorkers);
    }

    @Test
    public void testArrivedWoodcutterRestsInHutAndThenLeaves() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place and grow tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the forester hut */
        constructHouse(wc, map);
        
        /* Manually place forester */        
        Worker wcWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), wc, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Run the game logic 99 times and make sure the forester stays in the hut */
        int i;
        for (i = 0; i < 9; i++) {
            assertTrue(wcWorker.isInsideBuilding());
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());
    }
    
    @Test
    public void testWoodcutterFindsSpotToCutDownTree() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place and grow tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the forester hut */
        constructHouse(wc, map);
        
        /* Manually place forester */
        Worker wcWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), wc, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Wait for woodcutter worker to leave the hut */
        Utils.fastForward(99, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());    

        Point point = wcWorker.getTarget();
        assertNotNull(point);
        
        assertEquals(point, point2);
        assertTrue(map.isTreeAtPoint(point));
        assertTrue(wcWorker.isTraveling());
        assertFalse(map.isBuildingAtPoint(point));
        assertFalse(map.isRoadAtPoint(point));
        assertFalse(map.isFlagAtPoint(point));
    }

    @Test
    public void testWoodcutterReachesPointToCutDownTree() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place and grow tree */
        Point point2 = new Point(12, 4);        
        Tree tree = map.placeTree(point2);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);
        
        /* Construct the forester hut */
        constructHouse(wc, map);
        
        /* Manually place forester */
        Worker wcWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), wc, map);

        
        /* Wait for woodcutter worker to rest */
        Utils.fastForward(99, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());    

        Point point = wcWorker.getTarget();

        assertEquals(wcWorker.getTarget(), point2);
        assertTrue(wcWorker.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        assertEquals(wcWorker.getPosition(), point);
        assertFalse(wcWorker.isTraveling());
        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));
    }

    @Test
    public void testWoodcutterCutsDownTree() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);
        
        /* Construct the forester hut */
        constructHouse(wc, map);
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(wcWorker, wc, map);


        /* Wait for the woodcutter to rest */        
        Utils.fastForward(99, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());

        Point point = wcWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(wcWorker.isTraveling());
        
        /* Let the woodcutter reach the tree and start cutting */
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));

        map.stepTime();
        
        assertTrue(wcWorker.isCuttingTree());
        assertNull(wcWorker.getCargo());
        
        /* Wait for the woodcutter to finish cutting the tree */
        int i;
        for (i = 0; i < 49; i++) {
            assertTrue(wcWorker.isCuttingTree());
            assertTrue(map.isTreeAtPoint(point));
            map.stepTime();
        }

        /* Verify that the woodcutter stopped cutting */
        assertFalse(wcWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));
        assertNotNull(wcWorker.getCargo());
        assertEquals(wcWorker.getCargo().getMaterial(), WOOD);
    }

    @Test
    public void testWoodcutterReturnsAndStoresWoodAsCargo() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Point point3 = new Point(6, 4);
        Building hq = map.placeBuilding(new Headquarter(player0), point3);

        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);        

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);
        
        Point point4 = new Point(9, 3);
        
        Road road0 = map.placeRoad(player0, hq.getFlag().getPosition(), point4, wc.getFlag().getPosition());
        
        /* Construct the forester hut */
        constructHouse(wc, map);
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);

        Utils.occupyBuilding(wcWorker, wc, map);

        
        /* Wait for the woodcutter to rest */        
        Utils.fastForward(99, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());    

        Point point = wcWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(wcWorker.isTraveling());
        
        /* Let the woodcutter reach the tree */
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));

        map.stepTime();
        
        assertTrue(wcWorker.isCuttingTree());
        
        /* Wait for the woodcutter to cut down the tree */
        Utils.fastForward(50, map);
        
        /* The woodcutter has cut down the tree and goes back via the flag*/
        assertFalse(wcWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));
        assertNotNull(wcWorker.getCargo());
        
        assertEquals(wcWorker.getTarget(), wc.getPosition());
        assertTrue(wcWorker.getPlannedPath().contains(wc.getFlag().getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, wc.getPosition());
                
        assertTrue(wcWorker.isInsideBuilding());
        assertNotNull(wcWorker.getCargo());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        /* Woodcutter leaves the building and puts the cargo on the building's flag */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());
        assertEquals(wcWorker.getTarget(), wc.getFlag().getPosition());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        /* Let the woodcuttter reach the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, wc.getFlag().getPosition());
        
        assertFalse(wc.getFlag().getStackedCargo().isEmpty());
        assertNull(wcWorker.getCargo());
        assertEquals(wcWorker.getTarget(), wc.getPosition());
        
        Cargo cargo = wc.getFlag().getStackedCargo().get(0);
        
        assertEquals(cargo.getTarget(), hq);
        
        /* Let the woodcutter go back to the hut */
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        map.stepTime();
        
        /* Verify that the woodcutter remains in the hut */
        assertTrue(wcWorker.isInsideBuilding());
        
        Utils.fastForward(99, map);
        
        assertTrue(wcWorker.isInsideBuilding());
    }
    
        @Test
    public void testWoodCargoIsCorrect() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Point point3 = new Point(6, 4);
        Building hq = map.placeBuilding(new Headquarter(player0), point3);
        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place forester hut (is this needed for the test?) */
        Point point0 = new Point(14, 4);
        Building hut = map.placeBuilding(new ForesterHut(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);
        
        Point point4 = new Point(2, 4);
        Building hut2 = map.placeBuilding(new ForesterHut(player0), point4);
        
        Point point5 = new Point(9, 3);
        
        Road road0 = map.placeRoad(player0, hq.getFlag().getPosition(), point5, wc.getFlag().getPosition());
        Road road1 = map.placeAutoSelectedRoad(player0, hut.getFlag(), wc.getFlag());
        Road road2 = map.placeAutoSelectedRoad(player0, hut2.getFlag(), hq.getFlag());
        
        /* Construct the forester hut */
        constructHouse(wc, map);
        constructHouse(hut, map);
        constructHouse(hut2, map);
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(wcWorker, wc, map);

        
        /* Wait for the woodcutter to rest */        
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        Utils.fastForward(99, map);
        
        assertTrue(wcWorker.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());

        Point point = wcWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(wcWorker.isTraveling());
        
        /* Let the woodcutter reach the tree */
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));

        map.stepTime();
        
        assertTrue(wcWorker.isCuttingTree());
        
        /* Wait for the woodcutter to cut down the tree */
        Utils.fastForward(50, map);
        
        /* The woodcutter has cut down the tree and goes back via the flag*/
        assertFalse(wcWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));
        
        assertEquals(wcWorker.getTarget(), wc.getPosition());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        assertTrue(wcWorker.getPlannedPath().contains(wc.getFlag().getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, wc.getPosition());
        
        assertTrue(wcWorker.isInsideBuilding());
        assertNotNull(wcWorker.getCargo());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        /* Woodcutter leaves the building and puts the cargo on the building's flag */
        map.stepTime();

        assertFalse(wcWorker.isInsideBuilding());
        assertEquals(wcWorker.getTarget(), wc.getFlag().getPosition());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        /* Let the woodcuttter reach the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, wc.getFlag().getPosition());
        
        /* Verify that the cargo is setup correctly */
        assertFalse(wc.getFlag().getStackedCargo().isEmpty());
        assertNull(wcWorker.getCargo());
        assertEquals(wcWorker.getTarget(), wc.getPosition());
        
        Cargo cargo = wc.getFlag().getStackedCargo().get(0);
        
        assertEquals(cargo.getTarget(), hq);

        Road wcToHqRoad = map.getRoad(wc.getFlag().getPosition(), hq.getFlag().getPosition());

        assertEquals(cargo.getNextStep(), wcToHqRoad.getWayPoints().get(1));
    }
    
    @Test
    public void testWoodcutterHutWithoutTreesProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);
        
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the forester hut */
        
        constructHouse(wc, map);
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);

        Utils.occupyBuilding(wcWorker, wc, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        assertNull(wcWorker.getCargo());

        int i;
        for (i = 0; i < 100; i++) {
            map.stepTime();
            assertNull(wcWorker.getCargo());
        }
    }

    @Test
    public void testWoodcutterStaysInHouseWhenNoTreeIsAvailable() {
        // TODO
    }

    @Test
    public void testWoodcutterDoesNotCutSmallOrMediumTrees() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);
        
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the woodcutter hut */
        constructHouse(wc, map);
        
        /* Place the woodcutter */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);

        Utils.occupyBuilding(wcWorker, wc, map);
        
        /* Run the game logic 99 times and make sure the forester stays in the hut */

        int i;
        for (i = 0; i < 9; i++) {
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        
        assertEquals(tree.getSize(), Size.SMALL);
        
        /* Step once and make sure the forester stays in the hut */
        map.stepTime();
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Grow tree to medium*/
        for (i = 0; i < 500; i++) {
            map.stepTime();
            
            if (tree.getSize() == MEDIUM) {
                break;
            }
        }

        assertEquals(tree.getSize(), MEDIUM);

        /* Step once and make sure the forester stays in the hut */
        
        map.stepTime();
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Grow the tree to large */
        Utils.fastForwardUntilTreeIsGrown(tree, map);
        
        /* Step time and make sure the forester leaves the hut */
        
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());
    }
    
    @Test
    public void testWoodcutterGoesOutToCutTreesSeveralTimes() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point3 = new Point(6, 4);
        Building hq = map.placeBuilding(new Headquarter(player0), point3);

        /* Plant and grow trees */
        Point point2 = new Point(12, 4);
        Tree tree0 = map.placeTree(point2);

        Point point5 = new Point(11, 5);
        Tree tree1 = map.placeTree(point5);
        
        Utils.fastForwardUntilTreeIsGrown(tree0, map);
        
        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);
        
        Point point4 = new Point(9, 3);
        
        Road road0 = map.placeRoad(player0, hq.getFlag().getPosition(), point4, wc.getFlag().getPosition());
        

        /* Construct the forester hut */
        constructHouse(wc, map);
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);

        Utils.occupyBuilding(wcWorker, wc, map);
        
        /* Wait for the woodcutter worker to leave the hut */
        Utils.fastForward(100, map);
        
        assertFalse(wcWorker.isInsideBuilding());    

        assertTrue(wcWorker.isTraveling());
        assertTrue(wcWorker.getPlannedPath().contains(wc.getFlag().getPosition()));

        /* Let the woodcutter reach the tree */
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        assertTrue(wcWorker.isArrived());

        map.stepTime();
        
        assertTrue(wcWorker.isCuttingTree());
        
        /* Wait for the woodcutter to cut down the tree */
        Utils.fastForward(50, map);
        
        /* The woodcutter has cut down the tree and goes back to the hut */
        assertFalse(wcWorker.isCuttingTree());
        
        assertEquals(wcWorker.getTarget(), wc.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, wc.getPosition());

        /* Woodcutter enters building but does not store the cargo yet */
        assertTrue(wcWorker.isInsideBuilding());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        assertEquals(wc.getPosition(), wc.getPosition());
        assertNotNull(wcWorker.getCargo());
        
        /* Woodcutter leaves the building and puts the cargo on the building's flag */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());
        assertEquals(wcWorker.getTarget(), wc.getFlag().getPosition());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        /* Let the woodcuttter reach the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, wc.getFlag().getPosition());
        
        assertFalse(wc.getFlag().getStackedCargo().isEmpty());
        assertNull(wcWorker.getCargo());
        assertEquals(wcWorker.getTarget(), wc.getPosition());
        
        Cargo cargo = wc.getFlag().getStackedCargo().get(0);
        
        assertEquals(cargo.getTarget(), hq);
        
        /* Let the woodcutter go back to the hut */
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertTrue(wcWorker.isInsideBuilding());
        
        /* Let the woodcutter rest */
        Utils.fastForward(99, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Verify that the woodcutter goes out again */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());
        
    }

    @Test
    public void testPositionIsCorrectWhenWoodcutterEntersHut() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(8, 6);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);

        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the woodcutter */
        Utils.constructHouse(wc, map);
        
        /* Run game logic twice, once to place courier and once to place woodcutter worker */
        Utils.fastForward(2, map);
        
        WoodcutterWorker wcWorker = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof WoodcutterWorker) {
                wcWorker = (WoodcutterWorker)w;
            }
        }
    
        assertNotNull(wcWorker);
        assertEquals(wcWorker.getTarget(), wc.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        assertEquals(wcWorker.getPosition(), wc.getPosition());
    }

    @Test
    public void testWoodcutterWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Plant and grow trees */
        Point point2 = new Point(10, 8);
        Tree tree0 = map.placeTree(point2);

        Point point3 = new Point(11, 7);
        Tree tree1 = map.placeTree(point3);

        Utils.fastForwardUntilTreeIsGrown(tree0, map);

        /* Placing woodcutter */
        Point point26 = new Point(8, 8);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0, map);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0, map);

        /* Let the woodcutter worker rest */
        Utils.fastForward(100, map);

        /* Wait for the woodcutter worker to go to the tree */
        Worker ww = woodcutter0.getWorker();

        assertTrue(ww.getTarget().equals(tree0.getPosition()) || ww.getTarget().equals(tree1.getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, ww.getTarget());

        /* Wait for the woodcutter to cut the tree */
        Utils.fastForward(50, map);
        
        assertNotNull(ww.getCargo());
        assertEquals(ww.getTarget(), woodcutter0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, woodcutter0.getPosition());

        /* Verify that the woodcutter worker puts the wood cargo at the flag */
        map.stepTime();

        assertEquals(ww.getTarget(), woodcutter0.getFlag().getPosition());
        assertTrue(woodcutter0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, woodcutter0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(woodcutter0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the woodcutter */
        assertEquals(ww.getTarget(), woodcutter0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, woodcutter0.getPosition());

        /* Let the woodcutter worker rest */
        Utils.fastForward(100, map);

        /* Wait for the woodcutter worker to go to the next tree */
        assertTrue(ww.getTarget().equals(tree0.getPosition()) || ww.getTarget().equals(tree1.getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, ww.getTarget());

        /* Wait for the woodcutter to cut the tree */
        Utils.fastForward(50, map);

        assertNotNull(ww.getCargo());

        /* Wait for the woodcutter worker to go back to the woodcutter */
        assertEquals(ww.getTarget(), woodcutter0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, woodcutter0.getPosition());

        /* Verify that the second cargo is put at the flag */
        map.stepTime();
        
        assertEquals(ww.getTarget(), woodcutter0.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, woodcutter0.getFlag().getPosition());
        
        assertNull(ww.getCargo());
        assertEquals(woodcutter0.getFlag().getStackedCargo().size(), 2);
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

        /* Plant and grow trees */
        Point point2 = new Point(10, 8);
        Tree tree0 = map.placeTree(point2);

        Utils.fastForwardUntilTreeIsGrown(tree0, map);

        /* Placing woodcutter */
        Point point26 = new Point(8, 8);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0, map);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0, map);

        /* Let the woodcutter worker rest */
        Utils.fastForward(100, map);

        /* Wait for the woodcutter worker to go to the tree */
        Worker ww = woodcutter0.getWorker();

        assertEquals(ww.getTarget(), tree0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, tree0.getPosition());

        /* Wait for the woodcutter to cut the tree */
        Utils.fastForward(50, map);

        assertNotNull(ww.getCargo());
        
        /* Wait for the woodcutter worker to go back to the woodcutter */
        assertEquals(ww.getTarget(), woodcutter0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, woodcutter0.getPosition());

        /* Verify that the woodcutter worker puts the wood cargo at the flag */
        map.stepTime();
        
        assertEquals(ww.getTarget(), woodcutter0.getFlag().getPosition());
        assertTrue(woodcutter0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, woodcutter0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(woodcutter0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = woodcutter0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), woodcutter0.getFlag().getPosition());
    
        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());
    
        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);
    
        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(woodcutter0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));
    
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
    
        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), woodcutter0.getFlag().getPosition());
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);
        
        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());
        
        int amount = headquarter0.getAmount(WOOD);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());
        
        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(WOOD), amount + 1);
    }

    @Test
    public void testWoodcutterWorkerGoesBackToStorageWhenWoodcutterIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing woodcutter */
        Point point26 = new Point(8, 8);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0, map);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0, map);
        
        /* Destroy the woodcutter */
        Worker ww = woodcutter0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(WOODCUTTER_WORKER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());

        /* Verify that the woodcutter worker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(WOODCUTTER_WORKER), amount + 1);
    }

    @Test
    public void testWoodcutterWorkerGoesBackOnToStorageOnRoadsIfPossibleWhenWoodcutterIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing woodcutter */
        Point point26 = new Point(8, 8);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Connect the woodcutter with the headquarter */
        map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0, map);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0, map);
        
        /* Destroy the woodcutter */
        Worker ww = woodcutter0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point p : ww.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test
    public void testDestroyedWoodcutterIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing woodcutter */
        Point point26 = new Point(8, 8);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Connect the woodcutter with the headquarter */
        map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0, map);

        /* Destroy the woodcutter */
        woodcutter0.tearDown();

        assertTrue(woodcutter0.burningDown());

        /* Wait for the woodcutter to stop burning */
        Utils.fastForward(50, map);
        
        assertTrue(woodcutter0.destroyed());
        
        /* Wait for the woodcutter to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), woodcutter0);
            
            map.stepTime();
        }
        
        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(woodcutter0));
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

        /* Placing woodcutter */
        Point point26 = new Point(8, 8);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);
        
        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0, map);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(woodcutter0.getPosition(), woodcutter0.getFlag().getPosition()));
        
        map.removeFlag(woodcutter0.getFlag());

        assertNull(map.getRoad(woodcutter0.getPosition(), woodcutter0.getFlag().getPosition()));
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

        /* Placing woodcutter */
        Point point26 = new Point(8, 8);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);
        
        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0, map);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(woodcutter0.getPosition(), woodcutter0.getFlag().getPosition()));
        
        woodcutter0.tearDown();

        assertNull(map.getRoad(woodcutter0.getPosition(), woodcutter0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInWoodcutterCanBeStopped() throws Exception {
        
        /* Create gamemap */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Plant and grow trees */
        Point point12 = new Point(10, 8);
        Tree tree0 = map.placeTree(point12);

        Utils.fastForwardUntilTreeIsGrown(tree0, map);
        
        /* Place woodcutter */
        Point point1 = new Point(8, 6);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);
        
        /* Connect the woodcutter with the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the woodcutter */
        Utils.constructHouse(woodcutter, map);
        
        /* Assign a worker to the woodcutter */
        WoodcutterWorker ww = new WoodcutterWorker(player0, map);
        
        Utils.occupyBuilding(ww, woodcutter, map);
        
        assertTrue(ww.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the worker to produce wood */
        Utils.fastForwardUntilWorkerProducesCargo(map, ww);

        assertEquals(ww.getCargo().getMaterial(), WOOD);

        /* Wait for the worker to return to the woodcutter hut */
        assertEquals(ww.getTarget(), woodcutter.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, woodcutter.getPosition());

        /* Wait for the worker to deliver the cargo */
        map.stepTime();

        assertEquals(ww.getTarget(), woodcutter.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, woodcutter.getFlag().getPosition());

        /* Stop production and verify that no wood is produced */
        woodcutter.stopProduction();

        assertFalse(woodcutter.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(ww.getCargo());
            
            map.stepTime();
        }
    }

    @Test
    public void testProductionInWoodcutterCanBeResumed() throws Exception {

        /* Create gamemap */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Plant and grow trees */
        Point point12 = new Point(10, 8);
        Tree tree0 = map.placeTree(point12);

        Point point13 = new Point(8, 8);
        Tree tree1 = map.placeTree(point13);

        Utils.fastForwardUntilTreeIsGrown(tree0, map);
        
        /* Place woodcutter */
        Point point1 = new Point(8, 6);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);
        
        /* Connect the woodcutter with the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the woodcutter */
        Utils.constructHouse(woodcutter, map);
        
        /* Assign a worker to the woodcutter */
        WoodcutterWorker ww = new WoodcutterWorker(player0, map);
        
        Utils.occupyBuilding(ww, woodcutter, map);
        
        assertTrue(ww.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the worker to produce wood */
        Utils.fastForwardUntilWorkerProducesCargo(map, ww);

        assertEquals(ww.getCargo().getMaterial(), WOOD);

        /* Wait for the worker to return to the woodcutter hut */
        assertEquals(ww.getTarget(), woodcutter.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, woodcutter.getPosition());

        /* Wait for the worker to deliver the cargo */
        map.stepTime();

        assertEquals(ww.getTarget(), woodcutter.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, woodcutter.getFlag().getPosition());

        /* Stop production */
        woodcutter.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(ww.getCargo());
            
            map.stepTime();
        }

        /* Resume production and verify that the woodcutter produces wood again */
        woodcutter.resumeProduction();

        assertTrue(woodcutter.isProductionEnabled());
        
        Utils.fastForwardUntilWorkerProducesCargo(map, ww);

        assertNotNull(ww.getCargo());
    }

    @Test
    public void testAssignedWoodcutterWorkerHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place woodcutter*/
        Point point1 = new Point(20, 14);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0, map);
        
        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        /* Wait for woodcutter worker to get assigned and leave the headquarter */
        List<WoodcutterWorker> workers = Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0, map);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        WoodcutterWorker worker = workers.get(0);

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
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

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

        /* Place woodcutter close to the new border */
        Point point4 = new Point(28, 18);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point4);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0, map);

        /* Occupy the woodcutter */
        WoodcutterWorker worker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0, map);

        /* Connect the woodcutter to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down*/
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }
}
