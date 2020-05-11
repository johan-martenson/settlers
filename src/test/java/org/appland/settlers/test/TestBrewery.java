/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Brewer;
import org.appland.settlers.model.Brewery;
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
import org.appland.settlers.model.Storehouse;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.BREWER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
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
public class TestBrewery {

    @Test
    public void testBreweryOnlyNeedsTwoPlanksAndTwoStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing brewery */
        Point point22 = new Point(6, 22);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point22);

        /* Deliver two plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        brewery0.putCargo(plankCargo);
        brewery0.putCargo(plankCargo);
        brewery0.putCargo(stoneCargo);
        brewery0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the brewery */
        for (int i = 0; i < 150; i++) {
            assertTrue(brewery0.underConstruction());

            map.stepTime();
        }

        assertTrue(brewery0.isReady());
    }

    @Test
    public void testBreweryCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing brewery */
        Point point22 = new Point(6, 22);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point22);

        /* Deliver one plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        brewery0.putCargo(plankCargo);
        brewery0.putCargo(stoneCargo);
        brewery0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the brewery */
        for (int i = 0; i < 500; i++) {
            assertTrue(brewery0.underConstruction());

            map.stepTime();
        }

        assertFalse(brewery0.isReady());
    }

    @Test
    public void testBreweryCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing brewery */
        Point point22 = new Point(6, 22);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point22);

        /* Deliver two planks and one stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        brewery0.putCargo(plankCargo);
        brewery0.putCargo(plankCargo);
        brewery0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the brewery */
        for (int i = 0; i < 500; i++) {
            assertTrue(brewery0.underConstruction());

            map.stepTime();
        }

        assertFalse(brewery0.isReady());
    }

    @Test
    public void testBreweryNeedsWorker() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point3);

        /* Unfinished sawmill doesn't need worker */
        assertFalse(brewery.needsWorker());

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        assertTrue(brewery.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneBrewerAtStart() {
        Headquarter headquarter = new Headquarter(null);

        assertEquals(headquarter.getAmount(BREWER), 1);
    }

    @Test
    public void testBreweryGetsAssignedWorker() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point3);

        /* Place a road between the headquarter and the brewery */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        assertTrue(brewery.needsWorker());

        /* Verify that a brewery worker leaves the headquarter */
        assertEquals(map.getWorkers().size(), 1);

        Utils.fastForward(3, map);

        assertEquals(map.getWorkers().size(), 3);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Brewer.class);

        /* Let the brewery worker reach the brewery */
        Brewer brewer0 = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Brewer) {
                brewer0 = (Brewer)worker;
            }
        }

        assertNotNull(brewer0);
        assertEquals(brewer0.getTarget(), brewery.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, brewer0);

        assertTrue(brewer0.isInsideBuilding());
        assertEquals(brewer0.getHome(), brewery);
        assertEquals(brewery.getWorker(), brewer0);
    }

    @Test
    public void testOccupiedBreweryWithoutWheatAndWaterProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point3);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Occupy the brewery */
        Worker brewer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        assertTrue(brewer0.isInsideBuilding());
        assertEquals(brewer0.getHome(), brewery);
        assertEquals(brewery.getWorker(), brewer0);

        /* Verify that the brewery doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(brewery.getFlag().getStackedCargo().isEmpty());
            assertNull(brewer0.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedBreweryProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point3);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Verify that the brewery doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(brewery.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedBreweryWithWaterAndWheatProducesBeer() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point3);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Occupy the brewery */
        Worker brewer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        assertTrue(brewer0.isInsideBuilding());
        assertEquals(brewer0.getHome(), brewery);
        assertEquals(brewery.getWorker(), brewer0);

        /* Deliver wheat and water to the brewery */
        brewery.putCargo(new Cargo(WHEAT, map));
        brewery.putCargo(new Cargo(WATER, map));

        /* Verify that the brewery produces beer */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(brewery.getFlag().getStackedCargo().isEmpty());
            assertNull(brewer0.getCargo());
        }

        map.stepTime();

        assertNotNull(brewer0.getCargo());
        assertEquals(brewer0.getCargo().getMaterial(), BEER);
        assertTrue(brewery.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testBrewerLeavesBeerAtTheFlag() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point3);

        /* Place a road between the headquarter and the brewery */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Occupy the brewery */
        Worker brewer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        assertTrue(brewer0.isInsideBuilding());
        assertEquals(brewer0.getHome(), brewery);
        assertEquals(brewery.getWorker(), brewer0);

        /* Deliver wheat and water to the brewery */
        brewery.putCargo(new Cargo(WHEAT, map));
        brewery.putCargo(new Cargo(WATER, map));

        /* Verify that the brewery produces beer */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(brewery.getFlag().getStackedCargo().isEmpty());
            assertNull(brewer0.getCargo());
        }

        map.stepTime();

        assertNotNull(brewer0.getCargo());
        assertEquals(brewer0.getCargo().getMaterial(), BEER);
        assertTrue(brewery.getFlag().getStackedCargo().isEmpty());

        /* Verify that the brewery worker leaves the cargo at the flag */
        assertEquals(brewer0.getTarget(), brewery.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, brewer0, brewery.getFlag().getPosition());

        assertFalse(brewery.getFlag().getStackedCargo().isEmpty());
        assertNull(brewer0.getCargo());
        assertEquals(brewer0.getTarget(), brewery.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, brewer0);

        assertTrue(brewer0.isInsideBuilding());
    }

    @Test
    public void testProductionOfOneBeerConsumesOneWheatAndOneWater() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point3);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Occupy the brewery */
        Worker brewer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        /* Deliver wheat and water to the brewery */
        brewery.putCargo(new Cargo(WHEAT, map));
        brewery.putCargo(new Cargo(WATER, map));

        /* Wait until the brewery worker produces an wheat bar */
        assertEquals(brewery.getAmount(WHEAT), 1);
        assertEquals(brewery.getAmount(WATER), 1);

        Utils.fastForward(150, map);

        assertEquals(brewery.getAmount(WHEAT), 0);
        assertEquals(brewery.getAmount(WATER), 0);
        assertTrue(brewery.needsMaterial(WHEAT));
        assertTrue(brewery.needsMaterial(WATER));
    }

    @Test
    public void testProductionCountdownStartsWhenWheatAndWaterAreAvailable() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point3);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Occupy the brewery */
        Worker brewer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        /* Fast forward so that the brewer would produced beer
           if it had had wheat and water
        */
        Utils.fastForward(150, map);

        assertNull(brewer0.getCargo());

        /* Deliver wheat and water to the brewery */
        brewery.putCargo(new Cargo(WHEAT, map));
        brewery.putCargo(new Cargo(WATER, map));

        /* Verify that it takes 50 steps for the brewery worker to produce the wheat bar */
        for (int i = 0; i < 50; i++) {
            assertNull(brewer0.getCargo());
            map.stepTime();
        }

        assertNotNull(brewer0.getCargo());
    }

    @Test
    public void testBreweryCannotProduceWithOnlyWheat() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point3);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Occupy the brewery */
        Worker brewer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        /* Deliver wheat but not water to the brewery */
        brewery.putCargo(new Cargo(WHEAT, map));

        /* Verify that the wheat founder doesn't produce beer since it doesn't have any water */
        for (int i = 0; i < 200; i++) {
            assertNull(brewer0.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testBreweryCannotProduceWithOnlyWater() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point3);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Occupy the brewery */
        Worker brewer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        /* Deliver wheat but not water to the brewery */
        brewery.putCargo(new Cargo(WATER, map));

        /* Verify that the wheat founder doesn't produce beer since it doesn't have any water */
        for (int i = 0; i < 200; i++) {
            assertNull(brewer0.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testBreweryWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing brewery */
        Point point26 = new Point(8, 8);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point26);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery0);

        /* Occupy the brewery */
        Utils.occupyBuilding(new Brewer(player0, map), brewery0);

        /* Deliver material to the brewery */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);

        brewery0.putCargo(wheatCargo);
        brewery0.putCargo(wheatCargo);

        brewery0.putCargo(waterCargo);
        brewery0.putCargo(waterCargo);

        /* Let the brewer rest */
        Utils.fastForward(100, map);

        /* Wait for the brewer to produce a new bread cargo */
        Utils.fastForward(50, map);

        Worker worker = brewery0.getWorker();

        assertNotNull(worker.getCargo());

        /* Verify that the brewer puts the bread cargo at the flag */
        assertEquals(worker.getTarget(), brewery0.getFlag().getPosition());
        assertTrue(brewery0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, brewery0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(brewery0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the brewery */
        assertEquals(worker.getTarget(), brewery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, brewery0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(worker.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(worker.getTarget(), brewery0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, brewery0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertEquals(brewery0.getFlag().getStackedCargo().size(), 2);
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

        /* Placing brewery */
        Point point26 = new Point(8, 8);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point26);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery0);

        /* Deliver material to the brewery */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);

        brewery0.putCargo(wheatCargo);
        brewery0.putCargo(wheatCargo);

        brewery0.putCargo(waterCargo);
        brewery0.putCargo(waterCargo);

        /* Occupy the brewery */
        Utils.occupyBuilding(new Brewer(player0, map), brewery0);

        /* Let the brewer rest */
        Utils.fastForward(100, map);

        /* Wait for the brewer to produce a new bread cargo */
        Utils.fastForward(50, map);

        Worker worker = brewery0.getWorker();

        assertNotNull(worker.getCargo());

        /* Verify that the brewer puts the bread cargo at the flag */
        assertEquals(worker.getTarget(), brewery0.getFlag().getPosition());
        assertTrue(brewery0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, brewery0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(brewery0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = brewery0.getFlag().getStackedCargo().get(0);

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), brewery0.getFlag().getPosition());

        /* Connect the brewery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), brewery0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), brewery0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));


        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), brewery0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BEER);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(BEER), amount + 1);
    }

    @Test
    public void testBrewerGoesBackToStorageWhenBreweryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing brewery */
        Point point26 = new Point(8, 8);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point26);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery0);

        /* Occupy the brewery */
        Utils.occupyBuilding(new Brewer(player0, map), brewery0);

        /* Destroy the brewery */
        Worker worker = brewery0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), brewery0.getPosition());

        brewery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BREWER);

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

        /* Verify that the brewer is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(BREWER), amount + 1);
    }

    @Test
    public void testBrewerGoesBackOnToStorageOnRoadsIfPossibleWhenBreweryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing brewery */
        Point point26 = new Point(8, 8);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point26);

        /* Connect the brewery with the headquarter */
        map.placeAutoSelectedRoad(player0, brewery0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery0);

        /* Occupy the brewery */
        Utils.occupyBuilding(new Brewer(player0, map), brewery0);

        /* Destroy the brewery */
        Worker worker = brewery0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), brewery0.getPosition());

        brewery0.tearDown();

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
    public void testProductionInBreweryCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(12, 8);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point1);

        /* Connect the brewery and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, brewery0.getFlag(), headquarter.getFlag());

        /* Finish the brewery */
        Utils.constructHouse(brewery0);

        /* Deliver material to the brewery */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);

        brewery0.putCargo(wheatCargo);
        brewery0.putCargo(wheatCargo);

        brewery0.putCargo(waterCargo);
        brewery0.putCargo(waterCargo);

        /* Assign a worker to the brewery */
        Brewer worker = new Brewer(player0, map);

        Utils.occupyBuilding(worker, brewery0);

        assertTrue(worker.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the brewer to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), BEER);

        /* Wait for the worker to deliver the cargo */
        assertEquals(worker.getTarget(), brewery0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, brewery0.getFlag().getPosition());

        /* Stop production and verify that no beer is produced */
        brewery0.stopProduction();

        assertFalse(brewery0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInBreweryCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(12, 8);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point1);

        /* Connect the brewery and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, brewery0.getFlag(), headquarter.getFlag());

        /* Finish the brewery */
        Utils.constructHouse(brewery0);

        /* Assign a worker to the brewery */
        Brewer worker = new Brewer(player0, map);

        Utils.occupyBuilding(worker, brewery0);

        assertTrue(worker.isInsideBuilding());

        /* Deliver material to the brewery */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);

        brewery0.putCargo(wheatCargo);
        brewery0.putCargo(wheatCargo);

        brewery0.putCargo(waterCargo);
        brewery0.putCargo(waterCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the brewer to produce beer */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), BEER);

        /* Wait for the worker to deliver the cargo */
        assertEquals(worker.getTarget(), brewery0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, brewery0.getFlag().getPosition());

        /* Stop production */
        brewery0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the brewery produces water again */
        brewery0.resumeProduction();

        assertTrue(brewery0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertNotNull(worker.getCargo());
    }

    @Test
    public void testAssignedBrewerHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place brewery */
        Point point1 = new Point(20, 14);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point1);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery0);

        /* Connect the brewery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), brewery0.getFlag());

        /* Wait for brewer to get assigned and leave the headquarter */
        List<Brewer> workers = Utils.waitForWorkersOutsideBuilding(Brewer.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Brewer worker = workers.get(0);

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

        /* Place brewery close to the new border */
        Point point4 = new Point(28, 18);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point4);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery0);

        /* Occupy the brewery */
        Brewer worker = Utils.occupyBuilding(new Brewer(player0, map), brewery0);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testBrewerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

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

        /* Placing brewer */
        Point point2 = new Point(14, 4);
        Building brewery = map.placeBuilding(new Brewery(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, brewery.getFlag());

        /* Wait for the brewer to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Brewer.class, 1, player0);

        Brewer brewer = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Brewer) {
                brewer = (Brewer) worker;
            }
        }

        assertNotNull(brewer);
        assertEquals(brewer.getTarget(), brewery.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, brewer, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the brewer has started walking */
        assertFalse(brewer.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the brewer continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, brewer, flag0.getPosition());

        assertEquals(brewer.getPosition(), flag0.getPosition());

        /* Verify that the brewer returns to the headquarter when it reaches the flag */
        assertEquals(brewer.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, brewer, headquarter0.getPosition());
    }

    @Test
    public void testBrewerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

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

        /* Placing brewer */
        Point point2 = new Point(14, 4);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point2.upLeft());


        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, brewery0.getFlag());

        /* Wait for the brewer to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Brewer.class, 1, player0);

        Brewer brewer = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Brewer) {
                brewer = (Brewer) worker;
            }
        }

        assertNotNull(brewer);
        assertEquals(brewer.getTarget(), brewery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, brewer, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the brewer has started walking */
        assertFalse(brewer.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the brewer continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, brewer, flag0.getPosition());

        assertEquals(brewer.getPosition(), flag0.getPosition());

        /* Verify that the brewer continues to the final flag */
        assertEquals(brewer.getTarget(), brewery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, brewer, brewery0.getFlag().getPosition());

        /* Verify that the brewer goes out to brewer instead of going directly back */
        assertNotEquals(brewer.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testBrewerReturnsToStorageIfBreweryIsDestroyed() throws Exception {

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

        /* Placing brewery */
        Point point2 = new Point(14, 4);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, brewery0.getFlag());

        /* Wait for the brewer to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Brewer.class, 1, player0);

        Brewer brewer = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Brewer) {
                brewer = (Brewer) worker;
            }
        }

        assertNotNull(brewer);
        assertEquals(brewer.getTarget(), brewery0.getPosition());

        /* Wait for the brewer to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, brewer, flag0.getPosition());

        map.stepTime();

        /* See that the brewer has started walking */
        assertFalse(brewer.isExactlyAtPoint());

        /* Tear down the brewery */
        brewery0.tearDown();

        /* Verify that the brewer continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, brewer, brewery0.getFlag().getPosition());

        assertEquals(brewer.getPosition(), brewery0.getFlag().getPosition());

        /* Verify that the brewer goes back to storage */
        assertEquals(brewer.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testBrewerGoesOffroadBackToClosestStorageWhenBreweryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing brewery */
        Point point26 = new Point(17, 17);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point26);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery0);

        /* Occupy the brewery */
        Utils.occupyBuilding(new Brewer(player0, map), brewery0);

        /* Place a second storage closer to the brewery */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the brewery */
        Worker brewer = brewery0.getWorker();

        assertTrue(brewer.isInsideBuilding());
        assertEquals(brewer.getPosition(), brewery0.getPosition());

        brewery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(brewer.isInsideBuilding());
        assertEquals(brewer.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(BREWER);

        Utils.fastForwardUntilWorkerReachesPoint(map, brewer, storehouse0.getPosition());

        /* Verify that the brewer is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(BREWER), amount + 1);
    }

    @Test
    public void testBrewerReturnsOffroadAndAvoidsBurningStorageWhenBreweryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing brewery */
        Point point26 = new Point(17, 17);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point26);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery0);

        /* Occupy the brewery */
        Utils.occupyBuilding(new Brewer(player0, map), brewery0);

        /* Place a second storage closer to the brewery */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Destroy the brewery */
        Worker brewer = brewery0.getWorker();

        assertTrue(brewer.isInsideBuilding());
        assertEquals(brewer.getPosition(), brewery0.getPosition());

        brewery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(brewer.isInsideBuilding());
        assertEquals(brewer.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BREWER);

        Utils.fastForwardUntilWorkerReachesPoint(map, brewer, headquarter0.getPosition());

        /* Verify that the brewer is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(BREWER), amount + 1);
    }

    @Test
    public void testBrewerReturnsOffroadAndAvoidsDestroyedStorageWhenBreweryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing brewery */
        Point point26 = new Point(17, 17);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point26);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery0);

        /* Occupy the brewery */
        Utils.occupyBuilding(new Brewer(player0, map), brewery0);

        /* Place a second storage closer to the brewery */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

        /* Destroy the brewery */
        Worker brewer = brewery0.getWorker();

        assertTrue(brewer.isInsideBuilding());
        assertEquals(brewer.getPosition(), brewery0.getPosition());

        brewery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(brewer.isInsideBuilding());
        assertEquals(brewer.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BREWER);

        Utils.fastForwardUntilWorkerReachesPoint(map, brewer, headquarter0.getPosition());

        /* Verify that the brewer is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(BREWER), amount + 1);
    }

    @Test
    public void testBrewerReturnsOffroadAndAvoidsUnfinishedStorageWhenBreweryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing brewery */
        Point point26 = new Point(17, 17);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point26);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery0);

        /* Occupy the brewery */
        Utils.occupyBuilding(new Brewer(player0, map), brewery0);

        /* Place a second storage closer to the brewery */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the brewery */
        Worker brewer = brewery0.getWorker();

        assertTrue(brewer.isInsideBuilding());
        assertEquals(brewer.getPosition(), brewery0.getPosition());

        brewery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(brewer.isInsideBuilding());
        assertEquals(brewer.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BREWER);

        Utils.fastForwardUntilWorkerReachesPoint(map, brewer, headquarter0.getPosition());

        /* Verify that the brewer is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(BREWER), amount + 1);
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

        /* Place brewery */
        Point point26 = new Point(17, 17);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point26);

        /* Place road to connect the headquarter and the brewery */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), brewery0.getFlag());

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Brewer.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, brewery0.getFlag().getPosition());

        /* Tear down the building */
        brewery0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquarter instead of entering
        */
        assertEquals(worker.getTarget(), brewery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, brewery0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }
    @Test
    public void testBreweryWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point1);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Populate the brewery */
        Worker armorer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), brewery);
        assertEquals(brewery.getWorker(), armorer0);

        /* Verify that the productivity is 0% when the brewery doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(brewery.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
            assertEquals(brewery.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testBreweryWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point1);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Populate the brewery */
        Worker armorer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), brewery);
        assertEquals(brewery.getWorker(), armorer0);

        /* Connect the brewery with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), brewery.getFlag());

        /* Make the brewery create some bread with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (brewery.needsMaterial(WATER)) {
                brewery.putCargo(new Cargo(WATER, map));
            }

            if (brewery.needsMaterial(WHEAT)) {
                brewery.putCargo(new Cargo(WHEAT, map));
            }
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(brewery.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (brewery.needsMaterial(WATER)) {
                brewery.putCargo(new Cargo(WATER, map));
            }

            if (brewery.needsMaterial(WHEAT)) {
                brewery.putCargo(new Cargo(WHEAT, map));
            }

            assertEquals(brewery.getProductivity(), 100);
        }
    }

    @Test
    public void testBreweryLosesProductivityWhenResourcesRunOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point1);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Populate the brewery */
        Worker armorer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), brewery);
        assertEquals(brewery.getWorker(), armorer0);

        /* Connect the brewery with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), brewery.getFlag());

        /* Make the brewery create some bread with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (brewery.needsMaterial(WATER) && brewery.getAmount(WATER) < 2) {
                brewery.putCargo(new Cargo(WATER, map));
            }

            if (brewery.needsMaterial(WHEAT) && brewery.getAmount(WHEAT) < 2) {
                brewery.putCargo(new Cargo(WHEAT, map));
            }
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(brewery.getProductivity(), 100);

        for (int i = 0; i < 2000; i++) {
            map.stepTime();
        }

        assertEquals(brewery.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedBreweryHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point1);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Verify that the unoccupied brewery is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(brewery.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testBreweryCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point1);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Populate the brewery */
        Worker brewer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        /* Verify that the brewery can produce */
        assertTrue(brewery.canProduce());
    }

    @Test
    public void testBreweryReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(6, 22);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point1);

        /* Construct the brewery */
        Utils.constructHouse(brewery0);

        /* Verify that the reported output is correct */
        assertEquals(brewery0.getProducedMaterial().length, 1);
        assertEquals(brewery0.getProducedMaterial()[0], BEER);
    }

    @Test
    public void testBreweryReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(6, 22);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(brewery0.getMaterialNeeded().size(), 2);
        assertTrue(brewery0.getMaterialNeeded().contains(PLANK));
        assertTrue(brewery0.getMaterialNeeded().contains(STONE));
        assertEquals(brewery0.getTotalAmountNeeded(PLANK), 2);
        assertEquals(brewery0.getTotalAmountNeeded(STONE), 2);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(brewery0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testBreweryReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(6, 22);
        Building brewery0 = map.placeBuilding(new Brewery(player0), point1);

        /* Construct the brewery */
        Utils.constructHouse(brewery0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(brewery0.getMaterialNeeded().size(), 2);
        assertTrue(brewery0.getMaterialNeeded().contains(WATER));
        assertTrue(brewery0.getMaterialNeeded().contains(WHEAT));
        assertEquals(brewery0.getTotalAmountNeeded(WATER), 1);
        assertEquals(brewery0.getTotalAmountNeeded(WHEAT), 1);

        for (Material material : Material.values()) {
            if (material == WATER || material == WHEAT) {
                continue;
            }

            assertEquals(brewery0.getTotalAmountNeeded(material), 0);
        }
    }
}
