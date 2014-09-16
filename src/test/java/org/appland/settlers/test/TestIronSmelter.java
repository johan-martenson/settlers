/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.IronFounder;
import org.appland.settlers.model.IronSmelter;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.IRON_BAR;
import static org.appland.settlers.model.Material.IRON_FOUNDER;
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
public class TestIronSmelter {
    
    @Test
    public void testIronSmelterNeedsWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironFounder = map.placeBuilding(new IronSmelter(), point3);

        /* Unfinished iron smelter doesn't need worker */
        assertFalse(ironFounder.needsWorker());
        
        /* Finish construction of the iron smelter */
        Utils.constructMediumHouse(ironFounder);
        
        assertTrue(ironFounder.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneIronFounderAtStart() {
        Headquarter hq = new Headquarter();
        
        assertTrue(hq.getAmount(IRON_FOUNDER) == 1);
    }
    
    @Test
    public void testIronSmelterGetsAssignedWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(), point3);

        /* Place a road between the headquarter and the iron smelter */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the iron smelter */
        Utils.constructMediumHouse(ironSmelter);
        
        assertTrue(ironSmelter.needsWorker());

        /* Verify that a iron smelter worker leaves the hq */        
        assertTrue(map.getAllWorkers().size() == 1);

        Utils.fastForward(3, map);
        
        assertTrue(map.getAllWorkers().size() == 3);

        Utils.verifyListContainsWorkerOfType(map.getAllWorkers(), IronFounder.class);
        
        /* Let the iron smelter worker reach the iron smelter */
        IronFounder sw = null;
        
        for (Worker w : map.getAllWorkers()) {
            if (w instanceof IronFounder) {
                sw = (IronFounder)w;
            }
        }
        
        assertNotNull(sw);
        assertEquals(sw.getTarget(), ironSmelter.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, sw);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), sw);
    }
    
    @Test
    public void testOccupiedIronSmelterWithoutCoalAndIronProducesNothing() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(), point3);

        /* Finish construction of the iron smelter */
        Utils.constructMediumHouse(ironSmelter);

        /* Occupy the iron smelter */
        Worker sw = Utils.occupyBuilding(new IronFounder(map), ironSmelter, map);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), sw);        

        /* Verify that the iron smelter doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
            assertNull(sw.getCargo());
            map.stepTime();
        }
    }
    
    @Test
    public void testUnoccupiedIronSmelterProducesNothing() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(), point3);

        /* Finish construction of the iron smelter */
        Utils.constructMediumHouse(ironSmelter);

        /* Verify that the iron smelter doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedIronSmelterWithIronAndCoalProducesIronBars() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(), point3);

        /* Finish construction of the iron smelter */
        Utils.constructMediumHouse(ironSmelter);
        
        /* Occupy the iron smelter */
        Worker sw = Utils.occupyBuilding(new IronFounder(map), ironSmelter, map);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), sw);        

        /* Deliver iron and coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(COAL, map));
        ironSmelter.putCargo(new Cargo(IRON, map));
        
        /* Verify that the iron smelter produces iron bars */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
            assertNull(sw.getCargo());
        }

        map.stepTime();

        assertNotNull(sw.getCargo());
        assertEquals(sw.getCargo().getMaterial(), IRON_BAR);
        assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testIronFounderLeavesIronBarAtTheFlag() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(), point3);

        /* Place a road between the headquarter and the iron smelter */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the iron smelter */
        Utils.constructMediumHouse(ironSmelter);

        /* Occupy the iron smelter */
        Worker sw = Utils.occupyBuilding(new IronFounder(map), ironSmelter, map);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), sw);        

        /* Deliver iron and coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(IRON, map));
        ironSmelter.putCargo(new Cargo(COAL, map));
        
        /* Verify that the iron smelter produces iron bars */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
            assertNull(sw.getCargo());
        }

        map.stepTime();
        
        assertNotNull(sw.getCargo());
        assertEquals(sw.getCargo().getMaterial(), IRON_BAR);
        assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
        
        /* Verify that the iron smelter worker leaves the cargo at the flag */
        assertEquals(sw.getTarget(), ironSmelter.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, sw, ironSmelter.getFlag().getPosition());
        
        assertFalse(ironSmelter.getFlag().getStackedCargo().isEmpty());
        assertNull(sw.getCargo());
        assertEquals(sw.getTarget(), ironSmelter.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, sw);
        
        assertTrue(sw.isInsideBuilding());
    }

    @Test
    public void testProductionOfOneIronBarConsumesOneIronAndOneCoal() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(), point3);

        /* Finish construction of the iron smelter */
        Utils.constructMediumHouse(ironSmelter);
        
        /* Occupy the iron smelter */
        Worker sw = Utils.occupyBuilding(new IronFounder(map), ironSmelter, map);
        
        /* Deliver iron and coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(IRON, map));
        ironSmelter.putCargo(new Cargo(COAL, map));
        
        /* Wait until the iron smelter worker produces an iron bar */
        assertTrue(ironSmelter.getAmount(IRON) == 1);
        assertTrue(ironSmelter.getAmount(COAL) == 1);
        
        Utils.fastForward(150, map);
        
        assertTrue(ironSmelter.getAmount(IRON) == 0);
        assertTrue(ironSmelter.getAmount(COAL) == 0);
        assertTrue(ironSmelter.needsMaterial(IRON));
        assertTrue(ironSmelter.needsMaterial(COAL));
    }

    @Test
    public void testProductionCountdownStartsWhenIronAndCoalAreAvailable() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(), point3);

        /* Finish construction of the iron smelter */
        Utils.constructMediumHouse(ironSmelter);
        
        /* Occupy the iron smelter */
        Worker sw = Utils.occupyBuilding(new IronFounder(map), ironSmelter, map);
        
        /* Fast forward so that the iron smelter worker would produced iron bars
           if it had had iron and coal
        */
        Utils.fastForward(150, map);
        
        assertNull(sw.getCargo());
        
        /* Deliver iron and coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(IRON, map));
        ironSmelter.putCargo(new Cargo(COAL, map));
        
        /* Verify that it takes 50 steps for the iron smelter worker to produce the iron bar */
        int i;
        for (i = 0; i < 50; i++) {
            assertNull(sw.getCargo());
            map.stepTime();
        }
        
        assertNotNull(sw.getCargo());
    }

    @Test
    public void testIronSmelterCannotProduceWithOnlyIron() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(), point3);

        /* Finish construction of the iron smelter */
        Utils.constructMediumHouse(ironSmelter);
        
        /* Occupy the iron smelter */
        Worker sw = Utils.occupyBuilding(new IronFounder(map), ironSmelter, map);
        
        /* Deliver iron but not coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(IRON, map));
        
        /* Verify that the iron founder doesn't produce iron bars since it doesn't have any coal */
        int i;
        for (i = 0; i < 200; i++) {
            assertNull(sw.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testIronSmelterCannotProduceWithOnlyCoal() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place iron smelter */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(), point3);

        /* Finish construction of the iron smelter */
        Utils.constructMediumHouse(ironSmelter);
        
        /* Occupy the iron smelter */
        Worker sw = Utils.occupyBuilding(new IronFounder(map), ironSmelter, map);
                
        /* Deliver iron but not coal to the iron smelter */
        ironSmelter.putCargo(new Cargo(COAL, map));
        
        /* Verify that the iron founder doesn't produce iron bars since it doesn't have any coal */
        int i;
        for (i = 0; i < 200; i++) {
            assertNull(sw.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testIronSmelterWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(), point26);

        /* Finish construction of the iron smelter */
        Utils.constructMediumHouse(ironSmelter0);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(map), ironSmelter0, map);

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

        Worker ww = ironSmelter0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the iron founder puts the iron bar cargo at the flag */
        assertEquals(ww.getTarget(), ironSmelter0.getFlag().getPosition());
        assertTrue(ironSmelter0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, ironSmelter0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(ironSmelter0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait for the worker to go back to the iron smelter */
        assertEquals(ww.getTarget(), ironSmelter0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, ironSmelter0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(ww.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(ww.getTarget(), ironSmelter0.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, ironSmelter0.getFlag().getPosition());
        
        assertNull(ww.getCargo());
        assertEquals(ironSmelter0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(), point26);

        /* Finish construction of the iron smelter */
        Utils.constructMediumHouse(ironSmelter0);

        /* Deliver material to the iron smelter */
        Cargo ironCargo = new Cargo(COAL, map);
        Cargo coalCargo = new Cargo(IRON, map);
        
        ironSmelter0.putCargo(ironCargo);
        ironSmelter0.putCargo(ironCargo);

        ironSmelter0.putCargo(coalCargo);
        ironSmelter0.putCargo(coalCargo);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(map), ironSmelter0, map);

        /* Let the iron founder rest */
        Utils.fastForward(100, map);

        /* Wait for the iron founder to produce a new iron bar cargo */
        Utils.fastForward(50, map);

        Worker ww = ironSmelter0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the iron founder puts the iron bar cargo at the flag */
        assertEquals(ww.getTarget(), ironSmelter0.getFlag().getPosition());
        assertTrue(ironSmelter0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, ironSmelter0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(ironSmelter0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = ironSmelter0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), ironSmelter0.getFlag().getPosition());
    
        /* Connect the iron smelter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(headquarter0.getFlag(), ironSmelter0.getFlag());
    
        /* Assign a courier to the road */
        Courier courier = new Courier(map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);
    
        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(ironSmelter0.getFlag().getPosition()));
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
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter = map.placeBuilding(new IronSmelter(), point26);

        /* Finish construction of the iron smelter */
        Utils.constructMediumHouse(ironSmelter);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(map), ironSmelter, map);
        
        /* Destroy the iron smelter */
        Worker ww = ironSmelter.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), ironSmelter.getPosition());

        ironSmelter.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(IRON_FOUNDER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());

        /* Verify that the iron founder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(IRON_FOUNDER), amount + 1);
    }

    @Test
    public void testIronFounderGoesBackOnToStorageOnRoadsIfPossibleWhenIronSmelterIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing iron smelter */
        Point point26 = new Point(8, 8);
        Building ironSmelter = map.placeBuilding(new IronSmelter(), point26);

        /* Connect the iron smelter with the headquarter */
        map.placeAutoSelectedRoad(ironSmelter.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the iron smelter */
        Utils.constructMediumHouse(ironSmelter);

        /* Occupy the iron smelter */
        Utils.occupyBuilding(new IronFounder(map), ironSmelter, map);
        
        /* Destroy the iron smelter */
        Worker ww = ironSmelter.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), ironSmelter.getPosition());

        ironSmelter.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        /* Verify that the worker plans to use the roads */
        for (Point p : ww.getPlannedPath()) {
            assertTrue(map.isRoadAtPoint(p));
        }
    }
}
