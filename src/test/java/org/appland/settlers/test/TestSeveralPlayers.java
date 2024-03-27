/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.GREEN;
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

        /* Create empty player list */
        List<Player> players = new LinkedList<>();

        /* Create game map choosing no players */
        try {
            GameMap map = new GameMap(players, 20, 20);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testChooseOnePlayer() throws Exception {

        /* Create player list with one player */
        List<Player> players = new LinkedList<>();
        players.add(new Player("Player 0", java.awt.Color.BLUE));

        /* Create game map choosing one players */
        GameMap map = new GameMap(players, 20, 20);

        /* Verify that there is one player in the game map */
        assertEquals(map.getPlayers().size(), 1);

        /* Verify that the one player is correct */
        assertEquals(map.getPlayers().get(0), players.get(0));
    }

    @Test
    public void testChooseTwoPlayer() throws Exception {

        /* Create player list with two players */
        List<Player> players = new LinkedList<>();
        players.add(new Player("Player 0", java.awt.Color.BLUE));
        players.add(new Player("Player 1", GREEN));

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 20, 20);

        /* Verify that there is one player in the game map */
        assertEquals(map.getPlayers().size(), 2);

        /* Verify that the two players are correct */
        assertTrue(map.getPlayers().contains(players.get(0)));
        assertTrue(map.getPlayers().contains(players.get(1)));
    }

    @Test
    public void testCannotPlacePlayersHeadquartersTogether() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter for first player */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the other player can't place a building close to the first player's headquarter */
        Point point1 = new Point(10, 10);

        try {
            Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlacedHouseHasCorrectPlayerForSeveralPlayers() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter for first player */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for second player */
        Point point1 = new Point(40, 40);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Verify that the player is set correctly in both headquarters */
        assertEquals(headquarter0.getPlayer(), player0);
        assertEquals(headquarter1.getPlayer(), player1);
    }

    @Test
    public void testSeveralPlayersCanPlaceAdditionalBuildingsAfterHeadquarter() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter far away from player 0 */
        Point point1 = new Point(90, 84);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place woodcutter for player 0 */
        Point point2 = new Point(10, 6);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);

        /* Place woodcutter for player 1 */
        Point point3 = new Point(90, 80);
        Building woodcutter1 = map.placeBuilding(new Woodcutter(player1), point3);
    }

    @Test
    public void testRetrievePlayersFromGameMap() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Verify that the correct players can be retrieved from the game map */
        List<Player> retrievedPlayers = map.getPlayers();

        assertEquals(retrievedPlayers.size(), 2);
        assertTrue(retrievedPlayers.contains(player0));
        assertTrue(retrievedPlayers.contains(player1));
    }
}
