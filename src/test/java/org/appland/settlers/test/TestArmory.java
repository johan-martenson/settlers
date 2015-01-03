/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.ARMORER;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Armory;
import org.appland.settlers.model.Armorer;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.Material;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Worker;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestArmory {
    
    @Test
    public void testArmoryNeedsWorker() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Unfinished armory doesn't need worker */
        assertFalse(armory.needsWorker());
        
        /* Finish construction of the armory */
        Utils.constructHouse(armory, map);
        
        assertTrue(armory.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneArmorerAtStart() {
        Headquarter hq = new Headquarter(null);
        
        assertEquals(hq.getAmount(ARMORER), 1);
    }
    
    @Test
    public void testArmoryGetsAssignedWorker() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the armory */
        Utils.constructHouse(armory, map);
        
        assertTrue(armory.needsWorker());

        /* Verify that a armory worker leaves the hq */
        Utils.fastForward(3, map);
        
        assertEquals(map.getWorkers().size(), 3);

        /* Let the armory worker reach the armory */
        Armorer armorer = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Armorer) {
                armorer = (Armorer)w;
            }
        }

        assertNotNull(armorer);
        assertEquals(armorer.getTarget(), armory.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, armorer);
        
        assertTrue(armorer.isInsideBuilding());
        assertEquals(armorer.getHome(), armory);
        assertEquals(armory.getWorker(), armorer);
    }
    
    @Test
    public void testOccupiedArmoryWithoutCoalAndIronProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point3);

        /* Finish construction of the armory */
        Utils.constructHouse(armory, map);
        
        /* Populate the armory */
        Worker armorer = Utils.occupyBuilding(new Armorer(player0, map), armory, map);
        
        assertTrue(armorer.isInsideBuilding());
        assertEquals(armorer.getHome(), armory);
        assertEquals(armory.getWorker(), armorer);        

        /* Verify that the armory doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(armory.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer.getCargo());
            map.stepTime();
        }
    }
    
    @Test
    public void testUnoccupiedArmoryProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point3);

        /* Finish construction of the armory */
        Utils.constructHouse(armory, map);

        /* Verify that the armory doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(armory.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedArmoryWithCoalAndIronProducesWeapon() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the armory */
        Utils.constructHouse(armory, map);
        
        /* Populate the armory */        
        Worker armorer = Utils.occupyBuilding(new Armorer(player0, map), armory, map);
        
        assertTrue(armorer.isInsideBuilding());
        assertEquals(armorer.getHome(), armory);
        assertEquals(armory.getWorker(), armorer);        

        /* Deliver material to the armory */
        armory.putCargo(new Cargo(IRON, map));
        armory.putCargo(new Cargo(COAL, map));
        
        /* Verify that the armory produces weapons */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(armory.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer.getCargo());
        }

        map.stepTime();
        
        assertNotNull(armorer.getCargo());
        assertEquals(armorer.getCargo().getMaterial(), SWORD);
        assertTrue(armory.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testArmorerLeavesWeaponAtTheFlag() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the armory */
        Utils.constructHouse(armory, map);
        
        /* Populate the armory */        
        Worker armorer = Utils.occupyBuilding(new Armorer(player0, map), armory, map);
        
        assertTrue(armorer.isInsideBuilding());
        assertEquals(armorer.getHome(), armory);
        assertEquals(armory.getWorker(), armorer);        

        /* Deliver ingredients to the armory */
        armory.putCargo(new Cargo(IRON, map));
        armory.putCargo(new Cargo(COAL, map));
        
        /* Verify that the armory produces weapons */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(armory.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer.getCargo());
        }

        map.stepTime();
        
        assertNotNull(armorer.getCargo());
        assertEquals(armorer.getCargo().getMaterial(), SWORD);
        assertTrue(armory.getFlag().getStackedCargo().isEmpty());
        
        /* Verify that the armory worker leaves the cargo at the flag */
        assertEquals(armorer.getTarget(), armory.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, armorer, armory.getFlag().getPosition());
        
        assertFalse(armory.getFlag().getStackedCargo().isEmpty());
        assertNull(armorer.getCargo());
        assertEquals(armorer.getTarget(), armory.getPosition());
        
        /* Verify that the armorer goes back to the armory */
        Utils.fastForwardUntilWorkersReachTarget(map, armorer);
        
        assertTrue(armorer.isInsideBuilding());
    }

    @Test
    public void testProductionOfOneSwordConsumesOneCoalAndOneIron() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point3);

        /* Finish construction of the armory */
        Utils.constructHouse(armory, map);
        
        /* Populate the armory */        
        Worker armorer = Utils.occupyBuilding(new Armorer(player0, map), armory, map);
        
        /* Deliver ingredients to the armory */
        armory.putCargo(new Cargo(IRON, map));
        armory.putCargo(new Cargo(COAL, map));
        
        /* Wait until the armory worker produces a weapons */
        assertEquals(armory.getAmount(IRON), 1);
        assertEquals(armory.getAmount(COAL), 1);
        
        Utils.fastForward(150, map);
        
        assertEquals(armory.getAmount(COAL), 0);
        assertEquals(armory.getAmount(IRON), 0);
    }

    @Test
    public void testProductionCountdownStartsWhenMaterialsAreAvailable() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point3);

        /* Finish construction of the armory */
        Utils.constructHouse(armory, map);
        
        /* Populate the armory */        
        Worker armorer = Utils.occupyBuilding(new Armorer(player0, map), armory, map);
        
        /* Fast forward so that the armory worker would have produced weapons
           if it had had the ingredients
        */        
        Utils.fastForward(150, map);
        
        assertNull(armorer.getCargo());
        
        /* Deliver ingredients to the armory */
        armory.putCargo(new Cargo(IRON, map));
        armory.putCargo(new Cargo(COAL, map));
        
        /* Verify that it takes 50 steps for the armory worker to produce the planck */
        int i;
        for (i = 0; i < 50; i++) {
            assertNull(armorer.getCargo());
            map.stepTime();
        }
        
        assertNotNull(armorer.getCargo());
    }

    @Test
    public void testArmoryShiftsBetweenProducingSwordsAndShields() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the armory */
        Utils.constructHouse(armory, map);
        
        /* Populate the armory */        
        Worker armorer = Utils.occupyBuilding(new Armorer(player0, map), armory, map);
        
        assertTrue(armorer.isInsideBuilding());
        assertEquals(armorer.getHome(), armory);
        assertEquals(armory.getWorker(), armorer);        

        /* Deliver material to the armory */
        armory.putCargo(new Cargo(IRON, map));
        armory.putCargo(new Cargo(IRON, map));
        armory.putCargo(new Cargo(COAL, map));
        armory.putCargo(new Cargo(COAL, map));
        
        /* Verify that the armory produces a sword*/
        Utils.fastForward(150, map);
        
        assertNotNull(armorer.getCargo());
        assertEquals(armorer.getCargo().getMaterial(), SWORD);
        
        /* Wait for the armorer to put the sword at the flag */
        assertEquals(armorer.getTarget(), armory.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, armorer, armory.getFlag().getPosition());
        
        /* Wait for the armorer to go back to the armory */
        assertEquals(armorer.getTarget(), armory.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, armorer);
        
        /* Verify that the armorer produces a shield */
        Utils.fastForward(150, map);
        
        assertEquals(armorer.getCargo().getMaterial(), SHIELD);
    }

    @Test
    public void testArmoryWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing armory */
        Point point26 = new Point(8, 8);
        Building armory0 = map.placeBuilding(new Armory(player0), point26);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0, map);

        /* Occupy the armory */
        Utils.occupyBuilding(new Armorer(player0, map), armory0, map);

        /* Deliver material to the armory */
        Cargo ironCargo = new Cargo(IRON, map);
        Cargo coalCargo = new Cargo(COAL, map);
        
        armory0.putCargo(ironCargo);
        armory0.putCargo(ironCargo);

        armory0.putCargo(coalCargo);
        armory0.putCargo(coalCargo);
        
        /* Let the armorer rest */
        Utils.fastForward(100, map);

        /* Wait for the armorer to produce a new weapon cargo */
        Utils.fastForward(50, map);

        Worker armorer = armory0.getWorker();

        assertNotNull(armorer.getCargo());

        /* Verify that the armorer puts the weapon cargo at the flag */
        assertEquals(armorer.getTarget(), armory0.getFlag().getPosition());
        assertTrue(armory0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer, armory0.getFlag().getPosition());

        assertNull(armorer.getCargo());
        assertFalse(armory0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait for the worker to go back to the armory */
        assertEquals(armorer.getTarget(), armory0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, armorer, armory0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(armorer.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(armorer.getTarget(), armory0.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, armorer, armory0.getFlag().getPosition());
        
        assertNull(armorer.getCargo());
        assertEquals(armory0.getFlag().getStackedCargo().size(), 2);
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
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing armory */
        Point point26 = new Point(8, 8);
        Building armory0 = map.placeBuilding(new Armory(player0), point26);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0, map);

        /* Deliver material to the armory */
        Cargo ironCargo = new Cargo(IRON, map);
        Cargo coalCargo = new Cargo(COAL, map);
        
        armory0.putCargo(ironCargo);
        armory0.putCargo(ironCargo);

        armory0.putCargo(coalCargo);
        armory0.putCargo(coalCargo);

        /* Occupy the armory */
        Utils.occupyBuilding(new Armorer(player0, map), armory0, map);

        /* Let the armorer rest */
        Utils.fastForward(100, map);

        /* Wait for the armorer to produce a new weapon cargo */
        Utils.fastForward(50, map);

        Worker armorer = armory0.getWorker();

        assertNotNull(armorer.getCargo());

        /* Verify that the armorer puts the weapon cargo at the flag */
        assertEquals(armorer.getTarget(), armory0.getFlag().getPosition());
        assertTrue(armory0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer, armory0.getFlag().getPosition());

        assertNull(armorer.getCargo());
        assertFalse(armory0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = armory0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), armory0.getFlag().getPosition());
    
        /* Connect the armory with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory0.getFlag());
    
        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);
    
        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(armory0.getFlag().getPosition()));
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

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing armory */
        Point point26 = new Point(8, 8);
        Building armory0 = map.placeBuilding(new Armory(player0), point26);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0, map);

        /* Occupy the armory */
        Utils.occupyBuilding(new Armorer(player0, map), armory0, map);
        
        /* Destroy the armory */
        Worker armorer = armory0.getWorker();
        
        assertTrue(armorer.isInsideBuilding());
        assertEquals(armorer.getPosition(), armory0.getPosition());

        armory0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(armorer.isInsideBuilding());
        assertEquals(armorer.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(ARMORER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, armorer, headquarter0.getPosition());

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

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing armory */
        Point point26 = new Point(8, 8);
        Building armory0 = map.placeBuilding(new Armory(player0), point26);

        /* Connect the armory with the headquarter */
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the armory */
        Utils.constructHouse(armory0, map);

        /* Occupy the armory */
        Utils.occupyBuilding(new Armorer(player0, map), armory0, map);
        
        /* Destroy the armory */
        Worker armorer = armory0.getWorker();
        
        assertTrue(armorer.isInsideBuilding());
        assertEquals(armorer.getPosition(), armory0.getPosition());

        armory0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(armorer.isInsideBuilding());
        assertEquals(armorer.getTarget(), headquarter0.getPosition());
    
        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point p : armorer.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
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
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place armory */
        Point point1 = new Point(8, 6);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);
        
        /* Connect the armory and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the armory */
        Utils.constructHouse(armory0, map);
        
        /* Deliver material to the armory */
        Cargo ironCargo = new Cargo(IRON, map);
        Cargo coalCargo = new Cargo(COAL, map);
        
        armory0.putCargo(ironCargo);
        armory0.putCargo(ironCargo);

        armory0.putCargo(coalCargo);
        armory0.putCargo(coalCargo);

        /* Assign a worker to the armory */
        Armorer armorer = new Armorer(player0, map);
        
        Utils.occupyBuilding(armorer, armory0, map);
        
        assertTrue(armorer.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the armorer to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, armorer);
        
        /* Wait for the worker to deliver the cargo */
        assertEquals(armorer.getTarget(), armory0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer, armory0.getFlag().getPosition());

        /* Stop production and verify that no water is produced */
        armory0.stopProduction();
        
        assertFalse(armory0.isProductionEnabled());
        
        for (int i = 0; i < 300; i++) {
            assertNull(armorer.getCargo());
            
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
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place armory */
        Point point1 = new Point(8, 6);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);
        
        /* Connect the armory and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the armory */
        Utils.constructHouse(armory0, map);
        
        /* Assign a worker to the armory */
        Armorer armorer = new Armorer(player0, map);
        
        Utils.occupyBuilding(armorer, armory0, map);
        
        assertTrue(armorer.isInsideBuilding());

        /* Deliver material to the armory */
        Cargo ironCargo = new Cargo(IRON, map);
        Cargo coalCargo = new Cargo(COAL, map);
        
        armory0.putCargo(ironCargo);
        armory0.putCargo(ironCargo);

        armory0.putCargo(coalCargo);
        armory0.putCargo(coalCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the armorer to produce water */
        Utils.fastForwardUntilWorkerProducesCargo(map, armorer);

        /* Wait for the worker to deliver the cargo */
        assertEquals(armorer.getTarget(), armory0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer, armory0.getFlag().getPosition());

        /* Stop production */
        armory0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(armorer.getCargo());
            
            map.stepTime();
        }

        /* Resume production and verify that the armory produces water again */
        armory0.resumeProduction();

        assertTrue(armory0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, armorer);

        assertNotNull(armorer.getCargo());
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
        Point hqPoint = new Point(15, 15);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place armory*/
        Point point1 = new Point(20, 14);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0, map);
        
        /* Connect the armory with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory0.getFlag());

        /* Wait for armorer to get assigned and leave the headquarter */
        List<Armorer> workers = Utils.waitForWorkersOutsideBuilding(Armorer.class, 1, player0, map);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Armorer worker = workers.get(0);

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
        Building headquarter2 = new Headquarter(player2);
        Point point10 = new Point(70, 70);
        map.placeBuilding(headquarter2, point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
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

        /* Place armory close to the new border */
        Point point4 = new Point(28, 18);
        Armory armory0 = map.placeBuilding(new Armory(player0), point4);

        /* Finish construction of the armory */
        Utils.constructHouse(armory0, map);

        /* Occupy the armory */
        Armorer worker = Utils.occupyBuilding(new Armorer(player0, map), armory0, map);

        /* Verify that the enemy's headquarter is closer */
        assertTrue(armory0.getPosition().distance(headquarter0.getPosition()) > 
                   armory0.getPosition().distance(headquarter1.getPosition()));

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down*/
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }
}
