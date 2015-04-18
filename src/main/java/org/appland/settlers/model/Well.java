/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WELL_WORKER;
import static org.appland.settlers.model.Size.SMALL;

/**
 *
 * @author johan
 */
@HouseSize(size = SMALL, material = {PLANCK, PLANCK})
@Production(requiredGoods = {}, output = WATER)
@RequiresWorker(workerType = WELL_WORKER)
public class Well extends Building {

    public Well(Player player0) {
        super(player0);
    }
}
