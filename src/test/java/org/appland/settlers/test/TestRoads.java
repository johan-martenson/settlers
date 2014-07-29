/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameLogic;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidRouteException;
import static org.appland.settlers.model.Material.BEER;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
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
        GameMap map = new GameMap(10, 10);

        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(4, 2));

        map.placeFlag(f1);
        map.placeFlag(f2);

        Road r = map.placeAutoSelectedRoad(f1, f2);

        thrown.expect(InvalidRouteException.class);
        map.findWayWithExistingRoads(f1.getPosition(), new Point(3, 3));
    }

    @Test
    public void testFindRouteWithSingleRoad() throws InvalidEndPointException, InvalidRouteException, Exception {
        GameMap map = new GameMap(10, 10);

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

        GameMap map = new GameMap(20, 20);

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
        map.placeRoad(points[1], points[2]);
        
        map.placeRoad(points[2], points[3]);
        
        map.placeRoad(points[3], points[4]);
        map.placeRoad(points[4], points[10]);
        map.placeRoad(points[2], points[2].upRight(), points[9]);
        map.placeRoad(points[9], target);
        map.placeAutoSelectedRoad(points[1], points[5]);
        map.placeRoad(points[5], points[6]);
        map.placeAutoSelectedRoad(points[1], points[7]);
        map.placeRoad(points[7], points[8]);

        /* Test route with List<Point> */
        List<Point> route = map.findWayWithExistingRoads(points[0], target);

        assertNotNull(route);
        assertTrue(!route.isEmpty());

        assertTrue(route.size() < 8);
        assertEquals(route.get(0), points[0]);
        assertEquals(route.get(route.size() - 1), target);

        Utils.assertNoStepDirectlyUpwards(route);
        
        route = map.findWayWithExistingRoads(target, points[0]);

        assertTrue(route.size() < 8);
        assertEquals(route.get(0), target);
        assertEquals(route.get(route.size() - 1), points[0]);

        Utils.assertNoStepDirectlyUpwards(route);
        
        route = map.findWayWithExistingRoads(points[1], points[2]);

        assertTrue(route.size() == 2);
        assertEquals(route.get(0), points[1]);
        assertEquals(route.get(1), points[2]);
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

        Courier c = new Courier(map);

        map.placeWorker(c, f1);
        c.assignToRoad(r);
        
        assertFalse(r.needsCourier());
        
        Utils.fastForwardUntilWorkersReachTarget(map, c);

        assertFalse(r.needsCourier());
    }

    @Test(expected=Exception.class)
    public void testAssignWorkerToRoadNotOnMap() throws Exception {
        GameMap map = new GameMap(10, 10);

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
        GameMap map = new GameMap(10, 10);

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
        GameMap map = new GameMap(10, 10);

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
        GameMap map = new GameMap(10, 10);

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
        GameMap map = new GameMap(10, 10);

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

	Road r = map.getRoads().get(0);
        
        wayPoints = r.getWayPoints();

	assertTrue(wayPoints.size() == 3);
	assertTrue(wayPoints.get(0).equals(start.getPosition()));
	assertTrue(wayPoints.get(1).equals(middlePoint));
	assertTrue(wayPoints.get(2).equals(end.getPosition()));
    }

    @Test(expected = Exception.class)
    public void testLargerStepThanOneIsNotOk() throws Exception {
        GameMap map = new GameMap(10, 10);

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
        GameMap map = new GameMap(10, 10);

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

        map.placeBuilding(new Headquarter(), new Point(5, 5));
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
        Point point2 = new Point(12, 8);
        Tree tree0 = map.placeTree(point2);
        Point point3 = new Point(10, 8);
        Flag flag0 = map.placeFlag(point3);

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(point3);
    
        assertFalse(points.contains(point2));
    }
    
    @Test
    public void testPlaceRoadWithVarargs() throws Exception {
        GameMap map = new GameMap(10, 10);

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
        map.placeBuilding(new Headquarter(), new Point(5, 5));
        map.placeFlag(new Flag(new Point(9, 5)));
        map.placeFlag(new Flag(new Point(13, 9)));
        
        Point start = new Point(9, 5);
        Point end = new Point(13, 9);
        Point m1 = new Point(10, 6);
        Point m2 = new Point(11, 7);
        Point m3 = new Point(12, 8);
        map.placeRoad(start, m1, m2, m3, end);
        
        assertTrue(map.getRoads().size() == 1);
        
        map.placeFlag(new Flag(m2));

        assertTrue(map.getRoads().size() == 2);
        Road r1 = map.getRoads().get(0);
        Road r2 = map.getRoads().get(1);

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
        
        /* Make the courier pick up a cargo and start walking to deliver it */
        Cargo cargo = new Cargo(BEER);
        endFlag.putCargo(cargo);
        cargo.setTarget(hq, map);
        
        map.stepTime();
        
        assertEquals(courier.getTarget(), endPoint);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, endPoint);
        
        map.stepTime();
        
        assertTrue(courier.isTraveling());
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), hq.getFlag().getPosition());
        
        /* Split road */
        map.placeFlag(new Flag(middlePoint2));

        assertFalse(courier.isWalkingToRoad());
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), middlePoint2);

        assertTrue((courier.getAssignedRoad().getStart().equals(middlePoint2) && courier.getAssignedRoad().getEnd().equals(endPoint)) ||
                   (courier.getAssignedRoad().getEnd().equals(middlePoint2) && courier.getAssignedRoad().getStart().equals(endPoint)));
    }
    
    @Test
    public void testCourierDeliveringCargoFinishesDeliveryAndBecomesIdleWhenRoadIsSplit() throws Exception {
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
        
        /* Make the courier pick up a cargo and start walking to deliver it */
        Cargo cargo = new Cargo(BEER);
        endFlag.putCargo(cargo);
        cargo.setTarget(hq, map);
        
        map.stepTime();
        
        assertEquals(courier.getTarget(), endPoint);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, endPoint);
        
        map.stepTime();
        
        assertTrue(courier.isTraveling());
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), hq.getFlag().getPosition());
        
        /* Split road */
        Flag middleFlag = map.placeFlag(new Flag(middlePoint2));

        assertFalse(courier.isWalkingToRoad());
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), middlePoint2);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, middlePoint2);
        
        assertNull(courier.getCargo());
        assertTrue(middleFlag.getStackedCargo().contains(cargo));
        assertTrue(courier.isWalkingToIdlePoint());
        assertEquals(courier.getTarget(), middlePoint3);
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertTrue(courier.isIdle());
        assertEquals(courier.getPosition(), middlePoint3);
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
        map.placeFlag(new Flag(middlePoint2));

        assertTrue(map.getAllWorkers().size() == 1);
        
        GameLogic gameLogic = new GameLogic();
        gameLogic.gameLoop(map);

        assertTrue(map.getAllWorkers().size() == 2);
        
        Worker w2 = map.getAllWorkers().get(1);
        
        if (w2.equals(courier)) {
            w2 = map.getAllWorkers().get(0);
        }
        
        assertTrue(w2 instanceof Courier);
        
        Courier secondCourier = (Courier)w2;
        
        assertNotNull(secondCourier.getAssignedRoad());
        assertFalse(secondCourier.getAssignedRoad().equals(road));
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

        GameLogic gameLogic = new GameLogic();
        
        assertTrue(road0.needsCourier());
        assertTrue(map.getAllWorkers().isEmpty());
        
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        
        /* Let the new courier reach its road */
        assertTrue(map.getAllWorkers().size() == 1);
        
        Utils.fastForwardUntilWorkersReachTarget(map, map.getAllWorkers().get(0));
        
        assertFalse(road0.needsCourier());
        assertTrue(map.getAllWorkers().size() == 1);
        assertEquals(road0.getCourier(), map.getAllWorkers().get(0));
        
        Utils.fastForward(100, map);
        
        assertFalse(road0.needsCourier());
        assertTrue(map.getAllWorkers().size() == 1);
        assertEquals(road0.getCourier(), map.getAllWorkers().get(0));
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
        assertTrue(map.getAllWorkers().isEmpty());

        GameLogic gameLogic = new GameLogic();
        
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        
        assertTrue(road0.needsCourier());
        assertTrue(map.getAllWorkers().isEmpty());
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

        GameLogic gameLogic = new GameLogic();
        
        gameLogic.gameLoop(map);
        Utils.fastForward(100, map);
        
        Point point3 = new Point(6, 4);
        Point point4 = new Point(8, 4);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(12, 4);
        Road road0 = map.placeRoad(point3, point4, point5, point6, point2);
        
        assertTrue(map.getAllWorkers().isEmpty());
        assertTrue(road0.needsCourier());

        gameLogic.gameLoop(map);
        map.stepTime();
        
        assertTrue(map.getAllWorkers().size() == 1);
        assertFalse(road0.needsCourier());
        
        Point point7 = new Point(14, 6);
        Point point8 = new Point(15, 7);
        Point point9 = new Point(16, 8);
        Point point10 = new Point(15, 9);
        Road road1 = map.placeRoad(point2, point7, point8, point9, point10);

        assertTrue(road1.needsCourier());
        assertTrue(map.getAllWorkers().size() == 1);
        
        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);
        
        assertTrue(map.getAllWorkers().size() == 2);
        assertFalse(road0.needsCourier());
        
        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);

        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);

        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);


        assertTrue(map.getAllWorkers().size() == 2);
    }

    @Test
    public void testFindShortestWay() throws Exception {
        GameMap map = new GameMap(20, 20);
        
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
}
