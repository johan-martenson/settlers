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
import static org.appland.settlers.model.Crop.GrowthState.FULL_GROWN;
import static org.appland.settlers.model.Crop.GrowthState.HARVESTED;
import static org.appland.settlers.model.Crop.GrowthState.JUST_PLANTED;
import org.appland.settlers.model.Farm;
import org.appland.settlers.model.Farmer;
import org.appland.settlers.model.GameLogic;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Material;
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
        Point point0 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(), point0);

        assertTrue(farm.getConstructionState() == Building.ConstructionState.UNDER_CONSTRUCTION);
        assertFalse(farm.needsWorker());
        assertFalse(farm.needsWorker(Material.FARMER));        
    }

    @Test
    public void testFinishedFarmNeedsFarmer() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(), point0);

        Utils.constructLargeHouse(farm);
        
        assertTrue(farm.getConstructionState() == Building.ConstructionState.DONE);
        assertTrue(farm.needsWorker());
        assertTrue(farm.needsWorker(Material.FARMER));
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

        GameLogic gameLogic = new GameLogic();

        /* Finish the forester hut */
        Utils.constructLargeHouse(farm);
        
        /* Run game logic twice, once to place courier and once to place forester */
        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);
        
        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);

        List<Worker> workers = map.getAllWorkers();
        assertTrue(workers.size() == 2);
        assertTrue(workers.get(0) instanceof Farmer || workers.get(1) instanceof Farmer);
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
        
        map.placeWorker(farmer, farm.getFlag());
        farm.assignWorker(farmer);
        farmer.enterBuilding(farm);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();
        
        int i;
        for (i = 0; i < 9; i++) {
            assertTrue(farmer.isInsideBuilding());
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
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
        
        map.placeWorker(farmer, farm.getFlag());
        farm.assignWorker(farmer);
        farmer.enterBuilding(farm);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();
        
        int i;
        for (i = 0; i < 9; i++) {
            assertTrue(farmer.isInsideBuilding());
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();        
        
        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();

        assertTrue(farmer.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);
        
        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));

        map.stepTime();
        
        assertTrue(farmer.isPlanting());
        
        for (i = 0; i < 19; i++) {
            assertTrue(farmer.isPlanting());
            gameLogic.gameLoop(map);
            map.stepTime();
        }

        assertTrue(farmer.isPlanting());
        assertFalse(map.isTreeAtPoint(point));

        map.stepTime();
        
        assertFalse(farmer.isPlanting());
        assertTrue(map.isCropAtPoint(point));
        assertNull(farmer.getCargo());
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

        Farmer farmer = new Farmer(map);
        
        map.placeWorker(farmer, farm.getFlag());
        farm.assignWorker(farmer);
        farmer.enterBuilding(farm);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();
        
        int i;
        for (i = 0; i < 9; i++) {
            assertTrue(farmer.isInsideBuilding());
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();        
        
        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();

        assertTrue(farmer.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);
        
        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));

        map.stepTime();
        
        assertTrue(farmer.isPlanting());
        
        for (i = 0; i < 19; i++) {
            assertTrue(farmer.isPlanting());
            gameLogic.gameLoop(map);
            map.stepTime();
        }

        assertTrue(farmer.isPlanting());
        assertFalse(map.isCropAtPoint(point));

        map.stepTime();
        
        assertFalse(farmer.isPlanting());
        assertTrue(map.isCropAtPoint(point));
        assertTrue(farmer.isTraveling());
        assertEquals(farmer.getTarget(), farm.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, farmer);
        
        assertTrue(farmer.isArrived());
        
        map.stepTime();
        
        assertTrue(farmer.isInsideBuilding());
    }
    
    @Test
    public void testFarmerHarvestsWhenPossible() throws Exception {
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

        int i;
        for (i = 0; i < 500; i++) {
            if (crop.getGrowthState() == FULL_GROWN) {
                break;
            }

            map.stepTime();
        }
        
        assertEquals(crop.getGrowthState(), FULL_GROWN);
        
        Farmer farmer = new Farmer(map);
        
        map.placeWorker(farmer, farm.getFlag());
        farm.assignWorker(farmer);
        farmer.enterBuilding(farm);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();
        
        for (i = 0; i < 9; i++) {
            assertTrue(farmer.isInsideBuilding());
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();        
        
        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();
        
        assertTrue(farmer.isTraveling());
        assertEquals(point, crop.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);
        
        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));

        map.stepTime();
        
        assertFalse(farmer.isPlanting());
        assertTrue(farmer.isHarvesting());
        
        for (i = 0; i < 19; i++) {
            assertFalse(farmer.isPlanting());
            assertTrue(farmer.isHarvesting());
            gameLogic.gameLoop(map);
            map.stepTime();
        }

        assertTrue(farmer.isHarvesting());
        assertTrue(map.isCropAtPoint(point));
        assertNull(farmer.getCargo());
        
        map.stepTime();
        
        assertFalse(farmer.isHarvesting());
        assertEquals(crop.getGrowthState(), HARVESTED);
        assertNotNull(farmer.getCargo());
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

        Utils.constructLargeHouse(farm);

        int i;
        for (i = 0; i < 500; i++) {
            if (crop.getGrowthState() == Crop.GrowthState.FULL_GROWN) {
                break;
            }

            map.stepTime();
        }
        
        Farmer farmer = new Farmer(map);
        
        map.placeWorker(farmer, farm.getFlag());
        farm.assignWorker(farmer);
        farmer.enterBuilding(farm);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();
        
        for (i = 0; i < 9; i++) {
            assertTrue(farmer.isInsideBuilding());
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();        
        
        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();
        
        assertTrue(farmer.isTraveling());
        assertEquals(point, crop.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);
        
        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));

        map.stepTime();
        
        assertFalse(farmer.isPlanting());
        assertTrue(farmer.isHarvesting());
        
        for (i = 0; i < 19; i++) {
            assertFalse(farmer.isPlanting());
            assertTrue(farmer.isHarvesting());
            gameLogic.gameLoop(map);
            map.stepTime();
        }

        assertTrue(farmer.isHarvesting());
        assertTrue(map.isCropAtPoint(point));
        assertNull(farmer.getCargo());
        
        map.stepTime();
        
        assertFalse(farmer.isHarvesting());
        assertEquals(crop.getGrowthState(), HARVESTED);
        assertNotNull(farmer.getCargo());

        assertEquals(farmer.getTarget(), farm.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);
        
        assertTrue(farmer.isArrived());
        assertFalse(farmer.isInsideBuilding());
        
        map.stepTime();
        
        assertTrue(farmer.isInsideBuilding());
        assertNull(farmer.getCargo());
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
        
        Farmer farmer = new Farmer(map);
        
        map.placeWorker(farmer, farm.getFlag());
        farm.assignWorker(farmer);
        farmer.enterBuilding(farm);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Make sure the farmer plants five crops */
        int j;
        for (j = 0; j < 5; j++) {
        
            /* Run the game logic 10 times and make sure the forester stays in the hut */
            GameLogic gameLogic = new GameLogic();

            int i;
            for (i = 0; i < 9; i++) {
                assertTrue(farmer.isInsideBuilding());
                gameLogic.gameLoop(map);
                Utils.fastForward(10, farmer);
            }

            Utils.fastForward(9, farmer);

            assertTrue(farmer.isInsideBuilding());

            /* Step once and make sure the forester goes out of the hut */
            farmer.stepTime();        

            assertFalse(farmer.isInsideBuilding());

            Point point = farmer.getTarget();
            
            assertFalse(map.isCropAtPoint(point));

            assertTrue(farmer.isTraveling());

            Utils.fastForwardUntilWorkersReachTarget(map, farmer);

            assertTrue(farmer.isArrived());
            assertTrue(farmer.isAt(point));

            /* Farmer starts to plant */
            map.stepTime();

            assertTrue(farmer.isPlanting());
            assertFalse(farmer.isHarvesting());

            for (i = 0; i < 19; i++) {
                assertTrue(farmer.isPlanting());
                assertFalse(farmer.isHarvesting());
                gameLogic.gameLoop(map);
                farmer.stepTime();
            }

            assertTrue(farmer.isPlanting());
            assertFalse(map.isCropAtPoint(point));

            /* Farmer goes back to house */
            farmer.stepTime();

            assertFalse(farmer.isHarvesting());
            assertTrue(map.isCropAtPoint(point));

            assertEquals(farmer.getTarget(), farm.getPosition());

            Utils.fastForwardUntilWorkersReachTarget(map, farmer);

            assertTrue(farmer.isArrived());
            assertFalse(farmer.isInsideBuilding());

            farmer.stepTime();

            assertTrue(farmer.isInsideBuilding());
        }

        /* Verify that the farmer stays in the hut when there are five crops 
           planted that can not be harvested
        */
        int i;
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

        
        /* Run the game logic 20 times and make sure the farm doesn't produce any wheat */
        GameLogic gameLogic = new GameLogic();
        
        int i;
        for (i = 0; i < 20; i++) {
            assertFalse(farm.isCargoReady());
            
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        assertFalse(farm.isCargoReady());        
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
        
        /* Place farmer */
        Farmer farmer = new Farmer(map);
        
        map.placeWorker(farmer, farm.getFlag());
        farm.assignWorker(farmer);
        farmer.enterBuilding(farm);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();
        
        int i;
        for (i = 0; i < 9; i++) {
            assertTrue(farmer.isInsideBuilding());
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();        
        
        /* Check that the farmer goes out to plant */
        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();

        assertTrue(map.isCropAtPoint(point));
        assertEquals(map.getCropAtPoint(point).getGrowthState(), HARVESTED);
        assertTrue(farmer.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);
        
        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));

        map.stepTime();
        
        assertTrue(farmer.isPlanting());
        
        for (i = 0; i < 19; i++) {
            assertTrue(farmer.isPlanting());
            gameLogic.gameLoop(map);
            map.stepTime();
        }

        assertTrue(farmer.isPlanting());
        assertFalse(map.isTreeAtPoint(point));

        map.stepTime();
        
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
