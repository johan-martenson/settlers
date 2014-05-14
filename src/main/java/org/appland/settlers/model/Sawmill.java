package org.appland.settlers.model;

import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Size.MEDIUM;

import java.util.logging.Logger;

@Production(output=PLANCK, requiredGoods={WOOD}, productionTime = 100)
@HouseSize(size=MEDIUM)
public class Sawmill extends Building {
	
	public static Sawmill createSawmill() {
		return new Sawmill();
	}
	
	public String toString() {
		return "Sawmill with " + buildingToString();
	}
}
