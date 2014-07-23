/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Crop;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestFarm {
    
    @Test
    public void testPlaceCrop() throws Exception {
        GameMap map   = new GameMap(10, 10);
        Point point0 = new Point(3, 3);

        Crop crop = map.placeCrop(point0);
        
        assertNotNull(crop);
        assertTrue(map.isCropAtPoint(point0));
    }
    
    @Test
    public void testCropGrowsOverTime() throws Exception {
        GameMap map   = new GameMap(10, 10);
        Point point0 = new Point(3, 3);

        Crop crop = map.placeCrop(point0);
        
        assertEquals(crop.getGrowthState(), Crop.GrowthState.JUST_PLANTED);
        
        Utils.fastForward(99, map);

        assertEquals(crop.getGrowthState(), Crop.GrowthState.JUST_PLANTED);
        
        map.stepTime();
        
        assertEquals(crop.getGrowthState(), Crop.GrowthState.HALFWAY);
        
        Utils.fastForward(99, map);
                
        assertEquals(crop.getGrowthState(), Crop.GrowthState.HALFWAY);
        
        map.stepTime();
        
        assertEquals(crop.getGrowthState(), Crop.GrowthState.FULL_GROWN);
    }
}
