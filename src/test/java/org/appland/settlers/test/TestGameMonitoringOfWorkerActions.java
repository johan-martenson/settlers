package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Forester;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Stonemason;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.TreeSize;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.WoodcutterWorker;
import org.appland.settlers.model.WorkerAction;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.BLUE;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestGameMonitoringOfWorkerActions {

    @Test
    public void testMonitoringEventWhenForesterPlantsTree() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        /* Let the forester rest */
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        Point point = forester.getTarget();

        assertTrue(forester.isTraveling());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the forester to get to the point where he will plant a tree */
        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());

        /* Wait for the forester to finish planting the tree */
        Utils.waitForForesterToStopPlantingTree(forester, map);

        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));
        assertNull(forester.getCargo());

        /* Verify that an event was sent when the forester started planting the tree */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(forester)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(forester);

                assertEquals(workerAction, WorkerAction.PLANTING);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);
    }

    @Test
    public void testMonitoringEventWhenForesterPlantsTreeIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        /* Let the forester rest */
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        Point point = forester.getTarget();

        assertTrue(forester.isTraveling());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the forester to get to the point where he will plant a tree */
        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());

        /* Wait for the forester to finish planting the tree */
        Utils.waitForForesterToStopPlantingTree(forester, map);

        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));
        assertNull(forester.getCargo());

        /* Verify that an event was sent when the forester started planting the tree */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(forester)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(forester);

                assertEquals(workerAction, WorkerAction.PLANTING);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);

        /* Verify that the event is only sent once */
        GameChangesList lastEvent = monitor.getEvents().get(monitor.getEvents().size() - 1);

        Utils.fastForward(5, map);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastEvent)) {
            assertEquals(gameChangesList.getWorkersWithStartedActions().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenWoodcutterCutsDownTree() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2, Tree.TreeType.PINE, TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the forester hut */
        constructHouse(woodcutter);

        /* Manually place forester */
        WoodcutterWorker woodcutterWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(woodcutterWorker, woodcutter);

        /* Wait for the woodcutter to rest */
        Utils.fastForward(99, map);

        assertTrue(woodcutterWorker.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(woodcutterWorker.isInsideBuilding());

        Point point = woodcutterWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(woodcutterWorker.isTraveling());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Let the woodcutter reach the tree and start cutting */
        Utils.fastForwardUntilWorkersReachTarget(map, woodcutterWorker);

        assertTrue(woodcutterWorker.isArrived());
        assertTrue(woodcutterWorker.isAt(point));

        map.stepTime();

        assertTrue(woodcutterWorker.isCuttingTree());
        assertNull(woodcutterWorker.getCargo());

        /* Wait for the woodcutter to finish cutting the tree */
        for (int i = 0; i < 49; i++) {
            assertTrue(woodcutterWorker.isCuttingTree());
            assertTrue(map.isTreeAtPoint(point));
            map.stepTime();
        }

        /* Verify that the woodcutter stopped cutting */
        assertFalse(woodcutterWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));
        assertNotNull(woodcutterWorker.getCargo());
        assertEquals(woodcutterWorker.getCargo().getMaterial(), WOOD);

        /* Verify that an event was sent when the forester started planting the tree */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(woodcutterWorker)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(woodcutterWorker);

                assertEquals(workerAction, WorkerAction.CUTTING);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);
    }

    @Test
    public void testMonitoringEventWhenWoodcutterCutsDownTreeIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2, Tree.TreeType.PINE, TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the forester hut */
        constructHouse(woodcutter);

        /* Manually place forester */
        WoodcutterWorker woodcutterWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(woodcutterWorker, woodcutter);

        /* Wait for the woodcutter to rest */
        Utils.fastForward(99, map);

        assertTrue(woodcutterWorker.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(woodcutterWorker.isInsideBuilding());

        Point point = woodcutterWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(woodcutterWorker.isTraveling());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Let the woodcutter reach the tree and start cutting */
        Utils.fastForwardUntilWorkersReachTarget(map, woodcutterWorker);

        assertTrue(woodcutterWorker.isArrived());
        assertTrue(woodcutterWorker.isAt(point));

        map.stepTime();

        assertTrue(woodcutterWorker.isCuttingTree());
        assertNull(woodcutterWorker.getCargo());

        /* Wait for the woodcutter to finish cutting the tree */
        for (int i = 0; i < 49; i++) {
            assertTrue(woodcutterWorker.isCuttingTree());
            assertTrue(map.isTreeAtPoint(point));
            map.stepTime();
        }

        /* Verify that the woodcutter stopped cutting */
        assertFalse(woodcutterWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));
        assertNotNull(woodcutterWorker.getCargo());
        assertEquals(woodcutterWorker.getCargo().getMaterial(), WOOD);

        /* Verify that an event was sent when the forester started planting the tree */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(woodcutterWorker)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(woodcutterWorker);

                assertEquals(workerAction, WorkerAction.CUTTING);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);

        /* Verify that the event is only sent once */
        GameChangesList lastEvent = monitor.getEvents().get(monitor.getEvents().size() - 1);

        Utils.fastForward(5, map);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastEvent)) {
            assertEquals(gameChangesList.getWorkersWithStartedActions().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenStonemasonGetsStone() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Place stone */
        Point point2 = new Point(11, 5);
        Stone stone = map.placeStone(point2);

        /* Construct the quarry */
        constructHouse(quarry);

        /* Assign a stonemason to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry);

        /* Wait for the stonemason to rest */
        Utils.fastForward(99, map);

        assertTrue(stonemason.isInsideBuilding());
        assertTrue(map.isStoneAtPoint(point2));

        /* Step once to let the stonemason go out to get stone */
        map.stepTime();

        assertFalse(stonemason.isInsideBuilding());

        Point point = stonemason.getTarget();

        assertEquals(stonemason.getTarget(), stone.getPosition());
        assertTrue(stonemason.isTraveling());

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Let the stonemason reach the chosen spot if it isn't already there */
        if (!stonemason.isArrived()) {
            Utils.fastForwardUntilWorkersReachTarget(map, stonemason);
        }

        assertTrue(stonemason.isArrived());
        assertEquals(stonemason.getPosition(), stone.getPosition());
        assertTrue(stonemason.isGettingStone());

        /* Verify that the stonemason gets stone */
        for (int i = 0; i < 49; i++) {
            assertTrue(stonemason.isGettingStone());
            map.stepTime();
        }

        assertTrue(stonemason.isGettingStone());

        /* Verify that the stonemason is done getting stone at the correct time */
        map.stepTime();

        assertFalse(stonemason.isGettingStone());
        assertNotNull(stonemason.getCargo());
        assertEquals(stonemason.getCargo().getMaterial(), STONE);

        /* Verify that an event was sent when the stonemason started picking the stone */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(stonemason)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(stonemason);

                assertEquals(workerAction, WorkerAction.HACKING_STONE);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);
    }

    @Test
    public void testMonitoringEventWhenStonemasonGetsStoneIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Place stone */
        Point point2 = new Point(11, 5);
        Stone stone = map.placeStone(point2);

        /* Construct the quarry */
        constructHouse(quarry);

        /* Assign a stonemason to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry);

        /* Wait for the stonemason to rest */
        Utils.fastForward(99, map);

        assertTrue(stonemason.isInsideBuilding());
        assertTrue(map.isStoneAtPoint(point2));

        /* Step once to let the stonemason go out to get stone */
        map.stepTime();

        assertFalse(stonemason.isInsideBuilding());

        Point point = stonemason.getTarget();

        assertEquals(stonemason.getTarget(), stone.getPosition());
        assertTrue(stonemason.isTraveling());

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Let the stonemason reach the chosen spot if it isn't already there */
        if (!stonemason.isArrived()) {
            Utils.fastForwardUntilWorkersReachTarget(map, stonemason);
        }

        assertTrue(stonemason.isArrived());
        assertEquals(stonemason.getPosition(), stone.getPosition());
        assertTrue(stonemason.isGettingStone());

        /* Verify that the stonemason gets stone */
        for (int i = 0; i < 49; i++) {
            assertTrue(stonemason.isGettingStone());
            map.stepTime();
        }

        assertTrue(stonemason.isGettingStone());

        /* Verify that the stonemason is done getting stone at the correct time */
        map.stepTime();

        assertFalse(stonemason.isGettingStone());
        assertNotNull(stonemason.getCargo());
        assertEquals(stonemason.getCargo().getMaterial(), STONE);

        /* Verify that an event was sent when the stonemason started picking the stone */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(stonemason)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(stonemason);

                assertEquals(workerAction, WorkerAction.HACKING_STONE);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);

        /* Verify that the event is only sent once */
        GameChangesList lastEvent = monitor.getEvents().get(monitor.getEvents().size() - 1);

        Utils.fastForward(5, map);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastEvent)) {
            assertEquals(gameChangesList.getWorkersWithStartedActions().size(), 0);
        }
    }
}
