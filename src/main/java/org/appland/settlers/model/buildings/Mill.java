/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model.buildings;

import org.appland.settlers.model.Player;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.MEDIUM;

/**
 *
 * @author johan
 */
@HouseSize(size = MEDIUM, material = {PLANK, PLANK, STONE, STONE})
@Production(requiredGoods = {WHEAT, WHEAT, WHEAT, WHEAT, WHEAT, WHEAT}, output = FLOUR)
@RequiresWorker(workerType = HELPER)
public class Mill extends Building {

    public Mill(Player player0) {
        super(player0);
    }
}
