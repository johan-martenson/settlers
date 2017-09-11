/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Donkey;
import org.appland.settlers.model.DonkeyBreeder;
import org.appland.settlers.model.DonkeyFarm;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.DONKEY_BREEDER;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Worker;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestDonkeyFarm {

    @Test
    public void testDonkeyFarmOnlyNeedsThreePlancksAndThreeStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing donkey farm */
        Point point22 = new Point(6, 22);
        Building farm0 = map.placeBuilding(new DonkeyFarm(player0), point22);

        /* Deliver three planck and three stone */
        Cargo planckCargo = new Cargo(PLANCK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        farm0.putCargo(planckCargo);
        farm0.putCargo(planckCargo);
        farm0.putCargo(planckCargo);
        farm0.putCargo(stoneCargo);
        farm0.putCargo(stoneCargo);
        farm0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the donkey farm */
        for (int i = 0; i < 200; i++) {
            assertTrue(farm0.underConstruction());

            map.stepTime();
        }

        assertTrue(farm0.ready());
    }

    @Test
    public void testDonkeyFarmCannotBeConstructedWithTooFewPlancks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing donkey farm */
        Point point22 = new Point(6, 22);
        Building farm0 = map.placeBuilding(new DonkeyFarm(player0), point22);

        /* Deliver two planck and three stone */
        Cargo planckCargo = new Cargo(PLANCK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        farm0.putCargo(planckCargo);
        farm0.putCargo(planckCargo);
        farm0.putCargo(stoneCargo);
        farm0.putCargo(stoneCargo);
        farm0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the donkey farm */
        for (int i = 0; i < 500; i++) {
            assertTrue(farm0.underConstruction());

            map.stepTime();
        }

        assertFalse(farm0.ready());
    }

    @Test
    public void testDonkeyFarmCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing donkey farm */
        Point point22 = new Point(6, 22);
        Building farm0 = map.placeBuilding(new DonkeyFarm(player0), point22);

        /* Deliver three plancks and two stones */
        Cargo planckCargo = new Cargo(PLANCK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        farm0.putCargo(planckCargo);
        farm0.putCargo(planckCargo);
        farm0.putCargo(planckCargo);
        farm0.putCargo(stoneCargo);
        farm0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the donkey farm */
        for (int i = 0; i < 500; i++) {
            assertTrue(farm0.underConstruction());

            map.stepTime();
        }

        assertFalse(farm0.ready());
    }

    @Test
    public void testUnfinishedDonkeyFarmNeedsNoDonkeyBreeder() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place donkey farm */
        Point point0 = new Point(10, 6);
        Building farm = map.placeBuilding(new DonkeyFarm(player0), point0);

        assertTrue(farm.underConstruction());
        assertFalse(farm.needsWorker());
    }

    @Test
    public void testFinishedDonkeyFarmNeedsDonkeyBreeder() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place donkey farm */
        Point point0 = new Point(10, 6);
        Building farm = map.placeBuilding(new DonkeyFarm(player0), point0);

        Utils.constructHouse(farm, map);

        assertTrue(farm.ready());
        assertTrue(farm.needsWorker());
    }

    @Test
    public void testDonkeyBreederIsAssignedToFinishedDonkeyFarm() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new DonkeyFarm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish the donkey farm */
        Utils.constructHouse(farm, map);

        /* Fast forward so the headquarter dispatches a courier and a donkey breeder */
        Utils.fastForward(20, map);

        /* Verify that there was a donkey breeder added */
        Utils.verifyListContainsWorkerOfType(map.getWorkers(), DonkeyBreeder.class);
    }

    @Test
    public void testDonkeyBreederRestsInDonkeyFarmThenLeaves() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(donkeyFarm, map);

        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm.putCargo(waterCargo);
        donkeyFarm.putCargo(wheatCargo);

        /* Run the game logic 99 times and make sure the donkey breeder stays in the donkey farm */
        int i;
        for (i = 0; i < 99; i++) {
            assertTrue(donkeyBreeder.isInsideBuilding());
            map.stepTime();
        }

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Step once and make sure the donkey breedre goes out of the donkey farm */
        map.stepTime();        

        assertFalse(donkeyBreeder.isInsideBuilding());
    }

    @Test
    public void testDonkeyBreederFeedsTheDonkeysWhenItHasResources() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(donkeyFarm, map);

        /* Deliver wheat and donkey to the farm */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);

        donkeyFarm.putCargo(wheatCargo);
        donkeyFarm.putCargo(waterCargo);

        /* Occupy the donkey farm with a donkey breeder */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Let the donkey breeder rest */
        Utils.fastForward(99, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Step once and make sure the donkey breeder goes out of the farm */
        map.stepTime();        

        assertFalse(donkeyBreeder.isInsideBuilding());

        Point point = donkeyBreeder.getTarget();

        assertTrue(donkeyBreeder.isTraveling());

        /* Let the donkey breeder reach the spot and start to feed the donkeys */
        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);

        assertTrue(donkeyBreeder.isArrived());
        assertTrue(donkeyBreeder.isAt(point));
        assertTrue(donkeyBreeder.isFeeding());

        int i;
        for (i = 0; i < 19; i++) {
            assertTrue(donkeyBreeder.isFeeding());
            map.stepTime();
        }

        assertTrue(donkeyBreeder.isFeeding());
        assertFalse(map.isCropAtPoint(point));

        map.stepTime();

        /* Verify that the donkey breeder stopped feeding */
        assertFalse(donkeyBreeder.isFeeding());
        assertNull(donkeyBreeder.getCargo());
    }

    @Test
    public void testDonkeyBreederReturnsAfterFeeding() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(donkeyFarm, map);

        /* Assign a donkey breeder to the farm */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm.putCargo(waterCargo);
        donkeyFarm.putCargo(wheatCargo);

        /* Wait for the donkey breeder to rest */
        Utils.fastForward(99, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Step once to let the donkey breeder go out to plant */
        map.stepTime();        

        assertFalse(donkeyBreeder.isInsideBuilding());

        Point point = donkeyBreeder.getTarget();

        assertTrue(donkeyBreeder.isTraveling());

        /* Let the donkey breeder reach the intended spot and start to feed */
        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);

        assertTrue(donkeyBreeder.isArrived());
        assertTrue(donkeyBreeder.isAt(point));
        assertTrue(donkeyBreeder.isFeeding());

        /* Wait for the donkey breeder to feed */
        Utils.fastForward(19, map);

        assertTrue(donkeyBreeder.isFeeding());

        map.stepTime();

        /* Verify that the donkey breeder stopped feeding and is walking back to the farm */
        assertFalse(donkeyBreeder.isFeeding());
        assertTrue(donkeyBreeder.isTraveling());
        assertEquals(donkeyBreeder.getTarget(), donkeyFarm.getPosition());
        assertTrue(donkeyBreeder.getPlannedPath().contains(donkeyFarm.getFlag().getPosition()));

        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);

        assertTrue(donkeyBreeder.isArrived());        
        assertTrue(donkeyBreeder.isInsideBuilding());
    }

    @Test
    public void testDonkeyWalksToStorageByItself() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(donkeyFarm, map);

        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm.putCargo(waterCargo);
        donkeyFarm.putCargo(wheatCargo);

        /* Assign a donkey breeder to the farm */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Let the donkey breeder rest */
        Utils.fastForward(99, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Step once and to let the donkey breeder go out to feed */
        map.stepTime();        

        assertFalse(donkeyBreeder.isInsideBuilding());

        Point point = donkeyBreeder.getTarget();

        assertTrue(donkeyBreeder.isTraveling());

        /* Let the donkey breeder reach the intended spot */
        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);

        assertTrue(donkeyBreeder.isArrived());
        assertTrue(donkeyBreeder.isAt(point));
        assertTrue(donkeyBreeder.isFeeding());

        /* Wait for the donkey breeder to feed the donkeys */
        Utils.fastForward(19, map);

        assertTrue(donkeyBreeder.isFeeding());

        map.stepTime();

        /* DonkeyBreeder is walking back to farm without carrying a cargo */
        assertFalse(donkeyBreeder.isFeeding());
        assertEquals(donkeyBreeder.getTarget(), donkeyFarm.getPosition());
        assertNull(donkeyBreeder.getCargo());

        /* Let the donkey breeder reach the farm */
        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);

        assertTrue(donkeyBreeder.isArrived());
        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Wait for the donkey breeder to prepare the donkey */
        int amount = map.getWorkers().size();

        Utils.fastForward(20, map);

        /* Verify that the donkey walks to the storage by itself and the donkey 
           breeder stays in the farm */
        map.stepTime();

        assertTrue(donkeyBreeder.isInsideBuilding());
        assertEquals(map.getWorkers().size(), amount + 1);
        assertNull(donkeyBreeder.getCargo());

        Donkey donkey = null;
        for (Worker w : map.getWorkers()) {
            if (w instanceof Donkey && w.getTarget().equals(hq.getPosition())) {
                donkey = (Donkey)w;

                break;
            }
        }

        assertNotNull(donkey);
        assertEquals(donkey.getTarget(), hq.getPosition());

        /* Verify that the donkey walks to the headquarter */
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, hq.getPosition());

        assertEquals(donkey.getPosition(), hq.getPosition());
    }

    @Test
    public void testDonkeyFarmWithoutDonkeyBreederProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(donkeyFarm0, map);

        /* Verify that the farm does not produce any donkeys */
        boolean newDonkeyFound = false;

        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    newDonkeyFound = true;

                    break;
                }
            }

            if (newDonkeyFound) {
                break;
            }

            map.stepTime();
        }

        assertFalse(newDonkeyFound);
    }

    @Test
    public void testDonkeyFarmWithoutConnectedStorageDoesNotProduce() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing donkey farm */
        Point point26 = new Point(8, 8);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Occupy the donkey farm */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0, map);

        /* Deliver material to the donkey farm */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);

        donkeyFarm0.putCargo(wheatCargo);
        donkeyFarm0.putCargo(wheatCargo);

        donkeyFarm0.putCargo(waterCargo);
        donkeyFarm0.putCargo(waterCargo);

        /* Let the donkey breeder rest */
        Utils.fastForward(100, map);

        /* Wait for the donkey breeder to produce a new donkey */
        boolean newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    newDonkeyFound = true;

                    break;
                }

            }

            if (newDonkeyFound) {
                break;
            }

            map.stepTime();
        }

        assertFalse(newDonkeyFound);
    }

    @Test
    public void testDonkeyBreederGoesBackToStorageWhenDonkeyFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing donkey farm */
        Point point26 = new Point(8, 8);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Occupy the donkey farm */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0, map);

        /* Destroy the donkey farm */
        Worker ww = donkeyFarm0.getWorker();

        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), donkeyFarm0.getPosition());

        donkeyFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(DONKEY_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());

        /* Verify that the donkey breeder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(DONKEY_BREEDER), amount + 1);
    }

    @Test
    public void testDonkeyBreederGoesBackOnToStorageOnRoadsIfPossibleWhenDonkeyFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing donkey farm */
        Point point26 = new Point(8, 8);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        /* Connect the donkey farm with the headquarter */
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Occupy the donkey farm */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0, map);

        /* Destroy the donkey farm */
        Worker ww = donkeyFarm0.getWorker();

        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), donkeyFarm0.getPosition());

        donkeyFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point p : ww.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test
    public void testDonkeyBreederWithoutResourcesProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        Utils.constructHouse(donkeyFarm, map);

        /* Occupy the donkey farm with a donkey breeder */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Let the donkey breeder rest */
        Utils.fastForward(99, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Verify that the donkey breeder doesn't produce anything */
        for (int i = 0; i < 300; i++) {
            assertEquals(donkeyBreeder.getCargo(), null);

            map.stepTime();
        }
    }

    @Test
    public void testDonkeyBreederWithoutResourcesStaysInHouse() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        Utils.constructHouse(donkeyFarm, map);

        /* Occupy the donkey farm with a donkey breeder */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Let the donkey breeder rest */
        Utils.fastForward(99, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Verify that the donkey breeder doesn't produce anything */
        for (int i = 0; i < 300; i++) {
            assertTrue(donkeyBreeder.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testDonkeyBreederFeedsDonkeysWithWaterAndWheat() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        Utils.constructHouse(donkeyFarm, map);

        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm.putCargo(waterCargo);
        donkeyFarm.putCargo(wheatCargo);

        /* Assign a donkey breeder to the farm */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Let the donkey breeder rest */
        Utils.fastForward(99, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Step once and to let the donkey breeder go out to feed */
        map.stepTime();        

        assertFalse(donkeyBreeder.isInsideBuilding());

        Point point = donkeyBreeder.getTarget();

        assertTrue(donkeyBreeder.isTraveling());

        /* Let the donkey breeder reach the intended spot */
        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);

        assertTrue(donkeyBreeder.isArrived());
        assertTrue(donkeyBreeder.isAt(point));
        assertTrue(donkeyBreeder.isFeeding());

        /* Wait for the donkey breeder to feed the donkeys */
        Utils.fastForward(19, map);

        assertTrue(donkeyBreeder.isFeeding());

        map.stepTime();

        /* Verify that the donkey breeder is done feeding and has consumed the water and wheat */
        assertFalse(donkeyBreeder.isFeeding());
        assertEquals(donkeyFarm.getAmount(WATER), 0);
        assertEquals(donkeyFarm.getAmount(WHEAT), 0);
    }

    @Test
    public void testDestroyedDonkeyFarmIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing donkey farm */
        Point point26 = new Point(8, 8);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        /* Connect the donkey farm with the headquarter */
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Destroy the donkey farm */
        donkeyFarm0.tearDown();

        assertTrue(donkeyFarm0.burningDown());

        /* Wait for the donkey farm to stop burning */
        Utils.fastForward(50, map);

        assertTrue(donkeyFarm0.destroyed());

        /* Wait for the donkey farm to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), donkeyFarm0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(donkeyFarm0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing donkey farm */
        Point point26 = new Point(8, 8);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(donkeyFarm0.getPosition(), donkeyFarm0.getFlag().getPosition()));

        map.removeFlag(donkeyFarm0.getFlag());

        assertNull(map.getRoad(donkeyFarm0.getPosition(), donkeyFarm0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing donkey farm */
        Point point26 = new Point(8, 8);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(donkeyFarm0.getPosition(), donkeyFarm0.getFlag().getPosition()));

        donkeyFarm0.tearDown();

        assertNull(map.getRoad(donkeyFarm0.getPosition(), donkeyFarm0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInDonkeyFarmCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point1 = new Point(8, 6);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Connect the donkey farm and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Assign a worker to the donkey farm */
        DonkeyBreeder ww = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(ww, donkeyFarm0, map);

        assertTrue(ww.isInsideBuilding());

        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm0.putCargo(waterCargo);
        donkeyFarm0.putCargo(wheatCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);

        boolean newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    newDonkeyFound = true;

                    break;
                }

            }

            if (newDonkeyFound) {
                break;
            }

            map.stepTime();
        }

        assertTrue(newDonkeyFound);

        /* Wait for the new donkey to walk away from the donkey farm */
        Utils.fastForward(20, map);

        /* Stop production and verify that no donkey is produced */
        donkeyFarm0.stopProduction();

        assertFalse(donkeyFarm0.isProductionEnabled());

        newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    newDonkeyFound = true;

                    break;
                }

            }

            if (newDonkeyFound) {
                break;
            }

            map.stepTime();
        }

        assertFalse(newDonkeyFound);
    }

    @Test
    public void testProductionInDonkeyFarmCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point1 = new Point(8, 6);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Connect the donkey farm and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm0.putCargo(waterCargo);
        donkeyFarm0.putCargo(waterCargo);

        donkeyFarm0.putCargo(wheatCargo);
        donkeyFarm0.putCargo(wheatCargo);

        /* Assign a worker to the donkey farm */
        DonkeyBreeder ww = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(ww, donkeyFarm0, map);

        assertTrue(ww.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the donkey breeder to produce donkey */
        boolean newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    newDonkeyFound = true;

                    break;
                }
            }

            if (newDonkeyFound) {
                break;
            }

            map.stepTime();
        }

        assertTrue(newDonkeyFound);

        /* Wait for the new donkey to walk away from the donkey farm */
        Utils.fastForward(20, map);

        /* Stop production */
        donkeyFarm0.stopProduction();

        newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    newDonkeyFound = true;

                    break;
                }
            }

            if (newDonkeyFound) {
                break;
            }

            map.stepTime();
        }

        assertFalse(newDonkeyFound);

        /* Resume production and verify that the donkey farm produces donkey again */
        donkeyFarm0.resumeProduction();

        assertTrue(donkeyFarm0.isProductionEnabled());

        newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    newDonkeyFound = true;

                    break;
                }
            }

            if (newDonkeyFound) {
                break;
            }

            map.stepTime();
        }

        assertTrue(newDonkeyFound);
    }

    @Test
    public void testDonkeyBreederCarriesNoCargo() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(donkeyFarm, map);

        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm.putCargo(waterCargo);
        donkeyFarm.putCargo(wheatCargo);

        /* Assign a donkey breeder to the farm */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Verify that the donkey breeder does not pick up any cargo */
        for (int i = 0; i < 500; i++) {
            assertNull(donkeyBreeder.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testDonkeyWalksToStorageOnExistingRoads() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(donkeyFarm0, map);

        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm0.putCargo(waterCargo);
        donkeyFarm0.putCargo(wheatCargo);

        /* Assign a donkey breeder to the farm */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm0, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Wait for the donkey farm to create a donkey */
        Donkey donkey = null;

        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    donkey = (Donkey)w;

                    break;
                }

            }

            if (donkey != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(donkey);

        /* Verify that the donkey walks to the storage on existing roads */
        Point previous = null;

        for (Point p : donkey.getPlannedPath()) {
            if (previous == null) {
                previous = p;

                continue;
            }

            if (!map.isFlagAtPoint(p)) {
                continue;
            }

            assertNotNull(map.getRoad(previous, p));

            previous = p;
        }
    }

    @Test
    public void testProducedDonkeyIsOnlyAddedOnce() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        Utils.constructHouse(donkeyFarm0, map);

        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm0.putCargo(waterCargo);
        donkeyFarm0.putCargo(wheatCargo);

        /* Assign a donkey breeder to the farm */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm0, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Wait for the donkey farm to create a donkey */
        Donkey donkey = null;

        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    donkey = (Donkey)w;

                    break;
                }

            }

            if (donkey != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(donkey);

        /* Verify that the donkey is only added once */
        for (int i = 0; i < 600; i++) {
            assertEquals(map.getWorkers().indexOf(donkey), map.getWorkers().lastIndexOf(donkey));

            map.stepTime();
        }

    }

    @Test
    public void testAssignedDonkeyBreederHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place donkey farm*/
        Point point1 = new Point(20, 14);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Connect the donkey farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), donkeyFarm0.getFlag());

        /* Wait for donkey breeder to get assigned and leave the headquarter */
        List<DonkeyBreeder> workers = Utils.waitForWorkersOutsideBuilding(DonkeyBreeder.class, 1, player0, map);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        DonkeyBreeder worker = workers.get(0);

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);
        Player player2 = new Player("Player 2", RED);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 2's headquarter */
        Building headquarter2 = new Headquarter(player2);
        Point point10 = new Point(70, 70);
        map.placeBuilding(headquarter2, point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = new Fortress(player0);
        map.placeBuilding(fortress0, point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0, map);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0, map);

        /* Place donkey farm close to the new border */
        Point point4 = new Point(28, 18);
        DonkeyFarm donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point4);

        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Occupy the donkey farm */
        DonkeyBreeder worker = Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0, map);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down*/
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testDonkeyBreederReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing donkey farm */
        Point point2 = new Point(14, 4);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, donkeyFarm0.getFlag());

        /* Wait for the donkey breeder to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(DonkeyBreeder.class, 1, player0, map);

        DonkeyBreeder donkeyBreeder = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof DonkeyBreeder) {
                donkeyBreeder = (DonkeyBreeder) w;
            }
        }

        assertNotNull(donkeyBreeder);
        assertEquals(donkeyBreeder.getTarget(), donkeyFarm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the donkey breeder has started walking */
        assertFalse(donkeyBreeder.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the donkey breeder continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, flag0.getPosition());

        assertEquals(donkeyBreeder.getPosition(), flag0.getPosition());

        /* Verify that the donkey breeder returns to the headquarter when it reaches the flag */
        assertEquals(donkeyBreeder.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, headquarter0.getPosition());
    }

    @Test
    public void testDonkeyBreederContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing donkey farm */
        Point point2 = new Point(14, 4);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, donkeyFarm0.getFlag());

        /* Wait for the donkey breeder to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(DonkeyBreeder.class, 1, player0, map);

        DonkeyBreeder donkeyBreeder = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof DonkeyBreeder) {
                donkeyBreeder = (DonkeyBreeder) w;
            }
        }

        assertNotNull(donkeyBreeder);
        assertEquals(donkeyBreeder.getTarget(), donkeyFarm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the donkey breeder has started walking */
        assertFalse(donkeyBreeder.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the donkey breeder continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, flag0.getPosition());

        assertEquals(donkeyBreeder.getPosition(), flag0.getPosition());

        /* Verify that the donkey breeder continues to the final flag */
        assertEquals(donkeyBreeder.getTarget(), donkeyFarm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, donkeyFarm0.getFlag().getPosition());

        /* Verify that the donkey breeder goes out to the donkey farm instead of going directly back */
        assertNotEquals(donkeyBreeder.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testDonkeyBreederReturnsToStorageIfDonkeyFarmIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing donkey farm */
        Point point2 = new Point(14, 4);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, donkeyFarm0.getFlag());

        /* Wait for the donkey breeder to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(DonkeyBreeder.class, 1, player0, map);

        DonkeyBreeder donkeyBreeder = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof DonkeyBreeder) {
                donkeyBreeder = (DonkeyBreeder) w;
            }
        }

        assertNotNull(donkeyBreeder);
        assertEquals(donkeyBreeder.getTarget(), donkeyFarm0.getPosition());

        /* Wait for the donkey breeder to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, flag0.getPosition());

        map.stepTime();

        /* See that the donkey breeder has started walking */
        assertFalse(donkeyBreeder.isExactlyAtPoint());

        /* Tear down the donkey farm */
        donkeyFarm0.tearDown();

        /* Verify that the donkey breeder continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, donkeyFarm0.getFlag().getPosition());

        assertEquals(donkeyBreeder.getPosition(), donkeyFarm0.getFlag().getPosition());

        /* Verify that the donkey breeder goes back to storage */
        assertEquals(donkeyBreeder.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testDonkeyBreederGoesOffroadBackToClosestStorageWhenDonkeyFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing donkey farm */
        Point point26 = new Point(17, 17);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Occupy the donkey farm */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0, map);

        /* Place a second storage closer to the donkey farm */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the donkey farm */
        Worker donkeyBreeder = donkeyFarm0.getWorker();

        assertTrue(donkeyBreeder.isInsideBuilding());
        assertEquals(donkeyBreeder.getPosition(), donkeyFarm0.getPosition());

        donkeyFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(donkeyBreeder.isInsideBuilding());
        assertEquals(donkeyBreeder.getTarget(), storage0.getPosition());

        int amount = storage0.getAmount(DONKEY_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, storage0.getPosition());

        /* Verify that the donkey breeder is stored correctly in the headquarter */
        assertEquals(storage0.getAmount(DONKEY_BREEDER), amount + 1);
    }

    @Test
    public void testDonkeyBreederReturnsOffroadAndAvoidsBurningStorageWhenDonkeyFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing donkey farm */
        Point point26 = new Point(17, 17);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Occupy the donkey farm */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0, map);

        /* Place a second storage closer to the donkey farm */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

        /* Destroy the donkey farm */
        Worker donkeyBreeder = donkeyFarm0.getWorker();

        assertTrue(donkeyBreeder.isInsideBuilding());
        assertEquals(donkeyBreeder.getPosition(), donkeyFarm0.getPosition());

        donkeyFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(donkeyBreeder.isInsideBuilding());
        assertEquals(donkeyBreeder.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(DONKEY_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, headquarter0.getPosition());

        /* Verify that the donkey breeder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(DONKEY_BREEDER), amount + 1);
    }

    @Test
    public void testDonkeyBreederReturnsOffroadAndAvoidsDestroyedStorageWhenDonkeyFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing donkey farm */
        Point point26 = new Point(17, 17);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Occupy the donkey farm */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0, map);

        /* Place a second storage closer to the donkey farm */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storage0, map);

        /* Destroy the donkey farm */
        Worker donkeyBreeder = donkeyFarm0.getWorker();

        assertTrue(donkeyBreeder.isInsideBuilding());
        assertEquals(donkeyBreeder.getPosition(), donkeyFarm0.getPosition());

        donkeyFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(donkeyBreeder.isInsideBuilding());
        assertEquals(donkeyBreeder.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(DONKEY_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, headquarter0.getPosition());

        /* Verify that the donkey breeder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(DONKEY_BREEDER), amount + 1);
    }

    @Test
    public void testDonkeyBreederReturnsOffroadAndAvoidsUnfinishedStorageWhenDonkeyFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing donkey farm */
        Point point26 = new Point(17, 17);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Occupy the donkey farm */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0, map);

        /* Place a second storage closer to the donkey farm */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Destroy the donkey farm */
        Worker donkeyBreeder = donkeyFarm0.getWorker();

        assertTrue(donkeyBreeder.isInsideBuilding());
        assertEquals(donkeyBreeder.getPosition(), donkeyFarm0.getPosition());

        donkeyFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(donkeyBreeder.isInsideBuilding());
        assertEquals(donkeyBreeder.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(DONKEY_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, headquarter0.getPosition());

        /* Verify that the donkey breeder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(DONKEY_BREEDER), amount + 1);
    }
}
