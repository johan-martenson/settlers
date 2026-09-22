package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Rank.GENERAL_RANK;
import static org.appland.settlers.model.actors.Rank.PRIVATE_RANK;
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

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Clear soldiers from the headquarters
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        // Place barracks for player 0
        var point2 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point2);

        // Place barracks for player 1
        var point3 = new Point(23, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point3);

        // Finish construction
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        // Populate player 0's barracks
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        // Populate player 1's barracks
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        // Order an attack
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        // Find the military that was chosen to attack
        map.stepTime();

        var attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);
        assertFalse(attacker.isFighting());

        // Wait for the military to reach the attacked building
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        // Wait for the defender to go to the attacker
        var defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());
        assertFalse(defender.isFighting());

        Utils.fastForwardUntilWorkerReachesPoint(defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        // Verify that the attacker and the defender walk apart
        assertFalse(attacker.isFighting());
        assertFalse(defender.isFighting());

        var attackersDistance = -1;
        var defendersDistance = -1;

        for (int i = 0; i < 20; i++) {
            if (attacker.isExactlyAtPoint() || defender.isExactlyAtPoint()) {
                map.stepTime();

                continue;
            }

            var newAttackersDistance = attacker.getPercentageOfDistanceTraveled();
            var newDefendersDistance = defender.getPercentageOfDistanceTraveled();

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

        // Verify that one soldier is attacking and the other is defending or getting hit
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

        System.out.println(attacker.state);
        System.out.println(attacker.fightState);
        System.out.println(defender.state);
        System.out.println(defender.fightState);
        System.out.println("Attacker: " + attacker.isHitting() + ", " + defender.isJumpingBack() + " " + defender.isJumpingBack() + " " + defender.isJumpingBack());
        System.out.println("Defender: " + defender.isHitting() + ", " + attacker.isJumpingBack() + " " + attacker.isJumpingBack() + " " + attacker.isJumpingBack());

        assertEquals(attacker.getOpponent(), defender);
        assertEquals(defender.getOpponent(), attacker);
        assertTrue(
                (attacker.isHitting() && (defender.isJumpingBack() || defender.isStandingAside() || defender.isGettingHit())) ||
                        (defender.isHitting() && (attacker.isJumpingBack() || attacker.isStandingAside() || attacker.isGettingHit()))
        );
        assertFalse(attacker.isAttacking() && defender.isAttacking());

        // Wait for one of the soldiers to be dying
        var dyingSoldier = Utils.waitForSoldierToBeDying(map, attacker, defender);

        // Wait for the dying soldier to die
        Utils.waitForWorkerToDie(map, dyingSoldier);

        assertTrue(!map.getWorkers().contains(attacker) || !map.getWorkers().contains(defender));

        // Get the winner
        Soldier winner;

        if (attacker.isDead()) {
            winner = defender;
        } else {
            winner = attacker;
        }

        // Give the winner some time to notice that it's won
        for (int i = 0; i < 5; i++) {
            if (!winner.isFighting()) {
                break;
            }

            assertTrue(winner.isFighting());

            map.stepTime();
        }

        // Verify that the winner isn't fighting when it's walking back
        assertFalse(winner.isFighting());

        // Verify that the winner walks back to the flag
        assertEquals(winner.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(winner, barracks1.getFlag().getPosition());

        assertEquals(winner.getPosition(), barracks1.getFlag().getPosition());
    }

    @Test
    public void testGeneralHealth() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(9, 15);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Remove all soldiers from the headquarters and place one general in player 0's headquarters
        Utils.clearInventory(headquarter0, PRIVATE, Material.PRIVATE_FIRST_CLASS, Material.SERGEANT, Material.OFFICER);
        Utils.clearInventory(headquarter1, PRIVATE, Material.PRIVATE_FIRST_CLASS, Material.SERGEANT, Material.OFFICER, Material.GENERAL);

        Utils.adjustInventoryTo(headquarter0, GENERAL, 2);

        // Set no reserved soldiers in the headquarters
        Utils.setNoReservedSoldiers(headquarter0);
        Utils.setNoReservedSoldiers(headquarter1);

        // Place barracks for player 1
        var point3 = new Point(23, 15);
        var fortress = map.placeBuilding(new Fortress(player1), point3);

        // Finish construction
        Utils.constructHouse(fortress);

        // Make sure both barracks have soldiers
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 9, fortress);

        // Order an attack on player 1's fortress
        assertTrue(player0.canAttack(fortress));

        player0.attack(fortress, 1, AttackStrength.STRONG);

        // Get the attacking general
        map.stepTime();

        var attackingGeneral = Utils.findSoldierOutsideBuilding(player0);

        // Verify that the general dies after seven hits
        for (int i = 0; i < 200; i++) {

            // Wait for the attacking general to get hit
            Utils.waitForSoldierToGetHit(attackingGeneral, map);

            assertTrue(attackingGeneral.isGettingHit());

            Utils.waitForSoldierToStopGettingHit(attackingGeneral, map);

            assertFalse(attackingGeneral.isGettingHit());

            // Verify that the general is dying after the right amount of hits
            if (attackingGeneral.isDying()) {
                assertEquals(i, 6);

                break;
            }

            assertFalse(i > 6);
        }

        assertTrue(attackingGeneral.isDying());
    }

    @Test
    public void testOfficerHealth() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(9, 15);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Remove all soldiers from the headquarters and place one general in player 0's headquarters
        Utils.clearInventory(headquarter0, PRIVATE, Material.PRIVATE_FIRST_CLASS, Material.SERGEANT, Material.OFFICER);
        Utils.clearInventory(headquarter1, PRIVATE, Material.PRIVATE_FIRST_CLASS, Material.SERGEANT, Material.OFFICER, Material.GENERAL);

        Utils.adjustInventoryTo(headquarter0, OFFICER, 2);

        // Set no reserved soldiers in the headquarters
        Utils.setNoReservedSoldiers(headquarter0);
        Utils.setNoReservedSoldiers(headquarter1);

        // Place barracks for player 1
        var point3 = new Point(23, 15);
        var fortress = map.placeBuilding(new Fortress(player1), point3);

        // Finish construction
        Utils.constructHouse(fortress);

        // Make sure both barracks have soldiers
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 9, fortress);

        // Order an attack on player 1's fortress
        assertTrue(player0.canAttack(fortress));

        player0.attack(fortress, 1, AttackStrength.STRONG);

        // Get the attacking general
        map.stepTime();

        var attackingGeneral = Utils.findSoldierOutsideBuilding(player0);

        // Verify that the general dies after seven hits
        for (int i = 0; i < 200; i++) {

            // Wait for the attacking general to get hit
            Utils.waitForSoldierToGetHit(attackingGeneral, map);

            assertTrue(attackingGeneral.isGettingHit());

            Utils.waitForSoldierToStopGettingHit(attackingGeneral, map);

            assertFalse(attackingGeneral.isGettingHit());

            // Verify that the general is dying after the right amount of hits
            if (attackingGeneral.isDying()) {
                assertEquals(i, 5);

                break;
            }

            assertFalse(i > 5);
        }

        assertTrue(attackingGeneral.isDying());
    }

    @Test
    public void testSergeantHealth() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(9, 15);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Remove all soldiers from the headquarters and place one general in player 0's headquarters
        Utils.clearInventory(headquarter0, PRIVATE, Material.PRIVATE_FIRST_CLASS, Material.SERGEANT, Material.OFFICER);
        Utils.clearInventory(headquarter1, PRIVATE, Material.PRIVATE_FIRST_CLASS, Material.SERGEANT, Material.OFFICER, Material.GENERAL);

        Utils.adjustInventoryTo(headquarter0, SERGEANT, 2);

        // Set no reserved soldiers in the headquarters
        Utils.setNoReservedSoldiers(headquarter0);
        Utils.setNoReservedSoldiers(headquarter1);

        // Place barracks for player 1
        var point3 = new Point(23, 15);
        var fortress = map.placeBuilding(new Fortress(player1), point3);

        // Finish construction
        Utils.constructHouse(fortress);

        // Make sure both barracks have soldiers
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 9, fortress);

        // Order an attack on player 1's fortress
        assertTrue(player0.canAttack(fortress));

        player0.attack(fortress, 1, AttackStrength.STRONG);

        // Get the attacking general
        map.stepTime();

        var attackingGeneral = Utils.findSoldierOutsideBuilding(player0);

        // Verify that the general dies after seven hits
        for (int i = 0; i < 200; i++) {

            // Wait for the attacking general to get hit
            Utils.waitForSoldierToGetHit(attackingGeneral, map);

            assertTrue(attackingGeneral.isGettingHit());

            Utils.waitForSoldierToStopGettingHit(attackingGeneral, map);

            assertFalse(attackingGeneral.isGettingHit());

            // Verify that the general is dying after the right amount of hits
            if (attackingGeneral.isDying()) {
                assertEquals(i, 4);

                break;
            }

            assertFalse(i > 4);
        }

        assertTrue(attackingGeneral.isDying());
    }

    @Test
    public void testPrivateFirstRankHealth() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(9, 15);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Remove all soldiers from the headquarters and place one general in player 0's headquarters
        Utils.clearInventory(headquarter0, PRIVATE, Material.PRIVATE_FIRST_CLASS, Material.SERGEANT, Material.OFFICER);
        Utils.clearInventory(headquarter1, PRIVATE, Material.PRIVATE_FIRST_CLASS, Material.SERGEANT, Material.OFFICER, Material.GENERAL);

        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 2);

        // Set no reserved soldiers in the headquarters
        Utils.setNoReservedSoldiers(headquarter0);
        Utils.setNoReservedSoldiers(headquarter1);

        // Place barracks for player 1
        var point3 = new Point(23, 15);
        var fortress = map.placeBuilding(new Fortress(player1), point3);

        // Finish construction
        Utils.constructHouse(fortress);

        // Make sure both barracks have soldiers
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 9, fortress);

        // Order an attack on player 1's fortress
        assertTrue(player0.canAttack(fortress));

        player0.attack(fortress, 1, AttackStrength.STRONG);

        // Get the attacking general
        map.stepTime();

        var attackingGeneral = Utils.findSoldierOutsideBuilding(player0);

        // Verify that the general dies after seven hits
        for (int i = 0; i < 200; i++) {

            // Wait for the attacking general to get hit
            Utils.waitForSoldierToGetHit(attackingGeneral, map);

            assertTrue(attackingGeneral.isGettingHit());

            Utils.waitForSoldierToStopGettingHit(attackingGeneral, map);

            assertFalse(attackingGeneral.isGettingHit());

            // Verify that the general is dying after the right amount of hits
            if (attackingGeneral.isDying()) {
                assertEquals(i, 3);

                break;
            }

            assertFalse(i > 3);
        }

        assertTrue(attackingGeneral.isDying());
    }

    @Test
    public void testPrivateHealth() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(9, 15);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Remove all soldiers from the headquarters and place one general in player 0's headquarters
        Utils.clearInventory(headquarter0, PRIVATE, Material.PRIVATE_FIRST_CLASS, Material.SERGEANT, Material.OFFICER);
        Utils.clearInventory(headquarter1, PRIVATE, Material.PRIVATE_FIRST_CLASS, Material.SERGEANT, Material.OFFICER, Material.GENERAL);

        Utils.adjustInventoryTo(headquarter0, PRIVATE, 2);

        // Set no reserved soldiers in the headquarters
        Utils.setNoReservedSoldiers(headquarter0);
        Utils.setNoReservedSoldiers(headquarter1);

        // Place barracks for player 1
        var point3 = new Point(23, 15);
        var fortress = map.placeBuilding(new Fortress(player1), point3);

        // Finish construction
        Utils.constructHouse(fortress);

        // Make sure both barracks have soldiers
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 9, fortress);

        // Order an attack on player 1's fortress
        assertTrue(player0.canAttack(fortress));

        player0.attack(fortress, 1, AttackStrength.STRONG);

        // Get the attacking general
        map.stepTime();

        var attackingGeneral = Utils.findSoldierOutsideBuilding(player0);

        // Verify that the general dies after seven hits
        for (int i = 0; i < 200; i++) {

            // Wait for the attacking general to get hit
            Utils.waitForSoldierToGetHit(attackingGeneral, map);

            assertTrue(attackingGeneral.isGettingHit());

            Utils.waitForSoldierToStopGettingHit(attackingGeneral, map);

            assertFalse(attackingGeneral.isGettingHit());

            // Verify that the general is dying after the right amount of hits
            if (attackingGeneral.isDying()) {
                assertEquals(i, 2);

                break;
            }

            assertFalse(i > 2);
        }

        assertTrue(attackingGeneral.isDying());
    }
}
