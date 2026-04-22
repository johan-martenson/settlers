package org.appland.settlers.test.attacks;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.test.Utils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.junit.Assert.*;

public class TestAttackScenarios {

    @Test
    public void testPrimaryAttackerWaitsForRemoteDefender() throws InvalidUserActionException {

        /// To test:
        ///  - Primary attacker beats home defenders
        ///  - There is a remote defender waiting
        ///  - Primary waits at the flag for the remote defender
        ///  - The remote defender goes to the flag and starts fighting the primary attacker
        ///
        /// Setup:
        ///  - Two attackers - semi strong - two sergeants in player 0's headquarters
        ///  - Weak defense of attacked building - two private in player 1's barracks
        ///  - Strong remote defender - one general in player 1's headquarters

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Give each headquarters the right amount and type of soldiers
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        Utils.adjustInventoryTo(headquarter0, GENERAL, 1);
        Utils.adjustInventoryTo(headquarter1, PRIVATE, 4);

        // Place barracks for player 1
        var point2 = new Point(21, 15);
        var barracks0 = map.placeBuilding(new Barracks(player1), point2);

        // Connect the barracks to the headquarters and wait for it to get constructed and occupied
        var road0 = map.placeAutoSelectedRoad(player1, headquarter1.getFlag(), barracks0.getFlag());

        Utils.waitForBuildingToBeConstructed(barracks0);

        Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

        // Let player 0 attack player 1's barracks with one soldier
        assertTrue(player0.canAttack(barracks0));
        assertEquals(player0.getNumberOfAvailableAttackers(barracks0), 1);

        player0.attack(barracks0, 1, AttackStrength.STRONG);

        // Find the attacker
        var attacker = Utils.waitForSoldierOutsideBuilding(player0);

        // Wait for the attacker to walk to the flag
        assertEquals(attacker.getTarget(), barracks0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        // Find the remote defender
        var remoteDefender = Utils.waitForRemoteDefender(barracks0).getFirst();

        // Wait for the primary attacker to fight the last home defender
        for (int i = 0; i < 20_000; i++) {
            if (attacker.isFighting() && attacker.getOpponent().getHome().equals(barracks0) && barracks0.getHostedSoldiers().isEmpty()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(attacker.isFighting());
        assertEquals(attacker.getOpponent().getHome(), barracks0);
        assertEquals(barracks0.getHostedSoldiers().size(), 0);

        map.stepTime();

        var homeDefender = attacker.getOpponent();

        assertNotNull(homeDefender);
        assertEquals(homeDefender.getHome(), barracks0);
        assertTrue(homeDefender.isFighting());
        assertNotEquals(homeDefender, remoteDefender);

        // Verify that the remote defender stands waiting while the primary attacker and the home defender fights
        for (int i = 0; i < 2_000; i++) {
            System.out.println("Test - Waiting for primary attacker to fight home defender " + i + "  (" + remoteDefender + ")");

            if (homeDefender.isDead()) {
                break;
            }

            assertTrue(attacker.isFighting());
            assertTrue(homeDefender.isFighting());
            assertEquals(attacker.getOpponent(), homeDefender);
            assertFalse(attacker.isDead());

            map.stepTime();
        }

        assertTrue(homeDefender.isDead());
        assertFalse(attacker.isDead());

        map.stepTime();

        assertFalse(attacker.isFighting());

        Utils.waitForSoldierToWalkToFixedPoint(attacker, map);

        assertFalse(attacker.isTraveling());
        assertEquals(attacker.getPosition(), barracks0.getFlag().getPosition());

        // Verify that the primary attacker waits by the flag and the remote defender goes to fight it
        map.stepTime();

        assertEquals(remoteDefender.getTarget(), barracks0.getFlag().getPosition());

        for (int i = 0; i < 2_000; i++) {
            if (remoteDefender.getPosition().equals(barracks0.getFlag().getPosition())) {
                break;
            }

            assertEquals(attacker.getPosition(), barracks0.getFlag().getPosition());
            assertFalse(attacker.isFighting());

            map.stepTime();
        }

        assertEquals(remoteDefender.getPosition(), barracks0.getFlag().getPosition());

        System.out.println("Test - 1 - remote defender: " + remoteDefender);
        System.out.println("      " + remoteDefender.getPercentageOfDistanceTraveled());

        map.stepTime();

        System.out.println("Test - 2 - remote defender: " + remoteDefender);
        System.out.println("      " + remoteDefender.getPercentageOfDistanceTraveled());

        assertTrue(remoteDefender.getTarget().equals(barracks0.getFlag().getPosition().right()) ||
                remoteDefender.getTarget().equals(barracks0.getFlag().getPosition().left()));

        Utils.waitForSoldierToStopWalkingApart(remoteDefender, map);

        System.out.println("Test - 3 - remote defender: " + remoteDefender);
        System.out.println("      " + remoteDefender.getPercentageOfDistanceTraveled());

        map.stepTime();

        assertTrue(remoteDefender.isFighting());
        assertEquals(remoteDefender.getOpponent(), attacker);
        assertEquals(attacker.getOpponent(), remoteDefender);
    }
}
