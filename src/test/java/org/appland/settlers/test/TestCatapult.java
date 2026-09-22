package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.CatapultWorker;
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
import static org.appland.settlers.model.actors.Rank.PRIVATE_RANK;
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

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place catapult
        var point22 = new Point(6, 12);
        var catapult0 = map.placeBuilding(new Catapult(player0), point22);

        // Deliver four planks and two stones
        var plankCargo = new Cargo(PLANK, map);
        var stoneCargo = new Cargo(STONE, map);

        catapult0.putCargo(plankCargo);
        catapult0.putCargo(plankCargo);
        catapult0.putCargo(plankCargo);
        catapult0.putCargo(plankCargo);
        catapult0.putCargo(stoneCargo);
        catapult0.putCargo(stoneCargo);

        // Assign builder
        Utils.assignBuilder(catapult0);

        // Verify that this is enough to construct the catapult
        for (int i = 0; i < 150; i++) {
            assertTrue(catapult0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(catapult0.isReady());
    }

    @Test
    public void testCatapultCannotBeConstructedWithTooFewPlanks() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place catapult
        var point22 = new Point(6, 12);
        var catapult0 = map.placeBuilding(new Catapult(player0), point22);

        // Deliver three plank and three stone
        var plankCargo = new Cargo(PLANK, map);
        var stoneCargo = new Cargo(STONE, map);

        catapult0.putCargo(plankCargo);
        catapult0.putCargo(plankCargo);
        catapult0.putCargo(plankCargo);
        catapult0.putCargo(stoneCargo);
        catapult0.putCargo(stoneCargo);

        // Assign builder
        Utils.assignBuilder(catapult0);

        // Verify that this is not enough to construct the catapult
        for (int i = 0; i < 500; i++) {
            assertTrue(catapult0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(catapult0.isReady());
    }

    @Test
    public void testCatapultCannotBeConstructedWithTooFewStones() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place catapult
        var point22 = new Point(6, 12);
        var catapult0 = map.placeBuilding(new Catapult(player0), point22);

        // Deliver four planks and one stone
        var plankCargo = new Cargo(PLANK, map);
        var stoneCargo = new Cargo(STONE, map);

        catapult0.putCargo(plankCargo);
        catapult0.putCargo(plankCargo);
        catapult0.putCargo(plankCargo);
        catapult0.putCargo(plankCargo);
        catapult0.putCargo(stoneCargo);

        // Assign builder
        Utils.assignBuilder(catapult0);

        // Verify that this is not enough to construct the catapult
        for (int i = 0; i < 500; i++) {
            assertTrue(catapult0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(catapult0.isReady());
    }

    @Test
    public void testCatapultCannotAddTooManyStonesWhenUnderConstruction() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place catapult
        var point22 = new Point(6, 12);
        var catapult0 = map.placeBuilding(new Catapult(player0), point22);

        // Deliver three stones
        var stoneCargo = new Cargo(STONE, map);

        catapult0.promiseDelivery(STONE);
        catapult0.putCargo(stoneCargo);

        catapult0.promiseDelivery(STONE);
        catapult0.putCargo(stoneCargo);

        // Verify that the catapult doesn't need more stones
        assertFalse(catapult0.needsMaterial(STONE));

        // Verify that delivering another stone throws an exception
        try {
            catapult0.putCargo(stoneCargo);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCatapultNeedsWorker() throws Exception {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place catapult
        var point3 = new Point(7, 9);
        var catapult = map.placeBuilding(new Catapult(player0), point3);

        // Unfinished catapult doesn't need worker
        assertFalse(catapult.needsWorker());

        // Finish construction of the catapult
        Utils.constructHouse(catapult);

        assertTrue(catapult.needsWorker());
    }

    @Test
    public void testCatapultGetsAssignedWorker() throws Exception {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place catapult
        var point3 = new Point(7, 9);
        var catapult = map.placeBuilding(new Catapult(player0), point3);

        // Place a road between the headquarters and the catapult
        var road0 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter.getFlag());

        // Finish construction of the catapult
        Utils.constructHouse(catapult);

        assertTrue(catapult.needsWorker());

        // Verify that a catapult worker leaves the headquarters
        var catapultWorker = Utils.waitForWorkerOutsideBuilding(CatapultWorker.class, player0);

        assertTrue(map.getWorkers().contains(catapultWorker));

        // Let the catapult worker reach the catapult
        assertNotNull(catapultWorker);
        assertEquals(catapultWorker.getTarget(), catapult.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(catapultWorker);

        assertTrue(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker);
    }

    @Test
    public void testOccupiedCatapultProducesNothing() throws Exception {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place catapult
        var point3 = new Point(7, 9);
        var catapult = map.placeBuilding(new Catapult(player0), point3);

        // Finish construction of the catapult
        Utils.constructHouse(catapult);

        // Occupy the catapult
        var worker = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getHome(), catapult);
        assertEquals(catapult.getWorker(), worker);

        // Deliver stones to the catapult
        var cargo = new Cargo(STONE, map);

        catapult.putCargo(cargo);
        catapult.putCargo(cargo);

        // Verify that the catapult doesn't produce anything
        for (int i = 0; i < 500; i++) {
            assertTrue(catapult.getFlag().getStackedCargo().isEmpty());
            assertNull(worker.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedCatapultProducesNothing() throws Exception {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place catapult
        var point3 = new Point(7, 9);
        var catapult = map.placeBuilding(new Catapult(player0), point3);

        // Finish construction of the catapult
        Utils.constructHouse(catapult);

        // Deliver stones to the catapult
        var cargo = new Cargo(STONE, map);

        catapult.putCargo(cargo);
        catapult.putCargo(cargo);

        // Verify that the catapult doesn't produce anything
        for (int i = 0; i < 500; i++) {
            assertTrue(catapult.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedCatapultWithStonesDoesNotThrowWithoutTarget() throws Exception {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place catapult
        var point3 = new Point(7, 9);
        var catapult = map.placeBuilding(new Catapult(player0), point3);

        // Finish construction of the catapult
        Utils.constructHouse(catapult);

        // Occupy the catapult
        var catapultWorker0 = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        // Remove all the stones in the headquarters
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        // Deliver stones to the catapult
        catapult.putCargo(new Cargo(STONE, map));
        catapult.putCargo(new Cargo(STONE, map));

        // Verify that the catapult doesn't throw projectiles
        assertTrue(map.getProjectiles().isEmpty());

        for (int i = 0; i < 500; i++) {
            map.stepTime();

            assertTrue(catapult.getFlag().getStackedCargo().isEmpty());
            assertEquals(catapult.getAmount(STONE), 2);
            assertEquals(map.getProjectiles().size(), 0);
        }
    }

    @Test
    public void testCatapultThrowsProjectile() throws Exception {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        Utils.adjustInventoryTo(headquarter0, STONE, 2);

        // Place headquarters
        var point1 = new Point(45, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place barracks and wait for it to get constructed and occupied
        var point2 = new Point(29, 5);
        var barracks0 = map.placeBuilding(new Barracks(player1), point2);
        var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

        Utils.waitForBuildingToBeConstructed(barracks0);

        Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

        // Place catapult and wait for it to get constructed and occupied
        var point3 = new Point(17, 5);
        var catapult = map.placeBuilding(new Catapult(player0), point3);
        var road1 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(catapult);

        var catapultWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        // Deliver stones to the catapult
        Utils.adjustInventoryTo(headquarter0, STONE, 1);

        Utils.waitForBuildingToHave(catapult, STONE, 1);

        // Verify that the catapult throws a projectile
        for (int i = 0; i < 99; i++) {
            assertTrue(map.getProjectiles().isEmpty());

            map.stepTime();
        }

        // Get the projectile
        assertEquals(map.getProjectiles().size(), 1);

        var projectile = map.getProjectiles().getFirst();

        assertNotNull(projectile);

        // Verify that the projectile is targeted at the barracks
        assertEquals(projectile.getTarget(), barracks0.getPosition());

        // Verify that the projectile comes from the catapult
        assertEquals(projectile.getSource(), catapult);

        // Verify that the projectile starts at the source
        assertEquals(projectile.getProgress(), 0);
    }

    @Test
    public void testCatapultDoesNotFireOnNonMilitaryEnemyBuildings() throws Exception {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place headquarters
        var point1 = new Point(49, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place woodcutter
        var point2 = new Point(33, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player1), point2);

        // Finish construction of the woodcutter
        Utils.constructHouse(woodcutter0);

        // Place catapult
        var point3 = new Point(21, 5);
        var catapult = map.placeBuilding(new Catapult(player0), point3);

        // Finish construction of the catapult
        Utils.constructHouse(catapult);

        // Occupy the catapult
        var catapultWorker0 = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        // Remove all the stones in the headquarters
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        // Deliver stones to the catapult
        catapult.putCargo(new Cargo(STONE, map));
        catapult.putCargo(new Cargo(STONE, map));

        // Verify that the catapult doesn't throw a projectile
        assertTrue(GameUtils.distanceInGameSteps(catapult.getPosition(), headquarter1.getPosition()) > 12);

        for (int i = 0; i < 500; i++) {
            if (!map.getProjectiles().isEmpty()) {
                System.out.println(map.getProjectiles());
            }

            assertTrue(map.getProjectiles().isEmpty());
            assertEquals(catapult.getAmount(STONE), 2);

            map.stepTime();
        }
    }

    @Test
    public void testCatapultDestroysEmptyBarracks() throws Exception {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 101);

        // Place headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place headquarters
        var point1 = new Point(45, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place barracks
        var point2 = new Point(33, 5);
        var barracks0 = map.placeBuilding(new Barracks(player1), point2);

        // Finish construction of the woodcutter
        Utils.constructHouse(barracks0);

        // Place catapult
        var point3 = new Point(21, 5);
        var catapult = map.placeBuilding(new Catapult(player0), point3);

        // Finish construction of the catapult
        Utils.constructHouse(catapult);

        // Occupy the catapult
        var catapultWorker0 = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        // Remove all the stones in the headquarters
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        // Verify that the catapult destroys the barracks
        for (int i = 0; i < 100; i++) {

            // Deliver stone to the catapult
            catapult.putCargo(new Cargo(STONE, map));

            // Wait for the catapult to throw a projectile
            var projectile = Utils.waitForCatapultToThrowProjectile(catapult);

            // Wait for the projectile to reach its target
            Utils.waitForProjectileToReachTarget(projectile, map);

            // Check if the projectile hit and destroyed the barracks
            if (barracks0.isBurningDown()) {
                break;
            }
        }

        assertTrue(barracks0.isBurningDown());
    }

    @Test
    public void testCatapultWaitsBeforeThrowingAfterReceivingStone() throws Exception {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        players.add(player1);
        var map = new GameMap(players, 100, 101);

        // Place headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place headquarters
        var point1 = new Point(45, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place barracks
        var point2 = new Point(33, 5);
        var barracks0 = map.placeBuilding(new Barracks(player1), point2);

        // Finish construction of the woodcutter
        Utils.constructHouse(barracks0);

        // Place catapult
        var point3 = new Point(21, 5);
        var catapult = map.placeBuilding(new Catapult(player0), point3);

        // Finish construction of the catapult
        Utils.constructHouse(catapult);

        // Occupy the catapult
        var catapultWorker0 = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        // Remove all the stones in the headquarters
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        // Make the catapult worker wait
        Utils.fastForward(300, map);

        // Deliver stone to the catapult
        catapult.putCargo(new Cargo(STONE, map));

        // Verify that the catapult worker waits before throwing a projectile
        for (int i = 0; i < 100; i++) {

            // Verify that the catapult hasn't thrown yet
            assertTrue(map.getProjectiles().isEmpty());

            map.stepTime();
        }

        assertFalse(map.getProjectiles().isEmpty());
    }

    @Test
    public void testCatapultFiresAtRightPointInTime() throws Exception {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        players.add(player1);
        var map = new GameMap(players, 100, 101);

        // Place headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place headquarters
        var point1 = new Point(45, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place barracks
        var point2 = new Point(29, 5);
        var barracks0 = map.placeBuilding(new Barracks(player1), point2);

        // Finish construction of the barracks
        Utils.constructHouse(barracks0);

        // Place catapult
        var point3 = new Point(21, 5);
        var catapult = map.placeBuilding(new Catapult(player0), point3);

        // Finish construction of the catapult
        Utils.constructHouse(catapult);

        // Occupy the catapult
        var catapultWorker0 = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        // Remove all the stones in the headquarters
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        // Deliver stones to the catapult
        catapult.putCargo(new Cargo(STONE, map));
        catapult.putCargo(new Cargo(STONE, map));

        // Verify that the catapult throws a projectile at the right time
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

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place catapult
        var point26 = new Point(8, 8);
        var catapult0 = map.placeBuilding(new Catapult(player0), point26);

        // Finish construction of the catapult
        Utils.constructHouse(catapult0);

        // Occupy the catapult
        Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0);

        // Destroy the catapult
        var catapultWorker = catapult0.getWorker();

        assertTrue(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getPosition(), catapult0.getPosition());

        catapult0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(catapultWorker, headquarter0.getPosition());

        // Verify that the worker isn't left on the map
        assertFalse(map.getWorkers().contains(catapultWorker));
    }

    @Test
    public void testCatapultWorkerGoesBackOnToStorageOnRoadsIfPossibleWhenCatapultIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place catapult
        var point26 = new Point(8, 8);
        var catapult0 = map.placeBuilding(new Catapult(player0), point26);

        // Connect the catapult with the headquarters
        map.placeAutoSelectedRoad(player0, catapult0.getFlag(), headquarter0.getFlag());

        // Finish construction of the catapult
        Utils.constructHouse(catapult0);

        // Occupy the catapult
        Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0);

        // Destroy the catapult
        var catapultWorker = catapult0.getWorker();

        assertTrue(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getPosition(), catapult0.getPosition());

        catapult0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getTarget(), headquarter0.getPosition());

        // Verify that the worker plans to use the roads
        var firstStep = true;
        for (var point : catapultWorker.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testDestroyedCatapultIsRemovedAfterSomeTime() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place catapult
        var point26 = new Point(8, 8);
        var catapult0 = map.placeBuilding(new Catapult(player0), point26);

        // Connect the catapult with the headquarters
        map.placeAutoSelectedRoad(player0, catapult0.getFlag(), headquarter0.getFlag());

        // Finish construction of the catapult
        Utils.constructHouse(catapult0);

        // Destroy the catapult
        catapult0.tearDown();

        assertTrue(catapult0.isBurningDown());

        // Wait for the catapult to stop burning
        Utils.fastForward(100, map);

        assertTrue(catapult0.isDestroyed());

        // Wait for the catapult to disappear
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

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place catapult
        var point26 = new Point(8, 8);
        var catapult0 = map.placeBuilding(new Catapult(player0), point26);

        // Finish construction of the catapult
        Utils.constructHouse(catapult0);

        // Remove the flag and verify that the driveway is removed
        assertNotNull(map.getRoad(catapult0.getPosition(), catapult0.getFlag().getPosition()));

        map.removeFlag(catapult0.getFlag());

        assertNull(map.getRoad(catapult0.getPosition(), catapult0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place catapult
        var point26 = new Point(8, 8);
        var catapult0 = map.placeBuilding(new Catapult(player0), point26);

        // Finish construction of the catapult
        Utils.constructHouse(catapult0);

        // Tear down the building and verify that the driveway is removed
        assertNotNull(map.getRoad(catapult0.getPosition(), catapult0.getFlag().getPosition()));

        catapult0.tearDown();

        assertNull(map.getRoad(catapult0.getPosition(), catapult0.getFlag().getPosition()));
    }

    //  -  Can catapults be stopped?        - 
    //  -  Test projectile's path           - 
    //  -  Test projectile kills military   - 
    //  -  Test projectiles miss sometimes  - 

    @Test
    public void testAssignedCatapultWorkerHasCorrectlySetPlayer() throws Exception {

        // Create players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);

        // Create game map
        var map = new GameMap(players, 50, 51);

        // Place headquarters
        var point0 = new Point(15, 15);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place catapult
        var point1 = new Point(20, 14);
        var catapult0 = map.placeBuilding(new Catapult(player0), point1);

        // Finish construction of the catapult
        Utils.constructHouse(catapult0);

        // Connect the catapult with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), catapult0.getFlag());

        // Wait for catapult worker to get assigned and leave the headquarters
        var workers = Utils.waitForWorkersOutsideBuilding(CatapultWorker.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        // Verify that the player is set correctly in the worker
        var worker = workers.getFirst();

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

        var players = new LinkedList<Player>();
        players.add(player0);
        players.add(player1);
        players.add(player2);

        // Create game map choosing two players
        var map = new GameMap(players, 100, 101);

        // Place player 2's headquarters
        var point10 = new Point(70, 70);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        // Place player 0's headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(45, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place fortress for player 0
        var point2 = new Point(21, 9);
        var fortress0 = map.placeBuilding(new Fortress(player0), point2);

        // Finish construction of the fortress
        Utils.constructHouse(fortress0);

        // Occupy the fortress
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        // Place catapult close to the new border
        var point4 = new Point(28, 18);
        var catapult0 = map.placeBuilding(new Catapult(player0), point4);

        // Finish construction of the catapult
        Utils.constructHouse(catapult0);

        // Occupy the catapult
        var worker = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0);

        // Verify that the worker goes back to its own storage when the fortress is torn down
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testCatapultWorkerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place catapult
        var point2 = new Point(14, 4);
        var catapult0 = map.placeBuilding(new Catapult(player0), point2.upLeft());

        // Connect headquarters and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, catapult0.getFlag());

        // Wait for the catapult worker to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(CatapultWorker.class, 1, player0);

        var catapultWorker = (CatapultWorker) null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof CatapultWorker) {
                catapultWorker = (CatapultWorker) worker;
            }
        }

        assertNotNull(catapultWorker);
        assertEquals(catapultWorker.getTarget(), catapult0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(catapultWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        // See that the catapult worker has started walking
        assertFalse(catapultWorker.isExactlyAtPoint());

        // Remove the next road
        map.removeRoad(road1);

        // Verify that the catapult worker continues walking to the flag
        Utils.fastForwardUntilWorkerReachesPoint(catapultWorker, flag0.getPosition());

        assertEquals(catapultWorker.getPosition(), flag0.getPosition());

        // Verify that the catapult worker returns to the headquarters when it reaches the flag
        assertEquals(catapultWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(catapultWorker, headquarter0.getPosition());
    }

    @Test
    public void testCatapultWorkerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place catapult
        var point2 = new Point(14, 4);
        var catapult0 = map.placeBuilding(new Catapult(player0), point2.upLeft());

        // Connect headquarters and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, catapult0.getFlag());

        // Wait for the catapult worker to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(CatapultWorker.class, 1, player0);

        var catapultWorker = (CatapultWorker) null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof CatapultWorker) {
                catapultWorker = (CatapultWorker) worker;
            }
        }

        assertNotNull(catapultWorker);
        assertEquals(catapultWorker.getTarget(), catapult0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(catapultWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        // See that the catapult worker has started walking
        assertFalse(catapultWorker.isExactlyAtPoint());

        // Remove the current road
        map.removeRoad(road0);

        // Verify that the catapult worker continues walking to the flag
        Utils.fastForwardUntilWorkerReachesPoint(catapultWorker, flag0.getPosition());

        assertEquals(catapultWorker.getPosition(), flag0.getPosition());

        // Verify that the catapult worker continues to the final flag
        assertEquals(catapultWorker.getTarget(), catapult0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(catapultWorker, catapult0.getFlag().getPosition());

        // Verify that the catapult worker goes out to catapult worker instead of going directly back
        assertNotEquals(catapultWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testCatapultWorkerReturnsToStorageIfCatapultIsDestroyed() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place catapult
        var point2 = new Point(14, 4);
        var catapult0 = map.placeBuilding(new Catapult(player0), point2.upLeft());

        // Connect headquarters and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, catapult0.getFlag());

        // Wait for the catapult to get constructed
        Utils.waitForBuildingToBeConstructed(catapult0);

        // Wait for the catapult worker to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(CatapultWorker.class, 1, player0);

        var catapultWorker = (CatapultWorker) null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof CatapultWorker) {
                catapultWorker = (CatapultWorker) worker;
            }
        }

        assertNotNull(catapultWorker);
        assertEquals(catapultWorker.getTarget(), catapult0.getPosition());

        // Wait for the catapult worker to reach the first flag
        Utils.fastForwardUntilWorkerReachesPoint(catapultWorker, flag0.getPosition());

        map.stepTime();

        // See that the catapult worker has started walking
        assertFalse(catapultWorker.isExactlyAtPoint());

        // Tear down the catapult
        catapult0.tearDown();

        // Verify that the catapult worker continues walking to the next flag
        Utils.fastForwardUntilWorkerReachesPoint(catapultWorker, catapult0.getFlag().getPosition());

        assertEquals(catapultWorker.getPosition(), catapult0.getFlag().getPosition());

        // Verify that the catapult worker goes back to storage
        assertEquals(catapultWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testCatapultWorkerGoesOffroadBackToClosestStorageWhenCatapultIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place catapult
        var point26 = new Point(17, 17);
        var catapult0 = map.placeBuilding(new Catapult(player0), point26);

        // Finish construction of the catapult
        Utils.constructHouse(catapult0);

        // Occupy the catapult
        Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0);

        // Place a second storage closer to the catapult
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        Utils.constructHouse(storehouse0);

        // Destroy the catapult
        var catapultWorker = catapult0.getWorker();

        assertTrue(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getPosition(), catapult0.getPosition());

        catapult0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getTarget(), storehouse0.getPosition());

        var amount = storehouse0.getAmount(CATAPULT_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(catapultWorker, storehouse0.getPosition());
    }

    @Test
    public void testCatapultWorkerReturnsOffroadAndAvoidsBurningStorageWhenCatapultIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place catapult
        var point26 = new Point(17, 17);
        var catapult0 = map.placeBuilding(new Catapult(player0), point26);

        // Finish construction of the catapult
        Utils.constructHouse(catapult0);

        // Occupy the catapult
        Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0);

        // Place a second storage closer to the catapult
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        Utils.constructHouse(storehouse0);

        // Destroy the storage
        storehouse0.tearDown();

        // Destroy the catapult
        var catapultWorker = catapult0.getWorker();

        assertTrue(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getPosition(), catapult0.getPosition());

        catapult0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getTarget(), headquarter0.getPosition());

        var amount = headquarter0.getAmount(CATAPULT_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(catapultWorker, headquarter0.getPosition());
    }

    @Test
    public void testCatapultWorkerReturnsOffroadAndAvoidsDestroyedStorageWhenCatapultIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place catapult
        var point26 = new Point(17, 17);
        var catapult0 = map.placeBuilding(new Catapult(player0), point26);

        // Finish construction of the catapult
        Utils.constructHouse(catapult0);

        // Occupy the catapult
        Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0);

        // Place a second storage closer to the catapult
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        Utils.constructHouse(storehouse0);

        // Destroy the storage
        storehouse0.tearDown();

        // Wait for the storage to burn down
        Utils.waitForBuildingToBurnDown(storehouse0);

        // Destroy the catapult
        var catapultWorker = catapult0.getWorker();

        assertTrue(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getPosition(), catapult0.getPosition());

        catapult0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getTarget(), headquarter0.getPosition());

        var amount = headquarter0.getAmount(CATAPULT_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(catapultWorker, headquarter0.getPosition());
    }

    @Test
    public void testCatapultWorkerReturnsOffroadAndAvoidsUnfinishedStorageWhenCatapultIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place catapult
        var point26 = new Point(17, 17);
        var catapult0 = map.placeBuilding(new Catapult(player0), point26);

        // Finish construction of the catapult
        Utils.constructHouse(catapult0);

        // Occupy the catapult
        Utils.occupyBuilding(new CatapultWorker(player0, map), catapult0);

        // Place a second storage closer to the catapult
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Destroy the catapult
        var catapultWorker = catapult0.getWorker();

        assertTrue(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getPosition(), catapult0.getPosition());

        catapult0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(catapultWorker.isInsideBuilding());
        assertEquals(catapultWorker.getTarget(), headquarter0.getPosition());

        var amount = headquarter0.getAmount(CATAPULT_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(catapultWorker, headquarter0.getPosition());
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place catapult
        var point26 = new Point(17, 17);
        var catapult0 = map.placeBuilding(new Catapult(player0), point26);

        // Place road to connect the headquarters and the catapult
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), catapult0.getFlag());

        // Finish construction of the catapult
        Utils.constructHouse(catapult0);

        // Wait for a worker to start walking to the building
        var worker = Utils.waitForWorkersOutsideBuilding(CatapultWorker.class, 1, player0).getFirst();

        // Wait for the worker to get to the building's flag
        Utils.fastForwardUntilWorkerReachesPoint(worker, catapult0.getFlag().getPosition());

        // Tear down the building
        catapult0.tearDown();

        // Verify that the worker goes to the building and then returns to the headquarters instead of entering
        assertEquals(worker.getTarget(), catapult0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(worker, catapult0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(worker, headquarter0.getPosition());
    }

    @Test
    public void testCatapultReportsCorrectOutput() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place catapult
        var point1 = new Point(6, 12);
        var catapult0 = map.placeBuilding(new Catapult(player0), point1);

        // Construct the catapult
        Utils.constructHouse(catapult0);

        // Verify that the reported output is correct
        assertEquals(catapult0.getProducedMaterial().length, 0);
    }

    @Test
    public void testCatapultReportsCorrectMaterialsNeededForConstruction() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place catapult
        var point1 = new Point(6, 12);
        var catapult0 = map.placeBuilding(new Catapult(player0), point1);

        // Verify that the reported needed construction material is correct
        assertEquals(catapult0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(catapult0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(catapult0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(catapult0.getCanHoldAmount(PLANK), 4);
        assertEquals(catapult0.getCanHoldAmount(STONE), 2);

        for (var material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(catapult0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testCatapultReportsCorrectMaterialsNeededForProduction() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place catapult
        var point1 = new Point(6, 12);
        var catapult0 = map.placeBuilding(new Catapult(player0), point1);

        // Construct the catapult
        Utils.constructHouse(catapult0);

        // Verify that the reported needed construction material is correct
        assertEquals(catapult0.getTypesOfMaterialNeeded().size(), 1);
        assertTrue(catapult0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(catapult0.getCanHoldAmount(STONE), 4);

        for (var material : Material.values()) {
            if (material == STONE) {
                continue;
            }

            assertEquals(catapult0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testCatapultShootsAtClosestBuilding() throws Exception {

        // Fire several shots
        for (var i = 0; i < 10; i++) {

            // Create new game map
            var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
            var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
            var map = new GameMap(List.of(player0, player1), 100, 101);

            // Place headquarters
            var point0 = new Point(15, 11);
            var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            Utils.adjustInventoryTo(headquarter0, STONE, 19);

            // Place headquarters
            var point1 = new Point(41, 25);
            var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

            // Place barracks six journeys from the catapult and wait for it to get constructed and occupied
            // Make it diagonally down from the headquarters, as close to the border as possible
            var point2 = new Point(34, 18);
            var barracks0 = map.placeBuilding(new Barracks(player1), point2);
            var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks0);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

            // Place second barracks for player 1
            var point5 = new Point(40, 18);
            var barracks2 = map.placeBuilding(new Barracks(player1), point5);
            var road1 = map.placeAutoSelectedRoad(player1, barracks2.getFlag(), headquarter1.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks2);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks2);

            // Place fortress for player 0
            // Make it diagonally down-left from the barracks, as close to the border as possible
            var point3 = new Point(25, 9);
            var fortress0 = map.placeBuilding(new Fortress(player0), point3);
            var road3 = map.placeAutoSelectedRoad(player0, fortress0.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(fortress0);

            Utils.waitForMilitaryBuildingToGetPopulated(fortress0);

            // Place catapult six journeys from the barracks and wait for it to get constructed and occupied
            // Make it diagonally up-right from the fortress, as close to the border as possible
            var point4 = new Point(30, 14);
            var catapult = map.placeBuilding(new Catapult(player0), point4);
            var road2 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(catapult);

            var catapultWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(catapult);

            assertTrue(catapultWorker0.isInsideBuilding());
            assertEquals(catapultWorker0.getHome(), catapult);
            assertEquals(catapult.getWorker(), catapultWorker0);
            assertEquals(4, GameUtils.distanceInGameSteps(catapult.getPosition(), barracks0.getPosition()));

            // Deliver stones to the catapult
            Utils.adjustInventoryTo(headquarter0, STONE, 1);

            Utils.waitForBuildingToHave(catapult, STONE, 1);

            // Verify that the catapult throws at the closest barracks
            assertTrue(
                    GameUtils.distanceInGameSteps(catapult.getPosition(), barracks0.getPosition()) <
                    GameUtils.distanceInGameSteps(catapult.getPosition(), barracks2.getPosition()));

            while (map.getProjectiles().isEmpty()) {
                map.stepTime();
            }

            var projectile = map.getProjectiles().getFirst();

            assertEquals(projectile.getTarget(), barracks0.getPosition());
        }
    }

    @Test
    public void testCatapultPrefersCloserTargetToTheLeft() throws Exception {

        // Fire several shots
        for (var i = 0; i < 10; i++) {

            // Create new game map
            var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
            var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
            var map = new GameMap(List.of(player0, player1), 100, 101);

            // Place headquarters
            var point0 = new Point(15, 11);
            var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            Utils.adjustInventoryTo(headquarter0, STONE, 19);

            // Place headquarters
            var point1 = new Point(37, 17);
            var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

            // Place the closer barracks to the left of the catapult
            var point2 = new Point(42, 12);
            var barracks0 = map.placeBuilding(new Barracks(player1), point2);
            var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks0);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

            // Place the farther barracks to the right of the catapult
            var point5 = new Point(40, 16);
            var barracks1 = map.placeBuilding(new Barracks(player1), point5);
            var road1 = map.placeAutoSelectedRoad(player1, barracks1.getFlag(), headquarter1.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks1);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks1);

            // Place fortress for player 0
            var point3 = new Point(23, 5);
            var fortress0 = map.placeBuilding(new Fortress(player0), point3);
            var road2 = map.placeAutoSelectedRoad(player0, fortress0.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(fortress0);

            Utils.waitForMilitaryBuildingToGetPopulated(fortress0);

            // Place catapult
            var point4 = new Point(29, 7);
            var catapult = map.placeBuilding(new Catapult(player0), point4);
            var road3 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(catapult);

            var catapultWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(catapult);

            assertTrue(catapultWorker0.isInsideBuilding());
            assertEquals(catapultWorker0.getHome(), catapult);
            assertEquals(catapult.getWorker(), catapultWorker0);

            // Verify the intended geometry
            var distanceToBarracks0 = GameUtils.distanceInGameSteps(catapult.getPosition(), barracks0.getPosition());
            var distanceToBarracks1 = GameUtils.distanceInGameSteps(catapult.getPosition(), barracks1.getPosition());

            assertTrue(distanceToBarracks0 < distanceToBarracks1);
            assertTrue(distanceToBarracks0 < 13);
            assertTrue(distanceToBarracks1 < 13);

            // Deliver stones to the catapult
            Utils.adjustInventoryTo(headquarter0, STONE, 1);

            Utils.waitForBuildingToHave(catapult, STONE, 1);

            // Wait for the catapult to fire
            while (map.getProjectiles().isEmpty()) {
                map.stepTime();
            }

            var projectile = map.getProjectiles().getFirst();

            // Verify that the catapult targets the closer building on the left
            assertEquals(barracks0.getPosition(), projectile.getTarget());
        }
    }

    @Test
    public void testCatapultPrefersCloserTargetAbove() throws Exception {

        // Fire several shots
        for (var i = 0; i < 10; i++) {

            // Create new game map
            var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
            var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
            var map = new GameMap(List.of(player0, player1), 100, 101);

            // Place headquarters
            var point0 = new Point(15, 11);
            var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            Utils.adjustInventoryTo(headquarter0, STONE, 19);

            // Place headquarters
            var point1 = new Point(41, 17);
            var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

            // Place the closer barracks above the catapult
            var point2 = new Point(33, 25);
            var point5 = new Point(38, 10);
            var barracks0 = map.placeBuilding(new Barracks(player1), point2);
            var road0 = map.placeAutoSelectedRoad(player1, barracks0.getFlag(), headquarter1.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks0);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

            // Place the farther barracks below the catapult
            var barracks1 = map.placeBuilding(new Barracks(player1), point5);
            var road1 = map.placeAutoSelectedRoad(player1, barracks1.getFlag(), headquarter1.getFlag());

            Utils.waitForBuildingToBeConstructed(barracks1);

            Utils.waitForMilitaryBuildingToGetPopulated(barracks1);

            // Place fortress for player 0
            var point3 = new Point(25, 9);
            var fortress0 = map.placeBuilding(new Fortress(player0), point3);
            var road2 = map.placeAutoSelectedRoad(player0, fortress0.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(fortress0);

            Utils.waitForMilitaryBuildingToGetPopulated(fortress0);

            // Place catapult
            var point4 = new Point(27, 19);
            Utils.printPlayerLand(player0, List.of(point2, point5, point4));
            var catapult = map.placeBuilding(new Catapult(player0), point4);
            var road3 = map.placeAutoSelectedRoad(player0, catapult.getFlag(), headquarter0.getFlag());

            Utils.waitForBuildingToBeConstructed(catapult);

            var catapultWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(catapult);

            assertTrue(catapultWorker0.isInsideBuilding());
            assertEquals(catapultWorker0.getHome(), catapult);
            assertEquals(catapult.getWorker(), catapultWorker0);

            // Verify the intended geometry
            assertTrue(barracks0.getPosition().y > catapult.getPosition().y);
            assertTrue(barracks1.getPosition().y < catapult.getPosition().y);

            assertTrue(
                    GameUtils.distanceInGameSteps(catapult.getPosition(), barracks0.getPosition()) <
                            GameUtils.distanceInGameSteps(catapult.getPosition(), barracks1.getPosition()));

            // Deliver stones to the catapult
            Utils.adjustInventoryTo(headquarter0, STONE, 1);

            Utils.waitForBuildingToHave(catapult, STONE, 1);

            // Wait for the catapult to fire
            while (map.getProjectiles().isEmpty()) {
                map.stepTime();
            }

            var projectile = map.getProjectiles().getFirst();

            // Verify that the catapult targets the closer building above it
            assertEquals(barracks0.getPosition(), projectile.getTarget());
        }
    }
}
