/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
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
        Utils.constructMediumHouse(mint);
        
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
        Utils.constructMediumHouse(mint);
        
        assertTrue(mint.needsWorker());

        /* Verify that a minter leaves the hq */
        map.stepTime();
        
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
        Utils.constructMediumHouse(mint);
        
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
        Utils.constructMediumHouse(mint);

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
        Utils.constructMediumHouse(mint);
        
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
        Utils.constructMediumHouse(mint);
        
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
        Utils.constructMediumHouse(mint);
        
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
        Utils.constructMediumHouse(mint);
        
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

}
