/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Scout;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.SCOUT;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Worker;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestScout {

    @Test
    public void testScoutCanBeCalledFromFlag() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Call scout from the flag */
        flag.callScout();
    }

    @Test
    public void testStorageDispatchesScoutWhenItHasBeenCalled() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        int amountWorkers = map.getWorkers().size();

        flag.callScout();

        /* Verify that a scout is dispatched from the headquarter */
        map.stepTime();

        assertEquals(map.getWorkers().size(), amountWorkers + 1);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Scout.class);
    }

    @Test
    public void testScoutGetsToFlagThenLeavesToNearbySpot() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Worker scout = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Scout) {
                scout = w;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that the scout keeps going to a point within a radius of 3 */
        assertTrue(scout.getPosition().distance(scout.getTarget()) < 4);

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());
    }

    @Test
    public void testScoutWalksEastTowardAndThroughTheBorder() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(19, 9);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Scout) {
                scout = (Scout) w;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that the scout goes south toward the border */
        assertTrue(scout.getTarget().getX() > scout.getPosition().getX());
    }

    @Test
    public void testScoutWalksNorthTowardAndThroughTheBorder() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(5, 23);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Scout) {
                scout = (Scout) w;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that the scout goes north toward the border */
        assertTrue(scout.getTarget().getY() > scout.getPosition().getY());
    }

    @Test
    public void testScoutCanUseEnemiesRoads() {
        // TODO: Implement test
    }

    @Test
    public void testScoutWalksEightByThreeSegmentsAndThenReturnsToFlag() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(18, 18);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Scout) {
                scout = (Scout) w;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that the scout walks eight by three segments */
        for (int i = 0; i < 8; i++) {
            assertFalse(scout.getTarget().equals(scout.getPosition()));
            assertTrue(scout.getPosition().distance(scout.getTarget()) < 4);

            Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());
        }

        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag.getPosition());

        assertEquals(scout.getPosition(), flag.getPosition());
    }

    @Test
    public void testScoutDoesNotGoOutsideTheMap() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(9, 3);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Scout) {
                scout = (Scout) w;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that the scout does not go outside the map */
        for (int i = 0; i < 1000; i++) {
            assertTrue(map.isWithinMap(scout.getTarget()));
            assertTrue(map.isWithinMap(scout.getPosition()));

            if (scout.getTarget().equals(headquarter0.getPosition())) {
                break;
            }

            map.stepTime();
        }
    }

    @Test
    public void testScoutDiscoversNewGround() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(18, 18);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Scout) {
                scout = (Scout) w;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that the scout discovers new ground */
        for (int i = 0; i < 8; i++) {
            assertFalse(scout.getTarget().equals(scout.getPosition()));
            assertTrue(scout.getPosition().distance(scout.getTarget()) < 4);

            Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

            assertTrue(player0.getDiscoveredLand().contains(scout.getPosition()));
        }

        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag.getPosition());

        assertEquals(scout.getPosition(), flag.getPosition());
    }

    @Test
    public void testScoutDiscoversNewGroundForPlayer() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(18, 18);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Scout) {
                scout = (Scout) w;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that the scout discovers new ground */
        for (int i = 0; i < 8; i++) {
            assertFalse(scout.getTarget().equals(scout.getPosition()));
            assertTrue(scout.getPosition().distance(scout.getTarget()) < 4);

            Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

            assertTrue(player0.getDiscoveredLand().contains(scout.getPosition()));
        }

        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag.getPosition());

        assertEquals(scout.getPosition(), flag.getPosition());
    }

    @Test
    public void testDepositingScoutIncreasesAmountOfScouts() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Storage headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Add a scout to the headquarter and verify that the amount goes up */
        int amount = headquarter0.getAmount(SCOUT);

        headquarter0.depositWorker(new Scout(player0, map));

        assertEquals(headquarter0.getAmount(SCOUT), amount + 1);
    }

    @Test
    public void testSeveralScoutsCanBeCalled() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Storage headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Add more scouts to the headquarter */
        headquarter0.depositWorker(new Scout(player0, map));

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        int workers = map.getWorkers().size();

        flag.callScout();

        /* Wait for the scout to leave the headquarter */
        map.stepTime();

        assertEquals(map.getWorkers().size(), workers + 1);

        /* Call for another scout and verify that there is a new scout on the way */
        flag.callScout();

        map.stepTime();

        assertEquals(map.getWorkers().size(), workers + 2);
    }

    @Test
    public void testScoutGoesOutAgainIfNeeded() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Ensure there is exactly one scout in the headquarter */
        Utils.adjustInventoryTo(headquarter0, SCOUT, 1, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Scout) {
                scout = (Scout) w;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Call for a second scout */
        flag.callScout();

        /* Wait for the scout to explore and then return to the flag */
        for (int i = 0; i < 1000; i++) {
            if (flag.getPosition().equals(scout.getTarget())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag.getPosition());

        /* Let the scout go back to the headquarter */
        assertEquals(scout.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, headquarter0.getPosition());

        /* Verify that the scout leaves again */
        scout = null;

        for (int i = 0; i < 100; i++) {
            for (Worker w : map.getWorkers()) {
                if (w instanceof Scout && flag.getPosition().equals(w.getTarget())) {
                    scout = (Scout)w;

                    break;
                }
            }

            if (scout != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());
    }

    @Test
    public void testReturningScoutIncreasesAmountInStorage() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Ensure there is exactly one scout in the headquarter */
        Utils.adjustInventoryTo(headquarter0, SCOUT, 1, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Scout) {
                scout = (Scout) w;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that the amount of scouts in the headquarter is 0 */
        assertEquals(headquarter0.getAmount(SCOUT), 0);

        /* Wait for the scout to explore and then return to the flag */
        for (int i = 0; i < 1000; i++) {
            if (flag.getPosition().equals(scout.getTarget())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag.getPosition());

        /* Let the scout go back to the headquarter */
        assertEquals(scout.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, headquarter0.getPosition());

        /* Verify that the amount of scouts is 1 */
        assertEquals(headquarter0.getAmount(SCOUT), 1);
    }

    @Test
    public void testAssignedScoutHasThePlayerSetCorrectly() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Ensure there is exactly one scout in the headquarter */
        Utils.adjustInventoryTo(headquarter0, SCOUT, 1, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        List<Scout> scouts = Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0, map);

        assertEquals(scouts.size(), 1);

        Scout scout = scouts.get(0);

        assertEquals(scout.getPlayer(), player0);
    }

    @Test
    public void testNothingHappensWithScoutCalledFromFlagWithoutConnectionToStorage() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Call scout from the flag */
        flag.callScout();

        /* Verify that no scout leaves the headquarter */
        for (int i = 0; i < 100; i++) {

            List<Scout> scouts = Utils.findWorkersOfTypeOutsideForPlayer(Scout.class, player0, map);

            assertEquals(scouts.size(), 0);

            map.stepTime();
        }
    }

    // TODO: test that scout goes via the flag when it returns to the storage

    @Test
    public void testScoutReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing second flag */
        Point point2 = new Point(14, 4);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Call scout from the second flag */
        flag1.callScout();

        /* Wait for the scout to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0, map);

        Scout scout = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Scout) {
                scout = (Scout) w;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the scout has started walking */
        assertFalse(scout.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the scout continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag0.getPosition());

        assertEquals(scout.getPosition(), flag0.getPosition());

        /* Verify that the scout returns to the headquarter when it reaches the flag */
        assertEquals(scout.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, headquarter0.getPosition());

        assertTrue(scout.isInsideBuilding());
    }

    @Test
    public void testScoutContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing second flag */
        Point point2 = new Point(14, 4);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Call scout from the second flag */
        flag1.callScout();

        /* Wait for the scout to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0, map);

        Scout scout = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Scout) {
                scout = (Scout) w;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the scout has started walking */
        assertFalse(scout.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the scout continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag0.getPosition());

        assertEquals(scout.getPosition(), flag0.getPosition());

        /* Verify that the scout continues to the final flag */
        assertEquals(scout.getTarget(), flag1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag1.getPosition());

        /* Verify that the scout goes out to scout instead of going directly back */
        assertNotEquals(scout.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testScoutContinuesEvenIfFlagIsRemovedWhenItIsClose() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing second flag */
        Point point2 = new Point(14, 4);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Call scout from the second flag */
        flag1.callScout();

        /* Wait for the scout to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0, map);

        Scout scout = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Scout) {
                scout = (Scout) w;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag1.getPosition());

        /* Wait for the scout to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag0.getPosition());

        map.stepTime();

        /* See that the scout has started walking */
        assertFalse(scout.isExactlyAtPoint());

        /* Remove the second flag */
        map.removeFlag(flag1);

        /* Verify that the scout continues walking to the second flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag1.getPosition());

        assertEquals(scout.getPosition(), flag1.getPosition());

        /* Verify that the scout goes out to scout instead of going directly back */
        map.stepTime();

        assertNotEquals(scout.getTarget(), headquarter0.getPosition());
    }
}
