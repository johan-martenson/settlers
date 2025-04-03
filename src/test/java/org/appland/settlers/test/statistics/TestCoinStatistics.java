package org.appland.settlers.test.statistics;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Minter;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.junit.Assert.*;

public class TestCoinStatistics {

    @Test
    public void testInitialCoinStatisticsForDefaultResources() throws InvalidUserActionException {

        // Create a single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarter.
        var headquarterPoint = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), headquarterPoint);

        // Verify that the initial statistics for coins are correct.
        var statisticsManager = map.getStatisticsManager();

        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().value(), headquarter.getAmount(COIN));
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().time(), 1);
    }

    @Test
    public void testStatisticsChangesWhenCoinIsProduced() throws Exception {

        // Create a single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarter.
        var headquarterPoint = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), headquarterPoint);

        // Place mint.
        var mintPoint = new Point(7, 9);
        var mint = map.placeBuilding(new Mint(player0), mintPoint);

        // Connect the mint with the headquarters.
        var road0 = map.placeAutoSelectedRoad(player0, mint.getFlag(), headquarter.getFlag());

        // Finish construction of the mint.
        Utils.constructHouse(mint);

        // Populate the mint.
        var minter = Utils.occupyBuilding(new Minter(player0, map), mint);

        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getHome(), mint);
        assertEquals(mint.getWorker(), minter);

        // Deliver gold and coal to the mint
        mint.putCargo(new Cargo(GOLD, map));
        mint.putCargo(new Cargo(COAL, map));

        // Verify that the statistics for coins are updated when one is produced.
        var statisticsManager = map.getStatisticsManager();

        for (int i = 0; i < 149; i++) {
            map.stepTime();

            assertTrue(mint.getFlag().getStackedCargo().isEmpty());
            assertNull(minter.getCargo());
        }

        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().size(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().time(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().value(), 0);

        map.stepTime();

        assertNotNull(minter.getCargo());
        assertEquals(minter.getCargo().getMaterial(), COIN);
        assertTrue(mint.getFlag().getStackedCargo().isEmpty());
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().size(), 2);
        assertTrue(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().time() > 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().value(), 1);
    }

    @Test
    public void testCoinStatisticsAreNotChangedWhenACoinIsConsumed() throws InvalidUserActionException {

        // Create a single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Set the inventory for the headquarters.
        Utils.clearInventory(headquarter, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.adjustInventoryTo(headquarter, PRIVATE, 1);

        assertEquals(headquarter.getAmount(COIN), 0);

        // Place barracks, connect it to the headquarters, and wait for it to get finished and occupied
        var point1 = new Point(10, 10);
        var barracks = map.placeBuilding(new Barracks(player0), point1);

        var road0 = map.placeAutoSelectedRoad(player0, barracks.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(barracks);

        Utils.waitForMilitaryBuildingToGetPopulated(barracks);

        // Place mint, connect it with the headquarters, and wait for it to get constructed and occupied
        var mintPoint = new Point(7, 9);
        var mint = map.placeBuilding(new Mint(player0), mintPoint);

        var road1 = map.placeAutoSelectedRoad(player0, mint.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(mint);

        var minter = Utils.waitForNonMilitaryBuildingToGetPopulated(mint);

        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getHome(), mint);
        assertEquals(mint.getWorker(), minter);

        // Deliver gold and coal to the mint
        mint.putCargo(new Cargo(GOLD, map));
        mint.putCargo(new Cargo(COAL, map));

        // Wait for a coin to get produced
        var statisticsManager = map.getStatisticsManager();

        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().size(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().time(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().value(), 0);

        for (int i = 0; i < 149; i++) {
            map.stepTime();

            assertTrue(mint.getFlag().getStackedCargo().isEmpty());
            assertNull(minter.getCargo());
        }

        Utils.fastForwardUntilWorkerCarriesCargo(map, mint.getWorker(), COIN);

        map.stepTime();

        assertNotNull(minter.getCargo());
        assertEquals(minter.getCargo().getMaterial(), COIN);
        assertTrue(mint.getFlag().getStackedCargo().isEmpty());
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().size(), 2);
        assertTrue(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().time() > 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().value(), 1);

        // Wait for the barracks to receive the coin
        Utils.waitForBuildingToHave(barracks, COIN, 1);

        // Wait for the coin to get consumed when a soldier gets promoted
        assertEquals(barracks.getHostedSoldiers().size(), 1);
        assertEquals(barracks.getHostedSoldiers().getFirst().getRank(), Soldier.Rank.PRIVATE_RANK);
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().size(), 2);
        assertTrue(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().time() > 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().value(), 1);

        Utils.waitForBuildingToHave(barracks, COIN, 0);

        assertEquals(barracks.getHostedSoldiers().size(), 1);
        assertEquals(barracks.getHostedSoldiers().getFirst().getRank(), Soldier.Rank.PRIVATE_FIRST_CLASS_RANK);
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().size(), 2);
        assertTrue(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().time() > 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().value(), 1);
    }

    @Test
    public void testListenToCoinStatistics() throws InvalidUserActionException {

        // Create a single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarter.
        var headquarterPoint = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), headquarterPoint);

        // Place mint.
        var mintPoint = new Point(7, 9);
        var mint = map.placeBuilding(new Mint(player0), mintPoint);

        // Connect the mint with the headquarters.
        var road0 = map.placeAutoSelectedRoad(player0, mint.getFlag(), headquarter.getFlag());

        // Finish construction of the mint.
        Utils.constructHouse(mint);

        // Populate the mint.
        var minter = Utils.occupyBuilding(new Minter(player0, map), mint);

        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getHome(), mint);
        assertEquals(mint.getWorker(), minter);

        // Deliver gold and coal to the mint
        mint.putCargo(new Cargo(GOLD, map));
        mint.putCargo(new Cargo(COAL, map));

        // Start listening to statistics updates
        var monitor = new Utils.GameViewMonitor();

        map.getStatisticsManager().addListener(monitor);

        // Verify that the statistics for coins are updated when one is produced.
        var statisticsManager = map.getStatisticsManager();

        for (int i = 0; i < 149; i++) {
            map.stepTime();

            assertTrue(mint.getFlag().getStackedCargo().isEmpty());
            assertNull(minter.getCargo());
        }

        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().size(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().time(), 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().value(), 0);
        assertEquals(monitor.getStatisticsEvents().size(), 0);

        map.stepTime();

        assertNotNull(minter.getCargo());
        assertEquals(minter.getCargo().getMaterial(), COIN);
        assertTrue(mint.getFlag().getStackedCargo().isEmpty());
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().size(), 2);
        assertTrue(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().time() > 1);
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().value(), 1);
        assertEquals(monitor.getStatisticsEvents().size(), 1);
    }
}
