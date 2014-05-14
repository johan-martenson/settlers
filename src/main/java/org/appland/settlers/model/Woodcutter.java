package org.appland.settlers.model;

import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Size.SMALL;

@Production(output=WOOD, requiredGoods={}, productionTime=100)
@HouseSize(size=SMALL)
public class Woodcutter extends Building {

	public static Building createWoodcutter() {
		return new Woodcutter();
	}
}
