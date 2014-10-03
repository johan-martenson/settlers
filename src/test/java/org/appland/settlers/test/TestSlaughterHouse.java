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
import org.appland.settlers.model.Courier;
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
        Utils.constructHouse(slaughterHouse, map);
        
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
        Utils.constructHouse(slaughterHouse, map);
        
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
        Utils.constructHouse(slaughterHouse, map);
        
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
        Utils.constructHouse(slaughterHouse, map);

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
        Utils.constructHouse(slaughterHouse, map);
        
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
        Utils.constructHouse(slaughterHouse, map);
        
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
        Utils.constructHouse(slaughterHouse, map);
        
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
        Utils.constructHouse(slaughterHouse, map);
        
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

    @Test
    public void testSlaughterHouseWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing slaughter house */
        Point point26 = new Point(8, 8);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(), point26);

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0, map);

        /* Occupy the slaughter house */
        Utils.occupyBuilding(new Butcher(map), slaughterHouse0, map);

        /* Deliver material to the slaughter house */
        Cargo pigCargo = new Cargo(PIG, map);
        
        slaughterHouse0.putCargo(pigCargo);
        slaughterHouse0.putCargo(pigCargo);
        
        /* Let the butcher rest */
        Utils.fastForward(100, map);

        /* Wait for the butcher to produce a new meat cargo */
        Utils.fastForward(50, map);

        Worker ww = slaughterHouse0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the butcher puts the meat cargo at the flag */
        assertEquals(ww.getTarget(), slaughterHouse0.getFlag().getPosition());
        assertTrue(slaughterHouse0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, slaughterHouse0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(slaughterHouse0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait for the worker to go back to the slaughter house */
        assertEquals(ww.getTarget(), slaughterHouse0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, slaughterHouse0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(ww.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(ww.getTarget(), slaughterHouse0.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, slaughterHouse0.getFlag().getPosition());
        
        assertNull(ww.getCargo());
        assertEquals(slaughterHouse0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing slaughter house */
        Point point26 = new Point(8, 8);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(), point26);

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0, map);

        /* Deliver material to the slaughter house */
        Cargo pigCargo = new Cargo(PIG, map);
        
        slaughterHouse0.putCargo(pigCargo);
        slaughterHouse0.putCargo(pigCargo);

        /* Occupy the slaughter house */
        Utils.occupyBuilding(new Butcher(map), slaughterHouse0, map);

        /* Let the butcher rest */
        Utils.fastForward(100, map);

        /* Wait for the butcher to produce a new meat cargo */
        Utils.fastForward(50, map);

        Worker ww = slaughterHouse0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the butcher puts the meat cargo at the flag */
        assertEquals(ww.getTarget(), slaughterHouse0.getFlag().getPosition());
        assertTrue(slaughterHouse0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, slaughterHouse0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(slaughterHouse0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = slaughterHouse0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), slaughterHouse0.getFlag().getPosition());
    
        /* Connect the slaughter house with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(headquarter0.getFlag(), slaughterHouse0.getFlag());
    
        /* Assign a courier to the road */
        Courier courier = new Courier(map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);
    
        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(slaughterHouse0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
    
        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), slaughterHouse0.getFlag().getPosition());
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);
        
        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());
        
        int amount = headquarter0.getAmount(MEAT);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());
        
        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(MEAT), amount + 1);
    }

    @Test
    public void testButcherGoesBackToStorageWhenSlaughterHouseIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing slaughter house */
        Point point26 = new Point(8, 8);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(), point26);

        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0, map);

        /* Occupy the slaughter house */
        Utils.occupyBuilding(new Butcher(map), slaughterHouse0, map);
        
        /* Destroy the slaughter house */
        Worker ww = slaughterHouse0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), slaughterHouse0.getPosition());

        slaughterHouse0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(BUTCHER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());

        /* Verify that the butcher is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(BUTCHER), amount + 1);
    }

    @Test
    public void testButcherGoesBackOnToStorageOnRoadsIfPossibleWhenSlaughterHouseIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing slaughter house */
        Point point26 = new Point(8, 8);
        Building slaughterHouse0 = map.placeBuilding(new SlaughterHouse(), point26);

        /* Connect the slaughter house with the headquarter */
        map.placeAutoSelectedRoad(slaughterHouse0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the slaughter house */
        Utils.constructHouse(slaughterHouse0, map);

        /* Occupy the slaughter house */
        Utils.occupyBuilding(new Butcher(map), slaughterHouse0, map);
        
        /* Destroy the slaughter house */
        Worker ww = slaughterHouse0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), slaughterHouse0.getPosition());

        slaughterHouse0.tearDown();

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
