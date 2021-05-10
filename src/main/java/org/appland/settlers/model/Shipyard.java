/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.BOAT;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.SHIPWRIGHT;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Size.LARGE;

/**
 *
 * @author johan
 */
@HouseSize(size = LARGE, material = {PLANK, PLANK, PLANK, STONE, STONE, STONE})
@Production(output = BOAT, requiredGoods = {PLANK, PLANK, PLANK, PLANK})
@RequiresWorker(workerType = SHIPWRIGHT)
public class Shipyard extends Building {

    private boolean produceShips;

    public Shipyard(Player player0) {
        super(player0);
    }

    public void produceShips() {
        produceShips = true;
    }

    public boolean isProducingShips() {
        return produceShips;
    }

    public boolean isProducingBoats() {
        return !produceShips;
    }
}
