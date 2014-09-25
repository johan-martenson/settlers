package org.appland.settlers.test;

import java.util.Map;
import org.appland.settlers.model.Barracks;
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

        assertTrue(wc.getAmount(WOOD) == 0);
        assertTrue(wc.getAmount(PLANCK) == 0);
        assertTrue(wc.underConstruction());

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
        int i;
        for (i = 0; i < 1000; i++) {
            assertTrue(wc.underConstruction());
            wc.stepTime();
        }

        Cargo planckCargo = new Cargo(PLANCK, null);
        Cargo stoneCargo = new Cargo(STONE, null);
        wc.putCargo(planckCargo);
        wc.putCargo(planckCargo);
        wc.putCargo(stoneCargo);

        for (i = 0; i < 1000; i++) {
            assertTrue(wc.underConstruction());
            wc.stepTime();
        }

        /* Verify that construction can finish when all material is delivered */
        wc.putCargo(stoneCargo);
        wc.stepTime();

        assertTrue(wc.ready());

        assertFalse(wc.isMilitaryBuilding());

        /* Verify that all material was consumed by the construction */
        assertTrue(wc.getAmount(PLANCK) == 0);
        assertTrue(wc.getAmount(STONE) == 0);

        /* Verify that the woodcutter doesn't need any material when it's finished */
        for (Material m : Material.values()) {
            assertFalse(wc.needsMaterial(m));
        }

        wc.tearDown();

        assertTrue(wc.burningDown());

        for (i = 0; i < 50; i++) {
            assertTrue(wc.burningDown());
            wc.stepTime();
        }

        assertTrue(wc.destroyed());
    }

    @Test
    public void testCreateNewBarracks() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        Barracks brks = new Barracks();

        assertTrue(brks.underConstruction());

        assertTrue(brks.isMilitaryBuilding());
        assertTrue(brks.getMaxHostedMilitary() == 2);
        assertTrue(brks.getHostedMilitary() == 0);
        assertTrue(brks.getPromisedMilitary() == 0);

        assertFalse(brks.needsMilitaryManning());

        /* brks needs a reference to the game map and this is set implicityly when it's placed on the map */
        GameMap map = new GameMap(30, 30);
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Point point1 = new Point(13, 13);
        map.placeBuilding(brks, point1);
        
        Utils.constructHouse(brks, map);

        assertTrue(brks.isMilitaryBuilding());
        assertTrue(brks.ready());
        assertTrue(brks.getHostedMilitary() == 0);
        assertTrue(brks.getMaxHostedMilitary() == 2);
        assertTrue(brks.needsMilitaryManning());

        assertTrue(brks.isMilitaryBuilding());
    }

    @Test
    public void testCreateNewSawmill() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        Sawmill sm = new Sawmill();

        assertTrue(sm.underConstruction());

        /* Verify that construction doesn't finish before material is delivered */
        for (int i = 0; i < 1000; i++) {
            assertTrue(sm.underConstruction());
            sm.stepTime();
        }

        Cargo planckCargo = new Cargo(PLANCK, null);
        Cargo stoneCargo = new Cargo(STONE, null);
        sm.putCargo(planckCargo);
        sm.putCargo(planckCargo);
        sm.putCargo(planckCargo);
        sm.putCargo(planckCargo);
        sm.putCargo(stoneCargo);
        sm.putCargo(stoneCargo);

        for (int i = 0; i < 1000; i++) {
            assertTrue(sm.underConstruction());
            sm.stepTime();
        }

        /* Verify that construction can finish when all material is delivered */
        sm.putCargo(stoneCargo);
        sm.stepTime();

        assertTrue(sm.ready());

        /* Verify that all material was consumed by the construction */
        assertTrue(sm.getAmount(PLANCK) == 0);
        assertTrue(sm.getAmount(STONE) == 0);

        /* Verify that the sawmill needs only WOOD when it's finished */
        assertTrue(sm.needsMaterial(WOOD));
        assertFalse(sm.needsMaterial(PLANCK));
        assertFalse(sm.needsMaterial(STONE));

        sm.tearDown();

        for (int i = 0; i < 50; i++) {
            assertTrue(sm.burningDown());
            sm.stepTime();
        }


        assertTrue(sm.destroyed());
    }

    @Test
    public void testCreateFarm() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        Farm farm = new Farm();
        assertTrue(farm.underConstruction());

        /* Verify that construction doesn't finish before material is delivered */
        for (int i = 0; i < 1000; i++) {
            assertTrue(farm.underConstruction());
            farm.stepTime();
        }


        Cargo planckCargo = new Cargo(PLANCK, null);
        Cargo stoneCargo = new Cargo(STONE, null);
        farm.putCargo(planckCargo);
        farm.putCargo(planckCargo);
        farm.putCargo(planckCargo);
        farm.putCargo(planckCargo);
        farm.putCargo(stoneCargo);
        farm.putCargo(stoneCargo);
        farm.putCargo(stoneCargo);

        for (int i = 0; i < 1000; i++) {
            assertTrue(farm.underConstruction());
            farm.stepTime();
        }

        /* Verify that construction can finish when all material is delivered */
        farm.putCargo(stoneCargo);
        farm.stepTime();

        assertTrue(farm.ready());

        /* Verify that all material was consumed by the construction */
        assertTrue(farm.getAmount(PLANCK) == 0);
        assertTrue(farm.getAmount(STONE) == 0);

        farm.tearDown();

        for (int i = 0; i < 50; i++) {
            assertTrue(farm.burningDown());
            farm.stepTime();
        }


        assertTrue(farm.destroyed());
    }

    @Test(expected = InvalidMaterialException.class)
    public void testInvalidDeliveryToUnfinishedSawmill() throws Exception {
        Sawmill sw = new Sawmill();

        sw.putCargo(new Cargo(SWORD, null));
    }

    @Test(expected = InvalidStateForProduction.class)
    public void testDeliveryToBurningSawmill() throws InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException, Exception {
        Sawmill sm = new Sawmill();

        Utils.constructMediumHouse(sm);

        assertTrue(sm.ready());

        sm.tearDown();

        sm.putCargo(new Cargo(WOOD, null));
    }

    @Test(expected = InvalidStateForProduction.class)
    public void testDeliveryToDestroyedSawmill() throws InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException, Exception {
        Sawmill sm = new Sawmill();

        Utils.constructMediumHouse(sm);

        assertTrue(sm.ready());

        sm.tearDown();

        Utils.fastForward(1000, sm);

        sm.putCargo(new Cargo(WOOD, null));
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
