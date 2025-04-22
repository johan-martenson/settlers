package org.appland.settlers.test.monitoring;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Material.SAW;
import static org.junit.Assert.*;

public class TestGameMonitoringOfToolPriorities {

    @Test
    public void testMonitoringWhenSawIsChanged() throws InvalidUserActionException {

        // Create game map
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        GameMap map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor);

        // Verify that an event is sent
        assertEquals(monitor.getEvents().size(), 0);
        assertNotEquals(player0.getProductionQuotaForTool(SAW), 9);

        player0.setProductionQuotaForTool(SAW, 9);

        map.stepTime();

        assertTrue(monitor.getEvents().size() > 0);
        assertEquals(monitor.getEvents().getLast().toolQuotaChanged().size(), 1);
        assertTrue(monitor.getEvents().getLast().toolQuotaChanged().contains(SAW));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertEquals(monitor.getEvents().size(), 0);
    }
}
