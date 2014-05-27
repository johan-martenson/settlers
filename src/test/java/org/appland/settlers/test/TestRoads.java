/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.List;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Woodcutter;
import static org.appland.settlers.test.Utils.roadEqualsFlags;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestRoads {
    
    @Test
    public void testGetNotExistingRoad() {
        GameMap map = GameMap.createGameMap();
        
        assertNull(map.getRoad(Flag.createFlag(new Point(1, 1)), Flag.createFlag(new Point(2, 2))));
    }
    
        @Test(expected=InvalidRouteException.class)
        public void testUnreachableRoute() throws InvalidEndPointException, InvalidRouteException {
            GameMap map = GameMap.createGameMap();
            
            Flag f1 = Flag.createFlag(new Point(1, 1));
            Flag f2 = Flag.createFlag(new Point(2, 2));
            
            map.placeFlag(f1);
            map.placeFlag(f2);
            
            map.placeRoad(f1, f2);
            
            map.findWay(f1, Flag.createFlag(new Point(3, 3)));
        }
    
        @Test
        public void testFindRouteWithSingleRoad() throws InvalidEndPointException, InvalidRouteException {
            GameMap map = GameMap.createGameMap();
            
            Flag f1 = Flag.createFlag(new Point(1, 1));
            Flag f2 = Flag.createFlag(new Point(2, 2));
            
            map.placeFlag(f1);
            map.placeFlag(f2);
            
            map.placeRoad(f1, f2);
            
            List<Flag> way = map.findWay(f1, f2);
            
            assertTrue(way.size() == 2);
            assertTrue(way.get(0).equals(f1));
            assertTrue(way.get(1).equals(f2));
        }
        
    	@Test
	public void testFindRoute() throws InvalidEndPointException, InvalidRouteException {
		/*
		 * F--F1--F2--F3--F4--F10-----------------|
		 *    |    |                             |
		 *    |    |---F9--Target building       |
		 *    |                                  |
		 *    |                                  |
		 *    |---F5---F6------------------------
		 *    |
		 *    |---F7---F8
		 */
		
		GameMap map = GameMap.createGameMap();
		
		Flag[] points = new Flag[] {
		new Flag(1,1),    // F
		new Flag(2, 1),   // F1
		new Flag(3, 1),  // F2
		new Flag(4, 1),   // F3
		new Flag(5, 1),   // F4
		new Flag(2, 4),   // F5
		new Flag(3, 4),   // F6
		new Flag(2, 6),   // F7
		new Flag(3, 6),   // F8
		new Flag(4, 2),   // F9
                new Flag(6, 1)};  // F10
		
		int i;
		for (i = 0; i < points.length; i++) {
			map.placeFlag(points[i]);
		}

                Woodcutter wc = Woodcutter.createWoodcutter();
		map.placeBuilding(wc, new Point(6, 2));
                
                Flag target = wc.getFlag();
		
		map.placeRoad(points[0], points[1]);
		map.placeRoad(points[1], points[2]);
		map.placeRoad(points[2], points[3]);
		map.placeRoad(points[3], points[4]);
                map.placeRoad(points[4], points[10]);
                map.placeRoad(points[10], points[6]);
		map.placeRoad(points[2], points[9]);
		map.placeRoad(points[9], target);
		map.placeRoad(points[1], points[5]);
		map.placeRoad(points[5], points[6]);
		map.placeRoad(points[1], points[7]);
		map.placeRoad(points[7], points[8]);
		
                /* Test route with List<Point> */
		List<Flag> route = map.findWay(points[0], target);
		
		assertNotNull(route);
		assertTrue(!route.isEmpty());
		
		assertEquals(route.get(0), points[0]);
		assertEquals(route.get(1), points[1]);
		assertEquals(route.get(2), points[2]);
		assertEquals(route.get(3), points[9]);
		assertEquals(route.get(4), target);

		route = map.findWay(target, points[0]);
		
		assertEquals(route.get(0), target);		
		assertEquals(route.get(1), points[9]);
		assertEquals(route.get(2), points[2]);
		assertEquals(route.get(3), points[1]);	
		assertEquals(route.get(4), points[0]);
		
		route = map.findWay(points[1], points[2]);
		
		assertEquals(route.get(0), points[1]);
		assertEquals(route.get(1), points[2]);
                
                /* Test route with List<Road> */
                List<Road> roadsRoute = map.findWayInRoads(points[0], target);

                System.out.println(roadsRoute);
                
		assertNotNull(roadsRoute);
		assertTrue(!roadsRoute.isEmpty());
		
		assertTrue(roadEqualsFlags(roadsRoute.get(0), points[0], points[1]));
                assertTrue(roadEqualsFlags(roadsRoute.get(1), points[1], points[2]));
                assertTrue(roadEqualsFlags(roadsRoute.get(2), points[2], points[9]));
                assertTrue(roadEqualsFlags(roadsRoute.get(3), points[9], target));

                
                roadsRoute = map.findWayInRoads(target, (points[0]));
		
		assertNotNull(roadsRoute);
		assertTrue(!roadsRoute.isEmpty());
		
                assertTrue(roadEqualsFlags(roadsRoute.get(0), points[9], target));
                assertTrue(roadEqualsFlags(roadsRoute.get(1), points[2], points[9]));
                assertTrue(roadEqualsFlags(roadsRoute.get(2), points[1], points[2]));
		assertTrue(roadEqualsFlags(roadsRoute.get(3), points[0], points[1]));
        }
}
