/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.DONKEY;
import static org.appland.settlers.model.Material.DONKEY_BREEDER;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Size.LARGE;

/**
 *
 * @author johan
 */
@HouseSize(size = LARGE, material = {PLANCK, PLANCK, PLANCK, STONE, STONE, STONE})
@Production(output = DONKEY, requiredGoods = {WHEAT, WATER})
@RequiresWorker(workerType = DONKEY_BREEDER)
public class DonkeyFarm extends Building{

    public DonkeyFarm(Player player0) {
        super(player0);
    }
}
