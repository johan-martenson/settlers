/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import static java.awt.Color.GREEN;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.MEDIUM;
import org.appland.settlers.model.Tile;
import org.appland.settlers.model.Tile.Vegetation;
import static org.appland.settlers.model.Tile.Vegetation.GRASS;
import static org.appland.settlers.model.Tile.Vegetation.MOUNTAIN;
import static org.appland.settlers.model.Tile.Vegetation.WATER;
import org.appland.settlers.model.Woodcutter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestPlacement {

    @Test
    public void testDefaultMapIsEmpty() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);
    
        assertTrue(map.getBuildings().isEmpty());
        assertTrue(map.getRoads().isEmpty());
        assertTrue(map.getStones().isEmpty());
        assertTrue(map.getTrees().isEmpty());
        assertTrue(map.getFlags().isEmpty());
    
        // TODO: verify all placable objects in map
    }
    
    @Test
    public void testEmptyMapHasNoBorders() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);
    
        assertEquals(player0.getBorders().size(), 0);
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
        List<Point> flagPoints = map.getAvailableFlagPoints(player0);

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
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        List<Point> flagPoints = map.getAvailableFlagPoints(player0);

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

        /* Points on right, sampled*/
        assertFalse(possibleHouses.containsKey(point1.upRight()));
        assertFalse(possibleHouses.containsKey(point1.left()));
    }
    
    @Test
    public void testNoAvailableFlagOnLake() throws Exception {
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

        /* Verify that there is no available spot for a flag on the lake */
        List<Point> possibleFlags = map.getAvailableFlagPoints(player0);
        
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
        List<Point> possibleFlags = map.getAvailableFlagPoints(player0);
        
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
        List<Point> possibleFlags = map.getAvailableFlagPoints(player0);
        
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
        List<Point> possibleFlags = map.getAvailableFlagPoints(player0);
        
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
        List<Point> possibleFlagPoints = map.getAvailableFlagPoints(player0);
        
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
        
        Woodcutter wc = new Woodcutter(player0);
        Point wcPoint = new Point(6, 4);
        
        map.placeBuilding(wc, wcPoint);
        
        List<Point> possibleFlagPoints = map.getAvailableFlagPoints(player0);
        
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
        List<Point> possibleFlagPoints = map.getAvailableFlagPoints(player0);

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
    public void testAvailableHousesNextToLargeHouse() throws Exception {

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

        /* Verify that the available house points next to the farm are correct */
        Map<Point, Size> possibleHouses = map.getAvailableHousePoints(player0);

        /* The house's own point */
        assertFalse(possibleHouses.containsKey(point0));

        /* More space under the house */
        assertFalse(possibleHouses.containsKey(point0.upLeft()));
        assertFalse(possibleHouses.containsKey(point0.downLeft()));
        assertFalse(possibleHouses.containsKey(point0.left()));

        /* The house's flag */
        assertFalse(possibleHouses.containsKey(point0.downRight()));

        /* Points in front, TBD sampled */ 
        // assertFalse(possibleFlagPoints.contains(new Point(9, 1)));
        // assertFalse(possibleFlagPoints.contains(new Point(10, 2)));
        assertFalse(possibleHouses.containsKey(point0.down()));
        assertFalse(possibleHouses.containsKey(point0.right()));

        /* Points on left, sampled */
        assertFalse(possibleHouses.containsKey(point0.left().down()));
        assertEquals(possibleHouses.get(point0.left().downLeft()), MEDIUM);
        assertEquals(possibleHouses.get(point0.left().left()), MEDIUM);

        /* Points on top, sampled */
        assertFalse(possibleHouses.containsKey(point0.left().upLeft()));
        assertFalse(possibleHouses.containsKey(point0.left().up()));
        assertEquals(possibleHouses.get(point0.up().upLeft()), MEDIUM);

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
        List<Point> possibleFlags = map.getAvailableFlagPoints(player0);

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
        
        Woodcutter wc = new Woodcutter(player0);
        Point wcPoint = new Point(6, 4);
        
        map.placeBuilding(wc, wcPoint);

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
        
        Woodcutter wc = new Woodcutter(player0);
        Point wcPoint = new Point(6, 4);
        Point point0  = new Point(6, 4);
        
        map.placeFlag(player0, point0);

        map.placeBuilding(wc, wcPoint);
    }
    
    @Test(expected=Exception.class)
    public void testPlaceHouseOnHouse() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Woodcutter wc  = new Woodcutter(player0);
        Quarry     qry = new Quarry(player0);
        Point wcPoint  = new Point(6, 4);
        Point qryPoint = new Point(5, 5);
        
        map.placeBuilding(wc, wcPoint);
        map.placeBuilding(qry, qryPoint);
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
        Tile waterTile = map.getTerrain().getTile(sharedPoint1, sharedPoint2, waterPoint);
        Tile grassTile = map.getTerrain().getTile(sharedPoint1, sharedPoint2, grassPoint);
        
        waterTile.setVegetationType(WATER);
        grassTile.setVegetationType(GRASS);

        List<Point> possibleFlags = map.getAvailableFlagPoints(player0);
        
        assertTrue(possibleFlags.contains(sharedPoint1));
        assertTrue(possibleFlags.contains(sharedPoint2));
        assertTrue(possibleFlags.contains(grassPoint));
    }

    @Test
    public void testCanNotPlaceFlagInWater() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 6, 6);
        Point borderPoint1 = new Point(1, 1);
        Point borderPoint2 = new Point(2, 0);
        Point borderPoint3 = new Point(4, 0);
        Point borderPoint4 = new Point(5, 1);
        Point borderPoint5 = new Point(4, 2);
        Point borderPoint6 = new Point(2, 2);
        Point centerPoint  = new Point(3, 1);
        
        Tile waterTile1 = map.getTerrain().getTile(borderPoint1, borderPoint2, centerPoint);
        Tile waterTile2 = map.getTerrain().getTile(borderPoint2, borderPoint3, centerPoint);
        Tile waterTile3 = map.getTerrain().getTile(borderPoint3, borderPoint4, centerPoint);
        Tile waterTile4 = map.getTerrain().getTile(borderPoint4, borderPoint5, centerPoint);
        Tile waterTile5 = map.getTerrain().getTile(borderPoint5, borderPoint6, centerPoint);
        Tile waterTile6 = map.getTerrain().getTile(borderPoint6, borderPoint1, centerPoint);
        
        waterTile1.setVegetationType(WATER);
        waterTile2.setVegetationType(WATER);
        waterTile3.setVegetationType(WATER);
        waterTile4.setVegetationType(WATER);
        waterTile5.setVegetationType(WATER);
        waterTile6.setVegetationType(WATER);

        List<Point> possibleFlags = map.getAvailableFlagPoints(player0);
        
        assertFalse(possibleFlags.contains(centerPoint));
    }    

    @Test
    public void testDifferentPointOrderGivesSameTile() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);
        Point point1 = new Point(3, 1);
        Point point2 = new Point(4, 2);
        Point point3 = new Point(5, 1);
        
        Tile tile1 = map.getTerrain().getTile(point1, point2, point3);
        Tile tile2 = map.getTerrain().getTile(point1, point3, point2);
        Tile tile3 = map.getTerrain().getTile(point2, point1, point3);
        Tile tile4 = map.getTerrain().getTile(point2, point3, point1);
        Tile tile5 = map.getTerrain().getTile(point3, point1, point2);
        Tile tile6 = map.getTerrain().getTile(point3, point2, point1);
    
        assertEquals(tile1, tile2);
        assertEquals(tile2, tile3);
        assertEquals(tile3, tile4);
        assertEquals(tile4, tile5);
        assertEquals(tile5, tile6);
    }

    @Test
    public void testDifferentiateBetweenCloseTiles() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);
        Point bottom1 = new Point(1, 1);
        Point bottom2 = new Point(3, 1);
        Point middle  = new Point(2, 2);        
        Point top1    = new Point(1, 3);
        Point top2    = new Point(3, 3);
    
        Tile t1 = map.getTerrain().getTile(bottom1, bottom2, middle);
        Tile t2 = map.getTerrain().getTile(top1, top2, middle);
        
        assertFalse(t1.equals(t2));
    }

    @Test
    public void testSetTileToMountainTerrain() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);
        Point left  = new Point(1, 1);
        Point top   = new Point(2, 2);        
        Point right = new Point(3, 1);
    
        Tile t1 = map.getTerrain().getTile(left, right, top);

        t1.setVegetationType(MOUNTAIN);

        t1 = map.getTerrain().getTile(left, right, top);
        
        assertEquals(t1.getVegetationType(), MOUNTAIN);
    }

    @Test
    public void testTreeCannotBePlacedOnStone() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);
        Point point0  = new Point(3, 3);
    
        map.placeStone(point0);
        
        try {
            map.placeTree(point0);
            assertFalse(true);
        } catch (Exception e) {}

        assertTrue(map.getTrees().isEmpty());
    }

    @Test
    public void testTreeCannotBePlacedOnMountain() throws Exception {

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

        /* Verify that it's not possible to place a tree on the mountain */
        try {
            map.placeTree(point0);
            assertFalse(true);
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
            assertFalse(true);
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
            assertFalse(true);
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
            assertFalse(true);
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

        /* Place a water tile */
        Point point0 = new Point(10, 4);
        Point point1 = new Point(8, 4);
        Point point2 = new Point(9, 5);
        map.getTerrain().getTile(point0, point1, point2).setVegetationType(Vegetation.WATER);

        /* Place a water tile */
        Point point3 = new Point(11, 5);
        map.getTerrain().getTile(point0, point2, point3).setVegetationType(Vegetation.WATER);

        /* Place a water tile */
        Point point4 = new Point(12, 4);
        map.getTerrain().getTile(point0, point3, point4).setVegetationType(Vegetation.WATER);

        /* Place a water tile */
        Point point5 = new Point(11, 3);
        map.getTerrain().getTile(point0, point4, point5).setVegetationType(Vegetation.WATER);

        /* Place a water tile */
        Point point6 = new Point(9, 3);
        map.getTerrain().getTile(point0, point5, point6).setVegetationType(Vegetation.WATER);

        /* Place a water tile */
        map.getTerrain().getTile(point0, point6, point1).setVegetationType(Vegetation.WATER);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing flag */
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing flag */
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
        Point point1 = new Point(8, 4);
        Point point2 = new Point(9, 5);
        map.getTerrain().getTile(point0, point1, point2).setVegetationType(Vegetation.WATER);

        /* Place a water tile */
        Point point3 = new Point(11, 5);
        map.getTerrain().getTile(point0, point2, point3).setVegetationType(Vegetation.WATER);

        /* Place a water tile */
        Point point4 = new Point(12, 4);
        map.getTerrain().getTile(point0, point3, point4).setVegetationType(Vegetation.WATER);

        /* Place a water tile */
        Point point5 = new Point(11, 3);
        map.getTerrain().getTile(point0, point4, point5).setVegetationType(Vegetation.WATER);

        /* Place a water tile */
        Point point6 = new Point(9, 3);
        map.getTerrain().getTile(point0, point5, point6).setVegetationType(Vegetation.WATER);

        /* Place a water tile */
        map.getTerrain().getTile(point0, point6, point1).setVegetationType(Vegetation.WATER);

        /* Placing headquarter */
        Point point21 = new Point(10, 10);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing flag */
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing flag */
        Flag flag1 = map.placeFlag(player0, point4);

        /* Verify that suggested connections from flag0 don't include a point in the water */
        assertFalse(map.getPossibleAdjacentRoadConnections(player0, point1, point4).contains(point1.right()));

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
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(8, 6);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);
        
        /* Finish the woodcutter */
        Utils.constructHouse(woodcutter0, map);

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
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

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
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

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
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

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
        assertFalse(map.getAvailableHousePoints(player0).keySet().contains(point1));
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
            Utils.waitForBuildingToDisappear(map, woodcutter0);

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
        List<Point> availableFlagPoints = map.getAvailableFlagPoints(player0);

        for (Collection<Point> border : player0.getBorders()) {
            for (Point borderPoint : border) {
                assertFalse(availableFlagPoints.contains(borderPoint));
            }
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

        for (Collection<Point> border : player0.getBorders()) {
            for (Point borderPoint : border) {
                assertFalse(availableHousePoints.contains(borderPoint));
            }
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
        for (Collection<Point> border : player0.getBorders()) {
            for (Point borderPoint : border) {

                try {
                    Flag flag0 = map.placeFlag(player0, borderPoint);

                    assertFalse(true);
                } catch (Exception e) {}

                assertFalse(map.isFlagAtPoint(borderPoint));

                try {
                    map.getFlagAtPoint(borderPoint);
                    assertFalse(true);
                } catch (Exception e) {}
            }
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
        for (Collection<Point> border : player0.getBorders()) {
            for (Point borderPoint : border) {

                try {
                    Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), borderPoint);

                    assertFalse(true);
                } catch (Exception e) {}

                assertFalse(map.isBuildingAtPoint(borderPoint));
                assertNull(map.getBuildingAtPoint(borderPoint));
            }
        }
    }
}
