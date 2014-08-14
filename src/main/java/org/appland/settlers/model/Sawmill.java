package org.appland.settlers.model;

import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.SAWMILL_WORKER;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Size.MEDIUM;

@Production(output = PLANCK, requiredGoods = {WOOD}, productionTime = 100, manualProduction = true)
@HouseSize(size = MEDIUM)
@RequiresWorker(workerType = SAWMILL_WORKER)
public class Sawmill extends Building {
}
