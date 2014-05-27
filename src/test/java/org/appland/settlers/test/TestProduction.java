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
		
		Utils.constructMediumHouse(sawmill);

		assertTrue(sawmill.getConstructionState() == DONE);
		assertFalse(sawmill.isCargoReady());

		sawmill.deliver(Cargo.createCargo(WOOD));
		
		assertTrue(1 == sawmill.getMaterialInQueue(WOOD));
		
		Utils.fastForward(100, sawmill);
		assertTrue(sawmill.isCargoReady());

		Cargo result = sawmill.retrieveCargo();
		
		assertNotNull(result);
		assertTrue(result.getMaterial() == PLANCK);
		assertTrue(0 == sawmill.getMaterialInQueue(WOOD));
	}

	@Test(expected=InvalidMaterialException.class)
	public void testWrongMaterialToSawmill() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
		Sawmill sawmill = Sawmill.createSawmill();
		
		Utils.constructMediumHouse(sawmill);
		
		assertTrue(DONE == sawmill.getConstructionState());
		
		sawmill.deliver(Cargo.createCargo(GOLD));
	}
	
	@Test
	public void testProduceWood() throws InvalidStateForProduction, InvalidLogicException, InvalidMaterialException, DeliveryNotPossibleException {
		Building woodcutter = Woodcutter.createWoodcutter();
		
                Utils.constructSmallHouse(woodcutter);
                
		assertTrue(woodcutter.getConstructionState() == DONE);
                assertFalse(woodcutter.isCargoReady());
                
                Utils.fastForward(100, woodcutter);
		assertTrue(woodcutter.isCargoReady());

		Cargo result = woodcutter.retrieveCargo();
                assertFalse(woodcutter.isCargoReady());
                assertNotNull(result);
		assertTrue(result.getMaterial() == WOOD);
	}
	
	@Test(expected=DeliveryNotPossibleException.class)
	public void testDeliverMaterialToWoodcutter() throws DeliveryNotPossibleException, InvalidMaterialException, InvalidStateForProduction {
		Building woodcutter = Woodcutter.createWoodcutter();
		
                Utils.constructSmallHouse(woodcutter);
                
		woodcutter.deliver(Cargo.createCargo(WOOD));
	}
	
	@Test
	public void testProduceStone() throws InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException {
		Quarry quarry = Quarry.createQuarry();
		Cargo result;
		
		Utils.constructSmallHouse(quarry);
		assertFalse(quarry.isCargoReady());
                
                Utils.fastForward(100, quarry);
                assertTrue(quarry.isCargoReady());
                
		result= quarry.retrieveCargo();
                assertFalse(quarry.isCargoReady());
		assertTrue(STONE == result.getMaterial());
	}
	
	@Test
	public void testRetrieveStoneDuringConstruction() throws InvalidStateForProduction {
		Quarry quarry = Quarry.createQuarry();
		
		quarry.stepTime();
		Cargo result = quarry.retrieveCargo();
		
		assertNull(result);
	}
	
	@Test(expected=DeliveryNotPossibleException.class)
	public void testDeliveryMaterialToQuarry() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
		Quarry quarry = Quarry.createQuarry();
		
                Utils.constructSmallHouse(quarry);
                
		quarry.deliver(Cargo.createCargo(BEER));
	}
}
