package org.appland.settlers.test;

import org.appland.settlers.model.Size;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestSize {

    @Test
    public void testSizeContains() {
        assertTrue(Size.SMALL.contains(Size.SMALL));
        assertFalse(Size.SMALL.contains(Size.MEDIUM));
        assertFalse(Size.SMALL.contains(Size.LARGE));

        assertTrue(Size.MEDIUM.contains(Size.SMALL));
        assertTrue(Size.MEDIUM.contains(Size.MEDIUM));
        assertFalse(Size.MEDIUM.contains(Size.LARGE));

        assertTrue(Size.LARGE.contains(Size.SMALL));
        assertTrue(Size.LARGE.contains(Size.MEDIUM));
        assertTrue(Size.LARGE.contains(Size.LARGE));
    }
}
