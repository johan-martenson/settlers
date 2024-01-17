/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GuardHouse;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.WatchTower;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.WoodcutterWorker;
import org.appland.settlers.model.Worker;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.OFFICER_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_FIRST_CLASS_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Military.Rank.SERGEANT_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author johan
 */
public class TestAttack {

    /**
     * Todo:
     *  - Test attack using only soldiers from the headquarters
     *  - Test attackers waiting can't stand on buildings
     */

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
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter far away from player 0 */
        Point point1 = new Point(90, 90);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Verify that there are no available attackers for each player to attack the other */
        assertFalse(player0.canAttack(headquarter1));
        assertFalse(player1.canAttack(headquarter0));
        assertEquals(player0.getAvailableAttackersForBuilding(headquarter1), 0);
        assertEquals(player1.getAvailableAttackersForBuilding(headquarter0), 0);
    }

    @Test
    public void testNonMilitaryBuildingDoesNotContributeAvailableAttackers() throws Exception {

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
        Point point1 = new Point(29, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place woodcutter */
        Point point2 = new Point(13, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);

        /* Construct the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Occupy the woodcutter */
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        /* Verify that there are no available attackers for each player 0 to attack player 1 */
        assertFalse(player0.canAttack(headquarter1));
        assertEquals(player0.getAvailableAttackersForBuilding(headquarter1), 0);
    }

    @Test
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
        Point point0 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter far away from player 0 */
        Point point1 = new Point(90, 90);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place woodcutter for player 1 */
        Point point2 = new Point(80, 90);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player1), point2);

        /* Verify that get available attackers can not be called for non-military building */
        assertFalse(player0.canAttack(woodcutter0));

        try {
            player0.getAvailableAttackersForBuilding(woodcutter0);

            fail();
        } catch (Exception e) {}
    }

    @Test
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
        Point point0 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter far away from player 0 */
        Point point1 = new Point(90, 90);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(15, 15);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Verify that get available attackers can not be called for own building */
        assertFalse(player0.canAttack(barracks0));

        try {
            player0.getAvailableAttackersForBuilding(barracks0);

            fail();
        } catch (Exception e) {}
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

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters far away from player 0 */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventory */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Verify that there are available attackers for player 0 to attack player 1's barracks */
        assertTrue(player0.canAttack(barracks1));
        assertEquals(player0.getAvailableAttackersForBuilding(barracks1), 1);
    }

    @Test
    public void testCannotAttackUnoccupiedBarracks() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters far away from player 0 */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from both headquarters */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);

        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Verify that it's not possible to attack the unoccupied barracks */
        assertTrue(barracks1.isReady());
        assertFalse(barracks1.isOccupied());
        assertFalse(player0.canAttack(barracks1));

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        assertTrue(player0.canAttack(barracks1));
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

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters far away from player 0 */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventory */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(21, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(guardHouse0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 3, guardHouse0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        /* Verify that there are available attackers for player 0 to attack player 1's barracks */
        assertTrue(player0.canAttack(barracks1));
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
        Point point0 = new Point(9, 5);
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
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Verify that player 0 can attack player 1's barracks */
        assertTrue(barracks1.isOccupied());
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);
    }

    @Test
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
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(13, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction of player 0's barracks */
        Utils.constructHouse(barracks0);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Verify that player 0 can't attack its own barracks */
        assertFalse(player0.canAttack(barracks0));

        try {
            player0.attack(barracks0, 1, AttackStrength.STRONG);

            fail();
        } catch (Exception e) {}
    }

    @Test
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
        Point point0 = new Point(15, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place woodcutter for player 1 */
        Point point3 = new Point(29, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player1), point3);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Finish construction of player0's barracks */
        Utils.constructHouse(barracks0);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Verify that player 0 can't attack player 1's woodcutter */
        assertTrue(woodcutter0.isReady());
        assertFalse(player0.canAttack(woodcutter0));

        try {
            player0.attack(woodcutter0, 1, AttackStrength.STRONG);

            fail();
        } catch (Exception e) {}
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

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);

        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Verify that no soldiers leave the barracks before the attack is initiated */
        for (int i = 0; i < 100; i++) {
            for (Worker worker : map.getWorkers()) {
                if (worker instanceof Military) {
                    assertTrue(worker.isInsideBuilding());
                }
            }

            map.stepTime();
        }

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Verify that a military leaves the barracks before the attack when initiated */
        map.stepTime();

        List<Military> militaryOutside = Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0);

        assertEquals(militaryOutside.size(), 1);
        assertEquals(barracks0.getNumberOfHostedMilitary(), 1);
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
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);

        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

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
        Point point0 = new Point(9, 5);
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

        /* Connect player 0's barracks with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Wait for player 0's barracks to get populated */
        Utils.waitForMilitaryBuildingToGetPopulated(barracks0, 2);

        /* Occupy player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

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
        Point point0 = new Point(9, 5);
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
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        /* Empty barracks 1 */
        Road road0 = map.placeAutoSelectedRoad(player1, barracks1.getFlag(), headquarter1.getFlag());

        barracks1.evacuate();

        Military military = Utils.waitForMilitaryOutsideBuilding(player1);

        assertEquals(military.getTarget(), headquarter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, military, headquarter1.getPosition());

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);

        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);

        /* Wait for the attacker to get to the attacked buildings flag */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

        /* Verify that the attacker takes over the building */
        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);
        assertEquals(barracks1.getPlayer(), player1);
        assertEquals(attacker.getTarget(), barracks1.getPosition());
        assertTrue(barracks1.isReady());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertEquals(barracks1.getPlayer(), player0);
        assertTrue(barracks1.getHostedMilitary().contains(attacker));
        assertTrue(attacker.isInsideBuilding());
        assertEquals(barracks1.getPlayer(), player0);
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
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
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

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

        /* Verify that the defender goes to the attacker */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());
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
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());
        assertFalse(barracks1.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);

        /* Get the defender */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertNotNull(defender);

        /* Wait for the defender to go to the attacker */
        assertEquals(defender.getTarget(), attacker.getPosition());
        assertTrue(barracks1.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());
        assertEquals(defender.getRank(), PRIVATE_RANK);
        assertEquals(attacker.getRank(), GENERAL_RANK);

        /* Verify that the soldiers are fighting */
        assertTrue(barracks1.isUnderAttack());

        /* Verify that the general beats the private */
        Utils.waitForFightToStart(map, attacker, defender);

        Utils.waitForSoldierToWinFight(attacker, map);

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

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Remove the soldiers from the inventory of both headquarters */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

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
        Utils.waitForSoldierToBeDying(defender, map);

        Utils.waitForSoldierToWinFight(attacker, map);

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

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Remove all soldiers from both headquarters */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

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
        Utils.waitForFightToStart(map, attacker, defender);

        Utils.waitForSoldierToWinFight(attacker, map);

        assertTrue(defender.isDying() || defender.isDead());

        /* Verify that player 1's barracks is in player 1's border and not player 0's */
        Utils.verifyPointIsNotWithinBorder(player0, barracks1.getPosition());
        Utils.verifyPointIsWithinBorder(player1, barracks1.getPosition());

        /* Wait for the attacker to return to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Wait for the attacker to go to the barracks */
        assertEquals(attacker.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        /* Verify that the border is updated to include the captured building and that it's not in player 1's border anymore */
        Utils.verifyPointIsWithinBorder(player0, barracks1.getPosition());
        Utils.verifyPointIsNotWithinBorder(player1, barracks1.getPosition());
        assertEquals(player0.getPlayerAtPoint(barracks1.getPosition()), player0);
    }

    @Test
    public void testDiscoveredLandIsUpdatedWhenAttackerTakesOverBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from both headquarters */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

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
        assertEquals(defender.getRank(), PRIVATE_RANK);
        assertEquals(attacker.getRank(), GENERAL_RANK);
        assertEquals(defender.getPlayer(), player1);
        assertEquals(attacker.getPlayer(), player0);

        Utils.waitForFightToStart(map, attacker, defender);

        Utils.waitForSoldierToWinFight(attacker, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Wait for the attacker to return to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Verify that the field of view has not been updated yet */
        Point point4 = new Point(22, 18);
        Point point5 = new Point(35, 27);

        assertTrue(player0.getDiscoveredLand().contains(point4));
        assertFalse(player0.getDiscoveredLand().contains(point5));

        /* Wait for the attacker to go to the barracks */
        assertEquals(attacker.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        /* Verify that the discovered land is updated when the attacker has taken over the barracks */
        assertTrue(barracks1.isOccupied());
        assertTrue(player0.getDiscoveredLand().contains(point5));
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

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

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

        /* Wait for the defender's general to beat the attacker's private */
        Utils.waitForFightToStart(map, attacker, defender);

        Utils.waitForSoldierToWinFight(defender, map);

        /* Wait for the defender to return to the fixed point */
        assertEquals(defender.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, defender.getTarget());

        /* Verify that the defender goes back to its building and that the building still belongs to player 1 */
        assertEquals(defender.getTarget(), barracks1.getPosition());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, barracks1.getPosition());

        assertTrue(defender.isInsideBuilding());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
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

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Remove all soldiers from the headquarters */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, SERGEANT);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, SERGEANT);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

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

        /* Wait for the attacker's general to beat the defender's private */
        Utils.waitForSoldierToBeDying(defender, map);

        /* Verify that the attacker goes back to its building if the defender destroys the attacked building */
        Utils.waitForWorkerToHaveTarget(map, attacker, barracks1.getPosition());

        map.stepTime();

        barracks1.tearDown();

        assertEquals(attacker.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertEquals(attacker.getTarget(), barracks0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks0.getPosition());

        assertTrue(attacker.isInsideBuilding());
        assertEquals(barracks0.getNumberOfHostedMilitary(), 2);
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

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventory */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

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

        /* Wait for the soldiers to start fighting */
        Utils.waitForFightToStart(map, attacker, defender);

        /* Burn down the building after the soldiers have started to fight */
        barracks1.tearDown();

        /* Verify that the soldiers keep fighting */
        map.stepTime();

        assertTrue(attacker.isFighting());
        assertTrue(defender.isFighting());

        /* Wait for the attacker to win the fight */
        Utils.waitForSoldierToWinFight(attacker, map);

        /* Wait for the attacker to go back to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Verify that the attacker goes back to its building after the fight */
        assertEquals(attacker.getTarget(), barracks0.getPosition());
        assertEquals(barracks0.getNumberOfHostedMilitary(), 1);

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks0.getPosition());

        assertTrue(attacker.isInsideBuilding());
        assertEquals(barracks0.getNumberOfHostedMilitary(), 2);
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
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(barracks1.getNumberOfHostedMilitary(), 2);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);

        /* Wait for the defender to go to the attacker */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Wait for the attacker to win the fight and verify that no additional defender goes out from the barracks */
        for (int i = 0; i < 1000; i++) {

            if (!map.getWorkers().contains(defender)) {
                break;
            }

            assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
            assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player1).size(), 1);

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(defender));

        /* Verify that a new defender goes out from the barracks */
        map.stepTime();

        Military nextDefender = Utils.waitForMilitaryOutsideBuilding(player1);

        assertNotNull(nextDefender);
        assertNotEquals(attacker, nextDefender);

        /* Verify that the new defender starts fighting with the attacker */
        assertEquals(nextDefender.getTarget(), barracks1.getFlag().getPosition());

        Utils.waitForFightToStart(map, attacker, nextDefender);
    }

    @Ignore
    @Test
    public void testAttackingPrivateFirstClassBeatsThreeButNotFourDefendingPrivates() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building fortress0 = map.placeBuilding(new Fortress(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(fortress0);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_FIRST_CLASS_RANK, 2, barracks0);

        /* Populate player 1's barracks */
        assertTrue(fortress0.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 7, fortress0);

        /* Empty both headquarters for soldiers */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Order an attack */
        assertTrue(player0.canAttack(fortress0));

        player0.attack(fortress0, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(fortress0.getNumberOfHostedMilitary(), 7);
        assertEquals(attacker.getTarget(), fortress0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, fortress0.getFlag().getPosition());

        assertEquals(attacker.getPosition(), fortress0.getFlag().getPosition());

        /* Verify that the attacking corporal beats three privates */
        assertEquals(attacker.getRank(), PRIVATE_FIRST_CLASS_RANK);

        for (int i = 0; i < 3; i++) {

            /* Wait for the defender to go to the attacker */
            Military defender = Utils.waitForSoldierNotDyingOutsideBuilding(player1);

            assertEquals(attacker.getPosition(), fortress0.getFlag().getPosition());
            assertNotNull(defender);
            assertFalse(defender.isDying());
            assertEquals(defender.getTarget(), fortress0.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, defender, defender.getTarget());

            assertEquals(defender.getPosition(), attacker.getPosition());

            /* Wait for the attacker to win the fight */
            Utils.waitForSoldierToBeDying(defender, map);

            /* Verify that the attacker is still alive */
            assertTrue(map.getWorkers().contains(attacker));
        }

        /* Verify that the sixth defender beats the general */
        Military nextDefender = Utils.waitForMilitaryOutsideBuilding(player1);

        assertNotNull(nextDefender);
        assertNotEquals(attacker, nextDefender);

        /* Wait for the fight to start */
        Utils.waitForFightToStart(map, attacker, nextDefender);

        /* Verify that the defender beats the attacker */
        Utils.waitForSoldierToWinFight(nextDefender, map);

        assertTrue(map.getWorkers().contains(nextDefender));
    }

    @Ignore
    @Test
    public void testAttackingSergeantBeatsThreeButNotFourDefendingCorporals() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building fortress0 = map.placeBuilding(new Fortress(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(fortress0);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, 2, barracks0);

        /* Populate player 1's barracks */
        assertTrue(fortress0.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_FIRST_CLASS_RANK, 7, fortress0);

        /* Empty both headquarters for soldiers */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Order an attack */
        assertTrue(player0.canAttack(fortress0));

        player0.attack(fortress0, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(fortress0.getNumberOfHostedMilitary(), 7);
        assertEquals(attacker.getTarget(), fortress0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, fortress0.getFlag().getPosition());

        assertEquals(attacker.getPosition(), fortress0.getFlag().getPosition());

        /* Verify that the attacking corporal beats three privates */
        assertEquals(attacker.getRank(), SERGEANT_RANK);

        for (int i = 0; i < 3; i++) {

            /* Wait for the defender to go to the attacker */
            Military defender = Utils.waitForMilitaryOutsideBuilding(player1);

            assertNotNull(defender);
            assertEquals(defender.getTarget(), fortress0.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, defender, defender.getTarget());

            assertEquals(defender.getPosition(), attacker.getPosition());

            /* Wait for the attacker to win the fight */
            Utils.waitForSoldierToWinFight(attacker, map);

            /* Verify that the attacker is still alive */
            assertTrue(map.getWorkers().contains(attacker));
        }

        /* Verify that the fourth defender beats the general */
        Military nextDefender = Utils.waitForMilitaryOutsideBuilding(player1);

        assertNotNull(nextDefender);
        assertNotEquals(attacker, nextDefender);

        /* Wait for the fight to start */
        Utils.waitForFightToStart(map, attacker, nextDefender);

        /* Verify that the defender beats the attacker */
        Utils.waitForWorkerToDisappear(attacker, map);

        assertTrue(map.getWorkers().contains(nextDefender));
    }

    @Ignore
    @Test
    public void testAttackingOfficerBeatsThreeButNotFourDefendingSergeants() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building fortress0 = map.placeBuilding(new Fortress(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(fortress0);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(OFFICER_RANK, 2, barracks0);

        /* Populate player 1's barracks */
        assertTrue(fortress0.isReady());

        Utils.occupyMilitaryBuilding(SERGEANT_RANK, 7, fortress0);

        /* Empty both headquarters for soldiers */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Order an attack */
        assertTrue(player0.canAttack(fortress0));

        player0.attack(fortress0, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(fortress0.getNumberOfHostedMilitary(), 7);
        assertEquals(attacker.getTarget(), fortress0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, fortress0.getFlag().getPosition());

        assertEquals(attacker.getPosition(), fortress0.getFlag().getPosition());

        /* Verify that the attacking officer beats three sergeants */
        assertEquals(attacker.getRank(), OFFICER_RANK);

        for (int i = 0; i < 3; i++) {

            /* Wait for the defender to go to the attacker */
            Military defender = Utils.waitForMilitaryOutsideBuilding(player1);

            assertNotNull(defender);
            assertEquals(defender.getTarget(), fortress0.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, defender, defender.getTarget());

            assertEquals(defender.getPosition(), attacker.getPosition());

            /* Wait for the attacker to win the fight */
            Utils.waitForSoldierToWinFight(attacker, map);

            /* Verify that the attacker is still alive */
            assertTrue(map.getWorkers().contains(attacker));
        }

        /* Verify that the fourth defender beats the general */
        Military nextDefender = Utils.waitForMilitaryOutsideBuilding(player1);

        assertNotNull(nextDefender);
        assertNotEquals(attacker, nextDefender);

        /* Wait for the fight to start */
        Utils.waitForFightToStart(map, attacker, nextDefender);

        /* Verify that the defender beats the attacker */
        Utils.waitForWorkerToDisappear(attacker, map);

        assertTrue(map.getWorkers().contains(nextDefender));
    }

    @Ignore
    @Test
    public void testAttackingGeneralBeatsThreeButNotFourDefendingOfficers() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building fortress0 = map.placeBuilding(new Fortress(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(fortress0);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks0);

        /* Populate player 1's barracks */
        assertTrue(fortress0.isReady());

        Utils.occupyMilitaryBuilding(OFFICER_RANK, 7, fortress0);

        /* Empty both headquarters for soldiers */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Order an attack */
        assertTrue(player0.canAttack(fortress0));

        player0.attack(fortress0, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(fortress0.getNumberOfHostedMilitary(), 7);
        assertEquals(attacker.getTarget(), fortress0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, fortress0.getFlag().getPosition());

        assertEquals(attacker.getPosition(), fortress0.getFlag().getPosition());

        /* Verify that the attacking general beats three officers */
        assertEquals(attacker.getRank(), GENERAL_RANK);

        for (int i = 0; i < 3; i++) {

            /* Wait for the defender to go to the attacker */
            Military defender = Utils.waitForMilitaryOutsideBuilding(player1);

            assertNotNull(defender);
            assertEquals(defender.getTarget(), fortress0.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, defender, defender.getTarget());

            assertEquals(defender.getPosition(), attacker.getPosition());

            /* Wait for the attacker to win the fight */
            Utils.waitForSoldierToWinFight(attacker, map);

            /* Verify that the attacker is still alive */
            assertTrue(map.getWorkers().contains(attacker));
        }

        /* Verify that the fourth defender beats the general */
        Military nextDefender = Utils.waitForMilitaryOutsideBuilding(player1);

        assertNotNull(nextDefender);
        assertNotEquals(attacker, nextDefender);

        /* Wait for the fight to start */
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
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(guardHouse0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's guard house */
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, 3, guardHouse0);

        assertEquals(guardHouse0.getNumberOfHostedMilitary(), 3);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks1);

        /* Empty both headquarters for soldiers */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 2, AttackStrength.STRONG);

        /* Verify that two soldiers leave the guard house */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 2, player0);

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
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(guardHouse0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's guard house */
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, 3, guardHouse0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        /* Empty both headquarters for soldiers */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 2, AttackStrength.STRONG);

        /* Wait for two soldiers to leave the guard house */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 2, player0);

        assertNotNull(attackers);
        assertEquals(attackers.size(), 2);

        /* Verify that one attacker goes to the flag and the other attacker waits on an adjacent point */
        Military attacker1 = attackers.get(0);
        Military attacker2 = attackers.get(1);

        assertTrue(attacker1.getTarget().equals(barracks1.getFlag().getPosition())
                || attacker2.getTarget().equals(barracks1.getFlag().getPosition()));

        Point flagPoint = barracks1.getFlag().getPosition();

        assertTrue(flagPoint.isAdjacent(attacker1.getTarget()) || flagPoint.equals(attacker1.getTarget()));
        assertTrue(flagPoint.isAdjacent(attacker2.getTarget()) || flagPoint.equals(attacker2.getTarget()));
        assertNotEquals(attacker1.getTarget(), attacker2.getTarget());
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
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(guardHouse0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's guard house */
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, 3, guardHouse0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        /* Clear soldiers from the inventory */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);


        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 2, AttackStrength.STRONG);

        /* Wait for two soldiers to leave the guard house */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 2, player0);

        assertNotNull(attackers);
        assertEquals(attackers.size(), 2);

        /* Get the first attacker */
        Military firstAttacker = null;

        for (Military military : attackers) {
            if (military.getTarget().equals(barracks1.getFlag().getPosition())) {
                firstAttacker = military;

                break;
            }

            map.stepTime();
        }

        assertNotNull(firstAttacker);

        /* Wait for the first attacker to reach the attacked building */
        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, barracks1.getFlag().getPosition());

        /* Verify that one attacker waits until the other attacker wins the fight before entering the building */
        Military defender = Utils.waitForMilitaryOutsideBuilding(player1);

        /* Get the waiting attacker */
        Military waitingAttacker;
        if (attackers.get(0).equals(firstAttacker)) {
            waitingAttacker = attackers.get(1);
        } else {
            waitingAttacker = attackers.get(0);
        }

        if (waitingAttacker.isTraveling()) {
            Utils.fastForwardUntilWorkerReachesPoint(map, waitingAttacker, waitingAttacker.getTarget());
        }

        Point waitingPosition = waitingAttacker.getPosition();

        assertFalse(waitingAttacker.isTraveling());

        /* Wait for the fight to start */
        Utils.waitForFightToStart(map, firstAttacker, defender);

        /* Make sure there are only two attackers */
        assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0).size(), 2);

        /* Wait for the fight to end and verify that the waiting attacker doesn't move */
        Utils.waitForSoldierToWinFight(firstAttacker, map);

        assertTrue(defender.isDying());

        /* Wait for the fighting attacker to go back to the flag */
        assertEquals(firstAttacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, firstAttacker.getTarget());

        /* Verify that only the active attacker enters the building */
        assertEquals(firstAttacker.getTarget(), barracks1.getPosition());
        assertEquals(firstAttacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);

        for (int i = 0; i < 100; i++) {
            if (firstAttacker.getPosition().equals(barracks1.getPosition())) {
                break;
            }

            assertEquals(waitingAttacker.getPosition(), waitingPosition);

            map.stepTime();
        }

        assertEquals(firstAttacker.getPosition(), barracks1.getPosition());
        assertTrue(firstAttacker.isInsideBuilding());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);

        /* Verify that the waiting attacker enters the building */
        map.stepTime();

        assertEquals(waitingAttacker.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, waitingAttacker, barracks1.getPosition());

        assertEquals(waitingAttacker.getPosition(), barracks1.getPosition());
    }

    @Test
    public void testAttackersGoHomeAfterVictoryIfTheyDoNotFitInAttackedBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(49, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point2);

        /* Finish construction */
        Utils.constructHouse(watchTower0);

        /* Populate player 0's watch tower */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 4, watchTower0);

        /* Empty both headquarters for soldiers */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Verify that the attackers-to-be are in the watchtower */
        assertEquals(watchTower0.getNumberOfHostedMilitary(), 4);
        assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0).size(), 0);

        /* Order an attack */
        assertTrue(player0.canAttack(headquarter1));

        player0.attack(headquarter1, 3, AttackStrength.STRONG);

        /* Wait for three soldiers to leave the watch tower */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 3, player0);

        assertNotNull(attackers);
        assertEquals(attackers.size(), 3);

        /* Add reinforcements to the watch tower */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 4, watchTower0);

        /* Verify that there are five soldiers in the watch tower */
        assertEquals(watchTower0.getNumberOfHostedMilitary(), 5);

        /* Get the first attacker */
        Military firstAttacker = Utils.getMainAttacker(headquarter1, attackers);

        /* Wait for the first attacker to reach its position */
        assertEquals(firstAttacker.getTarget(), headquarter1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, headquarter1.getFlag().getPosition());

        /* Wait for the attacker to go to the headquarter */
        assertEquals(firstAttacker.getTarget(), headquarter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, headquarter1.getPosition());

        /* Give the attackers time to detect that the headquarter is destroyed */
        map.stepTime();
        map.stepTime();
        map.stepTime();
        map.stepTime();
        map.stepTime();

        /* Verify that one attacker goes back to the watch tower and the others go to the headquarter */
        int attackersToWatchTower = 0;
        int attackersToHeadquarter = 0;

        for (Military attacker : attackers) {

            if (attacker.getTarget().equals(watchTower0.getPosition())) {
                attackersToWatchTower++;
            } else if (attacker.getTarget().equals(headquarter0.getPosition())) {
                attackersToHeadquarter++;
            }
        }

        assertEquals(attackersToWatchTower, 1);
        assertEquals(attackersToHeadquarter, 2);
    }

    @Test
    public void testAttackersGoHomeOrToStorageAfterCapturingHeadquarter() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(watchTower0);
        Utils.constructHouse(barracks1);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        /* Populate player 0's guard house */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 4, watchTower0);

        /* Empty both headquarters for soldiers */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Verify that the attackers-to-be are in the watchtower */
        assertEquals(watchTower0.getNumberOfHostedMilitary(), 4);
        assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0).size(), 0);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 3, AttackStrength.STRONG);

        /* Wait for two soldiers to leave the watch tower */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 3, player0);

        assertNotNull(attackers);
        assertEquals(attackers.size(), 3);

        /* Get the first attacker */
        Military firstAttacker = Utils.getMainAttacker(barracks1, attackers);

        assertNotNull(firstAttacker);

        /* Wait for the first attacker to reach its position */
        assertEquals(firstAttacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, barracks1.getFlag().getPosition());

        /* Get the defender */
        Military defender = Utils.waitForMilitaryOutsideBuilding(player1);

        assertNotNull(defender);

        /* Wait for the fight to end */
        Utils.waitForWorkerToDisappear(defender, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Wait for the attacker to go back to the fixed point */
        assertEquals(firstAttacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, firstAttacker.getTarget());

        /* Wait for the active attacker to enter the building and verify that the two other attackers wait */
        assertEquals(firstAttacker.getTarget(), barracks1.getPosition());

        attackers.remove(firstAttacker);

        for (int i = 0; i < 100; i++) {
            if (firstAttacker.getPosition().equals(barracks1.getPosition())) {
                break;
            }

            for (Worker military : attackers) {
                assertNotEquals(military.getPosition(), barracks1.getPosition());
            }

            map.stepTime();
        }

        assertEquals(firstAttacker.getPosition(), barracks1.getPosition());
        assertTrue(firstAttacker.isInsideBuilding());

        /* Verify that the captured barracks is manned correctly by the winner */
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);

        /* Verify that the second military enters the captured barracks and the third military returns to the watch tower */
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
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(watchTower0);
        Utils.constructHouse(barracks1);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        /* Populate player 0's guard house */
        assertTrue(watchTower0.isReady());

        Utils.occupyMilitaryBuilding(GENERAL_RANK, 6, watchTower0);

        /* Empty both headquarters for soldiers */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Verify that the attackers-to-be are in the watchtower */
        assertEquals(watchTower0.getNumberOfHostedMilitary(), 6);
        assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0).size(), 0);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 3, AttackStrength.STRONG);

        /* Wait for two soldiers to leave the guard house */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 3, player0);

        /* Wait for the first attacker to reach its position */
        Military firstAttacker = Utils.getMainAttacker(barracks1, attackers);

        assertNotNull(firstAttacker);

        /* Find the defender */
        Military defender = Utils.waitForMilitaryOutsideBuilding(player1);

        assertNotNull(defender);

        /* Fill the watchtower so there is no space there for returning soldiers */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 3, watchTower0);

        assertFalse(watchTower0.needsMilitaryManning());
        assertEquals(watchTower0.getNumberOfHostedMilitary(), 6);

        /* Wait for the fight to end */
        Utils.waitForWorkerToDisappear(defender, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Wait for the active attacker to go back to the flag */
        assertEquals(firstAttacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, firstAttacker.getTarget());

        /* Wait for the active attacker to enter the building and verify that the two other attackers wait */
        assertEquals(firstAttacker.getTarget(), barracks1.getPosition());

        attackers.remove(firstAttacker);

        for (int i = 0; i < 100; i++) {
            if (firstAttacker.getPosition().equals(barracks1.getPosition())) {
                break;
            }

            for (Worker military : attackers) {
                assertNotEquals(military.getPosition(), barracks1.getPosition());
            }

            map.stepTime();
        }

        assertEquals(firstAttacker.getPosition(), barracks1.getPosition());
        assertTrue(firstAttacker.isInsideBuilding());

        /* Verify that the captured barracks is manned correctly by the winner */
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);

        /* Verify that the second military enters the captured barracks and the third military returns to the storage since the watch tower is full */
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
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place watch tower for player 0 */
        Point point2 = new Point(21, 5);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(watchTower0);
        Utils.constructHouse(barracks1);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        /* Populate player 0's watch tower */
        assertTrue(watchTower0.isReady());

        Utils.occupyMilitaryBuilding(GENERAL_RANK, 6, watchTower0);

        /* Empty both headquarters for soldiers */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Verify that the attackers-to-be are in the watchtower */
        assertEquals(watchTower0.getNumberOfHostedMilitary(), 6);
        assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0).size(), 0);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 3, AttackStrength.STRONG);

        /* Wait for two soldiers to leave the watch tower */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 3, player0);

        /* Get the first attacker */
        Military firstAttacker = Utils.getMainAttacker(barracks1, attackers);

        assertNotNull(firstAttacker);

        /* Wait for the first attacker to reach its positions */
        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, firstAttacker.getTarget());

        /* Find the defender */
        Military defender = Utils.waitForMilitaryOutsideBuilding(player1);

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
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(40, 12);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(30, 12);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Place flag */
        Point point4 = new Point(56, 12);
        Flag flag0 = map.placeFlag(player1, point4);

        /* Connect the flag with the barracks */
        Road road0 = map.placeAutoSelectedRoad(player1, flag0, barracks1.getFlag());

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        /* Empty both headquarters for soldiers */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Wait for the attacker to walk to the attacked building */
        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Wait for defender to leave the attacked building */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertNotNull(defender);

        /* Wait for the fight to start and for the defender to be close to dying */
        Utils.waitForFightToStart(map, attacker, defender);

        Utils.waitForSoldierToBeCloseToDying(defender, map);

        /* Send a reinforcement to the attacked building */
        Military reinforcement = new Military(player1, PRIVATE_RANK, map);

        map.placeWorker(reinforcement, flag0);

        reinforcement.setTargetBuilding(barracks1);

        assertEquals(reinforcement.getTarget(), barracks1.getPosition());

        /* Verify that the reinforcement returns to the storage instead of entering the barracks */
        Utils.waitForSoldierToBeDying(defender, map);

        Utils.waitForSoldierToWinFight(attacker, map);

        /* Wait for the attacker to take over the barracks */
        Utils.waitForWorkerToHaveTarget(map, attacker, barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        /* Wait for the reinforcement to reach the next point */
        for (int i = 0; i < 100; i++) {

            if (reinforcement.isExactlyAtPoint()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(reinforcement.isExactlyAtPoint());

        /* Verify that the defender goes back to the headquarters */
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

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(40, 12);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventory */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(30, 12);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Place flags */
        Point point4 = new Point(56, 12);
        Flag flag0 = map.placeFlag(player1, point4);
        Point point5 = new Point(40, 18);
        Flag flag1 = map.placeFlag(player1, point5);

        /* Connect the flag with the barracks */
        Road road0 = map.placeAutoSelectedRoad(player1, flag0, flag1);
        Road road1 = map.placeAutoSelectedRoad(player1, flag1, barracks1.getFlag());

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks0);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        /* Empty both headquarters for soldiers */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Wait for the attacker to walk to the attacked building */
        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Wait for defender to leave the attacked building */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertNotNull(defender);

        /* Wait for the fight to start  */
        Utils.waitForFightToStart(map, attacker, defender);

        /* Send a reinforcement to the attacked building */
        Military reinforcement = new Military(player1, PRIVATE_RANK, map);

        map.placeWorker(reinforcement, flag0);

        reinforcement.setTargetBuilding(barracks1);

        assertEquals(reinforcement.getTarget(), barracks1.getPosition());

        map.stepTime();

        /* Destroy the barracks to make the reinforcement return to the storage */
        barracks1.tearDown();

        assertEquals(reinforcement.getPlayer(), player1);

        /* Verify that the reinforcement returns to the storage instead of entering the barracks */

        /* Wait for the reinforcement to reach the next point */
        for (int i = 0; i < 100; i++) {
            if (reinforcement.isExactlyAtPoint()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(reinforcement.isExactlyAtPoint());

        /* Verify that the defender goes back to the headquarters */
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
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building guardHouse = map.placeBuilding(new GuardHouse(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Place flag */
        Point point4 = new Point(53, 15);
        Flag flag0 = map.placeFlag(player1, point4);

        /* Connect the flag with the barracks */
        Road road0 = map.placeAutoSelectedRoad(player1, flag0, barracks1.getFlag());

        /* Finish construction */
        Utils.constructHouse(guardHouse);
        Utils.constructHouse(barracks1);

        /* Populate player 0's guard house */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 3, guardHouse);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        /* Empty both headquarters for soldiers */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 2, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 2, player0);

        assertNotNull(attackers);
        assertEquals(attackers.size(), 2);

        /* Wait for the attackers to walk to the attacked building */
        Utils.fastForwardUntilWorkerReachesPoint(map, attackers.get(0), attackers.get(0).getTarget());

        /* Wait for defender to leave the attacked building */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

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

            assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
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

        assertEquals(reinforcement.getTarget(), barracks1.getFlag().getPosition());
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

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 19);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 19);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Place flag */
        Point point4 = new Point(53, 19);
        Flag flag0 = map.placeFlag(player1, point4);

        /* Connect the flag with the barracks */
        Road road0 = map.placeAutoSelectedRoad(player1, flag0, barracks1.getFlag());

        /* Finish construction */
        Utils.constructHouse(fortress0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's guard house */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 9, fortress0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks1);

        /* Empty both headquarters for soldiers */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 8, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 8, player0);

        assertNotNull(attackers);
        assertEquals(attackers.size(), 8);

        /* Get the first attacker to fight */
        Military fightingAttacker = Utils.getMainAttacker(barracks1, attackers);

        assertNotNull(fightingAttacker);

        /* Wait for the fighting attacker to walk to the attacked building */
        assertEquals(fightingAttacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fightingAttacker, fightingAttacker.getTarget());

        /* Wait for defender to leave the attacked building */
        Military defender = Utils.waitForMilitaryOutsideBuilding(player1);

        assertNotNull(defender);

        /* Verify that the defender goes to the flag */
        assertEquals(defender.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, barracks1.getFlag().getPosition());

        /* Get the other attackers' positions */
        Map<Military, Point> positions = new HashMap<>();

        for (Military military : attackers) {
            if (military.equals(fightingAttacker)) {
                continue;
            }

            positions.put(military, military.getPosition());
        }

        /* Wait for the fight to start */
        Utils.waitForFightToStart(map, fightingAttacker, defender);

        assertTrue(fightingAttacker.isFighting());
        assertTrue(defender.isFighting());
        assertFalse(defender.isDying());
        assertFalse(defender.isDead());

        /* Verify that the other attackers wait and don't start fighting */
        for (int i = 0; i < 1000; i++) {

            if (defender.isDying()) {
                break;
            }

            assertFalse(fightingAttacker.isDying());
            assertFalse(fightingAttacker.isDead());
            assertTrue(fightingAttacker.isFighting());

            /* Verify that the other attackers are not fighting */
            for (Military military : attackers) {
                if (military.equals(fightingAttacker)) {
                    continue;
                }

                assertFalse(military.isFighting());
                assertEquals(military.getPosition(), positions.get(military));
            }

            /* Verify that no more defenders leave the barracks */
            assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
            assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player1).size(), 1);

            map.stepTime();
        }
    }

    @Test
    public void testMilitaryEnteringBuildingLeavesIfItIsCaptured() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building guardHouse = map.placeBuilding(new GuardHouse(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Place flag */
        Point point4 = new Point(53, 15);
        Flag flag0 = map.placeFlag(player1, point4);

        /* Connect the flag with the barracks */
        Road road0 = map.placeAutoSelectedRoad(player1, flag0, barracks1.getFlag());

        /* Finish construction */
        Utils.constructHouse(guardHouse);
        Utils.constructHouse(barracks1);

        /* Populate player 0's guard house */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 3, guardHouse);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        /* Empty both headquarters for soldiers */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.waitForMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);

        /* Wait for the attackers to walk to the attacked building */
        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Wait for defender to leave the attacked building */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertNotNull(defender);

        /* Wait for the fight to be over */
        Utils.waitForFightToStart(map, attacker, defender);

        Utils.waitForSoldierToWinFight(attacker, map);

        /* Wait for the attacker to go back to the flag */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        /* Let the attacker get started walking to the barracks */
        assertEquals(attacker.getTarget(), barracks1.getPosition());

        map.stepTime();
        map.stepTime();
        map.stepTime();

        /* Send a reinforcement to the attacked building */
        Military reinforcement = new Military(player1, PRIVATE_RANK, map);

        map.placeWorker(reinforcement, barracks1.getFlag());

        reinforcement.setTargetBuilding(barracks1);

        /* Wait for the attacker to reach the building */
        assertNotEquals(attacker.getPosition(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertEquals(barracks1.getPlayer(), player0);
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
        assertTrue(attacker.isInsideBuilding());

        /* Verify that reinforcement reaches the barracks and leaves */
        assertNotEquals(reinforcement.getPosition(), barracks1.getPosition());
        assertEquals(reinforcement.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, reinforcement, barracks1.getPosition());

        assertEquals(reinforcement.getTarget(), headquarter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, reinforcement, headquarter1.getPosition());

        assertEquals(reinforcement.getPosition(), headquarter1.getPosition());
        assertTrue(reinforcement.isInsideBuilding());
    }

    @Test
    public void testAttackersEnterDespitePromisedReinforcement() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventory */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place guard house for player 0 */
        Point point2 = new Point(21, 5);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Connect the barracks with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player1, barracks1.getFlag(), headquarter1.getFlag());

        /* Finish construction */
        Utils.constructHouse(guardHouse0);
        Utils.constructHouse(barracks1);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        /* Populate player 0's guard house */
        assertTrue(guardHouse0.isReady());

        Utils.occupyMilitaryBuilding(GENERAL_RANK, 3, guardHouse0);

        /* Empty both headquarters for soldiers */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 2, AttackStrength.STRONG);

        /* Wait for two soldiers to leave the guard house */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 2, player0);

        assertNotNull(attackers);
        assertEquals(attackers.size(), 2);

        /* Get the first attacker */
        Military firstAttacker = Utils.getMainAttacker(barracks1, attackers);

        /* Wait for the first attacker to reach the attacked building */
        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, barracks1.getFlag().getPosition());

        /* Verify that one attacker waits until the other attacker wins the fight before entering the building */
        Military defender = Utils.waitForMilitaryOutsideBuilding(player1);

        assertNotNull(defender);
        assertTrue(map.getWorkers().contains(defender));

        /* Get the waiting attacker */
        Military waitingAttacker;
        if (attackers.get(0).equals(firstAttacker)) {
            waitingAttacker = attackers.get(1);
        } else {
            waitingAttacker = attackers.get(0);
        }

        /* Make sure there are two attackers */
        assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0).size(), 2);

        /* Wait for the fight to start */
        Utils.waitForFightToStart(map, firstAttacker, defender);

        /* Wait for the defender to be close to dying */
        assertEquals(barracks1.getHostedMilitary().size(), 0);

        Utils.waitForSoldierToBeDying(defender, map);

        /* Send two reinforcements from far away */
        Military reinforcement1 = new Military(player1, GENERAL_RANK, map);
        Military reinforcement2 = new Military(player1, GENERAL_RANK, map);

        map.placeWorker(reinforcement1, headquarter1.getFlag());
        map.placeWorker(reinforcement2, headquarter1.getFlag());

        reinforcement1.setTargetBuilding(barracks1);
        reinforcement2.setTargetBuilding(barracks1);

        barracks1.promiseMilitary(reinforcement1);
        barracks1.promiseMilitary(reinforcement2);

        /* Wait for the fight to end */
        Utils.waitForSoldierToWinFight(firstAttacker, map);

        /* Wait for the fighting attacker to go back to the flag */
        assertEquals(firstAttacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, firstAttacker.getTarget());

        /* Wait for the active attacker enters the building */
        assertEquals(firstAttacker.getTarget(), barracks1.getPosition());
        assertEquals(firstAttacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, barracks1.getPosition());

        assertEquals(firstAttacker.getPosition(), barracks1.getPosition());
        assertTrue(firstAttacker.isInsideBuilding());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);

        /* Verify that the waiting attacker enters the building */
        map.stepTime();

        assertEquals(waitingAttacker.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, waitingAttacker, barracks1.getPosition());

        assertEquals(waitingAttacker.getPosition(), barracks1.getPosition());
    }

    @Test
    public void testRoadRemovedInAttackCannotBeUsedToFindWay() throws Exception {

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

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(44, 18);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place player 2's headquarter */
        Point point10 = new Point(70, 70);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 9, fortress0);

        /* Connect the fortress with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        /* Occupy the road */
        Utils.occupyRoad(road0, map);

        /* Place barracks close to the new border */
        Point point4 = new Point(34, 18);
        assertTrue(player1.getLandInPoints().contains(point4));
        Barracks barracks0 = map.placeBuilding(new Barracks(player1), point4);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Connect the barracks with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player1, headquarter1.getFlag(), barracks0.getFlag());

        /* Occupy the road */
        Utils.occupyRoad(road1, map);

        /* Capture the barracks for player 0 */
        assertTrue(player0.canAttack(barracks0));

        player0.attack(barracks0, 2, AttackStrength.STRONG);

        /* Wait for player 0 to take over the barracks */
        for (int i = 0; i < 2000; i++) {

            if (barracks0.getPlayer().equals(player0) && barracks0.getNumberOfHostedMilitary() > 0) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.areFlagsOrBuildingsConnectedViaRoads(headquarter0, barracks0.getFlag()));
    }

    @Test
    public void testHeadquarterIsDestroyedWhenItGetsCaptured() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Empty the soldiers in player 1's headquarter */
        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Verify that there are no hosted soldiers in the headquarter */
        assertEquals(headquarter1.getNumberOfHostedMilitary(), 0);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 9, fortress0);

        /* Verify that it's possible to attack the headquarter */
        assertEquals(player0.getAvailableAttackersForBuilding(headquarter1), 8);

        /* Capture the player 1's headquarter */
        assertTrue(player0.canAttack(headquarter1));

        player0.attack(headquarter1, 8, AttackStrength.STRONG);

        /* Get attackers */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 8, player0);

        /* Get the main attacker */
        Military firstAttacker = Utils.getMainAttacker(headquarter1, attackers);

        /* Wait for the main attacker to get to the flag */
        assertEquals(firstAttacker.getTarget(), headquarter1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, firstAttacker.getTarget());

        /* Verify that the headquarter remains intact until the main attacker enters */
        for (int i = 0; i < 100; i++) {

            if (firstAttacker.getPosition().equals(headquarter1.getPosition())) {
                break;
            }

            assertTrue(headquarter0.isReady());

            map.stepTime();
        }

        assertEquals(firstAttacker.getPosition(), headquarter1.getPosition());
        assertTrue(headquarter1.isBurningDown());
        assertFalse(headquarter1.isReady());

        /* Verify that the headquarter eventually burns down */
        Utils.fastForward(50, map);

        assertTrue(headquarter1.isDestroyed());

        /* Verify that the headquarter disappears from the map */
        Utils.fastForward(100, map);

        assertFalse(map.getBuildings().contains(headquarter1));
    }

    @Test
    public void testBorderIsRemovedWhenHeadquarterIsDestroyed() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Empty the soldiers in player 1's headquarter */
        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Verify that there are no hosted soldiers in the headquarter */
        assertEquals(headquarter1.getNumberOfHostedMilitary(), 0);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 9, fortress0);

        /* Verify that it's possible to attack the headquarter */
        assertEquals(player0.getAvailableAttackersForBuilding(headquarter1), 8);

        /* Capture the player 1's headquarter */
        assertTrue(player0.canAttack(headquarter1));

        player0.attack(headquarter1, 8, AttackStrength.STRONG);

        /* Get attackers */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 8, player0);

        /* Get the main attacker */
        Military firstAttacker = Utils.getMainAttacker(headquarter1, attackers);

        /* Wait for the main attacker to get to the flag */
        assertEquals(firstAttacker.getTarget(), headquarter1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, firstAttacker.getTarget());

        /* Verify that the headquarter remains intact until the main attacker enters */
        assertTrue(headquarter1.isMilitaryBuilding());
        for (int i = 0; i < 100; i++) {

            if (firstAttacker.getPosition().equals(headquarter1.getPosition())) {
                break;
            }

            assertTrue(headquarter0.isReady());

            map.stepTime();
        }

        assertEquals(firstAttacker.getPosition(), headquarter1.getPosition());
        assertTrue(headquarter1.isBurningDown());
        assertFalse(headquarter1.isReady());

        /* Verify that the border is gone */
        assertTrue(player1.getBorderPoints().isEmpty());

        /* Verify that player 1 has no land */
        assertTrue(player1.getLandInPoints().isEmpty());
    }

    @Test
    public void testMilitaryReturnsToStorageAfterCapturingHeadquarter() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Empty the soldiers in player 1's headquarter */
        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Verify that there are no hosted soldiers in the headquarter */
        assertEquals(headquarter1.getNumberOfHostedMilitary(), 0);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 9, fortress0);

        /* Verify that it's possible to attack the headquarter */
        assertEquals(player0.getAvailableAttackersForBuilding(headquarter1), 8);

        /* Capture the player 1's headquarter */
        assertTrue(player0.canAttack(headquarter1));

        player0.attack(headquarter1, 8, AttackStrength.STRONG);

        /* Get attackers */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 8, player0);

        /* Get the main attacker */
        Military firstAttacker = Utils.getMainAttacker(headquarter1, attackers);

        /* Wait for the main attacker to get to the flag */
        assertEquals(firstAttacker.getTarget(), headquarter1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, firstAttacker.getTarget());

        /* Wait for the main attacker to capture the headquarter */
        assertEquals(firstAttacker.getTarget(), headquarter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, firstAttacker, headquarter1.getPosition());

        /* Wait for the attackers to get to the headquarter and start to walk home */
        for (int i = 0; i < 200; i++) {

            boolean allWalkingBack = true;

            for (Military attacker : attackers) {
                if (!attacker.getTarget().equals(headquarter0.getPosition()) &&
                    !attacker.getTarget().equals(fortress0.getPosition())) {
                    allWalkingBack = false;

                    break;
                }
            }

            if (allWalkingBack) {
                break;
            }

            map.stepTime();
        }

        /* Verify that the attackers return to storage or the fortress */
        for (Military attacker : attackers) {
            assertTrue(attacker.getTarget().equals(headquarter0.getPosition()) ||
                       attacker.getTarget().equals(fortress0.getPosition()));
        }
    }

    @Test
    public void testBarracksGetReinforcedWhenHostedMilitaryTakesOverOtherBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Fill up extra soldiers in player 0's headquarter */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 10);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Connect the barracks to the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Remove all soldiers from player 0's headquarter */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

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
        Utils.waitForFightToStart(map, attacker, defender);

        Utils.waitForSoldierToWinFight(attacker, map);

        /* Wait for the attacker to go back to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Wait for the attacker to take over the building */
        assertEquals(attacker.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertEquals(barracks1.getPlayer(), player0);

        /* Verify that the barracks gets reinforced */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 10);

        assertTrue(headquarter0.getAmount(PRIVATE) > 0);

        for (int i = 0; i < 1000; i++) {

            if (barracks0.getNumberOfHostedMilitary() == 2) {
                break;
            }

            map.stepTime();
        }

        assertEquals(barracks0.getNumberOfHostedMilitary(), 2);
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
    }

    @Test
    public void testConqueredBarracksCanBeUpgradedAndGetMaterial() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(40, 12);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Fill up extra stone in player 0's headquarters */
        Utils.adjustInventoryTo(headquarter0, STONE, 10);

        /* Remove all soldiers from player 0's headquarters */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Connect the barracks to the headquarters */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Place barracks for player 1 */
        Point point3 = new Point(30, 12);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        assertEquals(map.getFlagAtPoint(barracks1.getPosition().downRight()), barracks1.getFlag());

        /* Populate player 0's barracks */
        assertEquals(barracks0.getHostedMilitary().size(), 0);

        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        assertEquals(map.getFlagAtPoint(barracks1.getPosition().downRight()), barracks1.getFlag());

        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        assertTrue(barracks1.isReady());
        assertEquals(map.getFlagAtPoint(barracks1.getPosition().downRight()), barracks1.getFlag());

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

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
        Utils.waitForFightToStart(map, attacker, defender);

        Utils.waitForSoldierToWinFight(attacker, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Wait for the attacker to go back to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Wait for the attacker to take over the building */
        assertEquals(attacker.getTarget(), barracks1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertEquals(barracks1.getPlayer(), player0);

        /* Verify that the barracks can be upgraded */
        barracks1.upgrade();

        assertTrue(barracks1.isUpgrading());

        /* Connect the barracks to the headquarter */
        assertTrue(map.isBuildingAtPoint(headquarter0.getPosition()));
        assertTrue(map.isBuildingAtPoint(barracks1.getPosition()));
        assertTrue(barracks1.isReady());
        assertEquals(map.getFlagAtPoint(barracks1.getPosition().downRight()), barracks1.getFlag());

        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks1.getFlag());

        /* Verify that stone is delivered to the barracks */
        assertTrue(barracks1.needsMaterial(STONE));
        assertEquals(barracks1.getAmount(STONE), 0);

        Worker worker = headquarter0.getWorker();
        Utils.fastForwardUntilWorkerCarriesCargo(map, worker, STONE);

        Cargo cargo = worker.getCargo();

        assertEquals(cargo.getTarget(), barracks1);

        Utils.waitForCargoToReachTarget(map, cargo);

        assertEquals(barracks1.getAmount(STONE), 1);

        /* Wait for the upgrade to happen */
        for (int i = 0; i < 1000; i++) {

            assertTrue(barracks1.isUpgrading());

            if (map.getBuildingAtPoint(point3) instanceof GuardHouse) {
                break;
            }

            map.stepTime();
        }

        assertTrue(map.getBuildingAtPoint(point3) instanceof GuardHouse);
    }

    @Test
    public void testBuildingIsNoLongerUnderAttackWhenDefenderWins() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Wait for a military to leave the attacked building to defend when the attacker reaches the flag */
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);

        /* Wait for the defender to go to the attacker */
        assertTrue(barracks1.isUnderAttack());

        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Wait for the defender's general to beat the attacker's private */
        Utils.waitForFightToStart(map, defender, attacker);

        Utils.waitForSoldierToWinFight(defender, map);

        /* Wait for the defender to return to the fixed point */
        assertEquals(defender.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, defender.getTarget());

        /* Wait for the defender to go back to its building */
        assertEquals(defender.getTarget(), barracks1.getPosition());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, barracks1.getPosition());

        assertTrue(defender.isInsideBuilding());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
        assertEquals(player1, barracks1.getPlayer());

        /* Verify that the barracks is not under attack anymore */
        assertFalse(barracks1.isUnderAttack());
    }

    @Test
    public void testCanAttackWithManyAttackers() throws Exception {

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
        Point point1 = new Point(41, 9);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(17, 5);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Remove all soldiers in the headquarters */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Place fortress for player 0 */
        Point point3 = new Point(17, 13);
        Fortress fortress1 = map.placeBuilding(new Fortress(player0), point3);

        /* Place barracks for player 1 */
        Point point4 = new Point(27, 9);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point4);

        /* Finish construction */
        Utils.constructHouse(fortress0);
        Utils.constructHouse(fortress1);
        Utils.constructHouse(barracks1);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks1);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks1);

        /* Populate player 0's fortresses */
        assertTrue(fortress0.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        assertTrue(fortress1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress1);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress1);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress1);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress1);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress1);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress1);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress1);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress1);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress1);

        assertEquals(player0.getAvailableAttackersForBuilding(barracks1), 16);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 16, AttackStrength.STRONG);

        /* Verify that 16 attackers leave */
        Set<Worker> soldiersOutside = new HashSet<>();
        for (int i = 0; i < 1000; i++) {

            for (Worker worker : map.getWorkers()) {
                if (!worker.getPlayer().equals(player0)) {
                    continue;
                }

                if (! (worker instanceof Military)) {
                    continue;
                }

                if (worker.isInsideBuilding()) {
                    continue;
                }

                soldiersOutside.add(worker);
            }

            if (soldiersOutside.size() == 16) {
                break;
            }

            map.stepTime();
        }

        assertEquals(soldiersOutside.size(), 16);

        for (Worker worker : map.getWorkers()) {
            if (!worker.getPlayer().equals(player0)) {
                continue;
            }

            if (! (worker instanceof Military)) {
                continue;
            }

            if (worker.isInsideBuilding()) {
                continue;
            }

            assertTrue(worker.getTarget().distance(barracks1.getPosition()) < 10 );
        }
    }

    @Test
    public void testFlagsAreRemovedCompletelyWhenTerritoryIsLost() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Remove all soldiers from both headquarters */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Place additional flag close to player 1's barracks */
        Point point4 = new Point(25, 17);
        Flag flag0 = map.placeFlag(player1, point4);

        assertTrue(map.isFlagAtPoint(point4));
        assertEquals(map.getFlagAtPoint(point4), flag0);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);

        /* Wait for the attacker to get to the attacked buildings flag */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

        /* Wait for a defender to come out */
        Military defender = Utils.waitForSoldierNotDyingOutsideBuilding(player1);

        /* Wait for the attacker to beat the defender */
        Utils.waitForFightToStart(map, attacker, defender);

        Utils.waitForSoldierToWinFight(attacker, map);

        /* Wait for the attacker to take over the building */
        Utils.waitForWorkerToHaveTarget(map, attacker, barracks1.getPosition());

        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);
        assertEquals(barracks1.getPlayer(), player1);
        assertEquals(attacker.getTarget(), barracks1.getPosition());
        assertTrue(barracks1.isReady());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertTrue(attacker.isInsideBuilding());
        assertEquals(barracks1.getPlayer(), player0);
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);

        /* Verify that the flag is now outside of player 1's border and completely gone */
        assertFalse(player1.isWithinBorder(point4));
        assertFalse(map.getFlags().contains(flag0));
        assertFalse(map.isFlagAtPoint(point4));
    }
// Test:
    //  - Test all points that can be attacked are within the FOV (not the case today?)
    //  - Winning private meets new private and loses
    //    (what happens if this is before the fight is done?)
    //  - Test several soldiers can defend
    //  - Test soldiers rally from several buildings
    //  - Test no attack possible with only one military in the building
    //  - Test that the attacked building gets filled fully
    //    go back to their original buildings
    //  - Test that promised soldiers walking to the building return home when
    //    it is captured

    @Test
    public void testAttackerBeatsAllDefendingSoldiersAndTakesOverBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventory */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(guardHouse0);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(guardHouse0.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

        /* Order an attack */
        assertTrue(player0.canAttack(guardHouse0));

        player0.attack(guardHouse0, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(guardHouse0.getNumberOfHostedMilitary(), 3);
        assertEquals(attacker.getTarget(), guardHouse0.getFlag().getPosition());
        assertFalse(guardHouse0.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, guardHouse0.getFlag().getPosition());

        assertEquals(attacker.getPosition(), guardHouse0.getFlag().getPosition());
        assertEquals(guardHouse0.getNumberOfHostedMilitary(), 2);

        /* Get the defender */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertNotNull(defender);

        /* Wait for the defender to go to the attacker */
        assertEquals(defender.getTarget(), attacker.getPosition());
        assertTrue(guardHouse0.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Wait for the attacker to beat the first defender */
        Utils.waitForMilitaryToStartFighting(map, defender);

        assertEquals(guardHouse0.getHostedMilitary().size(), 2);

        Utils.waitForSoldierToBeDying(defender, map);

        /* Wait for the second defender to come out */
        Military defender2 = Utils.waitForSoldierNotDyingOutsideBuilding(player1);

        assertEquals(guardHouse0.getHostedMilitary().size(), 1);

        Utils.waitForMilitaryToStartFighting(map, defender2);

        Utils.waitForSoldierToBeDying(defender2, map);

        assertTrue(defender2.isDying());

        /* Wait for the third defender to come out */
        Military defender3 = Utils.waitForSoldierNotDyingOutsideBuilding(player1);

        assertEquals(guardHouse0.getHostedMilitary().size(), 0);

        Utils.waitForMilitaryToStartFighting(map, defender3);

        Utils.waitForSoldierToBeDying(defender3, map);

        assertTrue(defender3.isDying());

        /* Verify that the attacker takes over the building when all defenders are gone */
        Utils.waitForWorkerToBeExactlyOnPoint(attacker, map);

        assertEquals(attacker.getTarget(), guardHouse0.getPosition());
        assertEquals(guardHouse0.getPlayer(), player1);

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, guardHouse0.getPosition());

        assertEquals(guardHouse0.getPlayer(), player0);
    }

    @Test
    public void testHeadquarterIsDefended() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Empty the soldiers in player 1's headquarter */
        Utils.adjustInventoryTo(headquarter1, PRIVATE, 1);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Verify that there are no hosted soldiers in the headquarter */
        assertEquals(headquarter1.getNumberOfHostedMilitary(), 1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 9, fortress0);

        /* Verify that it's possible to attack the headquarters */
        assertEquals(player0.getAvailableAttackersForBuilding(headquarter1), 8);

        /* Capture the player 1's headquarter */
        assertTrue(player0.canAttack(headquarter1));

        player0.attack(headquarter1, 8, AttackStrength.STRONG);

        /* Get attackers */
        List<Military> attackers = Utils.waitForWorkersOutsideBuilding(Military.class, 8, player0);

        /* Get the main attacker */
        Military firstAttacker = Utils.getMainAttacker(headquarter1, attackers);

        /* Verify that the defender comes out and starts to fight */
        assertEquals(headquarter1.getAmount(PRIVATE), 1);

        Military defender = Utils.waitForWorkerOutsideBuilding(Military.class, player1);

        assertEquals(firstAttacker.getPosition(), headquarter1.getFlag().getPosition());
        assertEquals(defender.getTarget(), headquarter1.getFlag().getPosition());

        Utils.waitForFightToStart(map, firstAttacker, defender);
    }

    @Test
    public void testMilitaryAttackWithStrongAttackers() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));
        assertEquals(barracks0.getNumberOfHostedMilitary(), 2);

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Verify that the strong soldier comes out to attack */
        map.stepTime();

        List<Military> militaryOutside = Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0);

        assertEquals(militaryOutside.size(), 1);
        assertEquals(barracks0.getNumberOfHostedMilitary(), 1);
        assertEquals(militaryOutside.getFirst().getRank(), GENERAL_RANK);
    }

    @Test
    public void testMilitaryAttackWithWeakAttackers() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.WEAK);

        /* Verify that the strong soldier comes out to attack */
        map.stepTime();

        List<Military> militaryOutside = Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0);

        assertEquals(militaryOutside.size(), 1);
        assertEquals(barracks0.getNumberOfHostedMilitary(), 1);
        assertEquals(militaryOutside.getFirst().getRank(), PRIVATE_RANK);
    }

    @Test
    public void testClosestAttackerIsPicked() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        Utils.adjustInventoryTo(headquarter0, PRIVATE, 2);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.WEAK);

        /* Verify that the close soldier comes out to attack */
        map.stepTime();

        List<Military> soldiersOutside = Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0);

        assertEquals(soldiersOutside.size(), 1);

        var soldierOutside = soldiersOutside.getFirst();

        assertEquals(barracks0.getNumberOfHostedMilitary(), 1);
        assertEquals(headquarter0.getNumberOfHostedMilitary(), 2);
        assertEquals(soldierOutside.getRank(), PRIVATE_RANK);
        assertEquals(soldierOutside.getPosition(), barracks0.getPosition());
    }
}
