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
import org.appland.settlers.model.Brewer;
import org.appland.settlers.model.Brewery;
import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.BREWER;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
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
public class TestBrewery {
    
    @Test
    public void testBreweryNeedsWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(), point3);

        /* Unfinished samwill doesn't need worker */
        assertFalse(brewery.needsWorker());
        
        /* Finish construction of the brewery */
        Utils.constructMediumHouse(brewery);
        
        assertTrue(brewery.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneBrewerAtStart() {
        Headquarter hq = new Headquarter();
        
        assertTrue(hq.getAmount(BREWER) == 1);
    }
    
    @Test
    public void testBreweryGetsAssignedWorker() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(), point3);

        /* Place a road between the headquarter and the brewery */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the brewery */
        Utils.constructMediumHouse(brewery);
        
        assertTrue(brewery.needsWorker());

        /* Verify that a brewery worker leaves the hq */        
        assertTrue(map.getAllWorkers().size() == 1);

        Utils.fastForward(3, map);
        
        assertTrue(map.getAllWorkers().size() == 3);

        Utils.verifyListContainsWorkerOfType(map.getAllWorkers(), Brewer.class);
        
        /* Let the brewery worker reach the brewery */
        Brewer sw = null;
        
        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Brewer) {
                sw = (Brewer)w;
            }
        }
        
        assertNotNull(sw);
        assertEquals(sw.getTarget(), brewery.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, sw);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), brewery);
        assertEquals(brewery.getWorker(), sw);
    }
    
    @Test
    public void testOccupiedBreweryWithoutWheatAndWaterProducesNothing() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(), point3);

        /* Finish construction of the brewery */
        Utils.constructMediumHouse(brewery);

        /* Occupy the brewery */
        Worker sw = Utils.occupyBuilding(new Brewer(map), brewery, map);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), brewery);
        assertEquals(brewery.getWorker(), sw);        

        /* Verify that the brewery doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(brewery.getFlag().getStackedCargo().isEmpty());
            assertNull(sw.getCargo());
            map.stepTime();
        }
    }
    
    @Test
    public void testUnoccupiedBreweryProducesNothing() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(), point3);

        /* Finish construction of the brewery */
        Utils.constructMediumHouse(brewery);

        /* Verify that the brewery doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(brewery.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedBreweryWithWaterAndWheatProducesBeer() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(), point3);

        /* Finish construction of the brewery */
        Utils.constructMediumHouse(brewery);
        
        /* Occupy the brewery */
        Worker sw = Utils.occupyBuilding(new Brewer(map), brewery, map);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), brewery);
        assertEquals(brewery.getWorker(), sw);        

        /* Deliver wheat and water to the brewery */
        brewery.putCargo(new Cargo(WHEAT, map));
        brewery.putCargo(new Cargo(WATER, map));
        
        /* Verify that the brewery produces beer */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(brewery.getFlag().getStackedCargo().isEmpty());
            assertNull(sw.getCargo());
        }

        map.stepTime();

        assertNotNull(sw.getCargo());
        assertEquals(sw.getCargo().getMaterial(), BEER);
        assertTrue(brewery.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testBrewerLeavesBeerAtTheFlag() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(), point3);

        /* Place a road between the headquarter and the brewery */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish construction of the brewery */
        Utils.constructMediumHouse(brewery);

        /* Occupy the brewery */
        Worker sw = Utils.occupyBuilding(new Brewer(map), brewery, map);
        
        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), brewery);
        assertEquals(brewery.getWorker(), sw);        

        /* Deliver wheat and water to the brewery */
        brewery.putCargo(new Cargo(WHEAT, map));
        brewery.putCargo(new Cargo(WATER, map));
        
        /* Verify that the brewery produces beer */
        int i;
        for (i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(brewery.getFlag().getStackedCargo().isEmpty());
            assertNull(sw.getCargo());
        }

        map.stepTime();
        
        assertNotNull(sw.getCargo());
        assertEquals(sw.getCargo().getMaterial(), BEER);
        assertTrue(brewery.getFlag().getStackedCargo().isEmpty());
        
        /* Verify that the brewery worker leaves the cargo at the flag */
        assertEquals(sw.getTarget(), brewery.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, sw, brewery.getFlag().getPosition());
        
        assertFalse(brewery.getFlag().getStackedCargo().isEmpty());
        assertNull(sw.getCargo());
        assertEquals(sw.getTarget(), brewery.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, sw);
        
        assertTrue(sw.isInsideBuilding());
    }

    @Test
    public void testProductionOfOneBeerConsumesOneWheatAndOneWater() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(), point3);

        /* Finish construction of the brewery */
        Utils.constructMediumHouse(brewery);
        
        /* Occupy the brewery */
        Worker sw = Utils.occupyBuilding(new Brewer(map), brewery, map);
        
        /* Deliver wheat and water to the brewery */
        brewery.putCargo(new Cargo(WHEAT, map));
        brewery.putCargo(new Cargo(WATER, map));
        
        /* Wait until the brewery worker produces an wheat bar */
        assertTrue(brewery.getAmount(WHEAT) == 1);
        assertTrue(brewery.getAmount(WATER) == 1);
        
        Utils.fastForward(150, map);
        
        assertTrue(brewery.getAmount(WHEAT) == 0);
        assertTrue(brewery.getAmount(WATER) == 0);
        assertTrue(brewery.needsMaterial(WHEAT));
        assertTrue(brewery.needsMaterial(WATER));
    }

    @Test
    public void testProductionCountdownStartsWhenWheatAndWaterAreAvailable() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(), point3);

        /* Finish construction of the brewery */
        Utils.constructMediumHouse(brewery);
        
        /* Occupy the brewery */
        Worker sw = Utils.occupyBuilding(new Brewer(map), brewery, map);
        
        /* Fast forward so that the brewer would produced beer
           if it had had wheat and water
        */
        Utils.fastForward(150, map);
        
        assertNull(sw.getCargo());
        
        /* Deliver wheat and water to the brewery */
        brewery.putCargo(new Cargo(WHEAT, map));
        brewery.putCargo(new Cargo(WATER, map));
        
        /* Verify that it takes 50 steps for the brewery worker to produce the wheat bar */
        int i;
        for (i = 0; i < 50; i++) {
            assertNull(sw.getCargo());
            map.stepTime();
        }
        
        assertNotNull(sw.getCargo());
    }

    @Test
    public void testBreweryCannotProduceWithOnlyWheat() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(), point3);

        /* Finish construction of the brewery */
        Utils.constructMediumHouse(brewery);
        
        /* Occupy the brewery */
        Worker sw = Utils.occupyBuilding(new Brewer(map), brewery, map);
        
        /* Deliver wheat but not water to the brewery */
        brewery.putCargo(new Cargo(WHEAT, map));
        
        /* Verify that the wheat founder doesn't produce beer since it doesn't have any water */
        int i;
        for (i = 0; i < 200; i++) {
            assertNull(sw.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testBreweryCannotProduceWithOnlyWater() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* Place brewery */
        Point point3 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(), point3);

        /* Finish construction of the brewery */
        Utils.constructMediumHouse(brewery);
        
        /* Occupy the brewery */
        Worker sw = Utils.occupyBuilding(new Brewer(map), brewery, map);
                
        /* Deliver wheat but not water to the brewery */
        brewery.putCargo(new Cargo(WATER, map));
        
        /* Verify that the wheat founder doesn't produce beer since it doesn't have any water */
        int i;
        for (i = 0; i < 200; i++) {
            assertNull(sw.getCargo());
            map.stepTime();
        }
    }

}
