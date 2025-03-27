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

    public StoneAmount getStoneAmount() {
        return switch(amount) {
            case 1 -> StoneAmount.MINI;
            case 2 -> StoneAmount.LITTLE;
            case 3 -> StoneAmount.LITTLE_MORE;
            case 4 -> StoneAmount.MIDDLE;
            case 5 -> StoneAmount.ALMOST_FULL;
            case 6 -> StoneAmount.FULL;
            default -> throw new InvalidGameLogicException("Invalid stone amount: " + amount);
        };
    }

    public enum StoneType {
        STONE_2,
        STONE_1
    }
}
