package org.appland.settlers.test;

import java.util.Map;
import org.appland.settlers.model.Barracks;
import static org.appland.settlers.model.Building.ConstructionState.BURNING;
import static org.appland.settlers.model.Building.ConstructionState.DESTROYED;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
import static org.appland.settlers.model.Building.ConstructionState.UNDER_CONSTRUCTION;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Farm;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidStateForProduction;
import org.appland.settlers.model.Material;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.WOOD;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Woodcutter;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConstructionTest {

    @Test
    public void testCreateNewWoodcutter() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        Woodcutter wc = new Woodcutter();

        assertFalse(wc.isMilitaryBuilding());

        assertTrue(Utils.materialIntMapIsEmpty(wc.getInQueue()));
        assertTrue(wc.getConstructionState() == UNDER_CONSTRUCTION);
        assertFalse(wc.isCargoReady());

        assertTrue(wc.needsMaterial(PLANCK));
        assertTrue(wc.needsMaterial(STONE));

        assertFalse(wc.needsMaterial(SERGEANT));

        assertTrue(wc.needsMaterial(PLANCK));
        assertTrue(wc.needsMaterial(STONE));

        wc.promiseDelivery(PLANCK);
        assertTrue(wc.needsMaterial(PLANCK));
        assertTrue(wc.needsMaterial(STONE));

        wc.promiseDelivery(PLANCK);
        assertFalse(wc.needsMaterial(PLANCK));
        assertTrue(wc.needsMaterial(STONE));

        wc.promiseDelivery(STONE);
        assertFalse(wc.needsMaterial(PLANCK));
        assertTrue(wc.needsMaterial(STONE));

        wc.promiseDelivery(STONE);
        assertFalse(wc.needsMaterial(PLANCK));
        assertFalse(wc.needsMaterial(STONE));

        /* Verify that construction doesn't finish before material is delivered */
        Utils.assertConstructionStateDuringFastForward(1000, wc, UNDER_CONSTRUCTION);

        Cargo planckCargo = new Cargo(PLANCK, null);
        Cargo stoneCargo = new Cargo(STONE, null);
        wc.deliver(planckCargo);
        wc.deliver(planckCargo);
        wc.deliver(stoneCargo);

        Utils.assertConstructionStateDuringFastForward(1000, wc, UNDER_CONSTRUCTION);

        /* Verify that construction can finish when all material is delivered */
        wc.deliver(stoneCargo);
        wc.stepTime();

        assertTrue(wc.getConstructionState() == DONE);

        assertFalse(wc.isMilitaryBuilding());

        /* Verify that all material was consumed by the construction */
        assertTrue(wc.getMaterialInQueue(PLANCK) == 0);
        assertTrue(wc.getMaterialInQueue(STONE) == 0);

        /* Verify that the woodcutter doesn't need any material when it's finished */
        for (Material m : Material.values()) {
            assertFalse(wc.needsMaterial(m));
        }

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
    public void testCreateNewBarracks() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        Barracks brks = new Barracks();

        assertTrue(brks.getConstructionState() == UNDER_CONSTRUCTION);

        assertTrue(brks.isMilitaryBuilding());
        assertTrue(brks.getMaxHostedMilitary() == 2);
        assertTrue(brks.getHostedMilitary() == 0);
        assertTrue(brks.getPromisedMilitary() == 0);

        assertFalse(brks.needMilitaryManning());

        /* brks needs a reference to the game map and this is set implicityly when it's placed on the map */
        GameMap map = new GameMap(30, 30);
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Point point1 = new Point(13, 13);
        map.placeBuilding(brks, point1);
        
        Utils.constructSmallHouse(brks);

        assertTrue(brks.isMilitaryBuilding());
        assertTrue(brks.getConstructionState() == DONE);
        assertTrue(brks.getHostedMilitary() == 0);
        assertTrue(brks.getMaxHostedMilitary() == 2);
        assertTrue(brks.needMilitaryManning());

        assertTrue(brks.isMilitaryBuilding());
    }

    @Test
    public void testCreateNewSawmill() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        Sawmill sm = new Sawmill();

        assertTrue(sm.getConstructionState() == UNDER_CONSTRUCTION);

        /* Verify that construction doesn't finish before material is delivered */
        Utils.assertConstructionStateDuringFastForward(1000, sm, UNDER_CONSTRUCTION);

        Cargo planckCargo = new Cargo(PLANCK, null);
        Cargo stoneCargo = new Cargo(STONE, null);
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
        assertTrue(sm.getMaterialInQueue(PLANCK) == 0);
        assertTrue(sm.getMaterialInQueue(STONE) == 0);

        /* Verify that the sawmill needs only WOOD when it's finished */
        assertTrue(sm.needsMaterial(WOOD));
        assertFalse(sm.needsMaterial(PLANCK));
        assertFalse(sm.needsMaterial(STONE));

        sm.tearDown();

        Utils.assertConstructionStateDuringFastForward(50, sm, BURNING);

        assertTrue(sm.getConstructionState() == DESTROYED);
    }

    @Test
    public void testCreateFarm() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        Farm farm = new Farm();
        assertTrue(farm.getConstructionState() == UNDER_CONSTRUCTION);

        /* Verify that construction doesn't finish before material is delivered */
        Utils.assertConstructionStateDuringFastForward(1000, farm, UNDER_CONSTRUCTION);

        Cargo planckCargo = new Cargo(PLANCK, null);
        Cargo stoneCargo = new Cargo(STONE, null);
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
        assertTrue(farm.getMaterialInQueue(PLANCK) == 0);
        assertTrue(farm.getMaterialInQueue(STONE) == 0);

        farm.tearDown();

        Utils.assertConstructionStateDuringFastForward(50, farm, BURNING);

        assertTrue(farm.getConstructionState() == DESTROYED);
    }

    @Test(expected = InvalidMaterialException.class)
    public void testInvalidDeliveryToUnfinishedSawmill() throws InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException {
        Sawmill sw = new Sawmill();

        sw.deliver(new Cargo(SWORD, null));
    }

    @Test(expected = InvalidStateForProduction.class)
    public void testDeliveryToBurningSawmill() throws InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException, Exception {
        Sawmill sm = new Sawmill();

        Utils.constructMediumHouse(sm);

        assertTrue(sm.getConstructionState() == DONE);

        sm.tearDown();

        sm.deliver(new Cargo(WOOD, null));
    }

    @Test(expected = InvalidStateForProduction.class)
    public void testDeliveryToDestroyedSawmill() throws InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException, Exception {
        Sawmill sm = new Sawmill();

        Utils.constructMediumHouse(sm);

        assertTrue(sm.getConstructionState() == DONE);

        sm.tearDown();

        Utils.fastForward(1000, sm);

        sm.deliver(new Cargo(WOOD, null));
    }

    private boolean matchesRequiredMaterialForSmallHouse(Map<Material, Integer> requiredMaterialToFinish) {
        boolean matches = true;

        for (Material m : Material.values()) {
            if (m == PLANCK && requiredMaterialToFinish.get(m) != 2) {
                matches = false;
            } else if (m == STONE && requiredMaterialToFinish.get(m) != 2) {
                matches = false;
            } else if (requiredMaterialToFinish.get(m) != 0) {
                matches = false;
            }
        }

        return matches;
    }
}
