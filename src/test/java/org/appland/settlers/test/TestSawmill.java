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
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.SAWMILL_WORKER;
import static org.appland.settlers.model.Material.WOOD;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.SawmillWorker;
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
public class TestSawmill {
    
    @Test
    public void testSawmillNeedsWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(), point3);

        /* Unfinished samwill doesn't need worker */
        assertFalse(sawmill.needsWorker());
        
        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill);
        
        assertTrue(sawmill.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneSawmillWorkerAtStart() {
        Headquarter hq = new Headquarter();
        
        assertTrue(hq.getAmount(SAWMILL_WORKER) == 1);
    }
    
    @Test
    public void testSawmillGetsAssignedWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(), point3);

        /* Place a road between the headquarter and the sawmill */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill);
        
        assertTrue(sawmill.needsWorker());

        /* Verify that a sawmill worker leaves the hq */        
        assertTrue(map.getAllWorkers().size() == 1);

        Utils.fastForward(3, map);
        
        assertTrue(map.getAllWorkers().size() == 3);

        Utils.verifyListContainsWorkerOfType(map.getAllWorkers(), SawmillWorker.class);
        
        /* Let the sawmill worker reach the sawmill */
        SawmillWorker sw = null;
        
        for (Worker w : map.getAllWorkers()) {
            if (w instanceof SawmillWorker) {
                sw = (SawmillWorker)w;
            }
        }
        
        assertNotNull(sw);
        assertEquals(sw.getTarget(), sawmill.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, sw);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sw);
    }
    
    @Test
    public void testOccupiedSawmillWithoutWoodProducesNothing() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(), point3);

        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill);

        /* Occupy the sawmill */
        Worker sw = Utils.occupyBuilding(new SawmillWorker(map), sawmill, map);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sw);        

        /* Verify that the sawmill doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertNull(sw.getCargo());
            map.stepTime();
        }
    }
    
    @Test
    public void testUnoccupiedSawmillProducesNothing() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place sawmill */
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

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(), point3);

        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill);
        
        /* Occupy the sawmill */
        Worker sw = Utils.occupyBuilding(new SawmillWorker(map), sawmill, map);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sw);        

        /* Deliver wood to the sawmill */
        sawmill.putCargo(new Cargo(WOOD, map));
        sawmill.putCargo(new Cargo(WOOD, map));
        
        /* Verify that the sawmill produces plancks */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertNull(sw.getCargo());
        }

        map.stepTime();

        assertNotNull(sw.getCargo());
        assertEquals(sw.getCargo().getMaterial(), PLANCK);
        assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testSawmillWorkerLeavesPlancksAtTheFlag() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(), point3);

        /* Place a road between the headquarter and the sawmill */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill);

        /* Occupy the sawmill */
        Worker sw = Utils.occupyBuilding(new SawmillWorker(map), sawmill, map);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sw);        

        /* Deliver wood to the sawmill */
        sawmill.putCargo(new Cargo(WOOD, map));
        sawmill.putCargo(new Cargo(WOOD, map));
        
        /* Verify that the sawmill produces plancks */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertNull(sw.getCargo());
        }

        map.stepTime();
        
        assertNotNull(sw.getCargo());
        assertEquals(sw.getCargo().getMaterial(), PLANCK);
        assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
        
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

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(), point3);

        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill);
        
        /* Occupy the sawmill */
        Worker sw = Utils.occupyBuilding(new SawmillWorker(map), sawmill, map);
        
        /* Deliver wood to the sawmill */
        sawmill.putCargo(new Cargo(WOOD, map));
        
        /* Wait until the sawmill worker produces a planck */
        assertTrue(sawmill.getAmount(WOOD) == 1);
        
        Utils.fastForward(150, map);
        
        assertTrue(sawmill.getAmount(WOOD) == 0);
        assertTrue(sawmill.needsMaterial(WOOD));
    }

    @Test
    public void testProductionCountdownStartsWhenWoodIsAvailable() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(), point3);

        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill);
        
        /* Occupy the sawmill */
        Worker sw = Utils.occupyBuilding(new SawmillWorker(map), sawmill, map);
        
        /* Fast forward so that the sawmill worker would produced plancks
           if it had had any wood
        */
        Utils.fastForward(150, map);
        
        assertNull(sw.getCargo());
        
        /* Deliver wood to the sawmill */
        sawmill.putCargo(new Cargo(WOOD, map));
        
        /* Verify that it takes 50 steps for the sawmill worker to produce the planck */
        int i;
        for (i = 0; i < 50; i++) {
            assertNull(sw.getCargo());
            map.stepTime();
        }
        
        assertNotNull(sw.getCargo());
    }

    @Test
    public void testSawmillWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing sawmill */
        Point point26 = new Point(8, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(), point26);

        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill0);

        /* Occupy the sawmill */
        Utils.occupyBuilding(new SawmillWorker(map), sawmill0, map);

        /* Deliver material to the sawmill */
        Cargo woodCargo = new Cargo(WOOD, map);
        
        sawmill0.putCargo(woodCargo);
        sawmill0.putCargo(woodCargo);
        
        /* Let the sawmill worker rest */
        Utils.fastForward(100, map);

        /* Wait for the sawmill worker to produce a new planck cargo */
        Utils.fastForward(50, map);

        Worker ww = sawmill0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the sawmill worker puts the planck cargo at the flag */
        assertEquals(ww.getTarget(), sawmill0.getFlag().getPosition());
        assertTrue(sawmill0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, sawmill0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(sawmill0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait for the worker to go back to the sawmill */
        assertEquals(ww.getTarget(), sawmill0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, sawmill0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(ww.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(ww.getTarget(), sawmill0.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, sawmill0.getFlag().getPosition());
        
        assertNull(ww.getCargo());
        assertEquals(sawmill0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing sawmill */
        Point point26 = new Point(8, 8);
        Building sawmill0 = map.placeBuilding(new Sawmill(), point26);

        /* Finish construction of the sawmill */
        Utils.constructMediumHouse(sawmill0);

        /* Deliver material to the sawmill */
        Cargo woodCargo = new Cargo(WOOD, map);
        
        sawmill0.putCargo(woodCargo);
        sawmill0.putCargo(woodCargo);

        /* Occupy the sawmill */
        Utils.occupyBuilding(new SawmillWorker(map), sawmill0, map);

        /* Let the sawmill worker rest */
        Utils.fastForward(100, map);

        /* Wait for the sawmill worker to produce a new planck cargo */
        Utils.fastForward(50, map);

        Worker ww = sawmill0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the sawmill worker puts the planck cargo at the flag */
        assertEquals(ww.getTarget(), sawmill0.getFlag().getPosition());
        assertTrue(sawmill0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, sawmill0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(sawmill0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = sawmill0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), sawmill0.getFlag().getPosition());
    
        /* Connect the sawmill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(headquarter0.getFlag(), sawmill0.getFlag());
    
        /* Assign a courier to the road */
        Courier courier = new Courier(map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);
    
        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(sawmill0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));
    
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
    
        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), sawmill0.getFlag().getPosition());
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);
        
        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());
        
        int amount = headquarter0.getAmount(PLANCK);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());
        
        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(PLANCK), amount + 1);
    }
}

