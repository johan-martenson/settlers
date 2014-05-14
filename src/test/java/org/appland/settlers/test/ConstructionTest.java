package org.appland.settlers.test;

import static org.appland.settlers.model.Building.ConstructionState.BURNING;
import static org.appland.settlers.model.Building.ConstructionState.DESTROYED;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
import static org.appland.settlers.model.Building.ConstructionState.UNDER_CONSTRUCTION;
import static org.appland.settlers.model.Material.WOOD;
import static org.junit.Assert.assertTrue;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidStateForProduction;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

public class ConstructionTest {

	@Test
	public void testCreateNewWoodcutter() {
		Building wc = Woodcutter.createWoodcutter();
		
		assertTrue(wc.getConstructionState() == UNDER_CONSTRUCTION);
		
		int i = 0;
		for (i = 0; i < 100; i++) {
			assertTrue(wc.getConstructionState() == UNDER_CONSTRUCTION);
			wc.stepTime();
		}
		
		assertTrue(wc.getConstructionState() == DONE);
		
		wc.tearDown();
		
		assertTrue(wc.getConstructionState() == BURNING);
		
		for (i = 0; i < 50; i++) {
			assertTrue(wc.getConstructionState() == BURNING);
			wc.stepTime();
		}
		
		assertTrue(wc.getConstructionState() == DESTROYED);
	}
	
	@Test
	public void testCreateNewSawmill() {
		Sawmill sm = Sawmill.createSawmill();
		
		assertTrue(sm.getConstructionState() == UNDER_CONSTRUCTION);
		
		int i = 0;
		for (i = 0; i < 150; i++) {
			assertTrue(sm.getConstructionState() == UNDER_CONSTRUCTION);
			sm.stepTime();
		}
		
		assertTrue(sm.getConstructionState() == DONE);
		
		sm.tearDown();
		
		assertTrue(sm.getConstructionState() == BURNING);
		
		for (i = 0; i < 50; i++) {
			assertTrue(sm.getConstructionState() == BURNING);
			sm.stepTime();
		}
		
		assertTrue(sm.getConstructionState() == DESTROYED);
	}

	@Test(expected=InvalidStateForProduction.class)
	public void testDeliveryToUnfinishedSawmill() throws InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException {
		Sawmill sw = Sawmill.createSawmill();

		sw.deliver(Cargo.createCargo(WOOD));
	}

	@Test(expected=InvalidStateForProduction.class)
	public void testDeliveryToBurningSawmill() throws InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException {
		Sawmill sm = Sawmill.createSawmill();
		
		Utils.fastForward(1000, sm);
		
		assertTrue(sm.getConstructionState() == DONE);
		
		sm.tearDown();
		
		sm.deliver(Cargo.createCargo(WOOD));
	}
	
	@Test (expected=InvalidStateForProduction.class)
	public void testDeliveryToDestroyedSawmill() throws InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException {
		Sawmill sm = Sawmill.createSawmill();
		
		Utils.fastForward(1000, sm);
		
		assertTrue(sm.getConstructionState() == DONE);
		
		sm.tearDown();
		
		Utils.fastForward(1000, sm);
		
		sm.deliver(Cargo.createCargo(WOOD));
	}
	
	@Test
	public void testCreateHeadquarter() {
		Headquarter.createHeadquarter();
	}
}
