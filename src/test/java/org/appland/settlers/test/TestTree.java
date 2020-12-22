package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Tree.TreeType;
import org.appland.settlers.model.Well;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.BLUE;
import static org.appland.settlers.model.Size.LARGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestTree {

    /**
     * To test:
     *  - for dead tree:
     *      - can vs can't place on each vegetation
     *      - available construction on surrounding points
     */

    @Test
    public void testTreeTypes() {

        assertEquals(TreeType.values().length, 9);
        assertEquals(TreeType.TREE_TYPE_1.name(), "TREE_TYPE_1");
        assertEquals(TreeType.TREE_TYPE_2.name(), "TREE_TYPE_2");
        assertEquals(TreeType.TREE_TYPE_3.name(), "TREE_TYPE_3");
        assertEquals(TreeType.TREE_TYPE_4.name(), "TREE_TYPE_4");
        assertEquals(TreeType.TREE_TYPE_5.name(), "TREE_TYPE_5");
        assertEquals(TreeType.TREE_TYPE_6.name(), "TREE_TYPE_6");
        assertEquals(TreeType.TREE_TYPE_7.name(), "TREE_TYPE_7");
        assertEquals(TreeType.TREE_TYPE_8.name(), "TREE_TYPE_8");
        assertEquals(TreeType.TREE_TYPE_9.name(), "TREE_TYPE_9");
    }

    @Test
    public void testDefaultTreeType() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place tree */
        Point point0 = new Point(10, 10);
        Tree tree0 = map.placeTree(point0);

        /* Verify that the tree has the default tree type */
        assertEquals(tree0.getTreeType(), TreeType.TREE_TYPE_1);
    }

    @Test
    public void testTreeTypeCanBeSet() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place tree */
        Point point0 = new Point(10, 10);
        Tree tree0 = map.placeTree(point0);

        /* Verify that the tree type can be set */
        tree0.setTreeType(TreeType.TREE_TYPE_2);

        assertEquals(tree0.getTreeType(), TreeType.TREE_TYPE_2);
    }

    @Test
    public void testNoDeadTreesByDefault() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Verify that there are no dead trees without being explicitly placed */
        assertEquals(map.getDeadTrees().size(), 0);
    }

    @Test
    public void testPlaceDeadTree() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Verify that a dead tree can be placed */
        assertEquals(map.getDeadTrees().size(), 0);

        Point point0 = new Point(20, 20);
        map.placeDeadTree(point0);

        assertEquals(map.getDeadTrees().size(), 1);
        assertTrue(map.getDeadTrees().contains(point0));
    }

    @Test
    public void testCannotPlaceDeadTreeOnHouse() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(6, 12);
        Building well0 = map.placeBuilding(new Well(player0), point1);

        /* Verify that it's not possible to place a dead tree on a house */
        assertEquals(map.getDeadTrees().size(), 0);

        try {
            map.placeDeadTree(point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getDeadTrees().size(), 0);
    }

    @Test
    public void testCannotPlaceDeadTreeOnStone() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place stone */
        Point point1 = new Point(6, 12);
        Stone stone0 = map.placeStone(point1);

        /* Verify that it's not possible to place a dead tree on a stone */
        assertEquals(map.getDeadTrees().size(), 0);

        try {
            map.placeDeadTree(point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getDeadTrees().size(), 0);
    }

    @Test
    public void testCannotPlaceDeadTreeOnRoad() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Verify that it's not possible to place a dead tree on a road */
        assertEquals(map.getDeadTrees().size(), 0);

        try {
            map.placeDeadTree(point1.left());

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getDeadTrees().size(), 0);
    }

    @Test
    public void testCannotPlaceDeadTreeOnTree() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place tree */
        Point point1 = new Point(6, 12);
        Tree tree0 = map.placeTree(point1);

        /* Verify that it's not possible to place a dead tree on a tree */
        assertEquals(map.getDeadTrees().size(), 0);

        try {
            map.placeDeadTree(point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getDeadTrees().size(), 0);
    }

    @Test
    public void testCannotPlaceDeadTreeOnFlag() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(6, 12);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Verify that it's not possible to place a dead tree on a flag */
        assertEquals(map.getDeadTrees().size(), 0);

        try {
            map.placeDeadTree(point1);

            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getDeadTrees().size(), 0);
    }

    @Test
    public void testOnlyFlagIsAvailableOnDeadTree() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place dead tree */
        Point point1 = new Point(10, 6);
        map.placeDeadTree(point1);

        /* Verify that it's only possible to place a flag on the dead tree */
        assertTrue(map.isAvailableFlagPoint(player0, point1));
        assertEquals(map.isAvailableHousePoint(player0, point1), null);
    }

    @Test
    public void testCanPlaceFlagOnDeadTree() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place dead tree */
        Point point1 = new Point(6, 12);
        map.placeDeadTree(point1);

        /* Verify that it's possible to place a flag on a dead tree */
        Flag flag0 = map.placeFlag(player0, point1);

        assertEquals(map.getDeadTrees().size(), 0);
        assertFalse(map.isDeadTree(point1));
        assertTrue(map.isFlagAtPoint(point1));
    }

    @Test
    public void testCanPlaceLargeBuildingAfterPlacedAndRemovedFlagOnDeadTree() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place dead tree */
        Point point1 = new Point(6, 12);

        assertFalse(map.isDeadTree(point1));

        map.placeDeadTree(point1);

        assertTrue(map.isDeadTree(point1));

        /* Place flag on the dead tree */
        Flag flag0 = map.placeFlag(player0, point1);

        assertFalse(map.isDeadTree(point1));

        /* Remove the flag */
        map.removeFlag(flag0);

        /* Verify that it's now possible to place a large building on the point where the dead tree was */
        assertEquals(map.isAvailableHousePoint(player0, point1), LARGE);
    }
}
