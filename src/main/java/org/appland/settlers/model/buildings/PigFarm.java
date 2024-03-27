/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model.buildings;

import org.appland.settlers.model.Player;

import static org.appland.settlers.model.Material.PIG;
import static org.appland.settlers.model.Material.PIG_BREEDER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Size.LARGE;

/**
 *
 * @author johan
 */
@HouseSize(size = LARGE, material = {PLANK, PLANK, PLANK, STONE, STONE, STONE})
@Production(output = PIG, requiredGoods = {WHEAT, WHEAT, WHEAT, WHEAT, WHEAT, WHEAT, WATER, WATER, WATER, WATER, WATER, WATER})
@RequiresWorker(workerType = PIG_BREEDER)
public class PigFarm extends Building {

    public PigFarm(Player player0) {
        super(player0);
    }
}
