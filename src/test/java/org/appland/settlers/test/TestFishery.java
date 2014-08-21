/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import static org.appland.settlers.model.Building.ConstructionState.UNDER_CONSTRUCTION;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Fisherman;
import org.appland.settlers.model.Fishery;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidStateForProduction;
import static org.appland.settlers.model.Material.FISH;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import static org.appland.settlers.test.Utils.constructSmallHouse;
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
public class TestFishery {

    @Test
    public void testConstructFisherman() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        GameMap map = new GameMap(20, 20);
        
        Fishery f = new Fishery();

        assertTrue(f.getConstructionState() == Building.ConstructionState.UNDER_CONSTRUCTION);

        assertFalse(f.needsWorker());

        Utils.constructSmallHouse(f);

        /* Verify that the fishery is unoccupied when it's newly constructed */
        assertTrue(f.needsWorker());

        /* Verify that the Fishery requires a worker */
        assertTrue(f.needsWorker());

        Fisherman fisherman = new Fisherman(map);

        /* Assign worker */
        f.assignWorker(fisherman);

        assertFalse(f.needsWorker());
        assertTrue(f.getWorker().equals(fisherman));
    }

    @Test(expected = Exception.class)
    public void testPromiseWorkerToUnfinishedFishery() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Fishery f = new Fishery();

        assertTrue(f.getConstructionState() == UNDER_CONSTRUCTION);

        f.promiseWorker(new Fisherman(map));
    }

    @Test(expected = Exception.class)
    public void testAssignWorkerToUnfinishedFisherman() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Fishery f = new Fishery();

        assertTrue(f.getConstructionState() == UNDER_CONSTRUCTION);

        f.assignWorker(new Fisherman(map));
    }

    @Test(expected = Exception.class)
    public void testAssignWorkerTwice() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Fishery f = new Fishery();

        Utils.constructSmallHouse(f);

        f.assignWorker(new Fisherman(map));

        f.assignWorker(new Fisherman(map));
    }

    @Test(expected = Exception.class)
    public void testPromiseWorkerTwice() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Fishery f = new Fishery();

        Utils.constructSmallHouse(f);

        f.promiseWorker(new Fisherman(map));

        f.promiseWorker(new Fisherman(map));
    }

    @Test
    public void testFisheryIsNotMilitary() throws Exception {
        Fishery f = new Fishery();

        Utils.constructSmallHouse(f);

        assertFalse(f.isMilitaryBuilding());
        assertTrue(f.getHostedMilitary() == 0);
        assertTrue(f.getMaxHostedMilitary() == 0);
    }

    @Test
    public void testFisheryUnderConstructionNotNeedsWorker() {
        Fishery f = new Fishery();

        assertFalse(f.needsWorker());
    }

    @Test
    public void testFishermanIsAssignedToFishery() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        Point point1 = new Point(10, 4);
        Building fishery = map.placeBuilding(new Fishery(), point1);

        Point point2 = new Point(6, 4);
        Point point3 = new Point(7, 3);
        Point point4 = new Point(8, 2);
        Point point5 = new Point(10, 2);
        Point point6 = new Point(11, 3);
        Road road0 = map.placeRoad(point2, point3, point4, point5, point6);

        /* Finish the fisherman hut */
        Utils.constructSmallHouse(fishery);
        
        /* Run game logic twice, once to place courier and once to place fisherman */
        Utils.fastForward(2, map);

        Utils.verifyListContainsWorkerOfType(map.getAllWorkers(), Fisherman.class);
    }

    @Test
    public void testOnlyOneFishermanIsAssignedToFishery() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        Point point1 = new Point(10, 4);
        Building fishery = map.placeBuilding(new Fishery(), point1);

        Point point2 = new Point(6, 4);
        Point point3 = new Point(7, 3);
        Point point4 = new Point(8, 2);
        Point point5 = new Point(10, 2);
        Point point6 = new Point(11, 3);
        Road road0 = map.placeRoad(point2, point3, point4, point5, point6);

        /* Construct the fisherman hut */
        constructSmallHouse(fishery);
        
        /* Run game logic twice, once to place courier and once to place fisherman */
        Utils.fastForward(2, map);

        assertTrue(map.getAllWorkers().size() == 3);

        /* Keep running the game loop and make sure no more workers are allocated */
        Utils.fastForward(200, map);

        assertTrue(map.getAllWorkers().size() == 3);
    }

    @Test
    public void testArrivedFishermanRestsInFisherytAndThenLeaves() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place water on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        Utils.surroundPointWithWater(point0, point1, point2, map);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point3 = new Point(10, 4);
        Building fishery = map.placeBuilding(new Fishery(), point3);

        /* Construct the fisherman hut */
        constructSmallHouse(fishery);
        
        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(map);

        Utils.occupyBuilding(fisherman, fishery, map);
        
        assertTrue(fisherman.isInsideBuilding());
        
        /* Run the game logic 99 times and make sure the fisherman stays in the fishery */        
        int i;
        for (i = 0; i < 99; i++) {
            assertTrue(fisherman.isInsideBuilding());
            map.stepTime();
        }
        
        assertTrue(fisherman.isInsideBuilding());
        
        /* Step once and make sure the fisherman goes out of the fishery */
        map.stepTime();        
        
        assertFalse(fisherman.isInsideBuilding());
    }

    @Test
    public void testFishermanFindsSpotToFish() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place water on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        Utils.surroundPointWithWater(point0, point1, point2, map);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point3 = new Point(6, 6);
        Building fishery = map.placeBuilding(new Fishery(), point3);

        /* Construct the fisherman fishery */
        constructSmallHouse(fishery);
        
        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(map);

        Utils.occupyBuilding(fisherman, fishery, map);
        
        assertTrue(fisherman.isInsideBuilding());
        
        /* Let the fisherman rest */
        Utils.fastForward(99, map);
        
        assertTrue(fisherman.isInsideBuilding());
        
        /* Step once and make sure the fisherman goes out of the hut */
        map.stepTime();
        
        assertFalse(fisherman.isInsideBuilding());    

        Point point = fisherman.getTarget();
        assertTrue(point.equals(point2) || point.equals(point1) || point.equals(point0));
        
        assertFalse(map.isBuildingAtPoint(point));
        assertTrue(fisherman.isTraveling());
    }

    @Test
    public void testFishermanReachesPointToFish() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place water on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        Utils.surroundPointWithWater(point0, point1, point2, map);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point3 = new Point(7, 5);
        Building fishermanHut = map.placeBuilding(new Fishery(), point3);

        /* Construct the fisherman hut */
        constructSmallHouse(fishermanHut);
        
        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(map);

        Utils.occupyBuilding(fisherman, fishermanHut, map);
        
        /* Let the fisherman rest */
        Utils.fastForward(99, map);
        
        assertTrue(fisherman.isInsideBuilding());
        
        /* Step once and make sure the fisherman goes out of the house */
        map.stepTime();
        
        assertFalse(fisherman.isInsideBuilding());    

        Point point = fisherman.getTarget();

        assertTrue(point.equals(point2) || point.equals(point1) || point.equals(point0));
        assertTrue(fisherman.isTraveling());
        
        /* Let the fisherman reach the point */
        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);
        
        assertEquals(fisherman.getPosition(), point);
        assertFalse(fisherman.isTraveling());
        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isAt(point));
    }

    @Test
    public void testFishermanFishes() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place water on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        Utils.surroundPointWithWater(point0, point1, point2, map);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point3 = new Point(7, 5);
        Building fishermanHut = map.placeBuilding(new Fishery(), point3);

        /* Construct the fisherman hut */
        constructSmallHouse(fishermanHut);
        
        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(map);

        Utils.occupyBuilding(fisherman, fishermanHut, map);
        
        /* Let the fisherman rest */
        Utils.fastForward(99, map);
                
        assertTrue(fisherman.isInsideBuilding());
        
        /* Step once and make sure the fisherman goes out of the hut */
        map.stepTime();
        
        assertFalse(fisherman.isInsideBuilding());    

        Point point = fisherman.getTarget();

        assertTrue(fisherman.isTraveling());
        
        /* Let the fisherman reach the spot and start fishing */
        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);
        
        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isAt(point));        
        assertTrue(fisherman.isFishing());
        
        /* Verify that the fisherman fishes the right time */
        int i;
        for (i = 0; i < 19; i++) {
            assertTrue(fisherman.isFishing());
            map.stepTime();
        }

        assertTrue(fisherman.isFishing());
        assertNull(fisherman.getCargo());

        map.stepTime();
        
        /* Verify that the fisherman is done fishing and that the amount of fish 
            has decreased
        */
        assertFalse(fisherman.isFishing());
        assertNotNull(fisherman.getCargo());
        assertEquals(fisherman.getCargo().getMaterial(), FISH);
        // TODO: verify that the amount of fish has decreased
    }

    @Test
    public void testFishermanReturnsHomeAfterFishing() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place water on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        Utils.surroundPointWithWater(point0, point1, point2, map);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point3 = new Point(7, 5);
        Building fishermanHut = map.placeBuilding(new Fishery(), point3);

        /* Construct the fisherman hut */
        constructSmallHouse(fishermanHut);
        
        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(map);

        Utils.occupyBuilding(fisherman, fishermanHut, map);
        
        /* Let the fisherman rest */
        Utils.fastForward(99, map);
                
        assertTrue(fisherman.isInsideBuilding());
        
        /* Step once and make sure the fisherman goes out of the house */
        map.stepTime();
        
        assertFalse(fisherman.isInsideBuilding());    

        Point point = fisherman.getTarget();

        assertTrue(fisherman.isTraveling());
        
        /* Wait for the fisherman to reach the spot */
        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);
        
        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isAt(point));
        assertTrue(fisherman.isFishing());
        
        /* Wait for the fisherman to get a fish */
        Utils.fastForward(19, map);
        
        assertTrue(fisherman.isFishing());

        map.stepTime();
        
        /* Verify that the fisherman goes back home */
        assertFalse(fisherman.isFishing());

        assertEquals(fisherman.getTarget(), fishermanHut.getPosition());
        assertTrue(fisherman.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isInsideBuilding());
    }
    
    @Test
    public void testFishermanPlacesFishAtFlag() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place water on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        Utils.surroundPointWithWater(point0, point1, point2, map);
        
        Point hqPoint = new Point(15, 15);
        Building hq = map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point3 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(), point3);

        /* Connect the hq with the fishery */
        map.placeAutoSelectedRoad(hq.getFlag(), fishery.getFlag());
        
        /* Construct the fisherman hut */
        constructSmallHouse(fishery);
        
        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(map);

        Utils.occupyBuilding(fisherman, fishery, map);
        
        /* Let the fisherman rest */
        Utils.fastForward(99, map);
                
        assertTrue(fisherman.isInsideBuilding());
        
        /* Step once and make sure the fisherman goes out of the house */
        map.stepTime();
        
        assertFalse(fisherman.isInsideBuilding());    

        Point point = fisherman.getTarget();

        assertTrue(fisherman.isTraveling());
        
        /* Wait for the fisherman to reach the spot */
        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);
        
        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isAt(point));
        assertTrue(fisherman.isFishing());
        
        /* Wait for the fisherman to get a fish */
        Utils.fastForward(19, map);
        
        assertTrue(fisherman.isFishing());

        map.stepTime();
        
        /* Verify that the fisherman goes back home */
        assertFalse(fisherman.isFishing());

        assertEquals(fisherman.getTarget(), fishery.getPosition());
        assertTrue(fisherman.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isInsideBuilding());
        
        /* Verify that the fisherman goes out to the flag with the cargo */
        map.stepTime();
        
        assertEquals(fisherman.getTarget(), fishery.getFlag().getPosition());
        assertTrue(fishery.getFlag().getStackedCargo().isEmpty());
        assertNotNull(fisherman.getCargo());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fisherman.getTarget());
        
        assertFalse(fishery.getFlag().getStackedCargo().isEmpty());
        assertNull(fisherman.getCargo());
        
        /* Verify that the fisherman goes back to the house again */
        assertEquals(fisherman.getTarget(), fishery.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);
        
        assertTrue(fisherman.isInsideBuilding());
    }
    
    @Test
    public void testFishermanStaysInsideWhenThereIsNoWaterClose() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point1 = new Point(10, 4);
        Building fishermanHut = map.placeBuilding(new Fishery(), point1);

        /* Construct the fisherman hut */
        constructSmallHouse(fishermanHut);

        /* Put trees around the fisherman hut */
        for (Point p : map.getPointsWithinRadius(fishermanHut.getPosition(), 4)) {
            if (p.equals(point1)) {
                continue;
            }
            
            if (map.isBuildingAtPoint(p) || map.isFlagAtPoint(p) || map.isRoadAtPoint(p) || map.isStoneAtPoint(p)) {
                continue;
            }
            
            map.placeTree(p);
        }
        
        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(map);

        Utils.occupyBuilding(fisherman, fishermanHut, map);
        
        assertTrue(fisherman.isInsideBuilding());
        
        /* Verify that the fisherman stays in the hut */
        int i;
        for (i = 0; i < 200; i++) {
            assertTrue(fisherman.isInsideBuilding());
            map.stepTime();
        }
    }
}
