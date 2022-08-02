package org.appland.settlers.test;

import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Forester;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static java.awt.Color.BLUE;
import static org.appland.settlers.model.DecorationType.BROWN_MUSHROOM;
import static org.appland.settlers.model.DecorationType.BUSH;
import static org.appland.settlers.model.DecorationType.CATTAIL;
import static org.appland.settlers.model.DecorationType.DEAD_TREE;
import static org.appland.settlers.model.DecorationType.DEAD_TREE_LYING_DOWN;
import static org.appland.settlers.model.DecorationType.FEW_SMALL_STONES;
import static org.appland.settlers.model.DecorationType.FLOWERS;
import static org.appland.settlers.model.DecorationType.GRASS_1;
import static org.appland.settlers.model.DecorationType.GRASS_2;
import static org.appland.settlers.model.DecorationType.LARGE_BUSH;
import static org.appland.settlers.model.DecorationType.LITTLE_GRASS;
import static org.appland.settlers.model.DecorationType.MINI_BROWN_MUSHROOM;
import static org.appland.settlers.model.DecorationType.MINI_BUSH;
import static org.appland.settlers.model.DecorationType.MINI_STONE;
import static org.appland.settlers.model.DecorationType.MINI_STONE_WITH_GRASS;
import static org.appland.settlers.model.DecorationType.SMALL_BUSH;
import static org.appland.settlers.model.DecorationType.SMALL_SKELETON;
import static org.appland.settlers.model.DecorationType.SMALL_STONE;
import static org.appland.settlers.model.DecorationType.SMALL_STONE_WITH_GRASS;
import static org.appland.settlers.model.DecorationType.SOME_SMALLER_STONES;
import static org.appland.settlers.model.DecorationType.SOME_SMALL_STONES;
import static org.appland.settlers.model.DecorationType.SOME_WATER;
import static org.appland.settlers.model.DecorationType.SPARSE_BUSH;
import static org.appland.settlers.model.DecorationType.STONE;
import static org.appland.settlers.model.DecorationType.TOADSTOOL;
import static org.appland.settlers.model.Size.LARGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestDecorations {

    static final Set<DecorationType> PURE_DECORATIONS = EnumSet.of(
            MINI_BROWN_MUSHROOM,
            TOADSTOOL,
            MINI_STONE,
            SMALL_STONE,
            STONE,
            SMALL_SKELETON,
            FLOWERS,
            LARGE_BUSH,
            CATTAIL,
            GRASS_1,
            GRASS_2,
            BUSH,
            SMALL_BUSH,
            MINI_BUSH,
            BROWN_MUSHROOM,
            MINI_STONE_WITH_GRASS,
            SMALL_STONE_WITH_GRASS,
            SOME_SMALL_STONES,
            SOME_SMALLER_STONES,
            FEW_SMALL_STONES,
            SPARSE_BUSH,
            SOME_WATER,
            LITTLE_GRASS,
            DEAD_TREE,
            DEAD_TREE_LYING_DOWN
    );

    /**
     * TODO:
     *   - Test plant tree removes pure decorations
     *   - Test place mine on pure decorations
     *   - Test place harbor on pure decorations
     *   - Test animals, workers can walk on pure decorations
     *   - For decorations that affect the game (not pure):
     *      - Test can't build
     *      - Test can't place road
     *   - Test can't place decorations on water (or only some - like cattail)
     *
     */

    @Test
    public void testAvailableConstructionOnPureDecorations() throws InvalidUserActionException {

        for (DecorationType decoration : PURE_DECORATIONS) {

            /* Create new game map */
            Player player0 = new Player("Player 0", BLUE);
            List<Player> players = new ArrayList<>();
            players.add(player0);

            GameMap map = new GameMap(players, 100, 100);

            /* Place headquarters */
            Point point0 = new Point(5, 27);
            Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            /* Place decoration that should not have any impact on the game */
            Point point1 = new Point(10, 26);
            map.placeDecoration(point1, decoration);

            assertTrue(map.isDecoratedAtPoint(point1));
            assertEquals(map.getDecorationAtPoint(point1), decoration);
            assertTrue(map.getDecorations().containsKey(point1));
            assertEquals(map.getDecorations().get(point1), decoration);

            /* Verify that there is available construction on the decoration */
            assertEquals(map.isAvailableHousePoint(player0, point1), LARGE);
            assertTrue(map.isAvailableFlagPoint(player0, point1));
        }
    }

    @Test
    public void testPlaceFlagOnPureDecorations() throws InvalidUserActionException {

        for (DecorationType decoration : PURE_DECORATIONS) {

            /* Create new game map */
            Player player0 = new Player("Player 0", BLUE);
            List<Player> players = new ArrayList<>();
            players.add(player0);

            GameMap map = new GameMap(players, 100, 100);

            /* Place headquarters */
            Point point0 = new Point(5, 27);
            Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            /* Place decoration that should not have any impact on the game */
            Point point1 = new Point(10, 26);
            map.placeDecoration(point1, decoration);

            assertTrue(map.isDecoratedAtPoint(point1));
            assertEquals(map.getDecorationAtPoint(point1), decoration);
            assertTrue(map.getDecorations().containsKey(point1));
            assertEquals(map.getDecorations().get(point1), decoration);

            /* Verify that a flag can be placed on the decoration */
            Flag flag = map.placeFlag(player0, point1);

            assertTrue(map.isFlagAtPoint(point1));
            assertFalse(map.isDecoratedAtPoint(point1));
            assertFalse(map.getDecorations().containsKey(point1));
        }
    }

    @Test
    public void testPlaceBuildingOnPureDecorations() throws InvalidUserActionException {

        for (DecorationType decoration : PURE_DECORATIONS) {

            /* Create new game map */
            Player player0 = new Player("Player 0", BLUE);
            List<Player> players = new ArrayList<>();
            players.add(player0);

            GameMap map = new GameMap(players, 100, 100);

            /* Place headquarters */
            Point point0 = new Point(5, 27);
            Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            /* Place decoration that should not have any impact on the game */
            Point point1 = new Point(10, 26);
            map.placeDecoration(point1, decoration);

            assertTrue(map.isDecoratedAtPoint(point1));
            assertEquals(map.getDecorationAtPoint(point1), decoration);
            assertTrue(map.getDecorations().containsKey(point1));
            assertEquals(map.getDecorations().get(point1), decoration);

            /* Verify that a building can be placed on the decoration */
            Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

            assertTrue(map.isBuildingAtPoint(point1));
            assertFalse(map.isDecoratedAtPoint(point1));
            assertFalse(map.getDecorations().containsKey(point1));
        }
    }

    @Test
    public void testPlaceRoadOnPureDecorations() throws InvalidUserActionException {

        for (DecorationType decoration : PURE_DECORATIONS) {

            /* Create new game map */
            Player player0 = new Player("Player 0", BLUE);
            List<Player> players = new ArrayList<>();
            players.add(player0);

            GameMap map = new GameMap(players, 100, 100);

            /* Place headquarters */
            Point point0 = new Point(5, 27);
            Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            /* Place decoration that should not have any impact on the game */
            Point point1 = new Point(7, 27);
            map.placeDecoration(point1, decoration);

            assertTrue(map.isDecoratedAtPoint(point1));
            assertEquals(map.getDecorationAtPoint(point1), decoration);
            assertTrue(map.getDecorations().containsKey(point1));
            assertEquals(map.getDecorations().get(point1), decoration);

            /* Place flag */
            Point point2 = new Point(9, 27);
            Flag flag = map.placeFlag(player0, point2);

            /* Verify that a road can be placed on the decoration */
            Road road = map.placeRoad(player0, headquarter0.getFlag().getPosition(), point1, flag.getPosition());

            assertTrue(map.isRoadAtPoint(point1));
            assertFalse(map.isDecoratedAtPoint(point1));
            assertFalse(map.getDecorations().containsKey(point1));
        }
    }

    @Test
    public void testForesterPlantsTreeOnPureDecorations() throws InvalidUserActionException {

        for (DecorationType decoration : PURE_DECORATIONS) {

            /* Create new game map */
            Player player0 = new Player("Player 0", BLUE);
            List<Player> players = new ArrayList<>();
            players.add(player0);

            GameMap map = new GameMap(players, 100, 100);

            /* Place decorations that should not have any impact on the game all over the map */
            for (int x = 0; x < 100; x++) {
                for (int y = 0; y < 100; y++) {

                    if ((x + y) % 2 != 0) {
                        continue;
                    }

                    Point point = new Point(x, y);
                    map.placeDecoration(point, decoration);

                    assertTrue(map.isDecoratedAtPoint(point));
                    assertEquals(map.getDecorationAtPoint(point), decoration);
                    assertTrue(map.getDecorations().containsKey(point));
                    assertEquals(map.getDecorations().get(point), decoration);
                }
            }

            /* Place headquarters */
            Point point0 = new Point(5, 27);
            Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            /* Place forester hut */
            Point point2 = new Point(9, 27);
            ForesterHut foresterHut = map.placeBuilding(new ForesterHut(player0), point2);

            /* Connect the forester hut with the headquarter */
            Road road = map.placeAutoSelectedRoad(player0, foresterHut.getFlag(), headquarter0.getFlag());

            /* Wait for the forester hut to get constructed and populated */
            Utils.waitForBuildingToBeConstructed(foresterHut);

            Forester forester = (Forester) Utils.waitForNonMilitaryBuildingToGetPopulated(foresterHut);

            /* Wait for the forester to go out to plant */
            assertTrue(forester.isInsideBuilding());

            Utils.waitForWorkerToBeOutside(forester, map);

            /* Wait for the forester to reach the point to plant */
            assertNotNull(forester.getTarget());

            Utils.fastForwardUntilWorkerReachesPoint(map, forester, forester.getTarget());

            /* Verify that the forester places a tree on a decoration (and that the decoration then is removed) */
            Point point3 = forester.getPosition();

            assertFalse(map.isTreeAtPoint(point3));
            assertTrue(map.isDecoratedAtPoint(point3));

            Utils.waitForForesterToStopPlantingTree(forester, map);

            assertTrue(map.isTreeAtPoint(point3));
            assertFalse(map.isDecoratedAtPoint(point3));
        }
    }
}
