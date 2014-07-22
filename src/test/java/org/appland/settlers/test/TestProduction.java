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
        Sawmill sawmill = new Sawmill();
        assertTrue(sawmill.getConstructionState() == UNDER_CONSTRUCTION);

        Utils.constructMediumHouse(sawmill);

        assertTrue(sawmill.getConstructionState() == DONE);
        assertFalse(sawmill.isCargoReady());

        sawmill.deliver(new Cargo(WOOD));

        assertTrue(1 == sawmill.getMaterialInQueue(WOOD));

        Utils.fastForward(100, sawmill);
        assertTrue(sawmill.isCargoReady());

        Cargo result = sawmill.retrieveCargo();

        assertNotNull(result);
        assertTrue(result.getMaterial() == PLANCK);
        assertTrue(0 == sawmill.getMaterialInQueue(WOOD));
    }

    @Test(expected = InvalidMaterialException.class)
    public void testWrongMaterialToSawmill() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
        Sawmill sawmill = new Sawmill();

        Utils.constructMediumHouse(sawmill);

        assertTrue(DONE == sawmill.getConstructionState());

        sawmill.deliver(new Cargo(GOLD));
    }

    @Test(expected = DeliveryNotPossibleException.class)
    public void testDeliverMaterialToWoodcutter() throws DeliveryNotPossibleException, InvalidMaterialException, InvalidStateForProduction {
        Building woodcutter = new Woodcutter();

        Utils.constructSmallHouse(woodcutter);

        woodcutter.deliver(new Cargo(WOOD));
    }

    @Test
    public void testRetrieveStoneDuringConstruction() throws InvalidStateForProduction {
        Quarry quarry = new Quarry();

        quarry.stepTime();
        Cargo result = quarry.retrieveCargo();

        assertNull(result);
    }

    @Test(expected = DeliveryNotPossibleException.class)
    public void testDeliveryMaterialToQuarry() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
        Quarry quarry = new Quarry();

        Utils.constructSmallHouse(quarry);

        quarry.deliver(new Cargo(BEER));
    }

    @Test(expected=Exception.class)
    public void testGetWorkerTypeForBuildingNotNeedingWorker() throws Exception {
        Sawmill sm = new Sawmill();

        Utils.constructMediumHouse(sm);

        sm.getWorkerType();
    }

    @Test
    public void testSawmillNotNeedsWorker() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
        Sawmill sm = new Sawmill();

        Utils.constructMediumHouse(sm);

        assertFalse(sm.needsWorker());
    }
}
