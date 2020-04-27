/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.LookoutTower;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Scout;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.SCOUT;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author johan
 */
public class TestLookoutTower {

    // TODO: test how much land the lookout tower discovers!

    @Test
    public void testLookoutTowerOnlyNeedsFourPlanksForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing LookoutTower */
        Point point22 = new Point(6, 22);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point22);

        /* Deliver two plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);

        lookoutTower0.putCargo(plankCargo);
        lookoutTower0.putCargo(plankCargo);
        lookoutTower0.putCargo(plankCargo);
        lookoutTower0.putCargo(plankCargo);

        /* Verify that this is enough to construct the LookoutTower */
        for (int i = 0; i < 100; i++) {
            assertTrue(lookoutTower0.underConstruction());

            map.stepTime();
        }

        assertTrue(lookoutTower0.ready());
    }

    @Test
    public void testLookoutTowerCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing LookoutTower */
        Point point22 = new Point(6, 22);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point22);

        /* Deliver one plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        lookoutTower0.putCargo(plankCargo);
        lookoutTower0.putCargo(plankCargo);
        lookoutTower0.putCargo(plankCargo);

        /* Verify that this is not enough to construct the LookoutTower */
        for (int i = 0; i < 500; i++) {
            assertTrue(lookoutTower0.underConstruction());

            map.stepTime();
        }

        assertFalse(lookoutTower0.ready());
    }

    @Test
    public void testLookoutTowerNeedsWorker() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place LookoutTower */
        Point point3 = new Point(7, 9);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the LookoutTower with the headquarter */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Unfinished lookout tower doesn't need worker */
        assertFalse(lookoutTower0.needsWorker());

        /* Finish construction of the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        assertTrue(lookoutTower0.needsWorker());
    }

    @Test
    public void testHeadquarterHasAtLeastOneScoutAtStart() {
        Headquarter headquarter = new Headquarter(null);

        assertTrue(headquarter.getAmount(SCOUT) >= 1);
    }

    @Test
    public void testLookoutTowerGetsAssignedWorker() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place LookoutTower */
        Point point3 = new Point(7, 9);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the LookoutTower with the headquarter */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        assertTrue(lookoutTower0.needsWorker());

        /* Verify that a LookoutTower worker leaves the headquarter */
        Utils.fastForward(3, map);

        assertEquals(map.getWorkers().size(), 3);

        /* Let the LookoutTower worker reach the LookoutTower */
        Scout Scout = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                Scout = (Scout)worker;
            }
        }

        assertNotNull(Scout);
        assertEquals(Scout.getTarget(), lookoutTower0.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, Scout);

        assertTrue(Scout.isInsideBuilding());
        assertEquals(Scout.getHome(), lookoutTower0);
        assertEquals(lookoutTower0.getWorker(), Scout);
    }

    @Test
    public void testUnoccupiedLookoutTowerDoesNotDiscoverLand() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place LookoutTower */
        Point point3 = new Point(7, 9);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the LookoutTower with the headquarter */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Verify that the unoccupied LookoutTower does not discover any land */
        Set<Point> discoveredLandBefore = new HashSet<>(player0.getDiscoveredLand());

        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        assertEquals(discoveredLandBefore.size(), player0.getDiscoveredLand().size());
    }

    @Test
    public void testLookoutTowerDiscoversLandWhenOccupied() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place LookoutTower */
        Point point3 = new Point(21, 5);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Connect the LookoutTower with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), lookoutTower0.getFlag());

        /* Wait for construction of the LookoutTower */
        Utils.waitForBuildingToBeConstructed(lookoutTower0);

        assertTrue(lookoutTower0.needsWorker());

        /* Verify that a LookoutTower worker leaves the headquarter */
        Utils.fastForward(3, map);

        assertEquals(map.getWorkers().size(), 3);

        /* Verifies that the lookout tower discovers new land when it gets occupied */
        Set<Point> discoveredLandBefore = new HashSet<>(player0.getDiscoveredLand());

        Scout Scout = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                Scout = (Scout)worker;
            }
        }

        assertNotNull(Scout);
        assertEquals(Scout.getTarget(), lookoutTower0.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, Scout);

        assertTrue(Scout.isInsideBuilding());
        assertEquals(Scout.getHome(), lookoutTower0);
        assertEquals(lookoutTower0.getWorker(), Scout);
        assertTrue(player0.getDiscoveredLand().size() > discoveredLandBefore.size());
    }

    @Test
    public void testOccupiedLookoutTowerProducesNothing() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place LookoutTower */
        Point point3 = new Point(7, 9);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Finish construction of the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Populate the LookoutTower */
        Worker Scout = Utils.occupyBuilding(new Scout(player0, map), lookoutTower0);

        assertTrue(Scout.isInsideBuilding());
        assertEquals(Scout.getHome(), lookoutTower0);
        assertEquals(lookoutTower0.getWorker(), Scout);

        /* Verify that the LookoutTower doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(lookoutTower0.getFlag().getStackedCargo().isEmpty());
            assertNull(Scout.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedLookoutTowerProducesNothing() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place LookoutTower */
        Point point3 = new Point(7, 9);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point3);

        /* Finish construction of the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Verify that the LookoutTower doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(lookoutTower0.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testScoutGoesBackToStorageWhenLookoutTowerIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing LookoutTower */
        Point point26 = new Point(8, 8);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point26);

        /* Finish construction of the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Occupy the LookoutTower */
        Utils.occupyBuilding(new Scout(player0, map), lookoutTower0);

        /* Destroy the LookoutTower */
        Worker Scout = lookoutTower0.getWorker();

        assertTrue(Scout.isInsideBuilding());
        assertEquals(Scout.getPosition(), lookoutTower0.getPosition());

        lookoutTower0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(Scout.isInsideBuilding());
        assertEquals(Scout.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(SCOUT);

        Utils.fastForwardUntilWorkerReachesPoint(map, Scout, headquarter0.getPosition());

        /* Verify that the Scout is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(SCOUT), amount + 1);
    }

    @Test
    public void testScoutGoesBackOnToStorageOnRoadsIfPossibleWhenLookoutTowerIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing LookoutTower */
        Point point26 = new Point(8, 8);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point26);

        /* Connect the LookoutTower with the headquarter */
        map.placeAutoSelectedRoad(player0, lookoutTower0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Occupy the LookoutTower */
        Utils.occupyBuilding(new Scout(player0, map), lookoutTower0);

        /* Destroy the LookoutTower */
        Worker Scout = lookoutTower0.getWorker();

        assertTrue(Scout.isInsideBuilding());
        assertEquals(Scout.getPosition(), lookoutTower0.getPosition());

        lookoutTower0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(Scout.isInsideBuilding());
        assertEquals(Scout.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point point : Scout.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testProductionInLookoutTowerCannotBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place LookoutTower */
        Point point1 = new Point(8, 6);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point1);

        /* Connect the LookoutTower and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Assign a worker to the LookoutTower */
        Scout Scout = new Scout(player0, map);

        Utils.occupyBuilding(Scout, lookoutTower0);

        assertTrue(Scout.isInsideBuilding());

        /* Verify that it's not possible to stop production in the LookoutTower */
        try {
            lookoutTower0.stopProduction();

            fail();
        } catch (Exception e) {

        }
    }

    @Test
    public void testProductionInLookoutTowerCannotBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place LookoutTower */
        Point point1 = new Point(8, 6);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point1);

        /* Connect the LookoutTower and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Assign a worker to the LookoutTower */
        Scout Scout = new Scout(player0, map);

        Utils.occupyBuilding(Scout, lookoutTower0);

        assertTrue(Scout.isInsideBuilding());

        /* Verify that production cannot be resumed in the LookoutTower */
        try {
            lookoutTower0.resumeProduction();

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testAssignedScoutHasCorrectSetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place LookoutTower */
        Point point1 = new Point(20, 14);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point1);

        /* Finish construction of the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Connect the LookoutTower with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), lookoutTower0.getFlag());

        /* Wait for Scout to get assigned and leave the headquarter */
        List<Scout> workers = Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Scout worker = workers.get(0);

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);
        Player player2 = new Player("Player 2", RED);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 2's headquarter */
        Headquarter headquarter2 = new Headquarter(player2);
        Point point10 = new Point(70, 70);
        map.placeBuilding(headquarter2, point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = new Fortress(player0);
        map.placeBuilding(fortress0, point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place LookoutTower close to the new border */
        Point point4 = new Point(28, 18);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point4);

        /* Finish construction of the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Occupy the LookoutTower */
        Scout worker = Utils.occupyBuilding(new Scout(player0, map), lookoutTower0);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

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

        /* Placing LookoutTower */
        Point point2 = new Point(14, 4);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, lookoutTower0.getFlag());

        /* Wait for the Scout to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0);

        Scout Scout = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                Scout = (Scout) worker;
            }
        }

        assertNotNull(Scout);
        assertEquals(Scout.getTarget(), lookoutTower0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, Scout, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the Scout has started walking */
        assertFalse(Scout.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the Scout continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, Scout, flag0.getPosition());

        assertEquals(Scout.getPosition(), flag0.getPosition());

        /* Verify that the Scout returns to the headquarter when it reaches the flag */
        assertEquals(Scout.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, Scout, headquarter0.getPosition());
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

        /* Placing LookoutTower */
        Point point2 = new Point(14, 4);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, lookoutTower0.getFlag());

        /* Wait for the Scout to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0);

        Scout Scout = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                Scout = (Scout) worker;
            }
        }

        assertNotNull(Scout);
        assertEquals(Scout.getTarget(), lookoutTower0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, Scout, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the Scout has started walking */
        assertFalse(Scout.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the Scout continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, Scout, flag0.getPosition());

        assertEquals(Scout.getPosition(), flag0.getPosition());

        /* Verify that the Scout continues to the final flag */
        assertEquals(Scout.getTarget(), lookoutTower0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, Scout, lookoutTower0.getFlag().getPosition());

        /* Verify that the Scout goes out to Scout instead of going directly back */
        assertNotEquals(Scout.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testScoutReturnsToStorageIfLookoutTowerIsDestroyed() throws Exception {

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

        /* Placing LookoutTower */
        Point point2 = new Point(14, 4);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, lookoutTower0.getFlag());

        /* Wait for the Scout to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0);

        Scout Scout = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Scout) {
                Scout = (Scout) worker;
            }
        }

        assertNotNull(Scout);
        assertEquals(Scout.getTarget(), lookoutTower0.getPosition());

        /* Wait for the Scout to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, Scout, flag0.getPosition());

        map.stepTime();

        /* See that the Scout has started walking */
        assertFalse(Scout.isExactlyAtPoint());

        /* Tear down the LookoutTower */
        lookoutTower0.tearDown();

        /* Verify that the Scout continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, Scout, lookoutTower0.getFlag().getPosition());

        assertEquals(Scout.getPosition(), lookoutTower0.getFlag().getPosition());

        /* Verify that the Scout goes back to storage */
        assertEquals(Scout.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testScoutGoesOffroadBackToClosestStorageWhenLookoutTowerIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing LookoutTower */
        Point point26 = new Point(17, 17);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point26);

        /* Finish construction of the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Occupy the LookoutTower */
        Utils.occupyBuilding(new Scout(player0, map), lookoutTower0);

        /* Place a second storage closer to the LookoutTower */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0);

        /* Destroy the LookoutTower */
        Worker Scout = lookoutTower0.getWorker();

        assertTrue(Scout.isInsideBuilding());
        assertEquals(Scout.getPosition(), lookoutTower0.getPosition());

        lookoutTower0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(Scout.isInsideBuilding());
        assertEquals(Scout.getTarget(), storage0.getPosition());

        int amount = storage0.getAmount(SCOUT);

        Utils.fastForwardUntilWorkerReachesPoint(map, Scout, storage0.getPosition());

        /* Verify that the Scout is stored correctly in the headquarter */
        assertEquals(storage0.getAmount(SCOUT), amount + 1);
    }

    @Test
    public void testScoutReturnsOffroadAndAvoidsBurningStorageWhenLookoutTowerIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing LookoutTower */
        Point point26 = new Point(17, 17);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point26);

        /* Finish construction of the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Occupy the LookoutTower */
        Utils.occupyBuilding(new Scout(player0, map), lookoutTower0);

        /* Place a second storage closer to the LookoutTower */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0);

        /* Destroy the storage */
        storage0.tearDown();

        /* Destroy the LookoutTower */
        Worker Scout = lookoutTower0.getWorker();

        assertTrue(Scout.isInsideBuilding());
        assertEquals(Scout.getPosition(), lookoutTower0.getPosition());

        lookoutTower0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(Scout.isInsideBuilding());
        assertEquals(Scout.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(SCOUT);

        Utils.fastForwardUntilWorkerReachesPoint(map, Scout, headquarter0.getPosition());

        /* Verify that the Scout is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(SCOUT), amount + 1);
    }

    @Test
    public void testScoutReturnsOffroadAndAvoidsDestroyedStorageWhenLookoutTowerIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing LookoutTower */
        Point point26 = new Point(17, 17);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point26);

        /* Finish construction of the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Occupy the LookoutTower */
        Utils.occupyBuilding(new Scout(player0, map), lookoutTower0);

        /* Place a second storage closer to the LookoutTower */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0);

        /* Destroy the storage */
        storage0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storage0);

        /* Destroy the LookoutTower */
        Worker Scout = lookoutTower0.getWorker();

        assertTrue(Scout.isInsideBuilding());
        assertEquals(Scout.getPosition(), lookoutTower0.getPosition());

        lookoutTower0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(Scout.isInsideBuilding());
        assertEquals(Scout.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(SCOUT);

        Utils.fastForwardUntilWorkerReachesPoint(map, Scout, headquarter0.getPosition());

        /* Verify that the Scout is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(SCOUT), amount + 1);
    }

    @Test
    public void testScoutReturnsOffroadAndAvoidsUnfinishedStorageWhenLookoutTowerIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing LookoutTower */
        Point point26 = new Point(17, 17);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point26);

        /* Finish construction of the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Occupy the LookoutTower */
        Utils.occupyBuilding(new Scout(player0, map), lookoutTower0);

        /* Place a second storage closer to the LookoutTower */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Destroy the LookoutTower */
        Worker Scout = lookoutTower0.getWorker();

        assertTrue(Scout.isInsideBuilding());
        assertEquals(Scout.getPosition(), lookoutTower0.getPosition());

        lookoutTower0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter instead of the unfinished closer
           storage building
           */
        assertFalse(Scout.isInsideBuilding());
        assertEquals(Scout.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(SCOUT);

        Utils.fastForwardUntilWorkerReachesPoint(map, Scout, headquarter0.getPosition());

        /* Verify that the Scout is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(SCOUT), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place LookoutTower */
        Point point26 = new Point(17, 17);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point26);

        /* Place road to connect the headquarter and the LookoutTower */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), lookoutTower0.getFlag());

        /* Finish construction of the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, lookoutTower0.getFlag().getPosition());

        /* Tear down the building */
        lookoutTower0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquarter instead of entering
        */
        assertEquals(worker.getTarget(), lookoutTower0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, lookoutTower0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testLookoutTowerAlwaysHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place LookoutTower */
        Point point1 = new Point(7, 9);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point1);

        /* Finish construction of the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Populate the LookoutTower */
        Worker armorer0 = Utils.occupyBuilding(new Scout(player0, map), lookoutTower0);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), lookoutTower0);
        assertEquals(lookoutTower0.getWorker(), armorer0);

        /* Verify that the productivity is 0% */
        for (int i = 0; i < 500; i++) {
            assertTrue(lookoutTower0.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
            assertEquals(lookoutTower0.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedLookoutTowerHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place LookoutTower */
        Point point1 = new Point(7, 9);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point1);

        /* Finish construction of the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Verify that the unoccupied LookoutTower is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(lookoutTower0.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testLookoutTowerCannotProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place LookoutTower */
        Point point1 = new Point(7, 9);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point1);

        /* Finish construction of the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Populate the LookoutTower */
        Worker Scout0 = Utils.occupyBuilding(new Scout(player0, map), lookoutTower0);

        /* Verify that the LookoutTower can't produce */
        assertFalse(lookoutTower0.canProduce());
    }

    @Test
    public void testLookoutTowerReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place LookoutTower */
        Point point1 = new Point(6, 22);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point1);

        /* Construct the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Verify that the reported output is correct */
        assertEquals(lookoutTower0.getProducedMaterial().length, 0);
    }

    @Test
    public void testLookoutTowerReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place LookoutTower */
        Point point1 = new Point(6, 22);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(lookoutTower0.getMaterialNeeded().size(), 1);
        assertTrue(lookoutTower0.getMaterialNeeded().contains(PLANK));
        assertFalse(lookoutTower0.getMaterialNeeded().contains(STONE));
        assertEquals(lookoutTower0.getTotalAmountNeeded(PLANK), 4);

        for (Material material : Material.values()) {
            if (material == PLANK) {
                continue;
            }

            assertEquals(lookoutTower0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testLookoutTowerReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place LookoutTower */
        Point point1 = new Point(6, 22);
        LookoutTower lookoutTower0 = map.placeBuilding(new LookoutTower(player0), point1);

        /* Construct the LookoutTower */
        Utils.constructHouse(lookoutTower0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(lookoutTower0.getMaterialNeeded().size(), 0);
    }
}