package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
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
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Woodcutter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConstructionTest {

    @Test
    public void testCreateNewWoodcutter() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players,40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point1 = new Point(9, 9);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1);

        assertFalse(wc.isMilitaryBuilding());

        assertEquals(wc.getAmount(WOOD), 0);
        assertEquals(wc.getAmount(PLANCK), 0);
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
        assertEquals(wc.getAmount(PLANCK), 0);
        assertEquals(wc.getAmount(STONE), 0);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        Barracks brks = new Barracks(player0);

        assertTrue(brks.underConstruction());

        assertTrue(brks.isMilitaryBuilding());
        assertEquals(brks.getMaxHostedMilitary(), 2);
        assertEquals(brks.getHostedMilitary(), 0);
        assertEquals(brks.getPromisedMilitary(), 0);

        assertFalse(brks.needsMilitaryManning());

        /* brks needs a reference to the game map and this is set implicityly when it's placed on the map */
        GameMap map = new GameMap(players,30, 30);
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);
        
        Point point1 = new Point(13, 13);
        map.placeBuilding(brks, point1);
        
        Utils.constructHouse(brks, map);

        assertTrue(brks.isMilitaryBuilding());
        assertTrue(brks.ready());
        assertEquals(brks.getHostedMilitary(), 0);
        assertEquals(brks.getMaxHostedMilitary(), 2);
        assertTrue(brks.needsMilitaryManning());

        assertTrue(brks.isMilitaryBuilding());
    }

    @Test
    public void testCreateNewSawmill() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players,40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point1 = new Point(9, 9);
        Building sm = map.placeBuilding(new Sawmill(player0), point1);

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
        assertEquals(sm.getAmount(PLANCK), 0);
        assertEquals(sm.getAmount(STONE), 0);

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

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players,40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point1 = new Point(9, 9);
        Building farm = map.placeBuilding(new Farm(player0), point1);

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
        assertEquals(farm.getAmount(PLANCK), 0);
        assertEquals(farm.getAmount(STONE), 0);

        farm.tearDown();

        for (int i = 0; i < 50; i++) {
            assertTrue(farm.burningDown());
            farm.stepTime();
        }


        assertTrue(farm.destroyed());
    }

    @Test(expected = InvalidMaterialException.class)
    public void testInvalidDeliveryToUnfinishedSawmill() throws Exception {
        Sawmill sw = new Sawmill(null);

        sw.putCargo(new Cargo(SWORD, null));
    }

    @Test(expected = InvalidStateForProduction.class)
    public void testDeliveryToBurningSawmill() throws InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException, Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players,40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing sawmill */
        Point point1 = new Point(9, 9);
        Building sm = map.placeBuilding(new Sawmill(player0), point1);

        Utils.constructHouse(sm, null);

        assertTrue(sm.ready());

        sm.tearDown();

        sm.putCargo(new Cargo(WOOD, null));
    }

    @Test(expected = InvalidStateForProduction.class)
    public void testDeliveryToDestroyedSawmill() throws InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException, Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players,20, 20);
        
        map.placeBuilding(new Headquarter(player0), new Point(10, 10));
        
        Building sm = map.placeBuilding(new Sawmill(player0), new Point(4, 4));

        Utils.constructHouse(sm, map);

        assertTrue(sm.ready());

        sm.tearDown();

        Utils.fastForward(1000, map);

        sm.putCargo(new Cargo(WOOD, null));
    }
}
