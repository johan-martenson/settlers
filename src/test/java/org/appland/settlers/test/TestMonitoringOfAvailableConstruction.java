package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.Farm;
import org.appland.settlers.model.Farmer;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Stonemason;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.PRIVATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestMonitoringOfAvailableConstruction {

    /*
     TODO:
      - test removing things - anything missing?
      - test not sent to wrong player
       - remove stone one by one gives a field of flags which is wrong
     */

    @Test
    public void testMonitoringAvailableConstructionWhenPlacingFlag() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a flag is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlacingFlagIsOnlySentOnce() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a flag is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingFlag() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a flag is removed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        map.removeFlag(flag0);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingFlagIsOnlySentOnce() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a flag is removed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        map.removeFlag(flag0);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlacingTree() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a flag is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(10, 10);
        Tree tree0 = map.placeTree(point1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlacingTreeIsOnlySentOnce() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a flag is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(10, 10);
        Tree tree0 = map.placeTree(point1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingTree() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place tree */
        Point point1 = new Point(10, 10);
        Tree tree0 = map.placeTree(point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Place a woodcutter close to the tree */
        Point point2 = new Point(13, 13);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point2);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        /* Wait for the woodcutter to get constructed */
        Utils.waitForBuildingToBeConstructed(woodcutter);

        /* Verify that the event for the changed available construction when a tree is removed is correct */
        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Utils.waitForTreeToGetCutDown(tree0, map);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingTreeIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place tree */
        Point point1 = new Point(10, 10);
        Tree tree0 = map.placeTree(point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Place a woodcutter close to the tree */
        Point point2 = new Point(13, 13);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point2);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        /* Wait for the woodcutter to get constructed */
        Utils.waitForBuildingToBeConstructed(woodcutter);

        /* Verify that the event for the changed available construction when a tree is removed is correct */
        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Utils.waitForTreeToGetCutDown(tree0, map);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlacingSmallBuilding() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a flag is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlacingSmallBuildingIsOnlySentOnce() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a flag is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenTearingDownSmallBuilding() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a small building is removed is correct */
        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        woodcutter0.tearDown();

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertNotEquals(gameChanges.getChangedBuildings().size(), 0);
        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenTearingDownSmallBuildingIsOnlySentOnce() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a small building is removed is correct */
        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        woodcutter0.tearDown();

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertNotEquals(gameChanges.getChangedBuildings().size(), 0);
        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenSmallBuildingIsDestroyed() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a small building is removed is correct */
        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        woodcutter0.tearDown();

        Utils.waitForBuildingToBurnDown(woodcutter0);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertNotEquals(gameChanges.getChangedBuildings().size(), 0);
        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenSmallBuildingIsDestroyedIsOnlySentOnce() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a small building is removed is correct */
        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        woodcutter0.tearDown();

        Utils.waitForBuildingToBurnDown(woodcutter0);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertNotEquals(gameChanges.getChangedBuildings().size(), 0);
        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenSmallBuildingDisappears() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a small building is removed is correct */
        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        woodcutter0.tearDown();

        Utils.waitForBuildingToDisappear(woodcutter0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertNotEquals(gameChanges.getRemovedBuildings().size(), 0);
        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenSmallBuildingDisappearsIsOnlySentOnce() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a small building is removed is correct */
        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        woodcutter0.tearDown();

        Utils.waitForBuildingToDisappear(woodcutter0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertNotEquals(gameChanges.getRemovedBuildings().size(), 0);
        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlacingMediumBuilding() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a flag is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(10, 10);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlacingMediumBuildingIsOnlySentOnce() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a flag is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(10, 10);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenTearingDownMediumBuilding() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(10, 10);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a medium building is torn down is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        sawmill0.tearDown();

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenTearingDownMediumBuildingIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(10, 10);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a medium building is torn down is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        sawmill0.tearDown();

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenMediumBuildingIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(10, 10);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a medium building has burnt down is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        sawmill0.tearDown();

        Utils.waitForBuildingToBurnDown(sawmill0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenMediumBuildingIsDestroyedIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(10, 10);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a medium building has burnt down is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        sawmill0.tearDown();

        Utils.waitForBuildingToBurnDown(sawmill0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenMediumBuildingDisappears() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(10, 10);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a medium building has burnt down is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        sawmill0.tearDown();

        Utils.waitForBuildingToDisappear(sawmill0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenMediumBuildingDisappearsIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(10, 10);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a medium building has burnt down is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        sawmill0.tearDown();

        Utils.waitForBuildingToDisappear(sawmill0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlacingLargeBuilding() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a flag is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(10, 10);
        Farm farm0 = map.placeBuilding(new Farm(player0), point1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlacingLargeBuildingIsOnlySentOnce() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a flag is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(10, 10);
        Farm farm0 = map.placeBuilding(new Farm(player0), point1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenLargeBuildingIsTornDown() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(10, 10);
        Farm farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Connect the farm with the headquarter */
        Road road = map.placeAutoSelectedRoad(player0, farm0.getFlag(), headquarter0.getFlag());

        /* Wait for the farm to get constructed */
        Utils.waitForBuildingToBeConstructed(farm0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a flag is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        farm0.tearDown();

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenLargeBuildingIsTornDownIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(10, 10);
        Farm farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Connect the farm with the headquarter */
        Road road = map.placeAutoSelectedRoad(player0, farm0.getFlag(), headquarter0.getFlag());

        /* Wait for the farm to get constructed */
        Utils.waitForBuildingToBeConstructed(farm0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a flag is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        farm0.tearDown();

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenLargeBuildingIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(10, 10);
        Farm farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Connect the farm with the headquarter */
        Road road = map.placeAutoSelectedRoad(player0, farm0.getFlag(), headquarter0.getFlag());

        /* Wait for the farm to get constructed */
        Utils.waitForBuildingToBeConstructed(farm0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a flag is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        farm0.tearDown();

        Utils.waitForBuildingToDisappear(farm0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenLargeBuildingIsDestroyedIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(10, 10);
        Farm farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Connect the farm with the headquarter */
        Road road = map.placeAutoSelectedRoad(player0, farm0.getFlag(), headquarter0.getFlag());

        /* Wait for the farm to get constructed */
        Utils.waitForBuildingToBeConstructed(farm0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a flag is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        farm0.tearDown();

        Utils.waitForBuildingToDisappear(farm0);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlacingHorizontalRoad() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(22, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a road is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(10, 10);
        Point point2 = new Point(18, 10);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlacingHorizontalRoadIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(22, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a road is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(10, 10);
        Point point2 = new Point(18, 10);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingHorizontalRoad() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(22, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place road */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(18, 10);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a road is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        map.removeRoad(road0);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingHorizontalRoadIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(22, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place road */
        Point point1 = new Point(10, 10);
        Point point2 = new Point(18, 10);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a road is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        map.removeRoad(road0);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlacingLeftToRightDiagonalRoad() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(20, 12);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a road is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(10, 10);
        Point point2 = new Point(14, 14);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlacingLeftToRightDiagonalRoadIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(20, 12);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a road is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(10, 10);
        Point point2 = new Point(14, 14);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingLeftToRightDiagonalRoad() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(20, 12);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a road is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(10, 10);
        Point point2 = new Point(14, 14);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingLeftToRightDiagonalRoadIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(20, 12);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a road is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(10, 10);
        Point point2 = new Point(14, 14);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingRightToLeftDiagonalRoad() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(16, 12);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place road */
        Point point1 = new Point(14, 14);
        Point point2 = new Point(10, 10);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a road is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        map.removeRoad(road0);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingRightToLeftDiagonalRoadIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(22, 12);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place road */
        Point point1 = new Point(14, 14);
        Point point2 = new Point(10, 10);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);

        Road road0 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a road is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        map.removeRoad(road0);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlacingCrop() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a road is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(14, 10);

        Crop crop = map.placeCrop(point1);

        assertTrue(player0.getDiscoveredLand().contains(point1));

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();
        System.out.println(gameChanges);
        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlacingCropIsOnlySentOnce() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when a road is placed is correct */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Point point1 = new Point(14, 10);

        Crop crop = map.placeCrop(point1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenHarvestedCrop() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(10, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm near the crop */
        Point point2 = new Point(16, 16);
        Farm farm0 = map.placeBuilding(new Farm(player0), point2);

        /* Connect the farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, farm0.getFlag(), headquarter0.getFlag());

        /* Wait for the farm to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(farm0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(farm0);

        /* Wait for the farmer to plant a crop */
        Crop crop = Utils.waitForFarmerToPlantCrop(map, (Farmer)farm0.getWorker());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when the farmer harvests the crop */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Utils.waitForCropToGetHarvested(map, crop);

        GameChangesList gameChanges = monitor.getLastEvent();

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenHarvestedCropIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(10, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm near the crop */
        Point point2 = new Point(16, 16);
        Farm farm0 = map.placeBuilding(new Farm(player0), point2);

        /* Connect the farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, farm0.getFlag(), headquarter0.getFlag());

        /* Wait for the farm to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(farm0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(farm0);

        /* Wait for the farmer to plant a crop */
        Crop crop = Utils.waitForFarmerToPlantCrop(map, (Farmer)farm0.getWorker());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when the farmer harvests the crop */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Utils.waitForCropToGetHarvested(map, crop);

        GameChangesList gameChanges = monitor.getLastEvent();

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenHarvestedCropDisappears() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(10, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm near the crop */
        Point point2 = new Point(16, 16);
        Farm farm0 = map.placeBuilding(new Farm(player0), point2);

        /* Connect the farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, farm0.getFlag(), headquarter0.getFlag());

        /* Wait for the farm to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(farm0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(farm0);

        /* Wait for the farmer to plant a crop */
        Crop crop = Utils.waitForFarmerToPlantCrop(map, (Farmer)farm0.getWorker());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when the farmer harvests the crop */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Utils.waitForCropToGetHarvested(map, crop);

        Utils.waitForHarvestedCropToDisappear(map, crop);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenHarvestedCropDisappearsIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(10, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm near the crop */
        Point point2 = new Point(16, 16);
        Farm farm0 = map.placeBuilding(new Farm(player0), point2);

        /* Connect the farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, farm0.getFlag(), headquarter0.getFlag());

        /* Wait for the farm to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(farm0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(farm0);

        /* Wait for the farmer to plant a crop */
        Crop crop = Utils.waitForFarmerToPlantCrop(map, (Farmer)farm0.getWorker());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event for the changed available construction when the farmer harvests the crop */
        assertEquals(monitor.getEvents().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Utils.waitForCropToGetHarvested(map, crop);

        Utils.waitForHarvestedCropToDisappear(map, crop);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.getChangedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenBorderIsExtended() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Wait for a military sent from the headquarter */
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);

        map.stepTime();

        Worker military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertNotNull(military);

        /* Verify that an event is sent when the military reaches the barracks and the border is extended */
        Point point3 = new Point(6, 24);
        Point point4 = new Point(6, 32);
        assertEquals(military.getTarget(), barracks0.getPosition());
        assertTrue(player0.getBorderPoints().contains(point3));
        assertFalse(player0.getBorderPoints().contains(point4));

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        Utils.fastForwardUntilWorkerReachesPoint(map, military, barracks0.getPosition());

        assertFalse(player0.getBorderPoints().contains(point3));
        assertTrue(player0.getBorderPoints().contains(point4));

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertNotEquals(gameChangesList.getChangedAvailableConstruction().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenBorderIsExtendedIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Wait for a military sent from the headquarter */
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);

        map.stepTime();

        Worker military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertNotNull(military);

        /* Verify that an event is sent when the military reaches the barracks and the border is extended */
        Point point3 = new Point(6, 24);
        Point point4 = new Point(6, 32);

        assertEquals(military.getTarget(), barracks0.getPosition());
        assertTrue(player0.getBorderPoints().contains(point3));
        assertFalse(player0.getBorderPoints().contains(point4));

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        Utils.fastForwardUntilWorkerReachesPoint(map, military, barracks0.getPosition());

        assertFalse(player0.getBorderPoints().contains(point3));
        assertTrue(player0.getBorderPoints().contains(point4));

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertNotEquals(gameChangesList.getChangedAvailableConstruction().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenBorderIsLost() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Wait for a military sent from the headquarter */
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);

        map.stepTime();

        Worker military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertNotNull(military);

        /* Wait for the barracks to get occupied so the border extends */
        Point point3 = new Point(6, 24);
        Point point4 = new Point(6, 32);

        assertEquals(military.getTarget(), barracks0.getPosition());
        assertTrue(player0.getBorderPoints().contains(point3));
        assertFalse(player0.getBorderPoints().contains(point4));

        Utils.fastForwardUntilWorkerReachesPoint(map, military, barracks0.getPosition());

        assertFalse(player0.getBorderPoints().contains(point3));
        assertTrue(player0.getBorderPoints().contains(point4));

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        map.stepTime();

        /* Verify that an event is sent and that the available construction is monitored correctly when border is lost */
        barracks0.tearDown();

        map.stepTime();

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertNotEquals(gameChangesList.getChangedBorders().size(), 0);
        assertNotEquals(gameChangesList.getChangedAvailableConstruction().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenBorderIsLostIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Wait for a military sent from the headquarter */
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);

        map.stepTime();

        Worker military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertNotNull(military);

        /* Wait for the barracks to get occupied so the border extends */
        Point point3 = new Point(6, 24);
        Point point4 = new Point(6, 32);

        assertEquals(military.getTarget(), barracks0.getPosition());
        assertTrue(player0.getBorderPoints().contains(point3));
        assertFalse(player0.getBorderPoints().contains(point4));

        Utils.fastForwardUntilWorkerReachesPoint(map, military, barracks0.getPosition());

        assertFalse(player0.getBorderPoints().contains(point3));
        assertTrue(player0.getBorderPoints().contains(point4));

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        map.stepTime();

        /* Verify that an event is sent and that the available construction is monitored correctly when border is lost */
        barracks0.tearDown();

        map.stepTime();

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertNotEquals(gameChangesList.getChangedBorders().size(), 0);
        assertNotEquals(gameChangesList.getChangedAvailableConstruction().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenStoneDisappearsAfterAllHasBeenRetrieved() throws Exception {

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

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        assertEquals(monitor.getEvents().size(), 0);

        Utils.waitForStonemasonToFinishGettingStone(map, stonemason);

        assertFalse(map.isStoneAtPoint(point1));

        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertEquals(gameChanges.getRemovedStones().size(), 1);
        assertEquals(gameChanges.getRemovedStones().get(0), stone0);

        assertNotEquals(gameChanges.getChangedAvailableConstruction().size() ,0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenStoneDisappearsAfterAllHasBeenRetrievedIsOnlySentOnce() throws Exception {

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

        assertNotEquals(gameChanges.getChangedAvailableConstruction().size() ,0);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.getChangedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenMultipleStoneDisappear() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point2 = new Point(14, 4);
        Building quarry = map.placeBuilding(new Quarry(player0), point2);

        /* Construct the quarry */
        Utils.constructHouse(quarry);

        /* Connect the quarry to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), quarry.getFlag());

        /* Place stone */
        Point point1 = new Point(12, 4);
        Point point3 = new Point(13, 3);
        Point point4 = new Point(11, 3);
        Stone stone0 = map.placeStone(point1);
        Stone stone1 = map.placeStone(point3);
        Stone stone2 = map.placeStone(point4);

        /* Let the stonemason remove the final part of the stone and verify that an event is sent */
        Stonemason stonemason = Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0).get(0);

        Utils.waitForStonemasonToStartGettingStone(map, stonemason);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that there are three correct events for available construction when the stones are gone */
        Utils.waitForStonesToDisappear(map, stone0, stone1, stone2);

        int availableConstructionChanges = 0;

        for (GameChangesList gameChanges : monitor.getEvents()) {
            if (gameChanges.getChangedAvailableConstruction().isEmpty()) {
                continue;
            }

            availableConstructionChanges = availableConstructionChanges + 1;
        }

        int removedStones = 0;

        for (GameChangesList gameChanges : monitor.getEvents()) {
            if (gameChanges.getRemovedStones().isEmpty()) {
                continue;
            }

            removedStones = removedStones + 1;
        }

        assertEquals(removedStones, 3);
        assertEquals(availableConstructionChanges, 3);
        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }
}
