package org.appland.settlers.test;

import org.appland.settlers.model.DetailedVegetation;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Well;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.junit.Assert.*;

public class TestDetailedVegetation {

    /*
      TODO:
         - Test can/cannot build house on - done
         - Test can/cannot build flag on - done
         - Test can/cannot build road on - done
         - Test can/cannot build mine on - done
         - Test can/cannot build flag and road at edge of
         - Test can/cannot place crop on
         - Available house
         - Available flag
         - Available mine
     */

    @Test
    public void testSetAndGetDetailedVegetationAroundPoint() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Verify that each detailed vegetation can be set and read */
        Point point0 = new Point(10, 20);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_1);
        map.setDetailedVegetationAbove(point0, DetailedVegetation.MOUNTAIN_2);
        map.setDetailedVegetationUpRight(point0, DetailedVegetation.MOUNTAIN_3);
        map.setDetailedVegetationDownRight(point0, DetailedVegetation.MOUNTAIN_4);
        map.setDetailedVegetationBelow(point0, DetailedVegetation.MEADOW_1);
        map.setDetailedVegetationDownLeft(point0, DetailedVegetation.MEADOW_2);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MOUNTAIN_1);
        assertEquals(map.getDetailedVegetationAbove(point0), DetailedVegetation.MOUNTAIN_2);
        assertEquals(map.getDetailedVegetationUpRight(point0), DetailedVegetation.MOUNTAIN_3);
        assertEquals(map.getDetailedVegetationDownRight(point0), DetailedVegetation.MOUNTAIN_4);
        assertEquals(map.getDetailedVegetationBelow(point0), DetailedVegetation.MEADOW_1);
        assertEquals(map.getDetailedVegetationDownLeft(point0), DetailedVegetation.MEADOW_2);
    }

    @Test
    public void testSetAndGetEachDetailedVegetationType() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Verify that each detailed vegetation can be set and read */
        Point point0 = new Point(10, 20);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.SAVANNAH);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.SAVANNAH);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_1);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MOUNTAIN_1);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.SNOW);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.SNOW);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.SWAMP);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.SWAMP);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.DESERT_1);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.DESERT_1);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.WATER);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.WATER);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.BUILDABLE_WATER);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.BUILDABLE_WATER);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.DESERT_2);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.DESERT_2);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MEADOW_1);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MEADOW_1);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MEADOW_2);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MEADOW_2);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MEADOW_3);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MEADOW_3);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_2);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MOUNTAIN_2);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_3);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MOUNTAIN_3);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_4);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MOUNTAIN_4);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.STEPPE);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.STEPPE);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.FLOWER_MEADOW);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.FLOWER_MEADOW);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.LAVA);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.LAVA);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MAGENTA);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MAGENTA);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_MEADOW);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MOUNTAIN_MEADOW);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.WATER_2);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.WATER_2);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.LAVA_2);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.LAVA_2);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.LAVA_3);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.LAVA_3);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.LAVA_4);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.LAVA_4);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.BUILDABLE_MOUNTAIN);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.BUILDABLE_MOUNTAIN);
    }

    @Test
    public void testCanBuildHouseOnSavannah() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place savannah */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.SAVANNAH, map);

        /* Verify that it's possible to place well */
        Well well0 = map.placeBuilding(new Well(player0), point1);

        assertTrue(map.isBuildingAtPoint(point1));

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(well0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(well0);
    }

    @Test
    public void testCannotBuildHouseOnMountain1() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mountain 1 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MOUNTAIN_1, map);

        /* Verify that it's not possible to place well */
        try {
            Well well0 = map.placeBuilding(new Well(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildHouseOnSnow() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place snow */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.SNOW, map);

        /* Verify that it's not possible to place well */
        try {
            Well well0 = map.placeBuilding(new Well(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildHouseOnSwamp() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place swamp */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.SWAMP, map);

        /* Verify that it's not possible to place well */
        try {
            Well well0 = map.placeBuilding(new Well(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildHouseOnDesert1() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place desert 1 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.DESERT_1, map);

        /* Verify that it's not possible to place well */
        try {
            Well well0 = map.placeBuilding(new Well(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildHouseOnWater() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place water */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.WATER, map);

        /* Verify that it's not possible to place well */
        try {
            Well well0 = map.placeBuilding(new Well(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCanBuildHouseOnBuildableWater() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place buildable water */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.BUILDABLE_WATER, map);

        /* Verify that it's possible to place well */
        Well well0 = map.placeBuilding(new Well(player0), point1);

        assertTrue(map.isBuildingAtPoint(point1));

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(well0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(well0);
    }

    @Test
    public void testCannotBuildHouseOnDesert2() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place desert 2 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.DESERT_2, map);

        /* Verify that it's not possible to place well */
        try {
            Well well0 = map.placeBuilding(new Well(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCanBuildHouseOnMeadow1() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place meadow 1 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MEADOW_1, map);

        /* Verify that it's possible to place well */
        Well well0 = map.placeBuilding(new Well(player0), point1);

        assertTrue(map.isBuildingAtPoint(point1));

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(well0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(well0);
    }

    @Test
    public void testCanBuildHouseOnMeadow2() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place meadow 2 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MEADOW_2, map);

        /* Verify that it's possible to place well */
        Well well0 = map.placeBuilding(new Well(player0), point1);

        assertTrue(map.isBuildingAtPoint(point1));

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(well0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(well0);
    }

    @Test
    public void testCanBuildHouseOnMeadow3() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place meadow 3 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MEADOW_3, map);

        /* Verify that it's possible to place well */
        Well well0 = map.placeBuilding(new Well(player0), point1);

        assertTrue(map.isBuildingAtPoint(point1));

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(well0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(well0);
    }

    @Test
    public void testCannotBuildHouseOnMountain2() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mountain 2 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MOUNTAIN_2, map);

        /* Verify that it's not possible to place well */
        try {
            Well well0 = map.placeBuilding(new Well(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildHouseOnMountain3() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mountain 3 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MOUNTAIN_3, map);

        /* Verify that it's not possible to place well */
        try {
            Well well0 = map.placeBuilding(new Well(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildHouseOnMountain4() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mountain 4 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MOUNTAIN_4, map);

        /* Verify that it's not possible to place well */
        try {
            Well well0 = map.placeBuilding(new Well(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCanBuildHouseOnSteppe() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place steppe */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.STEPPE, map);

        /* Verify that it's possible to place well */
        Well well0 = map.placeBuilding(new Well(player0), point1);

        assertTrue(map.isBuildingAtPoint(point1));

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(well0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(well0);
    }

    @Test
    public void testCanBuildHouseOnFlowerMeadow() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flower meadow */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.FLOWER_MEADOW, map);

        /* Verify that it's possible to place well */
        Well well0 = map.placeBuilding(new Well(player0), point1);

        assertTrue(map.isBuildingAtPoint(point1));

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(well0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(well0);
    }

    @Test
    public void testCannotBuildHouseOnLava() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place lava */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.LAVA, map);

        /* Verify that it's not possible to place well */
        try {
            Well well0 = map.placeBuilding(new Well(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildHouseOnMagenta() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place magenta */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MAGENTA, map);

        /* Verify that it's not possible to place well */
        try {
            Well well0 = map.placeBuilding(new Well(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCanBuildHouseOnMountainMeadow() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mountain meadow */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MOUNTAIN_MEADOW, map);

        /* Verify that it's possible to place well */
        Well well0 = map.placeBuilding(new Well(player0), point1);

        assertTrue(map.isBuildingAtPoint(point1));

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(well0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(well0);
    }

    @Test
    public void testCannotBuildHouseOnWater2() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place water 2 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.WATER_2, map);

        /* Verify that it's not possible to place well */
        try {
            Well well0 = map.placeBuilding(new Well(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildHouseOnLava2() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place lava 2 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.LAVA_2, map);

        /* Verify that it's not possible to place well */
        try {
            Well well0 = map.placeBuilding(new Well(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildHouseOnLava3() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place lava 3 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.LAVA_3, map);

        /* Verify that it's not possible to place well */
        try {
            Well well0 = map.placeBuilding(new Well(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildHouseOnLava4() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place lava 4 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.LAVA_4, map);

        /* Verify that it's not possible to place well */
        try {
            Well well0 = map.placeBuilding(new Well(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCanBuildHouseOnBuildableMountain() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place buildable mountain */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.BUILDABLE_MOUNTAIN, map);

        /* Verify that it's possible to place well */
        Well well0 = map.placeBuilding(new Well(player0), point1);

        assertTrue(map.isBuildingAtPoint(point1));

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(well0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(well0);
    }

    @Test
    public void testCanBuildRoadOnSavannah() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place savannah */
        Point point1 = new Point(7, 9);
        Point point2 = new Point(9, 9);
        Point point3 = new Point(11, 9);

        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.SAVANNAH, map);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.SAVANNAH, map);
        Utils.surroundPointWithDetailedVegetation(point3, DetailedVegetation.SAVANNAH, map);

        /* Verify that it's possible to place road */
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point1, point2, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testCanBuildRoadOnMountain1() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mountain 1 */
        Point point1 = new Point(7, 9);
        Point point2 = new Point(9, 9);
        Point point3 = new Point(11, 9);

        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MOUNTAIN_1, map);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.MOUNTAIN_1, map);
        Utils.surroundPointWithDetailedVegetation(point3, DetailedVegetation.MOUNTAIN_1, map);

        /* Verify that it's possible to place road */
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point1, point2, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testCannotBuildRoadOnSnow() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place snow */
        Point point2 = new Point(9, 9);

        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.SNOW, map);

        /* Verify that it's not possible to place flag */
        try {
            map.placeFlag(player0, point2);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isFlagAtPoint(point2));

        /* Verify that it's not possible to place road */
        Point point1 = new Point(7, 9);
        Point point3 = new Point(11, 9);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));

        try {
            Road road0 = map.placeRoad(player0, point1, point2, point3);

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getRoads().size(), 1);
    }

    @Test
    public void testCannotBuildRoadOnSwamp() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place swamp */
        Point point2 = new Point(9, 9);

        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.SWAMP, map);

        /* Verify that it's not possible to place flag */
        try {
            map.placeFlag(player0, point2);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isFlagAtPoint(point2));

        /* Verify that it's not possible to place road */
        Point point1 = new Point(7, 9);
        Point point3 = new Point(11, 9);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));

        try {
            Road road0 = map.placeRoad(player0, point1, point2, point3);

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getRoads().size(), 1);
    }

    @Test
    public void testCanBuildRoadOnDesert1() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place desert 1 */
        Point point1 = new Point(7, 9);
        Point point2 = new Point(9, 9);
        Point point3 = new Point(11, 9);

        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.DESERT_1, map);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.DESERT_1, map);
        Utils.surroundPointWithDetailedVegetation(point3, DetailedVegetation.DESERT_1, map);

        /* Verify that it's possible to place road */
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point1, point2, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testCannotBuildRoadOnWater() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place water */
        Point point2 = new Point(9, 9);

        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.WATER, map);

        /* Verify that it's not possible to place flag */
        try {
            map.placeFlag(player0, point2);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isFlagAtPoint(point2));

        /* Verify that it's not possible to place road */
        Point point1 = new Point(7, 9);
        Point point3 = new Point(11, 9);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));

        try {
            Road road0 = map.placeRoad(player0, point1, point2, point3);

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getRoads().size(), 1);
    }

    @Test
    public void testCanBuildRoadOnBuildableWater() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place buildable water */
        Point point1 = new Point(7, 9);
        Point point2 = new Point(9, 9);
        Point point3 = new Point(11, 9);

        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.BUILDABLE_WATER, map);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.BUILDABLE_WATER, map);
        Utils.surroundPointWithDetailedVegetation(point3, DetailedVegetation.BUILDABLE_WATER, map);

        /* Verify that it's possible to place road */
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point1, point2, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testCanBuildRoadOnDesert2() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place desert 2 */
        Point point1 = new Point(7, 9);
        Point point2 = new Point(9, 9);
        Point point3 = new Point(11, 9);

        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.DESERT_2, map);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.DESERT_2, map);
        Utils.surroundPointWithDetailedVegetation(point3, DetailedVegetation.DESERT_2, map);

        /* Verify that it's possible to place road */
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point1, point2, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testCanBuildRoadOnMeadow1() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place meadow 1 */
        Point point1 = new Point(7, 9);
        Point point2 = new Point(9, 9);
        Point point3 = new Point(11, 9);

        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MEADOW_1, map);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.MEADOW_1, map);
        Utils.surroundPointWithDetailedVegetation(point3, DetailedVegetation.MEADOW_1, map);

        /* Verify that it's possible to place road */
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point1, point2, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testCanBuildRoadOnMeadow2() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place meadow 2 */
        Point point1 = new Point(7, 9);
        Point point2 = new Point(9, 9);
        Point point3 = new Point(11, 9);

        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MEADOW_2, map);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.MEADOW_2, map);
        Utils.surroundPointWithDetailedVegetation(point3, DetailedVegetation.MEADOW_2, map);

        /* Verify that it's possible to place road */
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point1, point2, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testCanBuildRoadOnMeadow3() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place meadow 3 */
        Point point1 = new Point(7, 9);
        Point point2 = new Point(9, 9);
        Point point3 = new Point(11, 9);

        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MEADOW_3, map);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.MEADOW_3, map);
        Utils.surroundPointWithDetailedVegetation(point3, DetailedVegetation.MEADOW_3, map);

        /* Verify that it's possible to place road */
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point1, point2, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testCanBuildRoadOnMountain2() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mountain 2 */
        Point point1 = new Point(7, 9);
        Point point2 = new Point(9, 9);
        Point point3 = new Point(11, 9);

        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MOUNTAIN_2, map);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.MOUNTAIN_2, map);
        Utils.surroundPointWithDetailedVegetation(point3, DetailedVegetation.MOUNTAIN_2, map);

        /* Verify that it's possible to place road */
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point1, point2, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testCanBuildRoadOnMountain3() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mountain 3 */
        Point point1 = new Point(7, 9);
        Point point2 = new Point(9, 9);
        Point point3 = new Point(11, 9);

        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MOUNTAIN_3, map);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.MOUNTAIN_3, map);
        Utils.surroundPointWithDetailedVegetation(point3, DetailedVegetation.MOUNTAIN_3, map);

        /* Verify that it's possible to place road */
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point1, point2, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testCanBuildRoadOnMountain4() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mountain 4 */
        Point point1 = new Point(7, 9);
        Point point2 = new Point(9, 9);
        Point point3 = new Point(11, 9);

        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MOUNTAIN_4, map);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.MOUNTAIN_4, map);
        Utils.surroundPointWithDetailedVegetation(point3, DetailedVegetation.MOUNTAIN_4, map);

        /* Verify that it's possible to place road */
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point1, point2, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testCanBuildRoadOnSteppe() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place steppe */
        Point point1 = new Point(7, 9);
        Point point2 = new Point(9, 9);
        Point point3 = new Point(11, 9);

        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.STEPPE, map);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.STEPPE, map);
        Utils.surroundPointWithDetailedVegetation(point3, DetailedVegetation.STEPPE, map);

        /* Verify that it's possible to place road */
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point1, point2, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testCanBuildRoadOnFlowerMeadow() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flower meadow */
        Point point1 = new Point(7, 9);
        Point point2 = new Point(9, 9);
        Point point3 = new Point(11, 9);

        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.FLOWER_MEADOW, map);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.FLOWER_MEADOW, map);
        Utils.surroundPointWithDetailedVegetation(point3, DetailedVegetation.FLOWER_MEADOW, map);

        /* Verify that it's possible to place road */
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point1, point2, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testCannotBuildRoadOnLava() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place lava */
        Point point2 = new Point(9, 9);

        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.LAVA, map);

        /* Verify that it's not possible to place flag */
        try {
            map.placeFlag(player0, point2);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isFlagAtPoint(point2));

        /* Verify that it's not possible to place road */
        Point point1 = new Point(7, 9);
        Point point3 = new Point(11, 9);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));

        try {
            Road road0 = map.placeRoad(player0, point1, point2, point3);

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getRoads().size(), 1);
    }

    @Test
    public void testCanBuildRoadOnMagenta() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place magenta */
        Point point1 = new Point(7, 9);
        Point point2 = new Point(9, 9);
        Point point3 = new Point(11, 9);

        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MAGENTA, map);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.MAGENTA, map);
        Utils.surroundPointWithDetailedVegetation(point3, DetailedVegetation.MAGENTA, map);

        /* Verify that it's possible to place road */
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point1, point2, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testCanBuildRoadOnMountainMeadow() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mountain meadow */
        Point point1 = new Point(7, 9);
        Point point2 = new Point(9, 9);
        Point point3 = new Point(11, 9);

        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MOUNTAIN_MEADOW, map);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.MOUNTAIN_MEADOW, map);
        Utils.surroundPointWithDetailedVegetation(point3, DetailedVegetation.MOUNTAIN_MEADOW, map);

        /* Verify that it's possible to place road */
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point1, point2, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testCannotBuildRoadOnWater2() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place water 2 */
        Point point2 = new Point(9, 9);

        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.WATER_2, map);

        /* Verify that it's not possible to place flag */
        try {
            map.placeFlag(player0, point2);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isFlagAtPoint(point2));

        /* Verify that it's not possible to place road */
        Point point1 = new Point(7, 9);
        Point point3 = new Point(11, 9);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));

        try {
            Road road0 = map.placeRoad(player0, point1, point2, point3);

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getRoads().size(), 1);
    }

    @Test
    public void testCannotBuildRoadOnLava2() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place lava 2 */
        Point point2 = new Point(9, 9);

        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.LAVA_2, map);

        /* Verify that it's not possible to place flag */
        try {
            map.placeFlag(player0, point2);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isFlagAtPoint(point2));

        /* Verify that it's not possible to place road */
        Point point1 = new Point(7, 9);
        Point point3 = new Point(11, 9);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));

        try {
            Road road0 = map.placeRoad(player0, point1, point2, point3);

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getRoads().size(), 1);
    }

    @Test
    public void testCannotBuildRoadOnLava3() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place lava 3 */
        Point point2 = new Point(9, 9);

        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.LAVA_3, map);

        /* Verify that it's not possible to place flag */
        try {
            map.placeFlag(player0, point2);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isFlagAtPoint(point2));

        /* Verify that it's not possible to place road */
        Point point1 = new Point(7, 9);
        Point point3 = new Point(11, 9);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));

        try {
            Road road0 = map.placeRoad(player0, point1, point2, point3);

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getRoads().size(), 1);
    }

    @Test
    public void testCannotBuildRoadOnLava4() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place lava 4 */
        Point point2 = new Point(9, 9);

        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.LAVA_4, map);

        /* Verify that it's not possible to place flag */
        try {
            map.placeFlag(player0, point2);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isFlagAtPoint(point2));

        /* Verify that it's not possible to place road */
        Point point1 = new Point(7, 9);
        Point point3 = new Point(11, 9);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));

        try {
            Road road0 = map.placeRoad(player0, point1, point2, point3);

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getRoads().size(), 1);
    }

    @Test
    public void testCanBuildRoadOnBuildableMountain() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(4, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place buildable mountain */
        Point point1 = new Point(7, 9);
        Point point2 = new Point(9, 9);
        Point point3 = new Point(11, 9);

        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.BUILDABLE_MOUNTAIN, map);
        Utils.surroundPointWithDetailedVegetation(point2, DetailedVegetation.BUILDABLE_MOUNTAIN, map);
        Utils.surroundPointWithDetailedVegetation(point3, DetailedVegetation.BUILDABLE_MOUNTAIN, map);

        /* Verify that it's possible to place road */
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point3);

        Road road0 = map.placeRoad(player0, point1, point2, point3);

        assertTrue(map.isFlagAtPoint(point1));
        assertTrue(map.isFlagAtPoint(point3));
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testCannotBuildMineOnSavannah() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place savannah */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.SAVANNAH, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCanBuildMineOnMountain1() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mountain 1 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MOUNTAIN_1, map);

        /* Verify that it's possible to place a mine */
        GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        assertTrue(map.isBuildingAtPoint(point1));

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(goldMine0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(goldMine0);
    }

    @Test
    public void testCannotBuildMineOnSnow() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place snow */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.SNOW, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnSwamp() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place swamp */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.SWAMP, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnDesert1() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place desert 1 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.DESERT_1, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnWater() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place water */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.WATER, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnBuildableWater() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place buildable water */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.BUILDABLE_WATER, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnDesert2() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place desert 2 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.DESERT_2, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnMeadow1() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place meadow 1 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MEADOW_1, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnMeadow2() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place meadow 2 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MEADOW_2, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnMeadow3() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place meadow 3 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MEADOW_3, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCanBuildMineOnMountain2() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mountain 2 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MOUNTAIN_2, map);

        /* Verify that it's possible to place a mine */
        GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        assertTrue(map.isBuildingAtPoint(point1));

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(goldMine0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(goldMine0);
    }

    @Test
    public void testCanBuildMineOnMountain3() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mountain 3 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MOUNTAIN_3, map);

        /* Verify that it's possible to place a mine */
        GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        assertTrue(map.isBuildingAtPoint(point1));

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(goldMine0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(goldMine0);
    }

    @Test
    public void testCanBuildMineOnMountain4() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mountain 4 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MOUNTAIN_4, map);

        /* Verify that it's possible to place a mine */
        GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

        assertTrue(map.isBuildingAtPoint(point1));

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(goldMine0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(goldMine0);
    }

    @Test
    public void testCannotBuildMineOnSteppe() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place steppe */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.STEPPE, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnFlowerMeadow() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flower meadow */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.FLOWER_MEADOW, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnLava() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place lava */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.LAVA, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnMagenta() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place magenta */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MAGENTA, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnMountainMeadow() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mountain meadow */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.MOUNTAIN_MEADOW, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnWater2() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place water 2 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.WATER_2, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnLava2() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place lava 2 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.LAVA_2, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnLava3() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place lava 3 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.LAVA_3, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnLava4() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place lava 4 */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.LAVA_4, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }

    @Test
    public void testCannotBuildMineOnBuildableMountain() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place buildable mountain */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithDetailedVegetation(point1, DetailedVegetation.BUILDABLE_MOUNTAIN, map);

        /* Verify that it's not possible to place a mine */
        try {
            GoldMine goldMine0 = map.placeBuilding(new GoldMine(player0), point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isBuildingAtPoint(point1));
    }
}
