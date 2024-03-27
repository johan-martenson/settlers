/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.buildings.Well;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.WellWorker;
import org.appland.settlers.model.actors.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static org.appland.settlers.model.Material.*;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestRoads {

    @Test
    public void testGetNotExistingRoad() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);

        /* Verify that trying to get a non-existing road returns null */
        Point point1 = new Point(2, 2);
        Point point2 = new Point(6, 2);

        assertNull(map.getRoad(point1, point2));
    }

    @Test
    public void testUnreachableRoute() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
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
        Road road = map.placeRoad(player0, point1, point3, point2);

        /* Verify that it's not possible to reach a point not on the road */
        Point point4 = new Point(9, 3);
        assertNull(map.findWayWithExistingRoads(point1, point4));
    }

    @Test
    public void testFindRouteWithSingleRoad() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
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
        assertEquals(way.get(0), point1);
        assertEquals(way.get(2), point2);
    }

    @Test
    public void testFindRoute() throws Exception {
        /*
         * x: 3        x: 11                x: 23                  x: ?
         * F--x--F1--x--F2--x--F3--x--F4--x--F10----------------|  y: 3
         *       |      |
         *       |      |                                       |
         *       |      |---F9--Target building                 |  y: 6
         *       |                                              |
         *       |                                              |
         *       |---F5---F6------------------------------------   y: 10
         *       |
         *       |---F7---F8                                       y: 16
         */

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(18, 18);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place fortresses to expand the area to build roads on */
        Point pointX = new Point(5, 19);
        Point pointY = new Point(19, 11);
        Point pointZ = new Point(5, 13);

        Fortress fortress0 = map.placeBuilding(new Fortress(player0), pointX);

        Utils.constructHouse(fortress0);
        Utils.occupyMilitaryBuilding(Soldier.Rank.GENERAL_RANK, fortress0);

        Fortress fortress1 = map.placeBuilding(new Fortress(player0), pointY);
        Utils.constructHouse(fortress1);
        Utils.occupyMilitaryBuilding(Soldier.Rank.GENERAL_RANK, fortress1);

        Fortress fortress2 = map.placeBuilding(new Fortress(player0), pointZ);
        Utils.constructHouse(fortress2);
        Utils.occupyMilitaryBuilding(Soldier.Rank.GENERAL_RANK, fortress2);

        /* Create the list of points for the roads */
        Point[] points = {
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

        for (Point point : points) {
            map.placeFlag(player0, point);
        }

        /* Place flag */
        Point target = new Point(18, 6);
        map.placeFlag(player0, target);

        /* Place roads to build up the network */
        map.placeRoad(player0, points[0], points[0].right(), points[1]);
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
        assertFalse(route.isEmpty());

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
    public void testNeedsCourier() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
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
        Road road = map.placeAutoSelectedRoad(player0, point1, point2);

        /* Verify that the road needs a courier */
        assertTrue(road.needsCourier());

        /* Assign a courier to the road */
        Utils.occupyRoad(road, map);

        /* Verify that the road doesn't need a courier */
        assertFalse(road.needsCourier());
    }

    @Test
    public void testAssignWorkerToRoadNotOnMap() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 16, 16);

        /* Place headquarter */
        Point point0 = new Point(12, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(3, 3);
        Point point2 = new Point(7, 3);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place road */
        Point point3 = new Point(5, 3);
        Road road = map.placeRoad(player0, point1, point3, point2);

        /* Remove the road */
        map.removeRoad(road);

        /* Verify that it's not possible to assign a worker to the removed road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, flag0);

        try {
            courier.assignToRoad(road);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testAssignTwoWorkersToRoad() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(3, 3);
        Point point2 = new Point(7, 3);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place road */
        Road road = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Occupy the road */
        Courier courier0 = new Courier(player0, map);
        map.placeWorker(courier0, flag0);
        courier0.assignToRoad(road);

        /* Verify that it's not possible to occupy the road again with a second worker */
        Courier courier1 = new Courier(player0, map);
        map.placeWorker(courier1, flag0);

        try {
            courier1.assignToRoad(road);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testRoadCanNotShareSegment() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(12, 4);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(3, 3);
        Point point2 = new Point(4, 6);
        Point point3 = new Point(6, 4);

        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);
        Flag flag2 = map.placeFlag(player0, point3);

        /* Place road */
        Point middlePoint = new Point(4, 4);
        map.placeRoad(player0, point1, middlePoint, point2.downLeft(), point2);

        /* Verify that it's not possible to place a second road that shares a segment with the previous road */
        try {
            map.placeRoad(player0, point1, middlePoint, point3);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testRoadsCanNotCross() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 6);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point start1 = new Point(5, 3);
        Point end1 = new Point(7, 5);
        Point start2 = new Point(4, 6);
        Point end2 = new Point(9, 3);
        Point middlePoint = new Point(6, 4);

        map.placeFlag(player0, start1);
        map.placeFlag(player0, start2);
        map.placeFlag(player0, end1);
        map.placeFlag(player0, end2);

        /* Place road */
        map.placeRoad(player0, start1, middlePoint, end1);

        /* Verify that it's not possible to place a road that crosses another road */
        try {
            map.placeRoad(player0, start2, start2.downRight(), middlePoint, end2.left(), end2);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testWayPointsEqualsChosenRoad() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
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
        map.placeRoad(player0, start, middlePoint, end);

        /* Verify that the way points are set correctly in the road */
        Road road = map.getRoad(start, end);

        List<Point> wayPoints = road.getWayPoints();

        assertEquals(wayPoints.size(), 3);
        assertEquals(wayPoints.get(0), start);
        assertEquals(wayPoints.get(1), middlePoint);
        assertEquals(wayPoints.get(2), end);
    }

    @Test
    public void testLargerStepThanOneIsNotOk() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarter */
        Point point0 = new Point(6, 8);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point start = new Point(4, 4);
        Point end = new Point(10, 4);
        Point middlePoint = new Point(6, 4);
        map.placeFlag(player0, start);
        map.placeFlag(player0, end);

        assertTrue(player0.getLandInPoints().contains(start));

        try {
            map.placeRoad(player0, start, middlePoint, end);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPossibleDirectConnectionsFromFlag() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 14);
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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 10, 10);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the possible connections are correct in the corners */
        Point downRight = new Point(8, 2);
        Point downLeft = new Point(2, 2);
        Point upRight = new Point(8, 8);
        Point upLeft = new Point(2, 8);

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
    public void testAvailableFlagPointOnRoadIsPossibleDirectConnection() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a flag */
        Point point4 = new Point(14, 4);
        Flag flag0 = map.placeFlag(player0, point4);

        /* Place a road */
        Point point1 = new Point(8, 4);
        Point point2 = new Point(10, 4);
        Point point3 = new Point(12, 4);
        Road road0 = map.placeRoad(player0, headquarter0.getFlag().getPosition(), point1, point2, point3, point4);

        /* Verify that the available flag point on the road is a possible road connection end point */
        Point point5 = new Point(11, 5);

        assertTrue(map.isAvailableFlagPoint(player0, point2));

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point5);

        assertTrue(points.contains(point2));
    }

    @Test
    public void testPossibleDirectConnectionsOnSides() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 12, 10);

        /* Place headquarter */
        Point point0 = new Point(6, 6);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the possible connections on the side are correct */
        Point right = new Point(10, 6);
        Point left = new Point(2, 6);
        Point up = new Point(6, 8);
        Point down = new Point(6, 2);

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

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(16, 10);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(16, 12);
        Point point2 = new Point(20, 12);
        Point point3 = new Point(12, 12);
        Flag flag0 = map.placeFlag(player0, point1);
        Flag flag1 = map.placeFlag(player0, point2);
        Flag flag2 = map.placeFlag(player0, point3);

        /* Place road */
        Road road0 = map.placeRoad(player0,
                point2,
                new Point(18, 12),
                new Point(17, 13),
                new Point(18, 14),
                new Point(17, 15),
                new Point(16, 16),
                new Point(15, 15),
                new Point(14, 14),
                new Point(15, 13),
                new Point(14, 12),
                point3);

        /* Verify that there is no available road connection straight up or straight down */
        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point1);

        assertFalse(points.contains(point1.up()));
        assertFalse(points.contains(point1.down()));
    }

    @Test
    public void testNoPossibleConnectionUnderBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point2 = new Point(12, 8);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);

        /* Place flag */
        Point point3 = new Point(10, 8);
        Flag flag0 = map.placeFlag(player0, point3);

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point3);

        assertFalse(points.contains(point2));
    }

    @Test
    public void testNoPossibleConnectionUnderStone() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point2 = new Point(12, 8);
        Stone stone0 = map.placeStone(point2, Stone.StoneType.STONE_1, 7);

        /* Place flag */
        Point point3 = new Point(10, 8);
        Flag flag0 = map.placeFlag(player0, point3);

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point3);

        assertFalse(points.contains(point2));
    }

    @Test
    public void testNoPossibleConnectionUnderTree() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point2 = new Point(12, 8);
        Tree tree0 = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        /* Place flag */
        Point point3 = new Point(10, 8);
        Flag flag0 = map.placeFlag(player0, point3);

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point3);

        assertFalse(points.contains(point2));
    }

    @Test
    public void testNoPossibleConnectionsAtAndOutsideBorder() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(50, 64);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point3 = new Point(50, 72);
        Flag flag0 = map.placeFlag(player0, point3);

        /* Verify that there are no possible connections at and outside the border */
        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point3);

        /* Points on border */
        assertTrue(player0.getBorderPoints().contains(point3.upLeft()));
        assertTrue(player0.getBorderPoints().contains(point3.upRight()));
        assertFalse(points.contains(point3.upLeft()));
        assertFalse(points.contains(point3.upRight()));

        /* Points outside border */
        assertFalse(points.contains(point3.up().upLeft()));
        assertFalse(points.contains(point3.up().upRight()));
    }

    @Test
    public void testNoPossibleConnectionThroughNewCrop() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place crop */
        Point point2 = new Point(12, 8);
        Crop crop0 = map.placeCrop(point2, Crop.CropType.TYPE_1);

        /* Place flag */
        Point point3 = new Point(10, 8);
        Flag flag0 = map.placeFlag(player0, point3);

        List<Point> points = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point3);

        assertFalse(points.contains(point2));
    }

    @Test
    public void testPlaceRoadWithVarargs() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(3, 3);
        Point point2 = new Point(7, 3);
        map.placeFlag(player0, point1);
        map.placeFlag(player0, point2);

        /* Verify that it's possible to place a road with var args */
        Road road = map.placeRoad(player0, point1, point1.right(), point2);

        assertNotNull(road);
    }

    @Test
    public void testNotPossibleToPlaceRoadThroughNewCrop() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place crop */
        Point point2 = new Point(12, 8);
        Crop crop0 = map.placeCrop(point2, Crop.CropType.TYPE_1);

        /* Place flag */
        Point point3 = new Point(10, 8);
        Flag flag0 = map.placeFlag(player0, point3);

        /* Place flag */
        Point point4 = new Point(14, 8);
        Flag flag1 = map.placeFlag(player0, point4);

        /* Verify that a road can't be placed through the new crop */
        try {
            map.placeRoad(player0, point3, point2, point4);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testConnectNewRoadToFlagInExistingRoad() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(12, 4);
        Point point2 = new Point(14, 6);

        map.placeFlag(player0, point1);
        map.placeFlag(player0, point2);

        /* Place road */
        map.placeRoad(player0, point1, point1.upRight(), point2);

        /* Place flag */
        Point point3 = new Point(16, 8);
        map.placeFlag(player0, point3);

        /* Place road */
        map.placeRoad(player0, point2, point2.upRight(), point3);

        /* Place flag */
        Point point4 = new Point(16, 4);
        map.placeFlag(player0, point4);

        /* Place road */
        Road road = map.placeRoad(player0, point4, point4.upLeft(), point2);

        assertTrue(map.getRoads().contains(road));
    }

    @Test
    public void testPlaceFlagInExistingRoadSplitsTheRoad() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(12, 10);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point4 = new Point(9, 5);
        Flag flag0 = map.placeFlag(player0, point4);

        /* Place road */
        Point point1 = new Point(10, 6);
        Point point2 = new Point(11, 7);
        Point point3 = new Point(12, 8);
        map.placeRoad(player0, point4, point1, point2, point3, headquarter.getFlag().getPosition());

        assertEquals(map.getRoads().size(), 2);

        /* Place a flag in the middle of the road */
        map.placeFlag(player0, point2);

        assertEquals(map.getRoads().size(), 3);
        List<Road> roads = new ArrayList<>(map.getRoads());

        roads.remove(map.getRoad(point0, point0.downRight()));

        Road road0 = roads.get(0);
        Road road1 = roads.get(1);

        Point end = headquarter.getFlag().getPosition();

        assertTrue((road0.getStart().equals(point4) && road0.getEnd().equals(point2))
                || (road0.getStart().equals(point2) && road0.getEnd().equals(point4))
                || (road1.getStart().equals(point4) && road1.getEnd().equals(point2))
                || (road1.getStart().equals(point2) && road1.getEnd().equals(point4)));

        assertTrue((road0.getStart().equals(point2) && road0.getEnd().equals(end))
                || (road0.getStart().equals(end) && road0.getEnd().equals(point2))
                || (road1.getStart().equals(point2) && road1.getEnd().equals(end))
                || (road1.getStart().equals(end) && road1.getEnd().equals(point2)));
    }

    @Test
    public void testPlaceFlagInReverseExistingRoadSplitsTheRoad() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(14, 12);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point start = new Point(9, 5);
        Point end = new Point(13, 9);
        map.placeFlag(player0, start);
        map.placeFlag(player0, end);

        /* Place the reversed road */
        Point point1 = new Point(10, 6);
        Point point2 = new Point(11, 7);
        Point point3 = new Point(12, 8);
        map.placeRoad(player0, end, point3, point2, point1, start);

        assertEquals(map.getRoads().size(), 2);

        /* Place a flag on the road to split it */
        map.placeFlag(player0, point2);

        assertEquals(map.getRoads().size(), 3);
        List<Road> roads = new ArrayList<>(map.getRoads());

        roads.remove(map.getRoad(point0, point0.downRight()));

        Road road0 = roads.get(0);
        Road road1 = roads.get(1);

        assertTrue((road0.getStart().equals(start) && road0.getEnd().equals(point2))
                || (road0.getStart().equals(point2) && road0.getEnd().equals(start))
                || (road1.getStart().equals(start) && road1.getEnd().equals(point2))
                || (road1.getStart().equals(point2) && road1.getEnd().equals(start)));

        assertTrue((road0.getStart().equals(point2) && road0.getEnd().equals(end))
                || (road0.getStart().equals(end) && road0.getEnd().equals(point2))
                || (road1.getStart().equals(point2) && road1.getEnd().equals(end))
                || (road1.getStart().equals(end) && road1.getEnd().equals(point2)));
    }

    @Test
    public void testIdleCourierIsAssignedWhenRoadIsSplit() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(player0, endPoint);

        /* Place road */
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Road road = map.placeRoad(player0, headquarter.getFlag().getPosition(),
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
    public void testCourierDeliveringToBuildingChoosesClosestRoadWhenRoadIsSplit() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter at the end of the road */
        Point endPoint = new Point(14, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), endPoint.upLeft());

        /* Place road from the flag to the headquarter's flag */
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Road road = map.placeRoad(player0, headquarter.getFlag().getPosition(),
                middlePoint1,
                middlePoint2,
                middlePoint3,
                woodcutter.getFlag().getPosition());

        /* Wait for a courier to reach the first half of the road */
        Courier courier = null;
        for (int i = 0; i < 1000; i++) {

            for (Worker worker : map.getWorkers()) {
                if (worker instanceof Courier) {
                    if (((Courier)worker).getAssignedRoad().equals(road)) {
                        courier = (Courier)worker;
                        break;
                    }
                }
            }

            if (courier != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(courier);
        assertEquals(courier.getTarget(), middlePoint2);
        assertEquals(courier.getAssignedRoad(), road);

        /* Wait for the courier to start delivering plank cargo to the woodcutter */
        assertTrue(woodcutter.needsMaterial(PLANK));
        assertTrue(headquarter.getAmount(PLANK) > 0);
        assertTrue(map.arePointsConnectedByRoads(headquarter.getPosition(), woodcutter.getPosition()));

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, PLANK);

        /* Wait for the courier to reach the woodcutter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, woodcutter.getFlag().getPosition());

        /* Make the courier take a step toward the woodcutter */
        map.stepTime();

        assertFalse(courier.isExactlyAtPoint());

        /* Verify that the courier chooses the closest road when the road is split */
        map.placeFlag(player0, middlePoint2);

        assertEquals(courier.getAssignedRoad(), map.getRoad(woodcutter.getFlag().getPosition(), middlePoint2));
    }

    @Test
    public void testCourierJustAfterDeliveryToBuildingChoosesClosestRoadWhenRoadIsSplit() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter at the end of the road */
        Point endPoint = new Point(14, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), endPoint.upLeft());

        /* Place road from the flag to the headquarter's flag */
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Road road = map.placeRoad(player0, headquarter.getFlag().getPosition(),
                middlePoint1,
                middlePoint2,
                middlePoint3,
                woodcutter.getFlag().getPosition());

        /* Wait for a courier to reach the first half of the road */
        Courier courier = null;
        for (int i = 0; i < 1000; i++) {

            for (Worker worker : map.getWorkers()) {
                if (worker instanceof Courier) {
                    if (((Courier)worker).getAssignedRoad().equals(road)) {
                        courier = (Courier)worker;
                        break;
                    }
                }
            }

            if (courier != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(courier);
        assertEquals(courier.getTarget(), middlePoint2);
        assertEquals(courier.getAssignedRoad(), road);

        /* Wait for the courier to start delivering plank cargo to the woodcutter */
        assertTrue(woodcutter.needsMaterial(PLANK));
        assertTrue(headquarter.getAmount(PLANK) > 0);
        assertTrue(map.arePointsConnectedByRoads(headquarter.getPosition(), woodcutter.getPosition()));

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, PLANK);

        /* Wait for the courier to reach the woodcutter */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, woodcutter.getPosition());

        /* Make the courier take a step from the woodcutter */
        map.stepTime();

        assertFalse(courier.isExactlyAtPoint());

        /* Verify that the courier chooses the closest road when the road is split */
        map.placeFlag(player0, middlePoint2);

        assertEquals(courier.getAssignedRoad(), map.getRoad(woodcutter.getFlag().getPosition(), middlePoint2));
    }

    @Test
    public void testIdleCourierIsAssignedToClosestFromLeftRoadWhenRoadIsSplit() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(player0, endPoint);

        /* Place road from the flag to the headquarter's flag */
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Road road = map.placeRoad(player0, headquarter.getFlag().getPosition(),
                middlePoint1,
                middlePoint2,
                middlePoint3,
                endPoint);

        /* Wait for a courier to reach the first half of the road */
        Courier courier = Utils.waitForWorkersOutsideBuilding(Courier.class, 1, player0).get(0);

        assertEquals(courier.getTarget(), middlePoint2);
        assertEquals(courier.getAssignedRoad(), road);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, middlePoint1);

        assertTrue(courier.isAt(middlePoint1));

        /* Split road */
        map.placeFlag(player0, middlePoint2);

        assertEquals(courier.getAssignedRoad(), map.getRoad(headquarter.getFlag().getPosition(), middlePoint2));
    }

    @Test
    public void testIdleCourierIsAssignedToClosestFromRightRoadWhenRoadIsSplit() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(player0, endPoint);

        /* Place road from the flag to the headquarter's flag */
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Road road = map.placeRoad(player0, headquarter.getFlag().getPosition(),
                middlePoint1,
                middlePoint2,
                middlePoint3,
                endPoint);

        /* Put a courier on the second half of the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, endFlag);
        courier.assignToRoad(road);

        assertEquals(courier.getTarget(), middlePoint2);
        assertEquals(courier.getAssignedRoad(), road);

        /* Verify that the courier chooses the road to the right after the split */
        map.placeFlag(player0, middlePoint2);

        assertEquals(courier.getAssignedRoad(), map.getRoad(middlePoint2, endPoint));
    }

    @Test
    public void testCourierDeliveringCargoFinishesDeliveryAndIsAssignedWhenRoadIsSplit() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(player0, endPoint);

        /* Place road */
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Road road = map.placeRoad(player0, headquarter.getFlag().getPosition(),
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

        /* Place the cargo on the end of the road, furthest away from the headquarter */
        Cargo cargo = new Cargo(BEER, map);
        endFlag.putCargo(cargo);
        cargo.setTarget(headquarter);

        map.stepTime();

        assertEquals(courier.getTarget(), endPoint);

        /* Make the courier pick up a cargo and start walking to deliver it */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, endPoint);

        map.stepTime();

        assertTrue(courier.isTraveling());
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), headquarter.getPosition());

        /* Split road with the courier on the road further away from the headquarter */
        map.placeFlag(player0, middlePoint2);

        assertFalse(courier.isWalkingToRoad());
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), middlePoint2);

        assertTrue((courier.getAssignedRoad().getStart().equals(middlePoint2) && courier.getAssignedRoad().getEnd().equals(endPoint))
                || (courier.getAssignedRoad().getEnd().equals(middlePoint2) && courier.getAssignedRoad().getStart().equals(endPoint)));
    }

    @Test
    public void testCourierFarFromToBuildingDeliveringCargoFinishesDeliveryAndBecomesIdleWhenRoadIsSplit() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Storehouse headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(14, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Road road = map.placeRoad(player0, headquarter.getFlag().getPosition(),
                middlePoint1,
                middlePoint2,
                middlePoint3,
                point1);

        /* Wait for a courier to occupy the road */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road);

        assertEquals(courier.getAssignedRoad(), road);
        assertEquals(road.getCourier(), courier);

        /* Wait for the courier to be idle at the middle the road */
        Utils.waitForCouriersToBeIdle(map, courier);

        assertTrue(courier.isIdle());
        assertTrue(courier.isAt(middlePoint2));

        /* Make the courier pick up a cargo and start walking to deliver it */
        Cargo cargo = Utils.placeCargo(map, BEER, flag0, headquarter);

        map.stepTime();

        assertEquals(courier.getTarget(), point1);

        /* Fast forward until the courier picks up the cargo */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, point1);

        map.stepTime();

        assertTrue(courier.isTraveling());
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), headquarter.getPosition());

        /* Split road with the courier on the new road further away from the headquarter */
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

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Storehouse headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(player0, endPoint);

        /* Place road */
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Road road = map.placeRoad(player0, headquarter.getFlag().getPosition(),
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
        cargo.setTarget(headquarter);

        map.stepTime();

        assertEquals(courier.getTarget(), endPoint);

        /* Fast forward until the courier picks up the cargo */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, endPoint);

        map.stepTime();

        assertTrue(courier.isTraveling());
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), headquarter.getPosition());

        /* Let the courier pass the middle of the road */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, middlePoint1);

        /* Split road with the courier on the new road closer to the headquarter */
        Flag middleFlag = map.placeFlag(player0, middlePoint2);

        assertFalse(courier.isWalkingToRoad());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier is still targeting the headquarter */
        assertEquals(courier.getTarget(), headquarter.getPosition());

        /* Let the courier leave the cargo */
        assertEquals(headquarter.getAmount(BEER), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter.getPosition());

        assertNull(courier.getCargo());
        assertEquals(headquarter.getAmount(BEER), 1);

        /* Let the courier walk back to the headquarter's flag */
        assertEquals(courier.getTarget(), headquarter.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter.getFlag().getPosition());

        /* Verify that the courier becomes idle after delivery */
        assertEquals(courier.getTarget(), middlePoint1);

        Utils.fastForwardUntilWorkersReachTarget(map, courier);

        assertTrue(courier.isIdle());
        assertEquals(courier.getPosition(), middlePoint1);
    }

    @Test
    public void testCourierWalkingToAssignedRoadAdaptsWhenItsRoadIsSplit() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(player0, endPoint);

        /* Place road */
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Road road = map.placeRoad(player0, headquarter.getFlag().getPosition(),
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

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(player0, endPoint);

        /* Place road */
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Road road = map.placeRoad(player0, headquarter.getFlag().getPosition(),
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
        Road road1 = map.getRoad(middlePoint2, endPoint);

        if (!road1.needsCourier()) {
            road1 = map.getRoad(headquarter.getFlag().getPosition(), middlePoint2);
        }

        assertTrue(road1.needsCourier());

        assertEquals(GameUtils.getClosestStorageConnectedByRoads(road1.getStart(), player0), headquarter);

        map.stepTime();

        assertEquals(map.getWorkers().size(), 3);

        Worker worker2 = null;

        for (Worker worker : map.getWorkers()) {
            if (!(worker instanceof Courier)) {
                continue;
            }

            if (worker.equals(courier)) {
                continue;
            }

            worker2 = worker;

            break;
        }

        assertNotNull(worker2);
        assertTrue(worker2 instanceof Courier);

        Courier secondCourier = (Courier) worker2;

        assertNotNull(secondCourier);
        assertNotNull(secondCourier.getAssignedRoad());
        assertNotEquals(secondCourier.getAssignedRoad(), road);
    }

    @Test
    public void testCourierDeliversCorrectlyToBuildingAfterItsRoadIsSplit() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Storehouse headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point endPoint = new Point(14, 4);
        Flag endFlag = map.placeFlag(player0, endPoint);

        /* Place road */
        Point middlePoint1 = new Point(8, 4);
        Point middlePoint2 = new Point(10, 4);
        Point middlePoint3 = new Point(12, 4);
        Road road = map.placeRoad(player0, headquarter.getFlag().getPosition(),
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
        cargo.setTarget(headquarter);

        map.stepTime();

        assertEquals(courier.getTarget(), endPoint);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, endPoint);

        map.stepTime();

        assertTrue(courier.isTraveling());
        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), headquarter.getPosition());

        /* Let the courier get close to the headquarter */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, middlePoint1);

        /* Split road with the courier close to the headquarter */
        Flag middleFlag = map.placeFlag(player0, middlePoint2);

        assertEquals(headquarter.getAmount(BEER), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter.getPosition());

        assertNull(courier.getCargo());
        assertEquals(headquarter.getAmount(BEER), 1);
    }

    @Test
    public void testRoadCanNotOverlapExistingFlag() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point point1 = new Point(10, 4);
        Point point2 = new Point(13, 7);
        Point point3 = new Point(11, 5);
        Point point4 = new Point(12, 6);
        Point point5 = new Point(14, 4);
        Point point6 = new Point(9, 7);
        map.placeFlag(player0, point1);
        map.placeFlag(player0, point2);
        map.placeFlag(player0, point5);
        map.placeFlag(player0, point6);

        /* Place road */
        map.placeRoad(player0, point1, point3, point4, point2);

        /* Verify that placing a road across a flag doesn't work */
        Point point7 = new Point(12, 4);
        Point point8 = new Point(9, 5);
        Point point9 = new Point(8, 6);
        try {
            map.placeRoad(player0, point5, point7, point1, point8, point9, point6);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testOnlyOneCourierIsAssignedToNewRoad() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(12, 6);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        assertTrue(road0.needsCourier());
        assertEquals(map.getWorkers().size(), 1);

        List<Worker> workersBefore = new LinkedList<>(map.getWorkers());

        /* Step time to let the headquarter send new workers */
        map.stepTime();

        /* Let the new courier reach its road */
        assertEquals(map.getWorkers().size(), 2);

        /* Find the added courier */
        List<Worker> workersAfter = new LinkedList<>(map.getWorkers());
        workersAfter.removeAll(workersBefore);

        Worker worker = workersAfter.get(0);

        Utils.fastForwardUntilWorkersReachTarget(map, worker);

        assertFalse(road0.needsCourier());
        assertEquals(road0.getCourier(), worker);

        Utils.fastForward(100, map);

        assertFalse(road0.needsCourier());
        assertTrue(map.getWorkers().size() >= 2);
        assertEquals(road0.getCourier(), worker);
    }

    @Test
    public void testThatCourierIsNotDispatchedToNewRoadWithNoConnection() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(13, 7);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place flag */
        Point point2 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place a road */
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

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(14, 10);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place flag */
        Point point2 = new Point(13, 5);
        Flag flag0 = map.placeFlag(player0, point2);

        Utils.fastForward(100, map);

        /* Place road */
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

        assertTrue(map.getWorkers().size() >= 3);
        assertFalse(road0.needsCourier());

        Utils.fastForward(10, map);

        Utils.fastForward(10, map);

        Utils.fastForward(10, map);

        assertTrue(map.getWorkers().size() >= 3);
    }

    @Test
    public void testFindShortestWay() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(6, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        List<Point> path = map.findAutoSelectedRoad(player0, new Point(3, 3), new Point(12, 12), null);

        assertEquals(path.size(), 10);

        path = map.findAutoSelectedRoad(player0, new Point(12, 12), new Point(4, 4), null);

        assertEquals(path.size(), 9);

        path = map.findAutoSelectedRoad(player0, new Point(4, 4), new Point(12, 4), null);

        assertEquals(path.size(), 5);

        path = map.findAutoSelectedRoad(player0, new Point(12, 4), new Point(4, 4), null);

        assertEquals(path.size(), 5);
    }

    @Test
    public void testRoadCannotGoThroughSmallBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point2 = new Point(8, 6);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Verify that it's not possible to place a road through the flag */
        Point point3 = new Point(6, 4);
        Point point4 = new Point(5, 3);
        Point point5 = new Point(4, 4);
        Point point6 = new Point(6, 6);

        try {
            Road road0 = map.placeRoad(player0, point3, point4, point5, point0, point6, point2);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testRoadIsCreatedBetweenHouseAndFlag() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that there is a road between the headquarter and its flag */
        Point point3 = new Point(6, 4);

        assertNotNull(map.getRoad(point0, point3));
    }

    @Test
    public void testRoadBetweenHouseAndFlagNeedsNoCourier() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the drive way for the headquarter doesn't need a courier */
        Point point3 = new Point(6, 4);

        assertFalse(map.getRoad(point0, point3).needsCourier());
    }

    @Test
    public void testCannotPlaceFlagsTooCloseTogether() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(3, 3);
        map.placeFlag(player0, point1);

        /* Verify that it's not possible to place a flag too close to the other flag */
        Point point2 = new Point(5, 3);

        try {
            map.placeFlag(player0, point2);

            fail();
        } catch (Exception e) {}

        assertFalse(map.isFlagAtPoint(point2));
    }

    @Test
    public void testSplitHorizontalRoadWithTooShortRemainingRoads() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flags */
        Point start = new Point(9, 5);
        Point end = new Point(17, 5);
        map.placeFlag(player0, start);
        map.placeFlag(player0, end);

        /* Place road */
        Point point1 = new Point(11, 5);
        Point point2 = new Point(13, 5);
        Point point3 = new Point(15, 5);
        Road road0 = map.placeRoad(player0, start, point1, point2, point3, end);

        assertEquals(map.getRoads().size(), 2);

        List<Point> wayPointsBefore = new ArrayList<>(road0.getWayPoints());

        /* Verify that it's not possible to place a flag on the road directly next to another flag */
        try {
            map.placeFlag(player0, point1);
            fail();
        } catch (Exception e) {
        }

        assertTrue(map.getRoads().contains(road0));
        assertEquals(road0.getWayPoints().size(), 5);
        assertEquals(map.getRoads().size(), 2);
        assertEquals(wayPointsBefore, road0.getWayPoints());
        assertNotNull(map.getRoad(start, end));
    }

    @Test
    public void testSplitHorizontalRoadInEndWithTooShortRemainingRoads() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place start and end flag */
        Point start = new Point(9, 5);
        Point end = new Point(17, 5);
        map.placeFlag(player0, start);
        map.placeFlag(player0, end);

        /* Create the road */
        Point point1 = new Point(11, 5);
        Point point2 = new Point(13, 5);
        Point point3 = new Point(15, 5);
        map.placeRoad(player0, start, point1, point2, point3, end);

        assertEquals(map.getRoads().size(), 2);

        /* Verify that the road is too small to split and an exception is thrown */
        try {
            map.placeFlag(player0, point3);
            fail();
        } catch (Exception e) {
        }

        assertEquals(map.getRoads().size(), 2);
        assertNotNull(map.getRoad(start, end));
    }

    @Test
    public void testCourierGoesBackToStorageWhenRoadIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place road */
        Point point26 = new Point(8, 8);
        Flag flag0 = map.placeFlag(player0, point26);
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Occupy the road */
        Utils.occupyRoad(road0, map);

        /* Remove the road */
        Worker worker = road0.getCourier();

        assertTrue(road0.getWayPoints().contains(worker.getPosition()));

        map.removeRoad(road0);

        /* Verify that the worker goes back to the headquarter */
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

        /* Verify that the worker is no longer on the map */
        assertFalse(map.getWorkers().contains(worker));
    }

    @Test
    public void testCourierGoesBackToStorageOnRoadsIfPossibleWhenRoadIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

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
        Worker courier = road1.getCourier();

        assertTrue(road1.getWayPoints().contains(courier.getPosition()));

        map.removeRoad(road1);

        /* Verify that the worker leaves goes back to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        for (Point point : courier.getPlannedPath()) {
            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testRoadCannotBePlacedThroughExistingFlagThatIsAlsoEndpoint() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place flag */
        Point point39 = new Point(10, 8);
        Flag flag0 = map.placeFlag(player0, point39);

        /* Place road between (6, 4) and (10, 8) */
        Point point40 = new Point(6, 4);
        Point point41 = new Point(7, 5);
        Point point42 = new Point(8, 6);
        Point point43 = new Point(9, 7);
        Point point44 = new Point(11, 9);
        Point point45 = new Point(10, 10);
        Point point46 = new Point(9, 9);

        try {
            Road road0 = map.placeRoad(player0, point40, point41, point42, point43, point39, point44, point45, point46, point39);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testRoadBecomesAgedWithDeliveryToBuilding() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place flag */
        Point point2 = new Point(5, 9);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place road between the headquarter and the flag */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Place a worker on the road */
        Courier courier = Utils.occupyRoad(road0, map);

        /* Deliver 99 cargo and verify that the road does not become a main road */
        for (int i = 0; i < 99; i++) {
            Cargo cargo = Utils.placeCargo(map, COIN, flag0, headquarter0);

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
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

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

        /* Deliver 99 cargo and verify that the road does not become a main road */
        for (int i = 0; i < 99; i++) {
            Cargo cargo = Utils.placeCargo(map, COIN, flag1, headquarter0);

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
    public void testDrivewayBecomesAgedWithDeliveriesToStorage() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place well */
        Point point2 = new Point(5, 9);
        Well well0 = map.placeBuilding(new Well(player0), point2);

        /* Construct the well */
        Utils.constructHouse(well0);

        /* Occupy the well */
        Utils.occupyBuilding(new WellWorker(player0, map), well0);

        /* Place road between the headquarter and the flag */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        /* Place a worker on the road */
        Courier courier = Utils.occupyRoad(road0, map);

        /* Wait for the courier to deliver 99 water cargo without turning the driveways into main roads */
        Road driveway = map.getRoad(headquarter0.getPosition(), headquarter0.getFlag().getPosition());

        for (int i = 0; i < 99; i++) {

            /* Wait for the courier to pick up a water cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, courier, WATER);

            /* Wait for the courier to deliver the water */
            assertEquals(courier.getTarget(), headquarter0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

            assertNull(courier.getCargo());
            assertFalse(driveway.isMainRoad());
        }

        /* Verify that the driveways become main roads after one more delivery */
        assertNull(courier.getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, WATER);

        /* Wait for the courier to deliver the cargo */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        assertTrue(driveway.isMainRoad());
    }

    @Test
    public void testDrivewayBecomesAgedWithWellWorkersDeliveries() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point38 = new Point(5, 11);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place well */
        Point point2 = new Point(5, 15);
        Well well0 = map.placeBuilding(new Well(player0), point2);

        /* Construct the well */
        Utils.constructHouse(well0);

        /* Occupy the well */
        WellWorker wellWorker = Utils.occupyBuilding(new WellWorker(player0, map), well0);

        /* Place road between the headquarter and the flag */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        /* Place a worker on the road */
        Utils.occupyRoad(road0, map);

        /* Wait for the well worker to deliver 99 water cargo without turning the driveways into main roads */
        Road driveway = map.getRoad(well0.getPosition(), well0.getFlag().getPosition());

        for (int i = 0; i < 99; i++) {

            /* Wait for the well worker to pick up a water cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, wellWorker, WATER);

            /* Wait for the well worker to deliver the water */
            assertEquals(wellWorker.getTarget(), well0.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, well0.getFlag().getPosition());

            assertNull(wellWorker.getCargo());
            assertFalse(driveway.isMainRoad());
        }

        /* Verify that the driveways become main roads after one more delivery */
        assertNull(wellWorker.getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(map, wellWorker, WATER);

        /* Wait for the courier to deliver the cargo */
        assertEquals(wellWorker.getTarget(), well0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, wellWorker, well0.getFlag().getPosition());

        assertTrue(driveway.isMainRoad());
    }

    @Test
    public void testDifferentRoadsWithSameEndpointsAreNotEqual() throws Exception {

        /* Create player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter for player0 */
        Point point17 = new Point(8, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point17);

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

        assertNotEquals(road1, road2);
    }

    @Test
    public void testGetOtherEndPoint() throws Exception {

        /* Create player */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new LinkedList<>();
        players.add(player0);

        /* Creating game map */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter for player0 */
        Point point17 = new Point(8, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point17);

        /* Place flag */
        Point point0 = new Point(12, 10);
        Flag flag0 = map.placeFlag(player0, point0);

        /* Place road between the flag and the headquarter's flag */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Verify that we can get the other endpoints for the new road */
        assertEquals(road0.getOtherEndPoint(flag0), headquarter0.getFlag());
        assertEquals(road0.getOtherEndPoint(headquarter0.getFlag()), flag0);

        /* Verify that we can get the other endpoints for the driveway for the headquarter */
        Road road1 = map.getRoad(headquarter0.getPosition(), headquarter0.getFlag().getPosition());

        assertEquals(road1.getOtherEndPoint(headquarter0), headquarter0.getFlag());
        assertEquals(road1.getOtherEndPoint(headquarter0.getFlag()), headquarter0);
    }

    @Test
    public void testSplitMainRoadResultsInTwoMainRoadsAndMainFlag() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place flag */
        Point point2 = new Point(5, 9);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place flag */
        Point point3 = new Point(13, 9);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Place road between the headquarters and the first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Place road between the headquarter and the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Place workers on the roads */
        Courier courier0 = Utils.occupyRoad(road0, map);
        Courier courier1 = Utils.occupyRoad(road1, map);

        /* Deliver 99 cargo and verify that the road does not become a main road */
        for (int i = 0; i < 99; i++) {
            Cargo cargo = Utils.placeCargo(map, COIN, flag1, headquarter0);

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

        /* Test that splitting the main road into two by placing a flag in the middle creates two main roads */
        Point point4 = new Point(9, 9);
        Flag flag2 = map.placeFlag(player0, point4);

        assertTrue(map.getRoad(flag0.getPosition(), flag2.getPosition()).isMainRoad());
        assertTrue(map.getRoad(flag2.getPosition(), flag1.getPosition()).isMainRoad());
        assertEquals(flag2.getType(), Flag.FlagType.MAIN);
    }
}
