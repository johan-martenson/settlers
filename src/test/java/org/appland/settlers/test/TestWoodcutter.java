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
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Storehouse;
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
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
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
    public void testWoodcutterOnlyNeedsTwoPlanksForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing woodcutter */
        Point point22 = new Point(6, 12);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point22);

        /* Deliver two planks */
        Cargo cargo = new Cargo(PLANK, map);

        woodcutter0.putCargo(cargo);
        woodcutter0.putCargo(cargo);

        /* Verify that this is enough to construct the woodcutter */
        for (int i = 0; i < 100; i++) {
            assertTrue(woodcutter0.underConstruction());

            map.stepTime();
        }

        assertTrue(woodcutter0.isReady());
    }

    @Test
    public void testWoodcutterCannotBeConstructedWithOnePlank() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing woodcutter */
        Point point22 = new Point(6, 12);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point22);

        /* Deliver two planks */
        Cargo cargo = new Cargo(PLANK, map);

        woodcutter0.putCargo(cargo);

        /* Verify that this is enough to construct the woodcutter */
        for (int i = 0; i < 500; i++) {
            assertTrue(woodcutter0.underConstruction());

            map.stepTime();
        }

        assertFalse(woodcutter0.isReady());
    }

    @Test
    public void testUnfinishedWoodcutterNeedsNoWoodcutter() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(8, 6);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify the the woodcutter doesn't need any worker when it's under construction */
        assertTrue(woodcutter.underConstruction());
        assertFalse(woodcutter.needsWorker());
    }

    @Test
    public void testFinishedWoodcutterNeedsWoodcutterWorker() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(8, 6);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the woodcutter */
        Utils.constructHouse(woodcutter);

        /* Verify that it needs a worker */
        assertTrue(woodcutter.needsWorker());
    }

    @Test
    public void testWoodcutterIsAssignedToFinishedHouse() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(8, 6);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        /* Finish the woodcutter */
        Utils.constructHouse(woodcutter);

        /* Run game logic twice, once to place courier and once to place woodcutter worker */
        Utils.fastForward(2, map);

        boolean foundWoodcutter = false;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof WoodcutterWorker) {
                foundWoodcutter = true;

                break;
            }
        }

        assertTrue(foundWoodcutter);
    }

    @Test
    public void testWoodcutterIsCreatedFromTools() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(8, 6);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Remove all woodcutter workers and add an axe to the headquarter */
        Utils.adjustInventoryTo(headquarter0, WOODCUTTER_WORKER, 0);
        Utils.adjustInventoryTo(headquarter0, Material.AXE, 1);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        /* Finish the woodcutter */
        Utils.constructHouse(woodcutter);

        /* Run game logic twice, once to place courier and once to place woodcutter worker */
        Utils.fastForward(2, map);

        boolean foundWoodcutter = false;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof WoodcutterWorker) {
                foundWoodcutter = true;

                break;
            }
        }

        assertTrue(foundWoodcutter);
    }

    @Test
    public void testOnlyOneWoodcutterIsAssignedToHouse() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(8, 6);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        /* Construct the forester hut */
        constructHouse(woodcutter);

        /* Run game logic twice, once to place courier and once to place forester */
        Utils.fastForward(2, map);

        int nrWorkers = map.getWorkers().size();

        /* Keep running the game loop and make sure no more workers are allocated */
        for (int i = 0; i < 20; i++) {
            Utils.fastForward(10, map);
        }

        assertEquals(map.getWorkers().size(), nrWorkers);
    }

    @Test
    public void testArrivedWoodcutterRestsInHutAndThenLeaves() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place and grow tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the forester hut */
        constructHouse(woodcutter);

        /* Manually place forester */
        Worker wcWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place and grow tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the forester hut */
        constructHouse(woodcutter);

        /* Manually place forester */
        Worker wcWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarer */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place and grow tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the forester hut */
        constructHouse(woodcutter);

        /* Manually place forester */
        Worker wcWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the forester hut */
        constructHouse(woodcutter);

        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(wcWorker, woodcutter);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);

        /* Wait for the tree to grow */
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        /* Construct the forester hut */
        constructHouse(woodcutter);

        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);

        Utils.occupyBuilding(wcWorker, woodcutter);


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

        assertEquals(wcWorker.getTarget(), woodcutter.getPosition());
        assertTrue(wcWorker.getPlannedPath().contains(woodcutter.getFlag().getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, woodcutter.getPosition());

        assertTrue(wcWorker.isInsideBuilding());
        assertNotNull(wcWorker.getCargo());
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());

        /* Woodcutter leaves the building and puts the cargo on the building's flag */
        map.stepTime();

        assertFalse(wcWorker.isInsideBuilding());
        assertEquals(wcWorker.getTarget(), woodcutter.getFlag().getPosition());
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());

        /* Let the woodcutter reach the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, woodcutter.getFlag().getPosition());

        assertFalse(woodcutter.getFlag().getStackedCargo().isEmpty());
        assertNull(wcWorker.getCargo());
        assertEquals(wcWorker.getTarget(), woodcutter.getPosition());

        Cargo cargo = woodcutter.getFlag().getStackedCargo().get(0);

        assertEquals(cargo.getTarget(), headquarter);

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
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place forester hut (is this needed for the test?) */
        Point point0 = new Point(14, 4);
        Building hut = map.placeBuilding(new ForesterHut(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 4);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place forester */
        Point point4 = new Point(12, 12);
        Building hut2 = map.placeBuilding(new ForesterHut(player0), point4);

        /* Construct the forester hut */
        constructHouse(woodcutter);
        constructHouse(hut);
        constructHouse(hut2);

        /* Place roads */
        Point point5 = new Point(9, 3);

        Road road0 = map.placeRoad(player0, headquarter.getFlag().getPosition(), point5, woodcutter.getFlag().getPosition());
        Road road1 = map.placeAutoSelectedRoad(player0, hut.getFlag(), woodcutter.getFlag());
        Road road2 = map.placeAutoSelectedRoad(player0, hut2.getFlag(), headquarter.getFlag());

        /* Verify that the woodcutter is occupied */
        WoodcutterWorker woodcutterWorker = (WoodcutterWorker) Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter);

        /* Wait for the woodcutter to rest */
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());

        Utils.fastForward(99, map);

        assertTrue(woodcutterWorker.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(woodcutterWorker.isInsideBuilding());

        Point point = woodcutterWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(woodcutterWorker.isTraveling());

        /* Let the woodcutter reach the tree */
        Utils.fastForwardUntilWorkersReachTarget(map, woodcutterWorker);

        assertTrue(woodcutterWorker.isArrived());
        assertTrue(woodcutterWorker.isAt(point));

        map.stepTime();

        assertTrue(woodcutterWorker.isCuttingTree());

        /* Wait for the woodcutter to cut down the tree */
        Utils.fastForward(50, map);

        /* The woodcutter has cut down the tree and goes back via the flag */
        assertFalse(woodcutterWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));

        assertEquals(woodcutterWorker.getTarget(), woodcutter.getPosition());
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());
        assertTrue(woodcutterWorker.getPlannedPath().contains(woodcutter.getFlag().getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, woodcutter.getPosition());

        assertTrue(woodcutterWorker.isInsideBuilding());
        assertNotNull(woodcutterWorker.getCargo());
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());

        /* Woodcutter leaves the building and puts the cargo on the building's flag */
        map.stepTime();

        assertFalse(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getTarget(), woodcutter.getFlag().getPosition());
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());

        /* Let the woodcutter reach the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, woodcutter.getFlag().getPosition());

        /* Verify that the cargo is setup correctly */
        assertFalse(woodcutter.getFlag().getStackedCargo().isEmpty());
        assertNull(woodcutterWorker.getCargo());
        assertEquals(woodcutterWorker.getTarget(), woodcutter.getPosition());

        Cargo cargo = woodcutter.getFlag().getStackedCargo().get(0);

        assertEquals(cargo.getTarget(), headquarter);
        assertEquals(cargo.getNextFlagOrBuilding(), headquarter.getFlag().getPosition());
    }

    @Test
    public void testWoodcutterHutWithoutTreesProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 4);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the forester hut */
        constructHouse(woodcutter);

        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);

        Utils.occupyBuilding(wcWorker, woodcutter);

        assertTrue(wcWorker.isInsideBuilding());
        assertNull(wcWorker.getCargo());

        for (int i = 0; i < 100; i++) {
            map.stepTime();
            assertNull(wcWorker.getCargo());
        }
    }

    @Test
    public void testWoodcutterDoesNotCutSmallOrMediumTrees() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 4);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the woodcutter hut */
        constructHouse(woodcutter);

        /* Place the woodcutter */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);

        Utils.occupyBuilding(wcWorker, woodcutter);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Plant and grow trees */
        Point point2 = new Point(12, 4);
        Tree tree0 = map.placeTree(point2);

        /* Place tree */
        Point point5 = new Point(11, 5);
        Tree tree1 = map.placeTree(point5);

        /* Wait for the tree to grow */
        Utils.fastForwardUntilTreeIsGrown(tree0, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        /* Construct the forester hut */
        constructHouse(woodcutter);

        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);

        Utils.occupyBuilding(wcWorker, woodcutter);

        /* Wait for the woodcutter worker to leave the hut */
        Utils.fastForward(100, map);

        assertFalse(wcWorker.isInsideBuilding());

        assertTrue(wcWorker.isTraveling());
        assertTrue(wcWorker.getPlannedPath().contains(woodcutter.getFlag().getPosition()));

        /* Let the woodcutter reach the tree */
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertTrue(wcWorker.isArrived());

        map.stepTime();

        assertTrue(wcWorker.isCuttingTree());

        /* Wait for the woodcutter to cut down the tree */
        Utils.fastForward(50, map);

        /* The woodcutter has cut down the tree and goes back to the hut */
        assertFalse(wcWorker.isCuttingTree());

        assertEquals(wcWorker.getTarget(), woodcutter.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, woodcutter.getPosition());

        /* Woodcutter enters building but does not store the cargo yet */
        assertTrue(wcWorker.isInsideBuilding());
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());
        assertEquals(woodcutter.getPosition(), woodcutter.getPosition());
        assertNotNull(wcWorker.getCargo());

        /* Woodcutter leaves the building and puts the cargo on the building's flag */
        map.stepTime();

        assertFalse(wcWorker.isInsideBuilding());
        assertEquals(wcWorker.getTarget(), woodcutter.getFlag().getPosition());
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());

        /* Let the woodcutter reach the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, woodcutter.getFlag().getPosition());

        assertFalse(woodcutter.getFlag().getStackedCargo().isEmpty());
        assertNull(wcWorker.getCargo());
        assertEquals(wcWorker.getTarget(), woodcutter.getPosition());

        Cargo cargo = woodcutter.getFlag().getStackedCargo().get(0);

        assertEquals(cargo.getTarget(), headquarter);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(8, 6);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        /* Finish the woodcutter */
        Utils.constructHouse(woodcutter);

        /* Run game logic twice, once to place courier and once to place woodcutter worker */
        Utils.fastForward(2, map);

        WoodcutterWorker wcWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof WoodcutterWorker) {
                wcWorker = (WoodcutterWorker)worker;
            }
        }

        assertNotNull(wcWorker);
        assertEquals(wcWorker.getTarget(), woodcutter.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertEquals(wcWorker.getPosition(), woodcutter.getPosition());
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

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
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Let the woodcutter worker rest */
        Utils.fastForward(100, map);

        /* Wait for the woodcutter worker to go to the tree */
        Worker worker = woodcutter0.getWorker();

        assertTrue(worker.getTarget().equals(tree0.getPosition()) || worker.getTarget().equals(tree1.getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        /* Wait for the woodcutter to cut the tree */
        Utils.fastForward(50, map);

        assertNotNull(worker.getCargo());
        assertEquals(worker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getPosition());

        /* Verify that the woodcutter worker puts the wood cargo at the flag */
        map.stepTime();

        assertEquals(worker.getTarget(), woodcutter0.getFlag().getPosition());
        assertTrue(woodcutter0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(woodcutter0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the woodcutter */
        assertEquals(worker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getPosition());

        /* Let the woodcutter worker rest */
        Utils.fastForward(100, map);

        /* Wait for the woodcutter worker to go to the next tree */
        assertTrue(worker.getTarget().equals(tree0.getPosition()) || worker.getTarget().equals(tree1.getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        /* Wait for the woodcutter to cut the tree */
        Utils.fastForward(50, map);

        assertNotNull(worker.getCargo());

        /* Wait for the woodcutter worker to go back to the woodcutter */
        assertEquals(worker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getPosition());

        /* Verify that the second cargo is put at the flag */
        map.stepTime();

        assertEquals(worker.getTarget(), woodcutter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertEquals(woodcutter0.getFlag().getStackedCargo().size(), 2);
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

        /* Plant and grow trees */
        Point point2 = new Point(10, 8);
        Tree tree0 = map.placeTree(point2);

        Utils.fastForwardUntilTreeIsGrown(tree0, map);

        /* Placing woodcutter */
        Point point26 = new Point(8, 8);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Let the woodcutter worker rest */
        Utils.fastForward(100, map);

        /* Wait for the woodcutter worker to go to the tree */
        Worker worker = woodcutter0.getWorker();

        assertEquals(worker.getTarget(), tree0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, tree0.getPosition());

        /* Wait for the woodcutter to cut the tree */
        Utils.fastForward(50, map);

        assertNotNull(worker.getCargo());

        /* Wait for the woodcutter worker to go back to the woodcutter */
        assertEquals(worker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getPosition());

        /* Verify that the woodcutter worker puts the wood cargo at the flag */
        map.stepTime();

        assertEquals(worker.getTarget(), woodcutter0.getFlag().getPosition());
        assertTrue(woodcutter0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getFlag().getPosition());

        assertNull(worker.getCargo());
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
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), woodcutter0.getFlag().getPosition());
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing woodcutter */
        Point point26 = new Point(8, 8);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Destroy the woodcutter */
        Worker worker = woodcutter0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(WOODCUTTER_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing woodcutter */
        Point point26 = new Point(8, 8);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Connect the woodcutter with the headquarter */
        map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Destroy the woodcutter */
        Worker worker = woodcutter0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point point : worker.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing woodcutter */
        Point point26 = new Point(8, 8);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Connect the woodcutter with the headquarter */
        map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Destroy the woodcutter */
        woodcutter0.tearDown();

        assertTrue(woodcutter0.isBurningDown());

        /* Wait for the woodcutter to stop burning */
        Utils.fastForward(50, map);

        assertTrue(woodcutter0.isDestroyed());

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing woodcutter */
        Point point26 = new Point(8, 8);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing woodcutter */
        Point point26 = new Point(8, 8);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

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
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

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
        Utils.constructHouse(woodcutter);

        /* Assign a worker to the woodcutter */
        WoodcutterWorker worker = new WoodcutterWorker(player0, map);

        Utils.occupyBuilding(worker, woodcutter);

        assertTrue(worker.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the worker to produce wood */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), WOOD);

        /* Wait for the worker to return to the woodcutter hut */
        assertEquals(worker.getTarget(), woodcutter.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter.getPosition());

        /* Wait for the worker to deliver the cargo */
        map.stepTime();

        assertEquals(worker.getTarget(), woodcutter.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter.getFlag().getPosition());

        /* Stop production and verify that no wood is produced */
        woodcutter.stopProduction();

        assertFalse(woodcutter.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

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
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

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
        Utils.constructHouse(woodcutter);

        /* Assign a worker to the woodcutter */
        WoodcutterWorker worker = new WoodcutterWorker(player0, map);

        Utils.occupyBuilding(worker, woodcutter);

        assertTrue(worker.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the worker to produce wood */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), WOOD);

        /* Wait for the worker to return to the woodcutter hut */
        assertEquals(worker.getTarget(), woodcutter.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter.getPosition());

        /* Wait for the worker to deliver the cargo */
        map.stepTime();

        assertEquals(worker.getTarget(), woodcutter.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter.getFlag().getPosition());

        /* Stop production */
        woodcutter.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the woodcutter produces wood again */
        woodcutter.resumeProduction();

        assertTrue(woodcutter.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertNotNull(worker.getCargo());
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
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(20, 14);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        /* Wait for woodcutter worker to get assigned and leave the headquarter */
        List<WoodcutterWorker> workers = Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0);

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
        Point point10 = new Point(70, 70);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(17, 9);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place woodcutter close to the new border */
        Point point4 = new Point(28, 18);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point4);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter */
        WoodcutterWorker worker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
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
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place and grow the tree directly behind the woodcutter */
        Point point2 = new Point(9, 5);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Construct the woodcutter */
        constructHouse(woodcutter);

        /* Manually place woodcutter worker */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(wcWorker, woodcutter);

        /* Wait for the woodcutter to rest */
        Utils.fastForward(100, map);

        assertFalse(wcWorker.isInsideBuilding());
        assertTrue(wcWorker.isTraveling());

        /* Verify that the woodcutter chooses a path that goes via the flag and doesn't go through the house */
        assertTrue(wcWorker.getPlannedPath().contains(woodcutter.getFlag().getPosition()));
        assertTrue(wcWorker.getPlannedPath().lastIndexOf(woodcutter.getPosition()) < 1);

        /* Let the woodcutter reach the tree and start cutting */
        assertEquals(wcWorker.getTarget(), point2);

        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point2));
        assertTrue(wcWorker.isCuttingTree());

        /* Wait for the woodcutter to cut down the tree */
        Utils.fastForward(50, map);

        /* Verify that the woodcutter chooses a path back that goes via the flag */
        assertEquals(wcWorker.getTarget(), woodcutter.getPosition());
        assertTrue(wcWorker.getPlannedPath().contains(woodcutter.getFlag().getPosition()));
        assertTrue(wcWorker.getPlannedPath().contains(woodcutter.getPosition()));
        assertTrue(wcWorker.getPlannedPath().indexOf(woodcutter.getFlag().getPosition()) <
                   wcWorker.getPlannedPath().indexOf(woodcutter.getPosition()));
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
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place and grow the tree directly behind the woodcutter */
        Point point2 = new Point(15, 3);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place stone */
        Point point3 = new Point(13, 3);
        Stone stone0 = map.placeStone(point3);

        /* Construct the woodcutter */
        constructHouse(woodcutter);

        /* Manually place woodcutter worker */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(wcWorker, woodcutter);

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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
        Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0);

        WoodcutterWorker woodcutterWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof WoodcutterWorker) {
                woodcutterWorker = (WoodcutterWorker) worker;
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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
        Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0);

        WoodcutterWorker woodcutterWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof WoodcutterWorker) {
                woodcutterWorker = (WoodcutterWorker) worker;
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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
        Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0);

        WoodcutterWorker woodcutterWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof WoodcutterWorker) {
                woodcutterWorker = (WoodcutterWorker) worker;
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing woodcutter */
        Point point26 = new Point(13, 13);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Place a second storage closer to the woodcutter */
        Point point2 = new Point(7, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the woodcutter */
        Worker woodcutterWorker = woodcutter0.getWorker();

        assertTrue(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(WOODCUTTER_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, storehouse0.getPosition());

        /* Verify that the woodcutter worker is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(WOODCUTTER_WORKER), amount + 1);
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing woodcutter */
        Point point26 = new Point(13, 13);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Place a second storage closer to the woodcutter */
        Point point2 = new Point(7, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

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

        /* Verify that the woodcutter worker is stored correctly in the headquarter */
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing woodcutter */
        Point point26 = new Point(13, 13);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Place a second storage closer to the woodcutter */
        Point point2 = new Point(7, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

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

        /* Verify that the woodcutter worker is stored correctly in the headquarter */
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing woodcutter */
        Point point26 = new Point(13, 13);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Place a second storage closer to the woodcutter */
        Point point2 = new Point(7, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

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

        /* Verify that the woodcutter worker is stored correctly in the headquarter */
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place woodcutter */
        Point point26 = new Point(13, 13);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Place road to connect the headquarter and the woodcutter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getFlag().getPosition());

        /* Tear down the building */
        woodcutter0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
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
        Point point0 = new Point(9, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place and grow the tree */
        Point point1 = new Point(12, 6);
        Tree tree = map.placeTree(point1);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutters */
        Point point2 = new Point(7, 5);
        Point point3 = new Point(13, 5);
        assertNotNull(map.isAvailableHousePoint(player0, point2));
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);
        assertNotNull(map.isAvailableHousePoint(player0, point3));
        Building woodcutter1 = map.placeBuilding(new Woodcutter(player0), point3);

        /* Construct the woodcutters */
        constructHouse(woodcutter0);
        constructHouse(woodcutter1);

        /* Manually place woodcutters */
        WoodcutterWorker wcWorker0 = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);
        WoodcutterWorker wcWorker1 = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter1);

        /* Wait for the woodcutters to leave the buildings and try to cut down the same tree */
        Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 2, player0);

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

        if (!wcWorker1.getPosition().equals(woodcutter1.getPosition())) {
            Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker1, woodcutter1.getPosition());
        }

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter hut */
        Point point1 = new Point(7, 9);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Populate the woodcutter hut */
        Worker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter hut */
        Point point1 = new Point(7, 9);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Populate the woodcutter hut */
        Worker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter hut */
        Point point1 = new Point(7, 9);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Populate the woodcutter hut */
        Worker woodcutterWorker0 = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter hut */
        Point point1 = new Point(7, 9);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 10);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Populate the woodcutter */
        Worker woodcutterWorker0 = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Verify that the hunter hut can produce */
        assertTrue(woodcutter0.canProduce());
    }

    @Test
    public void testWoodcutterReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(6, 12);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Verify that the reported output is correct */
        assertEquals(woodcutter0.getProducedMaterial().length, 1);
        assertEquals(woodcutter0.getProducedMaterial()[0], WOOD);
    }

    @Test
    public void testWoodcutterReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(6, 12);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(woodcutter0.getMaterialNeeded().size(), 1);
        assertTrue(woodcutter0.getMaterialNeeded().contains(PLANK));
        assertEquals(woodcutter0.getTotalAmountNeeded(PLANK), 2);

        for (Material material : Material.values()) {
            if (material == PLANK) {
                continue;
            }

            assertEquals(woodcutter0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testWoodcutterReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(6, 12);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(woodcutter0.getMaterialNeeded().size(), 0);

        for (Material material : Material.values()) {
            assertEquals(woodcutter0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testWoodcutterWaitsWhenFlagIsFull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(16, 6);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place trees */
        Point point2 = new Point(18, 6);
        Point point3 = new Point(19, 7);
        Point point4 = new Point(20, 6);
        Tree tree0 = map.placeTree(point2);
        Tree tree1 = map.placeTree(point3);
        Tree tree2 = map.placeTree(point4);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        /* Wait for the woodcutter to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(woodcutter);
        Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, woodcutter.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the woodcutter waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 600; i++) {
            assertEquals(woodcutter.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        /* Reconnect the woodcutter with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 700; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(courier.getCargo());
            assertEquals(woodcutter.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(woodcutter.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, woodcutter.getWorker(), WOOD);
    }

    @Test
    public void testWoodcutterDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(16, 6);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place trees */
        Point point2 = new Point(18, 6);
        Point point3 = new Point(19, 7);
        Point point4 = new Point(20, 6);
        Tree tree0 = map.placeTree(point2);
        Tree tree1 = map.placeTree(point3);
        Tree tree2 = map.placeTree(point4);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        /* Wait for the woodcutter to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(woodcutter);
        Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, woodcutter.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The woodcutter waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 500; i++) {
            assertEquals(woodcutter.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        /* Reconnect the woodcutter with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 600; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertEquals(woodcutter.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(woodcutter.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, woodcutter.getWorker(), WOOD);

        /* Wait for the worker to put the cargo on the flag */
        Utils.waitForFlagToGetStackedCargo(map, woodcutter.getFlag(), 8);

        assertEquals(woodcutter.getFlag().getStackedCargo().size(), 8);

        /* Verify that the woodcutter doesn't produce anything because the flag is full */
        for (int i = 0; i < 600; i++) {
            assertEquals(woodcutter.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }
    }

    @Test
    public void testWhenWoodDeliveryAreBlockedWoodcutterFillsUpFlagAndThenStops() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place Woodcutter */
        Point point1 = new Point(7, 9);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place road to connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Place trees */
        for (Point point : Utils.getAreaInsideHexagon(6, woodcutter0.getPosition())) {
            try {
                map.placeTree(point);
            } catch (Exception e) {}
        }

        /* Wait for the woodcutter to get constructed and occupied */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(woodcutter0);

        Worker woodcutterWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter0);

        assertTrue(woodcutterWorker0.isInsideBuilding());
        assertEquals(woodcutterWorker0.getHome(), woodcutter0);
        assertEquals(woodcutter0.getWorker(), woodcutterWorker0);

        /* Block storage of wood */
        headquarter0.blockDeliveryOfMaterial(WOOD);

        /* Verify that the woodcutter puts eight wood pieces on the flag and then stops */
        Utils.waitForFlagToGetStackedCargo(map, woodcutter0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker0, woodcutter0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(woodcutter0.getFlag().getStackedCargo().size(), 8);

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), WOOD);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndWoodcutterIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place woodcutter */
        Point point2 = new Point(18, 6);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the woodcutter */
        Road road1 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the woodcutter and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, woodcutter0);

        /* Wait for the woodcutter and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, woodcutter0);

        Worker woodcutterWorker0 = woodcutter0.getWorker();

        assertTrue(woodcutterWorker0.isInsideBuilding());
        assertEquals(woodcutterWorker0.getHome(), woodcutter0);
        assertEquals(woodcutter0.getWorker(), woodcutterWorker0);

        /* Verify that the worker goes to the storage when the woodcutter is torn down */
        headquarter0.blockDeliveryOfMaterial(WOODCUTTER_WORKER);

        woodcutter0.tearDown();

        map.stepTime();

        assertFalse(woodcutterWorker0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker0, woodcutter0.getFlag().getPosition());

        assertEquals(woodcutterWorker0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, woodcutterWorker0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(woodcutterWorker0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndWoodcutterIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place woodcutter */
        Point point2 = new Point(18, 6);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the woodcutter */
        Road road1 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the woodcutter and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, woodcutter0);

        /* Wait for the woodcutter and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, woodcutter0);

        Worker woodcutterWorker0 = woodcutter0.getWorker();

        assertTrue(woodcutterWorker0.isInsideBuilding());
        assertEquals(woodcutterWorker0.getHome(), woodcutter0);
        assertEquals(woodcutter0.getWorker(), woodcutterWorker0);

        /* Verify that the worker goes to the storage off-road when the woodcutter is torn down */
        headquarter0.blockDeliveryOfMaterial(WOODCUTTER_WORKER);

        woodcutter0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(woodcutterWorker0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker0, woodcutter0.getFlag().getPosition());

        assertEquals(woodcutterWorker0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(woodcutterWorker0));
    }

    @Test
    public void testWorkerGoesOutAndBackInWhenSentOutWithoutBlocking() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, WOODCUTTER_WORKER, 1);

        assertEquals(headquarter0.getAmount(WOODCUTTER_WORKER), 1);

        headquarter0.pushOutAll(WOODCUTTER_WORKER);

        for (int i = 0; i < 10; i++) {
            Worker worker = Utils.waitForWorkerOutsideBuilding(WoodcutterWorker.class, player0);

            assertEquals(headquarter0.getAmount(WOODCUTTER_WORKER), 0);
            assertEquals(worker.getPosition(), headquarter0.getPosition());
            assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

            assertEquals(worker.getPosition(), headquarter0.getFlag().getPosition());
            assertEquals(worker.getTarget(), headquarter0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

            assertFalse(map.getWorkers().contains(worker));
        }
    }

    @Test
    public void testPushedOutWorkerWithNowhereToGoWalksAwayAndDies() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, WOODCUTTER_WORKER, 1);

        headquarter0.blockDeliveryOfMaterial(WOODCUTTER_WORKER);
        headquarter0.pushOutAll(WOODCUTTER_WORKER);

        Worker worker = Utils.waitForWorkerOutsideBuilding(WoodcutterWorker.class, player0);

        assertEquals(worker.getPosition(), headquarter0.getPosition());
        assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

        assertEquals(worker.getPosition(), headquarter0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), headquarter0.getPosition());
        assertFalse(worker.isDead());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        assertTrue(worker.isDead());

        for (int i = 0; i < 100; i++) {
            assertTrue(worker.isDead());
            assertTrue(map.getWorkers().contains(worker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    @Test
    public void testWorkerWithNowhereToGoWalksAwayAndDiesWhenHouseIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(7, 9);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place road to connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the woodcutter to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(woodcutter0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter
        */
        headquarter0.blockDeliveryOfMaterial(WOODCUTTER_WORKER);

        Worker worker = woodcutter0.getWorker();

        woodcutter0.tearDown();

        assertEquals(worker.getPosition(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getFlag().getPosition());

        assertEquals(worker.getPosition(), woodcutter0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), woodcutter0.getPosition());
        assertNotEquals(worker.getTarget(), headquarter0.getPosition());
        assertFalse(worker.isDead());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        assertTrue(worker.isDead());

        for (int i = 0; i < 100; i++) {
            assertTrue(worker.isDead());
            assertTrue(map.getWorkers().contains(worker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    @Test
    public void testWorkerGoesAwayAndDiesWhenItReachesTornDownHouseAndStorageIsBlocked() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(7, 9);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place road to connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the woodcutter to get constructed */
        Utils.waitForBuildingToBeConstructed(woodcutter0);

        /* Wait for a woodcutter worker to start walking to the woodcutter */
        WoodcutterWorker woodcutterWorker = Utils.waitForWorkerOutsideBuilding(WoodcutterWorker.class, player0);

        /* Wait for the woodcutter worker to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the woodcutter worker goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(woodcutterWorker.getTarget(), woodcutter0.getPosition());

        headquarter0.blockDeliveryOfMaterial(WOODCUTTER_WORKER);

        woodcutter0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, woodcutter0.getFlag().getPosition());

        assertEquals(woodcutterWorker.getPosition(), woodcutter0.getFlag().getPosition());
        assertNotEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());
        assertFalse(woodcutterWorker.isInsideBuilding());
        assertNull(woodcutter0.getWorker());
        assertNotNull(woodcutterWorker.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, woodcutterWorker.getTarget());

        Point point = woodcutterWorker.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(woodcutterWorker.isDead());
            assertEquals(woodcutterWorker.getPosition(), point);
            assertTrue(map.getWorkers().contains(woodcutterWorker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(woodcutterWorker));
    }
}
