package org.appland.settlers.test;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Hunter;
import org.appland.settlers.model.HunterHut;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidStateForProduction;
import static org.appland.settlers.model.Material.HUNTER;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.WildAnimal;
import org.appland.settlers.model.Worker;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TestHunterHut {

    @Test
    public void testHunterHutOnlyNeedsTwoPlancksForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing hunter hut */
        Point point22 = new Point(6, 22);
        Building hunterHut0 = map.placeBuilding(new HunterHut(player0), point22);
        
        /* Deliver two plancks */
        Cargo cargo = new Cargo(PLANCK, map);

        hunterHut0.putCargo(cargo);
        hunterHut0.putCargo(cargo);
    
        /* Verify that this is enough to construct the hunter hut */
        for (int i = 0; i < 100; i++) {
            assertTrue(hunterHut0.underConstruction());
            
            map.stepTime();
        }

        assertTrue(hunterHut0.ready());
    }

    @Test
    public void testHunterHutCannotBeConstructedWithOnePlanck() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing hunter hut */
        Point point22 = new Point(6, 22);
        Building hunterHut0 = map.placeBuilding(new HunterHut(player0), point22);
        
        /* Deliver two plancks */
        Cargo cargo = new Cargo(PLANCK, map);

        hunterHut0.putCargo(cargo);
    
        /* Verify that this is enough to construct the hunter hut */
        for (int i = 0; i < 500; i++) {
            assertTrue(hunterHut0.underConstruction());

            map.stepTime();
        }

        assertFalse(hunterHut0.ready());
    }

    @Test
    public void testConstructHunter() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        HunterHut f = new HunterHut(null);

        assertTrue(f.underConstruction());

        assertFalse(f.needsWorker());

        Utils.constructHouse(f, null);

        /* Verify that the forrester is unoccupied when it's newly constructed */
        assertTrue(f.needsWorker());

        /* Verify that the HunterHut requires a worker */
        assertTrue(f.needsWorker());

        Hunter hunter = new Hunter(null, null);

        /* Assign worker */
        f.assignWorker(hunter);

        assertFalse(f.needsWorker());
        assertTrue(f.getWorker().equals(hunter));
    }

    @Test(expected = Exception.class)
    public void testPromiseWorkerToUnfinishedHunter() throws Exception {
        HunterHut f = new HunterHut(null);

        assertTrue(f.underConstruction());

        f.promiseWorker(new Hunter(null, null));
    }

    @Test(expected = Exception.class)
    public void testAssignWorkerToUnfinishedHunter() throws Exception {
        HunterHut f = new HunterHut(null);

        assertTrue(f.underConstruction());

        f.assignWorker(new Hunter(null, null));
    }

    @Test(expected = Exception.class)
    public void testAssignWorkerTwice() throws Exception {
        HunterHut f = new HunterHut(null);

        Utils.constructHouse(f, null);

        f.assignWorker(new Hunter(null, null));

        f.assignWorker(new Hunter(null, null));
    }

    @Test(expected = Exception.class)
    public void testPromiseWorkerTwice() throws Exception {
        HunterHut f = new HunterHut(null);

        Utils.constructHouse(f, null);

        f.promiseWorker(new Hunter(null, null));

        f.promiseWorker(new Hunter(null, null));
    }

    @Test
    public void testHunterHutIsNotMilitary() throws Exception {
        HunterHut f = new HunterHut(null);

        Utils.constructHouse(f, null);

        assertFalse(f.isMilitaryBuilding());
        assertEquals(f.getHostedMilitary(), 0);
        assertEquals(f.getMaxHostedMilitary(), 0);
    }

    @Test
    public void testHunterHutUnderConstructionNotNeedsWorker() {
        HunterHut f = new HunterHut(null);

        assertFalse(f.needsWorker());
    }

    @Test
    public void testHunterIsAssignedToHunterHut() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place hunter hut */
        Point point1 = new Point(10, 4);
        Building hunterHut = map.placeBuilding(new HunterHut(player0), point1);

        /* Connect the hunter hut with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, hunterHut.getFlag(), headquarter.getFlag());

        /* Finish the hunter hut */
        Utils.constructHouse(hunterHut, map);
        
        /* Run game logic twice, once to place courier and once to place hunter */
        Utils.fastForward(2, map);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Hunter.class);
    }

    @Test
    public void testOnlyOneHunterIsAssignedToHunterHut() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place hunter hut */
        Point point1 = new Point(10, 4);
        Building hunterHut = map.placeBuilding(new HunterHut(player0), point1);

        /* Connect the hunter hut with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, hunterHut.getFlag(), headquarter.getFlag());

        /* Construct the hunter hut */
        constructHouse(hunterHut, map);
        
        /* Run game logic twice, once to place courier and once to place hunter */
        Utils.fastForward(2, map);

        assertEquals(map.getWorkers().size(), 3);

        /* Keep running the game loop and make sure no more workers are allocated */
        Utils.fastForward(200, map);

        assertEquals(map.getWorkers().size(), 3);
    }

    @Test
    public void testArrivedHunterFirstRestsInHut() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place hunter hut */
        Point point1 = new Point(10, 4);
        Building hunterHut = map.placeBuilding(new HunterHut(player0), point1);

        /* Construct the hunter hut */
        constructHouse(hunterHut, map);
        
        /* Manually place hunter */
        Hunter hunter = new Hunter(player0, map);

        Utils.occupyBuilding(hunter, hunterHut, map);
        
        assertTrue(hunter.isInsideBuilding());
        
        /* Run the game logic 99 times and make sure the hunter stays in the hut */        
        int i;
        for (i = 0; i < 99; i++) {
            assertTrue(hunter.isInsideBuilding());
            map.stepTime();
        }
        
        assertTrue(hunter.isInsideBuilding());
    }

    @Test
    public void testHunterStaysInHutWhenThereAreNoWildAnimalsNear() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place hunter hut */
        Point point1 = new Point(10, 4);
        Building hunterHut = map.placeBuilding(new HunterHut(player0), point1);

        /* Construct the hunter hut */
        constructHouse(hunterHut, map);
        
        /* Manually place hunter */
        Hunter hunter = new Hunter(player0, map);

        Utils.occupyBuilding(hunter, hunterHut, map);
        
        assertTrue(hunter.isInsideBuilding());
        
        /* Verify that the hunter stays in the hut as long as there are no wild 
           animals close */
        boolean animalClose;
        for (int i = 0; i < 500; i++) {

            /* Break if there are wild animals close */
            animalClose = false;
            for (WildAnimal animal : map.getWildAnimals()) {
                if (animal.getPosition().distance(hunterHut.getPosition()) < 20) {
                    animalClose = true;

                    break;
                }
            }

            if (animalClose) {
                break;
            }

            assertTrue(hunter.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testHunterLeavesHutWhenAWildAnimalIsNear() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place hunter hut */
        Point point1 = new Point(10, 4);
        Building hunterHut = map.placeBuilding(new HunterHut(player0), point1);

        /* Construct the hunter hut */
        constructHouse(hunterHut, map);
        
        /* Manually place hunter */
        Hunter hunter = new Hunter(player0, map);

        Utils.occupyBuilding(hunter, hunterHut, map);

        assertTrue(hunter.isInsideBuilding());

        /* Let the hunter rest */
        Utils.fastForward(99, map);

        assertTrue(hunter.isInsideBuilding());

        /* Wait for a wild animal to come close to the hut */
        WildAnimal animal = Utils.waitForWildAnimalCloseToPoint(hunterHut.getPosition(), map);

        /* Step once and make sure the hunter goes out of the hut */
        map.stepTime();

        assertFalse(hunter.isInsideBuilding());

        Point point = hunter.getTarget();
        assertNotNull(point);
    }

    @Test
    public void testHunterTracksWildAnimal() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place hunter hut */
        Point point1 = new Point(10, 4);
        Building hunterHut = map.placeBuilding(new HunterHut(player0), point1);

        /* Construct the hunter hut */
        constructHouse(hunterHut, map);
        
        /* Manually place hunter */
        Hunter hunter = new Hunter(player0, map);

        Utils.occupyBuilding(hunter, hunterHut, map);

        assertTrue(hunter.isInsideBuilding());

        /* Let the hunter rest */
        Utils.fastForward(99, map);

        assertTrue(hunter.isInsideBuilding());

        /* Wait for a wild animal to come close to the hut */
        WildAnimal animal = Utils.waitForWildAnimalCloseToPoint(hunterHut.getPosition(), map);

        map.stepTime();

        assertNotNull(hunter.getTarget());

        /* Wait for the hunter to go out the hut and reach the flag */
        assertEquals(hunter.getTarget(), hunterHut.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, hunter, hunterHut.getFlag().getPosition());

        /* Verify that the hunter tracks the wild animal */
        for (int i = 0; i < 1000; i++) {

            /* Break if the animal is too far away from the hunter hut */
            if (animal.getPosition().distance(hunterHut.getPosition()) > 70) {
                break;
            }

            /* Break if the hunter reaches the prey */
            if (animal.getPosition().distance(hunter.getPosition()) <= 2) {
                break;
            }
            
            /* Verify that the next planned step is toward the animal */
            assertTrue(hunter.getTarget().distance(animal.getPosition()) <= 
                    hunter.getPosition().distance(animal.getPosition()));

            /* Verify that the hunter doesn't plan too far ahead */
            assertTrue(hunter.getTarget().distance(hunter.getPosition()) < 3);

            /* Wait for the hunter to reach the next spot */
            Utils.fastForwardUntilWorkerReachesPoint(map, hunter, hunter.getTarget());
        }
    }

    @Test
    public void testHunterReachesAndKillsWildAnimalAndPicksUpMeat() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place hunter hut */
        Point point1 = new Point(10, 4);
        Building hunterHut = map.placeBuilding(new HunterHut(player0), point1);

        /* Construct the hunter hut */
        constructHouse(hunterHut, map);
        
        /* Manually place hunter */
        Hunter hunter = new Hunter(player0, map);

        Utils.occupyBuilding(hunter, hunterHut, map);

        assertTrue(hunter.isInsideBuilding());

        /* Let the hunter rest */
        Utils.fastForward(100, map);

        assertTrue(hunter.isInsideBuilding());

        /* Wait for a wild animal to come close to the hut */
        Utils.waitForWildAnimalCloseToPoint(hunterHut.getPosition(), map);

        map.stepTime();

        WildAnimal animal = hunter.getPrey();

        assertNotNull(hunter.getPrey());

        /* Wait for the hunter to reach the wild animal and start shooting */
        assertTrue(animal.isAlive());
        assertFalse(hunter.isShooting());

        for (int i = 0; i < 5000; i++) {

            if (hunter.isShooting()) {
                assertTrue(hunter.getPosition().distance(animal.getPosition()) <= 2);
                assertTrue(hunter.isExactlyAtPoint());

                break;
            }

            map.stepTime();
        }

        /* Verify that the hunter shoots for five steps of time */
        for (int i = 0; i < 5; i++) {
            assertTrue(hunter.isShooting());
            assertTrue(animal.isAlive());

            map.stepTime();
        }

        assertFalse(hunter.isShooting());

        /* Verify that the hunter killed the animal */
        assertFalse(animal.isAlive());

        /* Verify that the animal doesn't move after it's dead */
        Point lastAnimalPoint = animal.getPosition();

        /* Wait for the hunter to reach the dead animal */
        assertEquals(hunter.getTarget(), lastAnimalPoint);

        Utils.fastForwardUntilWorkerReachesPoint(map, hunter, lastAnimalPoint);

        assertEquals(animal.getPosition(), lastAnimalPoint);
        assertNotNull(hunter.getCargo());
        assertEquals(hunter.getCargo().getMaterial(), MEAT);
    }

    @Test
    public void testHunterReturnsHomeWithMeat() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place hunter hut */
        Point point1 = new Point(10, 4);
        Building hunterHut = map.placeBuilding(new HunterHut(player0), point1);

        /* Construct the hunter hut */
        constructHouse(hunterHut, map);
        
        /* Manually place hunter */
        Hunter hunter = new Hunter(player0, map);

        Utils.occupyBuilding(hunter, hunterHut, map);

        assertTrue(hunter.isInsideBuilding());

        /* Let the hunter rest */
        Utils.fastForward(99, map);

        assertTrue(hunter.isInsideBuilding());

        /* Wait for a wild animal to come close to the hut */
        WildAnimal animal = Utils.waitForWildAnimalCloseToPoint(hunterHut.getPosition(), map);

        /* Wait for the hunter to reach the wild animal */
        Utils.waitForActorsToGetClose(hunter, animal, 2, map);

        /* Wait for the hunter to kill the wild animal */
        Utils.fastForward(6, map);

        assertFalse(hunter.isShooting());
        assertFalse(animal.isAlive());

        /* Wait for the hunter to reach the dead animal */
        assertEquals(hunter.getTarget(), animal.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, hunter, animal.getPosition());

        assertNotNull(hunter.getCargo());

        /* Verify that the hunter walks home with the meat and puts it at the flag */
        assertEquals(hunter.getTarget(), hunterHut.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, hunter, hunterHut.getPosition());

        /* Verify that the hunter goes out and puts the meat at the flag */
        assertEquals(hunter.getTarget(), hunterHut.getFlag().getPosition());
        assertNotNull(hunter.getCargo());

        Cargo cargo = hunter.getCargo();

        Utils.fastForwardUntilWorkerReachesPoint(map, hunter, hunterHut.getFlag().getPosition());

        assertNull(hunter.getCargo());
        assertTrue(hunterHut.getFlag().getStackedCargo().contains(cargo));

        /* Verify that the hunter returns to the hut */
        assertEquals(hunter.getTarget(), hunterHut.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, hunter, hunterHut.getPosition());

        assertEquals(hunter.getPosition(), hunterHut.getPosition());
        assertTrue(hunter.isInsideBuilding());
    }

    @Test
    public void testHunterGoesBackToStorageWhenHunterHutIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing hunter hut */
        Point point26 = new Point(8, 8);
        Building hunterHut0 = map.placeBuilding(new HunterHut(player0), point26);

        /* Finish construction of the hunter hut */
        Utils.constructHouse(hunterHut0, map);

        /* Occupy the hunter hut */
        Utils.occupyBuilding(new Hunter(player0, map), hunterHut0, map);
        
        /* Destroy the hunter hut */
        Worker hunter = hunterHut0.getWorker();
        
        assertTrue(hunter.isInsideBuilding());
        assertEquals(hunter.getPosition(), hunterHut0.getPosition());

        hunterHut0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(hunter.isInsideBuilding());
        assertEquals(hunter.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(HUNTER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, hunter, headquarter0.getPosition());

        /* Verify that the miner is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(HUNTER), amount + 1);
    }

    @Test
    public void testHunterGoesBackOnToStorageOnRoadsIfPossibleWhenHunterHutIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing hunter hut */
        Point point26 = new Point(8, 8);
        Building hunterHut0 = map.placeBuilding(new HunterHut(player0), point26);

        /* Connect the hunter hut with the headquarter */
        map.placeAutoSelectedRoad(player0, hunterHut0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the hunter hut */
        Utils.constructHouse(hunterHut0, map);

        /* Occupy the hunter hut */
        Utils.occupyBuilding(new Hunter(player0, map), hunterHut0, map);
        
        /* Destroy the hunter hut */
        Worker hunter = hunterHut0.getWorker();
        
        assertTrue(hunter.isInsideBuilding());
        assertEquals(hunter.getPosition(), hunterHut0.getPosition());

        hunterHut0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(hunter.isInsideBuilding());
        assertEquals(hunter.getTarget(), headquarter0.getPosition());
    
        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point p : hunter.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test
    public void testProductionInHunterHutCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place hunter hut */
        Point point1 = new Point(8, 6);
        Building hunterHut0 = map.placeBuilding(new HunterHut(player0), point1);
        
        /* Connect the hunter hut and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the hunter hut */
        Utils.constructHouse(hunterHut0, map);
        
        /* Assign a worker to the hunter hut */
        Hunter hunter = new Hunter(player0, map);
        
        Utils.occupyBuilding(hunter, hunterHut0, map);
        
        assertTrue(hunter.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        assertTrue(hunter.isInsideBuilding());

        /* Stop production and verify that no tree is planted */
        hunterHut0.stopProduction();

        assertFalse(hunterHut0.isProductionEnabled());

        for (int i = 0; i < 3000; i++) {
            assertTrue(hunter.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInHunterHutCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place hunter hut */
        Point point1 = new Point(8, 6);
        Building hunterHut0 = map.placeBuilding(new HunterHut(player0), point1);
        
        /* Connect the hunter hut and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the hunter hut */
        Utils.constructHouse(hunterHut0, map);
        
        /* Assign a worker to the hunter hut */
        Hunter hunter = new Hunter(player0, map);
        
        Utils.occupyBuilding(hunter, hunterHut0, map);
        
        assertTrue(hunter.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the hunter to leave the hunter hut */
        for (int i = 0; i < 5000; i++) {
            if (!hunter.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(hunter.isInsideBuilding());

        /* Wait for the hunter to go back to the hunter hut */
        for (int i = 0; i < 300; i++) {
            if (hunter.isInsideBuilding()) {
                break;
            }
        
            map.stepTime();
        }

        assertTrue(hunter.isInsideBuilding());

        /* Stop production */
        hunterHut0.stopProduction();

        for (int i = 0; i < 10000; i++) {
            assertNull(hunter.getCargo());
            
            map.stepTime();
        }

        /* Resume production and verify that the hunter plants trees again */
        hunterHut0.resumeProduction();

        assertTrue(hunterHut0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            if (!hunter.isInsideBuilding()) {
                break;
            }
            
            map.stepTime();
        }

        assertFalse(hunter.isInsideBuilding());
    }

    @Test
    public void testAssignedhunterHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place hunter hut*/
        Point point1 = new Point(20, 14);
        Building hunterHut0 = map.placeBuilding(new HunterHut(player0), point1);

        /* Finish construction of the hunter hut */
        Utils.constructHouse(hunterHut0, map);
        
        /* Connect the hunter hut with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), hunterHut0.getFlag());

        /* Wait for hunter to get assigned and leave the headquarter */
        List<Hunter> workers = Utils.waitForWorkersOutsideBuilding(Hunter.class, 1, player0, map);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Hunter worker = workers.get(0);

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

        /* Place hunter hut close to the new border */
        Point point4 = new Point(28, 18);
        HunterHut hunterHut0 = map.placeBuilding(new HunterHut(player0), point4);

        /* Finish construction of the hunter hut */
        Utils.constructHouse(hunterHut0, map);

        /* Occupy the hunter hut */
        Hunter worker = Utils.occupyBuilding(new Hunter(player0, map), hunterHut0, map);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down*/
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }
}
