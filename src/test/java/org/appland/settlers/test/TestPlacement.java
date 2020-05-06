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
import org.appland.settlers.model.GoldMine;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Tile;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.MEDIUM;
import static org.appland.settlers.model.Size.SMALL;
import static org.appland.settlers.model.Tile.Vegetation.DESERT;
import static org.appland.settlers.model.Tile.Vegetation.GRASS;
import static org.appland.settlers.model.Tile.Vegetation.MOUNTAIN;
import static org.appland.settlers.model.Tile.Vegetation.WATER;
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
public class TestPlacement {

    @Test
    public void testDefaultMapIsEmpty() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);

        /* Verify that the map starts out empty */
        assertTrue(map.getBuildings().isEmpty());
        assertTrue(map.getRoads().isEmpty());
        assertTrue(map.getStones().isEmpty());
        assertTrue(map.getTrees().isEmpty());
        assertTrue(map.getFlags().isEmpty());

        // TODO: verify all placeable objects in map
    }

    @Test
    public void testEmptyMapHasNoBorders() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);

        /* Verify that the player has no borders yet */
        assertEquals(player0.getBorderPoints().size(), 0);
    }

    @Test
    public void testAvailableFlagPointsContainsValidFlagPoint() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that there is a valid flag point in the available flag points */
        Collection<Point> flagPoints = map.getAvailableFlagPoints(player0);

        assertTrue(flagPoints.contains(new Point(8, 6)));
    }

    @Test
    public void testFlagsCannotBePlacedEdgeOfGameMap() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        Collection<Point> flagPoints = map.getAvailableFlagPoints(player0);

        /* Test that flags can't be placed on the borders */
        for (int y = 0; y < 11; y += 2) {
            assertFalse(flagPoints.contains(new Point(0, y)));
            assertFalse(flagPoints.contains(new Point(10, y)));
        }

        for (int x = 0; x < 11; x += 2) {
            assertFalse(flagPoints.contains(new Point(x, 0)));
            assertFalse(flagPoints.contains(new Point(x, 10)));
        }
    }

    @Test
    public void testAvailableHousesNextToSmallHouse() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(6, 6);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify that the available houses next to the woodcutter are correct */
        Map<Point, Size> possibleHouses = map.getAvailableHousePoints(player0);

        /* The house's own point */
        assertFalse(possibleHouses.containsKey(point1));

        /* The house's flag */
        assertFalse(possibleHouses.containsKey(point1.downRight()));

        /* Points in front, sampled */
        assertEquals(possibleHouses.get(point1.right().down()), MEDIUM);
        assertEquals(possibleHouses.get(point1.right().downRight()), MEDIUM);

        /* Points on left, sampled */
        assertFalse(possibleHouses.containsKey(point1.down()));
        assertFalse(possibleHouses.containsKey(point1.downLeft()));

        /* Points on top, sampled */
        assertFalse(possibleHouses.containsKey(point1.left()));
        assertFalse(possibleHouses.containsKey(point1.upLeft()));
        assertEquals(possibleHouses.get(point1.up()), MEDIUM);

        /* Points on right, sampled */
        assertFalse(possibleHouses.containsKey(point1.upRight()));
        assertFalse(possibleHouses.containsKey(point1.left()));
    }

    @Test
    public void testCannotPlaceHouseTwice() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(6, 6);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify that it's not possible to place the woodcutter again */
        Point point2 = new Point(12, 10);
        try {
            map.placeBuilding(woodcutter, point2);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testNoAvailableFlagOnLake() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Create mini-lake */
        Point waterPoint   = new Point(2, 2);
        Utils.surroundPointWithWater(waterPoint, map);

        /* Verify that there is no available spot for a flag on the lake */
        Collection<Point> possibleFlags = map.getAvailableFlagPoints(player0);

        assertFalse(possibleFlags.contains(waterPoint));
    }

    @Test
    public void testNoAvailableBuildingSpotOnLake() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point waterPoint   = new Point(2, 2);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Create mini-lake */
        Utils.surroundPointWithWater(waterPoint, map);

        /* Verify that there is no available spot for a building on the lake */
        Map<Point, Size> possibleBuildings = map.getAvailableHousePoints(player0);

        assertFalse(possibleBuildings.containsKey(waterPoint));
    }

    @Test
    public void testNoAvailableFlagOnStone() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a stone */
        Point point1 = new Point(2, 2);
        map.placeStone(point1);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that there is no available spot for a flag on the stone */
        Collection<Point> possibleFlags = map.getAvailableFlagPoints(player0);

        assertFalse(possibleFlags.contains(point1));
    }

    @Test
    public void testNoAvailableBuildingSpotOnStone() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a stone */
        Point point1 = new Point(2, 2);
        map.placeStone(point1);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that there is no available spot for a building on the stone */
        Map<Point, Size> possibleBuildings = map.getAvailableHousePoints(player0);

        assertFalse(possibleBuildings.containsKey(point1));
    }

    @Test
    public void testNoAvailableFlagOnTree() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Plant a tree */
        Point point1 = new Point(2, 2);
        map.placeTree(point1);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that there is no available spot for a flag on the tree */
        Collection<Point> possibleFlags = map.getAvailableFlagPoints(player0);

        assertFalse(possibleFlags.contains(point1));
    }

    @Test
    public void testNoAvailableBuildingSpotOnTree() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Plant a tree */
        Point point1 = new Point(2, 2);
        map.placeTree(point1);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that there is no available spot for a building on the tree */
        Map<Point, Size> possibleBuildings = map.getAvailableHousePoints(player0);

        assertFalse(possibleBuildings.containsKey(point1));
    }

    @Test
    public void testNoAvailableFlagOnRoad() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a road */
        Point point1 = new Point(5, 5);
        Point point2 = new Point(7, 5);
        Point point3 = new Point(9, 5);

        map.placeFlag(player0, point1);
        map.placeFlag(player0, point3);

        map.placeRoad(player0, point1, point2, point3);

        /* Verify that there is no available spot for a flag on the tree */
        Collection<Point> possibleFlags = map.getAvailableFlagPoints(player0);

        assertFalse(possibleFlags.contains(point2));
    }

    @Test
    public void testNoAvailableBuildingSpotOnRoad() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a road */
        Point point1 = new Point(5, 5);
        Point point2 = new Point(7, 5);
        Point point3 = new Point(9, 5);

        map.placeFlag(player0, point1);
        map.placeFlag(player0, point3);

        map.placeRoad(player0, point1, point2, point3);

        /* Verify that there is no available spot for a building on the road */
        Map<Point, Size> possibleBuildings = map.getAvailableHousePoints(player0);

        assertFalse(possibleBuildings.containsKey(point2));
    }

    @Test
    public void testAvailableFlagsNextToFlag() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place flag */
        Point point0 = new Point(7, 7);
        Flag flag0 = map.placeFlag(player0, point0);

        /* Verify that the available flag points next to the flag are correct */
        Collection<Point> possibleFlagPoints = map.getAvailableFlagPoints(player0);

        /* The flag's own point */
        assertFalse(possibleFlagPoints.contains(point0));

        /* The right and left of the flag */
        assertFalse(possibleFlagPoints.contains(point0.right()));
        assertFalse(possibleFlagPoints.contains(point0.left()));

        /* Diagonally of the flag */
        assertFalse(possibleFlagPoints.contains(point0.upRight()));
        assertFalse(possibleFlagPoints.contains(point0.downRight()));
        assertFalse(possibleFlagPoints.contains(point0.upLeft()));
        assertFalse(possibleFlagPoints.contains(point0.downLeft()));

        /* Surrounding points */
        assertTrue (possibleFlagPoints.contains(point0.up()));
        assertTrue (possibleFlagPoints.contains(point0.down()));
        assertTrue (possibleFlagPoints.contains(point0.upRight().upRight()));
        assertTrue (possibleFlagPoints.contains(point0.upRight().right()));
        assertTrue (possibleFlagPoints.contains(point0.right().right()));
        assertTrue (possibleFlagPoints.contains(point0.downRight().right()));
        assertTrue (possibleFlagPoints.contains(point0.downRight().downRight()));
        assertTrue (possibleFlagPoints.contains(point0.down()));
        assertTrue (possibleFlagPoints.contains(point0.downLeft().downLeft()));
        assertTrue (possibleFlagPoints.contains(point0.downLeft().left()));
        assertTrue (possibleFlagPoints.contains(point0.left().left()));
        assertTrue (possibleFlagPoints.contains(point0.upLeft().left()));
        assertTrue (possibleFlagPoints.contains(point0.upLeft().upLeft()));
    }

    @Test
    public void testAvailableFlagsNextToSmallHouse() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        Woodcutter woodcutter = new Woodcutter(player0);
        Point wcPoint = new Point(6, 4);

        map.placeBuilding(woodcutter, wcPoint);

        Collection<Point> possibleFlagPoints = map.getAvailableFlagPoints(player0);

        /* The house's own point */
        assertFalse(possibleFlagPoints.contains(new Point(6, 4)));

        /* The house's flag */
        assertFalse(possibleFlagPoints.contains(new Point(7, 3)));

        /* Points in front, sampled */
        assertFalse(possibleFlagPoints.contains(new Point(8, 2)));
        assertFalse(possibleFlagPoints.contains(new Point(9, 3)));

        /* Points on left, sampled */
        assertFalse(possibleFlagPoints.contains(new Point(6, 2)));
        assertFalse(possibleFlagPoints.contains(new Point(5, 3)));

        /* Points on top, sampled */
        assertTrue (possibleFlagPoints.contains(new Point(4, 4)));
        assertTrue (possibleFlagPoints.contains(new Point(5, 5)));
        assertTrue (possibleFlagPoints.contains(new Point(6, 6)));

        /* Points on right, sampled */
        assertTrue (possibleFlagPoints.contains(new Point(7, 5)));
        assertFalse(possibleFlagPoints.contains(new Point(8, 4)));
    }

    @Test
    public void testAvailableHousesNextToMediumHouse() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point hqPoint = new Point(15, 17);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place sawmill */
        Point point0 = new Point(7, 7);
        Sawmill sawmill = map.placeBuilding(new Sawmill(player0), point0);

        /* Verify that the available houses next to the sawmill are correct */
        Map<Point, Size> possibleHouseSizes = map.getAvailableHousePoints(player0);

        /* The house's own point */
        assertFalse(possibleHouseSizes.containsKey(point0));

        /* The house's flag */
        assertFalse(possibleHouseSizes.containsKey(point0.downRight()));

        /* Points in front, sampled */
        assertEquals(possibleHouseSizes.get(point0.right().down()), MEDIUM);
        assertEquals(possibleHouseSizes.get(point0.right().downRight()), MEDIUM);

        /* Points on left, sampled */
//        assertEquals(possibleHouseSizes.get(new Point(7, 1)), MEDIUM); // WEIRD!!
        assertFalse(possibleHouseSizes.containsKey(point0.downLeft()));
        assertFalse(possibleHouseSizes.containsKey(point0.left()));

        assertEquals(possibleHouseSizes.get(point0.left().downLeft()), MEDIUM);
        assertEquals(possibleHouseSizes.get(point0.left().down()), MEDIUM);

        /* Points on top, sampled */
        assertFalse(possibleHouseSizes.containsKey(point0.upLeft()));
        assertEquals (possibleHouseSizes.get(point0.up()), MEDIUM);

        /* Points on right, sampled */
        assertFalse(possibleHouseSizes.containsKey(point0.upRight()));
        assertFalse(possibleHouseSizes.containsKey(point0.right()));
    }

    @Test
    public void testAvailableFlagsNextToMediumHouse() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place sawmill */
        Point point0 = new Point(7, 7);
        Sawmill sawmill = map.placeBuilding(new Sawmill(player0), point0);

        /* Verify that the available flag points next to the sawmill are correct */
        Collection<Point> possibleFlagPoints = map.getAvailableFlagPoints(player0);

        /* The house's own point */
        assertFalse(possibleFlagPoints.contains(point0));

        /* The house's flag */
        assertFalse(possibleFlagPoints.contains(point0.downRight()));

        /* Points in front, sampled */
        assertFalse(possibleFlagPoints.contains(point0.right().down()));
        assertFalse(possibleFlagPoints.contains(point0.right().downRight()));

        /* Points on left, sampled */
        assertFalse(possibleFlagPoints.contains(point0.down()));
        assertFalse(possibleFlagPoints.contains(point0.downLeft()));
        assertTrue (possibleFlagPoints.contains(point0.left()));

        assertTrue (possibleFlagPoints.contains(point0.left().downLeft()));
        assertTrue (possibleFlagPoints.contains(point0.left().down()));

        /* Points on top, sampled */
        assertTrue (possibleFlagPoints.contains(point0.upLeft()));
        assertTrue (possibleFlagPoints.contains(point0.up()));
        assertTrue (possibleFlagPoints.contains(point0.up().upRight()));

        /* Points on right, sampled */
        assertTrue (possibleFlagPoints.contains(point0.upRight()));
        assertFalse(possibleFlagPoints.contains(point0.right()));

        assertTrue (possibleFlagPoints.contains(point0.right().up()));
        assertTrue (possibleFlagPoints.contains(point0.right().upRight()));
        assertTrue (possibleFlagPoints.contains(point0.right().right()));
        assertTrue (possibleFlagPoints.contains(point0.right().right().downRight()));
    }

    @Test
    public void testAvailableConstructionNextToLargeHouse() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(16, 16);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place farm */
        Point point0 = new Point(10, 8);
        Farm farm = map.placeBuilding(new Farm(player0), point0);

        /*
        left: none
        up-left: none
        up-right: none
        right: none
        down-right: none (is the flag)
        down-left: none

        left-down-left: medium building | flag
        left-left: medium building | flag
        left-up-left: flag
        up-left-up-left: flag
        up: flag
        up-right-up-right: flag
        up-right-right: small house | flag
        right-right: small house | flag
        right-down-right: ?

        left-left-left: large building | flag
        left-left-down-left: large building | flag
        left-left-up-left: large building | flag
        up-left-up-left-left: large building | flag
        up-left-up-left-up-left: large building | flag
        up-left-up-left-up-right: large building | flag
        up-up-right: large building | flag
        up-right-up-right-up-right: large building | flag
        up-right-up-right-right: ?
        up-right-right-right: ?
        right-right-right: ?

         */

        /* Verify that the available construction around the farm is correct
        *
        * left: none
        * up-left: none
        * up-right: none
        * right: none
        * down-right: none (is the flag)
        * down-left: none
        *
        * */
        Map<Point, Size> possibleHouses = map.getAvailableHousePoints(player0);

        /* The house's own point */
        assertFalse(possibleHouses.containsKey(point0));

        /* More space under the house */
        assertFalse(possibleHouses.containsKey(point0.left()));
        assertFalse(possibleHouses.containsKey(point0.upLeft()));
        assertFalse(possibleHouses.containsKey(point0.upRight()));
        assertFalse(possibleHouses.containsKey(point0.right()));
        assertFalse(possibleHouses.containsKey(point0.downLeft()));

        /* The house's flag */
        assertFalse(possibleHouses.containsKey(point0.downRight()));

        /* Surrounding points
        *
        * left-down-left: medium building | flag
        * left-left: medium building | flag
        * left-up-left: flag
        * up-left-up-left: flag
        * up: flag
        * up-right-up-right: flag
        * up-right-right: small house | flag
        * right-right: ?
        * right-down-right: ?
        *
        *  */
        assertEquals(map.isAvailableHousePoint(player0, point0.left().downLeft()), MEDIUM);
        assertTrue(map.isAvailableFlagPoint(player0, point0.left().downLeft()));

        assertEquals(map.isAvailableHousePoint(player0, point0.left().left()), MEDIUM);
        assertTrue(map.isAvailableFlagPoint(player0, point0.left().left()));

        assertNull(map.isAvailableHousePoint(player0, point0.upLeft().upLeft()));
        assertTrue(map.isAvailableFlagPoint(player0, point0.upLeft().upLeft()));

        assertNull(map.isAvailableHousePoint(player0, point0.up()));
        assertTrue(map.isAvailableFlagPoint(player0, point0.up()));

        assertNull(map.isAvailableHousePoint(player0, point0.upRight().upRight()));
        assertTrue(map.isAvailableFlagPoint(player0, point0.upRight().upRight()));

        assertEquals(map.isAvailableHousePoint(player0, point0.upRight().right()), SMALL);
        assertTrue(map.isAvailableFlagPoint(player0, point0.upRight().right()));

        /*
         * Wider area:
         *         left-left-left: large building | flag
         *         left-left-down-left: large building | flag
         *         left-left-up-left: large building | flag
         *         up-left-up-left-left: large building | flag
         *         up-left-up-left-up-left: large building | flag
         *         up-left-up-left-up-right: large building | flag
         *         up-up-right: large building | flag
         *         up-right-up-right-up-right: large building | flag
         *         up-right-up-right-right: ?
         *         up-right-right-right: ?
         *         right-right-right: ?
         */
        assertEquals(map.isAvailableHousePoint(player0, point0.left().left().left()), LARGE);
        assertTrue(map.isAvailableFlagPoint(player0, point0.left().left().left()));

        assertEquals(map.isAvailableHousePoint(player0, point0.left().left().downLeft()), LARGE);
        assertTrue(map.isAvailableFlagPoint(player0, point0.left().left().downLeft()));

        assertEquals(map.isAvailableHousePoint(player0, point0.left().left().upLeft()), LARGE);
        assertTrue(map.isAvailableFlagPoint(player0, point0.left().left().upLeft()));

        assertEquals(map.isAvailableHousePoint(player0, point0.upLeft().upLeft().left()), LARGE);
        assertTrue(map.isAvailableFlagPoint(player0, point0.upLeft().upLeft().left()));

        assertEquals(map.isAvailableHousePoint(player0, point0.upLeft().upLeft().upLeft()), LARGE);
        assertTrue(map.isAvailableFlagPoint(player0, point0.upLeft().upLeft().upLeft()));

        assertEquals(map.isAvailableHousePoint(player0, point0.upLeft().upLeft().upRight()), LARGE);
        assertTrue(map.isAvailableFlagPoint(player0, point0.upLeft().upLeft().upRight()));

        assertEquals(map.isAvailableHousePoint(player0, point0.up().up().right()), LARGE);
        assertTrue(map.isAvailableFlagPoint(player0, point0.up().up().right()));

        assertEquals(map.isAvailableHousePoint(player0, point0.upRight().upRight().upRight()), LARGE);
        assertTrue(map.isAvailableFlagPoint(player0, point0.upRight().upRight().upRight()));

        /* -- PREVIOUS SAMPLED POINTS -- */
        /* Points on left, sampled */
        assertFalse(possibleHouses.containsKey(point0.left().down()));
        assertEquals(possibleHouses.get(point0.left().downLeft()), MEDIUM);
        assertEquals(possibleHouses.get(point0.left().left()), MEDIUM);

        /* Points on top, sampled */
        assertFalse(possibleHouses.containsKey(point0.left().upLeft()));
        assertFalse(possibleHouses.containsKey(point0.left().up()));

        /* Points on right, sampled */
        assertFalse(possibleHouses.containsKey(point0.up()));
        assertFalse(possibleHouses.containsKey(point0.upRight()));
    }

    @Test
    public void testAvailableFlagsNextToLargeHouse() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place farm */
        Point point0 = new Point(8, 8);
        Farm farm = map.placeBuilding(new Farm(player0), point0);

        /* Verify that the available points for flags next to the farm are correct */
        Collection<Point> possibleFlags = map.getAvailableFlagPoints(player0);

        /* The house's own point */
        assertFalse(possibleFlags.contains(point0));

        /* More space under the house */
        assertFalse(possibleFlags.contains(point0.upLeft()));
        assertFalse(possibleFlags.contains(point0.downLeft()));
        assertFalse(possibleFlags.contains(point0.left()));

        /* The house's flag */
        assertFalse(possibleFlags.contains(point0.downRight()));

        /* Points in front, TBD sampled */
        // assertFalse(possibleFlagPoints.contains(new Point(9, 1)));
        // assertFalse(possibleFlagPoints.contains(new Point(10, 2)));
        assertFalse(possibleFlags.contains(point0.down()));
        assertFalse(possibleFlags.contains(point0.right()));

        /* Points on left, sampled */
        assertTrue(possibleFlags.contains(point0.left().down()));
        assertTrue(possibleFlags.contains(point0.left().downLeft()));
        assertTrue(possibleFlags.contains(point0.left().left()));

        /* Points on top, sampled */
        assertTrue(possibleFlags.contains(point0.left().upLeft()));
        assertTrue(possibleFlags.contains(point0.left().up()));
        assertTrue(possibleFlags.contains(point0.up().upLeft()));

        /* Points on right, sampled */
        assertTrue(possibleFlags.contains(point0.up()));
        assertFalse(possibleFlags.contains(point0.upRight()));
    }

    @Test(expected=Exception.class)
    public void testPlaceFlagTooCloseToSmallHouse() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        Woodcutter woodcutter = new Woodcutter(player0);
        Point wcPoint = new Point(6, 4);

        map.placeBuilding(woodcutter, wcPoint);

        Point point0 = new Point(6, 2);

        map.placeFlag(player0, point0);
    }

    @Test(expected=Exception.class)
    public void testPlaceFlagOnHouse() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place woodcutter */
        Point wcPoint = new Point(8, 8);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), wcPoint);

        /* Verify that it's not possible to place a flag on the woodcutter */
        map.placeFlag(player0, wcPoint);
    }

    @Test(expected=Exception.class)
    public void testPlaceHouseOnFlag() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        Woodcutter woodcutter = new Woodcutter(player0);
        Point wcPoint = new Point(6, 4);
        Point point0  = new Point(6, 4);

        map.placeFlag(player0, point0);

        map.placeBuilding(woodcutter, wcPoint);
    }

    @Test(expected=Exception.class)
    public void testPlaceHouseOnHouse() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        Woodcutter woodcutter  = new Woodcutter(player0);
        Quarry     quarry0 = new Quarry(player0);
        Point wcPoint  = new Point(6, 4);
        Point qryPoint = new Point(5, 5);

        map.placeBuilding(woodcutter, wcPoint);
        map.placeBuilding(quarry0, qryPoint);
    }

    @Test
    public void testAutomaticWaypointSelection() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);
        //TODO: Add this test!
    }

    @Test
    public void testPossibleFlagsNextToWater() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);
        Point sharedPoint1 = new Point(7, 5);
        Point sharedPoint2 = new Point(8, 6);
        Point grassPoint   = new Point(9, 5);
        Point waterPoint   = new Point(6, 6);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Create water and grass tiles */
        Tile waterTile = map.getTerrain().getTileDownRight(waterPoint);
        Tile grassTile = map.getTerrain().getTileUpLeft(grassPoint);

        waterTile.setVegetationType(WATER);
        grassTile.setVegetationType(GRASS);

        Collection<Point> possibleFlags = map.getAvailableFlagPoints(player0);

        assertTrue(possibleFlags.contains(sharedPoint1));
        assertTrue(possibleFlags.contains(sharedPoint2));
        assertTrue(possibleFlags.contains(grassPoint));
    }

    @Test
    public void testCannotPlaceFlagInWater() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point centerPoint  = new Point(3, 1);
        Utils.surroundPointWithVegetation(centerPoint, WATER, map);

        /* Verify that the center point is in the middle of the lake */
        assertEquals(map.getTerrain().getTileUpLeft(centerPoint).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileAbove(centerPoint).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileUpRight(centerPoint).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileDownRight(centerPoint).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileBelow(centerPoint).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileDownLeft(centerPoint).getVegetationType(), WATER);

        /* Place headquarter */
        Point point0 = new Point(5, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to place a flag in the lake */
        Collection<Point> possibleFlags = map.getAvailableFlagPoints(player0);

        assertFalse(possibleFlags.contains(centerPoint));
        assertFalse(map.isAvailableFlagPoint(player0, centerPoint));
    }

    @Test
    public void testSetTileToMountainTerrain() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 10, 10);

        /* Set a tile's vegetation to mountain */
        Point top   = new Point(2, 2);

        Tile tile1 = map.getTerrain().getTileBelow(top);

        tile1.setVegetationType(MOUNTAIN);

        /* Verify that the tile's vegetation is set to mountain */
        assertEquals(map.getTerrain().getTileBelow(top).getVegetationType(), MOUNTAIN);
    }

    @Test
    public void testTreeCannotBePlacedOnStone() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 10, 10);

        /* Place stone */
        Point point0  = new Point(3, 3);
        map.placeStone(point0);

        /* Verify that it's not possible to place a tree on the stone */
        try {
            map.placeTree(point0);
            fail();
        } catch (Exception e) {}

        assertTrue(map.getTrees().isEmpty());
    }

    @Test
    public void testTreeCanBePlacedOnMountain() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 10, 10);

        /* Put a small mountain on the map */
        Point point0 = new Point(5, 5);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Verify that it's possible to place a tree on the mountain */
        map.placeTree(point0);

        assertTrue(map.isTreeAtPoint(point0));
    }

    @Test
    public void testWoodcutterCannotBePlacedOnMountain() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(9, 9);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Verify that it's not possible to place a woodcutter on the mountain */
        try {
            map.placeBuilding(new Woodcutter(player0), point1);
            fail();
        } catch (Exception e) {}

        assertTrue(map.getTrees().isEmpty());
    }

    @Test
    public void testWoodcutterCannotBePlacedRightNextToMountain() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(9, 9);
        Utils.surroundPointWithMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Verify that it's not possible to place a woodcutter on the mountain */
        try {
            map.placeBuilding(new Woodcutter(player0), point1.downRight());
            fail();
        } catch (Exception e) {}

        assertTrue(map.getTrees().isEmpty());
    }

    @Test
    public void testBuildingCannotBePlacedDirectlyNextToWater() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small lake on the map */
        Point point1 = new Point(9, 9);
        Utils.surroundPointWithWater(point1, map);

        /* Verify that it's not possible to place a woodcutter right next to the lake */
        try {
            Point point2 = new Point(10, 8);
            map.placeBuilding(new Woodcutter(player0), point2);
            fail();
        } catch (Exception e) {}

        assertTrue(map.getTrees().isEmpty());
    }

    @Test
    public void testCanNotPlaceFlagOnStone() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);
        Point point0  = new Point(3, 3);

        Point hqPoint = new Point(6, 6);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        map.placeStone(point0);

        try {
            map.placeFlag(player0, point0);
            fail();
        } catch (Exception e) {}

        assertEquals(map.getBuildings().size(), 1);
    }

    @Test
    public void testCanNotPlaceFlagOnTree() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);
        Point point0  = new Point(3, 3);

        Point hqPoint = new Point(6, 6);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        map.placeTree(point0);

        try {
            map.placeFlag(player0, point0);
            fail();
        } catch (Exception e) {}

        assertEquals(map.getBuildings().size(), 1);
    }

    @Test
    public void testCanNotPlaceBuildingIfFlagCanNotBePlaced() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);
        Point point0  = new Point(3, 3);

        Point hqPoint = new Point(6, 6);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        map.placeTree(point0.downRight());

        try {
            map.placeBuilding(new Woodcutter(player0), point0);
            fail();
        } catch (Exception e) {}

        assertEquals(map.getBuildings().size(), 1);
        assertEquals(map.getFlags().size(), 1);
        assertEquals(map.getTrees().size(), 1);
    }

    @Test(expected = Exception.class)
    public void testCannotPlaceRoadAcrossLake() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(10, 4);
        Utils.surroundPointWithVegetation(point0, WATER, map);

        /* Verify that the point is surrounded by water */
        assertEquals(map.getTerrain().getTileUpLeft(point0).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileAbove(point0).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileUpRight(point0).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileDownRight(point0).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileBelow(point0).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileDownLeft(point0).getVegetationType(), WATER);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing flag */
        Point point1 = new Point(8, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing flag */
        Point point4 = new Point(12, 4);
        Flag flag1 = map.placeFlag(player0, point4);

        /* Placing road between (8, 4) and (12, 4) */
        Road road0 = map.placeRoad(player0, point1, point0, point4);
    }

    @Test
    public void testRoadConnectionSuggestionsDoNotIncludePointsInWater() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a water tile */
        Point point0 = new Point(10, 4);
        Utils.surroundPointWithVegetation(point0, WATER, map);

        /* Verify that the point is surrounded by water */
        assertEquals(map.getTerrain().getTileUpLeft(point0).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileAbove(point0).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileUpRight(point0).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileDownRight(point0).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileBelow(point0).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileDownLeft(point0).getVegetationType(), WATER);

        /* Placing headquarter */
        Point point21 = new Point(10, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing flag */
        Point point1 = new Point(8, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing flag */
        Point point4 = new Point(12, 4);
        Flag flag1 = map.placeFlag(player0, point4);

        /* Verify that suggested connections from flag0 don't include a point in the water */
        assertFalse(map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point1).contains(point1.right()));
        assertFalse(map.getPossibleRoadConnectionsExcludingEndpoints(player0, point1).contains(point1.right()));
    }

    @Test
    public void testFlagCanBePlacedOnSign() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);
        Point point0  = new Point(3, 3);

        /* Place headquarter */
        Point hqPoint = new Point(6, 6);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place sign */
        map.placeEmptySign(point0);

        assertFalse(map.getSigns().isEmpty());
        assertTrue(map.isSignAtPoint(point0));

        /* Place flag on the sign */
        map.placeFlag(player0, point0);

        /* Verify that the sign is gone and the flag exists */
        assertTrue(map.isFlagAtPoint(point0));
        assertFalse(map.isSignAtPoint(point0));
        assertTrue(map.getSigns().isEmpty());
    }

    @Test
    public void testMineCanBePlacedOnSign() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Put a small mountain on point0 */
        Point point0  = new Point(8, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place sign */
        map.placeEmptySign(point0);

        assertFalse(map.getSigns().isEmpty());
        assertTrue(map.isSignAtPoint(point0));

        /* Build a mine on the sign */
        map.placeBuilding(new GoldMine(player0), point0);

        /* Verify that the sign is gone and the mine exists */
        assertTrue(map.isBuildingAtPoint(point0));
        assertFalse(map.isSignAtPoint(point0));
        assertTrue(map.getSigns().isEmpty());
    }

    @Test
    public void testPlaceHouseOnFullyDestroyedHouse() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(8, 6);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Finish the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Destroy the house */
        woodcutter0.tearDown();

        /* Wait for the house finish burning and disappear from the map */
        for (int i = 0; i < 1000; i++) {
            if (!map.isBuildingAtPoint(point1)) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point1));

        /* Verify that it's possible to place a house again */
        Building woodcutter1 = map.placeBuilding(new Woodcutter(player0), point1);
    }

    @Test (expected = RuntimeException.class)
    public void testCreateInvalidPoint() {

        /* Verify that an exception is thrown when an invalid point is created */
        new Point(5, 4);
    }

    @Test (expected = Exception.class)
    public void testCannotPlaceHouseIfFlagIsTooCloseToOtherFlag() throws Exception {

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
        Point point0 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point0);

        /* Verify that a building cannot be place so that its flag is too
           close to the other flag */
        Point point1 = new Point(7, 11);
        Building woodcutter0 = map.placeBuilding(new Building(player0), point1);
    }

    @Test (expected = Exception.class)
    public void testThatBuildingOtherThanHeadquarterCannotBeFirst() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        GameMap map = new GameMap(players, 100, 100);

        /* Placing headquarter */
        Point point38 = new Point(5, 5);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point38);
    }

    @Test (expected = Exception.class)
    public void testPlaceRoadWithoutFlagAtTheEnd() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(8, 6);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Verify that it's not possible to place a road without a flag at the end */
        Point point2 = new Point(10, 6);
        Point point3 = new Point(12, 6);
        Road road0 = map.placeRoad(player0, point1, point2, point3);
    }

    @Test (expected = Exception.class)
    public void testCannotPlaceFlagOnFlag() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(8, 6);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Verify that it's not possible to place a flag on the existing flag */
        Flag flag1 = map.placeFlag(player0, point1);
    }

    @Test
    public void testNoAvailablePointForFlagTooCloseToBottomEdgeOfMap() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 10, 10);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that there is no available space for a flag too close to the bottom edge */
        Point point1 = new Point(4, 0);

        assertFalse(map.isAvailableFlagPoint(player0, point1));
        assertFalse(map.getAvailableFlagPoints(player0).contains(point1));
    }

    @Test
    public void testNoAvailablePointForHouseTooCloseToBottomEdgeOfMap() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 10, 10);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that there is no available space for a house too close to the bottom edge */
        Point point1 = new Point(5, 1);

        assertNull(map.isAvailableHousePoint(player0, point1));
        assertFalse(map.getAvailableHousePoints(player0).containsKey(point1));
    }

    @Test
    public void testNoAvailablePointForHouseOutsideBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 60, 60);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that there is no available space for a house outside the border */
        Point point1 = new Point(58, 58);

        assertNull(map.isAvailableHousePoint(player0, point1));
        assertFalse(map.getAvailableHousePoints(player0).containsKey(point1));
    }

    @Test
    public void testNoAvailablePointOnTree() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 60, 60);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place tree */
        Point point1 = new Point(8, 8);
        map.placeTree(point1);

        /* Verify that there is no available space for a house on a tree */
        assertNull(map.isAvailableHousePoint(player0, point1));
        assertFalse(map.getAvailableHousePoints(player0).containsKey(point1));
    }

    @Test
    public void testNoAvailablePointTooCloseToExistingHouseDiagonally() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 60, 60);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(9, 9);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify that there is no available space for a house on a tree */
        assertNull(map.isAvailableHousePoint(player0, point1.upRight()));
        assertFalse(map.getAvailableHousePoints(player0).containsKey(point1.upRight()));
    }

    @Test
    public void testCannotPlaceTreeOnBuilding() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 60, 60);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(9, 9);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify that it's not possible to place a tree on the woodcutter */
        try {
            map.placeTree(point1);
            fail();
        } catch (Exception e) {}
    }

    @Test (expected = Exception.class)
    public void testCannotBuildHouseTooCloseToBottomEdgeOfMap() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 10, 10);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to build a house too close to the bottom edge */
        Point point1 = new Point(5, 1);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);
    }

    @Test
    public void testThereIsAvailableFlagSpotForEachAvailableHouseSpot() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that there are available flag spots for all available house spots */
        for (Point point : map.getAvailableHousePoints(player0).keySet()) {
            Point flagPoint = point.downRight();

            /* Skip the test for the headquarter's flag */
            if (flagPoint.equals(point0.downRight())) {
                continue;
            }

            assertTrue(map.isAvailableFlagPoint(player0, flagPoint));
            assertTrue(map.getAvailableFlagPoints(player0).contains(flagPoint));
        }
    }

    @Test
    public void testPlaceFlagOnEachAvailableFlagSpot() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's possible to place a flag at each available flag spot */
        for (Point point : map.getAvailableFlagPoints(player0)) {
            Flag flag0 = map.placeFlag(player0, point);

            assertTrue(map.isFlagAtPoint(point));

            map.removeFlag(flag0);
        }
    }

    @Test
    public void testPlaceHouseOnEachAvailableHouseSpot() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's possible to place a house at each available house spot */
        for (Point point : map.getAvailableHousePoints(player0).keySet()) {

            /* Place building */
            Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point);
            Flag flag0 = woodcutter0.getFlag();

            assertTrue(map.isBuildingAtPoint(point));

            /* Destroy the building */
            woodcutter0.tearDown();

            /* Wait for the building to stop burning and disappear */
            Utils.waitForBuildingToDisappear(woodcutter0);

            /* Remove the flag */
            map.removeFlag(flag0);
        }
    }

    @Test
    public void testAllAvailableHouseSpotsAreWithinBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that available house spots are within the border */
        for (Point point : map.getAvailableHousePoints(player0).keySet()) {

            assertTrue(player0.isWithinBorder(point));
        }
    }

    @Test
    public void testAllAvailableFlagSpotsAreWithinBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that available flag spots are within the border */
        for (Point point : map.getAvailableFlagPoints(player0)) {

            assertTrue(player0.isWithinBorder(point));
        }
    }

    @Test
    public void testNoAvailableFlagSpotsOnTheBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that there are no available flag spots on the border */
        Collection<Point> availableFlagPoints = map.getAvailableFlagPoints(player0);

        for (Point borderPoint : player0.getBorderPoints()) {
            assertFalse(availableFlagPoints.contains(borderPoint));
        }
    }

    @Test
    public void testNoAvailableHouseSpotsOnTheBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that there are no available flag spots on the border */
        Set<Point> availableHousePoints = map.getAvailableHousePoints(player0).keySet();

        for (Point borderPoint : player0.getBorderPoints()) {
            assertFalse(availableHousePoints.contains(borderPoint));
        }
    }

    @Test
    public void testCannotPlaceFlagOnBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to place a flag on any point of the border */
        for (Point borderPoint : player0.getBorderPoints()) {

            try {
                Flag flag0 = map.placeFlag(player0, borderPoint);

                fail();
            } catch (Exception e) {}

            assertFalse(map.isFlagAtPoint(borderPoint));

            try {
                assertNull(map.getFlagAtPoint(borderPoint));
            } catch (Exception e) {}
        }
    }

    @Test
    public void testCannotPlaceHouseOnBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to place a house on any point of the border */
        for (Point borderPoint : player0.getBorderPoints()) {
            try {
                Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), borderPoint);

                fail();
            } catch (Exception e) {}

            assertFalse(map.isBuildingAtPoint(borderPoint));
            assertNull(map.getBuildingAtPoint(borderPoint));
        }
    }

    @Test
    public void testPlaceHouseOnEachAvailableSpotWithLakeInMap() throws Exception {

        /* Starting new game */

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", RED);
        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place a water tile */
        Point point0 = new Point(10, 4);
        Utils.surroundPointWithVegetation(point0, WATER, map);

        /* Verify that the point is surrounded by water */
        assertEquals(map.getTerrain().getTileUpLeft(point0).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileAbove(point0).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileUpRight(point0).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileDownRight(point0).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileBelow(point0).getVegetationType(), WATER);
        assertEquals(map.getTerrain().getTileDownLeft(point0).getVegetationType(), WATER);

        /* Placing headquarter for player0 */
        Point point46 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point46);

        /* Verify that it's possible to build on all available house sites */
        for (Entry<Point, Size> pair : map.getAvailableHousePoints(player0).entrySet()) {
            Building building = null;

            /* Filter points that are not available */
            if (pair.getValue() == null) {
                continue;
            }

            /* Build a house with the right size */
            if (pair.getValue() == SMALL) {
                building = map.placeBuilding(new Woodcutter(player0), pair.getKey());
            } else if (pair.getValue() == MEDIUM) {
                building = map.placeBuilding(new Sawmill(player0), pair.getKey());
            } else if (pair.getValue() == LARGE) {
                building = map.placeBuilding(new Farm(player0), pair.getKey());
            }

            assertNotNull(building);
            assertTrue(map.isBuildingAtPoint(pair.getKey()));
            assertEquals(map.getBuildingAtPoint(pair.getKey()), building);

            /* Tear down the house */
            map.removeFlag(building.getFlag());

            Utils.waitForBuildingToDisappear(building);

            assertFalse(map.isBuildingAtPoint(pair.getKey()));
        }
    }

    @Test
    public void testPlaceHouseOnEachAvailableSpotWithSwampInMap() throws Exception {

        /* Starting new game */

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", RED);
        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Put a two parallel mountains with grass between on the map */
        Point point23 = new Point(8, 6);
        Utils.surroundPointWithSwamp(point23, map);

        Point point24 = new Point(14, 6);
        Utils.surroundPointWithSwamp(point24, map);

        Point point25 = new Point(9, 7);
        Utils.surroundPointWithSwamp(point25, map);

        Point point26 = new Point(15, 7);
        Utils.surroundPointWithSwamp(point26, map);

        Point point27 = new Point(10, 8);
        Utils.surroundPointWithSwamp(point27, map);

        Point point28 = new Point(16, 8);
        Utils.surroundPointWithSwamp(point28, map);

        /* Place the last mountain as a hat */
        Point point29 = new Point(15, 9);
        Utils.surroundPointWithSwamp(point29, map);

        /* Placing headquarter for player0 */
        Point point46 = new Point(26, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point46);

        /* Verify that it's possible to build on all available house sites */
        for (Entry<Point, Size> pair : map.getAvailableHousePoints(player0).entrySet()) {
            Building building = null;

            /* Filter points that are not available */
            if (pair.getValue() == null) {
                continue;
            }

            /* Build a house with the right size */
            if (pair.getValue() == SMALL) {
                building = map.placeBuilding(new Woodcutter(player0), pair.getKey());
            } else if (pair.getValue() == MEDIUM) {
                building = map.placeBuilding(new Sawmill(player0), pair.getKey());
            } else if (pair.getValue() == LARGE) {
                building = map.placeBuilding(new Farm(player0), pair.getKey());
            }

            assertNotNull(building);
            assertTrue(map.isBuildingAtPoint(pair.getKey()));
            assertEquals(map.getBuildingAtPoint(pair.getKey()), building);

            /* Tear down the house */
            map.removeFlag(building.getFlag());

            Utils.waitForBuildingToDisappear(building);

            assertFalse(map.isBuildingAtPoint(pair.getKey()));
        }
    }

    @Test
    public void testAvailableMineOnMountain() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Put a small mountain on point0 */
        Point point0  = new Point(8, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Verify that there is an available point for a mine on the mountain */
        assertTrue(map.isAvailableMinePoint(player0, point0));
        assertTrue(map.getAvailableMinePoints(player0).contains(point0));
    }

    @Test
    public void testNoAvailableHouseOnMountain() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Put a small mountain on point0 */
        Point point0  = new Point(8, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Verify that there is no available point for a house on the mountain */
        assertNull(map.isAvailableHousePoint(player0, point0));
        assertFalse(map.getAvailableHousePoints(player0).containsKey(point0));
    }

    @Test
    public void testNoAvailableMineOnGrass() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Verify that there is no available mine on the grass */
        Point point0  = new Point(8, 8);
        assertFalse(map.isAvailableMinePoint(player0, point0));
        assertFalse(map.getAvailableMinePoints(player0).contains(point0));
    }

    @Test
    public void testNoAvailableMineOutsideBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Put a small mountain on point0 */
        Point point0  = new Point(47, 47);
        Utils.surroundPointWithMountain(point0, map);

        /* Verify that there is no available mine on the grass */
        assertFalse(map.isAvailableMinePoint(player0, point0));
        assertFalse(map.getAvailableMinePoints(player0).contains(point0));
    }

    @Test
    public void testNoAvailableMineOnExistingMine() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Put a small mountain on point0 */
        Point point0  = new Point(8, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a mine on the mountain */
        GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Verify that there is no available point for a house on the mountain */
        assertFalse(map.isAvailableMinePoint(player0, point0));
        assertFalse(map.getAvailableMinePoints(player0).contains(point0));
    }

    @Test
    public void testNoAvailableMineOnFlag() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Put a small mountain on point0 */
        Point point0  = new Point(8, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place a flag on the mountain */
        Flag flag0 = map.placeFlag(player0, point0);

        /* Verify that there is no available point for a house on the mountain */
        assertFalse(map.isAvailableMinePoint(player0, point0));
        assertFalse(map.getAvailableMinePoints(player0).contains(point0));
    }

    @Test
    public void testNoAvailableMineOnRoad() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Put a small mountain on point0 */
        Point point0  = new Point(8, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Place flags */
        Point point1 = new Point(6, 8);
        Flag flag0 = map.placeFlag(player0, point1);
        Point point2 = new Point(10, 8);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place a road on the mountain */
        Road road0 = map.placeRoad(player0, point1, point0, point2);

        /* Verify that there is no available point for a house on the mountain */
        assertFalse(map.isAvailableMinePoint(player0, point0));
        assertFalse(map.getAvailableMinePoints(player0).contains(point0));
    }

    @Test
    public void testNoAvailableMineOnWater() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Put a lake on the map */
        Point point0  = new Point(8, 8);
        Utils.surroundPointWithWater(point0, map);

        /* Place headquarter */
        Point hqPoint = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Verify that there is no available mine on the grass */
        assertFalse(map.isAvailableMinePoint(player0, point0));
        assertFalse(map.getAvailableMinePoints(player0).contains(point0));
    }

    @Test
    public void testOnlyAvailableFlagPointsOnBorderBetweenMountainAndGrass() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Put a small mountain on point0 */
        Point point0  = new Point(8, 8);
        Utils.surroundPointWithMountain(point0, map);

        /* Verify that there are available flag points next to the mountain */
        List<Point> edgePoints = new LinkedList<>();
        edgePoints.add(point0.upRight());
        edgePoints.add(point0.right());
        edgePoints.add(point0.downRight());
        edgePoints.add(point0.downLeft());
        edgePoints.add(point0.left());
        edgePoints.add(point0.upLeft());

        for (Point point : edgePoints) {
            assertTrue(map.isAvailableFlagPoint(player0, point));
            assertTrue(map.getAvailableFlagPoints(player0).contains(point));
        }

        /* Verify that there are no available mine points on the edge */
        for (Point point : edgePoints) {
            assertFalse(map.isAvailableMinePoint(player0, point));
            assertFalse(map.getAvailableMinePoints(player0).contains(point));
        }

        /* Verify that there are no available house points on the edge */
        for (Point point : edgePoints) {
            assertNull(map.isAvailableHousePoint(player0, point));
            assertFalse(map.getAvailableHousePoints(player0).containsKey(point));
        }
    }

    @Test
    public void testOnlyAvailableFlagPointsNextToStone() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a stone */
        Point point0  = new Point(8, 8);
        Stone stone0 = map.placeStone(point0);

        /* Verify that there are available flag points next to the mountain */
        List<Point> edgePoints = new LinkedList<>();
        edgePoints.add(point0.upRight());
        edgePoints.add(point0.right());
        edgePoints.add(point0.downRight());
        edgePoints.add(point0.downLeft());
        edgePoints.add(point0.left());
        edgePoints.add(point0.upLeft());

        for (Point point : edgePoints) {
            assertTrue(map.isAvailableFlagPoint(player0, point));
            assertTrue(map.getAvailableFlagPoints(player0).contains(point));
        }

        /* Verify that there are no available mine points on the edge */
        for (Point point : edgePoints) {
            assertFalse(map.isAvailableMinePoint(player0, point));
            assertFalse(map.getAvailableMinePoints(player0).contains(point));
        }

        /* Verify that there are no available house points on the edge */
        for (Point point : edgePoints) {
            assertNull(map.isAvailableHousePoint(player0, point));
            assertFalse(map.getAvailableHousePoints(player0).containsKey(point));
        }
    }

    @Test
    public void testAvailableMineSite() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player2 = new Player("Player 0", Color.RED);
        Player player3 = new Player("Player 1", Color.BLUE);
        List<Player> players = new LinkedList<>();
        players.add(player2);
        players.add(player3);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);
        /* Create a small mountain */
        Point point0 = new Point(5, 13);
        Point point1 = new Point(8, 14);
        Point point2 = new Point(5, 15);
        Utils.surroundPointWithVegetation(point0, MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN, map);

        /* Put gold at mountain */
        map.getTerrain().surroundPointWithMineral(point0, GOLD);
        map.getTerrain().surroundPointWithMineral(point1, GOLD);
        map.getTerrain().surroundPointWithMineral(point2, GOLD);

        /* Create a small mountain */
        Point point3 = new Point(8, 16);
        Point point4 = new Point(11, 17);
        Point point5 = new Point(8, 18);
        Utils.surroundPointWithVegetation(point3, MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point4, MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point5, MOUNTAIN, map);

        /* Put coal at mountain */
        map.getTerrain().surroundPointWithMineral(point3, COAL);
        map.getTerrain().surroundPointWithMineral(point4, COAL);
        map.getTerrain().surroundPointWithMineral(point5, COAL);

        /* Placing headquarter for player2 */
        Point point17 = new Point(8, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player2), point17);

        /* Placing headquarter for player3 */
        Point point18 = new Point(45, 21);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player3), point18);

        /* Placing barracks for player3 */
        Point point19 = new Point(29, 21);
        Barracks barracks0 = map.placeBuilding(new Barracks(player3), point19);

        /* Placing road between (30, 20) and (46, 20) */
        Point point20 = new Point(30, 20);
        Point point21 = new Point(32, 20);
        Point point22 = new Point(34, 20);
        Point point23 = new Point(36, 20);
        Point point24 = new Point(38, 20);
        Point point25 = new Point(40, 20);
        Point point26 = new Point(42, 20);
        Point point27 = new Point(44, 20);
        Point point28 = new Point(46, 20);
        Road road0 = map.placeRoad(player3, point20, point21, point22, point23, point24, point25, point26, point27, point28);

        /* Placing flag */
        Point point29 = new Point(5, 11);
        Flag flag0 = map.placeFlag(player2, point29);

        /* Placing road between (5, 11) and (9, 9) */
        Point point30 = new Point(6, 10);
        Point point31 = new Point(7, 9);
        Point point32 = new Point(9, 9);
        Road road1 = map.placeRoad(player2, point29, point30, point31, point32);

        /* Place mine */
        Point point33 = new Point(5, 13);

        assertFalse(map.isAvailableMinePoint(player2, point33));
    }

    @Test
    public void testSizeComparisons() {
        assertTrue(Size.contains(LARGE, LARGE));
        assertTrue(Size.contains(LARGE, MEDIUM));
        assertTrue(Size.contains(LARGE, SMALL));

        assertFalse(Size.contains(MEDIUM, LARGE));
        assertTrue(Size.contains(MEDIUM, MEDIUM));
        assertTrue(Size.contains(MEDIUM, SMALL));

        assertFalse(Size.contains(SMALL, LARGE));
        assertFalse(Size.contains(SMALL, MEDIUM));
        assertTrue(Size.contains(SMALL, SMALL));

        assertFalse(Size.contains(null, LARGE));
        assertFalse(Size.contains(null, MEDIUM));
        assertFalse(Size.contains(null, SMALL));

        assertTrue(LARGE.contains(LARGE));
        assertTrue(LARGE.contains(MEDIUM));
        assertTrue(LARGE.contains(SMALL));

        assertFalse(MEDIUM.contains(LARGE));
        assertTrue(MEDIUM.contains(MEDIUM));
        assertTrue(MEDIUM.contains(SMALL));

        assertFalse(SMALL.contains(LARGE));
        assertFalse(SMALL.contains(MEDIUM));
        assertTrue(SMALL.contains(SMALL));
    }

    @Test
    public void testOnlyFlagsAvailableAroundStone() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", Color.RED);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place stone */
        Point point1 = new Point(5, 5);
        Stone stone0 = map.placeStone(point1);

        /* Verify that there are only available flags around the stone */
        assertTrue(map.isAvailableFlagPoint(player0, point1.left()));
        assertTrue(map.isAvailableFlagPoint(player0, point1.upLeft()));
        assertTrue(map.isAvailableFlagPoint(player0, point1.upRight()));
        assertTrue(map.isAvailableFlagPoint(player0, point1.right()));
        assertTrue(map.isAvailableFlagPoint(player0, point1.downRight()));
        assertTrue(map.isAvailableFlagPoint(player0, point1.downLeft()));

        assertNull(map.isAvailableHousePoint(player0, point1.left()));
        assertNull(map.isAvailableHousePoint(player0, point1.upLeft()));
        assertNull(map.isAvailableHousePoint(player0, point1.upRight()));
        assertNull(map.isAvailableHousePoint(player0, point1.right()));
        assertNull(map.isAvailableHousePoint(player0, point1.downRight()));
        assertNull(map.isAvailableHousePoint(player0, point1.downLeft()));
    }

    @Test
    public void testAvailableHousesInLineBetweenTrees() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", Color.RED);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place tree */
        Point point1 = new Point(5, 5);
        Tree tree0 = map.placeTree(point1);

        /* Place tree */
        Point point2 = new Point(13, 5);
        Tree tree1 = map.placeTree(point2);

        /* Verify that the available buildings between the trees are small house, castle, small house */
        assertEquals(map.isAvailableHousePoint(player0, point1.right()), SMALL);
        assertEquals(map.isAvailableHousePoint(player0, point1.right().right()), LARGE);
        assertEquals(map.isAvailableHousePoint(player0, point1.right().right().right()), SMALL);
    }

    @Test
    public void testAvailableSmallHousesDiagonallyFromTree() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", Color.RED);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place tree */
        Point point1 = new Point(7, 15);
        Tree tree0 = map.placeTree(point1);

        /* Verify that only small houses are up right, down right, down left from the tree */
        assertEquals(map.isAvailableHousePoint(player0, point1.downRight()), SMALL);
        assertEquals(map.isAvailableHousePoint(player0, point1.upRight()),   SMALL);
        assertEquals(map.isAvailableHousePoint(player0, point1.downLeft()),  SMALL);

        /* Verify that only a flag is available up left from the tree - the tree occupies the point that would be
         * required for the flag for a house
         */
        assertNull(map.isAvailableHousePoint(player0, point1.upLeft()));
        assertTrue(map.isAvailableFlagPoint(player0, point1.upLeft()));
    }

    @Test
    public void testLargeHouseAvailableUnderFlagAndRoadTerrain() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", Color.RED);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(20, 20);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place terrain that can only handle roads and flags - no buildings */
        Point point1 = new Point(15, 15);
        Utils.surroundPointWithVegetation(point1, DESERT, map);
        Utils.surroundPointWithVegetation(point1.right(), DESERT, map);
        Utils.surroundPointWithVegetation(point1.right().right(), DESERT, map);

        /* Verify that it's possible to build a large house close to the vegetation */
        assertEquals(map.isAvailableHousePoint(player0, point1.right().down()), LARGE);
    }
}
