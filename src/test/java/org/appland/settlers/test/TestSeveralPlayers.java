/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.appland.settlers.model.PlayerColor.GREEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author johan
 */
public class TestSeveralPlayers {

    @Test
    public void testChooseNoPlayers() {

        // Create empty player list
        var players = new LinkedList<Player>();
        // Create game map choosing no players
        try {
            var map = new GameMap(players, 20, 21);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testChooseOnePlayer() throws Exception {

        // Create player list with one player
        var players = new LinkedList<Player>();        players.add(new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN));

        // Create game map choosing one players
        var map = new GameMap(players, 20, 21);

        // Verify that there is one player in the game map
        assertEquals(map.getPlayers().size(), 1);

        // Verify that the one player is correct
        assertEquals(map.getPlayers().getFirst(), players.getFirst());
    }

    @Test
    public void testChooseTwoPlayer() throws Exception {

        // Create player list with two players
        var players = new LinkedList<Player>();        players.add(new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN));
        players.add(new Player("Player 1", GREEN, Nation.ROMANS, PlayerType.HUMAN));

        // Create game map choosing two players
        var map = new GameMap(players, 20, 21);

        // Verify that there is one player in the game map
        assertEquals(map.getPlayers().size(), 2);

        // Verify that the two players are correct
        assertTrue(map.getPlayers().contains(players.get(0)));
        assertTrue(map.getPlayers().contains(players.get(1)));
    }

    @Test
    public void testCannotPlacePlayersHeadquartersTogether() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);

        var players = new LinkedList<Player>();        players.add(player0);
        players.add(player1);

        // Create game map choosing two players
        var map = new GameMap(players, 20, 21);

        // Place headquarter for first player
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the other player can't place a building close to the first player's headquarter
        var point1 = new Point(10, 10);

        try {
            var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlacedHouseHasCorrectPlayerForSeveralPlayers() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);

        var players = new LinkedList<Player>();        players.add(player0);
        players.add(player1);

        // Create game map choosing two players
        var map = new GameMap(players, 50, 51);

        // Place headquarter for first player
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place headquarter for second player
        var point1 = new Point(40, 40);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Verify that the player is set correctly in both headquarters
        assertEquals(headquarter0.getPlayer(), player0);
        assertEquals(headquarter1.getPlayer(), player1);
    }

    @Test
    public void testSeveralPlayersCanPlaceAdditionalBuildingsAfterHeadquarter() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);

        var players = new LinkedList<Player>();
        players.add(player0);
        players.add(player1);

        // Create game map choosing two players
        var map = new GameMap(players, 100, 101);

        // Place player 0's headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarter far away from player 0
        var point1 = new Point(90, 84);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place woodcutter for player 0
        var point2 = new Point(10, 6);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);

        // Place woodcutter for player 1
        var point3 = new Point(90, 80);
        var woodcutter1 = map.placeBuilding(new Woodcutter(player1), point3);
    }

    @Test
    public void testRetrievePlayersFromGameMap() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);

        var players = new LinkedList<Player>();
        players.add(player0);
        players.add(player1);

        // Create game map choosing two players
        var map = new GameMap(players, 100, 101);

        // Verify that the correct players can be retrieved from the game map
        var retrievedPlayers = map.getPlayers();

        assertEquals(retrievedPlayers.size(), 2);
        assertTrue(retrievedPlayers.contains(player0));
        assertTrue(retrievedPlayers.contains(player1));
    }
}
