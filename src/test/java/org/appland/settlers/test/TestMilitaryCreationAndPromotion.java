package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Material;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.PRIVATE;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Storage;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestMilitaryCreationAndPromotion {

    @Test
    public void createPrivate() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place storage */
        Point point1 = new Point(10, 10);
        Storage storage0 = map.placeBuilding(new Storage(player0), point1);

        Utils.constructHouse(storage0, map);

        int numberOfPrivates = storage0.getAmount(Material.PRIVATE);

        storage0.putCargo(new Cargo(Material.BEER, null));
        storage0.putCargo(new Cargo(Material.SWORD, null));
        storage0.putCargo(new Cargo(Material.SWORD, null));
        storage0.putCargo(new Cargo(Material.SHIELD, null));
        storage0.putCargo(new Cargo(Material.SHIELD, null));
        storage0.putCargo(new Cargo(Material.SHIELD, null));

        assertEquals(storage0.getAmount(Material.PRIVATE), numberOfPrivates);
        assertEquals(storage0.getAmount(Material.BEER), 1);
        assertEquals(storage0.getAmount(Material.SWORD), 2);
        assertEquals(storage0.getAmount(Material.SHIELD), 3);

        Utils.fastForward(110, storage0);

        assertEquals(storage0.getAmount(Material.PRIVATE), numberOfPrivates + 1);
        assertEquals(storage0.getAmount(Material.BEER), 0);
        assertEquals(storage0.getAmount(Material.SWORD), 1);
        assertEquals(storage0.getAmount(Material.SHIELD), 2);
    }

    @Test
    public void storageDoesNotPromote() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        Utils.adjustInventoryTo(headquarter0, COIN, 10, map);
        Utils.adjustInventoryTo(headquarter0, GOLD, 10, map);
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 10, map);

        assertEquals(10, headquarter0.getAmount(Material.COIN));
        assertEquals(10, headquarter0.getAmount(Material.GOLD));
        assertEquals(10, headquarter0.getAmount(Material.PRIVATE));
        assertEquals(0, headquarter0.getAmount(Material.SERGEANT));
        assertEquals(0, headquarter0.getAmount(Material.GENERAL));

        Utils.fastForward(500, map);

        assertEquals(10, headquarter0.getAmount(Material.COIN));
        assertEquals(10, headquarter0.getAmount(Material.GOLD));
        assertEquals(10, headquarter0.getAmount(Material.PRIVATE));
        assertEquals(0, headquarter0.getAmount(Material.SERGEANT));
        assertEquals(0, headquarter0.getAmount(Material.GENERAL));
    }

    @Test
    public void promoteWithoutMilitary() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing fortress */
        Point point1 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Construct the fortress */
        Utils.constructHouse(fortress0, map);

        /* Put gold in the fortress */
        Utils.deliverCargo(fortress0, COIN, map);

        /* Verify that no promotion happens when no military is present */
        Utils.fastForward(200, map);

        assertEquals(fortress0.getAmount(COIN), 1);
    }

    @Test
    public void promoteWithOnlyGenerals() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing fortress */
        Point point1 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Construct the fortress */
        Utils.constructHouse(fortress0, map);

        /* Put gold in the fortress */
        Utils.deliverCargo(fortress0, COIN, map);

        /* Verify that no promotion happens when all occupants are generals */
        Military military0 = Utils.occupyMilitaryBuilding(Military.Rank.GENERAL_RANK, fortress0, map);
        Military military1 = Utils.occupyMilitaryBuilding(Military.Rank.GENERAL_RANK, fortress0, map);

        Utils.fastForward(200, map);

        assertEquals(military0.getRank(), Military.Rank.GENERAL_RANK);
        assertEquals(military1.getRank(), Military.Rank.GENERAL_RANK);
        assertEquals(fortress0.getAmount(COIN), 1);
    }

    @Test
    public void promoteWithoutGold() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing fortress */
        Point point1 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Construct the fortress */
        Utils.constructHouse(fortress0, map);

        /* Verify that no promotion happens without gold */
        Military military0 = Utils.occupyMilitaryBuilding(Military.Rank.PRIVATE_RANK, fortress0, map);
        Military military1 = Utils.occupyMilitaryBuilding(Military.Rank.PRIVATE_RANK, fortress0, map);

        Utils.fastForward(100, map);

        assertEquals(military0.getRank(), Military.Rank.PRIVATE_RANK);
        assertEquals(military1.getRank(), Military.Rank.PRIVATE_RANK);
    }
}
