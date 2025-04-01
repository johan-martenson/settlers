package org.appland.settlers.test.statistics;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Builder;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.rest.resource.ResourceLevel;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Material.HAMMER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestWorkerStatistics {

    @Test
    public void testInitialWorkerStatisticsForMediumResources() throws InvalidUserActionException {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarter
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Set medium resources
        headquarter0.setInitialResources(ResourceLevel.MEDIUM);

        // Verify that the initial resource statistics are correct
        var statisticsManager = map.getStatisticsManager();
        var resourceMeasurement = statisticsManager.getGeneralStatistics(player0).workers();

        assertEquals(resourceMeasurement.getMeasurements().size(), 1);
        assertEquals(resourceMeasurement.getMeasurements().getFirst().time(), 1);
        assertEquals(resourceMeasurement.getMeasurements().getFirst().value(), 70);
    }

    @Test
    public void testCreatedBuilderCausesChangedWorkerStatistics() throws InvalidUserActionException {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarter
        var point0 = new Point(50, 50);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Set the resource level to low
        headquarter0.setInitialResources(ResourceLevel.LOW);

        assertEquals(headquarter0.getAmount(Material.BUILDER), 5);

        // Place two flags and connect them to the headquarters
        var pointA = new Point(54, 50);
        var pointB = new Point(46, 50);

        var flag0 = map.placeFlag(player0, pointA);
        var flag1 = map.placeFlag(player0, pointB);

        var road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());
        var road1 = map.placeAutoSelectedRoad(player0, flag1, headquarter0.getFlag());

        // Place and connect six buildings
        var point1 = new Point(56, 50);
        var point2 = new Point(54, 56);
        var point3 = new Point(50, 58);
        var point4 = new Point(45, 51);
        var point5 = new Point(46, 56);
        var point6 = new Point(50, 44);

        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);
        var woodcutter1 = map.placeBuilding(new Woodcutter(player0), point2);
        var woodcutter2 = map.placeBuilding(new Woodcutter(player0), point3);
        var woodcutter3 = map.placeBuilding(new Woodcutter(player0), point4);
        var woodcutter4 = map.placeBuilding(new Woodcutter(player0), point5);
        var woodcutter5 = map.placeBuilding(new Woodcutter(player0), point6);

        map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), flag0);
        map.placeAutoSelectedRoad(player0, woodcutter1.getFlag(), flag0);
        map.placeAutoSelectedRoad(player0, woodcutter2.getFlag(), flag0);
        // Woodcutter 3 is already connected...
        map.placeAutoSelectedRoad(player0, woodcutter4.getFlag(), flag1);
        map.placeAutoSelectedRoad(player0, woodcutter5.getFlag(), flag1);

        // Verify that a builder is created - six builders leave the headquarters
        var statisticsManager = map.getStatisticsManager();
        var workerStatistics = statisticsManager.getGeneralStatistics(player0).workers();

        assertEquals(workerStatistics.getMeasurements().size(), 1);
        assertEquals(workerStatistics.getMeasurements().getFirst().time(), 1);
        assertTrue(headquarter0.getAmount(HAMMER) > 1);

        var workersBefore = workerStatistics.getMeasurements().getFirst().value();

        for (int i = 0; i < 2000; i++) {
            if (map.getWorkers().stream().filter(worker -> worker instanceof Builder).count() == 6) {
                break;
            }

            map.stepTime();
        }

        assertEquals(map.getWorkers().stream().filter(worker -> worker instanceof Builder).count(), 6);
        assertEquals(workerStatistics.getMeasurements().size(), 2);
        assertTrue(workerStatistics.getMeasurements().getLast().time() > 1);
        assertEquals(workerStatistics.getMeasurements().getLast().value(), workersBefore + 1);
    }

}
