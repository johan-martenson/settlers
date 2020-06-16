package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Storehouse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.OFFICER_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_FIRST_CLASS_RANK;
import static org.appland.settlers.model.Military.Rank.SERGEANT_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point1);

        Utils.constructHouse(storehouse0);

        int numberOfPrivates = storehouse0.getAmount(PRIVATE);

        storehouse0.putCargo(new Cargo(Material.BEER, null));
        storehouse0.putCargo(new Cargo(Material.SWORD, null));
        storehouse0.putCargo(new Cargo(Material.SWORD, null));
        storehouse0.putCargo(new Cargo(Material.SHIELD, null));
        storehouse0.putCargo(new Cargo(Material.SHIELD, null));
        storehouse0.putCargo(new Cargo(Material.SHIELD, null));

        assertEquals(storehouse0.getAmount(PRIVATE), numberOfPrivates);
        assertEquals(storehouse0.getAmount(Material.BEER), 1);
        assertEquals(storehouse0.getAmount(Material.SWORD), 2);
        assertEquals(storehouse0.getAmount(Material.SHIELD), 3);

        Utils.fastForward(110, map);

        assertEquals(storehouse0.getAmount(PRIVATE), numberOfPrivates + 1);
        assertEquals(storehouse0.getAmount(Material.BEER), 0);
        assertEquals(storehouse0.getAmount(Material.SWORD), 1);
        assertEquals(storehouse0.getAmount(Material.SHIELD), 2);
    }

    @Test
    public void testAvailableRanks() {

        assertEquals(Military.Rank.values().length, 5);

        assertEquals(Military.Rank.valueOf("PRIVATE_RANK"), Military.Rank.PRIVATE_RANK);
        assertEquals(Military.Rank.valueOf("PRIVATE_FIRST_CLASS_RANK"), PRIVATE_FIRST_CLASS_RANK);
        assertEquals(Military.Rank.valueOf("SERGEANT_RANK"), SERGEANT_RANK);
        assertEquals(Military.Rank.valueOf("OFFICER_RANK"), OFFICER_RANK);
        assertEquals(Military.Rank.valueOf("GENERAL_RANK"), GENERAL_RANK);
    }

    @Test
    public void testAvailableMilitary() {
        assertEquals(Material.valueOf("PRIVATE"), PRIVATE);
        assertEquals(Material.valueOf("PRIVATE_FIRST_CLASS"), Material.PRIVATE_FIRST_CLASS);
        assertEquals(Material.valueOf("SERGEANT"), Material.SERGEANT);
        assertEquals(Material.valueOf("OFFICER"), Material.OFFICER);
        assertEquals(Material.valueOf("GENERAL"), Material.GENERAL);
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

        Utils.adjustInventoryTo(headquarter0, COIN, 10);
        Utils.adjustInventoryTo(headquarter0, GOLD, 10);
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 10);

        assertEquals(10, headquarter0.getAmount(COIN));
        assertEquals(10, headquarter0.getAmount(GOLD));
        assertEquals(10, headquarter0.getAmount(PRIVATE));
        assertEquals(0, headquarter0.getAmount(Material.SERGEANT));
        assertEquals(0, headquarter0.getAmount(Material.GENERAL));

        Utils.fastForward(500, map);

        assertEquals(10, headquarter0.getAmount(COIN));
        assertEquals(10, headquarter0.getAmount(GOLD));
        assertEquals(10, headquarter0.getAmount(PRIVATE));
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing fortress */
        Point point1 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Construct the fortress */
        Utils.constructHouse(fortress0);

        /* Put gold in the fortress */
        Utils.deliverCargo(fortress0, COIN);

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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing fortress */
        Point point1 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Construct the fortress */
        Utils.constructHouse(fortress0);

        /* Put gold in the fortress */
        Utils.deliverCargo(fortress0, COIN);

        /* Verify that no promotion happens when all occupants are generals */
        Military military0 = Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress0);
        Military military1 = Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress0);

        Utils.fastForward(200, map);

        assertEquals(military0.getRank(), GENERAL_RANK);
        assertEquals(military1.getRank(), GENERAL_RANK);
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing fortress */
        Point point1 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Construct the fortress */
        Utils.constructHouse(fortress0);

        /* Verify that no promotion happens without gold */
        Military military0 = Utils.occupyMilitaryBuilding(Military.Rank.PRIVATE_RANK, fortress0);
        Military military1 = Utils.occupyMilitaryBuilding(Military.Rank.PRIVATE_RANK, fortress0);

        Utils.fastForward(100, map);

        assertEquals(military0.getRank(), Military.Rank.PRIVATE_RANK);
        assertEquals(military1.getRank(), Military.Rank.PRIVATE_RANK);
    }

    @Test
    public void testPlayerIsCorrectInMilitaryDispatchedFromHeadquarter() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 12);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Connect the barracks to the headquarter */
        map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for a military to start walking to the barracks */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertNotNull(military);
        assertEquals(military.getPlayer(), player0);
    }

    @Test
    public void testPromotedPrivateBecomesCorporal() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(6, 12);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct the barracks */
        Utils.constructHouse(barracks0);

        /* Place a private in the barracks */
        Military military0 = Utils.occupyMilitaryBuilding(Military.Rank.PRIVATE_RANK, barracks0);

        /* Add one coin */
        Cargo coinCargo = new Cargo(COIN, map);
        barracks0.promiseDelivery(COIN);
        barracks0.putCargo(coinCargo);

        /* Wait for the promotion to happen */
        Utils.fastForward(100, map);

        assertEquals(barracks0.getHostedMilitary().size(), 1);
        assertEquals(barracks0.getHostedMilitary().get(0).getRank(), PRIVATE_FIRST_CLASS_RANK);
    }

    @Test
    public void testPromotedCorporalBecomesSergeant() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(6, 12);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct the barracks */
        Utils.constructHouse(barracks0);

        /* Place a corporal in the barracks */
        Military military0 = Utils.occupyMilitaryBuilding(PRIVATE_FIRST_CLASS_RANK, barracks0);

        /* Add one coin */
        Cargo coinCargo = new Cargo(COIN, map);
        barracks0.promiseDelivery(COIN);
        barracks0.putCargo(coinCargo);

        /* Wait for the promotion to happen */
        Utils.fastForward(100, map);

        assertEquals(barracks0.getHostedMilitary().size(), 1);
        assertEquals(barracks0.getHostedMilitary().get(0).getRank(), SERGEANT_RANK);
    }

    @Test
    public void testPromotedSergeantBecomesOfficer() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(6, 12);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct the barracks */
        Utils.constructHouse(barracks0);

        /* Place a sergeant in the barracks */
        Military military0 = Utils.occupyMilitaryBuilding(SERGEANT_RANK, barracks0);

        /* Add one coin */
        Cargo coinCargo = new Cargo(COIN, map);
        barracks0.promiseDelivery(COIN);
        barracks0.putCargo(coinCargo);

        /* Wait for the promotion to happen */
        Utils.fastForward(100, map);

        assertEquals(barracks0.getHostedMilitary().size(), 1);
        assertEquals(barracks0.getHostedMilitary().get(0).getRank(), OFFICER_RANK);
    }

    @Test
    public void testPromotedOfficerBecomesGeneral() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(6, 12);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct the barracks */
        Utils.constructHouse(barracks0);

        /* Place an officer in the barracks */
        Military military0 = Utils.occupyMilitaryBuilding(OFFICER_RANK, barracks0);

        /* Add one coin */
        Cargo coinCargo = new Cargo(COIN, map);
        barracks0.promiseDelivery(COIN);
        barracks0.putCargo(coinCargo);

        /* Wait for the promotion to happen */
        Utils.fastForward(100, map);

        assertEquals(barracks0.getHostedMilitary().size(), 1);
        assertEquals(barracks0.getHostedMilitary().get(0).getRank(), GENERAL_RANK);
    }

    @Test
    public void testGeneralCannotBePromoted() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(6, 12);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct the barracks */
        Utils.constructHouse(barracks0);

        /* Place a general in the barracks */
        Military military0 = Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Add one coin */
        Cargo coinCargo = new Cargo(COIN, map);
        barracks0.promiseDelivery(COIN);
        barracks0.putCargo(coinCargo);

        /* Verify that no promotion happens */
        Utils.fastForward(100, map);

        assertEquals(barracks0.getHostedMilitary().size(), 1);
        assertEquals(barracks0.getHostedMilitary().get(0).getRank(), GENERAL_RANK);
    }
}
