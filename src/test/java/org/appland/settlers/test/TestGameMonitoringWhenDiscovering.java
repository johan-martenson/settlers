package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.BorderChange;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.LookoutTower;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sign;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.TreeSize;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.SCOUT;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Size.LARGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place a tree to discover */
        Point point1 = new Point(33, 5);
        Tree tree0 = map.placeTree(point1, Tree.TreeType.PINE, TreeSize.FULL_GROWN);

        /* Place lookout tower */
        Point point2 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2);

        /* Connect the lookout tower with the headquarter */
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

        assertEquals(gameChangesList.getNewTrees().size(), 1);
        assertEquals(gameChangesList.getNewTrees().get(0), tree0);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringTreeIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place a tree to discover */
        Point point1 = new Point(33, 5);
        Tree tree0 = map.placeTree(point1, Tree.TreeType.PINE, TreeSize.FULL_GROWN);

        /* Place lookout tower */
        Point point2 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2);

        /* Connect the lookout tower with the headquarter */
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

        assertEquals(gameChangesList.getNewTrees().size(), 1);
        assertEquals(gameChangesList.getNewTrees().get(0), tree0);

        /* Verify that the new tree event is not sent again */
        for (GameChangesList changes : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(changes.getNewTrees().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringStone() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place a stone to discover */
        Point point1 = new Point(33, 5);
        Stone stone0 = map.placeStone(point1);

        /* Place lookout tower */
        Point point2 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2);

        /* Connect the lookout tower with the headquarter */
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

        assertEquals(gameChangesList.getNewStones().size(), 1);
        assertEquals(gameChangesList.getNewStones().get(0), stone0);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringStoneIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place a stone to discover */
        Point point1 = new Point(33, 5);
        Stone stone0 = map.placeStone(point1);

        /* Place lookout tower */
        Point point2 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2);

        /* Connect the lookout tower with the headquarter */
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

        assertEquals(gameChangesList.getNewStones().size(), 1);
        assertEquals(gameChangesList.getNewStones().get(0), stone0);

        /* Verify that the event is not sent again */
        Utils.fastForward(200, map);

        for (GameChangesList changes : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(changes.getNewStones().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringFlag() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
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

        /* Connect the lookout tower with the headquarter */
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

        assertEquals(gameChangesList.getNewFlags().size(), 1);
        assertEquals(gameChangesList.getNewFlags().get(0), flag0);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringFlagIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
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

        /* Connect the lookout tower with the headquarter */
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

        assertEquals(gameChangesList.getNewFlags().size(), 1);
        assertEquals(gameChangesList.getNewFlags().get(0), flag0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.getNewFlags().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringHouse() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
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

        /* Connect the lookout tower with the headquarter */
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

        assertEquals(gameChangesList.getNewBuildings().size(), 1);
        assertEquals(gameChangesList.getNewBuildings().get(0), woodcutter0);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringHouseIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
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

        /* Connect the lookout tower with the headquarter */
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

        assertEquals(gameChangesList.getNewBuildings().size(), 1);
        assertEquals(gameChangesList.getNewBuildings().get(0), woodcutter0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.getNewBuildings().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringRoad() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
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

        /* Connect the lookout tower with the headquarter */
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

        assertTrue(gameChangesList.getNewRoads().size() >= 1);
        assertTrue(gameChangesList.getNewRoads().contains(road0));
    }

    @Test
    public void testMonitoringEventWhenDiscoveringRoadIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
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

        /* Connect the lookout tower with the headquarter */
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

        assertTrue(gameChangesList.getNewRoads().size() >= 1);
        assertTrue(gameChangesList.getNewRoads().contains(road0));

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.getNewRoads().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringFlagWithRoads() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
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

        /* Connect the lookout tower with the headquarter */
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

        assertTrue(gameChangesList.getNewFlags().size() >= 1);
        assertTrue(gameChangesList.getNewFlags().contains(flag0));
        assertTrue(gameChangesList.getNewRoads().size() >= 2);
        assertTrue(gameChangesList.getNewRoads().contains(road0));
        assertTrue(gameChangesList.getNewRoads().contains(road1));
    }

    @Test
    public void testMonitoringEventWhenDiscoveringBorder() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
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

        /* Connect the lookout tower with the headquarter */
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

        assertEquals(gameChangesList.getChangedBorders().size(), 1);

        BorderChange borderChange = gameChangesList.getChangedBorders().get(0);

        assertTrue(borderChange.getNewBorder().size() > 0);
        assertEquals(borderChange.getRemovedBorder().size(), 0);
        assertEquals(borderChange.getPlayer(), player1);

        for (Point point : player1.getBorderPoints()) {
            if (player0.getDiscoveredLand().contains(point)) {
                assertTrue(borderChange.getNewBorder().contains(point));
            } else {
                assertFalse(borderChange.getNewBorder().contains(point));
            }
        }

        for (Point point : borderChange.getNewBorder()) {
            assertTrue(player1.getBorderPoints().contains(point));
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringBorderIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
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

        /* Connect the lookout tower with the headquarter */
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

        assertEquals(gameChangesList.getChangedBorders().size(), 1);

        boolean otherBorderFound = false;

        for (BorderChange borderChange : monitor.getLastEvent().getChangedBorders()) {
            if (borderChange.getPlayer().equals(player1)) {
                otherBorderFound = true;

                assertTrue(borderChange.getNewBorder().contains(point2));
                assertEquals(borderChange.getRemovedBorder().size(), 0);

                break;
            }
        }

        assertTrue(otherBorderFound);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.getChangedBorders().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringSign() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place a sign to discover */
        Point point1 = new Point(33, 5);
        Sign sign0 = map.placeSign(IRON, LARGE, point1);

        /* Place lookout tower */
        Point point2 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2);

        /* Connect the lookout tower with the headquarter */
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

        assertEquals(gameChangesList.getNewSigns().size(), 1);
        assertEquals(gameChangesList.getNewSigns().get(0), sign0);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringSignIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place a sign to discover */
        Point point1 = new Point(33, 5);
        Sign sign0 = map.placeSign(IRON, LARGE, point1);

        /* Place lookout tower */
        Point point2 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2);

        /* Connect the lookout tower with the headquarter */
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

        assertEquals(gameChangesList.getNewSigns().size(), 1);
        assertEquals(gameChangesList.getNewSigns().get(0), sign0);

        /* Verify that the new sign event is not sent again */
        for (GameChangesList changes : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(changes.getNewSigns().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringCrop() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
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
        Crop crop0 = map.placeCrop(point2);

        /* Place lookout tower */
        Point point3 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the lookout tower with the headquarter */
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

        assertEquals(gameChangesList.getNewCrops().size(), 1);
        assertEquals(gameChangesList.getNewCrops().get(0), crop0);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringCropIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
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
        Crop crop0 = map.placeCrop(point2);

        /* Place lookout tower */
        Point point3 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the lookout tower with the headquarter */
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

        assertEquals(gameChangesList.getNewCrops().size(), 1);
        assertEquals(gameChangesList.getNewCrops().get(0), crop0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.getNewCrops().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringWorker() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
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

        /* Connect the road with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player1, flag0, headquarter1.getFlag());

        /* Place lookout tower */
        Point point4 = new Point(19, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point4);

        /* Connect the lookout tower with the headquarter */
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

        assertEquals(gameChangesList.getNewWorkers().size(), 1);
        assertEquals(gameChangesList.getNewWorkers().get(0), courier0);
        assertEquals(gameChangesList.getWorkersWithNewTargets().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringWorkerIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
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
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.getNewWorkers().size(), 1);
        assertEquals(gameChangesList.getNewWorkers().get(0), courier0);
        assertEquals(gameChangesList.getWorkersWithNewTargets().size(), 0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertFalse(newChanges.getNewWorkers().contains(courier0));
            assertFalse(newChanges.getWorkersWithNewTargets().contains(courier0));
        }
    }

    @Test
    public void testMonitoringEventWhenDiscoveringBorderThroughEnemyExpansion() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
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

        /* Connect the fortress to the headquarter */
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

        for (BorderChange borderChange : monitor.getLastEvent().getChangedBorders()) {
            if (borderChange.getPlayer().equals(player1) && !borderChange.getNewBorder().isEmpty()) {
                otherBorderFound = true;

                break;
            }
        }

        assertTrue(otherBorderFound);
    }

    @Test
    public void testMonitoringEventWhenDiscoveringBorderThroughEnemyExpansionIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
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

        /* Connect the fortress to the headquarter */
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

        for (BorderChange borderChange : monitor.getLastEvent().getChangedBorders()) {
            if (borderChange.getPlayer().equals(player1) && !borderChange.getNewBorder().isEmpty()) {
                otherBorderFound = true;

                break;
            }
        }

        assertTrue(otherBorderFound);

        /* Verify that the event is only sent once */
        GameChangesList gameChanges = monitor.getLastEvent();

        Utils.fastForward(100, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedBorders().size(), 0);
        }
    }

    @Test
    public void testMonitoringWhenSoldierDies() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

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
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());
        assertFalse(barracks1.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);

        /* Get the defender */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertNotNull(defender);

        /* Wait for the defender to go to the attacker */
        assertEquals(defender.getTarget(), attacker.getPosition());
        assertTrue(barracks1.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Verify that the soldiers are fighting */
        assertTrue(barracks1.isUnderAttack());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that the general beats the private */
        Utils.waitForWorkerToDisappear(defender, map);

        assertFalse(map.getWorkers().contains(defender));
        assertTrue(map.getWorkers().contains(attacker));
        assertFalse(attacker.isFighting());
        assertNotNull(monitor.getLastEvent());

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertTrue(gameChangesList.getRemovedWorkers().size() > 0);
        assertTrue(gameChangesList.getRemovedWorkers().contains(defender));
    }

    @Test
    public void testMonitoringEventWhenDiscoveringDeadTree() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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

        /* Connect the lookout tower to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a game monitoring message is sent when the dead tree is discovered */
        assertFalse(player0.getDiscoveredLand().contains(point1));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        assertTrue(player0.getDiscoveredLand().contains(point1));

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.getRemovedDeadTrees().size(), 0);
        assertTrue(gameChangesList.getDiscoveredDeadTrees().size() > 0);
        assertTrue(gameChangesList.getDiscoveredDeadTrees().contains(point1));
    }

    @Test
    public void testMonitoringEventWhenDiscoveringDeadTreeIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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

        /* Connect the lookout tower to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a game monitoring message is sent when the dead tree is discovered */
        assertFalse(player0.getDiscoveredLand().contains(point1));

        Utils.waitForNonMilitaryBuildingToGetPopulated(lookoutTower0);

        assertTrue(player0.getDiscoveredLand().contains(point1));

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.getRemovedDeadTrees().size(), 0);
        assertTrue(gameChangesList.getDiscoveredDeadTrees().size() > 0);
        assertTrue(gameChangesList.getDiscoveredDeadTrees().contains(point1));

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
            assertEquals(newChanges.getDiscoveredDeadTrees().size(), 0);
        }
    }
}
