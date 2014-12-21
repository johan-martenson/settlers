/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.MINER;
import static org.appland.settlers.model.Size.SMALL;

/**
 *
 * @author johan
 */
@HouseSize(size = SMALL)
@RequiresWorker(workerType = MINER)
@Production(output = IRON, requiredGoods = {BREAD, FISH})
public class IronMine extends Building {

    public IronMine(Player player0) {
        super(player0);
    }
}
