package org.appland.settlers.model;

import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Size.SMALL;

@Production(output = WOOD, requiredGoods = {}, manualProduction = true)
@HouseSize(size = SMALL)
@RequiresWorker(workerType = Material.WOODCUTTER_WORKER)
public class Woodcutter extends Building {
}
