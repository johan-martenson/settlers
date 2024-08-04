package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.appland.settlers.model.Size.LARGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestHeight {

    @Test
    public void testDefaultHeight() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Verify that the default height is 10 */
        int startX = 1;
        int startY = 1;

        for (int y = startY; y < map.getHeight(); y++) {
            for (int x = startX; x < map.getWidth(); x += 2) {
                assertEquals(map.getHeightAtPoint(new Point(x, y)), 10);
            }

            if (startX == 1) {
                startX = 2;
            } else {
                startX = 1;
            }
        }
    }

    @Test
    public void testSettingHeight() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Verify that the height can be set */
        Point point0 = new Point(10, 10);
        map.setHeightAtPoint(point0, 11);

        /* Verify that the height is correct */
        assertEquals(map.getHeightAtPoint(point0), 11);
    }

    @Test
    public void testCannotPlaceFortressWithTooMuchHeightDifferenceOnLeft() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Raise one point too high for a fortress to be placed, i.e. the difference is more than three*/
        Point point0 = new Point(10, 10);
        map.setHeightAtPoint(point0, 14);

        /* Place headquarter */
        Point point1 = new Point(10, 18);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Verify that it's not possible to place a fortress when the height difference is too big */
        try {
            Fortress fortress0 = map.placeBuilding(new Fortress(player0), point0.right());
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testCannotPlaceFortressWithTooMuchHeightDifferenceOnUpLeft() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Raise one point too high for a fortress to be placed, i.e. the difference is more than three*/
        Point point0 = new Point(10, 10);
        map.setHeightAtPoint(point0, 14);

        /* Place headquarter */
        Point point1 = new Point(10, 18);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Verify that it's not possible to place a fortress when the height difference is too big */
        try {
            Fortress fortress0 = map.placeBuilding(new Fortress(player0), point0.downRight());
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testCannotPlaceFortressWithTooMuchHeightDifferenceOnUpRight() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Raise one point too high for a fortress to be placed, i.e. the difference is more than three*/
        Point point0 = new Point(10, 10);
        map.setHeightAtPoint(point0, 14);

        /* Place headquarter */
        Point point1 = new Point(10, 18);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Verify that it's not possible to place a fortress when the height difference is too big */
        try {
            Fortress fortress0 = map.placeBuilding(new Fortress(player0), point0.downLeft());
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testCannotPlaceFortressWithTooMuchHeightDifferenceOnRight() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Raise one point too high for a fortress to be placed, i.e. the difference is more than three*/
        Point point0 = new Point(10, 10);
        map.setHeightAtPoint(point0, 14);

        /* Place headquarter */
        Point point1 = new Point(10, 18);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Verify that it's not possible to place a fortress when the height difference is too big */
        try {
            Fortress fortress0 = map.placeBuilding(new Fortress(player0), point0.left());
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testCannotPlaceFortressWithTooMuchHeightDifferenceOnDownRight() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Raise one point too high for a fortress to be placed, i.e. the difference is more than three*/
        Point point0 = new Point(10, 10);
        map.setHeightAtPoint(point0, 14);

        /* Place headquarter */
        Point point1 = new Point(10, 18);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Verify that it's not possible to place a fortress when the height difference is too big */
        try {
            Fortress fortress0 = map.placeBuilding(new Fortress(player0), point0.upLeft());

            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testCannotPlaceFortressWithTooMuchHeightDifferenceOnDownLeft() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Raise one point too high for a fortress to be placed, i.e. the difference is more than three*/
        Point point0 = new Point(10, 10);
        map.setHeightAtPoint(point0, 14);

        /* Place headquarter */
        Point point1 = new Point(10, 18);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Verify that it's not possible to place a fortress when the height difference is too big */
        try {
            Fortress fortress0 = map.placeBuilding(new Fortress(player0), point0.upRight());
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testNoFortressAvailableWithTooMuchHeightDifferenceOnLeft() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Raise one point too high for a fortress to be placed, i.e. the difference is more than three*/
        Point point0 = new Point(10, 10);
        map.setHeightAtPoint(point0, 14);

        /* Place headquarter */
        Point point1 = new Point(10, 18);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Verify that it's not possible to place a fortress when the height difference is too big */
        assertNotEquals(map.isAvailableHousePoint(player0, point0.right()), LARGE);
    }

    @Test
    public void testNoFortressAvailableWithTooMuchHeightDifferenceOnUpLeft() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Raise one point too high for a fortress to be placed, i.e. the difference is more than three*/
        Point point0 = new Point(10, 10);
        map.setHeightAtPoint(point0, 14);

        /* Place headquarter */
        Point point1 = new Point(10, 18);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Verify that it's not possible to place a fortress when the height difference is too big */
        assertNotEquals(map.isAvailableHousePoint(player0, point0.downRight()), LARGE);
    }

    @Test
    public void testNoFortressAvailableWithTooMuchHeightDifferenceOnUpRight() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Raise one point too high for a fortress to be placed, i.e. the difference is more than three*/
        Point point0 = new Point(10, 10);
        map.setHeightAtPoint(point0, 14);

        /* Place headquarter */
        Point point1 = new Point(10, 18);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Verify that it's not possible to place a fortress when the height difference is too big */
        assertNotEquals(map.isAvailableHousePoint(player0, point0.downLeft()), LARGE);
    }

    @Test
    public void testNoFortressAvailableWithTooMuchHeightDifferenceOnRight() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Raise one point too high for a fortress to be placed, i.e. the difference is more than three*/
        Point point0 = new Point(10, 10);
        map.setHeightAtPoint(point0, 14);

        /* Place headquarter */
        Point point1 = new Point(10, 18);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Verify that it's not possible to place a fortress when the height difference is too big */
        assertNotEquals(map.isAvailableHousePoint(player0, point0.left()), LARGE);
    }

    @Test
    public void testNoFortressAvailableWithTooMuchHeightDifferenceDownRight() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Raise one point too high for a fortress to be placed, i.e. the difference is more than three*/
        Point point0 = new Point(10, 10);
        map.setHeightAtPoint(point0, 14);

        /* Place headquarter */
        Point point1 = new Point(10, 18);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Verify that it's not possible to place a fortress when the height difference is too big */
        assertNotEquals(map.isAvailableHousePoint(player0, point0.upLeft()), LARGE);
    }

    @Test
    public void testNoFortressAvailableWithTooMuchHeightDifferenceOnDownLeft() throws Exception {

        /* Creating new game map with size 100x100 */
        Player player0 = new Player("Player 0", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Raise one point too high for a fortress to be placed, i.e. the difference is more than three*/
        Point point0 = new Point(10, 10);
        map.setHeightAtPoint(point0, 14);

        /* Place headquarter */
        Point point1 = new Point(10, 18);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Verify that it's not possible to place a fortress when the height difference is too big */
        assertNotEquals(map.isAvailableHousePoint(player0, point0.upRight()), LARGE);
    }
}
