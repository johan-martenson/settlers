package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BorderChange {
    private final Player player;
    private final List<Point> newBorder;
    private final List<Point> removedBorder;
    private final List<Point> newOwnedLand;
    private final List<Point> removedOwnedLand;

    public BorderChange(
            Player player,
            Collection<Point> newBorder,
            Collection<Point> removedBorder,
            Collection<Point> newOwnedLand,
            Collection<Point> removedOwnedLand
    ) {
        this.player = player;
        this.newBorder = new ArrayList<>(newBorder);
        this.removedBorder = new ArrayList<>(removedBorder);
        this.newOwnedLand = new ArrayList<>(newOwnedLand);
        this.removedOwnedLand = new ArrayList<>(removedOwnedLand);
    }

    public Player player() {
        return player;
    }

    public List<Point> newBorder() {
        return newBorder;
    }

    public List<Point> removedBorder() {
        return removedBorder;
    }

    public List<Point> newOwnedLand() {
        return newOwnedLand;
    }

    public List<Point> removedOwnedLand() {
        return removedOwnedLand;
    }

    @Override
    public String toString() {
        return "Border change for " + player + ", added " + newBorder + ", removed " + removedBorder;
    }
}