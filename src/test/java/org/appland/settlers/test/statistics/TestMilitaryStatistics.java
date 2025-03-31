package org.appland.settlers.test.statistics;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestMilitaryStatistics {

    @Test
    public void testInitialMilitaryStatisticsIsCorrect() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the military statistics is correct
        var statisticsManager = map.getStatisticsManager();

        var totalSoldiers = headquarter.getAmount(PRIVATE) +
                headquarter.getAmount(PRIVATE_FIRST_CLASS) +
                headquarter.getAmount(SERGEANT) +
                headquarter.getAmount(OFFICER) +
                headquarter.getAmount(GENERAL);

        assertTrue(totalSoldiers > 0);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getLast().value(), totalSoldiers);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getLast().time(), 1);
    }

    @Test
    public void testMilitaryStatisticsAreUpdatedWhenPrivateIsDrafted() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storage */
        Point point1 = new Point(10, 10);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point1);

        Utils.constructHouse(storehouse0);

        int numberOfPrivates = storehouse0.getAmount(PRIVATE);

        storehouse0.putCargo(new Cargo(Material.BEER, null));
        storehouse0.putCargo(new Cargo(Material.SWORD, null));
        storehouse0.putCargo(new Cargo(Material.SWORD, null));
        storehouse0.putCargo(new Cargo(Material.SHIELD, null));
        storehouse0.putCargo(new Cargo(Material.SHIELD, null));
        storehouse0.putCargo(new Cargo(Material.SHIELD, null));

        var statisticsManager = map.getStatisticsManager();

        var soldiersBeforeCreation = statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getLast();

        assertEquals(storehouse0.getAmount(PRIVATE), numberOfPrivates);
        assertEquals(storehouse0.getAmount(Material.BEER), 1);
        assertEquals(storehouse0.getAmount(Material.SWORD), 2);
        assertEquals(storehouse0.getAmount(Material.SHIELD), 3);

        Utils.fastForward(110, map);

        assertEquals(storehouse0.getAmount(PRIVATE), numberOfPrivates + 1);
        assertEquals(storehouse0.getAmount(Material.BEER), 0);
        assertEquals(storehouse0.getAmount(Material.SWORD), 1);
        assertEquals(storehouse0.getAmount(Material.SHIELD), 2);
        assertEquals(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getLast().value(), soldiersBeforeCreation.value() + 1);
        assertTrue(statisticsManager.getGeneralStatistics(player0).soldiers().getMeasurements().getLast().time() > soldiersBeforeCreation.time());
    }
}
