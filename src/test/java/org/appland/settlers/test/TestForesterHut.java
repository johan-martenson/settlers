/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Forester;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.StoneType;
import org.appland.settlers.model.Storehouse;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.TreeSize;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static org.appland.settlers.model.DetailedVegetation.MEADOW_1;
import static org.appland.settlers.model.DetailedVegetation.MOUNTAIN_1;
import static org.appland.settlers.model.DetailedVegetation.WATER;
import static org.appland.settlers.model.Material.FORESTER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Soldier.Rank.PRIVATE_RANK;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestForesterHut {

    /*
    * TODO:
    *   - test return to storage when forester is out working - doesn't have to use roads then
    * */

    @Test
    public void testForesterHutOnlyNeedsTwoPlanksForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place forester hut */
        Point point22 = new Point(6, 12);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point22);

        /* Deliver two planks */
        Cargo cargo = new Cargo(PLANK, map);

        foresterHut0.putCargo(cargo);
        foresterHut0.putCargo(cargo);

        /* Assign builder */
        Utils.assignBuilder(foresterHut0);

        /* Verify that this is enough to construct the forester hut */
        for (int i = 0; i < 100; i++) {
            assertTrue(foresterHut0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(foresterHut0.isReady());
    }

    @Test
    public void testForesterHutCannotBeConstructedWithOnePlank() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place forester hut */
        Point point22 = new Point(6, 12);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point22);

        /* Deliver two planks */
        Cargo cargo = new Cargo(PLANK, map);

        foresterHut0.putCargo(cargo);

        /* Assign builder */
        Utils.assignBuilder(foresterHut0);

        /* Verify that this is enough to construct the forester hut */
        for (int i = 0; i < 500; i++) {
            assertTrue(foresterHut0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(foresterHut0.isReady());
    }

    @Test
    public void testConstructForester() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(7, 9);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        assertTrue(foresterHut0.isPlanned());
        assertFalse(foresterHut0.needsWorker());

        /* Connect the forester with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        /* Verify that the forester is unoccupied when it's newly constructed */
        assertTrue(foresterHut0.needsWorker());

        /* Verify that the forester hut requires a worker */
        assertTrue(foresterHut0.needsWorker());

        Forester forester = new Forester(null, null);

        /* Assign worker */
        foresterHut0.assignWorker(forester);

        assertFalse(foresterHut0.needsWorker());
        assertEquals(foresterHut0.getWorker(), forester);
    }

    @Test
    public void testPromiseWorkerToUnfinishedForester() {
        ForesterHut foresterHut = new ForesterHut(null);

        assertTrue(foresterHut.isPlanned());

        try {
            foresterHut.promiseWorker(new Forester(null, null));

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testAssignWorkerToUnfinishedForester() {
        ForesterHut foresterHut = new ForesterHut(null);

        assertTrue(foresterHut.isPlanned());

        try {
            foresterHut.assignWorker(new Forester(null, null));

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testAssignWorkerTwice() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(7, 9);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        constructHouse(foresterHut);

        foresterHut.assignWorker(new Forester(null, null));

        try {
            foresterHut.assignWorker(new Forester(null, null));

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPromiseWorkerTwice() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(7, 9);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        constructHouse(foresterHut);

        foresterHut.promiseWorker(new Forester(null, null));

        try {
            foresterHut.promiseWorker(new Forester(null, null));

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testForesterHutIsNotMilitary() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(7, 9);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        assertTrue(foresterHut0.isPlanned());
        assertFalse(foresterHut0.needsWorker());

        /* Connect the forester with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        assertFalse(foresterHut0.isMilitaryBuilding());
        assertEquals(foresterHut0.getNumberOfHostedSoldiers(), 0);
        assertEquals(foresterHut0.getMaxHostedSoldiers(), 0);
    }

    @Test
    public void testForesterHutUnderConstructionNotNeedsWorker() {
        ForesterHut foresterHut = new ForesterHut(null);

        assertFalse(foresterHut.needsWorker());
    }

    @Test
    public void testForesterIsAssignedToForesterHut() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create single player game */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Finish the forester hut */
        constructHouse(foresterHut);

        /* Run game logic twice, once to place courier and once to place forester */
        Utils.fastForward(2, map);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Forester.class);
    }

    @Test
    public void testForesterIsNotASoldier() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create single player game */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Finish the forester hut */
        constructHouse(foresterHut);

        /* Wait for a forester to walk out */
        Forester forester0 = Utils.waitForWorkerOutsideBuilding(Forester.class, player0);

        assertNotNull(forester0);
        assertFalse(forester0.isSoldier());
    }

    @Test
    public void testForesterIsCreatedFromShovel() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create single player game */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all foresters and place shovels in the headquarters */
        Utils.adjustInventoryTo(headquarter, FORESTER, 0);
        Utils.adjustInventoryTo(headquarter, Material.SHOVEL, 1);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Finish the forester hut */
        constructHouse(foresterHut);

        /* Run game logic twice, once to place courier and once to place forester */
        Utils.fastForward(2, map);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Forester.class);
    }

    @Test
    public void testOnlyOneForesterIsAssignedToForesterHut() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create single player game */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Run game logic twice, once to place courier and once to place forester */
        Utils.fastForward(2, map);

        assertTrue(map.getWorkers().size() >= 3);

        /* Keep running the game loop and make sure no more workers are allocated */
        Utils.fastForward(200, map);

        assertTrue(map.getWorkers().size() >= 3);
    }

    @Test
    public void testArrivedForesterRestsInHutAndThenLeaves() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        assertTrue(forester.isInsideBuilding());

        /* Run the game logic 99 times and make sure the forester stays in the hut */
        for (int i = 0; i < 99; i++) {
            assertTrue(forester.isInsideBuilding());
            map.stepTime();
        }

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(forester.isInsideBuilding());
    }

    @Test
    public void testForesterFindsSpotToPlantNewTree() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        assertTrue(forester.isInsideBuilding());

        /* Let the forester rest */
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        Point point = forester.getTarget();
        assertNotNull(point);

        assertFalse(map.isBuildingAtPoint(point));
        assertFalse(map.isRoadAtPoint(point));
        assertFalse(map.isFlagAtPoint(point));
        assertFalse(map.isTreeAtPoint(point));
        assertTrue(forester.isTraveling());
    }

    @Test
    public void testForesterReachesPointToPlantTree() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        /* Let the forester rest */
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        Point point = forester.getTarget();

        assertTrue(forester.isTraveling());

        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertEquals(forester.getPosition(), point);
        assertFalse(forester.isTraveling());
        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
    }

    @Test
    public void testForesterPlantsTree() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        /* Let the forester rest */
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        Point point = forester.getTarget();

        assertTrue(forester.isTraveling());

        /* Wait for the forester to reach the spot */
        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());

        /* Verify that the forester plants a tree */
        Utils.waitForForesterToStopPlantingTree(forester, map);

        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));
        assertNull(forester.getCargo());
    }

    @Test
    public void testForesterPlantsTreesEvenly() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(15, 19);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(20, 20);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        /* Let the forester plant six trees and keep track of where they are planted */
        List<Point> treeLocations = new ArrayList<>();

        for (int i = 0; i < 6; i++) {

            /* Wait for the forester to leave the house */
            Utils.waitForWorkerToBeOutside(forester, map);

            /* Wait for the forester to start planting a tree */
            Utils.waitForForesterToBePlantingTree(forester, map);

            treeLocations.add(forester.getPosition());

            /* Verify that the forester plants a tree */
            Utils.waitForForesterToStopPlantingTree(forester, map);

            /* Wait for the forester to go back home */
            assertEquals(forester.getTarget(), foresterHut.getPosition());

            Utils.waitForWorkerToGoToPoint(map, forester, foresterHut.getPosition());

            assertTrue(forester.isInsideBuilding());
        }

        /* Verify that the planted trees are spread evenly enough */
        int totalX = 0;
        int totalY = 0;

        for (Point point : treeLocations) {
            totalX = totalX + point.x;
            totalY = totalY + point.y;
        }

        double averageX = totalX / (double) 6;
        double averageY = totalY / (double) 6;

        assertTrue(Math.abs(foresterHut.getPosition().x - averageX) < 4);
        assertTrue(Math.abs(foresterHut.getPosition().y - averageY) < 4);
    }

    @Test
    public void testForesterDoesNotPlantTreeOnCrop() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Fill the map with crops */
        Utils.fillMapWithCrops(map);

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut);

        /* Wait for the forester hut to get occupied */
        Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        Forester forester = (Forester) foresterHut.getWorker();

        assertNotNull(forester);

        /* Verify that the forester stays in the hut because there is nowhere to plant a tree */
        for (int i = 0; i < 1000; i++) {
            assertTrue(forester.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testForesterReturnsHomeAfterPlantingTree() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        /* Let the forester rest */
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        Point point = forester.getTarget();

        assertTrue(forester.isTraveling());

        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());

        /* Wait for the forester to plant the tree */
        Utils.waitForForesterToStopPlantingTree(forester, map);

        /* Verify that the forester goes back home */
        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));

        assertEquals(forester.getTarget(), foresterHut.getPosition());
        assertTrue(forester.isTraveling());

        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isInsideBuilding());
    }

    @Test
    public void testGrowthStepsAndTimeForTree() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        /* Let the forester rest */
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        Point point = forester.getTarget();

        assertTrue(forester.isTraveling());

        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());

        /* Wait for the forester to plant the tree */
        Utils.fastForward(50, map);

        assertTrue(forester.isPlanting());
        assertFalse(map.isTreeAtPoint(point));

        map.stepTime();

        /* Make sure there is only one tree */
        foresterHut.stopProduction();

        /* Verify the growth of the tree */
        Tree tree = map.getTreeAtPoint(point);

        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));
        assertEquals(tree.getSize(), TreeSize.NEWLY_PLANTED);

        for (int i = 0; i < 150; i++) {

            /* Allow one steps difference because the tree may get to run stepTime() when it gets added by the forester */
            if (i == 149 && tree.getSize() == TreeSize.SMALL) {
                break;
            }

            assertEquals(tree.getSize(), TreeSize.NEWLY_PLANTED);

            map.stepTime();
        }

        assertEquals(tree.getSize(), TreeSize.SMALL);

        for (int i = 0; i < 150; i++) {
            assertEquals(tree.getSize(), TreeSize.SMALL);

            map.stepTime();
        }

        assertEquals(tree.getSize(), TreeSize.MEDIUM);

        for (int i = 0; i < 150; i++) {
            assertEquals(tree.getSize(), TreeSize.MEDIUM);

            map.stepTime();
        }

        assertEquals(tree.getSize(), TreeSize.FULL_GROWN);

        for (int i = 0; i < 300; i++) {
            assertEquals(tree.getSize(), TreeSize.FULL_GROWN);

            map.stepTime();
        }
    }

    @Test
    public void testForesterPlantsDifferentTypesOfTreeButNotPineapples() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        assertTrue(forester.isInsideBuilding());

        /* Verify that the forester plants different types of trees */
        Map<Tree.TreeType, Integer> treeTypeCount = new HashMap<>();

        for (int i = 0; i < 60; i++) {
            Utils.waitForWorkerToBeOutside(forester, map);

            Utils.waitForForesterToBePlantingTree(forester, map);

            Point point2 = forester.getPosition();

            Utils.waitForForesterToStopPlantingTree(forester, map);

            Tree tree = map.getTreeAtPoint(point2);

            int currentAmount = treeTypeCount.getOrDefault(tree.getTreeType(), 0);

            treeTypeCount.put(tree.getTreeType(), currentAmount + 1);

            Utils.waitForWorkerToBeInside(forester, map);
        }

        for (Tree.TreeType treeType : Tree.TreeType.values()) {

            if (treeType == Tree.TreeType.PINE_APPLE) {
                assertEquals((int)treeTypeCount.getOrDefault(Tree.TreeType.PINE_APPLE, 0), 0);

                continue;
            }

            assertTrue(treeTypeCount.get(treeType) > 0);
        }
    }

    @Test
    public void testForesterHutProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        assertTrue(forester.isInsideBuilding());
        assertNull(forester.getCargo());

        /* Verify that the forester doesn't produce anything */
        for (int i = 0; i < 100; i++) {
            map.stepTime();
            assertNull(forester.getCargo());
            assertTrue(foresterHut.getFlag().getStackedCargo().isEmpty());
        }
    }

    @Test
    public void testForesterStaysInsideWhenThereAreNoSpotsAvailable() throws Exception {

        /* Create a new game map with a single player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        /* Put trees around the forester hut */
        for (Point point : map.getPointsWithinRadius(foresterHut.getPosition(), 20)) {
            if (point.equals(point1)) {
                continue;
            }

            if (map.isBuildingAtPoint(point) ||
                map.isFlagAtPoint(point)     ||
                map.isRoadAtPoint(point)     ||
                map.isStoneAtPoint(point)    ||
                !map.isWithinMap(point)) {
                continue;
            }

            map.placeTree(point, Tree.TreeType.PINE, TreeSize.FULL_GROWN);
        }

        assertTrue(forester.isInsideBuilding());

        /* Verify that the forester stays in the hut */
        for (int i = 0; i < 200; i++) {
            assertTrue(forester.isInsideBuilding());
            map.stepTime();
        }
    }

    @Test
    public void testForesterAvoidsUnreachableSpots() throws Exception {

        /* Create a new game map with a single player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Create a lake with an island inside */
        for (Point point : map.getPointsWithinRadius(point1, 4)) {
            Utils.surroundPointWithVegetation(point, WATER, map);
        }

        Utils.surroundPointWithVegetation(point1, MEADOW_1, map);

        /* Construct the forester hut */
        Utils.constructHouse(foresterHut);

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut);

        /* Occupy the forester hut */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        /* Put trees around the forester hut */
        for (Point point : map.getPointsWithinRadius(foresterHut.getPosition(), 4)) {
            if (point.equals(point1)) {
                continue;
            }

            if (map.isBuildingAtPoint(point)  ||
                    map.isFlagAtPoint(point)  ||
                    map.isRoadAtPoint(point)  ||
                    map.isStoneAtPoint(point) ||
                    map.isInWater(point)) {
                continue;
            }

            map.placeTree(point, Tree.TreeType.PINE, TreeSize.FULL_GROWN);
        }

        assertTrue(forester.isInsideBuilding());

        /* Verify that the forester stays in the hut */
        for (int i = 0; i < 200; i++) {
            assertTrue(forester.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testForesterDoesNotPlantTreesInWater() throws Exception {

        /* Create a new game map with a single player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Create a lake with an island inside */
        for (Point point : map.getPointsWithinRadius(point1, 10)) {
            Utils.surroundPointWithVegetation(point, WATER, map);
        }

        Utils.surroundPointWithVegetation(point1, WATER, map);

        /* Construct the forester hut */
        Utils.constructHouse(foresterHut);

        /* Occupy the forester hut */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        /* Put trees around the forester hut */
        for (Point point : map.getPointsWithinRadius(foresterHut.getPosition(), 4)) {
            if (point.equals(point1)) {
                continue;
            }

            if (map.isBuildingAtPoint(point)  ||
                    map.isFlagAtPoint(point)  ||
                    map.isRoadAtPoint(point)  ||
                    map.isStoneAtPoint(point) ||
                    map.isInWater(point)) {
                continue;
            }

            map.placeTree(point, Tree.TreeType.PINE, TreeSize.FULL_GROWN);
        }

        assertTrue(forester.isInsideBuilding());

        /* Verify that the forester stays in the hut */
        for (int i = 0; i < 200; i++) {
            assertTrue(forester.isInsideBuilding());
            map.stepTime();
        }
    }

    @Test
    public void testForesterDoesNotPlantTreeOnStone() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        /* Put stones all over the map so there is nowhere to plant trees */
        for (Point point : Utils.getAllPointsOnMap(map)) {
            if (point.equals(point1)) {
                continue;
            }

            if (map.isBuildingAtPoint(point) || map.isFlagAtPoint(point) || map.isRoadAtPoint(point) || map.isStoneAtPoint(point)) {
                continue;
            }

            map.placeStone(point, StoneType.STONE_1, 7);
        }

        assertTrue(forester.isInsideBuilding());

        /* Wait for the forester to rest */
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester stays in the hut */
        map.stepTime();

        assertTrue(forester.isInsideBuilding());
    }

    @Test
    public void testForesterDoesNotPlantTreeOnMountain() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Create a small mountain */
        Point point4 = new Point(8, 16);
        Point point5 = new Point(11, 17);
        Point point6 = new Point(14, 16);
        Utils.surroundPointWithVegetation(point4, MOUNTAIN_1, map);
        Utils.surroundPointWithVegetation(point5, MOUNTAIN_1, map);
        Utils.surroundPointWithVegetation(point6, MOUNTAIN_1, map);

        /* Place headquarters */
        Point point0 = new Point(10, 10);
        Headquarter headquarter =  map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 14);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        /* Put stones around the forester hut but not on the mountain */
        for (Point point : map.getPointsWithinRadius(foresterHut.getPosition(), 10)) {
            if (point.equals(point1)) {
                continue;
            }

            if (map.isBuildingAtPoint(point) ||
                map.isFlagAtPoint(point)     ||
                map.isRoadAtPoint(point)     ||
                map.isStoneAtPoint(point)    ||
                map.isOnMineableMountain(point)) {
                continue;
            }

            map.placeTree(point, Tree.TreeType.PINE, TreeSize.FULL_GROWN);
        }

        assertTrue(forester.isInsideBuilding());

        /* Wait for the forester to rest */
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester stays in the hut */
        map.stepTime();

        assertTrue(forester.isInsideBuilding());
    }

    @Test
    public void testForesterGoesBackToStorageWhenForesterHutIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place forester hut */
        Point point26 = new Point(8, 8);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point26);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        /* Destroy the forester hut */
        assertTrue(forester.isInsideBuilding());
        assertEquals(forester.getPosition(), foresterHut0.getPosition());

        foresterHut0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters */
        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FORESTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getPosition());

        /* Verify that the miner is stored correctly in the headquarters */
        assertEquals(headquarter0.getAmount(FORESTER), amount + 1);
    }

    @Test
    public void testForesterGoesBackOnToStorageOnRoadsIfPossibleWhenForesterHutIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(8, 8);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        /* Destroy the forester hut */
        assertTrue(forester.isInsideBuilding());
        assertEquals(forester.getPosition(), foresterHut0.getPosition());

        foresterHut0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters */
        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point point : forester.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testProductionInForesterHutCanBeStopped() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(8, 6);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut and the headquarters */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        assertTrue(forester.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the forester to leave the forester hut */
        for (int i = 0; i < 300; i++) {
            if (!forester.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(forester.isInsideBuilding());

        /* Wait for the forester to go back to the forester hut */
        for (int i = 0; i < 300; i++) {
            if (forester.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(forester.isInsideBuilding());

        /* Stop production and verify that no tree is planted */
        foresterHut0.stopProduction();

        assertFalse(foresterHut0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertTrue(forester.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInForesterHutCanBeResumed() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(8, 6);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut and the headquarters */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        assertTrue(forester.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the forester to leave the forester hut */
        for (int i = 0; i < 300; i++) {
            if (!forester.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(forester.isInsideBuilding());

        /* Wait for the forester to go back to the forester hut */
        for (int i = 0; i < 300; i++) {
            if (forester.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(forester.isInsideBuilding());

        /* Stop production */
        foresterHut0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(forester.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the forester plants trees again */
        foresterHut0.resumeProduction();

        assertTrue(foresterHut0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            if (!forester.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(forester.isInsideBuilding());
    }

    @Test
    public void testAssignedForesterHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create single player game */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarters */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(20, 14);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Finish construction of the forester hut */
        constructHouse(foresterHut0);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), foresterHut0.getFlag());

        /* Wait for forester to get assigned and leave the headquarters */
        List<Forester> workers = Utils.waitForWorkersOutsideBuilding(Forester.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Forester worker = workers.get(0);

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with three players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);
        Player player2 = new Player("Player 2", RED);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        /* Create single player game choosing two players */
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

        /* Place forester hut close to the new border */
        Point point4 = new Point(28, 18);
        ForesterHut foresterHut0 = map.placeBuilding(new ForesterHut(player0), point4);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(forester.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testForesterReturnsHomeWithoutPlantingTreeIfAFlagIsPlacedThereWhilePlanting() throws Exception {

        /* Create a game map with one player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place the forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        /* Wait for the forester to pick a spot to plant a tree where a flag can be placed */
        for (int i = 0; i < 10000; i++) {

            Point spot = forester.getTarget();

            if (spot != null && map.isAvailableFlagPoint(player0, spot)) {
                break;
            }

            map.stepTime();
        }

        Point point = forester.getTarget();

        assertTrue(map.isAvailableFlagPoint(player0, forester.getTarget()));
        assertTrue(forester.isTraveling());

        /* Wait for the forester to reach the spot for the tree */
        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());
        assertEquals(forester.getPosition(), point);

        /* Put a flag on the spot where the forester is planting the tree */
        map.placeFlag(player0, forester.getPosition());

        /* Wait until the forester stops planting and verify that it goes back to the forester hut without planting a tree */
        for (int i = 0; i < 200; i++) {
            if (!forester.isPlanting()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(forester.isPlanting());
        assertFalse(map.isTreeAtPoint(point));

        /* Verify that the forester goes back home */
        assertEquals(forester.getTarget(), foresterHut.getPosition());
        assertTrue(forester.isTraveling());

        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isInsideBuilding());
        assertFalse(map.isTreeAtPoint(point));
    }

    @Test
    public void testForesterReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

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

        /* Place forester hut */
        Point point2 = new Point(14, 4);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2.upLeft());

        /* Connect headquarters and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, foresterHut0.getFlag());

        /* Wait for the forester to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Forester.class, 1, player0);

        Forester forester = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Forester) {
                forester = (Forester) worker;
            }
        }

        assertNotNull(forester);
        assertEquals(forester.getTarget(), foresterHut0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the forester has started walking */
        assertFalse(forester.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the forester continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, flag0.getPosition());

        assertEquals(forester.getPosition(), flag0.getPosition());

        /* Verify that the forester returns to the headquarters when it reaches the flag */
        assertEquals(forester.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getPosition());
    }

    @Test
    public void testForesterContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

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

        /* Place forester hut */
        Point point2 = new Point(14, 4);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2.upLeft());

        /* Connect headquarters and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, foresterHut0.getFlag());

        /* Wait for the forester to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Forester.class, 1, player0);

        Forester forester = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Forester) {
                forester = (Forester) worker;
            }
        }

        assertNotNull(forester);
        assertEquals(forester.getTarget(), foresterHut0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the forester has started walking */
        assertFalse(forester.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the forester continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, flag0.getPosition());

        assertEquals(forester.getPosition(), flag0.getPosition());

        /* Verify that the forester continues to the final flag */
        assertEquals(forester.getTarget(), foresterHut0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, foresterHut0.getFlag().getPosition());

        /* Verify that the forester goes out to forester instead of going directly back */
        assertNotEquals(forester.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testForesterReturnsToStorageIfForesterHutIsDestroyed() throws Exception {

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

        /* Place forester hut */
        Point point2 = new Point(14, 4);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2.upLeft());

        /* Connect headquarters and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, foresterHut0.getFlag());

        /* Wait for the forester to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Forester.class, 1, player0);

        Forester forester = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Forester) {
                forester = (Forester) worker;
            }
        }

        assertNotNull(forester);
        assertEquals(forester.getTarget(), foresterHut0.getPosition());

        /* Wait for the forester to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, flag0.getPosition());

        map.stepTime();

        /* See that the forester has started walking */
        assertFalse(forester.isExactlyAtPoint());

        /* Tear down the forester hut */
        foresterHut0.tearDown();

        /* Verify that the forester continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, foresterHut0.getFlag().getPosition());

        assertEquals(forester.getPosition(), foresterHut0.getFlag().getPosition());

        /* Verify that the forester goes back to storage */
        assertEquals(forester.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testForesterGoesOffroadBackToClosestStorageWhenForesterHutIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(17, 17);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        /* Place a second storage closer to the forester hut */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Wait for the forester to be inside the house */
        Utils.waitForWorkerToBeInside(foresterHut0.getWorker(), map);

        /* Destroy the forester hut */
        assertTrue(forester.isInsideBuilding());
        assertEquals(forester.getPosition(), foresterHut0.getPosition());

        foresterHut0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters */
        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(FORESTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, storehouse0.getPosition());

        /* Verify that the forester is stored correctly in the headquarters */
        assertEquals(storehouse0.getAmount(FORESTER), amount + 1);
    }

    @Test
    public void testForesterReturnsOffroadAndAvoidsBurningStorageWhenForesterHutIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(17, 17);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        /* Place a second storage closer to the forester hut */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Wait for the forester to be inside the house */
        Utils.waitForWorkerToBeInside(foresterHut0.getWorker(), map);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Destroy the forester hut */
        assertTrue(forester.isInsideBuilding());
        assertEquals(forester.getPosition(), foresterHut0.getPosition());

        foresterHut0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters */
        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FORESTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getPosition());

        /* Verify that the forester is stored correctly in the headquarters */
        assertEquals(headquarter0.getAmount(FORESTER), amount + 1);
    }

    @Test
    public void testForesterReturnsOffroadAndAvoidsDestroyedStorageWhenForesterHutIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(17, 17);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        /* Place a second storage closer to the forester hut */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Wait for the forester to be inside the house */
        Utils.waitForWorkerToBeInside(foresterHut0.getWorker(), map);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

        /* Destroy the forester hut */
        assertTrue(forester.isInsideBuilding());
        assertEquals(forester.getPosition(), foresterHut0.getPosition());

        foresterHut0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters */
        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FORESTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getPosition());

        /* Verify that the forester is stored correctly in the headquarters */
        assertEquals(headquarter0.getAmount(FORESTER), amount + 1);
    }

    @Test
    public void testForesterReturnsOffroadAndAvoidsUnfinishedStorageWhenForesterHutIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(17, 17);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        /* Place a second storage closer to the forester hut */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the forester hut */
        assertTrue(forester.isInsideBuilding());
        assertEquals(forester.getPosition(), foresterHut0.getPosition());

        foresterHut0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarters */
        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FORESTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getPosition());

        /* Verify that the forester is stored correctly in the headquarters */
        assertEquals(headquarter0.getAmount(FORESTER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(17, 17);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Place road to connect the headquarters and the forester hut */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), foresterHut0.getFlag());

        /* Finish construction of the forester hut */
        constructHouse(foresterHut0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Forester.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, foresterHut0.getFlag().getPosition());

        /* Tear down the building */
        foresterHut0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarters instead of entering */
        assertEquals(worker.getTarget(), foresterHut0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, foresterHut0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testTwoForestersThatTryToPlantOnSameSpotResultInOneTreeAndBothGoBack() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester huts */
        Point point1 = new Point(9, 5);
        Point point2 = new Point(13, 5);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);
        Building foresterHut1 = map.placeBuilding(new ForesterHut(player0), point2);

        /* Construct the forester huts */
        constructHouse(foresterHut0);
        constructHouse(foresterHut1);

        /* Manually place foresters */
        Forester forester0 = Utils.occupyBuilding(new Forester(player0, map), foresterHut0);
        Forester forester1 = Utils.occupyBuilding(new Forester(player0, map), foresterHut1);

        /* Fill the whole map with trees but leave one free spot */
        Point point3 = new Point(12, 4);

        for (Point point4 : player0.getDiscoveredLand()) {

            /* Place on all points except for one */
            if (point4.equals(point3)) {
                continue;
            }

            try {
                map.placeTree(point4, Tree.TreeType.PINE, TreeSize.FULL_GROWN);
            } catch (Exception e) {}
        }

        /* Wait for the foresters to get out of their huts */
        Utils.waitForWorkersOutsideBuilding(Forester.class, 2, player0);

        /* Verify that both foresters go to the free point but only one plants */
        assertEquals(forester0.getTarget(), point3);
        assertEquals(forester1.getTarget(), point3);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester0, point3);

        assertTrue(forester0.isPlanting() || forester1.isPlanting());

        /* Verify that a tree is planted and both foresters go back */
        Utils.waitForTreeToGetPlanted(map, point3);

        assertTrue(map.isTreeAtPoint(point3));
        assertEquals(forester0.getTarget(), foresterHut0.getPosition());
        assertEquals(forester1.getTarget(), foresterHut1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester0, foresterHut0.getPosition());
    }

    @Test
    public void testForesterHutCannotProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(7, 9);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        /* Wait for the forester hut to get occupied */
        Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        /* Verify that the forester hut can produce */
        assertFalse(foresterHut0.canProduce());
    }

    @Test
    public void testForesterHutReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(6, 12);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut0);

        /* Verify that the reported output is correct */
        assertEquals(foresterHut0.getProducedMaterial().length, 0);
    }

    @Test
    public void testForesterHutReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(6, 12);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(foresterHut0.getTypesOfMaterialNeeded().size(), 1);
        assertTrue(foresterHut0.getTypesOfMaterialNeeded().contains(PLANK));
        assertEquals(foresterHut0.getCanHoldAmount(PLANK), 2);

        for (Material material : Material.values()) {
            if (material == PLANK) {
                continue;
            }

            assertEquals(foresterHut0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testForesterHutReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(6, 12);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(foresterHut0.getTypesOfMaterialNeeded().size(), 0);

        for (Material material : Material.values()) {
            assertEquals(foresterHut0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhenStorageIsBlockedAndForesterHutIsTornDown() throws Exception {

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

        /* Place forester hut */
        Point point2 = new Point(18, 6);
        ForesterHut foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2);

        /* Place road to connect the storehouse with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarters with the forester hut */
        Road road1 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the forester hut and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, foresterHut0);

        /* Wait for the forester hut and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, foresterHut0);

        Worker forester0 = foresterHut0.getWorker();

        assertEquals(forester0.getHome(), foresterHut0);
        assertEquals(foresterHut0.getWorker(), forester0);

        /* Verify that the worker goes to the storage when the forester hut is torn down */
        headquarter0.blockDeliveryOfMaterial(FORESTER);

        Utils.waitForWorkerToBeInside(forester0, map);

        foresterHut0.tearDown();

        map.stepTime();

        assertFalse(forester0.isInsideBuilding());


        Utils.fastForwardUntilWorkerReachesPoint(map, forester0, foresterHut0.getFlag().getPosition());

        assertEquals(forester0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, forester0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(forester0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndForesterHutIsTornDown() throws Exception {

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

        /* Place forester hut */
        Point point2 = new Point(18, 6);
        ForesterHut foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2);

        /* Place road to connect the storehouse with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarters with the forester hut */
        Road road1 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the forester hut and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, foresterHut0);

        /* Wait for the forester hut and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, foresterHut0);

        Worker forester0 = foresterHut0.getWorker();

        assertEquals(forester0.getHome(), foresterHut0);
        assertEquals(foresterHut0.getWorker(), forester0);

        /* Verify that the worker goes to the storage off-road when the forester hut is torn down */
        headquarter0.blockDeliveryOfMaterial(FORESTER);

        Utils.waitForWorkerToBeInside(forester0, map);

        map.removeRoad(road0);

        foresterHut0.tearDown();

        map.stepTime();

        assertFalse(forester0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester0, foresterHut0.getFlag().getPosition());

        assertEquals(forester0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(forester0));
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

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, FORESTER, 1);

        assertEquals(headquarter0.getAmount(FORESTER), 1);

        headquarter0.pushOutAll(FORESTER);

        for (int i = 0; i < 10; i++) {
            Worker worker = Utils.waitForWorkerOutsideBuilding(Forester.class, player0);

            assertEquals(headquarter0.getAmount(FORESTER), 0);
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

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, FORESTER, 1);

        headquarter0.blockDeliveryOfMaterial(FORESTER);
        headquarter0.pushOutAll(FORESTER);

        Worker worker = Utils.waitForWorkerOutsideBuilding(Forester.class, player0);

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

        /* Place forester hut */
        Point point1 = new Point(7, 9);
        ForesterHut foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Place road to connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the forester hut to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(foresterHut0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        /* Verify that worker goes out and then walks away and dies when
           the building is torn down because delivery is blocked in the headquarters
        */
        headquarter0.blockDeliveryOfMaterial(FORESTER);

        Worker worker = foresterHut0.getWorker();

        foresterHut0.tearDown();

        assertEquals(worker.getPosition(), foresterHut0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, foresterHut0.getFlag().getPosition());

        assertEquals(worker.getPosition(), foresterHut0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), foresterHut0.getPosition());
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

        /* Place forester hut */
        Point point1 = new Point(7, 9);
        ForesterHut foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        /* Place road to connect the forester hut with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        /* Wait for a forester to start walking to the forester hut */
        Forester forester = Utils.waitForWorkerOutsideBuilding(Forester.class, player0);

        /* Wait for the forester to go past the headquarters' flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the forester goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(forester.getTarget(), foresterHut0.getPosition());

        headquarter0.blockDeliveryOfMaterial(FORESTER);

        foresterHut0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, foresterHut0.getFlag().getPosition());

        assertEquals(forester.getPosition(), foresterHut0.getFlag().getPosition());
        assertNotEquals(forester.getTarget(), headquarter0.getPosition());
        assertFalse(forester.isInsideBuilding());
        assertNull(foresterHut0.getWorker());
        assertNotNull(forester.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, forester.getTarget());

        Point point = forester.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(forester.isDead());
            assertEquals(forester.getPosition(), point);
            assertTrue(map.getWorkers().contains(forester));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(forester));
    }
}
