/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Baker;
import org.appland.settlers.model.Bakery;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
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
import static org.appland.settlers.model.Material.BAKER;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WATER;
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
public class TestBakery {

    @Test
    public void testBakeryOnlyNeedsTwoPlanksAndTwoStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing bakery */
        Point point22 = new Point(6, 22);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point22);

        /* Deliver two plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        bakery0.putCargo(plankCargo);
        bakery0.putCargo(plankCargo);
        bakery0.putCargo(stoneCargo);
        bakery0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the bakery */
        for (int i = 0; i < 150; i++) {
            assertTrue(bakery0.underConstruction());

            map.stepTime();
        }

        assertTrue(bakery0.ready());
    }

    @Test
    public void testBakeryCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing bakery */
        Point point22 = new Point(6, 22);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point22);

        /* Deliver one plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        bakery0.putCargo(plankCargo);
        bakery0.putCargo(stoneCargo);
        bakery0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the bakery */
        for (int i = 0; i < 500; i++) {
            assertTrue(bakery0.underConstruction());

            map.stepTime();
        }

        assertFalse(bakery0.ready());
    }

    @Test
    public void testBakeryCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing bakery */
        Point point22 = new Point(6, 22);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point22);

        /* Deliver two planks and one stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        bakery0.putCargo(plankCargo);
        bakery0.putCargo(plankCargo);
        bakery0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the bakery */
        for (int i = 0; i < 500; i++) {
            assertTrue(bakery0.underConstruction());

            map.stepTime();
        }

        assertFalse(bakery0.ready());
    }

    @Test
    public void testBakeryNeedsWorker() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point3);

        /* Connect the bakery with the headquarter */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Unfinished sawmill doesn't need worker */
        assertFalse(bakery.needsWorker());

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        assertTrue(bakery.needsWorker());
    }

    @Test
    public void testHeadquarterHasAtLeastOneBakerAtStart() {
        Headquarter headquarter = new Headquarter(null);

        assertTrue(headquarter.getAmount(BAKER) >= 1);
    }

    @Test
    public void testBakeryGetsAssignedWorker() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point3);

        /* Connect the bakery with the headquarter */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        assertTrue(bakery.needsWorker());

        /* Verify that a bakery worker leaves the headquarter */
        Utils.fastForward(3, map);

        assertEquals(map.getWorkers().size(), 3);

        /* Let the bakery worker reach the bakery */
        Baker baker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Baker) {
                baker = (Baker)worker;
            }
        }

        assertNotNull(baker);
        assertEquals(baker.getTarget(), bakery.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, baker);

        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker);
    }

    @Test
    public void testOccupiedBakeryWithoutIngredientsProducesNothing() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point3 = new Point(7, 9);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point3);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        /* Populate the bakery */
        Worker baker = Utils.occupyBuilding(new Baker(player0, map), bakery);

        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker);

        /* Verify that the bakery doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            assertNull(baker.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedBakeryProducesNothing() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point3 = new Point(7, 9);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point3);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        /* Verify that the bakery doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedBakeryWithIngredientsProducesBread() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point3 = new Point(7, 9);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point3);

        /* Connect the bakery with the headquarter */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        /* Populate the bakery */
        Worker baker = Utils.occupyBuilding(new Baker(player0, map), bakery);

        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker);

        /* Deliver wood to the bakery */
        bakery.putCargo(new Cargo(WATER, map));
        bakery.putCargo(new Cargo(FLOUR, map));

        /* Verify that the bakery produces bread */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            assertNull(baker.getCargo());
        }

        map.stepTime();

        assertNotNull(baker.getCargo());
        assertEquals(baker.getCargo().getMaterial(), BREAD);
        assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testBakerLeavesBreadAtTheFlag() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point3 = new Point(7, 9);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point3);

        /* Connect the bakery with the headquarter */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        /* Populate the bakery */
        Worker baker = Utils.occupyBuilding(new Baker(player0, map), bakery);

        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker);

        /* Deliver ingredients to the bakery */
        bakery.putCargo(new Cargo(WATER, map));
        bakery.putCargo(new Cargo(FLOUR, map));

        /* Verify that the bakery produces bread */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            assertNull(baker.getCargo());
        }

        map.stepTime();

        assertNotNull(baker.getCargo());
        assertEquals(baker.getCargo().getMaterial(), BREAD);
        assertTrue(bakery.getFlag().getStackedCargo().isEmpty());

        /* Verify that the bakery worker leaves the cargo at the flag */
        assertEquals(baker.getTarget(), bakery.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, bakery.getFlag().getPosition());

        assertFalse(bakery.getFlag().getStackedCargo().isEmpty());
        assertNull(baker.getCargo());
        assertEquals(baker.getTarget(), bakery.getPosition());

        /* Verify that the baker goes back to the bakery */
        Utils.fastForwardUntilWorkersReachTarget(map, baker);

        assertTrue(baker.isInsideBuilding());
    }

    @Test
    public void testProductionOfOneBreadConsumesOneWaterAndOneFlour() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point3 = new Point(7, 9);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point3);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        /* Populate the bakery */
        Worker baker = Utils.occupyBuilding(new Baker(player0, map), bakery);

        /* Deliver ingredients to the bakery */
        bakery.putCargo(new Cargo(WATER, map));
        bakery.putCargo(new Cargo(FLOUR, map));

        /* Wait until the bakery worker produces a bread */
        assertEquals(bakery.getAmount(WATER), 1);
        assertEquals(bakery.getAmount(FLOUR), 1);

        Utils.fastForward(150, map);

        assertEquals(bakery.getAmount(FLOUR), 0);
        assertEquals(bakery.getAmount(WATER), 0);
    }

    @Test
    public void testProductionCountdownStartsWhenIngredientsAreAvailable() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point3 = new Point(7, 9);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point3);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        /* Populate the bakery */
        Worker baker = Utils.occupyBuilding(new Baker(player0, map), bakery);

        /* Fast forward so that the bakery worker would have produced bread
           if it had had the ingredients
        */
        Utils.fastForward(150, map);

        assertNull(baker.getCargo());

        /* Deliver ingredients to the bakery */
        bakery.putCargo(new Cargo(WATER, map));
        bakery.putCargo(new Cargo(FLOUR, map));

        /* Verify that it takes 50 steps for the bakery worker to produce the plank */
        for (int i = 0; i < 50; i++) {
            assertNull(baker.getCargo());
            map.stepTime();
        }

        assertNotNull(baker.getCargo());
    }

    @Test
    public void testBakeryWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing bakery */
        Point point26 = new Point(8, 8);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(player0, map), bakery0);

        /* Deliver material to the bakery */
        Cargo ironCargo = new Cargo(FLOUR, map);
        Cargo coalCargo = new Cargo(WATER, map);

        bakery0.putCargo(ironCargo);
        bakery0.putCargo(ironCargo);

        bakery0.putCargo(coalCargo);
        bakery0.putCargo(coalCargo);

        /* Let the baker rest */
        Utils.fastForward(100, map);

        /* Wait for the baker to produce a new bread cargo */
        Utils.fastForward(50, map);

        Worker baker = bakery0.getWorker();

        assertNotNull(baker.getCargo());

        /* Verify that the baker puts the bread cargo at the flag */
        assertEquals(baker.getTarget(), bakery0.getFlag().getPosition());
        assertTrue(bakery0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, bakery0.getFlag().getPosition());

        assertNull(baker.getCargo());
        assertFalse(bakery0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the bakery */
        assertEquals(baker.getTarget(), bakery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, bakery0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(baker.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(baker.getTarget(), bakery0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, bakery0.getFlag().getPosition());

        assertNull(baker.getCargo());
        assertEquals(bakery0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing bakery */
        Point point26 = new Point(8, 8);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Deliver material to the bakery */
        Cargo ironCargo = new Cargo(FLOUR, map);
        Cargo coalCargo = new Cargo(WATER, map);

        bakery0.putCargo(ironCargo);
        bakery0.putCargo(ironCargo);

        bakery0.putCargo(coalCargo);
        bakery0.putCargo(coalCargo);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(player0, map), bakery0);

        /* Let the baker rest */
        Utils.fastForward(100, map);

        /* Wait for the baker to produce a new bread cargo */
        Utils.fastForward(50, map);

        Worker baker = bakery0.getWorker();

        assertNotNull(baker.getCargo());

        /* Verify that the baker puts the bread cargo at the flag */
        assertEquals(baker.getTarget(), bakery0.getFlag().getPosition());
        assertTrue(bakery0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, bakery0.getFlag().getPosition());

        assertNull(baker.getCargo());
        assertFalse(bakery0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = bakery0.getFlag().getStackedCargo().get(0);

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), bakery0.getFlag().getPosition());

        /* Connect the bakery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), bakery0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), bakery0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), bakery0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BREAD);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(BREAD), amount + 1);
    }

    @Test
    public void testBakerGoesBackToStorageWhenBakeryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing bakery */
        Point point26 = new Point(8, 8);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(player0, map), bakery0);

        /* Destroy the bakery */
        Worker baker = bakery0.getWorker();

        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getPosition(), bakery0.getPosition());

        bakery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(baker.isInsideBuilding());
        assertEquals(baker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BAKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, headquarter0.getPosition());

        /* Verify that the baker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(BAKER), amount + 1);
    }

    @Test
    public void testBakerGoesBackOnToStorageOnRoadsIfPossibleWhenBakeryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing bakery */
        Point point26 = new Point(8, 8);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Connect the bakery with the headquarter */
        map.placeAutoSelectedRoad(player0, bakery0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(player0, map), bakery0);

        /* Destroy the bakery */
        Worker baker = bakery0.getWorker();

        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getPosition(), bakery0.getPosition());

        bakery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(baker.isInsideBuilding());
        assertEquals(baker.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point point : baker.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testProductionInBakeryCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(12, 8);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point1);

        /* Connect the bakery and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, bakery0.getFlag(), headquarter.getFlag());

        /* Finish the bakery */
        Utils.constructHouse(bakery0);

        /* Deliver material to the bakery */
        Cargo ironCargo = new Cargo(FLOUR, map);
        Cargo coalCargo = new Cargo(WATER, map);

        bakery0.putCargo(ironCargo);
        bakery0.putCargo(ironCargo);

        bakery0.putCargo(coalCargo);
        bakery0.putCargo(coalCargo);

        /* Assign a worker to the bakery */
        Baker baker = new Baker(player0, map);

        Utils.occupyBuilding(baker, bakery0);

        assertTrue(baker.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the baker to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, baker);

        assertEquals(baker.getCargo().getMaterial(), BREAD);

        /* Wait for the worker to deliver the cargo */
        assertEquals(baker.getTarget(), bakery0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, bakery0.getFlag().getPosition());

        /* Stop production and verify that no bread is produced */
        bakery0.stopProduction();

        assertFalse(bakery0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(baker.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInBakeryCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(12, 8);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point1);

        /* Connect the bakery and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, bakery0.getFlag(), headquarter.getFlag());

        /* Finish the bakery */
        Utils.constructHouse(bakery0);

        /* Deliver material to the bakery */
        Cargo ironCargo = new Cargo(FLOUR, map);
        Cargo coalCargo = new Cargo(WATER, map);

        bakery0.putCargo(ironCargo);
        bakery0.putCargo(ironCargo);

        bakery0.putCargo(coalCargo);
        bakery0.putCargo(coalCargo);

        /* Assign a worker to the bakery */
        Baker baker = new Baker(player0, map);

        Utils.occupyBuilding(baker, bakery0);

        assertTrue(baker.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the baker to produce bread */
        Utils.fastForwardUntilWorkerProducesCargo(map, baker);

        assertEquals(baker.getCargo().getMaterial(), BREAD);

        /* Wait for the worker to deliver the cargo */
        assertEquals(baker.getTarget(), bakery0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, bakery0.getFlag().getPosition());

        /* Stop production */
        bakery0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(baker.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the bakery produces water again */
        bakery0.resumeProduction();

        assertTrue(bakery0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, baker);

        assertNotNull(baker.getCargo());
    }

    @Test
    public void testAssignedBakerHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place bakery */
        Point point1 = new Point(20, 14);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point1);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Connect the bakery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), bakery0.getFlag());

        /* Wait for baker to get assigned and leave the headquarter */
        List<Baker> workers = Utils.waitForWorkersOutsideBuilding(Baker.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Baker worker = workers.get(0);

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
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place bakery close to the new border */
        Point point4 = new Point(28, 18);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point4);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Occupy the bakery */
        Baker worker = Utils.occupyBuilding(new Baker(player0, map), bakery0);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testBakerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

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

        /* Placing bakery */
        Point point2 = new Point(14, 4);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, bakery0.getFlag());

        /* Wait for the baker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Baker.class, 1, player0);

        Baker baker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Baker) {
                baker = (Baker) worker;
            }
        }

        assertNotNull(baker);
        assertEquals(baker.getTarget(), bakery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the baker has started walking */
        assertFalse(baker.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the baker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, baker, flag0.getPosition());

        assertEquals(baker.getPosition(), flag0.getPosition());

        /* Verify that the baker returns to the headquarter when it reaches the flag */
        assertEquals(baker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, headquarter0.getPosition());
    }

    @Test
    public void testBakerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

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

        /* Placing bakery */
        Point point2 = new Point(14, 4);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, bakery0.getFlag());

        /* Wait for the baker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Baker.class, 1, player0);

        Baker baker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Baker) {
                baker = (Baker) worker;
            }
        }

        assertNotNull(baker);
        assertEquals(baker.getTarget(), bakery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the baker has started walking */
        assertFalse(baker.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the baker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, baker, flag0.getPosition());

        assertEquals(baker.getPosition(), flag0.getPosition());

        /* Verify that the baker continues to the final flag */
        assertEquals(baker.getTarget(), bakery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, bakery0.getFlag().getPosition());

        /* Verify that the baker goes out to baker instead of going directly back */
        assertNotEquals(baker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testBakerReturnsToStorageIfBakeryIsDestroyed() throws Exception {

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

        /* Placing bakery */
        Point point2 = new Point(14, 4);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, bakery0.getFlag());

        /* Wait for the baker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Baker.class, 1, player0);

        Baker baker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Baker) {
                baker = (Baker) worker;
            }
        }

        assertNotNull(baker);
        assertEquals(baker.getTarget(), bakery0.getPosition());

        /* Wait for the baker to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, baker, flag0.getPosition());

        map.stepTime();

        /* See that the baker has started walking */
        assertFalse(baker.isExactlyAtPoint());

        /* Tear down the bakery */
        bakery0.tearDown();

        /* Verify that the baker continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, baker, bakery0.getFlag().getPosition());

        assertEquals(baker.getPosition(), bakery0.getFlag().getPosition());

        /* Verify that the baker goes back to storage */
        assertEquals(baker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testBakerGoesOffroadBackToClosestStorageWhenBakeryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing bakery */
        Point point26 = new Point(17, 17);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(player0, map), bakery0);

        /* Place a second storage closer to the bakery */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0);

        /* Destroy the bakery */
        Worker baker = bakery0.getWorker();

        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getPosition(), bakery0.getPosition());

        bakery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(baker.isInsideBuilding());
        assertEquals(baker.getTarget(), storage0.getPosition());

        int amount = storage0.getAmount(BAKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, storage0.getPosition());

        /* Verify that the baker is stored correctly in the headquarter */
        assertEquals(storage0.getAmount(BAKER), amount + 1);
    }

    @Test
    public void testBakerReturnsOffroadAndAvoidsBurningStorageWhenBakeryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing bakery */
        Point point26 = new Point(17, 17);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(player0, map), bakery0);

        /* Place a second storage closer to the bakery */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0);

        /* Destroy the storage */
        storage0.tearDown();

        /* Destroy the bakery */
        Worker baker = bakery0.getWorker();

        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getPosition(), bakery0.getPosition());

        bakery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(baker.isInsideBuilding());
        assertEquals(baker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BAKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, headquarter0.getPosition());

        /* Verify that the baker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(BAKER), amount + 1);
    }

    @Test
    public void testBakerReturnsOffroadAndAvoidsDestroyedStorageWhenBakeryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing bakery */
        Point point26 = new Point(17, 17);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(player0, map), bakery0);

        /* Place a second storage closer to the bakery */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0);

        /* Destroy the storage */
        storage0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storage0);

        /* Destroy the bakery */
        Worker baker = bakery0.getWorker();

        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getPosition(), bakery0.getPosition());

        bakery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(baker.isInsideBuilding());
        assertEquals(baker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BAKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, headquarter0.getPosition());

        /* Verify that the baker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(BAKER), amount + 1);
    }

    @Test
    public void testBakerReturnsOffroadAndAvoidsUnfinishedStorageWhenBakeryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing bakery */
        Point point26 = new Point(17, 17);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(player0, map), bakery0);

        /* Place a second storage closer to the bakery */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Destroy the bakery */
        Worker baker = bakery0.getWorker();

        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getPosition(), bakery0.getPosition());

        bakery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(baker.isInsideBuilding());
        assertEquals(baker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BAKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, headquarter0.getPosition());

        /* Verify that the baker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(BAKER), amount + 1);
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

        /* Place bakery */
        Point point26 = new Point(17, 17);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Place road to connect the headquarter and the bakery */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), bakery0.getFlag());

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Baker.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, bakery0.getFlag().getPosition());

        /* Tear down the building */
        bakery0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquarter instead of entering
        */
        assertEquals(worker.getTarget(), bakery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, bakery0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }
    @Test
    public void testBakeryWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point1);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        /* Populate the bakery */
        Worker armorer0 = Utils.occupyBuilding(new Baker(player0, map), bakery);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), bakery);
        assertEquals(bakery.getWorker(), armorer0);

        /* Verify that the productivity is 0% when the bakery doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
            assertEquals(bakery.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testBakeryWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point1);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        /* Populate the bakery */
        Worker armorer0 = Utils.occupyBuilding(new Baker(player0, map), bakery);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), bakery);
        assertEquals(bakery.getWorker(), armorer0);

        /* Connect the bakery with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), bakery.getFlag());

        /* Make the bakery create some bread with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (bakery.needsMaterial(WATER)) {
                bakery.putCargo(new Cargo(WATER, map));
            }

            if (bakery.needsMaterial(FLOUR)) {
                bakery.putCargo(new Cargo(FLOUR, map));
            }
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(bakery.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (bakery.needsMaterial(WATER)) {
                bakery.putCargo(new Cargo(WATER, map));
            }

            if (bakery.needsMaterial(FLOUR)) {
                bakery.putCargo(new Cargo(FLOUR, map));
            }

            assertEquals(bakery.getProductivity(), 100);
        }
    }

    @Test
    public void testBakeryLosesProductivityWhenResourcesRunOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point1);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        /* Populate the bakery */
        Worker armorer0 = Utils.occupyBuilding(new Baker(player0, map), bakery);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), bakery);
        assertEquals(bakery.getWorker(), armorer0);

        /* Connect the bakery with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), bakery.getFlag());

        /* Make the bakery create some bread with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (bakery.needsMaterial(WATER) && bakery.getAmount(WATER) < 2) {
                bakery.putCargo(new Cargo(WATER, map));
            }

            if (bakery.needsMaterial(FLOUR) && bakery.getAmount(FLOUR) < 2) {
                bakery.putCargo(new Cargo(FLOUR, map));
            }
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(bakery.getProductivity(), 100);

        for (int i = 0; i < 2000; i++) {
            map.stepTime();
        }

        assertEquals(bakery.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedBakeryHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point1);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        /* Verify that the unoccupied bakery is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(bakery.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testBakeryCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point1);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        /* Populate the bakery */
        Worker baker0 = Utils.occupyBuilding(new Baker(player0, map), bakery);

        /* Verify that the bakery can produce */
        assertTrue(bakery.canProduce());
    }

    @Test
    public void testBakeryReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(6, 22);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point1);

        /* Construct the bakery */
        Utils.constructHouse(bakery0);

        /* Verify that the reported output is correct */
        assertEquals(bakery0.getProducedMaterial().length, 1);
        assertEquals(bakery0.getProducedMaterial()[0], BREAD);
    }

    @Test
    public void testBakeryReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(6, 22);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(bakery0.getMaterialNeeded().size(), 2);
        assertTrue(bakery0.getMaterialNeeded().contains(PLANK));
        assertTrue(bakery0.getMaterialNeeded().contains(STONE));
        assertEquals(bakery0.getTotalAmountNeeded(PLANK), 2);
        assertEquals(bakery0.getTotalAmountNeeded(STONE), 2);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(bakery0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testBakeryReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(6, 22);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point1);

        /* Construct the bakery */
        Utils.constructHouse(bakery0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(bakery0.getMaterialNeeded().size(), 2);
        assertTrue(bakery0.getMaterialNeeded().contains(WATER));
        assertTrue(bakery0.getMaterialNeeded().contains(FLOUR));
        assertEquals(bakery0.getTotalAmountNeeded(WATER), 1);
        assertEquals(bakery0.getTotalAmountNeeded(FLOUR), 1);

        for (Material material : Material.values()) {
            if (material == WATER || material == FLOUR) {
                continue;
            }

            assertEquals(bakery0.getTotalAmountNeeded(material), 0);
        }
    }
}
