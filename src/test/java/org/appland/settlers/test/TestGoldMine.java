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
import org.appland.settlers.model.Storehouse;
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
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.MINER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
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
import static org.junit.Assert.fail;

/**
 *
 * @author johan
 */
public class TestGoldMine {

    @Test
    public void testGoldMineOnlyNeedsFourPlanksForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put a small mountain on the map */
        Point point22 = new Point(6, 12);
        Utils.surroundPointWithMountain(point22, map);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

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
            assertTrue(goldMine0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(goldMine0.isReady());
    }

    @Test
    public void testGoldMineCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put a small mountain on the map */
        Point point22 = new Point(6, 12);
        Utils.surroundPointWithMountain(point22, map);

        /* Placing headquarter */
        Point point21 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point22);

        /* Deliver two plank and three stone */
        Cargo plankCargo = new Cargo(PLANK, map);

        goldMine0.putCargo(plankCargo);
        goldMine0.putCargo(plankCargo);
        goldMine0.putCargo(plankCargo);

        /* Verify that this is not enough to construct the gold mine */
        for (int i = 0; i < 500; i++) {
            assertTrue(goldMine0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(goldMine0.isReady());
    }

    @Test
    public void testConstructGoldMine() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 14);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point point1 = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a goldmine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        assertTrue(mine.isUnderConstruction());

        constructHouse(mine);

        assertTrue(mine.isReady());
    }

    @Test
    public void testGoldmineIsNotMilitary() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 14);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point point1 = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Verify that the mine is not a military building */
        assertFalse(mine.isMilitaryBuilding());

        constructHouse(mine);

        assertFalse(mine.isMilitaryBuilding());
    }

    @Test
    public void testGoldmineUnderConstructionNotNeedsMiner() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 14);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point point1 = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Verify that the unfinished mine does not need a worker */
        assertFalse(mine.needsWorker());
    }

    @Test
    public void testFinishedGoldmineNeedsMiner() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 14);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point point1 = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        constructHouse(mine);

        /* Verify that the finished mine needs a worker */
        assertTrue(mine.needsWorker());
    }

    @Test
    public void testMinerIsAssignedToFinishedGoldmine() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 8);
        Utils.surroundPointWithMountain(point1, map);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point1);

        /* Place a road between the headquarter and the goldmine */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the mine */
        constructHouse(mine);

        assertTrue(mine.isReady());

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

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 8);
        Utils.surroundPointWithMountain(point1, map);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point1);

        assertEquals(map.getBuildings().size(), 2);
    }

    @Test
    public void testArrivedMinerRests() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Construct the gold mine */
        constructHouse(mine);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Construct the gold mine */
        constructHouse(mine);

        /* Deliver food to the miner */
        Cargo food = new Cargo(BREAD, map);
        mine.putCargo(food);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the gold mine */
        constructHouse(mine);

        /* Deliver food to the miner */
        Cargo food = new Cargo(BREAD, map);
        mine.putCargo(food);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to place a gold mine on grass */
        Point point1 = new Point(2, 2);
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            fail();
        } catch (Exception e) {}

        assertEquals(map.getBuildings().size(), 1);
    }

    @Test
    public void testGoldmineRunsOutOfGold() throws Exception {

        /* Create a new game map with one player */
        Player player0 = new Player("Player 0", BLUE);
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
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the gold mine */
        constructHouse(mine);

        /* Deliver food to the miner */
        Utils.deliverCargo(mine, BREAD);
        Utils.deliverCargo(mine, FISH);
        Utils.deliverCargo(mine, MEAT);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the gold mine */
        constructHouse(mine);

        /* Deliver food to the miner */
        Cargo food = new Cargo(BREAD, map);
        mine.putCargo(food);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Construct the gold mine */
        constructHouse(mine);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Construct the gold mine */
        constructHouse(mine);

        /* Deliver food to the miner */
        Cargo food = new Cargo(BREAD, map);
        mine.putCargo(food);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        /* Construct the gold mine */
        constructHouse(mine);

        /* Deliver food of all types to the miner */
        assertTrue(mine.needsMaterial(FISH));
        assertTrue(mine.needsMaterial(MEAT));
        assertTrue(mine.needsMaterial(BREAD));

        mine.putCargo(new Cargo(FISH, map));
        mine.putCargo(new Cargo(MEAT, map));
        mine.putCargo(new Cargo(BREAD, map));

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Finish construction of the gold mine */
        constructHouse(goldMine0);

        /* Occupy the gold mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);

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
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Finish construction of the gold mine */
        constructHouse(goldMine0);

        /* Deliver material to the gold mine */
        Cargo fishCargo = new Cargo(FISH, map);

        goldMine0.putCargo(fishCargo);
        goldMine0.putCargo(fishCargo);

        /* Occupy the gold mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);

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

        /* Remove the resources the iron mine needs from the headquarter */
        Utils.adjustInventoryTo(headquarter0, MEAT, 0);
        Utils.adjustInventoryTo(headquarter0, BREAD, 0);
        Utils.adjustInventoryTo(headquarter0, FISH, 0);

        /* Connect the gold mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), goldMine0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), goldMine0.getFlag().getPosition());
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Finish construction of the gold mine */
        constructHouse(goldMine0);

        /* Occupy the gold mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Connect the gold mine with the headquarter */
        map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the gold mine */
        constructHouse(goldMine0);

        /* Occupy the gold mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);

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
        for (Point point : miner.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testProductionInGoldMineCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 6);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Connect the gold mine and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter.getFlag());

        /* Finish the gold mine */
        constructHouse(goldMine0);

        /* Assign a worker to the gold mine */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, goldMine0);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 6);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Connect the gold mine and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter.getFlag());

        /* Finish the gold mine */
        constructHouse(goldMine0);

        /* Assign a worker to the gold mine */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, goldMine0);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 6);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Place headquarter */
        Point point1 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Finish construction of the gold mine */
        constructHouse(goldMine0);

        /* Connect the gold mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), goldMine0.getFlag());

        /* Wait for miner to get assigned and leave the headquarter */
        List<Miner> workers = Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0);

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
        Point point4 = new Point(28, 6);
        Utils.surroundPointWithMountain(point4, map);
        Utils.putGoldAtSurroundingTiles(point4, LARGE, map);

        /* Place player 2's headquarter */
        Point point10 = new Point(70, 70);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point3 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point3);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place gold mine close to the new border */
        GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point4);

        /* Finish construction of the gold mine */
        constructHouse(goldMine0);

        /* Occupy the gold mine */
        Miner worker = Utils.occupyBuilding(new Miner(player0, map), goldMine0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMinerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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
        Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0);

        Miner miner = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Miner) {
                miner = (Miner) worker;
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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
        Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0);

        Miner miner = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Miner) {
                miner = (Miner) worker;
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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
        Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0);

        Miner miner = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Miner) {
                miner = (Miner) worker;
            }
        }

        assertNotNull(miner);
        assertEquals(miner.getTarget(), goldMine0.getPosition());

        /* Wait for the miner to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, flag0.getPosition());

        map.stepTime();

        /* See that the miner has started walking */
        assertFalse(miner.isExactlyAtPoint());

        /* Tear down the gold mine */
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point1 = new Point(17, 17);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        constructHouse(goldMine0);

        /* Occupy the gold mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);

        /* Place a second storage closer to the gold mine */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the gold mine */
        Worker miner = goldMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), goldMine0.getPosition());

        goldMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(MINER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, storehouse0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerReturnsOffroadAndAvoidsBurningStorageWhenGoldMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point1 = new Point(17, 17);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        constructHouse(goldMine0);

        /* Occupy the gold mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);

        /* Place a second storage closer to the gold mine */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point1 = new Point(17, 17);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        constructHouse(goldMine0);

        /* Occupy the gold mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);

        /* Place a second storage closer to the gold mine */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

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
    public void testMinerReturnsOffroadAndAvoidsUnfinishedStorageWhenGoldMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point1 = new Point(17, 17);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Placing gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        constructHouse(goldMine0);

        /* Occupy the gold mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);

        /* Place a second storage closer to the gold mine */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point1 = new Point(17, 17);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Place road to connect the headquarter and the gold mine */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), goldMine0.getFlag());

        /* Finish construction of the gold mine */
        constructHouse(goldMine0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, goldMine0.getFlag().getPosition());

        /* Tear down the building */
        goldMine0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(worker.getTarget(), goldMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, goldMine0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }
    @Test
    public void testGoldMineWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place gold mine */
        Building goldMine = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        constructHouse(goldMine);

        /* Populate the gold mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), goldMine);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place gold mine */
        Building goldMine = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        constructHouse(goldMine);

        /* Populate the gold mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), goldMine);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place gold mine */
        Building goldMine = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        constructHouse(goldMine);

        /* Populate the gold mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), goldMine);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place gold mine */
        Building goldMine = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        constructHouse(goldMine);

        /* Verify that the unoccupied gold mine is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(goldMine.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testGoldMineCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMountain(point1, map);

        /* Place gold mine */
        Building goldMine = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        constructHouse(goldMine);

        /* Populate the gold mine */
        Worker miner = Utils.occupyBuilding(new Miner(player0, map), goldMine);

        /* Verify that the gold mine can produce */
        assertTrue(goldMine.canProduce());
    }

    @Test
    public void testGoldMineReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a small mountain */
        Point point1 = new Point(6, 12);
        Utils.surroundPointWithMountain(point1, map);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Construct the gold mine */
        constructHouse(goldMine0);

        /* Verify that the reported output is correct */
        assertEquals(goldMine0.getProducedMaterial().length, 1);
        assertEquals(goldMine0.getProducedMaterial()[0], GOLD);
    }

    @Test
    public void testGoldMineReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a small mountain */
        Point point1 = new Point(6, 12);
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a small mountain */
        Point point1 = new Point(6, 12);
        Utils.surroundPointWithMountain(point1, map);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Construct the gold mine */
        constructHouse(goldMine0);

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

    @Test
    public void testGoldMineWaitsWhenFlagIsFull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a small mountain */
        Point point1 = new Point(16, 6);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place gold mine */
        Building goldMine = map.placeBuilding(new GoldMine(player0), point1);

        /* Connect the gold mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, goldMine.getFlag(), headquarter.getFlag());

        /* Wait for the gold mine to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(goldMine);
        Utils.waitForNonMilitaryBuildingToGetPopulated(goldMine);

        /* Give material to the gold mine */
        Utils.putCargoToBuilding(goldMine, BREAD);
        Utils.putCargoToBuilding(goldMine, BREAD);
        Utils.putCargoToBuilding(goldMine, FISH);
        Utils.putCargoToBuilding(goldMine, FISH);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, goldMine.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the gold mine waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(goldMine.getFlag().getStackedCargo().size(), 8);
            assertNull(goldMine.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the gold mine with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, goldMine.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(goldMine.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(goldMine.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(goldMine.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, goldMine.getWorker(), GOLD);
    }

    @Test
    public void testGoldMineDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a small mountain */
        Point point1 = new Point(16, 6);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place gold mine */
        GoldMine goldMine = map.placeBuilding(new GoldMine(player0), point1);

        /* Connect the gold mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, goldMine.getFlag(), headquarter.getFlag());

        /* Wait for the gold mine to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(goldMine);
        Utils.waitForNonMilitaryBuildingToGetPopulated(goldMine);

        /* Give material to the gold mine */
        Utils.putCargoToBuilding(goldMine, BREAD);
        Utils.putCargoToBuilding(goldMine, BREAD);
        Utils.putCargoToBuilding(goldMine, FISH);
        Utils.putCargoToBuilding(goldMine, FISH);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, goldMine.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The gold mine waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(goldMine.getFlag().getStackedCargo().size(), 8);
            assertNull(goldMine.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the gold mine with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, goldMine.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(goldMine.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(goldMine.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(goldMine.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, goldMine.getWorker(), GOLD);

        /* Wait for the worker to put the cargo on the flag */
        assertEquals(goldMine.getWorker().getTarget(), goldMine.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, goldMine.getWorker(), goldMine.getFlag().getPosition());

        assertEquals(goldMine.getFlag().getStackedCargo().size(), 8);

        /* Verify that the gold mine doesn't produce anything because the flag is full */
        for (int i = 0; i < 400; i++) {
            assertEquals(goldMine.getFlag().getStackedCargo().size(), 8);
            assertNull(goldMine.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testWhenGoldDeliveryAreBlockedGoldMineFillsUpFlagAndThenStops() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place Gold mine */
        GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Place road to connect the gold mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());

        /* Wait for the gold mine to get constructed and occupied */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(goldMine0);

        Worker miner0 = Utils.waitForNonMilitaryBuildingToGetPopulated(goldMine0);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), goldMine0);
        assertEquals(goldMine0.getWorker(), miner0);

        /* Add a lot of material to the headquarter for the gold mine to consume */
        Utils.adjustInventoryTo(headquarter0, MEAT, 40);
        Utils.adjustInventoryTo(headquarter0, BREAD, 40);

        /* Block storage of gold */
        headquarter0.blockDeliveryOfMaterial(GOLD);

        /* Verify that the gold mine puts eight gold pieces on the flag and then stops */
        Utils.waitForFlagToGetStackedCargo(map, goldMine0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner0, goldMine0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(goldMine0.getFlag().getStackedCargo().size(), 8);
            assertTrue(miner0.isInsideBuilding());

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), GOLD);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndGoldMineIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Put a small mountain on the map */
        Point point2 = new Point(18, 6);
        Utils.surroundPointWithMountain(point2, map);
        Utils.putGoldAtSurroundingTiles(point2, LARGE, map);

        /* Place gold mine */
        GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the gold mine */
        Road road1 = map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the gold mine and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, goldMine0);

        /* Add a lot of material to the headquarter for the gold mine to consume */
        Utils.adjustInventoryTo(headquarter0, MEAT, 40);
        Utils.adjustInventoryTo(headquarter0, BREAD, 40);

        /* Wait for the gold mine and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, goldMine0);

        Worker miner0 = goldMine0.getWorker();

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), goldMine0);
        assertEquals(goldMine0.getWorker(), miner0);

        /* Verify that the worker goes to the storage when the gold mine is torn down */
        headquarter0.blockDeliveryOfMaterial(MINER);

        goldMine0.tearDown();

        map.stepTime();

        assertFalse(miner0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner0, goldMine0.getFlag().getPosition());

        assertEquals(miner0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, miner0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(miner0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndGoldMineIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Put a small mountain on the map */
        Point point2 = new Point(18, 6);
        Utils.surroundPointWithMountain(point2, map);
        Utils.putGoldAtSurroundingTiles(point2, LARGE, map);

        /* Place gold mine */
        GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the gold mine */
        Road road1 = map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the gold mine and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, goldMine0);

        /* Add a lot of material to the headquarter for the gold mine to consume */
        Utils.adjustInventoryTo(headquarter0, MEAT, 40);
        Utils.adjustInventoryTo(headquarter0, BREAD, 40);

        /* Wait for the gold mine and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, goldMine0);

        Worker miner0 = goldMine0.getWorker();

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), goldMine0);
        assertEquals(goldMine0.getWorker(), miner0);

        /* Verify that the worker goes to the storage off-road when the gold mine is torn down */
        headquarter0.blockDeliveryOfMaterial(MINER);

        goldMine0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(miner0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner0, goldMine0.getFlag().getPosition());

        assertEquals(miner0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(miner0));
    }

    @Test
    public void testWorkerGoesOutAndBackInWhenSentOutWithoutBlocking() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, MINER, 1);

        assertEquals(headquarter0.getAmount(MINER), 1);

        headquarter0.pushOutAll(MINER);

        for (int i = 0; i < 10; i++) {
            Worker worker = Utils.waitForWorkerOutsideBuilding(Miner.class, player0);

            assertEquals(headquarter0.getAmount(MINER), 0);
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, MINER, 1);

        headquarter0.blockDeliveryOfMaterial(MINER);
        headquarter0.pushOutAll(MINER);

        Worker worker = Utils.waitForWorkerOutsideBuilding(Miner.class, player0);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place gold mine */
        GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Place road to connect the gold mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the gold mine to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(goldMine0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(goldMine0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter
        */
        headquarter0.blockDeliveryOfMaterial(MINER);

        Worker worker = goldMine0.getWorker();

        goldMine0.tearDown();

        assertEquals(worker.getPosition(), goldMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, goldMine0.getFlag().getPosition());

        assertEquals(worker.getPosition(), goldMine0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), goldMine0.getPosition());
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place gold mine */
        GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Place road to connect the gold mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the gold mine to get constructed */
        Utils.waitForBuildingToBeConstructed(goldMine0);

        /* Wait for a miner to start walking to the gold mine */
        Miner miner = Utils.waitForWorkerOutsideBuilding(Miner.class, player0);

        /* Wait for the miner to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the miner goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(miner.getTarget(), goldMine0.getPosition());

        headquarter0.blockDeliveryOfMaterial(MINER);

        goldMine0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, goldMine0.getFlag().getPosition());

        assertEquals(miner.getPosition(), goldMine0.getFlag().getPosition());
        assertNotEquals(miner.getTarget(), headquarter0.getPosition());
        assertFalse(miner.isInsideBuilding());
        assertNull(goldMine0.getWorker());
        assertNotNull(miner.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, miner.getTarget());

        Point point = miner.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(miner.isDead());
            assertEquals(miner.getPosition(), point);
            assertTrue(map.getWorkers().contains(miner));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(miner));
    }
}
