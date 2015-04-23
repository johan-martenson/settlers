package org.appland.settlers.model;

import static org.appland.settlers.model.Material.CATAPULT_WORKER;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Size.MEDIUM;

@Production(output = {}, requiredGoods = {STONE})
@HouseSize(size = MEDIUM, material = {PLANCK, PLANCK,PLANCK, PLANCK, STONE, STONE, STONE})
@RequiresWorker(workerType = CATAPULT_WORKER)
public class Catapult extends Building {

    public Catapult(Player player0) {
        super(player0);
    }
}
