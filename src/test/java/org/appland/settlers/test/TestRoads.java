/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import static java.awt.Color.BLUE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidRouteException;
import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.COIN;
import org.appland.settlers.model.Player;
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);

        assertNull(map.getRoad(new Point(1, 1), new Point(2, 2)));
    }

    @Test
    public void testUnreachableRoute() throws InvalidEndPointException, InvalidRouteException, Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(3, 3);
        map.placeFlag(player0, point1);

        Point point2 = new Point(7, 3);
        map.placeFlag(player0, point2);

        /* Place a road */
        Point point3 = new Point(5, 3);
        Road r = map.placeRoad(player0, point1, point3, point2);

        /* Verify that it's not possible to reach a point not on the road */
        Point point4 = new Point(9, 3);
        assertNull(map.findWayWithExistingRoads(point1, point4));
    }

    @Test
    public void testFindRouteWithSingleRoad() throws InvalidEndPointException, InvalidRouteException, Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(3, 3);
        map.placeFlag(player0, point1);

        Point point2 = new Point(5, 5);
        map.placeFlag(player0, point2);

        /* Place a road */
        map.placeAutoSelectedRoad(player0, point1, point2);

        /* Verify that it's possible to find a way between the end points */
        List<Point> way = map.findWayWithExistingRoads(point1, point2);

        assertEquals(way.size(), 3);
        assertTrue(way.get(0).equals(point1));
        assertTrue(way.get(2).equals(point2));
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

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        Point point0 = new Point(17, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        Point[] points = new Point[]{
            new Point(3, 3), // F
            new Point(7, 3), // F1
            new Point(11, 3), // F2
            new Point(15, 3), // F3
            new Point(19, 3), // F4
            new Point(6, 10), // F5
            new Point(10, 10), // F6
            new Point(6, 16), // F7
            new Point(10, 16), // F8
            new Point(14, 6), // F9
            new Point(23, 3)};  // F10

        int i;
        for (i = 0; i < points.length; i++) {
            map.placeFlag(player0, points[i]);
        }

        Point target = new Point(18, 6);

        map.placeFlag(player0, target);

        map.placeRoad(player0, points[0], new Point(5, 3), points[1]);
        map.placeRoad(player0, points[1], points[1].right(), points[2]);

        map.placeRoad(player0, points[2], points[2].right(), points[3]);

        map.placeRoad(player0, points[3], points[3].right(), points[4]);
        map.placeRoad(player0, points[4], points[4].right(), points[10]);
        map.placeRoad(player0, points[2], points[2].upRight(), points[9].downLeft(), points[9]);
        map.placeRoad(player0, points[9], points[9].right(), target);
        map.placeAutoSelectedRoad(player0, points[1], points[5]);
        map.placeRoad(player0, points[5], points[5].right(), points[6]);
        map.placeAutoSelectedRoad(player0, points[1], points[7]);
        map.placeRoad(player0, points[7], points[7].right(), points[8]);

        /* Test route with List<Point> */
        List<Point> route = map.findWayWithExistingRoads(points[0], target);

        assertNotNull(route);
        assertTrue(!route.isEmpty());

        assertTrue(route.size() < 11);
        assertEquals(route.get(0), points[0]);
        assertEquals(route.get(route.size() - 1), target);

        Utils.assertNoStepDirectlyUpwards(route);

        route = map.findWayWithExistingRoads(target, points[0]);

        assertTrue(route.size() < 11);
        assertEquals(route.get(0), target);
        assertEquals(route.get(route.size() - 1), points[0]);

        Utils.assertNoStepDirectlyUpwards(route);

        route = map.findWayWithExistingRoads(points[1], points[2]);

        assertEquals(route.size(), 3);
        assertEquals(route.get(0), points[1]);
        assertEquals(route.get(1), points[1].right());
        assertEquals(route.get(2), points[2]);
    }

    @Test
    public void testNeedsCourier() throws InvalidEndPointException, InvalidRouteException, Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(3, 3);
        Flag flag0 = map.placeFlag(player0, point1);

        Point point2 = new Point(7, 3);
        map.placeFlag(player0, point2);

        /* Place a new road */
        Road r = map.placeAutoSelectedRoad(player0, point1, point2);

        /* Verify that the road needs a courier */
        assertTrue(r.needsCourier());

        /* Assign a courier to the road */
        Utils.occupyRoad(r, map);

        /* Verify that the road doesn't need a courier */
        assertFalse(r.needsCourier());
    }

    @Test(expected = Exception.class)
    public void testAssignWorkerToRoadNotOnMap() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(1, 1);
        Point point2 = new Point(4, 2);

        Flag flag0 = map.placeFlag(player0, point1);
        map.placeFlag(player0, point2);

        Courier c = new Courier(player0, map);

        Road r = map.placeRoad(player0, point1, point2);
        map.placeWorker(c, flag0);
        c.assignToRoad(r);
    }

    @Test(expected = Exception.class)
    public void testAssignTwoWorkersToRoad() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(1, 1);
        Point point2 = new Point(4, 2);

        Flag flag0 = map.placeFlag(player0, point1);
        map.placeFlag(player0, point2);

        Courier c = new Courier(player0, map);
        Courier c2 = new Courier(player0, map);

        Road r = map.placeRoad(player0, point1, point2);

        map.placeWorker(c, flag0);
        map.placeWorker(c2, flag0);
        c.assignToRoad(r);

        c2.assignToRoad(r);
    }

    @Test(expected = Exception.class)
    public void testRoadCanNotShareSegment() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        Flag commonStart = new Flag(new Point(1, 1));
        Flag end1 = new Flag(new Point(1, 3));
        Flag end2 = new Flag(new Point(3, 1));
        Point middlePoint = new Point(2, 2);

        List<Point> wayPoints1 = new ArrayList<>();
        List<Point> wayPoints2 = new ArrayList<>();
        wayPoints1.add(commonStart.getPosition());
        wayPoints1.add(middlePoint);
        wayPoints1.add(end1.getPosition());

        wayPoints2.add(commonStart.getPosition());
        wayPoints2.add(middlePoint);
        wayPoints2.add(end2.getPosition());

        map.placeRoad(player0, wayPoints1);
        map.placeRoad(player0, wayPoints2);
    }

    @Test
    public void testWayPointsAreCorrectInSingleSegmentRoad() {
        //TODO: IMPLEMENT!
    }

    @Test(expected = Exception.class)
    public void testRoadsCanNotCross() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        Point start1 = new Point(1, 1);
        Point end1 = new Point(3, 3);
        Point start2 = new Point(1, 3);
        Point end2 = new Point(3, 1);
        Point middlePoint = new Point(2, 2);

        List<Point> wayPoints1 = new ArrayList<>();
        List<Point> wayPoints2 = new ArrayList<>();
        wayPoints1.add(start1);
        wayPoints1.add(middlePoint);
        wayPoints1.add(end1);

        wayPoints2.add(start2);
        wayPoints2.add(middlePoint);
        wayPoints2.add(end2);

        map.placeFlag(player0, start1);
        map.placeFlag(player0, start2);
        map.placeFlag(player0, end1);
        map.placeFlag(player0, end2);

        map.placeRoad(player0, wayPoints1);
        map.placeRoad(player0, wayPoints2);
    }

    @Test
    public void testWayPointsEqualsChosenRoad() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point start = new Point(3, 3);
        map.placeFlag(player0, start);

        Point end = new Point(5, 5);
        map.placeFlag(player0, end);

        /* Place road */
        Point middlePoint = new Point(4, 4);

        List<Point> wayPoints = new ArrayList<>();
        wayPoints.add(start);
        wayPoints.add(middlePoint);
        wayPoints.add(end);

        map.placeRoad(player0, wayPoints);

        /* Verify that the way points are set correctly in the road */
        Road r = map.getRoad(start, end);

        wayPoints = r.getWayPoints();

        assertEquals(wayPoints.size(), 3);
        assertTrue(wayPoints.get(0).equals(start));
        assertTrue(wayPoints.get(1).equals(middlePoint));
        assertTrue(wayPoints.get(2).equals(end));
    }

    @Test(expected = Exception.class)
    public void testLargerStepThanOneIsNotOk() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        Point start = new Point(1, 1);
        Point end = new Point(4, 4);
        Point middlePoint = new Point(2, 2);

        List<Point> middlePoints = new ArrayList<>();
        middlePoints.add(start);
        middlePoints.add(middlePoint);
        middlePoints.add(end);

        map.placeFlag(player0, start);
        map.placeFlag(player0, end);

        map.placeRoad(player0, middlePoints);
    }

    @Test
    public void testPossibleDirectConnectionsFromFlag() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Verify that the possible direct connections from the flag are correct */
        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point1);

        assertEquals(points.size(), 6);
        assertTrue(points.contains(point1.upLeft()));
        assertTrue(points.contains(point1.downLeft()));
        assertTrue(points.contains(point1.upRight()));
        assertTrue(points.contains(point1.downRight()));

        assertFalse(points.contains(point1.up()));
        assertFalse(points.contains(point1.down()));
        assertTrue(points.contains(point1.left()));
        assertTrue(points.contains(point1.right()));
    }

    @Test
    public void testPossibleDirectConnectionsInCorners() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 10, 10);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the possible connections are correct in the corners */
        Point downRight = new Point(7, 3);
        Point downLeft = new Point(3, 3);
        Point upRight = new Point(7, 7);
        Point upLeft = new Point(3, 7);

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, downRight);

        assertEquals(points.size(), 2);
        assertTrue(points.contains(downRight.left()));
        assertTrue(points.contains(downRight.upLeft()));
        assertFalse(points.contains(downRight.up()));
        assertFalse(points.contains(downRight.upRight()));
        assertFalse(points.contains(downRight.right()));
        assertFalse(points.contains(downRight.downRight()));
        assertFalse(points.contains(downRight.down()));
        assertFalse(points.contains(downRight.downLeft()));

        points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, downLeft);

        assertEquals(points.size(), 2);
        assertTrue(points.contains(downLeft.right()));
        assertTrue(points.contains(downLeft.upRight()));
        assertFalse(points.contains(downLeft.up()));
        assertFalse(points.contains(downLeft.upLeft()));
        assertFalse(points.contains(downLeft.left()));
        assertFalse(points.contains(downLeft.downLeft()));
        assertFalse(points.contains(downLeft.down()));
        assertFalse(points.contains(downLeft.downRight()));

        points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, upRight);

        assertEquals(points.size(), 2);
        assertTrue(points.contains(upRight.left()));
        assertTrue(points.contains(upRight.downLeft()));
        assertFalse(points.contains(upRight.down()));
        assertFalse(points.contains(upRight.downRight()));
        assertFalse(points.contains(upRight.right()));
        assertFalse(points.contains(upRight.upRight()));
        assertFalse(points.contains(upRight.up()));
        assertFalse(points.contains(upRight.upLeft()));

        points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, upLeft);

        assertEquals(points.size(), 2);
        assertTrue(points.contains(upLeft.right()));
        assertTrue(points.contains(upLeft.downRight()));
        assertFalse(points.contains(upLeft.down()));
        assertFalse(points.contains(upLeft.downLeft()));
        assertFalse(points.contains(upLeft.left()));
        assertFalse(points.contains(upLeft.upLeft()));
        assertFalse(points.contains(upLeft.up()));
        assertFalse(points.contains(upLeft.upRight()));
    }

    @Test
    public void testPossibleDirectConnectionsOnSides() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(12, 8);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the possible connections on the side are correct */
        Point right = new Point(17, 5);
        Point left = new Point(3, 5);
        Point up = new Point(5, 17);
        Point down = new Point(5, 3);

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, right);
        
        assertEquals(points.size(), 3);

        assertFalse(points.contains(right.up()));
        assertTrue(points.contains(right.upLeft()));
        assertTrue(points.contains(right.left()));
        assertTrue(points.contains(right.downLeft()));
        assertFalse(points.contains(right.down()));
        assertFalse(points.contains(right.downRight()));
        assertFalse(points.contains(right.right()));
        assertFalse(points.contains(right.upRight()));

        points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, left);

        assertEquals(points.size(), 3);

        assertFalse(points.contains(left.up()));
        assertFalse(points.contains(left.upLeft()));
        assertFalse(points.contains(left.left()));
        assertFalse(points.contains(left.downLeft()));
        assertFalse(points.contains(left.down()));
        assertTrue(points.contains(left.downRight()));
        assertTrue(points.contains(left.right()));
        assertTrue(points.contains(left.upRight()));

        points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, up);

        assertEquals(points.size(), 4);

        assertFalse(points.contains(up.up()));
        assertFalse(points.contains(up.upLeft()));
        assertTrue(points.contains(up.left()));
        assertTrue(points.contains(up.downLeft()));
        assertFalse(points.contains(up.down()));
        assertTrue(points.contains(up.downRight()));
        assertTrue(points.contains(up.right()));
        assertFalse(points.contains(up.upRight()));

        points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, down);

        assertEquals(points.size(), 4);

        assertFalse(points.contains(down.up()));
        assertTrue(points.contains(down.upLeft()));
        assertTrue(points.contains(down.left()));
        assertFalse(points.contains(down.downLeft()));
        assertFalse(points.contains(down.down()));
        assertFalse(points.contains(down.downRight()));
        assertTrue(points.contains(down.right()));
        assertTrue(points.contains(down.upRight()));
    }

    @Test
    public void testNoPossibleConnectionUpOrDownWithSurroundingRoads() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 30);

        map.placeBuilding(new Headquarter(player0), new Point(16, 10));
        map.placeFlag(player0, new Point(16, 12));
        map.placeFlag(player0, new Point(20, 12));
        map.placeFlag(player0, new Point(12, 12));
        map.placeRoad(player0, Arrays.asList(new Point[]{new Point(20, 12), new Point(18, 12), new Point(17, 13), new Point(18, 14), new Point(17, 15), new Point(16, 16), new Point(15, 15), new Point(14, 14), new Point(15, 13), new Point(14, 12), new Point(12, 12)}));

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, new Point(16, 12));

        assertFalse(points.contains(new Point(16, 14)));

        map.placeFlag(player0, new Point(21, 25));
        map.placeFlag(player0, new Point(25, 25));
        map.placeFlag(player0, new Point(17, 25));
        map.placeRoad(player0, Arrays.asList(new Point[]{new Point(25, 25), new Point(23, 25), new Point(22, 24), new Point(23, 23), new Point(22, 22), new Point(21, 21), new Point(20, 22), new Point(19, 23), new Point(20, 24), new Point(19, 25), new Point(17, 25)}));

        assertFalse(points.contains(new Point(21, 23)));
    }

    @Test
    public void testNoPossibleConnectionUnderBuilding() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point2 = new Point(12, 8);
        Building building1 = map.placeBuilding(new Woodcutter(player0), point2);
        Point point3 = new Point(10, 8);
        Flag flag0 = map.placeFlag(player0, point3);

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point3);

        assertFalse(points.contains(point2));
    }

    @Test
    public void testNoPossibleConnectionUnderStone() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point2 = new Point(12, 8);
        Stone stone0 = map.placeStone(point2);
        Point point3 = new Point(10, 8);
        Flag flag0 = map.placeFlag(player0, point3);

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point3);

        assertFalse(points.contains(point2));
    }

    @Test
    public void testNoPossibleConnectionUnderTree() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point2 = new Point(12, 8);
        Tree tree0 = map.placeTree(point2);
        Point point3 = new Point(10, 8);
        Flag flag0 = map.placeFlag(player0, point3);

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point3);

        assertFalse(points.contains(point2));
    }

    @Test
    public void testNoPossibleConnectionsAtAndOutsideBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point3 = new Point(50, 68);
        Flag flag0 = map.placeFlag(player0, point3);

        /* Verify that there are no possible connections at and outside the border */
        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point3);

        /* Points on border */
        assertFalse(points.contains(point3.upLeft()));
        assertFalse(points.contains(point3.upRight()));

        /* Points outside border */
        assertFalse(points.contains(point3.up().upLeft()));
        assertFalse(points.contains(point3.up().upRight()));
    }

    @Test
    public void testNoPossibleConnectionThroughNewCrop() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place crop */
        Point point2 = new Point(12, 8);
        Crop crop0 = map.placeCrop(point2);

        /* Place flag */
        Point point3 = new Point(10, 8);
        Flag flag0 = map.placeFlag(player0, point3);

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point3);

        assertFalse(points.contains(point2));
    }

    @Test
    public void testPlaceRoadWithVarargs() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(3, 3);
        map.placeFlag(player0, point1);

        Point point2 = new Point(7, 3);
        map.placeFlag(player0, point2);

        /* Verify that it's possible to place a road with var args */
        Road r = map.placeRoad(player0, point1, new Point(5, 3), point2);

        assertNotNull(r);
    }

    @Test(expected = Exception.class)
    public void testNotPossibleToPlaceRoadThroughNewCrop() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place crop */
        Point point2 = new Point(12, 8);
        Crop crop0 = map.placeCrop(point2);

        /* Place flag */
        Point point3 = new Point(10, 8);
        Flag flag0 = map.placeFlag(player0, point3);

        /* Place flag */
        Point point4 = new Point(14, 8);
        Flag flag1 = map.placeFlag(player0, point4);

        /* Verify that a road can't be placed through the new crop */
        map.placeRoad(player0, point3, point2, point4);
    }

    @Test
    public void testConnectNewRoadToFlagInExistingRoad() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        map.placeBuilding(new Headquarter(player0), new Point(5, 5));
        map.placeFlag(player0, new Point(12, 4));
        map.placeFlag(player0, new Point(14, 6));
        map.placeRoad(player0, new Point(12, 4), new Point(13, 5), new Point(14, 6));
        map.placeFlag(player0, new Point(16, 8));
        map.placeRoad(player0, new Point(14, 6), new Point(15, 7), new Point(16, 8));
        map.placeFlag(player0, new Point(16, 4));
        map.placeRoad(player0, new Point(16, 4), new Point(15, 5), new Point(14, 6));
    }

    @Test
    public void testPlaceFlagInExistingRoadSplitsTheRoad() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        map.placeFlag(player0, new Point(9, 5));
        map.placeFlag(player0, new Point(13, 9));

        Point start = new Point(9, 5);
        Point end = new Point(13, 9);
        Point m1 = new Point(10, 6);
        Point m2 = new Point(11, 7);
        Point m3 = new Point(12, 8);
        map.placeRoad(player0, start, m1, m2, m3, end);

        assertEquals(map.getRoads().size(), 2);

        map.placeFlag(player0, m2);

        assertEquals(map.getRoads().size(), 3);
        List<Road> roads = new ArrayList<>();
        roads.addAll(map.getRoads());

        roads.remove(map.getRoad(point0, point0.downRight()));

        Road r1 = roads.get(0);
        Road r2 = roads.get(1);

        assertTrue((r1.getStart().equals(start) && r1.getEnd().equals(m2))
                || (r1.getStart().equals(m2) && r1.getEnd().equals(start))
                || (r2.getStart().equals(start) && r2.getEnd().equals(m2))
                || (r2.getStart().equals(m2) && r2.getEnd().equals(start)));

        assertTrue((r1.getStart().equals(m2) && r1.getEnd().equals(end))
                || (r1.getStart().equals(end) && r1.getEnd().equals(m2))
                || (r2.getStart().equals(m2) && r2.getEnd().equals(end))
                || (r2.getStart().equals(end) && r2.getEnd().equals(m2)));
    }

    @Test
    public void testIdleCourierIsAssignedWhenRoadIsSplit() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Building hq = map.placeBuilding(new Headquarter(player0), new Point(5, 5));
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(player0, endPoint);
        Road road = map.placeRoad(player0, hq.getFlag().getPosition(),
                middlePoint1,
                middlePoint2,
                middlePoint3,
                endPoint);

        /* Place original courier */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, endFlag);
        courier.assignToRoad(road);

        Utils.fastForwardUntilWorkersReachTarget(map, courier);

        assertEquals(courier.getAssignedRoad(), road);
        assertEquals(road.getCourier(), courier);
        assertTrue(courier.isIdle());
        assertTrue(courier.isAt(middlePoint2));

        /* Split road */
        map.placeFlag(player0, middlePoint2);

        assertTrue(courier.isWalkingToRoad());
        assertTrue(courier.getAssignedRoad().getStart().equals(middlePoint2)
                || courier.getAssignedRoad().getEnd().equals(middlePoint2));
    }

    @Test
    public void testCourierDeliveringCargoFinishesDeliveryAndIsAssignedWhenRoadIsSplit() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Building hq = map.placeBuilding(new Headquarter(player0), new Point(5, 5));
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(player0, endPoint);
        Road road = map.placeRoad(player0, hq.getFlag().getPosition(),
                middlePoint1,
                middlePoint2,
                middlePoint3,
                endPoint);

        /* Place original courier */
        Courier courier = new Courier(player0, map);
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
        map.placeFlag(player0, middlePoint2);

        assertFalse(courier.isWalkingToRoad());
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), middlePoint2);

        assertTrue((courier.getAssignedRoad().getStart().equals(middlePoint2) && courier.getAssignedRoad().getEnd().equals(endPoint))
                || (courier.getAssignedRoad().getEnd().equals(middlePoint2) && courier.getAssignedRoad().getStart().equals(endPoint)));
    }

    @Test
    public void testCourierFarFromToBuildingDeliveringCargoFinishesDeliveryAndBecomesIdleWhenRoadIsSplit() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Storage hq = new Headquarter(player0);
        map.placeBuilding(hq, new Point(5, 5));
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(player0, endPoint);
        Road road = map.placeRoad(player0, hq.getFlag().getPosition(),
                middlePoint1,
                middlePoint2,
                middlePoint3,
                endPoint);

        /* Place original courier */
        Courier courier = new Courier(player0, map);
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
        Flag middleFlag = map.placeFlag(player0, middlePoint2);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Storage hq = new Headquarter(player0);
        map.placeBuilding(hq, new Point(5, 5));
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(player0, endPoint);
        Road road = map.placeRoad(player0, hq.getFlag().getPosition(),
                middlePoint1,
                middlePoint2,
                middlePoint3,
                endPoint);

        /* Place original courier */
        Courier courier = new Courier(player0, map);
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
        Flag middleFlag = map.placeFlag(player0, middlePoint2);

        assertFalse(courier.isWalkingToRoad());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier is sitll targeting the hq */
        assertEquals(courier.getTarget(), hq.getPosition());

        /* Let the courier leave the cargo */
        assertEquals(hq.getAmount(BEER), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, hq.getPosition());

        assertNull(courier.getCargo());
        assertEquals(hq.getAmount(BEER), 1);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Building hq = map.placeBuilding(new Headquarter(player0), new Point(5, 5));
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(player0, endPoint);
        Road road = map.placeRoad(player0, hq.getFlag().getPosition(),
                middlePoint1,
                middlePoint2,
                middlePoint3,
                endPoint);

        /* Place original courier */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, endFlag);
        courier.assignToRoad(road);

        map.stepTime();

        assertEquals(courier.getAssignedRoad(), road);
        assertEquals(road.getCourier(), courier);
        assertTrue(courier.isWalkingToRoad());

        /* Split road */
        Flag middleFlag = map.placeFlag(player0, middlePoint2);

        assertTrue(courier.isWalkingToRoad());
        assertTrue(courier.getTarget().equals(middlePoint1)
                || courier.getTarget().equals(middlePoint3));

        Utils.fastForwardUntilWorkersReachTarget(map, courier);

        assertTrue(courier.isIdle());
    }

    @Test
    public void testNewCourierIsDispatchedWhenRoadIsSplit() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Building hq = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(player0, endPoint);
        Road road = map.placeRoad(player0, hq.getFlag().getPosition(),
                middlePoint1,
                middlePoint2,
                middlePoint3,
                endPoint);

        /* Place original courier */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, endFlag);
        courier.assignToRoad(road);

        Utils.fastForwardUntilWorkersReachTarget(map, courier);

        assertEquals(courier.getAssignedRoad(), road);
        assertEquals(road.getCourier(), courier);
        assertTrue(courier.isIdle());
        assertTrue(courier.isAt(middlePoint2));

        /* Split road */
        assertEquals(map.getRoads().size(), 2);

        map.placeFlag(player0, middlePoint2);

        assertEquals(map.getRoads().size(), 3);
        assertEquals(map.getWorkers().size(), 2);

        /* Step time to let the headquarter assign a courier to the new road */
        Road r = map.getRoad(middlePoint2, endPoint);

        if (!r.needsCourier()) {
            r = map.getRoad(hq.getFlag().getPosition(), middlePoint2);
        }

        assertTrue(r.needsCourier());

        assertEquals(map.getClosestStorage(r.getStart()), hq);

        map.stepTime();

        assertEquals(map.getWorkers().size(), 3);

        Worker w2 = null;

        for (Worker w : map.getWorkers()) {
            if (!(w instanceof Courier)) {
                continue;
            }

            if (w.equals(courier)) {
                continue;
            }

            w2 = w;

            break;
        }

        assertTrue(w2 instanceof Courier);

        Courier secondCourier = (Courier) w2;

        assertNotNull(secondCourier);
        assertNotNull(secondCourier.getAssignedRoad());
        assertFalse(secondCourier.getAssignedRoad().equals(road));
    }

    @Test
    public void testCourierDeliversCorrectlyToBuildingAfterItsRoadIsSplit() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Storage hq = new Headquarter(player0);
        map.placeBuilding(hq, new Point(5, 5));
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(player0, endPoint);
        Road road = map.placeRoad(player0, hq.getFlag().getPosition(),
                middlePoint1,
                middlePoint2,
                middlePoint3,
                endPoint);

        /* Place original courier */
        Courier courier = new Courier(player0, map);
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
        Flag middleFlag = map.placeFlag(player0, middlePoint2);

        assertEquals(hq.getAmount(BEER), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, hq.getPosition());

        assertNull(courier.getCargo());
        assertEquals(hq.getAmount(BEER), 1);
    }

    @Test
    public void testRoadCanNotOverlapExistingFlag() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        map.placeBuilding(new Headquarter(player0), new Point(5, 5));
        map.placeFlag(player0, new Point(10, 4));
        map.placeFlag(player0, new Point(13, 7));
        map.placeRoad(player0, new Point(10, 4), new Point(11, 5), new Point(12, 6), new Point(13, 7));
        map.placeFlag(player0, new Point(14, 4));
        map.placeFlag(player0, new Point(9, 7));

        thrown.expect(Exception.class);
        map.placeRoad(player0, new Point(14, 4), new Point(12, 4), new Point(10, 4), new Point(9, 5), new Point(8, 6), new Point(9, 7));
    }

    @Test
    public void testOnlyOneCourierIsAssignedToNewRoad() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);
        Point point1 = new Point(12, 6);
        Building building1 = map.placeBuilding(new Woodcutter(player0), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(10, 4);
        Point point5 = new Point(12, 4);
        Point point6 = new Point(13, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4, point5, point6);

        assertTrue(road0.needsCourier());
        assertEquals(map.getWorkers().size(), 1);

        List<Worker> workersBefore = new LinkedList<>();
        workersBefore.addAll(map.getWorkers());

        /* Step time to let the headquarter send new workers */
        map.stepTime();

        /* Let the new courier reach its road */
        assertEquals(map.getWorkers().size(), 2);

        /* Find the added courier */
        List<Worker> workersAfter = new LinkedList<>();
        workersAfter.addAll(map.getWorkers());
        workersAfter.removeAll(workersBefore);

        Worker w = workersAfter.get(0);

        Utils.fastForwardUntilWorkersReachTarget(map, w);

        assertFalse(road0.needsCourier());
        assertEquals(road0.getCourier(), w);

        Utils.fastForward(100, map);

        assertFalse(road0.needsCourier());
        assertEquals(map.getWorkers().size(), 2);
        assertEquals(road0.getCourier(), w);
    }

    @Test
    public void testThatCourierIsNotDispatchedToNewRoadWithNoConnection() throws Exception {

        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);
        Point point1 = new Point(13, 7);
        Building building1 = map.placeBuilding(new Woodcutter(player0), point1);
        Point point2 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point2);

        Point point3 = new Point(12, 4);
        Point point4 = new Point(13, 5);
        Point point5 = new Point(14, 6);
        Road road0 = map.placeRoad(player0, point2, point3, point4, point5);

        assertTrue(road0.needsCourier());
        assertEquals(map.getWorkers().size(), 1);

        /* Step time to let the headquarter send new workers */
        map.stepTime();

        assertTrue(road0.needsCourier());
        assertEquals(map.getWorkers().size(), 1);
    }

    @Test
    public void testOnlyTwoCouriersAreAssignedToTwoRoads() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);
        Point point1 = new Point(14, 10);
        Building building1 = map.placeBuilding(new Woodcutter(player0), point1);
        Point point2 = new Point(13, 5);
        Flag flag0 = map.placeFlag(player0, point2);

        Utils.fastForward(100, map);

        Point point3 = new Point(6, 4);
        Point point4 = new Point(8, 4);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(12, 4);
        Road road0 = map.placeRoad(player0, point3, point4, point5, point6, point2);

        assertEquals(map.getWorkers().size(), 1);
        assertTrue(road0.needsCourier());

        map.stepTime();

        assertEquals(map.getWorkers().size(), 2);
        assertFalse(road0.needsCourier());

        Point point7 = new Point(14, 6);
        Point point8 = new Point(15, 7);
        Point point9 = new Point(16, 8);
        Point point10 = new Point(15, 9);
        Road road1 = map.placeRoad(player0, point2, point7, point8, point9, point10);

        assertTrue(road1.needsCourier());
        assertEquals(map.getWorkers().size(), 2);

        Utils.fastForward(10, map);

        assertEquals(map.getWorkers().size(), 3);
        assertFalse(road0.needsCourier());

        Utils.fastForward(10, map);

        Utils.fastForward(10, map);

        Utils.fastForward(10, map);

        assertEquals(map.getWorkers().size(), 3);
    }

    @Test
    public void testFindShortestWay() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point hqPoint = new Point(6, 10);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        List<Point> path = map.findAutoSelectedRoad(player0, new Point(3, 3), new Point(12, 12), null);

        assertEquals(path.size(), 10);

        path = map.findAutoSelectedRoad(player0, new Point(12, 12), new Point(4, 4), null);

        assertEquals(path.size(), 9);

        path = map.findAutoSelectedRoad(player0, new Point(4, 4), new Point(12, 4), null);

        assertEquals(path.size(), 5);

        path = map.findAutoSelectedRoad(player0, new Point(12, 4), new Point(4, 4), null);

        assertEquals(path.size(), 5);
    }

    @Test(expected = Exception.class)
    public void testRoadCannotGoThroughSmallBuilding() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);
        Point point2 = new Point(8, 6);
        Flag flag0 = map.placeFlag(player0, point2);

        Point point3 = new Point(6, 4);
        Point point4 = new Point(5, 3);
        Point point5 = new Point(4, 4);
        Point point6 = new Point(6, 6);
        Road road0 = map.placeRoad(player0, point3, point4, point5, point0, point6, point2);
    }

    @Test
    public void testRoadIsCreatedBetweenHouseAndFlag() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(6, 4);

        assertNotNull(map.getRoad(point0, point3));
    }

    @Test
    public void testRoadBetweenHouseAndFlagNeedsNoCourier() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point3 = new Point(6, 4);

        assertFalse(map.getRoad(point0, point3).needsCourier());
    }

    @Test(expected = Exception.class)
    public void testCanNotCreateHorizontalRoadWithoutSpaceForCourier() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(3, 3);
        Point point2 = new Point(5, 3);

        map.placeFlag(player0, point1);
        map.placeFlag(player0, point2);

        map.placeRoad(player0, point1, point2);
    }

    @Test
    public void testSplitHorisontalRoadWithTooShortRemainingRoads() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        map.placeFlag(player0, new Point(9, 5));
        map.placeFlag(player0, new Point(17, 5));

        Point start = new Point(9, 5);
        Point end = new Point(17, 5);
        Point m1 = new Point(11, 5);
        Point m2 = new Point(13, 5);
        Point m3 = new Point(15, 5);
        Road road0 = map.placeRoad(player0, start, m1, m2, m3, end);

        assertEquals(map.getRoads().size(), 2);

        List<Point> wayPointsBefore = new ArrayList<>(road0.getWayPoints());

        try {
            map.placeFlag(player0, m1);
            assertFalse(true);
        } catch (Exception e) {}

        assertTrue(map.getRoads().contains(road0));
        assertEquals(road0.getWayPoints().size(), 5);
        assertEquals(map.getRoads().size(), 2);
        assertEquals(wayPointsBefore, road0.getWayPoints());
        assertNotNull(map.getRoad(start, end));
    }

    @Test
    public void testSplitHorisontalRoadInEndWithTooShortRemainingRoads() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        map.placeFlag(player0, new Point(9, 5));
        map.placeFlag(player0, new Point(17, 5));

        Point start = new Point(9, 5);
        Point end = new Point(17, 5);
        Point m1 = new Point(11, 5);
        Point m2 = new Point(13, 5);
        Point m3 = new Point(15, 5);
        map.placeRoad(player0, start, m1, m2, m3, end);

        assertEquals(map.getRoads().size(), 2);

        try {
            map.placeFlag(player0, m3);
            assertFalse(true);
        } catch (Exception e) {
        }

        assertEquals(map.getRoads().size(), 2);
        assertNotNull(map.getRoad(start, end));
    }

    @Test
    public void testCourierGoesBackToStorageWhenRoadIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing road */
        Point point26 = new Point(8, 8);
        Flag flag0 = map.placeFlag(player0, point26);
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Occupy the road */
        Utils.occupyRoad(road0, map);

        /* Remove the road */
        Worker ww = road0.getCourier();

        assertTrue(road0.getWayPoints().contains(ww.getPosition()));

        map.removeRoad(road0);

        /* Verify that the worker goes back to the headquarter */
        assertEquals(ww.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());

        /* Verify that the worker is no longer on the map */
        assertFalse(map.getWorkers().contains(ww));
    }

    @Test
    public void testCourierGoesBackToStorageOnRoadsIfPossibleWhenRoadIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place road */
        Point point0 = new Point(8, 6);
        Flag flag0 = map.placeFlag(player0, point0);

        Point point26 = new Point(8, 8);
        Flag flag1 = map.placeFlag(player0, point26);

        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Occupy the road */
        Utils.occupyRoad(road1, map);

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

    @Test(expected = Exception.class)
    public void testRoadCannotBePlacedThroughExistingFlagThatIsAlsoEndpoint() throws Exception {
        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point38 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* 93 ticks from start */
        Utils.fastForward(92, map);

        /* Placing flag */
        Point point39 = new Point(10, 8);
        Flag flag0 = map.placeFlag(player0, point39);

        /* Placing road between (6, 4) and (10, 8) */
        Point point40 = new Point(6, 4);
        Point point41 = new Point(7, 5);
        Point point42 = new Point(8, 6);
        Point point43 = new Point(9, 7);
        Point point44 = new Point(11, 9);
        Point point45 = new Point(10, 10);
        Point point46 = new Point(9, 9);
        Road road0 = map.placeRoad(player0, point40, point41, point42, point43, point39, point44, point45, point46, point39);
    }

    @Test
    public void testRoadBecomesAgedWithDeliveryToBuilding() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point38 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place flag */
        Point point2 = new Point(5, 9);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place road between the headquarter and the flag */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Place a worker on the road */
        Courier courier = Utils.occupyRoad(road0, map);

        /* Deliver 99 cargos and verify that the road does not become a main road */
        for (int i = 0; i < 99; i++) {
            Cargo cargo = new Cargo(COIN, map);

            flag0.putCargo(cargo);

            cargo.setTarget(headquarter0);

            /* Wait for the courier to pick up the cargo */
            assertNull(courier.getCargo());

            Utils.fastForwardUntilWorkerCarriesCargo(map, courier, cargo);

            /* Wait for the courier to deliver the cargo */
            assertEquals(courier.getTarget(), headquarter0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

            assertNull(courier.getCargo());

            assertFalse(road0.isMainRoad());
        }

        /* Deliver one more cargo and verify that the road becomes a main road */
        Cargo cargo = new Cargo(COIN, map);

        flag0.putCargo(cargo);

        cargo.setTarget(headquarter0);

        /* Wait for the courier to pick up the cargo */
        assertNull(courier.getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, cargo);

        /* Wait for the courier to deliver the cargo */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        assertTrue(road0.isMainRoad());
    }

    @Test
    public void testRoadBecomesAgedWithDeliveryToFlag() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point38 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place flag */
        Point point2 = new Point(5, 9);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place flag */
        Point point3 = new Point(5, 13);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Place road between the headquarter and the first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Place road between the headquarter and the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Place workers on the roads */
        Courier courier0 = Utils.occupyRoad(road0, map);
        Courier courier1 = Utils.occupyRoad(road1, map);

        /* Deliver 99 cargos and verify that the road does not become a main road */
        for (int i = 0; i < 99; i++) {
            Cargo cargo = new Cargo(COIN, map);

            flag1.putCargo(cargo);

            cargo.setTarget(headquarter0);

            /* Wait for the courier to pick up the cargo */
            assertNull(courier1.getCargo());

            Utils.fastForwardUntilWorkerCarriesCargo(map, courier1, cargo);

            /* Wait for the courier to deliver the cargo */
            assertEquals(courier1.getTarget(), flag0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, courier1, flag0.getPosition());

            assertNull(courier1.getCargo());

            assertFalse(road1.isMainRoad());
        }

        /* Deliver one more cargo and verify that the road becomes a main road */
        Cargo cargo = new Cargo(COIN, map);

        flag1.putCargo(cargo);

        cargo.setTarget(headquarter0);

        /* Wait for the courier to pick up the cargo */
        assertNull(courier1.getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier1, cargo);

        /* Wait for the courier to deliver the cargo */
        assertEquals(courier1.getTarget(), flag0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier1, flag0.getPosition());

        assertTrue(road1.isMainRoad());
    }
    @Test
    public void testDifferentRoadsWithSameEndpointsAreNotEqual() throws Exception {

        /* Create player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Placing headquarter for player0 */
        Point point17 = new Point(8, 10);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point17);

        /* Place flags */
        Point point0 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point0);

        Point point1 = new Point(15, 11);
        Flag flag1 = map.placeFlag(player0, point1);

        /* Verify that roads with same endpoints but different waypoints are not equal */
        Point point2 = new Point(14, 10);
        Point point3 = new Point(13, 11);
        Road road1 = map.placeRoad(player0, point0, point2, point1);
        Road road2 = map.placeRoad(player0, point0, point3, point1);

        assertFalse(road1.equals(road2));
    }
}