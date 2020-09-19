package org.appland.settlers.model;

import static org.appland.settlers.model.Material.CATAPULT_WORKER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Size.MEDIUM;

@Production(output = {}, requiredGoods = {STONE, STONE, STONE, STONE})
@HouseSize(size = MEDIUM, material = {PLANK, PLANK, PLANK, PLANK, STONE, STONE})
@RequiresWorker(workerType = CATAPULT_WORKER)
public class Catapult extends Building {

    public Catapult(Player player0) {
        super(player0);
    }
}
