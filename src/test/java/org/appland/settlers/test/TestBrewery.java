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
import org.appland.settlers.model.Courier;
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

    @Test
    public void testBreweryWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing brewery */
        Point point26 = new Point(8, 8);
        Building brewery0 = map.placeBuilding(new Brewery(), point26);

        /* Finish construction of the brewery */
        Utils.constructMediumHouse(brewery0);

        /* Occupy the brewery */
        Utils.occupyBuilding(new Brewer(map), brewery0, map);

        /* Deliver material to the brewery */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);
        
        brewery0.putCargo(wheatCargo);
        brewery0.putCargo(wheatCargo);

        brewery0.putCargo(waterCargo);
        brewery0.putCargo(waterCargo);
        
        /* Let the brewer rest */
        Utils.fastForward(100, map);

        /* Wait for the brewer to produce a new bread cargo */
        Utils.fastForward(50, map);

        Worker ww = brewery0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the brewer puts the bread cargo at the flag */
        assertEquals(ww.getTarget(), brewery0.getFlag().getPosition());
        assertTrue(brewery0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, brewery0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(brewery0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait for the worker to go back to the brewery */
        assertEquals(ww.getTarget(), brewery0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, brewery0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(ww.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(ww.getTarget(), brewery0.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, brewery0.getFlag().getPosition());
        
        assertNull(ww.getCargo());
        assertEquals(brewery0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing brewery */
        Point point26 = new Point(8, 8);
        Building brewery0 = map.placeBuilding(new Brewery(), point26);

        /* Finish construction of the brewery */
        Utils.constructMediumHouse(brewery0);

        /* Deliver material to the brewery */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);
        
        brewery0.putCargo(wheatCargo);
        brewery0.putCargo(wheatCargo);

        brewery0.putCargo(waterCargo);
        brewery0.putCargo(waterCargo);

        /* Occupy the brewery */
        Utils.occupyBuilding(new Brewer(map), brewery0, map);

        /* Let the brewer rest */
        Utils.fastForward(100, map);

        /* Wait for the brewer to produce a new bread cargo */
        Utils.fastForward(50, map);

        Worker ww = brewery0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the brewer puts the bread cargo at the flag */
        assertEquals(ww.getTarget(), brewery0.getFlag().getPosition());
        assertTrue(brewery0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, brewery0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(brewery0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = brewery0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), brewery0.getFlag().getPosition());
    
        /* Connect the brewery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(headquarter0.getFlag(), brewery0.getFlag());
    
        /* Assign a courier to the road */
        Courier courier = new Courier(map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);
    
        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(brewery0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));
    
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
    
        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), brewery0.getFlag().getPosition());
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);
        
        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());
        
        int amount = headquarter0.getAmount(BEER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());
        
        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(BEER), amount + 1);
    }
}
