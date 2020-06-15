/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.MEDIUM;
import static org.appland.settlers.model.Size.SMALL;

/**
 *
 * @author johan
 */
public class Tree implements Actor {
    private Size size;

    private final Countdown countdown;
    private final Point position;

    Tree(Point point) {
        size = SMALL;

        position = point;

        countdown = new Countdown();

        countdown.countFrom(200);
    }

    @Override
    public void stepTime() {
        if (size == LARGE) {
            return;
        }

        if (countdown.hasReachedZero()) {
            if (size == MEDIUM) {
                size = LARGE;

                countdown.countFrom(200);
            } else if (size == SMALL) {
                size = MEDIUM;

                countdown.countFrom(200);
            }
        } else {
            countdown.step();
        }
    }

    public Point getPosition() {
        return position;
    }

    public Size getSize() {
        return size;
    }

    public String toString() {
        return "Tree (" + position.x + ", " + position.y + ")";
    }
}
