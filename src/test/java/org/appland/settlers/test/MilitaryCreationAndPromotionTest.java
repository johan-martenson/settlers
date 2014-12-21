package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Storage;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MilitaryCreationAndPromotionTest {

    Storage storage;
    Map<Material, Integer> inventory;

    @Before
    public void setupTest() throws Exception {
        Player player0 = new Player("Player 0");
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
    public void createPrivate() throws Exception {
        int numberOfPrivates = storage.getAmount(Material.PRIVATE);

        storage.putCargo(new Cargo(Material.BEER, null));
        storage.putCargo(new Cargo(Material.SWORD, null));
        storage.putCargo(new Cargo(Material.SWORD, null));
        storage.putCargo(new Cargo(Material.SHIELD, null));
        storage.putCargo(new Cargo(Material.SHIELD, null));
        storage.putCargo(new Cargo(Material.SHIELD, null));

        assertEquals(storage.getAmount(Material.PRIVATE), numberOfPrivates);
        assertEquals(storage.getAmount(Material.BEER), 1);
        assertEquals(storage.getAmount(Material.SWORD), 2);
        assertEquals(storage.getAmount(Material.SHIELD), 3);

        Utils.fastForward(110, storage);

        assertEquals(storage.getAmount(Material.PRIVATE), numberOfPrivates + 1);
        assertEquals(storage.getAmount(Material.BEER), 0);
        assertEquals(storage.getAmount(Material.SWORD), 1);
        assertEquals(storage.getAmount(Material.SHIELD), 2);
    }

    @Test
    public void promoteSinglePrivate() throws Exception {
        storage.putCargo(new Cargo(Material.GOLD, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));

        assertEquals(1, storage.getAmount(Material.GOLD));
        assertEquals(1, storage.getAmount(Material.PRIVATE));
        assertEquals(0, storage.getAmount(Material.SERGEANT));
        assertEquals(0, storage.getAmount(Material.GENERAL));

        Utils.fastForward(110, storage);

        assertEquals(0, storage.getAmount(Material.GOLD));
        assertEquals(0, storage.getAmount(Material.PRIVATE));
        assertEquals(1, storage.getAmount(Material.SERGEANT));
        assertEquals(0, storage.getAmount(Material.GENERAL));
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

        assertEquals(10, storage.getAmount(Material.GOLD));
        assertEquals(5, storage.getAmount(Material.PRIVATE));
        assertEquals(0, storage.getAmount(Material.SERGEANT));
        assertEquals(0, storage.getAmount(Material.GENERAL));

        Utils.fastForward(110, storage);

        assertEquals(9, storage.getAmount(Material.GOLD));
        assertEquals(4, storage.getAmount(Material.PRIVATE));
        assertEquals(1, storage.getAmount(Material.SERGEANT));
        assertEquals(0, storage.getAmount(Material.GENERAL));
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

        assertEquals(10, storage.getAmount(Material.GOLD));
        assertEquals(5, storage.getAmount(Material.PRIVATE));
        assertEquals(3, storage.getAmount(Material.SERGEANT));
        assertEquals(0, storage.getAmount(Material.GENERAL));

        Utils.fastForward(110, storage);

        assertEquals(8, storage.getAmount(Material.GOLD));
        assertEquals(4, storage.getAmount(Material.PRIVATE));
        assertEquals(3, storage.getAmount(Material.SERGEANT));
        assertEquals(1, storage.getAmount(Material.GENERAL));

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

        assertEquals(10, storage.getAmount(Material.GOLD));
        assertEquals(0, storage.getAmount(Material.PRIVATE));
        assertEquals(0, storage.getAmount(Material.SERGEANT));
        assertEquals(0, storage.getAmount(Material.GENERAL));
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

        assertEquals(10, storage.getAmount(Material.GOLD));
        assertEquals(0, storage.getAmount(Material.PRIVATE));
        assertEquals(0, storage.getAmount(Material.SERGEANT));
        assertEquals(10, storage.getAmount(Material.GENERAL));

    }

    @Test
    public void promoteWithoutGold() throws Exception {
        storage.putCargo(new Cargo(Material.PRIVATE, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));
        storage.putCargo(new Cargo(Material.PRIVATE, null));

        Utils.fastForward(100, storage);

        assertEquals(0, storage.getAmount(Material.GOLD));
        assertEquals(5, storage.getAmount(Material.PRIVATE));
        assertEquals(0, storage.getAmount(Material.SERGEANT));
        assertEquals(0, storage.getAmount(Material.GENERAL));

    }
}
