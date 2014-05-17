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

import static org.appland.settlers.model.Building.ConstructionState.*;
import static org.appland.settlers.model.Building.ConstructionState;

import static org.appland.settlers.model.Material.*;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidStateForProduction;
import org.appland.settlers.model.Sawmill;

import static org.junit.Assert.assertTrue;

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

    public static void assertConstructionStateDuringFastForward(int time, Building b, ConstructionState state) {
        int i = 0;
        for (i = 0; i < time; i++) {
            assertTrue(b.getConstructionState() == state);
            b.stepTime();
        }
    }
    
	public static List<Building> getNeedForMaterial(Material wood, List<Building> buildings) {
		log.log(Level.INFO, "Finding buildings requiring {0} in list {1}", new Object[] {wood, buildings});
		
		List<Building> result = new ArrayList<>();
		
		for (Building b : buildings) {
			if (b.needsMaterial(wood)) {
				log.log(Level.FINE, "Building {0} requires {1}", new Object[] {b, wood});
				result.add(b);
			}
		}
		
		return result;
	}

    static void constructMediumHouse(Building sm) throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
        Cargo woodCargo = Cargo.createCargo(PLANCK);
        Cargo stoneCargo = Cargo.createCargo(STONE);
        
        /* Deliver 4 wood and 3 stone */
        sm.deliver(woodCargo);
        sm.deliver(woodCargo);
        sm.deliver(woodCargo);
        sm.deliver(woodCargo);
        
        sm.deliver(stoneCargo);
        sm.deliver(stoneCargo);
        sm.deliver(stoneCargo);
        
        Utils.fastForward(150, sm);
    }

    static void constructSmallHouse(Building wc) throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
        Cargo woodCargo = Cargo.createCargo(PLANCK);
        Cargo stoneCargo = Cargo.createCargo(STONE);
        
        /* Deliver 4 wood and 3 stone */
        wc.deliver(woodCargo);
        wc.deliver(woodCargo);
        
        wc.deliver(stoneCargo);
        wc.deliver(stoneCargo);
        
        Utils.fastForward(100, wc);
    }
}
