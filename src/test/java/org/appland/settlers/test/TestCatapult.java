/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Projectile;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.actors.CatapultWorker;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Catapult;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestCatapult {

    /*
    * TODO:
    *   - Catapult must not shoot at unoccupied military buildings
    * */

    @Test
    public void testCatapultOnlyNeedsFourPlanksAndTwoStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place catapult */
        Point point22 = new Point(6, 12);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point22);

        /* Deliver four planks and two stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        catapult0.putCargo(plankCargo);
        catapult0.putCargo(plankCargo);
        catapult0.putCargo(plankCargo);
        catapult0.putCargo(plankCargo);
        catapult0.putCargo(stoneCargo);
        catapult0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(catapult0);

        /* Verify that this is enough to construct the catapult */
        for (int i = 0; i < 150; i++) {
            assertTrue(catapult0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(catapult0.isReady());
    }

    @Test
    public void testCatapultCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place catapult */
        Point point22 = new Point(6, 12);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point22);

        /* Deliver three plank and three stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        catapult0.putCargo(plankCargo);
        catapult0.putCargo(plankCargo);
        catapult0.putCargo(plankCargo);
        catapult0.putCargo(stoneCargo);
        catapult0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(catapult0);

        /* Verify that this is not enough to construct the catapult */
        for (int i = 0; i < 500; i++) {
            assertTrue(catapult0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(catapult0.isReady());
    }

    @Test
    public void testCatapultCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place catapult */
        Point point22 = new Point(6, 12);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point22);

        /* Deliver four planks and one stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        catapult0.putCargo(plankCargo);
        catapult0.putCargo(plankCargo);
        catapult0.putCargo(plankCargo);
        catapult0.putCargo(plankCargo);
        catapult0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(catapult0);

        /* Verify that this is not enough to construct the catapult */
        for (int i = 0; i < 500; i++) {
            assertTrue(catapult0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(catapult0.isReady());
    }

    @Test
    public void testCatapultCannotAddTooManyStonesWhenUnderConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place catapult */
        Point point22 = new Point(6, 12);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point22);

        /* Deliver three stones */
        Cargo stoneCargo = new Cargo(STONE, map);

        catapult0.promiseDelivery(STONE);
        catapult0.putCargo(stoneCargo);

        catapult0.promiseDelivery(STONE);
        catapult0.putCargo(stoneCargo);

        /* Verify that the catapult doesn't need more stones */
        assertFalse(catapult0.needsMaterial(STONE));

        /* Verify that delivering another stone throws an exception */
        try {
            catapult0.putCargo(stoneCargo);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCatapultNeedsWorker() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place catapult */
        Point point3 = new Point(7, 9);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Unfinished catapult doesn't need worker */
        assertFalse(catapult.needsWorker());

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult);

        assertTrue(catapult.needsWorker());
    }

    @Test
    public void testCatapultGetsAssignedWorker() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place catapult */
        Point point3 = new Point(7, 9);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Place a road between the headquarter and the catapult */
        Road road0 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter.getFlag());

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult);

        assertTrue(catapult.needsWorker());

        /* Verify that a catapult worker leaves the headquarter */
        Worker catapultWorker = Utils.waitForWorkerOutsideBuilding(CatapultWorker.class, player0);

        assertTrue(map.getWorkers().contains(catapultWorker));

        /* Let the catapult worker reach the catapult */
        assertNotNull(catapultWorker);
        assertEquals(catapultWorker.getTarget(), catapult.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, catapultWorker);

        assertTrue(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker);
    }

    @Test
    public void testOccupiedCatapultProducesNothing() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place catapult */
        Point point3 = new Point(7, 9);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult);

        /* Occupy the catapult */
        Worker worker = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getHome(), catapult);
        assertEquals(catapult.getWorker(), worker);

        /* Deliver stones to the catapult */
        Cargo cargo = new Cargo(STONE, map);

        catapult.putCargo(cargo);
        catapult.putCargo(cargo);

        /* Verify that the catapult doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(catapult.getFlag().getStackedCargo().isEmpty());
            assertNull(worker.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedCatapultProducesNothing() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place catapult */
        Point point3 = new Point(7, 9);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult);

        /* Deliver stones to the catapult */
        Cargo cargo = new Cargo(STONE, map);

        catapult.putCargo(cargo);
        catapult.putCargo(cargo);

        /* Verify that the catapult doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(catapult.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedCatapultWithStonesDoesNotThrowWithoutTarget() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place catapult */
        Point point3 = new Point(7, 9);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult);

        /* Occupy the catapult */
        Worker catapultWorker0 = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        /* Remove all the stones in the headquarter */
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        /* Deliver stones to the catapult */
        catapult.putCargo(new Cargo(STONE, map));
        catapult.putCargo(new Cargo(STONE, map));

        /* Verify that the catapult doesn't throw projectiles */
        assertTrue(map.getProjectiles().isEmpty());

        for (int i = 0; i < 500; i++) {

            map.stepTime();

            assertTrue(catapult.getFlag().getStackedCargo().isEmpty());
            assertEquals(catapult.getAmount(STONE), 2);
            assertEquals(map.getProjectiles().size(), 0);
        }
    }

    @Test
    public void testPathOfProjectile() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks */
        Point point2 = new Point(29, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player1), point2);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Place catapult */
        Point point3 = new Point(21, 5);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult);

        /* Occupy the catapult */
        Worker catapultWorker0 = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        /* Remove all the stones in the headquarter */
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        /* Deliver stones to the catapult */
        catapult.putCargo(new Cargo(STONE, map));
        catapult.putCargo(new Cargo(STONE, map));

        /* Wait for the catapult to throw a projectile */
        assertTrue(map.getProjectiles().isEmpty());

        Utils.fastForward(100, map);

        /* Get the projectile */
        assertEquals(map.getProjectiles().size(), 1);

        Projectile projectile = map.getProjectiles().getFirst();

        assertNotNull(projectile);

        /* Verify that the projectile is targeted at the barracks */
        assertEquals(projectile.getTarget(), barracks0.getPosition());

        /* Verify that the projectile comes from the catapult */
        assertEquals(projectile.getSource(), catapult);

        /* Verify that the projectile starts at the source */
        assertEquals(projectile.getProgress(), 0);

        /* Verify that the projectile travels toward the barracks with the right speed */
        for (int i = 0; i < 40; i++) {

            assertTrue(projectile.getProgress() - (i * 2.5) < 2);
            assertTrue(projectile.getProgress() - (i * 2.5) > -2);

            map.stepTime();
        }

        assertTrue(projectile.isArrived());
        assertTrue(map.getProjectiles().isEmpty());

    }

    @Test
    public void testCatapultDoesNotFireOnNonMilitaryEnemyBuildings() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place woodcutter */
        Point point2 = new Point(29, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player1), point2);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0);

        /* Place catapult */
        Point point3 = new Point(21, 5);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult);

        /* Occupy the catapult */
        Worker catapultWorker0 = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        /* Remove all the stones in the headquarter */
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        /* Deliver stones to the catapult */
        catapult.putCargo(new Cargo(STONE, map));
        catapult.putCargo(new Cargo(STONE, map));

        /* Verify that the catapult doesn't throw a projectile */
        for (int i = 0; i < 500; i++) {

            assertTrue(map.getProjectiles().isEmpty());
            assertEquals(catapult.getAmount(STONE), 2);

            map.stepTime();
        }
    }

    @Test
    public void testCatapultHitRateBetweenSeventyAndEightyPercent() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(13, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks */
        Point point2 = new Point(33, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player1), point2);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(barracks0);

        /* Place catapult */
        Point point3 = new Point(27, 15);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult);

        /* Occupy the catapult */
        Worker catapultWorker0 = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        /* Remove all the stones in the headquarter */
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        /* Verify that the catapult has a hit rate higher than seventy percent */
        int hits = 0;

        for (int i = 0; i < 100; i++) {

            /* Occupy the barracks if needed */
            if (barracks0.getNumberOfHostedSoldiers() == 0) {
                Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0);

                assertFalse(player1.getLandInPoints().contains(point3));
            }

            /* Deliver stone to the catapult */
            catapult.putCargo(new Cargo(STONE, map));

            /* Wait for the catapult to throw a projectile */
            assertTrue(catapult.isReady());

            Projectile projectile = Utils.waitForCatapultToThrowProjectile(catapult);

            int hostedBefore = barracks0.getNumberOfHostedSoldiers();

            /* Wait for the projectile to reach its target */
            Utils.waitForProjectileToReachTarget(projectile, map);

            /* Check if the projectile hit */
            if (barracks0.getNumberOfHostedSoldiers() < hostedBefore) {
                hits++;
            }
        }

        assertTrue(hits > 60);
        assertTrue(hits < 90);
    }

    @Test
    public void testCatapultDestroysEmptyBarracks() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks */
        Point point2 = new Point(33, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player1), point2);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(barracks0);

        /* Place catapult */
        Point point3 = new Point(21, 5);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult);

        /* Occupy the catapult */
        Worker catapultWorker0 = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        /* Remove all the stones in the headquarter */
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        /* Verify that the catapult destroys the barracks */
        for (int i = 0; i < 100; i++) {

            /* Deliver stone to the catapult */
            catapult.putCargo(new Cargo(STONE, map));

            /* Wait for the catapult to throw a projectile */
            Projectile projectile = Utils.waitForCatapultToThrowProjectile(catapult);

            /* Wait for the projectile to reach its target */
            Utils.waitForProjectileToReachTarget(projectile, map);

            /* Check if the projectile hit and destroyed the barracks */
            if (barracks0.isBurningDown()) {
                break;
            }
        }

        assertTrue(barracks0.isBurningDown());
    }

    @Test
    public void testCatapultWaitsBeforeThrowingAfterReceivingStone() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks */
        Point point2 = new Point(33, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player1), point2);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(barracks0);

        /* Place catapult */
        Point point3 = new Point(21, 5);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult);

        /* Occupy the catapult */
        Worker catapultWorker0 = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        /* Remove all the stones in the headquarter */
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        /* Make the catapult worker wait */
        Utils.fastForward(300, map);

        /* Deliver stone to the catapult */
        catapult.putCargo(new Cargo(STONE, map));

        /* Verify that the catapult worker waits before throwing a projectile */
        for (int i = 0; i < 100; i++) {

            /* Verify that the catapult hasn't thrown yet */
            assertTrue(map.getProjectiles().isEmpty());

            map.stepTime();
        }

        assertFalse(map.getProjectiles().isEmpty());
    }

    @Test
    public void testCatapultFiresAtRightPointInTime() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks */
        Point point2 = new Point(29, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player1), point2);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0);

        /* Place catapult */
        Point point3 = new Point(21, 5);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult);

        /* Occupy the catapult */
        Worker catapultWorker0 = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        /* Remove all the stones in the headquarter */
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        /* Deliver stones to the catapult */
        catapult.putCargo(new Cargo(STONE, map));
        catapult.putCargo(new Cargo(STONE, map));

        /* Verify that the catapult throws a projectile at the right time */
        for (int i = 0; i < 99; i++) {

            map.stepTime();

            assertTrue(map.getProjectiles().isEmpty());
            assertEquals(catapult.getAmount(STONE), 2);
        }

        map.stepTime();

        assertEquals(map.getProjectiles().size(), 1);
        assertEquals(catapult.getAmount(STONE), 1);
    }

    @Test
    public void testCatapultWorkerGoesBackToStorageWhenCatapultIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place catapult */
        Point point26 = new Point(8, 8);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point26);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0);

        /* Occupy the catapult */
        Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0);

        /* Destroy the catapult */
        Worker catapultWorker = catapult0.getWorker();

        assertTrue(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getPosition(), catapult0.getPosition());

        catapult0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, catapultWorker, headquarter0.getPosition());

        /* Verify that the worker isn't left on the map */
        assertFalse(map.getWorkers().contains(catapultWorker));
    }

    @Test
    public void testCatapultWorkerGoesBackOnToStorageOnRoadsIfPossibleWhenCatapultIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place catapult */
        Point point26 = new Point(8, 8);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point26);

        /* Connect the catapult with the headquarter */
        map.placeAutoSelectedRoad(player0, catapult0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0);

        /* Occupy the catapult */
        Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0);

        /* Destroy the catapult */
        Worker catapultWorker = catapult0.getWorker();

        assertTrue(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getPosition(), catapult0.getPosition());

        catapult0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point point : catapultWorker.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testDestroyedCatapultIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place catapult */
        Point point26 = new Point(8, 8);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point26);

        /* Connect the catapult with the headquarter */
        map.placeAutoSelectedRoad(player0, catapult0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0);

        /* Destroy the catapult */
        catapult0.tearDown();

        assertTrue(catapult0.isBurningDown());

        /* Wait for the catapult to stop burning */
        Utils.fastForward(100, map);

        assertTrue(catapult0.isDestroyed());

        /* Wait for the catapult to disappear */
        for (int i = 0; i < 50; i++) {
            assertEquals(map.getBuildingAtPoint(point26), catapult0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(catapult0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place catapult */
        Point point26 = new Point(8, 8);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point26);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(catapult0.getPosition(), catapult0.getFlag().getPosition()));

        map.removeFlag(catapult0.getFlag());

        assertNull(map.getRoad(catapult0.getPosition(), catapult0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place catapult */
        Point point26 = new Point(8, 8);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point26);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(catapult0.getPosition(), catapult0.getFlag().getPosition()));

        catapult0.tearDown();

        assertNull(map.getRoad(catapult0.getPosition(), catapult0.getFlag().getPosition()));
    }

    /*  -  Can catapults be stopped?        -  */
    /*  -  Test projectile's path           -  */
    /*  -  Test projectile kills military   -  */
    /*  -  Test projectiles miss sometimes  -  */

    @Test
    public void testAssignedCatapultWorkerHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place catapult */
        Point point1 = new Point(20, 14);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point1);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0);

        /* Connect the catapult with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), catapult0.getFlag());

        /* Wait for catapult worker to get assigned and leave the headquarter */
        List<CatapultWorker> workers = Utils.waitForWorkersOutsideBuilding(CatapultWorker.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        CatapultWorker worker = workers.getFirst();

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.GREEN);
        Player player2 = new Player("Player 2", PlayerColor.RED);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 2's headquarter */
        Point point10 = new Point(70, 70);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 9);
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place catapult close to the new border */
        Point point4 = new Point(28, 18);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point4);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0);

        /* Occupy the catapult */
        CatapultWorker worker = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testCatapultWorkerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place catapult */
        Point point2 = new Point(14, 4);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, catapult0.getFlag());

        /* Wait for the catapult worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(CatapultWorker.class, 1, player0);

        CatapultWorker catapultWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof CatapultWorker) {
                catapultWorker = (CatapultWorker) worker;
            }
        }

        assertNotNull(catapultWorker);
        assertEquals(catapultWorker.getTarget(), catapult0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, catapultWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the catapult worker has started walking */
        assertFalse(catapultWorker.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the catapult worker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, catapultWorker, flag0.getPosition());

        assertEquals(catapultWorker.getPosition(), flag0.getPosition());

        /* Verify that the catapult worker returns to the headquarter when it reaches the flag */
        assertEquals(catapultWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, catapultWorker, headquarter0.getPosition());
    }

    @Test
    public void testCatapultWorkerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place catapult */
        Point point2 = new Point(14, 4);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, catapult0.getFlag());

        /* Wait for the catapult worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(CatapultWorker.class, 1, player0);

        CatapultWorker catapultWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof CatapultWorker) {
                catapultWorker = (CatapultWorker) worker;
            }
        }

        assertNotNull(catapultWorker);
        assertEquals(catapultWorker.getTarget(), catapult0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, catapultWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the catapult worker has started walking */
        assertFalse(catapultWorker.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the catapult worker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, catapultWorker, flag0.getPosition());

        assertEquals(catapultWorker.getPosition(), flag0.getPosition());

        /* Verify that the catapult worker continues to the final flag */
        assertEquals(catapultWorker.getTarget(), catapult0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, catapultWorker, catapult0.getFlag().getPosition());

        /* Verify that the catapult worker goes out to catapult worker instead of going directly back */
        assertNotEquals(catapultWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testCatapultWorkerReturnsToStorageIfCatapultIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place catapult */
        Point point2 = new Point(14, 4);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, catapult0.getFlag());

        /* Wait for the catapult to get constructed */
        Utils.waitForBuildingToBeConstructed(catapult0);

        /* Wait for the catapult worker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(CatapultWorker.class, 1, player0);

        CatapultWorker catapultWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof CatapultWorker) {
                catapultWorker = (CatapultWorker) worker;
            }
        }

        assertNotNull(catapultWorker);
        assertEquals(catapultWorker.getTarget(), catapult0.getPosition());

        /* Wait for the catapult worker to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, catapultWorker, flag0.getPosition());

        map.stepTime();

        /* See that the catapult worker has started walking */
        assertFalse(catapultWorker.isExactlyAtPoint());

        /* Tear down the catapult */
        catapult0.tearDown();

        /* Verify that the catapult worker continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, catapultWorker, catapult0.getFlag().getPosition());

        assertEquals(catapultWorker.getPosition(), catapult0.getFlag().getPosition());

        /* Verify that the catapult worker goes back to storage */
        assertEquals(catapultWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testCatapultWorkerGoesOffroadBackToClosestStorageWhenCatapultIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place catapult */
        Point point26 = new Point(17, 17);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point26);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0);

        /* Occupy the catapult */
        Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0);

        /* Place a second storage closer to the catapult */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the catapult */
        Worker catapultWorker = catapult0.getWorker();

        assertTrue(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getPosition(), catapult0.getPosition());

        catapult0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(CATAPULT_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, catapultWorker, storehouse0.getPosition());
    }

    @Test
    public void testCatapultWorkerReturnsOffroadAndAvoidsBurningStorageWhenCatapultIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place catapult */
        Point point26 = new Point(17, 17);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point26);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0);

        /* Occupy the catapult */
        Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0);

        /* Place a second storage closer to the catapult */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Destroy the catapult */
        Worker catapultWorker = catapult0.getWorker();

        assertTrue(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getPosition(), catapult0.getPosition());

        catapult0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(CATAPULT_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, catapultWorker, headquarter0.getPosition());
    }

    @Test
    public void testCatapultWorkerReturnsOffroadAndAvoidsDestroyedStorageWhenCatapultIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place catapult */
        Point point26 = new Point(17, 17);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point26);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0);

        /* Occupy the catapult */
        Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0);

        /* Place a second storage closer to the catapult */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

        /* Destroy the catapult */
        Worker catapultWorker = catapult0.getWorker();

        assertTrue(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getPosition(), catapult0.getPosition());

        catapult0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(CATAPULT_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, catapultWorker, headquarter0.getPosition());
    }

    @Test
    public void testCatapultWorkerReturnsOffroadAndAvoidsUnfinishedStorageWhenCatapultIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place catapult */
        Point point26 = new Point(17, 17);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point26);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0);

        /* Occupy the catapult */
        Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0);

        /* Place a second storage closer to the catapult */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the catapult */
        Worker catapultWorker = catapult0.getWorker();

        assertTrue(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getPosition(), catapult0.getPosition());

        catapult0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(CATAPULT_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, catapultWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place catapult */
        Point point26 = new Point(17, 17);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point26);

        /* Place road to connect the headquarter and the catapult */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), catapult0.getFlag());

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(CatapultWorker.class, 1, player0).getFirst();

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, catapult0.getFlag().getPosition());

        /* Tear down the building */
        catapult0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(worker.getTarget(), catapult0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, catapult0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testCatapultReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place catapult */
        Point point1 = new Point(6, 12);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point1);

        /* Construct the catapult */
        Utils.constructHouse(catapult0);

        /* Verify that the reported output is correct */
        assertEquals(catapult0.getProducedMaterial().length, 0);
    }

    @Test
    public void testCatapultReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place catapult */
        Point point1 = new Point(6, 12);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(catapult0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(catapult0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(catapult0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(catapult0.getCanHoldAmount(PLANK), 4);
        assertEquals(catapult0.getCanHoldAmount(STONE), 2);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(catapult0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testCatapultReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place catapult */
        Point point1 = new Point(6, 12);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point1);

        /* Construct the catapult */
        Utils.constructHouse(catapult0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(catapult0.getTypesOfMaterialNeeded().size(), 1);
        assertTrue(catapult0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(catapult0.getCanHoldAmount(STONE), 4);

        for (Material material : Material.values()) {
            if (material == STONE) {
                continue;
            }

            assertEquals(catapult0.getCanHoldAmount(material), 0);
        }
    }
}
