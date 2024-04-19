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
import org.appland.settlers.model.actors.Baker;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.Headquarter;
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
public class TestBakery {

    @Test
    public void testBakeryOnlyNeedsTwoPlanksAndTwoStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place bakery */
        Point point22 = new Point(6, 12);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point22);

        /* Deliver two plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        bakery0.putCargo(plankCargo);
        bakery0.putCargo(plankCargo);
        bakery0.putCargo(stoneCargo);
        bakery0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(bakery0);

        /* Verify that this is enough to construct the bakery */
        for (int i = 0; i < 150; i++) {
            assertTrue(bakery0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(bakery0.isReady());
    }

    @Test
    public void testBakeryCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place bakery */
        Point point22 = new Point(6, 12);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point22);

        /* Deliver one plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        bakery0.putCargo(plankCargo);
        bakery0.putCargo(stoneCargo);
        bakery0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(bakery0);

        /* Verify that this is not enough to construct the bakery */
        for (int i = 0; i < 500; i++) {
            assertTrue(bakery0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(bakery0.isReady());
    }

    @Test
    public void testBakeryCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place bakery */
        Point point22 = new Point(6, 12);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point22);

        /* Deliver two planks and one stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        bakery0.putCargo(plankCargo);
        bakery0.putCargo(plankCargo);
        bakery0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(bakery0);

        /* Verify that this is not enough to construct the bakery */
        for (int i = 0; i < 500; i++) {
            assertTrue(bakery0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(bakery0.isReady());
    }

    @Test
    public void testBakeryNeedsWorker() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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
        Road road0 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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
        Road road0 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        assertTrue(bakery.needsWorker());

        /* Verify that a bakery worker leaves the headquarter */
        Utils.fastForward(3, map);

        assertTrue(map.getWorkers().size() >= 3);

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
    public void testBakeryIsNotASoldier() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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
        Road road0 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        assertTrue(bakery.needsWorker());

        /* Verify that a bakery worker leaves the headquarter */
        Utils.fastForward(3, map);

        assertTrue(map.getWorkers().size() >= 3);

        /* Verify that the baker is not a soldier */
        Baker baker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Baker) {
                baker = (Baker)worker;
            }
        }

        assertNotNull(baker);
        assertFalse(baker.isSoldier());
    }

    @Test
    public void testBakerIsCreatedFromRollingPing() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all bakers from the headquarter and add one rolling pin */
        Utils.adjustInventoryTo(headquarter, BAKER, 0);
        Utils.adjustInventoryTo(headquarter, Material.ROLLING_PIN, 1);

        /* Place bakery */
        Point point3 = new Point(7, 9);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point3);

        /* Connect the bakery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        assertTrue(bakery.needsWorker());

        /* Verify that a bakery worker leaves the headquarter */
        Utils.fastForward(3, map);

        assertTrue(map.getWorkers().size() >= 3);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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
        Road road0 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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
        Road road0 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

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
    public void testBreadCargoIsDeliveredToMineWhichIsCloserThanHeadquarters() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Remove all bread from the headquarters */
        Utils.adjustInventoryTo(headquarter, BREAD, 0);

        /* Place a small mountain */
        Point point4 = new Point(10, 4);
        Utils.surroundPointWithMinableMountain(point4, map);

        /* Place coal mine */
        CoalMine coalMine = map.placeBuilding(new CoalMine(player0), point4);

        /* Connect the coal mine to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, coalMine.getFlag(), headquarter.getFlag());

        /* Wait for the coal mine to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(coalMine);

        Utils.waitForNonMilitaryBuildingToGetPopulated(coalMine);

        /* Place the bakery */
        Point point1 = new Point(14, 4);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point1);

        /* Connect the bakery with the coal mine */
        Road road0 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), coalMine.getFlag());

        /* Wait for the bakery to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(bakery);

        Utils.waitForNonMilitaryBuildingToGetPopulated(bakery);

        /* Wait for the courier on the road between the coal mine and the bakery hut to have a bread cargo */
        Utils.adjustInventoryTo(headquarter, WATER, 2);
        Utils.adjustInventoryTo(headquarter, FLOUR, 2);

        Utils.waitForFlagToGetStackedCargo(map, bakery.getFlag(), 1);

        assertEquals(bakery.getFlag().getStackedCargo().getFirst().getMaterial(), BREAD);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the coal mine (and not the headquarters) */
        assertEquals(bakery.getAmount(BREAD), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), coalMine.getPosition());

        assertEquals(coalMine.getAmount(BREAD), 1);
    }

    @Test
    public void testBreadIsNotDeliveredToStorehouseUnderConstruction() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory */
        Utils.clearInventory(headquarter, STONE, PLANK, WATER, FLOUR, BREAD);

        /* Place storehouse */
        Point point4 = new Point(10, 4);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Connect the storehouse to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Place the bakery */
        Point point1 = new Point(14, 4);
        var bakery = map.placeBuilding(new Bakery(player0), point1);

        /* Connect the bakery with the storehouse */
        Road road0 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), storehouse.getFlag());

        /* Deliver the needed material to construct the bakery */
        Utils.deliverCargos(bakery, PLANK, 2);
        Utils.deliverCargos(bakery, STONE, 2);

        /* Wait for the bakery to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(bakery);

        Utils.waitForNonMilitaryBuildingToGetPopulated(bakery);

        /* Wait for the courier on the road between the storehouse and the bakery to have a plank cargo */
        Utils.deliverCargos(bakery, WATER, FLOUR);

        Utils.waitForFlagToGetStackedCargo(map, bakery.getFlag(), 1);

        assertEquals(bakery.getFlag().getStackedCargo().getFirst().getMaterial(), BREAD);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the storehouse's flag so that it can continue to the headquarters */
        assertEquals(headquarter.getAmount(BREAD), 0);
        assertEquals(bakery.getAmount(BREAD), 0);
        assertFalse(storehouse.needsMaterial(BREAD));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().getFirst().getMaterial().equals(BREAD));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testBreadIsNotDeliveredTwiceToBuildingThatOnlyNeedsOne() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory */
        Utils.clearInventory(headquarter, PLANK ,STONE, BREAD, MEAT, FISH, WATER, FLOUR);

        /* Place mountain */
        Point point4 = new Point(10, 4);
        Utils.surroundPointWithMinableMountain(point4, map);

        /* Place gold mine */
        GoldMine goldMine = map.placeBuilding(new GoldMine(player0), point4);

        /* Connect the gold mine to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, goldMine.getFlag(), headquarter.getFlag());

        /* Construct the gold mine */
        Utils.constructHouse(goldMine);

        /* Place the bakery */
        Point point1 = new Point(14, 4);
        var bakery = map.placeBuilding(new Bakery(player0), point1);

        /* Connect the bakery with the gold mine */
        Road road0 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), goldMine.getFlag());

        /* Deliver the needed material to construct the bakery */
        Utils.deliverCargos(bakery, PLANK, 2);
        Utils.deliverCargos(bakery, STONE, 2);

        /* Wait for the bakery to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(bakery);

        Utils.waitForNonMilitaryBuildingToGetPopulated(bakery);

        /* Fill up the gold mine so there is only space for one more bread */
        Utils.deliverCargo(goldMine, BREAD);

        /* Stop production */
        goldMine.stopProduction();

        /* Wait for the flag on the road between the gold mine and the bakery to have a plank cargo */
        Utils.deliverCargos(bakery, WATER, FLOUR);

        Utils.waitForFlagToGetStackedCargo(map, bakery.getFlag(), 1);

        assertEquals(bakery.getFlag().getStackedCargo().getFirst().getMaterial(), BREAD);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that no stone is delivered from the headquarters */
        Utils.adjustInventoryTo(headquarter, BREAD, 1);

        assertEquals(goldMine.getCanHoldAmount(BREAD) - goldMine.getAmount(BREAD), 1);
        assertFalse(goldMine.needsMaterial(BREAD));

        for (int i = 0; i < 200; i++) {
            assertNull(headquarter.getWorker().getCargo());

            map.stepTime();
        }

        assertEquals(headquarter.getAmount(BREAD), 1);
    }

    @Test
    public void testProductionOfOneBreadConsumesOneWaterAndOneFlour() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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

        /* Fast forward so that the bakery worker would have produced bread if it had had the ingredients */
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place bakery */
        Point point26 = new Point(8, 8);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point26);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place bakery */
        Point point26 = new Point(8, 8);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point26);

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
        Cargo cargo = bakery0.getFlag().getStackedCargo().getFirst();

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place bakery */
        Point point26 = new Point(8, 8);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point26);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place bakery */
        Point point26 = new Point(8, 8);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point26);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(12, 8);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point1);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(12, 8);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point1);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(20, 14);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point1);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Connect the bakery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), bakery0.getFlag());

        /* Wait for baker to get assigned and leave the headquarter */
        List<Baker> workers = Utils.waitForWorkersOutsideBuilding(Baker.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Baker worker = workers.getFirst();

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

        /* Place bakery close to the new border */
        Point point4 = new Point(28, 18);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point4);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Occupy the bakery */
        Baker worker = Utils.occupyBuilding(new Baker(player0, map), bakery0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testBakerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

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

        /* Place bakery */
        Point point2 = new Point(14, 4);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point2.upLeft());

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

        /* Place bakery */
        Point point2 = new Point(14, 4);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point2.upLeft());

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

        /* Place bakery */
        Point point2 = new Point(14, 4);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point2.upLeft());

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place bakery */
        Point point26 = new Point(17, 17);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(player0, map), bakery0);

        /* Place a second storage closer to the bakery */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the bakery */
        Worker baker = bakery0.getWorker();

        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getPosition(), bakery0.getPosition());

        bakery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(baker.isInsideBuilding());
        assertEquals(baker.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(BAKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, storehouse0.getPosition());

        /* Verify that the baker is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(BAKER), amount + 1);
    }

    @Test
    public void testBakerReturnsOffroadAndAvoidsBurningStorageWhenBakeryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place bakery */
        Point point26 = new Point(17, 17);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(player0, map), bakery0);

        /* Place a second storage closer to the bakery */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place bakery */
        Point point26 = new Point(17, 17);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(player0, map), bakery0);

        /* Place a second storage closer to the bakery */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place bakery */
        Point point26 = new Point(17, 17);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(player0, map), bakery0);

        /* Place a second storage closer to the bakery */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place bakery */
        Point point26 = new Point(17, 17);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Place road to connect the headquarter and the bakery */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), bakery0.getFlag());

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Baker.class, 1, player0).getFirst();

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, bakery0.getFlag().getPosition());

        /* Tear down the building */
        bakery0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(worker.getTarget(), bakery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, bakery0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testBakeryWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(7, 9);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point1);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        /* Populate the bakery */
        Worker baker0 = Utils.occupyBuilding(new Baker(player0, map), bakery);

        assertTrue(baker0.isInsideBuilding());
        assertEquals(baker0.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker0);

        /* Verify that the productivity is 0% when the bakery doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            assertNull(baker0.getCargo());
            assertEquals(bakery.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testBakeryWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(7, 9);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point1);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        /* Populate the bakery */
        Worker baker0 = Utils.occupyBuilding(new Baker(player0, map), bakery);

        assertTrue(baker0.isInsideBuilding());
        assertEquals(baker0.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker0);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(7, 9);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point1);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        /* Populate the bakery */
        Worker baker0 = Utils.occupyBuilding(new Baker(player0, map), bakery);

        assertTrue(baker0.isInsideBuilding());
        assertEquals(baker0.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker0);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(7, 9);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point1);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(7, 9);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point1);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(6, 12);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point1);

        /* Construct the bakery */
        Utils.constructHouse(bakery0);

        /* Verify that the reported output is correct */
        assertEquals(bakery0.getProducedMaterial().length, 1);
        assertEquals(bakery0.getProducedMaterial()[0], BREAD);
    }

    @Test
    public void testBakeryReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(6, 12);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(bakery0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(bakery0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(bakery0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(bakery0.getCanHoldAmount(PLANK), 2);
        assertEquals(bakery0.getCanHoldAmount(STONE), 2);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(bakery0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testBakeryReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(6, 12);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point1);

        /* Construct the bakery */
        Utils.constructHouse(bakery0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(bakery0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(bakery0.getTypesOfMaterialNeeded().contains(WATER));
        assertTrue(bakery0.getTypesOfMaterialNeeded().contains(FLOUR));
        assertEquals(bakery0.getCanHoldAmount(WATER), 6);
        assertEquals(bakery0.getCanHoldAmount(FLOUR), 6);

        for (Material material : Material.values()) {
            if (material == WATER || material == FLOUR) {
                continue;
            }

            assertEquals(bakery0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testBakeryWaitsWhenFlagIsFull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(16, 6);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point1);

        /* Connect the bakery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

        /* Wait for the bakery to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(bakery);
        Utils.waitForNonMilitaryBuildingToGetPopulated(bakery);

        /* Give material to the bakery */
        Utils.putCargoToBuilding(bakery, FLOUR);
        Utils.putCargoToBuilding(bakery, WATER);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, bakery.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the bakery waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(bakery.getFlag().getStackedCargo().size(), 8);
            assertNull(bakery.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the bakery with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(bakery.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(bakery.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(bakery.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, bakery.getWorker(), BREAD);
    }

    @Test
    public void testBakeryDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(16, 6);
        Bakery bakery = map.placeBuilding(new Bakery(player0), point1);

        /* Connect the bakery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

        /* Wait for the bakery to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(bakery);
        Utils.waitForNonMilitaryBuildingToGetPopulated(bakery);

        /* Give material to the bakery */
        Utils.putCargoToBuilding(bakery, FLOUR);
        Utils.putCargoToBuilding(bakery, FLOUR);
        Utils.putCargoToBuilding(bakery, WATER);
        Utils.putCargoToBuilding(bakery, WATER);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, bakery.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The bakery waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(bakery.getFlag().getStackedCargo().size(), 8);
            assertNull(bakery.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the bakery with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(bakery.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(bakery.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(bakery.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, bakery.getWorker(), BREAD);

        /* Wait for the worker to put the cargo on the flag */
        assertEquals(bakery.getWorker().getTarget(), bakery.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, bakery.getWorker(), bakery.getFlag().getPosition());

        assertEquals(bakery.getFlag().getStackedCargo().size(), 8);

        /* Verify that the bakery doesn't produce anything because the flag is full */
        for (int i = 0; i < 400; i++) {
            assertEquals(bakery.getFlag().getStackedCargo().size(), 8);
            assertNull(bakery.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testWhenBreadIsBlockedBakeryFillsUpFlagAndThenStops() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place Bakery */
        Point point1 = new Point(7, 9);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point1);

        /* Place road to connect the bakery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, bakery0.getFlag(), headquarter0.getFlag());

        /* Wait for the bakery to get constructed and occupied */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(bakery0);

        Worker baker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(bakery0);

        assertTrue(baker0.isInsideBuilding());
        assertEquals(baker0.getHome(), bakery0);
        assertEquals(bakery0.getWorker(), baker0);

        /* Add a lot of material to the headquarter for the bakery to consume */
        Utils.adjustInventoryTo(headquarter0, WATER, 40);
        Utils.adjustInventoryTo(headquarter0, FLOUR, 40);

        /* Block storage of bread */
        headquarter0.blockDeliveryOfMaterial(BREAD);

        /* Verify that the bakery puts eight breads on the flag and then stops */
        Utils.waitForFlagToGetStackedCargo(map, bakery0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, baker0, bakery0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(bakery0.getFlag().getStackedCargo().size(), 8);
            assertTrue(baker0.isInsideBuilding());

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), BREAD);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndBakeryIsTornDown() throws Exception {

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

        /* Place bakery */
        Point point2 = new Point(18, 6);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the bakery */
        Road road1 = map.placeAutoSelectedRoad(player0, bakery0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the bakery and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, bakery0);

        /* Add a lot of material to the headquarter for the bakery to consume */
        Utils.adjustInventoryTo(headquarter0, WATER, 40);
        Utils.adjustInventoryTo(headquarter0, FLOUR, 40);

        /* Wait for the bakery and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, bakery0);

        Worker baker0 = bakery0.getWorker();

        assertTrue(baker0.isInsideBuilding());
        assertEquals(baker0.getHome(), bakery0);
        assertEquals(bakery0.getWorker(), baker0);

        /* Verify that the worker goes to the storage when the bakery is torn down */
        headquarter0.blockDeliveryOfMaterial(BAKER);

        bakery0.tearDown();

        map.stepTime();

        assertFalse(baker0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, baker0, bakery0.getFlag().getPosition());

        assertEquals(baker0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, baker0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(baker0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndBakeryIsTornDown() throws Exception {

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

        /* Place bakery */
        Point point2 = new Point(18, 6);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the bakery */
        Road road1 = map.placeAutoSelectedRoad(player0, bakery0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the bakery and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, bakery0);

        /* Add a lot of material to the headquarter for the bakery to consume */
        Utils.adjustInventoryTo(headquarter0, WATER, 40);
        Utils.adjustInventoryTo(headquarter0, FLOUR, 40);

        /* Wait for the bakery and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, bakery0);

        Worker baker0 = bakery0.getWorker();

        assertTrue(baker0.isInsideBuilding());
        assertEquals(baker0.getHome(), bakery0);
        assertEquals(bakery0.getWorker(), baker0);

        /* Verify that the worker goes to the storage off-road when the bakery is torn down */
        headquarter0.blockDeliveryOfMaterial(BAKER);

        bakery0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(baker0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, baker0, bakery0.getFlag().getPosition());

        assertEquals(baker0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, baker0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(baker0));
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
        Utils.adjustInventoryTo(headquarter0, BAKER, 1);

        assertEquals(headquarter0.getAmount(BAKER), 1);

        headquarter0.pushOutAll(BAKER);

        for (int i = 0; i < 10; i++) {
            Worker worker = Utils.waitForWorkerOutsideBuilding(Baker.class, player0);

            assertEquals(headquarter0.getAmount(BAKER), 0);
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
        Utils.adjustInventoryTo(headquarter0, BAKER, 1);

        headquarter0.blockDeliveryOfMaterial(BAKER);
        headquarter0.pushOutAll(BAKER);

        Worker worker = Utils.waitForWorkerOutsideBuilding(Baker.class, player0);

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

        /* Place bakery */
        Point point1 = new Point(7, 9);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point1);

        /* Place road to connect the bakery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, bakery0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the bakery to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(bakery0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(bakery0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter
        */
        headquarter0.blockDeliveryOfMaterial(BAKER);

        Worker worker = bakery0.getWorker();

        bakery0.tearDown();

        assertEquals(worker.getPosition(), bakery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, bakery0.getFlag().getPosition());

        assertEquals(worker.getPosition(), bakery0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), bakery0.getPosition());
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

        /* Place bakery */
        Point point1 = new Point(7, 9);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point1);

        /* Place road to connect the bakery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, bakery0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the bakery to get constructed */
        Utils.waitForBuildingToBeConstructed(bakery0);

        /* Wait for a baker to start walking to the bakery */
        Baker baker = Utils.waitForWorkerOutsideBuilding(Baker.class, player0);

        /* Wait for the baker to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, baker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the baker goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(baker.getTarget(), bakery0.getPosition());

        headquarter0.blockDeliveryOfMaterial(BAKER);

        bakery0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, bakery0.getFlag().getPosition());

        assertEquals(baker.getPosition(), bakery0.getFlag().getPosition());
        assertNotEquals(baker.getTarget(), headquarter0.getPosition());
        assertFalse(baker.isInsideBuilding());
        assertNull(bakery0.getWorker());
        assertNotNull(baker.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, baker, baker.getTarget());

        Point point = baker.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(baker.isDead());
            assertEquals(baker.getPosition(), point);
            assertTrue(map.getWorkers().contains(baker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(baker));
    }
}
