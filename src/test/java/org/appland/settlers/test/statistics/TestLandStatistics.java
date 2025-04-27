package org.appland.settlers.test.statistics;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.statistics.StatisticsManager;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.GENERAL_RANK;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.junit.Assert.*;

public class TestLandStatistics {

    @Test
    public void testGetLandStatistics() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Get the statistics manager instance
        StatisticsManager statisticsManager = map.getStatisticsManager();

        // Verify that it's possible to get the land statistics
        assertEquals(statisticsManager.getGeneralStatistics(player0).land().getMeasurements().getFirst().value(), player0.getOwnedLand().size());
    }

    @Test
    public void testGetCurrentTimeForGame() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Verify that it's possible to get the current time for the game
        assertTrue(map.getTime() > -1);
    }

    @Test
    public void testCurrentTimeFollowsMapStepTime() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Verify that the current time increases each time the game is stepped
        var currentTime = map.getTime();

        for (int i = 0; i < 100; i++) {

            map.stepTime();

            var newCurrentTime = map.getTime();

            assertEquals(newCurrentTime, currentTime + 1);

            currentTime = newCurrentTime;
        }
    }

    @Test
    public void testThereIsLandStatisticsAtStartForEachPlayer() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        var point1 = new Point(30, 30);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Get the statistics manager instance
        StatisticsManager statisticsManager = map.getStatisticsManager();

        // Verify that there is land statistics at start for each player
        assertEquals(statisticsManager.getGeneralStatistics(player0).land().getMeasurements().getFirst().value(), player0.getOwnedLand().size());
        assertEquals(statisticsManager.getGeneralStatistics(player1).land().getMeasurements().getFirst().value(), player1.getOwnedLand().size());
    }

    @Test
    public void testLandStatisticsIsUpdatedWhenAttackerCapturesBuilding() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);

        // Create game map choosing two players
        var map = new GameMap(List.of(player0, player1), 100, 100);

        // Place player 0's headquarters
        var point0 = new Point(13, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Clear the soldiers from the inventories
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        // Place barracks for player 0
        var point2 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point2);

        // Place barracks for player 1
        var point3 = new Point(21, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point3);

        // Finish construction
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        // Populate player 0's barracks
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks0);

        // Populate player 1's barracks
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        // Order an attack
        player0.attack(barracks1, 1, AttackStrength.STRONG);

        // Find the military that was chosen to attack
        map.stepTime();

        var attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        // Verify that a soldier leaves the attacked building to defend when the attacker reaches the flag
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        // Wait for the defender to go to the attacker
        Soldier defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        // Wait for the general to beat the private
        Utils.waitForFightToStart(map, attacker, defender);

        Utils.waitForSoldierToWinFight(attacker, map);

        // Verify that player 1's barracks is in player 1's border and not player 0's
        Utils.verifyPointIsNotWithinBorder(player0, barracks1.getPosition());
        Utils.verifyPointIsWithinBorder(player1, barracks1.getPosition());

        // Wait for the attacker to return to the fixed point
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        // Verify that the land statistics is updated when the border changes
        assertEquals(attacker.getTarget(), barracks1.getPosition());

        var statisticsManager = map.getStatisticsManager();

        assertEquals(statisticsManager.getGeneralStatistics(player0).land().getMeasurements().size(), 2);
        assertEquals(statisticsManager.getGeneralStatistics(player0).land().getMeasurements().getLast().value(), player0.getOwnedLand().size());
        assertEquals(statisticsManager.getGeneralStatistics(player0).land().getMeasurements().size(), 2);
        assertEquals(statisticsManager.getGeneralStatistics(player1).land().getMeasurements().getLast().value(), player1.getOwnedLand().size());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertEquals(statisticsManager.getGeneralStatistics(player0).land().getMeasurements().size(), 3);
        assertEquals(statisticsManager.getGeneralStatistics(player0).land().getMeasurements().getLast().value(), player0.getOwnedLand().size());
        assertEquals(statisticsManager.getGeneralStatistics(player0).land().getMeasurements().size(), 3);
        assertEquals(statisticsManager.getGeneralStatistics(player1).land().getMeasurements().getLast().value(), player1.getOwnedLand().size());
    }

    @Test
    public void testMonitoringLandStatistics() throws InvalidUserActionException {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);

        // Create game map choosing two players
        var map = new GameMap(List.of(player0, player1), 100, 100);

        // Place player 0's headquarters
        var point0 = new Point(13, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Clear the soldiers from the inventories
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        // Place barracks for player 0
        var point2 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point2);

        // Place barracks for player 1
        var point3 = new Point(21, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point3);

        // Finish construction
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        // Populate player 0's barracks
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks0);

        // Populate player 1's barracks
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        // Order an attack
        player0.attack(barracks1, 1, AttackStrength.STRONG);

        // Find the military that was chosen to attack
        map.stepTime();

        var attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        // Verify that a military leaves the attacked building to defend when the attacker reaches the flag
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        // Wait for the defender to go to the attacker
        var defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        // Wait for the general to beat the private
        Utils.waitForFightToStart(map, attacker, defender);

        Utils.waitForSoldierToWinFight(attacker, map);

        // Verify that player 1's barracks is in player 1's border and not player 0's
        Utils.verifyPointIsNotWithinBorder(player0, barracks1.getPosition());
        Utils.verifyPointIsWithinBorder(player1, barracks1.getPosition());

        // Wait for the attacker to return to the fixed point
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        // Start monitoring statistics
        var monitor = new Utils.GameViewMonitor();

        map.getStatisticsManager().addListener(monitor);

        // Verify that a monitoring event is sent when the land is updated
        assertEquals(attacker.getTarget(), barracks1.getPosition());

        var statisticsManager = map.getStatisticsManager();

        assertEquals(monitor.getStatisticsEvents().size(), 0);
        assertEquals(statisticsManager.getGeneralStatistics(player0).land().getMeasurements().size(), 2);
        assertEquals(statisticsManager.getGeneralStatistics(player0).land().getMeasurements().getLast().value(), player0.getOwnedLand().size());
        assertEquals(statisticsManager.getGeneralStatistics(player0).land().getMeasurements().size(), 2);
        assertEquals(statisticsManager.getGeneralStatistics(player1).land().getMeasurements().getLast().value(), player1.getOwnedLand().size());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertEquals(statisticsManager.getGeneralStatistics(player0).land().getMeasurements().getLast().value(), player0.getOwnedLand().size());
        assertEquals(statisticsManager.getGeneralStatistics(player0).land().getMeasurements().size(), 3);
        assertEquals(statisticsManager.getGeneralStatistics(player1).land().getMeasurements().getLast().value(), player1.getOwnedLand().size());
    }


    @Test
    public void testLandStatisticsAreCorrectAtStartForSeveralPlayers() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(20, 20);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        var point1 = new Point(60, 40);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        var point2 = new Point(20, 60);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point2);

        // Get the statistics manager instance
        StatisticsManager statisticsManager = map.getStatisticsManager();

        // Verify that the land statistics are correct for each player
        assertEquals(statisticsManager.getGeneralStatistics(player0).land().getMeasurements().getFirst().value(), player0.getOwnedLand().size());
        assertEquals(statisticsManager.getGeneralStatistics(player1).land().getMeasurements().getFirst().value(), player1.getOwnedLand().size());
        assertEquals(statisticsManager.getGeneralStatistics(player2).land().getMeasurements().getFirst().value(), player2.getOwnedLand().size());
    }
}
