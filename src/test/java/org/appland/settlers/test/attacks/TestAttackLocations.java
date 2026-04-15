package org.appland.settlers.test.attacks;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.GameMap;
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
import static org.junit.Assert.*;

public class TestAttackLocations {

    @Test
    public void testAttackCloseToUpperEdge() throws Exception {

        // Create game with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(71, 95);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(51, 95);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Give player 0 lots of soldiers
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 100);

        // Give player 1 only generals
        headquarter1.setReservedSoldiers(Soldier.Rank.GENERAL_RANK, 0);
        Utils.clearSoldiersFromInventory(headquarter1);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 2);

        // Place barracks for player 1 and connect it to the headquarters
        var point2 = new Point(56, 98);
        var barracks0 = map.placeBuilding(new Barracks(player1), point2);
        var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

        // Wait for the barracks to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(barracks0);
        Utils.waitForMilitaryBuildingToGetPopulated(barracks0, 2);

        // Order an attack
        assertTrue(player0.canAttack(barracks0));

        player0.attack(barracks0, 20, AttackStrength.STRONG);

        // Find the 20 attackers
        var attackers = Utils.waitForAliveSoldiersOutsideBuilding(player0, 20);

        // Verify that they all reach their attacking positions and stay within the map
        Utils.waitForWorkersToStopWalking(attackers, map);

        Utils.printWorkers(attackers);

        for (var attacker : attackers) {
            assertTrue(attacker.getPosition().x >= 0);
            assertTrue(attacker.getPosition().y >= 0);
            assertTrue(attacker.getPosition().x < map.getWidth());
            assertTrue(attacker.getPosition().y < map.getHeight());
        }
    }

    @Test
    public void testAttackCloseToBottomEdge() throws Exception {

        // Create game with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(71, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(51, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Give player 0 lots of soldiers
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 100);

        // Give player 1 only generals
        headquarter1.setReservedSoldiers(Soldier.Rank.GENERAL_RANK, 0);
        Utils.clearSoldiersFromInventory(headquarter1);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 2);

        // Place barracks for player 1 and connect it to the headquarters
        var point2 = new Point(55, 3);
        var barracks0 = map.placeBuilding(new Barracks(player1), point2);
        var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

        // Wait for the barracks to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(barracks0);
        Utils.waitForMilitaryBuildingToGetPopulated(barracks0, 2);

        // Order an attack
        assertTrue(player0.canAttack(barracks0));

        player0.attack(barracks0, 20, AttackStrength.STRONG);

        // Find the 20 attackers
        var attackers = Utils.waitForAliveSoldiersOutsideBuilding(player0, 20);

        // Verify that they all reach their attacking positions and stay within the map
        Utils.waitForWorkersToStopWalking(attackers, map);

        Utils.printWorkers(attackers);

        for (var attacker : attackers) {
            assertTrue(attacker.getPosition().x >= 0);
            assertTrue(attacker.getPosition().y >= 0);
            assertTrue(attacker.getPosition().x < map.getWidth());
            assertTrue(attacker.getPosition().y < map.getHeight());
        }
    }

    @Test
    public void testAttackCloseToLeftEdge() throws Exception {

        // Create game with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(4, 30);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(4, 50);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Give player 0 lots of soldiers
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 100);

        // Give player 1 only generals
        headquarter1.setReservedSoldiers(Soldier.Rank.GENERAL_RANK, 0);
        Utils.clearSoldiersFromInventory(headquarter1);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 2);

        // Place barracks for player 1 and connect it to the headquarters
        var point2 = new Point(3, 45);
        var barracks0 = map.placeBuilding(new Barracks(player1), point2);
        var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

        // Wait for the barracks to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(barracks0);
        Utils.waitForMilitaryBuildingToGetPopulated(barracks0, 2);

        // Order an attack
        assertTrue(player0.canAttack(barracks0));

        player0.attack(barracks0, 20, AttackStrength.STRONG);

        // Find the 20 attackers
        var attackers = Utils.waitForAliveSoldiersOutsideBuilding(player0, 20);

        // Verify that they all reach their attacking positions and stay within the map
        Utils.waitForWorkersToStopWalking(attackers, map);

        // TODO: check that the soldiers are close to the building they are attacking...
        Utils.printWorkers(attackers);


        for (var attacker : attackers) {
            assertTrue(attacker.getPosition().x >= 0);
            assertTrue(attacker.getPosition().y >= 0);
            assertTrue(attacker.getPosition().x < map.getWidth());
            assertTrue(attacker.getPosition().y < map.getHeight());
        }
    }

    @Test
    public void testAttackCloseToRightEdge() throws Exception {

        // Create game with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(96, 30);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(96, 50);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Give player 0 lots of soldiers
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 100);

        // Give player 1 only generals
        headquarter1.setReservedSoldiers(Soldier.Rank.GENERAL_RANK, 0);
        Utils.clearSoldiersFromInventory(headquarter1);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 2);

        // Place barracks for player 1 and connect it to the headquarters
        var point2 = new Point(96, 44);
        var barracks0 = map.placeBuilding(new Barracks(player1), point2);
        var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

        // Wait for the barracks to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(barracks0);
        Utils.waitForMilitaryBuildingToGetPopulated(barracks0, 2);

        // Order an attack
        assertTrue(player0.canAttack(barracks0));

        player0.attack(barracks0, 20, AttackStrength.STRONG);

        // Find the 20 attackers
        var attackers = Utils.waitForAliveSoldiersOutsideBuilding(player0, 20);

        // Verify that they all reach their attacking positions and stay within the map
        Utils.waitForWorkersToStopWalking(attackers, map);

        Utils.printWorkers(attackers);

        for (var attacker : attackers) {
            assertTrue(attacker.getPosition().x >= 0);
            assertTrue(attacker.getPosition().y >= 0);
            assertTrue(attacker.getPosition().x < map.getWidth());
            assertTrue(attacker.getPosition().y < map.getHeight());
        }
    }

}
