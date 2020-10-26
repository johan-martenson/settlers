/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Scout;
import org.appland.settlers.model.Storehouse;
import org.appland.settlers.model.Vegetation;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.SCOUT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author johan
 */
public class TestScout {

    /**
     * TODO:
     *   - Test scout discovers points also on the way back to the storage
     *   - Test scout cannot return to storage where delivery is not allowed
     */

    @Test
    public void testScoutCanBeCalledFromFlag() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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
    public void testScoutGetsCreatedFromBow() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all scouts from the headquarter and add a bow */
        Utils.adjustInventoryTo(headquarter0, SCOUT, 0);
        Utils.adjustInventoryTo(headquarter0, Material.BOW, 1);

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                scout = worker;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that the scout keeps going */
        assertNotEquals(scout.getPosition().distance(scout.getTarget()), scout.getPosition());

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
        Point point0 = new Point(5, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                scout = (Scout) worker;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that the scout goes east toward the border */
        Utils.fastForward(100, map);

        assertTrue(scout.getPosition().getX() > flag.getPosition().getX());
    }

    @Test
    public void testScoutWalksNorthTowardAndThroughTheBorder() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 17);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(5, 25);
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

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                scout = (Scout) worker;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that the scout goes north toward the border */
        assertTrue(scout.getTarget().y > scout.getPosition().y);

        for (int i = 0; i < 200; i++) {
            if (scout.getPosition().y > flag.getPosition().y + 4) {
                break;
            }

            map.stepTime();
        }

        assertTrue(scout.getPosition().y > flag.getPosition().y + 4);
    }

    @Test
    public void testScoutWalksThirtyStepsAndThenReturnsToFlag() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(10, 18);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                scout = (Scout) worker;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that the scout walks 30 steps */
        for (int i = 0; i < 30; i++) {
            assertNotEquals(scout.getTarget(), scout.getPosition());

            assertTrue(scout.isExactlyAtPoint());

            map.stepTime();

            assertFalse(scout.isExactlyAtPoint());

            Utils.waitForWorkerToBeExactlyOnPoint(scout, map);
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                scout = (Scout) worker;
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
        Point point0 = new Point(13, 13);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                scout = (Scout) worker;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that the scout discovers new ground */
        for (int i = 0; i < 30; i++) {
            assertNotEquals(scout.getTarget(), scout.getPosition());

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
        Point point0 = new Point(10, 18);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                scout = (Scout) worker;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that the scout discovers new ground */
        for (int i = 0; i < 30; i++) {
            assertNotEquals(scout.getTarget(), scout.getPosition());

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
        Storehouse headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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
        Storehouse headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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
        Point point0 = new Point(13, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Ensure there is exactly one scout in the headquarter */
        Utils.adjustInventoryTo(headquarter0, SCOUT, 1);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                scout = (Scout) worker;
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
            for (Worker worker : map.getWorkers()) {
                if (worker instanceof Scout && flag.getPosition().equals(worker.getTarget())) {
                    scout = (Scout)worker;

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
        Point point0 = new Point(14, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Ensure there is exactly one scout in the headquarter */
        Utils.adjustInventoryTo(headquarter0, SCOUT, 1);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                scout = (Scout) worker;
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
        Point point0 = new Point(13, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Ensure there is exactly one scout in the headquarter */
        Utils.adjustInventoryTo(headquarter0, SCOUT, 1);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        List<Scout> scouts = Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0);

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Call scout from the flag */
        flag.callScout();

        /* Verify that no scout leaves the headquarter */
        for (int i = 0; i < 100; i++) {

            List<Scout> scouts = Utils.findWorkersOfTypeOutsideForPlayer(Scout.class, player0);

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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
        Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0);

        Scout scout = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                scout = (Scout) worker;
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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
        Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0);

        Scout scout = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                scout = (Scout) worker;
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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
        Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0);

        Scout scout = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                scout = (Scout) worker;
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

    @Test
    public void testScoutCanWalkAroundBlockingStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(23, 5);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Place stones that the scout needs to walk around */
        Point point2 = new Point(25, 5);
        Point point3 = new Point(24, 4);
        Point point4 = new Point(24, 6);
        Point point5 = new Point(23, 7);
        Point point6 = new Point(23, 3);

        map.placeStone(point2);
        map.placeStone(point3);
        map.placeStone(point4);
        map.placeStone(point5);
        map.placeStone(point6);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to come out */
        Scout scout = Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0).get(0);

        /* Ensure that the scout goes around the stones */
        for (int i = 0; i < 1000; i++) {

            if (scout.getPosition().x > 27) {
                break;
            }

            map.stepTime();
        }

        assertTrue(scout.getPosition().x > 27);
    }

    @Test
    public void testScoutWalksOtherDirectionIfBlockedBySea() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(17, 23);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Place the sea that the scout should walk along side */
        for (int i = 0; i < 40; i += 2) {
            map.surroundWithVegetation(new Point(i, 24), Vegetation.WATER);
        }

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to come out */
        Scout scout = Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0).get(0);

        /* Wait for the scout to reach the flag */
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag.getPosition());

        /* Ensure that the scout walks and does not get stuck until it is back in the headquarter */
        for (int i = 0; i < 100; i++) {

            if (scout.getPosition().equals(headquarter0.getPosition())) {
                break;
            }

            assertNotEquals(scout.getPosition(), scout.getTarget());

            Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());
        }

        assertEquals(scout.getPosition(), headquarter0.getPosition());
    }

    @Test
    public void testScoutContinuouslyDiscoversWithSameRadius() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Placing headquarter */
        Point point0 = new Point(9, 23);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(17, 23);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to come out */
        Scout scout = Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0).get(0);

        /* Wait for the scout to reach the flag */
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag.getPosition());

        /* Verify that the scout discovers its surroundings with the same radius until it goes back to the headquarter */
        for (int i = 0; i < 100; i++) {

            if (scout.getPosition().equals(headquarter0.getPosition())) {
                break;
            }

            if (scout.isExactlyAtPoint()) {
                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().left()));
                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().upLeft()));
                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().upRight()));
                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().right()));
                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().downRight()));
                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().downLeft()));

                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().left().upLeft()));
                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().left().up()));
                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().upLeft().up()));
                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().up().up()));

                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().right().upRight()));
                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().right().up()));
                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().upRight().up()));

                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().left().downLeft()));
                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().left().down()));
                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().downLeft().down()));
                assertTrue(
                        !map.isWithinMap(scout.getPosition().down().down()) ||
                        player0.getDiscoveredLand().contains(scout.getPosition().down().down())
                );

                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().right().downRight()));
                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().right().down()));
                assertTrue(player0.getDiscoveredLand().contains(scout.getPosition().downRight().down()));
            }

            assertNotEquals(scout.getPosition(), scout.getTarget());

            Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());
        }

        assertEquals(scout.getPosition(), headquarter0.getPosition());
    }

    @Test
    public void testScoutReturnsOffroadIfRoadIsMissingWhScoutReachesFlag() throws InvalidRouteException, InvalidUserActionException, InvalidEndPointException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Placing headquarter */
        Point point0 = new Point(9, 23);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(17, 23);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to come out */
        Scout scout = Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0).get(0);

        /* Wait for the scout to reach the flag */
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag.getPosition());

        /* Wait for the scout to leave the flag */
        assertEquals(scout.getPosition(), flag.getPosition());
        assertTrue(scout.isExactlyAtPoint());

        map.stepTime();

        assertFalse(scout.isExactlyAtPoint());

        /* Wait for the scout to be on the way back to the flag */
        Utils.waitForWorkerToSetTarget(map, scout, flag.getPosition());

        /* Wait for the scout to be almost at the flag */
        for (int i = 0; i < 2000; i++) {
            if (!scout.isExactlyAtPoint() && scout.getNextPoint().equals(flag.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertFalse(scout.isExactlyAtPoint());
        assertEquals(scout.getNextPoint(), flag.getPosition());

        /* Remove the road between the flag and the headquarter */
        map.removeRoad(road0);

        /* Verify that the scout walks offroad back to the headquarter */
        Utils.verifyWorkerWalksToTarget(map, scout, headquarter0.getPosition());
    }
}
