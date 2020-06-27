/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Fishery;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GoldMine;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Hunter;
import org.appland.settlers.model.HunterHut;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.Terrain;
import org.appland.settlers.model.WildAnimal;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.WoodcutterWorker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Vegetation.BUILDABLE_MOUNTAIN;
import static org.appland.settlers.model.Vegetation.DEEP_WATER;
import static org.appland.settlers.model.Vegetation.DESERT;
import static org.appland.settlers.model.Vegetation.GRASS;
import static org.appland.settlers.model.Vegetation.LAVA;
import static org.appland.settlers.model.Vegetation.MAGENTA;
import static org.appland.settlers.model.Vegetation.MOUNTAIN;
import static org.appland.settlers.model.Vegetation.MOUNTAIN_MEADOW;
import static org.appland.settlers.model.Vegetation.SAVANNAH;
import static org.appland.settlers.model.Vegetation.SHALLOW_WATER;
import static org.appland.settlers.model.Vegetation.SNOW;
import static org.appland.settlers.model.Vegetation.STEPPE;
import static org.appland.settlers.model.Vegetation.SWAMP;
import static org.appland.settlers.model.Vegetation.WATER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author johan
 */
public class TestPlacementInTerrain {

    // TODO: test placement on borders between vegetation areas

    @Test
    public void testPathCloseToLake() throws Exception, org.appland.settlers.model.InvalidUserActionException {

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

        Utils.surroundPointWithVegetation(pointX, WATER, map);
        Utils.surroundPointWithVegetation(point1, WATER, map);
        Utils.surroundPointWithVegetation(point2, WATER, map);
        Utils.surroundPointWithVegetation(point3, WATER, map);
        Utils.surroundPointWithVegetation(point4, WATER, map);

        /* Place headquarter */
        Point point0 = new Point(16, 6);
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

        assertTrue(map.isAvailableFlagPoint(player0, point5.downRight()));
        assertTrue(map.isAvailableFlagPoint(player0, point6.downRight()));
        assertNotNull(map.isAvailableHousePoint(player0, point5));
        assertNotNull(map.isAvailableHousePoint(player0, point6));

        Fishery fishery0 = map.placeBuilding(new Fishery(player0), point5);
        Fishery fishery1 = map.placeBuilding(new Fishery(player0), point6);

        /* Verify that it's possible to build a road between the flags that follows the edge of the lake */
        Point point7 = new Point(7, 5);
        Point point8 = new Point(8, 4);

        Terrain terrain = map.getTerrain();

        assertEquals(terrain.getTileDownLeft(fishery0.getFlag().getPosition()), WATER);
        assertEquals(terrain.getTileBelow(fishery0.getFlag().getPosition()), WATER);

        assertEquals(terrain.getTileDownLeft(point7), WATER);
        assertEquals(terrain.getTileBelow(point7), WATER);
        assertEquals(terrain.getTileDownRight(point7), GRASS);

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
        Road road0 = map.placeRoad(player0, fishery0.getFlag().getPosition(), point7, point8, fishery1.getFlag().getPosition());

        assertTrue(map.arePointsConnectedByRoads(point5, point6));
    }

    // Desert
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
        Utils.surroundPointWithVegetation(point1, DESERT, map);

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
        Utils.surroundPointWithVegetation(point1, DESERT, map);

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
        Utils.surroundPointWithVegetation(point1, DESERT, map);

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
        Utils.surroundPointWithVegetation(point1, DESERT, map);

        /* Verify that it's not possible to place a house in the desert */
        try {
            map.placeBuilding(new Woodcutter(player0), point1);
            fail();
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
        Utils.surroundPointWithVegetation(point1, DESERT, map);

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
        Utils.surroundPointWithVegetation(point1, DESERT, map);

        /* Verify that it's not possible to place a house in the desert */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            fail();
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
        Utils.surroundPointWithVegetation(point1, DESERT, map);

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
        Utils.surroundPointWithVegetation(point1, DESERT, map);
        Utils.surroundPointWithVegetation(point2, DESERT, map);

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
        Utils.surroundPointWithVegetation(point1, DESERT, map);
        Utils.surroundPointWithVegetation(point2, DESERT, map);

        /* Verify that it's not possible to place a house on the border of the desert */
        Point point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossDesert() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter hut on the map */
        Point point1 = new Point(15, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Surround the woodcutter hut with a desert */
        Point point2 = new Point(15, 7);
        Point point3 = new Point(18, 6);
        Point point4 = new Point(18, 4);
        Point point5 = new Point(15, 3);
        Point point6 = new Point(12, 4);
        Point point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, DESERT, map);
        Utils.surroundPointWithVegetation(point3, DESERT, map);
        Utils.surroundPointWithVegetation(point4, DESERT, map);
        Utils.surroundPointWithVegetation(point5, DESERT, map);
        Utils.surroundPointWithVegetation(point6, DESERT, map);
        Utils.surroundPointWithVegetation(point7, DESERT, map);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter hut */
        WoodcutterWorker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Tear down the woodcutter hut */
        woodcutter0.tearDown();

        /* Verify that the woodcutter worker can go back to the headquarter */
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoDesert() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a desert on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, DESERT, map);

        /* Place hunter hut */
        Point point1 = new Point(8, 6);
        Building hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        /* Finish construction of the hunter hut */
        Utils.constructHouse(hunter0);

        /* Occupy the hunter hut */
        Hunter hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        /* Place wild animal */
        WildAnimal animal0 = map.placeWildAnimal(point2);

        /* Verify that the worker can reach the animal */
        assertTrue(animal0.isExactlyAtPoint());

        boolean meet = false;
        for (int i = 0; i < 1000; i++) {
            hunter0.getWorker().stepTime();

            if (animal0.isExactlyAtPoint() && hunter.getPosition().equals(animal0.getPosition())) {
                meet = true;

                break;
            }
        }

        assertTrue(meet);
    }

    @Test
    public void testCanPlaceFlagOnBorderOfDesertAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a desert on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, DESERT, map);

        /* Place snow next to the desert */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of desert and snow */
        Point point5 = new Point(5, 7);
        map.placeFlag(player0, point5);
    }

    @Test
    public void testFlagPointAvailableOnBorderOfDesertAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a desert on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, DESERT, map);

        /* Place snow next to the desert */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of desert and snow */
        Point point5 = new Point(5, 7);
        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCannotPlaceHeadquarterOnDesert() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a desert on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, DESERT, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, DESERT, map);
        }

        /* Verify that a headquarter cannot be placed in the desert */
        try {
            map.placeBuilding(new Headquarter(player0), point2);
            fail();
        } catch (Exception e) {
        }
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
        Utils.surroundPointWithVegetation(point1, SNOW, map);

        /* Verify that there is no available flag point in the snow */
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
        Utils.surroundPointWithVegetation(point1, SNOW, map);

        /* Verify that it is not possible to place a flag on the snow */
        try {
            map.placeFlag(player0, point1);
            fail();
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
        Utils.surroundPointWithVegetation(point1, SNOW, map);

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
        Utils.surroundPointWithVegetation(point1, SNOW, map);

        /* Verify that it's not possible to place a house on the snow */
        try {
            map.placeBuilding(new Woodcutter(player0), point1);
            fail();
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
        Utils.surroundPointWithVegetation(point1, SNOW, map);

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
        Utils.surroundPointWithVegetation(point1, SNOW, map);

        /* Verify that it's not possible to place a house on the snow */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            fail();
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
        Utils.surroundPointWithVegetation(point1, SNOW, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's not possible to Build a road across the snow */
        try {
            Road road0 = map.placeRoad(player0, point2, point1, point3);
            fail();
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
        Utils.surroundPointWithVegetation(point1, SNOW, map);
        Utils.surroundPointWithVegetation(point2, SNOW, map);

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
        Utils.surroundPointWithVegetation(point1, SNOW, map);
        Utils.surroundPointWithVegetation(point2, SNOW, map);

        /* Verify that it's not possible to place a house on the border of the snow */
        Point point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testWorkerCannotWalkOffroadAcrossSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter hut on the map */
        Point point1 = new Point(15, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Surround the woodcutter hut with water */
        Point point2 = new Point(15, 7);
        Point point3 = new Point(18, 6);
        Point point4 = new Point(18, 4);
        Point point5 = new Point(15, 3);
        Point point6 = new Point(12, 4);
        Point point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, SNOW, map);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);
        Utils.surroundPointWithVegetation(point5, SNOW, map);
        Utils.surroundPointWithVegetation(point6, SNOW, map);
        Utils.surroundPointWithVegetation(point7, SNOW, map);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter hut */
        WoodcutterWorker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Tear down the woodcutter hut */
        woodcutter0.tearDown();

        /* Verify that the woodcutter worker can't go back to the headquarter */
        assertEquals(map.getTerrain().getTileAbove(point6), SNOW);
        assertEquals(map.getTerrain().getTileBelow(point7), SNOW);
        assertNotEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        for (int i = 0; i < 1000; i++) {

            assertNotEquals(woodcutterWorker.getPosition(), headquarter0.getPosition());

            map.stepTime();
        }
    }

    @Test
    public void testWorkerCannotGoIntoSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a patch of snow on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SNOW, map);

        /* Place a wild animal */
        WildAnimal animal0 = map.placeWildAnimal(point2);

        /* Place hunter hut */
        Point point1 = new Point(8, 6);
        Building hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        /* Finish construction of the hunter hut */
        Utils.constructHouse(hunter0);

        /* Occupy the hunter hut */
        Hunter hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        /* Verify that the worker can't reach the animal */
        for (int i = 0; i < 1000; i++) {
            hunter0.getWorker().stepTime();

            assertNotEquals(hunter.getPosition(), animal0.getPosition());
        }
    }

    @Test
    public void testCannotPlaceFlagOnBorderOfSnowAndWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a lake on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        /* Place snow next to the lake */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's not possible to place a flag on the border of water and snow */
        Point point5 = new Point(5, 7);

        try {
            map.placeFlag(player0, point5);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoFlagPointAvailableOnBorderOfSnowAndWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a lake on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        /* Place snow next to the lake */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's not possible to place a flag on the border of water and snow */
        Point point5 = new Point(5, 7);
        assertFalse(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCannotPlaceHeadquarterOnSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place snow on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SNOW, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, SNOW, map);
        }

        /* Verify that a headquarter cannot be placed on the snow */
        try {
            map.placeBuilding(new Headquarter(player0), point2);
            fail();
        } catch (Exception e) {
        }
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
        Utils.surroundPointWithVegetation(point1, GRASS, map);

        /* Verify that there is an available flag point on the grass */
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
        Utils.surroundPointWithVegetation(point1, GRASS, map);

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
        Utils.surroundPointWithVegetation(point1, GRASS, map);

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
        Utils.surroundPointWithVegetation(point1, GRASS, map);

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
        Utils.surroundPointWithVegetation(point1, GRASS, map);

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
        Utils.surroundPointWithVegetation(point1, GRASS, map);

        /* Verify that it's not possible to place a house on the grass */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            fail();
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
        Utils.surroundPointWithVegetation(point1, GRASS, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's possible to build a road across the grass */
        Road road0 = map.placeRoad(player0, point2, point1, point3);
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossGrass() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter hut on the map */
        Point point1 = new Point(15, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Surround the woodcutter hut with grass */
        Point point2 = new Point(15, 7);
        Point point3 = new Point(18, 6);
        Point point4 = new Point(18, 4);
        Point point5 = new Point(15, 3);
        Point point6 = new Point(12, 4);
        Point point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, GRASS, map);
        Utils.surroundPointWithVegetation(point3, GRASS, map);
        Utils.surroundPointWithVegetation(point4, GRASS, map);
        Utils.surroundPointWithVegetation(point5, GRASS, map);
        Utils.surroundPointWithVegetation(point6, GRASS, map);
        Utils.surroundPointWithVegetation(point7, GRASS, map);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter hut */
        WoodcutterWorker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Tear down the woodcutter hut */
        woodcutter0.tearDown();

        /* Verify that the woodcutter worker can go back to the headquarter */
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoGrass() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a patch of grass on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, GRASS, map);

        /* Place hunter hut */
        Point point1 = new Point(8, 6);
        Building hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        /* Finish construction of the hunter hut */
        Utils.constructHouse(hunter0);

        /* Occupy the hunter hut */
        Hunter hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        /* Place wild animal */
        WildAnimal animal0 = map.placeWildAnimal(point2);

        /* Verify that the worker can reach the animal */
        assertTrue(animal0.isExactlyAtPoint());

        boolean meet = false;
        for (int i = 0; i < 1000; i++) {
            hunter0.getWorker().stepTime();

            if (animal0.isExactlyAtPoint() && hunter.getPosition().equals(animal0.getPosition())) {
                meet = true;

                break;
            }
        }

        assertTrue(meet);
    }

    @Test
    public void testCanPlaceFlagOnBorderOfGrassAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a patch of grass on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, GRASS, map);

        /* Place snow next to the grass */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of grass and snow */
        Point point5 = new Point(5, 7);
        map.placeFlag(player0, point5);
    }

    @Test
    public void testFlagPointAvailableOnBorderOfGrassAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a patch of grass on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, GRASS, map);

        /* Place snow next to the grass */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of grass and snow */
        Point point5 = new Point(5, 7);
        assertTrue(map.isAvailableFlagPoint(player0, point5));
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
        Utils.surroundPointWithVegetation(point1, SAVANNAH, map);

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
        Utils.surroundPointWithVegetation(point1, SAVANNAH, map);

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
        Utils.surroundPointWithVegetation(point1, SAVANNAH, map);

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
        Utils.surroundPointWithVegetation(point1, SAVANNAH, map);

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
        Utils.surroundPointWithVegetation(point1, SAVANNAH, map);

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
        Utils.surroundPointWithVegetation(point1, SAVANNAH, map);

        /* Verify that it's not possible to place a house on the savannah */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            fail();
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
        Utils.surroundPointWithVegetation(point1, SAVANNAH, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's possible to build a road across the savannah */
        Road road0 = map.placeRoad(player0, point2, point1, point3);
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossSavannah() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter hut on the map */
        Point point1 = new Point(15, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Surround the woodcutter hut with savannah */
        Point point2 = new Point(15, 7);
        Point point3 = new Point(18, 6);
        Point point4 = new Point(18, 4);
        Point point5 = new Point(15, 3);
        Point point6 = new Point(12, 4);
        Point point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, SAVANNAH, map);
        Utils.surroundPointWithVegetation(point3, SAVANNAH, map);
        Utils.surroundPointWithVegetation(point4, SAVANNAH, map);
        Utils.surroundPointWithVegetation(point5, SAVANNAH, map);
        Utils.surroundPointWithVegetation(point6, SAVANNAH, map);
        Utils.surroundPointWithVegetation(point7, SAVANNAH, map);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter hut */
        WoodcutterWorker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Tear down the woodcutter hut */
        woodcutter0.tearDown();

        /* Verify that the woodcutter worker can go back to the headquarter */
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoSavannah() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a savannah on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SAVANNAH, map);

        /* Place hunter hut */
        Point point1 = new Point(8, 6);
        Building hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        /* Finish construction of the hunter hut */
        Utils.constructHouse(hunter0);

        /* Occupy the hunter hut */
        Hunter hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        /* Place wild animal */
        WildAnimal animal0 = map.placeWildAnimal(point2);

        /* Verify that the worker can reach the animal */
        boolean meet = false;
        for (int i = 0; i < 1000; i++) {
            hunter0.getWorker().stepTime();

            if (animal0.isExactlyAtPoint() && hunter.getPosition().equals(animal0.getPosition())) {
                meet = true;

                break;
            }
        }

        assertTrue(meet);
    }

    @Test
    public void testCanPlaceFlagOnBorderOfSavannahAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a savannah on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SAVANNAH, map);

        /* Place snow next to the savannah */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of savannah and snow */
        Point point5 = new Point(5, 7);
        map.placeFlag(player0, point5);
    }

    @Test
    public void testFlagPointAvailableOnBorderOfSavannahAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a savannah on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SAVANNAH, map);

        /* Place snow next to the savannah */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of savannah and snow */
        Point point5 = new Point(5, 7);
        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCanPlaceHeadquarterOnSavannah() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place savannah on the map */
        Point point2 = new Point(8, 8);
        Utils.surroundPointWithVegetation(point2, SAVANNAH, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, SAVANNAH, map);
        }

        /* Verify that a headquarter can be placed on the savannah */
        map.placeBuilding(new Headquarter(player0), point2);
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
        Utils.surroundPointWithVegetation(point1, SHALLOW_WATER, map);

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
        Utils.surroundPointWithVegetation(point1, SHALLOW_WATER, map);

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
        Utils.surroundPointWithVegetation(point1, SHALLOW_WATER, map);

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
        Utils.surroundPointWithVegetation(point1, SHALLOW_WATER, map);

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
        Utils.surroundPointWithVegetation(point1, SHALLOW_WATER, map);

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
        Utils.surroundPointWithVegetation(point1, SHALLOW_WATER, map);

        /* Verify that it's not possible to place a house on the buildable water */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            fail();
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
        Utils.surroundPointWithVegetation(point1, SHALLOW_WATER, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's possible to build a road across the buildable water */
        Road road0 = map.placeRoad(player0, point2, point1, point3);
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossBuildableWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter hut on the map */
        Point point1 = new Point(15, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Surround the woodcutter hut with buildable water */
        Point point2 = new Point(15, 7);
        Point point3 = new Point(18, 6);
        Point point4 = new Point(18, 4);
        Point point5 = new Point(15, 3);
        Point point6 = new Point(12, 4);
        Point point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, SHALLOW_WATER, map);
        Utils.surroundPointWithVegetation(point3, SHALLOW_WATER, map);
        Utils.surroundPointWithVegetation(point4, SHALLOW_WATER, map);
        Utils.surroundPointWithVegetation(point5, SHALLOW_WATER, map);
        Utils.surroundPointWithVegetation(point6, SHALLOW_WATER, map);
        Utils.surroundPointWithVegetation(point7, SHALLOW_WATER, map);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter hut */
        WoodcutterWorker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Tear down the woodcutter hut */
        woodcutter0.tearDown();

        /* Verify that the woodcutter worker can go back to the headquarter */
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoBuildableWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a lake of buildable water on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SHALLOW_WATER, map);

        /* Place hunter hut */
        Point point1 = new Point(8, 6);
        Building hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        /* Finish construction of the hunter hut */
        Utils.constructHouse(hunter0);

        /* Occupy the hunter hut */
        Hunter hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        /* Place wild animal */
        WildAnimal animal0 = map.placeWildAnimal(point2);

        /* Verify that the worker can reach the animal */
        assertTrue(animal0.isExactlyAtPoint());

        boolean meet = false;
        for (int i = 0; i < 1000; i++) {
            hunter0.getWorker().stepTime();

            if (animal0.isExactlyAtPoint() && hunter.getPosition().equals(animal0.getPosition())) {
                meet = true;

                break;
            }
        }

        assertTrue(meet);
    }

    @Test
    public void testCanPlaceFlagOnBorderOfBuildableWaterAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a lake of buildable water on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SHALLOW_WATER, map);

        /* Place snow next to the lake */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of buildable water and snow */
        Point point5 = new Point(5, 7);
        map.placeFlag(player0, point5);
    }

    @Test
    public void testFlagPointAvailableOnBorderOfBuildableWaterAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a lake of buildable water on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SHALLOW_WATER, map);

        /* Place snow next to the lake */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of buildable water and snow */
        Point point5 = new Point(5, 7);
        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCanPlaceHeadquarterOnBuildableWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place buildable water on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SHALLOW_WATER, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, SHALLOW_WATER, map);
        }

        /* Verify that a headquarter can be placed on the buildable water */
        map.placeBuilding(new Headquarter(player0), point2);
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
        Utils.surroundPointWithVegetation(point1, STEPPE, map);

        /* Verify that there is an available flag point on the steppe */
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
        Utils.surroundPointWithVegetation(point1, STEPPE, map);

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
        Utils.surroundPointWithVegetation(point1, STEPPE, map);

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
        Utils.surroundPointWithVegetation(point1, STEPPE, map);

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
        Utils.surroundPointWithVegetation(point1, STEPPE, map);

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
        Utils.surroundPointWithVegetation(point1, STEPPE, map);

        /* Verify that it's not possible to place a house on the steppe */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            fail();
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
        Utils.surroundPointWithVegetation(point1, STEPPE, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's possible to build a road across the steppe */
        Road road0 = map.placeRoad(player0, point2, point1, point3);
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossSteppe() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter hut on the map */
        Point point1 = new Point(15, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Surround the woodcutter hut with steppe */
        Point point2 = new Point(15, 7);
        Point point3 = new Point(18, 6);
        Point point4 = new Point(18, 4);
        Point point5 = new Point(15, 3);
        Point point6 = new Point(12, 4);
        Point point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, STEPPE, map);
        Utils.surroundPointWithVegetation(point3, STEPPE, map);
        Utils.surroundPointWithVegetation(point4, STEPPE, map);
        Utils.surroundPointWithVegetation(point5, STEPPE, map);
        Utils.surroundPointWithVegetation(point6, STEPPE, map);
        Utils.surroundPointWithVegetation(point7, STEPPE, map);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter hut */
        WoodcutterWorker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Tear down the woodcutter hut */
        woodcutter0.tearDown();

        /* Verify that the woodcutter worker can go back to the headquarter */
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoSteppe() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a steppe on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, STEPPE, map);

        /* Place hunter hut */
        Point point1 = new Point(8, 6);
        Building hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        /* Finish construction of the hunter hut */
        Utils.constructHouse(hunter0);

        /* Occupy the hunter hut */
        Hunter hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        /* Place wild animal */
        WildAnimal animal0 = map.placeWildAnimal(point2);

        /* Verify that the worker can reach the animal */
        boolean meet = false;
        for (int i = 0; i < 1000; i++) {
            hunter0.getWorker().stepTime();

            if (animal0.isExactlyAtPoint() && hunter.getPosition().equals(animal0.getPosition())) {
                meet = true;

                break;
            }
        }

        assertTrue(meet);
    }

    @Test
    public void testCanPlaceFlagOnBorderOfSteppeAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a steppe on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, STEPPE, map);

        /* Place snow next to the steppe */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of steppe and snow */
        Point point5 = new Point(5, 7);
        map.placeFlag(player0, point5);
    }

    @Test
    public void testFlagPointAvailableOnBorderOfSteppeAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a steppe on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, STEPPE, map);

        /* Place snow next to the steppe */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of steppe and snow */
        Point point5 = new Point(5, 7);
        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCanPlaceHeadquarterOnSteppe() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place steppe on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, STEPPE, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, STEPPE, map);
        }

        /* Verify that a headquarter can be placed on the steppe */
        map.placeBuilding(new Headquarter(player0), point2);
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
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_MEADOW, map);

        /* Verify that there is an available flag point on the mountain meadow */
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
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_MEADOW, map);

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
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_MEADOW, map);

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
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_MEADOW, map);

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
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_MEADOW, map);

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
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_MEADOW, map);

        /* Verify that it's not possible to place a house on the mountain meadow */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            fail();
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
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_MEADOW, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's possible to build a road across the mountain meadow */
        Road road0 = map.placeRoad(player0, point2, point1, point3);
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossMountainMeadow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter hut on the map */
        Point point1 = new Point(15, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Surround the woodcutter hut with mountain meadow */
        Point point2 = new Point(15, 7);
        Point point3 = new Point(18, 6);
        Point point4 = new Point(18, 4);
        Point point5 = new Point(15, 3);
        Point point6 = new Point(12, 4);
        Point point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_MEADOW, map);
        Utils.surroundPointWithVegetation(point3, MOUNTAIN_MEADOW, map);
        Utils.surroundPointWithVegetation(point4, MOUNTAIN_MEADOW, map);
        Utils.surroundPointWithVegetation(point5, MOUNTAIN_MEADOW, map);
        Utils.surroundPointWithVegetation(point6, MOUNTAIN_MEADOW, map);
        Utils.surroundPointWithVegetation(point7, MOUNTAIN_MEADOW, map);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter hut */
        WoodcutterWorker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Tear down the woodcutter hut */
        woodcutter0.tearDown();

        /* Verify that the woodcutter worker can go back to the headquarter */
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoMountainMeadow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a mountain meadow on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_MEADOW, map);

        /* Place hunter hut */
        Point point1 = new Point(8, 6);
        Building hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        /* Finish construction of the hunter hut */
        Utils.constructHouse(hunter0);

        /* Occupy the hunter hut */
        Hunter hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        /* Place wild animal */
        WildAnimal animal0 = map.placeWildAnimal(point2);

        /* Verify that the worker can reach the animal */
        assertTrue(animal0.isExactlyAtPoint());

        boolean meet = false;
        for (int i = 0; i < 1000; i++) {
            hunter0.getWorker().stepTime();

            if (animal0.isExactlyAtPoint() && hunter.getPosition().equals(animal0.getPosition())) {
                meet = true;

                break;
            }
        }

        assertTrue(meet);
    }

    @Test
    public void testCanPlaceFlagOnBorderOfMountainMeadowAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a mountain meadow on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_MEADOW, map);

        /* Place snow next to the mountain meadow */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of mountain meadow and snow */
        Point point5 = new Point(5, 7);
        map.placeFlag(player0, point5);
    }

    @Test
    public void testFlagPointAvailableOnBorderOfMountainMeadowAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a mountain meadow on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_MEADOW, map);

        /* Place snow next to the mountain meadow */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of mountain meadow and snow */
        Point point5 = new Point(5, 7);
        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCanPlaceHeadquarterOnMountainMeadow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place mountain meadow on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_MEADOW, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, MOUNTAIN_MEADOW, map);
        }

        /* Verify that a headquarter can be placed on the mountain meadow */
        map.placeBuilding(new Headquarter(player0), point2);
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
        Utils.surroundPointWithVegetation(point1, BUILDABLE_MOUNTAIN, map);

        /* Verify that there is an available flag point on the buildable mountain */
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
        Utils.surroundPointWithVegetation(point1, BUILDABLE_MOUNTAIN, map);

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
        Utils.surroundPointWithVegetation(point1, BUILDABLE_MOUNTAIN, map);

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
        Utils.surroundPointWithVegetation(point1, BUILDABLE_MOUNTAIN, map);

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
        Utils.surroundPointWithVegetation(point1, BUILDABLE_MOUNTAIN, map);

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
        Utils.surroundPointWithVegetation(point1, BUILDABLE_MOUNTAIN, map);

        /* Verify that it's not possible to place a house on the buildable mountain */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            fail();
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
        Utils.surroundPointWithVegetation(point1, BUILDABLE_MOUNTAIN, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's possible to build a road across the buildable mountain */
        Road road0 = map.placeRoad(player0, point2, point1, point3);
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossBuildableMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter hut on the map */
        Point point1 = new Point(15, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Surround the woodcutter hut with buildable mountain */
        Point point2 = new Point(15, 7);
        Point point3 = new Point(18, 6);
        Point point4 = new Point(18, 4);
        Point point5 = new Point(15, 3);
        Point point6 = new Point(12, 4);
        Point point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point3, BUILDABLE_MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point4, BUILDABLE_MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point5, BUILDABLE_MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point6, BUILDABLE_MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point7, BUILDABLE_MOUNTAIN, map);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter hut */
        WoodcutterWorker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Tear down the woodcutter hut */
        woodcutter0.tearDown();

        /* Verify that the woodcutter worker can go back to the headquarter */
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoBuildableMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a buildable mountain on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_MOUNTAIN, map);

        /* Place hunter hut */
        Point point1 = new Point(8, 6);
        Building hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        /* Finish construction of the hunter hut */
        Utils.constructHouse(hunter0);

        /* Occupy the hunter hut */
        Hunter hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        /* Place wild animal */
        WildAnimal animal0 = map.placeWildAnimal(point2);

        /* Verify that the worker can reach the animal */
        boolean meet = false;
        for (int i = 0; i < 1000; i++) {
            hunter0.getWorker().stepTime();

            if (animal0.isExactlyAtPoint() && hunter.getPosition().equals(animal0.getPosition())) {
                meet = true;

                break;
            }
        }

        assertTrue(meet);
    }

    @Test
    public void testCanPlaceFlagOnBorderOfBuildableMountainAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a buildable mountain on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_MOUNTAIN, map);

        /* Place snow next to the buildable mountain */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of buildable mountain and snow */
        Point point5 = new Point(5, 7);
        map.placeFlag(player0, point5);
    }

    @Test
    public void testFlagPointAvailableOnBorderOfBuildableMountainAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a buildable mountain on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_MOUNTAIN, map);

        /* Place snow next to the buildable mountain */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of buildable mountain and snow */
        Point point5 = new Point(5, 7);
        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCanPlaceHeadquarterOnBuildableMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place buildable mountain on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_MOUNTAIN, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, BUILDABLE_MOUNTAIN, map);
        }

        /* Verify that a headquarter can be placed on the buildable mountain */
        map.placeBuilding(new Headquarter(player0), point2);
    }

    @Test
    public void testAvailableLargeHouseOnBuildableMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(13, 13);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place a buildable mountain on the map */
        Point point2 = new Point(6, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_MOUNTAIN, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, BUILDABLE_MOUNTAIN, map);
        }

        /* Verify that there is an available point for a large house on the buildable mountain */
        try {
            assertEquals(map.isAvailableHousePoint(player0, point2), Size.LARGE);
        } catch (Exception e) {
        }
    }


    // Lava

    @Test
    public void testNoAvailableFlagOnLava() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of lava on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, LAVA, map);

        /* Verify that there is no available flag point in the lava */
        assertFalse(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCannotPlaceFlagOnLava() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of lava on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, LAVA, map);

        /* Verify that it is not possible to place a flag on the lava */
        try {
            map.placeFlag(player0, point1);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableHouseOnLava() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of lava on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, LAVA, map);

        /* Verify that there is no available house point on the lava */
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceHouseOnLava() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of lava on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, LAVA, map);

        /* Verify that it's not possible to place a house on the lava */
        try {
            map.placeBuilding(new Woodcutter(player0), point1);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableMineOnLava() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of lava on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, LAVA, map);

        /* Verify that there is no available house point on the lava */
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnLava() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of lava on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, LAVA, map);

        /* Verify that it's not possible to place a house on the lava */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCannotBuildRoadAcrossLava() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of lava on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, LAVA, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's not possible to Build a road across the lava */
        try {
            Road road0 = map.placeRoad(player0, point2, point1, point3);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableHouseOnBorderOfLava() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of lava on the map */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, LAVA, map);
        Utils.surroundPointWithVegetation(point2, LAVA, map);

        /* Verify that there is no available house point on the border of the lava */
        Point point3 = new Point(11, 11);
        assertNull(map.isAvailableHousePoint(player0, point3));
    }

    @Test
    public void testCannotPlaceHouseOnBorderOfLava() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of lava on the map */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, LAVA, map);
        Utils.surroundPointWithVegetation(point2, LAVA, map);

        /* Verify that it's not possible to place a house on the border of the lava */
        Point point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testWorkerCannotWalkOffroadAcrossLava() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter hut on the map */
        Point point1 = new Point(15, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Surround the woodcutter hut with water */
        Point point2 = new Point(15, 7);
        Point point3 = new Point(18, 6);
        Point point4 = new Point(18, 4);
        Point point5 = new Point(15, 3);
        Point point6 = new Point(12, 4);
        Point point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, LAVA, map);
        Utils.surroundPointWithVegetation(point3, LAVA, map);
        Utils.surroundPointWithVegetation(point4, LAVA, map);
        Utils.surroundPointWithVegetation(point5, LAVA, map);
        Utils.surroundPointWithVegetation(point6, LAVA, map);
        Utils.surroundPointWithVegetation(point7, LAVA, map);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter hut */
        WoodcutterWorker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Tear down the woodcutter hut */
        woodcutter0.tearDown();

        /* Verify that the woodcutter worker can't go back to the headquarter */
        assertEquals(map.getTerrain().getTileAbove(point6), LAVA);
        assertEquals(map.getTerrain().getTileBelow(point7), LAVA);
        assertNotEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        for (int i = 0; i < 1000; i++) {

            assertNotEquals(woodcutterWorker.getPosition(), headquarter0.getPosition());

            map.stepTime();
        }
    }

    @Test
    public void testCannotPlaceFlagOnBorderOfLavaAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place lava on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, LAVA, map);

        /* Place snow next to the swamp */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's not possible to place a flag on the border of lava and snow */
        Point point5 = new Point(5, 7);

        try {
            map.placeFlag(player0, point5);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoFlagPointAvailableOnBorderOfLavaAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a swamp on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, LAVA, map);

        /* Place snow next to the swamp */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's not possible to place a flag on the border of lava and snow */
        Point point5 = new Point(5, 7);
        assertFalse(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCannotPlaceHeadquarterOnLava() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place lava on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, LAVA, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, LAVA, map);
        }

        /* Verify that a headquarter can be placed on the lava */
        try {
            map.placeBuilding(new Headquarter(player0), point2);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testNoAvailableLargeHouseOnLava() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(13, 13);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place lava on the map */
        Point point2 = new Point(6, 6);
        Utils.surroundPointWithVegetation(point2, LAVA, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, LAVA, map);
        }

        /* Verify that there is no available point for a large house on the lava */
        try {
            assertNull(map.isAvailableHousePoint(player0, point2));
        } catch (Exception e) {
        }
    }


    // Deep water

    @Test
    public void testNoAvailableFlagOnDeepWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of deep water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, DEEP_WATER, map);

        /* Verify that there is no available flag point in the deep water */
        assertFalse(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCannotPlaceFlagOnDeepWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of deep water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, DEEP_WATER, map);

        /* Verify that it is not possible to place a flag on the deep water */
        try {
            map.placeFlag(player0, point1);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableHouseOnDeepWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of deep water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, DEEP_WATER, map);

        /* Verify that there is no available house point on the deep water */
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceHouseOnDeepWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of deep water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, DEEP_WATER, map);

        /* Verify that it's not possible to place a house on the deep water */
        try {
            map.placeBuilding(new Woodcutter(player0), point1);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableMineOnDeepWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of deep water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, DEEP_WATER, map);

        /* Verify that there is no available house point on the deep water */
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnDeepWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of deep water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, DEEP_WATER, map);

        /* Verify that it's not possible to place a house on the deep water */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCannotBuildRoadAcrossDeepWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of deep water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, DEEP_WATER, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's not possible to Build a road across the deep water */
        try {
            Road road0 = map.placeRoad(player0, point2, point1, point3);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableHouseOnBorderOfDeepWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of deep water on the map */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, DEEP_WATER, map);
        Utils.surroundPointWithVegetation(point2, DEEP_WATER, map);

        /* Verify that there is no available house point on the border of the deep water */
        Point point3 = new Point(11, 11);
        assertNull(map.isAvailableHousePoint(player0, point3));
    }

    @Test
    public void testCannotPlaceHouseOnBorderOfDeepWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of deep water on the map */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, DEEP_WATER, map);
        Utils.surroundPointWithVegetation(point2, DEEP_WATER, map);

        /* Verify that it's not possible to place a house on the border of the deep water */
        Point point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testWorkerCannotWalkOffroadAcrossDeepWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter hut on the map */
        Point point1 = new Point(15, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Surround the woodcutter hut with water */
        Point point2 = new Point(15, 7);
        Point point3 = new Point(18, 6);
        Point point4 = new Point(18, 4);
        Point point5 = new Point(15, 3);
        Point point6 = new Point(12, 4);
        Point point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, DEEP_WATER, map);
        Utils.surroundPointWithVegetation(point3, DEEP_WATER, map);
        Utils.surroundPointWithVegetation(point4, DEEP_WATER, map);
        Utils.surroundPointWithVegetation(point5, DEEP_WATER, map);
        Utils.surroundPointWithVegetation(point6, DEEP_WATER, map);
        Utils.surroundPointWithVegetation(point7, DEEP_WATER, map);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter hut */
        WoodcutterWorker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Tear down the woodcutter hut */
        woodcutter0.tearDown();

        /* Verify that the woodcutter worker can't go back to the headquarter */
        assertEquals(map.getTerrain().getTileAbove(point6), DEEP_WATER);
        assertEquals(map.getTerrain().getTileBelow(point7), DEEP_WATER);
        assertNotEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        for (int i = 0; i < 1000; i++) {

            assertNotEquals(woodcutterWorker.getPosition(), headquarter0.getPosition());

            map.stepTime();
        }
    }

    @Test
    public void testCannotPlaceFlagOnBorderOfDeepWaterAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a lake with deep water on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, DEEP_WATER, map);

        /* Place snow next to the lake */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's not possible to place a flag on the border of deep water and snow */
        Point point5 = new Point(5, 7);

        try {
            map.placeFlag(player0, point5);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoFlagPointAvailableOnBorderOfDeepWaterAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a lake with deep water on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, DEEP_WATER, map);

        /* Place snow next to the lake */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's not possible to place a flag on the border of deep water and snow */
        Point point5 = new Point(5, 7);
        assertFalse(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCannotPlaceHeadquarterOnDeepWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place deep water on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, DEEP_WATER, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point2, DEEP_WATER, map);
        }

        /* Verify that a headquarter cannot be placed on the deep water */
        try {
            map.placeBuilding(new Headquarter(player0), point2);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testNoAvailableLargeHouseOnDeepWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(13, 13);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place deep water on the map */
        Point point2 = new Point(6, 6);
        Utils.surroundPointWithVegetation(point2, DEEP_WATER, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, DEEP_WATER, map);
        }

        /* Verify that there is no available point for a large house on the deep water */
        try {
            assertNull(map.isAvailableHousePoint(player0, point2));
        } catch (Exception e) {
        }
    }


    // Regular water

    @Test
    public void testNoAvailableFlagOnWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of Water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);

        /* Verify that there is no available flag point in the water */
        assertFalse(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCannotPlaceFlagOnWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);

        /* Verify that it is not possible to place a flag on the water */
        try {
            map.placeFlag(player0, point1);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableHouseOnWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);

        /* Verify that there is no available house point on the water */
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceHouseOnWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);

        /* Verify that it's not possible to place a house on the water */
        try {
            map.placeBuilding(new Woodcutter(player0), point1);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableMineOnWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);

        /* Verify that there is no available house point on the water */
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);

        /* Verify that it's not possible to place a house on the water */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCannotBuildRoadAcrossWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of water on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's not possible to Build a road across the water */
        try {
            Road road0 = map.placeRoad(player0, point2, point1, point3);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableHouseOnBorderOfWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of water on the map */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        /* Verify that there is no available house point on the border of the water */
        Point point3 = new Point(11, 11);
        assertNull(map.isAvailableHousePoint(player0, point3));
    }

    @Test
    public void testCannotPlaceHouseOnBorderOfWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of water on the map */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        /* Verify that it's not possible to place a house on the border of the water */
        Point point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testWorkerCannotWalkOffroadAcrossWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter hut on the map */
        Point point1 = new Point(15, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Surround the woodcutter hut with water */
        Point point2 = new Point(15, 7);
        Point point3 = new Point(18, 6);
        Point point4 = new Point(18, 4);
        Point point5 = new Point(15, 3);
        Point point6 = new Point(12, 4);
        Point point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, WATER, map);
        Utils.surroundPointWithVegetation(point3, WATER, map);
        Utils.surroundPointWithVegetation(point4, WATER, map);
        Utils.surroundPointWithVegetation(point5, WATER, map);
        Utils.surroundPointWithVegetation(point6, WATER, map);
        Utils.surroundPointWithVegetation(point7, WATER, map);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter hut */
        WoodcutterWorker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Tear down the woodcutter hut */
        woodcutter0.tearDown();

        /* Verify that the woodcutter worker can't go back to the headquarter */
        assertEquals(map.getTerrain().getTileAbove(point6), WATER);
        assertEquals(map.getTerrain().getTileBelow(point7), WATER);
        assertNotEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        for (int i = 0; i < 1000; i++) {

            assertNotEquals(woodcutterWorker.getPosition(), headquarter0.getPosition());

            map.stepTime();
        }
    }

    @Test
    public void testCannotPlaceFlagOnBorderOfWaterAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a lake on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        /* Place snow next to the lake */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's not possible to place a flag on the border of water and snow */
        Point point5 = new Point(5, 7);

        try {
            map.placeFlag(player0, point5);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoFlagPointAvailableOnBorderOfWaterAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a lake on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        /* Place snow next to the lake */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's not possible to place a flag on the border of water and snow */
        Point point5 = new Point(5, 7);
        assertFalse(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCannotPlaceHeadquarterOnWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place water on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, WATER, map);
        }

        /* Verify that a headquarter cannot be placed on the water */
        try {
            map.placeBuilding(new Headquarter(player0), point2);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testNoAvailableLargeHouseOnWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(13, 13);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place water on the map */
        Point point2 = new Point(6, 6);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, WATER, map);
        }

        /* Verify that there is no available point for a large house on the water */
        try {
            assertNull(map.isAvailableHousePoint(player0, point2));
        } catch (Exception e) {
        }
    }


    // Swamp

    @Test
    public void testNoAvailableFlagOnSwamp() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of swamp on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);

        /* Verify that there is no available flag point in the swamp */
        assertFalse(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCannotPlaceFlagOnSwamp() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of swamp on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);

        /* Verify that it is not possible to place a flag on the swamp */
        try {
            map.placeFlag(player0, point1);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableHouseOnSwamp() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of swamp on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);

        /* Verify that there is no available house point on the swamp */
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceHouseOnSwamp() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of swamp on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);

        /* Verify that it's not possible to place a house on the swamp */
        try {
            map.placeBuilding(new Woodcutter(player0), point1);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableMineOnSwamp() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of swamp on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);

        /* Verify that there is no available house point on the swamp */
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnSwamp() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of swamp on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);

        /* Verify that it's not possible to place a house on the swamp */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCannotBuildRoadAcrossSwamp() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of swamp on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's not possible to Build a road across the swamp */
        try {
            Road road0 = map.placeRoad(player0, point2, point1, point3);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableHouseOnBorderOfSwamp() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of swamp on the map */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);
        Utils.surroundPointWithVegetation(point2, SWAMP, map);

        /* Verify that there is no available house point on the border of the swamp */
        Point point3 = new Point(11, 11);
        assertNull(map.isAvailableHousePoint(player0, point3));
    }

    @Test
    public void testCannotPlaceHouseOnBorderOfSwamp() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small patch of swamp on the map */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);
        Utils.surroundPointWithVegetation(point2, SWAMP, map);

        /* Verify that it's not possible to place a house on the border of the swamp */
        Point point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testWorkerCannotWalkOffroadAcrossSwamp() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter hut on the map */
        Point point1 = new Point(15, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Surround the woodcutter hut with water */
        Point point2 = new Point(15, 7);
        Point point3 = new Point(18, 6);
        Point point4 = new Point(18, 4);
        Point point5 = new Point(15, 3);
        Point point6 = new Point(12, 4);
        Point point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, SWAMP, map);
        Utils.surroundPointWithVegetation(point3, SWAMP, map);
        Utils.surroundPointWithVegetation(point4, SWAMP, map);
        Utils.surroundPointWithVegetation(point5, SWAMP, map);
        Utils.surroundPointWithVegetation(point6, SWAMP, map);
        Utils.surroundPointWithVegetation(point7, SWAMP, map);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter hut */
        WoodcutterWorker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Tear down the woodcutter hut */
        woodcutter0.tearDown();

        /* Verify that the woodcutter worker can't go back to the headquarter */
        assertEquals(map.getTerrain().getTileAbove(point6), SWAMP);
        assertEquals(map.getTerrain().getTileBelow(point7), SWAMP);
        assertNotEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        for (int i = 0; i < 1000; i++) {

            assertNotEquals(woodcutterWorker.getPosition(), headquarter0.getPosition());

            map.stepTime();
        }
    }

    @Test
    public void testWorkerCannotGoIntoSwamp() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a swamp on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SWAMP, map);

        /* Place tree */
        //Tree tree = map.placeTree(point2);
        WildAnimal animal0 = map.placeWildAnimal(point2);

        /* Place woodcutter */
        Point point1 = new Point(8, 6);
        Building woodcutter0 = map.placeBuilding(new HunterHut(player0), point1);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter */
        Hunter hunter = Utils.occupyBuilding(new Hunter(player0, map), woodcutter0);

        /* Verify that the worker can't reach the tree */
        for (int i = 0; i < 1000; i++) {
            woodcutter0.getWorker().stepTime();

            assertNotEquals(hunter.getPosition(), animal0.getPosition());
        }
    }

    @Test
    public void testCannotPlaceFlagOnBorderOfSwampAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a swamp on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SWAMP, map);

        /* Place snow next to the swamp */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's not possible to place a flag on the border of swamp and snow */
        Point point5 = new Point(5, 7);

        try {
            map.placeFlag(player0, point5);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoFlagPointAvailableOnBorderOfSwampAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a swamp on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SWAMP, map);

        /* Place snow next to the swamp */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's not possible to place a flag on the border of swamp and snow */
        Point point5 = new Point(5, 7);
        assertFalse(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCannotPlaceHeadquarterOnSwamp() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place swamp on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SWAMP, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, SWAMP, map);
        }

        /* Verify that a headquarter can be placed on the swamp */
        try {
            map.placeBuilding(new Headquarter(player0), point2);
            fail();
        } catch (Exception e) {

        }
    }

    @Test
    public void testNoAvailableLargeHouseOnSwamp() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(13, 13);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place swamp on the map */
        Point point2 = new Point(6, 6);
        Utils.surroundPointWithVegetation(point2, SWAMP, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, SWAMP, map);
        }

        /* Verify that there is no available point for a large house on the swamp */
        try {
            assertNull(map.isAvailableHousePoint(player0, point2));
        } catch (Exception e) {
        }
    }


    // Magenta
    @Test
    public void testAvailableFlagInMagenta() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small magenta on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);

        /* Verify that there is an available flag point in the magenta */
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagInMagenta() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small magenta on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);

        /* Verify that it is possible to place a flag in the magenta */
        map.placeFlag(player0, point1);
    }

    @Test
    public void testNoAvailableHouseInMagenta() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small magenta on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);

        /* Verify that there is no available house point in the magenta */
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceHouseInMagenta() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small magenta on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);

        /* Verify that it's not possible to place a house in the magenta */
        try {
            map.placeBuilding(new Woodcutter(player0), point1);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testNoAvailableMineInMagenta() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small magenta on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);

        /* Verify that there is no available house point in the magenta */
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineInMagenta() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small magenta on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);

        /* Verify that it's not possible to place a house in the magenta */
        try {
            map.placeBuilding(new GoldMine(player0), point1);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCanBuildRoadAcrossMagenta() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small magenta on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's possible to Build a road across the magenta */
        Road road0 = map.placeRoad(player0, point2, point1, point3);
    }

    @Test
    public void testNoAvailableHouseOnBorderOfMagenta() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small magenta on the map */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);
        Utils.surroundPointWithVegetation(point2, MAGENTA, map);

        /* Verify that there is no available house point on the border of the magenta */
        Point point3 = new Point(11, 11);
        assertNull(map.isAvailableHousePoint(player0, point3));
    }

    @Test
    public void testCannotPlaceHouseOnBorderOfMagenta() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small magenta on the map */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);
        Utils.surroundPointWithVegetation(point2, MAGENTA, map);

        /* Verify that it's not possible to place a house on the border of the magenta */
        Point point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossMagenta() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter hut on the map */
        Point point1 = new Point(15, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Surround the woodcutter hut with magenta */
        Point point2 = new Point(15, 7);
        Point point3 = new Point(18, 6);
        Point point4 = new Point(18, 4);
        Point point5 = new Point(15, 3);
        Point point6 = new Point(12, 4);
        Point point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, MAGENTA, map);
        Utils.surroundPointWithVegetation(point3, MAGENTA, map);
        Utils.surroundPointWithVegetation(point4, MAGENTA, map);
        Utils.surroundPointWithVegetation(point5, MAGENTA, map);
        Utils.surroundPointWithVegetation(point6, MAGENTA, map);
        Utils.surroundPointWithVegetation(point7, MAGENTA, map);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter hut */
        WoodcutterWorker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Tear down the woodcutter hut */
        woodcutter0.tearDown();

        /* Verify that the woodcutter worker can go back to the headquarter */
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoMagenta() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place magenta on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MAGENTA, map);

        /* Place hunter hut */
        Point point1 = new Point(8, 6);
        Building hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        /* Finish construction of the hunter hut */
        Utils.constructHouse(hunter0);

        /* Occupy the hunter hut */
        Hunter hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        /* Place wild animal */
        WildAnimal animal0 = map.placeWildAnimal(point2);

        /* Verify that the worker can reach the animal */
        assertTrue(animal0.isExactlyAtPoint());

        boolean meet = false;
        for (int i = 0; i < 1000; i++) {
            hunter0.getWorker().stepTime();

            if (animal0.isExactlyAtPoint() && hunter.getPosition().equals(animal0.getPosition())) {
                meet = true;

                break;
            }
        }

        assertTrue(meet);
    }

    @Test
    public void testCanPlaceFlagOnBorderOfMagentaAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place magenta on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MAGENTA, map);

        /* Place snow next to the magenta */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of magenta and snow */
        Point point5 = new Point(5, 7);
        map.placeFlag(player0, point5);
    }

    @Test
    public void testFlagPointAvailableOnBorderOfMagentaAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place magenta on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MAGENTA, map);

        /* Place snow next to the magenta */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of magenta and snow */
        Point point5 = new Point(5, 7);
        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCannotPlaceHeadquarterOnMagenta() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place magenta on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MAGENTA, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, MAGENTA, map);
        }

        /* Verify that a headquarter cannot be placed on the magenta */
        try {
            map.placeBuilding(new Headquarter(player0), point2);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testNoAvailableLargeHouseOnMagenta() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(13, 13);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place magenta on the map */
        Point point2 = new Point(6, 6);
        Utils.surroundPointWithVegetation(point2, MAGENTA, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, MAGENTA, map);
        }

        /* Verify that there is no available point for a large house on the magenta */
        try {
            assertNull(map.isAvailableHousePoint(player0, point2));
        } catch (Exception e) {
        }
    }


    // Regular mountain (that can be mined)
    @Test
    public void testAvailableFlagInMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN, map);

        /* Verify that there is an available flag point in the mountain */
        assertTrue(map.isAvailableFlagPoint(player0, point1));

    }

    @Test
    public void testCanPlaceFlagInMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN, map);

        /* Verify that it is possible to place a flag in the mountain */
        map.placeFlag(player0, point1);
    }

    @Test
    public void testNoAvailableHouseInMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN, map);

        /* Verify that there is no available house point in the mountain */
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceHouseInMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN, map);

        /* Verify that it's not possible to place a house in the mountain */
        try {
            map.placeBuilding(new Woodcutter(player0), point1);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testAvailableMineInMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN, map);

        /* Verify that there is no available house point in the mountain */
        assertTrue(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCanPlaceMineInMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN, map);

        /* Verify that it's possible to place a house in the mountain */
        map.placeBuilding(new GoldMine(player0), point1);
    }

    @Test
    public void testCanBuildRoadAcrossMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN, map);

        /* Place flags */
        Point point2 = new Point(8, 10);
        Point point3 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Verify that it's possible to Build a road across the mountain */
        Road road0 = map.placeRoad(player0, point2, point1, point3);
    }

    @Test
    public void testNoAvailableHouseOnBorderOfMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN, map);

        /* Verify that there is no available house point on the border of the mountain */
        Point point3 = new Point(11, 11);
        assertNull(map.isAvailableHousePoint(player0, point3));
    }

    @Test
    public void testCannotPlaceHouseOnBorderOfMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN, map);

        /* Verify that it's not possible to place a house on the border of the mountain */
        Point point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter hut on the map */
        Point point1 = new Point(15, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Surround the woodcutter hut with mountains */
        Point point2 = new Point(15, 7);
        Point point3 = new Point(18, 6);
        Point point4 = new Point(18, 4);
        Point point5 = new Point(15, 3);
        Point point6 = new Point(12, 4);
        Point point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point3, MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point4, MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point5, MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point6, MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point7, MOUNTAIN, map);

        /* Finish construction of the woodcutter hut */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter hut */
        WoodcutterWorker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Tear down the woodcutter hut */
        woodcutter0.tearDown();

        /* Verify that the woodcutter worker can go back to the headquarter */
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a mountain on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN, map);

        /* Place hunter hut */
        Point point1 = new Point(8, 6);
        Building hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        /* Finish construction of the hunter hut */
        Utils.constructHouse(hunter0);

        /* Occupy the hunter hut */
        Hunter hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        /* Place wild animal */
        WildAnimal animal0 = map.placeWildAnimal(point2);

        /* Verify that the worker can reach the animal */
        assertTrue(animal0.isExactlyAtPoint());

        boolean meet = false;
        for (int i = 0; i < 1000; i++) {
            hunter0.getWorker().stepTime();

            if (animal0.isExactlyAtPoint() && hunter.getPosition().equals(animal0.getPosition())) {
                meet = true;

                break;
            }
        }

        assertTrue(meet);
    }

    @Test
    public void testCanPlaceFlagOnBorderOfMountainAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a mountain on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN, map);

        /* Place snow next to the mountain */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of mountain and snow */
        Point point5 = new Point(5, 7);
        map.placeFlag(player0, point5);
    }

    @Test
    public void testFlagPointAvailableOnBorderOfMountainAndSnow() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place a mountain on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN, map);

        /* Place snow next to the mountain */
        Point point3 = new Point(4, 8);
        Point point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        /* Verify that it's possible to place a flag on the border of mountain and snow */
        Point point5 = new Point(5, 7);
        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCannotPlaceHeadquarterOnMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place mountain on the map */
        Point point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, MOUNTAIN, map);
        }

        /* Verify that a headquarter cannot be placed on the mountain */
        try {
            map.placeBuilding(new Headquarter(player0), point2);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testNoAvailableLargeHouseOnMountain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(13, 13);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place mountain on the map */
        Point point2 = new Point(6, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN, map);

        for (Point point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, MOUNTAIN, map);
        }

        /* Verify that there is no available point for a large house on the mountain */
        try {
            assertNull(map.isAvailableHousePoint(player0, point2));
        } catch (Exception e) {
        }
    }
}
