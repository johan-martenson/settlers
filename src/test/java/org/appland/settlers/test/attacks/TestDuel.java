package org.appland.settlers.test.attacks;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Rank;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.test.Utils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TestDuel {

    @Test
    public void testAttackerMakesTheFirstHitAttempt() throws Exception {

        // Run the test several times - to test each rank and with changed random state
        for (var rank : Rank.values()) {
            for (var i = 0; i < 10; i++) {

                // Create player list with two players
                var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.VIKINGS, PlayerType.HUMAN);
                var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

                // Create the map
                var map = new GameMap(List.of(player0, player1), 50, 51);

                // Place the headquarters
                var headquarters0 = map.placeBuilding(new Headquarter(player0), new Point(10, 10));
                var headquarters1 = map.placeBuilding(new Headquarter(player1), new Point(34, 10));

                Utils.clearReserves(headquarters0, headquarters1);
                Utils.clearSoldiersFromInventory(headquarters0, headquarters1);

                Utils.adjustInventoryTo(headquarters0, rank.toMaterial(), 1);
                Utils.adjustInventoryTo(headquarters1, rank.toMaterial(), 1);

                // Place the barracks
                var barracks0 = map.placeBuilding(new Barracks(player1), new Point(24, 10));

                // Connect the buildings
                map.placeAutoSelectedRoad(player1, headquarters1.getFlag(), barracks0.getFlag());

                // Wait for the buildings to be constructed
                Utils.waitForBuildingToBeConstructed(headquarters0);
                Utils.waitForBuildingToBeConstructed(headquarters1);
                Utils.waitForBuildingToBeConstructed(barracks0);

                // Wait for the military buildings to get populated
                Utils.waitForMilitaryBuildingToGetPopulated(barracks0, 1);

                // Order an attack
                player0.attack(barracks0, 1, AttackStrength.STRONG);

                var attacker = Utils.waitForPrimaryAttacker(barracks0, player0);

                // Wait for the fight to start
                Utils.waitForSoldierToBeFighting(attacker);

                var defender = attacker.getOpponent();

                // Wait until one soldier starts the first hit attempt
                Utils.waitUntil(() -> attacker.isHitting() || defender.isHitting(), map);

                // Verify that the attacker makes the first hit attempt
                assertTrue(attacker.isHitting());
                assertFalse(defender.isHitting());
            }
        }
    }

    @Test
    public void testHitAttemptAlternatesBetweenSoldiers() throws Exception {

        // Run the test several times - to test each rank and with changed random state
        for (var rank : Rank.values()) {
            for (var i = 0; i < 10; i++) {

                // Create player list with two players
                var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.VIKINGS, PlayerType.HUMAN);
                var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

                // Create the map
                var map = new GameMap(List.of(player0, player1), 50, 51);

                // Place the headquarters
                var headquarters0 = map.placeBuilding(new Headquarter(player0), new Point(10, 10));
                var headquarters1 = map.placeBuilding(new Headquarter(player1), new Point(34, 10));

                Utils.clearReserves(headquarters0, headquarters1);
                Utils.clearSoldiersFromInventory(headquarters0, headquarters1);

                Utils.adjustInventoryTo(headquarters0, rank.toMaterial(), 1);
                Utils.adjustInventoryTo(headquarters1, rank.toMaterial(), 1);

                // Place the barracks
                var barracks0 = map.placeBuilding(new Barracks(player1), new Point(24, 10));

                // Connect the buildings
                map.placeAutoSelectedRoad(player1, headquarters1.getFlag(), barracks0.getFlag());

                // Wait for the buildings to be constructed
                Utils.waitForBuildingToBeConstructed(barracks0);

                // Wait for the military buildings to get populated
                Utils.waitForMilitaryBuildingToGetPopulated(barracks0, 1);

                // Order an attack
                player0.attack(barracks0, 1, AttackStrength.STRONG);

                var attacker = Utils.waitForPrimaryAttacker(barracks0, player0);

                // Wait for the fight to start
                Utils.waitForSoldierToBeFighting(attacker);

                var defender = attacker.getOpponent();

                // Verify that hit attempts alternate between the soldiers
                for (var j = 0; j < 5; j++) {
                    assertFalse(attacker.isDying());
                    assertFalse(defender.isDying());

                    Utils.waitUntil(() -> defender.isDying() || attacker.isDying() || attacker.isHitting(), map);

                    if (defender.isDying() || attacker.isDying()) {
                        break;
                    }

                    assertTrue(attacker.isHitting());
                    assertFalse(defender.isHitting());

                    Utils.waitUntil(() -> defender.isDying() || attacker.isDying() || defender.isHitting(), map);

                    if (defender.isDying() || attacker.isDying()) {
                        break;
                    }

                    assertFalse(attacker.isHitting());
                    assertTrue(defender.isHitting());
                }
            }
        }
    }

    @Test
    public void testSuccessfulHitReducesHealthByOne() throws Exception {

        // Run the test several times - to test each rank and with changed random state
        for (var rank : Rank.values()) {
            for (var i = 0; i < 10; i++) {

                // Create player list with two players
                var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.VIKINGS, PlayerType.HUMAN);
                var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

                // Create the map
                var map = new GameMap(List.of(player0, player1), 50, 51);

                // Place the headquarters
                var headquarters0 = map.placeBuilding(new Headquarter(player0), new Point(10, 10));
                var headquarters1 = map.placeBuilding(new Headquarter(player1), new Point(34, 10));

                Utils.clearReserves(headquarters0, headquarters1);
                Utils.clearSoldiersFromInventory(headquarters0, headquarters1);

                Utils.adjustInventoryTo(headquarters0, rank.toMaterial(), 1);
                Utils.adjustInventoryTo(headquarters1, rank.toMaterial(), 1);

                // Place the barracks
                var barracks0 = map.placeBuilding(new Barracks(player1), new Point(24, 10));

                // Connect the buildings
                map.placeAutoSelectedRoad(player1, headquarters1.getFlag(), barracks0.getFlag());

                // Wait for the buildings to be constructed
                Utils.waitForBuildingToBeConstructed(barracks0);

                // Wait for the military buildings to get populated
                Utils.waitForMilitaryBuildingToGetPopulated(barracks0, 1);

                // Order an attack
                player0.attack(barracks0, 1, AttackStrength.STRONG);

                var attacker = Utils.waitForPrimaryAttacker(barracks0, player0);

                // Wait for the fight to start
                Utils.waitForSoldierToBeFighting(attacker);

                var defender = attacker.getOpponent();

                // Wait for the first successful hit
                while (!attacker.isDying() && !defender.isDying()) {

                    Utils.waitUntil(() ->
                            (attacker.isAttacking() && attacker.isGettingHit()) ||
                                    (defender.isDefending() && defender.isGettingHit()) ||
                                    attacker.isDying() ||
                                    defender.isDying(), map);

                    if (attacker.isDying() || defender.isDying()) {
                        break;
                    }

                    if (attacker.isGettingHit()) {

                        // Wait until the hit has completed
                        Utils.waitUntil(() -> !attacker.isGettingHit(), map);

                        // Verify that exactly one health point was lost
                        assertEquals(attacker.getMaxHealth() - 1, attacker.getHealth());
                        assertEquals(defender.getMaxHealth(), defender.getHealth());
                        break;
                    }

                    if (defender.isGettingHit()) {
                        assertEquals(defender.getMaxHealth(), defender.getHealth());
                        assertTrue(defender.isGettingHit());
                        assertTrue(defender.isDefending());

                        // Wait until the hit has completed
                        Utils.waitUntil(() -> !defender.isGettingHit(), map);

                        // Verify that exactly one health point was lost
                        assertFalse(defender.isGettingHit());
                        assertEquals(attacker.getMaxHealth(), attacker.getHealth());
                        assertEquals(defender.getMaxHealth() - 1, defender.getHealth());

                        break;
                    }
                }
            }
        }
    }

    @Test
    public void testBlockedHitDoesNotReduceHealth() throws Exception {

        // Run the test several times - to test each rank and with changed random state
        for (var rank : Rank.values()) {
            for (var i = 0; i < 10; i++) {

                // Create player list with two players
                var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.VIKINGS, PlayerType.HUMAN);
                var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

                // Create the map
                var map = new GameMap(List.of(player0, player1), 50, 51);

                // Place the headquarters
                var headquarters0 = map.placeBuilding(new Headquarter(player0), new Point(10, 10));
                var headquarters1 = map.placeBuilding(new Headquarter(player1), new Point(34, 10));

                Utils.clearReserves(headquarters0, headquarters1);
                Utils.clearSoldiersFromInventory(headquarters0, headquarters1);

                Utils.adjustInventoryTo(headquarters0, rank.toMaterial(), 1);
                Utils.adjustInventoryTo(headquarters1, rank.toMaterial(), 1);

                // Place the barracks
                var barracks0 = map.placeBuilding(new Barracks(player1), new Point(24, 10));

                // Connect the buildings
                map.placeAutoSelectedRoad(player1, headquarters1.getFlag(), barracks0.getFlag());

                // Wait for the buildings to be constructed
                Utils.waitForBuildingToBeConstructed(barracks0);

                // Wait for the military buildings to get populated
                Utils.waitForMilitaryBuildingToGetPopulated(barracks0, 1);

                // Order an attack
                player0.attack(barracks0, 1, AttackStrength.STRONG);

                var attacker = Utils.waitForPrimaryAttacker(barracks0, player0);

                // Wait for the fight to start
                Utils.waitForSoldierToBeFighting(attacker);

                var defender = attacker.getOpponent();

                // Wait for the first blocked hit
                while (!attacker.isDying() && !defender.isDying()) {
                    var attackerHealth = attacker.getHealth();
                    var defenderHealth = defender.getHealth();

                    for (int j = 0; j < 2_000; j++) {
                        if (attacker.isJumpingBack() ||
                                attacker.isStandingAside() ||
                                defender.isJumpingBack() ||
                                defender.isStandingAside() ||
                                attacker.isDying() ||
                                defender.isDying()) {
                            break;
                        }

                        attackerHealth = attacker.getHealth();
                        defenderHealth = defender.getHealth();

                        map.stepTime();
                    }

                    if (attacker.isDying() || defender.isDying()) {
                        break;
                    }

                    if (attacker.isJumpingBack() || attacker.isStandingAside()) {

                        // Wait until the blocked hit has completed
                        Utils.waitUntil(() ->
                                !attacker.isJumpingBack() &&
                                        !attacker.isStandingAside(), map);

                        // Verify that no health was lost
                        assertEquals(attackerHealth, attacker.getHealth());
                        assertEquals(defenderHealth, defender.getHealth());
                        break;
                    }

                    if (defender.isJumpingBack() || defender.isStandingAside()) {

                        // Wait until the blocked hit has completed
                        Utils.waitUntil(() ->
                                !defender.isJumpingBack() &&
                                        !defender.isStandingAside(), map);

                        // Verify that no health was lost
                        assertEquals(attackerHealth, attacker.getHealth());
                        assertEquals(defenderHealth, defender.getHealth());
                        break;
                    }
                }
            }
        }
    }

    @Test
    public void testCombatRollHitProbability() throws Exception {

        // Run the test for every rank
        for (var rank : Rank.values()) {

            var hits = 0;
            var blockedHits = 0;

            // Run many fights
            for (var i = 0; i < 500; i++) {

                // Create player list with two players
                var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.VIKINGS, PlayerType.HUMAN);
                var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

                // Create the map
                var map = new GameMap(List.of(player0, player1), 50, 51);

                // Place the headquarters
                var headquarters0 = map.placeBuilding(new Headquarter(player0), new Point(10, 10));
                var headquarters1 = map.placeBuilding(new Headquarter(player1), new Point(34, 10));

                Utils.clearReserves(headquarters0, headquarters1);
                Utils.clearSoldiersFromInventory(headquarters0, headquarters1);

                Utils.adjustInventoryTo(headquarters0, rank.toMaterial(), 1);
                Utils.adjustInventoryTo(headquarters1, rank.toMaterial(), 1);

                // Place the barracks
                var barracks0 = map.placeBuilding(new Barracks(player1), new Point(24, 10));

                // Connect the buildings
                map.placeAutoSelectedRoad(player1, headquarters1.getFlag(), barracks0.getFlag());

                // Wait for the buildings to be constructed
                Utils.waitForBuildingToBeConstructed(barracks0);

                // Wait for the military buildings to get populated
                Utils.waitForMilitaryBuildingToGetPopulated(barracks0, 1);

                // Order an attack
                player0.attack(barracks0, 1, AttackStrength.STRONG);

                var attacker = Utils.waitForPrimaryAttacker(barracks0, player0);

                // Wait for the fight to start
                Utils.waitForSoldierToBeFighting(attacker);

                var defender = attacker.getOpponent();

                // Measure all hit attempts during the fight
                while (!attacker.isDying() && !defender.isDying()) {

                    Utils.waitUntil(() ->
                            attacker.isGettingHit() ||
                                    defender.isGettingHit() ||
                                    attacker.isJumpingBack() ||
                                    attacker.isStandingAside() ||
                                    defender.isJumpingBack() ||
                                    defender.isStandingAside() ||
                                    attacker.isDying() ||
                                    defender.isDying(), map);

                    if (attacker.isDying() || defender.isDying()) {
                        break;
                    }

                    if (attacker.isGettingHit() || defender.isGettingHit()) {

                        hits++;

                        Utils.waitUntil(() ->
                                !attacker.isGettingHit() &&
                                        !defender.isGettingHit(), map);

                        continue;
                    }

                    blockedHits++;

                    Utils.waitUntil(() ->
                            !attacker.isJumpingBack() &&
                                    !attacker.isStandingAside() &&
                                    !defender.isJumpingBack() &&
                                    !defender.isStandingAside(), map);
                }
            }

            var hitRate = hits / (double)(hits + blockedHits);

            System.out.println(hitRate);

            switch (rank) {
                case PRIVATE_RANK -> {
                    assertTrue(hitRate >= 0.36);
                    assertTrue(hitRate <= 0.39);
                }

                case PRIVATE_FIRST_CLASS_RANK -> {
                    assertTrue(hitRate >= 0.39);
                    assertTrue(hitRate <= 0.41);
                }

                case SERGEANT_RANK -> {
                    assertTrue(hitRate >= 0.41);
                    assertTrue(hitRate <= 0.43);
                }

                case OFFICER_RANK -> {
                    assertTrue(hitRate >= 0.42);
                    assertTrue(hitRate <= 0.44);
                }

                case GENERAL_RANK -> {
                    assertTrue(hitRate >= 0.43);
                    assertTrue(hitRate <= 0.45);
                }
            }
        }
    }

    @Test
    public void testWinnerKeepsRemainingHealth() throws Exception {

        // Run the test several times - to test each rank and with changed random state
        for (var rank : Rank.values()) {
            for (var i = 0; i < 10; i++) {

                // Create player list with two players
                var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.VIKINGS, PlayerType.HUMAN);
                var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

                // Create the map
                var map = new GameMap(List.of(player0, player1), 50, 51);

                // Place the headquarters
                var headquarters0 = map.placeBuilding(new Headquarter(player0), new Point(10, 10));
                var headquarters1 = map.placeBuilding(new Headquarter(player1), new Point(34, 10));

                Utils.clearReserves(headquarters0, headquarters1);
                Utils.clearSoldiersFromInventory(headquarters0, headquarters1);

                Utils.adjustInventoryTo(headquarters0, rank.toMaterial(), 1);
                Utils.adjustInventoryTo(headquarters1, rank.toMaterial(), 1);

                // Place the barracks
                var barracks0 = map.placeBuilding(new Barracks(player1), new Point(24, 10));

                // Connect the buildings
                map.placeAutoSelectedRoad(player1, headquarters1.getFlag(), barracks0.getFlag());

                // Wait for the buildings to be constructed
                Utils.waitForBuildingToBeConstructed(barracks0);

                // Wait for the military buildings to get populated
                Utils.waitForMilitaryBuildingToGetPopulated(barracks0, 1);

                // Order an attack
                player0.attack(barracks0, 1, AttackStrength.STRONG);

                var attacker = Utils.waitForPrimaryAttacker(barracks0, player0);

                // Wait for the fight to start
                Utils.waitForSoldierToBeFighting(attacker);

                var defender = attacker.getOpponent();

                int winnerHealth = -1;
                Soldier winner = null;

                // Wait until one soldier starts dying
                while (true) {

                    Utils.waitUntil(() ->
                            attacker.isDying() ||
                                    defender.isDying(), map);

                    if (attacker.isDying()) {
                        winner = defender;
                        winnerHealth = defender.getHealth();
                        break;
                    }

                    if (defender.isDying()) {
                        winner = attacker;
                        winnerHealth = attacker.getHealth();
                        break;
                    }
                }

                // Wait for the fight to end
                for (int j = 0; j < 2_000; j++) {
                    if (!winner.isFighting()) {
                        break;
                    }

                    map.stepTime();
                }

                assertFalse(winner.isFighting());

                // Verify that the winner kept the remaining health
                assertEquals(winnerHealth, winner.getHealth());
            }
        }
    }

    @Test
    public void testExactlyOneSoldierDies() throws Exception {

        // Run the test several times - to test each rank and with changed random state
        for (var rank : Rank.values()) {
            for (var i = 0; i < 10; i++) {

                // Create player list with two players
                var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.VIKINGS, PlayerType.HUMAN);
                var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

                // Create the map
                var map = new GameMap(List.of(player0, player1), 50, 51);

                // Place the headquarters
                var headquarters0 = map.placeBuilding(new Headquarter(player0), new Point(10, 10));
                var headquarters1 = map.placeBuilding(new Headquarter(player1), new Point(34, 10));

                Utils.clearReserves(headquarters0, headquarters1);
                Utils.clearSoldiersFromInventory(headquarters0, headquarters1);

                Utils.adjustInventoryTo(headquarters0, rank.toMaterial(), 1);
                Utils.adjustInventoryTo(headquarters1, rank.toMaterial(), 1);

                // Place the barracks
                var barracks0 = map.placeBuilding(new Barracks(player1), new Point(24, 10));

                // Connect the buildings
                map.placeAutoSelectedRoad(player1, headquarters1.getFlag(), barracks0.getFlag());

                // Wait for the buildings to be constructed
                Utils.waitForBuildingToBeConstructed(barracks0);

                // Wait for the military buildings to get populated
                Utils.waitForMilitaryBuildingToGetPopulated(barracks0, 1);

                // Order an attack
                player0.attack(barracks0, 1, AttackStrength.STRONG);

                var attacker = Utils.waitForPrimaryAttacker(barracks0, player0);

                // Wait for the fight to start
                Utils.waitForSoldierToBeFighting(attacker);

                var defender = attacker.getOpponent();

                // Wait until one soldier starts dying
                Utils.waitUntil(() ->
                        attacker.isDying() ||
                                defender.isDying(), map);

                // Verify that exactly one soldier dies
                assertNotEquals(attacker.isDying(), defender.isDying());

                if (attacker.isDying()) {
                    Utils.waitUntil(attacker::isDead, map);

                    assertTrue(attacker.isDead());
                    assertFalse(defender.isDead());
                } else {
                    Utils.waitUntil(defender::isDead, map);

                    assertTrue(defender.isDead());
                    assertFalse(attacker.isDead());
                }
            }
        }
    }

    @Test
    public void testWinnerReturnsToBuildingWithRemainingHealth() throws Exception {

        // Run the test several times - to test each rank and with changed random state
        for (var rank : Rank.values()) {
            for (var i = 0; i < 10; i++) {

                // Create player list with two players
                var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.VIKINGS, PlayerType.HUMAN);
                var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

                // Create the map
                var map = new GameMap(List.of(player0, player1), 50, 51);

                // Place the headquarters
                var headquarters0 = map.placeBuilding(new Headquarter(player0), new Point(10, 10));
                var headquarters1 = map.placeBuilding(new Headquarter(player1), new Point(34, 10));

                Utils.clearReserves(headquarters0, headquarters1);
                Utils.clearSoldiersFromInventory(headquarters0, headquarters1);

                Utils.adjustInventoryTo(headquarters0, rank.toMaterial(), 1);
                Utils.adjustInventoryTo(headquarters1, rank.toMaterial(), 1);

                // Place the barracks
                var barracks0 = map.placeBuilding(new Barracks(player1), new Point(24, 10));

                // Connect the buildings
                map.placeAutoSelectedRoad(player1, headquarters1.getFlag(), barracks0.getFlag());

                // Wait for the buildings to be constructed
                Utils.waitForBuildingToBeConstructed(barracks0);

                // Wait for the military buildings to get populated
                Utils.waitForMilitaryBuildingToGetPopulated(barracks0, 1);

                // Order an attack
                player0.attack(barracks0, 1, AttackStrength.STRONG);

                var attacker = Utils.waitForPrimaryAttacker(barracks0, player0);

                // Wait for the fight to start
                Utils.waitForSoldierToBeFighting(attacker);

                var defender = attacker.getOpponent();

                // Wait until one soldier starts dying
                Utils.waitUntil(() ->
                        attacker.isDying() ||
                                defender.isDying(), map);

                Soldier winner;
                Building destination;
                int remainingHealth;

                if (attacker.isDying()) {
                    winner = defender;
                    destination = barracks0;
                    remainingHealth = defender.getHealth();
                } else {
                    winner = attacker;
                    destination = headquarters0;
                    remainingHealth = attacker.getHealth();
                }

                // Wait for the winner to return to the military building
                Utils.waitUntil(() -> winner.getHome() == destination, map);

                // Verify that the winner kept the remaining health while returning
                assertEquals(remainingHealth, winner.getHealth());
            }
        }
    }
}
