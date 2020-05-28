package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.LandDataPoint;
import org.appland.settlers.model.LandStatistics;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.StatisticsManager;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestLandStatistics {

    @Test
    public void testGetLandStatistics() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);

        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Get the statistics manager instance */
        StatisticsManager statisticsManager = map.getStatisticsManager();

        /* Verify that it's possible to get the land statistics */
        LandStatistics landStatistics = statisticsManager.getLandStatistics();

        assertNotNull(landStatistics);
    }

    @Test
    public void testGetCurrentTimeForGame() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);

        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Verify that it's possible to get the current time for the game */
        assertTrue(map.getCurrentTime() > -1);
    }

    @Test
    public void testCurrentTimeFollowsMapStepTime() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);

        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Verify that the current time increases each time the game is stepped */
        long currentTime = map.getCurrentTime();

        for (int i = 0; i < 100; i++) {

            map.stepTime();

            long newCurrentTime = map.getCurrentTime();

            assertEquals(newCurrentTime, currentTime + 1);

            currentTime = newCurrentTime;
        }
    }

    @Test
    public void testThereIsLandStatisticsAtStartForEachPlayer() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);

        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);

        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Get the statistics manager instance */
        StatisticsManager statisticsManager = map.getStatisticsManager();

        /* Verify that there is land statistics at start for each player */
        LandStatistics landStatistics = statisticsManager.getLandStatistics();

        List<LandDataPoint> landDataPoints = landStatistics.getDataPoints();

        assertNotNull(landDataPoints);
        assertEquals(landDataPoints.size(), 1);
        assertEquals(landDataPoints.get(0).getValues().length, 2);
    }


    @Test
    public void testLandStatisticsIsUpdatedWhenAttackerCapturesBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(13, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(21, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);

        /* Wait for the defender to go to the attacker */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Wait for the general to beat the private */
        Utils.waitForWorkerToDisappear(defender, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Verify that player 1's barracks is in player 1's border and not player 0's */
        Utils.verifyPointIsNotWithinBorder(player0, barracks1.getPosition());
        Utils.verifyPointIsWithinBorder(player1, barracks1.getPosition());

        /* Wait for the attacker to return to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Verify that the land statistics is updated when the border changes */
        LandStatistics landStatistics = map.getStatisticsManager().getLandStatistics();

        assertEquals(attacker.getTarget(), barracks1.getPosition());

        int numberMeasurements = landStatistics.getDataPoints().size();

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        int lastIndex = landStatistics.getDataPoints().size() - 1;

        assertEquals(landStatistics.getDataPoints().size(), numberMeasurements + 1);
        assertTrue(landStatistics.getDataPoints().get(lastIndex).getValues()[0] > landStatistics.getDataPoints().get(lastIndex - 1).getValues()[0]);
        assertTrue(landStatistics.getDataPoints().get(lastIndex).getValues()[1] < landStatistics.getDataPoints().get(lastIndex - 1).getValues()[1]);
        assertTrue(landStatistics.getDataPoints().get(lastIndex).getTime() > landStatistics.getDataPoints().get(lastIndex - 1).getTime());
    }

}
