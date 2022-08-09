package org.appland.settlers.test;

import org.appland.settlers.model.DetailedVegetation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.BLUE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestPossibleHarborPoints {

    /*
     * TODO:
     *   - all types of land
     *
     */

    @Test
    public void testCanMarkPlaceForHarborNotDirectlyNextToWater() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place lake of water */
        Point point0 = new Point(10, 10);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Verify that there is a possible point for harbor with water one step away */
        for (Point point : Utils.getHexagonBorder(point0, 2)) {
            map.setPossiblePlaceForHarbor(point);

            assertTrue(map.isAvailableHarborPoint(point));
        }
    }

    @Test
    public void testCannotMarkPlaceForHarborDirectlyNextToWater() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place lake of water */
        Point point0 = new Point(10, 10);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Verify that there is no possible point for harbor with water one step away */
        for (Point point : Utils.getHexagonBorder(point0, 1)) {
            try {
                map.setPossiblePlaceForHarbor(point);

                fail();
            } catch (InvalidUserActionException e) { }

            assertFalse(map.isAvailableHarborPoint(point));
        }
    }

    @Test
    public void testCanMarkAvailablePlaceForHarborWithWaterNextToFlag() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place lake */
        Point point0 = new Point(10, 10);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Verify that there can be a possible point for harbor with water close to the flag */
        map.setPossiblePlaceForHarbor(point0.upLeft().upLeft());
        map.setPossiblePlaceForHarbor(point0.upRight().upLeft());

        assertTrue(map.isAvailableHarborPoint(point0.upLeft().upLeft()));
        assertTrue(map.isAvailableHarborPoint(point0.upRight().upLeft()));
    }

    @Test
    public void testCanMarkAvailablePlaceForHarborWithWaterOneStepAway() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place lake of buildable water */
        Point point0 = new Point(10, 10);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Verify that there is no possible point for harbor with water one step away */
        for (Point point : Utils.getHexagonBorder(point0, 2)) {
            map.setPossiblePlaceForHarbor(point);

            assertTrue(map.isAvailableHarborPoint(point));
        }
    }

    @Test
    public void testCanMarkAvailablePlaceForHarborWithoutWaterOneStepAway() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Verify that there is no possible point for harbor without water one step away */
        Point point0 = new Point(10, 10);
        map.setPossiblePlaceForHarbor(point0);

        assertTrue(map.isAvailableHarborPoint(point0));
    }
}
