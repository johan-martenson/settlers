package org.appland.settlers.test;

import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.junit.Assert.*;

public class TestFighting {

    /*
    Fight:
     - One soldier waits in place for another soldier
     - Second soldier goes to the same point as the waiting soldier
     - They walk a little bit apart
     - Repeat until one of the soldiers dies:
         - One of them attacks
         - The other soldier:
            - Gets hit
            - Jumps back a bit
            - Stands on the side a bit

    Todo:
     - Test dying time
     - Test disappearing from map after dying time
     - Test skeleton is left
     - Test walking back to the point where the soldiers first met (instead of going further away)
     */

    @Test
    public void testFightStartsAndOneSoldierHitsWhileTheOtherGetsHitOrAvoids() throws Exception {

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

        /* Clear soldiers from the headquarters */
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
        assertFalse(attacker.isFighting());

        /* Wait for the military to reach the attacked building */
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);

        /* Wait for the defender to go to the attacker */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

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

            if (attackersDistance < 50) {
                assertTrue(newAttackersDistance > attackersDistance);
            }

            if (defendersDistance < 50) {
                assertTrue(newDefendersDistance > defendersDistance);
            }

            attackersDistance = newAttackersDistance;
            defendersDistance = newDefendersDistance;

            if (newAttackersDistance >= 50 && newDefendersDistance >= 50) {
                break;
            }

            assertTrue(attackersDistance < 50 || defendersDistance < 50);
            assertFalse(attackersDistance > 50 || defendersDistance > 50);

            map.stepTime();
        }

        assertEquals(attackersDistance, 50);
        assertEquals(defendersDistance, 50);

        /* Verify that one soldier is attacking and the other is defending or getting hit */
        assertTrue(attacker.isFighting());
        assertTrue(defender.isFighting());

        for (int i = 0; i < 200; i++) {
            if (attacker.isHitting() || defender.isHitting()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(attacker.isAttacking());
        assertTrue(attacker.isFighting());
        assertTrue(defender.isDefending());
        assertTrue(defender.isFighting());
        assertTrue(attacker.isHitting() || defender.isHitting());

        map.stepTime();

        assertTrue(
                (attacker.isHitting() && (defender.isJumpingBack() || defender.isStandingAside() || defender.isGettingHit())) ||
                        (defender.isHitting() && (attacker.isJumpingBack() || attacker.isStandingAside() || attacker.isGettingHit()))
        );
        assertFalse(attacker.isAttacking() && defender.isAttacking());

        /* Wait for one of the soldiers to be dying */
        Military dyingSoldier = Utils.waitForSoldierToBeDying(map, attacker, defender);

        /* Wait for the dying soldier to die */
        Utils.waitForWorkerToDie(map, dyingSoldier);

        assertTrue(!map.getWorkers().contains(attacker) || !map.getWorkers().contains(defender));

        /* Get the winner */
        Military winner;

        if (attacker.isDead()) {
            winner = defender;
        } else {
            winner = attacker;
        }

        /* Give the winner some time to notice that it's won */
        for (int i = 0; i < 5; i++) {
            if (!winner.isFighting()) {
                break;
            }

            assertTrue(winner.isFighting());

            map.stepTime();
        }

        /* Verify that the winner isn't fighting when it's walking back */
        assertFalse(winner.isFighting());

        /* Verify that the winner walks back to the flag */
        assertEquals(winner.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, winner, barracks1.getFlag().getPosition());

        assertEquals(winner.getPosition(), barracks1.getFlag().getPosition());
    }

    @Test
    public void testBothAttackerAndDefenderCanMakeFirstHitInFight() throws Exception {

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
        Utils.clearInventory(headquarter0, PRIVATE, Material.PRIVATE_FIRST_CLASS, Material.SERGEANT, Material.OFFICER, Material.GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, Material.PRIVATE_FIRST_CLASS, Material.SERGEANT, Material.OFFICER, Material.GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Verify that both the attacker and the defender can make the first hit */
        boolean attackerHasMadeFirstHit = false;
        boolean defenderHasMadeFirstHit = false;

        /* Make sure both barracks have soldiers */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Do continuous attacking until both the attacker and the defender has initiated the fight with a hit */
        for (int i = 0; i < 200; i++) {

            /* Make sure both barracks have soldiers */
            if (barracks0.getNumberOfHostedMilitary() < 2) {
                Utils.occupyMilitaryBuilding(
                        PRIVATE_RANK,
                        2 - barracks0.getNumberOfHostedMilitary(),
                        barracks0);
            }

            if (barracks1.getNumberOfHostedMilitary() < 1) {
                Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);
            }

            /* Find the soldier that was chosen to attack */
            Military attacker = Utils.waitForSoldierNotDyingOutsideBuilding(player0);

            /* Wait for the military to reach the attacked building */
            assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

            assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

            /* Wait for the defender to go to the attacker */
            Military defender = Utils.waitForSoldierNotDyingOutsideBuilding(player1);

            assertFalse(defender.isDead());
            assertNotNull(defender);
            assertFalse(defender.isFighting());
            assertEquals(defender.getTarget(), attacker.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

            assertEquals(defender.getPosition(), attacker.getPosition());

            /* Wait for the fight to start */
            assertFalse(attacker.isFighting());
            assertFalse(defender.isFighting());

            Utils.waitForFightToStart(map, attacker, defender);

            /* Verify that one soldier is attacking and the other is defending or getting hit */
            assertTrue(attacker.isFighting());
            assertTrue(defender.isFighting());

            if (attacker.isHitting()) {
                attackerHasMadeFirstHit = true;
            } else if (defender.isHitting()) {
                defenderHasMadeFirstHit = true;
            }

            if (attackerHasMadeFirstHit && defenderHasMadeFirstHit) {
                break;
            }

            /* Wait for the fight to end */
            Utils.waitForFightToEnd(map, attacker, defender);

            assertTrue(attacker.isDead() || defender.isDead());

            /* If the attacker won, the next defender should come out */
            if (defender.isDead()) {
                if (attacker.isFighting()) {
                    Utils.waitForSoldierToWinFight(attacker, map);
                }
            } else if (attacker.isDead()) {
                if (defender.isFighting()) {
                    Utils.waitForSoldierToWinFight(defender, map);
                }

                /* Wait for the defender to go back to the barracks */
                for (int k = 0; k < 200; k++) {
                    assertNotEquals(defender.getTarget(), headquarter1.getPosition());

                    if (barracks1.getPosition().equals(defender.getTarget())) {
                        break;
                    }

                    map.stepTime();
                }

                assertEquals(defender.getTarget(), barracks1.getPosition());

                Utils.fastForwardUntilWorkerReachesPoint(map, defender, barracks1.getPosition());

                /* Order a new attack */
                assertTrue(player0.canAttack(barracks1));

                player0.attack(barracks1, 1, AttackStrength.STRONG);

                map.stepTime();
            } else {
                fail();
            }

            /* Make sure player 0's barracks is occupied */
            if (barracks0.getNumberOfHostedMilitary() < 1) {
                Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
            }

            /* Make sure player 1's barracks is occupied */
            if (barracks1.getNumberOfHostedMilitary() < 1) {
                Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);
            }
        }

        assertTrue(attackerHasMadeFirstHit && defenderHasMadeFirstHit);
    }
}
