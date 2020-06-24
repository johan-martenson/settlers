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
import org.appland.settlers.model.GraniteMine;
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
public class TestGraniteMine {

    @Test
    public void testGraniteMineOnlyNeedsFourPlanksForConstruction() throws Exception {

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

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point22);

        /* Deliver four planks */
        Cargo cargo = new Cargo(PLANK, map);

        graniteMine0.putCargo(cargo);
        graniteMine0.putCargo(cargo);
        graniteMine0.putCargo(cargo);
        graniteMine0.putCargo(cargo);

        /* Verify that this is enough to construct the granite mine */
        for (int i = 0; i < 100; i++) {
            assertTrue(graniteMine0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(graniteMine0.isReady());
    }

    @Test
    public void testGraniteMineCannotBeConstructedWithThreePlanks() throws Exception {

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

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point22);

        /* Deliver three planks */
        Cargo cargo = new Cargo(PLANK, map);

        graniteMine0.putCargo(cargo);
        graniteMine0.putCargo(cargo);
        graniteMine0.putCargo(cargo);

        /* Verify that this is not enough to construct the granite mine */
        for (int i = 0; i < 500; i++) {
            assertTrue(graniteMine0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(graniteMine0.isReady());
    }

    @Test
    public void testConstructGraniteMine() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(12, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point point1 = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a granite mine */
        Building mine = map.placeBuilding(new GraniteMine(player0), point0);

        assertTrue(mine.isUnderConstruction());

        constructHouse(mine);

        assertTrue(mine.isReady());
    }

    @Test
    public void testGraniteMineIsNotMilitary() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(12, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point point1 = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a granite mine */
        Building mine = map.placeBuilding(new GraniteMine(player0), point0);

        /* Verify that the mine is not a military building */
        assertFalse(mine.isMilitaryBuilding());

        constructHouse(mine);

        assertFalse(mine.isMilitaryBuilding());
    }

    @Test
    public void testGraniteMineUnderConstructionNotNeedsMiner() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(12, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point point1 = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a granite mine */
        Building mine = map.placeBuilding(new GraniteMine(player0), point0);

        /* Verify that the unfinished mine does not need a worker */
        assertFalse(mine.needsWorker());
    }

    @Test
    public void testFinishedGraniteMineNeedsMiner() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(12, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point point1 = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a granite mine */
        Building mine = map.placeBuilding(new GraniteMine(player0), point0);

        constructHouse(mine);

        /* Verify that the finished mine needs a worker */
        assertTrue(mine.needsWorker());
    }

    @Test
    public void testMinerIsAssignedToFinishedGraniteMine() throws Exception {

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

        /* Place a granite mine */
        Building mine = map.placeBuilding(new GraniteMine(player0), point1);

        /* Place a road between the headquarter and the granite mine */
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

        /* Place a granite mine */
        Building mine = map.placeBuilding(new GraniteMine(player0), point1);

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

        /* Place a granite mine */
        Building mine = map.placeBuilding(new GraniteMine(player0), point0);

        /* Construct the granite mine */
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
    public void testMinerMinesGranite() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a granite mine */
        Building mine = map.placeBuilding(new GraniteMine(player0), point0);

        /* Construct the granite mine */
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

        /* Verify that the miner mines for granite */
        int amountGranite = map.getAmountOfMineralAtPoint(STONE, point0);

        for (int i = 0; i < 50; i++) {
            assertTrue(miner.isMining());
            map.stepTime();
        }

        /* Verify that the miner finishes mining on time and has granite */
        assertFalse(miner.isMining());
        assertFalse(miner.isInsideBuilding());
        assertNotNull(miner.getCargo());
        assertEquals(miner.getCargo().getMaterial(), STONE);
        assertTrue(map.getAmountOfMineralAtPoint(STONE, point0) < amountGranite);
    }

    @Test
    public void testGraniteMineGoesToFlagWithCargoAndBack() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a granite mine */
        Building mine = map.placeBuilding(new GraniteMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the granite mine */
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

        /* Wait for the miner to mine granite */
        Utils.fastForward(50, map);

        /* Verify that the miner leaves the granite at the flag */
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
        GameMap map = new GameMap(players, 10, 10);

        Point point0 = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(2, 2);
        try {
            map.placeBuilding(new GraniteMine(player0), point1);
            fail();
        } catch (Exception e) {}

        assertEquals(map.getBuildings().size(), 1);
    }

    @Test
    public void testGraniteMineRunsOutOfGranite() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGraniteAtSurroundingTiles(point0, SMALL, map);

        /* Remove all granite but one */
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountOfMineralAtPoint(STONE, point0) > 1) {
                map.mineMineralAtPoint(STONE, point0);
            }
        }

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a granite mine */
        Building mine = map.placeBuilding(new GraniteMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the granite mine */
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

        /* Wait for the miner to mine granite */
        assertFalse(mine.isOutOfNaturalResources());

        Utils.fastForward(50, map);

        /* Wait for the miner to leave the granite at the flag */
        assertEquals(miner.getTarget(), mine.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getFlag().getPosition());

        assertNull(miner.getCargo());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getPosition());

        assertTrue(miner.isInsideBuilding());

        /* Verify that the granite is gone and that the miner gets no granite */
        assertEquals(map.getAmountOfMineralAtPoint(STONE, point0), 0);

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());

            map.stepTime();
        }

        /* Verify that the granite mine can run out of resources */
        assertTrue(mine.isOutOfNaturalResources());
    }

    @Test
    public void testGraniteMineWithoutGraniteProducesNothing() throws Exception {

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

        /* Place a granite mine */
        Building mine = map.placeBuilding(new GraniteMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the granite mine */
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

        /* Verify that there is no granite and that the miner gets no granite */
        assertEquals(map.getAmountOfMineralAtPoint(STONE, point0), 0);

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testGraniteMineWithoutFoodProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a granite mine */
        Building mine = map.placeBuilding(new GraniteMine(player0), point0);

        /* Construct the granite mine */
        constructHouse(mine);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine);

        assertTrue(miner.isInsideBuilding());

        /* Wait for the miner to rest */
        Utils.fastForward(100, map);

        /* Verify that the miner gets no granite */

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testMiningConsumesFood() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a granite mine */
        Building mine = map.placeBuilding(new GraniteMine(player0), point0);

        /* Construct the granite mine */
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

        /* Verify that the miner mines for granite */
        assertEquals(mine.getAmount(BREAD), 1);

        Utils.fastForward(50, map);

        /* Verify that the miner consumed the bread */
        assertEquals(mine.getAmount(BREAD), 0);
    }

    @Test
    public void testGraniteMineCanConsumeAllTypesOfFood() throws Exception {

        /* Start new game with one player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a granite mine */
        Building mine = map.placeBuilding(new GraniteMine(player0), point0);

        /* Construct the granite mine */
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
            Utils.fastForwardUntilWorkerCarriesCargo(map, miner, STONE);

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
    public void testGraniteMineWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point0);

        /* Finish construction of the granite mine */
        constructHouse(graniteMine0);

        /* Occupy the granite mine */
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

        /* Deliver material to the granite mine */
        Cargo fishCargo = new Cargo(FISH, map);

        graniteMine0.putCargo(fishCargo);
        graniteMine0.putCargo(fishCargo);

        /* Let the miner rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce a new stone cargo */
        Utils.fastForward(50, map);

        Worker miner = graniteMine0.getWorker();

        assertNotNull(miner.getCargo());

        /* Verify that the miner puts the stone cargo at the flag */
        assertEquals(miner.getTarget(), graniteMine0.getFlag().getPosition());
        assertTrue(graniteMine0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, graniteMine0.getFlag().getPosition());

        assertNull(miner.getCargo());
        assertFalse(graniteMine0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the granite mine */
        assertEquals(miner.getTarget(), graniteMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, graniteMine0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(miner.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(miner.getTarget(), graniteMine0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, graniteMine0.getFlag().getPosition());

        assertNull(miner.getCargo());
        assertEquals(graniteMine0.getFlag().getStackedCargo().size(), 2);
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
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point0);

        /* Finish construction of the granite mine */
        constructHouse(graniteMine0);

        /* Deliver material to the granite mine */
        Cargo fishCargo = new Cargo(FISH, map);

        graniteMine0.putCargo(fishCargo);
        graniteMine0.putCargo(fishCargo);

        /* Occupy the granite mine */
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

        /* Let the miner rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce a new stone cargo */
        Utils.fastForward(50, map);

        Worker miner = graniteMine0.getWorker();

        assertNotNull(miner.getCargo());

        /* Verify that the miner puts the stone cargo at the flag */
        assertEquals(miner.getTarget(), graniteMine0.getFlag().getPosition());
        assertTrue(graniteMine0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, graniteMine0.getFlag().getPosition());

        assertNull(miner.getCargo());
        assertFalse(graniteMine0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = graniteMine0.getFlag().getStackedCargo().get(0);

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), graniteMine0.getFlag().getPosition());

        /* Remove the resources the iron mine needs from the headquarter */
        Utils.adjustInventoryTo(headquarter0, MEAT, 0);
        Utils.adjustInventoryTo(headquarter0, BREAD, 0);
        Utils.adjustInventoryTo(headquarter0, FISH, 0);

        /* Connect the granite mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), graniteMine0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), graniteMine0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), graniteMine0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(STONE);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(STONE), amount + 1);
    }

    @Test
    public void testMinerGoesBackToStorageWhenGraniteMineIsDestroyed() throws Exception {

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
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point0);

        /* Finish construction of the granite mine */
        constructHouse(graniteMine0);

        /* Occupy the granite mine */
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

        /* Destroy the granite mine */
        Worker miner = graniteMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), graniteMine0.getPosition());

        graniteMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MINER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerGoesBackOnToStorageOnRoadsIfPossibleWhenGraniteMineIsDestroyed() throws Exception {

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
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point0);

        /* Connect the granite mine with the headquarter */
        map.placeAutoSelectedRoad(player0, graniteMine0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the granite mine */
        constructHouse(graniteMine0);

        /* Occupy the granite mine */
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

        /* Destroy the granite mine */
        Worker miner = graniteMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), graniteMine0.getPosition());

        graniteMine0.tearDown();

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
    public void testProductionInGraniteMineCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 6);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point1);

        /* Connect the granite mine and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, graniteMine0.getFlag(), headquarter.getFlag());

        /* Finish the granite mine */
        constructHouse(graniteMine0);

        /* Deliver food to the miner */
        Cargo food = new Cargo(BREAD, map);
        graniteMine0.putCargo(food);

        /* Assign a worker to the granite mine */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, graniteMine0);

        assertTrue(miner.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, miner);

        assertEquals(miner.getCargo().getMaterial(), STONE);

        /* Wait for the worker to deliver the cargo */
        assertEquals(miner.getTarget(), graniteMine0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, graniteMine0.getFlag().getPosition());

        /* Stop production and verify that no stone is produced */
        graniteMine0.stopProduction();

        assertFalse(graniteMine0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(miner.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInGraniteMineCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 6);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point1);

        /* Connect the granite mine and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, graniteMine0.getFlag(), headquarter.getFlag());

        /* Finish the granite mine */
        constructHouse(graniteMine0);

        /* Deliver food to the miner */
        Cargo food = new Cargo(BREAD, map);

        graniteMine0.putCargo(food);
        graniteMine0.putCargo(food);

        /* Assign a worker to the granite mine */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, graniteMine0);

        assertTrue(miner.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce stone */
        Utils.fastForwardUntilWorkerProducesCargo(map, miner);

        assertEquals(miner.getCargo().getMaterial(), STONE);

        /* Wait for the worker to deliver the cargo */
        assertEquals(miner.getTarget(), graniteMine0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, graniteMine0.getFlag().getPosition());

        /* Stop production */
        graniteMine0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(miner.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the granite mine produces stone again */
        graniteMine0.resumeProduction();

        assertTrue(graniteMine0.isProductionEnabled());

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
        Point point0 = new Point(10, 10);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);

        /* Place headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point0);

        /* Finish construction of the granite mine */
        constructHouse(graniteMine0);

        /* Connect the granite mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), graniteMine0.getFlag());

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
        Point point4 = new Point(28, 12);
        Utils.surroundPointWithMountain(point4, map);
        Utils.putGraniteAtSurroundingTiles(point4, LARGE, map);

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

        /* Place granite mine close to the new border */
        GraniteMine graniteMine0 = map.placeBuilding(new GraniteMine(player0), point4);

        /* Finish construction of the granite mine */
        constructHouse(graniteMine0);

        /* Occupy the granite mine */
        Miner worker = Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

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
        Utils.putGraniteAtSurroundingTiles(point2, LARGE, map);

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, graniteMine0.getFlag());

        /* Wait for the miner to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0);

        Miner miner = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Miner) {
                miner = (Miner) worker;
            }
        }

        assertNotNull(miner);
        assertEquals(miner.getTarget(), graniteMine0.getPosition());

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
        Utils.putGraniteAtSurroundingTiles(point2, LARGE, map);

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, graniteMine0.getFlag());

        /* Wait for the miner to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0);

        Miner miner = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Miner) {
                miner = (Miner) worker;
            }
        }

        assertNotNull(miner);
        assertEquals(miner.getTarget(), graniteMine0.getPosition());

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
        assertEquals(miner.getTarget(), graniteMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, graniteMine0.getFlag().getPosition());

        /* Verify that the miner goes out to miner instead of going directly back */
        assertNotEquals(miner.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMinerReturnsToStorageIfGraniteMineIsDestroyed() throws Exception {

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
        Utils.putGraniteAtSurroundingTiles(point2, LARGE, map);

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, graniteMine0.getFlag());

        /* Wait for the miner to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0);

        Miner miner = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Miner) {
                miner = (Miner) worker;
            }
        }

        assertNotNull(miner);
        assertEquals(miner.getTarget(), graniteMine0.getPosition());

        /* Wait for the miner to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, flag0.getPosition());

        map.stepTime();

        /* See that the miner has started walking */
        assertFalse(miner.isExactlyAtPoint());

        /* Tear down the granite mine */
        graniteMine0.tearDown();

        /* Verify that the miner continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, graniteMine0.getFlag().getPosition());

        assertEquals(miner.getPosition(), graniteMine0.getFlag().getPosition());

        /* Verify that the miner goes back to storage */
        assertEquals(miner.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMinerGoesOffroadBackToClosestStorageWhenGraniteMineIsDestroyed() throws Exception {

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
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point1);

        /* Finish construction of the granite mine */
        constructHouse(graniteMine0);

        /* Occupy the granite mine */
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

        /* Place a second storage closer to the granite mine */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the granite mine */
        Worker miner = graniteMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), graniteMine0.getPosition());

        graniteMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(MINER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, storehouse0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerReturnsOffroadAndAvoidsBurningStorageWhenGraniteMineIsDestroyed() throws Exception {

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
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point1);

        /* Finish construction of the granite mine */
        constructHouse(graniteMine0);

        /* Occupy the granite mine */
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

        /* Place a second storage closer to the granite mine */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Destroy the granite mine */
        Worker miner = graniteMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), graniteMine0.getPosition());

        graniteMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MINER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerReturnsOffroadAndAvoidsDestroyedStorageWhenGraniteMineIsDestroyed() throws Exception {

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
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point1);

        /* Finish construction of the granite mine */
        constructHouse(graniteMine0);

        /* Occupy the granite mine */
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

        /* Place a second storage closer to the granite mine */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

        /* Destroy the granite mine */
        Worker miner = graniteMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), graniteMine0.getPosition());

        graniteMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MINER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerReturnsOffroadAndAvoidsUnfinishedStorageWhenGraniteMineIsDestroyed() throws Exception {

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
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point1);

        /* Finish construction of the granite mine */
        constructHouse(graniteMine0);

        /* Occupy the granite mine */
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

        /* Place a second storage closer to the granite mine */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the granite mine */
        Worker miner = graniteMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), graniteMine0.getPosition());

        graniteMine0.tearDown();

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
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Place granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point1);

        /* Place road to connect the headquarter and the granite mine */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), graniteMine0.getFlag());

        /* Finish construction of the granite mine */
        constructHouse(graniteMine0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, graniteMine0.getFlag().getPosition());

        /* Tear down the building */
        graniteMine0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(worker.getTarget(), graniteMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, graniteMine0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testGraniteMineWithoutResourcesHasZeroProductivity() throws Exception {

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
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Place granite mine */
        Building graniteMine = map.placeBuilding(new GraniteMine(player0), point1);

        /* Finish construction of the granite mine */
        constructHouse(graniteMine);

        /* Populate the granite mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), graniteMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), graniteMine);
        assertEquals(graniteMine.getWorker(), miner0);

        /* Verify that the productivity is 0% when the granite mine doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(graniteMine.getFlag().getStackedCargo().isEmpty());
            assertNull(miner0.getCargo());
            assertEquals(graniteMine.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testGraniteMineWithAbundantResourcesHasFullProductivity() throws Exception {

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
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Place granite mine */
        Building graniteMine = map.placeBuilding(new GraniteMine(player0), point1);

        /* Finish construction of the granite mine */
        constructHouse(graniteMine);

        /* Populate the granite mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), graniteMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), graniteMine);
        assertEquals(graniteMine.getWorker(), miner0);

        /* Connect the granite mine with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), graniteMine.getFlag());

        /* Make the granite mine create some granite with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (graniteMine.needsMaterial(FISH)) {
                graniteMine.putCargo(new Cargo(FISH, map));
            }
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(graniteMine.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (graniteMine.needsMaterial(FISH)) {
                graniteMine.putCargo(new Cargo(FISH, map));
            }

            assertEquals(graniteMine.getProductivity(), 100);
        }
    }

    @Test
    public void testGraniteMineLosesProductivityWhenResourcesRunOut() throws Exception {

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
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Place granite mine */
        Building graniteMine = map.placeBuilding(new GraniteMine(player0), point1);

        /* Finish construction of the granite mine */
        constructHouse(graniteMine);

        /* Populate the granite mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), graniteMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), graniteMine);
        assertEquals(graniteMine.getWorker(), miner0);

        /* Connect the granite mine with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), graniteMine.getFlag());

        /* Make the granite mine create some granite with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (graniteMine.needsMaterial(FISH) && graniteMine.getAmount(FISH) < 2) {
                graniteMine.putCargo(new Cargo(FISH, map));
            }
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(graniteMine.getProductivity(), 100);

        for (int i = 0; i < 5000; i++) {
            map.stepTime();
        }

        assertEquals(graniteMine.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedGraniteMineHasNoProductivity() throws Exception {

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
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Place granite mine */
        Building graniteMine = map.placeBuilding(new GraniteMine(player0), point1);

        /* Finish construction of the granite mine */
        constructHouse(graniteMine);

        /* Verify that the unoccupied granite mine is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(graniteMine.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testGraniteMineCanProduce() throws Exception {

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

        /* Place granite mine */
        Building graniteMine = map.placeBuilding(new GraniteMine(player0), point1);

        /* Finish construction of the stone mine */
        constructHouse(graniteMine);

        /* Populate the granite mine */
        Worker miner = Utils.occupyBuilding(new Miner(player0, map), graniteMine);

        /* Verify that the granite mine can produce */
        assertTrue(graniteMine.canProduce());
    }

    @Test
    public void testGraniteMineReportsCorrectOutput() throws Exception {

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

        /* Place granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point1);

        /* Construct the granite mine */
        constructHouse(graniteMine0);

        /* Verify that the reported output is correct */
        assertEquals(graniteMine0.getProducedMaterial().length, 1);
        assertEquals(graniteMine0.getProducedMaterial()[0], STONE);
    }

    @Test
    public void testGraniteMineReportsCorrectMaterialsNeededForConstruction() throws Exception {

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

        /* Place granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(graniteMine0.getMaterialNeeded().size(), 1);
        assertTrue(graniteMine0.getMaterialNeeded().contains(PLANK));
        assertEquals(graniteMine0.getTotalAmountNeeded(PLANK), 4);

        for (Material material : Material.values()) {
            if (material == PLANK) {
                continue;
            }

            assertEquals(graniteMine0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testGraniteMineReportsCorrectMaterialsNeededForProduction() throws Exception {

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

        /* Place granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point1);

        /* Construct the granite mine */
        constructHouse(graniteMine0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(graniteMine0.getMaterialNeeded().size(), 3);
        assertTrue(graniteMine0.getMaterialNeeded().contains(BREAD));
        assertTrue(graniteMine0.getMaterialNeeded().contains(MEAT));
        assertTrue(graniteMine0.getMaterialNeeded().contains(FISH));
        assertEquals(graniteMine0.getTotalAmountNeeded(BREAD), 1);
        assertEquals(graniteMine0.getTotalAmountNeeded(MEAT), 1);
        assertEquals(graniteMine0.getTotalAmountNeeded(FISH), 1);

        for (Material material : Material.values()) {
            if (material == BREAD || material == MEAT || material == FISH) {
                continue;
            }

            assertEquals(graniteMine0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testGraniteMineWaitsWhenFlagIsFull() throws Exception {

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
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Place granite mine */
        Building graniteMine = map.placeBuilding(new GraniteMine(player0), point1);

        /* Connect the granite mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, graniteMine.getFlag(), headquarter.getFlag());

        /* Wait for the granite mine to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(graniteMine);
        Utils.waitForNonMilitaryBuildingToGetPopulated(graniteMine);

        /* Give material to the granite mine */
        Utils.putCargoToBuilding(graniteMine, BREAD);
        Utils.putCargoToBuilding(graniteMine, BREAD);
        Utils.putCargoToBuilding(graniteMine, FISH);
        Utils.putCargoToBuilding(graniteMine, FISH);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, graniteMine.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the granite mine waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(graniteMine.getFlag().getStackedCargo().size(), 8);
            assertNull(graniteMine.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the granite mine with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, graniteMine.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(graniteMine.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(graniteMine.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(graniteMine.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, graniteMine.getWorker(), STONE);
    }

    @Test
    public void testGraniteMineDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

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
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Place granite mine */
        GraniteMine graniteMine = map.placeBuilding(new GraniteMine(player0), point1);

        /* Connect the granite mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, graniteMine.getFlag(), headquarter.getFlag());

        /* Wait for the granite mine to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(graniteMine);
        Utils.waitForNonMilitaryBuildingToGetPopulated(graniteMine);

        /* Give material to the granite mine */
        Utils.putCargoToBuilding(graniteMine, BREAD);
        Utils.putCargoToBuilding(graniteMine, BREAD);
        Utils.putCargoToBuilding(graniteMine, FISH);
        Utils.putCargoToBuilding(graniteMine, FISH);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, graniteMine.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The granite mine waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(graniteMine.getFlag().getStackedCargo().size(), 8);
            assertNull(graniteMine.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the granite mine with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, graniteMine.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(graniteMine.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(graniteMine.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(graniteMine.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, graniteMine.getWorker(), STONE);

        /* Wait for the worker to put the cargo on the flag */
        assertEquals(graniteMine.getWorker().getTarget(), graniteMine.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, graniteMine.getWorker(), graniteMine.getFlag().getPosition());

        assertEquals(graniteMine.getFlag().getStackedCargo().size(), 8);

        /* Verify that the granite mine doesn't produce anything because the flag is full */
        for (int i = 0; i < 400; i++) {
            assertEquals(graniteMine.getFlag().getStackedCargo().size(), 8);
            assertNull(graniteMine.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testWhenStoneDeliveryAreBlockedGraniteMineFillsUpFlagAndThenStops() throws Exception {

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
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Place Stone mine */
        GraniteMine stoneMine0 = map.placeBuilding(new GraniteMine(player0), point1);

        /* Place road to connect the stone mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, stoneMine0.getFlag(), headquarter0.getFlag());

        /* Wait for the stone mine to get constructed and occupied */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(stoneMine0);

        Worker miner0 = Utils.waitForNonMilitaryBuildingToGetPopulated(stoneMine0);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), stoneMine0);
        assertEquals(stoneMine0.getWorker(), miner0);

        /* Add a lot of material to the headquarter for the stone mine to consume */
        Utils.adjustInventoryTo(headquarter0, MEAT, 40);
        Utils.adjustInventoryTo(headquarter0, BREAD, 40);

        /* Block storage of stone */
        headquarter0.blockDeliveryOfMaterial(STONE);

        /* Verify that the stone mine puts eight stones on the flag and then stops */
        Utils.waitForFlagToGetStackedCargo(map, stoneMine0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner0, stoneMine0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(stoneMine0.getFlag().getStackedCargo().size(), 8);
            assertTrue(miner0.isInsideBuilding());

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), STONE);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndGraniteMineIsTornDown() throws Exception {

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
        Utils.putGraniteAtSurroundingTiles(point2, LARGE, map);

        /* Place stone mine */
        GraniteMine stoneMine0 = map.placeBuilding(new GraniteMine(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the stone mine */
        Road road1 = map.placeAutoSelectedRoad(player0, stoneMine0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the stone mine and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, stoneMine0);

        /* Add a lot of material to the headquarter for the stone mine to consume */
        Utils.adjustInventoryTo(headquarter0, MEAT, 40);
        Utils.adjustInventoryTo(headquarter0, BREAD, 40);

        /* Wait for the stone mine and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, stoneMine0);

        Worker miner0 = stoneMine0.getWorker();

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), stoneMine0);
        assertEquals(stoneMine0.getWorker(), miner0);

        /* Verify that the worker goes to the storage when the stone mine is torn down */
        headquarter0.blockDeliveryOfMaterial(MINER);

        stoneMine0.tearDown();

        map.stepTime();

        assertFalse(miner0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner0, stoneMine0.getFlag().getPosition());

        assertEquals(miner0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, miner0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(miner0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndGraniteMineIsTornDown() throws Exception {

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
        Utils.putGraniteAtSurroundingTiles(point2, LARGE, map);

        /* Place stone mine */
        GraniteMine stoneMine0 = map.placeBuilding(new GraniteMine(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the stone mine */
        Road road1 = map.placeAutoSelectedRoad(player0, stoneMine0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the stone mine and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, stoneMine0);

        /* Add a lot of material to the headquarter for the stone mine to consume */
        Utils.adjustInventoryTo(headquarter0, MEAT, 40);
        Utils.adjustInventoryTo(headquarter0, BREAD, 40);

        /* Wait for the stone mine and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, stoneMine0);

        Worker miner0 = stoneMine0.getWorker();

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), stoneMine0);
        assertEquals(stoneMine0.getWorker(), miner0);

        /* Verify that the worker goes to the storage off-road when the stone mine is torn down */
        headquarter0.blockDeliveryOfMaterial(MINER);

        stoneMine0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(miner0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner0, stoneMine0.getFlag().getPosition());

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
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Place stone mine */
        GraniteMine stoneMine0 = map.placeBuilding(new GraniteMine(player0), point1);

        /* Place road to connect the stone mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, stoneMine0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the stone mine to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(stoneMine0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(stoneMine0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter
        */
        headquarter0.blockDeliveryOfMaterial(MINER);

        Worker worker = stoneMine0.getWorker();

        stoneMine0.tearDown();

        assertEquals(worker.getPosition(), stoneMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, stoneMine0.getFlag().getPosition());

        assertEquals(worker.getPosition(), stoneMine0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), stoneMine0.getPosition());
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
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Place stone mine */
        GraniteMine stoneMine0 = map.placeBuilding(new GraniteMine(player0), point1);

        /* Place road to connect the stone mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, stoneMine0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the stone mine to get constructed */
        Utils.waitForBuildingToBeConstructed(stoneMine0);

        /* Wait for a miner to start walking to the stone mine */
        Miner miner = Utils.waitForWorkerOutsideBuilding(Miner.class, player0);

        /* Wait for the miner to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the miner goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(miner.getTarget(), stoneMine0.getPosition());

        headquarter0.blockDeliveryOfMaterial(MINER);

        stoneMine0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, stoneMine0.getFlag().getPosition());

        assertEquals(miner.getPosition(), stoneMine0.getFlag().getPosition());
        assertNotEquals(miner.getTarget(), headquarter0.getPosition());
        assertFalse(miner.isInsideBuilding());
        assertNull(stoneMine0.getWorker());
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
