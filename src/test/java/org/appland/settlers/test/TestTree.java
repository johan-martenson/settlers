package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Tree.TreeType;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Well;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Vegetation.*;
import static org.appland.settlers.model.Size.LARGE;
import static org.junit.Assert.*;

public class TestTree {

    /**
     * To test:
     *  - for dead tree: --- update tests below to cover all
     *      - can place on:
     *          - MEADOW_1
     *          - MEADOW_2
     *          - MEADOW_3
     *          - FLOWER_MEADOW
     *          - STEPPE
     *          - LAVA
     *          - SAVANNAH
     *          - MEADOW_1
     *          - MEADOW_2
     *          - DESERT_1
     *          - DESERT_2
     *          - MOUNTAIN_MEADOW
     *          - MOUNTAIN_1
     *          - MOUNTAIN_4
     *          - SWAMP
     *      - cannot place on all other vegetations
     *          - SNOW
     *          - WATER
     *          - BUILDABLE_WATER
     *          - MAGENTA
     *          - WATER_2
     *          - LAVA_2
     *          - LAVA_3
     *          - LAVA_4
     *          - MOUNTAIN_2
     *          - MOUNTAIN_3
     *          - BUILDABLE_MOUNTAIN_2
     */

    @Test
    public void testTreeTypes() {

        assertEquals(TreeType.values().length, 9);
        assertEquals(TreeType.PINE.name(), "PINE");
        assertEquals(TreeType.BIRCH.name(), "BIRCH");
        assertEquals(TreeType.OAK.name(), "OAK");
        assertEquals(TreeType.PALM_1.name(), "PALM_1");
        assertEquals(TreeType.PALM_2.name(), "PALM_2");
        assertEquals(TreeType.PINE_APPLE.name(), "PINE_APPLE");
        assertEquals(TreeType.CYPRESS.name(), "CYPRESS");
        assertEquals(TreeType.CHERRY.name(), "CHERRY");
        assertEquals(TreeType.FIR.name(), "FIR");
    }

    @Test
    public void testDefaultTreeType() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place tree
        var point0 = new Point(10, 10);
        var tree0 = map.placeTree(point0, TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Verify that the tree has the default tree type
        assertEquals(tree0.getTreeType(), TreeType.PINE);
    }

    @Test
    public void testTreeTypeCanBeSet() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place tree
        var point0 = new Point(10, 10);
        var tree0 = map.placeTree(point0, TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Verify that the tree type can be set
        tree0.setTreeType(TreeType.BIRCH);

        assertEquals(tree0.getTreeType(), TreeType.BIRCH);
    }

    @Test
    public void testNoDeadTreesByDefault() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Verify that there are no dead trees without being explicitly placed
        assertEquals(map.getDeadTrees().size(), 0);
    }

    @Test
    public void testPlaceDeadTree() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Verify that a dead tree can be placed
        assertEquals(map.getDeadTrees().size(), 0);

        var point0 = new Point(20, 20);

        assertFalse(map.isDeadTree(point0));

        map.placeDeadTree(point0);

        assertEquals(map.getDeadTrees().size(), 1);
        assertTrue(map.getDeadTrees().contains(point0));
        assertTrue(map.isDeadTree(point0));
    }

    @Test
    public void testCannotPlaceDeadTreeOnHouse() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place well
        var point1 = new Point(6, 12);
        var well0 = map.placeBuilding(new Well(player0), point1);

        // Verify that it's not possible to place a dead tree on a house
        assertEquals(map.getDeadTrees().size(), 0);

        try {
            map.placeDeadTree(point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getDeadTrees().size(), 0);
    }

    @Test
    public void testCannotPlaceDeadTreeOnStone() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place stone
        var point1 = new Point(6, 12);
        var stone0 = map.placeStone(point1, Stone.StoneType.STONE_1, 7);

        // Verify that it's not possible to place a dead tree on a stone
        assertEquals(map.getDeadTrees().size(), 0);

        try {
            map.placeDeadTree(point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getDeadTrees().size(), 0);
    }

    @Test
    public void testCannotPlaceDeadTreeOnRoad() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Verify that it's not possible to place a dead tree on a road
        assertEquals(map.getDeadTrees().size(), 0);

        try {
            map.placeDeadTree(point1.left());

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getDeadTrees().size(), 0);
    }

    @Test
    public void testCannotPlaceDeadTreeOnTree() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place tree
        var point1 = new Point(6, 12);
        var tree0 = map.placeTree(point1, TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Verify that it's not possible to place a dead tree on a tree
        assertEquals(map.getDeadTrees().size(), 0);

        try {
            map.placeDeadTree(point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getDeadTrees().size(), 0);
    }

    @Test
    public void testCannotPlaceDeadTreeOnFlag() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag
        var point1 = new Point(6, 12);
        var flag0 = map.placeFlag(player0, point1);

        // Verify that it's not possible to place a dead tree on a flag
        assertEquals(map.getDeadTrees().size(), 0);

        try {
            map.placeDeadTree(point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getDeadTrees().size(), 0);
    }

    @Test
    public void testOnlyFlagIsAvailableOnDeadTree() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place dead tree
        var point1 = new Point(10, 6);
        map.placeDeadTree(point1);

        // Verify that it's only possible to place a flag on the dead tree
        assertTrue(map.isAvailableFlagPoint(player0, point1));
        assertNull(map.isAvailableHousePoint(player0, point1));
    }

    @Test
    public void testCanPlaceFlagOnDeadTree() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place dead tree
        var point1 = new Point(6, 12);
        map.placeDeadTree(point1);

        // Verify that it's possible to place a flag on a dead tree
        var flag0 = map.placeFlag(player0, point1);

        assertEquals(map.getDeadTrees().size(), 0);
        assertFalse(map.isDeadTree(point1));
        assertTrue(map.isFlagAtPoint(point1));
    }

    @Test
    public void testCanPlaceLargeBuildingAfterPlacedAndRemovedFlagOnDeadTree() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place dead tree
        var point1 = new Point(6, 12);

        assertFalse(map.isDeadTree(point1));

        map.placeDeadTree(point1);

        assertTrue(map.isDeadTree(point1));

        // Place flag on the dead tree
        var flag0 = map.placeFlag(player0, point1);

        assertFalse(map.isDeadTree(point1));

        // Remove the flag
        map.removeFlag(flag0);

        // Verify that it's now possible to place a large building on the point where the dead tree was
        assertEquals(map.isAvailableHousePoint(player0, point1), LARGE);
    }

    @Test
    public void testCanPlaceDeadTreeOnMountain() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place mountain
        var point1 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point1, MOUNTAIN_1, map);

        // Verify that a dead tree can be placed on a mountain
        map.placeDeadTree(point1);

        assertTrue(map.isDeadTree(point1));
    }

    @Test
    public void testCanPlaceDeadTreeOnSavannah() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place savannah
        var point1 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point1, SAVANNAH, map);

        // Verify that a dead tree can be placed on a savannah
        map.placeDeadTree(point1);

        assertTrue(map.isDeadTree(point1));
    }

    @Test
    public void testCanPlaceDeadTreeOnLava() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place lava
        var point1 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point1, LAVA_1, map);

        // Verify that a dead tree can be placed on lava
        map.placeDeadTree(point1);

        assertTrue(map.isDeadTree(point1));
    }

    @Test
    public void testCanPlaceDeadTreeOnSteppe() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place steppe
        var point1 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point1, STEPPE, map);

        // Verify that a dead tree can be placed on steppe
        map.placeDeadTree(point1);

        assertTrue(map.isDeadTree(point1));
    }

    @Test
    public void testCanPlaceDeadTreeOnSwamp() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place swamp
        var point1 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point1, SWAMP, map);

        // Verify that a dead tree can be placed on swamp
        map.placeDeadTree(point1);

        assertTrue(map.isDeadTree(point1));
    }

    @Test
    public void testCanPlaceDeadTreeOnDesert() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place desert
        var point1 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point1, DESERT_1, map);

        // Verify that a dead tree can be placed on desert
        map.placeDeadTree(point1);

        assertTrue(map.isDeadTree(point1));
    }

    @Test
    public void testCanPlaceDeadTreeOnCombinedVegetation() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place savannah and mountain on the map
        var point1 = new Point(12, 6);
        map.setVegetationBelow(point1, SAVANNAH);
        map.setVegetationDownRight(point1, MOUNTAIN_1);
        map.setVegetationDownLeft(point1, DESERT_1);

        // Verify that a dead tree can be placed on a mountain
        map.placeDeadTree(point1);

        assertTrue(map.isDeadTree(point1));
    }

    @Test
    public void testCannotPlaceDeadTreeOnSnow() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place snow
        var point1 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point1, SNOW, map);

        // Verify that a dead tree can be placed on snow
        try {
            map.placeDeadTree(point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isDeadTree(point1));
    }

    @Test
    public void testCannotPlaceDeadTreeOnShallowWater() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place shallow water
        var point1 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point1, BUILDABLE_WATER, map);

        // Verify that a dead tree can be placed on shallow water
        try {
            map.placeDeadTree(point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isDeadTree(point1));
    }

    @Test
    public void testCannotPlaceDeadTreeOnWater() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place water
        var point1 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point1, WATER, map);

        // Verify that a dead tree can be placed on water
        try {
            map.placeDeadTree(point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isDeadTree(point1));
    }

    @Test
    public void testCannotPlaceDeadTreeOnDeepWater() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place deep water
        var point1 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point1, WATER_2, map);

        // Verify that a dead tree can be placed on deep water
        try {
            map.placeDeadTree(point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isDeadTree(point1));
    }
}
