/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Armorer;
import org.appland.settlers.model.Armory;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
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
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static org.appland.settlers.model.Material.ARMORER;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.IRON_BAR;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author johan
 */
public class TestArmory {

    @Test
    public void testArmoryOnlyNeedsTwoPlanksAndTwoStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(6, 22);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Deliver two plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        armory0.putCargo(plankCargo);
        armory0.putCargo(plankCargo);
        armory0.putCargo(stoneCargo);
        armory0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the armory */
        for (int i = 0; i < 150; i++) {
            assertTrue(armory0.underConstruction());

            map.stepTime();
        }

        assertTrue(armory0.isReady());
    }

    @Test
    public void testArmoryCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(6, 22);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Deliver one plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        armory0.putCargo(plankCargo);
        armory0.putCargo(stoneCargo);
        armory0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the armory */
        for (int i = 0; i < 500; i++) {
            assertTrue(armory0.underConstruction());

            map.stepTime();
        }

        assertFalse(armory0.isReady());
    }

    @Test
    public void testArmoryCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(6, 22);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Deliver two planks and one stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        armory0.putCargo(plankCargo);
        armory0.putCargo(plankCargo);
        armory0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the armory */
        for (int i = 0; i < 500; i++) {
            assertTrue(armory0.underConstruction());

            map.stepTime();
        }

        assertFalse(armory0.isReady());
    }

    @Test
    public void testArmoryNeedsWorker() throws Exception {

        /* Create new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point1);

        /* Place road */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Unfinished armory doesn't need worker */
        assertFalse(armory.needsWorker());

        /* Finish construction of the armory */
        Utils.constructHouse(armory);

        assertTrue(armory.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneArmorerAtStart() {
        Headquarter headquarter = new Headquarter(null);

        assertEquals(headquarter.getAmount(ARMORER), 1);
    }

    @Test
    public void testArmoryGetsAssignedWorker() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(7, 9);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Place road to connect the armory with the headquarter */
        Point point2 = new Point(8, 8);
        Point point3 = new Point(7, 7);
        Point point4 = new Point(8, 6);
        Point point5 = new Point(7, 5);
        Point point6 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point2, point3, point4, point5, point6);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        assertTrue(armory0.needsWorker());

        /* Verify that a armory worker leaves the headquarter */
        Utils.fastForward(3, map);

        assertEquals(map.getWorkers().size(), 3);

        /* Let the armory worker reach the armory */
        Armorer armorer0 = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Armorer) {
                armorer0 = (Armorer)worker;
            }
        }

        assertNotNull(armorer0);
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, armorer0);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory0);
        assertEquals(armory0.getWorker(), armorer0);
    }

    @Test
    public void testOccupiedArmoryWithoutCoalAndIronProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory);

        /* Populate the armory */
        Worker armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory);
        assertEquals(armory.getWorker(), armorer0);

        /* Verify that the armory doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(armory.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedArmoryProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point3);

        /* Finish construction of the armory */
        Utils.constructHouse(armory);

        /* Verify that the armory doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(armory.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedArmoryWithCoalAndIronProducesWeapon() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(7, 9);
        Armory armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Place road to connect the armory with the headquarter */
        Point point2 = new Point(8, 8);
        Point point3 = new Point(7, 7);
        Point point4 = new Point(8, 6);
        Point point5 = new Point(7, 5);
        Point point6 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point2, point3, point4, point5, point6);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Populate the armory */
        Worker armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory0);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory0);
        assertEquals(armory0.getWorker(), armorer0);

        /* Deliver material to the armory */
        armory0.putCargo(new Cargo(IRON_BAR, map));
        armory0.putCargo(new Cargo(COAL, map));

        /* Verify that the armory produces weapons */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(armory0.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
        }

        map.stepTime();

        assertNotNull(armorer0.getCargo());
        assertEquals(armorer0.getCargo().getMaterial(), SWORD);
        assertTrue(armory0.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testArmorerLeavesWeaponAtTheFlag() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(7, 9);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Place road to connect the armory with the headquarter */
        Point point2 = new Point(8, 8);
        Point point3 = new Point(7, 7);
        Point point4 = new Point(8, 6);
        Point point5 = new Point(7, 5);
        Point point6 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point2, point3, point4, point5, point6);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Populate the armory */
        Worker armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory0);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory0);
        assertEquals(armory0.getWorker(), armorer0);

        /* Deliver ingredients to the armory */
        armory0.putCargo(new Cargo(IRON_BAR, map));
        armory0.putCargo(new Cargo(COAL, map));

        /* Verify that the armory produces weapons */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(armory0.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
        }

        map.stepTime();

        assertNotNull(armorer0.getCargo());
        assertEquals(armorer0.getCargo().getMaterial(), SWORD);
        assertTrue(armory0.getFlag().getStackedCargo().isEmpty());

        /* Verify that the armory worker leaves the cargo at the flag */
        assertEquals(armorer0.getTarget(), armory0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        assertFalse(armory0.getFlag().getStackedCargo().isEmpty());
        assertNull(armorer0.getCargo());
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        /* Verify that the armorer goes back to the armory */
        Utils.fastForwardUntilWorkersReachTarget(map, armorer0);

        assertTrue(armorer0.isInsideBuilding());
    }

    @Test
    public void testProductionOfOneSwordConsumesOneCoalAndOneIron() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(7, 9);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Populate the armory */
        Worker armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Deliver ingredients to the armory */
        armory0.putCargo(new Cargo(IRON_BAR, map));
        armory0.putCargo(new Cargo(COAL, map));

        /* Wait until the armory worker produces a weapons */
        assertEquals(armory0.getAmount(IRON_BAR), 1);
        assertEquals(armory0.getAmount(COAL), 1);

        Utils.fastForward(150, map);

        assertEquals(armory0.getAmount(COAL), 0);
        assertEquals(armory0.getAmount(IRON_BAR), 0);
    }

    @Test
    public void testProductionCountdownStartsWhenMaterialsAreAvailable() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(7, 9);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Populate the armory */
        Worker armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Fast forward so that the armory worker would have produced weapons
           if it had had the ingredients
        */
        Utils.fastForward(150, map);

        assertNull(armorer0.getCargo());

        /* Deliver ingredients to the armory */
        armory0.putCargo(new Cargo(IRON_BAR, map));
        armory0.putCargo(new Cargo(COAL, map));

        /* Verify that it takes 50 steps for the armory worker to produce the plank */
        for (int i = 0; i < 50; i++) {
            assertNull(armorer0.getCargo());
            map.stepTime();
        }

        assertNotNull(armorer0.getCargo());
    }

    @Test
    public void testArmoryShiftsBetweenProducingSwordsAndShields() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(7, 9);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Place road to connect the armory with the headquarter */
        Point point2 = new Point(8, 8);
        Point point3 = new Point(7, 7);
        Point point4 = new Point(8, 6);
        Point point5 = new Point(7, 5);
        Point point6 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point2, point3, point4, point5, point6);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Populate the armory */
        Worker armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory0);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory0);
        assertEquals(armory0.getWorker(), armorer0);

        /* Deliver material to the armory */
        armory0.putCargo(new Cargo(IRON_BAR, map));
        armory0.putCargo(new Cargo(IRON_BAR, map));
        armory0.putCargo(new Cargo(COAL, map));
        armory0.putCargo(new Cargo(COAL, map));

        /* Verify that the armory produces a sword */
        Utils.fastForward(150, map);

        assertNotNull(armorer0.getCargo());
        assertEquals(armorer0.getCargo().getMaterial(), SWORD);

        /* Wait for the armorer to put the sword at the flag */
        assertEquals(armorer0.getTarget(), armory0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        /* Wait for the armorer to go back to the armory */
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, armorer0);

        /* Verify that the armorer produces a shield */
        Utils.fastForward(150, map);

        assertEquals(armorer0.getCargo().getMaterial(), SHIELD);
    }

    @Test
    public void testArmoryWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(8, 8);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Occupy the armory */
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Deliver material to the armory */
        Cargo ironCargo = new Cargo(IRON_BAR, map);
        Cargo coalCargo = new Cargo(COAL, map);

        armory0.putCargo(ironCargo);
        armory0.putCargo(ironCargo);

        armory0.putCargo(coalCargo);
        armory0.putCargo(coalCargo);

        /* Let the armorer rest */
        Utils.fastForward(100, map);

        /* Wait for the armorer to produce a new weapon cargo */
        Utils.fastForward(50, map);

        Worker armorer0 = armory0.getWorker();

        assertNotNull(armorer0.getCargo());

        /* Verify that the armorer puts the weapon cargo at the flag */
        assertEquals(armorer0.getTarget(), armory0.getFlag().getPosition());
        assertTrue(armory0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        assertNull(armorer0.getCargo());
        assertFalse(armory0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the armory */
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(armorer0.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(armorer0.getTarget(), armory0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        assertNull(armorer0.getCargo());
        assertEquals(armory0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(8, 8);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Deliver material to the armory */
        Cargo ironCargo = new Cargo(IRON_BAR, map);
        Cargo coalCargo = new Cargo(COAL, map);

        armory0.putCargo(ironCargo);
        armory0.putCargo(ironCargo);

        armory0.putCargo(coalCargo);
        armory0.putCargo(coalCargo);

        /* Occupy the armory */
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Let the armorer rest */
        Utils.fastForward(100, map);

        /* Wait for the armorer to produce a new weapon cargo */
        Utils.fastForward(50, map);

        Worker armorer0 = armory0.getWorker();

        assertNotNull(armorer0.getCargo());

        /* Verify that the armorer puts the weapon cargo at the flag */
        assertEquals(armorer0.getTarget(), armory0.getFlag().getPosition());
        assertTrue(armory0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        assertNull(armorer0.getCargo());
        assertFalse(armory0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = armory0.getFlag().getStackedCargo().get(0);

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), armory0.getFlag().getPosition());

        /* Remove material the armory needs from the headquarter */
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 0);
        Utils.adjustInventoryTo(headquarter0, COAL, 0);

        /* Connect the armory with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), armory0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), armory0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);
        assertTrue(cargo.getMaterial() == SWORD || cargo.getMaterial() == SHIELD);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        Material material = cargo.getMaterial();
        int amount = headquarter0.getAmount(material);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(material), amount + 1);
    }

    @Test
    public void testArmorerGoesBackToStorageWhenArmoryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(8, 8);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Occupy the armory */
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Destroy the armory */
        Worker armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getPosition(), armory0.getPosition());

        armory0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(armorer0.isInsideBuilding());
        assertEquals(armorer0.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(ARMORER);

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, headquarter0.getPosition());

        /* Verify that the armorer is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(ARMORER), amount + 1);
    }

    @Test
    public void testArmorerGoesBackOnToStorageOnRoadsIfPossibleWhenArmoryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(8, 8);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Connect the armory with the headquarter */
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Occupy the armory */
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Destroy the armory */
        Worker armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getPosition(), armory0.getPosition());

        armory0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(armorer0.isInsideBuilding());
        assertEquals(armorer0.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point point : armorer0.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testProductionInArmoryCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(12, 8);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Connect the armory and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Finish the armory */
        Utils.constructHouse(armory0);

        /* Deliver material to the armory */
        Cargo ironCargo = new Cargo(IRON_BAR, map);
        Cargo coalCargo = new Cargo(COAL, map);

        armory0.putCargo(ironCargo);
        armory0.putCargo(ironCargo);

        armory0.putCargo(coalCargo);
        armory0.putCargo(coalCargo);

        /* Assign a worker to the armory */
        Armorer armorer0 = new Armorer(player0, map);

        Utils.occupyBuilding(armorer0, armory0);

        assertTrue(armorer0.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the armorer to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, armorer0);

        /* Wait for the worker to deliver the cargo */
        assertEquals(armorer0.getTarget(), armory0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        /* Stop production and verify that no water is produced */
        armory0.stopProduction();

        assertFalse(armory0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(armorer0.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInArmoryCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(12, 8);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Connect the armory and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Finish the armory */
        Utils.constructHouse(armory0);

        /* Assign a worker to the armory */
        Armorer armorer0 = new Armorer(player0, map);

        Utils.occupyBuilding(armorer0, armory0);

        assertTrue(armorer0.isInsideBuilding());

        /* Deliver material to the armory */
        Cargo ironCargo = new Cargo(IRON_BAR, map);
        Cargo coalCargo = new Cargo(COAL, map);

        armory0.putCargo(ironCargo);
        armory0.putCargo(ironCargo);

        armory0.putCargo(coalCargo);
        armory0.putCargo(coalCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the armorer to produce water */
        Utils.fastForwardUntilWorkerProducesCargo(map, armorer0);

        /* Wait for the worker to deliver the cargo */
        assertEquals(armorer0.getTarget(), armory0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        /* Stop production */
        armory0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(armorer0.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the armory produces water again */
        armory0.resumeProduction();

        assertTrue(armory0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, armorer0);

        assertNotNull(armorer0.getCargo());
    }

    @Test
    public void testAssignedArmorerHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(20, 14);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Connect the armory with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory0.getFlag());

        /* Wait for armorer to get assigned and leave the headquarter */
        List<Armorer> workers = Utils.waitForWorkersOutsideBuilding(Armorer.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Armorer worker = workers.get(0);

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with three players */
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

        /* Place armory close to the new border */
        Point point4 = new Point(28, 18);
        Armory armory0 = map.placeBuilding(new Armory(player0), point4);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Occupy the armory */
        Armorer worker = Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Verify that the enemy's headquarter is closer */
        assertTrue(armory0.getPosition().distance(headquarter0.getPosition()) >
                   armory0.getPosition().distance(headquarter1.getPosition()));

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test (expected = InvalidUserActionException.class)
    public void testNonMilitaryBuildingCannotBeUpgraded() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(20, 14);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Verify that non-military building cannot be upgraded */
        armory0.upgrade();
    }

    @Test
    public void testArmorerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place armory */
        Point point2 = new Point(14, 4);
        Building armory0 = map.placeBuilding(new Armory(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, armory0.getFlag());

        /* Wait for the armorer to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Armorer.class, 1, player0);

        Armorer armorer0 = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Armorer) {
                armorer0 = (Armorer) worker;
            }
        }

        assertNotNull(armorer0);
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the armorer has started walking */
        assertFalse(armorer0.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the armorer continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, flag0.getPosition());

        assertEquals(armorer0.getPosition(), flag0.getPosition());

        /* Verify that the armorer returns to the headquarter when it reaches the flag */
        assertEquals(armorer0.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, headquarter0.getPosition());
    }

    @Test
    public void testArmorerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place armory */
        Point point2 = new Point(14, 4);
        Building armory0 = map.placeBuilding(new Armory(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, armory0.getFlag());

        /* Wait for the armorer to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Armorer.class, 1, player0);

        Armorer armorer0 = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Armorer) {
                armorer0 = (Armorer) worker;
            }
        }

        assertNotNull(armorer0);
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the armorer has started walking */
        assertFalse(armorer0.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the armorer continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, flag0.getPosition());

        assertEquals(armorer0.getPosition(), flag0.getPosition());

        /* Verify that the armorer continues to the final flag */
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        /* Verify that the armorer goes out to the armory instead of going directly back */
        assertNotEquals(armorer0.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testArmorerReturnsToStorageIfArmoryIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place armory */
        Point point2 = new Point(14, 4);
        Building armory0 = map.placeBuilding(new Armory(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, armory0.getFlag());

        /* Wait for the armorer to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Armorer.class, 1, player0);

        Armorer armorer0 = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Armorer) {
                armorer0 = (Armorer) worker;
            }
        }

        assertNotNull(armorer0);
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        /* Wait for the armorer to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, flag0.getPosition());

        map.stepTime();

        /* See that the armorer has started walking */
        assertFalse(armorer0.isExactlyAtPoint());

        /* Tear down the armory */
        armory0.tearDown();

        /* Verify that the armorer continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        assertEquals(armorer0.getPosition(), armory0.getFlag().getPosition());

        /* Verify that the armorer goes back to storage */
        assertEquals(armorer0.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testCannotTearDownArmoryTwice() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point2 = new Point(14, 4);
        Building armory0 = map.placeBuilding(new Armory(player0), point2.upLeft());

        /* Connect armory with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory0.getFlag());

        /* Wait for the armory to get constructed */
        Utils.fastForwardUntilBuildingIsConstructed(armory0);

        /* Tear down the armory */
        armory0.tearDown();

        /* Verify that it cannot be torn down twice */
        try {
            armory0.tearDown();
            fail();
        } catch (Throwable t) {
            assertEquals(t.getClass(), InvalidUserActionException.class);
        }
    }

    @Test
    public void testArmorerGoesOffroadBackToClosestStorageWhenArmoryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(17, 17);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Occupy the armory */
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Place a second storage closer to the armory */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the armory */
        Worker armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getPosition(), armory0.getPosition());

        armory0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(armorer0.isInsideBuilding());
        assertEquals(armorer0.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(ARMORER);

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, storehouse0.getPosition());

        /* Verify that the armorer is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(ARMORER), amount + 1);
    }

    @Test
    public void testArmorerReturnsOffroadAndAvoidsBurningStorageWhenArmoryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(17, 17);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Occupy the armory */
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Place a second storage closer to the armory */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Destroy the armory */
        Worker armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getPosition(), armory0.getPosition());

        armory0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(armorer0.isInsideBuilding());
        assertEquals(armorer0.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(ARMORER);

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, headquarter0.getPosition());

        /* Verify that the armorer is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(ARMORER), amount + 1);
    }

    @Test
    public void testArmorerReturnsOffroadAndAvoidsDestroyedStorageWhenArmoryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(17, 17);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Occupy the armory */
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Place a second storage closer to the armory */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

        /* Destroy the armory */
        Worker armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getPosition(), armory0.getPosition());

        armory0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(armorer0.isInsideBuilding());
        assertEquals(armorer0.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(ARMORER);

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, headquarter0.getPosition());

        /* Verify that the armorer is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(ARMORER), amount + 1);
    }

    @Test
    public void testArmorerReturnsOffroadAndAvoidsUnfinishedStorageWhenArmoryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(17, 17);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Occupy the armory */
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Place a second storage closer to the armory */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the armory */
        Worker armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getPosition(), armory0.getPosition());

        armory0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(armorer0.isInsideBuilding());
        assertEquals(armorer0.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(ARMORER);

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, headquarter0.getPosition());

        /* Verify that the armorer is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(ARMORER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place armory */
        Point point2 = new Point(17, 17);
        Building armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Place road to connect the headquarter and the armory */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory0.getFlag());

        /* Finish construction of the armory */
        Utils.constructHouse(armory0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Armorer.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, armory0.getFlag().getPosition());

        /* Tear down the building */
        armory0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquarter instead of entering
        */
        assertEquals(worker.getTarget(), armory0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, armory0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testArmoryWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory);

        /* Populate the armory */
        Worker armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory);
        assertEquals(armory.getWorker(), armorer0);

        /* Verify that the productivity is 0% when the armory doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(armory.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
            assertEquals(armory.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testArmoryWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory);

        /* Populate the armory */
        Worker armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory);
        assertEquals(armory.getWorker(), armorer0);

        /* Connect the armory with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory.getFlag());

        /* Make the armory create some weapons with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (armory.needsMaterial(COAL)) {
                armory.putCargo(new Cargo(COAL, map));
            }

            if (armory.needsMaterial(IRON_BAR)) {
                armory.putCargo(new Cargo(IRON_BAR, map));
            }
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(armory.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (armory.needsMaterial(COAL)) {
                armory.putCargo(new Cargo(COAL, map));
            }

            if (armory.needsMaterial(IRON_BAR)) {
                armory.putCargo(new Cargo(IRON_BAR, map));
            }

            assertEquals(armory.getProductivity(), 100);
        }
    }

    @Test
    public void testArmoryLosesProductivityWhenResourcesRunOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory);

        /* Populate the armory */
        Worker armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory);
        assertEquals(armory.getWorker(), armorer0);

        /* Remove the resources the armory needs from the headquarter */
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 0);
        Utils.adjustInventoryTo(headquarter0, COAL, 0);

        /* Connect the armory with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory.getFlag());

        /* Make the armory create some weapons with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (armory.needsMaterial(COAL) && armory.getAmount(COAL) < 2) {
                armory.putCargo(new Cargo(COAL, map));
            }

            if (armory.needsMaterial(IRON_BAR) && armory.getAmount(IRON_BAR) < 2) {
                armory.putCargo(new Cargo(IRON_BAR, map));
            }
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(armory.getProductivity(), 100);

        for (int i = 0; i < 2000; i++) {
            map.stepTime();
        }

        assertEquals(armory.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedArmoryHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory);

        /* Verify that the unoccupied armory is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(armory.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testArmoryCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory);

        /* Populate the armory */
        Worker armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory);

        /* Verify that the armory can produce */
        assertTrue(armory.canProduce());
    }

    @Test
    public void testArmoryReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(6, 22);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Construct the armory */
        Utils.constructHouse(armory0);

        /* Verify that the reported output is correct */
        assertEquals(armory0.getProducedMaterial().length, 2);
        assertTrue((armory0.getProducedMaterial()[0] == SWORD && armory0.getProducedMaterial()[1] == SHIELD) ||
                   (armory0.getProducedMaterial()[1] == SWORD && armory0.getProducedMaterial()[0] == SHIELD));
    }

    @Test
    public void testArmoryReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(6, 22);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(armory0.getMaterialNeeded().size(), 2);
        assertTrue(armory0.getMaterialNeeded().contains(PLANK));
        assertTrue(armory0.getMaterialNeeded().contains(STONE));
        assertEquals(armory0.getTotalAmountNeeded(PLANK), 2);
        assertEquals(armory0.getTotalAmountNeeded(STONE), 2);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(armory0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testArmoryReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(6, 22);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Construct the armory */
        Utils.constructHouse(armory0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(armory0.getMaterialNeeded().size(), 2);
        assertTrue(armory0.getMaterialNeeded().contains(COAL));
        assertTrue(armory0.getMaterialNeeded().contains(IRON_BAR));
        assertEquals(armory0.getTotalAmountNeeded(COAL), 2);
        assertEquals(armory0.getTotalAmountNeeded(IRON_BAR), 2);

        for (Material material : Material.values()) {
            if (material == COAL || material == IRON_BAR) {
                continue;
            }

            assertEquals(armory0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testUnoccupiedArmoryGetsMaximumMaterialButNotMore() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(6, 22);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Remove all armorers from the headquarter */
        Utils.adjustInventoryTo(headquarter0, ARMORER, 0);

        /* Add extra resources to the headquarter */
        Utils.adjustInventoryTo(headquarter0, COAL, 10);
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 10);

        /* Construct the armory */
        Utils.constructHouse(armory0);

        /* Connect the armory to the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory0.getFlag());

        /* Wait for the maximum amount of resources to get delivered */
        for (int i = 0; i < 2000; i++) {
            if (armory0.getAmount(COAL) == 2 && armory0.getAmount(IRON_BAR) == 2) {
                break;
            }

            map.stepTime();
        }

        assertEquals(armory0.getAmount(COAL), 2);
        assertEquals(armory0.getAmount(IRON_BAR), 2);

        /* Verify that the armory gets the maximum amount of resources but not more */
        for (int i = 0; i < 2000; i++) {
            assertEquals(armory0.getAmount(COAL), 2);
            assertEquals(armory0.getAmount(IRON_BAR), 2);

            map.stepTime();
        }
    }
}
