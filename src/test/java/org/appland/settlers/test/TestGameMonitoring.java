package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.BorderChange;
import org.appland.settlers.model.Builder;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.Farm;
import org.appland.settlers.model.Farmer;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Forester;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Geologist;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Scout;
import org.appland.settlers.model.Sign;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Stonemason;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.WildAnimal;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.WoodcutterWorker;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.appland.settlers.model.Crop.GrowthState.JUST_PLANTED;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Size.SMALL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestGameMonitoring {

    /*
    TODO:
        - workers and soldiers that die
        - catapulted stone,
        - available road connections (?),
        - road that becomes main road,
        - monitoring of couriers when road is split
        - dead tree that disappears (when a flag is placed)
        - ship - ready and under construction
    *  */

    @Test
    public void testNoMonitoringEventsWhenNothingHappens() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that only wild animal events are sent when nothing happens */
        Utils.fastForward(200, map);

        for (GameChangesList gameChangesList : monitor.getEvents()) {

            for (Worker worker : gameChangesList.getWorkersWithNewTargets()) {
                assertTrue(worker instanceof WildAnimal);
            }

            assertEquals(gameChangesList.getNewTrees().size(), 0);
            assertEquals(gameChangesList.getNewDiscoveredLand().size(), 0);
            assertEquals(gameChangesList.getNewFlags().size(), 0);
            assertEquals(gameChangesList.getNewBuildings().size(), 0);
            assertEquals(gameChangesList.getNewRoads().size(), 0);
            assertEquals(gameChangesList.getNewCrops().size(), 0);
            assertEquals(gameChangesList.getNewSigns().size(), 0);

            assertEquals(gameChangesList.getChangedBorders().size(), 0);

            assertEquals(gameChangesList.getRemovedTrees().size(), 0);
            assertEquals(gameChangesList.getRemovedFlags().size(), 0);
            assertEquals(gameChangesList.getRemovedBuildings().size(), 0);
            assertEquals(gameChangesList.getRemovedRoads().size(), 0);
            assertEquals(gameChangesList.getRemovedCrops().size(), 0);
            assertEquals(gameChangesList.getRemovedSigns().size(), 0);
            assertEquals(gameChangesList.getRemovedWorkers().size(), 0);

        }
    }

    @Test
    public void testMonitoringEventWhenFlagIsAdded() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify an event is sent when a flag is placed */
        assertEquals(monitor.getEvents().size(), 0);

        Point point1 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point1);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getNewFlags().size(), 1);
        assertEquals(gameChanges.getNewFlags().get(0), flag0);

        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 0);
        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenFlagIsAddedOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place a flag and get the corresponding event */
        assertEquals(monitor.getEvents().size(), 0);

        Point point1 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point1);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertEquals(gameChanges.getNewFlags().size(), 1);

        /* Verify that the event is not sent again */
        Utils.fastForward(100, map);

        assertEquals(monitor.getEvents().size(), 1);
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenFlagIsAdded() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        /* Verify that no event is sent when a flag is placed for player 0 */
        assertEquals(monitor.getEvents().size(), 0);

        Point point2 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point2);

        map.stepTime();

        assertFalse(monitor.getEvents().size() > 1);

        if (monitor.getEvents().size() == 1) {
            assertTrue(monitor.getLastEvent().getNewFlags().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenFlagIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point1);

        map.stepTime();

        /* Verify an event is sent when a flag is removed */
        assertEquals(monitor.getEvents().size(), 1);

        map.removeFlag(flag0);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 2);

        GameChangesList gameChanges = monitor.getEvents().get(1);

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 1);
        assertEquals(gameChanges.getRemovedFlags().get(0), flag0);

        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 0);
        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenFlagIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place flag */
        Point point2 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point2);

        map.stepTime();

        /* Set up monitoring subscription for the player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        /* Verify that no event is sent when a flag is removed for player 0 */
        assertEquals(monitor.getEvents().size(), 0);

        map.removeFlag(flag0);

        map.stepTime();

        assertFalse(monitor.getEvents().size() > 1);

        if (monitor.getEvents().size() == 1) {
            assertTrue(monitor.getLastEvent().getRemovedFlags().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenFlagIsRemovedOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point1);

        map.stepTime();

        /* Remove a flag and get the event sent */
        assertEquals(monitor.getEvents().size(), 1);

        map.removeFlag(flag0);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 2);

        /* Verify that the event is only sent once */
        Utils.fastForward(100, map);

        assertEquals(monitor.getEvents().size(), 2);
    }

    @Test
    public void testMonitoringEventWhenRoadIsAdded() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place flags */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(14, 10);
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify an event is sent when a road is placed */
        assertEquals(monitor.getEvents().size(), 0);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getNewRoads().size(), 1);
        assertEquals(gameChanges.getNewRoads().get(0), road0);

        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 0);
        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenRoadIsAddedOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place flags */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(14, 10);
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place a road and get the event */
        assertEquals(monitor.getEvents().size(), 0);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getNewRoads().size(), 1);

        /* Verify that no more events are sent */
        Utils.fastForward(100, map);

        assertEquals(monitor.getEvents().size(), 1);
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenRoadIsAdded() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place flags */
        Point point2 = new Point(10, 10);
        Point point3 = new Point(14, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        /* Verify that no event is sent when a road is placed for player 0 */
        assertEquals(monitor.getEvents().size(), 0);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        map.stepTime();

        assertFalse(monitor.getEvents().size() > 1);

        if (monitor.getEvents().size() == 1) {
            assertTrue(monitor.getLastEvent().getNewRoads().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place flags */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(14, 10);
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place a road */
        assertEquals(monitor.getEvents().size(), 0);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);
        GameChangesList gameChanges = monitor.getEvents().get(0);
        assertEquals(gameChanges.getNewRoads().size(), 1);

        /* Remove the road and verify that an event is sent */
        map.removeRoad(road0);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 2);

        gameChanges = monitor.getEvents().get(1);

        assertEquals(gameChanges.getRemovedRoads().size(), 1);
        assertEquals(gameChanges.getNewRoads().size(), 0);
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenRoadIsRemovedIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place flags */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(14, 10);
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place a road */
        assertEquals(monitor.getEvents().size(), 0);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertEquals(gameChanges.getNewRoads().size(), 1);

        /* Remove the road and get an event */
        map.removeRoad(road0);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 2);

        gameChanges = monitor.getEvents().get(1);

        assertEquals(gameChanges.getRemovedRoads().size(), 1);

        /* Verify that no more messages are sent */
        Utils.fastForward(100, map);

        assertEquals(monitor.getEvents().size(), 2);

        gameChanges = monitor.getEvents().get(1);

        assertEquals(gameChanges.getRemovedRoads().size(), 1);
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place flags */
        Point point2 = new Point(10, 10);
        Point point3 = new Point(14, 10);
        Flag flag0 = map.placeFlag(player0, point2);
        Flag flag1 = map.placeFlag(player0, point3);

        map.stepTime();

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        /* Place a road */
        assertEquals(monitor.getEvents().size(), 0);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        map.stepTime();

        /* Remove the road and verify that no event is sent to player 1 */
        map.removeRoad(road0);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getRemovedRoads().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenHouseIsAdded() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that three events are sent when a house is placed - for the house, the road, and the flag */
        assertEquals(monitor.getEvents().size(), 0);

        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getNewBuildings().size(), 1);
        assertEquals(gameChanges.getNewFlags().size(), 1);
        assertEquals(gameChanges.getNewRoads().size(), 1);

        assertEquals(gameChanges.getNewBuildings().get(0), woodcutter0);

        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenHouseIsAddedIsSentOnlyOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that three events are sent when a house is placed - for the house, the road, and the flag */
        assertEquals(monitor.getEvents().size(), 0);

        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getNewBuildings().size(), 1);
        assertEquals(gameChanges.getNewFlags().size(), 1);
        assertEquals(gameChanges.getNewRoads().size(), 1);

        /* Verify that the events are only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(gameChanges)) {
            assertTrue(gameChangesList.getNewBuildings().isEmpty());
            assertTrue(gameChangesList.getNewFlags().isEmpty());
            assertTrue(gameChangesList.getNewRoads().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenHouseChangesFromPlannedToUnderConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(woodcutter0.isPlanned());

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Wait for a builder to start walking to the woodcutter */
        Builder builder = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that an event is sent when a house changes from planned to under construction */
        assertEquals(monitor.getEvents().size(), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, builder, woodcutter0.getPosition());

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 1);
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);

        assertEquals(gameChanges.getChangedBuildings().get(0), woodcutter0);

        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenHouseChangesFromPlannedToUnderConstructionIsSentOnlyOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(woodcutter0.isPlanned());

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Wait for a builder to start walking to the woodcutter */
        Builder builder = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that an event is sent when a house changes from planned to under construction */
        assertEquals(monitor.getEvents().size(), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, builder, woodcutter0.getPosition());

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 1);
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);

        assertEquals(gameChanges.getChangedBuildings().get(0), woodcutter0);

        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);

        /* Verify that the events are only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(gameChangesList.getChangedBuildings().size(), 0);
            assertTrue(gameChangesList.getNewFlags().isEmpty());
            assertTrue(gameChangesList.getNewRoads().isEmpty());
        }
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenHouseIsAdded() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        /* Verify that no events are sent when a house is placed - for the house, the road, and the flag */
        assertEquals(monitor.getEvents().size(), 0);

        Point point2 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getNewBuildings().isEmpty());
            assertTrue(gameChangesList.getNewFlags().isEmpty());
            assertTrue(gameChangesList.getNewRoads().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenHouseIsTornDown() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the woodcutter */
        Utils.constructHouse(woodcutter0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that the event is sent when the house is removed */
        assertEquals(monitor.getEvents().size(), 0);

        Road road0 = map.getRoad(woodcutter0.getFlag().getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 1);
        assertEquals(gameChanges.getChangedBuildings().get(0), woodcutter0);
        assertEquals(gameChanges.getRemovedRoads().size(), 1);
        assertEquals(gameChanges.getRemovedRoads().get(0), road0);

        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 1);
        assertTrue(gameChanges.getWorkersWithNewTargets().contains(woodcutter0.getBuilder()));
        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenHouseIsTornDownIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the woodcutter */
        Utils.constructHouse(woodcutter0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that the event is sent when the house is removed */
        assertEquals(monitor.getEvents().size(), 0);

        Road road0 = map.getRoad(woodcutter0.getFlag().getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 1);
        assertEquals(gameChanges.getChangedBuildings().get(0), woodcutter0);

        /* Verify that no more changed building messages are sent before the building burns down */
        Utils.fastForward(30, map);

        for (GameChangesList newGameChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newGameChanges.getChangedBuildings().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenPlannedHouseIsTornDown() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that the event is sent when the house is removed */
        assertEquals(monitor.getEvents().size(), 0);

        Road road0 = map.getRoad(woodcutter0.getFlag().getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 1);
        assertEquals(gameChanges.getRemovedBuildings().get(0), woodcutter0);
        assertEquals(gameChanges.getRemovedRoads().size(), 1);
        assertEquals(gameChanges.getRemovedRoads().get(0), road0);

        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 0);
        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenPlannedHouseIsTornDownIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that the event is sent when the house is removed */
        assertEquals(monitor.getEvents().size(), 0);

        Road road0 = map.getRoad(woodcutter0.getFlag().getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 1);
        assertEquals(gameChanges.getRemovedBuildings().get(0), woodcutter0);
        assertEquals(gameChanges.getRemovedRoads().size(), 1);
        assertEquals(gameChanges.getRemovedRoads().get(0), road0);

        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 0);
        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);

        /* Verify that no more changed building messages are sent before the building burns down */
        Utils.fastForward(30, map);

        for (GameChangesList newGameChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newGameChanges.getRemovedBuildings().size(), 0);
            assertEquals(newGameChanges.getChangedBuildings().size(), 0);

        }
    }

    @Test
    public void testMonitoringEventWhenHouseIsConstructed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Verify that the event is sent when the house is constructed */
        assertEquals(monitor.getEvents().size(), 0);

        Utils.waitForBuildingToBeConstructed(woodcutter0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 1);
        assertEquals(gameChanges.getChangedBuildings().get(0), woodcutter0);
    }

    @Test
    public void testMonitoringEventWhenHouseIsConstructedIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Verify that the event is sent when the house is constructed */
        assertEquals(monitor.getEvents().size(), 0);

        Utils.waitForBuildingToBeConstructed(woodcutter0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 1);
        assertEquals(gameChanges.getChangedBuildings().get(0), woodcutter0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedBuildings().size(), 0);
        }
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenHouseIsConstructed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place woodcutter */
        Point point2 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Verify that no event is sent to player 1 when the house is constructed */
        Utils.waitForBuildingToBeConstructed(woodcutter0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertEquals(gameChanges.getChangedBuildings().size(), 0);
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenHouseIsTornDown() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place woodcutter */
        Point point2 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);

        map.stepTime();

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        /* Verify that no event is sent to player 1 when the house is removed */
        assertEquals(monitor.getEvents().size(), 0);

        woodcutter0.tearDown();

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getChangedBuildings().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenHouseIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        Utils.constructHouse(woodcutter0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that the event is sent when the house is removed */
        assertEquals(monitor.getEvents().size(), 0);

        woodcutter0.tearDown();

        map.stepTime();

        /* Wait for the house to burn down */
        assertEquals(monitor.getEvents().size(), 1);

        Utils.waitForBuildingToBurnDown(woodcutter0);

        map.stepTime();

        assertTrue(monitor.getEvents().size() >= 2);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);

        assertEquals(gameChanges.getChangedBuildings().size(), 1);
        assertEquals(gameChanges.getChangedBuildings().get(0), woodcutter0);

        assertEquals(gameChanges.getRemovedRoads().size(), 0);
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 0);
        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenHouseIsDestroyedIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        Utils.constructHouse(woodcutter0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that the event is sent when the house is removed */
        assertEquals(monitor.getEvents().size(), 0);

        woodcutter0.tearDown();

        map.stepTime();

        /* Wait for the house to burn down */
        assertEquals(monitor.getEvents().size(), 1);

        Utils.waitForBuildingToBurnDown(woodcutter0);

        map.stepTime();

        assertTrue(monitor.getEvents().size() >= 2);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 1);
        assertEquals(gameChanges.getChangedBuildings().get(0), woodcutter0);

        /* Verify that no more messages are sent before the house disappears */
        Utils.fastForward(10, map);

        assertEquals(monitor.getEventsAfterEvent(gameChanges).size(), 0);
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenHouseIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place woodcutter */
        Point point2 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);

        Utils.constructHouse(woodcutter0);

        map.stepTime();

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        /* Verify that the event is sent when the house is removed */
        assertEquals(monitor.getEvents().size(), 0);

        woodcutter0.tearDown();

        map.stepTime();

        /* Wait for the house to burn down */
        Utils.waitForBuildingToBurnDown(woodcutter0);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getChangedBuildings().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenHouseDisappears() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        Utils.constructHouse(woodcutter0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that the event is sent when the house is removed */
        assertEquals(monitor.getEvents().size(), 0);

        woodcutter0.tearDown();

        map.stepTime();

        /* Wait for the house to burn down */
        assertEquals(monitor.getEvents().size(), 1);

        Utils.waitForBuildingToBurnDown(woodcutter0);

        map.stepTime();

        assertTrue(monitor.getEvents().size() >= 2);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 1);
        assertEquals(gameChanges.getChangedBuildings().get(0), woodcutter0);

        /* Verify that an event is sent when the building disappears */
        assertTrue(woodcutter0.isDestroyed());
        assertTrue(monitor.getEvents().size() >= 2);

        Utils.waitForBuildingToDisappear(woodcutter0);

        assertTrue(monitor.getEvents().size() >= 3);

        gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 1);
        assertEquals(gameChanges.getRemovedBuildings().get(0), woodcutter0);

        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 0);
        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenHouseDisappearsIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        Utils.constructHouse(woodcutter0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that the event is sent when the house is removed */
        assertEquals(monitor.getEvents().size(), 0);

        woodcutter0.tearDown();

        map.stepTime();

        /* Wait for the house to burn down */
        assertEquals(monitor.getEvents().size(), 1);

        Utils.waitForBuildingToBurnDown(woodcutter0);

        map.stepTime();

        assertTrue(monitor.getEvents().size() >= 2);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 1);
        assertEquals(gameChanges.getChangedBuildings().get(0), woodcutter0);

        /* Verify that an event is sent when the building disappears */
        assertTrue(woodcutter0.isDestroyed());
        assertTrue(monitor.getEvents().size() >= 2);

        Utils.waitForBuildingToDisappear(woodcutter0);

        assertTrue(monitor.getEvents().size() >= 3);

        gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 1);
        assertEquals(gameChanges.getRemovedBuildings().get(0), woodcutter0);

        /* Verify that no more message is sent */
        Utils.fastForward(100, map);

        for (GameChangesList newGameChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newGameChanges.getRemovedBuildings().size(), 0);
        }
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenHouseDisappears() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place woodcutter */
        Point point2 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);

        Utils.constructHouse(woodcutter0);

        map.stepTime();

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        /* Verify that no event is sent to player 1 when the house is removed */
        assertEquals(monitor.getEvents().size(), 0);

        woodcutter0.tearDown();

        map.stepTime();

        /* Wait for the house to burn down */
        Utils.waitForBuildingToBurnDown(woodcutter0);

        map.stepTime();

        /* Verify that no event is sent when the building disappears */
        assertTrue(woodcutter0.isDestroyed());

        Utils.waitForBuildingToDisappear(woodcutter0);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getRemovedBuildings().isEmpty());
            assertTrue(gameChangesList.getRemovedRoads().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenWorkerIsAdded() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Verify an event is sent when the courier and the storage worker appear */
        map.stepTime();

        assertEquals(map.getWorkers().size(), 2);
        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertTrue(headquarter0.getWorker().isInsideBuilding());
        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 1);
        assertEquals(gameChanges.getWorkersWithNewTargets().get(0), road0.getCourier());

        assertEquals(gameChanges.getNewRoads().size(), 1);

        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenWorkerIsAddedIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place road and get the events for the road and the courier */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertTrue(headquarter0.getWorker().isInsideBuilding());
        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 1);
        assertEquals(gameChanges.getNewRoads().size(), 1);

        /* Verify that no more events are sent when nothing happens */
        Utils.fastForward(100, map);

        assertEquals(monitor.getEvents().size(), 1);
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenWorkerIsAdded() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place flag */
        Point point2 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point2);

        map.stepTime();

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Verify an event is sent when the courier and the storage worker appear */
        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getWorkersWithNewTargets().contains(road0.getCourier()));
        }
    }

    @Test
    public void testMonitoringEventWhenWorkerGoesBackToStorage() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place road and get the events for the road and the courier */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertTrue(headquarter0.getWorker().isInsideBuilding());
        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 1);
        assertEquals(gameChanges.getNewRoads().size(), 1);

        /* Wait for the courier walk onto the road */
        Courier courier0 = road0.getCourier();

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, headquarter0.getFlag().getPosition());

        assertEquals(monitor.getEvents().size(), 1);

        /* Verify that an event is sent when the worker goes back to storage */
        assertEquals(monitor.getEvents().size(), 1);
        assertEquals(road0.getCourier(), courier0);

        map.removeRoad(road0);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 2);

        gameChanges = monitor.getEvents().get(1);

        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 1);
        assertEquals(gameChanges.getWorkersWithNewTargets().get(0), courier0);
        assertEquals(gameChanges.getRemovedWorkers().size(), 0);
        assertEquals(courier0.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, headquarter0.getPosition());

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 3);

        gameChanges = monitor.getEvents().get(2);

        assertEquals(gameChanges.getRemovedWorkers().size(), 1);
        assertEquals(gameChanges.getRemovedWorkers().get(0), courier0);

        assertEquals(gameChanges.getNewRoads().size(), 0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 0);
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenWorkerGoesBackToStorageIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place road and get the events for the road and the courier */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertTrue(headquarter0.getWorker().isInsideBuilding());
        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 1);
        assertEquals(gameChanges.getNewRoads().size(), 1);

        /* Wait for the courier walk onto the road */
        Courier courier0 = road0.getCourier();

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, headquarter0.getFlag().getPosition());

        assertEquals(monitor.getEvents().size(), 1);

        /* Remove the road and get an event when the worker goes back to the storage */
        assertEquals(monitor.getEvents().size(), 1);
        assertEquals(road0.getCourier(), courier0);

        map.removeRoad(road0);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 2);

        gameChanges = monitor.getEvents().get(1);

        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 1);
        assertEquals(gameChanges.getWorkersWithNewTargets().get(0), courier0);
        assertEquals(gameChanges.getRemovedWorkers().size(), 0);
        assertEquals(courier0.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, headquarter0.getPosition());

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 3);

        gameChanges = monitor.getEvents().get(2);

        assertEquals(gameChanges.getRemovedWorkers().size(), 1);
        assertEquals(gameChanges.getRemovedWorkers().get(0), courier0);

        /* Verify that no more messages are sent */
        Utils.fastForward(100, map);

        assertEquals(monitor.getEvents().size(), 3);
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenWorkerGoesBackToStorage() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place flag */
        Point point2 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point2);

        map.stepTime();

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        /* Place road and get the events for the road and the courier */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        map.stepTime();

        assertTrue(headquarter0.getWorker().isInsideBuilding());

        /* Wait for the courier walk onto the road */
        Courier courier0 = road0.getCourier();

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, headquarter0.getFlag().getPosition());

        /* Verify that no event is sent to player 1 when the worker goes back to storage */
        assertEquals(road0.getCourier(), courier0);

        map.removeRoad(road0);

        map.stepTime();

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, headquarter0.getPosition());

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.getRemovedWorkers().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenRoadIsSplit() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place flags */
        Point point1 = new Point(14, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        map.stepTime();

        /* Place a road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Wait for a courier to come out and go to the road */
        Courier courier0 = Utils.waitForRoadToGetAssignedCourier(map, road0);
        Point point3 = new Point(10, 4);

        assertEquals(courier0.getTarget(), point3);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, point3);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Split the road and verify that an event is sent: new flag, new roads, removed road, courier with new target */
        Flag flag2 = map.placeFlag(player0, point3);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);
        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertEquals(gameChanges.getNewFlags().size(), 1);
        assertEquals(gameChanges.getNewFlags().get(0), flag2);
        assertEquals(gameChanges.getRemovedRoads().size(), 1);
        assertEquals(gameChanges.getRemovedRoads().get(0), road0);
        assertEquals(gameChanges.getNewRoads().size(), 2);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 2);
        assertTrue(gameChanges.getWorkersWithNewTargets().contains(courier0));

        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenRoadIsSplit() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        map.stepTime();

        /* Place flags */
        Point point2 = new Point(14, 4);
        Flag flag0 = map.placeFlag(player0, point2);

        map.stepTime();

        /* Place a road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Wait for a courier to come out and go to the road */
        Courier courier0 = Utils.waitForRoadToGetAssignedCourier(map, road0);
        Point point3 = new Point(10, 4);

        assertEquals(courier0.getTarget(), point3);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, point3);

        map.stepTime();

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Split the road and verify that an event is sent: new flag, new roads, removed road, courier with new target */
        Flag flag2 = map.placeFlag(player0, point3);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.getNewFlags().size(), 0);
            assertEquals(gameChangesList.getRemovedRoads().size(), 0);
            assertEquals(gameChangesList.getNewRoads().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenForesterPlantsTree() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        Utils.constructHouse(foresterHut);

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        /* Let the forester rest */
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);
        assertTrue(forester.isTraveling());

        Point point = forester.getTarget();

        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());

        for (int i = 0; i < 19; i++) {
            assertTrue(forester.isPlanting());
            map.stepTime();
        }

        assertTrue(forester.isPlanting());
        assertFalse(map.isTreeAtPoint(point));

        if (!monitor.getEvents().isEmpty()) {
            assertTrue(monitor.getLastEvent().getNewTrees().isEmpty());
        }

        map.stepTime();

        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));
        assertNull(forester.getCargo());
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertEquals(gameChanges.getNewTrees().size(), 1);
        assertEquals(gameChanges.getNewTrees().get(0), map.getTreeAtPoint(point));
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 1);

        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);

        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenForesterPlantsTree() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place forester hut */
        Point point2 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point2);

        /* Construct the forester hut */
        Utils.constructHouse(foresterHut);

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        /* Let the forester rest */
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);
        assertTrue(forester.isTraveling());

        Point point = forester.getTarget();

        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());

        for (int i = 0; i < 19; i++) {
            assertTrue(forester.isPlanting());
            map.stepTime();
        }

        assertTrue(forester.isPlanting());
        assertFalse(map.isTreeAtPoint(point));

        if (!monitor.getEvents().isEmpty()) {
            assertTrue(monitor.getLastEvent().getNewTrees().isEmpty());
        }

        map.stepTime();

        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));
        assertNull(forester.getCargo());

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.getNewTrees().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenForesterPlantsTreeIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        Utils.constructHouse(foresterHut);

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        /* Let the forester rest */
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);
        assertTrue(forester.isTraveling());

        Point point = forester.getTarget();

        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());

        for (int i = 0; i < 19; i++) {
            assertTrue(forester.isPlanting());
            map.stepTime();
        }

        assertTrue(forester.isPlanting());
        assertFalse(map.isTreeAtPoint(point));

        map.stepTime();

        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));
        assertNull(forester.getCargo());
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertEquals(gameChanges.getNewTrees().size(), 1);
        assertEquals(gameChanges.getNewTrees().get(0), map.getTreeAtPoint(point));
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 1);

        /* Verify that there is no more event sent before the forester plants a new tree */
        int amountEvents = monitor.getEvents().size();

        for (int i = 0; i < 10; i++) {
            map.stepTime();

            if (monitor.getEvents().size() > amountEvents) {
                for (GameChangesList changes : monitor.getEventsAfterEvent(gameChanges)) {
                    assertEquals(changes.getNewTrees().size(), 0);
                }
            }
        }
    }

    @Test
    public void testMonitoringEventWhenWoodcutterCutsDownTree() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the forester hut */
        Utils.constructHouse(woodcutter);

        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(wcWorker, woodcutter);


        /* Wait for the woodcutter to rest */
        Utils.fastForward(99, map);

        assertTrue(wcWorker.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(wcWorker.isInsideBuilding());

        Point point = wcWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(wcWorker.isTraveling());

        /* Let the woodcutter reach the tree and start cutting */
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        map.stepTime();

        assertTrue(wcWorker.isCuttingTree());
        assertNull(wcWorker.getCargo());

        /* Wait for the woodcutter to finish cutting the tree */
        Tree tree0 = map.getTreeAtPoint(point);

        Utils.waitForTreeToGetCutDown(tree0, map);

        /* Verify that the woodcutter stopped cutting */
        assertFalse(wcWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));
        assertNotNull(wcWorker.getCargo());
        assertEquals(wcWorker.getCargo().getMaterial(), WOOD);

        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertEquals(gameChanges.getRemovedTrees().size(), 1);
        assertEquals(gameChanges.getRemovedTrees().get(0), tree0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 1);
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenWoodcutterCutsDownTree() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point3 = new Point(10, 4);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point3);

        /* Construct the forester hut */
        Utils.constructHouse(woodcutter);

        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(wcWorker, woodcutter);

        /* Wait for the woodcutter to rest */
        Utils.fastForward(99, map);

        assertTrue(wcWorker.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(wcWorker.isInsideBuilding());

        Point point = wcWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(wcWorker.isTraveling());

        /* Let the woodcutter reach the tree and start cutting */
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        map.stepTime();

        assertTrue(wcWorker.isCuttingTree());
        assertNull(wcWorker.getCargo());

        /* Wait for the woodcutter to finish cutting the tree */
        Tree tree0 = map.getTreeAtPoint(point);

        Utils.waitForTreeToGetCutDown(tree0, map);

        /* Verify that the woodcutter stopped cutting */
        assertFalse(wcWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));
        assertNotNull(wcWorker.getCargo());
        assertEquals(wcWorker.getCargo().getMaterial(), WOOD);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.getRemovedTrees().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenWoodcutterCutsDownTreeIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the forester hut */
        Utils.constructHouse(woodcutter);

        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(wcWorker, woodcutter);


        /* Wait for the woodcutter to rest */
        Utils.fastForward(99, map);

        assertTrue(wcWorker.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(wcWorker.isInsideBuilding());

        Point point = wcWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(wcWorker.isTraveling());

        /* Let the woodcutter reach the tree and start cutting */
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        map.stepTime();

        assertTrue(wcWorker.isCuttingTree());
        assertNull(wcWorker.getCargo());

        /* Wait for the woodcutter to finish cutting the tree */
        Tree tree0 = map.getTreeAtPoint(point);

        for (int i = 0; i < 49; i++) {
            assertTrue(wcWorker.isCuttingTree());
            assertTrue(map.isTreeAtPoint(point));

            map.stepTime();
        }

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.getNewTrees().size(), 0);
        }

        /* Verify that the woodcutter stopped cutting */
        assertFalse(wcWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));
        assertNotNull(wcWorker.getCargo());
        assertEquals(wcWorker.getCargo().getMaterial(), WOOD);

        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertEquals(gameChanges.getRemovedTrees().size(), 1);
        assertEquals(gameChanges.getRemovedTrees().get(0), tree0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 1);

        /* Verify that no more messages are sent before the worker gets home */
        int amountEvents = monitor.getEvents().size();

        for (int i = 0; i < 10; i++) {
            map.stepTime();

            if (monitor.getEvents().size() > amountEvents) {
                for (GameChangesList changes : monitor.getEventsAfterEvent(gameChanges)) {
                    assertEquals(changes.getRemovedTrees().size(), 0);
                }
            }
        }
    }

    @Test
    public void testMonitoringEventWhenStoneDisappearsAfterAllHasBeenRetrieved() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point2 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(player0), point2);

        /* Construct the quarry */
        Utils.constructHouse(quarry);

        /* Connect the quarry to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), quarry.getFlag());

        /* Place stone */
        Point point1 = new Point(12, 4);
        Stone stone0 = map.placeStone(point1);

        /* Remove all except the last part of the stone */
        for (int i = 0; i < 9; i++) {
            stone0.removeOnePart();

            assertTrue(map.isStoneAtPoint(point1));
        }

        /* Let the stonemason remove the final part of the stone and verify that an event is sent */
        Stonemason stonemason = Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0).get(0);

        Utils.waitForStonemasonToStartGettingStone(map, stonemason);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        Utils.waitForStonemasonToFinishGettingStone(map, stonemason);

        assertFalse(map.isStoneAtPoint(point1));

        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertEquals(gameChanges.getRemovedStones().size(), 1);
        assertEquals(gameChanges.getRemovedStones().get(0), stone0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 1);

        assertEquals(gameChanges.getRemovedTrees().size(), 0);
        assertEquals(gameChanges.getNewTrees().size(), 0);
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);

        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenStoneDisappearsAfterAllHasBeenRetrievedIsSentOnlyOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point2 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(player0), point2);

        /* Construct the quarry */
        Utils.constructHouse(quarry);

        /* Connect the quarry to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), quarry.getFlag());

        /* Place stone */
        Point point1 = new Point(12, 4);
        Stone stone0 = map.placeStone(point1);

        /* Remove all except the last part of the stone */
        for (int i = 0; i < 9; i++) {
            stone0.removeOnePart();

            assertTrue(map.isStoneAtPoint(point1));
        }

        /* Let the stonemason remove the final part of the stone and verify that an event is sent */
        Stonemason stonemason = Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0).get(0);

        Utils.waitForStonemasonToStartGettingStone(map, stonemason);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        Utils.waitForStonemasonToFinishGettingStone(map, stonemason);

        assertFalse(map.isStoneAtPoint(point1));

        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertEquals(gameChanges.getRemovedStones().size(), 1);
        assertEquals(gameChanges.getRemovedStones().get(0), stone0);
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 1);

        /* Verify that no more messages are sent before the stonemason is back in the quarry */
        int amountEvents = monitor.getEvents().size();

        for (int i = 0; i < 10; i++) {
            map.stepTime();

            if (monitor.getEvents().size() > amountEvents) {
                for (GameChangesList changes : monitor.getEventsAfterEvent(gameChanges)) {
                    assertEquals(changes.getRemovedStones().size(), 0);
                }
            }
        }
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenStoneDisappearsAfterAllHasBeenRetrieved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place quarry */
        Point point2 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(player0), point2);

        /* Construct the quarry */
        Utils.constructHouse(quarry);

        /* Connect the quarry to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), quarry.getFlag());

        /* Place stone */
        Point point3 = new Point(12, 4);
        Stone stone0 = map.placeStone(point3);

        /* Remove all except the last part of the stone */
        for (int i = 0; i < 9; i++) {
            stone0.removeOnePart();

            assertTrue(map.isStoneAtPoint(point3));
        }

        /* Let the stonemason remove the final part of the stone and verify that an event is sent */
        Stonemason stonemason = Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0).get(0);

        Utils.waitForStonemasonToStartGettingStone(map, stonemason);

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that no message is sent to player 1 when the stone is gone */
        Utils.waitForStonemasonToFinishGettingStone(map, stonemason);

        assertFalse(map.isStoneAtPoint(point1));

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.getRemovedStones().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenGeologistDoesResearchAndPutsUpSign() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist) worker;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to reach the first site to investigate */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that an event is sent when the geologist investigates the point */
        Point site = geologist.getPosition();

        for (int i = 0; i < 20; i++) {
            assertTrue(geologist.isInvestigating());

            map.stepTime();
        }

        assertFalse(geologist.isInvestigating());
        assertNotNull(map.getSignAtPoint(site));

        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertEquals(gameChanges.getNewSigns().size(), 1);
        assertEquals(gameChanges.getNewSigns().get(0), map.getSignAtPoint(site));
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 1);

        assertEquals(gameChanges.getRemovedStones().size(), 0);
        assertEquals(gameChanges.getRemovedTrees().size(), 0);
        assertEquals(gameChanges.getNewTrees().size(), 0);
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);

        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenGeologistDoesResearchAndPutsUpSignIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist) worker;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to reach the first site to investigate */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that an event is sent when the geologist investigates the point */
        Point site = geologist.getPosition();

        for (int i = 0; i < 20; i++) {
            assertTrue(geologist.isInvestigating());

            map.stepTime();
        }

        assertFalse(geologist.isInvestigating());
        assertNotNull(map.getSignAtPoint(site));

        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertEquals(gameChanges.getNewSigns().size(), 1);
        assertEquals(gameChanges.getNewSigns().get(0), map.getSignAtPoint(site));
        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 1);

        /* Verify that no more messages are sent before the geologist places a new sign */
        int amountEvents = monitor.getEvents().size();

        for (int i = 0; i < 6; i++) {
            map.stepTime();

            if (monitor.getEvents().size() > amountEvents) {
                for (GameChangesList changes : monitor.getEventsAfterEvent(gameChanges)) {
                    assertEquals(changes.getNewSigns().size(), 0);
                }
            }
        }
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenGeologistDoesResearchAndPutsUpSign() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place flag */
        Point point2 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point2);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist) worker;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to reach the first site to investigate */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that an event is sent when the geologist investigates the point */
        Point site = geologist.getPosition();

        for (int i = 0; i < 20; i++) {
            assertTrue(geologist.isInvestigating());

            map.stepTime();
        }

        assertFalse(geologist.isInvestigating());
        assertNotNull(map.getSignAtPoint(site));

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.getRemovedSigns().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenSignExpires() throws Exception {

        /* Create a new game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place sign */
        Point point1 = new Point(6, 6);
        map.placeSign(IRON, SMALL, point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that a monitoring event is sent when the sign disappears */
        Sign sign0 = map.getSignAtPoint(point1);

        Utils.waitForSignToDisappear(map, sign0);

        assertFalse(map.isSignAtPoint(point1));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertEquals(gameChanges.getRemovedSigns().size(), 1);
        assertEquals(gameChanges.getRemovedSigns().get(0), sign0);

        assertEquals(gameChanges.getNewSigns().size(), 0);
        assertEquals(gameChanges.getRemovedStones().size(), 0);
        assertEquals(gameChanges.getRemovedTrees().size(), 0);
        assertEquals(gameChanges.getNewTrees().size(), 0);
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);

        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenSignExpiresIsSentOnlyOnce() throws Exception {

        /* Create a new game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place sign */
        Point point1 = new Point(6, 6);
        map.placeSign(IRON, SMALL, point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that a monitoring event is sent when the sign disappears */
        Sign sign0 = map.getSignAtPoint(point1);

        Utils.waitForSignToDisappear(map, sign0);

        assertFalse(map.isSignAtPoint(point1));
        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertEquals(gameChanges.getRemovedSigns().size(), 1);
        assertEquals(gameChanges.getRemovedSigns().get(0), sign0);

        /* Verify that no more events are sent */
        int amountEvents = monitor.getEvents().size();

        for (int i = 0; i < 10; i++) {
            map.stepTime();

            if (monitor.getEvents().size() > amountEvents) {
                for (GameChangesList changes : monitor.getEventsAfterEvent(gameChanges)) {
                    assertEquals(changes.getRemovedSigns().size(), 0);
                }
            }
        }
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenSignExpires() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place sign */
        Point point2 = new Point(6, 6);
        map.placeSign(IRON, SMALL, point2);

        map.stepTime();

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that a monitoring event is sent when the sign disappears */
        Sign sign0 = map.getSignAtPoint(point2);

        Utils.waitForSignToDisappear(map, sign0);

        assertFalse(map.isSignAtPoint(point0));

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.getRemovedSigns().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenHouseIsPlacedOnSign() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place sign */
        Point point1 = new Point(10, 4);
        Sign sign0 = map.placeSign(IRON, SMALL, point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that placing a house on the sign sends an event where the sign is gone */
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertEquals(gameChanges.getRemovedSigns().size(), 1);
        assertEquals(gameChanges.getRemovedSigns().get(0), sign0);
        assertEquals(gameChanges.getNewFlags().size(), 1);
        assertEquals(gameChanges.getNewRoads().size(), 1);
        assertEquals(gameChanges.getNewBuildings().size(), 1);

        assertEquals(gameChanges.getWorkersWithNewTargets().size(), 0);
        assertEquals(gameChanges.getRemovedTrees().size(), 0);
        assertEquals(gameChanges.getNewTrees().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenHouseIsPlacedOnSignIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place sign */
        Point point1 = new Point(10, 4);
        Sign sign0 = map.placeSign(IRON, SMALL, point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that placing a house on the sign sends an event where the sign is gone */
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertEquals(gameChanges.getRemovedSigns().size(), 1);
        assertEquals(gameChanges.getRemovedSigns().get(0), sign0);

        /* Verify that the event is not sent again */
        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(gameChangesList.getRemovedSigns().size(), 0);
        }
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenHouseIsPlacedOnSign() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place sign */
        Point point3 = new Point(10, 4);
        Sign sign0 = map.placeSign(IRON, SMALL, point3);

        map.stepTime();

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that placing a house on the sign sends an event where the sign is gone */
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point3);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getRemovedSigns().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventFarmerPlantsWhenThereAreFreeSpotsAndNothingToHarvest() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point3 = new Point(10, 6);
        Farm farm = map.placeBuilding(new Farm(player0), point3);

        /* Connect the farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), farm.getFlag());

        /* Wait for the farm to get constructed */
        Utils.waitForBuildingToBeConstructed(farm);

        /* Wait for the farm to get occupied */
        Farmer farmer = (Farmer) Utils.waitForNonMilitaryBuildingToGetPopulated(farm);

        assertTrue(farmer.isInsideBuilding());

        /* Let the farmer rest */
        Utils.fastForward(99, map);

        assertTrue(farmer.isInsideBuilding());

        /* Step once and make sure the farmer goes out of the farm */
        map.stepTime();

        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();

        assertTrue(farmer.isTraveling());

        /* Let the farmer reach the spot and start to plant */
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);

        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));
        assertTrue(farmer.isPlanting());

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that an event is sent when the crop has been planted */
        Utils.waitForFarmerToPlantCrop(map, farmer);

        /* Verify that the farmer stopped planting and there is a crop */
        assertFalse(farmer.isPlanting());
        assertTrue(map.isCropAtPoint(point));
        assertNull(farmer.getCargo());
        assertEquals(map.getCropAtPoint(point).getGrowthState(), JUST_PLANTED);

        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertEquals(gameChanges.getNewCrops().size(), 1);
        assertEquals(gameChanges.getNewCrops().get(0), map.getCropAtPoint(point));
        assertTrue(gameChanges.getWorkersWithNewTargets().size() >= 1);
        assertTrue(gameChanges.getWorkersWithNewTargets().contains(farmer));

        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedSigns().size(), 0);
        assertEquals(gameChanges.getRemovedTrees().size(), 0);
        assertEquals(gameChanges.getNewTrees().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
    }

    @Test
    public void testMonitoringEventFarmerPlantsWhenThereAreFreeSpotsAndNothingToHarvestIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point3 = new Point(10, 6);
        Farm farm = map.placeBuilding(new Farm(player0), point3);

        /* Connect the farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), farm.getFlag());

        /* Wait for the farm to get constructed */
        Utils.waitForBuildingToBeConstructed(farm);

        /* Wait for the farm to get occupied */
        Farmer farmer = (Farmer) Utils.waitForNonMilitaryBuildingToGetPopulated(farm);

        assertTrue(farmer.isInsideBuilding());

        /* Let the farmer rest */
        Utils.fastForward(99, map);

        assertTrue(farmer.isInsideBuilding());

        /* Step once and make sure the farmer goes out of the farm */
        map.stepTime();

        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();

        assertTrue(farmer.isTraveling());

        /* Let the farmer reach the spot and start to plant */
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);

        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));
        assertTrue(farmer.isPlanting());

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that an event is sent when the crop has been planted */
        Utils.waitForFarmerToPlantCrop(map, farmer);

        /* Verify that the farmer stopped planting and there is a crop */
        assertFalse(farmer.isPlanting());
        assertTrue(map.isCropAtPoint(point));
        assertNull(farmer.getCargo());
        assertEquals(map.getCropAtPoint(point).getGrowthState(), JUST_PLANTED);

        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertEquals(gameChanges.getNewCrops().size(), 1);
        assertEquals(gameChanges.getNewCrops().get(0), map.getCropAtPoint(point));
        assertTrue(gameChanges.getWorkersWithNewTargets().size() > 0);
        assertTrue(gameChanges.getWorkersWithNewTargets().contains(farmer));

        /* Verify that no more messages are sent before the farmer enters the farm */
        int amountEvents = monitor.getEvents().size();

        for (int i = 0; i < 10; i++) {
            map.stepTime();

            if (monitor.getEvents().size() > amountEvents) {
                for (GameChangesList changes : monitor.getEventsAfterEvent(gameChanges)) {
                    assertEquals(changes.getNewCrops().size(), 0);
                }
            }
        }
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenFarmerPlantsWhenThereAreFreeSpotsAndNothingToHarvest() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place farm */
        Point point3 = new Point(10, 6);
        Farm farm = map.placeBuilding(new Farm(player0), point3);

        /* Connect the farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), farm.getFlag());

        /* Wait for the farm to get constructed */
        Utils.waitForBuildingToBeConstructed(farm);

        /* Wait for the farm to get occupied */
        Farmer farmer = (Farmer) Utils.waitForNonMilitaryBuildingToGetPopulated(farm);

        assertTrue(farmer.isInsideBuilding());

        /* Let the farmer rest */
        Utils.fastForward(99, map);

        assertTrue(farmer.isInsideBuilding());

        /* Step once and make sure the farmer goes out of the farm */
        map.stepTime();

        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();

        assertTrue(farmer.isTraveling());

        /* Let the farmer reach the spot and start to plant */
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);

        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));
        assertTrue(farmer.isPlanting());

        map.stepTime();

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that an event is sent when the crop has been planted */
        Utils.waitForFarmerToPlantCrop(map, farmer);

        /* Verify that the farmer stopped planting and there is a crop */
        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getNewCrops().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventFarmerHarvestsWhenPossible() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point3 = new Point(10, 6);
        Farm farm = map.placeBuilding(new Farm(player0), point3);

        /* Connect the farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), farm.getFlag());

        /* Wait for the farm to get constructed */
        Utils.waitForBuildingToBeConstructed(farm);

        /* Wait for the farm to get occupied */
        Farmer farmer = (Farmer) Utils.waitForNonMilitaryBuildingToGetPopulated(farm);

        assertTrue(farmer.isInsideBuilding());

        /* Wait for the farmer to place a crop */
        Crop crop0 = Utils.waitForFarmerToPlantCrop(map, farmer);

        /* Wait for the crop to grow */
        Utils.waitForCropToGetReady(map, crop0);

        /* Wait for the worker to walk to the crop and start harvesting */
        Utils.waitForWorkerToSetTarget(map, farmer, crop0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, crop0.getPosition());

        map.stepTime();

        /* Wait for the farmer to harvest the crop */
        Utils.waitForFarmerToHarvestCrop(map, farmer, crop0);

        /* Stop production in the farm */
        farm.stopProduction();

        /* Wait for the farmer to go back to the farm */
        assertEquals(farmer.getTarget(), farm.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, farm.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that an event is sent when the crop disappears */
        Utils.waitForHarvestedCropToDisappear(map, crop0);

        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getEvents().get(monitor.getEvents().size() - 1);

        assertEquals(gameChanges.getRemovedCrops().size(), 1);
        assertEquals(gameChanges.getRemovedCrops().get(0), crop0);

        assertEquals(gameChanges.getNewCrops().size(), 0);
        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedSigns().size(), 0);
        assertEquals(gameChanges.getRemovedTrees().size(), 0);
        assertEquals(gameChanges.getNewTrees().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
    }

    @Test
    public void testMonitoringEventFarmerHarvestsWhenPossibleIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point3 = new Point(10, 4);
        Farm farm = map.placeBuilding(new Farm(player0), point3);

        /* Connect the farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), farm.getFlag());

        /* Wait for the farm to get constructed */
        Utils.waitForBuildingToBeConstructed(farm);

        /* Wait for the farm to get occupied */
        Farmer farmer = (Farmer) Utils.waitForNonMilitaryBuildingToGetPopulated(farm);

        assertTrue(farmer.isInsideBuilding());

        /* Wait for the farmer to place a crop */
        Crop crop0 = Utils.waitForFarmerToPlantCrop(map, farmer);

        /* Wait for the crop to grow */
        Utils.waitForCropToGetReady(map, crop0);

        /* Wait for the worker to walk to the crop and start harvesting */
        Utils.waitForWorkerToSetTarget(map, farmer, crop0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, crop0.getPosition());

        map.stepTime();

        /* Wait for the farmer to harvest the crop */
        Utils.waitForFarmerToHarvestCrop(map, farmer, crop0);

        /* Stop production in the farm */
        farm.stopProduction();

        /* Cut up the road between the farm and the headquarter */
        map.removeRoad(road0);

        /* Wait for the farmer to go back to the farm */
        assertEquals(farmer.getTarget(), farm.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, farm.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that an event is sent when the crop disappears */
        Utils.waitForHarvestedCropToDisappear(map, crop0);

        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getEvents().get(monitor.getEvents().size() - 1);

        assertEquals(gameChanges.getRemovedCrops().size(), 1);
        assertEquals(gameChanges.getRemovedCrops().get(0), crop0);

        /* Verify that no more messages are sent */
        int amountEvents = monitor.getEvents().size();

        Utils.fastForward(10, map);

        if (monitor.getEvents().size() > amountEvents) {
            for (GameChangesList changes : monitor.getEvents().subList(amountEvents, monitor.getEvents().size() - 1)) {
                assertTrue(gameChanges.getRemovedCrops().isEmpty());
            }
        }
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenFarmerHarvestsWhenPossible() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place farm */
        Point point3 = new Point(10, 6);
        Farm farm = map.placeBuilding(new Farm(player0), point3);

        /* Connect the farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), farm.getFlag());

        /* Wait for the farm to get constructed */
        Utils.waitForBuildingToBeConstructed(farm);

        /* Wait for the farm to get occupied */
        Farmer farmer = (Farmer) Utils.waitForNonMilitaryBuildingToGetPopulated(farm);

        assertTrue(farmer.isInsideBuilding());

        /* Wait for the farmer to place a crop */
        Crop crop0 = Utils.waitForFarmerToPlantCrop(map, farmer);

        /* Wait for the crop to grow */
        Utils.waitForCropToGetReady(map, crop0);

        /* Wait for the worker to walk to the crop and start harvesting */
        Utils.waitForWorkerToSetTarget(map, farmer, crop0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, crop0.getPosition());

        map.stepTime();

        /* Wait for the farmer to harvest the crop */
        Utils.waitForFarmerToHarvestCrop(map, farmer, crop0);

        /* Stop production in the farm */
        farm.stopProduction();

        /* Wait for the farmer to go back to the farm */
        assertEquals(farmer.getTarget(), farm.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, farmer, farm.getPosition());

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that no event is sent to player 1 when the crop disappears */
        Utils.waitForHarvestedCropToDisappear(map, crop0);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getRemovedCrops().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenFieldOfViewIsExtendedWhenBarracksIsOccupied() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that an event is sent when the barracks get populated and the player discovers new land */
        Set<Point> discoveredAtStart = new HashSet<>(player0.getDiscoveredLand());

        Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

        Set<Point> newlyDiscovered = new HashSet<>(player0.getDiscoveredLand());

        newlyDiscovered.removeAll(discoveredAtStart);

        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getEvents().get(monitor.getEvents().size() - 1);

        assertTrue(gameChanges.getNewDiscoveredLand().size() > 1);

        for (Point point : newlyDiscovered) {
            assertTrue(gameChanges.getNewDiscoveredLand().contains(point));
        }

        for (Point point : discoveredAtStart) {
            assertFalse(gameChanges.getNewDiscoveredLand().contains(point));
        }

        assertEquals(gameChanges.getRemovedCrops().size(), 0);

        assertEquals(gameChanges.getNewCrops().size(), 0);
        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedSigns().size(), 0);
        assertEquals(gameChanges.getRemovedTrees().size(), 0);
        assertEquals(gameChanges.getNewTrees().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenFieldOfViewIsExtendedWhenBarracksIsOccupied() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        map.stepTime();

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that no event is sent to player 1 when the barracks get populated and the player discovers new land */
        Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getNewDiscoveredLand().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenBorderIsExtendedWhenBarracksIsOccupied() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that an event is sent when the barracks get populated and the player discovers new land */
        Set<Point> borderAtStart = new HashSet<>(player0.getBorderPoints());

        Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

        Set<Point> newBorder = new HashSet<>(player0.getBorderPoints());
        Set<Point> fullNewBorder = new HashSet<>(newBorder);

        newBorder.removeAll(borderAtStart);

        Set<Point> removedBorder = new HashSet<>(borderAtStart);

        removedBorder.removeAll(fullNewBorder);

        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getEvents().get(monitor.getEvents().size() - 1);

        assertEquals(1, gameChanges.getChangedBorders().size());

        List<BorderChange> borderChanges = gameChanges.getChangedBorders();

        assertEquals(borderChanges.size(), 1);

        BorderChange borderChange = borderChanges.get(0);

        assertEquals(borderChange.getPlayer(), player0);
        assertEquals(borderChange.getNewBorder().size(), newBorder.size());
        assertEquals(borderChange.getRemovedBorder().size(), removedBorder.size());

        for (Point point : newBorder) {
            assertTrue(borderChange.getNewBorder().contains(point));
            assertFalse(borderChange.getRemovedBorder().contains(point));
        }

        for (Point point : removedBorder) {
            assertFalse(borderChange.getNewBorder().contains(point));
            assertTrue(borderChange.getRemovedBorder().contains(point));
        }

        /* Verify that no more events are sent for discovered land */
        int amountEvents = monitor.getEvents().size();

        for (int i = 0; i < 10; i++) {
            map.stepTime();

            if (monitor.getEvents().size() > amountEvents) {
                for (GameChangesList changes : monitor.getEvents().subList(amountEvents, monitor.getEvents().size() - 1)) {
                    assertEquals(changes.getChangedBorders().size(), 0);
                }
            }
        }
    }

    @Test
    public void testMonitoringEventWhenFieldOfViewIsExtendedWhenBarracksIsOccupiedIsOnlySentOnce() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that an event is sent when the barracks get populated and the player discovers new land */
        Set<Point> discoveredAtStart = new HashSet<>(player0.getDiscoveredLand());

        Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

        Set<Point> newlyDiscovered = new HashSet<>(player0.getDiscoveredLand());

        newlyDiscovered.removeAll(discoveredAtStart);

        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getEvents().get(monitor.getEvents().size() - 1);

        assertTrue(gameChanges.getNewDiscoveredLand().size() > 1);

        for (Point point : newlyDiscovered) {
            assertTrue(gameChanges.getNewDiscoveredLand().contains(point));
        }

        for (Point point : discoveredAtStart) {
            assertFalse(gameChanges.getNewDiscoveredLand().contains(point));
        }

        /* Verify that no more events are sent for discovered land */
        int amountEvents = monitor.getEvents().size();

        for (int i = 0; i < 10; i++) {
            map.stepTime();

            if (monitor.getEvents().size() > amountEvents) {
                for (GameChangesList changes : monitor.getEvents().subList(amountEvents, monitor.getEvents().size() - 1)) {
                    assertEquals(changes.getNewDiscoveredLand().size(), 0);
                }
            }
        }
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenBorderIsExtendedWhenBarracksIsOccupied() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(75, 75);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        map.stepTime();

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        /* Verify that no event is sent to player 1 when the barracks get populated and the player discovers new land */
        Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

        for (Point point : player0.getBorderPoints()) {
            assertFalse(player1.getDiscoveredLand().contains(point));
        }

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getChangedBorders().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenWorkerEntersBuilding() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place barracks */
        Point point26 = new Point(21, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Construct the house */
        Utils.constructHouse(woodcutter0);

        /* Wait for a worker to start walking to the house */
        WoodcutterWorker woodcutterWorker = Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0).get(0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that an event is sent when the worker enters the building */
        assertEquals(woodcutterWorker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, woodcutter0.getPosition());

        assertTrue(woodcutterWorker.isInsideBuilding());

        GameChangesList gameChanges = monitor.getEvents().get(monitor.getEvents().size() - 1);

        assertEquals(gameChanges.getRemovedWorkers().size(), 1);
        assertEquals(gameChanges.getRemovedWorkers().get(0), woodcutterWorker);

        assertEquals(gameChanges.getRemovedCrops().size(), 0);
        assertEquals(gameChanges.getNewCrops().size(), 0);
        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedSigns().size(), 0);
        assertEquals(gameChanges.getRemovedTrees().size(), 0);
        assertEquals(gameChanges.getNewTrees().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenWorkerEntersBuildingIsSentOnlyOnce() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place barracks */
        Point point26 = new Point(21, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Construct the house */
        Utils.constructHouse(woodcutter0);

        /* Wait for a worker to start walking to the house */
        WoodcutterWorker woodcutterWorker = Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0).get(0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that an event is sent when the worker enters the building */
        assertEquals(woodcutterWorker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, woodcutter0.getPosition());

        assertTrue(woodcutterWorker.isInsideBuilding());

        GameChangesList gameChanges = monitor.getEvents().get(monitor.getEvents().size() - 1);

        assertEquals(gameChanges.getRemovedWorkers().size(), 1);
        assertEquals(gameChanges.getRemovedWorkers().get(0), woodcutterWorker);

        /* Verify that the event is sent only once */
        int amountEvents = monitor.getEvents().size();

        for (int i = 0; i < 10; i++) {
            map.stepTime();

            if (monitor.getEvents().size() > amountEvents) {
                for (GameChangesList changes : monitor.getEventsAfterEvent(gameChanges)) {
                    assertEquals(changes.getRemovedWorkers().size(), 0);
                }
            }
        }
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenWorkerEntersBuilding() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks */
        Point point26 = new Point(21, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Construct the house */
        Utils.constructHouse(woodcutter0);

        /* Wait for a worker to start walking to the house */
        WoodcutterWorker woodcutterWorker = Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0).get(0);

        map.stepTime();

        /* Set up monitoring subscription for player 1 */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that no event is sent to player 1 when the worker enters the building */
        assertEquals(woodcutterWorker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, woodcutter0.getPosition());

        assertTrue(woodcutterWorker.isInsideBuilding());

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getRemovedWorkers().isEmpty());
        }
    }

    @Test
    public void testNoMonitoringEventWhenFieldOfViewIsExtendedWhenBarracksIsOccupiedBeforeMonitoringStarts() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        map.stepTime();

        /* Wait for the barracks to get populated so the discovered area expands */
        Set<Point> discoveredAtStart = new HashSet<>(player0.getDiscoveredLand());

        Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

        Set<Point> newlyDiscovered = new HashSet<>(player0.getDiscoveredLand());

        newlyDiscovered.removeAll(discoveredAtStart);

        assertTrue(newlyDiscovered.size() > 0);

        /* Verify that no message is sent for the new discovered area when the monitoring starts after */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        map.stepTime();

        if (!monitor.getEvents().isEmpty()) {
            assertEquals(monitor.getLastEvent().getNewDiscoveredLand().size(), 0);
            assertEquals(monitor.getEvents().size(), 1);
        }
    }

    @Test
    public void testMonitoringEventWhenScoutDiscoversNewGround() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(19, 5);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                scout = (Scout) worker;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that an event is sent when the scout discovers new ground */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        boolean foundNewGround = false;
        boolean wentToNewGround = false;

        Point discoveredPoint = null;

        Set<Point> discoveredLandBefore = new HashSet<>(player0.getDiscoveredLand());

        for (int i = 0; i < 30; i++) {
            Point target = scout.getTarget();

            if (!discoveredLandBefore.contains(target)) {
                foundNewGround = true;
            }

            assertNotEquals(target, scout.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, scout, target);

            assertTrue(player0.getDiscoveredLand().contains(scout.getPosition()));
            assertEquals(scout.getPosition(), target);

            if (foundNewGround) {
                wentToNewGround = true;
                discoveredPoint = target;

                break;
            }
        }

        assertTrue(foundNewGround);
        assertTrue(wentToNewGround);

        assertTrue(monitor.getEvents().size() >= 1);

        boolean discoveryReported = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getNewDiscoveredLand().contains(discoveredPoint)) {
                discoveryReported = true;

                break;
            }
        }

        assertTrue(discoveryReported);
    }

    @Test
    public void testMonitoringEventWhenScoutDiscoversNewGroundIsSentOnlyOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(19, 5);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                scout = (Scout) worker;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that an event is sent when the scout discovers new ground */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        boolean foundNewGround = false;
        boolean wentToNewGround = false;

        Point discoveredPoint = null;


        Set<Point> discoveredLandBefore = new HashSet<>(player0.getDiscoveredLand());

        for (int i = 0; i < 30; i++) {
            Point target = scout.getTarget();

            if (!discoveredLandBefore.contains(target)) {
                foundNewGround = true;
            }

            assertNotEquals(target, scout.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, scout, target);

            assertTrue(player0.getDiscoveredLand().contains(scout.getPosition()));
            assertEquals(scout.getPosition(), target);

            if (foundNewGround) {
                wentToNewGround = true;
                discoveredPoint = target;

                break;
            }
        }

        assertTrue(foundNewGround);
        assertTrue(wentToNewGround);

        assertTrue(monitor.getEvents().size() >= 1);

        boolean discoveryReported = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getNewDiscoveredLand().contains(discoveredPoint)) {
                discoveryReported = true;

                break;
            }
        }

        assertTrue(discoveryReported);

        GameChangesList gameChanges = monitor.getLastEvent();

        /* Verify that the event is sent only once */
        int amountEvents = monitor.getEvents().size();

        for (int i = 0; i < 5; i++) {
            map.stepTime();

            if (monitor.getEvents().size() > amountEvents) {
                for (GameChangesList changes : monitor.getEventsAfterEvent(gameChanges)) {
                    assertEquals(changes.getNewDiscoveredLand().size(), 0);
                }
            }
        }
    }

    @Test
    public void testNoMonitoringEventWhenScoutDiscoversNewGroundBeforeMonitoringStarts() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(21, 5);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                scout = (Scout) worker;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        boolean foundNewGround = false;
        boolean wentToNewGround = false;

        Set<Point> discoveredLandBefore = new HashSet<>(player0.getDiscoveredLand());

        for (int i = 0; i < 30; i++) {
            Point target = scout.getTarget();

            if (!discoveredLandBefore.contains(target)) {
                foundNewGround = true;
            }

            assertNotEquals(target, scout.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, scout, target);

            assertTrue(player0.getDiscoveredLand().contains(scout.getPosition()));
            assertEquals(scout.getPosition(), target);

            if (foundNewGround) {
                wentToNewGround = true;

                break;
            }
        }

        assertTrue(foundNewGround);
        assertTrue(wentToNewGround);

        /* Wait for the scout to go back to the flag */
        Utils.waitForWorkerToSetTarget(map, scout, flag.getPosition());

        /* Verify that there is no message about discovered land when starting monitoring */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        map.stepTime();

        assertFalse(monitor.getEvents().size() > 1);

        if (monitor.getEvents().size() == 1) {
            assertTrue(monitor.getLastEvent().getNewDiscoveredLand().isEmpty());
        }
    }

    @Test
    public void testNoMonitoringEventForOtherPlayerWhenScoutDiscoversNewGround() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter for player 0 */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(65, 65);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place flag */
        Point point2 = new Point(19, 5);
        Flag flag = map.placeFlag(player0, point2);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                scout = (Scout) worker;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that no event is sent to player 1 when the scout discovers new ground */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        boolean foundNewGround = false;
        boolean wentToNewGround = false;

        Set<Point> discoveredLandBefore = new HashSet<>(player0.getDiscoveredLand());

        for (int i = 0; i < 30; i++) {
            Point target = scout.getTarget();

            if (!discoveredLandBefore.contains(target)) {
                foundNewGround = true;
            }

            assertNotEquals(target, scout.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, scout, target);

            assertTrue(player0.getDiscoveredLand().contains(scout.getPosition()));
            assertEquals(scout.getPosition(), target);

            if (foundNewGround) {
                wentToNewGround = true;

                break;
            }
        }

        assertTrue(foundNewGround);
        assertTrue(wentToNewGround);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getNewDiscoveredLand().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenMilitaryBuildingIsUpgraded() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place barracks */
        Point point1 = new Point(10, 10);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Connect the barracks to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to get constructed */
        Utils.waitForBuildingToBeConstructed(barracks0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that three events are sent when a house is placed - for the house, the road, and the flag */
        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that an event is sent when the barracks is upgraded */
        barracks0.upgrade();

        Utils.waitForUpgradeToFinish(barracks0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 1);
        assertEquals(gameChanges.getChangedBuildings().get(0), map.getBuildingAtPoint(point1));

        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getNewRoads().size(), 0);
        assertEquals(gameChanges.getNewBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
        assertEquals(gameChanges.getRemovedBuildings().size(), 0);
        assertEquals(gameChanges.getRemovedRoads().size(), 0);
    }

    @Test
    public void testMonitoringEventWhenMilitaryBuildingIsUpgradedIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place barracks */
        Point point1 = new Point(10, 10);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Connect the barracks to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to get constructed */
        Utils.waitForBuildingToBeConstructed(barracks0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that three events are sent when a house is placed - for the house, the road, and the flag */
        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that an event is sent when the barracks is upgraded */
        barracks0.upgrade();

        Utils.waitForUpgradeToFinish(barracks0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getChangedBuildings().size(), 1);
        assertEquals(gameChanges.getChangedBuildings().get(0), map.getBuildingAtPoint(point1));

        /* Verify that the message is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedBuildings().size(), 0);
        }
    }

    @Test
    public void testMonitorOnlyGetsOneMessage() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Set up monitoring subscription for the player, start subscription twice */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);
        player0.monitorGameView(monitor);

        /* Verify that only one event is sent when a flag is placed, even if the monitor called subscribe twice */
        assertEquals(monitor.getEvents().size(), 0);

        Point point1 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point1);

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 1);

        GameChangesList gameChanges = monitor.getEvents().get(0);

        assertTrue(gameChanges.getTime() > 0);
        assertEquals(gameChanges.getNewFlags().size(), 1);
        assertEquals(gameChanges.getNewFlags().get(0), flag0);
    }

    @Test
    public void testMonitoringWhenRoadBecomesPromotedToMainRoad() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place flag */
        Point point2 = new Point(5, 9);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place road between the headquarter and the flag */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Place a worker on the road */
        Courier courier = Utils.occupyRoad(road0, map);

        /* Deliver 99 cargo - the road does not become a main road */
        for (int i = 0; i < 99; i++) {
            Cargo cargo = Utils.placeCargo(map, COIN, flag0, headquarter0);

            /* Wait for the courier to pick up the cargo */
            assertNull(courier.getCargo());

            Utils.fastForwardUntilWorkerCarriesCargo(map, courier, cargo);

            /* Wait for the courier to deliver the cargo */
            assertEquals(courier.getTarget(), headquarter0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

            assertNull(courier.getCargo());

            assertFalse(road0.isMainRoad());
        }

        /* Deliver one more cargo and make it a main road */
        Cargo cargo = Utils.placeCargo(map, COIN, flag0, headquarter0);

        /* Wait for the courier to pick up the cargo */
        assertNull(courier.getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, cargo);

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the courier to deliver the cargo */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getFlag().getPosition());

        assertTrue(road0.isMainRoad());

        /* Verify that an event is sent when the road becomes a main road */
        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.getPromotedRoads().size(), 1);
        assertEquals(gameChangesList.getPromotedRoads().get(0), road0);
    }

    @Test
    public void testMonitoringWhenRoadBecomesPromotedToMainRoadIsOnlySentOnce() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place flag */
        Point point2 = new Point(5, 9);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place road between the headquarter and the flag */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Place a worker on the road */
        Courier courier = Utils.occupyRoad(road0, map);

        /* Deliver 99 cargo - the road does not become a main road */
        for (int i = 0; i < 99; i++) {
            Cargo cargo = Utils.placeCargo(map, COIN, flag0, headquarter0);

            /* Wait for the courier to pick up the cargo */
            assertNull(courier.getCargo());

            Utils.fastForwardUntilWorkerCarriesCargo(map, courier, cargo);

            /* Wait for the courier to deliver the cargo */
            assertEquals(courier.getTarget(), headquarter0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

            assertNull(courier.getCargo());

            assertFalse(road0.isMainRoad());
        }

        /* Deliver one more cargo and make it a main road */
        Cargo cargo = Utils.placeCargo(map, COIN, flag0, headquarter0);

        /* Wait for the courier to pick up the cargo */
        assertNull(courier.getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, cargo);

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the courier to deliver the cargo */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getFlag().getPosition());

        assertTrue(road0.isMainRoad());

        /* Verify that an event is sent when the road becomes a main road */
        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.getPromotedRoads().size(), 1);
        assertEquals(gameChangesList.getPromotedRoads().get(0), road0);

        /* Verify that the road promotion is only reported once */
        Utils.fastForward(20, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertFalse(newChanges.getPromotedRoads().contains(road0));
        }
    }

    @Test
    public void testMonitoringWhenFlagGetsCargo() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(13, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place flag between the buildings */
        Point point2 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point2);

        map.stepTime();

        /* Connect the headquarter with the flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the woodcutter with the flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter0.getFlag());

        /* Wait for the first road to get populated */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Wait for the courier of the first road to carry planks for the woodcutter */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, PLANK);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that an event is sent when the courier puts the cargo down by the flag */
        assertEquals(courier.getTarget(), flag0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition());

        assertTrue(monitor.getEvents().size() > 0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertTrue(gameChanges.getChangedFlags().size() > 0);
        assertTrue(gameChanges.getChangedFlags().contains(flag0));
        assertFalse(gameChanges.getChangedFlags().contains(woodcutter0.getFlag()));
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
    }

    @Test
    public void testMonitoringWhenFlagGetsCargoIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(13, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place flag between the buildings */
        Point point2 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point2);

        map.stepTime();

        /* Connect the headquarter with the flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the woodcutter with the flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter0.getFlag());

        /* Wait for the first road to get populated */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Wait for the courier of the first road to carry planks for the woodcutter */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, PLANK);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that an event is sent when the courier puts the cargo down by the flag */
        assertEquals(courier.getTarget(), flag0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition());

        assertTrue(monitor.getEvents().size() > 0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertTrue(gameChanges.getChangedFlags().size() > 0);
        assertTrue(gameChanges.getChangedFlags().contains(flag0));
        assertFalse(gameChanges.getChangedFlags().contains(woodcutter0.getFlag()));
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);

        /* Verify that the event is only sent once */
        Utils.fastForward(5, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedFlags().size(), 0);
        }
    }

    @Test
    public void testMonitoringWhenCargoGetsPickedUpFromFlag() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(13, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place flag between the buildings */
        Point point2 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point2);

        map.stepTime();

        /* Change so there is only one plank in the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 1);

        /* Connect the headquarter with the flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the woodcutter with the flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter0.getFlag());

        /* Wait for the first road to get populated */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Wait for the courier of the first road to go to the headquarter's flag to pick up a plank */
        Utils.waitForWorkerToSetTarget(map, courier, headquarter0.getFlag().getPosition());

        assertEquals(headquarter0.getFlag().getStackedCargo().size(), 1);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the courier picks up the plank from the flag */
        assertEquals(headquarter0.getFlag().getStackedCargo().size(), 1);
        assertNull(monitor.getLastEvent());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getFlag().getPosition());

        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo().getMaterial(), PLANK);
        assertTrue(monitor.getEvents().size() > 0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertTrue(gameChanges.getChangedFlags().size() > 0);
        assertTrue(gameChanges.getChangedFlags().contains(headquarter0.getFlag()));
        assertFalse(gameChanges.getChangedFlags().contains(flag0));
        assertFalse(gameChanges.getChangedFlags().contains(woodcutter0.getFlag()));
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);
    }

    @Test
    public void testMonitoringWhenCargoGetsPickedUpFromFlagIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(13, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place flag between the buildings */
        Point point2 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point2);

        map.stepTime();

        /* Change so there is only one plank in the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 1);

        /* Connect the headquarter with the flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the woodcutter with the flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter0.getFlag());

        /* Wait for the first road to get populated */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Wait for the courier of the first road to go to the headquarter's flag to pick up a plank */
        Utils.waitForWorkerToSetTarget(map, courier, headquarter0.getFlag().getPosition());

        assertEquals(headquarter0.getFlag().getStackedCargo().size(), 1);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the courier picks up the plank from the flag */
        assertEquals(headquarter0.getFlag().getStackedCargo().size(), 1);
        assertNull(monitor.getLastEvent());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getFlag().getPosition());

        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo().getMaterial(), PLANK);
        assertTrue(monitor.getEvents().size() > 0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertTrue(gameChanges.getChangedFlags().size() > 0);
        assertTrue(gameChanges.getChangedFlags().contains(headquarter0.getFlag()));
        assertFalse(gameChanges.getChangedFlags().contains(flag0));
        assertFalse(gameChanges.getChangedFlags().contains(woodcutter0.getFlag()));
        assertEquals(gameChanges.getNewFlags().size(), 0);
        assertEquals(gameChanges.getRemovedFlags().size(), 0);

        /* Verify that the message is only sent once */
        Utils.fastForward(5, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedFlags().size(), 0);
        }
    }

    @Test
    public void testMonitoringWhenCourierWaitsToDeliverWhenFlagIsFull() throws Exception {

        /* Start new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag far away from the headquarter */
        Point point1 = new Point(21, 5);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place flag closer to the headquarter */
        Point point2 = new Point(17, 5);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place a short road to make a courier wait on */
        Road road0 = map.placeRoad(player0, flag0.getPosition(), flag0.getPosition().left(), flag1.getPosition());

        /* Place a long road to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, flag1, headquarter0.getFlag());

        /* Wait for both roads to get their couriers assigned and standing idle */
        Collection<Courier> couriers = Utils.waitForRoadsToGetAssignedCouriers(map, road0, road1);

        Utils.waitForCouriersToBeIdle(map, couriers);

        /* Place eight cargos on the flag between the roads targeting the headquarter */
        Utils.placeCargos(map, WATER, 8, flag1, headquarter0);

        /* Place two cargos on the other flag */
        Utils.placeCargo(map, IRON, flag0, headquarter0);

        map.stepTime();

        /* Wait for the courier of the short road to pick up a cargo */
        assertEquals(road1.getCourier().getTarget(), flag1.getPosition());
        assertNull(road1.getCourier().getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Wait for the courier for the short road to be one step away from the middle flag */
        assertEquals(road1.getCourier().getTarget(), flag1.getPosition());
        assertNull(road1.getCourier().getCargo());

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), flag1.getPosition().right());

        /* Verify that an event is sent when the courier waits because there is no space on the flag for more cargo */
        assertEquals(flag1.getStackedCargo().size(), 8);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertTrue(gameChanges.getWorkersWithNewTargets().size() > 0);
        assertTrue(gameChanges.getWorkersWithNewTargets().contains(road0.getCourier()));
    }

    @Test
    public void testMonitoringEnemyBorderChanges() throws InvalidUserActionException {

        /* Start new game */
        Player player0 = new Player("Player 0", Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter for the first player */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for the second player */
        Point point1 = new Point(25, 25);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place fortress for the second player */
        Point point2 = new Point(18, 18);
        Fortress fortress0 = map.placeBuilding(new Fortress(player1), point2);

        /* Connect the fortress to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player1, headquarter1.getFlag(), fortress0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the second player's border changes */
        List<Point> visibleBorderBeforeChange = new ArrayList<>();

        for (Point point : player1.getBorderPoints()) {
            if (!player0.getDiscoveredLand().contains(point)) {
                continue;
            }

            visibleBorderBeforeChange.add(point);
        }

        Utils.waitForBuildingToBeConstructed(fortress0);

        Utils.waitForMilitaryBuildingToGetPopulated(fortress0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertTrue(gameChanges.getChangedBorders().size() > 0);

        List<Point> visibleBorderAfterChange = new ArrayList<>();

        for (Point point : player1.getBorderPoints()) {
            if (!player0.getDiscoveredLand().contains(point)) {
                continue;
            }

            visibleBorderAfterChange.add(point);
        }

        List<BorderChange> borderChanges = gameChanges.getChangedBorders();

        BorderChange borderChangePlayer1 = null;

        for (BorderChange borderChange : borderChanges) {
            if (borderChange.getPlayer().equals(player1)) {
                borderChangePlayer1 = borderChange;

                break;
            }
        }

        assertNotNull(borderChangePlayer1);
        assertEquals(borderChangePlayer1.getPlayer(), player1);
        assertTrue(borderChangePlayer1.getNewBorder().size() > 0);
        assertTrue(borderChangePlayer1.getRemovedBorder().size() > 0);

        for (Point point : visibleBorderBeforeChange) {

            if (player1.getBorderPoints().contains(point)) {
                assertFalse(borderChangePlayer1.getNewBorder().contains(point));
                assertFalse(borderChangePlayer1.getRemovedBorder().contains(point));
            } else {
                assertTrue(borderChangePlayer1.getRemovedBorder().contains(point));
            }
        }

        for (Point point : visibleBorderAfterChange) {
            if (visibleBorderBeforeChange.contains(point)) {
                assertFalse(borderChangePlayer1.getNewBorder().contains(point));
                assertFalse(borderChangePlayer1.getRemovedBorder().contains(point));
            } else {
                assertTrue(borderChangePlayer1.getNewBorder().contains(point));
            }
        }
    }

    @Test
    public void testMonitoringWhenDeadTreeIsRemoved() throws Exception {

        /* Start new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place dead tree */
        Point point1 = new Point(21, 5);
        map.placeDeadTree(point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that an event is sent when the dead tree is removed */
        Flag flag0 = map.placeFlag(player0, point1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertTrue(gameChanges.getRemovedDeadTrees().size() > 0);
        assertTrue(gameChanges.getRemovedDeadTrees().contains(point1));
    }

    @Test
    public void testMonitoringWhenDeadTreeIsRemovedIsOnlySentOnce() throws Exception {

        /* Start new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place dead tree */
        Point point1 = new Point(21, 5);
        map.placeDeadTree(point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that an event is sent when the dead tree is removed */
        Flag flag0 = map.placeFlag(player0, point1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getTime() > 0);
        assertTrue(gameChanges.getRemovedDeadTrees().size() > 0);
        assertTrue(gameChanges.getRemovedDeadTrees().contains(point1));

        /* Verify that the message is only sent once */
        Utils.fastForward(20, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getRemovedDeadTrees().size(), 0);
        }
    }

    @Test
    public void testMonitoringWhenWildAnimalMoves() throws Exception {

        /* Start new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place wild animal */
        Point point1 = new Point(21, 5);
        WildAnimal wildAnimal0 = map.placeWildAnimal(point1);

        /* Verify that an event is sent when the wild animal moves */
        Utils.waitForWorkerToHaveTargetSet(wildAnimal0, map);

        map.stepTime();

        assertNotNull(wildAnimal0.getTarget());
        assertFalse(wildAnimal0.isExactlyAtPoint());

        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithNewTargets().contains(wildAnimal0)) {
                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);
    }

    @Test
    public void testMonitoringEventWhenWildAnimalMovesIsOnlySentOnce() throws Exception {

        /* Start new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place wild animal */
        Point point1 = new Point(21, 5);
        WildAnimal wildAnimal0 = map.placeWildAnimal(point1);

        /* Verify that an event is sent when the wild animal moves */
        Utils.waitForWorkerToHaveTargetSet(wildAnimal0, map);

        map.stepTime();

        assertNotNull(wildAnimal0.getTarget());
        assertFalse(wildAnimal0.isExactlyAtPoint());

        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithNewTargets().contains(wildAnimal0)) {
                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);

        /* Verify that the event is only sent once */
        GameChangesList lastEvent = monitor.getEvents().get(0);

        Utils.fastForward(5, map);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastEvent)) {
            assertEquals(gameChangesList.getWorkersWithNewTargets().size(), 0);
        }
    }
}
