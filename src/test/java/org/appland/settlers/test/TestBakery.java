/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameLogic;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.BAKER;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Bakery;
import org.appland.settlers.model.Baker;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.WATER;
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
        assertFalse(bakery.needsWorker(BAKER));
        
        /* Finish construction of the bakery */
        Utils.constructMediumHouse(bakery);
        
        assertTrue(bakery.needsWorker());
        assertTrue(bakery.needsWorker(BAKER));
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
        Utils.constructMediumHouse(bakery);
        
        assertTrue(bakery.needsWorker());
        assertTrue(bakery.needsWorker(BAKER));

        /* Verify that a bakery worker leaves the hq */
        GameLogic gameLogic = new GameLogic();
        
        assertTrue(map.getAllWorkers().size() == 1);
        
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        
        assertTrue(map.getAllWorkers().size() == 3);

        /* Let the bakery worker reach the bakery */
        Baker baker = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Baker) {
                baker = (Baker)w;
            }
        }

        assertEquals(baker.getTarget(), bakery.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, baker);
        
        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker);
    }
    
    @Test
    public void testOccupiedBakeryWithoutWoodProducesNothing() throws Exception {
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
        Utils.constructMediumHouse(bakery);
        
        assertTrue(bakery.needsWorker());
        assertTrue(bakery.needsWorker(BAKER));

        /* Verify that a bakery worker leaves the hq */
        GameLogic gameLogic = new GameLogic();
        
        assertTrue(map.getAllWorkers().size() == 1);
        
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        
        assertTrue(map.getAllWorkers().size() == 3);

        /* Let the bakery worker reach the bakery */
        Baker baker = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Baker) {
                baker = (Baker)w;
            }
        }

        assertEquals(baker.getTarget(), bakery.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, baker);
        
        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker);        

        /* Verify that the bakery doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
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
        Utils.constructMediumHouse(bakery);

        /* Verify that the bakery doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedBakeryWithWoodProducesPlancks() throws Exception {
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
        Utils.constructMediumHouse(bakery);
        
        assertTrue(bakery.needsWorker());
        assertTrue(bakery.needsWorker(BAKER));

        /* Verify that a bakery worker leaves the hq */
        GameLogic gameLogic = new GameLogic();
        
        assertTrue(map.getAllWorkers().size() == 1);
        
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        
        assertTrue(map.getAllWorkers().size() == 3);

        /* Let the bakery worker reach the bakery */
        Baker baker = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Baker) {
                baker = (Baker)w;
            }
        }

        assertEquals(baker.getTarget(), bakery.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, baker);
        
        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker);        

        /* Deliver wood to the bakery */
        bakery.putCargo(new Cargo(WATER, map));
        bakery.putCargo(new Cargo(FLOUR, map));
        
        /* Verify that the bakery produces plancks */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            assertFalse(bakery.isCargoReady());
            assertNull(baker.getCargo());
        }

        map.stepTime();
        
        assertNotNull(baker.getCargo());
        assertEquals(baker.getCargo().getMaterial(), BREAD);
        assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
        assertFalse(bakery.isCargoReady());
    }

    @Test
    public void testBakerLeavesPlancksAtTheFlag() throws Exception {
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
        Utils.constructMediumHouse(bakery);
        
        assertTrue(bakery.needsWorker());
        assertTrue(bakery.needsWorker(BAKER));

        /* Verify that a bakery worker leaves the hq */
        GameLogic gameLogic = new GameLogic();
        
        assertTrue(map.getAllWorkers().size() == 1);
        
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        
        assertTrue(map.getAllWorkers().size() == 3);

        /* Let the bakery worker reach the bakery */
        Baker baker = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Baker) {
                baker = (Baker)w;
            }
        }

        assertEquals(baker.getTarget(), bakery.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, baker);
        
        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker);        

        /* Deliver wood to the bakery */
        bakery.putCargo(new Cargo(WATER, map));
        bakery.putCargo(new Cargo(FLOUR, map));
        
        /* Verify that the bakery produces plancks */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            assertFalse(bakery.isCargoReady());
            assertNull(baker.getCargo());
        }

        map.stepTime();
        
        assertNotNull(baker.getCargo());
        assertEquals(baker.getCargo().getMaterial(), BREAD);
        assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
        assertFalse(bakery.isCargoReady());
        
        /* Verify that the bakery worker leaves the cargo at the flag */
        assertEquals(baker.getTarget(), bakery.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, baker, bakery.getFlag().getPosition());
        
        assertFalse(bakery.getFlag().getStackedCargo().isEmpty());
        assertNull(baker.getCargo());
        assertEquals(baker.getTarget(), bakery.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, baker);
        
        assertTrue(baker.isInsideBuilding());
    }

    @Test
    public void testProductionOfOnePlanckConsumesOneWood() throws Exception {
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
        Utils.constructMediumHouse(bakery);
        
        assertTrue(bakery.needsWorker());
        assertTrue(bakery.needsWorker(BAKER));

        /* Verify that a bakery worker leaves the hq */
        GameLogic gameLogic = new GameLogic();
        
        assertTrue(map.getAllWorkers().size() == 1);
        
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);

        /* Let the bakery worker reach the bakery */
        Baker baker = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Baker) {
                baker = (Baker)w;
            }
        }

        assertEquals(baker.getTarget(), bakery.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, baker);
        
        /* Deliver wood to the bakery */
        bakery.putCargo(new Cargo(WATER, map));
        bakery.putCargo(new Cargo(FLOUR, map));
        
        /* Wait until the bakery worker produces a planck */
        assertTrue(bakery.getAmount(WATER) == 1);
        assertTrue(bakery.getAmount(FLOUR) == 1);
        
        Utils.fastForward(150, map);
        
        assertTrue(bakery.getAmount(FLOUR) == 0);
        assertTrue(bakery.getAmount(WATER) == 0);
    }

    @Test
    public void testProductionCountdownStartsWhenWoodIsAvailable() throws Exception {
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
        Utils.constructMediumHouse(bakery);
        
        assertTrue(bakery.needsWorker());
        assertTrue(bakery.needsWorker(BAKER));

        /* Verify that a bakery worker leaves the hq */
        GameLogic gameLogic = new GameLogic();
        
        assertTrue(map.getAllWorkers().size() == 1);
        
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);

        /* Let the bakery worker reach the bakery */
        Baker baker = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Baker) {
                baker = (Baker)w;
            }
        }

        assertEquals(baker.getTarget(), bakery.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, baker);
        
        /* Fast forward so that the bakery worker would produced plancks
           if it had had any wood
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

}
