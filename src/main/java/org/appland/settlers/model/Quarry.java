package org.appland.settlers.model;

import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Size.*;

@Production(output = STONE, requiredGoods = {}, manualProduction = true)
@HouseSize(size = SMALL)
@RequiresWorker(workerType = Material.STONEMASON)
public class Quarry extends Building {

    @Override
    public String toString() {
        return "Quarry";
    }
}
