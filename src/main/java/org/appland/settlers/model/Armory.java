/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.ARMORER;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.IRON_BAR;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Size.MEDIUM;

/**
 *
 * @author johan
 */
@HouseSize(size = MEDIUM, material = {PLANK, PLANK, STONE, STONE})
@Production(requiredGoods = {IRON_BAR, IRON_BAR, COAL, COAL}, output = {SWORD, SHIELD})
@RequiresWorker(workerType = ARMORER)
public class Armory extends Building {

    public Armory(Player player0) {
        super(player0);
    }
}
