package org.appland.settlers.test.monitoring;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Carpenter;
import org.appland.settlers.model.actors.Miller;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.junit.Assert.*;

public class TestMonitoringWhenBuildingsDoWork {

    @Test
    public void testMonitoringEventWhenMillWorks() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place mill
        var point1 = new Point(12, 8);
        var mill = map.placeBuilding(new Mill(player0), point1);

        // Connect the mill with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        // Wait for the mill to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(mill);

        var miller = (Miller) Utils.waitForNonMilitaryBuildingToGetPopulated(mill);

        assertTrue(miller.isInsideBuilding());

        // Deliver wheat to the mill
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


    @Test
    public void testMonitoringEventWhenSawmillWorks() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point3 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point3);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        // Occupy the sawmill
        var sawmillWorker0 = Utils.occupyBuilding(new Carpenter(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        // Deliver wood to the sawmill
        sawmill.putCargo(new Cargo(WOOD, map));
        sawmill.putCargo(new Cargo(WOOD, map));

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor);

        // Verify that a monitoring event is sent when the sawmill starts producing planks
        monitor.clearEvents();

        assertFalse(sawmill.isWorking());

        for (int i = 0; i < 2_000; i++) {
            if (sawmill.isWorking()) {
                break;
            }

            assertFalse(sawmill.isWorking());
            assertFalse(sawmillWorker0.isWorking());

            monitor.clearEvents();

            map.stepTime();
        }

        assertTrue(sawmill.isWorking());
        assertTrue(sawmillWorker0.isWorking());
        assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
        assertNull(sawmillWorker0.getCargo());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(sawmill));

        // Verify that an event is sent when the sawmill stops cutting the tree
        monitor.clearEvents();

        Utils.waitForCarpenterToStopSawing(sawmillWorker0, map);

        assertNotNull(sawmillWorker0.getCargo());
        assertEquals(sawmillWorker0.getCargo().getMaterial(), PLANK);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(sawmill));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertFalse(sawmill.isWorking());
        assertFalse(sawmillWorker0.isWorking());
        assertEquals(monitor.getEvents().size(), 0);
    }


    @Test
    public void testOccupiedArmoryWithCoalAndIronProducesWeapon() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Place road to connect the armory with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Wait for the armory to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(armory0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(armory0);

        Worker armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory0);
        assertEquals(armory0.getWorker(), armorer0);

        // Deliver material to the armory
        Utils.deliverCargo(armory0, IRON_BAR);
        Utils.deliverCargo(armory0, COAL);

        assertFalse(armory0.isWorking());

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor);

        // Verify that an event is sent when the armory is working
        for (int i = 0; i < 99; i++) {
            map.stepTime();

            monitor.clearEvents();

            assertFalse(armory0.isWorking());
            assertTrue(armory0.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
        }

        map.stepTime();

        assertTrue(armory0.isWorking());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(armory0));

        // Verify that an event is sent when the armory stops working
        monitor.clearEvents();

        for (int i = 0; i < 49; i++) {
            map.stepTime();

            monitor.clearEvents();

            assertTrue(armory0.isWorking());
            assertTrue(armory0.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
        }

        assertTrue(armory0.isWorking());
        assertTrue(monitor.getEvents().size() == 0 || !monitor.getEvents().getLast().changedBuildings().contains(armory0));

        map.stepTime();

        assertFalse(armory0.isWorking());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(armory0));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertFalse(armory0.isWorking());
        assertNotNull(armorer0.getCargo());
        assertEquals(armorer0.getCargo().getMaterial(), SWORD);
        assertTrue(armory0.getFlag().getStackedCargo().isEmpty());
        assertTrue(monitor.getEvents().size() == 0 || !monitor.getEvents().getLast().changedBuildings().contains(armory0));
    }

}
