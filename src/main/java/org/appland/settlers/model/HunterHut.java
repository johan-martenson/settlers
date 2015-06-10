/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.HUNTER;
import static org.appland.settlers.model.Material.PLANCK;

@HouseSize(size = Size.SMALL, material = {PLANCK, PLANCK})
@RequiresWorker(workerType = HUNTER)
public class HunterHut extends Building {

    public HunterHut(Player player0) {
        super(player0);
    }
}
