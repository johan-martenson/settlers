package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.WOOD;
import static org.junit.Assert.assertTrue;

public class TestProduction {

    @Test(expected = InvalidMaterialException.class)
    public void testWrongMaterialToSawmill() throws Exception {
        Sawmill sawmill = new Sawmill(null);

        Utils.constructHouse(sawmill, null);

        assertTrue(sawmill.ready());

        sawmill.putCargo(new Cargo(GOLD, null));
    }

    @Test(expected = DeliveryNotPossibleException.class)
    public void testDeliverMaterialToWoodcutter() throws Exception {
        Building woodcutter = new Woodcutter(null);

        Utils.constructHouse(woodcutter, null);

        woodcutter.putCargo(new Cargo(WOOD, null));
    }

    @Test(expected = DeliveryNotPossibleException.class)
    public void testDeliveryMaterialToQuarry() throws Exception {
        Quarry quarry = new Quarry(null);

        Utils.constructHouse(quarry, null);

        quarry.putCargo(new Cargo(BEER, null));
    }

    @Test(expected=Exception.class)
    public void testGetWorkerTypeForBuildingNotNeedingWorker() throws Exception {
        Headquarter headquarter = new Headquarter(null);

        headquarter.getWorkerType();
    }
}
