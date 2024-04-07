package org.appland.settlers.test;

import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.actors.Forester;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.appland.settlers.model.DecorationType.STONE;
import static org.appland.settlers.model.DecorationType.*;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Stone.StoneType.STONE_1;
import static org.appland.settlers.model.actors.Soldier.Rank.GENERAL_RANK;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.junit.Assert.*;

public class TestDecorations {

    static final Set<DecorationType> PURE_DECORATIONS = EnumSet.of(
            MINI_BROWN_MUSHROOM,
            TOADSTOOL,
            MINI_STONE,
            SMALL_STONE,
            STONE,
            ANIMAL_SKELETON_2,
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
            Player player0 = new Player("Player 0", PlayerColor.BLUE);
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
            Player player0 = new Player("Player 0", PlayerColor.BLUE);
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
            Player player0 = new Player("Player 0", PlayerColor.BLUE);
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
            Player player0 = new Player("Player 0", PlayerColor.BLUE);
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
            Player player0 = new Player("Player 0", PlayerColor.BLUE);
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

    @Test
    public void testSkeletonAppearsWhenSoldierDies() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouses(barracks0, barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        /* Get the defender */
        Soldier defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);

        /* Wait for the fight to start */
        Utils.waitForFightToStart(map, attacker, defender);

        /* Wait for the fight to end and the defender to be dying */
        Utils.waitForSoldierToBeDying(defender, map);

        /* Verify that a skeleton is placed when the defender dies */
        assertFalse(map.isDecoratedAtPoint(defender.getPosition()));
        assertNotEquals(map.getDecorations().get(defender.getPosition()), DecorationType.ANIMAL_SKELETON_1);
        assertNotEquals(map.getDecorationAtPoint(defender.getPosition()), ANIMAL_SKELETON_1);

        Utils.waitForWorkerToDie(map, defender);

        assertFalse(map.getWorkers().contains(defender));
        assertTrue(map.isDecoratedAtPoint(defender.getPosition()));
        assertEquals(map.getDecorations().get(defender.getPosition()), DecorationType.ANIMAL_SKELETON_1);
        assertEquals(map.getDecorationAtPoint(defender.getPosition()), ANIMAL_SKELETON_1);
    }

    @Test
    public void testStoneDecorationIsPlacedWhenStoneRunsOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place stone */
        Point point1 = new Point(5, 5);
        Stone stone0 = map.placeStone(point1, STONE_1, 7);

        /* Remove all but one pats of the stone */
        for (int i = 0; i < 6; i++) {
            stone0.removeOnePart();

            map.stepTime();

            assertTrue(map.isStoneAtPoint(point1));
        }

        /* Verify that a stone decoration is placed when the stone runs out */
        assertFalse(map.isDecoratedAtPoint(stone0.getPosition()));
        assertNotEquals(map.getDecorations().get(stone0.getPosition()), FEW_SMALL_STONES);
        assertNotEquals(map.getDecorationAtPoint(stone0.getPosition()), ANIMAL_SKELETON_1);

        stone0.removeOnePart();

        assertEquals(stone0.getAmount(), 0);

        map.stepTime();

        assertFalse(map.isStoneAtPoint(point1));
        assertFalse(map.isDecoratedAtPoint(stone0.getPosition()));
        assertNotEquals(map.getDecorations().get(stone0.getPosition()), DecorationType.ANIMAL_SKELETON_1);
        assertNotEquals(map.getDecorationAtPoint(stone0.getPosition()), ANIMAL_SKELETON_1);
    }

}

