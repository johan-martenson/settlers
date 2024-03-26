/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Bakery;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Storehouse;
import org.appland.settlers.model.Well;
import org.appland.settlers.model.actors.WellWorker;
import org.appland.settlers.model.actors.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.*;
import static org.appland.settlers.model.DetailedVegetation.BUILDABLE_MOUNTAIN;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestWell {

    /*
    * TODO: test what detailed vegetations the well can get water from
    * */

    @Test
    public void testWellOnlyNeedsTwoPlanksForConstruction() throws Exception {

        /* Start new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(6, 12);
        Well well0 = map.placeBuilding(new Well(player0), point1);

        /* Deliver three plank and three stone */
        Cargo plankCargo = new Cargo(PLANK, map);

        well0.putCargo(plankCargo);
        well0.putCargo(plankCargo);

        /* Assign builder */
        Utils.assignBuilder(well0);

        /* Verify that this is enough to construct the well */
        for (int i = 0; i < 100; i++) {
            assertTrue(well0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(well0.isReady());
    }

    @Test
    public void testWellCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(6, 12);
        Well well0 = map.placeBuilding(new Well(player0), point1);

        /* Deliver one plank */
        Cargo plankCargo = new Cargo(PLANK, map);

        well0.putCargo(plankCargo);

        /* Assign builder */
        Utils.assignBuilder(well0);

        /* Verify that this is not enough to construct the well */
        for (int i = 0; i < 500; i++) {
            assertTrue(well0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(well0.isReady());
    }

    @Test
    public void testFinishedWellNeedsWorker() throws Exception {

        /* Create gamemap */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(8, 6);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Finish construction of the well */
        Utils.constructHouse(well);

        assertTrue(well.isReady());
        assertTrue(well.needsWorker());
    }

    @Test
    public void testWellWorkerIsAssignedToFinishedHouse() throws Exception {

        /* Create gamemap */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(8, 6);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well.getFlag(), headquarter.getFlag());

        /* Finish the well */
        Utils.constructHouse(well);

        /* Run game logic twice, once to place courier and once to place well worker */
        Utils.fastForward(2, map);

        assertTrue(map.getWorkers().size() >= 3);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), WellWorker.class);
    }

    @Test
    public void testWellWorkerIsNotASoldier() throws Exception {

        /* Create gamemap */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(8, 6);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well.getFlag(), headquarter.getFlag());

        /* Finish the well */
        Utils.constructHouse(well);

        /* Wait for a well worker to walk out */
        WellWorker wellWorker0 = Utils.waitForWorkerOutsideBuilding(WellWorker.class, player0);

        /* Verify that the well worker is not a soldier */
        assertFalse(wellWorker0.isSoldier());
    }

    @Test
    public void testUnoccupiedWellProducesNothing() throws Exception {

        /* Create gamemap */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Connect the well with the headquarter */
        Point point1 = new Point(8, 6);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Finish the well */
        Utils.constructHouse(well);

        /* Verify that the unoccupied well produces nothing */
        for (int i = 0; i < 200; i++) {
            assertTrue(well.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testWellworkerIsCreatedWhenNeeded() throws Exception {

        /* Create gamemap */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all well workers from the headquarters */
        Utils.adjustInventoryTo(headquarter, WELL_WORKER, 0);

        /* Place the well */
        Point point1 = new Point(8, 6);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Connect the well with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), well.getFlag());

        /* Finish the well */
        Utils.constructHouse(well);

        /* Verify that the headquarters uses the bucket to create a new well worker */
        WellWorker wellWorker = Utils.waitForWorkerOutsideBuilding(WellWorker.class, player0);

        assertEquals(headquarter.getAmount(WELL_WORKER), 0);
    }

    @Test
    public void testAssignedWellWorkerEntersTheWell() throws Exception {

        /* Create gamemap */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(8, 6);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Connect well with headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter.getFlag());
        courier.assignToRoad(road0);

        /* Finish the well */
        Utils.constructHouse(well);

        /* Run game logic twice, once to place courier and once to place well worker */
        Utils.fastForward(2, map);

        /* Get the well worker */
        Worker wellWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof WellWorker) {
                wellWorker = worker;
            }
        }

        /* Let the well worker reach the well */
        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, well.getPosition());

        assertNotNull(wellWorker);
        assertTrue(wellWorker.isInsideBuilding());
        assertEquals(well.getWorker(), wellWorker);
    }

    @Test
    public void testWellWorkerRests() throws Exception {

        /* Create gamemap */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(8, 6);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Connect the well with the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the well */
        Utils.constructHouse(well);

        /* Assign a worker to the well */
        WellWorker worker = new WellWorker(player0, map);

        Utils.occupyBuilding(worker, well);

        assertTrue(worker.isInsideBuilding());

        /* Verify that the worker rests first without producing anything */
        for (int i = 0; i < 100; i++) {
            assertNull(worker.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testWellWorkerProducesWater() throws Exception {

        /* Create gamemap */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(8, 6);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Connect the well with the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the well */
        Utils.constructHouse(well);

        /* Assign a worker to the well */
        WellWorker worker = new WellWorker(player0, map);

        Utils.occupyBuilding(worker, well);

        assertTrue(worker.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Verify that it the worker produces water at the right time */
        for (int i = 0; i < 50; i++) {
            assertNull(worker.getCargo());
            map.stepTime();
        }

        assertNotNull(worker.getCargo());
        assertEquals(worker.getCargo().getMaterial(), WATER);
    }

    @Test
    public void testWellWorkerPlacesWaterCargoAtTheFlag() throws Exception {

        /* Create gamemap */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(8, 6);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Connect the well with the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the well */
        Utils.constructHouse(well);

        /* Assign a worker to the well */
        WellWorker worker = new WellWorker(player0, map);

        Utils.occupyBuilding(worker, well);

        assertTrue(worker.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the well worker to produce water */
        Utils.fastForward(50, map);

        assertNotNull(worker.getCargo());
        assertEquals(worker.getTarget(), well.getFlag().getPosition());

        /* Let the worker reach the flag and place the cargo */
        assertTrue(well.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, well.getFlag().getPosition());

        assertFalse(well.getFlag().getStackedCargo().isEmpty());

        /* Verify that the water cargo has the right target */
        assertEquals(well.getFlag().getStackedCargo().get(0).getTarget(), headquarter);

        /* Let the worker walk back to the well */
        assertEquals(worker.getTarget(), well.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, worker);

        assertTrue(worker.isInsideBuilding());
    }

    @Test
    public void testWaterCargoIsDeliveredToBakeryWhichIsCloserThanHeadquarters() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Remove all stone from the headquarters */
        Utils.adjustInventoryTo(headquarter, WATER, 0);

        /* Place bakery */
        Point point4 = new Point(10, 4);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point4);

        /* Connect the bakery to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

        /* Wait for the bakery to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(bakery);

        Utils.waitForNonMilitaryBuildingToGetPopulated(bakery);

        /* Place the well */
        Point point1 = new Point(14, 4);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Connect the well with the bakery */
        Road road0 = map.placeAutoSelectedRoad(player0, well.getFlag(), bakery.getFlag());

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(well);

        Utils.waitForNonMilitaryBuildingToGetPopulated(well);

        /* Wait for the courier on the road between the bakery and the well hut to have a water cargo */
        Utils.waitForFlagToGetStackedCargo(map, well.getFlag(), 1);

        assertEquals(well.getFlag().getStackedCargo().get(0).getMaterial(), WATER);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the bakery (and not the headquarters) */
        assertEquals(well.getAmount(WATER), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), bakery.getPosition());

        assertEquals(bakery.getAmount(WATER), 1);
    }

    @Test
    public void testWaterIsNotDeliveredToStorehouseUnderConstruction() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory so that there are no stones, planks or water */
        Utils.adjustInventoryTo(headquarter, PLANK, 0);
        Utils.adjustInventoryTo(headquarter, STONE, 0);
        Utils.adjustInventoryTo(headquarter, WATER, 0);

        /* Place store house */
        Point point4 = new Point(10, 4);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Connect the store house to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Place the well */
        Point point1 = new Point(14, 4);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Connect the well with the store house */
        Road road0 = map.placeAutoSelectedRoad(player0, well.getFlag(), storehouse.getFlag());

        /* Deliver the needed planks to the well */
        Utils.deliverCargos(well, PLANK, 2);

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(well);

        Utils.waitForNonMilitaryBuildingToGetPopulated(well);

        /* Wait for the courier on the road between the store house and the well hut to have a stone cargo */
        Utils.waitForFlagToGetStackedCargo(map, well.getFlag(), 1);

        assertEquals(well.getFlag().getStackedCargo().get(0).getMaterial(), WATER);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the store house's flag so that it can continue to the headquarters */
        assertEquals(headquarter.getAmount(WATER), 0);
        assertEquals(well.getAmount(WATER), 0);
        assertFalse(storehouse.needsMaterial(WATER));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().get(0).getMaterial().equals(WATER));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testWaterIsNotDeliveredTwiceToBuildingThatOnlyNeedsOne() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory so that there are stones but no planks */
        Utils.adjustInventoryTo(headquarter, STONE, 0);

        /* Place bakery */
        Point point4 = new Point(10, 4);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point4);

        /* Construct the bakery */
        Utils.constructHouse(bakery);

        /* Connect the store house to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

        /* Place the well */
        Point point1 = new Point(14, 4);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Connect the well with the bakery */
        Road road0 = map.placeAutoSelectedRoad(player0, well.getFlag(), bakery.getFlag());

        /* Deliver the needed planks to the well */
        Utils.deliverCargos(well, PLANK, 2);

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(well);

        Utils.waitForNonMilitaryBuildingToGetPopulated(well);

        /* Stop production */
        bakery.stopProduction();

        /* Fill up the bakery so there is only space for one more wheat */
        Utils.deliverCargos(bakery, WATER, 5);

        /* Wait for the flag on the road between the bakery and the well to have a water cargo */
        Utils.waitForFlagToGetStackedCargo(map, well.getFlag(), 1);

        assertEquals(well.getFlag().getStackedCargo().get(0).getMaterial(), WATER);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that no water is delivered from the headquarters */
        Utils.adjustInventoryTo(headquarter, WATER, 1);

        assertEquals(bakery.getCanHoldAmount(WATER) - bakery.getAmount(WATER), 1);
        assertFalse(bakery.needsMaterial(WATER));

        for (int i = 0; i < 200; i++) {
            assertNull(headquarter.getWorker().getCargo());

            map.stepTime();
        }

        assertEquals(headquarter.getAmount(WATER), 1);
    }

    @Test
    public void testWellWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place well */
        Point point26 = new Point(8, 8);
        Well well0 = map.placeBuilding(new Well(player0), point26);

        /* Finish construction of the well */
        Utils.constructHouse(well0);

        /* Occupy the well */
        Utils.occupyBuilding(new WellWorker(player0, map), well0);

        /* Let the well worker rest */
        Utils.fastForward(100, map);

        /* Wait for the well worker to produce a new water cargo */
        Utils.fastForward(50, map);

        Worker worker = well0.getWorker();

        assertNotNull(worker.getCargo());

        /* Verify that the well worker puts the water cargo at the flag */
        assertEquals(worker.getTarget(), well0.getFlag().getPosition());
        assertTrue(well0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, well0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(well0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the well */
        assertEquals(worker.getTarget(), well0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, well0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(worker.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(worker.getTarget(), well0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, well0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertEquals(well0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place well */
        Point point26 = new Point(8, 8);
        Well well0 = map.placeBuilding(new Well(player0), point26);

        /* Finish construction of the well */
        Utils.constructHouse(well0);

        /* Occupy the well */
        Utils.occupyBuilding(new WellWorker(player0, map), well0);

        /* Let the well worker rest */
        Utils.fastForward(100, map);

        /* Wait for the well worker to produce a new water cargo */
        Utils.fastForward(50, map);

        Worker worker = well0.getWorker();

        assertNotNull(worker.getCargo());

        /* Verify that the well worker puts the water cargo at the flag */
        assertEquals(worker.getTarget(), well0.getFlag().getPosition());
        assertTrue(well0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, well0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(well0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = well0.getFlag().getStackedCargo().get(0);

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), well0.getFlag().getPosition());

        /* Connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), well0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), well0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));


        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), well0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(WATER);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(WATER), amount + 1);
    }

    @Test
    public void testWellWorkerGoesBackToStorageWhenWellIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place well */
        Point point26 = new Point(8, 8);
        Well well0 = map.placeBuilding(new Well(player0), point26);

        /* Finish construction of the well */
        Utils.constructHouse(well0);

        /* Occupy the well */
        Utils.occupyBuilding(new WellWorker(player0, map), well0);

        /* Destroy the well */
        Worker worker = well0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), well0.getPosition());

        well0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(WELL_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

        /* Verify that the well worker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(WELL_WORKER), amount + 1);
    }

    @Test
    public void testWellWorkerGoesBackOnToStorageOnRoadsIfPossibleWhenWellIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place well */
        Point point26 = new Point(8, 8);
        Well well0 = map.placeBuilding(new Well(player0), point26);

        /* Connect the well with the headquarter */
        map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the well */
        Utils.constructHouse(well0);

        /* Occupy the well */
        Utils.occupyBuilding(new WellWorker(player0, map), well0);

        /* Destroy the well */
        Worker worker = well0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), well0.getPosition());

        well0.tearDown();

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
    public void testDestroyedWellIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place well */
        Point point26 = new Point(8, 8);
        Well well0 = map.placeBuilding(new Well(player0), point26);

        /* Connect the well with the headquarter */
        map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the well */
        Utils.constructHouse(well0);

        /* Destroy the well */
        well0.tearDown();

        assertTrue(well0.isBurningDown());

        /* Wait for the well to stop burning */
        Utils.fastForward(50, map);

        assertTrue(well0.isDestroyed());

        /* Wait for the well to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), well0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(well0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place well */
        Point point26 = new Point(8, 8);
        Well well0 = map.placeBuilding(new Well(player0), point26);

        /* Finish construction of the well */
        Utils.constructHouse(well0);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(well0.getPosition(), well0.getFlag().getPosition()));

        map.removeFlag(well0.getFlag());

        assertNull(map.getRoad(well0.getPosition(), well0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place well */
        Point point26 = new Point(8, 8);
        Well well0 = map.placeBuilding(new Well(player0), point26);

        /* Finish construction of the well */
        Utils.constructHouse(well0);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(well0.getPosition(), well0.getFlag().getPosition()));

        well0.tearDown();

        assertNull(map.getRoad(well0.getPosition(), well0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInWellCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(8, 6);
        Well well0 = map.placeBuilding(new Well(player0), point1);

        /* Connect the well and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the well */
        Utils.constructHouse(well0);

        /* Assign a worker to the well */
        WellWorker worker = new WellWorker(player0, map);

        Utils.occupyBuilding(worker, well0);

        assertTrue(worker.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the well worker to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), WATER);

        /* Wait for the worker to deliver the cargo */
        assertEquals(worker.getTarget(), well0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, well0.getFlag().getPosition());

        /* Stop production and verify that no water is produced */
        well0.stopProduction();

        assertFalse(well0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInWellCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(8, 6);
        Well well0 = map.placeBuilding(new Well(player0), point1);

        /* Connect the well and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the well */
        Utils.constructHouse(well0);

        /* Assign a worker to the well */
        WellWorker worker = new WellWorker(player0, map);

        Utils.occupyBuilding(worker, well0);

        assertTrue(worker.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the well worker to produce water */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), WATER);

        /* Wait for the worker to deliver the cargo */
        assertEquals(worker.getTarget(), well0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, well0.getFlag().getPosition());

        /* Stop production */
        well0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the well produces water again */
        well0.resumeProduction();

        assertTrue(well0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertNotNull(worker.getCargo());
    }

    @Test
    public void testAssignedWellWorkerHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(20, 14);
        Well well0 = map.placeBuilding(new Well(player0), point1);

        /* Finish construction of the well */
        Utils.constructHouse(well0);

        /* Connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), well0.getFlag());

        /* Wait for well worker to get assigned and leave the headquarter */
        List<WellWorker> workers = Utils.waitForWorkersOutsideBuilding(WellWorker.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        WellWorker worker = workers.get(0);

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
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(17, 9);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place well close to the new border */
        Point point4 = new Point(28, 18);
        Well well0 = map.placeBuilding(new Well(player0), point4);

        /* Finish construction of the well */
        Utils.constructHouse(well0);

        /* Occupy the well */
        WellWorker worker = Utils.occupyBuilding(new WellWorker(player0, map), well0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testWellWorkerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

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

        /* Place well */
        Point point2 = new Point(14, 4);
        Well well0 = map.placeBuilding(new Well(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, well0.getFlag());

        /* Wait for the well worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(WellWorker.class, 1, player0);

        WellWorker wellWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof WellWorker) {
                wellWorker = (WellWorker) worker;
            }
        }

        assertNotNull(wellWorker);
        assertEquals(wellWorker.getTarget(), well0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the well worker has started walking */
        assertFalse(wellWorker.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the well worker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, flag0.getPosition());

        assertEquals(wellWorker.getPosition(), flag0.getPosition());

        /* Verify that the well worker returns to the headquarter when it reaches the flag */
        assertEquals(wellWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, headquarter0.getPosition());
    }

    @Test
    public void testWellWorkerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

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

        /* Place well */
        Point point2 = new Point(14, 4);
        Well well0 = map.placeBuilding(new Well(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, well0.getFlag());

        /* Wait for the well worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(WellWorker.class, 1, player0);

        WellWorker wellWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof WellWorker) {
                wellWorker = (WellWorker) worker;
            }
        }

        assertNotNull(wellWorker);
        assertEquals(wellWorker.getTarget(), well0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the well worker has started walking */
        assertFalse(wellWorker.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the well worker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, flag0.getPosition());

        assertEquals(wellWorker.getPosition(), flag0.getPosition());

        /* Verify that the well worker continues to the final flag */
        assertEquals(wellWorker.getTarget(), well0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, well0.getFlag().getPosition());

        /* Verify that the well worker goes out to well instead of going directly back */
        assertNotEquals(wellWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testWellWorkerReturnsToStorageIfWellIsDestroyed() throws Exception {

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

        /* Place well */
        Point point2 = new Point(14, 4);
        Well well0 = map.placeBuilding(new Well(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, well0.getFlag());

        /* Wait for the well worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(WellWorker.class, 1, player0);

        WellWorker wellWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof WellWorker) {
                wellWorker = (WellWorker) worker;
            }
        }

        assertNotNull(wellWorker);
        assertEquals(wellWorker.getTarget(), well0.getPosition());

        /* Wait for the well worker to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, flag0.getPosition());

        map.stepTime();

        /* See that the well worker has started walking */
        assertFalse(wellWorker.isExactlyAtPoint());

        /* Tear down the well */
        well0.tearDown();

        /* Verify that the well worker continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, well0.getFlag().getPosition());

        assertEquals(wellWorker.getPosition(), well0.getFlag().getPosition());

        /* Verify that the well worker goes back to storage */
        assertEquals(wellWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testWellWorkerGoesOffroadBackToClosestStorageWhenWellIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place well */
        Point point26 = new Point(13, 13);
        Well well0 = map.placeBuilding(new Well(player0), point26);

        /* Finish construction of the well */
        Utils.constructHouse(well0);

        /* Occupy the well */
        Utils.occupyBuilding(new WellWorker(player0, map), well0);

        /* Place a second storage closer to the well */
        Point point2 = new Point(7, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Wait for the well worker to be back inside the well */
        Utils.waitForWorkerToBeInside(well0.getWorker(), map);

        /* Destroy the well */
        Worker wellWorker = well0.getWorker();

        assertTrue(wellWorker.isInsideBuilding());
        assertEquals(wellWorker.getPosition(), well0.getPosition());

        well0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(wellWorker.isInsideBuilding());
        assertEquals(wellWorker.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(WELL_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, storehouse0.getPosition());

        /* Verify that the well worker is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(WELL_WORKER), amount + 1);
    }

    @Test
    public void testWellWorkerReturnsOffroadAndAvoidsBurningStorageWhenWellIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place well */
        Point point26 = new Point(13, 13);
        Well well0 = map.placeBuilding(new Well(player0), point26);

        /* Finish construction of the well */
        Utils.constructHouse(well0);

        /* Occupy the well */
        Utils.occupyBuilding(new WellWorker(player0, map), well0);

        /* Place a second storage closer to the well */
        Point point2 = new Point(7, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the well worker to be back inside the well */
        Utils.waitForWorkerToBeInside(well0.getWorker(), map);

        /* Destroy the well */
        Worker wellWorker = well0.getWorker();

        assertTrue(wellWorker.isInsideBuilding());
        assertEquals(wellWorker.getPosition(), well0.getPosition());

        well0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(wellWorker.isInsideBuilding());
        assertEquals(wellWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(WELL_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, headquarter0.getPosition());

        /* Verify that the well worker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(WELL_WORKER), amount + 1);
    }

    @Test
    public void testWellWorkerReturnsOffroadAndAvoidsDestroyedStorageWhenWellIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place well */
        Point point26 = new Point(13, 13);
        Well well0 = map.placeBuilding(new Well(player0), point26);

        /* Finish construction of the well */
        Utils.constructHouse(well0);

        /* Occupy the well */
        Utils.occupyBuilding(new WellWorker(player0, map), well0);

        /* Place a second storage closer to the well */
        Point point2 = new Point(7, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

        /* Destroy the well */
        Worker wellWorker = well0.getWorker();

        assertTrue(wellWorker.isInsideBuilding());
        assertEquals(wellWorker.getPosition(), well0.getPosition());

        well0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(wellWorker.isInsideBuilding());
        assertEquals(wellWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(WELL_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, headquarter0.getPosition());

        /* Verify that the well worker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(WELL_WORKER), amount + 1);
    }

    @Test
    public void testWellWorkerReturnsOffroadAndAvoidsUnfinishedStorageWhenWellIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place well */
        Point point26 = new Point(13, 13);
        Well well0 = map.placeBuilding(new Well(player0), point26);

        /* Finish construction of the well */
        Utils.constructHouse(well0);

        /* Occupy the well */
        Utils.occupyBuilding(new WellWorker(player0, map), well0);

        /* Place a second storage closer to the well */
        Point point2 = new Point(7, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the well */
        Worker wellWorker = well0.getWorker();

        assertTrue(wellWorker.isInsideBuilding());
        assertEquals(wellWorker.getPosition(), well0.getPosition());

        well0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(wellWorker.isInsideBuilding());
        assertEquals(wellWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(WELL_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, headquarter0.getPosition());

        /* Verify that the well worker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(WELL_WORKER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place well */
        Point point26 = new Point(13, 13);
        Well well0 = map.placeBuilding(new Well(player0), point26);

        /* Place road to connect the headquarter and the well */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), well0.getFlag());

        /* Finish construction of the well */
        Utils.constructHouse(well0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(WellWorker.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, well0.getFlag().getPosition());

        /* Tear down the building */
        well0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(worker.getTarget(), well0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, well0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testWellWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a buildable mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithVegetation(point1, BUILDABLE_MOUNTAIN, map);

        /* Place well */
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Finish construction of the well */
        Utils.constructHouse(well);

        /* Populate the well */
        Worker wellWorker0 = Utils.occupyBuilding(new WellWorker(player0, map), well);

        assertTrue(wellWorker0.isInsideBuilding());
        assertEquals(wellWorker0.getHome(), well);
        assertEquals(well.getWorker(), wellWorker0);

        /* Verify that the productivity is 0% when the well doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(well.getFlag().getStackedCargo().isEmpty());
            assertNull(wellWorker0.getCargo());
            assertEquals(well.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testWellWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(7, 9);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Finish construction of the well */
        Utils.constructHouse(well);

        /* Populate the well */
        Worker wellWorker0 = Utils.occupyBuilding(new WellWorker(player0, map), well);

        assertTrue(wellWorker0.isInsideBuilding());
        assertEquals(wellWorker0.getHome(), well);
        assertEquals(well.getWorker(), wellWorker0);

        /* Connect the well with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), well.getFlag());

        /* Make the well produce some water with full resources available */
        for (int i = 0; i < 1000; i++) {
            map.stepTime();
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(well.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            assertEquals(well.getProductivity(), 100);
        }
    }

    @Test
    public void testUnoccupiedWellHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(7, 9);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Finish construction of the well */
        Utils.constructHouse(well);

        /* Verify that the unoccupied well is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(well.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testWellCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(10, 10);
        Well well0 = map.placeBuilding(new Well(player0), point1);

        /* Finish construction of the well */
        Utils.constructHouse(well0);

        /* Populate the well */
        Worker wellWorker0 = Utils.occupyBuilding(new WellWorker(player0, map), well0);

        /* Verify that the well can produce */
        assertTrue(well0.canProduce());
    }

    @Test
    public void testWellReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(6, 12);
        Well well0 = map.placeBuilding(new Well(player0), point1);

        /* Construct the well */
        Utils.constructHouse(well0);

        /* Verify that the reported output is correct */
        assertEquals(well0.getProducedMaterial().length, 1);
        assertEquals(well0.getProducedMaterial()[0], WATER);
    }

    @Test
    public void testWellReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(6, 12);
        Well well0 = map.placeBuilding(new Well(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(well0.getTypesOfMaterialNeeded().size(), 1);
        assertTrue(well0.getTypesOfMaterialNeeded().contains(PLANK));
        assertEquals(well0.getCanHoldAmount(PLANK), 2);

        for (Material material : Material.values()) {
            if (material == PLANK) {
                continue;
            }

            assertEquals(well0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testWellReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(6, 12);
        Well well0 = map.placeBuilding(new Well(player0), point1);

        /* Construct the well */
        Utils.constructHouse(well0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(well0.getTypesOfMaterialNeeded().size(), 0);

        for (Material material : Material.values()) {
            assertEquals(well0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testWellWaitsWhenFlagIsFull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(16, 6);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well.getFlag(), headquarter.getFlag());

        /* Wait for the well to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(well);
        Utils.waitForNonMilitaryBuildingToGetPopulated(well);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, well.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the well waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(well.getFlag().getStackedCargo().size(), 8);
            assertNull(well.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the well with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, well.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(well.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(well.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(well.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, well.getWorker(), WATER);
    }

    @Test
    public void testWellDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(16, 6);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well.getFlag(), headquarter.getFlag());

        /* Wait for the well to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(well);
        Utils.waitForNonMilitaryBuildingToGetPopulated(well);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, well.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The well waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(well.getFlag().getStackedCargo().size(), 8);
            assertNull(well.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the well with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, well.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(well.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(well.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(well.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, well.getWorker(), WATER);

        /* Wait for the worker to put the cargo on the flag */
        assertEquals(well.getWorker().getTarget(), well.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, well.getWorker(), well.getFlag().getPosition());

        assertEquals(well.getFlag().getStackedCargo().size(), 8);

        /* Verify that the well doesn't produce anything because the flag is full */
        for (int i = 0; i < 400; i++) {
            assertEquals(well.getFlag().getStackedCargo().size(), 8);
            assertNull(well.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testWhenWaterDeliveryAreBlockedWellFillsUpFlagAndThenStops() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place Well */
        Point point1 = new Point(7, 9);
        Well well0 = map.placeBuilding(new Well(player0), point1);

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        /* Wait for the well to get constructed and occupied */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(well0);

        Worker wellWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(well0);

        assertTrue(wellWorker0.isInsideBuilding());
        assertEquals(wellWorker0.getHome(), well0);
        assertEquals(well0.getWorker(), wellWorker0);

        /* Block storage of water */
        headquarter0.blockDeliveryOfMaterial(WATER);

        /* Verify that the well puts eight waters on the flag and then stops */
        Utils.waitForFlagToGetStackedCargo(map, well0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker0, well0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(well0.getFlag().getStackedCargo().size(), 8);
            assertTrue(wellWorker0.isInsideBuilding());

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), WATER);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndWellIsTornDown() throws Exception {

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

        /* Place well */
        Point point2 = new Point(18, 6);
        Well well0 = map.placeBuilding(new Well(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the well */
        Road road1 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, well0);

        /* Wait for the well and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, well0);

        Worker wellWorker0 = well0.getWorker();

        assertTrue(wellWorker0.isInsideBuilding());
        assertEquals(wellWorker0.getHome(), well0);
        assertEquals(well0.getWorker(), wellWorker0);

        /* Verify that the worker goes to the storage when the well is torn down */
        headquarter0.blockDeliveryOfMaterial(WELL_WORKER);

        well0.tearDown();

        map.stepTime();

        assertFalse(wellWorker0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker0, well0.getFlag().getPosition());

        assertEquals(wellWorker0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, wellWorker0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(wellWorker0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndWellIsTornDown() throws Exception {

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

        /* Place well */
        Point point2 = new Point(18, 6);
        Well well0 = map.placeBuilding(new Well(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the well */
        Road road1 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, well0);

        /* Wait for the well and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, well0);

        Worker wellWorker0 = well0.getWorker();

        assertTrue(wellWorker0.isInsideBuilding());
        assertEquals(wellWorker0.getHome(), well0);
        assertEquals(well0.getWorker(), wellWorker0);

        /* Verify that the worker goes to the storage off-road when the well is torn down */
        headquarter0.blockDeliveryOfMaterial(WELL_WORKER);

        well0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(wellWorker0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker0, well0.getFlag().getPosition());

        assertEquals(wellWorker0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(wellWorker0));
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
        Utils.adjustInventoryTo(headquarter0, WELL_WORKER, 1);

        assertEquals(headquarter0.getAmount(WELL_WORKER), 1);

        headquarter0.pushOutAll(WELL_WORKER);

        for (int i = 0; i < 10; i++) {
            Worker worker = Utils.waitForWorkerOutsideBuilding(WellWorker.class, player0);

            assertEquals(headquarter0.getAmount(WELL_WORKER), 0);
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
        Utils.adjustInventoryTo(headquarter0, WELL_WORKER, 1);

        headquarter0.blockDeliveryOfMaterial(WELL_WORKER);
        headquarter0.pushOutAll(WELL_WORKER);

        Worker worker = Utils.waitForWorkerOutsideBuilding(WellWorker.class, player0);

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

        /* Place well */
        Point point1 = new Point(7, 9);
        Well well0 = map.placeBuilding(new Well(player0), point1);

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(well0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(well0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter
        */
        headquarter0.blockDeliveryOfMaterial(WELL_WORKER);

        Worker worker = well0.getWorker();

        well0.tearDown();

        assertEquals(worker.getPosition(), well0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, well0.getFlag().getPosition());

        assertEquals(worker.getPosition(), well0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), well0.getPosition());
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

        /* Place well */
        Point point1 = new Point(7, 9);
        Well well0 = map.placeBuilding(new Well(player0), point1);

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well to get constructed */
        Utils.waitForBuildingToBeConstructed(well0);

        /* Wait for a well worker to start walking to the well */
        WellWorker wellWorker = Utils.waitForWorkerOutsideBuilding(WellWorker.class, player0);

        /* Wait for the well worker to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the well worker goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(wellWorker.getTarget(), well0.getPosition());

        headquarter0.blockDeliveryOfMaterial(WELL_WORKER);

        well0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, well0.getFlag().getPosition());

        assertEquals(wellWorker.getPosition(), well0.getFlag().getPosition());
        assertNotEquals(wellWorker.getTarget(), headquarter0.getPosition());
        assertFalse(wellWorker.isInsideBuilding());
        assertNull(well0.getWorker());
        assertNotNull(wellWorker.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, wellWorker.getTarget());

        Point point = wellWorker.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(wellWorker.isDead());
            assertEquals(wellWorker.getPosition(), point);
            assertTrue(map.getWorkers().contains(wellWorker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(wellWorker));
    }
}
