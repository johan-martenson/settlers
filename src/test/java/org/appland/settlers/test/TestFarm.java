/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Crop;
import static org.appland.settlers.model.Crop.GrowthState.HARVESTED;
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
        
        Utils.fastForward(99, map);

        assertEquals(crop.getGrowthState(), Crop.GrowthState.JUST_PLANTED);
        
        map.stepTime();
        
        assertEquals(crop.getGrowthState(), Crop.GrowthState.HALFWAY);
        
        Utils.fastForward(99, map);
                
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
        assertEquals(farmer.getTarget(), farm.getFlag().getPosition());

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
        
        map.stepTime();
        
        assertFalse(farmer.isHarvesting());
        assertEquals(crop.getGrowthState(), HARVESTED);
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
        
        map.stepTime();
        
        assertFalse(farmer.isHarvesting());
        assertEquals(crop.getGrowthState(), HARVESTED);        

        assertEquals(farmer.getTarget(), farm.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);
        
        assertTrue(farmer.isArrived());
        assertFalse(farmer.isInsideBuilding());
        
        map.stepTime();
        
        assertTrue(farmer.isInsideBuilding());
    }
    
    @Test
    public void testFarmerHasMaxFiveCropsAtTheSameTime() {
        // TODO
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
}
