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
import org.junit.Before;
import org.junit.Test;

import java.awt.geom.Path2D;
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

/**
 *
 * @author johan
 */
public class TestGameMap {
    private GameMap map;
    private final Player player0 = new Player("Player 0", java.awt.Color.BLUE);
    private final List<Player> players = new ArrayList<>();

    @Before
    public void setup() throws Exception {

        players.add(player0);
        map = new GameMap(players, 50, 50);
    }

    @Test
    public void testSetPlayers() {

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
    public void testSetStartingPoints() {

        /* Verify that starting points can be set */
        List<Point> points = new ArrayList<>();

        points.add(new Point(3, 3));
        points.add(new Point(7, 7));

        assertEquals(map.getStartingPoints().size(), 0);

        map.setStartingPoints(points);

        assertEquals(map.getStartingPoints().size(), 2);
    }

    @Test
    public void testMapIsCorrectAfterSetPlayers() {

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
        Point hqPoint = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), hqPoint);

        assertTrue(headquarter.getPosition().equals(hqPoint));
    }

    @Test(expected=Exception.class)
    public void testPlaceSameBuildingTwice() throws Exception {
        Point hqPoint = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), hqPoint);

        Woodcutter woodcutter    = new Woodcutter(player0);
        Point wcPoint    = new Point(2, 2);
        Point otherPoint = new Point(2, 8);

        map.placeBuilding(woodcutter, wcPoint);
        map.placeBuilding(woodcutter, otherPoint);
    }

    @Test(expected=Exception.class)
    public void testPlaceTwoBuildingsOnSameSpot() throws Exception {
        Point hqPoint = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), hqPoint);

        Woodcutter woodcutter  = new Woodcutter(player0);
        Quarry     quarry0 = new Quarry(player0);
        Point wcPoint  = new Point(1, 1);

        map.placeBuilding(woodcutter, wcPoint);
        map.placeBuilding(quarry0, wcPoint);
    }

    @Test(expected=Exception.class)
    public void testPlaceFlagsOnSamePlace() throws Exception {
        Point hqPoint = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), hqPoint);

        Point point1 = new Point(1, 1);
        Point point2 = new Point(1, 1);

        map.placeFlag(player0, point1);
        map.placeFlag(player0, point2);
    }

    @Test(expected=Exception.class)
    public void testPlaceSameFlagTwice() throws Exception {
        Point hqPoint = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), hqPoint);

        Point point1 = new Point(1, 1);

        map.placeFlag(player0, point1);

        point1.x = 3;

        map.placeFlag(player0, point1);
    }

    @Test(expected=Exception.class)
    public void testAddRoadWithIdenticalStartAndEnd() throws Exception {
        Point hqPoint = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), hqPoint);

        Point point1 = new Point(1, 1);
        Point point2 = new Point(1, 1);

        map.placeFlag(player0, point1);
        map.placeFlag(player0, point2);

        Road road = map.placeAutoSelectedRoad(player0, point1, point2);
    }

    @Test(expected=Exception.class)
    public void testAddRoadBetweenFlagsNotOnMap() throws Exception {
        Point hqPoint = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), hqPoint);

        Flag flag0 = new Flag(new Point(1, 1));
        Flag flag1 = new Flag(new Point(1, 2));

        Road road = map.placeAutoSelectedRoad(player0, flag0, flag1);
    }

    @Test(expected=InvalidRouteException.class)
    public void testFindWayBetweenSameFlag() throws Exception {

        /* Place headquarter */
        Point hqPoint = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place flag */
        Point point1 = new Point(3, 3);
        map.placeFlag(player0, point1);

        /* Test that it's not possible to find a way from and to same point */
        map.findWayWithExistingRoads(point1, point1);
    }

    @Test
    public void testCreateMinimalMap() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        new GameMap(players, 5, 5);
    }

    @Test(expected=Exception.class)
    public void testCreateTooSmallMap() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        new GameMap(players, 4, 4);
    }

    @Test(expected=Exception.class)
    public void testCreateMapWithNegativeHeight() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        new GameMap(players, 10, -1);
    }

    @Test(expected=Exception.class)
    public void testCreateMapWithNegativeWidth() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        new GameMap(players, -3, 10);
    }

    @Test
    public void testGetFlags() throws Exception {

        /* Place headquarter */
        Point hqPoint = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place flag */
        Point point1    = new Point(6, 6);
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

    @Test(expected=Exception.class)
    public void testPlaceBuildingOnInvalidPoint() throws Exception {
        Point hqPoint = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), hqPoint);

        Farm    farm      = new Farm(player0);
        Point   farmPoint = new Point(3, 4);

        map.placeBuilding(farm, farmPoint);
    }

    @Test(expected=Exception.class)
    public void testPlaceFlagOnInvalidPoint() throws Exception {
        Point hqPoint = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), hqPoint);

        Point point0 = new Point(4, 7);

        map.placeFlag(player0, point0);
    }

    @Test
    public void testPlaceBuildingSetsMap() throws Exception {
        Point hqPoint = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), hqPoint);

        Farm    farm   = new Farm(player0);
        Point   point0 = new Point(5, 5);

        assertNull(farm.getMap());

        map.placeBuilding(farm, point0);

        assertEquals(farm.getMap(), map);
    }

    @Test
    public void testFindWayBetweenHouseAndItsFlag() throws Exception {
        Point hqPoint = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), hqPoint);

        Woodcutter woodcutter     = new Woodcutter(player0);
        Point      point0 = new Point(5, 5);
        Point      point1 = new Point(6, 4);

        map.placeBuilding(woodcutter, point0);

        assertNotNull(map.getRoad(point0, point1));

        assertNotNull(map.findWayOffroad(point0, point1, null));
        assertNotNull(map.findWayOffroad(point1, point0, null));

        assertNotNull(map.findWayWithExistingRoads(point0, point1));
        assertNotNull(map.findWayWithExistingRoads(point1, point0));

        assertTrue(map.arePointsConnectedByRoads(point0, point1));
        assertTrue(map.arePointsConnectedByRoads(point1, point0));
    }

    @Test
    public void testCreateHouseNextToExistingFlag() throws Exception {
        Point hqPoint = new Point(8, 8);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), hqPoint);

        Point      point0 = new Point(5, 5);
        Flag       flag0  = map.placeFlag(player0, point0);

        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point0.upLeft());

        assertEquals(woodcutter.getFlag(), flag0);
        assertNotNull(map.getRoad(woodcutter.getPosition(), point0));
    }

    @Test
    public void testGetPerimeterWithOnlyHeadquarter() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        assertEquals(player0.getBorders().size(), 1);

        Collection<Point> border = player0.getBorders().get(0);
        assertTrue(border.contains(new Point(50, 30)));
        assertTrue(border.contains(new Point(50, 70)));

        assertTrue(border.contains(new Point(30, 50)));
        assertTrue(border.contains(new Point(70, 50)));

        assertFalse(border.contains(new Point(28, 50)));
        assertFalse(border.contains(new Point(32, 50)));

        assertFalse(border.contains(new Point(68, 50)));
        assertFalse(border.contains(new Point(72, 50)));

        assertFalse(border.contains(new Point(71, 51)));
        assertFalse(border.contains(new Point(71, 49)));
    }

    @Test(expected = Exception.class)
    public void testPlaceFlagOutsideBorder() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);
        Point point1 = new Point(71, 71);
        map.placeFlag(player0, point1);
    }

    @Test(expected = Exception.class)
    public void testPlaceBuildingOutsideBorder() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);
        Point point1 = new Point(71, 71);
        map.placeBuilding(new Woodcutter(player0), point1);
    }

    @Test
    public void testPlaceBuildingOutsideBorderHasNoEffect() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);
        Point point1 = new Point(71, 71);

        try {
            map.placeBuilding(new Woodcutter(player0), point1);
        } catch (Exception e) {}

        assertEquals(map.getBuildings().size(), 1);
        assertEquals(map.getFlags().size(), 1);
        assertEquals(map.getRoads().size(), 1);
    }

    @Test
    public void testPlaceRoadThatGoesOutsideTheBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(49, 67);
        map.placeFlag(player0, point1);

        Point point9 = new Point(51, 65);
        map.placeFlag(player0, point9);

        /* Verify that it's not possible to create a road that goes outside the border */
        Point point2 = new Point(48, 68);
        Point point3 = new Point(49, 69);
        Point point4 = new Point(48, 70);
        Point point5 = new Point(49, 71);
        Point point6 = new Point(50, 70);
        Point point7 = new Point(51, 69);
        Point point8 = new Point(50, 68);
        Point point10 = new Point(51, 67);

        try {
            map.placeRoad(player0, point1, point2, point3, point4, point5, point6, point7, point8, point10, point9);
            assertFalse(true);
        } catch (Exception e) {}

        assertEquals(map.getRoads().size(), 1);
    }

    @Test
    public void testPlaceRoadThatTouchesBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(49, 67);
        map.placeFlag(player0, point1);

        Point point9 = new Point(51, 65);
        map.placeFlag(player0, point9);

        /* Verify that it's not possible to create a road that touches the border */
        Point point2 = new Point(48, 68);
        Point point3 = new Point(49, 69);
        Point point6 = new Point(50, 70);
        Point point7 = new Point(51, 69);
        Point point8 = new Point(50, 68);
        Point point10 = new Point(51, 67);

        try {
            map.placeRoad(player0, point1, point2, point3, point6, point7, point8, point10, point9);            assertFalse(true);
        } catch (Exception e) {}

        assertEquals(map.getRoads().size(), 1);
    }

    @Test
    public void testRemoveFlag() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(50, 68);
        Flag flag0 = map.placeFlag(player0, point1);

        map.removeFlag(flag0);

        assertFalse(map.getFlags().contains(flag0));
        assertFalse(map.isFlagAtPoint(point1));
    }

    @Test
    public void testRemovingFlagRemovesConnectedRoad() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);
        Point point0 = new Point(50, 50);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        Point point2 = new Point(57, 49);
        Flag flag0 = map.placeFlag(player0, point2);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter.getFlag());

        map.removeFlag(flag0);

        assertFalse(map.getFlags().contains(flag0));
        assertFalse(map.getRoads().contains(road0));
        assertNull(map.getRoad(point0, point2));
    }

    @Test
    public void testDestroyBuildingByRemovingFlag() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(50, 68);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        map.removeFlag(woodcutter.getFlag());

        assertEquals(map.getFlags().size(), 1);
        assertEquals(map.getRoads().size(), 1);
        assertEquals(map.getBuildings().size(), 2);
        assertTrue(woodcutter.burningDown());
    }

    @Test
    public void testRemovingRemoteBarracksSplitsBorder() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        assertEquals(player0.getBorders().size(), 1);
        Collection<Point> border = player0.getBorders().get(0);

        assertTrue(border.contains(new Point(50, 70)));
        assertFalse(border.contains(new Point(50, 74)));

        Point point1 = new Point(50, 68);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point1);

        Utils.constructHouse(barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);

        assertEquals(player0.getBorders().size(), 1);
        border = player0.getBorders().get(0);

        assertFalse(border.contains(new Point(50, 70)));
        assertTrue(border.contains(new Point(50, 76)));

        Point point2 = new Point(50, 72);
        Building barracks1 = map.placeBuilding(new Barracks(player0), point2);

        Utils.constructHouse(barracks1, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1, map);

        assertEquals(player0.getBorders().size(), 1);
        border = player0.getBorders().get(0);

        assertFalse(border.contains(new Point(50, 74)));
        assertTrue(border.contains(new Point(50, 80)));

        Point point3 = new Point(50, 78);
        Building barracks2 = map.placeBuilding(new Barracks(player0), point3);

        Utils.constructHouse(barracks2, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks2, map);

        assertEquals(player0.getBorders().size(), 1);
        border = player0.getBorders().get(0);

        assertFalse(border.contains(new Point(50, 80)));
        assertTrue(border.contains(new Point(50, 86)));

        Point point4 = new Point(50, 84);
        Building barracks3 = map.placeBuilding(new Barracks(player0), point4);

        Utils.constructHouse(barracks3, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks3, map);

        assertEquals(player0.getBorders().size(), 1);
        border = player0.getBorders().get(0);

        assertFalse(border.contains(new Point(50, 86)));
        assertTrue(border.contains(new Point(50, 92)));

        barracks0.tearDown();
        barracks1.tearDown();
        barracks2.tearDown();

        assertEquals(player0.getBorders().size(), 2);
    }

    @Test
    public void testShrinkingBorderDestroysHouseNowOutsideOfBorder() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        assertEquals(player0.getBorders().size(), 1);
        Collection<Point> border = player0.getBorders().get(0);

        assertTrue(border.contains(new Point(50, 70)));
        assertFalse(border.contains(new Point(50, 74)));

        Point point1 = new Point(50, 68);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point1);

        Utils.constructHouse(barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);

        Point point2 = new Point(50, 72);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point2);

        Utils.constructHouse(woodcutter, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);

        assertTrue(map.getBuildings().contains(woodcutter));
        assertTrue(woodcutter.ready());

        barracks0.tearDown();

        assertTrue(map.getBuildings().contains(woodcutter));
        assertTrue(woodcutter.burningDown());
    }

    @Test
    public void testShrinkingBorderDestroysFlagNowOutsideOfBorder() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        assertEquals(player0.getBorders().size(), 1);
        Collection<Point> border = player0.getBorders().get(0);

        assertTrue(border.contains(new Point(50, 70)));
        assertFalse(border.contains(new Point(50, 74)));

        Point point1 = new Point(50, 68);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point1);

        Utils.constructHouse(barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);

        Point point2 = new Point(50, 72);
        Flag flag0 = map.placeFlag(player0, point2);

        assertTrue(map.getFlags().contains(flag0));

        barracks0.tearDown();

        assertFalse(map.getFlags().contains(flag0));
    }

    @Test
    public void testShrinkingBorderDestroysRoadNowOutsideOfBorder() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        assertEquals(player0.getBorders().size(), 1);
        Collection<Point> border = player0.getBorders().get(0);

        assertTrue(border.contains(new Point(50, 70)));
        assertFalse(border.contains(new Point(50, 74)));

        Point point1 = new Point(50, 68);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point1);

        Utils.constructHouse(barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);

        Point point2 = new Point(50, 72);
        Flag flag0 = map.placeFlag(player0, point2);

        Point point3 = new Point(48, 70);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point2, point2.downLeft(), point3);

        assertTrue(map.getRoads().contains(road0));

        barracks0.tearDown();

        assertFalse(map.getRoads().contains(road0));
    }

    @Test
    public void testBorderCanBeConcave() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point3 = new Point(5, 23);
        Building building1 = map.placeBuilding(new Barracks(player0), point3);

        Utils.constructHouse(building1, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, building1, map);

        /* Place barracks */
        Point point4 = new Point(18, 18);
        Building building2 = map.placeBuilding(new Barracks(player0), point4);

        Utils.constructHouse(building2, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, building2, map);

        /* Place barracks */
        Point point39 = new Point(18, 24);
        Building building3 = map.placeBuilding(new Barracks(player0), point39);

        Utils.constructHouse(building3, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, building3, map);

        /* Place barracks */
        Point point45 = new Point(22, 28);
        Building building4 = map.placeBuilding(new Barracks(player0), point45);

        Utils.constructHouse(building4, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, building4, map);

        /* Verify that the owned land is not split in different parts */
        assertEquals(player0.getBorders().size(), 1);

        /* Verify that the border is concave */
        Collection<Point> border = player0.getBorders().get(0);

        assertTrue(border.contains(new Point(22, 36)));
        assertTrue(border.contains(new Point(5, 31)));

        Point point46 = new Point(11, 27);
        assertTrue(border.contains(point46));
    }

    @Test
    public void testFieldOfViewIsOutsideBorder() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(1, 21);
        assertTrue(player0.getBorders().get(0).contains(point1));

        Point point2 = new Point(1, 25);
        assertTrue(player0.getFieldOfView().contains(point2));
    }

    @Test
    public void testFieldOfViewContainsAllOwnedLand() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        Collection<Point> border = player0.getBorders().get(0);
        List<Point> fieldOfView = player0.getFieldOfView();

        Path2D.Double path = new Path2D.Double();
        path.moveTo(fieldOfView.get(fieldOfView.size() - 1).x, fieldOfView.get(fieldOfView.size() - 1).y);

        for (Point point : fieldOfView) {
            path.lineTo(point.x, point.y);
        }

        for (Point point : border) {
            assertTrue(path.contains(point));
        }
    }

    @Test
    public void testFieldOfViewCannotShrink() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point3 = new Point(5, 23);
        Building building1 = map.placeBuilding(new Barracks(player0), point3);

        /* Finish construction of the barracks */
        Utils.constructHouse(building1, map);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, building1, map);

        /* Verify that the field of view does not shrink when the barracks is destroyed */
        Collection<Point> fieldOfViewBefore = player0.getFieldOfView();

        building1.tearDown();

        assertEquals(fieldOfViewBefore, player0.getFieldOfView());
    }

    @Test
    public void testFieldOfViewGrowsWhenBorderGrows() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Get the field of view before construction of barracks */
        Collection<Point> oldFieldOfView = player0.getFieldOfView();

        Point point1 = new Point(5, 27);
        Point point3 = new Point(4, 32);

        assertTrue(oldFieldOfView.contains(point1));
        assertFalse(oldFieldOfView.contains(point3));

        /* Place barracks */
        Point point2 = new Point(5, 23);
        Building building1 = map.placeBuilding(new Barracks(player0), point2);

        /* Finish construction of barracks */
        Utils.constructHouse(building1, map);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, building1, map);

        /* Verify that the field of view has grown */
        Collection<Point> newFieldOfView = player0.getFieldOfView();

        assertTrue(newFieldOfView.contains(point3));
        assertFalse(newFieldOfView.contains(point1));
    }

    @Test
    public void testBarracksCanOnlyBeBuiltCloseToBorder() {
        // TODO: Implement test
    }

    @Test
    public void testGetWidthAndHeight() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        /* Creating new game map with size 100x100 */
        GameMap map = new GameMap(players, 100, 100);

        /* Placing headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Placing barracks */
        Point point39 = new Point(4, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point39);

        /* Finish construction of barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);

        /* Placing barracks */
        Point point50 = new Point(4, 28);
        Building barracks1 = map.placeBuilding(new Barracks(player0), point50);

        /* Finish construction of barracks */
        Utils.constructHouse(barracks1, map);

        /* Occupy barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1, map);

        /* Placing barracks */
        Point point58 = new Point(4, 34);
        Building barracks2 = map.placeBuilding(new Barracks(player0), point58);

        /* Finish construction of barracks */
        Utils.constructHouse(barracks2, map);

        /* Occupy barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks2, map);

        /* Placing barracks */
        Point point65 = new Point(7, 37);
        Building barracks3 = map.placeBuilding(new Barracks(player0), point65);

        /* Finish construction of barracks */
        Utils.constructHouse(barracks3, map);

        /* Occupy barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks3, map);

        /* Placing barracks */
        Point point70 = new Point(11, 37);
        Building barracks4 = map.placeBuilding(new Barracks(player0), point70);

        /* Finish construction of barracks */
        Utils.constructHouse(barracks4, map);

        /* Occupy barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks4, map);

        /* Placing barracks */
        Point point74 = new Point(16, 38);
        Building barracks5 = map.placeBuilding(new Barracks(player0), point74);

        /* Finish construction of barracks */
        Utils.constructHouse(barracks5, map);

        /* Occupy barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks5, map);

        /* Placing barracks */
        Point point78 = new Point(19, 35);
        Building barracks6 = map.placeBuilding(new Barracks(player0), point78);

        /* Finish construction of barracks */
        Utils.constructHouse(barracks6, map);

        /* Occupy barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks6, map);

        /* Placing barracks */
        Point point85 = new Point(22, 32);
        Building barracks7 = map.placeBuilding(new Barracks(player0), point85);

        /* Finish construction of barracks */
        Utils.constructHouse(barracks7, map);

        /* Occupy barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks7, map);

        /* Placing barracks */
        Point point87 = new Point(22, 28);
        Building barracks8 = map.placeBuilding(new Barracks(player0), point87);

        /* Finish construction of barracks */
        Utils.constructHouse(barracks8, map);

        /* Occupy barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks8, map);

        /* Placing barracks */
        Point point88 = new Point(21, 23);
        Building barracks9 = map.placeBuilding(new Barracks(player0), point88);

        /* Finish construction of barracks */
        Utils.constructHouse(barracks9, map);

        /* Occupy barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks9, map);

        /* Verify that there is an internal border created for the space that
           the circle of barracks doesn't cover */
        Point point86 = new Point(14, 24);
        assertEquals(player0.getBorders().size(), 2);
        assertTrue(player0.getBorders().get(0).contains(point86) ||
                   player0.getBorders().get(1).contains(point86));
    }

    @Test (expected = Exception.class)
    public void testCannotGetNonExistingFlagAtPoint() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Flag flag0 = map.getFlagAtPoint(point1);
    }

    @Test
    public void testGetRoadAtPoint() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
