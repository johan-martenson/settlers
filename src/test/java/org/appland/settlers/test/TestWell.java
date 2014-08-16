/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.List;
import org.appland.settlers.model.Building;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.GameLogic;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WELL_WORKER;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Well;
import org.appland.settlers.model.WellWorker;
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
public class TestWell {

    @Test
    public void testFinishedWellNeedsWorker() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Point point1 = new Point(8, 6);
        Building well = map.placeBuilding(new Well(), point1);

        Utils.constructSmallHouse(well);

        assertTrue(well.getConstructionState() == DONE);
        assertTrue(well.needsWorker());
        assertTrue(well.needsWorker(WELL_WORKER));
    }
    
    @Test
    public void testWellWorkerIsAssignedToFinishedHouse() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(8, 6);
        Building well = map.placeBuilding(new Well(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);

        GameLogic gameLogic = new GameLogic();
        
        /* Finish the well */
        Utils.constructSmallHouse(well);
        
        /* Run game logic twice, once to place courier and once to place woodcutter worker */
        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);
        
        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);
        
        List<Worker> workers = map.getAllWorkers();
        assertTrue(map.getAllWorkers().size() == 2);
        assertTrue(workers.get(0) instanceof WellWorker || workers.get(1) instanceof WellWorker);
        assertTrue(workers.get(0) instanceof Courier || workers.get(1) instanceof Courier);
    }

    @Test
    public void testUnoccupiedWellProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(8, 6);
        Building well = map.placeBuilding(new Well(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);

        GameLogic gameLogic = new GameLogic();
        
        /* Finish the well */
        Utils.constructSmallHouse(well);
        
        int i;
        for (i = 0; i < 200; i++) {
            assertTrue(well.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testWellWorkerEntersTheWell() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(8, 6);
        Building well = map.placeBuilding(new Well(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);

        GameLogic gameLogic = new GameLogic();
        
        Courier courier = new Courier(map);
        map.placeWorker(courier, hq.getFlag());
        courier.assignToRoad(road0);
        
        /* Finish the well */
        Utils.constructSmallHouse(well);
        
        /* Run game logic twice, once to place courier and once to place woodcutter worker */
        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);
        
        /* Get the well worker */
        List<Worker> workers = map.getAllWorkers();
        Worker ww = workers.get(0);
        
        if (! (ww instanceof WellWorker)) {
            ww = workers.get(1);
        }

        /* Let the well worker reach the well */
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, well.getPosition());
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(well.getWorker(), ww);
    }
    
    @Test
    public void testWellWorkerRests() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(8, 6);
        Building well = map.placeBuilding(new Well(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);

        GameLogic gameLogic = new GameLogic();
        
        Courier courier = new Courier(map);
        map.placeWorker(courier, hq.getFlag());
        courier.assignToRoad(road0);
        
        /* Finish the well */
        Utils.constructSmallHouse(well);
        
        /* Put the worker in the well */
        WellWorker ww = new WellWorker(map);
        
        map.placeWorker(ww, well.getFlag());
        ww.setTargetBuilding(well);
        
        Utils.fastForwardUntilWorkersReachTarget(map, ww);
        
        assertTrue(ww.isInsideBuilding());

        /* Verify that the worker rests first without producing anything */
        int i;
        for (i = 0; i < 100; i++) {
            assertNull(ww.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testWellWorkerProducesWater() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(8, 6);
        Building well = map.placeBuilding(new Well(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);

        GameLogic gameLogic = new GameLogic();
        
        Courier courier = new Courier(map);
        map.placeWorker(courier, hq.getFlag());
        courier.assignToRoad(road0);
        
        /* Finish the well */
        Utils.constructSmallHouse(well);
        
        /* Put the worker in the well */
        WellWorker ww = new WellWorker(map);
        
        map.placeWorker(ww, well.getFlag());
        ww.setTargetBuilding(well);
        
        Utils.fastForwardUntilWorkersReachTarget(map, ww);
        
        assertTrue(ww.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Verify that it the worker produces water at the right time */
        int i;
        for (i = 0; i < 50; i++) {
            assertNull(ww.getCargo());
            map.stepTime();
        }
    
        assertNotNull(ww.getCargo());
        assertEquals(ww.getCargo().getMaterial(), WATER);
    }

    @Test
    public void testWellWorkerPlacesWaterCargoAtTheFlag() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(8, 6);
        Building well = map.placeBuilding(new Well(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);

        GameLogic gameLogic = new GameLogic();
        
        Courier courier = new Courier(map);
        map.placeWorker(courier, hq.getFlag());
        courier.assignToRoad(road0);
        
        /* Finish the well */
        Utils.constructSmallHouse(well);
        
        /* Put the worker in the well */
        WellWorker ww = new WellWorker(map);
        
        map.placeWorker(ww, well.getFlag());
        ww.setTargetBuilding(well);
        
        Utils.fastForwardUntilWorkersReachTarget(map, ww);
        
        assertTrue(ww.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Verify that it the worker produces water at the right time */
        Utils.fastForward(50, map);
        
        assertNotNull(ww.getCargo());
        assertEquals(ww.getTarget(), well.getFlag().getPosition());
        
        /* Let the worker reach the flag and place the cargo*/
        assertTrue(well.getFlag().getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, well.getFlag().getPosition());

        assertFalse(well.getFlag().getStackedCargo().isEmpty());

        /* Verify that the water cargo has the right target */
        assertEquals(well.getFlag().getStackedCargo().get(0).getTarget(), hq);
        
        /* Let the worker walk back to the well */
        assertEquals(ww.getTarget(), well.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, ww);
        
        assertTrue(ww.isInsideBuilding());
    }
}
