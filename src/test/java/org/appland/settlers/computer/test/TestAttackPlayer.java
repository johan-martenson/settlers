/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.computer.test;

import org.appland.settlers.computer.AttackPlayer;
import org.appland.settlers.computer.ComputerPlayer;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Soldier;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author johan
 */
public class TestAttackPlayer {

    @Test
    public void testAttackerDoesNotBuild() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Create the computer player */
        ComputerPlayer computerPlayer = new AttackPlayer(player0, map);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the player does not place any buildings */
        for (int i = 0; i < 1000; i++) {

            computerPlayer.turn();

            assertEquals(player0.getBuildings().size(), 1);
            assertTrue(player0.getBuildings().contains(headquarter));

            map.stepTime();
        }
    }

    @Test
    public void testAttackerWithBuildingInReachAttacks() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Create the computer player */
        ComputerPlayer computerPlayer = new AttackPlayer(player0, map);

        /* Place headquarter for player 0*/
        Point point0 = new Point(10, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter for player 1 */
        Point point1 = new Point(44, 10);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place and occupy barracks for player 0 */
        Point point2 = new Point(24, 10);
        Barracks barracks0 = Utils.placeAndOccupyBarracks(player0, point2);

        /* Place and occupy barracks for player 1 */
        Point point3 = new Point(34, 10);
        Barracks barracks1 = Utils.placeAndOccupyBarracks(player1, point3);

        /* Add an extra soldier to the attacking player's barracks */
        Utils.occupyMilitaryBuilding(Soldier.Rank.PRIVATE_RANK, barracks0);

        /* Verify that the player attacks the other player */
        assertEquals(player0.getAvailableAttackersForBuilding(barracks1), 1);

        for (int i = 0; i < 100; i++) {

            computerPlayer.turn();

            List<Soldier> militariesOutside = Utils.findWorkersOfTypeOutsideForPlayer(Soldier.class, player0);

            if (militariesOutside.size() == 1) {
                break;
            }

            map.stepTime();
        }

        List<Soldier> militaries = Utils.findWorkersOfTypeOutsideForPlayer(Soldier.class, player0);

        assertEquals(militaries.size(), 1);

        Soldier attacker = militaries.get(0);

        assertTrue(attacker.getTarget().distance(barracks1.getPosition()) < 3);
    }

    @Test
    public void testAttackerAttacksWithAllAvailableMilitaries() {

    }
}
