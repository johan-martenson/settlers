/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
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

        /* Unfinished samwill doesn't need worker */
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

        map.stepTime();
        
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
    public void testOccupiedIronSmelterWithoutWoodProducesNothing() throws Exception {
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
    public void testOccupiedIronSmelterWithWoodProducesIronBars() throws Exception {
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

}
