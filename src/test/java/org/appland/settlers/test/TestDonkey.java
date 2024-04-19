/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Donkey;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.WatchTower;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestDonkey {

    /*
    * TODO:
    *   - House is torn down just before donkey attempts to deliver - donkey drops cargo and goes back to the road and continues
    * */

    @Test
    public void testDonkeyIsDispatchedToMainRoad() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place flag */
        Point point2 = new Point(5, 9);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place flag */
        Point point3 = new Point(5, 13);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Place road between the headquarter and the first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Place road between the headquarter and the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Place workers on the roads */
        Courier courier0 = Utils.occupyRoad(road0, map);
        Courier courier1 = Utils.occupyRoad(road1, map);

        /* Deliver 99 cargo and verify that the road does not become a main road */
        for (int i = 0; i < 99; i++) {
            Cargo cargo = new Cargo(COIN, map);

            flag1.putCargo(cargo);

            cargo.setTarget(headquarter0);

            /* Wait for the courier to pick up the cargo */
            assertNull(courier1.getCargo());

            Utils.fastForwardUntilWorkerCarriesCargo(map, courier1, cargo);

            /* Wait for the courier to deliver the cargo */
            assertEquals(courier1.getTarget(), flag0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, courier1, flag0.getPosition());

            assertNull(courier1.getCargo());

            assertFalse(road1.isMainRoad());
        }

        /* Deliver one more cargo and verify that the road becomes a main road */
        Cargo cargo = new Cargo(COIN, map);

        flag1.putCargo(cargo);

        cargo.setTarget(headquarter0);

        /* Wait for the courier to pick up the cargo */
        assertNull(courier1.getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier1, cargo);

        /* Wait for the road to become a main road and verify that a donkey gets dispatched from the headquarter */
        assertEquals(courier1.getTarget(), flag0.getPosition());
        assertNull(road1.getDonkey());

        int amount = map.getWorkers().size();
        int donkeysInHq = headquarter0.getAmount(DONKEY);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier1, flag0.getPosition());

        assertTrue(road1.isMainRoad());

        map.stepTime();

        assertEquals(map.getWorkers().size(), amount + 1);
        assertEquals(headquarter0.getAmount(DONKEY), donkeysInHq - 1);
        assertNotNull(road1.getDonkey());
        assertFalse(road1.needsDonkey());
    }

    @Test
    public void testDonkeyWalksToIntendedRoad() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(8, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place flag */
        Point point2 = new Point(11, 3);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place flag */
        Point point3 = new Point(4, 4);
        Flag flag2 = map.placeFlag(player0, point3);

        /* Place roads */
        Point point4 = new Point(6, 4);
        Point point5 = new Point(9, 3);

        Road road0 = map.placeRoad(player0, point3, point4, point1);
        Road road1 = map.placeRoad(player0, point1, point5, point2);

        /* Assign a donkey to road1 */
        Donkey donkey = new Donkey(player0, map);
        map.placeWorker(donkey, flag2);

        donkey.assignToRoad(road1);

        /* Verify that the donkey walks to the road */
        Utils.fastForwardUntilWorkersReachTarget(map, donkey);

        assertEquals(donkey.getAssignedRoad(), road1);
        assertTrue(donkey.isIdle());
    }

    @Test
    public void testDonkeyIsNotASoldier() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(8, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place flag */
        Point point2 = new Point(11, 3);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place flag */
        Point point3 = new Point(4, 4);
        Flag flag2 = map.placeFlag(player0, point3);

        /* Place roads */
        Point point4 = new Point(6, 4);
        Point point5 = new Point(9, 3);

        Road road0 = map.placeRoad(player0, point3, point4, point1);
        Road road1 = map.placeRoad(player0, point1, point5, point2);

        /* Assign a donkey to road1 */
        Donkey donkey = new Donkey(player0, map);
        map.placeWorker(donkey, flag2);

        donkey.assignToRoad(road1);

        /* Verify that the donkey is not a soldier */
        assertFalse(donkey.isSoldier());
    }

    @Test
    public void testDonkeyGoesToMiddlePointOfRoad() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(player0, point1);

        /* Place flag */
        Point point2 = new Point(6, 4);
        Flag flag2 = map.placeFlag(player0, point2);

        /* Place road */
        Point point3 = new Point(8, 4);
        Road road0 = map.placeRoad(player0, point2, point3, point1);

        /* Place a donkey and assign it to the road */
        Donkey donkey = new Donkey(player0, map);
        map.placeWorker(donkey, flag2);

        donkey.assignToRoad(road0);

        /* Verify that the donkey goes to the middle point of the road */
        assertTrue(donkey.isWalkingToRoad());
        assertFalse(donkey.isIdle());

        Utils.fastForwardUntilWorkersReachTarget(map, donkey);

        assertEquals(donkey.getPosition(), point3);
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());
        assertFalse(donkey.isWalkingToIdlePoint());
    }

    @Test
    public void testDonkeyIsIdleWhenMiddlePointIsReached() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(player0, point1);

        /* Place flag */
        Point point2 = new Point(6, 4);
        Flag flag2 = map.placeFlag(player0, point2);

        /* Place road */
        Point point3 = new Point(8, 4);
        Road road0 = map.placeRoad(player0, point2, point3, point1);

        /* Assign a donkey to the road */
        Donkey donkey = new Donkey(player0, map);
        map.placeWorker(donkey, flag2);

        donkey.assignToRoad(road0);

        /* Wait for the donkey to walk to the road */
        assertTrue(donkey.isWalkingToRoad());
        assertFalse(donkey.isIdle());

        Utils.fastForwardUntilWorkersReachTarget(map, donkey);

        assertEquals(donkey.getPosition(), point3);
        assertTrue(donkey.isArrived());

        /* Verify that the donkey is idle after reaching the middle point */
        assertTrue(donkey.isIdle());
    }

    @Test
    public void testDonkeyRemainsIdleWhenThereIsNoCargo() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(player0, point1);

        /* Place flag */
        Point point2 = new Point(6, 4);
        Flag flag2 = map.placeFlag(player0, point2);

        /* Place road */
        Point point3 = new Point(8, 4);
        Road road0 = map.placeRoad(player0, point2, point3, point1);

        /* Place a donkey and assign it to the road */
        Donkey donkey = new Donkey(player0, map);
        map.placeWorker(donkey, flag2);

        donkey.assignToRoad(road0);

        /* Wait for the donkey to reach the road */
        Utils.fastForwardUntilWorkersReachTarget(map, donkey);

        assertEquals(donkey.getPosition(), point3);
        assertTrue(donkey.isArrived());

        /* Verify that the donkey stays idle when there is nothing to do */
        for (int i = 0; i < 200; i++) {

            assertTrue(donkey.isArrived());
            assertTrue(donkey.isIdle());
            assertEquals(donkey.getPosition(), point3);
            assertFalse(donkey.isWalkingToIdlePoint());

            map.stepTime();
        }
    }

    @Test
    public void testDonkeyWalksToMiddleOfRoadWhenItIsAssignedEvenIfFlagsHaveCargo() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(19, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(player0, point1);

        /* Place flag */
        Point point2 = new Point(6, 4);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place woodcutter */
        Point point4 = new Point(13, 5);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point4.upLeft());

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter);

        /* Place roads */
        Point middle = new Point(8, 4);
        Point point3 = new Point(11, 5);
        Road road0 = map.placeRoad(player0, point2, middle, point1);
        Road road1 = map.placeRoad(player0, point1, point3, point4);

        /* Place a cargo for the woodcutter */
        Cargo cargo = new Cargo(WOOD, map);
        flag0.putCargo(cargo);
        cargo.setTarget(woodcutter);

        /* Place a donkey and assign it to road0 */
        Donkey donkey = new Donkey(player0, map);
        map.placeWorker(donkey, flag0);

        donkey.assignToRoad(road0);

        /* Verify that the donkey walks to the middle of the road even when there are cargo available for pickup */
        assertTrue(donkey.isWalkingToRoad());
        assertFalse(donkey.isIdle());

        Utils.fastForwardUntilWorkersReachTarget(map, donkey);

        assertEquals(donkey.getPosition(), middle);
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());

        assertFalse(donkey.isWalkingToIdlePoint());
    }

    @Test
    public void testDonkeyPicksUpCargoFromFlag() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(19, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(player0, point1);

        /* Place flag */
        Point point2 = new Point(6, 4);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place woodcutter */
        Point point4 = new Point(13, 5);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point4.upLeft());

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter);

        /* Place roads */
        Point point3 = new Point(11, 5);
        Point middle = new Point(8, 4);
        Road road0 = map.placeRoad(player0, point2, middle, point1);
        Road road1 = map.placeRoad(player0, point1, point3, point4);

        /* Place cargo at flag0 */
        Cargo cargo = new Cargo(WOOD, map);
        flag0.putCargo(cargo);
        cargo.setTarget(woodcutter);

        /* Place donkey at same flag as cargo */
        Donkey donkey = new Donkey(player0, map);
        map.placeWorker(donkey, flag0);

        donkey.assignToRoad(road0);

        /* Donkey will walk to idle point at the road */
        assertTrue(donkey.isWalkingToRoad());

        Utils.fastForwardUntilWorkersReachTarget(map, donkey);

        assertEquals(donkey.getPosition(), middle);
        assertFalse(flag0.getStackedCargo().isEmpty());
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());

        /* Donkey detects the cargo */
        map.stepTime();

        assertEquals(donkey.getTarget(), flag0.getPosition());
        assertTrue(donkey.isTraveling());
        assertFalse(flag0.getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());

        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
        */
        assertEquals(donkey.getCargo(), cargo);
        assertEquals(donkey.getTarget(), flag1.getPosition());
        assertTrue(flag0.getStackedCargo().isEmpty());
    }

    @Test
    public void testDonkeyDeliversCargoAndBecomesIdle() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(19, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag1 = map.placeFlag(player0, point1);

        /* Place flag */
        Point point2 = new Point(6, 4);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place woodcutter */
        Point point4 = new Point(13, 5);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point4.upLeft());

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter);

        /* Place roads */
        Point point3 = new Point(12, 4);
        Point middle = new Point(8, 4);
        Road road0 = map.placeRoad(player0, point2, middle, point1);
        Road road1 = map.placeRoad(player0, point1, point3, point4);

        /* Place cargo at flag0 */
        Cargo cargo = new Cargo(WOOD, map);
        flag0.putCargo(cargo);
        cargo.setTarget(woodcutter);

        /* Place donkey at same flag as cargo */
        Donkey donkey = new Donkey(player0, map);
        map.placeWorker(donkey, flag0);

        donkey.assignToRoad(road0);

        /* Donkey will walk to idle point at the road */
        assertTrue(donkey.isWalkingToRoad());

        Utils.fastForwardUntilWorkersReachTarget(map, donkey);

        assertEquals(donkey.getPosition(), middle);
        assertFalse(flag0.getStackedCargo().isEmpty());
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());

        /* Donkey detects the cargo */
        map.stepTime();

        assertEquals(donkey.getTarget(), flag0.getPosition());
        assertTrue(donkey.isTraveling());
        assertFalse(flag0.getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());

        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(donkey.getCargo(), cargo);
        assertEquals(donkey.getTarget(), flag1.getPosition());
        assertTrue(flag0.getStackedCargo().isEmpty());
        assertFalse(donkey.isIdle());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());

        assertNull(donkey.getCargo());
        assertFalse(flag1.getStackedCargo().isEmpty());
        assertFalse(donkey.isIdle());
        assertEquals(flag1.getStackedCargo().getFirst(), cargo);

        /* After delivering the cargo, the donkey goes back to the idle spot */
        assertFalse(donkey.isIdle());
        assertNotEquals(donkey.getTarget(), donkey.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());

        assertTrue(donkey.isIdle());
    }

    @Test
    public void testDonkeyPicksUpNewCargoAtSameFlagAfterDelivery() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(19, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point middleFlagPoint = new Point(10, 4);
        Flag middleFlag = map.placeFlag(player0, middleFlagPoint);

        /* Place left woodcutter */
        Point leftFlagPoint = new Point(6, 4);
        Building leftWoodcutter = map.placeBuilding(new Woodcutter(player0), leftFlagPoint.upLeft());

        /* Place right woodcutter */
        Point rightFlagPoint = new Point(13, 5);
        Building rightWoodcutter = map.placeBuilding(new Woodcutter(player0), rightFlagPoint.upLeft());

        /* Finish construction of the right woodcutter */
        Utils.constructHouse(rightWoodcutter);

        /* Place roads */
        Point point3 = new Point(12, 4);
        Point middlePoint = new Point(8, 4);
        Road road0 = map.placeRoad(player0, leftFlagPoint, middlePoint, middleFlagPoint);
        Road road1 = map.placeRoad(player0, middleFlagPoint, point3, rightFlagPoint);

        /* Place cargo at flag0 */
        Cargo cargoForRightWoodcutter = new Cargo(WOOD, map);
        leftWoodcutter.getFlag().putCargo(cargoForRightWoodcutter);
        cargoForRightWoodcutter.setTarget(rightWoodcutter);

        /* Place donkey at same flag as cargo */
        Donkey donkey = new Donkey(player0, map);
        map.placeWorker(donkey, leftWoodcutter.getFlag());

        donkey.assignToRoad(road0);

        /* Donkey will walk to idle point at the road */
        assertTrue(donkey.isWalkingToRoad());

        Utils.fastForwardUntilWorkersReachTarget(map, donkey);

        assertEquals(donkey.getPosition(), middlePoint);
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());

        /* Donkey detects the cargo */
        map.stepTime();

        assertEquals(donkey.getTarget(), leftWoodcutter.getFlag().getPosition());
        assertTrue(donkey.isTraveling());
        assertFalse(leftWoodcutter.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());

        assertTrue(donkey.isAt(leftWoodcutter.getFlag().getPosition()));

        /* Place cargo at other flag for donkey to discover after delivery */
        Cargo cargoForLeftWoodcutter = new Cargo(STONE, map);

        cargoForLeftWoodcutter.setPosition(middleFlagPoint);
        middleFlag.putCargo(cargoForLeftWoodcutter);
        cargoForLeftWoodcutter.setTarget(leftWoodcutter);

        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(donkey.getCargo(), cargoForRightWoodcutter);
        assertEquals(donkey.getTarget(), middleFlagPoint);
        assertTrue(leftWoodcutter.getFlag().getStackedCargo().isEmpty());
        assertFalse(donkey.isIdle());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, middleFlagPoint);

        assertEquals(middleFlag.getStackedCargo().getFirst(), cargoForRightWoodcutter);
        assertEquals(donkey.getCargo(), cargoForLeftWoodcutter);
        assertFalse(donkey.isIdle());

        /* After delivering the cargo, the donkey picks up the other cargo without going back to the middle */
        assertEquals(donkey.getTarget(), leftWoodcutter.getPosition());
    }

    @Test
    public void testDonkeyPicksUpNewCargoAtOtherFlagAfterDelivery() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(19, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point flagPoint = new Point(10, 4);
        Flag middleFlag = map.placeFlag(player0, flagPoint);

        /* Place left woodcutter */
        Point leftFlag = new Point(6, 4);
        Building leftWoodcutter = map.placeBuilding(new Woodcutter(player0), leftFlag.upLeft());

        /* Place right woodcutter */
        Point point4 = new Point(13, 5);
        Building rightWoodcutter = map.placeBuilding(new Woodcutter(player0), point4.upLeft());

        /* Finish construction of the right woodcutter */
        Utils.constructHouse(rightWoodcutter);

        /* Place roads */
        Point point3 = new Point(12, 4);
        Point middlePoint = new Point(8, 4);
        Road road0 = map.placeRoad(player0, leftFlag, middlePoint, flagPoint);
        Road road1 = map.placeRoad(player0, flagPoint, point3, point4);

        /* Place cargo at flag0 */
        Cargo cargoForRightWoodcutter = new Cargo(WOOD, map);
        leftWoodcutter.getFlag().putCargo(cargoForRightWoodcutter);
        cargoForRightWoodcutter.setTarget(rightWoodcutter);

        /* Place donkey at same flag as cargo */
        Donkey donkey = new Donkey(player0, map);
        map.placeWorker(donkey, leftWoodcutter.getFlag());

        donkey.assignToRoad(road0);

        /* Donkey will walk to idle point at the road */
        assertTrue(donkey.isWalkingToRoad());

        Utils.fastForwardUntilWorkersReachTarget(map, donkey);

        assertEquals(donkey.getPosition(), middlePoint);
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());

        /* Donkey detects the cargo */
        map.stepTime();

        assertEquals(donkey.getTarget(), leftWoodcutter.getFlag().getPosition());
        assertTrue(donkey.isTraveling());
        assertFalse(leftWoodcutter.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());

        assertTrue(donkey.isAt(leftWoodcutter.getFlag().getPosition()));

        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(donkey.getCargo(), cargoForRightWoodcutter);
        assertEquals(donkey.getTarget(), middleFlag.getPosition());
        assertTrue(leftWoodcutter.getFlag().getStackedCargo().isEmpty());
        assertFalse(donkey.isIdle());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, middleFlag.getPosition());

        assertEquals(middleFlag.getStackedCargo().getFirst(), cargoForRightWoodcutter);
        assertNull(donkey.getCargo());
        assertFalse(donkey.isIdle());
        assertEquals(donkey.getTarget(), middlePoint);

        Utils.fastForwardUntilWorkersReachTarget(map, donkey);

        assertTrue(donkey.isIdle());
        assertEquals(donkey.getPosition(), middlePoint);
    }

    @Test
    public void testDonkeyDeliversToBuilding() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point rightFlagPoint = new Point(10, 4);
        Flag rightFlag = map.placeFlag(player0, rightFlagPoint);

        /* Place woodcutter */
        Point leftFlagPoint = new Point(6, 4);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), leftFlagPoint.upLeft());

        /* Place road */
        Point middlePoint = new Point(8, 4);
        Road road0 = map.placeRoad(player0, leftFlagPoint, middlePoint, rightFlagPoint);

        /* Place cargo at flag0 */
        Cargo cargoForRightWoodcutter = new Cargo(PLANK, map);
        rightFlag.putCargo(cargoForRightWoodcutter);
        cargoForRightWoodcutter.setTarget(woodcutter);

        /* Place donkey at same flag as cargo */
        Donkey donkey = new Donkey(player0, map);
        map.placeWorker(donkey, woodcutter.getFlag());

        donkey.assignToRoad(road0);

        /* Donkey will walk to idle point at the road */
        assertTrue(donkey.isWalkingToRoad());

        Utils.fastForwardUntilWorkersReachTarget(map, donkey);

        assertEquals(donkey.getPosition(), middlePoint);
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());

        /* Donkey detects the cargo */
        map.stepTime();

        assertEquals(donkey.getTarget(), rightFlagPoint);
        assertTrue(donkey.isTraveling());
        assertFalse(rightFlag.getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());

        assertTrue(donkey.isAt(rightFlagPoint));

        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(donkey.getCargo(), cargoForRightWoodcutter);
        assertEquals(donkey.getTarget(), woodcutter.getPosition());
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());
        assertFalse(donkey.isIdle());
        assertEquals(woodcutter.getAmount(PLANK), 0);

        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, leftFlagPoint);

        /* Verify that donkey does not deliver the cargo to the flag */
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());
        assertNotNull(donkey.getCargo());
        assertEquals(donkey.getTarget(), woodcutter.getPosition());
        assertEquals(woodcutter.getAmount(PLANK), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, woodcutter.getPosition());

        assertNull(donkey.getCargo());
        assertFalse(donkey.isIdle());
        assertEquals(donkey.getPosition(), woodcutter.getPosition());
        assertEquals(woodcutter.getAmount(PLANK), 1);
    }

    @Test
    public void testDonkeyGoesBackToIdlePointAfterDeliveryToBuilding() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point rightFlagPoint = new Point(10, 4);
        Flag rightFlag = map.placeFlag(player0, rightFlagPoint);

        /* Place woodcutter */
        Point leftFlagPoint = new Point(6, 4);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), leftFlagPoint.upLeft());

        /* Place road */
        Point middlePoint = new Point(8, 4);
        Road road0 = map.placeRoad(player0, leftFlagPoint, middlePoint, rightFlagPoint);

        /* Place cargo at flag0 */
        Cargo cargoForRightWoodcutter = new Cargo(PLANK, map);
        rightFlag.putCargo(cargoForRightWoodcutter);
        cargoForRightWoodcutter.setTarget(woodcutter);

        /* Place donkey at same flag as cargo */
        Donkey donkey = new Donkey(player0, map);
        map.placeWorker(donkey, woodcutter.getFlag());

        donkey.assignToRoad(road0);

        /* Donkey will walk to idle point at the road */
        assertTrue(donkey.isWalkingToRoad());

        Utils.fastForwardUntilWorkersReachTarget(map, donkey);

        assertEquals(donkey.getPosition(), middlePoint);
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());

        /* Donkey detects the cargo */
        map.stepTime();

        assertEquals(donkey.getTarget(), rightFlagPoint);
        assertTrue(donkey.isTraveling());
        assertFalse(rightFlag.getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());

        assertTrue(donkey.isAt(rightFlagPoint));

        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(donkey.getCargo(), cargoForRightWoodcutter);
        assertEquals(donkey.getTarget(), woodcutter.getPosition());
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());
        assertFalse(donkey.isIdle());
        assertEquals(woodcutter.getAmount(PLANK), 0);

        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, leftFlagPoint);

        /* Verify that donkey does not deliver the cargo to the flag */
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());
        assertNotNull(donkey.getCargo());
        assertEquals(donkey.getTarget(), woodcutter.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, woodcutter.getPosition());

        assertFalse(donkey.isIdle());
        assertEquals(donkey.getPosition(), woodcutter.getPosition());
        assertEquals(donkey.getTarget(), woodcutter.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, woodcutter.getFlag().getPosition());

        assertEquals(donkey.getPosition(), woodcutter.getFlag().getPosition());
        assertFalse(donkey.isIdle());

        Utils.fastForwardUntilWorkersReachTarget(map, donkey);

        assertEquals(donkey.getPosition(), middlePoint);
        assertTrue(donkey.isIdle());
    }

    @Test
    public void testDonkeyDeliversToBuildingWhenItIsAlreadyAtFlagAndPicksUpCargo() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(19, 5);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point middleFlagPoint = new Point(10, 4);
        Flag middleFlag = map.placeFlag(player0, middleFlagPoint);

        /* Place flag */
        Point rightFlagPoint = new Point(14, 4);
        Flag rightFlag = map.placeFlag(player0, rightFlagPoint);

        /* Place woodcutter */
        Point wcFlagPoint = new Point(6, 4);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), wcFlagPoint.upLeft());

        /* Place roads */
        Point middlePoint = new Point(8, 4);
        Road road0 = map.placeRoad(player0, wcFlagPoint, middlePoint, middleFlagPoint);
        Road road1 = map.placeRoad(player0, middleFlagPoint, middleFlagPoint.right(), rightFlagPoint);

        Building quarry = map.placeBuilding(new Quarry(player0), rightFlagPoint.upLeft());

        /* Place cargo at the woodcutter's flag */
        Cargo cargoForQuarry = new Cargo(PLANK, map);
        woodcutter.getFlag().putCargo(cargoForQuarry);
        cargoForQuarry.setTarget(quarry);

        /* Place donkey at middle flag */
        Donkey donkey = new Donkey(player0, map);
        map.placeWorker(donkey, middleFlag);

        donkey.assignToRoad(road0);

        /* Donkey will walk to idle point at the road */
        assertTrue(donkey.isWalkingToRoad());
        assertEquals(donkey.getTarget(), middlePoint);

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, middlePoint);

        assertEquals(donkey.getPosition(), middlePoint);
        assertTrue(donkey.isArrived());
        assertTrue(donkey.isIdle());

        /* Donkey detects the cargo at the woodcutter */
        map.stepTime();

        assertEquals(donkey.getTarget(), woodcutter.getFlag().getPosition());
        assertTrue(donkey.isTraveling());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, donkey.getTarget());

        assertTrue(donkey.isAt(woodcutter.getFlag().getPosition()));

        /* When worker arrives at the flag it automatically picks up the cargo
            - It picks up the cargo directly and sets the other flag as target
            - It walks to the other flag and delivers the cargo
        */
        assertEquals(donkey.getCargo(), cargoForQuarry);
        assertEquals(donkey.getTarget(), middleFlagPoint);
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());
        assertFalse(donkey.isIdle());

        /* Put the other cargo at the middle flag with the woodcutter as its target */
        Cargo cargoForWoodcutter = new Cargo(PLANK, map);
        middleFlag.putCargo(cargoForWoodcutter);
        cargoForWoodcutter.setTarget(woodcutter);

        /* Let the donkey reach the middle flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, middleFlagPoint);

        /* Verify that donkey puts down the cargo and picks up the new cargo */
        assertFalse(middleFlag.getStackedCargo().isEmpty());
        assertEquals(donkey.getCargo(), cargoForWoodcutter);
        assertEquals(donkey.getTarget(), woodcutter.getPosition());
        assertEquals(woodcutter.getAmount(PLANK), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, woodcutter.getPosition());

        assertNull(donkey.getCargo());
        assertFalse(donkey.isIdle());
        assertEquals(donkey.getPosition(), woodcutter.getPosition());
        assertEquals(woodcutter.getAmount(PLANK), 1);

    }

    @Test
    public void testDonkeysStopCarryingThingsAtSplittingRoads() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place forester */
        Point point22 = new Point(22, 6);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point22);

        /* Place woodcutter */
        Point point23 = new Point(19, 7);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point23);

        /* Place quarry */
        Point point24 = new Point(10, 12);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point24);

        /* Place road between the forester and the woodcutter */
        Point point25 = new Point(23, 5);
        Point point26 = new Point(21, 5);
        Point point27 = new Point(20, 6);
        Road road0 = map.placeRoad(player0, point25, point26, point27);

        /* Place road between the woodcutter and the quarry */
        Point point50 = new Point(19, 5);
        Point point28 = new Point(18, 4);
        Point point29 = new Point(17, 5);
        Point point30 = new Point(16, 6);
        Point point31 = new Point(15, 7);
        Point point32 = new Point(14, 8);
        Point point33 = new Point(13, 9);
        Point point34 = new Point(12, 10);
        Point point35 = new Point(11, 11);
        Road road1 = map.placeRoad(player0, point27, point50, point28, point29, point30, point31, point32, point33, point34, point35);

        /* Place road between the headquarter and the quarry */
        Point point36 = new Point(10, 10);
        Point point37 = new Point(11, 9);
        Point point38 = new Point(10, 8);
        Point point39 = new Point(9, 7);
        Point point40 = new Point(10, 6);
        Point point41 = new Point(9, 5);
        Point point42 = new Point(8, 4);
        Road road2 = map.placeRoad(player0, point35, point36, point37, point38, point39, point40, point41, point42);

        /* Place flag */
        Flag flag0 = map.placeFlag(player0, point40);

        /* Place flag */
        Flag flag1 = map.placeFlag(player0, point38);

        /* Place flag */
        Flag flag2 = map.placeFlag(player0, point33);

        /* Place flag */
        Flag flag3 = map.placeFlag(player0, point31);

        /* Place flag */
        Flag flag4 = map.placeFlag(player0, point29);

        /* Wait for all donkeys to become idle */
        for (int i = 0; i < 2000; i++) {
            boolean allIdle = true;

            for (Worker worker : map.getWorkers()) {
                if (worker instanceof Donkey && worker.isTraveling()) {
                    allIdle = false;
                }
            }

            if (allIdle) {
                break;
            }

            map.stepTime();
        }

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Donkey donkey) {

                assertFalse(donkey.isTraveling());
                assertFalse(donkey.isWalkingToRoad());
            }
        }
    }

    @Test
    public void testBothDonkeyAndCourierCanBeAssignedToSameRoad() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road between flags */
        Point point2 = new Point(8, 4);
        Road road0 = map.placeRoad(player0, headquarter0.getFlag().getPosition(), point2, point1);

        /* Verify that both a courier and a donkey can be assigned to the road */
        Courier courier0 = new Courier(player0, map);
        Donkey donkey0 = new Donkey(player0, map);

        map.placeWorker(courier0, flag0);
        map.placeWorker(donkey0, flag0);

        courier0.assignToRoad(road0);
        donkey0.assignToRoad(road0);

        assertEquals(road0.getCourier(), courier0);
        assertEquals(road0.getDonkey(), donkey0);
    }

    @Test
    public void testCannotAssignTwoDonkeysToSameRoad() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road between flags */
        Point point2 = new Point(8, 4);
        Road road0 = map.placeRoad(player0, headquarter0.getFlag().getPosition(), point2, point1);

        /* Verify that two donkeys can't be assigned to the road */
        Donkey donkey0 = new Donkey(player0, map);
        Donkey donkey1 = new Donkey(player0, map);

        map.placeWorker(donkey0, flag0);
        map.placeWorker(donkey1, flag0);

        donkey0.assignToRoad(road0);

        try {
            donkey1.assignToRoad(road0);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testDonkeyIsNotDispatchedToDriveway() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place flag */
        Point point2 = new Point(5, 9);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place road between the headquarter and the flag */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Place courier on the road */
        Courier courier0 = Utils.occupyRoad(road0, map);

        /* Make sure there are donkeys in the headquarter */
        Utils.adjustInventoryTo(headquarter0, DONKEY, 10);

        /* Turn the roads into main roads */
        Road headquarterDriveway = map.getRoad(headquarter0.getPosition(), headquarter0.getFlag().getPosition());

        for (int i = 0; i < 500; i++) {
            Cargo cargo = new Cargo(COIN, map);

            flag0.putCargo(cargo);

            cargo.setTarget(headquarter0);

            /* Wait for the courier to pick up the cargo */
            assertNull(courier0.getCargo());

            Utils.fastForwardUntilWorkerCarriesCargo(map, courier0, cargo);

            /* Wait for the courier to deliver the cargo */
            assertEquals(courier0.getTarget(), headquarter0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, courier0, headquarter0.getPosition());

            assertNull(courier0.getCargo());

            if (road0.isMainRoad() && headquarterDriveway.isMainRoad()) {
                break;
            }
        }

        assertTrue(road0.isMainRoad());
        assertTrue(headquarterDriveway.isMainRoad());

        /* Verify that the driveway does not get assigned a donkey */
        assertFalse(headquarterDriveway.needsDonkey());
        assertNull(headquarterDriveway.getDonkey());

        Utils.fastForward(500, map);

        assertNull(headquarterDriveway.getDonkey());
    }

    @Test
    public void testDonkeyIsNotDispatchedToOpponentsRoadWithoutConnectedStorage() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point38 = new Point(21, 13);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Remove all donkeys from the inventory */
        Utils.adjustInventoryTo(headquarter0, DONKEY, 0);

        /* Extend the border */
        Point point0 = new Point(11, 15);
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point0);

        /* Construct the fortress & occupy it */
        Utils.constructHouse(fortress0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place a guardhouse */
        Point point1 = new Point(7, 13);
        WatchTower watchTower0 = map.placeBuilding(new WatchTower(player0), point1);

        /* Construct the guardhouse & occupy it*/
        Utils.constructHouse(watchTower0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, watchTower0);

        /* Place flag */
        Point point2 = new Point(4, 4);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place flag */
        Point point3 = new Point(8, 4);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Place road between the headquarter and the first flag of the road */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Place road between the headquarter and the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Place workers on the roads */
        Courier courier0 = Utils.occupyRoad(road0, map);
        Courier courier1 = Utils.occupyRoad(road1, map);

        /* Deliver 100 cargo to make the road a main road */
        for (int i = 0; i < 100; i++) {
            Cargo cargo = new Cargo(COIN, map);

            flag1.putCargo(cargo);

            cargo.setTarget(headquarter0);

            /* Wait for the courier to pick up the cargo */
            assertNull(courier1.getCargo());

            Utils.fastForwardUntilWorkerCarriesCargo(map, courier1, cargo);

            /* Wait for the courier to deliver the cargo */
            assertEquals(courier1.getTarget(), flag0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, courier1, flag0.getPosition());

            assertNull(courier1.getCargo());
        }

        assertTrue(road1.isMainRoad());

        /* Remove the connecting road so that road 1 is not connected to the headquarter */
        map.removeRoad(road0);

        assertFalse(map.arePointsConnectedByRoads(road1.getStart(), headquarter0.getPosition()));

        /* Place an opponent */
        Point point4 = new Point(40, 40);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point4);

        /* Put donkeys in the opponent's inventory */
        Utils.adjustInventoryTo(headquarter1, DONKEY, 10);

        /* Verify that the opponent's headquarter doesn't try to deliver donkeys */
        for (int i = 0; i < 500; i++) {

            assertEquals(headquarter1.getAmount(DONKEY), 10);

            map.stepTime();
        }
    }

    @Test
    public void testDonkeyDoesntDoSpecialActionsWhenIdle() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place flag */
        Point point2 = new Point(5, 9);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place flag */
        Point point3 = new Point(5, 13);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Place road between the headquarter and the first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Place road between the headquarter and the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Place workers on the roads */
        Courier courier0 = Utils.occupyRoad(road0, map);
        Courier courier1 = Utils.occupyRoad(road1, map);

        /* Deliver 99 cargo and verify that the road does not become a main road */
        for (int i = 0; i < 99; i++) {
            Cargo cargo = new Cargo(COIN, map);

            flag1.putCargo(cargo);

            cargo.setTarget(headquarter0);

            /* Wait for the courier to pick up the cargo */
            assertNull(courier1.getCargo());

            Utils.fastForwardUntilWorkerCarriesCargo(map, courier1, cargo);

            /* Wait for the courier to deliver the cargo */
            assertEquals(courier1.getTarget(), flag0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, courier1, flag0.getPosition());

            assertNull(courier1.getCargo());

            assertFalse(road1.isMainRoad());
        }

        /* Deliver one more cargo and verify that the road becomes a main road */
        Cargo cargo = new Cargo(COIN, map);

        flag1.putCargo(cargo);

        cargo.setTarget(headquarter0);

        /* Wait for the courier to pick up the cargo */
        assertNull(courier1.getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier1, cargo);

        /* Wait for the road to become a main road and verify that a donkey gets dispatched from the headquarter */
        assertEquals(courier1.getTarget(), flag0.getPosition());
        assertNull(road1.getDonkey());

        int amount = map.getWorkers().size();
        int donkeysInHq = headquarter0.getAmount(DONKEY);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier1, flag0.getPosition());

        assertTrue(road1.isMainRoad());

        map.stepTime();

        assertEquals(map.getWorkers().size(), amount + 1);
        assertEquals(headquarter0.getAmount(DONKEY), donkeysInHq - 1);
        assertNotNull(road1.getDonkey());
        assertFalse(road1.needsDonkey());

        /* Wait for the donkey to become idle */
        Donkey donkey = Utils.waitForWorkerOutsideBuilding(Donkey.class, player0);

        Utils.waitForCourierToBeIdle(donkey, map);

        /* Verify that the donkey doesn't do any special actions when it's idle (unlike couriers) */
        for (int i = 0; i < 5000; i++) {

            assertFalse(donkey.isJumpingSkipRope());
            assertFalse(donkey.isChewingGum());
            assertFalse(donkey.isReadingPaper());
            assertFalse(donkey.isTouchingNose());
            assertFalse(donkey.isSittingDown());

            map.stepTime();
        }
    }
}
