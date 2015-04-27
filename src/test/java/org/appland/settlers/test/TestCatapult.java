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
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Catapult;
import org.appland.settlers.model.CatapultWorker;
import org.appland.settlers.model.Projectile;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestCatapult {

    @Test
    public void testCatapultOnlyNeedsFourPlancksAndThreeStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing catapult */
        Point point22 = new Point(6, 22);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point22);
        
        /* Deliver four plancks and three stones */
        Cargo planckCargo = new Cargo(PLANCK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        catapult0.putCargo(planckCargo);
        catapult0.putCargo(planckCargo);
        catapult0.putCargo(planckCargo);
        catapult0.putCargo(planckCargo);
        catapult0.putCargo(stoneCargo);
        catapult0.putCargo(stoneCargo);
        catapult0.putCargo(stoneCargo);
    
        /* Verify that this is enough to construct the catapult */
        for (int i = 0; i < 150; i++) {
            assertTrue(catapult0.underConstruction());
            
            map.stepTime();
        }

        assertTrue(catapult0.ready());
    }

    @Test
    public void testCatapultCannotBeConstructedWithTooFewPlancks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing catapult */
        Point point22 = new Point(6, 22);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point22);
        
        /* Deliver three planck and three stone */
        Cargo planckCargo = new Cargo(PLANCK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        catapult0.putCargo(planckCargo);
        catapult0.putCargo(planckCargo);
        catapult0.putCargo(planckCargo);
        catapult0.putCargo(stoneCargo);
        catapult0.putCargo(stoneCargo);
        catapult0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the catapult */
        for (int i = 0; i < 500; i++) {
            assertTrue(catapult0.underConstruction());

            map.stepTime();
        }

        assertFalse(catapult0.ready());
    }

    @Test
    public void testCatapultCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing catapult */
        Point point22 = new Point(6, 22);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point22);
        
        /* Deliver four plancks and two stone */
        Cargo planckCargo = new Cargo(PLANCK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        catapult0.putCargo(planckCargo);
        catapult0.putCargo(planckCargo);
        catapult0.putCargo(planckCargo);
        catapult0.putCargo(planckCargo);
        catapult0.putCargo(stoneCargo);
        catapult0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the catapult */
        for (int i = 0; i < 500; i++) {
            assertTrue(catapult0.underConstruction());

            map.stepTime();
        }

        assertFalse(catapult0.ready());
    }

    @Test (expected = Exception.class)
    public void testCatapultCannotAddTooManyStonesWhenUnderConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing catapult */
        Point point22 = new Point(6, 22);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point22);

        /* Deliver three stones */
        Cargo stoneCargo  = new Cargo(STONE, map);

        catapult0.promiseDelivery(STONE);
        catapult0.putCargo(stoneCargo);

        catapult0.promiseDelivery(STONE);
        catapult0.putCargo(stoneCargo);

        catapult0.promiseDelivery(STONE);
        catapult0.putCargo(stoneCargo);

        /* Verify that the catapult doesn't need more stones */
        assertFalse(catapult0.needsMaterial(STONE));

        /* Verify that delivering another stone throws an exception */
        catapult0.putCargo(stoneCargo);
    }

    @Test
    public void testCatapultNeedsWorker() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place catapult */
        Point point3 = new Point(7, 9);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Unfinished catapult doesn't need worker */
        assertFalse(catapult.needsWorker());

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult, map);

        assertTrue(catapult.needsWorker());
    }

    @Test
    public void testCatapultGetsAssignedWorker() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place catapult */
        Point point3 = new Point(7, 9);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Place a road between the headquarter and the catapult */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult, map);

        assertTrue(catapult.needsWorker());

        /* Verify that a catapult worker leaves the hq */        
        assertEquals(map.getWorkers().size(), 1);

        Utils.fastForward(3, map);

        assertEquals(map.getWorkers().size(), 3);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), CatapultWorker.class);

        /* Let the catapult worker reach the catapult */
        CatapultWorker worker = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof CatapultWorker) {
                worker = (CatapultWorker)w;
            }
        }

        assertNotNull(worker);
        assertEquals(worker.getTarget(), catapult.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, worker);

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getHome(), catapult);
        assertEquals(catapult.getWorker(), worker);
    }

    @Test
    public void testOccupiedCatapultNothing() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place catapult */
        Point point3 = new Point(7, 9);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult, map);

        /* Occupy the catapult */
        Worker worker = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult, map);

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getHome(), catapult);
        assertEquals(catapult.getWorker(), worker);        

        /* Deliver stones to the catapult */
        Cargo cargo = new Cargo(STONE, map);

        catapult.putCargo(cargo);
        catapult.putCargo(cargo);

        /* Verify that the catapult doesn't produce anything */
        int i;
        for (i = 0; i < 500; i++) {
            assertTrue(catapult.getFlag().getStackedCargo().isEmpty());
            assertNull(worker.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedCatapultProducesNothing() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place catapult */
        Point point3 = new Point(7, 9);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Utils.constructHouse(catapult, map);

        /* Occupy the catapult */
        Worker sw = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult, map);

        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), catapult);
        assertEquals(catapult.getWorker(), sw);

        /* Remove all the stones in the headquarter */
        Utils.adjustInventoryTo(headquarter0, STONE, 0, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks */
        Point point2 = new Point(29, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player1), point2);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Place catapult */
        Point point3 = new Point(21, 5);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult, map);

        /* Occupy the catapult */
        Worker sw = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult, map);

        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), catapult);
        assertEquals(catapult.getWorker(), sw);

        /* Remove all the stones in the headquarter */
        Utils.adjustInventoryTo(headquarter0, STONE, 0, map);

        /* Deliver stones to the catapult */
        catapult.putCargo(new Cargo(STONE, map));
        catapult.putCargo(new Cargo(STONE, map));

        /* Wait for the catapult to throw a projectile */
        assertTrue(map.getProjectiles().isEmpty());

        Utils.fastForward(50, map);

        /* Get the projectile */
        assertEquals(map.getProjectiles().size(), 1);

        Projectile projectile = map.getProjectiles().get(0);

        assertNotNull(projectile);

        /* Verify that the projectile is targeted at the barracks */
        assertEquals(projectile.getTarget(), barracks0.getPosition());

        /* Verify that the projectile comes from the catapult */
        assertEquals(projectile.getSource(), catapult.getPosition());

        /* Verify that the projectile starts at the source */
        assertEquals(projectile.getProgress(), 0);

        /* Verify that the projectile travels toward the barracks with the right speed */
        for (int i = 0; i < 40; i++) {

            assertTrue(projectile.getProgress() - (i * 2.5) < 2);
            assertTrue(projectile.getProgress() - (i * 2.5) > -2);

            map.stepTime();
        }
    }

    @Test
    public void testCatapultDoesNotFireOnNonMilitaryEnemyBuildings() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place woodcutter */
        Point point2 = new Point(29, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player1), point2);

        /* Finish construction of the woodcutter */
        Utils.constructHouse(woodcutter0, map);

        /* Place catapult */
        Point point3 = new Point(21, 5);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult, map);

        /* Occupy the catapult */
        Worker sw = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult, map);

        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), catapult);
        assertEquals(catapult.getWorker(), sw);

        /* Remove all the stones in the headquarter */
        Utils.adjustInventoryTo(headquarter0, STONE, 0, map);

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
    public void testBarracksLosesOneSoldierWhenProjectileHits() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks */
        Point point2 = new Point(29, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player1), point2);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks with one military */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0, map);

        assertEquals(barracks0.getHostedMilitary(), 1);

        /* Place catapult */
        Point point3 = new Point(17, 5);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult, map);

        /* Occupy the catapult */
        Worker sw = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult, map);

        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), catapult);
        assertEquals(catapult.getWorker(), sw);

        /* Remove all the stones in the headquarter */
        Utils.adjustInventoryTo(headquarter0, STONE, 0, map);

        /* Deliver stones to the catapult */
        catapult.putCargo(new Cargo(STONE, map));
        catapult.putCargo(new Cargo(STONE, map));

        /* Wait for the catapult to throw a projectile */
        assertTrue(map.getProjectiles().isEmpty());

        Utils.fastForward(50, map);

        /* Get the projectile */
        assertEquals(map.getProjectiles().size(), 1);

        Projectile projectile = map.getProjectiles().get(0);

        assertNotNull(projectile);

        /* Verify that the projectile is targeted at the barracks */
        assertEquals(projectile.getTarget(), barracks0.getPosition());

        /* Wait for the projectile to reach the barracks */
        for (int i = 0; i < 500; i++) {

            if (projectile.arrived()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(projectile.arrived());

        /* Verify that the barracks lost one military */
        assertEquals(barracks0.getHostedMilitary(), 0);
    }

    @Test
    public void testCatapultFiresAtRightPointInTime() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks */
        Point point2 = new Point(29, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player1), point2);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Place catapult */
        Point point3 = new Point(21, 5);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult, map);

        /* Occupy the catapult */
        Worker sw = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult, map);

        assertTrue(sw.isInsideBuilding());
        assertEquals(sw.getHome(), catapult);
        assertEquals(catapult.getWorker(), sw);

        /* Remove all the stones in the headquarter */
        Utils.adjustInventoryTo(headquarter0, STONE, 0, map);

        /* Deliver stones to the catapult */
        catapult.putCargo(new Cargo(STONE, map));
        catapult.putCargo(new Cargo(STONE, map));

        /* Verify that the catapult throws a projectile at the right time */
        for (int i = 0; i < 49; i++) {

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing catapult */
        Point point26 = new Point(8, 8);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point26);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0, map);

        /* Occupy the catapult */
        Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing catapult */
        Point point26 = new Point(8, 8);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point26);

        /* Connect the catapult with the headquarter */
        map.placeAutoSelectedRoad(player0, catapult0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0, map);

        /* Occupy the catapult */
        Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0, map);
        
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
        for (Point p : catapultWorker.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test
    public void testDestroyedCatapultIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing catapult */
        Point point26 = new Point(8, 8);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point26);

        /* Connect the catapult with the headquarter */
        map.placeAutoSelectedRoad(player0, catapult0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0, map);

        /* Destroy the catapult */
        catapult0.tearDown();

        assertTrue(catapult0.burningDown());

        /* Wait for the catapult to stop burning */
        Utils.fastForward(50, map);
        
        assertTrue(catapult0.destroyed());
        
        /* Wait for the catapult to disappear */
        for (int i = 0; i < 100; i++) {
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing catapult */
        Point point26 = new Point(8, 8);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point26);
        
        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0, map);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(catapult0.getPosition(), catapult0.getFlag().getPosition()));
        
        map.removeFlag(catapult0.getFlag());

        assertNull(map.getRoad(catapult0.getPosition(), catapult0.getFlag().getPosition()));
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing catapult */
        Point point26 = new Point(8, 8);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point26);
        
        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place catapult*/
        Point point1 = new Point(20, 14);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point1);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0, map);
        
        /* Connect the catapult with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), catapult0.getFlag());

        /* Wait for catapult worker to get assigned and leave the headquarter */
        List<CatapultWorker> workers = Utils.waitForWorkersOutsideBuilding(CatapultWorker.class, 1, player0, map);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        CatapultWorker worker = workers.get(0);

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
        Headquarter headquarter2 = new Headquarter(player2);
        Point point10 = new Point(70, 70);
        map.placeBuilding(headquarter2, point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
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

        /* Place catapult close to the new border */
        Point point4 = new Point(28, 18);
        Catapult catapult0 = map.placeBuilding(new Catapult(player0), point4);

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult0, map);

        /* Occupy the catapult */
        CatapultWorker worker = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0, map);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down*/
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }
}
