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
import org.appland.settlers.model.GraniteMine;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.MINER;
import static org.appland.settlers.model.Material.STONE;
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
public class TestGraniteMine {
    
    @Test
    public void testConstructGraniteMine() throws Exception {
        GameMap map   = new GameMap(10, 10);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(8, 2);
        Utils.surroundPointWithMountain(point0, map);
        
        /* Place a headquarter */
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a goldmine*/
        Building mine = map.placeBuilding(new GraniteMine(), point0);
        
        assertTrue(mine.underConstruction());
        
        Utils.constructHouse(mine, map);
        
        assertTrue(mine.ready());
    }
    
    @Test
    public void testGranitemineIsNotMilitary() throws Exception {
        GameMap map   = new GameMap(10, 10);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 2);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new GraniteMine(), point0);
        
        /* Verify that the mine is not a military building */
        assertFalse(mine.isMilitaryBuilding());
        
        Utils.constructHouse(mine, map);
        
        assertFalse(mine.isMilitaryBuilding());
    }
    
    @Test
    public void testGranitemineUnderConstructionNotNeedsMiner() throws Exception {
        GameMap map   = new GameMap(10, 10);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 2);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine*/
        Building mine = map.placeBuilding(new GraniteMine(), point0);
        
        /* Verify that the unfinished mine does not need a worker */
        assertFalse(mine.needsWorker());
    }
    
    @Test
    public void testFinishedGranitemineNeedsMiner() throws Exception {
        GameMap map   = new GameMap(10, 10);

        /* Put a small mountain on the map */
        Point point0 = new Point(8, 2);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a headquarter */
        Point hqPoint = new Point(7, 7);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine*/
        Building mine = map.placeBuilding(new GraniteMine(), point0);

        Utils.constructHouse(mine, map);
        
        /* Verify that the finished mine needs a worker */
        assertTrue(mine.needsWorker());
    }
    
    @Test
    public void testMinerIsAssignedToFinishedGranitemine() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place a headquarter */
        Point hqPoint = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), hqPoint);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new GraniteMine(), point0);
        
        /* Place a road between the headquarter and the goldmine */
        Road road0 = map.placeAutoSelectedRoad(building0.getFlag(), mine.getFlag());

        /* Construct the mine */
        constructHouse(mine, map);
        
        assertTrue(mine.ready());

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
        Building mine = map.placeBuilding(new GraniteMine(), point0);
        
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
        Building mine = map.placeBuilding(new GraniteMine(), point0);

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
    public void testMinerMinesGranite() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);
        
        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new GraniteMine(), point0);

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
        int amountGranite = map.getAmountOfMineralAtPoint(STONE, point0);
        
        int i;
        for (i = 0; i < 50; i++) {
            assertTrue(miner.isMining());
            map.stepTime();
        }
        
        /* Verify that the miner finishes mining on time and has gold */
        assertFalse(miner.isMining());
        assertFalse(miner.isInsideBuilding());
        assertNotNull(miner.getCargo());
        assertEquals(miner.getCargo().getMaterial(), STONE);
        assertTrue(map.getAmountOfMineralAtPoint(STONE, point0) < amountGranite);
    }

    @Test
    public void testGranitemineGoesToFlagWithCargoAndBack() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Building building0 = map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new GraniteMine(), point0);

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
            map.placeBuilding(new GraniteMine(), point0);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertTrue(map.getBuildings().size() == 1);
    }

    @Test
    public void testGranitemineRunsOutOfGranite() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGraniteAtSurroundingTiles(point0, SMALL, map);

        /* Remove all gold but one */
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountOfMineralAtPoint(STONE, point0) > 1) {
                map.mineMineralAtPoint(STONE, point0);
            }
        }
        
        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Building building0 = map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new GraniteMine(), point0);

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
        assertEquals(map.getAmountOfMineralAtPoint(STONE, point0), 0);

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());
            
            map.stepTime();
        }
    }

    @Test
    public void testGranitemineWithoutGraniteProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        
        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Building building0 = map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new GraniteMine(), point0);

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
        assertEquals(map.getAmountOfMineralAtPoint(STONE, point0), 0);

        for (int i = 0; i < 200; i++) {
            assertTrue(miner.isInsideBuilding());
            assertNull(miner.getCargo());
            
            map.stepTime();
        }
    }
    
    @Test
    public void testGranitemineWithoutFoodProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);
        
        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Building building0 = map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new GraniteMine(), point0);

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
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);
        
        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place a gold mine */
        Building mine = map.placeBuilding(new GraniteMine(), point0);

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
    public void testGraniteMineWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);
        
        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(), point0);

        /* Finish construction of the granite mine */
        Utils.constructMediumHouse(graniteMine0);

        /* Occupy the granite mine */
        Utils.occupyBuilding(new Miner(map), graniteMine0, map);

        /* Deliver material to the granite mine */
        Cargo fishCargo = new Cargo(FISH, map);
        
        graniteMine0.putCargo(fishCargo);
        graniteMine0.putCargo(fishCargo);
        
        /* Let the miner rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce a new stone cargo */
        Utils.fastForward(50, map);

        Worker ww = graniteMine0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the miner puts the stone cargo at the flag */
        assertEquals(ww.getTarget(), graniteMine0.getFlag().getPosition());
        assertTrue(graniteMine0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, graniteMine0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(graniteMine0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait for the worker to go back to the granite mine */
        assertEquals(ww.getTarget(), graniteMine0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, graniteMine0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(ww.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(ww.getTarget(), graniteMine0.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, graniteMine0.getFlag().getPosition());
        
        assertNull(ww.getCargo());
        assertEquals(graniteMine0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(), point0);

        /* Finish construction of the granite mine */
        Utils.constructMediumHouse(graniteMine0);

        /* Deliver material to the granite mine */
        Cargo fishCargo = new Cargo(FISH, map);
        
        graniteMine0.putCargo(fishCargo);
        graniteMine0.putCargo(fishCargo);

        /* Occupy the granite mine */
        Utils.occupyBuilding(new Miner(map), graniteMine0, map);

        /* Let the miner rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to produce a new stone cargo */
        Utils.fastForward(50, map);

        Worker ww = graniteMine0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the miner puts the stone cargo at the flag */
        assertEquals(ww.getTarget(), graniteMine0.getFlag().getPosition());
        assertTrue(graniteMine0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, graniteMine0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(graniteMine0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = graniteMine0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), graniteMine0.getFlag().getPosition());
    
        /* Connect the granite mine with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(headquarter0.getFlag(), graniteMine0.getFlag());
    
        /* Assign a courier to the road */
        Courier courier = new Courier(map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);
    
        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(graniteMine0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));
    
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
    
        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), graniteMine0.getFlag().getPosition());
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);
        
        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());
        
        int amount = headquarter0.getAmount(STONE);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());
        
        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(STONE), amount + 1);
    }

    @Test
    public void testMinerGoesBackToStorageWhenGraniteMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(), point0);

        /* Finish construction of the granite mine */
        Utils.constructHouse(graniteMine0, map);

        /* Occupy the granite mine */
        Utils.occupyBuilding(new Miner(map), graniteMine0, map);
        
        /* Destroy the granite mine */
        Worker ww = graniteMine0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), graniteMine0.getPosition());

        graniteMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(MINER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINER), amount + 1);
    }

    @Test
    public void testMinerGoesBackOnToStorageOnRoadsIfPossibleWhenGraniteMineIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putGraniteAtSurroundingTiles(point0, LARGE, map);

        /* Placing granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(), point0);

        /* Connect the granite mine with the headquarter */
        map.placeAutoSelectedRoad(graniteMine0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the granite mine */
        Utils.constructHouse(graniteMine0, map);

        /* Occupy the granite mine */
        Utils.occupyBuilding(new Miner(map), graniteMine0, map);
        
        /* Destroy the granite mine */
        Worker ww = graniteMine0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), graniteMine0.getPosition());

        graniteMine0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        /* Verify that the worker plans to use the roads */
        for (Point p : ww.getPlannedPath()) {
            assertTrue(map.isRoadAtPoint(p));
        }
    }
}
