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
public class Sign {

    private final static int SIGN_EXPIRATION_TIME = 999;
    
    private final Material type;
    private final Size     size;
    private final Point    position;
    private final GameMap  map;

    private int age;
    
    Sign(Material m, Size s, Point p, GameMap mapToSet) {
        position = p;
        type     = m;
        size     = s;
        map      = mapToSet;
        age      = 0;
    }    

    public Material getType() {
        return type;
    }

    public Size getSize() {
        return size;
    }

    public Point getPosition() {
        return position;
    }

    public boolean isEmpty() {
        return type == null;
    }

    void stepTime() {
        age++;
        
        if (age > SIGN_EXPIRATION_TIME) {
            map.removeSignWithinStepTime(this);
        }
    }
}
