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
import org.appland.settlers.model.IronFounder;
import org.appland.settlers.model.IronSmelter;
import org.appland.settlers.model.Material;
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
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.IRON_BAR;
import static org.appland.settlers.model.Material.IRON_FOUNDER;
import static org.appland.settlers.model.Material.PLANCK;
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
public class TestIronSmelter {

    @Test
    public void testIronSmelterOnlyNeedsTwoPlancksAndTwoStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing iron smelter */
        Point point22 = new Point(6, 22);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point22);

        /* Deliver two planck and two stone */
        Cargo planckCargo = new Cargo(PLANCK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        ironSmelter0.putCargo(planckCargo);
        ironSmelter0.putCargo(planckCargo);
        ironSmelter0.putCargo(stoneCargo);
        ironSmelter0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the iron smelter */
        for (int i = 0; i < 150; i++) {
            assertTrue(ironSmelter0.underConstruction());

            map.stepTime();
        }

        assertTrue(ironSmelter0.ready());
    }

    @Test
    public void testIronSmelterCannotBeConstructedWithTooFewPlancks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing iron smelter */
        Point point22 = new Point(6, 22);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point22);

        /* Deliver one planck and two stone */
        Cargo planckCargo = new Cargo(PLANCK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        ironSmelter0.putCargo(planckCargo);
        ironSmelter0.putCargo(stoneCargo);
        ironSmelter0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the iron smelter */
        for (int i = 0; i < 500; i++) {
            assertTrue(ironSmelter0.underConstruction());

            map.stepTime();
        }

        assertFalse(ironSmelter0.ready());
    }

    @Test
    public void testIronSmelterCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing iron smelter */
        Point point22 = new Point(6, 22);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point22);

        /* Deliver two plancks and one stones */
        Cargo planckCargo = new Cargo(PLANCK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        ironSmelter0.putCargo(planckCargo);
        ironSmelter0.putCargo(planckCargo);
        ironSmelter0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the iron smelter */
        for (int i = 0; i < 500; i++) {
            assertTrue(ironSmelter0.underConstruction());

            map.stepTime();
        }

        assertFalse(ironSmelter0.ready());
    }

    @Test
    public void testIronSmelterNeedsWorker() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironFounder = map.placeBuilding(new IronSmelter(player0), point3);

        /* Unfinished iron smelter doesn't need worker */
        assertFalse(ironFounder.needsWorker());

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironFounder, map);

        assertTrue(ironFounder.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneIronFounderAtStart() {
        Headquarter hq = new Headquarter(null);

        assertEquals(hq.getAmount(IRON_FOUNDER), 1);
    }

    @Test
    public void testIronSmelterGetsAssignedWorker() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Place a road between the headquarter and the iron smelter */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter, map);

        assertTrue(ironSmelter.needsWorker());

        /* Verify that a iron smelter worker leaves the hq */
        assertEquals(map.getWorkers().size(), 1);

        Utils.fastForward(3, map);

        assertEquals(map.getWorkers().size(), 3);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), IronFounder.class);

        /* Let the iron smelter worker reach the iron smelter */
        IronFounder sw = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof IronFounder) {
                sw = (IronFounder)w;
            }
        }

        assertNotNull(sw);
        assertEquals(sw.getTarget(), ironSmelter.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, sw);

        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), sw);
    }

    @Test
    public void testOccupiedIronSmelterWithoutCoalAndIronProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter, map);

        /* Occupy the iron smelter */
        Worker sw = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter, map);

        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), sw);

        /* Verify that the iron smelter doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
            assertNull(sw.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedIronSmelterProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter, map);

        /* Verify that the iron smelter doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedIronSmelterWithIronAndCoalProducesIronBars() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter, map);

        /* Occupy the iron smelter */
        Worker sw = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter, map);

        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), sw);

        /* Deliver iron and coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(COAL, map));
        ironSmelter.putCargo(new Cargo(IRON, map));

        /* Verify that the iron smelter produces iron bars */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
            assertNull(sw.getCargo());
        }

        map.stepTime();

        assertNotNull(sw.getCargo());
        assertEquals(sw.getCargo().getMaterial(), IRON_BAR);
        assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testIronFounderLeavesIronBarAtTheFlag() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Place a road between the headquarter and the iron smelter */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter, map);

        /* Occupy the iron smelter */
        Worker sw = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter, map);

        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), sw);

        /* Deliver iron and coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(IRON, map));
        ironSmelter.putCargo(new Cargo(COAL, map));

        /* Verify that the iron smelter produces iron bars */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
            assertNull(sw.getCargo());
        }

        map.stepTime();

        assertNotNull(sw.getCargo());
        assertEquals(sw.getCargo().getMaterial(), IRON_BAR);
        assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());

        /* Verify that the iron smelter worker leaves the cargo at the flag */
        assertEquals(sw.getTarget(), ironSmelter.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sw, ironSmelter.getFlag().getPosition());

        assertFalse(ironSmelter.getFlag().getStackedCargo().isEmpty());
        assertNull(sw.getCargo());
        assertEquals(sw.getTarget(), ironSmelter.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, sw);

        assertTrue(sw.isInsideBuilding());
    }

    @Test
    public void testProductionOfOneIronBarConsumesOneIronAndOneCoal() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter, map);

        /* Occupy the iron smelter */
        Worker sw = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter, map);

        /* Deliver iron and coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(IRON, map));
        ironSmelter.putCargo(new Cargo(COAL, map));

        /* Wait until the iron smelter worker produces an iron bar */
        assertEquals(ironSmelter.getAmount(IRON), 1);
        assertEquals(ironSmelter.getAmount(COAL), 1);

        Utils.fastForward(150, map);

        assertEquals(ironSmelter.getAmount(IRON), 0);
        assertEquals(ironSmelter.getAmount(COAL), 0);
        assertTrue(ironSmelter.needsMaterial(IRON));
        assertTrue(ironSmelter.needsMaterial(COAL));
    }

    @Test
    public void testProductionCountdownStartsWhenIronAndCoalAreAvailable() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter, map);

        /* Occupy the iron smelter */
        Worker sw = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter, map);

        /* Fast forward so that the iron smelter worker would produced iron bars
           if it had had iron and coal
        */
        Utils.fastForward(150, map);

        assertNull(sw.getCargo());

        /* Deliver iron and coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(IRON, map));
        ironSmelter.putCargo(new Cargo(COAL, map));

        /* Verify that it takes 50 steps for the iron smelter worker to produce the iron bar */
        for (int i = 0; i < 50; i++) {
            assertNull(sw.getCargo());
            map.stepTime();
        }

        assertNotNull(sw.getCargo());
    }

    @Test
    public void testIronSmelterCannotProduceWithOnlyIron() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter, map);

        /* Occupy the iron smelter */
        Worker sw = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter, map);

        /* Deliver iron but not coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(IRON, map));

        /* Verify that the iron founder doesn't produce iron bars since it doesn't have any coal */
        for (int i = 0; i < 200; i++) {
            assertNull(sw.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testIronSmelterCannotProduceWithOnlyCoal() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter, map);

        /* Occupy the iron smelter */
        Worker sw = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter, map);

        /* Deliver iron but not coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(COAL, map));

        /* Verify that the iron founder doesn't produce iron bars since it doesn't have any coal */
        for (int i = 0; i < 200; i++) {
            assertNull(sw.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testIronSmelterWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0, map);

        /* Deliver material to the iron smelter */
        Cargo ironCargo = new Cargo(COAL, map);
        Cargo coalCargo = new Cargo(IRON, map);

        ironSmelter0.putCargo(ironCargo);
        ironSmelter0.putCargo(ironCargo);

        ironSmelter0.putCargo(coalCargo);
        ironSmelter0.putCargo(coalCargo);

        /* Let the iron founder rest */
        Utils.fastForward(100, map);

        /* Wait for the iron founder to produce a new iron bar cargo */
        Utils.fastForward(50, map);

        Worker ironFounder = ironSmelter0.getWorker();

        assertNotNull(ironFounder.getCargo());

        /* Verify that the iron founder puts the iron bar cargo at the flag */
        assertEquals(ironFounder.getTarget(), ironSmelter0.getFlag().getPosition());
        assertTrue(ironSmelter0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, ironSmelter0.getFlag().getPosition());

        assertNull(ironFounder.getCargo());
        assertFalse(ironSmelter0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the iron smelter */
        assertEquals(ironFounder.getTarget(), ironSmelter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, ironSmelter0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(ironFounder.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(ironFounder.getTarget(), ironSmelter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, ironSmelter0.getFlag().getPosition());

        assertNull(ironFounder.getCargo());
        assertEquals(ironSmelter0.getFlag().getStackedCargo().size(), 2);
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
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Deliver material to the iron smelter */
        Cargo ironCargo = new Cargo(COAL, map);
        Cargo coalCargo = new Cargo(IRON, map);

        ironSmelter0.putCargo(ironCargo);
        ironSmelter0.putCargo(ironCargo);

        ironSmelter0.putCargo(coalCargo);
        ironSmelter0.putCargo(coalCargo);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0, map);

        /* Let the iron founder rest */
        Utils.fastForward(100, map);

        /* Wait for the iron founder to produce a new iron bar cargo */
        Utils.fastForward(50, map);

        Worker ironFounder = ironSmelter0.getWorker();

        assertNotNull(ironFounder.getCargo());

        /* Verify that the iron founder puts the iron bar cargo at the flag */
        assertEquals(ironFounder.getTarget(), ironSmelter0.getFlag().getPosition());
        assertTrue(ironSmelter0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, ironSmelter0.getFlag().getPosition());

        assertNull(ironFounder.getCargo());
        assertFalse(ironSmelter0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = ironSmelter0.getFlag().getStackedCargo().get(0);

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), ironSmelter0.getFlag().getPosition());

        /* Connect the iron smelter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironSmelter0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(ironSmelter0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));


        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), ironSmelter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(IRON_BAR);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(IRON_BAR), amount + 1);
    }

    @Test
    public void testIronFounderGoesBackToStorageWhenIronSmelterIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter, map);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter, map);

        /* Destroy the iron smelter */
        Worker ironFounder = ironSmelter.getWorker();

        assertTrue(ironFounder.isInsideBuilding());
        assertEquals(ironFounder.getPosition(), ironSmelter.getPosition());

        ironSmelter.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ironFounder.isInsideBuilding());
        assertEquals(ironFounder.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(IRON_FOUNDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, headquarter0.getPosition());

        /* Verify that the iron founder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(IRON_FOUNDER), amount + 1);
    }

    @Test
    public void testIronFounderGoesBackOnToStorageOnRoadsIfPossibleWhenIronSmelterIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point26);

        /* Connect the iron smelter with the headquarter */
        map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), headquarter0.getFlag());

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter, map);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter, map);

        /* Destroy the iron smelter */
        Worker ironFounder = ironSmelter.getWorker();

        assertTrue(ironFounder.isInsideBuilding());
        assertEquals(ironFounder.getPosition(), ironSmelter.getPosition());

        ironSmelter.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ironFounder.isInsideBuilding());
        assertEquals(ironFounder.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point p : ironFounder.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test
    public void testDestroyedIronSmelterIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Connect the iron smelter with the headquarter */
        map.placeAutoSelectedRoad(player0, ironSmelter0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Destroy the iron smelter */
        ironSmelter0.tearDown();

        assertTrue(ironSmelter0.burningDown());

        /* Wait for the iron smelter to stop burning */
        Utils.fastForward(50, map);

        assertTrue(ironSmelter0.destroyed());

        /* Wait for the iron smelter to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), ironSmelter0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(ironSmelter0));
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
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(ironSmelter0.getPosition(), ironSmelter0.getFlag().getPosition()));

        map.removeFlag(ironSmelter0.getFlag());

        assertNull(map.getRoad(ironSmelter0.getPosition(), ironSmelter0.getFlag().getPosition()));
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
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(ironSmelter0.getPosition(), ironSmelter0.getFlag().getPosition()));

        ironSmelter0.tearDown();

        assertNull(map.getRoad(ironSmelter0.getPosition(), ironSmelter0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInIronSmelterCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(8, 6);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Connect the iron smelter and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Deliver iron and coal to the iron smelter */
        ironSmelter0.putCargo(new Cargo(COAL, map));
        ironSmelter0.putCargo(new Cargo(IRON, map));

        /* Assign a worker to the iron smelter */
        IronFounder ironFounder = new IronFounder(player0, map);

        Utils.occupyBuilding(ironFounder, ironSmelter0, map);

        assertTrue(ironFounder.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the iron founder to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, ironFounder);

        assertEquals(ironFounder.getCargo().getMaterial(), IRON_BAR);

        /* Wait for the worker to deliver the cargo */
        assertEquals(ironFounder.getTarget(), ironSmelter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, ironSmelter0.getFlag().getPosition());

        /* Stop production and verify that no iron bar is produced */
        ironSmelter0.stopProduction();

        assertFalse(ironSmelter0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(ironFounder.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInIronSmelterCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(8, 6);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Connect the iron smelter and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Assign a worker to the iron smelter */
        IronFounder ironFounder = new IronFounder(player0, map);

        Utils.occupyBuilding(ironFounder, ironSmelter0, map);

        assertTrue(ironFounder.isInsideBuilding());

        /* Deliver iron and coal to the iron smelter */
        ironSmelter0.putCargo(new Cargo(COAL, map));
        ironSmelter0.putCargo(new Cargo(COAL, map));

        ironSmelter0.putCargo(new Cargo(IRON, map));
        ironSmelter0.putCargo(new Cargo(IRON, map));

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the iron founder to produce iron bar */
        Utils.fastForwardUntilWorkerProducesCargo(map, ironFounder);

        assertEquals(ironFounder.getCargo().getMaterial(), IRON_BAR);

        /* Wait for the worker to deliver the cargo */
        assertEquals(ironFounder.getTarget(), ironSmelter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, ironSmelter0.getFlag().getPosition());

        /* Stop production */
        ironSmelter0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(ironFounder.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the iron smelter produces iron bar again */
        ironSmelter0.resumeProduction();

        assertTrue(ironSmelter0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, ironFounder);

        assertNotNull(ironFounder.getCargo());
    }

    @Test
    public void testAssignedIronFounderHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place iron smelter */
        Point point1 = new Point(20, 14);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Connect the iron smelter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironSmelter0.getFlag());

        /* Wait for iron founder to get assigned and leave the headquarter */
        List<IronFounder> workers = Utils.waitForWorkersOutsideBuilding(IronFounder.class, 1, player0, map);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        IronFounder worker = workers.get(0);

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

        /* Place iron smelter close to the new border */
        Point point4 = new Point(28, 18);
        IronSmelter ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point4);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Occupy the iron smelter */
        IronFounder worker = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0, map);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testIronFounderReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

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

        /* Placing iron smelter */
        Point point2 = new Point(14, 4);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, ironSmelter0.getFlag());

        /* Wait for the iron founder to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(IronFounder.class, 1, player0, map);

        IronFounder ironFounder = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof IronFounder) {
                ironFounder = (IronFounder) w;
            }
        }

        assertNotNull(ironFounder);
        assertEquals(ironFounder.getTarget(), ironSmelter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the iron founder has started walking */
        assertFalse(ironFounder.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the iron founder continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, flag0.getPosition());

        assertEquals(ironFounder.getPosition(), flag0.getPosition());

        /* Verify that the iron founder returns to the headquarter when it reaches the flag */
        assertEquals(ironFounder.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, headquarter0.getPosition());
    }

    @Test
    public void testIronFounderContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

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

        /* Placing iron smelter */
        Point point2 = new Point(14, 4);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, ironSmelter0.getFlag());

        /* Wait for the iron founder to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(IronFounder.class, 1, player0, map);

        IronFounder ironFounder = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof IronFounder) {
                ironFounder = (IronFounder) w;
            }
        }

        assertNotNull(ironFounder);
        assertEquals(ironFounder.getTarget(), ironSmelter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the iron founder has started walking */
        assertFalse(ironFounder.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the iron founder continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, flag0.getPosition());

        assertEquals(ironFounder.getPosition(), flag0.getPosition());

        /* Verify that the iron founder continues to the final flag */
        assertEquals(ironFounder.getTarget(), ironSmelter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, ironSmelter0.getFlag().getPosition());

        /* Verify that the iron founder goes out to ironFounder instead of going directly back */
        assertNotEquals(ironFounder.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testIronFounderReturnsToStorageIfIronSmelterIsDestroyed() throws Exception {

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

        /* Placing iron smelter */
        Point point2 = new Point(14, 4);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, ironSmelter0.getFlag());

        /* Wait for the iron founder to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(IronFounder.class, 1, player0, map);

        IronFounder ironFounder = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof IronFounder) {
                ironFounder = (IronFounder) w;
            }
        }

        assertNotNull(ironFounder);
        assertEquals(ironFounder.getTarget(), ironSmelter0.getPosition());

        /* Wait for the iron founder to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, flag0.getPosition());

        map.stepTime();

        /* See that the iron founder has started walking */
        assertFalse(ironFounder.isExactlyAtPoint());

        /* Tear down the iron smelter */
        ironSmelter0.tearDown();

        /* Verify that the iron founder continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, ironSmelter0.getFlag().getPosition());

        assertEquals(ironFounder.getPosition(), ironSmelter0.getFlag().getPosition());

        /* Verify that the iron founder goes back to storage */
        assertEquals(ironFounder.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testIronFounderGoesOffroadBackToClosestStorageWhenIronSmelterIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing iron smelter */
        Point point26 = new Point(17, 17);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0, map);

        /* Place a second storage closer to the iron smelter */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the iron smelter */
        Worker ironFounder = ironSmelter0.getWorker();

        assertTrue(ironFounder.isInsideBuilding());
        assertEquals(ironFounder.getPosition(), ironSmelter0.getPosition());

        ironSmelter0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ironFounder.isInsideBuilding());
        assertEquals(ironFounder.getTarget(), storage0.getPosition());

        int amount = storage0.getAmount(IRON_FOUNDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, storage0.getPosition());

        /* Verify that the ironFounder is stored correctly in the headquarter */
        assertEquals(storage0.getAmount(IRON_FOUNDER), amount + 1);
    }

    @Test
    public void testIronFounderReturnsOffroadAndAvoidsBurningStorageWhenIronSmelterIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing iron smelter */
        Point point26 = new Point(17, 17);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0, map);

        /* Place a second storage closer to the iron smelter */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

        /* Destroy the iron smelter */
        Worker ironFounder = ironSmelter0.getWorker();

        assertTrue(ironFounder.isInsideBuilding());
        assertEquals(ironFounder.getPosition(), ironSmelter0.getPosition());

        ironSmelter0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ironFounder.isInsideBuilding());
        assertEquals(ironFounder.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(IRON_FOUNDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, headquarter0.getPosition());

        /* Verify that the iron founder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(IRON_FOUNDER), amount + 1);
    }

    @Test
    public void testIronFounderReturnsOffroadAndAvoidsDestroyedStorageWhenIronSmelterIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing iron smelter */
        Point point26 = new Point(17, 17);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0, map);

        /* Place a second storage closer to the iron smelter */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storage0, map);

        /* Destroy the iron smelter */
        Worker ironFounder = ironSmelter0.getWorker();

        assertTrue(ironFounder.isInsideBuilding());
        assertEquals(ironFounder.getPosition(), ironSmelter0.getPosition());

        ironSmelter0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ironFounder.isInsideBuilding());
        assertEquals(ironFounder.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(IRON_FOUNDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, headquarter0.getPosition());

        /* Verify that the iron founder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(IRON_FOUNDER), amount + 1);
    }

    @Test
    public void testIronFounderReturnsOffroadAndAvoidsUnfinishedStorageWhenIronSmelterIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing iron smelter */
        Point point26 = new Point(17, 17);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0, map);

        /* Place a second storage closer to the iron smelter */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Destroy the iron smelter */
        Worker ironFounder = ironSmelter0.getWorker();

        assertTrue(ironFounder.isInsideBuilding());
        assertEquals(ironFounder.getPosition(), ironSmelter0.getPosition());

        ironSmelter0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ironFounder.isInsideBuilding());
        assertEquals(ironFounder.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(IRON_FOUNDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, headquarter0.getPosition());

        /* Verify that the iron founder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(IRON_FOUNDER), amount + 1);
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

        /* Place ironSmelter */
        Point point26 = new Point(17, 17);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Place road to connect the headquarter and the iron smelter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironSmelter0.getFlag());

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(IronFounder.class, 1, player0, map).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, ironSmelter0.getFlag().getPosition());

        /* Tear down the building */
        ironSmelter0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquarter instead of entering
        */
        assertEquals(worker.getTarget(), ironSmelter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, ironSmelter0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testIronSmelterWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter, map);

        /* Populate the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter, map);

        assertTrue(ironFounder0.isInsideBuilding());
        assertEquals(ironFounder0.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), ironFounder0);

        /* Verify that the productivity is 0% when the iron smelter doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
            assertNull(ironFounder0.getCargo());
            assertEquals(ironSmelter.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testIronSmelterWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter, map);

        /* Populate the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter, map);

        assertTrue(ironFounder0.isInsideBuilding());
        assertEquals(ironFounder0.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), ironFounder0);

        /* Connect the iron smelter with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironSmelter.getFlag());

        /* Make the iron smelter produce some iron bars with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (ironSmelter.needsMaterial(COAL) && ironSmelter.getAmount(COAL) < 2) {
                ironSmelter.putCargo(new Cargo(COAL, map));
            }

            if (ironSmelter.needsMaterial(IRON) && ironSmelter.getAmount(IRON) < 2) {
                ironSmelter.putCargo(new Cargo(IRON, map));
            }
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(ironSmelter.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (ironSmelter.needsMaterial(COAL) && ironSmelter.getAmount(COAL) < 2) {
                ironSmelter.putCargo(new Cargo(COAL, map));
            }

            if (ironSmelter.needsMaterial(IRON) && ironSmelter.getAmount(IRON) < 2) {
                ironSmelter.putCargo(new Cargo(IRON, map));
            }

            assertEquals(ironSmelter.getProductivity(), 100);
        }
    }

    @Test
    public void testIronSmelterLosesProductivityWhenResourcesRunOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter, map);

        /* Populate the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter, map);

        assertTrue(ironFounder0.isInsideBuilding());
        assertEquals(ironFounder0.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), ironFounder0);

        /* Connect the iron smelter with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironSmelter.getFlag());

        /* Make the iron smelter produce some iron bars with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (ironSmelter.needsMaterial(COAL) && ironSmelter.getAmount(COAL) < 2) {
                ironSmelter.putCargo(new Cargo(COAL, map));
            }

            if (ironSmelter.needsMaterial(IRON) && ironSmelter.getAmount(IRON) < 2) {
                ironSmelter.putCargo(new Cargo(IRON, map));
            }
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(ironSmelter.getProductivity(), 100);

        for (int i = 0; i < 5000; i++) {
            map.stepTime();
        }

        assertEquals(ironSmelter.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedIronSmelterHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter, map);

        /* Verify that the unoccupied iron smelter is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(ironSmelter.getProductivity(), 0);

            if (ironSmelter.needsMaterial(COAL) && ironSmelter.getAmount(COAL) < 2) {
                ironSmelter.putCargo(new Cargo(COAL, map));
            }

            if (ironSmelter.needsMaterial(IRON) && ironSmelter.getAmount(IRON) < 2) {
                ironSmelter.putCargo(new Cargo(IRON, map));
            }

            map.stepTime();
        }
    }

    @Test
    public void testIronSmelterCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(10, 10);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Populate the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0, map);

        /* Verify that the iron smelter can produce */
        assertTrue(ironSmelter0.canProduce());
    }

    @Test
    public void testIronSmelterReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(6, 22);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Construct the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Verify that the reported output is correct */
        assertEquals(ironSmelter0.getProducedMaterial().length, 1);
        assertEquals(ironSmelter0.getProducedMaterial()[0], IRON_BAR);
    }

    @Test
    public void testIronSmelterReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(6, 22);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(ironSmelter0.getMaterialNeeded().size(), 2);
        assertTrue(ironSmelter0.getMaterialNeeded().contains(PLANCK));
        assertTrue(ironSmelter0.getMaterialNeeded().contains(STONE));
        assertEquals(ironSmelter0.getTotalAmountNeeded(PLANCK), 2);
        assertEquals(ironSmelter0.getTotalAmountNeeded(STONE), 2);

        for (Material material : Material.values()) {
            if (material == PLANCK || material == STONE) {
                continue;
            }

            assertEquals(ironSmelter0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testIronSmelterReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(6, 22);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Construct the iron smelter */
        Utils.constructHouse(ironSmelter0, map);

        /* Verify that the reported needed construction material is correct */
        assertEquals(ironSmelter0.getMaterialNeeded().size(), 2);
        assertTrue(ironSmelter0.getMaterialNeeded().contains(COAL));
        assertTrue(ironSmelter0.getMaterialNeeded().contains(IRON));
        assertEquals(ironSmelter0.getTotalAmountNeeded(COAL), 1);
        assertEquals(ironSmelter0.getTotalAmountNeeded(IRON), 1);

        for (Material material : Material.values()) {
            if (material == COAL || material == IRON) {
                continue;
            }

            assertEquals(ironSmelter0.getTotalAmountNeeded(material), 0);
        }
    }
}
