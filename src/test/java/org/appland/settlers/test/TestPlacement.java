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
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

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
import static org.appland.settlers.model.DetailedVegetation.DESERT_1;
import static org.appland.settlers.model.DetailedVegetation.MEADOW_1;
import static org.appland.settlers.model.DetailedVegetation.MOUNTAIN_1;
import static org.appland.settlers.model.DetailedVegetation.WATER;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.MEDIUM;
import static org.appland.settlers.model.Size.SMALL;
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
        Player player0 = new Player("Player 0", BLUE);
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);

        /* Verify that the player has no borders yet */
        assertEquals(player0.getBorderPoints().size(), 0);
    }

    @Test
    public void testAvailableFlagPointsContainsValidFlagPoint() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that there is a valid flag point in the available flag points */
        Collection<Point> flagPoints = map.getAvailableFlagPoints(player0);

        Point point1 = new Point(8, 6);
        assertTrue(flagPoints.contains(point1));
    }

    @Test
    public void testFlagsCannotBePlacedEdgeOfGameMap() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 10, 10);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        Collection<Point> flagPoints = map.getAvailableFlagPoints(player0);

        /* Test that flags can't be placed on the edge of the map */
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(13, 7);
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Create mini-lake */
        Point waterPoint = new Point(2, 2);
        Utils.surroundPointWithWater(waterPoint, map);

        /* Verify that there is no available spot for a flag on the lake */
        Collection<Point> possibleFlags = map.getAvailableFlagPoints(player0);

        assertFalse(possibleFlags.contains(waterPoint));
    }

    @Test
    public void testNoAvailableBuildingSpotOnLake() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);


        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Create mini-lake */
        Point waterPoint = new Point(2, 2);
        Utils.surroundPointWithWater(waterPoint, map);

        /* Verify that there is no available spot for a building on the lake */
        Map<Point, Size> possibleBuildings = map.getAvailableHousePoints(player0);

        assertFalse(possibleBuildings.containsKey(waterPoint));
    }

    @Test
    public void testNoAvailableFlagOnStone() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
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

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
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

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Plant a tree */
        Point point1 = new Point(2, 2);
        map.placeTree(point1, Tree.TreeType.PINE);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that there is no available spot for a flag on the tree */
        Collection<Point> possibleFlags = map.getAvailableFlagPoints(player0);

        assertFalse(possibleFlags.contains(point1));
    }

    @Test
    public void testNoAvailableBuildingSpotOnTree() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Plant a tree */
        Point point1 = new Point(2, 2);
        map.placeTree(point1, Tree.TreeType.PINE);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that there is no available spot for a building on the tree */
        Map<Point, Size> possibleBuildings = map.getAvailableHousePoints(player0);

        assertFalse(possibleBuildings.containsKey(point1));
    }

    @Test
    public void testNoAvailableFlagOnRoad() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(5, 5);
        Point point3 = new Point(9, 5);
        map.placeFlag(player0, point1);
        map.placeFlag(player0, point3);

        /* Place a road */
        Point point2 = new Point(7, 5);
        map.placeRoad(player0, point1, point2, point3);

        /* Verify that there is no available spot for a flag on the tree */
        Collection<Point> possibleFlags = map.getAvailableFlagPoints(player0);

        assertFalse(possibleFlags.contains(point2));
    }

    @Test
    public void testNoAvailableBuildingSpotOnRoad() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(5, 5);
        Point point3 = new Point(9, 5);
        map.placeFlag(player0, point1);
        map.placeFlag(player0, point3);

        /* Place a road */
        Point point2 = new Point(7, 5);
        map.placeRoad(player0, point1, point2, point3);

        /* Verify that there is no available spot for a building on the road */
        Map<Point, Size> possibleBuildings = map.getAvailableHousePoints(player0);

        assertFalse(possibleBuildings.containsKey(point2));
    }

    @Test
    public void testAvailableFlagsNextToFlag() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(12, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(7, 7);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Verify that the available flag points next to the flag are correct */
        Collection<Point> possibleFlagPoints = map.getAvailableFlagPoints(player0);

        /* The flag's own point */
        assertFalse(possibleFlagPoints.contains(point1));

        /* The right and left of the flag */
        assertFalse(possibleFlagPoints.contains(point1.right()));
        assertFalse(possibleFlagPoints.contains(point1.left()));

        /* Diagonally of the flag */
        assertFalse(possibleFlagPoints.contains(point1.upRight()));
        assertFalse(possibleFlagPoints.contains(point1.downRight()));
        assertFalse(possibleFlagPoints.contains(point1.upLeft()));
        assertFalse(possibleFlagPoints.contains(point1.downLeft()));

        /* Surrounding points */
        assertTrue(possibleFlagPoints.contains(point1.up()));
        assertTrue(possibleFlagPoints.contains(point1.down()));
        assertTrue(possibleFlagPoints.contains(point1.upRight().upRight()));
        assertTrue(possibleFlagPoints.contains(point1.upRight().right()));
        assertTrue(possibleFlagPoints.contains(point1.right().right()));
        assertTrue(possibleFlagPoints.contains(point1.downRight().right()));
        assertTrue(possibleFlagPoints.contains(point1.downRight().downRight()));
        assertTrue(possibleFlagPoints.contains(point1.down()));
        assertTrue(possibleFlagPoints.contains(point1.downLeft().downLeft()));
        assertTrue(possibleFlagPoints.contains(point1.downLeft().left()));
        assertTrue(possibleFlagPoints.contains(point1.left().left()));
        assertTrue(possibleFlagPoints.contains(point1.upLeft().left()));
        assertTrue(possibleFlagPoints.contains(point1.upLeft().upLeft()));
    }

    @Test
    public void testAvailableFlagsNextToSmallHouse() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter */
        Point point1 = new Point(6, 4);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        Collection<Point> possibleFlagPoints = map.getAvailableFlagPoints(player0);

        /* The house's own point */
        assertFalse(possibleFlagPoints.contains(point1));

        /* The house's flag */
        assertFalse(possibleFlagPoints.contains(point1.downRight()));

        /* Points in front, sampled */
        assertFalse(possibleFlagPoints.contains(point1.downRight().downRight()));
        assertFalse(possibleFlagPoints.contains(point1.downRight().right()));

        /* Points on left, sampled */
        assertFalse(possibleFlagPoints.contains(point1.down()));
        assertFalse(possibleFlagPoints.contains(point1.downLeft()));

        /* Points on top, sampled */
        assertTrue(possibleFlagPoints.contains(point1.left()));
        assertTrue(possibleFlagPoints.contains(point1.upLeft()));
        assertTrue(possibleFlagPoints.contains(point1.up()));

        /* Points on right, sampled */
        assertTrue(possibleFlagPoints.contains(point1.upRight()));
        assertFalse(possibleFlagPoints.contains(point1.right()));
    }

    @Test
    public void testAvailableHousesNextToMediumHouse() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(13, 7);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(7, 7);
        Sawmill sawmill = map.placeBuilding(new Sawmill(player0), point1);

        /* Verify that the available houses next to the sawmill are correct */
        Map<Point, Size> possibleHouseSizes = map.getAvailableHousePoints(player0);

        /* The house's own point */
        assertFalse(possibleHouseSizes.containsKey(point1));

        /* The house's flag */
        assertFalse(possibleHouseSizes.containsKey(point1.downRight()));

        /* Points in front, sampled */
        assertEquals(possibleHouseSizes.get(point1.right().down()), MEDIUM);
        assertEquals(possibleHouseSizes.get(point1.right().downRight()), MEDIUM);

        /* Points on left, sampled */
//        assertEquals(possibleHouseSizes.get(new Point(7, 1)), MEDIUM); // WEIRD!!
        assertFalse(possibleHouseSizes.containsKey(point1.downLeft()));
        assertFalse(possibleHouseSizes.containsKey(point1.left()));

        assertEquals(possibleHouseSizes.get(point1.left().downLeft()), MEDIUM);
        assertEquals(possibleHouseSizes.get(point1.left().down()), MEDIUM);

        /* Points on top, sampled */
        assertFalse(possibleHouseSizes.containsKey(point1.upLeft()));
        assertEquals(possibleHouseSizes.get(point1.up()), MEDIUM);

        /* Points on right, sampled */
        assertFalse(possibleHouseSizes.containsKey(point1.upRight()));
        assertFalse(possibleHouseSizes.containsKey(point1.right()));
    }

    @Test
    public void testAvailableFlagsNextToMediumHouse() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(12, 12);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(7, 7);
        Sawmill sawmill = map.placeBuilding(new Sawmill(player0), point1);

        /* Verify that the available flag points next to the sawmill are correct */
        Collection<Point> possibleFlagPoints = map.getAvailableFlagPoints(player0);

        /* The house's own point */
        assertFalse(possibleFlagPoints.contains(point1));

        /* The house's flag */
        assertFalse(possibleFlagPoints.contains(point1.downRight()));

        /* Points in front, sampled */
        assertFalse(possibleFlagPoints.contains(point1.right().down()));
        assertFalse(possibleFlagPoints.contains(point1.right().downRight()));

        /* Points on left, sampled */
        assertFalse(possibleFlagPoints.contains(point1.down()));
        assertFalse(possibleFlagPoints.contains(point1.downLeft()));
        assertTrue(possibleFlagPoints.contains(point1.left()));

        assertTrue(possibleFlagPoints.contains(point1.left().downLeft()));
        assertTrue(possibleFlagPoints.contains(point1.left().down()));

        /* Points on top, sampled */
        assertTrue(possibleFlagPoints.contains(point1.upLeft()));
        assertTrue(possibleFlagPoints.contains(point1.up()));
        assertTrue(possibleFlagPoints.contains(point1.up().upRight()));

        /* Points on right, sampled */
        assertTrue(possibleFlagPoints.contains(point1.upRight()));
        assertFalse(possibleFlagPoints.contains(point1.right()));

        assertTrue(possibleFlagPoints.contains(point1.right().up()));
        assertTrue(possibleFlagPoints.contains(point1.right().upRight()));
        assertTrue(possibleFlagPoints.contains(point1.right().right()));
        assertTrue(possibleFlagPoints.contains(point1.right().right().downRight()));
    }

    @Test
    public void testAvailableConstructionNextToLargeHouse() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(18, 8);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(10, 8);
        Farm farm = map.placeBuilding(new Farm(player0), point1);

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
        assertFalse(possibleHouses.containsKey(point1));

        /* More space under the house */
        assertFalse(possibleHouses.containsKey(point1.left()));
        assertFalse(possibleHouses.containsKey(point1.upLeft()));
        assertFalse(possibleHouses.containsKey(point1.upRight()));
        assertFalse(possibleHouses.containsKey(point1.right()));
        assertFalse(possibleHouses.containsKey(point1.downLeft()));

        /* The house's flag */
        assertFalse(possibleHouses.containsKey(point1.downRight()));

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
        assertEquals(map.isAvailableHousePoint(player0, point1.left().downLeft()), MEDIUM);
        assertTrue(map.isAvailableFlagPoint(player0, point1.left().downLeft()));

        assertEquals(map.isAvailableHousePoint(player0, point1.left().left()), MEDIUM);
        assertTrue(map.isAvailableFlagPoint(player0, point1.left().left()));

        assertNull(map.isAvailableHousePoint(player0, point1.upLeft().upLeft()));
        assertTrue(map.isAvailableFlagPoint(player0, point1.upLeft().upLeft()));

        assertNull(map.isAvailableHousePoint(player0, point1.up()));
        assertTrue(map.isAvailableFlagPoint(player0, point1.up()));

        assertNull(map.isAvailableHousePoint(player0, point1.upRight().upRight()));
        assertTrue(map.isAvailableFlagPoint(player0, point1.upRight().upRight()));

        assertEquals(map.isAvailableHousePoint(player0, point1.upRight().right()), SMALL);
        assertTrue(map.isAvailableFlagPoint(player0, point1.upRight().right()));

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
        assertEquals(map.isAvailableHousePoint(player0, point1.left().left().left()), LARGE);
        assertTrue(map.isAvailableFlagPoint(player0, point1.left().left().left()));

        assertEquals(map.isAvailableHousePoint(player0, point1.left().left().downLeft()), LARGE);
        assertTrue(map.isAvailableFlagPoint(player0, point1.left().left().downLeft()));

        assertEquals(map.isAvailableHousePoint(player0, point1.left().left().upLeft()), LARGE);
        assertTrue(map.isAvailableFlagPoint(player0, point1.left().left().upLeft()));

        assertEquals(map.isAvailableHousePoint(player0, point1.upLeft().upLeft().left()), LARGE);
        assertTrue(map.isAvailableFlagPoint(player0, point1.upLeft().upLeft().left()));

        assertEquals(map.isAvailableHousePoint(player0, point1.upLeft().upLeft().upLeft()), LARGE);
        assertTrue(map.isAvailableFlagPoint(player0, point1.upLeft().upLeft().upLeft()));

        assertEquals(map.isAvailableHousePoint(player0, point1.upLeft().upLeft().upRight()), LARGE);
        assertTrue(map.isAvailableFlagPoint(player0, point1.upLeft().upLeft().upRight()));

        assertEquals(map.isAvailableHousePoint(player0, point1.up().up().right()), LARGE);
        assertTrue(map.isAvailableFlagPoint(player0, point1.up().up().right()));

        assertEquals(map.isAvailableHousePoint(player0, point1.upRight().upRight().upRight()), LARGE);
        assertTrue(map.isAvailableFlagPoint(player0, point1.upRight().upRight().upRight()));

        /* -- PREVIOUS SAMPLED POINTS -- */
        /* Points on left, sampled */
        assertFalse(possibleHouses.containsKey(point1.left().down()));
        assertEquals(possibleHouses.get(point1.left().downLeft()), MEDIUM);
        assertEquals(possibleHouses.get(point1.left().left()), MEDIUM);

        /* Points on top, sampled */
        assertFalse(possibleHouses.containsKey(point1.left().upLeft()));
        assertFalse(possibleHouses.containsKey(point1.left().up()));

        /* Points on right, sampled */
        assertFalse(possibleHouses.containsKey(point1.up()));
        assertFalse(possibleHouses.containsKey(point1.upRight()));
    }

    @Test
    public void testAvailableFlagsNextToLargeHouse() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 12);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(8, 8);
        Farm farm = map.placeBuilding(new Farm(player0), point1);

        /* Verify that the available points for flags next to the farm are correct */
        Collection<Point> possibleFlags = map.getAvailableFlagPoints(player0);

        /* The house's own point */
        assertFalse(possibleFlags.contains(point1));

        /* More space under the house */
        assertFalse(possibleFlags.contains(point1.upLeft()));
        assertFalse(possibleFlags.contains(point1.downLeft()));
        assertFalse(possibleFlags.contains(point1.left()));

        /* The house's flag */
        assertFalse(possibleFlags.contains(point1.downRight()));

        /* Points in front, TBD sampled */
        // assertFalse(possibleFlagPoints.contains(new Point(9, 1)));
        // assertFalse(possibleFlagPoints.contains(new Point(10, 2)));
        assertFalse(possibleFlags.contains(point1.down()));
        assertFalse(possibleFlags.contains(point1.right()));

        /* Points on left, sampled */
        assertTrue(possibleFlags.contains(point1.left().down()));
        assertTrue(possibleFlags.contains(point1.left().downLeft()));
        assertTrue(possibleFlags.contains(point1.left().left()));

        /* Points on top, sampled */
        assertTrue(possibleFlags.contains(point1.left().upLeft()));
        assertTrue(possibleFlags.contains(point1.left().up()));
        assertTrue(possibleFlags.contains(point1.up().upLeft()));

        /* Points on right, sampled */
        assertTrue(possibleFlags.contains(point1.up()));
        assertFalse(possibleFlags.contains(point1.upRight()));
    }

    @Test
    public void testPlaceFlagTooCloseToSmallHouse() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(14, 4);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter */
        Point point1 = new Point(6, 4);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify that it's not possible to place a flag too close to the woodcutter */
        Point point3 = new Point(6, 2);

        try {
            map.placeFlag(player0, point3);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlaceFlagOnHouse() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(18, 8);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(8, 8);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify that it's not possible to place a flag on the woodcutter */
        try {
            map.placeFlag(player0, point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlaceHouseOnFlag() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(14, 4);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(6, 4);
        map.placeFlag(player0, point1);

        /* Verify that it's not possible to place a house on the flag */
        Woodcutter woodcutter = new Woodcutter(player0);

        try {
            map.placeBuilding(woodcutter, point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlaceHouseWithFlagOnOtherHouse() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter */
        Point point1 = new Point(6, 4);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify that it's not possible to place a quarry so that the flag is on top of the woodcutter */
        Quarry quarry0 = new Quarry(player0);
        Point point2 = new Point(5, 5);

        try {
            map.placeBuilding(quarry0, point2);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPossibleFlagsNextToWater() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);
        Point sharedPoint1 = new Point(7, 5);
        Point sharedPoint2 = new Point(8, 6);
        Point grassPoint = new Point(9, 5);
        Point waterPoint = new Point(6, 6);

        /* Place headquarter */
        Point point0 = new Point(13, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Create water and meadow 1 tiles */
        map.setDetailedVegetationDownRight(waterPoint, WATER);
        map.setDetailedVegetationBelow(grassPoint, MEADOW_1);

        /* Verify that it's possible to place flags next to the water */
        Collection<Point> possibleFlags = map.getAvailableFlagPoints(player0);

        assertTrue(possibleFlags.contains(sharedPoint1));
        assertTrue(possibleFlags.contains(sharedPoint2));
        assertTrue(possibleFlags.contains(grassPoint));
    }

    @Test
    public void testCannotPlaceFlagInWater() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point centerPoint = new Point(3, 1);
        Utils.surroundPointWithVegetation(centerPoint, WATER, map);

        /* Verify that the center point is in the middle of the lake */
        assertEquals(map.getDetailedVegetationUpLeft(centerPoint), WATER);
        assertEquals(map.getDetailedVegetationAbove(centerPoint), WATER);
        assertEquals(map.getDetailedVegetationUpRight(centerPoint), WATER);
        assertEquals(map.getDetailedVegetationDownRight(centerPoint), WATER);
        assertEquals(map.getDetailedVegetationBelow(centerPoint), WATER);
        assertEquals(map.getDetailedVegetationDownLeft(centerPoint), WATER);

        /* Place headquarter */
        Point point0 = new Point(5, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to place a flag in the lake */
        Collection<Point> possibleFlags = map.getAvailableFlagPoints(player0);

        assertFalse(possibleFlags.contains(centerPoint));
        assertFalse(map.isAvailableFlagPoint(player0, centerPoint));
    }

    @Test
    public void testSetDetailedVegetationToMountainTerrain() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 10, 10);

        /* Set a tile's vegetation to mountain */
        Point top = new Point(2, 2);

        map.setDetailedVegetationBelow(top, MOUNTAIN_1);

        /* Verify that the tile's vegetation is set to mountain */
        assertEquals(map.getDetailedVegetationBelow(top), MOUNTAIN_1);
    }

    @Test
    public void testTreeCannotBePlacedOnStone() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 10, 10);

        /* Place stone */
        Point point0 = new Point(3, 3);
        map.placeStone(point0);

        /* Verify that it's not possible to place a tree on the stone */
        try {
            map.placeTree(point0, Tree.TreeType.PINE);
            fail();
        } catch (Exception e) {}

        assertTrue(map.getTrees().isEmpty());
    }

    @Test
    public void testTreeCanBePlacedOnMountain() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 10, 10);

        /* Put a small mountain on the map */
        Point point0 = new Point(5, 5);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Verify that it's possible to place a tree on the mountain */
        map.placeTree(point0, Tree.TreeType.PINE);

        assertTrue(map.isTreeAtPoint(point0));
    }

    @Test
    public void testWoodcutterCannotBePlacedOnMountain() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(9, 9);
        Utils.surroundPointWithMinableMountain(point1, map);
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(9, 9);
        Utils.surroundPointWithMinableMountain(point1, map);
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
        Player player0 = new Player("Player 0", BLUE);
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

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);

        /* Place headquarter */
        Point point0 = new Point(6, 6);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place stone */
        Point point1 = new Point(3, 3);
        map.placeStone(point1);

        /* Verify that it's not possible to place a flag on the stone */
        try {
            map.placeFlag(player0, point1);

            fail();
        } catch (Exception e) {}

        assertEquals(map.getBuildings().size(), 1);
    }

    @Test
    public void testCanNotPlaceFlagOnTree() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);

        /* Place headquarter */
        Point point0 = new Point(6, 6);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place tree */
        Point point1 = new Point(3, 3);
        map.placeTree(point1, Tree.TreeType.PINE);

        /* Verify that it's not possible to place a flag on the tree */
        try {
            map.placeFlag(player0, point1);
            fail();
        } catch (Exception e) {}

        assertEquals(map.getBuildings().size(), 1);
    }

    @Test
    public void testCanNotPlaceBuildingIfFlagCanNotBePlaced() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);

        /* Place headquarter */
        Point point0 = new Point(6, 6);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place tree */
        Point point1 = new Point(3, 3);
        map.placeTree(point1.downRight(), Tree.TreeType.PINE);

        /* Verify that it's not possible to place a house on the tree */
        try {
            map.placeBuilding(new Woodcutter(player0), point1);
            fail();
        } catch (Exception e) {}

        assertEquals(map.getBuildings().size(), 1);
        assertEquals(map.getFlags().size(), 1);
        assertEquals(map.getTrees().size(), 1);
    }

    @Test
    public void testCannotPlaceRoadAcrossLake() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(10, 4);
        Utils.surroundPointWithVegetation(point0, WATER, map);

        /* Verify that the point is surrounded by water */
        assertEquals(map.getDetailedVegetationUpLeft(point0), WATER);
        assertEquals(map.getDetailedVegetationAbove(point0), WATER);
        assertEquals(map.getDetailedVegetationUpRight(point0), WATER);
        assertEquals(map.getDetailedVegetationDownRight(point0), WATER);
        assertEquals(map.getDetailedVegetationBelow(point0), WATER);
        assertEquals(map.getDetailedVegetationDownLeft(point0), WATER);

        /* Place headquarter */
        Point point21 = new Point(4, 4);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place flag */
        Point point1 = new Point(8, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place flag */
        Point point4 = new Point(12, 4);
        Flag flag1 = map.placeFlag(player0, point4);

        /* Place road between (8, 4) and (12, 4) */
        try {
            Road road0 = map.placeRoad(player0, point1, point0, point4);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testRoadConnectionSuggestionsDoNotIncludePointsInWater() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a water tile */
        Point point0 = new Point(10, 4);
        Utils.surroundPointWithVegetation(point0, WATER, map);

        /* Verify that the point is surrounded by water */
        assertEquals(map.getDetailedVegetationUpLeft(point0), WATER);
        assertEquals(map.getDetailedVegetationAbove(point0), WATER);
        assertEquals(map.getDetailedVegetationUpRight(point0), WATER);
        assertEquals(map.getDetailedVegetationDownRight(point0), WATER);
        assertEquals(map.getDetailedVegetationBelow(point0), WATER);
        assertEquals(map.getDetailedVegetationDownLeft(point0), WATER);

        /* Place headquarter */
        Point point21 = new Point(10, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place flag */
        Point point1 = new Point(8, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place flag */
        Point point4 = new Point(12, 4);
        Flag flag1 = map.placeFlag(player0, point4);

        /* Verify that suggested connections from flag0 don't include a point in the water */
        assertFalse(map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point1).contains(point1.right()));
        assertFalse(map.getPossibleRoadConnectionsExcludingEndpoints(player0, point1).contains(point1.right()));
    }

    @Test
    public void testFlagCanBePlacedOnSign() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);

        /* Place headquarter */
        Point point0 = new Point(6, 6);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place sign */
        Point point1 = new Point(3, 3);
        map.placeEmptySign(point1);

        assertFalse(map.getSigns().isEmpty());
        assertTrue(map.isSignAtPoint(point1));

        /* Place flag on the sign */
        map.placeFlag(player0, point1);

        /* Verify that the sign is gone and the flag exists */
        assertTrue(map.isFlagAtPoint(point1));
        assertFalse(map.isSignAtPoint(point1));
        assertTrue(map.getSigns().isEmpty());
    }

    @Test
    public void testMineCanBePlacedOnSign() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 12);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on point0 */
        Point point1 = new Point(8, 8);
        Utils.surroundPointWithMinableMountain(point1, map);

        /* Place sign */
        map.placeEmptySign(point1);

        assertFalse(map.getSigns().isEmpty());
        assertTrue(map.isSignAtPoint(point1));

        /* Build a mine on the sign */
        map.placeBuilding(new GoldMine(player0), point1);

        /* Verify that the sign is gone and the mine exists */
        assertTrue(map.isBuildingAtPoint(point1));
        assertFalse(map.isSignAtPoint(point1));
        assertTrue(map.getSigns().isEmpty());
    }

    @Test
    public void testPlaceHouseOnFullyDestroyedHouse() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
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

    @Test
    public void testCannotPlaceHouseIfFlagIsTooCloseToOtherFlag() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place flag */
        Point point0 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point0);

        /* Verify that a building cannot be place so that its flag is too close to the other flag */
        Point point1 = new Point(7, 11);

        try {
            Building woodcutter0 = map.placeBuilding(new Building(player0), point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testThatBuildingOtherThanHeadquarterCannotBeFirst() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();
        players.add(player0);
        players.add(player1);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point38 = new Point(5, 5);

        /* Verify that it's not possible to place a house on the headquarter */
        try {
            Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point38);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlaceRoadWithoutFlagAtTheEnd() throws Exception {

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
        Flag flag0 = map.placeFlag(player0, point1);

        /* Verify that it's not possible to place a road without a flag at the end */
        Point point2 = new Point(10, 6);
        Point point3 = new Point(12, 6);

        try {
            Road road0 = map.placeRoad(player0, point1, point2, point3);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCannotPlaceFlagOnFlag() throws Exception {

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
        Flag flag0 = map.placeFlag(player0, point1);

        /* Verify that it's not possible to place a flag on the existing flag */
        try {
            Flag flag1 = map.placeFlag(player0, point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailablePointForFlagTooCloseToBottomEdgeOfMap() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
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
        Player player0 = new Player("Player 0", BLUE);
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
        Player player0 = new Player("Player 0", BLUE);
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 60, 60);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place tree */
        Point point1 = new Point(8, 8);
        map.placeTree(point1, Tree.TreeType.PINE);

        /* Verify that there is no available space for a house on a tree */
        assertNull(map.isAvailableHousePoint(player0, point1));
        assertFalse(map.getAvailableHousePoints(player0).containsKey(point1));
    }

    @Test
    public void testNoAvailablePointTooCloseToExistingHouseDiagonally() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
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
        Player player0 = new Player("Player 0", BLUE);
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
            map.placeTree(point1, Tree.TreeType.PINE);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCannotBuildHouseTooCloseToBottomEdgeOfMap() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 10, 10);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to build a house too close to the bottom edge */
        Point point1 = new Point(5, 1);

        try {
            Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testThereIsAvailableFlagSpotForEachAvailableHouseSpot() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
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
        Player player0 = new Player("Player 0", BLUE);
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
        Player player0 = new Player("Player 0", BLUE);
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

            /* Remove the flag */
            map.removeFlag(flag0);
        }
    }

    @Test
    public void testAllAvailableHouseSpotsAreWithinBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
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
        Player player0 = new Player("Player 0", BLUE);
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
        Player player0 = new Player("Player 0", BLUE);
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
        Player player0 = new Player("Player 0", BLUE);
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
        Player player0 = new Player("Player 0", BLUE);
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
        Player player0 = new Player("Player 0", BLUE);
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
        assertEquals(map.getDetailedVegetationUpLeft(point0), WATER);
        assertEquals(map.getDetailedVegetationAbove(point0), WATER);
        assertEquals(map.getDetailedVegetationUpRight(point0), WATER);
        assertEquals(map.getDetailedVegetationDownRight(point0), WATER);
        assertEquals(map.getDetailedVegetationBelow(point0), WATER);
        assertEquals(map.getDetailedVegetationDownLeft(point0), WATER);

        /* Place headquarter for player0 */
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

        /* Place headquarter for player0 */
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

            assertFalse(map.isBuildingAtPoint(pair.getKey()));
        }
    }

    @Test
    public void testAvailableMineOnMountain() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 8);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on point0 */
        Point point1 = new Point(8, 8);
        Utils.surroundPointWithMinableMountain(point1, map);

        /* Verify that there is an available point for a mine on the mountain */
        assertTrue(map.isAvailableMinePoint(player0, point1));
        assertTrue(map.getAvailableMinePoints(player0).contains(point1));
    }

    @Test
    public void testNoAvailableHouseOnMountain() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on point0 */
        Point point1 = new Point(8, 8);
        Utils.surroundPointWithMinableMountain(point1, map);

        /* Verify that there is no available point for a house on the mountain */
        assertNull(map.isAvailableHousePoint(player0, point1));
        assertFalse(map.getAvailableHousePoints(player0).containsKey(point1));
    }

    @Test
    public void testNoAvailableMineOnGrass() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that there is no available mine on the grass */
        Point point1 = new Point(8, 8);
        assertFalse(map.isAvailableMinePoint(player0, point1));
        assertFalse(map.getAvailableMinePoints(player0).contains(point1));
    }

    @Test
    public void testNoAvailableMineOutsideBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on point0 */
        Point point1 = new Point(47, 47);
        Utils.surroundPointWithMinableMountain(point1, map);

        /* Verify that there is no available mine on the grass */
        assertFalse(map.isAvailableMinePoint(player0, point1));
        assertFalse(map.getAvailableMinePoints(player0).contains(point1));
    }

    @Test
    public void testNoAvailableMineOnExistingMine() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 12);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on point0 */
        Point point1 = new Point(8, 8);
        Utils.surroundPointWithMinableMountain(point1, map);

        /* Place a mine on the mountain */
        GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        /* Verify that there is no available point for a house on the mountain */
        assertFalse(map.isAvailableMinePoint(player0, point1));
        assertFalse(map.getAvailableMinePoints(player0).contains(point1));
    }

    @Test
    public void testNoAvailableMineOnFlag() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 8);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on point0 */
        Point point1 = new Point(8, 8);
        Utils.surroundPointWithMinableMountain(point1, map);

        /* Place a flag on the mountain */
        Flag flag0 = map.placeFlag(player0, point1);

        /* Verify that there is no available point for a house on the mountain */
        assertFalse(map.isAvailableMinePoint(player0, point1));
        assertFalse(map.getAvailableMinePoints(player0).contains(point1));
    }

    @Test
    public void testNoAvailableMineOnRoad() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 8);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on point0 */
        Point point1 = new Point(8, 8);
        Utils.surroundPointWithMinableMountain(point1, map);

        /* Place flags */
        Point point2 = new Point(6, 8);
        Flag flag0 = map.placeFlag(player0, point2);
        Point point3 = new Point(10, 8);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Place a road on the mountain */
        Road road0 = map.placeRoad(player0, point2, point1, point3);

        /* Verify that there is no available point for a house on the mountain */
        assertFalse(map.isAvailableMinePoint(player0, point1));
        assertFalse(map.getAvailableMinePoints(player0).contains(point1));
    }

    @Test
    public void testNoAvailableMineOnWater() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Put a lake on the map */
        Point point0 = new Point(8, 8);
        Utils.surroundPointWithWater(point0, map);

        /* Place headquarter */
        Point point1 = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Verify that there is no available mine on the grass */
        assertFalse(map.isAvailableMinePoint(player0, point0));
        assertFalse(map.getAvailableMinePoints(player0).contains(point0));
    }

    @Test
    public void testOnlyAvailableFlagPointsOnBorderBetweenMountainAndGrass() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on point0 */
        Point point1 = new Point(8, 8);
        Utils.surroundPointWithMinableMountain(point1, map);

        /* Verify that there are available flag points next to the mountain */
        List<Point> edgePoints = new LinkedList<>();
        edgePoints.add(point1.upRight());
        edgePoints.add(point1.right());
        edgePoints.add(point1.downRight());
        edgePoints.add(point1.downLeft());
        edgePoints.add(point1.left());
        edgePoints.add(point1.upLeft());

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 8);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a stone */
        Point point1 = new Point(8, 8);
        Stone stone0 = map.placeStone(point1);

        /* Verify that there are available flag points next to the mountain */
        List<Point> edgePoints = new LinkedList<>();
        edgePoints.add(point1.upRight());
        edgePoints.add(point1.right());
        edgePoints.add(point1.downRight());
        edgePoints.add(point1.downLeft());
        edgePoints.add(point1.left());
        edgePoints.add(point1.upLeft());

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
        Player player2 = new Player("Player 0", RED);
        Player player3 = new Player("Player 1", BLUE);
        List<Player> players = new LinkedList<>();
        players.add(player2);
        players.add(player3);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Create a small mountain */
        Point point0 = new Point(5, 13);
        Point point1 = new Point(8, 14);
        Point point2 = new Point(5, 15);
        Utils.surroundPointWithVegetation(point0, MOUNTAIN_1, map);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_1, map);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_1, map);

        /* Put gold at mountain */
        map.surroundPointWithMineral(point0, GOLD);
        map.surroundPointWithMineral(point1, GOLD);
        map.surroundPointWithMineral(point2, GOLD);

        /* Create a small mountain */
        Point point3 = new Point(8, 16);
        Point point4 = new Point(11, 17);
        Point point5 = new Point(8, 18);
        Utils.surroundPointWithVegetation(point3, MOUNTAIN_1, map);
        Utils.surroundPointWithVegetation(point4, MOUNTAIN_1, map);
        Utils.surroundPointWithVegetation(point5, MOUNTAIN_1, map);

        /* Put coal at mountain */
        map.surroundPointWithMineral(point3, COAL);
        map.surroundPointWithMineral(point4, COAL);
        map.surroundPointWithMineral(point5, COAL);

        /* Place headquarter for player2 */
        Point point6 = new Point(8, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player2), point6);

        /* Place headquarter for player3 */
        Point point7 = new Point(45, 21);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player3), point7);

        /* Place barracks for player3 */
        Point point8 = new Point(29, 21);
        Barracks barracks0 = map.placeBuilding(new Barracks(player3), point8);

        /* Place road between (30, 20) and (46, 20) */
        Point point9 = new Point(30, 20);
        Point point10 = new Point(32, 20);
        Point point11 = new Point(34, 20);
        Point point12 = new Point(36, 20);
        Point point13 = new Point(38, 20);
        Point point14 = new Point(40, 20);
        Point point15 = new Point(42, 20);
        Point point16 = new Point(44, 20);
        Point point17 = new Point(46, 20);
        Road road0 = map.placeRoad(player3, point9, point10, point11, point12, point13, point14, point15, point16, point17);

        /* Place flag */
        Point point18 = new Point(5, 11);
        Flag flag0 = map.placeFlag(player2, point18);

        /* Place road between (5, 11) and (9, 9) */
        Point point19 = new Point(6, 10);
        Point point20 = new Point(7, 9);
        Point point21 = new Point(9, 9);
        Road road1 = map.placeRoad(player2, point18, point19, point20, point21);

        /* Place mine */
        Point point22 = new Point(5, 13);

        assertFalse(map.isAvailableMinePoint(player2, point22));
    }

    @Test
    public void testSizeComparisons() {

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
        Player player0 = new Player("Player 0", RED);
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
        Player player0 = new Player("Player 0", RED);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place tree */
        Point point1 = new Point(5, 5);
        Tree tree0 = map.placeTree(point1, Tree.TreeType.PINE);

        /* Place tree */
        Point point2 = new Point(13, 5);
        Tree tree1 = map.placeTree(point2, Tree.TreeType.PINE);

        /* Verify that the available buildings between the trees are small house, castle, small house */
        assertEquals(map.isAvailableHousePoint(player0, point1.right()), SMALL);
        assertEquals(map.isAvailableHousePoint(player0, point1.right().right()), LARGE);
        assertEquals(map.isAvailableHousePoint(player0, point1.right().right().right()), SMALL);
    }

    @Test
    public void testAvailableSmallHousesDiagonallyFromTree() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", RED);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place tree */
        Point point1 = new Point(7, 15);
        Tree tree0 = map.placeTree(point1, Tree.TreeType.PINE);

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
        Player player0 = new Player("Player 0", RED);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(20, 20);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place terrain that can only handle roads and flags - no buildings */
        Point point1 = new Point(15, 15);
        Utils.surroundPointWithVegetation(point1, DESERT_1, map);
        Utils.surroundPointWithVegetation(point1.right(), DESERT_1, map);
        Utils.surroundPointWithVegetation(point1.right().right(), DESERT_1, map);

        /* Verify that it's possible to build a large house close to the vegetation */
        assertEquals(map.isAvailableHousePoint(player0, point1.right().down()), LARGE);
    }
}
