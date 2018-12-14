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
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Mint;
import org.appland.settlers.model.Minter;
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
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.MINTER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
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
public class TestMint {

    @Test
    public void testMintOnlyNeedsTwoPlanksAndTwoStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing mint */
        Point point22 = new Point(6, 22);
        Building mint0 = map.placeBuilding(new Mint(player0), point22);

        /* Deliver two plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        mint0.putCargo(plankCargo);
        mint0.putCargo(plankCargo);
        mint0.putCargo(stoneCargo);
        mint0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the mint */
        for (int i = 0; i < 150; i++) {
            assertTrue(mint0.underConstruction());

            map.stepTime();
        }

        assertTrue(mint0.ready());
    }

    @Test
    public void testMintCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing mint */
        Point point22 = new Point(6, 22);
        Building mint0 = map.placeBuilding(new Mint(player0), point22);

        /* Deliver one plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        mint0.putCargo(plankCargo);
        mint0.putCargo(stoneCargo);
        mint0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the mint */
        for (int i = 0; i < 500; i++) {
            assertTrue(mint0.underConstruction());

            map.stepTime();
        }

        assertFalse(mint0.ready());
    }

    @Test
    public void testMintCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing mint */
        Point point22 = new Point(6, 22);
        Building mint0 = map.placeBuilding(new Mint(player0), point22);

        /* Deliver two planks and one stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        mint0.putCargo(plankCargo);
        mint0.putCargo(plankCargo);
        mint0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the mint */
        for (int i = 0; i < 500; i++) {
            assertTrue(mint0.underConstruction());

            map.stepTime();
        }

        assertFalse(mint0.ready());
    }

    @Test
    public void testMintNeedsWorker() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(player0), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Unfinished mint doesn't need minter */
        assertFalse(mint.needsWorker());

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);

        assertTrue(mint.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneMinterAtStart() {
        Headquarter headquarter = new Headquarter(null);

        assertEquals(headquarter.getAmount(MINTER), 1);
    }

    @Test
    public void testMintGetsAssignedWorker() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(player0), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);

        assertTrue(mint.needsWorker());

        /* Verify that a minter leaves the headquarter */
        Utils.fastForward(3, map);

        assertEquals(map.getWorkers().size(), 3);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(player0), point3);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);

        /* Populate the mint */
        Worker minter = Utils.occupyBuilding(new Minter(player0, map), mint, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(player0), point3);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);

        /* Verify that the mint doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(mint.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedMintWithGoldAndCoalProducesCoins() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(player0), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);

        /* Populate the mint */
        Worker minter = Utils.occupyBuilding(new Minter(player0, map), mint, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(player0), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);

        /* Populate the mint */
        Worker minter = Utils.occupyBuilding(new Minter(player0, map), mint, map);

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
    public void testProductionOfOneBreadConsumesOneWaterAndOneFlour() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(player0), point3);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);

        /* Populate the mint */
        Worker minter = Utils.occupyBuilding(new Minter(player0, map), mint, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(player0), point3);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);

        /* Populate the mint */
        Worker minter = Utils.occupyBuilding(new Minter(player0, map), mint, map);

        /* Fast forward so that the mint worker would have produced bread
           if it had had the ingredients
        */
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing mint */
        Point point26 = new Point(8, 8);
        Building mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(player0, map), mint0, map);

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
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing mint */
        Point point26 = new Point(8, 8);
        Building mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Deliver material to the mint */
        Cargo coalCargo = new Cargo(COAL, map);
        Cargo goldCargo = new Cargo(GOLD, map);

        mint0.putCargo(coalCargo);
        mint0.putCargo(coalCargo);

        mint0.putCargo(goldCargo);
        mint0.putCargo(goldCargo);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(player0, map), mint0, map);

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
        Cargo cargo = mint0.getFlag().getStackedCargo().get(0);

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), mint0.getFlag().getPosition());

        /* Remove the resources needed for the mint in the headquarter */
        Utils.adjustInventoryTo(headquarter0, GOLD, 0, map);
        Utils.adjustInventoryTo(headquarter0, COAL, 0, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing mint */
        Point point26 = new Point(8, 8);
        Building mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(player0, map), mint0, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing mint */
        Point point26 = new Point(8, 8);
        Building mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Connect the mint with the headquarter */
        map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(player0, map), mint0, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing mint */
        Point point26 = new Point(8, 8);
        Building mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Connect the mint with the headquarter */
        map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Destroy the mint */
        mint0.tearDown();

        assertTrue(mint0.burningDown());

        /* Wait for the mint to stop burning */
        Utils.fastForward(50, map);

        assertTrue(mint0.destroyed());

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing mint */
        Point point26 = new Point(8, 8);
        Building mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(mint0.getPosition(), mint0.getFlag().getPosition()));

        map.removeFlag(mint0.getFlag());

        assertNull(map.getRoad(mint0.getPosition(), mint0.getFlag().getPosition()));
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

        /* Placing mint */
        Point point26 = new Point(8, 8);
        Building mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(mint0.getPosition(), mint0.getFlag().getPosition()));

        mint0.tearDown();

        assertNull(map.getRoad(mint0.getPosition(), mint0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInMintCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(8, 6);
        Building mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Connect the mint and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the mint */
        Utils.constructHouse(mint0, map);

        /* Deliver material to the mint */
        Cargo coalCargo = new Cargo(COAL, map);
        Cargo goldCargo = new Cargo(GOLD, map);

        mint0.putCargo(coalCargo);
        mint0.putCargo(coalCargo);

        mint0.putCargo(goldCargo);
        mint0.putCargo(goldCargo);

        /* Assign a worker to the mint */
        Minter worker = new Minter(player0, map);

        Utils.occupyBuilding(worker, mint0, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(8, 6);
        Building mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Connect the mint and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the mint */
        Utils.constructHouse(mint0, map);

        /* Assign a worker to the mint */
        Minter worker = new Minter(player0, map);

        Utils.occupyBuilding(worker, mint0, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place mint */
        Point point1 = new Point(20, 14);
        Building mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Connect the mint with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mint0.getFlag());

        /* Wait for minter to get assigned and leave the headquarter */
        List<Minter> workers = Utils.waitForWorkersOutsideBuilding(Minter.class, 1, player0, map);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Minter worker = workers.get(0);

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
        Headquarter headquarter2 = new Headquarter(player2);
        Point point10 = new Point(70, 70);
        map.placeBuilding(headquarter2, point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
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

        /* Place mint close to the new border */
        Point point4 = new Point(28, 18);
        Mint mint0 = map.placeBuilding(new Mint(player0), point4);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Occupy the mint */
        Minter worker = Utils.occupyBuilding(new Minter(player0, map), mint0, map);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMinterReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

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

        /* Placing mint */
        Point point2 = new Point(14, 4);
        Building mint0 = map.placeBuilding(new Mint(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, mint0.getFlag());

        /* Wait for the minter to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Minter.class, 1, player0, map);

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

        /* Placing mint */
        Point point2 = new Point(14, 4);
        Building mint0 = map.placeBuilding(new Mint(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, mint0.getFlag());

        /* Wait for the minter to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Minter.class, 1, player0, map);

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

        /* Placing mint */
        Point point2 = new Point(14, 4);
        Building mint0 = map.placeBuilding(new Mint(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, mint0.getFlag());

        /* Wait for the minter to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Minter.class, 1, player0, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing mint */
        Point point26 = new Point(17, 17);
        Building mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(player0, map), mint0, map);

        /* Place a second storage closer to the mint */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the mint */
        Worker minter = mint0.getWorker();

        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getPosition(), mint0.getPosition());

        mint0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(minter.isInsideBuilding());
        assertEquals(minter.getTarget(), storage0.getPosition());

        int amount = storage0.getAmount(MINTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, minter, storage0.getPosition());

        /* Verify that the minter is stored correctly in the headquarter */
        assertEquals(storage0.getAmount(MINTER), amount + 1);
    }

    @Test
    public void testMinterReturnsOffroadAndAvoidsBurningStorageWhenMintIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing mint */
        Point point26 = new Point(17, 17);
        Building mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(player0, map), mint0, map);

        /* Place a second storage closer to the mint */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing mint */
        Point point26 = new Point(17, 17);
        Building mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(player0, map), mint0, map);

        /* Place a second storage closer to the mint */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storage0, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing mint */
        Point point26 = new Point(17, 17);
        Building mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(player0, map), mint0, map);

        /* Place a second storage closer to the mint */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mint */
        Point point26 = new Point(17, 17);
        Building mint0 = map.placeBuilding(new Mint(player0), point26);

        /* Place road to connect the headquarter and the mint */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mint0.getFlag());

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Minter.class, 1, player0, map).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mint0.getFlag().getPosition());

        /* Tear down the building */
        mint0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquarter instead of entering
        */
        assertEquals(worker.getTarget(), mint0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mint0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testMintWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(player0), point1);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);

        /* Populate the mint */
        Worker minter0 = Utils.occupyBuilding(new Minter(player0, map), mint, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(player0), point1);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);

        /* Populate the mint */
        Worker minter0 = Utils.occupyBuilding(new Minter(player0, map), mint, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(player0), point1);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);

        /* Populate the mint */
        Worker minter0 = Utils.occupyBuilding(new Minter(player0, map), mint, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(player0), point1);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(10, 10);
        Building mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Populate the mint */
        Worker minter0 = Utils.occupyBuilding(new Minter(player0, map), mint0, map);

        /* Verify that the mint can produce */
        assertTrue(mint0.canProduce());
    }

    @Test
    public void testMintReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(6, 22);
        Building mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Construct the mint */
        Utils.constructHouse(mint0, map);

        /* Verify that the reported output is correct */
        assertEquals(mint0.getProducedMaterial().length, 1);
        assertEquals(mint0.getProducedMaterial()[0], COIN);
    }

    @Test
    public void testMintReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(6, 22);
        Building mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(mint0.getMaterialNeeded().size(), 2);
        assertTrue(mint0.getMaterialNeeded().contains(PLANK));
        assertTrue(mint0.getMaterialNeeded().contains(STONE));
        assertEquals(mint0.getTotalAmountNeeded(PLANK), 2);
        assertEquals(mint0.getTotalAmountNeeded(STONE), 2);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(mint0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testMintReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(6, 22);
        Building mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Construct the mint */
        Utils.constructHouse(mint0, map);

        /* Verify that the reported needed construction material is correct */
        assertEquals(mint0.getMaterialNeeded().size(), 2);
        assertTrue(mint0.getMaterialNeeded().contains(COAL));
        assertTrue(mint0.getMaterialNeeded().contains(GOLD));
        assertEquals(mint0.getTotalAmountNeeded(COAL), 1);
        assertEquals(mint0.getTotalAmountNeeded(GOLD), 1);

        for (Material material : Material.values()) {
            if (material == COAL || material == GOLD) {
                continue;
            }

            assertEquals(mint0.getTotalAmountNeeded(material), 0);
        }
    }
}
