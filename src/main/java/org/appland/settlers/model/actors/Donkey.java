/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model.actors;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.actors.Walker;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Donkey extends Courier {

    public Donkey(Player player, GameMap map) {
        super(player, map);

        shouldDoSpecialActions = false;
    }
}
