/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GuardHouse;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Military.Rank.PRIVATE_FIRST_CLASS_RANK;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.OFFICER_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Military.Rank.SERGEANT_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author johan
 */
public class TestBarracks {

    /*
    TODO:

    add test for upgrade of non-occupied barracks!!

    player's list of buildings is correct
    gamemap mappoint, gamemap buildings

    percentage of upgrade progress is getting updated
    is possible to see if upgrades are possible
    promotion timers are running through upgrades
    it's not possible to deliver too much material to the barracks during upgrade
    barracks being upgraded can be attacked and won
    upgrades finish (and state goes back to normal)

    lack of space can hinder upgrades
    upgrade of regular building
    isUpgrading() in regular buildings

     */

    @Test
    public void testBarracksOnlyNeedsTwoPlanksForConstruction() throws Exception {

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

        /* Deliver two planks */
        Cargo cargo = new Cargo(PLANK, map);

        barracks0.putCargo(cargo);
        barracks0.putCargo(cargo);

        /* Verify that this is enough to construct the barracks */
        for (int i = 0; i < 100; i++) {
            assertTrue(barracks0.underConstruction());

            map.stepTime();
        }

        assertTrue(barracks0.isReady());
    }

    @Test
    public void testBarracksCannotBeConstructedWithOnePlank() throws Exception {

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

        /* Deliver two planks */
        Cargo cargo = new Cargo(PLANK, map);

        barracks0.putCargo(cargo);

        /* Verify that this is enough to construct the barracks */
        for (int i = 0; i < 500; i++) {
            assertTrue(barracks0.underConstruction());

            map.stepTime();
        }

        assertFalse(barracks0.isReady());
    }

    @Test
    public void testBarracksGetPopulatedWhenFinished() throws Exception {

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

        /* Placing road between (7, 21) and (6, 4) */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Verify that a military is sent from the headquarter */
        map.stepTime();

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Military.class);

        Military military = null;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Military) {
                military = (Military)worker;
            }
        }

        assertNotNull(military);

        /* Wait for the military to reach the barracks */
        assertEquals(military.getTarget(), barracks0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, military, barracks0.getPosition());

        assertTrue(military.isInsideBuilding());
    }

    @Test
    public void testBorderIsNotExtendedWhenBarracksIsFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(6, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        Point point2 = new Point(5, 25);

        assertTrue(player0.getBorderPoints().contains(point2));

        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        assertTrue(player0.getBorderPoints().contains(point2));
    }

    @Test
    public void testBorderIsExtendedWhenBarracksIsPopulated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(6, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Verify that a military is sent from the headquarter */
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);

        map.stepTime();

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Military.class);

        Military military = null;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Military) {
                military = (Military)worker;
            }
        }

        assertNotNull(military);

        /* Verify that the border is extended when the military reaches the barracks */
        Point point3 = new Point(5, 25);
        Point point4 = new Point(9, 31);

        assertEquals(military.getTarget(), barracks0.getPosition());
        assertTrue(player0.getBorderPoints().contains(point3));
        assertFalse(player0.getBorderPoints().contains(point4));

        Utils.fastForwardUntilWorkerReachesPoint(map, military, barracks0.getPosition());

        assertTrue(player0.getBorderPoints().contains(point4));
        assertFalse(player0.getBorderPoints().contains(point3));
    }

    @Test
    public void testBarracksOnlyNeedsTwoSoldiers() throws Exception {

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

        Utils.constructHouse(barracks0);

        /* Occupy the barracks with two soldiers */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Verify that the barracks does not need another military */
        assertFalse(barracks0.needsMilitaryManning());
    }

    @Test
    public void testBarracksCannotHoldSoldiersBeforeFinished() throws Exception {

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

        /* Verify that the barracks can't hold soldiers before it's finished */
        assertFalse(barracks0.needsMilitaryManning());

        Military military = new Military(player0, PRIVATE_RANK, map);

        map.placeWorker(military, barracks0);

        try {
            military.enterBuilding(barracks0);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testBarracksCannotHoldMoreThanTwoSoldiers() throws Exception {

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

        Utils.constructHouse(barracks0);

        /* Occupy the barracks with two soldiers */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Verify that the barracks does not need another military */
        Military military = new Military(player0, PRIVATE_RANK, map);

        map.placeWorker(military, barracks0);

        try {
            military.enterBuilding(barracks0);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testBarracksNeedsCoin() throws Exception {

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

        Utils.constructHouse(barracks0);

        assertTrue(barracks0.needsMaterial(COIN));
    }

    @Test
    public void testUnfinishedBarracksNotNeedsCoin() throws Exception {

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

        assertFalse(barracks0.needsMaterial(COIN));
    }

    @Test
    public void testBarracksCanHoldOnlyOneCoin() throws Exception {

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

        Utils.constructHouse(barracks0);

        assertTrue(barracks0.needsMaterial(COIN));

        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);

        barracks0.promiseDelivery(COIN);
        barracks0.putCargo(cargo);

        /* Verify that the barracks can't hold another coin */
        assertFalse(barracks0.needsMaterial(COIN));
        assertEquals(barracks0.getAmount(COIN), 1);

        try {
            barracks0.putCargo(cargo);
            fail();
        } catch (Exception e) {}

        assertEquals(barracks0.getAmount(COIN), 1);
    }

    @Test
    public void testStorageStopsDeliveringCoinsWhenBarracksIsFull() throws Exception {

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

        Utils.constructHouse(barracks0);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Fill up headquarter with coins */
        Utils.adjustInventoryTo(headquarter0, COIN, 10);

        /* Wait for the barracks to get a coin */
        for (int i = 0; i < 1000; i++) {
            if (barracks0.getAmount(COIN) == 1) {
                break;
            }

            map.stepTime();
        }

        assertEquals(barracks0.getAmount(COIN), 1);

        /* Disable promotion */
        barracks0.disablePromotions();

        /* Verify that no more coin is delivered */
        Courier courier = road0.getCourier();
        for (int i = 0; i < 1000; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial().equals(COIN)) {
                fail();
            }

            map.stepTime();
        }
    }

    @Test
    public void testPrivateIsPromotedWhenCoinIsAvailable() throws Exception {

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

        Utils.constructHouse(barracks0);

        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);

        barracks0.putCargo(cargo);

        /* Occupy the barracks with one private */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Verify that the private is promoted at the right time */
        for (int i = 0; i < 100; i++) {
            assertEquals(military.getRank(), PRIVATE_RANK);
            map.stepTime();
        }

        assertEquals(military.getRank(), PRIVATE_FIRST_CLASS_RANK);
    }

    @Test
    public void testOnlyOnePrivateIsPromoted() throws Exception {

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

        Utils.constructHouse(barracks0);

        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);

        barracks0.putCargo(cargo);

        /* Occupy the barracks with one private */
        Military military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Military military2 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Wait for the promotion to happen */
        Utils.fastForward(100, map);

        assertTrue((military1.getRank() == PRIVATE_FIRST_CLASS_RANK && military2.getRank() == PRIVATE_RANK) ||
                   (military1.getRank() == PRIVATE_RANK  && military2.getRank() == PRIVATE_FIRST_CLASS_RANK));
    }

    @Test
    public void testTimeSpentWithCoinButNoMilitaryDoesNotSpeedUpPromotion() throws Exception {

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

        Utils.constructHouse(barracks0);

        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);

        barracks0.putCargo(cargo);

        /* Wait before the barracks is populated */
        Utils.fastForward(200, map);

        /* Occupy the barracks with one private */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Verify that it still takes the same time for the private to get promoted */
        Utils.fastForward(99, map);

        assertEquals(military.getRank(), PRIVATE_RANK);

        map.stepTime();

        assertEquals(military.getRank(), PRIVATE_FIRST_CLASS_RANK);
    }

    @Test
    public void testPromotionConsumesCoin() throws Exception {

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

        Utils.constructHouse(barracks0);

        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);

        barracks0.putCargo(cargo);

        /* Occupy the barracks with one private */
        Military military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Verify that the promotion consumes the coin */
        assertEquals(barracks0.getAmount(COIN), 1);

        Utils.fastForward(100, map);

        assertEquals(barracks0.getAmount(COIN), 0);
    }

    @Test
    public void testBarracksWithNoPromotionPossibleDoesNotConsumeCoin() throws Exception {

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

        Utils.constructHouse(barracks0);

        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);

        barracks0.putCargo(cargo);

        /* Occupy the barracks with one private */
        Military military1 = Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Military military2 = Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Verify that the promotion consumes the coin */
        assertEquals(barracks0.getAmount(COIN), 1);

        Utils.fastForward(100, map);

        assertEquals(barracks0.getAmount(COIN), 1);
    }

    @Test
    public void testCanDisableCoinsToBarracks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Add coins to the headquarter */
        headquarter0.putCargo(new Cargo(COIN, map));

        /* Placing barracks */
        Point point22 = new Point(6, 12);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        Utils.constructHouse(barracks0);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Occupy the road */
        Courier courier = Utils.occupyRoad(road0, map);

        /* Verify that promotions are enabled initially */
        assertTrue(barracks0.isPromotionEnabled());

        /* Disable coins to the barracks and verify that it doesn't need coins */
        barracks0.disablePromotions();

        assertFalse(barracks0.needsMaterial(COIN));

        /* Verify that promotions are disabled */
        assertFalse(barracks0.isPromotionEnabled());

        /* Verify that no coins are delivered */
        Utils.verifyNoDeliveryOfMaterial(map, road0);
    }

    @Test
    public void testCanResumeDeliveryOfCoinsToBarracks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Add coins to the headquarter */
        headquarter0.putCargo(new Cargo(COIN, map));

        /* Placing barracks */
        Point point22 = new Point(6, 12);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        Utils.constructHouse(barracks0);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Occupy the road */
        Courier courier = Utils.occupyRoad(road0, map);

        /* Disable coins to the barracks and verify that it doesn't need coins */
        barracks0.disablePromotions();

        assertFalse(barracks0.needsMaterial(COIN));

        /* Verify that no coins are delivered */
        Utils.verifyNoDeliveryOfMaterial(map, road0);

        /* Resume delivery of coins to the barracks */
        barracks0.enablePromotions();

        /* Verify that the barracks needs coins again */
        assertTrue(barracks0.needsMaterial(COIN));

        /* Verify that promotions are enabled again */
        assertTrue(barracks0.isPromotionEnabled());

        /* Verify that a coin is delivered to the barracks */
        Utils.verifyDeliveryOfMaterial(map, road0);
    }

    @Test
    public void testOccupiedBarracksCanBeEvacuated() throws Exception {

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

        /* Connect headquarter and barracks */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Evacuate the barracks and verify that the military leaves the barracks */
        assertTrue(military.isInsideBuilding());

        barracks0.evacuate();

        map.stepTime();

        assertFalse(military.isInsideBuilding());
    }

    @Test
    public void testEvacuatedMilitaryReturnsToStorage() throws Exception {

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

        /* Connect headquarter and barracks */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Evacuate the barracks */
        assertTrue(military.isInsideBuilding());

        barracks0.evacuate();

        map.stepTime();

        assertFalse(military.isInsideBuilding());
        assertEquals(barracks0.getNumberOfHostedMilitary(), 0);

        /* Verify that the evacuated military returns to the storage */
        assertEquals(military.getTarget(), headquarter0.getPosition());
        int amount = headquarter0.getAmount(PRIVATE);

        Utils.fastForwardUntilWorkerReachesPoint(map, military, military.getTarget());

        assertTrue(military.isInsideBuilding());
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    }

    @Test
    public void testEvacuatedSoldierReturnsOffroadWhenNotConnected() throws Exception {

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

        /* Occupy the barracks */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Evacuate the barracks */
        assertTrue(military.isInsideBuilding());

        barracks0.evacuate();

        map.stepTime();

        assertFalse(military.isInsideBuilding());

        /* Verify that the evacuated military returns to the storage */
        assertEquals(military.getTarget(), headquarter0.getPosition());
        int amount = headquarter0.getAmount(PRIVATE);

        Utils.fastForwardUntilWorkerReachesPoint(map, military, military.getTarget());

        assertTrue(military.isInsideBuilding());
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    }

    @Test
    public void testNoMilitaryIsDispatchedToEvacuatedBarracks() throws Exception {

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

        /* Connect headquarters and barracks */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Verify that the barracks are evacuated */
        assertFalse(barracks0.isEvacuated());

        /* Evacuate the barracks */
        barracks0.evacuate();

        /* Verify that the barracks are evacuated */
        assertTrue(barracks0.isEvacuated());

        /* Verify that no soldiers are assigned to the barracks */
        for (int i = 0; i < 200; i++) {
            assertEquals(barracks0.getNumberOfHostedMilitary(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testEvacuationCanBeCanceled() throws Exception {

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

        /* Occupy the barracks */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Evacuate the barracks */
        assertTrue(military.isInsideBuilding());

        barracks0.evacuate();

        map.stepTime();

        assertFalse(military.isInsideBuilding());

        /* Wait for the evacuated military to return to the storage */
        assertEquals(military.getTarget(), headquarter0.getPosition());
        int amount = headquarter0.getAmount(PRIVATE);

        Utils.fastForwardUntilWorkerReachesPoint(map, military, military.getTarget());

        assertTrue(military.isInsideBuilding());
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);

        /* Cancel evacuation */
        assertFalse(barracks0.needsMilitaryManning());

        barracks0.cancelEvacuation();

        assertTrue(barracks0.needsMilitaryManning());

        /* Verify that the barracks are not evacuated */
        assertFalse(barracks0.isEvacuated());
    }

    @Test
    public void testMilitaryGoesBackToStorageWhenBarracksIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(8, 8);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Destroy the barracks */
        assertEquals(barracks0.getNumberOfHostedMilitary(), 1);

        barracks0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0);

        int amount = headquarter0.getAmount(PRIVATE);

        assertNotNull(military);
        assertEquals(military.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, military, headquarter0.getPosition());

        /* Verify that the military is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    }

    @Test
    public void testMilitaryGoesBackOnToStorageOnRoadsIfPossibleWhenBarracksIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(8, 8);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Destroy the barracks */
        assertEquals(barracks0.getNumberOfHostedMilitary(), 1);

        barracks0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertNotNull(military);
        assertEquals(military.getTarget(), headquarter0.getPosition());

        /* Verify that the military plans to use the roads */
        boolean firstStep = true;
        for (Point point : military.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testProductionCannotBeResumedInBarracks() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(8, 8);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Verify that production cannot be resumed in barracks */
        try {
            barracks0.resumeProduction();

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testFieldOfViewIsExtendedWhenBarracksIsOccupied() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Placing headquarter */
        Point point25 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Verify that the field of view remains the same until the barracks gets occupied */
        Point pointInOldFOV = new Point(33, 5);
        Point pointInNewFOV = new Point(45, 5);

        for (int i = 0; i < 1000; i++) {
            if (barracks0.getNumberOfHostedMilitary() == 0) {
                assertTrue(player0.getDiscoveredLand().contains(pointInOldFOV));
                assertFalse(player0.getDiscoveredLand().contains(pointInNewFOV));
            } else {
                break;
            }

            map.stepTime();
        }

        /* Verify that the field of view is updated when a military has entered the barracks */
        assertTrue(barracks0.getNumberOfHostedMilitary() > 0);
        assertTrue(player0.getDiscoveredLand().contains(pointInNewFOV));
        assertTrue(player0. getDiscoveredLand().contains(pointInOldFOV));
    }

    @Test
    public void testBarracksCanBeUpgraded() throws Exception, InvalidUserActionException {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0);

        /* Upgrade the barracks */
        assertFalse(barracks0.isUpgrading());

        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Verify that the upgrade isn't too quick */
        for (int i = 0; i < 100; i++) {

            assertTrue(barracks0.isUpgrading());
            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        Building upgradedBuilding = map.getBuildingAtPoint(barracks0.getPosition());

        assertFalse(map.getBuildings().contains(barracks0));
        assertTrue(map.getBuildings().contains(upgradedBuilding));
        assertFalse(player0.getBuildings().contains(barracks0));
        assertTrue(player0.getBuildings().contains(upgradedBuilding));
        assertNotNull(upgradedBuilding);
        assertFalse(upgradedBuilding.isUpgrading());
        assertEquals(upgradedBuilding.getClass(), GuardHouse.class);
    }

    @Test
    public void testUnfinishedBarracksCannotBeUpgraded() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(13, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Upgrade the barracks */
        try {
            barracks0.upgrade();

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testBurningBarracksCannotBeUpgraded() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(13, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Tear down the barracks so it's on fire */
        barracks0.tearDown();

        /* Upgrade the barracks */
        try {
            barracks0.upgrade();

            fail();
        } catch (InvalidUserActionException e) {}
    }

    @Test
    public void testCannotUpgradeBarracksWithoutMaterial() throws Exception, InvalidUserActionException {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0);

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials but not enough for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Verify that the upgrade cannot happen without the required material */
        for (int i = 0; i < 1000; i++) {

            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        /* Add the last required material for the upgrade */
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);

        /* Step time once and verify that the barracks is upgraded */
        map.stepTime();

        assertNotEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));
        assertEquals(map.getBuildingAtPoint(barracks0.getPosition()).getClass(), GuardHouse.class);
    }

    @Test
    public void testCannotUpgradeBarracksBeingUpgraded() throws Exception, InvalidUserActionException {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(13, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0);

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Verify that the barracks can't get upgraded again */
        try {
            barracks0.upgrade();

            fail();
        } catch (InvalidUserActionException e) {}
    }

    @Test
    public void testUpgradingCausesMaterialToGetDelivered() throws Exception, InvalidUserActionException {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Make sure there is material for upgrading */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10);
        Utils.adjustInventoryTo(headquarter0, STONE, 10);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);

        assertFalse(barracks0.needsMilitaryManning());
        assertFalse(barracks0.needsMaterial(PRIVATE));

        /* Place the courier on the road */
        assertNotNull(road0.getCourier());

        Courier courier0 = road0.getCourier();

        /* Verify that the barracks doesn't need stone before the upgrade */
        assertFalse(barracks0.needsMaterial(STONE));

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Verify that the barracks needs stone */
        assertTrue(barracks0.needsMaterial(STONE));

        /* Verify that the courier picks up a stone */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier0);

        assertEquals(courier0.getCargo().getMaterial(), STONE);

        /* Verify that the courier delivers the stone */
        assertEquals(courier0.getCargo().getTarget(), barracks0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, barracks0.getPosition());

        assertNull(courier0.getCargo());

        /* Verify that the courier picks up the second stone */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier0);

        assertEquals(courier0.getCargo().getMaterial(), STONE);

        /* Verify that the courier delivers the stone */
        assertEquals(courier0.getCargo().getTarget(), barracks0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, barracks0.getPosition());

        assertNull(courier0.getCargo());

        /* Verify that the courier picks up the third stone */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier0);

        assertEquals(courier0.getCargo().getMaterial(), STONE);

        /* Verify that the courier delivers the stone */
        assertEquals(courier0.getCargo().getTarget(), barracks0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, barracks0.getPosition());

        assertNull(courier0.getCargo());

        /* Verify that the courier doesn't deliver anything else to the barracks */
        for (int i = 0; i < 1000; i++) {

            assertTrue(courier0.getCargo() == null || !courier0.getCargo().getTarget().equals(barracks0));

            map.stepTime();
        }
    }

    @Test
    public void testOccupiedBarracksIsOccupiedAfterUpgrade() throws Exception, InvalidUserActionException {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0);

        assertTrue(barracks0.isOccupied());

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Verify that the upgrade isn't too quick */
        for (int i = 0; i < 100; i++) {

            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        /* Verify that the upgraded building is also occupied */
        Building guardHouse0 = map.getBuildingAtPoint(barracks0.getPosition());

        assertTrue(guardHouse0.isOccupied());
        assertEquals(guardHouse0.getNumberOfHostedMilitary(), 1);
    }

    @Test
    public void testCoinRemainsAfterUpgrade() throws Exception, InvalidUserActionException {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Put a coin in the building */
        Cargo coinCargo = new Cargo(COIN, map);

        barracks0.promiseDelivery(COIN);

        barracks0.putCargo(coinCargo);

        assertEquals(barracks0.getAmount(COIN), 1);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        /* Verify that the coin is still in the building */
        Building guardHouse0 = map.getBuildingAtPoint(barracks0.getPosition());

        assertEquals(guardHouse0.getAmount(COIN), 1);
    }

    @Test
    public void testBuildingDuringUpgradeCanBeDestroyed() throws Exception, InvalidUserActionException {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0);

        assertTrue(barracks0.isOccupied());

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Upgrade for a while */
        for (int i = 0; i < 10; i++) {

            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        /* Verify that the building can be destroyed */
        barracks0.tearDown();

        assertTrue(barracks0.isBurningDown());
    }

    @Test
    public void testPlayerIsCorrectAfterUpgrade() throws Exception, InvalidUserActionException {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        Player player2 = new Player("Player 2", java.awt.Color.BLACK);
        List<Player> players = new ArrayList<>();
        players.add(player2);
        players.add(player0);
        players.add(player1);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0);

        assertTrue(barracks0.isOccupied());

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Put a coin in the building */
        Cargo coinCargo = new Cargo(COIN, map);

        barracks0.promiseDelivery(COIN);

        barracks0.putCargo(coinCargo);

        assertEquals(barracks0.getAmount(COIN), 1);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {
            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        /* Verify that the player is set correctly in the upgraded building */
        Building guardHouse0 = map.getBuildingAtPoint(barracks0.getPosition());

        assertEquals(guardHouse0.getPlayer(), player0);
    }

    @Test
    public void testCanHostRightNumberOfSoldiersAfterUpgraded() throws Exception, InvalidUserActionException {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0);

        assertTrue(barracks0.isOccupied());

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        /* Verify that two more soldiers can be hosted in the building */
        Building guardHouse0 = map.getBuildingAtPoint(barracks0.getPosition());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);
    }

    @Test
    public void testBorderIsExpandedAfterUpgrade() throws Exception, InvalidUserActionException {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0);

        assertTrue(barracks0.isOccupied());

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Verify the border before the upgrade */
        Point point27 = new Point(37, 5);
        Point point28 = new Point(39, 5);

        assertTrue(player0.getBorderPoints().contains(point27));
        assertFalse(player0.getBorderPoints().contains(point28));
        assertFalse(player0.isWithinBorder(point27));

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        /* Verify that the border is expanded after the upgrade */
        assertFalse(player0.getBorderPoints().contains(point27));
        assertTrue(player0.getBorderPoints().contains(point28));
        assertTrue(player0.isWithinBorder(point27));
    }

    @Test
    public void testFlagIsCorrectAfterUpgrade() throws Exception, InvalidUserActionException {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0);

        assertTrue(barracks0.isOccupied());

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        /* Verify that the flag is correct after the upgrade */
        Building buildingAfterUpgrade = map.getBuildingAtPoint(point26);

        assertNotNull(buildingAfterUpgrade);
        assertNotNull(buildingAfterUpgrade.getFlag());
        assertEquals(buildingAfterUpgrade.getFlag().getPosition(), point26.downRight());
    }

    @Test
    public void testOccupiedBuildingRemainsOccupiedDuringUpgrade() throws Exception, InvalidUserActionException {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0);

        assertTrue(barracks0.isOccupied());

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Verify that the barracks is still occupied */
        assertTrue(barracks0.isOccupied());

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Verify that the barracks is occupied during the upgrade */
        for (int i = 0; i < 100; i++) {

            /* Verify that the barracks is still occupied */
            assertTrue(barracks0.isOccupied());

            map.stepTime();
        }
    }

    @Test
    public void testEvacuatedBuildingKeepsSendingHomeMilitary() throws Exception {

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

        /* Evacuate the barracks */
        barracks0.evacuate();

        assertTrue(barracks0.isEvacuated());
        assertFalse(barracks0.needsMilitaryManning());

        /* Connect headquarter and barracks */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Occupy the barracks */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Verify that the military comes out immediately */

        map.stepTime();

        assertFalse(military.isInsideBuilding());
        assertEquals(barracks0.getNumberOfHostedMilitary(), 0);
    }

    @Test
    public void testCanUpgradeAfterDisablingPromotions() throws Exception, InvalidUserActionException {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0);

        assertTrue(barracks0.isOccupied());

        /* Disable promotions */
        barracks0.disablePromotions();

        assertFalse(barracks0.isPromotionEnabled());

        /* Verify that the barracks can be upgraded */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        /* Verify that the barracks is upgraded */
        Building buildingAfterUpgrade = map.getBuildingAtPoint(point26);

        assertNotNull(buildingAfterUpgrade);
        assertEquals(buildingAfterUpgrade.getClass(), GuardHouse.class);
    }

    @Test
    public void testEvacuatedMilitaryGetsAddedCorrectlyInStorage() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Keep track of the original amount of soldiers */
        int originalAmount = Utils.getAmountMilitary(headquarter0);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Wait for a military to start walking to the barracks */
        Military military = null;
        for (int i = 0; i < 1000; i++) {
            for (Worker worker : map.getWorkers()) {
                if (worker instanceof Military && worker.getTarget().equals(barracks0.getPosition())) {
                    military = (Military)worker;
                    break;
                }
            }

            if (military != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(military);
        assertTrue(Utils.getAmountMilitary(headquarter0) < originalAmount);

        /* Evacuate the barracks */
        barracks0.evacuate();

        /* Wait for the military to reach the barracks */
        assertEquals(military.getTarget(), barracks0.getPosition());
        assertEquals(barracks0.getNumberOfHostedMilitary(), 0);
        assertFalse(barracks0.isOccupied());

        Utils.fastForwardUntilWorkerReachesPoint(map, military, barracks0.getPosition());

        assertTrue(barracks0.isOccupied());

        /* Verify that the military walks back and the barracks remains occupied */
        for (int i = 0; i < 1000; i++) {
            if (barracks0.getNumberOfHostedMilitary() == 0) {
                break;
            }

            map.stepTime();
        }

        for (int i = 0; i < 100; i++) {

            assertTrue(barracks0.isOccupied());
            assertEquals(barracks0.getNumberOfHostedMilitary(), 0);

            map.stepTime();
        }

        /* Verify that the evacuated soldiers are added correctly */
        assertEquals(Utils.getAmountMilitary(headquarter0), originalAmount);
    }

    @Test
    public void testCanCancelEvacuationAndRefillBarracks() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Add extra soldiers to inventory */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 10);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Wait for a military to start walking to the barracks */
        Military military = null;
        for (int i = 0; i < 1000; i++) {
            for (Worker worker : map.getWorkers()) {
                if (worker instanceof Military && worker.getTarget().equals(barracks0.getPosition())) {
                    military = (Military)worker;
                    break;
                }
            }

            if (military != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(military);

        /* Evacuate the barracks */
        barracks0.evacuate();

        /* Wait for the military to reach the barracks */
        assertEquals(military.getTarget(), barracks0.getPosition());
        assertEquals(barracks0.getNumberOfHostedMilitary(), 0);
        assertFalse(barracks0.isOccupied());

        Utils.fastForwardUntilWorkerReachesPoint(map, military, barracks0.getPosition());

        assertTrue(barracks0.isOccupied());

        /* Wait for the military to walk out */
        for (int i = 0; i < 1000; i++) {
            if (barracks0.getNumberOfHostedMilitary() == 0) {
                break;
            }

            map.stepTime();
        }

        assertFalse(military.isInsideBuilding());
        assertEquals(barracks0.getNumberOfHostedMilitary(), 0);
        assertEquals(military.getTarget(), headquarter0.getPosition());

        /* Verify that it's possible to cancel evacuation and fill up with soldiers again */
        barracks0.cancelEvacuation();

        assertFalse(barracks0.isEvacuated());

        for (int i = 0; i < 500; i++) {

            if (barracks0.getNumberOfHostedMilitary() == 2) {
                break;
            }

            map.stepTime();
        }

        assertEquals(barracks0.getNumberOfHostedMilitary(), 2);
    }

    @Test
    public void testUpgradeDoesNotDestroyNearbyHouses() throws Exception, InvalidUserActionException {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0);

        /* Place a second barracks */
        Point point1 = new Point(26, 6);
        Building barracks1 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct the barracks */
        Utils.constructHouse(barracks1);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        /* Place regular building */
        Point point2 = new Point(30, 6);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2);

        /* Connect the buildings with a road */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks1.getFlag(), foresterHut0.getFlag());

        /* Evacuate the barracks and wait for the barracks to become empty */
        barracks1.evacuate();

        for (int i = 0; i < 1000; i++) {

            if (barracks1.getNumberOfHostedMilitary() == 0) {
                break;
            }

            map.stepTime();
        }

        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);

        /* Upgrade the barracks */
        barracks1.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks1.promiseDelivery(STONE);
        barracks1.promiseDelivery(STONE);
        barracks1.promiseDelivery(STONE);

        barracks1.putCargo(stoneCargo);
        barracks1.putCargo(stoneCargo);
        barracks1.putCargo(stoneCargo);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(barracks1, map.getBuildingAtPoint(barracks1.getPosition()));

            map.stepTime();
        }

        /* Verify that the forester hut and the road remains */
        assertEquals(map.getBuildingAtPoint(barracks1.getPosition()).getClass(), GuardHouse.class);
        assertTrue(map.isBuildingAtPoint(foresterHut0.getPosition()));
        assertEquals(map.getBuildingAtPoint(foresterHut0.getPosition()), foresterHut0);
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testUnoccupiedBuildingRemainsUnoccupiedDuringAndAfterUpgrade() throws Exception, InvalidUserActionException {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Upgrade the barracks */
        assertFalse(barracks0.isOccupied());

        barracks0.upgrade();

        /* Verify that the barracks is still unoccupied */
        assertFalse(barracks0.isOccupied());

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Verify that the barracks is unoccupied during and after the upgrade */
        Point point1 = new Point(25, 5);
        Point point2 = new Point(27, 5);

        for (int i = 0; i < 100; i++) {

            /* Verify that the barracks is still occupied */
            assertFalse(barracks0.isOccupied());
            assertTrue(player0.getBorderPoints().contains(point1));
            assertFalse(player0.isWithinBorder(point2));

            map.stepTime();
        }

        assertEquals(map.getBuildingAtPoint(barracks0.getPosition()).getClass(), GuardHouse.class);
        assertFalse(map.getBuildingAtPoint(barracks0.getPosition()).isOccupied());
        assertTrue(player0.getBorderPoints().contains(point1));
        assertFalse(player0.isWithinBorder(point2));
    }

    @Test
    public void testUpgradeOfBuildingWithMilitaryDoesNotCauseOverAllocation() throws Exception, InvalidUserActionException {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Fill the barracks with soldiers */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks0);

        assertEquals(barracks0.getNumberOfHostedMilitary(), 2);

        /* Make sure there are enough soldiers in the headquarter */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 200);

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Wait for the upgrade to happen */
        assertEquals(barracks0.getNumberOfHostedMilitary(), 2);

        barracks0 = Utils.waitForBuildingToGetUpgraded(barracks0);

        assertEquals(barracks0.getNumberOfHostedMilitary(), 2);
        assertEquals(map.getBuildingAtPoint(barracks0.getPosition()), barracks0);
        assertEquals(barracks0.getMaxHostedMilitary(), 3);
        /* Verify that only one military is sent out to occupy the building */

        /* Wait for the military */
        assertTrue(barracks0.needsMilitaryManning());
        assertEquals(barracks0.getNumberOfHostedMilitary(), 2);

        Military military0 = null;
        for (int i = 0; i < 2000; i++) {

            military0 = Utils.findMilitaryOutsideBuilding(player0);

            if (military0 != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(military0);
        assertFalse(barracks0.needsMilitaryManning());

        /* Wait for the military to get to the barracks and verify no other military is sent out */
        for (int i = 0; i < 2000; i++) {

            if (military0.getPosition().equals(barracks0.getPosition())) {
                break;
            }

            assertEquals(Utils.findSoldiersOutsideBuilding(player0).size(), 1);

            map.stepTime();
        }

        assertEquals(barracks0.getNumberOfHostedMilitary(), 3);

        /* Verify that no more soldiers are sent */
        for (int i = 0; i < 2000; i++) {

            assertNull(Utils.findMilitaryOutsideBuilding(player0));

            map.stepTime();
        }
    }

    @Test
    public void testSoldiersOfDifferentTypesArePromotedInParallel() throws Exception {

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

        Utils.constructHouse(barracks0);

        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);

        barracks0.putCargo(cargo);

        /* Occupy the barracks with one private */
        Military military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Military military2 = Utils.occupyMilitaryBuilding(SERGEANT_RANK, barracks0);

        /* Wait for the promotion to happen */
        Utils.fastForward(100, map);

        assertTrue((military1.getRank() == OFFICER_RANK && military2.getRank() == PRIVATE_FIRST_CLASS_RANK) ||
                   (military1.getRank() == PRIVATE_FIRST_CLASS_RANK && military2.getRank() == OFFICER_RANK));
    }

    @Test
    public void testUpgradedBarracksGetsPopulatedFully() throws Exception, InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Add privates to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 20);

        /* Placing barracks */
        Point point22 = new Point(6, 12);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        Utils.constructHouse(barracks0);

        /* Connect the barracks to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to get occupied */
        for (int i = 0; i < 1000; i++) {

            if (barracks0.getNumberOfHostedMilitary() == 2) {
                break;
            }

            map.stepTime();
        }

        assertEquals(barracks0.getNumberOfHostedMilitary(), 2);

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Wait for the upgrade to happen */
        barracks0 = Utils.waitForBuildingToGetUpgraded(barracks0);

        assertEquals(map.getBuildingAtPoint(barracks0.getPosition()), barracks0);
        assertEquals(barracks0.getMaxHostedMilitary(), 3);

        /* Verify that the building gets fully occupied */
        for (int i = 0; i < 500; i++) {

            if (barracks0.getNumberOfHostedMilitary() == 3) {
                break;
            }

            map.stepTime();
        }

        assertEquals(barracks0.getNumberOfHostedMilitary(), 3);
    }

    @Test
    public void testCannotStopProductionInBarracks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 17);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Verify that it's not possible to stop production */
        try {
            barracks0.stopProduction();
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testBarracksCannotProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(7, 9);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Populate the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Verify that the barracks can't produce */
        assertFalse(barracks0.canProduce());
    }


    @Test
    public void testBarracksReportsCorrectOutput() throws Exception {

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

        /* Verify that the reported output is correct */
        assertEquals(barracks0.getProducedMaterial().length, 0);
    }

    @Test
    public void testBarracksReportsCorrectMaterialsNeededForConstruction() throws Exception {

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

        /* Verify that the reported needed construction material is correct */
        assertEquals(barracks0.getMaterialNeeded().size(), 1);
        assertTrue(barracks0.getMaterialNeeded().contains(PLANK));
        assertEquals(barracks0.getTotalAmountNeeded(PLANK), 2);

        for (Material material : Material.values()) {
            if (material == PLANK) {
                continue;
            }

            assertEquals(barracks0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testBarracksReportsCorrectMaterialsNeededForProduction() throws Exception {

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

        /* Verify that the reported needed construction material is correct */
        assertEquals(barracks0.getMaterialNeeded().size(), 1);
        assertEquals(barracks0.getTotalAmountNeeded(COIN), 1);

        for (Material material : Material.values()) {
            if (material == COIN) {
                continue;
            }

            assertEquals(barracks0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testHostedMilitaryListIsEmptyForBarracksUnderConstruction() throws Exception {

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

        /* Verify that the list of hosted soldiers is empty */
        assertEquals(0, barracks0.getHostedMilitary().size());
    }

    @Test
    public void testHostedMilitaryListIsEmptyForEmptyBarracks() throws Exception {

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

        /* Construct barracks */
        Utils.constructHouse(barracks0);

        /* Verify that the list of hosted soldiers is empty */
        assertEquals(0, barracks0.getHostedMilitary().size());
    }

    @Test
    public void testAddingMilitaryUpsHostedMilitaryList() throws Exception {

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

        /* Construct barracks */
        Utils.constructHouse(barracks0);

        /* Add one military */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0);

        /* Verify that the list of hosted soldiers increased empty */
        assertEquals(1, barracks0.getHostedMilitary().size());
        assertEquals(barracks0.getHostedMilitary().get(0).getRank(), PRIVATE_RANK);
    }

    @Test
    public void testRankIsCorrectInHostedMilitaryList() throws Exception {

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

        /* Construct barracks */
        Utils.constructHouse(barracks0);

        /* Add one military */
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, 1, barracks0);

        /* Verify that the rank for the hosted military is correct */
        assertEquals(barracks0.getHostedMilitary().get(0).getRank(), SERGEANT_RANK);
    }

    @Test
    public void testBorderForBarracksIsCorrect() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter */
        Point point0 = new Point(30, 30);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(25, 23);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Verify that the border around the barracks is hexagon shaped and the middle of each line is 8 steps away from the center of the headquarter
        Border:

                -8, +8  -------  +8, +8
                  /                  \
            -16, 0        H          16, 0
                  \                  /
                -8, -8  -------  +8, +8

         */

        int radius = 8;
        Set<Point> barracksHexagonBorder = Utils.getHexagonBorder(barracks0.getPosition(), radius);
        Set<Point> headquarterHexagonBorder = Utils.getHexagonBorder(headquarter0.getPosition(), 9);

        /* Verify that all points in the hexagon are part of the actual border */
        Set<Point> border = player0.getBorderPoints();
        for (Point point : barracksHexagonBorder) {

            /* Ignore points that are within the player's land */
            if (player0.getLandInPoints().contains(point)) {
                continue;
            }

            assertTrue(border.contains(point));
        }

        /* Verify that all points in the actual border are part of the hexagon border */
        for (Point point : border) {

            /* Ignore points that are part of the hexagon around the headquarter */
            if (headquarterHexagonBorder.contains(point)) {
                continue;
            }

            assertTrue(barracksHexagonBorder.contains(point));
        }
    }

    @Test
    public void testLandForBarracksIsCorrect() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter */
        Point point0 = new Point(30, 30);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(25, 23);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Verify that the land of the headquarter is hexagon shaped and the middle of each line is 9 steps away from the center of the headquarter
        Land

                -8, +8  -------  +8, +8
                  /                  \
            -16, 0        H          16, 0
                  \                  /
                -8, -8  -------  +8, +8

         */
        Set<Point> area = Utils.getAreaInsideHexagon(7, barracks0.getPosition());

        /* Verify that all points in the hexagon land are part of the actual land */
        Collection<Point> land = barracks0.getDefendedLand();
        for (Point point : land) {

            /* Ignore points that are part of the headquarters land */
            if (headquarter0.getDefendedLand().contains(point)) {
                continue;
            }

            assertTrue(area.contains(point));
        }

        /* Verify that all points in the actual land are part of the hexagon land */
        for (Point point : area) {
            assertTrue(land.contains(point));
        }
    }

    @Test
    public void testDiscoveredLandForBarracksIsCorrect() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter */
        Point point0 = new Point(30, 30);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(25, 23);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Verify that the discovered land of the barracks is hexagon shaped and the middle of each line is 8 + 4 steps away
        from the center of the headquarter

         Land

                -8, +8  -------  +8, +8
                  /                  \
            -16, 0        H          16, 0
                  \                  /
                -8, -8  -------  +8, +8

         */
        Set<Point> barracksHexagonDiscoveredArea = Utils.getAreaInsideHexagon(12, barracks0.getPosition());
        Set<Point> headquarterDiscoveredLand = Utils.getAreaInsideHexagon(13, headquarter0.getPosition());

        /* Verify that all points in the hexagon land are part of the actual land */
        Collection<Point> discoveredLand = player0.getDiscoveredLand();
        for (Point point : discoveredLand) {

            /* Ignore points within the discovered land for the headquarter */
            if (headquarterDiscoveredLand.contains(point)) {
                continue;
            }

            assertTrue(barracksHexagonDiscoveredArea.contains(point));
        }

        /* Verify that all points in the actual land are part of the hexagon land */
        for (Point point : barracksHexagonDiscoveredArea) {
            assertTrue(discoveredLand.contains(point));
        }
    }

    @Test
    public void testDiscoveredLandForPlayerCannotBeOutsideTheMap() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(3, 3);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Verify that the discovered land is only inside the map */
        for (Point point : player0.getDiscoveredLand()) {
            assertTrue(point.x >= 0);
            assertTrue(point.y >= 0);
        }
    }

    @Test
    public void testOwnedLandForPlayerCannotBeOutsideTheMap() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(3, 3);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Verify that the discovered land is only inside the map */
        for (Point point : player0.getLandInPoints()) {
            assertTrue(point.x >= 0);
            assertTrue(point.y >= 0);
        }
    }
}
