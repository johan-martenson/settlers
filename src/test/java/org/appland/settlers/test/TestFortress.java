/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
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
import static org.appland.settlers.model.Military.Rank.CORPORAL_RANK;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Military.Rank.SERGEANT_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author johan
 */
public class TestFortress {

    @Test
    public void testFortressNeedsFourPlanksAndSevenStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Deliver two planks and three stones */
        Cargo cargo = new Cargo(PLANK, map);

        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the fortress */
        for (int i = 0; i < 200; i++) {
            assertTrue(fortress0.underConstruction());

            map.stepTime();
        }

        assertTrue(fortress0.isReady());
    }

    @Test
    public void testFortressCannotBeConstructedWithOnePlankTooLittle() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Deliver one plank and three stones */
        Cargo cargo = new Cargo(PLANK, map);

        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);

        /* Verify that the fortress needs a plank */
        assertTrue(fortress0.needsMaterial(PLANK));

        /* Verify that this is not enough to construct the fortress */
        for (int i = 0; i < 500; i++) {
            assertTrue(fortress0.underConstruction());

            map.stepTime();
        }

        assertFalse(fortress0.isReady());
    }

    @Test
    public void testFortressCannotBeConstructedWithOneStoneTooLittle() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Deliver four planks and six stones */
        Cargo cargo = new Cargo(PLANK, map);

        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);

        /* Verify that the fortress needs a stone */
        assertTrue(fortress0.needsMaterial(STONE));

        /* Verify that this is not enough to construct the fortress */
        for (int i = 0; i < 500; i++) {
            assertTrue(fortress0.underConstruction());

            map.stepTime();
        }

        assertFalse(fortress0.isReady());
    }

    @Test
    public void testFortressGetPopulatedWhenFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Connect the fortress with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, fortress0.getFlag(), headquarter0.getFlag());

        /* Wait for the fortress to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(fortress0);

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

        /* Wait for the military to reach the fortress */
        assertEquals(military.getTarget(), fortress0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, military, fortress0.getPosition());

        assertTrue(military.isInsideBuilding());
    }

    @Test
    public void testBorderIsNotExtendedWhenFortressIsFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(5, 23);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        /* Wait for the fortress to finish construction */
        Point point3 = new Point(6, 24);
        assertTrue(player0.getBorderPoints().contains(point3));

        Utils.fastForwardUntilBuildingIsConstructed(fortress0);

        assertTrue(player0.getBorderPoints().contains(point3));
    }

    @Test
    public void testBorderIsExtendedWhenFortressIsPopulated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Placing headquarter */
        Point point21 = new Point(5, 17);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(5, 23);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        /* Wait for the fortress to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(fortress0);

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

        /* Verify that the border is extended when the military reaches the fortress */
        Point point3 = new Point(4, 26);
        Point point4 = new Point(6, 34);

        assertEquals(military.getTarget(), fortress0.getPosition());
        assertTrue(player0.getBorderPoints().contains(point3));

        Utils.fastForwardUntilWorkerReachesPoint(map, military, fortress0.getPosition());

        assertFalse(player0.getBorderPoints().contains(point3));
        assertTrue(player0.getBorderPoints().contains(point4));
    }

    @Test
    public void testFortressOnlyNeedsNineSoldiers() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        Utils.constructHouse(fortress0);

        /* Occupy the fortress with nine soldiers */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Verify that the fortress does not need another military */
        assertFalse(fortress0.needsMilitaryManning());
    }

    @Test (expected = Exception.class)
    public void testFortressCannotHoldSoldiersBeforeFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Verify that the fortress can't hold soldiers before it's finished */
        assertFalse(fortress0.needsMilitaryManning());

        Military military = new Military(player0, PRIVATE_RANK, map);

        map.placeWorker(military, fortress0);

        military.enterBuilding(fortress0);
    }

    @Test (expected = Exception.class)
    public void testFortressCannotHoldMoreThanNineSoldiers() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        Utils.constructHouse(fortress0);

        /* Occupy the fortress with nine soldiers */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Verify that the fortress does not need another military */
        Military military = new Military(player0, PRIVATE_RANK, map);

        map.placeWorker(military, fortress0);

        military.enterBuilding(fortress0);
    }

    @Test
    public void testFortressNeedsCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        Utils.constructHouse(fortress0);

        assertTrue(fortress0.needsMaterial(COIN));
    }

    @Test
    public void testUnfinishedFortressNotNeedsCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        assertFalse(fortress0.needsMaterial(COIN));
    }

    @Test
    public void testFortressCanHoldFourCoins() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        Utils.constructHouse(fortress0);

        assertTrue(fortress0.needsMaterial(COIN));

        /* Deliver four coins to the fortress */
        Cargo cargo = new Cargo(COIN, map);

        fortress0.promiseDelivery(COIN);
        fortress0.promiseDelivery(COIN);
        fortress0.promiseDelivery(COIN);
        fortress0.promiseDelivery(COIN);

        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);

        /* Verify that the fortress can't hold another coin */
        assertFalse(fortress0.needsMaterial(COIN));
        assertEquals(fortress0.getAmount(COIN), 4);

        try {
            fortress0.putCargo(cargo);
            fail();
        } catch (Exception e) {}

        assertEquals(fortress0.getAmount(COIN), 4);
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

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        Utils.constructHouse(fortress0);

        /* Deliver one coin to the fortress */
        Cargo cargo = new Cargo(COIN, map);

        fortress0.putCargo(cargo);

        /* Occupy the fortress with one private */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Verify that the private is promoted at the right time */
        for (int i = 0; i < 100; i++) {
            assertEquals(military.getRank(), PRIVATE_RANK);
            map.stepTime();
        }

        assertEquals(military.getRank(), CORPORAL_RANK);
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

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        Utils.constructHouse(fortress0);

        /* Deliver one coin to the fortress */
        Cargo cargo = new Cargo(COIN, map);

        fortress0.putCargo(cargo);

        /* Occupy the fortress with one private */
        Military military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Military military2 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Wait for the promotion to happen */
        Utils.fastForward(100, map);

        assertTrue((military1.getRank() == CORPORAL_RANK && military2.getRank() == PRIVATE_RANK) ||
                   (military1.getRank() == PRIVATE_RANK  && military2.getRank() == CORPORAL_RANK));
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

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        Utils.constructHouse(fortress0);

        /* Deliver one coin to the fortress */
        Cargo cargo = new Cargo(COIN, map);

        fortress0.putCargo(cargo);

        /* Wait until the fortress is populated */
        Utils.fastForward(200, map);

        /* Occupy the fortress with one private */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Verify that it still takes the same time for the private to get promoted */
        Utils.fastForward(99, map);

        assertEquals(military.getRank(), PRIVATE_RANK);

        map.stepTime();

        assertEquals(military.getRank(), CORPORAL_RANK);
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

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        Utils.constructHouse(fortress0);

        /* Deliver one coin to the fortress */
        Cargo cargo = new Cargo(COIN, map);

        fortress0.putCargo(cargo);

        /* Occupy the fortress with one private */
        Military military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Verify that the promotion consumes the coin */
        assertEquals(fortress0.getAmount(COIN), 1);

        Utils.fastForward(100, map);

        assertEquals(fortress0.getAmount(COIN), 0);
    }

    @Test
    public void testOnePromotionOnlyConsumesOneCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        Utils.constructHouse(fortress0);

        /* Deliver one coin to the fortress */
        Cargo cargo = new Cargo(COIN, map);

        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);

        /* Occupy the fortress with one private */
        Military military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Military military2 = Utils.occupyMilitaryBuilding(SERGEANT_RANK, fortress0);

        /* Verify that the promotion consumes the coin */
        assertEquals(fortress0.getAmount(COIN), 2);

        Utils.fastForward(100, map);

        assertEquals(fortress0.getAmount(COIN), 1);
    }

    @Test
    public void testFortressWithNoPromotionPossibleDoesNotConsumeCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        Utils.constructHouse(fortress0);

        /* Deliver one coin to the fortress */
        Cargo cargo = new Cargo(COIN, map);

        fortress0.putCargo(cargo);

        /* Occupy the fortress with one private */
        Military military1 = Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress0);
        Military military2 = Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress0);

        /* Verify that coin is not consumed */
        assertEquals(fortress0.getAmount(COIN), 1);

        Utils.fastForward(100, map);

        assertEquals(fortress0.getAmount(COIN), 1);
    }

    @Test
    public void testCanDisableCoinsToFortress() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        Utils.constructHouse(fortress0);

        /* Deliver one coin to the fortress */
        assertTrue(fortress0.needsMaterial(COIN));

        /* Disable coins to the fortress and verify that it doesn't need coins */
        fortress0.disablePromotions();

        assertFalse(fortress0.needsMaterial(COIN));
    }

    @Test
    public void testOccupiedFortressCanBeEvacuated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Connect headquarter and fortress */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Evacuate the fortress and verify that the military leaves the fortress */
        assertTrue(military.isInsideBuilding());

        fortress0.evacuate();

        map.stepTime();

        assertFalse(military.isInsideBuilding());
        assertEquals(fortress0.getNumberOfHostedMilitary(), 0);
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

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Connect headquarter and fortress */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Evacuate the fortress */
        assertTrue(military.isInsideBuilding());

        fortress0.evacuate();

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
    public void testEvacuatedSoldierReturnsOffroadWhenNotConnected() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Evacuate the fortress */
        assertTrue(military.isInsideBuilding());

        fortress0.evacuate();

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
    public void testNoMilitaryIsDispatchedToEvacuatedFortress() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Connect headquarters and fortress */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Evacuate the fortress */
        fortress0.evacuate();

        /* Verify that no soldiers are assigned to the fortress */
        for (int i = 0; i < 200; i++) {
            assertEquals(fortress0.getNumberOfHostedMilitary(), 0);
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

        /* Placing fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Evacuate the fortress */
        assertTrue(military.isInsideBuilding());

        fortress0.evacuate();

        map.stepTime();

        assertFalse(military.isInsideBuilding());

        /* Wait for the evacuated military to return to the storage */
        assertEquals(military.getTarget(), headquarter0.getPosition());
        int amount = headquarter0.getAmount(PRIVATE);

        Utils.fastForwardUntilWorkerReachesPoint(map, military, military.getTarget());

        assertTrue(military.isInsideBuilding());
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);

        /* Cancel evacuation */
        assertFalse(fortress0.needsMilitaryManning());

        fortress0.cancelEvacuation();

        assertTrue(fortress0.needsMilitaryManning());
    }

    @Test
    public void testMilitaryGoesBackToStorageWhenFortressIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing fortress */
        Point point26 = new Point(8, 8);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point26);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Destroy the fortress */
        assertEquals(fortress0.getNumberOfHostedMilitary(), 1);

        fortress0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertNotNull(military);
        assertEquals(military.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(PRIVATE);

        Utils.fastForwardUntilWorkerReachesPoint(map, military, headquarter0.getPosition());

        /* Verify that the military is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    }

    @Test
    public void testMilitaryGoesBackOnToStorageOnRoadsIfPossibleWhenFortressIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing fortress */
        Point point26 = new Point(8, 8);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point26);

        /* Connect the fortress with the headquarter */
        map.placeAutoSelectedRoad(player0, fortress0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Destroy the fortress */
        assertEquals(fortress0.getNumberOfHostedMilitary(), 1);

        fortress0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertNotNull(military);
        assertEquals(military.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point point : military.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test (expected = Exception.class)
    public void testProductionCannotBeResumedInFortress() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing fortress */
        Point point26 = new Point(8, 8);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point26);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Verify that production cannot be resumed in fortress */
        fortress0.resumeProduction();
    }

    @Test
    public void testCannotStopProductionInFortress() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(5, 9);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        /* Wait for the fortress to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Verify that it's not possible to stop production */
        try {
            fortress0.stopProduction();
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testFortressCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fortress */
        Point point1 = new Point(7, 9);
        Building fortress = map.placeBuilding(new Fortress(player0), point1);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress);

        /* Populate the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress);

        /* Verify that the fortress can't produce */
        assertFalse(fortress.canProduce());
    }

    @Test
    public void testFortressReportsCorrectOutput() throws Exception {

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
        Building fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Construct the fortress */
        Utils.constructHouse(fortress0);

        /* Verify that the reported output is correct */
        assertEquals(fortress0.getProducedMaterial().length, 0);
    }

    @Test
    public void testFortressReportsCorrectMaterialsNeededForConstruction() throws Exception {

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
        Building fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(fortress0.getMaterialNeeded().size(), 2);
        assertTrue(fortress0.getMaterialNeeded().contains(PLANK));
        assertTrue(fortress0.getMaterialNeeded().contains(STONE));
        assertEquals(fortress0.getTotalAmountNeeded(PLANK), 4);
        assertEquals(fortress0.getTotalAmountNeeded(STONE), 7);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(fortress0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testFortressReportsCorrectMaterialsNeededForProduction() throws Exception {

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
        Building fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Construct the fortress */
        Utils.constructHouse(fortress0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(fortress0.getMaterialNeeded().size(), 1);
        assertEquals(fortress0.getTotalAmountNeeded(COIN), 4);

        for (Material material : Material.values()) {
            if (material == COIN) {
                continue;
            }

            assertEquals(fortress0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testHostedMilitaryListIsEmptyForFortressUnderConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Verify that the list of hosted soldiers is empty */
        assertEquals(0, fortress0.getHostedMilitary().size());
    }

    @Test
    public void testHostedMilitaryListIsEmptyForEmptyFortress() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Construct fortress */
        Utils.constructHouse(fortress0);

        /* Verify that the list of hosted soldiers is empty */
        assertEquals(0, fortress0.getHostedMilitary().size());
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

        /* Place fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Construct fortress */
        Utils.constructHouse(fortress0);

        /* Add one military */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, fortress0);

        /* Verify that the list of hosted soldiers increased empty */
        assertEquals(1, fortress0.getHostedMilitary().size());
        assertEquals(fortress0.getHostedMilitary().get(0).getRank(), PRIVATE_RANK);
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

        /* Place fortress */
        Point point22 = new Point(6, 12);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Construct fortress */
        Utils.constructHouse(fortress0);

        /* Add one military */
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, 1, fortress0);

        /* Verify that the rank for the hosted military is correct */
        assertEquals(fortress0.getHostedMilitary().get(0).getRank(), SERGEANT_RANK);
    }

    @Test
    public void testBorderForFortressIsCorrect() throws Exception {

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
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(fortress0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress0);

        /* Verify that the border around the barracks is hexagon shaped and the middle of each line is 8 steps away from the center of the headquarter
        Border:

                -8, +8  -------  +8, +8
                  /                  \
            -16, 0        H          16, 0
                  \                  /
                -8, -8  -------  +8, +8

         */
        Set<Point> watchTowerHexagonBorder = Utils.getHexagonBorder(fortress0.getPosition(), 11);
        Set<Point> headquarterHexagonBorder = Utils.getHexagonBorder(headquarter0.getPosition(), 9);

        /* Verify that all points in the hexagon are part of the actual border */
        Set<Point> border = player0.getBorderPoints();
        for (Point point : watchTowerHexagonBorder) {

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

            assertTrue(watchTowerHexagonBorder.contains(point));
        }
    }

    @Test
    public void testLandForFortressIsCorrect() throws Exception {

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
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(fortress0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress0);

        /* Verify that the land of the headquarter is hexagon shaped and the middle of each line is 9 steps away from the center of the headquarter
        Land

                -8, +8  -------  +8, +8
                  /                  \
            -16, 0        H          16, 0
                  \                  /
                -8, -8  -------  +8, +8

         */
        Set<Point> area = Utils.getAreaInsideHexagon(10, fortress0.getPosition());

        /* Verify that all points in the hexagon land are part of the actual land */
        Collection<Point> land = fortress0.getDefendedLand();
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
    public void testDiscoveredLandForFortressIsCorrect() throws Exception {

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
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(fortress0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress0);

        /* Verify that the discovered land of the barracks is hexagon shaped and the middle of each line is 8 + 4 steps away
        from the center of the headquarter

         Land

                -8, +8  -------  +8, +8
                  /                  \
            -16, 0        H          16, 0
                  \                  /
                -8, -8  -------  +8, +8

         */
        Set<Point> watchTowerHexagonDiscoveredArea = Utils.getAreaInsideHexagon(15, fortress0.getPosition());
        Set<Point> headquarterDiscoveredLand = Utils.getAreaInsideHexagon(13, headquarter0.getPosition());

        /* Verify that all points in the hexagon land are part of the actual land */
        Collection<Point> discoveredLand = player0.getDiscoveredLand();
        for (Point point : discoveredLand) {

            /* Ignore points within the discovered land for the headquarter */
            if (headquarterDiscoveredLand.contains(point)) {
                continue;
            }

            assertTrue(watchTowerHexagonDiscoveredArea.contains(point));
        }

        /* Verify that all points in the actual land are part of the hexagon land */
        for (Point point : watchTowerHexagonDiscoveredArea) {

            /* Filter points outside the map */
            if (point.x < 0 || point.y < 0) {
                continue;
            }

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
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

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
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Verify that the discovered land is only inside the map */
        for (Point point : player0.getLandInPoints()) {
            assertTrue(point.x >= 0);
            assertTrue(point.y >= 0);
        }
    }
}
