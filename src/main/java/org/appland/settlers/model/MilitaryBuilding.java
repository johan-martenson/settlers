/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author johan
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MilitaryBuilding {

    int maxHostedMilitary();
    
    int defenceRadius();
    
    int maxCoins() default 0;
}
