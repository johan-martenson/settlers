package org.appland.settlers.model.buildings;

import org.appland.settlers.model.Player;

import static org.appland.settlers.model.Material.AXE;
import static org.appland.settlers.model.Material.BOW;
import static org.appland.settlers.model.Material.CLEAVER;
import static org.appland.settlers.model.Material.CRUCIBLE;
import static org.appland.settlers.model.Material.FISHING_ROD;
import static org.appland.settlers.model.Material.IRON_BAR;
import static org.appland.settlers.model.Material.METALWORKER;
import static org.appland.settlers.model.Material.PICK_AXE;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.ROLLING_PIN;
import static org.appland.settlers.model.Material.SAW;
import static org.appland.settlers.model.Material.SCYTHE;
import static org.appland.settlers.model.Material.SHOVEL;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.TONGS;
import static org.appland.settlers.model.Size.MEDIUM;

@HouseSize(size = MEDIUM, material = {PLANK, PLANK, STONE, STONE})
@Production(output = {
        AXE,
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
