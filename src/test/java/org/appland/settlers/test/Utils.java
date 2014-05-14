package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;

import org.appland.settlers.model.Actor;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Worker;

public class Utils {
	
	private static Logger log = Logger.getLogger(Worker.class.getName());
	
	public static void fastForward(int time, Actor b) {
		int i = 0;
		for (i = 0; i < time; i++) {
			b.stepTime();
		}
	}
	
	public static void fastForward(int time, Actor... b) {
	    fastForward(time, Arrays.asList(b));
	}

    public static void fastForward(int time, List<Actor> actors) {
	for (Actor a : actors) {
	    fastForward(time, a);
	}
    }

	public static List<Building> getNeedForMaterial(Material wood, List<Building> buildings) {
		log.log(Level.INFO, "Finding buildings requiring {0} in list {1}", new Object[] {wood, buildings});
		
		List<Building> result = new ArrayList<Building>();
		
		for (Building b : buildings) {
			if (b.needsMaterial(wood)) {
				log.log(Level.FINE, "Building {0} requires {1}", new Object[] {b, wood});
				result.add(b);
			}
		}
		
		return result;
	}
}
