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
import org.appland.settlers.model.IronMine;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.MINER;
import org.appland.settlers.model.Miner;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.SMALL;
import org.appland.settlers.model.Worker;
import static org.appland.settlers.test.Utils.constructHouse;
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
public class TestIronMine {
    
    @Test
    public void testConstructIronMine() throws Exception {
        GameMap map   = new GameMap(10, 10);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(8, 2);
        Utils.surroundPointWithMountain(point0, map);
        
        /* Place a headquarter */
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a goldmine*/
        Building mine = map.placeBuilding(new IronMine(), point0);
        
        assertTrue(mine.underConstruction());
        
        Utils.constructHouse(mine, map);
        
        assertTrue(mine.ready());
    }
    
    @Test
    public void testIronmineIsNotMilitary() throws Exception {
        GameMap map   = new GameMap(10, 10);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 2);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new IronMine(), point0);
        
        /* Verify that the mine is not a military building */
        assertFalse(mine.isMilitaryBuilding());
        
        Utils.constructHouse(mine, map);
        
        assertFalse(mine.isMilitaryBuilding());
    }
    
    @Test
    public void testIronmineUnderConstructionNotNeedsMiner() throws Exception {
        GameMap map   = new GameMap(10, 10);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 2);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine*/
        Building mine = map.placeBuilding(new IronMine(), point0);
        
        /* Verify that the unfinished mine does not need a worker */
        assertFalse(mine.needsWorker());
    }
    
    @Test
    public void testFinishedIronmineNeedsMiner() throws Exception {
        GameMap map   = new GameMap(10, 10);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 2);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine*/
        Building mine = map.placeBuilding(new IronMine(), point0);

        Utils.constructHouse(mine, map);
        
        /* Verify that the finished mine needs a worker */
        assertTrue(mine.needsWorker());
    }
    
    @Test
    public void testMinerIsAssignedToFinishedIronmine() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place a headquarter */
        Point hqPoint = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), hqPoint);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new IronMine(), point0);
        
        /* Place a road between the headquarter and the goldmine */
        Road road0 = map.placeAutoSelectedRoad(building0.getFlag(), mine.getFlag());

        /* Construct the mine */
        constructHouse(mine, map);
        
        assertTrue(mine.ready());

        /* Run game logic twice, once to place courier and once to place miner */
        Utils.fastForward(2, map);
        
        assertEquals(map.getWorkers().size(), 3);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Miner.class);
        
        /* Keep running the game loop and make sure no more workers are allocated */
        Utils.fastForward(200, map);

        assertEquals(map.getWorkers().size(), 3);
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
        Building mine = map.placeBuilding(new IronMine(), point0);
        
        assertEquals(map.getBuildings().size(), 2);
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
        Building mine = map.placeBuilding(new IronMine(), point0);

        /* Construct the gold mine */
        constructHouse(mine, map);
        
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
    public void testMinerMinesIron() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);
        
        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new IronMine(), point0);

        /* Construct the gold mine */
        constructHouse(mine, map);
        
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
        int amountIron = map.getAmountOfMineralAtPoint(IRON, point0);
        
        int i;
        for (i = 0; i < 50; i++) {
            assertTrue(miner.isMining());
            map.stepTime();
        }
        
        /* Verify that the miner finishes mining on time and has gold */
        assertFalse(miner.isMining());
        assertFalse(miner.isInsideBuilding());
        assertNotNull(miner.getCargo());
        assertEquals(miner.getCargo().getMaterial(), IRON);
        assertTrue(map.getAmountOfMineralAtPoint(IRON, point0) < amountIron);
    }

    @Test
    public void testIronmineGoesToFlagWithCargoAndBack() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Building building0 = map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new IronMine(), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(building0.getFlag(), mine.getFlag());
        
        /* Construct the gold mine */
        constructHouse(mine, map);

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
            map.placeBuilding(new IronMine(), point0);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertEquals(map.getBuildings().size(), 1);
    }

    @Test
    public void testIronmineRunsOutOfIron() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, SMALL, map);

        /* Remove all gold but one */
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountOfMineralAtPoint(IRON, point0) > 1) {
                map.mineMineralAtPoint(IRON, point0);
            }
        }
        
        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Building building0 = map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new IronMine(), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(building0.getFlag(), mine.getFlag());
        
        /* Construct the gold mine */
        constructHouse(mine, map);

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
        
        /* Wait for the miner to leave the gold at the flag */
        assertEquals(miner.getTarget(), mine.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getFlag().getPosition());
        
        assertNull(miner.getCargo());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getPosition());
        
        assertTrue(miner.isInsideBuilding());

        /* Verify that the gold is gone and that the miner gets no gold */
        assertEquals(map.getAmountOfMineralAtPoint(IRON, point0), 0);

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());
            
            map.stepTime();
        }
    }

    @Test
    public void testIronmineWithoutIronProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        
        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Building building0 = map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new IronMine(), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(building0.getFlag(), mine.getFlag());
        
        /* Construct the gold mine */
        constructHouse(mine, map);

        /* Deliver food to the miner */
        Cargo food = new Cargo(BREAD, map);
        mine.putCargo(food);

        /* Manually place miner */
        Miner miner = new Miner(map);

        Utils.occupyBuilding(miner, mine, map);
        
        assertTrue(miner.isInsideBuilding());
        
        /* Wait for the miner to rest */
        Utils.fastForward(100, map);
        
        /* Verify that there is no gold and that the miner gets no gold */
        assertEquals(map.getAmountOfMineralAtPoint(IRON, point0), 0);

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());
            
            map.stepTime();
        }
    }
    
    @Test
    public void testIronmineWithoutFoodProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);
        
        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Building building0 = map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new IronMine(), point0);

        /* Construct the gold mine */
        constructHouse(mine, map);

        /* Manually place miner */
        Miner miner = new Miner(map);

        Utils.occupyBuilding(miner, mine, map);
        
        assertTrue(miner.isInsideBuilding());
        
        /* Wait for the miner to rest */
        Utils.fastForward(100, map);
        
        /* Verify that the miner gets no gold */

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());
            
            map.stepTime();
        }
    }

    @Test
    public void testMiningConsumesFood() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);
        
        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new IronMine(), point0);

        /* Construct the gold mine */
        constructHouse(mine, map);
        
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
        assertEquals(mine.getAmount(BREAD), 1);
        
        Utils.fastForward(50, map);
        
        /* Verify that the miner consumed the bread */
        assertEquals(mine.getAmount(BREAD), 0);
    }

    @Test
    public void testIronMineWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(), point0);

        /* Finish construction of the iron mine */
        Utils.constructHouse(ironMine0, map);

        /* Occupy the iron mine */
        Utils.occupyBuilding(new Miner(map), ironMine0, map);

        /* Deliver material to the iron mine */
        Cargo fishCargo = new Cargo(FISH, map);
        
        ironMine0.putCargo(fishCargo);
        ironMine0.putCargo(fishCargo);
        
        /* Let the miner rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce a new iron cargo */
        Utils.fastForward(50, map);

        Worker miner = ironMine0.getWorker();

        assertNotNull(miner.getCargo());

        /* Verify that the miner puts the iron cargo at the flag */
        assertEquals(miner.getTarget(), ironMine0.getFlag().getPosition());
        assertTrue(ironMine0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, ironMine0.getFlag().getPosition());

        assertNull(miner.getCargo());
        assertFalse(ironMine0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait for the worker to go back to the iron mine */
        assertEquals(miner.getTarget(), ironMine0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, ironMine0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(miner.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(miner.getTarget(), ironMine0.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, ironMine0.getFlag().getPosition());
        
        assertNull(miner.getCargo());
        assertEquals(ironMine0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(), point0);

        /* Finish construction of the iron mine */
        Utils.constructHouse(ironMine0, map);

        /* Deliver material to the iron mine */
        Cargo fishCargo = new Cargo(FISH, map);
        
        ironMine0.putCargo(fishCargo);
        ironMine0.putCargo(fishCargo);

        /* Occupy the iron mine */
        Utils.occupyBuilding(new Miner(map), ironMine0, map);

        /* Let the miner rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce a new iron cargo */
        Utils.fastForward(50, map);

        Worker miner = ironMine0.getWorker();

        assertNotNull(miner.getCargo());

        /* Verify that the miner puts the iron cargo at the flag */
        assertEquals(miner.getTarget(), ironMine0.getFlag().getPosition());
        assertTrue(ironMine0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, ironMine0.getFlag().getPosition());

        assertNull(miner.getCargo());
        assertFalse(ironMine0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = ironMine0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), ironMine0.getFlag().getPosition());
    
        /* Connect the iron mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(headquarter0.getFlag(), ironMine0.getFlag());
    
        /* Assign a courier to the road */
        Courier courier = new Courier(map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);
    
        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(ironMine0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
    
        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), ironMine0.getFlag().getPosition());
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);
        
        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());
        
        int amount = headquarter0.getAmount(IRON);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());
        
        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(IRON), amount + 1);
    }

    @Test
    public void testMinerGoesBackToStorageWhenIronMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Placing iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(), point0);

        /* Finish construction of the iron mine */
        Utils.constructHouse(ironMine0, map);

        /* Occupy the iron mine */
        Utils.occupyBuilding(new Miner(map), ironMine0, map);
        
        /* Destroy the iron mine */
        Worker miner = ironMine0.getWorker();
        
        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), ironMine0.getPosition());

        ironMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(MINER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, miner, headquarter0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerGoesBackOnToStorageOnRoadsIfPossibleWhenIronMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Placing iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(), point0);

        /* Connect the iron mine with the headquarter */
        map.placeAutoSelectedRoad(ironMine0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the iron mine */
        Utils.constructHouse(ironMine0, map);

        /* Occupy the iron mine */
        Utils.occupyBuilding(new Miner(map), ironMine0, map);
        
        /* Destroy the iron mine */
        Worker miner = ironMine0.getWorker();
        
        assertTrue(miner.isInsideBuilding());
        assertEquals(miner.getPosition(), ironMine0.getPosition());

        ironMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(miner.isInsideBuilding());
        assertEquals(miner.getTarget(), headquarter0.getPosition());
    
        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point p : miner.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test
    public void testProductionInMineCanBeStopped() throws Exception {

        /* Create game map */
        GameMap map = new GameMap(20, 20);
        
        /* Put a small mountain on the map */
        Point point1 = new Point(10, 6);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        
        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(), point1);
        
        /* Connect the iron mine and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Point point5 = new Point(11, 5);
        Road road0 = map.placeRoad(point2, point3, point4, point5);
        
        /* Finish the iron mine */
        Utils.constructHouse(ironMine0, map);
        
        /* Deliver material to the iron mine */
        Cargo fishCargo = new Cargo(FISH, map);
        
        ironMine0.putCargo(fishCargo);
        ironMine0.putCargo(fishCargo);

        /* Assign a worker to the iron mine */
        Miner miner = new Miner(map);
        
        Utils.occupyBuilding(miner, ironMine0, map);
        
        assertTrue(miner.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the miner to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, miner);
        
        assertEquals(miner.getCargo().getMaterial(), IRON);

        /* Wait for the worker to deliver the cargo */
        assertEquals(miner.getTarget(), ironMine0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, ironMine0.getFlag().getPosition());

        /* Stop production and verify that no iron is produced */
        ironMine0.stopProduction();
        
        assertFalse(ironMine0.isProductionEnabled());
        
        for (int i = 0; i < 300; i++) {
            assertNull(miner.getCargo());
            
            map.stepTime();
        }
    }

    @Test
    public void testProductionInMineCanBeResumed() throws Exception {

        /* Create game map */
        GameMap map = new GameMap(20, 20);
        
        /* Put a small mountain on the map */
        Point point1 = new Point(10, 6);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        
        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(), point1);
        
        /* Connect the iron mine and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Point point5 = new Point(11, 5);
        Road road0 = map.placeRoad(point2, point3, point4, point5);
        
        /* Finish the iron mine */
        Utils.constructHouse(ironMine0, map);
        
        /* Assign a worker to the iron mine */
        Miner miner = new Miner(map);
        
        Utils.occupyBuilding(miner, ironMine0, map);
        
        assertTrue(miner.isInsideBuilding());

        /* Deliver material to the iron mine */
        Cargo fishCargo = new Cargo(FISH, map);
        
        ironMine0.putCargo(fishCargo);
        ironMine0.putCargo(fishCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the miner to produce iron */
        Utils.fastForwardUntilWorkerProducesCargo(map, miner);

        assertEquals(miner.getCargo().getMaterial(), IRON);

        /* Wait for the worker to deliver the cargo */
        assertEquals(miner.getTarget(), ironMine0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, ironMine0.getFlag().getPosition());

        /* Stop production */
        ironMine0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(miner.getCargo());
            
            map.stepTime();
        }

        /* Resume production and verify that the iron mine produces iron again */
        ironMine0.resumeProduction();

        assertTrue(ironMine0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, miner);

        assertNotNull(miner.getCargo());
    }
}
