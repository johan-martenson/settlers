/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerChangeListener;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.PlayerColor.BLUE;
import static org.appland.settlers.model.PlayerColor.RED;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestPlayer {

    @Test
    public void testPlayerIsSetWhenBuildingIsCreated() {

        // Create player
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        // Create building with the player set
        var woodcutter0 = new Woodcutter(player0);

        // Verify that the player is set in the building
        assertNotNull(woodcutter0.getPlayer());
        assertEquals(woodcutter0.getPlayer(), player0);
    }

    @Test
    public void testNameIsSetInPlayer() {

        // Create player
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        // Verify that the name is set
        assertEquals(player0.getName(), "Player 0");
    }

    @Test
    public void testCreatePlayer() {

        // Create game
        var player = new Player("Player one", BLUE, Nation.ROMANS, PlayerType.HUMAN);

        assertEquals(player.getName(), "Player one");
        assertEquals(player.getColor(), BLUE);
    }

    @Test
    public void testCreateHouseWithPlayer() {

        // Create game
        var player = new Player("Player one", BLUE, Nation.ROMANS, PlayerType.HUMAN);

        // Create house belonging to player one
        var woodcutter0 = new Woodcutter(player);

        assertEquals(woodcutter0.getPlayer(), player);
    }

    @Test
    public void testPlayerIsAlsoSetInBuildingsFlag() {

        // Create game
        var player = new Player("Player one", BLUE, Nation.ROMANS, PlayerType.HUMAN);

        // Create house belonging to player one
        var woodcutter0 = new Woodcutter(player);

        // Verify that the building's flag has the player set correctly
        assertEquals(woodcutter0.getFlag().getPlayer(), player);
    }

    @Test
    public void testPlayerCanOnlyCreateOneHeadquarter() throws Exception {

        // Create game
        var player = new Player("Player one", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player), 50, 50);

        // Place first headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player), point0);

        // Create second headquarter belonging to player one
        var headquarter1 = new Headquarter(player);

        // Verify that it's not possible to place a second headquarter
        var point1 = new Point(15, 15);

        try {
            map.placeBuilding(headquarter1, point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlayerIsAlsoSetInRoad() throws Exception {

        // Create game
        var player0 = new Player("Player one", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 50);

        // Place first headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag
        var point1 = new Point(9, 5);
        var flag0 = map.placeFlag(player0, point1);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        assertEquals(road0.getPlayer(), player0);
    }

    @Test
    public void testPlayerIsSetInDriveWay() throws Exception {

        // Create game
        var player0 = new Player("Player one", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 50);

        // Place first headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        var point1 = new Point(11, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Verify that the woodcutter's driveway has the player set correctly
        var road0 = map.getRoad(woodcutter0.getPosition(), woodcutter0.getFlag().getPosition());

        assertEquals(road0.getPlayer(), player0);
    }

    @Test
    public void testCannotPlaceBuildingWithInvalidPlayer() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that it's not possible to place a building with an invalid player
        var invalidPlayer = new Player("", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var point1 = new Point(8, 6);

        try {
            var woodcutter = map.placeBuilding(new Woodcutter(invalidPlayer), point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCannotPlaceRoadWithInvalidPlayer() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag
        var point1 = new Point(8, 6);
        var point3 = new Point(12, 6);
        var flag0 = map.placeFlag(player0, point1);
        var flag1 = map.placeFlag(player0, point3);

        // Verify that it's not possible to place a road with an invalid player
        var invalidPlayer = new Player("", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var point2 = new Point(10, 6);

        try {
            var road0 = map.placeRoad(invalidPlayer, point1, point2, point3);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCannotPlaceFlagWithInvalidPlayer() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that it's not possible to place a flag with an invalid player
        var invalidPlayer = new Player("", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var point1 = new Point(8, 6);

        try {
            var flag0 = map.placeFlag(invalidPlayer, point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testColorIsCorrectInPlayer() {

        // Create player
        var player0 = new Player("Player 0", RED, Nation.ROMANS, PlayerType.HUMAN);

        // Verify that the color is set correctly
        assertEquals(player0.getColor(), RED);
    }

    @Test
    public void testChangeColorInPlayer() {

        // Create player
        var player0 = new Player("Player 0", RED, Nation.ROMANS, PlayerType.HUMAN);

        // Change the color
        player0.setColor(BLUE);

        // Verify that the color is set correctly
        assertEquals(player0.getColor(), BLUE);
    }

    @Test
    public void testChangeNameInPlayer() {

        // Create player
        var player0 = new Player("Player 0", RED, Nation.ROMANS, PlayerType.HUMAN);

        // Change the name
        player0.setName("Another player");

        // Verify that the color is set correctly
        assertEquals(player0.getName(), "Another player");
    }

    @Test
    public void testCannotHaveTwoPlayersWithSameColor() {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", BLUE, Nation.ROMANS, PlayerType.HUMAN);

        // Verify that it's not possible to have two players with the same color
        try {
            var map = new GameMap(List.of(player0, player1), 20, 20);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlayerDefaultNationIsRoman() throws InvalidUserActionException {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        assertEquals(player0.getNation(), Nation.ROMANS);

        // Verify that it's not possible to have two players with the same color
        var map = new GameMap(List.of(player0), 20, 20);

        assertEquals(player0.getNation(), Nation.ROMANS);
    }

    @Test
    public void testSetNationForPlayer() throws InvalidUserActionException {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        assertEquals(player0.getNation(), Nation.ROMANS);

        player0.setNation(Nation.VIKINGS);

        assertEquals(player0.getNation(), Nation.VIKINGS);

        var map = new GameMap(List.of(player0), 20, 20);

        assertEquals(player0.getNation(), Nation.VIKINGS);
    }

    @Test
    public void testUpdatePlayer() {
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var listener = new PlayerChangeListener() {
            boolean playerChanged = false;

            @Override
            public void onPlayerChanged() {
                playerChanged = true;
            }
        };

        // Listen to changes in the player
        player0.addPlayerChangeListener(listener);

        // Verify that updating the player sets the name, color, and nation
        player0.update("Other name", Nation.JAPANESE, PlayerColor.RED);

        assertEquals(player0.getName(), "Other name");
        assertEquals(player0.getColor(), PlayerColor.RED);
        assertEquals(player0.getNation(), Nation.JAPANESE);

        // Verify that the listener was called
        assertTrue(listener.playerChanged);
    }
}
