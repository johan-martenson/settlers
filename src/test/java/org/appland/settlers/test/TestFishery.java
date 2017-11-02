/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Fisherman;
import org.appland.settlers.model.Fishery;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidStateForProduction;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.FISHERMAN;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Tile.Vegetation.MOUNTAIN;
import static org.appland.settlers.model.Tile.Vegetation.WATER;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author johan
 */
public class TestFishery {

    @Test
    public void testFisheryOnlyNeedsTwoPlancksForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fishery */
        Point point22 = new Point(6, 22);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point22);

        /* Deliver two planck and two stone */
        Cargo planckCargo = new Cargo(PLANCK, map);

        fishery0.putCargo(planckCargo);
        fishery0.putCargo(planckCargo);

        /* Verify that this is enough to construct the fishery */
        for (int i = 0; i < 100; i++) {
            assertTrue(fishery0.underConstruction());

            map.stepTime();
        }

        assertTrue(fishery0.ready());
    }

    @Test
    public void testFisheryCannotBeConstructedWithTooFewPlancks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fishery */
        Point point22 = new Point(6, 22);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point22);

        /* Deliver one planck */
        Cargo planckCargo = new Cargo(PLANCK, map);

        fishery0.putCargo(planckCargo);

        /* Verify that this is not enough to construct the fishery */
        for (int i = 0; i < 500; i++) {
            assertTrue(fishery0.underConstruction());

            map.stepTime();
        }

        assertFalse(fishery0.ready());
    }

    @Test
    public void testConstructFisherman() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Fishery f = new Fishery(player0);

        assertTrue(f.underConstruction());

        assertFalse(f.needsWorker());

        Utils.constructHouse(f, map);

        /* Verify that the fishery is unoccupied when it's newly constructed */
        assertTrue(f.needsWorker());

        /* Verify that the Fishery requires a worker */
        assertTrue(f.needsWorker());

        Fisherman fisherman = new Fisherman(player0, map);

        /* Assign worker */
        f.assignWorker(fisherman);

        assertFalse(f.needsWorker());
        assertTrue(f.getWorker().equals(fisherman));
    }

    @Test(expected = Exception.class)
    public void testPromiseWorkerToUnfinishedFishery() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Fishery f = new Fishery(player0);

        assertTrue(f.underConstruction());

        f.promiseWorker(new Fisherman(player0, map));
    }

    @Test(expected = Exception.class)
    public void testAssignWorkerToUnfinishedFisherman() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Fishery f = new Fishery(player0);

        assertTrue(f.underConstruction());

        f.assignWorker(new Fisherman(player0, map));
    }

    @Test(expected = Exception.class)
    public void testAssignWorkerTwice() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Fishery f = new Fishery(player0);

        Utils.constructHouse(f, map);

        f.assignWorker(new Fisherman(player0, map));

        f.assignWorker(new Fisherman(player0, map));
    }

    @Test(expected = Exception.class)
    public void testPromiseWorkerTwice() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Fishery f = new Fishery(player0);

        Utils.constructHouse(f, map);

        f.promiseWorker(new Fisherman(player0, map));

        f.promiseWorker(new Fisherman(player0, map));
    }

    @Test
    public void testFisheryIsNotMilitary() throws Exception {
        Fishery f = new Fishery(null);

        Utils.constructHouse(f, null);

        assertFalse(f.isMilitaryBuilding());
        assertEquals(f.getNumberOfHostedMilitary(), 0);
        assertEquals(f.getMaxHostedMilitary(), 0);
    }

    @Test
    public void testFisheryUnderConstructionNotNeedsWorker() {
        Fishery f = new Fishery(null);

        assertFalse(f.needsWorker());
    }

    @Test
    public void testFishermanIsAssignedToFishery() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(10, 4);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        /* Finish the fisherman hut */
        Utils.constructHouse(fishery, map);

        /* Run game logic twice, once to place courier and once to place fisherman */
        Utils.fastForward(2, map);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Fisherman.class);
    }

    @Test
    public void testOnlyOneFishermanIsAssignedToFishery() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(10, 4);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Connect the fishery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        /* Construct the fisherman hut */
        constructHouse(fishery, map);

        /* Run game logic twice, once to place courier and once to place fisherman */
        Utils.fastForward(2, map);

        assertEquals(map.getWorkers().size(), 3);

        /* Keep running the game loop and make sure no more workers are allocated */
        Utils.fastForward(200, map);

        assertEquals(map.getWorkers().size(), 3);
    }

    @Test
    public void testArrivedFishermanRestsInFisherytAndThenLeaves() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        Utils.setTileToWater(point0, point1, point2, map);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        Point point3 = new Point(10, 4);
        Building fishery = map.placeBuilding(new Fishery(player0), point3);

        /* Construct the fisherman hut */
        constructHouse(fishery, map);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Run the game logic 99 times and make sure the fisherman stays in the fishery */
        for (int i = 0; i < 99; i++) {
            assertTrue(fisherman.isInsideBuilding());
            map.stepTime();
        }

        assertTrue(fisherman.isInsideBuilding());

        /* Step once and make sure the fisherman goes out of the fishery */
        map.stepTime();

        assertFalse(fisherman.isInsideBuilding());
    }

    @Test
    public void testFishermanFindsSpotToFish() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        Utils.setTileToWater(point0, point1, point2, map);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        Point point3 = new Point(6, 6);
        Building fishery = map.placeBuilding(new Fishery(player0), point3);

        /* Construct the fisherman fishery */
        constructHouse(fishery, map);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Let the fisherman rest */
        Utils.fastForward(99, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Step once and make sure the fisherman goes out of the hut */
        map.stepTime();

        assertFalse(fisherman.isInsideBuilding());

        Point point = fisherman.getTarget();
        assertTrue(point.equals(point2) || point.equals(point1) || point.equals(point0));

        assertFalse(map.isBuildingAtPoint(point));
        assertTrue(fisherman.isTraveling());
    }

    @Test
    public void testFishermanReachesPointToFish() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        Utils.setTileToWater(point0, point1, point2, map);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        Point point3 = new Point(7, 5);
        Building fishermanHut = map.placeBuilding(new Fishery(player0), point3);

        /* Construct the fisherman hut */
        constructHouse(fishermanHut, map);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishermanHut, map);

        /* Let the fisherman rest */
        Utils.fastForward(99, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Step once and make sure the fisherman goes out of the house */
        map.stepTime();

        assertFalse(fisherman.isInsideBuilding());

        Point point = fisherman.getTarget();

        assertTrue(point.equals(point2) || point.equals(point1) || point.equals(point0));
        assertTrue(fisherman.isTraveling());

        /* Let the fisherman reach the point */
        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        assertEquals(fisherman.getPosition(), point);
        assertFalse(fisherman.isTraveling());
        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isAt(point));
    }

    @Test
    public void testFishermanFishes() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        Utils.setTileToWater(point0, point1, point2, map);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        Point point3 = new Point(7, 5);
        Building fishermanHut = map.placeBuilding(new Fishery(player0), point3);

        /* Construct the fisherman hut */
        constructHouse(fishermanHut, map);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishermanHut, map);

        /* Let the fisherman rest */
        Utils.fastForward(99, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Step once and make sure the fisherman goes out of the hut */
        map.stepTime();

        assertFalse(fisherman.isInsideBuilding());

        Point point = fisherman.getTarget();

        assertTrue(fisherman.isTraveling());

        /* Let the fisherman reach the spot and start fishing */
        int amountOfFish = map.getAmountFishAtPoint(point);

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isAt(point));
        assertTrue(fisherman.isFishing());

        /* Verify that the fisherman fishes the right time */
        for (int i = 0; i < 19; i++) {
            assertTrue(fisherman.isFishing());
            map.stepTime();
        }

        assertTrue(fisherman.isFishing());
        assertNull(fisherman.getCargo());

        map.stepTime();

        /* Verify that the fisherman is done fishing and that the amount of fish
            has decreased
        */
        assertFalse(fisherman.isFishing());
        assertNotNull(fisherman.getCargo());
        assertEquals(fisherman.getCargo().getMaterial(), FISH);
        assertTrue(map.getAmountFishAtPoint(point) < amountOfFish);
    }

    @Test
    public void testFishermanReturnsHomeAfterFishing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        Utils.setTileToWater(point0, point1, point2, map);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        Point point3 = new Point(7, 5);
        Building fishermanHut = map.placeBuilding(new Fishery(player0), point3);

        /* Construct the fisherman hut */
        constructHouse(fishermanHut, map);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishermanHut, map);

        /* Let the fisherman rest */
        Utils.fastForward(99, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Step once and make sure the fisherman goes out of the house */
        map.stepTime();

        assertFalse(fisherman.isInsideBuilding());

        Point point = fisherman.getTarget();

        assertTrue(fisherman.isTraveling());

        /* Wait for the fisherman to reach the spot */
        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isAt(point));
        assertTrue(fisherman.isFishing());

        /* Wait for the fisherman to get a fish */
        Utils.fastForward(19, map);

        assertTrue(fisherman.isFishing());

        map.stepTime();

        /* Verify that the fisherman goes back home */
        assertFalse(fisherman.isFishing());

        assertEquals(fisherman.getTarget(), fishermanHut.getPosition());
        assertTrue(fisherman.isTraveling());

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isInsideBuilding());
    }

    @Test
    public void testFishermanPlacesFishAtFlag() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        Utils.setTileToWater(point0, point1, point2, map);

        Point hqPoint = new Point(15, 15);
        Building hq = map.placeBuilding(new Headquarter(player0), hqPoint);

        Point point3 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(player0), point3);

        /* Connect the hq with the fishery */
        map.placeAutoSelectedRoad(player0, hq.getFlag(), fishery.getFlag());

        /* Construct the fisherman hut */
        constructHouse(fishery, map);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery, map);

        /* Let the fisherman rest */
        Utils.fastForward(99, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Step once and make sure the fisherman goes out of the house */
        map.stepTime();

        assertFalse(fisherman.isInsideBuilding());

        Point point = fisherman.getTarget();

        assertTrue(fisherman.isTraveling());

        /* Wait for the fisherman to reach the spot */
        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isAt(point));
        assertTrue(fisherman.isFishing());

        /* Wait for the fisherman to get a fish */
        Utils.fastForward(19, map);

        assertTrue(fisherman.isFishing());

        map.stepTime();

        /* Verify that the fisherman goes back home */
        assertFalse(fisherman.isFishing());

        assertEquals(fisherman.getTarget(), fishery.getPosition());
        assertTrue(fisherman.isTraveling());

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isInsideBuilding());

        /* Verify that the fisherman goes out to the flag with the cargo */
        map.stepTime();

        assertEquals(fisherman.getTarget(), fishery.getFlag().getPosition());
        assertTrue(fishery.getFlag().getStackedCargo().isEmpty());
        assertNotNull(fisherman.getCargo());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fisherman.getTarget());

        assertFalse(fishery.getFlag().getStackedCargo().isEmpty());
        assertNull(fisherman.getCargo());

        /* Verify that the fisherman goes back to the house again */
        assertEquals(fisherman.getTarget(), fishery.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        assertTrue(fisherman.isInsideBuilding());
    }

    @Test
    public void testFishermanStaysInsideWhenThereIsNoWaterClose() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        Point point1 = new Point(10, 4);
        Building fishermanHut = map.placeBuilding(new Fishery(player0), point1);

        /* Construct the fisherman hut */
        constructHouse(fishermanHut, map);

        /* Put trees around the fisherman hut */
        for (Point p : map.getPointsWithinRadius(fishermanHut.getPosition(), 4)) {
            if (p.equals(point1)) {
                continue;
            }

            if (map.isBuildingAtPoint(p) || map.isFlagAtPoint(p) || map.isRoadAtPoint(p) || map.isStoneAtPoint(p)) {
                continue;
            }

            map.placeTree(p);
        }

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishermanHut, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Verify that the fisherman stays in the hut */
        for (int i = 0; i < 200; i++) {
            assertTrue(fisherman.isInsideBuilding());
            map.stepTime();
        }
    }

    @Test
    public void testPlaceFisherySoFirstMatchIsMiddleOfLake() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a fish tile */
        Point point0 = new Point(10, 4);
        Point point1 = new Point(8, 4);
        Point point2 = new Point(9, 5);
        map.getTerrain().getTile(point0, point1, point2).setVegetationType(WATER);

        /* Place a fish tile */
        Point point3 = new Point(11, 5);
        map.getTerrain().getTile(point0, point2, point3).setVegetationType(WATER);

        /* Place a fish tile */
        Point point4 = new Point(12, 4);
        map.getTerrain().getTile(point0, point3, point4).setVegetationType(WATER);

        /* Place a fish tile */
        Point point5 = new Point(11, 3);
        map.getTerrain().getTile(point0, point4, point5).setVegetationType(WATER);

        /* Place a fish tile */
        Point point6 = new Point(9, 3);
        map.getTerrain().getTile(point0, point5, point6).setVegetationType(WATER);

        /* Place a fish tile */
        map.getTerrain().getTile(point0, point6, point1).setVegetationType(WATER);

        /* Place a mountain tile */
        Point point7 = new Point(5, 13);
        Point point8 = new Point(3, 13);
        Point point9 = new Point(4, 14);
        map.getTerrain().getTile(point7, point8, point9).setVegetationType(MOUNTAIN);

        /* Place a mountain tile */
        Point point10 = new Point(6, 14);
        map.getTerrain().getTile(point7, point9, point10).setVegetationType(MOUNTAIN);

        /* Place a mountain tile */
        Point point11 = new Point(7, 13);
        map.getTerrain().getTile(point7, point10, point11).setVegetationType(MOUNTAIN);

        /* Place a mountain tile */
        Point point12 = new Point(6, 12);
        map.getTerrain().getTile(point7, point11, point12).setVegetationType(MOUNTAIN);

        /* Place a mountain tile */
        Point point13 = new Point(4, 12);
        map.getTerrain().getTile(point7, point12, point13).setVegetationType(MOUNTAIN);

        /* Place a mountain tile */
        map.getTerrain().getTile(point7, point13, point8).setVegetationType(MOUNTAIN);

        /* Place a mountain tile */
        Point point14 = new Point(8, 14);
        Point point15 = new Point(7, 15);
        map.getTerrain().getTile(point14, point10, point15).setVegetationType(MOUNTAIN);

        /* Place a mountain tile */
        Point point16 = new Point(9, 15);
        map.getTerrain().getTile(point14, point15, point16).setVegetationType(MOUNTAIN);

        /* Place a mountain tile */
        Point point17 = new Point(10, 14);
        map.getTerrain().getTile(point14, point16, point17).setVegetationType(MOUNTAIN);

        /* Place a mountain tile */
        Point point18 = new Point(9, 13);
        map.getTerrain().getTile(point14, point17, point18).setVegetationType(MOUNTAIN);

        /* Place a mountain tile */
        map.getTerrain().getTile(point14, point18, point11).setVegetationType(MOUNTAIN);

        /* Place a mountain tile */
        map.getTerrain().getTile(point14, point11, point10).setVegetationType(MOUNTAIN);

        /* Placing stone */
        Point point19 = new Point(12, 12);
        Stone stone0 = map.placeStone(point19);

        /* Placing stone */
        Point point20 = new Point(13, 11);
        Stone stone1 = map.placeStone(point20);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* 7 ticks from start */
        for (int i = 0; i < 7; i++) {
            map.stepTime();
        }

        /* Placing fishery */
        Point point22 = new Point(10, 8);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point22);

        /* 20 ticks from start */
        for (int i = 0; i < 13; i++) {
            map.stepTime();
        }

        /* Placing road between (11, 7) and (6, 4) */
        Point point23 = new Point(11, 7);
        Point point24 = new Point(10, 6);
        Point point25 = new Point(7, 5);
        Point point26 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point23, point24, point2, point25, point26);

        /* Wait for the fishery to be finished */
        Utils.fastForwardUntilBuildingIsConstructed(fishery0, map);

        /* Wait for the fishery to get occupied */
        Utils.fastForwardUntilBuildingIsOccupied(fishery0, map);

        /* Wait for the fisherman to rest */
        Utils.fastForward(100, map);

        /* Verify that the fisherman leaves the hut */
        Worker fisher = fishery0.getWorker();

        assertNotNull(fisher.getTarget());
    }

    @Test
    public void testFishermanCanRunOutOfFish() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        Utils.setTileToWater(point0, point1, point2, map);

        /* Remove fishes until there is only one left */
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountFishAtPoint(point0) == 1) {
                break;
            }

            map.catchFishAtPoint(point0);
        }

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Building hq = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place fishery */
        Point point3 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(player0), point3);

        /* Place a road from the headquarter to the fishery */
        map.placeAutoSelectedRoad(player0, hq.getFlag(), fishery.getFlag());

        /* Construct the fisherman hut */
        constructHouse(fishery, map);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery, map);

        /* Let the fisherman rest */
        Utils.fastForward(99, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Step once and make sure the fisherman goes out of the hut */
        map.stepTime();

        assertFalse(fisherman.isInsideBuilding());

        Point point = fisherman.getTarget();

        assertTrue(fisherman.isTraveling());

        /* Let the fisherman reach the spot and start fishing */
        int amountOfFish = map.getAmountFishAtPoint(point);

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isAt(point));
        assertTrue(fisherman.isFishing());

        /* Wait for the fisherman to finish fishing */
        assertFalse(fishery.outOfNaturalResources());

        Utils.fastForward(20, map);

        /* Let the fisherman go back to the fishery */
        assertEquals(fisherman.getTarget(), fishery.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        /* Wait for the fisherman to leave the cargo of fish at the flag */
        map.stepTime();

        assertEquals(fisherman.getTarget(), fishery.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getFlag().getPosition());

        assertNull(fisherman.getCargo());
        assertEquals(fisherman.getTarget(), fishery.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        /* Verify that there is no more fish and that the fisherman stays at home */
        assertEquals(map.getAmountFishAtPoint(point0), 0);
        assertEquals(map.getAmountFishAtPoint(point1), 0);
        assertEquals(map.getAmountFishAtPoint(point2), 0);

        for (int i = 0; i < 200; i++) {
            assertTrue(fisherman.isInsideBuilding());
            assertNull(fisherman.getCargo());

            map.stepTime();
        }

        /* Verify that the fishery can run out of fish */
        assertTrue(fishery.outOfNaturalResources());
    }

    @Test
    public void testFisheryWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place fish on one tile */
        Point point0 = new Point(8, 6);
        Point point1 = new Point(10, 6);
        Point point2 = new Point(9, 7);

        Utils.setTileToWater(point0, point1, point2, map);

        /* Placing fishery */
        Point point26 = new Point(8, 8);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Finish construction of the fishery */
        Utils.constructHouse(fishery0, map);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(player0, map), fishery0, map);

        /* Let the fisherman rest */
        Utils.fastForward(100, map);

        /* Wait for the fisherman to produce a new fish cargo */
        Worker fisherman = fishery0.getWorker();

        for (int i = 0; i < 1000; i++) {
            if (fisherman.getCargo() != null && fisherman.isAt(fishery0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(fisherman.getCargo());

        /* Verify that the fisherman puts the fish cargo at the flag */
        map.stepTime();

        assertEquals(fisherman.getTarget(), fishery0.getFlag().getPosition());
        assertTrue(fishery0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery0.getFlag().getPosition());

        assertNull(fisherman.getCargo());
        assertFalse(fishery0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the fishery */
        assertEquals(fisherman.getTarget(), fishery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        for (int i = 0; i < 1000; i++) {
            if (fisherman.getCargo() != null && fisherman.isAt(fishery0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(fisherman.getCargo());

        /* Verify that the second cargo is put at the flag */
        map.stepTime();

        assertEquals(fisherman.getTarget(), fishery0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery0.getFlag().getPosition());

        assertNull(fisherman.getCargo());
        assertEquals(fishery0.getFlag().getStackedCargo().size(), 2);
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

        /* Place fish on one tile */
        Point point0 = new Point(8, 6);
        Point point1 = new Point(10, 6);
        Point point2 = new Point(9, 7);

        Utils.setTileToWater(point0, point1, point2, map);

        /* Placing fishery */
        Point point26 = new Point(8, 8);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Finish construction of the fishery */
        Utils.constructHouse(fishery0, map);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(player0, map), fishery0, map);

        /* Let the fisherman rest */
        Utils.fastForward(100, map);

        /* Wait for the fisherman to produce a new fish cargo */
        Worker fisherman = fishery0.getWorker();

        for (int i = 0; i < 1000; i++) {
            if (fisherman.getCargo() != null && fisherman.isAt(fishery0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(fisherman.getCargo());

        /* Verify that the fisherman puts the fish cargo at the flag */
        map.stepTime();

        assertEquals(fisherman.getTarget(), fishery0.getFlag().getPosition());
        assertTrue(fishery0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery0.getFlag().getPosition());

        assertNull(fisherman.getCargo());
        assertFalse(fishery0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = fishery0.getFlag().getStackedCargo().get(0);

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), fishery0.getFlag().getPosition());

        /* Connect the fishery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fishery0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(fishery0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));


        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), fishery0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FISH);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(FISH), amount + 1);
    }

    @Test
    public void testFishermanGoesBackToStorageWhenFisheryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing fishery */
        Point point26 = new Point(8, 8);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Finish construction of the fishery */
        Utils.constructHouse(fishery0, map);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(player0, map), fishery0, map);

        /* Destroy the fishery */
        Worker fisherman = fishery0.getWorker();

        assertTrue(fisherman.isInsideBuilding());
        assertEquals(fisherman.getPosition(), fishery0.getPosition());

        fishery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(fisherman.isInsideBuilding());
        assertEquals(fisherman.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FISHERMAN);

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, headquarter0.getPosition());

        /* Verify that the fisherman is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(FISHERMAN), amount + 1);
    }

    @Test
    public void testFishermanGoesBackOnToStorageOnRoadsIfPossibleWhenFisheryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing fishery */
        Point point26 = new Point(8, 8);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Connect the fishery with the headquarter */
        map.placeAutoSelectedRoad(player0, fishery0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the fishery */
        Utils.constructHouse(fishery0, map);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(player0, map), fishery0, map);

        /* Destroy the fishery */
        Worker fisherman = fishery0.getWorker();

        assertTrue(fisherman.isInsideBuilding());
        assertEquals(fisherman.getPosition(), fishery0.getPosition());

        fishery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(fisherman.isInsideBuilding());
        assertEquals(fisherman.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point p : fisherman.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test
    public void testProductionInFisheryCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point10 = new Point(10, 6);
        Point point11 = new Point(12, 6);
        Point point12 = new Point(11, 7);

        Utils.setTileToWater(point10, point11, point12, map);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(8, 6);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point1);

        /* Connect the fishery and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the fishery */
        Utils.constructHouse(fishery0, map);

        /* Assign a worker to the fishery */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery0, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the fisherman to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, fisherman);

        assertEquals(fisherman.getCargo().getMaterial(), FISH);

        /* Wait for the worker to return to the fishery */
        assertEquals(fisherman.getTarget(), fishery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery0.getPosition());

        /* Wait for the worker to deliver the cargo */
        map.stepTime();

        assertEquals(fisherman.getTarget(), fishery0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery0.getFlag().getPosition());

        /* Stop production and verify that no fish is produced */
        fishery0.stopProduction();

        assertFalse(fishery0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(fisherman.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInFisheryCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point10 = new Point(10, 6);
        Point point11 = new Point(12, 6);
        Point point12 = new Point(11, 7);

        Utils.setTileToWater(point10, point11, point12, map);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(8, 6);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point1);

        /* Connect the fishery and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the fishery */
        Utils.constructHouse(fishery0, map);

        /* Assign a worker to the fishery */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery0, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the fisherman to produce fish */
        Utils.fastForwardUntilWorkerProducesCargo(map, fisherman);

        assertEquals(fisherman.getCargo().getMaterial(), FISH);

        /* Wait for the worker to return to the fishery */
        assertEquals(fisherman.getTarget(), fishery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery0.getPosition());

        /* Wait for the worker to deliver the cargo */
        map.stepTime();

        assertEquals(fisherman.getTarget(), fishery0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery0.getFlag().getPosition());

        /* Stop production */
        fishery0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(fisherman.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the fishery produces fish again */
        fishery0.resumeProduction();

        assertTrue(fishery0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, fisherman);

        assertNotNull(fisherman.getCargo());
    }

    @Test
    public void testAssignedFishermanHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place fishery */
        Point point1 = new Point(20, 14);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point1);

        /* Finish construction of the fishery */
        Utils.constructHouse(fishery0, map);

        /* Connect the fishery with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fishery0.getFlag());

        /* Wait for fisherman to get assigned and leave the headquarter */
        List<Fisherman> workers = Utils.waitForWorkersOutsideBuilding(Fisherman.class, 1, player0, map);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Fisherman worker = workers.get(0);

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

        /* Place fishery close to the new border */
        Point point4 = new Point(28, 18);
        Fishery fishery0 = map.placeBuilding(new Fishery(player0), point4);

        /* Finish construction of the fishery */
        Utils.constructHouse(fishery0, map);

        /* Occupy the fishery */
        Fisherman worker = Utils.occupyBuilding(new Fisherman(player0, map), fishery0, map);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testFishermanReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing fishery */
        Point point2 = new Point(14, 4);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, fishery0.getFlag());

        /* Wait for the fisherman to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Fisherman.class, 1, player0, map);

        Fisherman fisherman = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Fisherman) {
                fisherman = (Fisherman) w;
            }
        }

        assertNotNull(fisherman);
        assertEquals(fisherman.getTarget(), fishery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the fisherman has started walking */
        assertFalse(fisherman.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the fisherman continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, flag0.getPosition());

        assertEquals(fisherman.getPosition(), flag0.getPosition());

        /* Verify that the fisherman returns to the headquarter when it reaches the flag */
        assertEquals(fisherman.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, headquarter0.getPosition());
    }

    @Test
    public void testFishermanContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing fishery */
        Point point2 = new Point(14, 4);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, fishery0.getFlag());

        /* Wait for the fisherman to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Fisherman.class, 1, player0, map);

        Fisherman fisherman = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Fisherman) {
                fisherman = (Fisherman) w;
            }
        }

        assertNotNull(fisherman);
        assertEquals(fisherman.getTarget(), fishery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the fisherman has started walking */
        assertFalse(fisherman.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the fisherman continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, flag0.getPosition());

        assertEquals(fisherman.getPosition(), flag0.getPosition());

        /* Verify that the fisherman continues to the final flag */
        assertEquals(fisherman.getTarget(), fishery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery0.getFlag().getPosition());

        /* Verify that the fisherman goes out to fisherman instead of going directly back */
        assertNotEquals(fisherman.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testFishermanReturnsToStorageIfFisheryIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing fishery */
        Point point2 = new Point(14, 4);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, fishery0.getFlag());

        /* Wait for the fisherman to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Fisherman.class, 1, player0, map);

        Fisherman fisherman = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Fisherman) {
                fisherman = (Fisherman) w;
            }
        }

        assertNotNull(fisherman);
        assertEquals(fisherman.getTarget(), fishery0.getPosition());

        /* Wait for the fisherman to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, flag0.getPosition());

        map.stepTime();

        /* See that the fisherman has started walking */
        assertFalse(fisherman.isExactlyAtPoint());

        /* Tear down the fishery */
        fishery0.tearDown();

        /* Verify that the fisherman continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery0.getFlag().getPosition());

        assertEquals(fisherman.getPosition(), fishery0.getFlag().getPosition());

        /* Verify that the fisherman goes back to storage */
        assertEquals(fisherman.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testFishermanGoesOffroadBackToClosestStorageWhenFisheryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing fishery */
        Point point26 = new Point(17, 17);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Finish construction of the fishery */
        Utils.constructHouse(fishery0, map);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(player0, map), fishery0, map);

        /* Place a second storage closer to the fishery */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the fishery */
        Worker fisherman = fishery0.getWorker();

        assertTrue(fisherman.isInsideBuilding());
        assertEquals(fisherman.getPosition(), fishery0.getPosition());

        fishery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(fisherman.isInsideBuilding());
        assertEquals(fisherman.getTarget(), storage0.getPosition());

        int amount = storage0.getAmount(FISHERMAN);

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, storage0.getPosition());

        /* Verify that the fisherman is stored correctly in the headquarter */
        assertEquals(storage0.getAmount(FISHERMAN), amount + 1);
    }

    @Test
    public void testFishermanReturnsOffroadAndAvoidsBurningStorageWhenFisheryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing fishery */
        Point point26 = new Point(17, 17);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Finish construction of the fishery */
        Utils.constructHouse(fishery0, map);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(player0, map), fishery0, map);

        /* Place a second storage closer to the fishery */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

        /* Destroy the fishery */
        Worker fisherman = fishery0.getWorker();

        assertTrue(fisherman.isInsideBuilding());
        assertEquals(fisherman.getPosition(), fishery0.getPosition());

        fishery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(fisherman.isInsideBuilding());
        assertEquals(fisherman.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FISHERMAN);

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, headquarter0.getPosition());

        /* Verify that the fisherman is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(FISHERMAN), amount + 1);
    }

    @Test
    public void testFishermanReturnsOffroadAndAvoidsDestroyedStorageWhenFisheryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing fishery */
        Point point26 = new Point(17, 17);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Finish construction of the fishery */
        Utils.constructHouse(fishery0, map);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(player0, map), fishery0, map);

        /* Place a second storage closer to the fishery */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storage0, map);

        /* Destroy the fishery */
        Worker fisherman = fishery0.getWorker();

        assertTrue(fisherman.isInsideBuilding());
        assertEquals(fisherman.getPosition(), fishery0.getPosition());

        fishery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(fisherman.isInsideBuilding());
        assertEquals(fisherman.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FISHERMAN);

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, headquarter0.getPosition());

        /* Verify that the fisherman is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(FISHERMAN), amount + 1);
    }

    @Test
    public void testFishermanReturnsOffroadAndAvoidsUnfinishedStorageWhenFisheryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing fishery */
        Point point26 = new Point(17, 17);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Finish construction of the fishery */
        Utils.constructHouse(fishery0, map);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(player0, map), fishery0, map);

        /* Place a second storage closer to the fishery */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Destroy the fishery */
        Worker fisherman = fishery0.getWorker();

        assertTrue(fisherman.isInsideBuilding());
        assertEquals(fisherman.getPosition(), fishery0.getPosition());

        fishery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(fisherman.isInsideBuilding());
        assertEquals(fisherman.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FISHERMAN);

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, headquarter0.getPosition());

        /* Verify that the fisherman is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(FISHERMAN), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place fishery */
        Point point26 = new Point(17, 17);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Place road to connect the headquarter and the fishery */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fishery0.getFlag());

        /* Finish construction of the fishery */
        Utils.constructHouse(fishery0, map);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Fisherman.class, 1, player0, map).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, fishery0.getFlag().getPosition());

        /* Tear down the building */
        fishery0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquarter instead of entering
        */
        assertEquals(worker.getTarget(), fishery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, fishery0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testFisheryWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(7, 9);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Finish construction of the fishery */
        Utils.constructHouse(fishery, map);

        /* Populate the fishery */
        Worker fisherman0 = Utils.occupyBuilding(new Fisherman(player0, map), fishery, map);

        assertTrue(fisherman0.isInsideBuilding());
        assertEquals(fisherman0.getHome(), fishery);
        assertEquals(fishery.getWorker(), fisherman0);

        /* Verify that the productivity is 0% when the fishery doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(fishery.getFlag().getStackedCargo().isEmpty());
            assertNull(fisherman0.getCargo());
            assertEquals(fishery.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testFisheryWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a fish tile */
        Point point2 = new Point(11, 9);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        /* Place fishery */
        Point point1 = new Point(7, 9);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Finish construction of the fishery */
        Utils.constructHouse(fishery, map);

        /* Populate the fishery */
        Worker fisherman0 = Utils.occupyBuilding(new Fisherman(player0, map), fishery, map);

        assertTrue(fisherman0.isInsideBuilding());
        assertEquals(fisherman0.getHome(), fishery);
        assertEquals(fishery.getWorker(), fisherman0);

        /* Connect the fishery with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fishery.getFlag());

        /* Make the fishery catch some fish with full resources available */
        for (int i = 0; i < 1000; i++) {
            map.stepTime();
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(fishery.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            assertEquals(fishery.getProductivity(), 100);
        }
    }

    @Test
    public void testFisheryLosesProductivityWhenResourcesRunOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a fish tile */
        Point point2 = new Point(11, 9);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        /* Place fishery */
        Point point1 = new Point(7, 9);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Finish construction of the fishery */
        Utils.constructHouse(fishery, map);

        /* Populate the fishery */
        Worker fisherman0 = Utils.occupyBuilding(new Fisherman(player0, map), fishery, map);

        assertTrue(fisherman0.isInsideBuilding());
        assertEquals(fisherman0.getHome(), fishery);
        assertEquals(fishery.getWorker(), fisherman0);

        /* Connect the fishery with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fishery.getFlag());

        /* Make the fishery catch some fish with full resources available */
        for (int i = 0; i < 1000; i++) {
            map.stepTime();
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(fishery.getProductivity(), 100);

        for (int i = 0; i < 10000; i++) {
            map.stepTime();
        }

        assertEquals(fishery.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedFisheryHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a fish tile */
        Point point2 = new Point(11, 9);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        /* Place fishery */
        Point point1 = new Point(7, 9);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Finish construction of the fishery */
        Utils.constructHouse(fishery, map);

        /* Verify that the unoccupied fishery is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(fishery.getProductivity(), 0);

            map.stepTime();
        }
    }
}
