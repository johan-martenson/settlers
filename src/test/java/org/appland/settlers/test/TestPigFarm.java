/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.PigFarm;
import org.appland.settlers.model.PigBreeder;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.PIG_BREEDER;
import static org.appland.settlers.model.Material.PIG;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
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
public class TestPigFarm {

    @Test
    public void testUnfinishedPigFarmNeedsNoPigBreeder() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place pig farm */
        Point point0 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(), point0);

        assertTrue(farm.underConstruction());
        assertFalse(farm.needsWorker());
    }

    @Test
    public void testFinishedPigFarmNeedsPigBreeder() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place pig farm */
        Point point0 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(), point0);

        Utils.constructHouse(farm, map);
        
        assertTrue(farm.ready());
        assertTrue(farm.needsWorker());
    }

    @Test
    public void testPigBreederIsAssignedToFinishedPigFarm() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish the pig farm */
        Utils.constructHouse(farm, map);
        
        /* Run game logic twice, once to place courier and once to place pig breeder */
        Utils.fastForward(2, map);

        /* Verify that there was a pig breeder added */
        assertTrue(map.getAllWorkers().size() == 3);

        Utils.verifyListContainsWorkerOfType(map.getAllWorkers(), PigBreeder.class);
    }

    @Test
    public void testPigBreederRestsInPigFarmThenLeaves() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructHouse(pigFarm, map);

        PigBreeder pigBreeder = new PigBreeder(map);
        
        Utils.occupyBuilding(pigBreeder, pigFarm, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm.putCargo(waterCargo);
        pigFarm.putCargo(wheatCargo);
        
        /* Run the game logic 99 times and make sure the pig breeder stays in the pig farm */
        int i;
        for (i = 0; i < 99; i++) {
            assertTrue(pigBreeder.isInsideBuilding());
            map.stepTime();
        }
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Step once and make sure the pig breedre goes out of the pig farm */
        map.stepTime();        
        
        assertFalse(pigBreeder.isInsideBuilding());
    }

    @Test
    public void testPigBreederFeedsThePigsWhenItHasResources() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructHouse(pigFarm, map);

        /* Deliver wheat and pig to the farm */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);
        
        pigFarm.putCargo(wheatCargo);
        pigFarm.putCargo(waterCargo);
        
        /* Occupy the pig farm with a pig breeder */
        PigBreeder pigBreeder = new PigBreeder(map);
        
        Utils.occupyBuilding(pigBreeder, pigFarm, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Let the pigBreeder rest */
        Utils.fastForward(99, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Step once and make sure the pigBreeder goes out of the farm */
        map.stepTime();        
        
        assertFalse(pigBreeder.isInsideBuilding());

        Point point = pigBreeder.getTarget();

        assertTrue(pigBreeder.isTraveling());
        
        /* Let the pigBreeder reach the spot and start to feed the pigs */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);
        
        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isAt(point));
        assertTrue(pigBreeder.isFeeding());
        
        int i;
        for (i = 0; i < 19; i++) {
            assertTrue(pigBreeder.isFeeding());
            map.stepTime();
        }

        assertTrue(pigBreeder.isFeeding());
        assertFalse(map.isCropAtPoint(point));

        map.stepTime();
        
        /* Verify that the pigBreeder stopped feeding */
        assertFalse(pigBreeder.isFeeding());
        assertNull(pigBreeder.getCargo());
    }

    @Test
    public void testPigBreederReturnsAfterFeeding() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructHouse(pigFarm, map);

        /* Assign a pigBreeder to the farm */
        PigBreeder pigBreeder = new PigBreeder(map);
        
        Utils.occupyBuilding(pigBreeder, pigFarm, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm.putCargo(waterCargo);
        pigFarm.putCargo(wheatCargo);

        /* Wait for the pigBreeder to rest */
        Utils.fastForward(99, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Step once to let the pigBreeder go out to plant */
        map.stepTime();        
        
        assertFalse(pigBreeder.isInsideBuilding());

        Point point = pigBreeder.getTarget();

        assertTrue(pigBreeder.isTraveling());
        
        /* Let the pigBreeder reach the intended spot and start to feed */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);
        
        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isAt(point));
        assertTrue(pigBreeder.isFeeding());
        
        /* Wait for the pigBreeder to feed */
        Utils.fastForward(19, map);
        
        assertTrue(pigBreeder.isFeeding());

        map.stepTime();
        
        /* Verify that the pigBreeder stopped feeding and is walking back to the farm */
        assertFalse(pigBreeder.isFeeding());
        assertTrue(pigBreeder.isTraveling());
        assertEquals(pigBreeder.getTarget(), pigFarm.getPosition());
        assertTrue(pigBreeder.getPlannedPath().contains(pigFarm.getFlag().getPosition()));

        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);
        
        assertTrue(pigBreeder.isArrived());        
        assertTrue(pigBreeder.isInsideBuilding());
    }

    @Test
    public void testPigBreederDeliversPigToFlag() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);
        
        Utils.constructHouse(pigFarm, map);
        
        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm.putCargo(waterCargo);
        pigFarm.putCargo(wheatCargo);

        /* Assign a pigBreeder to the farm */
        PigBreeder pigBreeder = new PigBreeder(map);
        
        Utils.occupyBuilding(pigBreeder, pigFarm, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Let the pigBreeder rest */
        Utils.fastForward(99, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Step once and to let the pigBreeder go out to feed */
        map.stepTime();        
        
        assertFalse(pigBreeder.isInsideBuilding());

        Point point = pigBreeder.getTarget();
        
        assertTrue(pigBreeder.isTraveling());
        
        /* Let the pigBreeder reach the intended spot */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);
        
        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isAt(point));
        assertTrue(pigBreeder.isFeeding());
        
        /* Wait for the pigBreeder to feed the pigs */
        Utils.fastForward(19, map);
        
        assertTrue(pigBreeder.isFeeding());
        
        map.stepTime();
        
        /* PigBreeder is walking back to farm without carrying a cargo */
        assertFalse(pigBreeder.isFeeding());
        assertEquals(pigBreeder.getTarget(), pigFarm.getPosition());
        assertNull(pigBreeder.getCargo());
        
        /* Let the pigBreeder reach the farm */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isInsideBuilding());

        /* Wait for the pig breeder to prepare the pig */
        for (int i = 0; i < 20; i++) {
            assertNull(pigBreeder.getCargo());
            map.stepTime();
        }
        
        /* PigBreeder leaves the building to place the cargo at the flag */
        map.stepTime();
        
        assertFalse(pigBreeder.isInsideBuilding());
        assertTrue(pigFarm.getFlag().getStackedCargo().isEmpty());
        assertNotNull(pigBreeder.getCargo());
        assertEquals(pigBreeder.getCargo().getMaterial(), PIG);
        assertEquals(pigBreeder.getTarget(), pigFarm.getFlag().getPosition());
        
        /* Let the pigBreeder reach the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, pigFarm.getFlag().getPosition());
        
        assertFalse(pigFarm.getFlag().getStackedCargo().isEmpty());
        assertNull(pigBreeder.getCargo());
        
        /* The pigBreeder goes back to the building */
        assertEquals(pigBreeder.getTarget(), pigFarm.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);
        
        assertTrue(pigBreeder.isInsideBuilding());
    }

    @Test
    public void testPigFarmWithoutPigBreederProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructHouse(farm, map);
        
        /* Verify that the farm does not produce any wheat */
        int i;
        for (i = 0; i < 200; i++) {
            assertTrue(farm.getFlag().getStackedCargo().isEmpty());
            
            map.stepTime();
        }

        assertTrue(farm.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testPigFarmWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing pig farm */
        Point point26 = new Point(8, 8);
        Building pigFarm0 = map.placeBuilding(new PigFarm(), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(map), pigFarm0, map);

        /* Deliver material to the pig farm */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);

        pigFarm0.putCargo(wheatCargo);
        pigFarm0.putCargo(wheatCargo);

        pigFarm0.putCargo(waterCargo);
        pigFarm0.putCargo(waterCargo);
        
        /* Let the pig breeder rest */
        Utils.fastForward(100, map);

        /* Wait for the pig breeder to produce a new meat cargo */
        Worker ww = pigFarm0.getWorker();

        for (int i = 0; i < 1000; i++) {
            if (ww.getCargo() != null && ww.getPosition().equals(pigFarm0.getPosition())) {
                break;
            }
            
            map.stepTime();
        }

        assertNotNull(ww.getCargo());

        /* Verify that the pig breeder puts the meat cargo at the flag */
        assertEquals(ww.getTarget(), pigFarm0.getFlag().getPosition());
        assertTrue(pigFarm0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, pigFarm0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(pigFarm0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait for the worker to go back to the pig farm */
        assertEquals(ww.getTarget(), pigFarm0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, pigFarm0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        for (int i = 0; i < 1000; i++) {
            if (ww.getCargo() != null && ww.getPosition().equals(pigFarm0.getPosition())) {
                break;
            }
            
            map.stepTime();
        }

        assertNotNull(ww.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(ww.getTarget(), pigFarm0.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, pigFarm0.getFlag().getPosition());
        
        assertNull(ww.getCargo());
        assertEquals(pigFarm0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing pig farm */
        Point point26 = new Point(8, 8);
        Building pigFarm0 = map.placeBuilding(new PigFarm(), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Deliver material to the pig farm */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);
        
        pigFarm0.putCargo(wheatCargo);
        pigFarm0.putCargo(wheatCargo);

        pigFarm0.putCargo(waterCargo);
        pigFarm0.putCargo(waterCargo);
        
        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(map), pigFarm0, map);

        /* Let the pig breeder rest */
        Utils.fastForward(100, map);

        /* Wait for the pig breeder to produce a new meat cargo */
        Worker ww = pigFarm0.getWorker();

        for (int i = 0; i < 1000; i++) {
            if (ww.getCargo() != null && ww.getPosition().equals(pigFarm0.getPosition())) {
                break;
            }
            
            map.stepTime();
        }

        assertNotNull(ww.getCargo());

        /* Verify that the pig breeder puts the meat cargo at the flag */
        assertEquals(ww.getTarget(), pigFarm0.getFlag().getPosition());
        assertTrue(pigFarm0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, pigFarm0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(pigFarm0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = pigFarm0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), pigFarm0.getFlag().getPosition());
    
        /* Connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(headquarter0.getFlag(), pigFarm0.getFlag());
    
        /* Assign a courier to the road */
        Courier courier = new Courier(map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);
    
        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(pigFarm0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));
    
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
    
        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), pigFarm0.getFlag().getPosition());
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);
        
        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());
        
        int amount = headquarter0.getAmount(PIG);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());
        
        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(PIG), amount + 1);
    }

    @Test
    public void testPigBreederGoesBackToStorageWhenPigFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing pig farm */
        Point point26 = new Point(8, 8);
        Building pigFarm0 = map.placeBuilding(new PigFarm(), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(map), pigFarm0, map);
        
        /* Destroy the pig farm */
        Worker ww = pigFarm0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), pigFarm0.getPosition());

        pigFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(PIG_BREEDER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());

        /* Verify that the pig breeder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(PIG_BREEDER), amount + 1);
    }

    @Test
    public void testPigBreederGoesBackOnToStorageOnRoadsIfPossibleWhenPigFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing pig farm */
        Point point26 = new Point(8, 8);
        Building pigFarm0 = map.placeBuilding(new PigFarm(), point26);

        /* Connect the pig farm with the headquarter */
        map.placeAutoSelectedRoad(pigFarm0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(map), pigFarm0, map);
        
        /* Destroy the pig farm */
        Worker ww = pigFarm0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), pigFarm0.getPosition());

        pigFarm0.tearDown();

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
    public void testPigBreederWithoutResourcesProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(), point3);

        Utils.constructHouse(pigFarm, map);

        /* Occupy the pig farm with a pig breeder */
        PigBreeder pigBreeder = new PigBreeder(map);
        
        Utils.occupyBuilding(pigBreeder, pigFarm, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Let the pigBreeder rest */
        Utils.fastForward(99, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Verify that the pig breeder doesn't produce anything */
        for (int i = 0; i < 300; i++) {
            assertEquals(pigBreeder.getCargo(), null);
            
            map.stepTime();
        }
    }

    @Test
    public void testPigBreederWithoutResourcesStaysInHouse() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(), point3);

        Utils.constructHouse(pigFarm, map);

        /* Occupy the pig farm with a pig breeder */
        PigBreeder pigBreeder = new PigBreeder(map);
        
        Utils.occupyBuilding(pigBreeder, pigFarm, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Let the pigBreeder rest */
        Utils.fastForward(99, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Verify that the pig breeder doesn't produce anything */
        for (int i = 0; i < 300; i++) {
            assertTrue(pigBreeder.isInsideBuilding());
            
            map.stepTime();
        }
    }

    @Test
    public void testPigBreederFeedsPigsWithWaterAndWheat() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(), point3);
        
        Utils.constructHouse(pigFarm, map);

        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm.putCargo(waterCargo);
        pigFarm.putCargo(wheatCargo);

        /* Assign a pigBreeder to the farm */
        PigBreeder pigBreeder = new PigBreeder(map);
        
        Utils.occupyBuilding(pigBreeder, pigFarm, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Let the pigBreeder rest */
        Utils.fastForward(99, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Step once and to let the pigBreeder go out to feed */
        map.stepTime();        
        
        assertFalse(pigBreeder.isInsideBuilding());

        Point point = pigBreeder.getTarget();
        
        assertTrue(pigBreeder.isTraveling());
        
        /* Let the pigBreeder reach the intended spot */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);
        
        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isAt(point));
        assertTrue(pigBreeder.isFeeding());
        
        /* Wait for the pigBreeder to feed the pigs */
        Utils.fastForward(19, map);
        
        assertTrue(pigBreeder.isFeeding());
        
        map.stepTime();
        
        /* Verify that the pig breeder is done feeding and has consumed the water and wheat */
        assertFalse(pigBreeder.isFeeding());
        assertEquals(pigFarm.getAmount(WATER), 0);
        assertEquals(pigFarm.getAmount(WHEAT), 0);
    }

    @Test
    public void testDestroyedPigFarmIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing pig farm */
        Point point26 = new Point(8, 8);
        Building pigFarm0 = map.placeBuilding(new PigFarm(), point26);

        /* Connect the pig farm with the headquarter */
        map.placeAutoSelectedRoad(pigFarm0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Destroy the pig farm */
        pigFarm0.tearDown();

        assertTrue(pigFarm0.burningDown());

        /* Wait for the pig farm to stop burning */
        Utils.fastForward(50, map);
        
        assertTrue(pigFarm0.destroyed());
        
        /* Wait for the pig farm to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), pigFarm0);
            
            map.stepTime();
        }
        
        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(pigFarm0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing pig farm */
        Point point26 = new Point(8, 8);
        Building pigFarm0 = map.placeBuilding(new PigFarm(), point26);
        
        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(pigFarm0.getPosition(), pigFarm0.getFlag().getPosition()));
        
        map.removeFlag(pigFarm0.getFlag());

        assertNull(map.getRoad(pigFarm0.getPosition(), pigFarm0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing pig farm */
        Point point26 = new Point(8, 8);
        Building pigFarm0 = map.placeBuilding(new PigFarm(), point26);
        
        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0, map);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(pigFarm0.getPosition(), pigFarm0.getFlag().getPosition()));
        
        pigFarm0.tearDown();

        assertNull(map.getRoad(pigFarm0.getPosition(), pigFarm0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInPigFarmCanBeStopped() throws Exception {

        /* Create game map */
        GameMap map = new GameMap(20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        
        /* Place pig farm */
        Point point1 = new Point(8, 6);
        Building pigFarm0 = map.placeBuilding(new PigFarm(), point1);
        
        /* Connect the pig farm and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);
        
        /* Finish the pig farm */
        Utils.constructHouse(pigFarm0, map);
        
        /* Assign a worker to the pig farm */
        PigBreeder ww = new PigBreeder(map);
        
        Utils.occupyBuilding(ww, pigFarm0, map);
        
        assertTrue(ww.isInsideBuilding());

        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm0.putCargo(waterCargo);
        pigFarm0.putCargo(wheatCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the pig breeder to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, ww);
        
        assertEquals(ww.getCargo().getMaterial(), PIG);

        /* Wait for the worker to deliver the cargo */
        assertEquals(ww.getTarget(), pigFarm0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, pigFarm0.getFlag().getPosition());

        /* Stop production and verify that no pig is produced */
        pigFarm0.stopProduction();
        
        assertFalse(pigFarm0.isProductionEnabled());
        
        for (int i = 0; i < 300; i++) {
            assertNull(ww.getCargo());
            
            map.stepTime();
        }
    }

    @Test
    public void testProductionInPigFarmCanBeResumed() throws Exception {

        /* Create game map */
        GameMap map = new GameMap(20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        
        /* Place pig farm */
        Point point1 = new Point(8, 6);
        Building pigFarm0 = map.placeBuilding(new PigFarm(), point1);
        
        /* Connect the pig farm and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);
        
        /* Finish the pig farm */
        Utils.constructHouse(pigFarm0, map);
        
        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm0.putCargo(waterCargo);
        pigFarm0.putCargo(waterCargo);

        pigFarm0.putCargo(wheatCargo);
        pigFarm0.putCargo(wheatCargo);

        /* Assign a worker to the pig farm */
        PigBreeder ww = new PigBreeder(map);
        
        Utils.occupyBuilding(ww, pigFarm0, map);
        
        assertTrue(ww.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the pig breeder to produce pig */
        Utils.fastForwardUntilWorkerProducesCargo(map, ww);

        assertEquals(ww.getCargo().getMaterial(), PIG);

        /* Wait for the worker to deliver the cargo */
        assertEquals(ww.getTarget(), pigFarm0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, pigFarm0.getFlag().getPosition());

        /* Stop production */
        pigFarm0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(ww.getCargo());
            
            map.stepTime();
        }

        /* Resume production and verify that the pig farm produces pig again */
        pigFarm0.resumeProduction();

        assertTrue(pigFarm0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, ww);

        assertNotNull(ww.getCargo());
    }
}
