/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Miller;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Mill;
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
public class TestMill {

    @Test
    public void testMillCanHoldSixWheatBarsAndSixWater() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(6, 12);
        var mill0 = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter0.getFlag());

        /* Make sure the headquarters has enough resources */
        Utils.adjustInventoryTo(headquarter0, PLANK, 20);
        Utils.adjustInventoryTo(headquarter0, STONE, 20);
        Utils.adjustInventoryTo(headquarter0, WHEAT, 20);
        Utils.adjustInventoryTo(headquarter0, WATER, 20);
        Utils.adjustInventoryTo(headquarter0, BREWER, 20);

        /* Wait for the mill to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(mill0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(mill0);

        /* Stop production */
        mill0.stopProduction();

        /* Wait for the mill to get six iron bars and six planks */
        Utils.waitForBuildingToGetAmountOfMaterial(mill0, WHEAT, 6);

        /* Verify that the mill doesn't need any more resources and doesn't get any more deliveries */
        assertFalse(mill0.needsMaterial(WHEAT));

        for (int i = 0; i < 2000; i++) {
            assertFalse(mill0.needsMaterial(WHEAT));
            assertEquals(mill0.getAmount(WHEAT), 6);

            map.stepTime();
        }
    }

    @Test
    public void testMillOnlyNeedsTwoPlanksAndTwoStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place mill */
        Point point22 = new Point(6, 12);
        Mill mill0 = map.placeBuilding(new Mill(player0), point22);

        /* Deliver two plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        mill0.putCargo(plankCargo);
        mill0.putCargo(plankCargo);
        mill0.putCargo(stoneCargo);
        mill0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(mill0);

        /* Verify that this is enough to construct the mill */
        for (int i = 0; i < 150; i++) {
            assertTrue(mill0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(mill0.isReady());
    }

    @Test
    public void testMillCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place mill */
        Point point22 = new Point(6, 12);
        Mill mill0 = map.placeBuilding(new Mill(player0), point22);

        /* Deliver one plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        mill0.putCargo(plankCargo);
        mill0.putCargo(stoneCargo);
        mill0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(mill0);

        /* Verify that this is not enough to construct the mill */
        for (int i = 0; i < 500; i++) {
            assertTrue(mill0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(mill0.isReady());
    }

    @Test
    public void testMillCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place mill */
        Point point22 = new Point(6, 12);
        Mill mill0 = map.placeBuilding(new Mill(player0), point22);

        /* Deliver two planks and one stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        mill0.putCargo(plankCargo);
        mill0.putCargo(plankCargo);
        mill0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(mill0);

        /* Verify that this is not enough to construct the mill */
        for (int i = 0; i < 500; i++) {
            assertTrue(mill0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(mill0.isReady());
    }

    @Test
    public void testFinishedMillNeedsWorker() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(8, 6);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Construct the mill */
        Utils.constructHouse(mill);

        /* Verify that the mill needs a worker */
        assertTrue(mill.isReady());
        assertTrue(mill.needsWorker());
    }

    @Test
    public void testMillerIsAssignedToFinishedHouse() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(12, 8);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        /* Finish the mill */
        Utils.constructHouse(mill);

        /* Run game logic twice, once to place courier and once to place miller */
        Utils.fastForward(2, map);

        assertTrue(map.getWorkers().size() >= 3);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Miller.class);
    }

    @Test
    public void testMillerIsNotASoldier() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(12, 8);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        /* Finish the mill */
        Utils.constructHouse(mill);

        /* Wait for a miller to walk out */
        Miller miller0 = Utils.waitForWorkerOutsideBuilding(Miller.class, player0);

        assertNotNull(miller0);

        /* Verify that the miller is not a soldier */
        assertFalse(miller0.isSoldier());
    }

    @Test
    public void testUnoccupiedMillProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(12, 8);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        /* Finish the mill */
        Utils.constructHouse(mill);

        for (int i = 0; i < 200; i++) {
            assertTrue(mill.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testMillerEntersTheMill() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(12, 8);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        /* Place a courier */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter.getFlag());
        courier.assignToRoad(road0);

        /* Finish the mill */
        Utils.constructHouse(mill);

        /* Run game logic twice, once to place courier and once to place miller */
        Utils.fastForward(2, map);

        /* Get the miller */
        Miller miller = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Miller) {
                miller = (Miller) worker;
            }
        }

        /* Let the miller reach the mill */
        Utils.fastForwardUntilWorkerReachesPoint(map, miller, mill.getPosition());

        assertNotNull(miller);
        assertTrue(miller.isInsideBuilding());
        assertEquals(mill.getWorker(), miller);
    }

    @Test
    public void testMillWorkerRests() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(12, 8);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        /* Place a courier */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter.getFlag());
        courier.assignToRoad(road0);

        /* Finish the mill */
        Utils.constructHouse(mill);

        /* Put the miller in the mill */
        Miller miller = new Miller(player0, map);

        Utils.occupyBuilding(miller, mill);

        assertTrue(miller.isInsideBuilding());

        /* Verify that the worker rests first without producing anything */
        for (int i = 0; i < 100; i++) {
            assertNull(miller.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testMillWithoutWheatProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(12, 8);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        /* Place a courier */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter.getFlag());
        courier.assignToRoad(road0);

        /* Finish the mill */
        Utils.constructHouse(mill);

        /* Put the miller in the mill */
        Miller miller = new Miller(player0, map);

        Utils.occupyBuilding(miller, mill);

        assertTrue(miller.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Verify that it the worker doesn't produce any wheat */
        for (int i = 0; i < 200; i++) {
            assertNull(miller.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testMillProducesFlour() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(12, 8);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        /* Place courier */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter.getFlag());
        courier.assignToRoad(road0);

        /* Finish the mill */
        Utils.constructHouse(mill);

        /* Deliver wheat to the mill */
        Cargo cargo = new Cargo(WHEAT, map);

        mill.putCargo(cargo);

        /* Put the worker in the mill */
        Miller miller = new Miller(player0, map);

        Utils.occupyBuilding(miller, mill);

        assertTrue(miller.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Verify that it the worker produces flour at the right time */
        for (int i = 0; i < 50; i++) {
            assertNull(miller.getCargo());
            map.stepTime();
        }

        assertNotNull(miller.getCargo());
        assertEquals(miller.getCargo().getMaterial(), FLOUR);
    }

    @Test
    public void testMillWorkerPlacesFlourCargoAtTheFlag() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(12, 8);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        /* Place a courier on the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter.getFlag());
        courier.assignToRoad(road0);

        /* Finish the mill */
        Utils.constructHouse(mill);

        /* Deliver wheat to the mill */
        Cargo cargo = new Cargo(WHEAT, map);

        mill.putCargo(cargo);

        /* Put the worker in the mill */
        Miller miller = new Miller(player0, map);

        Utils.occupyBuilding(miller, mill);

        assertTrue(miller.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Verify that it the worker produces flour at the right time */
        Utils.fastForward(50, map);

        assertNotNull(miller.getCargo());
        assertEquals(miller.getTarget(), mill.getFlag().getPosition());

        /* Let the worker reach the flag and place the cargo */
        assertTrue(mill.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, miller, mill.getFlag().getPosition());

        assertFalse(mill.getFlag().getStackedCargo().isEmpty());

        /* Let the worker walk back to the mill */
        assertEquals(miller.getTarget(), mill.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, miller);

        assertTrue(miller.isInsideBuilding());
    }

    @Test
    public void testFlourCargoIsDeliveredToGuardHouseUnderConstructionWhichIsCloserThanHeadquarters() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Remove all flour from the headquarters */
        Utils.adjustInventoryTo(headquarter, FLOUR, 0);

        /* Place bakery */
        Point point4 = new Point(10, 4);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point4);

        /* Connect the bakery to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

        /* Place the mill */
        Point point1 = new Point(14, 4);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill with the bakery */
        Road road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), bakery.getFlag());

        /* Wait for the mill to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(mill);

        Utils.waitForNonMilitaryBuildingToGetPopulated(mill);

        /* Wait for the courier on the road between the bakery and the mill hut to have a flour cargo */
        Utils.deliverCargo(mill, WHEAT);

        Utils.waitForFlagToGetStackedCargo(map, mill.getFlag(), 1);

        assertEquals(mill.getFlag().getStackedCargo().getFirst().getMaterial(), FLOUR);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the bakery (and not the headquarters) */
        assertEquals(mill.getAmount(FLOUR), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), bakery.getPosition());

        assertEquals(bakery.getAmount(FLOUR), 1);
    }

    @Test
    public void testFlourIsNotDeliveredToStorehouseUnderConstruction() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory so that there are no stones, planks, or wood */
        Utils.clearInventory(headquarter, PLANK, STONE, FLOUR, WHEAT);

        /* Place storehouse */
        Point point4 = new Point(10, 4);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Connect the storehouse to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Place the mill */
        Point point1 = new Point(14, 4);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill with the storehouse */
        Road road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), storehouse.getFlag());

        /* Deliver the needed material to construct the mill */
        Utils.deliverCargos(mill, PLANK, 2);
        Utils.deliverCargos(mill, STONE, 2);

        /* Wait for the mill to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(mill);

        Utils.waitForNonMilitaryBuildingToGetPopulated(mill);

        /* Wait for the courier on the road between the storehouse and the mill to have a plank cargo */
        Utils.deliverCargo(mill, WHEAT);

        Utils.waitForFlagToGetStackedCargo(map, mill.getFlag(), 1);

        assertEquals(mill.getFlag().getStackedCargo().getFirst().getMaterial(), FLOUR);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the storehouse's flag so that it can continue to the headquarters */
        assertEquals(headquarter.getAmount(FLOUR), 0);
        assertEquals(mill.getAmount(FLOUR), 0);
        assertFalse(storehouse.needsMaterial(FLOUR));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().getFirst().getMaterial().equals(FLOUR));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testFlourIsNotDeliveredTwiceToBuildingThatOnlyNeedsOne() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory so that there are no planks, stones, flour, or wheat */
        Utils.clearInventory(headquarter, PLANK, STONE, FLOUR, WHEAT);

        /* Place bakery */
        Point point4 = new Point(10, 4);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point4);

        /* Construct the bakery */
        Utils.constructHouse(bakery);

        /* Connect the bakery to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

        /* Place the mill */
        Point point1 = new Point(14, 4);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill with the bakery */
        Road road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), bakery.getFlag());

        /* Deliver the needed material to construct the mill */
        Utils.deliverCargos(mill, PLANK, 2);
        Utils.deliverCargos(mill, STONE, 2);

        /* Wait for the mill to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(mill);

        Utils.waitForNonMilitaryBuildingToGetPopulated(mill);

        /* Stop production */
        bakery.stopProduction();

        /* Fill up the bakery so there is only space for one more wheat */
        Utils.deliverCargos(bakery, FLOUR, 5);

        /* Wait for the flag on the road between the bakery and the mill to have a flour cargo */
        Utils.deliverCargo(mill, WHEAT);

        Utils.waitForFlagToGetStackedCargo(map, mill.getFlag(), 1);

        assertEquals(mill.getFlag().getStackedCargo().getFirst().getMaterial(), FLOUR);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that no flour is delivered from the headquarters */
        Utils.adjustInventoryTo(headquarter, FLOUR, 1);

        assertEquals(bakery.getCanHoldAmount(FLOUR) - bakery.getAmount(FLOUR), 1);
        assertFalse(bakery.needsMaterial(FLOUR));

        for (int i = 0; i < 200; i++) {
            assertNull(headquarter.getWorker().getCargo());

            map.stepTime();
        }

        assertEquals(headquarter.getAmount(FLOUR), 1);
    }

    @Test
    public void testMillWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mill */
        Point point26 = new Point(8, 8);
        Mill mill0 = map.placeBuilding(new Mill(player0), point26);

        /* Finish construction of the mill */
        Utils.constructHouse(mill0);

        /* Occupy the mill */
        Utils.occupyBuilding(new Miller(player0, map), mill0);

        /* Deliver material to the mill */
        Cargo wheatCargo = new Cargo(WHEAT, map);

        mill0.putCargo(wheatCargo);
        mill0.putCargo(wheatCargo);

        /* Let the miller rest */
        Utils.fastForward(100, map);

        /* Wait for the miller to produce a new flour cargo */
        Utils.fastForward(50, map);

        Worker worker = mill0.getWorker();

        assertNotNull(worker.getCargo());

        /* Verify that the miller puts the flour cargo at the flag */
        assertEquals(worker.getTarget(), mill0.getFlag().getPosition());
        assertTrue(mill0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mill0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(mill0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the mill */
        assertEquals(worker.getTarget(), mill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mill0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(worker.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(worker.getTarget(), mill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mill0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertEquals(mill0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mill */
        Point point26 = new Point(8, 8);
        Mill mill0 = map.placeBuilding(new Mill(player0), point26);

        /* Finish construction of the mill */
        Utils.constructHouse(mill0);

        /* Deliver material to the mill */
        Cargo wheatCargo = new Cargo(WHEAT, map);

        mill0.putCargo(wheatCargo);
        mill0.putCargo(wheatCargo);

        /* Occupy the mill */
        Utils.occupyBuilding(new Miller(player0, map), mill0);

        /* Let the miller rest */
        Utils.fastForward(100, map);

        /* Wait for the miller to produce a new flour cargo */
        Utils.fastForward(50, map);

        Worker worker = mill0.getWorker();

        assertNotNull(worker.getCargo());

        /* Verify that the miller puts the flour cargo at the flag */
        assertEquals(worker.getTarget(), mill0.getFlag().getPosition());
        assertTrue(mill0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mill0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(mill0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = mill0.getFlag().getStackedCargo().getFirst();

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), mill0.getFlag().getPosition());

        /* Connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mill0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), mill0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), mill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FLOUR);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(FLOUR), amount + 1);
    }

    @Test
    public void testMillerGoesBackToStorageWhenMillIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mill */
        Point point26 = new Point(8, 8);
        Mill mill0 = map.placeBuilding(new Mill(player0), point26);

        /* Finish construction of the mill */
        Utils.constructHouse(mill0);

        /* Occupy the mill */
        Utils.occupyBuilding(new Miller(player0, map), mill0);

        /* Destroy the mill */
        Worker worker = mill0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), mill0.getPosition());

        mill0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MILLER);

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

        /* Verify that the miller is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MILLER), amount + 1);
    }

    @Test
    public void testMillerGoesBackOnToStorageOnRoadsIfPossibleWhenMillIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mill */
        Point point26 = new Point(8, 8);
        Mill mill0 = map.placeBuilding(new Mill(player0), point26);

        /* Connect the mill with the headquarter */
        map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the mill */
        Utils.constructHouse(mill0);

        /* Occupy the mill */
        Utils.occupyBuilding(new Miller(player0, map), mill0);

        /* Destroy the mill */
        Worker worker = mill0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), mill0.getPosition());

        mill0.tearDown();

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
    public void testDestroyedMillIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mill */
        Point point26 = new Point(8, 8);
        Mill mill0 = map.placeBuilding(new Mill(player0), point26);

        /* Connect the mill with the headquarter */
        map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the mill */
        Utils.constructHouse(mill0);

        /* Destroy the mill */
        mill0.tearDown();

        assertTrue(mill0.isBurningDown());

        /* Wait for the mill to stop burning */
        Utils.fastForward(50, map);

        assertTrue(mill0.isDestroyed());

        /* Wait for the mill to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), mill0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(mill0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mill */
        Point point26 = new Point(8, 8);
        Mill mill0 = map.placeBuilding(new Mill(player0), point26);

        /* Finish construction of the mill */
        Utils.constructHouse(mill0);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(mill0.getPosition(), mill0.getFlag().getPosition()));

        map.removeFlag(mill0.getFlag());

        assertNull(map.getRoad(mill0.getPosition(), mill0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mill */
        Point point26 = new Point(8, 8);
        Mill mill0 = map.placeBuilding(new Mill(player0), point26);

        /* Finish construction of the mill */
        Utils.constructHouse(mill0);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(mill0.getPosition(), mill0.getFlag().getPosition()));

        mill0.tearDown();

        assertNull(map.getRoad(mill0.getPosition(), mill0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInMillCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(12, 8);
        Mill mill0 = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter.getFlag());

        /* Finish the mill */
        Utils.constructHouse(mill0);

        /* Deliver material to the mill */
        Cargo wheatCargo = new Cargo(WHEAT, map);

        mill0.putCargo(wheatCargo);
        mill0.putCargo(wheatCargo);

        /* Assign a worker to the mill */
        Miller worker = new Miller(player0, map);

        Utils.occupyBuilding(worker, mill0);

        assertTrue(worker.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the miller to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), FLOUR);

        /* Wait for the worker to deliver the cargo */
        assertEquals(worker.getTarget(), mill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mill0.getFlag().getPosition());

        /* Stop production and verify that no flour is produced */
        mill0.stopProduction();

        assertFalse(mill0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInMillCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(12, 8);
        Mill mill0 = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter.getFlag());

        /* Finish the mill */
        Utils.constructHouse(mill0);

        /* Assign a worker to the mill */
        Miller worker = new Miller(player0, map);

        Utils.occupyBuilding(worker, mill0);

        assertTrue(worker.isInsideBuilding());

        /* Deliver material to the mill */
        Cargo wheatCargo = new Cargo(WHEAT, map);

        mill0.putCargo(wheatCargo);
        mill0.putCargo(wheatCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the miller to produce flour */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), FLOUR);

        /* Wait for the worker to deliver the cargo */
        assertEquals(worker.getTarget(), mill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mill0.getFlag().getPosition());

        /* Stop production */
        mill0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the mill produces flour again */
        mill0.resumeProduction();

        assertTrue(mill0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertNotNull(worker.getCargo());
    }

    @Test
    public void testAssignedMillerHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(20, 14);
        Mill mill0 = map.placeBuilding(new Mill(player0), point1);

        /* Finish construction of the mill */
        Utils.constructHouse(mill0);

        /* Connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mill0.getFlag());

        /* Wait for miller to get assigned and leave the headquarter */
        List<Miller> workers = Utils.waitForWorkersOutsideBuilding(Miller.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Miller worker = workers.getFirst();

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        Player player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

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

        /* Place mill close to the new border */
        Point point4 = new Point(28, 18);
        Mill mill0 = map.placeBuilding(new Mill(player0), point4);

        /* Finish construction of the mill */
        Utils.constructHouse(mill0);

        /* Occupy the mill */
        Miller worker = Utils.occupyBuilding(new Miller(player0, map), mill0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMillerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place mill */
        Point point2 = new Point(14, 4);
        Mill mill0 = map.placeBuilding(new Mill(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, mill0.getFlag());

        /* Wait for the miller to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Miller.class, 1, player0);

        Miller miller = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Miller) {
                miller = (Miller) worker;
            }
        }

        assertNotNull(miller);
        assertEquals(miller.getTarget(), mill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miller, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the miller has started walking */
        assertFalse(miller.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the miller continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miller, flag0.getPosition());

        assertEquals(miller.getPosition(), flag0.getPosition());

        /* Verify that the miller returns to the headquarter when it reaches the flag */
        assertEquals(miller.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miller, headquarter0.getPosition());
    }

    @Test
    public void testMillerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place mill */
        Point point2 = new Point(14, 4);
        Mill mill0 = map.placeBuilding(new Mill(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, mill0.getFlag());

        /* Wait for the miller to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Miller.class, 1, player0);

        Miller miller = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Miller) {
                miller = (Miller) worker;
            }
        }

        assertNotNull(miller);
        assertEquals(miller.getTarget(), mill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miller, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the miller has started walking */
        assertFalse(miller.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the miller continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miller, flag0.getPosition());

        assertEquals(miller.getPosition(), flag0.getPosition());

        /* Verify that the miller continues to the final flag */
        assertEquals(miller.getTarget(), mill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miller, mill0.getFlag().getPosition());

        /* Verify that the miller goes out to miller instead of going directly back */
        assertNotEquals(miller.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMillerReturnsToStorageIfMillIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place mill */
        Point point2 = new Point(14, 4);
        Mill mill0 = map.placeBuilding(new Mill(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, mill0.getFlag());

        /* Wait for the miller to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Miller.class, 1, player0);

        Miller miller = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Miller) {
                miller = (Miller) worker;
            }
        }

        assertNotNull(miller);
        assertEquals(miller.getTarget(), mill0.getPosition());

        /* Wait for the miller to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miller, flag0.getPosition());

        map.stepTime();

        /* See that the miller has started walking */
        assertFalse(miller.isExactlyAtPoint());

        /* Tear down the mill */
        mill0.tearDown();

        /* Verify that the miller continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miller, mill0.getFlag().getPosition());

        assertEquals(miller.getPosition(), mill0.getFlag().getPosition());

        /* Verify that the miller goes back to storage */
        assertEquals(miller.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMillerGoesOffroadBackToClosestStorageWhenMillIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mill */
        Point point26 = new Point(17, 17);
        Mill mill0 = map.placeBuilding(new Mill(player0), point26);

        /* Finish construction of the mill */
        Utils.constructHouse(mill0);

        /* Occupy the mill */
        Utils.occupyBuilding(new Miller(player0, map), mill0);

        /* Place a second storage closer to the mill */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the mill */
        Worker miller = mill0.getWorker();

        assertTrue(miller.isInsideBuilding());
        assertEquals(miller.getPosition(), mill0.getPosition());

        mill0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miller.isInsideBuilding());
        assertEquals(miller.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(MILLER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miller, storehouse0.getPosition());

        /* Verify that the miller is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(MILLER), amount + 1);
    }

    @Test
    public void testMillerReturnsOffroadAndAvoidsBurningStorageWhenMillIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mill */
        Point point26 = new Point(17, 17);
        Mill mill0 = map.placeBuilding(new Mill(player0), point26);

        /* Finish construction of the mill */
        Utils.constructHouse(mill0);

        /* Occupy the mill */
        Utils.occupyBuilding(new Miller(player0, map), mill0);

        /* Place a second storage closer to the mill */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Destroy the mill */
        Worker miller = mill0.getWorker();

        assertTrue(miller.isInsideBuilding());
        assertEquals(miller.getPosition(), mill0.getPosition());

        mill0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miller.isInsideBuilding());
        assertEquals(miller.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MILLER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miller, headquarter0.getPosition());

        /* Verify that the miller is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MILLER), amount + 1);
    }

    @Test
    public void testMillerReturnsOffroadAndAvoidsDestroyedStorageWhenMillIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mill */
        Point point26 = new Point(17, 17);
        Mill mill0 = map.placeBuilding(new Mill(player0), point26);

        /* Finish construction of the mill */
        Utils.constructHouse(mill0);

        /* Occupy the mill */
        Utils.occupyBuilding(new Miller(player0, map), mill0);

        /* Place a second storage closer to the mill */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

        /* Destroy the mill */
        Worker miller = mill0.getWorker();

        assertTrue(miller.isInsideBuilding());
        assertEquals(miller.getPosition(), mill0.getPosition());

        mill0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miller.isInsideBuilding());
        assertEquals(miller.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MILLER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miller, headquarter0.getPosition());

        /* Verify that the miller is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MILLER), amount + 1);
    }

    @Test
    public void testMillerReturnsOffroadAndAvoidsUnfinishedStorageWhenMillIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mill */
        Point point26 = new Point(17, 17);
        Mill mill0 = map.placeBuilding(new Mill(player0), point26);

        /* Finish construction of the mill */
        Utils.constructHouse(mill0);

        /* Occupy the mill */
        Utils.occupyBuilding(new Miller(player0, map), mill0);

        /* Place a second storage closer to the mill */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the mill */
        Worker miller = mill0.getWorker();

        assertTrue(miller.isInsideBuilding());
        assertEquals(miller.getPosition(), mill0.getPosition());

        mill0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miller.isInsideBuilding());
        assertEquals(miller.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MILLER);

        Utils.fastForwardUntilWorkerReachesPoint(map, miller, headquarter0.getPosition());

        /* Verify that the miller is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MILLER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place mill */
        Point point26 = new Point(17, 17);
        Mill mill0 = map.placeBuilding(new Mill(player0), point26);

        /* Place road to connect the headquarter and the mill */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mill0.getFlag());

        /* Finish construction of the mill */
        Utils.constructHouse(mill0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Miller.class, 1, player0).getFirst();

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mill0.getFlag().getPosition());

        /* Tear down the building */
        mill0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(worker.getTarget(), mill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mill0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testMillWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(7, 9);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Finish construction of the mill */
        Utils.constructHouse(mill);

        /* Populate the mill */
        Worker miller0 = Utils.occupyBuilding(new Miller(player0, map), mill);

        assertTrue(miller0.isInsideBuilding());
        assertEquals(miller0.getHome(), mill);
        assertEquals(mill.getWorker(), miller0);

        /* Verify that the productivity is 0% when the mill doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(mill.getFlag().getStackedCargo().isEmpty());
            assertNull(miller0.getCargo());
            assertEquals(mill.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testMillWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(7, 9);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Finish construction of the mill */
        Utils.constructHouse(mill);

        /* Populate the mill */
        Worker miller0 = Utils.occupyBuilding(new Miller(player0, map), mill);

        assertTrue(miller0.isInsideBuilding());
        assertEquals(miller0.getHome(), mill);
        assertEquals(mill.getWorker(), miller0);

        /* Connect the mill with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mill.getFlag());

        /* Make the mill produce some flour with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (mill.needsMaterial(WHEAT) && mill.getAmount(WHEAT) < 2) {
                mill.putCargo(new Cargo(WHEAT, map));
            }
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(mill.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (mill.needsMaterial(WHEAT) && mill.getAmount(WHEAT) < 2) {
                mill.putCargo(new Cargo(WHEAT, map));
            }

            assertEquals(mill.getProductivity(), 100);
        }
    }

    @Test
    public void testMillLosesProductivityWhenResourcesRunOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(7, 9);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Finish construction of the mill */
        Utils.constructHouse(mill);

        /* Populate the mill */
        Worker miller0 = Utils.occupyBuilding(new Miller(player0, map), mill);

        assertTrue(miller0.isInsideBuilding());
        assertEquals(miller0.getHome(), mill);
        assertEquals(mill.getWorker(), miller0);

        /* Connect the mill with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mill.getFlag());

        /* Make the mill produce some flour with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (mill.needsMaterial(WHEAT) && mill.getAmount(WHEAT) < 2) {
                mill.putCargo(new Cargo(WHEAT, map));
            }
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(mill.getProductivity(), 100);

        for (int i = 0; i < 5000; i++) {
            map.stepTime();
        }

        assertEquals(mill.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedMillHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(7, 9);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Finish construction of the mill */
        Utils.constructHouse(mill);

        /* Verify that the unoccupied mill is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(mill.getProductivity(), 0);

            if (mill.needsMaterial(WHEAT) && mill.getAmount(WHEAT) < 2) {
                mill.putCargo(new Cargo(WHEAT, map));
            }

            map.stepTime();
        }
    }

    @Test
    public void testMillCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(10, 10);
        Mill mill0 = map.placeBuilding(new Mill(player0), point1);

        /* Finish construction of the mill */
        Utils.constructHouse(mill0);

        /* Populate the mill */
        Worker miller0 = Utils.occupyBuilding(new Miller(player0, map), mill0);

        /* Verify that the mill can produce */
        assertTrue(mill0.canProduce());
    }

    @Test
    public void testMillReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(6, 12);
        Mill mill0 = map.placeBuilding(new Mill(player0), point1);

        /* Construct the mill */
        Utils.constructHouse(mill0);

        /* Verify that the reported output is correct */
        assertEquals(mill0.getProducedMaterial().length, 1);
        assertEquals(mill0.getProducedMaterial()[0], FLOUR);
    }

    @Test
    public void testMillReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(6, 12);
        Mill mill0 = map.placeBuilding(new Mill(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(mill0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(mill0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(mill0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(mill0.getCanHoldAmount(PLANK), 2);
        assertEquals(mill0.getCanHoldAmount(STONE), 2);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(mill0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testMillReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(6, 12);
        Mill mill0 = map.placeBuilding(new Mill(player0), point1);

        /* Construct the mill */
        Utils.constructHouse(mill0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(mill0.getTypesOfMaterialNeeded().size(), 1);
        assertTrue(mill0.getTypesOfMaterialNeeded().contains(WHEAT));
        assertEquals(mill0.getCanHoldAmount(WHEAT), 6);

        for (Material material : Material.values()) {
            if (material == WHEAT) {
                continue;
            }

            assertEquals(mill0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testMillWaitsWhenFlagIsFull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(16, 6);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        /* Wait for the mill to get constructed and assigned a miller */
        Utils.waitForBuildingToBeConstructed(mill);
        Utils.waitForNonMilitaryBuildingToGetPopulated(mill);

        /* Give wheat to the mill */
        Utils.putCargoToBuilding(mill, WHEAT);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, mill.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the mill waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(mill.getFlag().getStackedCargo().size(), 8);
            assertNull(mill.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the mill with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(mill.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(mill.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(mill.getFlag().getStackedCargo().size(), 7);

        /* Verify that the miller produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, mill.getWorker(), FLOUR);
    }

    @Test
    public void testMillDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(16, 6);
        Mill mill = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        /* Wait for the mill to get constructed and assigned a miller */
        Utils.waitForBuildingToBeConstructed(mill);
        Utils.waitForNonMilitaryBuildingToGetPopulated(mill);

        /* Give wheat to the mill */
        Utils.putCargoToBuilding(mill, WHEAT);
        Utils.putCargoToBuilding(mill, WHEAT);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, mill.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The mill waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(mill.getFlag().getStackedCargo().size(), 8);
            assertNull(mill.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the mill with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(mill.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(mill.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(mill.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The miller produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, mill.getWorker(), FLOUR);

        /* Wait for the miller to put the cargo on the flag */
        assertEquals(mill.getWorker().getTarget(), mill.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, mill.getWorker(), mill.getFlag().getPosition());

        assertEquals(mill.getFlag().getStackedCargo().size(), 8);

        /* Verify that the mill doesn't produce anything because the flag is full */
        for (int i = 0; i < 400; i++) {
            assertEquals(mill.getFlag().getStackedCargo().size(), 8);
            assertNull(mill.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testWhenFlourDeliveryAreBlockedMillFillsUpFlagAndThenStops() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place Mill */
        Point point1 = new Point(7, 9);
        Mill mill0 = map.placeBuilding(new Mill(player0), point1);

        /* Place road to connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter0.getFlag());

        /* Wait for the mill to get constructed and occupied */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(mill0);

        Worker miller0 = Utils.waitForNonMilitaryBuildingToGetPopulated(mill0);

        assertTrue(miller0.isInsideBuilding());
        assertEquals(miller0.getHome(), mill0);
        assertEquals(mill0.getWorker(), miller0);

        /* Add a lot of material to the headquarter for the mill to consume */
        Utils.adjustInventoryTo(headquarter0, WHEAT, 40);

        /* Block storage of flour */
        headquarter0.blockDeliveryOfMaterial(FLOUR);

        /* Verify that the mill puts eight flour bags on the flag and then stops */
        Utils.waitForFlagToGetStackedCargo(map, mill0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, miller0, mill0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(mill0.getFlag().getStackedCargo().size(), 8);
            assertTrue(miller0.isInsideBuilding());

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), FLOUR);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndMillIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place mill */
        Point point2 = new Point(18, 6);
        Mill mill0 = map.placeBuilding(new Mill(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the mill */
        Road road1 = map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the mill and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, mill0);

        /* Add a lot of material to the headquarter for the mill to consume */
        Utils.adjustInventoryTo(headquarter0, WHEAT, 40);

        /* Wait for the mill and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, mill0);

        Worker miller0 = mill0.getWorker();

        assertTrue(miller0.isInsideBuilding());
        assertEquals(miller0.getHome(), mill0);
        assertEquals(mill0.getWorker(), miller0);

        /* Verify that the worker goes to the storage when the mill is torn down */
        headquarter0.blockDeliveryOfMaterial(MILLER);

        mill0.tearDown();

        map.stepTime();

        assertFalse(miller0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, miller0, mill0.getFlag().getPosition());

        assertEquals(miller0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, miller0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(miller0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndMillIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place mill */
        Point point2 = new Point(18, 6);
        Mill mill0 = map.placeBuilding(new Mill(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the mill */
        Road road1 = map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the mill and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, mill0);

        /* Add a lot of material to the headquarter for the mill to consume */
        Utils.adjustInventoryTo(headquarter0, WHEAT, 40);

        /* Wait for the mill and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, mill0);

        Worker miller0 = mill0.getWorker();

        assertTrue(miller0.isInsideBuilding());
        assertEquals(miller0.getHome(), mill0);
        assertEquals(mill0.getWorker(), miller0);

        /* Verify that the worker goes to the storage off-road when the mill is torn down */
        headquarter0.blockDeliveryOfMaterial(MILLER);

        mill0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(miller0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, miller0, mill0.getFlag().getPosition());

        assertEquals(miller0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miller0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(miller0));
    }

    @Test
    public void testWorkerGoesOutAndBackInWhenSentOutWithoutBlocking() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, MILLER, 1);

        assertEquals(headquarter0.getAmount(MILLER), 1);

        headquarter0.pushOutAll(MILLER);

        for (int i = 0; i < 10; i++) {
            Worker worker = Utils.waitForWorkerOutsideBuilding(Miller.class, player0);

            assertEquals(headquarter0.getAmount(MILLER), 0);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, MILLER, 1);

        headquarter0.blockDeliveryOfMaterial(MILLER);
        headquarter0.pushOutAll(MILLER);

        Worker worker = Utils.waitForWorkerOutsideBuilding(Miller.class, player0);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(7, 9);
        Mill mill0 = map.placeBuilding(new Mill(player0), point1);

        /* Place road to connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the mill to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(mill0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(mill0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter
        */
        headquarter0.blockDeliveryOfMaterial(MILLER);

        Worker worker = mill0.getWorker();

        mill0.tearDown();

        assertEquals(worker.getPosition(), mill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, mill0.getFlag().getPosition());

        assertEquals(worker.getPosition(), mill0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), mill0.getPosition());
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(7, 9);
        Mill mill0 = map.placeBuilding(new Mill(player0), point1);

        /* Place road to connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the mill to get constructed */
        Utils.waitForBuildingToBeConstructed(mill0);

        /* Wait for a miller to start walking to the mill */
        Miller miller = Utils.waitForWorkerOutsideBuilding(Miller.class, player0);

        /* Wait for the miller to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, miller, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the miller goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(miller.getTarget(), mill0.getPosition());

        headquarter0.blockDeliveryOfMaterial(MILLER);

        mill0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, miller, mill0.getFlag().getPosition());

        assertEquals(miller.getPosition(), mill0.getFlag().getPosition());
        assertNotEquals(miller.getTarget(), headquarter0.getPosition());
        assertFalse(miller.isInsideBuilding());
        assertNull(mill0.getWorker());
        assertNotNull(miller.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, miller, miller.getTarget());

        Point point = miller.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(miller.isDead());
            assertEquals(miller.getPosition(), point);
            assertTrue(map.getWorkers().contains(miller));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(miller));
    }

    @Test
    public void testWorkerCanDeliverAfterHavingWaited() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(7, 9);
        Mill mill0 = map.placeBuilding(new Mill(player0), point1);

        /* Place road to connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter0.getFlag());

        /* Wait for the mill to get constructed and populated */
        Utils.waitForBuildingToBeConstructed(mill0);

        Utils.waitForNonMilitaryBuildingsToGetPopulated(mill0);

        /* Fill the mill's flag */
        Utils.placeCargos(map, GOLD, 8, mill0.getFlag(), headquarter0);

        /* Remove the road */
        map.removeRoad(road0);

        /* Give the miller wheat to mill */
        Utils.deliverCargo(mill0, WHEAT);

        /* Wait for the miller to produce flour */
        Utils.verifyWorkerStaysAtHome(mill0.getWorker(), map);

        assertEquals(mill0.getFlag().getStackedCargo().size(), 8);

        /* Remove one cargo so the miller can deliver */
        Utils.retrieveOneCargo(mill0.getFlag());

        assertEquals(mill0.getFlag().getStackedCargo().size(), 7);

        /* Wait for the miller to deliver its cargo */
        map.stepTime();

        assertFalse(mill0.getWorker().isInsideBuilding());
        assertNotNull(mill0.getWorker().getCargo());
        assertEquals(mill0.getWorker().getTarget(), mill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, mill0.getWorker(), mill0.getFlag().getPosition());

        assertEquals(mill0.getFlag().getStackedCargo().size(), 8);
        assertEquals(mill0.getWorker().getTarget(), mill0.getPosition());
        assertNull(mill0.getWorker().getCargo());

        Utils.fastForwardUntilWorkerReachesPoint(map, mill0.getWorker(), mill0.getPosition());

        /* Make the miller produce one more cargo and check that it can't be delivered */
        Utils.deliverCargo(mill0, WHEAT);

        Utils.verifyWorkerStaysAtHome(mill0.getWorker(), map);

        /* Verify that the miller can deliver when one of the cargos is removed */
        Utils.retrieveOneCargo(mill0.getFlag());

        assertEquals(mill0.getFlag().getStackedCargo().size(), 7);

        Utils.fastForwardUntilWorkerCarriesCargo(map, mill0.getWorker());

        assertFalse(mill0.getWorker().isInsideBuilding());
        assertNotNull(mill0.getWorker().getCargo());
        assertEquals(mill0.getWorker().getTarget(), mill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, mill0.getWorker(), mill0.getFlag().getPosition());

        assertEquals(mill0.getFlag().getStackedCargo().size(), 8);
        assertEquals(mill0.getWorker().getTarget(), mill0.getPosition());
        assertNull(mill0.getWorker().getCargo());
    }
}
