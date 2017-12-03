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
import org.appland.settlers.model.PigBreeder;
import org.appland.settlers.model.PigFarm;
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
import static org.appland.settlers.model.Material.PIG;
import static org.appland.settlers.model.Material.PIG_BREEDER;
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
public class TestPigFarm {

    @Test
    public void testPigFarmOnlyNeedsThreePlanksAndThreeStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing pig farm */
        Point point22 = new Point(6, 22);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point22);

        /* Deliver three plank and three stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        pigFarm0.putCargo(plankCargo);
        pigFarm0.putCargo(plankCargo);
        pigFarm0.putCargo(plankCargo);
        pigFarm0.putCargo(stoneCargo);
        pigFarm0.putCargo(stoneCargo);
        pigFarm0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the pig farm */
        for (int i = 0; i < 200; i++) {
            assertTrue(pigFarm0.underConstruction());

            map.stepTime();
        }

        assertTrue(pigFarm0.ready());
    }

    @Test
    public void testPigFarmCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing pig farm */
        Point point22 = new Point(6, 22);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point22);

        /* Deliver two plank and three stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        pigFarm0.putCargo(plankCargo);
        pigFarm0.putCargo(plankCargo);
        pigFarm0.putCargo(stoneCargo);
        pigFarm0.putCargo(stoneCargo);
        pigFarm0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the pig farm */
        for (int i = 0; i < 500; i++) {
            assertTrue(pigFarm0.underConstruction());

            map.stepTime();
        }

        assertFalse(pigFarm0.ready());
    }

    @Test
    public void testPigFarmCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing pig farm */
        Point point22 = new Point(6, 22);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point22);

        /* Deliver three planks and two stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        pigFarm0.putCargo(plankCargo);
        pigFarm0.putCargo(plankCargo);
        pigFarm0.putCargo(plankCargo);
        pigFarm0.putCargo(stoneCargo);
        pigFarm0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the pig farm */
        for (int i = 0; i < 500; i++) {
            assertTrue(pigFarm0.underConstruction());

            map.stepTime();
        }

        assertFalse(pigFarm0.ready());
    }

    @Test
    public void testUnfinishedPigFarmNeedsNoPigBreeder() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place pig farm */
        Point point0 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(player0), point0);

        assertTrue(farm.underConstruction());
        assertFalse(farm.needsWorker());
    }

    @Test
    public void testFinishedPigFarmNeedsPigBreeder() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place pig farm */
        Point point0 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(player0), point0);

        Utils.constructHouse(farm, map);

        assertTrue(farm.ready());
        assertTrue(farm.needsWorker());
    }

    @Test
    public void testPigBreederIsAssignedToFinishedPigFarm() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish the pig farm */
        Utils.constructHouse(farm, map);

        /* Run game logic twice, once to place courier and once to place pig breeder */
        Utils.fastForward(2, map);

        /* Verify that there was a pig breeder added */
        assertEquals(map.getWorkers().size(), 3);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), PigBreeder.class);
    }

    @Test
    public void testPigBreederRestsInPigFarmThenLeaves() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(pigFarm, map);

        PigBreeder pigBreeder = new PigBreeder(player0, map);

        Utils.occupyBuilding(pigBreeder, pigFarm, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm.putCargo(waterCargo);
        pigFarm.putCargo(wheatCargo);

        /* Run the game logic 99 times and make sure the pig breeder stays in the pig farm */
        for (int i = 0; i < 99; i++) {
            assertTrue(pigBreeder.isInsideBuilding());
            map.stepTime();
        }

        assertTrue(pigBreeder.isInsideBuilding());

        /* Step once and make sure the pig breeder goes out of the pig farm */
        map.stepTime();

        assertFalse(pigBreeder.isInsideBuilding());
    }

    @Test
    public void testPigBreederFeedsThePigsWhenItHasResources() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(pigFarm, map);

        /* Deliver wheat and pig to the farm */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);

        pigFarm.putCargo(wheatCargo);
        pigFarm.putCargo(waterCargo);

        /* Occupy the pig farm with a pig breeder */
        PigBreeder pigBreeder = new PigBreeder(player0, map);

        Utils.occupyBuilding(pigBreeder, pigFarm, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Let the pigBreeder rest */
        Utils.fastForward(99, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Step once and make sure the pigBreeder goes out of the farm */
        map.stepTime();

        assertFalse(pigBreeder.isInsideBuilding());

        Point point = pigBreeder.getTarget();

        assertTrue(pigBreeder.isTraveling());

        /* Let the pigBreeder reach the spot and start to feed the pigs */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isAt(point));
        assertTrue(pigBreeder.isFeeding());

        for (int i = 0; i < 19; i++) {
            assertTrue(pigBreeder.isFeeding());
            map.stepTime();
        }

        assertTrue(pigBreeder.isFeeding());
        assertFalse(map.isCropAtPoint(point));

        map.stepTime();

        /* Verify that the pigBreeder stopped feeding */
        assertFalse(pigBreeder.isFeeding());
        assertNull(pigBreeder.getCargo());
    }

    @Test
    public void testPigBreederReturnsAfterFeeding() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(pigFarm, map);

        /* Assign a pigBreeder to the farm */
        PigBreeder pigBreeder = new PigBreeder(player0, map);

        Utils.occupyBuilding(pigBreeder, pigFarm, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm.putCargo(waterCargo);
        pigFarm.putCargo(wheatCargo);

        /* Wait for the pigBreeder to rest */
        Utils.fastForward(99, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Step once to let the pigBreeder go out to plant */
        map.stepTime();

        assertFalse(pigBreeder.isInsideBuilding());

        Point point = pigBreeder.getTarget();

        assertTrue(pigBreeder.isTraveling());

        /* Let the pigBreeder reach the intended spot and start to feed */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isAt(point));
        assertTrue(pigBreeder.isFeeding());

        /* Wait for the pigBreeder to feed */
        Utils.fastForward(19, map);

        assertTrue(pigBreeder.isFeeding());

        map.stepTime();

        /* Verify that the pigBreeder stopped feeding and is walking back to the farm */
        assertFalse(pigBreeder.isFeeding());
        assertTrue(pigBreeder.isTraveling());
        assertEquals(pigBreeder.getTarget(), pigFarm.getPosition());
        assertTrue(pigBreeder.getPlannedPath().contains(pigFarm.getFlag().getPosition()));

        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isInsideBuilding());
    }

    @Test
    public void testPigBreederDeliversPigToFlag() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(pigFarm, map);

        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm.putCargo(waterCargo);
        pigFarm.putCargo(wheatCargo);

        /* Assign a pigBreeder to the farm */
        PigBreeder pigBreeder = new PigBreeder(player0, map);

        Utils.occupyBuilding(pigBreeder, pigFarm, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Let the pigBreeder rest */
        Utils.fastForward(99, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Step once and to let the pigBreeder go out to feed */
        map.stepTime();

        assertFalse(pigBreeder.isInsideBuilding());

        Point point = pigBreeder.getTarget();

        assertTrue(pigBreeder.isTraveling());

        /* Let the pigBreeder reach the intended spot */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isAt(point));
        assertTrue(pigBreeder.isFeeding());

        /* Wait for the pigBreeder to feed the pigs */
        Utils.fastForward(19, map);

        assertTrue(pigBreeder.isFeeding());

        map.stepTime();

        /* PigBreeder is walking back to farm without carrying a cargo */
        assertFalse(pigBreeder.isFeeding());
        assertEquals(pigBreeder.getTarget(), pigFarm.getPosition());
        assertNull(pigBreeder.getCargo());

        /* Let the pigBreeder reach the farm */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isInsideBuilding());

        /* Wait for the pig breeder to prepare the pig */
        for (int i = 0; i < 20; i++) {
            assertNull(pigBreeder.getCargo());
            map.stepTime();
        }

        /* PigBreeder leaves the building to place the cargo at the flag */
        map.stepTime();

        assertFalse(pigBreeder.isInsideBuilding());
        assertTrue(pigFarm.getFlag().getStackedCargo().isEmpty());
        assertNotNull(pigBreeder.getCargo());
        assertEquals(pigBreeder.getCargo().getMaterial(), PIG);
        assertEquals(pigBreeder.getTarget(), pigFarm.getFlag().getPosition());

        /* Let the pigBreeder reach the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, pigFarm.getFlag().getPosition());

        assertFalse(pigFarm.getFlag().getStackedCargo().isEmpty());
        assertNull(pigBreeder.getCargo());

        /* The pigBreeder goes back to the building */
        assertEquals(pigBreeder.getTarget(), pigFarm.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isInsideBuilding());
    }

    @Test
    public void testPigFarmWithoutPigBreederProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(farm, map);

        /* Verify that the farm does not produce any wheat */
        for (int i = 0; i < 200; i++) {
            assertTrue(farm.getFlag().getStackedCargo().isEmpty());

            map.stepTime();
        }

        assertTrue(farm.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testPigFarmWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing pig farm */
        Point point26 = new Point(8, 8);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0, map);

        /* Deliver material to the pig farm */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);

        pigFarm0.putCargo(wheatCargo);
        pigFarm0.putCargo(wheatCargo);

        pigFarm0.putCargo(waterCargo);
        pigFarm0.putCargo(waterCargo);

        /* Let the pig breeder rest */
        Utils.fastForward(100, map);

        /* Wait for the pig breeder to produce a new meat cargo */
        Worker worker = pigFarm0.getWorker();

        for (int i = 0; i < 1000; i++) {
            if (worker.getCargo() != null && worker.getPosition().equals(pigFarm0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(worker.getCargo());

        /* Verify that the pig breeder puts the meat cargo at the flag */
        assertEquals(worker.getTarget(), pigFarm0.getFlag().getPosition());
        assertTrue(pigFarm0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(pigFarm0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the pig farm */
        assertEquals(worker.getTarget(), pigFarm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        for (int i = 0; i < 1000; i++) {
            if (worker.getCargo() != null && worker.getPosition().equals(pigFarm0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(worker.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(worker.getTarget(), pigFarm0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertEquals(pigFarm0.getFlag().getStackedCargo().size(), 2);
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

        /* Placing pig farm */
        Point point26 = new Point(8, 8);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Deliver material to the pig farm */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);

        pigFarm0.putCargo(wheatCargo);
        pigFarm0.putCargo(wheatCargo);

        pigFarm0.putCargo(waterCargo);
        pigFarm0.putCargo(waterCargo);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0, map);

        /* Let the pig breeder rest */
        Utils.fastForward(100, map);

        /* Wait for the pig breeder to produce a new meat cargo */
        Worker worker = pigFarm0.getWorker();

        for (int i = 0; i < 1000; i++) {
            if (worker.getCargo() != null && worker.getPosition().equals(pigFarm0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(worker.getCargo());

        /* Verify that the pig breeder puts the meat cargo at the flag */
        assertEquals(worker.getTarget(), pigFarm0.getFlag().getPosition());
        assertTrue(pigFarm0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(pigFarm0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = pigFarm0.getFlag().getStackedCargo().get(0);

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), pigFarm0.getFlag().getPosition());

        /* Connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), pigFarm0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(pigFarm0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));


        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), pigFarm0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(PIG);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(PIG), amount + 1);
    }

    @Test
    public void testPigBreederGoesBackToStorageWhenPigFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing pig farm */
        Point point26 = new Point(8, 8);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0, map);

        /* Destroy the pig farm */
        Worker worker = pigFarm0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), pigFarm0.getPosition());

        pigFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(PIG_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

        /* Verify that the pig breeder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(PIG_BREEDER), amount + 1);
    }

    @Test
    public void testPigBreederGoesBackOnToStorageOnRoadsIfPossibleWhenPigFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing pig farm */
        Point point26 = new Point(8, 8);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Connect the pig farm with the headquarter */
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0, map);

        /* Destroy the pig farm */
        Worker worker = pigFarm0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), pigFarm0.getPosition());

        pigFarm0.tearDown();

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
    public void testPigBreederWithoutResourcesProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(player0), point3);

        Utils.constructHouse(pigFarm, map);

        /* Occupy the pig farm with a pig breeder */
        PigBreeder pigBreeder = new PigBreeder(player0, map);

        Utils.occupyBuilding(pigBreeder, pigFarm, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Let the pigBreeder rest */
        Utils.fastForward(99, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Verify that the pig breeder doesn't produce anything */
        for (int i = 0; i < 300; i++) {
            assertEquals(pigBreeder.getCargo(), null);

            map.stepTime();
        }
    }

    @Test
    public void testPigBreederWithoutResourcesStaysInHouse() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(player0), point3);

        Utils.constructHouse(pigFarm, map);

        /* Occupy the pig farm with a pig breeder */
        PigBreeder pigBreeder = new PigBreeder(player0, map);

        Utils.occupyBuilding(pigBreeder, pigFarm, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Let the pigBreeder rest */
        Utils.fastForward(99, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Verify that the pig breeder doesn't produce anything */
        for (int i = 0; i < 300; i++) {
            assertTrue(pigBreeder.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testPigBreederFeedsPigsWithWaterAndWheat() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(player0), point3);

        Utils.constructHouse(pigFarm, map);

        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm.putCargo(waterCargo);
        pigFarm.putCargo(wheatCargo);

        /* Assign a pigBreeder to the farm */
        PigBreeder pigBreeder = new PigBreeder(player0, map);

        Utils.occupyBuilding(pigBreeder, pigFarm, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Let the pigBreeder rest */
        Utils.fastForward(99, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Step once and to let the pigBreeder go out to feed */
        map.stepTime();

        assertFalse(pigBreeder.isInsideBuilding());

        Point point = pigBreeder.getTarget();

        assertTrue(pigBreeder.isTraveling());

        /* Let the pigBreeder reach the intended spot */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isAt(point));
        assertTrue(pigBreeder.isFeeding());

        /* Wait for the pigBreeder to feed the pigs */
        Utils.fastForward(19, map);

        assertTrue(pigBreeder.isFeeding());

        map.stepTime();

        /* Verify that the pig breeder is done feeding and has consumed the water and wheat */
        assertFalse(pigBreeder.isFeeding());
        assertEquals(pigFarm.getAmount(WATER), 0);
        assertEquals(pigFarm.getAmount(WHEAT), 0);
    }

    @Test
    public void testDestroyedPigFarmIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing pig farm */
        Point point26 = new Point(8, 8);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Connect the pig farm with the headquarter */
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Destroy the pig farm */
        pigFarm0.tearDown();

        assertTrue(pigFarm0.burningDown());

        /* Wait for the pig farm to stop burning */
        Utils.fastForward(50, map);

        assertTrue(pigFarm0.destroyed());

        /* Wait for the pig farm to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), pigFarm0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(pigFarm0));
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

        /* Placing pig farm */
        Point point26 = new Point(8, 8);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(pigFarm0.getPosition(), pigFarm0.getFlag().getPosition()));

        map.removeFlag(pigFarm0.getFlag());

        assertNull(map.getRoad(pigFarm0.getPosition(), pigFarm0.getFlag().getPosition()));
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

        /* Placing pig farm */
        Point point26 = new Point(8, 8);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(pigFarm0.getPosition(), pigFarm0.getFlag().getPosition()));

        pigFarm0.tearDown();

        assertNull(map.getRoad(pigFarm0.getPosition(), pigFarm0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInPigFarmCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(8, 6);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Connect the pig farm and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Assign a worker to the pig farm */
        PigBreeder worker = new PigBreeder(player0, map);

        Utils.occupyBuilding(worker, pigFarm0, map);

        assertTrue(worker.isInsideBuilding());

        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm0.putCargo(waterCargo);
        pigFarm0.putCargo(wheatCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the pig breeder to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), PIG);

        /* Wait for the worker to deliver the cargo */
        assertEquals(worker.getTarget(), pigFarm0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getFlag().getPosition());

        /* Stop production and verify that no pig is produced */
        pigFarm0.stopProduction();

        assertFalse(pigFarm0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInPigFarmCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(8, 6);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Connect the pig farm and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm0.putCargo(waterCargo);
        pigFarm0.putCargo(waterCargo);

        pigFarm0.putCargo(wheatCargo);
        pigFarm0.putCargo(wheatCargo);

        /* Assign a worker to the pig farm */
        PigBreeder worker = new PigBreeder(player0, map);

        Utils.occupyBuilding(worker, pigFarm0, map);

        assertTrue(worker.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the pig breeder to produce pig */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), PIG);

        /* Wait for the worker to deliver the cargo */
        assertEquals(worker.getTarget(), pigFarm0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getFlag().getPosition());

        /* Stop production */
        pigFarm0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the pig farm produces pig again */
        pigFarm0.resumeProduction();

        assertTrue(pigFarm0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertNotNull(worker.getCargo());
    }

    @Test
    public void testAssignedPigBreederHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place pig farm */
        Point point1 = new Point(20, 14);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), pigFarm0.getFlag());

        /* Wait for pig breeder to get assigned and leave the headquarter */
        List<PigBreeder> workers = Utils.waitForWorkersOutsideBuilding(PigBreeder.class, 1, player0, map);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        PigBreeder worker = workers.get(0);

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

        /* Place pig farm close to the new border */
        Point point4 = new Point(28, 18);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point4);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Occupy the pig farm */
        PigBreeder worker = Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0, map);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testPigBreederReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

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

        /* Placing pig farm */
        Point point2 = new Point(14, 4);
        Building farm0 = map.placeBuilding(new PigFarm(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, farm0.getFlag());

        /* Wait for the pig breeder to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(PigBreeder.class, 1, player0, map);

        PigBreeder pigBreeder = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof PigBreeder) {
                pigBreeder = (PigBreeder) worker;
            }
        }

        assertNotNull(pigBreeder);
        assertEquals(pigBreeder.getTarget(), farm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the pig breeder has started walking */
        assertFalse(pigBreeder.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the pig breeder continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, flag0.getPosition());

        assertEquals(pigBreeder.getPosition(), flag0.getPosition());

        /* Verify that the pig breeder returns to the headquarter when it reaches the flag */
        assertEquals(pigBreeder.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, headquarter0.getPosition());
    }

    @Test
    public void testPigBreederContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

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

        /* Placing pig farm */
        Point point2 = new Point(14, 4);
        Building farm0 = map.placeBuilding(new PigFarm(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, farm0.getFlag());

        /* Wait for the pig breeder to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(PigBreeder.class, 1, player0, map);

        PigBreeder pigBreeder = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof PigBreeder) {
                pigBreeder = (PigBreeder) worker;
            }
        }

        assertNotNull(pigBreeder);
        assertEquals(pigBreeder.getTarget(), farm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the farmer has started walking */
        assertFalse(pigBreeder.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the pig breeder continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, flag0.getPosition());

        assertEquals(pigBreeder.getPosition(), flag0.getPosition());

        /* Verify that the pig breeder continues to the final flag */
        assertEquals(pigBreeder.getTarget(), farm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, farm0.getFlag().getPosition());

        /* Verify that the pig breeder goes out to pig farm instead of going directly back */
        assertNotEquals(pigBreeder.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testPigBreederReturnsToStorageIfPigFarmIsDestroyed() throws Exception {

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

        /* Placing pig farm */
        Point point2 = new Point(14, 4);
        Building farm0 = map.placeBuilding(new PigFarm(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, farm0.getFlag());

        /* Wait for the pig breeder to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(PigBreeder.class, 1, player0, map);

        PigBreeder pigBreeder = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof PigBreeder) {
                pigBreeder = (PigBreeder) worker;
            }
        }

        assertNotNull(pigBreeder);
        assertEquals(pigBreeder.getTarget(), farm0.getPosition());

        /* Wait for the pig breeder to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, flag0.getPosition());

        map.stepTime();

        /* See that the pig breeder has started walking */
        assertFalse(pigBreeder.isExactlyAtPoint());

        /* Tear down the pig farm */
        farm0.tearDown();

        /* Verify that the pig breeder continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, farm0.getFlag().getPosition());

        assertEquals(pigBreeder.getPosition(), farm0.getFlag().getPosition());

        /* Verify that the pig breeder goes back to storage */
        assertEquals(pigBreeder.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testPigBreederGoesOffroadBackToClosestStorageWhenPigFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing pig farm */
        Point point26 = new Point(17, 17);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0, map);

        /* Place a second storage closer to the pig farm */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the pig farm */
        Worker pigBreeder = pigFarm0.getWorker();

        assertTrue(pigBreeder.isInsideBuilding());
        assertEquals(pigBreeder.getPosition(), pigFarm0.getPosition());

        pigFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(pigBreeder.isInsideBuilding());
        assertEquals(pigBreeder.getTarget(), storage0.getPosition());

        int amount = storage0.getAmount(PIG_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, storage0.getPosition());

        /* Verify that the pig breeder is stored correctly in the headquarter */
        assertEquals(storage0.getAmount(PIG_BREEDER), amount + 1);
    }

    @Test
    public void testPigBreederReturnsOffroadAndAvoidsBurningStorageWhenPigFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing pig farm */
        Point point26 = new Point(17, 17);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0, map);

        /* Place a second storage closer to the pig farm */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

        /* Destroy the pig farm */
        Worker pigBreeder = pigFarm0.getWorker();

        assertTrue(pigBreeder.isInsideBuilding());
        assertEquals(pigBreeder.getPosition(), pigFarm0.getPosition());

        pigFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(pigBreeder.isInsideBuilding());
        assertEquals(pigBreeder.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(PIG_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, headquarter0.getPosition());

        /* Verify that the pig breeder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(PIG_BREEDER), amount + 1);
    }

    @Test
    public void testPigBreederReturnsOffroadAndAvoidsDestroyedStorageWhenPigFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing pig farm */
        Point point26 = new Point(17, 17);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0, map);

        /* Place a second storage closer to the pig farm */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storage0, map);

        /* Destroy the pig farm */
        Worker pigBreeder = pigFarm0.getWorker();

        assertTrue(pigBreeder.isInsideBuilding());
        assertEquals(pigBreeder.getPosition(), pigFarm0.getPosition());

        pigFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(pigBreeder.isInsideBuilding());
        assertEquals(pigBreeder.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(PIG_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, headquarter0.getPosition());

        /* Verify that the pig breeder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(PIG_BREEDER), amount + 1);
    }

    @Test
    public void testPigBreederReturnsOffroadAndAvoidsUnfinishedStorageWhenPigFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing pig farm */
        Point point26 = new Point(17, 17);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0, map);

        /* Place a second storage closer to the pig farm */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Destroy the pig farm */
        Worker pigBreeder = pigFarm0.getWorker();

        assertTrue(pigBreeder.isInsideBuilding());
        assertEquals(pigBreeder.getPosition(), pigFarm0.getPosition());

        pigFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(pigBreeder.isInsideBuilding());
        assertEquals(pigBreeder.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(PIG_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, headquarter0.getPosition());

        /* Verify that the pig breeder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(PIG_BREEDER), amount + 1);
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

        /* Place pigFarm */
        Point point26 = new Point(17, 17);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Place road to connect the headquarter and the pig farm */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), pigFarm0.getFlag());

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(PigBreeder.class, 1, player0, map).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getFlag().getPosition());

        /* Tear down the building */
        pigFarm0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquarter instead of entering
        */
        assertEquals(worker.getTarget(), pigFarm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testPigFarmWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(7, 9);
        Building pigFarm = map.placeBuilding(new PigFarm(player0), point1);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm, map);

        /* Populate the pig farm */
        Worker pigBreeder0 = Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm, map);

        assertTrue(pigBreeder0.isInsideBuilding());
        assertEquals(pigBreeder0.getHome(), pigFarm);
        assertEquals(pigFarm.getWorker(), pigBreeder0);

        /* Verify that the productivity is 0% when the pig farm doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(pigFarm.getFlag().getStackedCargo().isEmpty());
            assertNull(pigBreeder0.getCargo());
            assertEquals(pigFarm.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testPigFarmWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(7, 9);
        Building pigFarm = map.placeBuilding(new PigFarm(player0), point1);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm, map);

        /* Populate the pig farm */
        Worker pigBreeder0 = Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm, map);

        assertTrue(pigBreeder0.isInsideBuilding());
        assertEquals(pigBreeder0.getHome(), pigFarm);
        assertEquals(pigFarm.getWorker(), pigBreeder0);

        /* Connect the pig farm with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), pigFarm.getFlag());

        /* Make the pig farm produce some pigs with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (pigFarm.needsMaterial(WHEAT) && pigFarm.getAmount(WHEAT) < 2) {
                pigFarm.putCargo(new Cargo(WHEAT, map));
            }

            if (pigFarm.needsMaterial(WATER) && pigFarm.getAmount(WATER) < 2) {
                pigFarm.putCargo(new Cargo(WATER, map));
            }
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(pigFarm.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (pigFarm.needsMaterial(WHEAT) && pigFarm.getAmount(WHEAT) < 2) {
                pigFarm.putCargo(new Cargo(WHEAT, map));
            }

            if (pigFarm.needsMaterial(WATER) && pigFarm.getAmount(WATER) < 2) {
                pigFarm.putCargo(new Cargo(WATER, map));
            }

            assertEquals(pigFarm.getProductivity(), 100);
        }
    }

    @Test
    public void testPigFarmLosesProductivityWhenResourcesRunOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(7, 9);
        Building pigFarm = map.placeBuilding(new PigFarm(player0), point1);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm, map);

        /* Populate the pig farm */
        Worker pigBreeder0 = Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm, map);

        assertTrue(pigBreeder0.isInsideBuilding());
        assertEquals(pigBreeder0.getHome(), pigFarm);
        assertEquals(pigFarm.getWorker(), pigBreeder0);

        /* Connect the pig farm with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), pigFarm.getFlag());

        /* Make the pig farm produce some pigs with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (pigFarm.needsMaterial(WHEAT) && pigFarm.getAmount(WHEAT) < 2) {
                pigFarm.putCargo(new Cargo(WHEAT, map));
            }

            if (pigFarm.needsMaterial(WATER) && pigFarm.getAmount(WATER) < 2) {
                pigFarm.putCargo(new Cargo(WATER, map));
            }
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(pigFarm.getProductivity(), 100);

        for (int i = 0; i < 5000; i++) {
            map.stepTime();
        }

        assertEquals(pigFarm.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedPigFarmHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(7, 9);
        Building pigFarm = map.placeBuilding(new PigFarm(player0), point1);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm, map);

        /* Verify that the unoccupied pig farm is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(pigFarm.getProductivity(), 0);

            if (pigFarm.needsMaterial(WHEAT) && pigFarm.getAmount(WHEAT) < 2) {
                pigFarm.putCargo(new Cargo(WHEAT, map));
            }

            if (pigFarm.needsMaterial(WATER) && pigFarm.getAmount(WATER) < 2) {
                pigFarm.putCargo(new Cargo(WATER, map));
            }

            map.stepTime();
        }
    }

    @Test
    public void testPigFarmCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(10, 10);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Populate the pig farm */
        Worker pigBreeder0 = Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0, map);

        /* Verify that the pig farm can produce */
        assertTrue(pigFarm0.canProduce());
    }

    @Test
    public void testPigFarmReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(6, 22);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Construct the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Verify that the reported output is correct */
        assertEquals(pigFarm0.getProducedMaterial().length, 1);
        assertEquals(pigFarm0.getProducedMaterial()[0], PIG);
    }

    @Test
    public void testPigFarmReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(6, 22);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(pigFarm0.getMaterialNeeded().size(), 2);
        assertTrue(pigFarm0.getMaterialNeeded().contains(PLANK));
        assertTrue(pigFarm0.getMaterialNeeded().contains(STONE));
        assertEquals(pigFarm0.getTotalAmountNeeded(PLANK), 3);
        assertEquals(pigFarm0.getTotalAmountNeeded(STONE), 3);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(pigFarm0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testPigFarmReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(6, 22);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Construct the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Verify that the reported needed construction material is correct */
        assertEquals(pigFarm0.getMaterialNeeded().size(), 2);
        assertTrue(pigFarm0.getMaterialNeeded().contains(WATER));
        assertTrue(pigFarm0.getMaterialNeeded().contains(WHEAT));
        assertEquals(pigFarm0.getTotalAmountNeeded(WATER), 1);
        assertEquals(pigFarm0.getTotalAmountNeeded(WHEAT), 1);

        for (Material material : Material.values()) {
            if (material == WATER || material == WHEAT) {
                continue;
            }

            assertEquals(pigFarm0.getTotalAmountNeeded(material), 0);
        }
    }
}
