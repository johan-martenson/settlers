/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Butcher;
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
import org.appland.settlers.model.SlaughterHouse;
import org.appland.settlers.model.Storehouse;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static org.appland.settlers.model.Material.BUTCHER;
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.PIG;
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
public class TestSlaughterHouse {

    @Test
    public void testSlaughterHouseOnlyNeedsTwoPlanksAndTwoStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing slaughter house */
        Point point22 = new Point(6, 12);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point22);

        /* Deliver two plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        slaughterHouse0.putCargo(plankCargo);
        slaughterHouse0.putCargo(plankCargo);
        slaughterHouse0.putCargo(stoneCargo);
        slaughterHouse0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the slaughter house */
        for (int i = 0; i < 150; i++) {
            assertTrue(slaughterHouse0.underConstruction());

            map.stepTime();
        }

        assertTrue(slaughterHouse0.isReady());
    }

    @Test
    public void testSlaughterHouseCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing slaughter house */
        Point point22 = new Point(6, 12);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point22);

        /* Deliver one plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        slaughterHouse0.putCargo(plankCargo);
        slaughterHouse0.putCargo(stoneCargo);
        slaughterHouse0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the slaughter house */
        for (int i = 0; i < 500; i++) {
            assertTrue(slaughterHouse0.underConstruction());

            map.stepTime();
        }

        assertFalse(slaughterHouse0.isReady());
    }

    @Test
    public void testSlaughterHouseCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing slaughter house */
        Point point22 = new Point(6, 12);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point22);

        /* Deliver two planks and one stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        slaughterHouse0.putCargo(plankCargo);
        slaughterHouse0.putCargo(plankCargo);
        slaughterHouse0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the slaughter house */
        for (int i = 0; i < 500; i++) {
            assertTrue(slaughterHouse0.underConstruction());

            map.stepTime();
        }

        assertFalse(slaughterHouse0.isReady());
    }

    @Test
    public void testSlaughterHouseNeedsWorker() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        /* Connect the slaughter house with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        /* Unfinished slaughter house doesn't need worker */
        assertFalse(slaughterHouse.needsWorker());

        /* Finish construction of the slaughterHouse */
        Utils.constructHouse(slaughterHouse);

        assertTrue(slaughterHouse.needsWorker());
    }

    @Test
    public void testHeadquarterAtLeastHasOneButcherAtStart() {
        Headquarter headquarter = new Headquarter(null);

        assertTrue(headquarter.getAmount(BUTCHER) >= 1);
    }

    @Test
    public void testSlaughterHouseGetsAssignedWorker() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        /* Connect the slaughter house with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        /* Finish construction of the slaughterHouse */
        Utils.constructHouse(slaughterHouse);

        assertTrue(slaughterHouse.needsWorker());

        /* Verify that a slaughterHouse worker leaves the headquarter */
        Utils.fastForward(3, map);

        assertEquals(map.getWorkers().size(), 3);

        /* Let the slaughterHouse worker reach the slaughterHouse */
        Butcher butcher = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Butcher) {
                butcher = (Butcher)worker;
            }
        }

        assertNotNull(butcher);
        assertEquals(butcher.getTarget(), slaughterHouse.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, butcher);

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);
    }

    @Test
    public void testButcherGetsCreatedFromCleaver() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all butchers from the headquarter and add one cleaver */
        Utils.adjustInventoryTo(headquarter, BUTCHER, 0);
        Utils.adjustInventoryTo(headquarter, Material.CLEAVER, 1);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        /* Connect the slaughter house with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        /* Finish construction of the slaughterHouse */
        Utils.constructHouse(slaughterHouse);

        assertTrue(slaughterHouse.needsWorker());

        /* Verify that a slaughterHouse worker leaves the headquarter */
        Utils.fastForward(3, map);

        assertEquals(map.getWorkers().size(), 3);

        /* Let the slaughterHouse worker reach the slaughterHouse */
        Butcher butcher = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Butcher) {
                butcher = (Butcher)worker;
            }
        }

        assertNotNull(butcher);
        assertEquals(butcher.getTarget(), slaughterHouse.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, butcher);

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);
    }

    @Test
    public void testOccupiedSlaughterHouseWithoutPigsProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        /* Finish construction of the slaughterHouse */
        Utils.constructHouse(slaughterHouse);

        /* Populate the slaughterHouse */
        Worker butcher = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse);

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);

        /* Verify that the slaughterHouse doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
            assertNull(butcher.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedSlaughterHouseProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        /* Finish construction of the slaughterHouse */
        Utils.constructHouse(slaughterHouse);

        /* Verify that the slaughterHouse doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedSlaughterHouseWithPigsProducesMeat() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        /* Connect the slaughter house with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        /* Finish construction of the slaughterHouse */
        Utils.constructHouse(slaughterHouse);

        /* Populate the slaughterHouse */
        Worker butcher = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse);

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);

        /* Deliver pig to the slaughterHouse */
        slaughterHouse.putCargo(new Cargo(PIG, map));

        /* Verify that the slaughterHouse produces meat */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
            assertNull(butcher.getCargo());
        }

        map.stepTime();

        assertNotNull(butcher.getCargo());
        assertEquals(butcher.getCargo().getMaterial(), MEAT);
        assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testButcherLeavesMeatAtTheFlag() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        /* Connect the slaughter house with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        /* Finish construction of the slaughterHouse */
        Utils.constructHouse(slaughterHouse);

        /* Populate the slaughterHouse */
        Worker butcher = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse);

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);

        /* Deliver ingredients to the slaughterHouse */
        slaughterHouse.putCargo(new Cargo(PIG, map));

        /* Verify that the slaughterHouse produces meat */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
            assertNull(butcher.getCargo());
        }

        map.stepTime();

        assertNotNull(butcher.getCargo());
        assertEquals(butcher.getCargo().getMaterial(), MEAT);
        assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());

        /* Verify that the slaughterHouse worker leaves the cargo at the flag */
        assertEquals(butcher.getTarget(), slaughterHouse.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, slaughterHouse.getFlag().getPosition());

        assertFalse(slaughterHouse.getFlag().getStackedCargo().isEmpty());
        assertNull(butcher.getCargo());
        assertEquals(butcher.getTarget(), slaughterHouse.getPosition());

        /* Verify that the butcher goes back to the slaughterHouse */
        Utils.fastForwardUntilWorkersReachTarget(map, butcher);

        assertTrue(butcher.isInsideBuilding());
    }

    @Test
    public void testProductionOfOneBreadConsumesOnePig() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        /* Finish construction of the slaughterHouse */
        Utils.constructHouse(slaughterHouse);

        /* Populate the slaughterHouse */
        Worker butcher = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse);

        /* Deliver ingredients to the slaughterHouse */
        slaughterHouse.putCargo(new Cargo(PIG, map));

        /* Wait until the slaughterHouse worker produces meat */
        assertEquals(slaughterHouse.getAmount(PIG), 1);

        Utils.fastForward(150, map);

        assertEquals(slaughterHouse.getAmount(PIG), 0);
    }

    @Test
    public void testProductionCountdownStartsWhenMaterialIsAvailable() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        /* Finish construction of the slaughterHouse */
        Utils.constructHouse(slaughterHouse);

        /* Populate the slaughterHouse */
        Worker butcher = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse);

        /* Fast forward so that the slaughterHouse worker would have produced meat if it had had a pig */
        Utils.fastForward(150, map);

        assertNull(butcher.getCargo());

        /* Deliver ingredients to the slaughterHouse */
        slaughterHouse.putCargo(new Cargo(PIG, map));

        /* Verify that it takes 50 steps for the slaughterHouse worker to produce the meat */
        for (int i = 0; i < 50; i++) {
            assertNull(butcher.getCargo());
            map.stepTime();
        }

        assertNotNull(butcher.getCargo());
    }

    @Test
    public void testSlaughterHouseWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing slaughter house */
        Point point26 = new Point(8, 8);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Occupy the slaughter house */
        Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        /* Deliver material to the slaughter house */
        Cargo pigCargo = new Cargo(PIG, map);

        slaughterHouse0.putCargo(pigCargo);
        slaughterHouse0.putCargo(pigCargo);

        /* Let the butcher rest */
        Utils.fastForward(100, map);

        /* Wait for the butcher to produce a new meat cargo */
        Utils.fastForward(50, map);

        Worker worker = slaughterHouse0.getWorker();

        assertNotNull(worker.getCargo());

        /* Verify that the butcher puts the meat cargo at the flag */
        assertEquals(worker.getTarget(), slaughterHouse0.getFlag().getPosition());
        assertTrue(slaughterHouse0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(slaughterHouse0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the slaughter house */
        assertEquals(worker.getTarget(), slaughterHouse0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(worker.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(worker.getTarget(), slaughterHouse0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertEquals(slaughterHouse0.getFlag().getStackedCargo().size(), 2);
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

        /* Placing slaughter house */
        Point point26 = new Point(8, 8);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Deliver material to the slaughter house */
        Cargo pigCargo = new Cargo(PIG, map);

        slaughterHouse0.putCargo(pigCargo);
        slaughterHouse0.putCargo(pigCargo);

        /* Occupy the slaughter house */
        Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        /* Let the butcher rest */
        Utils.fastForward(100, map);

        /* Wait for the butcher to produce a new meat cargo */
        Utils.fastForward(50, map);

        Worker worker = slaughterHouse0.getWorker();

        assertNotNull(worker.getCargo());

        /* Verify that the butcher puts the meat cargo at the flag */
        assertEquals(worker.getTarget(), slaughterHouse0.getFlag().getPosition());
        assertTrue(slaughterHouse0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(slaughterHouse0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = slaughterHouse0.getFlag().getStackedCargo().get(0);

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), slaughterHouse0.getFlag().getPosition());

        /* Connect the slaughter house with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), slaughterHouse0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), slaughterHouse0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), slaughterHouse0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MEAT);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(MEAT), amount + 1);
    }

    @Test
    public void testButcherGoesBackToStorageWhenSlaughterHouseIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing slaughter house */
        Point point26 = new Point(8, 8);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Occupy the slaughter house */
        Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        /* Destroy the slaughter house */
        Worker worker = slaughterHouse0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), slaughterHouse0.getPosition());

        slaughterHouse0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BUTCHER);

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

        /* Verify that the butcher is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(BUTCHER), amount + 1);
    }

    @Test
    public void testButcherGoesBackOnToStorageOnRoadsIfPossibleWhenSlaughterHouseIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing slaughter house */
        Point point26 = new Point(8, 8);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        /* Connect the slaughter house with the headquarter */
        map.placeAutoSelectedRoad(player0, slaughterHouse0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Occupy the slaughter house */
        Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        /* Destroy the slaughter house */
        Worker worker = slaughterHouse0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), slaughterHouse0.getPosition());

        slaughterHouse0.tearDown();

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
    public void testDestroyedSlaughterHouseIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing slaughter house */
        Point point26 = new Point(8, 8);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        /* Connect the slaughter house with the headquarter */
        map.placeAutoSelectedRoad(player0, slaughterHouse0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Destroy the slaughter house */
        slaughterHouse0.tearDown();

        assertTrue(slaughterHouse0.isBurningDown());

        /* Wait for the slaughter house to stop burning */
        Utils.fastForward(50, map);

        assertTrue(slaughterHouse0.isDestroyed());

        /* Wait for the slaughter house to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), slaughterHouse0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(slaughterHouse0));
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

        /* Placing slaughter house */
        Point point26 = new Point(8, 8);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(slaughterHouse0.getPosition(), slaughterHouse0.getFlag().getPosition()));

        map.removeFlag(slaughterHouse0.getFlag());

        assertNull(map.getRoad(slaughterHouse0.getPosition(), slaughterHouse0.getFlag().getPosition()));
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

        /* Placing slaughter house */
        Point point26 = new Point(8, 8);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(slaughterHouse0.getPosition(), slaughterHouse0.getFlag().getPosition()));

        slaughterHouse0.tearDown();

        assertNull(map.getRoad(slaughterHouse0.getPosition(), slaughterHouse0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInSlaughterHouseCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughter house */
        Point point1 = new Point(10, 8);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        /* Connect the slaughter house and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, slaughterHouse0.getFlag(), headquarter.getFlag());

        /* Finish the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Assign a worker to the slaughter house */
        Butcher worker = new Butcher(player0, map);

        Utils.occupyBuilding(worker, slaughterHouse0);

        assertTrue(worker.isInsideBuilding());

        /* Deliver material to the slaughter house */
        Cargo pigCargo = new Cargo(PIG, map);

        slaughterHouse0.putCargo(pigCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the butcher to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), MEAT);

        /* Wait for the worker to deliver the cargo */
        assertEquals(worker.getTarget(), slaughterHouse0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getFlag().getPosition());

        /* Stop production and verify that no meat is produced */
        slaughterHouse0.stopProduction();

        assertFalse(slaughterHouse0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInSlaughterHouseCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughter house */
        Point point1 = new Point(10, 8);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        /* Connect the slaughter house and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, slaughterHouse0.getFlag(), headquarter.getFlag());

        /* Finish the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Deliver material to the slaughter house */
        Cargo pigCargo = new Cargo(PIG, map);

        slaughterHouse0.putCargo(pigCargo);

        /* Assign a worker to the slaughter house */
        Butcher worker = new Butcher(player0, map);

        Utils.occupyBuilding(worker, slaughterHouse0);

        assertTrue(worker.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Deliver pig to the slaughterHouse */
        slaughterHouse0.putCargo(new Cargo(PIG, map));

        /* Wait for the butcher to produce meat */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), MEAT);

        /* Wait for the worker to deliver the cargo */
        assertEquals(worker.getTarget(), slaughterHouse0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getFlag().getPosition());

        /* Stop production */
        slaughterHouse0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the slaughter house produces meat again */
        slaughterHouse0.resumeProduction();

        assertTrue(slaughterHouse0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertNotNull(worker.getCargo());
    }

    @Test
    public void testAssignedButcherHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughter house */
        Point point1 = new Point(20, 14);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Connect the slaughter house with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), slaughterHouse0.getFlag());

        assertTrue(map.arePointsConnectedByRoads(headquarter0.getPosition(), slaughterHouse0.getPosition()));
        assertTrue(headquarter0.getAmount(BUTCHER) > 0);

        /* Wait for butcher to get assigned and leave the headquarter */
        List<Butcher> workers = Utils.waitForWorkersOutsideBuilding(Butcher.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Butcher worker = workers.get(0);

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

        /* Place slaughter house close to the new border */
        Point point4 = new Point(28, 18);
        SlaughterHouse slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point4);

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Occupy the slaughter house */
        Butcher worker = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testButcherReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

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

        /* Placing slaughter house */
        Point point2 = new Point(14, 4);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, slaughterHouse0.getFlag());

        /* Wait for the butcher to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Butcher.class, 1, player0);

        Butcher butcher = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Butcher) {
                butcher = (Butcher) worker;
            }
        }

        assertNotNull(butcher);
        assertEquals(butcher.getTarget(), slaughterHouse0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the butcher has started walking */
        assertFalse(butcher.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the butcher continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, flag0.getPosition());

        assertEquals(butcher.getPosition(), flag0.getPosition());

        /* Verify that the butcher returns to the headquarter when it reaches the flag */
        assertEquals(butcher.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, headquarter0.getPosition());
    }

    @Test
    public void testButcherContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

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

        /* Placing slaughter house */
        Point point2 = new Point(14, 4);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, slaughterHouse0.getFlag());

        /* Wait for the butcher to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Butcher.class, 1, player0);

        Butcher butcher = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Butcher) {
                butcher = (Butcher) worker;
            }
        }

        assertNotNull(butcher);
        assertEquals(butcher.getTarget(), slaughterHouse0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the butcher has started walking */
        assertFalse(butcher.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the butcher continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, flag0.getPosition());

        assertEquals(butcher.getPosition(), flag0.getPosition());

        /* Verify that the butcher continues to the final flag */
        assertEquals(butcher.getTarget(), slaughterHouse0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, slaughterHouse0.getFlag().getPosition());

        /* Verify that the butcher goes out to slaughter house instead of going directly back */
        assertNotEquals(butcher.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testButcherReturnsToStorageIfSlaughterHouseIsDestroyed() throws Exception {

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

        /* Placing slaughter house */
        Point point2 = new Point(14, 4);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, slaughterHouse0.getFlag());

        /* Wait for the butcher to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Butcher.class, 1, player0);

        Butcher butcher = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Butcher) {
                butcher = (Butcher) worker;
            }
        }

        assertNotNull(butcher);
        assertEquals(butcher.getTarget(), slaughterHouse0.getPosition());

        /* Wait for the butcher to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, flag0.getPosition());

        map.stepTime();

        /* See that the butcher has started walking */
        assertFalse(butcher.isExactlyAtPoint());

        /* Tear down the slaughterHouse */
        slaughterHouse0.tearDown();

        /* Verify that the butcher continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, slaughterHouse0.getFlag().getPosition());

        assertEquals(butcher.getPosition(), slaughterHouse0.getFlag().getPosition());

        /* Verify that the butcher goes back to storage */
        assertEquals(butcher.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testButcherGoesOffroadBackToClosestStorageWhenSlaughterHouseIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing slaughter house */
        Point point26 = new Point(17, 17);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Occupy the slaughterHouse */
        Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        /* Place a second storage closer to the slaughter house */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the slaughter house */
        Worker butcher = slaughterHouse0.getWorker();

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getPosition(), slaughterHouse0.getPosition());

        slaughterHouse0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(butcher.isInsideBuilding());
        assertEquals(butcher.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(BUTCHER);

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, storehouse0.getPosition());

        /* Verify that the butcher is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(BUTCHER), amount + 1);
    }

    @Test
    public void testButcherReturnsOffroadAndAvoidsBurningStorageWhenSlaughterHouseIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing slaughter house */
        Point point26 = new Point(17, 17);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Occupy the slaughter house */
        Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        /* Place a second storage closer to the slaughter house */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Destroy the slaughter house */
        Worker butcher = slaughterHouse0.getWorker();

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getPosition(), slaughterHouse0.getPosition());

        slaughterHouse0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(butcher.isInsideBuilding());
        assertEquals(butcher.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BUTCHER);

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, headquarter0.getPosition());

        /* Verify that the butcher is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(BUTCHER), amount + 1);
    }

    @Test
    public void testButcherReturnsOffroadAndAvoidsDestroyedStorageWhenSlaughterHouseIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing slaughter house */
        Point point26 = new Point(17, 17);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Occupy the slaughter house */
        Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        /* Place a second storage closer to the slaughter house */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

        /* Destroy the slaughter house */
        Worker butcher = slaughterHouse0.getWorker();

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getPosition(), slaughterHouse0.getPosition());

        slaughterHouse0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(butcher.isInsideBuilding());
        assertEquals(butcher.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BUTCHER);

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, headquarter0.getPosition());

        /* Verify that the butcher is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(BUTCHER), amount + 1);
    }

    @Test
    public void testButcherReturnsOffroadAndAvoidsUnfinishedStorageWhenSlaughterHouseIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing slaughter house */
        Point point26 = new Point(17, 17);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Occupy the slaughter house */
        Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        /* Place a second storage closer to the slaughter house */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the slaughter house */
        Worker butcher = slaughterHouse0.getWorker();

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getPosition(), slaughterHouse0.getPosition());

        slaughterHouse0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(butcher.isInsideBuilding());
        assertEquals(butcher.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BUTCHER);

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, headquarter0.getPosition());

        /* Verify that the butcher is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(BUTCHER), amount + 1);
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

        /* Place slaughter house */
        Point point26 = new Point(10, 10);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Place road to connect the headquarter and the slaughter house */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), slaughterHouse0.getFlag());

        /* Wait for a worker to start walking to the building */
        Butcher worker = Utils.waitForWorkersOutsideBuilding(Butcher.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getFlag().getPosition());

        /* Tear down the building */
        slaughterHouse0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(worker.getTarget(), slaughterHouse0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testSlaughterHouseWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place butcher */
        Point point1 = new Point(7, 9);
        Building butcher = map.placeBuilding(new SlaughterHouse(player0), point1);

        /* Finish construction of the butcher */
        Utils.constructHouse(butcher);

        /* Populate the butcher */
        Worker butcher0 = Utils.occupyBuilding(new Butcher(player0, map), butcher);

        assertTrue(butcher0.isInsideBuilding());
        assertEquals(butcher0.getHome(), butcher);
        assertEquals(butcher.getWorker(), butcher0);

        /* Verify that the productivity is 0% when the butcher doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(butcher.getFlag().getStackedCargo().isEmpty());
            assertNull(butcher0.getCargo());
            assertEquals(butcher.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testSlaughterHouseWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place butcher */
        Point point1 = new Point(7, 9);
        Building butcher = map.placeBuilding(new SlaughterHouse(player0), point1);

        /* Finish construction of the butcher */
        Utils.constructHouse(butcher);

        /* Populate the butcher */
        Worker butcher0 = Utils.occupyBuilding(new Butcher(player0, map), butcher);

        assertTrue(butcher0.isInsideBuilding());
        assertEquals(butcher0.getHome(), butcher);
        assertEquals(butcher.getWorker(), butcher0);

        /* Connect the butcher with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), butcher.getFlag());

        /* Make the butcher produce some meat with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (butcher.needsMaterial(PIG) && butcher.getAmount(PIG) < 2) {
                butcher.putCargo(new Cargo(PIG, map));
            }
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(butcher.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (butcher.needsMaterial(PIG) && butcher.getAmount(PIG) < 2) {
                butcher.putCargo(new Cargo(PIG, map));
            }

            assertEquals(butcher.getProductivity(), 100);
        }
    }

    @Test
    public void testSlaughterHouseLosesProductivityWhenResourcesRunOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place butcher */
        Point point1 = new Point(7, 9);
        Building butcher = map.placeBuilding(new SlaughterHouse(player0), point1);

        /* Finish construction of the butcher */
        Utils.constructHouse(butcher);

        /* Populate the butcher */
        Worker butcher0 = Utils.occupyBuilding(new Butcher(player0, map), butcher);

        assertTrue(butcher0.isInsideBuilding());
        assertEquals(butcher0.getHome(), butcher);
        assertEquals(butcher.getWorker(), butcher0);

        /* Connect the butcher with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), butcher.getFlag());

        /* Make the butcher produce some meat with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (butcher.needsMaterial(PIG) && butcher.getAmount(PIG) < 2) {
                butcher.putCargo(new Cargo(PIG, map));
            }
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(butcher.getProductivity(), 100);

        for (int i = 0; i < 5000; i++) {
                map.stepTime();
        }

        assertEquals(butcher.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedSlaughterHouseHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place butcher */
        Point point1 = new Point(7, 9);
        Building butcher = map.placeBuilding(new SlaughterHouse(player0), point1);

        /* Finish construction of the butcher */
        Utils.constructHouse(butcher);

        /* Verify that the unoccupied butcher is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(butcher.getProductivity(), 0);

            if (butcher.needsMaterial(PIG) && butcher.getAmount(PIG) < 2) {
                butcher.putCargo(new Cargo(PIG, map));
            }

            map.stepTime();
        }
    }

    @Test
    public void testSlaughterHouseCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughter house */
        Point point1 = new Point(10, 10);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Populate the slaughter house */
        Worker butcher0 = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        /* Verify that the slaughter house can produce */
        assertTrue(slaughterHouse0.canProduce());
    }

    @Test
    public void testSlaughterHouseReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughter house */
        Point point1 = new Point(6, 12);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        /* Construct the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Verify that the reported output is correct */
        assertEquals(slaughterHouse0.getProducedMaterial().length, 1);
        assertEquals(slaughterHouse0.getProducedMaterial()[0], MEAT);
    }

    @Test
    public void testSlaughterHouseReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughter house */
        Point point1 = new Point(6, 12);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(slaughterHouse0.getMaterialNeeded().size(), 2);
        assertTrue(slaughterHouse0.getMaterialNeeded().contains(PLANK));
        assertTrue(slaughterHouse0.getMaterialNeeded().contains(STONE));
        assertEquals(slaughterHouse0.getTotalAmountNeeded(PLANK), 2);
        assertEquals(slaughterHouse0.getTotalAmountNeeded(STONE), 2);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(slaughterHouse0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testSlaughterHouseReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughter house */
        Point point1 = new Point(6, 12);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        /* Construct the slaughter house */
        Utils.constructHouse(slaughterHouse0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(slaughterHouse0.getMaterialNeeded().size(), 1);
        assertTrue(slaughterHouse0.getMaterialNeeded().contains(PIG));
        assertEquals(slaughterHouse0.getTotalAmountNeeded(PIG), 1);

        for (Material material : Material.values()) {
            if (material == PIG) {
                continue;
            }

            assertEquals(slaughterHouse0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testSlaughterHouseWaitsWhenFlagIsFull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughterHouse */
        Point point1 = new Point(16, 6);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point1);

        /* Connect the slaughterHouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        /* Wait for the slaughterHouse to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(slaughterHouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(slaughterHouse);

        /* Give material to the slaughterHouse */
        Utils.putCargoToBuilding(slaughterHouse, PIG);
        Utils.putCargoToBuilding(slaughterHouse, PIG);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, slaughterHouse.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the slaughterHouse waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(slaughterHouse.getFlag().getStackedCargo().size(), 8);
            assertNull(slaughterHouse.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the slaughterHouse with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(slaughterHouse.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(slaughterHouse.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(slaughterHouse.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, slaughterHouse.getWorker(), MEAT);
    }

    @Test
    public void testSlaughterHouseDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughterHouse */
        Point point1 = new Point(16, 6);
        SlaughterHouse slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point1);

        /* Connect the slaughterHouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        /* Wait for the slaughterHouse to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(slaughterHouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(slaughterHouse);

        /* Give material to the slaughterHouse */
        Utils.putCargoToBuilding(slaughterHouse, PIG);
        Utils.putCargoToBuilding(slaughterHouse, PIG);
        Utils.putCargoToBuilding(slaughterHouse, PIG);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, slaughterHouse.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The slaughterHouse waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(slaughterHouse.getFlag().getStackedCargo().size(), 8);
            assertNull(slaughterHouse.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the slaughterHouse with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(slaughterHouse.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(slaughterHouse.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(slaughterHouse.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, slaughterHouse.getWorker(), MEAT);

        /* Wait for the worker to put the cargo on the flag */
        assertEquals(slaughterHouse.getWorker().getTarget(), slaughterHouse.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, slaughterHouse.getWorker(), slaughterHouse.getFlag().getPosition());

        assertEquals(slaughterHouse.getFlag().getStackedCargo().size(), 8);

        /* Verify that the slaughterHouse doesn't produce anything because the flag is full */
        for (int i = 0; i < 400; i++) {
            assertEquals(slaughterHouse.getFlag().getStackedCargo().size(), 8);
            assertNull(slaughterHouse.getWorker().getCargo());

            map.stepTime();
        }
    }
}
