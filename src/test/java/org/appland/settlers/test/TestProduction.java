package org.appland.settlers.test;

import static org.appland.settlers.model.Building.ConstructionState.DONE;
import static org.appland.settlers.model.Building.ConstructionState.UNDER_CONSTRUCTION;
import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.InvalidLogicException;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidStateForProduction;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Woodcutter;

import org.junit.Before;
import org.junit.Test;

public class TestProduction {

	@Before
	public void setupTest() {
	}
	
	@Test
	public void testProducePlancks() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
		Sawmill sawmill = Sawmill.createSawmill();
		assertTrue(sawmill.getConstructionState() == UNDER_CONSTRUCTION);
		
		Utils.fastForward(1000, sawmill);

		assertTrue(sawmill.getConstructionState() == DONE);
		assertFalse(sawmill.outputAvailable());

		sawmill.deliver(Cargo.createCargo(WOOD));
		
		assertTrue(1 == sawmill.getQueue(WOOD));
		
		Utils.fastForward(100, sawmill);
		assertTrue(sawmill.outputAvailable());

		Cargo result = sawmill.retrieveCargo();
		
		assertNotNull(result);
		assertTrue(result.getMaterial() == PLANCK);
		assertTrue(0 == sawmill.getQueue(WOOD));
	}

	@Test(expected=InvalidMaterialException.class)
	public void testWrongMaterialToSawmill() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
		Sawmill sawmill = Sawmill.createSawmill();
		
		assertTrue(sawmill.getConstructionState() == UNDER_CONSTRUCTION);
		
		Utils.fastForward(1000, sawmill);
		
		assertTrue(DONE == sawmill.getConstructionState());
		
		sawmill.deliver(Cargo.createCargo(GOLD));
	}
	
	@Test
	public void testProduceWood() throws InvalidStateForProduction, InvalidLogicException {
		Building woodcutter = Woodcutter.createWoodcutter();
		
		Utils.fastForward(1000, woodcutter);
		
		assertTrue(woodcutter.getConstructionState() == DONE);
		assertFalse(woodcutter.cargoIsReady());

		Cargo result = woodcutter.retrieveCargo();
		
		assertNotNull(result);
		assertTrue(result.getMaterial() == WOOD);
	}
	
	@Test(expected=InvalidStateForProduction.class)
	public void testDeliverMaterialToWoodcutter() throws DeliveryNotPossibleException, InvalidMaterialException, InvalidStateForProduction {
		Building woodcutter = Woodcutter.createWoodcutter();
		
		woodcutter.deliver(Cargo.createCargo(WOOD));
	}
	
	@Test
	public void testProduceStone() throws InvalidStateForProduction {
		Quarry quarry = Quarry.createQuarry();
		Cargo result;
		
		Utils.fastForward(210, quarry);
		
		result= quarry.retrieveCargo();

		assertTrue(STONE == result.getMaterial());
	}
	
	@Test
	public void testRetrieveStoneDuringConstruction() throws InvalidStateForProduction {
		Quarry quarry = Quarry.createQuarry();
		
		quarry.stepTime();
		Cargo result = quarry.retrieveCargo();
		
		assertNull(result);
	}
	
	@Test(expected=InvalidStateForProduction.class)
	public void testDeliveryMaterialToQuarry() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
		Quarry quarry = Quarry.createQuarry();
		
		quarry.deliver(Cargo.createCargo(BEER));
	}
}
