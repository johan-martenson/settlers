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
import org.appland.settlers.model.actors.Armorer;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Storehouse;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.junit.Assert.*;

/**
 *
 * @author johan
*/
public class TestArmory {

    @Test
    public void testArmoryOnlyNeedsTwoPlanksAndTwoStonesForConstruction() throws Exception {

        // Starting new game */
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(6, 12);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Deliver two plank and two stone
        Utils.deliverCargos(armory0, PLANK, 2);
        Utils.deliverCargos(armory0, STONE, 2);

        // Assign a builder
        Utils.assignBuilder(armory0);

        // Verify that this is enough to construct the armory
        for (int i = 0; i < 150; i++) {
            assertTrue(armory0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(armory0.isReady());
    }

    @Test
    public void testArmoryCannotBeConstructedWithTooFewPlanks() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(6, 12);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Assign a builder
        Utils.assignBuilder(armory0);

        // Deliver one plank and two stone
        Utils.deliverCargos(armory0, PLANK, 1);
        Utils.deliverCargos(armory0, STONE, 2);

        // Verify that this is not enough to construct the armory
        for (int i = 0; i < 500; i++) {
            assertTrue(armory0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(armory0.isReady());
    }

    @Test
    public void testArmoryCannotBeConstructedWithTooFewStones() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(6, 12);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Deliver two planks and one stones
        Utils.deliverCargos(armory0, PLANK, 2);
        Utils.deliverCargos(armory0, STONE, 1);

        // Assign a builder
        Utils.assignBuilder(armory0);

        // Verify that this is not enough to construct the armory
        for (int i = 0; i < 500; i++) {
            assertTrue(armory0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(armory0.isReady());
    }

    @Test
    public void testArmoryNeedsWorker() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory = map.placeBuilding(new Armory(player0), point1);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, armory.getFlag(), headquarter0.getFlag());

        // Unfinished armory doesn't need worker
        assertFalse(armory.needsWorker());

        // Finish construction of the armory
        Utils.constructHouse(armory);

        assertTrue(armory.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneArmorerAtStart() {
        var headquarter = new Headquarter(null);

        assertEquals(headquarter.getAmount(ARMORER), 1);
    }

    @Test
    public void testArmoryGetsAssignedWorker() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Place road to connect the armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Wait for the armory to get constructed
        Utils.waitForBuildingToBeConstructed(armory0);

        assertTrue(armory0.needsWorker());

        // Verify that a armory worker leaves the headquarters
        Utils.fastForward(3, map);

        assertTrue(map.getWorkers().size() >= 3);

        // Let the armory worker reach the armory
        Armorer armorer0 = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Armorer) {
                armorer0 = (Armorer)worker;
            }
        }

        assertNotNull(armorer0);
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, armorer0);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory0);
        assertEquals(armory0.getWorker(), armorer0);
    }

    @Test
    public void testArmorerIsNotASoldier() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Place road to connect the armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Wait for the armory to get constructed
        Utils.waitForBuildingToBeConstructed(armory0);

        assertTrue(armory0.needsWorker());

        // Verify that a armory worker leaves the headquarters
        Utils.fastForward(3, map);

        assertTrue(map.getWorkers().size() >= 3);

        // Verify that the armorer is not a soldier
        Armorer armorer0 = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Armorer) {
                armorer0 = (Armorer)worker;
            }
        }

        assertNotNull(armorer0);
        assertFalse(armorer0.isSoldier());
    }

    @Test
    public void testArmorerGetsCreatedFromTongs() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Remove all armorers from the headquarters and add tongs
        Utils.adjustInventoryTo(headquarter0, ARMORER, 0);
        Utils.adjustInventoryTo(headquarter0, Material.TONGS, 1);

        // Place armory
        var point1 = new Point(7, 9);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Place road to connect the armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Wait for the armory to get constructed
        Utils.waitForBuildingToBeConstructed(armory0);

        assertTrue(armory0.needsWorker());

        // Verify that a armory worker leaves the headquarters
        Utils.fastForward(3, map);

        assertTrue(map.getWorkers().size() >= 3);

        // Let the armory worker reach the armory
        Armorer armorer0 = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Armorer) {
                armorer0 = (Armorer)worker;
            }
        }

        assertNotNull(armorer0);
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, armorer0);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory0);
        assertEquals(armory0.getWorker(), armorer0);
    }

    @Test
    public void testOccupiedArmoryWithoutCoalAndIronProducesNothing() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Empty coal and iron bars from the headquarters
        Utils.adjustInventoryTo(headquarter0, COAL, 0);
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory = map.placeBuilding(new Armory(player0), point1);

        // Connect the armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, armory.getFlag(), headquarter0.getFlag());

        // Wait for the armory to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(armory);

        var armorer0 = Utils.waitForNonMilitaryBuildingToGetPopulated(armory);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory);
        assertEquals(armory.getWorker(), armorer0);

        // Verify that the armory doesn't produce anything
        for (int i = 0; i < 500; i++) {
            assertTrue(armory.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
            assertFalse(armory.isWorking());

            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedArmoryProducesNothing() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory = map.placeBuilding(new Armory(player0), point1);

        // Finish construction of the armory
        Utils.constructHouse(armory);

        // Verify that the armory doesn't produce anything
        for (int i = 0; i < 500; i++) {
            assertTrue(armory.getFlag().getStackedCargo().isEmpty());

            map.stepTime();
        }
    }

    @Test
    public void testOccupiedArmoryWithCoalAndIronProducesWeapon() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Place road to connect the armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Wait for the armory to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(armory0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(armory0);

        var armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory0);
        assertEquals(armory0.getWorker(), armorer0);

        // Deliver material to the armory
        Utils.deliverCargo(armory0, IRON_BAR);
        Utils.deliverCargo(armory0, COAL);

        assertFalse(armory0.isWorking());

        // Verify that the armory produces weapons
        for (int i = 0; i < 99; i++) {
            map.stepTime();

            assertFalse(armory0.isWorking());
            assertTrue(armory0.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
        }

        for (int i = 0; i < 50; i++) {
            map.stepTime();

            assertTrue(armory0.isWorking());
            assertTrue(armory0.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
        }

        map.stepTime();

        assertFalse(armory0.isWorking());
        assertNotNull(armorer0.getCargo());
        assertEquals(armorer0.getCargo().getMaterial(), SWORD);
        assertTrue(armory0.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testArmorerLeavesWeaponAtTheFlag() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Place road to connect the armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Wait for the armory to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(armory0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(armory0);

        var armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory0);
        assertEquals(armory0.getWorker(), armorer0);

        // Deliver ingredients to the armory
        Utils.deliverCargo(armory0, IRON_BAR);
        Utils.deliverCargo(armory0, COAL);

        // Verify that the armory produces weapons
        for (int i = 0; i < 149; i++) {
            map.stepTime();

            assertTrue(armory0.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
        }

        map.stepTime();

        assertNotNull(armorer0.getCargo());
        assertEquals(armorer0.getCargo().getMaterial(), SWORD);
        assertTrue(armory0.getFlag().getStackedCargo().isEmpty());

        // Verify that the armory worker leaves the cargo at the flag
        assertEquals(armorer0.getTarget(), armory0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        assertFalse(armory0.getFlag().getStackedCargo().isEmpty());
        assertNull(armorer0.getCargo());
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        // Verify that the armorer goes back to the armory
        Utils.fastForwardUntilWorkersReachTarget(map, armorer0);

        assertTrue(armorer0.isInsideBuilding());
    }

    @Test
    public void testWeaponsAreNotDeliveredToStorehouseUnderConstruction() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point3 = new Point(6, 4);
        var headquarter = map.placeBuilding(new Headquarter(player0), point3);

        // Adjust the inventory so that there are no stones, planks, or wood
        Utils.clearInventory(headquarter, STONE, PLANK, IRON_BAR, COAL, SHIELD, SWORD);

        // Place storehouse
        var point4 = new Point(10, 4);
        var storehouse = map.placeBuilding(new Storehouse(player0), point4);

        // Connect the storehouse to the headquarters
        var road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        // Place the armory
        var point1 = new Point(14, 4);
        var armory = map.placeBuilding(new Armory(player0), point1);

        // Connect the armory with the storehouse
        var road0 = map.placeAutoSelectedRoad(player0, armory.getFlag(), storehouse.getFlag());

        // Deliver the needed material to construct the armory
        Utils.deliverCargos(armory, PLANK, 2);
        Utils.deliverCargos(armory, STONE, 2);

        // Wait for the armory to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(armory);

        Utils.waitForNonMilitaryBuildingToGetPopulated(armory);

        // Wait for the courier on the road between the storehouse and the armory to have a plank cargo
        Utils.deliverCargos(armory, COAL, IRON_BAR);

        Utils.waitForFlagToGetStackedCargo(map, armory.getFlag(), 1);

        var weapon = armory.getFlag().getStackedCargo().getFirst().getMaterial();

        assertTrue(weapon == SWORD || weapon == SHIELD);

        // Wait for the courier to pick up the cargo
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        // Verify that the courier delivers the cargo to the storehouse's flag so that it can continue to the headquarters
        assertEquals(headquarter.getAmount(weapon), 0);
        assertEquals(armory.getAmount(weapon), 0);
        assertFalse(storehouse.needsMaterial(weapon));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().getFirst().getMaterial().equals(weapon));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testProductionOfOneSwordConsumesOneCoalAndOneIron() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Empty coal and iron bars from the headquarters
        Utils.adjustInventoryTo(headquarter0, COAL, 0);
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Place road to connect the armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Wait for the armory to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(armory0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(armory0);

        // Deliver ingredients to the armory
        Utils.deliverCargo(armory0, IRON_BAR);
        Utils.deliverCargo(armory0, COAL);

        // Wait until the armory worker produces a weapons
        assertEquals(armory0.getAmount(IRON_BAR), 1);
        assertEquals(armory0.getAmount(COAL), 1);

        Utils.fastForward(150, map);

        assertEquals(armory0.getAmount(COAL), 0);
        assertEquals(armory0.getAmount(IRON_BAR), 0);
    }

    @Test
    public void testProductionCountdownStartsWhenMaterialsAreAvailable() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Finish construction of the armory
        Utils.constructHouse(armory0);

        // Populate the armory
        var armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory0);

        // Fast forward so that the armory var would have produced weapons if it had had the ingredients
        Utils.fastForward(150, map);

        assertNull(armorer0.getCargo());

        // Deliver ingredients to the armory
        Utils.deliverCargo(armory0, IRON_BAR);
        Utils.deliverCargo(armory0, COAL);

        // Verify that it takes 50 steps for the armory worker to produce the plank
        for (int i = 0; i < 50; i++) {
            assertNull(armorer0.getCargo());

            map.stepTime();
        }

        map.stepTime();

        assertNotNull(armorer0.getCargo());
    }

    @Test
    public void testArmoryShiftsBetweenProducingSwordsAndShields() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Place road to connect the armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Wait for the armory to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(armory0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(armory0);

        var armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory0);
        assertEquals(armory0.getWorker(), armorer0);

        // Deliver material to the armory
        Utils.deliverCargos(armory0, IRON_BAR, 2);
        Utils.deliverCargos(armory0, COAL, 2);

        // Verify that the armory produces a sword
        Utils.fastForward(150, map);

        assertNotNull(armorer0.getCargo());
        assertEquals(armorer0.getCargo().getMaterial(), SWORD);

        // Wait for the armorer to put the sword at the flag
        assertEquals(armorer0.getTarget(), armory0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        // Wait for the armorer to go back to the armory
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, armorer0);

        // Verify that the armorer produces a shield
        Utils.fastForward(150, map);

        assertEquals(armorer0.getCargo().getMaterial(), SHIELD);
    }

    @Test
    public void testArmoryWithoutConnectedStorageKeepsProducing() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(8, 8);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Finish construction of the armory
        Utils.constructHouse(armory0);

        // Occupy the armory
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        // Deliver material to the armory
        Utils.deliverCargos(armory0, IRON_BAR, 2);
        Utils.deliverCargos(armory0, COAL, 2);

        // Let the armorer rest
        Utils.fastForward(100, map);

        // Wait for the armorer to produce a new weapon cargo
        Utils.fastForward(50, map);

        var armorer0 = armory0.getWorker();

        assertNotNull(armorer0.getCargo());

        // Verify that the armorer puts the weapon cargo at the flag
        assertEquals(armorer0.getTarget(), armory0.getFlag().getPosition());
        assertTrue(armory0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        assertNull(armorer0.getCargo());
        assertFalse(armory0.getFlag().getStackedCargo().isEmpty());

        // Wait for the worker to go back to the armory
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getPosition());

        // Wait for the worker to rest and produce another cargo
        Utils.fastForward(150, map);

        assertNotNull(armorer0.getCargo());

        // Verify that the second cargo is put at the flag
        assertEquals(armorer0.getTarget(), armory0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        assertNull(armorer0.getCargo());
        assertEquals(armory0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(8, 8);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Finish construction of the armory
        Utils.constructHouse(armory0);

        // Deliver material to the armory
        Utils.deliverCargos(armory0, IRON_BAR, 2);
        Utils.deliverCargos(armory0, COAL, 2);

        // Occupy the armory
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        // Let the armorer rest
        Utils.fastForward(100, map);

        // Wait for the armorer to produce a new weapon cargo
        Utils.fastForward(50, map);

        var armorer0 = armory0.getWorker();

        assertNotNull(armorer0.getCargo());

        // Verify that the armorer puts the weapon cargo at the flag
        assertEquals(armorer0.getTarget(), armory0.getFlag().getPosition());
        assertTrue(armory0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        assertNull(armorer0.getCargo());
        assertFalse(armory0.getFlag().getStackedCargo().isEmpty());

        // Wait to let the cargo remain at the flag without any connection to the storage
        Cargo cargo = armory0.getFlag().getStackedCargo().getFirst();

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), armory0.getFlag().getPosition());

        // Remove material the armory needs from the headquarters
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 0);
        Utils.adjustInventoryTo(headquarter0, COAL, 0);

        // Connect the armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory0.getFlag());

        // Assign a courier to the road
        var courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        // Wait for the courier to reach the idle point of the road
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), armory0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        // Verify that the courier walks to pick up the cargo
        map.stepTime();

        assertEquals(courier.getTarget(), armory0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        // Verify that the courier has picked up the cargo
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);
        assertTrue(cargo.getMaterial() == SWORD || cargo.getMaterial() == SHIELD);

        // Verify that the courier delivers the cargo to the headquarters
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        var material = cargo.getMaterial();
        int amount = headquarter0.getAmount(material);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        // Verify that the courier has delivered the cargo to the headquarters
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(material), amount + 1);
    }

    @Test
    public void testArmorerGoesBackToStorageWhenArmoryIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(8, 8);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Finish construction of the armory
        Utils.constructHouse(armory0);

        // Occupy the armory
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        // Destroy the armory
        var armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getPosition(), armory0.getPosition());

        armory0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(armorer0.isInsideBuilding());
        assertEquals(armorer0.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(ARMORER);

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, headquarter0.getPosition());

        // Verify that the armorer is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(ARMORER), amount + 1);
    }

    @Test
    public void testArmorerGoesBackOnToStorageOnRoadsIfPossibleWhenArmoryIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(8, 8);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Connect the armory with the headquarters
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Finish construction of the armory
        Utils.constructHouse(armory0);

        // Occupy the armory
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        // Destroy the armory
        var armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getPosition(), armory0.getPosition());

        armory0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(armorer0.isInsideBuilding());
        assertEquals(armorer0.getTarget(), headquarter0.getPosition());

        // Verify that the worker plans to use the roads
        boolean firstStep = true;
        for (var point : armorer0.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testProductionInArmoryCanBeStopped() throws Exception {

        // Create game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(12, 8);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Connect the armory and the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Finish the armory
        Utils.constructHouse(armory0);

        // Deliver material to the armory
        Utils.deliverCargos(armory0, IRON_BAR, 2);
        Utils.deliverCargos(armory0, COAL, 2);

        // Assign a worker to the armory
        var armorer0 = new Armorer(player0, map);

        Utils.occupyBuilding(armorer0, armory0);

        assertTrue(armorer0.isInsideBuilding());

        // Let the worker rest
        Utils.fastForward(100, map);

        // Wait for the armorer to produce cargo
        Utils.fastForwardUntilWorkerProducesCargo(map, armorer0);

        // Wait for the worker to deliver the cargo
        assertEquals(armorer0.getTarget(), armory0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        // Stop production and verify that no water is produced
        armory0.stopProduction();

        assertFalse(armory0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(armorer0.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInArmoryCanBeResumed() throws Exception {

        // Create game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(12, 8);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Connect the armory and the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Finish the armory
        Utils.constructHouse(armory0);

        // Assign a worker to the armory
        var armorer0 = new Armorer(player0, map);

        Utils.occupyBuilding(armorer0, armory0);

        assertTrue(armorer0.isInsideBuilding());

        // Deliver material to the armory
        Utils.deliverCargos(armory0, IRON_BAR, 2);
        Utils.deliverCargos(armory0, COAL, 2);

        // Let the worker rest
        Utils.fastForward(100, map);

        // Wait for the armorer to produce water
        Utils.fastForwardUntilWorkerProducesCargo(map, armorer0);

        // Wait for the worker to deliver the cargo
        assertEquals(armorer0.getTarget(), armory0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        // Stop production
        armory0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(armorer0.getCargo());

            map.stepTime();
        }

        // Resume production and verify that the armory produces water again
        armory0.resumeProduction();

        assertTrue(armory0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, armorer0);

        assertNotNull(armorer0.getCargo());
    }

    @Test
    public void testAssignedArmorerHasCorrectlySetPlayer() throws Exception {

        // Create players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 50);

        // Place headquarters
        var point0 = new Point(15, 15);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(20, 14);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Finish construction of the armory
        Utils.constructHouse(armory0);

        // Connect the armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory0.getFlag());

        // Wait for armorer to get assigned and leave the headquarters
        List<Armorer> workers = Utils.waitForWorkersOutsideBuilding(Armorer.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        // Verify that the player is set correctly in the worker
        var worker = workers.getFirst();

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        // Create player list with three players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place player 2's headquarters
        var point0 = new Point(70, 70);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point0);

        // Place player 0's headquarters
        var point1 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        // Place player 1's headquarters
        var point2 = new Point(45, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point2);

        // Place fortress for player 0
        var point3 = new Point(21, 9);
        var fortress0 = map.placeBuilding(new Fortress(player0), point3);

        // Finish construction of the fortress
        Utils.constructHouse(fortress0);

        // Occupy the fortress
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        // Place armory close to the new border
        var point4 = new Point(28, 18);
        var armory0 = map.placeBuilding(new Armory(player0), point4);

        // Finish construction of the armory
        Utils.constructHouse(armory0);

        // Occupy the armory
        var worker = Utils.occupyBuilding(new Armorer(player0, map), armory0);

        // Verify that the enemy's headquarters is closer
        assertTrue(armory0.getPosition().distance(headquarter0.getPosition()) >
                   armory0.getPosition().distance(headquarter1.getPosition()));

        // Verify that the worker goes back to its own storage when the fortress is torn down
        fortress0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, armory0.getFlag().getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testNonMilitaryBuildingCannotBeUpgraded() throws Exception {

        // Create players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 50);

        // Place headquarters
        var point0 = new Point(15, 15);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(20, 14);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Finish construction of the armory
        Utils.constructHouse(armory0);

        // Verify that non-military building cannot be upgraded
        try {
            armory0.upgrade();

            fail();
        } catch (InvalidUserActionException e) {}
    }

    @Test
    public void testArmorerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place armory
        var point2 = new Point(14, 4);
        var armory0 = map.placeBuilding(new Armory(player0), point2.upLeft());

        // Connect headquarters and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, armory0.getFlag());

        // Wait for the armorer to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(Armorer.class, 1, player0);

        Armorer armorer0 = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Armorer) {
                armorer0 = (Armorer) worker;
            }
        }

        assertNotNull(armorer0);
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, headquarter0.getFlag().getPosition());

        map.stepTime();

        // See that the armorer has started walking
        assertFalse(armorer0.isExactlyAtPoint());

        // Remove the next road
        map.removeRoad(road1);

        // Verify that the armorer continues walking to the flag
        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, flag0.getPosition());

        assertEquals(armorer0.getPosition(), flag0.getPosition());

        // Verify that the armorer returns to the headquarters when it reaches the flag
        assertEquals(armorer0.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, headquarter0.getPosition());
    }

    @Test
    public void testArmorerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place armory
        var point2 = new Point(14, 4);
        var armory0 = map.placeBuilding(new Armory(player0), point2.upLeft());

        // Connect headquarters and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, armory0.getFlag());

        // Wait for the armorer to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(Armorer.class, 1, player0);

        Armorer armorer0 = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Armorer) {
                armorer0 = (Armorer) worker;
            }
        }

        assertNotNull(armorer0);
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, headquarter0.getFlag().getPosition());

        map.stepTime();

        // See that the armorer has started walking
        assertFalse(armorer0.isExactlyAtPoint());

        // Remove the current road
        map.removeRoad(road0);

        // Verify that the armorer continues walking to the flag
        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, flag0.getPosition());

        assertEquals(armorer0.getPosition(), flag0.getPosition());

        // Verify that the armorer continues to the final flag
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        // Verify that the armorer goes out to the armory instead of going directly back
        assertNotEquals(armorer0.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testArmorerReturnsToStorageIfArmoryIsDestroyed() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place armory
        var point2 = new Point(14, 4);
        var armory0 = map.placeBuilding(new Armory(player0), point2.upLeft());

        // Connect headquarters and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, armory0.getFlag());

        // Wait for the armory to get constructed
        assertTrue(headquarter0.getAmount(BUILDER) > 0);
        assertTrue(headquarter0.getAmount(PLANK) > 20);
        assertTrue(headquarter0.getAmount(STONE) > 20);
        assertTrue(armory0.isPlanned());
        assertTrue(armory0.needsMaterial(PLANK));
        assertTrue(armory0.needsMaterial(STONE));
        assertTrue(map.arePointsConnectedByRoads(armory0.getPosition(), headquarter0.getPosition()));

        Utils.waitForBuildingToBeConstructed(armory0);

        assertTrue(armory0.isReady());

        // Wait for the armorer to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(Armorer.class, 1, player0);

        Armorer armorer0 = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Armorer) {
                armorer0 = (Armorer) worker;
            }
        }

        assertNotNull(armorer0);
        assertEquals(armorer0.getTarget(), armory0.getPosition());

        // Wait for the armorer to reach the first flag
        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, flag0.getPosition());

        map.stepTime();

        // See that the armorer has started walking
        assertFalse(armorer0.isExactlyAtPoint());

        // Tear down the armory
        armory0.tearDown();

        // Verify that the armorer continues walking to the next flag
        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        assertEquals(armorer0.getPosition(), armory0.getFlag().getPosition());

        // Verify that the armorer goes back to storage
        assertEquals(armorer0.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testCannotTearDownArmoryTwice() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(14, 4);
        var armory0 = map.placeBuilding(new Armory(player0), point1.upLeft());

        // Connect armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory0.getFlag());

        // Wait for the armory to get constructed
        Utils.fastForwardUntilBuildingIsConstructed(armory0);

        // Tear down the armory
        armory0.tearDown();

        // Verify that it cannot be torn down twice
        try {
            armory0.tearDown();

            fail();
        } catch (Throwable t) {
            assertEquals(t.getClass(), InvalidUserActionException.class);
        }
    }

    @Test
    public void testArmorerGoesOffroadBackToClosestStorageWhenArmoryIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(17, 17);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Finish construction of the armory
        Utils.constructHouse(armory0);

        // Occupy the armory
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        // Place a second storage closer to the armory
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        Utils.constructHouse(storehouse0);

        // Destroy the armory
        var armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getPosition(), armory0.getPosition());

        armory0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(armorer0.isInsideBuilding());
        assertEquals(armorer0.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(ARMORER);

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, storehouse0.getPosition());

        // Verify that the armorer is stored correctly in the headquarters
        assertEquals(storehouse0.getAmount(ARMORER), amount + 1);
    }

    @Test
    public void testArmorerReturnsOffroadAndAvoidsBurningStorageWhenArmoryIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(17, 17);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Finish construction of the armory
        Utils.constructHouse(armory0);

        // Occupy the armory
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        // Place a second storage closer to the armory
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        Utils.constructHouse(storehouse0);

        // Destroy the storage
        storehouse0.tearDown();

        // Destroy the armory
        var armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getPosition(), armory0.getPosition());

        armory0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(armorer0.isInsideBuilding());
        assertEquals(armorer0.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(ARMORER);

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, headquarter0.getPosition());

        // Verify that the armorer is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(ARMORER), amount + 1);
    }

    @Test
    public void testArmorerReturnsOffroadAndAvoidsDestroyedStorageWhenArmoryIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(17, 17);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Finish construction of the armory
        Utils.constructHouse(armory0);

        // Occupy the armory
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        // Place a second storage closer to the armory
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        Utils.constructHouse(storehouse0);

        // Destroy the storage
        storehouse0.tearDown();

        // Wait for the storage to burn down
        Utils.waitForBuildingToBurnDown(storehouse0);

        // Destroy the armory
        var armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getPosition(), armory0.getPosition());

        armory0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(armorer0.isInsideBuilding());
        assertEquals(armorer0.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(ARMORER);

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, headquarter0.getPosition());

        // Verify that the armorer is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(ARMORER), amount + 1);
    }

    @Test
    public void testArmorerReturnsOffroadAndAvoidsUnfinishedStorageWhenArmoryIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(17, 17);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Finish construction of the armory
        Utils.constructHouse(armory0);

        // Occupy the armory
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        // Place a second storage closer to the armory
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Destroy the armory
        var armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getPosition(), armory0.getPosition());
        assertTrue(armorer0 instanceof Armorer);
        assertTrue(storehouse0.isPlanned());

        armory0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(armorer0.isInsideBuilding());
        assertEquals(armorer0.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(ARMORER);

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, headquarter0.getPosition());

        // Verify that the armorer is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(ARMORER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(17, 17);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Place road to connect the headquarters and the armory
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory0.getFlag());

        // Finish construction of the armory
        Utils.constructHouse(armory0);

        // Wait for a worker to start walking to the building
        var worker = Utils.waitForWorkersOutsideBuilding(Armorer.class, 1, player0).getFirst();

        // Wait for the worker to get to the building's flag
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, armory0.getFlag().getPosition());

        // Tear down the building
        armory0.tearDown();

        // Verify that the worker goes to the building and then returns to the headquarters instead of entering
        assertEquals(worker.getTarget(), armory0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, armory0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testArmoryWithoutResourcesHasZeroProductivity() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory = map.placeBuilding(new Armory(player0), point1);

        // Finish construction of the armory
        Utils.constructHouse(armory);

        // Populate the armory
        var armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory);
        assertEquals(armory.getWorker(), armorer0);

        // Verify that the productivity is 0% when the armory doesn't produce anything
        for (int i = 0; i < 500; i++) {
            assertTrue(armory.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
            assertEquals(armory.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testArmoryWithAbundantResourcesHasFullProductivity() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory = map.placeBuilding(new Armory(player0), point1);

        // Finish construction of the armory
        Utils.constructHouse(armory);

        // Populate the armory
        var armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory);
        assertEquals(armory.getWorker(), armorer0);

        // Connect the armory with the headquarters
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory.getFlag());

        // Make the armory create some weapons with full resources available
        for (int i = 0; i < 1000; i++) {
            map.stepTime();

            if (armory.needsMaterial(COAL)) {
                Utils.deliverCargo(armory, COAL);
            }

            if (armory.needsMaterial(IRON_BAR)) {
                Utils.deliverCargo(armory, IRON_BAR);
            }
        }

        // Verify that the productivity is 100% and stays there
        assertEquals(armory.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {
            map.stepTime();

            if (armory.needsMaterial(COAL)) {
                Utils.deliverCargo(armory, COAL);
            }

            if (armory.needsMaterial(IRON_BAR)) {
                Utils.deliverCargo(armory, IRON_BAR);
            }

            assertEquals(armory.getProductivity(), 100);
        }
    }

    @Test
    public void testArmoryLosesProductivityWhenResourcesRunOut() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory = map.placeBuilding(new Armory(player0), point1);

        // Finish construction of the armory
        Utils.constructHouse(armory);

        // Populate the armory
        var armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory);
        assertEquals(armory.getWorker(), armorer0);

        // Remove the resources the armory needs from the headquarters
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 0);
        Utils.adjustInventoryTo(headquarter0, COAL, 0);

        // Connect the armory with the headquarters
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory.getFlag());

        // Make the armory create some weapons with full resources available
        for (int i = 0; i < 1000; i++) {
            map.stepTime();

            if (armory.needsMaterial(COAL) && armory.getAmount(COAL) < 2) {
                Utils.deliverCargo(armory, COAL);
            }

            if (armory.needsMaterial(IRON_BAR) && armory.getAmount(IRON_BAR) < 2) {
                Utils.deliverCargo(armory, IRON_BAR);
            }
        }

        // Verify that the productivity goes down when resources run out
        assertEquals(armory.getProductivity(), 100);

        for (int i = 0; i < 2000; i++) {
            map.stepTime();
        }

        assertEquals(armory.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedArmoryHasNoProductivity() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory = map.placeBuilding(new Armory(player0), point1);

        // Finish construction of the armory
        Utils.constructHouse(armory);

        // Verify that the unoccupied armory is unproductive
        for (int i = 0; i < 1000; i++) {
            assertEquals(armory.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testArmoryCanProduce() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory = map.placeBuilding(new Armory(player0), point1);

        // Finish construction of the armory
        Utils.constructHouse(armory);

        // Populate the armory
        var armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory);

        // Verify that the armory can produce
        assertTrue(armory.canProduce());
    }

    @Test
    public void testArmoryReportsCorrectOutput() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(6, 12);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Construct the armory
        Utils.constructHouse(armory0);

        // Verify that the reported output is correct
        assertEquals(armory0.getProducedMaterial().length, 2);
        assertTrue((armory0.getProducedMaterial()[0] == SWORD && armory0.getProducedMaterial()[1] == SHIELD) ||
                   (armory0.getProducedMaterial()[1] == SWORD && armory0.getProducedMaterial()[0] == SHIELD));
    }

    @Test
    public void testArmoryReportsCorrectMaterialsNeededForConstruction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(6, 12);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Verify that the reported needed construction material is correct
        assertEquals(armory0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(armory0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(armory0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(armory0.getCanHoldAmount(PLANK), 2);
        assertEquals(armory0.getCanHoldAmount(STONE), 2);

        for (var material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(armory0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testArmoryReportsCorrectMaterialsNeededForProduction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(6, 12);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Construct the armory
        Utils.constructHouse(armory0);

        // Verify that the reported needed construction material is correct
        assertEquals(armory0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(armory0.getTypesOfMaterialNeeded().contains(COAL));
        assertTrue(armory0.getTypesOfMaterialNeeded().contains(IRON_BAR));
        assertEquals(armory0.getCanHoldAmount(COAL), 2);
        assertEquals(armory0.getCanHoldAmount(IRON_BAR), 2);

        for (var material : Material.values()) {
            if (material == COAL || material == IRON_BAR) {
                continue;
            }

            assertEquals(armory0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testUnoccupiedArmoryGetsMaximumMaterialButNotMore() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(6, 12);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Remove all armorers from the headquarters
        Utils.adjustInventoryTo(headquarter0, ARMORER, 0);

        // Add extra resources to the headquarters
        Utils.adjustInventoryTo(headquarter0, COAL, 10);
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 10);

        // Construct the armory
        Utils.constructHouse(armory0);

        // Connect the armory to the headquarters
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory0.getFlag());

        // Wait for the maximum amount of resources to get delivered
        for (int i = 0; i < 2000; i++) {
            if (armory0.getAmount(COAL) == 2 && armory0.getAmount(IRON_BAR) == 2) {
                break;
            }

            map.stepTime();
        }

        assertEquals(armory0.getAmount(COAL), 2);
        assertEquals(armory0.getAmount(IRON_BAR), 2);

        // Verify that the armory gets the maximum amount of resources but not more
        for (int i = 0; i < 2000; i++) {
            assertEquals(armory0.getAmount(COAL), 2);
            assertEquals(armory0.getAmount(IRON_BAR), 2);

            map.stepTime();
        }
    }

    @Test
    public void testArmoryWaitsWhenFlagIsFull() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(16, 6);
        var armory = map.placeBuilding(new Armory(player0), point1);

        // Connect the armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, armory.getFlag(), headquarter.getFlag());

        // Wait for the armory to get constructed and assigned a worker
        Utils.waitForBuildingToBeConstructed(armory);
        Utils.waitForNonMilitaryBuildingToGetPopulated(armory);

        // Give material to the armory
        Utils.deliverCargo(armory, IRON_BAR);
        Utils.deliverCargo(armory, COAL);

        // Fill the flag with flour cargos
        Utils.placeCargos(map, FLOUR, 8, armory.getFlag(), headquarter);

        // Remove the road
        map.removeRoad(road0);

        // Verify that the armory waits for the flag to get empty and produces nothing
        for (int i = 0; i < 300; i++) {
            assertEquals(armory.getFlag().getStackedCargo().size(), 8);
            assertNull(armory.getWorker().getCargo());

            map.stepTime();
        }

        // Reconnect the armory with the headquarters
        var road1 = map.placeAutoSelectedRoad(player0, armory.getFlag(), headquarter.getFlag());

        // Wait for the courier to pick up one of the cargos
        var courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(armory.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(armory.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(armory.getFlag().getStackedCargo().size(), 7);

        // Verify that the worker produces a cargo of flour and puts it on the flag
        Utils.fastForwardUntilWorkerCarriesCargo(map, armory.getWorker(), SHIELD, SWORD);
    }

    @Test
    public void testArmoryDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(16, 6);
        var armory = map.placeBuilding(new Armory(player0), point1);

        // Connect the armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, armory.getFlag(), headquarter.getFlag());

        // Wait for the armory to get constructed and assigned a worker
        Utils.waitForBuildingToBeConstructed(armory);
        Utils.waitForNonMilitaryBuildingToGetPopulated(armory);

        // Give material to the armory
        Utils.deliverCargo(armory, IRON_BAR);
        Utils.deliverCargo(armory, IRON_BAR);
        Utils.deliverCargo(armory, COAL);
        Utils.deliverCargo(armory, COAL);

        // Fill the flag with cargos
        Utils.placeCargos(map, FLOUR, 8, armory.getFlag(), headquarter);

        // Remove the road
        map.removeRoad(road0);

        // The armory waits for the flag to get empty and produces nothing
        for (int i = 0; i < 300; i++) {
            assertEquals(armory.getFlag().getStackedCargo().size(), 8);
            assertNull(armory.getWorker().getCargo());

            map.stepTime();
        }

        // Reconnect the armory with the headquarters
        var road1 = map.placeAutoSelectedRoad(player0, armory.getFlag(), headquarter.getFlag());

        // Wait for the courier to pick up one of the cargos
        var courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(armory.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(armory.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(armory.getFlag().getStackedCargo().size(), 7);

        // Remove the road
        map.removeRoad(road1);

        // The worker produces a cargo of shield or sword and puts it on the flag
        Utils.fastForwardUntilWorkerCarriesCargo(map, armory.getWorker(), FLOUR, SWORD);

        // Wait for the worker to put the cargo on the flag
        assertEquals(armory.getWorker().getTarget(), armory.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armory.getWorker(), armory.getFlag().getPosition());

        assertEquals(armory.getFlag().getStackedCargo().size(), 8);

        // Verify that the armory doesn't produce anything because the flag is full
        for (int i = 0; i < 400; i++) {
            assertEquals(armory.getFlag().getStackedCargo().size(), 8);
            assertNull(armory.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testWhenWeaponsAreBlockedArmoryFillsUpFlagAndThenStops() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Place road to connect the armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Wait for the armory to get constructed and occupied
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(armory0);

        var armorer0 = Utils.waitForNonMilitaryBuildingToGetPopulated(armory0);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory0);
        assertEquals(armory0.getWorker(), armorer0);

        // Add a lot of material to the headquarters for the armory to consume
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 40);
        Utils.adjustInventoryTo(headquarter0, COAL, 40);

        // Block storage of weapons
        headquarter0.blockDeliveryOfMaterial(SHIELD);
        headquarter0.blockDeliveryOfMaterial(SWORD);

        // Verify that the armory puts eight weapons on the flag and then stops
        Utils.waitForFlagToGetStackedCargo(map, armory0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(armory0.getFlag().getStackedCargo().size(), 8);
            assertTrue(armorer0.isInsideBuilding());

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), SHIELD);
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), SWORD);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndArmoryIsTornDown() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(5, 5);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Place armory
        var point2 = new Point(18, 6);
        var armory0 = map.placeBuilding(new Armory(player0), point2);

        // Place road to connect the storehouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        // Place road to connect the headquarters with the armory
        var road1 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Add a lot of planks and stones to the headquarters
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the armory and the storehouse to get constructed
        Utils.waitForBuildingsToBeConstructed(storehouse, armory0);

        // Add a lot of material to the headquarters for the armory to consume
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 40);
        Utils.adjustInventoryTo(headquarter0, COAL, 40);

        // Wait for the armory and the storage to get occupied
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, armory0);

        var armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory0);
        assertEquals(armory0.getWorker(), armorer0);

        // Verify that the worker goes to the storage when the armory is torn down
        headquarter0.blockDeliveryOfMaterial(ARMORER);

        armory0.tearDown();

        map.stepTime();

        assertFalse(armorer0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        assertEquals(armorer0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, armorer0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(armorer0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndArmoryIsTornDown() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(5, 5);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Place armory
        var point2 = new Point(18, 6);
        var armory0 = map.placeBuilding(new Armory(player0), point2);

        // Place road to connect the storehouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        // Place road to connect the headquarters with the armory
        var road1 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Add a lot of planks and stones to the headquarters
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the armory and the storehouse to get constructed
        Utils.waitForBuildingsToBeConstructed(storehouse, armory0);

        // Add a lot of material to the headquarters for the armory to consume
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 40);
        Utils.adjustInventoryTo(headquarter0, COAL, 40);

        // Wait for the armory and the storage to get occupied
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, armory0);

        var armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory0);
        assertEquals(armory0.getWorker(), armorer0);

        // Verify that the worker goes to the storage off-road when the armory is torn down
        headquarter0.blockDeliveryOfMaterial(ARMORER);

        armory0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(armorer0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, armory0.getFlag().getPosition());

        assertEquals(armorer0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(armorer0));
    }

    @Test
    public void testWorkerGoesOutAndBackInWhenSentOutWithoutBlocking() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that worker goes out and in continuously when sent out without being blocked
        Utils.adjustInventoryTo(headquarter0, ARMORER, 1);

        assertEquals(headquarter0.getAmount(ARMORER), 1);

        headquarter0.pushOutAll(ARMORER);

        for (int i = 0; i < 10; i++) {
            var worker = Utils.waitForWorkerOutsideBuilding(Armorer.class, player0);

            assertEquals(headquarter0.getAmount(ARMORER), 0);
            assertEquals(worker.getPosition(), headquarter0.getPosition());
            assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

            assertEquals(worker.getPosition(), headquarter0.getFlag().getPosition());
            assertEquals(worker.getTarget(), headquarter0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

            assertFalse(map.getWorkers().contains(worker));
        }
    }

    @Test
    public void testPushedOutWorkerWithNowhereToGoWalksAwayAndDies() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that worker goes out and in continuously when sent out without being blocked
        Utils.adjustInventoryTo(headquarter0, ARMORER, 1);

        headquarter0.blockDeliveryOfMaterial(ARMORER);
        headquarter0.pushOutAll(ARMORER);

        var worker = Utils.waitForWorkerOutsideBuilding(Armorer.class, player0);

        assertEquals(worker.getPosition(), headquarter0.getPosition());
        assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

        assertEquals(worker.getPosition(), headquarter0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), headquarter0.getPosition());
        assertFalse(worker.isDead());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        assertTrue(worker.isDead());

        for (int i = 0; i < 100; i++) {
            assertTrue(worker.isDead());
            assertTrue(map.getWorkers().contains(worker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    @Test
    public void testWorkerWithNowhereToGoWalksAwayAndDiesWhenHouseIsTornDown() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Place road to connect the armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the armory to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(armory0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(armory0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarters
       */
        headquarter0.blockDeliveryOfMaterial(ARMORER);

        var worker = armory0.getWorker();

        armory0.tearDown();

        assertEquals(worker.getPosition(), armory0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, armory0.getFlag().getPosition());

        assertEquals(worker.getPosition(), armory0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), armory0.getPosition());
        assertNotEquals(worker.getTarget(), headquarter0.getPosition());
        assertFalse(worker.isDead());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        assertTrue(worker.isDead());

        for (int i = 0; i < 100; i++) {
            assertTrue(worker.isDead());
            assertTrue(map.getWorkers().contains(worker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    @Test
    public void testWorkerGoesAwayAndDiesWhenItReachesTornDownHouseAndStorageIsBlocked() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Place road to connect the armory with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the armory to get constructed
        Utils.waitForBuildingToBeConstructed(armory0);

        // Wait for a armorer to start walking to the armory
        var armorer = Utils.waitForWorkerOutsideBuilding(Armorer.class, player0);

        // Wait for the armorer to go past the headquarters's flag
        Utils.fastForwardUntilWorkerReachesPoint(map, armorer, headquarter0.getFlag().getPosition());

        map.stepTime();

        // Verify that the armorer goes away and dies when the house has been torn down and storage is not possible
        assertEquals(armorer.getTarget(), armory0.getPosition());

        headquarter0.blockDeliveryOfMaterial(ARMORER);

        armory0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer, armory0.getFlag().getPosition());

        assertEquals(armorer.getPosition(), armory0.getFlag().getPosition());
        assertNotEquals(armorer.getTarget(), headquarter0.getPosition());
        assertFalse(armorer.isInsideBuilding());
        assertNull(armory0.getWorker());
        assertNotNull(armorer.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, armorer, armorer.getTarget());

        var point = armorer.getPosition();

        for (int i = 0; i < 100; i++) {
            assertTrue(armorer.isDead());
            assertEquals(armorer.getPosition(), point);
            assertTrue(map.getWorkers().contains(armorer));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(armorer));
    }
}
