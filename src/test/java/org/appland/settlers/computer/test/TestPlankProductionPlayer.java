/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.computer.test;

import org.appland.settlers.computer.ComputerPlayer;
import org.appland.settlers.computer.PlankProductionPlayer;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author johan
 */
public class TestPlankProductionPlayer {

    @Test
    public void testCreatePlankProductionPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", org.appland.settlers.model.PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 10, 10);

        /* Create the computer player */
        ComputerPlayer computerPlayer = new PlankProductionPlayer(player0, map);
    }

    @Test
    public void testPlayerFirstPlacesForesterHut() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", org.appland.settlers.model.PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Create the computer player */
        ComputerPlayer computerPlayer = new PlankProductionPlayer(player0, map);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the player starts with placing a forester */
        for (int i = 0; i < 20; i++) {
            computerPlayer.turn();

            if (player0.getBuildings().size() > 1) {
                break;
            }

            map.stepTime();
        }

        assertEquals(player0.getBuildings().size(), 2);

        Utils.verifyPlayersBuildingsContain(player0, ForesterHut.class);
    }

    @Test
    public void testPlayerDoesNothingUntilForesterHutIsCompleted() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", org.appland.settlers.model.PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Create the computer player */
        ComputerPlayer computerPlayer = new PlankProductionPlayer(player0, map);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Wait for the player to place a forester hut */
        ForesterHut foresterHut = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, ForesterHut.class);

        /* Verify that the player doesn't build anything else until the forester hut is done */
        int amount = player0.getBuildings().size();

        for (int i = 0; i < 1000; i++) {
            if (foresterHut.isReady()) {
                break;
            }

            assertEquals(player0.getBuildings().size(), amount);

            computerPlayer.turn();

            map.stepTime();
        }

        assertTrue(foresterHut.isReady());
    }

    @Test
    public void testPlayerPlacesWoodcutterWhenForesterHutIsCompleted() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", org.appland.settlers.model.PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Create the computer player */
        ComputerPlayer computerPlayer = new PlankProductionPlayer(player0, map);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Wait for the player to place a forester hut */
        ForesterHut foresterHut = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, ForesterHut.class);

        /* Wait for the forester hut to get finished */
        Utils.waitForBuildingToGetConstructedWithComputerPlayer(computerPlayer, foresterHut);

        /* Verify that the player now places a woodcutter */
        Utils.verifyPlayerPlacesOnlyBuilding(computerPlayer, Woodcutter.class);
    }

    @Test
    public void testPlayerPlacesSawmillWhenWoodcutterIsCompleted() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", org.appland.settlers.model.PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Create the computer player */
        ComputerPlayer computerPlayer = new PlankProductionPlayer(player0, map);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Wait for the player to place a forester hut */
        ForesterHut foresterHut = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, ForesterHut.class);

        /* Wait for the forester hut to get finished */
        Utils.waitForBuildingToGetConstructedWithComputerPlayer(computerPlayer, foresterHut);

        /* Wait for the player to place a woodcutter */
        Woodcutter woodcutter = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, Woodcutter.class);

        /* Wait for the woodcutter to get finished */
        Utils.waitForBuildingToGetConstructedWithComputerPlayer(computerPlayer, woodcutter);

        /* Verify that the player now places a sawmill */
        Utils.verifyPlayerPlacesOnlyBuilding(computerPlayer, Sawmill.class);
    }

    @Test
    public void testPlayerDoesNothingAfterPlacingSawmill() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", org.appland.settlers.model.PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Create the computer player */
        ComputerPlayer computerPlayer = new PlankProductionPlayer(player0, map);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Wait for the player to place a forester hut */
        ForesterHut foresterHut = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, ForesterHut.class);

        /* Wait for the forester hut to get finished */
        Utils.waitForBuildingToGetConstructedWithComputerPlayer(computerPlayer, foresterHut);

        /* Wait for the player to place a woodcutter */
        Woodcutter woodcutter = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, Woodcutter.class);

        /* Wait for the woodcutter to get finished */
        Utils.waitForBuildingToGetConstructedWithComputerPlayer(computerPlayer, woodcutter);

        /* Wait for the player to place the sawmill */
        Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, Sawmill.class);

        /* Verify that the player does nothing after the sawmill is placed */
        int amount = player0.getBuildings().size();

        for (int i = 0; i < 1000; i++) {
            computerPlayer.turn();

            assertEquals(map.getBuildings().size(), amount);

            map.stepTime();
        }
    }

    /*
       - Test that the player is smart about choosing a spot to create a forest

    */
}
