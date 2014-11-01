/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Scout;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.SCOUT;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Worker;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);

        /* Call scout from the flag */
        flag.callScout();
    }

    @Test
    public void testStorageDispatchesScoutWhenItHasBeenCalled() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        int amountWorkers = map.getAllWorkers().size();

        flag.callScout();

        /* Verify that a scout is dispatched from the headquarter */
        map.stepTime();

        assertEquals(map.getAllWorkers().size(), amountWorkers + 1);

        Utils.verifyListContainsWorkerOfType(map.getAllWorkers(), Scout.class);
    }

    @Test
    public void testScoutGetsToFlagThenLeavesToNearbySpot() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Worker scout = null;

        for (Worker w : map.getAllWorkers()) {
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
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker w : map.getAllWorkers()) {
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
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(5, 23);
        Flag flag = map.placeFlag(point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker w : map.getAllWorkers()) {
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
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker w : map.getAllWorkers()) {
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
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(9, 3);
        Flag flag = map.placeFlag(point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Scout) {
                scout = (Scout) w;
            }
        }

        assertNotNull(scout);
        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, scout.getTarget());

        /* Verify that the scout does not go outside the map */
        for (int i = 0; i < 1000; i++) {
            assertTrue(map.isWithinBorder(scout.getTarget()));
            assertTrue(map.isWithinBorder(scout.getPosition()));

            if (scout.getTarget().equals(headquarter0.getPosition())) {
                break;
            }

            map.stepTime();
        }
    }

    @Test
    public void testScoutDiscoversNewGround() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker w : map.getAllWorkers()) {
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

            assertTrue(map.getDiscoveredLand().contains(scout.getPosition()));
        }

        assertEquals(scout.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag.getPosition());

        assertEquals(scout.getPosition(), flag.getPosition());
    }

    @Test
    public void testDepositingScoutIncreasesAmountOfScouts() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Storage headquarter0 = (Storage) map.placeBuilding(new Headquarter(), point0);

        /* Add a scout to the headquarter and verify that the amount goes up*/
        int amount = headquarter0.getAmount(SCOUT);

        headquarter0.depositWorker(new Scout(map));

        assertEquals(headquarter0.getAmount(SCOUT), amount + 1);
    }

    @Test
    public void testSeveralScoutsCanBeCalled() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Storage headquarter0 = (Storage) map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);

        /* Add more scouts to the headquarter */
        headquarter0.depositWorker(new Scout(map));

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call scout from the flag */
        int workers = map.getAllWorkers().size();

        flag.callScout();

        /* Wait for the scout to leave the headquarter */
        map.stepTime();

        assertEquals(map.getAllWorkers().size(), workers + 1);

        /* Call for another scout and verify that there is a new scout on the way*/
        flag.callScout();

        map.stepTime();

        assertEquals(map.getAllWorkers().size(), workers + 2);
    }

    @Test
    public void testScoutGoesOutAgainIfNeeded() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Ensure there is exactly one scout in the headquarter */
        Utils.adjustInventoryTo((Storage)headquarter0, SCOUT, 1, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker w : map.getAllWorkers()) {
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

        System.out.println(" SCOUTS IN STORAGE " + headquarter0.getAmount(SCOUT));
        
        for (int i = 0; i < 100; i++) {
            for (Worker w : map.getAllWorkers()) {
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
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Ensure there is exactly one scout in the headquarter */
        Utils.adjustInventoryTo((Storage)headquarter0, SCOUT, 1, map);

        /* Call scout from the flag */
        flag.callScout();

        /* Wait for the scout to go to the flag */
        map.stepTime();

        Scout scout = null;

        for (Worker w : map.getAllWorkers()) {
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

    // TODO: test that scout goes via the flag when it returns to the storage
}
