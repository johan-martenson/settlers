/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import static java.awt.Color.BLUE;
import static java.awt.Color.RED;
import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Woodcutter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestPlayer {

    @Test
    public void testPlayerIsSetWhenBuildingIsCreated() {

        /* Create player */
        Player player0 = new Player("Player 0", BLUE);

        /* Create building with the player set */
        Woodcutter woodcutter0 = new Woodcutter(player0);

        /* Verify that the player is set in the building */
        assertNotNull(woodcutter0.getPlayer());
        assertEquals(woodcutter0.getPlayer(), player0);
    }

    @Test
    public void testNameIsSetInPlayer() {

        /* Create player */
        Player player0 = new Player("Player 0", BLUE);

        /* Verify that the name is set */
        assertEquals(player0.getName(), "Player 0");
    }

    @Test
    public void testCreatePlayer() {

        /* Create player 'player one' */
        Player p = new Player("Player one", BLUE);
    }

    @Test
    public void testCreateHouseWithPlayer() {

        /* Create player 'player one' */
        Player p = new Player("Player one", BLUE);

        /* Create house belonging to player one */
        Woodcutter woodcutter0 = new Woodcutter(p);
    }

    @Test
    public void testPlayerIsAlsoSetInBuildingsFlag() {

        /* Create player 'player one' */
        Player p = new Player("Player one", BLUE);

        /* Create house belonging to player one */
        Woodcutter woodcutter0 = new Woodcutter(p);

        /* Verify that the building's flag has the player set correctly */
        assertEquals(woodcutter0.getFlag().getPlayer(), p);
    }

    @Test (expected = Exception.class)
    public void testPlayerCanOnlyCreateOneHeadquarter() throws Exception {

        /* Create player 'player one' */
        Player p = new Player("Player one", BLUE);

        /* Create headquarter belonging to player one */
        Building headquarter0 = new Headquarter(p);

        /* Create game map with one player */
        List<Player> players = new ArrayList<>();
        players.add(p);
        GameMap map = new GameMap(players, 50, 50);

        /* Place first headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Create second headquarter belonging to player one */
        Building headquarter1 = new Headquarter(p);

        /* Place second headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(headquarter1, point1);
    }

    @Test
    public void testPlayerIsAlsoSetInRoad() throws Exception {

        /* Create player 'player one' */
        Player player0 = new Player("Player one", BLUE);

        /* Create headquarter belonging to player one */
        Building headquarter0 = new Headquarter(player0);

        /* Create game map with one player */
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 50, 50);

        /* Place first headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place flag */
        Point point1 = new Point(9, 5);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        assertEquals(road0.getPlayer(), player0);
    }

    @Test
    public void testPlayerIsSetInDriveWay() throws Exception {

        /* Create player 'player one' */
        Player player0 = new Player("Player one", BLUE);

        /* Create headquarter belonging to player one */
        Building headquarter0 = new Headquarter(player0);

        /* Create game map with one player */
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 50, 50);

        /* Place first headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place woodcutter */
        Point point1 = new Point(11, 5);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify that the woodcutter's driveway has the player set correctly */
        Road road0 = map.getRoad(woodcutter0.getPosition(), woodcutter0.getFlag().getPosition());

        assertEquals(road0.getPlayer(), player0);
    }

    @Test (expected = Exception.class)
    public void testCannotPlaceBuildingWithInvalidPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to place a building with an invalid player */
        Player invalidPlayer = new Player("", BLUE);
        Point point1 = new Point(8, 6);
        Building wc = map.placeBuilding(new Woodcutter(invalidPlayer), point1);
    }

    @Test (expected = Exception.class)
    public void testCannotPlaceRoadWithInvalidPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(8, 6);
        Point point3 = new Point(12, 6);
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's not possible to place a road with an invalid player */
        Player invalidPlayer = new Player("", BLUE);
        Point point2 = new Point(10, 6);
        Road road0 = map.placeRoad(invalidPlayer, point1, point2, point3);
    }

    @Test (expected = Exception.class)
    public void testCannotPlaceFlagWithInvalidPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to place a flag with an invalid player */
        Player invalidPlayer = new Player("", BLUE);
        Point point1 = new Point(8, 6);
        Flag flag0 = map.placeFlag(invalidPlayer, point1);
    }

    @Test
    public void testColorIsCorrectInPlayer() {

        /* Create player */
        Player player0 = new Player("Player 0", RED);

        /* Verify that the color is set correctly */
        assertEquals(player0.getColor(), RED);
    }

    @Test (expected = Exception.class)
    public void testCannotHaveTwoPlayersWithSameColor() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);

        /* Verify that it's not possible to have two players with the same color */
        GameMap map = new GameMap(players, 20, 20);
    }
}
