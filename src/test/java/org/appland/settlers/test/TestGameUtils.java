package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.appland.settlers.model.GameUtils.getHexagonAreaAroundPoint;
import static org.appland.settlers.model.PlayerColor.BLUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGameUtils {
    private static GameMap map(int width, int height) throws InvalidUserActionException {
        return new GameMap(List.of(new Player("Player 0", BLUE, Nation.ROMANS, PlayerType.HUMAN)), width, height);
    }

    @Test
    void radiusZeroReturnsOnlyCenter() throws InvalidUserActionException {
        var map = map(10, 10);
        var center = new Point(5, 5);

        var result = getHexagonAreaAroundPoint(center, 0, map);

        assertEquals(Set.of(center), result);
    }
    public static void printHexagon(Set<Point> points, Point center) {
        int minX = points.stream().mapToInt(p -> p.x).min().orElse(center.x);
        int maxX = points.stream().mapToInt(p -> p.x).max().orElse(center.x);
        int minY = points.stream().mapToInt(p -> p.y).min().orElse(center.y);
        int maxY = points.stream().mapToInt(p -> p.y).max().orElse(center.y);

        for (int y = maxY; y >= minY; y--) {
            if ((y & 1) == 1)
                System.out.print(" ");

            for (int x = minX; x <= maxX; x++) {
                var p = new Point(x, y);

                if (center.equals(p))
                    System.out.print("C ");
                else if (points.contains(p))
                    System.out.print("x ");
                else
                    System.out.print(". ");
            }

            System.out.println();
        }
    }
    @Test
    void radiusOneInsideMapReturnsFullHexagon() throws InvalidUserActionException {
        var map = map(10, 10);
        var center = new Point(5, 5);

        var result = getHexagonAreaAroundPoint(center, 1, map);

        assertEquals(7, result.size());
        assertTrue(result.contains(center));
        assertTrue(result.contains(center.upLeft()));
        assertTrue(result.contains(center.upRight()));
        assertTrue(result.contains(center.right()));
        assertTrue(result.contains(center.downRight()));
        assertTrue(result.contains(center.downLeft()));
        assertTrue(result.contains(center.left()));
    }

    @Test
    void radiusTwoInsideMapReturnsFullHexagon() throws InvalidUserActionException {
        var map = map(20, 20);
        var center = new Point(10, 10);

        var result = getHexagonAreaAroundPoint(center, 2, map);

        assertEquals(19, result.size());
        assertTrue(result.contains(center));
    }

    @Test
    void nearLeftEdgeClipsHexagon() throws InvalidUserActionException {
        var map = map(10, 10);
        var center = new Point(0, 5);

        var result = getHexagonAreaAroundPoint(center, 2, map);

        assertTrue(result.stream().allMatch(p -> p.x >= 0));
        assertTrue(result.size() < 19);
    }

    @Test
    void nearRightEdgeClipsHexagonOnIndentedRow() throws InvalidUserActionException {
        var map = map(10, 10);
        var center = new Point(9, 5);

        var result = getHexagonAreaAroundPoint(center, 1, map);

        assertTrue(result.stream().allMatch(p -> p.x < map.getWidth()));
        assertEquals(result.size(), 4);
    }

    @Test
    void nearRightEdgeClipsHexagonOnUnindentedRow() throws InvalidUserActionException {
        var map = map(10, 10);
        var center = new Point(8, 4);

        var result = getHexagonAreaAroundPoint(center, 1, map);

        assertTrue(result.stream().allMatch(p -> p.x < map.getWidth()));
        assertEquals(result.size(), 6);
    }

    @Test
    void nearLeftEdgeClipsHexagonWithRadiusOne() throws InvalidUserActionException {
        var map = map(10, 10);
        var center = new Point(0, 5);

        var result = getHexagonAreaAroundPoint(center, 1, map);

        assertTrue(result.stream().allMatch(p -> p.x >= 0));
        assertEquals(result.size(), 4);
    }

    @Test
    void nearLeftEdgeClipsHexagonWithRadiusOneWithIndent() throws InvalidUserActionException {
        var map = map(10, 10);
        var center = new Point(1, 4);

        var result = getHexagonAreaAroundPoint(center, 1, map);

        assertTrue(result.stream().allMatch(p -> p.x >= 0));
        assertEquals(result.size(), 6);
    }

    @Test
    void nearBottomEdgeClipsHexagon() throws InvalidUserActionException {
        var map = map(10, 10);
        var center = new Point(5, 0);

        var result = getHexagonAreaAroundPoint(center, 2, map);

        assertTrue(result.stream().allMatch(p -> p.y >= 0));
        assertEquals(result.size(), 12);
    }

    @Test
    void nearTopEdgeClipsHexagon() throws InvalidUserActionException {
        var map = map(10, 10);
        var center = new Point(4, 9);

        var result = getHexagonAreaAroundPoint(center, 1, map);

        assertTrue(result.stream().allMatch(p -> p.y < 10));
        assertEquals(result.size(), 5);
    }

    @Test
    void cornerClipsStrongly() throws InvalidUserActionException {
        var map = map(10, 10);
        var center = new Point(0, 0);

        var result = getHexagonAreaAroundPoint(center, 2, map);

        assertTrue(result.stream().allMatch(p -> p.x >= 0));
        assertTrue(result.stream().allMatch(p -> p.y >= 0));
        assertTrue(result.size() < 19);
    }

    @Test
    void staggeredRowBehaviourOddRow() throws InvalidUserActionException {
        var map = map(10, 10);
        var center = new Point(5, 5); // odd/even row depends on implementation

        var result = getHexagonAreaAroundPoint(center, 1, map);

        assertEquals(7, result.size());
    }

    @Test
    void staggeredRowBehaviourEvenRow() throws InvalidUserActionException {
        var map = map(10, 10);
        var center = new Point(5, 6);

        var result = getHexagonAreaAroundPoint(center, 1, map);

        assertEquals(7, result.size());
    }

    @Test
    public void testHorizontalDistance() {
        assertEquals(1, GameUtils.distanceInGameSteps(
                new Point(1, 2),
                new Point(3, 2)));

        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(2, 2),
                new Point(6, 2)));
    }

    @Test
    public void testDiagonalDistance() {
        assertEquals(1, GameUtils.distanceInGameSteps(
                new Point(1, 1),
                new Point(2, 2)));

        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(1, 1),
                new Point(3, 3)));

        assertEquals(4, GameUtils.distanceInGameSteps(
                new Point(2, 2),
                new Point(6, 6)));
    }

    @Test
    public void testMixedHorizontalAndDiagonalDistance() {
        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(1, 1),
                new Point(3, 3)));

        assertEquals(3, GameUtils.distanceInGameSteps(
                new Point(1, 1),
                new Point(5, 3)));

        assertEquals(3, GameUtils.distanceInGameSteps(
                new Point(2, 2),
                new Point(6, 4)));
    }

    @Test
    public void testDistanceUsedByCatapultScenarios() {
        assertEquals(6, GameUtils.distanceInGameSteps(
                new Point(21, 11),
                new Point(25, 5)));

        assertEquals(12, GameUtils.distanceInGameSteps(
                new Point(17, 17),
                new Point(29, 5)));

        assertEquals(12, GameUtils.distanceInGameSteps(
                new Point(16, 17),
                new Point(28, 5)));
    }

    @Test
    public void testDistanceToSamePointIsZero() {
        assertEquals(0, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(10, 10)));
    }

    @Test
    public void testDistanceToHorizontalNeighborsIsOne() {
        assertEquals(1, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(8, 10)));

        assertEquals(1, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(12, 10)));
    }

    @Test
    public void testDistanceToDiagonalNeighborsIsOne() {
        assertEquals(1, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(9, 9)));

        assertEquals(1, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(11, 9)));

        assertEquals(1, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(9, 11)));

        assertEquals(1, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(11, 11)));
    }

    @Test
    public void testDistanceTwoHorizontalSteps() {
        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(6, 10)));

        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(14, 10)));
    }

    @Test
    public void testDistanceTwoDiagonalSteps() {
        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(8, 8)));

        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(12, 8)));

        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(8, 12)));

        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(12, 12)));
    }

    @Test
    public void testDistanceHorizontalAndDiagonal() {
        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(7, 9)));

        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(13, 9)));

        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(7, 11)));

        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(10, 10),
                new Point(13, 11)));
    }

    @Test
    public void testDistanceIsSymmetric() {
        var a = new Point(17, 17);
        var b = new Point(29, 5);

        assertEquals(
                GameUtils.distanceInGameSteps(a, b),
                GameUtils.distanceInGameSteps(b, a));
    }

    @Test
    public void testDistanceUsedForCatapultRange() {
        assertEquals(6, GameUtils.distanceInGameSteps(
                new Point(23, 11),
                new Point(25, 5)));

        assertEquals(6, GameUtils.distanceInGameSteps(
                new Point(22, 11),
                new Point(25, 5)));

        assertEquals(6, GameUtils.distanceInGameSteps(
                new Point(21, 11),
                new Point(25, 5)));

        assertEquals(12, GameUtils.distanceInGameSteps(
                new Point(22, 17),
                new Point(29, 5)));

        assertEquals(12, GameUtils.distanceInGameSteps(
                new Point(21, 17),
                new Point(29, 5)));

        assertEquals(12, GameUtils.distanceInGameSteps(
                new Point(20, 17),
                new Point(29, 5)));

        assertEquals(12, GameUtils.distanceInGameSteps(
                new Point(19, 17),
                new Point(29, 5)));

        assertEquals(12, GameUtils.distanceInGameSteps(
                new Point(18, 17),
                new Point(29, 5)));

        assertEquals(12, GameUtils.distanceInGameSteps(
                new Point(17, 17),
                new Point(29, 5)));

        assertEquals(13, GameUtils.distanceInGameSteps(
                new Point(16, 17),
                new Point(30, 5)));
    }

    @Test
    public void testHorizontalNeighbor() {
        assertEquals(1, GameUtils.distanceInGameSteps(
                new Point(10,10),
                new Point(12,10)));
    }

    @Test
    public void testDiagonalNeighborNorthEast() {
        assertEquals(1, GameUtils.distanceInGameSteps(
                new Point(10,10),
                new Point(11,9)));
    }

    @Test
    public void testDiagonalNeighborSouthEast() {
        assertEquals(1, GameUtils.distanceInGameSteps(
                new Point(10,10),
                new Point(11,11)));
    }

    @Test
    public void testTwoHorizontalSteps() {
        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(10,10),
                new Point(14,10)));
    }

    @Test
    public void testTwoDiagonalSteps() {
        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(10,10),
                new Point(12,8)));
    }

    @Test
    public void testHorizontalThenDiagonal() {
        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(10,10),
                new Point(13,9)));
    }

    @Test
    public void testThreeDiagonalSteps() {
        assertEquals(3, GameUtils.distanceInGameSteps(
                new Point(10,10),
                new Point(13,7)));
    }


    @Test
    public void testGoingVertically() {
        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(3, 3),
                new Point(3, 5)
        ));

        assertEquals(2, GameUtils.distanceInGameSteps(
                new Point(5, 4),
                new Point(5, 2)
        ));
    }
}