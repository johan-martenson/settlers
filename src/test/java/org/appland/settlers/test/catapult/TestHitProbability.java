package org.appland.settlers.test.catapult;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Catapult;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.test.Utils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.appland.settlers.model.Material.STONE;
import static org.junit.Assert.*;

public class TestHitProbability {

    /*
    TODO:
     - Test probabilites of hit for distance 4, 5
     - Test that the catapult can't throw for a distance shorter than 4

     Distance 4 - 	9	9/18 = 50.0%
     Distance 5	-   8	8/18 = 44.4%
     */

    @Test
    public void testCatapultHitProbabilityAtDistance4() throws Exception {
        var hits = 0;
        var misses = 0;

        // Fire many independent shots
        for (var i = 0; i < 500; i++) {

            // Create new game map
            var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
            var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
            var map = new GameMap(List.of(player0, player1), 100, 101);

            // Place headquarters
            var point0 = new Point(15, 11);
            var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            Utils.adjustInventoryTo(headquarter0, STONE, 19);

            // Place headquarters
            var point1 = new Point(41, 25);
            var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

            // Place barracks six journeys from the catapult and wait for it to get constructed and occupied
            // Make it diagonally down from the headquarters, as close to the border as possible
            var point2 = new Point(34, 18);
            var barracks0 = map.placeBuilding(new Barracks(player1), point2);
            var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks0);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

            // Place fortress for player 0
            // Make it diagonally down-left from the barracks, as close to the border as possible
            var point3 = new Point(25, 9);
            var fortress0 = map.placeBuilding(new Fortress(player0), point3);
            var road1 = map.placeAutoSelectedRoad(player0, fortress0.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(fortress0);

            Utils.waitForMilitaryBuildingToGetPopulated(fortress0);

            // Place catapult six journeys from the barracks and wait for it to get constructed and occupied
            // Make it diagonally up-right from the fortress, as close to the border as possible
            var point4 = new Point(30, 14);
            var catapult = map.placeBuilding(new Catapult(player0), point4);
            var road2 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(catapult);

            var catapultWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(catapult);

            assertTrue(catapultWorker0.isInsideBuilding());
            assertEquals(catapultWorker0.getHome(), catapult);
            assertEquals(catapult.getWorker(), catapultWorker0);
            assertEquals(4, GameUtils.distanceInGameSteps(catapult.getPosition(), barracks0.getPosition()));

            // Deliver stones to the catapult
            Utils.adjustInventoryTo(headquarter0, STONE, 1);

            Utils.waitForBuildingToHave(catapult, STONE, 1);

            // Wait for the projectile to be launched
            while (map.getProjectiles().isEmpty()) {
                map.stepTime();
            }

            var projectile = map.getProjectiles().getFirst();

            // Wait for the projectile to hit or miss
            var soldiersBeforeHit = barracks0.getNumberOfHostedSoldiers();
            while (map.getProjectiles().contains(projectile)) {
                assertEquals(soldiersBeforeHit, barracks0.getNumberOfHostedSoldiers());

                map.stepTime();
            }

            // Count the outcome
            if (barracks0.getNumberOfHostedSoldiers() == soldiersBeforeHit - 1) {
                hits++;
            } else {
                misses++;
            }
        }

        var hitProbability = hits / (double)(hits + misses);

        // Verify the hit probability
        assertTrue(hitProbability >= 0.47);
        assertTrue(hitProbability <= 0.53);
    }

    @Test
    public void testCatapultHitProbabilityAtDistance5() throws Exception {
        var hits = 0;
        var misses = 0;

        // Fire many independent shots
        for (var i = 0; i < 500; i++) {

            // Create new game map
            var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
            var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
            var map = new GameMap(List.of(player0, player1), 100, 101);

            // Place headquarters
            var point0 = new Point(15, 11);
            var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            Utils.adjustInventoryTo(headquarter0, STONE, 19);

            // Place headquarters
            var point1 = new Point(41, 25);
            var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

            // Place barracks six journeys from the catapult and wait for it to get constructed and occupied
            // Make it diagonally down from the headquarters, as close to the border as possible
            var point2 = new Point(34, 18);
            var barracks0 = map.placeBuilding(new Barracks(player1), point2);
            var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks0);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

            // Place fortress for player 0
            // Make it diagonally down-left from the barracks, as close to the border as possible
            var point3 = new Point(25, 9);
            var fortress0 = map.placeBuilding(new Fortress(player0), point3);
            var road1 = map.placeAutoSelectedRoad(player0, fortress0.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(fortress0);

            Utils.waitForMilitaryBuildingToGetPopulated(fortress0);

            // Place catapult six journeys from the barracks and wait for it to get constructed and occupied
            // Make it diagonally up-right from the fortress, as close to the border as possible
            var point4 = new Point(29, 13);
            var catapult = map.placeBuilding(new Catapult(player0), point4);
            var road2 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(catapult);

            var catapultWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(catapult);

            assertTrue(catapultWorker0.isInsideBuilding());
            assertEquals(catapultWorker0.getHome(), catapult);
            assertEquals(catapult.getWorker(), catapultWorker0);
            assertEquals(5, GameUtils.distanceInGameSteps(catapult.getPosition(), barracks0.getPosition()));

            // Deliver stones to the catapult
            Utils.adjustInventoryTo(headquarter0, STONE, 1);

            Utils.waitForBuildingToHave(catapult, STONE, 1);

            // Wait for the projectile to be launched
            while (map.getProjectiles().isEmpty()) {
                map.stepTime();
            }

            var projectile = map.getProjectiles().getFirst();

            // Wait for the projectile to hit or miss
            var soldiersBeforeHit = barracks0.getNumberOfHostedSoldiers();
            while (map.getProjectiles().contains(projectile)) {
                assertEquals(soldiersBeforeHit, barracks0.getNumberOfHostedSoldiers());

                map.stepTime();
            }

            // Count the outcome
            if (barracks0.getNumberOfHostedSoldiers() == soldiersBeforeHit - 1) {
                hits++;
            } else {
                misses++;
            }
        }

        var hitProbability = hits / (double)(hits + misses);

        // Verify the hit probability
        assertTrue(hitProbability >= 0.41);
        assertTrue(hitProbability <= 0.47);
    }

    @Test
    public void testCatapultHitProbabilityAtDistance6() throws Exception {
        var hits = 0;
        var misses = 0;

        // Fire many independent shots
        for (var i = 0; i < 500; i++) {

            // Create new game map
            var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
            var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
            var map = new GameMap(List.of(player0, player1), 100, 101);

            // Place headquarters
            var point0 = new Point(9, 11);
            var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            Utils.adjustInventoryTo(headquarter0, STONE, 2);

            // Place headquarters
            var point1 = new Point(41, 5);
            var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

            // Place barracks six journeys from the catapult and wait for it to get constructed and occupied
            var point2 = new Point(25, 5);
            var barracks0 = map.placeBuilding(new Barracks(player1), point2);
            var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks0);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

            // Place barracks for player 0
            var point3 = new Point(17, 11);
            var barracks1 = map.placeBuilding(new Barracks(player0), point3);
            var road1 = map.placeAutoSelectedRoad(player0, barracks1.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks1);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks1);

            // Place catapult six journeys from the barracks and wait for it to get constructed and occupied
            var point4 = new Point(21, 11);
            var catapult = map.placeBuilding(new Catapult(player0), point4);
            var road2 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(catapult);

            var catapultWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(catapult);

            assertTrue(catapultWorker0.isInsideBuilding());
            assertEquals(catapultWorker0.getHome(), catapult);
            assertEquals(catapult.getWorker(), catapultWorker0);
            assertEquals(6, GameUtils.distanceInGameSteps(catapult.getPosition(), barracks0.getPosition()));

            // Deliver stones to the catapult
            Utils.adjustInventoryTo(headquarter0, STONE, 1);

            Utils.waitForBuildingToHave(catapult, STONE, 1);

            // Wait for the projectile to be launched
            while (map.getProjectiles().isEmpty()) {
                map.stepTime();
            }

            var projectile = map.getProjectiles().getFirst();

            // Wait for the projectile to hit or miss
            var soldiersBeforeHit = barracks0.getNumberOfHostedSoldiers();
            while (map.getProjectiles().contains(projectile)) {
                assertEquals(soldiersBeforeHit, barracks0.getNumberOfHostedSoldiers());

                map.stepTime();
            }

            // Count the outcome
            if (barracks0.getNumberOfHostedSoldiers() == soldiersBeforeHit - 1) {
                hits++;
            } else {
                misses++;
            }
        }

        var hitProbability = hits / (double)(hits + misses);

        // Verify the hit probability
        assertTrue(hitProbability >= 0.37);
        assertTrue(hitProbability <= 0.41);
    }

    @Test
    public void testCatapultHitProbabilityAtDistance7() throws Exception {
        var hits = 0;
        var misses = 0;

        // Fire many independent shots
        for (var i = 0; i < 500; i++) {

            // Create new game map
            var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
            var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
            var map = new GameMap(List.of(player0, player1), 100, 101);

            // Place headquarters
            var point0 = new Point(9, 17);
            var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            Utils.adjustInventoryTo(headquarter0, STONE, 9);

            // Place headquarters
            var point1 = new Point(33, 5);
            var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

            // Place barracks seven journeys from the catapult and wait for it to get constructed and occupied
            var point2 = new Point(19, 7);
            var barracks0 = map.placeBuilding(new Barracks(player1), point2);
            var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks0);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

            // Place fortress for player 0
            var point3 = new Point(17, 17);
            var fortress0 = map.placeBuilding(new Fortress(player0), point3);
            var road1 = map.placeAutoSelectedRoad(player0, fortress0.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(fortress0);

            Utils.waitForMilitaryBuildingToGetPopulated(fortress0);

            // Place catapult seven journeys from the barracks and wait for it to get constructed and occupied
            var point4 = new Point(22, 14);
            var catapult = map.placeBuilding(new Catapult(player0), point4);
            var road2 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(catapult);

            var catapultWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(catapult);

            assertTrue(catapultWorker0.isInsideBuilding());
            assertEquals(catapultWorker0.getHome(), catapult);
            assertEquals(catapult.getWorker(), catapultWorker0);
            assertEquals(7, GameUtils.distanceInGameSteps(catapult.getPosition(), barracks0.getPosition()));
            assertTrue(catapult.isReady());
            assertTrue(barracks0.isReady());

            // Deliver stones to the catapult
            Utils.adjustInventoryTo(headquarter0, STONE, 1);

            Utils.waitForBuildingToHave(catapult, STONE, 1);

            // Wait for the projectile to be launched
            while (map.getProjectiles().isEmpty()) {
                map.stepTime();
            }

            var projectile = map.getProjectiles().getFirst();

            assertEquals(projectile.getTarget(), barracks0.getPosition());

            // Wait for the projectile to hit or miss
            while (map.getProjectiles().contains(projectile)) {
                assertEquals(barracks0.getNumberOfHostedSoldiers(), 2);

                map.stepTime();
            }

            // Count the outcome
            if (barracks0.getNumberOfHostedSoldiers() == 1) {
                hits++;
            } else {
                misses++;
            }
        }

        var hitProbability = hits / (double) (hits + misses);

        // Verify the hit probability
        assertTrue(hitProbability >= 0.31);
        assertTrue(hitProbability <= 0.36);
    }

    @Test
    public void testCatapultHitProbabilityAtDistance8() throws Exception {

        var hits = 0;
        var misses = 0;

        // Fire many independent shots
        for (var i = 0; i < 500; i++) {

            // Create new game map
            var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
            var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
            var map = new GameMap(List.of(player0, player1), 100, 101);

            // Place headquarters
            var point0 = new Point(9, 17);
            var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            Utils.adjustInventoryTo(headquarter0, STONE, 2);

            // Place headquarters
            var point1 = new Point(49, 5);
            var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

            // Place barracks eight journeys from the catapult and wait for it to get constructed and occupied
            var point2 = new Point(33, 5);
            var barracks0 = map.placeBuilding(new Barracks(player1), point2);
            var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks0);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

            // Place barracks for player 0
            var point3 = new Point(19, 13);
            var barracks1 = map.placeBuilding(new Barracks(player0), point3);
            var road1 = map.placeAutoSelectedRoad(player0, barracks1.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks1);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks1);

            // Place catapult eight journeys from the barracks and wait for it to get constructed and occupied
            var point4 = new Point(19, 7);
            var catapult = map.placeBuilding(new Catapult(player0), point4);
            var road2 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(catapult);

            var catapultWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(catapult);

            assertTrue(catapultWorker0.isInsideBuilding());
            assertEquals(catapultWorker0.getHome(), catapult);
            assertEquals(catapult.getWorker(), catapultWorker0);
            assertEquals(8, GameUtils.distanceInGameSteps(catapult.getPosition(), barracks0.getPosition()));

            // Deliver stones to the catapult
            Utils.adjustInventoryTo(headquarter0, STONE, 1);

            Utils.waitForBuildingToHave(catapult, STONE, 1);

            // Wait for the projectile to be launched
            while (map.getProjectiles().isEmpty()) {
                map.stepTime();
            }

            var projectile = map.getProjectiles().getFirst();

            // Wait for the projectile to hit or miss
            while (map.getProjectiles().contains(projectile)) {
                assertEquals(2, barracks0.getNumberOfHostedSoldiers());

                map.stepTime();
            }

            // Count the outcome
            if (barracks0.getNumberOfHostedSoldiers() == 1) {
                hits++;
            } else {
                misses++;
            }
        }

        var hitProbability = hits / (double) (hits + misses);

        // Verify the hit probability
        assertTrue(hitProbability >= 0.25);
        assertTrue(hitProbability <= 0.30);
    }

    @Test
    public void testCatapultHitProbabilityAtDistance9() throws Exception {
        var hits = 0;
        var misses = 0;

        // Fire many independent shots
        for (var i = 0; i < 500; i++) {

            // Create new game map
            var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
            var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
            var map = new GameMap(List.of(player0, player1), 100, 101);

            // Place headquarters
            var point0 = new Point(9, 17);
            var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            Utils.adjustInventoryTo(headquarter0, STONE, 2);

            // Place headquarters
            var point1 = new Point(49, 5);
            var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

            // Place barracks nine journeys from the catapult and wait for it to get constructed and occupied
            var point2 = new Point(33, 5);
            var barracks0 = map.placeBuilding(new Barracks(player1), point2);
            var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks0);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

            // Place barracks for player 0
            var point3 = new Point(16, 10);
            var barracks1 = map.placeBuilding(new Barracks(player0), point3);
            var road1 = map.placeAutoSelectedRoad(player0, barracks1.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks1);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks1);

            // Place catapult nine journeys from the barracks and wait for it to get constructed and occupied
            var point4 = new Point(20, 10);
            var catapult = map.placeBuilding(new Catapult(player0), point4);
            var road2 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(catapult);

            var catapultWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(catapult);

            assertTrue(catapultWorker0.isInsideBuilding());
            assertEquals(catapultWorker0.getHome(), catapult);
            assertEquals(catapult.getWorker(), catapultWorker0);
            assertEquals(9, GameUtils.distanceInGameSteps(catapult.getPosition(), barracks0.getPosition()));

            // Deliver stones to the catapult
            Utils.adjustInventoryTo(headquarter0, STONE, 1);

            Utils.waitForBuildingToHave(catapult, STONE, 1);

            // Wait for the projectile to be launched
            while (map.getProjectiles().isEmpty()) {
                map.stepTime();
            }

            var projectile = map.getProjectiles().getFirst();

            // Wait for the projectile to hit or miss
            while (map.getProjectiles().contains(projectile)) {
                assertEquals(2, barracks0.getNumberOfHostedSoldiers());

                map.stepTime();
            }

            // Count the outcome
            if (barracks0.getNumberOfHostedSoldiers() == 1) {
                hits++;
            } else {
                misses++;
            }
        }

        var hitProbability = hits / (double) (hits + misses);

        // Verify the hit probability
        assertTrue(hitProbability >= 0.20);
        assertTrue(hitProbability <= 0.25);
    }

    @Test
    public void testCatapultHitProbabilityAtDistance10() throws Exception {

        var hits = 0;
        var misses = 0;

        // Fire many independent shots
        for (var i = 0; i < 500; i++) {

            // Create new game map
            var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
            var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
            var map = new GameMap(List.of(player0, player1), 100, 101);

            // Place headquarters
            var point0 = new Point(9, 17);
            var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            Utils.adjustInventoryTo(headquarter0, STONE, 2);

            // Place headquarters
            var point1 = new Point(49, 5);
            var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

            // Place barracks ten journeys from the catapult and wait for it to get constructed and occupied
            var point2 = new Point(33, 5);
            var point4 = new Point(20, 12);

            var barracks0 = map.placeBuilding(new Barracks(player1), point2);
            var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks0);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

            // Place barracks for player 0
            var point3 = new Point(13, 17);
            var barracks1 = map.placeBuilding(new Barracks(player0), point3);
            var road1 = map.placeAutoSelectedRoad(player0, barracks1.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks1);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks1);

            // Place catapult ten journeys from the barracks and wait for it to get constructed and occupied
            var catapult = map.placeBuilding(new Catapult(player0), point4);
            var road2 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(catapult);

            var catapultWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(catapult);

            assertTrue(catapultWorker0.isInsideBuilding());
            assertEquals(catapultWorker0.getHome(), catapult);
            assertEquals(catapult.getWorker(), catapultWorker0);
            assertEquals(10, GameUtils.distanceInGameSteps(catapult.getPosition(), barracks0.getPosition()));

            // Deliver stones to the catapult
            Utils.adjustInventoryTo(headquarter0, STONE, 1);

            Utils.waitForBuildingToHave(catapult, STONE, 1);

            // Wait for the projectile to be launched
            while (map.getProjectiles().isEmpty()) {
                map.stepTime();
            }

            var projectile = map.getProjectiles().getFirst();

            // Wait for the projectile to hit or miss
            while (map.getProjectiles().contains(projectile)) {
                assertEquals(projectile.getTarget(), barracks0.getPosition());
                assertEquals(2, barracks0.getNumberOfHostedSoldiers());

                map.stepTime();
            }

            // Count the outcome
            if (barracks0.getNumberOfHostedSoldiers() == 1) {
                hits++;
            } else {
                misses++;
            }
        }

        var hitProbability = hits / (double) (hits + misses);

        // Verify the hit probability
        assertTrue(hitProbability >= 0.14);
        assertTrue(hitProbability <= 0.19);
    }

    @Test
    public void testCatapultHitProbabilityAtDistance11() throws Exception {
        var hits = 0;
        var misses = 0;

        // Fire many independent shots
        for (var i = 0; i < 500; i++) {

            // Create new game map
            var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
            var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
            var map = new GameMap(List.of(player0, player1), 100, 101);

            // Place headquarters
            var point0 = new Point(9, 17);
            var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            Utils.adjustInventoryTo(headquarter0, STONE, 2);

            // Place headquarters
            var point1 = new Point(49, 5);
            var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

            // Place barracks eleven journeys from the catapult and wait for it to get constructed and occupied
            var point2 = new Point(33, 5);
            var barracks0 = map.placeBuilding(new Barracks(player1), point2);
            var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks0);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

            // Place barracks for player 0
            var point3 = new Point(23, 17);
            var barracks1 = map.placeBuilding(new Barracks(player0), point3);
            var road1 = map.placeAutoSelectedRoad(player0, barracks1.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks1);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks1);

            // Place catapult eleven journeys from the barracks and wait for it to get constructed and occupied
            var point4 = new Point(20, 14);
            var catapult = map.placeBuilding(new Catapult(player0), point4);
            var road2 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(catapult);

            var catapultWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(catapult);

            assertTrue(catapultWorker0.isInsideBuilding());
            assertEquals(catapultWorker0.getHome(), catapult);
            assertEquals(catapult.getWorker(), catapultWorker0);
            assertEquals(11, GameUtils.distanceInGameSteps(catapult.getPosition(), barracks0.getPosition()));

            // Deliver stones to the catapult
            Utils.adjustInventoryTo(headquarter0, STONE, 1);

            Utils.waitForBuildingToHave(catapult, STONE, 1);

            // Wait for the projectile to be launched
            assertEquals(1, catapult.getAmount(STONE));
            assertNotNull(catapult.getWorker());
            assertNotEquals(player0, barracks0.getPlayer());
            assertTrue(barracks0.isReady());
            assertTrue(barracks0.isMilitaryBuilding());

            var projectile = Utils.waitForCatapultToThrowProjectile(catapult);

            // Wait for the projectile to hit or miss
            assertEquals(2, barracks0.getNumberOfHostedSoldiers());

            Utils.waitForProjectileToReachTarget(projectile, map);

            // Count the outcome
            if (barracks0.getNumberOfHostedSoldiers() == 1) {
                hits++;
            } else {
                misses++;
            }
        }

        var hitProbability = hits / (double) (hits + misses);

        // Verify the hit probability
        assertTrue(hitProbability >= 0.09);
        assertTrue(hitProbability <= 0.13);
    }

    @Test
    public void testCatapultHitProbabilityAtDistance12() throws Exception {

        var hits = 0;
        var misses = 0;

        // Fire many independent shots
        for (var i = 0; i < 500; i++) {

            // Create new game map
            var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
            var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
            var map = new GameMap(List.of(player0, player1), 100, 101);

            // Place headquarters
            var point0 = new Point(9, 17);
            var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            Utils.adjustInventoryTo(headquarter0, STONE, 2);

            // Place headquarters
            var point1 = new Point(49, 5);
            var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

            // Place barracks twelve journeys from the catapult and wait for it to get constructed and occupied
            var point2 = new Point(33, 5);
            var barracks0 = map.placeBuilding(new Barracks(player1), point2);
            var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks0);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

            // Place barracks for player 0
            var point3 = new Point(13, 17);
            var barracks1 = map.placeBuilding(new Barracks(player0), point3);
            var road1 = map.placeAutoSelectedRoad(player0, barracks1.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks1);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks1);

            // Place catapult twelve journeys from the barracks and wait for it to get constructed and occupied
            var point4 = new Point(19, 15);
            var catapult = map.placeBuilding(new Catapult(player0), point4);
            var road2 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(catapult);

            var catapultWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(catapult);

            assertTrue(catapultWorker0.isInsideBuilding());
            assertEquals(catapultWorker0.getHome(), catapult);
            assertEquals(catapult.getWorker(), catapultWorker0);
            assertEquals(12, GameUtils.distanceInGameSteps(catapult.getPosition(), barracks0.getPosition()));

            // Deliver stones to the catapult
            Utils.adjustInventoryTo(headquarter0, STONE, 1);

            Utils.waitForBuildingToHave(catapult, STONE, 1);

            // Wait for the projectile to be launched
            while (map.getProjectiles().isEmpty()) {
                map.stepTime();
            }

            var projectile = map.getProjectiles().getFirst();

            // Wait for the projectile to hit or miss
            while (map.getProjectiles().contains(projectile)) {
                assertEquals(2, barracks0.getNumberOfHostedSoldiers());

                map.stepTime();
            }

            // Count the outcome
            if (barracks0.getNumberOfHostedSoldiers() == 1) {
                hits++;
            } else {
                misses++;
            }
        }

        var hitProbability = hits / (double) (hits + misses);

        // Verify the hit probability
        assertTrue(hitProbability >= 0.04);
        assertTrue(hitProbability <= 0.07);
    }

    @Test
    public void testCatapultDoesNotFireAtDistance13() throws Exception {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place headquarters
        var point0 = new Point(9, 17);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        Utils.adjustInventoryTo(headquarter0, STONE, 2);

        // Place headquarters
        var point1 = new Point(53, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place barracks thirteen journeys from the catapult and wait for it to get constructed and occupied
        var point2 = new Point(37, 5);
        var barracks0 = map.placeBuilding(new Barracks(player1), point2);
        var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

        Utils.waitForBuildingToBeConstructed(barracks0);

        Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

        // Place barracks for player 0
        var point3 = new Point(13, 17);
        var barracks1 = map.placeBuilding(new Barracks(player0), point3);
        var road1 = map.placeAutoSelectedRoad(player0, barracks1.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(barracks1);

        Utils.waitForMilitaryBuildingToGetPopulated(barracks1);

        // Place catapult thirteen journeys from the barracks and wait for it to get constructed and occupied
        var point4 = new Point(17, 11);
        var catapult = map.placeBuilding(new Catapult(player0), point4);
        var road2 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(catapult);

        var catapultWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);
        assertEquals(13, GameUtils.distanceInGameSteps(catapult.getPosition(), barracks0.getPosition()));

        // Deliver stones to the catapult
        Utils.adjustInventoryTo(headquarter0, STONE, 1);

        Utils.waitForBuildingToHave(catapult, STONE, 1);

        // Verify that the catapult never fires
        for (var i = 0; i < 500; i++) {
            assertTrue(map.getProjectiles().isEmpty());
            assertEquals(2, barracks0.getNumberOfHostedSoldiers());

            map.stepTime();
        }

        assertTrue(map.getProjectiles().isEmpty());
        assertEquals(2, barracks0.getNumberOfHostedSoldiers());
        assertEquals(1, catapult.getAmount(STONE));
    }
}
