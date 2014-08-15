package org.appland.settlers.test;

import static org.appland.settlers.model.Building.ConstructionState.DONE;
import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.WOOD;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Headquarter;
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

    @Test(expected = InvalidMaterialException.class)
    public void testWrongMaterialToSawmill() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
        Sawmill sawmill = new Sawmill();

        Utils.constructMediumHouse(sawmill);

        assertTrue(DONE == sawmill.getConstructionState());

        sawmill.deliver(new Cargo(GOLD, null));
    }

    @Test(expected = DeliveryNotPossibleException.class)
    public void testDeliverMaterialToWoodcutter() throws DeliveryNotPossibleException, InvalidMaterialException, InvalidStateForProduction {
        Building woodcutter = new Woodcutter();

        Utils.constructSmallHouse(woodcutter);

        woodcutter.deliver(new Cargo(WOOD, null));
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

        quarry.deliver(new Cargo(BEER, null));
    }

    @Test(expected=Exception.class)
    public void testGetWorkerTypeForBuildingNotNeedingWorker() throws Exception {
        Headquarter hq = new Headquarter();

        hq.getWorkerType();
    }
}
