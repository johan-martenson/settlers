package org.appland.settlers.test;

import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.buildings.Storehouse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.OFFICER;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.PRIVATE_FIRST_CLASS;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.actors.Soldier.Rank.GENERAL_RANK;
import static org.appland.settlers.model.actors.Soldier.Rank.OFFICER_RANK;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_FIRST_CLASS_RANK;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.actors.Soldier.Rank.SERGEANT_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestSoldierCreationAndPromotion {

    @Test
    public void createPrivate() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

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

        assertEquals(Soldier.Rank.values().length, 5);

        assertEquals(Soldier.Rank.valueOf("PRIVATE_RANK"), Soldier.Rank.PRIVATE_RANK);
        assertEquals(Soldier.Rank.valueOf("PRIVATE_FIRST_CLASS_RANK"), PRIVATE_FIRST_CLASS_RANK);
        assertEquals(Soldier.Rank.valueOf("SERGEANT_RANK"), SERGEANT_RANK);
        assertEquals(Soldier.Rank.valueOf("OFFICER_RANK"), OFFICER_RANK);
        assertEquals(Soldier.Rank.valueOf("GENERAL_RANK"), GENERAL_RANK);
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

        /* Place headquarter */
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

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fortress */
        Point point1 = new Point(6, 12);
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point1);

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

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fortress */
        Point point1 = new Point(6, 12);
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Construct the fortress */
        Utils.constructHouse(fortress0);

        /* Put gold in the fortress */
        Utils.deliverCargo(fortress0, COIN);

        /* Verify that no promotion happens when all occupants are generals */
        Soldier military0 = Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress0);
        Soldier military1 = Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress0);

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

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fortress */
        Point point1 = new Point(6, 12);
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Construct the fortress */
        Utils.constructHouse(fortress0);

        /* Verify that no promotion happens without gold */
        Soldier military0 = Utils.occupyMilitaryBuilding(Soldier.Rank.PRIVATE_RANK, fortress0);
        Soldier military1 = Utils.occupyMilitaryBuilding(Soldier.Rank.PRIVATE_RANK, fortress0);

        Utils.fastForward(100, map);

        assertEquals(military0.getRank(), Soldier.Rank.PRIVATE_RANK);
        assertEquals(military1.getRank(), Soldier.Rank.PRIVATE_RANK);
    }

    @Test
    public void testPlayerIsCorrectInMilitaryDispatchedFromHeadquarter() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place barracks */
        Point point22 = new Point(6, 12);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Connect the barracks to the headquarter */
        map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for a military to start walking to the barracks */
        Soldier military = Utils.waitForSoldierOutsideBuilding(player0);

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
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct the barracks */
        Utils.constructHouse(barracks0);

        /* Place a private in the barracks */
        Soldier military0 = Utils.occupyMilitaryBuilding(Soldier.Rank.PRIVATE_RANK, barracks0);

        /* Add one coin */
        Cargo coinCargo = new Cargo(COIN, map);
        barracks0.promiseDelivery(COIN);
        barracks0.putCargo(coinCargo);

        /* Wait for the promotion to happen */
        Utils.fastForward(100, map);

        assertEquals(barracks0.getHostedSoldiers().size(), 1);
        assertEquals(barracks0.getHostedSoldiers().getFirst().getRank(), PRIVATE_FIRST_CLASS_RANK);
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
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct the barracks */
        Utils.constructHouse(barracks0);

        /* Place a corporal in the barracks */
        Soldier military0 = Utils.occupyMilitaryBuilding(PRIVATE_FIRST_CLASS_RANK, barracks0);

        /* Add one coin */
        Cargo coinCargo = new Cargo(COIN, map);
        barracks0.promiseDelivery(COIN);
        barracks0.putCargo(coinCargo);

        /* Wait for the promotion to happen */
        Utils.fastForward(100, map);

        assertEquals(barracks0.getHostedSoldiers().size(), 1);
        assertEquals(barracks0.getHostedSoldiers().getFirst().getRank(), SERGEANT_RANK);
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
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct the barracks */
        Utils.constructHouse(barracks0);

        /* Place a sergeant in the barracks */
        Soldier military0 = Utils.occupyMilitaryBuilding(SERGEANT_RANK, barracks0);

        /* Add one coin */
        Cargo coinCargo = new Cargo(COIN, map);
        barracks0.promiseDelivery(COIN);
        barracks0.putCargo(coinCargo);

        /* Wait for the promotion to happen */
        Utils.fastForward(100, map);

        assertEquals(barracks0.getHostedSoldiers().size(), 1);
        assertEquals(barracks0.getHostedSoldiers().getFirst().getRank(), OFFICER_RANK);
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
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct the barracks */
        Utils.constructHouse(barracks0);

        /* Place an officer in the barracks */
        Soldier military0 = Utils.occupyMilitaryBuilding(OFFICER_RANK, barracks0);

        /* Add one coin */
        Cargo coinCargo = new Cargo(COIN, map);
        barracks0.promiseDelivery(COIN);
        barracks0.putCargo(coinCargo);

        /* Wait for the promotion to happen */
        Utils.fastForward(100, map);

        assertEquals(barracks0.getHostedSoldiers().size(), 1);
        assertEquals(barracks0.getHostedSoldiers().getFirst().getRank(), GENERAL_RANK);
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
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct the barracks */
        Utils.constructHouse(barracks0);

        /* Place a general in the barracks */
        Soldier military0 = Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Add one coin */
        Cargo coinCargo = new Cargo(COIN, map);
        barracks0.promiseDelivery(COIN);
        barracks0.putCargo(coinCargo);

        /* Verify that no promotion happens */
        Utils.fastForward(100, map);

        assertEquals(barracks0.getHostedSoldiers().size(), 1);
        assertEquals(barracks0.getHostedSoldiers().getFirst().getRank(), GENERAL_RANK);
    }

    @Test
    public void testUpgradeOfAllRanksAtSameTime() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fortress */
        Point point1 = new Point(6, 12);
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Connect the headquarter with the fortress */
        Road road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        /* Make sure the headquarter has only privates */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 5);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 1);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 1);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 1);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 1);

        /* Wait for the fortress to get constructed and populated */
        Utils.waitForBuildingToBeConstructed(fortress0);

        Utils.waitForMilitaryBuildingToGetPopulated(fortress0, 9);

        /* Verify that adding a coin will promote one of each type of soldier */
        Utils.deliverCargo(fortress0, COIN);

        Utils.waitForBuildingToGetAmountOfMaterial(fortress0, COIN, 0);

        Map<Soldier.Rank, Integer> rankCount = new HashMap<>();

        for (Soldier military : fortress0.getHostedSoldiers()) {
            int amount = rankCount.getOrDefault(military.getRank(), 0);

            rankCount.put(military.getRank(), amount + 1);
        }

        assertEquals(rankCount.get(PRIVATE_RANK).intValue(), 4);
        assertEquals(rankCount.get(PRIVATE_FIRST_CLASS_RANK).intValue(), 1);
        assertEquals(rankCount.get(SERGEANT_RANK).intValue(), 1);
        assertEquals(rankCount.get(OFFICER_RANK).intValue(), 1);
        assertEquals(rankCount.get(GENERAL_RANK).intValue(), 2);
    }

    @Test
    public void testUpgradeOfOnlyPrivates() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fortress */
        Point point1 = new Point(6, 12);
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Connect the headquarter with the fortress */
        Road road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        /* Make sure the headquarter has only privates */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 9);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        /* Wait for the fortress to get constructed and populated */
        Utils.waitForBuildingToBeConstructed(fortress0);

        Utils.waitForMilitaryBuildingToGetPopulated(fortress0, 9);

        /* Verify that adding a coin will promote one of each type of soldier */
        Utils.deliverCargo(fortress0, COIN);

        Utils.waitForBuildingToGetAmountOfMaterial(fortress0, COIN, 0);

        Map<Soldier.Rank, Integer> rankCount = new HashMap<>();

        for (Soldier military : fortress0.getHostedSoldiers()) {
            int amount = rankCount.getOrDefault(military.getRank(), 0);

            rankCount.put(military.getRank(), amount + 1);
        }

        assertEquals(rankCount.get(PRIVATE_RANK).intValue(), 8);
        assertEquals(rankCount.getOrDefault(PRIVATE_FIRST_CLASS_RANK, 0).intValue(), 1);
        assertEquals(rankCount.getOrDefault(SERGEANT_RANK, 0).intValue(), 0);
        assertEquals(rankCount.getOrDefault(OFFICER_RANK, 0).intValue(), 0);
        assertEquals(rankCount.getOrDefault(GENERAL_RANK, 0).intValue(), 0);
    }

    @Test
    public void testUpgradeOfPrivatesAndOnePrivateFirstRank() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fortress */
        Point point1 = new Point(6, 12);
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Connect the headquarter with the fortress */
        Road road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        /* Make sure the headquarter has only privates */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 8);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 1);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        /* Wait for the fortress to get constructed and populated */
        Utils.waitForBuildingToBeConstructed(fortress0);

        Utils.waitForMilitaryBuildingToGetPopulated(fortress0, 9);

        /* Verify that adding a coin will promote one of each type of soldier */
        Utils.deliverCargo(fortress0, COIN);

        Utils.waitForBuildingToGetAmountOfMaterial(fortress0, COIN, 0);

        Map<Soldier.Rank, Integer> rankCount = new HashMap<>();

        for (Soldier military : fortress0.getHostedSoldiers()) {
            int amount = rankCount.getOrDefault(military.getRank(), 0);

            rankCount.put(military.getRank(), amount + 1);
        }

        assertEquals(rankCount.get(PRIVATE_RANK).intValue(), 7);
        assertEquals(rankCount.getOrDefault(PRIVATE_FIRST_CLASS_RANK, 0).intValue(), 1);
        assertEquals(rankCount.getOrDefault(SERGEANT_RANK, 0).intValue(), 1);
        assertEquals(rankCount.getOrDefault(OFFICER_RANK, 0).intValue(), 0);
        assertEquals(rankCount.getOrDefault(GENERAL_RANK, 0).intValue(), 0);
    }

    @Test
    public void testUpgradeOfPrivatesAndPrivateFirstRankAndOneSergeant() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fortress */
        Point point1 = new Point(6, 12);
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Connect the headquarter with the fortress */
        Road road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        /* Make sure the headquarter has only privates */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 7);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 1);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 1);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        /* Wait for the fortress to get constructed and populated */
        Utils.waitForBuildingToBeConstructed(fortress0);

        Utils.waitForMilitaryBuildingToGetPopulated(fortress0, 9);

        /* Verify that adding a coin will promote one of each type of soldier */
        Utils.deliverCargo(fortress0, COIN);

        Utils.waitForBuildingToGetAmountOfMaterial(fortress0, COIN, 0);

        Map<Soldier.Rank, Integer> rankCount = new HashMap<>();

        for (Soldier military : fortress0.getHostedSoldiers()) {
            int amount = rankCount.getOrDefault(military.getRank(), 0);

            rankCount.put(military.getRank(), amount + 1);
        }

        assertEquals(rankCount.get(PRIVATE_RANK).intValue(), 6);
        assertEquals(rankCount.getOrDefault(PRIVATE_FIRST_CLASS_RANK, 0).intValue(), 1);
        assertEquals(rankCount.getOrDefault(SERGEANT_RANK, 0).intValue(), 1);
        assertEquals(rankCount.getOrDefault(OFFICER_RANK, 0).intValue(), 1);
        assertEquals(rankCount.getOrDefault(GENERAL_RANK, 0).intValue(), 0);
    }

    @Test
    public void testUpgradeOfPrivatesAndPrivateFirstRankAndOneSergeantAndOneOfficer() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fortress */
        Point point1 = new Point(6, 12);
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Connect the headquarter with the fortress */
        Road road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        /* Make sure the headquarter has only privates */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 6);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 1);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 1);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 1);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        /* Wait for the fortress to get constructed and populated */
        Utils.waitForBuildingToBeConstructed(fortress0);

        Utils.waitForMilitaryBuildingToGetPopulated(fortress0, 9);

        /* Verify that adding a coin will promote one of each type of soldier */
        Utils.deliverCargo(fortress0, COIN);

        Utils.waitForBuildingToGetAmountOfMaterial(fortress0, COIN, 0);

        Map<Soldier.Rank, Integer> rankCount = new HashMap<>();

        for (Soldier military : fortress0.getHostedSoldiers()) {
            int amount = rankCount.getOrDefault(military.getRank(), 0);

            rankCount.put(military.getRank(), amount + 1);
        }

        assertEquals(rankCount.get(PRIVATE_RANK).intValue(), 5);
        assertEquals(rankCount.getOrDefault(PRIVATE_FIRST_CLASS_RANK, 0).intValue(), 1);
        assertEquals(rankCount.getOrDefault(SERGEANT_RANK, 0).intValue(), 1);
        assertEquals(rankCount.getOrDefault(OFFICER_RANK, 0).intValue(), 1);
        assertEquals(rankCount.getOrDefault(GENERAL_RANK, 0).intValue(), 1);
    }

    @Test
    public void testPrivateIsSoldier() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust the inventory of the headquarter */
        Utils.adjustInventoryTo(headquarter, PRIVATE, 1);
        Utils.adjustInventoryTo(headquarter, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter, GENERAL, 0);

        /* Place storage */
        Point point1 = new Point(10, 10);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Connect the barracks to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter.getFlag());

        /* Wait for the barracks to get constructed */
        Utils.waitForBuildingToBeConstructed(barracks0);

        /* Verify that the private walking to the barracks is really a soldier */
        Soldier soldier = Utils.waitForSoldierOutsideBuilding(player0);

        assertTrue(soldier.isSoldier());
    }

    @Test
    public void testPrivateFirstRankIsSoldier() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust the inventory of the headquarter */
        Utils.adjustInventoryTo(headquarter, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter, PRIVATE_FIRST_CLASS, 1);
        Utils.adjustInventoryTo(headquarter, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter, GENERAL, 0);

        /* Place storage */
        Point point1 = new Point(10, 10);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Connect the barracks to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter.getFlag());

        /* Wait for the barracks to get constructed */
        Utils.waitForBuildingToBeConstructed(barracks0);

        /* Verify that the private first class walking to the barracks is really a soldier */
        Soldier soldier = Utils.waitForSoldierOutsideBuilding(player0);

        assertEquals(soldier.getRank(), PRIVATE_FIRST_CLASS_RANK);
        assertTrue(soldier.isSoldier());
    }

    @Test
    public void testSergeantIsSoldier() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust the inventory of the headquarter */
        Utils.adjustInventoryTo(headquarter, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter, SERGEANT, 1);
        Utils.adjustInventoryTo(headquarter, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter, GENERAL, 0);

        /* Place storage */
        Point point1 = new Point(10, 10);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Connect the barracks to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter.getFlag());

        /* Wait for the barracks to get constructed */
        Utils.waitForBuildingToBeConstructed(barracks0);

        /* Verify that the sergeant walking to the barracks is really a soldier */
        Soldier soldier = Utils.waitForSoldierOutsideBuilding(player0);

        assertEquals(soldier.getRank(), SERGEANT_RANK);
        assertTrue(soldier.isSoldier());
    }

    @Test
    public void testOfficerIsSoldier() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust the inventory of the headquarter */
        Utils.adjustInventoryTo(headquarter, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter, OFFICER, 1);
        Utils.adjustInventoryTo(headquarter, GENERAL, 0);

        /* Place storage */
        Point point1 = new Point(10, 10);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Connect the barracks to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter.getFlag());

        /* Wait for the barracks to get constructed */
        Utils.waitForBuildingToBeConstructed(barracks0);

        /* Verify that the officer walking to the barracks is really a soldier */
        Soldier soldier = Utils.waitForSoldierOutsideBuilding(player0);

        assertEquals(soldier.getRank(), OFFICER_RANK);
        assertTrue(soldier.isSoldier());
    }

    @Test
    public void testGeneralIsSoldier() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust the inventory of the headquarter */
        Utils.adjustInventoryTo(headquarter, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter, GENERAL, 1);

        /* Place storage */
        Point point1 = new Point(10, 10);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Connect the barracks to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter.getFlag());

        /* Wait for the barracks to get constructed */
        Utils.waitForBuildingToBeConstructed(barracks0);

        /* Verify that the general walking to the barracks is really a soldier */
        Soldier soldier = Utils.waitForSoldierOutsideBuilding(player0);

        assertEquals(soldier.getRank(), GENERAL_RANK);
        assertTrue(soldier.isSoldier());
    }
}
