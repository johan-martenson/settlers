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
import org.appland.settlers.model.IronMine;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.MINER;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import org.appland.settlers.model.Miner;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.SMALL;
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
public class TestIronMine {

    @Test
    public void testIronMineOnlyNeedsFourPlancksForConstruction() throws Exception {

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

        /* Placing iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point22);
        
        /* Deliver four plancks */
        Cargo planckCargo = new Cargo(PLANCK, map);

        ironMine0.putCargo(planckCargo);
        ironMine0.putCargo(planckCargo);
        ironMine0.putCargo(planckCargo);
        ironMine0.putCargo(planckCargo);
    
        /* Verify that this is enough to construct the iron mine */
        for (int i = 0; i < 100; i++) {
            assertTrue(ironMine0.underConstruction());
            
            map.stepTime();
        }

        assertTrue(ironMine0.ready());
    }

    @Test
    public void testIronMineCannotBeConstructedWithTooFewPlancks() throws Exception {

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

        /* Placing iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point22);
        
        /* Deliver two planck and three stone */
        Cargo planckCargo = new Cargo(PLANCK, map);

        ironMine0.putCargo(planckCargo);
        ironMine0.putCargo(planckCargo);
        ironMine0.putCargo(planckCargo);

        /* Verify that this is not enough to construct the iron mine */
        for (int i = 0; i < 500; i++) {
            assertTrue(ironMine0.underConstruction());

            map.stepTime();
        }

        assertFalse(ironMine0.ready());
    }

    @Test
    public void testConstructIronMine() throws Exception {
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
        
        /* Place a ironmine*/
        Building mine = map.placeBuilding(new IronMine(player0), point0);
        
        assertTrue(mine.underConstruction());
        
        Utils.constructHouse(mine, map);
        
        assertTrue(mine.ready());
    }
    
    @Test
    public void testIronmineIsNotMilitary() throws Exception {
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
        
        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);
        
        /* Verify that the mine is not a military building */
        assertFalse(mine.isMilitaryBuilding());
        
        Utils.constructHouse(mine, map);
        
        assertFalse(mine.isMilitaryBuilding());
    }
    
    @Test
    public void testIronmineUnderConstructionNotNeedsMiner() throws Exception {
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
        
        /* Place a iron mine*/
        Building mine = map.placeBuilding(new IronMine(player0), point0);
        
        /* Verify that the unfinished mine does not need a worker */
        assertFalse(mine.needsWorker());
    }
    
    @Test
    public void testFinishedIronmineNeedsMiner() throws Exception {
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
        
        /* Place a iron mine*/
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        Utils.constructHouse(mine, map);
        
        /* Verify that the finished mine needs a worker */
        assertTrue(mine.needsWorker());
    }
    
    @Test
    public void testMinerIsAssignedToFinishedIronmine() throws Exception {
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

        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);
        
        /* Place a road between the headquarter and the ironmine */
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

        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);
        
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
        
        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Construct the iron mine */
        constructHouse(mine, map);
        
        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine, map);
        
        assertTrue(miner.isInsideBuilding());
        
        /* Run the game logic 99 times and make sure the miner stays in the house */
        int i;
        for (i = 0; i < 99; i++) {
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
    public void testMinerMinesIron() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);
        
        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Construct the iron mine */
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
        
        /* Verify that the miner mines for iron */
        int amountIron = map.getAmountOfMineralAtPoint(IRON, point0);
        
        int i;
        for (i = 0; i < 50; i++) {
            assertTrue(miner.isMining());
            map.stepTime();
        }
        
        /* Verify that the miner finishes mining on time and has iron */
        assertFalse(miner.isMining());
        assertFalse(miner.isInsideBuilding());
        assertNotNull(miner.getCargo());
        assertEquals(miner.getCargo().getMaterial(), IRON);
        assertTrue(map.getAmountOfMineralAtPoint(IRON, point0) < amountIron);
    }

    @Test
    public void testIronmineGoesToFlagWithCargoAndBack() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Building building0 = map.placeBuilding(new Headquarter(player0), hqPoint);
        
        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, building0.getFlag(), mine.getFlag());
        
        /* Construct the iron mine */
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
        
        /* Wait for the miner to mine iron */
        Utils.fastForward(50, map);
        
        /* Verify that the miner leaves the iron at the flag */
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
        GameMap map = new GameMap(players, 10, 10);
        
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Point point0 = new Point(2, 2);
        try {
            map.placeBuilding(new IronMine(player0), point0);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertEquals(map.getBuildings().size(), 1);
    }

    @Test
    public void testIronmineRunsOutOfIron() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, SMALL, map);

        /* Remove all iron but one */
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountOfMineralAtPoint(IRON, point0) > 1) {
                map.mineMineralAtPoint(IRON, point0);
            }
        }
        
        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Building building0 = map.placeBuilding(new Headquarter(player0), hqPoint);
        
        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, building0.getFlag(), mine.getFlag());
        
        /* Construct the iron mine */
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
        
        /* Wait for the miner to mine iron */
        Utils.fastForward(50, map);
        
        /* Wait for the miner to leave the iron at the flag */
        assertEquals(miner.getTarget(), mine.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getFlag().getPosition());
        
        assertNull(miner.getCargo());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getPosition());
        
        assertTrue(miner.isInsideBuilding());

        /* Verify that the iron is gone and that the miner gets no iron */
        assertEquals(map.getAmountOfMineralAtPoint(IRON, point0), 0);

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());
            
            map.stepTime();
        }
    }

    @Test
    public void testIronmineWithoutIronProducesNothing() throws Exception {
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
        
        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, building0.getFlag(), mine.getFlag());
        
        /* Construct the iron mine */
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
        
        /* Verify that there is no iron and that the miner gets no iron */
        assertEquals(map.getAmountOfMineralAtPoint(IRON, point0), 0);

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());
            
            map.stepTime();
        }
    }
    
    @Test
    public void testIronmineWithoutFoodProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);
        
        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Building building0 = map.placeBuilding(new Headquarter(player0), hqPoint);
        
        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Construct the iron mine */
        constructHouse(mine, map);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine, map);
        
        assertTrue(miner.isInsideBuilding());
        
        /* Wait for the miner to rest */
        Utils.fastForward(100, map);
        
        /* Verify that the miner gets no iron */

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());
            
            map.stepTime();
        }
    }

    @Test
    public void testMiningConsumesFood() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);
        
        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Construct the iron mine */
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
        
        /* Verify that the miner mines for iron */
        assertEquals(mine.getAmount(BREAD), 1);
        
        Utils.fastForward(50, map);
        
        /* Verify that the miner consumed the bread */
        assertEquals(mine.getAmount(BREAD), 0);
    }

    @Test
    public void testIronmineCanConsumeAllTypesOfFood() throws Exception {

        /* Start new game with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);
        
        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Construct the iron mine */
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
            Utils.fastForwardUntilWorkerCarriesCargo(map, miner, IRON);

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
    public void testIronMineWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point0);

        /* Finish construction of the iron mine */
        Utils.constructHouse(ironMine0, map);

        /* Occupy the iron mine */
        Utils.occupyBuilding(new Miner(player0, map), ironMine0, map);

        /* Deliver material to the iron mine */
        Cargo fishCargo = new Cargo(FISH, map);
        
        ironMine0.putCargo(fishCargo);
        ironMine0.putCargo(fishCargo);
        
        /* Let the miner rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce a new iron cargo */
        Utils.fastForward(50, map);

        Worker miner = ironMine0.getWorker();

        assertNotNull(miner.getCargo());

        /* Verify that the miner puts the iron cargo at the flag */
        assertEquals(miner.getTarget(), ironMine0.getFlag().getPosition());
        assertTrue(ironMine0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, ironMine0.getFlag().getPosition());

        assertNull(miner.getCargo());
        assertFalse(ironMine0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait for the worker to go back to the iron mine */
        assertEquals(miner.getTarget(), ironMine0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, ironMine0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(miner.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(miner.getTarget(), ironMine0.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, ironMine0.getFlag().getPosition());
        
        assertNull(miner.getCargo());
        assertEquals(ironMine0.getFlag().getStackedCargo().size(), 2);
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
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point0);

        /* Finish construction of the iron mine */
        Utils.constructHouse(ironMine0, map);

        /* Deliver material to the iron mine */
        Cargo fishCargo = new Cargo(FISH, map);
        
        ironMine0.putCargo(fishCargo);
        ironMine0.putCargo(fishCargo);

        /* Occupy the iron mine */
        Utils.occupyBuilding(new Miner(player0, map), ironMine0, map);

        /* Let the miner rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce a new iron cargo */
        Utils.fastForward(50, map);

        Worker miner = ironMine0.getWorker();

        assertNotNull(miner.getCargo());

        /* Verify that the miner puts the iron cargo at the flag */
        assertEquals(miner.getTarget(), ironMine0.getFlag().getPosition());
        assertTrue(ironMine0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, ironMine0.getFlag().getPosition());

        assertNull(miner.getCargo());
        assertFalse(ironMine0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = ironMine0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), ironMine0.getFlag().getPosition());
    
        /* Connect the iron mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironMine0.getFlag());
    
        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);
    
        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(ironMine0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
    
        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), ironMine0.getFlag().getPosition());
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);
        
        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());
        
        int amount = headquarter0.getAmount(IRON);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());
        
        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(IRON), amount + 1);
    }

    @Test
    public void testMinerGoesBackToStorageWhenIronMineIsDestroyed() throws Exception {

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
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Placing iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point0);

        /* Finish construction of the iron mine */
        Utils.constructHouse(ironMine0, map);

        /* Occupy the iron mine */
        Utils.occupyBuilding(new Miner(player0, map), ironMine0, map);
        
        /* Destroy the iron mine */
        Worker miner = ironMine0.getWorker();
        
        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), ironMine0.getPosition());

        ironMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(MINER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerGoesBackOnToStorageOnRoadsIfPossibleWhenIronMineIsDestroyed() throws Exception {

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
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Placing iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point0);

        /* Connect the iron mine with the headquarter */
        map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the iron mine */
        Utils.constructHouse(ironMine0, map);

        /* Occupy the iron mine */
        Utils.occupyBuilding(new Miner(player0, map), ironMine0, map);
        
        /* Destroy the iron mine */
        Worker miner = ironMine0.getWorker();
        
        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), ironMine0.getPosition());

        ironMine0.tearDown();

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
    public void testProductionInMineCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Put a small mountain on the map */
        Point point1 = new Point(10, 6);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);
        
        /* Connect the iron mine and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Point point5 = new Point(11, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4, point5);
        
        /* Finish the iron mine */
        Utils.constructHouse(ironMine0, map);
        
        /* Deliver material to the iron mine */
        Cargo fishCargo = new Cargo(FISH, map);
        
        ironMine0.putCargo(fishCargo);
        ironMine0.putCargo(fishCargo);

        /* Assign a worker to the iron mine */
        Miner miner = new Miner(player0, map);
        
        Utils.occupyBuilding(miner, ironMine0, map);
        
        assertTrue(miner.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the miner to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, miner);
        
        assertEquals(miner.getCargo().getMaterial(), IRON);

        /* Wait for the worker to deliver the cargo */
        assertEquals(miner.getTarget(), ironMine0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, ironMine0.getFlag().getPosition());

        /* Stop production and verify that no iron is produced */
        ironMine0.stopProduction();
        
        assertFalse(ironMine0.isProductionEnabled());
        
        for (int i = 0; i < 300; i++) {
            assertNull(miner.getCargo());
            
            map.stepTime();
        }
    }

    @Test
    public void testProductionInMineCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 6);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);
        
        /* Connect the iron mine and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Point point5 = new Point(11, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4, point5);
        
        /* Finish the iron mine */
        Utils.constructHouse(ironMine0, map);
        
        /* Assign a worker to the iron mine */
        Miner miner = new Miner(player0, map);
        
        Utils.occupyBuilding(miner, ironMine0, map);
        
        assertTrue(miner.isInsideBuilding());

        /* Deliver material to the iron mine */
        Cargo fishCargo = new Cargo(FISH, map);
        
        ironMine0.putCargo(fishCargo);
        ironMine0.putCargo(fishCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the miner to produce iron */
        Utils.fastForwardUntilWorkerProducesCargo(map, miner);

        assertEquals(miner.getCargo().getMaterial(), IRON);

        /* Wait for the worker to deliver the cargo */
        assertEquals(miner.getTarget(), ironMine0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, ironMine0.getFlag().getPosition());

        /* Stop production */
        ironMine0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(miner.getCargo());
            
            map.stepTime();
        }

        /* Resume production and verify that the iron mine produces iron again */
        ironMine0.resumeProduction();

        assertTrue(ironMine0.isProductionEnabled());

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
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place iron mine*/
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Finish construction of the iron mine */
        Utils.constructHouse(ironMine0, map);
        
        /* Connect the iron mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironMine0.getFlag());

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
        Utils.putIronAtSurroundingTiles(point4, LARGE, map);

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

        /* Place iron mine close to the new border */
        IronMine ironMine0 = map.placeBuilding(new IronMine(player0), point4);

        /* Finish construction of the iron mine */
        Utils.constructHouse(ironMine0, map);

        /* Occupy the iron mine */
        Miner worker = Utils.occupyBuilding(new Miner(player0, map), ironMine0, map);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }
}
