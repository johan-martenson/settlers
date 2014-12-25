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
public class Stone implements Piece {

    private int amount;
    private final Point position;
    
    public Stone(Point p) {
        amount = 10;
        position = p;
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

    @Override
    public Point getPosition() {
        return position;
    }
}
