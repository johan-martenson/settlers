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
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.IronMine;
import org.appland.settlers.model.IronSmelter;
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

import static java.awt.Color.*;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.SMALL;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestIronMine {

    @Test
    public void testIronMineOnlyNeedsFourPlanksForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put a small mountain on the map */
        Point point0 = new Point(6, 12);
        Utils.surroundPointWithMinableMountain(point0, map);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point0);

        /* Deliver four planks */
        Cargo plankCargo = new Cargo(PLANK, map);

        ironMine0.putCargo(plankCargo);
        ironMine0.putCargo(plankCargo);
        ironMine0.putCargo(plankCargo);
        ironMine0.putCargo(plankCargo);

        /* Assign builder */
        Utils.assignBuilder(ironMine0);

        /* Verify that this is enough to construct the iron mine */
        for (int i = 0; i < 100; i++) {
            assertTrue(ironMine0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(ironMine0.isReady());
    }

    @Test
    public void testIronMineCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put a small mountain on the map */
        Point point22 = new Point(6, 12);
        Utils.surroundPointWithMinableMountain(point22, map);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point22);

        /* Deliver two plank and three stone */
        Cargo plankCargo = new Cargo(PLANK, map);

        ironMine0.putCargo(plankCargo);
        ironMine0.putCargo(plankCargo);
        ironMine0.putCargo(plankCargo);

        /* Assign builder */
        Utils.assignBuilder(ironMine0);

        /* Verify that this is not enough to construct the iron mine */
        for (int i = 0; i < 500; i++) {
            assertTrue(ironMine0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(ironMine0.isReady());
    }

    @Test
    public void testConstructIronMine() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 14);
        Utils.surroundPointWithMinableMountain(point0, map);

        /* Place a headquarter */
        Point point1 = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        assertTrue(mine.isPlanned());

        constructHouse(mine);

        assertTrue(mine.isReady());
    }

    @Test
    public void testIronMineIsNotMilitary() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 14);
        Utils.surroundPointWithMinableMountain(point0, map);

        /* Place a headquarter */
        Point point1 = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Verify that the mine is not a military building */
        assertFalse(mine.isMilitaryBuilding());

        constructHouse(mine);

        assertFalse(mine.isMilitaryBuilding());
    }

    @Test
    public void testIronMineUnderConstructionNotNeedsMiner() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 14);
        Utils.surroundPointWithMinableMountain(point0, map);

        /* Place a headquarter */
        Point point1 = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Verify that the unfinished mine does not need a worker */
        assertFalse(mine.needsWorker());
    }

    @Test
    public void testFinishedIronMineNeedsMiner() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 14);
        Utils.surroundPointWithMinableMountain(point0, map);

        /* Place a headquarter */
        Point point1 = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        constructHouse(mine);

        /* Verify that the finished mine needs a worker */
        assertTrue(mine.needsWorker());
    }

    @Test
    public void testMinerIsAssignedToFinishedIronMine() throws Exception {

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
        Utils.surroundPointWithMinableMountain(point1, map);

        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point1);

        /* Place a road between the headquarter and the iron mine */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the mine */
        constructHouse(mine);

        assertTrue(mine.isReady());

        /* Run game logic twice, once to place courier and once to place miner */
        Utils.fastForward(2, map);

        assertTrue(map.getWorkers().size() >= 3);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Miner.class);

        /* Keep running the game loop and make sure no more workers are allocated */
        Utils.fastForward(200, map);

        assertTrue(map.getWorkers().size() >= 3);
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
        Utils.surroundPointWithMinableMountain(point1, map);

        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point1);

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
        Utils.surroundPointWithMinableMountain(point0, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Construct the iron mine */
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
    public void testMinerMinesIron() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Construct the iron mine */
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

        /* Verify that the miner mines for iron */
        int amountIron = map.getAmountOfMineralAtPoint(IRON, point0);

        for (int i = 0; i < 50; i++) {
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
    public void testIronMineGoesToFlagWithCargoAndBack() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the iron mine */
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
    public void testIronCargoIsDeliveredToMintWhichIsCloserThanHeadquarters() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Remove all iron from the headquarters */
        Utils.adjustInventoryTo(headquarter, IRON, 0);

        /* Place iron smelter */
        Point point4 = new Point(10, 4);
        IronSmelter ironSmelter = map.placeBuilding(new IronSmelter(player0), point4);

        /* Connect the iron smelter to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), headquarter.getFlag());

        /* Place mountain */
        Point point1 = new Point(14, 4);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place the iron mine */
        IronMine ironMine = map.placeBuilding(new IronMine(player0), point1);

        /* Connect the iron mine with the iron smelter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironMine.getFlag(), ironSmelter.getFlag());

        /* Wait for the iron mine and the iron smelter to get constructed and occupied */
        Utils.waitForBuildingsToBeConstructed(ironMine, ironSmelter);

        Utils.waitForNonMilitaryBuildingsToGetPopulated(ironMine, ironSmelter);

        /* Wait for the courier on the road between the iron smelter and the iron mine hut to have an iron cargo */
        Utils.deliverCargo(ironMine, FISH);

        Utils.waitForFlagToGetStackedCargo(map, ironMine.getFlag(), 1);

        assertEquals(ironMine.getFlag().getStackedCargo().get(0).getMaterial(), IRON);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the iron to the iron smelter (and not the headquarters) */
        assertEquals(ironMine.getAmount(IRON), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), ironSmelter.getPosition());

        assertEquals(ironSmelter.getAmount(IRON), 1);
    }

    @Test
    public void testIronIsNotDeliveredToStorehouseUnderConstruction() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory */
        Utils.clearInventory(headquarter, BREAD, FISH, MEAT, IRON, PLANK, STONE);

        /* Place storehouse */
        Point point4 = new Point(10, 4);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Connect the storehouse to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Place mountain */
        Point point1 = new Point(14, 4);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1,LARGE, map);

        /* Place the iron mine */
        IronMine ironMine = map.placeBuilding(new IronMine(player0), point1);

        /* Connect the iron mine with the storehouse */
        Road road0 = map.placeAutoSelectedRoad(player0, ironMine.getFlag(), storehouse.getFlag());

        /* Deliver the needed material to construct the iron mine */
        Utils.deliverCargos(ironMine, PLANK, 4);

        /* Wait for the iron mine to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(ironMine);

        Utils.waitForNonMilitaryBuildingToGetPopulated(ironMine);

        /* Wait for the courier on the road between the storehouse and the iron mine to have an iron cargo */
        Utils.deliverCargo(ironMine, BREAD);

        Utils.waitForFlagToGetStackedCargo(map, ironMine.getFlag(), 1);

        assertEquals(ironMine.getFlag().getStackedCargo().get(0).getMaterial(), IRON);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the storehouse's flag so that it can continue to the headquarters */
        assertEquals(headquarter.getAmount(IRON), 0);
        assertEquals(ironMine.getAmount(IRON), 0);
        assertFalse(storehouse.needsMaterial(IRON));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().get(0).getMaterial().equals(IRON));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testIronIsNotDeliveredTwiceToBuildingThatOnlyNeedsOne() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory */
        Utils.clearInventory(headquarter, PLANK, STONE, BREAD, FISH, MEAT, IRON);

        /* Place iron smelter */
        Point point4 = new Point(10, 4);
        IronSmelter ironSmelter = map.placeBuilding(new IronSmelter(player0), point4);

        /* Construct the iron smelter */
        Utils.constructHouse(ironSmelter);

        /* Stop production in the iron smelter */
        ironSmelter.stopProduction();

        /* Connect the iron smelter to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), headquarter.getFlag());

        /* Place mountain */
        Point point1 = new Point(14, 4);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1,LARGE, map);

        /* Place the iron mine */
        IronMine ironMine = map.placeBuilding(new IronMine(player0), point1);

        /* Connect the iron mine with the iron smelter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironMine.getFlag(), ironSmelter.getFlag());

        /* Deliver the needed material to construct the iron mine */
        Utils.deliverCargos(ironMine, PLANK, 4);

        /* Wait for the iron mine to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(ironMine);

        Utils.waitForNonMilitaryBuildingToGetPopulated(ironMine);

        /* Wait for the flag on the road between the iron smelter and the iron mine to have an iron cargo */
        Utils.deliverCargo(ironMine, MEAT);

        Utils.waitForFlagToGetStackedCargo(map, ironMine.getFlag(), 1);

        assertEquals(ironMine.getFlag().getStackedCargo().get(0).getMaterial(), IRON);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that no stone is delivered from the headquarters */
        Utils.adjustInventoryTo(headquarter, IRON, 1);

        assertEquals(ironSmelter.getAmount(IRON), 0);
        assertEquals(ironSmelter.getCanHoldAmount(IRON), 1);
        assertEquals(ironSmelter.getCanHoldAmount(IRON) - ironSmelter.getAmount(IRON), 1);
        assertFalse(ironSmelter.needsMaterial(IRON));

        for (int i = 0; i < 200; i++) {
            assertNull(headquarter.getWorker().getCargo());

            map.stepTime();
        }

        assertEquals(headquarter.getAmount(IRON), 1);
    }

    @Test
    public void testCanNotPlaceMineOnGrass() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 12, 12);

        /* Place headquarter */
        Point point0 = new Point(7, 7);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to place a mine on grass */
        Point point1 = new Point(2, 2);
        try {
            map.placeBuilding(new IronMine(player0), point1);
            fail();
        } catch (Exception e) {}

        assertEquals(map.getBuildings().size(), 1);
    }

    @Test
    public void testIronMineRunsOutOfIron() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, SMALL, map);

        /* Remove all iron but one */
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountOfMineralAtPoint(IRON, point0) > 1) {
                map.mineMineralAtPoint(IRON, point0);
            }
        }

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the iron mine */
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

        /* Wait for the miner to mine iron */
        assertFalse(mine.isOutOfNaturalResources());

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

        /* Verify that the mine can run out of resources */
        assertTrue(mine.isOutOfNaturalResources());
    }

    @Test
    public void testIronMineWithoutIronProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the iron mine */
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

        /* Verify that there is no iron and that the miner gets no iron */
        assertEquals(map.getAmountOfMineralAtPoint(IRON, point0), 0);

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testIronMineWithoutFoodProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Construct the iron mine */
        constructHouse(mine);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Construct the iron mine */
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

        /* Verify that the miner mines for iron */
        assertEquals(mine.getAmount(BREAD), 1);

        Utils.fastForward(50, map);

        /* Verify that the miner consumed the bread */
        assertEquals(mine.getAmount(BREAD), 0);
    }

    @Test
    public void testIronMineCanConsumeAllTypesOfFood() throws Exception {

        /* Start new game with one player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a iron mine */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        /* Construct the iron mine */
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point0);

        /* Finish construction of the iron mine */
        constructHouse(ironMine0);

        /* Occupy the iron mine */
        Utils.occupyBuilding(new Miner(player0, map), ironMine0);

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
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point0);

        /* Finish construction of the iron mine */
        constructHouse(ironMine0);

        /* Deliver material to the iron mine */
        Cargo fishCargo = new Cargo(FISH, map);

        ironMine0.putCargo(fishCargo);
        ironMine0.putCargo(fishCargo);

        /* Occupy the iron mine */
        Utils.occupyBuilding(new Miner(player0, map), ironMine0);

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

        /* Remove the resources the iron mine needs from the headquarter */
        Utils.adjustInventoryTo(headquarter0, MEAT, 0);
        Utils.adjustInventoryTo(headquarter0, BREAD, 0);
        Utils.adjustInventoryTo(headquarter0, FISH, 0);

        /* Connect the iron mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironMine0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), ironMine0.getFlag().getPosition());
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point0);

        /* Finish construction of the iron mine */
        constructHouse(ironMine0);

        /* Occupy the iron mine */
        Utils.occupyBuilding(new Miner(player0, map), ironMine0);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point0);

        /* Connect the iron mine with the headquarter */
        map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the iron mine */
        constructHouse(ironMine0);

        /* Occupy the iron mine */
        Utils.occupyBuilding(new Miner(player0, map), ironMine0);

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
        for (Point point : miner.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testProductionInMineCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 6);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Connect the iron mine and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Point point5 = new Point(11, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4, point5);

        /* Finish the iron mine */
        constructHouse(ironMine0);

        /* Deliver material to the iron mine */
        Cargo fishCargo = new Cargo(FISH, map);

        ironMine0.putCargo(fishCargo);
        ironMine0.putCargo(fishCargo);

        /* Assign a worker to the iron mine */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, ironMine0);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 6);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Connect the iron mine and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Point point5 = new Point(11, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4, point5);

        /* Finish the iron mine */
        constructHouse(ironMine0);

        /* Assign a worker to the iron mine */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, ironMine0);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 10);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Place headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point0);

        /* Finish construction of the iron mine */
        constructHouse(ironMine0);

        /* Connect the iron mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironMine0.getFlag());

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
        Utils.surroundPointWithMinableMountain(point4, map);
        Utils.putIronAtSurroundingTiles(point4, LARGE, map);

        /* Place player 2's headquarter */
        Point point10 = new Point(70, 70);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place iron mine close to the new border */
        IronMine ironMine0 = map.placeBuilding(new IronMine(player0), point4);

        /* Finish construction of the iron mine */
        constructHouse(ironMine0);

        /* Occupy the iron mine */
        Miner worker = Utils.occupyBuilding(new Miner(player0, map), ironMine0);

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

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Put a small mountain on the map */
        Point point2 = new Point(13, 5);
        Utils.surroundPointWithMinableMountain(point2, map);
        Utils.putIronAtSurroundingTiles(point2, LARGE, map);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, ironMine0.getFlag());

        /* Wait for the miner to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0);

        Miner miner = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Miner) {
                miner = (Miner) worker;
            }
        }

        assertNotNull(miner);
        assertEquals(miner.getTarget(), ironMine0.getPosition());

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

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Put a small mountain on the map */
        Point point2 = new Point(13, 5);
        Utils.surroundPointWithMinableMountain(point2, map);
        Utils.putIronAtSurroundingTiles(point2, LARGE, map);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, ironMine0.getFlag());

        /* Wait for the miner to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0);

        Miner miner = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Miner) {
                miner = (Miner) worker;
            }
        }

        assertNotNull(miner);
        assertEquals(miner.getTarget(), ironMine0.getPosition());

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
        assertEquals(miner.getTarget(), ironMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, ironMine0.getFlag().getPosition());

        /* Verify that the miner goes out to miner instead of going directly back */
        assertNotEquals(miner.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMinerReturnsToStorageIfIronMineIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Put a small mountain on the map */
        Point point2 = new Point(13, 5);
        Utils.surroundPointWithMinableMountain(point2, map);
        Utils.putIronAtSurroundingTiles(point2, LARGE, map);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, ironMine0.getFlag());

        /* Wait for the miner to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0);

        Miner miner = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Miner) {
                miner = (Miner) worker;
            }
        }

        assertNotNull(miner);
        assertEquals(miner.getTarget(), ironMine0.getPosition());

        /* Wait for the miner to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, flag0.getPosition());

        map.stepTime();

        /* See that the miner has started walking */
        assertFalse(miner.isExactlyAtPoint());

        /* Tear down the iron mine */
        ironMine0.tearDown();

        /* Verify that the miner continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, ironMine0.getFlag().getPosition());

        assertEquals(miner.getPosition(), ironMine0.getFlag().getPosition());

        /* Verify that the miner goes back to storage */
        assertEquals(miner.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMinerGoesOffroadBackToClosestStorageWhenIronMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point1 = new Point(17, 17);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place iron Mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Finish construction of the iron Mine */
        constructHouse(ironMine0);

        /* Occupy the iron mine */
        Utils.occupyBuilding(new Miner(player0, map), ironMine0);

        /* Place a second storage closer to the iron Mine */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the iron Mine */
        Worker miner = ironMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), ironMine0.getPosition());

        ironMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(MINER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, storehouse0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerReturnsOffroadAndAvoidsBurningStorageWhenIronMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point1 = new Point(17, 17);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Finish construction of the iron mine */
        constructHouse(ironMine0);

        /* Occupy the iron mine */
        Utils.occupyBuilding(new Miner(player0, map), ironMine0);

        /* Place a second storage closer to the iron mine */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

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
    public void testMinerReturnsOffroadAndAvoidsDestroyedStorageWhenIronMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point1 = new Point(17, 17);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Finish construction of the iron mine */
        constructHouse(ironMine0);

        /* Occupy the iron mine */
        Utils.occupyBuilding(new Miner(player0, map), ironMine0);

        /* Place a second storage closer to the iron mine */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

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
    public void testMinerReturnsOffroadAndAvoidsUnfinishedStorageWhenIronMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point1 = new Point(17, 17);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Finish construction of the iron mine */
        constructHouse(ironMine0);

        /* Occupy the iron mine */
        Utils.occupyBuilding(new Miner(player0, map), ironMine0);

        /* Place a second storage closer to the iron mine */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

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
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Place road to connect the headquarter and the iron mine */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironMine0.getFlag());

        /* Finish construction of the iron mine */
        constructHouse(ironMine0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, ironMine0.getFlag().getPosition());

        /* Tear down the building */
        ironMine0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(worker.getTarget(), ironMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, ironMine0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testIronMineWithoutResourcesHasZeroProductivity() throws Exception {

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
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place iron mine */
        Building ironMine = map.placeBuilding(new IronMine(player0), point1);

        /* Finish construction of the iron mine */
        constructHouse(ironMine);

        /* Populate the iron mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), ironMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), ironMine);
        assertEquals(ironMine.getWorker(), miner0);

        /* Verify that the productivity is 0% when the iron mine doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(ironMine.getFlag().getStackedCargo().isEmpty());
            assertNull(miner0.getCargo());
            assertEquals(ironMine.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testIronMineWithAbundantResourcesHasFullProductivity() throws Exception {

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
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place iron mine */
        Building ironMine = map.placeBuilding(new IronMine(player0), point1);

        /* Finish construction of the iron mine */
        constructHouse(ironMine);

        /* Populate the iron mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), ironMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), ironMine);
        assertEquals(ironMine.getWorker(), miner0);

        /* Connect the iron mine with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironMine.getFlag());

        /* Make the iron mine create some iron with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (ironMine.needsMaterial(FISH)) {
                ironMine.putCargo(new Cargo(FISH, map));
            }
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(ironMine.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (ironMine.needsMaterial(FISH)) {
                ironMine.putCargo(new Cargo(FISH, map));
            }

            assertEquals(ironMine.getProductivity(), 100);
        }
    }

    @Test
    public void testIronMineLosesProductivityWhenResourcesRunOut() throws Exception {

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
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place iron mine */
        Building ironMine = map.placeBuilding(new IronMine(player0), point1);

        /* Finish construction of the iron mine */
        constructHouse(ironMine);

        /* Populate the iron mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), ironMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), ironMine);
        assertEquals(ironMine.getWorker(), miner0);

        /* Connect the iron mine with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironMine.getFlag());

        /* Make the iron mine create some iron with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (ironMine.needsMaterial(FISH) && ironMine.getAmount(FISH) < 2) {
                ironMine.putCargo(new Cargo(FISH, map));
            }
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(ironMine.getProductivity(), 100);

        for (int i = 0; i < 5000; i++) {
            map.stepTime();
        }

        assertEquals(ironMine.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedIronMineHasNoProductivity() throws Exception {

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
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place iron mine */
        Building ironMine = map.placeBuilding(new IronMine(player0), point1);

        /* Finish construction of the iron mine */
        constructHouse(ironMine);

        /* Verify that the unoccupied iron mine is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(ironMine.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testIronMineCanProduce() throws Exception {

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
        Utils.surroundPointWithMinableMountain(point1, map);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Finish construction of the iron mine */
        constructHouse(ironMine0);

        /* Populate the iron mine */
        Worker miner = Utils.occupyBuilding(new Miner(player0, map), ironMine0);

        /* Verify that the iron mine can produce */
        assertTrue(ironMine0.canProduce());
    }

    @Test
    public void testIronMineReportsCorrectOutput() throws Exception {

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
        Utils.surroundPointWithMinableMountain(point1, map);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Construct the iron mine */
        constructHouse(ironMine0);

        /* Verify that the reported output is correct */
        assertEquals(ironMine0.getProducedMaterial().length, 1);
        assertEquals(ironMine0.getProducedMaterial()[0], IRON);
    }

    @Test
    public void testIronMineReportsCorrectMaterialsNeededForConstruction() throws Exception {

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
        Utils.surroundPointWithMinableMountain(point1, map);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(ironMine0.getTypesOfMaterialNeeded().size(), 1);
        assertTrue(ironMine0.getTypesOfMaterialNeeded().contains(PLANK));
        assertEquals(ironMine0.getCanHoldAmount(PLANK), 4);

        for (Material material : Material.values()) {
            if (material == PLANK) {
                continue;
            }

            assertEquals(ironMine0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testIronMineReportsCorrectMaterialsNeededForProduction() throws Exception {

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
        Utils.surroundPointWithMinableMountain(point1, map);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Construct the iron mine */
        constructHouse(ironMine0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(ironMine0.getTypesOfMaterialNeeded().size(), 3);
        assertTrue(ironMine0.getTypesOfMaterialNeeded().contains(BREAD));
        assertTrue(ironMine0.getTypesOfMaterialNeeded().contains(MEAT));
        assertTrue(ironMine0.getTypesOfMaterialNeeded().contains(FISH));
        assertEquals(ironMine0.getCanHoldAmount(BREAD), 1);
        assertEquals(ironMine0.getCanHoldAmount(MEAT), 1);
        assertEquals(ironMine0.getCanHoldAmount(FISH), 1);

        for (Material material : Material.values()) {
            if (material == BREAD || material == MEAT || material == FISH) {
                continue;
            }

            assertEquals(ironMine0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testIronMineWaitsWhenFlagIsFull() throws Exception {

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
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place iron mine */
        Building ironMine = map.placeBuilding(new IronMine(player0), point1);

        /* Connect the iron mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironMine.getFlag(), headquarter.getFlag());

        /* Wait for the iron mine to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(ironMine);
        Utils.waitForNonMilitaryBuildingToGetPopulated(ironMine);

        /* Give material to the iron mine */
        Utils.putCargoToBuilding(ironMine, BREAD);
        Utils.putCargoToBuilding(ironMine, BREAD);
        Utils.putCargoToBuilding(ironMine, FISH);
        Utils.putCargoToBuilding(ironMine, FISH);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, ironMine.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the iron mine waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(ironMine.getFlag().getStackedCargo().size(), 8);
            assertNull(ironMine.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the iron mine with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, ironMine.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(ironMine.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(ironMine.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(ironMine.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, ironMine.getWorker(), IRON);
    }

    @Test
    public void testIronMineDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

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
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place iron mine */
        IronMine ironMine = map.placeBuilding(new IronMine(player0), point1);

        /* Connect the iron mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironMine.getFlag(), headquarter.getFlag());

        /* Wait for the iron mine to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(ironMine);
        Utils.waitForNonMilitaryBuildingToGetPopulated(ironMine);

        /* Give material to the iron mine */
        Utils.putCargoToBuilding(ironMine, BREAD);
        Utils.putCargoToBuilding(ironMine, BREAD);
        Utils.putCargoToBuilding(ironMine, FISH);
        Utils.putCargoToBuilding(ironMine, FISH);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, ironMine.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The iron mine waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(ironMine.getFlag().getStackedCargo().size(), 8);
            assertNull(ironMine.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the iron mine with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, ironMine.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(ironMine.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(ironMine.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(ironMine.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, ironMine.getWorker(), IRON);

        /* Wait for the worker to put the cargo on the flag */
        assertEquals(ironMine.getWorker().getTarget(), ironMine.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironMine.getWorker(), ironMine.getFlag().getPosition());

        assertEquals(ironMine.getFlag().getStackedCargo().size(), 8);

        /* Verify that the iron mine doesn't produce anything because the flag is full */
        for (int i = 0; i < 400; i++) {
            assertEquals(ironMine.getFlag().getStackedCargo().size(), 8);
            assertNull(ironMine.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testWhenIronDeliveryAreBlockedIronMineFillsUpFlagAndThenStops() throws Exception {

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
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place Iron mine */
        IronMine ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Place road to connect the iron mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());

        /* Wait for the iron mine to get constructed and occupied */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(ironMine0);

        Worker miner0 = Utils.waitForNonMilitaryBuildingToGetPopulated(ironMine0);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), ironMine0);
        assertEquals(ironMine0.getWorker(), miner0);

        /* Add a lot of material to the headquarter for the iron mine to consume */
        Utils.adjustInventoryTo(headquarter0, MEAT, 40);
        Utils.adjustInventoryTo(headquarter0, BREAD, 40);

        /* Block storage of iron */
        headquarter0.blockDeliveryOfMaterial(IRON);

        /* Verify that the iron mine puts eight iron ores on the flag and then stops */
        Utils.waitForFlagToGetStackedCargo(map, ironMine0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner0, ironMine0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(ironMine0.getFlag().getStackedCargo().size(), 8);
            assertTrue(miner0.isInsideBuilding());

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), IRON);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndIronMineIsTornDown() throws Exception {

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
        Utils.surroundPointWithMinableMountain(point2, map);
        Utils.putIronAtSurroundingTiles(point2, LARGE, map);

        /* Place iron mine */
        IronMine ironMine0 = map.placeBuilding(new IronMine(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the iron mine */
        Road road1 = map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the iron mine and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, ironMine0);

        /* Add a lot of material to the headquarter for the iron mine to consume */
        Utils.adjustInventoryTo(headquarter0, MEAT, 40);
        Utils.adjustInventoryTo(headquarter0, BREAD, 40);

        /* Wait for the iron mine and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, ironMine0);

        Worker miner0 = ironMine0.getWorker();

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), ironMine0);
        assertEquals(ironMine0.getWorker(), miner0);

        /* Verify that the worker goes to the storage when the iron mine is torn down */
        headquarter0.blockDeliveryOfMaterial(MINER);

        ironMine0.tearDown();

        map.stepTime();

        assertFalse(miner0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner0, ironMine0.getFlag().getPosition());

        assertEquals(miner0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, miner0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(miner0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndIronMineIsTornDown() throws Exception {

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
        Utils.surroundPointWithMinableMountain(point2, map);
        Utils.putIronAtSurroundingTiles(point2, LARGE, map);

        /* Place iron mine */
        IronMine ironMine0 = map.placeBuilding(new IronMine(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the iron mine */
        Road road1 = map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the iron mine and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, ironMine0);

        /* Add a lot of material to the headquarter for the iron mine to consume */
        Utils.adjustInventoryTo(headquarter0, MEAT, 40);
        Utils.adjustInventoryTo(headquarter0, BREAD, 40);

        /* Wait for the iron mine and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, ironMine0);

        Worker miner0 = ironMine0.getWorker();

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), ironMine0);
        assertEquals(ironMine0.getWorker(), miner0);

        /* Verify that the worker goes to the storage off-road when the iron mine is torn down */
        headquarter0.blockDeliveryOfMaterial(MINER);

        ironMine0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(miner0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner0, ironMine0.getFlag().getPosition());

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
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place iron mine */
        IronMine ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Place road to connect the iron mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the iron mine to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(ironMine0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(ironMine0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter
        */
        headquarter0.blockDeliveryOfMaterial(MINER);

        Worker worker = ironMine0.getWorker();

        ironMine0.tearDown();

        assertEquals(worker.getPosition(), ironMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, ironMine0.getFlag().getPosition());

        assertEquals(worker.getPosition(), ironMine0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), ironMine0.getPosition());
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
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place iron mine */
        IronMine ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Place road to connect the iron mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the iron mine to get constructed */
        Utils.waitForBuildingToBeConstructed(ironMine0);

        /* Wait for a miner to start walking to the iron mine */
        Miner miner = Utils.waitForWorkerOutsideBuilding(Miner.class, player0);

        /* Wait for the miner to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the miner goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(miner.getTarget(), ironMine0.getPosition());

        headquarter0.blockDeliveryOfMaterial(MINER);

        ironMine0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, ironMine0.getFlag().getPosition());

        assertEquals(miner.getPosition(), ironMine0.getFlag().getPosition());
        assertNotEquals(miner.getTarget(), headquarter0.getPosition());
        assertFalse(miner.isInsideBuilding());
        assertNull(ironMine0.getWorker());
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
