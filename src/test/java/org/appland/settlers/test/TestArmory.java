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
import static org.appland.settlers.model.Material.ARMORER;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Armory;
import org.appland.settlers.model.Armorer;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.SHIELD;
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
public class TestArmory {
    
    @Test
    public void testArmoryNeedsWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Unfinished armory doesn't need worker */
        assertFalse(armory.needsWorker());
        
        /* Finish construction of the armory */
        Utils.constructMediumHouse(armory);
        
        assertTrue(armory.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneArmorerAtStart() {
        Headquarter hq = new Headquarter();
        
        assertTrue(hq.getAmount(ARMORER) == 1);
    }
    
    @Test
    public void testArmoryGetsAssignedWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the armory */
        Utils.constructMediumHouse(armory);
        
        assertTrue(armory.needsWorker());

        /* Verify that a armory worker leaves the hq */
        map.stepTime();
        
        assertTrue(map.getAllWorkers().size() == 3);

        /* Let the armory worker reach the armory */
        Armorer armorer = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Armorer) {
                armorer = (Armorer)w;
            }
        }

        assertNotNull(armorer);
        assertEquals(armorer.getTarget(), armory.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, armorer);
        
        assertTrue(armorer.isInsideBuilding());
        assertEquals(armorer.getHome(), armory);
        assertEquals(armory.getWorker(), armorer);
    }
    
    @Test
    public void testOccupiedArmoryWithoutCoalAndIronProducesNothing() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(), point3);

        /* Finish construction of the armory */
        Utils.constructMediumHouse(armory);
        
        /* Populate the armory */
        Worker armorer = Utils.occupyBuilding(new Armorer(map), armory, map);
        
        assertTrue(armorer.isInsideBuilding());
        assertEquals(armorer.getHome(), armory);
        assertEquals(armory.getWorker(), armorer);        

        /* Verify that the armory doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(armory.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer.getCargo());
            map.stepTime();
        }
    }
    
    @Test
    public void testUnoccupiedArmoryProducesNothing() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(), point3);

        /* Finish construction of the armory */
        Utils.constructMediumHouse(armory);

        /* Verify that the armory doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(armory.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedArmoryWithCoalAndIronProducesWeapon() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the armory */
        Utils.constructMediumHouse(armory);
        
        /* Populate the armory */        
        Worker armorer = Utils.occupyBuilding(new Armorer(map), armory, map);
        
        assertTrue(armorer.isInsideBuilding());
        assertEquals(armorer.getHome(), armory);
        assertEquals(armory.getWorker(), armorer);        

        /* Deliver material to the armory */
        armory.putCargo(new Cargo(IRON, map));
        armory.putCargo(new Cargo(COAL, map));
        
        /* Verify that the armory produces weapons */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(armory.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer.getCargo());
        }

        map.stepTime();
        
        assertNotNull(armorer.getCargo());
        assertEquals(armorer.getCargo().getMaterial(), SWORD);
        assertTrue(armory.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testArmorerLeavesWeaponAtTheFlag() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the armory */
        Utils.constructMediumHouse(armory);
        
        /* Populate the armory */        
        Worker armorer = Utils.occupyBuilding(new Armorer(map), armory, map);
        
        assertTrue(armorer.isInsideBuilding());
        assertEquals(armorer.getHome(), armory);
        assertEquals(armory.getWorker(), armorer);        

        /* Deliver ingredients to the armory */
        armory.putCargo(new Cargo(IRON, map));
        armory.putCargo(new Cargo(COAL, map));
        
        /* Verify that the armory produces weapons */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(armory.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer.getCargo());
        }

        map.stepTime();
        
        assertNotNull(armorer.getCargo());
        assertEquals(armorer.getCargo().getMaterial(), SWORD);
        assertTrue(armory.getFlag().getStackedCargo().isEmpty());
        
        /* Verify that the armory worker leaves the cargo at the flag */
        assertEquals(armorer.getTarget(), armory.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, armorer, armory.getFlag().getPosition());
        
        assertFalse(armory.getFlag().getStackedCargo().isEmpty());
        assertNull(armorer.getCargo());
        assertEquals(armorer.getTarget(), armory.getPosition());
        
        /* Verify that the armorer goes back to the armory */
        Utils.fastForwardUntilWorkersReachTarget(map, armorer);
        
        assertTrue(armorer.isInsideBuilding());
    }

    @Test
    public void testProductionOfOneSwordConsumesOneCoalAndOneIron() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(), point3);

        /* Finish construction of the armory */
        Utils.constructMediumHouse(armory);
        
        /* Populate the armory */        
        Worker armorer = Utils.occupyBuilding(new Armorer(map), armory, map);
        
        /* Deliver ingredients to the armory */
        armory.putCargo(new Cargo(IRON, map));
        armory.putCargo(new Cargo(COAL, map));
        
        /* Wait until the armory worker produces a weapons */
        assertTrue(armory.getAmount(IRON) == 1);
        assertTrue(armory.getAmount(COAL) == 1);
        
        Utils.fastForward(150, map);
        
        assertTrue(armory.getAmount(COAL) == 0);
        assertTrue(armory.getAmount(IRON) == 0);
    }

    @Test
    public void testProductionCountdownStartsWhenMaterialsAreAvailable() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(), point3);

        /* Finish construction of the armory */
        Utils.constructMediumHouse(armory);
        
        /* Populate the armory */        
        Worker armorer = Utils.occupyBuilding(new Armorer(map), armory, map);
        
        /* Fast forward so that the armory worker would have produced weapons
           if it had had the ingredients
        */        
        Utils.fastForward(150, map);
        
        assertNull(armorer.getCargo());
        
        /* Deliver ingredients to the armory */
        armory.putCargo(new Cargo(IRON, map));
        armory.putCargo(new Cargo(COAL, map));
        
        /* Verify that it takes 50 steps for the armory worker to produce the planck */
        int i;
        for (i = 0; i < 50; i++) {
            assertNull(armorer.getCargo());
            map.stepTime();
        }
        
        assertNotNull(armorer.getCargo());
    }

    @Test
    public void testArmoryShiftsBetweenProducingSwordsAndShields() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 52 ticks from start */
        Point point3 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(), point3);

        /* 64 ticks from start */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the armory */
        Utils.constructMediumHouse(armory);
        
        /* Populate the armory */        
        Worker armorer = Utils.occupyBuilding(new Armorer(map), armory, map);
        
        assertTrue(armorer.isInsideBuilding());
        assertEquals(armorer.getHome(), armory);
        assertEquals(armory.getWorker(), armorer);        

        /* Deliver material to the armory */
        armory.putCargo(new Cargo(IRON, map));
        armory.putCargo(new Cargo(IRON, map));
        armory.putCargo(new Cargo(COAL, map));
        armory.putCargo(new Cargo(COAL, map));
        
        /* Verify that the armory produces a sword*/
        Utils.fastForward(150, map);
        
        assertNotNull(armorer.getCargo());
        assertEquals(armorer.getCargo().getMaterial(), SWORD);
        
        /* Wait for the armorer to put the sword at the flag */
        assertEquals(armorer.getTarget(), armory.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, armorer, armory.getFlag().getPosition());
        
        /* Wait for the armorer to go back to the armory */
        assertEquals(armorer.getTarget(), armory.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, armorer);
        
        /* Verify that the armorer produces a shield */
        Utils.fastForward(150, map);
        
        assertEquals(armorer.getCargo().getMaterial(), SHIELD);
    }
}
