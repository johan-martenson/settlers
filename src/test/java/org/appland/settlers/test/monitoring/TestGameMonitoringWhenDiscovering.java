package org.appland.settlers.test.monitoring;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.BorderChange;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sign;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.LookoutTower;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.actors.Soldier.Rank.GENERAL_RANK;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.junit.Assert.*;

public class TestGameMonitoringWhenDiscovering {

    /*
    TODO:
     - flag+road
     - wild animals
     - own border not being reported twice
     - dead tree
     - ship - ready and under construction
     */

    @Test
    public void testMonitoringEventWhenDiscoveringTree() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place a tree to discover */
        Point point1 = new Point(33, 5);
        Tree tree0 = map.placeTree(point1, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        /* Place lookout tower */
        Point point2 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2);

        /* Connect the lookout tower with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        assertFalse(player0.getDiscoveredLand().contains(tree0.getPosition()));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered tree */
        assertTrue(player0.getDiscoveredLand().contains(tree0.getPosition()));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newTrees().size(), 1);
        assertEquals(gameChangesList.newTrees().getFirst(), tree0);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringTreeIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place a tree to discover */
        Point point1 = new Point(33, 5);
        Tree tree0 = map.placeTree(point1, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        /* Place lookout tower */
        Point point2 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2);

        /* Connect the lookout tower with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        assertFalse(player0.getDiscoveredLand().contains(tree0.getPosition()));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered tree */
        assertTrue(player0.getDiscoveredLand().contains(tree0.getPosition()));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newTrees().size(), 1);
        assertEquals(gameChangesList.newTrees().getFirst(), tree0);

        /* Verify that the new tree event is not sent again */
        for (GameChangesList changes : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(changes.newTrees().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringStone() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place a stone to discover */
        Point point1 = new Point(33, 5);
        Stone stone0 = map.placeStone(point1, Stone.StoneType.STONE_1, 7);

        /* Place lookout tower */
        Point point2 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2);

        /* Connect the lookout tower with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        assertFalse(player0.getDiscoveredLand().contains(stone0.getPosition()));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered stone */
        assertTrue(player0.getDiscoveredLand().contains(stone0.getPosition()));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newStones().size(), 1);
        assertEquals(gameChangesList.newStones().getFirst(), stone0);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringStoneIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place a stone to discover */
        Point point1 = new Point(33, 5);
        Stone stone0 = map.placeStone(point1, Stone.StoneType.STONE_1, 7);

        /* Place lookout tower */
        Point point2 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2);

        /* Connect the lookout tower with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        assertFalse(player0.getDiscoveredLand().contains(stone0.getPosition()));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered stone */
        assertTrue(player0.getDiscoveredLand().contains(stone0.getPosition()));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newStones().size(), 1);
        assertEquals(gameChangesList.newStones().getFirst(), stone0);

        /* Verify that the event is not sent again */
        Utils.fastForward(200, map);

        for (GameChangesList changes : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(changes.newStones().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringFlag() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place a flag for player 0 to discover */
        Point point2 = new Point(33, 5);
        Flag flag0 = map.placeFlag(player1, point2);

        /* Place lookout tower */
        Point point3 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the lookout tower with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        assertFalse(player0.getDiscoveredLand().contains(flag0.getPosition()));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered stone */
        assertTrue(player0.getDiscoveredLand().contains(flag0.getPosition()));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newFlags().size(), 1);
        assertEquals(gameChangesList.newFlags().getFirst(), flag0);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringFlagIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place a flag for player 0 to discover */
        Point point2 = new Point(33, 5);
        Flag flag0 = map.placeFlag(player1, point2);

        /* Place lookout tower */
        Point point3 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the lookout tower with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        assertFalse(player0.getDiscoveredLand().contains(flag0.getPosition()));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered stone */
        assertTrue(player0.getDiscoveredLand().contains(flag0.getPosition()));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newFlags().size(), 1);
        assertEquals(gameChangesList.newFlags().getFirst(), flag0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.newFlags().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringHouse() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place a house for player 0 to discover */
        Point point2 = new Point(33, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player1), point2);

        /* Place lookout tower */
        Point point3 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the lookout tower with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        assertFalse(player0.getDiscoveredLand().contains(woodcutter0.getPosition()));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered stone */
        assertTrue(player0.getDiscoveredLand().contains(woodcutter0.getPosition()));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newBuildings().size(), 1);
        assertEquals(gameChangesList.newBuildings().getFirst(), woodcutter0);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringHouseIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place a house for player 0 to discover */
        Point point2 = new Point(33, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player1), point2);

        /* Place lookout tower */
        Point point3 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the lookout tower with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        assertFalse(player0.getDiscoveredLand().contains(woodcutter0.getPosition()));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered stone */
        assertTrue(player0.getDiscoveredLand().contains(woodcutter0.getPosition()));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newBuildings().size(), 1);
        assertEquals(gameChangesList.newBuildings().getFirst(), woodcutter0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.newBuildings().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringRoad() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(49, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place a road for player 0 to discover */
        Point point2 = new Point(33, 5);
        Point point3 = new Point(37, 5);

        Flag flag0 = map.placeFlag(player1, point2);
        Flag flag1 = map.placeFlag(player1, point3);

        Road road0 = map.placeAutoSelectedRoad(player1, flag0, flag1);

        /* Place lookout tower */
        Point point4 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point4);

        /* Connect the lookout tower with the headquarters */
        Road road1 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        Point point5 = new Point(35, 5);
        assertFalse(player0.getDiscoveredLand().contains(point5));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered stone */
        assertTrue(player0.getDiscoveredLand().contains(point5));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertTrue(gameChangesList.newRoads().size() >= 1);
        assertTrue(gameChangesList.newRoads().contains(road0));
    }

    @Test
    public void testMonitoringEventWhenDiscoveringRoadIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(43, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place a road for player 0 to discover */
        Point point2 = new Point(33, 5);
        Point point3 = new Point(37, 5);

        Flag flag0 = map.placeFlag(player1, point2);
        Flag flag1 = map.placeFlag(player1, point3);

        Road road0 = map.placeAutoSelectedRoad(player1, flag0, flag1);

        /* Place lookout tower */
        Point point4 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point4);

        /* Connect the lookout tower with the headquarters */
        Road road1 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        Point point5 = new Point(35, 5);
        assertFalse(player0.getDiscoveredLand().contains(point5));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered stone */
        assertTrue(player0.getDiscoveredLand().contains(point5));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertTrue(gameChangesList.newRoads().size() >= 1);
        assertTrue(gameChangesList.newRoads().contains(road0));

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.newRoads().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringFlagWithRoads() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(43, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place a road for player 0 to discover */
        Point point2 = new Point(33, 5);
        Point point3 = new Point(37, 5);

        Flag flag0 = map.placeFlag(player1, point2);
        Flag flag1 = map.placeFlag(player1, point3);

        Road road0 = map.placeAutoSelectedRoad(player1, flag0, flag1);
        Road road1 = map.placeAutoSelectedRoad(player1, flag0, flag1);

        /* Place lookout tower */
        Point point4 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point4);

        /* Connect the lookout tower with the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        Point point5 = new Point(35, 5);
        assertFalse(player0.getDiscoveredLand().contains(point5));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered stone */
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertTrue(gameChangesList.newFlags().size() >= 1);
        assertTrue(gameChangesList.newFlags().contains(flag0));
        assertTrue(gameChangesList.newRoads().size() >= 2);
        assertTrue(gameChangesList.newRoads().contains(road0));
        assertTrue(gameChangesList.newRoads().contains(road1));
    }

    @Test
    public void testMonitoringEventWhenDiscoveringBorder() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(51, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Pick a border point for player 0 to discover */
        Point point2 = new Point(33, 5);

        assertTrue(player1.getBorderPoints().contains(point2));

        /* Place lookout tower */
        Point point3 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the lookout tower with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        assertFalse(player0.getDiscoveredLand().contains(point2));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered border */
        assertTrue(player0.getDiscoveredLand().contains(point2));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBorders().size(), 1);

        BorderChange borderChange = gameChangesList.changedBorders().getFirst();

        assertTrue(borderChange.newBorder().size() > 0);
        assertEquals(borderChange.removedBorder().size(), 0);
        assertEquals(borderChange.player(), player1);

        for (Point point : player1.getBorderPoints()) {
            if (player0.getDiscoveredLand().contains(point)) {
                assertTrue(borderChange.newBorder().contains(point));
            } else {
                assertFalse(borderChange.newBorder().contains(point));
            }
        }

        for (Point point : borderChange.newBorder()) {
            assertTrue(player1.getBorderPoints().contains(point));
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringBorderIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(51, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Pick a border point for player 0 to discover */
        Point point2 = new Point(33, 5);

        assertTrue(player1.getBorderPoints().contains(point2));

        /* Place lookout tower */
        Point point3 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the lookout tower with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        assertFalse(player0.getDiscoveredLand().contains(point2));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered border */
        assertTrue(player0.getDiscoveredLand().contains(point2));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBorders().size(), 1);

        boolean otherBorderFound = false;

        for (BorderChange borderChange : monitor.getLastEvent().changedBorders()) {
            if (borderChange.player().equals(player1)) {
                otherBorderFound = true;

                assertTrue(borderChange.newBorder().contains(point2));
                assertEquals(borderChange.removedBorder().size(), 0);

                break;
            }
        }

        assertTrue(otherBorderFound);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.newSigns().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringSign() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place a sign to discover */
        Point point1 = new Point(33, 5);
        Sign sign0 = map.placeSign(IRON, LARGE, point1);

        /* Place lookout tower */
        Point point2 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2);

        /* Connect the lookout tower with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        assertFalse(player0.getDiscoveredLand().contains(sign0.getPosition()));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered sign */
        assertTrue(player0.getDiscoveredLand().contains(sign0.getPosition()));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newSigns().size(), 1);
        assertEquals(gameChangesList.newSigns().getFirst(), sign0);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringSignIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place a sign to discover */
        Point point1 = new Point(33, 5);
        Sign sign0 = map.placeSign(IRON, LARGE, point1);

        /* Place lookout tower */
        Point point2 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2);

        /* Connect the lookout tower with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        assertFalse(player0.getDiscoveredLand().contains(sign0.getPosition()));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered sign */
        assertTrue(player0.getDiscoveredLand().contains(sign0.getPosition()));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newSigns().size(), 1);
        assertEquals(gameChangesList.newSigns().getFirst(), sign0);

        /* Verify that the new sign event is not sent again */
        for (GameChangesList changes : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(changes.newSigns().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringCrop() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place a crop for player 0 to discover */
        Point point2 = new Point(33, 5);
        Crop crop0 = map.placeCrop(point2, Crop.CropType.TYPE_1);

        /* Place lookout tower */
        Point point3 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the lookout tower with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        assertFalse(player0.getDiscoveredLand().contains(crop0.getPosition()));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered stone */
        assertTrue(player0.getDiscoveredLand().contains(crop0.getPosition()));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newCrops().size(), 1);
        assertEquals(gameChangesList.newCrops().getFirst(), crop0);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringCropIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place a crop for player 0 to discover */
        Point point2 = new Point(33, 5);
        Crop crop0 = map.placeCrop(point2, Crop.CropType.TYPE_1);

        /* Place lookout tower */
        Point point3 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the lookout tower with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        assertFalse(player0.getDiscoveredLand().contains(crop0.getPosition()));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered stone */
        assertTrue(player0.getDiscoveredLand().contains(crop0.getPosition()));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newCrops().size(), 1);
        assertEquals(gameChangesList.newCrops().getFirst(), crop0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.newCrops().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringWorker() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(43, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place a road for player 0 to discover */
        Point point2 = new Point(33, 5);
        Point point3 = new Point(37, 5);

        Flag flag0 = map.placeFlag(player1, point2);
        Flag flag1 = map.placeFlag(player1, point3);

        Road road0 = map.placeAutoSelectedRoad(player1, flag0, flag1);

        /* Connect the road with the headquarters */
        Road road1 = map.placeAutoSelectedRoad(player1, flag0, headquarter1.getFlag());

        /* Place lookout tower */
        Point point4 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point4);

        /* Connect the lookout tower with the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Wait for the road to get a courier standing at the middle */
        Courier courier0 = Utils.waitForRoadToGetAssignedCourier(map, road0);

        Point point5 = new Point(35, 5);
        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, point5);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        assertFalse(player0.getDiscoveredLand().contains(point5));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly worker */
        assertTrue(player0.getDiscoveredLand().contains(point5));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newWorkers().size(), 1);
        assertEquals(gameChangesList.newWorkers().getFirst(), courier0);
        assertEquals(gameChangesList.workersWithNewTargets().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringWorkerIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(43, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place a road for player 0 to discover */
        Point point2 = new Point(33, 5);
        Point point3 = new Point(37, 5);

        Flag flag0 = map.placeFlag(player1, point2);
        Flag flag1 = map.placeFlag(player1, point3);

        Road road0 = map.placeAutoSelectedRoad(player1, flag0, flag1);

        /* Connect the road with the headquarters */
        Road road1 = map.placeAutoSelectedRoad(player1, flag0, headquarter1.getFlag());

        /* Place lookout tower */
        Point point4 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point4);

        /* Connect the lookout tower with the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Wait for the road to get a courier standing at the middle */
        Courier courier0 = Utils.waitForRoadToGetAssignedCourier(map, road0);

        Point point5 = new Point(35, 5);
        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, point5);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        assertFalse(player0.getDiscoveredLand().contains(point5));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the new worker */
        assertTrue(player0.getDiscoveredLand().contains(point5));
        System.out.println(monitor.getEvents());
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newWorkers().size(), 1);
        assertEquals(gameChangesList.newWorkers().getFirst(), courier0);
        assertFalse(gameChangesList.workersWithNewTargets().contains(courier0));

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertFalse(newChanges.newWorkers().contains(courier0));
            assertFalse(newChanges.workersWithNewTargets().contains(courier0));
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringBorderThroughEnemyExpansion() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(51, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place a fortress for player 1 */
        Point point2 = new Point(35, 5);
        Fortress fortress0 = map.placeBuilding(new Fortress(player1), point2);

        /* Connect the fortress to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player1, fortress0.getFlag(), headquarter1.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the fortress to get constructed */
        Utils.waitForBuildingToBeConstructed(fortress0);

        /* Verify that a game monitoring message is sent when player 0 discovers player 1's border */
        for (Point point : player1.getBorderPoints()) {
            assertFalse(player0.getDiscoveredLand().contains(point));
        }

        Utils.waitForMilitaryBuildingToGetPopulated(fortress0);

        boolean canSeeEnemysBorder = false;

        for (Point point : player1.getBorderPoints()) {
            if (player0.getDiscoveredLand().contains(point)) {
                canSeeEnemysBorder = true;

                break;
            }
        }

        assertTrue(canSeeEnemysBorder);

        boolean otherBorderFound = false;

        for (BorderChange borderChange : monitor.getLastEvent().changedBorders()) {
            if (borderChange.player().equals(player1) && !borderChange.newBorder().isEmpty()) {
                otherBorderFound = true;

                break;
            }
        }

        assertTrue(otherBorderFound);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringBorderThroughEnemyExpansionIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(51, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place a fortress for player 1 */
        Point point2 = new Point(35, 5);
        Fortress fortress0 = map.placeBuilding(new Fortress(player1), point2);

        /* Connect the fortress to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player1, fortress0.getFlag(), headquarter1.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the fortress to get constructed */
        Utils.waitForBuildingToBeConstructed(fortress0);

        /* Verify that a game monitoring message is sent when player 0 discovers player 1's border */
        for (Point point : player1.getBorderPoints()) {
            assertFalse(player0.getDiscoveredLand().contains(point));
        }

        Utils.waitForMilitaryBuildingToGetPopulated(fortress0);

        boolean canSeeEnemysBorder = false;

        for (Point point : player1.getBorderPoints()) {
            if (player0.getDiscoveredLand().contains(point)) {
                canSeeEnemysBorder = true;

                break;
            }
        }

        assertTrue(canSeeEnemysBorder);

        boolean otherBorderFound = false;

        for (BorderChange borderChange : monitor.getLastEvent().changedBorders()) {
            if (borderChange.player().equals(player1) && !borderChange.newBorder().isEmpty()) {
                otherBorderFound = true;

                break;
            }
        }

        assertTrue(otherBorderFound);

        /* Verify that the event is only sent once */
        GameChangesList gameChanges = monitor.getLastEvent();

        Utils.fastForward(100, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.newSigns().size(), 0);
        }
    }

    @Test
    public void testMonitoringWhenSoldierDies() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);

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

        /* Clear the soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

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

        /* Wait for the defender to go to the attacker */
        assertEquals(defender.getTarget(), attacker.getPosition());
        assertTrue(barracks1.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Verify that the soldiers are fighting */
        Utils.waitForFightToStart(map, attacker, defender);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that an event is sent when the general beats the private */
        Utils.waitForSoldierToWinFight(attacker, map);

        assertFalse(attacker.isFighting());
        assertNotNull(monitor.getLastEvent());

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertTrue(gameChangesList.removedWorkers().size() > 0);
        assertTrue(gameChangesList.removedWorkers().contains(defender));
    }

    @Test
    public void testMonitoringEventWhenDiscoveringDeadTree() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();

        players.add(player0);

        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place dead tree to discover */
        Point point1 = new Point(10, 20);
        map.placeDeadTree(point1);

        assertFalse(player0.getDiscoveredLand().contains(point1));

        /* Place a lookout tower */
        Point point2 = new Point(10, 12);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2);

        /* Connect the lookout tower to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a game monitoring message is sent when the dead tree is discovered */
        assertFalse(player0.getDiscoveredLand().contains(point1));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        assertTrue(player0.getDiscoveredLand().contains(point1));

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.removedDeadTrees().size(), 0);
        assertTrue(gameChangesList.discoveredDeadTrees().size() > 0);
        assertTrue(gameChangesList.discoveredDeadTrees().contains(point1));
    }

    @Test
    public void testMonitoringEventWhenDiscoveringDeadTreeIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();

        players.add(player0);

        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place dead tree to discover */
        Point point1 = new Point(4, 20);
        map.placeDeadTree(point1);

        assertFalse(player0.getDiscoveredLand().contains(point1));

        /* Place a lookout tower */
        Point point2 = new Point(4, 12);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2);

        /* Connect the lookout tower to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a game monitoring message is sent when the dead tree is discovered */
        assertFalse(player0.getDiscoveredLand().contains(point1));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        assertTrue(player0.getDiscoveredLand().contains(point1));

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.removedDeadTrees().size(), 0);
        assertTrue(gameChangesList.discoveredDeadTrees().size() > 0);
        assertTrue(gameChangesList.discoveredDeadTrees().contains(point1));

        /* Verify that the event is only sent once */

        /* Place a second lookout tower to discover more land */
        Point point3 = new Point(12, 12);
        LookoutTower lookoutTower1 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the new lookout tower to the headquarter and wait for it to get constructed and occupied */
        Road road1 = map.placeAutoSelectedRoad(player0, lookoutTower1.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(lookoutTower1);

        Point point4 = new Point(16, 20);

        assertFalse(player0.getDiscoveredLand().contains(point4));

        Utils.adjustInventoryTo(headquarter0, SCOUT, 5);

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower1);

        assertTrue(player0.getDiscoveredLand().contains(point4));

        GameChangesList gameChanges = monitor.getLastEvent();

        Utils.fastForward(100, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.discoveredDeadTrees().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringDecoration() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place a decoration for player 0 to discover */
        Point point2 = new Point(33, 5);
        map.placeDecoration(point2, DecorationType.TOADSTOOL);

        /* Place lookout tower */
        Point point3 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the lookout tower with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        int count = Utils.countMonitoredEventsForNewDecoration(point2, monitor);

        assertEquals(count, 0);
        assertFalse(player0.getDiscoveredLand().contains(point2));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered stone */
        count = Utils.countMonitoredEventsForNewDecoration(point2, monitor);

        assertTrue(player0.getDiscoveredLand().contains(point2));
        assertEquals(count, 1);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringDecorationIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 40);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place a decoration for player 0 to discover */
        Point point2 = new Point(33, 5);
        map.placeDecoration(point2, DecorationType.TOADSTOOL);

        /* Place lookout tower */
        Point point3 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the lookout tower with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the lookout tower to get constructed */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        /* Wait for the lookout tower to get occupied */
        int count = Utils.countMonitoredEventsForNewDecoration(point2, monitor);

        assertEquals(count, 0);
        assertFalse(player0.getDiscoveredLand().contains(point2));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        /* Verify that an event was sent for the newly discovered stone */
        count = Utils.countMonitoredEventsForNewDecoration(point2, monitor);

        assertTrue(player0.getDiscoveredLand().contains(point2));
        assertEquals(count, 1);

        /* Verify that the message is only sent once */
        Utils.fastForward(10, map);

        count = Utils.countMonitoredEventsForNewDecoration(point2, monitor);

        assertTrue(player0.getDiscoveredLand().contains(point2));
        assertEquals(count, 1);
    }
}
