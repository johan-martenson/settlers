package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.actors.Hunter;
import org.appland.settlers.model.actors.WoodcutterWorker;
import org.appland.settlers.model.buildings.Fishery;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.HunterHut;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Vegetation.*;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestPlacementInTerrain {

    // TODO: test placement on borders between vegetation areas

    @Test
    public void testPathCloseToLake() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        /* Place lake with this form:

        _____
        W W  \______
             W  W  W

        */
        var pointX = new Point(4, 4);
        var point1 = new Point(6, 4);
        var point2 = new Point(7, 3);
        var point3 = new Point(9, 3);
        var point4 = new Point(11, 3);

        Utils.surroundPointWithVegetation(pointX, WATER, map);
        Utils.surroundPointWithVegetation(point1, WATER, map);
        Utils.surroundPointWithVegetation(point2, WATER, map);
        Utils.surroundPointWithVegetation(point3, WATER, map);
        Utils.surroundPointWithVegetation(point4, WATER, map);

        // Place headquarters
        var point0 = new Point(16, 6);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place fisheries like this:

      Fishery
        \         Fishery
        Flag_       \
        W W  \______Flag
             W  W  W

        */

        var point5 = new Point(4, 6);
        var point6 = new Point(9, 5);

        assertTrue(map.isAvailableFlagPoint(player0, point5.downRight()));
        assertTrue(map.isAvailableFlagPoint(player0, point6.downRight()));
        assertNotNull(map.isAvailableHousePoint(player0, point5));
        assertNotNull(map.isAvailableHousePoint(player0, point6));

        var fishery0 = map.placeBuilding(new Fishery(player0), point5);
        var fishery1 = map.placeBuilding(new Fishery(player0), point6);

        // Verify that it's possible to build a road between the flags that follows the edge of the lake
        var point7 = new Point(7, 5);
        var point8 = new Point(8, 4);

        assertEquals(map.getVegetationDownLeft(fishery0.getFlag().getPosition()), WATER);
        assertEquals(map.getVegetationBelow(fishery0.getFlag().getPosition()), WATER);

        assertEquals(map.getVegetationDownLeft(point7), WATER);
        assertEquals(map.getVegetationBelow(point7), WATER);
        assertEquals(map.getVegetationDownRight(point7), MEADOW_1);

        assertTrue(map.isNextToAnyWater(fishery0.getFlag().getPosition()));
        assertTrue(map.isNextToAnyWater(fishery1.getFlag().getPosition()));

        // First, define the road like a player would
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

        // Then place the road
        var road0 = map.placeRoad(player0, fishery0.getFlag().getPosition(), point7, point8, fishery1.getFlag().getPosition());

        assertTrue(map.arePointsConnectedByRoads(point5, point6));
    }

    // Desert
    @Test
    public void testAvailableFlagInDesert() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small desert on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, DESERT_1, map);

        // Verify that there is an available flag point in the desert
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagInDesert() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small desert on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, DESERT_1, map);

        // Verify that it is possible to place a flag in the desert
        var flag = map.placeFlag(player0, point1);

        assertTrue(map.isFlagAtPoint(point1));
        assertEquals(flag, map.getFlagAtPoint(point1));
    }

    @Test
    public void testNoAvailableHouseInDesert() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small desert on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, DESERT_1, map);

        // Verify that there is no available house point in the desert
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceHouseInDesert() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small desert on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, DESERT_1, map);

        // Verify that it's not possible to place a house in the desert
        try {
            map.placeBuilding(new Woodcutter(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableMineInDesert() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small desert on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, DESERT_1, map);

        // Verify that there is no available house point in the desert
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineInDesert() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small desert on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, DESERT_1, map);

        // Verify that it's not possible to place a house in the desert
        try {
            map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testCanBuildRoadAcrossDesert() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small desert on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, DESERT_1, map);

        // Place flags
        var point2 = new Point(8, 10);
        var point3 = new Point(12, 10);
        var flag0 = map.placeFlag(player0, point2);
        var flag1 = map.placeFlag(player0, point3);

        // Verify that it's possible to Build a road across the desert
        var road0 = map.placeRoad(player0, point2, point1, point3);

        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testNoAvailableHouseOnBorderOfDesert() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small desert on the map
        var point1 = new Point(10, 10);
        var point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, DESERT_1, map);
        Utils.surroundPointWithVegetation(point2, DESERT_1, map);

        // Verify that there is no available house point on the border of the desert
        var point3 = new Point(11, 11);
        assertNull(map.isAvailableHousePoint(player0, point3));
    }

    @Test
    public void testCannotPlaceHouseOnBorderOfDesert() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small desert on the map
        var point1 = new Point(10, 10);
        var point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, DESERT_1, map);
        Utils.surroundPointWithVegetation(point2, DESERT_1, map);

        // Verify that it's not possible to place a house on the border of the desert
        var point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossDesert() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a woodcutter hut on the map
        var point1 = new Point(15, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Surround the woodcutter hut with a desert
        var point2 = new Point(15, 7);
        var point3 = new Point(18, 6);
        var point4 = new Point(18, 4);
        var point5 = new Point(15, 3);
        var point6 = new Point(12, 4);
        var point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, DESERT_1, map);
        Utils.surroundPointWithVegetation(point3, DESERT_1, map);
        Utils.surroundPointWithVegetation(point4, DESERT_1, map);
        Utils.surroundPointWithVegetation(point5, DESERT_1, map);
        Utils.surroundPointWithVegetation(point6, DESERT_1, map);
        Utils.surroundPointWithVegetation(point7, DESERT_1, map);

        // Finish construction of the woodcutter hut
        Utils.constructHouse(woodcutter0);

        // Occupy the woodcutter hut
        var woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Tear down the woodcutter hut
        woodcutter0.tearDown();

        // Verify that the woodcutter worker can go back to the headquarters
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoDesert() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a desert on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, DESERT_1, map);

        // Place hunter hut
        var point1 = new Point(8, 6);
        var hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        // Finish construction of the hunter hut
        Utils.constructHouse(hunter0);

        // Occupy the hunter hut
        var hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        // Place wild animal
        var animal0 = map.placeWildAnimal(point2);

        // Verify that the worker can reach the animal
        assertTrue(animal0.isExactlyAtPoint());

        var meet = false;
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

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a desert on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, DESERT_1, map);

        // Place snow next to the desert
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of desert and snow
        var point5 = new Point(5, 7);
        var flag = map.placeFlag(player0, point5);

        assertTrue(map.isFlagAtPoint(point5));
        assertEquals(flag, map.getFlagAtPoint(point5));
    }

    @Test
    public void testFlagPointAvailableOnBorderOfDesertAndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a desert on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, DESERT_1, map);

        // Place snow next to the desert
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of desert and snow
        var point5 = new Point(5, 7);
        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCannotPlaceHeadquarterOnDesert() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place a desert on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, DESERT_1, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, DESERT_1, map);
        }

        // Verify that a headquarters cannot be placed in the desert
        try {
            map.placeBuilding(new Headquarter(player0), point2);

            fail();
        } catch (Exception e) { }
    }


    // Snow
// Also test: -build road next to snow. Is that OK?

    @Test
    public void testNoAvailableFlagOnSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of snow on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SNOW, map);

        // Verify that there is no available flag point in the snow
        assertFalse(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCannotPlaceFlagOnSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of snow on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SNOW, map);

        // Verify that it is not possible to place a flag on the snow
        try {
            map.placeFlag(player0, point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableHouseOnSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of snow on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SNOW, map);

        // Verify that there is no available house point on the snow
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceHouseOnSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of snow on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SNOW, map);

        // Verify that it's not possible to place a house on the snow
        try {
            map.placeBuilding(new Woodcutter(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableMineOnSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of snow on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SNOW, map);

        // Verify that there is no available house point on the snow
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of snow on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SNOW, map);

        // Verify that it's not possible to place a house on the snow
        try {
            map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testCannotBuildRoadAcrossSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of snow on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SNOW, map);

        // Place flags
        var point2 = new Point(8, 10);
        var point3 = new Point(12, 10);
        var flag0 = map.placeFlag(player0, point2);
        var flag1 = map.placeFlag(player0, point3);

        // Verify that it's not possible to Build a road across the snow
        try {
            var road0 = map.placeRoad(player0, point2, point1, point3);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableHouseOnBorderOfSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of snow on the map
        var point1 = new Point(10, 10);
        var point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, SNOW, map);
        Utils.surroundPointWithVegetation(point2, SNOW, map);

        // Verify that there is no available house point on the border of the snow
        var point3 = new Point(11, 11);
        assertNull(map.isAvailableHousePoint(player0, point3));
    }

    @Test
    public void testCannotPlaceHouseOnBorderOfSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of snow on the map
        var point1 = new Point(10, 10);
        var point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, SNOW, map);
        Utils.surroundPointWithVegetation(point2, SNOW, map);

        // Verify that it's not possible to place a house on the border of the snow
        var point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testWorkerCannotWalkOffroadAcrossSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a woodcutter hut on the map
        var point1 = new Point(15, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Surround the woodcutter hut with water
        var point2 = new Point(15, 7);
        var point3 = new Point(18, 6);
        var point4 = new Point(18, 4);
        var point5 = new Point(15, 3);
        var point6 = new Point(12, 4);
        var point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, SNOW, map);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);
        Utils.surroundPointWithVegetation(point5, SNOW, map);
        Utils.surroundPointWithVegetation(point6, SNOW, map);
        Utils.surroundPointWithVegetation(point7, SNOW, map);

        // Finish construction of the woodcutter hut
        Utils.constructHouse(woodcutter0);

        // Occupy the woodcutter hut
        var woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Tear down the woodcutter hut
        woodcutter0.tearDown();

        // Verify that the woodcutter worker can't go back to the headquarters
        assertEquals(map.getVegetationAbove(point6), SNOW);
        assertEquals(map.getVegetationBelow(point7), SNOW);
        assertNotEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        for (int i = 0; i < 1000; i++) {
            assertNotEquals(woodcutterWorker.getPosition(), headquarter0.getPosition());

            map.stepTime();
        }
    }

    @Test
    public void testWorkerCannotGoIntoSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a patch of snow on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SNOW, map);

        // Place a wild animal
        var animal0 = map.placeWildAnimal(point2);

        // Place hunter hut
        var point1 = new Point(8, 6);
        var hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        // Finish construction of the hunter hut
        Utils.constructHouse(hunter0);

        // Occupy the hunter hut
        var hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        // Verify that the worker can't reach the animal
        for (int i = 0; i < 1000; i++) {
            hunter0.getWorker().stepTime();

            assertNotEquals(hunter.getPosition(), animal0.getPosition());
        }
    }

    @Test
    public void testCannotPlaceFlagOnBorderOfSnowAndWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a lake on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        // Place snow next to the lake
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's not possible to place a flag on the border of water and snow
        var point5 = new Point(5, 7);

        try {
            map.placeFlag(player0, point5);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoFlagPointAvailableOnBorderOfSnowAndWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a lake on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        // Place snow next to the lake
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's not possible to place a flag on the border of water and snow
        var point5 = new Point(5, 7);

        assertFalse(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCannotPlaceHeadquarterOnSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place snow on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SNOW, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, SNOW, map);
        }

        // Verify that a headquarters cannot be placed on the snow
        try {
            map.placeBuilding(new Headquarter(player0), point2);

            fail();
        } catch (Exception e) {
        }
    }


    // MEADOW_1

    @Test
    public void testAvailableFlagOnMeadow1() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of grass on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MEADOW_1, map);

        // Verify that there is an available flag point on the grass
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagOnMeadow1() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of grass on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MEADOW_1, map);

        // Verify that it is  possible to place a flag on the grass
        var flag = map.placeFlag(player0, point1);

        assertTrue(map.isFlagAtPoint(point1));
        assertEquals(flag, map.getFlagAtPoint(point1));
    }

    @Test
    public void testAvailableHouseOnMeadow1() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of grass on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MEADOW_1, map);

        // Verify that there is an available house point on the grass
        assertNotNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCanPlaceHouseOnMeadow1() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of grass on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MEADOW_1, map);

        // Verify that it's possible to place a house on the grass
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        assertNotNull(woodcutter);
        assertTrue(map.isBuildingAtPoint(point1));
        assertEquals(woodcutter, map.getBuildingAtPoint(point1));
    }

    @Test
    public void testNoAvailableMineOnMeadow1() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of grass on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MEADOW_1, map);

        // Verify that there is no available house point on the grass
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnMeadow1() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of grass on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MEADOW_1, map);

        // Verify that it's not possible to place a house on the grass
        try {
            map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testCanBuildRoadAcrossMeadow1() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of grass on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MEADOW_1, map);

        // Place flags
        var point2 = new Point(8, 10);
        var point3 = new Point(12, 10);
        var flag0 = map.placeFlag(player0, point2);
        var flag1 = map.placeFlag(player0, point3);

        // Verify that it's possible to build a road across the grass
        var road0 = map.placeRoad(player0, point2, point1, point3);

        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossMeadow1() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a woodcutter hut on the map
        var point1 = new Point(15, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Surround the woodcutter hut with grass
        var point2 = new Point(15, 7);
        var point3 = new Point(18, 6);
        var point4 = new Point(18, 4);
        var point5 = new Point(15, 3);
        var point6 = new Point(12, 4);
        var point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, MEADOW_1, map);
        Utils.surroundPointWithVegetation(point3, MEADOW_1, map);
        Utils.surroundPointWithVegetation(point4, MEADOW_1, map);
        Utils.surroundPointWithVegetation(point5, MEADOW_1, map);
        Utils.surroundPointWithVegetation(point6, MEADOW_1, map);
        Utils.surroundPointWithVegetation(point7, MEADOW_1, map);

        // Finish construction of the woodcutter hut
        Utils.constructHouse(woodcutter0);

        // Occupy the woodcutter hut
        var woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Tear down the woodcutter hut
        woodcutter0.tearDown();

        // Verify that the woodcutter worker can go back to the headquarters
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoMeadow1() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a patch of grass on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MEADOW_1, map);

        // Place hunter hut
        var point1 = new Point(8, 6);
        var hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        // Finish construction of the hunter hut
        Utils.constructHouse(hunter0);

        // Occupy the hunter hut
        var hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        // Place wild animal
        var animal0 = map.placeWildAnimal(point2);

        // Verify that the worker can reach the animal
        assertTrue(animal0.isExactlyAtPoint());

        var meet = false;
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
    public void testCanPlaceFlagOnBorderOfMeadow1AndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a patch of grass on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MEADOW_1, map);

        // Place snow next to the grass
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of grass and snow
        var point5 = new Point(5, 7);
        var flag = map.placeFlag(player0, point5);

        assertTrue(map.isFlagAtPoint(point5));
        assertEquals(flag, map.getFlagAtPoint(point5));
    }

    @Test
    public void testFlagPointAvailableOnBorderOfMeadow1AndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a patch of grass on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MEADOW_1, map);

        // Place snow next to the grass
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of grass and snow
        var point5 = new Point(5, 7);
        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }


    // Savannah

    @Test
    public void testAvailableFlagOnSavannah() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of savannah on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SAVANNAH, map);

        // Verify that there is an available flag point on the savannah
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagOnSavannah() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of savannah on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SAVANNAH, map);

        // Verify that it is  possible to place a flag on the savannah
        var flag = map.placeFlag(player0, point1);

        assertTrue(map.getFlags().contains(flag));
        assertTrue(map.isFlagAtPoint(point1));
        assertEquals(map.getFlagAtPoint(point1), flag);
    }

    @Test
    public void testAvailableHouseOnSavannah() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of savannah on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SAVANNAH, map);

        // Verify that there is an available house point on the savannah
        assertNotNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCanPlaceHouseOnSavannah() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of savannah on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SAVANNAH, map);

        // Verify that it's possible to place a house on the savannah
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(map.getBuildings().contains(woodcutter));
        assertTrue(map.isBuildingAtPoint(point1));
        assertEquals(map.getBuildingAtPoint(point1), woodcutter);
    }

    @Test
    public void testNoAvailableMineOnSavannah() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of savannah on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SAVANNAH, map);

        // Verify that there is no available house point on the savannah
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnSavannah() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of savannah on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SAVANNAH, map);

        // Verify that it's not possible to place a house on the savannah
        try {
            map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testCanBuildRoadAcrossSavannah() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of savannah on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SAVANNAH, map);

        // Place flags
        var point2 = new Point(8, 10);
        var point3 = new Point(12, 10);
        var flag0 = map.placeFlag(player0, point2);
        var flag1 = map.placeFlag(player0, point3);

        // Verify that it's possible to build a road across the savannah
        var road0 = map.placeRoad(player0, point2, point1, point3);

        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossSavannah() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a woodcutter hut on the map
        var point1 = new Point(15, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Surround the woodcutter hut with savannah
        var point2 = new Point(15, 7);
        var point3 = new Point(18, 6);
        var point4 = new Point(18, 4);
        var point5 = new Point(15, 3);
        var point6 = new Point(12, 4);
        var point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, SAVANNAH, map);
        Utils.surroundPointWithVegetation(point3, SAVANNAH, map);
        Utils.surroundPointWithVegetation(point4, SAVANNAH, map);
        Utils.surroundPointWithVegetation(point5, SAVANNAH, map);
        Utils.surroundPointWithVegetation(point6, SAVANNAH, map);
        Utils.surroundPointWithVegetation(point7, SAVANNAH, map);

        // Finish construction of the woodcutter hut
        Utils.constructHouse(woodcutter0);

        // Occupy the woodcutter hut
        var woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Tear down the woodcutter hut
        woodcutter0.tearDown();

        // Verify that the woodcutter worker can go back to the headquarters
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoSavannah() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a savannah on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SAVANNAH, map);

        // Place hunter hut
        var point1 = new Point(8, 6);
        var hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        // Finish construction of the hunter hut
        Utils.constructHouse(hunter0);

        // Occupy the hunter hut
        var hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        // Place wild animal
        var animal0 = map.placeWildAnimal(point2);

        // Verify that the worker can reach the animal
        var meet = false;
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

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a savannah on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SAVANNAH, map);

        // Place snow next to the savannah
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of savannah and snow
        var point5 = new Point(5, 7);
        var flag = map.placeFlag(player0, point5);

        assertTrue(map.getFlags().contains(flag));
        assertTrue(map.isFlagAtPoint(point5));
        assertEquals(map.getFlagAtPoint(point5), flag);
    }

    @Test
    public void testFlagPointAvailableOnBorderOfSavannahAndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a savannah on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SAVANNAH, map);

        // Place snow next to the savannah
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of savannah and snow
        var point5 = new Point(5, 7);
        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCanPlaceHeadquarterOnSavannah() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place savannah on the map
        var point2 = new Point(8, 8);
        Utils.surroundPointWithVegetation(point2, SAVANNAH, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, SAVANNAH, map);
        }

        // Verify that a headquarters can be placed on the savannah
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        assertTrue(map.getBuildings().contains(headquarter));
        assertTrue(map.isBuildingAtPoint(point2));
        assertEquals(map.getBuildingAtPoint(point2), headquarter);
    }


    // Shallow water (buildable water)

    @Test
    public void testAvailableFlagOnBuildableWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of buildable water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, BUILDABLE_WATER, map);

        // Verify that there is an available flag point on the buildable water
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagOnBuildableWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of buildable water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, BUILDABLE_WATER, map);

        // Verify that it is  possible to place a flag on the buildable water
        var flag = map.placeFlag(player0, point1);

        assertTrue(map.getFlags().contains(flag));
        assertTrue(map.isFlagAtPoint(point1));
        assertEquals(map.getFlagAtPoint(point1), flag);
    }

    @Test
    public void testAvailableHouseOnBuildableWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of buildable water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, BUILDABLE_WATER, map);

        // Verify that there is an available house point next to the buildable water
        assertNotNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCanPlaceHouseOnBuildableWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of buildable water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, BUILDABLE_WATER, map);

        // Verify that it's possible to place a house on the buildable water
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(map.getBuildings().contains(woodcutter));
        assertTrue(map.isBuildingAtPoint(point1));
        assertEquals(map.getBuildingAtPoint(point1), woodcutter);
    }

    @Test
    public void testNoAvailableMineOnBuildableWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of buildable water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER_2, map);

        // Verify that there is no available house point on the buildable water
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnBuildableWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of buildable water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER_2, map);

        // Verify that it's not possible to place a house on the buildable water
        try {
            map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testCanBuildRoadAcrossBuildableWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of buildable water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, BUILDABLE_WATER, map);

        // Place flags
        var point2 = new Point(8, 10);
        var point3 = new Point(12, 10);
        var flag0 = map.placeFlag(player0, point2);
        var flag1 = map.placeFlag(player0, point3);

        // Verify that it's possible to build a road across the buildable water
        var road0 = map.placeRoad(player0, point2, point1, point3);

        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossBuildableWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a woodcutter hut on the map
        var point1 = new Point(15, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Surround the woodcutter hut with buildable water
        var point2 = new Point(15, 7);
        var point3 = new Point(18, 6);
        var point4 = new Point(18, 4);
        var point5 = new Point(15, 3);
        var point6 = new Point(12, 4);
        var point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_WATER, map);
        Utils.surroundPointWithVegetation(point3, BUILDABLE_WATER, map);
        Utils.surroundPointWithVegetation(point4, BUILDABLE_WATER, map);
        Utils.surroundPointWithVegetation(point5, BUILDABLE_WATER, map);
        Utils.surroundPointWithVegetation(point6, BUILDABLE_WATER, map);
        Utils.surroundPointWithVegetation(point7, BUILDABLE_WATER, map);

        // Finish construction of the woodcutter hut
        Utils.constructHouse(woodcutter0);

        // Occupy the woodcutter hut
        var woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Tear down the woodcutter hut
        woodcutter0.tearDown();

        // Verify that the woodcutter worker can go back to the headquarters
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoBuildableWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a lake of buildable water on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_WATER, map);

        // Place hunter hut
        var point1 = new Point(8, 6);
        var hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        // Finish construction of the hunter hut
        Utils.constructHouse(hunter0);

        // Occupy the hunter hut
        var hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        // Place wild animal
        var animal0 = map.placeWildAnimal(point2);

        // Verify that the worker can reach the animal
        assertTrue(animal0.isExactlyAtPoint());

        var meet = false;
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

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a lake of buildable water on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_WATER, map);

        // Place snow next to the lake
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of buildable water and snow
        var point5 = new Point(5, 7);
        var flag = map.placeFlag(player0, point5);

        assertTrue(map.getFlags().contains(flag));
        assertTrue(map.isFlagAtPoint(point5));
        assertEquals(map.getFlagAtPoint(point5), flag);
    }

    @Test
    public void testFlagPointAvailableOnBorderOfBuildableWaterAndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a lake of buildable water on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_WATER, map);

        // Place snow next to the lake
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of buildable water and snow
        var point5 = new Point(5, 7);
        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCanPlaceHeadquarterOnBuildableWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place buildable water on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_WATER, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, BUILDABLE_WATER, map);
        }

        // Verify that a headquarters can be placed on the buildable water
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        assertTrue(map.getBuildings().contains(headquarter));
        assertTrue(map.isBuildingAtPoint(point2));
        assertEquals(map.getBuildingAtPoint(point2), headquarter);
    }


    // Steppe

    @Test
    public void testAvailableFlagOnSteppe() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of steppe on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, STEPPE, map);

        // Verify that there is an available flag point on the steppe
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagOnSteppe() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of steppe on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, STEPPE, map);

        // Verify that it is  possible to place a flag on the steppe
        var flag = map.placeFlag(player0, point1);

        assertTrue(map.getFlags().contains(flag));
        assertTrue(map.isFlagAtPoint(point1));
        assertEquals(map.getFlagAtPoint(point1), flag);
    }

    @Test
    public void testAvailableHouseOnSteppe() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of steppe on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, STEPPE, map);

        // Verify that there is an available house point on the steppe
        assertNotNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCanPlaceHouseOnSteppe() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of steppe on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, STEPPE, map);

        // Verify that it's possible to place a house on the steppe
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(map.getBuildings().contains(woodcutter));
        assertTrue(map.isBuildingAtPoint(point1));
        assertEquals(map.getBuildingAtPoint(point1), woodcutter);
    }

    @Test
    public void testNoAvailableMineOnSteppe() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of steppe on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, STEPPE, map);

        // Verify that there is no available house point on the steppe
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnSteppe() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of steppe on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, STEPPE, map);

        // Verify that it's not possible to place a house on the steppe
        try {
            map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testCanBuildRoadAcrossSteppe() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of steppe on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, STEPPE, map);

        // Place flags
        var point2 = new Point(8, 10);
        var point3 = new Point(12, 10);
        var flag0 = map.placeFlag(player0, point2);
        var flag1 = map.placeFlag(player0, point3);

        // Verify that it's possible to build a road across the steppe
        var road0 = map.placeRoad(player0, point2, point1, point3);

        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossSteppe() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a woodcutter hut on the map
        var point1 = new Point(15, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Surround the woodcutter hut with steppe
        var point2 = new Point(15, 7);
        var point3 = new Point(18, 6);
        var point4 = new Point(18, 4);
        var point5 = new Point(15, 3);
        var point6 = new Point(12, 4);
        var point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, STEPPE, map);
        Utils.surroundPointWithVegetation(point3, STEPPE, map);
        Utils.surroundPointWithVegetation(point4, STEPPE, map);
        Utils.surroundPointWithVegetation(point5, STEPPE, map);
        Utils.surroundPointWithVegetation(point6, STEPPE, map);
        Utils.surroundPointWithVegetation(point7, STEPPE, map);

        // Finish construction of the woodcutter hut
        Utils.constructHouse(woodcutter0);

        // Occupy the woodcutter hut
        var woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Tear down the woodcutter hut
        woodcutter0.tearDown();

        // Verify that the woodcutter worker can go back to the headquarters
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoSteppe() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a steppe on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, STEPPE, map);

        // Place hunter hut
        var point1 = new Point(8, 6);
        var hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        // Finish construction of the hunter hut
        Utils.constructHouse(hunter0);

        // Occupy the hunter hut
        var hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        // Place wild animal
        var animal0 = map.placeWildAnimal(point2);

        // Verify that the worker can reach the animal
        var meet = false;
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

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a steppe on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, STEPPE, map);

        // Place snow next to the steppe
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of steppe and snow
        var point5 = new Point(5, 7);
        var flag = map.placeFlag(player0, point5);

        assertTrue(map.getFlags().contains(flag));
        assertTrue(map.isFlagAtPoint(point5));
        assertEquals(map.getFlagAtPoint(point5), flag);
    }

    @Test
    public void testFlagPointAvailableOnBorderOfSteppeAndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a steppe on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, STEPPE, map);

        // Place snow next to the steppe
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of steppe and snow
        var point5 = new Point(5, 7);
        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCanPlaceHeadquarterOnSteppe() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place steppe on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, STEPPE, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, STEPPE, map);
        }

        // Verify that a headquarters can be placed on the steppe
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        assertTrue(map.getBuildings().contains(headquarter));
        assertTrue(map.isBuildingAtPoint(point2));
        assertEquals(map.getBuildingAtPoint(point2), headquarter);
    }


    // Mountain meadow

    @Test
    public void testAvailableFlagOnMountainMeadow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of mountain meadow on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_MEADOW, map);

        // Verify that there is an available flag point on the mountain meadow
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagOnMountainMeadow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of mountain meadow on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_MEADOW, map);

        // Verify that it is  possible to place a flag on the mountain meadow
        var flag = map.placeFlag(player0, point1);

        assertTrue(map.getFlags().contains(flag));
        assertTrue(map.isFlagAtPoint(point1));
        assertEquals(map.getFlagAtPoint(point1), flag);
    }

    @Test
    public void testAvailableHouseOnMountainMeadow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of mountain meadow on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_MEADOW, map);

        // Verify that there is an available house point on the mountain meadow
        assertNotNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCanPlaceHouseOnMountainMeadow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of mountain meadow on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_MEADOW, map);

        // Verify that it's possible to place a house on the mountain meadow
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(map.getBuildings().contains(woodcutter));
        assertTrue(map.isBuildingAtPoint(point1));
        assertEquals(map.getBuildingAtPoint(point1), woodcutter);
    }

    @Test
    public void testNoAvailableMineOnMountainMeadow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of mountain meadow on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_MEADOW, map);

        // Verify that there is no available house point on the mountain meadow
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnMountainMeadow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of mountain meadow on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_MEADOW, map);

        // Verify that it's not possible to place a house on the mountain meadow
        try {
            map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testCanBuildRoadAcrossMountainMeadow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of mountain meadow on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_MEADOW, map);

        // Place flags
        var point2 = new Point(8, 10);
        var point3 = new Point(12, 10);
        var flag0 = map.placeFlag(player0, point2);
        var flag1 = map.placeFlag(player0, point3);

        // Verify that it's possible to build a road across the mountain meadow
        var road0 = map.placeRoad(player0, point2, point1, point3);

        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossMountainMeadow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a woodcutter hut on the map
        var point1 = new Point(15, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Surround the woodcutter hut with mountain meadow
        var point2 = new Point(15, 7);
        var point3 = new Point(18, 6);
        var point4 = new Point(18, 4);
        var point5 = new Point(15, 3);
        var point6 = new Point(12, 4);
        var point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_MEADOW, map);
        Utils.surroundPointWithVegetation(point3, MOUNTAIN_MEADOW, map);
        Utils.surroundPointWithVegetation(point4, MOUNTAIN_MEADOW, map);
        Utils.surroundPointWithVegetation(point5, MOUNTAIN_MEADOW, map);
        Utils.surroundPointWithVegetation(point6, MOUNTAIN_MEADOW, map);
        Utils.surroundPointWithVegetation(point7, MOUNTAIN_MEADOW, map);

        // Finish construction of the woodcutter hut
        Utils.constructHouse(woodcutter0);

        // Occupy the woodcutter hut
        var woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Tear down the woodcutter hut
        woodcutter0.tearDown();

        // Verify that the woodcutter worker can go back to the headquarters
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoMountainMeadow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a mountain meadow on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_MEADOW, map);

        // Place hunter hut
        var point1 = new Point(8, 6);
        var hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        // Finish construction of the hunter hut
        Utils.constructHouse(hunter0);

        // Occupy the hunter hut
        var hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        // Place wild animal
        var animal0 = map.placeWildAnimal(point2);

        // Verify that the worker can reach the animal
        assertTrue(animal0.isExactlyAtPoint());

        var meet = false;
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

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a mountain meadow on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_MEADOW, map);

        // Place snow next to the mountain meadow
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of mountain meadow and snow
        var point5 = new Point(5, 7);
        var flag = map.placeFlag(player0, point5);

        assertTrue(map.getFlags().contains(flag));
        assertTrue(map.isFlagAtPoint(point5));
        assertEquals(map.getFlagAtPoint(point5), flag);
    }

    @Test
    public void testFlagPointAvailableOnBorderOfMountainMeadowAndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a mountain meadow on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_MEADOW, map);

        // Place snow next to the mountain meadow
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of mountain meadow and snow
        var point5 = new Point(5, 7);
        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCanPlaceHeadquarterOnMountainMeadow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place mountain meadow on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_MEADOW, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, MOUNTAIN_MEADOW, map);
        }

        // Verify that a headquarters can be placed on the mountain meadow
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        assertTrue(map.getBuildings().contains(headquarter));
        assertTrue(map.isBuildingAtPoint(point2));
        assertEquals(map.getBuildingAtPoint(point2), headquarter);
    }

    // Buildable mountain

    @Test
    public void testAvailableFlagOnBuildableMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of buildable mountain on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, BUILDABLE_MOUNTAIN, map);

        // Verify that there is an available flag point on the buildable mountain
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagOnBuildableMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of buildable mountain on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, BUILDABLE_MOUNTAIN, map);

        // Verify that it is  possible to place a flag on the buildable mountain
        var flag = map.placeFlag(player0, point1);

        assertTrue(map.getFlags().contains(flag));
        assertTrue(map.isFlagAtPoint(point1));
        assertEquals(map.getFlagAtPoint(point1), flag);
    }

    @Test
    public void testAvailableHouseOnBuildableMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of buildable mountain on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, BUILDABLE_MOUNTAIN, map);

        // Verify that there is an available house point on the buildable mountain
        assertNotNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCanPlaceHouseOnBuildableMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of buildable mountain on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, BUILDABLE_MOUNTAIN, map);

        // Verify that it's possible to place a house on the buildable mountain
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(map.getBuildings().contains(woodcutter));
        assertTrue(map.isBuildingAtPoint(point1));
        assertEquals(map.getBuildingAtPoint(point1), woodcutter);
    }

    @Test
    public void testNoAvailableMineOnBuildableMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of buildable mountain on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, BUILDABLE_MOUNTAIN, map);

        // Verify that there is no available house point on the buildable mountain
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnBuildableMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of buildable mountain on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, BUILDABLE_MOUNTAIN, map);

        // Verify that it's not possible to place a house on the buildable mountain
        try {
            map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testCanBuildRoadAcrossBuildableMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of buildable mountain on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, BUILDABLE_MOUNTAIN, map);

        // Place flags
        var point2 = new Point(8, 10);
        var point3 = new Point(12, 10);
        var flag0 = map.placeFlag(player0, point2);
        var flag1 = map.placeFlag(player0, point3);

        // Verify that it's possible to build a road across the buildable mountain
        var road0 = map.placeRoad(player0, point2, point1, point3);

        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossBuildableMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a woodcutter hut on the map
        var point1 = new Point(15, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Surround the woodcutter hut with buildable mountain
        var point2 = new Point(15, 7);
        var point3 = new Point(18, 6);
        var point4 = new Point(18, 4);
        var point5 = new Point(15, 3);
        var point6 = new Point(12, 4);
        var point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point3, BUILDABLE_MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point4, BUILDABLE_MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point5, BUILDABLE_MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point6, BUILDABLE_MOUNTAIN, map);
        Utils.surroundPointWithVegetation(point7, BUILDABLE_MOUNTAIN, map);

        // Finish construction of the woodcutter hut
        Utils.constructHouse(woodcutter0);

        // Occupy the woodcutter hut
        var woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Tear down the woodcutter hut
        woodcutter0.tearDown();

        // Verify that the woodcutter worker can go back to the headquarters
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoBuildableMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a buildable mountain on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_MOUNTAIN, map);

        // Place hunter hut
        var point1 = new Point(8, 6);
        var hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        // Finish construction of the hunter hut
        Utils.constructHouse(hunter0);

        // Occupy the hunter hut
        var hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        // Place wild animal
        var animal0 = map.placeWildAnimal(point2);

        // Verify that the worker can reach the animal
        var meet = false;
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

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a buildable mountain on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_MOUNTAIN, map);

        // Place snow next to the buildable mountain
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of buildable mountain and snow
        var point5 = new Point(5, 7);
        var flag = map.placeFlag(player0, point5);

        assertTrue(map.getFlags().contains(flag));
        assertTrue(map.isFlagAtPoint(point5));
        assertEquals(map.getFlagAtPoint(point5), flag);
    }

    @Test
    public void testFlagPointAvailableOnBorderOfBuildableMountainAndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a buildable mountain on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_MOUNTAIN, map);

        // Place snow next to the buildable mountain
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of buildable mountain and snow
        var point5 = new Point(5, 7);
        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCanPlaceHeadquarterOnBuildableMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place buildable mountain on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_MOUNTAIN, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, BUILDABLE_MOUNTAIN, map);
        }

        // Verify that a headquarters can be placed on the buildable mountain
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        assertTrue(map.getBuildings().contains(headquarter));
        assertTrue(map.isBuildingAtPoint(point2));
        assertEquals(map.getBuildingAtPoint(point2), headquarter);
    }

    @Test
    public void testAvailableLargeHouseOnBuildableMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point1 = new Point(13, 13);
        map.placeBuilding(new Headquarter(player0), point1);

        // Place a buildable mountain on the map
        var point2 = new Point(6, 6);
        Utils.surroundPointWithVegetation(point2, BUILDABLE_MOUNTAIN, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, BUILDABLE_MOUNTAIN, map);
        }

        // Verify that there is an available point for a large house on the buildable mountain
        try {
            assertEquals(map.isAvailableHousePoint(player0, point2), Size.LARGE);
        } catch (Exception e) { }
    }


    // Lava

    @Test
    public void testNoAvailableFlagOnLava() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of lava on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, LAVA_1, map);

        // Verify that there is no available flag point in the lava
        assertFalse(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCannotPlaceFlagOnLava() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of lava on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, LAVA_1, map);

        // Verify that it is not possible to place a flag on the lava
        try {
            map.placeFlag(player0, point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableHouseOnLava() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of lava on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, LAVA_1, map);

        // Verify that there is no available house point on the lava
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceHouseOnLava() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of lava on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, LAVA_1, map);

        // Verify that it's not possible to place a house on the lava
        try {
            map.placeBuilding(new Woodcutter(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableMineOnLava() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of lava on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, LAVA_1, map);

        // Verify that there is no available house point on the lava
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnLava() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of lava on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, LAVA_1, map);

        // Verify that it's not possible to place a house on the lava
        try {
            map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testCannotBuildRoadAcrossLava() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of lava on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, LAVA_1, map);

        // Place flags
        var point2 = new Point(8, 10);
        var point3 = new Point(12, 10);
        var flag0 = map.placeFlag(player0, point2);
        var flag1 = map.placeFlag(player0, point3);

        // Verify that it's not possible to Build a road across the lava
        try {
            var road0 = map.placeRoad(player0, point2, point1, point3);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableHouseOnBorderOfLava() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of lava on the map
        var point1 = new Point(10, 10);
        var point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, LAVA_1, map);
        Utils.surroundPointWithVegetation(point2, LAVA_1, map);

        // Verify that there is no available house point on the border of the lava
        var point3 = new Point(11, 11);
        assertNull(map.isAvailableHousePoint(player0, point3));
    }

    @Test
    public void testCannotPlaceHouseOnBorderOfLava() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of lava on the map
        var point1 = new Point(10, 10);
        var point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, LAVA_1, map);
        Utils.surroundPointWithVegetation(point2, LAVA_1, map);

        // Verify that it's not possible to place a house on the border of the lava
        var point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testWorkerCannotWalkOffroadAcrossLava() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a woodcutter hut on the map
        var point1 = new Point(15, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Surround the woodcutter hut with water
        var point2 = new Point(15, 7);
        var point3 = new Point(18, 6);
        var point4 = new Point(18, 4);
        var point5 = new Point(15, 3);
        var point6 = new Point(12, 4);
        var point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, LAVA_1, map);
        Utils.surroundPointWithVegetation(point3, LAVA_1, map);
        Utils.surroundPointWithVegetation(point4, LAVA_1, map);
        Utils.surroundPointWithVegetation(point5, LAVA_1, map);
        Utils.surroundPointWithVegetation(point6, LAVA_1, map);
        Utils.surroundPointWithVegetation(point7, LAVA_1, map);

        // Finish construction of the woodcutter hut
        Utils.constructHouse(woodcutter0);

        // Occupy the woodcutter hut
        var woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Tear down the woodcutter hut
        woodcutter0.tearDown();

        // Verify that the woodcutter worker can't go back to the headquarters
        assertEquals(map.getVegetationAbove(point6), LAVA_1);
        assertEquals(map.getVegetationBelow(point7), LAVA_1);
        assertNotEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        for (int i = 0; i < 1000; i++) {
            assertNotEquals(woodcutterWorker.getPosition(), headquarter0.getPosition());

            map.stepTime();
        }
    }

    @Test
    public void testCannotPlaceFlagOnBorderOfLavaAndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place lava on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, LAVA_1, map);

        // Place snow next to the swamp
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's not possible to place a flag on the border of lava and snow
        var point5 = new Point(5, 7);

        try {
            map.placeFlag(player0, point5);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoFlagPointAvailableOnBorderOfLavaAndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a swamp on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, LAVA_1, map);

        // Place snow next to the swamp
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's not possible to place a flag on the border of lava and snow
        var point5 = new Point(5, 7);
        assertFalse(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCannotPlaceHeadquarterOnLava() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place lava on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, LAVA_1, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, LAVA_1, map);
        }

        // Verify that a headquarters can be placed on the lava
        try {
            map.placeBuilding(new Headquarter(player0), point2);

            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testNoAvailableLargeHouseOnLava() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point1 = new Point(13, 13);
        map.placeBuilding(new Headquarter(player0), point1);

        // Place lava on the map
        var point2 = new Point(6, 6);
        Utils.surroundPointWithVegetation(point2, LAVA_1, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, LAVA_1, map);
        }

        // Verify that there is no available point for a large house on the lava
        try {
            assertNull(map.isAvailableHousePoint(player0, point2));
        } catch (Exception e) {
        }
    }


    // Deep water

    @Test
    public void testNoAvailableFlagOnDeepWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of deep water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER_2, map);

        // Verify that there is no available flag point in the deep water
        assertFalse(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCannotPlaceFlagOnDeepWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of deep water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER_2, map);

        // Verify that it is not possible to place a flag on the deep water
        try {
            map.placeFlag(player0, point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableHouseOnDeepWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of deep water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER_2, map);

        // Verify that there is no available house point on the deep water
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceHouseOnDeepWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of deep water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER_2, map);

        // Verify that it's not possible to place a house on the deep water
        try {
            map.placeBuilding(new Woodcutter(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableMineOnDeepWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of deep water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, BUILDABLE_WATER, map);

        // Verify that there is no available house point on the deep water
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnDeepWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of deep water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, BUILDABLE_WATER, map);

        // Verify that it's not possible to place a house on the deep water
        try {
            map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testCannotBuildRoadAcrossDeepWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of deep water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER_2, map);

        // Place flags
        var point2 = new Point(8, 10);
        var point3 = new Point(12, 10);
        var flag0 = map.placeFlag(player0, point2);
        var flag1 = map.placeFlag(player0, point3);

        // Verify that it's not possible to Build a road across the deep water
        try {
            var road0 = map.placeRoad(player0, point2, point1, point3);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableHouseOnBorderOfDeepWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of deep water on the map
        var point1 = new Point(10, 10);
        var point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, WATER_2, map);
        Utils.surroundPointWithVegetation(point2, WATER_2, map);

        // Verify that there is no available house point on the border of the deep water
        var point3 = new Point(11, 11);

        assertNull(map.isAvailableHousePoint(player0, point3));
    }

    @Test
    public void testCannotPlaceHouseOnBorderOfDeepWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of deep water on the map
        var point1 = new Point(10, 10);
        var point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, WATER_2, map);
        Utils.surroundPointWithVegetation(point2, WATER_2, map);

        // Verify that it's not possible to place a house on the border of the deep water
        var point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testWorkerCannotWalkOffroadAcrossDeepWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a woodcutter hut on the map
        var point1 = new Point(15, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Surround the woodcutter hut with water
        var point2 = new Point(15, 7);
        var point3 = new Point(18, 6);
        var point4 = new Point(18, 4);
        var point5 = new Point(15, 3);
        var point6 = new Point(12, 4);
        var point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, WATER_2, map);
        Utils.surroundPointWithVegetation(point3, WATER_2, map);
        Utils.surroundPointWithVegetation(point4, WATER_2, map);
        Utils.surroundPointWithVegetation(point5, WATER_2, map);
        Utils.surroundPointWithVegetation(point6, WATER_2, map);
        Utils.surroundPointWithVegetation(point7, WATER_2, map);

        // Finish construction of the woodcutter hut
        Utils.constructHouse(woodcutter0);

        // Occupy the woodcutter hut
        var woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Tear down the woodcutter hut
        woodcutter0.tearDown();

        // Verify that the woodcutter worker can't go back to the headquarters
        assertEquals(map.getVegetationAbove(point6), WATER_2);
        assertEquals(map.getVegetationBelow(point7), WATER_2);
        assertNotEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        for (int i = 0; i < 1000; i++) {

            assertNotEquals(woodcutterWorker.getPosition(), headquarter0.getPosition());

            map.stepTime();
        }
    }

    @Test
    public void testCannotPlaceFlagOnBorderOfDeepWaterAndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a lake with deep water on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, WATER_2, map);

        // Place snow next to the lake
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's not possible to place a flag on the border of deep water and snow
        var point5 = new Point(5, 7);

        try {
            map.placeFlag(player0, point5);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoFlagPointAvailableOnBorderOfDeepWaterAndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a lake with deep water on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, WATER_2, map);

        // Place snow next to the lake
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's not possible to place a flag on the border of deep water and snow
        var point5 = new Point(5, 7);
        assertFalse(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCannotPlaceHeadquarterOnDeepWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place deep water on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, WATER_2, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point2, WATER_2, map);
        }

        // Verify that a headquarters cannot be placed on the deep water
        try {
            map.placeBuilding(new Headquarter(player0), point2);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableLargeHouseOnDeepWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point1 = new Point(13, 13);
        map.placeBuilding(new Headquarter(player0), point1);

        // Place deep water on the map
        var point2 = new Point(6, 6);
        Utils.surroundPointWithVegetation(point2, WATER_2, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, WATER_2, map);
        }

        // Verify that there is no available point for a large house on the deep water
        try {
            assertNull(map.isAvailableHousePoint(player0, point2));
        } catch (Exception e) { }
    }


    // Regular water

    @Test
    public void testNoAvailableFlagOnWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of Water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);

        // Verify that there is no available flag point in the water
        assertFalse(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCannotPlaceFlagOnWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);

        // Verify that it is not possible to place a flag on the water
        try {
            map.placeFlag(player0, point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableHouseOnWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);

        // Verify that there is no available house point on the water
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceHouseOnWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);

        // Verify that it's not possible to place a house on the water
        try {
            map.placeBuilding(new Woodcutter(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableMineOnWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);

        // Verify that there is no available house point on the water
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);

        // Verify that it's not possible to place a house on the water
        try {
            map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testCannotBuildRoadAcrossWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of water on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);

        // Place flags
        var point2 = new Point(8, 10);
        var point3 = new Point(12, 10);
        var flag0 = map.placeFlag(player0, point2);
        var flag1 = map.placeFlag(player0, point3);

        // Verify that it's not possible to Build a road across the water
        try {
            var road0 = map.placeRoad(player0, point2, point1, point3);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableHouseOnBorderOfWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of water on the map
        var point1 = new Point(10, 10);
        var point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        // Verify that there is no available house point on the border of the water
        var point3 = new Point(11, 11);

        assertNull(map.isAvailableHousePoint(player0, point3));
    }

    @Test
    public void testCannotPlaceHouseOnBorderOfWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of water on the map
        var point1 = new Point(10, 10);
        var point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, WATER, map);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        // Verify that it's not possible to place a house on the border of the water
        var point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testWorkerCannotWalkOffroadAcrossWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a woodcutter hut on the map
        var point1 = new Point(15, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Surround the woodcutter hut with water
        var point2 = new Point(15, 7);
        var point3 = new Point(18, 6);
        var point4 = new Point(18, 4);
        var point5 = new Point(15, 3);
        var point6 = new Point(12, 4);
        var point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, WATER, map);
        Utils.surroundPointWithVegetation(point3, WATER, map);
        Utils.surroundPointWithVegetation(point4, WATER, map);
        Utils.surroundPointWithVegetation(point5, WATER, map);
        Utils.surroundPointWithVegetation(point6, WATER, map);
        Utils.surroundPointWithVegetation(point7, WATER, map);

        // Finish construction of the woodcutter hut
        Utils.constructHouse(woodcutter0);

        // Occupy the woodcutter hut
        var woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Tear down the woodcutter hut
        woodcutter0.tearDown();

        // Verify that the woodcutter worker can't go back to the headquarters
        assertEquals(map.getVegetationAbove(point6), WATER);
        assertEquals(map.getVegetationBelow(point7), WATER);
        assertNotEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        for (int i = 0; i < 1000; i++) {
            assertNotEquals(woodcutterWorker.getPosition(), headquarter0.getPosition());

            map.stepTime();
        }
    }

    @Test
    public void testCannotPlaceFlagOnBorderOfWaterAndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a lake on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        // Place snow next to the lake
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's not possible to place a flag on the border of water and snow
        var point5 = new Point(5, 7);

        try {
            map.placeFlag(player0, point5);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoFlagPointAvailableOnBorderOfWaterAndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a lake on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        // Place snow next to the lake
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's not possible to place a flag on the border of water and snow
        var point5 = new Point(5, 7);

        assertFalse(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCannotPlaceHeadquarterOnWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place water on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, WATER, map);
        }

        // Verify that a headquarters cannot be placed on the water
        try {
            map.placeBuilding(new Headquarter(player0), point2);

            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testNoAvailableLargeHouseOnWater() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point1 = new Point(13, 13);
        map.placeBuilding(new Headquarter(player0), point1);

        // Place water on the map
        var point2 = new Point(6, 6);
        Utils.surroundPointWithVegetation(point2, WATER, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, WATER, map);
        }

        // Verify that there is no available point for a large house on the water
        try {
            assertNull(map.isAvailableHousePoint(player0, point2));
        } catch (Exception e) { }
    }


    // Swamp

    @Test
    public void testNoAvailableFlagOnSwamp() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of swamp on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);

        // Verify that there is no available flag point in the swamp
        assertFalse(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCannotPlaceFlagOnSwamp() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of swamp on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);

        // Verify that it is not possible to place a flag on the swamp
        try {
            map.placeFlag(player0, point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableHouseOnSwamp() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of swamp on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);

        // Verify that there is no available house point on the swamp
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceHouseOnSwamp() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of swamp on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);

        // Verify that it's not possible to place a house on the swamp
        try {
            map.placeBuilding(new Woodcutter(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableMineOnSwamp() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of swamp on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);

        // Verify that there is no available house point on the swamp
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineOnSwamp() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of swamp on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);

        // Verify that it's not possible to place a house on the swamp
        try {
            map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testCannotBuildRoadAcrossSwamp() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of swamp on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);

        // Place flags
        var point2 = new Point(8, 10);
        var point3 = new Point(12, 10);
        var flag0 = map.placeFlag(player0, point2);
        var flag1 = map.placeFlag(player0, point3);

        // Verify that it's not possible to Build a road across the swamp
        try {
            var road0 = map.placeRoad(player0, point2, point1, point3);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableHouseOnBorderOfSwamp() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of swamp on the map
        var point1 = new Point(10, 10);
        var point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);
        Utils.surroundPointWithVegetation(point2, SWAMP, map);

        // Verify that there is no available house point on the border of the swamp
        var point3 = new Point(11, 11);

        assertNull(map.isAvailableHousePoint(player0, point3));
    }

    @Test
    public void testCannotPlaceHouseOnBorderOfSwamp() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small patch of swamp on the map
        var point1 = new Point(10, 10);
        var point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);
        Utils.surroundPointWithVegetation(point2, SWAMP, map);

        // Verify that it's not possible to place a house on the border of the swamp
        var point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testWorkerCannotWalkOffroadAcrossSwamp() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a woodcutter hut on the map
        var point1 = new Point(15, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Surround the woodcutter hut with water
        var point2 = new Point(15, 7);
        var point3 = new Point(18, 6);
        var point4 = new Point(18, 4);
        var point5 = new Point(15, 3);
        var point6 = new Point(12, 4);
        var point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, SWAMP, map);
        Utils.surroundPointWithVegetation(point3, SWAMP, map);
        Utils.surroundPointWithVegetation(point4, SWAMP, map);
        Utils.surroundPointWithVegetation(point5, SWAMP, map);
        Utils.surroundPointWithVegetation(point6, SWAMP, map);
        Utils.surroundPointWithVegetation(point7, SWAMP, map);

        // Finish construction of the woodcutter hut
        Utils.constructHouse(woodcutter0);

        // Occupy the woodcutter hut
        var woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Tear down the woodcutter hut
        woodcutter0.tearDown();

        // Verify that the woodcutter worker can't go back to the headquarters
        assertEquals(map.getVegetationAbove(point6), SWAMP);
        assertEquals(map.getVegetationBelow(point7), SWAMP);
        assertNotEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        for (int i = 0; i < 1000; i++) {
            assertNotEquals(woodcutterWorker.getPosition(), headquarter0.getPosition());

            map.stepTime();
        }
    }

    @Test
    public void testWorkerCannotGoIntoSwamp() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a swamp on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SWAMP, map);

        // Place tree
        var animal0 = map.placeWildAnimal(point2);

        // Place woodcutter
        var point1 = new Point(8, 6);
        var woodcutter0 = map.placeBuilding(new HunterHut(player0), point1);

        // Finish construction of the woodcutter
        Utils.constructHouse(woodcutter0);

        // Occupy the woodcutter
        var hunter = Utils.occupyBuilding(new Hunter(player0, map), woodcutter0);

        // Verify that the worker can't reach the tree
        for (int i = 0; i < 1000; i++) {
            woodcutter0.getWorker().stepTime();

            assertNotEquals(hunter.getPosition(), animal0.getPosition());
        }
    }

    @Test
    public void testCannotPlaceFlagOnBorderOfSwampAndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a swamp on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SWAMP, map);

        // Place snow next to the swamp
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's not possible to place a flag on the border of swamp and snow
        var point5 = new Point(5, 7);

        try {
            map.placeFlag(player0, point5);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoFlagPointAvailableOnBorderOfSwampAndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a swamp on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SWAMP, map);

        // Place snow next to the swamp
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's not possible to place a flag on the border of swamp and snow
        var point5 = new Point(5, 7);

        assertFalse(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCannotPlaceHeadquarterOnSwamp() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place swamp on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, SWAMP, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, SWAMP, map);
        }

        // Verify that a headquarters can be placed on the swamp
        try {
            map.placeBuilding(new Headquarter(player0), point2);

            fail();
        } catch (Exception e) {

        }
    }

    @Test
    public void testNoAvailableLargeHouseOnSwamp() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point1 = new Point(13, 13);
        map.placeBuilding(new Headquarter(player0), point1);

        // Place swamp on the map
        var point2 = new Point(6, 6);
        Utils.surroundPointWithVegetation(point2, SWAMP, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, SWAMP, map);
        }

        // Verify that there is no available point for a large house on the swamp
        try {
            assertNull(map.isAvailableHousePoint(player0, point2));
        } catch (Exception e) { }
    }


    // Magenta
    @Test
    public void testAvailableFlagInMagenta() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small magenta on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);

        // Verify that there is an available flag point in the magenta
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagInMagenta() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small magenta on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);

        // Verify that it is possible to place a flag in the magenta
        var flag = map.placeFlag(player0, point1);

        assertTrue(map.getFlags().contains(flag));
        assertTrue(map.isFlagAtPoint(point1));
        assertEquals(map.getFlagAtPoint(point1), flag);
    }

    @Test
    public void testNoAvailableHouseInMagenta() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small magenta on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);

        // Verify that there is no available house point in the magenta
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceHouseInMagenta() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small magenta on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);

        // Verify that it's not possible to place a house in the magenta
        try {
            map.placeBuilding(new Woodcutter(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableMineInMagenta() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small magenta on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);

        // Verify that there is no available house point in the magenta
        assertFalse(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceMineInMagenta() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small magenta on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);

        // Verify that it's not possible to place a house in the magenta
        try {
            map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testCanBuildRoadAcrossMagenta() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small magenta on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);

        // Place flags
        var point2 = new Point(8, 10);
        var point3 = new Point(12, 10);
        var flag0 = map.placeFlag(player0, point2);
        var flag1 = map.placeFlag(player0, point3);

        // Verify that it's possible to Build a road across the magenta
        var road0 = map.placeRoad(player0, point2, point1, point3);

        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testNoAvailableHouseOnBorderOfMagenta() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small magenta on the map
        var point1 = new Point(10, 10);
        var point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);
        Utils.surroundPointWithVegetation(point2, MAGENTA, map);

        // Verify that there is no available house point on the border of the magenta
        var point3 = new Point(11, 11);
        assertNull(map.isAvailableHousePoint(player0, point3));
    }

    @Test
    public void testCannotPlaceHouseOnBorderOfMagenta() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small magenta on the map
        var point1 = new Point(10, 10);
        var point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, MAGENTA, map);
        Utils.surroundPointWithVegetation(point2, MAGENTA, map);

        // Verify that it's not possible to place a house on the border of the magenta
        var point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossMagenta() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a woodcutter hut on the map
        var point1 = new Point(15, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Surround the woodcutter hut with magenta
        var point2 = new Point(15, 7);
        var point3 = new Point(18, 6);
        var point4 = new Point(18, 4);
        var point5 = new Point(15, 3);
        var point6 = new Point(12, 4);
        var point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, MAGENTA, map);
        Utils.surroundPointWithVegetation(point3, MAGENTA, map);
        Utils.surroundPointWithVegetation(point4, MAGENTA, map);
        Utils.surroundPointWithVegetation(point5, MAGENTA, map);
        Utils.surroundPointWithVegetation(point6, MAGENTA, map);
        Utils.surroundPointWithVegetation(point7, MAGENTA, map);

        // Finish construction of the woodcutter hut
        Utils.constructHouse(woodcutter0);

        // Occupy the woodcutter hut
        var woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Tear down the woodcutter hut
        woodcutter0.tearDown();

        // Verify that the woodcutter worker can go back to the headquarters
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoMagenta() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place magenta on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MAGENTA, map);

        // Place hunter hut
        var point1 = new Point(8, 6);
        var hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        // Finish construction of the hunter hut
        Utils.constructHouse(hunter0);

        // Occupy the hunter hut
        var hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        // Place wild animal
        var animal0 = map.placeWildAnimal(point2);

        // Verify that the worker can reach the animal
        assertTrue(animal0.isExactlyAtPoint());

        var meet = false;
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

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place magenta on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MAGENTA, map);

        // Place snow next to the magenta
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of magenta and snow
        var point5 = new Point(5, 7);
        var flag = map.placeFlag(player0, point5);

        assertTrue(map.getFlags().contains(flag));
        assertTrue(map.isFlagAtPoint(point5));
        assertEquals(map.getFlagAtPoint(point5), flag);
    }

    @Test
    public void testFlagPointAvailableOnBorderOfMagentaAndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place magenta on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MAGENTA, map);

        // Place snow next to the magenta
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of magenta and snow
        var point5 = new Point(5, 7);

        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCannotPlaceHeadquarterOnMagenta() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place magenta on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MAGENTA, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, MAGENTA, map);
        }

        // Verify that a headquarters cannot be placed on the magenta
        try {
            map.placeBuilding(new Headquarter(player0), point2);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableLargeHouseOnMagenta() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point1 = new Point(13, 13);
        map.placeBuilding(new Headquarter(player0), point1);

        // Place magenta on the map
        var point2 = new Point(6, 6);
        Utils.surroundPointWithVegetation(point2, MAGENTA, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, MAGENTA, map);
        }

        // Verify that there is no available point for a large house on the magenta
        try {
            assertNull(map.isAvailableHousePoint(player0, point2));
        } catch (Exception e) { }
    }


    // Regular mountain (that can be mined)
    @Test
    public void testAvailableFlagInMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small mountain on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_1, map);

        // Verify that there is an available flag point in the mountain
        assertTrue(map.isAvailableFlagPoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagInMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small mountain on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_1, map);

        // Verify that it is possible to place a flag in the mountain
        var flag = map.placeFlag(player0, point1);

        assertTrue(map.getFlags().contains(flag));
        assertTrue(map.isFlagAtPoint(point1));
        assertEquals(map.getFlagAtPoint(point1), flag);
    }

    @Test
    public void testNoAvailableHouseInMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small mountain on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_1, map);

        // Verify that there is no available house point in the mountain
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCannotPlaceHouseInMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small mountain on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_1, map);

        // Verify that it's not possible to place a house in the mountain
        try {
            map.placeBuilding(new Woodcutter(player0), point1);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testAvailableMineInMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small mountain on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_1, map);

        // Verify that there is no available house point in the mountain
        assertTrue(map.isAvailableMinePoint(player0, point1));
    }

    @Test
    public void testCanPlaceMineInMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small mountain on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_1, map);

        // Verify that it's possible to place a house in the mountain
        var goldMine = map.placeBuilding(new GoldMine(player0), point1);

        assertTrue(map.getBuildings().contains(goldMine));
        assertTrue(map.isBuildingAtPoint(point1));
        assertEquals(map.getBuildingAtPoint(point1), goldMine);
    }

    @Test
    public void testCanBuildRoadAcrossMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small mountain on the map
        var point1 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_1, map);

        // Place flags
        var point2 = new Point(8, 10);
        var point3 = new Point(12, 10);
        var flag0 = map.placeFlag(player0, point2);
        var flag1 = map.placeFlag(player0, point3);

        // Verify that it's possible to Build a road across the mountain
        var road0 = map.placeRoad(player0, point2, point1, point3);

        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testNoAvailableHouseOnBorderOfMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small mountain on the map
        var point1 = new Point(10, 10);
        var point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_1, map);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_1, map);

        // Verify that there is no available house point on the border of the mountain
        var point3 = new Point(11, 11);

        assertNull(map.isAvailableHousePoint(player0, point3));
    }

    @Test
    public void testCannotPlaceHouseOnBorderOfMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        // Put a small mountain on the map
        var point1 = new Point(10, 10);
        var point2 = new Point(12, 10);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_1, map);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_1, map);

        // Verify that it's not possible to place a house on the border of the mountain
        var point3 = new Point(11, 11);
        try {
            map.placeBuilding(new Woodcutter(player0), point3);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testWorkerCanWalkOffroadAcrossMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a woodcutter hut on the map
        var point1 = new Point(15, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Surround the woodcutter hut with mountains
        var point2 = new Point(15, 7);
        var point3 = new Point(18, 6);
        var point4 = new Point(18, 4);
        var point5 = new Point(15, 3);
        var point6 = new Point(12, 4);
        var point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_1, map);
        Utils.surroundPointWithVegetation(point3, MOUNTAIN_1, map);
        Utils.surroundPointWithVegetation(point4, MOUNTAIN_1, map);
        Utils.surroundPointWithVegetation(point5, MOUNTAIN_1, map);
        Utils.surroundPointWithVegetation(point6, MOUNTAIN_1, map);
        Utils.surroundPointWithVegetation(point7, MOUNTAIN_1, map);

        // Finish construction of the woodcutter hut
        Utils.constructHouse(woodcutter0);

        // Occupy the woodcutter hut
        var woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Tear down the woodcutter hut
        woodcutter0.tearDown();

        // Verify that the woodcutter worker can go back to the headquarters
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerCanGoIntoMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a mountain on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_1, map);

        // Place hunter hut
        var point1 = new Point(8, 6);
        var hunter0 = map.placeBuilding(new HunterHut(player0), point1);

        // Finish construction of the hunter hut
        Utils.constructHouse(hunter0);

        // Occupy the hunter hut
        var hunter = Utils.occupyBuilding(new Hunter(player0, map), hunter0);

        // Place wild animal
        var animal0 = map.placeWildAnimal(point2);

        // Verify that the worker can reach the animal
        assertTrue(animal0.isExactlyAtPoint());

        var meet = false;
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

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a mountain on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_1, map);

        // Place snow next to the mountain
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of mountain and snow
        var point5 = new Point(5, 7);
        var flag = map.placeFlag(player0, point5);

        assertTrue(map.getFlags().contains(flag));
        assertTrue(map.isFlagAtPoint(point5));
        assertEquals(map.getFlagAtPoint(point5), flag);
    }

    @Test
    public void testFlagPointAvailableOnBorderOfMountainAndSnow() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place a mountain on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_1, map);

        // Place snow next to the mountain
        var point3 = new Point(4, 8);
        var point4 = new Point(7, 7);
        Utils.surroundPointWithVegetation(point3, SNOW, map);
        Utils.surroundPointWithVegetation(point4, SNOW, map);

        // Verify that it's possible to place a flag on the border of mountain and snow
        var point5 = new Point(5, 7);

        assertTrue(map.isAvailableFlagPoint(player0, point5));
    }

    @Test
    public void testCannotPlaceHeadquarterOnMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place mountain on the map
        var point2 = new Point(4, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_1, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, MOUNTAIN_1, map);
        }

        // Verify that a headquarters cannot be placed on the mountain
        try {
            map.placeBuilding(new Headquarter(player0), point2);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testNoAvailableLargeHouseOnMountain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point1 = new Point(13, 13);
        map.placeBuilding(new Headquarter(player0), point1);

        // Place mountain on the map
        var point2 = new Point(6, 6);
        Utils.surroundPointWithVegetation(point2, MOUNTAIN_1, map);

        for (var point : point2.getAdjacentPoints()) {
            Utils.surroundPointWithVegetation(point, MOUNTAIN_1, map);
        }

        // Verify that there is no available point for a large house on the mountain
        try {
            assertNull(map.isAvailableHousePoint(player0, point2));
        } catch (Exception e) { }
    }
}
