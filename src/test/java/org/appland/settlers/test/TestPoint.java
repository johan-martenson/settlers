package org.appland.settlers.test;

import org.appland.settlers.model.Point;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestPoint {

    @Test
    public void testCompareToOtherPoints() {

        Point point = new Point(10, 10);
        Point left = new Point(8, 10);
        Point right = new Point(12, 10);
        Point upLeft = new Point(9, 11);
        Point downLeft = new Point(9, 9);
        Point upRight = new Point(11, 11);
        Point downRight = new Point(11, 9);

        assertTrue(left.isLeftOf(point));
        assertTrue(right.isRightOf(point));
        assertTrue(upLeft.isUpLeftOf(point));
        assertTrue(downLeft.isDownLeftOf(point));
        assertTrue(upRight.isUpRightOf(point));
        assertTrue(downRight.isDownRightOf(point));
    }
}
