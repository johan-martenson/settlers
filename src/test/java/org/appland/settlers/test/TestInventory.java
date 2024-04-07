/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Forester;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Storehouse;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.*;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestInventory {

    private Storehouse storehouse;

    @Before
    public void initTests() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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
        storehouse.depositWorker(new Soldier(null, PRIVATE_RANK, null));
        assertEquals(storehouse.getAmount(PRIVATE), 1);
        assertTrue(storehouse.isInStock(PRIVATE));
        storehouse.retrieveSoldierFromInventory(PRIVATE);
        assertNoMilitaryInInventory(storehouse);
        assertFalse(storehouse.isInStock(PRIVATE));

        storehouse.depositWorker(new Soldier(null, GENERAL_RANK, null));
        assertEquals(storehouse.getAmount(GENERAL), 1);
        assertTrue(storehouse.isInStock(GENERAL));
        storehouse.retrieveSoldierFromInventory(GENERAL);
        assertNoMilitaryInInventory(storehouse);
        assertFalse(storehouse.isInStock(GENERAL));

        storehouse.depositWorker(new Soldier(null, SERGEANT_RANK, null));
        assertEquals(storehouse.getAmount(SERGEANT), 1);
        assertTrue(storehouse.isInStock(SERGEANT));
        storehouse.retrieveSoldierFromInventory(SERGEANT);
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

        Courier courier = storehouse.retrieveCourier();

        assertNotNull(courier);
    }

    @Test
    public void testRetrieveAnyMilitaryFromEmptyInventory() {
        assertEquals(storehouse.getAmount(PRIVATE), 0);
        assertEquals(storehouse.getAmount(SERGEANT), 0);
        assertEquals(storehouse.getAmount(GENERAL), 0);

        try {
            storehouse.retrieveSoldierToPopulateBuilding();

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testRetrieveAnyMilitary() {
        storehouse.depositWorker(new Soldier(null, PRIVATE_RANK, null));
        assertTrue(storehouse.isInStock(PRIVATE));

        Soldier military = storehouse.retrieveSoldierToPopulateBuilding();
        assertNotNull(military);

        storehouse.depositWorker(new Soldier(null, SERGEANT_RANK, null));
        assertTrue(storehouse.isInStock(SERGEANT));

        military = storehouse.retrieveSoldierToPopulateBuilding();
        assertNotNull(military);

        storehouse.depositWorker(new Soldier(null, GENERAL_RANK, null));
        assertTrue(storehouse.isInStock(GENERAL));

        military = storehouse.retrieveSoldierToPopulateBuilding();
        assertNotNull(military);
    }

    @Test
    public void testRetrievePrivateFromEmptyInventory() {
        assertFalse(storehouse.isInStock(PRIVATE));

        try {
            storehouse.retrieveSoldierFromInventory(PRIVATE);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testRetrieveSergeantFromEmptyInventory() {
        assertFalse(storehouse.isInStock(SERGEANT));

        try {
        storehouse.retrieveSoldierFromInventory(SERGEANT);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testRetrieveGeneralFromEmptyInventory() {
        assertFalse(storehouse.isInStock(GENERAL));

        try {
            storehouse.retrieveSoldierFromInventory(GENERAL);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testDepositAndRetrieveMilitaryOfEachKind() {
        storehouse.depositWorker(new Soldier(null, PRIVATE_RANK, null));
        assertEquals(storehouse.getAmount(PRIVATE), 1);
        storehouse.retrieveSoldierFromInventory(PRIVATE);
        assertEquals(storehouse.getAmount(PRIVATE), 0);

        storehouse.depositWorker(new Soldier(null, SERGEANT_RANK, null));
        assertEquals(storehouse.getAmount(SERGEANT), 1);
        storehouse.retrieveSoldierFromInventory(SERGEANT);
        assertEquals(storehouse.getAmount(SERGEANT), 0);

        storehouse.depositWorker(new Soldier(null, GENERAL_RANK, null));
        assertEquals(storehouse.getAmount(GENERAL), 1);
        storehouse.retrieveSoldierFromInventory(GENERAL);
        assertEquals(storehouse.getAmount(GENERAL), 0);
    }

    private void assertNoMilitaryInInventory(Storehouse storehouse) {
        assertEquals(storehouse.getAmount(PRIVATE), 0);
        assertEquals(storehouse.getAmount(SERGEANT), 0);
        assertEquals(storehouse.getAmount(GENERAL), 0);
    }
}
