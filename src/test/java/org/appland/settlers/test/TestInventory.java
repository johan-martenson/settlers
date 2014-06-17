/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Forester;
import static org.appland.settlers.model.Material.*;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Military.Rank;
import org.appland.settlers.model.Storage;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestInventory {

    Storage storage;

    @Before
    public void initTests() {
        storage = new Storage();
    }

    @Test
    public void testInitialInventoryIsEmpty() {
        assertTrue(storage.getAmount(SWORD) == 0);
        assertTrue(storage.getAmount(SHIELD) == 0);
        assertTrue(storage.getAmount(PRIVATE) == 0);
        assertTrue(storage.getAmount(GENERAL) == 0);
        assertTrue(storage.getAmount(SERGEANT) == 0);
        assertTrue(storage.getAmount(BEER) == 0);
        assertTrue(storage.getAmount(GOLD) == 0);
        assertTrue(storage.getAmount(WOOD) == 0);
        assertTrue(storage.getAmount(PLANCK) == 0);
        assertTrue(storage.getAmount(STONE) == 0);
        assertTrue(storage.getAmount(WHEAT) == 0);
        assertTrue(storage.getAmount(COURIER) == 0);
        assertTrue(storage.getAmount(FORESTER) == 0);
        
        assertFalse(storage.isInStock(SWORD));
        assertFalse(storage.isInStock(SHIELD));
        assertFalse(storage.isInStock(PRIVATE));
        assertFalse(storage.isInStock(GENERAL));
        assertFalse(storage.isInStock(SERGEANT));
        assertFalse(storage.isInStock(BEER));
        assertFalse(storage.isInStock(GOLD));
        assertFalse(storage.isInStock(WOOD));
        assertFalse(storage.isInStock(PLANCK));
        assertFalse(storage.isInStock(STONE));
        assertFalse(storage.isInStock(WHEAT));
        assertFalse(storage.isInStock(COURIER));
        assertFalse(storage.isInStock(FORESTER));
    }
    
    @Test
    public void testDepositRetrieveDeadMatter() throws Exception {

        storage.deliver(new Cargo(SWORD));
        assertTrue(storage.getAmount(SWORD) == 1);
        assertTrue(storage.isInStock(SWORD));
        storage.retrieve(SWORD);
        assertTrue(storage.getAmount(SWORD) == 0);
        assertFalse(storage.isInStock(SWORD));

        storage.deliver(new Cargo(SHIELD));
        assertTrue(storage.getAmount(SHIELD) == 1);
        assertTrue(storage.isInStock(SHIELD));
        storage.retrieve(SHIELD);
        assertTrue(storage.getAmount(SHIELD) == 0);
        assertFalse(storage.isInStock(SHIELD));

        storage.deliver(new Cargo(BEER));
        assertTrue(storage.getAmount(BEER) == 1);
        assertTrue(storage.isInStock(BEER));
        storage.retrieve(BEER);
        assertTrue(storage.getAmount(BEER) == 0);
        assertFalse(storage.isInStock(BEER));

        storage.deliver(new Cargo(GOLD));
        assertTrue(storage.getAmount(GOLD) == 1);
        assertTrue(storage.isInStock(GOLD));
        storage.retrieve(GOLD);
        assertTrue(storage.getAmount(GOLD) == 0);
        assertFalse(storage.isInStock(GOLD));

        storage.deliver(new Cargo(WOOD));
        assertTrue(storage.getAmount(WOOD) == 1);
        assertTrue(storage.isInStock(WOOD));
        storage.retrieve(WOOD);
        assertTrue(storage.getAmount(WOOD) == 0);
        assertFalse(storage.isInStock(WOOD));

        storage.deliver(new Cargo(PLANCK));
        assertTrue(storage.getAmount(PLANCK) == 1);
        assertTrue(storage.isInStock(PLANCK));
        storage.retrieve(PLANCK);
        assertTrue(storage.getAmount(PLANCK) == 0);
        assertFalse(storage.isInStock(PLANCK));

        storage.deliver(new Cargo(STONE));
        assertTrue(storage.getAmount(STONE) == 1);
        assertTrue(storage.isInStock(STONE));
        storage.retrieve(STONE);
        assertTrue(storage.getAmount(STONE) == 0);
        assertFalse(storage.isInStock(STONE));

        storage.deliver(new Cargo(WHEAT));
        assertTrue(storage.getAmount(WHEAT) == 1);
        assertTrue(storage.isInStock(WHEAT));
        storage.retrieve(WHEAT);
        assertTrue(storage.getAmount(WHEAT) == 0);
        assertFalse(storage.isInStock(WHEAT));
    }

    @Test
    public void testDepositRetrieveWorkers() throws Exception {
        storage.depositWorker(new Forester());
        assertTrue(storage.getAmount(FORESTER) == 1);
        assertTrue(storage.isInStock(FORESTER));
        storage.retrieveWorker(FORESTER);
        assertTrue(storage.getAmount(FORESTER) == 0);        
        assertFalse(storage.isInStock(FORESTER));
    }
    
    @Test
    public void testDepositRetriveMilitary() throws Exception {

        assertNoMilitaryInInventory(storage);
        storage.depositWorker(new Military(Rank.PRIVATE_RANK));
        assertTrue(storage.getAmount(PRIVATE) == 1);
        assertTrue(storage.isInStock(PRIVATE));
        storage.retrieveMilitary(PRIVATE);
        assertNoMilitaryInInventory(storage);
        assertFalse(storage.isInStock(PRIVATE));

        storage.depositWorker(new Military(Rank.GENERAL_RANK));
        assertTrue(storage.getAmount(GENERAL) == 1);
        assertTrue(storage.isInStock(GENERAL));
        storage.retrieveMilitary(GENERAL);
        assertNoMilitaryInInventory(storage);
        assertFalse(storage.isInStock(GENERAL));

        storage.depositWorker(new Military(Rank.SERGEANT_RANK));
        assertTrue(storage.getAmount(SERGEANT) == 1);
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
        assertTrue(storage.getAmount(PRIVATE) == 0);
        assertTrue(storage.getAmount(SERGEANT) == 0);
        assertTrue(storage.getAmount(GENERAL) == 0);
        
        storage.retrieveAnyMilitary();
    }

    @Test
    public void testRetrieveAnyMilitary() throws Exception {
        storage.depositWorker(new Military(Rank.PRIVATE_RANK));
        assertTrue(storage.isInStock(PRIVATE));
        
        Military m = storage.retrieveAnyMilitary();
        assertNotNull(m);
        
        storage.depositWorker(new Military(Rank.SERGEANT_RANK));
        assertTrue(storage.isInStock(SERGEANT));
        
        m = storage.retrieveAnyMilitary();
        assertNotNull(m);
        
        storage.depositWorker(new Military(Rank.GENERAL_RANK));
        assertTrue(storage.isInStock(GENERAL));
        
        m = storage.retrieveAnyMilitary();
        assertNotNull(m);
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
        storage.depositWorker(new Courier(null));
        storage.retrieve(COURIER);
    }
    
    @Test(expected=Exception.class)
    public void testRetrieveCourierLikeWorker() throws Exception {
        storage.depositWorker(new Courier(null));
        storage.retrieveWorker(COURIER);
    }
    
    @Test
    public void testDepositAndRetrieveMilitaryOfEachKind() throws Exception {
        storage.depositWorker(new Military(Rank.PRIVATE_RANK));
        assertTrue(storage.getAmount(PRIVATE) == 1);
        storage.retrieveMilitary(PRIVATE);
        assertTrue(storage.getAmount(PRIVATE) == 0);
    
        storage.depositWorker(new Military(Rank.SERGEANT_RANK));
        assertTrue(storage.getAmount(SERGEANT) == 1);
        storage.retrieveMilitary(SERGEANT);
        assertTrue(storage.getAmount(SERGEANT) == 0);
        
        storage.depositWorker(new Military(Rank.GENERAL_RANK));
        assertTrue(storage.getAmount(GENERAL) == 1);
        storage.retrieveMilitary(GENERAL);
        assertTrue(storage.getAmount(GENERAL) == 0);
    }

    @Test(expected=Exception.class)
    public void testRetrieveMilitaryAsWorker() throws Exception {
        storage.depositWorker(new Military(Rank.PRIVATE_RANK));
        
        storage.retrieveWorker(PRIVATE);
    }
    
    @Test(expected=Exception.class)
    public void testRetrieveMilitaryAsMaterial() throws Exception {
        storage.depositWorker(new Military(Rank.PRIVATE_RANK));
        
        storage.retrieve(PRIVATE);
    }
    
    private void assertNoMilitaryInInventory(Storage storage) {
        assertTrue(storage.getAmount(PRIVATE) == 0);
        assertTrue(storage.getAmount(SERGEANT) == 0);
        assertTrue(storage.getAmount(GENERAL) == 0);
    }
}
