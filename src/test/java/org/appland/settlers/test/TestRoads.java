/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Woodcutter;
import static org.appland.settlers.test.Utils.roadEqualsFlags;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestRoads {

    @Test
    public void testGetNotExistingRoad() throws Exception {
        GameMap map = new GameMap(10, 10);

        assertNull(map.getRoad(new Flag(new Point(1, 1)), new Flag(new Point(2, 2))));
    }

    @Test(expected = InvalidRouteException.class)
    public void testUnreachableRoute() throws InvalidEndPointException, InvalidRouteException, Exception {
        GameMap map = new GameMap(10, 10);

        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(4, 2));

        map.placeFlag(f1);
        map.placeFlag(f2);

        map.placeAutoSelectedRoad(f1, f2);

        map.findWay(f1, new Flag(new Point(3, 3)));
    }

    @Test
    public void testFindRouteWithSingleRoad() throws InvalidEndPointException, InvalidRouteException, Exception {
        GameMap map = new GameMap(10, 10);

        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(4, 2));

        map.placeFlag(f1);
        map.placeFlag(f2);

        map.placeAutoSelectedRoad(f1, f2);
        
        List<Flag> way = map.findWay(f1, f2);

        assertTrue(way.size() == 2);
        assertTrue(way.get(0).equals(f1));
        assertTrue(way.get(1).equals(f2));
    }

    @Test
    public void testFindRoute() throws InvalidEndPointException, InvalidRouteException, Exception {
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

        GameMap map = new GameMap(20, 20);

        Flag[] points = new Flag[]{
            new Flag(1, 1), // F
            new Flag(3, 1), // F1
            new Flag(5, 1), // F2
            new Flag(7, 1), // F3
            new Flag(9, 1), // F4
            new Flag(2, 6), // F5
            new Flag(4, 6), // F6
            new Flag(2, 10), // F7
            new Flag(4, 10), // F8
            new Flag(4, 4), // F9
            new Flag(11, 1)};  // F10

        int i;
        for (i = 0; i < points.length; i++) {
            map.placeFlag(points[i]);
        }

        Woodcutter wc = new Woodcutter();
        map.placeBuilding(wc, new Point(10, 4));

        Flag target = wc.getFlag();

        map.placeAutoSelectedRoad(points[0], points[1]);
        map.placeAutoSelectedRoad(points[1], points[2]);
        map.placeAutoSelectedRoad(points[2], points[3]);
        map.placeAutoSelectedRoad(points[3], points[4]);
        map.placeAutoSelectedRoad(points[4], points[10]);
        map.placeAutoSelectedRoad(points[10], points[6]);
        map.placeAutoSelectedRoad(points[2], points[9]);
        map.placeAutoSelectedRoad(points[9], target);
        map.placeAutoSelectedRoad(points[1], points[5]);
        map.placeAutoSelectedRoad(points[5], points[6]);
        map.placeAutoSelectedRoad(points[1], points[7]);
        map.placeAutoSelectedRoad(points[7], points[8]);

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

    @Test
    public void testNeedsCourier() throws InvalidEndPointException, InvalidRouteException, Exception {
        GameMap map = new GameMap(10, 10);

        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(4, 2));

        map.placeFlag(f1);
        map.placeFlag(f2);

        Road r = map.placeAutoSelectedRoad(f1, f2);

        assertTrue(r.needsCourier());

        r.promiseCourier();

        assertFalse(r.needsCourier());

        Courier c = new Courier(map);

        map.placeWorker(c, f1);
        map.assignCourierToRoad(c, r);

        assertFalse(r.needsCourier());
    }

    @Test(expected=Exception.class)
    public void testAssignCourierAtWrongFlagToRoad() throws Exception {
        GameMap map = new GameMap(10, 10);

        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(4, 2));
        Flag f3 = new Flag(new Point(4, 4));

        map.placeFlag(f1);
        map.placeFlag(f2);
        map.placeFlag(f3);

        Road r = map.placeAutoSelectedRoad(f1, f2);

        Courier c = new Courier(map);

        map.placeWorker(c, f3);
        map.assignCourierToRoad(c, r);
    }

    @Test(expected=Exception.class)
    public void testAssignWorkerToRoadNotOnMap() throws Exception {
        GameMap map = new GameMap(10, 10);

        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(4, 2));
        Road r  = new Road(f1, f2);

        map.placeFlag(f1);
        map.placeFlag(f2);

        Courier c = new Courier(map);

        map.placeWorker(c, f1);
        map.assignCourierToRoad(c, r);
    }

    @Test(expected=Exception.class)
    public void testRoadCanNotShareSegment() throws Exception {
        GameMap map = new GameMap(10, 10);

        Flag  commonStart = new Flag(new Point(1, 1));
        Flag  end1        = new Flag(new Point(1, 3));
        Flag  end2        = new Flag(new Point(3, 1));
        Point middlePoint = new Point(2, 2);
        
        List<Point> middlePoints = new ArrayList<>();
        middlePoints.add(middlePoint);
        
        map.placeFlag(commonStart);
        map.placeFlag(end1);
        map.placeFlag(end2);

        map.placeRoad(commonStart, middlePoints, end1);
        map.placeRoad(commonStart, middlePoints, end2);
    }

    @Test(expected=Exception.class)
    public void testRoadsCanNotCross() throws Exception {
        GameMap map = new GameMap(10, 10);

        Flag  start1      = new Flag(new Point(1, 1));
        Flag  end1        = new Flag(new Point(3, 3));
        Flag  start2      = new Flag(new Point(1, 3));
        Flag  end2        = new Flag(new Point(3, 1));
        Point middlePoint = new Point(2, 2);
        
        List<Point> middlePoints = new ArrayList<>();
        middlePoints.add(middlePoint);
        
        map.placeFlag(start1);
        map.placeFlag(start2);
        map.placeFlag(end1);
        map.placeFlag(end2);

        map.placeRoad(start1, middlePoints, end1);
        map.placeRoad(start2, middlePoints, end2);
    }

    @Test
    public void testPossibleDirectConnectionsFromFlag() throws Exception {
        GameMap map = new GameMap(10, 10);

        Flag f = new Flag(new Point(3, 5));

        map.placeFlag(f);
    
        List<Point> points = map.getPossibleAdjacentRoadConnections(f.getPosition());
        
        assertTrue(points.size() == 8);
        assertTrue(points.contains(new Point(2, 6)));
        assertTrue(points.contains(new Point(2, 4)));
        assertTrue(points.contains(new Point(4, 6)));
        assertTrue(points.contains(new Point(4, 4)));

        assertTrue(points.contains(new Point(3, 7)));
        assertTrue(points.contains(new Point(3, 3)));
        assertTrue(points.contains(new Point(1, 5)));
        assertTrue(points.contains(new Point(5, 5)));
    }

    @Test
    public void testPossibleDirectConnectionsInCorners() throws Exception {
        GameMap map = new GameMap(10, 10);

        Point downRight = new Point(9, 1);
        Point downLeft  = new Point(1, 1);
        Point upRight   = new Point(9, 9);
        Point upLeft    = new Point(1, 9);
    
        List<Point> points = map.getPossibleAdjacentRoadConnections(downRight);
        
        assertTrue(points.size() == 3);
        assertTrue(points.contains(new Point(8, 2)));
        assertTrue(points.contains(new Point(9, 3)));
        assertTrue(points.contains(new Point(7, 1)));

        points = map.getPossibleAdjacentRoadConnections(downLeft);
    
        assertTrue(points.size() == 3);
        assertTrue(points.contains(new Point(2, 2)));
        assertTrue(points.contains(new Point(1, 3)));
        assertTrue(points.contains(new Point(3, 1)));

        points = map.getPossibleAdjacentRoadConnections(upRight);
        
        assertTrue(points.size() == 3);
        assertTrue(points.contains(new Point(8, 8)));
        assertTrue(points.contains(new Point(7, 9)));
        assertTrue(points.contains(new Point(9, 7)));
        
        points = map.getPossibleAdjacentRoadConnections(upLeft);
        
        assertTrue(points.size() == 3);
        assertTrue(points.contains(new Point(2, 8)));
        assertTrue(points.contains(new Point(3, 9)));
        assertTrue(points.contains(new Point(1, 7)));
    }

    @Test
    public void testPossibleDirectConnectionsOnSides() throws Exception {
        GameMap map = new GameMap(10, 10);

        Point right = new Point(9, 5);
        Point left  = new Point(1, 5);
        Point up    = new Point(5, 9);
        Point down  = new Point(5, 1);
    
        List<Point> points = map.getPossibleAdjacentRoadConnections(right);
        
        assertTrue(points.size() == 5);
        assertTrue(points.contains(new Point(8, 4)));
        assertTrue(points.contains(new Point(8, 6)));
        assertTrue(points.contains(new Point(7, 5)));
        assertTrue(points.contains(new Point(9, 3)));
        assertTrue(points.contains(new Point(9, 7)));

        points = map.getPossibleAdjacentRoadConnections(left);
    
        assertTrue(points.size() == 5);
        assertTrue(points.contains(new Point(2, 4)));
        assertTrue(points.contains(new Point(2, 6)));
        assertTrue(points.contains(new Point(3, 5)));
        assertTrue(points.contains(new Point(1, 3)));
        assertTrue(points.contains(new Point(1, 7)));

        points = map.getPossibleAdjacentRoadConnections(up);
        
        assertTrue(points.size() == 5);
        assertTrue(points.contains(new Point(4, 8)));
        assertTrue(points.contains(new Point(6, 8)));
        assertTrue(points.contains(new Point(5, 7)));
        assertTrue(points.contains(new Point(3, 9)));
        assertTrue(points.contains(new Point(7, 9)));

        
        points = map.getPossibleAdjacentRoadConnections(down);
        
        assertTrue(points.size() == 5);
        assertTrue(points.contains(new Point(4, 2)));
        assertTrue(points.contains(new Point(6, 2)));
        assertTrue(points.contains(new Point(5, 3)));
        assertTrue(points.contains(new Point(3, 1)));
        assertTrue(points.contains(new Point(7, 1)));
    }
}