package org.appland.settlers.test;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.TransportCategory;
import org.appland.settlers.model.buildings.Headquarter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestGameMonitoringOfPlayerSettings {

    @Test
    public void testEventWhenTransportPriorityIsChanged() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Start monitoring */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place flag */
        var point1 = new Point(9, 5);
        var flag = map.placeFlag(player0, point1);

        map.stepTime();

        /* Verify that the event does not include changes to the transport priority */
        assertNotEquals(monitor.getEvents().size(), 0);
        assertFalse(monitor.getLastEvent().isTransportPriorityChanged());

        /* Verify that an event is sent when the transport priority is */
        monitor.clearEvents();

        player0.setTransportPriority(3, TransportCategory.WOOD);

        map.stepTime();

        assertTrue(monitor.getLastEvent().isTransportPriorityChanged());

        /* Verify that the event is only sent once */
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor.getEvents().isEmpty() || !monitor.getLastEvent().isTransportPriorityChanged());
    }
}
