package org.appland.settlers.model;

import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.IRON_BAR;
import static org.appland.settlers.model.Material.METALWORKER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Size.MEDIUM;

@HouseSize(size = MEDIUM, material = {PLANK, PLANK, STONE, STONE})
@Production(output = BEER, requiredGoods = {PLANK, IRON_BAR})
@RequiresWorker(workerType = METALWORKER)
public class Metalworks extends Building {
    public Metalworks(Player player0) {
        super(player0);
    }
}
