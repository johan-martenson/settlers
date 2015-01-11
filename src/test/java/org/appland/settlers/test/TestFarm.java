/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Crop;
import static org.appland.settlers.model.Crop.GrowthState.HARVESTED;
import static org.appland.settlers.model.Crop.GrowthState.JUST_PLANTED;
import org.appland.settlers.model.Farm;
import org.appland.settlers.model.Farmer;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.FARMER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import org.appland.settlers.model.Player;
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);
        Point point0 = new Point(3, 3);

        Crop crop = map.placeCrop(point0);
        
        assertNotNull(crop);
        assertTrue(map.isCropAtPoint(point0));
    }
    
    @Test
    public void testCropGrowsOverTime() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Point point0 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point0);

        assertTrue(farm.underConstruction());
        assertFalse(farm.needsWorker());
    }

    @Test
    public void testFinishedFarmNeedsFarmer() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Point point0 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point0);

        Utils.constructHouse(farm, map);
        
        assertTrue(farm.ready());
        assertTrue(farm.needsWorker());
    }

    @Test
    public void testFarmerIsAssignedToFinishedFarm() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish the forester hut */
        Utils.constructHouse(farm, map);
        
        /* Run game logic twice, once to place courier and once to place forester */
        Utils.fastForward(2, map);

        /* Verify that there was a farmer added */
        assertEquals(map.getWorkers().size(), 3);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Farmer.class);
    }

    @Test
    public void testFarmerRestsInFarmThenLeaves() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(farm, map);

        Farmer farmer = new Farmer(player0, map);
        
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
    public void testFarmerGoesOutViaFlag() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(farm, map);

        Farmer farmer = new Farmer(player0, map);
        
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

        /* Verify that the farmer goes out via the flag */
        assertTrue(farmer.getPlannedPath().contains(farm.getFlag().getPosition()));
    }

    @Test
    public void testFarmerPlantsWhenThereAreFreeSpotsAndNothingToHarvest() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(farm, map);

        Farmer farmer = new Farmer(player0, map);
        
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
    public void testFarmerReturnsViaFlagAfterPlanting() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(farm, map);

        /* Assign a farmer to the farm */
        Farmer farmer = new Farmer(player0, map);
        
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Crop crop = map.placeCrop(point3.upRight().upRight());

        Utils.constructHouse(farm, map);

        Utils.fastForwardUntilCropIsGrown(crop, map);
        
        /* Assign a farmer to the farm */
        Farmer farmer = new Farmer(player0, map);
        
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
    public void testFarmerReturnsViaFlagAfterHarvesting() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Utils.constructHouse(farm, map);

        /* Wait for the crop to grow */
        Crop crop = map.placeCrop(point3.upRight().upRight());
        Utils.fastForwardUntilCropIsGrown(crop, map);

        /* Assign a farmer to the farm */
        Farmer farmer = new Farmer(player0, map);
        
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
    public void testFarmWithoutFarmerProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Crop crop = map.placeCrop(point3.upRight().upRight());

        Utils.constructHouse(farm, map);
        
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(farm, map);

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
        Farmer farmer = new Farmer(player0, map);
        
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 5);

        Crop crop = map.placeCrop(point0);
        crop.harvest();
        
        map.placeCrop(point0);
    }

    @Test(expected = Exception.class)
    public void testCanNotPlaceCropOnNotHarvestedCrop() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 5);

        Crop crop = map.placeCrop(point0);

        map.placeCrop(point0);        
    }

    @Test
    public void testFarmWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing farm */
        Point point26 = new Point(8, 8);
        Building farm0 = map.placeBuilding(new Farm(player0), point26);

        /* Finish construction of the farm */
        Utils.constructHouse(farm0, map);

        /* Occupy the farm */
        Utils.occupyBuilding(new Farmer(player0, map), farm0, map);
        
        /* Let the farmer rest */
        Utils.fastForward(100, map);

        /* Wait for the farmer to plant and harvest a new crop */
        Worker farmer = farm0.getWorker();

        for (int i = 0; i < 1000; i++) {
            if (farmer.getCargo() != null && farmer.getPosition().equals(farm0.getPosition())) {
                break;
            }
        
            map.stepTime();
        }

        assertNotNull(farmer.getCargo());
        
        /* Wait one tick for the farmer to start walking to the flag */
        map.stepTime();
        
        assertEquals(farmer.getTarget(), farm0.getFlag().getPosition());

        /* Verify that the farmer puts the wheat cargo at the flag */
        assertEquals(farmer.getTarget(), farm0.getFlag().getPosition());
        assertTrue(farm0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, farm0.getFlag().getPosition());

        assertNull(farmer.getCargo());
        assertFalse(farm0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait for the worker to go back to the farm */
        assertEquals(farmer.getTarget(), farm0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, farm0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        for (int i = 0; i < 1000; i++) {
            if (farmer.getCargo() != null && farmer.getPosition().equals(farm0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(farmer.getCargo());

        /* Verify that the second cargo is put at the flag */
        map.stepTime();
        
        assertEquals(farmer.getTarget(), farm0.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, farm0.getFlag().getPosition());
        
        assertNull(farmer.getCargo());
        assertEquals(farm0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing farm */
        Point point26 = new Point(8, 8);
        Building farm0 = map.placeBuilding(new Farm(player0), point26);

        /* Finish construction of the farm */
        Utils.constructHouse(farm0, map);

        /* Occupy the farm */
        Utils.occupyBuilding(new Farmer(player0, map), farm0, map);

        /* Let the farmer rest */
        Utils.fastForward(100, map);

        /* Wait for the farmer to produce a new wheat cargo */
        Worker farmer = farm0.getWorker();

        for (int i = 0; i < 1000; i++) {
            if (farmer.getCargo() != null && farmer.getPosition().equals(farm0.getPosition())) {
                break;
            }
        
            map.stepTime();
        }

        assertNotNull(farmer.getCargo());

        /* Verify that the farmer puts the wheat cargo at the flag */
        map.stepTime();
        
        assertEquals(farmer.getTarget(), farm0.getFlag().getPosition());
        assertTrue(farm0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, farm0.getFlag().getPosition());

        assertNull(farmer.getCargo());
        assertFalse(farm0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = farm0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), farm0.getFlag().getPosition());
    
        /* Connect the farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), farm0.getFlag());
    
        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);
    
        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(farm0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));
    
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
    
        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), farm0.getFlag().getPosition());
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);
        
        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());
        
        int amount = headquarter0.getAmount(WHEAT);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(WHEAT), amount + 1);
    }

    @Test
    public void testFarmerGoesBackToStorageWhenFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing farm */
        Point point26 = new Point(8, 8);
        Building farm0 = map.placeBuilding(new Farm(player0), point26);

        /* Finish construction of the farm */
        Utils.constructHouse(farm0, map);

        /* Occupy the farm */
        Utils.occupyBuilding(new Farmer(player0, map), farm0, map);
        
        /* Destroy the farm */
        Worker farmer = farm0.getWorker();
        
        assertTrue(farmer.isInsideBuilding());
        assertEquals(farmer.getPosition(), farm0.getPosition());

        farm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(farmer.isInsideBuilding());
        assertEquals(farmer.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(FARMER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, headquarter0.getPosition());

        /* Verify that the farmer is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(FARMER), amount + 1);
    }

    @Test
    public void testFarmerGoesBackOnToStorageOnRoadsIfPossibleWhenFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing farm */
        Point point26 = new Point(8, 8);
        Building farm0 = map.placeBuilding(new Farm(player0), point26);

        /* Connect the farm with the headquarter */
        map.placeAutoSelectedRoad(player0, farm0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the farm */
        Utils.constructHouse(farm0, map);

        /* Occupy the farm */
        Utils.occupyBuilding(new Farmer(player0, map), farm0, map);
        
        /* Destroy the farm */
        Worker farmer = farm0.getWorker();
        
        assertTrue(farmer.isInsideBuilding());
        assertEquals(farmer.getPosition(), farm0.getPosition());

        farm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(farmer.isInsideBuilding());
        assertEquals(farmer.getTarget(), headquarter0.getPosition());
    
        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point p : farmer.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test
    public void testProductionInFarmCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place farm */
        Point point1 = new Point(8, 6);
        Building farm0 = map.placeBuilding(new Farm(player0), point1);
        
        /* Connect the farm and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the farm */
        Utils.constructHouse(farm0, map);
        
        /* Assign a worker to the farm */
        Farmer farmer = new Farmer(player0, map);
        
        Utils.occupyBuilding(farmer, farm0, map);
        
        assertTrue(farmer.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the farmer to produce leave the farm */
        for (int i = 0; i < 100; i++) {
            if (!farmer.isInsideBuilding()) {
                break;
            }
        
            map.stepTime();
        }
        
        assertFalse(farmer.isInsideBuilding());
        
        /* Wait for the farmer to return to the farm */
        for (int i = 0; i < 200; i++) {
            if (farmer.isInsideBuilding()) {
                break;
            }
        
            map.stepTime();
        }
        
        assertTrue(farmer.isInsideBuilding());
        
        /* Stop production and verify that the farmer stays in the farm */
        farm0.stopProduction();
        
        assertFalse(farm0.isProductionEnabled());
        
        for (int i = 0; i < 300; i++) {
            assertTrue(farmer.isInsideBuilding());
            
            map.stepTime();
        }
    }

    @Test
    public void testProductionInFarmCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place farm */
        Point point1 = new Point(8, 6);
        Building farm0 = map.placeBuilding(new Farm(player0), point1);
        
        /* Connect the farm and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the farm */
        Utils.constructHouse(farm0, map);
        
        /* Assign a worker to the farm */
        Farmer farmer = new Farmer(player0, map);
        
        Utils.occupyBuilding(farmer, farm0, map);
        
        assertTrue(farmer.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the farmer to produce leave the farm */
        for (int i = 0; i < 100; i++) {
            if (!farmer.isInsideBuilding()) {
                break;
            }
        
            map.stepTime();
        }
        
        assertFalse(farmer.isInsideBuilding());
        
        /* Wait for the farmer to return to the farm */
        for (int i = 0; i < 200; i++) {
            if (farmer.isInsideBuilding()) {
                break;
            }
        
            map.stepTime();
        }
        
        assertTrue(farmer.isInsideBuilding());

        /* Stop production */
        farm0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertTrue(farmer.isInsideBuilding());
            
            map.stepTime();
        }

        /* Resume production and verify that the farmer leaves the farm */
        farm0.resumeProduction();

        assertTrue(farm0.isProductionEnabled());

        for (int i = 0; i < 200; i++) {
            if (!farmer.isInsideBuilding()) {
                break;
            }
            
            map.stepTime();
        }
    
        assertFalse(farmer.isInsideBuilding());
    }

    @Test
    public void testAssignedFarmerHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place farm*/
        Point point1 = new Point(20, 14);
        Building farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Finish construction of the farm */
        Utils.constructHouse(farm0, map);
        
        /* Connect the farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), farm0.getFlag());

        /* Wait for farmer to get assigned and leave the headquarter */
        List<Farmer> workers = Utils.waitForWorkersOutsideBuilding(Farmer.class, 1, player0, map);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Farmer worker = workers.get(0);

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);
        Player player2 = new Player("Player 2", RED);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 2's headquarter */
        Building headquarter2 = new Headquarter(player2);
        Point point10 = new Point(70, 70);
        map.placeBuilding(headquarter2, point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = new Fortress(player0);
        map.placeBuilding(fortress0, point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0, map);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0, map);

        /* Place farm close to the new border */
        Point point4 = new Point(28, 18);
        Farm farm0 = map.placeBuilding(new Farm(player0), point4);

        /* Finish construction of the farm */
        Utils.constructHouse(farm0, map);

        /* Occupy the farm */
        Farmer worker = Utils.occupyBuilding(new Farmer(player0, map), farm0, map);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down*/
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }
}
