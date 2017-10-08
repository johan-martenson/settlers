/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author johan
 */
public class Point extends java.awt.Point {
    private static final long serialVersionUID = 1L;

    /* Uncomment below to track allocations of points */
    //static Map<String, Integer> allocators = new HashMap<>();
    //static int printCount = 0;

    public Point(int x, int y) {
        super(x, y);

        /* Throw an exception if the sum of x and y is odd which is clearly 
           invalid */
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
        return from.y == this.y && from.x == this.x - 2;
    }

    public boolean isUpLeftOf(Point from) {
        return from.x == this.x - 1 && from.y == this.y + 1;
    }

    public boolean isUpRightOf(Point from) {
        return from.x == this.x + 1 && from.y == this.y + 1;
    }

    public boolean isRightOf(Point from) {
        return from.x == this.x + 2 && from.y == this.y;
    }

    public boolean isDownRightOf(Point from) {
        return from.x == this.x + 1 && from.y == this.y - 1;
    }

    public boolean isDownLeftOf(Point from) {
        return from.x == this.x - 1 && from.y == this.y - 1;
    }
}
