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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TravelSpeed {

    private static final int EXPECTED_TRAVEL_TIME_DISTANCE_4 = 20;
    private static final int EXPECTED_TRAVEL_TIME_DISTANCE_5 = 25;
    private static final int EXPECTED_TRAVEL_TIME_DISTANCE_6 = 30;

    @Test
    public void testProjectileTravelTimeAtDistance6() throws Exception {

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

        // Place barracks four journeys from the catapult and wait for it to get constructed and occupied
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

        // Place catapult four journeys from the barracks and wait for it to get constructed and occupied
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

        // Measure the projectile travel time
        var travelTime = 0;

        while (map.getProjectiles().contains(projectile)) {
            map.stepTime();
            travelTime++;
        }

        // Verify the projectile travel time
        assertEquals(EXPECTED_TRAVEL_TIME_DISTANCE_6, travelTime);
    }

    @Test
    public void testProjectileTravelTimeAtDistance5() throws Exception {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        Utils.adjustInventoryTo(headquarter0, STONE, 9);

        // Place headquarters
        var point1 = new Point(45, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place barracks four journeys from the catapult and wait for it to get constructed and occupied
        var point2 = new Point(29, 5);
        var barracks0 = map.placeBuilding(new Barracks(player1), point2);
        var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

        Utils.waitForBuildingToBeConstructed(barracks0);

        Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

        // Place military building for player 0 to make it possible to place the catapult closer to the barracks
        var point4 = new Point(15, 7);
        var fortress0 = map.placeBuilding(new Fortress(player0), point4);
        var road2 = map.placeAutoSelectedRoad(player0, fortress0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(fortress0);

        Utils.waitForMilitaryBuildingToGetPopulated(fortress0);

        // Place catapult four journeys from the barracks and wait for it to get constructed and occupied
        var point3 = new Point(19, 5);
        var catapult = map.placeBuilding(new Catapult(player0), point3);
        var road1 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter0.getFlag());

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

        // Measure the projectile travel time
        var travelTime = 0;

        while (map.getProjectiles().contains(projectile)) {
            map.stepTime();
            travelTime++;
        }

        // Verify the projectile travel time
        assertEquals(EXPECTED_TRAVEL_TIME_DISTANCE_5, travelTime);
    }
}
