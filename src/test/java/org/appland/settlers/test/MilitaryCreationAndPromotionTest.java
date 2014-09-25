package org.appland.settlers.test;

import java.util.Map;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Storage;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class MilitaryCreationAndPromotionTest {

    Storage storage;
    Map<Material, Integer> inventory;

    @Before
    public void setupTest() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);

        storage = new Storage();
        
        Point point1 = new Point(10, 10);
        map.placeBuilding(storage, point1);
        
        Utils.constructHouse(storage, map);
    }

    @Test
    public void createPrivate() throws Exception {
        int numberOfPrivates = storage.getAmount(Material.PRIVATE);

        storage.putCargo(new Cargo(Material.BEER, null));
        storage.putCargo(new Cargo(Material.SWORD, null));
        storage.putCargo(new Cargo(Material.SWORD, null));
        storage.putCargo(new Cargo(Material.SHIELD, null));
        storage.putCargo(new Cargo(Material.SHIELD, null));
        storage.putCargo(new Cargo(Material.SHIELD, null));

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
    public void promoteSinglePrivate() throws Exception {
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));

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
    public void promoteGroupOfPrivates() throws Exception {
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));

        storage.putCargo(new Cargo(Material.PRIVATE, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));

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
    public void promotePrivateAndSergeant() throws Exception {
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));

        storage.putCargo(new Cargo(Material.PRIVATE, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));

        storage.putCargo(new Cargo(Material.SERGEANT, null));
        storage.putCargo(new Cargo(Material.SERGEANT, null));
        storage.putCargo(new Cargo(Material.SERGEANT, null));

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
    public void promoteWithoutMilitary() throws Exception {
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));

        Utils.fastForward(100, storage);

        assertTrue(10 == storage.getAmount(Material.GOLD));
        assertTrue(0 == storage.getAmount(Material.PRIVATE));
        assertTrue(0 == storage.getAmount(Material.SERGEANT));
        assertTrue(0 == storage.getAmount(Material.GENERAL));
    }

    @Test
    public void promoteWithOnlyGenerals() throws Exception {
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.GOLD, null));

        storage.putCargo(new Cargo(Material.GENERAL, null));
        storage.putCargo(new Cargo(Material.GENERAL, null));
        storage.putCargo(new Cargo(Material.GENERAL, null));
        storage.putCargo(new Cargo(Material.GENERAL, null));
        storage.putCargo(new Cargo(Material.GENERAL, null));
        storage.putCargo(new Cargo(Material.GENERAL, null));
        storage.putCargo(new Cargo(Material.GENERAL, null));
        storage.putCargo(new Cargo(Material.GENERAL, null));
        storage.putCargo(new Cargo(Material.GENERAL, null));
        storage.putCargo(new Cargo(Material.GENERAL, null));

        Utils.fastForward(100, storage);

        assertTrue(10 == storage.getAmount(Material.GOLD));
        assertTrue(0 == storage.getAmount(Material.PRIVATE));
        assertTrue(0 == storage.getAmount(Material.SERGEANT));
        assertTrue(10 == storage.getAmount(Material.GENERAL));

    }

    @Test
    public void promoteWithoutGold() throws Exception {
        storage.putCargo(new Cargo(Material.PRIVATE, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));

        Utils.fastForward(100, storage);

        assertTrue(0 == storage.getAmount(Material.GOLD));
        assertTrue(5 == storage.getAmount(Material.PRIVATE));
        assertTrue(0 == storage.getAmount(Material.SERGEANT));
        assertTrue(0 == storage.getAmount(Material.GENERAL));

    }
}
