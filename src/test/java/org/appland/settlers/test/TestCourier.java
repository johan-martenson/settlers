/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Material;
import static org.appland.settlers.model.Material.COURIER;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Woodcutter;
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
public class TestCourier {
    
    @Test
    public void testNewStorageHasCouriers() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);

        Storage storage = new Storage();
        
        Point point1 = new Point(10, 10);
        map.placeBuilding(storage, point1);
        
        Utils.constructMediumHouse(storage);
        
        assertTrue(storage.getAmount(COURIER) > 0);
    }
    
    @Test
    public void testCourierWalksToIntendedRoad() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point0 = new Point(8, 4);
        Flag flag0 = map.placeFlag(point0);

        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(point1);

        Point point2 = new Point(6, 4);
        Flag flag2 = map.placeFlag(point2);
        
        Point point3 = new Point(7, 3);
        Point point4 = new Point(9, 3);
        
        Road road0 = map.placeRoad(point2, point3, point0);
        Road road1 = map.placeRoad(point0, point4, point1);

        Courier courier = new Courier(map);
        map.placeWorker(courier, flag2);

        courier.assignToRoad(road1);        
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getAssignedRoad(), road1);
        assertTrue(courier.isIdle());
    }

    @Test
    public void testCourierGoesToMiddlePointOfRoad() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point0 = new Point(8, 4);
        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(point1);

        Point point2 = new Point(6, 4);
        Flag flag2 = map.placeFlag(point2);
        
        Road road0 = map.placeRoad(point2, point0, point1);

        Courier courier = new Courier(map);
        map.placeWorker(courier, flag2);

        courier.assignToRoad(road0);        
        
        assertTrue(courier.isWalkingToRoad());
        assertFalse(courier.isIdle());
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getPosition(), point0);
        assertTrue(courier.isArrived());
        assertTrue(courier.isIdle());
        assertFalse(courier.isWalkingToIdlePoint());
    }

    @Test
    public void testCourierIsIdleWhenMiddlePointIsReached() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point0 = new Point(8, 4);
        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(point1);

        Point point2 = new Point(6, 4);
        Flag flag2 = map.placeFlag(point2);
        
        Road road0 = map.placeRoad(point2, point0, point1);

        Courier courier = new Courier(map);
        map.placeWorker(courier, flag2);

        courier.assignToRoad(road0);
        
        assertTrue(courier.isWalkingToRoad());
        assertFalse(courier.isIdle());
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getPosition(), point0);
        assertTrue(courier.isArrived());
        assertTrue(courier.isIdle());
    }
    
    @Test
    public void testCourierRemainsIdleWhenThereIsNoCargo() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point0 = new Point(8, 4);
        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(point1);

        Point point2 = new Point(6, 4);
        Flag flag2 = map.placeFlag(point2);
        
        Road road0 = map.placeRoad(point2, point0, point1);

        Courier courier = new Courier(map);
        map.placeWorker(courier, flag2);

        courier.assignToRoad(road0);
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getPosition(), point0);
        assertTrue(courier.isArrived());

        int i;
        for (i = 0; i < 200; i++) {
            assertTrue(courier.isArrived());
            assertTrue(courier.isIdle());
            
            assertEquals(courier.getPosition(), point0);

            assertFalse(flag1.hasCargoWaitingForRoad(road0));
            assertFalse(flag2.hasCargoWaitingForRoad(road0));
            assertFalse(courier.isWalkingToIdlePoint());
            
            map.stepTime();
        }
    }

    @Test
    public void testCourierWalksToMiddleOfRoadWhenItIsAssignedEvenIfFlagsHaveCargo() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point middle = new Point(7, 3);
        Point point1 = new Point(8, 4);
        Flag flag1 = map.placeFlag(point1);

        Point point2 = new Point(6, 4);
        Flag flag0 = map.placeFlag(point2);
        
        Point point3 = new Point(9, 5);
        Point point4 = new Point(11, 5);
        Building wc = map.placeBuilding(new Woodcutter(), point4.upLeft());
        
        Utils.constructSmallHouse(wc);
        
        Road road0 = map.placeRoad(point2, middle, point1);
        Road road1 = map.placeRoad(point1, point3, point4);
        
        Cargo cargo = new Cargo(WOOD, map);
        flag0.putCargo(cargo);
        cargo.setTarget(wc);
        
        Courier courier = new Courier(map);
        map.placeWorker(courier, flag0);

        courier.assignToRoad(road0);
        
        assertTrue(flag0.hasCargoWaitingForRoad(road0));
        assertTrue(courier.isWalkingToRoad());
        
        assertFalse(courier.isIdle());
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getPosition(), middle);
        assertTrue(courier.isArrived());
        assertTrue(courier.isIdle());
        
        assertFalse(courier.isWalkingToIdlePoint());
    }
    
    @Test
    public void testCourierPicksUpCargoFromFlag() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point middle = new Point(7, 3);
        Point point1 = new Point(8, 4);
        Flag flag1 = map.placeFlag(point1);

        Point point2 = new Point(6, 4);
        Flag flag0 = map.placeFlag(point2);
        
        Point point3 = new Point(9, 5);
        Point point4 = new Point(11, 5);
        Building wc = map.placeBuilding(new Woodcutter(), point4.upLeft());
        
        Utils.constructSmallHouse(wc);
        
        Road road0 = map.placeRoad(point2, middle, point1);
        Road road1 = map.placeRoad(point1, point3, point4);
        
        /* Place cargo at flag0 */
        Cargo cargo = new Cargo(WOOD, map);
        flag0.putCargo(cargo);
        cargo.setTarget(wc);
        
        /* Place courier at same flag as cargo */
        Courier courier = new Courier(map);
        map.placeWorker(courier, flag0);

        courier.assignToRoad(road0);

        /* Courier will walk to idle point at the road */
        assertTrue(courier.isWalkingToRoad());
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getPosition(), middle);

        assertFalse(flag0.getStackedCargo().isEmpty());

        assertTrue(courier.isArrived());
        assertTrue(flag0.hasCargoWaitingForRoad(road0));
        assertTrue(courier.isIdle());
        
        /* Courier detects the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), flag0.getPosition());
        assertTrue(courier.isTraveling());
        assertFalse(flag0.getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
        */
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), flag1.getPosition());
        assertTrue(flag0.getStackedCargo().isEmpty());
    }
    
    @Test
    public void testCourierDeliversCargoAndBecomesIdle() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point middle = new Point(8, 4);
        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(point1);

        Point point2 = new Point(6, 4);
        Flag flag0 = map.placeFlag(point2);
        
        Point point3 = new Point(12, 4);
        Point point4 = new Point(13, 5);
        Building wc = map.placeBuilding(new Woodcutter(), point4.upLeft());
        
        Utils.constructSmallHouse(wc);
        
        Road road0 = map.placeRoad(point2, middle, point1);
        Road road1 = map.placeRoad(point1, point3, point4);
        
        /* Place cargo at flag0 */
        Cargo cargo = new Cargo(WOOD, map);
        flag0.putCargo(cargo);
        cargo.setTarget(wc);
        
        /* Place courier at same flag as cargo */
        Courier courier = new Courier(map);
        map.placeWorker(courier, flag0);

        courier.assignToRoad(road0);

        /* Courier will walk to idle point at the road */
        assertTrue(courier.isWalkingToRoad());
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getPosition(), middle);

        assertFalse(flag0.getStackedCargo().isEmpty());

        assertTrue(courier.isArrived());
        assertTrue(flag0.hasCargoWaitingForRoad(road0));
        assertTrue(courier.isIdle());
        
        /* Courier detects the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), flag0.getPosition());
        assertTrue(courier.isTraveling());
        assertFalse(flag0.getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), flag1.getPosition());
        assertTrue(flag0.getStackedCargo().isEmpty());
        assertFalse(courier.isIdle());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        assertNull(courier.getCargo());
        assertFalse(flag1.getStackedCargo().isEmpty());
        assertFalse(courier.isIdle());
        assertEquals(flag1.getStackedCargo().get(0), cargo);
        
        /* After delivering the cargo, the courier goes back to the idle spot */
        assertFalse(courier.isIdle());
        assertFalse(courier.getTarget().equals(courier.getPosition()));
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        assertTrue(courier.isIdle());
    }
    
    @Test
    public void testCourierPicksUpNewCargoAtSameFlagAfterDelivery() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point middlePoint = new Point(8, 4);
        Point middleFlagPoint = new Point(10, 4);
        Flag middleFlag = map.placeFlag(middleFlagPoint);

        Point leftFlagPoint = new Point(6, 4);
        
        Point point3 = new Point(12, 4);
        Point rightFlagPoint = new Point(13, 5);
        Building rightWoodcutter = map.placeBuilding(new Woodcutter(), rightFlagPoint.upLeft());
        Building leftWoodcutter = map.placeBuilding(new Woodcutter(), leftFlagPoint.upLeft());
        
        Utils.constructSmallHouse(rightWoodcutter);
        
        Road road0 = map.placeRoad(leftFlagPoint, middlePoint, middleFlagPoint);
        Road road1 = map.placeRoad(middleFlagPoint, point3, rightFlagPoint);
        
        /* Place cargo at flag0 */
        Cargo cargoForRightWoodcutter = new Cargo(WOOD, map);
        leftWoodcutter.getFlag().putCargo(cargoForRightWoodcutter);
        cargoForRightWoodcutter.setTarget(rightWoodcutter);
        
        /* Place courier at same flag as cargo */
        Courier courier = new Courier(map);
        map.placeWorker(courier, leftWoodcutter.getFlag());

        courier.assignToRoad(road0);

        /* Courier will walk to idle point at the road */
        assertTrue(courier.isWalkingToRoad());
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getPosition(), middlePoint);
        assertTrue(courier.isArrived());
        assertTrue(courier.isIdle());
        
        /* Courier detects the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), leftWoodcutter.getFlag().getPosition());
        assertTrue(courier.isTraveling());
        assertFalse(leftWoodcutter.getFlag().getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        assertTrue(courier.isAt(leftWoodcutter.getFlag().getPosition()));
        
        /* Place cargo at other flag for courier to discover after delivery */
        Cargo cargoForLeftWoodcutter = new Cargo(STONE, map);
        
        cargoForLeftWoodcutter.setPosition(middleFlagPoint);
        middleFlag.putCargo(cargoForLeftWoodcutter);
        cargoForLeftWoodcutter.setTarget(leftWoodcutter);
        
        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(courier.getCargo(), cargoForRightWoodcutter);
        assertEquals(courier.getTarget(), middleFlagPoint);
        assertTrue(leftWoodcutter.getFlag().getStackedCargo().isEmpty());
        assertFalse(courier.isIdle());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, middleFlagPoint);
        
        assertEquals(middleFlag.getStackedCargo().get(0), cargoForRightWoodcutter);
        assertEquals(courier.getCargo(), cargoForLeftWoodcutter);
        assertFalse(courier.isIdle());
        
        /* After delivering the cargo, the courier picks up the other cargo without going back to the middle */
        assertEquals(courier.getTarget(), leftWoodcutter.getPosition());
    }

    @Test
    public void testCourierPicksUpNewCargoAtOtherFlagAfterDelivery() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point middlePoint = new Point(8, 4);
        Point flagPoint = new Point(10, 4);
        Flag middleFlag = map.placeFlag(flagPoint);

        Point leftFlag = new Point(6, 4);
        
        Point point3 = new Point(12, 4);
        Point point4 = new Point(13, 5);
        Building rightWoodcutter = map.placeBuilding(new Woodcutter(), point4.upLeft());
        Building leftWoodcutter = map.placeBuilding(new Woodcutter(), leftFlag.upLeft());
        
        Utils.constructSmallHouse(rightWoodcutter);
        
        Road road0 = map.placeRoad(leftFlag, middlePoint, flagPoint);
        Road road1 = map.placeRoad(flagPoint, point3, point4);
        
        /* Place cargo at flag0 */
        Cargo cargoForRightWoodcutter = new Cargo(WOOD, map);
        leftWoodcutter.getFlag().putCargo(cargoForRightWoodcutter);
        cargoForRightWoodcutter.setTarget(rightWoodcutter);
        
        /* Place courier at same flag as cargo */
        Courier courier = new Courier(map);
        map.placeWorker(courier, leftWoodcutter.getFlag());

        courier.assignToRoad(road0);

        /* Courier will walk to idle point at the road */
        assertTrue(courier.isWalkingToRoad());
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getPosition(), middlePoint);
        assertTrue(courier.isArrived());
        assertTrue(courier.isIdle());
        
        /* Courier detects the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), leftWoodcutter.getFlag().getPosition());
        assertTrue(courier.isTraveling());
        assertFalse(leftWoodcutter.getFlag().getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        assertTrue(courier.isAt(leftWoodcutter.getFlag().getPosition()));
                
        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(courier.getCargo(), cargoForRightWoodcutter);
        assertEquals(courier.getTarget(), middleFlag.getPosition());
        assertTrue(leftWoodcutter.getFlag().getStackedCargo().isEmpty());
        assertFalse(courier.isIdle());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, middleFlag.getPosition());
        
        assertEquals(middleFlag.getStackedCargo().get(0), cargoForRightWoodcutter);
        assertEquals(courier.getCargo(), null);
        assertFalse(courier.isIdle());
        assertEquals(courier.getTarget(), middlePoint);
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertTrue(courier.isIdle());
        assertEquals(courier.getPosition(), middlePoint);
    }

    @Test
    public void testCourierDeliversToBuilding() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point middlePoint = new Point(8, 4);
        Point rightFlagPoint = new Point(10, 4);
        Flag rightFlag = map.placeFlag(rightFlagPoint);

        Point leftFlagPoint = new Point(6, 4);
        
        Building wc = map.placeBuilding(new Woodcutter(), leftFlagPoint.upLeft());
        
        Road road0 = map.placeRoad(leftFlagPoint, middlePoint, rightFlagPoint);
        
        /* Place cargo at flag0 */
        Cargo cargoForRightWoodcutter = new Cargo(Material.PLANCK, map);
        rightFlag.putCargo(cargoForRightWoodcutter);
        cargoForRightWoodcutter.setTarget(wc);
        
        /* Place courier at same flag as cargo */
        Courier courier = new Courier(map);
        map.placeWorker(courier, wc.getFlag());

        courier.assignToRoad(road0);

        /* Courier will walk to idle point at the road */
        assertTrue(courier.isWalkingToRoad());
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getPosition(), middlePoint);
        assertTrue(courier.isArrived());
        assertTrue(courier.isIdle());
        
        /* Courier detects the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), rightFlagPoint);
        assertTrue(courier.isTraveling());
        assertFalse(rightFlag.getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        assertTrue(courier.isAt(rightFlagPoint));
                
        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(courier.getCargo(), cargoForRightWoodcutter);
        assertEquals(courier.getTarget(), wc.getPosition());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        assertFalse(courier.isIdle());
        assertTrue(wc.getAmount(PLANCK) == 0);
        
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, leftFlagPoint);
        
        /* Verify that courier does not deliver the cargo to the flag */
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        assertNotNull(courier.getCargo());
        assertEquals(courier.getTarget(), wc.getPosition());
        assertTrue(wc.getAmount(PLANCK) == 0);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, wc.getPosition());

        assertNull(courier.getCargo());
        assertFalse(courier.isIdle());
        assertEquals(courier.getPosition(), wc.getPosition());
        assertTrue(wc.getAmount(PLANCK) == 1);
    }

    @Test
    public void testCourierGoesBackToIdlePointAfterDeliveryToBuilding() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point middlePoint = new Point(8, 4);
        Point rightFlagPoint = new Point(10, 4);
        Flag rightFlag = map.placeFlag(rightFlagPoint);

        Point leftFlagPoint = new Point(6, 4);
        
        Building wc = map.placeBuilding(new Woodcutter(), leftFlagPoint.upLeft());
        
        Road road0 = map.placeRoad(leftFlagPoint, middlePoint, rightFlagPoint);
        
        /* Place cargo at flag0 */
        Cargo cargoForRightWoodcutter = new Cargo(Material.PLANCK, map);
        rightFlag.putCargo(cargoForRightWoodcutter);
        cargoForRightWoodcutter.setTarget(wc);
        
        /* Place courier at same flag as cargo */
        Courier courier = new Courier(map);
        map.placeWorker(courier, wc.getFlag());

        courier.assignToRoad(road0);

        /* Courier will walk to idle point at the road */
        assertTrue(courier.isWalkingToRoad());
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getPosition(), middlePoint);
        assertTrue(courier.isArrived());
        assertTrue(courier.isIdle());
        
        /* Courier detects the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), rightFlagPoint);
        assertTrue(courier.isTraveling());
        assertFalse(rightFlag.getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        assertTrue(courier.isAt(rightFlagPoint));
                
        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(courier.getCargo(), cargoForRightWoodcutter);
        assertEquals(courier.getTarget(), wc.getPosition());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        assertFalse(courier.isIdle());
        assertTrue(wc.getAmount(PLANCK) == 0);
        
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, leftFlagPoint);
        
        /* Verify that courier does not deliver the cargo to the flag */
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        assertNotNull(courier.getCargo());
        assertEquals(courier.getTarget(), wc.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, wc.getPosition());

        assertFalse(courier.isIdle());
        assertEquals(courier.getPosition(), wc.getPosition());
        assertEquals(courier.getTarget(), wc.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, wc.getFlag().getPosition());
        
        assertEquals(courier.getPosition(), wc.getFlag().getPosition());
        assertFalse(courier.isIdle());
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getPosition(), middlePoint);
        assertTrue(courier.isIdle());
    }

    @Test
    public void testCourierDeliversToBuildingWhenItIsAlreadyAtFlagAndPicksUpCargo() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point middlePoint = new Point(8, 4);
        Point middleFlagPoint = new Point(10, 4);
        Flag middleFlag = map.placeFlag(middleFlagPoint);
        
        Point rightFlagPoint = new Point(14, 4);
        Flag rightFlag = map.placeFlag(rightFlagPoint);

        Point wcFlagPoint = new Point(6, 4);
        
        Building wc = map.placeBuilding(new Woodcutter(), wcFlagPoint.upLeft());
        
        Road road0 = map.placeRoad(wcFlagPoint, middlePoint, middleFlagPoint);
        Road road1 = map.placeRoad(middleFlagPoint, middleFlagPoint.right(), rightFlagPoint);
        
        Building quarry = map.placeBuilding(new Quarry(), rightFlagPoint.upLeft());
                
        /* Place cargo at the woodcutter's flag */
        Cargo cargoForQuarry = new Cargo(PLANCK, map);
        wc.getFlag().putCargo(cargoForQuarry);
        cargoForQuarry.setTarget(quarry);
        
        /* Place courier at middle flag */
        Courier courier = new Courier(map);
        map.placeWorker(courier, middleFlag);

        courier.assignToRoad(road0);

        /* Courier will walk to idle point at the road */
        assertTrue(courier.isWalkingToRoad());
        assertEquals(courier.getTarget(), middlePoint);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, middlePoint);
        
        assertEquals(courier.getPosition(), middlePoint);
        assertTrue(courier.isArrived());
        assertTrue(courier.isIdle());
        
        /* Courier detects the cargo at the woodcutter */
        map.stepTime();
        
        assertEquals(courier.getTarget(), wc.getFlag().getPosition());
        assertTrue(courier.isTraveling());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        assertTrue(courier.isAt(wc.getFlag().getPosition()));
                
        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(courier.getCargo(), cargoForQuarry);
        assertEquals(courier.getTarget(), middleFlagPoint);
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        assertFalse(courier.isIdle());
        
        /* Put the other cargo at the middle flag with the woodcutter as its target */
        Cargo cargoForWoodcutter = new Cargo(PLANCK, map);
        middleFlag.putCargo(cargoForWoodcutter);
        cargoForWoodcutter.setTarget(wc);

        /* Let the courier reach the middle flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, middleFlagPoint);
        
        /* Verify that courier puts down the cargo and picks up the new cargo */
        assertFalse(middleFlag.getStackedCargo().isEmpty());
        assertEquals(courier.getCargo(), cargoForWoodcutter);
        assertEquals(courier.getTarget(), wc.getPosition());
        assertTrue(wc.getAmount(PLANCK) == 0);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, wc.getPosition());

        assertNull(courier.getCargo());
        assertFalse(courier.isIdle());
        assertEquals(courier.getPosition(), wc.getPosition());
        assertTrue(wc.getAmount(PLANCK) == 1);
        
    }

    @Test
    public void testCouriersStopCarryingThingsAtSplittingRoads() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* 133 ticks from start */
        Utils.fastForward(133, map);

        /* Placing forester */
        Point point22 = new Point(22, 4);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(), point22);

        /* 147 ticks from start */
        Utils.fastForward(14, map);

        /* Placing woodcutter */
        Point point23 = new Point(19, 5);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(), point23);

        /* 185 ticks from start */
        Utils.fastForward(38, map);

        /* Placing quarry */
        Point point24 = new Point(10, 12);
        Building quarry0 = map.placeBuilding(new Quarry(), point24);

        /* 206 ticks from start */
        Utils.fastForward(21, map);

        /* Placing road between (23, 3) and (20, 4) */
        Point point25 = new Point(23, 3);
        Point point26 = new Point(21, 3);
        Point point27 = new Point(20, 4);
        Road road0 = map.placeRoad(point25, point26, point27);

        /* 227 ticks from start */
        Utils.fastForward(21, map);

        /* Placing road between (20, 4) and (11, 11) */
        Point point28 = new Point(18, 4);
        Point point29 = new Point(17, 5);
        Point point30 = new Point(16, 6);
        Point point31 = new Point(15, 7);
        Point point32 = new Point(14, 8);
        Point point33 = new Point(13, 9);
        Point point34 = new Point(12, 10);
        Point point35 = new Point(11, 11);
        Road road1 = map.placeRoad(point27, point28, point29, point30, point31, point32, point33, point34, point35);

        /* 254 ticks from start */
        Utils.fastForward(27, map);

        /* Placing road between (11, 11) and (6, 4) */
        Point point36 = new Point(10, 10);
        Point point37 = new Point(9, 9);
        Point point38 = new Point(8, 8);
        Point point39 = new Point(7, 7);
        Point point40 = new Point(8, 6);
        Point point41 = new Point(7, 5);
        Point point42 = new Point(6, 4);
        Road road2 = map.placeRoad(point35, point36, point37, point38, point39, point40, point41, point42);

        /* 269 ticks from start */
        Utils.fastForward(15, map);

        /* Placing flag */
        Flag flag0 = map.placeFlag(point40);

        /* 282 ticks from start */
        Utils.fastForward(13, map);

        /* Placing flag */
        Flag flag1 = map.placeFlag(point38);

        /* 297 ticks from start */
        Utils.fastForward(15, map);

        /* Placing flag */
        Flag flag2 = map.placeFlag(point33);

        /* 311 ticks from start */
        Utils.fastForward(14, map);

        /* Placing flag */
        Flag flag3 = map.placeFlag(point31);

        /* 329 ticks from start */
        Utils.fastForward(18, map);

        /* Placing flag */
        Flag flag4 = map.placeFlag(point29);

        /* Wait for all couriers to become idle */
        for (int i = 0; i < 2000; i++) {
            boolean allIdle = true;
            
            for (Worker w : map.getAllWorkers()) {
                if (w instanceof Courier && w.isTraveling()) {
                    allIdle = false;
                }
            }
            
            if (allIdle) {
                break;
            }
            
            map.stepTime();
        }

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Courier) {
                Courier c = (Courier)w;
                
                assertFalse(c.isTraveling());
                assertFalse(c.isWalkingToRoad());
            }
        }

    }
}
