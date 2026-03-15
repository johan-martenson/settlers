package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Stone;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.StoneAmount.*;
import static org.junit.Assert.assertEquals;

public class TestStones {

    @Test
    public void testStoneAmount() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place stone
        var point0 = new Point(5, 5);
        var stone0 = map.placeStone(point0, Stone.StoneType.STONE_1, 6);

        var point1 = new Point(7, 5);
        var stone1 = map.placeStone(point1, Stone.StoneType.STONE_1, 5);

        var point2 = new Point(9, 5);
        var stone2 = map.placeStone(point2, Stone.StoneType.STONE_1, 4);

        var point3 = new Point(11, 5);
        var stone3 = map.placeStone(point3, Stone.StoneType.STONE_1, 3);

        var point4 = new Point(13, 5);
        var stone4 = map.placeStone(point4, Stone.StoneType.STONE_1, 2);

        var point5 = new Point(15, 5);
        var stone5 = map.placeStone(point5, Stone.StoneType.STONE_1, 1);

        // Verify that the right stone amount is returned
        assertEquals(stone0.getStoneAmount(), FULL);
        assertEquals(stone1.getStoneAmount(), ALMOST_FULL);
        assertEquals(stone2.getStoneAmount(), MIDDLE);
        assertEquals(stone3.getStoneAmount(), LITTLE_MORE);
        assertEquals(stone4.getStoneAmount(), LITTLE);
        assertEquals(stone5.getStoneAmount(), MINI);
    }

    @Test
    public void testStoneAmountIsEqualForBothTypes() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place stones
        var point0 = new Point(5, 5);
        var stone0 = map.placeStone(point0, Stone.StoneType.STONE_1, 6);

        var point1 = new Point(7, 5);
        var stone1 = map.placeStone(point1, Stone.StoneType.STONE_2, 6);

        var point2 = new Point(9, 5);
        var stone2 = map.placeStone(point2, Stone.StoneType.STONE_1, 4);

        var point3 = new Point(11, 5);
        var stone3 = map.placeStone(point3, Stone.StoneType.STONE_2, 4);

        // Verify that the right stone amount is returned
        assertEquals(stone0.getStoneAmount(), stone1.getStoneAmount());
        assertEquals(stone2.getStoneAmount(), stone3.getStoneAmount());
    }
}
