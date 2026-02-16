/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model.buildings;

import org.appland.settlers.maps.Animal;
import org.appland.settlers.model.Player;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.LARGE;

/**
 *
 * @author johan
 */
@HouseSize(size = LARGE, material = {PLANK, PLANK, PLANK, STONE, STONE, STONE})
@Production(output = PIG, requiredGoods = {WHEAT, WHEAT, WHEAT, WHEAT, WHEAT, WHEAT, WATER, WATER, WATER, WATER, WATER, WATER})
@RequiresWorker(workerType = PIG_BREEDER)
public class PigFarm extends Building {

    private final List<Animal> pigs = new ArrayList<>();

    public PigFarm(Player player0) {
        super(player0);
    }

    public List<Animal> getPigs() {
        return pigs;
    }
}
