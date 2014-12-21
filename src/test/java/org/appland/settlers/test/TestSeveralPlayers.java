/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import java.util.LinkedList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author johan
 */
public class TestSeveralPlayers {

    @Test(expected = Exception.class)
    public void testChooseNoPlayers() throws Exception {

        /* Create empty player list */
        List<Player> players = new LinkedList<>();

        /* Create game map choosing no players */
        GameMap map = new GameMap(players, 20, 20);
    }

    @Test
    public void testChooseOnePlayer() throws Exception {

        /* Create player list with one player */
        List<Player> players = new LinkedList<>();
        players.add(new Player("Player 0"));

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
        players.add(new Player("Player 0"));
        players.add(new Player("Player 1"));

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 20, 20);

        /* Verify that there is one player in the game map */
        assertEquals(map.getPlayers().size(), 2);

        /* Verify that the two players are correct */
        assertTrue(map.getPlayers().contains(players.get(0)));
        assertTrue(map.getPlayers().contains(players.get(1)));
    }

    @Test(expected = Exception.class)
    public void testCannotPlacePlayersHeadquartersTogether() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0");
        Player player1 = new Player("Player 1");

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter for first player */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the other player can't place a building close to the 
           first player's headquarter
        */
        Point point1 = new Point(10, 10);
        Building headquarter1 = map.placeBuilding(new Headquarter(player1), point1);
    }

    @Test
    public void testPlacedHouseHasCorrectPlayerForSeveralPlayers() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0");
        Player player1 = new Player("Player 1");

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter for first player */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for second player */
        Point point1 = new Point(40, 40);
        Building headquarter1 = map.placeBuilding(new Headquarter(player1), point1);
    
        /* Verify that the player is set correctly in both headquarters */
        assertEquals(headquarter0.getPlayer(), player0);
        assertEquals(headquarter1.getPlayer(), player1);
    }

    @Test(expected = Exception.class)
    public void testCannotPlaceBuildingWithoutPlayerSetWithSeveralPlayers() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0");
        Player player1 = new Player("Player 1");

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter for first player */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);
    }

    @Test
    public void testSeveralPlayersCanPlaceAdditionalBuildingsAfterHeadquarter() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0");
        Player player1 = new Player("Player 1");

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);
    
        /* Place player 1's headquarter far away from player 0 */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(90, 90);
        map.placeBuilding(headquarter1, point1);
    
        /* Place woodcutter for player 0 */
        Point point2 = new Point(10, 6);
        Building woodcutter0 = new Woodcutter(player0);        
        map.placeBuilding(woodcutter0, point2);

        /* Place woodcutter for player 1 */
        Point point3 = new Point(90, 80);
        Building woodcutter1 = new Woodcutter(player1);
        map.placeBuilding(woodcutter1, point3);
    }
}
