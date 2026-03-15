package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Vegetation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestPossibleHarborPoints {

    /*
     * TODO:
     *   - all types of land
     *
     */

    @Test
    public void testCanMarkPlaceForHarborNotDirectlyNextToWater() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place lake of water
        var point0 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point0, Vegetation.WATER, map);

        // Verify that there is a possible point for harbor with water one step away
        for (var point : Utils.getHexagonBorder(point0, 2)) {
            map.setPossiblePlaceForHarbor(point);

            assertTrue(map.isAvailableHarborPoint(point));
        }
    }

    @Test
    public void testCannotMarkPlaceForHarborDirectlyNextToWater() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place lake of water
        var point0 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point0, Vegetation.WATER, map);

        // Verify that there is no possible point for harbor with water one step away
        for (var point : Utils.getHexagonBorder(point0, 1)) {
            try {
                map.setPossiblePlaceForHarbor(point);

                fail();
            } catch (InvalidUserActionException e) { }

            assertFalse(map.isAvailableHarborPoint(point));
        }
    }

    @Test
    public void testCanMarkAvailablePlaceForHarborWithWaterNextToFlag() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place lake
        var point0 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point0, Vegetation.WATER, map);

        // Verify that there can be a possible point for harbor with water close to the flag
        map.setPossiblePlaceForHarbor(point0.upLeft().upLeft());
        map.setPossiblePlaceForHarbor(point0.upRight().upLeft());

        assertTrue(map.isAvailableHarborPoint(point0.upLeft().upLeft()));
        assertTrue(map.isAvailableHarborPoint(point0.upRight().upLeft()));
    }

    @Test
    public void testCanMarkAvailablePlaceForHarborWithWaterOneStepAway() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place lake of buildable water
        var point0 = new Point(10, 10);
        Utils.surroundPointWithVegetation(point0, Vegetation.WATER, map);

        // Verify that there is no possible point for harbor with water one step away
        for (var point : Utils.getHexagonBorder(point0, 2)) {
            map.setPossiblePlaceForHarbor(point);

            assertTrue(map.isAvailableHarborPoint(point));
        }
    }

    @Test
    public void testCanMarkAvailablePlaceForHarborWithoutWaterOneStepAway() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Verify that there is no possible point for harbor without water one step away
        var point0 = new Point(10, 10);
        map.setPossiblePlaceForHarbor(point0);

        assertTrue(map.isAvailableHarborPoint(point0));
    }
}
