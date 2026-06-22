package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.GuardHouse;
import org.appland.settlers.model.buildings.Headquarter;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for soldier health recovery mechanics after combat.
 */
public class TestSoldierRecovery {

    //
    // GROUP 1: Tests where the attack fails, the defender is hurt, and recovers
    //

    @Test
    public void testDefenderRecoversInBarracksAfterFailedAttack() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Clear soldiers from the inventories and configure ranks for the fight
        Utils.clearSoldiersFromInventory(headquarter0, headquarter1);

        headquarter0.setReservedSoldiers(PRIVATE_RANK, 0);
        headquarter1.setReservedSoldiers(GENERAL_RANK, 0);

        // Player 0 gets a weak attacker (Private), Player 1 gets a strong defender (General)
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 2);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 1);

        // Place barracks for both players
        var point2 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point2);
        var point3 = new Point(23, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point3);

        // Connect military buildings to headquarters to allow natural construction
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());
        map.placeAutoSelectedRoad(player1, headquarter1.getFlag(), barracks1.getFlag());

        // Wait for natural construction and occupancy
        Utils.waitForBuildingsToBeConstructed(barracks0, barracks1);
        Utils.waitForMilitaryBuildingsToGetPopulated(barracks0, barracks1);

        // Initiate attack
        assertEquals(2, barracks0.getHostedSoldiers().size());
        assertEquals(2, barracks0.getNumberOfHostedSoldiers());
        assertEquals(1, barracks1.getHostedSoldiers().size());
        assertEquals(1, barracks1.getNumberOfHostedSoldiers());
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        map.stepTime();

        // Get the attacker and defender
        var attacker = Utils.findSoldierOutsideBuilding(player0);
        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        var defender = Utils.findSoldierOutsideBuilding(player1);
        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        // Wait for the fight to start and the defender to win
        Utils.waitForFightToStart(map, attacker, defender);
        Utils.waitForSoldierToWinFight(defender, map);

        // Verify that the defender took damage
        assertTrue(defender.getHealth() < defender.getMaxHealth());

        int healthAfterFight = defender.getHealth();

        // Wait for the defender to go back into the barracks
        assertEquals(defender.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, defender.getTarget());
        Utils.fastForwardUntilWorkerReachesPoint(map, defender, barracks1.getPosition());

        assertTrue(defender.isInsideBuilding());

        // Fast-forward time and verify the soldier recovers health inside the building
        Utils.fastForward(100, map);

        assertTrue(defender.getHealth() > healthAfterFight);

        // Wait for full recovery
        for (int i = 0; i < 3_000; i++) {
            if (defender.getHealth() == defender.getMaxHealth()) {
                break;
            }

            map.stepTime();
        }

        assertEquals(defender.getMaxHealth(), defender.getHealth());
    }

    @Test
    public void testDefenderRecoversInHeadquarterAfterFailedAttack() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place headquarters (Player 1's HQ is the target)
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        var point1 = new Point(23, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Clear soldiers from the inventories
        Utils.clearSoldiersFromInventory(headquarter0, headquarter1);

        headquarter0.setReservedSoldiers(SERGEANT_RANK, 0);
        headquarter1.setReservedSoldiers(GENERAL_RANK, 0);

        Utils.adjustInventoryTo(headquarter0, SERGEANT, 2);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 1);

        // Place a barracks for player 0 so they can reach player 1's headquarters
        var point2 = new Point(20, 6);
        var barracks0 = map.placeBuilding(new Barracks(player0), point2);

        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        // Wait for construction and occupancy
        Utils.waitForBuildingsToBeConstructed(barracks0);
        Utils.waitForMilitaryBuildingsToGetPopulated(barracks0);

        Utils.waitForMilitaryBuildingToHaveNumberOfHostedSoldiers(barracks0, 2);

        // Player 0 initiates attack directly on Player 1's headquarters
        assertEquals(barracks0.getHostedSoldiers().size(), 2);
        assertTrue(player0.canAttack(headquarter1));

        player0.attack(headquarter1, 1, AttackStrength.STRONG);

        map.stepTime();

        var attacker = Utils.findSoldierOutsideBuilding(player0);
        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, headquarter1.getFlag().getPosition());

        var defender = Utils.findSoldierOutsideBuilding(player1);
        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        // Let the fight conclude with the defender winning
        Utils.waitForFightToStart(map, attacker, defender);
        Utils.waitForSoldierToWinFight(defender, map);

        assertTrue(defender.getHealth() < defender.getMaxHealth());

        // Defender goes back to the headquarters
        Utils.fastForwardUntilWorkerReachesPoint(map, defender, headquarter1.getFlag().getPosition());
        Utils.fastForwardUntilWorkerReachesPoint(map, defender, headquarter1.getPosition());

        assertTrue(defender.isInsideBuilding());

        // Wait for full health recovery inside the headquarters
        for (int i = 0; i < 3_000; i++) {
            if (defender.getHealth() == defender.getMaxHealth()) {
                break;
            }

            map.stepTime();
        }

        assertEquals(defender.getMaxHealth(), defender.getHealth());
    }

    //
    // GROUP 2: Tests where the attack succeeds, the attacker is hurt, and recovers in the captured building
    //

    @Test
    public void testAttackerRecoversInCapturedBarracksAfterSuccessfulAttack() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Clear soldiers from the inventories
        Utils.clearSoldiersFromInventory(headquarter0, headquarter1);

        headquarter0.setReservedSoldiers(GENERAL_RANK, 0);
        headquarter1.setReservedSoldiers(PRIVATE_RANK, 0);

        // Player 0 gets a strong attacker (General), Player 1 gets a weak defender (Private)
        Utils.adjustInventoryTo(headquarter0, GENERAL, 2);
        Utils.adjustInventoryTo(headquarter1, PRIVATE, 1);

        // Place barracks
        var point2 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point2);
        var point3 = new Point(23, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point3);

        // Connect roads
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());
        map.placeAutoSelectedRoad(player1, headquarter1.getFlag(), barracks1.getFlag());

        // Wait for natural construction and occupancy
        Utils.waitForBuildingsToBeConstructed(barracks0, barracks1);
        Utils.waitForMilitaryBuildingsToGetPopulated(barracks0, barracks1);

        // Initiate attack
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        map.stepTime();

        var attacker = Utils.findSoldierOutsideBuilding(player0);
        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        var defender = Utils.findSoldierOutsideBuilding(player1);
        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        // Wait for the fight to start and the attacker to win
        Utils.waitForFightToStart(map, attacker, defender);
        Utils.waitForSoldierToWinFight(attacker, map);

        // Verify the attacker was hurt during the fight
        assertTrue(attacker.getHealth() < attacker.getMaxHealth());

        int healthAfterFight = attacker.getHealth();

        // The attacker returns to the flag, then captures the building
        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());
        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertTrue(attacker.isInsideBuilding());
        assertEquals(barracks1.getPlayer(), player0);

        // Fast-forward time and verify the attacker recovers health inside the captured building
        Utils.fastForward(100, map);

        assertTrue(attacker.getHealth() > healthAfterFight);

        // Wait for full health recovery
        for (int i = 0; i < 3_000; i++) {
            if (attacker.getHealth() == attacker.getMaxHealth()) {
                break;
            }

            map.stepTime();
        }

        assertEquals(attacker.getMaxHealth(), attacker.getHealth());
    }

    @Test
    public void testAttackerRecoversInCapturedGuardHouseAfterSuccessfulAttack() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Clear soldiers from the inventories
        Utils.clearSoldiersFromInventory(headquarter0, headquarter1);

        headquarter0.setReservedSoldiers(GENERAL_RANK, 0);
        headquarter1.setReservedSoldiers(SERGEANT_RANK, 0);

        // Attacker gets General, Defender gets Private
        Utils.adjustInventoryTo(headquarter0, GENERAL, 2);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 1);

        // Player 0 gets a Barracks, Player 1 gets a GuardHouse
        var point2 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point2);
        var point3 = new Point(23, 15);
        var guardHouse1 = map.placeBuilding(new GuardHouse(player1), point3);

        // Connect roads
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());
        map.placeAutoSelectedRoad(player1, headquarter1.getFlag(), guardHouse1.getFlag());

        // Wait for natural construction and occupancy
        Utils.waitForBuildingsToBeConstructed(barracks0, guardHouse1);
        Utils.waitForMilitaryBuildingsToGetPopulated(barracks0, guardHouse1);

        // Initiate attack against the GuardHouse
        assertTrue(player0.canAttack(guardHouse1));

        player0.attack(guardHouse1, 1, AttackStrength.STRONG);

        map.stepTime();

        var attacker = Utils.findSoldierOutsideBuilding(player0);
        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, guardHouse1.getFlag().getPosition());

        var defender = Utils.findSoldierOutsideBuilding(player1);
        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        // Wait for the fight to start and the attacker to win
        Utils.waitForFightToStart(map, attacker, defender);
        Utils.waitForSoldierToWinFight(attacker, map);

        assertTrue(attacker.getHealth() < attacker.getMaxHealth());

        // The attacker captures the GuardHouse
        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, guardHouse1.getFlag().getPosition());
        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, guardHouse1.getPosition());

        assertTrue(attacker.isInsideBuilding());
        assertEquals(guardHouse1.getPlayer(), player0);

        // Wait for full health recovery
        for (int i = 0; i < 3_000; i++) {
            if (attacker.getHealth() == attacker.getMaxHealth()) {
                break;
            }

            map.stepTime();
        }

        assertEquals(attacker.getMaxHealth(), attacker.getHealth());
    }

    //
    // GROUP 3: Tests ensuring no recovery happens outside buildings
    //

    @Test
    public void testDefenderDoesNotRecoverWhileOutsideAfterFailedAttack() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Clear soldiers and configure ranks
        Utils.clearSoldiersFromInventory(headquarter0, headquarter1);
        headquarter0.setReservedSoldiers(SERGEANT_RANK, 0);
        headquarter1.setReservedSoldiers(GENERAL_RANK, 0);

        // Player 0 gets a weak attacker (Private), Player 1 gets a strong defender (General)
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 2);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 1);

        // Place barracks for both players
        var point2 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point2);
        var point3 = new Point(23, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point3);

        // Connect roads
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());
        map.placeAutoSelectedRoad(player1, headquarter1.getFlag(), barracks1.getFlag());

        // Wait for natural construction and occupancy
        Utils.waitForBuildingsToBeConstructed(barracks0, barracks1);
        Utils.waitForMilitaryBuildingsToGetPopulated(barracks0, barracks1);

        // Initiate attack
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        map.stepTime();

        // Get the attacker and defender and fast-forward to the fight
        var attacker = Utils.findSoldierOutsideBuilding(player0);
        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        var defender = Utils.findSoldierOutsideBuilding(player1);
        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        // Wait for the fight to start and the defender to win
        Utils.waitForFightToStart(map, attacker, defender);
        Utils.waitForSoldierToWinFight(defender, map);

        // Verify the defender took damage
        assertTrue(defender.getHealth() < defender.getMaxHealth());

        int healthAfterFight = defender.getHealth();

        // Step time frame-by-frame while the defender walks back to the barracks.
        // Assert that health does not increase at any point while outside.
        int stepsOutside = 0;
        while (!defender.isInsideBuilding()) {
            map.stepTime();

            assertEquals("Defender should not recover health while outside", healthAfterFight, defender.getHealth());

            stepsOutside++;
        }

        assertTrue("Defender should have spent time walking outside", stepsOutside > 0);
    }

    @Test
    public void testAttackerDoesNotRecoverWhileOutsideAfterSuccessfulAttack() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Clear soldiers and configure ranks
        Utils.clearSoldiersFromInventory(headquarter0, headquarter1);
        headquarter0.setReservedSoldiers(GENERAL_RANK, 0);
        headquarter1.setReservedSoldiers(SERGEANT_RANK, 0);

        // Player 0 gets a strong attacker (General), Player 1 gets a weak defender (Private)
        Utils.adjustInventoryTo(headquarter0, GENERAL, 2);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 1);

        // Place barracks
        var point2 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point2);
        var point3 = new Point(23, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point3);

        // Connect roads
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());
        map.placeAutoSelectedRoad(player1, headquarter1.getFlag(), barracks1.getFlag());

        // Wait for natural construction and occupancy
        Utils.waitForBuildingsToBeConstructed(barracks0, barracks1);
        Utils.waitForMilitaryBuildingsToGetPopulated(barracks0, barracks1);

        // Initiate attack
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        map.stepTime();

        // Get the attacker and defender and fast-forward to the fight
        var attacker = Utils.findSoldierOutsideBuilding(player0);
        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        var defender = Utils.findSoldierOutsideBuilding(player1);
        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        // Wait for the fight to start and the attacker to win
        Utils.waitForFightToStart(map, attacker, defender);
        Utils.waitForSoldierToWinFight(attacker, map);

        // Verify the attacker was hurt during the fight
        assertTrue(attacker.getHealth() < attacker.getMaxHealth());

        int healthAfterFight = attacker.getHealth();

        // Step time frame-by-frame while the attacker walks to the flag and captures the building.
        // Assert that health does not increase at any point while outside.
        int stepsOutside = 0;
        while (!attacker.isInsideBuilding()) {
            map.stepTime();

            assertEquals("Attacker should not recover health while outside", healthAfterFight, attacker.getHealth());

            stepsOutside++;
        }

        assertTrue("Attacker should have spent time walking outside", stepsOutside > 0);
    }
}