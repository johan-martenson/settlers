/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import java.util.LinkedList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.SERGEANT;
import org.appland.settlers.model.Military;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestWinning {

    @Test
    public void testOnePlayerDoesNotWinAutomatically() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);

        List<Player> players = new LinkedList<>();

        players.add(player0);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Headquarter headquarter0 = new Headquarter(player0);
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Verify that the only player does not win automatically */
        for (int i = 0; i < 100; i++) {

            assertNull(map.getWinner());

            map.stepTime();
        }
    }

    @Test
    public void testPlayerWinsWhenBeatingOnlyOtherPlayer() throws Exception {

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
        Point point1 = new Point(39, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(19, 5);
        Building fortress0 = new Fortress(player0);
        map.placeBuilding(fortress0, point2);

        /* Finish construction */
        Utils.constructHouse(fortress0);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, fortress0);

        /* Empty all militaries from the second player's headquarter */
        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Order an attack */
        assertTrue(player0.getAvailableAttackersForBuilding(headquarter1) > 0);

        player0.attack(headquarter1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);

        /* Wait for the attacker to get to the attacked buildings flag */
        assertEquals(attacker.getTarget(), headquarter1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, headquarter1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), headquarter1.getFlag().getPosition());

        /* Verify that the headquarter is destroyed and the first player won the game */
        assertEquals(headquarter1.getNumberOfHostedMilitary(), 0);
        assertEquals(headquarter1.getPlayer(), player1);
        assertEquals(attacker.getTarget(), headquarter1.getPosition());
        assertNull(map.getWinner());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, headquarter1.getPosition());

        assertEquals(map.getWinner(), player0);
        assertEquals(player1.getBuildings().size(), 1);
        assertTrue(player1.getBuildings().get(0).isBurningDown());
    }

    /*

    - add test that the winner remains a winner
    - add test that the winner remains a winner even if all his buildings are destroyed




    */
}
