/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

/**
 *
 * @author johan
 */
public class Tree {
    private static final int TIME_TO_GROW_TREE_ONE_STEP = 149; // TODO: update based on measurements from the game

    private final Countdown countdown;
    private final Point position;

    private TreeSize size;
    private TreeType type;

    Tree(Point point) {

        /* Make trees start out as newly planted by default */
        size = TreeSize.NEWLY_PLANTED;

        position = point;

        countdown = new Countdown();

        countdown.countFrom(TIME_TO_GROW_TREE_ONE_STEP);

        /* Make one of the tree types default */
        type = TreeType.PINE;
    }

    public void stepTime() {

        if (size == TreeSize.FULL_GROWN) {
            return;
        }

        if (countdown.hasReachedZero()) {
            if (size == TreeSize.MEDIUM) {
                size = TreeSize.FULL_GROWN;

                countdown.countFrom(TIME_TO_GROW_TREE_ONE_STEP);
            } else if (size == TreeSize.SMALL) {
                size = TreeSize.MEDIUM;

                countdown.countFrom(TIME_TO_GROW_TREE_ONE_STEP);
            } else if (size == TreeSize.NEWLY_PLANTED) {
                size = TreeSize.SMALL;

                countdown.countFrom(TIME_TO_GROW_TREE_ONE_STEP);
            }
        } else {
            countdown.step();
        }
    }

    public Point getPosition() {
        return position;
    }

    public TreeSize getSize() {
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
        PINE,
        BIRCH,
        OAK,
        PALM_1,
        PALM_2,
        PINE_APPLE,
        CYPRESS,
        CHERRY,
        FIR
    }
}
