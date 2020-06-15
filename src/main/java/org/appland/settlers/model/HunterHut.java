/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.HUNTER;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.PLANK;

@HouseSize(size = Size.SMALL, material = {PLANK, PLANK})
@RequiresWorker(workerType = HUNTER)
@Production(requiredGoods = {}, output = MEAT)
public class HunterHut extends Building {

    public HunterHut(Player player0) {
        super(player0);
    }
}
