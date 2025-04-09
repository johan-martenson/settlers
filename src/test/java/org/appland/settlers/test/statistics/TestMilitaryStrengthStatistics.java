package org.appland.settlers.test.statistics;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.CatapultWorker;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Catapult;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.GENERAL_RANK;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.junit.Assert.*;

public class TestMilitaryStrengthStatistics {

    @Test
    public void testInitialMilitaryStrength() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        assertEquals(headquarter0.getAmount(Material.PRIVATE), 51);
        assertEquals(headquarter0.getAmount(Material.PRIVATE_FIRST_CLASS), 0);
        assertEquals(headquarter0.getAmount(Material.SERGEANT), 0);
        assertEquals(headquarter0.getAmount(Material.OFFICER), 0);
        assertEquals(headquarter0.getAmount(Material.GENERAL), 0);

        // Verify that the initial measurement of military strength is correct.
        var statisticsManager = map.getStatisticsManager();

        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().size(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getFirst().time(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getFirst().value(), 51);
    }

    @Test
    public void testMilitaryStrengthIncreasesWhenSoldierIsDraftedInHeadquarters() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Put beer, a sword, and a shield in the inventory.
        assertEquals(headquarter0.getAmount(Material.PRIVATE), 51);

        Utils.adjustInventoryTo(headquarter0, Material.BEER, 1);
        Utils.adjustInventoryTo(headquarter0, Material.SHIELD, 1);
        Utils.adjustInventoryTo(headquarter0, Material.SWORD, 1);

        // Verify that military statistics are updated when a soldier is drafted
        var statisticsManager = map.getStatisticsManager();

        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().size(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getFirst().time(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getFirst().value(), 51);

        Utils.waitForBuildingToHave(headquarter0, Material.PRIVATE, 52);

        assertEquals(headquarter0.getAmount(Material.PRIVATE), 52);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().size(), 2);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getFirst().time(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getFirst().value(), 51);
        assertTrue(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getLast().time() > 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getLast().value(), 52);
    }

    @Test
    public void testMilitaryStrengthIncreasesWhenSoldierIsDraftedInStorehouse() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        Utils.adjustInventoryTo(headquarter0, Material.BEER, 0);
        Utils.adjustInventoryTo(headquarter0, Material.SWORD, 0);
        Utils.adjustInventoryTo(headquarter0, Material.SHIELD, 0);

        // Place storehouse, connect it to the headquarters, and wait for it to get constructed and occupied.
        var point1 = new Point(10, 4);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point1);

        var road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse0);

        // Put beer, a sword, and a shield in the inventory of the storehouse.
        assertEquals(storehouse0.getAmount(Material.PRIVATE), 0);

        Utils.adjustInventoryTo(storehouse0, Material.BEER, 1);
        Utils.adjustInventoryTo(storehouse0, Material.SHIELD, 1);
        Utils.adjustInventoryTo(storehouse0, Material.SWORD, 1);

        // Verify that military statistics are updated when a soldier is drafted
        var statisticsManager = map.getStatisticsManager();

        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().size(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getFirst().time(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getFirst().value(), 51);

        Utils.waitForBuildingToHave(storehouse0, Material.PRIVATE, 1);

        assertEquals(storehouse0.getAmount(Material.PRIVATE), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().size(), 2);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getFirst().time(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getFirst().value(), 51);
        assertTrue(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getLast().time() > 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getLast().value(), 52);
    }

    @Test
    public void testMilitaryStrengthStatisticsIsUpdatedWhenASoldierDiesWhileDefending() throws Exception {

        // Create player list with two players.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);

        // Create game map choosing two players.
        var map = new GameMap(List.of(player0, player1), 100, 100);

        // Place player 0's headquarters.
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters.
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Clear soldiers from the inventories.
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        // Place barracks for player 0.
        var point2 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point2);

        // Place barracks for player 1.
        var point3 = new Point(23, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point3);

        // Finish construction.
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        // Populate player 0's barracks.
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        // Populate player 1's barracks
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        // Order an attack.
        assertTrue(player0.canAttack(barracks1));
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 2);

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        // Find the military that was chosen to attack.
        map.stepTime();

        var attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        // Verify that a military leaves the attacked building to defend when the attacker reaches the flag
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 2);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);

        // Wait for the defender to go to the attacker.
        var defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        // Verify that the military strength for player 1 is updated when its soldier dies.
        var statisticsManager = map.getStatisticsManager();

        assertEquals(statisticsManager.getGeneralStatistics(player1).soldiers().getMeasurements().size(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player1).soldiers().getMeasurements().getFirst().time(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player1).soldiers().getMeasurements().getFirst().value(), 51);

        for (int i = 0; i < 1000; i++) {
            if (defender.isDead()) {
                break;
            }

            assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
            assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Soldier.class, player1).size(), 1);

            map.stepTime();
        }

        assertEquals(statisticsManager.getGeneralStatistics(player1).soldiers().getMeasurements().size(), 2);
        assertTrue(statisticsManager.getGeneralStatistics(player1).soldiers().getMeasurements().getLast().time() > 1);
        assertEquals(statisticsManager.getGeneralStatistics(player1).soldiers().getMeasurements().getLast().value(), 50);

        map.stepTime();

        assertTrue(barracks1.isUnderAttack());
        assertTrue(defender.isDead());
        assertFalse(attacker.isFighting());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
    }

    @Test
    public void testMilitaryStrengthIsUpdatedWhenSoldierDiesFromCatapult() throws Exception {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player0, player1), 100, 100);

        // Place headquarter
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place headquarter
        var point1 = new Point(45, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place barracks
        var point2 = new Point(35, 5);
        var barracks0 = map.placeBuilding(new Barracks(player1), point2);

        // Finish construction of the barracks.
        Utils.constructHouse(barracks0);

        // Occupy the barracks with 2 soldiers.
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);

        // Place catapult
        var point3 = new Point(21, 5);
        var catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult);

        /* Occupy the catapult */
        var catapultWorker0 = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        // Remove all the stones in the headquarters
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        // Verify that the military strength is updated when the catapult kills a soldier.
        var generalStatistics = map.getStatisticsManager().getGeneralStatistics(player1);

        var soldiersBeforeHit = generalStatistics.soldiers().getMeasurements().getLast().value();

        for (int i = 0; i < 100; i++) {

            // Deliver stone to the catapult
            catapult.putCargo(new Cargo(STONE, map));

            // Wait for the catapult to throw a projectile
            var projectile = Utils.waitForCatapultToThrowProjectile(catapult);

            // Wait for the projectile to reach its target
            assertEquals(barracks0.getHostedSoldiers().size(), 2);
            assertEquals(generalStatistics.soldiers().getMeasurements().getLast().value(), soldiersBeforeHit);

            Utils.waitForProjectileToReachTarget(projectile, map);

            /* Check if the projectile hit and destroyed the barracks */
            if (barracks0.getHostedSoldiers().size() == 1) {
                break;
            }
        }

        assertEquals(barracks0.getHostedSoldiers().size(), 1);
        assertEquals(generalStatistics.soldiers().getMeasurements().getLast().value(), soldiersBeforeHit - 1);
    }

    @Test
    public void testMonitoringStatisticsWhenSoldierDies() throws Exception {

        // Create player list with two players.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);

        // Create game map choosing two players.
        var map = new GameMap(List.of(player0, player1), 100, 100);

        // Place player 0's headquarters.
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters.
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Clear soldiers from the inventories.
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        // Place barracks for player 0.
        var point2 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point2);

        // Place barracks for player 1.
        var point3 = new Point(23, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point3);

        // Finish construction.
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        // Populate player 0's barracks.
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        // Populate player 1's barracks
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        // Order an attack.
        assertTrue(player0.canAttack(barracks1));
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 2);

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        // Find the military that was chosen to attack.
        map.stepTime();

        var attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        // Verify that a military leaves the attacked building to defend when the attacker reaches the flag
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 2);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);

        // Wait for the defender to go to the attacker.
        var defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        // Start monitoring statistics
        var monitor = new Utils.GameViewMonitor();

        map.getStatisticsManager().addListener(monitor);

        // Verify that a monitoring event is sent when a soldier dies and the military statistics changes.
        var statisticsManager = map.getStatisticsManager();

        assertEquals(statisticsManager.getGeneralStatistics(player1).soldiers().getMeasurements().size(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player1).soldiers().getMeasurements().getFirst().time(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player1).soldiers().getMeasurements().getFirst().value(), 51);
        assertEquals(monitor.getStatisticsEvents().size(), 0);

        for (int i = 0; i < 1000; i++) {
            if (defender.isDead()) {
                break;
            }

            assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
            assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Soldier.class, player1).size(), 1);

            map.stepTime();
        }

        assertEquals(statisticsManager.getGeneralStatistics(player1).soldiers().getMeasurements().size(), 2);
        assertTrue(statisticsManager.getGeneralStatistics(player1).soldiers().getMeasurements().getLast().time() > 1);
        assertEquals(statisticsManager.getGeneralStatistics(player1).soldiers().getMeasurements().getLast().value(), 50);
        assertTrue(monitor.getStatisticsEvents().size() >= 1); // Player0: killed enemies++, Player1: militaryStrength--

        map.stepTime();

        assertTrue(barracks1.isUnderAttack());
        assertTrue(defender.isDead());
        assertFalse(attacker.isFighting());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
    }
}
