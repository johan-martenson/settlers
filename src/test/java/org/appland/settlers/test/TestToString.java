package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.BorderChange;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Vegetation;
import org.appland.settlers.model.actors.Armorer;
import org.appland.settlers.model.actors.Baker;
import org.appland.settlers.model.actors.Butcher;
import org.appland.settlers.model.actors.Carpenter;
import org.appland.settlers.model.actors.CatapultWorker;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.IronFounder;
import org.appland.settlers.model.actors.Minter;
import org.appland.settlers.model.actors.Shipwright;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Catapult;
import org.appland.settlers.model.buildings.Fishery;
import org.appland.settlers.model.buildings.Harbor;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.IronSmelter;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Shipyard;
import org.appland.settlers.model.buildings.SlaughterHouse;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.messages.BombardedByCatapultMessage;
import org.appland.settlers.model.messages.BuildingCapturedMessage;
import org.appland.settlers.model.messages.BuildingLostMessage;
import org.appland.settlers.model.messages.GameEndedMessage;
import org.appland.settlers.model.messages.GeologistFindMessage;
import org.appland.settlers.model.messages.MilitaryBuildingCausedLostLandMessage;
import org.appland.settlers.model.messages.MilitaryBuildingOccupiedMessage;
import org.appland.settlers.model.messages.MilitaryBuildingReadyMessage;
import org.appland.settlers.model.messages.NoMoreResourcesMessage;
import org.appland.settlers.model.messages.StoreHouseIsReadyMessage;
import org.appland.settlers.model.messages.TreeConservationProgramActivatedMessage;
import org.appland.settlers.model.messages.TreeConservationProgramDeactivatedMessage;
import org.appland.settlers.model.messages.UnderAttackMessage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.junit.Assert.*;

public class TestToString {

    /*
    * TODO:
    *   - Bombarded by catapult message
    */

    @Test
    public void testStoneToString() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create game map
        var map = new GameMap(players, 40, 40);

        // Place stone
        var point0 = new Point(3, 3);
        var stone0 = map.placeStone(point0, Stone.StoneType.STONE_1, 7);

        // Verify that the toString() method is correct
        assertEquals(stone0.toString(), "Stone (3, 3)");
    }

    @Test
    public void testTreeToString() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create game map
        var map = new GameMap(players, 40, 40);

        // Place stone
        var point0 = new Point(3, 5);
        var tree0 = map.placeTree(point0, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Verify that the toString() method is correct
        assertEquals(tree0.toString(), "Tree (3, 5)");
    }

    @Test
    public void testPointToString() {

        // Verify that the toString() method is correct
        var point0 = new Point(3, 5);

        assertEquals(point0.toString(), "(3, 5)");
    }

    @Test
    public void testEmptyFlagToString() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create game map
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place flag
        var point0 = new Point(15, 5);
        var flag0 = map.placeFlag(player0, point0);

        // Verify that the toString() method is correct
        assertEquals(flag0.toString(), "Flag (15, 5)");
    }

    @Test
    public void testGameEventMessages() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create game map
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place barracks
        var point1 = new Point(11, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point1);

        // Place fishery
        var point2 = new Point(12, 8);
        var fishery = map.placeBuilding(new Fishery(player0), point2);

        // Place storehouse
        var point3 = new Point(8, 10);
        var storehouse = map.placeBuilding(new Storehouse(player0), point3);

        // Place catapult
        var point4 = new Point(4, 10);
        var catapult = map.placeBuilding(new Catapult(player0), point4);

        // Create messages
        MilitaryBuildingReadyMessage militaryBuildingReadyMessage = new MilitaryBuildingReadyMessage(barracks0);
        MilitaryBuildingOccupiedMessage militaryBuildingOccupiedMessage = new MilitaryBuildingOccupiedMessage(barracks0);
        MilitaryBuildingCausedLostLandMessage militaryBuildingCausedLostLandMessage = new MilitaryBuildingCausedLostLandMessage(barracks0);
        UnderAttackMessage underAttackMessage = new UnderAttackMessage(barracks0);
        GeologistFindMessage geologistFindMessage = new GeologistFindMessage(point1, IRON);
        NoMoreResourcesMessage noMoreResourcesMessage = new NoMoreResourcesMessage(fishery);
        BuildingLostMessage buildingLostMessage = new BuildingLostMessage(barracks0);
        BuildingCapturedMessage buildingCapturedMessage = new BuildingCapturedMessage(barracks0);
        StoreHouseIsReadyMessage storeHouseIsReadyMessage = new StoreHouseIsReadyMessage(storehouse);
        TreeConservationProgramActivatedMessage treeConservationProgramActivatedMessage = new TreeConservationProgramActivatedMessage();
        TreeConservationProgramDeactivatedMessage treeConservationProgramDeactivatedMessage = new TreeConservationProgramDeactivatedMessage();
        GameEndedMessage gameEndedMessage = new GameEndedMessage(player0);
        BombardedByCatapultMessage bombardedByCatapultMessage = new BombardedByCatapultMessage(catapult, barracks0);

        // Verify the toString method of each message type
        assertEquals(militaryBuildingReadyMessage.toString(), "Message: Barracks (11, 5) is ready");
        assertEquals(militaryBuildingOccupiedMessage.toString(), "Message: Barracks (11, 5) is occupied");
        assertEquals(militaryBuildingCausedLostLandMessage.toString(), "Message: Barracks (11, 5) has caused lost land");
        assertEquals(underAttackMessage.toString(), "Message: Barracks (11, 5) is under attack");
        assertEquals(geologistFindMessage.toString(), "Message: Geologist found iron at (11, 5)");
        assertEquals(noMoreResourcesMessage.toString(), "Message: No more resources in Fishery at (12, 8)");
        assertEquals(buildingLostMessage.toString(), "Message: Barracks (11, 5) lost to enemy");
        assertEquals(buildingCapturedMessage.toString(), "Message: Barracks (11, 5) captured by enemy");
        assertEquals(storeHouseIsReadyMessage.toString(), "Message: Storehouse (8, 10) is ready");
        assertEquals(treeConservationProgramActivatedMessage.toString(), "Message: Tree conservation program is activated");
        assertEquals(treeConservationProgramDeactivatedMessage.toString(), "Message: Tree conservation program is deactivated");
        assertEquals(gameEndedMessage.toString(), "Message: Game ended with Player 0 as winner");
        assertEquals(bombardedByCatapultMessage.toString(), "Message: Barracks (11, 5) hit by catapult (4, 10)");
    }

    @Test
    public void testPrivateSoldierToString() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create game map
        var map = new GameMap(players, 40, 40);

        // Place barracks
        var point0 = new Point(10, 10);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place barracks
        var point1 = new Point(15, 15);
        var barracks0 = map.placeBuilding(new Barracks(player0), point1);

        // Ensure the only soldier in the headquarters is a private
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 1);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        // Connect the barracks with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        // Wait for the barracks to get constructed
        Utils.waitForBuildingToBeConstructed(barracks0);

        // Wait for a soldier to start walking to the barracks
        var military = Utils.waitForSoldierOutsideBuilding(player0);

        assertTrue(military.isExactlyAtPoint());
        assertNotNull(military);
        assertEquals(military.getRank(), Soldier.Rank.PRIVATE_RANK);

        // Verify that the toString() method is correct
        assertEquals(military.toString(), "Private soldier (10, 10) (WALKING_TO_TARGET)");

        map.stepTime();

        assertFalse(military.isExactlyAtPoint());
        assertEquals(military.toString(), "Private soldier (10, 10) - (11, 9) (WALKING_TO_TARGET)");
    }

    @Test
    public void testPrivateFirstClassSoldierToString() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create game map
        var map = new GameMap(players, 40, 40);

        // Place barracks
        var point0 = new Point(10, 10);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place barracks
        var point1 = new Point(15, 15);
        var barracks0 = map.placeBuilding(new Barracks(player0), point1);

        // Ensure the only soldier in the headquarter is a private
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 1);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        // Connect the barracks with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        // Wait for the barracks to get constructed
        Utils.waitForBuildingToBeConstructed(barracks0);

        // Wait for a soldier to start walking to the barracks
        var military = Utils.waitForSoldierOutsideBuilding(player0);

        assertTrue(military.isExactlyAtPoint());
        assertNotNull(military);
        assertEquals(military.getRank(), Soldier.Rank.PRIVATE_FIRST_CLASS_RANK);

        // Verify that the toString() method is correct
        assertEquals(military.toString(), "Private first class soldier (10, 10) (WALKING_TO_TARGET)");

        map.stepTime();

        assertFalse(military.isExactlyAtPoint());
        assertEquals(military.toString(), "Private first class soldier (10, 10) - (11, 9) (WALKING_TO_TARGET)");
    }

    @Test
    public void testSergeantSoldierToString() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create game map
        var map = new GameMap(players, 40, 40);

        // Place barracks
        var point0 = new Point(10, 10);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place barracks
        var point1 = new Point(15, 15);
        var barracks0 = map.placeBuilding(new Barracks(player0), point1);

        // Ensure the only soldier in the headquarter is a private
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 1);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        // Connect the barracks with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        // Wait for the barracks to get constructed
        Utils.waitForBuildingToBeConstructed(barracks0);

        // Wait for a soldier to start walking to the barracks
        var military = Utils.waitForSoldierOutsideBuilding(player0);

        assertTrue(military.isExactlyAtPoint());
        assertNotNull(military);
        assertEquals(military.getRank(), Soldier.Rank.SERGEANT_RANK);

        // Verify that the toString() method is correct
        assertEquals(military.toString(), "Sergeant soldier (10, 10) (WALKING_TO_TARGET)");

        map.stepTime();

        assertFalse(military.isExactlyAtPoint());
        assertEquals(military.toString(), "Sergeant soldier (10, 10) - (11, 9) (WALKING_TO_TARGET)");
    }

    @Test
    public void testOfficerSoldierToString() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create game map
        var map = new GameMap(players, 40, 40);

        // Place barracks
        var point0 = new Point(10, 10);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place barracks
        var point1 = new Point(15, 15);
        var barracks0 = map.placeBuilding(new Barracks(player0), point1);

        // Ensure the only soldier in the headquarters is a private
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 1);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        // Connect the barracks with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        // Wait for the barracks to get constructed
        Utils.waitForBuildingToBeConstructed(barracks0);

        // Wait for a soldier to start walking to the barracks
        var military = Utils.waitForSoldierOutsideBuilding(player0);

        assertTrue(military.isExactlyAtPoint());
        assertNotNull(military);
        assertEquals(military.getRank(), Soldier.Rank.OFFICER_RANK);

        // Verify that the toString() method is correct
        assertEquals(military.toString(), "Officer soldier (10, 10) (WALKING_TO_TARGET)");

        map.stepTime();

        assertFalse(military.isExactlyAtPoint());
        assertEquals(military.toString(), "Officer soldier (10, 10) - (11, 9) (WALKING_TO_TARGET)");
    }

    @Test
    public void testGeneralSoldierToString() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create game map
        var map = new GameMap(players, 40, 40);

        // Place barracks
        var point0 = new Point(10, 10);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place barracks
        var point1 = new Point(15, 15);
        var barracks0 = map.placeBuilding(new Barracks(player0), point1);

        // Ensure the only soldier in the headquarter is a private
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 1);

        // Connect the barracks with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        // Wait for the barracks to get constructed
        Utils.waitForBuildingToBeConstructed(barracks0);

        // Wait for a soldier to start walking to the barracks
        var military = Utils.waitForSoldierOutsideBuilding(player0);

        assertTrue(military.isExactlyAtPoint());
        assertNotNull(military);
        assertEquals(military.getRank(), Soldier.Rank.GENERAL_RANK);

        // Verify that the toString() method is correct
        assertEquals(military.toString(), "General soldier (10, 10) (WALKING_TO_TARGET)");

        map.stepTime();

        assertFalse(military.isExactlyAtPoint());
        assertEquals(military.toString(), "General soldier (10, 10) - (11, 9) (WALKING_TO_TARGET)");
    }

    @Test
    public void testRoadToString() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create game map
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(10, 10);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place barracks
        var point1 = new Point(15, 15);
        var flag0 = map.placeFlag(player0, point1);

        // Connect the barracks with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        // Verify that the toString() method is correct
        var roadToString = road0.toString();

        assertTrue(roadToString.equals("Road (11, 9) - (15, 15)") || roadToString.equals("Road (15, 15) - (11, 9)"));
    }

    @Test
    public void testBorderChangeEventToString() {

        // Create a border change instance
        var player = new Player("Player 0", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var newBorder = Arrays.asList(new Point(10, 10), new Point(12, 10));
        var removedBorder = Arrays.asList(new Point(8, 10), new Point(10, 12));

        var borderChange = new BorderChange(player, newBorder, removedBorder, new ArrayList<>(), new ArrayList<>());

        // Verify that the toString() method returns the correct string
        assertEquals(borderChange.toString(), "Border change for Player 0, added [(10, 10), (12, 10)], removed [(8, 10), (10, 12)]");
    }

    @Test
    public void testCargoToString() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create game map
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(10, 10);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Create a cargo instance
        var cargo = new Cargo(GOLD, map);

        headquarter0.getFlag().putCargo(cargo);

        cargo.setTarget(headquarter0);

        // Verify that the toString() method returns the correct string
        assertEquals(cargo.toString(), "Cargo of gold to Headquarter (10, 10), at (11, 9)");
    }

    @Test
    public void testCatapultWorkerToString() throws Exception {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place catapult
        var point3 = new Point(7, 9);
        var catapult = map.placeBuilding(new Catapult(player0), point3);

        // Place a road between the headquarters and the catapult
        var road0 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter.getFlag());

        // Finish construction of the catapult
        Utils.constructHouse(catapult);

        assertTrue(catapult.needsWorker());

        // Verify that a catapult worker leaves the headquarters
        var catapultWorker = Utils.waitForWorkerOutsideBuilding(CatapultWorker.class, player0);

        assertTrue(map.getWorkers().contains(catapultWorker));

        // Verify that the toString() method is correct
        assertTrue(catapultWorker.isExactlyAtPoint());
        assertEquals(catapultWorker.toString(), "Catapult worker (5, 5)");

        map.stepTime();

        assertFalse(catapultWorker.isExactlyAtPoint());
        assertEquals(catapultWorker.toString(), "Catapult worker (5, 5) - (6, 4)");
    }

    @Test
    public void testBakeryToString() throws Exception {

        // Create new single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place bakery
        var point3 = new Point(7, 9);
        var bakery = map.placeBuilding(new Bakery(player0), point3);

        // Connect the bakery with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

        // Finish construction of the bakery
        Utils.constructHouse(bakery);

        assertTrue(bakery.needsWorker());

        // Verify that a bakery worker leaves the headquarters
        var baker = Utils.waitForWorkerOutsideBuilding(Baker.class, player0);

        // Verify that the toString() method is correct
        assertTrue(baker.isExactlyAtPoint());
        assertEquals(baker.toString(), "Baker (5, 5)");

        map.stepTime();

        assertFalse(baker.isExactlyAtPoint());
        assertEquals(baker.toString(), "Baker (5, 5) - (6, 4)");
    }

    @Test
    public void testButcherToString() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughter house
        var point3 = new Point(7, 9);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        // Connect the slaughter house with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse);

        assertTrue(slaughterHouse.needsWorker());

        // Verify that a bakery worker leaves the headquarters
        var butcher = Utils.waitForWorkerOutsideBuilding(Butcher.class, player0);

        // Verify that the toString() method is correct
        assertTrue(butcher.isExactlyAtPoint());
        assertEquals(butcher.toString(), "Butcher (5, 5)");

        map.stepTime();

        assertFalse(butcher.isExactlyAtPoint());
        assertEquals(butcher.toString(), "Butcher (5, 5) - (6, 4)");
    }

    @Test
    public void testArmorerToString() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Place road to connect the armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Finish construction of the armory
        Utils.constructHouse(armory0);

        assertTrue(armory0.needsWorker());

        // Wait for an armorer to start walking to the armory
        var armorer = Utils.waitForWorkerOutsideBuilding(Armorer.class, player0);

        // Verify that the toString() method is correct
        assertTrue(armorer.isExactlyAtPoint());
        assertEquals(armorer.toString(), "Armorer (5, 5)");

        map.stepTime();

        assertFalse(armorer.isExactlyAtPoint());
        assertEquals(armorer.toString(), "Armorer (5, 5) - (6, 4)");
    }

    @Test
    public void testSawmillWorkerToString() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point3 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point3);

        // Place a road between the headquarters and the sawmill
        var road0 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter.getFlag());

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        assertTrue(sawmill.needsWorker());

        // Wait for a sawmill worker to start walking to the sawmill
        var carpenter = Utils.waitForWorkerOutsideBuilding(Carpenter.class, player0);

        // Verify that the toString() method is correct
        assertTrue(carpenter.isExactlyAtPoint());
        assertEquals(carpenter.toString(), "Sawmill worker (5, 5)");

        map.stepTime();

        assertFalse(carpenter.isExactlyAtPoint());
        assertEquals(carpenter.toString(), "Sawmill worker (5, 5) - (6, 4)");
    }

    @Test
    public void testMintToString() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place mint
        var point3 = new Point(7, 9);
        var mint = map.placeBuilding(new Mint(player0), point3);

        // Connect the mint with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, mint.getFlag(), headquarter.getFlag());

        // Finish construction of the mint
        Utils.constructHouse(mint);

        assertTrue(mint.needsWorker());

        // Wait for a minter to leave the headquarters
        var minter = Utils.waitForWorkerOutsideBuilding(Minter.class, player0);

        // Verify that tovar is correct
        assertTrue(minter.isExactlyAtPoint());
        assertEquals(minter.toString(), "Minter (5, 5)");

        map.stepTime();

        assertFalse(minter.isExactlyAtPoint());
        assertEquals(minter.toString(), "Minter (5, 5) - (6, 4)");
    }

    @Test
    public void testIronFounderToString() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place iron smelter
        var point3 = new Point(7, 9);
        var ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        // Place a road between the headquarter and the iron smelter
        var road0 = map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), headquarter.getFlag());

        // Finish construction of the iron smelter
        Utils.constructHouse(ironSmelter);

        assertTrue(ironSmelter.needsWorker());

        // Wait for an iron founder to leave the headquarter
        var ironFounder0 = Utils.waitForWorkerOutsideBuilding(IronFounder.class, player0);

        // Verify toString
        assertNotNull(ironFounder0);
        assertTrue(ironFounder0.isExactlyAtPoint());
        assertEquals(ironFounder0.toString(), "Iron founder (5, 5)");

        map.stepTime();

        assertFalse(ironFounder0.isExactlyAtPoint());
        assertEquals(ironFounder0.toString(), "Iron founder (5, 5) - (6, 4)");
    }

    @Test
    public void testCourierToString() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 30, 30);

        // Place headquarter
        var point0 = new Point(19, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter.getFlag());

        // Wait for a courier to get assigned to the road
        var courier = Utils.waitForWorkerOutsideBuilding(Courier.class, player0);

        // Verify that toString is correct
        assertNotNull(courier);
        assertTrue(courier.isExactlyAtPoint());
        assertEquals(courier.toString(), "Courier for Road (10, 4) - (20, 4) at (19, 5)");

        map.stepTime();

        assertFalse(courier.isExactlyAtPoint());
        assertEquals(courier.toString(), "Courier for Road (10, 4) - (20, 4) walking (19, 5) - (20, 4)");
    }

    @Test
    public void testShipUnderConstructionToString() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place a lake
        for (int i = 7; i < 53; i += 2) {
            var point = new Point(i, 11);  //  7, 11  --  51, 11

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(57, 11);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(8, 8);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        // Place shipyard
        var point3 = new Point(14, 6);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarter
        var road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has plenty of materials
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Verify toString for the ship when it's under construction
        assertEquals(ship.toString(), "Ship under construction (" + ship.getPosition().x + ", " + ship.getPosition().y + ")");
    }

    @Test
    public void testShipReadyToString() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place a lake
        for (int i = 7; i < 53; i += 2) {
            var point = new Point(i, 11);  //  7, 11  --  51, 11

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(57, 11);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(8, 8);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        // Place shipyard
        var point3 = new Point(14, 6);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarter
        var road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has plenty of materials
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get finished
        Utils.waitForShipToGetBuilt(map, ship);

        // Verify toString for the ship when it's ready
        assertEquals(ship.toString(), "Ship (" + ship.getPosition().x + ", " + ship.getPosition().y + ")");
    }
}
