/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.*;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.RED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
        Player player = new Player("Player one", BLUE);

        assertEquals(player.getName(), "Player one");
        assertEquals(player.getColor(), BLUE);
    }

    @Test
    public void testCreateHouseWithPlayer() {

        /* Create player 'player one' */
        Player player = new Player("Player one", BLUE);

        /* Create house belonging to player one */
        Woodcutter woodcutter0 = new Woodcutter(player);

        assertEquals(woodcutter0.getPlayer(), player);
    }

    @Test
    public void testPlayerIsAlsoSetInBuildingsFlag() {

        /* Create player 'player one' */
        Player player = new Player("Player one", BLUE);

        /* Create house belonging to player one */
        Woodcutter woodcutter0 = new Woodcutter(player);

        /* Verify that the building's flag has the player set correctly */
        assertEquals(woodcutter0.getFlag().getPlayer(), player);
    }

    @Test
    public void testPlayerCanOnlyCreateOneHeadquarter() throws Exception {

        /* Create player 'player one' */
        Player player = new Player("Player one", BLUE);

        /* Create game map with one player */
        List<Player> players = new ArrayList<>();
        players.add(player);
        GameMap map = new GameMap(players, 50, 50);

        /* Place first headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player), point0);

        /* Create second headquarter belonging to player one */
        Headquarter headquarter1 = new Headquarter(player);

        /* Verify that it's not possible to place a second headquarter */
        Point point1 = new Point(15, 15);

        try {
            map.placeBuilding(headquarter1, point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlayerIsAlsoSetInRoad() throws Exception {

        /* Create player 'player one' */
        Player player0 = new Player("Player one", BLUE);

        /* Create game map with one player */
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 50, 50);

        /* Place first headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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

        /* Create game map with one player */
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 50, 50);

        /* Place first headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(11, 5);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify that the woodcutter's driveway has the player set correctly */
        Road road0 = map.getRoad(woodcutter0.getPosition(), woodcutter0.getFlag().getPosition());

        assertEquals(road0.getPlayer(), player0);
    }

    @Test
    public void testCannotPlaceBuildingWithInvalidPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to place a building with an invalid player */
        Player invalidPlayer = new Player("", BLUE);
        Point point1 = new Point(8, 6);

        try {
            Building woodcutter = map.placeBuilding(new Woodcutter(invalidPlayer), point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCannotPlaceRoadWithInvalidPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(8, 6);
        Point point3 = new Point(12, 6);
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's not possible to place a road with an invalid player */
        Player invalidPlayer = new Player("", BLUE);
        Point point2 = new Point(10, 6);

        try {
            Road road0 = map.placeRoad(invalidPlayer, point1, point2, point3);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCannotPlaceFlagWithInvalidPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to place a flag with an invalid player */
        Player invalidPlayer = new Player("", BLUE);
        Point point1 = new Point(8, 6);

        try {
            Flag flag0 = map.placeFlag(invalidPlayer, point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testColorIsCorrectInPlayer() {

        /* Create player */
        Player player0 = new Player("Player 0", RED);

        /* Verify that the color is set correctly */
        assertEquals(player0.getColor(), RED);
    }

    @Test
    public void testChangeColorInPlayer() {

        /* Create player */
        Player player0 = new Player("Player 0", RED);

        /* Change the color */
        player0.setColor(BLUE);

        /* Verify that the color is set correctly */
        assertEquals(player0.getColor(), BLUE);
    }

    @Test
    public void testChangeNameInPlayer() {

        /* Create player */
        Player player0 = new Player("Player 0", RED);

        /* Change the name */
        player0.setName("Another player");

        /* Verify that the color is set correctly */
        assertEquals(player0.getName(), "Another player");
    }

    @Test
    public void testCannotHaveTwoPlayersWithSameColor() {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);

        /* Verify that it's not possible to have two players with the same color */
        try {
            GameMap map = new GameMap(players, 20, 20);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlayerDefaultNationIsRoman() throws InvalidUserActionException {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);

        assertEquals(player0.getNation(), Nation.ROMANS);

        List<Player> players = new ArrayList<>();
        players.add(player0);

        assertEquals(player0.getNation(), Nation.ROMANS);

        /* Verify that it's not possible to have two players with the same color */
        GameMap map = new GameMap(players, 20, 20);

        assertEquals(player0.getNation(), Nation.ROMANS);
    }

    @Test
    public void testSetNationForPlayer() throws InvalidUserActionException {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);

        assertEquals(player0.getNation(), Nation.ROMANS);

        player0.setNation(Nation.VIKINGS);

        List<Player> players = new ArrayList<>();
        players.add(player0);

        assertEquals(player0.getNation(), Nation.VIKINGS);

        GameMap map = new GameMap(players, 20, 20);

        assertEquals(player0.getNation(), Nation.VIKINGS);
    }
}
