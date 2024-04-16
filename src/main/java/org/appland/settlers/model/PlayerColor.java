package org.appland.settlers.model;

public enum PlayerColor {
    BLUE(0),
    YELLOW(1),
    RED(2),
    PURPLE(3),
    GRAY(4),
    GREEN(5),
    BROWN(6),
    WHITE(7);

    public final int index;

    PlayerColor(int index) {
        this.index = index;
    }
}
