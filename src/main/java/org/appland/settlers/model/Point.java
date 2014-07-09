/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static java.lang.Math.abs;

/**
 *
 * @author johan
 */
public class Point extends java.awt.Point {

    public Point(int x, int y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    Point downRight() {
        return new Point(x + 1, y - 1);
    }

    Point right() {
        return new Point(x + 2, y);
    }

    Point down() {
        return new Point(x, y - 2);
    }

    Point left() {
        return new Point(x - 2, y);
    }

    Point downLeft() {
        return new Point(x - 1, y - 1);
    }

    Point upLeft() {
        return new Point(x - 1, y + 1);
    }

    Point up() {
        return new Point(x, y + 2);
    }

    Point upRight() {
        return new Point(x + 1, y + 1);
    }

    boolean isAdjacent(Point p) {
        if (p.equals(this)) {
            return false;
        }

        return abs(p.x - x) + abs(p.y - y) == 2;
    }
}
