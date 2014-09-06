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

    private final Material type;
    private final Size     size;
    
    Sign(Material m, Size s) {
        type = m;
        size = s;
    }    

    public Material getType() {
        return type;
    }

    public Size getSize() {
        return size;
    }
}
