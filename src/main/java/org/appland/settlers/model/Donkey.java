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
@Walker(speed = 10)
public class Donkey extends Courier {

    public Donkey(GameMap map) {
        super(map);
    }
}
