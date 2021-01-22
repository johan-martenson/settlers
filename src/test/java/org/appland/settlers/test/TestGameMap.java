/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Farm;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author johan
 */
public class TestGameMap {

    @Test
    public void testSetPlayers() throws Exception {

        /* Create single player game */
        List<Player> players = new ArrayList<>();
        players.add(new Player("Some name", Color.YELLOW));

        GameMap map = new GameMap(players, 100, 100);

        /* Verify that the player list can be set */
        Player player0 = new Player("Player 0", GREEN);
        Player player1 = new Player("Player 1", BLUE);

        List<Player> newPlayers = new ArrayList<>();

        newPlayers.add(player0);
        newPlayers.add(player1);

        map.setPlayers(newPlayers);

        assertEquals(map.getPlayers().size(), 2);
        assertTrue(map.getPlayers().contains(player0));
        assertTrue(map.getPlayers().contains(player1));
    }

    @Test
    public void testSetStartingPoints() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Verify that starting points can be set */
        List<Point> points = new ArrayList<>();

        points.add(new Point(3, 3));
        points.add(new Point(7, 7));

        assertEquals(map.getStartingPoints().size(), 0);

        map.setStartingPoints(points);

        assertEquals(map.getStartingPoints().size(), 2);
    }

    @Test
    public void testMapIsCorrectAfterSetPlayers() throws Exception {

        /* Create single player game */
        List<Player> players = new ArrayList<>();
        players.add(new Player("Some player", Color.YELLOW));

        GameMap map = new GameMap(players, 100, 100);

        /* Verify that the player list can be set */
        Player player0 = new Player("Player 0", GREEN);
        Player player1 = new Player("Player 1", BLUE);

        List<Player> newPlayers = new ArrayList<>();

        newPlayers.add(player0);
        newPlayers.add(player1);

        map.setPlayers(newPlayers);

        assertEquals(map.getPlayers().size(), 2);
        assertEquals(map.getPlayers().get(0).getMap(), map);
        assertEquals(map.getPlayers().get(1).getMap(), map);
    }

    @Test
    public void testPlaceBuildingOnEmptyMap() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Verify that a headquarter can be placed */
        Point point0 = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        assertEquals(headquarter.getPosition(), point0);
    }

    @Test
    public void testPlaceSameBuildingTwice() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(4, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify that it's not possible to place the woodcutter a second time */
        Point point2 = new Point(2, 8);

        try {
            map.placeBuilding(woodcutter, point2);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlaceTwoBuildingsOnSameSpot() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(12, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(4, 4);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify that it's not possible to place a quarry on the same spot as the woodcutter */
        Quarry quarry0 = new Quarry(player0);

        try {
            map.placeBuilding(quarry0, point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlaceFlagsOnSamePlace() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(4, 4);
        map.placeFlag(player0, point1);

        /* Verify that it's not possible to place a second flag on top of the first flag */
        try {
            map.placeFlag(player0, point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testAddRoadBetweenFlagsNotOnMap() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Create flags but don't put them on the map */
        Point point1 = new Point(4, 4);
        Point point2 = new Point(8, 4);
        Flag flag0 = new Flag(point1);
        Flag flag1 = new Flag(point2);

        /* Verify that it's not possible to place a road between the flags */
        try {
            Road road = map.placeAutoSelectedRoad(player0, flag0, flag1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testFindWayBetweenSameFlag() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(3, 3);
        map.placeFlag(player0, point1);

        /* Test that it's not possible to find a way from and to same point */
        try {
            map.findWayWithExistingRoads(point1, point1);

            fail();
        } catch (InvalidRouteException e) {}
    }

    @Test
    public void testCreateMinimalMap() throws Exception {

        /* Verify that it's possible to create a minimal game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        new GameMap(players, 5, 5);
    }

    @Test
    public void testCreateTooSmallMap() {

        /* Verify that it's not possible to create a too small game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        try {
            new GameMap(players, 4, 4);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCreateMapWithNegativeHeight() {

        /* Verify that it's not possible to create a map with negative height */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        try {
            new GameMap(players, 10, -10);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCreateMapWithNegativeWidth() {

        /* Verify that it's not possible to create a game with a negative width */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        try {
            new GameMap(players, -30, 10);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testGetFlags() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(6, 6);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place farm */
        Point farmPoint = new Point(4, 4);
        Farm farm = map.placeBuilding(new Farm(player0), farmPoint);

        /* Verify that getFlags returns all three flags */
        List<Flag> flags = map.getFlags();

        assertEquals(flags.size(), 3);
        assertTrue(flags.contains(flag0));
        assertTrue(flags.contains(farm.getFlag()));
    }

    @Test
    public void testPlaceBuildingOnInvalidPoint() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to place a farm on an invalid point */
        Point point1 = new Point(3, 3);
        Farm farm = new Farm(player0);

        point1.x = point1.x + 1;

        try {
            map.placeBuilding(farm, point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlaceFlagOnInvalidPoint() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to place a flag on an invalid point */
        Point point1 = new Point(4, 6);

        point1.x = point1.x + 1;

        try {
            map.placeFlag(player0, point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlaceBuildingSetsMap() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the map field of the building gets set correctly when it's placed */
        Point point1 = new Point(5, 5);
        Farm farm = new Farm(player0);

        assertNull(farm.getMap());

        map.placeBuilding(farm, point1);

        assertEquals(farm.getMap(), map);
    }

    @Test
    public void testFindWayBetweenHouseAndItsFlag() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(5, 5);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify that it's possible to find a way between the woodcutter and its flag */
        Point point2 = new Point(6, 4);

        assertNotNull(map.getRoad(point1, point2));

        assertNotNull(map.findWayOffroad(point1, point2, null));
        assertNotNull(map.findWayOffroad(point2, point1, null));

        assertNotNull(map.findWayWithExistingRoads(point1, point2));
        assertNotNull(map.findWayWithExistingRoads(point2, point1));

        assertTrue(map.arePointsConnectedByRoads(point1, point2));
        assertTrue(map.arePointsConnectedByRoads(point2, point1));
    }

    @Test
    public void testCreateHouseNextToExistingFlag() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(5, 5);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Verify that it's possible place a house up-next from an existing flag */
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1.upLeft());

        assertEquals(woodcutter.getFlag(), flag0);
        assertNotNull(map.getRoad(woodcutter.getPosition(), point1));
    }

    @Test
    public void testPlaceFlagOutsideBorder() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to place a flag outside the player's border */
        Point point1 = new Point(71, 71);

        try {
            map.placeFlag(player0, point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlaceBuildingOutsideBorderHasNoEffect() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that placing a building outside of the player's border throws an exception and doesn't place the building */
        try {
            Point point1 = new Point(71, 71);
            map.placeBuilding(new Woodcutter(player0), point1);

            fail();
        } catch (Exception e) {}

        assertEquals(map.getBuildings().size(), 1);
        assertEquals(map.getFlags().size(), 1);
        assertEquals(map.getRoads().size(), 1);
    }

    @Test
    public void testPlaceRoadThatGoesOutsideTheBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(48, 58);
        map.placeFlag(player0, point1);

        Point point9 = new Point(52, 58);
        map.placeFlag(player0, point9);

        /* Verify that it's not possible to create a road that goes outside the border */

        try {
            map.placeRoad(player0, point1, point1.upRight(), point1.upRight().upRight(), point9.upLeft(), point9);
            fail();
        } catch (Exception e) {}

        assertEquals(map.getRoads().size(), 1);
    }

    @Test
    public void testPlaceRoadThatTouchesBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(48, 58);
        map.placeFlag(player0, point1);

        Point point9 = new Point(52, 58);
        map.placeFlag(player0, point9);

        /* Verify that it's not possible to create a road that touches the border */

        try {
            map.placeRoad(player0, point1, point1.upRight(), point1.upRight(), point9.upLeft(), point9);

            fail();
        } catch (Exception e) {}

        assertEquals(map.getRoads().size(), 1);
    }

    @Test
    public void testRemoveFlag() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(58, 50);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Verify that the flag can be removed */
        map.removeFlag(flag0);

        assertFalse(map.getFlags().contains(flag0));
        assertFalse(map.isFlagAtPoint(point1));
    }

    @Test
    public void testRemovingFlagRemovesConnectedRoad() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(50, 50);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point2 = new Point(57, 49);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Connect the flag with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter.getFlag());

        /* Verify that removing the flag also removes the road */
        assertTrue(map.getRoads().contains(road0));
        assertNotNull(map.getRoad(point2, headquarter.getFlag().getPosition()));

        map.removeFlag(flag0);

        assertFalse(map.getFlags().contains(flag0));
        assertFalse(map.getRoads().contains(road0));
        assertNull(map.getRoad(point2, headquarter.getFlag().getPosition()));
    }

    @Test
    public void testDestroyBuildingByRemovingFlag() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(58, 50);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the woodcutter */
        Utils.constructHouse(woodcutter);

        /* Verify that removing the flag tears down the woodcutter */
        map.removeFlag(woodcutter.getFlag());

        assertEquals(map.getFlags().size(), 1);
        assertEquals(map.getRoads().size(), 1);
        assertEquals(map.getBuildings().size(), 2);
        assertTrue(woodcutter.isBurningDown());
    }

    @Test
    public void testRemoveBuildingByRemovingFlag() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(58, 50);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify that removing the flag tears down the woodcutter */
        map.removeFlag(woodcutter.getFlag());

        assertEquals(map.getFlags().size(), 1);
        assertEquals(map.getRoads().size(), 1);
        assertFalse(map.isBuildingAtPoint(point1));
        assertNull(map.getBuildingAtPoint(point1));
        assertFalse(map.getBuildings().contains(woodcutter));
    }

    @Test
    public void testRemovingRemoteBarracksSplitsBorder() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(48, 58);
        Utils.placeAndOccupyBarracks(player0, point1);

        /* Place barracks */
        Point point2 = new Point(47, 65);
        Building barracks1 = map.placeBuilding(new Barracks(player0), point2);

        /* Construct and occupy the barracks */
        Utils.constructHouse(barracks1);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Place barracks */
        Point point5 = new Point(48, 72);
        Building barracks2 = map.placeBuilding(new Barracks(player0), point5);

        /* Construct and occupy the barracks */
        Utils.constructHouse(barracks2);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks2);

        /* Place barracks */
        Point point6 = new Point(47, 79);
        Utils.placeAndOccupyBarracks(player0, point6);

        /* Verify that removing the barracks in the middle creates two separate borders */
        barracks1.tearDown();
        barracks2.tearDown();

        Point point7 = new Point(49, 41);
        Point point8 = new Point(48, 66);
        Point point9 = new Point(49, 71);
        Point point10 = new Point(49, 87);

        assertTrue(player0.getBorderPoints().contains(point7));
        assertTrue(player0.getBorderPoints().contains(point8));
        assertTrue(player0.getBorderPoints().contains(point9));
        assertTrue(player0.getBorderPoints().contains(point10));
    }

    @Test
    public void testShrinkingBorderDestroysHouseNowOutsideOfBorder() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        Collection<Point> border = player0.getBorderPoints();

        Point point3 = new Point(49, 59);
        Point point4 = new Point(49, 63);

        assertTrue(border.contains(point3));
        assertFalse(border.contains(point4));

        /* Place barracks */
        Point point1 = new Point(50, 58);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Finish construction and populate the barracks */
        Utils.constructHouse(barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Place woodcutter */
        Point point2 = new Point(50, 62);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point2);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter);

        /* Place a second soldier in the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Verify that the woodcutter burns down when the barracks is torn down */
        assertTrue(map.getBuildings().contains(woodcutter));
        assertTrue(woodcutter.isReady());

        barracks0.tearDown();

        assertTrue(map.getBuildings().contains(woodcutter));
        assertTrue(woodcutter.isBurningDown());
    }

    @Test
    public void testShrinkingBorderDestroysFlagNowOutsideOfBorder() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        Collection<Point> border = player0.getBorderPoints();

        Point point3 = new Point(49, 59);
        Point point4 = new Point(49, 63);

        assertTrue(border.contains(point3));
        assertFalse(border.contains(point4));

        /* Place barracks */
        Point point1 = new Point(48, 58);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Place flag */
        Point point2 = new Point(50, 62);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Verify that the flag is removed when the barracks is torn down */
        assertTrue(map.getFlags().contains(flag0));

        barracks0.tearDown();

        assertFalse(map.getFlags().contains(flag0));
    }

    @Test
    public void testShrinkingBorderDestroysRoadNowOutsideOfBorder() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        Collection<Point> border = player0.getBorderPoints();

        Point point4 = new Point(49, 59);
        Point point5 = new Point(49, 63);

        assertTrue(border.contains(point4));
        assertFalse(border.contains(point5));

        /* Place barracks */
        Point point1 = new Point(49, 57);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Finish construction and populate the barracks */
        Utils.constructHouse(barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Place flag */
        Point point2 = new Point(48, 64);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place flag */
        Point point3 = new Point(51, 63);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Place road */
        Road road0 = map.placeRoad(player0, point2, point2.downRight(), point3);

        /* Verify that the road id removed when the barracks is destroyed */
        assertTrue(map.getRoads().contains(road0));

        barracks0.tearDown();

        assertFalse(map.getRoads().contains(road0));
    }

    @Test
    public void testBorderCanBeConcave() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point3 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point3);

        Utils.constructHouse(barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Place barracks */
        Point point4 = new Point(18, 18);
        Building barracks1 = map.placeBuilding(new Barracks(player0), point4);

        Utils.constructHouse(barracks1);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Place barracks */
        Point point39 = new Point(18, 24);
        Building barracks2 = map.placeBuilding(new Barracks(player0), point39);

        Utils.constructHouse(barracks2);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks2);

        /* Place barracks */
        Point point45 = new Point(22, 28);
        Building barracks3 = map.placeBuilding(new Barracks(player0), point45);

        /* Finish construction and occupy the barracks */
        Utils.constructHouse(barracks3);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks3);

        /* Verify that the border is concave */
        Collection<Point> border = player0.getBorderPoints();

        Point point5 = new Point(22, 36);
        Point point6 = new Point(5, 31);
        Point point46 = new Point(11, 33);

        assertTrue(border.contains(point5));
        assertTrue(border.contains(point6));
        assertTrue(border.contains(point46));
    }

    @Test
    public void testFieldOfViewIsOutsideBorder() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(1, 13);
        assertTrue(player0.getBorderPoints().contains(point1));

        Point point2 = new Point(2, 18);
        assertTrue(player0.getDiscoveredLand().contains(point2));
    }

    @Test
    public void testFieldOfViewCannotShrink() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point3 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point3);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Verify that the field of view does not shrink when the barracks is destroyed */
        Collection<Point> discoveredLandBefore = player0.getDiscoveredLand();

        barracks0.tearDown();

        assertEquals(player0.getDiscoveredLand().size(), discoveredLandBefore.size());
    }

    @Test
    public void testFieldOfViewGrowsWhenBorderGrows() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Get the field of view before construction of barracks */
        Point point1 = new Point(4, 28);
        Point point3 = new Point(5, 35);

        assertTrue(player0.getDiscoveredLand().contains(point1));
        assertFalse(player0.getDiscoveredLand().contains(point3));

        /* Place barracks */
        Point point2 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Finish construction of barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Verify that the field of view has grown */
        assertTrue(player0.getDiscoveredLand().contains(point3));
    }

    @Test
    public void testGetWidthAndHeight() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 30);

        /* Verify that the width and height are correct */
        assertEquals(map.getWidth(), 20);
        assertEquals(map.getHeight(), 30);
    }

    @Test
    public void testPointWithinBorderAreDiscovered() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that a point within the border is discovered */
        Point point1 = new Point(10, 12);

        assertTrue(player0.isWithinBorder(point1));
        assertTrue(player0.getDiscoveredLand().contains(point1));
    }

    @Test
    public void testRemotePointOutsideBorderIsNotDiscovered() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that a point far outside the border is not yet discovered */
        Point point1 = new Point(39, 39);

        assertFalse(player0.isWithinBorder(point1));
        assertFalse(player0.getDiscoveredLand().contains(point1));
    }

    @Test
    public void testCircleOfBarracksCreatesInternalBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        /* Creating new game map with size 100x100 */
        GameMap map = new GameMap(players, 100, 100);

        /* Placing headquarter */
        Point point38 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Placing barracks */
        Point point39 = new Point(4, 22);
        Utils.placeAndOccupyBarracks(player0, point39);

        /* Placing barracks */
        Point point50 = new Point(5, 29);
        Utils.placeAndOccupyBarracks(player0, point50);

        /* Placing barracks */
        Point point58 = new Point(4, 36);
        Utils.placeAndOccupyBarracks(player0, point58);

        /* Placing barracks */
        Point point65 = new Point(14, 36);
        Utils.placeAndOccupyBarracks(player0, point65);

        /* Placing barracks */
        Point point70 = new Point(24, 36);
        Utils.placeAndOccupyBarracks(player0, point70);

        /* Placing barracks */
        Point point74 = new Point(34, 36);
        Utils.placeAndOccupyBarracks(player0, point74);

        /* Placing barracks */
        Point point78 = new Point(44, 36);
        Utils.placeAndOccupyBarracks(player0, point78);

        /* Placing barracks */
        Point point85 = new Point(42, 30);
        Utils.placeAndOccupyBarracks(player0, point85);

        /* Placing barracks */
        Point point87 = new Point(42, 24);
        Utils.placeAndOccupyBarracks(player0, point87);

        /* Placing barracks */
        Point point88 = new Point(44, 18);
        Utils.placeAndOccupyBarracks(player0, point88);

        /* Placing barracks */
        Point point89 = new Point(44, 12);
        Utils.placeAndOccupyBarracks(player0, point89);

        /* Placing barracks */
        Point point90 = new Point(44, 6);
        Utils.placeAndOccupyBarracks(player0, point90);

        /* Placing barracks */
        Point point91 = new Point(37, 3);
        Utils.placeAndOccupyBarracks(player0, point91);

        /* Placing barracks */
        Point point92 = new Point(29, 3);
        Utils.placeAndOccupyBarracks(player0, point92);

        /* Placing barracks */
        Point point93 = new Point(21, 3);
        Utils.placeAndOccupyBarracks(player0, point93);

        /* Placing barracks */
        Point point94 = new Point(13, 3);
        Utils.placeAndOccupyBarracks(player0, point94);

        /* Placing barracks */
        Point point95 = new Point(5, 3);
        Utils.placeAndOccupyBarracks(player0, point95);

        /* Verify that there is an internal border created for the space that the circle of barracks doesn't cover */
        Point point86 = new Point(24, 44);
        Point point96 = new Point(24, 28);
        Point point97 = new Point(26, 24);
        Point point98 = new Point(25, 1);
        assertTrue(player0.getBorderPoints().contains(point86));
        assertTrue(player0.getBorderPoints().contains(point96));
        assertTrue(player0.getBorderPoints().contains(point97));
        assertTrue(player0.getBorderPoints().contains(point98));
    }

    @Test
    public void testCannotGetNonExistingFlagAtPoint() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        GameMap map = new GameMap(players, 100, 100);

        /* Placing headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Verify that it's not possible to get a non-existing flag */
        Point point1 = new Point(20, 20);
        assertNull(map.getFlagAtPoint(point1));
    }

    @Test
    public void testGetRoadAtPoint() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        GameMap map = new GameMap(players, 100, 100);

        /* Placing headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Verify that it's possible to get the road */
        Point point2 = new Point(8, 4);
        Road road1 = map.getRoadAtPoint(point2);

        assertEquals(road0, road1);
    }

    @Test
    public void testIsRoadAtPoint() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        GameMap map = new GameMap(players, 100, 100);

        /* Placing headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Verify that it's possible to get the road */
        Point point2 = new Point(8, 4);
        assertTrue(map.isRoadAtPoint(point2));
    }

    @Test
    public void testIsNotRoadAtPoint() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        GameMap map = new GameMap(players, 100, 100);

        /* Placing headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Verify that there is no road at the point */
        Point point1 = new Point(10, 12);
        assertFalse(map.isRoadAtPoint(point1));
    }

    @Test
    public void testPlaceTree() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        GameMap map = new GameMap(players, 100, 100);

        /* Placing headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Verify that it's possible to place a tree */
        Point point1 = new Point(15, 15);
        Tree tree0 = map.placeTree(point1);
    }

    @Test
    public void testIsTree() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        GameMap map = new GameMap(players, 100, 100);

        /* Placing headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place tree */
        Point point1 = new Point(15, 15);
        Tree tree0 = map.placeTree(point1);

        /* Verify that the tree is there */
        assertTrue(map.isTreeAtPoint(point1));
    }

    @Test
    public void testIsNoTree() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        GameMap map = new GameMap(players, 100, 100);

        /* Placing headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place tree */
        Point point1 = new Point(15, 15);
        Tree tree0 = map.placeTree(point1);

        /* Verify that there is no tree on another spot */
        Point point2 = new Point(20, 16);
        assertFalse(map.isTreeAtPoint(point2));
    }

    @Test
    public void testGetTrees() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        GameMap map = new GameMap(players, 100, 100);

        /* Placing headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place trees */
        Point point1 = new Point(15, 15);
        Tree tree0 = map.placeTree(point1);

        Point point2 = new Point(15, 17);
        Tree tree1 = map.placeTree(point2);

        /* Verify that there are exactly these trees on the map */
        Collection<Tree> trees = map.getTrees();

        assertEquals(trees.size(), 2);
        assertTrue(trees.contains(tree0));
        assertTrue(trees.contains(tree1));
    }
}
