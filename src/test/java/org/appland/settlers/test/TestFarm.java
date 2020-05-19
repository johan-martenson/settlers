/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.Farm;
import org.appland.settlers.model.Farmer;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Storehouse;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static org.appland.settlers.model.Crop.GrowthState.HARVESTED;
import static org.appland.settlers.model.Crop.GrowthState.JUST_PLANTED;
import static org.appland.settlers.model.Material.FARMER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author johan
 */
public class TestFarm {

    @Test
    public void testFarmOnlyNeedsThreePlanksAndThreeStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing farm */
        Point point22 = new Point(6, 12);
        Building farm0 = map.placeBuilding(new Farm(player0), point22);

        /* Deliver three plank and three stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        farm0.putCargo(plankCargo);
        farm0.putCargo(plankCargo);
        farm0.putCargo(plankCargo);
        farm0.putCargo(stoneCargo);
        farm0.putCargo(stoneCargo);
        farm0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the farm */
        for (int i = 0; i < 200; i++) {
            assertTrue(farm0.underConstruction());

            map.stepTime();
        }

        assertTrue(farm0.isReady());
    }

    @Test
    public void testFarmCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing farm */
        Point point22 = new Point(6, 12);
        Building farm0 = map.placeBuilding(new Farm(player0), point22);

        /* Deliver two plank and three stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        farm0.putCargo(plankCargo);
        farm0.putCargo(plankCargo);
        farm0.putCargo(stoneCargo);
        farm0.putCargo(stoneCargo);
        farm0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the farm */
        for (int i = 0; i < 500; i++) {
            assertTrue(farm0.underConstruction());

            map.stepTime();
        }

        assertFalse(farm0.isReady());
    }

    @Test
    public void testFarmCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing farm */
        Point point22 = new Point(6, 12);
        Building farm0 = map.placeBuilding(new Farm(player0), point22);

        /* Deliver three planks and two stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        farm0.putCargo(plankCargo);
        farm0.putCargo(plankCargo);
        farm0.putCargo(plankCargo);
        farm0.putCargo(stoneCargo);
        farm0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the farm */
        for (int i = 0; i < 500; i++) {
            assertTrue(farm0.underConstruction());

            map.stepTime();
        }

        assertFalse(farm0.isReady());
    }

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

        Point point0 = new Point(10, 10);
        Building farm = map.placeBuilding(new Farm(player0), point0);

        assertTrue(farm.underConstruction());
        assertFalse(farm.needsWorker());
    }

    @Test
    public void testFinishedFarmNeedsFarmer() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place farm */
        Point point0 = new Point(10, 10);
        Building farm = map.placeBuilding(new Farm(player0), point0);

        Utils.constructHouse(farm);

        assertTrue(farm.isReady());
        assertTrue(farm.needsWorker());
    }

    @Test
    public void testFarmerIsAssignedToFinishedFarm() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish the forester hut */
        Utils.constructHouse(farm);

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
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(farm);

        Farmer farmer = new Farmer(player0, map);

        Utils.occupyBuilding(farmer, farm);

        assertTrue(farmer.isInsideBuilding());

        /* Run the game logic 99 times and make sure the forester stays in the hut */
        for (int i = 0; i < 99; i++) {
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
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(farm);

        Farmer farmer = new Farmer(player0, map);

        Utils.occupyBuilding(farmer, farm);

        assertTrue(farmer.isInsideBuilding());

        /* Run the game logic 99 times and make sure the forester stays in the hut */
        for (int i = 0; i < 99; i++) {
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

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point3 = new Point(10, 6);
        Farm farm = map.placeBuilding(new Farm(player0), point3);

        /* Connect the farm with the headquarter */
        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the farm */
        Utils.constructHouse(farm);

        /* Occupy the farm */
        Farmer farmer = Utils.occupyBuilding(new Farmer(player0, map), farm);

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

        for (int i = 0; i < 19; i++) {
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
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(farm);

        /* Assign a farmer to the farm */
        Farmer farmer = new Farmer(player0, map);

        Utils.occupyBuilding(farmer, farm);

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
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Crop crop = map.placeCrop(point3.upRight().upRight());

        Utils.constructHouse(farm);

        Utils.fastForwardUntilCropIsGrown(crop, map);

        /* Assign a farmer to the farm */
        Farmer farmer = new Farmer(player0, map);

        Utils.occupyBuilding(farmer, farm);

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

        for (int i = 0; i < 19; i++) {
            assertFalse(farmer.isPlanting());
            assertTrue(farmer.isHarvesting());
            map.stepTime();
        }

        assertTrue(farmer.isHarvesting());
        assertTrue(map.isCropAtPoint(point));
        assertNull(farmer.getCargo());

        map.stepTime();

        /* Verify that the farmer harvested successfully */
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
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Utils.constructHouse(farm);

        /* Wait for the crop to grow */
        Crop crop = map.placeCrop(point3.upRight().upRight());
        Utils.fastForwardUntilCropIsGrown(crop, map);

        /* Assign a farmer to the farm */
        Farmer farmer = new Farmer(player0, map);

        Utils.occupyBuilding(farmer, farm);

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
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Crop crop = map.placeCrop(point3.upRight().upRight());

        Utils.constructHouse(farm);

        /* Verify that the farm does not produce any wheat */
        for (int i = 0; i < 200; i++) {
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
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(farm);

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
        for (Point point : possibleSpotsToPlant) {
            crops.add(map.placeCrop(point));
        }

        /*      ... harvest crops  */
        for (Crop crop : crops) {
            crop.harvest();
            assertEquals(crop.getGrowthState(), HARVESTED);
        }

        /* Assign a farmer to the farm */
        Farmer farmer = new Farmer(player0, map);

        Utils.occupyBuilding(farmer, farm);

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
        for (int i = 0; i < 19; i++) {
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

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 5);

        /* Place a crop */
        Crop crop = map.placeCrop(point0);

        /* Harvest the crop */
        crop.harvest();

        /* Verify that it's possible to place a new crop on the harvested crop */
        map.placeCrop(point0);
    }

    @Test
    public void testHarvestedCropDisappearsEventually() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 5);

        /* Place a crop */
        Crop crop = map.placeCrop(point0);

        /* Harvest the crop */
        crop.harvest();

        /* Verify that the harvested crop disappears after a specific time */
        for (int i = 0; i < 200; i++) {

            /* Verify that the crop is still there */
            assertTrue(map.isCropAtPoint(point0));

            map.stepTime();
        }

        assertFalse(map.isCropAtPoint(point0));
        assertFalse(map.getCrops().iterator().hasNext());
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing farm */
        Point point26 = new Point(8, 8);
        Building farm0 = map.placeBuilding(new Farm(player0), point26);

        /* Finish construction of the farm */
        Utils.constructHouse(farm0);

        /* Occupy the farm */
        Utils.occupyBuilding(new Farmer(player0, map), farm0);

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
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing farm */
        Point point26 = new Point(8, 8);
        Building farm0 = map.placeBuilding(new Farm(player0), point26);

        /* Finish construction of the farm */
        Utils.constructHouse(farm0);

        /* Occupy the farm */
        Utils.occupyBuilding(new Farmer(player0, map), farm0);

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
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), farm0.getFlag().getPosition());
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing farm */
        Point point26 = new Point(8, 8);
        Building farm0 = map.placeBuilding(new Farm(player0), point26);

        /* Finish construction of the farm */
        Utils.constructHouse(farm0);

        /* Occupy the farm */
        Utils.occupyBuilding(new Farmer(player0, map), farm0);

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing farm */
        Point point26 = new Point(8, 8);
        Building farm0 = map.placeBuilding(new Farm(player0), point26);

        /* Connect the farm with the headquarter */
        map.placeAutoSelectedRoad(player0, farm0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the farm */
        Utils.constructHouse(farm0);

        /* Occupy the farm */
        Utils.occupyBuilding(new Farmer(player0, map), farm0);

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
        for (Point point : farmer.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
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
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(12, 8);
        Building farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Connect the farm and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, farm0.getFlag(), headquarter.getFlag());

        /* Finish the farm */
        Utils.constructHouse(farm0);

        /* Assign a worker to the farm */
        Farmer farmer = new Farmer(player0, map);

        Utils.occupyBuilding(farmer, farm0);

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
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(12, 8);
        Building farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Connect the farm and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, farm0.getFlag(), headquarter.getFlag());

        /* Finish the farm */
        Utils.constructHouse(farm0);

        /* Assign a worker to the farm */
        Farmer farmer = new Farmer(player0, map);

        Utils.occupyBuilding(farmer, farm0);

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place farm */
        Point point1 = new Point(20, 14);
        Building farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Finish construction of the farm */
        Utils.constructHouse(farm0);

        /* Connect the farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), farm0.getFlag());

        /* Wait for farmer to get assigned and leave the headquarter */
        List<Farmer> workers = Utils.waitForWorkersOutsideBuilding(Farmer.class, 1, player0);

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
        Headquarter headquarter2 = new Headquarter(player2);
        Point point10 = new Point(70, 70);
        map.placeBuilding(headquarter2, point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 9);
        Building fortress0 = new Fortress(player0);
        map.placeBuilding(fortress0, point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place farm close to the new border */
        Point point4 = new Point(28, 18);
        Farm farm0 = map.placeBuilding(new Farm(player0), point4);

        /* Finish construction of the farm */
        Utils.constructHouse(farm0);

        /* Occupy the farm */
        Farmer worker = Utils.occupyBuilding(new Farmer(player0, map), farm0);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testCannotPlaceBuildingOnGrowingCrop() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 5);

        /* Placing headquarter */
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a crop */
        Point point1 = new Point(10, 10);
        Crop crop = map.placeCrop(point1);

        /* Verify that it's not possible to place a building on the growing crop */
        try {
            Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);
            fail();
        } catch (Exception e) {

        }
    }

    @Test
    public void testNoAvailableBuildingSpaceOnGrowingCrop() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 5);

        /* Placing headquarter */
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a crop */
        Point point1 = new Point(10, 10);
        Crop crop = map.placeCrop(point1);

        /* Verify that there is no available building space on the growing crop */
        assertNull(map.isAvailableHousePoint(player0, point0));
    }

    @Test
    public void testCannotPlaceBuildingOnNewlyHarvestedCrop() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 9);

        /* Placing headquarter */
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a farm */
        Point point1 = new Point(5, 15);
        Building farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Construct the farm */
        Utils.constructHouse(farm0);

        /* Occupy the farm */
        Farmer farmer0 = Utils.occupyBuilding(new Farmer(player0, map), farm0);

        /* Wait for the farmer to plant a crop */
        Crop crop = Utils.waitForFarmerToPlantCrop(map, farmer0);

        /* Let the crop grow fully */
        Utils.waitForCropToGetReady(map, crop);

        /* Wait for the crop to get harvested */
        Utils.waitForCropToGetHarvested(map, crop);

        /* Verify that it's possible to place a building on the growing crop */
        try {
            Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), crop.getPosition());
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testNotAvailableBuildingSpaceOnNewlyHarvestedCrop() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 9);

        /* Placing headquarter */
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a farm */
        Point point1 = new Point(5, 15);
        Building farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Construct the farm */
        Utils.constructHouse(farm0);

        /* Occupy the farm */
        Farmer farmer0 = Utils.occupyBuilding(new Farmer(player0, map), farm0);

        /* Wait for the farmer to plant a crop */
        Crop crop = Utils.waitForFarmerToPlantCrop(map, farmer0);

        /* Let the crop grow fully */
        Utils.waitForCropToGetReady(map, crop);

        /* Wait for the crop to get harvested */
        Utils.waitForCropToGetHarvested(map, crop);

        /* Verify that there is available building space on the growing crop */
        assertNull(map.isAvailableHousePoint(player0, crop.getPosition()));
    }

    @Test
    public void testCanPlaceFlagOnNewlyHarvestedCrop() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Placing headquarter */
        Point point0 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a farm */
        Point point1 = new Point(5, 15);
        Building farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Construct the farm */
        Utils.constructHouse(farm0);

        /* Occupy the farm */
        Farmer farmer0 = Utils.occupyBuilding(new Farmer(player0, map), farm0);

        /* Wait for the farmer to plant a crop */
        Crop crop = null;

        for (int i = 0; i < 1000; i++) {
            crop = Utils.waitForFarmerToPlantCrop(map, farmer0);

            /* Look for a point where there is no flag too close */
            boolean noCloseFlag = true;
            for (Point point : crop.getPosition().getAdjacentPoints()) {
                if (map.isFlagAtPoint(point)) {
                    noCloseFlag = false;
                }
            }

            if (noCloseFlag) {
                break;
            }
        }

        assertNotNull(crop);

        /* Let the crop grow fully */
        Utils.waitForCropToGetReady(map, crop);

        /* Wait for the crop to get harvested */
        Utils.waitForCropToGetHarvested(map, crop);

        /* Verify that it's possible to place a flag on the growing crop */
        Flag flag0 = map.placeFlag(player0, crop.getPosition());
    }

    @Test (expected = Exception.class)
    public void testCannotPlaceFlagOnGrowingCrop() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 5);

        /* Placing headquarter */
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a farm */
        Point point1 = new Point(5, 15);
        Building farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Construct the farm */
        Utils.constructHouse(farm0);

        /* Occupy the farm */
        Farmer farmer0 = Utils.occupyBuilding(new Farmer(player0, map), farm0);

        /* Wait for the farmer to plant a crop */
        Crop crop = null;

        for (int i = 0; i < 1000; i++) {
            crop = Utils.waitForFarmerToPlantCrop(map, farmer0);

            /* Look for a point where there is no flag too close */
            boolean noCloseFlag = true;
            for (Point point : crop.getPosition().getAdjacentPoints()) {
                if (map.isFlagAtPoint(point)) {
                    noCloseFlag = false;
                }
            }

            if (noCloseFlag) {
                break;
            }
        }

        assertNotNull(crop);
        assertTrue(map.isCropAtPoint(crop.getPosition()));
        assertNotEquals(crop.getGrowthState(), Crop.GrowthState.HARVESTED);

        /* Verify that it's possible to place a flag on the growing crop */
        Flag flag0 = map.placeFlag(player0, crop.getPosition());
    }

    @Test
    public void testNoAvailableFlagOnGrowingCrop() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 9);

        /* Placing headquarter */
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a farm */
        Point point1 = new Point(5, 15);
        Building farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Construct the farm */
        Utils.constructHouse(farm0);

        /* Occupy the farm */
        Farmer farmer0 = Utils.occupyBuilding(new Farmer(player0, map), farm0);

        /* Wait for the farmer to plant a crop */
        Crop crop = null;

        for (int i = 0; i < 1000; i++) {
            crop = Utils.waitForFarmerToPlantCrop(map, farmer0);

            /* Look for a point where there is no flag too close */
            boolean noCloseFlag = true;
            for (Point point : crop.getPosition().getAdjacentPoints()) {
                if (map.isFlagAtPoint(point)) {
                    noCloseFlag = false;
                }
            }

            if (noCloseFlag) {
                break;
            }
        }

        assertNotNull(crop);
        assertTrue(map.isCropAtPoint(crop.getPosition()));
        assertNotEquals(crop.getGrowthState(), Crop.GrowthState.HARVESTED);

        /* Verify that it's possible to place a flag on the growing crop */
        assertFalse(map.isAvailableFlagPoint(player0, crop.getPosition()));
    }

    @Test
    public void testAvailableFlagSpaceOnNewlyHarvestedCrop() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 9);

        /* Placing headquarter */
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a farm */
        Point point1 = new Point(5, 15);
        Building farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Construct the farm */
        Utils.constructHouse(farm0);

        /* Occupy the farm */
        Farmer farmer0 = Utils.occupyBuilding(new Farmer(player0, map), farm0);

        /* Wait for the farmer to plant a crop */
        Crop crop = null;

        for (int i = 0; i < 1000; i++) {
            crop = Utils.waitForFarmerToPlantCrop(map, farmer0);

            /* Look for a point where there is no flag too close */
            boolean noCloseFlag = true;
            for (Point point : crop.getPosition().getAdjacentPoints()) {
                if (map.isFlagAtPoint(point)) {
                    noCloseFlag = false;
                }
            }

            if (noCloseFlag) {
                break;
            }
        }

        assertNotNull(crop);

        /* Let the crop grow fully */
        Utils.waitForCropToGetReady(map, crop);

        /* Wait for the crop to get harvested */
        Utils.waitForCropToGetHarvested(map, crop);

        /* Verify that there is available building space on the growing crop */
        assertTrue(map.isAvailableFlagPoint(player0, crop.getPosition()));
    }

    /*
    Test building point available on crop
    crop blocking flag
    building point available with crop blocking flag

    place crop on building
    place crop on flag

    */

    @Test
    public void testFarmerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing farm */
        Point point2 = new Point(14, 4);
        Building farm0 = map.placeBuilding(new Farm(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, farm0.getFlag());

        /* Wait for the farmer to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Farmer.class, 1, player0);

        Farmer farmer = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Farmer) {
                farmer = (Farmer) worker;
            }
        }

        assertNotNull(farmer);
        assertEquals(farmer.getTarget(), farm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the farmer has started walking */
        assertFalse(farmer.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the farmer continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, flag0.getPosition());

        assertEquals(farmer.getPosition(), flag0.getPosition());

        /* Verify that the farmer returns to the headquarter when it reaches the flag */
        assertEquals(farmer.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, headquarter0.getPosition());
    }

    @Test
    public void testFarmerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing farm */
        Point point2 = new Point(14, 4);
        Building farm0 = map.placeBuilding(new Farm(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, farm0.getFlag());

        /* Wait for the farmer to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Farmer.class, 1, player0);

        Farmer farmer = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Farmer) {
                farmer = (Farmer) worker;
            }
        }

        assertNotNull(farmer);
        assertEquals(farmer.getTarget(), farm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the farmer has started walking */
        assertFalse(farmer.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the farmer continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, flag0.getPosition());

        assertEquals(farmer.getPosition(), flag0.getPosition());

        /* Verify that the farmer continues to the final flag */
        assertEquals(farmer.getTarget(), farm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, farm0.getFlag().getPosition());

        /* Verify that the farmer goes out to farmer instead of going directly back */
        assertNotEquals(farmer.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testFarmerReturnsToStorageIfFarmIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing farm */
        Point point2 = new Point(14, 4);
        Building farm0 = map.placeBuilding(new Farm(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, farm0.getFlag());

        /* Wait for the farmer to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Farmer.class, 1, player0);

        Farmer farmer = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Farmer) {
                farmer = (Farmer) worker;
            }
        }

        assertNotNull(farmer);
        assertEquals(farmer.getTarget(), farm0.getPosition());

        /* Wait for the farmer to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, flag0.getPosition());

        map.stepTime();

        /* See that the farmer has started walking */
        assertFalse(farmer.isExactlyAtPoint());

        /* Tear down the farm */
        farm0.tearDown();

        /* Verify that the farmer continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, farm0.getFlag().getPosition());

        assertEquals(farmer.getPosition(), farm0.getFlag().getPosition());

        /* Verify that the farmer goes back to storage */
        assertEquals(farmer.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testFarmerGoesOffroadBackToClosestStorageWhenFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing farm */
        Point point26 = new Point(17, 17);
        Building farm0 = map.placeBuilding(new Farm(player0), point26);

        /* Finish construction of the farm */
        Utils.constructHouse(farm0);

        /* Occupy the farm */
        Utils.occupyBuilding(new Farmer(player0, map), farm0);

        /* Place a second storage closer to the farm */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the farm */
        Worker farmer = farm0.getWorker();

        assertTrue(farmer.isInsideBuilding());
        assertEquals(farmer.getPosition(), farm0.getPosition());

        farm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(farmer.isInsideBuilding());
        assertEquals(farmer.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(FARMER);

        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, storehouse0.getPosition());

        /* Verify that the farmer is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(FARMER), amount + 1);
    }

    @Test
    public void testFarmerReturnsOffroadAndAvoidsBurningStorageWhenFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing farm */
        Point point26 = new Point(17, 17);
        Building farm0 = map.placeBuilding(new Farm(player0), point26);

        /* Finish construction of the farm */
        Utils.constructHouse(farm0);

        /* Occupy the farm */
        Utils.occupyBuilding(new Farmer(player0, map), farm0);

        /* Place a second storage closer to the farm */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

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
    public void testFarmerReturnsOffroadAndAvoidsDestroyedStorageWhenFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing farm */
        Point point26 = new Point(17, 17);
        Building farm0 = map.placeBuilding(new Farm(player0), point26);

        /* Finish construction of the farm */
        Utils.constructHouse(farm0);

        /* Occupy the farm */
        Utils.occupyBuilding(new Farmer(player0, map), farm0);

        /* Place a second storage closer to the farm */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

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
    public void testFarmerReturnsOffroadAndAvoidsUnfinishedStorageWhenFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing farm */
        Point point26 = new Point(17, 17);
        Building farm0 = map.placeBuilding(new Farm(player0), point26);

        /* Finish construction of the farm */
        Utils.constructHouse(farm0);

        /* Occupy the farm */
        Utils.occupyBuilding(new Farmer(player0, map), farm0);

        /* Place a second storage closer to the farm */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

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
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place fam */
        Point point26 = new Point(17, 17);
        Building farm0 = map.placeBuilding(new Farm(player0), point26);

        /* Place road to connect the headquarter and the farm */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), farm0.getFlag());

        /* Finish construction of the farm */
        Utils.constructHouse(farm0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Farmer.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, farm0.getFlag().getPosition());

        /* Tear down the building */
        farm0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquarter instead of entering
        */
        assertEquals(worker.getTarget(), farm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, farm0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testFarmWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(10, 10);
        Building farm = map.placeBuilding(new Farm(player0), point1);

        /* Finish construction of the farm */
        Utils.constructHouse(farm);

        /* Populate the farm */
        Worker farmer0 = Utils.occupyBuilding(new Farmer(player0, map), farm);

        assertTrue(farmer0.isInsideBuilding());
        assertEquals(farmer0.getHome(), farm);
        assertEquals(farm.getWorker(), farmer0);

        /* Verify that the productivity is 0% when the farm doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(farm.getFlag().getStackedCargo().isEmpty());
            assertNull(farmer0.getCargo());
            assertEquals(farm.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testFarmWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(10, 10);
        Building farm = map.placeBuilding(new Farm(player0), point1);

        /* Finish construction of the farm */
        Utils.constructHouse(farm);

        /* Populate the farm */
        Worker farmer0 = Utils.occupyBuilding(new Farmer(player0, map), farm);

        assertTrue(farmer0.isInsideBuilding());
        assertEquals(farmer0.getHome(), farm);
        assertEquals(farm.getWorker(), farmer0);

        /* Connect the farm with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), farm.getFlag());

        /* Make the farm create some wheat with full resources available */
        for (int i = 0; i < 3000; i++) {
            map.stepTime();
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(farm.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {
            map.stepTime();

            assertEquals(farm.getProductivity(), 100);
        }
    }

    @Test
    public void testFarmLosesProductivityWhenResourcesRunOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(10, 10);
        Building farm = map.placeBuilding(new Farm(player0), point1);

        /* Finish construction of the farm */
        Utils.constructHouse(farm);

        /* Populate the farm */
        Worker farmer0 = Utils.occupyBuilding(new Farmer(player0, map), farm);

        assertTrue(farmer0.isInsideBuilding());
        assertEquals(farmer0.getHome(), farm);
        assertEquals(farm.getWorker(), farmer0);

        /* Connect the farm with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), farm.getFlag());

        /* Make the farm create some wheat with full space to plant on available */
        for (int i = 0; i < 3000; i++) {

            map.stepTime();

            if (farm.getProductivity() == 100) {
                break;
            }
        }

        /* Verify that the productivity goes down when there is no space to plant on */
        assertEquals(farm.getProductivity(), 100);

        for (int i = 0; i < 5000; i++) {

            map.stepTime();

            Utils.putStonesOnPoints(map.getPointsWithinRadius(farm.getPosition(), 5), map);

            if (farm.getProductivity() == 0) {
                break;
            }
        }

        assertEquals(farm.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedFarmHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(10, 10);
        Building farm = map.placeBuilding(new Farm(player0), point1);

        /* Finish construction of the farm */
        Utils.constructHouse(farm);

        /* Verify that the unoccupied farm is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(farm.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testFarmCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(7, 9);
        Building farm = map.placeBuilding(new Farm(player0), point1);

        /* Finish construction of the farm */
        Utils.constructHouse(farm);

        /* Populate the farm */
        Worker farmer0 = Utils.occupyBuilding(new Farmer(player0, map), farm);

        /* Verify that the farm can produce */
        assertTrue(farm.canProduce());
    }

    @Test
    public void testFarmReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(6, 12);
        Building farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Construct the farm */
        Utils.constructHouse(farm0);

        /* Verify that the reported output is correct */
        assertEquals(farm0.getProducedMaterial().length, 1);
        assertEquals(farm0.getProducedMaterial()[0], WHEAT);
    }
}
