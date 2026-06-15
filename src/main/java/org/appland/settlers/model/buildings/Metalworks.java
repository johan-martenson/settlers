package org.appland.settlers.model.buildings;

import org.appland.settlers.model.Player;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.MEDIUM;

@HouseSize(size = MEDIUM, material = {PLANK, PLANK, STONE, STONE})
@Production(output = {
        AXE,
        HAMMER,
        SHOVEL,
        PICK_AXE,
        FISHING_ROD,
        BOW,
        SAW,
        CLEAVER,
        ROLLING_PIN,
        CRUCIBLE,
        TONGS,
        SCYTHE
}, requiredGoods = {PLANK, PLANK, PLANK, PLANK, PLANK, PLANK, IRON_BAR, IRON_BAR, IRON_BAR, IRON_BAR, IRON_BAR, IRON_BAR})
@RequiresWorker(workerType = METALWORKER)
public class Metalworks extends Building {
    public Metalworks(Player player0) {
        super(player0);
    }
}
