/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.CoalMine;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Miner;
import org.appland.settlers.model.Mint;
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
public class TestCoalMine {

    @Test
    public void testCoalMineOnlyNeedsFourPlanksForConstruction() throws Exception {

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

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point22);

        /* Deliver four planks */
        Cargo plankCargo = new Cargo(PLANK, map);

        coalMine0.putCargo(plankCargo);
        coalMine0.putCargo(plankCargo);
        coalMine0.putCargo(plankCargo);
        coalMine0.putCargo(plankCargo);

        /* Assign builder */
        Utils.assignBuilder(coalMine0);

        /* Verify that this is enough to construct the coal mine */
        for (int i = 0; i < 100; i++) {
            assertTrue(coalMine0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(coalMine0.isReady());
    }

    @Test
    public void testCoalMineCannotBeConstructedWithTooFewPlanks() throws Exception {

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

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point22);

        /* Deliver two plank and three stone */
        Cargo plankCargo = new Cargo(PLANK, map);

        coalMine0.putCargo(plankCargo);
        coalMine0.putCargo(plankCargo);
        coalMine0.putCargo(plankCargo);

        /* Assign builder */
        Utils.assignBuilder(coalMine0);

        /* Verify that this is not enough to construct the coal mine */
        for (int i = 0; i < 500; i++) {
            assertTrue(coalMine0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(coalMine0.isReady());
    }

    @Test
    public void testConstructCoalMine() throws Exception {

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
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a coalmine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

        /* Connect the mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mine.getFlag(), headquarter.getFlag());

        /* Wait for the mine to get constructed */
        Utils.waitForBuildingToBeConstructed(mine);

        assertTrue(mine.isReady());
    }

    @Test
    public void testCoalmineIsNotMilitary() throws Exception {

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

        /* Place a coal mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

        /* Verify that the mine is not a military building */
        assertFalse(mine.isMilitaryBuilding());

        constructHouse(mine);

        assertFalse(mine.isMilitaryBuilding());
    }

    @Test
    public void testCoalmineUnderConstructionNotNeedsMiner() throws Exception {

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

        /* Place a gold mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

        /* Verify that the unfinished mine does not need a worker */
        assertFalse(mine.needsWorker());
    }

    @Test
    public void testFinishedCoalmineNeedsMiner() throws Exception {

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

        /* Place a gold mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

        constructHouse(mine);

        /* Verify that the finished mine needs a worker */
        assertTrue(mine.needsWorker());
    }

    @Test
    public void testMinerIsAssignedToFinishedCoalmine() throws Exception {

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

        /* Place a gold mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point1);

        /* Place a road between the headquarter and the goldmine */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Wait for the mine to get constructed */
        Utils.waitForBuildingToBeConstructed(mine);

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
    public void testMinerIsNotASoldier() throws Exception {

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

        /* Place a gold mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point1);

        /* Place a road between the headquarter and the goldmine */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the mine */
        constructHouse(mine);

        assertTrue(mine.isReady());

        /* Wait for a miner to walk out */
        Miner miner0 = Utils.waitForWorkerOutsideBuilding(Miner.class, player0);

        assertNotNull(miner0);
        assertFalse(miner0.isSoldier());
    }

    @Test
    public void testMinerIsCreatedFromPickAxe() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all miners from the headquarter and add one pick axe */
        Utils.adjustInventoryTo(headquarter, MINER, 0);
        Utils.adjustInventoryTo(headquarter, PICK_AXE, 1);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point1, map);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point1);

        /* Place a road between the headquarter and the goldmine */
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

        /* Place a gold mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point1);

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

        /* Place a gold mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

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
    public void testMinerMinesCoal() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putCoalAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

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
        int amountCoal = map.getAmountOfMineralAtPoint(COAL, point0);

        for (int i = 0; i < 50; i++) {
            assertTrue(miner.isMining());
            map.stepTime();
        }

        /* Verify that the miner finishes mining on time and has gold */
        assertFalse(miner.isMining());
        assertFalse(miner.isInsideBuilding());
        assertNotNull(miner.getCargo());
        assertEquals(miner.getCargo().getMaterial(), COAL);
        assertTrue(map.getAmountOfMineralAtPoint(COAL, point0) < amountCoal);
    }

    @Test
    public void testCoalmineGoesToFlagWithCargoAndBack() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putCoalAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

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
    public void testCoalCargoIsDeliveredToMintWhichIsCloserThanHeadquarters() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Remove all coal from the headquarters */
        Utils.adjustInventoryTo(headquarter, COAL, 0);

        /* Place mint */
        Point point4 = new Point(10, 4);
        Mint mint = map.placeBuilding(new Mint(player0), point4);

        /* Connect the mint to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, mint.getFlag(), headquarter.getFlag());

        /* Place mountain */
        Point point1 = new Point(14, 4);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place the coal mine */
        CoalMine coalMine = map.placeBuilding(new CoalMine(player0), point1);

        /* Connect the coal mine with the mint */
        Road road0 = map.placeAutoSelectedRoad(player0, coalMine.getFlag(), mint.getFlag());

        /* Wait for the coal mine and the mint to get constructed and occupied */
        Utils.waitForBuildingsToBeConstructed(coalMine, mint);

        Utils.waitForNonMilitaryBuildingsToGetPopulated(coalMine, mint);

        /* Wait for the courier on the road between the mint and the coal mine hut to have a coal cargo */
        Utils.waitForFlagToGetStackedCargo(map, coalMine.getFlag(), 1);

        assertEquals(coalMine.getFlag().getStackedCargo().get(0).getMaterial(), COAL);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the mint (and not the headquarters) */
        assertEquals(coalMine.getAmount(COAL), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), mint.getPosition());

        assertEquals(mint.getAmount(COAL), 1);
    }

    @Test
    public void testCoalIsNotDeliveredToStorehouseUnderConstruction() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory */
        Utils.clearInventory(headquarter, GOLD, STONE, MEAT, BREAD, FISH, PLANK, COAL);

        /* Place storehouse */
        Point point4 = new Point(10, 4);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Connect the storehouse to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Add stones to the storehouse doesn't need any more stones */
        Utils.deliverMaxCargos(storehouse, STONE);

        /* Place mountain */
        Point point1 = new Point(14, 4);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place the coal mine */
        CoalMine coalMine = map.placeBuilding(new CoalMine(player0), point1);

        /* Connect the coal mine with the storehouse */
        Road road0 = map.placeAutoSelectedRoad(player0, coalMine.getFlag(), storehouse.getFlag());

        /* Deliver the needed material to construct the coal mine */
        Utils.deliverCargos(coalMine, PLANK, 4);

        /* Wait for the coal mine to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(coalMine);

        Utils.waitForNonMilitaryBuildingToGetPopulated(coalMine);

        /* Wait for the courier on the road between the storehouse and the coal mine to have a coal cargo */
        Utils.deliverCargo(coalMine, BREAD);

        Utils.waitForFlagToGetStackedCargo(map, coalMine.getFlag(), 1);

        assertEquals(coalMine.getFlag().getStackedCargo().get(0).getMaterial(), COAL);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the storehouse's flag so that it can continue to the headquarters */
        assertEquals(headquarter.getAmount(COAL), 0);
        assertEquals(coalMine.getAmount(COAL), 0);
        assertFalse(storehouse.needsMaterial(COAL));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().get(0).getMaterial().equals(COAL));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testGoldIsNotDeliveredTwiceToBuildingThatOnlyNeedsOne() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory */
        Utils.clearInventory(headquarter, BREAD, FISH, MEAT, GOLD, COAL, GOLD);

        /* Place mint */
        Point point4 = new Point(10, 4);
        Mint mint = map.placeBuilding(new Mint(player0), point4);

        /* Connect the mint to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, mint.getFlag(), headquarter.getFlag());

        /* Wait for the mint to get constructed */
        Utils.waitForBuildingToBeConstructed(mint);

        /* Ensure that the mint only needs one more coal */
        assertEquals(mint.getCanHoldAmount(COAL) - mint.getAmount(COAL), 1);

        /* Place mountain */
        Point point1 = new Point(14, 4);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place the coal mine */
        CoalMine coalMine = map.placeBuilding(new CoalMine(player0), point1);

        /* Connect the coal mine with the mint */
        Road road0 = map.placeAutoSelectedRoad(player0, coalMine.getFlag(), mint.getFlag());

        /* Deliver the needed material to construct the coal mine */
        Utils.deliverCargos(coalMine, PLANK, 4);

        /* Wait for the coal mine to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(coalMine);

        Utils.waitForNonMilitaryBuildingToGetPopulated(coalMine);

        /* Wait for the flag on the road between the mint and the coal mine to have a coal cargo */
        Utils.deliverCargo(coalMine, BREAD);

        Utils.waitForFlagToGetStackedCargo(map, coalMine.getFlag(), 1);

        assertEquals(coalMine.getFlag().getStackedCargo().get(0).getMaterial(), COAL);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that no stone is delivered from the headquarters */
        Utils.adjustInventoryTo(headquarter, COAL, 1);

        assertEquals(mint.getCanHoldAmount(COAL) - mint.getAmount(COAL), 1);
        assertFalse(mint.needsMaterial(COAL));

        for (int i = 0; i < 200; i++) {
            assertNull(headquarter.getWorker().getCargo());

            map.stepTime();
        }

        assertEquals(headquarter.getAmount(COAL), 1);
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

        /* Verify that it's not possible to place a mine on the grass */
        Point point1 = new Point(2, 2);
        try {
            map.placeBuilding(new CoalMine(player0), point1);
            fail();
        } catch (Exception e) {}

        assertEquals(map.getBuildings().size(), 1);
    }

    @Test
    public void testCoalmineRunsOutOfCoal() throws Exception {

        /* Create game map with one player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putCoalAtSurroundingTiles(point0, SMALL, map);

        /* Remove all coal but one */
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountOfMineralAtPoint(COAL, point0) > 1) {
                map.mineMineralAtPoint(COAL, point0);
            }
        }

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a coal mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the coal mine */
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

        /* Wait for the miner to mine coal */
        Utils.fastForward(50, map);

        assertFalse(mine.isOutOfNaturalResources());

        /* Wait for the miner to leave the coal at the flag */
        assertEquals(miner.getTarget(), mine.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getFlag().getPosition());

        assertNull(miner.getCargo());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getPosition());

        assertTrue(miner.isInsideBuilding());

        /* Verify that the coal is gone and that the miner gets no coal */
        assertEquals(map.getAmountOfMineralAtPoint(COAL, point0), 0);

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());

            map.stepTime();
        }

        /* Verify that the mine is out of resources */
        assertTrue(mine.isOutOfNaturalResources());
    }

    @Test
    public void testCoalmineWithoutCoalProducesNothing() throws Exception {

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

        /* Place a coal mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the coal mine */
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

        /* Verify that there is no coal and that the miner gets no coal */
        assertEquals(map.getAmountOfMineralAtPoint(COAL, point0), 0);

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testCoalmineWithoutFoodProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putCoalAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a coal mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

        /* Construct the coal mine */
        constructHouse(mine);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine);

        assertTrue(miner.isInsideBuilding());

        /* Wait for the miner to rest */
        Utils.fastForward(100, map);

        /* Verify that the miner gets no coal */

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
        Utils.putCoalAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a coal mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

        /* Construct the coal mine */
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

        /* Verify that the miner mines for coal */
        assertEquals(mine.getAmount(BREAD), 1);

        Utils.fastForward(50, map);

        /* Verify that the miner consumed the bread */
        assertEquals(mine.getAmount(BREAD), 0);
    }

    @Test
    public void testCoalmineCanConsumeAllTypesOfFood() throws Exception {

        /* Start new game with one player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putCoalAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a coal mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

        /* Construct the coal mine */
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
            Utils.fastForwardUntilWorkerCarriesCargo(map, miner, COAL);

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
    public void testCoalMineWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putCoalAtSurroundingTiles(point0, LARGE, map);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point0);

        /* Finish construction of the coal mine */
        constructHouse(coalMine0);

        /* Occupy the coal mine */
        Utils.occupyBuilding(new Miner(player0, map), coalMine0);

        /* Deliver material to the coal mine */
        Cargo fishCargo = new Cargo(FISH, map);

        coalMine0.putCargo(fishCargo);
        coalMine0.putCargo(fishCargo);

        /* Let the miner rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce a new coal cargo */
        Utils.fastForward(50, map);

        Worker miner = coalMine0.getWorker();

        assertNotNull(miner.getCargo());

        /* Verify that the miner puts the coal cargo at the flag */
        assertEquals(miner.getTarget(), coalMine0.getFlag().getPosition());
        assertTrue(coalMine0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, coalMine0.getFlag().getPosition());

        assertNull(miner.getCargo());
        assertFalse(coalMine0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the coal mine */
        assertEquals(miner.getTarget(), coalMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, coalMine0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(miner.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(miner.getTarget(), coalMine0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, coalMine0.getFlag().getPosition());

        assertNull(miner.getCargo());
        assertEquals(coalMine0.getFlag().getStackedCargo().size(), 2);
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
        Utils.putCoalAtSurroundingTiles(point0, LARGE, map);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point0);

        /* Finish construction of the coal mine */
        constructHouse(coalMine0);

        /* Deliver material to the coal mine */
        Cargo fishCargo = new Cargo(FISH, map);

        coalMine0.putCargo(fishCargo);
        coalMine0.putCargo(fishCargo);

        /* Occupy the coal mine */
        Utils.occupyBuilding(new Miner(player0, map), coalMine0);

        /* Let the miner rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce a new coal cargo */
        Utils.fastForward(50, map);

        Worker miner = coalMine0.getWorker();

        assertNotNull(miner.getCargo());

        /* Verify that the miner puts the coal cargo at the flag */
        assertEquals(miner.getTarget(), coalMine0.getFlag().getPosition());
        assertTrue(coalMine0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, coalMine0.getFlag().getPosition());

        assertNull(miner.getCargo());
        assertFalse(coalMine0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = coalMine0.getFlag().getStackedCargo().get(0);

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), coalMine0.getFlag().getPosition());

        /* Remove the resources the iron mine needs from the headquarter */
        Utils.adjustInventoryTo(headquarter0, MEAT, 0);
        Utils.adjustInventoryTo(headquarter0, BREAD, 0);
        Utils.adjustInventoryTo(headquarter0, FISH, 0);

        /* Connect the coal mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), coalMine0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), coalMine0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), coalMine0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(COAL);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(COAL), amount + 1);
    }

    @Test
    public void testMinerGoesBackToStorageWhenCoalMineIsDestroyed() throws Exception {

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
        Utils.putCoalAtSurroundingTiles(point0, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point0);

        /* Finish construction of the coal mine */
        constructHouse(coalMine0);

        /* Occupy the coal mine */
        Utils.occupyBuilding(new Miner(player0, map), coalMine0);

        /* Destroy the coal mine */
        Worker miner = coalMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), coalMine0.getPosition());

        coalMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MINER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerGoesBackOnToStorageOnRoadsIfPossibleWhenCoalMineIsDestroyed() throws Exception {

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
        Utils.putCoalAtSurroundingTiles(point0, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point0);

        /* Connect the coal mine with the headquarter */
        map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the coal mine */
        constructHouse(coalMine0);

        /* Occupy the coal mine */
        Utils.occupyBuilding(new Miner(player0, map), coalMine0);

        /* Destroy the coal mine */
        Worker miner = coalMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), coalMine0.getPosition());

        coalMine0.tearDown();

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
    public void testProductionInCoalMineCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 6);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point1);

        /* Connect the coal mine and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter.getFlag());

        /* Finish the coal mine */
        constructHouse(coalMine0);

        /* Assign a worker to the coal mine */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, coalMine0);

        assertTrue(miner.isInsideBuilding());

        /* Deliver material to the coal mine */
        Cargo fishCargo = new Cargo(FISH, map);

        coalMine0.putCargo(fishCargo);
        coalMine0.putCargo(fishCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, miner);

        assertEquals(miner.getCargo().getMaterial(), COAL);

        /* Wait for the worker to deliver the cargo */
        assertEquals(miner.getTarget(), coalMine0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, coalMine0.getFlag().getPosition());

        /* Stop production and verify that no coal is produced */
        coalMine0.stopProduction();

        assertFalse(coalMine0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(miner.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInCoalMineCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 6);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point1);

        /* Connect the coal mine and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter.getFlag());

        /* Finish the coal mine */
        constructHouse(coalMine0);

        /* Deliver material to the coal mine */
        Cargo fishCargo = new Cargo(FISH, map);

        coalMine0.putCargo(fishCargo);
        coalMine0.putCargo(fishCargo);

        /* Assign a worker to the coal mine */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, coalMine0);

        assertTrue(miner.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce coal */
        Utils.fastForwardUntilWorkerProducesCargo(map, miner);

        assertEquals(miner.getCargo().getMaterial(), COAL);

        /* Wait for the worker to deliver the cargo */
        assertEquals(miner.getTarget(), coalMine0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, coalMine0.getFlag().getPosition());

        /* Stop production */
        coalMine0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(miner.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the coal mine produces coal again */
        coalMine0.resumeProduction();

        assertTrue(coalMine0.isProductionEnabled());

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
        Point point0 = new Point(8, 10);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putCoalAtSurroundingTiles(point0, LARGE, map);

        /* Place headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point0);

        /* Finish construction of the coal mine */
        constructHouse(coalMine0);

        /* Connect the coal mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), coalMine0.getFlag());

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
        Point point4 = new Point(28, 18);
        Utils.surroundPointWithMinableMountain(point4, map);
        Utils.putCoalAtSurroundingTiles(point4, LARGE, map);

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
        Point point2 = new Point(21, 9);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place coal mine close to the new border */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point4);

        /* Finish construction of the coal mine */
        constructHouse(coalMine0);

        /* Occupy the coal mine */
        Miner worker = Utils.occupyBuilding(new Miner(player0, map), coalMine0);

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
        Point point2 = new Point(14, 4);
        Utils.surroundPointWithMinableMountain(point2, map);
        Utils.putCoalAtSurroundingTiles(point2, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, coalMine0.getFlag());

        /* Wait for the miner to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0);

        Miner miner = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Miner) {
                miner = (Miner) worker;
            }
        }

        assertNotNull(miner);
        assertEquals(miner.getTarget(), coalMine0.getPosition());

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
        Point point2 = new Point(14, 4);
        Utils.surroundPointWithMinableMountain(point2, map);
        Utils.putCoalAtSurroundingTiles(point2, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, coalMine0.getFlag());

        /* Wait for the miner to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0);

        Miner miner = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Miner) {
                miner = (Miner) worker;
            }
        }

        assertNotNull(miner);
        assertEquals(miner.getTarget(), coalMine0.getPosition());

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
        assertEquals(miner.getTarget(), coalMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, coalMine0.getFlag().getPosition());

        /* Verify that the miner goes out to miner instead of going directly back */
        assertNotEquals(miner.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMinerReturnsToStorageIfCoalMineIsDestroyed() throws Exception {

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
        Point point2 = new Point(14, 4);
        Utils.surroundPointWithMinableMountain(point2, map);
        Utils.putCoalAtSurroundingTiles(point2, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, coalMine0.getFlag());

        /* Wait for the miner to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0);

        Miner miner = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Miner) {
                miner = (Miner) worker;
            }
        }

        assertNotNull(miner);
        assertEquals(miner.getTarget(), coalMine0.getPosition());

        /* Wait for the miner to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, flag0.getPosition());

        map.stepTime();

        /* See that the miner has started walking */
        assertFalse(miner.isExactlyAtPoint());

        /* Tear down the coal mine */
        coalMine0.tearDown();

        /* Verify that the miner continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, coalMine0.getFlag().getPosition());

        assertEquals(miner.getPosition(), coalMine0.getFlag().getPosition());

        /* Verify that the miner goes back to storage */
        assertEquals(miner.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMinerGoesOffroadBackToClosestStorageWhenCoalMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point3 = new Point(17, 17);
        Utils.surroundPointWithMinableMountain(point3, map);
        Utils.putCoalAtSurroundingTiles(point3, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point3);

        /* Finish construction of the coal mine */
        constructHouse(coalMine0);

        /* Occupy the coal mine */
        Utils.occupyBuilding(new Miner(player0, map), coalMine0);

        /* Place a second storage closer to the coal mine */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the coal mine */
        Worker miner = coalMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), coalMine0.getPosition());

        coalMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(MINER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, storehouse0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerReturnsOffroadAndAvoidsBurningStorageWhenCoalMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Put a small mountain on the map */
        Point point3 = new Point(17, 17);
        Utils.surroundPointWithMinableMountain(point3, map);
        Utils.putCoalAtSurroundingTiles(point3, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point3);

        /* Finish construction of the coal mine */
        constructHouse(coalMine0);

        /* Occupy the coal mine */
        Utils.occupyBuilding(new Miner(player0, map), coalMine0);

        /* Place a second storage closer to the coal mine */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Destroy the coal mine */
        Worker miner = coalMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), coalMine0.getPosition());

        coalMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MINER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerReturnsOffroadAndAvoidsDestroyedStorageWhenCoalMineIsDestroyed() throws Exception {

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
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point1);

        /* Finish construction of the coal mine */
        constructHouse(coalMine0);

        /* Occupy the coal mine */
        Utils.occupyBuilding(new Miner(player0, map), coalMine0);

        /* Place a second storage closer to the coal mine */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

        /* Destroy the coal mine */
        Worker miner = coalMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), coalMine0.getPosition());

        coalMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MINER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerReturnsOffroadAndAvoidsUnfinishedStorageWhenCoalMineIsDestroyed() throws Exception {

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
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point1);

        /* Finish construction of the coal mine */
        constructHouse(coalMine0);

        /* Occupy the coal mine */
        Utils.occupyBuilding(new Miner(player0, map), coalMine0);

        /* Place a second storage closer to the coal mine */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the coal mine */
        Worker miner = coalMine0.getWorker();

        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), coalMine0.getPosition());

        coalMine0.tearDown();

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
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point1);

        /* Place road to connect the headquarter and the coal mine */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), coalMine0.getFlag());

        /* Finish construction of the coal mine */
        constructHouse(coalMine0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Miner.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, coalMine0.getFlag().getPosition());

        /* Tear down the building */
        coalMine0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(worker.getTarget(), coalMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, coalMine0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testCoalMineWithoutResourcesHasZeroProductivity() throws Exception {

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
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine = map.placeBuilding(new CoalMine(player0), point1);

        /* Finish construction of the coal mine */
        constructHouse(coalMine);

        /* Populate the coal mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), coalMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), coalMine);
        assertEquals(coalMine.getWorker(), miner0);

        /* Verify that the productivity is 0% when the coal mine doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(coalMine.getFlag().getStackedCargo().isEmpty());
            assertNull(miner0.getCargo());
            assertEquals(coalMine.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testCoalMineWithAbundantResourcesHasFullProductivity() throws Exception {

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
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine = map.placeBuilding(new CoalMine(player0), point1);

        /* Finish construction of the coal mine */
        constructHouse(coalMine);

        /* Populate the coal mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), coalMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), coalMine);
        assertEquals(coalMine.getWorker(), miner0);

        /* Connect the coal mine with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), coalMine.getFlag());

        /* Make the coal mine create some coal with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (coalMine.needsMaterial(FISH)) {
                coalMine.putCargo(new Cargo(FISH, map));
            }
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(coalMine.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (coalMine.needsMaterial(FISH)) {
                coalMine.putCargo(new Cargo(FISH, map));
            }

            assertEquals(coalMine.getProductivity(), 100);
        }
    }

    @Test
    public void testCoalMineLosesProductivityWhenResourcesRunOut() throws Exception {

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
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine = map.placeBuilding(new CoalMine(player0), point1);

        /* Finish construction of the coal mine */
        constructHouse(coalMine);

        /* Populate the coal mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), coalMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), coalMine);
        assertEquals(coalMine.getWorker(), miner0);

        /* Connect the coal mine with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), coalMine.getFlag());

        /* Make the coal mine create some coal with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (coalMine.needsMaterial(FISH) && coalMine.getAmount(FISH) < 2) {
                coalMine.putCargo(new Cargo(FISH, map));
            }
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(coalMine.getProductivity(), 100);

        for (int i = 0; i < 5000; i++) {
            map.stepTime();

            if (coalMine.getProductivity() == 0) {
                break;
            }
        }

        assertEquals(coalMine.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedCoalMineHasNoProductivity() throws Exception {

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
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine = map.placeBuilding(new CoalMine(player0), point1);

        /* Finish construction of the coal mine */
        constructHouse(coalMine);

        /* Verify that the unoccupied coal mine is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(coalMine.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testCoalMineCanProduce() throws Exception {

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

        /* Place coal mine */
        CoalMine coalMine = map.placeBuilding(new CoalMine(player0), point1);

        /* Finish construction of the coal mine */
        constructHouse(coalMine);

        /* Populate the coal mine */
        Worker miner = Utils.occupyBuilding(new Miner(player0, map), coalMine);

        /* Verify that the coal mine can produce */
        assertTrue(coalMine.canProduce());
    }

    @Test
    public void testCoalMineReportsCorrectOutput() throws Exception {

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

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point1);

        /* Construct the coal mine */
        constructHouse(coalMine0);

        /* Verify that the reported output is correct */
        assertEquals(coalMine0.getProducedMaterial().length, 1);
        assertEquals(coalMine0.getProducedMaterial()[0], COAL);
    }

    @Test
    public void testCoalMineReportsCorrectMaterialsNeededForConstruction() throws Exception {

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

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(coalMine0.getTypesOfMaterialNeeded().size(), 1);
        assertTrue(coalMine0.getTypesOfMaterialNeeded().contains(PLANK));
        assertEquals(coalMine0.getCanHoldAmount(PLANK), 4);

        for (Material material : Material.values()) {
            if (material == PLANK) {
                continue;
            }

            assertEquals(coalMine0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testCoalMineReportsCorrectMaterialsNeededForProduction() throws Exception {

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

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point1);

        /* Construct the coal mine */
        constructHouse(coalMine0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(coalMine0.getTypesOfMaterialNeeded().size(), 3);
        assertTrue(coalMine0.getTypesOfMaterialNeeded().contains(BREAD));
        assertTrue(coalMine0.getTypesOfMaterialNeeded().contains(MEAT));
        assertTrue(coalMine0.getTypesOfMaterialNeeded().contains(FISH));
        assertEquals(coalMine0.getCanHoldAmount(BREAD), 1);
        assertEquals(coalMine0.getCanHoldAmount(MEAT), 1);
        assertEquals(coalMine0.getCanHoldAmount(FISH), 1);

        for (Material material : Material.values()) {
            if (material == BREAD || material == MEAT || material == FISH) {
                continue;
            }

            assertEquals(coalMine0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testCoalMineWaitsWhenFlagIsFull() throws Exception {

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
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine = map.placeBuilding(new CoalMine(player0), point1);

        /* Connect the coal mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, coalMine.getFlag(), headquarter.getFlag());

        /* Wait for the coal mine to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(coalMine);
        Utils.waitForNonMilitaryBuildingToGetPopulated(coalMine);

        /* Give material to the coal mine */
        Utils.putCargoToBuilding(coalMine, BREAD);
        Utils.putCargoToBuilding(coalMine, BREAD);
        Utils.putCargoToBuilding(coalMine, FISH);
        Utils.putCargoToBuilding(coalMine, FISH);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, coalMine.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the coal mine waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(coalMine.getFlag().getStackedCargo().size(), 8);
            assertNull(coalMine.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the coal mine with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, coalMine.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(coalMine.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(coalMine.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(coalMine.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, coalMine.getWorker(), COAL);
    }

    @Test
    public void testCoalMineDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

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
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine = map.placeBuilding(new CoalMine(player0), point1);

        /* Connect the coal mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, coalMine.getFlag(), headquarter.getFlag());

        /* Wait for the coal mine to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(coalMine);
        Utils.waitForNonMilitaryBuildingToGetPopulated(coalMine);

        /* Give material to the coal mine */
        Utils.putCargoToBuilding(coalMine, BREAD);
        Utils.putCargoToBuilding(coalMine, BREAD);
        Utils.putCargoToBuilding(coalMine, FISH);
        Utils.putCargoToBuilding(coalMine, FISH);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, coalMine.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The coal mine waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(coalMine.getFlag().getStackedCargo().size(), 8);
            assertNull(coalMine.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the coal mine with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, coalMine.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(coalMine.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(coalMine.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(coalMine.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, coalMine.getWorker(), COAL);

        /* Wait for the worker to put the cargo on the flag */
        assertEquals(coalMine.getWorker().getTarget(), coalMine.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, coalMine.getWorker(), coalMine.getFlag().getPosition());

        assertEquals(coalMine.getFlag().getStackedCargo().size(), 8);

        /* Verify that the coal mine doesn't produce anything because the flag is full */
        for (int i = 0; i < 400; i++) {
            assertEquals(coalMine.getFlag().getStackedCargo().size(), 8);
            assertNull(coalMine.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testWhenCoalDeliveryAreBlockedCoalMineFillsUpFlagAndThenStops() throws Exception {

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
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place Coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point1);

        /* Place road to connect the coal mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());

        /* Wait for the coal mine to get constructed and occupied */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(coalMine0);

        Worker miner0 = Utils.waitForNonMilitaryBuildingToGetPopulated(coalMine0);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), coalMine0);
        assertEquals(coalMine0.getWorker(), miner0);

        /* Add a lot of material to the headquarter for the coal mine to consume */
        Utils.adjustInventoryTo(headquarter0, MEAT, 40);
        Utils.adjustInventoryTo(headquarter0, BREAD, 40);

        /* Block storage of coal */
        headquarter0.blockDeliveryOfMaterial(COAL);

        /* Verify that the coal mine puts eight coals on the flag and then stops */
        Utils.waitForFlagToGetStackedCargo(map, coalMine0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, miner0, coalMine0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(coalMine0.getFlag().getStackedCargo().size(), 8);
            assertTrue(miner0.isInsideBuilding());

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), COAL);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndCoalMineIsTornDown() throws Exception {

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
        Utils.putCoalAtSurroundingTiles(point2, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the coal mine */
        Road road1 = map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the coal mine and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, coalMine0);

        /* Add a lot of material to the headquarter for the coal mine to consume */
        Utils.adjustInventoryTo(headquarter0, MEAT, 40);
        Utils.adjustInventoryTo(headquarter0, BREAD, 40);

        /* Wait for the coal mine and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, coalMine0);

        Worker miner0 = coalMine0.getWorker();

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), coalMine0);
        assertEquals(coalMine0.getWorker(), miner0);

        /* Verify that the worker goes to the storage when the coal mine is torn down */
        headquarter0.blockDeliveryOfMaterial(MINER);

        coalMine0.tearDown();

        map.stepTime();

        assertFalse(miner0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner0, coalMine0.getFlag().getPosition());

        assertEquals(miner0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, miner0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(miner0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndCoalMineIsTornDown() throws Exception {

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
        Utils.putCoalAtSurroundingTiles(point2, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the coal mine */
        Road road1 = map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the coal mine and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, coalMine0);

        /* Add a lot of material to the headquarter for the coal mine to consume */
        Utils.adjustInventoryTo(headquarter0, MEAT, 40);
        Utils.adjustInventoryTo(headquarter0, BREAD, 40);

        /* Wait for the coal mine and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, coalMine0);

        Worker miner0 = coalMine0.getWorker();

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), coalMine0);
        assertEquals(coalMine0.getWorker(), miner0);

        /* Verify that the worker goes to the storage off-road when the coal mine is torn down */
        headquarter0.blockDeliveryOfMaterial(MINER);

        coalMine0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(miner0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner0, coalMine0.getFlag().getPosition());

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
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point1);

        /* Place road to connect the coal mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the coal mine to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(coalMine0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(coalMine0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter
        */
        headquarter0.blockDeliveryOfMaterial(MINER);

        Worker worker = coalMine0.getWorker();

        coalMine0.tearDown();

        assertEquals(worker.getPosition(), coalMine0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, coalMine0.getFlag().getPosition());

        assertEquals(worker.getPosition(), coalMine0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), coalMine0.getPosition());
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
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place coal mine */
        CoalMine coalMine0 = map.placeBuilding(new CoalMine(player0), point1);

        /* Place road to connect the coal mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the coal mine to get constructed */
        Utils.waitForBuildingToBeConstructed(coalMine0);

        /* Wait for a miner to start walking to the coal mine */
        Miner miner = Utils.waitForWorkerOutsideBuilding(Miner.class, player0);

        /* Wait for the miner to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the miner goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(miner.getTarget(), coalMine0.getPosition());

        headquarter0.blockDeliveryOfMaterial(MINER);

        coalMine0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, coalMine0.getFlag().getPosition());

        assertEquals(miner.getPosition(), coalMine0.getFlag().getPosition());
        assertNotEquals(miner.getTarget(), headquarter0.getPosition());
        assertFalse(miner.isInsideBuilding());
        assertNull(coalMine0.getWorker());
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
