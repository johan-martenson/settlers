/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Crop;
import static org.appland.settlers.model.Crop.GrowthState.HARVESTED;
import static org.appland.settlers.model.Crop.GrowthState.JUST_PLANTED;
import org.appland.settlers.model.Farm;
import org.appland.settlers.model.Farmer;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
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
public class TestFarm {
    
    @Test
    public void testPlaceCrop() throws Exception {
        GameMap map   = new GameMap(10, 10);
        Point point0 = new Point(3, 3);

        Crop crop = map.placeCrop(point0);
        
        assertNotNull(crop);
        assertTrue(map.isCropAtPoint(point0));
    }
    
    @Test
    public void testCropGrowsOverTime() throws Exception {
        GameMap map   = new GameMap(10, 10);
        Point point0 = new Point(3, 3);

        Crop crop = map.placeCrop(point0);
        
        assertEquals(crop.getGrowthState(), Crop.GrowthState.JUST_PLANTED);
        
        Utils.fastForward(199, map);

        assertEquals(crop.getGrowthState(), Crop.GrowthState.JUST_PLANTED);
        
        map.stepTime();
        
        assertEquals(crop.getGrowthState(), Crop.GrowthState.HALFWAY);
        
        Utils.fastForward(199, map);
                
        assertEquals(crop.getGrowthState(), Crop.GrowthState.HALFWAY);
        
        map.stepTime();
        
        assertEquals(crop.getGrowthState(), Crop.GrowthState.FULL_GROWN);
    }

    @Test
    public void testUnfinishedFarmNeedsNoFarmer() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point0 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(), point0);

        assertTrue(farm.underConstruction());
        assertFalse(farm.needsWorker());
    }

    @Test
    public void testFinishedFarmNeedsFarmer() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point point0 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(), point0);

        Utils.constructLargeHouse(farm);
        
        assertTrue(farm.ready());
        assertTrue(farm.needsWorker());
    }

    @Test
    public void testFarmerIsAssignedToFinishedFarm() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish the forester hut */
        Utils.constructLargeHouse(farm);
        
        /* Run game logic twice, once to place courier and once to place forester */
        Utils.fastForward(2, map);

        /* Verify that there was a farmer added */
        assertTrue(map.getAllWorkers().size() == 3);

        Utils.verifyListContainsWorkerOfType(map.getAllWorkers(), Farmer.class);
    }

    @Test
    public void testFarmerRestsInFarmThenLeaves() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructLargeHouse(farm);

        Farmer farmer = new Farmer(map);
        
        Utils.occupyBuilding(farmer, farm, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Run the game logic 99 times and make sure the forester stays in the hut */
        int i;
        for (i = 0; i < 99; i++) {
            assertTrue(farmer.isInsideBuilding());
            map.stepTime();
        }
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();        
        
        assertFalse(farmer.isInsideBuilding());
    }

    @Test
    public void testFarmerPlantsWhenThereAreFreeSpotsAndNothingToHarvest() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructLargeHouse(farm);

        Farmer farmer = new Farmer(map);
        
        Utils.occupyBuilding(farmer, farm, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Let the farmer rest */
        Utils.fastForward(99, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Step once and make sure the farmer goes out of the farm */
        map.stepTime();        
        
        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();

        assertTrue(farmer.isTraveling());
        
        /* Let the farmer reach the spot and start to plant */
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);
        
        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));
        assertTrue(farmer.isPlanting());
        
        int i;
        for (i = 0; i < 19; i++) {
            assertTrue(farmer.isPlanting());
            map.stepTime();
        }

        assertTrue(farmer.isPlanting());
        assertFalse(map.isCropAtPoint(point));

        map.stepTime();
        
        /* Verify that the farmer stopped planting and there is a crop */
        assertFalse(farmer.isPlanting());
        assertTrue(map.isCropAtPoint(point));
        assertNull(farmer.getCargo());
        assertEquals(map.getCropAtPoint(point).getGrowthState(), JUST_PLANTED);
    }

    @Test
    public void testFarmerReturnsAfterPlanting() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructLargeHouse(farm);

        /* Assign a farmer to the farm */
        Farmer farmer = new Farmer(map);
        
        Utils.occupyBuilding(farmer, farm, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Wait for the farmer to rest */
        Utils.fastForward(99, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Step once to let the farmer go out to plant */
        map.stepTime();        
        
        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();

        assertTrue(farmer.isTraveling());
        
        /* Let the farmer reach the intended spot and start to plant */
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);
        
        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));
        assertTrue(farmer.isPlanting());
        
        /* Wait for the farmer to plant */
        Utils.fastForward(19, map);
        
        assertTrue(farmer.isPlanting());
        assertFalse(map.isCropAtPoint(point));

        map.stepTime();
        
        /* Verify that the farmer stopped planting and is walking back to the farm */
        assertFalse(farmer.isPlanting());
        assertTrue(map.isCropAtPoint(point));
        assertTrue(farmer.isTraveling());
        assertEquals(farmer.getTarget(), farm.getPosition());
        assertTrue(farmer.getPlannedPath().contains(farm.getFlag().getPosition()));

        Utils.fastForwardUntilWorkersReachTarget(map, farmer);
        
        assertTrue(farmer.isArrived());        
        assertTrue(farmer.isInsideBuilding());
    }
    
    @Test
    public void testFarmerHarvestsWhenPossible() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(), point3);

        Crop crop = map.placeCrop(point3.upRight().upRight());

        Utils.constructLargeHouse(farm);

        Utils.fastForwardUntilCropIsGrown(crop, map);
        
        /* Assign a farmer to the farm */
        Farmer farmer = new Farmer(map);
        
        Utils.occupyBuilding(farmer, farm, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Wait for the farmer to rest */
        Utils.fastForward(99, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Step once to let the farmer go out to harvest */
        map.stepTime();        
        
        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();
        
        assertTrue(farmer.isTraveling());
        assertEquals(point, crop.getPosition());
        
        /* Let the farmer reach the crop and start harvesting */
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);
        
        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));
        assertTrue(farmer.isHarvesting());
        assertFalse(farmer.isPlanting());
        
        int i;
        for (i = 0; i < 19; i++) {
            assertFalse(farmer.isPlanting());
            assertTrue(farmer.isHarvesting());
            map.stepTime();
        }

        assertTrue(farmer.isHarvesting());
        assertTrue(map.isCropAtPoint(point));
        assertNull(farmer.getCargo());
        
        map.stepTime();
        
        /* Verify that the farmer harvested successfuly */
        assertFalse(farmer.isHarvesting());
        assertEquals(crop.getGrowthState(), HARVESTED);
        assertNotNull(farmer.getCargo());
        assertEquals(farmer.getCargo().getMaterial(), WHEAT);
    }

    @Test
    public void testFarmerReturnsAfterHarvesting() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Crop crop = map.placeCrop(point3.upRight().upRight());

        Utils.fastForwardUntilCropIsGrown(crop, map);
        
        Utils.constructLargeHouse(farm);
        
        /* Assign a farmer to the farm */
        Farmer farmer = new Farmer(map);
        
        Utils.occupyBuilding(farmer, farm, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Let the farmer rest */
        Utils.fastForward(99, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Step once and to let the farmer go out to plant */
        map.stepTime();        
        
        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();
        
        assertTrue(farmer.isTraveling());
        assertEquals(point, crop.getPosition());
        
        /* Let the farmer reach the intended spot */
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);
        
        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));
        assertTrue(farmer.isHarvesting());
        assertFalse(farmer.isPlanting());
        
        /* Wait for the farmer to plant a new crop */
        Utils.fastForward(19, map);
        
        assertTrue(farmer.isHarvesting());
        
        map.stepTime();
        
        /* Farmer is walking back to farm with cargo of wheat */
        assertFalse(farmer.isHarvesting());
        assertEquals(farmer.getTarget(), farm.getPosition());
        
        /* Let the farmer reach the farm */
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);
        
        assertTrue(farmer.isArrived());
        assertTrue(farmer.isInsideBuilding());
        assertTrue(farm.getFlag().getStackedCargo().isEmpty());
        assertNotNull(farmer.getCargo());

        /* Farmer leaves the building to place the cargo at the flag */
        map.stepTime();
        
        assertFalse(farmer.isInsideBuilding());
        assertTrue(farm.getFlag().getStackedCargo().isEmpty());
        assertNotNull(farmer.getCargo());
        assertEquals(farmer.getTarget(), farm.getFlag().getPosition());
        
        /* Let the farmer reach the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, farm.getFlag().getPosition());
        
        assertFalse(farm.getFlag().getStackedCargo().isEmpty());
        assertNull(farmer.getCargo());
        
        /* The farmer goes back to the building */
        assertEquals(farmer.getTarget(), farm.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);
        
        assertTrue(farmer.isInsideBuilding());
    }
    
    @Test
    public void testFarmerHasMaxFiveCropsAtTheSameTime() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructLargeHouse(farm);
        
        /* Assign a farmer to the farm */
        Farmer farmer = new Farmer(map);
        
        Utils.occupyBuilding(farmer, farm, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Make sure the farmer plants five crops */
        int i;
        for (i = 0; i < 5; i++) {
        
            /* Wait for the farmer to rest */
            Utils.fastForward(100, farmer);

            assertFalse(farmer.isInsideBuilding());

            /* Let farmer reach the point to plant */
            Point point = farmer.getTarget();
            
            assertFalse(map.isCropAtPoint(point));
            assertTrue(farmer.isTraveling());

            Utils.fastForwardUntilWorkersReachTarget(map, farmer);

            assertTrue(farmer.isArrived());
            assertTrue(farmer.isAt(point));
            assertTrue(farmer.isPlanting());
            assertFalse(farmer.isHarvesting());

            /* Wait for farmer to plant */
            Utils.fastForward(20, map);
            
            assertFalse(farmer.isPlanting());
            assertTrue(map.isCropAtPoint(point));

            /* Wait for the farmer to go back to the house */
            assertEquals(farmer.getTarget(), farm.getPosition());

            Utils.fastForwardUntilWorkersReachTarget(map, farmer);

            assertTrue(farmer.isArrived());
            assertTrue(farmer.isInsideBuilding());
        }

        /* Verify that the farmer stays in the hut when there are five crops 
           planted that can not be harvested
        */
        for (i = 0; i < 400; i++) {
            assertTrue(farmer.isInsideBuilding());
            farmer.stepTime();
        }
    }

    @Test
    public void testFarmWithoutFarmerProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(), point3);

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

    @Test
    public void testFarmerPlantsOnHarvestedCrops() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructLargeHouse(farm);

        /* Fill all available points to plant with harvested crops */
        Set<Point> possibleSpotsToPlant = new HashSet<>();
        
        possibleSpotsToPlant.addAll(Arrays.asList(point3.getAdjacentPoints()));
        possibleSpotsToPlant.addAll(Arrays.asList(point3.upLeft().getAdjacentPoints()));
        possibleSpotsToPlant.addAll(Arrays.asList(point3.upRight().getAdjacentPoints()));
        
        possibleSpotsToPlant.remove(point3);
        possibleSpotsToPlant.remove(point3.upLeft());
        possibleSpotsToPlant.remove(point3.upRight());
        
        List<Crop> crops = new ArrayList<>();
        
        /*      ... place crops  */
        for (Point p : possibleSpotsToPlant) {
            crops.add(map.placeCrop(p));
        }
        
        /*      ... harvest crops  */
        for (Crop crop : crops) {
            crop.harvest();
            assertEquals(crop.getGrowthState(), HARVESTED);
        }
        
        /* Assign a farmer to the farm */
        Farmer farmer = new Farmer(map);
        
        Utils.occupyBuilding(farmer, farm, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Wait for the farmer to rest */
        Utils.fastForward(99, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Let the farmer go out to plant */
        map.stepTime();        
        
        /* Check that the farmer goes out to plant */
        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();

        assertTrue(map.isCropAtPoint(point));
        assertEquals(map.getCropAtPoint(point).getGrowthState(), HARVESTED);
        assertTrue(farmer.isTraveling());
        
        /* Let the farmer reach the spot to plant */
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);
        
        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));
        assertTrue(farmer.isPlanting());
        
        /* Verify that the farmer plants */
        int i;
        for (i = 0; i < 19; i++) {
            assertTrue(farmer.isPlanting());
            map.stepTime();
        }

        assertTrue(farmer.isPlanting());
        assertFalse(map.isTreeAtPoint(point));

        map.stepTime();
        
        /* Verify that the farmer stops planting at the correct time */
        assertFalse(farmer.isPlanting());
        assertTrue(map.isCropAtPoint(point));
        assertEquals(map.getCropAtPoint(point).getGrowthState(), JUST_PLANTED);
    }

    @Test
    public void testCropCanBePlacedOnHarvestedCrop() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);

        Crop crop = map.placeCrop(point0);
        crop.harvest();
        
        map.placeCrop(point0);
    }

    @Test(expected = Exception.class)
    public void testCanNotPlaceCropOnNotHarvestedCrop() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);

        Crop crop = map.placeCrop(point0);

        map.placeCrop(point0);        
    }
}
