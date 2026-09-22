package org.appland.settlers.test.donkeybreeder;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.DonkeyBreeder;
import org.appland.settlers.model.buildings.DonkeyFarm;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestDonkeys {

    @Test
    public void testUnoccupiedDonkeyBreederHasNoDonkey() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        // Place donkey breeder and wait for it to get constructed
        var donkeyBreederBuilding =
                map.placeBuilding(new DonkeyFarm(player0), new Point(10, 6));
        var road0 = map.placeAutoSelectedRoad(
                player0,
                donkeyBreederBuilding.getFlag(),
                headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(donkeyBreederBuilding);

        // Remove the road to the donkey breeder to keep it unoccupied
        map.removeRoad(road0);

        // Verify that the unoccupied donkey breeder has no donkeys
        Utils.fastForward(200, map, () -> {
            assertTrue(donkeyBreederBuilding.isUnoccupied());
            assertEquals(0, donkeyBreederBuilding.getDonkeys().size());
        });
    }

    @Test
    public void testDonkeyBreederShowsNoDonkeysAt0To30PercentProductivity() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        // Place donkey breeder and wait for it to get constructed and occupied
        var donkeyBreederBuilding =
                map.placeBuilding(new DonkeyFarm(player0), new Point(10, 6));
        map.placeAutoSelectedRoad(
                player0,
                donkeyBreederBuilding.getFlag(),
                headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(donkeyBreederBuilding);

        var donkeyBreeder =
                (DonkeyBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(donkeyBreederBuilding);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Fill the inventory of the donkey breeder
        Utils.deliverCargos(donkeyBreederBuilding, WATER, 6);
        Utils.deliverCargos(donkeyBreederBuilding, WHEAT, 6);

        // Let the donkey breeder work for the first productivity measurement
        Utils.fastForward(160, map);

        // The first 16-second measurement is averaged with six zero measurements
        assertTrue(donkeyBreederBuilding.getProductivity() >= 0);
        assertTrue(donkeyBreederBuilding.getProductivity() < 30);

        // Verify that 0-29% productivity shows no donkeys
        assertEquals(0, donkeyBreederBuilding.getDonkeys().size());
    }

    @Test
    public void testDonkeyBreederShowsOneDonkeyAt30To60PercentProductivity() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        // Place donkey breeder and wait for it to get constructed and occupied
        var donkeyBreederBuilding =
                map.placeBuilding(new DonkeyFarm(player0), new Point(10, 6));
        map.placeAutoSelectedRoad(
                player0,
                donkeyBreederBuilding.getFlag(),
                headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(donkeyBreederBuilding);

        var donkeyBreeder =
                (DonkeyBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(donkeyBreederBuilding);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Fill the inventory of the donkey breeder
        Utils.deliverCargos(donkeyBreederBuilding, WATER, 6);
        Utils.deliverCargos(donkeyBreederBuilding, WHEAT, 6);

        // Let the donkey breeder complete three productivity measurements
        Utils.fastForwardUntil(map, () -> donkeyBreederBuilding.getProductivity() >= 30);

        // The rolling average is now (100 + 100 + 100) / 7 = 42%
        assertTrue(donkeyBreederBuilding.getProductivity() >= 30);
        assertTrue(donkeyBreederBuilding.getProductivity() < 60);

        // Verify that 30-59% productivity shows one donkey
        assertEquals(1, donkeyBreederBuilding.getDonkeys().size());

        assertEquals(
                DonkeyFarm.DonkeySlot.SLOT_1,
                donkeyBreederBuilding.getDonkeys().getFirst().slot());
    }

    @Test
    public void testDonkeyBreederShowsTwoDonkeysAt60To90PercentProductivity() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        // Place donkey breeder and wait for it to get constructed and occupied
        var donkeyBreederBuilding =
                map.placeBuilding(new DonkeyFarm(player0), new Point(10, 6));
        map.placeAutoSelectedRoad(
                player0,
                donkeyBreederBuilding.getFlag(),
                headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(donkeyBreederBuilding);

        var donkeyBreeder =
                (DonkeyBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(donkeyBreederBuilding);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Fill the inventory of the donkey breeder
        Utils.deliverCargos(donkeyBreederBuilding, WATER, 6);
        Utils.deliverCargos(donkeyBreederBuilding, WHEAT, 6);

        // Let the donkey breeder complete five productivity measurements
        Utils.fastForwardUntil(map, () -> donkeyBreederBuilding.getProductivity() >= 60);

        // The rolling average is now (100 + 100 + 100 + 100 + 100) / 7 = 71%
        assertTrue(donkeyBreederBuilding.getProductivity() >= 60);
        assertTrue(donkeyBreederBuilding.getProductivity() < 90);

        // Verify that 60-89% productivity shows two donkeys
        assertEquals(2, donkeyBreederBuilding.getDonkeys().size());

        assertEquals(
                DonkeyFarm.DonkeySlot.SLOT_1,
                donkeyBreederBuilding.getDonkeys().getFirst().slot());
        assertEquals(
                DonkeyFarm.DonkeySlot.SLOT_2,
                donkeyBreederBuilding.getDonkeys().get(1).slot());
    }

    @Test
    public void testDonkeyBreederShowsThreeDonkeysAt90To100PercentProductivity() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        // Place donkey breeder and wait for it to get constructed and occupied
        var donkeyBreederBuilding =
                map.placeBuilding(new DonkeyFarm(player0), new Point(10, 6));
        map.placeAutoSelectedRoad(
                player0,
                donkeyBreederBuilding.getFlag(),
                headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(donkeyBreederBuilding);

        var donkeyBreeder =
                (DonkeyBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(donkeyBreederBuilding);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Fill the inventory of the donkey breeder
        Utils.deliverCargos(donkeyBreederBuilding, WATER, 6);
        Utils.deliverCargos(donkeyBreederBuilding, WHEAT, 6);

        // Let the donkey breeder complete seven productivity measurements
        Utils.fastForwardUntil(map, () -> {
            Utils.deliverCargoIfNeeded(donkeyBreederBuilding, WATER);
            Utils.deliverCargoIfNeeded(donkeyBreederBuilding, WHEAT);

            return donkeyBreederBuilding.getProductivity() >= 90;
        });

        // Verify that productivity is in the 90-100% range
        assertTrue(donkeyBreederBuilding.getProductivity() >= 90);
        assertTrue(donkeyBreederBuilding.getProductivity() <= 100);

        // Verify that 90-100% productivity shows three donkeys
        assertEquals(3, donkeyBreederBuilding.getDonkeys().size());

        assertEquals(
                DonkeyFarm.DonkeySlot.SLOT_1,
                donkeyBreederBuilding.getDonkeys().getFirst().slot());
        assertEquals(
                DonkeyFarm.DonkeySlot.SLOT_2,
                donkeyBreederBuilding.getDonkeys().get(1).slot());
        assertEquals(
                DonkeyFarm.DonkeySlot.SLOT_3,
                donkeyBreederBuilding.getDonkeys().get(2).slot());
    }
}
