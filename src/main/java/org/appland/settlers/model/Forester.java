/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

@Walker(speed = 10)
public class Forester extends Worker {

    public Forester() {
        this(null);
    }
    
    public Forester(GameMap map) {
        super(map);
    }
}
