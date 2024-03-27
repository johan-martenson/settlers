/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model.buildings;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author johan
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface UpgradeCost {
    int planks() default 0;
    int stones() default 0;
}
