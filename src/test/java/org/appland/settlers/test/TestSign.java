/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.IRON;
import org.appland.settlers.model.Point;
import static org.appland.settlers.model.Size.SMALL;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestSign {
    
    @Test
    public void testSignExpires() throws Exception {
        
        /* Create a new game map */
        GameMap map = new GameMap(20, 20);
        
        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
    
        /* Place sign */
        Point point0 = new Point(6, 6);
        map.placeSign(IRON, SMALL, point0);

        /* Verify that the sign remains for the right time */
        for (int i = 0; i < 1000; i++) {
            assertTrue(map.isSignAtPoint(point0));
            
            map.stepTime();
        }
    
        assertFalse(map.isSignAtPoint(point0));
    }
}
