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
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Mint;
import org.appland.settlers.model.Minter;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.MINTER;
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
public class TestMint {
    
    @Test
    public void testMintNeedsWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Unfinished mint doesn't need minter */
        assertFalse(mint.needsWorker());
        
        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);
        
        assertTrue(mint.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneMinterAtStart() {
        Headquarter hq = new Headquarter();
        
        assertTrue(hq.getAmount(MINTER) == 1);
    }
    
    @Test
    public void testMintGetsAssignedWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);
        
        assertTrue(mint.needsWorker());

        /* Verify that a minter leaves the hq */
        Utils.fastForward(3, map);
        
        assertTrue(map.getAllWorkers().size() == 3);

        /* Let the mint worker reach the mint */
        Minter minter = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Minter) {
                minter = (Minter)w;
            }
        }

        assertNotNull(minter);
        assertEquals(minter.getTarget(), mint.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, minter);
        
        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getHome(), mint);
        assertEquals(mint.getWorker(), minter);
    }
    
    @Test
    public void testOccupiedMintWithoutIngredientsProducesNothing() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(), point3);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);
        
        /* Populate the mint */
        Worker minter = Utils.occupyBuilding(new Minter(map), mint, map);
        
        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getHome(), mint);
        assertEquals(mint.getWorker(), minter);        

        /* Verify that the mint doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(mint.getFlag().getStackedCargo().isEmpty());
            assertNull(minter.getCargo());
            map.stepTime();
        }
    }
    
    @Test
    public void testUnoccupiedMintProducesNothing() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(), point3);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);

        /* Verify that the mint doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(mint.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedMintWithGoldAndCoalProducesCoins() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);
        
        /* Populate the mint */        
        Worker minter = Utils.occupyBuilding(new Minter(map), mint, map);
        
        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getHome(), mint);
        assertEquals(mint.getWorker(), minter);        

        /* Deliver wood to the mint */
        mint.putCargo(new Cargo(GOLD, map));
        mint.putCargo(new Cargo(COAL, map));
        
        /* Verify that the mint produces coin */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(mint.getFlag().getStackedCargo().isEmpty());
            assertNull(minter.getCargo());
        }

        map.stepTime();
        
        assertNotNull(minter.getCargo());
        assertEquals(minter.getCargo().getMaterial(), COIN);
        assertTrue(mint.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testMinterLeavesBreadAtTheFlag() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);
        
        /* Populate the mint */        
        Worker minter = Utils.occupyBuilding(new Minter(map), mint, map);
        
        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getHome(), mint);
        assertEquals(mint.getWorker(), minter);        

        /* Deliver ingredients to the mint */
        mint.putCargo(new Cargo(GOLD, map));
        mint.putCargo(new Cargo(COAL, map));
        
        /* Verify that the mint produces bread */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(mint.getFlag().getStackedCargo().isEmpty());
            assertNull(minter.getCargo());
        }

        map.stepTime();
        
        assertNotNull(minter.getCargo());
        assertEquals(minter.getCargo().getMaterial(), COIN);
        assertTrue(mint.getFlag().getStackedCargo().isEmpty());
        
        /* Verify that the mint worker leaves the cargo at the flag */
        assertEquals(minter.getTarget(), mint.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, minter, mint.getFlag().getPosition());
        
        assertFalse(mint.getFlag().getStackedCargo().isEmpty());
        assertNull(minter.getCargo());
        assertEquals(minter.getTarget(), mint.getPosition());
        
        /* Verify that the minter goes back to the mint */
        Utils.fastForwardUntilWorkersReachTarget(map, minter);
        
        assertTrue(minter.isInsideBuilding());
    }

    @Test
    public void testProductionOfOneBreadConsumesOneWaterAndOneFlour() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(), point3);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);
        
        /* Populate the mint */        
        Worker minter = Utils.occupyBuilding(new Minter(map), mint, map);
        
        /* Deliver ingredients to the mint */
        mint.putCargo(new Cargo(GOLD, map));
        mint.putCargo(new Cargo(COAL, map));
        
        /* Wait until the mint worker produces a bread */
        assertTrue(mint.getAmount(GOLD) == 1);
        assertTrue(mint.getAmount(COAL) == 1);
        
        Utils.fastForward(150, map);
        
        assertTrue(mint.getAmount(GOLD) == 0);
        assertTrue(mint.getAmount(COAL) == 0);
    }

    @Test
    public void testProductionCountdownStartsWhenIngredientsAreAvailable() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(), point3);

        /* Finish construction of the mint */
        Utils.constructHouse(mint, map);
        
        /* Populate the mint */        
        Worker minter = Utils.occupyBuilding(new Minter(map), mint, map);
        
        /* Fast forward so that the mint worker would have produced bread
           if it had had the ingredients
        */        
        Utils.fastForward(150, map);
        
        assertNull(minter.getCargo());
        
        /* Deliver ingredients to the mint */
        mint.putCargo(new Cargo(GOLD, map));
        mint.putCargo(new Cargo(COAL, map));
        
        /* Verify that it takes 50 steps for the mint worker to produce the planck */
        int i;
        for (i = 0; i < 50; i++) {
            assertNull(minter.getCargo());
            map.stepTime();
        }
        
        assertNotNull(minter.getCargo());
    }

    @Test
    public void testMintWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing mint */
        Point point26 = new Point(8, 8);
        Building mint0 = map.placeBuilding(new Mint(), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(map), mint0, map);

        /* Deliver material to the mint */
        Cargo coalCargo = new Cargo(COAL, map);
        Cargo goldCargo = new Cargo(GOLD, map);
        
        mint0.putCargo(coalCargo);
        mint0.putCargo(coalCargo);

        mint0.putCargo(goldCargo);
        mint0.putCargo(goldCargo);
        
        /* Let the minter rest */
        Utils.fastForward(100, map);

        /* Wait for the minter to produce a new coin cargo */
        Utils.fastForward(50, map);

        Worker ww = mint0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the minter puts the coin cargo at the flag */
        assertEquals(ww.getTarget(), mint0.getFlag().getPosition());
        assertTrue(mint0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, mint0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(mint0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait for the worker to go back to the mint */
        assertEquals(ww.getTarget(), mint0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, mint0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(ww.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(ww.getTarget(), mint0.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, mint0.getFlag().getPosition());
        
        assertNull(ww.getCargo());
        assertEquals(mint0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing mint */
        Point point26 = new Point(8, 8);
        Building mint0 = map.placeBuilding(new Mint(), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Deliver material to the mint */
        Cargo coalCargo = new Cargo(COAL, map);
        Cargo goldCargo = new Cargo(GOLD, map);
        
        mint0.putCargo(coalCargo);
        mint0.putCargo(coalCargo);

        mint0.putCargo(goldCargo);
        mint0.putCargo(goldCargo);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(map), mint0, map);

        /* Let the minter rest */
        Utils.fastForward(100, map);

        /* Wait for the minter to produce a new coin cargo */
        Utils.fastForward(50, map);

        Worker ww = mint0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the minter puts the coin cargo at the flag */
        assertEquals(ww.getTarget(), mint0.getFlag().getPosition());
        assertTrue(mint0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, mint0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(mint0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = mint0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), mint0.getFlag().getPosition());
    
        /* Connect the mint with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(headquarter0.getFlag(), mint0.getFlag());
    
        /* Assign a courier to the road */
        Courier courier = new Courier(map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);
    
        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(mint0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));
    
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
    
        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), mint0.getFlag().getPosition());
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);
        
        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());
        
        int amount = headquarter0.getAmount(COIN);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());
        
        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(COIN), amount + 1);
    }

    @Test
    public void testMinterGoesBackToStorageWhenMintIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing mint */
        Point point26 = new Point(8, 8);
        Building mint0 = map.placeBuilding(new Mint(), point26);

        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(map), mint0, map);
        
        /* Destroy the mint */
        Worker ww = mint0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), mint0.getPosition());

        mint0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(MINTER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());

        /* Verify that the minter is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(MINTER), amount + 1);
    }

    @Test
    public void testMinterGoesBackOnToStorageOnRoadsIfPossibleWhenMintIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing mint */
        Point point26 = new Point(8, 8);
        Building mint0 = map.placeBuilding(new Mint(), point26);

        /* Connect the mint with the headquarter */
        map.placeAutoSelectedRoad(mint0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the mint */
        Utils.constructHouse(mint0, map);

        /* Occupy the mint */
        Utils.occupyBuilding(new Minter(map), mint0, map);
        
        /* Destroy the mint */
        Worker ww = mint0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), mint0.getPosition());

        mint0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        /* Verify that the worker plans to use the roads */
        for (Point p : ww.getPlannedPath()) {
            assertTrue(map.isRoadAtPoint(p));
        }
    }
}
