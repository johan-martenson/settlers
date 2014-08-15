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
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.SAWMILL_WORKER;
import static org.appland.settlers.model.Material.WOOD;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.SawmillWorker;
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
public class TestSawmill {
    
    @Test
    public void testSawmillNeedsWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Unfinished samwill doesn't need worker */
        assertFalse(sawmill.needsWorker());
        assertFalse(sawmill.needsWorker(SAWMILL_WORKER));
        
        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill);
        
        assertTrue(sawmill.needsWorker());
        assertTrue(sawmill.needsWorker(SAWMILL_WORKER));
    }

    @Test
    public void testHeadquarterHasOneSawmillWorkerAtStart() {
        Headquarter hq = new Headquarter();
        
        assertTrue(hq.getAmount(SAWMILL_WORKER) == 1);
    }
    
    @Test
    public void testSawmillGetsAssignedWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill);
        
        assertTrue(sawmill.needsWorker());
        assertTrue(sawmill.needsWorker(SAWMILL_WORKER));

        /* Verify that a sawmill worker leaves the hq */
        GameLogic gameLogic = new GameLogic();
        
        assertTrue(map.getAllWorkers().isEmpty());
        
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        
        assertTrue(map.getAllWorkers().size() == 2);
        assertTrue(map.getAllWorkers().get(0) instanceof SawmillWorker ||
                   map.getAllWorkers().get(1) instanceof SawmillWorker);

        /* Let the sawmill worker reach the sawmill */
        SawmillWorker sw;
        if (map.getAllWorkers().get(0) instanceof SawmillWorker) {
            sw = (SawmillWorker)map.getAllWorkers().get(0);
        } else {
            sw = (SawmillWorker)map.getAllWorkers().get(1);
        }

        assertEquals(sw.getTarget(), sawmill.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, sw);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sw);
    }
    
    @Test
    public void testOccupiedSawmillWithoutWoodProducesNothing() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill);
        
        assertTrue(sawmill.needsWorker());
        assertTrue(sawmill.needsWorker(SAWMILL_WORKER));

        /* Verify that a sawmill worker leaves the hq */
        GameLogic gameLogic = new GameLogic();
        
        assertTrue(map.getAllWorkers().isEmpty());
        
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        
        assertTrue(map.getAllWorkers().size() == 2);
        assertTrue(map.getAllWorkers().get(0) instanceof SawmillWorker ||
                   map.getAllWorkers().get(1) instanceof SawmillWorker);

        /* Let the sawmill worker reach the sawmill */
        SawmillWorker sw;
        if (map.getAllWorkers().get(0) instanceof SawmillWorker) {
            sw = (SawmillWorker)map.getAllWorkers().get(0);
        } else {
            sw = (SawmillWorker)map.getAllWorkers().get(1);
        }

        assertEquals(sw.getTarget(), sawmill.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, sw);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sw);        

        /* Verify that the sawmill doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }
    
    @Test
    public void testUnoccupiedSawmillProducesNothing() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(), point3);

        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill);

        /* Verify that the sawmill doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedSawmillWithWoodProducesPlancks() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill);
        
        assertTrue(sawmill.needsWorker());
        assertTrue(sawmill.needsWorker(SAWMILL_WORKER));

        /* Verify that a sawmill worker leaves the hq */
        GameLogic gameLogic = new GameLogic();
        
        assertTrue(map.getAllWorkers().isEmpty());
        
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        
        assertTrue(map.getAllWorkers().size() == 2);
        assertTrue(map.getAllWorkers().get(0) instanceof SawmillWorker ||
                   map.getAllWorkers().get(1) instanceof SawmillWorker);

        /* Let the sawmill worker reach the sawmill */
        SawmillWorker sw;
        if (map.getAllWorkers().get(0) instanceof SawmillWorker) {
            sw = (SawmillWorker)map.getAllWorkers().get(0);
        } else {
            sw = (SawmillWorker)map.getAllWorkers().get(1);
        }

        assertEquals(sw.getTarget(), sawmill.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, sw);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sw);        

        /* Deliver wood to the sawmill */
        sawmill.deliver(new Cargo(WOOD, map));
        sawmill.deliver(new Cargo(WOOD, map));
        
        /* Verify that the sawmill produces plancks */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertFalse(sawmill.isCargoReady());
            assertNull(sw.getCargo());
        }

        map.stepTime();
        
        assertNotNull(sw.getCargo());
        assertEquals(sw.getCargo().getMaterial(), PLANCK);
        assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
        assertFalse(sawmill.isCargoReady());
    }

    @Test
    public void testSawmillWorkerLeavesPlancksAtTheFlag() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill);
        
        assertTrue(sawmill.needsWorker());
        assertTrue(sawmill.needsWorker(SAWMILL_WORKER));

        /* Verify that a sawmill worker leaves the hq */
        GameLogic gameLogic = new GameLogic();
        
        assertTrue(map.getAllWorkers().isEmpty());
        
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        
        assertTrue(map.getAllWorkers().size() == 2);
        assertTrue(map.getAllWorkers().get(0) instanceof SawmillWorker ||
                   map.getAllWorkers().get(1) instanceof SawmillWorker);

        /* Let the sawmill worker reach the sawmill */
        SawmillWorker sw;
        if (map.getAllWorkers().get(0) instanceof SawmillWorker) {
            sw = (SawmillWorker)map.getAllWorkers().get(0);
        } else {
            sw = (SawmillWorker)map.getAllWorkers().get(1);
        }

        assertEquals(sw.getTarget(), sawmill.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, sw);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sw);        

        /* Deliver wood to the sawmill */
        sawmill.deliver(new Cargo(WOOD, map));
        sawmill.deliver(new Cargo(WOOD, map));
        
        /* Verify that the sawmill produces plancks */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertFalse(sawmill.isCargoReady());
            assertNull(sw.getCargo());
        }

        map.stepTime();
        
        assertNotNull(sw.getCargo());
        assertEquals(sw.getCargo().getMaterial(), PLANCK);
        assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
        assertFalse(sawmill.isCargoReady());
        
        /* Verify that the sawmill worker leaves the cargo at the flag */
        assertEquals(sw.getTarget(), sawmill.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, sw, sawmill.getFlag().getPosition());
        
        assertFalse(sawmill.getFlag().getStackedCargo().isEmpty());
        assertNull(sw.getCargo());
        assertEquals(sw.getTarget(), sawmill.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, sw);
        
        assertTrue(sw.isInsideBuilding());
    }

    @Test
    public void testProductionOfOnePlanckConsumesOneWood() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill);
        
        assertTrue(sawmill.needsWorker());
        assertTrue(sawmill.needsWorker(SAWMILL_WORKER));

        /* Verify that a sawmill worker leaves the hq */
        GameLogic gameLogic = new GameLogic();
        
        assertTrue(map.getAllWorkers().isEmpty());
        
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);

        /* Let the sawmill worker reach the sawmill */
        SawmillWorker sw;
        if (map.getAllWorkers().get(0) instanceof SawmillWorker) {
            sw = (SawmillWorker)map.getAllWorkers().get(0);
        } else {
            sw = (SawmillWorker)map.getAllWorkers().get(1);
        }

        assertEquals(sw.getTarget(), sawmill.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, sw);
        
        /* Deliver wood to the sawmill */
        sawmill.deliver(new Cargo(WOOD, map));
        sawmill.deliver(new Cargo(WOOD, map));
        
        /* Wait until the sawmill worker produces a planck */
        assertTrue(sawmill.getAmount(WOOD) == 2);
        
        Utils.fastForward(150, map);
        
        assertTrue(sawmill.getAmount(WOOD) == 1);
    }

    @Test
    public void testProductionCountdownStartsWhenWoodIsAvailable() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill);
        
        assertTrue(sawmill.needsWorker());
        assertTrue(sawmill.needsWorker(SAWMILL_WORKER));

        /* Verify that a sawmill worker leaves the hq */
        GameLogic gameLogic = new GameLogic();
        
        assertTrue(map.getAllWorkers().isEmpty());
        
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);

        /* Let the sawmill worker reach the sawmill */
        SawmillWorker sw;
        if (map.getAllWorkers().get(0) instanceof SawmillWorker) {
            sw = (SawmillWorker)map.getAllWorkers().get(0);
        } else {
            sw = (SawmillWorker)map.getAllWorkers().get(1);
        }

        assertEquals(sw.getTarget(), sawmill.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, sw);
        
        /* Fast forward so that the sawmill worker would produced plancks
           if it had had any wood
        */
        
        Utils.fastForward(150, map);
        
        assertNull(sw.getCargo());
        
        /* Deliver wood to the sawmill */
        sawmill.deliver(new Cargo(WOOD, map));
        
        /* Verify that it takes 50 steps for the sawmill worker to produce the planck */
        int i;
        for (i = 0; i < 50; i++) {
            assertNull(sw.getCargo());
            map.stepTime();
        }
        
        assertNotNull(sw.getCargo());
    }
}

