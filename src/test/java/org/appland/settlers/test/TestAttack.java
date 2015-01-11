/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GuardHouse;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.SERGEANT;
import org.appland.settlers.model.Military;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Military.Rank.SERGEANT_RANK;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.WatchTower;
import org.appland.settlers.model.Woodcutter;
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
public class TestAttack {

    @Test
    public void testNoAvailableAttackersWhenOutOfReach() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter far away from player 0 */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(90, 90);
        map.placeBuilding(headquarter1, point1);

        /* Verify that there are no available attackers for each player to
         attack the other */
        assertEquals(player0.getAvailableAttackersForBuilding(headquarter1), 0);
        assertEquals(player1.getAvailableAttackersForBuilding(headquarter0), 0);
    }

    @Test(expected = Exception.class)
    public void testNoAvailableAttackersForNonMilitaryBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter far away from player 0 */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(90, 90);
        map.placeBuilding(headquarter1, point1);

        /* Place woodcutter for player 1 */
        Point point2 = new Point(90, 80);
        Building woodcutter0 = new Woodcutter(player1);
        map.placeBuilding(woodcutter0, point2);

        /* Verify that get available attackers can not be called for 
         non-military building */
        player0.getAvailableAttackersForBuilding(woodcutter0);
    }

    @Test(expected = Exception.class)
    public void testNoAvailableAttackersForOwnBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter far away from player 0 */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(90, 90);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(20, 10);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Verify that get available attackers can not be called for 
         own building */
        player0.getAvailableAttackersForBuilding(barracks0);
    }

    @Test
    public void testOneAvailableAttackerForBarracksCloseToEnemyBarracks() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter far away from player 0 */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);

        /* Verify that there are available attackers for player 0 to attack
         player 1's barracks */
        assertEquals(player0.getAvailableAttackersForBuilding(barracks1), 1);
    }

    @Test
    public void testTwoAvailableAttackersForGuardHouseCloseToEnemyBarracks() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter far away from player 0 */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building guardHouse0 = new GuardHouse(player0);
        map.placeBuilding(guardHouse0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(guardHouse0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 3, guardHouse0, map);

        /* Verify that there are available attackers for player 0 to attack
         player 1's barracks */
        assertEquals(player0.getAvailableAttackersForBuilding(barracks1), 2);
    }

    @Test
    public void testPlayerCanInitiateAttack() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);

        /* Verify that player 0 can attack player 1's barracks */
        player0.attack(barracks1, 1);
    }

    @Test(expected = Exception.class)
    public void testPlayerCannotAttackHimself() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);

        /* Verify that player 0 can attack its own barracks */
        player0.attack(barracks0, 1);
    }

    @Test(expected = Exception.class)
    public void testCannotAttackNonMilitaryBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place woodcutter for player 1 */
        Point point3 = new Point(29, 5);
        Building woodcutter0 = new Woodcutter(player1);
        map.placeBuilding(woodcutter0, point3);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);

        /* Verify that player 0 can't attack player 1's woodcutter */
        player0.attack(woodcutter0, 1);
    }

    @Test
    public void testMilitaryLeavesBarracksWhenAttackIsInitiated() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1, map);

        /* Verify that no militaries leave the barracks before the attack is 
         initiated */
        for (int i = 0; i < 100; i++) {
            for (Worker w : map.getWorkers()) {
                if (w instanceof Military) {
                    assertTrue(w.isInsideBuilding());
                }
            }

            map.stepTime();
        }

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Verify that a military leaves the barracks before the attack when 
         initiated */
        map.stepTime();

        List<Military> militaryOutside = Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0, map);

        assertEquals(militaryOutside.size(), 1);
        assertEquals(barracks0.getHostedMilitary(), 1);
    }

    @Test
    public void testAttackingMilitaryWalksToFlagOfAttackedBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);

        /* Verify that the attacker walks to the attacked building's flag */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
    }

    @Test
    public void testThatPlayerIsCorrectInChosenAttacker() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Connect player 0's barracks with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Wait for player 0's barracks to get populated */
        Utils.waitForMilitaryBuildingToGetPopulated(map, barracks0, 2);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);
        assertEquals(player0, headquarter0.getPlayer());

        /* Verify that the player is set correctly in the attacker */
        assertEquals(player0, attacker.getPlayer());
    }

    @Test
    public void testAttackerWinsEmptyBuildingDirectly() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);

        /* Wait for the attacker to get to the attacked buildings flag */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

        /* Verify that the attacker takes over the building */
        assertEquals(barracks1.getHostedMilitary(), 0);
        assertEquals(barracks1.getPlayer(), player1);
        assertEquals(attacker.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertTrue(attacker.isInsideBuilding());
        assertEquals(barracks1.getPlayer(), player0);
        assertEquals(barracks1.getHostedMilitary(), 1);
    }

    @Test
    public void testMilitaryLeavesAttackedBuildingToDefendAndMeetsAttacker() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when 
         the attacker reaches the flag */
        assertEquals(barracks1.getHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getHostedMilitary(), 0);

        /* Verify that the defender goes to the attacker */
        Military defender = Utils.findMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());
    }

    @Test
    public void testFightInDetail() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);
        assertFalse(attacker.isFighting());

        /* Wait for the military to reach the attacked building */
        assertEquals(barracks1.getHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getHostedMilitary(), 0);

        /* Wait for the defender to go to the attacker */
        Military defender = Utils.findMilitaryOutsideBuilding(player1, map);

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

        /* Verify that the militaries fight */
        assertTrue(attacker.isFighting());
        assertTrue(defender.isFighting());

        /* Wait for the fight to end */
        for (int i = 0; i < 200; i++) {

            /* Break when one of the militaries is gone */
            if (!map.getWorkers().contains(attacker) || !map.getWorkers().contains(defender)) {
                break;
            }

            /* Verify that the militaries stay in place */
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

        Utils.fastForwardUntilWorkerReachesPoint(map, winner, barracks1.getFlag().getPosition());

        assertEquals(winner.getPosition(), barracks1.getFlag().getPosition());
    }

    @Test
    public void testGeneralAttackerBeatsPrivateDefender() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when 
         the attacker reaches the flag */
        assertEquals(barracks1.getHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());
        assertFalse(barracks1.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getHostedMilitary(), 0);

        /* Get the defender */
        Military defender = Utils.findMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);

        /* Wait for the defender to go to the attacker */
        assertEquals(defender.getTarget(), attacker.getPosition());
        assertTrue(barracks1.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Verify that the militaries are fighting */
        assertTrue(barracks1.isUnderAttack());

        /* Verify that the general beats the private */
        Utils.waitForWorkerToDisappear(defender, map);

        assertFalse(map.getWorkers().contains(defender));
        assertTrue(map.getWorkers().contains(attacker));
        assertFalse(attacker.isFighting());
    }

    @Test
    public void testAttackerTakesOverBuildingAfterWinningFight() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when 
         the attacker reaches the flag */
        assertEquals(barracks1.getHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getHostedMilitary(), 0);

        /* Wait for the defender to go to the attacker */
        Military defender = Utils.findMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Wait for the general to beat the private */
        Utils.waitForWorkerToDisappear(defender, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Wait for the attacker to go back to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Verify that the attacker takes over the building */
        assertEquals(attacker.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertEquals(barracks1.getPlayer(), player0);
    }

    @Test
    public void testAttackersBorderIsUpdatedWhenItCapturesBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when 
         the attacker reaches the flag */
        assertEquals(barracks1.getHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getHostedMilitary(), 0);

        /* Wait for the defender to go to the attacker */
        Military defender = Utils.findMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Wait for the general to beat the private */
        Utils.waitForWorkerToDisappear(defender, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Verify that player 1's barracks is in player 1's border and not player 0's */
        Utils.verifyPointIsNotWithinBorder(map, player0, barracks1.getPosition());
        Utils.verifyPointIsWithinBorder(map, player1, barracks1.getPosition());

        /* Wait for the attacker to return to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Wait for the attacker to go to the barracks */
        assertEquals(attacker.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        /* Verify that the border is updated to include the captured building
         and that it's not in player 1's border anymore */
        Utils.verifyPointIsWithinBorder(map, player0, barracks1.getPosition());
        Utils.verifyPointIsNotWithinBorder(map, player1, barracks1.getPosition());
    }

    @Test
    public void testFieldOfViewIsUpdatedWhenAttackerTakesOverBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when 
         the attacker reaches the flag */
        assertEquals(barracks1.getHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getHostedMilitary(), 0);

        /* Wait for the defender to go to the attacker */
        Military defender = Utils.findMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Wait for the general to beat the private */
        Utils.waitForWorkerToDisappear(defender, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Wait for the attacker to return to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Verify that the field of view has not been updated yet */
        assertTrue(player0.getFieldOfView().contains(new Point(31, 5)));
        assertFalse(player0.getFieldOfView().contains(new Point(37, 5)));

        /* Wait for the attacker to go to the barracks */
        assertEquals(attacker.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        /* Verify that the field of view is updated when the attacker has taken
         over the barracks */
        assertFalse(player0.getFieldOfView().contains(new Point(31, 5)));
        assertTrue(player0.getFieldOfView().contains(new Point(39, 5)));
    }

    @Test
    public void testDefenderWinsAndGoesBackToBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks1, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when 
         the attacker reaches the flag */
        assertEquals(barracks1.getHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getHostedMilitary(), 0);

        /* Wait for the defender to go to the attacker */
        Military defender = Utils.findMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Wait for the defender's general to beat the attacker's private */
        Utils.waitForWorkerToDisappear(attacker, map);

        assertFalse(map.getWorkers().contains(attacker));

        /* Wait for the defender to return to the fixed point */
        assertEquals(defender.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, defender.getTarget());

        /* Verify that the defender goes back to its building and that the 
         building still belongs to player 1 */
        assertEquals(defender.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, barracks1.getPosition());

        assertTrue(defender.isInsideBuilding());
        assertEquals(barracks1.getHostedMilitary(), 1);
        assertEquals(player1, barracks1.getPlayer());
    }

    @Test
    public void testAttackerGoesBackWhenDefenderBurnsBuildingAfterVictory() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when 
         the attacker reaches the flag */
        assertEquals(barracks1.getHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getHostedMilitary(), 0);

        /* Wait for the defender to go to the attacker */
        Military defender = Utils.findMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Wait for the attacker's general to beat the defender's private */
        Utils.waitForWorkerToDisappear(defender, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Wait for the attacker to return to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Verify that the attacker goes back to its building if the defender
         destroyes the attacked building */
        assertEquals(attacker.getTarget(), barracks1.getPosition());

        map.stepTime();

        barracks1.tearDown();

        assertEquals(attacker.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertEquals(attacker.getTarget(), barracks0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks0.getPosition());

        assertTrue(attacker.isInsideBuilding());
        assertEquals(barracks0.getHostedMilitary(), 2);
        assertEquals(player0, barracks0.getPlayer());
    }

    @Test
    public void testAttackerFinishesFightAndGoesBackWhenDefenderBurnsBuildingBeforeVictory() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when 
         the attacker reaches the flag */
        assertEquals(barracks1.getHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getHostedMilitary(), 0);

        /* Wait for the defender to go to the attacker */
        Military defender = Utils.findMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Wait for the militaries to start fighting */
        Utils.waitForFightToStart(map, attacker, defender);

        /* Burn down the building after the militaries have started to fight */
        barracks1.tearDown();

        /* Verify that the militaries keep fighting */
        map.stepTime();

        assertTrue(attacker.isFighting());
        assertTrue(defender.isFighting());

        /* Wait for the attacker to win the fight */
        Utils.waitForWorkerToDisappear(defender, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Wait for the attacker to go back to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Verify that the attacker goes back to its building after the fight */
        assertEquals(attacker.getTarget(), barracks0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks0.getPosition());

        assertTrue(attacker.isInsideBuilding());
        assertEquals(barracks0.getHostedMilitary(), 2);
        assertEquals(player0, barracks0.getPlayer());
    }

    @Test
    public void testNextDefenderGoesOutWhenFirstDies() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Building headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when 
         the attacker reaches the flag */
        assertEquals(barracks1.getHostedMilitary(), 2);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getHostedMilitary(), 1);

        /* Wait for the defender to go to the attacker */
        Military defender = Utils.findMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Wait for the attacker to win the fight and verify that no additional 
           defender goes out from the barracks*/
        for (int i = 0; i < 1000; i++) {

            if (!map.getWorkers().contains(defender)) {
                break;
            }

            assertEquals(barracks1.getHostedMilitary(), 1);
            assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player1, map).size(), 1);

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(defender));

        /* Verify that a new defender goes out from the barracks */
        map.stepTime();

        Military nextDefender = Utils.waitForMilitaryOutsideBuilding(player1, map);

        assertNotNull(nextDefender);
        assertFalse(attacker.equals(nextDefender));

        /* Verify that the new defender starts fighting with the attacker */
        assertEquals(nextDefender.getTarget(), barracks1.getFlag().getPosition());

        Utils.waitForFightToStart(map, attacker, nextDefender);
    }

    @Test
    public void testAttackingGeneralBeatsSixButNotSevenDefendingPrivates() throws Exception {

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
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(49, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(31, 5);
        Building barracks1 = new Fortress(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 7, barracks1, map);

        /* Empty both headquarters for militaries */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0, map);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when 
         the attacker reaches the flag */
        assertEquals(barracks1.getHostedMilitary(), 7);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

        /* Verify that the attacking general beats six privates */
        assertTrue(attacker.getRank().equals(GENERAL_RANK));

        for (int i = 0; i < 6; i++) {

            /* Wait for the defender to go to the attacker */
            Military defender = Utils.waitForMilitaryOutsideBuilding(player1, map);

            assertNotNull(defender);
            assertEquals(defender.getTarget(), barracks1.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, defender, defender.getTarget());

            assertEquals(defender.getPosition(), attacker.getPosition());

            /* Wait for the attacker to win the fight */
            Utils.waitForWorkerToDisappear(defender, map);

            /* Verify that the attacker is still alive */
            assertTrue(map.getWorkers().contains(attacker));
        }

        /* Verify that the seventh defender beats the general */
        Military nextDefender = Utils.waitForMilitaryOutsideBuilding(player1, map);

        assertNotNull(nextDefender);
        assertFalse(attacker.equals(nextDefender));

        /* Wait for the fight to start */
        Utils.waitForFightToStart(map, attacker, nextDefender);

        /* Verify that the defender beats the attacker */
        Utils.waitForWorkerToDisappear(attacker, map);

        assertTrue(map.getWorkers().contains(nextDefender));
    }

    @Test
    public void testAttackingSergeantBeatsThreeButNotFourDefendingPrivates() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Headquarter headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(49, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(31, 5);
        Building barracks1 = new Fortress(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, 2, barracks0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 4, barracks1, map);

        /* Empty both headquarters for militaries */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0, map);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when 
         the attacker reaches the flag */
        assertEquals(barracks1.getHostedMilitary(), 4);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

        /* Verify that the attacking general beats three privates */
        assertTrue(attacker.getRank().equals(SERGEANT_RANK));

        for (int i = 0; i < 3; i++) {

            /* Wait for the defender to go to the attacker */
            Military defender = Utils.waitForMilitaryOutsideBuilding(player1, map);

            assertNotNull(defender);
            assertEquals(defender.getTarget(), barracks1.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, defender, defender.getTarget());

            assertEquals(defender.getPosition(), attacker.getPosition());

            /* Wait for the attacker to win the fight */
            Utils.waitForWorkerToDisappear(defender, map);

            /* Verify that the attacker is still alive */
            assertTrue(map.getWorkers().contains(attacker));
        }

        /* Verify that the seventh defender beats the sergeant */
        Military nextDefender = Utils.waitForMilitaryOutsideBuilding(player1, map);

        assertNotNull(nextDefender);
        assertFalse(attacker.equals(nextDefender));

        /* Wait for the defender to start fighting with the attacker */
        Utils.waitForFightToStart(map, attacker, nextDefender);

        /* Verify that the defender beats the attacker */
        Utils.waitForWorkerToDisappear(attacker, map);

        assertTrue(map.getWorkers().contains(nextDefender));
    }

    @Test
    public void testCanOrderAttackWithSeveralAttackersFromOneBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Headquarter headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(49, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building guardHouse0 = new GuardHouse(player0);
        map.placeBuilding(guardHouse0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(31, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(guardHouse0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's guard house */
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, 3, guardHouse0, map);

        assertEquals(guardHouse0.getHostedMilitary(), 3);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks1, map);

        /* Empty both headquarters for militaries */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0, map);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0, map);

        /* Order an attack */
        player0.attack(barracks1, 2);

        /* Verify that two militaries leave the guard house */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 2, player0, map);

        assertNotNull(attackers);
        assertEquals(attackers.size(), 2);
    }

    @Test
    public void testArrivalOfAttackWithSeveralAttackersAndOneDefender() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Headquarter headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(49, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building guardHouse0 = new GuardHouse(player0);
        map.placeBuilding(guardHouse0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(31, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(guardHouse0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's guard house */
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, 3, guardHouse0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1, map);

        /* Empty both headquarters for militaries */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0, map);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0, map);

        /* Order an attack */
        player0.attack(barracks1, 2);

        /* Wait for two militaries to leave the guard house */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 2, player0, map);

        assertNotNull(attackers);
        assertEquals(attackers.size(), 2);

        /* Verify that one attacker goes to the flag and the other attacker waits
         on an adjacent point */
        Military attacker1 = attackers.get(0);
        Military attacker2 = attackers.get(1);

        assertTrue(attacker1.getTarget().equals(barracks1.getFlag().getPosition())
                || attacker2.getTarget().equals(barracks1.getFlag().getPosition()));

        Point flagPoint = barracks1.getFlag().getPosition();

        assertTrue(flagPoint.isAdjacent(attacker1.getTarget()) || flagPoint.equals(attacker1.getTarget()));
        assertTrue(flagPoint.isAdjacent(attacker2.getTarget()) || flagPoint.equals(attacker2.getTarget()));
        assertFalse(attacker1.getTarget().equals(attacker2.getTarget()));
    }

    @Test
    public void testSurplusAttackersWaitUntilBuildingIsCapturedBeforeEntering() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Headquarter headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(49, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building guardHouse0 = new GuardHouse(player0);
        map.placeBuilding(guardHouse0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(31, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(guardHouse0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's guard house */
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, 3, guardHouse0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1, map);

        /* Empty both headquarters for militaries */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0, map);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0, map);

        /* Order an attack */
        player0.attack(barracks1, 2);

        /* Wait for two militaries to leave the guard house */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 2, player0, map);

        assertNotNull(attackers);
        assertEquals(attackers.size(), 2);

        /* Get the first attacker */
        Military firstAttacker = null;

        for (Military m : attackers) {
            if (m.getTarget().equals(barracks1.getFlag().getPosition())) {
                firstAttacker = m;

                break;
            }

            map.stepTime();
        }

        assertNotNull(firstAttacker);

        /* Wait for the first attacker to reach the attacked building */
        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, barracks1.getFlag().getPosition());

        /* Verify that one attacker waits until the other attacker wins 
         the fight before entering the building */
        Military defender = Utils.waitForMilitaryOutsideBuilding(player1, map);

        /* Get the waiting attacker */
        Military waitingAttacker = null;
        if (attackers.get(0).equals(firstAttacker)) {
            waitingAttacker = attackers.get(1);
        } else {
            waitingAttacker = attackers.get(0);
        }

        Point waitingPosition = waitingAttacker.getPosition();

        /* Wait for the fight to start */
        for (int i = 0; i < 1000; i++) {
            if (firstAttacker.isFighting() && defender.isFighting()) {
                break;
            }

            map.stepTime();
        }

        /* Make sure there are only two attackers */
        assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0, map).size(), 2);

        /* Wait for the fight to end and verify that the waiting attacker doesn't move */
        for (int i = 0; i < 1000; i++) {
            if (!map.getWorkers().contains(defender)) {
                break;
            }

            assertEquals(waitingAttacker.getPosition(), waitingPosition);

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(defender));

        /* Wait for the fighting attacker to go back to the flag */
        assertEquals(firstAttacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, firstAttacker.getTarget());

        /* Verify that only the active attacker enters the building */
        assertEquals(firstAttacker.getTarget(), barracks1.getPosition());
        assertEquals(firstAttacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getHostedMilitary(), 0);

        for (int i = 0; i < 100; i++) {
            if (firstAttacker.getPosition().equals(barracks1.getPosition())) {
                break;
            }

            assertTrue(waitingAttacker.getPosition().equals(waitingPosition));

            map.stepTime();
        }

        assertTrue(firstAttacker.getPosition().equals(barracks1.getPosition()));
        assertTrue(firstAttacker.isInsideBuilding());
        assertEquals(barracks1.getHostedMilitary(), 1);

        /* Verify that the waiting attacker enters the building */
        map.stepTime();

        assertEquals(waitingAttacker.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, waitingAttacker, barracks1.getPosition());

        assertEquals(waitingAttacker.getPosition(), barracks1.getPosition());
    }

    @Test
    public void testAttackersGoHomeAfterVictoryIfTheyDontFitInAttackedBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Headquarter headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(49, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building watchTower0 = new WatchTower(player0);
        map.placeBuilding(watchTower0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(31, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(watchTower0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1, map);

        /* Populate player 0's guard house */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 4, watchTower0, map);

        /* Empty both headquarters for militaries */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0, map);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0, map);

        /* Verify that the attackers-to-be are in the watchtower */
        assertEquals(watchTower0.getHostedMilitary(), 4);
        assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0, map).size(), 0);

        /* Order an attack */
        player0.attack(barracks1, 3);

        /* Wait for two militaries to leave the watch tower */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 3, player0, map);

        assertNotNull(attackers);
        assertEquals(attackers.size(), 3);

        /* Get the first attacker */
        Military firstAttacker = null;

        for (Military m : attackers) {
            if (m.getTarget().equals(barracks1.getFlag().getPosition())) {
                firstAttacker = m;
            }
        }

        assertNotNull(firstAttacker);

        /* Wait for the first attacker to reach its position */
        assertEquals(firstAttacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, barracks1.getFlag().getPosition());

        /* Get the defender */
        Military defender = Utils.waitForMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);

        /* Wait for the fight to end */
        Utils.waitForWorkerToDisappear(defender, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Wait for the attacker to go back to the fixed point */
        assertEquals(firstAttacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, firstAttacker.getTarget());

        /* Wait for the active attacker to enter the building and verify that
         the two other attackers wait */
        assertEquals(firstAttacker.getTarget(), barracks1.getPosition());

        attackers.remove(firstAttacker);

        for (int i = 0; i < 100; i++) {
            if (firstAttacker.getPosition().equals(barracks1.getPosition())) {
                break;
            }

            for (Worker m : attackers) {
                assertFalse(m.getPosition().equals(barracks1.getPosition()));
            }

            map.stepTime();
        }

        assertEquals(firstAttacker.getPosition(), barracks1.getPosition());
        assertTrue(firstAttacker.isInsideBuilding());

        /* Verify that the captured barracks is manned correctly by the winner */
        assertEquals(barracks1.getHostedMilitary(), 1);

        /* Verify that the second military enters the captured barracks and the
         third military returns to the watch tower */
        Military remainingAttacker1 = attackers.get(0);
        Military remainingAttacker2 = attackers.get(1);

        map.stepTime();

        assertTrue(remainingAttacker1.getTarget().equals(barracks1.getPosition())
                || remainingAttacker2.getTarget().equals(barracks1.getPosition()));
        assertTrue(remainingAttacker1.getTarget().equals(watchTower0.getPosition())
                || remainingAttacker2.getTarget().equals(watchTower0.getPosition()));

        /* Verify that the remaining attackers go to their respective targets */
        for (int i = 0; i < 1000; i++) {
            if (remainingAttacker1.getPosition().equals(remainingAttacker1.getTarget())
                    && remainingAttacker2.getPosition().equals(remainingAttacker2.getTarget())) {
                break;
            }

            map.stepTime();
        }

        assertTrue(remainingAttacker1.getPosition().equals(barracks1.getPosition())
                || remainingAttacker2.getPosition().equals(barracks1.getPosition()));
        assertTrue(remainingAttacker1.getPosition().equals(watchTower0.getPosition())
                || remainingAttacker2.getPosition().equals(watchTower0.getPosition()));
    }

    @Test
    public void testAttackersGoesToStorageAfterVictoryIfOtherBuildingsAreFull() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Headquarter headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(49, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building watchTower0 = new WatchTower(player0);
        map.placeBuilding(watchTower0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(31, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(watchTower0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1, map);

        /* Populate player 0's guard house */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 6, watchTower0, map);

        /* Empty both headquarters for militaries */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0, map);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0, map);

        /* Verify that the attackers-to-be are in the watchtower */
        assertEquals(watchTower0.getHostedMilitary(), 6);
        assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0, map).size(), 0);

        /* Order an attack */
        player0.attack(barracks1, 3);

        /* Wait for two militaries to leave the guard house */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 3, player0, map);

        /* Wait for the first attacker to reach its position */
        Military firstAttacker = null;

        for (Military m : attackers) {
            if (m.getTarget().equals(barracks1.getFlag().getPosition())) {
                firstAttacker = m;

                break;
            }
        }

        assertNotNull(firstAttacker);

        /* Find the defender */
        Military defender = Utils.waitForMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);

        /* Fill the watchtower so there is no space there for returning militaries */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 3, watchTower0, map);

        assertFalse(watchTower0.needsMilitaryManning());
        assertEquals(watchTower0.getHostedMilitary(), 6);

        /* Wait for the fight to end */
        Utils.waitForWorkerToDisappear(defender, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Wait for the active attacker to go back to the flag */
        assertEquals(firstAttacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, firstAttacker.getTarget());

        /* Wait for the active attacker to enter the building and verify that
         the two other attackers wait */
        assertEquals(firstAttacker.getTarget(), barracks1.getPosition());

        attackers.remove(firstAttacker);

        for (int i = 0; i < 100; i++) {
            if (firstAttacker.getPosition().equals(barracks1.getPosition())) {
                break;
            }

            for (Worker m : attackers) {
                assertFalse(m.getPosition().equals(barracks1.getPosition()));
            }

            map.stepTime();
        }

        assertEquals(firstAttacker.getPosition(), barracks1.getPosition());
        assertTrue(firstAttacker.isInsideBuilding());

        /* Verify that the captured barracks is manned correctly by the winner */
        assertEquals(barracks1.getHostedMilitary(), 1);

        /* Verify that the second military enters the captured barracks and the
         third military returns to the storage since the watch tower is full */
        Military remainingAttacker1 = attackers.get(0);
        Military remainingAttacker2 = attackers.get(1);

        map.stepTime();

        assertTrue(remainingAttacker1.getTarget().equals(barracks1.getPosition())
                || remainingAttacker2.getTarget().equals(barracks1.getPosition()));
        assertTrue(remainingAttacker1.getTarget().equals(headquarter0.getPosition())
                || remainingAttacker2.getTarget().equals(headquarter0.getPosition()));

        /* Verify that the remaining attackers go to their respective targets */
        for (int i = 0; i < 1000; i++) {
            if (remainingAttacker1.getPosition().equals(remainingAttacker1.getTarget())
                    && remainingAttacker2.getPosition().equals(remainingAttacker2.getTarget())) {
                break;
            }

            map.stepTime();
        }

        assertTrue(remainingAttacker1.getPosition().equals(barracks1.getPosition())
                || remainingAttacker2.getPosition().equals(barracks1.getPosition()));
        assertTrue(remainingAttacker1.getPosition().equals(headquarter0.getPosition())
                || remainingAttacker2.getPosition().equals(headquarter0.getPosition()));
    }

    @Test
    public void testDriveWayRemainsWhenBuildingIsCaptured() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Headquarter headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(49, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place watch tower for player 0 */
        Point point2 = new Point(21, 5);
        Building watchTower0 = new WatchTower(player0);
        map.placeBuilding(watchTower0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(31, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Finish construction */
        Utils.constructHouse(watchTower0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1, map);

        /* Populate player 0's watch tower */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 6, watchTower0, map);

        /* Empty both headquarters for militaries */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0, map);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0, map);

        /* Verify that the attackers-to-be are in the watchtower */
        assertEquals(watchTower0.getHostedMilitary(), 6);
        assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0, map).size(), 0);

        /* Order an attack */
        player0.attack(barracks1, 3);

        /* Wait for two militaries to leave the watch tower */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 3, player0, map);

        /* Get the first attacker */
        Military firstAttacker = null;

        for (Military m : attackers) {
            if (m.getTarget().equals(barracks1.getFlag().getPosition())) {
                firstAttacker = m;
            }
        }

        assertNotNull(firstAttacker);

        /* Wait for the first attacker to reach its positions */
        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, firstAttacker.getTarget());

        /* Find the defender */
        Military defender = Utils.waitForMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);

        /* Wait for the fight to end */
        Utils.waitForWorkerToDisappear(defender, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Wait for the active attacker to go back to the flag */
        assertEquals(firstAttacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, firstAttacker.getTarget());

        /* Wait for the active attacker to enter the building  */
        assertEquals(firstAttacker.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, barracks1.getPosition());

        /* Verify that the road of the captured barracks is not removed */
        assertNotNull(map.getRoad(barracks1.getPosition(), barracks1.getFlag().getPosition()));
    }

    @Test
    public void testReinforcementReturnsWhenBuildingIsCaptured() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Headquarter headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(49, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(31, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Place flag */
        Point point4 = new Point(61, 5);
        Flag flag0 = map.placeFlag(player1, point4);

        /* Connect the flag with the barracks */
        Road road0 = map.placeAutoSelectedRoad(player1, flag0, barracks1.getFlag());

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1, map);

        /* Empty both headquarters for militaries */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0, map);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Wait for the attacker to walk to the attacked building */
        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Wait for defender to leave the attacked building */
        Military defender = Utils.findMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);

        /* Send a reinforcement to the attacked building */
        Military reinforcement = new Military(player1, PRIVATE_RANK, map);

        map.placeWorker(reinforcement, flag0);

        reinforcement.setTargetBuilding(barracks1);

        assertEquals(reinforcement.getTarget(), barracks1.getPosition());

        /* Verify that the reinforcement returns to the storage instead of entering the barracks */
        Utils.waitForWorkerToDisappear(defender, map);

        /* Wait for the attacker to go back to the flag */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Wait for the attacker to take over the barracks */
        assertEquals(attacker.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        /* Wait for the reinforcement to reach the next point */
        for (int i = 0; i < 100; i++) {

            if (reinforcement.isExactlyAtPoint()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(reinforcement.isExactlyAtPoint());

        /* Verify that the defender goes back to the headquarter */
        assertEquals(reinforcement.getTarget(), headquarter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, reinforcement, headquarter1.getPosition());

        assertEquals(headquarter1.getAmount(PRIVATE), 1);
    }

    @Test
    public void testReinforcementReturnsWhenBuildingIsTornDown() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Headquarter headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(49, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(31, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Place flag */
        Point point4 = new Point(61, 5);
        Flag flag0 = map.placeFlag(player1, point4);

        /* Connect the flag with the barracks */
        Road road0 = map.placeAutoSelectedRoad(player1, flag0, barracks1.getFlag());

        /* Finish construction */
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1, map);

        /* Empty both headquarters for militaries */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0, map);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Wait for the attacker to walk to the attacked building */
        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Wait for defender to leave the attacked building */
        Military defender = Utils.findMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);

        /* Send a reinforcement to the attacked building */
        Military reinforcement = new Military(player1, PRIVATE_RANK, map);

        map.placeWorker(reinforcement, flag0);

        reinforcement.setTargetBuilding(barracks1);

        assertEquals(reinforcement.getTarget(), barracks1.getPosition());

        /* Verify that the reinforcement returns to the storage instead of entering the barracks */
        Utils.waitForWorkerToDisappear(defender, map);

        /* Destroy the barracks to make the reinforcement return to the storage */
        barracks1.tearDown();

        /* Wait for the reinforcement to reach the next point */
        for (int i = 0; i < 100; i++) {

            if (reinforcement.isExactlyAtPoint()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(reinforcement.isExactlyAtPoint());

        /* Verify that the defender goes back to the headquarter */
        assertEquals(reinforcement.getTarget(), headquarter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, reinforcement, headquarter1.getPosition());

        assertEquals(headquarter1.getAmount(PRIVATE), 1);
    }

    @Test
    public void testReinforcementJoinsDefenseWhenDefenderDies() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Headquarter headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(49, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building guardHouse = new GuardHouse(player0);
        map.placeBuilding(guardHouse, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(31, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Place flag */
        Point point4 = new Point(61, 5);
        Flag flag0 = map.placeFlag(player1, point4);

        /* Connect the flag with the barracks */
        Road road0 = map.placeAutoSelectedRoad(player1, flag0, barracks1.getFlag());

        /* Finish construction */
        Utils.constructHouse(guardHouse, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's guard house */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 3, guardHouse, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1, map);

        /* Empty both headquarters for militaries */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0, map);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0, map);

        /* Order an attack */
        player0.attack(barracks1, 2);

        /* Find the military that was chosen to attack */
        map.stepTime();

        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 2, player0, map);

        assertNotNull(attackers);
        assertEquals(attackers.size(), 2);

        /* Wait for the attackers to walk to the attacked building */
        Utils.fastForwardUntilWorkerReachesPoint(map, attackers.get(0), attackers.get(0).getTarget());

        /* Wait for defender to leave the attacked building */
        Military defender = Utils.findMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);

        /* Send a reinforcement to the attacked building */
        Military reinforcement = new Military(player1, PRIVATE_RANK, map);

        map.placeWorker(reinforcement, barracks1.getFlag());

        reinforcement.setTargetBuilding(barracks1);

        /* Wait for the reinforcement to go to the building */
        assertEquals(reinforcement.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, reinforcement, barracks1.getPosition());

        /* Verify that the reinforcement stays in the building when the active defender is alive */
        for (int i = 0; i < 1000; i++) {

            if (!map.getWorkers().contains(defender)) {
                break;
            }

            assertEquals(barracks1.getHostedMilitary(), 1);
            assertEquals(reinforcement.getPosition(), barracks1.getPosition());
            assertTrue(reinforcement.isInsideBuilding());

            map.stepTime();
        }

        /* Verify that the reinforcement joins the defense */
        for (int i = 0; i < 10; i++) {
            if (reinforcement.getTarget().equals(barracks1.getFlag().getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertTrue(reinforcement.getTarget().equals(barracks1.getFlag().getPosition()));
    }

    @Test
    public void testAdditionalAttackersWaitForMainAttacker() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Headquarter headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(49, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = new Fortress(player0);
        map.placeBuilding(fortress0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(31, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

        /* Place flag */
        Point point4 = new Point(61, 5);
        Flag flag0 = map.placeFlag(player1, point4);

        /* Connect the flag with the barracks */
        Road road0 = map.placeAutoSelectedRoad(player1, flag0, barracks1.getFlag());

        /* Finish construction */
        Utils.constructHouse(fortress0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 9, fortress0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks1, map);

        /* Empty both headquarters for militaries */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0, map);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0, map);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0, map);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0, map);

        /* Order an attack */
        player0.attack(barracks1, 8);

        /* Find the military that was chosen to attack */
        map.stepTime();

        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 8, player0, map);

        assertNotNull(attackers);
        assertEquals(attackers.size(), 8);

        /* Get the first attacker to fight */
        Military fightingAttacker = null;

        for (Military m : attackers) {
            if (m.getTarget().equals(barracks1.getFlag().getPosition())) {
                fightingAttacker = m;

                break;
            }

            map.stepTime();
        }

        assertNotNull(fightingAttacker);

        /* Wait for the fighting attacker to walk to the attacked building */
        assertEquals(fightingAttacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fightingAttacker, fightingAttacker.getTarget());

        /* Wait for defender to leave the attacked building */
        Military defender = Utils.waitForMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);

        /* Verify that the defender goes to the flag */
        assertEquals(defender.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, barracks1.getFlag().getPosition());

        /* Get the other attackers' positions */
        Map<Military, Point> positions = new HashMap<>();

        for (Military m : attackers) {
            if (m.equals(fightingAttacker)) {
                continue;
            }

            positions.put(m, m.getPosition());
        }

        /* Wait for the fight to start */
        Utils.waitForFightToStart(map, fightingAttacker, defender);

        /* Verify that the other attackers wait and don't start fighting */
        for (int i = 0; i < 1000; i++) {

            if (!map.getWorkers().contains(defender)) {
                break;
            }

            assertTrue(fightingAttacker.isFighting());

            /* Verify that the other attackers are not fighting */
            for (Military m : attackers) {
                if (m.equals(fightingAttacker)) {
                    continue;
                }

                assertFalse(m.isFighting());
                assertEquals(m.getPosition(), positions.get(m));
            }

            /* Verify that no more defenders leave the barracks */
            assertEquals(barracks1.getHostedMilitary(), 1);
            assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player1, map).size(), 1);

            map.stepTime();
        }
    }

    // Test:
    //  - Test all points that can be attacked are within the FOV (not the case today?)
    //  - Winning private meets new private and loses
    //    (what happens if this is before the fight is done?)    
    //  - Test several militaries can defend
    //  - Test militaries rally from several buildings
    //  - Test no attack possible with only one military in the building
    //  - Test that the attacked building gets filled fully
    //    go back to their original buildings
    //  - Test that promised militaries walking to the building return home when
    //    it is captured
}
