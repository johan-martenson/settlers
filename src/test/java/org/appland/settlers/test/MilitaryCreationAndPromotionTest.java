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

        storage.deliver(new Cargo(Material.BEER, null));
        storage.deliver(new Cargo(Material.SWORD, null));
        storage.deliver(new Cargo(Material.SWORD, null));
        storage.deliver(new Cargo(Material.SHIELD, null));
        storage.deliver(new Cargo(Material.SHIELD, null));
        storage.deliver(new Cargo(Material.SHIELD, null));

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
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.PRIVATE, null));

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
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));

        storage.deliver(new Cargo(Material.PRIVATE, null));
        storage.deliver(new Cargo(Material.PRIVATE, null));
        storage.deliver(new Cargo(Material.PRIVATE, null));
        storage.deliver(new Cargo(Material.PRIVATE, null));
        storage.deliver(new Cargo(Material.PRIVATE, null));

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
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));

        storage.deliver(new Cargo(Material.PRIVATE, null));
        storage.deliver(new Cargo(Material.PRIVATE, null));
        storage.deliver(new Cargo(Material.PRIVATE, null));
        storage.deliver(new Cargo(Material.PRIVATE, null));
        storage.deliver(new Cargo(Material.PRIVATE, null));

        storage.deliver(new Cargo(Material.SERGEANT, null));
        storage.deliver(new Cargo(Material.SERGEANT, null));
        storage.deliver(new Cargo(Material.SERGEANT, null));

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
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));

        Utils.fastForward(100, storage);

        assertTrue(10 == storage.getAmount(Material.GOLD));
        assertTrue(0 == storage.getAmount(Material.PRIVATE));
        assertTrue(0 == storage.getAmount(Material.SERGEANT));
        assertTrue(0 == storage.getAmount(Material.GENERAL));
    }

    @Test
    public void promoteWithOnlyGenerals() {
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));
        storage.deliver(new Cargo(Material.GOLD, null));

        storage.deliver(new Cargo(Material.GENERAL, null));
        storage.deliver(new Cargo(Material.GENERAL, null));
        storage.deliver(new Cargo(Material.GENERAL, null));
        storage.deliver(new Cargo(Material.GENERAL, null));
        storage.deliver(new Cargo(Material.GENERAL, null));
        storage.deliver(new Cargo(Material.GENERAL, null));
        storage.deliver(new Cargo(Material.GENERAL, null));
        storage.deliver(new Cargo(Material.GENERAL, null));
        storage.deliver(new Cargo(Material.GENERAL, null));
        storage.deliver(new Cargo(Material.GENERAL, null));

        Utils.fastForward(100, storage);

        assertTrue(10 == storage.getAmount(Material.GOLD));
        assertTrue(0 == storage.getAmount(Material.PRIVATE));
        assertTrue(0 == storage.getAmount(Material.SERGEANT));
        assertTrue(10 == storage.getAmount(Material.GENERAL));

    }

    @Test
    public void promoteWithoutGold() {
        storage.deliver(new Cargo(Material.PRIVATE, null));
        storage.deliver(new Cargo(Material.PRIVATE, null));
        storage.deliver(new Cargo(Material.PRIVATE, null));
        storage.deliver(new Cargo(Material.PRIVATE, null));
        storage.deliver(new Cargo(Material.PRIVATE, null));

        Utils.fastForward(100, storage);

        assertTrue(0 == storage.getAmount(Material.GOLD));
        assertTrue(5 == storage.getAmount(Material.PRIVATE));
        assertTrue(0 == storage.getAmount(Material.SERGEANT));
        assertTrue(0 == storage.getAmount(Material.GENERAL));

    }
}
