package org.appland.settlers.test;

import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Headquarter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestGameMonitoringOfDecorations {

    /**
     * TODO:
     *   - Test monitoring when decorations disappear
     *   - Test monitoring when discovering new area with decorations
     */

    @Test
    public void testEventMessageWhenPureDecorationsAreRemoved() throws InvalidUserActionException {

        for (DecorationType decoration : TestDecorations.PURE_DECORATIONS) {

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

            /* Set up monitoring subscription for the player */
            Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
            player0.monitorGameView(monitor);

            /* Verify that a flag can be placed on the decoration */
            Flag flag = map.placeFlag(player0, point1);

            map.stepTime();

            assertTrue(map.isFlagAtPoint(point1));
            assertFalse(map.isDecoratedAtPoint(point1));
            assertFalse(map.getDecorations().containsKey(point1));

            /* Verify that an event was sent when the decoration was removed */
            int decorationEventCount = Utils.countMonitoredEventsForDecoration(point1, monitor);

            assertEquals(decorationEventCount, 1);
        }
    }

    @Test
    public void testEventMessageWhenPureDecorationsAreRemovedIsOnlySentOnce() throws InvalidUserActionException {

        for (DecorationType decoration : TestDecorations.PURE_DECORATIONS) {

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

            /* Set up monitoring subscription for the player */
            Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
            player0.monitorGameView(monitor);

            /* Verify that a flag can be placed on the decoration */
            Flag flag = map.placeFlag(player0, point1);

            map.stepTime();

            assertTrue(map.isFlagAtPoint(point1));
            assertFalse(map.isDecoratedAtPoint(point1));
            assertFalse(map.getDecorations().containsKey(point1));

            /* Verify that an event was sent when the decoration was removed */
            int decorationEventCount = Utils.countMonitoredEventsForDecoration(point1, monitor);

            assertEquals(decorationEventCount, 1);

            /* Fast forward a bit */
            Utils.fastForward(10, map);

            /* Verify that no more event is sent for the removed decoration */
            decorationEventCount = Utils.countMonitoredEventsForDecoration(point1, monitor);

            assertEquals(decorationEventCount, 1);
        }
    }
}
