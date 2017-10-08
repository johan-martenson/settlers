/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.Fishery;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GoldMine;
import org.appland.settlers.model.Headquarter;

import static org.appland.settlers.model.Tile.Vegetation.WATER;
import static org.junit.Assert.*;

import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Terrain;

import org.appland.settlers.model.Tile;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestPlacementInTerrain {

    // TODO: test placement on borders between vegetation areas

    @Test
    public void testPathCloseToLake() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place lake with this form:
        
        _____
        W W  \______
             W  W  W
        
        */
        Point pointX = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(7, 3);
        Point point3 = new Point(9, 3);
        Point point4 = new Point(11, 3);

        map.surroundPointWithWater(pointX);
        map.surroundPointWithWater(point1);
        map.surroundPointWithWater(point2);
        map.surroundPointWithWater(point3);
        map.surroundPointWithWater(point4);

        /* Place headquarter */
        Point point0 = new Point(6, 8);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place fisheries like this:
      Fishery
        \         Fishery
        Flag_       \
        W W  \______Flag
             W  W  W
        
        */
        Point point5 = new Point(4, 6);
        Point point6 = new Point(9, 5);
        Fishery fishery0 = map.placeBuilding(new Fishery(player0), point5);
        Fishery fishery1 = map.placeBuilding(new Fishery(player0), point6);

        /* Verify that it's possible to build a road between the flags that follows
           the edge of the lake
        */
        Point point7 = new Point(6, 4);
        Point point8 = new Point(8, 4);

        Terrain terrain = map.getTerrain();

        assertEquals(terrain.getTile(fishery0.getFlag().getPosition(),
                fishery0.getFlag().getPosition().left(),
                fishery0.getFlag().getPosition().downLeft()).getVegetationType(),
                WATER);
        assertEquals(terrain.getTile(fishery0.getFlag().getPosition(), fishery0.getFlag().getPosition().downRight(), fishery0.getFlag().getPosition().downLeft()).getVegetationType(), WATER);

        assertEquals(terrain.getTile(point7, point7.left(), point7.downLeft()).getVegetationType(), WATER);
        assertEquals(terrain.getTile(point7, point7.downRight(), point7.downLeft()).getVegetationType(), WATER);
        assertEquals(terrain.getTile(point7, point7.right(), point7.downRight()).getVegetationType(), WATER);

        assertFalse(terrain.isOnGrass(fishery0.getFlag().getPosition()));
        assertFalse(terrain.isOnGrass(fishery1.getFlag().getPosition()));

        /* First, define the road like a player would */
        assertTrue(
                map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0,
                        fishery0.getFlag().getPosition()).contains(point7)
        );

        assertTrue(
                map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0,
                        point7).contains(point8)
        );

        assertTrue(
                map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0,
                        point8).contains(fishery1.getFlag().getPosition())
        );

        /* Then place the road */
        Road road0 = map.placeRoad(player0, fishery0.getFlag().getPosition(), point7, fishery1.getFlag().getPosition());

        assertTrue(map.arePointsConnectedByRoads(point5, point6));
    }

    @Test
    public void testAvailableFlagInDesert() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small desert on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.DESERT, map);

        /* Verify that there is an available flag point in the desert */
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagInDesert() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small desert on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.DESERT, map);

        /* Verify that it is possible to place a flag in the desert */
        map.placeFlag(player0, point1);
    }

    @Test
    public void testNoAvailableHouseInDesert() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small desert on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.DESERT, map);

        /* Verify that there is no available house point in the desert */
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceHouseInDesert() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small desert on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.DESERT, map);

        /* Verify that it's not possible to place a house in the desert */
        try {
            map.placeBuilding(new Woodcutter(player0), point1);
            assertTrue(false);
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableMineInDesert() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small desert on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.DESERT, map);

        /* Verify that there is no available house point in the desert */
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineInDesert() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small desert on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.DESERT, map);

        /* Verify that it's not possible to place a house in the desert */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            assertTrue(false);
        } catch (Exception e) {}
    }

    @Test
    public void testCanBuildRoadAcrossDesert() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small desert on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.DESERT, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's possible to Build a road across the desert */
        Road road0 = map.placeRoad(player0, point2, point1, point3);
    }

    @Test
    public void testNoAvailableHouseOnBorderOfDesert() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small desert on the map */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.DESERT, map);
        Utils.surroundPointWithVegetation(point2, Tile.Vegetation.DESERT, map);

        /* Verify that there is no available house point on the border of the desert */
        Point point3 = new Point(11, 11);
        assertNull(map.isAvailableHousePoint(player0, point3));
    }

    @Test
    public void testCannotPlaceHouseOnBorderOfDesert() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small desert on the map */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.DESERT, map);
        Utils.surroundPointWithVegetation(point2, Tile.Vegetation.DESERT, map);

        /* Verify that it's not possible to place a house on the border of the desert */
        Point point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);
            assertTrue(false);
        } catch (Exception e) {}
    }
// Snow
// Also test: -build road next to snow. Is that OK?

    @Test
    public void testNoAvailableFlagOnSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of snow on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SNOW, map);

        /* Verify that there is no available flag point in the desert */
        assertFalse(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCannotPlaceFlagOnSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of snow on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SNOW, map);

        /* Verify that it is not possible to place a flag on the snow */
        try {
            map.placeFlag(player0, point1);
            assertTrue(false);
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableHouseOnSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of snow on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SNOW, map);

        /* Verify that there is no available house point on the snow */
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceHouseOnSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of snow on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SNOW, map);

        /* Verify that it's not possible to place a house on the snow */
        try {
            map.placeBuilding(new Woodcutter(player0), point1);
            assertTrue(false);
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableMineOnSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of snow on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SNOW, map);

        /* Verify that there is no available house point on the snow */
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of snow on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SNOW, map);

        /* Verify that it's not possible to place a house on the snow */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            assertTrue(false);
        } catch (Exception e) {}
    }

    @Test
    public void testCannotBuildRoadAcrossSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of snow on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SNOW, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's not possible to Build a road across the snow */
        try {
            Road road0 = map.placeRoad(player0, point2, point1, point3);
            assertTrue(false);
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableHouseOnBorderOfSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of snow on the map */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SNOW, map);
        Utils.surroundPointWithVegetation(point2, Tile.Vegetation.SNOW, map);

        /* Verify that there is no available house point on the border of the snow */
        Point point3 = new Point(11, 11);
        assertNull(map.isAvailableHousePoint(player0, point3));
    }

    @Test
    public void testCannotPlaceHouseOnBorderOfSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of snow on the map */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SNOW, map);
        Utils.surroundPointWithVegetation(point2, Tile.Vegetation.SNOW, map);

        /* Verify that it's not possible to place a house on the border of the snow */
        Point point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);
            assertTrue(false);
        } catch (Exception e) {}
    }

    // Grass (meadow)

    @Test
    public void testAvailableFlagOnGrass() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of grass on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.GRASS, map);

        /* Verify that there is an available flag point on the snow */
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagOnGrass() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of grass on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.GRASS, map);

        /* Verify that it is  possible to place a flag on the grass */
        map.placeFlag(player0, point1);
    }

    @Test
    public void testAvailableHouseOnGrass() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of grass on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.GRASS, map);

        /* Verify that there is an available house point on the grass */
        assertNotNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCanPlaceHouseOnGrass() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of grass on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.GRASS, map);

        /* Verify that it's possible to place a house on the grass */
        map.placeBuilding(new Woodcutter(player0), point1);
    }

    @Test
    public void testNoAvailableMineOnGrass() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of grass on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.GRASS, map);

        /* Verify that there is no available house point on the grass */
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnGrass() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of grass on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.GRASS, map);

        /* Verify that it's not possible to place a house on the grass */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            assertTrue(false);
        } catch (Exception e) {}
    }

    @Test
    public void testCanBuildRoadAcrossGrass() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of grass on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.GRASS, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's possible to build a road across the grass */
        Road road0 = map.placeRoad(player0, point2, point1, point3);
    }

    // Savannah

    @Test
    public void testAvailableFlagOnSavannah() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of savannah on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SAVANNAH, map);

        /* Verify that there is an available flag point on the savannah */
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagOnSavannah() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of savannah on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SAVANNAH, map);

        /* Verify that it is  possible to place a flag on the savannah */
        map.placeFlag(player0, point1);
    }

    @Test
    public void testAvailableHouseOnSavannah() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of savannah on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SAVANNAH, map);

        /* Verify that there is an available house point on the savannah */
        assertNotNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCanPlaceHouseOnSavannah() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of savannah on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SAVANNAH, map);

        /* Verify that it's possible to place a house on the savannah */
        map.placeBuilding(new Woodcutter(player0), point1);
    }

    @Test
    public void testNoAvailableMineOnSavannah() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of savannah on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SAVANNAH, map);

        /* Verify that there is no available house point on the savannah */
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnSavannah() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of savannah on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SAVANNAH, map);

        /* Verify that it's not possible to place a house on the savannah */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            assertTrue(false);
        } catch (Exception e) {}
    }

    @Test
    public void testCanBuildRoadAcrossSavannah() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of savannah on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SAVANNAH, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's possible to build a road across the savannah */
        Road road0 = map.placeRoad(player0, point2, point1, point3);
    }

// Shallow water (buildable water)

    @Test
    public void testAvailableFlagOnBuildableWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of buildable water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SHALLOW_WATER, map);

        /* Verify that there is an available flag point on the buildable water */
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagOnBuildableWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of buildable water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SHALLOW_WATER, map);

        /* Verify that it is  possible to place a flag on the buildable water */
        map.placeFlag(player0, point1);
    }

    @Test
    public void testAvailableHouseOnBuildableWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of buildable water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SHALLOW_WATER, map);

        /* Verify that there is an available house point on the buildable */
        assertNotNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCanPlaceHouseOnBuildableWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of buildable water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SHALLOW_WATER, map);

        /* Verify that it's possible to place a house on the buildable water */
        map.placeBuilding(new Woodcutter(player0), point1);
    }

    @Test
    public void testNoAvailableMineOnBuildableWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of buildable water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SHALLOW_WATER, map);

        /* Verify that there is no available house point on the buildable water */
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnBuildableWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of buildable water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SHALLOW_WATER, map);

        /* Verify that it's not possible to place a house on the buildable water */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            assertTrue(false);
        } catch (Exception e) {}
    }

    @Test
    public void testCanBuildRoadAcrossBuildableWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of buildable water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.SHALLOW_WATER, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's possible to build a road across the buildable water */
        Road road0 = map.placeRoad(player0, point2, point1, point3);
    }

    // Steppe

    @Test
    public void testAvailableFlagOnSteppe() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of steppe on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.STEPPE, map);

        /* Verify that there is an available flag point on the snow */
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagOnSteppe() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of steppe on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.STEPPE, map);

        /* Verify that it is  possible to place a flag on the steppe */
        map.placeFlag(player0, point1);
    }

    @Test
    public void testAvailableHouseOnSteppe() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of steppe on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.STEPPE, map);

        /* Verify that there is an available house point on the steppe */
        assertNotNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCanPlaceHouseOnSteppe() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of steppe on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.STEPPE, map);

        /* Verify that it's possible to place a house on the steppe */
        map.placeBuilding(new Woodcutter(player0), point1);
    }

    @Test
    public void testNoAvailableMineOnSteppe() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of steppe on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.STEPPE, map);

        /* Verify that there is no available house point on the steppe */
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnSteppe() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of steppe on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.STEPPE, map);

        /* Verify that it's not possible to place a house on the steppe */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            assertTrue(false);
        } catch (Exception e) {}
    }

    @Test
    public void testCanBuildRoadAcrossSteppe() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of steppe on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.STEPPE, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's possible to build a road across the steppe */
        Road road0 = map.placeRoad(player0, point2, point1, point3);
    }

// Mountain meadow

    @Test
    public void testAvailableFlagOnMountainMeadow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of mountain meadow on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.MOUNTAIN_MEADOW, map);

        /* Verify that there is an available flag point on the snow */
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagOnMountainMeadow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of mountain meadow on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.MOUNTAIN_MEADOW, map);

        /* Verify that it is  possible to place a flag on the mountain meadow */
        map.placeFlag(player0, point1);
    }

    @Test
    public void testAvailableHouseOnMountainMeadow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of mountain meadow on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.MOUNTAIN_MEADOW, map);

        /* Verify that there is an available house point on the mountain meadow */
        assertNotNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCanPlaceHouseOnMountainMeadow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of mountain meadow on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.MOUNTAIN_MEADOW, map);

        /* Verify that it's possible to place a house on the mountain meadow */
        map.placeBuilding(new Woodcutter(player0), point1);
    }

    @Test
    public void testNoAvailableMineOnMountainMeadow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of mountain meadow on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.MOUNTAIN_MEADOW, map);

        /* Verify that there is no available house point on the mountain meadow */
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnMountainMeadow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of mountain meadow on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.MOUNTAIN_MEADOW, map);

        /* Verify that it's not possible to place a house on the mountain meadow */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            assertTrue(false);
        } catch (Exception e) {}
    }

    @Test
    public void testCanBuildRoadAcrossMountainMeadow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of mountain meadow on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.MOUNTAIN_MEADOW, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's possible to build a road across the mountain meadow */
        Road road0 = map.placeRoad(player0, point2, point1, point3);
    }

    // Buildable mountain

    @Test
    public void testAvailableFlagOnBuildableMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of buildable mountain on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.BUILDABLE_MOUNTAIN, map);

        /* Verify that there is an available flag point on the snow */
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagOnBuildableMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of buildable mountain on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.BUILDABLE_MOUNTAIN, map);

        /* Verify that it is  possible to place a flag on the buildable mountain */
        map.placeFlag(player0, point1);
    }

    @Test
    public void testAvailableHouseOnBuildableMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of buildable mountain on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.BUILDABLE_MOUNTAIN, map);

        /* Verify that there is an available house point on the buildable mountain */
        assertNotNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCanPlaceHouseOnBuildableMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of buildable mountain on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.BUILDABLE_MOUNTAIN, map);

        /* Verify that it's possible to place a house on the buildable mountain */
        map.placeBuilding(new Woodcutter(player0), point1);
    }

    @Test
    public void testNoAvailableMineOnBuildableMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of buildable mountain on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.BUILDABLE_MOUNTAIN, map);

        /* Verify that there is no available house point on the buildable mountain */
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnBuildableMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of buildable mountain on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.BUILDABLE_MOUNTAIN, map);

        /* Verify that it's not possible to place a house on the buildable mountain */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            assertTrue(false);
        } catch (Exception e) {}
    }

    @Test
    public void testCanBuildRoadAcrossBuildableMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of buildable mountain on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, Tile.Vegetation.BUILDABLE_MOUNTAIN, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's possible to build a road across the buildable mountain */
        Road road0 = map.placeRoad(player0, point2, point1, point3);
    }

}
