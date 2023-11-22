/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.CoalMine;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Fisherman;
import org.appland.settlers.model.Fishery;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GoldMine;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.StoneType;
import org.appland.settlers.model.Storehouse;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.TreeSize;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.awt.Color.*;
import static org.appland.settlers.model.DetailedVegetation.WATER;
import static org.appland.settlers.model.DetailedVegetation.*;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.appland.settlers.test.Utils.occupyBuilding;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestFishery {

    // FIXME: test fishing in different types of water! Upwards and downwards facing triangles

    @Test
    public void testFisheryOnlyNeedsTwoPlanksForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place fishery */
        Point point22 = new Point(6, 12);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point22);

        /* Deliver two plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);

        fishery0.putCargo(plankCargo);
        fishery0.putCargo(plankCargo);

        /* Assign builder */
        Utils.assignBuilder(fishery0);

        /* Verify that this is enough to construct the fishery */
        for (int i = 0; i < 100; i++) {
            assertTrue(fishery0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(fishery0.isReady());
    }

    @Test
    public void testFisheryCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place fishery */
        Point point22 = new Point(6, 12);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point22);

        /* Deliver one plank */
        Cargo plankCargo = new Cargo(PLANK, map);

        fishery0.putCargo(plankCargo);

        /* Assign builder */
        Utils.assignBuilder(fishery0);

        /* Verify that this is not enough to construct the fishery */
        for (int i = 0; i < 500; i++) {
            assertTrue(fishery0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(fishery0.isReady());
    }

    @Test
    public void testConstructFisherman() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(10, 10);
        Fishery fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        assertTrue(fishery.isPlanned());
        assertFalse(fishery.needsWorker());

        /* Wait for the fishery to get constructed */
        Utils.waitForBuildingToBeConstructed(fishery);

        /* Verify that the fishery is unoccupied when it's newly constructed */
        assertTrue(fishery.needsWorker());

        /* Verify that the Fishery requires a worker */
        assertTrue(fishery.needsWorker());

        Fisherman fisherman = new Fisherman(player0, map);

        /* Assign worker */
        fishery.assignWorker(fisherman);

        assertFalse(fishery.needsWorker());
        assertEquals(fishery.getWorker(), fisherman);
    }

    @Test
    public void testPromiseWorkerToUnfinishedFishery() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Fishery fishery = new Fishery(player0);

        assertTrue(fishery.isPlanned());

        try {
            fishery.promiseWorker(new Fisherman(player0, map));

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testAssignWorkerToUnfinishedFisherman() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Fishery fishery = new Fishery(player0);

        assertTrue(fishery.isPlanned());

        try {
            fishery.assignWorker(new Fisherman(player0, map));

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testAssignWorkerTwice() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(10, 10);
        Fishery fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        /* Wait for the fishery to get constructed */
        Utils.waitForBuildingToBeConstructed(fishery);

        /* Wait for the fishery to get occupied */
        Fisherman fisherman = (Fisherman) Utils.waitForNonMilitaryBuildingToGetPopulated(fishery);

        assertTrue(fisherman.isInsideBuilding());

        /* Verify that it's not possible to assign a worker again */
        try {
            fishery.assignWorker(new Fisherman(player0, map));

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPromiseWorkerTwice() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(10, 10);
        Fishery fishery = map.placeBuilding(new Fishery(player0), point1);

        constructHouse(fishery);

        fishery.promiseWorker(new Fisherman(player0, map));

        try {
            fishery.promiseWorker(new Fisherman(player0, map));

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testFisheryIsNotMilitary() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(10, 10);
        Fishery fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        assertTrue(fishery.isPlanned());
        assertFalse(fishery.needsWorker());

        /* Wait for the fishery to get constructed */
        Utils.waitForBuildingToBeConstructed(fishery);

        assertFalse(fishery.isMilitaryBuilding());
        assertEquals(fishery.getNumberOfHostedMilitary(), 0);
        assertEquals(fishery.getMaxHostedMilitary(), 0);
    }

    @Test
    public void testFisheryUnderConstructionNotNeedsWorker() {
        Fishery fishery = new Fishery(null);

        assertFalse(fishery.needsWorker());
    }

    @Test
    public void testFishermanIsAssignedToFishery() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(10, 4);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        /* Finish the fisherman hut */
        constructHouse(fishery);

        /* Run game logic twice, once to place courier and once to place fisherman */
        Utils.fastForward(2, map);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Fisherman.class);
    }

    @Test
    public void testFishermanIsCreatedFromRod() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all fishermen from the headquarters and add a fishing rod */
        Utils.adjustInventoryTo(headquarter, FISHERMAN, 0);
        Utils.adjustInventoryTo(headquarter, Material.FISHING_ROD, 1);

        /* Place fishery */
        Point point1 = new Point(10, 4);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        /* Finish the fisherman hut */
        constructHouse(fishery);

        /* Run game logic twice, once to place courier and once to place fisherman */
        Utils.fastForward(2, map);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Fisherman.class);
    }

    @Test
    public void testOnlyOneFishermanIsAssignedToFishery() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(10, 4);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        /* Construct the fisherman hut */
        constructHouse(fishery);

        /* Run game logic twice, once to place courier and once to place fisherman */
        Utils.fastForward(2, map);

        assertTrue(map.getWorkers().size() >= 3);

        /* Keep running the game loop and make sure no more workers are allocated */
        Utils.fastForward(200, map);

        assertTrue(map.getWorkers().size() >= 3);
    }

    @Test
    public void testOnlyOneFishermanIsNotASoldier() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(10, 4);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        /* Wait for the fishery to get constructed */
        Utils.waitForBuildingToBeConstructed(fishery);

        /* Wait for a fisherman to walk out */
        Fisherman fisherman0 = Utils.waitForWorkerOutsideBuilding(Fisherman.class, player0);

        assertNotNull(fisherman0);
        assertFalse(fisherman0.isSoldier());
    }

    @Test
    public void testArrivedFishermanRestsInFisheryAndThenLeaves() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(5, 5);
        map.setDetailedVegetationBelow(point0, WATER);

        /* Place headquarters */
        Point point1 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place fishery */
        Point point2 = new Point(10, 4);
        Building fishery = map.placeBuilding(new Fishery(player0), point2);

        /* Connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        /* Wait for the fishery to get constructed */
        Utils.waitForBuildingToBeConstructed(fishery);

        /* Wait for the fishery to get occupied */
        Fisherman fisherman = (Fisherman) Utils.waitForNonMilitaryBuildingToGetPopulated(fishery);

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

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        map.setDetailedVegetationBelow(point2, WATER);

        /* Place headquarters */
        Point point3 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point3);

        Point point4 = new Point(6, 6);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Construct the fisherman fishery */
        constructHouse(fishery);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery);

        assertTrue(fisherman.isInsideBuilding());

        /* Let the fisherman rest */
        Utils.fastForward(99, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Step once and make sure the fisherman goes out of the hut */
        map.stepTime();

        assertFalse(fisherman.isInsideBuilding());

        /* Verify that the fisherman is walking to the lake */
        Point point = fisherman.getTarget();
        assertTrue(point.equals(point2) || point.equals(point1) || point.equals(point0));

        assertFalse(map.isBuildingAtPoint(point));
        assertTrue(fisherman.isTraveling());
    }

    @Test
    public void testFishermanReachesPointToFish() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        map.setDetailedVegetationBelow(point2, WATER);

        /* Place headquarters */
        Point point3 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point3);

        Point point4 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Construct the fisherman hut */
        constructHouse(fishery);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery);

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

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point2 = new Point(5, 5);

        map.setDetailedVegetationBelow(point2, WATER);

        /* Place headquarters */
        Point point3 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Place fishery */
        Point point4 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        /* Wait for the fishery to get constructed */
        Utils.waitForBuildingToBeConstructed(fishery);

        /* Wait for the fishery to get occupied */
        Fisherman fisherman = (Fisherman) Utils.waitForNonMilitaryBuildingToGetPopulated(fishery);

        assertTrue(fisherman.isInsideBuilding());

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
        for (int i = 0; i < 136; i++) {
            assertTrue(fisherman.isFishing());

            map.stepTime();
        }

        assertTrue(fisherman.isFishing());
        assertNull(fisherman.getCargo());

        map.stepTime();

        /* Verify that the fisherman is done fishing and that the amount of fish has decreased */
        assertFalse(fisherman.isFishing());
        assertNotNull(fisherman.getCargo());
        assertEquals(fisherman.getCargo().getMaterial(), FISH);
        assertTrue(map.getAmountFishAtPoint(point) < amountOfFish);
    }

    @Test
    public void testFishermanReturnsHomeAfterFishing() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point2 = new Point(5, 5);

        map.setDetailedVegetationBelow(point2, WATER);

        /* Place headquarters */
        Point point3 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point3);

        Point point4 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Construct the fisherman hut */
        constructHouse(fishery);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery);

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
        Utils.fastForward(136, map);

        assertTrue(fisherman.isFishing());

        map.stepTime();

        /* Verify that the fisherman goes back home */
        assertFalse(fisherman.isFishing());

        assertEquals(fisherman.getTarget(), fishery.getPosition());
        assertTrue(fisherman.isTraveling());

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isInsideBuilding());
    }

    @Test
    public void testFishCargoIsDeliveredToMineWhichIsCloserThanHeadquarters() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Remove all bread from the headquarters */
        Utils.adjustInventoryTo(headquarter, FISH, 0);

        /* Place a small mountain */
        Point point4 = new Point(10, 4);
        Utils.surroundPointWithMinableMountain(point4, map);

        /* Place coal mine */
        CoalMine coalMine = map.placeBuilding(new CoalMine(player0), point4);

        /* Connect the coal mine to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, coalMine.getFlag(), headquarter.getFlag());

        /* Wait for the coal mine to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(coalMine);

        Utils.waitForNonMilitaryBuildingToGetPopulated(coalMine);

        /* Place a small lake */
        Point point5 = new Point(17, 5);
        Utils.surroundPointWithWater(point5, map);

        /* Place the fishery */
        Point point1 = new Point(14, 4);
        Fishery fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Connect the fishery with the coal mine */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), coalMine.getFlag());

        /* Wait for the fishery to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(fishery);

        Utils.waitForNonMilitaryBuildingToGetPopulated(fishery);

        /* Wait for the courier on the road between the coal mine and the fishery hut to have a fish cargo */
        Utils.waitForFlagToGetStackedCargo(map, fishery.getFlag(), 1);

        assertEquals(fishery.getFlag().getStackedCargo().get(0).getMaterial(), FISH);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the coal mine (and not the headquarters) */
        assertEquals(fishery.getAmount(FISH), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), coalMine.getPosition());

        assertEquals(coalMine.getAmount(FISH), 1);
    }

    @Test
    public void testFishIsNotDeliveredToStorehouseUnderConstruction() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory */
        Utils.clearInventory(headquarter, PLANK, STONE, IRON_BAR, COAL, FISH);

        /* Place storehouse */
        Point point4 = new Point(10, 4);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Connect the storehouse to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Place lake */
        Point point5 = new Point(17, 5);
        Utils.surroundPointWithWater(point5, map);

        /* Place the fishery */
        Point point1 = new Point(14, 4);
        Fishery fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Connect the fishery with the storehouse */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), storehouse.getFlag());

        /* Deliver the needed material to construct the fishery */
        Utils.deliverCargos(fishery, PLANK, 2);

        /* Wait for the fishery to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(fishery);

        Utils.waitForNonMilitaryBuildingToGetPopulated(fishery);

        /* Wait for the courier on the road between the storehouse and the fishery to have a fish cargo */
        Utils.waitForFlagToGetStackedCargo(map, fishery.getFlag(), 1);

        assertEquals(fishery.getFlag().getStackedCargo().get(0).getMaterial(), FISH);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the storehouse's flag so that it can continue to the headquarters */
        assertEquals(headquarter.getAmount(FISH), 0);
        assertEquals(fishery.getAmount(FISH), 0);
        assertFalse(storehouse.needsMaterial(FISH));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().get(0).getMaterial().equals(FISH));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testFishIsNotDeliveredTwiceToBuildingThatOnlyNeedsOne() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory */
        Utils.clearInventory(headquarter, FISH, COAL, IRON, BREAD, MEAT);

        /* Place mountain */
        Point point4 = new Point(10, 4);
        Utils.surroundPointWithMinableMountain(point4, map);

        /* Place gold mine */
        GoldMine goldMine = map.placeBuilding(new GoldMine(player0), point4);

        /* Construct the gold mine */
        Utils.constructHouse(goldMine);

        /* Ensure the gold mine can hold one (and only one) more fish */
        assertEquals(goldMine.getCanHoldAmount(FISH) - goldMine.getAmount(FISH), 1);

        /* Connect the gold mine to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, goldMine.getFlag(), headquarter.getFlag());

        /* Place lake */
        Point point5 = new Point(17, 5);
        Utils.surroundPointWithWater(point5, map);

        /* Place the fishery */
        Point point1 = new Point(14, 4);
        var fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Connect the fishery with the gold mine */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), goldMine.getFlag());

        /* Deliver the needed material to construct the fishery */
        Utils.deliverCargos(fishery, PLANK, 2);

        /* Wait for the fishery to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(fishery);

        Utils.waitForNonMilitaryBuildingToGetPopulated(fishery);

        /* Wait for the flag on the road between the gold mine and the fishery to have a fish cargo */
        Utils.waitForFlagToGetStackedCargo(map, fishery.getFlag(), 1);

        assertEquals(fishery.getFlag().getStackedCargo().get(0).getMaterial(), FISH);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that no iron bar is delivered from the headquarters */
        Utils.adjustInventoryTo(headquarter, FISH, 1);

        assertEquals(goldMine.getCanHoldAmount(FISH) - goldMine.getAmount(FISH), 1);
        assertFalse(goldMine.needsMaterial(FISH));

        for (int i = 0; i < 200; i++) {
            if (goldMine.getAmount(IRON_BAR) == 0) {
                break;
            }

            assertEquals(headquarter.getAmount(IRON_BAR), 1);
            assertNull(headquarter.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testFishermanPlacesFishAtFlag() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point2 = new Point(5, 5);

        map.setDetailedVegetationBelow(point2, WATER);

        /* Place headquarters */
        Point point3 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        Point point4 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Connect the headquarters with the fishery */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), fishery.getFlag());

        /* Construct the fisherman hut */
        constructHouse(fishery);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery);

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
        Utils.waitForFishermanToStopFishing(fisherman, map);

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

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(10, 4);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point1);

        /* Construct the fisherman hut */
        constructHouse(fishery0);

        /* Put trees around the fisherman hut */
        for (Point point : map.getPointsWithinRadius(fishery0.getPosition(), 4)) {
            if (point.equals(point1)) {
                continue;
            }

            if (map.isBuildingAtPoint(point) || map.isFlagAtPoint(point) || map.isRoadAtPoint(point) || map.isStoneAtPoint(point)) {
                continue;
            }

            map.placeTree(point, Tree.TreeType.PINE, TreeSize.FULL_GROWN);
        }

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery0);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake on the map */
        Point point0 = new Point(10, 4);
        Utils.surroundPointWithVegetation(point0, WATER, map);

        /* Verify that the point is surrounded by water */
        assertEquals(map.getDetailedVegetationUpLeft(point0), WATER);
        assertEquals(map.getDetailedVegetationAbove(point0), WATER);
        assertEquals(map.getDetailedVegetationUpRight(point0), WATER);
        assertEquals(map.getDetailedVegetationDownRight(point0), WATER);
        assertEquals(map.getDetailedVegetationBelow(point0), WATER);
        assertEquals(map.getDetailedVegetationDownLeft(point0), WATER);

        /* Place a mountain */
        Point point7 = new Point(5, 13);
        Point point14 = new Point(8, 14);

        Utils.surroundPointWithVegetation(point7, MOUNTAIN_1, map);
        Utils.surroundPointWithVegetation(point14, MOUNTAIN_1, map);

        /* Verify that the points are surrounded by mountain */
        assertEquals(map.getDetailedVegetationUpLeft(point7), MOUNTAIN_1);
        assertEquals(map.getDetailedVegetationAbove(point7), MOUNTAIN_1);
        assertEquals(map.getDetailedVegetationUpRight(point7), MOUNTAIN_1);
        assertEquals(map.getDetailedVegetationDownRight(point7), MOUNTAIN_1);
        assertEquals(map.getDetailedVegetationBelow(point7), MOUNTAIN_1);
        assertEquals(map.getDetailedVegetationDownLeft(point7), MOUNTAIN_1);

        assertEquals(map.getDetailedVegetationUpLeft(point14), MOUNTAIN_1);
        assertEquals(map.getDetailedVegetationAbove(point14), MOUNTAIN_1);
        assertEquals(map.getDetailedVegetationUpRight(point14), MOUNTAIN_1);
        assertEquals(map.getDetailedVegetationDownRight(point14), MOUNTAIN_1);
        assertEquals(map.getDetailedVegetationBelow(point14), MOUNTAIN_1);
        assertEquals(map.getDetailedVegetationDownLeft(point14), MOUNTAIN_1);

        /* Place stone */
        Point point19 = new Point(12, 12);
        Stone stone0 = map.placeStone(point19, StoneType.STONE_1, 7);

        /* Place stone */
        Point point20 = new Point(13, 11);
        Stone stone1 = map.placeStone(point20, StoneType.STONE_1, 7);

        /* Place headquarters */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place fishery */
        Point point22 = new Point(10, 8);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point22);

        /* Place road between (11, 7) and (6, 4) */
        Point point2 = new Point(9, 5);
        Point point23 = new Point(11, 7);
        Point point24 = new Point(10, 6);
        Point point25 = new Point(7, 5);
        Point point26 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point23, point24, point2, point25, point26);

        /* Wait for the fishery to be finished */
        Utils.fastForwardUntilBuildingIsConstructed(fishery0);

        /* Wait for the fishery to get occupied */
        Utils.fastForwardUntilBuildingIsOccupied(fishery0);

        /* Wait for the fisherman to rest */
        Utils.fastForward(100, map);

        /* Verify that the fisherman leaves the hut */
        Worker fisher = fishery0.getWorker();

        assertNotNull(fisher.getTarget());
    }

    @Test
    public void testFishermanCanRunOutOfFish() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        map.setDetailedVegetationBelow(point2, WATER);

        /* Remove fishes until there is only one left */
        Utils.removeAllFish(map, point1);
        Utils.removeAllFish(map, point2);

        for (int i = 0; i < 1000; i++) {
            if (map.getAmountFishAtPoint(point0) == 1) {
                break;
            }

            map.catchFishAtPoint(point0);
        }

        /* Place headquarters */
        Point point3 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Place fishery */
        Point point4 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Place a road from the headquarters to the fishery */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), fishery.getFlag());

        /* Construct the fisherman hut */
        constructHouse(fishery);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery);

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
        assertFalse(fishery.isOutOfNaturalResources());

        Utils.waitForFishermanToStopFishing(fisherman, map);

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
        assertTrue(fishery.isOutOfNaturalResources());
    }

    @Test
    public void testFisheryWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place fish on one tile */
        Point point2 = new Point(9, 7);

        map.setDetailedVegetationBelow(point2, WATER);

        /* Place fishery */
        Point point26 = new Point(8, 8);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Finish construction of the fishery */
        constructHouse(fishery0);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(player0, map), fishery0);

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
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place fish on one tile */
        Point point0 = new Point(8, 6);
        Point point1 = new Point(10, 6);
        Point point2 = new Point(9, 7);

        map.setDetailedVegetationBelow(point2, WATER);

        /* Place fishery */
        Point point26 = new Point(8, 8);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Finish construction of the fishery */
        constructHouse(fishery0);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(player0, map), fishery0);

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

        /* Connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fishery0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), fishery0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));


        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), fishery0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarters */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FISH);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarters */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(FISH), amount + 1);
    }

    @Test
    public void testFishermanGoesBackToStorageWhenFisheryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place fishery */
        Point point26 = new Point(8, 8);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Finish construction of the fishery */
        constructHouse(fishery0);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(player0, map), fishery0);

        /* Destroy the fishery */
        Worker fisherman = fishery0.getWorker();

        assertTrue(fisherman.isInsideBuilding());
        assertEquals(fisherman.getPosition(), fishery0.getPosition());

        fishery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters */
        assertFalse(fisherman.isInsideBuilding());
        assertEquals(fisherman.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FISHERMAN);

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, headquarter0.getPosition());

        /* Verify that the fisherman is stored correctly in the headquarters */
        assertEquals(headquarter0.getAmount(FISHERMAN), amount + 1);
    }

    @Test
    public void testFishermanGoesBackOnToStorageOnRoadsIfPossibleWhenFisheryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place fishery */
        Point point26 = new Point(8, 8);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Connect the fishery with the headquarters */
        map.placeAutoSelectedRoad(player0, fishery0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the fishery */
        constructHouse(fishery0);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(player0, map), fishery0);

        /* Destroy the fishery */
        Worker fisherman = fishery0.getWorker();

        assertTrue(fisherman.isInsideBuilding());
        assertEquals(fisherman.getPosition(), fishery0.getPosition());

        fishery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters */
        assertFalse(fisherman.isInsideBuilding());
        assertEquals(fisherman.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point point : fisherman.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testProductionInFisheryCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point10 = new Point(10, 6);
        Point point11 = new Point(12, 6);
        Point point12 = new Point(11, 7);

        map.setDetailedVegetationBelow(point12, WATER);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(8, 6);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point1);

        /* Connect the fishery and the headquarters */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the fishery */
        constructHouse(fishery0);

        /* Assign a worker to the fishery */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery0);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point12 = new Point(11, 7);

        map.setDetailedVegetationBelow(point12, WATER);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(8, 6);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point1);

        /* Connect the fishery and the headquarters */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the fishery */
        constructHouse(fishery0);

        /* Assign a worker to the fishery */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery0);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarters */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(20, 14);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point1);

        /* Finish construction of the fishery */
        constructHouse(fishery0);

        /* Connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fishery0.getFlag());

        /* Wait for fisherman to get assigned and leave the headquarters */
        List<Fisherman> workers = Utils.waitForWorkersOutsideBuilding(Fisherman.class, 1, player0);

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

        /* Place player 2's headquarters */
        Point point10 = new Point(70, 70);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 9);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place fishery close to the new border */
        Point point4 = new Point(28, 18);
        Fishery fishery0 = map.placeBuilding(new Fishery(player0), point4);

        /* Finish construction of the fishery */
        constructHouse(fishery0);

        /* Occupy the fishery */
        Fisherman worker = Utils.occupyBuilding(new Fisherman(player0, map), fishery0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testFishermanReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place fishery */
        Point point2 = new Point(14, 4);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, fishery0.getFlag());

        /* Wait for the fisherman to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Fisherman.class, 1, player0);

        Fisherman fisherman = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Fisherman) {
                fisherman = (Fisherman) worker;
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

        /* Verify that the fisherman returns to the headquarters when it reaches the flag */
        assertEquals(fisherman.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, headquarter0.getPosition());
    }

    @Test
    public void testFishermanContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place fishery */
        Point point2 = new Point(14, 4);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, fishery0.getFlag());

        /* Wait for the fisherman to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Fisherman.class, 1, player0);

        Fisherman fisherman = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Fisherman) {
                fisherman = (Fisherman) worker;
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place fishery */
        Point point2 = new Point(14, 4);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, fishery0.getFlag());

        /* Wait for the fisherman to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Fisherman.class, 1, player0);

        Fisherman fisherman = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Fisherman) {
                fisherman = (Fisherman) worker;
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place fishery */
        Point point26 = new Point(17, 17);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Finish construction of the fishery */
        constructHouse(fishery0);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(player0, map), fishery0);

        /* Place a second storage closer to the fishery */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the fishery */
        Worker fisherman = fishery0.getWorker();

        assertTrue(fisherman.isInsideBuilding());
        assertEquals(fisherman.getPosition(), fishery0.getPosition());

        fishery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters */
        assertFalse(fisherman.isInsideBuilding());
        assertEquals(fisherman.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(FISHERMAN);

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, storehouse0.getPosition());

        /* Verify that the fisherman is stored correctly in the headquarters */
        assertEquals(storehouse0.getAmount(FISHERMAN), amount + 1);
    }

    @Test
    public void testFishermanReturnsOffroadAndAvoidsBurningStorageWhenFisheryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place fishery */
        Point point26 = new Point(17, 17);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Finish construction of the fishery */
        constructHouse(fishery0);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(player0, map), fishery0);

        /* Place a second storage closer to the fishery */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Destroy the fishery */
        Worker fisherman = fishery0.getWorker();

        assertTrue(fisherman.isInsideBuilding());
        assertEquals(fisherman.getPosition(), fishery0.getPosition());

        fishery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters */
        assertFalse(fisherman.isInsideBuilding());
        assertEquals(fisherman.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FISHERMAN);

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, headquarter0.getPosition());

        /* Verify that the fisherman is stored correctly in the headquarters */
        assertEquals(headquarter0.getAmount(FISHERMAN), amount + 1);
    }

    @Test
    public void testFishermanReturnsOffroadAndAvoidsDestroyedStorageWhenFisheryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place fishery */
        Point point26 = new Point(17, 17);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Finish construction of the fishery */
        constructHouse(fishery0);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(player0, map), fishery0);

        /* Place a second storage closer to the fishery */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

        /* Destroy the fishery */
        Worker fisherman = fishery0.getWorker();

        assertTrue(fisherman.isInsideBuilding());
        assertEquals(fisherman.getPosition(), fishery0.getPosition());

        fishery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters */
        assertFalse(fisherman.isInsideBuilding());
        assertEquals(fisherman.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FISHERMAN);

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, headquarter0.getPosition());

        /* Verify that the fisherman is stored correctly in the headquarters */
        assertEquals(headquarter0.getAmount(FISHERMAN), amount + 1);
    }

    @Test
    public void testFishermanReturnsOffroadAndAvoidsUnfinishedStorageWhenFisheryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place fishery */
        Point point26 = new Point(17, 17);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Finish construction of the fishery */
        constructHouse(fishery0);

        /* Occupy the fishery */
        Utils.occupyBuilding(new Fisherman(player0, map), fishery0);

        /* Place a second storage closer to the fishery */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the fishery */
        Worker fisherman = fishery0.getWorker();

        assertTrue(fisherman.isInsideBuilding());
        assertEquals(fisherman.getPosition(), fishery0.getPosition());

        fishery0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters */
        assertFalse(fisherman.isInsideBuilding());
        assertEquals(fisherman.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FISHERMAN);

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, headquarter0.getPosition());

        /* Verify that the fisherman is stored correctly in the headquarters */
        assertEquals(headquarter0.getAmount(FISHERMAN), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place fishery */
        Point point26 = new Point(17, 17);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point26);

        /* Place road to connect the headquarter and the fishery */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fishery0.getFlag());

        /* Finish construction of the fishery */
        constructHouse(fishery0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Fisherman.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, fishery0.getFlag().getPosition());

        /* Tear down the building */
        fishery0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(worker.getTarget(), fishery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, fishery0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testFisheryWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(7, 9);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Finish construction of the fishery */
        constructHouse(fishery);

        /* Populate the fishery */
        Worker fisherman0 = Utils.occupyBuilding(new Fisherman(player0, map), fishery);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a fish tile */
        Point point2 = new Point(11, 9);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        /* Place fishery */
        Point point1 = new Point(7, 9);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Finish construction of the fishery */
        constructHouse(fishery);

        /* Populate the fishery */
        Worker fisherman0 = Utils.occupyBuilding(new Fisherman(player0, map), fishery);

        assertTrue(fisherman0.isInsideBuilding());
        assertEquals(fisherman0.getHome(), fishery);
        assertEquals(fishery.getWorker(), fisherman0);

        /* Connect the fishery with the headquarters */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fishery.getFlag());

        /* Make the fishery catch some fish with full resources available */
        for (int i = 0; i < 10000; i++) {

            if (fishery.getProductivity() == 100) {
                break;
            }

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a fish tile */
        Point point2 = new Point(11, 9);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        /* Place fishery */
        Point point1 = new Point(7, 9);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter0.getFlag());

        /* Wait for the fishery to get constructed */
        Utils.waitForBuildingToBeConstructed(fishery);

        /* Wait for the fishery to get occupied */
        Fisherman fisherman0 = (Fisherman) Utils.waitForNonMilitaryBuildingToGetPopulated(fishery);

        assertTrue(fisherman0.isInsideBuilding());
        assertEquals(fisherman0.getHome(), fishery);
        assertEquals(fishery.getWorker(), fisherman0);

        /* Make the fishery catch some fish with full resources available */
        for (int i = 0; i < 10000; i++) {

            if (fishery.getProductivity() == 100) {
                break;
            }

            map.stepTime();
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(fishery.getProductivity(), 100);

        for (int i = 0; i < 20000; i++) {
            if (fishery.getProductivity() == 0) {
                break;
            }

            map.stepTime();
        }

        assertEquals(fishery.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedFisheryHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a fish tile */
        Point point2 = new Point(11, 9);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        /* Place fishery */
        Point point1 = new Point(7, 9);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Finish construction of the fishery */
        constructHouse(fishery);

        /* Verify that the unoccupied fishery is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(fishery.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testFisheryCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(7, 9);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Finish construction of the fishery */
        constructHouse(fishery);

        /* Populate the fishery */
        Worker fisherman0 = Utils.occupyBuilding(new Fisherman(player0, map), fishery);

        /* Verify that the fishery can produce */
        assertTrue(fishery.canProduce());
    }

    @Test
    public void testFisheryReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(6, 12);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point1);

        /* Construct the fishery */
        constructHouse(fishery0);

        /* Verify that the reported output is correct */
        assertEquals(fishery0.getProducedMaterial().length, 1);
        assertEquals(fishery0.getProducedMaterial()[0], FISH);
    }

    @Test
    public void testFisheryReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(6, 12);
        Building fisher0 = map.placeBuilding(new Fishery(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(fisher0.getTypesOfMaterialNeeded().size(), 1);
        assertTrue(fisher0.getTypesOfMaterialNeeded().contains(PLANK));
        assertEquals(fisher0.getCanHoldAmount(PLANK), 2);

        for (Material material : Material.values()) {
            if (material == PLANK) {
                continue;
            }

            assertEquals(fisher0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testFisheryReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(6, 12);
        Building fishery0 = map.placeBuilding(new Fishery(player0), point1);

        /* Construct the fishery */
        constructHouse(fishery0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(fishery0.getTypesOfMaterialNeeded().size(), 0);

        for (Material material : Material.values()) {
            assertEquals(fishery0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testFisheryWaitsWhenFlagIsFull() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(5, 5);

        map.setDetailedVegetationBelow(point0, WATER);

        /* Place headquarters */
        Point point1 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place fishery */
        Point point2 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(player0), point2);

        /* Connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        /* Wait for the fishery to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(fishery);
        Utils.waitForNonMilitaryBuildingToGetPopulated(fishery);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, fishery.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the fishery waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 500; i++) {
            assertEquals(fishery.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        /* Reconnect the fishery with the headquarters */
        Road road1 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(courier.getCargo());
            assertEquals(fishery.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(fishery.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, fishery.getWorker(), FISH);
    }

    @Test
    public void testFisheryDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(5, 5);
        map.setDetailedVegetationBelow(point0, WATER);

        /* Place headquarters */
        Point point1 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place fishery */
        Point point2 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(player0), point2);

        /* Connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        /* Wait for the fishery to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(fishery);
        Utils.waitForNonMilitaryBuildingToGetPopulated(fishery);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, fishery.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The fishery waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 500; i++) {
            assertEquals(fishery.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        /* Reconnect the fishery with the headquarters */
        Road road1 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(courier.getCargo());
            assertEquals(fishery.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(fishery.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, fishery.getWorker(), FISH);

        Utils.fastForwardUntilWorkerReachesPoint(map, fishery.getWorker(), fishery.getPosition());

        /* Wait for the worker to put the cargo on the flag */
        map.stepTime();

        assertEquals(fishery.getWorker().getTarget(), fishery.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fishery.getWorker(), fishery.getFlag().getPosition());

        assertEquals(fishery.getFlag().getStackedCargo().size(), 8);

        /* Verify that the fishery doesn't produce anything because the flag is full */
        for (int i = 0; i < 800; i++) {
            assertEquals(fishery.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }
    }

    @Test
    public void testWhenFishDeliveryAreBlockedFisheryFillsUpFlagAndThenStops() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place Fishery */
        Point point1 = new Point(7, 9);
        Fishery fishery0 = map.placeBuilding(new Fishery(player0), point1);

        /* Place fish on one tile */
        Point point2 = new Point(11, 11);
        map.setDetailedVegetationBelow(point2, WATER);

        /* Place road to connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery0.getFlag(), headquarter0.getFlag());

        /* Wait for the fishery to get constructed and occupied */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(fishery0);

        Worker fisherman0 = Utils.waitForNonMilitaryBuildingToGetPopulated(fishery0);

        assertTrue(fisherman0.isInsideBuilding());
        assertEquals(fisherman0.getHome(), fishery0);
        assertEquals(fishery0.getWorker(), fisherman0);

        /* Block storage of fish */
        headquarter0.blockDeliveryOfMaterial(FISH);

        /* Verify that the fishery puts eight fishes on the flag and then stops */
        Utils.waitForFlagToGetStackedCargo(map, fishery0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman0, fishery0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(fishery0.getFlag().getStackedCargo().size(), 8);

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), FISH);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndFisheryIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place fishery */
        Point point2 = new Point(18, 6);
        Fishery fishery0 = map.placeBuilding(new Fishery(player0), point2);

        /* Place fish on one tile */
        Point point3 = new Point(7, 11);
        map.setDetailedVegetationBelow(point3, WATER);

        /* Place road to connect the storehouse with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse);

        /* Wait for the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse);

        /* Place road to connect the headquarters with the fishery */
        Road road1 = map.placeAutoSelectedRoad(player0, fishery0.getFlag(), headquarter0.getFlag());

        /* Wait for the fishery to get constructed */
        Utils.waitForBuildingsToBeConstructed(fishery0);

        /* Wait for the fishery to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(fishery0);

        Worker fisherman0 = fishery0.getWorker();

        assertTrue(fisherman0.isInsideBuilding());
        assertEquals(fisherman0.getHome(), fishery0);
        assertEquals(fishery0.getWorker(), fisherman0);

        /* Verify that the worker goes to the storage when the fishery is torn down */
        headquarter0.blockDeliveryOfMaterial(FISHERMAN);

        fishery0.tearDown();

        map.stepTime();

        assertFalse(fisherman0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman0, fishery0.getFlag().getPosition());

        assertEquals(fisherman0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, fisherman0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(fisherman0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndFisheryIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place fishery */
        Point point2 = new Point(18, 6);
        Fishery fishery0 = map.placeBuilding(new Fishery(player0), point2);

        /* Place fish on one tile */
        Point point3 = new Point(7, 11);
        map.setDetailedVegetationBelow(point3, WATER);

        /* Place road to connect the storehouse with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse);

        /* Wait for the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse);

        /* Place road to connect the headquarter with the fishery */
        Road road1 = map.placeAutoSelectedRoad(player0, fishery0.getFlag(), headquarter0.getFlag());

        /* Wait for the fishery to get constructed */
        Utils.waitForBuildingsToBeConstructed(fishery0);

        /* Wait for the fishery to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(fishery0);

        Worker fisherman0 = fishery0.getWorker();

        assertTrue(fisherman0.isInsideBuilding());
        assertEquals(fisherman0.getHome(), fishery0);
        assertEquals(fishery0.getWorker(), fisherman0);

        /* Verify that the worker goes to the storage off-road when the fishery is torn down */
        headquarter0.blockDeliveryOfMaterial(FISHERMAN);

        fishery0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(fisherman0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman0, fishery0.getFlag().getPosition());

        assertEquals(fisherman0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(fisherman0));
    }

    @Test
    public void testWorkerGoesOutAndBackInWhenSentOutWithoutBlocking() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fish on one tile */
        Point point1 = new Point(11, 11);
        map.setDetailedVegetationBelow(point1, WATER);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, FISHERMAN, 1);

        assertEquals(headquarter0.getAmount(FISHERMAN), 1);

        headquarter0.pushOutAll(FISHERMAN);

        for (int i = 0; i < 10; i++) {
            Worker worker = Utils.waitForWorkerOutsideBuilding(Fisherman.class, player0);

            assertEquals(headquarter0.getAmount(FISHERMAN), 0);
            assertEquals(worker.getPosition(), headquarter0.getPosition());
            assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

            assertEquals(worker.getPosition(), headquarter0.getFlag().getPosition());
            assertEquals(worker.getTarget(), headquarter0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

            assertFalse(map.getWorkers().contains(worker));
        }
    }

    @Test
    public void testPushedOutWorkerWithNowhereToGoWalksAwayAndDies() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fish on one tile */
        Point point1 = new Point(11, 11);
        map.setDetailedVegetationBelow(point1, WATER);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, FISHERMAN, 1);

        headquarter0.blockDeliveryOfMaterial(FISHERMAN);
        headquarter0.pushOutAll(FISHERMAN);

        Worker worker = Utils.waitForWorkerOutsideBuilding(Fisherman.class, player0);

        assertEquals(worker.getPosition(), headquarter0.getPosition());
        assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

        assertEquals(worker.getPosition(), headquarter0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), headquarter0.getPosition());
        assertFalse(worker.isDead());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        assertTrue(worker.isDead());

        for (int i = 0; i < 100; i++) {
            assertTrue(worker.isDead());
            assertTrue(map.getWorkers().contains(worker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    @Test
    public void testWorkerWithNowhereToGoWalksAwayAndDiesWhenHouseIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(7, 9);
        Fishery fishery0 = map.placeBuilding(new Fishery(player0), point1);

        /* Place fish on one tile */
        Point point2 = new Point(11, 11);
        map.setDetailedVegetationBelow(point2, WATER);

        /* Place road to connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the fishery to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(fishery0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(fishery0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter
        */
        headquarter0.blockDeliveryOfMaterial(FISHERMAN);

        Worker worker = fishery0.getWorker();

        fishery0.tearDown();

        assertEquals(worker.getPosition(), fishery0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, fishery0.getFlag().getPosition());

        assertEquals(worker.getPosition(), fishery0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), fishery0.getPosition());
        assertNotEquals(worker.getTarget(), headquarter0.getPosition());
        assertFalse(worker.isDead());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        assertTrue(worker.isDead());

        for (int i = 0; i < 100; i++) {
            assertTrue(worker.isDead());
            assertTrue(map.getWorkers().contains(worker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    @Test
    public void testWorkerGoesAwayAndDiesWhenItReachesTornDownHouseAndStorageIsBlocked() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fishery */
        Point point1 = new Point(7, 9);
        Fishery fishery0 = map.placeBuilding(new Fishery(player0), point1);

        /* Place fish on one tile */
        Point point2 = new Point(11, 11);
        map.setDetailedVegetationBelow(point2, WATER);

        /* Place road to connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the fishery to get constructed */
        Utils.waitForBuildingToBeConstructed(fishery0);

        /* Wait for a fisherman to start walking to the fishery */
        Fisherman fisherman = Utils.waitForWorkerOutsideBuilding(Fisherman.class, player0);

        /* Wait for the fisherman to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the fisherman goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(fisherman.getTarget(), fishery0.getPosition());

        headquarter0.blockDeliveryOfMaterial(FISHERMAN);

        fishery0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery0.getFlag().getPosition());

        assertEquals(fisherman.getPosition(), fishery0.getFlag().getPosition());
        assertNotEquals(fisherman.getTarget(), headquarter0.getPosition());
        assertFalse(fisherman.isInsideBuilding());
        assertNull(fishery0.getWorker());
        assertNotNull(fisherman.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fisherman.getTarget());

        Point point = fisherman.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(fisherman.isDead());
            assertEquals(fisherman.getPosition(), point);
            assertTrue(map.getWorkers().contains(fisherman));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(fisherman));
    }

    @Test
    public void testFishermanFishNextToWater2OnTileBelow() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a small lake on one tile */
        Point point0 = new Point(5, 5);
        map.setDetailedVegetationBelow(point0, WATER_2);

        /* Place headquarters */
        Point point3 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point3);

        /* Place fishery */
        Point point4 = new Point(6, 6);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Construct and occupy the fishery */
        constructHouse(fishery);
        Fisherman fisherman = occupyBuilding(new Fisherman(player0, map), fishery);

        assertTrue(fisherman.isInsideBuilding());

        /* Remove all but one fish in the three points around the small lake */
        Utils.removeAllFishExceptOne(map, point0);
        Utils.removeAllFishExceptOne(map, point0.downLeft());
        Utils.removeAllFishExceptOne(map, point0.downRight());

        /* Verify that the fisherman fishes from the three points around the small lake and then has no more fish */
        Set<Point> visited = new HashSet<>();

        for (int i = 0; i < 3; i++) {

            /* Wait for the worker to go outside */
            Utils.waitForWorkerToBeOutside(fisherman, map);

            /* Wait for the worker to go to a point on the lake */
            Point target = fisherman.getTarget();

            assertNotNull(target);
            assertTrue(target.equals(point0) || target.equals(point0.downLeft()) || target.equals(point0.downRight()));
            assertFalse(visited.contains(target));

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, target);

            assertTrue(fisherman.isFishing());

            visited.add(target);

            /* Wait for the worker to go back to the fishery */
            Utils.waitForFishermanToStopFishing(fisherman, map);

            assertFalse(fisherman.isFishing());
            assertEquals(fisherman.getTarget(), fishery.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getPosition());

            map.stepTime();

            assertEquals(fisherman.getTarget(), fishery.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getFlag().getPosition());

            assertEquals(fisherman.getTarget(), fishery.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getPosition());
        }

        assertEquals(visited.size(), 3);
        assertTrue(visited.contains(point0));
        assertTrue(visited.contains(point0.downLeft()));
        assertTrue(visited.contains(point0.downRight()));

        /* Verify that the worker stays indoors when there is no more fish to catch */
        for (int i = 0; i < 300; i++) {
            assertTrue(fisherman.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testFishermanFishNextToWater2OnTileAbove() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a small lake on one tile */
        Point point0 = new Point(6, 4);
        map.setDetailedVegetationAbove(point0, WATER_2);

        /* Place headquarters */
        Point point3 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point3);

        /* Place fishery */
        Point point4 = new Point(6, 6);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Construct and occupy the fishery */
        constructHouse(fishery);
        Fisherman fisherman = occupyBuilding(new Fisherman(player0, map), fishery);

        assertTrue(fisherman.isInsideBuilding());

        /* Remove all but one fish in the three points around the small lake */
        Utils.removeAllFishExceptOne(map, point0);
        Utils.removeAllFishExceptOne(map, point0.upLeft());
        Utils.removeAllFishExceptOne(map, point0.upRight());

        /* Verify that the fisherman fishes from the three points around the small lake and then has no more fish */
        Set<Point> visited = new HashSet<>();

        for (int i = 0; i < 3; i++) {

            /* Wait for the worker to go outside */
            Utils.waitForWorkerToBeOutside(fisherman, map);

            /* Wait for the worker to go to a point on the lake */
            Point target = fisherman.getTarget();

            assertNotNull(target);
            assertTrue(target.equals(point0) || target.equals(point0.upLeft()) || target.equals(point0.upRight()));
            assertFalse(visited.contains(target));

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, target);

            assertTrue(fisherman.isFishing());

            visited.add(target);

            /* Wait for the worker to go back to the fishery */
            Utils.waitForFishermanToStopFishing(fisherman, map);

            assertFalse(fisherman.isFishing());
            assertEquals(fisherman.getTarget(), fishery.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getPosition());

            map.stepTime();

            assertEquals(fisherman.getTarget(), fishery.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getFlag().getPosition());

            assertEquals(fisherman.getTarget(), fishery.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getPosition());
        }

        assertEquals(visited.size(), 3);
        assertTrue(visited.contains(point0));
        assertTrue(visited.contains(point0.upLeft()));
        assertTrue(visited.contains(point0.upRight()));

        /* Verify that the worker stays indoors when there is no more fish to catch */
        for (int i = 0; i < 300; i++) {
            assertTrue(fisherman.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testFishermanFishNextToNormalWaterOnTileBelow() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a small lake on one tile */
        Point point0 = new Point(5, 5);
        map.setDetailedVegetationBelow(point0, WATER);

        /* Place headquarters */
        Point point3 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point3);

        /* Place fishery */
        Point point4 = new Point(6, 6);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Construct and occupy the fishery */
        constructHouse(fishery);
        Fisherman fisherman = occupyBuilding(new Fisherman(player0, map), fishery);

        assertTrue(fisherman.isInsideBuilding());

        /* Remove all but one fish in the three points around the small lake */
        Utils.removeAllFishExceptOne(map, point0);
        Utils.removeAllFishExceptOne(map, point0.downLeft());
        Utils.removeAllFishExceptOne(map, point0.downRight());

        /* Verify that the fisherman fishes from the three points around the small lake and then has no more fish */
        Set<Point> visited = new HashSet<>();

        for (int i = 0; i < 3; i++) {

            /* Wait for the worker to go outside */
            Utils.waitForWorkerToBeOutside(fisherman, map);

            /* Wait for the worker to go to a point on the lake */
            Point target = fisherman.getTarget();

            assertNotNull(target);
            assertTrue(target.equals(point0) || target.equals(point0.downLeft()) || target.equals(point0.downRight()));
            assertFalse(visited.contains(target));

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, target);

            assertTrue(fisherman.isFishing());

            visited.add(target);

            /* Wait for the worker to go back to the fishery */
            Utils.waitForFishermanToStopFishing(fisherman, map);

            assertFalse(fisherman.isFishing());
            assertEquals(fisherman.getTarget(), fishery.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getPosition());

            map.stepTime();

            assertEquals(fisherman.getTarget(), fishery.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getFlag().getPosition());

            assertEquals(fisherman.getTarget(), fishery.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getPosition());
        }

        assertEquals(visited.size(), 3);
        assertTrue(visited.contains(point0));
        assertTrue(visited.contains(point0.downLeft()));
        assertTrue(visited.contains(point0.downRight()));

        /* Verify that the worker stays indoors when there is no more fish to catch */
        for (int i = 0; i < 300; i++) {
            assertTrue(fisherman.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testFishermanFishNextToNormalWaterOnTileAbove() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a small lake on one tile */
        Point point0 = new Point(6, 4);
        map.setDetailedVegetationAbove(point0, WATER);

        /* Place headquarters */
        Point point3 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point3);

        /* Place fishery */
        Point point4 = new Point(6, 6);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Construct and occupy the fishery */
        constructHouse(fishery);
        Fisherman fisherman = occupyBuilding(new Fisherman(player0, map), fishery);

        assertTrue(fisherman.isInsideBuilding());

        /* Remove all but one fish in the three points around the small lake */
        Utils.removeAllFishExceptOne(map, point0);
        Utils.removeAllFishExceptOne(map, point0.upLeft());
        Utils.removeAllFishExceptOne(map, point0.upRight());

        /* Verify that the fisherman fishes from the three points around the small lake and then has no more fish */
        Set<Point> visited = new HashSet<>();

        for (int i = 0; i < 3; i++) {

            /* Wait for the worker to go outside */
            Utils.waitForWorkerToBeOutside(fisherman, map);

            /* Wait for the worker to go to a point on the lake */
            Point target = fisherman.getTarget();

            assertNotNull(target);
            assertTrue(target.equals(point0) || target.equals(point0.upLeft()) || target.equals(point0.upRight()));
            assertFalse(visited.contains(target));

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, target);

            assertTrue(fisherman.isFishing());

            visited.add(target);

            /* Wait for the worker to go back to the fishery */
            Utils.waitForFishermanToStopFishing(fisherman, map);

            assertFalse(fisherman.isFishing());
            assertEquals(fisherman.getTarget(), fishery.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getPosition());

            map.stepTime();

            assertEquals(fisherman.getTarget(), fishery.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getFlag().getPosition());

            assertEquals(fisherman.getTarget(), fishery.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getPosition());
        }

        assertEquals(visited.size(), 3);
        assertTrue(visited.contains(point0));
        assertTrue(visited.contains(point0.upLeft()));
        assertTrue(visited.contains(point0.upRight()));

        /* Verify that the worker stays indoors when there is no more fish to catch */
        for (int i = 0; i < 300; i++) {
            assertTrue(fisherman.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testFishermanFishNextToBuildableWaterOnTileBelow() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a small lake on one tile */
        Point point0 = new Point(5, 5);
        map.setDetailedVegetationBelow(point0, BUILDABLE_WATER);

        /* Place headquarters */
        Point point3 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point3);

        /* Place fishery */
        Point point4 = new Point(6, 6);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Construct and occupy the fishery */
        constructHouse(fishery);
        Fisherman fisherman = occupyBuilding(new Fisherman(player0, map), fishery);

        assertTrue(fisherman.isInsideBuilding());

        /* Remove all but one fish in the three points around the small lake */
        Utils.removeAllFishExceptOne(map, point0);
        Utils.removeAllFishExceptOne(map, point0.downLeft());
        Utils.removeAllFishExceptOne(map, point0.downRight());

        /* Verify that the fisherman fishes from the three points around the small lake and then has no more fish */
        Set<Point> visited = new HashSet<>();

        for (int i = 0; i < 3; i++) {

            /* Wait for the worker to go outside */
            Utils.waitForWorkerToBeOutside(fisherman, map);

            /* Wait for the worker to go to a point on the lake */
            Point target = fisherman.getTarget();

            assertNotNull(target);
            assertTrue(target.equals(point0) || target.equals(point0.downLeft()) || target.equals(point0.downRight()));
            assertFalse(visited.contains(target));

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, target);

            assertTrue(fisherman.isFishing());

            visited.add(target);

            /* Wait for the worker to go back to the fishery */
            Utils.waitForFishermanToStopFishing(fisherman, map);

            assertFalse(fisherman.isFishing());
            assertEquals(fisherman.getTarget(), fishery.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getPosition());

            map.stepTime();

            assertEquals(fisherman.getTarget(), fishery.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getFlag().getPosition());

            assertEquals(fisherman.getTarget(), fishery.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getPosition());
        }

        assertEquals(visited.size(), 3);
        assertTrue(visited.contains(point0));
        assertTrue(visited.contains(point0.downLeft()));
        assertTrue(visited.contains(point0.downRight()));

        /* Verify that the worker stays indoors when there is no more fish to catch */
        for (int i = 0; i < 300; i++) {
            assertTrue(fisherman.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testFishermanFishNextToBuildableWaterOnTileAbove() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a small lake on one tile */
        Point point0 = new Point(6, 4);
        map.setDetailedVegetationAbove(point0, BUILDABLE_WATER);

        /* Place headquarters */
        Point point3 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point3);

        /* Place fishery */
        Point point4 = new Point(6, 6);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Construct and occupy the fishery */
        constructHouse(fishery);
        Fisherman fisherman = occupyBuilding(new Fisherman(player0, map), fishery);

        assertTrue(fisherman.isInsideBuilding());

        /* Remove all but one fish in the three points around the small lake */
        Utils.removeAllFishExceptOne(map, point0);
        Utils.removeAllFishExceptOne(map, point0.upLeft());
        Utils.removeAllFishExceptOne(map, point0.upRight());

        /* Verify that the fisherman fishes from the three points around the small lake and then has no more fish */
        Set<Point> visited = new HashSet<>();

        for (int i = 0; i < 3; i++) {

            /* Wait for the worker to go outside */
            Utils.waitForWorkerToBeOutside(fisherman, map);

            /* Wait for the worker to go to a point on the lake */
            Point target = fisherman.getTarget();

            assertNotNull(target);
            assertTrue(target.equals(point0) || target.equals(point0.upLeft()) || target.equals(point0.upRight()));
            assertFalse(visited.contains(target));

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, target);

            assertTrue(fisherman.isFishing());

            visited.add(target);

            /* Wait for the worker to go back to the fishery */
            Utils.waitForFishermanToStopFishing(fisherman, map);

            assertFalse(fisherman.isFishing());
            assertEquals(fisherman.getTarget(), fishery.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getPosition());

            map.stepTime();

            assertEquals(fisherman.getTarget(), fishery.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getFlag().getPosition());

            assertEquals(fisherman.getTarget(), fishery.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getPosition());
        }

        assertEquals(visited.size(), 3);
        assertTrue(visited.contains(point0));
        assertTrue(visited.contains(point0.upLeft()));
        assertTrue(visited.contains(point0.upRight()));

        /* Verify that the worker stays indoors when there is no more fish to catch */
        for (int i = 0; i < 300; i++) {
            assertTrue(fisherman.isInsideBuilding());

            map.stepTime();
        }
    }
}
