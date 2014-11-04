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

        Utils.constructHouse(mill, map);

        assertTrue(mill.ready());
        assertTrue(mill.needsWorker());
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
        
        /* Finish the mill */
        Utils.constructHouse(mill, map);
        
        /* Run game logic twice, once to place courier and once to place miller */
        Utils.fastForward(2, map);
        
        assertTrue(map.getWorkers().size() == 3);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Miller.class);
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
        
        /* Finish the mill */
        Utils.constructHouse(mill, map);
        
        int i;
        for (i = 0; i < 200; i++) {
            assertTrue(mill.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testMillerEntersTheMill() throws Exception {
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
        
        /* Finish the mill */
        Utils.constructHouse(mill, map);
        
        /* Run game logic twice, once to place courier and once to place miller */
        Utils.fastForward(2, map);
        
        /* Get the miller */
        Miller miller = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Miller) {
                miller = (Miller)w;
            }
        }

        /* Let the miller reach the mill */
        Utils.fastForwardUntilWorkerReachesPoint(map, miller, mill.getPosition());
        
        assertNotNull(miller);
        assertTrue(miller.isInsideBuilding());
        assertEquals(mill.getWorker(), miller);
    }
    
    @Test
    public void testMillWorkerRests() throws Exception {
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
        
        /* Finish the mill */
        Utils.constructHouse(mill, map);
        
        /* Put the miller in the mill */
        Miller miller = new Miller(map);
        
        Utils.occupyBuilding(miller, mill, map);
        
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
        
        /* Finish the mill */
        Utils.constructHouse(mill, map);
        
        /* Put the miller in the mill */
        Miller miller = new Miller(map);
        
        Utils.occupyBuilding(miller, mill, map);
        
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
        
        /* Finish the mill */
        Utils.constructHouse(mill, map);
        
        /* Deliver wheat to the mill */
        Cargo cargo = new Cargo(WHEAT, map);
        
        mill.putCargo(cargo);
        
        /* Put the worker in the mill */
        Miller miller = new Miller(map);
        
        Utils.occupyBuilding(miller, mill, map);
        
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
        
        /* Finish the mill */
        Utils.constructHouse(mill, map);

        /* Deliver wheat to the mill */
        Cargo cargo = new Cargo(WHEAT, map);
        
        mill.putCargo(cargo);
        
        /* Put the worker in the mill */
        Miller miller = new Miller(map);
        
        Utils.occupyBuilding(miller, mill, map);
        
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
        
        /* Let the worker walk back to the mill */
        assertEquals(miller.getTarget(), mill.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, miller);
        
        assertTrue(miller.isInsideBuilding());
    }

    @Test
    public void testMillWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing mill */
        Point point26 = new Point(8, 8);
        Building mill0 = map.placeBuilding(new Mill(), point26);

        /* Finish construction of the mill */
        Utils.constructHouse(mill0, map);

        /* Occupy the mill */
        Utils.occupyBuilding(new Miller(map), mill0, map);

        /* Deliver material to the mill */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        
        mill0.putCargo(wheatCargo);
        mill0.putCargo(wheatCargo);
        
        /* Let the miller rest */
        Utils.fastForward(100, map);

        /* Wait for the miller to produce a new flour cargo */
        Utils.fastForward(50, map);

        Worker ww = mill0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the miller puts the flour cargo at the flag */
        assertEquals(ww.getTarget(), mill0.getFlag().getPosition());
        assertTrue(mill0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, mill0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(mill0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait for the worker to go back to the mill */
        assertEquals(ww.getTarget(), mill0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, mill0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(ww.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(ww.getTarget(), mill0.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, mill0.getFlag().getPosition());
        
        assertNull(ww.getCargo());
        assertEquals(mill0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing mill */
        Point point26 = new Point(8, 8);
        Building mill0 = map.placeBuilding(new Mill(), point26);

        /* Finish construction of the mill */
        Utils.constructHouse(mill0, map);

        /* Deliver material to the mill */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        
        mill0.putCargo(wheatCargo);
        mill0.putCargo(wheatCargo);

        /* Occupy the mill */
        Utils.occupyBuilding(new Miller(map), mill0, map);

        /* Let the miller rest */
        Utils.fastForward(100, map);

        /* Wait for the miller to produce a new flour cargo */
        Utils.fastForward(50, map);

        Worker ww = mill0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the miller puts the flour cargo at the flag */
        assertEquals(ww.getTarget(), mill0.getFlag().getPosition());
        assertTrue(mill0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, mill0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(mill0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = mill0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), mill0.getFlag().getPosition());
    
        /* Connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(headquarter0.getFlag(), mill0.getFlag());
    
        /* Assign a courier to the road */
        Courier courier = new Courier(map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);
    
        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(mill0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));
    
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
    
        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), mill0.getFlag().getPosition());
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);
        
        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());
        
        int amount = headquarter0.getAmount(FLOUR);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());
        
        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(FLOUR), amount + 1);
    }

    @Test
    public void testMillerGoesBackToStorageWhenMillIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing mill */
        Point point26 = new Point(8, 8);
        Building mill0 = map.placeBuilding(new Mill(), point26);

        /* Finish construction of the mill */
        Utils.constructHouse(mill0, map);

        /* Occupy the mill */
        Utils.occupyBuilding(new Miller(map), mill0, map);
        
        /* Destroy the mill */
        Worker ww = mill0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), mill0.getPosition());

        mill0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(MILLER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());

        /* Verify that the miller is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MILLER), amount + 1);
    }

    @Test
    public void testMillerGoesBackOnToStorageOnRoadsIfPossibleWhenMillIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing mill */
        Point point26 = new Point(8, 8);
        Building mill0 = map.placeBuilding(new Mill(), point26);

        /* Connect the mill with the headquarter */
        map.placeAutoSelectedRoad(mill0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the mill */
        Utils.constructHouse(mill0, map);

        /* Occupy the mill */
        Utils.occupyBuilding(new Miller(map), mill0, map);
        
        /* Destroy the mill */
        Worker ww = mill0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), mill0.getPosition());

        mill0.tearDown();

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

    @Test
    public void testDestroyedMillIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing mill */
        Point point26 = new Point(8, 8);
        Building mill0 = map.placeBuilding(new Mill(), point26);

        /* Connect the mill with the headquarter */
        map.placeAutoSelectedRoad(mill0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the mill */
        Utils.constructHouse(mill0, map);

        /* Destroy the mill */
        mill0.tearDown();

        assertTrue(mill0.burningDown());

        /* Wait for the mill to stop burning */
        Utils.fastForward(50, map);
        
        assertTrue(mill0.destroyed());
        
        /* Wait for the mill to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), mill0);
            
            map.stepTime();
        }
        
        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(mill0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing mill */
        Point point26 = new Point(8, 8);
        Building mill0 = map.placeBuilding(new Mill(), point26);
        
        /* Finish construction of the mill */
        Utils.constructHouse(mill0, map);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(mill0.getPosition(), mill0.getFlag().getPosition()));
        
        map.removeFlag(mill0.getFlag());

        assertNull(map.getRoad(mill0.getPosition(), mill0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing mill */
        Point point26 = new Point(8, 8);
        Building mill0 = map.placeBuilding(new Mill(), point26);
        
        /* Finish construction of the mill */
        Utils.constructHouse(mill0, map);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(mill0.getPosition(), mill0.getFlag().getPosition()));
        
        mill0.tearDown();

        assertNull(map.getRoad(mill0.getPosition(), mill0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInMillCanBeStopped() throws Exception {

        /* Create game map */
        GameMap map = new GameMap(20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        
        /* Place mill */
        Point point1 = new Point(8, 6);
        Building mill0 = map.placeBuilding(new Mill(), point1);
        
        /* Connect the mill and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);
        
        /* Finish the mill */
        Utils.constructHouse(mill0, map);
        
        /* Deliver material to the mill */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        
        mill0.putCargo(wheatCargo);
        mill0.putCargo(wheatCargo);

        /* Assign a worker to the mill */
        Miller ww = new Miller(map);
        
        Utils.occupyBuilding(ww, mill0, map);
        
        assertTrue(ww.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the miller to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, ww);
        
        assertEquals(ww.getCargo().getMaterial(), FLOUR);

        /* Wait for the worker to deliver the cargo */
        assertEquals(ww.getTarget(), mill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, mill0.getFlag().getPosition());

        /* Stop production and verify that no flour is produced */
        mill0.stopProduction();
        
        assertFalse(mill0.isProductionEnabled());
        
        for (int i = 0; i < 300; i++) {
            assertNull(ww.getCargo());
            
            map.stepTime();
        }
    }

    @Test
    public void testProductionInMillCanBeResumed() throws Exception {

        /* Create game map */
        GameMap map = new GameMap(20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        
        /* Place mill */
        Point point1 = new Point(8, 6);
        Building mill0 = map.placeBuilding(new Mill(), point1);
        
        /* Connect the mill and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);
        
        /* Finish the mill */
        Utils.constructHouse(mill0, map);
        
        /* Assign a worker to the mill */
        Miller ww = new Miller(map);
        
        Utils.occupyBuilding(ww, mill0, map);
        
        assertTrue(ww.isInsideBuilding());

        /* Deliver material to the mill */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        
        mill0.putCargo(wheatCargo);
        mill0.putCargo(wheatCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the miller to produce flour */
        Utils.fastForwardUntilWorkerProducesCargo(map, ww);

        assertEquals(ww.getCargo().getMaterial(), FLOUR);

        /* Wait for the worker to deliver the cargo */
        assertEquals(ww.getTarget(), mill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, mill0.getFlag().getPosition());

        /* Stop production */
        mill0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(ww.getCargo());
            
            map.stepTime();
        }

        /* Resume production and verify that the mill produces flour again */
        mill0.resumeProduction();

        assertTrue(mill0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, ww);

        assertNotNull(ww.getCargo());
    }
}
