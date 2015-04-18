/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.FISHERMAN;
import static org.appland.settlers.model.Material.PLANCK;

/**
 *
 * @author johan
 */
@HouseSize(size = Size.SMALL, material = {PLANCK, PLANCK})
@RequiresWorker(workerType = FISHERMAN)
public class Fishery extends Building {

    public Fishery(Player player0) {
        super(player0);
    }
}
