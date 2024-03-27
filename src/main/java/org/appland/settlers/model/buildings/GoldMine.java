/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model.buildings;

import org.appland.settlers.model.Player;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.SMALL;

/**
 *
 * @author johan
 */
@HouseSize(size = SMALL, material = {PLANK, PLANK, PLANK, PLANK})
@RequiresWorker(workerType = MINER)
@Production(output = GOLD, requiredGoods = {BREAD, BREAD, FISH, FISH, MEAT, MEAT})
public class GoldMine extends Building {

    public GoldMine(Player player0) {
        super(player0);
    }

    @Override
    public boolean isMine() {
        return true;
    }
}
