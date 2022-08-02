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
public class Stone {

    private final Point position;
    private final StoneType stoneType;

    private int amount;

    public Stone(Point point, StoneType stoneType, int amount) {
        this.amount = amount;
        this.position = point;
        this.stoneType = stoneType;
    }

    public void removeOnePart() {
        amount--;
    }

    boolean noMoreStone() {
        return amount == 0;
    }

    public int getAmount() {
        return amount;
    }

    public Point getPosition() {
        return position;
    }

    public String toString() {
        return "Stone (" + position.x + ", " + position.y + ")";
    }

    public StoneType getStoneType() {
        return stoneType;
    }
}
