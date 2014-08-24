/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
import static org.appland.settlers.model.Building.ConstructionState.UNDER_CONSTRUCTION;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GoldMine;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.GOLD;
import org.appland.settlers.model.Miner;
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
public class TestGoldMine {
    
    @Test
    public void testConstructGoldMine() throws Exception {
        GameMap map   = new GameMap(10, 10);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(8, 2);
        Utils.surroundPointWithMountain(point0, map);
        
        /* Place a headquarter */
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a goldmine*/
        Building mine = map.placeBuilding(new GoldMine(), point0);
        
        assertEquals(mine.getConstructionState(), UNDER_CONSTRUCTION);
        
        Utils.constructSmallHouse(mine);
        
        assertEquals(mine.getConstructionState(), DONE);
    }
    
    @Test
    public void testGoldmineIsNotMilitary() throws Exception {
        GameMap map   = new GameMap(10, 10);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 2);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(), point0);
        
        /* Verify that the mine is not a military building */
        assertFalse(mine.isMilitaryBuilding());
        
        Utils.constructSmallHouse(mine);
        
        assertFalse(mine.isMilitaryBuilding());
    }
    
    @Test
    public void testGoldmineUnderConstructionNotNeedsMiner() throws Exception {
        GameMap map   = new GameMap(10, 10);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 2);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine*/
        Building mine = map.placeBuilding(new GoldMine(), point0);
        
        /* Verify that the unfinished mine does not need a worker */
        assertFalse(mine.needsWorker());
    }
    
    @Test
    public void testFinishedGoldmineNeedsMiner() throws Exception {
        GameMap map   = new GameMap(10, 10);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 2);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine*/
        Building mine = map.placeBuilding(new GoldMine(), point0);

        Utils.constructSmallHouse(mine);
        
        /* Verify that the finished mine needs a worker */
        assertTrue(mine.needsWorker());
    }
    
    @Test
    public void testMinerIsAssignedToFinishedGoldmine() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place a headquarter */
        Point hqPoint = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), hqPoint);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(), point0);
        
        /* Place a road between the headquarter and the goldmine */
        Road road0 = map.placeAutoSelectedRoad(building0.getFlag(), mine.getFlag());

        /* Construct the mine */
        constructSmallHouse(mine);
        
        assertEquals(mine.getConstructionState(), DONE);

        /* Run game logic twice, once to place courier and once to place miner */
        Utils.fastForward(2, map);
        
        assertTrue(map.getAllWorkers().size() == 3);

        Utils.verifyListContainsWorkerOfType(map.getAllWorkers(), Miner.class);
        
        /* Keep running the game loop and make sure no more workers are allocated */
        Utils.fastForward(200, map);

        assertTrue(map.getAllWorkers().size() == 3);
    }
    
    @Test
    public void testCanPlaceMineOnPointSurroundedByMountain() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place a headquarter */
        Point hqPoint = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), hqPoint);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(), point0);
        
        assertTrue(map.getBuildings().size() == 2);
    }

    @Test
    public void testArrivedMinerRests() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(), point0);

        /* Construct the gold mine */
        constructSmallHouse(mine);
        
        /* Manually place miner */
        Miner miner = new Miner(map);

        Utils.occupyBuilding(miner, mine, map);
        
        assertTrue(miner.isInsideBuilding());
        
        /* Run the game logic 99 times and make sure the miner stays in the house */
        int i;
        for (i = 0; i < 99; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());
            assertFalse(miner.isMining());
            map.stepTime();
        }
        
        assertNull(miner.getCargo());
        assertFalse(miner.isMining());
        assertTrue(miner.isInsideBuilding());
    }

    @Test
    public void testMinerMinesGold() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(), point0);

        /* Construct the gold mine */
        constructSmallHouse(mine);
        
        /* Deliver food to the miner */
        Cargo food = new Cargo(BREAD, map);
        mine.putCargo(food);
        
        /* Manually place miner */
        Miner miner = new Miner(map);

        Utils.occupyBuilding(miner, mine, map);
        
        assertTrue(miner.isInsideBuilding());
        
        /* Wait for the miner to rest */
        Utils.fastForward(100, map);
        
        /* Verify that the miner mines for gold */
        int i;
        for (i = 0; i < 50; i++) {
            assertTrue(miner.isMining());
            map.stepTime();
        }
        
        /* Verify that the miner finishes mining on time and has gold */
        assertFalse(miner.isMining());
        assertFalse(miner.isInsideBuilding());
        assertNotNull(miner.getCargo());
        assertEquals(miner.getCargo().getMaterial(), GOLD);
    }

    @Test
    public void testGoldmineGoesToFlagWithCargoAndBack() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Building building0 = map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new GoldMine(), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(building0.getFlag(), mine.getFlag());
        
        /* Construct the gold mine */
        constructSmallHouse(mine);

        /* Deliver food to the miner */
        Cargo food = new Cargo(BREAD, map);
        mine.putCargo(food);

        /* Manually place miner */
        Miner miner = new Miner(map);

        Utils.occupyBuilding(miner, mine, map);
        
        assertTrue(miner.isInsideBuilding());
        
        /* Wait for the miner to rest */
        Utils.fastForward(100, map);
        
        /* Wait for the miner to mine gold */
        Utils.fastForward(50, map);
        
        /* Verify that the miner leaves the gold at the flag */
        assertFalse(miner.isMining());
        assertFalse(miner.isInsideBuilding());
        assertNotNull(miner.getCargo());
        assertEquals(miner.getTarget(), mine.getFlag().getPosition());
        assertTrue(mine.getFlag().getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getFlag().getPosition());
        
        assertNull(miner.getCargo());
        assertFalse(mine.getFlag().getStackedCargo().isEmpty());
        assertEquals(miner.getTarget(), mine.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getPosition());
        
        assertTrue(miner.isInsideBuilding());
    }
    
    @Test
    public void testCanNotPlaceMineOnGrass() throws Exception {
        GameMap map   = new GameMap(10, 10);
        
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point0 = new Point(2, 2);
        try {
            map.placeBuilding(new GoldMine(), point0);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertTrue(map.getBuildings().size() == 1);
    }

    @Test
    public void testGoldmineRunsOutOfGold() {
        // TODO: Implement test
    }

    @Test
    public void testGoldmineWithoutGoldProducesNothing() {
        // TODO: Implement test
    }
    
    @Test
    public void testGoldmineWithoutFoodProducesNothing() {
        // TODO: Implement test
    }

    @Test
    public void testMiningConsumesFood() {
        
    }
}
