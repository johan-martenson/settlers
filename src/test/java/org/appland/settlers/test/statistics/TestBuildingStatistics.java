package org.appland.settlers.test.statistics;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestBuildingStatistics {

    @Test
    public void testBuildingStatisticsAtStartForTwoPlayers() throws InvalidUserActionException {

        // Starting new game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 100, 100);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        var point1 = new Point(40, 40);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Verify that the building statistics are correct
        var statisticsManager = map.getStatisticsManager();

        var buildingStatsForAllPlayers = statisticsManager.getBuildingStatistics();
        var generalStatisticsForPlayer0 = statisticsManager.getGeneralStatistics(player0);
        var generalStatisticsForPlayer1 = statisticsManager.getGeneralStatistics(player1);

        assertEquals(buildingStatsForAllPlayers.size(), 2);
        assertEquals(buildingStatsForAllPlayers.get(player0).size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Headquarter.class).getMeasurements().size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Headquarter.class).getMeasurements().getFirst().time(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Headquarter.class).getMeasurements().getFirst().value(), 1);
        assertEquals(generalStatisticsForPlayer0.totalAmountBuildings().getMeasurements().getFirst().time(), 1);
        assertEquals(generalStatisticsForPlayer0.totalAmountBuildings().getMeasurements().getFirst().value(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).get(Headquarter.class).getMeasurements().size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).get(Headquarter.class).getMeasurements().getFirst().time(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).get(Headquarter.class).getMeasurements().getFirst().value(), 1);
        assertEquals(generalStatisticsForPlayer1.totalAmountBuildings().getMeasurements().getFirst().time(), 1);
        assertEquals(generalStatisticsForPlayer1.totalAmountBuildings().getMeasurements().getFirst().value(), 1);
    }

    // Test step time doesn't impact
    @Test
    public void testAdditionalTimeDoesNotImpactBuildingStatistics() throws InvalidUserActionException {

        // Starting new game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 100, 100);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        var point1 = new Point(40, 40);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Verify that the building statistics are correct
        var statisticsManager = map.getStatisticsManager();

        var buildingStatsForAllPlayers = statisticsManager.getBuildingStatistics();

        assertEquals(buildingStatsForAllPlayers.size(), 2);
        assertEquals(buildingStatsForAllPlayers.get(player0).size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Headquarter.class).getMeasurements().size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Headquarter.class).getMeasurements().getFirst().time(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Headquarter.class).getMeasurements().getFirst().value(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).get(Headquarter.class).getMeasurements().size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).get(Headquarter.class).getMeasurements().getFirst().time(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).get(Headquarter.class).getMeasurements().getFirst().value(), 1);

        Utils.fastForward(1000, map);

        assertEquals(buildingStatsForAllPlayers.size(), 2);
        assertEquals(buildingStatsForAllPlayers.get(player0).size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Headquarter.class).getMeasurements().size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Headquarter.class).getMeasurements().getFirst().time(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Headquarter.class).getMeasurements().getFirst().value(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).get(Headquarter.class).getMeasurements().size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).get(Headquarter.class).getMeasurements().getFirst().time(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).get(Headquarter.class).getMeasurements().getFirst().value(), 1);
    }

    @Test
    public void testBurningDownHouseImpactsBuildingStatistics() throws InvalidUserActionException {

        // Starting new game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 100, 100);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).totalAmountBuildings().getMeasurements().size(), 1);

        var point1 = new Point(40, 40);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place woodcutter, connect it to the headquarters, and wait for it to get constructed
        var point2 = new Point(15, 5);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point2);

        var road = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).totalAmountBuildings().getMeasurements().size(), 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).totalAmountBuildings().getMeasurements().getLast().value(), 1);


        Utils.waitForBuildingToBeConstructed(woodcutter);

        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).totalAmountBuildings().getMeasurements().size(), 2);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).totalAmountBuildings().getMeasurements().getLast().value(), 2);

        // Verify that burning down the house impacts the building statistics
        var statisticsManager = map.getStatisticsManager();

        var buildingStatsForAllPlayers = statisticsManager.getBuildingStatistics();
        var generalStatisticsForPlayer0 = statisticsManager.getGeneralStatistics(player0);

        System.out.println(buildingStatsForAllPlayers);

        assertEquals(buildingStatsForAllPlayers.size(), 2);
        assertEquals(buildingStatsForAllPlayers.get(player0).size(), 2);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Headquarter.class).getMeasurements().size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Headquarter.class).getMeasurements().getFirst().time(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Headquarter.class).getMeasurements().getFirst().value(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().size(), 2);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().getFirst().time(), 1);
        assertTrue(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().getLast().time() > 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().getLast().value(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).get(Headquarter.class).getMeasurements().size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).get(Headquarter.class).getMeasurements().getFirst().time(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).get(Headquarter.class).getMeasurements().getFirst().value(), 1);
        assertEquals(generalStatisticsForPlayer0.totalAmountBuildings().getMeasurements().getLast().value(), 2);
        assertEquals(generalStatisticsForPlayer0.totalAmountBuildings().getMeasurements().size(), 2);

        map.stepTime();

        woodcutter.tearDown();

        var woodcutterConstructedAt = buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().get(1).time();

        assertNotEquals(woodcutterConstructedAt, 1);
        assertEquals(buildingStatsForAllPlayers.size(), 2);
        assertEquals(buildingStatsForAllPlayers.get(player0).size(), 2);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Headquarter.class).getMeasurements().size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Headquarter.class).getMeasurements().getFirst().time(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Headquarter.class).getMeasurements().getFirst().value(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().size(), 3);
        assertTrue(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().get(1).time() > 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().get(1).value(), 1);
        assertTrue(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().getLast().time() > woodcutterConstructedAt);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().getLast().value(), 0);
        assertEquals(buildingStatsForAllPlayers.get(player1).size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).get(Headquarter.class).getMeasurements().size(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).get(Headquarter.class).getMeasurements().getFirst().time(), 1);
        assertEquals(buildingStatsForAllPlayers.get(player1).get(Headquarter.class).getMeasurements().getFirst().value(), 1);
        assertEquals(generalStatisticsForPlayer0.totalAmountBuildings().getMeasurements().getLast().value(), 1);
        assertEquals(generalStatisticsForPlayer0.totalAmountBuildings().getMeasurements().size(), 3);
    }


    // Test under construction vs unoccupied vs occupied
    @Test
    public void testPlaceMultipleBuildingsOfSameType() throws InvalidUserActionException {

        // Starting new game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 100, 100);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        var point1 = new Point(40, 40);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place woodcutter, connect it to the headquarters, and wait for it to get constructed
        var point2 = new Point(15, 5);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point2);

        var road = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(woodcutter);

        // Place a second woodcutter and connect it to the headquarters
        var point3 = new Point(12, 8);
        var woodcutter2 = map.placeBuilding(new Woodcutter(player0), point3);

        var road2 = map.placeAutoSelectedRoad(player0, woodcutter2.getFlag(), headquarter0.getFlag());

        // Verify that placing a second woodcutter increases the count
        var statisticsManager = map.getStatisticsManager();
        var buildingStatsForAllPlayers = statisticsManager.getBuildingStatistics();

        var timeFirstBuildingAdded = buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().getLast().time();

        assertEquals(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().size(), 2);
        assertTrue(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().getLast().time() > 1);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().getLast().value(), 1);

        Utils.waitForBuildingToBeConstructed(woodcutter2);

        System.out.println(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class));

        assertEquals(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().size(), 3);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().get(1).time(), timeFirstBuildingAdded);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().get(1).value(), 1);
        assertTrue(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().getLast().time() > timeFirstBuildingAdded);
        assertEquals(buildingStatsForAllPlayers.get(player0).get(Woodcutter.class).getMeasurements().getLast().value(), 2);
    }

    @Test
    public void testMonitorEventWhenNewBuildingIsConstructed() throws InvalidUserActionException {

        // Starting new game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter and connect it to the headquarters
        var point2 = new Point(15, 5);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point2);

        var road = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        // Subscribe to statistics notifications
        var statisticsManager = map.getStatisticsManager();

        var monitor = new Utils.GameViewMonitor();
        statisticsManager.addListener(monitor);

        map.stepTime();

        // Verify that the listener is called when the building statistics changes because a new building is constructed
        assertEquals(monitor.getStatisticsEvents().size(), 0);

        Utils.waitForBuildingToBeConstructed(woodcutter);

        assertEquals(monitor.getStatisticsEvents().size(), 1);

        // Verify that the event is only sent once
        map.stepTime();

        assertEquals(monitor.getStatisticsEvents().size(), 1);
    }

    @Test
    public void testStatisticsEventWhenBuildingIsTornDown() throws InvalidUserActionException {

        // Starting new game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter, connect it to the headquarters, and wait for it to get constructed
        var point2 = new Point(15, 5);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point2);

        var road = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(woodcutter);

        // Subscribe to statistics notifications
        var statisticsManager = map.getStatisticsManager();

        var monitor = new Utils.GameViewMonitor();
        statisticsManager.addListener(monitor);

        map.stepTime();

        // Verify that the listener is called when the building statistics changes because a building is torn down
        assertEquals(monitor.getStatisticsEvents().size(), 0);

        woodcutter.tearDown();

        assertEquals(monitor.getStatisticsEvents().size(), 1);

        // Verify that the event is only sent once
        map.stepTime();

        assertEquals(monitor.getStatisticsEvents().size(), 1);
    }

    // Test empty measurement at start of data series for normal building
    @Test
    public void testEmptyMeasurementAtStartOfDataSeriesForNormalBuilding() throws InvalidUserActionException {

        // Starting new game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that there is no measurement for woodcutters yet
        var statisticsManager = map.getStatisticsManager();

        assertNull(statisticsManager.getBuildingStatistics().get(player0).get(Woodcutter.class));

        // Place woodcutter, connect it to the headquarters, and wait for it to get constructed
        var point2 = new Point(15, 5);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point2);

        var road = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(woodcutter);

        // Verify that the first measurement in the data series for a woodcutter is now 0
        assertNotNull(statisticsManager.getBuildingStatistics().get(player0).get(Woodcutter.class));
        assertEquals(statisticsManager.getBuildingStatistics().get(player0).get(Woodcutter.class).getMeasurements().getFirst().value(), 0);
        assertEquals(statisticsManager.getBuildingStatistics().get(player0).get(Woodcutter.class).getMeasurements().size(), 2);
        assertEquals(statisticsManager.getBuildingStatistics().get(player0).get(Woodcutter.class).getMeasurements().getLast().value(), 1);
    }

    // Test no empty measurement at start of data series for headquarters

    // Test multiple houses of same type

    // Test down-sampling after MAX_SAMPLES is reached

    // Test removing planned or under-construction-building
}
