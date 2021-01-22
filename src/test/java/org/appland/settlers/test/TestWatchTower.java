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
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.WatchTower;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_FIRST_CLASS_RANK;
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
public class TestWatchTower {

    @Test
    public void testWatchTowerNeedsThreePlanksAndFiveStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Deliver two planks and five stones */
        Cargo cargo = new Cargo(PLANK, map);

        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(watchTower0);

        /* Verify that this is enough to construct the watch tower */
        for (int i = 0; i < 150; i++) {
            assertTrue(watchTower0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(watchTower0.isReady());
    }

    @Test
    public void testWatchTowerCannotBeConstructedWithOnePlankTooLittle() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Deliver one plank and three stones */
        Cargo cargo = new Cargo(PLANK, map);

        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        /* Verify that the watch tower needs a plank */
        assertTrue(watchTower0.needsMaterial(PLANK));

        /* Assign builder */
        Utils.assignBuilder(watchTower0);

        /* Verify that this is not enough to construct the watch tower */
        for (int i = 0; i < 500; i++) {
            assertTrue(watchTower0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(watchTower0.isReady());
    }

    @Test
    public void testWatchTowerCannotBeConstructedWithOneStoneTooLittle() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Deliver one plank and three stones */
        Cargo cargo = new Cargo(PLANK, map);

        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        /* Verify that the watch tower needs a stone */
        assertTrue(watchTower0.needsMaterial(STONE));

        /* Assign builder */
        Utils.assignBuilder(watchTower0);

        /* Verify that this is not enough to construct the watch tower */
        for (int i = 0; i < 500; i++) {
            assertTrue(watchTower0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(watchTower0.isReady());
    }

    @Test
    public void testWatchTowerGetPopulatedWhenFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Connect the watch tower with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());

        /* Wait for the watch tower to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(watchTower0);

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

        /* Wait for the military to reach the watch tower */
        assertEquals(military.getTarget(), watchTower0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, military, watchTower0.getPosition());

        assertTrue(military.isInsideBuilding());
    }

    @Test
    public void testBorderIsNotExtendedWhenWatchTowerIsFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(5, 13);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());

        /* Verify that the border doesn't change when the watch tower finishes construction */
        Point point3 = new Point(6, 14);
        assertTrue(player0.getBorderPoints().contains(point3));

        Utils.fastForwardUntilBuildingIsConstructed(watchTower0);

        assertTrue(player0.getBorderPoints().contains(point3));
    }

    @Test
    public void testBorderIsExtendedWhenWatchTowerIsPopulated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(5, 13);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());

        /* Wait for the watch tower to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(watchTower0);

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

        /* Verify that the border is extended when the military reaches the watch tower */
        assertEquals(military.getTarget(), watchTower0.getPosition());
        Point point3 = new Point(6, 14);
        assertTrue(player0.getBorderPoints().contains(point3));

        Utils.fastForwardUntilWorkerReachesPoint(map, military, watchTower0.getPosition());

        Point point4 = new Point(7, 23);

        assertFalse(player0.getBorderPoints().contains(point3));
        assertTrue(player0.getBorderPoints().contains(point4));
    }

    @Test
    public void testWatchTowerOnlyNeedsSixSoldiers() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower with six soldiers */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        /* Verify that the watch tower does not need another military */
        assertFalse(watchTower0.needsMilitaryManning());
    }

    @Test
    public void testWatchTowerCannotHoldSoldiersBeforeFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Verify that the watch tower can't hold soldiers before it's finished */
        assertFalse(watchTower0.needsMilitaryManning());

        Military military = new Military(player0, PRIVATE_RANK, map);

        map.placeWorker(military, watchTower0);

        try {
            military.enterBuilding(watchTower0);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testWatchTowerCannotHoldMoreThanSixSoldiers() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower with six soldiers */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        /* Verify that the watch tower does not need another military */
        Military military = new Military(player0, PRIVATE_RANK, map);

        map.placeWorker(military, watchTower0);

        try {
            military.enterBuilding(watchTower0);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testWatchTowerNeedsCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        assertTrue(watchTower0.needsMaterial(COIN));
    }

    @Test
    public void testUnfinishedWatchTowerNotNeedsCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        assertFalse(watchTower0.needsMaterial(COIN));
    }

    @Test
    public void testWatchTowerCanHoldThreeCoins() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        assertTrue(watchTower0.needsMaterial(COIN));

        /* Deliver one coin to the watch tower */
        Cargo cargo = new Cargo(COIN, map);

        watchTower0.promiseDelivery(COIN);
        watchTower0.promiseDelivery(COIN);
        watchTower0.promiseDelivery(COIN);

        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);

        /* Verify that the watch tower can't hold another coin */
        assertFalse(watchTower0.needsMaterial(COIN));
        assertEquals(watchTower0.getAmount(COIN), 3);

        try {
            watchTower0.putCargo(cargo);
            fail();
        } catch (Exception e) {}

        assertEquals(watchTower0.getAmount(COIN), 3);
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

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        /* Deliver one coin to the watch tower */
        Cargo cargo = new Cargo(COIN, map);

        watchTower0.putCargo(cargo);

        /* Occupy the watch tower with one private */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

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

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        /* Deliver one coin to the watch tower */
        Cargo cargo = new Cargo(COIN, map);

        watchTower0.putCargo(cargo);

        /* Occupy the watch tower with one private */
        Military military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Military military2 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

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

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        /* Deliver one coin to the watch tower */
        Cargo cargo = new Cargo(COIN, map);

        watchTower0.putCargo(cargo);

        /* Wait before the watch tower is populated */
        Utils.fastForward(200, map);

        /* Occupy the watch tower with one private */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

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

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        /* Deliver one coin to the watch tower */
        Cargo cargo = new Cargo(COIN, map);

        watchTower0.putCargo(cargo);

        /* Occupy the watch tower with one private */
        Military military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        /* Verify that the promotion consumes the coin */
        assertEquals(watchTower0.getAmount(COIN), 1);

        Utils.fastForward(100, map);

        assertEquals(watchTower0.getAmount(COIN), 0);
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

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        /* Deliver one coin to the watch tower */
        Cargo cargo = new Cargo(COIN, map);

        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);

        /* Occupy the watch tower with one private */
        Military military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Military military2 = Utils.occupyMilitaryBuilding(SERGEANT_RANK, watchTower0);

        /* Verify that the promotion consumes the coin */
        assertEquals(watchTower0.getAmount(COIN), 2);

        Utils.fastForward(100, map);

        assertEquals(watchTower0.getAmount(COIN), 1);
    }

    @Test
    public void testWatchTowerWithNoPromotionPossibleDoesNotConsumeCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        /* Deliver one coin to the watch tower */
        Cargo cargo = new Cargo(COIN, map);

        watchTower0.putCargo(cargo);

        /* Occupy the watch tower with one private */
        Military military1 = Utils.occupyMilitaryBuilding(GENERAL_RANK, watchTower0);
        Military military2 = Utils.occupyMilitaryBuilding(GENERAL_RANK, watchTower0);

        /* Verify that coin is not consumed */
        assertEquals(watchTower0.getAmount(COIN), 1);

        Utils.fastForward(100, map);

        assertEquals(watchTower0.getAmount(COIN), 1);
    }

    @Test
    public void testCanDisableCoinsToWatchTower() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        /* Deliver one coin to the watch tower */
        assertTrue(watchTower0.needsMaterial(COIN));

        /* Disable coins to the watch tower and verify that it doesn't need coins */
        watchTower0.disablePromotions();

        assertFalse(watchTower0.needsMaterial(COIN));
    }

    @Test
    public void testOccupiedWatchTowerCanBeEvacuated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Connect headquarter and watch tower */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        /* Evacuate the watch tower and verify that the military leaves the watch tower */
        assertTrue(military.isInsideBuilding());

        watchTower0.evacuate();

        map.stepTime();

        assertFalse(military.isInsideBuilding());
        assertEquals(watchTower0.getNumberOfHostedMilitary(), 0);
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

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Connect headquarter and watch tower */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        /* Evacuate the watch tower */
        assertTrue(military.isInsideBuilding());

        watchTower0.evacuate();

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

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        /* Evacuate the watch tower */
        assertTrue(military.isInsideBuilding());

        watchTower0.evacuate();

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
    public void testNoMilitaryIsDispatchedToEvacuatedWatchTower() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Connect headquarters and watch tower */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Evacuate the watch tower */
        watchTower0.evacuate();

        /* Verify that no soldiers are assigned to the watch tower */
        for (int i = 0; i < 200; i++) {
            assertEquals(watchTower0.getNumberOfHostedMilitary(), 0);
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

        /* Placing watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        /* Evacuate the watch tower */
        assertTrue(military.isInsideBuilding());

        watchTower0.evacuate();

        map.stepTime();

        assertFalse(military.isInsideBuilding());

        /* Wait for the evacuated military to return to the storage */
        assertEquals(military.getTarget(), headquarter0.getPosition());
        int amount = headquarter0.getAmount(PRIVATE);

        Utils.fastForwardUntilWorkerReachesPoint(map, military, military.getTarget());

        assertTrue(military.isInsideBuilding());
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);

        /* Cancel evacuation */
        assertFalse(watchTower0.needsMilitaryManning());

        watchTower0.cancelEvacuation();

        assertTrue(watchTower0.needsMilitaryManning());
    }

    @Test
    public void testMilitaryGoesBackToStorageWhenWatchTowerIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing watch tower */
        Point point26 = new Point(8, 8);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point26);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        /* Destroy the watch tower */
        assertEquals(watchTower0.getNumberOfHostedMilitary(), 1);

        watchTower0.tearDown();

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
    public void testProductionCannotBeResumedInWatchTower() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing watch tower */
        Point point26 = new Point(8, 8);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point26);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Verify that production cannot be resumed in watch tower */
        try {
            watchTower0.resumeProduction();

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testMilitaryGoesBackOnToStorageOnRoadsIfPossibleWhenWatchTowerIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing watch tower */
        Point point26 = new Point(8, 8);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point26);

        /* Connect the watch tower with the headquarter */
        map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        /* Destroy the watch tower */
        assertEquals(watchTower0.getNumberOfHostedMilitary(), 1);

        watchTower0.tearDown();

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

    @Test
    public void testCannotStopProductionInBarracks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(5, 13);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());

        /* Wait for the watch tower to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(watchTower0);

        /* Occupy the watch tower */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        /* Verify that it's not possible to stop production */
        try {
            watchTower0.stopProduction();
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testWatchTowerCannotProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(10, 10);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Populate the watch tower */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        /* Verify that the watch tower can produce */
        assertFalse(watchTower0.canProduce());
    }

    @Test
    public void testWatchTowerReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Construct the watch tower */
        Utils.constructHouse(watchTower0);

        /* Verify that the reported output is correct */
        assertEquals(watchTower0.getProducedMaterial().length, 0);
    }

    @Test
    public void testWatchTowerReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(watchTower0.getMaterialNeeded().size(), 2);
        assertTrue(watchTower0.getMaterialNeeded().contains(PLANK));
        assertTrue(watchTower0.getMaterialNeeded().contains(STONE));
        assertEquals(watchTower0.getTotalAmountNeeded(PLANK), 3);
        assertEquals(watchTower0.getTotalAmountNeeded(STONE), 5);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(watchTower0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testWatchTowerReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Construct the watch tower */
        Utils.constructHouse(watchTower0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(watchTower0.getMaterialNeeded().size(), 1);
        assertEquals(watchTower0.getTotalAmountNeeded(COIN), 3);

        for (Material material : Material.values()) {
            if (material == COIN) {
                continue;
            }

            assertEquals(watchTower0.getTotalAmountNeeded(material), 0);
        }
    }

    @Test
    public void testHostedMilitaryListIsEmptyForWatchTowerUnderConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Verify that the list of hosted soldiers is empty */
        assertEquals(0, watchTower0.getHostedMilitary().size());
    }

    @Test
    public void testHostedMilitaryListIsEmptyForEmptyWatchTower() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Construct watch tower */
        Utils.constructHouse(watchTower0);

        /* Verify that the list of hosted soldiers is empty */
        assertEquals(0, watchTower0.getHostedMilitary().size());
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

        /* Place watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Construct watch tower */
        Utils.constructHouse(watchTower0);

        /* Add one military */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        /* Verify that the list of hosted soldiers increased empty */
        assertEquals(1, watchTower0.getHostedMilitary().size());
        assertEquals(watchTower0.getHostedMilitary().get(0).getRank(), PRIVATE_RANK);
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

        /* Place watch tower */
        Point point22 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Construct watch tower */
        Utils.constructHouse(watchTower0);

        /* Add one military */
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, 1, watchTower0);

        /* Verify that the rank for the hosted military is correct */
        assertEquals(watchTower0.getHostedMilitary().get(0).getRank(), SERGEANT_RANK);
    }

    @Test
    public void testBorderForWatchTowerIsCorrect() throws Exception {

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
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(watchTower0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, watchTower0);

        /* Verify that the border around the barracks is hexagon shaped and the middle of each line is 8 steps away from the center of the headquarter
        Border:

                -8, +8  -------  +8, +8
                  /                  \
            -16, 0        H          16, 0
                  \                  /
                -8, -8  -------  +8, +8

         */
        Set<Point> watchTowerHexagonBorder = Utils.getHexagonBorder(watchTower0.getPosition(), 10);
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
    public void testLandForWatchTowerIsCorrect() throws Exception {

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
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(watchTower0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, watchTower0);

        /* Verify that the land of the headquarter is hexagon shaped and the middle of each line is 9 steps away from the center of the headquarter
        Land

                -8, +8  -------  +8, +8
                  /                  \
            -16, 0        H          16, 0
                  \                  /
                -8, -8  -------  +8, +8

         */
        Set<Point> area = Utils.getAreaInsideHexagon(9, watchTower0.getPosition());

        /* Verify that all points in the hexagon land are part of the actual land */
        Collection<Point> land = watchTower0.getDefendedLand();
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
    public void testDiscoveredLandForWatchTowerIsCorrect() throws Exception {

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
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(watchTower0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, watchTower0);

        /* Verify that the discovered land of the barracks is hexagon shaped and the middle of each line is 8 + 4 steps away
        from the center of the headquarter

         Land

                -8, +8  -------  +8, +8
                  /                  \
            -16, 0        H          16, 0
                  \                  /
                -8, -8  -------  +8, +8

         */
        Set<Point> watchTowerHexagonDiscoveredArea = Utils.getAreaInsideHexagon(14, watchTower0.getPosition());
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
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

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
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        /* Verify that the discovered land is only inside the map */
        for (Point point : player0.getLandInPoints()) {
            assertTrue(point.x >= 0);
            assertTrue(point.y >= 0);
        }
    }

    @Test
    public void testWatchTowerCanBeUpgraded() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(21, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(watchTower0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        /* Upgrade the barracks */
        assertFalse(watchTower0.isUpgrading());

        watchTower0.upgrade();

        /* Add materials for the upgrade */
        Utils.deliverCargos(watchTower0, PLANK, 1);
        Utils.deliverCargos(watchTower0, STONE, 2);

        /* Verify that the upgrade isn't too quick */
        for (int i = 0; i < 100; i++) {

            assertTrue(watchTower0.isUpgrading());
            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        Building upgradedBuilding = map.getBuildingAtPoint(watchTower0.getPosition());

        assertFalse(map.getBuildings().contains(watchTower0));
        assertTrue(map.getBuildings().contains(upgradedBuilding));
        assertFalse(player0.getBuildings().contains(watchTower0));
        assertTrue(player0.getBuildings().contains(upgradedBuilding));
        assertNotNull(upgradedBuilding);
        assertFalse(upgradedBuilding.isUpgrading());
        assertEquals(upgradedBuilding.getClass(), Fortress.class);
    }

    @Test
    public void testUnfinishedWatchTowerCannotBeUpgraded() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(13, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Connect the watch tower with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());

        /* Upgrade the watch tower */
        try {
            watchTower0.upgrade();

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testBurningWatchTowerCannotBeUpgraded() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(13, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Connect the watch tower with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Tear down the watch tower so it's on fire */
        watchTower0.tearDown();

        /* Upgrade the watch tower */
        try {
            watchTower0.upgrade();

            fail();
        } catch (InvalidUserActionException e) {}
    }

    @Test
    public void testCannotUpgradeWatchTowerWithoutMaterial() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(21, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        /* Upgrade the watch tower */
        watchTower0.upgrade();

        /* Add materials but not enough for the upgrade */
        Utils.deliverCargos(watchTower0, PLANK, 1);
        Utils.deliverCargos(watchTower0, STONE, 1); // One less than needed

        /* Verify that the upgrade cannot happen without the required material */
        for (int i = 0; i < 1000; i++) {

            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        /* Add the last required material for the upgrade */
        Utils.deliverCargos(watchTower0, STONE, 1);

        /* Step time once and verify that the watch tower is upgraded */
        map.stepTime();

        assertNotEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));
        assertEquals(map.getBuildingAtPoint(watchTower0.getPosition()).getClass(), Fortress.class);
    }

    @Test
    public void testCannotUpgradeWatchTowerBeingUpgraded() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(13, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Connect the watch tower with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        /* Upgrade the watch tower */
        watchTower0.upgrade();

        /* Verify that the fortress can't get upgraded again */
        try {
            watchTower0.upgrade();

            fail();
        } catch (InvalidUserActionException e) {}
    }

    @Test
    public void testUpgradingCausesMaterialToGetDelivered() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Make sure there is material for upgrading */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10);
        Utils.adjustInventoryTo(headquarter0, STONE, 10);

        /* Place watch tower */
        Point point1 = new Point(21, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Connect the watch tower with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 6, watchTower0);

        assertFalse(watchTower0.needsMilitaryManning());
        assertFalse(watchTower0.needsMaterial(PRIVATE));

        /* Place the courier on the road */
        assertNotNull(road0.getCourier());

        Courier courier0 = road0.getCourier();

        /* Verify that the watch tower doesn't need stone before the upgrade */
        assertFalse(watchTower0.needsMaterial(STONE));

        /* Upgrade the watch tower */
        watchTower0.upgrade();

        /* Verify that the watch tower needs stone */
        assertTrue(watchTower0.needsMaterial(STONE));

        /* Verify that the courier picks up a stone or plank */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier0);

        assertNotNull(courier0.getCargo());
        assertTrue(Objects.equals(courier0.getCargo().getMaterial(), STONE) || Objects.equals(courier0.getCargo().getMaterial(), PLANK));

        /* Verify that the courier delivers the cargo */
        assertEquals(courier0.getCargo().getTarget(), watchTower0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, watchTower0.getPosition());

        assertNull(courier0.getCargo());
    }

    @Test
    public void testOccupiedWatchTowerIsOccupiedAfterUpgrade() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(21, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        assertTrue(watchTower0.isOccupied());

        /* Upgrade the watch tower */
        watchTower0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        /* Verify that the upgrade isn't too quick */
        for (int i = 0; i < 100; i++) {

            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        /* Verify that the upgraded building is also occupied */
        Building fortress0 = map.getBuildingAtPoint(watchTower0.getPosition());

        assertTrue(fortress0.isOccupied());
        assertEquals(fortress0.getNumberOfHostedMilitary(), 1);
    }

    @Test
    public void testCoinRemainsAfterUpgrade() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(21, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Upgrade the watch tower */
        watchTower0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        /* Put a coin in the building */
        Cargo coinCargo = new Cargo(COIN, map);

        watchTower0.promiseDelivery(COIN);

        watchTower0.putCargo(coinCargo);

        assertEquals(watchTower0.getAmount(COIN), 1);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        /* Verify that the coin is still in the building */
        Building fortress0 = map.getBuildingAtPoint(watchTower0.getPosition());

        assertEquals(fortress0.getAmount(COIN), 1);
    }

    @Test
    public void testBuildingDuringUpgradeCanBeDestroyed() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(21, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        assertTrue(watchTower0.isOccupied());

        /* Upgrade the watch tower */
        watchTower0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        /* Upgrade for a while */
        for (int i = 0; i < 10; i++) {

            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        /* Verify that the building can be destroyed */
        watchTower0.tearDown();

        assertTrue(watchTower0.isBurningDown());
    }

    @Test
    public void testPlayerIsCorrectAfterUpgrade() throws Exception {

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

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(21, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        assertTrue(watchTower0.isOccupied());

        /* Upgrade the watch tower */
        watchTower0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        /* Put a coin in the building */
        Cargo coinCargo = new Cargo(COIN, map);

        watchTower0.promiseDelivery(COIN);

        watchTower0.putCargo(coinCargo);

        assertEquals(watchTower0.getAmount(COIN), 1);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {
            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        /* Verify that the player is set correctly in the upgraded building */
        Building fortress0 = map.getBuildingAtPoint(watchTower0.getPosition());

        assertEquals(fortress0.getPlayer(), player0);
    }

    @Test
    public void testCanHostRightNumberOfSoldiersAfterUpgraded() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(21, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        assertTrue(watchTower0.isOccupied());

        /* Upgrade the watch tower */
        watchTower0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        /* Verify that two more soldiers can be hosted in the building */
        Building fortress0 = map.getBuildingAtPoint(watchTower0.getPosition());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
    }

    @Test
    public void testBorderIsExpandedAfterUpgrade() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(21, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        assertTrue(watchTower0.isOccupied());

        /* Upgrade the watch tower */
        watchTower0.upgrade();

        /* Add materials for the upgrade */
        Utils.deliverCargos(watchTower0, PLANK, 1);
        Utils.deliverCargos(watchTower0, STONE, 2);

        /* Verify the border before the upgrade */
        Point point2 = new Point(36, 10);
        Point point3 = new Point(36, 12);

        assertTrue(player0.getBorderPoints().contains(point2));
        assertFalse(player0.getBorderPoints().contains(point3));
        assertFalse(player0.isWithinBorder(point2));

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        /* Verify that the border is expanded after the upgrade */
        assertFalse(player0.getBorderPoints().contains(point2));
        assertTrue(player0.getBorderPoints().contains(point3));
        assertTrue(player0.isWithinBorder(point2));
    }

    @Test
    public void testFlagIsCorrectAfterUpgrade() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(21, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        assertTrue(watchTower0.isOccupied());

        /* Upgrade the watch tower */
        watchTower0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        /* Verify that the flag is correct after the upgrade */
        Building buildingAfterUpgrade = map.getBuildingAtPoint(point1);

        assertNotNull(buildingAfterUpgrade);
        assertNotNull(buildingAfterUpgrade.getFlag());
        assertEquals(buildingAfterUpgrade.getFlag().getPosition(), point1.downRight());
    }

    @Test
    public void testOccupiedBuildingRemainsOccupiedDuringUpgrade() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(21, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        assertTrue(watchTower0.isOccupied());

        /* Upgrade the watch tower */
        watchTower0.upgrade();

        /* Verify that the watch tower is still occupied */
        assertTrue(watchTower0.isOccupied());

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        /* Verify that the watch tower is occupied during the upgrade */
        for (int i = 0; i < 100; i++) {

            /* Verify that the watch tower is still occupied */
            assertTrue(watchTower0.isOccupied());

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

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Evacuate the watch tower */
        watchTower0.evacuate();

        assertTrue(watchTower0.isEvacuated());
        assertFalse(watchTower0.needsMilitaryManning());

        /* Connect headquarter and watch tower */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());

        /* Occupy the watch tower */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        /* Verify that the military comes out immediately */

        map.stepTime();

        assertFalse(military.isInsideBuilding());
        assertEquals(watchTower0.getNumberOfHostedMilitary(), 0);
    }

    @Test
    public void testCanUpgradeAfterDisablingPromotions() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(21, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        assertTrue(watchTower0.isOccupied());

        /* Disable promotions */
        watchTower0.disablePromotions();

        assertFalse(watchTower0.isPromotionEnabled());

        /* Verify that the watch tower can be upgraded */
        watchTower0.upgrade();

        assertTrue(watchTower0.isUpgrading());

        /* Add materials for the upgrade */
        Utils.deliverCargos(watchTower0, PLANK, 1);
        Utils.deliverCargos(watchTower0, STONE, 2);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        /* Verify that the watch tower is upgraded */
        assertNotEquals(map.getBuildingAtPoint(watchTower0.getPosition()), watchTower0);

        Building buildingAfterUpgrade = map.getBuildingAtPoint(point1);

        assertFalse(buildingAfterUpgrade.isUpgrading());
        assertNotNull(buildingAfterUpgrade);
        assertEquals(buildingAfterUpgrade.getClass(), Fortress.class);
    }

    @Test
    public void testUpgradeDoesNotDestroyNearbyHouses() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0);

        /* Place a watch tower */
        Point point2 = new Point(26, 6);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point2);

        /* Construct the watch tower */
        Utils.constructHouse(watchTower0);

        /* Occupy the watch tower */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        /* Place regular building */
        Point point3 = new Point(30, 6);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point3);

        /* Connect the buildings with a road */
        Road road0 = map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), foresterHut0.getFlag());

        /* Evacuate the watch tower and wait for the watch tower to become empty */
        watchTower0.evacuate();

        for (int i = 0; i < 1000; i++) {

            if (watchTower0.getNumberOfHostedMilitary() == 0) {
                break;
            }

            map.stepTime();
        }

        assertEquals(watchTower0.getNumberOfHostedMilitary(), 0);

        /* Upgrade the watch tower */
        watchTower0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        /* Verify that the forester hut and the road remains */
        assertEquals(map.getBuildingAtPoint(watchTower0.getPosition()).getClass(), WatchTower.class);
        assertTrue(map.isBuildingAtPoint(foresterHut0.getPosition()));
        assertEquals(map.getBuildingAtPoint(foresterHut0.getPosition()), foresterHut0);
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testUnoccupiedBuildingRemainsUnoccupiedDuringAndAfterUpgrade() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(21, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Upgrade the watch tower */
        assertFalse(watchTower0.isOccupied());

        watchTower0.upgrade();

        /* Verify that the watch tower is still unoccupied */
        assertFalse(watchTower0.isOccupied());

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        /* Verify that the watch tower is unoccupied during and after the upgrade */
        Point point2 = new Point(25, 5);
        Point point3 = new Point(27, 5);

        for (int i = 0; i < 100; i++) {

            /* Verify that the watch tower is still occupied */
            assertFalse(watchTower0.isOccupied());
            assertTrue(player0.getBorderPoints().contains(point2));
            assertFalse(player0.isWithinBorder(point3));

            map.stepTime();
        }

        assertEquals(map.getBuildingAtPoint(watchTower0.getPosition()).getClass(), WatchTower.class);
        assertFalse(map.getBuildingAtPoint(watchTower0.getPosition()).isOccupied());
        assertTrue(player0.getBorderPoints().contains(point2));
        assertFalse(player0.isWithinBorder(point3));
    }

    @Test
    public void testUpgradeOfBuildingWithMilitaryDoesNotCauseOverAllocation() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place watch tower */
        Point point1 = new Point(21, 5);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Connect the watch tower with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0);

        /* Fill the watch tower with soldiers */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 6, watchTower0);

        assertEquals(watchTower0.getNumberOfHostedMilitary(), 6);

        /* Make sure there are enough soldiers in the headquarter */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 200);

        /* Upgrade the watch tower */
        watchTower0.upgrade();

        /* Add materials for the upgrade */
        Utils.deliverCargos(watchTower0, PLANK, 1);
        Utils.deliverCargos(watchTower0, STONE, 2);

        /* Wait for the upgrade to happen */
        assertEquals(watchTower0.getNumberOfHostedMilitary(), 6);

        Building fortress0 = Utils.waitForBuildingToGetUpgraded(watchTower0);

        assertEquals(fortress0.getNumberOfHostedMilitary(), 6);
        assertEquals(map.getBuildingAtPoint(watchTower0.getPosition()), fortress0);
        assertEquals(fortress0.getMaxHostedMilitary(), 9);

        /* Verify that only three soldiers are sent out to occupy the building */

        /* Wait for three soldiers to occupy the building */
        Utils.waitForMilitaryBuildingToGetPopulated(fortress0, 9);

        /* Verify that no more soldiers are sent out */
        assertFalse(fortress0.needsMilitaryManning());
        assertEquals(fortress0.getNumberOfHostedMilitary(), 9);

        for (int i = 0; i < 2000; i++) {

            assertNull(Utils.findMilitaryOutsideBuilding(player0));

            map.stepTime();
        }
    }

    @Test
    public void testUpgradedWatchTowerGetsPopulatedFully() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Add privates to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 20);

        /* Place watch tower */
        Point point1 = new Point(6, 12);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        Utils.constructHouse(watchTower0);

        /* Connect the watch tower to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());

        /* Wait for the watch tower to get occupied */
        for (int i = 0; i < 1000; i++) {

            if (watchTower0.getNumberOfHostedMilitary() == 2) {
                break;
            }

            map.stepTime();
        }

        assertEquals(watchTower0.getNumberOfHostedMilitary(), 2);

        /* Upgrade the watch tower */
        watchTower0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        /* Wait for the upgrade to happen */
        Building fortress0 = Utils.waitForBuildingToGetUpgraded(watchTower0);

        assertEquals(map.getBuildingAtPoint(watchTower0.getPosition()), fortress0);
        assertEquals(fortress0.getMaxHostedMilitary(), 9);

        /* Verify that the building gets fully occupied */
        for (int i = 0; i < 500; i++) {

            if (fortress0.getNumberOfHostedMilitary() == 9) {
                break;
            }

            map.stepTime();
        }

        assertEquals(fortress0.getNumberOfHostedMilitary(), 9);
    }
}
