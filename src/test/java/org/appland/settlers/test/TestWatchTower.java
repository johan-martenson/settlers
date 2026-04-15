/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.WatchTower;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.*;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestWatchTower {

    @Test
    public void testWatchTowerNeedsThreePlanksAndFiveStonesForConstruction() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Deliver two planks and five stones
        var cargo = new Cargo(PLANK, map);

        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);

        var stoneCargo = new Cargo(STONE, map);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        // Assign builder
        Utils.assignBuilder(watchTower0);

        // Verify that this is enough to construct the watch tower
        for (int i = 0; i < 150; i++) {
            assertTrue(watchTower0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(watchTower0.isReady());
    }

    @Test
    public void testWatchTowerCannotBeConstructedWithOnePlankTooLittle() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Deliver one plank and three stones
        var cargo = new Cargo(PLANK, map);

        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);

        var stoneCargo = new Cargo(STONE, map);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        // Verify that the watch tower needs a plank
        assertTrue(watchTower0.needsMaterial(PLANK));

        // Assign builder
        Utils.assignBuilder(watchTower0);

        // Verify that this is not enough to construct the watch tower
        for (int i = 0; i < 500; i++) {
            assertTrue(watchTower0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(watchTower0.isReady());
    }

    @Test
    public void testWatchTowerCannotBeConstructedWithOneStoneTooLittle() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Deliver one plank and three stones
        var cargo = new Cargo(PLANK, map);

        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);

        var stoneCargo = new Cargo(STONE, map);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        // Verify that the watch tower needs a stone
        assertTrue(watchTower0.needsMaterial(STONE));

        // Assign builder
        Utils.assignBuilder(watchTower0);

        // Verify that this is not enough to construct the watch tower
        for (int i = 0; i < 500; i++) {
            assertTrue(watchTower0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(watchTower0.isReady());
    }

    @Test
    public void testWatchTowerGetPopulatedWhenFinished() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Connect the watch tower with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());

        // Wait for the watch tower to finish construction
        Utils.fastForwardUntilBuildingIsConstructed(watchTower0);

        // Verify that a military is sent from the headquarter
        map.stepTime();

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Soldier.class);

        var military = (Soldier) null;
        for (var worker : map.getWorkers()) {
            if (worker instanceof Soldier) {
                military = (Soldier)worker;
            }
        }

        assertNotNull(military);

        // Wait for the military to reach the watch tower
        assertEquals(military.getTarget(), watchTower0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, military, watchTower0.getPosition());

        assertTrue(military.isInsideBuilding());
    }

    @Test
    public void testBorderIsNotExtendedWhenWatchTowerIsFinished() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(5, 13);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());

        // Verify that the border doesn't change when the watch tower finishes construction
        var point3 = new Point(6, 14);
        assertTrue(player0.getBorderPoints().contains(point3));

        Utils.fastForwardUntilBuildingIsConstructed(watchTower0);

        assertTrue(player0.getBorderPoints().contains(point3));
    }

    @Test
    public void testBorderIsExtendedWhenWatchTowerIsPopulated() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(5, 13);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());

        // Wait for the watch tower to finish construction
        Utils.fastForwardUntilBuildingIsConstructed(watchTower0);

        // Verify that a military is sent from the headquarter
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);

        map.stepTime();

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Soldier.class);

        var military = (Soldier) null;
        for (var worker : map.getWorkers()) {
            if (worker instanceof Soldier) {
                military = (Soldier)worker;
            }
        }

        assertNotNull(military);

        // Verify that the border is extended when the military reaches the watch tower
        assertEquals(military.getTarget(), watchTower0.getPosition());
        var point3 = new Point(6, 14);
        assertTrue(player0.getBorderPoints().contains(point3));

        Utils.fastForwardUntilWorkerReachesPoint(map, military, watchTower0.getPosition());

        var point4 = new Point(7, 23);

        assertFalse(player0.getBorderPoints().contains(point3));
        assertTrue(player0.getBorderPoints().contains(point4));
    }

    @Test
    public void testWatchTowerOnlyNeedsSixSoldiers() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        // Occupy the watch tower with six soldiers
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Verify that the watch tower does not need another military
        assertFalse(watchTower0.needsMilitaryManning());
    }

    @Test
    public void testWatchTowerCannotHoldSoldiersBeforeFinished() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Verify that the watch tower can't hold soldiers before it's finished
        assertFalse(watchTower0.needsMilitaryManning());

        var military = new Soldier(player0, PRIVATE_RANK, map);

        map.placeWorker(military, watchTower0);

        try {
            military.enterBuilding(watchTower0);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testWatchTowerCannotHoldMoreThanSixSoldiers() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        // Occupy the watch tower with six soldiers
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Verify that the watch tower does not need another military
        var military = new Soldier(player0, PRIVATE_RANK, map);

        map.placeWorker(military, watchTower0);

        try {
            military.enterBuilding(watchTower0);

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testWatchTowerNeedsCoin() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        assertTrue(watchTower0.needsMaterial(COIN));
    }

    @Test
    public void testUnfinishedWatchTowerNotNeedsCoin() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        assertFalse(watchTower0.needsMaterial(COIN));
    }

    @Test
    public void testWatchTowerCanHoldThreeCoins() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        assertTrue(watchTower0.needsMaterial(COIN));

        // Deliver one coin to the watch tower
        var cargo = new Cargo(COIN, map);

        watchTower0.promiseDelivery(COIN);
        watchTower0.promiseDelivery(COIN);
        watchTower0.promiseDelivery(COIN);

        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);

        // Verify that the watch tower can't hold another coin
        assertFalse(watchTower0.needsMaterial(COIN));
        assertEquals(watchTower0.getAmount(COIN), 3);

        try {
            watchTower0.putCargo(cargo);

            fail();
        } catch (Exception e) { }

        assertEquals(watchTower0.getAmount(COIN), 3);
    }

    @Test
    public void testPrivateIsPromotedWhenCoinIsAvailable() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        // Deliver one coin to the watch tower
        var cargo = new Cargo(COIN, map);

        watchTower0.putCargo(cargo);

        // Occupy the watch tower with one private
        var military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Verify that the private is promoted at the right time
        for (int i = 0; i < 100; i++) {
            assertEquals(military.getRank(), PRIVATE_RANK);

            map.stepTime();
        }

        assertEquals(military.getRank(), PRIVATE_FIRST_CLASS_RANK);
    }

    @Test
    public void testOnlyOnePrivateIsPromoted() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        // Deliver one coin to the watch tower
        var cargo = new Cargo(COIN, map);

        watchTower0.putCargo(cargo);

        // Occupy the watch tower with one private
        var military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        var military2 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Wait for the promotion to happen
        Utils.fastForward(100, map);

        assertTrue((military1.getRank() == PRIVATE_FIRST_CLASS_RANK && military2.getRank() == PRIVATE_RANK) ||
                   (military1.getRank() == PRIVATE_RANK  && military2.getRank() == PRIVATE_FIRST_CLASS_RANK));
    }

    @Test
    public void testTimeSpentWithCoinButNoMilitaryDoesNotSpeedUpPromotion() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        // Deliver one coin to the watch tower
        var cargo = new Cargo(COIN, map);

        watchTower0.putCargo(cargo);

        // Wait before the watch tower is populated
        Utils.fastForward(200, map);

        // Occupy the watch tower with one private
        var military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Verify that it still takes the same time for the private to get promoted
        Utils.fastForward(99, map);

        assertEquals(military.getRank(), PRIVATE_RANK);

        map.stepTime();

        assertEquals(military.getRank(), PRIVATE_FIRST_CLASS_RANK);
    }

    @Test
    public void testPromotionConsumesCoin() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        // Deliver one coin to the watch tower
        var cargo = new Cargo(COIN, map);

        watchTower0.putCargo(cargo);

        // Occupy the watch tower with one private
        var military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Verify that the promotion consumes the coin
        assertEquals(watchTower0.getAmount(COIN), 1);

        Utils.fastForward(100, map);

        assertEquals(watchTower0.getAmount(COIN), 0);
    }

    @Test
    public void testOnePromotionOnlyConsumesOneCoin() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        // Deliver one coin to the watch tower
        var cargo = new Cargo(COIN, map);

        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);

        // Occupy the watch tower with one private
        var military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        var military2 = Utils.occupyMilitaryBuilding(SERGEANT_RANK, watchTower0);

        // Verify that the promotion consumes the coin
        assertEquals(watchTower0.getAmount(COIN), 2);

        Utils.fastForward(100, map);

        assertEquals(watchTower0.getAmount(COIN), 1);
    }

    @Test
    public void testWatchTowerWithNoPromotionPossibleDoesNotConsumeCoin() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        // Deliver one coin to the watch tower
        var cargo = new Cargo(COIN, map);

        watchTower0.putCargo(cargo);

        // Occupy the watch tower with one private
        var military1 = Utils.occupyMilitaryBuilding(GENERAL_RANK, watchTower0);
        var military2 = Utils.occupyMilitaryBuilding(GENERAL_RANK, watchTower0);

        // Verify that coin is not consumed
        assertEquals(watchTower0.getAmount(COIN), 1);

        Utils.fastForward(100, map);

        assertEquals(watchTower0.getAmount(COIN), 1);
    }

    @Test
    public void testCanDisableCoinsToWatchTower() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        Utils.constructHouse(watchTower0);

        // Deliver one coin to the watch tower
        assertTrue(watchTower0.needsMaterial(COIN));

        // Disable coins to the watch tower and verify that it doesn't need coins
        watchTower0.disablePromotions();

        assertFalse(watchTower0.needsMaterial(COIN));
    }

    @Test
    public void testOccupiedWatchTowerCanBeEvacuated() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Connect headquarter and watch tower
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        var military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Evacuate the watch tower and verify that the military leaves the watch tower
        assertTrue(military.isInsideBuilding());

        watchTower0.evacuate();

        map.stepTime();

        assertFalse(military.isInsideBuilding());
        assertEquals(watchTower0.getNumberOfHostedSoldiers(), 0);
    }

    @Test
    public void testEvacuatedMilitaryReturnsToStorage() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Connect headquarter and watch tower
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        var military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Evacuate the watch tower
        assertTrue(military.isInsideBuilding());

        watchTower0.evacuate();

        map.stepTime();

        assertFalse(military.isInsideBuilding());

        // Verify that the evacuated military returns to the storage
        assertEquals(military.getTarget(), headquarter0.getPosition());
        var amount = headquarter0.getAmount(PRIVATE);

        Utils.fastForwardUntilWorkerReachesPoint(map, military, military.getTarget());

        assertTrue(military.isInsideBuilding());
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    }

    @Test
    public void testEvacuatedSoldierReturnsOffroadWhenNotConnected() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        var military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Evacuate the watch tower
        assertTrue(military.isInsideBuilding());

        watchTower0.evacuate();

        map.stepTime();

        assertFalse(military.isInsideBuilding());

        // Verify that the evacuated military returns to the storage
        assertEquals(military.getTarget(), headquarter0.getPosition());
        var amount = headquarter0.getAmount(PRIVATE);

        Utils.fastForwardUntilWorkerReachesPoint(map, military, military.getTarget());

        assertTrue(military.isInsideBuilding());
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    }

    @Test
    public void testNoMilitaryIsDispatchedToEvacuatedWatchTower() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Connect headquarters and watch tower
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Evacuate the watch tower
        watchTower0.evacuate();

        // Verify that no soldiers are assigned to the watch tower
        for (int i = 0; i < 200; i++) {
            assertEquals(watchTower0.getNumberOfHostedSoldiers(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testEvacuationCanBeCanceled() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        var military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Evacuate the watch tower
        assertTrue(military.isInsideBuilding());

        watchTower0.evacuate();

        map.stepTime();

        assertFalse(military.isInsideBuilding());

        // Wait for the evacuated military to return to the storage
        assertEquals(military.getTarget(), headquarter0.getPosition());
        var amount = headquarter0.getAmount(PRIVATE);

        Utils.fastForwardUntilWorkerReachesPoint(map, military, military.getTarget());

        assertTrue(military.isInsideBuilding());
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);

        // Cancel evacuation
        assertFalse(watchTower0.needsMilitaryManning());

        watchTower0.cancelEvacuation();

        assertTrue(watchTower0.needsMilitaryManning());
    }

    @Test
    public void testMilitaryGoesBackToStorageWhenWatchTowerIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place watch tower
        var point26 = new Point(8, 8);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point26);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Destroy the watch tower
        assertEquals(watchTower0.getNumberOfHostedSoldiers(), 1);

        watchTower0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarter
        var military = Utils.waitForSoldierOutsideBuilding(player0);

        assertNotNull(military);
        assertEquals(military.getTarget(), headquarter0.getPosition());

        var amount = headquarter0.getAmount(PRIVATE);

        Utils.fastForwardUntilWorkerReachesPoint(map, military, headquarter0.getPosition());

        // Verify that the military is stored correctly in the headquarter
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    }

    @Test
    public void testProductionCannotBeResumedInWatchTower() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place watch tower
        var point26 = new Point(8, 8);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point26);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Verify that production cannot be resumed in watch tower
        try {
            watchTower0.resumeProduction();

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testMilitaryGoesBackOnToStorageOnRoadsIfPossibleWhenWatchTowerIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place watch tower
        var point26 = new Point(8, 8);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point26);

        // Connect the watch tower with the headquarter
        map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Destroy the watch tower
        assertEquals(watchTower0.getNumberOfHostedSoldiers(), 1);

        watchTower0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarter
        var military = Utils.waitForSoldierOutsideBuilding(player0);

        assertNotNull(military);
        assertEquals(military.getTarget(), headquarter0.getPosition());

        // Verify that the worker plans to use the roads
        var firstStep = true;
        for (var point : military.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testCannotStopProductionInBarracks() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(5, 13);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());

        // Wait for the watch tower to finish construction
        Utils.fastForwardUntilBuildingIsConstructed(watchTower0);

        // Occupy the watch tower
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Verify that it's not possible to stop production
        try {
            watchTower0.stopProduction();

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testWatchTowerCannotProduce() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(10, 10);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Populate the watch tower
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Verify that the watch tower can produce
        assertFalse(watchTower0.canProduce());
    }

    @Test
    public void testWatchTowerReportsCorrectOutput() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Construct the watch tower
        Utils.constructHouse(watchTower0);

        // Verify that the reported output is correct
        assertEquals(watchTower0.getProducedMaterial().length, 0);
    }

    @Test
    public void testWatchTowerReportsCorrectMaterialsNeededForConstruction() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Verify that the reported needed construction material is correct
        assertEquals(watchTower0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(watchTower0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(watchTower0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(watchTower0.getCanHoldAmount(PLANK), 3);
        assertEquals(watchTower0.getCanHoldAmount(STONE), 5);

        for (var material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(watchTower0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testWatchTowerReportsCorrectMaterialsNeededForProduction() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Construct the watch tower
        Utils.constructHouse(watchTower0);

        // Verify that the reported needed construction material is correct
        assertEquals(watchTower0.getTypesOfMaterialNeeded().size(), 1);
        assertEquals(watchTower0.getCanHoldAmount(COIN), 3);

        for (var material : Material.values()) {
            if (material == COIN) {
                continue;
            }

            assertEquals(watchTower0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testHostedMilitaryListIsEmptyForWatchTowerUnderConstruction() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Verify that the list of hosted soldiers is empty
        assertEquals(0, watchTower0.getHostedSoldiers().size());
    }

    @Test
    public void testHostedMilitaryListIsEmptyForEmptyWatchTower() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Construct watch tower
        Utils.constructHouse(watchTower0);

        // Verify that the list of hosted soldiers is empty
        assertEquals(0, watchTower0.getHostedSoldiers().size());
    }

    @Test
    public void testAddingMilitaryUpsHostedMilitaryList() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Construct watch tower
        Utils.constructHouse(watchTower0);

        // Add one military
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        // Verify that the list of hosted soldiers increased empty
        assertEquals(1, watchTower0.getHostedSoldiers().size());
        assertEquals(watchTower0.getHostedSoldiers().getFirst().getRank(), PRIVATE_RANK);
    }

    @Test
    public void testRankIsCorrectInHostedMilitaryList() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place watch tower
        var point22 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        // Construct watch tower
        Utils.constructHouse(watchTower0);

        // Add one military
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, 1, watchTower0);

        // Verify that the rank for the hosted military is correct
        assertEquals(watchTower0.getHostedSoldiers().getFirst().getRank(), SERGEANT_RANK);
    }

    @Test
    public void testBorderForWatchTowerIsCorrect() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 80, 81);

        // Place headquarter
        var point0 = new Point(30, 30);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place barracks
        var point1 = new Point(25, 23);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Construct and occupy the barracks
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
        var watchTowerHexagonBorder = Utils.getHexagonBorder(watchTower0.getPosition(), 10);
        var headquarterHexagonBorder = Utils.getHexagonBorder(headquarter0.getPosition(), 9);

        // Verify that all points in the hexagon are part of the actual border
        var border = player0.getBorderPoints();
        for (var point : watchTowerHexagonBorder) {

            // Ignore points that are within the player's land
            if (player0.getOwnedLand().contains(point)) {
                continue;
            }

            assertTrue(border.contains(point));
        }

        // Verify that all points in the actual border are part of the hexagon border
        for (var point : border) {

            // Ignore points that are part of the hexagon around the headquarter
            if (headquarterHexagonBorder.contains(point)) {
                continue;
            }

            assertTrue(watchTowerHexagonBorder.contains(point));
        }
    }

    @Test
    public void testLandForWatchTowerIsCorrect() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 80, 81);

        // Place headquarter
        var point0 = new Point(30, 30);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place barracks
        var point1 = new Point(25, 23);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Construct and occupy the barracks
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
        var area = Utils.getAreaInsideHexagon(9, watchTower0.getPosition());

        // Verify that all points in the hexagon land are part of the actual land
        var land = watchTower0.getDefendedLand();
        for (var point : land) {

            // Ignore points that are part of the headquarters land
            if (headquarter0.getDefendedLand().contains(point)) {
                continue;
            }

            assertTrue(area.contains(point));
        }

        // Verify that all points in the actual land are part of the hexagon land
        for (var point : area) {
            assertTrue(land.contains(point));
        }
    }

    @Test
    public void testDiscoveredLandForWatchTowerIsCorrect() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 80, 81);

        // Place headquarter
        var point0 = new Point(30, 30);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place barracks
        var point1 = new Point(25, 23);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Construct and occupy the barracks
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
        var watchTowerHexagonDiscoveredArea = Utils.getAreaInsideHexagon(14, watchTower0.getPosition());
        var headquarterDiscoveredLand = Utils.getAreaInsideHexagon(13, headquarter0.getPosition());

        // Verify that all points in the hexagon land are part of the actual land
        var discoveredLand = player0.getDiscoveredLand();
        for (var point : discoveredLand) {

            // Ignore points within the discovered land for the headquarter
            if (headquarterDiscoveredLand.contains(point)) {
                continue;
            }

            assertTrue(watchTowerHexagonDiscoveredArea.contains(point));
        }

        // Verify that all points in the actual land are part of the hexagon land
        for (var point : watchTowerHexagonDiscoveredArea) {

            // Filter points outside the map
            if (point.x < 0 || point.y < 0) {
                continue;
            }

            assertTrue(discoveredLand.contains(point));
        }
    }

    @Test
    public void testDiscoveredLandForPlayerCannotBeOutsideTheMap() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 80, 81);

        // Place headquarter
        var point0 = new Point(10, 10);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place barracks
        var point1 = new Point(3, 3);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Construct and occupy the barracks
        Utils.constructHouse(watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Verify that the discovered land is only inside the map
        for (var point : player0.getDiscoveredLand()) {
            assertTrue(point.x >= 0);
            assertTrue(point.y >= 0);
        }
    }

    @Test
    public void testOwnedLandForPlayerCannotBeOutsideTheMap() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 80, 81);

        // Place headquarter
        var point0 = new Point(10, 10);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place barracks
        var point1 = new Point(3, 3);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Construct and occupy the barracks
        Utils.constructHouse(watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Verify that the discovered land is only inside the map
        for (var point : player0.getOwnedLand()) {
            assertTrue(point.x >= 0);
            assertTrue(point.y >= 0);
        }
    }

    @Test
    public void testWatchTowerCanBeUpgraded() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place barracks
        var point1 = new Point(21, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Connect the barracks with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());

        // Finish construction of the barracks
        Utils.constructHouse(watchTower0);

        // Occupy the barracks
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        // Upgrade the barracks
        assertFalse(watchTower0.isUpgrading());

        watchTower0.upgrade();

        // Add materials for the upgrade
        Utils.deliverCargos(watchTower0, PLANK, 1);
        Utils.deliverCargos(watchTower0, STONE, 2);

        // Verify that the upgrade isn't too quick
        for (int i = 0; i < 100; i++) {
            assertTrue(watchTower0.isUpgrading());
            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        var upgradedBuilding = map.getBuildingAtPoint(watchTower0.getPosition());

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

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(13, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Connect the watch tower with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());

        // Upgrade the watch tower
        try {
            watchTower0.upgrade();

            fail();
        } catch (Exception e) { }
    }

    @Test
    public void testBurningWatchTowerCannotBeUpgraded() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(13, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Connect the watch tower with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Tear down the watch tower so it's on fire
        watchTower0.tearDown();

        // Upgrade the watch tower
        try {
            watchTower0.upgrade();

            fail();
        } catch (InvalidUserActionException e) { }
    }

    @Test
    public void testCannotUpgradeWatchTowerWithoutMaterial() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(21, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        // Upgrade the watch tower
        watchTower0.upgrade();

        // Add materials but not enough for the upgrade
        Utils.deliverCargos(watchTower0, PLANK, 1);
        Utils.deliverCargos(watchTower0, STONE, 1); // One less than needed

        // Verify that the upgrade cannot happen without the required material
        for (int i = 0; i < 1000; i++) {
            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        // Add the last required material for the upgrade
        Utils.deliverCargos(watchTower0, STONE, 1);

        // Step time once and verify that the watch tower is upgraded
        map.stepTime();

        assertNotEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));
        assertEquals(map.getBuildingAtPoint(watchTower0.getPosition()).getClass(), Fortress.class);
    }

    @Test
    public void testCannotUpgradeWatchTowerBeingUpgraded() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(13, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Connect the watch tower with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        // Upgrade the watch tower
        watchTower0.upgrade();

        // Verify that the fortress can't get upgraded again
        try {
            watchTower0.upgrade();

            fail();
        } catch (InvalidUserActionException e) { }
    }

    @Test
    public void testUpgradingCausesMaterialToGetDelivered() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Make sure there is material for upgrading
        Utils.adjustInventoryTo(headquarter0, PLANK, 10);
        Utils.adjustInventoryTo(headquarter0, STONE, 10);

        // Place watch tower
        var point1 = new Point(21, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Connect the watch tower with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 6, watchTower0);

        assertFalse(watchTower0.needsMilitaryManning());
        assertFalse(watchTower0.needsMaterial(PRIVATE));

        // Place the courier on the road
        assertNotNull(road0.getCourier());

        var courier0 = road0.getCourier();

        // Verify that the watch tower doesn't need stone before the upgrade
        assertFalse(watchTower0.needsMaterial(STONE));

        // Upgrade the watch tower
        watchTower0.upgrade();

        // Verify that the watch tower needs stone
        assertTrue(watchTower0.needsMaterial(STONE));

        // Verify that the courier picks up a stone or plank
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier0);

        assertNotNull(courier0.getCargo());
        assertTrue(Objects.equals(courier0.getCargo().getMaterial(), STONE) || Objects.equals(courier0.getCargo().getMaterial(), PLANK));

        // Verify that the courier delivers the cargo
        assertEquals(courier0.getCargo().getTarget(), watchTower0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, watchTower0.getPosition());

        assertNull(courier0.getCargo());
    }

    @Test
    public void testOccupiedWatchTowerIsOccupiedAfterUpgrade() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(21, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        assertTrue(watchTower0.isOccupied());

        // Upgrade the watch tower
        watchTower0.upgrade();

        // Add materials for the upgrade
        var stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        // Verify that the upgrade isn't too quick
        for (int i = 0; i < 100; i++) {
            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        // Verify that the upgraded building is also occupied
        var fortress0 = map.getBuildingAtPoint(watchTower0.getPosition());

        assertTrue(fortress0.isOccupied());
        assertEquals(fortress0.getNumberOfHostedSoldiers(), 1);
    }

    @Test
    public void testCoinRemainsAfterUpgrade() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(21, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Upgrade the watch tower
        watchTower0.upgrade();

        // Add materials for the upgrade
        var stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        // Put a coin in the building
        var coinCargo = new Cargo(COIN, map);

        watchTower0.promiseDelivery(COIN);

        watchTower0.putCargo(coinCargo);

        assertEquals(watchTower0.getAmount(COIN), 1);

        // Wait for the upgrade
        for (int i = 0; i < 100; i++) {
            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        // Verify that the coin is still in the building
        var fortress0 = map.getBuildingAtPoint(watchTower0.getPosition());

        assertEquals(fortress0.getAmount(COIN), 1);
    }

    @Test
    public void testBuildingDuringUpgradeCanBeDestroyed() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(21, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        assertTrue(watchTower0.isOccupied());

        // Upgrade the watch tower
        watchTower0.upgrade();

        // Add materials for the upgrade
        var stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        // Upgrade for a while
        for (int i = 0; i < 10; i++) {
            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        // Verify that the building can be destroyed
        watchTower0.tearDown();

        assertTrue(watchTower0.isBurningDown());
    }

    @Test
    public void testPlayerIsCorrectAfterUpgrade() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.GRAY, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 40, 41);

        // Place headquarter
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(21, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        assertTrue(watchTower0.isOccupied());

        // Upgrade the watch tower
        watchTower0.upgrade();

        // Add materials for the upgrade
        var stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        // Put a coin in the building
        var coinCargo = new Cargo(COIN, map);

        watchTower0.promiseDelivery(COIN);

        watchTower0.putCargo(coinCargo);

        assertEquals(watchTower0.getAmount(COIN), 1);

        // Wait for the upgrade
        for (int i = 0; i < 100; i++) {
            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        // Verify that the player is set correctly in the upgraded building
        var fortress0 = map.getBuildingAtPoint(watchTower0.getPosition());

        assertEquals(fortress0.getPlayer(), player0);
    }

    @Test
    public void testCanHostRightNumberOfSoldiersAfterUpgraded() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(21, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        assertTrue(watchTower0.isOccupied());

        // Upgrade the watch tower
        watchTower0.upgrade();

        // Add materials for the upgrade
        var stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        // Wait for the upgrade
        for (int i = 0; i < 100; i++) {
            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        // Verify that two more soldiers can be hosted in the building
        var fortress0 = map.getBuildingAtPoint(watchTower0.getPosition());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);
    }

    @Test
    public void testBorderIsExpandedAfterUpgrade() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(7, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(21, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        assertTrue(watchTower0.isOccupied());

        // Upgrade the watch tower
        watchTower0.upgrade();

        // Add materials for the upgrade
        Utils.deliverCargos(watchTower0, PLANK, 1);
        Utils.deliverCargos(watchTower0, STONE, 2);

        // Verify the border before the upgrade
        var point2 = new Point(36, 10);
        var point3 = new Point(36, 12);

        assertTrue(player0.getBorderPoints().contains(point2));
        assertFalse(player0.getBorderPoints().contains(point3));
        assertFalse(player0.isWithinBorder(point2));

        // Wait for the upgrade
        for (int i = 0; i < 100; i++) {
            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        // Verify that the border is expanded after the upgrade
        assertFalse(player0.getBorderPoints().contains(point2));
        assertTrue(player0.getBorderPoints().contains(point3));
        assertTrue(player0.isWithinBorder(point2));
    }

    @Test
    public void testFlagIsCorrectAfterUpgrade() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(21, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        assertTrue(watchTower0.isOccupied());

        // Upgrade the watch tower
        watchTower0.upgrade();

        // Add materials for the upgrade
        var stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        // Wait for the upgrade
        for (int i = 0; i < 100; i++) {
            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        // Verify that the flag is correct after the upgrade
        var buildingAfterUpgrade = map.getBuildingAtPoint(point1);

        assertNotNull(buildingAfterUpgrade);
        assertNotNull(buildingAfterUpgrade.getFlag());
        assertEquals(buildingAfterUpgrade.getFlag().getPosition(), point1.downRight());
    }

    @Test
    public void testOccupiedBuildingRemainsOccupiedDuringUpgrade() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(21, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        assertTrue(watchTower0.isOccupied());

        // Upgrade the watch tower
        watchTower0.upgrade();

        // Verify that the watch tower is still occupied
        assertTrue(watchTower0.isOccupied());

        // Add materials for the upgrade
        var stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        // Verify that the watch tower is occupied during the upgrade
        for (int i = 0; i < 100; i++) {
            assertTrue(watchTower0.isOccupied());

            map.stepTime();
        }
    }

    @Test
    public void testEvacuatedBuildingKeepsSendingHomeMilitary() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Evacuate the watch tower
        watchTower0.evacuate();

        assertTrue(watchTower0.isEvacuated());
        assertFalse(watchTower0.needsMilitaryManning());

        // Connect headquarter and watch tower
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());

        // Occupy the watch tower
        var military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        // Verify that the military comes out immediately

        map.stepTime();

        assertFalse(military.isInsideBuilding());
        assertEquals(watchTower0.getNumberOfHostedSoldiers(), 0);
    }

    @Test
    public void testCanUpgradeAfterDisablingPromotions() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(21, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        assertTrue(watchTower0.isOccupied());

        // Disable promotions
        watchTower0.disablePromotions();

        assertFalse(watchTower0.isPromotionEnabled());

        // Verify that the watch tower can be upgraded
        watchTower0.upgrade();

        assertTrue(watchTower0.isUpgrading());

        // Add materials for the upgrade
        Utils.deliverCargos(watchTower0, PLANK, 1);
        Utils.deliverCargos(watchTower0, STONE, 2);

        // Wait for the upgrade
        for (int i = 0; i < 100; i++) {
            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        // Verify that the watch tower is upgraded
        assertNotEquals(map.getBuildingAtPoint(watchTower0.getPosition()), watchTower0);

        var buildingAfterUpgrade = map.getBuildingAtPoint(point1);

        assertFalse(buildingAfterUpgrade.isUpgrading());
        assertNotNull(buildingAfterUpgrade);
        assertEquals(buildingAfterUpgrade.getClass(), Fortress.class);
    }

    @Test
    public void testUpgradeDoesNotDestroyNearbyHouses() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place barracks
        var point1 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point1);

        // Finish construction of the barracks
        Utils.constructHouse(barracks0);

        // Occupy the barracks
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0);

        // Place a watch tower
        var point2 = new Point(26, 6);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point2);

        // Construct the watch tower
        Utils.constructHouse(watchTower0);

        // Occupy the watch tower
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, watchTower0);

        // Place regular building
        var point3 = new Point(30, 6);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point3);

        // Connect the buildings with a road
        var road0 = map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), foresterHut0.getFlag());

        // Evacuate the watch tower and wait for the watch tower to become empty
        watchTower0.evacuate();

        for (int i = 0; i < 1000; i++) {
            if (watchTower0.getNumberOfHostedSoldiers() == 0) {
                break;
            }

            map.stepTime();
        }

        assertEquals(watchTower0.getNumberOfHostedSoldiers(), 0);

        // Upgrade the watch tower
        watchTower0.upgrade();

        // Add materials for the upgrade
        var stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        // Wait for the upgrade
        for (int i = 0; i < 100; i++) {
            assertEquals(watchTower0, map.getBuildingAtPoint(watchTower0.getPosition()));

            map.stepTime();
        }

        // Verify that the forester hut and the road remains
        assertEquals(map.getBuildingAtPoint(watchTower0.getPosition()).getClass(), WatchTower.class);
        assertTrue(map.isBuildingAtPoint(foresterHut0.getPosition()));
        assertEquals(map.getBuildingAtPoint(foresterHut0.getPosition()), foresterHut0);
        assertTrue(map.getRoads().contains(road0));
    }

    @Test
    public void testUnoccupiedBuildingRemainsUnoccupiedDuringAndAfterUpgrade() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(7, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(21, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Upgrade the watch tower
        assertFalse(watchTower0.isOccupied());

        watchTower0.upgrade();

        // Verify that the watch tower is still unoccupied
        assertFalse(watchTower0.isOccupied());

        // Add materials for the upgrade
        var stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        // Verify that the watch tower is unoccupied during and after the upgrade
        var point2 = new Point(25, 5);
        var point3 = new Point(27, 5);

        for (int i = 0; i < 100; i++) {
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

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place watch tower
        var point1 = new Point(21, 5);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        // Connect the watch tower with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());

        // Finish construction of the watch tower
        Utils.constructHouse(watchTower0);

        // Fill the watch tower with soldiers
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 6, watchTower0);

        assertEquals(watchTower0.getNumberOfHostedSoldiers(), 6);

        // Make sure there are enough soldiers in the headquarter
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 200);

        // Upgrade the watch tower
        watchTower0.upgrade();

        // Add materials for the upgrade
        Utils.deliverCargos(watchTower0, PLANK, 1);
        Utils.deliverCargos(watchTower0, STONE, 2);

        // Wait for the upgrade to happen
        assertEquals(watchTower0.getNumberOfHostedSoldiers(), 6);

        var fortress0 = Utils.waitForBuildingToGetUpgraded(watchTower0);

        assertEquals(fortress0.getNumberOfHostedSoldiers(), 6);
        assertEquals(map.getBuildingAtPoint(watchTower0.getPosition()), fortress0);
        assertEquals(fortress0.getMaxHostedSoldiers(), 9);

        // Verify that only three soldiers are sent out to occupy the building

        // Wait for three soldiers to occupy the building
        Utils.waitForMilitaryBuildingToGetPopulated(fortress0, 9);

        // Verify that no more soldiers are sent out
        assertFalse(fortress0.needsMilitaryManning());
        assertEquals(fortress0.getNumberOfHostedSoldiers(), 9);

        for (int i = 0; i < 2000; i++) {
            assertNull(Utils.findSoldierOutsideBuilding(player0));

            map.stepTime();
        }
    }

    @Test
    public void testUpgradedWatchTowerGetsPopulatedFully() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Add privates to the headquarter
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 20);

        // Place watch tower
        var point1 = new Point(6, 12);
        var watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        Utils.constructHouse(watchTower0);

        // Connect the watch tower to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());

        // Wait for the watch tower to get occupied
        for (int i = 0; i < 1000; i++) {
            if (watchTower0.getNumberOfHostedSoldiers() == 2) {
                break;
            }

            map.stepTime();
        }

        assertEquals(watchTower0.getNumberOfHostedSoldiers(), 2);

        // Upgrade the watch tower
        watchTower0.upgrade();

        // Add materials for the upgrade
        var stoneCargo = new Cargo(STONE, map);

        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);
        watchTower0.promiseDelivery(STONE);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        // Wait for the upgrade to happen
        var fortress0 = Utils.waitForBuildingToGetUpgraded(watchTower0);

        assertEquals(map.getBuildingAtPoint(watchTower0.getPosition()), fortress0);
        assertEquals(fortress0.getMaxHostedSoldiers(), 9);

        // Verify that the building gets fully occupied
        for (int i = 0; i < 500; i++) {
            if (fortress0.getNumberOfHostedSoldiers() == 9) {
                break;
            }

            map.stepTime();
        }

        assertEquals(fortress0.getNumberOfHostedSoldiers(), 9);
    }
}
