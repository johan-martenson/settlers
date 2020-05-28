/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.SawmillWorker;
import org.appland.settlers.model.Well;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author johan
 */
public class TestWorker {

    @Test
    public void testWorkerCannotEnterBuildingWhenItsNotAtRightPosition() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Sawmill sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Occupy the sawmill */
        SawmillWorker worker = new SawmillWorker(player0, map);
        map.placeWorker(worker, sawmill.getFlag());

        /* Verify that the worker cannot enter the sawmill when standing at the flag */
        try {
            worker.enterBuilding(sawmill);
            fail();
        } catch (Exception e) {}

        assertFalse(worker.isInsideBuilding());
    }

    @Test
    public void testWalking() throws Exception {

        /* Create gamemap */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(13, 5);
        Building well = map.placeBuilding(new Well(player0), point1);

        /* Place flag */
        Point point4 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point4);

        /* Connect the headquarter with the flag */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Connect the well with the flag */
        Point point5 = new Point(12, 4);
        Road road1 = map.placeRoad(player0, point4, point5, well.getFlag().getPosition());

        /* Place a courier at the headquarter */
        Courier courier = new Courier(player0, map);

        map.placeWorker(courier, headquarter);

        /* Assign the courier to the remote road */
        courier.assignToRoad(road1);

        /* Verify that the courier is correct when it starts walking */
        assertEquals(courier.getPosition(), headquarter.getPosition());
        assertEquals(courier.getNextPoint(), headquarter.getFlag().getPosition());
        assertEquals(courier.getLastPoint(), headquarter.getPosition());
        assertTrue(courier.isExactlyAtPoint());
        assertTrue(courier.isTraveling());
        assertTrue(courier.isAt(headquarter.getPosition()));

        /* Verify that the courier walks correctly to the the headquarter's flag */
        for (int i = 1; i < 10; i++) {

            map.stepTime();

            /* Verify that these are unaffected */
            assertEquals(courier.getNextPoint(), headquarter.getFlag().getPosition());
            assertEquals(courier.getLastPoint(), headquarter.getPosition());
            assertTrue(courier.isTraveling());

            /* Verify that the courier is not exactly at a point */
            assertFalse(courier.isExactlyAtPoint());
            assertFalse(courier.isAt(headquarter.getPosition()));
            assertFalse(courier.isAt(headquarter.getFlag().getPosition()));

            /* Verify that the percentage increases correctly */
            assertEquals(courier.getPercentageOfDistanceTraveled(), i * 10);
        }

        map.stepTime();

        /* Verify that the courier reached the headquarter's flag correctly */
        assertEquals(courier.getPosition(), headquarter.getFlag().getPosition());
        assertEquals(courier.getNextPoint(), point3);
        assertEquals(courier.getLastPoint(), headquarter.getFlag().getPosition());
        assertEquals(courier.getPercentageOfDistanceTraveled(), 100);
        assertTrue(courier.isExactlyAtPoint());
        assertTrue(courier.isTraveling());
        assertTrue(courier.isAt(headquarter.getFlag().getPosition()));

        /* Verify that the courier walks correctly to the middle of the first road */
        for (int i = 1; i < 10; i++) {

            map.stepTime();

            /* Verify that these are unaffected */
            assertEquals(courier.getNextPoint(), point3);
            assertEquals(courier.getLastPoint(), headquarter.getFlag().getPosition());
            assertTrue(courier.isTraveling());

            /* Verify that the courier is not exactly at a point */
            assertFalse(courier.isExactlyAtPoint());
            assertFalse(courier.isAt(point3));
            assertFalse(courier.isAt(headquarter.getFlag().getPosition()));

            /* Verify that the percentage increases correctly */
            assertEquals(courier.getPercentageOfDistanceTraveled(), i * 10);
        }

        map.stepTime();

        /* Verify that the courier reached the middle of the road correctly */
        assertEquals(courier.getPosition(), point3);
        assertEquals(courier.getNextPoint(), flag0.getPosition());
        assertEquals(courier.getLastPoint(), point3);
        assertEquals(courier.getPercentageOfDistanceTraveled(), 100);
        assertTrue(courier.isExactlyAtPoint());
        assertTrue(courier.isTraveling());
        assertTrue(courier.isAt(point3));

        /* Verify that the courier walks correctly to the end of the first road */
        for (int i = 1; i < 10; i++) {

            map.stepTime();

            /* Verify that these are unaffected */
            assertEquals(courier.getNextPoint(), flag0.getPosition());
            assertEquals(courier.getLastPoint(), point3);
            assertTrue(courier.isTraveling());

            /* Verify that the courier is not exactly at a point */
            assertFalse(courier.isExactlyAtPoint());
            assertFalse(courier.isAt(flag0.getPosition()));
            assertFalse(courier.isAt(point3));

            /* Verify that the percentage increases correctly */
            assertEquals(courier.getPercentageOfDistanceTraveled(), i * 10);
        }

        map.stepTime();

        /* Verify that the courier reached the end of the road correctly */
        assertEquals(courier.getPosition(), flag0.getPosition());
        assertEquals(courier.getNextPoint(), point5);
        assertEquals(courier.getLastPoint(), flag0.getPosition());
        assertEquals(courier.getPercentageOfDistanceTraveled(), 100);
        assertTrue(courier.isExactlyAtPoint());
        assertTrue(courier.isTraveling());
        assertTrue(courier.isAt(flag0.getPosition()));

        /* Verify that next point is available when the courier gets close to the target */
        assertEquals(courier.getTarget(), point5);

        for (int i = 1; i < 10; i++) {

            assertEquals(courier.getNextPoint(), point5);

            map.stepTime();
        }
    }

    @Test
    public void testWalkingHalfway() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(31, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(17, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(17, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        assertFalse(player0.getLandInPoints().contains(barracks1.getPosition()));
        assertFalse(player0.getLandInPoints().contains(barracks1.getFlag().getPosition()));
        assertTrue(barracks1.isReady());

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);
        assertFalse(attacker.isFighting());

        /* Wait for the military to reach the attacked building */
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertTrue(attacker.isExactlyAtPoint());
        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);

        /* Wait for the defender to go to the attacker */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        Point fightingPoint = attacker.getPosition();

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());
        assertFalse(defender.isFighting());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Verify that the attacker and the defender walk apart */
        assertFalse(attacker.isFighting());
        assertFalse(defender.isFighting());

        int attackersDistance = -1;
        int defendersDistance = -1;

        for (int i = 0; i < 20; i++) {

            if (attacker.isExactlyAtPoint() || defender.isExactlyAtPoint()) {
                map.stepTime();

                continue;
            }

            assertEquals(attacker.getNextPoint(), fightingPoint.left());
            assertEquals(defender.getNextPoint(), fightingPoint.right());

            assertEquals(attacker.getLastPoint(), fightingPoint);
            assertEquals(defender.getLastPoint(), fightingPoint);

            assertFalse(attacker.isExactlyAtPoint());
            assertFalse(defender.isExactlyAtPoint());

            int newAttackersDistance = attacker.getPercentageOfDistanceTraveled();
            int newDefendersDistance = defender.getPercentageOfDistanceTraveled();

            assertTrue(newAttackersDistance > attackersDistance);
            assertTrue(newDefendersDistance > defendersDistance);

            attackersDistance = newAttackersDistance;
            defendersDistance = newDefendersDistance;

            if (newAttackersDistance >= 50 && newDefendersDistance >= 50) {
                break;
            }

            assertTrue(attackersDistance < 50);
            assertTrue(defendersDistance < 50);

            assertFalse(attacker.isFighting());
            assertFalse(defender.isFighting());

            map.stepTime();
        }

        assertEquals(attackersDistance, 50);
        assertEquals(defendersDistance, 50);

        /* Verify that the soldiers fight */
        assertTrue(attacker.isFighting());
        assertTrue(defender.isFighting());

        /* Wait for the fight to end */
        for (int i = 0; i < 200; i++) {

            /* Break when one of the soldiers is gone */
            if (!map.getWorkers().contains(attacker) || !map.getWorkers().contains(defender)) {
                break;
            }

            /* Verify that the soldiers stay in place */
            assertEquals(attacker.getPercentageOfDistanceTraveled(), attackersDistance);
            assertEquals(defender.getPercentageOfDistanceTraveled(), defendersDistance);

            map.stepTime();
        }

        assertTrue(!map.getWorkers().contains(attacker) || !map.getWorkers().contains(defender));

        /* Get the winner */
        Military winner;

        if (!map.getWorkers().contains(attacker)) {
            winner = defender;
        } else {
            winner = attacker;
        }

        /* Verify that the winner isn't fighting when it's walking back */
        assertFalse(winner.isFighting());

        /* Verify that the winner walks back to the flag */
        assertEquals(winner.getTarget(), barracks1.getFlag().getPosition());

        int distance = winner.getPercentageOfDistanceTraveled();

        for (int i = 0; i < 10; i++) {

            map.stepTime();

            if (winner.isExactlyAtPoint()) {
                break;
            }

            assertEquals(winner.getNextPoint(), barracks1.getFlag().getPosition());
            assertEquals(winner.getLastPoint(), barracks1.getFlag().getPosition().left());

            int newDistance = winner.getPercentageOfDistanceTraveled();

            assertTrue(newDistance > distance);

            distance = newDistance;
        }

        assertEquals(winner.getPosition(), barracks1.getFlag().getPosition());
    }
}
