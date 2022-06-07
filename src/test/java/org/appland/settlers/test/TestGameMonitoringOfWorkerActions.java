package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Forester;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.WorkerAction;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.BLUE;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
}
