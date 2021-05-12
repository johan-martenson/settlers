import org.appland.settlers.model.DetailedVegetation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.awt.Color.BLUE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestPossibleHarborPoints {

    /*
     * TODO:
     *   - all types of land
     *   - other types of water
     *
     */

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
        Utils.surroundPointWithDetailedVegetation(point0.right(), DetailedVegetation.WATER, map);

        /* Verify that there can be a possible point for harbor with water close to the flag */
        map.setPossiblePlaceForHarbor(point0.upLeft().upLeft());
        map.setPossiblePlaceForHarbor(point0.downLeft().upLeft());
        map.setPossiblePlaceForHarbor(point0.upRight().upLeft());
        map.setPossiblePlaceForHarbor(point0.left().upLeft());
        map.setPossiblePlaceForHarbor(point0.right().right().upLeft());

        assertTrue(map.isAvailableHarborPoint(point0.upLeft().upLeft()));
        assertTrue(map.isAvailableHarborPoint(point0.downLeft().upLeft()));
        assertTrue(map.isAvailableHarborPoint(point0.upRight().upLeft()));
        assertTrue(map.isAvailableHarborPoint(point0.left().upLeft()));
        assertTrue(map.isAvailableHarborPoint(point0.right().right().upLeft()));
    }

    @Test
    public void testCanMarkAvailablePlaceForHarborWithWaterNextToBuilding() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place lake */
        Point point0 = new Point(10, 10);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);
        Utils.surroundPointWithDetailedVegetation(point0.right(), DetailedVegetation.WATER, map);

        /* Verify that there can be a possible point for harbor with water close to the building */
        map.setPossiblePlaceForHarbor(point0.right().upRight());
        map.setPossiblePlaceForHarbor(point0.right().right());
        map.setPossiblePlaceForHarbor(point0.right().downRight());
        map.setPossiblePlaceForHarbor(point0.downRight());
        map.setPossiblePlaceForHarbor(point0.downLeft());
        map.setPossiblePlaceForHarbor(point0.left());

        assertTrue(map.isAvailableHarborPoint(point0.right().upRight()));
        assertTrue(map.isAvailableHarborPoint(point0.right().right()));
        assertTrue(map.isAvailableHarborPoint(point0.right().downRight()));
        assertTrue(map.isAvailableHarborPoint(point0.downRight()));
        assertTrue(map.isAvailableHarborPoint(point0.downLeft()));
        assertTrue(map.isAvailableHarborPoint(point0.left()));
    }

    @Test
    public void testCannotMarkAvailablePlaceForHarborWithoutWaterNextToBuildingOrFlag() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place lake */
        Point point0 = new Point(10, 10);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Verify that it's not possible to mark a possible point for harbor without being close to water */
        List<Point> invalidPlaces = new ArrayList<>(Arrays.asList(new Point[]{
            point0.upLeft().upLeft().upLeft(),
            point0.upLeft().up(),
            point0.upRight().upRight(),
            point0.upRight().right(),
            point0.right().right(),
            point0.downRight().downRight(),
            point0.down(),
            point0.downLeft().downLeft(),
            point0.left().left()
        }));

        for (Point point : invalidPlaces) {
            try {
                map.setPossiblePlaceForHarbor(point);

                fail();
            } catch (InvalidUserActionException e) { }

            assertFalse(map.isAvailableHarborPoint(point));
        }
    }

    @Test
    public void testCannotMarkAvailablePlaceForHarborCompletelySurroundedByWater() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place lake */
        Point point0 = new Point(10, 10);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Verify that it's not possible to mark a possible point for harbor completely surrounded by water */
        try {
            map.setPossiblePlaceForHarbor(point0);

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isAvailableHarborPoint(point0));
    }

    @Test
    public void testCannotMarkAvailablePlaceForHarborFlagCompletelySurroundedByWater() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place lake */
        Point point0 = new Point(10, 10);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Verify that it's not possible to mark a possible point for harbor completely surrounded by water */
        try {
            map.setPossiblePlaceForHarbor(point0.upLeft());

            fail();
        } catch (InvalidUserActionException e) { }

        assertFalse(map.isAvailableHarborPoint(point0));
    }
}
