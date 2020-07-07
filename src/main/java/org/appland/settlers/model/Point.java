/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 *
 * @author johan
 */
public class Point extends java.awt.Point {
    private static final long serialVersionUID = 1L;

    /* Uncomment below to track allocations of points */
    //static Map<String, Integer> allocators = new HashMap<>();
    //static int printCount = 0;

    public Point(java.awt.Point point) {
        super(point.x, point.y);
    }

    public Point(int x, int y) {
        super(x, y);

        /* Throw an exception if the sum of x and y is odd which is clearly invalid */
        if ((x + y) % 2 == 1) {
            throw new RuntimeException("Can't create point " + x + ", " + y);
        }

        /* Uncomment below to track allocations of points */
        /*StackTraceElement frame1 = Thread.currentThread().getStackTrace()[2];
        StackTraceElement frame2 = Thread.currentThread().getStackTrace()[3];
        String method1 = frame1.getClassName() + "::" + frame1.getMethodName();
        String method2 = frame2.getClassName() + "::" + frame2.getMethodName();

        allocators.put(method1, allocators.getOrDefault(method1, 0) + 1);
        allocators.put(method2, allocators.getOrDefault(method2, 0) + 1);

        printCount++;

        if (printCount % 20000 == 0) {
            System.out.println("\n\nAllocated points at");

            for (String key : allocators.keySet()) {
                if (allocators.get(key) > 1000) {
                    System.out.println("  " + key + " - " + allocators.get(key));
                }
            }
        }*/
    }

    public static Point fitToGamePoint(double x, double y) {
        int roundedGameX = (int)Math.round(x);
        int roundedGameY = (int)Math.round(y);

        double faultX = x - roundedGameX;
        double faultY = y - roundedGameY;

        /* Call the handler directly if both points are odd or even */
        if ((roundedGameX + roundedGameY) % 2 != 0) {

            /* Find the closest valid point (odd-odd, or even-even) */
            if (abs(faultX) > abs(faultY)) {

                if (faultX > 0) {
                    roundedGameX++;
                } else {
                    roundedGameX--;
                }
            } else if (abs(faultX) < abs(faultY)) {
                if (faultY > 0) {
                    roundedGameY++;
                } else {
                    roundedGameY--;
                }
            } else {
                roundedGameX++;
            }
        }

        return new Point(roundedGameX, roundedGameY);
    }

    public static boolean isValid(int i, int j) {
        return (i + j) % 2 == 0;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public Point downRight() {
        return new Point(x + 1, y - 1);
    }

    public Point right() {
        return new Point(x + 2, y);
    }

    public Point down() {
        return new Point(x, y - 2);
    }

    public Point left() {
        return new Point(x - 2, y);
    }

    public Point downLeft() {
        return new Point(x - 1, y - 1);
    }

    public Point upLeft() {
        return new Point(x - 1, y + 1);
    }

    public Point up() {
        return new Point(x, y + 2);
    }

    public Point upRight() {
        return new Point(x + 1, y + 1);
    }

    public boolean isAdjacent(Point p) {
        if (p.equals(this)) {
            return false;
        }

        return abs(p.x - x) + abs(p.y - y) <= 2;
    }

    public Point[] getAdjacentPoints() {
        Point[] adjacentPoints = new Point[8];

        adjacentPoints[0] = new Point(x - 2, y    );
        adjacentPoints[1] = new Point(x - 1, y + 1);
        adjacentPoints[2] = new Point(x    , y + 2);
        adjacentPoints[3] = new Point(x + 1, y + 1);
        adjacentPoints[4] = new Point(x + 2, y    );
        adjacentPoints[5] = new Point(x + 1, y - 1);
        adjacentPoints[6] = new Point(x    , y - 2);
        adjacentPoints[7] = new Point(x - 1, y - 1);

        return adjacentPoints;
    }

    public Point[] getAdjacentPointsExceptAboveAndBelow() {
        Point[] adjacentPoints = new Point[6];

        adjacentPoints[0] = new Point(x - 2, y    );
        adjacentPoints[1] = new Point(x - 1, y + 1);
        adjacentPoints[2] = new Point(x + 1, y + 1);
        adjacentPoints[3] = new Point(x + 2, y    );
        adjacentPoints[4] = new Point(x + 1, y - 1);
        adjacentPoints[5] = new Point(x - 1, y - 1);

        return adjacentPoints;
    }

    Iterable<Point> getDiagonalPoints() {
        List<Point> result = new ArrayList<>();

        result.add(upRight());
        result.add(downRight());
        result.add(upLeft());
        result.add(downLeft());

        return result;
    }

    List<Point> getDiagonalPointsAndSides() {
        List<Point> result = new ArrayList<>();

        result.add(upRight());
        result.add(downRight());
        result.add(upLeft());
        result.add(downLeft());
        result.add(left());
        result.add(right());

        return result;
    }

    public boolean isLeftOf(Point from) {
        return x == from.x - 2 && y == from.y;
    }

    public boolean isUpLeftOf(Point from) {
        return x == from.x - 1 && y == from.y + 1;
    }

    public boolean isUpRightOf(Point from) {
        return x == from.x + 1 && y == from.y + 1;
    }

    public boolean isRightOf(Point from) {
        return x == from.x + 2 && y == from.y;
    }

    public boolean isDownRightOf(Point from) {
        return x == from.x + 1 && y == from.y - 1;
    }

    public boolean isDownLeftOf(Point from) {
        return x == from.x - 1 && y == from.y - 1;
    }

    public Point upRightUpRight() {
        return new Point(x + 2, y + 2);
    }

    public Point downLeftDownLeft() {
        return new Point(x - 2, y - 2);
    }

    public Point downRightDownRight() {
        return new Point(x + 2, y - 2);
    }

    public Point downLeftLeft() {
        return new Point(x - 3, y - 1);
    }

    public Point downRightRight() {
        return new Point(x + 3, y - 1);
    }

    public Point upRightRight() {
        return new Point(x + 3, y + 1);
    }

    public Point rightRight() {
        return new Point(x + 4, y);
    }

    public Point leftLeft() {
        return new Point(x - 4, y);
    }

    public Point upLeftLeft() {
        return new Point(x - 3, y + 1);
    }

    public Point upLeftUpLeft() {
        return new Point(x - 2, y + 2);
    }
}
