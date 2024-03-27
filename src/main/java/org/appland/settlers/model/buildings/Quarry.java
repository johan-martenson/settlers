package org.appland.settlers.model.buildings;

import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Size.SMALL;

@Production(output = STONE, requiredGoods = {})
@HouseSize(size = SMALL, material = {PLANK, PLANK})
@RequiresWorker(workerType = Material.STONEMASON)
public class Quarry extends Building {

    public Quarry(Player player0) {
        super(player0);
    }
}
