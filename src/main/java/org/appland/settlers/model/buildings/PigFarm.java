/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model.buildings;

import org.appland.settlers.model.Player;
import org.appland.settlers.model.actors.Pig;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.actors.Pig.PigAge.ADULT;
import static org.appland.settlers.model.actors.Pig.PigAge.PIGLET;

/**
 *
 * @author johan
 */
@HouseSize(size = LARGE, material = {PLANK, PLANK, PLANK, STONE, STONE, STONE})
@Production(output = PIG, requiredGoods = {WHEAT, WHEAT, WHEAT, WHEAT, WHEAT, WHEAT, WATER, WATER, WATER, WATER, WATER, WATER})
@RequiresWorker(workerType = PIG_BREEDER)
public class PigFarm extends Building {

    private final List<Pig> pigs = new ArrayList<>();

    public PigFarm(Player player0) {
        super(player0);
    }

    public List<Pig> getPigs() {
        return pigs;
    }

    public void setNumberOfPigs(int numberOfPigs) {
        var current = this.pigs.size();
        var diff = numberOfPigs - current;

        if (diff > 0) {
            for (var i = 0; i < diff; i++) {
                var pig = indexToPig(current + i);
                pigs.add(pig);
            }
        } else if (diff < 0) {
            for (var i = 0; i < Math.abs(diff); i++) {
                pigs.removeLast();
            }
        }
    }

    private Pig indexToPig(int index) {
        var age = index == 0 ? ADULT : PIGLET;
        var slot = switch (index) {
            case 0: yield StyeSlot.SLOT_1;
            case 1: yield StyeSlot.SLOT_2;
            case 2: yield StyeSlot.SLOT_3;
            case 3: yield StyeSlot.SLOT_4;
            case 4: yield StyeSlot.SLOT_5;
            default: throw new RuntimeException("Invalid index: " + index);
        };

        return new Pig(age, slot);
    }

    public enum StyeSlot {
        SLOT_1,
        SLOT_2,
        SLOT_3,
        SLOT_4,
        SLOT_5,
    }
}
