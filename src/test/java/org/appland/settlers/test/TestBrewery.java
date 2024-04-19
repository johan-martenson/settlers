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
import org.appland.settlers.model.actors.Brewer;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Brewery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Fortress;
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
public class TestBrewery {

    @Test
    public void testBreweryCanHoldSixWheatBarsAndSixWater() throws InvalidUserActionException {

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
        var brewery0 = map.placeBuilding(new Brewery(player0), point1);

        /* Connect the brewery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, brewery0.getFlag(), headquarter0.getFlag());

        /* Make sure the headquarters has enough resources */
        Utils.adjustInventoryTo(headquarter0, PLANK, 20);
        Utils.adjustInventoryTo(headquarter0, STONE, 20);
        Utils.adjustInventoryTo(headquarter0, WHEAT, 20);
        Utils.adjustInventoryTo(headquarter0, WATER, 20);
        Utils.adjustInventoryTo(headquarter0, BREWER, 20);

        /* Wait for the brewery to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(brewery0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(brewery0);

        /* Stop production */
        brewery0.stopProduction();

        /* Wait for the brewery to get six iron bars and six planks */
        Utils.waitForBuildingToGetAmountOfMaterial(brewery0, WHEAT, 6);
        Utils.waitForBuildingToGetAmountOfMaterial(brewery0, WATER, 6);

        /* Verify that the brewery doesn't need any more resources and doesn't get any more deliveries */
        assertFalse(brewery0.needsMaterial(WHEAT));
        assertFalse(brewery0.needsMaterial(WATER));

        for (int i = 0; i < 2000; i++) {
            assertFalse(brewery0.needsMaterial(WHEAT));
            assertFalse(brewery0.needsMaterial(WATER));
            assertEquals(brewery0.getAmount(WHEAT), 6);
            assertEquals(brewery0.getAmount(WATER), 6);

            map.stepTime();
        }
    }

    @Test
    public void testBreweryOnlyNeedsTwoPlanksAndTwoStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place brewery */
        Point point22 = new Point(6, 12);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point22);

        /* Deliver two plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        brewery0.putCargo(plankCargo);
        brewery0.putCargo(plankCargo);
        brewery0.putCargo(stoneCargo);
        brewery0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(brewery0);

        /* Verify that this is enough to construct the brewery */
        for (int i = 0; i < 150; i++) {
            assertTrue(brewery0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(brewery0.isReady());
    }

    @Test
    public void testBreweryCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place brewery */
        Point point22 = new Point(6, 12);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point22);

        /* Deliver one plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        brewery0.putCargo(plankCargo);
        brewery0.putCargo(stoneCargo);
        brewery0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(brewery0);

        /* Verify that this is not enough to construct the brewery */
        for (int i = 0; i < 500; i++) {
            assertTrue(brewery0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(brewery0.isReady());
    }

    @Test
    public void testBreweryCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place brewery */
        Point point22 = new Point(6, 12);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point22);

        /* Deliver two planks and one stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        brewery0.putCargo(plankCargo);
        brewery0.putCargo(plankCargo);
        brewery0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(brewery0);

        /* Verify that this is not enough to construct the brewery */
        for (int i = 0; i < 500; i++) {
            assertTrue(brewery0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(brewery0.isReady());
    }

    @Test
    public void testBreweryNeedsWorker() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point3);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point3);

        /* Place a road between the headquarter and the brewery */
        Road road0 = map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter.getFlag());

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        assertTrue(brewery.needsWorker());

        /* Verify that a brewery worker leaves the headquarter */
        Worker brewer0 = Utils.waitForWorkerOutsideBuilding(Brewer.class, player0);

        assertTrue(map.getWorkers().contains(brewer0));

        /* Let the brewery worker reach the brewery */
        assertNotNull(brewer0);
        assertEquals(brewer0.getTarget(), brewery.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, brewer0);

        assertTrue(brewer0.isInsideBuilding());
        assertEquals(brewer0.getHome(), brewery);
        assertEquals(brewery.getWorker(), brewer0);
    }

    @Test
    public void testBrewerIsNotASoldier() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point3);

        /* Place a road between the headquarter and the brewery */
        Road road0 = map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter.getFlag());

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        assertTrue(brewery.needsWorker());

        /* Verify that a brewery worker leaves the headquarter */
        Worker brewer0 = Utils.waitForWorkerOutsideBuilding(Brewer.class, player0);

        assertTrue(map.getWorkers().contains(brewer0));

        /* Verify that the brewer is not a soldier */
        assertNotNull(brewer0);
        assertFalse(brewer0.isSoldier());
    }

    @Test
    public void testOccupiedBreweryWithoutWheatAndWaterProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point3);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point3);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point3);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point3);

        /* Place a road between the headquarter and the brewery */
        Road road0 = map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter.getFlag());

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
    public void testBeerIsNotDeliveredToStorehouseUnderConstruction() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory */
        Utils.clearInventory(headquarter, STONE, PLANK, BEER, WHEAT, WATER);

        /* Place storehouse */
        Point point4 = new Point(10, 4);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Connect the storehouse to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Place the brewery */
        Point point1 = new Point(14, 4);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point1);

        /* Connect the brewery with the storehouse */
        Road road0 = map.placeAutoSelectedRoad(player0, brewery.getFlag(), storehouse.getFlag());

        /* Deliver the needed material to construct the brewery */
        Utils.deliverCargos(brewery, PLANK, 2);
        Utils.deliverCargos(brewery, STONE, 2);

        /* Wait for the brewery to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(brewery);

        Utils.waitForNonMilitaryBuildingToGetPopulated(brewery);

        /* Wait for the courier on the road between the storehouse and the brewery to have a beer cargo */
        Utils.deliverCargos(brewery, WATER, WHEAT);

        Utils.waitForFlagToGetStackedCargo(map, brewery.getFlag(), 1);

        assertEquals(brewery.getFlag().getStackedCargo().getFirst().getMaterial(), BEER);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the storehouse's flag so that it can continue to the headquarters */
        assertEquals(headquarter.getAmount(BEER), 0);
        assertEquals(brewery.getAmount(BEER), 0);
        assertFalse(storehouse.needsMaterial(BEER));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().getFirst().getMaterial().equals(BEER));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testProductionOfOneBeerConsumesOneWheatAndOneWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point3);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point3);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Occupy the brewery */
        Worker brewer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        /* Fast forward so that the brewer would produced beer if it had had wheat and water */
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

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point3);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point3);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place brewery */
        Point point26 = new Point(8, 8);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point26);

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

        /* Wait for the brewer to produce a new beer cargo */
        Utils.fastForward(50, map);

        Worker worker = brewery0.getWorker();

        assertNotNull(worker.getCargo());

        /* Verify that the brewer puts the beer cargo at the flag */
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place brewery */
        Point point26 = new Point(8, 8);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point26);

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

        /* Wait for the brewer to produce a new beer cargo */
        Utils.fastForward(50, map);

        Worker worker = brewery0.getWorker();

        assertNotNull(worker.getCargo());

        /* Verify that the brewer puts the beer cargo at the flag */
        assertEquals(worker.getTarget(), brewery0.getFlag().getPosition());
        assertTrue(brewery0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, brewery0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(brewery0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = brewery0.getFlag().getStackedCargo().getFirst();

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place brewery */
        Point point26 = new Point(8, 8);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point26);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place brewery */
        Point point26 = new Point(8, 8);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point26);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(12, 8);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point1);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(12, 8);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point1);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(20, 14);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point1);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery0);

        /* Connect the brewery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), brewery0.getFlag());

        /* Wait for brewer to get assigned and leave the headquarter */
        List<Brewer> workers = Utils.waitForWorkersOutsideBuilding(Brewer.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Brewer worker = workers.getFirst();

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

        /* Place brewery close to the new border */
        Point point4 = new Point(28, 18);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point4);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery0);

        /* Occupy the brewery */
        Brewer worker = Utils.occupyBuilding(new Brewer(player0, map), brewery0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testBrewerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

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

        /* Place brewer */
        Point point2 = new Point(14, 4);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point2.upLeft());

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

        /* Place brewer */
        Point point2 = new Point(14, 4);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point2.upLeft());

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

        /* Place brewery */
        Point point2 = new Point(14, 4);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point2.upLeft());

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place brewery */
        Point point26 = new Point(17, 17);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point26);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place brewery */
        Point point26 = new Point(17, 17);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point26);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place brewery */
        Point point26 = new Point(17, 17);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point26);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place brewery */
        Point point26 = new Point(17, 17);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point26);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place brewery */
        Point point26 = new Point(17, 17);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point26);

        /* Place road to connect the headquarter and the brewery */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), brewery0.getFlag());

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Brewer.class, 1, player0).getFirst();

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, brewery0.getFlag().getPosition());

        /* Tear down the building */
        brewery0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(worker.getTarget(), brewery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, brewery0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }
    @Test
    public void testBreweryWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point1);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point1);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Populate the brewery */
        Worker armorer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), brewery);
        assertEquals(brewery.getWorker(), armorer0);

        /* Connect the brewery with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), brewery.getFlag());

        /* Make the brewery create some beer with full resources available */
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point1);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Populate the brewery */
        Worker armorer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), brewery);
        assertEquals(brewery.getWorker(), armorer0);

        /* Connect the brewery with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), brewery.getFlag());

        /* Make the brewery create some beer with full resources available */
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point1);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point1);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(6, 12);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point1);

        /* Construct the brewery */
        Utils.constructHouse(brewery0);

        /* Verify that the reported output is correct */
        assertEquals(brewery0.getProducedMaterial().length, 1);
        assertEquals(brewery0.getProducedMaterial()[0], BEER);
    }

    @Test
    public void testBreweryReportsCorrectMaterialsNeededForConstruction() throws Exception {

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
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(brewery0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(brewery0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(brewery0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(brewery0.getCanHoldAmount(PLANK), 2);
        assertEquals(brewery0.getCanHoldAmount(STONE), 2);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(brewery0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testBreweryReportsCorrectMaterialsNeededForProduction() throws Exception {

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
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point1);

        /* Construct the brewery */
        Utils.constructHouse(brewery0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(brewery0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(brewery0.getTypesOfMaterialNeeded().contains(WATER));
        assertTrue(brewery0.getTypesOfMaterialNeeded().contains(WHEAT));
        assertEquals(brewery0.getCanHoldAmount(WATER), 6);
        assertEquals(brewery0.getCanHoldAmount(WHEAT), 6);

        for (Material material : Material.values()) {
            if (material == WATER || material == WHEAT) {
                continue;
            }

            assertEquals(brewery0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testBreweryWaitsWhenFlagIsFull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(16, 6);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point1);

        /* Connect the brewery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter.getFlag());

        /* Wait for the brewery to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(brewery);
        Utils.waitForNonMilitaryBuildingToGetPopulated(brewery);

        /* Give material to the brewery */
        Utils.putCargoToBuilding(brewery, WHEAT);
        Utils.putCargoToBuilding(brewery, WATER);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, WHEAT, 8, brewery.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the brewery waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(brewery.getFlag().getStackedCargo().size(), 8);
            assertNull(brewery.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the brewery with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == WHEAT) {
                break;
            }

            assertNull(brewery.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(brewery.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(brewery.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, brewery.getWorker(), BEER);
    }

    @Test
    public void testBreweryDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(16, 6);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point1);

        /* Connect the brewery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter.getFlag());

        /* Wait for the brewery to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(brewery);
        Utils.waitForNonMilitaryBuildingToGetPopulated(brewery);

        /* Give material to the brewery */
        Utils.putCargoToBuilding(brewery, WHEAT);
        Utils.putCargoToBuilding(brewery, WHEAT);
        Utils.putCargoToBuilding(brewery, WATER);
        Utils.putCargoToBuilding(brewery, WATER);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, WHEAT, 8, brewery.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The brewery waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(brewery.getFlag().getStackedCargo().size(), 8);
            assertNull(brewery.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the brewery with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == WHEAT) {
                break;
            }

            assertNull(brewery.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(brewery.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(brewery.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, brewery.getWorker(), BEER);

        /* Wait for the worker to put the cargo on the flag */
        assertEquals(brewery.getWorker().getTarget(), brewery.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, brewery.getWorker(), brewery.getFlag().getPosition());

        assertEquals(brewery.getFlag().getStackedCargo().size(), 8);

        /* Verify that the brewery doesn't produce anything because the flag is full */
        for (int i = 0; i < 400; i++) {
            assertEquals(brewery.getFlag().getStackedCargo().size(), 8);
            assertNull(brewery.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testWhenBeerDeliveryAreBlockedBreweryFillsUpFlagAndThenStops() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place Brewery */
        Point point1 = new Point(7, 9);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point1);

        /* Place road to connect the brewery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, brewery0.getFlag(), headquarter0.getFlag());

        /* Wait for the brewery to get constructed and occupied */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(brewery0);

        Worker brewer0 = Utils.waitForNonMilitaryBuildingToGetPopulated(brewery0);

        assertTrue(brewer0.isInsideBuilding());
        assertEquals(brewer0.getHome(), brewery0);
        assertEquals(brewery0.getWorker(), brewer0);

        /* Add a lot of material to the headquarter for the brewery to consume */
        Utils.adjustInventoryTo(headquarter0, WATER, 40);
        Utils.adjustInventoryTo(headquarter0, WHEAT, 40);

        /* Block storage of beer */
        headquarter0.blockDeliveryOfMaterial(BEER);

        /* Verify that the brewery puts eight beers on the flag and then stops */
        Utils.waitForFlagToGetStackedCargo(map, brewery0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, brewer0, brewery0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(brewery0.getFlag().getStackedCargo().size(), 8);
            assertTrue(brewer0.isInsideBuilding());

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), BEER);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndBreweryIsTornDown() throws Exception {

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

        /* Place brewery */
        Point point2 = new Point(18, 6);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the brewery */
        Road road1 = map.placeAutoSelectedRoad(player0, brewery0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the brewery and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, brewery0);

        /* Add a lot of material to the headquarter for the brewery to consume */
        Utils.adjustInventoryTo(headquarter0, WATER, 40);
        Utils.adjustInventoryTo(headquarter0, WHEAT, 40);

        /* Wait for the brewery and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, brewery0);

        Worker brewer0 = brewery0.getWorker();

        assertTrue(brewer0.isInsideBuilding());
        assertEquals(brewer0.getHome(), brewery0);
        assertEquals(brewery0.getWorker(), brewer0);

        /* Verify that the worker goes to the storage when the brewery is torn down */
        headquarter0.blockDeliveryOfMaterial(BREWER);

        brewery0.tearDown();

        map.stepTime();

        assertFalse(brewer0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, brewer0, brewery0.getFlag().getPosition());

        assertEquals(brewer0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, brewer0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(brewer0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndBreweryIsTornDown() throws Exception {

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

        /* Place brewery */
        Point point2 = new Point(18, 6);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the brewery */
        Road road1 = map.placeAutoSelectedRoad(player0, brewery0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the brewery and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, brewery0);

        /* Add a lot of material to the headquarter for the brewery to consume */
        Utils.adjustInventoryTo(headquarter0, WATER, 40);
        Utils.adjustInventoryTo(headquarter0, WHEAT, 40);

        /* Wait for the brewery and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, brewery0);

        Worker brewer0 = brewery0.getWorker();

        assertTrue(brewer0.isInsideBuilding());
        assertEquals(brewer0.getHome(), brewery0);
        assertEquals(brewery0.getWorker(), brewer0);

        /* Verify that the worker goes to the storage off-road when the brewery is torn down */
        headquarter0.blockDeliveryOfMaterial(BREWER);

        brewery0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(brewer0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, brewer0, brewery0.getFlag().getPosition());

        assertEquals(brewer0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, brewer0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(brewer0));
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
        Utils.adjustInventoryTo(headquarter0, BREWER, 1);

        assertEquals(headquarter0.getAmount(BREWER), 1);

        headquarter0.pushOutAll(BREWER);

        for (int i = 0; i < 10; i++) {
            Worker worker = Utils.waitForWorkerOutsideBuilding(Brewer.class, player0);

            assertEquals(headquarter0.getAmount(BREWER), 0);
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
        Utils.adjustInventoryTo(headquarter0, BREWER, 1);

        headquarter0.blockDeliveryOfMaterial(BREWER);
        headquarter0.pushOutAll(BREWER);

        Worker worker = Utils.waitForWorkerOutsideBuilding(Brewer.class, player0);

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

        /* Place brewery */
        Point point1 = new Point(7, 9);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point1);

        /* Place road to connect the brewery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, brewery0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the brewery to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(brewery0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(brewery0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter
        */
        headquarter0.blockDeliveryOfMaterial(BREWER);

        Worker worker = brewery0.getWorker();

        brewery0.tearDown();

        assertEquals(worker.getPosition(), brewery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, brewery0.getFlag().getPosition());

        assertEquals(worker.getPosition(), brewery0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), brewery0.getPosition());
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

        /* Place brewery */
        Point point1 = new Point(7, 9);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point1);

        /* Place road to connect the brewery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, brewery0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the brewery to get constructed */
        Utils.waitForBuildingToBeConstructed(brewery0);

        /* Wait for a brewer to start walking to the brewery */
        Brewer brewer = Utils.waitForWorkerOutsideBuilding(Brewer.class, player0);

        /* Wait for the brewer to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, brewer, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the brewer goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(brewer.getTarget(), brewery0.getPosition());

        headquarter0.blockDeliveryOfMaterial(BREWER);

        brewery0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, brewer, brewery0.getFlag().getPosition());

        assertEquals(brewer.getPosition(), brewery0.getFlag().getPosition());
        assertNotEquals(brewer.getTarget(), headquarter0.getPosition());
        assertFalse(brewer.isInsideBuilding());
        assertNull(brewery0.getWorker());
        assertNotNull(brewer.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, brewer, brewer.getTarget());

        Point point = brewer.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(brewer.isDead());
            assertEquals(brewer.getPosition(), point);
            assertTrue(map.getWorkers().contains(brewer));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(brewer));
    }
}
