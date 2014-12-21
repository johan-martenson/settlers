package org.appland.settlers.model;

import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Size.*;

@Production(output = STONE, requiredGoods = {})
@HouseSize(size = SMALL)
@RequiresWorker(workerType = Material.STONEMASON)
public class Quarry extends Building {

    public Quarry(Player player0) {
        super(player0);
    }

    @Override
    public String toString() {
        return "Quarry";
    }
}
