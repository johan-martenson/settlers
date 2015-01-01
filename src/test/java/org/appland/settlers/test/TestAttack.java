/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import static java.awt.Color.GREEN;
import java.util.LinkedList;
import java.util.List;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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

    @Test (expected = Exception.class)
    public void testNoAvailableAttackersForNonMilitaryBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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

    @Test (expected = Exception.class)
    public void testNoAvailableAttackersForOwnBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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

    @Test (expected = Exception.class)
    public void testPlayerCannotAttackHimself() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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

    @Test (expected = Exception.class)
    public void testCannotAttackNonMilitaryBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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

        List<Worker> militaryOutside = Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0, map);

        assertEquals(militaryOutside.size(), 1);
    }

    @Test
    public void testAttackingMilitaryWalksToFlagOfAttackedBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
    public void testAttackerWinsEmptyBuidingDirectly() throws Exception {
        
        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
    }

    @Test
    public void testMilitaryLeavesAttackedBuildingToDefendAndMeetsAttacker() throws Exception {
        
        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
    public void testGeneralAttackerBeatsPrivateDefender() throws Exception {
        
        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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

        /* Verify that the general beats the private */
        for (int i = 0; i < 200; i++) {
            if (!map.getWorkers().contains(defender)) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(defender));
    }

    @Test
    public void testAttackerTakesOverBuildingAfterWinningFight() throws Exception {
        
        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        for (int i = 0; i < 500; i++) {
            if (!map.getWorkers().contains(defender)) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(defender));

        /* Verify that the attacker takes over the building */
        assertEquals(attacker.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertEquals(barracks1.getPlayer(), player0);
    }

    @Test
    public void testAttackersBorderIsUpdatedWhenItCapturesBuilding() throws Exception {
        
        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        for (int i = 0; i < 500; i++) {
            if (!map.getWorkers().contains(defender)) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(defender));

        /* Verify that player 1's barracks is in player 1's border and not player 0's */
        Utils.verifyPointIsNotWithinBorder(map, player0, barracks1.getPosition());
        Utils.verifyPointIsWithinBorder(map, player1, barracks1.getPosition());

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        for (int i = 0; i < 500; i++) {
            if (!map.getWorkers().contains(defender)) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(defender));

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        for (int i = 0; i < 500; i++) {
            if (!map.getWorkers().contains(attacker)) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(attacker));

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        for (int i = 0; i < 500; i++) {
            if (!map.getWorkers().contains(defender)) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(defender));

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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

        /* Burn down the building after the militaries have started to fight */
        map.stepTime();

        assertTrue(attacker.isFighting());
        assertTrue(defender.isFighting());

        barracks1.tearDown();

        /* Verify that the militaries keep fighting */
        map.stepTime();

        assertTrue(attacker.isFighting());
        assertTrue(defender.isFighting());

        /* Wait for the attacker to win the fight */
        for (int i = 0; i < 500; i++) {
            if (!map.getWorkers().contains(defender)) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(defender));

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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

        /* Wait for the attacker to win the fight */
        for (int i = 0; i < 500; i++) {
            if (!map.getWorkers().contains(defender)) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(defender));

        /* Verify that a new defender goes out from the barracks */
        map.stepTime();

        Military nextDefender = Utils.findMilitaryOutsideBuilding(player1, map);

        assertNotNull(nextDefender);
        assertFalse(attacker.equals(nextDefender));

        /* Verify that the new defender starts fighting with the attacker */
        assertEquals(nextDefender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, nextDefender, attacker.getPosition());

        assertTrue(nextDefender.isFighting());
    }

    @Test
    public void testAttackingGeneralBeatsSixButNotSevenDefendingPrivates() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Point point3 = new Point(29, 5);
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
            assertEquals(defender.getTarget(), attacker.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

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

        /* Wait for the defender to start fighting with the attacker */
        assertEquals(nextDefender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, nextDefender, attacker.getPosition());

        assertTrue(nextDefender.isFighting());

        /* Verify that the defender beats the attacker */
        Utils.waitForWorkerToDisappear(attacker, map);

        assertTrue(map.getWorkers().contains(nextDefender));
    }

    @Test
    public void testAttackingSergeantBeatsThreeButNotFourDefendingPrivates() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Point point3 = new Point(29, 5);
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
            assertEquals(defender.getTarget(), attacker.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

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
        assertEquals(nextDefender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, nextDefender, attacker.getPosition());

        assertTrue(nextDefender.isFighting());

        /* Verify that the defender beats the attacker */
        Utils.waitForWorkerToDisappear(attacker, map);

        assertTrue(map.getWorkers().contains(nextDefender));
    }

    @Test
    public void testCanOrderAttackWithSeveralAttackersFromOneBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        List<Worker> attackers = null;

        for (int i = 0; i < 1000; i++) {

            attackers = Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0, map);

            if (attackers.size() == 2) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(attackers);
        assertEquals(attackers.size(), 2);
    }

    @Test
    public void testArrivalOfAttackWithSeveralAttackersAndOneDefender() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        List<Worker> attackers = null;

        for (int i = 0; i < 1000; i++) {

            attackers = Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0, map);

            if (attackers.size() == 2) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(attackers);
        assertEquals(attackers.size(), 2);

        /* Verify that one attacker goes to the flag and the other attacker waits
           on an adjacent point */
        Military attacker1 = (Military)attackers.get(0);
        Military attacker2 = (Military)attackers.get(1);

        assertTrue(attacker1.getTarget().equals(barracks1.getFlag().getPosition()) ||
                   attacker2.getTarget().equals(barracks1.getFlag().getPosition()));

        Point flagPoint = barracks1.getFlag().getPosition();

        assertTrue(flagPoint.isAdjacent(attacker1.getTarget()) || flagPoint.equals(attacker1.getTarget()));
        assertTrue(flagPoint.isAdjacent(attacker2.getTarget()) || flagPoint.equals(attacker2.getTarget()));
        assertFalse(attacker1.getTarget().equals(attacker2.getTarget()));
    }
    // Test:
    //  - Test all points that can be attacked are within the FOV (not the case today?)
    //  - Winning private meets new private and loses
    //    (what happens if this is before the fight is done?)    
    //  - Test several militaries can attack and defend
    //  - Test militaries rally from several buildings
    //  - Test one general beats six privates, not seven when defending
    //  - Test no attack possible with only one military in the building
    //  - Test attacking with more militaries than the attacked building can hold
    //    and verify that it gets filled fully and that the remaining militaries 
    //    go back to their original buildings
}
