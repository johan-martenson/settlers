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
public class Tree {
    private static final int TIME_TO_GROW_TREE_ONE_STEP = 200;

    private final Countdown countdown;
    private final Point position;

    private Size size;
    private TreeType type;

    Tree(Point point) {
        size = SMALL;

        position = point;

        countdown = new Countdown();

        countdown.countFrom(TIME_TO_GROW_TREE_ONE_STEP);

        /* Make one of the tree types default */
        type = TreeType.TREE_TYPE_1;
    }

    public void stepTime() {
        if (size == LARGE) {
            return;
        }

        if (countdown.hasReachedZero()) {
            if (size == MEDIUM) {
                size = LARGE;

                countdown.countFrom(TIME_TO_GROW_TREE_ONE_STEP);
            } else if (size == SMALL) {
                size = MEDIUM;

                countdown.countFrom(TIME_TO_GROW_TREE_ONE_STEP);
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

    public TreeType getTreeType() {
        return type;
    }

    public void setTreeType(TreeType type) {
        this.type = type;
    }

    public enum TreeType {
        TREE_TYPE_1,
        TREE_TYPE_2,
        TREE_TYPE_3,
        TREE_TYPE_4,
        TREE_TYPE_5,
        TREE_TYPE_6,
        TREE_TYPE_7,
        TREE_TYPE_8,
        TREE_TYPE_9;
    }
}
