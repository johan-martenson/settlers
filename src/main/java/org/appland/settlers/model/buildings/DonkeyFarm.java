/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model.buildings;

import org.appland.settlers.model.Player;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.LARGE;

/**
 *
 * @author johan
 */
@HouseSize(size = LARGE, material = {PLANK, PLANK, PLANK, STONE, STONE, STONE})
@Production(output = DONKEY, requiredGoods = {WHEAT, WHEAT, WHEAT, WHEAT, WHEAT, WHEAT, WATER, WATER, WATER, WATER, WATER, WATER})
@RequiresWorker(workerType = HELPER)
public class DonkeyFarm extends Building {

    public DonkeyFarm(Player player0) {
        super(player0);
    }
}
