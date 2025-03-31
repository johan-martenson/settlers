package org.appland.settlers.test.statistics;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.actors.Minter;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class TestCoinStatistics {

    @Test
    public void testInitialCoinStatisticsForDefaultResources() throws InvalidUserActionException {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the initial statistics for coins are correct
        var statisticsManager = map.getStatisticsManager();

        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().value(), headquarter.getAmount(COIN));
        assertEquals(statisticsManager.getGeneralStatistics(player0).coins().getMeasurements().getLast().time(), 1);
    }

    @Test
    public void testStatisticsChangesWhenCoinIsProduced() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point3 = new Point(7, 9);
        Mint mint = map.placeBuilding(new Mint(player0), point3);

        /* Connect the mint with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, mint.getFlag(), headquarter.getFlag());

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        /* Populate the mint */
        Worker minter = Utils.occupyBuilding(new Minter(player0, map), mint);

        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getHome(), mint);
        assertEquals(mint.getWorker(), minter);

        /* Deliver wood to the mint */
        mint.putCargo(new Cargo(GOLD, map));
        mint.putCargo(new Cargo(COAL, map));

        // Verify that the statistics for coins are updated when one is produced
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
}
