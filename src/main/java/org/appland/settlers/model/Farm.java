/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.FARMER;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Size.LARGE;

/**
 *
 * @author johan
 */
@HouseSize(size = LARGE, material = {PLANCK, PLANCK, PLANCK, STONE, STONE, STONE})
@Production(output = WHEAT, requiredGoods = {})
@RequiresWorker(workerType = FARMER)
public class Farm extends Building {

    public Farm(Player player0) {
        super(player0);
    }
}
