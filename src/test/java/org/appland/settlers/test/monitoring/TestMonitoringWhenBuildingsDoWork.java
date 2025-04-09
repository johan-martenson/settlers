package org.appland.settlers.test.monitoring;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Miller;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.WHEAT;
import static org.junit.Assert.*;

public class TestMonitoringWhenBuildingsDoWork {

    @Test
    public void testMonitoringEventWhenMillWorks() throws InvalidUserActionException {

        /* Create single player game */
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        /* Place headquarter */
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        var point1 = new Point(12, 8);
        var mill = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill with the headquarter */
        var road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        // Wait for the mill to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(mill);

        Miller miller = (Miller) Utils.waitForNonMilitaryBuildingToGetPopulated(mill);

        assertTrue(miller.isInsideBuilding());

        /* Deliver wheat to the mill */
        Utils.deliverCargos(mill, WHEAT, 6);

        assertFalse(mill.needsMaterial(WHEAT));

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor);

        // Wait for the miller to start producing flour
        for (int i = 0; i < 100; i++) {
            if (miller.isWorking()) {
                break;
            }

            assertFalse(mill.isWorking());
            assertTrue(miller.isInsideBuilding());
            assertTrue(
                    monitor.getEvents().isEmpty() ||
                            monitor.getEvents().stream().noneMatch(ev -> ev.changedBuildings().contains(mill))
            );

            map.stepTime();
        }

        // Verify that a monitoring event is sent when the miller is working to produce flour
        assertNull(miller.getCargo());
        assertTrue(mill.isWorking());
        assertTrue(monitor.getEvents().size() > 0);

        System.out.println(monitor.getEvents());

        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(mill));

        // Verify that a monitoring event is sent when the miller stops producing flour
        monitor.clearEvents();

        Utils.waitForMillerToStopProducingFlour(miller, map);

        assertNotNull(miller.getCargo());
        assertEquals(miller.getCargo().getMaterial(), FLOUR);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(mill));

        // Verify that the message is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertFalse(mill.isWorking());
        assertTrue(
                monitor.getEvents().isEmpty() ||
                        monitor.getEvents().stream().noneMatch(ev -> ev.changedBuildings().contains(mill))
        );
    }
}
