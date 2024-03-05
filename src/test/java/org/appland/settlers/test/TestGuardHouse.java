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
import org.appland.settlers.model.Soldier;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.WatchTower;
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
import static org.appland.settlers.model.Soldier.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Soldier.Rank.PRIVATE_FIRST_CLASS_RANK;
import static org.appland.settlers.model.Soldier.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Soldier.Rank.SERGEANT_RANK;
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
public class TestGuardHouse {

    /*
    TODO: test upgrade
     */

    @Test
    public void testGuardHouseNeedsThreePlanksAndTwoStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Deliver three planks and two stones */
        Cargo cargo = new Cargo(PLANK, map);

        guardHouse0.putCargo(cargo);
        guardHouse0.putCargo(cargo);
        guardHouse0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(guardHouse0);

        /* Verify that this is enough to construct the guard house */
        for (int i = 0; i < 100; i++) {
            assertTrue(guardHouse0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(guardHouse0.isReady());
    }

    @Test
    public void testGuardHouseCannotBeConstructedWithOnePlankTooLittle() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Deliver one plank and three stones */
        Cargo cargo = new Cargo(PLANK, map);

        guardHouse0.putCargo(cargo);
        guardHouse0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(guardHouse0);

        /* Verify that this is enough to construct the guard house */
        for (int i = 0; i < 500; i++) {
            assertTrue(guardHouse0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(guardHouse0.isReady());
    }

    @Test
    public void testGuardHouseCannotBeConstructedWithOneStoneTooLittle() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Deliver one plank and three stones */
        Cargo cargo = new Cargo(PLANK, map);

        guardHouse0.putCargo(cargo);
        guardHouse0.putCargo(cargo);
        guardHouse0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(guardHouse0);

        /* Verify that this is enough to construct the guard house */
        for (int i = 0; i < 500; i++) {
            assertTrue(guardHouse0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(guardHouse0.isReady());
    }

    @Test
    public void testGuardHouseGetPopulatedWhenFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Connect the guard house with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, guardHouse0.getFlag(), headquarter0.getFlag());

        /* Wait for the guard house to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(guardHouse0);

        /* Verify that a military is sent from the headquarter */
        map.stepTime();

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Soldier.class);

        Soldier military = null;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Soldier) {
                military = (Soldier)worker;
            }
        }

        assertNotNull(military);

        /* Wait for the military to reach the guard house */
        assertEquals(military.getTarget(), guardHouse0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, military, guardHouse0.getPosition());

        assertTrue(military.isInsideBuilding());
    }

    @Test
    public void testBorderIsNotExtendedWhenGuardHouseIsFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 17);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(5, 23);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), guardHouse0.getFlag());

        /* Wait for the guard house to finish construction */
        Point point2 = new Point(6, 26);

        assertTrue(player0.getBorderPoints().contains(point2));

        Utils.fastForwardUntilBuildingIsConstructed(guardHouse0);

        assertTrue(player0.getBorderPoints().contains(point2));
    }

    @Test
    public void testBorderIsExtendedWhenGuardHouseIsPopulated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(5, 23);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), guardHouse0.getFlag());

        /* Wait for the guard house to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(guardHouse0);

        /* Verify that a military is sent from the headquarter */
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);

        map.stepTime();

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Soldier.class);

        Soldier military = null;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Soldier) {
                military = (Soldier)worker;
            }
        }

        assertNotNull(military);

        /* Verify that the border is extended when the military reaches the guard house */
        Point point2 = new Point(6, 24);
        Point point3 = new Point(6, 32);

        assertEquals(military.getTarget(), guardHouse0.getPosition());
        assertTrue(player0.getBorderPoints().contains(point2));
        assertFalse(player0.getBorderPoints().contains(point3));

        Utils.fastForwardUntilWorkerReachesPoint(map, military, guardHouse0.getPosition());

        assertFalse(player0.getBorderPoints().contains(point2));
        assertTrue(player0.getBorderPoints().contains(point3));
    }

    @Test
    public void testGuardHouseOnlyNeedsThreeSoldiers() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house with two soldiers */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

        /* Verify that the guard house does not need another military */
        assertFalse(guardHouse0.needsMilitaryManning());
    }

    @Test
    public void testGuardHouseCannotHoldSoldiersBeforeFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Verify that the guard house can't hold soldiers before it's finished */
        assertFalse(guardHouse0.needsMilitaryManning());

        Soldier military = new Soldier(player0, PRIVATE_RANK, map);

        map.placeWorker(military, guardHouse0);

        try {
            military.enterBuilding(guardHouse0);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testGuardHouseCannotHoldMoreThanThreeSoldiers() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house with two soldiers */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

        /* Verify that the guard house does not need another military */
        Soldier military = new Soldier(player0, PRIVATE_RANK, map);

        map.placeWorker(military, guardHouse0);

        try {
            military.enterBuilding(guardHouse0);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testGuardHouseNeedsCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        Utils.constructHouse(guardHouse0);

        assertTrue(guardHouse0.needsMaterial(COIN));
    }

    @Test
    public void testUnfinishedGuardHouseNotNeedsCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        assertFalse(guardHouse0.needsMaterial(COIN));
    }

    @Test
    public void testGuardHouseCanHoldTwoCoins() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        Utils.constructHouse(guardHouse0);

        assertTrue(guardHouse0.needsMaterial(COIN));

        /* Deliver one coin to the guard house */
        Cargo cargo = new Cargo(COIN, map);

        guardHouse0.promiseDelivery(COIN);
        guardHouse0.promiseDelivery(COIN);

        guardHouse0.putCargo(cargo);
        guardHouse0.putCargo(cargo);

        /* Verify that the guard house can't hold another coin */
        assertFalse(guardHouse0.needsMaterial(COIN));
        assertEquals(guardHouse0.getAmount(COIN), 2);

        try {
            guardHouse0.putCargo(cargo);
            fail();
        } catch (Exception e) {}

        assertEquals(guardHouse0.getAmount(COIN), 2);
    }

    @Test
    public void testPrivateIsPromotedWhenCoinIsAvailable() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        Utils.constructHouse(guardHouse0);

        /* Deliver one coin to the guard house */
        Cargo cargo = new Cargo(COIN, map);

        guardHouse0.putCargo(cargo);

        /* Occupy the guard house with one private */
        Soldier military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

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

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        Utils.constructHouse(guardHouse0);

        /* Deliver one coin to the guard house */
        Cargo cargo = new Cargo(COIN, map);

        guardHouse0.putCargo(cargo);

        /* Occupy the guard house with one private */
        Soldier military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);
        Soldier military2 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

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

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        Utils.constructHouse(guardHouse0);

        /* Deliver one coin to the guard house */
        Cargo cargo = new Cargo(COIN, map);

        guardHouse0.putCargo(cargo);

        /* Wait before the guard house is populated */
        Utils.fastForward(200, map);

        /* Occupy the guard house with one private */
        Soldier military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

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

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        Utils.constructHouse(guardHouse0);

        /* Deliver one coin to the guard house */
        Cargo cargo = new Cargo(COIN, map);

        guardHouse0.putCargo(cargo);

        /* Occupy the guard house with one private */
        Soldier military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

        /* Verify that the promotion consumes the coin */
        assertEquals(guardHouse0.getAmount(COIN), 1);

        Utils.fastForward(100, map);

        assertEquals(guardHouse0.getAmount(COIN), 0);
    }

    @Test
    public void testPromotionOnlyConsumesOneCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        Utils.constructHouse(guardHouse0);

        /* Deliver one coin to the guard house */
        Cargo cargo = new Cargo(COIN, map);

        guardHouse0.putCargo(cargo);
        guardHouse0.putCargo(cargo);

        /* Occupy the guard house with one private */
        Soldier military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);
        Soldier military2 = Utils.occupyMilitaryBuilding(SERGEANT_RANK, guardHouse0);

        /* Verify that the promotion consumes the coin */
        assertEquals(guardHouse0.getAmount(COIN), 2);

        Utils.fastForward(100, map);

        assertEquals(guardHouse0.getAmount(COIN), 1);
    }

    @Test
    public void testGuardHouseWithNoPromotionPossibleDoesNotConsumeCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        Utils.constructHouse(guardHouse0);

        /* Deliver one coin to the guard house */
        Cargo cargo = new Cargo(COIN, map);

        guardHouse0.putCargo(cargo);

        /* Occupy the guard house with one private */
        Soldier military1 = Utils.occupyMilitaryBuilding(GENERAL_RANK, guardHouse0);
        Soldier military2 = Utils.occupyMilitaryBuilding(GENERAL_RANK, guardHouse0);

        /* Verify that coin is not consumed */
        assertEquals(guardHouse0.getAmount(COIN), 1);

        Utils.fastForward(100, map);

        assertEquals(guardHouse0.getAmount(COIN), 1);
    }

    @Test
    public void testCanDisableCoinsToGuardHouse() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        Utils.constructHouse(guardHouse0);

        /* Deliver one coin to the guard house */
        assertTrue(guardHouse0.needsMaterial(COIN));

        /* Disable coins to the guard house and verify that it doesn't need coins */
        guardHouse0.disablePromotions();

        assertFalse(guardHouse0.needsMaterial(COIN));
    }

    @Test
    public void testOccupiedGuardHouseCanBeEvacuated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Connect headquarter and guard house */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), guardHouse0.getFlag());

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Soldier military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

        /* Evacuate the guard house and verify that the military leaves the guard house */
        assertTrue(military.isInsideBuilding());

        guardHouse0.evacuate();

        map.stepTime();

        assertFalse(military.isInsideBuilding());
        assertEquals(guardHouse0.getNumberOfHostedSoldiers(), 0);
    }

    @Test
    public void testEvacuatedMilitaryReturnsToStorage() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Connect headquarter and guard house */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), guardHouse0.getFlag());

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Soldier military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

        /* Evacuate the guard house */
        assertTrue(military.isInsideBuilding());

        guardHouse0.evacuate();

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

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Soldier military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

        /* Evacuate the guard house */
        assertTrue(military.isInsideBuilding());

        guardHouse0.evacuate();

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
    public void testNoMilitaryIsDispatchedToEvacuatedGuardHouse() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Connect headquarters and guard house */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), guardHouse0.getFlag());

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Evacuate the guard house */
        guardHouse0.evacuate();

        /* Verify that no soldiers are assigned to the guard house */
        for (int i = 0; i < 200; i++) {
            assertEquals(guardHouse0.getNumberOfHostedSoldiers(), 0);
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

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Soldier military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

        /* Evacuate the guard house */
        assertTrue(military.isInsideBuilding());

        guardHouse0.evacuate();

        map.stepTime();

        assertFalse(military.isInsideBuilding());

        /* Wait for the evacuated military to return to the storage */
        assertEquals(military.getTarget(), headquarter0.getPosition());
        int amount = headquarter0.getAmount(PRIVATE);

        Utils.fastForwardUntilWorkerReachesPoint(map, military, military.getTarget());

        assertTrue(military.isInsideBuilding());
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);

        /* Cancel evacuation */
        assertFalse(guardHouse0.needsMilitaryManning());

        guardHouse0.cancelEvacuation();

        assertTrue(guardHouse0.needsMilitaryManning());
    }

    @Test
    public void testMilitaryGoesBackToStorageWhenGuardHouseIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(8, 8);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

        /* Destroy the guard house */
        assertEquals(guardHouse0.getNumberOfHostedSoldiers(), 1);

        guardHouse0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        Soldier military = Utils.waitForSoldierOutsideBuilding(player0);

        int amount = headquarter0.getAmount(PRIVATE);

        assertNotNull(military);
        assertEquals(military.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, military, headquarter0.getPosition());

        /* Verify that the military is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    }

    @Test
    public void testMilitaryGoesBackOnToStorageOnRoadsIfPossibleWhenGuardHouseIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(8, 8);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Connect the guard house with the headquarter */
        map.placeAutoSelectedRoad(player0, guardHouse0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

        /* Destroy the guard house */
        assertEquals(guardHouse0.getNumberOfHostedSoldiers(), 1);

        guardHouse0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        Soldier military = Utils.waitForSoldierOutsideBuilding(player0);

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
    public void testProductionCannotBeResumedInGuardHouse() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(8, 8);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Verify that production cannot be resumed in guard house */
        try {
            guardHouse0.resumeProduction();

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCannotStopProductionInGuardhouse() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 17);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(5, 23);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), guardHouse0.getFlag());

        /* Wait for the guard house to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(guardHouse0);

        /* Occupy the guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

        /* Verify that it's not possible to stop production */
        try {
            guardHouse0.stopProduction();
            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testGuardHouseCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house mine */
        Point point1 = new Point(10, 10);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Populate the guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

        /* Verify that the guard house can produce */
        assertFalse(guardHouse0.canProduce());
    }

    @Test
    public void testGuardHouseReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Construct the guard house */
        Utils.constructHouse(guardHouse0);

        /* Verify that the reported output is correct */
        assertEquals(guardHouse0.getProducedMaterial().length, 0);
    }

    @Test
    public void testGuardHouseReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(guardHouse0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(guardHouse0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(guardHouse0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(guardHouse0.getCanHoldAmount(PLANK), 3);
        assertEquals(guardHouse0.getCanHoldAmount(STONE), 2);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(guardHouse0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testGuardHouseReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Construct the guard house */
        Utils.constructHouse(guardHouse0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(guardHouse0.getTypesOfMaterialNeeded().size(), 1);
        assertEquals(guardHouse0.getCanHoldAmount(COIN), 2);

        for (Material material : Material.values()) {
            if (material == COIN) {
                continue;
            }

            assertEquals(guardHouse0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testHostedMilitaryListIsEmptyForGuardHouseUnderConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Verify that the list of hosted soldiers is empty */
        assertEquals(0, guardHouse0.getHostedSoldiers().size());
    }

    @Test
    public void testHostedMilitaryListIsEmptyForEmptyGuardHouse() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Construct guard house */
        Utils.constructHouse(guardHouse0);

        /* Verify that the list of hosted soldiers is empty */
        assertEquals(0, guardHouse0.getHostedSoldiers().size());
    }

    @Test
    public void testAddingMilitaryUpsHostedMilitaryList() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Construct guard house */
        Utils.constructHouse(guardHouse0);

        /* Add one military */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, guardHouse0);

        /* Verify that the list of hosted soldiers increased empty */
        assertEquals(1, guardHouse0.getHostedSoldiers().size());
        assertEquals(guardHouse0.getHostedSoldiers().get(0).getRank(), PRIVATE_RANK);
    }

    @Test
    public void testRankIsCorrectInHostedMilitaryList() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Construct guard house */
        Utils.constructHouse(guardHouse0);

        /* Add one military */
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, 1, guardHouse0);

        /* Verify that the rank for the hosted military is correct */
        assertEquals(guardHouse0.getHostedSoldiers().get(0).getRank(), SERGEANT_RANK);
    }

    @Test
    public void testBorderForGuardHouseIsCorrect() throws Exception {

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
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(guardHouse0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, guardHouse0);

        /* Verify that the border around the barracks is hexagon shaped and the middle of each line is 8 steps away from the center of the headquarter
        Border:

                -8, +8  -------  +8, +8
                  /                  \
            -16, 0        H          16, 0
                  \                  /
                -8, -8  -------  +8, +8

         */

        int radius = 9;
        Set<Point> barracksHexagonBorder = Utils.getHexagonBorder(guardHouse0.getPosition(), radius);
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
    public void testLandForGuardHouseIsCorrect() throws Exception {

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
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(guardHouse0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, guardHouse0);

        /* Verify that the land of the headquarter is hexagon shaped and the middle of each line is 9 steps away from the center of the headquarter
        Land

                -8, +8  -------  +8, +8
                  /                  \
            -16, 0        H          16, 0
                  \                  /
                -8, -8  -------  +8, +8

         */
        Set<Point> area = Utils.getAreaInsideHexagon(8, guardHouse0.getPosition());

        /* Verify that all points in the hexagon land are part of the actual land */
        Collection<Point> land = guardHouse0.getDefendedLand();
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
    public void testDiscoveredLandForGuardHouseIsCorrect() throws Exception {

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
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(guardHouse0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, guardHouse0);

        /* Verify that the discovered land of the barracks is hexagon shaped and the middle of each line is 8 + 4 steps away
        from the center of the headquarter

         Land

                -8, +8  -------  +8, +8
                  /                  \
            -16, 0        H          16, 0
                  \                  /
                -8, -8  -------  +8, +8

         */
        Set<Point> guardHouseHexagonDiscoveredArea = Utils.getAreaInsideHexagon(13, guardHouse0.getPosition());
        Set<Point> headquarterDiscoveredLand = Utils.getAreaInsideHexagon(13, headquarter0.getPosition());

        /* Verify that all points in the hexagon land are part of the actual land */
        Collection<Point> discoveredLand = player0.getDiscoveredLand();
        for (Point point : discoveredLand) {

            /* Ignore points within the discovered land for the headquarter */
            if (headquarterDiscoveredLand.contains(point)) {
                continue;
            }

            assertTrue(guardHouseHexagonDiscoveredArea.contains(point));
        }

        /* Verify that all points in the actual land are part of the hexagon land */
        for (Point point : guardHouseHexagonDiscoveredArea) {

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
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(guardHouse0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

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
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Construct and occupy the barracks */
        Utils.constructHouse(guardHouse0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

        /* Verify that the discovered land is only inside the map */
        for (Point point : player0.getLandInPoints()) {
            assertTrue(point.x >= 0);
            assertTrue(point.y >= 0);
        }
    }

    @Test
    public void testGuardHouseCanBeUpgraded() throws Exception {

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
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, guardHouse0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(guardHouse0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, guardHouse0);

        /* Upgrade the barracks */
        assertFalse(guardHouse0.isUpgrading());

        guardHouse0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Verify that the upgrade isn't too quick */
        for (int i = 0; i < 100; i++) {

            assertTrue(guardHouse0.isUpgrading());
            assertEquals(guardHouse0, map.getBuildingAtPoint(guardHouse0.getPosition()));

            map.stepTime();
        }

        Building upgradedBuilding = map.getBuildingAtPoint(guardHouse0.getPosition());

        assertFalse(map.getBuildings().contains(guardHouse0));
        assertTrue(map.getBuildings().contains(upgradedBuilding));
        assertFalse(player0.getBuildings().contains(guardHouse0));
        assertTrue(player0.getBuildings().contains(upgradedBuilding));
        assertNotNull(upgradedBuilding);
        assertFalse(upgradedBuilding.isUpgrading());
        assertEquals(upgradedBuilding.getClass(), WatchTower.class);
    }

    @Test
    public void testUnfinishedGuardHouseCannotBeUpgraded() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(13, 5);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Connect the guard house with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, guardHouse0.getFlag(), headquarter0.getFlag());

        /* Upgrade the guard house */
        try {
            guardHouse0.upgrade();

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testBurningGuardHouseCannotBeUpgraded() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(13, 5);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Connect the guard house with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, guardHouse0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Tear down the guard house so it's on fire */
        guardHouse0.tearDown();

        /* Upgrade the guard house */
        try {
            guardHouse0.upgrade();

            fail();
        } catch (InvalidUserActionException e) {}
    }

    @Test
    public void testCannotUpgradeGuardHouseWithoutMaterial() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(21, 5);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, guardHouse0);

        /* Upgrade the guard house */
        guardHouse0.upgrade();

        /* Add materials but not enough for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Verify that the upgrade cannot happen without the required material */
        for (int i = 0; i < 1000; i++) {

            assertEquals(guardHouse0, map.getBuildingAtPoint(guardHouse0.getPosition()));

            map.stepTime();
        }

        /* Add the last required material for the upgrade */
        guardHouse0.promiseDelivery(STONE);

        guardHouse0.putCargo(stoneCargo);

        /* Step time once and verify that the guard house is upgraded */
        map.stepTime();

        assertNotEquals(guardHouse0, map.getBuildingAtPoint(guardHouse0.getPosition()));
        assertEquals(map.getBuildingAtPoint(guardHouse0.getPosition()).getClass(), WatchTower.class);
    }

    @Test
    public void testCannotUpgradeGuardHouseBeingUpgraded() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(13, 5);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Connect the guard house with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, guardHouse0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, guardHouse0);

        /* Upgrade the guard house */
        guardHouse0.upgrade();

        /* Verify that the guard house can't get upgraded again */
        try {
            guardHouse0.upgrade();

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

        /* Place guard house */
        Point point1 = new Point(21, 5);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Connect the guard house with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, guardHouse0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 3, guardHouse0);

        assertFalse(guardHouse0.needsMilitaryManning());
        assertFalse(guardHouse0.needsMaterial(PRIVATE));

        /* Place the courier on the road */
        assertNotNull(road0.getCourier());

        Courier courier0 = road0.getCourier();

        /* Verify that the guard house doesn't need stone before the upgrade */
        assertFalse(guardHouse0.needsMaterial(STONE));

        /* Upgrade the guard house */
        guardHouse0.upgrade();

        /* Verify that the guard house needs stone */
        assertTrue(guardHouse0.needsMaterial(STONE));

        /* Verify that the courier picks up a stone */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier0);

        assertEquals(courier0.getCargo().getMaterial(), STONE);

        /* Verify that the courier delivers the stone */
        assertEquals(courier0.getCargo().getTarget(), guardHouse0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, guardHouse0.getPosition());

        assertNull(courier0.getCargo());

        /* Verify that the courier picks up the second stone */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier0);

        assertEquals(courier0.getCargo().getMaterial(), STONE);

        /* Verify that the courier delivers the stone */
        assertEquals(courier0.getCargo().getTarget(), guardHouse0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, guardHouse0.getPosition());

        assertNull(courier0.getCargo());

        /* Verify that the courier picks up the third stone */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier0);

        assertEquals(courier0.getCargo().getMaterial(), STONE);

        /* Verify that the courier delivers the stone */
        assertEquals(courier0.getCargo().getTarget(), guardHouse0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, guardHouse0.getPosition());

        assertNull(courier0.getCargo());

        /* Verify that the courier doesn't deliver anything else to the guard house */
        for (int i = 0; i < 1000; i++) {

            assertTrue(courier0.getCargo() == null || !courier0.getCargo().getTarget().equals(guardHouse0));

            map.stepTime();
        }
    }

    @Test
    public void testOccupiedGuardHouseIsOccupiedAfterUpgrade() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house */
        Point point1 = new Point(21, 5);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, guardHouse0);

        assertTrue(guardHouse0.isOccupied());

        /* Upgrade the guard house */
        guardHouse0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Verify that the upgrade isn't too quick */
        for (int i = 0; i < 100; i++) {

            assertEquals(guardHouse0, map.getBuildingAtPoint(guardHouse0.getPosition()));

            map.stepTime();
        }

        /* Verify that the upgraded building is also occupied */
        Building watchTower0 = map.getBuildingAtPoint(guardHouse0.getPosition());

        assertTrue(watchTower0.isOccupied());
        assertEquals(watchTower0.getNumberOfHostedSoldiers(), 1);
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

        /* Place guard house */
        Point point1 = new Point(21, 5);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Upgrade the guard house */
        guardHouse0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Put a coin in the building */
        Cargo coinCargo = new Cargo(COIN, map);

        guardHouse0.promiseDelivery(COIN);

        guardHouse0.putCargo(coinCargo);

        assertEquals(guardHouse0.getAmount(COIN), 1);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(guardHouse0, map.getBuildingAtPoint(guardHouse0.getPosition()));

            map.stepTime();
        }

        /* Verify that the coin is still in the building */
        Building watchTower0 = map.getBuildingAtPoint(guardHouse0.getPosition());

        assertEquals(watchTower0.getAmount(COIN), 1);
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

        /* Place guard house */
        Point point1 = new Point(21, 5);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, guardHouse0);

        assertTrue(guardHouse0.isOccupied());

        /* Upgrade the guard house */
        guardHouse0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Upgrade for a while */
        for (int i = 0; i < 10; i++) {

            assertEquals(guardHouse0, map.getBuildingAtPoint(guardHouse0.getPosition()));

            map.stepTime();
        }

        /* Verify that the building can be destroyed */
        guardHouse0.tearDown();

        assertTrue(guardHouse0.isBurningDown());
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

        /* Place guard house */
        Point point1 = new Point(21, 5);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, guardHouse0);

        assertTrue(guardHouse0.isOccupied());

        /* Upgrade the guard house */
        guardHouse0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Put a coin in the building */
        Cargo coinCargo = new Cargo(COIN, map);

        guardHouse0.promiseDelivery(COIN);

        guardHouse0.putCargo(coinCargo);

        assertEquals(guardHouse0.getAmount(COIN), 1);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {
            assertEquals(guardHouse0, map.getBuildingAtPoint(guardHouse0.getPosition()));

            map.stepTime();
        }

        /* Verify that the player is set correctly in the upgraded building */
        Building watchTower0 = map.getBuildingAtPoint(guardHouse0.getPosition());

        assertEquals(watchTower0.getPlayer(), player0);
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

        /* Place guard house */
        Point point1 = new Point(21, 5);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, guardHouse0);

        assertTrue(guardHouse0.isOccupied());

        /* Upgrade the guard house */
        guardHouse0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(guardHouse0, map.getBuildingAtPoint(guardHouse0.getPosition()));

            map.stepTime();
        }

        /* Verify that two more soldiers can be hosted in the building */
        Building watchTower0 = map.getBuildingAtPoint(guardHouse0.getPosition());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);
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

        /* Place guard house */
        Point point1 = new Point(21, 5);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, guardHouse0);

        assertTrue(guardHouse0.isOccupied());

        /* Upgrade the guard house */
        guardHouse0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Verify the border before the upgrade */
        Point point2 = new Point(37, 7);
        Point point3 = new Point(39, 7);

        assertTrue(player0.getBorderPoints().contains(point2));
        assertFalse(player0.getBorderPoints().contains(point3));
        assertFalse(player0.isWithinBorder(point2));

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(guardHouse0, map.getBuildingAtPoint(guardHouse0.getPosition()));

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

        /* Place guard house */
        Point point1 = new Point(21, 5);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, guardHouse0);

        assertTrue(guardHouse0.isOccupied());

        /* Upgrade the guard house */
        guardHouse0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(guardHouse0, map.getBuildingAtPoint(guardHouse0.getPosition()));

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

        /* Place guard house */
        Point point1 = new Point(21, 5);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, guardHouse0);

        assertTrue(guardHouse0.isOccupied());

        /* Upgrade the guard house */
        guardHouse0.upgrade();

        /* Verify that the guard house is still occupied */
        assertTrue(guardHouse0.isOccupied());

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Verify that the guard house is occupied during the upgrade */
        for (int i = 0; i < 100; i++) {

            /* Verify that the guard house is still occupied */
            assertTrue(guardHouse0.isOccupied());

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

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Evacuate the guard house */
        guardHouse0.evacuate();

        assertTrue(guardHouse0.isEvacuated());
        assertFalse(guardHouse0.needsMilitaryManning());

        /* Connect headquarter and guard house */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), guardHouse0.getFlag());

        /* Occupy the guard house */
        Soldier military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0);

        /* Verify that the military comes out immediately */

        map.stepTime();

        assertFalse(military.isInsideBuilding());
        assertEquals(guardHouse0.getNumberOfHostedSoldiers(), 0);
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

        /* Place guard house */
        Point point1 = new Point(21, 5);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, guardHouse0);

        assertTrue(guardHouse0.isOccupied());

        /* Disable promotions */
        guardHouse0.disablePromotions();

        assertFalse(guardHouse0.isPromotionEnabled());

        /* Verify that the guard house can be upgraded */
        guardHouse0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(guardHouse0, map.getBuildingAtPoint(guardHouse0.getPosition()));

            map.stepTime();
        }

        /* Verify that the guard house is upgraded */
        Building buildingAfterUpgrade = map.getBuildingAtPoint(point1);

        assertNotNull(buildingAfterUpgrade);
        assertEquals(buildingAfterUpgrade.getClass(), WatchTower.class);
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

        /* Place a guard house */
        Point point2 = new Point(26, 6);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point2);

        /* Construct the guard house */
        Utils.constructHouse(guardHouse0);

        /* Occupy the guard house */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, guardHouse0);

        /* Place regular building */
        Point point3 = new Point(30, 6);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point3);

        /* Connect the buildings with a road */
        Road road0 = map.placeAutoSelectedRoad(player0, guardHouse0.getFlag(), foresterHut0.getFlag());

        /* Evacuate the guard house and wait for the guard house to become empty */
        guardHouse0.evacuate();

        for (int i = 0; i < 1000; i++) {

            if (guardHouse0.getNumberOfHostedSoldiers() == 0) {
                break;
            }

            map.stepTime();
        }

        assertEquals(guardHouse0.getNumberOfHostedSoldiers(), 0);

        /* Upgrade the guard house */
        guardHouse0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(guardHouse0, map.getBuildingAtPoint(guardHouse0.getPosition()));

            map.stepTime();
        }

        /* Verify that the forester hut and the road remains */
        assertEquals(map.getBuildingAtPoint(guardHouse0.getPosition()).getClass(), WatchTower.class);
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

        /* Place guard house */
        Point point1 = new Point(21, 5);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Upgrade the guard house */
        assertFalse(guardHouse0.isOccupied());

        guardHouse0.upgrade();

        /* Verify that the guard house is still unoccupied */
        assertFalse(guardHouse0.isOccupied());

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Verify that the guard house is unoccupied during and after the upgrade */
        Point point2 = new Point(25, 5);
        Point point3 = new Point(27, 5);

        for (int i = 0; i < 100; i++) {

            /* Verify that the guard house is still occupied */
            assertFalse(guardHouse0.isOccupied());
            assertTrue(player0.getBorderPoints().contains(point2));
            assertFalse(player0.isWithinBorder(point3));

            map.stepTime();
        }

        assertEquals(map.getBuildingAtPoint(guardHouse0.getPosition()).getClass(), WatchTower.class);
        assertFalse(map.getBuildingAtPoint(guardHouse0.getPosition()).isOccupied());
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

        /* Place guard house */
        Point point1 = new Point(21, 5);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        /* Connect the guard house with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), guardHouse0.getFlag());

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0);

        /* Fill the guard house with soldiers */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 3, guardHouse0);

        assertEquals(guardHouse0.getNumberOfHostedSoldiers(), 3);

        /* Make sure there are enough soldiers in the headquarter */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 200);

        /* Upgrade the guard house */
        guardHouse0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Wait for the upgrade to happen */
        assertEquals(guardHouse0.getNumberOfHostedSoldiers(), 3);

        Building watchTower0 = Utils.waitForBuildingToGetUpgraded(guardHouse0);

        assertEquals(watchTower0.getNumberOfHostedSoldiers(), 3);
        assertEquals(map.getBuildingAtPoint(guardHouse0.getPosition()), watchTower0);
        assertEquals(watchTower0.getMaxHostedSoldiers(), 6);

        /* Verify that only three soldiers are sent out to occupy the building */

        /* Wait for three soldiers to occupy the building */
        Utils.waitForMilitaryBuildingToGetPopulated(watchTower0, 6);

        /* Verify that no more soldiers are sent out */
        assertFalse(watchTower0.needsMilitaryManning());
        assertEquals(watchTower0.getNumberOfHostedSoldiers(), 6);

        for (int i = 0; i < 2000; i++) {

            assertNull(Utils.findSoldierOutsideBuilding(player0));

            map.stepTime();
        }
    }

    @Test
    public void testUpgradedGuardHouseGetsPopulatedFully() throws Exception {

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

        /* Place guard house */
        Point point1 = new Point(6, 12);
        GuardHouse guardHouse0 = map.placeBuilding(new GuardHouse(player0), point1);

        Utils.constructHouse(guardHouse0);

        /* Connect the guard house to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, guardHouse0.getFlag(), headquarter0.getFlag());

        /* Wait for the guard house to get occupied */
        for (int i = 0; i < 1000; i++) {

            if (guardHouse0.getNumberOfHostedSoldiers() == 2) {
                break;
            }

            map.stepTime();
        }

        assertEquals(guardHouse0.getNumberOfHostedSoldiers(), 2);

        /* Upgrade the guard house */
        guardHouse0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);
        guardHouse0.promiseDelivery(STONE);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Wait for the upgrade to happen */
        Building watchTower0 = Utils.waitForBuildingToGetUpgraded(guardHouse0);

        assertEquals(map.getBuildingAtPoint(guardHouse0.getPosition()), watchTower0);
        assertEquals(watchTower0.getMaxHostedSoldiers(), 6);

        /* Verify that the building gets fully occupied */
        for (int i = 0; i < 500; i++) {

            if (watchTower0.getNumberOfHostedSoldiers() == 6) {
                break;
            }

            map.stepTime();
        }

        assertEquals(watchTower0.getNumberOfHostedSoldiers(), 6);
    }
}
