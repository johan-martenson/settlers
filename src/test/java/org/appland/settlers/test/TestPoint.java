package org.appland.settlers.test;

import org.appland.settlers.model.Point;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
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

        /* Verify isLeftOf */
        assertTrue(left.isLeftOf(point));
        assertFalse(upLeft.isLeftOf(point));
        assertFalse(downLeft.isLeftOf(point));
        assertFalse(right.isLeftOf(point));
        assertFalse(upRight.isLeftOf(point));
        assertFalse(downRight.isLeftOf(point));

        /* Verify isRightOf */
        assertTrue(right.isRightOf(point));
        assertFalse(left.isRightOf(point));
        assertFalse(upLeft.isRightOf(point));
        assertFalse(downLeft.isRightOf(point));
        assertFalse(upRight.isRightOf(point));
        assertFalse(downRight.isRightOf(point));

        /* Verify isUpLeftOf */
        assertTrue(upLeft.isUpLeftOf(point));
        assertFalse(left.isUpLeftOf(point));
        assertFalse(downLeft.isUpLeftOf(point));
        assertFalse(right.isUpLeftOf(point));
        assertFalse(upRight.isUpLeftOf(point));
        assertFalse(downRight.isUpLeftOf(point));

        /* Verify isDownLeftOf */
        assertTrue(downLeft.isDownLeftOf(point));
        assertFalse(left.isDownLeftOf(point));
        assertFalse(upLeft.isDownLeftOf(point));
        assertFalse(right.isDownLeftOf(point));
        assertFalse(upRight.isDownLeftOf(point));
        assertFalse(downRight.isDownLeftOf(point));

        /* Verify isUpRightOf */
        assertTrue(upRight.isUpRightOf(point));
        assertFalse(left.isUpRightOf(point));
        assertFalse(upLeft.isUpRightOf(point));
        assertFalse(downLeft.isUpRightOf(point));
        assertFalse(right.isUpRightOf(point));
        assertFalse(downRight.isUpRightOf(point));

        /* Verify isDownRightOf */
        assertTrue(downRight.isDownRightOf(point));
        assertFalse(left.isDownRightOf(point));
        assertFalse(upLeft.isDownRightOf(point));
        assertFalse(downLeft.isDownRightOf(point));
        assertFalse(right.isDownRightOf(point));
        assertFalse(upRight.isDownRightOf(point));
    }
}
