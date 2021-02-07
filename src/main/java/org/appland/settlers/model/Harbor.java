package org.appland.settlers.model;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.STORAGE_WORKER;
import static org.appland.settlers.model.Size.MEDIUM;

@HouseSize(size = MEDIUM, material = {PLANK, PLANK, PLANK, PLANK, STONE, STONE, STONE, STONE, STONE, STONE})
@RequiresWorker(workerType = STORAGE_WORKER)
public class Harbor extends Storehouse {

    public Harbor(Player player0) {
        super(player0);
    }
}
