package org.appland.settlers.model.buildings;

import org.appland.settlers.model.Player;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.CARPENTER;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Size.MEDIUM;

@Production(output = PLANK, requiredGoods = {WOOD, WOOD, WOOD, WOOD, WOOD, WOOD})
@HouseSize(size = MEDIUM, material = {PLANK, PLANK, STONE, STONE})
@RequiresWorker(workerType = CARPENTER)
public class Sawmill extends Building {

    public Sawmill(Player player0) {
        super(player0);
    }
}
