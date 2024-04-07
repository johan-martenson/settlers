/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.computer.test;

import org.appland.settlers.computer.ComputerPlayer;
import org.appland.settlers.computer.ConstructionPreparationPlayer;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author johan
 */
public class TestConstructionPreparationPlayer {

    @Test
    public void testCreatePlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", org.appland.settlers.model.PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 10, 10);

        /* Create the computer player */
        ComputerPlayer computerPlayer = new ConstructionPreparationPlayer(player0, map);
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
        ComputerPlayer computerPlayer = new ConstructionPreparationPlayer(player0, map);

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
        ComputerPlayer computerPlayer = new ConstructionPreparationPlayer(player0, map);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Wait for the player to place a forester hut */
        ForesterHut foresterHut = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, ForesterHut.class);
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
        ComputerPlayer computerPlayer = new ConstructionPreparationPlayer(player0, map);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Wait for the player to place a forester hut */
        ForesterHut foresterHut = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, ForesterHut.class);

        /* Verify that the player now places two  woodcutters */
        int woodcutters = 0;
        for (int i = 0; i < 1000; i++) {

            for (Building b : player0.getBuildings()) {
                if (b instanceof Woodcutter) {
                    woodcutters++;
                }
            }

            if (woodcutters == 2) {
                break;
            }

            computerPlayer.turn();
            map.stepTime();
        }

        assertEquals(woodcutters, 2);
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
        ComputerPlayer computerPlayer = new ConstructionPreparationPlayer(player0, map);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Wait for the player to place a forester hut */
        ForesterHut foresterHut = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, ForesterHut.class);

        /* Wait for the player to place a woodcutter */
        Woodcutter woodcutter = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, Woodcutter.class);

        /* Verify that the player now places a sawmill */
        Utils.verifyPlayerPlacesOnlyBuilding(computerPlayer, Sawmill.class);
    }

    @Test
    public void testPlayerPlacesQuarryAfterSawmillIsConstructed() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", org.appland.settlers.model.PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place stone */
        Point point1 = new Point(15, 17);
        Stone stone0 = map.placeStone(point1, Stone.StoneType.STONE_1, 7);

        /* Create the computer player */
        ComputerPlayer computerPlayer = new ConstructionPreparationPlayer(player0, map);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Wait for the player to place a forester hut */
        ForesterHut foresterHut = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, ForesterHut.class);

        /* Wait for the player to place a woodcutter */
        Woodcutter woodcutter = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, Woodcutter.class);

        /* Wait for the player to place the sawmill */
        Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, Sawmill.class);

        /* Verify that the player builds a quarry */
        Utils.verifyPlayerPlacesOnlyBuilding(computerPlayer, Quarry.class);
    }

    @Test
    public void testPlayerPlacesQuarryCloseToStone() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", org.appland.settlers.model.PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place stone */
        Point point1 = new Point(15, 17);
        Stone stone0 = map.placeStone(point1, Stone.StoneType.STONE_1, 7);

        /* Create the computer player */
        ComputerPlayer computerPlayer = new ConstructionPreparationPlayer(player0, map);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Wait for the player to place a forester hut */
        ForesterHut foresterHut = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, ForesterHut.class);

        /* Wait for the player to place a woodcutter */
        Woodcutter woodcutter = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, Woodcutter.class);

        /* Wait for the player to place the sawmill */
        Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, Sawmill.class);

        /* Wait for the player to build a quarry */
        Quarry quarry0 = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, Quarry.class);

        /* Verify that the quarry is built close to the stone */
        assertTrue(quarry0.getPosition().distance(stone0.getPosition()) < 5);
    }

    @Test
    public void testPlayerDestroysQuarryWhenStoneRunsOut() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", org.appland.settlers.model.PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place stone */
        Point point1 = new Point(16, 10);
        Stone stone0 = map.placeStone(point1, Stone.StoneType.STONE_1, 7);

        /* Create the computer player */
        ComputerPlayer computerPlayer = new ConstructionPreparationPlayer(player0, map);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Fast forward until player builds quarry */
        Quarry quarry = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, Quarry.class);

        assertNotNull(quarry);
        assertEquals(map.getBuildingAtPoint(quarry.getPosition()), quarry);

        /* Wait for the stone to run out */
        assertTrue(map.isStoneAtPoint(stone0.getPosition()));
        assertFalse(quarry.isOutOfNaturalResources());

        Utils.waitForStoneToRunOut(computerPlayer, stone0);

        assertFalse(map.isStoneAtPoint(stone0.getPosition()));

        /* Wait for stonemason to return to the quarry */
        for (int i = 0; i < 500; i++) {

            /* Loop until the stonemason is inside the quarry */
            if (quarry.getWorker().isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(quarry.getWorker().isInsideBuilding());
        assertEquals(quarry.getWorker().getPosition(), quarry.getPosition());

        /* Give the stonemason time to figure out that there is no more stone */
        Utils.fastForward(200, map);

        assertTrue(quarry.isOutOfNaturalResources());

        /* Verify that the player destroys the quarry */
        for (int i = 0; i < 150; i++) {

            assertNotNull(computerPlayer);

            try {
                computerPlayer.turn();
            } catch (Exception e) {
                for (StackTraceElement ste : e.getStackTrace()) {
                    System.out.println(ste.getClassName() + "." + ste.getMethodName() + ": " + ste.getLineNumber());
                }

                System.exit(1);
            }

            if (quarry.isBurningDown()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(quarry.isBurningDown());
    }

    @Test
    public void testPlayerRemovesRoadWhenItRemovesQuarry() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", org.appland.settlers.model.PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place stone */
        Point point1 = new Point(15, 17);
        Stone stone0 = map.placeStone(point1, Stone.StoneType.STONE_1, 7);

        /* Create the computer player */
        ComputerPlayer computerPlayer = new ConstructionPreparationPlayer(player0, map);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Fast forward until player builds quarry */
        Quarry quarry = Utils.waitForComputerPlayerToPlaceBuilding(computerPlayer, Quarry.class);

        /* Get the road from the quarry before it's destroyed */
        List<Point> points = map.findWayWithExistingRoads(headquarter0.getFlag().getPosition(), quarry.getFlag().getPosition());

        /* Collect all existing roads */
        Set<Point> otherRoads = new HashSet<>();

        for (Building b : player0.getBuildings()) {

            if (b.equals(quarry)) {
                continue;
            }

            if (b.equals(headquarter0)) {
                continue;
            }

            otherRoads.addAll(map.findWayWithExistingRoads(headquarter0.getPosition(), b.getPosition()));
        }

        /* Wait for the stone to run out */
        Utils.waitForStoneToRunOut(computerPlayer, stone0);

        /* Wait for player to destroy the quarry */
        Utils.waitForBuildingToGetTornDown(computerPlayer, quarry);

        /* Verify that the player removes the road as long as it doesn't connect to another building */
        for (Point point : points) {
            if (otherRoads.contains(point)) {
                continue;
            }

            assertFalse(map.isRoadAtPoint(point));
            assertFalse(map.isFlagAtPoint(point));
        }
    }

    /*
       - Test that the player is smart about choosing a spot to create a forest
       - Test player removes the quarry when it doesn't reach any stones
       - Test that the right parts of the road to the quarry are removed when it's removed
       - Test player builds other quarries when needed
    */
}
