/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Forester;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Storehouse;
import org.appland.settlers.model.Tile;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static org.appland.settlers.model.Material.FORESTER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Tile.Vegetation.GRASS;
import static org.appland.settlers.model.Tile.Vegetation.MOUNTAIN;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestForesterHut {


    @Test
    public void testForesterHutOnlyNeedsTwoPlanksForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing forester hut */
        Point point22 = new Point(6, 12);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point22);

        /* Deliver two planks */
        Cargo cargo = new Cargo(PLANK, map);

        foresterHut0.putCargo(cargo);
        foresterHut0.putCargo(cargo);

        /* Verify that this is enough to construct the forester hut */
        for (int i = 0; i < 100; i++) {
            assertTrue(foresterHut0.underConstruction());

            map.stepTime();
        }

        assertTrue(foresterHut0.isReady());
    }

    @Test
    public void testForesterHutCannotBeConstructedWithOnePlank() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing forester hut */
        Point point22 = new Point(6, 12);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point22);

        /* Deliver two planks */
        Cargo cargo = new Cargo(PLANK, map);

        foresterHut0.putCargo(cargo);

        /* Verify that this is enough to construct the forester hut */
        for (int i = 0; i < 500; i++) {
            assertTrue(foresterHut0.underConstruction());

            map.stepTime();
        }

        assertFalse(foresterHut0.isReady());
    }

    @Test
    public void testConstructForester() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(7, 9);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        assertTrue(foresterHut0.underConstruction());
        assertFalse(foresterHut0.needsWorker());

        /* Connect the forester with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        /* Verify that the forester is unoccupied when it's newly constructed */
        assertTrue(foresterHut0.needsWorker());

        /* Verify that the forester hut requires a worker */
        assertTrue(foresterHut0.needsWorker());

        Forester forester = new Forester(null, null);

        /* Assign worker */
        foresterHut0.assignWorker(forester);

        assertFalse(foresterHut0.needsWorker());
        assertEquals(foresterHut0.getWorker(), forester);
    }

    @Test
    public void testPromiseWorkerToUnfinishedForester() {
        ForesterHut foresterHut = new ForesterHut(null);

        assertTrue(foresterHut.underConstruction());

        try {
            foresterHut.promiseWorker(new Forester(null, null));

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testAssignWorkerToUnfinishedForester() {
        ForesterHut foresterHut = new ForesterHut(null);

        assertTrue(foresterHut.underConstruction());

        try {
            foresterHut.assignWorker(new Forester(null, null));

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testAssignWorkerTwice() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(7, 9);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        Utils.constructHouse(foresterHut);

        foresterHut.assignWorker(new Forester(null, null));

        try {
            foresterHut.assignWorker(new Forester(null, null));

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPromiseWorkerTwice() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(7, 9);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        Utils.constructHouse(foresterHut);

        foresterHut.promiseWorker(new Forester(null, null));

        try {
            foresterHut.promiseWorker(new Forester(null, null));

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testForesterHutIsNotMilitary() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(7, 9);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        assertTrue(foresterHut0.underConstruction());
        assertFalse(foresterHut0.needsWorker());

        /* Connect the forester with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        assertFalse(foresterHut0.isMilitaryBuilding());
        assertEquals(foresterHut0.getNumberOfHostedMilitary(), 0);
        assertEquals(foresterHut0.getMaxHostedMilitary(), 0);
    }

    @Test
    public void testForesterHutUnderConstructionNotNeedsWorker() {
        ForesterHut foresterHut = new ForesterHut(null);

        assertFalse(foresterHut.needsWorker());
    }

    @Test
    public void testForesterIsAssignedToForesterHut() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create single player game */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Finish the forester hut */
        Utils.constructHouse(foresterHut);

        /* Run game logic twice, once to place courier and once to place forester */
        Utils.fastForward(2, map);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Forester.class);
    }

    @Test
    public void testForesterIsCreatedFromShovel() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create single player game */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all foresters and place shovels in the headquarter */
        Utils.adjustInventoryTo(headquarter, FORESTER, 0);
        Utils.adjustInventoryTo(headquarter, Material.SHOVEL, 1);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Finish the forester hut */
        Utils.constructHouse(foresterHut);

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

        /* Create single player game */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Run game logic twice, once to place courier and once to place forester */
        Utils.fastForward(2, map);

        assertEquals(map.getWorkers().size(), 3);

        /* Keep running the game loop and make sure no more workers are allocated */
        Utils.fastForward(200, map);

        assertEquals(map.getWorkers().size(), 3);
    }

    @Test
    public void testArrivedForesterRestsInHutAndThenLeaves() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        assertTrue(forester.isInsideBuilding());

        /* Run the game logic 99 times and make sure the forester stays in the hut */
        for (int i = 0; i < 99; i++) {
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

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

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

        for (int i = 0; i < 19; i++) {
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

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        assertTrue(forester.isInsideBuilding());
        assertNull(forester.getCargo());

        /* Verify that the forester doesn't produce anything */
        for (int i = 0; i < 100; i++) {
            map.stepTime();
            assertNull(forester.getCargo());
            assertTrue(foresterHut.getFlag().getStackedCargo().isEmpty());
        }
    }

    @Test
    public void testForesterStaysInsideWhenThereAreNoSpotsAvailable() throws Exception {

        /* Create a new game map with a single player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Put trees around the forester hut */
        for (Point point : map.getPointsWithinRadius(foresterHut.getPosition(), 20)) {
            if (point.equals(point1)) {
                continue;
            }

            if (map.isBuildingAtPoint(point) ||
                map.isFlagAtPoint(point)     ||
                map.isRoadAtPoint(point)     ||
                map.isStoneAtPoint(point)    ||
                !map.isWithinMap(point)) {
                continue;
            }

            map.placeTree(point);
        }

        /* Manually place forester */
        Forester forester = new Forester(player0, map);

        Utils.occupyBuilding(forester, foresterHut);

        assertTrue(forester.isInsideBuilding());

        /* Verify that the forester stays in the hut */
        for (int i = 0; i < 200; i++) {
            assertTrue(forester.isInsideBuilding());
            map.stepTime();
        }
    }

    @Test
    public void testForesterAvoidsUnreachableSpots() throws Exception {

        /* Create a new game map with a single player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Create a lake with an island inside */
        for (Point point : map.getPointsWithinRadius(point1, 4)) {
            Utils.surroundPointWithVegetation(point, Tile.Vegetation.WATER, map);
        }

        Utils.surroundPointWithVegetation(point1, GRASS, map);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Put trees around the forester hut */
        for (Point point : map.getPointsWithinRadius(foresterHut.getPosition(), 4)) {
            if (point.equals(point1)) {
                continue;
            }

            if (map.isBuildingAtPoint(point)  ||
                    map.isFlagAtPoint(point)  ||
                    map.isRoadAtPoint(point)  ||
                    map.isStoneAtPoint(point) ||
                    map.getTerrain().isInWater(point)) {
                continue;
            }

            map.placeTree(point);
        }

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        assertTrue(forester.isInsideBuilding());

        /* Verify that the forester stays in the hut */
        for (int i = 0; i < 200; i++) {
            assertTrue(forester.isInsideBuilding());
            map.stepTime();
        }
    }

    @Test
    public void testForesterDoesNotPlantTreesInWater() throws Exception {

        /* Create a new game map with a single player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Create a lake with an island inside */
        for (Point point : map.getPointsWithinRadius(point1, 10)) {
            Utils.surroundPointWithVegetation(point, Tile.Vegetation.WATER, map);
        }

        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.WATER, map);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Put trees around the forester hut */
        for (Point point : map.getPointsWithinRadius(foresterHut.getPosition(), 4)) {
            if (point.equals(point1)) {
                continue;
            }

            if (map.isBuildingAtPoint(point)  ||
                    map.isFlagAtPoint(point)  ||
                    map.isRoadAtPoint(point)  ||
                    map.isStoneAtPoint(point) ||
                    map.getTerrain().isInWater(point)) {
                continue;
            }

            map.placeTree(point);
        }

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        assertTrue(forester.isInsideBuilding());

        /* Verify that the forester stays in the hut */
        for (int i = 0; i < 200; i++) {
            assertTrue(forester.isInsideBuilding());
            map.stepTime();
        }
    }

    @Test
    public void testForesterDoesNotPlantTreeOnStone() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Put trees around the forester hut */
        for (Point point : map.getPointsWithinRadius(foresterHut.getPosition(), 4)) {
            if (point.equals(point1)) {
                continue;
            }

            if (map.isBuildingAtPoint(point) || map.isFlagAtPoint(point) || map.isRoadAtPoint(point) || map.isStoneAtPoint(point)) {
                continue;
            }

            map.placeStone(point);
        }

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        assertTrue(forester.isInsideBuilding());

        /* Wait for the forester to rest */
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester stays in the hut */
        map.stepTime();

        assertTrue(forester.isInsideBuilding());
    }

    @Test
    public void testForesterDoesNotPlantTreeOnMountain() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Create a small mountain */
        Point point4 = new Point(8, 16);
        Point point5 = new Point(11, 17);
        Point point6 = new Point(14, 16);
        Utils.surroundPointWithVegetation(point4, MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point5, MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point6, MOUNTAIN, map);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 14);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Put stones around the forester hut but not on the mountain */
        for (Point point : map.getPointsWithinRadius(foresterHut.getPosition(), 10)) {
            if (point.equals(point1)) {
                continue;
            }

            if (map.isBuildingAtPoint(point) ||
                map.isFlagAtPoint(point)     ||
                map.isRoadAtPoint(point)     ||
                map.isStoneAtPoint(point)    ||
                map.getTerrain().isOnMountain(point)) {
                continue;
            }

            map.placeTree(point);
        }

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing forester hut */
        Point point26 = new Point(8, 8);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point26);

        /* Finish construction of the forester hut */
        Utils.constructHouse(foresterHut0);

        /* Occupy the forester hut */
        Utils.occupyBuilding(new Forester(player0, map), foresterHut0);

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
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing forester hut */
        Point point1 = new Point(8, 8);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarter */
        map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the forester hut */
        Utils.constructHouse(foresterHut0);

        /* Occupy the forester hut */
        Worker forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut0);

        /* Destroy the forester hut */
        assertTrue(forester.isInsideBuilding());
        assertEquals(forester.getPosition(), foresterHut0.getPosition());

        foresterHut0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point point : forester.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testProductionInForesterHutCanBeStopped() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(8, 6);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the forester hut */
        Utils.constructHouse(foresterHut0);

        /* Assign a worker to the forester hut */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut0);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(8, 6);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the forester hut */
        Utils.constructHouse(foresterHut0);

        /* Assign a worker to the forester hut */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut0);

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
    public void testAssignedForesterHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create single player game */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(20, 14);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Finish construction of the forester hut */
        Utils.constructHouse(foresterHut0);

        /* Connect the forester hut with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), foresterHut0.getFlag());

        /* Wait for forester to get assigned and leave the headquarter */
        List<Forester> workers = Utils.waitForWorkersOutsideBuilding(Forester.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Forester worker = workers.get(0);

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with three players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);
        Player player2 = new Player("Player 2", RED);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        /* Create single player game choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 2's headquarter */
        Headquarter headquarter2 = new Headquarter(player2);
        Point point10 = new Point(70, 70);
        map.placeBuilding(headquarter2, point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
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

        /* Place forester hut close to the new border */
        Point point4 = new Point(28, 18);
        ForesterHut foresterHut0 = map.placeBuilding(new ForesterHut(player0), point4);

        /* Finish construction of the forester hut */
        Utils.constructHouse(foresterHut0);

        /* Occupy the forester hut */
        Forester worker = Utils.occupyBuilding(new Forester(player0, map), foresterHut0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testForesterReturnsHomeWithoutPlantingTreeIfAFlagIsPlacedThereWhilePlanting() throws Exception {

        /* Create a game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place the forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        /* Wait for the forester to pick a spot to plant a tree where a flag
           can be placed
        */
        for (int i = 0; i < 10000; i++) {

            Point spot = forester.getTarget();

            if (spot != null && map.isAvailableFlagPoint(player0, spot)) {
                break;
            }

            map.stepTime();
        }

        Point point = forester.getTarget();

        assertTrue(map.isAvailableFlagPoint(player0, forester.getTarget()));
        assertTrue(forester.isTraveling());

        /* Wait for the forester to reach the spot for the tree */
        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());
        assertEquals(forester.getPosition(), point);

        /* Put a flag on the spot where the forester is planting the tree */
        map.placeFlag(player0, forester.getPosition());

        /* Wait until the forester stops planting and verify that it goes back
           to the forester hut without planting a tree
        */
        for (int i = 0; i < 200; i++) {
            if (!forester.isPlanting()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(forester.isPlanting());
        assertFalse(map.isTreeAtPoint(point));

        /* Verify that the forester goes back home */
        assertEquals(forester.getTarget(), foresterHut.getPosition());
        assertTrue(forester.isTraveling());

        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isInsideBuilding());
        assertFalse(map.isTreeAtPoint(point));
    }

    @Test
    public void testForesterReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

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

        /* Placing forester hut */
        Point point2 = new Point(14, 4);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, foresterHut0.getFlag());

        /* Wait for the forester to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Forester.class, 1, player0);

        Forester forester = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Forester) {
                forester = (Forester) worker;
            }
        }

        assertNotNull(forester);
        assertEquals(forester.getTarget(), foresterHut0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the forester has started walking */
        assertFalse(forester.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the forester continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, flag0.getPosition());

        assertEquals(forester.getPosition(), flag0.getPosition());

        /* Verify that the forester returns to the headquarter when it reaches the flag */
        assertEquals(forester.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getPosition());
    }

    @Test
    public void testForesterContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

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

        /* Placing forester hut */
        Point point2 = new Point(14, 4);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, foresterHut0.getFlag());

        /* Wait for the forester to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Forester.class, 1, player0);

        Forester forester = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Forester) {
                forester = (Forester) worker;
            }
        }

        assertNotNull(forester);
        assertEquals(forester.getTarget(), foresterHut0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the forester has started walking */
        assertFalse(forester.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the forester continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, flag0.getPosition());

        assertEquals(forester.getPosition(), flag0.getPosition());

        /* Verify that the forester continues to the final flag */
        assertEquals(forester.getTarget(), foresterHut0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, foresterHut0.getFlag().getPosition());

        /* Verify that the forester goes out to forester instead of going directly back */
        assertNotEquals(forester.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testForesterReturnsToStorageIfForesterHutIsDestroyed() throws Exception {

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

        /* Placing forester hut */
        Point point2 = new Point(14, 4);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, foresterHut0.getFlag());

        /* Wait for the forester to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Forester.class, 1, player0);

        Forester forester = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Forester) {
                forester = (Forester) worker;
            }
        }

        assertNotNull(forester);
        assertEquals(forester.getTarget(), foresterHut0.getPosition());

        /* Wait for the forester to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, flag0.getPosition());

        map.stepTime();

        /* See that the forester has started walking */
        assertFalse(forester.isExactlyAtPoint());

        /* Tear down the forester hut */
        foresterHut0.tearDown();

        /* Verify that the forester continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, foresterHut0.getFlag().getPosition());

        assertEquals(forester.getPosition(), foresterHut0.getFlag().getPosition());

        /* Verify that the forester goes back to storage */
        assertEquals(forester.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testForesterGoesOffroadBackToClosestStorageWhenForesterHutIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing forester hut */
        Point point1 = new Point(17, 17);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Finish construction of the forester hut */
        Utils.constructHouse(foresterHut0);

        /* Occupy the forester hut */
        Utils.occupyBuilding(new Forester(player0, map), foresterHut0);

        /* Place a second storage closer to the forester hut */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the forester hut */
        Worker forester = foresterHut0.getWorker();

        assertTrue(forester.isInsideBuilding());
        assertEquals(forester.getPosition(), foresterHut0.getPosition());

        foresterHut0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(FORESTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, storehouse0.getPosition());

        /* Verify that the forester is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(FORESTER), amount + 1);
    }

    @Test
    public void testForesterReturnsOffroadAndAvoidsBurningStorageWhenForesterHutIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing forester hut */
        Point point1 = new Point(17, 17);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Finish construction of the forester hut */
        Utils.constructHouse(foresterHut0);

        /* Occupy the forester hut */
        Utils.occupyBuilding(new Forester(player0, map), foresterHut0);

        /* Place a second storage closer to the forester hut */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

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

        /* Verify that the forester is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(FORESTER), amount + 1);
    }

    @Test
    public void testForesterReturnsOffroadAndAvoidsDestroyedStorageWhenForesterHutIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing forester hut */
        Point point1 = new Point(17, 17);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Finish construction of the forester hut */
        Utils.constructHouse(foresterHut0);

        /* Occupy the forester hut */
        Utils.occupyBuilding(new Forester(player0, map), foresterHut0);

        /* Place a second storage closer to the forester hut */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

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

        /* Verify that the forester is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(FORESTER), amount + 1);
    }

    @Test
    public void testForesterReturnsOffroadAndAvoidsUnfinishedStorageWhenForesterHutIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing forester hut */
        Point point1 = new Point(17, 17);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Finish construction of the forester hut */
        Utils.constructHouse(foresterHut0);

        /* Occupy the forester hut */
        Utils.occupyBuilding(new Forester(player0, map), foresterHut0);

        /* Place a second storage closer to the forester hut */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

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

        /* Verify that the forester is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(FORESTER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(17, 17);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Place road to connect the headquarter and the forester hut */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), foresterHut0.getFlag());

        /* Finish construction of the forester hut */
        Utils.constructHouse(foresterHut0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Forester.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, foresterHut0.getFlag().getPosition());

        /* Tear down the building */
        foresterHut0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquarter instead of entering
        */
        assertEquals(worker.getTarget(), foresterHut0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, foresterHut0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testTwoForestersThatTryToPlantOnSameSpotResultInOneTreeAndBothGoBack() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester huts */
        Point point1 = new Point(9, 5);
        Point point2 = new Point(13, 5);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);
        Building foresterHut1 = map.placeBuilding(new ForesterHut(player0), point2);

        /* Construct the forester huts */
        constructHouse(foresterHut0);
        constructHouse(foresterHut1);

        /* Manually place foresters */
        Forester forester0 = Utils.occupyBuilding(new Forester(player0, map), foresterHut0);
        Forester forester1 = Utils.occupyBuilding(new Forester(player0, map), foresterHut1);

        /* Fill the whole map with trees but leave one free spot */
        Point point3 = new Point(12, 4);

        for (Point point4 : player0.getDiscoveredLand()) {

            /* Place on all points except for one */
            if (point4.equals(point3)) {
                continue;
            }

            try {
                map.placeTree(point4);
            } catch (Exception e) {}
        }

        /* Wait for the foresters to get out of their huts */
        Utils.waitForWorkersOutsideBuilding(Forester.class, 2, player0);

        /* Verify that both foresters go to the free point but only one plants */
        assertEquals(forester0.getTarget(), point3);
        assertEquals(forester1.getTarget(), point3);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester0, point3);

        assertTrue(forester0.isPlanting() || forester1.isPlanting());

        /* Verify that a tree is planted and both foresters go back */
        Utils.waitForTreeToGetPlanted(map, point3);

        assertTrue(map.isTreeAtPoint(point3));
        assertEquals(forester0.getTarget(), foresterHut0.getPosition());
        assertEquals(forester1.getTarget(), foresterHut1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester0, foresterHut0.getPosition());
    }

    @Test
    public void testForesterHutCannotProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(7, 9);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Finish construction of the forester hut */
        Utils.constructHouse(foresterHut0);

        /* Populate the forester hut */
        Worker forester0 = Utils.occupyBuilding(new Forester(player0, map), foresterHut0);

        /* Verify that the forester hut can produce */
        assertFalse(foresterHut0.canProduce());
    }

    @Test
    public void testForesterHutReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(6, 12);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        Utils.constructHouse(foresterHut0);

        /* Verify that the reported output is correct */
        assertEquals(foresterHut0.getProducedMaterial().length, 0);
    }

    @Test
    public void testForesterHutReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(6, 12);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(foresterHut0.getMaterialNeeded().size(), 1);
        assertTrue(foresterHut0.getMaterialNeeded().contains(PLANK));
        assertEquals(foresterHut0.getTotalAmountNeeded(PLANK), 2);

        for (Material material : Material.values()) {
            if (material == PLANK) {
                continue;
            }

            assertEquals(foresterHut0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testForesterHutReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(6, 12);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        Utils.constructHouse(foresterHut0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(foresterHut0.getMaterialNeeded().size(), 0);

        for (Material material : Material.values()) {
            assertEquals(foresterHut0.getTotalAmountNeeded(material), 0);
        }
    }
}
