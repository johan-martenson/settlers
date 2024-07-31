package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.actors.Farmer;
import org.appland.settlers.model.actors.Stonemason;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Farm;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.PRIVATE;
import static org.junit.Assert.*;

public class TestMonitoringOfAvailableConstruction {

    /*
     TODO:
      - houses that are planned instead of under construction/constructed -- ~10 new tests
      - test removing things - anything missing?
      - test not sent to wrong player
      - remove stone one by one gives a field of flags which is wrong
      - test upgrade of barracks to guardhouse
      - test monitoring of available construction around ship starting and finishing construction
     */

    @Test
    public void testMonitoringAvailableConstructionWhenPlaceFlag() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlaceFlagIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingFlag() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingFlagIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlaceTree() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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
        Tree tree0 = map.placeTree(point1, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlaceTreeIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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
        Tree tree0 = map.placeTree(point1, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingTree() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place tree */
        Point point1 = new Point(10, 10);
        Tree tree0 = map.placeTree(point1, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

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

        /* Connect the woodcutter with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        /* Wait for the woodcutter to get constructed */
        Utils.waitForBuildingToBeConstructed(woodcutter);

        /* Verify that the event for the changed available construction when a tree is removed is correct */
        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Utils.waitForTreeToDisappearFromMap(tree0, map);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingTreeIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place tree */
        Point point1 = new Point(10, 10);
        Tree tree0 = map.placeTree(point1, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

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

        /* Connect the woodcutter with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        /* Wait for the woodcutter to get constructed */
        Utils.waitForBuildingToBeConstructed(woodcutter);

        /* Verify that the event for the changed available construction when a tree is removed is correct */
        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        Utils.waitForTreeToDisappearFromMap(tree0, map);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlaceSmallBuilding() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlaceSmallBuildingIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenTearingDownSmallBuilding() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the woodcutter */
        Utils.constructHouse(woodcutter0);

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

        assertNotEquals(gameChanges.changedBuildings().size(), 0);
        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenTearingDownSmallBuildingIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the woodcutter */
        Utils.constructHouse(woodcutter0);

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

        assertNotEquals(gameChanges.changedBuildings().size(), 0);
        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenSmallBuildingIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the woodcutter */
        Utils.constructHouse(woodcutter0);

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

        assertNotEquals(gameChanges.changedBuildings().size(), 0);
        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenSmallBuildingIsDestroyedIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the woodcutter */
        Utils.constructHouse(woodcutter0);

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

        assertNotEquals(gameChanges.changedBuildings().size(), 0);
        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenSmallBuildingDisappears() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the woodcutter */
        Utils.constructHouse(woodcutter0);

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

        assertNotEquals(gameChanges.removedBuildings().size(), 0);
        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenSmallBuildingDisappearsIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the woodcutter */
        Utils.constructHouse(woodcutter0);

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

        assertNotEquals(gameChanges.removedBuildings().size(), 0);
        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlaceMediumBuilding() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlaceMediumBuildingIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenTearingDownMediumBuilding() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenTearingDownMediumBuildingIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenMediumBuildingIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(10, 10);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        /* Construct the sawmill */
        Utils.constructHouse(sawmill0);

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

        assertTrue(monitor.getLastEvent().changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenMediumBuildingIsDestroyedIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(10, 10);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        /* Construct the sawmill */
        Utils.constructHouse(sawmill0);

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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenMediumBuildingDisappears() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(10, 10);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        /* Construct the sawmill */
        Utils.constructHouse(sawmill0);

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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenMediumBuildingDisappearsIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(10, 10);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        /* Construct the sawmill */
        Utils.constructHouse(sawmill0);

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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlaceLargeBuilding() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlaceLargeBuildingIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenLargeBuildingIsTornDown() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(10, 10);
        Farm farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Connect the farm with the headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenLargeBuildingIsTornDownIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(10, 10);
        Farm farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Connect the farm with the headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenLargeBuildingIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(10, 10);
        Farm farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Connect the farm with the headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenLargeBuildingIsDestroyedIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(10, 10);
        Farm farm0 = map.placeBuilding(new Farm(player0), point1);

        /* Connect the farm with the headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlaceHorizontalRoad() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlaceHorizontalRoadIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingHorizontalRoad() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingHorizontalRoadIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlaceLeftToRightDiagonalRoad() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlaceLeftToRightDiagonalRoadIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingLeftToRightDiagonalRoad() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingLeftToRightDiagonalRoadIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingRightToLeftDiagonalRoad() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenRemovingRightToLeftDiagonalRoadIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlaceCrop() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        Crop crop = map.placeCrop(point1, Crop.CropType.TYPE_1);

        assertTrue(player0.getDiscoveredLand().contains(point1));

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenPlaceCropIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
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

        Crop crop = map.placeCrop(point1, Crop.CropType.TYPE_1);

        map.stepTime();

        GameChangesList gameChanges = monitor.getLastEvent();

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenHarvestedCrop() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(10, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm near the crop */
        Point point2 = new Point(16, 16);
        Farm farm0 = map.placeBuilding(new Farm(player0), point2);

        /* Connect the farm with the headquarters */
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(10, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm near the crop */
        Point point2 = new Point(16, 16);
        Farm farm0 = map.placeBuilding(new Farm(player0), point2);

        /* Connect the farm with the headquarters */
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
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenHarvestedCropDisappears() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(10, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm near the crop */
        Point point2 = new Point(16, 16);
        Farm farm0 = map.placeBuilding(new Farm(player0), point2);

        /* Connect the farm with the headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenHarvestedCropDisappearsIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(10, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm near the crop */
        Point point2 = new Point(16, 16);
        Farm farm0 = map.placeBuilding(new Farm(player0), point2);

        /* Connect the farm with the headquarters */
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

        assertTrue(gameChanges.changedAvailableConstruction().size() > 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenBorderIsExtended() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place barracks */
        Point point22 = new Point(15, 11);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Wait for a military sent from the headquarters */
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);

        map.stepTime();

        Worker military = Utils.waitForSoldierOutsideBuilding(player0);

        assertNotNull(military);

        /* Verify that an event is sent when the military reaches the barracks and the border is extended */
        Point point3 = new Point(21, 13);
        Point point4 = new Point(29, 13);

        assertEquals(military.getTarget(), barracks0.getPosition());
        assertTrue(player0.getBorderPoints().contains(point3));
        assertFalse(player0.getBorderPoints().contains(point4));

        /* Set up monitoring subscription for the player and store the currently available construction */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that all changed available construction is reported when the border is extended */
        assertNull(map.isAvailableHousePoint(player0, barracks0.getFlag().getPosition().downLeft().downLeft()));
        assertTrue(map.isAvailableFlagPoint(player0, barracks0.getFlag().getPosition().downLeft().downLeft()));

        Utils.fastForwardUntilWorkerReachesPoint(map, military, barracks0.getPosition());

        assertFalse(player0.getBorderPoints().contains(point3));
        assertTrue(player0.getBorderPoints().contains(point4));

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertNotEquals(gameChangesList.changedAvailableConstruction().size(), 0);
        assertNotEquals(map.isAvailableHousePoint(player0, barracks0.getFlag().getPosition().downLeft().downLeft()), null);
        assertTrue(map.isAvailableFlagPoint(player0, barracks0.getFlag().getPosition().downLeft().downLeft()));
        assertFalse(gameChangesList.changedBorders().isEmpty());

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenBorderIsExtendedIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Wait for a military sent from the headquarters */
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);

        map.stepTime();

        Worker military = Utils.waitForSoldierOutsideBuilding(player0);

        assertNotNull(military);

        /* Verify that an event is sent when the military reaches the barracks, and the border is extended */
        Point point3 = new Point(6, 24);
        Point point4 = new Point(5, 31);

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

        System.out.println("SOLDIER REACHED BARRACKS");

        assertFalse(player0.getBorderPoints().contains(point3));
        assertTrue(player0.getBorderPoints().contains(point4));

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertNotEquals(gameChangesList.changedAvailableConstruction().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        monitor.clearEvents();

        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEvents()) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenBorderIsLost() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Wait for a military sent from the headquarters */
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);

        map.stepTime();

        Worker military = Utils.waitForSoldierOutsideBuilding(player0);

        assertNotNull(military);

        /* Wait for the barracks to get occupied so the border extends */
        Point point3 = new Point(6, 24);
        Point point4 = new Point(5, 31);

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

        assertNotEquals(gameChangesList.changedBorders().size(), 0);
        assertNotEquals(gameChangesList.changedAvailableConstruction().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenBorderIsLostIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Wait for a military sent from the headquarters */
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);

        map.stepTime();

        Worker military = Utils.waitForSoldierOutsideBuilding(player0);

        assertNotNull(military);

        /* Wait for the barracks to get occupied so the border extends */
        Point point3 = new Point(6, 24);
        Point point4 = new Point(5, 31);

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

        assertNotEquals(gameChangesList.changedBorders().size(), 0);
        assertNotEquals(gameChangesList.changedAvailableConstruction().size(), 0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenStoneDisappearsAfterAllHasBeenRetrieved() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(10, 10);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point2 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(player0), point2);

        /* Construct the quarry */
        Utils.constructHouse(quarry);

        /* Connect the quarry to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), quarry.getFlag());

        /* Place stone */
        Point point1 = new Point(12, 4);
        Stone stone0 = map.placeStone(point1, Stone.StoneType.STONE_1, 7);

        /* Remove all except the last part of the stone */
        for (int i = 0; i < 6; i++) {
            stone0.removeOnePart();

            assertTrue(map.isStoneAtPoint(point1));
        }

        assertEquals(stone0.getAmount(), 1);

        /* Let the stonemason remove the final part of the stone and verify that an event is sent */
        Stonemason stonemason = Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0).getFirst();

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

        assertEquals(gameChanges.removedStones().size(), 1);
        assertEquals(gameChanges.removedStones().getFirst(), stone0);

        assertNotEquals(gameChanges.changedAvailableConstruction().size() ,0);

        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }

    @Test
    public void testMonitoringAvailableConstructionWhenStoneDisappearsAfterAllHasBeenRetrievedIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(10, 10);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point2 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(player0), point2);

        /* Construct the quarry */
        Utils.constructHouse(quarry);

        /* Connect the quarry to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), quarry.getFlag());

        /* Place stone */
        Point point1 = new Point(12, 4);
        Stone stone0 = map.placeStone(point1, Stone.StoneType.STONE_1, 7);

        /* Remove all except the last part of the stone */
        for (int i = 0; i < 6; i++) {
            stone0.removeOnePart();

            assertTrue(map.isStoneAtPoint(point1));
        }

        assertEquals(stone0.getAmount(), 1);

        /* Let the stonemason remove the final part of the stone and verify that an event is sent */
        Stonemason stonemason = Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0).getFirst();

        Utils.waitForStonemasonToStartGettingStone(map, stonemason);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        Utils.waitForStonemasonToFinishGettingStone(map, stonemason);

        assertFalse(map.isStoneAtPoint(point1));

        assertTrue(monitor.getEvents().size() >= 1);

        GameChangesList gameChanges = monitor.getLastEvent();

        assertEquals(gameChanges.removedStones().size(), 1);
        assertEquals(gameChanges.removedStones().getFirst(), stone0);

        assertNotEquals(gameChanges.changedAvailableConstruction().size() ,0);

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChanges)) {
            assertEquals(newChanges.changedAvailableConstruction().size(), 0);
        }
    }

    @Test
    public void testMonitoringAvailableConstructionWhenMultipleStonesDisappear() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(10, 10);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point2 = new Point(14, 4);
        Building quarry = map.placeBuilding(new Quarry(player0), point2);

        /* Construct the quarry */
        Utils.constructHouse(quarry);

        /* Connect the quarry to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), quarry.getFlag());

        /* Place stones */
        Point point1 = new Point(12, 4);
        Point point3 = new Point(13, 3);
        Point point4 = new Point(11, 3);
        Stone stone0 = map.placeStone(point1, Stone.StoneType.STONE_1, 7);
        Stone stone1 = map.placeStone(point3, Stone.StoneType.STONE_1, 7);
        Stone stone2 = map.placeStone(point4, Stone.StoneType.STONE_1, 7);

        /* Let the stonemason remove the final part of the stone and verify that an event is sent */
        Stonemason stonemason = Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0).getFirst();

        Utils.waitForStonemasonToStartGettingStone(map, stonemason);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        map.stepTime();

        monitor.setAvailableConstruction(
                map.getAvailableHousePoints(player0),
                map.getAvailableFlagPoints(player0),
                map.getAvailableMinePoints(player0)
        );

        /* Verify that there are three correct events for available construction when the stones are gone */
        Utils.waitForStonesToDisappear(map, stone0, stone1, stone2);

        assertFalse(map.isStoneAtPoint(point1));
        assertFalse(map.isStoneAtPoint(point2));
        assertFalse(map.isStoneAtPoint(point3));
        assertEquals(monitor.getEvents().stream().filter(gcl -> !gcl.removedStones().isEmpty()).count(), 3);
        assertEquals(monitor.getEvents().stream().filter(gcl -> !gcl.changedAvailableConstruction().isEmpty()).count(), 3);
        monitor.assertMonitoredAvailableConstructionMatchesWithMap(map, player0);
    }
}
