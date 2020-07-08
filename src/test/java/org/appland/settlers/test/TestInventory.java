/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Forester;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Storehouse;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.COURIER;
import static org.appland.settlers.model.Material.FORESTER;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Military.Rank.SERGEANT_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author johan
 */
public class TestInventory {

    private Storehouse storehouse;

    @Before
    public void initTests() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(10, 10);
        storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Finish construction of the storehouse */
        Utils.constructHouse(storehouse);
    }

    @Test
    public void testInitialInventoryIsEmptyExceptCouriers() {
        assertEquals(storehouse.getAmount(SWORD), 0);
        assertEquals(storehouse.getAmount(SHIELD), 0);
        assertEquals(storehouse.getAmount(PRIVATE), 0);
        assertEquals(storehouse.getAmount(GENERAL), 0);
        assertEquals(storehouse.getAmount(SERGEANT), 0);
        assertEquals(storehouse.getAmount(BEER), 0);
        assertEquals(storehouse.getAmount(GOLD), 0);
        assertEquals(storehouse.getAmount(WOOD), 0);
        assertEquals(storehouse.getAmount(PLANK), 0);
        assertEquals(storehouse.getAmount(STONE), 0);
        assertEquals(storehouse.getAmount(WHEAT), 0);
        assertEquals(storehouse.getAmount(FORESTER), 0);

        assertFalse(storehouse.isInStock(SWORD));
        assertFalse(storehouse.isInStock(SHIELD));
        assertFalse(storehouse.isInStock(PRIVATE));
        assertFalse(storehouse.isInStock(GENERAL));
        assertFalse(storehouse.isInStock(SERGEANT));
        assertFalse(storehouse.isInStock(BEER));
        assertFalse(storehouse.isInStock(GOLD));
        assertFalse(storehouse.isInStock(WOOD));
        assertFalse(storehouse.isInStock(PLANK));
        assertFalse(storehouse.isInStock(STONE));
        assertFalse(storehouse.isInStock(WHEAT));
        assertFalse(storehouse.isInStock(FORESTER));
    }

    @Test
    public void testDepositRetrieveDeadMatter() {

        storehouse.putCargo(new Cargo(SWORD, null));
        assertEquals(storehouse.getAmount(SWORD), 1);
        assertTrue(storehouse.isInStock(SWORD));
        storehouse.retrieve(SWORD);
        assertEquals(storehouse.getAmount(SWORD), 0);
        assertFalse(storehouse.isInStock(SWORD));

        storehouse.putCargo(new Cargo(SHIELD, null));
        assertEquals(storehouse.getAmount(SHIELD), 1);
        assertTrue(storehouse.isInStock(SHIELD));
        storehouse.retrieve(SHIELD);
        assertEquals(storehouse.getAmount(SHIELD), 0);
        assertFalse(storehouse.isInStock(SHIELD));

        storehouse.putCargo(new Cargo(BEER, null));
        assertEquals(storehouse.getAmount(BEER), 1);
        assertTrue(storehouse.isInStock(BEER));
        storehouse.retrieve(BEER);
        assertEquals(storehouse.getAmount(BEER), 0);
        assertFalse(storehouse.isInStock(BEER));

        storehouse.putCargo(new Cargo(GOLD, null));
        assertEquals(storehouse.getAmount(GOLD), 1);
        assertTrue(storehouse.isInStock(GOLD));
        storehouse.retrieve(GOLD);
        assertEquals(storehouse.getAmount(GOLD), 0);
        assertFalse(storehouse.isInStock(GOLD));

        storehouse.putCargo(new Cargo(WOOD, null));
        assertEquals(storehouse.getAmount(WOOD), 1);
        assertTrue(storehouse.isInStock(WOOD));
        storehouse.retrieve(WOOD);
        assertEquals(storehouse.getAmount(WOOD), 0);
        assertFalse(storehouse.isInStock(WOOD));

        storehouse.putCargo(new Cargo(PLANK, null));
        assertEquals(storehouse.getAmount(PLANK), 1);
        assertTrue(storehouse.isInStock(PLANK));
        storehouse.retrieve(PLANK);
        assertEquals(storehouse.getAmount(PLANK), 0);
        assertFalse(storehouse.isInStock(PLANK));

        storehouse.putCargo(new Cargo(STONE, null));
        assertEquals(storehouse.getAmount(STONE), 1);
        assertTrue(storehouse.isInStock(STONE));
        storehouse.retrieve(STONE);
        assertEquals(storehouse.getAmount(STONE), 0);
        assertFalse(storehouse.isInStock(STONE));

        storehouse.putCargo(new Cargo(WHEAT, null));
        assertEquals(storehouse.getAmount(WHEAT), 1);
        assertTrue(storehouse.isInStock(WHEAT));
        storehouse.retrieve(WHEAT);
        assertEquals(storehouse.getAmount(WHEAT), 0);
        assertFalse(storehouse.isInStock(WHEAT));
    }

    @Test
    public void testDepositRetrieveWorkers() {
        storehouse.depositWorker(new Forester(null, null));
        assertEquals(storehouse.getAmount(FORESTER), 1);
        assertTrue(storehouse.isInStock(FORESTER));
        storehouse.retrieveWorker(FORESTER);
        assertEquals(storehouse.getAmount(FORESTER), 0);
        assertFalse(storehouse.isInStock(FORESTER));
    }

    @Test
    public void testDepositRetrieveMilitary() {

        assertNoMilitaryInInventory(storehouse);
        storehouse.depositWorker(new Military(null, PRIVATE_RANK, null));
        assertEquals(storehouse.getAmount(PRIVATE), 1);
        assertTrue(storehouse.isInStock(PRIVATE));
        storehouse.retrieveMilitary(PRIVATE);
        assertNoMilitaryInInventory(storehouse);
        assertFalse(storehouse.isInStock(PRIVATE));

        storehouse.depositWorker(new Military(null, GENERAL_RANK, null));
        assertEquals(storehouse.getAmount(GENERAL), 1);
        assertTrue(storehouse.isInStock(GENERAL));
        storehouse.retrieveMilitary(GENERAL);
        assertNoMilitaryInInventory(storehouse);
        assertFalse(storehouse.isInStock(GENERAL));

        storehouse.depositWorker(new Military(null, SERGEANT_RANK, null));
        assertEquals(storehouse.getAmount(SERGEANT), 1);
        assertTrue(storehouse.isInStock(SERGEANT));
        storehouse.retrieveMilitary(SERGEANT);
        assertNoMilitaryInInventory(storehouse);
        assertFalse(storehouse.isInStock(SERGEANT));
    }

    @Test(expected=Exception.class)
    public void testRetrieveWoodFromEmptyInventory() {
        storehouse.retrieve(WOOD);
    }

    @Test
    public void testRetrieveCourierFromEmptyInventory() {
        /* This should always work */

        storehouse.retrieveCourier();
    }

    @Test
    public void testRetrieveAnyMilitaryFromEmptyInventory() {
        assertEquals(storehouse.getAmount(PRIVATE), 0);
        assertEquals(storehouse.getAmount(SERGEANT), 0);
        assertEquals(storehouse.getAmount(GENERAL), 0);

        try {
            storehouse.retrieveAnyMilitary();

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testRetrieveAnyMilitary() {
        storehouse.depositWorker(new Military(null, PRIVATE_RANK, null));
        assertTrue(storehouse.isInStock(PRIVATE));

        Military military = storehouse.retrieveAnyMilitary();
        assertNotNull(military);

        storehouse.depositWorker(new Military(null, SERGEANT_RANK, null));
        assertTrue(storehouse.isInStock(SERGEANT));

        military = storehouse.retrieveAnyMilitary();
        assertNotNull(military);

        storehouse.depositWorker(new Military(null, GENERAL_RANK, null));
        assertTrue(storehouse.isInStock(GENERAL));

        military = storehouse.retrieveAnyMilitary();
        assertNotNull(military);
    }

    @Test
    public void testRetrievePrivateFromEmptyInventory() {
        assertFalse(storehouse.isInStock(PRIVATE));

        try {
            storehouse.retrieveMilitary(PRIVATE);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testRetrieveSergeantFromEmptyInventory() {
        assertFalse(storehouse.isInStock(SERGEANT));

        try {
        storehouse.retrieveMilitary(SERGEANT);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testRetrieveGeneralFromEmptyInventory() {
        assertFalse(storehouse.isInStock(GENERAL));

        try {
            storehouse.retrieveMilitary(GENERAL);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testRetrieveCourierLikeWorker() {
        storehouse.depositWorker(new Courier(null, null));

        try {
            storehouse.retrieveWorker(COURIER);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testDepositAndRetrieveMilitaryOfEachKind() {
        storehouse.depositWorker(new Military(null, PRIVATE_RANK, null));
        assertEquals(storehouse.getAmount(PRIVATE), 1);
        storehouse.retrieveMilitary(PRIVATE);
        assertEquals(storehouse.getAmount(PRIVATE), 0);

        storehouse.depositWorker(new Military(null, SERGEANT_RANK, null));
        assertEquals(storehouse.getAmount(SERGEANT), 1);
        storehouse.retrieveMilitary(SERGEANT);
        assertEquals(storehouse.getAmount(SERGEANT), 0);

        storehouse.depositWorker(new Military(null, GENERAL_RANK, null));
        assertEquals(storehouse.getAmount(GENERAL), 1);
        storehouse.retrieveMilitary(GENERAL);
        assertEquals(storehouse.getAmount(GENERAL), 0);
    }

    @Test
    public void testRetrieveMilitaryAsWorker() {
        storehouse.depositWorker(new Military(null, PRIVATE_RANK, null));

        try {
            storehouse.retrieveWorker(PRIVATE);

            fail();
        } catch (Exception e) {}
    }

    private void assertNoMilitaryInInventory(Storehouse storehouse) {
        assertEquals(storehouse.getAmount(PRIVATE), 0);
        assertEquals(storehouse.getAmount(SERGEANT), 0);
        assertEquals(storehouse.getAmount(GENERAL), 0);
    }
}
