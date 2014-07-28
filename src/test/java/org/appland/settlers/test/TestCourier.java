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
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Material;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Woodcutter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestCourier {
    
    @Test
    public void testCourierWalksToIntendedRoad() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(8, 4);
        Flag flag0 = map.placeFlag(point0);

        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(point1);

        Point point2 = new Point(6, 4);
        Flag flag2 = map.placeFlag(point2);
        
        Road road0 = map.placeRoad(point2, point0);
        Road road1 = map.placeRoad(point0, point1);

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
        Point middle = new Point(8, 4);
        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(point1);

        Point point2 = new Point(6, 4);
        Flag flag0 = map.placeFlag(point2);
        
        Point point3 = new Point(12, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point3.upLeft());
        
        Utils.constructSmallHouse(wc);
        
        Road road0 = map.placeRoad(point2, middle, point1);
        Road road1 = map.placeRoad(point1, point3);
        
        Cargo cargo = new Cargo(WOOD);
        flag0.putCargo(cargo);
        cargo.setTarget(wc, map);
        
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
        Point middle = new Point(8, 4);
        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(point1);

        Point point2 = new Point(6, 4);
        Flag flag0 = map.placeFlag(point2);
        
        Point point3 = new Point(12, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point3.upLeft());
        
        Utils.constructSmallHouse(wc);
        
        Road road0 = map.placeRoad(point2, middle, point1);
        Road road1 = map.placeRoad(point1, point3);
        
        /* Place cargo at flag0 */
        Cargo cargo = new Cargo(WOOD);
        flag0.putCargo(cargo);
        cargo.setTarget(wc, map);
        
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
        Point middle = new Point(8, 4);
        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(point1);

        Point point2 = new Point(6, 4);
        Flag flag0 = map.placeFlag(point2);
        
        Point point3 = new Point(12, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point3.upLeft());
        
        Utils.constructSmallHouse(wc);
        
        Road road0 = map.placeRoad(point2, middle, point1);
        Road road1 = map.placeRoad(point1, point3);
        
        /* Place cargo at flag0 */
        Cargo cargo = new Cargo(WOOD);
        flag0.putCargo(cargo);
        cargo.setTarget(wc, map);
        
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
        Point middlePoint = new Point(8, 4);
        Point flagPoint = new Point(10, 4);
        Flag middleFlag = map.placeFlag(flagPoint);

        Point leftFlag = new Point(6, 4);
        
        Point point3 = new Point(12, 4);
        Building rightWoodcutter = map.placeBuilding(new Woodcutter(), point3.upLeft());
        Building leftWoodcutter = map.placeBuilding(new Woodcutter(), leftFlag.upLeft());
        
        Utils.constructSmallHouse(rightWoodcutter);
        
        Road road0 = map.placeRoad(leftFlag, middlePoint, flagPoint);
        Road road1 = map.placeRoad(flagPoint, point3);
        
        /* Place cargo at flag0 */
        Cargo cargoForRightWoodcutter = new Cargo(WOOD);
        leftWoodcutter.getFlag().putCargo(cargoForRightWoodcutter);
        cargoForRightWoodcutter.setTarget(rightWoodcutter, map);
        
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
        Cargo cargoForLeftWoodcutter = new Cargo(STONE);
        
        cargoForLeftWoodcutter.setPosition(middleFlag.getPosition());
        middleFlag.putCargo(cargoForLeftWoodcutter);
        cargoForLeftWoodcutter.setTarget(leftWoodcutter, map);
        
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
        assertEquals(courier.getCargo(), cargoForLeftWoodcutter);
        assertFalse(courier.isIdle());
        
        /* After delivering the cargo, the courier picks up the other cargo without going back to the middle */
        assertEquals(courier.getTarget(), leftWoodcutter.getFlag().getPosition());
    }

    @Test
    public void testCourierPicksUpNewCargoAtOtherFlagAfterDelivery() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point middlePoint = new Point(8, 4);
        Point flagPoint = new Point(10, 4);
        Flag middleFlag = map.placeFlag(flagPoint);

        Point leftFlag = new Point(6, 4);
        
        Point point3 = new Point(12, 4);
        Building rightWoodcutter = map.placeBuilding(new Woodcutter(), point3.upLeft());
        Building leftWoodcutter = map.placeBuilding(new Woodcutter(), leftFlag.upLeft());
        
        Utils.constructSmallHouse(rightWoodcutter);
        
        Road road0 = map.placeRoad(leftFlag, middlePoint, flagPoint);
        Road road1 = map.placeRoad(flagPoint, point3);
        
        /* Place cargo at flag0 */
        Cargo cargoForRightWoodcutter = new Cargo(WOOD);
        leftWoodcutter.getFlag().putCargo(cargoForRightWoodcutter);
        cargoForRightWoodcutter.setTarget(rightWoodcutter, map);
        
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
        Point middlePoint = new Point(8, 4);
        Point rightFlagPoint = new Point(10, 4);
        Flag rightFlag = map.placeFlag(rightFlagPoint);

        Point leftFlagPoint = new Point(6, 4);
        
        Building wc = map.placeBuilding(new Woodcutter(), leftFlagPoint.upLeft());
        
        Road road0 = map.placeRoad(leftFlagPoint, middlePoint, rightFlagPoint);
        
        /* Place cargo at flag0 */
        Cargo cargoForRightWoodcutter = new Cargo(Material.PLANCK);
        rightFlag.putCargo(cargoForRightWoodcutter);
        cargoForRightWoodcutter.setTarget(wc, map);
        
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
        assertEquals(courier.getTarget(), wc.getFlag().getPosition());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        assertFalse(courier.isIdle());
        assertTrue(wc.getInQueue().get(Material.PLANCK) == 0);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, wc.getFlag().getPosition());
        
        assertNull(courier.getCargo());
        assertFalse(courier.isIdle());
        assertEquals(courier.getTarget(), middlePoint);
        assertTrue(wc.getInQueue().get(Material.PLANCK) == 1);
    }
}
