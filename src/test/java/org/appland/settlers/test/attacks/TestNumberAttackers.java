package org.appland.settlers.test.attacks;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.junit.Assert.*;

public class TestNumberAttackers {


    @Test
    public void testAmountAttackersFromHeadquarters() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(20, 12);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Control amount of soldiers for player 0
        Utils.setReserves(headquarter0, 0, 0, 0, 0, 0);

        Utils.removeAllSoldiersFromStorage(headquarter0);

        // Verify that player 0 can't attack player 1
        assertFalse(player0.canAttack(headquarter1));
        assertEquals(player0.getNumberOfAvailableAttackers(headquarter1), 0);

        try {
            player0.attack(headquarter1, 1, AttackStrength.STRONG);

            fail();
        } catch (InvalidUserActionException e) { }

        // Give player 0 a private
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 1);

        // Verify that player 0 can attack player 1
        assertTrue(player0.canAttack(headquarter1));
        assertEquals(player0.getNumberOfAvailableAttackers(headquarter1), 1);

        try {
            player0.attack(headquarter1, 2, AttackStrength.STRONG);

            fail();
        } catch (InvalidUserActionException e) { }

        // Verify that the right amount of attackers come out
        player0.attack(headquarter1, 1, AttackStrength.STRONG);

        var attacker = Utils.waitForSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertFalse(attacker.isInsideBuilding());
        assertFalse(attacker.isDead());
        assertEquals(attacker.getPlayer(), player0);
        assertTrue(attacker.isSoldier());

        for (int i = 0; i < 20_000; i++) {
            if (map.getWorkers().stream()
                    .filter(Worker::isSoldier)
                    .filter(soldier -> soldier.getPlayer().equals(player0))
                    .noneMatch(soldier -> !soldier.isInsideBuilding() && !soldier.isDead())) {
                break;
            }

            assertEquals(
                    1,
                    map.getWorkers().stream()
                            .filter(Worker::isSoldier)
                            .filter(soldier -> !soldier.isInsideBuilding())
                            .filter(soldier -> !soldier.isDead())
                            .filter(soldier -> soldier.getPlayer().equals(player0))
                            .count()
                    );

            map.stepTime();
        }

        assertTrue(map.getWorkers().stream()
                .filter(Worker::isSoldier)
                .filter(worker -> worker.getPlayer().equals(player0))
                .noneMatch(soldier -> !soldier.isInsideBuilding() && !soldier.isDead()));
    }

    @Test
    public void testCannotAttackWithMoreThanAvailableSoldiers() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(20, 12);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Control amount of soldiers for player 0
        Utils.removeAllSoldiersFromStorage(headquarter0);

        Utils.adjustInventoryTo(headquarter0, GENERAL, 10);

        // Place fortress for player 1
        var point2 = new Point(18, 8);
        var fortress = map.placeBuilding(new Fortress(player1), point2);

        //  Connect the fortress with the headquarters and wait for it to get populated and occupied
        var road0 = map.placeAutoSelectedRoad(player1, fortress.getFlag(), headquarter1.getFlag());

        Utils.waitForBuildingToBeConstructed(fortress);

        Utils.waitForMilitaryBuildingToGetPopulated(fortress, 9);

        // Verify that it's not possible to attack with more than the available soldiers for attacking
        assertEquals(player0.getNumberOfAvailableAttackers(fortress), 10);

        try {
            player0.attack(fortress, 11, AttackStrength.STRONG);

            fail();
        } catch (InvalidUserActionException e) { }
    }


    @Test
    public void testNeedToAttackWithMoreThanZeroSoldiers() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(20, 12);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Control amount of soldiers for player 0
        Utils.removeAllSoldiersFromStorage(headquarter0);

        Utils.adjustInventoryTo(headquarter0, GENERAL, 10);

        // Place fortress for player 1
        var point2 = new Point(18, 8);
        var fortress = map.placeBuilding(new Fortress(player1), point2);

        //  Connect the fortress with the headquarters and wait for it to get populated and occupied
        var road0 = map.placeAutoSelectedRoad(player1, fortress.getFlag(), headquarter1.getFlag());

        Utils.waitForBuildingToBeConstructed(fortress);

        Utils.waitForMilitaryBuildingToGetPopulated(fortress, 9);

        // Verify that it's not possible to attack zero or negative amounts of attackers
        assertEquals(player0.getNumberOfAvailableAttackers(fortress), 10);

        try {
            player0.attack(fortress, 0, AttackStrength.STRONG);

            fail();
        } catch (InvalidUserActionException e) { }

        try {
            player0.attack(fortress, -1, AttackStrength.STRONG);

            fail();
        } catch (InvalidUserActionException e) { }
    }

    @Test
    public void testSoldiersInHeadquartersAreReducedWhenTheyLeaveToAttack() throws InvalidUserActionException {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(20, 12);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Control amount of soldiers for player 0
        Utils.removeAllSoldiersFromStorage(headquarter0);

        Utils.adjustInventoryTo(headquarter0, GENERAL, 10);

        // Place fortress for player 1
        var point2 = new Point(18, 8);
        var fortress = map.placeBuilding(new Fortress(player1), point2);

        //  Connect the fortress with the headquarters and wait for it to get populated and occupied
        var road0 = map.placeAutoSelectedRoad(player1, fortress.getFlag(), headquarter1.getFlag());

        Utils.waitForBuildingToBeConstructed(fortress);

        Utils.waitForMilitaryBuildingToGetPopulated(fortress, 9);

        // Verify that the amount of soldiers in the headquarters are reduced when they leave to attack
        assertEquals(player0.getNumberOfAvailableAttackers(fortress), 10);
        assertEquals(headquarter0.getAmount(GENERAL), 10);

        player0.attack(fortress, 3, AttackStrength.STRONG);

        Utils.waitForAliveSoldiersOutsideBuilding(player0, 3);

        assertEquals(headquarter0.getAmount(GENERAL), 7);
    }
}
