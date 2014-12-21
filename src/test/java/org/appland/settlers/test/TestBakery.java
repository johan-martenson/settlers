/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.Baker;
import org.appland.settlers.model.Bakery;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.BAKER;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.WATER;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
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
public class TestBakery {
    
    @Test
    public void testBakeryNeedsWorker() throws Exception {
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Unfinished samwill doesn't need worker */
        assertFalse(bakery.needsWorker());
        
        /* Finish construction of the bakery */
        Utils.constructHouse(bakery, map);
        
        assertTrue(bakery.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneBakerAtStart() {
        Headquarter hq = new Headquarter(null);
        
        assertEquals(hq.getAmount(BAKER), 1);
    }
    
    @Test
    public void testBakeryGetsAssignedWorker() throws Exception {
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery, map);
        
        assertTrue(bakery.needsWorker());

        /* Verify that a bakery worker leaves the hq */
        Utils.fastForward(3, map);
        
        assertEquals(map.getWorkers().size(), 3);

        /* Let the bakery worker reach the bakery */
        Baker baker = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Baker) {
                baker = (Baker)w;
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
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point3);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery, map);
        
        /* Populate the bakery */
        Worker baker = Utils.occupyBuilding(new Baker(map), bakery, map);
        
        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker);        

        /* Verify that the bakery doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            assertNull(baker.getCargo());
            map.stepTime();
        }
    }
    
    @Test
    public void testUnoccupiedBakeryProducesNothing() throws Exception {
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point3);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery, map);

        /* Verify that the bakery doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedBakeryWithIngredientsProducesBread() throws Exception {
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery, map);
        
        /* Populate the bakery */        
        Worker baker = Utils.occupyBuilding(new Baker(map), bakery, map);
        
        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker);        

        /* Deliver wood to the bakery */
        bakery.putCargo(new Cargo(WATER, map));
        bakery.putCargo(new Cargo(FLOUR, map));
        
        /* Verify that the bakery produces bread */
        int i;
        for (i = 0; i < 149; i++) {
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
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery, map);
        
        /* Populate the bakery */        
        Worker baker = Utils.occupyBuilding(new Baker(map), bakery, map);
        
        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker);        

        /* Deliver ingredients to the bakery */
        bakery.putCargo(new Cargo(WATER, map));
        bakery.putCargo(new Cargo(FLOUR, map));
        
        /* Verify that the bakery produces bread */
        int i;
        for (i = 0; i < 149; i++) {
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
    public void testProductionOfOneBreadConsumesOneWaterAndOneFlour() throws Exception {
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point3);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery, map);
        
        /* Populate the bakery */        
        Worker baker = Utils.occupyBuilding(new Baker(map), bakery, map);
        
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
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point3);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery, map);
        
        /* Populate the bakery */        
        Worker baker = Utils.occupyBuilding(new Baker(map), bakery, map);
        
        /* Fast forward so that the bakery worker would have produced bread
           if it had had the ingredients
        */        
        Utils.fastForward(150, map);
        
        assertNull(baker.getCargo());
        
        /* Deliver ingredients to the bakery */
        bakery.putCargo(new Cargo(WATER, map));
        bakery.putCargo(new Cargo(FLOUR, map));
        
        /* Verify that it takes 50 steps for the bakery worker to produce the planck */
        int i;
        for (i = 0; i < 50; i++) {
            assertNull(baker.getCargo());
            map.stepTime();
        }
        
        assertNotNull(baker.getCargo());
    }

    @Test
    public void testBakeryWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing bakery */
        Point point26 = new Point(8, 8);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0, map);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(map), bakery0, map);

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
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing bakery */
        Point point26 = new Point(8, 8);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0, map);

        /* Deliver material to the bakery */
        Cargo ironCargo = new Cargo(FLOUR, map);
        Cargo coalCargo = new Cargo(WATER, map);
        
        bakery0.putCargo(ironCargo);
        bakery0.putCargo(ironCargo);

        bakery0.putCargo(coalCargo);
        bakery0.putCargo(coalCargo);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(map), bakery0, map);

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
        Cargo cargo = bakery0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), bakery0.getFlag().getPosition());
    
        /* Connect the bakery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), bakery0.getFlag());
    
        /* Assign a courier to the road */
        Courier courier = new Courier(map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);
    
        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(bakery0.getFlag().getPosition()));
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
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing bakery */
        Point point26 = new Point(8, 8);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0, map);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(map), bakery0, map);
        
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
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing bakery */
        Point point26 = new Point(8, 8);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point26);

        /* Connect the bakery with the headquarter */
        map.placeAutoSelectedRoad(player0, bakery0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0, map);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(map), bakery0, map);
        
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
        for (Point p : baker.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test
    public void testProductionInBakeryCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place bakery */
        Point point1 = new Point(8, 6);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point1);
        
        /* Connect the bakery and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the bakery */
        Utils.constructHouse(bakery0, map);
        
        /* Deliver material to the bakery */
        Cargo ironCargo = new Cargo(FLOUR, map);
        Cargo coalCargo = new Cargo(WATER, map);
        
        bakery0.putCargo(ironCargo);
        bakery0.putCargo(ironCargo);

        bakery0.putCargo(coalCargo);
        bakery0.putCargo(coalCargo);

        /* Assign a worker to the bakery */
        Baker baker = new Baker(map);
        
        Utils.occupyBuilding(baker, bakery0, map);
        
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
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place bakery */
        Point point1 = new Point(8, 6);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point1);
        
        /* Connect the bakery and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the bakery */
        Utils.constructHouse(bakery0, map);
        
        /* Deliver material to the bakery */
        Cargo ironCargo = new Cargo(FLOUR, map);
        Cargo coalCargo = new Cargo(WATER, map);
        
        bakery0.putCargo(ironCargo);
        bakery0.putCargo(ironCargo);

        bakery0.putCargo(coalCargo);
        bakery0.putCargo(coalCargo);

        /* Assign a worker to the bakery */
        Baker baker = new Baker(map);
        
        Utils.occupyBuilding(baker, bakery0, map);
        
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
}
