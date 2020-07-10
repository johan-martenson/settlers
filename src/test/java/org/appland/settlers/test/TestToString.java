package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.BombardedByCatapultMessage;
import org.appland.settlers.model.BorderChange;
import org.appland.settlers.model.BuildingCapturedMessage;
import org.appland.settlers.model.BuildingLostMessage;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Catapult;
import org.appland.settlers.model.Fishery;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameEndedMessage;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GeologistFindMessage;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.MilitaryBuildingCausedLostLandMessage;
import org.appland.settlers.model.MilitaryBuildingOccupiedMessage;
import org.appland.settlers.model.MilitaryBuildingReadyMessage;
import org.appland.settlers.model.NoMoreResourcesMessage;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.StoreHouseIsReadyMessage;
import org.appland.settlers.model.Storehouse;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.TreeConservationProgramActivatedMessage;
import org.appland.settlers.model.TreeConservationProgramDeactivatedMessage;
import org.appland.settlers.model.UnderAttackMessage;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.OFFICER;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.PRIVATE_FIRST_CLASS;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestToString {

    /*
    * TODO:
    *   - Bombarded by catapult message
    * */

    @Test
    public void testStoneToString() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place stone */
        Point point0 = new Point(3, 3);
        Stone stone0 = map.placeStone(point0);

        /* Verify that the toString() method is correct */
        assertEquals(stone0.toString(), "Stone (3, 3)");
    }

    @Test
    public void testTreeToString() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place stone */
        Point point0 = new Point(3, 5);
        Tree tree0 = map.placeTree(point0);

        /* Verify that the toString() method is correct */
        assertEquals(tree0.toString(), "Tree (3, 5)");
    }

    @Test
    public void testPointToString() {

        /* Verify that the toString() method is correct */
        Point point0 = new Point(3, 5);
        assertEquals(point0.toString(), "(3, 5)");
    }

    @Test
    public void testEmptyFlagToString() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place flag */
        Point point0 = new Point(15, 5);
        Flag flag0 = map.placeFlag(player0, point0);

        /* Verify that the toString() method is correct */
        assertEquals(flag0.toString(), "Flag (15, 5)");
    }

    @Test
    public void testGameEventMessages() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place barracks */
        Point point1 = new Point(11, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Place fishery */
        Point point2 = new Point(12, 8);
        Fishery fishery = map.placeBuilding(new Fishery(player0), point2);

        /* Place storehouse */
        Point point3 = new Point(8, 10);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point3);

        /* Place catapult */
        Point point4 = new Point(4, 10);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point4);

        /* Create messages */
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

        /* Verify the toString method of each message type */
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

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place barracks */
        Point point0 = new Point(10, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(15, 15);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Ensure the only soldier in the headquarter is a private */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 1);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to get constructed */
        Utils.waitForBuildingToBeConstructed(barracks0);

        /* Wait for a soldier to start walking to the barracks */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertTrue(military.isExactlyAtPoint());
        assertNotNull(military);
        assertEquals(military.getRank(), Military.Rank.PRIVATE_RANK);

        /* Verify that the toString() method is correct */
        assertEquals(military.toString(), "Private soldier (10, 10)");

        map.stepTime();

        assertFalse(military.isExactlyAtPoint());
        assertEquals(military.toString(), "Private soldier (10, 10) - (11, 9)");
    }

    @Test
    public void testPrivateFirstClassSoldierToString() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place barracks */
        Point point0 = new Point(10, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(15, 15);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Ensure the only soldier in the headquarter is a private */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 1);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to get constructed */
        Utils.waitForBuildingToBeConstructed(barracks0);

        /* Wait for a soldier to start walking to the barracks */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertTrue(military.isExactlyAtPoint());
        assertNotNull(military);
        assertEquals(military.getRank(), Military.Rank.PRIVATE_FIRST_CLASS_RANK);

        /* Verify that the toString() method is correct */
        assertEquals(military.toString(), "Private first class soldier (10, 10)");

        map.stepTime();

        assertFalse(military.isExactlyAtPoint());
        assertEquals(military.toString(), "Private first class soldier (10, 10) - (11, 9)");
    }

    @Test
    public void testSergeantSoldierToString() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place barracks */
        Point point0 = new Point(10, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(15, 15);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Ensure the only soldier in the headquarter is a private */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 1);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to get constructed */
        Utils.waitForBuildingToBeConstructed(barracks0);

        /* Wait for a soldier to start walking to the barracks */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertTrue(military.isExactlyAtPoint());
        assertNotNull(military);
        assertEquals(military.getRank(), Military.Rank.SERGEANT_RANK);

        /* Verify that the toString() method is correct */
        assertEquals(military.toString(), "Sergeant soldier (10, 10)");

        map.stepTime();

        assertFalse(military.isExactlyAtPoint());
        assertEquals(military.toString(), "Sergeant soldier (10, 10) - (11, 9)");
    }

    @Test
    public void testOfficerSoldierToString() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place barracks */
        Point point0 = new Point(10, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(15, 15);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Ensure the only soldier in the headquarter is a private */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 1);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to get constructed */
        Utils.waitForBuildingToBeConstructed(barracks0);

        /* Wait for a soldier to start walking to the barracks */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertTrue(military.isExactlyAtPoint());
        assertNotNull(military);
        assertEquals(military.getRank(), Military.Rank.OFFICER_RANK);

        /* Verify that the toString() method is correct */
        assertEquals(military.toString(), "Officer soldier (10, 10)");

        map.stepTime();

        assertFalse(military.isExactlyAtPoint());
        assertEquals(military.toString(), "Officer soldier (10, 10) - (11, 9)");
    }

    @Test
    public void testGeneralSoldierToString() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place barracks */
        Point point0 = new Point(10, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(15, 15);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Ensure the only soldier in the headquarter is a private */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 1);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to get constructed */
        Utils.waitForBuildingToBeConstructed(barracks0);

        /* Wait for a soldier to start walking to the barracks */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertTrue(military.isExactlyAtPoint());
        assertNotNull(military);
        assertEquals(military.getRank(), Military.Rank.GENERAL_RANK);

        /* Verify that the toString() method is correct */
        assertEquals(military.toString(), "General soldier (10, 10)");

        map.stepTime();

        assertFalse(military.isExactlyAtPoint());
        assertEquals(military.toString(), "General soldier (10, 10) - (11, 9)");
    }

    @Test
    public void testRoadToString() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(15, 15);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Verify that the toString() method is correct */
        String roadToString = road0.toString();

        assertTrue(roadToString.equals("Road (11, 9) - (15, 15)") || roadToString.equals("Road (15, 15) - (11, 9)"));
    }

    @Test
    public void testBorderChangeEventToString() {

        /* Create a border change instance */
        Player player = new Player("Player 0", Color.RED);
        List<Point> newBorder = Arrays.asList(new Point(10, 10), new Point(12, 10));
        List<Point> removedBorder = Arrays.asList(new Point(8, 10), new Point(10, 12));

        BorderChange borderChange = new BorderChange(player, newBorder, removedBorder);

        /* Verify that the toString() method returns the correct string */
        assertEquals(borderChange.toString(), "Border change for Player 0, added [(10, 10), (12, 10)], removed [(8, 10), (10, 12)]");
    }

    @Test
    public void testCargoToString() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Create a cargo instance */
        Cargo cargo = new Cargo(GOLD, map);

        headquarter0.getFlag().putCargo(cargo);

        cargo.setTarget(headquarter0);

        /* Verify that the toString() method returns the correct string */
        assertEquals(cargo.toString(), "Cargo of gold to Headquarter (10, 10), at (11, 9)");
    }
}
