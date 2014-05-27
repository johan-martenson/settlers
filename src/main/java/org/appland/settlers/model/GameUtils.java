/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author johan
 */
public class GameUtils {

    static Map createEmptyMaterialIntMap() {
        Map<Material, Integer> result = new HashMap<>();
        for (Material m : Material.values()) {
            result.put(m, 0);
        }
        return result;
    }

    static boolean isQueueEmpty(Map<Material, Integer> queue) {
        boolean isEmpty = true;
        
        for (Integer amount : queue.values()) {
            if (amount != 0) {
                isEmpty = false;
            }
        }
        
        return isEmpty;
    }
}
