/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Donkey;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
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
public class TestDonkey {

    @Test
    public void testDonkeyIsDispatchedToMainRoad() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point38 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point38);
        
        /* Place flag */
        Point point2 = new Point(5, 9);
        Flag flag0 = map.placeFlag(point2);
        
        /* Place flag */
        Point point3 = new Point(5, 13);
        Flag flag1 = map.placeFlag(point3);
        
        /* Place road between the headquarter and the first flag */
        Road road0 = map.placeAutoSelectedRoad(flag0, headquarter0.getFlag());
    
        /* Place road between the headquarter and the second flag */
        Road road1 = map.placeAutoSelectedRoad(flag0, flag1);
        
        /* Place workers on the roads */
        Courier courier0 = Utils.occupyRoad(new Courier(map), road0, map);
        Courier courier1 = Utils.occupyRoad(new Courier(map), road1, map);
    
        /* Deliver 99 cargos and verify that the road does not become a main road */
        for (int i = 0; i < 99; i++) {
            Cargo cargo = new Cargo(COIN, map);
 
            flag1.putCargo(cargo);

            cargo.setTarget(headquarter0);
            
            /* Wait for the courier to pick up the cargo */
            assertNull(courier1.getCargo());
            
            Utils.fastForwardUntilWorkerCarriesCargo(map, courier1, cargo);
            
            /* Wait for the courier to deliver the cargo */
            assertEquals(courier1.getTarget(), flag0.getPosition());
            
            Utils.fastForwardUntilWorkerReachesPoint(map, courier1, flag0.getPosition());
            
            assertNull(courier1.getCargo());
            
            assertFalse(road1.isMainRoad());
        }
    
        /* Deliver one more cargo and verify that the road becomes a main road */    
        Cargo cargo = new Cargo(COIN, map);
 
        flag1.putCargo(cargo);
 
        cargo.setTarget(headquarter0);

        /* Wait for the courier to pick up the cargo */
        assertNull(courier1.getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier1, cargo);

        /* Wait for the road to become a main road and verify that a donkey 
           gets dispatched from the headquarter */
        assertEquals(courier1.getTarget(), flag0.getPosition());
        assertNull(road1.getDonkey());
        
        int amount = map.getAllWorkers().size();

        Utils.fastForwardUntilWorkerReachesPoint(map, courier1, flag0.getPosition());

        assertTrue(road1.isMainRoad());

        map.stepTime();

        assertEquals(map.getAllWorkers().size(), amount + 1);
        assertNotNull(road1.getDonkey());
        assertFalse(road1.needsDonkey());
    }
    
    @Test
    public void testDonkeyWalksToIntendedRoad() throws Exception {
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

        Donkey donkey = new Donkey(map);
        map.placeWorker(donkey, flag2);

        donkey.assignToRoad(road1);
        
        Utils.fastForwardUntilWorkersReachTarget(map, donkey);
        
        assertEquals(donkey.getAssignedRoad(), road1);
        assertTrue(donkey.isIdle());
    }

    @Test
    public void testDonkeyGoesToMiddlePointOfRoad() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point0 = new Point(8, 4);
        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(point1);

        Point point2 = new Point(6, 4);
        Flag flag2 = map.placeFlag(point2);
        
        Road road0 = map.placeRoad(point2, point0, point1);

        Donkey donkey = new Donkey(map);
        map.placeWorker(donkey, flag2);

        donkey.assignToRoad(road0);        
        
        assertTrue(donkey.isWalkingToRoad());
        assertFalse(donkey.isIdle());
        
        Utils.fastForwardUntilWorkersReachTarget(map, donkey);
        
        assertEquals(donkey.getPosition(), point0);
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());
        assertFalse(donkey.isWalkingToIdlePoint());
    }

    @Test
    public void testDonkeyIsIdleWhenMiddlePointIsReached() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point0 = new Point(8, 4);
        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(point1);

        Point point2 = new Point(6, 4);
        Flag flag2 = map.placeFlag(point2);
        
        Road road0 = map.placeRoad(point2, point0, point1);

        Donkey donkey = new Donkey(map);
        map.placeWorker(donkey, flag2);

        donkey.assignToRoad(road0);
        
        assertTrue(donkey.isWalkingToRoad());
        assertFalse(donkey.isIdle());
        
        Utils.fastForwardUntilWorkersReachTarget(map, donkey);
        
        assertEquals(donkey.getPosition(), point0);
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());
    }
    
    @Test
    public void testDonkeyRemainsIdleWhenThereIsNoCargo() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point0 = new Point(8, 4);
        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(point1);

        Point point2 = new Point(6, 4);
        Flag flag2 = map.placeFlag(point2);
        
        Road road0 = map.placeRoad(point2, point0, point1);

        Donkey donkey = new Donkey(map);
        map.placeWorker(donkey, flag2);

        donkey.assignToRoad(road0);
        
        Utils.fastForwardUntilWorkersReachTarget(map, donkey);
        
        assertEquals(donkey.getPosition(), point0);
        assertTrue(donkey.isArrived());

        int i;
        for (i = 0; i < 200; i++) {
            assertTrue(donkey.isArrived());
            assertTrue(donkey.isIdle());
            
            assertEquals(donkey.getPosition(), point0);

            assertFalse(flag1.hasCargoWaitingForRoad(road0));
            assertFalse(flag2.hasCargoWaitingForRoad(road0));
            assertFalse(donkey.isWalkingToIdlePoint());
            
            map.stepTime();
        }
    }

    @Test
    public void testDonkeyWalksToMiddleOfRoadWhenItIsAssignedEvenIfFlagsHaveCargo() throws Exception {
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
        
        Utils.constructHouse(wc, map);
        
        Road road0 = map.placeRoad(point2, middle, point1);
        Road road1 = map.placeRoad(point1, point3, point4);
        
        Cargo cargo = new Cargo(WOOD, map);
        flag0.putCargo(cargo);
        cargo.setTarget(wc);
        
        Donkey donkey = new Donkey(map);
        map.placeWorker(donkey, flag0);

        donkey.assignToRoad(road0);
        
        assertTrue(flag0.hasCargoWaitingForRoad(road0));
        assertTrue(donkey.isWalkingToRoad());
        
        assertFalse(donkey.isIdle());
        
        Utils.fastForwardUntilWorkersReachTarget(map, donkey);
        
        assertEquals(donkey.getPosition(), middle);
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());
        
        assertFalse(donkey.isWalkingToIdlePoint());
    }
    
    @Test
    public void testDonkeyPicksUpCargoFromFlag() throws Exception {
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

        Utils.constructHouse(wc, map);

        Road road0 = map.placeRoad(point2, middle, point1);
        Road road1 = map.placeRoad(point1, point3, point4);
        
        /* Place cargo at flag0 */
        Cargo cargo = new Cargo(WOOD, map);
        flag0.putCargo(cargo);
        cargo.setTarget(wc);
        
        /* Place donkey at same flag as cargo */
        Donkey donkey = new Donkey(map);
        map.placeWorker(donkey, flag0);

        donkey.assignToRoad(road0);

        /* Donkey will walk to idle point at the road */
        assertTrue(donkey.isWalkingToRoad());
        
        Utils.fastForwardUntilWorkersReachTarget(map, donkey);
        
        assertEquals(donkey.getPosition(), middle);

        assertFalse(flag0.getStackedCargo().isEmpty());

        assertTrue(donkey.isArrived());
        assertTrue(flag0.hasCargoWaitingForRoad(road0));
        assertTrue(donkey.isIdle());
        
        /* Donkey detects the cargo */
        map.stepTime();
        
        assertEquals(donkey.getTarget(), flag0.getPosition());
        assertTrue(donkey.isTraveling());
        assertFalse(flag0.getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());
        
        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
        */
        assertEquals(donkey.getCargo(), cargo);
        assertEquals(donkey.getTarget(), flag1.getPosition());
        assertTrue(flag0.getStackedCargo().isEmpty());
    }
    
    @Test
    public void testDonkeyDeliversCargoAndBecomesIdle() throws Exception {
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
        
        Utils.constructHouse(wc, map);
        
        Road road0 = map.placeRoad(point2, middle, point1);
        Road road1 = map.placeRoad(point1, point3, point4);
        
        /* Place cargo at flag0 */
        Cargo cargo = new Cargo(WOOD, map);
        flag0.putCargo(cargo);
        cargo.setTarget(wc);
        
        /* Place donkey at same flag as cargo */
        Donkey donkey = new Donkey(map);
        map.placeWorker(donkey, flag0);

        donkey.assignToRoad(road0);

        /* Donkey will walk to idle point at the road */
        assertTrue(donkey.isWalkingToRoad());
        
        Utils.fastForwardUntilWorkersReachTarget(map, donkey);
        
        assertEquals(donkey.getPosition(), middle);

        assertFalse(flag0.getStackedCargo().isEmpty());

        assertTrue(donkey.isArrived());
        assertTrue(flag0.hasCargoWaitingForRoad(road0));
        assertTrue(donkey.isIdle());
        
        /* Donkey detects the cargo */
        map.stepTime();
        
        assertEquals(donkey.getTarget(), flag0.getPosition());
        assertTrue(donkey.isTraveling());
        assertFalse(flag0.getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());
        
        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(donkey.getCargo(), cargo);
        assertEquals(donkey.getTarget(), flag1.getPosition());
        assertTrue(flag0.getStackedCargo().isEmpty());
        assertFalse(donkey.isIdle());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());
        
        assertNull(donkey.getCargo());
        assertFalse(flag1.getStackedCargo().isEmpty());
        assertFalse(donkey.isIdle());
        assertEquals(flag1.getStackedCargo().get(0), cargo);
        
        /* After delivering the cargo, the donkey goes back to the idle spot */
        assertFalse(donkey.isIdle());
        assertFalse(donkey.getTarget().equals(donkey.getPosition()));
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());
        
        assertTrue(donkey.isIdle());
    }
    
    @Test
    public void testDonkeyPicksUpNewCargoAtSameFlagAfterDelivery() throws Exception {
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
        
        Utils.constructHouse(rightWoodcutter, map);
        
        Road road0 = map.placeRoad(leftFlagPoint, middlePoint, middleFlagPoint);
        Road road1 = map.placeRoad(middleFlagPoint, point3, rightFlagPoint);
        
        /* Place cargo at flag0 */
        Cargo cargoForRightWoodcutter = new Cargo(WOOD, map);
        leftWoodcutter.getFlag().putCargo(cargoForRightWoodcutter);
        cargoForRightWoodcutter.setTarget(rightWoodcutter);
        
        /* Place donkey at same flag as cargo */
        Donkey donkey = new Donkey(map);
        map.placeWorker(donkey, leftWoodcutter.getFlag());

        donkey.assignToRoad(road0);

        /* Donkey will walk to idle point at the road */
        assertTrue(donkey.isWalkingToRoad());
        
        Utils.fastForwardUntilWorkersReachTarget(map, donkey);
        
        assertEquals(donkey.getPosition(), middlePoint);
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());
        
        /* Donkey detects the cargo */
        map.stepTime();
        
        assertEquals(donkey.getTarget(), leftWoodcutter.getFlag().getPosition());
        assertTrue(donkey.isTraveling());
        assertFalse(leftWoodcutter.getFlag().getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());
        
        assertTrue(donkey.isAt(leftWoodcutter.getFlag().getPosition()));
        
        /* Place cargo at other flag for donkey to discover after delivery */
        Cargo cargoForLeftWoodcutter = new Cargo(STONE, map);
        
        cargoForLeftWoodcutter.setPosition(middleFlagPoint);
        middleFlag.putCargo(cargoForLeftWoodcutter);
        cargoForLeftWoodcutter.setTarget(leftWoodcutter);
        
        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(donkey.getCargo(), cargoForRightWoodcutter);
        assertEquals(donkey.getTarget(), middleFlagPoint);
        assertTrue(leftWoodcutter.getFlag().getStackedCargo().isEmpty());
        assertFalse(donkey.isIdle());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, middleFlagPoint);
        
        assertEquals(middleFlag.getStackedCargo().get(0), cargoForRightWoodcutter);
        assertEquals(donkey.getCargo(), cargoForLeftWoodcutter);
        assertFalse(donkey.isIdle());
        
        /* After delivering the cargo, the donkey picks up the other cargo without going back to the middle */
        assertEquals(donkey.getTarget(), leftWoodcutter.getPosition());
    }

    @Test
    public void testDonkeyPicksUpNewCargoAtOtherFlagAfterDelivery() throws Exception {
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
        
        Utils.constructHouse(rightWoodcutter, map);
        
        Road road0 = map.placeRoad(leftFlag, middlePoint, flagPoint);
        Road road1 = map.placeRoad(flagPoint, point3, point4);
        
        /* Place cargo at flag0 */
        Cargo cargoForRightWoodcutter = new Cargo(WOOD, map);
        leftWoodcutter.getFlag().putCargo(cargoForRightWoodcutter);
        cargoForRightWoodcutter.setTarget(rightWoodcutter);
        
        /* Place donkey at same flag as cargo */
        Donkey donkey = new Donkey(map);
        map.placeWorker(donkey, leftWoodcutter.getFlag());

        donkey.assignToRoad(road0);

        /* Donkey will walk to idle point at the road */
        assertTrue(donkey.isWalkingToRoad());
        
        Utils.fastForwardUntilWorkersReachTarget(map, donkey);
        
        assertEquals(donkey.getPosition(), middlePoint);
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());
        
        /* Donkey detects the cargo */
        map.stepTime();
        
        assertEquals(donkey.getTarget(), leftWoodcutter.getFlag().getPosition());
        assertTrue(donkey.isTraveling());
        assertFalse(leftWoodcutter.getFlag().getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());
        
        assertTrue(donkey.isAt(leftWoodcutter.getFlag().getPosition()));
                
        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(donkey.getCargo(), cargoForRightWoodcutter);
        assertEquals(donkey.getTarget(), middleFlag.getPosition());
        assertTrue(leftWoodcutter.getFlag().getStackedCargo().isEmpty());
        assertFalse(donkey.isIdle());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, middleFlag.getPosition());
        
        assertEquals(middleFlag.getStackedCargo().get(0), cargoForRightWoodcutter);
        assertEquals(donkey.getCargo(), null);
        assertFalse(donkey.isIdle());
        assertEquals(donkey.getTarget(), middlePoint);
        
        Utils.fastForwardUntilWorkersReachTarget(map, donkey);
        
        assertTrue(donkey.isIdle());
        assertEquals(donkey.getPosition(), middlePoint);
    }

    @Test
    public void testDonkeyDeliversToBuilding() throws Exception {
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
        Cargo cargoForRightWoodcutter = new Cargo(PLANCK, map);
        rightFlag.putCargo(cargoForRightWoodcutter);
        cargoForRightWoodcutter.setTarget(wc);
        
        /* Place donkey at same flag as cargo */
        Donkey donkey = new Donkey(map);
        map.placeWorker(donkey, wc.getFlag());

        donkey.assignToRoad(road0);

        /* Donkey will walk to idle point at the road */
        assertTrue(donkey.isWalkingToRoad());
        
        Utils.fastForwardUntilWorkersReachTarget(map, donkey);
        
        assertEquals(donkey.getPosition(), middlePoint);
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());
        
        /* Donkey detects the cargo */
        map.stepTime();
        
        assertEquals(donkey.getTarget(), rightFlagPoint);
        assertTrue(donkey.isTraveling());
        assertFalse(rightFlag.getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());
        
        assertTrue(donkey.isAt(rightFlagPoint));
                
        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(donkey.getCargo(), cargoForRightWoodcutter);
        assertEquals(donkey.getTarget(), wc.getPosition());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        assertFalse(donkey.isIdle());
        assertTrue(wc.getAmount(PLANCK) == 0);
        
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, leftFlagPoint);
        
        /* Verify that donkey does not deliver the cargo to the flag */
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        assertNotNull(donkey.getCargo());
        assertEquals(donkey.getTarget(), wc.getPosition());
        assertTrue(wc.getAmount(PLANCK) == 0);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, wc.getPosition());

        assertNull(donkey.getCargo());
        assertFalse(donkey.isIdle());
        assertEquals(donkey.getPosition(), wc.getPosition());
        assertTrue(wc.getAmount(PLANCK) == 1);
    }

    @Test
    public void testDonkeyGoesBackToIdlePointAfterDeliveryToBuilding() throws Exception {
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
        Cargo cargoForRightWoodcutter = new Cargo(PLANCK, map);
        rightFlag.putCargo(cargoForRightWoodcutter);
        cargoForRightWoodcutter.setTarget(wc);
        
        /* Place donkey at same flag as cargo */
        Donkey donkey = new Donkey(map);
        map.placeWorker(donkey, wc.getFlag());

        donkey.assignToRoad(road0);

        /* Donkey will walk to idle point at the road */
        assertTrue(donkey.isWalkingToRoad());
        
        Utils.fastForwardUntilWorkersReachTarget(map, donkey);
        
        assertEquals(donkey.getPosition(), middlePoint);
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());
        
        /* Donkey detects the cargo */
        map.stepTime();
        
        assertEquals(donkey.getTarget(), rightFlagPoint);
        assertTrue(donkey.isTraveling());
        assertFalse(rightFlag.getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());
        
        assertTrue(donkey.isAt(rightFlagPoint));
                
        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(donkey.getCargo(), cargoForRightWoodcutter);
        assertEquals(donkey.getTarget(), wc.getPosition());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        assertFalse(donkey.isIdle());
        assertTrue(wc.getAmount(PLANCK) == 0);
        
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, leftFlagPoint);
        
        /* Verify that donkey does not deliver the cargo to the flag */
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        assertNotNull(donkey.getCargo());
        assertEquals(donkey.getTarget(), wc.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, wc.getPosition());

        assertFalse(donkey.isIdle());
        assertEquals(donkey.getPosition(), wc.getPosition());
        assertEquals(donkey.getTarget(), wc.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, wc.getFlag().getPosition());
        
        assertEquals(donkey.getPosition(), wc.getFlag().getPosition());
        assertFalse(donkey.isIdle());
        
        Utils.fastForwardUntilWorkersReachTarget(map, donkey);
        
        assertEquals(donkey.getPosition(), middlePoint);
        assertTrue(donkey.isIdle());
    }

    @Test
    public void testDonkeyDeliversToBuildingWhenItIsAlreadyAtFlagAndPicksUpCargo() throws Exception {
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
        
        /* Place donkey at middle flag */
        Donkey donkey = new Donkey(map);
        map.placeWorker(donkey, middleFlag);

        donkey.assignToRoad(road0);

        /* Donkey will walk to idle point at the road */
        assertTrue(donkey.isWalkingToRoad());
        assertEquals(donkey.getTarget(), middlePoint);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, middlePoint);
        
        assertEquals(donkey.getPosition(), middlePoint);
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());
        
        /* Donkey detects the cargo at the woodcutter */
        map.stepTime();
        
        assertEquals(donkey.getTarget(), wc.getFlag().getPosition());
        assertTrue(donkey.isTraveling());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());
        
        assertTrue(donkey.isAt(wc.getFlag().getPosition()));
                
        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(donkey.getCargo(), cargoForQuarry);
        assertEquals(donkey.getTarget(), middleFlagPoint);
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        assertFalse(donkey.isIdle());
        
        /* Put the other cargo at the middle flag with the woodcutter as its target */
        Cargo cargoForWoodcutter = new Cargo(PLANCK, map);
        middleFlag.putCargo(cargoForWoodcutter);
        cargoForWoodcutter.setTarget(wc);

        /* Let the donkey reach the middle flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, middleFlagPoint);
        
        /* Verify that donkey puts down the cargo and picks up the new cargo */
        assertFalse(middleFlag.getStackedCargo().isEmpty());
        assertEquals(donkey.getCargo(), cargoForWoodcutter);
        assertEquals(donkey.getTarget(), wc.getPosition());
        assertTrue(wc.getAmount(PLANCK) == 0);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, wc.getPosition());

        assertNull(donkey.getCargo());
        assertFalse(donkey.isIdle());
        assertEquals(donkey.getPosition(), wc.getPosition());
        assertTrue(wc.getAmount(PLANCK) == 1);
        
    }

    @Test
    public void testDonkeysStopCarryingThingsAtSplittingRoads() throws Exception {

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

        /* Wait for all donkeys to become idle */
        for (int i = 0; i < 2000; i++) {
            boolean allIdle = true;
            
            for (Worker w : map.getAllWorkers()) {
                if (w instanceof Donkey && w.isTraveling()) {
                    allIdle = false;
                }
            }
            
            if (allIdle) {
                break;
            }
            
            map.stepTime();
        }

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Donkey) {
                Donkey c = (Donkey)w;
                
                assertFalse(c.isTraveling());
                assertFalse(c.isWalkingToRoad());
            }
        }

    }
}
