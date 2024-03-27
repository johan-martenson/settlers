package org.appland.settlers.model.buildings;

import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Size.SMALL;

@Production(output = WOOD, requiredGoods = {})
@HouseSize(size = SMALL, material = {PLANK, PLANK})
@RequiresWorker(workerType = Material.WOODCUTTER_WORKER)
public class Woodcutter extends Building {

    public Woodcutter(Player player) {
        super(player);
    }
}
