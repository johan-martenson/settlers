package org.appland.settlers.test;

import java.util.Map;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.InvalidNumberOfPlayersException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Storage;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class MilitaryCreationAndPromotionTest {

    Storage storage;
    Map<Material, Integer> inventory;

    @Before
    public void setupTest() throws InvalidNumberOfPlayersException {
        storage = new Storage();
    }

    @Test
    public void createPrivate() {
        int numberOfPrivates = storage.getAmount(Material.PRIVATE);

        storage.deposit(new Cargo(Material.BEER));
        storage.deposit(new Cargo(Material.SWORD));
        storage.deposit(new Cargo(Material.SWORD));
        storage.deposit(new Cargo(Material.SHIELD));
        storage.deposit(new Cargo(Material.SHIELD));
        storage.deposit(new Cargo(Material.SHIELD));

        assertTrue(storage.getAmount(Material.PRIVATE) == numberOfPrivates);
        assertTrue(storage.getAmount(Material.BEER) == 1);
        assertTrue(storage.getAmount(Material.SWORD) == 2);
        assertTrue(storage.getAmount(Material.SHIELD) == 3);

        Utils.fastForward(110, storage);

        assertTrue(storage.getAmount(Material.PRIVATE) == numberOfPrivates + 1);
        assertTrue(storage.getAmount(Material.BEER) == 0);
        assertTrue(storage.getAmount(Material.SWORD) == 1);
        assertTrue(storage.getAmount(Material.SHIELD) == 2);
    }

    @Test
    public void promoteSinglePrivate() {
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.PRIVATE));

        assertTrue(1 == storage.getAmount(Material.GOLD));
        assertTrue(1 == storage.getAmount(Material.PRIVATE));
        assertTrue(0 == storage.getAmount(Material.SERGEANT));
        assertTrue(0 == storage.getAmount(Material.GENERAL));

        Utils.fastForward(110, storage);

        assertTrue(0 == storage.getAmount(Material.GOLD));
        assertTrue(0 == storage.getAmount(Material.PRIVATE));
        assertTrue(1 == storage.getAmount(Material.SERGEANT));
        assertTrue(0 == storage.getAmount(Material.GENERAL));
    }

    @Test
    public void promoteGroupOfPrivates() {
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));

        storage.deposit(new Cargo(Material.PRIVATE));
        storage.deposit(new Cargo(Material.PRIVATE));
        storage.deposit(new Cargo(Material.PRIVATE));
        storage.deposit(new Cargo(Material.PRIVATE));
        storage.deposit(new Cargo(Material.PRIVATE));

        assertTrue(10 == storage.getAmount(Material.GOLD));
        assertTrue(5 == storage.getAmount(Material.PRIVATE));
        assertTrue(0 == storage.getAmount(Material.SERGEANT));
        assertTrue(0 == storage.getAmount(Material.GENERAL));

        Utils.fastForward(110, storage);

        assertTrue(9 == storage.getAmount(Material.GOLD));
        assertTrue(4 == storage.getAmount(Material.PRIVATE));
        assertTrue(1 == storage.getAmount(Material.SERGEANT));
        assertTrue(0 == storage.getAmount(Material.GENERAL));
    }

    @Test
    public void promotePrivateAndSergeant() {
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));

        storage.deposit(new Cargo(Material.PRIVATE));
        storage.deposit(new Cargo(Material.PRIVATE));
        storage.deposit(new Cargo(Material.PRIVATE));
        storage.deposit(new Cargo(Material.PRIVATE));
        storage.deposit(new Cargo(Material.PRIVATE));

        storage.deposit(new Cargo(Material.SERGEANT));
        storage.deposit(new Cargo(Material.SERGEANT));
        storage.deposit(new Cargo(Material.SERGEANT));

        assertTrue(10 == storage.getAmount(Material.GOLD));
        assertTrue(5 == storage.getAmount(Material.PRIVATE));
        assertTrue(3 == storage.getAmount(Material.SERGEANT));
        assertTrue(0 == storage.getAmount(Material.GENERAL));

        Utils.fastForward(110, storage);

        assertTrue(8 == storage.getAmount(Material.GOLD));
        assertTrue(4 == storage.getAmount(Material.PRIVATE));
        assertTrue(3 == storage.getAmount(Material.SERGEANT));
        assertTrue(1 == storage.getAmount(Material.GENERAL));

    }

    @Test
    public void promoteWithoutMilitary() {
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));

        Utils.fastForward(100, storage);

        assertTrue(10 == storage.getAmount(Material.GOLD));
        assertTrue(0 == storage.getAmount(Material.PRIVATE));
        assertTrue(0 == storage.getAmount(Material.SERGEANT));
        assertTrue(0 == storage.getAmount(Material.GENERAL));
    }

    @Test
    public void promoteWithOnlyGenerals() {
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));
        storage.deposit(new Cargo(Material.GOLD));

        storage.deposit(new Cargo(Material.GENERAL));
        storage.deposit(new Cargo(Material.GENERAL));
        storage.deposit(new Cargo(Material.GENERAL));
        storage.deposit(new Cargo(Material.GENERAL));
        storage.deposit(new Cargo(Material.GENERAL));
        storage.deposit(new Cargo(Material.GENERAL));
        storage.deposit(new Cargo(Material.GENERAL));
        storage.deposit(new Cargo(Material.GENERAL));
        storage.deposit(new Cargo(Material.GENERAL));
        storage.deposit(new Cargo(Material.GENERAL));

        Utils.fastForward(100, storage);

        assertTrue(10 == storage.getAmount(Material.GOLD));
        assertTrue(0 == storage.getAmount(Material.PRIVATE));
        assertTrue(0 == storage.getAmount(Material.SERGEANT));
        assertTrue(10 == storage.getAmount(Material.GENERAL));

    }

    @Test
    public void promoteWithoutGold() {
        storage.deposit(new Cargo(Material.PRIVATE));
        storage.deposit(new Cargo(Material.PRIVATE));
        storage.deposit(new Cargo(Material.PRIVATE));
        storage.deposit(new Cargo(Material.PRIVATE));
        storage.deposit(new Cargo(Material.PRIVATE));

        Utils.fastForward(100, storage);

        assertTrue(0 == storage.getAmount(Material.GOLD));
        assertTrue(5 == storage.getAmount(Material.PRIVATE));
        assertTrue(0 == storage.getAmount(Material.SERGEANT));
        assertTrue(0 == storage.getAmount(Material.GENERAL));

    }
}
