package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import static org.appland.settlers.model.Building.ConstructionState.BURNING;
import static org.appland.settlers.model.Building.ConstructionState.DESTROYED;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
import static org.appland.settlers.model.Building.ConstructionState.UNDER_CONSTRUCTION;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Farm;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidStateForProduction;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.WOOD;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Woodcutter;
import static org.junit.Assert.assertTrue;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConstructionTest {

	@Test
	public void testCreateNewWoodcutter() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
		Building wc = Woodcutter.createWoodcutter();
		
		assertTrue(wc.getConstructionState() == UNDER_CONSTRUCTION);
		
		/* Verify that construction doesn't finish before material is delivered */
                Utils.assertConstructionStateDuringFastForward(1000, wc, UNDER_CONSTRUCTION);
		
		Cargo planckCargo = Cargo.createCargo(PLANCK);
                Cargo stoneCargo = Cargo.createCargo(STONE);
		wc.deliver(planckCargo);
                wc.deliver(planckCargo);
                wc.deliver(stoneCargo);
                
                Utils.assertConstructionStateDuringFastForward(1000, wc, UNDER_CONSTRUCTION);
                
                /* Verify that construction can finish when all material is delivered */
                wc.deliver(stoneCargo);
                wc.stepTime();
                
		assertTrue(wc.getConstructionState() == DONE);
		
                /* Verify that all material was consumed by the construction */
                
                assertTrue(wc.getQueue(PLANCK) == 0);
                assertTrue(wc.getQueue(STONE) == 0);
                
		wc.tearDown();
		
		assertTrue(wc.getConstructionState() == BURNING);
		
                int i;
		for (i = 0; i < 50; i++) {
			assertTrue(wc.getConstructionState() == BURNING);
			wc.stepTime();
		}
		
		assertTrue(wc.getConstructionState() == DESTROYED);
	}
	
	@Test
	public void testCreateNewSawmill() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
		Sawmill sm = Sawmill.createSawmill();
		
		assertTrue(sm.getConstructionState() == UNDER_CONSTRUCTION);
		
		/* Verify that construction doesn't finish before material is delivered */
                Utils.assertConstructionStateDuringFastForward(1000, sm, UNDER_CONSTRUCTION);
		
		Cargo planckCargo = Cargo.createCargo(PLANCK);
                Cargo stoneCargo = Cargo.createCargo(STONE);
		sm.deliver(planckCargo);
                sm.deliver(planckCargo);
                sm.deliver(planckCargo);
                sm.deliver(planckCargo);
                sm.deliver(stoneCargo);
                sm.deliver(stoneCargo);
                
                Utils.assertConstructionStateDuringFastForward(1000, sm, UNDER_CONSTRUCTION);

                /* Verify that construction can finish when all material is delivered */
                sm.deliver(stoneCargo);
                sm.stepTime();
		
                assertTrue(sm.getConstructionState() == DONE);
                
                 /* Verify that all material was consumed by the construction */
                assertTrue(sm.getQueue(PLANCK) == 0);
                assertTrue(sm.getQueue(STONE) == 0);
                
		sm.tearDown();
		
		Utils.assertConstructionStateDuringFastForward(50, sm, BURNING);
		
		assertTrue(sm.getConstructionState() == DESTROYED);
	}

        @Test
        public void testCreateFarm() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
            Farm farm = Farm.createFarm();
            assertTrue(farm.getConstructionState() == UNDER_CONSTRUCTION);
            
            /* Verify that construction doesn't finish before material is delivered */
                Utils.assertConstructionStateDuringFastForward(1000, farm, UNDER_CONSTRUCTION);
		
		Cargo planckCargo = Cargo.createCargo(PLANCK);
                Cargo stoneCargo = Cargo.createCargo(STONE);
		farm.deliver(planckCargo);
                farm.deliver(planckCargo);
                farm.deliver(planckCargo);
                farm.deliver(planckCargo);
                farm.deliver(stoneCargo);
                farm.deliver(stoneCargo);
                farm.deliver(stoneCargo);
                
                Utils.assertConstructionStateDuringFastForward(1000, farm, UNDER_CONSTRUCTION);
            
                /* Verify that construction can finish when all material is delivered */
                farm.deliver(stoneCargo);
                farm.stepTime();
		
                assertTrue(farm.getConstructionState() == DONE);
                
                 /* Verify that all material was consumed by the construction */
                assertTrue(farm.getQueue(PLANCK) == 0);
                assertTrue(farm.getQueue(STONE) == 0);
                
		farm.tearDown();
		
		Utils.assertConstructionStateDuringFastForward(50, farm, BURNING);
		
		assertTrue(farm.getConstructionState() == DESTROYED);
        }
        
	@Test(expected=InvalidMaterialException.class)
	public void testInvalidDeliveryToUnfinishedSawmill() throws InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException {
		Sawmill sw = Sawmill.createSawmill();

		sw.deliver(Cargo.createCargo(SWORD));
	}

	@Test(expected=InvalidStateForProduction.class)
	public void testDeliveryToBurningSawmill() throws InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException {
		Sawmill sm = Sawmill.createSawmill();
		
                Utils.constructMediumHouse(sm);
		
		assertTrue(sm.getConstructionState() == DONE);
		
		sm.tearDown();
		
		sm.deliver(Cargo.createCargo(WOOD));
	}
	
	@Test (expected=InvalidStateForProduction.class)
	public void testDeliveryToDestroyedSawmill() throws InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException {
		Sawmill sm = Sawmill.createSawmill();
		
                Utils.constructMediumHouse(sm);
                
		assertTrue(sm.getConstructionState() == DONE);
		
		sm.tearDown();
		
		Utils.fastForward(1000, sm);
		
		sm.deliver(Cargo.createCargo(WOOD));
	}
	
	@Test
	public void testCreateHeadquarter() {
		Headquarter.createHeadquarter();
                
                // TODO: test creation of headquarter
	}
}
