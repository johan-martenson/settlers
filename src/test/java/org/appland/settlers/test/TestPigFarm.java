/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.PigFarm;
import org.appland.settlers.model.PigBreeder;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.PIG;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
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
public class TestPigFarm {

    @Test
    public void testUnfinishedPigFarmNeedsNoPigBreeder() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place pig farm */
        Point point0 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(), point0);

        assertTrue(farm.underConstruction());
        assertFalse(farm.needsWorker());
    }

    @Test
    public void testFinishedPigFarmNeedsPigBreeder() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place pig farm */
        Point point0 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(), point0);

        Utils.constructLargeHouse(farm);
        
        assertTrue(farm.ready());
        assertTrue(farm.needsWorker());
    }

    @Test
    public void testPigBreederIsAssignedToFinishedPigFarm() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish the pig farm */
        Utils.constructLargeHouse(farm);
        
        /* Run game logic twice, once to place courier and once to place pig breeder */
        Utils.fastForward(2, map);

        /* Verify that there was a pig breeder added */
        assertTrue(map.getAllWorkers().size() == 3);

        Utils.verifyListContainsWorkerOfType(map.getAllWorkers(), PigBreeder.class);
    }

    @Test
    public void testPigBreederRestsInPigFarmThenLeaves() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructLargeHouse(pigFarm);

        PigBreeder pigBreeder = new PigBreeder(map);
        
        Utils.occupyBuilding(pigBreeder, pigFarm, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Run the game logic 99 times and make sure the pig breeder stays in the pig farm */
        int i;
        for (i = 0; i < 99; i++) {
            assertTrue(pigBreeder.isInsideBuilding());
            map.stepTime();
        }
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Step once and make sure the pig breedre goes out of the pig farm */
        map.stepTime();        
        
        assertFalse(pigBreeder.isInsideBuilding());
    }

    @Test
    public void testPigBreederFeedsThePigsWhenItHasResources() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructLargeHouse(pigFarm);

        /* Deliver wheat and water to the farm */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);
        
        pigFarm.putCargo(wheatCargo);
        pigFarm.putCargo(waterCargo);
        
        /* Occupy the pig farm with a pig breeder */
        PigBreeder pigBreeder = new PigBreeder(map);
        
        Utils.occupyBuilding(pigBreeder, pigFarm, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Let the pigBreeder rest */
        Utils.fastForward(99, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Step once and make sure the pigBreeder goes out of the farm */
        map.stepTime();        
        
        assertFalse(pigBreeder.isInsideBuilding());

        Point point = pigBreeder.getTarget();

        assertTrue(pigBreeder.isTraveling());
        
        /* Let the pigBreeder reach the spot and start to feed the pigs */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);
        
        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isAt(point));
        assertTrue(pigBreeder.isFeeding());
        
        int i;
        for (i = 0; i < 19; i++) {
            assertTrue(pigBreeder.isFeeding());
            map.stepTime();
        }

        assertTrue(pigBreeder.isFeeding());
        assertFalse(map.isCropAtPoint(point));

        map.stepTime();
        
        /* Verify that the pigBreeder stopped feeding */
        assertFalse(pigBreeder.isFeeding());
        assertNull(pigBreeder.getCargo());
    }

    @Test
    public void testPigBreederReturnsAfterFeeding() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm = map.placeBuilding(new PigFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructLargeHouse(pigFarm);

        /* Assign a pigBreeder to the farm */
        PigBreeder pigBreeder = new PigBreeder(map);
        
        Utils.occupyBuilding(pigBreeder, pigFarm, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Wait for the pigBreeder to rest */
        Utils.fastForward(99, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Step once to let the pigBreeder go out to plant */
        map.stepTime();        
        
        assertFalse(pigBreeder.isInsideBuilding());

        Point point = pigBreeder.getTarget();

        assertTrue(pigBreeder.isTraveling());
        
        /* Let the pigBreeder reach the intended spot and start to feed */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);
        
        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isAt(point));
        assertTrue(pigBreeder.isFeeding());
        
        /* Wait for the pigBreeder to feed */
        Utils.fastForward(19, map);
        
        assertTrue(pigBreeder.isFeeding());

        map.stepTime();
        
        /* Verify that the pigBreeder stopped feeding and is walking back to the farm */
        assertFalse(pigBreeder.isFeeding());
        assertTrue(pigBreeder.isTraveling());
        assertEquals(pigBreeder.getTarget(), pigFarm.getPosition());
        assertTrue(pigBreeder.getPlannedPath().contains(pigFarm.getFlag().getPosition()));

        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);
        
        assertTrue(pigBreeder.isArrived());        
        assertTrue(pigBreeder.isInsideBuilding());
    }

    @Test
    public void testPigBreederDeliversPigToFlag() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Crop crop = map.placeCrop(point3.upRight().upRight());

        Utils.fastForwardUntilCropIsGrown(crop, map);
        
        Utils.constructLargeHouse(farm);
        
        /* Assign a pigBreeder to the farm */
        PigBreeder pigBreeder = new PigBreeder(map);
        
        Utils.occupyBuilding(pigBreeder, farm, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Let the pigBreeder rest */
        Utils.fastForward(99, map);
        
        assertTrue(pigBreeder.isInsideBuilding());
        
        /* Step once and to let the pigBreeder go out to feed */
        map.stepTime();        
        
        assertFalse(pigBreeder.isInsideBuilding());

        Point point = pigBreeder.getTarget();
        
        assertTrue(pigBreeder.isTraveling());
        
        /* Let the pigBreeder reach the intended spot */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);
        
        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isAt(point));
        assertTrue(pigBreeder.isFeeding());
        
        /* Wait for the pigBreeder to feed the pigs */
        Utils.fastForward(19, map);
        
        assertTrue(pigBreeder.isFeeding());
        
        map.stepTime();
        
        /* PigBreeder is walking back to farm without carrying a cargo */
        assertFalse(pigBreeder.isFeeding());
        assertEquals(pigBreeder.getTarget(), farm.getPosition());
        assertNull(pigBreeder.getCargo());
        
        /* Let the pigBreeder reach the farm */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isInsideBuilding());

        /* Wait for the pig breeder to prepare the pig */
        for (int i = 0; i < 20; i++) {
            assertNull(pigBreeder.getCargo());
            map.stepTime();
        }
        
        /* PigBreeder leaves the building to place the cargo at the flag */
        map.stepTime();
        
        assertFalse(pigBreeder.isInsideBuilding());
        assertTrue(farm.getFlag().getStackedCargo().isEmpty());
        assertNotNull(pigBreeder.getCargo());
        assertEquals(pigBreeder.getCargo().getMaterial(), PIG);
        assertEquals(pigBreeder.getTarget(), farm.getFlag().getPosition());
        
        /* Let the pigBreeder reach the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, farm.getFlag().getPosition());
        
        assertFalse(farm.getFlag().getStackedCargo().isEmpty());
        assertNull(pigBreeder.getCargo());
        
        /* The pigBreeder goes back to the building */
        assertEquals(pigBreeder.getTarget(), farm.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);
        
        assertTrue(pigBreeder.isInsideBuilding());
    }

    @Test
    public void testPigFarmWithoutPigBreederProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Crop crop = map.placeCrop(point3.upRight().upRight());

        Utils.constructLargeHouse(farm);
        
        /* Verify that the farm does not produce any wheat */
        int i;
        for (i = 0; i < 200; i++) {
            assertTrue(farm.getFlag().getStackedCargo().isEmpty());
            
            map.stepTime();
        }

        assertTrue(farm.getFlag().getStackedCargo().isEmpty());
    }
}
