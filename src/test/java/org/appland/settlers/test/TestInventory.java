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
import org.appland.settlers.model.Storage;
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

/**
 *
 * @author johan
 */
public class TestInventory {

    private Storage storage;

    @Before
    public void initTests() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        storage = new Storage(player0);

        Point point1 = new Point(10, 10);
        map.placeBuilding(storage, point1);

        Utils.constructHouse(storage, map);
    }

    @Test
    public void testInitialInventoryIsEmptyExceptCouriers() {
        assertEquals(storage.getAmount(SWORD), 0);
        assertEquals(storage.getAmount(SHIELD), 0);
        assertEquals(storage.getAmount(PRIVATE), 0);
        assertEquals(storage.getAmount(GENERAL), 0);
        assertEquals(storage.getAmount(SERGEANT), 0);
        assertEquals(storage.getAmount(BEER), 0);
        assertEquals(storage.getAmount(GOLD), 0);
        assertEquals(storage.getAmount(WOOD), 0);
        assertEquals(storage.getAmount(PLANK), 0);
        assertEquals(storage.getAmount(STONE), 0);
        assertEquals(storage.getAmount(WHEAT), 0);
        assertEquals(storage.getAmount(FORESTER), 0);

        assertFalse(storage.isInStock(SWORD));
        assertFalse(storage.isInStock(SHIELD));
        assertFalse(storage.isInStock(PRIVATE));
        assertFalse(storage.isInStock(GENERAL));
        assertFalse(storage.isInStock(SERGEANT));
        assertFalse(storage.isInStock(BEER));
        assertFalse(storage.isInStock(GOLD));
        assertFalse(storage.isInStock(WOOD));
        assertFalse(storage.isInStock(PLANK));
        assertFalse(storage.isInStock(STONE));
        assertFalse(storage.isInStock(WHEAT));
        assertFalse(storage.isInStock(FORESTER));
    }

    @Test
    public void testDepositRetrieveDeadMatter() throws Exception {

        storage.putCargo(new Cargo(SWORD, null));
        assertEquals(storage.getAmount(SWORD), 1);
        assertTrue(storage.isInStock(SWORD));
        storage.retrieve(SWORD);
        assertEquals(storage.getAmount(SWORD), 0);
        assertFalse(storage.isInStock(SWORD));

        storage.putCargo(new Cargo(SHIELD, null));
        assertEquals(storage.getAmount(SHIELD), 1);
        assertTrue(storage.isInStock(SHIELD));
        storage.retrieve(SHIELD);
        assertEquals(storage.getAmount(SHIELD), 0);
        assertFalse(storage.isInStock(SHIELD));

        storage.putCargo(new Cargo(BEER, null));
        assertEquals(storage.getAmount(BEER), 1);
        assertTrue(storage.isInStock(BEER));
        storage.retrieve(BEER);
        assertEquals(storage.getAmount(BEER), 0);
        assertFalse(storage.isInStock(BEER));

        storage.putCargo(new Cargo(GOLD, null));
        assertEquals(storage.getAmount(GOLD), 1);
        assertTrue(storage.isInStock(GOLD));
        storage.retrieve(GOLD);
        assertEquals(storage.getAmount(GOLD), 0);
        assertFalse(storage.isInStock(GOLD));

        storage.putCargo(new Cargo(WOOD, null));
        assertEquals(storage.getAmount(WOOD), 1);
        assertTrue(storage.isInStock(WOOD));
        storage.retrieve(WOOD);
        assertEquals(storage.getAmount(WOOD), 0);
        assertFalse(storage.isInStock(WOOD));

        storage.putCargo(new Cargo(PLANK, null));
        assertEquals(storage.getAmount(PLANK), 1);
        assertTrue(storage.isInStock(PLANK));
        storage.retrieve(PLANK);
        assertEquals(storage.getAmount(PLANK), 0);
        assertFalse(storage.isInStock(PLANK));

        storage.putCargo(new Cargo(STONE, null));
        assertEquals(storage.getAmount(STONE), 1);
        assertTrue(storage.isInStock(STONE));
        storage.retrieve(STONE);
        assertEquals(storage.getAmount(STONE), 0);
        assertFalse(storage.isInStock(STONE));

        storage.putCargo(new Cargo(WHEAT, null));
        assertEquals(storage.getAmount(WHEAT), 1);
        assertTrue(storage.isInStock(WHEAT));
        storage.retrieve(WHEAT);
        assertEquals(storage.getAmount(WHEAT), 0);
        assertFalse(storage.isInStock(WHEAT));
    }

    @Test
    public void testDepositRetrieveWorkers() throws Exception {
        storage.depositWorker(new Forester(null, null));
        assertEquals(storage.getAmount(FORESTER), 1);
        assertTrue(storage.isInStock(FORESTER));
        storage.retrieveWorker(FORESTER);
        assertEquals(storage.getAmount(FORESTER), 0);
        assertFalse(storage.isInStock(FORESTER));
    }

    @Test
    public void testDepositRetrieveMilitary() throws Exception {

        assertNoMilitaryInInventory(storage);
        storage.depositWorker(new Military(null, PRIVATE_RANK, null));
        assertEquals(storage.getAmount(PRIVATE), 1);
        assertTrue(storage.isInStock(PRIVATE));
        storage.retrieveMilitary(PRIVATE);
        assertNoMilitaryInInventory(storage);
        assertFalse(storage.isInStock(PRIVATE));

        storage.depositWorker(new Military(null, GENERAL_RANK, null));
        assertEquals(storage.getAmount(GENERAL), 1);
        assertTrue(storage.isInStock(GENERAL));
        storage.retrieveMilitary(GENERAL);
        assertNoMilitaryInInventory(storage);
        assertFalse(storage.isInStock(GENERAL));

        storage.depositWorker(new Military(null, SERGEANT_RANK, null));
        assertEquals(storage.getAmount(SERGEANT), 1);
        assertTrue(storage.isInStock(SERGEANT));
        storage.retrieveMilitary(SERGEANT);
        assertNoMilitaryInInventory(storage);
        assertFalse(storage.isInStock(SERGEANT));
    }

    @Test(expected=Exception.class)
    public void testRetrieveWoodFromEmptyInventory() throws Exception {
        storage.retrieve(WOOD);
    }

    @Test
    public void testRetrieveCourierFromEmptyInventory() {
        /* This should always work */

        storage.retrieveCourier();
    }

    @Test(expected=Exception.class)
    public void testRetrieveAnyMilitaryFromEmptyInventory() throws Exception {
        assertEquals(storage.getAmount(PRIVATE), 0);
        assertEquals(storage.getAmount(SERGEANT), 0);
        assertEquals(storage.getAmount(GENERAL), 0);

        storage.retrieveAnyMilitary();
    }

    @Test
    public void testRetrieveAnyMilitary() throws Exception {
        storage.depositWorker(new Military(null, PRIVATE_RANK, null));
        assertTrue(storage.isInStock(PRIVATE));

        Military military = storage.retrieveAnyMilitary();
        assertNotNull(military);

        storage.depositWorker(new Military(null, SERGEANT_RANK, null));
        assertTrue(storage.isInStock(SERGEANT));

        military = storage.retrieveAnyMilitary();
        assertNotNull(military);

        storage.depositWorker(new Military(null, GENERAL_RANK, null));
        assertTrue(storage.isInStock(GENERAL));

        military = storage.retrieveAnyMilitary();
        assertNotNull(military);
    }

    @Test(expected=Exception.class)
    public void testRetrievePrivateFromEmptyInventory() throws Exception {
        assertFalse(storage.isInStock(PRIVATE));
        storage.retrieveMilitary(PRIVATE);
    }

    @Test(expected=Exception.class)
    public void testRetrieveSergeantFromEmptyInventory() throws Exception {
        assertFalse(storage.isInStock(SERGEANT));
        storage.retrieveMilitary(SERGEANT);
    }

    @Test(expected=Exception.class)
    public void testRetrieveGeneralFromEmptyInventory() throws Exception {
        assertFalse(storage.isInStock(GENERAL));
        storage.retrieveMilitary(GENERAL);
    }

    @Test(expected=Exception.class)
    public void testRetrieveCourierLikeMaterial() throws Exception {
        storage.depositWorker(new Courier(null, null));
        storage.retrieve(COURIER);
    }

    @Test(expected=Exception.class)
    public void testRetrieveCourierLikeWorker() throws Exception {
        storage.depositWorker(new Courier(null, null));
        storage.retrieveWorker(COURIER);
    }

    @Test
    public void testDepositAndRetrieveMilitaryOfEachKind() throws Exception {
        storage.depositWorker(new Military(null, PRIVATE_RANK, null));
        assertEquals(storage.getAmount(PRIVATE), 1);
        storage.retrieveMilitary(PRIVATE);
        assertEquals(storage.getAmount(PRIVATE), 0);

        storage.depositWorker(new Military(null, SERGEANT_RANK, null));
        assertEquals(storage.getAmount(SERGEANT), 1);
        storage.retrieveMilitary(SERGEANT);
        assertEquals(storage.getAmount(SERGEANT), 0);

        storage.depositWorker(new Military(null, GENERAL_RANK, null));
        assertEquals(storage.getAmount(GENERAL), 1);
        storage.retrieveMilitary(GENERAL);
        assertEquals(storage.getAmount(GENERAL), 0);
    }

    @Test(expected=Exception.class)
    public void testRetrieveMilitaryAsWorker() throws Exception {
        storage.depositWorker(new Military(null, PRIVATE_RANK, null));

        storage.retrieveWorker(PRIVATE);
    }

    @Test(expected=Exception.class)
    public void testRetrieveMilitaryAsMaterial() throws Exception {
        storage.depositWorker(new Military(null, PRIVATE_RANK, null));

        storage.retrieve(PRIVATE);
    }

    private void assertNoMilitaryInInventory(Storage storage) {
        assertEquals(storage.getAmount(PRIVATE), 0);
        assertEquals(storage.getAmount(SERGEANT), 0);
        assertEquals(storage.getAmount(GENERAL), 0);
    }
}
