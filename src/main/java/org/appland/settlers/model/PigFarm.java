/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.PIG;
import static org.appland.settlers.model.Material.PIG_BREEDER;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Size.LARGE;

/**
 *
 * @author johan
 */
@HouseSize(size = LARGE)
@Production(output = PIG, requiredGoods = {WHEAT, WATER})
@RequiresWorker(workerType = PIG_BREEDER)
public class PigFarm extends Building {

    public PigFarm(Player player0) {
        super(player0);
    }
}
