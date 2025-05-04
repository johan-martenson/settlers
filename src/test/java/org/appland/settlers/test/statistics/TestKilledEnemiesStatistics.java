package org.appland.settlers.test.statistics;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.actors.Soldier.Rank.GENERAL_RANK;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.junit.Assert.*;

public class TestKilledEnemiesStatistics {

    @Test
    public void testKilledEnemiesAtStartIsZero() throws InvalidUserActionException {

        // Create a single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarter.
        var headquarterPoint = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), headquarterPoint);

        // Verify that killed enemies statistics is zero at start
        var statisticsManager = map.getStatisticsManager();

        assertEquals(statisticsManager.getPlayerStatistics(player0).killedEnemies().getMeasurements().size(), 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).killedEnemies().getMeasurements().getLast().value(), 0);
    }

    @Test
    public void testKilledEnemiesStatisticsWhenEnemyIsKilled() throws Exception {

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

        Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

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

        // Verify that a monitoring event is sent when a soldier dies and the military statistics changes.
        var statisticsManager = map.getStatisticsManager();

        assertEquals(statisticsManager.getPlayerStatistics(player0).killedEnemies().getMeasurements().size(), 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).killedEnemies().getMeasurements().getFirst().time(), 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).killedEnemies().getMeasurements().getFirst().value(), 0);

        for (int i = 0; i < 1000; i++) {
            if (defender.isDead()) {
                break;
            }

            assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
            assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Soldier.class, player1).size(), 1);

            map.stepTime();
        }

        assertEquals(statisticsManager.getPlayerStatistics(player0).killedEnemies().getMeasurements().size(), 2);
        assertTrue(statisticsManager.getPlayerStatistics(player0).killedEnemies().getMeasurements().getLast().time() > 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).killedEnemies().getMeasurements().getLast().value(), 1);

        map.stepTime();

        assertTrue(barracks1.isUnderAttack());
        assertTrue(defender.isDead());
        assertFalse(attacker.isFighting());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
    }
}
