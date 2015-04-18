/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.BAKER;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Size.MEDIUM;

/**
 *
 * @author johan
 */
@HouseSize(size = MEDIUM, material = {PLANCK, PLANCK, STONE, STONE})
@Production(requiredGoods = {WATER, FLOUR}, output = BREAD)
@RequiresWorker(workerType = BAKER)
public class Bakery extends Building {

    public Bakery(Player player0) {
        super(player0);
    }
}
