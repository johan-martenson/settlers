/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.MILLER;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Size.MEDIUM;

/**
 *
 * @author johan
 */
@HouseSize(size = MEDIUM, material = {PLANCK, PLANCK, STONE, STONE})
@Production(requiredGoods = {WHEAT}, output = FLOUR)
@RequiresWorker(workerType = MILLER)
public class Mill extends Building {

    public Mill(Player player0) {
        super(player0);
    }
}
