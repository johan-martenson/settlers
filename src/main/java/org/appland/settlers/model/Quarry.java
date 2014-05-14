package org.appland.settlers.model;

import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Size.*;

@Production(output=STONE, productionTime=100, requiredGoods = {})
@HouseSize(size=SMALL)
public class Quarry extends Building {

	public static Quarry createQuarry() {
		return new Quarry();
	}
}
