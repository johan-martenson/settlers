package org.appland.settlers.model;

import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.SAWMILL_WORKER;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Size.MEDIUM;

@Production(output = PLANCK, requiredGoods = {WOOD, WOOD, WOOD, WOOD, WOOD, WOOD})
@HouseSize(size = MEDIUM, material = {PLANCK, PLANCK, STONE, STONE})
@RequiresWorker(workerType = SAWMILL_WORKER)
public class Sawmill extends Building {

    public Sawmill(Player player0) {
        super(player0);
    }
}
