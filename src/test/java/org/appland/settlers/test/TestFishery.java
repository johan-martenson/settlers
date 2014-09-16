/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Fisherman;
import org.appland.settlers.model.Fishery;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidStateForProduction;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.FISHERMAN;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Tile.Vegetation;
import static org.appland.settlers.model.Tile.Vegetation.MOUNTAIN;
import org.appland.settlers.model.Worker;
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

        assertTrue(f.underConstruction());

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

        assertTrue(f.underConstruction());

        f.promiseWorker(new Fisherman(map));
    }

    @Test(expected = Exception.class)
    public void testAssignWorkerToUnfinishedFisherman() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Fishery f = new Fishery();

        assertTrue(f.underConstruction());

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
        int amountOfFish = map.getAmountFishAtPoint(point);
        
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
        assertTrue(map.getAmountFishAtPoint(point) < amountOfFish);
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

    @Test
    public void testPlaceFisherySoFirstMatchIsMiddleOfLake() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Place a water tile */
        Point point0 = new Point(10, 4);
        Point point1 = new Point(8, 4);
        Point point2 = new Point(9, 5);
        map.getTerrain().getTile(point0, point1, point2).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Place a water tile */
        Point point3 = new Point(11, 5);
        map.getTerrain().getTile(point0, point2, point3).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Place a water tile */
        Point point4 = new Point(12, 4);
        map.getTerrain().getTile(point0, point3, point4).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Place a water tile */
        Point point5 = new Point(11, 3);
        map.getTerrain().getTile(point0, point4, point5).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Place a water tile */
        Point point6 = new Point(9, 3);
        map.getTerrain().getTile(point0, point5, point6).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Place a water tile */
        map.getTerrain().getTile(point0, point6, point1).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Place a mountain tile */
        Point point7 = new Point(5, 13);
        Point point8 = new Point(3, 13);
        Point point9 = new Point(4, 14);
        map.getTerrain().getTile(point7, point8, point9).setVegetationType(MOUNTAIN);
        map.terrainIsUpdated();

        /* Place a mountain tile */
        Point point10 = new Point(6, 14);
        map.getTerrain().getTile(point7, point9, point10).setVegetationType(MOUNTAIN);
        map.terrainIsUpdated();

        /* Place a mountain tile */
        Point point11 = new Point(7, 13);
        map.getTerrain().getTile(point7, point10, point11).setVegetationType(MOUNTAIN);
        map.terrainIsUpdated();

        /* Place a mountain tile */
        Point point12 = new Point(6, 12);
        map.getTerrain().getTile(point7, point11, point12).setVegetationType(MOUNTAIN);
        map.terrainIsUpdated();

        /* Place a mountain tile */
        Point point13 = new Point(4, 12);
        map.getTerrain().getTile(point7, point12, point13).setVegetationType(MOUNTAIN);
        map.terrainIsUpdated();

        /* Place a mountain tile */
        map.getTerrain().getTile(point7, point13, point8).setVegetationType(MOUNTAIN);
        map.terrainIsUpdated();

        /* Place a mountain tile */
        Point point14 = new Point(8, 14);
        Point point15 = new Point(7, 15);
        map.getTerrain().getTile(point14, point10, point15).setVegetationType(MOUNTAIN);
        map.terrainIsUpdated();

        /* Place a mountain tile */
        Point point16 = new Point(9, 15);
        map.getTerrain().getTile(point14, point15, point16).setVegetationType(MOUNTAIN);
        map.terrainIsUpdated();

        /* Place a mountain tile */
        Point point17 = new Point(10, 14);
        map.getTerrain().getTile(point14, point16, point17).setVegetationType(MOUNTAIN);
        map.terrainIsUpdated();

        /* Place a mountain tile */
        Point point18 = new Point(9, 13);
        map.getTerrain().getTile(point14, point17, point18).setVegetationType(MOUNTAIN);
        map.terrainIsUpdated();

        /* Place a mountain tile */
        map.getTerrain().getTile(point14, point18, point11).setVegetationType(MOUNTAIN);
        map.terrainIsUpdated();

        /* Place a mountain tile */
        map.getTerrain().getTile(point14, point11, point10).setVegetationType(MOUNTAIN);
        map.terrainIsUpdated();

        /* Placing stone */
        Point point19 = new Point(12, 12);
        Stone stone0 = map.placeStone(point19);

        /* Placing stone */
        Point point20 = new Point(13, 11);
        Stone stone1 = map.placeStone(point20);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* 7 ticks from start */
        for (int i = 0; i < 7; i++) {
            map.stepTime();
        }

        /* Placing fishery */
        Point point22 = new Point(10, 8);
        Building fishery0 = map.placeBuilding(new Fishery(), point22);

        /* 20 ticks from start */
        for (int i = 0; i < 13; i++) {
            map.stepTime();
        }

        /* Placing road between (11, 7) and (6, 4) */
        Point point23 = new Point(11, 7);
        Point point24 = new Point(10, 6);
        Point point25 = new Point(7, 5);
        Point point26 = new Point(6, 4);
        Road road0 = map.placeRoad(point23, point24, point2, point25, point26);

        /* Wait for the fishery to be finished */
        Utils.fastForwardUntilBuildingIsConstructed(fishery0, map);
        
        /* Wait for the fishery to get occupied */
        Utils.fastForwardUntilBuildingIsOccupied(fishery0, map);
    
        /* Wait for the fisherman to rest */
        Utils.fastForward(100, map);
        
        /* Verify that the fisherman leaves the hut */
        Worker fisher = fishery0.getWorker();
        
        assertNotNull(fisher.getTarget());
    }

    @Test
    public void testFishermanCanRunOutOfFish() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place water on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        Utils.surroundPointWithWater(point0, point1, point2, map);

        /* Remove fishes until there is only one left */
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountFishAtPoint(point0) == 1) {
                break;
            }
        
            map.catchFishAtPoint(point0);
        }

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Building hq = map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place fishery */
        Point point3 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(), point3);

        /* Place a road from the headquarter to the fishery */
        map.placeAutoSelectedRoad(hq.getFlag(), fishery.getFlag());
        
        /* Construct the fisherman hut */
        constructSmallHouse(fishery);
        
        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(map);

        Utils.occupyBuilding(fisherman, fishery, map);
        
        /* Let the fisherman rest */
        Utils.fastForward(99, map);
                
        assertTrue(fisherman.isInsideBuilding());
        
        /* Step once and make sure the fisherman goes out of the hut */
        map.stepTime();
        
        assertFalse(fisherman.isInsideBuilding());

        Point point = fisherman.getTarget();

        assertTrue(fisherman.isTraveling());
        
        /* Let the fisherman reach the spot and start fishing */
        int amountOfFish = map.getAmountFishAtPoint(point);
        
        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);
        
        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isAt(point));        
        assertTrue(fisherman.isFishing());
        
        /* Wait for the fisherman to finish fishing */
        Utils.fastForward(20, map);
        
        /* Let the fisherman go back to the fishery */
        assertEquals(fisherman.getTarget(), fishery.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);
        
        /* Wait for the fisherman to leave the cargo of fish at the flag */
        map.stepTime();

        assertEquals(fisherman.getTarget(), fishery.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getFlag().getPosition());
        
        assertNull(fisherman.getCargo());
        assertEquals(fisherman.getTarget(), fishery.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);
        
        /* Verify that there is no more fish and that the fisherman statys at home */
        assertEquals(map.getAmountFishAtPoint(point0), 0);
        assertEquals(map.getAmountFishAtPoint(point1), 0);
        assertEquals(map.getAmountFishAtPoint(point2), 0);
        
        for (int i = 0; i < 200; i++) {
            assertTrue(fisherman.isInsideBuilding());
            assertNull(fisherman.getCargo());
            
            map.stepTime();
        }
    }

    @Test
    public void testFisheryWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Place water on one tile */
        Point point0 = new Point(8, 6);
        Point point1 = new Point(10, 6);
        Point point2 = new Point(9, 7);

        Utils.surroundPointWithWater(point0, point1, point2, map);
        
        /* Placing fishery */
        Point point26 = new Point(8, 8);
        Building fishery0 = map.placeBuilding(new Fishery(), point26);

        /* Finish construction of the fishery */
        Utils.constructSmallHouse(fishery0);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(map), fishery0, map);

        /* Let the fisherman rest */
        Utils.fastForward(100, map);

        /* Wait for the fisherman to produce a new fish cargo */
        Worker ww = fishery0.getWorker();

        for (int i = 0; i < 1000; i++) {
            if (ww.getCargo() != null && ww.isAt(fishery0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(ww.getCargo());

        /* Verify that the fisherman puts the fish cargo at the flag */
        map.stepTime();
        
        assertEquals(ww.getTarget(), fishery0.getFlag().getPosition());
        assertTrue(fishery0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, fishery0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(fishery0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait for the worker to go back to the fishery */
        assertEquals(ww.getTarget(), fishery0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, fishery0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        for (int i = 0; i < 1000; i++) {
            if (ww.getCargo() != null && ww.isAt(fishery0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(ww.getCargo());

        /* Verify that the second cargo is put at the flag */
        map.stepTime();
        
        assertEquals(ww.getTarget(), fishery0.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, fishery0.getFlag().getPosition());
        
        assertNull(ww.getCargo());
        assertEquals(fishery0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Place water on one tile */
        Point point0 = new Point(8, 6);
        Point point1 = new Point(10, 6);
        Point point2 = new Point(9, 7);

        Utils.surroundPointWithWater(point0, point1, point2, map);
        
        /* Placing fishery */
        Point point26 = new Point(8, 8);
        Building fishery0 = map.placeBuilding(new Fishery(), point26);

        /* Finish construction of the fishery */
        Utils.constructSmallHouse(fishery0);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(map), fishery0, map);

        /* Let the fisherman rest */
        Utils.fastForward(100, map);

        /* Wait for the fisherman to produce a new fish cargo */
        Worker ww = fishery0.getWorker();

        for (int i = 0; i < 1000; i++) {
            if (ww.getCargo() != null && ww.isAt(fishery0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(ww.getCargo());

        /* Verify that the fisherman puts the fish cargo at the flag */
        map.stepTime();
        
        assertEquals(ww.getTarget(), fishery0.getFlag().getPosition());
        assertTrue(fishery0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, fishery0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(fishery0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = fishery0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), fishery0.getFlag().getPosition());
    
        /* Connect the fishery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(headquarter0.getFlag(), fishery0.getFlag());
    
        /* Assign a courier to the road */
        Courier courier = new Courier(map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);
    
        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(fishery0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));
    
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
    
        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), fishery0.getFlag().getPosition());
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);
    
        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());
        
        int amount = headquarter0.getAmount(FISH);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());
        
        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(FISH), amount + 1);
    }

    @Test
    public void testFishermanGoesBackToStorageWhenFisheryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing fishery */
        Point point26 = new Point(8, 8);
        Building fishery0 = map.placeBuilding(new Fishery(), point26);

        /* Finish construction of the fishery */
        Utils.constructSmallHouse(fishery0);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(map), fishery0, map);
        
        /* Destroy the fishery */
        Worker ww = fishery0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), fishery0.getPosition());

        fishery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(FISHERMAN);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());

        /* Verify that the fisherman is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(FISHERMAN), amount + 1);
    }

    @Test
    public void testFishermanGoesBackOnToStorageOnRoadsIfPossibleWhenFisheryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing fishery */
        Point point26 = new Point(8, 8);
        Building fishery0 = map.placeBuilding(new Fishery(), point26);

        /* Connect the fishery with the headquarter */
        map.placeAutoSelectedRoad(fishery0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the fishery */
        Utils.constructSmallHouse(fishery0);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(map), fishery0, map);
        
        /* Destroy the fishery */
        Worker ww = fishery0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), fishery0.getPosition());

        fishery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        /* Verify that the worker plans to use the roads */
        for (Point p : ww.getPlannedPath()) {
            assertTrue(map.isRoadAtPoint(p));
        }
    }
}
