/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.List;
import org.appland.settlers.model.Building;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.MILLER;
import static org.appland.settlers.model.Material.WHEAT;
import org.appland.settlers.model.Mill;
import org.appland.settlers.model.Miller;
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
public class TestMill {

    @Test
    public void testFinishedMillNeedsWorker() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Point point1 = new Point(8, 6);
        Building mill = map.placeBuilding(new Mill(), point1);

        Utils.constructMediumHouse(mill);

        assertTrue(mill.getConstructionState() == DONE);
        assertTrue(mill.needsWorker());
        assertTrue(mill.needsWorker(MILLER));
    }
    
    @Test
    public void testMillerIsAssignedToFinishedHouse() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(8, 6);
        Building mill = map.placeBuilding(new Mill(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);
        
        /* Finish the well */
        Utils.constructMediumHouse(mill);
        
        /* Run game logic twice, once to place courier and once to place miller */
        Utils.fastForward(2, map);
        
        List<Worker> workers = map.getAllWorkers();
        assertTrue(map.getAllWorkers().size() == 3);
        Miller miller = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Miller) {
                miller = (Miller)w;
            }
        }
    
        assertNotNull(miller);
    }

    @Test
    public void testUnoccupiedMillProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(8, 6);
        Building mill = map.placeBuilding(new Mill(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);
        
        /* Finish the well */
        Utils.constructMediumHouse(mill);
        
        int i;
        for (i = 0; i < 200; i++) {
            assertTrue(mill.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testWellWorkerEntersTheWell() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(8, 6);
        Building mill = map.placeBuilding(new Mill(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);
        
        Courier courier = new Courier(map);
        map.placeWorker(courier, hq.getFlag());
        courier.assignToRoad(road0);
        
        /* Finish the well */
        Utils.constructMediumHouse(mill);
        
        /* Run game logic twice, once to place courier and once to place woodcutter worker */
        Utils.fastForward(2, map);
        
        /* Get the well worker */
        Miller miller = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Miller) {
                miller = (Miller)w;
            }
        }

        /* Let the well worker reach the well */
        Utils.fastForwardUntilWorkerReachesPoint(map, miller, mill.getPosition());
        
        assertNotNull(miller);
        assertTrue(miller.isInsideBuilding());
        assertEquals(mill.getWorker(), miller);
    }
    
    @Test
    public void testWellWorkerRests() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(8, 6);
        Building mill = map.placeBuilding(new Mill(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);
        
        Courier courier = new Courier(map);
        map.placeWorker(courier, hq.getFlag());
        courier.assignToRoad(road0);
        
        /* Finish the well */
        Utils.constructMediumHouse(mill);
        
        /* Put the worker in the well */
        Miller miller = new Miller(map);
        
        map.placeWorker(miller, mill.getFlag());
        miller.setTargetBuilding(mill);
        
        Utils.fastForwardUntilWorkersReachTarget(map, miller);
        
        assertTrue(miller.isInsideBuilding());

        /* Verify that the worker rests first without producing anything */
        int i;
        for (i = 0; i < 100; i++) {
            assertNull(miller.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testMillWithoutWheatProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(8, 6);
        Building mill = map.placeBuilding(new Mill(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);

        Courier courier = new Courier(map);
        map.placeWorker(courier, hq.getFlag());
        courier.assignToRoad(road0);
        
        /* Finish the well */
        Utils.constructMediumHouse(mill);
        
        /* Put the worker in the well */
        Miller miller = new Miller(map);
        
        map.placeWorker(miller, mill.getFlag());
        miller.setTargetBuilding(mill);
        
        Utils.fastForwardUntilWorkersReachTarget(map, miller);
        
        assertTrue(miller.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Verify that it the worker doesn't produce any wheat */
        int i;
        for (i = 0; i < 200; i++) {
            assertNull(miller.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testMillProducesFlour() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(8, 6);
        Building mill = map.placeBuilding(new Mill(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);
        
        Courier courier = new Courier(map);
        map.placeWorker(courier, hq.getFlag());
        courier.assignToRoad(road0);
        
        /* Finish the well */
        Utils.constructMediumHouse(mill);
        
        /* Deliver wheat to the mill */
        Cargo cargo = new Cargo(WHEAT, map);
        
        mill.putCargo(cargo);
        
        /* Put the worker in the well */
        Miller miller = new Miller(map);
        
        map.placeWorker(miller, mill.getFlag());
        miller.setTargetBuilding(mill);
        
        Utils.fastForwardUntilWorkersReachTarget(map, miller);
        
        assertTrue(miller.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Verify that it the worker produces flour at the right time */
        int i;
        for (i = 0; i < 50; i++) {
            assertNull(miller.getCargo());
            map.stepTime();
        }
    
        assertNotNull(miller.getCargo());
        assertEquals(miller.getCargo().getMaterial(), FLOUR);
    }

    @Test
    public void testMillWorkerPlacesFlourCargoAtTheFlag() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(8, 6);
        Building mill = map.placeBuilding(new Mill(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);
        
        Courier courier = new Courier(map);
        map.placeWorker(courier, hq.getFlag());
        courier.assignToRoad(road0);
        
        /* Finish the well */
        Utils.constructMediumHouse(mill);

        /* Deliver wheat to the mill */
        Cargo cargo = new Cargo(WHEAT, map);
        
        mill.putCargo(cargo);
        
        /* Put the worker in the well */
        Miller miller = new Miller(map);
        
        map.placeWorker(miller, mill.getFlag());
        miller.setTargetBuilding(mill);
        
        Utils.fastForwardUntilWorkersReachTarget(map, miller);
        
        assertTrue(miller.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Verify that it the worker produces flour at the right time */
        Utils.fastForward(50, map);
        
        assertNotNull(miller.getCargo());
        assertEquals(miller.getTarget(), mill.getFlag().getPosition());

        /* Let the worker reach the flag and place the cargo*/
        assertTrue(mill.getFlag().getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, miller, mill.getFlag().getPosition());

        assertFalse(mill.getFlag().getStackedCargo().isEmpty());
        
        /* Let the worker walk back to the well */
        assertEquals(miller.getTarget(), mill.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, miller);
        
        assertTrue(miller.isInsideBuilding());
    }
}
