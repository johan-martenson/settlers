package org.appland.settlers.model;

import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Size.SMALL;

@Production(output = WOOD, requiredGoods = {})
@HouseSize(size = SMALL, material = {PLANCK, PLANCK})
@RequiresWorker(workerType = Material.WOODCUTTER_WORKER)
public class Woodcutter extends Building {

    public Woodcutter(Player p) {
        super(p);
    }
}
