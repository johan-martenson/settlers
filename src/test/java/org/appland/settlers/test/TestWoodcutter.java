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
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.WoodcutterWorker;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Material.WOODCUTTER_WORKER;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Size.MEDIUM;
import static org.appland.settlers.test.Utils.constructHouse;
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
public class TestWoodcutter {


    @Test
    public void testWoodcutterOnlyNeedsTwoPlancksForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing woodcutter */
        Point point22 = new Point(6, 22);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point22);

        /* Deliver two plancks */
        Cargo cargo = new Cargo(PLANCK, map);

        woodcutter0.putCargo(cargo);
        woodcutter0.putCargo(cargo);

        /* Verify that this is enough to construct the woodcutter */
        for (int i = 0; i < 100; i++) {
            assertTrue(woodcutter0.underConstruction());

            map.stepTime();
        }

        assertTrue(woodcutter0.ready());
    }

    @Test
    public void testWoodcutterCannotBeConstructedWithOnePlanck() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing woodcutter */
        Point point22 = new Point(6, 22);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point22);

        /* Deliver two plancks */
        Cargo cargo = new Cargo(PLANCK, map);

        woodcutter0.putCargo(cargo);

        /* Verify that this is enough to construct the woodcutter */
        for (int i = 0; i < 500; i++) {
            assertTrue(woodcutter0.underConstruction());

            map.stepTime();
        }

        assertFalse(woodcutter0.ready());
    }

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
        for (int i = 0; i < 20; i++) {
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
        for (int i = 0; i < 9; i++) {
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
        for (int i = 0; i < 49; i++) {
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

        /* The woodcutter has cut down the tree and goes back via the flag */
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

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
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

        /* Place forester */
        Point point4 = new Point(16, 16);
        Building hut2 = map.placeBuilding(new ForesterHut(player0), point4);

        /* Place roads */
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

        /* The woodcutter has cut down the tree and goes back via the flag */
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
        assertEquals(cargo.getNextFlagOrBuilding(), hq.getFlag().getPosition());
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

        for (int i = 0; i < 100; i++) {
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

        for (int i = 0; i < 9; i++) {
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

        /* Grow tree to medium */
        for (int i = 0; i < 500; i++) {
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

        /* Place woodcutter */
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

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testWoodcutterDoesNotWalkStraightThroughHouse() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place and grow the tree directly behind the woodcutter */
        Point point2 = new Point(9, 5);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Construct the woodcutter */
        constructHouse(wc, map);

        /* Manually place woodcutter worker */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(wcWorker, wc, map);

        /* Wait for the woodcutter to rest */
        Utils.fastForward(100, map);

        assertFalse(wcWorker.isInsideBuilding());
        assertTrue(wcWorker.isTraveling());

        /* Verify that the woodcutter chooses a path that goes via the flag
           and doesn't go through the house */
        assertTrue(wcWorker.getPlannedPath().contains(wc.getFlag().getPosition()));
        assertTrue(wcWorker.getPlannedPath().lastIndexOf(wc.getPosition()) < 1);

        /* Let the woodcutter reach the tree and start cutting */
        assertEquals(wcWorker.getTarget(), point2);

        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point2));
        assertTrue(wcWorker.isCuttingTree());

        /* Wait for the woodcutter to cut down the tree */
        Utils.fastForward(50, map);

        /* Verify that the woodcutter chooses a path back that goes via the flag */
        assertEquals(wcWorker.getTarget(), wc.getPosition());
        assertTrue(wcWorker.getPlannedPath().contains(wc.getFlag().getPosition()));
        assertTrue(wcWorker.getPlannedPath().contains(wc.getPosition()));
        assertTrue(wcWorker.getPlannedPath().indexOf(wc.getFlag().getPosition()) <
                   wcWorker.getPlannedPath().indexOf(wc.getPosition()));
    }

    @Test
    public void testWoodcutterDoesNotWalkStraightThroughStone() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place and grow the tree directly behind the woodcutter */
        Point point2 = new Point(15, 3);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place stone */
        Point point3 = new Point(13, 3);
        Stone stone0 = map.placeStone(point3);

        /* Construct the woodcutter */
        constructHouse(wc, map);

        /* Manually place woodcutter worker */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(wcWorker, wc, map);

        /* Wait for the woodcutter to rest */
        Utils.fastForward(100, map);

        assertFalse(wcWorker.isInsideBuilding());
        assertTrue(wcWorker.isTraveling());

        /* Verify that the woodcutter chooses to walk around the stone */
        assertFalse(wcWorker.getPlannedPath().contains(point3));
    }

    @Test
    public void testWoodcutterWorkerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

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

        /* Placing woodcutter */
        Point point2 = new Point(14, 4);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter0.getFlag());

        /* Wait for the woodcutter worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0, map);

        WoodcutterWorker woodcutterWorker = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof WoodcutterWorker) {
                woodcutterWorker = (WoodcutterWorker) w;
            }
        }

        assertNotNull(woodcutterWorker);
        assertEquals(woodcutterWorker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the woodcutter worker has started walking */
        assertFalse(woodcutterWorker.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the woodcutter worker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, flag0.getPosition());

        assertEquals(woodcutterWorker.getPosition(), flag0.getPosition());

        /* Verify that the woodcutter worker returns to the headquarter when it reaches the flag */
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWoodcutterWorkerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

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

        /* Placing woodcutter */
        Point point2 = new Point(14, 4);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter0.getFlag());

        /* Wait for the woodcutter worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0, map);

        WoodcutterWorker woodcutterWorker = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof WoodcutterWorker) {
                woodcutterWorker = (WoodcutterWorker) w;
            }
        }

        assertNotNull(woodcutterWorker);
        assertEquals(woodcutterWorker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the woodcutter worker has started walking */
        assertFalse(woodcutterWorker.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the woodcutter worker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, flag0.getPosition());

        assertEquals(woodcutterWorker.getPosition(), flag0.getPosition());

        /* Verify that the woodcutter worker continues to the final flag */
        assertEquals(woodcutterWorker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, woodcutter0.getFlag().getPosition());

        /* Verify that the woodcutter worker goes out to woodcutter instead of going directly back */
        assertNotEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testWoodcutterWorkerReturnsToStorageIfWoodcutterIsDestroyed() throws Exception {

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

        /* Placing woodcutter */
        Point point2 = new Point(14, 4);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter0.getFlag());

        /* Wait for the woodcutter worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0, map);

        WoodcutterWorker woodcutterWorker = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof WoodcutterWorker) {
                woodcutterWorker = (WoodcutterWorker) w;
            }
        }

        assertNotNull(woodcutterWorker);
        assertEquals(woodcutterWorker.getTarget(), woodcutter0.getPosition());

        /* Wait for the woodcutter worker to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, flag0.getPosition());

        map.stepTime();

        /* See that the woodcutter worker has started walking */
        assertFalse(woodcutterWorker.isExactlyAtPoint());

        /* Tear down the woodcutter */
        woodcutter0.tearDown();

        /* Verify that the woodcutter worker continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, woodcutter0.getFlag().getPosition());

        assertEquals(woodcutterWorker.getPosition(), woodcutter0.getFlag().getPosition());

        /* Verify that the woodcutter worker goes back to storage */
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testWoodcutterWorkerGoesOffroadBackToClosestStorageWhenWoodcutterIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing woodcutter */
        Point point26 = new Point(17, 17);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0, map);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0, map);

        /* Place a second storage closer to the woodcutter */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the woodcutter */
        Worker woodcutterWorker = woodcutter0.getWorker();

        assertTrue(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getTarget(), storage0.getPosition());

        int amount = storage0.getAmount(WOODCUTTER_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, storage0.getPosition());

        /* Verify that the woodcutterWorker is stored correctly in the headquarter */
        assertEquals(storage0.getAmount(WOODCUTTER_WORKER), amount + 1);
    }

    @Test
    public void testWoodcutterWorkerReturnsOffroadAndAvoidsBurningStorageWhenWoodcutterIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing woodcutter */
        Point point26 = new Point(17, 17);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0, map);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0, map);

        /* Place a second storage closer to the woodcutter */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

        /* Destroy the woodcutter */
        Worker woodcutterWorker = woodcutter0.getWorker();

        assertTrue(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(WOODCUTTER_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());

        /* Verify that the woodcutterWorker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(WOODCUTTER_WORKER), amount + 1);
    }

    @Test
    public void testWoodcutterWorkerReturnsOffroadAndAvoidsDestroyedStorageWhenWoodcutterIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing woodcutter */
        Point point26 = new Point(17, 17);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0, map);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0, map);

        /* Place a second storage closer to the woodcutter */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storage0, map);

        /* Destroy the woodcutter */
        Worker woodcutterWorker = woodcutter0.getWorker();

        assertTrue(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(WOODCUTTER_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());

        /* Verify that the woodcutterWorker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(WOODCUTTER_WORKER), amount + 1);
    }

    @Test
    public void testWoodcutterWorkerReturnsOffroadAndAvoidsUnfinishedStorageWhenWoodcutterIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing woodcutter */
        Point point26 = new Point(17, 17);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0, map);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0, map);

        /* Place a second storage closer to the woodcutter */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Destroy the woodcutter */
        Worker woodcutterWorker = woodcutter0.getWorker();

        assertTrue(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(WOODCUTTER_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());

        /* Verify that the woodcutterWorker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(WOODCUTTER_WORKER), amount + 1);
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

        /* Place woodcutter */
        Point point26 = new Point(17, 17);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Place road to connect the headquarter and the woodcutter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0, map);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0, map).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getFlag().getPosition());

        /* Tear down the building */
        woodcutter0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquarter instead of entering
        */
        assertEquals(worker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testTwoWoodcuttersTryToCutDownSameTree() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place and grow the tree */
        Point point1 = new Point(10, 4);
        Tree tree = map.placeTree(point1);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutters */
        Point point2 = new Point(7, 5);
        Point point3 = new Point(11, 5);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);
        Building woodcutter1 = map.placeBuilding(new Woodcutter(player0), point3);

        /* Construct the woodcutters */
        constructHouse(woodcutter0, map);
        constructHouse(woodcutter1, map);

        /* Manually place woodcutters */
        WoodcutterWorker wcWorker0 = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0, map);
        WoodcutterWorker wcWorker1 = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter1, map);

        /* Wait for the woodcutters to leave the buildings and try to cut down the same tree */
        Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 2, player0, map);

        assertEquals(wcWorker0.getTarget(), tree.getPosition());
        assertEquals(wcWorker1.getTarget(), tree.getPosition());

        /* Let the woodcutters reach the tree and start cutting */
        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker0, tree.getPosition());

        assertEquals(wcWorker0.getPosition(), tree.getPosition());
        assertEquals(wcWorker1.getPosition(), tree.getPosition());

        /* Wait for one of them to cut down the tree */
        assertTrue(wcWorker0.isCuttingTree() || wcWorker1.isCuttingTree());

        /* Wait for the woodcutter to finish cutting the tree */
        for (int i = 0; i < 1000; i++) {

            if (!map.isTreeAtPoint(tree.getPosition())) {
                break;
            }

            assertTrue(wcWorker0.isCuttingTree() || wcWorker1.isCuttingTree());
            assertTrue(map.isTreeAtPoint(tree.getPosition()));

            map.stepTime();
        }

        assertFalse(map.isTreeAtPoint(tree.getPosition()));

        /* Verify that one of the woodcutters got the wood and both are going back */
        assertTrue(wcWorker0.getCargo() == null || wcWorker1.getCargo() == null);
        assertTrue(wcWorker0.getCargo() != null || wcWorker1.getCargo() != null);
        assertTrue((wcWorker0.getCargo() != null && wcWorker0.getCargo().getMaterial().equals(WOOD)) ||
                   (wcWorker1.getCargo() != null && wcWorker1.getCargo().getMaterial().equals(WOOD)));

        /* Verify that both woodcutters go back home */
        assertEquals(wcWorker0.getTarget(), woodcutter0.getPosition());
        assertEquals(wcWorker1.getTarget(), woodcutter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker0, woodcutter0.getPosition());

        assertTrue(wcWorker0.isInsideBuilding());
        assertTrue(wcWorker1.isInsideBuilding());
    }

    @Test
    public void testWoodcutterHutWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter hut */
        Point point1 = new Point(7, 9);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0, map);

        /* Populate the woodcutter hut */
        Worker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0, map);

        assertTrue(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getHome(), woodcutter0);
        assertEquals(woodcutter0.getWorker(), woodcutterWorker);

        /* Verify that the productivity is 0% when the woodcutter hut doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(woodcutter0.getFlag().getStackedCargo().isEmpty());
            assertNull(woodcutterWorker.getCargo());
            assertEquals(woodcutter0.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testWoodcutterHutWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter hut */
        Point point1 = new Point(7, 9);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0, map);

        /* Populate the woodcutter hut */
        Worker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0, map);

        assertTrue(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getHome(), woodcutter0);
        assertEquals(woodcutter0.getWorker(), woodcutterWorker);

        /* Connect the woodcutter hut with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        /* Place a lot of trees on the map */
        Utils.plantTreesOnPoints(map.getPointsWithinRadius(woodcutter0.getPosition(), 4), map);

        /* Wait for the trees to grow up */
        Utils.fastForward(300, map);

        /* Make the woodcutter take down some trees with plenty of trees available */
        for (int i = 0; i < 1000; i++) {
            map.stepTime();

            /* Fill up with more trees */
            Utils.plantTreesOnPoints(map.getPointsWithinRadius(woodcutter0.getPosition(), 4), map);
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(woodcutter0.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            assertEquals(woodcutter0.getProductivity(), 100);

            /* Fill up with more trees */
            Utils.plantTreesOnPoints(map.getPointsWithinRadius(woodcutter0.getPosition(), 4), map);
        }
    }

    @Test
    public void testWoodcutterHutLosesProductivityWhenResourcesRunOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter hut */
        Point point1 = new Point(7, 9);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0, map);

        /* Populate the woodcutter hut */
        Worker woodcutterWorker0 = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0, map);

        assertTrue(woodcutterWorker0.isInsideBuilding());
        assertEquals(woodcutterWorker0.getHome(), woodcutter0);
        assertEquals(woodcutter0.getWorker(), woodcutterWorker0);

        /* Connect the woodcutter hut with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        /* Place a lot of trees on the map */
        Utils.plantTreesOnPoints(map.getPointsWithinRadius(woodcutter0.getPosition(), 4), map);

        /* Wait for the trees to grow up */
        Utils.fastForward(300, map);

        /* Make the woodcutter take down trees until the trees are gone */
        for (int i = 0; i < 5000; i++) {

            map.stepTime();

            if (map.getTrees().isEmpty()) {
                break;
            }
        }

        assertEquals(woodcutter0.getProductivity(), 100);

        /* Verify that the productivity goes down when resources run out */
        for (int i = 0; i < 2000; i++) {
            map.stepTime();
        }

        assertEquals(woodcutter0.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedWoodcutterHutHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter hut */
        Point point1 = new Point(7, 9);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0, map);

        /* Verify that the unoccupied woodcutter hut is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(woodcutter0.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testWoodcutterCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 10);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0, map);

        /* Populate the woodcutter */
        Worker woodcutterWorker0 = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0, map);

        /* Verify that the hunter hut can produce */
        assertTrue(woodcutter0.canProduce());
    }
}
