/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Minter;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.buildings.Storehouse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestMint {

    @Test
    public void testMintCanHoldSixCoalBarsAndSixGold() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(6, 12);
        var brewery0 = map.placeBuilding(new Mint(player0), point1);

        /* Connect the brewery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, brewery0.getFlag(), headquarter0.getFlag());

        /* Make sure the headquarters has enough resources */
        Utils.adjustInventoryTo(headquarter0, PLANK, 20);
        Utils.adjustInventoryTo(headquarter0, STONE, 20);
        Utils.adjustInventoryTo(headquarter0, COAL, 20);
        Utils.adjustInventoryTo(headquarter0, GOLD, 20);
        Utils.adjustInventoryTo(headquarter0, BREWER, 20);

        /* Wait for the brewery to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(brewery0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(brewery0);

        /* Stop production */
        brewery0.stopProduction();

        /* Wait for the brewery to get six iron bars and six planks */
        Utils.waitForBuildingToGetAmountOfMaterial(brewery0, COAL, 6);
        Utils.waitForBuildingToGetAmountOfMaterial(brewery0, GOLD, 6);

        /* Verify that the brewery doesn't need any more resources and doesn't get any more deliveries */
        assertFalse(brewery0.needsMaterial(COAL));
        assertFalse(brewery0.needsMaterial(GOLD));

        for (int i = 0; i < 2000; i++) {
            assertFalse(brewery0.needsMaterial(COAL));
            assertFalse(brewery0.needsMaterial(GOLD));
            assertEquals(brewery0.getAmount(COAL), 6);
            assertEquals(brewery0.getAmount(GOLD), 6);

            map.stepTime();
        }
    }

    @Test
    public void testMintOnlyNeedsTwoPlanksAndTwoStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place mint */
        Point point22 = new Point(6, 12);
        Mint mint0 = map.placeBuilding(new Mint(player0), point22);

        /* Deliver two plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        mint0.putCargo(plankCargo);
        mint0.putCargo(plankCargo);
        mint0.putCargo(stoneCargo);
        mint0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(mint0);

        /* Verify that this is enough to construct the mint */
        for (int i = 0; i < 150; i++) {
            assertTrue(mint0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(mint0.isReady());
    }

    @Test
    public void testMintCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place mint */
        Point point22 = new Point(6, 12);
        Mint mint0 = map.placeBuilding(new Mint(player0), point22);

        /* Deliver one plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        mint0.putCargo(plankCargo);
        mint0.putCargo(stoneCargo);
        mint0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(mint0);

        /* Verify that this is not enough to construct the mint */
        for (int i = 0; i < 500; i++) {
            assertTrue(mint0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(mint0.isReady());
    }

    @Test
    public void testMintCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place mint */
        Point point22 = new Point(6, 12);
        Mint mint0 = map.placeBuilding(new Mint(player0), point22);

        /* Deliver two planks and one stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        mint0.putCargo(plankCargo);
        mint0.putCargo(plankCargo);
        mint0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(mint0);

        /* Verify that this is not enough to construct the mint */
        for (int i = 0; i < 500; i++) {
            assertTrue(mint0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(mint0.isReady());
    }

    @Test
    public void testMintNeedsWorker() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point3 = new Point(7, 9);
        Mint mint = map.placeBuilding(new Mint(player0), point3);

        /* Connect the mint with the headquarter */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Unfinished mint doesn't need minter */
        assertFalse(mint.needsWorker());

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        assertTrue(mint.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneMinterAtStart() {
        Headquarter headquarter = new Headquarter(null);

        assertEquals(headquarter.getAmount(MINTER), 1);
    }

    @Test
    public void testMintGetsAssignedWorker() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point3 = new Point(7, 9);
        Mint mint = map.placeBuilding(new Mint(player0), point3);

        /* Connect the mint with the headquarter */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        assertTrue(mint.needsWorker());

        /* Verify that a minter leaves the headquarter */
        Utils.fastForward(3, map);

        assertTrue(map.getWorkers().size() >= 3);

        /* Let the mint worker reach the mint */
        Minter minter = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Minter) {
                minter = (Minter)worker;
            }
        }

        assertNotNull(minter);
        assertEquals(minter.getTarget(), mint.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, minter);

        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getHome(), mint);
        assertEquals(mint.getWorker(), minter);
    }

    @Test
    public void testMinterIsNotASoldier() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point3 = new Point(7, 9);
        Mint mint = map.placeBuilding(new Mint(player0), point3);

        /* Connect the mint with the headquarter */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        assertTrue(mint.needsWorker());

        /* Verify that a minter leaves the headquarter */
        Minter minter0 = Utils.waitForWorkerOutsideBuilding(Minter.class, player0);

        assertNotNull(minter0);

        /* Verify that the minter is not a soldier */
        assertFalse(minter0.isSoldier());
    }

    @Test
    public void testMinterGetsCreatedFromCrucible() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all minters from the headquarter and add one crucible */
        Utils.adjustInventoryTo(headquarter, MINTER, 0);
        Utils.adjustInventoryTo(headquarter, CRUCIBLE, 1);

        /* Place mint */
        Point point3 = new Point(7, 9);
        Mint mint = map.placeBuilding(new Mint(player0), point3);

        /* Connect the mint with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mint.getFlag(), headquarter.getFlag());

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        assertTrue(mint.needsWorker());

        /* Verify that a minter leaves the headquarter */
        Utils.fastForward(3, map);

        assertTrue(map.getWorkers().size() >= 3);

        /* Let the mint worker reach the mint */
        Minter minter = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Minter) {
                minter = (Minter)worker;
            }
        }

        assertNotNull(minter);
        assertEquals(minter.getTarget(), mint.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, minter);

        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getHome(), mint);
        assertEquals(mint.getWorker(), minter);
    }

    @Test
    public void testOccupiedMintWithoutIngredientsProducesNothing() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point3 = new Point(7, 9);
        Mint mint = map.placeBuilding(new Mint(player0), point3);

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        /* Populate the mint */
        Worker minter = Utils.occupyBuilding(new Minter(player0, map), mint);

        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getHome(), mint);
        assertEquals(mint.getWorker(), minter);

        /* Verify that the mint doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(mint.getFlag().getStackedCargo().isEmpty());
            assertNull(minter.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedMintProducesNothing() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point3 = new Point(7, 9);
        Mint mint = map.placeBuilding(new Mint(player0), point3);

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        /* Verify that the mint doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(mint.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedMintWithGoldAndCoalProducesCoins() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point3 = new Point(7, 9);
        Mint mint = map.placeBuilding(new Mint(player0), point3);

        /* Connect the mint with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mint.getFlag(), headquarter.getFlag());

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        /* Populate the mint */
        Worker minter = Utils.occupyBuilding(new Minter(player0, map), mint);

        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getHome(), mint);
        assertEquals(mint.getWorker(), minter);

        /* Deliver wood to the mint */
        mint.putCargo(new Cargo(GOLD, map));
        mint.putCargo(new Cargo(COAL, map));

        /* Verify that the mint produces coin */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(mint.getFlag().getStackedCargo().isEmpty());
            assertNull(minter.getCargo());
        }

        map.stepTime();

        assertNotNull(minter.getCargo());
        assertEquals(minter.getCargo().getMaterial(), COIN);
        assertTrue(mint.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testMinterLeavesBreadAtTheFlag() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point3 = new Point(7, 9);
        Mint mint = map.placeBuilding(new Mint(player0), point3);

        /* Connect the mint with the headquarter */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        /* Populate the mint */
        Worker minter = Utils.occupyBuilding(new Minter(player0, map), mint);

        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getHome(), mint);
        assertEquals(mint.getWorker(), minter);

        /* Deliver ingredients to the mint */
        mint.putCargo(new Cargo(GOLD, map));
        mint.putCargo(new Cargo(COAL, map));

        /* Verify that the mint produces bread */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(mint.getFlag().getStackedCargo().isEmpty());
            assertNull(minter.getCargo());
        }

        map.stepTime();

        assertNotNull(minter.getCargo());
        assertEquals(minter.getCargo().getMaterial(), COIN);
        assertTrue(mint.getFlag().getStackedCargo().isEmpty());

        /* Verify that the mint worker leaves the cargo at the flag */
        assertEquals(minter.getTarget(), mint.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, minter, mint.getFlag().getPosition());

        assertFalse(mint.getFlag().getStackedCargo().isEmpty());
        assertNull(minter.getCargo());
        assertEquals(minter.getTarget(), mint.getPosition());

        /* Verify that the minter goes back to the mint */
        Utils.fastForwardUntilWorkersReachTarget(map, minter);

        assertTrue(minter.isInsideBuilding());
    }

    @Test
    public void testCoinCargoIsDeliveredToBarracksWhichIsCloserThanHeadquarters() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Remove all coins from the headquarters */
        Utils.adjustInventoryTo(headquarter, COIN, 0);

        /* Place barracks */
        Point point4 = new Point(10, 4);
        Barracks barracks = map.placeBuilding(new Barracks(player0), point4);

        /* Connect the barracks to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, barracks.getFlag(), headquarter.getFlag());

        /* Wait for the barracks to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(barracks);

        Utils.waitForMilitaryBuildingToGetPopulated(barracks);

        /* Place the mint */
        Point point1 = new Point(14, 4);
        Mint mint = map.placeBuilding(new Mint(player0), point1);

        /* Connect the mint with the barracks */
        Road road0 = map.placeAutoSelectedRoad(player0, mint.getFlag(), barracks.getFlag());

        /* Wait for the mint to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(mint);

        Utils.waitForNonMilitaryBuildingToGetPopulated(mint);

        /* Wait for the courier on the road between the coal mine and the mint hut to have a bread cargo */
        Utils.adjustInventoryTo(headquarter, COAL, 1);
        Utils.adjustInventoryTo(headquarter, GOLD, 1);

        Utils.waitForFlagToGetStackedCargo(map, mint.getFlag(), 1);

        assertEquals(mint.getFlag().getStackedCargo().getFirst().getMaterial(), COIN);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the coal mine (and not the headquarters) */
        assertEquals(mint.getAmount(COIN), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), barracks.getPosition());

        assertEquals(barracks.getAmount(COIN), 1);
    }

    @Test
    public void testCoinIsNotDeliveredToStorehouseUnderConstruction() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory so that there are no stones, planks, or coins */
        Utils.clearInventory(headquarter, PLANK, STONE, COIN, COAL, GOLD);

        /* Place storehouse */
        Point point4 = new Point(10, 4);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Connect the storehouse to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Place the mint */
        Point point1 = new Point(14, 4);
        Mint mint = map.placeBuilding(new Mint(player0), point1);

        /* Connect the mint with the storehouse */
        Road road0 = map.placeAutoSelectedRoad(player0, mint.getFlag(), storehouse.getFlag());

        /* Deliver the needed material to construct the mint */
        Utils.deliverCargos(mint, PLANK, 2);
        Utils.deliverCargos(mint, STONE, 2);

        /* Wait for the mint to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(mint);

        Utils.waitForNonMilitaryBuildingToGetPopulated(mint);

        /* Wait for the courier on the road between the storehouse and the mint to have a plank cargo */
        Utils.deliverCargo(mint, COAL);
        Utils.deliverCargo(mint, GOLD);

        Utils.waitForFlagToGetStackedCargo(map, mint.getFlag(), 1);

        assertEquals(mint.getFlag().getStackedCargo().getFirst().getMaterial(), COIN);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the storehouse's flag so that it can continue to the headquarters */
        assertEquals(headquarter.getAmount(COIN), 0);
        assertEquals(mint.getAmount(COIN), 0);
        assertFalse(storehouse.needsMaterial(COIN));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().getFirst().getMaterial().equals(COIN));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testCoinIsNotDeliveredTwiceToBuildingThatOnlyNeedsOne() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory so that there are no planks, stones or coins */
        Utils.clearInventory(headquarter, PLANK, COIN, STONE, COAL, GOLD);

        /* Place barracks */
        Point point4 = new Point(10, 4);
        Barracks barracks = map.placeBuilding(new Barracks(player0), point4);

        /* Construct the barracks */
        Utils.constructHouse(barracks);

        /* Connect the barracks to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, barracks.getFlag(), headquarter.getFlag());

        /* Place the mint */
        Point point1 = new Point(14, 4);
        Mint mint = map.placeBuilding(new Mint(player0), point1);

        /* Connect the mint with the barracks */
        Road road0 = map.placeAutoSelectedRoad(player0, mint.getFlag(), barracks.getFlag());

        /* Deliver the needed material to construct the mint */
        Utils.deliverCargos(mint, PLANK, 2);
        Utils.deliverCargos(mint, STONE, 2);

        /* Wait for the mint to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(mint);

        Utils.waitForNonMilitaryBuildingToGetPopulated(mint);

        /* Wait for the flag on the road between the barracks and the mint to have a coin cargo */
        Utils.deliverCargo(mint, COAL);
        Utils.deliverCargo(mint, GOLD);

        Utils.waitForFlagToGetStackedCargo(map, mint.getFlag(), 1);

        assertEquals(mint.getFlag().getStackedCargo().getFirst().getMaterial(), COIN);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        assertEquals(road0.getCourier().getCargo().getTarget(), barracks);

        /* Verify that no coin is delivered from the headquarters */
        Utils.adjustInventoryTo(headquarter, COIN, 1);

        assertEquals(barracks.getCanHoldAmount(COIN) - barracks.getAmount(COIN), 1);
        assertFalse(barracks.needsMaterial(COIN));

        for (int i = 0; i < 200; i++) {
            if (barracks.getAmount(COIN) == 0) {
                break;
            }

            assertNull(headquarter.getWorker().getCargo());
            assertEquals(headquarter.getAmount(COIN), 1);

            map.stepTime();
        }
    }

    @Test
    public void testProductionOfOneBreadConsumesOneGoldAndOneFlour() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point3 = new Point(7, 9);
        Mint mint = map.placeBuilding(new Mint(player0), point3);

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        /* Populate the mint */
        Worker minter = Utils.occupyBuilding(new Minter(player0, map), mint);

        /* Deliver ingredients to the mint */
        mint.putCargo(new Cargo(GOLD, map));
        mint.putCargo(new Cargo(COAL, map));

        /* Wait until the mint worker produces a bread */
        assertEquals(mint.getAmount(GOLD), 1);
        assertEquals(mint.getAmount(COAL), 1);

        Utils.fastForward(150, map);

        assertEquals(mint.getAmount(GOLD), 0);
        assertEquals(mint.getAmount(COAL), 0);
    }

    @Test
    public void testProductionCountdownStartsWhenIngredientsAreAvailable() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point3 = new Point(7, 9);
        Mint mint = map.placeBuilding(new Mint(player0), point3);

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        /* Populate the mint */
        Worker minter = Utils.occupyBuilding(new Minter(player0, map), mint);

        /* Fast forward so that the mint worker would have produced bread if it had had the ingredients */
        Utils.fastForward(150, map);

        assertNull(minter.getCargo());

        /* Deliver ingredients to the mint */
        mint.putCargo(new Cargo(GOLD, map));
        mint.putCargo(new Cargo(COAL, map));

        /* Verify that it takes 50 steps for the mint worker to produce the plank */
        for (int i = 0; i < 50; i++) {
            assertNull(minter.getCargo());
            map.stepTime();
        }

        assertNotNull(minter.getCargo());
    }

    @Test
    public void testMintWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mint */
        Point point26 = new Point(8, 8);
        Mint mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(player0, map), mint0);

        /* Deliver material to the mint */
        Cargo coalCargo = new Cargo(COAL, map);
        Cargo goldCargo = new Cargo(GOLD, map);

        mint0.putCargo(coalCargo);
        mint0.putCargo(coalCargo);

        mint0.putCargo(goldCargo);
        mint0.putCargo(goldCargo);

        /* Let the minter rest */
        Utils.fastForward(100, map);

        /* Wait for the minter to produce a new coin cargo */
        Utils.fastForward(50, map);

        Worker worker = mint0.getWorker();

        assertNotNull(worker.getCargo());

        /* Verify that the minter puts the coin cargo at the flag */
        assertEquals(worker.getTarget(), mint0.getFlag().getPosition());
        assertTrue(mint0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mint0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(mint0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the mint */
        assertEquals(worker.getTarget(), mint0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mint0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(worker.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(worker.getTarget(), mint0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mint0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertEquals(mint0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mint */
        Point point26 = new Point(8, 8);
        Mint mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0);

        /* Deliver material to the mint */
        Cargo coalCargo = new Cargo(COAL, map);
        Cargo goldCargo = new Cargo(GOLD, map);

        mint0.putCargo(coalCargo);
        mint0.putCargo(coalCargo);

        mint0.putCargo(goldCargo);
        mint0.putCargo(goldCargo);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(player0, map), mint0);

        /* Let the minter rest */
        Utils.fastForward(100, map);

        /* Wait for the minter to produce a new coin cargo */
        Utils.fastForward(50, map);

        Worker worker = mint0.getWorker();

        assertNotNull(worker.getCargo());

        /* Verify that the minter puts the coin cargo at the flag */
        assertEquals(worker.getTarget(), mint0.getFlag().getPosition());
        assertTrue(mint0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mint0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(mint0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = mint0.getFlag().getStackedCargo().getFirst();

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), mint0.getFlag().getPosition());

        /* Remove the resources needed for the mint in the headquarter */
        Utils.adjustInventoryTo(headquarter0, GOLD, 0);
        Utils.adjustInventoryTo(headquarter0, COAL, 0);

        /* Connect the mint with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mint0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), mint0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));


        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), mint0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(COIN);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(COIN), amount + 1);
    }

    @Test
    public void testMinterGoesBackToStorageWhenMintIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mint */
        Point point26 = new Point(8, 8);
        Mint mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(player0, map), mint0);

        /* Destroy the mint */
        Worker worker = mint0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), mint0.getPosition());

        mint0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MINTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

        /* Verify that the minter is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINTER), amount + 1);
    }

    @Test
    public void testMinterGoesBackOnToStorageOnRoadsIfPossibleWhenMintIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mint */
        Point point26 = new Point(8, 8);
        Mint mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Connect the mint with the headquarter */
        map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the mint */
        Utils.constructHouse(mint0);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(player0, map), mint0);

        /* Destroy the mint */
        Worker worker = mint0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), mint0.getPosition());

        mint0.tearDown();

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
    public void testDestroyedMintIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mint */
        Point point26 = new Point(8, 8);
        Mint mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Connect the mint with the headquarter */
        map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the mint */
        Utils.constructHouse(mint0);

        /* Destroy the mint */
        mint0.tearDown();

        assertTrue(mint0.isBurningDown());

        /* Wait for the mint to stop burning */
        Utils.fastForward(50, map);

        assertTrue(mint0.isDestroyed());

        /* Wait for the mint to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), mint0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(mint0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mint */
        Point point26 = new Point(8, 8);
        Mint mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(mint0.getPosition(), mint0.getFlag().getPosition()));

        map.removeFlag(mint0.getFlag());

        assertNull(map.getRoad(mint0.getPosition(), mint0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mint */
        Point point26 = new Point(8, 8);
        Mint mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(mint0.getPosition(), mint0.getFlag().getPosition()));

        mint0.tearDown();

        assertNull(map.getRoad(mint0.getPosition(), mint0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInMintCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(12, 8);
        Mint mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Connect the mint and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter.getFlag());

        /* Finish the mint */
        Utils.constructHouse(mint0);

        /* Deliver material to the mint */
        Cargo coalCargo = new Cargo(COAL, map);
        Cargo goldCargo = new Cargo(GOLD, map);

        mint0.putCargo(coalCargo);
        mint0.putCargo(coalCargo);

        mint0.putCargo(goldCargo);
        mint0.putCargo(goldCargo);

        /* Assign a worker to the mint */
        Minter worker = new Minter(player0, map);

        Utils.occupyBuilding(worker, mint0);

        assertTrue(worker.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the minter to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), COIN);

        /* Wait for the worker to deliver the cargo */
        assertEquals(worker.getTarget(), mint0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mint0.getFlag().getPosition());

        /* Stop production and verify that no coin is produced */
        mint0.stopProduction();

        assertFalse(mint0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInMintCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(10, 8);
        Mint mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Connect the mint and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter.getFlag());

        /* Finish the mint */
        Utils.constructHouse(mint0);

        /* Assign a worker to the mint */
        Minter worker = new Minter(player0, map);

        Utils.occupyBuilding(worker, mint0);

        assertTrue(worker.isInsideBuilding());

        /* Deliver material to the mint */
        Cargo coalCargo = new Cargo(COAL, map);
        Cargo goldCargo = new Cargo(GOLD, map);

        mint0.putCargo(coalCargo);
        mint0.putCargo(coalCargo);

        mint0.putCargo(goldCargo);
        mint0.putCargo(goldCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the minter to produce coin */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), COIN);

        /* Wait for the worker to deliver the cargo */
        assertEquals(worker.getTarget(), mint0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mint0.getFlag().getPosition());

        /* Stop production */
        mint0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the mint produces coin again */
        mint0.resumeProduction();

        assertTrue(mint0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertNotNull(worker.getCargo());
    }

    @Test
    public void testAssignedMinterHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(20, 14);
        Mint mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0);

        /* Connect the mint with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mint0.getFlag());

        /* Wait for minter to get assigned and leave the headquarter */
        List<Minter> workers = Utils.waitForWorkersOutsideBuilding(Minter.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Minter worker = workers.getFirst();

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.GREEN);
        Player player2 = new Player("Player 2", PlayerColor.RED);

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
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 9);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place mint close to the new border */
        Point point4 = new Point(28, 18);
        Mint mint0 = map.placeBuilding(new Mint(player0), point4);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0);

        /* Occupy the mint */
        Minter worker = Utils.occupyBuilding(new Minter(player0, map), mint0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMinterReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place mint */
        Point point2 = new Point(14, 4);
        Mint mint0 = map.placeBuilding(new Mint(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, mint0.getFlag());

        /* Wait for the minter to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Minter.class, 1, player0);

        Minter minter = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Minter) {
                minter = (Minter) worker;
            }
        }

        assertNotNull(minter);
        assertEquals(minter.getTarget(), mint0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, minter, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the minter has started walking */
        assertFalse(minter.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the minter continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, minter, flag0.getPosition());

        assertEquals(minter.getPosition(), flag0.getPosition());

        /* Verify that the minter returns to the headquarter when it reaches the flag */
        assertEquals(minter.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, minter, headquarter0.getPosition());
    }

    @Test
    public void testMinterContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place mint */
        Point point2 = new Point(14, 4);
        Mint mint0 = map.placeBuilding(new Mint(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, mint0.getFlag());

        /* Wait for the minter to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Minter.class, 1, player0);

        Minter minter = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Minter) {
                minter = (Minter) worker;
            }
        }

        assertNotNull(minter);
        assertEquals(minter.getTarget(), mint0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, minter, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the minter has started walking */
        assertFalse(minter.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the minter continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, minter, flag0.getPosition());

        assertEquals(minter.getPosition(), flag0.getPosition());

        /* Verify that the minter continues to the final flag */
        assertEquals(minter.getTarget(), mint0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, minter, mint0.getFlag().getPosition());

        /* Verify that the minter goes out to minter instead of going directly back */
        assertNotEquals(minter.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMinterReturnsToStorageIfMintIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place mint */
        Point point2 = new Point(14, 4);
        Mint mint0 = map.placeBuilding(new Mint(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, mint0.getFlag());

        /* Wait for the minter to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Minter.class, 1, player0);

        Minter minter = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Minter) {
                minter = (Minter) worker;
            }
        }

        assertNotNull(minter);
        assertEquals(minter.getTarget(), mint0.getPosition());

        /* Wait for the minter to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, minter, flag0.getPosition());

        map.stepTime();

        /* See that the minter has started walking */
        assertFalse(minter.isExactlyAtPoint());

        /* Tear down the mint */
        mint0.tearDown();

        /* Verify that the minter continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, minter, mint0.getFlag().getPosition());

        assertEquals(minter.getPosition(), mint0.getFlag().getPosition());

        /* Verify that the minter goes back to storage */
        assertEquals(minter.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMinterGoesOffroadBackToClosestStorageWhenMintIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mint */
        Point point26 = new Point(17, 17);
        Mint mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(player0, map), mint0);

        /* Place a second storage closer to the mint */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the mint */
        Worker minter = mint0.getWorker();

        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getPosition(), mint0.getPosition());

        mint0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(minter.isInsideBuilding());
        assertEquals(minter.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(MINTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, minter, storehouse0.getPosition());

        /* Verify that the minter is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(MINTER), amount + 1);
    }

    @Test
    public void testMinterReturnsOffroadAndAvoidsBurningStorageWhenMintIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mint */
        Point point26 = new Point(17, 17);
        Mint mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(player0, map), mint0);

        /* Place a second storage closer to the mint */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Destroy the mint */
        Worker minter = mint0.getWorker();

        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getPosition(), mint0.getPosition());

        mint0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(minter.isInsideBuilding());
        assertEquals(minter.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MINTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, minter, headquarter0.getPosition());

        /* Verify that the minter is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINTER), amount + 1);
    }

    @Test
    public void testMinterReturnsOffroadAndAvoidsDestroyedStorageWhenMintIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mint */
        Point point26 = new Point(17, 17);
        Mint mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(player0, map), mint0);

        /* Place a second storage closer to the mint */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

        /* Destroy the mint */
        Worker minter = mint0.getWorker();

        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getPosition(), mint0.getPosition());

        mint0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(minter.isInsideBuilding());
        assertEquals(minter.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MINTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, minter, headquarter0.getPosition());

        /* Verify that the minter is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINTER), amount + 1);
    }

    @Test
    public void testMinterReturnsOffroadAndAvoidsUnfinishedStorageWhenMintIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mint */
        Point point26 = new Point(17, 17);
        Mint mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(player0, map), mint0);

        /* Place a second storage closer to the mint */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the mint */
        Worker minter = mint0.getWorker();

        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getPosition(), mint0.getPosition());

        mint0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(minter.isInsideBuilding());
        assertEquals(minter.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MINTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, minter, headquarter0.getPosition());

        /* Verify that the minter is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINTER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mint */
        Point point26 = new Point(17, 17);
        Mint mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Place road to connect the headquarter and the mint */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mint0.getFlag());

        /* Finish construction of the mint */
        Utils.constructHouse(mint0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Minter.class, 1, player0).getFirst();

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mint0.getFlag().getPosition());

        /* Tear down the building */
        mint0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(worker.getTarget(), mint0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mint0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testMintWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(7, 9);
        Mint mint = map.placeBuilding(new Mint(player0), point1);

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        /* Populate the mint */
        Worker minter0 = Utils.occupyBuilding(new Minter(player0, map), mint);

        assertTrue(minter0.isInsideBuilding());
        assertEquals(minter0.getHome(), mint);
        assertEquals(mint.getWorker(), minter0);

        /* Verify that the productivity is 0% when the mint doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(mint.getFlag().getStackedCargo().isEmpty());
            assertNull(minter0.getCargo());
            assertEquals(mint.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testMintWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(7, 9);
        Mint mint = map.placeBuilding(new Mint(player0), point1);

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        /* Populate the mint */
        Worker minter0 = Utils.occupyBuilding(new Minter(player0, map), mint);

        assertTrue(minter0.isInsideBuilding());
        assertEquals(minter0.getHome(), mint);
        assertEquals(mint.getWorker(), minter0);

        /* Connect the mint with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mint.getFlag());

        /* Make the mint produce some coins with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (mint.needsMaterial(COAL) && mint.getAmount(COAL) < 2) {
                mint.putCargo(new Cargo(COAL, map));
            }

            if (mint.needsMaterial(GOLD) && mint.getAmount(GOLD) < 2) {
                mint.putCargo(new Cargo(GOLD, map));
            }
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(mint.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (mint.needsMaterial(COAL) && mint.getAmount(COAL) < 2) {
                mint.putCargo(new Cargo(COAL, map));
            }

            if (mint.needsMaterial(GOLD) && mint.getAmount(GOLD) < 2) {
                mint.putCargo(new Cargo(GOLD, map));
            }

            assertEquals(mint.getProductivity(), 100);
        }
    }

    @Test
    public void testMintLosesProductivityWhenResourcesRunOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(7, 9);
        Mint mint = map.placeBuilding(new Mint(player0), point1);

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        /* Populate the mint */
        Worker minter0 = Utils.occupyBuilding(new Minter(player0, map), mint);

        assertTrue(minter0.isInsideBuilding());
        assertEquals(minter0.getHome(), mint);
        assertEquals(mint.getWorker(), minter0);

        /* Connect the mint with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mint.getFlag());

        /* Make the mint produce some coins with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (mint.needsMaterial(COAL) && mint.getAmount(COAL) < 2) {
                mint.putCargo(new Cargo(COAL, map));
            }

            if (mint.needsMaterial(GOLD) && mint.getAmount(GOLD) < 2) {
                mint.putCargo(new Cargo(GOLD, map));
            }
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(mint.getProductivity(), 100);

        for (int i = 0; i < 5000; i++) {
            map.stepTime();
        }

        assertEquals(mint.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedMintHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(7, 9);
        Mint mint = map.placeBuilding(new Mint(player0), point1);

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        /* Verify that the unoccupied mint is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(mint.getProductivity(), 0);

            if (mint.needsMaterial(COAL) && mint.getAmount(COAL) < 2) {
                mint.putCargo(new Cargo(COAL, map));
            }


            if (mint.needsMaterial(GOLD) && mint.getAmount(GOLD) < 2) {
                mint.putCargo(new Cargo(GOLD, map));
            }
            map.stepTime();
        }
    }

    @Test
    public void testMintCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(10, 10);
        Mint mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0);

        /* Populate the mint */
        Worker minter0 = Utils.occupyBuilding(new Minter(player0, map), mint0);

        /* Verify that the mint can produce */
        assertTrue(mint0.canProduce());
    }

    @Test
    public void testMintReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(6, 12);
        Mint mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Construct the mint */
        Utils.constructHouse(mint0);

        /* Verify that the reported output is correct */
        assertEquals(mint0.getProducedMaterial().length, 1);
        assertEquals(mint0.getProducedMaterial()[0], COIN);
    }

    @Test
    public void testMintReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(6, 12);
        Mint mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(mint0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(mint0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(mint0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(mint0.getCanHoldAmount(PLANK), 2);
        assertEquals(mint0.getCanHoldAmount(STONE), 2);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(mint0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testMintReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(6, 12);
        Mint mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Construct the mint */
        Utils.constructHouse(mint0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(mint0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(mint0.getTypesOfMaterialNeeded().contains(COAL));
        assertTrue(mint0.getTypesOfMaterialNeeded().contains(GOLD));
        assertEquals(mint0.getCanHoldAmount(COAL), 6);
        assertEquals(mint0.getCanHoldAmount(GOLD), 6);

        for (Material material : Material.values()) {
            if (material == COAL || material == GOLD) {
                continue;
            }

            assertEquals(mint0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testMintWaitsWhenFlagIsFull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(16, 6);
        Mint mint = map.placeBuilding(new Mint(player0), point1);

        /* Connect the mint with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mint.getFlag(), headquarter.getFlag());

        /* Wait for the mint to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(mint);
        Utils.waitForNonMilitaryBuildingToGetPopulated(mint);

        /* Give material to the mint */
        Utils.putCargoToBuilding(mint, GOLD);
        Utils.putCargoToBuilding(mint, COAL);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, mint.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the mint waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(mint.getFlag().getStackedCargo().size(), 8);
            assertNull(mint.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the mint with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, mint.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(mint.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(mint.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(mint.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, mint.getWorker(), COIN);
    }

    @Test
    public void testMintDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(16, 6);
        Mint mint = map.placeBuilding(new Mint(player0), point1);

        /* Connect the mint with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mint.getFlag(), headquarter.getFlag());

        /* Wait for the mint to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(mint);
        Utils.waitForNonMilitaryBuildingToGetPopulated(mint);

        /* Give material to the mint */
        Utils.putCargoToBuilding(mint, GOLD);
        Utils.putCargoToBuilding(mint, GOLD);
        Utils.putCargoToBuilding(mint, COAL);
        Utils.putCargoToBuilding(mint, COAL);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, mint.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The mint waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(mint.getFlag().getStackedCargo().size(), 8);
            assertNull(mint.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the mint with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, mint.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(mint.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(mint.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(mint.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, mint.getWorker(), COIN);

        /* Wait for the worker to put the cargo on the flag */
        assertEquals(mint.getWorker().getTarget(), mint.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, mint.getWorker(), mint.getFlag().getPosition());

        assertEquals(mint.getFlag().getStackedCargo().size(), 8);

        /* Verify that the mint doesn't produce anything because the flag is full */
        for (int i = 0; i < 400; i++) {
            assertEquals(mint.getFlag().getStackedCargo().size(), 8);
            assertNull(mint.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testWhenCoinDeliveryAreBlockedMintFillsUpFlagAndThenStops() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place Mint */
        Point point1 = new Point(7, 9);
        Mint mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Place road to connect the mint with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter0.getFlag());

        /* Wait for the mint to get constructed and occupied */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(mint0);

        Worker minter0 = Utils.waitForNonMilitaryBuildingToGetPopulated(mint0);

        assertTrue(minter0.isInsideBuilding());
        assertEquals(minter0.getHome(), mint0);
        assertEquals(mint0.getWorker(), minter0);

        /* Add a lot of material to the headquarter for the mint to consume */
        Utils.adjustInventoryTo(headquarter0, COAL, 40);
        Utils.adjustInventoryTo(headquarter0, GOLD, 40);

        /* Block storage of coins */
        headquarter0.blockDeliveryOfMaterial(COIN);

        /* Verify that the mint puts eight coins on the flag and then stops */
        Utils.waitForFlagToGetStackedCargo(map, mint0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, minter0, mint0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(mint0.getFlag().getStackedCargo().size(), 8);
            assertTrue(minter0.isInsideBuilding());

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), BEER);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndMintIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place mint */
        Point point2 = new Point(18, 6);
        Mint mint0 = map.placeBuilding(new Mint(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the mint */
        Road road1 = map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the mint and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, mint0);

        /* Add a lot of material to the headquarter for the mint to consume */
        Utils.adjustInventoryTo(headquarter0, COAL, 40);
        Utils.adjustInventoryTo(headquarter0, GOLD, 40);

        /* Wait for the mint and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, mint0);

        Worker minter0 = mint0.getWorker();

        assertTrue(minter0.isInsideBuilding());
        assertEquals(minter0.getHome(), mint0);
        assertEquals(mint0.getWorker(), minter0);

        /* Verify that the worker goes to the storage when the mint is torn down */
        headquarter0.blockDeliveryOfMaterial(MINTER);

        mint0.tearDown();

        map.stepTime();

        assertFalse(minter0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, minter0, mint0.getFlag().getPosition());

        assertEquals(minter0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, minter0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(minter0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndMintIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place mint */
        Point point2 = new Point(18, 6);
        Mint mint0 = map.placeBuilding(new Mint(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the mint */
        Road road1 = map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the mint and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, mint0);

        /* Add a lot of material to the headquarter for the mint to consume */
        Utils.adjustInventoryTo(headquarter0, COAL, 40);
        Utils.adjustInventoryTo(headquarter0, GOLD, 40);

        /* Wait for the mint and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, mint0);

        Worker minter0 = mint0.getWorker();

        assertTrue(minter0.isInsideBuilding());
        assertEquals(minter0.getHome(), mint0);
        assertEquals(mint0.getWorker(), minter0);

        /* Verify that the worker goes to the storage off-road when the mint is torn down */
        headquarter0.blockDeliveryOfMaterial(MINTER);

        mint0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(minter0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, minter0, mint0.getFlag().getPosition());

        assertEquals(minter0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, minter0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(minter0));
    }

    @Test
    public void testWorkerGoesOutAndBackInWhenSentOutWithoutBlocking() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, MINTER, 1);

        assertEquals(headquarter0.getAmount(MINTER), 1);

        headquarter0.pushOutAll(MINTER);

        for (int i = 0; i < 10; i++) {
            Worker worker = Utils.waitForWorkerOutsideBuilding(Minter.class, player0);

            assertEquals(headquarter0.getAmount(MINTER), 0);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, MINTER, 1);

        headquarter0.blockDeliveryOfMaterial(MINTER);
        headquarter0.pushOutAll(MINTER);

        Worker worker = Utils.waitForWorkerOutsideBuilding(Minter.class, player0);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(7, 9);
        Mint mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Place road to connect the mint with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the mint to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(mint0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(mint0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter
        */
        headquarter0.blockDeliveryOfMaterial(MINTER);

        Worker worker = mint0.getWorker();

        mint0.tearDown();

        assertEquals(worker.getPosition(), mint0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mint0.getFlag().getPosition());

        assertEquals(worker.getPosition(), mint0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), mint0.getPosition());
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(7, 9);
        Mint mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Place road to connect the mint with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the mint to get constructed */
        Utils.waitForBuildingToBeConstructed(mint0);

        /* Wait for a minter to start walking to the mint */
        Minter minter = Utils.waitForWorkerOutsideBuilding(Minter.class, player0);

        /* Wait for the minter to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, minter, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the minter goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(minter.getTarget(), mint0.getPosition());

        headquarter0.blockDeliveryOfMaterial(MINTER);

        mint0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, minter, mint0.getFlag().getPosition());

        assertEquals(minter.getPosition(), mint0.getFlag().getPosition());
        assertNotEquals(minter.getTarget(), headquarter0.getPosition());
        assertFalse(minter.isInsideBuilding());
        assertNull(mint0.getWorker());
        assertNotNull(minter.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, minter, minter.getTarget());

        Point point = minter.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(minter.isDead());
            assertEquals(minter.getPosition(), point);
            assertTrue(map.getWorkers().contains(minter));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(minter));
    }
}
