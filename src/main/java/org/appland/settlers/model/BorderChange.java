package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.List;

public class BorderChange {
    private final Player player;
    private final List<Point> newBorder;
    private final List<Point> removedBorder;

    public BorderChange(Player player, List<Point> newBorder, List<Point> removedBorder) {
        this.player = player;
        this.newBorder = new ArrayList<>(newBorder);
        this.removedBorder = new ArrayList<>(removedBorder);
    }

    public Player getPlayer() {
        return player;
    }

    public List<Point> getNewBorder() {
        return newBorder;
    }

    public List<Point> getRemovedBorder() {
        return removedBorder;
    }

    @Override
    public String toString() {
        return "BorderChange{" +
                "player=" + player +
                ", newBorder=" + newBorder +
                ", removedBorder=" + removedBorder +
                '}';
    }
}