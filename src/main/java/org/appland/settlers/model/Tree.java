/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.appland.settlers.model.Tree.TreeType.BIRCH;
import static org.appland.settlers.model.Tree.TreeType.CHERRY;
import static org.appland.settlers.model.Tree.TreeType.CYPRESS;
import static org.appland.settlers.model.Tree.TreeType.FIR;
import static org.appland.settlers.model.Tree.TreeType.OAK;
import static org.appland.settlers.model.Tree.TreeType.PALM_1;
import static org.appland.settlers.model.Tree.TreeType.PALM_2;
import static org.appland.settlers.model.Tree.TreeType.PINE;

/**
 *
 * @author johan
 */
public class Tree {
    public static final TreeType[] PLANTABLE_TREES = {
            PINE,
            BIRCH,
            OAK,
            PALM_1,
            PALM_2,
            CYPRESS,
            CHERRY,
            FIR
    };
    public static final Set<TreeType> TREE_TYPES_THAT_CAN_BE_CUT_DOWN = new HashSet<>(Arrays.asList(Tree.PLANTABLE_TREES));

    private static final int TIME_TO_GROW_TREE_ONE_STEP = 149; // TODO: update based on measurements from the game
    public static final int TIME_TO_FALL = 10;

    private final Countdown countdown;
    private final Point position;

    private TreeSize size;
    private TreeType type;
    private boolean isFallingDown;
    private GameMap map;

    Tree(Point point, TreeType treeType, TreeSize treeSize) {
        size = treeSize;
        position = point;
        countdown = new Countdown();
        type = treeType;
        isFallingDown = false;

        countdown.countFrom(TIME_TO_GROW_TREE_ONE_STEP);
    }

    void setMap(GameMap map) {
        this.map = map;
    }

    public void stepTime() {
        if (isFallingDown) {
            if (countdown.hasReachedZero()) {
                map.removeTreeFromStepTime(this);

                map.placeDecoration(position, DecorationType.TREE_STUB);
            } else {
                countdown.step();
            }
        }

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

    public void setSize(TreeSize treeSize) {
        this.size = treeSize;
    }

    public void fallDown() {
        isFallingDown = true;

        countdown.countFrom(TIME_TO_FALL);

        map.reportFallingTree(this);
    }

    public boolean isFalling() {
        return isFallingDown;
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

    public enum TreeSize {
        NEWLY_PLANTED,
        SMALL,
        MEDIUM,
        FULL_GROWN
    }
}
