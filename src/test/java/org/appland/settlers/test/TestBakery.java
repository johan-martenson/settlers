/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

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
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Unfinished samwill doesn't need worker */
        assertFalse(bakery.needsWorker());
        
        /* Finish construction of the bakery */
        Utils.constructHouse(bakery, map);
        
        assertTrue(bakery.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneBakerAtStart() {
        Headquarter hq = new Headquarter();
        
        assertTrue(hq.getAmount(BAKER) == 1);
    }
    
    @Test
    public void testBakeryGetsAssignedWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery, map);
        
        assertTrue(bakery.needsWorker());

        /* Verify that a bakery worker leaves the hq */
        Utils.fastForward(3, map);
        
        assertTrue(map.getAllWorkers().size() == 3);

        /* Let the bakery worker reach the bakery */
        Baker baker = null;

        for (Worker w : map.getAllWorkers()) {
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
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(), point3);

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
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(), point3);

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
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

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
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

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
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(), point3);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery, map);
        
        /* Populate the bakery */        
        Worker baker = Utils.occupyBuilding(new Baker(map), bakery, map);
        
        /* Deliver ingredients to the bakery */
        bakery.putCargo(new Cargo(WATER, map));
        bakery.putCargo(new Cargo(FLOUR, map));
        
        /* Wait until the bakery worker produces a bread */
        assertTrue(bakery.getAmount(WATER) == 1);
        assertTrue(bakery.getAmount(FLOUR) == 1);
        
        Utils.fastForward(150, map);
        
        assertTrue(bakery.getAmount(FLOUR) == 0);
        assertTrue(bakery.getAmount(WATER) == 0);
    }

    @Test
    public void testProductionCountdownStartsWhenIngredientsAreAvailable() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(), point3);

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
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing bakery */
        Point point26 = new Point(8, 8);
        Building bakery0 = map.placeBuilding(new Bakery(), point26);

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

        Worker ww = bakery0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the baker puts the bread cargo at the flag */
        assertEquals(ww.getTarget(), bakery0.getFlag().getPosition());
        assertTrue(bakery0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, bakery0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(bakery0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait for the worker to go back to the bakery */
        assertEquals(ww.getTarget(), bakery0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, bakery0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(ww.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(ww.getTarget(), bakery0.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, bakery0.getFlag().getPosition());
        
        assertNull(ww.getCargo());
        assertEquals(bakery0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing bakery */
        Point point26 = new Point(8, 8);
        Building bakery0 = map.placeBuilding(new Bakery(), point26);

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

        Worker ww = bakery0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the baker puts the bread cargo at the flag */
        assertEquals(ww.getTarget(), bakery0.getFlag().getPosition());
        assertTrue(bakery0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, bakery0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(bakery0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = bakery0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), bakery0.getFlag().getPosition());
    
        /* Connect the bakery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(headquarter0.getFlag(), bakery0.getFlag());
    
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
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing bakery */
        Point point26 = new Point(8, 8);
        Building bakery0 = map.placeBuilding(new Bakery(), point26);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0, map);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(map), bakery0, map);
        
        /* Destroy the bakery */
        Worker ww = bakery0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), bakery0.getPosition());

        bakery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(BAKER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());

        /* Verify that the baker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(BAKER), amount + 1);
    }

    @Test
    public void testBakerGoesBackOnToStorageOnRoadsIfPossibleWhenBakeryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing bakery */
        Point point26 = new Point(8, 8);
        Building bakery0 = map.placeBuilding(new Bakery(), point26);

        /* Connect the bakery with the headquarter */
        map.placeAutoSelectedRoad(bakery0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the bakery */
        Utils.constructHouse(bakery0, map);

        /* Occupy the bakery */
        Utils.occupyBuilding(new Baker(map), bakery0, map);
        
        /* Destroy the bakery */
        Worker ww = bakery0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), bakery0.getPosition());

        bakery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point p : ww.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }
}
