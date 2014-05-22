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
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Point;

import static org.appland.settlers.test.Utils.roadEqualsPoints;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestRoads {
    
        @Test(expected=InvalidRouteException.class)
        public void testUnreachableRoute() throws InvalidEndPointException, InvalidRouteException {
            GameMap map = GameMap.createGameMap();
            
            Flag f1 = Flag.createFlag(new Point(1, 1));
            Flag f2 = Flag.createFlag(new Point(2, 2));
            
            map.placeFlag(f1);
            map.placeFlag(f2);
            
            map.placeRoad(f1.getPosition(), f2.getPosition());
            
            map.findWay(f1.getPosition(), new Point(3, 3));
        }
    
        @Test
        public void testFindRouteWithSingleRoad() throws InvalidEndPointException, InvalidRouteException {
            GameMap map = GameMap.createGameMap();
            
            Flag f1 = Flag.createFlag(new Point(1, 1));
            Flag f2 = Flag.createFlag(new Point(2, 2));
            
            map.placeFlag(f1);
            map.placeFlag(f2);
            
            map.placeRoad(f1.getPosition(), f2.getPosition());
            
            List<Point> way = map.findWay(f1.getPosition(), f2.getPosition());
            
            assertTrue(way.size() == 2);
            assertTrue(way.get(0).equals(new Point(1, 1)));
            assertTrue(way.get(1).equals(new Point(2,2)));
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
		
		Point[] points = new Point[] {
		new Point(1,1),    // F
		new Point(2, 1),   // F1
		new Point (3, 1),  // F2
		new Point(4, 1),   // F3
		new Point(5, 1),   // F4
		new Point(2, 4),   // F5
		new Point(3, 4),   // F6
		new Point(2, 6),   // F7
		new Point(3, 6),   // F8
		new Point(4, 2),   // F9
                new Point(6, 1)};  // F10
		
		Point target = new Point(6, 2);
		
		int i;
		for (i = 0; i < points.length; i++) {
			map.placeFlag(points[i]);
		}

		map.placeFlag(target);
		
		map.placeBuilding(Woodcutter.createWoodcutter(), target);
		
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
		List<Point> route = map.findWay(points[0], target);
		
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
                List<Road> roadsRoute = map.findWayInRoads(points[0], Flag.createFlag(target));

                System.out.println(roadsRoute);
                
		assertNotNull(roadsRoute);
		assertTrue(!roadsRoute.isEmpty());
		
		assertTrue(roadEqualsPoints(roadsRoute.get(0), points[0], points[1]));
                assertTrue(roadEqualsPoints(roadsRoute.get(1), points[1], points[2]));
                assertTrue(roadEqualsPoints(roadsRoute.get(2), points[2], points[9]));
                assertTrue(roadEqualsPoints(roadsRoute.get(3), points[9], target));

                
                roadsRoute = map.findWayInRoads(target, Flag.createFlag(points[0]));
		
		assertNotNull(roadsRoute);
		assertTrue(!roadsRoute.isEmpty());
		
                assertTrue(roadEqualsPoints(roadsRoute.get(0), points[9], target));
                assertTrue(roadEqualsPoints(roadsRoute.get(1), points[2], points[9]));
                assertTrue(roadEqualsPoints(roadsRoute.get(2), points[1], points[2]));
		assertTrue(roadEqualsPoints(roadsRoute.get(3), points[0], points[1]));
        }
}
