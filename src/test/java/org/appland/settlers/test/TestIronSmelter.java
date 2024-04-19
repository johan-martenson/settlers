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
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.IronFounder;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.IronSmelter;
import org.appland.settlers.model.buildings.Metalworks;
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
public class TestIronSmelter {

    @Test
    public void testIronSmelterOnlyNeedsTwoPlanksAndTwoStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place iron smelter */
        Point point22 = new Point(6, 12);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point22);

        /* Deliver two plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        ironSmelter0.putCargo(plankCargo);
        ironSmelter0.putCargo(plankCargo);
        ironSmelter0.putCargo(stoneCargo);
        ironSmelter0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(ironSmelter0);

        /* Verify that this is enough to construct the iron smelter */
        for (int i = 0; i < 150; i++) {
            assertTrue(ironSmelter0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(ironSmelter0.isReady());
    }

    @Test
    public void testIronSmelterCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place iron smelter */
        Point point22 = new Point(6, 12);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point22);

        /* Deliver one plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        ironSmelter0.putCargo(plankCargo);
        ironSmelter0.putCargo(stoneCargo);
        ironSmelter0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(ironSmelter0);

        /* Verify that this is not enough to construct the iron smelter */
        for (int i = 0; i < 500; i++) {
            assertTrue(ironSmelter0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(ironSmelter0.isReady());
    }

    @Test
    public void testIronSmelterCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place iron smelter */
        Point point22 = new Point(6, 12);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point22);

        /* Deliver two planks and one stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        ironSmelter0.putCargo(plankCargo);
        ironSmelter0.putCargo(plankCargo);
        ironSmelter0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(ironSmelter0);

        /* Verify that this is not enough to construct the iron smelter */
        for (int i = 0; i < 500; i++) {
            assertTrue(ironSmelter0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(ironSmelter0.isReady());
    }

    @Test
    public void testIronSmelterNeedsWorker() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironFounder = map.placeBuilding(new IronSmelter(player0), point3);

        /* Unfinished iron smelter doesn't need worker */
        assertFalse(ironFounder.needsWorker());

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironFounder);

        assertTrue(ironFounder.needsWorker());
    }

    @Test
    public void testHeadquarterHasAtLeastOneIronFounderAtStart() {
        Headquarter headquarter = new Headquarter(null);

        assertTrue(headquarter.getAmount(IRON_FOUNDER) >= 1);
    }

    @Test
    public void testIronSmelterGetsAssignedWorker() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Place a road between the headquarter and the iron smelter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), headquarter.getFlag());

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        assertTrue(ironSmelter.needsWorker());

        /* Verify that a iron smelter worker leaves the headquarter */
        IronFounder ironFounder0 = Utils.waitForWorkerOutsideBuilding(IronFounder.class, player0);

        /* Let the iron smelter worker reach the iron smelter */
        assertNotNull(ironFounder0);
        assertEquals(ironFounder0.getTarget(), ironSmelter.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, ironFounder0);

        assertTrue(ironFounder0.isInsideBuilding());
        assertEquals(ironFounder0.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), ironFounder0);
    }

    @Test
    public void testIronSmelterIsNotASoldier() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Place a road between the headquarter and the iron smelter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), headquarter.getFlag());

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        assertTrue(ironSmelter.needsWorker());

        /* Verify that a iron smelter worker leaves the headquarter */
        IronFounder ironFounder0 = Utils.waitForWorkerOutsideBuilding(IronFounder.class, player0);

        /* Verify that the iron founder is not a soldier */
        assertNotNull(ironFounder0);
        assertFalse(ironFounder0.isSoldier());
    }

    @Test
    public void testIronFounderGetsCreatedFromCrucible() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all iron founders from the headquarter and add one crucible */
        Utils.adjustInventoryTo(headquarter, IRON_FOUNDER, 0);
        Utils.adjustInventoryTo(headquarter, Material.CRUCIBLE, 1);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Place a road between the headquarter and the iron smelter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), headquarter.getFlag());

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        assertTrue(ironSmelter.needsWorker());

        /* Verify that a iron smelter worker leaves the headquarter */
        IronFounder ironFounder0 = Utils.waitForWorkerOutsideBuilding(IronFounder.class, player0);

        /* Let the iron smelter worker reach the iron smelter */
        assertNotNull(ironFounder0);
        assertEquals(ironFounder0.getTarget(), ironSmelter.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, ironFounder0);

        assertTrue(ironFounder0.isInsideBuilding());
        assertEquals(ironFounder0.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), ironFounder0);
    }

    @Test
    public void testOccupiedIronSmelterWithoutCoalAndIronProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        /* Occupy the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter);

        assertTrue(ironFounder0.isInsideBuilding());
        assertEquals(ironFounder0.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), ironFounder0);

        /* Verify that the iron smelter doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
            assertNull(ironFounder0.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedIronSmelterProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        /* Verify that the iron smelter doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedIronSmelterWithIronAndCoalProducesIronBars() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        /* Occupy the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter);

        assertTrue(ironFounder0.isInsideBuilding());
        assertEquals(ironFounder0.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), ironFounder0);

        /* Deliver iron and coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(COAL, map));
        ironSmelter.putCargo(new Cargo(IRON, map));

        /* Verify that the iron smelter produces iron bars */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
            assertNull(ironFounder0.getCargo());
        }

        map.stepTime();

        assertNotNull(ironFounder0.getCargo());
        assertEquals(ironFounder0.getCargo().getMaterial(), IRON_BAR);
        assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testIronFounderLeavesIronBarAtTheFlag() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Place a road between the headquarter and the iron smelter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), headquarter.getFlag());

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        /* Occupy the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter);

        assertTrue(ironFounder0.isInsideBuilding());
        assertEquals(ironFounder0.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), ironFounder0);

        /* Deliver iron and coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(IRON, map));
        ironSmelter.putCargo(new Cargo(COAL, map));

        /* Verify that the iron smelter produces iron bars */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
            assertNull(ironFounder0.getCargo());
        }

        map.stepTime();

        assertNotNull(ironFounder0.getCargo());
        assertEquals(ironFounder0.getCargo().getMaterial(), IRON_BAR);
        assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());

        /* Verify that the iron smelter worker leaves the cargo at the flag */
        assertEquals(ironFounder0.getTarget(), ironSmelter.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder0, ironSmelter.getFlag().getPosition());

        assertFalse(ironSmelter.getFlag().getStackedCargo().isEmpty());
        assertNull(ironFounder0.getCargo());
        assertEquals(ironFounder0.getTarget(), ironSmelter.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, ironFounder0);

        assertTrue(ironFounder0.isInsideBuilding());
    }

    @Test
    public void testIronBarCargoIsDeliveredToMetalworksWhichIsCloserThanHeadquarters() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Remove all iron bars from the headquarters */
        Utils.adjustInventoryTo(headquarter, IRON_BAR, 0);

        /* Place metal works */
        Point point4 = new Point(10, 4);
        Metalworks metalworks = map.placeBuilding(new Metalworks(player0), point4);

        /* Connect the metal works to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

        /* Wait for the metal works to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(metalworks);

        Utils.waitForNonMilitaryBuildingToGetPopulated(metalworks);

        /* Place the iron smelter */
        Point point1 = new Point(14, 4);
        IronSmelter ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);

        /* Connect the iron smelter with the metal works */
        Road road0 = map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), metalworks.getFlag());

        /* Wait for the iron smelter to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(ironSmelter);

        Utils.waitForNonMilitaryBuildingToGetPopulated(ironSmelter);

        /* Wait for the courier on the road between the metal works and the iron smelter hut to have a cargo */
        Utils.adjustInventoryTo(headquarter, IRON, 2);
        Utils.adjustInventoryTo(headquarter, COAL, 2);

        Utils.waitForFlagToGetStackedCargo(map, ironSmelter.getFlag(), 1);

        assertEquals(ironSmelter.getFlag().getStackedCargo().getFirst().getMaterial(), IRON_BAR);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the METAL WORKS (and not the headquarters) */
        assertEquals(ironSmelter.getAmount(IRON_BAR), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), metalworks.getPosition());

        assertEquals(metalworks.getAmount(IRON_BAR), 1);
    }

    @Test
    public void testIronBarIsNotDeliveredToStorehouseUnderConstruction() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory */
        Utils.clearInventory(headquarter, PLANK, STONE, IRON_BAR, COAL, IRON_BAR);

        /* Place storehouse */
        Point point4 = new Point(10, 4);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Connect the storehouse to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Place the iron smelter */
        Point point1 = new Point(14, 4);
        IronSmelter ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);

        /* Connect the iron smelter with the storehouse */
        Road road0 = map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), storehouse.getFlag());

        /* Deliver the needed material to construct the iron smelter */
        Utils.deliverCargos(ironSmelter, PLANK, 2);
        Utils.deliverCargos(ironSmelter, STONE, 2);

        /* Wait for the iron smelter to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(ironSmelter);

        Utils.waitForNonMilitaryBuildingToGetPopulated(ironSmelter);

        /* Wait for the courier on the road between the storehouse and the iron smelter to have an iron bar cargo */
        Utils.deliverCargos(ironSmelter, COAL, IRON);

        Utils.waitForFlagToGetStackedCargo(map, ironSmelter.getFlag(), 1);

        assertEquals(ironSmelter.getFlag().getStackedCargo().getFirst().getMaterial(), IRON_BAR);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the storehouse's flag so that it can continue to the headquarters */
        assertEquals(headquarter.getAmount(IRON_BAR), 0);
        assertEquals(ironSmelter.getAmount(IRON_BAR), 0);
        assertFalse(storehouse.needsMaterial(IRON_BAR));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().getFirst().getMaterial().equals(IRON_BAR));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testIronBarIsNotDeliveredTwiceToBuildingThatOnlyNeedsOne() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory */
        Utils.clearInventory(headquarter, IRON_BAR, COAL, IRON);

        /* Place armory */
        Point point4 = new Point(10, 4);
        Armory armory = map.placeBuilding(new Armory(player0), point4);

        /* Construct the armory */
        Utils.constructHouse(armory);

        /* Deliver an iron bar to the armory so it only has space for one more */
        Utils.deliverCargo(armory, IRON_BAR);

        assertTrue(armory.needsMaterial(IRON_BAR));

        /* Connect the armory to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, armory.getFlag(), headquarter.getFlag());

        /* Place the iron smelter */
        Point point1 = new Point(14, 4);
        var ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);

        /* Connect the iron smelter with the armory */
        Road road0 = map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), armory.getFlag());

        /* Deliver the needed material to construct the iron smelter */
        Utils.deliverCargos(ironSmelter, PLANK, 2);
        Utils.deliverCargos(ironSmelter, STONE, 2);

        /* Wait for the iron smelter to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(ironSmelter);

        Utils.waitForNonMilitaryBuildingToGetPopulated(ironSmelter);

        /* Wait for the flag on the road between the armory and the iron smelter to have an iron bar cargo */
        Utils.deliverCargos(ironSmelter, COAL, IRON);

        Utils.waitForFlagToGetStackedCargo(map, ironSmelter.getFlag(), 1);

        assertEquals(ironSmelter.getFlag().getStackedCargo().getFirst().getMaterial(), IRON_BAR);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that no iron bar is delivered from the headquarters */
        Utils.adjustInventoryTo(headquarter, IRON_BAR, 1);

        assertEquals(armory.getCanHoldAmount(IRON_BAR) - armory.getAmount(IRON_BAR), 1);
        assertFalse(armory.needsMaterial(IRON_BAR));

        for (int i = 0; i < 200; i++) {
            if (armory.getAmount(IRON_BAR) == 0) {
                break;
            }

            assertEquals(headquarter.getAmount(IRON_BAR), 1);
            assertNull(headquarter.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionOfOneIronBarConsumesOneIronAndOneCoal() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        /* Occupy the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        /* Occupy the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter);

        /* Fast forward so that the iron smelter worker would produced iron bars if it had had iron and coal */
        Utils.fastForward(150, map);

        assertNull(ironFounder0.getCargo());

        /* Deliver iron and coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(IRON, map));
        ironSmelter.putCargo(new Cargo(COAL, map));

        /* Verify that it takes 50 steps for the iron smelter worker to produce the iron bar */
        for (int i = 0; i < 50; i++) {
            assertNull(ironFounder0.getCargo());
            map.stepTime();
        }

        assertNotNull(ironFounder0.getCargo());
    }

    @Test
    public void testIronSmelterCannotProduceWithOnlyIron() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        /* Occupy the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter);

        /* Deliver iron but not coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(IRON, map));

        /* Verify that the iron founder doesn't produce iron bars since it doesn't have any coal */
        for (int i = 0; i < 200; i++) {
            assertNull(ironFounder0.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testIronSmelterCannotProduceWithOnlyCoal() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        /* Occupy the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter);

        /* Deliver iron but not coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(COAL, map));

        /* Verify that the iron founder doesn't produce iron bars since it doesn't have any coal */
        for (int i = 0; i < 200; i++) {
            assertNull(ironFounder0.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testIronSmelterWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0);

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
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Deliver material to the iron smelter */
        Cargo ironCargo = new Cargo(COAL, map);
        Cargo coalCargo = new Cargo(IRON, map);

        ironSmelter0.putCargo(ironCargo);
        ironSmelter0.putCargo(ironCargo);

        ironSmelter0.putCargo(coalCargo);
        ironSmelter0.putCargo(coalCargo);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0);

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
        Cargo cargo = ironSmelter0.getFlag().getStackedCargo().getFirst();

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), ironSmelter0.getFlag().getPosition());

        /* Remove the items the iron smelter needs from the headquarter's inventory */
        Utils.adjustInventoryTo(headquarter0, COAL, 0);
        Utils.adjustInventoryTo(headquarter0, IRON, 0);

        /* Connect the iron smelter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironSmelter0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), ironSmelter0.getFlag().getPosition());
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point26);

        /* Connect the iron smelter with the headquarter */
        map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), headquarter0.getFlag());

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter);

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
        for (Point point : ironFounder.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testDestroyedIronSmelterIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Connect the iron smelter with the headquarter */
        map.placeAutoSelectedRoad(player0, ironSmelter0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Destroy the iron smelter */
        ironSmelter0.tearDown();

        assertTrue(ironSmelter0.isBurningDown());

        /* Wait for the iron smelter to stop burning */
        Utils.fastForward(50, map);

        assertTrue(ironSmelter0.isDestroyed());

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(ironSmelter0.getPosition(), ironSmelter0.getFlag().getPosition()));

        map.removeFlag(ironSmelter0.getFlag());

        assertNull(map.getRoad(ironSmelter0.getPosition(), ironSmelter0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(ironSmelter0.getPosition(), ironSmelter0.getFlag().getPosition()));

        ironSmelter0.tearDown();

        assertNull(map.getRoad(ironSmelter0.getPosition(), ironSmelter0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInIronSmelterCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(12, 8);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Connect the iron smelter and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironSmelter0.getFlag(), headquarter.getFlag());

        /* Finish the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Deliver iron and coal to the iron smelter */
        ironSmelter0.putCargo(new Cargo(COAL, map));
        ironSmelter0.putCargo(new Cargo(IRON, map));

        /* Assign a worker to the iron smelter */
        IronFounder ironFounder = new IronFounder(player0, map);

        Utils.occupyBuilding(ironFounder, ironSmelter0);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(12, 8);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Connect the iron smelter and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironSmelter0.getFlag(), headquarter.getFlag());

        /* Finish the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Assign a worker to the iron smelter */
        IronFounder ironFounder = new IronFounder(player0, map);

        Utils.occupyBuilding(ironFounder, ironSmelter0);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(20, 14);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Connect the iron smelter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironSmelter0.getFlag());

        /* Wait for iron founder to get assigned and leave the headquarter */
        List<IronFounder> workers = Utils.waitForWorkersOutsideBuilding(IronFounder.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        IronFounder worker = workers.getFirst();

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

        /* Place iron smelter close to the new border */
        Point point4 = new Point(28, 18);
        IronSmelter ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point4);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Occupy the iron smelter */
        IronFounder worker = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testIronFounderReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

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

        /* Place iron smelter */
        Point point2 = new Point(14, 4);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, ironSmelter0.getFlag());

        /* Wait for the iron founder to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(IronFounder.class, 1, player0);

        IronFounder ironFounder = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof IronFounder) {
                ironFounder = (IronFounder) worker;
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

        /* Place iron smelter */
        Point point2 = new Point(14, 4);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, ironSmelter0.getFlag());

        /* Wait for the iron founder to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(IronFounder.class, 1, player0);

        IronFounder ironFounder = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof IronFounder) {
                ironFounder = (IronFounder) worker;
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

        /* Verify that the iron founder goes out to iron founder instead of going directly back */
        assertNotEquals(ironFounder.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testIronFounderReturnsToStorageIfIronSmelterIsDestroyed() throws Exception {

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

        /* Place iron smelter */
        Point point2 = new Point(14, 4);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, ironSmelter0.getFlag());

        /* Wait for the iron founder to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(IronFounder.class, 1, player0);

        IronFounder ironFounder = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof IronFounder) {
                ironFounder = (IronFounder) worker;
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place iron smelter */
        Point point26 = new Point(17, 17);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0);

        /* Place a second storage closer to the iron smelter */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the iron smelter */
        Worker ironFounder = ironSmelter0.getWorker();

        assertTrue(ironFounder.isInsideBuilding());
        assertEquals(ironFounder.getPosition(), ironSmelter0.getPosition());

        ironSmelter0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ironFounder.isInsideBuilding());
        assertEquals(ironFounder.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(IRON_FOUNDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, storehouse0.getPosition());

        /* Verify that the iron founder is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(IRON_FOUNDER), amount + 1);
    }

    @Test
    public void testIronFounderReturnsOffroadAndAvoidsBurningStorageWhenIronSmelterIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place iron smelter */
        Point point26 = new Point(17, 17);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0);

        /* Place a second storage closer to the iron smelter */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place iron smelter */
        Point point26 = new Point(17, 17);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0);

        /* Place a second storage closer to the iron smelter */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place iron smelter */
        Point point26 = new Point(17, 17);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0);

        /* Place a second storage closer to the iron smelter */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place iron smelter */
        Point point26 = new Point(17, 17);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point26);

        /* Place road to connect the headquarter and the iron smelter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironSmelter0.getFlag());

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(IronFounder.class, 1, player0).getFirst();

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, ironSmelter0.getFlag().getPosition());

        /* Tear down the building */
        ironSmelter0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(worker.getTarget(), ironSmelter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, ironSmelter0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testIronSmelterWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        /* Populate the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        /* Populate the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        /* Populate the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(10, 10);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Populate the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0);

        /* Verify that the iron smelter can produce */
        assertTrue(ironSmelter0.canProduce());
    }

    @Test
    public void testIronSmelterReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(6, 12);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Construct the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Verify that the reported output is correct */
        assertEquals(ironSmelter0.getProducedMaterial().length, 1);
        assertEquals(ironSmelter0.getProducedMaterial()[0], IRON_BAR);
    }

    @Test
    public void testIronSmelterReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(6, 12);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(ironSmelter0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(ironSmelter0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(ironSmelter0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(ironSmelter0.getCanHoldAmount(PLANK), 2);
        assertEquals(ironSmelter0.getCanHoldAmount(STONE), 2);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(ironSmelter0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testIronSmelterReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(6, 12);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Construct the iron smelter */
        Utils.constructHouse(ironSmelter0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(ironSmelter0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(ironSmelter0.getTypesOfMaterialNeeded().contains(COAL));
        assertTrue(ironSmelter0.getTypesOfMaterialNeeded().contains(IRON));
        assertEquals(ironSmelter0.getCanHoldAmount(COAL), 1);
        assertEquals(ironSmelter0.getCanHoldAmount(IRON), 1);

        for (Material material : Material.values()) {
            if (material == COAL || material == IRON) {
                continue;
            }

            assertEquals(ironSmelter0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testIronSmelterWaitsWhenFlagIsFull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(16, 6);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);

        /* Connect the iron smelter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), headquarter.getFlag());

        /* Wait for the iron smelter to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(ironSmelter);
        Utils.waitForNonMilitaryBuildingToGetPopulated(ironSmelter);

        /* Give material to the iron smelter */
        Utils.putCargoToBuilding(ironSmelter, IRON);
        Utils.putCargoToBuilding(ironSmelter, COAL);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, ironSmelter.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the iron smelter waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(ironSmelter.getFlag().getStackedCargo().size(), 8);
            assertNull(ironSmelter.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the iron smelter with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(ironSmelter.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(ironSmelter.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(ironSmelter.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, ironSmelter.getWorker(), IRON_BAR);
    }

    @Test
    public void testIronSmelterDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(16, 6);
        IronSmelter ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);

        /* Connect the iron smelter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), headquarter.getFlag());

        /* Wait for the iron smelter to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(ironSmelter);
        Utils.waitForNonMilitaryBuildingToGetPopulated(ironSmelter);

        /* Give material to the iron smelter */
        Utils.putCargoToBuilding(ironSmelter, IRON);
        Utils.putCargoToBuilding(ironSmelter, IRON);
        Utils.putCargoToBuilding(ironSmelter, COAL);
        Utils.putCargoToBuilding(ironSmelter, COAL);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, ironSmelter.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The iron smelter waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(ironSmelter.getFlag().getStackedCargo().size(), 8);
            assertNull(ironSmelter.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the iron smelter with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(ironSmelter.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(ironSmelter.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(ironSmelter.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, ironSmelter.getWorker(), IRON_BAR);

        /* Wait for the worker to put the cargo on the flag */
        assertEquals(ironSmelter.getWorker().getTarget(), ironSmelter.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironSmelter.getWorker(), ironSmelter.getFlag().getPosition());

        assertEquals(ironSmelter.getFlag().getStackedCargo().size(), 8);

        /* Verify that the iron smelter doesn't produce anything because the flag is full */
        for (int i = 0; i < 400; i++) {
            assertEquals(ironSmelter.getFlag().getStackedCargo().size(), 8);
            assertNull(ironSmelter.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testWhenIronBarDeliveryAreBlockedIronSmelterFillsUpFlagAndThenStops() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(7, 9);
        IronSmelter ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Place road to connect the iron smelter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironSmelter0.getFlag(), headquarter0.getFlag());

        /* Wait for the iron smelter to get constructed and occupied */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(ironSmelter0);

        Worker ironFounder0 = Utils.waitForNonMilitaryBuildingToGetPopulated(ironSmelter0);

        assertTrue(ironFounder0.isInsideBuilding());
        assertEquals(ironFounder0.getHome(), ironSmelter0);
        assertEquals(ironSmelter0.getWorker(), ironFounder0);

        /* Add a lot of material to the headquarter for the iron smelter to consume */
        Utils.adjustInventoryTo(headquarter0, IRON, 40);
        Utils.adjustInventoryTo(headquarter0, COAL, 40);

        /* Block storage of iron bar */
        headquarter0.blockDeliveryOfMaterial(IRON_BAR);

        /* Verify that the iron smelter puts eight iron bars on the flag and then stops */
        Utils.waitForFlagToGetStackedCargo(map, ironSmelter0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder0, ironSmelter0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(ironSmelter0.getFlag().getStackedCargo().size(), 8);
            assertTrue(ironFounder0.isInsideBuilding());

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), IRON_BAR);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndIronSmelterIsTornDown() throws Exception {

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

        /* Place iron smelter */
        Point point2 = new Point(18, 6);
        IronSmelter ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the iron smelter */
        Road road1 = map.placeAutoSelectedRoad(player0, ironSmelter0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the iron smelter and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, ironSmelter0);

        /* Add a lot of material to the headquarter for the iron smelter to consume */
        Utils.adjustInventoryTo(headquarter0, IRON, 40);
        Utils.adjustInventoryTo(headquarter0, COAL, 40);

        /* Wait for the iron smelter and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, ironSmelter0);

        Worker ironFounder0 = ironSmelter0.getWorker();

        assertTrue(ironFounder0.isInsideBuilding());
        assertEquals(ironFounder0.getHome(), ironSmelter0);
        assertEquals(ironSmelter0.getWorker(), ironFounder0);

        /* Verify that the worker goes to the storage when the iron smelter is torn down */
        headquarter0.blockDeliveryOfMaterial(IRON_FOUNDER);

        ironSmelter0.tearDown();

        map.stepTime();

        assertFalse(ironFounder0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder0, ironSmelter0.getFlag().getPosition());

        assertEquals(ironFounder0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, ironFounder0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(ironFounder0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndIronSmelterIsTornDown() throws Exception {

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

        /* Place iron smelter */
        Point point2 = new Point(18, 6);
        IronSmelter ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the iron smelter */
        Road road1 = map.placeAutoSelectedRoad(player0, ironSmelter0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the iron smelter and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, ironSmelter0);

        /* Add a lot of material to the headquarter for the iron smelter to consume */
        Utils.adjustInventoryTo(headquarter0, IRON, 40);
        Utils.adjustInventoryTo(headquarter0, COAL, 40);

        /* Wait for the iron smelter and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, ironSmelter0);

        Worker ironFounder0 = ironSmelter0.getWorker();

        assertTrue(ironFounder0.isInsideBuilding());
        assertEquals(ironFounder0.getHome(), ironSmelter0);
        assertEquals(ironSmelter0.getWorker(), ironFounder0);

        /* Verify that the worker goes to the storage off-road when the iron smelter is torn down */
        headquarter0.blockDeliveryOfMaterial(IRON_FOUNDER);

        ironSmelter0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(ironFounder0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder0, ironSmelter0.getFlag().getPosition());

        assertEquals(ironFounder0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(ironFounder0));
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
        Utils.adjustInventoryTo(headquarter0, IRON_FOUNDER, 1);

        assertEquals(headquarter0.getAmount(IRON_FOUNDER), 1);

        headquarter0.pushOutAll(IRON_FOUNDER);

        for (int i = 0; i < 10; i++) {
            Worker worker = Utils.waitForWorkerOutsideBuilding(IronFounder.class, player0);

            assertEquals(headquarter0.getAmount(IRON_FOUNDER), 0);
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
        Utils.adjustInventoryTo(headquarter0, IRON_FOUNDER, 1);

        headquarter0.blockDeliveryOfMaterial(IRON_FOUNDER);
        headquarter0.pushOutAll(IRON_FOUNDER);

        Worker worker = Utils.waitForWorkerOutsideBuilding(IronFounder.class, player0);

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

        /* Place iron smelter */
        Point point1 = new Point(7, 9);
        IronSmelter ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Place road to connect the iron smelter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironSmelter0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the iron smelter to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(ironSmelter0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(ironSmelter0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter
        */
        headquarter0.blockDeliveryOfMaterial(IRON_FOUNDER);

        Worker worker = ironSmelter0.getWorker();

        ironSmelter0.tearDown();

        assertEquals(worker.getPosition(), ironSmelter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, ironSmelter0.getFlag().getPosition());

        assertEquals(worker.getPosition(), ironSmelter0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), ironSmelter0.getPosition());
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

        /* Place iron smelter */
        Point point1 = new Point(7, 9);
        IronSmelter ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Place road to connect the iron smelter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, ironSmelter0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the iron smelter to get constructed */
        Utils.waitForBuildingToBeConstructed(ironSmelter0);

        /* Wait for a iron founder to start walking to the iron smelter */
        IronFounder ironFounder = Utils.waitForWorkerOutsideBuilding(IronFounder.class, player0);

        /* Wait for the iron founder to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the iron founder goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(ironFounder.getTarget(), ironSmelter0.getPosition());

        headquarter0.blockDeliveryOfMaterial(IRON_FOUNDER);

        ironSmelter0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, ironSmelter0.getFlag().getPosition());

        assertEquals(ironFounder.getPosition(), ironSmelter0.getFlag().getPosition());
        assertNotEquals(ironFounder.getTarget(), headquarter0.getPosition());
        assertFalse(ironFounder.isInsideBuilding());
        assertNull(ironSmelter0.getWorker());
        assertNotNull(ironFounder.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ironFounder, ironFounder.getTarget());

        Point point = ironFounder.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(ironFounder.isDead());
            assertEquals(ironFounder.getPosition(), point);
            assertTrue(map.getWorkers().contains(ironFounder));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(ironFounder));
    }
}
