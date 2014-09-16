/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidRouteException;
import static org.appland.settlers.model.Material.BEER;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author johan
 */
public class TestRoads {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetNotExistingRoad() throws Exception {
        GameMap map = new GameMap(10, 10);

        assertNull(map.getRoad(new Point(1, 1), new Point(2, 2)));
    }

    @Test
    public void testUnreachableRoute() throws InvalidEndPointException, InvalidRouteException, Exception {
        GameMap map = new GameMap(15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);

        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(4, 2));

        map.placeFlag(f1);
        map.placeFlag(f2);

        Road r = map.placeAutoSelectedRoad(f1, f2);

        assertNull(map.findWayWithExistingRoads(f1.getPosition(), new Point(3, 3)));
    }

    @Test
    public void testFindRouteWithSingleRoad() throws InvalidEndPointException, InvalidRouteException, Exception {
        GameMap map = new GameMap(15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(4, 2));

        map.placeFlag(f1);
        map.placeFlag(f2);

        map.placeAutoSelectedRoad(f1, f2);
        
        List<Point> way = map.findWayWithExistingRoads(f1.getPosition(), f2.getPosition());
        
        assertTrue(way.size() == 3);
        assertTrue(way.get(0).equals(f1.getPosition()));
        assertTrue(way.get(2).equals(f2.getPosition()));
    }

    @Test
    public void testFindRoute() throws InvalidEndPointException, InvalidRouteException, Exception {
        /*
         * F--F1--F2--F3--F4--F10----------------|
         *    |    |                             |
         *    |    |---F9--Target building       |
         *    |                                  |
         *    |                                  |
         *    |---F5---F6------------------------
         *    |
         *    |---F7---F8
         */

        GameMap map = new GameMap(30, 30);

        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(), point0);
        
        Point[] points = new Point[]{
            new Point(1,  3), // F
            new Point(5,  3), // F1
            new Point(7, 3), // F2
            new Point(9, 3), // F3
            new Point(11, 3), // F4
            new Point(4,  10), // F5
            new Point(6,  10), // F6
            new Point(4,  16), // F7
            new Point(6,  16), // F8
            new Point(7,  5), // F9
            new Point(13, 3)};  // F10

        int i;
        for (i = 0; i < points.length; i++) {
            map.placeFlag(points[i]);
        }

        Point target = new Point(9, 5);
        
        map.placeFlag(target);

        map.placeRoad(points[0], new Point(3, 3), points[1]);
        map.placeRoad(points[1], points[1].upRight(), points[2]);
        
        map.placeRoad(points[2], points[2].downRight(), points[3]);
        
        map.placeRoad(points[3], points[3].upRight(), points[4]);
        map.placeRoad(points[4], points[4].upRight(), points[10]);
        map.placeRoad(points[2], points[2].upRight(), points[9]);
        map.placeRoad(points[9], points[9].upRight(), target);
        map.placeAutoSelectedRoad(points[1], points[5]);
        map.placeRoad(points[5], points[5].upRight(), points[6]);
        map.placeAutoSelectedRoad(points[1], points[7]);
        map.placeRoad(points[7], points[7].upRight(), points[8]);

        /* Test route with List<Point> */
        List<Point> route = map.findWayWithExistingRoads(points[0], target);

        assertNotNull(route);
        assertTrue(!route.isEmpty());

        assertTrue(route.size() < 10);
        assertEquals(route.get(0), points[0]);
        assertEquals(route.get(route.size() - 1), target);

        Utils.assertNoStepDirectlyUpwards(route);
        
        route = map.findWayWithExistingRoads(target, points[0]);

        assertTrue(route.size() < 10);
        assertEquals(route.get(0), target);
        assertEquals(route.get(route.size() - 1), points[0]);

        Utils.assertNoStepDirectlyUpwards(route);
        
        route = map.findWayWithExistingRoads(points[1], points[2]);

        assertTrue(route.size() == 3);
        assertEquals(route.get(0), points[1]);
        assertEquals(route.get(1), points[1].upRight());
        assertEquals(route.get(2), points[2]);
    }

    @Test
    public void testNeedsCourier() throws InvalidEndPointException, InvalidRouteException, Exception {
        GameMap map = new GameMap(15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(4, 2));

        map.placeFlag(f1);
        map.placeFlag(f2);

        Road r = map.placeAutoSelectedRoad(f1, f2);

        assertTrue(r.needsCourier());

        Courier c = new Courier(map);

        map.placeWorker(c, f1);
        c.assignToRoad(r);
        
        assertFalse(r.needsCourier());
        
        Utils.fastForwardUntilWorkersReachTarget(map, c);

        assertFalse(r.needsCourier());
    }

    @Test(expected=Exception.class)
    public void testAssignWorkerToRoadNotOnMap() throws Exception {
        GameMap map = new GameMap(15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(4, 2));

        map.placeFlag(f1);
        map.placeFlag(f2);

        Courier c = new Courier(map);

        Road r = map.placeRoad(f1.getPosition(), f2.getPosition());
        map.placeWorker(c, f1);
        c.assignToRoad(r);
    }

    @Test(expected=Exception.class)
    public void testAssignTwoWorkersToRoad() throws Exception {
        GameMap map = new GameMap(15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(4, 2));

        map.placeFlag(f1);
        map.placeFlag(f2);

        Courier c  = new Courier(map);
        Courier c2 = new Courier(map);

        Road r = map.placeRoad(f1.getPosition(), f2.getPosition());
        
        map.placeWorker(c, f1);
        map.placeWorker(c2, f1);
        c.assignToRoad(r);
        
        c2.assignToRoad(r);
    }

    @Test(expected=Exception.class)
    public void testRoadCanNotShareSegment() throws Exception {
        GameMap map = new GameMap(15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Flag  commonStart = new Flag(new Point(1, 1));
        Flag  end1        = new Flag(new Point(1, 3));
        Flag  end2        = new Flag(new Point(3, 1));
        Point middlePoint = new Point(2, 2);
        
        List<Point> wayPoints1 = new ArrayList<>();
        List<Point> wayPoints2 = new ArrayList<>();
        wayPoints1.add(commonStart.getPosition());
        wayPoints1.add(middlePoint);
        wayPoints1.add(end1.getPosition());
        
        wayPoints2.add(commonStart.getPosition());
        wayPoints2.add(middlePoint);
        wayPoints2.add(end2.getPosition());

        map.placeRoad(wayPoints1);
        map.placeRoad(wayPoints2);
    }

    @Test
    public void testWayPointsAreCorrectInSingleSegmentRoad() {
	//TODO: IMPLEMENT!
    }

    @Test(expected=Exception.class)
    public void testRoadsCanNotCross() throws Exception {
        GameMap map = new GameMap(15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Flag  start1      = new Flag(new Point(1, 1));
        Flag  end1        = new Flag(new Point(3, 3));
        Flag  start2      = new Flag(new Point(1, 3));
        Flag  end2        = new Flag(new Point(3, 1));
        Point middlePoint = new Point(2, 2);
        
        List<Point> wayPoints1 = new ArrayList<>();
        List<Point> wayPoints2 = new ArrayList<>();
	wayPoints1.add(start1.getPosition());
        wayPoints1.add(middlePoint);
        wayPoints1.add(end1.getPosition());
        
	wayPoints2.add(start2.getPosition());
        wayPoints2.add(middlePoint);
        wayPoints2.add(end2.getPosition());

        map.placeFlag(start1);
        map.placeFlag(start2);
        map.placeFlag(end1);
        map.placeFlag(end2);

        map.placeRoad(wayPoints1);
        map.placeRoad(wayPoints2);
    }

    @Test
    public void testWayPointsEqualsChosenRoad() throws Exception {
        GameMap map = new GameMap(15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Flag  start       = new Flag(new Point(1, 1));
        Flag  end         = new Flag(new Point(3, 3));
        Point middlePoint = new Point(2, 2);
        
        List<Point> wayPoints = new ArrayList<>();
	wayPoints.add(start.getPosition());
        wayPoints.add(middlePoint);
	wayPoints.add(end.getPosition());
        
        map.placeFlag(start);
        map.placeFlag(end);

        map.placeRoad(wayPoints);

	Road r = map.getRoad(start.getPosition(), end.getPosition());
        
        wayPoints = r.getWayPoints();
        
	assertTrue(wayPoints.size() == 3);
	assertTrue(wayPoints.get(0).equals(start.getPosition()));
	assertTrue(wayPoints.get(1).equals(middlePoint));
	assertTrue(wayPoints.get(2).equals(end.getPosition()));
    }

    @Test(expected = Exception.class)
    public void testLargerStepThanOneIsNotOk() throws Exception {
        GameMap map = new GameMap(15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Flag  start       = new Flag(new Point(1, 1));
        Flag  end         = new Flag(new Point(4, 4));
        Point middlePoint = new Point(2, 2);
        
        List<Point> middlePoints = new ArrayList<>();
        middlePoints.add(start.getPosition());
        middlePoints.add(middlePoint);
        middlePoints.add(end.getPosition());
        
        map.placeFlag(start);
        map.placeFlag(end);

        map.placeRoad(middlePoints);
    }
    
    @Test
    public void testPossibleDirectConnectionsFromFlag() throws Exception {
        GameMap map = new GameMap(15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Flag f = new Flag(new Point(3, 5));

        map.placeFlag(f);
    
        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(f.getPosition());
        
        assertTrue(points.size() == 6);
        assertTrue(points.contains(new Point(2, 6)));
        assertTrue(points.contains(new Point(2, 4)));
        assertTrue(points.contains(new Point(4, 6)));
        assertTrue(points.contains(new Point(4, 4)));

        assertFalse(points.contains(new Point(3, 7)));
        assertFalse(points.contains(new Point(3, 3)));
        assertTrue(points.contains(new Point(1, 5)));
        assertTrue(points.contains(new Point(5, 5)));
    }

    @Test
    public void testPossibleDirectConnectionsInCorners() throws Exception {
        GameMap map = new GameMap(10, 10);

        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(), point0);
        
        Point downRight = new Point(9, 1);
        Point downLeft  = new Point(1, 1);
        Point upRight   = new Point(9, 9);
        Point upLeft    = new Point(1, 9);
    
        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(downRight);
        
        assertTrue(points.size() == 2);
        assertTrue(points.contains(new Point(8, 2)));
        assertFalse(points.contains(new Point(9, 3)));
        assertTrue(points.contains(new Point(7, 1)));

        points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(downLeft);
    
        assertTrue(points.size() == 2);
        assertTrue(points.contains(new Point(2, 2)));
        assertFalse(points.contains(new Point(1, 3)));
        assertTrue(points.contains(new Point(3, 1)));

        points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(upRight);
        
        assertTrue(points.size() == 2);
        assertTrue(points.contains(new Point(8, 8)));
        assertTrue(points.contains(new Point(7, 9)));
        assertFalse(points.contains(new Point(9, 7)));
        
        points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(upLeft);
        
        assertTrue(points.size() == 2);
        assertTrue(points.contains(new Point(2, 8)));
        assertTrue(points.contains(new Point(3, 9)));
        assertFalse(points.contains(new Point(1, 7)));
    }

    @Test
    public void testPossibleDirectConnectionsOnSides() throws Exception {
        GameMap map = new GameMap(10, 10);

        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(), point0);
        
        Point right = new Point(9, 5);
        Point left  = new Point(1, 5);
        Point up    = new Point(5, 9);
        Point down  = new Point(5, 1);
    
        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(right);
        
        assertTrue(points.size() == 3);
        assertTrue(points.contains(new Point(8, 4)));
        assertTrue(points.contains(new Point(8, 6)));
        assertTrue(points.contains(new Point(7, 5)));
        assertFalse(points.contains(new Point(9, 3)));
        assertFalse(points.contains(new Point(9, 7)));

        points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(left);
    
        assertTrue(points.size() == 3);
        assertTrue(points.contains(new Point(2, 4)));
        assertTrue(points.contains(new Point(2, 6)));
        assertTrue(points.contains(new Point(3, 5)));
        assertFalse(points.contains(new Point(1, 3)));
        assertFalse(points.contains(new Point(1, 7)));

        points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(up);
        
        assertTrue(points.size() == 4);
        assertTrue(points.contains(new Point(4, 8)));
        assertTrue(points.contains(new Point(6, 8)));
        assertFalse(points.contains(new Point(5, 7)));
        assertTrue(points.contains(new Point(3, 9)));
        assertTrue(points.contains(new Point(7, 9)));

        
        points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(down);
        
        assertTrue(points.size() == 4);
        assertTrue(points.contains(new Point(4, 2)));
        assertTrue(points.contains(new Point(6, 2)));
        assertFalse(points.contains(new Point(5, 3)));
        assertTrue(points.contains(new Point(3, 1)));
        assertTrue(points.contains(new Point(7, 1)));
    }

    @Test
    public void testNoPossibleConnectionUpOrDownWithSurroundingRoads() throws Exception {
        GameMap map = new GameMap(30, 30);

        map.placeBuilding(new Headquarter(), new Point(16, 10));
        map.placeFlag(new Flag(new Point(16, 12)));
        map.placeFlag(new Flag(new Point(20, 12)));
        map.placeFlag(new Flag(new Point(12, 12)));
        map.placeRoad(Arrays.asList(new Point[] {new Point(20, 12), new Point(18, 12), new Point(17, 13), new Point(18, 14), new Point(17, 15), new Point(16, 16), new Point(15, 15), new Point(14, 14), new Point(15, 13), new Point(14, 12), new Point(12, 12)}));

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(new Point(16, 12));
    
        assertFalse(points.contains(new Point(16, 14)));

        map.placeFlag(new Flag(new Point(21, 25)));
        map.placeFlag(new Flag(new Point(25, 25)));
        map.placeFlag(new Flag(new Point(17, 25)));
        map.placeRoad(Arrays.asList(new Point[] {new Point(25, 25), new Point(23, 25), new Point(22, 24), new Point(23, 23), new Point(22, 22), new Point(21, 21), new Point(20, 22), new Point(19, 23), new Point(20, 24), new Point(19, 25), new Point(17, 25)}));

        assertFalse(points.contains(new Point(21, 23)));
    }

    @Test
    public void testNoPossibleConnectionUnderBuilding() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(), point0);
        
        Point point2 = new Point(12, 8);
        Building building1 = map.placeBuilding(new Woodcutter(), point2);
        Point point3 = new Point(10, 8);
        Flag flag0 = map.placeFlag(point3);

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(point3);
    
        assertFalse(points.contains(point2));
    }

    @Test
    public void testNoPossibleConnectionUnderStone() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(), point0);
        
        Point point2 = new Point(12, 8);
        Stone stone0 = map.placeStone(point2);
        Point point3 = new Point(10, 8);
        Flag flag0 = map.placeFlag(point3);

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(point3);
    
        assertFalse(points.contains(point2));
    }

    @Test
    public void testNoPossibleConnectionUnderTree() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(), point0);
        
        Point point2 = new Point(12, 8);
        Tree tree0 = map.placeTree(point2);
        Point point3 = new Point(10, 8);
        Flag flag0 = map.placeFlag(point3);

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(point3);
    
        assertFalse(points.contains(point2));
    }
    
    @Test
    public void testNoPossibleConnectionsOutsideBorder() throws Exception {
        GameMap map = new GameMap(100, 100);
        Point point0 = new Point(50, 50);
        
        map.placeBuilding(new Headquarter(), point0);
        
        Point point1 = new Point(49, 71);
        Point point2 = new Point(51, 71);
        Point point3 = new Point(50, 70);
        Flag flag0 = map.placeFlag(point3);

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(point3);
        
        assertFalse(points.contains(point2));
        assertFalse(points.contains(point1));
    }
    
    @Test
    public void testPlaceRoadWithVarargs() throws Exception {
        GameMap map = new GameMap(15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(4, 2));

        map.placeFlag(f1);
        map.placeFlag(f2);

        Road r = map.placeRoad(f1.getPosition(), new Point(3, 1), f2.getPosition());
    }

    @Test
    public void testConnectNewRoadToFlagInExistingRoad() throws Exception {
        GameMap map = new GameMap(20, 20);
        map.placeBuilding(new Headquarter(), new Point(5, 5));
        map.placeFlag(new Flag(new Point(12, 4)));
        map.placeFlag(new Flag(new Point(14, 6)));
        map.placeRoad(new Point(12, 4), new Point(13, 5), new Point(14, 6));
        map.placeFlag(new Flag(new Point(16, 8)));
        map.placeRoad(new Point(14, 6), new Point(15, 7), new Point(16, 8));
        map.placeFlag(new Flag(new Point(16, 4)));
        map.placeRoad(new Point(16, 4), new Point(15, 5), new Point(14, 6));
    }

    @Test
    public void testPlaceFlagInExistingRoadSplitsTheRoad() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        map.placeFlag(new Flag(new Point(9, 5)));
        map.placeFlag(new Flag(new Point(13, 9)));
        
        Point start = new Point(9, 5);
        Point end = new Point(13, 9);
        Point m1 = new Point(10, 6);
        Point m2 = new Point(11, 7);
        Point m3 = new Point(12, 8);
        map.placeRoad(start, m1, m2, m3, end);
        
        assertTrue(map.getRoads().size() == 2);
        
        map.placeFlag(new Flag(m2));

        assertTrue(map.getRoads().size() == 3);
        List<Road> roads = new ArrayList<>();
        roads.addAll(map.getRoads());
        
        roads.remove(map.getRoad(point0, point0.downRight()));
        
        Road r1 = roads.get(0);
        Road r2 = roads.get(1);

        assertTrue((r1.getStart().equals(start) && r1.getEnd().equals(m2)) ||
                   (r1.getStart().equals(m2) && r1.getEnd().equals(start)) ||
                   (r2.getStart().equals(start) && r2.getEnd().equals(m2)) ||
                   (r2.getStart().equals(m2) && r2.getEnd().equals(start)));

        assertTrue((r1.getStart().equals(m2) && r1.getEnd().equals(end)) ||
                   (r1.getStart().equals(end) && r1.getEnd().equals(m2)) ||
                   (r2.getStart().equals(m2) && r2.getEnd().equals(end)) ||
                   (r2.getStart().equals(end) && r2.getEnd().equals(m2)));
    }

    @Test
    public void testIdleCourierIsAssignedWhenRoadIsSplit() throws Exception {
        GameMap map = new GameMap(20, 20);
        Building hq = map.placeBuilding(new Headquarter(), new Point(5, 5));
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(endPoint);
        Road road = map.placeRoad(hq.getFlag().getPosition(), 
                                  middlePoint1, 
                                  middlePoint2,
                                  middlePoint3,
                                  endPoint);
        
        /* Place original courier */
        Courier courier = new Courier(map);
        map.placeWorker(courier, endFlag);
        courier.assignToRoad(road);
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getAssignedRoad(), road);
        assertEquals(road.getCourier(), courier);
        assertTrue(courier.isIdle());
        assertTrue(courier.isAt(middlePoint2));
        
        /* Split road */
        map.placeFlag(new Flag(middlePoint2));

        assertTrue(courier.isWalkingToRoad());
        assertTrue(courier.getAssignedRoad().getStart().equals(middlePoint2) ||
                   courier.getAssignedRoad().getEnd().equals(middlePoint2));
    }
    
    @Test
    public void testCourierDeliveringCargoFinishesDeliveryAndIsAssignedWhenRoadIsSplit() throws Exception {
        GameMap map = new GameMap(20, 20);
        Building hq = map.placeBuilding(new Headquarter(), new Point(5, 5));
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(endPoint);
        Road road = map.placeRoad(hq.getFlag().getPosition(), 
                                  middlePoint1, 
                                  middlePoint2,
                                  middlePoint3,
                                  endPoint);
        
        /* Place original courier */
        Courier courier = new Courier(map);
        map.placeWorker(courier, endFlag);
        courier.assignToRoad(road);
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getAssignedRoad(), road);
        assertEquals(road.getCourier(), courier);
        assertTrue(courier.isIdle());
        assertTrue(courier.isAt(middlePoint2));
        
        /* Place the cargo on the end of the road, furthest away from the hq */
        Cargo cargo = new Cargo(BEER, map);
        endFlag.putCargo(cargo);
        cargo.setTarget(hq);
        
        map.stepTime();
        
        assertEquals(courier.getTarget(), endPoint);
        
        /* Make the courier pick up a cargo and start walking to deliver it */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, endPoint);
        
        map.stepTime();
        
        assertTrue(courier.isTraveling());
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), hq.getPosition());
        
        /* Split road with the courier on the road further away from the hq */
        map.placeFlag(new Flag(middlePoint2));

        assertFalse(courier.isWalkingToRoad());
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), middlePoint2);

        assertTrue((courier.getAssignedRoad().getStart().equals(middlePoint2) && courier.getAssignedRoad().getEnd().equals(endPoint)) ||
                   (courier.getAssignedRoad().getEnd().equals(middlePoint2) && courier.getAssignedRoad().getStart().equals(endPoint)));
    }
    
    @Test
    public void testCourierFarFromToBuildingDeliveringCargoFinishesDeliveryAndBecomesIdleWhenRoadIsSplit() throws Exception {
        GameMap map = new GameMap(20, 20);
        Storage hq = new Headquarter();
        map.placeBuilding(hq, new Point(5, 5));
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(endPoint);
        Road road = map.placeRoad(hq.getFlag().getPosition(), 
                                  middlePoint1, 
                                  middlePoint2,
                                  middlePoint3,
                                  endPoint);
        
        /* Place original courier */
        Courier courier = new Courier(map);
        map.placeWorker(courier, endFlag);
        courier.assignToRoad(road);
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getAssignedRoad(), road);
        assertEquals(road.getCourier(), courier);
        assertTrue(courier.isIdle());
        assertTrue(courier.isAt(middlePoint2));
        
        /* Make the courier pick up a cargo and start walking to deliver it */
        Cargo cargo = new Cargo(BEER, map);
        endFlag.putCargo(cargo);
        cargo.setTarget(hq);
        
        map.stepTime();
        
        assertEquals(courier.getTarget(), endPoint);
        
        /* Fast forward until the courier picks up the cargo */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, endPoint);
        
        map.stepTime();
        
        assertTrue(courier.isTraveling());
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), hq.getPosition());
        
        /* Split road with the courier on the new road further away from the 
           headquarter */
        Flag middleFlag = map.placeFlag(new Flag(middlePoint2));

        assertFalse(courier.isWalkingToRoad());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier has changed target to the new middle flag */
        assertEquals(courier.getTarget(), middlePoint2);
        
        /* Let the courier leave the cargo */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, middlePoint2);
        
        assertNull(courier.getCargo());
        assertTrue(middleFlag.getStackedCargo().contains(cargo));
        
        /* Verify that the courier becomes idle after delivery */
        assertEquals(courier.getTarget(), middlePoint3);
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertTrue(courier.isIdle());
        assertEquals(courier.getPosition(), middlePoint3);
    }
        
    @Test
    public void testCourierCloseToBuildingDeliveringCargoFinishesDeliveryAndBecomesIdleWhenRoadIsSplit() throws Exception {
        GameMap map = new GameMap(20, 20);
        Storage hq = new Headquarter();
        map.placeBuilding(hq, new Point(5, 5));
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(endPoint);
        Road road = map.placeRoad(hq.getFlag().getPosition(), 
                                  middlePoint1, 
                                  middlePoint2,
                                  middlePoint3,
                                  endPoint);
        
        /* Place original courier */
        Courier courier = new Courier(map);
        map.placeWorker(courier, endFlag);
        courier.assignToRoad(road);
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getAssignedRoad(), road);
        assertEquals(road.getCourier(), courier);
        assertTrue(courier.isIdle());
        assertTrue(courier.isAt(middlePoint2));
        
        /* Make the courier pick up a cargo and start walking to deliver it */
        Cargo cargo = new Cargo(BEER, map);
        endFlag.putCargo(cargo);
        cargo.setTarget(hq);
        
        map.stepTime();
        
        assertEquals(courier.getTarget(), endPoint);
        
        /* Fast forward until the courier picks up the cargo */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, endPoint);
        
        map.stepTime();
        
        assertTrue(courier.isTraveling());
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), hq.getPosition());
        
        /* Let the courier pass the middle of the road */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, middlePoint1);
        
        /* Split road with the courier on the new road closer to the 
           headquarter */
        Flag middleFlag = map.placeFlag(new Flag(middlePoint2));

        assertFalse(courier.isWalkingToRoad());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier is sitll targeting the hq */
        assertEquals(courier.getTarget(), hq.getPosition());
        
        /* Let the courier leave the cargo */
        assertTrue(hq.getAmount(BEER) == 0);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, hq.getPosition());
        
        assertNull(courier.getCargo());
        assertTrue(hq.getAmount(BEER) == 1);
        
        /* Let the courier walk back to the hq's flag */
        assertEquals(courier.getTarget(), hq.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, hq.getFlag().getPosition());
        
        /* Verify that the courier becomes idle after delivery */
        assertEquals(courier.getTarget(), middlePoint1);
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertTrue(courier.isIdle());
        assertEquals(courier.getPosition(), middlePoint1);
    }

    @Test
    public void testCourierWalkingToAssignedRoadAdaptsWhenItsRoadIsSplit() throws Exception {
        GameMap map = new GameMap(20, 20);
        Building hq = map.placeBuilding(new Headquarter(), new Point(5, 5));
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(endPoint);
        Road road = map.placeRoad(hq.getFlag().getPosition(), 
                                  middlePoint1, 
                                  middlePoint2,
                                  middlePoint3,
                                  endPoint);
        
        /* Place original courier */
        Courier courier = new Courier(map);
        map.placeWorker(courier, endFlag);
        courier.assignToRoad(road);
        
        map.stepTime();
        
        assertEquals(courier.getAssignedRoad(), road);
        assertEquals(road.getCourier(), courier);
        assertTrue(courier.isWalkingToRoad());
                
        /* Split road */
        Flag middleFlag = map.placeFlag(new Flag(middlePoint2));

        assertTrue(courier.isWalkingToRoad());
        assertTrue(courier.getTarget().equals(middlePoint1) ||
                   courier.getTarget().equals(middlePoint3));

        Utils.fastForwardUntilWorkersReachTarget(map, courier);

        assertTrue(courier.isIdle());        
    }
    
    @Test
    public void testNewCourierIsDispatchedWhenRoadIsSplit() throws Exception {
        GameMap map = new GameMap(20, 20);
        Building hq = map.placeBuilding(new Headquarter(), new Point(5, 5));

        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(endPoint);
        Road road = map.placeRoad(hq.getFlag().getPosition(), 
                                  middlePoint1, 
                                  middlePoint2,
                                  middlePoint3,
                                  endPoint);
        
        /* Place original courier */
        Courier courier = new Courier(map);
        map.placeWorker(courier, endFlag);
        courier.assignToRoad(road);
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getAssignedRoad(), road);
        assertEquals(road.getCourier(), courier);
        assertTrue(courier.isIdle());
        assertTrue(courier.isAt(middlePoint2));
        
        /* Split road */
        assertTrue(map.getRoads().size() == 2);
        
        map.placeFlag(new Flag(middlePoint2));

        assertTrue(map.getRoads().size() == 3);
        assertTrue(map.getAllWorkers().size() == 2);
        
        /* Step time to let the headquarter assign a courier to the new road */
        assertTrue(map.getRoadsThatNeedCouriers().size() == 1);
        Road r = map.getRoadsThatNeedCouriers().get(0);
        
        assertEquals(map.getClosestStorage(r.getStart()), hq);
        
        map.stepTime();
        
        assertTrue(map.getAllWorkers().size() == 3);
        
        Worker w2 = null;
        
        for (Worker w : map.getAllWorkers()) {
            if (! (w instanceof Courier)) {
                continue;
            }
        
            if (w.equals(courier)) {
                continue;
            }
        
            w2 = w;
            
            break;
        }
        
        assertTrue(w2 instanceof Courier);
        
        Courier secondCourier = (Courier)w2;
        
        assertNotNull(secondCourier.getAssignedRoad());
        assertFalse(secondCourier.getAssignedRoad().equals(road));
    }

    @Test
    public void testCourierDeliversCorrectlyToBuildingAfterItsRoadIsSplit() throws Exception {
        GameMap map = new GameMap(20, 20);
        Storage hq = new Headquarter();
        map.placeBuilding(hq, new Point(5, 5));
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(endPoint);
        Road road = map.placeRoad(hq.getFlag().getPosition(), 
                                  middlePoint1, 
                                  middlePoint2,
                                  middlePoint3,
                                  endPoint);
        
        /* Place original courier */
        Courier courier = new Courier(map);
        map.placeWorker(courier, endFlag);
        courier.assignToRoad(road);
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertEquals(courier.getAssignedRoad(), road);
        assertEquals(road.getCourier(), courier);
        assertTrue(courier.isIdle());
        assertTrue(courier.isAt(middlePoint2));
        
        /* Make the courier pick up a cargo and start walking to deliver it */
        Cargo cargo = new Cargo(BEER, map);
        endFlag.putCargo(cargo);
        cargo.setTarget(hq);
        
        map.stepTime();
        
        assertEquals(courier.getTarget(), endPoint);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, endPoint);
        
        map.stepTime();
        
        assertTrue(courier.isTraveling());
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), hq.getPosition());
        
        /* Let the courier get close to the hq */
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, middlePoint1);
        
        /* Split road with the courier close to the hq*/
        Flag middleFlag = map.placeFlag(new Flag(middlePoint2));
        
        assertTrue(hq.getAmount(BEER) == 0);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, hq.getPosition());
        
        assertNull(courier.getCargo());
        assertTrue(hq.getAmount(BEER) == 1);
    }
    
    @Test
    public void testRoadCanNotOverlapExistingFlag() throws Exception {
        GameMap map = new GameMap(20, 20);
        map.placeBuilding(new Headquarter(), new Point(5, 5));
        map.placeFlag(new Flag(new Point(10, 4)));
        map.placeFlag(new Flag(new Point(13, 7)));
        map.placeRoad(new Point(10, 4), new Point(11, 5), new Point(12, 6), new Point(13, 7));
        map.placeFlag(new Flag(new Point(14, 4)));
        map.placeFlag(new Flag(new Point(9, 7)));

        thrown.expect(Exception.class);
        map.placeRoad(new Point(14, 4), new Point(12, 4), new Point(10, 4), new Point(9, 5), new Point(8, 6), new Point(9, 7));
    }
    
    @Test
    public void testOnlyOneCourierIsAssignedToNewRoad() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(12, 6);
        Building building1 = map.placeBuilding(new Woodcutter(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(10, 4);
        Point point5 = new Point(12, 4);
        Point point6 = new Point(13, 5);
        Road road0 = map.placeRoad(point2, point3, point4, point5, point6);
        
        assertTrue(road0.needsCourier());
        assertTrue(map.getAllWorkers().size() == 1);
        
        List<Worker> workersBefore = new LinkedList<>();
        workersBefore.addAll(map.getAllWorkers());
        
        /* Step time to let the headquarter send new workers */
        map.stepTime();
        
        /* Let the new courier reach its road */
        assertTrue(map.getAllWorkers().size() == 2);
        
        /* Find the added courier */
        List<Worker> workersAfter = new LinkedList<>();
        workersAfter.addAll(map.getAllWorkers());
        workersAfter.removeAll(workersBefore);
        
        Worker w = workersAfter.get(0);
        
        Utils.fastForwardUntilWorkersReachTarget(map, w);
        
        assertFalse(road0.needsCourier());
        assertEquals(road0.getCourier(), w);
        
        Utils.fastForward(100, map);
        
        assertFalse(road0.needsCourier());
        assertTrue(map.getAllWorkers().size() == 2);
        assertEquals(road0.getCourier(), w);
    }

    @Test
    public void testThatCourierIsNotDispatchedToNewRoadWithNoConnection() throws Exception {

        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(13, 7);
        Building building1 = map.placeBuilding(new Woodcutter(), point1);
        Point point2 = new Point(10, 4);
        Flag flag0 = map.placeFlag(point2);

        Point point3 = new Point(12, 4);
        Point point4 = new Point(13, 5);
        Point point5 = new Point(14, 6);
        Road road0 = map.placeRoad(point2, point3, point4, point5);

        assertTrue(road0.needsCourier());
        assertTrue(map.getAllWorkers().size() == 1);
        
        /* Step time to let the headquarter send new workers */
        map.stepTime();
        
        assertTrue(road0.needsCourier());
        assertTrue(map.getAllWorkers().size() == 1);
    }

    @Test
    public void testOnlyTwoCouriersAreAssignedToTwoRoads() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(14, 10);
        Building building1 = map.placeBuilding(new Woodcutter(), point1);
        Point point2 = new Point(13, 5);
        Flag flag0 = map.placeFlag(point2);

        Utils.fastForward(100, map);
        
        Point point3 = new Point(6, 4);
        Point point4 = new Point(8, 4);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(12, 4);
        Road road0 = map.placeRoad(point3, point4, point5, point6, point2);
        
        assertTrue(map.getAllWorkers().size() == 1);
        assertTrue(road0.needsCourier());

        map.stepTime();
        
        assertTrue(map.getAllWorkers().size() == 2);
        assertFalse(road0.needsCourier());
        
        Point point7 = new Point(14, 6);
        Point point8 = new Point(15, 7);
        Point point9 = new Point(16, 8);
        Point point10 = new Point(15, 9);
        Road road1 = map.placeRoad(point2, point7, point8, point9, point10);

        assertTrue(road1.needsCourier());
        assertTrue(map.getAllWorkers().size() == 2);
        
        Utils.fastForward(10, map);
        
        assertTrue(map.getAllWorkers().size() == 3);
        assertFalse(road0.needsCourier());
        
        Utils.fastForward(10, map);

        Utils.fastForward(10, map);

        Utils.fastForward(10, map);


        assertTrue(map.getAllWorkers().size() == 3);
    }

    @Test
    public void testFindShortestWay() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(5, 9);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        List<Point> path = map.findAutoSelectedRoad(new Point(2, 2), new Point(11, 11), null);
        
        assertTrue(path.size() == 10);
    
        path = map.findAutoSelectedRoad(new Point(11, 11), new Point(2, 2), null);
        
        assertTrue(path.size() == 10);
        
        path = map.findAutoSelectedRoad(new Point(3, 3), new Point(11, 3), null);
        
        assertTrue(path.size() == 5);
    
        path = map.findAutoSelectedRoad(new Point(11, 3), new Point(3, 3), null);
        
        assertTrue(path.size() == 5);
    }

    @Test(expected = Exception.class)
    public void testRoadCannotGoThroughSmallBuilding() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);
        Point point2 = new Point(8, 6);
        Flag flag0 = map.placeFlag(point2);

        Point point3 = new Point(6, 4);
        Point point4 = new Point(5, 3);
        Point point5 = new Point(4, 4);
        Point point6 = new Point(6, 6);
        Road road0 = map.placeRoad(point3, point4, point5, point0, point6, point2);
    }

    @Test
    public void testRoadIsCreatedBetweenHouseAndFlag() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        Point point3 = new Point(6, 4);

        assertNotNull(map.getRoad(point0, point3));
    }

    @Test
    public void testRoadBetweenHouseAndFlagNeedsNoCourier() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        Point point3 = new Point(6, 4);

        assertFalse(map.getRoad(point0, point3).needsCourier());
    }

    @Test(expected = Exception.class)
    public void testCanNotCreateHorizontalRoadWithoutSpaceForCourier() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Point point1 = new Point(3, 3);
        Point point2 = new Point(5, 3);
        
        map.placeFlag(point1);
        map.placeFlag(point2);
        
        map.placeRoad(point1, point2);
    }

    @Test
    public void testSplitHorisontalRoadInBeginningWithTooShortRemainingRoads() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        map.placeFlag(new Flag(new Point(9, 5)));
        map.placeFlag(new Flag(new Point(17, 5)));
        
        Point start = new Point(9, 5);
        Point end = new Point(17, 5);
        Point m1 = new Point(11, 5);
        Point m2 = new Point(13, 5);
        Point m3 = new Point(15, 5);
        map.placeRoad(start, m1, m2, m3, end);
        
        assertTrue(map.getRoads().size() == 2);
        
        try {
            map.placeFlag(new Flag(m1));
            assertFalse(true);
        } catch (Exception e) {}

        assertTrue(map.getRoads().size() == 2);
        assertNotNull(map.getRoad(start, end));
    }

    @Test
    public void testSplitHorisontalRoadInEndWithTooShortRemainingRoads() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        map.placeFlag(new Flag(new Point(9, 5)));
        map.placeFlag(new Flag(new Point(17, 5)));
        
        Point start = new Point(9, 5);
        Point end = new Point(17, 5);
        Point m1 = new Point(11, 5);
        Point m2 = new Point(13, 5);
        Point m3 = new Point(15, 5);
        map.placeRoad(start, m1, m2, m3, end);
        
        assertTrue(map.getRoads().size() == 2);
        
        try {
            map.placeFlag(new Flag(m3));
            assertFalse(true);
        } catch (Exception e) {}

        assertTrue(map.getRoads().size() == 2);
        assertNotNull(map.getRoad(start, end));
    }

    @Test
    public void testCourierGoesBackToStorageWhenRoadIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing road */
        Point point26 = new Point(8, 8);
        Flag flag0 = map.placeFlag(point26);
        Road road0 = map.placeAutoSelectedRoad(headquarter0.getFlag(), flag0);

        /* Occupy the road */
        Utils.occupyRoad(new Courier(map), road0, map);
        
        /* Remove the road */
        Worker ww = road0.getCourier();

        assertTrue(road0.getWayPoints().contains(ww.getPosition()));

        map.removeRoad(road0);

        /* Verify that the worker goes back to the headquarter */
        assertEquals(ww.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());
    }

    @Test
    public void testCourierGoesBackToStorageOnRoadsIfPossibleWhenRoadIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Place road */
        Point point0 = new Point(8, 6);
        Flag flag0 = map.placeFlag(point0);
        
        Point point26 = new Point(8, 8);
        Flag flag1 = map.placeFlag(point26);

        Road road0 = map.placeAutoSelectedRoad(headquarter0.getFlag(), flag0);
        Road road1 = map.placeAutoSelectedRoad(flag0, flag1);

        /* Occupy the road */
        Utils.occupyRoad(new Courier(map), road1, map);
        
        /* Remove the road */
        Worker ww = road1.getCourier();

        assertTrue(road1.getWayPoints().contains(ww.getPosition()));

        map.removeRoad(road1);

        /* Verify that the worker leaves goes back to the headquarter */
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        /* Verify that the worker plans to use the roads */
        for (Point p : ww.getPlannedPath()) {
            assertTrue(map.isRoadAtPoint(p));
        }
    }
}
