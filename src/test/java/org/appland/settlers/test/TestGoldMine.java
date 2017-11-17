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
import org.appland.settlers.model.GoldMine;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Miner;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.MINER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.SMALL;
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
public class TestGoldMine {

    @Test
    public void testGoldMineOnlyNeedsFourPlanksForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put a small mountain on the map */
        Point point22 = new Point(6, 22);
        Utils.surroundPointWithMountain(point22, map);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point22);

        /* Deliver four planks */
        Cargo plankCargo = new Cargo(PLANK, map);

        goldMine0.putCargo(plankCargo);
        goldMine0.putCargo(plankCargo);
        goldMine0.putCargo(plankCargo);
        goldMine0.putCargo(plankCargo);

        /* Verify that this is enough to construct the gold mine */
        for (int i = 0; i < 100; i++) {
            assertTrue(goldMine0.underConstruction());

            map.stepTime();
        }

        assertTrue(goldMine0.ready());
    }

    @Test
    public void testGoldMineCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put a small mountain on the map */
        Point point22 = new Point(6, 22);
        Utils.surroundPointWithMountain(point22, map);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point22);

        /* Deliver two plank and three stone */
        Cargo plankCargo = new Cargo(PLANK, map);

        goldMine0.putCargo(plankCargo);
        goldMine0.putCargo(plankCargo);
        goldMine0.putCargo(plankCargo);

        /* Verify that this is not enough to construct the gold mine */
        for (int i = 0; i < 500; i++) {
            assertTrue(goldMine0.underConstruction());

            map.stepTime();
        }

        assertFalse(goldMine0.ready());
    }

    @Test
    public void testConstructGoldMine() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 14);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a goldmine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        assertTrue(mine.underConstruction());

        Utils.constructHouse(mine, map);

        assertTrue(mine.ready());
    }

    @Test
    public void testGoldmineIsNotMilitary() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 14);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Verify that the mine is not a military building */
        assertFalse(mine.isMilitaryBuilding());

        Utils.constructHouse(mine, map);

        assertFalse(mine.isMilitaryBuilding());
    }

    @Test
    public void testGoldmineUnderConstructionNotNeedsMiner() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 14);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Verify that the unfinished mine does not need a worker */
        assertFalse(mine.needsWorker());
    }

    @Test
    public void testFinishedGoldmineNeedsMiner() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 14);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        Utils.constructHouse(mine, map);

        /* Verify that the finished mine needs a worker */
        assertTrue(mine.needsWorker());
    }

    @Test
    public void testMinerIsAssignedToFinishedGoldmine() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a headquarter */
        Point hqPoint = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Place a road between the headquarter and the goldmine */
        Road road0 = map.placeAutoSelectedRoad(player0, building0.getFlag(), mine.getFlag());

        /* Construct the mine */
        constructHouse(mine, map);

        assertTrue(mine.ready());

        /* Run game logic twice, once to place courier and once to place miner */
        Utils.fastForward(2, map);

        assertEquals(map.getWorkers().size(), 3);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Miner.class);

        /* Keep running the game loop and make sure no more workers are allocated */
        Utils.fastForward(200, map);

        assertEquals(map.getWorkers().size(), 3);
    }

    @Test
    public void testCanPlaceMineOnPointSurroundedByMountain() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a headquarter */
        Point hqPoint = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        assertEquals(map.getBuildings().size(), 2);
    }

    @Test
    public void testArrivedMinerRests() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Construct the gold mine */
        constructHouse(mine, map);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine, map);

        assertTrue(miner.isInsideBuilding());

        /* Run the game logic 99 times and make sure the miner stays in the house */
        for (int i = 0; i < 99; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());
            assertFalse(miner.isMining());
            map.stepTime();
        }

        assertNull(miner.getCargo());
        assertFalse(miner.isMining());
        assertTrue(miner.isInsideBuilding());
    }

    @Test
    public void testMinerMinesGold() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Construct the gold mine */
        constructHouse(mine, map);

        /* Deliver food to the miner */
        Cargo food = new Cargo(BREAD, map);
        mine.putCargo(food);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine, map);

        assertTrue(miner.isInsideBuilding());

        /* Wait for the miner to rest */
        Utils.fastForward(100, map);

        /* Verify that the miner mines for gold */
        int amountGold = map.getAmountOfMineralAtPoint(GOLD, point0);

        for (int i = 0; i < 50; i++) {
            assertTrue(miner.isMining());
            map.stepTime();
        }

        /* Verify that the miner finishes mining on time and has gold */
        assertFalse(miner.isMining());
        assertFalse(miner.isInsideBuilding());
        assertNotNull(miner.getCargo());
        assertEquals(miner.getCargo().getMaterial(), GOLD);
        assertTrue(map.getAmountOfMineralAtPoint(GOLD, point0) < amountGold);
    }

    @Test
    public void testGoldmineGoesToFlagWithCargoAndBack() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Building building0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, building0.getFlag(), mine.getFlag());

        /* Construct the gold mine */
        constructHouse(mine, map);

        /* Deliver food to the miner */
        Cargo food = new Cargo(BREAD, map);
        mine.putCargo(food);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine, map);

        assertTrue(miner.isInsideBuilding());

        /* Wait for the miner to rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to mine gold */
        Utils.fastForward(50, map);

        /* Verify that the miner leaves the gold at the flag */
        assertFalse(miner.isMining());
        assertFalse(miner.isInsideBuilding());
        assertNotNull(miner.getCargo());
        assertEquals(miner.getTarget(), mine.getFlag().getPosition());
        assertTrue(mine.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getFlag().getPosition());

        assertNull(miner.getCargo());
        assertFalse(mine.getFlag().getStackedCargo().isEmpty());
        assertEquals(miner.getTarget(), mine.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getPosition());

        assertTrue(miner.isInsideBuilding());
    }

    @Test
    public void testCanNotPlaceMineOnGrass() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        Point point0 = new Point(2, 2);
        try {
            map.placeBuilding(new GoldMine(player0), point0);
            assertFalse(true);
        } catch (Exception e) {}

        assertEquals(map.getBuildings().size(), 1);
    }

    @Test
    public void testGoldmineRunsOutOfGold() throws Exception {

        /* Create a new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, SMALL, map);

        /* Remove all gold but one */
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountOfMineralAtPoint(GOLD, point0) > 1) {
                map.mineMineralAtPoint(GOLD, point0);
            }
        }

        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Building building0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, building0.getFlag(), mine.getFlag());

        /* Construct the gold mine */
        constructHouse(mine, map);

        /* Deliver food to the miner */
        Utils.deliverCargo(mine, BREAD, map);
        Utils.deliverCargo(mine, FISH, map);
        Utils.deliverCargo(mine, MEAT, map);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine, map);

        assertTrue(miner.isInsideBuilding());

        /* Wait for the miner to rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to mine gold */
        assertFalse(mine.outOfNaturalResources());

        Utils.fastForward(50, map);

        /* Wait for the miner to leave the gold at the flag */
        assertEquals(miner.getTarget(), mine.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getFlag().getPosition());

        assertNull(miner.getCargo());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getPosition());

        assertTrue(miner.isInsideBuilding());

        /* Verify that the gold is gone and that the miner gets no gold */
        assertEquals(map.getAmountOfMineralAtPoint(GOLD, point0), 0);

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());

            map.stepTime();
        }

        /* Verify that the mine is out of natural resources */
        assertTrue(mine.outOfNaturalResources());
    }

    @Test
    public void testGoldmineWithoutGoldProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Building building0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, building0.getFlag(), mine.getFlag());

        /* Construct the gold mine */
        constructHouse(mine, map);

        /* Deliver food to the miner */
        Cargo food = new Cargo(BREAD, map);
        mine.putCargo(food);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine, map);

        assertTrue(miner.isInsideBuilding());

        /* Wait for the miner to rest */
        Utils.fastForward(100, map);

        /* Verify that there is no gold and that the miner gets no gold */
        assertEquals(map.getAmountOfMineralAtPoint(GOLD, point0), 0);

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testGoldmineWithoutFoodProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Building building0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Construct the gold mine */
        constructHouse(mine, map);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine, map);

        assertTrue(miner.isInsideBuilding());

        /* Wait for the miner to rest */
        Utils.fastForward(100, map);

        /* Verify that the miner gets no gold */

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testMiningConsumesFood() throws Exception {

        /* Start new game with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Construct the gold mine */
        constructHouse(mine, map);

        /* Deliver food to the miner */
        Cargo food = new Cargo(BREAD, map);
        mine.putCargo(food);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine, map);

        assertTrue(miner.isInsideBuilding());

        /* Wait for the miner to rest */
        Utils.fastForward(100, map);

        /* Verify that the miner mines for gold */
        assertEquals(mine.getAmount(BREAD), 1);

        Utils.fastForward(50, map);

        /* Verify that the miner consumed the bread */
        assertEquals(mine.getAmount(BREAD), 0);
    }

    @Test
    public void testGoldmineCanConsumeAllTypesOfFood() throws Exception {

        /* Start new game with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Construct the gold mine */
        constructHouse(mine, map);

        /* Deliver food of all types to the miner */
        assertTrue(mine.needsMaterial(FISH));
        assertTrue(mine.needsMaterial(MEAT));
        assertTrue(mine.needsMaterial(BREAD));

        mine.putCargo(new Cargo(FISH, map));
        mine.putCargo(new Cargo(MEAT, map));
        mine.putCargo(new Cargo(BREAD, map));

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine, map);

        assertTrue(miner.isInsideBuilding());

        /* Mine three times and verify that the miner consumed all food */
        for (int i = 0; i < 3; i++) {

            /* Wait for the miner to produce ore */
            Utils.fastForwardUntilWorkerCarriesCargo(map, miner, GOLD);

            /* Wait for the miner to leave the ore at the flag */
            assertEquals(miner.getTarget(), mine.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getFlag().getPosition());

            assertNull(miner.getCargo());
        }

        assertEquals(mine.getAmount(BREAD), 0);
        assertEquals(mine.getAmount(FISH), 0);
        assertEquals(mine.getAmount(MEAT), 0);
    }

    @Test
    public void testGoldMineWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Finish construction of the gold mine */
        Utils.constructHouse(goldMine0, map);

        /* Occupy the gold mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0, map);

        /* Deliver material to the gold mine */
        Cargo fishCargo = new Cargo(FISH, map);

        goldMine0.putCargo(fishCargo);
        goldMine0.putCargo(fishCargo);

        /* Let the miner rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce a new gold cargo */
        Utils.fastForward(50, map);

        Worker miner = goldMine0.getWorker();

        assertNotNull(miner.getCargo());

        /* Verify that the miner puts the gold cargo at the flag */
        assertEquals(miner.getTarget(), goldMine0.getFlag().getPosition());
        assertTrue(goldMine0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, goldMine0.getFlag().getPosition());

        assertNull(miner.getCargo());
        assertFalse(goldMine0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the gold mine */
        assertEquals(miner.getTarget(), goldMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, goldMine0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(miner.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(miner.getTarget(), goldMine0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, goldMine0.getFlag().getPosition());

        assertNull(miner.getCargo());
        assertEquals(goldMine0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Finish construction of the gold mine */
        Utils.constructHouse(goldMine0, map);

        /* Deliver material to the gold mine */
        Cargo fishCargo = new Cargo(FISH, map);

        goldMine0.putCargo(fishCargo);
        goldMine0.putCargo(fishCargo);

        /* Occupy the gold mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0, map);

        /* Let the miner rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce a new gold cargo */
        Utils.fastForward(50, map);

        Worker miner = goldMine0.getWorker();

        assertNotNull(miner.getCargo());

        /* Verify that the miner puts the gold cargo at the flag */
        assertEquals(miner.getTarget(), goldMine0.getFlag().getPosition());
        assertTrue(goldMine0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, goldMine0.getFlag().getPosition());

        assertNull(miner.getCargo());
        assertFalse(goldMine0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = goldMine0.getFlag().getStackedCargo().get(0);

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), goldMine0.getFlag().getPosition());

        /* Connect the gold mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), goldMine0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(goldMine0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));


        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), goldMine0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(GOLD);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(GOLD), amount + 1);
    }

    @Test
    public void testMinerGoesBackToStorageWhenGoldMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Finish construction of the gold mine */
        Utils.constructHouse(goldMine0, map);

        /* Occupy the gold mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0, map);

        /* Destroy the gold mine */
        Worker miner = goldMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), goldMine0.getPosition());

        goldMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MINER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerGoesBackOnToStorageOnRoadsIfPossibleWhenGoldMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Connect the gold mine with the headquarter */
        map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the gold mine */
        Utils.constructHouse(goldMine0, map);

        /* Occupy the gold mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0, map);

        /* Destroy the gold mine */
        Worker miner = goldMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), goldMine0.getPosition());

        goldMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point p : miner.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test
    public void testProductionInGoldMineCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 6);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Connect the gold mine and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Point point5 = new Point(11, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4, point5);

        /* Finish the gold mine */
        Utils.constructHouse(goldMine0, map);

        /* Assign a worker to the gold mine */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, goldMine0, map);

        assertTrue(miner.isInsideBuilding());

        /* Deliver material to the gold mine */
        Cargo fishCargo = new Cargo(FISH, map);

        goldMine0.putCargo(fishCargo);
        goldMine0.putCargo(fishCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, miner);

        assertEquals(miner.getCargo().getMaterial(), GOLD);

        /* Wait for the worker to deliver the cargo */
        assertEquals(miner.getTarget(), goldMine0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, goldMine0.getFlag().getPosition());

        /* Stop production and verify that no gold is produced */
        goldMine0.stopProduction();

        assertFalse(goldMine0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(miner.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInGoldMineCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 6);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Connect the gold mine and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Point point5 = new Point(11, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4, point5);

        /* Finish the gold mine */
        Utils.constructHouse(goldMine0, map);

        /* Assign a worker to the gold mine */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, goldMine0, map);

        assertTrue(miner.isInsideBuilding());

        /* Deliver material to the gold mine */
        Cargo fishCargo = new Cargo(FISH, map);

        goldMine0.putCargo(fishCargo);
        goldMine0.putCargo(fishCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce gold */
        Utils.fastForwardUntilWorkerProducesCargo(map, miner);

        assertEquals(miner.getCargo().getMaterial(), GOLD);

        /* Wait for the worker to deliver the cargo */
        assertEquals(miner.getTarget(), goldMine0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, goldMine0.getFlag().getPosition());

        /* Stop production */
        goldMine0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(miner.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the gold mine produces gold again */
        goldMine0.resumeProduction();

        assertTrue(goldMine0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, miner);

        assertNotNull(miner.getCargo());
    }

    @Test
    public void testAssignedMinerHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 6);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        Utils.constructHouse(goldMine0, map);

        /* Connect the gold mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), goldMine0.getFlag());

        /* Wait for miner to get assigned and leave the headquarter */
        List<Miner> workers = Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0, map);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Miner worker = workers.get(0);

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

        /* Put a small mountain on the map */
        Point point4 = new Point(28, 18);
        Utils.surroundPointWithMountain(point4, map);
        Utils.putGoldAtSurroundingTiles(point4, LARGE, map);

        /* Place player 2's headquarter */
        Building headquarter2 = new Headquarter(player2);
        Point point10 = new Point(70, 70);
        map.placeBuilding(headquarter2, point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point3 = new Point(45, 5);
        map.placeBuilding(headquarter1, point3);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = new Fortress(player0);
        map.placeBuilding(fortress0, point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0, map);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0, map);

        /* Place gold mine close to the new border */
        GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point4);

        /* Finish construction of the gold mine */
        Utils.constructHouse(goldMine0, map);

        /* Occupy the gold mine */
        Miner worker = Utils.occupyBuilding(new Miner(player0, map), goldMine0, map);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMinerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

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

        /* Put a small mountain on the map */
        Point point2 = new Point(13, 5);
        Utils.surroundPointWithMountain(point2, map);
        Utils.putGoldAtSurroundingTiles(point2, LARGE, map);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, goldMine0.getFlag());

        /* Wait for the miner to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0, map);

        Miner miner = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Miner) {
                miner = (Miner) w;
            }
        }

        assertNotNull(miner);
        assertEquals(miner.getTarget(), goldMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the miner has started walking */
        assertFalse(miner.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the miner continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, flag0.getPosition());

        assertEquals(miner.getPosition(), flag0.getPosition());

        /* Verify that the miner returns to the headquarter when it reaches the flag */
        assertEquals(miner.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getPosition());
    }

    @Test
    public void testMinerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

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

        /* Put a small mountain on the map */
        Point point2 = new Point(13, 5);
        Utils.surroundPointWithMountain(point2, map);
        Utils.putGoldAtSurroundingTiles(point2, LARGE, map);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, goldMine0.getFlag());

        /* Wait for the miner to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0, map);

        Miner miner = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Miner) {
                miner = (Miner) w;
            }
        }

        assertNotNull(miner);
        assertEquals(miner.getTarget(), goldMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the miner has started walking */
        assertFalse(miner.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the miner continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, flag0.getPosition());

        assertEquals(miner.getPosition(), flag0.getPosition());

        /* Verify that the miner continues to the final flag */
        assertEquals(miner.getTarget(), goldMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, goldMine0.getFlag().getPosition());

        /* Verify that the miner goes out to miner instead of going directly back */
        assertNotEquals(miner.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMinerReturnsToStorageIfGoldMineIsDestroyed() throws Exception {

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

        /* Put a small mountain on the map */
        Point point2 = new Point(13, 5);
        Utils.surroundPointWithMountain(point2, map);
        Utils.putGoldAtSurroundingTiles(point2, LARGE, map);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, goldMine0.getFlag());

        /* Wait for the miner to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0, map);

        Miner miner = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Miner) {
                miner = (Miner) w;
            }
        }

        assertNotNull(miner);
        assertEquals(miner.getTarget(), goldMine0.getPosition());

        /* Wait for the miner to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, flag0.getPosition());

        map.stepTime();

        /* See that the miner has started walking */
        assertFalse(miner.isExactlyAtPoint());

        /* Tear down the goldMine */
        goldMine0.tearDown();

        /* Verify that the miner continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, goldMine0.getFlag().getPosition());

        assertEquals(miner.getPosition(), goldMine0.getFlag().getPosition());

        /* Verify that the miner goes back to storage */
        assertEquals(miner.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMinerGoesOffroadBackToClosestStorageWhenGoldMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point1 = new Point(17, 17);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        Utils.constructHouse(goldMine0, map);

        /* Occupy the gold mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0, map);

        /* Place a second storage closer to the gold mine */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the gold mine */
        Worker miner = goldMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), goldMine0.getPosition());

        goldMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), storage0.getPosition());

        int amount = storage0.getAmount(MINER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, storage0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(storage0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerReturnsOffroadAndAvoidsBurningStorageWhenGoldMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point1 = new Point(17, 17);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        Utils.constructHouse(goldMine0, map);

        /* Occupy the gold mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0, map);

        /* Place a second storage closer to the gold mine */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

        /* Destroy the gold mine */
        Worker miner = goldMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), goldMine0.getPosition());

        goldMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MINER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerReturnsOffroadAndAvoidsDestroyedStorageWhenGoldMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point1 = new Point(17, 17);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        Utils.constructHouse(goldMine0, map);

        /* Occupy the gold mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0, map);

        /* Place a second storage closer to the gold mine */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storage0, map);

        /* Destroy the goldMine */
        Worker miner = goldMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), goldMine0.getPosition());

        goldMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MINER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerReturnsOffroadAndAvoidsUnfinishedStorageWhenGoldMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point1 = new Point(17, 17);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        Utils.constructHouse(goldMine0, map);

        /* Occupy the gold mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0, map);

        /* Place a second storage closer to the gold mine */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Destroy the gold mine */
        Worker miner = goldMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), goldMine0.getPosition());

        goldMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MINER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINER), amount + 1);
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

        /* Put a small mountain on the map */
        Point point1 = new Point(17, 17);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place goldMine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Place road to connect the headquarter and the gold mine */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), goldMine0.getFlag());

        /* Finish construction of the gold mine */
        Utils.constructHouse(goldMine0, map);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0, map).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, goldMine0.getFlag().getPosition());

        /* Tear down the building */
        goldMine0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquarter instead of entering
        */
        assertEquals(worker.getTarget(), goldMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, goldMine0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }
    @Test
    public void testGoldMineWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place gold mine */
        Building goldMine = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        Utils.constructHouse(goldMine, map);

        /* Populate the gold mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), goldMine, map);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), goldMine);
        assertEquals(goldMine.getWorker(), miner0);

        /* Verify that the productivity is 0% when the gold mine doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(goldMine.getFlag().getStackedCargo().isEmpty());
            assertNull(miner0.getCargo());
            assertEquals(goldMine.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testGoldMineWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place gold mine */
        Building goldMine = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        Utils.constructHouse(goldMine, map);

        /* Populate the gold mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), goldMine, map);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), goldMine);
        assertEquals(goldMine.getWorker(), miner0);

        /* Connect the gold mine with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), goldMine.getFlag());

        /* Make the gold mine create some gold with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (goldMine.needsMaterial(FISH)) {
                goldMine.putCargo(new Cargo(FISH, map));
            }
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(goldMine.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (goldMine.needsMaterial(FISH)) {
                goldMine.putCargo(new Cargo(FISH, map));
            }

            assertEquals(goldMine.getProductivity(), 100);
        }
    }

    @Test
    public void testGoldMineLosesProductivityWhenResourcesRunOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place gold mine */
        Building goldMine = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        Utils.constructHouse(goldMine, map);

        /* Populate the gold mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), goldMine, map);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), goldMine);
        assertEquals(goldMine.getWorker(), miner0);

        /* Connect the gold mine with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), goldMine.getFlag());

        /* Make the gold mine create some gold with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (goldMine.needsMaterial(FISH) && goldMine.getAmount(FISH) < 2) {
                goldMine.putCargo(new Cargo(FISH, map));
            }
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(goldMine.getProductivity(), 100);

        for (int i = 0; i < 5000; i++) {
            map.stepTime();
        }

        assertEquals(goldMine.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedGoldMineHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place gold mine */
        Building goldMine = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        Utils.constructHouse(goldMine, map);

        /* Verify that the unoccupied gold mine is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(goldMine.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testGoldMineCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMountain(point1, map);

        /* Place coal mine */
        Building goldMine = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine*/
        Utils.constructHouse(goldMine, map);

        /* Populate the gold mine */
        Worker miner = Utils.occupyBuilding(new Miner(player0, map), goldMine, map);

        /* Verify that the gold mine can produce */
        assertTrue(goldMine.canProduce());
    }

    @Test
    public void testGoldMineReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a small mountain */
        Point point1 = new Point(6, 22);
        Utils.surroundPointWithMountain(point1, map);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Construct the gold mine */
        Utils.constructHouse(goldMine0, map);

        /* Verify that the reported output is correct */
        assertEquals(goldMine0.getProducedMaterial().length, 1);
        assertEquals(goldMine0.getProducedMaterial()[0], GOLD);
    }

    @Test
    public void testGoldMineReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a small mountain */
        Point point1 = new Point(6, 22);
        Utils.surroundPointWithMountain(point1, map);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(goldMine0.getMaterialNeeded().size(), 1);
        assertTrue(goldMine0.getMaterialNeeded().contains(PLANK));
        assertEquals(goldMine0.getTotalAmountNeeded(PLANK), 4);

        for (Material material : Material.values()) {
            if (material == PLANK) {
                continue;
            }

            assertEquals(goldMine0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testGoldMineReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);


        /* Place a small mountain */
        Point point1 = new Point(6, 22);
        Utils.surroundPointWithMountain(point1, map);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Construct the gold mine */
        Utils.constructHouse(goldMine0, map);

        /* Verify that the reported needed construction material is correct */
        assertEquals(goldMine0.getMaterialNeeded().size(), 3);
        assertTrue(goldMine0.getMaterialNeeded().contains(BREAD));
        assertTrue(goldMine0.getMaterialNeeded().contains(MEAT));
        assertTrue(goldMine0.getMaterialNeeded().contains(FISH));
        assertEquals(goldMine0.getTotalAmountNeeded(BREAD), 1);
        assertEquals(goldMine0.getTotalAmountNeeded(MEAT), 1);
        assertEquals(goldMine0.getTotalAmountNeeded(FISH), 1);

        for (Material material : Material.values()) {
            if (material == BREAD || material == MEAT || material == FISH) {
                continue;
            }

            assertEquals(goldMine0.getTotalAmountNeeded(material), 0);
        }
    }
}
