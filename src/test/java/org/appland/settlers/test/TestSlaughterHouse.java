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
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.SlaughterHouse;
import org.appland.settlers.model.Butcher;
import static org.appland.settlers.model.Material.BUTCHER;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.PIG;
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
public class TestSlaughterHouse {
    
    @Test
    public void testSlaughterHouseNeedsWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Unfinished slaughter house doesn't need worker */
        assertFalse(slaughterHouse.needsWorker());
        
        /* Finish construction of the slaughterHouse */
        Utils.constructMediumHouse(slaughterHouse);
        
        assertTrue(slaughterHouse.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneButcherAtStart() {
        Headquarter hq = new Headquarter();
        
        assertTrue(hq.getAmount(BUTCHER) == 1);
    }
    
    @Test
    public void testSlaughterHouseGetsAssignedWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the slaughterHouse */
        Utils.constructMediumHouse(slaughterHouse);
        
        assertTrue(slaughterHouse.needsWorker());

        /* Verify that a slaughterHouse worker leaves the hq */
        Utils.fastForward(3, map);
        
        assertTrue(map.getAllWorkers().size() == 3);

        /* Let the slaughterHouse worker reach the slaughterHouse */
        Butcher butcher = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Butcher) {
                butcher = (Butcher)w;
            }
        }

        assertNotNull(butcher);
        assertEquals(butcher.getTarget(), slaughterHouse.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, butcher);
        
        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);
    }
    
    @Test
    public void testOccupiedSlaughterHouseWithoutPigsProducesNothing() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(), point3);

        /* Finish construction of the slaughterHouse */
        Utils.constructMediumHouse(slaughterHouse);
        
        /* Populate the slaughterHouse */
        Worker butcher = Utils.occupyBuilding(new Butcher(map), slaughterHouse, map);
        
        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);        

        /* Verify that the slaughterHouse doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
            assertNull(butcher.getCargo());
            map.stepTime();
        }
    }
    
    @Test
    public void testUnoccupiedSlaughterHouseProducesNothing() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(), point3);

        /* Finish construction of the slaughterHouse */
        Utils.constructMediumHouse(slaughterHouse);

        /* Verify that the slaughterHouse doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedSlaughterHouseWithPigsProducesMeat() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the slaughterHouse */
        Utils.constructMediumHouse(slaughterHouse);
        
        /* Populate the slaughterHouse */        
        Worker butcher = Utils.occupyBuilding(new Butcher(map), slaughterHouse, map);
        
        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);        

        /* Deliver wood to the slaughterHouse */
        slaughterHouse.putCargo(new Cargo(PIG, map));
        
        /* Verify that the slaughterHouse produces meat */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
            assertNull(butcher.getCargo());
        }

        map.stepTime();
        
        assertNotNull(butcher.getCargo());
        assertEquals(butcher.getCargo().getMaterial(), MEAT);
        assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testButcherLeavesMeatAtTheFlag() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the slaughterHouse */
        Utils.constructMediumHouse(slaughterHouse);
        
        /* Populate the slaughterHouse */        
        Worker butcher = Utils.occupyBuilding(new Butcher(map), slaughterHouse, map);
        
        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);        

        /* Deliver ingredients to the slaughterHouse */
        slaughterHouse.putCargo(new Cargo(PIG, map));
        
        /* Verify that the slaughterHouse produces meat */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
            assertNull(butcher.getCargo());
        }

        map.stepTime();
        
        assertNotNull(butcher.getCargo());
        assertEquals(butcher.getCargo().getMaterial(), MEAT);
        assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
        
        /* Verify that the slaughterHouse worker leaves the cargo at the flag */
        assertEquals(butcher.getTarget(), slaughterHouse.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, slaughterHouse.getFlag().getPosition());
        
        assertFalse(slaughterHouse.getFlag().getStackedCargo().isEmpty());
        assertNull(butcher.getCargo());
        assertEquals(butcher.getTarget(), slaughterHouse.getPosition());
        
        /* Verify that the butcher goes back to the slaughterHouse */
        Utils.fastForwardUntilWorkersReachTarget(map, butcher);
        
        assertTrue(butcher.isInsideBuilding());
    }

    @Test
    public void testProductionOfOneBreadConsumesOnePig() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(), point3);

        /* Finish construction of the slaughterHouse */
        Utils.constructMediumHouse(slaughterHouse);
        
        /* Populate the slaughterHouse */        
        Worker butcher = Utils.occupyBuilding(new Butcher(map), slaughterHouse, map);
        
        /* Deliver ingredients to the slaughterHouse */
        slaughterHouse.putCargo(new Cargo(PIG, map));
        
        /* Wait until the slaughterHouse worker produces meat*/
        assertTrue(slaughterHouse.getAmount(PIG) == 1);
        
        Utils.fastForward(150, map);
        
        assertTrue(slaughterHouse.getAmount(PIG) == 0);
    }

    @Test
    public void testProductionCountdownStartsWhenMaterialIsAvailable() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place slaughter house */
        Point point3 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(), point3);

        /* Finish construction of the slaughterHouse */
        Utils.constructMediumHouse(slaughterHouse);
        
        /* Populate the slaughterHouse */        
        Worker butcher = Utils.occupyBuilding(new Butcher(map), slaughterHouse, map);
        
        /* Fast forward so that the slaughterHouse worker would have produced meat
           if it had had a pig
        */        
        Utils.fastForward(150, map);
        
        assertNull(butcher.getCargo());
        
        /* Deliver ingredients to the slaughterHouse */
        slaughterHouse.putCargo(new Cargo(PIG, map));
        
        /* Verify that it takes 50 steps for the slaughterHouse worker to produce the meat */
        int i;
        for (i = 0; i < 50; i++) {
            assertNull(butcher.getCargo());
            map.stepTime();
        }
        
        assertNotNull(butcher.getCargo());
    }

}
