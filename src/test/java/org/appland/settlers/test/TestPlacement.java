/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.List;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Woodcutter;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestPlacement {

    @Test
    public void testDefaultMapIsEmpty() {
        
    }
    
    @Test
    public void testGetPossibleFlagPlacements() throws Exception {
        GameMap map = new GameMap(10, 10);
        
        List<Point> flagPoints = map.getAvailableFlagPoints();
        
        /* Test that flags can be placed every second point within the map */
        int x, y;
        boolean oddRow = true;
        for (y = 1; y < 10; y++) {
            for (x = 1; x < 10; x++) {
                if (oddRow) {
                    if (x % 2 == 0) {
                        assertFalse(flagPoints.contains(new Point(x, y)));
                    } else {
                        assertTrue(flagPoints.contains(new Point(x, y)));
                    }
                } else {
                    if (x % 2 == 0) {
                        assertTrue(flagPoints.contains(new Point(x, y)));
                    } else {
                        assertFalse(flagPoints.contains(new Point(x, y)));
                    }
                }
            }

            oddRow = !oddRow;
        }

        /* Test that flags can't be placed on the borders */
        for (y = 0; y < 11; y++) {
            assertFalse(flagPoints.contains(new Point(0, y)));
            assertFalse(flagPoints.contains(new Point(10, y)));
        }
        
        for (x = 0; x < 11; x++) {
            assertFalse(flagPoints.contains(new Point(x, 0)));
            assertFalse(flagPoints.contains(new Point(x, 10)));
        }
    }

    @Test
    public void testAvailableFlagsNextToSmallHouse() throws Exception {
        GameMap map   = new GameMap(10, 10);
        Woodcutter wc = new Woodcutter();
        Point wcPoint = new Point(6, 4);
        
        map.placeBuilding(wc, wcPoint);
        
        List<Point> possibleFlagPoints = map.getAvailableFlagPoints();
        
        /* Verify that the woodcutter occupies the right points */
        boolean oddEvenFlip = true;
        int x, y;
        
        /* Verify that points at the place of the house are occupied */
        assertTrue (possibleFlagPoints.contains(new Point(3, 3)));
        assertFalse(possibleFlagPoints.contains(new Point(4, 3)));
        assertFalse(possibleFlagPoints.contains(new Point(5, 3)));
        assertFalse(possibleFlagPoints.contains(new Point(6, 3)));
        assertFalse(possibleFlagPoints.contains(new Point(7, 3)));
        
        assertFalse(possibleFlagPoints.contains(new Point(3, 4)));
        assertFalse(possibleFlagPoints.contains(new Point(4, 4)));
        assertFalse(possibleFlagPoints.contains(new Point(5, 4)));
        assertFalse(possibleFlagPoints.contains(new Point(6, 4)));
        assertFalse(possibleFlagPoints.contains(new Point(7, 4)));        
        
        assertFalse(possibleFlagPoints.contains(new Point(3, 5)));
        assertFalse(possibleFlagPoints.contains(new Point(4, 5)));
        assertFalse(possibleFlagPoints.contains(new Point(5, 5)));
        assertFalse(possibleFlagPoints.contains(new Point(6, 5)));
        assertFalse(possibleFlagPoints.contains(new Point(7, 5)));        
        
        assertFalse(possibleFlagPoints.contains(new Point(3, 6)));
        assertFalse(possibleFlagPoints.contains(new Point(4, 6)));
        assertFalse(possibleFlagPoints.contains(new Point(5, 6)));
        assertFalse(possibleFlagPoints.contains(new Point(6, 6)));
        assertFalse(possibleFlagPoints.contains(new Point(7, 6)));        

        /* Check first row */
        for (x = 1; x < 10; x++) {
            Point p = new Point(x, 1);
            
            if (oddEvenFlip) {
                assertTrue(possibleFlagPoints.contains(p));
            } else {
                assertFalse(possibleFlagPoints.contains(p));
            }
            
            oddEvenFlip = !oddEvenFlip;
        }

        /* Check second row */
        oddEvenFlip = false;
        for (x = 1; x < 10; x++) {
            Point p = new Point(x, 2);
            
            if (oddEvenFlip) {
                assertTrue(possibleFlagPoints.contains(p));
            } else {
                assertFalse(possibleFlagPoints.contains(p));
            }
        
            oddEvenFlip = !oddEvenFlip;
        }

        /* Check first column */
        oddEvenFlip = true;
        for (y = 1; y < 10; y++) {
            Point p = new Point(1, y);
            
            if (oddEvenFlip) {
                assertTrue(possibleFlagPoints.contains(p));
            } else {
                assertFalse(possibleFlagPoints.contains(p));
            }
        
            oddEvenFlip = !oddEvenFlip;
        }

        /* Check second column */
        oddEvenFlip = false;
        for (y = 1; y < 10; y++) {
            Point p = new Point(2, y);
            
            if (oddEvenFlip) {
                assertTrue(possibleFlagPoints.contains(p));
            } else {
                assertFalse(possibleFlagPoints.contains(p));
            }
        
            oddEvenFlip = !oddEvenFlip;
        }

        /* Check seventh row */
        oddEvenFlip = true;
        for (x = 1; x < 10; x++) {
            Point p = new Point(x, 7);
            
            if (oddEvenFlip) {
                assertTrue(possibleFlagPoints.contains(p));
            } else {
                assertFalse(possibleFlagPoints.contains(p));
            }
        
            oddEvenFlip = !oddEvenFlip;
        }

        /* Check eighth row */
        oddEvenFlip = false;
        for (x = 1; x < 10; x++) {
            Point p = new Point(x, 8);
            
            if (oddEvenFlip) {
                assertTrue(possibleFlagPoints.contains(p));
            } else {
                assertFalse(possibleFlagPoints.contains(p));
            }
        
            oddEvenFlip = !oddEvenFlip;
        }

        /* Check eighth column */
        oddEvenFlip = false;
        for (y = 1; y < 10; y++) {
            Point p = new Point(8, y);
            
            if (oddEvenFlip) {
                assertTrue(possibleFlagPoints.contains(p));
            } else {
                assertFalse(possibleFlagPoints.contains(p));
            }
        
            oddEvenFlip = !oddEvenFlip;
        }
        
        /* Check ninth column */
        oddEvenFlip = true;
        for (y = 1; y < 10; y++) {
            Point p = new Point(9, y);
            
            if (oddEvenFlip) {
                assertTrue(possibleFlagPoints.contains(p));
            } else {
                assertFalse(possibleFlagPoints.contains(p));
            }
        
            oddEvenFlip = !oddEvenFlip;
        }
    }

    @Test
    public void testPlaceFlagTakesSpace() throws Exception {
        GameMap map = new GameMap(10, 10);
        Point   p   = new Point(3, 3);
        Flag    f   = new Flag(p);
        
        map.placeFlag(f);
        
        List<Point> availableFlagPoints = map.getAvailableFlagPoints();
        
        assertTrue(availableFlagPoints.contains(new Point(1, 1)));
        assertTrue(availableFlagPoints.contains(new Point(3, 1)));
        assertTrue(availableFlagPoints.contains(new Point(5, 1)));
        
        assertFalse(availableFlagPoints.contains(new Point(2, 2)));
        assertFalse(availableFlagPoints.contains(new Point(4, 2)));

        assertTrue (availableFlagPoints.contains(new Point(1, 3)));
        assertFalse(availableFlagPoints.contains(new Point(3, 3)));
        assertTrue (availableFlagPoints.contains(new Point(5, 3)));
    
        assertFalse(availableFlagPoints.contains(new Point(2, 4)));
        assertFalse(availableFlagPoints.contains(new Point(4, 4)));
    
        assertTrue(availableFlagPoints.contains(new Point(1, 5)));
        assertTrue(availableFlagPoints.contains(new Point(3, 5)));
        assertTrue(availableFlagPoints.contains(new Point(5, 5)));

    }
    
    @Test(expected=Exception.class)
    public void testPlaceFlagTooCloseToSmallHouse() throws Exception {
        GameMap map = new GameMap(10, 10);

        Woodcutter wc = new Woodcutter();
        Point wcPoint = new Point(6, 4);
        
        map.placeBuilding(wc, wcPoint);

        Flag f = new Flag(new Point(3, 5));
        
        map.placeFlag(f);
    }
}
