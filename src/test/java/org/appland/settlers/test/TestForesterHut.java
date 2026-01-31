package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.actors.Farmer;
import org.appland.settlers.model.actors.Forester;
import org.appland.settlers.model.buildings.Farm;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Vegetation.*;
import static org.appland.settlers.model.Vegetation.WATER;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.*;

public class TestForesterHut {

    // TODO: test return to storage when forester is out working - doesn't have to use roads then

    @Test
    public void testForesterHutOnlyNeedsTwoPlanksForConstruction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place forester hut
        var point22 = new Point(6, 12);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point22);

        // Deliver two planks
        Cargo cargo = new Cargo(PLANK, map);

        foresterHut0.putCargo(cargo);
        foresterHut0.putCargo(cargo);

        // Assign builder
        Utils.assignBuilder(foresterHut0);

        // Verify that this is enough to construct the forester hut
        for (int i = 0; i < 100; i++) {
            assertTrue(foresterHut0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(foresterHut0.isReady());
    }

    @Test
    public void testForesterHutCannotBeConstructedWithOnePlank() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place forester hut
        var point22 = new Point(6, 12);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point22);

        // Deliver two planks
        var plankCargo = new Cargo(PLANK, map);

        foresterHut0.putCargo(plankCargo);

        // Assign builder
        Utils.assignBuilder(foresterHut0);

        // Verify that this is enough to construct the forester hut
        for (int i = 0; i < 500; i++) {
            assertTrue(foresterHut0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(foresterHut0.isReady());
    }

    @Test
    public void testConstructForester() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(7, 9);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        assertTrue(foresterHut0.isPlanned());
        assertFalse(foresterHut0.needsWorker());

        // Connect the forester with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        // Verify that the forester is unoccupied when it's newly constructed
        assertTrue(foresterHut0.needsWorker());

        // Verify that the forester hut requires a worker
        assertTrue(foresterHut0.needsWorker());

        var forester = new Forester(null, null);

        // Assign worker
        foresterHut0.assignWorker(forester);

        assertFalse(foresterHut0.needsWorker());
        assertEquals(foresterHut0.getWorker(), forester);
    }

    @Test
    public void testPromiseWorkerToUnfinishedForester() {
        var foresterHut = new ForesterHut(null);

        assertTrue(foresterHut.isPlanned());

        try {
            foresterHut.promiseWorker(new Forester(null, null));

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testAssignWorkerToUnfinishedForester() {
        var foresterHut = new ForesterHut(null);

        assertTrue(foresterHut.isPlanned());

        try {
            foresterHut.assignWorker(new Forester(null, null));

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testAssignWorkerTwice() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(7, 9);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        constructHouse(foresterHut);

        foresterHut.assignWorker(new Forester(null, null));

        try {
            foresterHut.assignWorker(new Forester(null, null));

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPromiseWorkerTwice() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(7, 9);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        constructHouse(foresterHut);

        foresterHut.promiseWorker(new Forester(null, null));

        try {
            foresterHut.promiseWorker(new Forester(null, null));

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testForesterHutIsNotMilitary() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(7, 9);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        assertTrue(foresterHut0.isPlanned());
        assertFalse(foresterHut0.needsWorker());

        // Connect the forester with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        assertFalse(foresterHut0.isMilitaryBuilding());
        assertEquals(foresterHut0.getNumberOfHostedSoldiers(), 0);
        assertEquals(foresterHut0.getMaxHostedSoldiers(), 0);
    }

    @Test
    public void testForesterHutUnderConstructionNotNeedsWorker() {
        var foresterHut = new ForesterHut(null);

        assertFalse(foresterHut.needsWorker());
    }

    @Test
    public void testForesterIsAssignedToForesterHut() throws Exception {

        // Create players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create single player game
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Finish the forester hut
        constructHouse(foresterHut);

        // Run game logic twice, once to place courier and once to place forester
        Utils.fastForward(2, map);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Forester.class);
    }

    @Test
    public void testForesterIsNotASoldier() throws Exception {

        // Create players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create single player game
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Finish the forester hut
        constructHouse(foresterHut);

        // Wait for a forester to walk out
        var forester0 = Utils.waitForWorkerOutsideBuilding(Forester.class, player0);

        assertNotNull(forester0);
        assertFalse(forester0.isSoldier());
    }

    @Test
    public void testForesterIsCreatedFromShovel() throws Exception {

        // Create players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create single player game
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Remove all foresters and place shovels in the headquarters
        Utils.adjustInventoryTo(headquarter, FORESTER, 0);
        Utils.adjustInventoryTo(headquarter, Material.SHOVEL, 1);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Finish the forester hut
        constructHouse(foresterHut);

        // Run game logic twice, once to place courier and once to place forester
        Utils.fastForward(2, map);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Forester.class);
    }

    @Test
    public void testOnlyOneForesterIsAssignedToForesterHut() throws Exception {

        // Create players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create single player game
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Construct the forester hut
        constructHouse(foresterHut);

        // Run game logic twice, once to place courier and once to place forester
        Utils.fastForward(2, map);

        assertTrue(map.getWorkers().size() >= 3);

        // Keep running the game loop and make sure no more workers are allocated
        Utils.fastForward(200, map);

        assertTrue(map.getWorkers().size() >= 3);
    }

    @Test
    public void testArrivedForesterRestsInHutAndThenLeaves() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        assertTrue(forester.isInsideBuilding());
        assertFalse(foresterHut.isWorking());

        // Run the game logic 99 times and make sure the forester stays in the hut
        for (int i = 0; i < 99; i++) {
            assertTrue(forester.isInsideBuilding());
            assertFalse(foresterHut.isWorking());

            map.stepTime();
        }

        assertTrue(forester.isInsideBuilding());

        // Step once and make sure the forester goes out of the hut
        map.stepTime();

        assertFalse(forester.isInsideBuilding());
        assertTrue(foresterHut.isWorking());
    }

    @Test
    public void testForesterFindsSpotToPlantNewTree() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        assertTrue(forester.isInsideBuilding());

        // Let the forester rest
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());
        assertFalse(foresterHut.isWorking());

        // Step once and make sure the forester goes out of the hut
        map.stepTime();

        assertFalse(forester.isInsideBuilding());
        assertTrue(foresterHut.isWorking());

        var point = forester.getTarget();

        assertNotNull(point);
        assertFalse(map.isBuildingAtPoint(point));
        assertFalse(map.isRoadAtPoint(point));
        assertFalse(map.isFlagAtPoint(point));
        assertFalse(map.isTreeAtPoint(point));
        assertTrue(forester.isTraveling());
    }

    @Test
    public void testForesterReachesPointToPlantTree() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        // Let the forester rest
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        // Step once and make sure the forester goes out of the hut
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        var point = forester.getTarget();

        assertTrue(forester.isTraveling());

        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertEquals(forester.getPosition(), point);
        assertFalse(forester.isTraveling());
        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(foresterHut.isWorking());
    }

    @Test
    public void testForesterPlantsTree() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        // Let the forester rest
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        // Step once and make sure the forester goes out of the hut
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        var point = forester.getTarget();

        assertTrue(forester.isTraveling());

        // Wait for the forester to reach the spot
        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());
        assertTrue(foresterHut.isWorking());

        // Verify that the forester plants a tree
        Utils.waitForForesterToStopPlantingTree(forester, map);

        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));
        assertNull(forester.getCargo());
        assertTrue(foresterHut.isWorking());
    }

    @Test
    public void testForesterPlantsTreesEvenly() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(15, 19);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(20, 20);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        // Let the forester plant six trees and keep track of where they are planted
        var treeLocations = new ArrayList<Point>();

        for (int i = 0; i < 10; i++) {

            // Wait for the forester to leave the house
            Utils.waitForWorkerToBeOutside(forester, map);

            // Wait for the forester to start planting a tree
            Utils.waitForForesterToBePlantingTree(forester, map);

            treeLocations.add(forester.getPosition());

            // Verify that the forester plants a tree
            Utils.waitForForesterToStopPlantingTree(forester, map);

            // Wait for the forester to go back home
            assertEquals(forester.getTarget(), foresterHut.getPosition());

            Utils.waitForWorkerToGoToPoint(map, forester, foresterHut.getPosition());

            assertTrue(forester.isInsideBuilding());
        }

        // Verify that the planted trees are spread evenly enough
        int totalX = 0;
        int totalY = 0;

        for (var point : treeLocations) {
            totalX = totalX + point.x;
            totalY = totalY + point.y;
        }

        double averageX = totalX / (double) 10;
        double averageY = totalY / (double) 10;

        assertTrue(Math.abs(foresterHut.getPosition().x - averageX) < 4);
        assertTrue(Math.abs(foresterHut.getPosition().y - averageY) < 4);
    }

    @Test
    public void testForesterDoesNotPlantTreeOnCrop() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Fill the map with crops
        Utils.fillMapWithCrops(map);

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut);

        // Wait for the forester hut to get occupied
        Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        var forester = (Forester) foresterHut.getWorker();

        assertNotNull(forester);

        // Verify that the forester stays in the hut because there is nowhere to plant a tree
        for (int i = 0; i < 1000; i++) {
            assertTrue(forester.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testForesterReturnsHomeAfterPlantingTree() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        // Let the forester rest
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        // Step once and make sure the forester goes out of the hut
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        var point = forester.getTarget();

        assertTrue(forester.isTraveling());

        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());

        // Wait for the forester to plant the tree
        Utils.waitForForesterToStopPlantingTree(forester, map);

        // Verify that the forester goes back home
        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));
        assertTrue(foresterHut.isWorking());
        assertEquals(forester.getTarget(), foresterHut.getPosition());
        assertTrue(forester.isTraveling());

        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isInsideBuilding());
        assertFalse(foresterHut.isWorking());
    }

    @Test
    public void testGrowthStepsAndTimeForTree() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        // Let the forester rest
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        // Step once and make sure the forester goes out of the hut
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        var point = forester.getTarget();

        assertTrue(forester.isTraveling());

        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());

        // Wait for the forester to plant the tree
        Utils.fastForward(50, map);

        assertTrue(forester.isPlanting());
        assertFalse(map.isTreeAtPoint(point));

        map.stepTime();

        // Make sure there is only one tree
        foresterHut.stopProduction();

        // Verify the growth of the tree
        Tree tree = map.getTreeAtPoint(point);

        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));
        assertEquals(tree.getSize(), Tree.TreeSize.NEWLY_PLANTED);

        for (int i = 0; i < 150; i++) {

            // Allow one steps difference because the tree may get to run stepTime() when it gets added by the forester
            if (i == 149 && tree.getSize() == Tree.TreeSize.SMALL) {
                break;
            }

            assertEquals(tree.getSize(), Tree.TreeSize.NEWLY_PLANTED);

            map.stepTime();
        }

        assertEquals(tree.getSize(), Tree.TreeSize.SMALL);

        for (int i = 0; i < 150; i++) {
            assertEquals(tree.getSize(), Tree.TreeSize.SMALL);

            map.stepTime();
        }

        assertEquals(tree.getSize(), Tree.TreeSize.MEDIUM);

        for (int i = 0; i < 150; i++) {
            assertEquals(tree.getSize(), Tree.TreeSize.MEDIUM);

            map.stepTime();
        }

        assertEquals(tree.getSize(), Tree.TreeSize.FULL_GROWN);

        for (int i = 0; i < 300; i++) {
            assertEquals(tree.getSize(), Tree.TreeSize.FULL_GROWN);

            map.stepTime();
        }
    }

    @Test
    public void testForesterPlantsDifferentTypesOfTreeButNotPineapples() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        assertTrue(forester.isInsideBuilding());

        // Verify that the forester plants different types of trees
        var treeTypeCount = new HashMap<Tree.TreeType, Integer>();

        for (int i = 0; i < 200; i++) {
            Utils.waitForWorkerToBeOutside(forester, map);

            Utils.waitForForesterToBePlantingTree(forester, map);

            var point2 = forester.getPosition();

            Utils.waitForForesterToStopPlantingTree(forester, map);

            Tree tree = map.getTreeAtPoint(point2);

            int currentAmount = treeTypeCount.getOrDefault(tree.getTreeType(), 0);

            treeTypeCount.put(tree.getTreeType(), currentAmount + 1);

            Utils.waitForWorkerToBeInside(forester, map);

            map.removeTree(tree.getPosition());
        }

        for (var treeType : Tree.TreeType.values()) {
            if (treeType == Tree.TreeType.PINE_APPLE) {
                assertEquals((int)treeTypeCount.getOrDefault(Tree.TreeType.PINE_APPLE, 0), 0);

                continue;
            }

            assertTrue(treeTypeCount.get(treeType) > 0);
        }
    }

    @Test
    public void testForesterHutProducesNothing() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        assertTrue(forester.isInsideBuilding());
        assertNull(forester.getCargo());

        // Verify that the forester doesn't produce anything
        for (int i = 0; i < 100; i++) {
            map.stepTime();

            assertNull(forester.getCargo());
            assertTrue(foresterHut.getFlag().getStackedCargo().isEmpty());
        }
    }

    @Test
    public void testForesterStaysInsideWhenThereAreNoSpotsAvailable() throws Exception {

        // Create a new game map with a single player
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        // Put trees around the forester hut
        for (var point : map.getPointsWithinRadius(foresterHut.getPosition(), 20)) {
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

            map.placeTree(point, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        }

        assertTrue(forester.isInsideBuilding());

        // Verify that the forester stays in the hut
        for (int i = 0; i < 200; i++) {
            assertTrue(forester.isInsideBuilding());
            map.stepTime();
        }
    }

    @Test
    public void testForesterAvoidsUnreachableSpots() throws Exception {

        // Create a new game map with a single player
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Create a lake with an island inside
        for (var point : map.getPointsWithinRadius(point1, 4)) {
            Utils.surroundPointWithVegetation(point, WATER, map);
        }

        Utils.surroundPointWithVegetation(point1, MEADOW_1, map);

        // Construct the forester hut
        Utils.constructHouse(foresterHut);

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut);

        // Occupy the forester hut
        var forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        // Put trees around the forester hut
        for (var point : map.getPointsWithinRadius(foresterHut.getPosition(), 4)) {
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

            map.placeTree(point, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        }

        assertTrue(forester.isInsideBuilding());

        // Verify that the forester stays in the hut
        for (int i = 0; i < 200; i++) {
            assertTrue(forester.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testForesterDoesNotPlantTreesInWater() throws Exception {

        // Create a new game map with a single player
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Create a lake with an island inside
        for (var point : map.getPointsWithinRadius(point1, 10)) {
            Utils.surroundPointWithVegetation(point, WATER, map);
        }

        Utils.surroundPointWithVegetation(point1, WATER, map);

        // Construct the forester hut
        Utils.constructHouse(foresterHut);

        // Occupy the forester hut
        var forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        // Put trees around the forester hut
        for (var point : map.getPointsWithinRadius(foresterHut.getPosition(), 4)) {
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

            map.placeTree(point, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        }

        assertTrue(forester.isInsideBuilding());

        // Verify that the forester stays in the hut
        for (int i = 0; i < 200; i++) {
            assertTrue(forester.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testForesterDoesNotPlantTreeOnStone() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        // Put stones all over the map so there is nowhere to plant trees
        for (var point : Utils.getAllPointsOnMap(map)) {
            if (point.equals(point1)) {
                continue;
            }

            if (map.isBuildingAtPoint(point) || map.isFlagAtPoint(point) || map.isRoadAtPoint(point) || map.isStoneAtPoint(point)) {
                continue;
            }

            map.placeStone(point, Stone.StoneType.STONE_1, 7);
        }

        assertTrue(forester.isInsideBuilding());

        // Wait for the forester to rest
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        // Step once and make sure the forester stays in the hut
        map.stepTime();

        assertTrue(forester.isInsideBuilding());
    }

    @Test
    public void testForesterDoesNotPlantTreeOnMountain() throws Exception {

        // Create new game map with one player
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 20, 20);

        // Create a small mountain
        var point4 = new Point(8, 16);
        var point5 = new Point(11, 17);
        var point6 = new Point(14, 16);
        Utils.surroundPointWithVegetation(point4, MOUNTAIN_1, map);
        Utils.surroundPointWithVegetation(point5, MOUNTAIN_1, map);
        Utils.surroundPointWithVegetation(point6, MOUNTAIN_1, map);

        // Place headquarters
        var point0 = new Point(10, 10);
        var headquarter =  map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(10, 14);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        // Put stones around the forester hut but not on the mountain
        for (var point : map.getPointsWithinRadius(foresterHut.getPosition(), 10)) {
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

            map.placeTree(point, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        }

        assertTrue(forester.isInsideBuilding());

        // Wait for the forester to rest
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        // Step once and make sure the forester stays in the hut
        map.stepTime();

        assertTrue(forester.isInsideBuilding());
    }

    @Test
    public void testForesterGoesBackToStorageWhenForesterHutIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place forester hut
        var point26 = new Point(8, 8);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point26);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        // Destroy the forester hut
        assertTrue(forester.isInsideBuilding());
        assertEquals(forester.getPosition(), foresterHut0.getPosition());

        foresterHut0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FORESTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getPosition());

        // Verify that the miner is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(FORESTER), amount + 1);
    }

    @Test
    public void testForesterGoesBackOnToStorageOnRoadsIfPossibleWhenForesterHutIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(8, 8);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        // Destroy the forester hut
        assertTrue(forester.isInsideBuilding());
        assertEquals(forester.getPosition(), foresterHut0.getPosition());

        foresterHut0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), headquarter0.getPosition());

        // Verify that the worker plans to use the roads
        boolean firstStep = true;
        for (var point : forester.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testProductionInForesterHutCanBeStopped() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(8, 6);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut and the headquarters
        var point2 = new Point(6, 4);
        var point3 = new Point(8, 4);
        var point4 = new Point(9, 5);
        var road0 = map.placeRoad(player0, point2, point3, point4);

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        assertTrue(forester.isInsideBuilding());

        // Let the worker rest
        Utils.fastForward(100, map);

        // Wait for the forester to leave the forester hut
        for (int i = 0; i < 300; i++) {
            if (!forester.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(forester.isInsideBuilding());

        // Wait for the forester to go back to the forester hut
        for (int i = 0; i < 300; i++) {
            if (forester.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(forester.isInsideBuilding());

        // Stop production and verify that no tree is planted
        foresterHut0.stopProduction();

        assertFalse(foresterHut0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertTrue(forester.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInForesterHutCanBeResumed() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(8, 6);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut and the headquarters
        var point2 = new Point(6, 4);
        var point3 = new Point(8, 4);
        var point4 = new Point(9, 5);
        var road0 = map.placeRoad(player0, point2, point3, point4);

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        assertTrue(forester.isInsideBuilding());

        // Let the worker rest
        Utils.fastForward(100, map);

        // Wait for the forester to leave the forester hut
        for (int i = 0; i < 300; i++) {
            if (!forester.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(forester.isInsideBuilding());

        // Wait for the forester to go back to the forester hut
        for (int i = 0; i < 300; i++) {
            if (forester.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(forester.isInsideBuilding());

        // Stop production
        foresterHut0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(forester.getCargo());

            map.stepTime();
        }

        // Resume production and verify that the forester plants trees again
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

        // Create players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create single player game
        var map = new GameMap(List.of(player0), 50, 50);

        // Place headquarters
        var point0 = new Point(15, 15);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(20, 14);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        // Finish construction of the forester hut
        constructHouse(foresterHut0);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), foresterHut0.getFlag());

        // Wait for forester to get assigned and leave the headquarters
        var workers = Utils.waitForWorkersOutsideBuilding(Forester.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        // Verify that the player is set correctly in the worker
        var worker = workers.getFirst();

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        // Create player list with three players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place player 2's headquarters
        var point10 = new Point(70, 70);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        // Place player 0's headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(45, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place fortress for player 0
        var point2 = new Point(21, 9);
        var fortress0 = map.placeBuilding(new Fortress(player0), point2);

        // Finish construction of the fortress
        constructHouse(fortress0);

        // Occupy the fortress
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        // Place forester hut close to the new border
        var point4 = new Point(28, 18);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point4);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        // Verify that the worker goes back to its own storage when the fortress is torn down
        fortress0.tearDown();

        assertEquals(forester.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testForesterReturnsHomeWithoutPlantingTreeIfAFlagIsPlacedThereWhilePlanting() throws Exception {

        // Create a game map with one player
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place the forester hut
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

        // Wait for the forester to pick a spot to plant a tree where a flag can be placed
        for (int i = 0; i < 10000; i++) {
            var spot = forester.getTarget();

            if (spot != null && map.isAvailableFlagPoint(player0, spot)) {
                break;
            }

            map.stepTime();
        }

        var point = forester.getTarget();

        assertTrue(map.isAvailableFlagPoint(player0, forester.getTarget()));
        assertTrue(forester.isTraveling());

        // Wait for the forester to reach the spot for the tree
        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());
        assertEquals(forester.getPosition(), point);

        // Put a flag on the spot where the forester is planting the tree
        map.placeFlag(player0, forester.getPosition());

        // Wait until the forester stops planting and verify that it goes back to the forester hut without planting a tree
        for (int i = 0; i < 200; i++) {
            if (!forester.isPlanting()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(forester.isPlanting());
        assertFalse(map.isTreeAtPoint(point));

        // Verify that the forester goes back home
        assertEquals(forester.getTarget(), foresterHut.getPosition());
        assertTrue(forester.isTraveling());

        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isInsideBuilding());
        assertFalse(map.isTreeAtPoint(point));
    }

    @Test
    public void testForesterReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place forester hut
        var point2 = new Point(14, 4);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2.upLeft());

        // Connect headquarters and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, foresterHut0.getFlag());

        // Wait for the forester to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(Forester.class, 1, player0);

        Forester forester = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Forester) {
                forester = (Forester) worker;
            }
        }

        assertNotNull(forester);
        assertEquals(forester.getTarget(), foresterHut0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getFlag().getPosition());

        map.stepTime();

        // See that the forester has started walking
        assertFalse(forester.isExactlyAtPoint());

        // Remove the next road
        map.removeRoad(road1);

        // Verify that the forester continues walking to the flag
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, flag0.getPosition());

        assertEquals(forester.getPosition(), flag0.getPosition());

        // Verify that the forester returns to the headquarters when it reaches the flag
        assertEquals(forester.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getPosition());
    }

    @Test
    public void testForesterContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place forester hut
        var point2 = new Point(14, 4);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2.upLeft());

        // Connect headquarters and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, foresterHut0.getFlag());

        // Wait for the forester to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(Forester.class, 1, player0);

        Forester forester = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Forester) {
                forester = (Forester) worker;
            }
        }

        assertNotNull(forester);
        assertEquals(forester.getTarget(), foresterHut0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getFlag().getPosition());

        map.stepTime();

        // See that the forester has started walking
        assertFalse(forester.isExactlyAtPoint());

        // Remove the current road
        map.removeRoad(road0);

        // Verify that the forester continues walking to the flag
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, flag0.getPosition());

        assertEquals(forester.getPosition(), flag0.getPosition());

        // Verify that the forester continues to the final flag
        assertEquals(forester.getTarget(), foresterHut0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, foresterHut0.getFlag().getPosition());

        // Verify that the forester goes out to forester instead of going directly back
        assertNotEquals(forester.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testForesterReturnsToStorageIfForesterHutIsDestroyed() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place forester hut
        var point2 = new Point(14, 4);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2.upLeft());

        // Connect headquarters and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, foresterHut0.getFlag());

        // Wait for the forester to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(Forester.class, 1, player0);

        Forester forester = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Forester) {
                forester = (Forester) worker;
            }
        }

        assertNotNull(forester);
        assertEquals(forester.getTarget(), foresterHut0.getPosition());

        // Wait for the forester to reach the first flag
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, flag0.getPosition());

        map.stepTime();

        // See that the forester has started walking
        assertFalse(forester.isExactlyAtPoint());

        // Tear down the forester hut
        foresterHut0.tearDown();

        // Verify that the forester continues walking to the next flag
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, foresterHut0.getFlag().getPosition());

        assertEquals(forester.getPosition(), foresterHut0.getFlag().getPosition());

        // Verify that the forester goes back to storage
        assertEquals(forester.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testForesterGoesOffroadBackToClosestStorageWhenForesterHutIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(17, 17);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        // Place a second storage closer to the forester hut
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        constructHouse(storehouse0);

        // Wait for the forester to be inside the house
        Utils.waitForWorkerToBeInside(foresterHut0.getWorker(), map);

        // Destroy the forester hut
        assertTrue(forester.isInsideBuilding());
        assertEquals(forester.getPosition(), foresterHut0.getPosition());

        foresterHut0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(FORESTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, storehouse0.getPosition());

        // Verify that the forester is stored correctly in the headquarters
        assertEquals(storehouse0.getAmount(FORESTER), amount + 1);
    }

    @Test
    public void testForesterReturnsOffroadAndAvoidsBurningStorageWhenForesterHutIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(17, 17);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        // Place a second storage closer to the forester hut
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        constructHouse(storehouse0);

        // Wait for the forester to be inside the house
        Utils.waitForWorkerToBeInside(foresterHut0.getWorker(), map);

        // Destroy the storage
        storehouse0.tearDown();

        // Destroy the forester hut
        assertTrue(forester.isInsideBuilding());
        assertEquals(forester.getPosition(), foresterHut0.getPosition());

        foresterHut0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FORESTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getPosition());

        // Verify that the forester is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(FORESTER), amount + 1);
    }

    @Test
    public void testForesterReturnsOffroadAndAvoidsDestroyedStorageWhenForesterHutIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(17, 17);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        // Place a second storage closer to the forester hut
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        constructHouse(storehouse0);

        // Wait for the forester to be inside the house
        Utils.waitForWorkerToBeInside(foresterHut0.getWorker(), map);

        // Destroy the storage
        storehouse0.tearDown();

        // Wait for the storage to burn down
        Utils.waitForBuildingToBurnDown(storehouse0);

        // Destroy the forester hut
        assertTrue(forester.isInsideBuilding());
        assertEquals(forester.getPosition(), foresterHut0.getPosition());

        foresterHut0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FORESTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getPosition());

        // Verify that the forester is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(FORESTER), amount + 1);
    }

    @Test
    public void testForesterReturnsOffroadAndAvoidsUnfinishedStorageWhenForesterHutIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(17, 17);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        // Place a second storage closer to the forester hut
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Destroy the forester hut
        assertTrue(forester.isInsideBuilding());
        assertEquals(forester.getPosition(), foresterHut0.getPosition());

        foresterHut0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(FORESTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getPosition());

        // Verify that the forester is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(FORESTER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(17, 17);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        // Place road to connect the headquarters and the forester hut
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), foresterHut0.getFlag());

        // Finish construction of the forester hut
        constructHouse(foresterHut0);

        // Wait for a worker to start walking to the building
        var worker = Utils.waitForWorkersOutsideBuilding(Forester.class, 1, player0).getFirst();

        // Wait for the worker to get to the building's flag
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, foresterHut0.getFlag().getPosition());

        // Tear down the building
        foresterHut0.tearDown();

        // Verify that the worker goes to the building and then returns to the headquarters instead of entering
        assertEquals(worker.getTarget(), foresterHut0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, foresterHut0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testTwoForestersThatTryToPlantOnSameSpotResultInOneTreeAndBothGoBack() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 50);

        // Place headquarters
        var point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place forester huts
        var point1 = new Point(9, 5);
        var point2 = new Point(13, 5);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);
        var foresterHut1 = map.placeBuilding(new ForesterHut(player0), point2);

        // Construct the forester huts
        constructHouse(foresterHut0);
        constructHouse(foresterHut1);

        // Manually place foresters
        var forester0 = Utils.occupyBuilding(new Forester(player0, map), foresterHut0);
        var forester1 = Utils.occupyBuilding(new Forester(player0, map), foresterHut1);

        // Fill the whole map with trees but leave one free spot
        var point3 = new Point(12, 4);

        for (var point4 : player0.getDiscoveredLand()) {

            // Place on all points except for one
            if (point4.equals(point3)) {
                continue;
            }

            try {
                map.placeTree(point4, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
            } catch (Exception e) {}
        }

        // Wait for the foresters to get out of their huts
        Utils.waitForWorkersOutsideBuilding(Forester.class, 2, player0);

        // Verify that both foresters go to the free point but only one plants
        assertEquals(forester0.getTarget(), point3);
        assertEquals(forester1.getTarget(), point3);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester0, point3);

        assertTrue(forester0.isPlanting() || forester1.isPlanting());

        // Verify that a tree is planted and both foresters go back
        Utils.waitForTreeToGetPlanted(map, point3);

        assertTrue(map.isTreeAtPoint(point3));
        assertEquals(forester0.getTarget(), foresterHut0.getPosition());
        assertEquals(forester1.getTarget(), foresterHut1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester0, foresterHut0.getPosition());
    }

    @Test
    public void testForesterHutCannotProduce() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(7, 9);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        // Connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        // Wait for the forester hut to get occupied
        var forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        // Verify that the forester hut can produce
        assertFalse(foresterHut0.canProduce());
    }

    @Test
    public void testForesterHutReportsCorrectOutput() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(6, 12);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        // Construct the forester hut
        constructHouse(foresterHut0);

        // Verify that the reported output is correct
        assertEquals(foresterHut0.getProducedMaterial().length, 0);
    }

    @Test
    public void testForesterHutReportsCorrectMaterialsNeededForConstruction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(6, 12);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        // Verify that the reported needed construction material is correct
        assertEquals(foresterHut0.getTypesOfMaterialNeeded().size(), 1);
        assertTrue(foresterHut0.getTypesOfMaterialNeeded().contains(PLANK));
        assertEquals(foresterHut0.getCanHoldAmount(PLANK), 2);

        for (var material : Material.values()) {
            if (material == PLANK) {
                continue;
            }

            assertEquals(foresterHut0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testForesterHutReportsCorrectMaterialsNeededForProduction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(6, 12);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        // Construct the forester hut
        constructHouse(foresterHut0);

        // Verify that the reported needed construction material is correct
        assertEquals(foresterHut0.getTypesOfMaterialNeeded().size(), 0);

        for (var material : Material.values()) {
            assertEquals(foresterHut0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhenStorageIsBlockedAndForesterHutIsTornDown() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(5, 5);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Place forester hut
        var point2 = new Point(18, 6);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2);

        // Place road to connect the storehouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        // Place road to connect the headquarters with the forester hut
        var road1 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        // Add a lot of planks and stones to the headquarters
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the forester hut and the storehouse to get constructed
        Utils.waitForBuildingsToBeConstructed(storehouse, foresterHut0);

        // Wait for the forester hut and the storage to get occupied
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, foresterHut0);

        var forester0 = foresterHut0.getWorker();

        assertEquals(forester0.getHome(), foresterHut0);
        assertEquals(foresterHut0.getWorker(), forester0);

        // Verify that the worker goes to the storage when the forester hut is torn down
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

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(5, 5);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Place forester hut
        var point2 = new Point(18, 6);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2);

        // Place road to connect the storehouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        // Place road to connect the headquarters with the forester hut
        var road1 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        // Add a lot of planks and stones to the headquarters
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the forester hut and the storehouse to get constructed
        Utils.waitForBuildingsToBeConstructed(storehouse, foresterHut0);

        // Wait for the forester hut and the storage to get occupied
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, foresterHut0);

        var forester0 = foresterHut0.getWorker();

        assertEquals(forester0.getHome(), foresterHut0);
        assertEquals(foresterHut0.getWorker(), forester0);

        // Verify that the worker goes to the storage off-road when the forester hut is torn down
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

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that worker goes out and in continuously when sent out without being blocked
        Utils.adjustInventoryTo(headquarter0, FORESTER, 1);

        assertEquals(headquarter0.getAmount(FORESTER), 1);

        headquarter0.pushOutAll(FORESTER);

        for (int i = 0; i < 10; i++) {
            var worker = Utils.waitForWorkerOutsideBuilding(Forester.class, player0);

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

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that worker goes out and in continuously when sent out without being blocked
        Utils.adjustInventoryTo(headquarter0, FORESTER, 1);

        headquarter0.blockDeliveryOfMaterial(FORESTER);
        headquarter0.pushOutAll(FORESTER);

        var worker = Utils.waitForWorkerOutsideBuilding(Forester.class, player0);

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

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(7, 9);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        // Place road to connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the forester hut to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(foresterHut0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut0);

        /* Verify that worker goes out and then walks away and dies when
           the building is torn down because delivery is blocked in the headquarters */
       
        headquarter0.blockDeliveryOfMaterial(FORESTER);

        var worker = foresterHut0.getWorker();

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

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester hut
        var point1 = new Point(7, 9);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        // Place road to connect the forester hut with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the forester hut to get constructed
        Utils.waitForBuildingToBeConstructed(foresterHut0);

        // Wait for a forester to start walking to the forester hut
        var forester = Utils.waitForWorkerOutsideBuilding(Forester.class, player0);

        // Wait for the forester to go past the headquarters' flag
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, headquarter0.getFlag().getPosition());

        map.stepTime();

        // Verify that the forester goes away and dies when the house has been torn down and storage is not possible
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

        var point = forester.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(forester.isDead());
            assertEquals(forester.getPosition(), point);
            assertTrue(map.getWorkers().contains(forester));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(forester));
    }

    @Test
    public void testForesterGoesBackWithoutPlantingIfOtherForesterCameFirstAndStartedPlanting() throws InvalidUserActionException {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag and connect it to the headquarters
        var point1 = new Point(15, 7);
        var flag = map.placeFlag(player0, point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        // Place two forester huts and connect them to the headquarters
        var point2 = new Point(12, 10);
        var point3 = new Point(16, 10);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2);
        var foresterHut1 = map.placeBuilding(new ForesterHut(player0), point3);
        var road1 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), flag);
        var road2 = map.placeAutoSelectedRoad(player0, foresterHut1.getFlag(), flag);

        // Wait for the forester huts to get constructed and occupied
        Utils.waitForBuildingsToBeConstructed(foresterHut0, foresterHut1);
        Utils.waitForNonMilitaryBuildingsToGetPopulated(foresterHut0, foresterHut1);

        // Place trees all over the map except for one open point
        var point4 = new Point(14, 12);

        for (var point : Utils.getAllPointsOnMap(map)) {
            if (point.equals(point4)) {
                continue;
            }

            if (map.isBuildingAtPoint(point) || map.isFlagAtPoint(point) || map.isRoadAtPoint(point)) {
                continue;
            }

            map.placeTree(point, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        }

        map.stepTime();

        // Wait for the second forester to go out to plant. Cheat by only stepping its time
        var secondForester = (Forester) foresterHut0.getWorker();

        for (int i = 0; i < 2_000; i++) {
            if (!secondForester.isInsideBuilding()) {
                break;
            }

            secondForester.stepTime();
        }

        assertFalse(secondForester.isInsideBuilding());

        // Wait for the first forester to go out and start planting. Cheat by only stepping its time so the first
        // forester doesn't start planting yet
        var firstForester = (Forester) foresterHut1.getWorker();

        for (int i = 0; i < 2_000; i++) {
            if (firstForester.isPlanting()) {
                break;
            }

            firstForester.stepTime();
        }

        assertTrue(firstForester.isPlanting());
        assertFalse(secondForester.isPlanting());
        assertEquals(secondForester.getTarget(), firstForester.getPosition());
        assertNotEquals(firstForester, secondForester);

        // Verify that the second forester goes back to its forester hut again without planting anything
        Utils.fastForwardUntilWorkerReachesPoint(map, secondForester, firstForester.getPosition());

        for (int i = 0; i < 2_000; i++) {
            if (secondForester.getPosition().equals(secondForester.getHome().getPosition())) {
                break;
            }

            assertEquals(secondForester.getTarget(), secondForester.getHome().getPosition());
            assertFalse(secondForester.isPlanting());

            map.stepTime();
        }

        assertTrue(secondForester.isInsideBuilding());
        assertFalse(secondForester.isPlanting());
    }

    @Test
    public void testForesterDoesNotGoOutToPlantIfOtherForesterIsAlreadyPlanting() throws InvalidUserActionException {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag and connect it to the headquarters
        var point1 = new Point(15, 7);
        var flag = map.placeFlag(player0, point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        // Place two forester huts and connect them to the headquarters
        var point2 = new Point(12, 10);
        var point3 = new Point(16, 10);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2);
        var foresterHut1 = map.placeBuilding(new ForesterHut(player0), point3);
        var road1 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), flag);
        var road2 = map.placeAutoSelectedRoad(player0, foresterHut1.getFlag(), flag);

        // Wait for the forester huts to get constructed and occupied
        Utils.waitForBuildingsToBeConstructed(foresterHut0, foresterHut1);

        Utils.waitForNonMilitaryBuildingsToGetPopulated(foresterHut0, foresterHut1);

        // Place trees all over the map except for one open point
        var point4 = new Point(14, 12);

        Utils.fillWithTrees(map, point4);

        map.stepTime();

        // Make the first forester go out and start planting. Cheat by only stepping its time so the other doesn't move
        var firstForester = (Forester) foresterHut0.getWorker();
        var secondForester = (Forester) foresterHut1.getWorker();

        for (int i = 0; i < 2_000; i++) {
            if (firstForester.isPlanting()) {
                break;
            }

            firstForester.stepTime();
        }

        assertTrue(firstForester.isPlanting());

        // Verify that the second forester doesn't go out when the first forester is planting on the only available point to plant
        for (int i = 0; i < 2_000; i++) {
            assertTrue(secondForester.isInsideBuilding());
            assertFalse(secondForester.isPlanting());
            assertTrue(firstForester.isPlanting());

            secondForester.stepTime();
        }
    }

    @Test
    public void testForesterDoesNotGoOutToPlantIfFarmerIsAlreadyPlanting() throws InvalidUserActionException {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag and connect it to the headquarters
        var point1 = new Point(15, 7);
        var flag = map.placeFlag(player0, point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        // Place a farm and a forester hut and connect them to the headquarters
        var point2 = new Point(12, 10);
        var point3 = new Point(16, 10);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point2);
        var farm1 = map.placeBuilding(new Farm(player0), point3);
        var road1 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), flag);
        var road2 = map.placeAutoSelectedRoad(player0, farm1.getFlag(), flag);

        // Make it impossible for the headquarters to provide foresters or farmers
        Utils.clearInventory(headquarter0, FORESTER, FARMER, SHOVEL, SCYTHE);

        // Wait for the houses to get constructed
        Utils.waitForBuildingsToBeConstructed(foresterHut, farm1);

        // Let the houses get occupied
        Utils.adjustInventoryTo(headquarter0, FORESTER, 1);
        Utils.adjustInventoryTo(headquarter0, FARMER, 1);

        Utils.waitForNonMilitaryBuildingsToGetPopulated(foresterHut, farm1);

        var forester = (Forester) foresterHut.getWorker();
        var farmer = (Farmer) farm1.getWorker();

        assertTrue(forester.isInsideBuilding());
        assertTrue(farmer.isInsideBuilding());

        // Place trees all over the map except for one open point
        var point4 = new Point(14, 12);

        Utils.fillWithTrees(map, point4);

        map.stepTime();

        // Let the farmer go out and start planting. Cheat by only stepping the farmer's time
        assertTrue(forester.isInsideBuilding());
        assertTrue(farmer.isInsideBuilding());

        for (int i = 0; i < 2_000; i++) {
            if (farmer.isPlanting()) {
                break;
            }

            farmer.stepTime();
        }

        // Verify that the forester doesn't go out when the farmer is planting on the only available point to plant
        for (int i = 0; i < 2_000; i++) {
            assertTrue(farmer.isPlanting());
            assertTrue(forester.isInsideBuilding());
            assertFalse(forester.isPlanting());

            forester.stepTime();
        }
    }

    @Test
    public void testForesterGoesBackWithoutPlantingIfFarmerIsAlreadyPlanting() throws InvalidUserActionException {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag and connect it to the headquarters
        var point1 = new Point(15, 7);
        var flag = map.placeFlag(player0, point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        // Place two farms and connect them to the headquarters
        var point2 = new Point(12, 10);
        var point3 = new Point(16, 10);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point2);
        var farm1 = map.placeBuilding(new Farm(player0), point3);
        var road1 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), flag);
        var road2 = map.placeAutoSelectedRoad(player0, farm1.getFlag(), flag);

        // Make the headquarters unable to provide a farmer or a forester
        Utils.clearInventory(headquarter0, FORESTER, SHOVEL, FARMER, SCYTHE);

        // Wait for the farms to get constructed
        Utils.waitForBuildingsToBeConstructed(foresterHut, farm1);

        // Now, populate the buildings
        Utils.adjustInventoryTo(headquarter0, FORESTER, 1);
        Utils.adjustInventoryTo(headquarter0, FARMER, 1);

        Utils.waitForNonMilitaryBuildingsToGetPopulated(foresterHut, farm1);

        var forester = (Forester) foresterHut.getWorker();
        var farmer = (Farmer) farm1.getWorker();

        assertTrue(forester.isInsideBuilding());
        assertTrue(farmer.isInsideBuilding());

        // Place trees all over the map except for one open point
        var point4 = new Point(14, 12);

        Utils.fillWithTrees(map, point4);

        map.stepTime();

        // Let the forester go out to plant. Cheat by only stepping its time
        for (int i = 0; i < 2_000; i++) {
            if (!forester.isInsideBuilding()) {
                break;
            }

            forester.stepTime();
        }

        assertFalse(forester.isInsideBuilding());
        assertEquals(forester.getTarget(), point4);

        // Let the farmer go out and start planting. Cheat by only stepping its time
        for (int i = 0; i < 2_000; i++) {
            if (farmer.isPlanting()) {
                break;
            }

            farmer.stepTime();
        }

        assertTrue(farmer.isPlanting());
        assertEquals(farmer.getPosition(), point4);
        assertFalse(forester.isInsideBuilding());

        // Let the forester reach the point to plant at. Cheat by only stepping its time
        assertEquals(forester.getTarget(), point4);

        for (int i = 0; i < 2_000; i++) {
            if (forester.getPosition().equals(point4)) {
                break;
            }

            assertEquals(forester.getTarget(), point4);

            forester.stepTime();
        }

        assertEquals(forester.getPosition(), point4);

        // Verify that the forester goes home and doesn't plant
        assertFalse(forester.isInsideBuilding());
        assertFalse(forester.isPlanting());
        assertEquals(forester.getTarget(), forester.getHome().getPosition());
        assertTrue(farmer.isPlanting());

        for (int i = 0; i < 2_000; i++) {
            if (forester.getPosition().equals(forester.getHome().getPosition())) {
                break;
            }

            assertEquals(forester.getTarget(), forester.getHome().getPosition());
            assertFalse(forester.isPlanting());

            map.stepTime();
        }

        assertTrue(forester.isInsideBuilding());
        assertFalse(forester.isPlanting());
    }

    @Test
    public void testForesterGoesBackWithoutPlantingIfFlagHasBeenPlaced() throws InvalidUserActionException {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag and connect it to the headquarters
        var point1 = new Point(15, 7);
        var flag = map.placeFlag(player0, point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        // Place a forester hut and connect it to the headquarters
        var point2 = new Point(12, 10);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point2);
        var road1 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), flag);

        // Wait for the forester hut to get constructed but not occupied
        Utils.waitForBuildingsToBeConstructed(foresterHut);

        // Wait for the forester hut to get occupied
        Utils.waitForNonMilitaryBuildingsToGetPopulated(foresterHut);

        // Wait for the forester to go out to plant at a point where a flag can be placed
        var forester = (Forester) foresterHut.getWorker();

        for (int i = 0; i < 20_000; i++) {
            if (!forester.isInsideBuilding() && map.isAvailableFlagPoint(player0, forester.getTarget())) {
                break;
            }

            map.stepTime();
        }

        assertTrue(map.isAvailableFlagPoint(player0, forester.getTarget()));
        assertFalse(forester.isInsideBuilding());

        // Place a flag on the point where the forester is planning to plant
        map.placeFlag(player0, forester.getTarget());

        // Verify that the forester doesn't start planting and instead goes back to its home
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, forester.getTarget());

        assertFalse(forester.isPlanting());
        assertEquals(forester.getTarget(), forester.getHome().getPosition());

        for (int i = 0; i < 2_000; i++) {
            if (forester.getPosition().equals(forester.getHome().getPosition())) {
                break;
            }

            assertEquals(forester.getTarget(), forester.getHome().getPosition());
            assertFalse(forester.isPlanting());

            map.stepTime();
        }

        assertTrue(forester.isInsideBuilding());
        assertFalse(forester.isPlanting());
    }

    // TODO: update to be about a forester...
    @Test
    @Ignore("For now there is no possibility to place a building where a forester may go out to plant")
    public void testForesterGoesBackWithoutPlantingIfHouseHasBeenPlaced() throws InvalidUserActionException {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag and connect it to the headquarters
        var point1 = new Point(15, 7);
        var flag = map.placeFlag(player0, point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        // Place two farms and connect them to the headquarters
        var point2 = new Point(12, 10);
        var farm = map.placeBuilding(new Farm(player0), point2);
        var road1 = map.placeAutoSelectedRoad(player0, farm.getFlag(), flag);

        // Wait for the farm to get constructed but not occupied
        Utils.waitForBuildingsToBeConstructed(farm);

        // Wait for the farm to get occupied
        Utils.waitForNonMilitaryBuildingsToGetPopulated(farm);

        // Wait for the farmer to go out to plant at a point where a flag can be placed
        var farmer = (Farmer) farm.getWorker();

        for (int i = 0; i < 2_000; i++) {
            if (!farmer.isInsideBuilding() && map.isAvailableHousePoint(player0, farmer.getTarget()) != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(map.isAvailableHousePoint(player0, farmer.getTarget()));
        assertFalse(farmer.isInsideBuilding());

        // Place a house on the point where the farmer is planning to plant
        map.placeBuilding(new Woodcutter(player0), farmer.getTarget());

        // Verify that the farmer doesn't start planting and instead goes back to its home
        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, farmer.getTarget());

        assertFalse(farmer.isPlanting());
        assertEquals(farmer.getTarget(), farmer.getHome().getPosition());

        for (int i = 0; i < 2_000; i++) {
            if (farmer.getPosition().equals(farmer.getHome().getPosition())) {
                break;
            }

            assertEquals(farmer.getTarget(), farmer.getHome().getPosition());
            assertFalse(farmer.isPlanting());

            map.stepTime();
        }

        assertTrue(farmer.isInsideBuilding());
        assertFalse(farmer.isPlanting());
    }

    @Test
    public void testFarmerGoesBackWithoutPlantingIfRoadHasBeenPlaced() throws InvalidUserActionException {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 12);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a forester hut and connect it to the headquarters
        var point1 = new Point(12, 16);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter0.getFlag());

        // Wait for the forester hut to get constructed and occupied
        Utils.waitForBuildingsToBeConstructed(foresterHut);
        Utils.waitForNonMilitaryBuildingsToGetPopulated(foresterHut);

        // Place trees on all points outside the player's land, including the border
        for (var point : Utils.getAllPointsOnMap(map)) {
            if (player0.getOwnedLand().contains(point) && !player0.getBorderPoints().contains(point)) {
                continue;
            }

            map.placeTree(point, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        }

        // Wait for the forester to go out to plant at a point where a road can be placed
        var forester = (Forester) foresterHut.getWorker();

        for (int i = 0; i < 2_000; i++) {
            if (!forester.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(forester.isInsideBuilding());

        // Place a road that covers the point the forester is going to
        var part0 = map.findAutoSelectedRoad(player0, headquarter0.getFlag().getPosition(), forester.getTarget(), null);

        var avoid = (List<Point>) null;

        if (part0.size() > 2) {
            avoid = part0.subList(1, part0.size() - 2);
        }

        var part1 = map.findAutoSelectedRoad(player0, forester.getTarget(), foresterHut.getFlag().getPosition(), new HashSet<>(avoid));

        assertNotNull(part0);
        assertNotNull(part1);

        part1.removeFirst();
        part0.addAll(part1);

        var road2 = map.placeRoad(player0, part0);

        assertTrue(map.isRoadAtPoint(forester.getTarget()));

        // Verify that the forester doesn't start planting and instead goes back to its home
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, forester.getTarget());

        assertFalse(forester.isPlanting());
        assertEquals(forester.getTarget(), forester.getHome().getPosition());

        for (int i = 0; i < 2_000; i++) {
            if (forester.getPosition().equals(forester.getHome().getPosition())) {
                break;
            }

            assertEquals(forester.getTarget(), forester.getHome().getPosition());
            assertFalse(forester.isPlanting());

            map.stepTime();
        }

        assertTrue(forester.isInsideBuilding());
        assertFalse(forester.isPlanting());
    }

    @Test
    public void testFlagIsPlacedDuringPlanting() throws InvalidUserActionException {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a forester hut and connect it to the headquarters
        var point2 = new Point(12, 10);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point2);
        var road1 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter0.getFlag());

        // Wait for the forester hut to get constructed and occupied
        Utils.waitForBuildingsToBeConstructed(foresterHut);
        Utils.waitForNonMilitaryBuildingsToGetPopulated(foresterHut);

        // Place trees on all points outside the player's land, including the border
        for (var point : Utils.getAllPointsOnMap(map)) {
            if (player0.getOwnedLand().contains(point) && !player0.getBorderPoints().contains(point)) {
                continue;
            }

            map.placeTree(point, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        }

        // Wait for the forester to reach the target and start planting
        var forester = (Forester) foresterHut.getWorker();

        Utils.waitForWorkerToBeOutside(forester, map);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, forester.getTarget());

        assertTrue(forester.isPlanting());

        // Place flag where the forester is planting
        var flag1 = map.placeFlag(player0, forester.getPosition());

        // Verify that the forester doesn't plant and instead goes back to the forester hut when it's done planting
        for (int i = 0; i < 2_000; i++) {
            assertTrue(map.isFlagAtPoint(forester.getPosition()));
            assertFalse(map.isTreeAtPoint(forester.getPosition()));

            if (!forester.isPlanting()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(map.isFlagAtPoint(forester.getPosition()));
        assertFalse(map.isTreeAtPoint(forester.getPosition()));
        assertEquals(forester.getTarget(), foresterHut.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, foresterHut.getPosition());

        assertTrue(forester.isInsideBuilding());
        assertFalse(forester.isPlanting());
    }

    @Test
    public void testRoadIsPlacedDuringPlanting() throws InvalidUserActionException {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 12);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a foresterHut and connect it to the headquarters
        var point2 = new Point(12, 16);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point2);
        var road1 = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter0.getFlag());

        // Place trees on all points outside the player's land, including the border
        for (var point : Utils.getAllPointsOnMap(map)) {
            if (player0.getOwnedLand().contains(point) && !player0.getBorderPoints().contains(point)) {
                continue;
            }

            map.placeTree(point, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        }

        // Wait for the foresterHut to get constructed and occupied
        Utils.waitForBuildingsToBeConstructed(foresterHut);
        Utils.waitForNonMilitaryBuildingsToGetPopulated(foresterHut);

        // Wait for the forester to start planting at a point where a road can be placed
        var forester = (Forester) foresterHut.getWorker();

        Utils.waitForWorkerToBeOutside(forester, map);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, forester.getTarget());

        assertTrue(forester.isPlanting());

        // Place a road that covers the point the forester is at
        var part0 = map.findAutoSelectedRoad(player0, headquarter0.getFlag().getPosition(), forester.getPosition(), null);

        var avoid = (List<Point>) null;

        if (part0.size() > 2) {
            avoid = part0.subList(1, part0.size() - 2);
        }

        var part1 = map.findAutoSelectedRoad(player0, forester.getPosition(), foresterHut.getFlag().getPosition(), new HashSet<>(avoid));

        assertNotNull(part0);
        assertNotNull(part1);

        part1.removeFirst();
        part0.addAll(part1);

        var road2 = map.placeRoad(player0, part0);

        assertTrue(map.isRoadAtPoint(forester.getPosition()));

        // Verify that the forester doesn't plant and instead goes back to the foresterHut when it's done planting
        for (int i = 0; i < 2_000; i++) {
            assertTrue(map.isRoadAtPoint(forester.getPosition()));
            assertFalse(map.isTreeAtPoint(forester.getPosition()));

            if (!forester.isPlanting()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(map.isRoadAtPoint(forester.getPosition()));
        assertFalse(map.isTreeAtPoint(forester.getPosition()));
        assertEquals(forester.getTarget(), foresterHut.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, foresterHut.getPosition());

        assertTrue(forester.isInsideBuilding());
        assertFalse(forester.isPlanting());
    }
}
