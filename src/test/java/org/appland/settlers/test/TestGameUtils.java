package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
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
}